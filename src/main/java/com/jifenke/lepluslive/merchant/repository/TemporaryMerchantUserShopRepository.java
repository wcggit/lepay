package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.TemporaryMerchantUserShop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wcg on 16/3/17.
 */
public interface TemporaryMerchantUserShopRepository
    extends JpaRepository<TemporaryMerchantUserShop, Long> {

  List<TemporaryMerchantUserShop> findByMerchant(Merchant merchant);


}
