package com.jifenke.lepluslive.order.domain.entities;

import com.jifenke.lepluslive.global.abstraction.Order;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Created by wcg on 16/5/5.
 */
@Entity
@Table(name = "OFF_LINE_ORDER")
public class OffLineOrder implements Order {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String orderSid = MvUtil.getOrderNumber();

  private Date createdDate = new Date();

  private Date completeDate;

  @ManyToOne
  private LeJiaUser leJiaUser;

  @ManyToOne
  private Merchant merchant;

  @ManyToOne
  private PayWay payWay;

  private Long totalPrice = 0L; //实际支付总金额 包括红包+微信

  private Long truePay = 0L;

  private Long ljCommission = 0L; //支付总手续费

  private Long trueScore = 0L; //实际使用红包

  private Long wxCommission = 0L; //微信手续费

  private Long rebate = 0L; //返利红包

  private Long scoreB = 0L; //发放积分

  private Integer state = 0; //支付状态

  private Long transferMoney; //每笔应该转给商户的金额(包括现金和红包)

  private Long transferMoneyFromTruePay; //每笔订单中现金支付转给商户的金额

  private Long truePayCommission = 0L;//实际支付手续费

  private Integer
      rebateWay;
  //返利方式,如果为0 代表非会员普通订单 则只返b积分 如果为1 导流订单 2 会员普通商户消费订单 3会员订单 4 非会员扫纯支付码 5 会员扫纯支付码 6会员联盟商户消费普通费率订单

  private Integer messageState = 0; //发送模版消息状态

  private Long monthlyOrderCount;//每月第几笔订单

  private String lepayCode = MvUtil.getLePayCode();

  private Long ljProfit = 0L;//每笔订单的额外收入

  private Long scoreC = 0L; //发放金币

  private Long shareMoney=0L; //每笔订单分润金额

  private BigDecimal commissionScale; // 订单费率

  private String policy = "0_0"; //佣金策略_红包策略 如 0_0 代表固定佣金策略 和普通红包策略

  private Integer criticalOrder = 0; //是否暴击订单 0 非暴击 1 暴击

  private Long nonCriticalRebate = 0L; //非暴击返鼓励金

  private Long sumPrice = 0L; //订单总金额 是实际支付总金额+优惠总金额(暂时只有储值优惠)

  public Long getSumPrice() {
    return sumPrice;
  }

  public void setSumPrice(Long sumPrice) {
    this.sumPrice = sumPrice;
  }

  public Long getShareMoney() {
    return shareMoney;
  }

  public void setShareMoney(Long shareMoney) {
    this.shareMoney = shareMoney;
  }

  public Long getScoreC() {
    return scoreC;
  }

  public void setScoreC(Long scoreC) {
    this.scoreC = scoreC;
  }

  public BigDecimal getCommissionScale() {
    return commissionScale;
  }

  public void setCommissionScale(BigDecimal commissionScale) {
    this.commissionScale = commissionScale;
  }

  public String getPolicy() {
    return policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public Integer getCriticalOrder() {
    return criticalOrder;
  }

  public void setCriticalOrder(Integer criticalOrder) {
    this.criticalOrder = criticalOrder;
  }

  public Long getNonCriticalRebate() {
    return nonCriticalRebate;
  }

  public void setNonCriticalRebate(Long nonCriticalRebate) {
    this.nonCriticalRebate = nonCriticalRebate;
  }

  public Long getLjProfit() {
    return ljProfit;
  }

  public void setLjProfit(Long ljProfit) {
    this.ljProfit = ljProfit;
  }

  public Long getTruePayCommission() {
    return truePayCommission;
  }

  public void setTruePayCommission(Long truePayCommission) {
    this.truePayCommission = truePayCommission;
  }

  public Long getMonthlyOrderCount() {
    return monthlyOrderCount;
  }

  public void setMonthlyOrderCount(Long monthlyOrderCount) {
    this.monthlyOrderCount = monthlyOrderCount;
  }

  public Long getTransferMoneyFromTruePay() {
    return transferMoneyFromTruePay;
  }

  public void setTransferMoneyFromTruePay(Long transferMoneyFromTruePay) {
    this.transferMoneyFromTruePay = transferMoneyFromTruePay;
  }

  public Integer getMessageState() {
    return messageState;
  }

  public void setMessageState(Integer messageState) {
    this.messageState = messageState;
  }

  public String getLepayCode() {
    return lepayCode;
  }

  public void setLepayCode(String lepayCode) {
    this.lepayCode = lepayCode;
  }

  public Integer getRebateWay() {
    return rebateWay;
  }

  public void setRebateWay(Integer rebateWay) {
    this.rebateWay = rebateWay;
  }

  public Long getTransferMoney() {
    return transferMoney;
  }

  public void setTransferMoney(Long transferMoney) {
    this.transferMoney = transferMoney;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderSid() {
    return orderSid;
  }

  public void setOrderSid(String orderSid) {
    this.orderSid = orderSid;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getCompleteDate() {
    return completeDate;
  }

  public void setCompleteDate(Date completeDate) {
    this.completeDate = completeDate;
  }

  public LeJiaUser getLeJiaUser() {
    return leJiaUser;
  }

  public void setLeJiaUser(LeJiaUser leJiaUser) {
    this.leJiaUser = leJiaUser;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public PayWay getPayWay() {
    return payWay;
  }

  public void setPayWay(PayWay payWay) {
    this.payWay = payWay;
  }

  public Long getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Long getTruePay() {
    return truePay;
  }

  public void setTruePay(Long truePay) {
    this.truePay = truePay;
  }

  public Long getLjCommission() {
    return ljCommission;
  }

  public void setLjCommission(Long ljCommission) {
    this.ljCommission = ljCommission;
  }

  public Long getTrueScore() {
    return trueScore;
  }

  public void setTrueScore(Long trueScore) {
    this.trueScore = trueScore;
  }

  public Long getWxCommission() {
    return wxCommission;
  }

  public void setWxCommission(Long wxCommission) {
    this.wxCommission = wxCommission;
  }

  public Long getRebate() {
    return rebate;
  }

  public void setRebate(Long rebate) {
    this.rebate = rebate;
  }

  public Long getScoreB() {
    return scoreB;
  }

  public void setScoreB(Long scoreB) {
    this.scoreB = scoreB;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }
}
