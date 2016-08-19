package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.PosOrderLog;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/8/4.
 */
public interface PosOrderLogRepository extends JpaRepository<PosOrderLog,Long> {

}
