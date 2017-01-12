//package com.jifenke.lepluslive.web.rest;
//
//import com.jifenke.lepluslive.Application;
//import com.jifenke.lepluslive.activity.service.InitialOrderRebateActivityService;
//import com.jifenke.lepluslive.global.config.Constants;
//import com.jifenke.lepluslive.global.util.PosCardCheckUtil;
//import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
//import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;
//import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
//import com.jifenke.lepluslive.order.domain.entities.PosOrder;
//import com.jifenke.lepluslive.order.repository.PosOrderRepository;
//import com.jifenke.lepluslive.order.service.OffLineOrderService;
//import com.jifenke.lepluslive.order.service.PosOrderService;
//import com.jifenke.lepluslive.wxpay.repository.WeiXinUserRepository;
//import com.jifenke.lepluslive.wxpay.service.DictionaryService;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.IntegrationTest;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.util.List;
//import java.util.Map;
//import java.util.SortedMap;
//
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//
///**
// * Created by wcg on 16/4/15.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration
//@IntegrationTest
//@ActiveProfiles({Constants.SPRING_PROFILE_DEVELOPMENT})
//public class ttt {
//
//
//  @Inject
//  private WeiXinUserRepository weiXinUserRepository;
//
//  @Inject
//  private LeJiaUserRepository leJiaUserRepository;
//
//  @Inject
//  private OffLineOrderService offLineOrderService;
//
//  @Inject
//  private InitialOrderRebateActivityService initialOrderRebateActivityService;
//
//  @Inject
//  private PosOrderRepository posOrderRepository;
//
//  @Inject
//  private PosOrderService posOrderService;
//
//  @Inject
//  private DictionaryService dictionaryService;
//
//  @Test
//  public void tttt() {
//    List<PosOrder>
//        posOrders =
//        posOrderRepository.findByStateAndTrueScoreAndLeJiaUserIsNull(1, 0L);
//    for (PosOrder posOrder : posOrders) {
//      MerchantPos merchantPos = posOrder.getMerchantPos();
//      BigDecimal paid = new BigDecimal(posOrder.getTotalPrice());
//      try {
//        BigDecimal ljCommission = null;
//        BigDecimal thirdCommission = null;
//        Integer tradeFlag = posOrder.getTradeFlag();
//        if (tradeFlag == 0) {//支付宝
//          ljCommission =
//              merchantPos.getAliCommission().multiply(paid);
//          thirdCommission =
//              new BigDecimal(dictionaryService.findDictionaryById(42L).getValue())
//                  .multiply(paid);
//        } else if (tradeFlag == 3) { //刷卡
//          if (posOrder.getCardType() == 1) {
//            ljCommission =
//                merchantPos.getCreditCardCommission().multiply(paid);
//            thirdCommission =
//                new BigDecimal(dictionaryService.findDictionaryById(46L).getValue())
//                    .multiply(paid);
//          } else if (posOrder.getCardType() == 0) {
//            ljCommission =
//                merchantPos.getDebitCardCommission().multiply(paid);
//            if (ljCommission.longValue() > merchantPos.getCeil()) {//封顶手续费
//              ljCommission = new BigDecimal(merchantPos.getCeil());
//            }
//            thirdCommission =
//                new BigDecimal(dictionaryService.findDictionaryById(45L).getValue())
//                    .multiply(paid);
//            BigDecimal thirdCommissionLimit = new BigDecimal(
//                dictionaryService.findDictionaryById(47L).getValue());
//            if (thirdCommission.longValue() > thirdCommissionLimit.longValue()) { //封顶第三方手续费
//              thirdCommission = thirdCommissionLimit;
//            }
//          } else {
//            posOrder.setCardType(2);
//          }
//        } else if (tradeFlag == 4) { //微信
//          ljCommission =
//              merchantPos.getWxCommission().multiply(paid);
//          thirdCommission =
//              new BigDecimal(dictionaryService.findDictionaryById(43L).getValue())
//                  .multiply(paid);
//        } else { //现金
//          ljCommission = new BigDecimal(0);
//        }
//        ljCommission = ljCommission.divide(new BigDecimal(100));
//        posOrder
//            .setLjCommission(Math.round(ljCommission.doubleValue()));
//        posOrder
//            .setWxCommission(thirdCommission == null ? 0 : Math
//                .round(thirdCommission.divide(new BigDecimal(100)).doubleValue()));
//        posOrder.setTransferMoney(paid.subtract(ljCommission).longValue());
//        posOrder.setTransferByBank(paid.subtract(ljCommission).longValue());
//        posOrderRepository.save(posOrder);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
////  public static void main(String[] args) {
////    System.out.println( new BigDecimal(11).divide(new BigDecimal(110), 2, BigDecimal.ROUND_HALF_UP)
////                            .doubleValue());
////
////
////
////    }
//
//
//}
//
