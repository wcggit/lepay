package com.jifenke.lepluslive.activity.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/9/12.
 */
@Entity
@Table(name = "INITIAL_ORDER_REBATE_ACTIVITY")
public class InitialOrderRebateActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  private Merchant merchant;

  @Column(name = "consume_limit")
  private Long limit = 0L;//每笔订单消费限制

  private Integer rebateType; //0 固定金额首单奖励 1 随机金额

  private Long maxRebate = 0L;//如果rebateType为 0 代表固定奖励

  private Long minRebate = 0L;

  private Integer dailyRebateType; //0无上限 1 有上限

  private Long dailyRebateLimit = 0L;//每日转账限制

  private Integer state;//活动状态 0 为开启 1 开启

  private Long totalRebateTimes = 0L;//累计返还次数

  private Long totalRebateMoney = 0L;

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

  public Long getLimit() {
    return limit;
  }

  public void setLimit(Long limit) {
    this.limit = limit;
  }

  public Integer getRebateType() {
    return rebateType;
  }

  public void setRebateType(Integer rebateType) {
    this.rebateType = rebateType;
  }

  public Long getMaxRebate() {
    return maxRebate;
  }

  public void setMaxRebate(Long maxRebate) {
    this.maxRebate = maxRebate;
  }

  public Long getMinRebate() {
    return minRebate;
  }

  public void setMinRebate(Long minRebate) {
    this.minRebate = minRebate;
  }

  public Integer getDailyRebateType() {
    return dailyRebateType;
  }

  public void setDailyRebateType(Integer dailyRebateType) {
    this.dailyRebateType = dailyRebateType;
  }

  public Long getDailyRebateLimit() {
    return dailyRebateLimit;
  }

  public void setDailyRebateLimit(Long dailyRebateLimit) {
    this.dailyRebateLimit = dailyRebateLimit;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Long getTotalRebateTimes() {
    return totalRebateTimes;
  }

  public void setTotalRebateTimes(Long totalRebateTimes) {
    this.totalRebateTimes = totalRebateTimes;
  }

  public Long getTotalRebateMoney() {
    return totalRebateMoney;
  }

  public void setTotalRebateMoney(Long totalRebateMoney) {
    this.totalRebateMoney = totalRebateMoney;
  }
}
