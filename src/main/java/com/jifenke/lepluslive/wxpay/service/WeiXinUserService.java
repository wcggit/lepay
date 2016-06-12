package com.jifenke.lepluslive.wxpay.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.RegisterOrigin;
import com.jifenke.lepluslive.lejiauser.service.BarcodeService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.domain.entities.ScoreB;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.score.repository.ScoreARepository;
import com.jifenke.lepluslive.score.repository.ScoreBRepository;
import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;
import com.jifenke.lepluslive.wxpay.repository.WeiXinUserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcg on 16/3/18.
 */
@Service
@Transactional(readOnly = true)
public class WeiXinUserService {

  @Value("${bucket.ossBarCodeReadRoot}")
  private String barCodeRootUrl;


  @Inject
  private BarcodeService barcodeService;

  @Inject
  private WeiXinUserRepository weiXinUserRepository;

  @Inject
  private ScoreARepository scoreARepository;

  @Inject
  private ScoreBRepository scoreBRepository;

  @Inject
  private LeJiaUserRepository leJiaUserRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public WeiXinUser findWeiXinUserByOpenId(String openId) {
    return weiXinUserRepository.findByOpenId(openId);
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void registerLeJiaUserForNonMember(String openid, WeiXinUser weiXinUser) {
    if (weiXinUser == null) {
      weiXinUser = new WeiXinUser();
      weiXinUser.setOpenId(openid);
      weiXinUser.setDateCreated(new Date());
      weiXinUser.setLastUpdated(new Date());
      LeJiaUser leJiaUser = new LeJiaUser();
      leJiaUser.setWeiXinUser(weiXinUser);
      RegisterOrigin registerOrigin = new RegisterOrigin();
      registerOrigin.setId(1L);
      leJiaUser.setRegisterOrigin(registerOrigin);
      leJiaUserRepository.save(leJiaUser);
      weiXinUser.setLeJiaUser(leJiaUser);
      ScoreA scoreA = new ScoreA();
      scoreA.setScore(0L);
      scoreA.setLeJiaUser(leJiaUser);
      scoreARepository.save(scoreA);
      ScoreB scoreB = new ScoreB();
      scoreB.setScore(0L);
      scoreB.setLeJiaUser(leJiaUser);
      scoreBRepository.save(scoreB);
      weiXinUser.setState(0);
      weiXinUserRepository.save(weiXinUser);
    }
  }
}
