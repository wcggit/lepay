package com.jifenke.lepluslive.pospay.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.RSAUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.service.UnionPosOrderLogService;
import com.jifenke.lepluslive.order.service.UnionPosOrderService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;

/**
 * Created by wcg on 16/8/8.
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
   * POS机混合支付掉支付插件前创建订单 16/10/19
   */
  @ApiOperation(value = "POS机混合支付掉支付插件前创建订单")
  @RequestMapping(value = "/pospay/u_pay/create", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult createOrder(@RequestParam Long merchantId, @RequestParam String account,
                          @RequestParam Long userId,
                          @RequestParam Long totalPrice, @RequestParam Long truePrice,
                          @RequestParam Long trueScore) {
    Merchant m = merchantService.findMerchantById(merchantId);
    LeJiaUser u = userService.findUserById(userId);

    Map result = unionOrderService.createOrder(m, account, u, totalPrice, truePrice, trueScore);
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
  LejiaResult paySuccess(@RequestParam Long orderId, @RequestParam Long merchantId,
                         @RequestParam Long userId,
                         @RequestParam String account, @RequestParam String data) {
    Merchant m = merchantService.findMerchantById(merchantId);
    LeJiaUser u = null;
    if (userId != null && userId != 0) {
      u = userService.findUserById(userId);
    }
    Map
        result =
        unionOrderService.PaySuccess(orderId, account, m, u, data);
    if (!"200".equals("" + result.get("status"))) {
      return LejiaResult
          .build(Integer.valueOf("" + result.get("status")), "" + result.get("msg"));
    }
    return LejiaResult.ok();
  }

  @RequestMapping(value = "/pospay/union_pay/search", method = RequestMethod.POST)
  public Map unionPosOrderSearch(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);
    unionPosOrderLogService
        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
                           parameters.get("req_serial_no").toString());
    String sign = parameters.get("sign").toString();
    String enc_card_no = parameters.get("enc_card_no").toString();
    String decode = RSAUtil.decode(enc_card_no);
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    if (RSAUtil.testSign(requestStr, sign)) {
      TreeMap returnMap = new TreeMap();
      returnMap.put("msg_type", parameters.get("msg_type"));
      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
      returnMap.put("msg_flg", "1");
      returnMap.put("msg_sender", parameters.get("msg_sender"));
      returnMap.put("msg_time", parameters.get("msg_time"));
      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
      returnMap.put("msg_rsp_code", "0000");
      returnMap.put("msg_rsp_desc", "成功");
      returnMap.put("msg_ver", "0.1");
      returnMap.put("point_cost", "11");
      returnMap.put("actual_amt", "11");
      returnMap.put("point_amt", "11");
      returnMap.put("pos_monitor", "");
      returnMap.put("pos_confirm_mode", "1");
      returnMap.put("discount_amt", "0");
      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
      return returnMap;
    }
    return null;
  }

  @RequestMapping(value = "/pospay/union_pay/write_off", method = RequestMethod.POST)
  public Map unionPosOrderWriteOff(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);
    unionPosOrderLogService
        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
                           parameters.get("req_serial_no").toString());
    String sign = parameters.get("sign").toString();
    String enc_card_no = parameters.get("enc_card_no").toString();
    String decode = RSAUtil.decode(enc_card_no);
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    if (RSAUtil.testSign(requestStr, sign)) {
      TreeMap returnMap = new TreeMap();
      returnMap.put("msg_type", parameters.get("msg_type"));
      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
      returnMap.put("msg_flg", "1");
      returnMap.put("msg_sender", parameters.get("msg_sender"));
      returnMap.put("msg_time", parameters.get("msg_time"));
      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
      returnMap.put("msg_rsp_code", "0000");
      returnMap.put("msg_rsp_desc", "成功");
      returnMap.put("msg_ver", "0.1");
      returnMap.put("orig_amt", "111");
      returnMap.put("discount_amt", "11");
      returnMap.put("pay_amt", "100");
      returnMap.put("serv_chg", "20");
      returnMap.put("commission", "8");
      returnMap.put("event_no", "000001");
      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
      return returnMap;
    }
    return null;
  }


  @RequestMapping(value = "/pospay/union_pay/write_off_correction", method = RequestMethod.POST)
  public Map unionPosOrderWriteOffCorrection(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);
    unionPosOrderLogService
        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
                           parameters.get("req_serial_no").toString());
    String sign = parameters.get("sign").toString();
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    if (RSAUtil.testSign(requestStr, sign)) {
      TreeMap returnMap = new TreeMap();
      returnMap.put("msg_type", parameters.get("msg_type"));
      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
      returnMap.put("msg_flg", "1");
      returnMap.put("msg_sender", parameters.get("msg_sender"));
      returnMap.put("msg_time", parameters.get("msg_time"));
      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
      returnMap.put("msg_rsp_code", "0000");
      returnMap.put("msg_rsp_desc", "成功");
      returnMap.put("msg_ver", "0.1");
      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
      return returnMap;
    }
    return null;
  }

  @RequestMapping(value = "/pospay/union_pay/write_off_revoke", method = RequestMethod.POST)
  public Map unionPosOrderWriteOffRevoke(HttpServletRequest request) {
    TreeMap parameters = getParametersFromRequest(request);
    unionPosOrderLogService
        .createPosOrderLog(parameters.get("msg_sys_sn").toString(), request.getQueryString(), 1,
                           parameters.get("req_serial_no").toString());
    String sign = parameters.get("sign").toString();
    parameters.remove("sign");
    String requestStr = getOriginStr(parameters);
    if (RSAUtil.testSign(requestStr, sign)) {
      TreeMap returnMap = new TreeMap();
      returnMap.put("msg_type", parameters.get("msg_type"));
      returnMap.put("msg_txn_code", parameters.get("msg_txn_code"));
      returnMap.put("msg_crrltn_id", parameters.get("msg_crrltn_id"));
      returnMap.put("msg_flg", "1");
      returnMap.put("msg_sender", parameters.get("msg_sender"));
      returnMap.put("msg_time", parameters.get("msg_time"));
      returnMap.put("msg_sys_sn", parameters.get("msg_sys_sn"));
      returnMap.put("msg_rsp_code", "0000");
      returnMap.put("msg_rsp_desc", "成功");
      returnMap.put("msg_ver", "0.1");
      returnMap.put("sign", RSAUtil.sign(getOriginStr(returnMap)));
      return returnMap;
    }
    return null;
  }

  public String getOriginStr(TreeMap parameters) {
    StringBuffer sb = new StringBuffer();
    Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
    Iterator it = es.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String k = (String) entry.getKey();
      String v = (String) entry.getValue();
      if (null != v && !"".equals(v)
          && !"sign".equals(k) && !"key".equals(k)) {
        sb.append(k + "=" + v + "&");
      }
    }
    sb.deleteCharAt(sb.length() - 1);
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

}
