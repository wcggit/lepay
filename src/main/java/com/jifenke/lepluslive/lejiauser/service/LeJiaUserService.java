package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.BankCard;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.RegisterOrigin;
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
public class LeJiaUserService {

  @Value("${bucket.ossBarCodeReadRoot}")
  private String barCodeRootUrl;

  @Inject
  private LeJiaUserRepository leJiaUserRepository;

  @Inject
  private BankCardRepository bankCardRepository;

  @Inject
  private WeiXinUserService weiXinUserService;

  @Inject
  private RegisterOriginService registerOriginService;

  @Inject
  private DictionaryService dictionaryService;

  @Inject
  private ScoreAService scoreAService;

  /**
   * 根据用户ID获取用户信息  16/10/14
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public LeJiaUser findUserById(Long id) {
    return leJiaUserRepository.findOne(id);
  }


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
    if (currPage == null || currPage <= 0) {
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
    if (leJiaUser.getWeiXinUser() != null && leJiaUser.getWeiXinUser().getState() == 1) {
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

    Optional<BankCard> bankCard = bankCardRepository.findByNumberAndState(cardNumber, 1);
    if (bankCard.isPresent()) {
      return bankCard.get().getLeJiaUser();
    }
    return null;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void checkUserBindCard(LeJiaUser leJiaUser, String cardNo) {
    if (cardNo != null) {
      Optional<BankCard> bankCard = bankCardRepository.findByNumberAndState(cardNo, 1);
      if (!bankCard.isPresent()) {
        BankCard newCard = new BankCard();
        newCard.setLeJiaUser(leJiaUser);
        newCard.setNumber(cardNo);
        bankCardRepository.save(newCard);
      }
    }
  }

  /**
   * 保存用户信息 16/10/24
   *
   * @param leJiaUser 用户
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveUser(LeJiaUser leJiaUser) {
    try {
      leJiaUserRepository.save(leJiaUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 商户支付页注册 17/2/27
   *
   * @param order       订单
   * @param phoneNumber 填充的手机号码
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Long registerByPay(OffLineOrder order, String phoneNumber)
      throws Exception {
    LeJiaUser leJiaUser = order.getLeJiaUser();
    ScoreA scoreA = scoreAService.findScoreAByLeJiaUser(leJiaUser);
    int flag = scoreAService.findByScoreAAndOriginAndOrderSid(scoreA, 9, order.getOrderSid());
    if (flag == 1) {
      return 0L;
    }
    Merchant merchant = order.getMerchant();
    try {
      //发放红包
      String[] str = dictionaryService.findDictionaryById(53L).getValue().split("_");
      int min = Integer.valueOf(str[0]);
      int max = Integer.valueOf(str[1]);
      long backA = (long) (Math.random() * (max - min)) + min;
      scoreAService.saveScoreA(scoreA, 1, backA);
      scoreAService.saveScoreCDetail(scoreA, 1, backA, 9, "手机注册送鼓励金", order.getOrderSid());
      //保存注册来源
      Date date = new Date();
      leJiaUser.setPhoneNumber(phoneNumber);
      leJiaUser.setPhoneBindDate(date);
      RegisterOrigin origin = registerOriginService.findAndSaveByMerchantAndOriginType(merchant, 3);
      leJiaUser.setRegisterOrigin(origin);
      WeiXinUser weiXinUser = leJiaUser.getWeiXinUser();
      weiXinUser.setState(1);
      weiXinUser.setStateDate(date);
      weiXinUserService.saveWeiXinUser(weiXinUser);
      leJiaUserRepository.save(leJiaUser);
      return backA;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
}
