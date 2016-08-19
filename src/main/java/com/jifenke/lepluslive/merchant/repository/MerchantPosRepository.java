package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/8/4.
 */
public interface MerchantPosRepository extends JpaRepository<MerchantPos,Long>{

  MerchantPos findByPosId(String posId);

}
