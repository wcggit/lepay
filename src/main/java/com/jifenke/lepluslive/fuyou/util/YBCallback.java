package com.jifenke.lepluslive.fuyou.util;

import java.util.Date;

/**
 * 易宝支付回调
 * Created by zhangwen on 2017/7/30.
 */
public class YBCallback {

  //注意左右开闭区间
  private int type;  //1=[00:00:00~23:30:00]|2=(23:30:00~23:59:30]|3=(23:59:30~00:00:00)

  private Date dateCompleted;

  private String settleDate;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Date getDateCompleted() {
    return dateCompleted;
  }

  public void setDateCompleted(Date dateCompleted) {
    this.dateCompleted = dateCompleted;
  }

  public String getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(String settleDate) {
    this.settleDate = settleDate;
  }

  @Override
  public String toString() {
    return "YBCallback{" +
           "type=" + type +
           ", dateCompleted=" + dateCompleted +
           ", settleDate='" + settleDate + '\'' +
           '}';
  }
}
