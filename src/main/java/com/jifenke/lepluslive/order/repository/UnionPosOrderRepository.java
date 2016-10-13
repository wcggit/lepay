package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wcg on 16/8/9.
 */
public interface UnionPosOrderRepository extends JpaRepository<UnionPosOrder, Long> {

  UnionPosOrder findByOrderSid(String orderNo);

  /**
   * 用户在某个商户用银联POS消费成功的次数 16/10/10
   */
  @Query(value = "select count(*),SUM(total_price) from union_pos_order where le_jia_user_id = ?1 and merchant_id = ?2 and state = 1", nativeQuery = true)
  List<Object[]> countByLeJiaUserAndMerchantAndState(Long leJiaUserId, Long merchantId);

}
