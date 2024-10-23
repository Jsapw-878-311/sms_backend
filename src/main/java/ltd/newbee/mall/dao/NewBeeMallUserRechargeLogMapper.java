package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.MallUserRechargeLog;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.util.PageQueryUtil;

import java.math.BigDecimal;
import java.util.List;

public interface NewBeeMallUserRechargeLogMapper {

    BigDecimal findFinallyValue(Long userId);

    int insert(MallUserRechargeLog mallUserRechargeLog);

    List<MallUserRechargeLog> findRechargeList(PageQueryUtil pageUtil);

    int getTotalNewBeeMallRecharge(PageQueryUtil pageUtil);
}
