package com.jifenke.lepluslive.merchant.domain.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/3/16. 商户(非门店) 储值活动
 */
@Entity
@Table(name = "MERCHANT_STORED_ACTIVITY")
public class MerchantStoredActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne
  private MerchantUser merchantUser; //对应商户

  private Integer commissionPolicy; //佣金收取策略 0 储值订单全收佣金 1 导流会员才收取佣金

  private BigDecimal commonRate; //普通费率

  private Long totalStored = 0L; //累计卖出的储值

  private Long totalCommission; //累计佣金

  private Date createdDate;

  private Integer state; //0 关闭 1 开启

  private Integer typeOfSettlements; //结算类型

  private String transferCard; //转账卡号

  private String name;//转账姓名

  private String BankName;

  private String memberBenefit; //会员权益文案

  private String extraDocument; //额外文案

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MerchantUser getMerchantUser() {
    return merchantUser;
  }

  public void setMerchantUser(MerchantUser merchantUser) {
    this.merchantUser = merchantUser;
  }

  public Integer getCommissionPolicy() {
    return commissionPolicy;
  }

  public void setCommissionPolicy(Integer commissionPolicy) {
    this.commissionPolicy = commissionPolicy;
  }

  public BigDecimal getCommonRate() {
    return commonRate;
  }

  public void setCommonRate(BigDecimal commonRate) {
    this.commonRate = commonRate;
  }

  public Long getTotalStored() {
    return totalStored;
  }

  public void setTotalStored(Long totalStored) {
    this.totalStored = totalStored;
  }

  public Long getTotalCommission() {
    return totalCommission;
  }

  public void setTotalCommission(Long totalCommission) {
    this.totalCommission = totalCommission;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Integer getTypeOfSettlements() {
    return typeOfSettlements;
  }

  public void setTypeOfSettlements(Integer typeOfSettlements) {
    this.typeOfSettlements = typeOfSettlements;
  }

  public String getTransferCard() {
    return transferCard;
  }

  public void setTransferCard(String transferCard) {
    this.transferCard = transferCard;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBankName() {
    return BankName;
  }

  public void setBankName(String bankName) {
    BankName = bankName;
  }

  public String getMemberBenefit() {
    return memberBenefit;
  }

  public void setMemberBenefit(String memberBenefit) {
    this.memberBenefit = memberBenefit;
  }

  public String getExtraDocument() {
    return extraDocument;
  }

  public void setExtraDocument(String extraDocument) {
    this.extraDocument = extraDocument;
  }
}
