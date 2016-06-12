package com.jifenke.lepluslive.score.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.score.domain.entities.ScoreB;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/3/18.
 */
public interface ScoreBRepository  extends JpaRepository<ScoreB,Long>{

  ScoreB findByLeJiaUser(LeJiaUser leJiaUser);
}
