package com.jifenke.lepluslive.score.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;
import com.jifenke.lepluslive.score.domain.entities.ScoreD;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/3/18.
 */
public interface ScoreDRepository extends JpaRepository<ScoreD,Long>{

  ScoreD findByLeJiaUserAndMerchantUser(LeJiaUser leJiaUser,MerchantUser merchantUser);
}
