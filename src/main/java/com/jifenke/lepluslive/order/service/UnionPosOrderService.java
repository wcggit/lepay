package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.repository.UnionPosOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/9.
 */
@Service
public class UnionPosOrderService {

  @Inject
  private UnionPosOrderRepository orderRepository;

  /**
   * 用户在某个商户用银联POS消费成功的次数和总额 16/10/10
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Object[] countByLeJiaUserAndMerchant(Long leJiaUserId, Long merchantId) {
    return orderRepository.countByLeJiaUserAndMerchantAndState(leJiaUserId, merchantId).get(0);
  }

  /**
   * 银联POS机查看某一订单详情 16/10/11
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public UnionPosOrder findUOrderById(Long orderId) {
    return orderRepository.findOne(orderId);
  }


}
