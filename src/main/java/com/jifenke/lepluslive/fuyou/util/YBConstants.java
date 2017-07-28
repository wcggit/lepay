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

  //子商户注册接口
  public static final String REGISTER_URL = PREFIX_URL + "register";

  //账户信息修改接口
  public static final String MODIFY_REQUEST_URL = PREFIX_URL + "modifyRequest";

  //账户信息修改查询接口
  public static final String QUERY_MODIFY_REQUEST_URL = PREFIX_URL + "queryModifyRequest";

  //后台接受修改成功回调通知地址 todo:域名上线需修改
  public static final String
      MODIFY_CALLBACK_URL =
      "http://www.tiegancrm.com/manage/ledger/modifyCallBack";

  // 分账方资质上传接口
  public static final String
      UPLOAD_LEDGER_QUALIFICATIONS_URL =
      PREFIX_URL + "uploadLedgerQualifications";

  //转账接口
  public static final String TRANSFER_URL = PREFIX_URL + "transfer";

  //转账查询接口
  public static final String TRANSFER_QUERY_URL = PREFIX_URL + "transferQuery";

  //余额查询接口
  public static final String QUERY_BALANCE_URL = PREFIX_URL + "queryBalance";

  //结算结果查询接口
  public static final String QUERY_SETTLEMENT_URL = PREFIX_URL + "querySettlement";

  //对账文件下载接口
  public static final String DOWNLOAD_ORDER_DOCUMENT_URL = PREFIX_URL + "downloadOrderDocument";

  //分账方审核结果查询
  public static final String QUERY_CHECK_RECORD_URL = PREFIX_URL + "queryCheckRecord";

}
