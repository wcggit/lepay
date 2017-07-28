package com.jifenke.lepluslive.fuyou.service;

import com.fuiou.mpay.encrypt.RSAUtils;
import com.jifenke.lepluslive.fuyou.util.YBConstants;
import com.jifenke.lepluslive.fuyou.util.ZGTUtils;
import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.HttpClientUtil;
import com.jifenke.lepluslive.global.util.MapUtil;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;

import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wcg on 2017/7/19.
 */
@Service
public class YeepayService {

  /**
   * 封装支付参数   16/11/15
   */
  public Map<String, String> buildParams(HttpServletRequest request, String openid,
                                         ScanCodeOrder order)
      throws Exception {

//    String mchnt_id = "0002230F0370520"; //商户号
//    String mchnt_id = "0002230F0336622"; //商户号
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    SortedMap<String, String> params = new TreeMap<>();
    params.put("customernumber", ZGTUtils.getCustomernumber()); //主账号商户编号
    params.put("requestid", order.getOrderSid()); //订单号
    params.put("amount", order.getTruePay() / 100.0 + ""); //总价 单位元
    params.put("productname", "测试支付-名称-" + order.getOrderSid());
    params.put("productdesc", "测试支付-描述-" + order.getOrderSid());
    params.put("callbackurl", Constants.WEI_XIN_ROOT_URL + "/pay/yeepay/afterPay");
    params.put("webcallbackurl", Constants.WEI_XIN_ROOT_URL + "/pay/yeepay/paySuccess");
    params.put("payproducttype", "ONEKEY");//
    params.put("openid", openid);//
    params.put("appid", Constants.APPID);//
    params.put("ip", getIpAddr(request));//
    params.put("directcode", "WAP_WECHATG");//
    String data = ZGTUtils.buildData(params, ZGTUtils.PAYAPI_REQUEST_HMAC_ORDER);
    Map<String, String> map = ZGTUtils.httpPost("https://o2o.yeepay.com/zgt-api/api/pay", data);

    try {

      return callBack(map);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  private static Map<String, String> callBack(Map<String, String> stringMap) {
    System.out.println("易宝的同步响应：" + stringMap);

    if (stringMap.containsKey("code")) {
      return stringMap;
    }
    Map<String, String> responseDataMap = ZGTUtils.decryptData(stringMap.get("data"));
    System.out.println("data解密后明文：" + responseDataMap);
    return responseDataMap;
  }

  /**
   * @return ip地址
   */
  private String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip != null && ip.length() > 15) {
      ip = ip.split(",")[0];
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
  }

  /**
   * 订单状态查询接口
   */
  public Map<String, String> checkOrderState(ScanCodeOrder order) {
    SortedMap<String, String> params = new TreeMap<>();
    params.put("customernumber", ZGTUtils.getCustomernumber()); //主账号商户编号
    params.put("requestid", order.getOrderSid()); //订单号
    String data = ZGTUtils.buildData(params, ZGTUtils.QUERYORDERAPI_REQUEST_HMAC_ORDER);
    Map<String, String> map = ZGTUtils.httpPost("https://o2o.yeepay.com/zgt-api/api/queryOrder", data);
    return callBack(map);
  }
}
