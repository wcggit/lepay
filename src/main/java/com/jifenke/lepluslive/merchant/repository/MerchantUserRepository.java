package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wcg on 16/5/18.
 */
public interface MerchantUserRepository extends JpaRepository<MerchantUser,Long> {

  MerchantUser findByName(String userName);

  List<MerchantUser> findAllByMerchant(Merchant merchant);

  MerchantUser findByMerchantAndType(Merchant merchant, int i);
}
