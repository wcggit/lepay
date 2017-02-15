package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.fuyou.service.FuYouPayService;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantRebatePolicy;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlement;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlementStore;
import com.jifenke.lepluslive.merchant.repository.MerchantRebatePolicyRepository;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantSettlementService;
import com.jifenke.lepluslive.merchant.service.MerchantSettlementStoreService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.repository.ScanCodeOrderRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.domain.entities.Category;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WxTemMsgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

/**
 * 富友扫码订单 Created by zhangwen on 2016/12/6.
 */
@Service
public class ScanCodeOrderService {

  private static final Logger log = LoggerFactory.getLogger(ScanCodeOrderService.class);

  private static ReentrantLock lock = new ReentrantLock();

  @Inject
  private ScanCodeOrderRepository orderRepository;

  @Inject
  private MerchantService merchantService;

  @Inject
  private MerchantSettlementStoreService storeService;

  @Inject
  private MerchantSettlementService merchantSettlementService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private OrderShareService orderShareService;

  @Inject
  private MerchantRebatePolicyRepository merchantRebatePolicyRepository;

  @Inject
  private OffLineOrderService offLineOrderService;

  @Inject
  private WxTemMsgService wxTemMsgService;

  @Inject
  private FuYouPayService fuYouPayService;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public ScanCodeOrder findOrderByOrderSid(String orderSid) {
    return orderRepository.findByOrderSid(orderSid);
  }

