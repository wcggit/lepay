package com.jifenke.lepluslive.pospay.controller;

import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.RSAUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.service.UnionPosOrderLogService;
import com.jifenke.lepluslive.order.service.UnionPosOrderService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;

/**
 * 银联商务支付相关 Created by zhangwen on 16/10/14.
 */
@RestController
@RequestMapping("/lepay")
public class UnionPosPayController {

  @Inject
  private UnionPosOrderLogService unionPosOrderLogService;

  @Inject
  private UnionPosOrderService unionOrderService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private LeJiaUserService userService;

  /**
   * POS机全积分支付 16/10/14
   */
  @ApiOperation(value = "POS机全积分支付")
  @RequestMapping(value = "/pospay/u_pay/pure", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult pure(@RequestParam Long merchantId, @RequestParam String account,
                   @RequestParam Long userId,
                   @RequestParam Long money) {
    Merchant m = merchantService.findMerchantById(merchantId);
    LeJiaUser u = userService.findUserById(userId);
    Map result = unionOrderService.pureScorePay(m, account, u, money);
    if (!"200".equals("" + result.get("status"))) {
      return LejiaResult
          .build(Integer.valueOf("" + result.get("status")), "" + result.get("msg"));
    }
    return LejiaResult.ok(result.get("data"));
  }


  /**
   * POS机混合支付成功后通知 16/10/19
   */
  @ApiOperation(value = "POS机混合支付成功后通知")
  @RequestMapping(value = "/pospay/u_pay/success", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult paySuccess(@RequestParam String orderSid, @RequestParam Long merchantId,
                         @RequestParam String account, @RequestParam String data) {
    Map
        result =
        unionOrderService.PaySuccess(orderSid, account, merchantId, data);
    if (!"200".equals("" + result.get("status"))) {
      return LejiaResult
          .build(Integer.valueOf("" + result.get("status")), "" + result.get("msg"));
    }
    return LejiaResult.ok();
  }

  /**
   * 4.1查询银行卡绑定活动接口（002000）
   */
  @RequestMapping(value = "/union_pay/search", method = RequestMethod.POST)
  public Map unionPosOrderSearch(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);

    System.out.println("===============4.1查询银行卡绑定活动接口（002000）==================");
    System.out.println("请求数据==================" + parameters.toString());

    String sign = String.valueOf(parameters.get("sign"));
//    String enc_card_no = parameters.get("enc_card_no").toString();
//    String decode = RSAUtil.decode(enc_card_no); //卡号
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    System.out.println("请求验签字符串===========" + requestStr);
    TreeMap<String, Object> returnMap = new TreeMap<>();
    returnMap.put("msg_type", "00");
    returnMap.put("msg_txn_code", "002000");
    returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
    returnMap.put("msg_flg", "1");
    returnMap.put("msg_sender", Constants.MSG_SENDER);//分配的渠道号
    returnMap.put("msg_time", parameters.get("msg_time"));
    returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
    returnMap.put("msg_ver", "0.1");
    if (RSAUtil.testSign(requestStr, sign)) { //验签
      //查找订单
      String orderSid = String.valueOf(parameters.get("order_no"));
      UnionPosOrder order = unionOrderService.findByOrderSid(orderSid);
      String resultCode = "0000";
      String resultDesc = "SUCCESS";
      if (order != null) {
        if (order.getRebateWay() == 0 || order.getRebateWay() == 2) {//普通费率没有活动
          resultCode = "1001";
          resultDesc = "no activity";
        }
      } else {
        resultCode = "9999";
        resultDesc = "order not exist";
      }
      //保存查询日志
      unionPosOrderLogService.saveLogBySearch(parameters, 1);
      // Long truePay = order.getTruePay();
      returnMap.put("msg_rsp_code", resultCode);
      returnMap.put("msg_rsp_desc", resultDesc);
      returnMap.put("orig_amt", parameters.get("amount")); //原始金额
      returnMap.put("discount_amt", 0); //折扣金额
      returnMap.put("pay_amt", parameters.get("amount")); //支付金额
      returnMap.put("event_no", Constants.EVENT_NO); //活动号
    } else {
      returnMap.put("msg_rsp_code", 9996);
      returnMap.put("msg_rsp_desc", "验签失败");
    }
    returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
    System.out.println("4.1 返回数据====================" + returnMap.toString());
    return returnMap;
  }

  /**
   * 4.2销账交易接口（002100）
   */
  @RequestMapping(value = "/union_pay/afterPay", method = RequestMethod.POST)
  public Map afterPay(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);
    System.out.println("===============4.2销账交易接口（002100）==================");
    System.out.println("请求数据==================" + parameters.toString());
    String sign = String.valueOf(parameters.get("sign"));
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    System.out.println("请求验签字符串===========" + requestStr);
    TreeMap<String, Object> returnMap = new TreeMap<>();
    returnMap.put("msg_type", "00");
    returnMap.put("msg_txn_code", "002100");
    returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
    returnMap.put("msg_flg", "1");
    returnMap.put("msg_sender", Constants.MSG_SENDER);//分配的渠道号
    returnMap.put("msg_time", parameters.get("msg_time"));
    returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
    returnMap.put("msg_ver", "0.1");
    if (RSAUtil.testSign(requestStr, sign)) { //验签
      //查找订单
      String orderSid = String.valueOf(parameters.get("order_no"));
      UnionPosOrder order = unionOrderService.findByOrderSid(orderSid);
      //对订单进行处理
      String resultCode = "0000";
      try {
        unionOrderService.paySuccess(order, parameters);
      } catch (Exception e) {
        e.printStackTrace();
        resultCode = "9999";
      }
      //保存销账日志
      unionPosOrderLogService.saveLogAfterPay(parameters, 2);
      Long truePay = order.getTruePay();
      Long commission = order.getYsCommission();

      returnMap.put("msg_rsp_code", resultCode);
      returnMap.put("msg_rsp_desc", "SUCCESS");
      returnMap.put("orig_amt", truePay); //原始金额
      returnMap.put("discount_amt", 0); //折扣金额
      returnMap.put("pay_amt", truePay); //支付金额
      returnMap.put("serv_chg", commission); //服务费
      returnMap.put("commission", commission); //佣金
//      returnMap.put("pos_receipt",
//                    "使用" + order.getTrueScore().doubleValue() / 100 + "红包抵扣");
      returnMap.put("ad",
                    "使用" + order.getTrueScore().doubleValue() / 100 + "元红包"); //POS优惠 打印在小票上
      returnMap.put("event_no", Constants.EVENT_NO); //活动号
    } else {
      returnMap.put("msg_rsp_code", 9996);
      returnMap.put("msg_rsp_desc", "验签失败");
    }
    returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
    System.out.println("4.2 返回数据====================" + returnMap.toString());
//    try {
//      Thread.sleep(16000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    return returnMap;
  }

