package com.jifenke.lepluslive.pospay.domain;

/**
 * Created by wcg on 16/8/4.
 */
public class PosResultInfo {

  private Integer pos;

  private Integer groupon;

  private String posId;

  private String orderNo;

  private String cardNo;

  private Integer state;

  private Long points;

  private Integer pointScale;

  private String store_name;


  public String getStore_name() {
    return store_name;
  }

  public void setStore_name(String store_name) {
    this.store_name = store_name;
  }

  public Integer getPos() {
    return pos;
  }

  public void setPos(Integer pos) {
    this.pos = pos;
  }

  public Integer getGroupon() {
    return groupon;
  }

  public void setGroupon(Integer groupon) {
    this.groupon = groupon;
  }

  public String getPosId() {
    return posId;
  }

  public void setPosId(String posId) {
    this.posId = posId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getCardNo() {
    return cardNo;
  }

  public void setCardNo(String cardNo) {
    this.cardNo = cardNo;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Long getPoints() {
    return points;
  }

  public void setPoints(Long points) {
    this.points = points;
  }

  public Integer getPointScale() {
    return pointScale;
  }

  public void setPointScale(Integer pointScale) {
    this.pointScale = pointScale;
  }
}
