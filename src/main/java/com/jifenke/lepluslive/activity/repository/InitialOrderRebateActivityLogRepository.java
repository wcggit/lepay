package com.jifenke.lepluslive.activity.repository;

import com.jifenke.lepluslive.activity.domain.entities.InitialOrderRebateActivityLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by wcg on 16/9/18.
 */
public interface InitialOrderRebateActivityLogRepository
    extends JpaRepository<InitialOrderRebateActivityLog, Long> {

  Page findAll(Specification<InitialOrderRebateActivityLog> whereClause, Pageable pageable);

  @Query(value = "select ifnull(sum(rebate),0) from initial_order_rebate_activity_log where merchant_id =?1 and created_date between ?2 and ?3 and state != 2", nativeQuery = true)
  Long countMerchantDailyIncome(Long id, Date start, Date end);

  @Query(value = "select ifnull(sum(rebate),0) from initial_order_rebate_activity_log where merchant_id =?1 and state  = ?2", nativeQuery = true)
  Long contInitialOrderLogByMerchantAndState(Long id, int i);

  @Modifying(clearAutomatically = true)
  @Query(value = "update initial_order_rebate_activity_log set state  = 1,nickname=?2,head_image_url=?3 where merchant_id = ?1 and  state  = 0", nativeQuery = true)
  void updateAllHandleLogToComplete(Long id, String nickname, String headImageUrl);

  @Modifying(clearAutomatically = true)
  @Query(value = "update initial_order_rebate_activity_log set exception_log  = ?2 where merchant_id = ?1 and  state  = 0", nativeQuery = true)
  void updateAllHandleLogToFail(Long id,String return_msg);
}
