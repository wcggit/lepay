package com.jifenke.lepluslive.partner.service;

import com.jifenke.lepluslive.partner.domain.entities.Partner;
import com.jifenke.lepluslive.partner.domain.entities.PartnerManager;
import com.jifenke.lepluslive.partner.domain.entities.PartnerManagerWallet;
import com.jifenke.lepluslive.partner.domain.entities.PartnerManagerWalletLog;
import com.jifenke.lepluslive.partner.domain.entities.PartnerWallet;
import com.jifenke.lepluslive.partner.domain.entities.PartnerWalletLog;
import com.jifenke.lepluslive.partner.repository.PartnerManagerRepository;
import com.jifenke.lepluslive.partner.repository.PartnerManagerWalletLogRepository;
import com.jifenke.lepluslive.partner.repository.PartnerManagerWalletRepository;
import com.jifenke.lepluslive.partner.repository.PartnerRepository;
import com.jifenke.lepluslive.partner.repository.PartnerWalletLogRepository;
import com.jifenke.lepluslive.partner.repository.PartnerWalletRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by wcg on 16/6/3.
 */
@Service
@Transactional(readOnly = true)
public class PartnerService {


  @Inject
  private PartnerWalletLogRepository partnerWalletLogRepository;

  @Inject
  private PartnerWalletRepository partnerWalletRepository;

  @Inject
  private PartnerManagerWalletLogRepository partnerManagerWalletLogRepository;

  @Inject
  private PartnerManagerWalletRepository partnerManagerWalletRepository;

  @Inject
  private PartnerManagerRepository partnerManagerRepository;


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void shareToPartner(Long shareMoney, Partner partner, String orderSid, Long type) {
    if (shareMoney > 0) {
      PartnerWalletLog partnerWalletLog = new PartnerWalletLog();

      PartnerWallet partnerWallet = partnerWalletRepository.findByPartner(partner);
      Long availableBalance = partnerWallet.getAvailableBalance();
      partnerWalletLog.setBeforeChangeMoney(availableBalance);
      long afterShareMoney = availableBalance + shareMoney;

      partnerWalletLog.setAfterChangeMoney(afterShareMoney);

      partnerWalletLog.setPartnerId(partner.getId());

      partnerWalletLog.setOrderSid(orderSid);

      partnerWalletLog.setType(type);

      partnerWallet.setTotalMoney(partnerWallet.getTotalMoney() + shareMoney);

      partnerWallet.setAvailableBalance(afterShareMoney);

      partnerWalletLogRepository.save(partnerWalletLog);

      partnerWalletRepository.save(partnerWallet);
    }
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void shareToPartnerManager(Long shareMoney, PartnerManager partnerManager,
                                    String orderSid, Long type) {
    if (shareMoney > 0) {
      PartnerManagerWalletLog log = new PartnerManagerWalletLog();

      PartnerManagerWallet
          partnerManagerWallet =
          partnerManagerWalletRepository.findByPartnerManager(
              partnerManager);
      Long availableBalance = partnerManagerWallet.getAvailableBalance();
      log.setBeforeChangeMoney(availableBalance);
      long afterShareMoney = availableBalance + shareMoney;

      log.setAfterChangeMoney(afterShareMoney);

      log.setPartnerManagerId(partnerManager.getId());

      log.setOrderSid(orderSid);

      log.setType(type);

      partnerManagerWallet.setTotalMoney(partnerManagerWallet.getTotalMoney() + shareMoney);

      partnerManagerWallet.setAvailableBalance(afterShareMoney);

      partnerManagerWalletLogRepository.save(log);

      partnerManagerWalletRepository.save(partnerManagerWallet);
    }
  }

  public PartnerManager findPartnerManagerById(long l) {
    return partnerManagerRepository.findOne(l);
  }
}
