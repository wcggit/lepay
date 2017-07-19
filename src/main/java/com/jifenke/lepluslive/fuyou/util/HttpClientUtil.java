package com.jifenke.lepluslive.fuyou.util;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpClientUtil {

  private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

  /**
   * HTTP GET
   */
  public static Map<Object, Object> get(String getUrl) {

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(getUrl);
    httpGet.addHeader("Content-Type", "application/json");
    CloseableHttpResponse response = null;
    try {
      response = client.execute(httpGet);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      Map<Object, Object>
          map =
          mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
                           Map.class);
      EntityUtils.consume(entity);
      response.close();
      return map;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String post(String url, Map<String, String> params, String charset) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String body = null;

    log.info("create httppost:" + url);
    HttpPost post = postForm(url, params);
    body = invoke(httpclient, post, charset);

    httpclient.getConnectionManager().shutdown();

    return body;
  }

  public static String get(String url, String charset) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String body = null;

    log.info("create httppost:" + url);
    HttpGet get = new HttpGet(url);
    body = invoke(httpclient, get, charset);

    httpclient.getConnectionManager().shutdown();

    return body;
  }

  private static String invoke(DefaultHttpClient httpclient,
                               HttpUriRequest httpost, String charset) {

    HttpResponse response = sendRequest(httpclient, httpost, charset);
    String body = paseResponse(response);

    return body;
  }

  private static String paseResponse(HttpResponse response) {
    log.info("get response from http server..");
    HttpEntity entity = response.getEntity();

    log.info("response status: " + response.getStatusLine());
    String charset = EntityUtils.getContentCharSet(entity);
    log.info(charset);

    String body = null;
    try {
      body = EntityUtils.toString(entity);
      log.info(body);
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return body;
  }

  private static HttpResponse sendRequest(DefaultHttpClient httpclient,
                                          HttpUriRequest httpost, String charset) {
    HttpResponse response = null;
    try {
      if (charset != null) {
        httpost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=" + charset);
      } else {
        httpost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=iso8859-1");
      }
      response = httpclient.execute(httpost);

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   *
   * @param url
   * @param params
   * @return
   */
  private static HttpPost postForm(String url, Map<String, String> params) {

    HttpPost httpost = new HttpPost(url);
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();

    Set<String> keySet = params.keySet();
    for (String key : keySet) {
      nvps.add(new BasicNameValuePair(key, params.get(key)));
      log.info(key + "------" + params.get(key));
    }

    try {
      log.info("set utf-8 form entity to httppost");
      httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return httpost;
  }


  //设置默认超时时间为60s
  public static final int DEFAULT_TIME_OUT = 60 * 1000;

  //http请求
  public static String sendHttpRequest(String url, Map<String, String> paramMap, String charset,
                                       boolean isPost) {
    return sendHttpRequest(url, paramMap, charset, isPost, DEFAULT_TIME_OUT);
  }

  //http请求
  private static String sendHttpRequest(String url, Map<String, String> paramMap, String charset,
                                        boolean isPost, int timeout) {
    if (isPost) {
      return httpPost(url, paramMap, charset, timeout);
    }

    return httpGet(url, paramMap, charset, timeout);
  }

  //post请求
  private static String httpPost(String url, Map<String, String> params, String charset,
                                 int timeout) {

    if (url == null || url.equals("")) {
      return null;
    }

    String result = null;

    //超时设置
    RequestConfig requestConfig = RequestConfig
        .custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();

    //参数组装
    List<NameValuePair> pairs = new ArrayList<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      pairs.add(new BasicNameValuePair(key, formatStr(value)));
    }

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = null;
    String responseBody = null;
    CloseableHttpResponse response = null;

    try {
      httpPost = new HttpPost(url);
      httpPost.setConfig(requestConfig);
      httpPost.setEntity(new UrlEncodedFormEntity(pairs));
      response = httpClient.execute(httpPost);

      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpPost.abort();
        throw new RuntimeException("HttpClient,error status code :" + statusCode);
      }

      HttpEntity entity = response.getEntity();
      responseBody = EntityUtils.toString(entity, charset);
      result = responseBody;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // 关闭连接,释放资源
        if (response != null) {
          response.close();
        }
        if (httpClient != null) {
          httpClient.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  //get请求
  private static String httpGet(String url, Map<String, String> params, String charset,
                                int timeout) {

    if (url == null || url.equals("")) {
      return null;
    }

    String result = null;

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = null;
    String responseBody = null;
    CloseableHttpResponse response = null;

    try {

      if (params != null && !params.isEmpty()) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          pairs.add(new BasicNameValuePair(key, formatStr(value)));
        }
        url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
      }

      httpGet = new HttpGet(url);
      response = httpClient.execute(httpGet);

      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpGet.abort();
        throw new RuntimeException("HttpClient,error status code :" + statusCode);
      }

      HttpEntity entity = response.getEntity();
      responseBody = EntityUtils.toString(entity, charset);
      result = responseBody;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // 关闭连接,释放资源
        if (response != null) {
          response.close();
        }
        if (httpClient != null) {
          httpClient.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  private static String formatStr(String text) {
    return (text == null ? "" : text.trim());
  }
}
