package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 富友扫码订单 Created by zhangwen on 16/12/6.
 */
public interface ScanCodeOrderRepository extends JpaRepository<ScanCodeOrder, String> {

  /**
   * 订单号查询订单  16/12/06
   *
   * @param orderSid 自有订单号
   */
  ScanCodeOrder findByOrderSid(String orderSid);

}
