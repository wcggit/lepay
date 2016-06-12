package com.jifenke.lepluslive.global.filter;

import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Created by wcg on 16/4/1.
*/
public class PayFilter implements HandlerInterceptor {

  private WeiXinUserService weiXinUserService;

  private String appId = Constants.APPID;

  private String weixinRootUrl = Constants.WEI_XIN_ROOT_URL;


  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response, Object o) throws Exception {
    String ua = request.getHeader("user-agent")
        .toLowerCase();
    String action = request.getRequestURI();
    if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
      String[] strs = action.split("/");
      // response.sendRedirect("/lepay/wxpay/" + strs[3]);
      request.getRequestDispatcher("/lepay/wxpay/" + strs[3]).forward(request, response);
      return false;
    } else {
      request.getRequestDispatcher("/lepay/scan").forward(request, response);
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
