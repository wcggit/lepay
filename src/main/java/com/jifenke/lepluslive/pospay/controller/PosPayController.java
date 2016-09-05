package com.jifenke.lepluslive.pospay.controller;

import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
import com.jifenke.lepluslive.merchant.service.MerchantPosService;
import com.jifenke.lepluslive.order.domain.entities.PosOrder;
import com.jifenke.lepluslive.order.service.PosOrderService;
import com.jifenke.lepluslive.pospay.domain.PosOrderResult;
import com.jifenke.lepluslive.pospay.domain.PosResult;
import com.jifenke.lepluslive.pospay.domain.PosResultInfo;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PosPayController {

  private static Logger log = LoggerFactory.getLogger(PosPayController.class);

  String vcpos = "VCPOS:F81DF6823EEE3E7DBEF286EB4DF2FBD1";


  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private PosOrderService posOrderService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private MerchantPosService merchantPosService;


  @RequestMapping(value = "/pospay/non_member")
  public void posOrderForNonMember(@RequestParam String posId, @RequestParam String orderNo,
                                   @RequestParam Integer act, @RequestParam Integer pos,
                                   @RequestParam Integer groupon,
                                   @RequestParam String orderTime, @RequestParam
  String orderPrice, @RequestParam String token, HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    String content = queryString.split("&token")[0];
    String md5Encode = MD5Util.MD5Encode(vcpos + content, "utf-8");
    if (token.equals(md5Encode) && act == 1 && pos == 1 && groupon == 5) {
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
  String orderPrice, @RequestParam String cardNo, @RequestParam String token,
                                       HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    String content = queryString.split("&token")[0];
    String md5Encode = MD5Util.MD5Encode(vcpos + content, "utf-8");
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    PosResult posResult = new PosResult();
    posResult.setMessage("卡不是会员卡");
    PosResultInfo posResultInfo = new PosResultInfo();
    posResult.setData(posResultInfo);
    posResultInfo.setPos(pos);
    posResultInfo.setGroupon(groupon);
    posResultInfo.setPosId(posId);
    posResultInfo.setOrderNo(orderNo);
    posResultInfo.setPointScale(100);
    posResult.setCode(111);
    posResultInfo.setState(0);
    posResultInfo.setPoints(0L);
    posResultInfo.setCardNo(cardNo);
    posResultInfo.setStore_name(merchantPos.getMerchant().getName());
    if (merchantPos == null) {
      log.error("找不到对应的pos,id=" + posId);
      return posResult;
    }
    if (token.equals(md5Encode) && cardNo != null && act == 2 && pos == 1 && groupon == 5) {
      LeJiaUser leJiaUser = null;
      if (cardNo.length() == 11) {
        leJiaUser = leJiaUserService.findUserByPhoneNumber(cardNo);
      } else {
        leJiaUser = leJiaUserService.findLeJiaUserByCard(cardNo);
      }
      if (leJiaUser != null && leJiaUser.getWeiXinUser().getState() == 1) {
        posResultInfo.setState(1);
        ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
        posOrderService
            .createPosOrderForMember(merchantPos, orderNo, orderPrice, orderTime, leJiaUser);
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
                                @RequestParam String cardNo,
                                HttpServletRequest request) {
    String queryString = request.getQueryString();
    posOrderService.createPosOrderLog(orderNo, queryString);
    PosOrderResult params = new PosOrderResult();
    Map paramsInfo = new HashMap<>();
    params.setMessage("order is success");
    params.setData(paramsInfo);
    paramsInfo.put("pos", pos);
    paramsInfo.put("groupon", groupon);
    paramsInfo.put("posId", posId);
    paramsInfo.put("orderNo", orderNo);
    paramsInfo.put("cardNo", cardNo);
    paramsInfo.put("state", paid);
    paramsInfo.put("paidType", paidType);
    PosOrder
        posOrder = null;
    if (paidType == 2) {
      params.setCode(222); //纯货币
    } else if (paidType == 3) {
      params.setCode(223);//混合支付
    } else {
      params.setCode(224); //纯积分
    }
    if (act == 20 && pos == 1 && groupon == 5 && paid == 1) {
      posOrder =
          posOrderService
              .memberAfterPay(posId, orderNo, paidTime, orderPrice, paidPoints, paidMoney,
                              tradeFlag, paidType, cardNo);
    }
    paramsInfo.put("usedPoints", paidPoints);
    paramsInfo.put("getPoints", posOrder == null ? 0 : posOrder.getRebate());

    return params;

  }

}
