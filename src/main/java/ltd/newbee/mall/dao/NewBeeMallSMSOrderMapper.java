package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.entity.NewBeeMallSMSOrder;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface NewBeeMallSMSOrderMapper {

    int selectByUserId(Long userId);

    NewBeeMallSMSOrder selectByOrderId(String orderId);

    int insertOrder(NewBeeMallSMSOrder newBeeMallSMSOrder);

    int getTotalNewBeeMallSMSOrders(PageQueryUtil pageUtil);

    List<NewBeeMallSMSOrder> findNewBeeMallSMSOrderList(PageQueryUtil pageUtil);

    int updateCancelOrderStatus(String orderId, int status);

//    int deleteByPrimaryKey(Long orderId);
//
//    int insert(NewBeeMallOrder record);
//
//    int insertSelective(NewBeeMallOrder record);
//
//    NewBeeMallOrder selectByPrimaryKey(Long orderId);
//
//    NewBeeMallOrder selectByOrderNo(String orderNo);
//
//    int updateByPrimaryKeySelective(NewBeeMallOrder record);
//
//    int updateByPrimaryKey(NewBeeMallOrder record);
//
//    List<NewBeeMallOrder> findNewBeeMallOrderList(PageQueryUtil pageUtil);
//
//    int getTotalNewBeeMallOrders(PageQueryUtil pageUtil);
//
//    List<NewBeeMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);
//
//    int checkOut(@Param("orderIds") List<Long> orderIds);
//
//    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);
//
//    int checkDone(@Param("orderIds") List<Long> asList);
}