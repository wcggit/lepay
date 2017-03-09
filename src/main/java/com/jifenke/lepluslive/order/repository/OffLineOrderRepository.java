package com.jifenke.lepluslive.order.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * Created by wcg on 16/5/5.
 */
public interface OffLineOrderRepository extends JpaRepository<OffLineOrder, Long> {

  OffLineOrder findByOrderSid(String orderSid);

  @Query(value = "select count(*) from off_line_order where merchant_id = ?1 and complete_date between ?2 and ?3 and state = 1", nativeQuery = true)
  Long countMerchantMonthlyOrder(Long id, Date time, Date date);

  /**
   * 用户在某个商户消费成功的次数和金额 16/10/10
   */
  @Query(value = "select count(*),SUM(total_price) from off_line_order where le_jia_user_id = ?1 and merchant_id = ?2 and state = 1", nativeQuery = true)
  List<Object[]> countByLeJiaUserAndMerchantAndState(Long leJiaUserId, Long merchantId);

  Optional<OffLineOrder> findByLeJiaUserAndCriticalOrderAndCompleteDateBetween(LeJiaUser leJiaUser, int i, Date start,
                                                        Date end);

  @Query(value = "select ifnull(sum(rebate),0) from off_line_order where le_jia_user_id = ?1 and state = 1 and (rebate_way = 1 or rebate_way = 3) and complete_date between ?2 and ?3", nativeQuery = true)
  Long calculateRebateScoreA(Long leJiaUserId, Date dateStart, Date dateEnd);

  @Query(value = "select ifnull(sum(rebate),0),DATE_FORMAT(complete_date,'%Y%m%d') from off_line_order where le_jia_user_id = ?1 and state = 1 and (rebate_way = 1 or rebate_way = 3) and complete_date between ?2 and ?3  group by DATE_FORMAT(complete_date,'%Y%m%d')", nativeQuery = true)
  List<Object[]> statisticRebateGroupByCompleteDate(Long id, Date start, Date end);

}
