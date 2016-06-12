package com.jifenke.lepluslive.score.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.domain.entities.ScoreADetail;
import com.jifenke.lepluslive.score.domain.entities.ScoreB;
import com.jifenke.lepluslive.score.domain.entities.ScoreBDetail;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreBService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;

/**
 * 积分和红包的查询 Created by zhangwen on 2016/5/11.
 */
@Controller
@RequestMapping("/score")
public class ScoreController {

  @Inject
  private ScoreAService scoreAService;

  @Inject
  private ScoreBService scoreBService;

  @Inject
  private LeJiaUserService leJiaUserService;

  @ApiOperation(value = "红包列表")
  @RequestMapping(value = "/listA", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult listA(@RequestParam(required = false) String token) {

    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(token);
    ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
    if (scoreA == null) {
      return LejiaResult.build(207, "请先登录");
    }

    List<ScoreADetail> aDetails = scoreAService.findAllScoreADetailByScoreA(scoreA);
    return LejiaResult.build(200, "ok", aDetails);
  }

  @ApiOperation(value = "B积分列表")
  @RequestMapping(value = "/listB", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult listB(@RequestParam(required = false) String token) {

    LeJiaUser leJiaUser = leJiaUserService.findUserByUserSid(token);
    ScoreB scoreB = scoreBService.findScoreBByleJiaUser(leJiaUser);
    if (scoreB == null) {
      return LejiaResult.build(207, "请先登录");
    }

    List<ScoreBDetail> bDetails = scoreBService.findAllScoreBDetailByScoreB(scoreB);
    return LejiaResult.build(200, "ok", bDetails);
  }

}
