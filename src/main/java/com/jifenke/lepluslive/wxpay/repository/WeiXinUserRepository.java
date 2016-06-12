package com.jifenke.lepluslive.wxpay.repository;

import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/3/18.
 */
public interface WeiXinUserRepository extends JpaRepository<WeiXinUser,Long> {

  WeiXinUser findByOpenId(String openId);
}
