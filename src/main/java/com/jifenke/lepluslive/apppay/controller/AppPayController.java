package com.jifenke.lepluslive.apppay.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;

/**
 * APP 扫码支付 Created by zhangwen on 16/10/18.
 */
@RestController
@RequestMapping("/lepay")
public class AppPayController {

  private static Logger log = LoggerFactory.getLogger(AppPayController.class);

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private OffLineOrderService offLineOrderService;

  /**
   * APP扫一扫获取商家及个人信息 16/09/12
   *
   * @param token       用户唯一标识
   * @param merchantSid 商家唯一标识
   * @param pure        扫码类型  为true=纯支付码
   */
  @ApiOperation(value = "APP扫一扫获取商家及个人信息")
  @RequestMapping(value = "/appPay/showPay", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult goPayPage(@RequestParam(required = true) String token,
                        @RequestParam(required = true) String merchantSid,
                        @RequestParam(required = false) String pure) {
    //判断是否有该用户
    if (token != null) {
      LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(token);
      if (leJiaUser == null) {
        return LejiaResult.build(401, "未找到用户信息");
      }
      //获取商家信息
      Optional<Merchant> optional = merchantService.findMerchantBySId(merchantSid);
      if (optional.isPresent()) {
        Merchant merchant = optional.get();
        //封装返回参数
        Map map = getMerchantInfo(merchant, pure);
        return LejiaResult.ok(map);
      } else {
        return LejiaResult.build(402, "商家信息有误");
      }
    } else {
      return LejiaResult.build(409, "请先登录");
    }
  }

  /**
   * 第一次确认支付 16/09/12
   *
   * @param token      用户唯一标识
   * @param merchantId 商家唯一标识
   * @param pure       扫码类型  为true=纯支付码
   */
  @ApiOperation(value = "第一次确认支付")
  @RequestMapping(value = "/appPay/pay", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult pay(@RequestParam(required = true) String token,
                  @RequestParam(required = true) Long merchantId,
                  @RequestParam(required = true) Integer pure,
                  @RequestParam(required = true) String price, HttpServletRequest request) {
    //判断是否有该用户
    if (token != null) {
      LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(token);
      if (leJiaUser == null) {
        return LejiaResult.build(401, "未找到用户信息");
      }
      OffLineOrder offLineOrder = null;
      //判断扫的是否是纯支付码
      if (pure == 1) { //是纯支付码
        WeiXinUser weiXinUser = leJiaUser.getWeiXinUser();
        if (weiXinUser == null) {
          return LejiaResult.build(402, "未找到微信信息");
        }
        offLineOrder = offLineOrderService.createOffLineOrderForNoNMember(price,
                                                                          merchantId,
                                                                          weiXinUser,
                                                                          true, 3L);

        SortedMap sortedMap = buildAppOrder(request, offLineOrder);
        if (sortedMap != null) {
          return LejiaResult.build(200, "ok", sortedMap);
        } else {
          return LejiaResult.build(403, "支付异常");
        }
      } else {//不是纯支付码
        ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
        if (scoreA == null) {
          return LejiaResult.build(404, "未找到红包信息");
        }
        if (scoreA.getScore() == 0) {//如果用户红包为0,直接生成订单吊起支付
          offLineOrder =
              offLineOrderService
                  .createOffLineOrderForMember(price, merchantId, "0", price, leJiaUser, 3L);
          SortedMap sortedMap = buildAppOrder(request, offLineOrder);
          if (sortedMap != null) {
            return LejiaResult.build(200, "ok", sortedMap);
          } else {
            return LejiaResult.build(403, "支付异常");
          }
        }
        int receiptAuth = 1;   //商户是否开通收取红包权限 1=已开通
        //判断是否可以使用红包(条件=用户有可用红包且商户开通了收取红包权限)
        Merchant merchant = merchantService.findMerchantById(merchantId);
        if (merchant.getReceiptAuth() == 0) { //判断商户是否开通收取红包权限
          receiptAuth = 0;
        }
        Long totalPrice = new BigDecimal(price).multiply(new BigDecimal(100)).longValue();
        Map<String, Object> map = new HashMap<>();
        map.put("pay", 0); //pay=0代表跳转到填写红包页面
        map.put("receiptAuth", receiptAuth); // 为0代表填写红包页面不可输入红包
        map.put("merchantId", merchant.getId());
        map.put("scoreA", scoreA.getScore());
        map.put("totalPrice", totalPrice);
        return LejiaResult.ok(map);
      }
    } else {
      return LejiaResult.build(409, "请先登录");
    }
  }

  /**
   * 输入使用红包页确认支付(第二次确认支付) 16/09/12
   *
   * @param token      用户唯一标识
   * @param merchantId 商家唯一标识
   * @param trueScore  实际使用红包
   * @param truePrice  实际支付金额
   * @param totalPrice 总支付金额(=trueScore+truePrice)
   */
  @ApiOperation(value = "输入使用红包页确认支付(第二次确认支付)")
  @RequestMapping(value = "/appPay/scorePay", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult scorePay(@RequestParam(required = true) String token,
                       @RequestParam(required = true) String merchantId,
                       @RequestParam(required = true) String trueScore,
                       @RequestParam(required = true) String truePrice,
                       @RequestParam(required = true) String totalPrice,
                       HttpServletRequest request) {
    //判断是否有该用户
    if (token != null) {
      LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(token);
      if (leJiaUser == null) {
        return LejiaResult.build(401, "未找到用户信息");
      }
      OffLineOrder offLineOrder = null;
      long truePay = Long.parseLong(truePrice);
      //判断用户是否有足够的可用红包
      long score = Long.parseLong(trueScore);
      ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
      if (scoreA == null || scoreA.getScore() < score) {
        return LejiaResult.build(400, "红包不足");
      }
      if (truePay == 0) { //全部都是红包支付，无需吊起支付，直接支付成功
        if (!totalPrice.equals(trueScore)) {
          return LejiaResult.build(401, "红包不等于总金额");
        }
        try {
          offLineOrder = offLineOrderService.payByScoreA(token, merchantId, totalPrice, 4L);
          Map map = paySuccessData(offLineOrder); //封装返回数据
          return LejiaResult.ok(map);
        } catch (Exception e) {
          return LejiaResult.build(500, "出现未知错误,请联系管理员");
        }
      } else {
        offLineOrder =
            offLineOrderService
                .createOffLineOrderForMember(truePrice, Long.parseLong(merchantId), trueScore,
                                             totalPrice, leJiaUser, 3L);
        SortedMap sortedMap = buildAppOrder(request, offLineOrder);
        if (sortedMap != null) {
          return LejiaResult.build(200, "ok", sortedMap);
        } else {
          return LejiaResult.build(403, "支付异常");
        }
      }
    } else {
      return LejiaResult.build(409, "请先登录");
    }
  }

  /**
   * 支付成功获取订单数据 16/09/12
   *
   * @param orderSid 订单号
   */
  @ApiOperation(value = "支付成功获取订单数据")
  @RequestMapping(value = "/appPay/paySuccess", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult getPaySuccessData(@RequestParam(required = true) String orderSid) {
    OffLineOrder order = offLineOrderService.findOffLineOrderByOrderSid(orderSid);
    if (order != null) {
      Map map = paySuccessData(order);
      return LejiaResult.ok(map);
    } else {
      return LejiaResult.build(401, "未找到该订单");
    }
  }


  /**
   * 扫码后封装返回参数 16/09/12
   *
   * @param merchant 商家信息
   * @param pure     扫码类型
   * @return 返回Map
   */
  private Map getMerchantInfo(Merchant merchant, String pure) {
    Map<String, Object> map = new HashMap<>();
    map.put("merchantName", merchant.getName());
    map.put("merchantId", merchant.getId());
    map.put("partnership", merchant.getPartnership());
    if (pure != null && "access".equals(pure)) {
      map.put("pure", 1);
    } else {
      map.put("pure", 0);
    }
    return map;
  }

  /**
   * 支付成功后封装返回参数 16/09/12
   *
   * @param offLineOrder 订单信息
   * @return 返回Map
   */
  private Map paySuccessData(OffLineOrder offLineOrder) {
    Map<String, Object> map = new HashMap<>();
    map.put("totalPrice", offLineOrder.getTotalPrice());
    map.put("truePay", offLineOrder.getTruePay());
    map.put("trueScore", offLineOrder.getTrueScore());
    map.put("merchantName", offLineOrder.getMerchant().getName());
    map.put("lepayCode", offLineOrder.getLepayCode());
    map.put("completeDate", offLineOrder.getCompleteDate());
    map.put("gift", offLineOrder.getRebate() / 100 + offLineOrder.getScoreB());
    map.put("rebate", offLineOrder.getRebate());
    map.put("scoreB", offLineOrder.getScoreB());
    map.put("orderSid", offLineOrder.getOrderSid());

    map.put("pay", 0); //pay=0代表全部红包支付
    return map;
  }

  /**
   * 生成预支付订单，返回参数 16/09/12
   *
   * @param request      请求
   * @param offLineOrder 订单数据
   * @return 支付所需参数
   */
  private SortedMap buildAppOrder(HttpServletRequest request, OffLineOrder offLineOrder) {
    //封装订单参数
    SortedMap<Object, Object> map = weiXinPayService.buildAppOrderParams(request, offLineOrder);
    //获取预支付id
    Map unifiedOrder = weiXinPayService.createUnifiedOrder(map);
    if (unifiedOrder.get("prepay_id") != null) {
      SortedMap sortedMap = weiXinPayService.buildAppParams(
          unifiedOrder.get("prepay_id").toString());
      sortedMap.put("pay", 1); //pay=1代表直接吊起支付
      sortedMap.put("orderSid", offLineOrder.getOrderSid()); //pay=1代表直接吊起支付
      return sortedMap;
    } else {
      return null;
    }
  }

}
