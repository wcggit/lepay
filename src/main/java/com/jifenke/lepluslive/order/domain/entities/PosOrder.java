package com.jifenke.lepluslive.order.domain.entities;

import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;

import java.util.Date;

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
@Table(name = "POS_ORDER")
public class PosOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String orderSid;

  private Date createdDate;

  private Date completeDate;

  @ManyToOne
  private LeJiaUser leJiaUser;

  @ManyToOne
  private MerchantPos merchantPos;

  @ManyToOne
  private PayWay payWay;

  private Long ljCommission = 0L; //乐加佣金

  private Long trueScore = 0L; //实际使用红包

  private Long wxCommission = 0L; //微信手续费

  private Long rebate = 0L; //返利红包

  private Long scoreB = 0L; //发放积分

  private Integer state = 0; //支付状态

  private Long transferMoney; //每笔应该转给商户的金额

  private Long totalPrice;

  private Long truePay; //实际支付

  private Integer rebateWay; //返利方式,如果为0 代表非会员普通订单 则只返b积分 如果为1 导流订单 2 会员普通订单 3会员订单 4 非会员扫纯支付码 5 会员扫纯支付码


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

  public MerchantPos getMerchantPos() {
    return merchantPos;
  }

  public void setMerchantPos(MerchantPos merchantPos) {
    this.merchantPos = merchantPos;
  }

  public PayWay getPayWay() {
    return payWay;
  }

  public void setPayWay(PayWay payWay) {
    this.payWay = payWay;
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

  public Long getTransferMoney() {
    return transferMoney;
  }

  public void setTransferMoney(Long transferMoney) {
    this.transferMoney = transferMoney;
  }

  public Integer getRebateWay() {
    return rebateWay;
  }

  public void setRebateWay(Integer rebateWay) {
    this.rebateWay = rebateWay;
  }

}