  /**
   * 非会员生成订单  16/12/6
   *
   * @param truePrice  实际支付
   * @param merchantId 商家
   * @param weiXinUser 用户
   * @param source     支付来源  0=WAP|1=APP
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public ScanCodeOrder createOrderForNoNMember(String truePrice, Long merchantId,
                                               WeiXinUser weiXinUser, int source) {
    ScanCodeOrder order = new ScanCodeOrder();
    Date date = new Date();
    order.setLeJiaUser(weiXinUser.getLeJiaUser());
    Long price = new BigDecimal(truePrice).multiply(new BigDecimal(100)).longValue();
    order.setTotalPrice(price);
    order.setTruePay(price);
    order.setCreatedDate(date);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    order.setMerchant(merchant);
    order.setMerchantUserId(merchant.getMerchantUser().getId());
    order.setSource(source);
    MerchantSettlementStore store = storeService.findByMerchantId(merchantId);
    if (merchant.getPartnership() == 0) {
      order.setOrderType(new Category(12001L));
    } else {
      order.setOrderType(new Category(12003L));
    }
    MerchantSettlement
        settlement =
        merchantSettlementService.findById(store.getCommonSettlementId());
    order.setMerchantNum(settlement.getMerchantNum());
    order.setMerchantRate(String.valueOf(settlement.getCommission()));
    Long commission = Math.round(price * settlement.getCommission().doubleValue() / 100.0);
    order.setCommission(commission);
    order.setTruePayCommission(commission);
//    order.setScoreCommission(0L);
    order.setWxCommission(Math.round(price * 6 / 1000.0));
    order.setWxTrueCommission(Math.round(price * 35 / 10000.0));
    long scoreB = Math.round(price * merchant.getScoreBRebate().doubleValue() / 10000.0);
    order.setScoreB(scoreB);
    Long transfer = price - commission;
    order.setTransferMoney(transfer);
    order.setTransferMoneyFromTruePay(transfer);

    orderRepository.save(order);
    return order;
  }

  /**
   * 会员生成订单  16/12/7
   *
   * @param truePrice  实际支付
   * @param merchantId 商家
   * @param leJiaUser  用户
   * @param source     支付来源  0=WAP|1=APP
   * @param trueScore  使用红包
   * @param totalPrice 总价
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public ScanCodeOrder createOrderForMember(String truePrice, Long merchantId,
                                            String trueScore,
                                            String totalPrice,
                                            LeJiaUser leJiaUser, int source
  ) {
    ScanCodeOrder order = new ScanCodeOrder();
    Date date = new Date();
    order.setLeJiaUser(leJiaUser);
    long truePay = Long.parseLong(truePrice);
    long total = Long.parseLong(totalPrice);
    long scoreA = Long.parseLong(trueScore);
    order.setTotalPrice(total);
    order.setTruePay(truePay);
    order.setTrueScore(scoreA);
    order.setCreatedDate(date);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    order.setMerchant(merchant);
    order.setMerchantUserId(merchant.getMerchantUser().getId());

    //付款方式  0=纯现金|1=纯红包|2=混合
    if (scoreA == 0) {
      order.setPayment(0);
    } else if (truePay == 0) {
      order.setPayment(1);
    } else {
      order.setPayment(2);
    }
    order.setSource(source);//支付来源  0=WAP|1=APP

    MerchantSettlementStore store = storeService.findByMerchantId(merchantId);
    //判断订单类型和使用的商户号
    long settlementId = store.getCommonSettlementId();
    Integer orderType = 2;//0代表普通订单(12002) 1导流订单(12004) 2 会员订单(12005and12006)
    if (merchant.getPartnership() == 0) { //普通商户  会员普通订单（普通商户）
      order.setOrderType(new Category(12002L));
      orderType = 0;
    } else { //联盟商户
      if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId()
          .equals(merchant.getId())) { //绑定商户，会员订单
        if (merchant.getMemberCommission().equals(merchant.getLjCommission())) { //佣金费率
          order.setOrderType(new Category(12005L));
          settlementId = store.getAllianceSettlementId();
        } else { //普通费率
          order.setOrderType(new Category(12006L));
        }
      } else { //导流订单
        order.setOrderType(new Category(12004L));
        settlementId = store.getAllianceSettlementId();
        orderType = 1;
      }
    }
    MerchantSettlement settlement = merchantSettlementService.findById(settlementId);//结算商户号
    order.setMerchantNum(settlement.getMerchantNum());
    order.setMerchantRate(String.valueOf(settlement.getCommission()));
    Long commission = Math.round(total * settlement.getCommission().doubleValue() / 100.0);
    Long truePayCommission = Math.round(truePay * settlement.getCommission().doubleValue() / 100.0);
    order.setCommission(commission);
    order.setTruePayCommission(truePayCommission);
    order.setScoreCommission(commission - truePayCommission);
    order.setWxCommission(Math.round(total * 6 / 1000.0));
    order.setWxTrueCommission(Math.round(truePay * 35 / 10000.0));
    //返利红包和发放积分
    Long rebateScoreA = 0L;
    Long rebateScoreB = 0L;
    Long[] rebates = null;
    MerchantRebatePolicy
        merchantRebatePolicy =
        merchantRebatePolicyRepository.findByMerchantId(merchantId);
    rebates = offLineOrderService.merchantRebatePolicy(rebateScoreA, rebateScoreB,
                                                       merchantRebatePolicy, merchant, orderType,
                                                       total,
                                                       commission,
                                                       order.getWxCommission());
    if (rebates != null) {
      order.setScoreB(rebates[1]);
      order.setRebate(rebates[0]);
      order.setLjProfit(rebates[2]);
    }
    long share = commission - order.getRebate() - order.getWxCommission();   //待分润金额
    if (share < 0) {
      share = 0;
    }
    order.setShare(share);
    order.setTransferMoney(total - commission);
    order.setTransferMoneyFromTruePay(truePay - truePayCommission);
    order.setTransferMoneyFromScore(scoreA - order.getScoreCommission());

    orderRepository.save(order);
    return order;
  }

  /**
   * 会员全红包支付生成订单  16/12/19
   *
   * @param merchantId 商家
   * @param userSid    用户
   * @param source     支付来源  0=WAP|1=APP
   * @param totalPrice 总价
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public ScanCodeOrder payByScoreA(String userSid, Long merchantId, String totalPrice,
                                   int source) throws Exception {
    ScanCodeOrder order = new ScanCodeOrder();
    Date date = new Date();
    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(userSid);
    order.setLeJiaUser(leJiaUser);
    long scoreA = Long.parseLong(totalPrice);
    order.setTotalPrice(scoreA);
    order.setTruePay(0L);
    order.setTrueScore(scoreA);
    order.setCreatedDate(date);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    order.setMerchant(merchant);
    order.setMerchantUserId(merchant.getMerchantUser().getId());

    //付款方式  0=纯现金|1=纯红包|2=混合
    order.setPayment(1);
    order.setSource(source);//支付来源  0=WAP|1=APP

    MerchantSettlementStore store = storeService.findByMerchantId(merchantId);
    //判断订单类型和使用的商户号
    long settlementId = store.getCommonSettlementId();
    Integer orderType = 2;//0代表普通订单(12002) 1导流订单(12004) 2 会员订单(12005and12006)
    if (merchant.getPartnership() == 0) { //普通商户  会员普通订单（普通商户）
      order.setOrderType(new Category(12002L));
      orderType = 0;
    } else { //联盟商户
      if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId()
          .equals(merchant.getId())) { //绑定商户，会员订单
        if (merchant.getMemberCommission().equals(merchant.getLjCommission())) { //佣金费率
          order.setOrderType(new Category(12005L));
          settlementId = store.getAllianceSettlementId();
        } else { //普通费率
          order.setOrderType(new Category(12006L));
        }
      } else { //导流订单
        order.setOrderType(new Category(12004L));
        settlementId = store.getAllianceSettlementId();
        orderType = 1;
      }
    }
    MerchantSettlement settlement = merchantSettlementService.findById(settlementId);//结算商户号
    order.setMerchantNum(settlement.getMerchantNum());
    order.setMerchantRate(String.valueOf(settlement.getCommission()));
    Long commission = Math.round(scoreA * settlement.getCommission().doubleValue() / 100.0);
    order.setCommission(commission);
    order.setTruePayCommission(0L);
    order.setScoreCommission(commission);
    order.setWxCommission(Math.round(scoreA * 6 / 1000.0));
    order.setWxTrueCommission(0L);
    //返利红包和发放积分
    Long rebateScoreA = 0L;
    Long rebateScoreB = 0L;
    Long[] rebates = null;
    MerchantRebatePolicy
        merchantRebatePolicy =
        merchantRebatePolicyRepository.findByMerchantId(merchantId);
    rebates = offLineOrderService.merchantRebatePolicy(rebateScoreA, rebateScoreB,
                                                       merchantRebatePolicy, merchant, orderType,
                                                       scoreA,
                                                       commission,
                                                       order.getWxCommission());
    if (rebates != null) {
      order.setScoreB(rebates[1]);
      order.setRebate(rebates[0]);
      order.setLjProfit(rebates[2]);
    }
    long share = commission - order.getRebate() - order.getWxCommission();   //待分润金额
    if (share < 0) {
      share = 0;
    }
    order.setShare(share);
    order.setTransferMoney(scoreA - commission);
    order.setTransferMoneyFromTruePay(0L);
    order.setTransferMoneyFromScore(scoreA - commission);

    order.setState(1);
    order.setCompleteDate(date);
    order.setSettleDate(new SimpleDateFormat("yyyyMMddHHmmss").format(date));
    scoreAService.paySuccessForMember(order);
    scoreBService.paySuccess(order);
    merchantService.paySuccess(merchant, order.getTransferMoney());
    order.setMessageState(1);
    wxTemMsgService
        .sendToClient(merchant.getName(), scoreA, 0L, scoreA, order.getRebate(), order.getScoreB(),
                      leJiaUser.getWeiXinUser().getOpenId(), order.getOrderSid());
    wxTemMsgService.sendToMerchant(scoreA, order.getOrderSid(), order.getLePayCode(), merchant);
    //判断是否需要绑定商户
    leJiaUserService.checkUserBindMerchant(leJiaUser, merchant);

    orderRepository.save(order);

    new Thread(() -> {
      orderShareService.offLIneOrderShare(order);
    }).start();
    return order;
  }

  public synchronized void lockPaySuccess(ScanCodeOrder order, Map<String, String> backMap) {
    paySuccess(order, backMap);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(ScanCodeOrder order, Map<String, String> backMap) {
    if (order.getState() == 0) {
      Date date = new Date();
      order.setCompleteDate(date);
      order.setOrderCode(backMap.get("transaction_id"));
      if (backMap.get("txn_fin_ts") == null) {
        System.out.println("=============支付完成时间不存在=============");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        order.setSettleDate(sdf.format(date));
      } else {
        order.setSettleDate(backMap.get("txn_fin_ts"));
      }
      long rebateWay = order.getOrderType().getId();
      if (rebateWay == 12001 || rebateWay == 12003) {
        //对于非会员 消费后只增加b积分
        scoreBService.paySuccess(order);
      } else {
        //对于乐加会员在签约商家消费,消费成功后a,b积分均改变,
        try {
          scoreAService.paySuccessForMember(order);
        } catch (Exception e) {
          log.error("富友订单出现问题===========" + order.getOrderSid());
        }
        scoreBService.paySuccess(order);
        //对于会员,判断是否需要绑定商户和合伙人
        leJiaUserService.checkUserBindMerchant(order.getLeJiaUser(), order.getMerchant());
        //分润
        if (order.getShare() > 0) {
          new Thread(() -> {
            orderShareService.offLIneOrderShare(order);
          }).start();
        }
      }
      order.setState(1);
      orderRepository.save(order);
    }
  }

  /**
   * 微信回调后发送支付成功模板消息
   */
  public void lockCheckMessageState(ScanCodeOrder order) {
    lock.lock();
    try {
      checkMessageState(order);
    } finally {
      lock.unlock();
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkMessageState(ScanCodeOrder order) {
    if (order.getMessageState() == 0) {
      order.setMessageState(1);
      //统计这是商家当月第几笔订单
//      Long count = countMerchantMonthlyOrder(offLineOrder);
//      offLineOrder.setMonthlyOrderCount(++count);
      orderRepository.save(order);
      new Thread(() -> {
        Merchant merchant = order.getMerchant();
        wxTemMsgService
            .sendToClient(merchant.getName(), order.getTrueScore(), order.getTruePay(),
                          order.getTotalPrice(), order.getRebate(),
                          order.getScoreB(),
                          order.getLeJiaUser().getWeiXinUser().getOpenId(), order.getOrderSid());
        wxTemMsgService
            .sendToMerchant(order.getTotalPrice(), order.getOrderSid(), order.getLePayCode(),
                            merchant);
      }).start();
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkOrderState(String orderSid, String settleDate) {
    ScanCodeOrder order = findOrderByOrderSid(orderSid);
    if (order.getState() == 0) {
      try {
        Map<String, String> result = fuYouPayService.buildOrderQueryParams(order);
        System.out.println("订单查询接口==============================" + result.toString());
        String resultCode = result.get("result_code");
        String trans_stat = result.get("trans_stat");
//          String orderCode = result.get("transaction_id");
        if ("000000".equals(resultCode) && "SUCCESS".equals(trans_stat)) {
          //对订单进行处理
          result.put("txn_fin_ts", settleDate);
          checkMessageState(order);
          paySuccess(order, result);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
