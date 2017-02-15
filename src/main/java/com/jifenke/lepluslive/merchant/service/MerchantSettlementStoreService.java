package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlementStore;
import com.jifenke.lepluslive.merchant.repository.MerchantSettlementStoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 富友门店结算规则 Created by zhangwen on 2016/12/6.
 */
@Service
@Transactional(readOnly = true)
public class MerchantSettlementStoreService {

  @Inject
  private MerchantSettlementStoreRepository repository;

  /**
   * 获取富友门店结算规则  16/12/6
   *
   * @param merchantId 门店ID
   */
  public MerchantSettlementStore findByMerchantId(Long merchantId) {
    return repository.findByMerchantId(merchantId);
  }

}
