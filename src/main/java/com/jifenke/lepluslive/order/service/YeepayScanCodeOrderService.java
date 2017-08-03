package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.fuyou.service.YeepayService;
import com.jifenke.lepluslive.fuyou.util.YBCallback;
import com.jifenke.lepluslive.fuyou.util.YBConstants;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantRebatePolicy;
import com.jifenke.lepluslive.merchant.repository.MerchantRebatePolicyRepository;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantSettlementService;
import com.jifenke.lepluslive.merchant.service.MerchantSettlementStoreService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrderExt;
import com.jifenke.lepluslive.order.repository.ScanCodeOrderExtRepository;
import com.jifenke.lepluslive.order.repository.ScanCodeOrderRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WxTemMsgService;
import com.jifenke.lepluslive.yibao.service.LedgerTransferService;
import com.jifenke.lepluslive.yibao.service.MerchantLedgerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

/**
 * 富友扫码订单 Created by zhangwen on 2016/12/6.
 */
@Service
public class YeepayScanCodeOrderService {

  private static final Logger log = LoggerFactory.getLogger(YeepayScanCodeOrderService.class);

  private static ReentrantLock lock = new ReentrantLock();

  @Inject
  private ScanCodeOrderRepository orderRepository;

  @Inject
  private ScanCodeOrderExtRepository scanCodeOrderExtRepository;

  @Inject
  private MerchantService merchantService;

  @Inject
  private LedgerTransferService ledgerTransferService;

  @Inject
  private MerchantLedgerService merchantLedgerService;

  @Inject
  private ScoreAService scoreAService;

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
  private YeepayService yeepayService;

