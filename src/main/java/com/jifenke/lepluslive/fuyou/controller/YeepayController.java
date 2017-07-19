package com.jifenke.lepluslive.fuyou.controller;

import com.jifenke.lepluslive.fuyou.service.FuYouPayService;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantScanPayWayService;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.service.ScanCodeOrderService;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
  private ScanCodeOrderService orderService;

  @Inject
  private FuYouPayService fuYouPayService;

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
