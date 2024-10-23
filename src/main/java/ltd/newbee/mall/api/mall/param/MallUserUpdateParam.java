/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 用户修改param
 */
@Data
public class MallUserUpdateParam implements Serializable {

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户密码(需要MD5加密)")
    private String passwordMd5;

    @ApiModelProperty("个性签名")
    private String introduceSign;


    @Override
    public String toString() {
        return "MallUserUpdateParam{" +
                "nickName='" + nickName + '\'' +
                ", passwordMd5='" + passwordMd5 + '\'' +
                ", introduceSign='" + introduceSign + '\'' +
                '}';
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    public String getIntroduceSign() {
        return introduceSign;
    }

    public void setIntroduceSign(String introduceSign) {
        this.introduceSign = introduceSign;
    }
}
