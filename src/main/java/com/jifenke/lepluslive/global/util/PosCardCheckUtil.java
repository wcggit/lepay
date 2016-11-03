package com.jifenke.lepluslive.global.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wcg on 2016/10/21.
 */
public class PosCardCheckUtil {


  /**
   * @param urlAll  :请求接口
   * @param httpArg :参数
   * @return 返回结果
   */
  public static String request(String httpUrl, String httpArg) {
    BufferedReader reader = null;
    String result = null;
    StringBuffer sbf = new StringBuffer();
    httpUrl = httpUrl + "?" + httpArg;

    try {
      URL url = new URL(httpUrl);
      HttpURLConnection connection = (HttpURLConnection) url
          .openConnection();
      connection.setRequestMethod("GET");
      // 填入apikey到HTTP header
      connection.setRequestProperty("apikey", "5653cfcff074312bcf6ee63f88d1720b");
      connection.connect();
      InputStream is = connection.getInputStream();
      reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String strRead = null;
      while ((strRead = reader.readLine()) != null) {
        sbf.append(strRead);
        sbf.append("\r\n");
      }
      reader.close();
      result = sbf.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

//  public static void main(String[] args) {
//    String httpUrl = "http://apis.baidu.com/datatiny/cardinfo/cardinfo";
//    String httpArg = "cardnum=6214830122180408";
//    String jsonResult = request(httpUrl, httpArg);
//    System.out.println(jsonResult.indexOf("贷"));
//    System.out.println(jsonResult);
//  }


}
