package com.jifenke.lepluslive.pospay.domain;

/**
 * 银联商务POS获取消费者信息 Created by zhangwen on 16/10/10.
 */
public class UserResult {

  private Long id;

  private String nickname;

  private String headImageUrl;

  private Integer state; //是否是会员 1=是

  private Long scoreA;  //红包余额

  private Long times; //到店消费次数

  private Long totalPrice;  //该店消费总额

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getHeadImageUrl() {
    return headImageUrl;
  }

  public void setHeadImageUrl(String headImageUrl) {
    this.headImageUrl = headImageUrl;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Long getScoreA() {
    return scoreA;
  }

  public void setScoreA(Long scoreA) {
    this.scoreA = scoreA;
  }

  public Long getTimes() {
    return times;
  }

  public void setTimes(Long times) {
    this.times = times;
  }

  public Long getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice;
  }
}
