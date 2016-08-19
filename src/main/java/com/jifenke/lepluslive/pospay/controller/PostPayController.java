package com.jifenke.lepluslive.pospay.controller;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.order.service.PosOrderService;
import com.jifenke.lepluslive.pospay.domain.PosOrderResult;
import com.jifenke.lepluslive.pospay.domain.PosResult;
import com.jifenke.lepluslive.pospay.domain.PosResultInfo;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by wcg on 16/8/2.
 */
@RestController
@RequestMapping("/lepay")
public class PostPayController {

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private PosOrderService posOrderService;

  @Inject
  private ScoreAService scoreAService;


  @RequestMapping(value = "/pospay/non_member")
  public void posOrderForNonMember(@RequestParam String posId, @RequestParam String orderNo,
                                   @RequestParam Integer act, @RequestParam Integer pos,
                                   @RequestParam Integer groupon,
                                   @RequestParam String orderTime, @RequestParam
  String orderPrice, @RequestParam Integer paidType, HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    if (act == 1 && pos == 1 && groupon == 5 && paidType == 1) {
      posOrderService.createPosOrderForNoNMember(posId, orderNo, orderTime, orderPrice);
    }
  }


  @RequestMapping(value = "/pospay/non_member/after_pay")
  public void posOrderForNonMemberAfterPay(@RequestParam String posId,
                                           @RequestParam String orderNo,
                                           @RequestParam Integer act,
                                           @RequestParam Integer pos,
                                           @RequestParam Integer groupon,
                                           @RequestParam String paidTime, @RequestParam
  String orderPrice, @RequestParam Integer paidType, @RequestParam Integer tradeFlag,
                                           @RequestParam Integer paid,
                                           @RequestParam String paidPoints,
                                           @RequestParam String paidMoney,
                                           HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    if (act == 10 && pos == 1 && groupon == 5 && paidType == 1 && paid == 1) {
      posOrderService.nonMemberAfterPay(posId, orderNo, paidTime, orderPrice, paidPoints, paidMoney,
                                        tradeFlag);
    }
  }

  @RequestMapping(value = "/pospay/member/confirm")
  public PosResult posOrderCheckMember(@RequestParam String posId,
                                       @RequestParam String orderNo,
                                       @RequestParam Integer act,
                                       @RequestParam Integer pos,
                                       @RequestParam Integer groupon,
                                       @RequestParam String orderTime, @RequestParam
  String orderPrice, @RequestParam String cardNo, HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    PosResult posResult = new PosResult();
    posResult.setCode(111);
    posResult.setMessage("卡不是会员卡");
    PosResultInfo posResultInfo = new PosResultInfo();
    posResult.setData(posResultInfo);
    posResultInfo.setPos(1);
    posResultInfo.setGroupon(5);
    posResultInfo.setPosId(posId);
    posResultInfo.setOrderNo(orderNo);
    posResultInfo.setPointScale(100);
    posResultInfo.setState(0);
    posResultInfo.setPoints(0L);
    posResultInfo.setCardNo(cardNo);
    posResultInfo.setStore_name("一品江南");
    if (cardNo != null && act == 2 && pos == 1 && groupon == 5) {
      LeJiaUser leJiaUser = null;
      if (cardNo.length() == 11) {
        leJiaUser = leJiaUserService.findUserByPhoneNumber(cardNo);
      } else {
        leJiaUser = leJiaUserService.findLeJiaUserByCard(cardNo);
      }
      if (leJiaUser != null && leJiaUser.getWeiXinUser().getState() == 1) {
        posResultInfo.setState(1);
        ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
        posResultInfo.setPoints(scoreA.getScore());
        posResult.setCode(200);
        posResult.setMessage("it is success 会员登录成功");
      }
    }
    return posResult;
  }

  @RequestMapping(value = "/pospay/member/after_pay")
  public
  @ResponseBody
  PosOrderResult memberAfterPay(@RequestParam String posId,
                                @RequestParam String orderNo,
                                @RequestParam Integer act,
                                @RequestParam Integer pos,
                                @RequestParam Integer groupon,
                                @RequestParam String paidTime, @RequestParam
  String orderPrice, @RequestParam Integer paidType, @RequestParam Integer tradeFlag,
                                @RequestParam Integer paid,
                                @RequestParam String paidPoints,
                                @RequestParam String paidMoney,
                                HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    PosOrderResult params = new PosOrderResult();
    String cardNo = null;
    Map paramsInfo = new HashMap<>();
    params.setMessage("order is success");
    params.setData(paramsInfo);
    paramsInfo.put("pos", "1");
    paramsInfo.put("groupon", "5");
    paramsInfo.put("posId", posId);
    paramsInfo.put("orderNo", orderNo);
    paramsInfo.put("cardNo", cardNo);
    paramsInfo.put("state", paid);
    paramsInfo.put("state", paid);
    paramsInfo.put("paidType", paidType);
    if (paidType == 2) {
      params.setCode(222);
    } else if (paidType == 3) {
      params.setCode(223);
    } else {
      params.setCode(224);
    }
    paramsInfo.put("usedPoints", paidPoints);
    paramsInfo.put("getPoints", 0);

    return params;

  }

}
