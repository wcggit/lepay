package com.jifenke.lepluslive.merchant.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/5/13.
 */
@Entity
@Table(name = "MERCHANT_WALLET")
public class MerchantWallet {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long transfersMoney = 0L;

  private Long totalTransferMoney = 0L;

  @OneToOne
  private Merchant merchant;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTransfersMoney() {
    return transfersMoney;
  }

  public void setTransfersMoney(Long transfersMoney) {
    this.transfersMoney = transfersMoney;
  }

  public Long getTotalTransferMoney() {
    return totalTransferMoney;
  }

  public void setTotalTransferMoney(Long totalTransferMoney) {
    this.totalTransferMoney = totalTransferMoney;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }
}
