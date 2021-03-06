package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.activity.service.InitialOrderRebateActivityService;
import com.jifenke.lepluslive.global.abstraction.Order;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.CommissionStage;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantRebatePolicy;
import com.jifenke.lepluslive.merchant.domain.entities.RebateStage;
import com.jifenke.lepluslive.merchant.repository.MerchantRebatePolicyRepository;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.order.repository.OffLineOrderRepository;
import com.jifenke.lepluslive.printer.service.PrinterService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.MathRandom;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeixinPayLogService;
import com.jifenke.lepluslive.wxpay.service.WxTemMsgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

/**
 * Created by wcg on 16/5/5.
 */
@Service
public class OffLineOrderService {

  private static final Logger log = LoggerFactory.getLogger(OffLineOrderService.class);

  private static ReentrantLock lock = new ReentrantLock();


  @Inject
  private OffLineOrderRepository repository;

  @Inject
  private InitialOrderRebateActivityService initialOrderRebateActivityService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private ScoreCService scoreCService;

  @Inject
  private WxTemMsgService wxTemMsgService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private WeixinPayLogService weixinPayLogService;

  @Inject
  private OrderShareService orderShareService;

  @Inject
  private MerchantRebatePolicyRepository merchantRebatePolicyRepository;

  @Inject
  private PrinterService printerService;

