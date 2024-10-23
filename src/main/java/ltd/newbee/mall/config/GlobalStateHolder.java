package ltd.newbee.mall.config;

import org.springframework.stereotype.Component;

@Component
public class GlobalStateHolder {

    private int apiList = 0;

    private String exchange = "7.3";

    private String convert = "2";

    private String smsPoolApiKey = "Bearer 5JHe5PPvhfkwIvfFXZxbe3v4Fm7DxY5N";

    public int getApiList() {
        return apiList;
    }

    public void setApiList(int apiList) {
        this.apiList = apiList;
    }

    public String getSmsPoolApiKey() {
        return smsPoolApiKey;
    }

    public void setSmsPoolApiKey(String smsPoolApiKey) {
        this.smsPoolApiKey = smsPoolApiKey;
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
