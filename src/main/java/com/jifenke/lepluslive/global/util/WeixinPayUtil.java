package com.jifenke.lepluslive.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jifenke.lepluslive.wxpay.domain.entities.AccessToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
//    try {
//      // 创建SSLContext对象，并使用我们指定的信任管理器初始化
//      Date start = new Date();
//      TrustManager[] tm = {new MyX509TrustManager()};
//      SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//      sslContext.init(null, tm, new java.security.SecureRandom());
//      // 从上述SSLContext对象中得到SSLSocketFactory对象
//      SSLSocketFactory ssf = sslContext.getSocketFactory();
//      URL url = new URL(requestUrl);
//      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//      conn.setSSLSocketFactory(ssf);
//      conn.setDoOutput(true);
//      conn.setDoInput(true);
//      conn.setUseCaches(false);
//      // 设置请求方式（GET/POST）
//      conn.setRequestMethod(requestMethod);
//      conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//      // 当outputStr不为null时向输出流写数据
//      if (null != outputStr) {
//        OutputStream outputStream = conn.getOutputStream();
//        // 注意编码格式
//        outputStream.write(outputStr.getBytes("UTF-8"));
//        outputStream.close();
//      }
//      // 从输入流读取返回内容
//      InputStream inputStream = conn.getInputStream();
//      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//      String str = null;
//      StringBuffer buffer = new StringBuffer();
//      while ((str = bufferedReader.readLine()) != null) {
//        buffer.append(str);
//      }
//      // 释放资源
//      bufferedReader.close();
//      inputStreamReader.close();
//      inputStream.close();
//      inputStream = null;
//      conn.disconnect();
//      Date end = new Date();
//      System.out.println(end.getTime()-start.getTime()+"微信请求的时间=========");
//      return doXMLParse(buffer.toString());
//    } catch (ConnectException ce) {
//    } catch (Exception e) {
//      log.error("https请求异常：{}", e);
//    }

//    Date start = new Date();
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(requestUrl);

    try {

      StringEntity myEntity = new StringEntity(outputStr, "utf-8");
      httppost.addHeader("Content-Type", "text/xml");
      httppost.setEntity(myEntity);
      CloseableHttpResponse response = httpclient.execute(httppost);
      HttpEntity resEntity = response.getEntity();
//      Date end = new Date();
//      System.out.println(end.getTime() - start.getTime() + "微信请求的时间=========");
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


  public static AccessToken getAccessToken(Long wxId) {
    String
        getUrl =
        "http://www.lepluslife.com:8081/accessToken/" + wxId;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(getUrl);
    httpGet.addHeader("Content-Type", "application/json;charset=utf8mb4");
    CloseableHttpResponse response = null;
    AccessToken accessToken = null;
    try {
      response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      accessToken =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           AccessToken.class);
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return accessToken;
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

}
