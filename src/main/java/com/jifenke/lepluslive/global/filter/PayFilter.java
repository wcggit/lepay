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
public class PayFilter implements HandlerInterceptor {

  private WeiXinUserService weiXinUserService;

  private String appId = Constants.APPID;

  private String aliAppId = Constants.ALIAPPID;

  private String weixinRootUrl = Constants.WEI_XIN_ROOT_URL;


  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response, Object o) throws Exception {
    String ua = request.getHeader("user-agent");
    if (ua != null) {
      ua = ua.toLowerCase();
      String action = request.getRequestURI();
      if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
        String[] strs = action.split("/");
        String pure = request.getParameter("pure");
        if (pure != null && "access".equals(pure)) {
          request.getRequestDispatcher("/lepay/wxpay/" + strs[3] + "?pure=" + pure)
              .forward(request, response);
        } else {
          request.getRequestDispatcher("/lepay/wxpay/" + strs[3]).forward(request, response);
        }
        return false;
      } else if (ua.indexOf("alipay") > 0) {

        String[] strs = action.split("/");
        String callbackUrl = weixinRootUrl + "/pay/alipay/userToken?merchantSid=" + strs[3];
        String
            redirectUrl =
            "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=" + aliAppId
            + "&redirect_uri=" +
            URLEncoder.encode(callbackUrl, "UTF-8")
            + "&response_type=code&scope=auth_base";
        response.sendRedirect(redirectUrl);
        return false;

      } else {
        request.getRequestDispatcher("/lepay/scan").forward(request, response);

      }
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

  public void setWeiXinUserService(WeiXinUserService weiXinUserService) {
    this.weiXinUserService = weiXinUserService;
  }
}
