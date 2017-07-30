package com.jifenke.lepluslive.fuyou.util;

import java.math.BigDecimal;

/**
 * 易宝相关全局参数
 * Created by zhangwen on 2017/7/12.
 */
public final class YBConstants {

  public static final BigDecimal WX_WEB_RATE = new BigDecimal(0.003);//微信WEB公众号成本费率

  public static final BigDecimal WX_APP_RATE = new BigDecimal(0.003);//微信APP成本费率

  public static final BigDecimal ALI_WEB_RATE = new BigDecimal(0.003);//支付宝WEB成本费率

  public static final BigDecimal ALI_APP_RATE = new BigDecimal(0.003);//支付宝APP成本费率

  public static final int SETTLEMENT_COST = 150;  //单笔结算费用

  //接口地址统一前缀
  private static final String PREFIX_URL = "https://o2o.yeepay.com/zgt-api/api/";

  //转账接口
  public static final String TRANSFER_URL = PREFIX_URL + "transfer";

  //转账查询接口
  public static final String TRANSFER_QUERY_URL = PREFIX_URL + "transferQuery";

  //支付接口
  public static final String PAY_URL = PREFIX_URL + "pay";

  //订单查询接口
  public static final String QUERY_ORDER_URL = PREFIX_URL + "queryOrder";

}
