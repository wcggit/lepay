package com.jifenke.lepluslive.merchant.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantInfo;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.pospay.domain.MerchantResult;

import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * 商户相关 Created by zhangwen on 16/10/10.
 */
@RestController
@RequestMapping("/lepay/m_user")
public class MerchantController {

  @Inject
  private MerchantService merchantService;

  /**
   * 银联商务POS机商户登录  16/10/10
   *
   * @param name 账户名
   * @param pwd  密码
   */
  @ApiOperation(value = "pos机商户登录")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult login(@RequestParam String name, @RequestParam String pwd) {

    MerchantUser merchantUser = merchantService.findMerchantUserByName(name);
    if (merchantUser != null) {
      String origin = MD5Util.MD5Encode(pwd, "utf-8");
      if (merchantUser.getPassword().equals(origin)) {
        MerchantResult result = new MerchantResult();
        result.setId(merchantUser.getMerchant().getId());
        result.setAccountId(merchantUser.getId());
        result.setAccount(name);
        MerchantInfo info = merchantUser.getMerchant().getMerchantInfo();
        String qrCode = "";
        if (info != null) {
          if (info.getTicket() != null) {
            qrCode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + info.getTicket();
          }
        }
        result.setQrCode(qrCode);
        return LejiaResult.ok(result);
      }
    }
    return LejiaResult.build(8001, "用户名或密码错误");
  }

  /**
   * 银联商务pos机商户登录密码修改  16/10/10
   *
   * @param accountId 商户账号ID
   * @param newPwd    新密码
   * @param pwd       旧密码
   */
  @ApiOperation(value = "pos机商户登录密码修改")
  @RequestMapping(value = "/editPwd", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult editPwd(@RequestParam Long accountId, @RequestParam String newPwd,
                      @RequestParam String pwd) {

    MerchantUser merchantUser = merchantService.findMerchantUserById(accountId);
    if (merchantUser != null) {
      String origin = MD5Util.MD5Encode(pwd, "utf-8");
      if (merchantUser.getPassword().equals(origin)) {
        merchantService.resetPwd(merchantUser, newPwd);
        return LejiaResult.ok();
      } else {
        return LejiaResult.build(8003, "原密码不正确");
      }
    }
    return LejiaResult.build(8002, "未找到商户登录账号信息");
  }


}
