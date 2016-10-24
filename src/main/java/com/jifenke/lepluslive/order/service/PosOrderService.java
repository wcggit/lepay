package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.global.util.PosCardCheckUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
import com.jifenke.lepluslive.merchant.service.MerchantPosService;
import com.jifenke.lepluslive.order.domain.entities.PosOrder;
import com.jifenke.lepluslive.order.domain.entities.PosOrderLog;
import com.jifenke.lepluslive.order.repository.PosOrderLogRepository;
import com.jifenke.lepluslive.order.repository.PosOrderRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

  @Inject
  private DictionaryService dictionaryService;


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
                                String paidPoints, String paidMoney, Integer tradeFlag,
                                String cardNo) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    PosOrder posOrder = posOrderRepository.findByOrderSid(orderNo);
    posOrder.setState(1);
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    try {
      BigDecimal ljCommission = null;
      BigDecimal thirdCommission = null;
      posOrder.setCompleteDate(sdf.parse(paidTime));
      posOrder.setTradeFlag(tradeFlag);
      if (tradeFlag == 0) {//支付宝
        ljCommission =
            merchantPos.getAliCommission().multiply(new BigDecimal(paidMoney));
        thirdCommission =
            new BigDecimal(dictionaryService.findDictionaryById(42L).getValue())
                .multiply(new BigDecimal(paidMoney));
      } else if (tradeFlag == 3) { //刷卡
        String httpUrl = "http://apis.baidu.com/datatiny/cardinfo/cardinfo";
        String httpArg = "cardnum=" + cardNo;
        String request = PosCardCheckUtil.request(httpUrl, httpArg);
        posOrder.setCardNo(cardNo);
        if (request.indexOf("贷") != -1) {
          posOrder.setCardType(1);
          ljCommission =
              merchantPos.getCreditCardCommission().multiply(new BigDecimal(paidMoney));
          thirdCommission =
              new BigDecimal(dictionaryService.findDictionaryById(46L).getValue())
                  .multiply(new BigDecimal(paidMoney));
        } else if (request.indexOf("借") != -1) {
          posOrder.setCardType(0);
          ljCommission =
              merchantPos.getDebitCardCommission().multiply(new BigDecimal(paidMoney));
          if (ljCommission.longValue() > merchantPos.getCeil()) {//封顶手续费
            ljCommission = new BigDecimal(merchantPos.getCeil());
          }
          thirdCommission =
              new BigDecimal(dictionaryService.findDictionaryById(45L).getValue())
                  .multiply(new BigDecimal(paidMoney));
          BigDecimal thirdCommissionLimit = new BigDecimal(
              dictionaryService.findDictionaryById(47L).getValue());
          if (thirdCommission.longValue() > thirdCommissionLimit.longValue()) { //封顶第三方手续费
            thirdCommission = thirdCommissionLimit;
          }
        } else {
          posOrder.setCardType(2);
        }
      } else if (tradeFlag == 4) { //微信
        ljCommission =
            merchantPos.getWxCommission().multiply(new BigDecimal(paidMoney));
        thirdCommission =
            new BigDecimal(dictionaryService.findDictionaryById(43L).getValue())
                .multiply(new BigDecimal(paidMoney));
      } else { //现金
        ljCommission = new BigDecimal(0);
      }
      posOrder
          .setLjCommission(Math.round(ljCommission.multiply(new BigDecimal(100)).doubleValue()));
      posOrder.setWxCommission(posOrder.getLjCommission());
      posOrder
          .setWxCommission(thirdCommission == null ? null : Math
              .round(thirdCommission.multiply(new BigDecimal(100)).doubleValue()));
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
      long price = new BigDecimal(orderPrice).longValue();
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
    posOrder.setTotalPrice(totalPrice.longValue());
    posOrder.setPaidType(paidType);
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    Merchant merchant = merchantPos.getMerchant();
    try {
      BigDecimal thirdCommission = null;
      BigDecimal ljCommission = null;
      BigDecimal truePayCommission = null;
      boolean flag = false;
      posOrder.setCompleteDate(sdf.parse(paidTime));
      if (tradeFlag == 0) {//支付宝
        thirdCommission =
            new BigDecimal(dictionaryService.findDictionaryById(42L).getValue())
                .multiply(truePay);
        if (merchantPos.getAliUserCommission() == null) {
          ljCommission = merchantPos.getAliCommission()
              .multiply(totalPrice);
          truePayCommission = merchantPos.getAliCommission()
              .multiply(truePay);
          flag = true;
        } else {
          ljCommission = merchantPos.getAliUserCommission().multiply(totalPrice);
          truePayCommission = merchantPos.getAliUserCommission()
              .multiply(truePay);
        }
      } else if (tradeFlag == 3) { //刷卡
        posOrder.setRebateWay(3);
        posOrder.setCardNo(cardNo);
        ljCommission = totalPrice.multiply(merchantPos.getLjCommission());
        truePayCommission = truePay.multiply(merchantPos.getLjCommission());
        String httpUrl = "http://apis.baidu.com/datatiny/cardinfo/cardinfo";
        String httpArg = "cardnum=" + cardNo;
        String request = PosCardCheckUtil.request(httpUrl, httpArg);
        posOrder.setCardNo(cardNo);
        if (request.indexOf("贷") != -1) {
          thirdCommission =
              new BigDecimal(dictionaryService.findDictionaryById(46L).getValue())
                  .multiply(truePay);
        } else {
          thirdCommission =
              new BigDecimal(dictionaryService.findDictionaryById(45L).getValue())
                  .multiply(truePay);
          BigDecimal thirdCommissionLimit = new BigDecimal(
              dictionaryService.findDictionaryById(47L).getValue());
          if (thirdCommission.longValue() > thirdCommissionLimit.longValue()) { //封顶第三方手续费
            thirdCommission = thirdCommissionLimit;
          }
        }
      } else if (tradeFlag == 4) { //微信
        posOrder.setRebateWay(2);
        thirdCommission =
            new BigDecimal(dictionaryService.findDictionaryById(43L).getValue())
                .multiply(truePay);
        if (merchantPos.getWxUserCommission() == null) {
          ljCommission = merchantPos.getWxCommission()
              .multiply(totalPrice);
          flag = true;
          truePayCommission = merchantPos.getWxCommission()
              .multiply(truePay);
        } else {
          ljCommission = merchantPos.getWxUserCommission()
              .multiply(totalPrice);
          truePayCommission = merchantPos.getWxUserCommission()
              .multiply(truePay);
        }
      } else if (tradeFlag == 5) { //纯积分
        ljCommission = totalPrice.multiply(merchantPos.getLjCommission());
        truePayCommission = new BigDecimal(0);
        thirdCommission =
            new BigDecimal(0);
      }
      posOrder
          .setWxCommission(Math.round(thirdCommission.divide(new BigDecimal(100)).doubleValue()));
      posOrder
          .setLjCommission(Math.round(ljCommission.divide(new BigDecimal(100)).doubleValue()));
      posOrder.setTruePayCommission(
          Math.round(truePayCommission.divide(new BigDecimal(100)).doubleValue()));
      posOrder.setTransferMoney(
          Math.round(totalPrice.subtract(ljCommission.divide(new BigDecimal(100))).doubleValue()));
      posOrder.setTransferByBank(
          Math.round(
              truePay.subtract(truePayCommission.divide(new BigDecimal(100))).doubleValue()));
      if (!flag) {
        if (posOrder.getLeJiaUser().getBindMerchant() != null && posOrder.getLeJiaUser()
            .getBindMerchant().getId()
            .equals(merchant.getId())) {//判断时导流订单还是会员订单
          //会员订单
          posOrder.setRebateWay(2);
          posOrder.setScoreB(
              Math.round(
                  totalPrice.multiply(merchantPos.getUserScoreBRebate())
                      .divide(new BigDecimal(10000))
                      .doubleValue()));
          posOrder
              .setRebate(Math.round(ljCommission.multiply(merchantPos.getUserScoreARebate().divide(
                  new BigDecimal(10000))).doubleValue()));
        } else {
          posOrder.setRebateWay(3);
          posOrder
              .setRebate(Math.round(ljCommission.multiply(merchantPos.getScoreARebate().divide(
                  new BigDecimal(10000))).doubleValue()));
          posOrder.setScoreB(
              Math.round(
                  totalPrice.multiply(merchantPos.getScoreBRebate()).divide(new BigDecimal(10000))
                      .doubleValue()));
        }
      } else {
        posOrder.setRebateWay(4);
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
