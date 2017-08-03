package com.jifenke.lepluslive.wxpay.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWeiXinUser;
import com.jifenke.lepluslive.merchant.domain.entities.TemporaryMerchantUserShop;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantWeiXinUserService;
import com.jifenke.lepluslive.merchant.service.TemporaryMerchantUserShopService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.wxpay.domain.entities.WxTemMsg;
import com.jifenke.lepluslive.wxpay.repository.WxTemMsgRepository;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;


/**
 * 发送模板消息 Created by zhangwen on 2016/5/10.
 */
@Service
@Transactional(readOnly = true)
public class WxTemMsgService {

  @Inject
  private WxTemMsgRepository wxTemMsgRepository;

  @Inject
  private MerchantWeiXinUserService merchantWeiXinUserService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private DictionaryService dictionaryService;

  @Inject
  private TemporaryMerchantUserShopService temporaryMerchantUserShopService;

  private static final Logger log = LoggerFactory.getLogger(WxTemMsgService.class);


  public void sendToClient(OffLineOrder offLineOrder) {
    new Thread(() -> {
      //为用户推送
      StringBuffer sb = new StringBuffer();
      String[] keys = new String[4];
      keys[0] = offLineOrder.getMerchant().getName();
      if (offLineOrder.getTrueScore() != 0) {
        if (offLineOrder.getTruePay() != 0) {
          sb.append("¥");
          sb.append(offLineOrder.getTotalPrice() / 100.0);
          sb.append("(");
          sb.append("微信¥");
          sb.append(offLineOrder.getTruePay() / 100.0);
          sb.append(",鼓励金¥");
          sb.append(offLineOrder.getTrueScore() / 100.0);
          sb.append(")");
          keys[1] = sb.toString();
        } else {
          sb.append("¥");
          sb.append(offLineOrder.getTotalPrice() / 100.0);
          sb.append("(");
          sb.append("鼓励金¥");
          sb.append(offLineOrder.getTrueScore() / 100.0);
          sb.append(")");
          keys[1] = sb.toString();
        }
      } else {
        sb.append("¥");
        sb.append(offLineOrder.getTotalPrice() / 100.0);
        sb.append("(");
        sb.append("微信¥");
        sb.append(offLineOrder.getTotalPrice() / 100.0);
        sb.append(")");
        keys[1] = sb.toString();
      }
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      keys[2] = dateFormat.format(new Date());

      HashMap<String, Object> mapRemark = new HashMap<>();
      sb.setLength(0);
      sb.append("您本次消费获得");
      if (offLineOrder.getRebate() != 0L) {
        sb.append("¥");
        sb.append(offLineOrder.getRebate() / 100.0);
        sb.append("鼓励金+");
      }
      sb.append("¥");
      sb.append(offLineOrder.getScoreC()/100.0);
      sb.append("金币");
      sb.append("(乐付码:");
      sb.append(offLineOrder.getLepayCode());
      sb.append("),");
      sb.append("点击查看详情");
      mapRemark.put("value", sb.toString());
      mapRemark.put("color", "#173177");
      HashMap<String, Object> map2 = new HashMap<>();
      map2.put("remark", mapRemark);

      sendTemMessage(offLineOrder.getLeJiaUser().getWeiXinUser().getOpenId(), 2L, keys,
                     offLineOrder.getOrderSid(), 7L, map2);

      //为商户发送模版消息
    }).start();

  }

  /**
   * 根据temId不同，发送不同的消息 keys  封装参数
   */
  public void sendTemMessage(String openId, Long temId, String[] keys, Serializable sid, Long wxId,
                             HashMap<String, Object> map2) {

    WxTemMsg wxTemMsg = wxTemMsgRepository.findOne(temId);

    HashMap<String, Object> mapfirst = new HashMap<>();
    mapfirst.put("value", wxTemMsg.getFirst());
    mapfirst.put("color", wxTemMsg.getColor());

    int i = 1;

    for (String key : keys) {
      HashMap<String, Object> mapKey = new HashMap<>();
      mapKey.put("value", key);
      mapKey.put("color", wxTemMsg.getColor());
      map2.put("keyword" + i, mapKey);
      i++;
    }

    // 先封装一个 JSON 对象
    JSONObject param = new JSONObject();

    param.put("touser", openId);
    param.put("template_id", wxTemMsg.getTemplateId());
    param.put("url", wxTemMsg.getUrl() + sid);
    param.put("data", map2);

    sendTemplateMessage(param, wxId, 3);
  }


