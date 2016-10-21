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

  private BigDecimal debitCardCommission; //借记卡非会员佣金比

  private BigDecimal ljCommission; //会员刷卡消费佣金

  private BigDecimal wxCommission; // 微信非会员佣金

  private BigDecimal aliCommission; //阿里佣金

  private BigDecimal creditCardCommission; //贷记卡非会员佣金比

  private Long ceil; //封顶手续费

  private BigDecimal posCommission; //会员刷卡佣金比

  private BigDecimal wxUserCommission;//微信会员佣金比

  private BigDecimal aliUserCommission; //支付宝会员佣金比

  private BigDecimal scoreARebate;//导流订单比

  public BigDecimal getPosCommission() {
    return posCommission;
  }

  public void setPosCommission(BigDecimal posCommission) {
    this.posCommission = posCommission;
  }

  public BigDecimal getWxUserCommission() {
    return wxUserCommission;
  }

  public void setWxUserCommission(BigDecimal wxUserCommission) {
    this.wxUserCommission = wxUserCommission;
  }

  public BigDecimal getAliUserCommission() {
    return aliUserCommission;
  }

  public void setAliUserCommission(BigDecimal aliUserCommission) {
    this.aliUserCommission = aliUserCommission;
  }

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


  public BigDecimal getDebitCardCommission() {
    return debitCardCommission;
  }

  public void setDebitCardCommission(BigDecimal debitCardCommission) {
    this.debitCardCommission = debitCardCommission;
  }

  public BigDecimal getCreditCardCommission() {
    return creditCardCommission;
  }

  public void setCreditCardCommission(BigDecimal creditCardCommission) {
    this.creditCardCommission = creditCardCommission;
  }
}
