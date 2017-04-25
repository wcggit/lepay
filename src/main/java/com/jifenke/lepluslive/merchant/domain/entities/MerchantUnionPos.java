package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 银联商务结算规则 Created by zhangwen on 16/11/21.
 */
@Entity
@Table(name = "MERCHANT_UNION_POS")
public class MerchantUnionPos {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long merchantId;

  private BigDecimal commission; //银联POS佣金费率

  private Boolean useCommission; //佣金费率or普通费率  true=佣金费率

  private Integer isNonCardCommission = 0;  //微信、支付宝是否收取收取费   1=收取

  private BigDecimal thirdRate = new BigDecimal(0.6);  //银联第三方手续费率|纯粹为了计算分润|并非银商真实手续费

  private BigDecimal scoreARebate;//导流订单返红包比

  private BigDecimal scoreBRebate; //导流订单返金币比

  private BigDecimal userScoreARebate; //会员订单返红包比

  private BigDecimal userScoreBRebate; //会员订单返金币比

  private BigDecimal userGeneralBRebate; //会员普通订单积分发放比|会员普通订单不发红包 //暂时废弃，默认0

  private BigDecimal userGeneralACommission; //红包标准手续费率|当会员订单【普通费率】使用了红包时，红包扣除该费率后结算给商户

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(Long merchantId) {
    this.merchantId = merchantId;
  }

  public BigDecimal getCommission() {
    return commission;
  }

  public void setCommission(BigDecimal commission) {
    this.commission = commission;
  }

  public Boolean getUseCommission() {
    return useCommission;
  }

  public void setUseCommission(Boolean useCommission) {
    this.useCommission = useCommission;
  }

  public BigDecimal getThirdRate() {
    return thirdRate;
  }

  public void setThirdRate(BigDecimal thirdRate) {
    this.thirdRate = thirdRate;
  }

  public BigDecimal getScoreARebate() {
    return scoreARebate;
  }

  public void setScoreARebate(BigDecimal scoreARebate) {
    this.scoreARebate = scoreARebate;
  }

  public BigDecimal getScoreBRebate() {
    return scoreBRebate;
  }

  public void setScoreBRebate(BigDecimal scoreBRebate) {
    this.scoreBRebate = scoreBRebate;
  }

  public BigDecimal getUserScoreARebate() {
    return userScoreARebate;
  }

  public void setUserScoreARebate(BigDecimal userScoreARebate) {
    this.userScoreARebate = userScoreARebate;
  }

  public BigDecimal getUserScoreBRebate() {
    return userScoreBRebate;
  }

  public void setUserScoreBRebate(BigDecimal userScoreBRebate) {
    this.userScoreBRebate = userScoreBRebate;
  }

  public BigDecimal getUserGeneralBRebate() {
    return userGeneralBRebate;
  }

  public void setUserGeneralBRebate(BigDecimal userGeneralBRebate) {
    this.userGeneralBRebate = userGeneralBRebate;
  }

  public BigDecimal getUserGeneralACommission() {
    return userGeneralACommission;
  }

  public void setUserGeneralACommission(BigDecimal userGeneralACommission) {
    this.userGeneralACommission = userGeneralACommission;
  }

  public Integer getIsNonCardCommission() {
    return isNonCardCommission;
  }

  public void setIsNonCardCommission(Integer isNonCardCommission) {
    this.isNonCardCommission = isNonCardCommission;
  }
}
