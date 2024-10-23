package ltd.newbee.mall.sdk.smspool.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetPriceParam {

    private String price;

    private String high_price;

    private Integer success_rate;

    private Integer pool;

    public String getPrice() {
        return price;
    }

    public String getHigh_price() {
        return high_price;
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public void setHigh_price(String high_price) {
        this.high_price = high_price;
    }

    public Integer getSuccess_rate() {
        return success_rate;
    }

    public void setSuccess_rate(Integer success_rate) {
        this.success_rate = success_rate;
    }

    public Integer getPool() {
        return pool;
    }

    public void setPool(Integer pool) {
        this.pool = pool;
    }
}
