/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.api.mall.NewBeeMallPersonalAPI;
import ltd.newbee.mall.api.mall.param.MallUserUpdateParam;
import ltd.newbee.mall.api.mall.vo.UserWalletFindVO;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.dao.NewBeeMallUserRechargeLogMapper;
import ltd.newbee.mall.dao.NewBeeMallUserTokenMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.entity.MallUserRechargeLog;
import ltd.newbee.mall.entity.MallUserToken;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.sdk.config.RestUtil;
import ltd.newbee.mall.sdk.pay.YiPayUrl;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {

    @Resource
    private MallUserMapper mallUserMapper;

    @Resource
    private NewBeeMallUserTokenMapper newBeeMallUserTokenMapper;

    @Resource
    private NewBeeMallUserRechargeLogMapper newBeeMallUserRechargeLogMapper;

    @Resource
    private RestUtil restUtil;

    private static final Logger logger = LoggerFactory.getLogger(NewBeeMallPersonalAPI.class);

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
//            检测用户是否已经存在
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
//            检测数据库写入结果
//            return ServiceResultEnum.SUCCESS.getResult();
            return this.login(registerUser.getLoginName(), registerUser.getPasswordMd5());
        }
        return ServiceResultEnum.DB_ERROR.getResult();
//        return this.login(registerUser.getLoginName(), registerUser.getPasswordMd5());
    }

    @Override
    public String login(String loginName, String passwordMD5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            MallUserToken mallUserToken = newBeeMallUserTokenMapper.selectByPrimaryKey(user.getUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 24 * 24 * 3600 * 1000);//过期时间 24 天
            if (mallUserToken == null) {
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (newBeeMallUserTokenMapper.insertSelective(mallUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //更新
                if (newBeeMallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        //user.setPasswordMd5(mallUser.getPasswordMd5());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())){
            user.setPasswordMd5(mallUser.getPasswordMd5());
        }
        user.setIntroduceSign(mallUser.getIntroduceSign());
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        return newBeeMallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Override
    public PageResult getNewBeeMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }

    @Override
    public BigDecimal findUserWalletValue(Long userId) {
        return mallUserMapper.findWalletValue(userId);
    }

    @Override
    public UserWalletFindVO confirmUserRecharge(Long userId, String userRechargeId, BigDecimal userRechargeValue) {
        //输入 用户id， 订单id， 充值金额
        //输出 钱包余额， 订单id， 充值时间， 充值金额， 确认结果

        //调用判断
        HttpHeaders headers = new HttpHeaders();
        headers.set("Custom-Header", "header-value");
        String result = restUtil.request(YiPayUrl.getOrderConfirm(), headers, HttpMethod.GET);
        // 处理响应
        System.out.println(result);

        //如果正确插入充值记录
        BigDecimal lastValue = mallUserMapper.findWalletValue(userId);
        lastValue.add(userRechargeValue);
        MallUserRechargeLog mallUserRechargeLog = null;
        assert false;
//        mallUserRechargeLog.setMallUserRechargeLog(userId, userRechargeId, null, userRechargeValue, null);
        newBeeMallUserRechargeLogMapper.insert(mallUserRechargeLog);
        //插入余额
        mallUserMapper.updateWalletByUserId(lastValue, userId);

        UserWalletFindVO userWalletFindVO = null;
        userWalletFindVO.setUserWalletFindVO(lastValue, userRechargeId, null, userRechargeValue, true);
        return userWalletFindVO;
    }

    public PageResult getRechargeList(PageQueryUtil pageUtil){
        List<MallUserRechargeLog> rechargeLogList = newBeeMallUserRechargeLogMapper.findRechargeList(pageUtil);
        int total = newBeeMallUserRechargeLogMapper.getTotalNewBeeMallRecharge(pageUtil);
        logger.info("jsapw---:", rechargeLogList.toString());
        PageResult pageResult = new PageResult(rechargeLogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
