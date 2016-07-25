package com.jifenke.lepluslive.wxpay.controller;

import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wcg on 16/3/18.
 */
@RestController
@RequestMapping("/lepay")
public class WeixinController {

  private static Logger log = LoggerFactory.getLogger(WeixinController.class);

  @Value("${weixin.appId}")
  private String appId;

  @Value("${weixin.weixinRootUrl}")
  private String weixinRootUrl;

  @Inject
  private WeiXinUserService weiXinUserService;

  @Inject
  private WeiXinService weiXinService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private LeJiaUserService leJiaUserService;


  @Inject
  private MerchantService merchantService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private OffLineOrderService offLineOrderService;

// String openid = "oVmqjxLVGMaHMX7dRsAzZg7BlpgE";

  @RequestMapping("/wxpay/{id}")
  public void merchantId() {

  }

//  @RequestMapping("/merchant/{merchantSid}")
//  public ModelAndView goPay(@PathVariable String merchantSid,Model model,HttpServletRequest request) {
//    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId(openid);
//    Optional<Merchant> optional = merchantService.findMerchantBySId(merchantSid);
//    if (optional.isPresent()) {
//      model.addAttribute("merchant", optional.get());
//      if (weiXinUser == null || weiXinUser.getState() == 0) {
//        new Thread(() -> {
//          //未关注公众号的人消费默认注册一个lejiauser
//          weiXinUserService.registerLeJiaUserForNonMember(openid, weiXinUser);
//        }).start();
//        model.addAttribute("openid", openid);
//
//      } else {
//        model.addAttribute("leJiaUser", weiXinUser.getLeJiaUser());
//        model
//            .addAttribute("scoreA", scoreAService.findScoreAByLeJiaUser(weiXinUser.getLeJiaUser()));
//        model.addAttribute("ljopenid", openid);
//      }
//
//      model.addAttribute("wxConfig", getWeiXinPayConfig(request));
//      new Thread(()->{
//        WeixinPayUtil
//            .createUnifiedOrder("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",
//                                "test");
//      }).start();
//      return MvUtil.go("/weixin/wxPay");
//    } else {
//      return null;
//    }
//
//  }

  @RequestMapping("/wxpay/pay")
  public ModelAndView goPayPage(@RequestParam String openid, @RequestParam String merchantSid,
                                @RequestParam(required = false) String pure,
                                HttpServletRequest request,
                                Model model) {

    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId(openid);
    Optional<Merchant> optional = merchantService.findMerchantBySId(merchantSid);
    if (optional.isPresent()) {
      model.addAttribute("merchant", optional.get());
      if (weiXinUser == null || weiXinUser.getState() == 0) {
        new Thread(() -> {
          //未关注公众号的人消费默认注册一个lejiauser
          weiXinUserService.registerLeJiaUserForNonMember(openid, weiXinUser);
        }).start();
        model.addAttribute("openid", openid);
        if (pure != null && "access".equals(pure)) {
          model.addAttribute("pure", true);
        }
      } else {
        //如果扫纯支付码
        if (pure != null && "access".equals(pure)) {
          model.addAttribute("openid", openid);
          model.addAttribute("pure", true);
        } else {
          model.addAttribute("leJiaUser", weiXinUser.getLeJiaUser());
          model
              .addAttribute("scoreA",
                            scoreAService.findScoreAByLeJiaUser(weiXinUser.getLeJiaUser()));
          model.addAttribute("ljopenid", openid);
        }
      }

      model.addAttribute("wxConfig", getWeiXinPayConfig(request));
      new Thread(() -> {
        WeixinPayUtil
            .createUnifiedOrder("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",
                                "test");
      }).start();
      return MvUtil.go("/weixin/wxPay");
    } else {
      return null;
    }
  }


  @RequestMapping("/wxpay/userRegister")
  public void userRegister(@RequestParam String merchantSid, @RequestParam String code,
                           @RequestParam(required = false) String pure,
                           HttpServletResponse response)
      throws IOException {
    Map<String, Object> map = weiXinService.getSnsAccessToken(code);
    System.out.println(map.toString());
    String openid = map.get("openid").toString();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("/lepay/wxpay/pay?openid=");
    stringBuffer.append(openid);
    stringBuffer.append("&merchantSid=");
    stringBuffer.append(merchantSid);
    if (pure != null && "access".equals(pure)) {
      stringBuffer.append("&pure=access");
    }
    response.sendRedirect(stringBuffer.toString());

  }