  /**
   * 4.3销账冲正通知接口（002101）
   */
  @RequestMapping(value = "/union_pay/reverse", method = RequestMethod.POST)
  public Map reverse(HttpServletRequest request) {
    System.out.println("===============4.3销账冲正通知接口（002101）==================");
    TreeMap parameters = getParametersFromRequest(request);
    System.out.println("请求数据==================" + parameters.toString());
    String sign = String.valueOf(parameters.get("sign"));
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    TreeMap<String, Object> returnMap = new TreeMap<>();
    returnMap.put("msg_type", "00");
    returnMap.put("msg_txn_code", "002101");
    returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
    returnMap.put("msg_flg", "1");
    returnMap.put("msg_sender", Constants.MSG_SENDER);//分配的渠道号
    returnMap.put("msg_time", parameters.get("msg_time"));
    returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
    returnMap.put("msg_ver", "0.1");
    if (RSAUtil.testSign(requestStr, sign)) { //验签
      //查找订单
      String orderCode = String.valueOf(parameters.get("orig_req_serial_no"));
      UnionPosOrder order = unionOrderService.findByOrderCode(orderCode);
      //对订单进行处理
      String resultCode = "0000";
      try {
        unionOrderService.payReverse(order, 3);
      } catch (Exception e) {
        e.printStackTrace();
        resultCode = "9999";
      }
      //保存销账冲正日志
      unionPosOrderLogService.saveLogReverse(parameters, 3);
      returnMap.put("msg_rsp_code", resultCode);
      returnMap.put("msg_rsp_desc", "SUCCESS");
    } else {
      returnMap.put("msg_rsp_code", 9996);
      returnMap.put("msg_rsp_desc", "验签失败");
    }
    returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
    System.out.println("4.3 返回数据====================" + returnMap.toString());
    return returnMap;
  }

