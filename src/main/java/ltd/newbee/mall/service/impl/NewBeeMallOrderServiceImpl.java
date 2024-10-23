package ltd.newbee.mall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ltd.newbee.mall.api.admin.NewBeeAdminOrderAPI;
import ltd.newbee.mall.api.mall.param.CreatSMSOrderParam;
import ltd.newbee.mall.api.mall.vo.NewBeeMallOrderDetailVO;
import ltd.newbee.mall.api.mall.vo.NewBeeMallOrderItemVO;
import ltd.newbee.mall.api.mall.vo.NewBeeMallOrderListVO;
import ltd.newbee.mall.api.mall.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.common.*;
import ltd.newbee.mall.config.GlobalStateHolder;
import ltd.newbee.mall.dao.*;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.sdk.smspool.SMSPoolRequest;
import ltd.newbee.mall.sdk.smspool.param.*;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewBeeMallOrderServiceImpl implements NewBeeMallOrderService {

    @Resource
    private NewBeeMallOrderMapper newBeeMallOrderMapper;

    @Resource
    private NewBeeMallOrderItemMapper newBeeMallOrderItemMapper;

    @Resource
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Resource
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @Resource
    private NewBeeMallOrderAddressMapper newBeeMallOrderAddressMapper;

    @Resource
    private MallUserMapper mallUserMapper;

    @Resource
    private NewBeeMallSMSOrderMapper newBeeMallSMSOrderMapper;

    SMSPoolRequest smsPoolRequest = new SMSPoolRequest();

    private static final Logger logger = LoggerFactory.getLogger(NewBeeAdminOrderAPI.class);

    @Override
    public List<ServiceListParam> getServiceList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //数组json转化成集合
        // 将 JSON 字符串转换为 CountryListParam 对象的列表
        List<ServiceListParam> list = mapper.readValue(smsPoolRequest.getServiceList(), new TypeReference<List<ServiceListParam>>(){});
//        System.out.println("==数组json转集合=="+list);
//        System.out.println("==改价=="+list);
        return list;

//        return smsPoolRequest.getServiceList();
    }

    public List<CountryListParam> getCountryList(int serviceNo) throws JsonProcessingException {
        //价格修改：换汇率，加抽成
        ObjectMapper mapper = new ObjectMapper();
        //数组json转化成集合
        // 将 JSON 字符串转换为 CountryListParam 对象的列表
        List<CountryListParam> list = mapper.readValue(smsPoolRequest.getCountryList(serviceNo), new TypeReference<List<CountryListParam>>(){});
//        System.out.println("==数组json转集合=="+list);
        for (int i = 0; i < list.size(); ++i) {
            String oldPrice = list.get(i).getPrice();
            String newPrice = priceConvert(oldPrice);
            list.get(i).setPrice(newPrice);
        }
//        System.out.println("==改价=="+list);
        return list;
    }

    public GetPriceParam getSMSPrice(CreatSMSOrderParam creatSMSOrderParam) throws JsonProcessingException {
        //价格修改：换汇率，加抽成
        ObjectMapper mapper = new ObjectMapper();
        //数组json转化成集合
        // 将 JSON 字符串转换为 CountryListParam 对象的列表
        GetPriceParam getPriceParam = mapper.readValue(
                smsPoolRequest.getPrice(creatSMSOrderParam.getServiceNo(), creatSMSOrderParam.getCountryNo()),
                GetPriceParam.class);
//        System.out.println("==数组json转集合=="+list);
        if(!getPriceParam.getPrice().equals("") && getPriceParam.getPrice() != null){
            String oldPrice = getPriceParam.getPrice();
            String newPrice = priceConvert(oldPrice);
            getPriceParam.setPrice(newPrice);
        }
        getPriceParam.setHigh_price("0.00");
//        System.out.println("==改价=="+list);
        return getPriceParam;
    }

    public NewBeeMallSMSOrder getPhoneNumber(int serviceNo, int countryNo, MallUser mallUser) throws JsonProcessingException {
        //检查余额；价格修改：换汇率，加抽成； 写入订单；扣费；
//        状态：0。余额不足。1。未获取到验证码。2。取消订单。3。获取到了验证码，订单结束
//        平台：0。smspool.net。
        ObjectMapper mapper = new ObjectMapper();
        CreatOrderParam creatOrderParam = mapper.readValue(
                smsPoolRequest.getNumber(serviceNo, countryNo),
                CreatOrderParam.class);
        String finPrice = priceConvert(creatOrderParam.getCost());
        creatOrderParam.setCost(finPrice);
        creatOrderParam.setCost_in_cents(priceConvert(finPrice));
        //检查用户账户余额
        BigDecimal finPriceDec = new BigDecimal(creatOrderParam.getCost());
        NewBeeMallSMSOrder newBeeMallSMSOrder = new NewBeeMallSMSOrder();
        if(mallUser.getWalletValue().compareTo(finPriceDec) < 0){
            logger.info("==jsapw==价格" + finPriceDec + "余额" + mallUser.getWalletValue());
            newBeeMallSMSOrder.setStatus(0);
            return newBeeMallSMSOrder;
        }

        newBeeMallSMSOrder.setStatus(1);
        newBeeMallSMSOrder.setUserId(mallUser.getUserId());
        newBeeMallSMSOrder.setCreatTime(new Date());
        newBeeMallSMSOrder.setPlatform("0");
        newBeeMallSMSOrder.setOrderId(creatOrderParam.getOrderid());
        newBeeMallSMSOrder.setServiceName(creatOrderParam.getService());
        newBeeMallSMSOrder.setCountryName(creatOrderParam.getCountry());
        newBeeMallSMSOrder.setSmsPrice(new BigDecimal(creatOrderParam.getCost()));
        newBeeMallSMSOrder.setPhone(creatOrderParam.getPhonenumber());

        newBeeMallSMSOrderMapper.insertOrder(newBeeMallSMSOrder);
        //添加数据库没有的需要返回的参数
        newBeeMallSMSOrder.setCc(creatOrderParam.getCc());
        newBeeMallSMSOrder.setExpiration(creatOrderParam.getExpiration());

        mallUserMapper.updateWalletByUserId(mallUser.getWalletValue().subtract(finPriceDec), mallUser.getUserId());

        return newBeeMallSMSOrder;
    }

    public CancelSMSParam getcancelOrder(String orderId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String param = smsPoolRequest.getCancelOrder(orderId);
        return mapper.readValue(param, CancelSMSParam.class);
    }

    public void cancelOrderResultSql(String orderId, int status){
        newBeeMallSMSOrderMapper.updateCancelOrderStatus(orderId, status);
    }

    public void cancelOrderPrice(String orderId, MallUser mallUser){
        newBeeMallSMSOrderMapper.selectByOrderId(orderId).getSmsPrice();
        mallUserMapper.updateWalletByUserId(mallUser.getWalletValue()
                .add(newBeeMallSMSOrderMapper.selectByOrderId(orderId).getSmsPrice()),
                mallUser.getUserId());
    }

    public CheckSMSParam getCode(String orderId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String param = smsPoolRequest.getCode(orderId);
        return mapper.readValue(param, CheckSMSParam.class);
    }

    public String priceConvert(String platformPrice){
        return new BigDecimal(platformPrice).multiply(
                new BigDecimal(new GlobalStateHolder().getExchange())).multiply(
                        new BigDecimal(new GlobalStateHolder().getConvert())).toString();
    }

    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByPrimaryKey(orderId);
        if (newBeeMallOrder == null) {
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);
            NewBeeMallOrderDetailVO newBeeMallOrderDetailVO = new NewBeeMallOrderDetailVO();
            BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
            newBeeMallOrderDetailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getName());
            newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getName());
            newBeeMallOrderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
            return newBeeMallOrderDetailVO;
        } else {
            NewBeeMallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder == null) {
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!userId.equals(newBeeMallOrder.getUserId())) {
            NewBeeMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            NewBeeMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);
        NewBeeMallOrderDetailVO newBeeMallOrderDetailVO = new NewBeeMallOrderDetailVO();
        BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
        newBeeMallOrderDetailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getName());
        newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getName());
        newBeeMallOrderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
        return newBeeMallOrderDetailVO;
    }


    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = newBeeMallSMSOrderMapper.getTotalNewBeeMallSMSOrders(pageUtil);
        List<NewBeeMallSMSOrder> newBeeMallSMSOrders = newBeeMallSMSOrderMapper.findNewBeeMallSMSOrderList(pageUtil);

        PageResult pageResult = new PageResult(newBeeMallSMSOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(newBeeMallOrder.getUserId())) {
                NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (newBeeMallOrder.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (newBeeMallOrderMapper.closeOrder(Collections.singletonList(newBeeMallOrder.getOrderId()), NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(newBeeMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (newBeeMallOrder.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newBeeMallOrder.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            newBeeMallOrder.setUpdateTime(new Date());
            if (newBeeMallOrderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (newBeeMallOrder.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newBeeMallOrder.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            newBeeMallOrder.setPayType((byte) payType);
            newBeeMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newBeeMallOrder.setPayTime(new Date());
            newBeeMallOrder.setUpdateTime(new Date());
            if (newBeeMallOrderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUser loginMallUser, MallUserAddress address, List<NewBeeMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<NewBeeMallGoods> newBeeMallGoods = newBeeMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<NewBeeMallGoods> goodsListNotSelling = newBeeMallGoods.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            NewBeeMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, NewBeeMallGoods> newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(newBeeMallGoods)) {
            if (newBeeMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = newBeeMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                NewBeeMallOrder newBeeMallOrder = new NewBeeMallOrder();
                newBeeMallOrder.setOrderNo(orderNo);
                newBeeMallOrder.setUserId(loginMallUser.getUserId());
                //总价
                for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                newBeeMallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                newBeeMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (newBeeMallOrderMapper.insertSelective(newBeeMallOrder) > 0) {
                    //生成订单收货地址快照，并保存至数据库
                    NewBeeMallOrderAddress newBeeMallOrderAddress = new NewBeeMallOrderAddress();
                    BeanUtil.copyProperties(address, newBeeMallOrderAddress);
                    newBeeMallOrderAddress.setOrderId(newBeeMallOrder.getOrderId());
                    //生成所有的订单项快照，并保存至数据库
                    List<NewBeeMallOrderItem> newBeeMallOrderItems = new ArrayList<>();
                    for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                        NewBeeMallOrderItem newBeeMallOrderItem = new NewBeeMallOrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(newBeeMallShoppingCartItemVO, newBeeMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
                        newBeeMallOrderItems.add(newBeeMallOrderItem);
                    }
                    //保存至数据库
                    if (newBeeMallOrderItemMapper.insertBatch(newBeeMallOrderItems) > 0 && newBeeMallOrderAddressMapper.insertSelective(newBeeMallOrderAddress) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResult getNewBeeMallOrdersPage(PageQueryUtil pageUtil) {
        List<NewBeeMallSMSOrder> newBeeMallSMSOrders = newBeeMallSMSOrderMapper.findNewBeeMallSMSOrderList(pageUtil);
        int total = newBeeMallSMSOrderMapper.getTotalNewBeeMallSMSOrders(pageUtil);
//        List<NewBeeMallOrder> newBeeMallOrders = newBeeMallOrderMapper.findNewBeeMallOrderList(pageUtil);
//        int total = newBeeMallOrderMapper.getTotalNewBeeMallOrders(pageUtil);
        PageResult pageResult = new PageResult(newBeeMallSMSOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        logger.info("adminUser:{}", pageResult.toString());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(NewBeeMallOrder newBeeMallOrder) {
        NewBeeMallOrder temp = newBeeMallOrderMapper.selectByPrimaryKey(newBeeMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(newBeeMallOrder.getTotalPrice());
            temp.setUpdateTime(new Date());
            if (newBeeMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<NewBeeMallOrder> orders = newBeeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (NewBeeMallOrder newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (newBeeMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<NewBeeMallOrder> orders = newBeeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (NewBeeMallOrder newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1 && newBeeMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (newBeeMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<NewBeeMallOrder> orders = newBeeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (NewBeeMallOrder newBeeMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (newBeeMallOrder.getOrderStatus() == 4 || newBeeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (newBeeMallOrderMapper.closeOrder(Arrays.asList(ids), NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<NewBeeMallOrderItemVO> getOrderItems(Long orderId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByPrimaryKey(orderId);
        if (newBeeMallOrder != null) {
            List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);
                return newBeeMallOrderItemVOS;
            }
        }
        return null;
    }
}
