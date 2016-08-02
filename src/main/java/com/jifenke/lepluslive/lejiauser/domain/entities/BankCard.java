package com.jifenke.lepluslive.lejiauser.domain.entities;

import com.jifenke.lepluslive.global.util.LejiaResult;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/8/2.
 */
@Entity
@Table(name = "BANK_CARD")
public class BankCard {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private LeJiaUser leJiaUser;

  private Date bindDate;

  private String number;

  private String bankName;

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LeJiaUser getLeJiaUser() {
    return leJiaUser;
  }

  public void setLeJiaUser(LeJiaUser leJiaUser) {
    this.leJiaUser = leJiaUser;
  }

  public Date getBindDate() {
    return bindDate;
  }

  public void setBindDate(Date bindDate) {
    this.bindDate = bindDate;
  }
}
