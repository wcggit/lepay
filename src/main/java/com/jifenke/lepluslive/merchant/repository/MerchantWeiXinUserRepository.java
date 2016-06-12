package com.jifenke.lepluslive.merchant.repository;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWeiXinUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wcg on 16/5/17.
 */
public interface MerchantWeiXinUserRepository extends JpaRepository<MerchantWeiXinUser,Long> {

  MerchantWeiXinUser findByOpenId(String openid);


  List<MerchantWeiXinUser> findAllByMerchantUser(MerchantUser merchantUser);
}
