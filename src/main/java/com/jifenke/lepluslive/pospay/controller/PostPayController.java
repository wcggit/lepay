package com.jifenke.lepluslive.pospay.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/2.
 */
@RestController
@RequestMapping("/lepay")
public class PostPayController {

  @Inject
  private LeJiaUserService leJiaUserService;


  @RequestMapping(value = "/pospay/{sid}")
  public LejiaResult posCheckUser(@PathVariable String sid) {
    if (sid != null) {
      if (sid.length() == 11) {
        LeJiaUser leJiaUser = leJiaUserService.findUserByPhoneNumber(sid);
        if (leJiaUser != null && leJiaUser.getWeiXinUser().getState() == 1) {

        }
      } else {
        LeJiaUser leJiaUser = leJiaUserService.findLeJiaUserByCard(sid);

      }
    }
    return null;
  }

}
