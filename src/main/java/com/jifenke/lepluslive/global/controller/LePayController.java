package com.jifenke.lepluslive.global.controller;

import com.jifenke.lepluslive.global.util.MvUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by wcg on 16/5/16.
 */
@RestController
@RequestMapping("/lepay")
public class LePayController {

  @RequestMapping("/scan")
  public ModelAndView goScanPage(){
    return MvUtil.go("/scanRvCode");
  }

}
