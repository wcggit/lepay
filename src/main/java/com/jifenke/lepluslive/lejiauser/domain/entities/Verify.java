package com.jifenke.lepluslive.lejiauser.domain.entities;

import com.jifenke.lepluslive.global.util.MvUtil;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zhangwen on 2017/6/29.
 */
@Entity
@Table(name = "VERIFY")
public class Verify {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Date dateCreated = new Date();

  private Date dateComplated;

  private Integer pageType;   //发送验证码页面类型   category.id

  private Long userId;

  private String pageSid = MvUtil.getRandomNumber(20);

  private Integer state = 0;

  private String phone;

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDateComplated() {
    return dateComplated;
  }

  public void setDateComplated(Date dateComplated) {
    this.dateComplated = dateComplated;
  }

  public String getPageSid() {
    return pageSid;
  }

  public void setPageSid(String pageSid) {
    this.pageSid = pageSid;
  }

  public Integer getPageType() {
    return pageType;
  }

  public void setPageType(Integer pageType) {
    this.pageType = pageType;
  }
}
