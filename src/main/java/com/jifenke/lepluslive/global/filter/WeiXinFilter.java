package com.jifenke.lepluslive.global.filter;

import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wcg on 16/4/1.
 */
public class WeiXinFilter implements HandlerInterceptor {

  private WeiXinUserService weiXinUserService;

  private String appId = Constants.APPID;
//  private String appId = Constants.TEST_APPID;

  private String weixinRootUrl = Constants.WEI_XIN_ROOT_URL;
//  private String weixinRootUrl = Constants.WEI_XIN_TEST_ROOT_URL;


  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response, Object o) throws Exception {
    String action = request.getRequestURI();
    if (action.equals("/lepay/wxpay/afterPay") || action.equals("/lepay/wxpay/userRegister")||action.equals("/lepay/wxpay/yeepay")) {
      return true;
    }

    String ua = request.getHeader("user-agent")
        .toLowerCase();
    if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
      if (action
              .equals("/lepay/wxpay/userpay") || action
              .equals("/lepay/wxpay/pay") || action
              .equals("/lepay/wxpay/offLineOrderForUser") || action
              .equals("/lepay/wxpay/offLineOrder") || action
              .equals("/lepay/wxpay/afterPay") || action
              .equals("/lepay/wxpay/payByScoreA") || action
              .equals("/lepay/wxpay/paySuccess") || action
              .equals("/lepay/wxpay/paySuccessForNon") || action
              .equals("/lepay/wxpay/userNonScoreA")) {
        return true;
      }
      String[] strs = action.split("/");
      String callbackUrl = weixinRootUrl + "/lepay/wxpay/userRegister?merchantSid=" + strs[3];
      String pure = request.getParameter("pure");
      if (pure != null && "access".equals(pure)) {
        callbackUrl += "&pure=access";
      }
      String
          redirectUrl =
          "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" +
          URLEncoder.encode(callbackUrl, "UTF-8")
          + "&response_type=code&scope=snsapi_base&state=123&connect_redirect=1#wechat_redirect";
      response.sendRedirect(redirectUrl);
      return false;
    }
    return false;
  }

  @Override
  public void postHandle(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse, Object o,
                         ModelAndView modelAndView) throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse, Object o, Exception e)
      throws Exception {

  }

}
