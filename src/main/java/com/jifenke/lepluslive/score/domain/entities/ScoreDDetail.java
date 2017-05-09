package com.jifenke.lepluslive.score.domain.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 储值记录 Created by wcg on 17/02/17.
 */
@Entity
@Table(name = "SCORED_DETAIL")
public class ScoreDDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long number = 0L; //消费金额 正代表充值 负代表消费

  private String operate;

  private Date dateCreated = new Date();

  @ManyToOne
  private ScoreD scoreD;

  private Long beforeChange; //改变前的数值

  private Long afterChange;  //改变后的数值

  private Integer origin;  //0 线上充值 1 线下消费

  private String orderSid;  //对应的订单号

  public ScoreD getScoreD() {
    return scoreD;
  }

  public void setScoreD(ScoreD scoreD) {
    this.scoreD = scoreD;
  }

  public Long getBeforeChange() {
    return beforeChange;
  }

  public void setBeforeChange(Long beforeChange) {
    this.beforeChange = beforeChange;
  }

  public Long getAfterChange() {
    return afterChange;
  }

  public void setAfterChange(Long afterChange) {
    this.afterChange = afterChange;
  }

  public Integer getOrigin() {
    return origin;
  }

  public void setOrigin(Integer origin) {
    this.origin = origin;
  }

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

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public String getOperate() {
    return operate;
  }

  public void setOperate(String operate) {
    this.operate = operate;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }
}
