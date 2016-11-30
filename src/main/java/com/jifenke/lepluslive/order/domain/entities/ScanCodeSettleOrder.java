package com.jifenke.lepluslive.order.domain.entities;

import com.jifenke.lepluslive.global.util.MvUtil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 富友扫码结算单 Created by zhangwen on 16/11/28.
 */
@Entity
@Table(name = "SCAN_CODE_SETTLE_ORDER")
public class ScanCodeSettleOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String orderSid = MvUtil.getOrderNumber(); //结算单号

  private Date createdDate;

  private Date balanceDate; //交易日期

  private Date transferDate; //转账日期

  private Integer state = 0; //转账状态 1=待转账|1=已转账|2=挂帐

  private Long transferMoney = 0L; //商户实际入账(包括现金和红包)=transferMoneyFromTruePay+transferMoneyFromScore

  private Long transferMoneyFromTruePay = 0L; //每笔订单中现金支付转给商户的金额(富友结算)

  private Long transferMoneyFromScore = 0L;   //每笔订单中红包支付转给商户的金额

  @Column(nullable = false, unique = true, length = 50)
  private String merchantNum; //富友商户号

  @Column(nullable = false)
  private Integer type = 0;  //商户号类型  0=普通商户号|1=佣金商户号

  @Column(nullable = false)
  private String shopIds;    //当时使用该商户号的门店(可多个id1_id2)

  private String shopNames;   //门店名称列表

  @Column(nullable = false)
  private Long merchantUserId;  //对应的商户ID

  private String merchantName;  //对应的商户名称

  @Column(nullable = false)
  private Integer accountType = 0; //账户类型  0=法人私账|1=非法人私账|2=对公账号

  @Column(nullable = false, length = 50)
  private String bankNumber; //结算卡号

  @Column(nullable = false, length = 100)
  private String bankName;  //开户支行

  @Column(nullable = false, length = 100)
  private String payee;    //收款人

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

  public Date getBalanceDate() {
    return balanceDate;
  }

  public void setBalanceDate(Date balanceDate) {
    this.balanceDate = balanceDate;
  }

  public Date getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(Date transferDate) {
    this.transferDate = transferDate;
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

  public Long getTransferMoneyFromTruePay() {
    return transferMoneyFromTruePay;
  }

  public void setTransferMoneyFromTruePay(Long transferMoneyFromTruePay) {
    this.transferMoneyFromTruePay = transferMoneyFromTruePay;
  }

  public Long getTransferMoneyFromScore() {
    return transferMoneyFromScore;
  }

  public void setTransferMoneyFromScore(Long transferMoneyFromScore) {
    this.transferMoneyFromScore = transferMoneyFromScore;
  }

  public String getMerchantNum() {
    return merchantNum;
  }

  public void setMerchantNum(String merchantNum) {
    this.merchantNum = merchantNum;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getShopIds() {
    return shopIds;
  }

  public void setShopIds(String shopIds) {
    this.shopIds = shopIds;
  }

  public String getShopNames() {
    return shopNames;
  }

  public void setShopNames(String shopNames) {
    this.shopNames = shopNames;
  }

  public Long getMerchantUserId() {
    return merchantUserId;
  }

  public void setMerchantUserId(Long merchantUserId) {
    this.merchantUserId = merchantUserId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public void setMerchantName(String merchantName) {
    this.merchantName = merchantName;
  }

  public Integer getAccountType() {
    return accountType;
  }

  public void setAccountType(Integer accountType) {
    this.accountType = accountType;
  }

  public String getBankNumber() {
    return bankNumber;
  }

  public void setBankNumber(String bankNumber) {
    this.bankNumber = bankNumber;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }
}
