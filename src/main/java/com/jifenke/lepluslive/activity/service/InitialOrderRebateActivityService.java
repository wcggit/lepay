package com.jifenke.lepluslive.activity.service;

import com.jifenke.lepluslive.activity.domain.criteria.RebateActivityCriteria;
import com.jifenke.lepluslive.activity.domain.criteria.RebateActivityLogCriteria;
import com.jifenke.lepluslive.activity.domain.entities.InitialOrderRebateActivity;
import com.jifenke.lepluslive.activity.domain.entities.InitialOrderRebateActivityLog;
import com.jifenke.lepluslive.activity.repository.InitialOrderRebateActivityLogRepository;
import com.jifenke.lepluslive.activity.repository.InitialOrderRebateActivityRepository;
import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWeiXinUser;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantWeiXinUserService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.wxpay.domain.entities.Dictionary;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by wcg on 16/9/12.
 */
@Service
@Transactional(readOnly = true)
public class InitialOrderRebateActivityService {

  @Inject
  private EntityManager em;

  @Value("${weixin.wxapiKey}")
  private String wxapiKey;

  @Inject
  private SSLContext sslContext;

  @Inject
  private MerchantService merchantService;

  @Inject
  private DictionaryService dictionaryService;

  @Inject
  private MerchantWeiXinUserService merchantWeiXinUserService;

  @Inject
  private InitialOrderRebateActivityRepository initialOrderRebateActivityRepository;

