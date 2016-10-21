package com.jifenke.lepluslive.pospay.domain;

/**
 * 银联商务POS获取商户信息 Created by zhangwen on 16/10/10.
 */
public class MerchantResult {

  private Long id; //商户id(merchantId)

  private Integer partnership;

  private String account;  //账户名称

  private Long accountId; //账户信息(MerchantUserId)

  private String qrCode; //商户永久二维码

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getQrCode() {
    return qrCode;
  }

  public void setQrCode(String qrCode) {
    this.qrCode = qrCode;
  }

  public Integer getPartnership() {
    return partnership;
  }

  public void setPartnership(Integer partnership) {
    this.partnership = partnership;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }
}
