package com.jifenke.lepluslive.activity.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/9/12.
 */
@Entity
@Table(name = "INITIAL_ORDER_REBATE_ACTIVITY_LOG")
public class InitialOrderRebateActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  @OneToOne
  private OffLineOrder offLineOrder;

  @ManyToOne
  private Merchant merchant;

  private String nickname; //防止店主微信号变化 每笔消费记录店主号

  private String headImageUrl;

  private Integer state;  // 完成状态

  private Long rebate; //返利金额

  private String exceptionLog; //如果未发放,说明原因

  @Column(columnDefinition = "LONGTEXT")
  private String detailResponse; //详细返回信息

  private Date createdDate;

  public String getDetailResponse() {
    return detailResponse;
  }

  public void setDetailResponse(String detailResponse) {
    this.detailResponse = detailResponse;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OffLineOrder getOffLineOrder() {
    return offLineOrder;
  }

  public void setOffLineOrder(OffLineOrder offLineOrder) {
    this.offLineOrder = offLineOrder;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getHeadImageUrl() {
    return headImageUrl;
  }

  public void setHeadImageUrl(String headImageUrl) {
    this.headImageUrl = headImageUrl;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Long getRebate() {
    return rebate;
  }

  public void setRebate(Long rebate) {
    this.rebate = rebate;
  }

  public String getExceptionLog() {
    return exceptionLog;
  }

  public void setExceptionLog(String exceptionLog) {
    this.exceptionLog = exceptionLog;
  }
}
