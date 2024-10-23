package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderOperationParam {

    @ApiModelProperty("订单号")
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
