package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.AliUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.BankCard;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.RegisterOrigin;
import com.jifenke.lepluslive.lejiauser.repository.AliUserRepository;
import com.jifenke.lepluslive.lejiauser.repository.BankCardRepository;
import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.partner.domain.entities.Partner;
import com.jifenke.lepluslive.score.domain.entities.ScoreA;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/4/21.
 */
@Service
@Transactional(readOnly = true)
public class AliUserService {


  @Inject
  private AliUserRepository aliUserRepository;

  /**
   * 根据用户ID获取用户信息  16/10/14
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public AliUser findUserById(String userId) {
    return aliUserRepository.findByUserId(userId);
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void bindLejiaUser(String userId, LeJiaUser leJiaUser) {
    AliUser userById = findUserById(userId);
    if(userById==null){
      AliUser aliUser = new AliUser();
      aliUser.setLeJiaUser(leJiaUser);
      aliUser.setUserId(userId);
      aliUserRepository.save(aliUser);
    }
  }
}
