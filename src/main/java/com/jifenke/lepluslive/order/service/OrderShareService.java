package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.global.abstraction.Order;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrderShare;
import com.jifenke.lepluslive.order.domain.entities.PosOrder;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.repository.OffLineOrderShareRepository;
import com.jifenke.lepluslive.partner.service.PartnerService;
import com.jifenke.lepluslive.wxpay.service.DictionaryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/29.
 */
@Service
public class OrderShareService {

  @Inject
  private DictionaryService dictionaryService;

  @Inject
  private PartnerService partnerService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private OffLineOrderShareRepository offLineOrderShareRepository;

  //分润
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void offLIneOrderShare(Order order) {
    OffLineOrderShare offLineOrderShare;
    BigDecimal
        shareMoney =
        new BigDecimal(order.getLjCommission() - order.getRebate() - order
            .getWxCommission());
    Long type = 0L;
    offLineOrderShare = new OffLineOrderShare();
    if (order instanceof OffLineOrder) {
      type = 1L;
      OffLineOrder offLineOrder = (OffLineOrder) order;
      offLineOrderShare.setOffLineOrder(offLineOrder);
      shareMoney = shareMoney.subtract(new BigDecimal(offLineOrder.getLjProfit()));
      offLineOrderShare.setType(1);
    }
    if (order instanceof PosOrder) {
      type = 2L;
      PosOrder posOrder = (PosOrder) order;
      offLineOrderShare.setPosOrder(posOrder);
      shareMoney = shareMoney.subtract(new BigDecimal(posOrder.getLjProfit()));
      offLineOrderShare.setType(2);
    }
    if (order instanceof UnionPosOrder) {
      type = 2L;
      offLineOrderShare.setUnionPosOrder((UnionPosOrder) order);
      offLineOrderShare.setType(3);
    }
    if (shareMoney.doubleValue() > 0) {

      offLineOrderShare.setShareMoney(shareMoney.longValue());
      offLineOrderShare.setTradeMerchant(order.getMerchant());
      //分润给交易合伙人
      long toLockMerchant = 0L;
      long toLockPartner = 0L;
      long toLockPartnerManager = 0L;
      long
          toTradePartner =
          (long) Math.floor(shareMoney.multiply(
              new BigDecimal(dictionaryService.findDictionaryById(11L).getValue())).doubleValue()
                            / 100.0);
      partnerService.shareToPartner(toTradePartner, order.getMerchant().getPartner(),
                                    order.getOrderSid(), type);
      offLineOrderShare.setTradePartner(order.getMerchant().getPartner());
      //分润给交易合伙人管理员
      long
          toTradePartnerManager =
          (long) Math.floor(shareMoney.multiply(
              new BigDecimal(dictionaryService.findDictionaryById(12L).getValue())).doubleValue()
                            / 100.0);
      partnerService.shareToPartnerManager(toTradePartnerManager,
                                           order.getMerchant().getPartner()
                                               .getPartnerManager(), order.getOrderSid(),
                                           type);
      offLineOrderShare.setTradePartnerManager(order.getMerchant().getPartner()
                                                   .getPartnerManager());

      offLineOrderShare.setToTradePartner(toTradePartner);
      offLineOrderShare.setToTradePartnerManager(toTradePartnerManager);
      LeJiaUser leJiaUser = order.getLeJiaUser();
      if (leJiaUser.getBindMerchant() != null) {
        toLockMerchant =
            (long) Math.floor(shareMoney.multiply(
                new BigDecimal(dictionaryService.findDictionaryById(13L).getValue()))
                                  .doubleValue() / 100.0);
        //分润给绑定商户
        Merchant bindMerchant = leJiaUser.getBindMerchant();
        if (bindMerchant.getPartnership() == 2) {//如果是虚拟商户分润方式改变
          offLineOrderShare.setToLockMerchant(0L);
        } else {
          offLineOrderShare.setToLockMerchant(toLockMerchant);
          merchantService.shareToMerchant(toLockMerchant, bindMerchant,
                                          order.getOrderSid(), type);
          offLineOrderShare.setLockMerchant(leJiaUser.getBindMerchant());
        }
        if (leJiaUser.getBindPartner() != null) {
          toLockPartner =
              (long) Math.floor(shareMoney.multiply(
                  new BigDecimal(dictionaryService.findDictionaryById(14L).getValue()))
                                    .doubleValue() / 100.0);
          offLineOrderShare
              .setToLockPartner(bindMerchant.getPartnership() == 2 ? toLockMerchant + toLockPartner
                                                                   : toLockPartner);
          //分润给绑定合伙人
          partnerService
              .shareToPartner(bindMerchant.getPartnership() == 2 ? toLockMerchant + toLockPartner
                                                                 : toLockPartner,
                              leJiaUser.getBindPartner(), order.getOrderSid(),
                              type);
          toLockPartnerManager =
              (long) Math.floor(shareMoney.multiply(
                  new BigDecimal(dictionaryService.findDictionaryById(15L).getValue()))
                                    .doubleValue() / 100.0);
          //分润给绑定合伙人管理员
          partnerService.shareToPartnerManager(toLockPartnerManager,
                                               leJiaUser.getBindPartner().getPartnerManager(),
                                               order.getOrderSid(), type);
          offLineOrderShare.setToLockPartnerManager(toLockPartnerManager);
          offLineOrderShare.setLockPartner(leJiaUser.getBindPartner());
          offLineOrderShare.setLockPartnerManager(leJiaUser.getBindPartner().getPartnerManager());
        }
      }

      offLineOrderShare
          .setToLePlusLife(
              shareMoney.longValue() - toTradePartner - toTradePartnerManager - toLockMerchant
              - toLockPartner - toLockPartnerManager);
      partnerService.shareToPartnerManager(offLineOrderShare.getToLePlusLife(),
                                           partnerService.findPartnerManagerById(1L),
                                           order.getOrderSid(), type);
      offLineOrderShare.setCreateDate(order.getCompleteDate());
      offLineOrderShareRepository.save(offLineOrderShare);


    }
  }


}
