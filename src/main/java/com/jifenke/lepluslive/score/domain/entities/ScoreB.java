package com.jifenke.lepluslive.score.domain.entities;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by wcg on 16/3/18.
 */
@Entity
@Table(name = "SCOREB")
@EntityListeners(AuditingEntityListener.class)
public class ScoreB {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long score=0L;

  @OneToOne
  private LeJiaUser leJiaUser;

  private Date createdDate = new Date();

  private Long totalScore = 0L;

  @LastModifiedDate
  private Date lastUpdateDate;

  public Long getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Long totalScore) {
    this.totalScore = totalScore;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getLastUpdateDate() {
    return lastUpdateDate;
  }

  public void setLastUpdateDate(Date lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
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
}
