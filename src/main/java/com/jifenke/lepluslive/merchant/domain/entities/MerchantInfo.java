package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商家信息 Created by zhangwen on 16/5/5.
 */
@Entity
@Table(name = "MERCHANT_INFO")
public class MerchantInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private BigDecimal star = new BigDecimal(0);   //星级

  private Integer perSale = 0;  //客单价

  private Integer park = 1;   //有停车? 0=无

  private Integer wifi = 1;   //有wifi? 0=无

  private Integer card = 1;   //可刷卡? 0=不可

  private Integer qrCode = 0;    //是否有永久二维码

  private String parameter;  //二维码参数(1-32位)随机生成  最多2万个

  private String ticket;      //获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码

  private Integer discount = 100; //乐加专享折扣 单位%  88表示88折

  //1=乐加联盟|2=消费获礼|3=优先体验|4=双重优惠|5=会员积分
  private String feature;  //店铺特色，最多三个 例：1_3_5

  private String vipPicture; //专享活动图

  private String reason; //推荐理由，多条以“+=”分隔

  private String description; //服务说明，多条以“+=”分隔

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getStar() {
    return star;
  }

  public void setStar(BigDecimal star) {
    this.star = star;
  }

  public Integer getPerSale() {
    return perSale;
  }

  public void setPerSale(Integer perSale) {
    this.perSale = perSale;
  }

  public Integer getPark() {
    return park;
  }

  public void setPark(Integer park) {
    this.park = park;
  }

  public Integer getWifi() {
    return wifi;
  }

  public void setWifi(Integer wifi) {
    this.wifi = wifi;
  }

  public Integer getCard() {
    return card;
  }

  public void setCard(Integer card) {
    this.card = card;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public Integer getQrCode() {
    return qrCode;
  }

  public void setQrCode(Integer qrCode) {
    this.qrCode = qrCode;
  }

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public Integer getDiscount() {
    return discount;
  }

  public void setDiscount(Integer discount) {
    this.discount = discount;
  }

  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  public String getVipPicture() {
    return vipPicture;
  }

  public void setVipPicture(String vipPicture) {
    this.vipPicture = vipPicture;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
