package com.jifenke.lepluslive.score.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.domain.entities.ScoreADetail;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.score.repository.ScoreADetailRepository;
import com.jifenke.lepluslive.score.repository.ScoreARepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by wcg on 16/3/18.
 */
@Service
@Transactional(readOnly = true)
public class ScoreAService {

  @Inject
  private ScoreARepository scoreARepository;

  @Inject
  private ScoreADetailRepository scoreADetailRepository;


  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ScoreA findScoreAByLeJiaUser(LeJiaUser leJiaUser) {
    return scoreARepository.findByLeJiaUser(leJiaUser);
  }

  public List<ScoreADetail> findAllScoreADetail(WeiXinUser weiXinUser) {
    return scoreADetailRepository.findAllByScoreA(findScoreAByLeJiaUser(weiXinUser.getLeJiaUser()));
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(LeJiaUser leJiaUser, Long totalPrice, String orderSid) {
    ScoreA scoreA = findScoreAByLeJiaUser(leJiaUser);
    Long payBackScore = (long) Math.ceil((double) (totalPrice * 12) / 100);
    scoreA.setScore(scoreA.getScore() + payBackScore);
    scoreA.setTotalScore(scoreA.getTotalScore() + payBackScore);
    ScoreADetail scoreADetail = new ScoreADetail();
    scoreADetail.setOperate("乐+商城返红包");
    scoreADetail.setOrigin(1);
    scoreADetail.setOrderSid(orderSid);
    scoreADetail.setScoreA(scoreA);
    scoreADetail.setNumber(payBackScore);
    scoreADetailRepository.save(scoreADetail);
    scoreARepository.save(scoreA);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ScoreADetail findScoreADetailByOrderSid(String orderSid) {
    return scoreADetailRepository.findOneByOrderSid(orderSid);
  }

  /**
   * 根据scoreA查询红包明细列表
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<ScoreADetail> findAllScoreADetailByScoreA(ScoreA scoreA) {
    return scoreADetailRepository.findAllByScoreA(scoreA);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccessForMember(OffLineOrder offLineOrder) {
    ScoreA scoreA = findScoreAByLeJiaUser(offLineOrder.getLeJiaUser());
      if (scoreA.getScore() - offLineOrder.getTrueScore() >= 0) {
        scoreA.setScore(scoreA.getScore() - offLineOrder.getTrueScore() + offLineOrder.getRebate());
        scoreA.setTotalScore(scoreA.getTotalScore() + offLineOrder.getRebate());
        if (offLineOrder.getTrueScore() != 0) {
          ScoreADetail scoreADetail = new ScoreADetail();
          scoreADetail.setOperate(offLineOrder.getMerchant().getName() + "消费");
          scoreADetail.setOrigin(3);
          scoreADetail.setOrderSid(offLineOrder.getOrderSid());
          scoreADetail.setScoreA(scoreA);
          scoreADetail.setNumber(-offLineOrder.getTrueScore());
          scoreADetailRepository.save(scoreADetail);
        }
      if (offLineOrder.getRebate() != 0) {
        ScoreADetail rebate = new ScoreADetail();
        rebate.setOperate(offLineOrder.getMerchant().getName() + "消费返红包");
        rebate.setOrigin(4);
        rebate.setOrderSid(offLineOrder.getOrderSid());
        rebate.setScoreA(scoreA);
        rebate.setNumber(offLineOrder.getRebate());
        scoreADetailRepository.save(rebate);
      }
      scoreARepository.save(scoreA);
    } else {
      throw new RuntimeException();
    }

  }
}
