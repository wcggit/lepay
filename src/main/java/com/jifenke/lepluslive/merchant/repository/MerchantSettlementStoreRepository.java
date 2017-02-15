package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlementStore;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 富友门店结算规则 Created by zhangwen on 16/12/6.
 */
public interface MerchantSettlementStoreRepository
    extends JpaRepository<MerchantSettlementStore, Long> {

  /**
   * 获取富友门店结算规则  16/12/6
   *
   * @param merchantId 门店ID
   */
  MerchantSettlementStore findByMerchantId(Long merchantId);

}
