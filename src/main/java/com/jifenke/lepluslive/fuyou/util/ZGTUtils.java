package com.jifenke.lepluslive.fuyou.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: yingjie.wang
 * @since : 2015-09-30 20:15
 */

public class ZGTUtils {

  //配置文件路径
  public static final String CONFIG_FILE_PATH = "yibao/merchantInfo";

  public static final String CHARSET = "UTF-8";

  //各个api的名字，用于从配置文件中读取数据
  public static final String PAYAPI_NAME = "PayApi";
  public static final String QUERYORDERAPI_NAME = "QueryOrderApi";
  public static final String REFUNDAPI_NAME = "RefundApi";
  public static final String QUERYREFUNDAPI_NAME = "QueryRefundApi";
  public static final String SETTLECONFIRMAPI_NAME = "SettleConfirmApi";
  public static final String QUERYBINDCARDSAPI_NAME = "QueryBindCardsApi";
  public static final String UNBINDCARDAPI_NAME = "UnbindCardApi";

  public static final String REGISTERAPI_NAME = "RegisterApi";
  public static final String UPLOADAPI_NAME = "uploadApi";
  public static final String MODIFYREQUESTAPI_NAME = "ModifyRequestApi";
  public static final String QUERYMODIFYREQUESTAPI_NAME = "QueryModifyRequestApi";
  public static final String TRANSFERAPI_NAME = "TransferApi";
  public static final String TRANSFERQUERYAPI_NAME = "TransferQueryApi";
  public static final String DIVIDEAPI_NAME = "DivideApi";
  public static final String QUERYDIVIDEAPI_NAME = "QueryDivideApi";
  public static final String QUERYBALANCEAPI_NAME = "QueryBalanceApi";
  public static final String QUERYSETTLEMENTAPI_NAME = "QuerySettlementApi";
  public static final String QUERYRECORDAPI_NAME = "QueryCheckRecordApi";
  public static final String IDCARDAUTHAPI_NAME = "IDCardAuthApi";

