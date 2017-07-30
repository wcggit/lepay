package com.jifenke.lepluslive.yibao.repository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.yibao.domain.entities.MerchantLedger;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhangwen on 2017/7/12.
 */
public interface MerchantLedgerRepository extends JpaRepository<MerchantLedger, Long> {

  MerchantLedger findByMerchant(Merchant merchant);

}
