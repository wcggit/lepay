package com.jifenke.lepluslive.wxpay.repository;


import com.jifenke.lepluslive.wxpay.domain.entities.WeixinPayLog;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhangwen on 16/5/25.
 */
public interface WeixinPayLogRepository extends JpaRepository<WeixinPayLog, Long> {

}
