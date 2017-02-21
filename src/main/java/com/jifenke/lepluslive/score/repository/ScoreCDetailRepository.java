package com.jifenke.lepluslive.score.repository;

import com.jifenke.lepluslive.score.domain.entities.ScoreB;
import com.jifenke.lepluslive.score.domain.entities.ScoreBDetail;
import com.jifenke.lepluslive.score.domain.entities.ScoreC;
import com.jifenke.lepluslive.score.domain.entities.ScoreCDetail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wcg on 16/3/18.
 */
public interface ScoreCDetailRepository extends JpaRepository<ScoreCDetail,Long>{


}
