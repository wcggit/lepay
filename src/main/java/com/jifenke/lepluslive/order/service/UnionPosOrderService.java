package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.global.util.MathUtil;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUnionPos;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantUnionPosService;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.repository.UnionPosOrderRepository;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreCService;
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
 * 银联商务订单相关 Created by zhangwen on 16/8/9.
 */
@Service
public class UnionPosOrderService {

  @Inject
  private UnionPosOrderRepository orderRepository;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreCService scoreCService;

  @Inject
  private OrderShareService orderShareService;

  @Inject
  private LeJiaUserService userService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private MerchantUnionPosService unionPosService;

  /**
   * 第二版POS机支付调支付插件前创建订单 16/11/22
   *
   * @param merchantId 商户
   * @param account    登录商户名
   * @param userId     消费者 如果为0则未验证会员身份
   * @param totalPrice 订单总额
   * @param truePrice  实际支付
   * @param trueScore  使用红包
   * @param channel    支付渠道 0=刷卡|1=微信|2=支付宝
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Map createOrder(Long merchantId, String account, Long userId, Long totalPrice,
                         Long truePrice,
                         Long trueScore, Integer channel) {
    Map<Object, Object> result = new HashMap<>();
    Merchant m = merchantService.findMerchantById(merchantId);
    MerchantUnionPos pos = unionPosService.findByMerchantId(merchantId);
    if (m == null || pos == null || totalPrice == null || truePrice == null || trueScore == null
        || totalPrice <= 0 || truePrice < 0 || trueScore < 0 || (totalPrice
                                                                 != truePrice + trueScore)) {
      result.put("status", 9001);
      result.put("msg", "输入信息有误");
      return result;
    }
    Date date = new Date();
    BigDecimal totalPrice_decimal = new BigDecimal(totalPrice);
    BigDecimal truePrice_decimal = new BigDecimal(truePrice);
    BigDecimal trueScore_decimal = new BigDecimal(trueScore);
    UnionPosOrder order = new UnionPosOrder();
    order.setChannel(channel);
    order.setAccount(account);
    order.setCreatedDate(date);
    order.setMerchant(m);
    order.setOrderSid(MvUtil.getOrderNumber());
    order.setState(0);
    order.setOrderState(0);
    Long commission = 0L;//乐加总佣金
    Long ljCommission = 0L; //乐加佣金(红包部分)
    Long ysCommission = 0L; //实际支付(银商)佣金
    Long ysCharge = 0L; //银商实际收取的手续费
    Long wxCommission = 0L; //三方手续费
    Long transferMoney = 0L; //每笔应该转给商户的金额=transferByBank+transferByScore
    Long transferByBank = 0L; //银商转给商户的金额
    Long transferByScore = 0L; //红包部分转给商户的金额
    Long rebate = 0L; //返利红包
    Long scoreB = 0L; //发放金币
    Integer profit = 0;  //是否真的收佣金

    if (userId == 0) {//未验证会员身份，产生普通订单
      order.setPaidType(1);
      order.setTotalPrice(totalPrice);
      order.setTruePay(totalPrice);
      order.setTrueScore(0L);
      order.setRebateWay(0);
      if (channel != 0 && pos.getIsNonCardCommission() == 1) { //微信支付宝&收佣金
        profit = 1;
        commission = MathUtil.result(pos.getCommission(), totalPrice_decimal);
        ysCommission = commission;
        wxCommission = MathUtil.result(pos.getThirdRate(), truePrice_decimal);
        transferMoney = totalPrice - commission;
        transferByBank = transferMoney;
      }
    } else { //产生其他三种订单
      LeJiaUser u = userService.findUserById(userId);
      ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(u);
      if (u == null || scoreA == null) {
        result.put("status", 9002);
        result.put("msg", "未找到会员信息");
        return result;
      } else if (scoreA.getScore() < trueScore) {
        result.put("status", 6004);
        result.put("msg", "红包不足");
        return result;
      }
      order.setLeJiaUser(u);
      if (trueScore == 0) {
        order.setPaidType(1);
      } else {
        order.setPaidType(3);
      }
      order.setTotalPrice(totalPrice);
      order.setTruePay(truePrice);
      order.setTrueScore(trueScore);

      if (u.getBindMerchant() != null && u.getBindMerchant().getId().equals(m.getId())) { //会员订单
        if (pos.getUseCommission()) {//会员订单(佣金费率)
          profit = 1;
          order.setRebateWay(3);
          commission = MathUtil.result(pos.getCommission(), totalPrice_decimal);
          ysCommission = MathUtil.result(pos.getCommission(), truePrice_decimal);
          ljCommission = commission - ysCommission;
          wxCommission = MathUtil.result(pos.getThirdRate(), truePrice_decimal);
          transferMoney = totalPrice - commission;
          transferByBank = truePrice - ysCommission;
          transferByScore = trueScore - ljCommission;

          scoreB = MathUtil.result(totalPrice_decimal, pos.getUserScoreBRebate());
          rebate = MathUtil.result(new BigDecimal(commission), pos.getUserScoreARebate());
        } else {//会员订单(普通费率)
          order.setRebateWay(2);
          ljCommission = MathUtil.result(pos.getUserGeneralACommission(), trueScore_decimal);
          wxCommission = MathUtil.result(pos.getThirdRate(), truePrice_decimal);
          transferByScore = trueScore - ljCommission;
        }
      } else { //导流订单
        profit = 1;
        order.setRebateWay(1);
        commission = MathUtil.result(pos.getCommission(), totalPrice_decimal);
        ysCommission = MathUtil.result(pos.getCommission(), truePrice_decimal);
        ljCommission = commission - ysCommission;
        wxCommission = MathUtil.result(pos.getThirdRate(), truePrice_decimal);
        transferMoney = totalPrice - commission;
        transferByBank = truePrice - ysCommission;
        transferByScore = trueScore - ljCommission;

        scoreB = MathUtil.result(totalPrice_decimal, pos.getScoreBRebate());
        rebate = MathUtil.result(new BigDecimal(commission), pos.getScoreARebate());
      }

      if (channel != 0) {
        if (pos.getIsNonCardCommission() == 0) { //微信支付宝不收佣金
          profit = 0;
          scoreB = 0L;
          rebate = 0L;
        } else if (order.getRebateWay() == 2) { //微信支付宝收佣金&订单为会员订单(普通费率) 则不返红包和金币
          profit = 1;
          scoreB = 0L;
          rebate = 0L;
        }
      }
    }

    order.setCommission(commission);
    order.setYsCommission(ysCommission);
    order.setLjCommission(ljCommission);
    order.setWxCommission(wxCommission);
    order.setYsCharge(ysCharge);
    order.setTransferMoney(transferMoney);
    order.setTransferByBank(transferByBank);
    order.setTransferByScore(transferByScore);
    order.setRebate(rebate);
    order.setScoreB(scoreB);
    order.setProfit(profit);

    orderRepository.saveAndFlush(order);
    result.put("status", 200);
    result.put("data", order.getOrderSid());

    return result;
  }

  /**
   * POS机销账通知 16/11/21
   *
   * @param order      订单
   * @param parameters 销账参数
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(UnionPosOrder order, Map parameters) throws Exception {
//    Map<Object, Object> result = new HashMap<>();
    String orderCode = String.valueOf(parameters.get("req_serial_no"));
    String settleDate = String.valueOf(parameters.get("sett_date"));
    if (order != null) {
      if (order.getState() == 0) {
        order.setState(1);
        order.setOrderCode(orderCode);
        order.setCompleteDate(new Date());
        order.setOrderState(1);
        order.setSettleDate(settleDate);
        scoreAService.paySuccessForMember(order);
        scoreCService.paySuccess(order);
        new Thread(() -> { //分润
          orderShareService.offLIneOrderShare(order);
        }).start();
        //todo:等待绑定
      }
    } else {
      throw new RuntimeException();
    }
    orderRepository.save(order);
  }

  /**
   * POS机销账撤销和冲正通知 16/11/23
   *
   * @param order 订单
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void payReverse(UnionPosOrder order, int orderState) throws Exception {

    if (order != null) {
      if (order.getState() == 1) {
        order.setState(0);
        order.setCancelDate(new Date());
        order.setOrderState(orderState);
        //todo:等待撤销分润、收回红包和积分等
      }
    } else {
      throw new RuntimeException();
    }
    orderRepository.save(order);
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
      order.setChannel(3);
      order.setTotalPrice(money);
      order.setTrueScore(money);
      order.setState(1);
      order.setOrderState(1);
      order.setSettleDate(new SimpleDateFormat("yyyyMMdd").format(date));

      Long wxCommission = 0L; //三方手续费
      Long commission = 0L;//乐加总佣金
      Long transferMoney = 0L; //每笔应该转给商户的金额=transferByBank+transferByScore
      Long transferByScore = 0L; //红包部分转给商户的金额
      Long rebate = 0L; //返利红包
      Long scoreB = 0L; //发放金币
      Integer profit = 0;

      MerchantUnionPos pos = unionPosService.findByMerchantId(m.getId());
      BigDecimal totalPrice_decimal = new BigDecimal(money);
      if (u.getBindMerchant() != null && u.getBindMerchant().getId().equals(m.getId())) { //会员订单
        if (pos.getUseCommission()) {//会员订单(佣金费率)
          profit = 1;
          order.setRebateWay(3);
          commission = MathUtil.result(pos.getCommission(), totalPrice_decimal);
          transferMoney = money - commission;
          transferByScore = transferMoney;
          scoreB = MathUtil.result(totalPrice_decimal, pos.getUserScoreBRebate());
          rebate = MathUtil.result(new BigDecimal(commission), pos.getUserScoreARebate());
        } else {//会员订单(普通费率)
          order.setRebateWay(2);
          commission = MathUtil.result(pos.getUserGeneralACommission(), totalPrice_decimal);
          transferByScore = money - commission;
        }
      } else { //导流订单
        profit = 1;
        order.setRebateWay(1);
        commission = MathUtil.result(pos.getCommission(), totalPrice_decimal);
        transferMoney = money - commission;
        transferByScore = transferMoney;

        scoreB = MathUtil.result(totalPrice_decimal, pos.getScoreBRebate());
        rebate = MathUtil.result(new BigDecimal(commission), pos.getScoreARebate());
      }
      wxCommission = MathUtil.result(pos.getThirdRate(), totalPrice_decimal);

      order.setWxCommission(wxCommission);
      order.setLjCommission(commission);
      order.setCommission(commission);
      order.setTransferMoney(transferMoney);
      order.setTransferByScore(transferByScore);
      order.setRebate(rebate);
      order.setScoreB(scoreB);
      order.setProfit(profit);

      orderRepository.save(order);
      scoreAService.paySuccessForMember(order);
      scoreCService.paySuccess(order);
      if (profit == 1) {
        new Thread(() -> {
          orderShareService.offLIneOrderShare(order);
        }).start();
      }
      //todo:判断是否需要绑定商户
//      userService.checkUserBindMerchant(u, m);
      result.put("status", 200);
      result.put("data", orderToMap(order, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
    }
    return result;
  }


  /**
   * POS机微信或支付宝支付或纯刷卡（未注册）成功后APP通知 17/4/11
   *
   * @param orderSid 订单Sid
   * @param data     银联支付成功的JSON
   * @param account  账号
   * @param mId      商户Id
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Map PaySuccess(String orderSid, String account, Long mId, String data) {
    Map<Object, Object> result = new HashMap<>();
    result.put("status", 200);
    Date date = new Date();
//    System.out.println(orderSid + "=====" + account + "=====" + mId + "======" + data);
    JSONObject object = JSONObject.fromObject(data);
    Map<String, Object> info = (Map<String, Object>) object.get("transData");
//      Long
//          totalPrice =
//          new BigDecimal("" + info.get("amt")).multiply(new BigDecimal(100)).longValue();
    String
        payDate =
        info.get("date") != null ? ("" + info.get("date")).replaceAll("/", "")
                                 : new SimpleDateFormat("yyyyMMdd").format(date);
    UnionPosOrder order = orderRepository.findByOrderSid(orderSid);
    if (order != null) {
      if (order.getState() == 0) {
//        MerchantUser mu = merchantService.findMerchantUserByName(account);
        if (order.getMerchant().getId().equals(mId) && account.equals(order.getAccount())) {
          order.setProfit(0);
          order.setState(1);
          order.setOrderState(1);
          order.setCompleteDate(date);
          order.setSettleDate(payDate);
          //不发金币、红包.不分润
          order.setRebate(0L);
          order.setScoreB(0L);
          //如果是会员订单（佣金费率）或 导流订单,且实付红包>0 重新计算红包部分手续费
          if (order.getTrueScore() > 0 && (order.getRebateWay() == 1
                                           || order.getRebateWay() == 3)) {
            MerchantUnionPos pos = unionPosService.findByMerchantId(order.getMerchant().getId());
            Long
                ljCommission =
                MathUtil
                    .result(pos.getUserGeneralACommission(), new BigDecimal(order.getTrueScore()));
            order.setLjCommission(ljCommission);
            order.setTransferByScore(order.getTrueScore() - ljCommission);
          }
          orderRepository.saveAndFlush(order);
          if (order.getLeJiaUser() != null) {
            scoreAService.paySuccessForMember(order);
          }
        } else {
          result.put("status", 5007);
          result.put("msg", "订单数据异常");
        }
      }
    } else {
      result.put("status", 5006);
      result.put("msg", "未找到该订单");
    }
    return result;
  }

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

  /**
   * 获取某一订单 16/11/21
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public UnionPosOrder findByOrderSid(String orderSid) {
    return orderRepository.findByOrderSid(orderSid);
  }

  /**
   * 获取某一订单,根据银联销账流水号 16/11/23
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public UnionPosOrder findByOrderCode(String orderCode) {
    return orderRepository.findByOrderCode(orderCode);
  }

  public Map orderToMap(UnionPosOrder order, SimpleDateFormat sdf) {
    Map<Object, Object> map = new HashMap<>();
    map.put("totalPrice", order.getTotalPrice());
    map.put("orderSid", order.getOrderSid());
    map.put("paidType", order.getPaidType());
    map.put("channel", order.getChannel());
    map.put("trueScore", order.getTrueScore());
    map.put("truePay", order.getTruePay());
    map.put("completeDate", sdf.format(order.getCompleteDate()));
    map.put("rebateWay", order.getRebateWay());
    map.put("account", order.getAccount());
    map.put("backA", order.getRebate());
    map.put("backB", order.getScoreB());
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
    map.put("channel", order.getChannel());
    map.put("trueScore", order.getTrueScore());
    map.put("truePay", order.getTruePay());
    map.put("completeDate", sdf.format(order.getCompleteDate()));
    map.put("rebateWay", order.getRebateWay());
    map.put("account", order.getAccount());
    return map;
  }
}
