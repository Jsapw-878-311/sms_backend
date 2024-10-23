package ltd.newbee.mall.sdk.smspool.param;

import lombok.Data;

@Data
public class CountryListParam {

    private Long pool;

    private Long country_id;

    private String name;

    private String short_name;

    private String price;

    public Long getPool() {
        return pool;
    }

    public void setPool(Long pool) {
        this.pool = pool;
    }

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