  /**
   * 用户在某个商户消费成功的次数和总额 16/10/10
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Object[] countByLeJiaUserAndMerchant(Long leJiaUserId, Long merchantId) {
    return repository.countByLeJiaUserAndMerchantAndState(leJiaUserId, merchantId).get(0);
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder createOffLineOrderForNoNMember(String truePrice, String sumPrice,
                                                     Long merchantId,
                                                     WeiXinUser weiXinUser, boolean pure,
                                                     Long payWay) {
    OffLineOrder offLineOrder = new OffLineOrder();
    Long truePirce = new BigDecimal(truePrice).multiply(new BigDecimal(100)).longValue();
    Long sum = new BigDecimal(sumPrice).multiply(new BigDecimal(100)).longValue();
    offLineOrder.setLeJiaUser(weiXinUser.getLeJiaUser());
    offLineOrder.setTotalPrice(truePirce);
    offLineOrder.setTruePay(truePirce);
    offLineOrder.setSumPrice(sum);
    offLineOrder.setCreatedDate(new Date());
    offLineOrder.setRebateWay(0);
    //如果扫纯支付码
    if (pure) {
      if (weiXinUser.getState() == 0) {
        offLineOrder.setRebateWay(4);
      } else {
        offLineOrder.setRebateWay(5);
      }
    }
    offLineOrder.setWxCommission(Math.round(truePirce * 6 / 1000.0));
    Merchant merchant = merchantService.findMerchantById(merchantId);
    offLineOrder.setMerchant(merchant);
    if (merchant.getPartnership() == 0) {
      if (merchant.getLjCommission().doubleValue() != 0) {
        offLineOrder.setLjCommission(
            Math.round(truePirce * merchant.getLjCommission().doubleValue() / 100.0));
        offLineOrder.setTruePayCommission(offLineOrder.getLjCommission());
        offLineOrder.setCommissionScale(merchant.getLjCommission());
      }
    } else {
      if (merchant.getLjBrokerage().doubleValue() != 0) {
        offLineOrder.setLjCommission(
            Math.round(truePirce * merchant.getLjBrokerage().doubleValue() / 100.0));
        offLineOrder.setCommissionScale(merchant.getLjBrokerage());
      }
      offLineOrder.setTruePayCommission(offLineOrder.getLjCommission());
    }
    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder.setTransferMoneyFromTruePay(
        offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder.setPayWay(new PayWay(payWay));
    repository.save(offLineOrder);
    long scoreB = Math.round(truePirce * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder createOffLineOrderForMember(String truePrice, Long merchantId,
                                                  String trueScore,
                                                  String totalPrice, String sumPrice,
                                                  LeJiaUser leJiaUser, Long payWay
  ) {
    MerchantRebatePolicy
        merchantRebatePolicy =
        merchantRebatePolicyRepository.findByMerchantId(merchantId);
    OffLineOrder offLineOrder = new OffLineOrder();
    long truePay = Long.parseLong(truePrice);
    long total = Long.parseLong(totalPrice);
    long sum = Long.parseLong(sumPrice);
    long scoreA = Long.parseLong(trueScore);
    Long rebateScoreA = 0L;
    Long rebateScoreB = 0L;
    Long[] rebates = null;
    offLineOrder.setSumPrice(sum);
    offLineOrder.setLeJiaUser(leJiaUser);
    offLineOrder.setTotalPrice(total);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(truePay);
    offLineOrder.setCreatedDate(new Date());
    Merchant merchant = merchantService.findMerchantById(merchantId);
    offLineOrder.setMerchant(merchant);
    offLineOrder.setRebateWay(2);
    offLineOrder.setWxCommission(Math.round(total * 6 / 1000.0));
    offLineOrder.setPayWay(new PayWay(payWay));
    if (merchant.getLjCommission().doubleValue() != 0) {
      long
          ljCommission =
          Math.round(
              new BigDecimal(total).multiply(merchant.getLjCommission()).divide(new BigDecimal(100))
                  .doubleValue());
      long
          truePayCommission =
          Math.round(
              new BigDecimal(truePay).multiply(merchant.getLjCommission())
                  .divide(new BigDecimal(100))
                  .doubleValue());
      offLineOrder.setLjCommission(ljCommission);
      offLineOrder.setCommissionScale(merchant.getLjCommission());
      offLineOrder.setTruePayCommission(truePayCommission);
      //代表联盟商户
      if (merchant.getPartnership() != 0) {
        if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId() == merchant
            .getId()) {
          if (merchant.getMemberCommission().doubleValue() > merchant.getLjBrokerage()
              .doubleValue()) {
            offLineOrder.setRebateWay(3);//代表会员订单
          } else {
            offLineOrder.setRebateWay(6);//会员联盟商户消费普通费率订单
          }
          ljCommission =
              Math.round(
                  new BigDecimal(total).multiply(merchant.getMemberCommission())
                      .divide(new BigDecimal(100))
                      .doubleValue());
          truePayCommission =
              Math.round(
                  new BigDecimal(truePay).multiply(merchant.getMemberCommission())
                      .divide(new BigDecimal(100))
                      .doubleValue());
          offLineOrder.setLjCommission(ljCommission);
          offLineOrder.setCommissionScale(merchant.getMemberCommission());
          offLineOrder.setTruePayCommission(truePayCommission);
          rebates =
              merchantRebatePolicy(rebateScoreA, rebateScoreB, merchantRebatePolicy, merchant, 2,
                                   total,
                                   ljCommission, offLineOrder.getWxCommission(), offLineOrder);
        } else { //导流订单
          offLineOrder.setRebateWay(1);
          rebates =
              merchantRebatePolicy(rebateScoreA, rebateScoreB, merchantRebatePolicy, merchant, 1,
                                   total,
                                   ljCommission, offLineOrder.getWxCommission(), offLineOrder);
        }
      }
    }

    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder
        .setTransferMoneyFromTruePay(
            new BigDecimal(truePay).longValue() - offLineOrder.getTruePayCommission());
    if (rebates != null) {
      offLineOrder.setScoreB(rebates[1]);
      offLineOrder.setScoreC(rebates[3]);
      offLineOrder.setRebate(rebates[0]);
      offLineOrder.setNonCriticalRebate(rebates[0]);
      offLineOrder.setLjProfit(
          offLineOrder.getLjCommission() - offLineOrder.getRebate() - offLineOrder.getWxCommission()
          - offLineOrder.getShareMoney());
    }
    offLineOrder.setPolicy(
        merchantRebatePolicy.getCommissionPolicy() + "_" + merchantRebatePolicy.getRebatePolicy());
    repository.save(offLineOrder);
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(OffLineOrder offLineOrder) {
    if (offLineOrder.getState() == 0) {
      offLineOrder.setCompleteDate(new Date());
      if (offLineOrder.getRebateWay() == 0) {
        //对于非会员 消费后只增加b积分
        scoreBService.paySuccess(offLineOrder);
      } else {
        //对于乐加会员在签约商家消费,消费成功后a,b积分均改变,
        try {
          if (offLineOrder.getRebateWay() == 1 || offLineOrder.getRebateWay() == 3) {
            if (offLineOrder.getPolicy().endsWith("1")) {//如果是鼓励金策略检查是否为暴击订单
              Calendar now = Calendar.getInstance();
              Date end = now.getTime();
              now.set(Calendar.MILLISECOND, 0);
              now.set(Calendar.SECOND, 0);
              now.set(Calendar.MINUTE, 0);
              now.set(Calendar.HOUR_OF_DAY, 0);
              Date start = now.getTime();
              if (now.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && !repository
                  .findByLeJiaUserAndCriticalOrderAndCompleteDateBetween(
                      offLineOrder.getLeJiaUser(),
                      1,
                      start, end).isPresent()) {
                offLineOrder.setCriticalOrder(1); //该笔是暴击订单
                now.add(Calendar.DAY_OF_WEEK, -3);
                Date dateStart = now.getTime();
                now.add(Calendar.DAY_OF_WEEK, 3);
                now.add(Calendar.SECOND, -1);
                Date dateEnd = now.getTime();
                Long
                    rebateScoreA =
                    repository.calculateRebateScoreA(offLineOrder.getLeJiaUser().getId(), dateStart,
                                                     dateEnd);
                offLineOrder.setRebate(offLineOrder.getRebate() * 2 + rebateScoreA); //暴击返鼓励金
              }
            }
            scoreCService.paySuccess(offLineOrder);
            orderShareService.offLIneOrderShare(offLineOrder);
          }
          scoreAService.paySuccessForMember(offLineOrder);
        } catch (Exception e) {
          e.printStackTrace();
          log.error("该笔订单出现问题===========" + offLineOrder.getOrderSid());
        }
        scoreBService.paySuccess(offLineOrder);
        //对于会员,判断是否需要绑定商户和合伙人
        leJiaUserService
            .checkUserBindMerchant(offLineOrder.getLeJiaUser(), offLineOrder.getMerchant());

        //对于返庸订单分润

      }
      //首单返红包活动
//      new Thread(() -> {
//        initialOrderRebateActivityService.checkActivity(offLineOrder);
//      }).start();
      offLineOrder.setState(1);
      repository.save(offLineOrder);
      //调易连云打印机接口
      try {
        final String orderSid = offLineOrder.getOrderSid();
        new Thread(() -> printerService.addReceipt(orderSid)).start();
      } catch (Exception e) {
      }
    }
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder findOffLineOrderByOrderSid(String orderSid) {
    return repository.findByOrderSid(orderSid);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder payByScoreA(String userSid, String merchantId, String totalPrice,
                                  String sumPrice,
                                  Long payWay) {
    OffLineOrder offLineOrder = new OffLineOrder();
    long scoreA = Long.parseLong(totalPrice);
    long sum = Long.parseLong(sumPrice);
    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(userSid);
    ScoreA score_a = scoreAService.findScoreAByLeJiaUser(leJiaUser);
    if (score_a.getScore() >= scoreA) {
      offLineOrder.setLeJiaUser(leJiaUser);
      offLineOrder.setTotalPrice(scoreA);
      offLineOrder.setTrueScore(scoreA);
      offLineOrder.setSumPrice(sum);
      offLineOrder.setTruePay(0L);
      offLineOrder.setCreatedDate(new Date());
      offLineOrder.setWxCommission(Math.round(scoreA * 6 / 1000.0));
      Merchant merchant = merchantService.findMerchantById(Long.parseLong(merchantId));
      MerchantRebatePolicy
          merchantRebatePolicy =
          merchantRebatePolicyRepository.findByMerchantId(Long.parseLong(merchantId));
      offLineOrder.setMerchant(merchant);
      offLineOrder.setPayWay(new PayWay(payWay));
      Long rebateScoreA = 0L;
      Long rebateScoreB = 0L;
      Long[] rebates = null;
      if (merchant.getLjCommission().doubleValue() != 0) {
        long
            ljCommission =
            Math.round(new BigDecimal(scoreA).multiply(merchant.getLjCommission())
                           .divide(new BigDecimal(100)).doubleValue());
        offLineOrder.setLjCommission(ljCommission);
        offLineOrder.setCommissionScale(merchant.getLjCommission());
        if (merchant.getPartnership() != 0) { //代表乐加会员在签约商家消费
          if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId() == merchant
              .getId()) {
            if (merchant.getMemberCommission().doubleValue() > merchant.getLjBrokerage()
                .doubleValue()) {
              offLineOrder.setRebateWay(3);//代表会员订单
            } else {
              offLineOrder.setRebateWay(6);//会员联盟商户消费普通订单
            }
            ljCommission =
                Math.round(
                    new BigDecimal(scoreA).multiply(merchant.getMemberCommission())
                        .divide(new BigDecimal(100))
                        .doubleValue());
            offLineOrder.setLjCommission(ljCommission);
            offLineOrder.setCommissionScale(merchant.getMemberCommission());
            rebates =
                merchantRebatePolicy(rebateScoreA, rebateScoreB, merchantRebatePolicy, merchant, 2,
                                     scoreA, ljCommission, offLineOrder.getWxCommission(),
                                     offLineOrder);
          } else { //导流订单
            offLineOrder.setRebateWay(1);

            rebates =
                merchantRebatePolicy(rebateScoreA, rebateScoreB, merchantRebatePolicy, merchant, 1,
                                     scoreA, ljCommission, offLineOrder.getWxCommission(),
                                     offLineOrder);
          }
        } else {
          rebates =
              merchantRebatePolicy(rebateScoreA, rebateScoreB, merchantRebatePolicy, merchant, 0,
                                   scoreA,
                                   ljCommission, offLineOrder.getWxCommission(), offLineOrder);
          offLineOrder.setRebateWay(2); //会员普通订单
        }
      }
      if (rebates != null) {
        offLineOrder.setScoreB(rebates[1]);
        offLineOrder.setRebate(rebates[0]);
        offLineOrder.setLjProfit(
            offLineOrder.getLjCommission() - offLineOrder.getRebate() - offLineOrder
                .getWxCommission()
            - offLineOrder.getShareMoney());
        offLineOrder.setScoreC(rebates[3]);
        offLineOrder.setNonCriticalRebate(rebates[0]);
      }
      offLineOrder.setPolicy(
          merchantRebatePolicy.getCommissionPolicy() + "_" + merchantRebatePolicy
              .getRebatePolicy());
      offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
      offLineOrder.setTransferMoneyFromTruePay(0L);
      paySuccess(offLineOrder);
      Long count = countMerchantMonthlyOrder(offLineOrder);
      offLineOrder.setMonthlyOrderCount(++count);
      wxTemMsgService.sendToMerchant(offLineOrder);
      wxTemMsgService.sendToClient(offLineOrder);
      offLineOrder.setMessageState(1);
      repository.save(offLineOrder);
    }
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkOrderState(String orderSId) {
    OffLineOrder offLineOrder = repository.findByOrderSid(orderSId);
    if (offLineOrder != null) {
      if (offLineOrder.getState() == 0) {
        //调接口查询订单是否支付完成
        SortedMap<Object, Object> map = weiXinPayService.buildOrderQueryParams(offLineOrder);
        Map orderMap = weiXinPayService.orderStatusQuery(map);
        String returnCode = (String) orderMap.get("return_code");
        String resultCode = (String) orderMap.get("result_code");
        String tradeState = (String) orderMap.get("trade_state");
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode) && "SUCCESS"
            .equals(tradeState)) {
          //对订单进行处理
          weixinPayLogService.savePayLog(orderSId, returnCode, resultCode, tradeState);
          checkMessageState(orderSId);
          paySuccess(offLineOrder);
        }
      }
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkMessageState(String orderSid) {
    OffLineOrder offLineOrder = repository.findByOrderSid(orderSid);
    if (offLineOrder.getMessageState() == 0) {
      offLineOrder.setMessageState(1);
      //统计这是商家当月第几笔订单
      Long count = countMerchantMonthlyOrder(offLineOrder);
      offLineOrder.setMonthlyOrderCount(++count);
      repository.save(offLineOrder);
      //以下两个方法内已异步，TODO:后期建议使用MQ
      wxTemMsgService.sendToMerchant(offLineOrder);
      wxTemMsgService.sendToClient(offLineOrder);
    }
  }

  public Long countMerchantMonthlyOrder(OffLineOrder offLineOrder) {
    //当月第一天
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    return repository
        .countMerchantMonthlyOrder(offLineOrder.getMerchant().getId(), calendar.getTime(),
                                   new Date());
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
  public void multiTest() {
    OffLineOrder order = repository.findOne(1L);
    order.setMessageState(order.getMessageState() + 1);
    repository.save(order);
  }

  public synchronized void lockMultiTest() {
    multiTest();
  }


  public void lockCheckMessageState(String orderSid) {
    lock.lock();
    try {
      checkMessageState(orderSid);
    } finally {
      lock.unlock();
    }
  }

  public synchronized void lockPaySuccess(String orderSid) {
    paySuccess(repository.findByOrderSid(orderSid));
  }


  public Long[] merchantRebatePolicy(Long scoreA, Long scoreB,
                                     MerchantRebatePolicy merchantRebatePolicy, Merchant merchant,
                                     Integer orderType, Long totalPrice, Long ljCommission,
                                     Long wxCommission, OffLineOrder offLineOrder) {
    long ljProfit = 0L;
    long scoreC = 0L;
    if (orderType == 0) {//0代表普通订单 1导流订单 2 会员订单
      //普通订单，scoreA=0
      scoreA = 0L;
      scoreB = Math.round(totalPrice * merchant.getScoreBRebate().doubleValue() / 10000.0);
    } else if (orderType == 1) {
      if (merchantRebatePolicy.getCommissionPolicy() == 0) { //固定策略
        Long
            maxScoreA =
            Math.round(ljCommission * merchant.getScoreARebate().doubleValue() / 100.0);
        //为最大可获得红包 落桶观察到底是那一部分分账
        if (merchantRebatePolicy.getRebatePolicy() == 0) { //普通策略发红包
          scoreA = returnScoreA(MathRandom.PercentageRandom(merchantRebatePolicy.getStageOne(),
                                                            merchantRebatePolicy.getStageTwo(),
                                                            merchantRebatePolicy.getStageThree(),
                                                            merchantRebatePolicy.getStageFour()),
                                maxScoreA.intValue(), merchantRebatePolicy);
        } else { //鼓励金策略发红包
          scoreA = stagePolicyRebate(totalPrice, merchantRebatePolicy.getRebateStages());
        }
        scoreB = Math.round(
            totalPrice * merchantRebatePolicy.getImportScoreBScale().doubleValue() / 10000.0);
        ljProfit = maxScoreA - scoreA;
        scoreC = Math.round(
            totalPrice * merchantRebatePolicy.getImportScoreCScale().doubleValue() / 100.0);
      } else { //阶段策略
        CommissionStage
            commissionStage =
            stagePolicyCommission(totalPrice, merchantRebatePolicy.getCommissionStages());
        if (commissionStage != null) {
          offLineOrder.setLjCommission(
              Math.round(totalPrice * commissionStage.getCommissionScale().doubleValue() / 100.0));
          offLineOrder.setCommissionScale(commissionStage.getCommissionScale());
          scoreA = stagePolicyRebate(totalPrice, merchantRebatePolicy.getRebateStages());
          scoreC = Math.round(totalPrice * commissionStage.getScoreCScale().doubleValue() / 100.0);
        }
      }
      offLineOrder.setShareMoney(Math.round(
          offLineOrder.getLjCommission() * merchantRebatePolicy.getImportShareScale()
              .doubleValue() / 100.0));
    } else {//如果是会员订单
      if (merchantRebatePolicy.getCommissionPolicy() == 0) { //固定策略
        if (merchantRebatePolicy.getRebateFlag() == 0) {//按比例发放积分和红包
          Long maxScoreA =
              Math.round(
                  ljCommission * merchantRebatePolicy.getUserScoreAScale().doubleValue() / 100.0);
          if (merchantRebatePolicy.getRebatePolicy() == 0) {

            //为最大可获得红包 落桶观察到底是那一部分分账
            scoreA = returnScoreA(MathRandom.PercentageRandom(merchantRebatePolicy.getStageOne(),
                                                              merchantRebatePolicy.getStageTwo(),
                                                              merchantRebatePolicy.getStageThree(),
                                                              merchantRebatePolicy.getStageFour()),
                                  maxScoreA.intValue(), merchantRebatePolicy);
          } else {
            scoreA = stagePolicyRebate(totalPrice, merchantRebatePolicy.getRebateStages());
          }
          scoreB = Math.round(
              totalPrice * merchantRebatePolicy.getUserScoreBScale().doubleValue() / 10000.0);
          ljProfit = maxScoreA - scoreA;
          scoreC = Math.round(
              totalPrice * merchantRebatePolicy.getUserScoreCScale().doubleValue() / 100.0);
          offLineOrder.setShareMoney(Math.round(
              offLineOrder.getLjCommission() * merchantRebatePolicy.getMemberShareScale()
                  .doubleValue() / 100.0));
        } else if (merchantRebatePolicy.getRebateFlag() == 1) {//全额发放红包
          if (merchantRebatePolicy.getRebatePolicy() == 0) {

            if (ljCommission - wxCommission > 0) { //如果会员佣金大于微信手续费则发红包
              scoreA = ljCommission - wxCommission;
            }
          } else {
            scoreA = stagePolicyRebate(totalPrice, merchantRebatePolicy.getRebateStages());
          }
          scoreB =
              Math.round(
                  totalPrice * merchantRebatePolicy.getUserScoreBScaleB().doubleValue() / 10000.0);
          scoreC = Math.round(
              totalPrice * merchantRebatePolicy.getUserScoreCScaleB().doubleValue() / 100.0);
          offLineOrder.setShareMoney(Math.round(
              offLineOrder.getLjCommission() * merchantRebatePolicy.getMemberShareScale()
                  .doubleValue() / 100.0));
        } else {
          scoreA = 0L;
          scoreB = Math.round(totalPrice * merchant.getScoreBRebate().doubleValue() / 10000.0);
        }
      } else { //阶段策略
        if (merchantRebatePolicy.getRebateFlag() == 0
            || merchantRebatePolicy.getRebateFlag() == 1) {
          CommissionStage
              commissionStage =
              stagePolicyCommission(totalPrice, merchantRebatePolicy.getCommissionStages());
          if (commissionStage != null) {
            offLineOrder.setLjCommission(
                Math.round(
                    totalPrice * commissionStage.getCommissionScale().doubleValue() / 100.0));
            offLineOrder.setCommissionScale(commissionStage.getCommissionScale());
            scoreA = stagePolicyRebate(totalPrice, merchantRebatePolicy.getRebateStages());
            scoreC =
                Math.round(totalPrice * commissionStage.getScoreCScale().doubleValue() / 100.0);
            offLineOrder.setShareMoney(Math.round(
                offLineOrder.getLjCommission() * merchantRebatePolicy.getMemberShareScale()
                    .doubleValue() / 100.0));
          }

        }
      }
    }
    Long[] result = new Long[4];
    result[0] = scoreA;
    result[1] = scoreB;
    result[2] = ljProfit;
    result[3] = scoreC;
    return result;
  }


  private CommissionStage stagePolicyCommission(Long totalPrice,
                                                List<CommissionStage> commissionStages) {
    CommissionStage stage = null;
    for (CommissionStage commissionStage : commissionStages) {
      if (commissionStage.getStart() <= totalPrice && totalPrice <= commissionStage.getEnd()) {
        stage = commissionStage;
        break;
      }
    }
    return stage;
  }


  public Long stagePolicyRebate(Long totalPrice, List<RebateStage> rebateStages) {
    Long scoreA = 0L;
    for (RebateStage rebateStage : rebateStages) {
      if (rebateStage.getStart() <= totalPrice && totalPrice <= rebateStage.getEnd()) {
        scoreA =
            new Random().nextInt((rebateStage.getRebateEnd().intValue()
                                  - rebateStage.getRebateStart().intValue() + 1))
            + rebateStage.getRebateStart();
        break;
      }
    }
    return scoreA;
  }

  public Long[] merchantRebatePolicy(Long scoreA, Long scoreB,
                                     MerchantRebatePolicy merchantRebatePolicy, Merchant merchant,
                                     Integer orderType, Long totalPrice, Long ljCommission,
                                     Long wxCommission) {
    long ljProfit = 0L;
    if (orderType == 0) {//0代表普通订单 1导流订单 2 会员订单
      //普通订单，scoreA=0
      scoreA = 0L;
      scoreB = Math.round(totalPrice * merchant.getScoreBRebate().doubleValue() / 10000.0);
    } else if (orderType == 1) {
      Long maxScoreA = Math.round(ljCommission * merchant.getScoreARebate().doubleValue() / 100.0);
      //为最大可获得红包 落桶观察到底是那一部分分账
      scoreA = returnScoreA(MathRandom.PercentageRandom(merchantRebatePolicy.getStageOne(),
                                                        merchantRebatePolicy.getStageTwo(),
                                                        merchantRebatePolicy.getStageThree(),
                                                        merchantRebatePolicy.getStageFour()),
                            maxScoreA.intValue(), merchantRebatePolicy);
      scoreB = Math.round(
          totalPrice * merchantRebatePolicy.getImportScoreBScale().doubleValue() / 10000.0);
      ljProfit = maxScoreA - scoreA;
    } else {//如果是会员订单
      if (merchantRebatePolicy.getRebateFlag() == 0) {//按比例发放积分和红包
        Long maxScoreA =
            Math.round(
                ljCommission * merchantRebatePolicy.getUserScoreAScale().doubleValue() / 100.0);
        //为最大可获得红包 落桶观察到底是那一部分分账
        scoreA = returnScoreA(MathRandom.PercentageRandom(merchantRebatePolicy.getStageOne(),
                                                          merchantRebatePolicy.getStageTwo(),
                                                          merchantRebatePolicy.getStageThree(),
                                                          merchantRebatePolicy.getStageFour()),
                              maxScoreA.intValue(), merchantRebatePolicy);
        scoreB = Math.round(
            totalPrice * merchantRebatePolicy.getUserScoreBScale().doubleValue() / 10000.0);
        ljProfit = maxScoreA - scoreA;
      } else if (merchantRebatePolicy.getRebateFlag() == 1) {//全额发放红包
        if (ljCommission - wxCommission > 0) { //如果会员佣金大于微信手续费则发红包
          scoreA = ljCommission - wxCommission;
        }
        scoreB =
            Math.round(
                totalPrice * merchantRebatePolicy.getUserScoreBScaleB().doubleValue() / 10000.0);
      } else {
        scoreA = 0L;
        scoreB = Math.round(totalPrice * merchant.getScoreBRebate().doubleValue() / 10000.0);
      }
    }
    Long[] result = new Long[3];
    result[0] = scoreA;
    result[1] = scoreB;
    result[2] = ljProfit;
    return result;
  }

  public Long returnScoreA(int bucket, int commission, MerchantRebatePolicy merchantRebatePolicy) {
    int rebate = 0;
    switch (bucket) {
      case 0:
        rebate =
            new Random().nextInt(((commission * merchantRebatePolicy.getRegionOne() / 100 + 1)));
        break;
      case 1:
        rebate =
            new Random().nextInt((commission * merchantRebatePolicy.getRegionTwo() / 100
                                  - commission * merchantRebatePolicy.getRegionOne() / 100 + 1))
            + commission * merchantRebatePolicy.getRegionOne() / 100;
        break;
      case 2:
        rebate =
            new Random().nextInt((commission * merchantRebatePolicy.getRegionThree() / 100
                                  - commission * merchantRebatePolicy.getRegionTwo() / 100 + 1))
            + commission * merchantRebatePolicy.getRegionTwo() / 100;
        break;
      case 3:
        rebate =
            new Random().nextInt((commission * merchantRebatePolicy.getRegionFour() / 100
                                  - commission * merchantRebatePolicy.getRegionThree() / 100 + 1))
            + commission * merchantRebatePolicy.getRegionThree() / 100;
        break;
      default:
        rebate =
            new Random()
                .nextInt((commission - commission * merchantRebatePolicy.getRegionFour() / 100 + 1))
            + commission * merchantRebatePolicy.getRegionFour() / 100;
    }
    return (long) rebate;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<Map> statisticRebateGroupByCompleteDate(LeJiaUser leJiaUser, Date start,
                                                      Date end, int day) {
    Boolean gtThurs = day >= 5 || day == 1;
    String thursday = null;
    OffLineOrder offLineOrder = null; //暴击订单

    List<Object[]>
        longs =
        repository.statisticRebateGroupByCompleteDate(leJiaUser.getId(), start, end);

    List<String> dates = new ArrayList<>();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(start);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    while (calendar.getTime().before(end)) {
      Date result = calendar.getTime();
      dates.add(sdf.format(result));
      if (gtThurs && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) { //今天星期4 查看是否暴击
        thursday = sdf.format(result);
        Optional<OffLineOrder>
            optional =
            repository
                .findByLeJiaUserAndCriticalOrderAndCompleteDateBetween(leJiaUser, 1, result, end);
        if (optional.isPresent()) {
          offLineOrder = optional.get();
        }
      }
      calendar.add(Calendar.DATE, 1);
    }
    List<Map> results = new ArrayList<>();
    boolean flag = true;
    for (String s : dates) {
      Map result = new HashMap();
      result.put("data", s);
      for (Object[] objects : longs) {
        if (s.equals((String) objects[1])) {
          long value = ((BigDecimal) objects[0]).longValue();
          if (gtThurs) {//今天大于等于星期四
            if (offLineOrder == null) {//如果没有暴击订单说明无翻倍
              result.put("value", value / 100.0 + "");
            } else {//当存在暴击当情况
              if (thursday.equals(s)) {//今天星期4
                flag = false;
                if (value > offLineOrder.getRebate()) {//如果暴击订单返鼓励金不是当前统计值，说明暴击当天消费了2比以上
                  result.put("value",
                             offLineOrder.getNonCriticalRebate() / 100.0 + " × 2" + " + " + (value
                                                                                             - offLineOrder
                                                                                                 .getRebate())
                                                                                            / 100.0);
                } else {
                  result.put("value", offLineOrder.getNonCriticalRebate() / 100.0 + " × 2");
                }
              } else {
                if (flag) {//flag为星期4之前,翻倍显示
                  result.put("value", value / 100.0 + " × 2");
                } else {
                  result.put("value", value / 100.0 + "");
                }
              }
            }
          } else {
            result.put("value", value / 100.0 + "");
          }
        }
      }
      if (!result.containsKey("value")) {
        result.put("value", "0");
      }
      results.add(result);
    }
    return results;
  }

//  public static void main(String[] args) {
//    System.out.println(0x7fffffff);
//    System.out.println(0b10000000000000000000000000000001);
//  }
}
