package com.jifenke.lepluslive.global.abstraction;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import java.util.Date;

/**
 * Created by wcg on 16/8/26.
 */
public interface Order {

  Merchant getMerchant();

  LeJiaUser getLeJiaUser();

  Long getTrueScore();

  Long getRebate();

  String getOrderSid();

  Date getCompleteDate();

  Long getLjCommission();

  Long getWxCommission();

  Long getScoreB();

  Long getTruePay();

  Long getTotalPrice();

}
