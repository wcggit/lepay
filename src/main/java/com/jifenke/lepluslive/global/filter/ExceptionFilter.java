//package com.jifenke.lepluslive.global.filter;
//
//
//import com.jifenke.lepluslive.global.util.JsonUtils;
//import com.jifenke.lepluslive.global.util.LejiaResult;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
//* Created by wcg on 16/3/9.
//*/
//@Configuration
//public class ExceptionFilter implements HandlerExceptionResolver {
//
//  private final Logger LOG = LoggerFactory.getLogger(ExceptionFilter.class);
//  @Override
//  public ModelAndView resolveException(HttpServletRequest httpServletRequest,
//                                       HttpServletResponse httpServletResponse, Object o,
//                                       Exception e) {
//      LOG.error(e+e.getMessage());
//    return new ModelAndView("error");
//  }
//}
