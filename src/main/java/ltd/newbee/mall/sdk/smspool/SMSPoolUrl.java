package ltd.newbee.mall.sdk.smspool;

public class SMSPoolUrl {

    static String baseURL = "https://api.smspool.net/";
    // header：Authorization    Bearer 5JHe5PPvhfkwIvfFXZxbe3v4Fm7DxY5N

    static String headerKey = "Authorization";

    static String serviceList = baseURL + "service/retrieve_all";//get

    static String countryList = baseURL + "request/suggested_countries";

    static String getPriceUrl = baseURL + "request/price";

    static String getNumberUrl = baseURL + "purchase/sms";

    static String getCancelOrder = baseURL + "sms/cancel";

    static String getCodeUrl = baseURL + "sms/check";

    public static String getBaseURL() {
        return baseURL;
    }

    public static String getHeaderKey() {
        return headerKey;
    }

    public static String getServiceList() {
        return serviceList;
    }

    public static String getCountryList() {
        return countryList;
    }

    public static String getGetPriceUrl() {
        return getPriceUrl;
    }

    public static String getGetNumberUrl() {
        return getNumberUrl;
    }

    public static String getGetCancelOrder() {
        return getCancelOrder;
    }

    public static String getGetCodeUrl() {
        return getCodeUrl;
    }
}
