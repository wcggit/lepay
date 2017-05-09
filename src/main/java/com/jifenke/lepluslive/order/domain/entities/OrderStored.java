package com.jifenke.lepluslive.order.domain.entities;

/**
 * Created by wcg on 2017/4/12. 订单储值扩展entity 不对应数据库表
 */
public class OrderStored {

  private Long number; //使用储值

  private Long scoreC; //返金币值

  private Long shareMoney; //分润金额

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Long getScoreC() {
    return scoreC;
  }

  public void setScoreC(Long scoreC) {
    this.scoreC = scoreC;
  }

  public Long getShareMoney() {
    return shareMoney;
  }

  public void setShareMoney(Long shareMoney) {
    this.shareMoney = shareMoney;
  }
}
