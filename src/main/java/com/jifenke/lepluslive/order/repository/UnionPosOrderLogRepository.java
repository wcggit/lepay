package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrder;
import com.jifenke.lepluslive.order.domain.entities.UnionPosOrderLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by wcg on 16/8/9.
 */
public interface UnionPosOrderLogRepository extends JpaRepository<UnionPosOrderLog, Long> {

}