  //4.1 订单支付接口请求及响应参数的hmac签名顺序
  public static final String[]
      PAYAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "amount", "assure", "productname", "productcat",
       "productdesc", "divideinfo", "callbackurl", "webcallbackurl", "bankid", "period", "memo"};
  public static final String[]
      PAYAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "externalid", "amount", "payurl"};

  //4.2 订单查询接口请求及响应参数的hmac签名顺序
  public static final String[] QUERYORDERAPI_REQUEST_HMAC_ORDER = {"customernumber", "requestid"};
  public static final String[]
      QUERYORDERAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "externalid", "amount", "productname", "productcat",
       "productdesc", "status", "ordertype", "busitype", "orderdate", "createdate", "bankid"};

  //4.3 订单退款接口请求及响应参数的hmac签名顺序
  public static final String[]
      REFUNDAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "orderrequestid", "amount", "divideinfo", "confirm", "memo"};
  public static final String[]
      REFUNDAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "refundexternalid"};

  //4.4 订单退款查询接口请求及响应参数的hmac签名顺序
  public static final String[]
      QUERYREFUNDAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "orderrequestid", "refundrequestid"};
  public static final String[]
      QUERYREFUNDAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "orderrequestid", "code", "externalid", "refundinfo"};

  //4.5 担保确认接口请求及响应参数的hmac签名顺序
  public static final String[]
      SETTLECONFIRMAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "orderrequestid"};
  public static final String[]
      SETTLECONFIRMAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "orderrequestid", "code"};

  //4.6 查询绑卡列表接口请求及响应参数的hmac签名顺序
  public static final String[] QUERYBINDCARDSAPI_REQUEST_HMAC_ORDER = {"customernumber", "userno"};
  public static final String[]
      QUERYBINDCARDSAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "userno", "code"};

  //4.7 解绑接口请求及响应参数的hmac签名顺序
  public static final String[]
      UNBINDCARDAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "userno", "bindid"};
  public static final String[]
      UNBINDCARDAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "userno", "bindid", "code"};

  //5.1 子账户注册接口请求及响应参数的hmac签名顺序
  public static final String[]
      REGISTERAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "bindmobile", "customertype", "signedname", "linkman",
       "idcard", "businesslicence", "legalperson", "minsettleamount", "riskreserveday",
       "bankaccountnumber", "bankname", "accountname", "bankaccounttype", "bankprovince",
       "bankcity"};
  public static final String[]
      REGISTERAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "ledgerno"};

  //5.3 账户信息修改接口请求及响应参数的hmac签名顺序
  public static final String[]
      MODIFYREQUESTAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "ledgerno", "bankaccountnumber", "bankname", "accountname",
       "bankaccounttype", "bankprovince", "bankcity", "minsettleamount", "riskreserveday",
       "manualsettle", "callbackurl"};
  public static final String[]
      MODIFYREQUESTAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code"};

  //5.4 账户信息修改查询接口请求及响应参数的hmac签名顺序
  public static final String[]
      QUERYMODIFYREQUESTAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid"};
  public static final String[]
      QUERYMODIFYREQUESTAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "status", "desc"};

  //5.5 转账接口请求及响应参数的hmac签名顺序
  public static final String[]
      TRANSFERAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "ledgerno", "amount"};
  public static final String[]
      TRANSFERAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code"};

  //5.6 转账查询接口请求及响应参数的hmac签名顺序
  public static final String[]
      TRANSFERQUERYAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid"};
  public static final String[]
      TRANSFERQUERYAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code", "ledgerno", "amount", "status"};

  //5.7 分账接口请求及响应参数的hmac签名顺序
  public static final String[]
      DIVIDEAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "orderrequestid", "divideinfo"};
  public static final String[]
      DIVIDEAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "code"};

  //5.8 分账查询接口请求及响应参数的hmac签名顺序
  public static final String[]
      QUERYDIVIDEAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "orderrequestid", "dividerequestid", "ledgerno"};
  public static final String[]
      QUERYDIVIDEAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "orderrequestid", "code", "divideinfo"};

  //5.9 余额查询接口请求及响应参数的hmac签名顺序
  public static final String[] QUERYBALANCEAPI_REQUEST_HMAC_ORDER = {"customernumber", "ledgerno"};
  public static final String[]
      QUERYBALANCEAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "code", "balance", "ledgerbalance"};

  //5.10 结算结果查询接口请求及响应参数的hmac签名顺序
  public static final String[]
      QUERYSETTLEMENTAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "ledgerno", "date"};
  public static final String[]
      QUERYSETTLEMENTAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "code", "info"};

  //5.12 分账方审核结果查询接口请求及响应参数的hmac签名顺序
  public static final String[] QUERYRECORDAPI_REQUEST_HMAC_ORDER = {"customernumber", "ledgerno"};
  public static final String[]
      QUERYRECORDAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "ledgerno", "status", "checkdate", "reason"};

  //5.13 企业法人补充身份证认证接口请求及响应参数的hmac签名顺序
  public static final String[]
      IDCARDAUTHAPI_REQUEST_HMAC_ORDER =
      {"customernumber", "requestid", "ledgerno", "idcard"};
  public static final String[]
      IDCARDAUTHAPI_RESPONSE_HMAC_ORDER =
      {"customernumber", "requestid", "ledgerno", "code"};

  //账户信息修改回调的hmac签名顺序
  public static final String[]
      MODIFYREQUESTAPICALLBACK_HMAC_ORDER =
      {"customernumber", "requestid", "code", "status", "desc"};

  //支付接口回调的hmac签名顺序
  public static final String[]
      PAYAPICALLBACK_HMAC_ORDER =
      {"customernumber", "requestid", "code", "notifytype", "externalid", "amount", "cardno"};

  //分账接口回调的hmac签名顺序
  public static final String[]
      DIVIDEAPICALLBACK_HMAC_ORDER =
      {"customernumber", "requestid", "code", "orderrequestid"};

  private static Configuration merchantInfo = null;
  private static String customernumber = "";
  private static String keyForHmac = "";
  private static String keyForAes = "";
  private static String basePath = "";

  static {
    //初始化merchantInfo
    merchantInfo = Configuration.getInstance(CONFIG_FILE_PATH);
    //从配置文件中获取商户编号
    customernumber = merchantInfo.getValue("customernumber");
    //从配置文件中获取商户密钥
    keyForHmac = merchantInfo.getValue("key");
    //aes密钥，为商户密钥的前16位
    keyForAes = keyForHmac.substring(0, 16);
    //资质图片保存路径
    basePath = merchantInfo.getValue("BasePath");
  }

  //生成hmac
  public static String buildHmac(String str, String key) {
    return Digest.hmacSign(str, key);
  }

  //生成hmac
  public static String buildHmac(String[] strArray, String key) {
    StringBuffer stringValue = new StringBuffer();
    for (int i = 0; i < strArray.length; i++) {
      stringValue.append(formatStr(strArray[i]));
    }
    return buildHmac(stringValue.toString(), key);
  }

  //生成hmac
  public static String buildHmac(String str) {
    return buildHmac(str, keyForHmac);
  }

  //生成hmac
  public static String buildHmac(String[] strArray) {
    return buildHmac(strArray, keyForHmac);
  }

  //生成data
  public static String buildData(Map<String, String> params, String key) {
    String jsonStr = JSON.toJSONString(params);
    return AESUtil.encrypt(jsonStr, key);
  }

  //生成data
  public static String buildData(Map<String, String> params) {
    return buildData(params, keyForAes);
  }

  //生成data
  public static String buildData(Map<String, String> params, String[] requestHmacOrder) {

    for (String key : params.keySet()) {
      params.put(key, formatStr(params.get(key)));
    }

    //对于null的value，置成""
    //添加商户编号
    params.put("customernumber", customernumber);

    //生成hmac
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < requestHmacOrder.length; i++) {
      buffer.append(formatStr(params.get(requestHmacOrder[i])));
    }
    String hmac = buildHmac(buffer.toString());

    //放入刚刚生成的hmac
    params.put("hmac", hmac);

    return buildData(params);
  }

  //post请求
  public static Map<String, String> httpPost(String url, String customernumber, String data) {
    //请求参数customernumber、data
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("customernumber", customernumber);
    paramMap.put("data", data);
    System.out.println("paramMap===" + paramMap);
    //get、post请求均可以，在该方法中选用了post请求方式
    String responseBody = HttpClientUtil.sendHttpRequest(url, paramMap, CHARSET, true);

    //掌柜通同步响应参数是一个json串，将该json串转成map。
    return JSON.parseObject(responseBody, new TypeReference<TreeMap<String, String>>() {
    });
  }

  //发起POST请求
  public static Map<String, String> httpPost(String url, String data) {
    return httpPost(url, customernumber, data);
  }

  //解密data：key为aeskey
  public static Map<String, String> decryptData(String data, String key) {
    String decryptData = AESUtil.decrypt(data, key);
    return JSON.parseObject(decryptData, new TypeReference<TreeMap<String, String>>() {
    });
  }

  //解密data
  public static Map<String, String> decryptData(String data) {
    return decryptData(data, keyForAes);
  }

  //hmac验签
  public static boolean checkHmac(Map<String, String> responseMap, String[] responseHmacOrder,
                                  String key) {

    if (responseMap == null || key == null || responseHmacOrder == null) {
      return false;
    }

    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < responseHmacOrder.length; i++) {
      buffer.append(formatStr(responseMap.get(responseHmacOrder[i])));
    }

    String hmacYeepay = responseMap.get("hmac");
    String hmacLocal = buildHmac(buffer.toString(), key);

    System.out.println("hmacYeepay===" + hmacYeepay);
    System.out.println("hmacLocal ===" + hmacLocal);
    System.out.println("key       ===" + key);
    System.out.println("buffer.toString()===" + buffer.toString());

    if (!hmacLocal.equals(hmacYeepay)) {
      return false;
    }

    return true;
  }

  //hmac验签
  public static boolean checkHmac(Map<String, String> responseMap, String[] responseHmacOrder) {
    return checkHmac(responseMap, responseHmacOrder, keyForHmac);
  }

  //获取请求地址
  public static String getRequestUrl(String apiName) {
    return merchantInfo.getValue(apiName);
  }

  //getter
  public static Configuration getMerchantInfo() {
    return merchantInfo;
  }

  //getter
  public static String getCustomernumber() {
    return customernumber;
  }

  //getter
  public static String getKeyForHmac() {
    return keyForHmac;
  }

  //getter
  public static String getKeyForAes() {
    return keyForAes;
  }

  //getter
  public static String getBasePath() {
    return basePath;
  }

  //字符串格式化
  public static String formatStr(String text) {
    return text == null ? "" : text;
  }

  public static String mapToQueryString(Map<String, Object> parameters, String charSet) {
    String queryString = "";
    if (parameters != null && !parameters.isEmpty()) {
      for (String key : parameters.keySet()) {
        try {
          Object value = parameters.get(key);
          if (value instanceof String) {
            queryString +=
                key + "=" + URLEncoder.encode(value == null ? "" : value.toString(), charSet)
                + "&";
          }
        } catch (UnsupportedEncodingException e) {
          throw new IllegalArgumentException("invalid charset : " + charSet);
        }
      }
      if (queryString.length() > 0) {
        queryString = queryString.substring(0, queryString.length() - 1);
      }
    }
    return queryString;
  }


  public static String uploadFile(Map<String, Object> params, String baseUrl) {
    if (!params.containsKey("file")) {
      throw new IllegalArgumentException("请上传图片");
    }
    File file = (File) params.get("file");
    if (!file.exists()) {
      throw new IllegalArgumentException("上传图片不存在");
    }
    if (baseUrl == null || baseUrl.trim().length() == 0) {
      throw new IllegalArgumentException("invalid url : " + baseUrl);
    }
    String queryString = mapToQueryString(params, "utf-8");
    int index = baseUrl.indexOf("?");
    if (index > 0) {
      baseUrl += "&" + queryString;
    } else {
      baseUrl += "?" + queryString;
    }
    PostMethod postMethod = new PostMethod(baseUrl);
    try {
      FilePart fp = new FilePart("file", file);
      Part[] parts = {fp};
      MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
      postMethod.setRequestEntity(mre);
      HttpClient client = new HttpClient();
      client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);// 设置连接时间
      int status = client.executeMethod(postMethod);
      if (status == HttpStatus.SC_OK) {
        return postMethod.getResponseBodyAsString();
      } else {
        throw new RuntimeException("上传请求异常");
      }
    } catch (Exception e) {
      throw new RuntimeException("上传请求异常");
    } finally {
      // 释放连接
      postMethod.releaseConnection();
    }
  }

  private static final String allowSuffix = "jpg,png,jpeg";//允许文件格式
  private static final long allowSize = 1L;//允许文件大小 单位 0.5M

  private static String getAllowSuffix() {
    return allowSuffix;
  }

  private static long getAllowSize() {
    return allowSize * 512 * 1024;
  }

  /**
   * 功能：文件上传
   *
   * @author zhangwen
   */
  public static File uploads(MultipartFile file, String fileName) throws Exception {

    try {
      String
          suffix =
          file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
      int length = getAllowSuffix().indexOf(suffix);
      if (length == -1) {
        throw new Exception("请上传允许格式的文件");
      }
      if (file.getSize() > getAllowSize()) {
        throw new Exception("您上传的文件大小已经超出范围");
      }
      String realPath = getBasePath();
      File destFile = new File(realPath);
      if (!destFile.exists()) {
        destFile.mkdirs();
      }
      String fileNameNew = fileName + "." + suffix;
      File f = new File(destFile.getAbsoluteFile() + "/" + fileNameNew);
      file.transferTo(f);
      f.createNewFile();
      return f;
    } catch (Exception e) {
      throw e;
    }
  }
}
