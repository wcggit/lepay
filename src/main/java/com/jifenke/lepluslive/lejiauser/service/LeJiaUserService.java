package com.jifenke.lepluslive.lejiauser.service;


import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.lejiauser.domain.entities.BankCard;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.repository.BankCardRepository;
import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.partner.domain.entities.Partner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
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
public class LeJiaUserService {

  @Value("${bucket.ossBarCodeReadRoot}")
  private String barCodeRootUrl;

  @Inject
  private LeJiaUserRepository leJiaUserRepository;

  @Inject
  private BankCardRepository bankCardRepository;

  /**
   * 根据用户token获取用户信息  16/10/10
   *
   * @param userSid token
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public LeJiaUser findUserByUserSid(String userSid) {
    return leJiaUserRepository.findByUserSid(userSid);
  }

  /**
   * 判断该手机号是否已经注册
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public LeJiaUser findUserByPhoneNumber(String phoneNumber) {
    return leJiaUserRepository.findByPhoneNumber(phoneNumber);
  }

  /**
   * POS机分页查询商家绑定的会员信息 16/10/12
   *
   * @param merchantId 商家ID
   * @param currPage   当前页码
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<Map> findUserByMerchantAndPage(Long merchantId, Integer currPage) {
    if (currPage == null || currPage < 0) {
      currPage = 1;
    }
    List<Object[]>
        list =
        leJiaUserRepository.findUserByMerchantAndPage(merchantId, (currPage - 1) * 10);
    List<Map> mapList = new ArrayList<>();
    if (list.size() > 0) {
      for (Object[] o : list) {
        Map<Object, Object> map = new HashMap<>();
        map.put("headImageUrl", o[0]);
        map.put("nickname", o[1]);
        map.put("date", o[2]);
        map.put("source", o[3]);
        mapList.add(map);
      }
      return mapList;
    }
    return null;
  }

  /**
   * POS机查询商家绑定的会员数量 16/10/12
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Long countUserByMerchant(Long merchantId) {
    return leJiaUserRepository.countMerchantBindLeJiaUser(merchantId);
  }

  /**
   * 判断是否需要绑定商户和合伙人
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkUserBindMerchant(LeJiaUser leJiaUser, Merchant merchant) {
    //判断是否需要绑定商户
    if (leJiaUser.getWeiXinUser().getState() == 1) {
      if (leJiaUser.getBindMerchant() == null) {
        long userLimit = leJiaUserRepository.countMerchantBindLeJiaUser(merchant.getId());
        if (merchant.getUserLimit() > userLimit) {
          leJiaUser.setBindMerchant(merchant);
          Partner partner = merchant.getPartner();
          leJiaUser.setBindMerchantDate(new Date());
          long partnerUserLimit = leJiaUserRepository.countPartnerBindLeJiaUser(partner.getId());
          partner = merchant.getPartner();

          if (partner.getUserLimit() > partnerUserLimit) {
            leJiaUser.setBindPartner(partner);
            leJiaUser.setBindPartnerDate(new Date());
          }
        }
      } else {
        if (leJiaUser.getBindPartner() == null) {
          //已绑定商户但是未绑定合伙人
          Merchant bindMerchant = leJiaUser.getBindMerchant();
          Partner partner = bindMerchant.getPartner();
          long partnerUserLimit = leJiaUserRepository.countPartnerBindLeJiaUser(partner.getId());
          if (partner.getUserLimit() > partnerUserLimit) {
            leJiaUser.setBindPartner(partner);
            leJiaUser.setBindPartnerDate(new Date());
          }
        }
      }
    }
  }

  /**
   * 登录
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public LeJiaUser findLeJiaUserByCard(String cardNumber) {

    Optional<BankCard> bankCard = bankCardRepository.findByNumber(cardNumber);
    if (bankCard.isPresent()) {
      return bankCard.get().getLeJiaUser();
    }
    return null;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkUserBindCard(LeJiaUser leJiaUser, String cardNo) {
    Optional<BankCard> bankCard = bankCardRepository.findByNumber(cardNo);
    if (!bankCard.isPresent()) {
      BankCard newCard = new BankCard();
      newCard.setLeJiaUser(leJiaUser);
      newCard.setNumber(cardNo);
      bankCardRepository.save(newCard);
    }

  }
}
