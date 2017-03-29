package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.TemporaryMerchantUserShop;
import com.jifenke.lepluslive.merchant.repository.TemporaryMerchantUserShopRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by wcg on 16/3/17.
 */
@Service
@Transactional(readOnly = true)
public class TemporaryMerchantUserShopService {

  @Inject
  private TemporaryMerchantUserShopRepository repository;

  public List<TemporaryMerchantUserShop> findAllByMerchant(Merchant merchant) {
    return repository.findByMerchant(merchant);
  }
}
