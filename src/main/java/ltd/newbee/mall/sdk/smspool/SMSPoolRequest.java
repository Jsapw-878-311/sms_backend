package ltd.newbee.mall.sdk.smspool;

import ltd.newbee.mall.api.mall.NewBeeMallPersonalAPI;
import ltd.newbee.mall.config.GlobalStateHolder;
import ltd.newbee.mall.sdk.config.RestUtil;
import ltd.newbee.mall.sdk.pay.YiPayUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;

public class SMSPoolRequest {

    private RestUtil restUtil = new RestUtil();

    private static final Logger logger = LoggerFactory.getLogger(NewBeeMallPersonalAPI.class);

    private HttpHeaders setToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SMSPoolUrl.getHeaderKey(), new GlobalStateHolder().getSmsPoolApiKey());
        return headers;
    }

    public String getServiceList(){
        return restUtil.getAddHeader(SMSPoolUrl.getServiceList(), setToken());
    }

    public String getCountryList(Integer serviceNo){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("service", serviceNo.toString());
        return restUtil.postAddHeader(SMSPoolUrl.getCountryList(), setToken(), map);
    }

    public String getPrice(Integer serviceNo, Integer countryNo){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("country", countryNo.toString());
        map.add("service", serviceNo.toString());
        return restUtil.postAddHeader(SMSPoolUrl.getGetPriceUrl(), setToken(), map);
    }

    public String getNumber(Integer serviceNo, Integer countryNo){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("service", serviceNo.toString());
        map.add("country", countryNo.toString());
        map.add("max_price", "10");
        map.add("pricing_option", "1");
        map.add("quantity", "1");
        map.add("areacode", "0");
        map.add("create_token", "0");
        String result = restUtil.postAddHeader(SMSPoolUrl.getGetNumberUrl(), setToken(), map);
        System.out.println(result);
        return result;
//        return "";
    }

    public String getCancelOrder(String orderId){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("orderid", orderId);
        return restUtil.postAddHeader(SMSPoolUrl.getGetCancelOrder(), setToken(), map);
//        return "";
    }

    public String getCode(String orderId){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("orderid", orderId);
        return restUtil.postAddHeader(SMSPoolUrl.getGetCodeUrl(), setToken(), map);
//        return "";
    }
}
