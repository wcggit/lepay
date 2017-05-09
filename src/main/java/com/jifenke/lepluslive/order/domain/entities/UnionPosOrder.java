package com.jifenke.lepluslive.order.domain.entities;

import com.jifenke.lepluslive.global.abstraction.Order;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 银联商务 Created by zhangwen on 16/10/10.
 */
@Entity
@Table(name = "UNION_POS_ORDER")
public class UnionPosOrder implements Order {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String orderSid;

  private String orderCode; //银联商务销账流水号，用于对账及撤销

  private String settleDate;  //清算日期  YYYYMMDD

  private Date createdDate;

  private Date completeDate;

  private Date cancelDate; //冲正或撤销时间

  @ManyToOne
  private LeJiaUser leJiaUser;

  @ManyToOne
  private Merchant merchant;

  //记录的是实际的会员与商户关系
  private Integer rebateWay = 0; //返利方式,如果为0 代表非会员普通订单 则只返c金币 如果为1 导流订单 2 会员普通订单 3会员订单

  private Integer profit = 1;  //是否注册(即是否真的收佣金)  1=是  否的话，不会分润,如果是导流，反鼓励金、红包、红包手续费会根据实付鼓励金重新计算

  private Long commission = 0L; //总佣金
  //有些订单是会员订单但是按照导流订单费率结算，由于银联只有两种rebateWay：1和3
  private Long ljCommission = 0L; //乐加佣金(红包部分)

  private Long ysCommission = 0L; //实际支付(银商)佣金

  private Long wxCommission = 0L; //三方手续费

  private Long ysCharge = 0L; //银商实际收取的手续费

  private Long rebate = 0L; //返利红包

  private Long scoreB = 0L; //发放金币

  private Integer state = 0; //支付状态 1=已支付

  private Integer orderState = 0; //订单状态 0=未支付|1=已支付(未对账)|2=已支付(已对账)|3=未支付(已冲正)|4=未支付(已撤销)

  private Long transferMoney = 0L; //每笔应该转给商户的金额=transferByBank+transferByScore

  private Long transferByBank = 0L; //银商转给商户的金额

  private Long transferByScore = 0L; //红包部分转给商户的金额

  private Long totalPrice = 0L;  //订单总额=truePay+trueScore

  private Long truePay = 0L; //实际支付

  private Long trueScore = 0L; //实际使用红包

  private Integer paidType;  //1纯通道（刷卡|纯微信|纯支付宝）   2纯鼓励金 3通道+红包

  private Integer channel = 0;  //0=刷卡|1=微信|2=支付宝|3=纯鼓励金

  private String account;  //操作账户名

  public Long getTransferByBank() {
    return transferByBank;
  }

  public void setTransferByBank(Long transferByBank) {
    this.transferByBank = transferByBank;
  }

  public Long getTruePay() {
    return truePay;
  }

  public void setTruePay(Long truePay) {
    this.truePay = truePay;
  }

  public Long getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderCode() {
    return orderCode;
  }

  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }

  public Integer getOrderState() {
    return orderState;
  }

  public Date getCancelDate() {
    return cancelDate;
  }

  public void setCancelDate(Date cancelDate) {
    this.cancelDate = cancelDate;
  }

  public Long getYsCharge() {
    return ysCharge;
  }

  public void setYsCharge(Long ysCharge) {
    this.ysCharge = ysCharge;
  }

  public void setOrderState(Integer orderState) {
    this.orderState = orderState;
  }

  public String getOrderSid() {
    return orderSid;
  }

  public Long getCommission() {
    return commission;
  }

  public void setCommission(Long commission) {
    this.commission = commission;
  }

  public Long getYsCommission() {
    return ysCommission;
  }

  public void setYsCommission(Long ysCommission) {
    this.ysCommission = ysCommission;
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

  // todo:注意：return 的是 commission
  public Long getLjCommission() {
    return commission;
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

  public Long getTransferMoney() {
    return transferMoney;
  }

  public void setTransferMoney(Long transferMoney) {
    this.transferMoney = transferMoney;
  }

  public Integer getPaidType() {
    return paidType;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public void setPaidType(Integer paidType) {
    this.paidType = paidType;
  }

  public Long getTransferByScore() {
    return transferByScore;
  }

  public void setTransferByScore(Long transferByScore) {
    this.transferByScore = transferByScore;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Integer getRebateWay() {
    return rebateWay;
  }

  public void setRebateWay(Integer rebateWay) {
    this.rebateWay = rebateWay;
  }

  public String getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(String settleDate) {
    this.settleDate = settleDate;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public Integer getProfit() {
    return profit;
  }

  public void setProfit(Integer profit) {
    this.profit = profit;
  }
}
