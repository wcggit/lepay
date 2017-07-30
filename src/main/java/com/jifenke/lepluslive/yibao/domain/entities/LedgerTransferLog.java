package com.jifenke.lepluslive.yibao.domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 易宝转账记录（一个业务转账单可能对应多个记录）
 * Created by zhangwen on 2017/7/21.
 */
@Entity
@Table(name = "YB_LEDGER_TRANSFER_LOG")
public class LedgerTransferLog {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Date dateCreated = new Date();

  private Date dateCompleted;  //请求响应时间

  @Column(nullable = false, length = 30)
  private String requestId;  //转账请求号

  @Column(nullable = false, length = 30)
  private String orderSid;  //对应业务转账单号

  private Integer type = 1;  //转账类型 1=交易实时转账，2=定时合并转账，3=手动补单

  private Integer state = 0;   //转账状态 1=转账成功，2=转账失败，3=转账中（查询非终态），其他为易宝错误码

  @Column(nullable = false, length = 50)
  private String ledgerNo;  //易宝的子商户号

  private Long amount = 0L;   //转账金额

  private String msg;  //返回错误描述

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateCompleted() {
    return dateCompleted;
  }

  public void setDateCompleted(Date dateCompleted) {
    this.dateCompleted = dateCompleted;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getOrderSid() {
    return orderSid;
  }

  public void setOrderSid(String orderSid) {
    this.orderSid = orderSid;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getLedgerNo() {
    return ledgerNo;
  }

  public void setLedgerNo(String ledgerNo) {
    this.ledgerNo = ledgerNo;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
