package com.jifenke.lepluslive.fuyou.controller;


import com.jifenke.lepluslive.fuyou.service.YeepayService;
import com.jifenke.lepluslive.global.util.CookieUtils;
import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.Verify;
import com.jifenke.lepluslive.lejiauser.service.AliUserService;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.lejiauser.service.ValidateCodeService;
import com.jifenke.lepluslive.lejiauser.service.VerifyService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.service.YeepayScanCodeOrderService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* 易宝扫码 Created by zhangwen on 2016/12/6.
*/
@RestController
@RequestMapping("/pay/yeepay/alipay")
public class YeepayAliPayController {

  private static Logger log = LoggerFactory.getLogger(YeepayAliPayController.class);

  @Inject
  private AliUserService aliUserService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private YeepayScanCodeOrderService orderService;

  @Inject
  private YeepayService yeepayService;

  @Inject
  private WeiXinUserService weiXinUserService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private VerifyService verifyService;

  @Inject
  private ValidateCodeService validateCodeService;

  @RequestMapping("/bind")
    public ModelAndView bind(@RequestParam String ext,Model model,HttpServletRequest request,HttpServletResponse response) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    model.addAttribute("userId",strs[0]);
    model.addAttribute("merchantId",strs[1]);
    model.addAttribute("totalPrice",strs[2]);
    //发送验证码限制
    CookieUtils.setCookie(request,response,"leJiaUnionId","doubleoUtt2v8EFx0nymdx6dD7UnNGU1zs");
    Verify verify = verifyService.addVerify(49l, 18006);
    model.addAttribute("pageSid", verify.getPageSid());
    return MvUtil.go("/yeepay/ali/userBind");
  }

  @RequestMapping("/bind_confirm")
  public LejiaResult bindConfirm(@RequestParam String code,@RequestParam String userId,@RequestParam String phone) {
    Boolean b = validateCodeService.findByPhoneNumberAndCode(phone, code); //验证码是否正确
    if (!b) {
      return LejiaResult.build(3001, "*验证码错误");
    }else {
      LeJiaUser leJiaUser = leJiaUserService.findUserByPhoneNumber(phone);
      if(leJiaUser!=null){
        aliUserService.bindLejiaUser(userId,leJiaUserService.findUserByPhoneNumber(phone));
        return LejiaResult.build(200, "绑定成功",leJiaUser.getUserSid());
      }else {
        return LejiaResult.build(500, "*您的手机号还没有注册成为乐+会员");
      }
    }
  }

  @RequestMapping("/user")
   public ModelAndView pay(@RequestParam(required = true) String userId,@RequestParam(required = true) String totalPrice,@RequestParam(required = true) String merchantSid, HttpServletResponse response,HttpServletRequest request,Model model) {
    model.addAttribute("userId",userId);
    model.addAttribute("totalPrice",totalPrice);
    model.addAttribute("merchant",merchantService.findMerchantBySId(merchantSid).get());
    return MvUtil.go("/yeepay/ali/confirm");
  }

  @RequestMapping(value = "/userpay")
  public ModelAndView goUserPayPage(@RequestParam String ext, Model model,
                                    HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    Long totalPrice = new BigDecimal(strs[1]).multiply(new BigDecimal(100)).longValue();
    Long merchantId = new Long(strs[2]);
    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(strs[0]);
    model.addAttribute("leJiaUser", leJiaUser);
    ScoreA scorea = scoreAService.findScoreAByLeJiaUser(leJiaUser);
    model.addAttribute("scoreA",scorea);
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("merchantId", merchantId);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    model.addAttribute("merchant", merchant);
    if (merchant.getReceiptAuth() == 0||scorea.getScore()==0) {
      return MvUtil.go("/yeepay/ali/userPayNonScore");
    }
    return MvUtil.go("/yeepay/ali/userPay");
  }

  //支付宝支付非会员接口
  @RequestMapping(value = "/offLineOrder")
  public Map weixinPay(@RequestParam String truePrice, @RequestParam String userId,
                       @RequestParam Long merchantId, HttpServletRequest request) {
    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId("doubles2");//虚拟用户
    ScanCodeOrder
        order =
        orderService.createOrderForNoNMember(truePrice, merchantId, weiXinUser, 0,0,userId);
    //封装订单参数
    Map<String, String> result = null;
    try {
      result = yeepayService.buildAliParams(request, order);
      result.put("orderSid", order.getOrderSid());
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      result = new HashMap<>();
      result.put("err_msg", "出现未知错误,请联系管理员或稍后重试");
      return result;
    }
  }


  //微信支付会员接口
  @RequestMapping(value = "/offLineOrderForUser")
  public Map weixinPayForMember(@RequestParam String ext,
                                HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    ScanCodeOrder
        order =
        orderService.createOrderForMember(strs[0], Long.parseLong(strs[3]), strs[1],
                                          strs[4], leJiaUserService
                .findUserByUserSid(strs[2]), 0);
    //封装订单参数
    Map<String, String> map = null;
    try {
      map = yeepayService.buildAliParams(request, order);
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      map = new HashMap<>();
      map.put("err_msg", "出现未知错误,请联系管理员或稍后重试");
      return map;
    }
  }


  //全部红包支付
  @RequestMapping(value = "/payByScoreA")
  public
  @ResponseBody
  LejiaResult payByScoreA(@RequestParam String ext) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    try {
      ScanCodeOrder
          order =
          orderService.payByScoreA(strs[0], Long.parseLong(strs[1]), strs[2], 0);

      return LejiaResult.build(200, "", order);
    } catch (Exception e) {
      return LejiaResult.build(500, "出现未知错误,请联系管理员");
    }
  }



  private String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip != null && ip.length() > 15) {
      ip = ip.split(",")[0];
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
  }


}
