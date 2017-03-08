package com.jifenke.lepluslive.score.service;

import com.jifenke.lepluslive.global.abstraction.Order;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.printer.service.PrinterService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.domain.entities.ScoreADetail;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;
import com.jifenke.lepluslive.score.domain.entities.ScoreCDetail;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.score.repository.ScoreADetailRepository;
import com.jifenke.lepluslive.score.repository.ScoreARepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

  @Inject
  private PrinterService printerService;


  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ScoreA findScoreAByLeJiaUser(LeJiaUser leJiaUser) {
    return scoreARepository.findByLeJiaUser(leJiaUser);
  }

  /**
   * 保存红包账户  17/2/20
   *
   * @param scoreA 红包账户
   * @param type   1=增加|0=减少
   * @param val    增加或减少的红包
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveScoreA(ScoreA scoreA, int type, Long val) throws Exception {
    Date date = new Date();
    scoreA.setLastUpdateDate(date);
    try {
      if (type == 1) {
        scoreA.setScore(scoreA.getScore() + val);
        scoreA.setTotalScore(scoreA.getTotalScore() + val);
      } else {
        scoreA.setScore(scoreA.getScore() - val);
      }
      scoreARepository.save(scoreA);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  /**
   * 添加用户红包变动明细   2017/2/20
   *
   * @param scoreA   红包账户
   * @param state    1=加|0=减
   * @param number   更改红包的数额
   * @param origin   变动来源
   * @param operate  变动文字描述
   * @param orderSid 对应的订单号(可为空)
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveScoreCDetail(ScoreA scoreA, Integer state, Long number, Integer origin,
                               String operate,
                               String orderSid) throws Exception {
    try {
      ScoreADetail detail = new ScoreADetail();
      detail.setOperate(operate);
      detail.setOrigin(origin);
      detail.setOrderSid(orderSid);
      detail.setScoreA(scoreA);
      if (state == 0) {
        detail.setNumber(-number);
      } else {
        detail.setNumber(number);
      }
      scoreADetailRepository.save(detail);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
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

  /**
   * 根据scoreA查询红包明细列表
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<ScoreADetail> findAllScoreADetailByScoreA(ScoreA scoreA) {
    return scoreADetailRepository.findAllByScoreA(scoreA);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccessForMember(Order order) {
    ScoreA scoreA = findScoreAByLeJiaUser(order.getLeJiaUser());
    if (scoreA.getScore() - order.getTrueScore() >= 0) {
      scoreA.setScore(scoreA.getScore() - order.getTrueScore() + order.getRebate());
      scoreA.setTotalScore(scoreA.getTotalScore() + order.getRebate());
      if (order.getTrueScore() != 0) {
        ScoreADetail scoreADetail = new ScoreADetail();
        scoreADetail.setOperate(order.getMerchant().getName() + "消费");
        scoreADetail.setOrigin(3);
        scoreADetail.setOrderSid(order.getOrderSid());
        scoreADetail.setScoreA(scoreA);
        scoreADetail.setNumber(-order.getTrueScore());
        scoreADetailRepository.save(scoreADetail);
      }
      if (order.getRebate() != 0) {
        ScoreADetail rebate = new ScoreADetail();
        rebate.setOperate(order.getMerchant().getName() + "消费返红包");
        rebate.setOrigin(4);
        rebate.setOrderSid(order.getOrderSid());
        rebate.setScoreA(scoreA);
        rebate.setNumber(order.getRebate());
        scoreADetailRepository.save(rebate);
      }
      scoreARepository.save(scoreA);
    } else {
      throw new RuntimeException();
    }
  }

  /**
   * 是否添加过这个订单的红包  2017/02/27
   */
  public int findByScoreAAndOriginAndOrderSid(ScoreA scoreA, Integer origin,
                                              String orderSid) {
    List<ScoreADetail>
        list =
        scoreADetailRepository.findByScoreAAndOriginAndOrderSid(scoreA, origin, orderSid);
    if (list == null) {
      return 0;
    } else {
      return 1;
    }
  }
}
