package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrderLog;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/8/9.
 */
public interface UnionPosOrderRepository extends JpaRepository<UnionPosOrderLog, Long> {

}
