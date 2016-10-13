package com.jifenke.lepluslive.lejiauser.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.order.service.UnionPosOrderService;
import com.jifenke.lepluslive.pospay.domain.UserResult;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;

/**
 * 用户相关 Created by zhangwen on 16/10/10.
 */
@RestController
@RequestMapping("/lepay/user")
public class LeJiaUserController {

  @Inject
  private LeJiaUserService userService;

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private UnionPosOrderService unionPosOrderService;

  @Inject
  private OffLineOrderService offLineOrderService;

  /**
   * 银联商务POS机会员验证  16/10/10
   *
   * @param type       验证方式 1=扫用户码|2=手机号
   * @param token      识别码
   * @param merchantId 商户ID
   */
  @ApiOperation(value = "pos机验证用户是否是会员")
  @RequestMapping(value = "/check", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult login(@RequestParam Integer type, @RequestParam String token,
                    @RequestParam Long merchantId) {
    LeJiaUser user = null;
    if (type == 1) {
      user = userService.findUserByUserSid(token);
    } else if (type == 2) {
      user = userService.findUserByPhoneNumber(token);
    }
    if (user != null) {
      UserResult result = new UserResult();
      ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(user);
      //统计用户在该商家在三张表中的消费成功次数
      Object[] o1 = unionPosOrderService.countByLeJiaUserAndMerchant(user.getId(), merchantId);
      Object[] o2 = offLineOrderService.countByLeJiaUserAndMerchant(user.getId(), merchantId);
      WeiXinUser u = user.getWeiXinUser();
      result.setId(user.getId());
      result.setScoreA(scoreA.getScore());
      Long c1 = Long.valueOf("" + o1[0]);
      Long c2 = Long.valueOf("" + o2[0]);
      result.setTimes(c1 + c2);
      result.setTotalPrice(
          ((c1 > 0) ? ((BigDecimal) o1[1]).longValue() : 0) + ((c2 > 0) ? ((BigDecimal) o2[1])
              .longValue() : 0));
      if (u != null) {
        result.setHeadImageUrl(u.getHeadImageUrl());
        result.setNickname(u.getNickname());
        result.setState(u.getState());
      } else {
        result.setNickname(user.getPhoneNumber());
        result.setState(0);
      }
      return LejiaResult.build(200, "", result);
    } else {
      return LejiaResult.build(2002, "未找到用户信息");
    }
  }

  /**
   * POS机查询商家绑定的会员数量 16/10/12
   */
  @ApiOperation(value = "POS机查询商家绑定的会员数量")
  @RequestMapping(value = "/count", method = RequestMethod.GET)
  public
  @ResponseBody
  LejiaResult countUserByMerchant(@RequestParam Long merchantId) {
    return LejiaResult.ok(userService.countUserByMerchant(merchantId));
  }

  /**
   * POS机分页查询商家绑定的会员信息 16/10/12
   */
  @ApiOperation(value = "POS机分页查询商家绑定的会员信息")
  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult findUserByMerchantAndPage(@RequestParam Long merchantId,
                                        @RequestParam Integer currPage) {
    List<Map> list = userService.findUserByMerchantAndPage(merchantId, currPage);
    return LejiaResult.ok(list);
  }


}
