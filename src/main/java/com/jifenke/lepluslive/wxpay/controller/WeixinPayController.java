package com.jifenke.lepluslive.wxpay.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.domain.entities.ScoreB;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinService;
import com.jifenke.lepluslive.wxpay.service.WeixinPayLogService;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wcg on 16/3/21.
 */
@RestController
@RequestMapping("/lepay/wxpay")
public class WeixinPayController {

  private static Logger log = LoggerFactory.getLogger(WeixinPayController.class);

  @Inject
  private OffLineOrderService offLineOrderService;

  @Inject
  private WeixinPayLogService weixinPayLogService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private DictionaryService dictionaryService;

  @Inject
  private ScoreBService scoreBService;

  /**
   * 微信回调函数
   */
  @RequestMapping(value = "/afterPay", produces = MediaType.APPLICATION_XML_VALUE)
  public void afterPay(HttpServletRequest request, HttpServletResponse response)
      throws IOException, JDOMException {
    InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "utf-8");
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String str = null;
    StringBuffer buffer = new StringBuffer();
    while ((str = bufferedReader.readLine()) != null) {
      buffer.append(str);
    }
    Map map = WeixinPayUtil.doXMLParse(buffer.toString());
    String orderSid = (String) map.get("out_trade_no");
    String returnCode = (String) map.get("return_code");
    String resultCode = (String) map.get("result_code");
    weixinPayLogService.savePayLog(orderSid, returnCode, resultCode);
    //操作订单
    if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
      //确定只发送一条模版消息;
      offLineOrderService.lockCheckMessageState(orderSid);
      //操作订单
      try {
        offLineOrderService.lockPaySuccess(orderSid);
      } catch (Exception e) {
        log.error(e.getMessage());
        buffer.delete(0, buffer.length());
        buffer.append("<xml>");
        buffer.append("<return_code>FAIL</" + "return_code" + ">");
        buffer.append("</xml>");
        String s = buffer.toString();
        response.setContentType("application/xml");
        response.getWriter().write(s);
        return;
      }
    }
    //返回微信的信息
    buffer.delete(0, buffer.length());
    buffer.append("<xml>");
    buffer.append("<return_code>" + returnCode + "</" + "return_code" + ">");
    buffer.append("</xml>");
    String s = buffer.toString();
    response.setContentType("application/xml");
    response.getWriter().write(s);
  }

  @RequestMapping(value = "/paySuccess")
  public ModelAndView goPaySuccessPageForMember(@RequestParam String orderSid, Model model) {
    offLineOrderService.checkOrderState(orderSid);
    OffLineOrder offLineOrder = offLineOrderService.findOffLineOrderByOrderSid(orderSid);
    model.addAttribute("offLineOrder", offLineOrder);
    if (offLineOrder.getRebateWay() != 1&&offLineOrder.getRebateWay() != 3) {
      return MvUtil.go("/weixin/paySuccessForNoNMember");
    } else {
      return MvUtil.go("/weixin/paySuccessForMember");
    }
  }

//  @RequestMapping(value = "/paySuccess")
//  public ModelAndView goPaySuccessPageForMember(@RequestParam String orderSid, Model model) {
//    offLineOrderService.checkOrderState(orderSid);
//    OffLineOrder offLineOrder = offLineOrderService.findOffLineOrderByOrderSid(orderSid);
//    model.addAttribute("offLineOrder", offLineOrder);
//    LeJiaUser leJiaUser = offLineOrder.getLeJiaUser();
//    Map<String, Object> map = new HashMap<>();
//    int tanChuang = 0;  //0=无弹窗|1=展示|2=发红包
//    if (leJiaUser != null) {
//      WeiXinUser weiXinUser = leJiaUser.getWeiXinUser();
//      ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
//      ScoreB scoreB = scoreBService.findScoreBByleJiaUser(leJiaUser);
//      if (weiXinUser != null && scoreA != null) {
//        if (weiXinUser.getState() == 1 && offLineOrder.getRebate() != 0) {
//          //是乐加会员且订单返红包不为0,弹出领取红包财神爷,滑动仅是展示效果,无实际作用
//          tanChuang = 1;
//          map.put("score", offLineOrder.getRebate());
//        } else if (weiXinUser.getState() == 0 && weiXinUser.getSubState() == 1) {
//          //不是乐加会员且已关注,弹出邀请您成为乐加会员财神爷,滑动触发发红包交互,并跳转到我的钱包页面
//          tanChuang = 2;
//          map.put("score", dictionaryService.findDictionaryById(29L).getValue());//发红包额度
//        }
//        map.put("currScoreA", scoreA.getScore());
//        map.put("totalScoreA", scoreA.getTotalScore());
//        if (scoreB != null) {
//          map.put("currScoreB", scoreB.getScore());
//        }
//      }
//    }
//    map.put("status", tanChuang);
//    model.addAttribute("map", map);
//
//    return MvUtil.go("/weixin/paySuccess");
//  }

}
