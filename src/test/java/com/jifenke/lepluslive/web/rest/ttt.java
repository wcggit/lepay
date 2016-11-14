//package com.jifenke.lepluslive.web.rest;
//
//import com.jifenke.lepluslive.Application;
//import com.jifenke.lepluslive.activity.service.InitialOrderRebateActivityService;
//import com.jifenke.lepluslive.global.config.Constants;
//import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
//import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;
//import com.jifenke.lepluslive.order.service.OffLineOrderService;
//import com.jifenke.lepluslive.wxpay.repository.WeiXinUserRepository;
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
//import java.util.List;
//import java.util.Map;
//import java.util.SortedMap;
//
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//
///**
//* Created by wcg on 16/4/15.
//*/
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
//
//  @Test
//  public void tttt() {
//    LeJiaUser one = leJiaUserRepository.findOne(51L);
//    offLineOrderService.createOffLineOrderForMember("1000",1L,"0","1000",one,1L);
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
//
//  }
//
