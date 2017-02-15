package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantScanPayWay;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 富友结算规则、类型及结算账户 Created by zhangwen on 16/12/6.
 */
public interface MerchantScanPayWayRepository extends JpaRepository<MerchantScanPayWay, Long> {

  /**
   * 门店扫码支付方式  16/12/6
   *
   * @param merchantId 门店ID
   */
  MerchantScanPayWay findByMerchantId(Long merchantId);

}
