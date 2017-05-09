package com.jifenke.lepluslive.score.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;
import com.jifenke.lepluslive.score.domain.entities.ScoreD;
import com.jifenke.lepluslive.score.repository.ScoreCDetailRepository;
import com.jifenke.lepluslive.score.repository.ScoreDRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/3/18.
 */
@Service
@Transactional(readOnly = true)
public class ScoreDService {

  @Inject
  private ScoreDRepository scoreDRepository;

  @Inject
  private ScoreCDetailRepository scoreCDetailRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ScoreD findScoreDByleJiaUserAndMerchantUser(LeJiaUser leJiaUser, MerchantUser merchantUser) {
    return scoreDRepository.findByLeJiaUserAndMerchantUser(leJiaUser,merchantUser);
  }



}
