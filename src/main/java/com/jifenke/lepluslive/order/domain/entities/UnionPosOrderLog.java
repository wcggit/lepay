package com.jifenke.lepluslive.order.domain.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 银联商务交易日志 Created by zhangwen on 16/11/21.
 */
@Entity
@Table(name = "UNION_POS_ORDER_LOG")
public class UnionPosOrderLog {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String msgTxnCode;  //交易代码

  private String msgTime; //报文日期

  private Date createDate = new Date();

  private String msgSysSn;//平台流水号

  private String shopNo;  //门店号

  private String termNo; //终端号

  private String encCardNo; //加密卡号

  private String partCardNo; //部分卡号

  private Long amount = 0L; //查询时消费金额 单位分

  private Long origAmt = 0L; //销账时原始金额

  private Long discountAmt = 0L; //销账时优惠金额

  private Long payAmt = 0L; //销账时支付金额

  private Integer payMode = 0;  //支付方式 1：现金；2：刷卡；3：积分；5：积分+刷卡；6：积分+现金；7：预付费卡；8：手机支付；9：圈存账户支付；10：翼支付

  private String couponNo; //券号

  private String orderNo; //自己的订单号

  private String reqSerialNo;//流水号

  private String origReqSerialNo; //原流水号

  private String acqTermSn; //受理终端流水号

  private String referNo; //检索参考号

  private String settDate;//清算日期T 数据体现在T+1

  private String txnDate;//交易日期 YYYYMMDD

  private String txnTime; //交易时间 HHMMSS

  private String equityNo; //权益号

  private String transCrrltnNo;//贯穿一笔交易(含撤销)的唯一流水号

  private String params; //参数字符串

  private String returnParams; //返回参数

  private Integer interfaceType; //接口类型 1,查询接口 2,销账接口,3冲正接口 4 撤销接口

  public String getMsgTime() {
    return msgTime;
  }

  public String getShopNo() {
    return shopNo;
  }

  public String getTermNo() {
    return termNo;
  }


  public String getCouponNo() {
    return couponNo;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getTransCrrltnNo() {
    return transCrrltnNo;
  }

  public String getOrigReqSerialNo() {
    return origReqSerialNo;
  }

  public String getAcqTermSn() {
    return acqTermSn;
  }

  public Long getOrigAmt() {
    return origAmt;
  }

  public String getEquityNo() {
    return equityNo;
  }

  public void setEquityNo(String equityNo) {
    this.equityNo = equityNo;
  }

  public void setOrigAmt(Long origAmt) {
    this.origAmt = origAmt;
  }

  public Long getDiscountAmt() {
    return discountAmt;
  }

  public void setDiscountAmt(Long discountAmt) {
    this.discountAmt = discountAmt;
  }

  public Long getPayAmt() {
    return payAmt;
  }

  public void setPayAmt(Long payAmt) {
    this.payAmt = payAmt;
  }

  public Integer getPayMode() {
    return payMode;
  }

  public void setPayMode(Integer payMode) {
    this.payMode = payMode;
  }

  public void setAcqTermSn(String acqTermSn) {
    this.acqTermSn = acqTermSn;
  }

  public void setOrigReqSerialNo(String origReqSerialNo) {
    this.origReqSerialNo = origReqSerialNo;
  }

  public void setTransCrrltnNo(String transCrrltnNo) {
    this.transCrrltnNo = transCrrltnNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public void setCouponNo(String couponNo) {
    this.couponNo = couponNo;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }


  public String getReferNo() {
    return referNo;
  }

  public void setReferNo(String referNo) {
    this.referNo = referNo;
  }

  public String getSettDate() {
    return settDate;
  }

  public void setSettDate(String settDate) {
    this.settDate = settDate;
  }

  public String getTxnDate() {
    return txnDate;
  }

  public void setTxnDate(String txnDate) {
    this.txnDate = txnDate;
  }

  public String getTxnTime() {
    return txnTime;
  }

  public void setTxnTime(String txnTime) {
    this.txnTime = txnTime;
  }

  public String getPartCardNo() {
    return partCardNo;
  }

  public void setPartCardNo(String partCardNo) {
    this.partCardNo = partCardNo;
  }

  public String getEncCardNo() {
    return encCardNo;
  }

  public void setEncCardNo(String encCardNo) {
    this.encCardNo = encCardNo;
  }

  public void setTermNo(String termNo) {
    this.termNo = termNo;
  }

  public void setShopNo(String shopNo) {
    this.shopNo = shopNo;
  }

  public void setMsgTime(String msgTime) {
    this.msgTime = msgTime;
  }

  public String getMsgTxnCode() {
    return msgTxnCode;
  }

  public void setMsgTxnCode(String msgTxnCode) {
    this.msgTxnCode = msgTxnCode;
  }

  public String getReqSerialNo() {
    return reqSerialNo;
  }

  public void setReqSerialNo(String reqSerialNo) {
    this.reqSerialNo = reqSerialNo;
  }

  public Integer getInterfaceType() {
    return interfaceType;
  }

  public void setInterfaceType(Integer interfaceType) {
    this.interfaceType = interfaceType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getMsgSysSn() {
    return msgSysSn;
  }

  public void setMsgSysSn(String msgSysSn) {
    this.msgSysSn = msgSysSn;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getReturnParams() {
    return returnParams;
  }

  public void setReturnParams(String returnParams) {
    this.returnParams = returnParams;
  }
}
