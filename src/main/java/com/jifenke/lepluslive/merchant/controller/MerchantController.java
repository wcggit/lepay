package com.jifenke.lepluslive.merchant.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantInfo;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.pospay.domain.MerchantResult;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;

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
  public LejiaResult login(@RequestParam String name, @RequestParam String pwd) {

    MerchantUser merchantUser = merchantService.findMerchantUserByName(name);
    if (merchantUser != null) {
      String origin = MD5Util.MD5Encode(pwd, "utf-8");
      if (merchantUser.getPassword().equals(origin)) {
        String merchantName = "";
        List<Merchant> list;
        if (merchantUser.getType() == 8) {
          list = merchantService.countByMerchantUser(merchantUser);
        } else {
          list = merchantService.countByMerchantUser(merchantUser.getId());
        }
        List<MerchantResult> result = new ArrayList<>();
        if (list != null) {
          merchantName = list.get(0).getMerchantUser().getMerchantName();
          for (Merchant merchant : list) {
            MerchantResult merchantResult = new MerchantResult();
            merchantResult.setId(merchant.getId());
            merchantResult.setPartnership(merchant.getPartnership());
            merchantResult.setAccountId(merchantUser.getId());
            merchantResult.setAccount(name);
            merchantResult.setMerchantName(merchant.getName());
            MerchantInfo info = merchant.getMerchantInfo();
            String qrCode = "";
            if (info != null) {
              if (info.getTicket() != null) {
                qrCode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + info.getTicket();
              }
            }
            merchantResult.setQrCode(qrCode);
            result.add(merchantResult);
          }
        }
        return LejiaResult.build(200, merchantName, result);
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
  public LejiaResult editPwd(@RequestParam Long accountId, @RequestParam String newPwd,
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