  //微信支付非会员接口
  @RequestMapping(value = "/wxpay/offLineOrder")
  public
  @ResponseBody
  Map<Object, Object> weixinPay(@RequestParam String truePrice, @RequestParam String openid,
                                @RequestParam boolean pure,
                                @RequestParam Long merchantId, HttpServletRequest request) {
    OffLineOrder
        offLineOrder =
        offLineOrderService.createOffLineOrderForNoNMember(truePrice, merchantId, openid,pure);
    //封装订单参数
    SortedMap<Object, Object>
        map =
        weiXinPayService.buildOrderParams(request, offLineOrder, openid);
    //获取预支付id
    Map unifiedOrder = weiXinPayService.createUnifiedOrder(map);
    if (unifiedOrder.get("prepay_id") != null) {
      //返回前端页面
      return weiXinPayService
          .buildJsapiParams(unifiedOrder.get("prepay_id").toString(), offLineOrder.getOrderSid());
    } else {
      log.error(unifiedOrder.get("return_msg").toString());
      unifiedOrder.clear();
      unifiedOrder.put("err_msg", "出现未知错误,请联系管理员或稍后重试");
      return unifiedOrder;
    }
  }

  @RequestMapping(value = "/wxpay/userpay")
  public ModelAndView goUserPayPage(@RequestParam String ext, Model model,
                                    HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    Long totalPrice = new BigDecimal(strs[1]).multiply(new BigDecimal(100)).longValue();
    Long merchantId = new Long(strs[2]);
    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(strs[0]);
    model.addAttribute("leJiaUser", leJiaUser);
    model.addAttribute("scoreA", scoreAService.findScoreAByLeJiaUser(leJiaUser));
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("merchantId", merchantId);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    model.addAttribute("merchant", merchant);
    model.addAttribute("wxConfig", getWeiXinPayConfig(request));
    model.addAttribute("openid", strs[3]);
    new Thread(() -> {
      WeixinPayUtil
          .createUnifiedOrder("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",
                              "test");
    }).start();
    if (merchant.getReceiptAuth() == 0) {
      return MvUtil.go("/weixin/userPayNonScore");
    }

    return MvUtil.go("/weixin/wxUserPay");
  }


  //微信支付会员接口
  @RequestMapping(value = "/wxpay/offLineOrderForUser")
  public
  @ResponseBody
  Map<Object, Object> weixinPayForMember(@RequestParam String ext,
                                         HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    OffLineOrder
        offLineOrder =
        offLineOrderService.createOffLineOrderForMember(strs[0], Long.parseLong(strs[3]), strs[1],
                                                        strs[4], leJiaUserService
                .findUserByUserSid(strs[2]));
    //封装订单参数
    SortedMap<Object, Object>
        map =
        weiXinPayService.buildOrderParams(request, offLineOrder, strs[5]);
    //获取预支付id
    Map unifiedOrder = weiXinPayService.createUnifiedOrder(map);
    if (unifiedOrder.get("prepay_id") != null) {
      //返回前端页面
      return weiXinPayService
          .buildJsapiParams(unifiedOrder.get("prepay_id").toString(), offLineOrder.getOrderSid());
    } else {
      log.error(unifiedOrder.get("return_msg").toString());
      unifiedOrder.clear();
      unifiedOrder.put("err_msg", "出现未知错误,请联系管理员或稍后重试");
      return unifiedOrder;
    }
  }


  //乐加会员但是没有红包消费
  @RequestMapping(value = "/wxpay/userNonScoreA")
  public
  @ResponseBody
  Map<Object, Object> userPayByScoreA(@RequestParam String ext,
                                      HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    OffLineOrder
        offLineOrder =
        offLineOrderService.createOffLineOrderForMember(strs[1], Long.parseLong(strs[2]), "0",
                                                        strs[1], leJiaUserService
                .findUserByUserSid(strs[0]));
    //封装订单参数
    SortedMap<Object, Object>
        map =
        weiXinPayService.buildOrderParams(request, offLineOrder, strs[3]);
    //获取预支付id
    Map unifiedOrder = weiXinPayService.createUnifiedOrder(map);
    if (unifiedOrder.get("prepay_id") != null) {
      //返回前端页面
      return weiXinPayService
          .buildJsapiParams(unifiedOrder.get("prepay_id").toString(), offLineOrder.getOrderSid());
    } else {
      log.error(unifiedOrder.get("return_msg").toString());
      unifiedOrder.clear();
      unifiedOrder.put("err_msg", "出现未知错误,请联系管理员或稍后重试");
      return unifiedOrder;
    }
  }

  //全部红包支付
  @RequestMapping(value = "/wxpay/payByScoreA")
  public
  @ResponseBody
  LejiaResult payByScoreA(@RequestParam String ext,
                          HttpServletRequest request) {
    String result = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = result.split(" ");
    try {
      OffLineOrder offLineOrder = offLineOrderService.payByScoreA(strs[0], strs[1], strs[2]);
      return LejiaResult.build(200, "", offLineOrder);

    } catch (Exception e) {
      return LejiaResult.build(500, "出现未知错误,请联系管理员");

    }
  }


  /**
   * 获取支付页面的配置参数wxconfig
   */
  private Map getWeiXinPayConfig(HttpServletRequest request) {
    Long timestamp = new Date().getTime() / 1000;
    String noncestr = MvUtil.getRandomStr();
    Map map = new HashMap<>();
    map.put("appId", appId);
    map.put("timestamp", timestamp);
    map.put("noncestr", noncestr);
    map.put("signature", weiXinPayService.getJsapiSignature(request, noncestr, timestamp));
    return map;
  }

}
