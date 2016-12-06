package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantUnionPos;
import com.jifenke.lepluslive.merchant.repository.MerchantUnionPosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 银联POS相关 Created by zhangwen on 16/11/22.
 */
@Service
@Transactional(readOnly = true)
public class MerchantUnionPosService {

  @Inject
  private MerchantUnionPosRepository unionPosRepository;

  /**
   * 获取某个商家的银联POS参数 16/11/22
   */
  public MerchantUnionPos findByMerchantId(Long merchantId) {
    return unionPosRepository.findByMerchantId(merchantId);
  }

}
