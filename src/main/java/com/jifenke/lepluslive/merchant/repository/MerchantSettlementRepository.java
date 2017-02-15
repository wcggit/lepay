package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantSettlement;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 富友结算规则、类型及结算账户 Created by zhangwen on 16/12/6.
 */
public interface MerchantSettlementRepository extends JpaRepository<MerchantSettlement, Long> {


}
