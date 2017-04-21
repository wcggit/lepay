package com.jifenke.lepluslive.score.domain.entities;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 2017/3/15. 储值账户,与用户,商户对应
 */
@Entity
@Table(name = "SCORED")
public class ScoreD {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long score = 0L; //可用余额，单位分

  @OneToOne
  private LeJiaUser leJiaUser;

  @OneToOne
  private MerchantUser merchantUser;

  private Long totalScore = 0L; // 累计储值

  private Long totalPay = 0L;// 累计实付金额

  private Long totalScoreC = 0L;//总返c积分值 储值消费会带来c积分的改变

  private Long
      scoreCToUser =
      0L;
      //已经返c积分值 对于每笔储值订单储值部分发放金币值 = (totalScoreC-scoreCToUser)* 使用储值金额／当前可用储值余额

  private Long totalShareMoney = 0L; //总分润值

  private Long sharedMoney = 0L; //已经分润的金额 

  private Date createdDate = new Date();

  public Long getTotalShareMoney() {
    return totalShareMoney;
  }

  public void setTotalShareMoney(Long totalShareMoney) {
    this.totalShareMoney = totalShareMoney;
  }

  public Long getSharedMoney() {
    return sharedMoney;
  }

  public void setSharedMoney(Long sharedMoney) {
    this.sharedMoney = sharedMoney;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getScore() {
    return score;
  }

  public void setScore(Long score) {
    this.score = score;
  }

  public LeJiaUser getLeJiaUser() {
    return leJiaUser;
  }

  public void setLeJiaUser(LeJiaUser leJiaUser) {
    this.leJiaUser = leJiaUser;
  }

  public MerchantUser getMerchantUser() {
    return merchantUser;
  }

  public void setMerchantUser(MerchantUser merchantUser) {
    this.merchantUser = merchantUser;
  }

  public Long getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Long totalScore) {
    this.totalScore = totalScore;
  }

  public Long getTotalPay() {
    return totalPay;
  }

  public void setTotalPay(Long totalPay) {
    this.totalPay = totalPay;
  }

  public Long getTotalScoreC() {
    return totalScoreC;
  }

  public void setTotalScoreC(Long totalScoreC) {
    this.totalScoreC = totalScoreC;
  }

  public Long getScoreCToUser() {
    return scoreCToUser;
  }

  public void setScoreCToUser(Long scoreCToUser) {
    this.scoreCToUser = scoreCToUser;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
}
