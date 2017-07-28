package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantScanPayWay;
import com.jifenke.lepluslive.merchant.repository.MerchantScanPayWayRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 门店扫码支付方式 Created by zhangwen on 2016/12/6.
 */
@Service
@Transactional(readOnly = true)
public class MerchantScanPayWayService {

  @Inject
  private MerchantScanPayWayRepository repository;

  /**
   * 门店扫码支付方式  16/12/6
   *
   * @param merchantId 门店ID
   * @return 扫码支付方式  0=富友结算|1=乐加结算|2=暂不开通|3=易宝结算
   */
  public int findByMerchantId(Long merchantId) {
    MerchantScanPayWay scanPayWay = repository.findByMerchantId(merchantId);
    if (scanPayWay != null) {
      return scanPayWay.getType();
    } else {
      return 1;
    }
  }

  /**
   * 门店扫码支付方式  16/12/6
   *
   * @param merchantId 门店ID
   * @return 扫码支付方式  0=富友结算|1=乐加结算|2=暂不开通
   */
  public MerchantScanPayWay findMerchantScanPayWayByMerchantId(Long merchantId) {
    MerchantScanPayWay scanPayWay = repository.findByMerchantId(merchantId);
    return scanPayWay;
  }

}
