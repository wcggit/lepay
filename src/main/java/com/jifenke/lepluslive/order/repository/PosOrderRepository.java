package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.PosOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wcg on 16/8/2.
 */
public interface PosOrderRepository extends JpaRepository<PosOrder,Long> {

  PosOrder findByOrderSid(String orderNo);

  List<PosOrder> findByStateAndTrueScoreAndLeJiaUserIsNull(int state,Long trueScore);
}
