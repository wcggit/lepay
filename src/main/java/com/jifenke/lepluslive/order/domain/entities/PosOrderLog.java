package com.jifenke.lepluslive.order.domain.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wcg on 16/8/4.
 */
@Entity
@Table(name = "POS_ORDER_LOG")
public class PosOrderLog {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  private Date createDate = new Date();

  private String orderSid;

  private String params;

  public String getOrderSid() {
    return orderSid;
  }

  public void setOrderSid(String orderSid) {
    this.orderSid = orderSid;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }
}
