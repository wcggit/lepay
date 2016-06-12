package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.order.repository.OffLineOrderRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;
import com.jifenke.lepluslive.wxpay.service.WeixinPayLogService;
import com.jifenke.lepluslive.wxpay.service.WxTemMsgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.inject.Inject;

/**
 * Created by wcg on 16/5/5.
 */
@Service
@Transactional(readOnly = true)
public class OffLineOrderService {

  private static final Logger log = LoggerFactory.getLogger(OffLineOrderService.class);

  @Inject
  private OffLineOrderRepository offLineOrderRepository;

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


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder createOffLineOrderForNoNMember(String truePrice, Long merchantId,
                                                     String openid) {
    OffLineOrder offLineOrder = new OffLineOrder();
    Long truePirce = new BigDecimal(truePrice).multiply(new BigDecimal(100)).longValue();
    offLineOrder.setLeJiaUser(weiXinUserService.findWeiXinUserByOpenId(openid).getLeJiaUser());
    offLineOrder.setTotalPrice(truePirce);
    offLineOrder.setTruePay(truePirce);
    offLineOrder.setCreatedDate(new Date());
    offLineOrder.setRebateWay(0);
    Merchant merchant = merchantService.findMerchantById(merchantId);
    offLineOrder.setMerchant(merchant);
    if (merchant.getPartnership() == 0) {
      offLineOrder.setLjCommission(
          (long) Math.ceil(truePirce * merchant.getLjCommission().doubleValue() / 100.0));
    } else {
      offLineOrder.setLjCommission(
          (long) Math.ceil(truePirce * merchant.getLjBrokerage().doubleValue() / 100.0));
    }
    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    offLineOrder.setPayWay(new PayWay(1L));
    offLineOrderRepository.save(offLineOrder);
    long scoreB = (long) Math.ceil(truePirce * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder createOffLineOrderForMember(String truePrice, Long merchantId,
                                                  String trueScore,
                                                  String totalPrice,
                                                  LeJiaUser leJiaUser
  ) {
    OffLineOrder offLineOrder = new OffLineOrder();
    long truePirce = Long.parseLong(truePrice);
    long total = Long.parseLong(totalPrice);
    long scoreA = Long.parseLong(trueScore);
    offLineOrder.setLeJiaUser(leJiaUser);
    offLineOrder.setTotalPrice(total);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(truePirce);
    offLineOrder.setCreatedDate(new Date());
    Merchant merchant = merchantService.findMerchantById(merchantId);
    offLineOrder.setMerchant(merchant);
    offLineOrder.setRebateWay(2);
    offLineOrder.setWxCommission((long) Math.ceil(total * 6 / 1000.0));
    offLineOrder.setPayWay(new PayWay(1L));
    long
        ljCommission =
        (long) Math.ceil(
            new BigDecimal(total).multiply(merchant.getLjCommission()).divide(new BigDecimal(100))
                .doubleValue());
    offLineOrder.setLjCommission(ljCommission);
    if (merchant.getPartnership() != 0) {
      offLineOrder.setRebateWay(1);
      long
          rebate =
          (long) Math.ceil(ljCommission * merchant.getScoreARebate().doubleValue() / 100.0);
      offLineOrder.setRebate(rebate);
    }
    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    long scoreB = (long) Math.ceil(total * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    offLineOrderRepository.save(offLineOrder);
    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(String orderSid) {
    OffLineOrder offLineOrder = offLineOrderRepository.findByOrderSid(orderSid);
    // 再开一个线程,为用户和商家推送2条模版消息

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
      }
      //不管会员还说非会员,消费完成对商家待转账金额增加
      merchantService.paySuccess(offLineOrder);
      offLineOrder.setState(1);
      offLineOrderRepository.save(offLineOrder);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder findOffLineOrderByOrderSid(String orderSid) {
    return offLineOrderRepository.findByOrderSid(orderSid);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public OffLineOrder payByScoreA(String userSid, String merchantId, String totalPrice) {
    OffLineOrder offLineOrder = new OffLineOrder();
    long scoreA = Long.parseLong(totalPrice);
    offLineOrder.setLeJiaUser(leJiaUserService.findUserByUserSid(userSid));
    offLineOrder.setTotalPrice(scoreA);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(0L);
    offLineOrder.setCreatedDate(new Date());
    Merchant merchant = merchantService.findMerchantById(Long.parseLong(merchantId));
    offLineOrder.setMerchant(merchant);
    offLineOrder.setWxCommission((long) Math.ceil(scoreA * 6 / 1000.0));
    offLineOrder.setState(1);
    offLineOrder.setPayWay(new PayWay(2L));
    long scoreB = (long) Math.ceil(scoreA * merchant.getScoreBRebate().doubleValue() / 10000.0);
    offLineOrder.setScoreB(scoreB);
    long
        ljCommission =
        (long) Math.ceil(new BigDecimal(scoreA).multiply(merchant.getLjCommission())
                             .divide(new BigDecimal(100)).doubleValue());
    offLineOrder.setLjCommission(ljCommission);
    if (merchant.getPartnership() != 0) { //代表乐加签约商家,会给消费者返现
      offLineOrder.setRebateWay(1);

      long
          rebate =
          (long) Math.ceil(ljCommission * merchant.getScoreARebate()
                  .doubleValue() / 100.0);
      offLineOrder.setRebate(rebate);
    } else {
      offLineOrder.setRebateWay(2);
    }
    offLineOrder.setTransferMoney(offLineOrder.getTotalPrice() - offLineOrder.getLjCommission());
    scoreAService.paySuccessForMember(offLineOrder);
    scoreBService.paySuccess(offLineOrder);
    offLineOrder.setCompleteDate(new Date());
    merchantService.paySuccess(offLineOrder);
    wxTemMsgService.sendToClient(offLineOrder);
    wxTemMsgService.sendToMerchant(offLineOrder);
    offLineOrder.setMessageState(1);
    offLineOrderRepository.save(offLineOrder);

    return offLineOrder;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkOrderState(String orderSId) {
    OffLineOrder offLineOrder = offLineOrderRepository.findByOrderSid(orderSId);
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
    OffLineOrder offLineOrder = offLineOrderRepository.findByOrderSid(orderSid);
    if (offLineOrder.getMessageState() == 0) {
      wxTemMsgService.sendToClient(offLineOrder);
      wxTemMsgService.sendToMerchant(offLineOrder);
    }
    offLineOrder.setMessageState(1);
    offLineOrderRepository.save(offLineOrder);
  }
}
