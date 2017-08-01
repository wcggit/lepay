package com.jifenke.lepluslive.yibao.service;


import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.yibao.domain.entities.MerchantLedger;
import com.jifenke.lepluslive.yibao.repository.MerchantLedgerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


/**
 * Created by zhangwen on 2017/7/12.
 */
@Service
@Transactional(readOnly = true)
public class MerchantLedgerService {

  @Inject
  private MerchantLedgerRepository repository;

  public MerchantLedger findByMerchant(Merchant merchant) {
    return repository.findByMerchant(merchant);
  }

}
