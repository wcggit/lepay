package com.jifenke.lepluslive.order.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.service.UnionPosOrderService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;

/**
 * 银联商务订单相关 Created by zhangwen on 16/10/11.
 */
@RestController
@RequestMapping("/lepay/u_order")
public class UnionPosOrderController {

  @Inject
  private UnionPosOrderService orderService;

  /**
   * POS机吊起支付插件前创建订单 16/11/23
   */
  @ApiOperation(value = "POS机吊起支付插件前创建订单")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public LejiaResult createOrder(@RequestParam Long merchantId, @RequestParam String account,
                                 @RequestParam Long userId, @RequestParam Long totalPrice,
                                 @RequestParam Long truePrice, @RequestParam Long trueScore,
                                 @RequestParam Integer channel) {
    Map
        result =
        orderService
            .createOrder(merchantId, account, userId, totalPrice, truePrice, trueScore, channel);
    if (!"200".equals("" + result.get("status"))) {
      return LejiaResult
          .build(Integer.valueOf("" + result.get("status")), "" + result.get("msg"));
    }
    return LejiaResult.ok(result.get("data"));
  }

  /**
   * 银联POS机查看订单详情  16/10/11
   *
   * @param orderId 订单ID
   */
  @ApiOperation(value = "银联POS机查看订单详情")
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public LejiaResult find(@RequestParam Long orderId) {

    UnionPosOrder order = orderService.findUOrderById(orderId);
    if (order != null) {
      return LejiaResult
          .ok(orderService.orderToMap(order, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
    }

    return LejiaResult.build(5006, "未找到订单");
  }

  /**
   * 银联POS机查看选定时间区域的订单列表  16/10/14
   *
   * @param merchantId 商户ID
   * @param startDate  查询起始时间
   * @param endDate    查询截止时间
   */
  @ApiOperation(value = "银联POS机查看选定时间区域的订单列表")
  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult orderList(@RequestParam Long merchantId, @RequestParam String startDate,
                        @RequestParam String endDate) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Merchant merchant = new Merchant();
    merchant.setId(merchantId);
    Date start = null;
    Date end = null;
    try {
      start = sdf.parse(startDate);
      end = sdf.parse(endDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return LejiaResult.build(501, "日期转换异常");
    }
    List<UnionPosOrder> list = orderService.findByCompleteDateBetween(merchant, start, end);
    List result = null;
    if (list != null && list.size() > 0) {
      result = formatOrderList(list);
    }
    return LejiaResult.ok(result);
  }

  //格式化交易列表
  private List formatOrderList(List<UnionPosOrder> list) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    SimpleDateFormat single = new SimpleDateFormat("MM-dd HH:mm");
    List<Map<Object, Object>> result = new ArrayList<>();
    for (UnionPosOrder o : list) {
      String currDay = sdf.format(o.getCompleteDate());
      Map<Object, Object> dayMap = null;
      for (Map<Object, Object> m : result) {
        if (currDay.equals(m.get("date"))) {
          dayMap = m;
          break;
        }
      }
      if (dayMap != null) {
        Long total = (Long) dayMap.get("total");
        dayMap.put("total", total + o.getTotalPrice());
        ArrayList<Map> newList = (ArrayList<Map>) dayMap.get("list");
        newList.add(orderService.orderListToMap(o, single));
        dayMap.put("list", newList);
      } else {
        dayMap = new HashMap<>();
        dayMap.put("date", currDay);
        dayMap.put("total", o.getTotalPrice());
        ArrayList<Map> newList = new ArrayList<>();
        newList.add(orderService.orderListToMap(o, single));
        dayMap.put("list", newList);
        result.add(dayMap);
      }
    }

    return result;
  }
}
