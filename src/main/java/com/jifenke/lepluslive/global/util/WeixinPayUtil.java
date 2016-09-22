package com.jifenke.lepluslive.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jifenke.lepluslive.wxpay.domain.entities.AccessToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by wcg on 16/3/21.
 */
public class WeixinPayUtil {

  private static Logger log = LoggerFactory.getLogger(WeixinPayUtil.class);

  public static Map createUnifiedOrder(String requestUrl, String requestMethod, String outputStr) {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(requestUrl);
    try {
      StringEntity myEntity = new StringEntity(outputStr, "utf-8");
      httppost.addHeader("Content-Type", "text/xml");
      httppost.setEntity(myEntity);
      CloseableHttpResponse response = httpclient.execute(httppost);
      HttpEntity resEntity = response.getEntity();
      return doXMLParse(EntityUtils.toString(resEntity, "UTF-8"));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 关闭连接,释放资源
      try {
        httpclient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static Map initialRebateOrder(String requestUrl, String outputStr, SSLContext sslcontext) {
    Map map = null;
    CloseableHttpClient httpclient = null;
    try {
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
          sslcontext,
          new String[]{"TLSv1"},
          null,
          SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
      httpclient = HttpClients.custom()
          .setSSLSocketFactory(sslsf)
          .build();
      HttpPost
          httppost =
          new HttpPost(requestUrl);
      StringEntity myEntity = new StringEntity(outputStr, "utf-8");
      httppost.addHeader("Content-Type", "text/xml");
      httppost.setEntity(myEntity);
      CloseableHttpResponse response = httpclient.execute(httppost);
      HttpEntity resEntity = response.getEntity();
      map = doXMLParse(EntityUtils.toString(resEntity, "UTF-8"));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 关闭连接,释放资源
      try {
        httpclient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return map;
  }


  public static String getUnionIdByAccessTokenAndOpenId(String accessToken, String openId) {
    String
        getUrl =
        "https://api.weixin.qq.com/cgi-bin/user/info";
    getUrl += "?access_token=" + accessToken;
    getUrl += "&openid=" + openId;
    getUrl += "&lang=zh_CN";
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(getUrl);
    httpGet.addHeader("Content-Type", "application/json;charset=utf8mb4");
    CloseableHttpResponse response = null;
    Map map = null;
    try {
      response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      map =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           Map.class);
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map.get("unionid").toString();
  }


  public static Map doXMLParse(String strxml) throws JDOMException, IOException {
    strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

    if (null == strxml || "".equals(strxml)) {
      return null;
    }

    Map m = new HashMap();

    InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(in);
    Element root = doc.getRootElement();
    List list = root.getChildren();
    Iterator it = list.iterator();
    while (it.hasNext()) {
      Element e = (Element) it.next();
      String k = e.getName();
      String v = "";
      List children = e.getChildren();
      if (children.isEmpty()) {
        v = e.getTextNormalize();
      } else {
        v = getChildrenText(children);
      }

      m.put(k, v);
    }

    //关闭流
    in.close();

    return m;
  }


  public static String getChildrenText(List children) {
    StringBuffer sb = new StringBuffer();
    if (!children.isEmpty()) {
      Iterator it = children.iterator();
      while (it.hasNext()) {
        Element e = (Element) it.next();
        String name = e.getName();
        String value = e.getTextNormalize();
        List list = e.getChildren();
        sb.append("<" + name + ">");
        if (!list.isEmpty()) {
          sb.append(getChildrenText(list));
        }
        sb.append(value);
        sb.append("</" + name + ">");
      }
    }

    return sb.toString();
  }

  public static void main1(String[] args) throws UnsupportedEncodingException {
    TreeMap<String, String> parameters = new TreeMap<String, String>();
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    Date date = new Date();
    parameters.put("msg_type", "10");
    parameters.put("msg_txn_code", "104001");
    parameters.put("msg_crrltn_id", "12345678901234567890123456789000");
    parameters.put("msg_flg", "0");
    parameters.put("msg_sender", "214");
    parameters.put("msg_time", format.format(date));
    parameters.put("msg_sys_sn", "12345678900987654321");
    parameters.put("msg_ver", "0.1");
    parameters.put("address", URLEncoder.encode("朝阳区", "utf-8"));
    parameters.put("term_no", "12345678");
    parameters.put("shop_no", "086123456123456789");
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    StringBuffer sb = new StringBuffer();
    Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
    Iterator it = es.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String k = (String) entry.getKey();
      String v = (String) entry.getValue();
      nvps.add(new BasicNameValuePair(k, v));
      if (null != v && !"".equals(v)
          && !"sign".equals(k) && !"key".equals(k)) {
        sb.append(k + "=" + v + "&");
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    String
        getUrl =
        "http://dev.spserv.yxlm.chinaums.com:17201/spservice/shopquery/doShopQuery";
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(getUrl);
    nvps.add(new BasicNameValuePair("sign", RSAUtil.sign(
        sb.toString())));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      Map result =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           HashMap.class);
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main2(String[] args) throws UnsupportedEncodingException {
    TreeMap<String, String> parameters = new TreeMap<String, String>();
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    Date date = new Date();
    parameters.put("msg_type", "10");
    parameters.put("msg_txn_code", "104003");
    parameters.put("msg_crrltn_id", "12345678901234567890123456789000");
    parameters.put("msg_flg", "0");
    parameters.put("msg_sender", "214");
    parameters.put("msg_time", format.format(date));
    parameters.put("msg_sys_sn", "12345678900987654321");
    parameters.put("msg_ver", "0.1");
    parameters.put("sp_chnl_no", "214");
    parameters.put("pk_card_no", RSAUtil.encode("6228480018572810978"));

    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    StringBuffer sb = new StringBuffer();
    Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
    Iterator it = es.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String k = (String) entry.getKey();
      String v = (String) entry.getValue();
      nvps.add(new BasicNameValuePair(k, v));
      if (null != v && !"".equals(v)
          && !"sign".equals(k) && !"key".equals(k)) {
        sb.append(k + "=" + v + "&");
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    String
        getUrl =
        "http://dev.spserv.yxlm.chinaums.com:17201/spservice/spenc/doReSign";
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(getUrl);
    nvps.add(new BasicNameValuePair("sign", RSAUtil.sign(
        sb.toString())));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      TreeMap result =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           TreeMap.class);
      String sign = result.get("sign").toString();
      result.remove("sign");
      String originStr = getOriginStr(result);
      boolean b = RSAUtil.testSign(originStr, sign);
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main3(String[] args) throws UnsupportedEncodingException {
    TreeMap<String, String> parameters = new TreeMap<String, String>();
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    Date date = new Date();
    parameters.put("msg_type", "10");
    parameters.put("msg_txn_code", "104003");
    parameters.put("msg_crrltn_id", "12345678901234567890123456789000");
    parameters.put("msg_flg", "0");
    parameters.put("msg_sender", "214");
    parameters.put("msg_time", format.format(date));
    parameters.put("msg_sys_sn", "12345678900987654321");
    parameters.put("msg_ver", "0.1");
    parameters.put("sp_chnl_no", "214");
    parameters.put("pk_card_no", RSAUtil.encode("6228480018572810978"));

    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    StringBuffer sb = new StringBuffer();
    Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
    Iterator it = es.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String k = (String) entry.getKey();
      String v = (String) entry.getValue();
      nvps.add(new BasicNameValuePair(k, v));
      if (null != v && !"".equals(v)
          && !"sign".equals(k) && !"key".equals(k)) {
        sb.append(k + "=" + v + "&");
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    String
        getUrl =
        "http://www.lepluslife.com/lepay/pospay/union_pay/search";
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(getUrl);
    nvps.add(new BasicNameValuePair("sign", RSAUtil.sign(
        sb.toString())));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      TreeMap result =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           TreeMap.class);
      String sign = result.get("sign").toString();
      result.remove("sign");
      String originStr = getOriginStr(result);
      boolean b = RSAUtil.testSign(originStr, sign);
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static String getOriginStr(TreeMap parameters) {
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

}
