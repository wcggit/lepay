package com.jifenke.lepluslive.lejiauser.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.crypto.Data;

/**
 * 注册来源|没有就创建 Created by wcg on 16/4/22.
 */
@Entity
@Table(name = "REGISTER_ORiGIN")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisterOrigin {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private Merchant merchant;

  private Integer originType; // 0=微信注册|1=app注册2=商户注册|3=线下支付完成页

  private Date dateCreated = new Date();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Integer getOriginType() {
    return originType;
  }

  public void setOriginType(Integer originType) {
    this.originType = originType;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }
}
