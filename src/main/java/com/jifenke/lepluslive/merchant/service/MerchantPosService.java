package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
import com.jifenke.lepluslive.merchant.repository.MerchantPosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/4.
 */
@Service
@Transactional(readOnly = true)
public class MerchantPosService {

  @Inject
  private MerchantPosRepository merchantPosRepository;

  public MerchantPos findMerchantPosByPosId(String posId) {
    return merchantPosRepository.findByPosId(posId);
  }

}
