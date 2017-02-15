package com.jifenke.lepluslive.fuyou.controller;

import com.jifenke.lepluslive.fuyou.service.FuYouPayService;
import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MapUtil;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantScanPayWayService;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.service.ScanCodeOrderService;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * 富友扫码 Created by zhangwen on 2016/12/6.
 */
@RestController
@RequestMapping("/pay/wxpay")
public class ScanCodeOrderController {

  private static Logger log = LoggerFactory.getLogger(ScanCodeOrderController.class);

  @Value("${weixin.appId}")
  private String appId;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private WeiXinUserService weiXinUserService;

  @Inject
  private ScanCodeOrderService orderService;

  @Inject
  private FuYouPayService fuYouPayService;

  @Inject
  private MerchantScanPayWayService scanPayWayService;

  @RequestMapping("/pay")
  public ModelAndView goPayPage(@RequestParam String openid, @RequestParam String merchantSid,
                                @RequestParam(required = false) String pure,
                                HttpServletRequest request,
                                Model model) {

    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId(openid);
    Optional<Merchant> optional = merchantService.findMerchantBySId(merchantSid);
    if (optional.isPresent()) {
      Merchant merchant = optional.get();
      model.addAttribute("merchant", merchant);
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
      //0=富友结算|1=乐加结算|2=暂不开通
      int way = scanPayWayService.findByMerchantId(merchant.getId());
      if (way == 1) {
        return MvUtil.go("/weixin/wxPay");
      } else if (way == 0) {
        return MvUtil.go("/fuyou/wxPay");
      } else {
        return MvUtil.go("/weixin/wxPay");
      }
    } else {
      return null;
    }
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
    model.addAttribute("scoreA", scoreAService.findScoreAByLeJiaUser(leJiaUser));
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("merchantId", merchantId);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    model.addAttribute("merchant", merchant);
    model.addAttribute("wxConfig", getWeiXinPayConfig(request));
    model.addAttribute("openid", strs[3]);
    if (merchant.getReceiptAuth() == 0) {
      return MvUtil.go("/fuyou/userPayNonScore");
    }
    return MvUtil.go("/fuyou/wxUserPay");
  }

  //微信支付非会员接口
  @RequestMapping(value = "/offLineOrder")
  public Map weixinPay(@RequestParam String truePrice, @RequestParam String openid,
                       @RequestParam Long merchantId, HttpServletRequest request) {
    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId(openid);
    ScanCodeOrder
        order =
        orderService.createOrderForNoNMember(truePrice, merchantId, weiXinUser, 0);
    //封装订单参数
    Map<String, String> result = null;
    try {
      result = fuYouPayService.buildParams(request, openid, order);
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
      map = fuYouPayService.buildParams(request, strs[5], order);
      map.put("orderSid", order.getOrderSid());
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

  /**
   * 微信回调函数
   */
  @RequestMapping(value = "/afterPay", produces = MediaType.APPLICATION_XML_VALUE)
  public String afterPay(HttpServletRequest request) throws IOException, JDOMException {

    System.out.println("---富友公众号支付充值回调成功请求收到-----");

    String content = request.getParameter("req");
    Map<String, String> contentPap;

    try {
      String result = URLDecoder.decode(content, "GBK");
      System.out.println(result);
      contentPap = MapUtil.xmlStringToMap(result);
      System.out.println(contentPap.toString());
      String outTradeNo = contentPap.get("mchnt_order_no"); //自己的订单号
      String dealStatus = contentPap.get("result_code");    //交易状态
      String orderCode = contentPap.get("transaction_id");    //第三方订单号
      String settleDate = contentPap.get("txn_fin_ts");    //第三方订单号
      System.out.println(
          "dealStatus------" + dealStatus + "-----outTradeNo---" + outTradeNo + "----orderCode---"
          + orderCode + "-----订单完成时间------" + settleDate);

      if ("000000".equals(dealStatus)) {
        ScanCodeOrder order = orderService.findOrderByOrderSid(outTradeNo);
        //确定只发送一条模版消息;
        orderService.lockCheckMessageState(order);
        //处理订单
        orderService.lockPaySuccess(order, contentPap);
        return "1";
      } else {
        System.out.println("支付失败");
        return "0";
      }
    } catch (Exception e3) {
      e3.printStackTrace();
      System.out.println("处理异常===支付失败");
      return "0";
    }
  }

  @RequestMapping(value = "/paySuccess")
  public ModelAndView goPaySuccessPageForMember(@RequestParam String orderSid, Model model) {
    ScanCodeOrder order = orderService.findOrderByOrderSid(orderSid);
    if (order.getState() == 0) {//延时8秒调接口查询订单是否支付完成
      String settleDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      new Thread(() -> {
        try {
          Thread.sleep(8000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        orderService.checkOrderState(orderSid, settleDate);
      }).start();
    }

    model.addAttribute("order", order);

    return MvUtil.go("/fuyou/paySuccess");

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
