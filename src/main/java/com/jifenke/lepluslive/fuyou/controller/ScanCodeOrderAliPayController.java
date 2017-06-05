package com.jifenke.lepluslive.fuyou.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jifenke.lepluslive.fuyou.service.FuYouPayService;
import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.global.util.HttpClientUtil;
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
import com.jifenke.lepluslive.score.service.ScoreCService;
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
@RequestMapping("/pay/alipay")
public class ScanCodeOrderAliPayController {

  private static Logger log = LoggerFactory.getLogger(ScanCodeOrderAliPayController.class);


  @RequestMapping("/userToken")
  public ModelAndView goPayPage(@RequestParam String auth_code, @RequestParam String merchantSid,
                                @RequestParam(required = false) String pure,
                                HttpServletRequest request,
                                Model model) {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "authorization_code");
    params.put("code", auth_code);
    try {
      String token =
          (String) new ObjectMapper().readValue(
              HttpClientUtil.post("https://openapi.alipay.com/gateway.do", params, "utf-8"),
              HashMap.class).get("alipay_system_oauth_token_response");
      String userId =
          (String) new ObjectMapper().readValue(token, HashMap.class).get("user_id");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


}
