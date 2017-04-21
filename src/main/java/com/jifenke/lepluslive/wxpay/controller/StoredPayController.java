//package com.jifenke.lepluslive.wxpay.controller;
//
//import com.jifenke.lepluslive.global.util.Des;
//import com.jifenke.lepluslive.global.util.LejiaResult;
//import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
//import com.jifenke.lepluslive.order.service.StoredPayService;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.inject.Inject;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * Created by wcg on 2017/4/12.
// */
//@RestController
//@RequestMapping("/lepay/storedPay")
//public class StoredPayController {
//
//  @Inject
//  private StoredPayService storedPayService;
//
//  /**
//   *
//   * @param ext
//   * @param request
//   * @return 纯储值支付
//   */
//  @RequestMapping(value = "/wxpay/payByScoreD")
//  public
//  @ResponseBody
//  LejiaResult payByScoreD(@RequestParam String ext,
//                          HttpServletRequest request) {
//    String result = Des.strDec(ext, "lepluslife", null, null);
//    String[] strs = result.split(" ");
//    try {
////      storedPayService.paidByStored();
//      return LejiaResult.build(200, "", null);
//    } catch (Exception e) {
//      return LejiaResult.build(500, "出现未知错误,请联系管理员");
//    }
//  }
//
//  /**
//   *
//   * @param ext
//   * @param request
//   * @return 储值现金混用支付
//   */
//  @RequestMapping(value = "/wxpay/payByScoreDAndCash")
//  public
//  @ResponseBody
//  LejiaResult payByScoreDAndCash(@RequestParam String ext,
//                                 HttpServletRequest request) {
//    String result = Des.strDec(ext, "lepluslife", null, null);
//    String[] strs = result.split(" ");
//    try {
////      storedPayService.paidByStored();
//      return LejiaResult.build(200, "", null);
//    } catch (Exception e) {
//      return LejiaResult.build(500, "出现未知错误,请联系管理员");
//    }
//  }
//
//
//  /**
//   *
//   * @param ext
//   * @param request
//   * @return 储值红包混用支付
//   */
//  @RequestMapping(value = "/wxpay/payByScoreDAndScoreA")
//  public
//  @ResponseBody
//  LejiaResult payByScoreDAndScoreA(@RequestParam String ext,
//                                 HttpServletRequest request) {
//    String result = Des.strDec(ext, "lepluslife", null, null);
//    String[] strs = result.split(" ");
//    try {
////      storedPayService.paidByStored();
//      return LejiaResult.build(200, "", null);
//    } catch (Exception e) {
//      return LejiaResult.build(500, "出现未知错误,请联系管理员");
//    }
//  }
//
//}
