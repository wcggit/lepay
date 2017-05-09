package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrderExt;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 富友扫码订单ext Created by zhangwen on 16/12/6.
 */
public interface ScanCodeOrderExtRepository extends JpaRepository<ScanCodeOrderExt, Long> {


}
