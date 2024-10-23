package ltd.newbee.mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserWalletFindVO implements Serializable {

    //输出 钱包余额， 订单id， 充值时间， 充值金额， 确认结果
    @ApiModelProperty("钱包余额")
    private BigDecimal walletValue;

    @ApiModelProperty("充值订单id")
    private String rechargeId;

    @ApiModelProperty("充值时间")
    private String rechargeTime;

    @ApiModelProperty("充值金额")
    private BigDecimal rechargeValue;

    @ApiModelProperty("充值结果确认")
    private boolean rechargeConfirm;

    @Override
    public String toString() {
        return "UserWalletFindVO{" +
                "walletValue='" + walletValue + '\'' +
                ", rechargeId='" + rechargeId + '\'' +
                ", rechargeTime='" + rechargeTime + '\'' +
                ", rechargeValue='" + rechargeValue + '\'' +
                ", rechargeConfirm='" + rechargeConfirm + '\'' +
                '}';
    }

    public BigDecimal getWalletValue() {
        return walletValue;
    }

    public void setWalletValue(BigDecimal walletValue) {
        this.walletValue = walletValue;
    }

    public String getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(String rechargeId) {
        this.rechargeId = rechargeId;
    }

    public String getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(String rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public BigDecimal getRechargeValue() {
        return rechargeValue;
    }

    public void setRechargeValue(BigDecimal rechargeValue) {
        this.rechargeValue = rechargeValue;
    }

    public boolean getRechargeConfirm() {
        return rechargeConfirm;
    }

    public void setRechargeConfirm(boolean rechargeConfirm) {
        this.rechargeConfirm = rechargeConfirm;
    }

    public void setUserWalletFindVO(BigDecimal walletValue, String rechargeId, String rechargeTime, BigDecimal rechargeValue, boolean rechargeConfirm) {
        this.walletValue = walletValue;
        this.rechargeId = rechargeId;
        this.rechargeTime = rechargeTime;
        this.rechargeValue = rechargeValue;
        this.rechargeConfirm = rechargeConfirm;
    }
}
