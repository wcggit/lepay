package com.jifenke.lepluslive.lejiauser.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wcg on 16/3/24.
 */
public interface LeJiaUserRepository extends JpaRepository<LeJiaUser, Long> {

  LeJiaUser findByUserSid(String userSid);

  LeJiaUser findByPhoneNumber(String phoneNumber);

  @Query(value = "select count(*) from le_jia_user where bind_merchant_id = ?1", nativeQuery = true)
  Long countMerchantBindLeJiaUser(Long merchantId);

  @Query(value = "select count(*) from le_jia_user,wei_xin_user  where wei_xin_user.le_jia_user_id = le_jia_user.id and wei_xin_user.state=1 and bind_partner_id =?1", nativeQuery = true)
  Long countPartnerBindLeJiaUser(Long partnerId);

  /**
   * POS机分页查询商家绑定的会员信息 16/10/12
   */
  @Query(value = "SELECT w.head_image_url,w.nickname,u.bind_merchant_date,w.sub_source FROM le_jia_user u LEFT OUTER JOIN wei_xin_user w ON u.wei_xin_user_id = w.id WHERE u.bind_merchant_id = ?1 ORDER BY u.bind_merchant_date DESC LIMIT ?2,10", nativeQuery = true)
  List<Object[]> findUserByMerchantAndPage(Long merchantId, Integer currPage);
}
