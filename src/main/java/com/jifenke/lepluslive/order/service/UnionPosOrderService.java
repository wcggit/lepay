package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.repository.UnionPosOrderRepository;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/9.
 */
@Service
public class UnionPosOrderService {

  @Inject
  private UnionPosOrderRepository orderRepository;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private OrderShareService orderShareService;

  @Inject
  private LeJiaUserService userService;

  /**
   * 用户在某个商户用银联POS消费成功的次数和总额 16/10/10
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Object[] countByLeJiaUserAndMerchant(Long leJiaUserId, Long merchantId) {
    return orderRepository.countByLeJiaUserAndMerchantAndState(leJiaUserId, merchantId).get(0);
  }

  /**
   * 根据起止时间查看POS完成订单列表 16/10/14
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<UnionPosOrder> findByCompleteDateBetween(Merchant merchant, Date startDate,
                                                       Date endDate) {
    return orderRepository.findByMerchantAndStateAndCompleteDateBetweenOrderByIdDesc(merchant, 1,
                                                                                     startDate,
                                                                                     endDate);
  }

  /**
   * 银联POS机查看某一订单详情 16/10/11
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public UnionPosOrder findUOrderById(Long orderId) {
    return orderRepository.findOne(orderId);
  }

  public Map orderToMap(UnionPosOrder order, SimpleDateFormat sdf) {
    Map<Object, Object> map = new HashMap<>();
    map.put("totalPrice", order.getTotalPrice());
    map.put("orderSid", order.getOrderSid());
    map.put("paidType", order.getPaidType());
    map.put("trueScore", order.getTrueScore());
    map.put("truePay", order.getTruePay());
    map.put("completeDate", sdf.format(order.getCompleteDate()));
    map.put("rebateWay", order.getRebateWay());
    map.put("account", order.getAccount());
    LeJiaUser leJiaUser = order.getLeJiaUser();
    if (leJiaUser != null) {
      map.put("bindMerchant",
              leJiaUser.getBindMerchant() != null ? leJiaUser.getBindMerchant().getId() : 0);
      WeiXinUser user = leJiaUser.getWeiXinUser();
      if (user != null) {
        map.put("headImageUrl", user.getHeadImageUrl());
        map.put("state", user.getState());
      }
    }
    return map;
  }

  public Map orderListToMap(UnionPosOrder order, SimpleDateFormat sdf) {
    Map<Object, Object> map = new HashMap<>();
    map.put("id", order.getId());
    map.put("totalPrice", order.getTotalPrice());
    map.put("orderSid", order.getOrderSid());
    map.put("paidType", order.getPaidType());
    map.put("trueScore", order.getTrueScore());
    map.put("truePay", order.getTruePay());
    map.put("completeDate", sdf.format(order.getCompleteDate()));
    map.put("rebateWay", order.getRebateWay());
    map.put("account", order.getAccount());
    return map;
  }

  /**
   * POS机全红包支付 16/10/14
   *
   * @param m       商户
   * @param account 登录商户名
   * @param u       消费者
   * @param money   金额
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Map pureScorePay(Merchant m, String account, LeJiaUser u, Long money) {
    Map<Object, Object> result = new HashMap<>();
    ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(u);
    if (m == null || u == null || scoreA == null || money == null || money <= 0) {
      result.put("status", 9001);
      result.put("msg", "输入信息有误");
    } else if (scoreA.getScore() < money) {
      result.put("status", 6004);
      result.put("msg", "红包不足");
    } else {
      Date date = new Date();
      UnionPosOrder order = new UnionPosOrder();
      order.setAccount(account);
      order.setCompleteDate(date);
      order.setCreatedDate(date);
      order.setLeJiaUser(u);
      order.setMerchant(m);
      order.setOrderSid(MvUtil.getOrderNumber());
      order.setPaidType(2);
      order.setTotalPrice(money);
      order.setTrueScore(money);
      order.setState(1);
      long scoreB = Math.round(money * m.getScoreBRebate().doubleValue() / 10000.0);
      order.setScoreB(scoreB);
      if (m.getLjCommission().doubleValue() != 0) {
        long
            ljCommission =
            Math.round(new BigDecimal(money).multiply(m.getLjCommission())
                           .divide(new BigDecimal(100)).doubleValue());
        order.setLjCommission(ljCommission);

        if (m.getPartnership() != 0) { //记录订单类型，但是都按照导流订单处理
          if (u.getBindMerchant() != null && u.getBindMerchant().getId().longValue() == m
              .getId()) { //代表会员订单
            order.setRebateWay(3);
          } else { //导流订单
            order.setRebateWay(1);
          }
          long
              rebate =
              Math.round(ljCommission * m.getScoreARebate().doubleValue() / 100.0);
          order.setRebate(rebate);
          new Thread(() -> {
            orderShareService.offLIneOrderShare(order);
          }).start();
        } else {
          order.setRebateWay(2); //会员普通订单
        }
      }
      long transMoney = order.getTotalPrice() - order.getLjCommission();
      order.setTransferMoney(transMoney);
      order.setTransferByBank(0L);
      order.setTransferByScore(transMoney);
      scoreAService.paySuccessForMember(order);
      scoreBService.paySuccess(order);
      orderRepository.save(order);
      //判断是否需要绑定商户
      userService.checkUserBindMerchant(u, m);
      result.put("status", 200);
      result.put("data", orderToMap(order, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
    }
    return result;
  }

  /**
   * POS机混合支付掉支付插件前创建订单 16/10/19
   *
   * @param m          商户
   * @param account    登录商户名
   * @param u          消费者
   * @param totalPrice 订单总额
   * @param truePrice  实际支付
   * @param trueScore  使用红包
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Map createOrder(Merchant m, String account, LeJiaUser u, Long totalPrice,
                         Long truePrice,
                         Long trueScore) {
    Map<Object, Object> result = new HashMap<>();
    ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(u);
    if (m == null || u == null || scoreA == null || totalPrice == null || truePrice == null
        || trueScore == null || totalPrice <= 0 || truePrice <= 0 || trueScore < 0) {
      result.put("status", 9001);
      result.put("msg", "输入信息有误");
    } else if (scoreA.getScore() < trueScore) {
      result.put("status", 6004);
      result.put("msg", "红包不足");
    } else {
      Date date = new Date();
      UnionPosOrder order = new UnionPosOrder();
      order.setAccount(account);
      order.setCreatedDate(date);
      order.setLeJiaUser(u);
      order.setMerchant(m);
      order.setOrderSid(MvUtil.getOrderNumber());
      order.setTotalPrice(totalPrice);
      order.setTruePay(truePrice);
      order.setTrueScore(trueScore);
      order.setState(0);
      if (trueScore == 0) {
        order.setPaidType(1);
      } else {
        order.setPaidType(3);
      }

      long scoreB = Math.round(totalPrice * m.getScoreBRebate().doubleValue() / 10000.0);
      order.setScoreB(scoreB);
      if (m.getLjCommission().doubleValue() != 0) {
        long
            ljCommission =
            Math.round(new BigDecimal(totalPrice).multiply(m.getLjCommission())
                           .divide(new BigDecimal(100)).doubleValue());
        order.setLjCommission(ljCommission);

        if (m.getPartnership() != 0) { //记录订单类型，但是都按照导流订单处理
          if (u.getBindMerchant() != null && u.getBindMerchant().getId().longValue() == m
              .getId()) { //代表会员订单
            order.setRebateWay(3);
          } else { //导流订单
            order.setRebateWay(1);
          }
          long
              rebate =
              Math.round(ljCommission * m.getScoreARebate().doubleValue() / 100.0);
          order.setRebate(rebate);
        } else {
          order.setRebateWay(2); //会员普通订单
        }
      }
      long transMoney = order.getTotalPrice() - order.getLjCommission();
      order.setTransferMoney(0L);
      order.setTransferByBank(0L);
      order.setTransferByScore(0L);
      orderRepository.saveAndFlush(order);
      result.put("status", 200);
      result.put("data", order.getId());
    }
    return result;
  }

  /**
   * POS机混合支付成功后通知 16/10/19
   *
   * @param orderId 订单ID
   * @param data    银联支付成功的JSON
   * @param account 账号
   * @param m       商户
   * @param u       消费者
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Map PaySuccess(Long orderId, String account, Merchant m, LeJiaUser u, String data) {
    Map<Object, Object> result = new HashMap<>();
    UnionPosOrder order = null;
    Date date = new Date();
    if (orderId == 0) {
      JSONObject object = JSONObject.fromObject(data);
      Map<Object, Object> info = (Map) object.get("transData");
      Long
          totalPrice =
          new BigDecimal("" + info.get("amt")).multiply(new BigDecimal(100)).longValue();
      order = new UnionPosOrder();
      order.setAccount(account);
      order.setCreatedDate(date);
      order.setCompleteDate(date);
      order.setMerchant(m);
      order.setLeJiaUser(u);
      order.setOrderSid(MvUtil.getOrderNumber());
      order.setPaidType(1);
      order.setTotalPrice(totalPrice);
      order.setTruePay(totalPrice);
      order.setState(1);
      order.setData(data);
      order.setRebateWay(0);
    } else {
      order = orderRepository.findOne(orderId);
      if (order != null) {
        if (order.getState() == 0) {
          order.setState(1);
          order.setData(data);
          order.setCompleteDate(date);
          scoreAService.paySuccessForMember(order);
          scoreBService.paySuccess(order);
        }
      } else {
        result.put("status", 5006);
        result.put("msg", "未找到该订单");
        return result;
      }
    }
    orderRepository.save(order);
    result.put("status", 200);

    return result;
  }

}