  /**
   * 4.4销账撤销通知接口（002102）（只支持撤销当天，未清算交易）
   */
  @RequestMapping(value = "/union_pay/cancel", method = RequestMethod.POST)
  public Map cancel(HttpServletRequest request) {
    System.out.println("===============4.4销账撤销通知接口（002102）（只支持撤销当天，未清算交易）==================");
    TreeMap parameters = getParametersFromRequest(request);
    String sign = String.valueOf(parameters.get("sign"));
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    TreeMap<String, Object> returnMap = new TreeMap<>();
    returnMap.put("msg_type", "00");
    returnMap.put("msg_txn_code", "002102");
    returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
    returnMap.put("msg_flg", "1");
    returnMap.put("msg_sender", Constants.MSG_SENDER);//分配的渠道号
    returnMap.put("msg_time", parameters.get("msg_time"));
    returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
    returnMap.put("msg_ver", "0.1");
    if (RSAUtil.testSign(requestStr, sign)) { //验签
      //查找订单
      String orderCode = String.valueOf(parameters.get("orig_req_serial_no"));
      UnionPosOrder order = unionOrderService.findByOrderCode(orderCode);
      //对订单进行处理
      String resultCode = "0000";
      try {
        unionOrderService.payReverse(order, 4);
      } catch (Exception e) {
        e.printStackTrace();
        resultCode = "9999";
      }
      //保存销账冲正日志
      unionPosOrderLogService.saveLogReverse(parameters, 4);

      returnMap.put("msg_rsp_code", resultCode);
      returnMap.put("msg_rsp_desc", "SUCCESS");
    } else {
      returnMap.put("msg_rsp_code", 9996);
      returnMap.put("msg_rsp_desc", "验签失败");
    }
    returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
    System.out.println("4.4 返回数据====================" + returnMap.toString());
    return returnMap;
  }


  private String getOriginStr(TreeMap parameters) {
    StringBuilder sb = new StringBuilder();
    Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
    Iterator it = es.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String k = (String) entry.getKey();
      Object v = entry.getValue();
//      if (null != v && !"".equals(v)
//          && !"sign".equals(k) && !"key".equals(k)) {
      sb.append(k).append("=").append(v).append("&");
//      }
    }
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public TreeMap getParametersFromRequest(HttpServletRequest request) {
    Map<String, String[]> params = request.getParameterMap();
    TreeMap returnMap = new TreeMap();
    for (String key : params.keySet()) {
      String[] values = params.get(key);
      for (int i = 0; i < values.length; i++) {
        returnMap.put(key, values[i]);
      }
    }
    return returnMap;
  }

