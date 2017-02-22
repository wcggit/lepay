package com.jifenke.lepluslive.score.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.score.domain.entities.ScoreB;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/3/18.
 */
public interface ScoreCRepository extends JpaRepository<ScoreC,Long>{

  ScoreC findByLeJiaUser(LeJiaUser leJiaUser);
}
