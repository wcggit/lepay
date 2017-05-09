package com.jifenke.lepluslive.order.domain.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/4/27.
 * 记录scanCodeOrder 额外信息
 */
@Entity
@Table(name = "SCAN_CODE_ORDER_EXT")
public class ScanCodeOrderExt {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long  merchantUserId; //门店对应的商户ID

  private Integer source = 0;   //支付来源  0=WAP|1=APP

  private Integer useWeixin = 0; //是否使用微信付款 0 不用 1 使用

  private Integer useAliPay=0; //是否使用支付宝付款 0 不用 1 使用

  private Integer useScoreA=0; //是否使用支付宝付款 0 不用 1 使用

  private String merchantNum;  //该订单使用的富友商户号

  private BigDecimal merchantRate;  //商户号当时的佣金费率


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMerchantUserId() {
    return merchantUserId;
  }

  public void setMerchantUserId(Long merchantUserId) {
    this.merchantUserId = merchantUserId;
  }

  public Integer getSource() {
    return source;
  }

  public void setSource(Integer source) {
    this.source = source;
  }

  public Integer getUseWeixin() {
    return useWeixin;
  }

  public void setUseWeixin(Integer useWeixin) {
    this.useWeixin = useWeixin;
  }

  public Integer getUseAliPay() {
    return useAliPay;
  }

  public void setUseAliPay(Integer useAliPay) {
    this.useAliPay = useAliPay;
  }

  public Integer getUseScoreA() {
    return useScoreA;
  }

  public void setUseScoreA(Integer useScoreA) {
    this.useScoreA = useScoreA;
  }

  public String getMerchantNum() {
    return merchantNum;
  }

  public void setMerchantNum(String merchantNum) {
    this.merchantNum = merchantNum;
  }

  public BigDecimal getMerchantRate() {
    return merchantRate;
  }

  public void setMerchantRate(BigDecimal merchantRate) {
    this.merchantRate = merchantRate;
  }
}
