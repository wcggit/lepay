package com.jifenke.lepluslive.fuyou.service;

import com.fuiou.mpay.encrypt.RSAUtils;
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
   * 富友封装支付参数   16/11/15
   */
  public Map<String, String> buildParams(HttpServletRequest request, String openid,
                                         ScanCodeOrder order)
      throws Exception {

//    String mchnt_id = "0002230F0370520"; //商户号
//    String mchnt_id = "0002230F0336622"; //商户号
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    SortedMap<String, Object> params = new TreeMap<>();
    params.put("customernumber", ""); //主账号商户编号
    params.put("requestid", order.getOrderSid()); //订单号
    params.put("amount", order.getTruePay()/100.0); //总价 单位元
    params.put("productdesc",  order.getMerchant().getName() + "消费");
    params.put("callbackurl", Constants.WEI_XIN_ROOT_URL + "/pay/wxpay/afterPay");//商品详情, 商品名称明细
    params.put("payproducttype", "WECHATU");//
    params.put("ins_cd", Constants.FUYOU_INS_CD);//机构号
    params.put("limit_pay", ""); //限制支付,no_credit:不能使用信用卡
    params.put("mchnt_cd", order.getScanCodeOrderExt().getMerchantNum());//商户号
    params.put("mchnt_order_no", order.getOrderSid());
    params.put("notify_url", Constants.WEI_XIN_ROOT_URL + "/pay/wxpay/afterPay");
    params.put("openid", "");//用户标识
    params.put("order_amt", order.getTruePay());//总金额, 订单总金额，单位为分
    params.put("product_id", "");//商品标识
    params.put("random_str", MvUtil.getRandomStr(32));//随机字符串
    params.put("sub_appid", Constants.APPID); //子商户公众号id, trade_type为JSAPI时必传
    params.put("sub_openid", openid);//子商户用户标识，trade_type为JSAPI时必传
    params.put("term_id", MvUtil.getRandomNumber(8));//终端号, 随机八位
    params.put("term_ip", getIpAddr(request));//终端IP
    params.put("trade_type", "JSAPI");//JSAPI--公众号支付、APP--app支付
    params.put("txn_begin_ts", sdf.format(order.getCreatedDate())); //交易起始时间,格式为yyyyMMddHHmmss
    params.put("version", "1.0");

    String srcSign = MapUtil.mapBlankJoin(params, false, false);

    String sign = RSAUtils.sign(srcSign.getBytes("GBK"), Constants.FUYOU_PRI_KEY);
    System.out.println(sign);
    params.put("sign", sign);
    System.out.println(params.toString());
    StringBuffer paramBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?><xml>");
    MapUtil.mapToXML(params, paramBuffer);
    paramBuffer.append("</xml>");
    System.out.println(paramBuffer.toString());
    try {
      Map<String, String> content = new HashMap<>();
      content.put("req", URLEncoder.encode(paramBuffer.toString(), "GBK"));
      String result =
          HttpClientUtil.post(Constants.FUYOU_PAY_URL, content, "GBK");
      result = new URLDecoder().decode(result, "GBK");
      System.out.println(result);
      return MapUtil.xmlStringToMap(result);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
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


}
