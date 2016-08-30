package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
import com.jifenke.lepluslive.merchant.service.MerchantPosService;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.order.domain.entities.PosOrder;
import com.jifenke.lepluslive.order.domain.entities.PosOrderLog;
import com.jifenke.lepluslive.order.repository.PosOrderLogRepository;
import com.jifenke.lepluslive.order.repository.PosOrderRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/4.
 */
@Service
public class PosOrderService {

  @Inject
  private PosOrderRepository posOrderRepository;

  @Inject
  private MerchantPosService merchantPosService;

  @Inject
  private PosOrderLogRepository posOrderLogRepository;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private OrderShareService orderShareService;


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderForNoNMember(String posId, String orderNo,
                                         String orderTime, String orderPrice) {
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    if (merchantPos != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      PosOrder posOrder = new PosOrder();
      posOrder.setMerchantPos(merchantPos);
      try {
        posOrder.setCreatedDate(sdf.parse(orderTime));
        posOrder.setOrderSid(orderNo);
        long price = new BigDecimal(orderPrice).multiply(new BigDecimal(100)).longValue();
        posOrder.setTotalPrice(price);
        posOrder.setPaidType(1);
        posOrder.setMerchant(merchantPos.getMerchant());
        posOrder.setTruePay(price);
        posOrder.setRebateWay(1);
        posOrderRepository.save(posOrder);
      } catch (Exception e) {
      }
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void nonMemberAfterPay(String posId, String orderNo, String paidTime, String orderPrice,
                                String paidPoints, String paidMoney, Integer tradeFlag) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    PosOrder posOrder = posOrderRepository.findByOrderSid(orderNo);
    posOrder.setState(1);
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    BigDecimal ljCommission;
    try {
      posOrder.setCompleteDate(sdf.parse(paidTime));

      if (tradeFlag == 0) {//支付宝
        ljCommission =
            merchantPos.getAliCommission().multiply(new BigDecimal(paidMoney));
        posOrder.setTradeFlag(0);
      } else if (tradeFlag == 3) { //刷卡
        posOrder.setTradeFlag(3);
        ljCommission =
            merchantPos.getPosCommission().multiply(new BigDecimal(paidMoney));
        if (merchantPos.getType() == 1) {//封顶pos
          if (ljCommission.longValue() >= merchantPos.getCeil()) {
            ljCommission = new BigDecimal(merchantPos.getCeil());
          }
        }
      } else if (tradeFlag == 4) { //微信
        posOrder.setTradeFlag(4);
        ljCommission =
            merchantPos.getWxCommission().multiply(new BigDecimal(paidMoney));
      } else { //现金
        posOrder.setTradeFlag(6);
        ljCommission = new BigDecimal(0);
      }
      posOrder
          .setLjCommission(Math.round(ljCommission.multiply(new BigDecimal(100)).doubleValue()));
      posOrder.setWxCommission(posOrder.getLjCommission());
      posOrder.setTransferMoney(new BigDecimal(paidMoney).multiply(new BigDecimal(100))
                                    .subtract(ljCommission).longValue());
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderLog(String orderSid, String params) {
    PosOrderLog posOrderlog = new PosOrderLog();
    posOrderlog.setOrderSid(orderSid);
    posOrderlog.setParams(params);
    posOrderLogRepository.save(posOrderlog);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderForMember(MerchantPos merchantPos, String orderNo, String orderPrice,
                                      String orderTime, LeJiaUser leJiaUser) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    PosOrder posOrder = new PosOrder();
    posOrder.setMerchantPos(merchantPos);
    try {
      posOrder.setCreatedDate(sdf.parse(orderTime));
      posOrder.setOrderSid(orderNo);
      long price = new BigDecimal(orderPrice).multiply(new BigDecimal(100)).longValue();
      posOrder.setTotalPrice(price);
      posOrder.setMerchant(merchantPos.getMerchant());
      posOrder.setLeJiaUser(leJiaUser);
      posOrderRepository.save(posOrder);
    } catch (Exception e) {
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public PosOrder memberAfterPay(String posId, String orderNo, String paidTime, String orderPrice,
                                 String paidPoints, String paidMoney, Integer tradeFlag,
                                 Integer paidType, String cardNo) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    PosOrder posOrder = posOrderRepository.findByOrderSid(orderNo);
    BigDecimal truePay = new BigDecimal(paidMoney).multiply(new BigDecimal(100));
    BigDecimal scoreA = new BigDecimal(paidPoints);
    BigDecimal totalPrice = new BigDecimal(orderPrice).multiply(new BigDecimal(100));
    posOrder.setState(1);
    posOrder.setTruePay(truePay.longValue());
    posOrder.setTrueScore(scoreA.longValue());
    posOrder.setTradeFlag(tradeFlag);
    posOrder.setPaidType(paidType);
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    Merchant merchant = merchantPos.getMerchant();
    try {
      BigDecimal thirdCommission = null;
      BigDecimal ljCommission = null;
      posOrder.setCompleteDate(sdf.parse(paidTime));
      posOrder.setScoreB(
          Math.round(totalPrice.multiply(merchant.getScoreBRebate()).divide(new BigDecimal(10000))
                         .doubleValue()));
      if (tradeFlag == 0) {//支付宝
        posOrder.setRebateWay(2);
        thirdCommission =
            merchantPos.getAliCommission().multiply(truePay);
        ljCommission = thirdCommission;
      } else if (tradeFlag == 3) { //刷卡
        posOrder.setRebateWay(3);
        ljCommission = totalPrice.multiply(merchantPos.getLjCommission());
        thirdCommission =
            merchantPos.getPosCommission().multiply(truePay);
        posOrder.setRebate(
            Math.round(
                ljCommission.multiply(merchant.getScoreARebate().divide(new BigDecimal(10000)))
                    .doubleValue()));
      } else if (tradeFlag == 4) { //微信
        posOrder.setRebateWay(2);
        thirdCommission =
            merchantPos.getWxCommission().multiply(truePay);
        ljCommission = thirdCommission;
      } else if (tradeFlag == 5) { //纯积分
        posOrder.setRebateWay(3);
        ljCommission = totalPrice.multiply(merchantPos.getLjCommission());
        thirdCommission =
            new BigDecimal(0);
        posOrder.setRebate(Math.round(ljCommission.multiply(merchant.getScoreARebate().divide(
            new BigDecimal(10000))).doubleValue()));
      }
      posOrder
          .setWxCommission(Math.round(thirdCommission.divide(new BigDecimal(100)).doubleValue()));
      posOrder
          .setLjCommission(Math.round(ljCommission.divide(new BigDecimal(100)).doubleValue()));
      posOrder.setTransferMoney(
          Math.round(totalPrice.subtract(ljCommission.divide(new BigDecimal(100))).doubleValue()));
      if (tradeFlag != 3) { //只有刷卡消费时,由银行转给商户的钱为实际支付减去佣金
        posOrder.setTransferByBank(
            Math.round(
                truePay.subtract(thirdCommission.divide(new BigDecimal(100))).doubleValue()));
      } else {
        posOrder.setTransferByBank(
            Math.round(truePay.subtract(
                truePay.multiply(merchantPos.getLjCommission()).divide(new BigDecimal(100)))
                           .doubleValue()));
      }

      leJiaUserService.checkUserBindCard(posOrder.getLeJiaUser(), cardNo);
      scoreAService.paySuccessForMember(posOrder);
      scoreBService.paySuccess(posOrder);
      orderShareService.offLIneOrderShare(posOrder);
      leJiaUserService
          .checkUserBindMerchant(posOrder.getLeJiaUser(), posOrder.getMerchant());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return posOrder;
  }
}
