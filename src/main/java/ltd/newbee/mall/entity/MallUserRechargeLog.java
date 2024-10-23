package ltd.newbee.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MallUserRechargeLog {

    private Long userId;

    private String userWalletIcbcRechargeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date userWalletRechargeTime;

    private BigDecimal userWalletRechargeValue;

    private BigDecimal userWalletFinallyValue;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserWalletIcbcRechargeId() {
        return userWalletIcbcRechargeId;
    }

    public void setUserWalletIcbcRechargeId(String userWalletIcbcRechargeId) {
        this.userWalletIcbcRechargeId = userWalletIcbcRechargeId;
    }

    public Date getUserWalletRechargeTime() {
        return userWalletRechargeTime;
    }

    public void setUserWalletRechargeTime(Date userWalletRechargeTime) {
        this.userWalletRechargeTime = userWalletRechargeTime;
    }

    public BigDecimal getUserWalletRechargeValue() {
        return userWalletRechargeValue;
    }

    public void setUserWalletRechargeValue(BigDecimal userWalletRechargeValue) {
        this.userWalletRechargeValue = userWalletRechargeValue;
    }

    public BigDecimal getUserWalletFinallyValue() {
        return userWalletFinallyValue;
    }

    public void setUserWalletFinallyValue(BigDecimal userWalletFinallyValue) {
        this.userWalletFinallyValue = userWalletFinallyValue;
    }

}