package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 富友结算规则、类型及结算账户 Created by zhangwen on 16/11/25.
 */
@Entity
@Table(name = "MERCHANT_SETTLEMENT")
public class MerchantSettlement {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Integer type = 0;  //商户号类型  0=普通商户号|1=佣金商户号

  @Column(nullable = false)
  private BigDecimal commission; //费率

  @Column(nullable = false, unique = true, length = 50)
  private String merchantNum; //富友商户号

  @Column(nullable = false)
  private Integer accountType = 0; //账户类型  0=法人私账|1=非法人私账|2=对公账号

  @Column(nullable = false, length = 50)
  private String bankNumber; //结算卡号

  @Column(nullable = false, length = 100)
  private String bankName;  //开户支行

  @Column(nullable = false, length = 50)
  private String bankUnion; //联行号

  @Column(nullable = false, length = 100)
  private String payee;    //收款人

  @Column(nullable = false)
  private Long merchantUserId;  //对应的商户ID

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public BigDecimal getCommission() {
    return commission;
  }

  public void setCommission(BigDecimal commission) {
    this.commission = commission;
  }

  public String getMerchantNum() {
    return merchantNum;
  }

  public void setMerchantNum(String merchantNum) {
    this.merchantNum = merchantNum;
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

  public String getBankUnion() {
    return bankUnion;
  }

  public void setBankUnion(String bankUnion) {
    this.bankUnion = bankUnion;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Long getMerchantUserId() {
    return merchantUserId;
  }

  public void setMerchantUserId(Long merchantUserId) {
    this.merchantUserId = merchantUserId;
  }
}
