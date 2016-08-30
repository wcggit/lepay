package com.jifenke.lepluslive.order.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.partner.domain.entities.Partner;
import com.jifenke.lepluslive.partner.domain.entities.PartnerManager;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/6/22.
 */
@Entity
@Table(name = "OFF_LINE_ORDER_SHARE")
public class OffLineOrderShare {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne
  private OffLineOrder offLineOrder;

  @OneToOne
  private PosOrder posOrder;

  private Integer type; //1 代表线下订单分润 2代表中慧pos订单分润

  private Long shareMoney;

  private Long toTradePartner = 0L;

  private Long toTradePartnerManager = 0L;

  private Long toLockMerchant = 0L;

  private Long toLockPartner = 0L;

  private Long toLockPartnerManager = 0L;

  private Long toLePlusLife = 0L;

  private Date createDate = new Date();

  @ManyToOne
  private Partner tradePartner;

  @ManyToOne
  private PartnerManager tradePartnerManager;

  @ManyToOne
  private Merchant tradeMerchant;

  @ManyToOne
  private Merchant lockMerchant;

  @ManyToOne
  private Partner lockPartner;

  @ManyToOne
  private PartnerManager lockPartnerManager;

  public PosOrder getPosOrder() {
    return posOrder;
  }

  public void setPosOrder(PosOrder posOrder) {
    this.posOrder = posOrder;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Merchant getTradeMerchant() {
    return tradeMerchant;
  }

  public void setTradeMerchant(Merchant tradeMerchant) {
    this.tradeMerchant = tradeMerchant;
  }

  public Long getShareMoney() {
    return shareMoney;
  }

  public void setShareMoney(Long shareMoney) {
    this.shareMoney = shareMoney;
  }

  public OffLineOrder getOffLineOrder() {
    return offLineOrder;
  }

  public void setOffLineOrder(OffLineOrder offLineOrder) {
    this.offLineOrder = offLineOrder;
  }

  public Partner getTradePartner() {
    return tradePartner;
  }

  public void setTradePartner(Partner tradePartner) {
    this.tradePartner = tradePartner;
  }

  public PartnerManager getTradePartnerManager() {
    return tradePartnerManager;
  }

  public void setTradePartnerManager(PartnerManager tradePartnerManager) {
    this.tradePartnerManager = tradePartnerManager;
  }

  public Merchant getLockMerchant() {
    return lockMerchant;
  }

  public void setLockMerchant(Merchant lockMerchant) {
    this.lockMerchant = lockMerchant;
  }

  public Partner getLockPartner() {
    return lockPartner;
  }

  public void setLockPartner(Partner lockPartner) {
    this.lockPartner = lockPartner;
  }

  public PartnerManager getLockPartnerManager() {
    return lockPartnerManager;
  }

  public void setLockPartnerManager(PartnerManager lockPartnerManager) {
    this.lockPartnerManager = lockPartnerManager;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Long getToLePlusLife() {
    return toLePlusLife;
  }

  public void setToLePlusLife(Long toLePlusLife) {
    this.toLePlusLife = toLePlusLife;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public Long getToTradePartner() {
    return toTradePartner;
  }

  public void setToTradePartner(Long toTradePartner) {
    this.toTradePartner = toTradePartner;
  }

  public Long getToTradePartnerManager() {
    return toTradePartnerManager;
  }

  public void setToTradePartnerManager(Long toTradePartnerManager) {
    this.toTradePartnerManager = toTradePartnerManager;
  }

  public Long getToLockMerchant() {
    return toLockMerchant;
  }

  public void setToLockMerchant(Long toLockMerchant) {
    this.toLockMerchant = toLockMerchant;
  }

  public Long getToLockPartner() {
    return toLockPartner;
  }

  public void setToLockPartner(Long toLockPartner) {
    this.toLockPartner = toLockPartner;
  }

  public Long getToLockPartnerManager() {
    return toLockPartnerManager;
  }

  public void setToLockPartnerManager(Long toLockPartnerManager) {
    this.toLockPartnerManager = toLockPartnerManager;
  }
}