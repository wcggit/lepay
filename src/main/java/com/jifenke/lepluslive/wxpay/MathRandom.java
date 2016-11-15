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

}