  @Inject
  private InitialOrderRebateActivityLogRepository initialOrderRebateActivityLogRepository;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List findMerchantOrderRebateActivityByCriteria(RebateActivityCriteria criteria) {
    int start = 10 * (criteria.getOffset() - 1);
    StringBuffer sql = new StringBuffer();
    sql.append(
        "select merchant.merchant_sid,merchant.name,initial_order_rebate_activity.state,merchant.partnership,merchant.bind_wx_user,initial_order_rebate_activity.total_rebate_times,initial_order_rebate_activity.total_rebate_money from (select id,merchant_sid,name,partnership,((select concat(head_image_url,'#=$(',nickname,')') from merchant_wx_user where merchant_wx_user.merchant_user_id = (select id from merchant_user where merchant_user.merchant_id = merchant.id and type =1) ))bind_wx_user from merchant where 1=1 ");
    if (criteria.getMerchant() != null) {
      sql.append(" and merchant.name like '%");
      sql.append(criteria.getMerchant());
      sql.append("%'");
    }
    if (criteria.getBindWxState() != null) {
      if (criteria.getBindWxState() == 1) {
        sql.append(
            "  and merchant.id  in (select merchant_user.merchant_id from merchant_wx_user,merchant_user where merchant_wx_user.merchant_user_id = merchant_user.id and merchant_user.type = 1 ) ");
      } else {
        sql.append(
            " and merchant.id not in (select merchant_user.merchant_id from merchant_wx_user,merchant_user where merchant_wx_user.merchant_user_id = merchant_user.id and merchant_user.type = 1 ) ");
      }
    }
    sql.append("order by create_date desc limit ");
    sql.append(start);
    sql.append(",10 )merchant");
    sql.append(
        " left join  initial_order_rebate_activity on merchant.id =  initial_order_rebate_activity.merchant_id where 1=1 ");

    if (criteria.getState() != null) {
      if (criteria.getState() == 0) {
        sql.append(
            " and initial_order_rebate_activity.state is null or initial_order_rebate_activity.state = 0 ");
      } else {
        sql.append(" and initial_order_rebate_activity.state = 1  ");
      }
    }
    Query nativeQuery = em.createNativeQuery(sql.toString());
    List<Object[]> resultList = nativeQuery.getResultList();
    return resultList;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Long countMerchantOrderRebateActivityByCriteria(RebateActivityCriteria criteria) {
    StringBuffer sql = new StringBuffer();
    sql.append("select count(*) from (select  merchant.id mid  from (select id from merchant");
    if (criteria.getMerchant() != null) {
      sql.append(" where merchant.name like '%");
      sql.append(criteria.getMerchant());
      sql.append("%'");
    }
    sql.append(
        ")merchant  left join initial_order_rebate_activity on merchant.id =  initial_order_rebate_activity.merchant_id ");
    if (criteria.getState() != null) {
      if (criteria.getState() == 0) {
        sql.append(
            " where initial_order_rebate_activity.state is null or initial_order_rebate_activity.state = 0 ");
      } else {
        sql.append(" where initial_order_rebate_activity.state = 1  ");
      }
    }
    sql.append(" )merchant");
    if (criteria.getBindWxState() != null) {
      if (criteria.getBindWxState() == 0) {
        sql.append(
            " where merchant.mid not in (select merchant_user.merchant_id from merchant_wx_user,merchant_user where merchant_wx_user.merchant_user_id = merchant_user.id and merchant_user.type = 1 )");
      } else {
        sql.append(
            " where merchant.mid  in (select merchant_user.merchant_id from merchant_wx_user,merchant_user where merchant_wx_user.merchant_user_id = merchant_user.id and merchant_user.type = 1 )");
      }
    }
    Query nativeQuery = em.createNativeQuery(sql.toString());
    List<BigInteger> details = nativeQuery.getResultList();
    return details.get(0).longValue();
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkActivity(OffLineOrder offLineOrder) {
    Dictionary dictionary = dictionaryService.findDictionaryById(33L);
    String limit = dictionary.getValue();
    if (new Integer(limit).intValue() <= offLineOrder.getTotalPrice()) {
      Merchant merchant = offLineOrder.getMerchant();
      InitialOrderRebateActivity
          activity =
          initialOrderRebateActivityRepository.findByMerchant(merchant);
      if (activity != null && activity.getState() == 1) {
        Long
            userInitialOrNot =
            initialOrderRebateActivityRepository
                .checkUserInitialOrNot(offLineOrder.getLeJiaUser().getId(), limit,
                                       offLineOrder.getId()); //判断用户是否首单
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);

        Date end = calendar.getTime();
        long
            merchantDailyIncome =
            initialOrderRebateActivityLogRepository
                .countMerchantDailyIncome(merchant.getId(), start, end); //商户日收入
        if (userInitialOrNot == 0) {
          long rebateMoney;
          if (activity.getRebateType() == 1) {
            Random rdm = new Random(new Long(MvUtil.getRandomNumber(10)));
            rebateMoney = rdm.nextInt(
                activity.getMaxRebate().intValue() - activity.getMinRebate().intValue() + 1)
                          + activity
                .getMinRebate();
          } else {
            rebateMoney = activity.getMaxRebate();
          }
          InitialOrderRebateActivityLog
              activityLog =
              new InitialOrderRebateActivityLog();
          activityLog.setMerchant(merchant);
          activityLog.setOffLineOrder(offLineOrder);
          activityLog.setCreatedDate(offLineOrder.getCompleteDate());
          //获取店主账户
          MerchantUser merchantUser = merchantService.findBossAccountByMerchant(merchant);
          List<MerchantWeiXinUser>
              wxUsers =
              merchantWeiXinUserService.findMerchantWeiXinUserByMerchantUser(merchantUser);
          if (merchantDailyIncome < activity.getDailyRebateLimit()) {
            if (merchantDailyIncome + rebateMoney >= activity
                .getDailyRebateLimit()) { //发了这笔后超出每日发放限制
              rebateMoney = activity.getDailyRebateLimit() - merchantDailyIncome;
            }
            activityLog.setRebate(rebateMoney);
            if (wxUsers == null) {
              activityLog.setState(0);
              activityLog.setExceptionLog("没有绑定店主账号");
            } else {
              //统计是否到达发放红包下限
              long
                  handlingRebateCount =
                  initialOrderRebateActivityLogRepository
                      .contInitialOrderLogByMerchantAndState(merchant.getId(), 0); //待发放金额

              String rebateLimit = dictionaryService.findDictionaryById(36L).getValue();
              long currentRebate = handlingRebateCount + rebateMoney;//此次发放的金额
              if (currentRebate >= new Long(rebateLimit)) {
                //发放红包
                MerchantWeiXinUser merchantWeiXinUser = wxUsers.get(0);
                activityLog.setHeadImageUrl(merchantWeiXinUser.getHeadImageUrl());
                activityLog.setNickname(merchantWeiXinUser.getNickname());
                String openId = merchantWeiXinUser.getOpenId();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
                parameters.put("wxappid", Constants.LE_PAY_APP_ID);
                parameters.put("mch_id", Constants.LE_PAY_MCH_ID);
                parameters.put("nonce_str", MvUtil.getRandomStr());
                parameters
                    .put("mch_billno",
                         Constants.LE_PAY_MCH_ID + format.format(new Date()) + MvUtil
                             .getRandomNumber(10));
                parameters.put("send_name", dictionaryService.findDictionaryById(37L).getValue());
                parameters.put("re_openid", openId);
                parameters.put("total_amount", currentRebate + "");
                parameters.put("total_num", "1");
                parameters.put("wishing", dictionaryService.findDictionaryById(38L).getValue());
                parameters.put("client_ip", "127.0.0.1");
                parameters.put("act_name", "首单返红包");
                parameters.put("remark", "首单返红包");
                parameters.put("sign", weiXinPayService.createSign("UTF-8", parameters, wxapiKey));
                Map map = WeixinPayUtil.initialRebateOrder(
                    "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack",
                    weiXinPayService.getRequestXml(parameters), sslContext);
                if ("SUCCESS".equals(map.get("result_code").toString()) && "SUCCESS".equals(map.get(
                    "err_code").toString())) {
                  activityLog.setState(1);
                  activity.setTotalRebateMoney(activity.getTotalRebateMoney() + rebateMoney);
                  activity.setTotalRebateTimes(activity.getTotalRebateTimes());
                  //批量修改数据库,让所有待发放状态变为已发放
                  initialOrderRebateActivityLogRepository.updateAllHandleLogToComplete(
                      merchant.getId(), merchantWeiXinUser.getNickname(),
                      merchantWeiXinUser.getHeadImageUrl());
                } else {
                  activityLog.setState(0);
                  activityLog.setExceptionLog(map.get("return_msg").toString());
                  initialOrderRebateActivityLogRepository
                      .updateAllHandleLogToFail(merchant.getId(), map.get("return_msg").toString());
                }
                activityLog.setDetailResponse(map.toString());
              } else {
                activityLog.setState(0);
              }
            }
          } else { //已经到达每日发放上限
            activityLog.setExceptionLog("到达每日上限");
            activityLog.setState(2);
          }
          initialOrderRebateActivityLogRepository.save(activityLog);
        }
      }
    }

  }

}
