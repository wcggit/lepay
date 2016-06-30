package com.jifenke.lepluslive.partner.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/6/3. 合伙人表
 */
@Entity
@Table(name = "PARTNER")
public class Partner {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  private String name;


  private Long userLimit = 0L;

  @ManyToOne
  private PartnerManager partnerManager;


  public PartnerManager getPartnerManager() {
    return partnerManager;
  }

  public void setPartnerManager(PartnerManager partnerManager) {
    this.partnerManager = partnerManager;
  }

  public Long getUserLimit() {
    return userLimit;
  }

  public void setUserLimit(Long userLimit) {
    this.userLimit = userLimit;
  }

  public Partner(Long id) {
    this.id = id;
  }

  public Partner() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
