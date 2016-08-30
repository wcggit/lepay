//package com.jifenke.lepluslive.global.config;
//
//import com.jifenke.lepluslive.global.util.MD5Util;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import javax.swing.text.html.Option;
//
///**
//* Created by wcg on 16/8/25.
//*/
//public class FunctionDemo {
//
//
//  static Boolean modifyTheValue(int valueToBeOperated, Predicate<BigDecimal> function) {
//    // Do some operations using the new value.
//    System.out.println(function.test(new BigDecimal(valueToBeOperated)));
//    return function.test(new BigDecimal(valueToBeOperated));
//  }
//
//  static BigDecimal modifyTheValues(int valueToBeOperated, Function<Integer, BigDecimal> function) {
//    // Do some operations using the new value.
//    System.out.println(function.apply(valueToBeOperated));
//    return function.apply(valueToBeOperated);
//  }
//
//  public static void main1(String[] args) {
//    BigDecimal[] bigDecimals = new BigDecimal[]{new BigDecimal(1), new BigDecimal(2)};
//    modifyTheValue(1, cc -> {
//      if (cc.intValue() == 1) {
//        return true;
//      }
//      return false;
//    });
//    modifyTheValue(new BigDecimal(1));
//    Optional<Integer>
//        integer =
//        Optional.ofNullable(1).map(val -> val + 1);
//    System.out.println(integer.get());
//    List<Integer> l = new ArrayList();
//    l.add(1);
//    l.add(3);
//    l.add(2);
//    List<Integer> collect = l.stream().filter(val -> {
//      if (val % 2 == 1) {
//        return true;
//      } else {
//        return false;
//      }
//    }).collect(Collectors.toList());
//
//    collect.forEach(val -> {
//      System.out.println(val);
//    });
//  }
//
//  public static void main2(String[] args) {
//    String vcpos = "VCPOS:F81DF6823EEE3E7DBEF286EB4DF2FBD1";
//    String content = "act=1&pos=1&groupon=5&posId=5012160300003573&orderNo=20160826150602476668&orderTime=20160826150602&orderPrice=0.01&cardNo=&passWord=&token=bb8f517712daa51de39892204ad47fe4".split("&token")[0];
//    String md5Encode = MD5Util.MD5Encode(vcpos + content, "utf-8");
//    System.out.println(md5Encode);
//  }
//
//  public static void main(String[] args) {
//    System.out.println(Math.round(new BigDecimal(1.61).doubleValue()));
//    System.out.println(new BigDecimal(1.61).longValue());
//  }
//  private static <T> T modifyTheValue(T t) {
//
//    System.out.println();
//    return t;
//  }
//
//}
