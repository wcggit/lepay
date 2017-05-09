package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantStoredActivity;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUnionPos;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhangwen on 16/11/22.
 */
public interface MerchantStoredActivityRepository extends JpaRepository<MerchantStoredActivity, Long> {

  MerchantStoredActivity findByMerchantUser(MerchantUser merchantUser);
}
