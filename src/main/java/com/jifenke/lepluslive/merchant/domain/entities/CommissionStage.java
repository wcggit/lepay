package com.jifenke.lepluslive.merchant.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/2/20. 阶段收取佣金区间
 */
@Entity
@Table(name = "COMMISSION_STAGE")
public class CommissionStage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JsonIgnore
  private MerchantRebatePolicy merchantRebatePolicy;

  private Long start;

  private Long end;

  private BigDecimal commissionScale; //佣金费率

  private BigDecimal scoreCScale;   //返金币费率

  private Integer sorted;  //根据该字段排序


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MerchantRebatePolicy getMerchantRebatePolicy() {
    return merchantRebatePolicy;
  }

  public void setMerchantRebatePolicy(MerchantRebatePolicy merchantRebatePolicy) {
    this.merchantRebatePolicy = merchantRebatePolicy;
  }

  public Long getStart() {
    return start;
  }

  public void setStart(Long start) {
    this.start = start;
  }

  public Long getEnd() {
    return end;
  }

  public void setEnd(Long end) {
    this.end = end;
  }

  public BigDecimal getCommissionScale() {
    return commissionScale;
  }

  public void setCommissionScale(BigDecimal commissionScale) {
    this.commissionScale = commissionScale;
  }

  public BigDecimal getScoreCScale() {
    return scoreCScale;
  }

  public void setScoreCScale(BigDecimal scoreCScale) {
    this.scoreCScale = scoreCScale;
  }

  public Integer getSorted() {
    return sorted;
  }

  public void setSorted(Integer sorted) {
    this.sorted = sorted;
  }
}
