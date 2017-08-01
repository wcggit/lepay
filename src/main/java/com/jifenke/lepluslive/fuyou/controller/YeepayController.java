package com.jifenke.lepluslive.fuyou.controller;

import com.jifenke.lepluslive.fuyou.service.YeepayService;
import com.jifenke.lepluslive.fuyou.util.ZGTUtils;
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
import com.jifenke.lepluslive.order.service.YeepayScanCodeOrderService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wcg on 2017/7/19.
 */
@RestController
@RequestMapping("/pay/yeepay")
public class YeepayController {

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
  private YeepayScanCodeOrderService orderService;

  @Inject
  private YeepayService yeepayService;

  @Inject
  private MerchantScanPayWayService scanPayWayService;

  @Inject
  private ScoreCService scoreCService;

  /**
   * 测试易宝支付微信支付接口
   * @param openid
   * @param merchantSid
   * @param pure
   * @param request
   * @param model
   * @return
   */
  @RequestMapping("/wxPay")
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
      } else if (way ==2){
        return MvUtil.go("/yeepay/wxPay");
      }else {
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
    ScoreA scorea = scoreAService.findScoreAByLeJiaUser(leJiaUser);
    model.addAttribute("scoreA",scorea);
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("merchantId", merchantId);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    model.addAttribute("merchant", merchant);
    model.addAttribute("wxConfig", getWeiXinPayConfig(request));
    model.addAttribute("openid", strs[3]);
    if (merchant.getReceiptAuth() == 0 ||scorea.getScore()==0) {
      return MvUtil.go("/yeepay/userPayNonScore");
    }
    return MvUtil.go("/yeepay/wxUserPay");
  }

  //微信支付非会员接口
  @RequestMapping(value = "/offLineOrder")
  public Map weixinPay(@RequestParam String truePrice, @RequestParam String openid,
                       @RequestParam Long merchantId, HttpServletRequest request) {
    WeiXinUser weiXinUser = weiXinUserService.findWeiXinUserByOpenId(openid);
    ScanCodeOrder
        order =
        orderService.createOrderForNoNMember(truePrice, merchantId, weiXinUser, 0,1,null);
    //封装订单参数
    Map<String, String> result = null;
    try {
      result = yeepayService.buildParams(request, openid, order);
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
      map = yeepayService.buildParams(request, strs[5], order);
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
  public void afterPay(HttpServletResponse res,@RequestParam String data) throws IOException, JDOMException {

    System.out.println("---易宝公众号支付充值回调成功请求收到-----");
    Map<String, String> responseMAp = ZGTUtils.decryptData(data);
    if (ZGTUtils.checkHmac(responseMAp, ZGTUtils.PAYAPICALLBACK_HMAC_ORDER)) {
      if (responseMAp.get("code").equals("1")) {
        ScanCodeOrder order = orderService.findOrderByOrderSid(responseMAp.get("requestid"));
        orderService.lockCheckMessageState(order);
        //处理订单
        try {
          orderService.lockPaySuccess(order, responseMAp);
          PrintWriter out = res.getWriter();
          out.println("SUCCESS");
          out.close();
        } catch (Exception e) {
        }
      }
    }
  }

  @RequestMapping(value = "/paySuccess")
  public ModelAndView goPaySuccessPageForMember(@RequestParam String data, Model model) {
    Map<String, String> responseMAp = ZGTUtils.decryptData(data);

    ScanCodeOrder order = orderService.findOrderByOrderSid(responseMAp.get("requestid"));
    if (order.getState() == 0) {//延时8秒调接口查询订单是否支付完成
      String settleDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      new Thread(() -> {
        try {
          Thread.sleep(8000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        orderService.checkOrderState(responseMAp.get("requestid"), settleDate);
      }).start();
    }

    model.addAttribute("order", order);
    model.addAttribute("scoreC", scoreCService.findScoreCByleJiaUser(order.getLeJiaUser()));
    return MvUtil.go("/fuyou/paySuccessbj");

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
