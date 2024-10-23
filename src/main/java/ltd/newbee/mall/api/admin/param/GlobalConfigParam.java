package ltd.newbee.mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GlobalConfigParam {

    @ApiModelProperty("sms平台配置")
    private int apiList;

    @ApiModelProperty("配置汇率")
    private String exchange ;

    @ApiModelProperty("配置转化率")
    private String convert ;

    public int getApiList() {
        return apiList;
    }

    public void setApiList(int apiList) {
        this.apiList = apiList;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getConvert() {
        return convert;
    }

    public void setConvert(String convert) {
        this.convert = convert;
    }
}
