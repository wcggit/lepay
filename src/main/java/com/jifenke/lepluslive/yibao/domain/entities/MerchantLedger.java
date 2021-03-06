package com.jifenke.lepluslive.yibao.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 门店对应选择的子商户
 * Created by zhangwen on 2017/7/11.
 */
@Entity
@Table(name = "YB_MERCHANT_LEDGER")
public class MerchantLedger {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Date dateCreated = new Date();

  @OneToOne(fetch = FetchType.LAZY)
  @NotNull
  private Merchant merchant;

  @ManyToOne
  @NotNull
  private MerchantUserLedger merchantUserLedger;

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

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public MerchantUserLedger getMerchantUserLedger() {
    return merchantUserLedger;
  }

  public void setMerchantUserLedger(
      MerchantUserLedger merchantUserLedger) {
    this.merchantUserLedger = merchantUserLedger;
  }
}
