package com.jifenke.lepluslive.merchant.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/2/20. 返鼓励金阶段区间
 */
@Entity
@Table(name = "REBATE_STAGE")
public class RebateStage {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JsonIgnore
  private MerchantRebatePolicy merchantRebatePolicy;

  private Long start;

  private Long end;

  private Long rebateStart; //最少返鼓励金

  private Long rebateEnd;  //最多返鼓励金

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

  public Long getRebateStart() {
    return rebateStart;
  }

  public void setRebateStart(Long rebateStart) {
    this.rebateStart = rebateStart;
  }

  public Long getRebateEnd() {
    return rebateEnd;
  }

  public void setRebateEnd(Long rebateEnd) {
    this.rebateEnd = rebateEnd;
  }
}
