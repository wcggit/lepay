package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by wcg on 16/3/17.
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

  Page<Merchant> findAll(Pageable pageable);

  @Query(value = "select count(*) from merchant group by ?1", nativeQuery = true)
  int getMerchantSid(String location);

  /**
   * 按照距离远近对商家排序
   *
   */
  @Query(value = "SELECT m.* FROM merchant m INNER JOIN merchant_user_shop s ON m.id = s.merchant_id WHERE s.merchant_user_id = ?1", nativeQuery = true)
  List<Merchant> findByMerchantUser(Long merchantUserId);


  Optional<Merchant> findByMerchantSid(String sid);

  /**
   * 查询某一商户下所有门店的详细信息  2017/01/09
   *
   * @param merchantUser 商户
   */
  List<Merchant> findByMerchantUser(MerchantUser merchantUser);
}