  /**
   * 发送模板消息
   */
  private void sendTemplateMessage(JSONObject param, Long wxId, Integer times) {
    try {
      // 绑定到请求 Entry

      StringEntity
          se =
          new StringEntity(new String(param.toString().getBytes("utf8"), "iso8859-1"));

      //获取token
      String token = dictionaryService.findDictionaryById(wxId).getValue();

      String
          getUrl =
          "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
      CloseableHttpClient httpclient = HttpClients.createDefault();
      HttpPost httpPost = new HttpPost(getUrl);
      httpPost.addHeader("Content-Type", "application/json");
      httpPost.setEntity(se);
      CloseableHttpResponse response = null;

      response = httpclient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String>
          map =
          mapper.readValue(
              new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8")),
              Map.class);

      EntityUtils.consume(entity);
      //如果catch到异常,则跳出递归,并且纪录bug
      if (!map.get("errmsg").equals("ok") && !String.valueOf(map.get("errcode")).equals("43004")) {
        log.error("出现异常" + map.get("errmsg").toString());
        if (times > 0) {
          try {
            Thread.sleep(10000);
            --times;
            sendTemplateMessage(param, wxId,times);
          } catch (InterruptedException e) {
            //e.printStackTrace();
            log.error(param.get("data").toString() + e.getMessage());
          }
        }
      }
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendToMerchant(OffLineOrder offLineOrder) {

    new Thread(() -> {
      //为商家推送
      StringBuffer sb = new StringBuffer();
      String[] keys = new String[4];
      keys[0] = offLineOrder.getTotalPrice() / 100.0 + "";
      keys[1] = offLineOrder.getOrderSid();
      HashMap<String, Object> mapRemark = new HashMap<>();
      sb.append("本次支付的乐付码是");
      sb.append(offLineOrder.getLepayCode());
      sb.append(",本月第");
      sb.append(offLineOrder.getMonthlyOrderCount());
      sb.append("笔订单");
      if(offLineOrder.getRebateWay()==1||offLineOrder.getRebateWay()==3){
        sb.append("(乐加订单)");
      }else {
        sb.append("(普通订单)");
      }
      sb.append(",点击查看详情");
      mapRemark.put("value", sb.toString());
      mapRemark.put("color", "#173177");
      HashMap<String, Object> map2 = new HashMap<>();
      map2.put("remark", mapRemark);
//      List<MerchantUser>
//          merchantUsers =
//          merchantService.findMerchantUserByMerchant(offLineOrder
//                                                         .getMerchant());
//      for (MerchantUser merchantUser : merchantUsers) {
      List<TemporaryMerchantUserShop>
          list =
          temporaryMerchantUserShopService.findAllByMerchant(offLineOrder.getMerchant());
      for (TemporaryMerchantUserShop s : list) {
        List<MerchantWeiXinUser>
            merchantWeiXinUsers =
            merchantWeiXinUserService.findMerchantWeiXinUserByMerchantUser(s.getMerchantUser());
        for (MerchantWeiXinUser merchantWeiXinUser : merchantWeiXinUsers) {
          sendTemMessage(merchantWeiXinUser.getOpenId(), 3L, keys,
                         offLineOrder.getOrderSid(), 9L, map2);
        }
      }
//      merchantService.findMerchantUserByMerchant(offLineOrder
//                                                     .getMerchant()).stream().map(merchantUser -> {
//        merchantWeiXinUserService.findMerchantWeiXinUserByMerchantUser(merchantUser).stream()
//            .map(merchantWeiXinUser -> {
//              sendTemMessage(merchantWeiXinUser.getOpenId(), 3L, keys,
//                             offLineOrder.getOrderSid(), 41L, map2);
//              return null;
//            })
//            .collect(Collectors.toList());
//        return null;
//      }).collect(
//          Collectors.toList());

      //为商户发送模版消息
    }).start();
  }

  /**
   * 发送支付成功模板消息给消费者   16/12/19
   *
   * @param merchantName 商户名称
   * @param trueScore    使用红包
   * @param truePay      实际支付
   * @param totalPrice   总支付
   * @param rebate       发红包
   * @param scoreB       发积分
   * @param openId       用户openId
   * @param orderSid     订单号
   * @param paytype     微信or支付宝
   */
  public void sendToClient(String merchantName, Long trueScore, Long truePay, Long totalPrice,
                           Long rebate, Long scorec, String openId, String orderSid,Integer paytype) {
    new Thread(() -> {
      //为用户推送
      StringBuffer sb = new StringBuffer();
      String[] keys = new String[4];
      keys[0] = merchantName;
      if (trueScore != 0) {
        if (truePay != 0) {
          sb.append("¥");
          sb.append(totalPrice / 100.0);
          sb.append("(");
          if(paytype==0){
            sb.append("微信¥");
          }else {
            sb.append("支付宝¥");
          }
          sb.append(truePay / 100.0);
          sb.append(",红包¥");
          sb.append(trueScore / 100.0);
          sb.append(")");
          keys[1] = sb.toString();
        } else {
          sb.append("¥");
          sb.append(totalPrice / 100.0);
          sb.append("(");
          sb.append("鼓励金¥");
          sb.append(trueScore / 100.0);
          sb.append(")");
          keys[1] = sb.toString();
        }
      } else {
        sb.append("¥");
        sb.append(totalPrice / 100.0);
        sb.append("(");
        if(paytype==0){
          sb.append("微信¥");
        }else {
          sb.append("支付宝¥");
        }
        sb.append(totalPrice / 100.0);
        sb.append(")");
        keys[1] = sb.toString();
      }
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      keys[2] = dateFormat.format(new Date());

      HashMap<String, Object> mapRemark = new HashMap<>();
      sb.setLength(0);
      sb.append("您本次消费获得");
      if (rebate != 0L) {
        sb.append("¥");
        sb.append(rebate / 100.0);
        sb.append("鼓励金+");
      }
      if(scorec!=0L){
        sb.append("¥");
        sb.append(scorec/100.0);
        sb.append("金币,");
      }
      sb.append("点击查看详情");
      mapRemark.put("value", sb.toString());
      mapRemark.put("color", "#173177");
      HashMap<String, Object> map2 = new HashMap<>();
      map2.put("remark", mapRemark);

      sendTemMessage(openId, 7L, keys, orderSid, 7L, map2);

      //为商户发送模版消息
    }).start();
  }


  /**
   * 给商家发送支付成功模板消息  2016/12/9
   *
   * @param totalPrice 订单总价
   * @param orderSid   订单号
   * @param lePayCode  乐付码
   * @param merchant   商户
   */
  public void sendToMerchant(Long totalPrice, String orderSid, String lePayCode,
                             Merchant merchant,Long orderType) {

    new Thread(() -> {
      //为商家推送
      StringBuffer sb = new StringBuffer();
      String[] keys = new String[4];
      keys[0] = totalPrice / 100.0 + "";
      keys[1] = orderSid;
      HashMap<String, Object> mapRemark = new HashMap<>();
      sb.append("本次支付的乐付码是");
      sb.append(lePayCode);
//      sb.append(",本月第");
//      sb.append(offLineOrder.getMonthlyOrderCount());
//      sb.append("笔订单");
      if(orderType==1L){
        sb.append("(乐加订单)");
      }else {
        sb.append("(普通订单)");
      }
      sb.append(",点击查看详情");
      mapRemark.put("value", sb.toString());
      mapRemark.put("color", "#173177");
      HashMap<String, Object> map2 = new HashMap<>();
      map2.put("remark", mapRemark);
//      List<MerchantUser>
//          merchantUsers =
//          merchantService.findMerchantUserByMerchant(merchant);
      List<TemporaryMerchantUserShop>
          list =
          temporaryMerchantUserShopService.findAllByMerchant(merchant);
      for (TemporaryMerchantUserShop s : list) {
        List<MerchantWeiXinUser>
            merchantWeiXinUsers =
            merchantWeiXinUserService.findMerchantWeiXinUserByMerchantUser(s.getMerchantUser());
        for (MerchantWeiXinUser merchantWeiXinUser : merchantWeiXinUsers) {
          sendTemMessage(merchantWeiXinUser.getOpenId(), 8L, keys,
                         orderSid, 9L, map2);
        }
      }
    }).start();
  }
}

