/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.mall;

import io.swagger.annotations.*;
import ltd.newbee.mall.api.mall.param.*;
import ltd.newbee.mall.api.mall.vo.UserWalletFindVO;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToMallUser;
import ltd.newbee.mall.api.mall.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.entity.MallUserRechargeLog;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.service.impl.NewBeeMallUserServiceImpl;
import ltd.newbee.mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "4.新蜂商城用户操作相关接口")
@RequestMapping("/api/v1")
public class NewBeeMallPersonalAPI {

    @Resource
    private NewBeeMallUserServiceImpl newBeeMallUserService;

    private static final Logger logger = LoggerFactory.getLogger(NewBeeMallPersonalAPI.class);

    @PostMapping("/user/login")
    @ApiOperation(value = "登录接口", notes = "返回token")
    public Result<String> login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam) {
//        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
////            如果用户的电话号码长度不为11位
//            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
//        }
        String loginResult = newBeeMallUserService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("login api,loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

//        登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
//        登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @PostMapping("/user/logout")
    @ApiOperation(value = "登出接口", notes = "清除token")
    public Result<String> logout(@TokenToMallUser MallUser loginMallUser) {
        Boolean logoutResult = newBeeMallUserService.logout(loginMallUser.getUserId());

        logger.info("logout api,loginMallUser={}", loginMallUser.getUserId());

        //登出成功
        if (logoutResult) {
            return ResultGenerator.genSuccessResult();
        }
        //登出失败
        return ResultGenerator.genFailResult("logout error");
    }

    @PostMapping("/user/register")
    @ApiOperation(value = "用户注册", notes = "")
    public Result register(@RequestBody @Valid MallUserRegisterParam mallUserRegisterParam) {
//        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
//            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
//        }
        String registerResult = newBeeMallUserService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword());

        logger.info("register api,loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);

        //注册成功
//        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
//            return ResultGenerator.genSuccessResult();
//        }
        if (!StringUtils.isEmpty(registerResult) && registerResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(registerResult);
            return result;
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PutMapping("/user/info")
    @ApiOperation(value = "修改用户信息", notes = "")
    public Result updateInfo(@RequestBody @ApiParam("用户信息") MallUserUpdateParam mallUserUpdateParam, @TokenToMallUser MallUser loginMallUser) {
        Boolean flag = newBeeMallUserService.updateUserInfo(mallUserUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @GetMapping("/user/info")
    @ApiOperation(value = "获取用户信息", notes = "")
    public Result<NewBeeMallUserVO> getUserDetail(@TokenToMallUser MallUser loginMallUser) {
        //已登录则直接返回
        NewBeeMallUserVO mallUserVO = new NewBeeMallUserVO();
        BeanUtil.copyProperties(loginMallUser, mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }

    @PostMapping("/user/getRecharge")
    @ApiOperation(value = "用户充值信息核查", notes = "")
    public Result<UserWalletFindVO> getUserRechargeResult(@RequestBody UserWalletFindParam userWalletFindParam,
                                                  @TokenToMallUser MallUser loginMallUser) {
        if(userWalletFindParam.isUserIsOnlyFind()){
            return ResultGenerator.genSuccessResult(
                    newBeeMallUserService.findUserWalletValue(loginMallUser.getUserId()));
        }
        return ResultGenerator.genSuccessResult(
                newBeeMallUserService.confirmUserRecharge(loginMallUser.getUserId(),
                userWalletFindParam.getUserRechargeId(),
                userWalletFindParam.getUserRechargeValue()));
    }

    @GetMapping("/user/rechargeList")
    @ApiOperation(value = "用户充值记录核查", notes = "")
    public Result getUserRechargeList(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                                      @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                      @TokenToMallUser MallUser loginMallUser) {
        logger.info("User:{}", loginMallUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("userId", loginMallUser.getUserId());
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newBeeMallUserService.getRechargeList(pageUtil));
    }
}
