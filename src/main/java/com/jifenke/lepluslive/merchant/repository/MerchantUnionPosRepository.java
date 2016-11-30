package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantUnionPos;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhangwen on 16/11/22.
 */
public interface MerchantUnionPosRepository extends JpaRepository<MerchantUnionPos, Long> {

  MerchantUnionPos findByMerchantId(Long merchantId);
}
