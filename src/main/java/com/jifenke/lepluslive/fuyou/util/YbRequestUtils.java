package com.jifenke.lepluslive.fuyou.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 易宝请求工具类（参数生成&发送请求）
 * Created by zhangwen on 2017/7/14.
 */
public class YbRequestUtils {


  /**
   * 转账  2017/7/19
   *
   * @param ledgerNo 易宝的子商户号
   * @param amount   转账金额（注意：此时单位为分，调用接口时需/100转换为元）
   * @param orderSid 转账单号(请求号)
   * @return map　转账结果
   */
  public static Map<String, String> transfer(String ledgerNo, Long amount,
                                             String orderSid) {

    //请求加密参数
    Map<String, String> dataMap = getCommonDataMap();
    dataMap.put("requestid", orderSid);
    dataMap.put("ledgerno", ledgerNo);
    dataMap.put("amount", "" + amount / 100.0);

    String data = ZGTUtils.buildData(dataMap, ZGTUtils.TRANSFERAPI_REQUEST_HMAC_ORDER);
    Map<String, String> map = ZGTUtils.httpPost(YBConstants.TRANSFER_URL, data);
    return callBack(map);
  }

  /**
   * 转账查询  2017/7/19
   *
   * @param requestId 转账请求号
   * @return map　转账结果
   */
  public static Map<String, String> queryTransfer(String requestId) {

    //请求加密参数
    Map<String, String> dataMap = getCommonDataMap();
    dataMap.put("requestid", requestId);

    String data = ZGTUtils.buildData(dataMap, ZGTUtils.TRANSFERQUERYAPI_REQUEST_HMAC_ORDER);
    Map<String, String> map = ZGTUtils.httpPost(YBConstants.TRANSFER_QUERY_URL, data);
    return callBack(map);
  }

  /**
   * 请求响应中解析返回结果  2017/7/16
   *
   * @param stringMap 响应Map
   * @return Map
   */
  public static Map<String, String> callBack(Map<String, String> stringMap) {
    System.out.println("易宝的同步响应：" + stringMap);

    if (stringMap.containsKey("code")) {
      return stringMap;
    }
    Map<String, String> responseDataMap = ZGTUtils.decryptData(stringMap.get("data"));
    System.out.println("data解密后明文：" + responseDataMap);
    return responseDataMap;
  }

  /**
   * 获取公共dataMAP  2017/7/14
   */
  private static Map<String, String> getCommonDataMap() {
    Map<String, String> dataMap = new HashMap<>();
    dataMap.put("customernumber", ZGTUtils.getCustomernumber());
    return dataMap;
  }

}
