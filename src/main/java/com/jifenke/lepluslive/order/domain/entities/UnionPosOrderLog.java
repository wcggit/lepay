package com.jifenke.lepluslive.order.domain.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wcg on 16/8/9.
 */
@Entity
@Table(name = "UNION_POS_ORDER_LOG")
public class UnionPosOrderLog {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  private Date createDate = new Date();

  private String msgSysSn;//平台流水号

  private String reqSerialNo;//流水号

  private String params; //参数字符串

  private String returnParams; //返回参数

  private Integer interfaceType; //接口类型 1,查询接口 2,销账接口,3冲正接口 4 撤销接口


  public String getReqSerialNo() {
    return reqSerialNo;
  }

  public void setReqSerialNo(String reqSerialNo) {
    this.reqSerialNo = reqSerialNo;
  }

  public Integer getInterfaceType() {
    return interfaceType;
  }

  public void setInterfaceType(Integer interfaceType) {
    this.interfaceType = interfaceType;
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

  public String getMsgSysSn() {
    return msgSysSn;
  }

  public void setMsgSysSn(String msgSysSn) {
    this.msgSysSn = msgSysSn;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getReturnParams() {
    return returnParams;
  }

  public void setReturnParams(String returnParams) {
    this.returnParams = returnParams;
  }
}
