package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by wcg on 16/5/5.
 */
public interface OffLineOrderRepository extends JpaRepository<OffLineOrder,Long> {

  OffLineOrder findByOrderSid(String orderSid);
}
