package com.jifenke.lepluslive.global.util;

import java.math.BigDecimal;

/**
 * 计算相关 Created by zhangwen on 2016/11/22.
 */
public class MathUtil {

  private static final BigDecimal d100 = new BigDecimal(100);

  private static final BigDecimal d10000 = new BigDecimal(10000);

  /**
   * 银联商务计算专业 val1*val2/100四舍五入取整
   */
  public static long result(BigDecimal val1, BigDecimal val2) {
    return Math.round(val1.multiply(val2).divide(d100, 4, BigDecimal.ROUND_HALF_UP).doubleValue());
  }

  /**
   * 银联商务计算专业返B积分 val1*val2/10000四舍五入取整
   */
  public static long resultB(BigDecimal val1, BigDecimal val2) {
    return Math
        .round(val1.multiply(val2).divide(d10000, 4, BigDecimal.ROUND_HALF_UP).doubleValue());
  }

}
