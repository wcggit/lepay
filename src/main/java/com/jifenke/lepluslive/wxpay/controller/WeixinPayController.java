package com.jifenke.lepluslive.wxpay.controller;

import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.printer.service.PrinterService;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;
import com.jifenke.lepluslive.wxpay.service.WeixinPayLogService;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private ScoreCService scoreCService;

  @Inject
  private ScoreAService scoreAService;


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

//  @RequestMapping(value = "/paySuccess")
//  public ModelAndView goPaySuccessPageForMember(@RequestParam String orderSid, Model model) {
//    offLineOrderService.checkOrderState(orderSid);
//    OffLineOrder offLineOrder = offLineOrderService.findOffLineOrderByOrderSid(orderSid);
//    model.addAttribute("offLineOrder", offLineOrder);
//    model.addAttribute("scoreC", scoreCService.findScoreCByleJiaUser(offLineOrder.getLeJiaUser()));
//    model.addAttribute("scoreA", scoreAService.findScoreAByLeJiaUser(offLineOrder.getLeJiaUser()));
//    if (offLineOrder.getRebateWay() != 1 && offLineOrder.getRebateWay() != 3) {
//      if (offLineOrder.getPolicy().endsWith("1")) {
//        if (offLineOrder.getRebateWay() == 2 || offLineOrder.getRebateWay() == 6
//            || offLineOrder.getRebateWay()
//               == 5) { //鼓励金策略订单类型为【会员订单（普通费率）】或者【普通订单（会员消费）】或者是【会员扫纯支付码】
//          return MvUtil.go("/weixin/newpolicy/paySuccessNonActivity");
//        }
//        if (offLineOrder.getRebateWay() == 0 || offLineOrder.getRebateWay() == 4) {//非会员订单 注册引导
//          return MvUtil.go("/weixin/newpolicy/paySuccessReceiveMoney");
//        }
//      }
//      return MvUtil.go("/weixin/paySuccessForNoNMember");
//    } else {
//      if (offLineOrder.getPolicy().endsWith("1")) { //鼓励金策略订单
//        Calendar calendar = Calendar.getInstance();
////        int day = calendar.get(Calendar.DAY_OF_WEEK);//今天星期几
//        int day = 5;//今天星期几
//        Date end = calendar.getTime();
//        model.addAttribute("day", day);
//        List<String> weekends = new ArrayList<>();
//        int toMonday = day == 1 ? 6 : day - 2;
//        Calendar weekStart = Calendar.getInstance();
//        weekStart.add(Calendar.DAY_OF_WEEK, -toMonday);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M.dd");
//        weekends.add(simpleDateFormat.format(weekStart.getTime()));
//        for (int i = 0; i < 6; i++) {
//          weekStart.add(Calendar.DAY_OF_WEEK, 1);
//          weekends.add(simpleDateFormat.format(weekStart.getTime()));
//        }
//        model.addAttribute("weekends",weekends);
//        if (day != 1) {
//          calendar.set(Calendar.MILLISECOND, 0);
//          calendar.set(Calendar.SECOND, 0);
//          calendar.set(Calendar.MINUTE, 0);
//          calendar.set(Calendar.HOUR_OF_DAY, 0);
//          calendar.add(Calendar.DAY_OF_WEEK, -(day - 2));
//          Date start = calendar.getTime();
//          model.addAttribute("dailyRebate", offLineOrderService
//              .statisticRebateGroupByCompleteDate(offLineOrder.getLeJiaUser(), start, end, day));
//        } else {
//          calendar.set(Calendar.MILLISECOND, 0);
//          calendar.set(Calendar.SECOND, 0);
//          calendar.set(Calendar.MINUTE, 0);
//          calendar.set(Calendar.HOUR, 0);
//          calendar.add(Calendar.DAY_OF_WEEK, -6);
//          Date start = calendar.getTime();
//          model.addAttribute("dailyRebate", offLineOrderService
//              .statisticRebateGroupByCompleteDate(offLineOrder.getLeJiaUser(), start, end, day));
//        }
//        if (day < Calendar.THURSDAY && day > Calendar.SUNDAY) { //星期1 至星期3
//          return MvUtil.go("/weixin/newpolicy/paySuccessglj");
//        } else if (day == Calendar.THURSDAY && offLineOrder.getCriticalOrder() == 1) { //暴击订单且星期4
//          return MvUtil.go("/weixin/newpolicy/paySuccessbj");
//        } else { //星期5,6,7且星期4非暴击订单
//          return MvUtil.go("/weixin/newpolicy/paySuccessybj");
//        }
//      }
//      return MvUtil.go("/weixin/paySuccessForMember");
//    }
//  }

  @RequestMapping(value = "/paySuccess")
  public ModelAndView goPaySuccessPageForMember(Model model) {
    OffLineOrder offLineOrder = offLineOrderService.findOffLineOrderByOrderSid("17010416295834626");
    model.addAttribute("offLineOrder", offLineOrder);
    model.addAttribute("scoreC", scoreCService.findScoreCByleJiaUser(offLineOrder.getLeJiaUser()));
    model.addAttribute("scoreA", scoreAService.findScoreAByLeJiaUser(offLineOrder.getLeJiaUser()));
    return MvUtil.go("/weixin/newpolicy/paySuccessReceiveMoney");
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
