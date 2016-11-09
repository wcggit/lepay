package com.jifenke.lepluslive.wxpay;

/**
 * Created by wcg on 2016/11/3.
 */

import java.util.Random;

/**
 * JAVA 返回随机数，并根据概率、比率
 *
 * @author
 */
public class MathRandom {

  /**
   * 百分比概率
   *
   * @return int
   */
  public static int PercentageRandom(int rate0, int rate1, int rate2, int rate3) {
    Random r = new Random();
    int point = r.nextInt(100) + 1;
    if (point <= rate0) {
      return 0;
    } else if (point <= rate0 + rate1) {
      return 1;
    } else if (point <= rate0 + rate1 + rate2) {
      return 2;
    } else if (point <= rate0 + rate1 + rate2 + rate3) {
      return 3;
    } else {
      return 4;
    }
  }

  /**
   * 测试主程序
   */
  public static void main(String[] agrs) {
    int i = 0;
    int j = 1000;
    int a1 = 0;
    int a2 = 0;
    int a3 = 0;
    int a4 = 0;
    int a5 = 0;
    MathRandom a = new MathRandom();
    for (i = 0; i < j; i++)//打印100个测试概率的准确性
    {
      int result = MathRandom.PercentageRandom(10, 20, 30, 40);
      if (result == 1) {
        a2++;
      } else if (result == 2) {
        a3++;
      } else if (result == 3) {
        a4++;
      } else if (result == 4) {
        a5++;
      } else if (result == 0) {
        a1++;
      }
    }
    System.out.println(a1);
    System.out.println(a2);
    System.out.println(a3);
    System.out.println(a4);
    System.out.println(a5);
  }
}


