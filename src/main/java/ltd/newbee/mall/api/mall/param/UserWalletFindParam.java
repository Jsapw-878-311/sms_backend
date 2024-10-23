package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加收货地址param
 */
@Data
public class UserWalletFindParam {

    @ApiModelProperty("是否充值后查询")
    private boolean userIsOnlyFind;

    @ApiModelProperty("充值号id")
    private String userRechargeId;

    @ApiModelProperty("充值金额")
    private BigDecimal userRechargeValue;


    @Override
    public String toString() {
        return "SaveMallUserAddressParam{" +
                "userIsOnlyFind='" + userIsOnlyFind + '\'' +
                ", userRechargeId='" + userRechargeId + '\'' +
                ", userRechargeValue=" + userRechargeValue +
                '}';
    }

    public boolean isUserIsOnlyFind() {
        return userIsOnlyFind;
    }

    public void setUserIsOnlyFind(boolean userIsOnlyFind) {
        this.userIsOnlyFind = userIsOnlyFind;
    }

    public String getUserRechargeId() {
        return userRechargeId;
    }

    public void setUserRechargeId(String userRechargeId) {
        this.userRechargeId = userRechargeId;
    }

    public BigDecimal getUserRechargeValue() {
        return userRechargeValue;
    }

    public void setUserRechargeValue(BigDecimal userRechargeValue) {
        this.userRechargeValue = userRechargeValue;
    }
}
