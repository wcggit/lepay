package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wcg on 2016/11/7.商户发放红包积分策略
 */
@Entity
@Table(name = "MERCHANT_REBATE_POLICY")
public class MerchantRebatePolicy {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long merchantId;

  private Integer rebateFlag; //是否开启会员订单 0未开启 1开启 2 会员订单走普通订单返积分百分比

  private BigDecimal importScoreBScale;//导流订单发放积分策略

  private BigDecimal userScoreBScale;//会员订单按比例发放积分策略返积分比

  private BigDecimal userScoreBScaleB;//会员订单全额发放积分策略返积分比


  private BigDecimal userScoreAScale;//会员订单按比例发放积分策略返红包比

  private Integer stageOne;//区间一的概率

  private Integer stageTwo;//区间二的概率

  private Integer stageThree;//区间三的概率

  private Integer stageFour;//区间四的概率

  private  Integer regionOne; //区间一 0%～regionOne%

  private Integer regionTwo; //区间二 regionOne%～regionTwo%

  private Integer regionThree;  //区间三 regionTwo%～regionThree%

  private Integer regionFour;//区间4 regionThree%～regionFour%


  public Integer getRegionOne() {
    return regionOne;
  }

  public void setRegionOne(Integer regionOne) {
    this.regionOne = regionOne;
  }

  public Integer getRegionTwo() {
    return regionTwo;
  }

  public void setRegionTwo(Integer regionTwo) {
    this.regionTwo = regionTwo;
  }

  public Integer getRegionThree() {
    return regionThree;
  }

  public void setRegionThree(Integer regionThree) {
    this.regionThree = regionThree;
  }

  public Integer getRegionFour() {
    return regionFour;
  }

  public void setRegionFour(Integer regionFour) {
    this.regionFour = regionFour;
  }

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

  public Integer getRebateFlag() {
    return rebateFlag;
  }

  public void setRebateFlag(Integer rebateFlag) {
    this.rebateFlag = rebateFlag;
  }

  public BigDecimal getImportScoreBScale() {
    return importScoreBScale;
  }

  public void setImportScoreBScale(BigDecimal importScoreBScale) {
    this.importScoreBScale = importScoreBScale;
  }

  public BigDecimal getUserScoreBScale() {
    return userScoreBScale;
  }

  public void setUserScoreBScale(BigDecimal userScoreBScale) {
    this.userScoreBScale = userScoreBScale;
  }

  public BigDecimal getUserScoreBScaleB() {
    return userScoreBScaleB;
  }

  public void setUserScoreBScaleB(BigDecimal userScoreBScaleB) {
    this.userScoreBScaleB = userScoreBScaleB;
  }

  public BigDecimal getUserScoreAScale() {
    return userScoreAScale;
  }

  public void setUserScoreAScale(BigDecimal userScoreAScale) {
    this.userScoreAScale = userScoreAScale;
  }

  public Integer getStageOne() {
    return stageOne;
  }

  public void setStageOne(Integer stageOne) {
    this.stageOne = stageOne;
  }

  public Integer getStageTwo() {
    return stageTwo;
  }

  public void setStageTwo(Integer stageTwo) {
    this.stageTwo = stageTwo;
  }

  public Integer getStageThree() {
    return stageThree;
  }

  public void setStageThree(Integer stageThree) {
    this.stageThree = stageThree;
  }

  public Integer getStageFour() {
    return stageFour;
  }

  public void setStageFour(Integer stageFour) {
    this.stageFour = stageFour;
  }
}
