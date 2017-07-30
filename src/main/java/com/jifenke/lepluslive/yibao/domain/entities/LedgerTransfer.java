package com.jifenke.lepluslive.yibao.domain.entities;

import com.jifenke.lepluslive.global.util.MvUtil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 业务转账单
 * Created by zhangwen on 2017/7/12.
 */
@Entity
@Table(name = "YB_LEDGER_TRANSFER")
public class LedgerTransfer {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Date dateCreated = new Date();

  private Date dateCompleted;  //转账成功的时间

  /**
   * 如果一次转账失败，需要给相关业务人员发送一条提醒短信。
   */
  private Integer state = 0;   //转账状态 0=待转账，1=转账成功，2=转账失败，3=转账中（查询非终态）

  private Integer type = 1;  //转账类型 1=交易实时转账，2=定时合并转账

  private Integer repair = 0;  //是否补单 1=是（此时一个业务转账单对应多个转账记录）

  @Column(nullable = false, length = 30)
  private String orderSid = MvUtil.getOrderNumber(10); //转账单号(非定时转账为关联的订单号，定时转账自己生成)

  @Column(nullable = false, length = 10)
  private String tradeDate; //清算日期（对应的交易记录是哪一天完成的）（yyyy-MM-dd）

  @Column(nullable = false, length = 50)
  private String ledgerNo;  //易宝的子商户号

  private Long actualTransfer = 0L;   //转账金额

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

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getOrderSid() {
    return orderSid;
  }

  public void setOrderSid(String orderSid) {
    this.orderSid = orderSid;
  }

  public String getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(String tradeDate) {
    this.tradeDate = tradeDate;
  }

  public String getLedgerNo() {
    return ledgerNo;
  }

  public void setLedgerNo(String ledgerNo) {
    this.ledgerNo = ledgerNo;
  }

  public Long getActualTransfer() {
    return actualTransfer;
  }

  public void setActualTransfer(Long actualTransfer) {
    this.actualTransfer = actualTransfer;
  }

  public Date getDateCompleted() {
    return dateCompleted;
  }

  public void setDateCompleted(Date dateCompleted) {
    this.dateCompleted = dateCompleted;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Integer getRepair() {
    return repair;
  }

  public void setRepair(Integer repair) {
    this.repair = repair;
  }
}
