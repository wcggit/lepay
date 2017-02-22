package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

  private Integer commissionPolicy; //佣金策略 0 固定策略 1 阶段性策略

  private Integer rebatePolicy; //红包策略 0 普通策略 1 开启鼓励金

  private Long merchantId;

  private Integer rebateFlag; //是否开启会员订单scoreBRebate     0-不开启（按比例）  1-开启（全部） 2-不开启

  private BigDecimal importScoreBScale;//导流订单发放积分策略    【导流订单积分】

  private BigDecimal importScoreCScale; //导流订单发放金币策略    【导流订单金币】

  private BigDecimal userScoreBScale;//会员订单按比例发放积分策略返积分比  【会员订单-比例-积分】

  private BigDecimal userScoreCScale;//会员订单按比例发放积分策略返积分比  【会员订单-比例-积分】

  private BigDecimal userScoreBScaleB;//会员订单全额发放积分策略返积分比   【会员订单-全额-积分】

  private BigDecimal userScoreCScaleB;//会员订单全额发放积分策略返积分比   【会员订单-全额-积分】

  private BigDecimal userScoreAScale;//会员订单按比例发放积分策略返红包比  【会员订单-比例-红包】

  private BigDecimal importShareScale; //导流订单分润百分比

  private BigDecimal memberShareScale; //导流订单分润百分比

  private Integer stageOne;//0%~20%

  private Integer stageTwo;//20%~40%

  private Integer stageThree;//40%·60%

  private Integer stageFour;//60%~80%

  private Integer regionOne; //区间一 0%～regionOne%

  private Integer regionTwo; //区间二 regionOne%～regionTwo%

  private Integer regionThree;  //区间三 regionTwo%～regionThree%

  private Integer regionFour;//区间4 regionThree%～regionFour%

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "merchantRebatePolicy")
  private List<CommissionStage> commissionStages;


  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "merchantRebatePolicy")
  private List<RebateStage> rebateStages;


  public Integer getCommissionPolicy() {
    return commissionPolicy;
  }

  public void setCommissionPolicy(Integer commissionPolicy) {
    this.commissionPolicy = commissionPolicy;
  }

  public Integer getRebatePolicy() {
    return rebatePolicy;
  }

  public void setRebatePolicy(Integer rebatePolicy) {
    this.rebatePolicy = rebatePolicy;
  }

  public BigDecimal getImportScoreCScale() {
    return importScoreCScale;
  }

  public void setImportScoreCScale(BigDecimal importScoreCScale) {
    this.importScoreCScale = importScoreCScale;
  }

  public BigDecimal getUserScoreCScale() {
    return userScoreCScale;
  }

  public void setUserScoreCScale(BigDecimal userScoreCScale) {
    this.userScoreCScale = userScoreCScale;
  }

  public BigDecimal getUserScoreCScaleB() {
    return userScoreCScaleB;
  }

  public void setUserScoreCScaleB(BigDecimal userScoreCScaleB) {
    this.userScoreCScaleB = userScoreCScaleB;
  }

  public BigDecimal getImportShareScale() {
    return importShareScale;
  }

  public void setImportShareScale(BigDecimal importShareScale) {
    this.importShareScale = importShareScale;
  }

  public BigDecimal getMemberShareScale() {
    return memberShareScale;
  }

  public void setMemberShareScale(BigDecimal memberShareScale) {
    this.memberShareScale = memberShareScale;
  }

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

  public List<CommissionStage> getCommissionStages() {
    return commissionStages;
  }

  public void setCommissionStages(List<CommissionStage> commissionStages) {
    this.commissionStages = commissionStages;
  }

  public List<RebateStage> getRebateStages() {
    return rebateStages;
  }

  public void setRebateStages(List<RebateStage> rebateStages) {
    this.rebateStages = rebateStages;
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
