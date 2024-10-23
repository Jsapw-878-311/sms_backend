package ltd.newbee.mall.sdk.pay;

public class YiPayUrl {

    static String baseURL = "https://yi-pay.com/";

    static String orderConfirm = baseURL + "api.php?act=order&pid={商户ID}&key={商户密钥}&out_trade_no={商户订单号}";

    public static String getBaseURL() {
        return baseURL;
    }

    public static String getOrderConfirm() {
        return orderConfirm;
    }
}
