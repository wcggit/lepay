package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrderShare;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.order.repository.OffLineOrderRepository;
import com.jifenke.lepluslive.order.repository.OffLineOrderShareRepository;
import com.jifenke.lepluslive.partner.service.PartnerService;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;
import com.jifenke.lepluslive.wxpay.service.WeixinPayLogService;
import com.jifenke.lepluslive.wxpay.service.WxTemMsgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
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
  private WeiXinUserService weiXinUserService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private WxTemMsgService wxTemMsgService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private WeixinPayLogService weixinPayLogService;

  @Inject
  private OrderShareService orderShareService;




  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder createOffLineOrderForNoNMember(String truePrice, Long merchantId,
                                                     WeiXinUser weiXinUser, boolean pure,Long payWay) {
    OffLineOrder offLineOrder = new OffLineOrder();
    Long truePirce = new BigDecimal(truePrice).multiply(new BigDecimal(100)).longValue();
    offLineOrder.setLeJiaUser(weiXinUser.getLeJiaUser());
    offLineOrder.setTotalPrice(truePirce);
    offLineOrder.setTruePay(truePirce);
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
      }
    } else {
      if (merchant.getLjBrokerage().doubleValue() != 0) {
        offLineOrder.setLjCommission(
            Math.round(truePirce * merchant.getLjBrokerage().doubleValue() / 100.0));
      }
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
                                                  String totalPrice,
                                                  LeJiaUser leJiaUser,Long payWay
  ) {
    OffLineOrder offLineOrder = new OffLineOrder();
    long truePay = Long.parseLong(truePrice);
    long total = Long.parseLong(totalPrice);
    long scoreA = Long.parseLong(trueScore);
    offLineOrder.setLeJiaUser(leJiaUser);
    offLineOrder.setTotalPrice(total);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(truePay);
    offLineOrder.setCreatedDate(new Date());
    Merchant merchant = merchantService.findMerchantById(merchantId);
    offLineOrder.setMerchant(merchant);
    offLineOrder.setRebateWay(2);
    offLineOrder.setWxCommission(Math.round(truePay * 6 / 1000.0));
    offLineOrder.setPayWay(new PayWay(payWay));
    if (merchant.getLjCommission().doubleValue() != 0) {
      long
          ljCommission =
          Math.round(
              new BigDecimal(total).multiply(merchant.getLjCommission()).divide(new BigDecimal(100))
                  .doubleValue());
      offLineOrder.setLjCommission(ljCommission);
      if (merchant.getPartnership() != 0) { //代表联盟商户
        if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId() == merchant
            .getId()) { //代表会员订单
          offLineOrder.setRebateWay(3);
          ljCommission =
              Math.round(
                  new BigDecimal(total).multiply(merchant.getMemberCommission())
                      .divide(new BigDecimal(100))
                      .doubleValue());
          offLineOrder.setLjCommission(ljCommission);
          if (ljCommission - offLineOrder.getWxCommission() > 0) { //如果会员佣金大于微信手续费则发红包
            offLineOrder.setRebate(ljCommission - offLineOrder.getWxCommission());
          }
        } else { //导流订单
          offLineOrder.setRebateWay(1);
          long
              rebate =
              Math.round(ljCommission * merchant.getScoreARebate().doubleValue() / 100.0);
          offLineOrder.setRebate(rebate);
        }
      }
    }

    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder
        .setTransferMoneyFromTruePay(new BigDecimal(truePay).divide(new BigDecimal(total), 2)
                                         .multiply(new BigDecimal(
                                             offLineOrder.getTransferMoney())).longValue());
    long scoreB = Math.round(total * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    repository.save(offLineOrder);
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(String orderSid) {
    OffLineOrder offLineOrder = repository.findByOrderSid(orderSid);
    if (offLineOrder.getState() == 0) {
      offLineOrder.setCompleteDate(new Date());
      if (offLineOrder.getRebateWay() == 0) {
        //对于非会员 消费后只增加b积分
        scoreBService.paySuccess(offLineOrder);
        //如果是会员在非签约商家消费,同样要处理红包
//        if (offLineOrder.getTrueScore() != 0) {
//          scoreAService.paySuccessForMember(offLineOrder);
//        }
      } else {
        //对于乐加会员在签约商家消费,消费成功后a,b积分均改变,
        try {
          scoreAService.paySuccessForMember(offLineOrder);
        } catch (Exception e) {
          log.error("该笔订单出现问题===========" + orderSid);
        }
        scoreBService.paySuccess(offLineOrder);
        //对于会员,判断是否需要绑定商户和合伙人
        leJiaUserService
            .checkUserBindMerchant(offLineOrder.getLeJiaUser(), offLineOrder.getMerchant());

        //对于返庸订单分润
        if (offLineOrder.getRebateWay() == 1) {
          new Thread(() -> {
            orderShareService.offLIneOrderShare(offLineOrder);
          }).start();
        }

      }
      offLineOrder.setState(1);
      repository.save(offLineOrder);
    }
  }



  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder findOffLineOrderByOrderSid(String orderSid) {
    return repository.findByOrderSid(orderSid);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder payByScoreA(String userSid, String merchantId, String totalPrice,Long payWay) {
    OffLineOrder offLineOrder = new OffLineOrder();
    long scoreA = Long.parseLong(totalPrice);
    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(userSid);
    offLineOrder.setLeJiaUser(leJiaUser);
    offLineOrder.setTotalPrice(scoreA);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(0L);
    offLineOrder.setCreatedDate(new Date());
    Merchant merchant = merchantService.findMerchantById(Long.parseLong(merchantId));
    offLineOrder.setMerchant(merchant);
    offLineOrder.setState(1);
    offLineOrder.setPayWay(new PayWay(payWay));
    long scoreB = Math.round(scoreA * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    if (merchant.getLjCommission().doubleValue() != 0) {
      long
          ljCommission =
          Math.round(new BigDecimal(scoreA).multiply(merchant.getLjCommission())
                         .divide(new BigDecimal(100)).doubleValue());
      offLineOrder.setLjCommission(ljCommission);

      if (merchant.getPartnership() != 0) { //代表乐加会员在签约商家消费
        if (leJiaUser.getBindMerchant() != null && leJiaUser.getBindMerchant().getId() == merchant
            .getId()) { //代表会员订单
          offLineOrder.setRebateWay(3);
          ljCommission =
              Math.round(
                  new BigDecimal(scoreA).multiply(merchant.getMemberCommission())
                      .divide(new BigDecimal(100))
                      .doubleValue());
          offLineOrder.setLjCommission(ljCommission);
          if (ljCommission - offLineOrder.getWxCommission() > 0) { //如果会员佣金大于微信手续费则发红包
            offLineOrder.setRebate(ljCommission - offLineOrder.getWxCommission());
          }
        } else { //导流订单
          offLineOrder.setRebateWay(1);
          long
              rebate =
              Math.round(ljCommission * merchant.getScoreARebate().doubleValue() / 100.0);
          offLineOrder.setRebate(rebate);
          new Thread(() -> {
            orderShareService.offLIneOrderShare(offLineOrder);
          }).start();
        }
      } else {
        offLineOrder.setRebateWay(2); //会员普通订单
      }
    }
    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder.setTransferMoneyFromTruePay(0L);
    scoreAService.paySuccessForMember(offLineOrder);
    scoreBService.paySuccess(offLineOrder);
    offLineOrder.setCompleteDate(new Date());
    merchantService.paySuccess(offLineOrder);
    wxTemMsgService.sendToClient(offLineOrder);
    wxTemMsgService.sendToMerchant(offLineOrder);
    offLineOrder.setMessageState(1);
    repository.save(offLineOrder);

    //判断是否需要绑定商户
    leJiaUserService
        .checkUserBindMerchant(offLineOrder.getLeJiaUser(), offLineOrder.getMerchant());
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
          paySuccess(orderSId);
        }
      }
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkMessageState(String orderSid) {
    OffLineOrder offLineOrder = repository.findByOrderSid(orderSid);
    if (offLineOrder.getMessageState() == 0) {
      offLineOrder.setMessageState(1);
      repository.save(offLineOrder);
      new Thread(() -> {
        wxTemMsgService.sendToClient(offLineOrder);
        wxTemMsgService.sendToMerchant(offLineOrder);
      }).start();
    }
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
    checkMessageState(orderSid);
    lock.unlock();
  }

  public synchronized void lockPaySuccess(String orderSid) {
    paySuccess(orderSid);
  }
}
