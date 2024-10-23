package ltd.newbee.mall.sdk.smspool.param;

import lombok.Data;

@Data
public class CancelSMSParam {

    private int success;

    private String message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
