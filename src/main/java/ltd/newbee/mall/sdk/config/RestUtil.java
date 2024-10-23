package ltd.newbee.mall.sdk.config;

import ltd.newbee.mall.api.admin.NewBeeAdminOrderAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@Component
public class RestUtil {

    @Resource
    private RestTemplate restTemplate = new RestTemplate();

    //一些自定义的请求头参数
    public static final String supplierID = "";

    public static final String interfacekey = "";

    private static final Logger logger = LoggerFactory.getLogger(NewBeeAdminOrderAPI.class);

    /**
     * DLT专用执行方法
     *
     * @param param  请求参数：可以添加一些常量请求值
     * @param url    访问的url
     * @param method 请求的方法
     * @return
     */
    public String execute(Map<String, Object> param, String url, HttpMethod method) {
        HttpHeaders headers = this.getDefaultHeader();
        Map<String, Object> requestor = this.getDefaultParam();
        param.put("requestor", requestor);
        param.put("supplierID", supplierID);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(param, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    /**
     * 获取默认的头请求信息
     *
     * @return
     */
    public HttpHeaders getDefaultHeader() {
        String timestamp = "" + System.currentTimeMillis();
        String signature = EncoderByMd5(supplierID + timestamp + interfacekey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("signature", signature);
        headers.add("timestamp", timestamp);
        return headers;
    }


    /**
     * 获取默认的参数
     *
     * @return
     */
    public Map<String, Object> getDefaultParam() {
        Map<String, Object> defParam = new HashMap<>();
        defParam.put("invoker", "xx");
        defParam.put("operatorName", "xx");
        return defParam;
    }


    /**
     * 通过MD5加密
     *
     * @param str
     * @return
     */
    public static String EncoderByMd5(String str) {
        if (str == null) {
            return null;
        }
        try {
            // 确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 加密后的字符串
            return Base64Utils.encodeToString(md5.digest(str.getBytes("utf-8"))).toUpperCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     *
     * @param url
     * @param headers
     * @param httpMethod
     * @return
     */
    public String request(String url, HttpHeaders headers, HttpMethod httpMethod) {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, httpMethod, entity, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return response.getBody();
    }

    /**
     * Get请求获取实体类
     *
     * @param url          请求的url
     * @param responseType 返回的类型
     * @param parms        不限定个数的参数
     * @param <T>          泛型
     * @return
     */
    public <T> T getForEntity(String url, Class<T> responseType, Object... parms) {
        return (T) restTemplate.getForEntity(url, responseType, parms);
    }

    /**
     * Get请求无请求头
     *
     * @param url
     * @param parm
     * @return
     */
    public String get(String url, Map<String, Object> parm) {
        return restTemplate.getForEntity(url, String.class, parm).getBody();
    }

    /**
     * 添加header的get请求
     * @param url
     * @param headers
     * @return
     */
    public String getAddHeader(String url, HttpHeaders headers) {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return response.getBody();
    }

    /**
     *
     * @param url
     * @param headers
     * @return
     */
    public String postAddHeader(String url, HttpHeaders headers, MultiValueMap<String, String> body) {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("jsapw---___", String.valueOf(ex));
            return "";
        }
        return response.getBody();
    }
}
