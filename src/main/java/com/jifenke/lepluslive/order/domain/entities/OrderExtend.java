package com.jifenke.lepluslive.order.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/4/12. 订单扩展表,用来表示订单的额外信息,比如储值的使用,优惠券等
 */
@Entity
@Table(name = "ORDER_EXTEND")
public class OrderExtend {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String json; //对应的额外信息json, 比如orderStored

  private Integer orderType; //对应的订单类型 0 线下订单

  private Integer privilegeType; //优惠类型 0 储值

  private Long orderId; //对应的订单Id

  private String orderSid; // 对应订单的sid

  private Integer state; // 0 未支付状态 1 已支付

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  public Integer getOrderType() {
    return orderType;
  }

  public void setOrderType(Integer orderType) {
    this.orderType = orderType;
  }

  public Integer getPrivilegeType() {
    return privilegeType;
  }

  public void setPrivilegeType(Integer privilegeType) {
    this.privilegeType = privilegeType;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getOrderSid() {
    return orderSid;
  }

  public void setOrderSid(String orderSid) {
    this.orderSid = orderSid;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }
}
