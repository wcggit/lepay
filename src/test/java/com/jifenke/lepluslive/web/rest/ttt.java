package com.jifenke.lepluslive.web.rest;

import com.jifenke.lepluslive.Application;
import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.score.repository.ScoreARepository;
import com.jifenke.lepluslive.score.repository.ScoreBRepository;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.repository.WeiXinUserRepository;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcg on 16/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles({Constants.SPRING_PROFILE_DEVELOPMENT})
public class ttt {


  @Inject
  private WeiXinUserRepository weiXinUserRepository;

  @Inject
  private LeJiaUserRepository leJiaUserRepository;

  @Inject
  private ScoreARepository scoreARepository;

  @Inject
  private LeJiaUserService leJiaUserService;

  @Inject
  private WeiXinPayService weiXinPayService;

  @Inject
  private OffLineOrderService offLineOrderService;

  @Inject
  private ScoreAService scoreAService;


  @Test
  public void tttt() {
//    OffLineOrder
//        offLineOrder =
//        offLineOrderService.findOffLineOrderByOrderSid("16052815212603340");
//    scoreAService.paySuccessForMember(offLineOrder);
////    Date start = new Date();
//    String
//        result =
//        Des.strDec(
//            "2D157C4763DFBCD966C384F81DC50CA03B62FE4351B82CD6C33B9FECC40A9996836DF36275B238386214B084EAB0DCD05BCAB1365060F4038BAEFF3E3E4F8393E15B7A11E9C9615455132B0C3034AC55C93A78E5665779A707C53B67857C85332D157C4763DFBCD966C384F81DC50CA03B62FE4351B82CD6C33B9FECC40A99967E73D1E9279F9D29110EB31D5BDC15C264D3B2CE23F8BD1AB21C1D400A2E6CB31DCDCA2B6D8C33775071C542662259077FA2895295942B60B6B93E9FD8D16B87AF53928D467BB234",
//            "lepluslife", null, null);
//    String[] strs = result.split(" ");
//    OffLineOrder
//        offLineOrder =
//        offLineOrderService.createOffLineOrderForMember(strs[0], Long.parseLong(strs[3]), strs[1],
//                                                        strs[4], leJiaUserService.findUserByUserSid(strs[2]));
//    Date end = new Date();
//    System.out.println(end.getTime()-start.getTime());
    //weiXinPayService.buildJsapiParams();
  }

////  public static void main(String[] args) {
////    int x[][] = new int[9][9];
////    for(int i=0;i<9;i++){
////      for(int y=0;y<9;y++){
////        x[i][y]=new Random().nextInt(2);
////      }
////    }
////    Scanner input = new Scanner(System.in);
////    int a = input.nextInt();
////    int b = input.nextInt();
////    int n = input.nextInt();
////
////    for(int z=1;z<n;z++){
////      int m = x[a][b];
////      int a1 = x[a-1][b];
////      int a2 = x[a+1][b];
////      int a3 = x[a][b+1];
////      int a4 = x[a][b-1];
////
////
////
////    }
//
//
//
//  }


}
