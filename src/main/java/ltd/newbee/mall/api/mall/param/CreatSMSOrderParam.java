package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreatSMSOrderParam {

    @ApiModelProperty("服务号")
    private int serviceNo;

    @ApiModelProperty("国家号")
    private int countryNo;

    @Override
    public String toString() {
        return "CreatSMSOrderParam{" +
                "serviceNo=" + serviceNo +
                ", countryNo=" + countryNo +
                '}';
    }

    public int getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(int serviceNo) {
        this.serviceNo = serviceNo;
    }

    public int getCountryNo() {
        return countryNo;
    }

    public void setCountryNo(int countryNo) {
        this.countryNo = countryNo;
    }
}
