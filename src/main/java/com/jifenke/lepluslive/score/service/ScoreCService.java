package com.jifenke.lepluslive.score.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;
import com.jifenke.lepluslive.score.domain.entities.ScoreCDetail;
import com.jifenke.lepluslive.score.repository.ScoreCDetailRepository;
import com.jifenke.lepluslive.score.repository.ScoreCRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.inject.Inject;

/**
 * Created by wcg on 16/3/18.
 */
@Service
@Transactional(readOnly = true)
public class ScoreCService {

  @Inject
  private ScoreCRepository scoreCRepository;

  @Inject
  private ScoreCDetailRepository scoreCDetailRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ScoreC findScoreCByleJiaUser(LeJiaUser leJiaUser) {
    return scoreCRepository.findByLeJiaUser(leJiaUser);
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(OffLineOrder offLineOrder) {
    if (offLineOrder.getScoreC() > 0) {
      ScoreC scoreC = findScoreCByleJiaUser(offLineOrder.getLeJiaUser());
      scoreC.setTotalScore(scoreC.getTotalScore() + offLineOrder.getScoreC());
      ScoreCDetail scoreCDetail = new ScoreCDetail();
      scoreCDetail.setOperate(offLineOrder.getMerchant().getName() + "消费返积分");
      scoreCDetail.setOrigin(4);
      scoreCDetail.setOrderSid(offLineOrder.getOrderSid());
      scoreCDetail.setScoreC(scoreC);
      scoreCDetail.setNumber(offLineOrder.getScoreC());
      scoreCDetailRepository.save(scoreCDetail);
      scoreCRepository.save(scoreC);
    }

  }

}