  @Inject
  private ScoreCService scoreCService;

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
                                               WeiXinUser weiXinUser, int source, int useWeixin,
                                               String aliUserId) {
    ScanCodeOrder order = new ScanCodeOrder();
    ScanCodeOrderExt ext = new ScanCodeOrderExt();
    ext.setGatewayType(1); //通道为易宝
    if (useWeixin == 1) {
      ext.setPayType(0);
    } else {
      ext.setPayType(1);
      ext.setAliUserid(aliUserId);
    }
    order.setScanCodeOrderExt(ext);
    Date date = new Date();
    order.setLeJiaUser(weiXinUser.getLeJiaUser());
    Long price = new BigDecimal(truePrice).multiply(new BigDecimal(100)).longValue();
    order.setTotalPrice(price);
    order.setTruePay(price);
    order.setCreatedDate(date);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    order.setMerchant(merchant);
    ext.setMerchantUserId(merchant.getMerchantUser().getId());
    ext.setSource(source);
    if (merchant.getPartnership() == 0) {
      order.setOrderType(12001L);
    } else {
      order.setOrderType(12003L);
    }
    if (merchant.getPartnership() == 0) {
      if (merchant.getLjCommission().doubleValue() != 0) {
        order.setCommission(
            Math.round(price * merchant.getLjCommission().doubleValue() / 100.0));
        ext.setMerchantRate(merchant.getLjCommission());
      }
    } else {
      if (merchant.getLjBrokerage().doubleValue() != 0) {
        order.setCommission(
            Math.round(price * merchant.getLjBrokerage().doubleValue() / 100.0));
        ext.setMerchantRate(merchant.getLjBrokerage());
      }
    }
    order.setTruePayCommission(order.getLjCommission());
    //输入子商户号
    ext.setMerchantNum(merchantLedgerService.findByMerchant(merchant).getMerchantUserLedger()
                           .getLedgerNo());
    order.setWxCommission(Math.round(price * YBConstants.WX_WEB_RATE.doubleValue()));
    order.setWxTrueCommission(order.getWxCommission());
    Long transfer = price - order.getCommission();
    order.setTransferMoney(transfer);
    order.setTransferMoneyFromTruePay(transfer);
    scanCodeOrderExtRepository.save(ext);
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
    ScanCodeOrderExt ext = new ScanCodeOrderExt();
    ext.setGatewayType(1); //通道为易宝
    order.setScanCodeOrderExt(ext);
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
    ext.setMerchantUserId(merchant.getMerchantUser().getId());

    //付款方式  0=纯现金|1=纯红包|2=混合
    if (scoreA == 0) {
      ext.setPayType(0);
    } else if (truePay == 0) {
      ext.setUseScoreA(1);
    } else {
      ext.setPayType(0);
      ext.setUseScoreA(1);
    }
    ext.setSource(source);//支付来源  0=WAP|1=APP

    MerchantRebatePolicy
        merchantRebatePolicy =
        merchantRebatePolicyRepository.findByMerchantId(merchantId);
    //判断订单类型和使用的商户号
    long rebate = 0l;
    long scoreC = 0l;
    long share = 0l;
    long commission =
        Math.round(
            new BigDecimal(total).multiply(merchant.getLjCommission()).divide(new BigDecimal(100))
                .doubleValue());
    if (merchant.getPartnership() == 0) { //普通商户  会员普通订单（普通商户）
      order.setOrderType(12002L);
    } else { //联盟商户
      if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId()
          .equals(merchant.getId())) { //绑定商户，会员订单
        order.setOrderType(12005L);
      } else { //导流订单
        order.setOrderType(12004L);
      }
      rebate = offLineOrderService.stagePolicyRebate(total, merchantRebatePolicy.getRebateStages());
      scoreC =
          Math.round(total * merchantRebatePolicy.getImportScoreCScale().doubleValue() / 100.0);
      share =
          Math.round(commission * merchantRebatePolicy.getImportShareScale().doubleValue() / 100.0);
    }

    order.setShare(share);
    order.setRebate(rebate);
    order.setScoreC(scoreC);
    //输入子商户号
    ext.setMerchantNum(merchantLedgerService.findByMerchant(merchant).getMerchantUserLedger()
                           .getLedgerNo());
    ext.setMerchantRate(merchant.getLjCommission());
    Long truePayCommission = Math.round(truePay * merchant.getLjCommission().doubleValue() / 100.0);
    order.setCommission(commission);
    order.setTruePayCommission(truePayCommission);
    order.setScoreCommission(commission - truePayCommission);
    order.setWxCommission(Math.round(total * YBConstants.WX_WEB_RATE.doubleValue()));
    order.setWxTrueCommission(Math.round(truePay * YBConstants.WX_WEB_RATE.doubleValue()));
    order.setTransferMoney(total - commission);
    order.setTransferMoneyFromTruePay(truePay - truePayCommission);
    order.setTransferMoneyFromScore(scoreA - order.getScoreCommission());
    scanCodeOrderExtRepository.save(ext);
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
    ScanCodeOrderExt ext = new ScanCodeOrderExt();
    order.setScanCodeOrderExt(ext);
    ext.setUseScoreA(1);
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
    ext.setMerchantUserId(merchant.getMerchantUser().getId());

    //付款方式  0=纯现金|1=纯红包|2=混合
    ext.setSource(source);//支付来源  0=WAP|1=APP

    //判断订单类型和使用的商户号
    long rebate = 0l;
    long scoreC = 0l;
    long share = 0l;

    MerchantRebatePolicy
        merchantRebatePolicy =
        merchantRebatePolicyRepository.findByMerchantId(merchantId);

    long commission =
        Math.round(
            new BigDecimal(scoreA).multiply(merchant.getLjCommission()).divide(new BigDecimal(100))
                .doubleValue());
    if (merchant.getPartnership() == 0) { //普通商户  会员普通订单（普通商户）
      order.setOrderType(12002L);
    } else { //联盟商户
      if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId()
          .equals(merchant.getId())) { //绑定商户，会员订单
        order.setOrderType(12005L);
      } else { //导流订单
        order.setOrderType(12004L);
      }
      rebate =
          offLineOrderService.stagePolicyRebate(scoreA, merchantRebatePolicy.getRebateStages());
      scoreC =
          Math.round(scoreA * merchantRebatePolicy.getImportScoreCScale().doubleValue() / 100.0);
      share =
          Math.round(commission * merchantRebatePolicy.getImportShareScale().doubleValue() / 100.0);
    }
    order.setShare(share);
    order.setRebate(rebate);
    order.setScoreC(scoreC);
    //输入子商户号
    ext.setMerchantNum(merchantLedgerService.findByMerchant(merchant).getMerchantUserLedger()
                           .getLedgerNo());
    ext.setMerchantRate(merchant.getLjCommission());
    order.setCommission(commission);
    order.setTruePayCommission(0L);
    order.setScoreCommission(commission);
    order.setTransferMoney(scoreA - commission);
    order.setTransferMoneyFromTruePay(0L);
    order.setTransferMoneyFromScore(scoreA - commission);

    order.setState(1);

    //完成时间和入账时间
    YBCallback callback = checkOrderDate();
    order.setSettleDate(callback.getSettleDate());
    order.setCompleteDate(callback.getDateCompleted());

    scoreAService.paySuccessForMember(order);
    scoreCService.paySuccess(order);

    merchantService.paySuccess(merchant, order.getTransferMoney());
    order.setMessageState(1);
    wxTemMsgService
        .sendToClient(merchant.getName(), scoreA, 0L, scoreA, order.getRebate(), order.getScoreC(),
                      leJiaUser.getWeiXinUser().getOpenId(), order.getOrderSid(),order.getScanCodeOrderExt().getPayType());
    Long type = order.getOrderType();
    if (type == 12001L || type == 12002L || type == 12003L) {
      type = 0L;
    } else {
      type = 1L;
    }
    wxTemMsgService
        .sendToMerchant(scoreA, order.getOrderSid(), order.getLePayCode(), merchant, type);
    //判断是否需要绑定商户
    leJiaUserService.checkUserBindMerchant(leJiaUser, merchant);
    orderRepository.save(order);
    orderShareService.offLIneOrderShare(order);
    //23:30:00~23:59:30支付成功实时转账
    if (callback.getType() == 2) { //转账
      ledgerTransferService
          .transfer(ext.getMerchantNum(), order.getTransferMoney(), order.getSettleDate(), 1);
    }
    return order;
  }

  public synchronized void lockPaySuccess(ScanCodeOrder order, Map<String, String> backMap) {
    paySuccess(order, backMap);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(ScanCodeOrder order, Map<String, String> backMap) {
    if (order.getState() == 0) {

      //完成时间和入账时间
      YBCallback callback = checkOrderDate();
      order.setSettleDate(callback.getSettleDate());
      order.setCompleteDate(callback.getDateCompleted());

      long rebateWay = order.getOrderType();
      if (rebateWay == 12001 || rebateWay == 12003) {
      } else {
        //对于乐加会员在签约商家消费,消费成功后a,b积分均改变,
        try {
          scoreAService.paySuccessForMember(order);
          scoreCService.paySuccess(order);
        } catch (Exception e) {
          log.error("易宝订单出现问题===========" + order.getOrderSid());
          throw e;
        }
        //对于会员,判断是否需要绑定商户和合伙人
        leJiaUserService.checkUserBindMerchant(order.getLeJiaUser(), order.getMerchant());
        //分润
        if (order.getShare() > 0) {
          orderShareService.offLIneOrderShare(order);
        }
      }
      order.setState(1);
      orderRepository.save(order);
      //23:30:00~23:59:30支付成功实时转账
      if (callback.getType() == 2) { //转账
        ledgerTransferService
            .transfer(order.getScanCodeOrderExt().getMerchantNum(), order.getTransferMoney(),
                      order.getSettleDate(), 1);
      }
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
                          order.getScoreC(),
                          order.getLeJiaUser().getWeiXinUser().getOpenId(), order.getOrderSid(),order.getScanCodeOrderExt().getPayType());
        Long orderType = order.getOrderType();
        if (orderType == 12001L || orderType == 12002L || orderType == 12003L) {
          orderType = 0L;
        } else {
          orderType = 1L;
        }
        wxTemMsgService
            .sendToMerchant(order.getTotalPrice(), order.getOrderSid(), order.getLePayCode(),
                            merchant, orderType);
      }).start();
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkOrderState(String orderSid, String settleDate) {
    ScanCodeOrder order = findOrderByOrderSid(orderSid);
    if (order.getState() == 0) {
      try {
        Map<String, String> result = yeepayService.checkOrderState(order);
        System.out.println("订单查询接口==============================" + result.toString());
        String trans_stat = result.get("status");
//          String orderCode = result.get("transaction_id");
        if ("SUCCESS".equals(trans_stat)) {
          //对订单进行处理
          checkMessageState(order);
          paySuccess(order, result);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * 易宝结算临界点匹配
   */
  private YBCallback checkOrderDate() {
    YBCallback callback = new YBCallback();
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currDate = sdf.format(date);
    Calendar calendar = Calendar.getInstance();
    //获取今日三个临界点 23:30:00、23:59:30、00:00:00
    Date date000000 = null;
    Date date233000 = null;
    Date date235930 = null;
    try {
      date000000 = sdf2.parse(currDate + " 00:00:00");
      date233000 = sdf2.parse(currDate + " 23:30:00");
      date235930 = sdf2.parse(currDate + " 23:59:30");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    //1=[00:00:00~23:30:00]|2=(23:30:00~23:59:30]|3=(23:59:30~00:00:00)
    if (date.compareTo(date000000) > -1 && date.compareTo(date233000) < 1) {
      callback.setType(1);
      callback.setSettleDate(currDate);
      callback.setDateCompleted(date);
    } else if (date.compareTo(date233000) == 1 && date.compareTo(date235930) < 1) {
      callback.setType(2);
      callback.setSettleDate(currDate);
      callback.setDateCompleted(date);
    } else {
      callback.setType(3);
      //完成时间延后
      calendar.setTime(date);
      calendar.add(Calendar.MINUTE, 2);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 1);
      Date dateCompleted = calendar.getTime();
      callback.setDateCompleted(dateCompleted);
      callback.setSettleDate(sdf.format(dateCompleted));
    }
    return callback;
  }
}
