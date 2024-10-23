package ltd.newbee.mall.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GlobalConfig {

    private int configId;

    private int apiList;

    private String exchangeConfig;

    private String convertConfig;

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getApiList() {
        return apiList;
    }

    public void setApiList(int apiList) {
        this.apiList = apiList;
    }

    public String getExchangeConfig() {
        return exchangeConfig;
    }

    public void setExchangeConfig(String exchangeConfig) {
        this.exchangeConfig = exchangeConfig;
    }

    public String getConvertConfig() {
        return convertConfig;
    }

    public void setConvertConfig(String convertConfig) {
        this.convertConfig = convertConfig;
    }
}
