package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/8/2.
 */
@Entity
@Table(name = "MERCHANT_POS")
public class MerchantPos {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private Merchant merchant;

  private String posId;

  private String sshKey;

  private String psamCard;

  private Integer type; //1 封顶类pos 0 非封顶pos

  private BigDecimal posCommission; //pos 佣金 针对非会员消费

  private BigDecimal ljCommission; //会员刷卡消费佣金

  private BigDecimal wxCommission; // 微信佣金

  private BigDecimal aliCommission; //阿里佣金

  private BigDecimal bdCommission; //百度佣金

  private Long ceil; //封顶手续费

  public BigDecimal getLjCommission() {
    return ljCommission;
  }

  public void setLjCommission(BigDecimal ljCommission) {
    this.ljCommission = ljCommission;
  }

  public String getPsamCard() {
    return psamCard;
  }

  public void setPsamCard(String psamCard) {
    this.psamCard = psamCard;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public BigDecimal getPosCommission() {
    return posCommission;
  }

  public void setPosCommission(BigDecimal posCommission) {
    this.posCommission = posCommission;
  }

  public BigDecimal getWxCommission() {
    return wxCommission;
  }

  public void setWxCommission(BigDecimal wxCommission) {
    this.wxCommission = wxCommission;
  }

  public BigDecimal getAliCommission() {
    return aliCommission;
  }

  public void setAliCommission(BigDecimal aliCommission) {
    this.aliCommission = aliCommission;
  }

  public BigDecimal getBdCommission() {
    return bdCommission;
  }

  public void setBdCommission(BigDecimal bdCommission) {
    this.bdCommission = bdCommission;
  }

  public Long getCeil() {
    return ceil;
  }

  public void setCeil(Long ceil) {
    this.ceil = ceil;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public String getPosId() {
    return posId;
  }

  public void setPosId(String posId) {
    this.posId = posId;
  }

  public String getSshKey() {
    return sshKey;
  }

  public void setSshKey(String sshKey) {
    this.sshKey = sshKey;
  }
}