  //  /**
//   * POS机混合支付掉支付插件前创建订单 老版本，待删除 16/10/19
//   */
//  @ApiOperation(value = "POS机混合支付掉支付插件前创建订单")
//  @RequestMapping(value = "/pospay/u_pay/create", method = RequestMethod.POST)
//  public
//  @ResponseBody
//  LejiaResult createOrder(@RequestParam Long merchantId, @RequestParam String account,
//                          @RequestParam Long userId,
//                          @RequestParam Long totalPrice, @RequestParam Long truePrice,
//                          @RequestParam Long trueScore) {
//    Merchant m = merchantService.findMerchantById(merchantId);
//    LeJiaUser u = userService.findUserById(userId);
//
//    Map result = unionOrderService.createOrder(m, account, u, totalPrice, truePrice, trueScore);
//    if (!"200".equals("" + result.get("status"))) {
//      return LejiaResult
//          .build(Integer.valueOf("" + result.get("status")), "" + result.get("msg"));
//    }
//    return LejiaResult.ok(result.get("data"));
//  }
//
//  @RequestMapping(value = "/pospay/union_pay/write_off", method = RequestMethod.POST)
//  public Map unionPosOrderWriteOff(HttpServletRequest request) {
//    TreeMap parameters = getParametersFromRequest(request);
//    unionPosOrderLogService
//        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
//                           parameters.get("req_serial_no").toString());
//    String sign = parameters.get("sign").toString();
//    String enc_card_no = parameters.get("enc_card_no").toString();
//    String decode = RSAUtil.decode(enc_card_no);
//    parameters.remove("sign");
//    String requestStr = getOriginStr(parameters);
//    if (RSAUtil.testSign(requestStr, sign)) {
//      TreeMap returnMap = new TreeMap();
//      returnMap.put("msg_type", parameters.get("msg_type"));
//      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
//      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
//      returnMap.put("msg_flg", "1");
//      returnMap.put("msg_sender", parameters.get("msg_sender"));
//      returnMap.put("msg_time", parameters.get("msg_time"));
//      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
//      returnMap.put("msg_rsp_code", "0000");
//      returnMap.put("msg_rsp_desc", "成功");
//      returnMap.put("msg_ver", "0.1");
//      returnMap.put("orig_amt", "111");
//      returnMap.put("discount_amt", "11");
//      returnMap.put("pay_amt", "100");
//      returnMap.put("serv_chg", "20");
//      returnMap.put("commission", "8");
//      returnMap.put("event_no", "000001");
//      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
//      return returnMap;
//    }
//    return null;
//  }
//
//
//  @RequestMapping(value = "/pospay/union_pay/write_off_correction", method = RequestMethod.POST)
//  public Map unionPosOrderWriteOffCorrection(HttpServletRequest request) {
//    TreeMap parameters = getParametersFromRequest(request);
//    unionPosOrderLogService
//        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
//                           parameters.get("req_serial_no").toString());
//    String sign = parameters.get("sign").toString();
//    parameters.remove("sign");
//    String requestStr = getOriginStr(parameters);
//    if (RSAUtil.testSign(requestStr, sign)) {
//      TreeMap returnMap = new TreeMap();
//      returnMap.put("msg_type", parameters.get("msg_type"));
//      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
//      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
//      returnMap.put("msg_flg", "1");
//      returnMap.put("msg_sender", parameters.get("msg_sender"));
//      returnMap.put("msg_time", parameters.get("msg_time"));
//      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
//      returnMap.put("msg_rsp_code", "0000");
//      returnMap.put("msg_rsp_desc", "成功");
//      returnMap.put("msg_ver", "0.1");
//      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
//      return returnMap;
//    }
//    return null;
//  }
//
//  @RequestMapping(value = "/pospay/union_pay/write_off_revoke", method = RequestMethod.POST)
//  public Map unionPosOrderWriteOffRevoke(HttpServletRequest request) {
//    TreeMap parameters = getParametersFromRequest(request);
//    unionPosOrderLogService
//        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
//                           parameters.get("req_serial_no").toString());
//    String sign = parameters.get("sign").toString();
//    parameters.remove("sign");
//    String requestStr = getOriginStr(parameters);
//    if (RSAUtil.testSign(requestStr, sign)) {
//      TreeMap returnMap = new TreeMap();
//      returnMap.put("msg_type", parameters.get("msg_type"));
//      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
//      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
//      returnMap.put("msg_flg", "1");
//      returnMap.put("msg_sender", parameters.get("msg_sender"));
//      returnMap.put("msg_time", parameters.get("msg_time"));
//      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
//      returnMap.put("msg_rsp_code", "0000");
//      returnMap.put("msg_rsp_desc", "成功");
//      returnMap.put("msg_ver", "0.1");
//      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
//      return returnMap;
//    }
//    return null;
//  }
}
