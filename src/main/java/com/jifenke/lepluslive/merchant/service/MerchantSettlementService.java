package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlement;
import com.jifenke.lepluslive.merchant.repository.MerchantSettlementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 富友结算规则、类型及结算账户 Created by zhangwen on 2016/12/6.
 */
@Service
@Transactional(readOnly = true)
public class MerchantSettlementService {

  @Inject
  private MerchantSettlementRepository repository;

  /**
   * 获取富友商户结算规则  16/12/6
   *
   * @param id ID
   */
  public MerchantSettlement findById(Long id) {
    return repository.findOne(id);
  }

}
