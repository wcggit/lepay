package com.jifenke.lepluslive.activity.repository;

import com.jifenke.lepluslive.activity.domain.entities.InitialOrderRebateActivity;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by wcg on 16/9/18.
 */
public interface InitialOrderRebateActivityRepository
    extends JpaRepository<InitialOrderRebateActivity, Long> {

  InitialOrderRebateActivity findByMerchant(Merchant merchant);

  @Query(value = "select count(*) from off_line_order where state = 1 and le_jia_user_id = ?1 and total_price > ?2 and id !=?3", nativeQuery = true)
  Long checkUserInitialOrNot(Long id, String limit, Long orderId);
}
