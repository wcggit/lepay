package com.jifenke.lepluslive.global.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wcg on 16/3/9.
 */
public class MvUtil {

  //新建视图
  public static ModelAndView go(String uri) {
    return new ModelAndView(uri);
  }


  public static String getExtendedName(String fullName) {
    if (fullName != null) {
      String[] arr = fullName.split(".");
      return arr[arr.length - 1];
    }
    return null;
  }

  //随机生成文件名
  public static String getFilePath(String extendName) {
    String
        randomStr =
        RandomStringUtils
            .random(10, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");

    return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + randomStr + "." + extendName;
  }


  //生成订单号
  public static String getOrderNumber() {
    String randomStr = RandomStringUtils.random(5, "1234567890");
    return new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + randomStr;
  }


  /**
   * 生成随机字符串
   */
  public static String getRandomStr() {
    return RandomStringUtils
        .random(16, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
  }

  /**
   * 生成六位数验证码
   */
  public static String getRandomNumber() {
    return RandomStringUtils.random(6, "0123456789");

  }

  /**
   * 生成4位数验证码
   */
  public static String getLePayCode() {
    return RandomStringUtils.random(4, "0123456789");

  }

  /**
   * 生成条形码
   */
  public static String getBarCodeStr() {
    return RandomStringUtils.random(13, "1234567890");
  }

  //生成商户随即号
  public static String getMerchantSid() {
    return RandomStringUtils.random(7, "1234567890");
  }

}
