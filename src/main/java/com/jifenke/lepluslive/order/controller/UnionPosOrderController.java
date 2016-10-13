package com.jifenke.lepluslive.order.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.service.UnionPosOrderService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
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
   * 银联POS机查看订单详情  16/10/11
   *
   * @param orderId 订单ID
   */
  @ApiOperation(value = "银联POS机查看订单详情")
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult login(@RequestParam Long orderId) {

    UnionPosOrder order = orderService.findUOrderById(orderId);
    if (order != null) {
      Map<Object, Object> map = new HashMap<>();
      map.put("totalPrice", order.getTotalPrice());
      map.put("orderSid", order.getOrderSid());
      map.put("paidType", order.getPaidType());
      map.put("trueScore", order.getTrueScore());
      map.put("truePay", order.getTruePay());
      map.put("completeDate",
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCompleteDate()));
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
      return LejiaResult.ok(map);
    }

    return LejiaResult.build(5006, "未找到订单");
  }
}
