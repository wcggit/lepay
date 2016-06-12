package com.jifenke.lepluslive.wxpay.service;


import com.jifenke.lepluslive.wxpay.domain.entities.WeixinPayLog;
import com.jifenke.lepluslive.wxpay.repository.WeixinPayLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by zhangwen on 2016/5/25.
 */
@Service
@Transactional(readOnly = true)
public class WeixinPayLogService {

  @Inject
  private WeixinPayLogRepository weixinPayLogRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void savePayLog(String outTradeNo, String returnCode, String resultCode) {

    WeixinPayLog weixinPayLog = new WeixinPayLog(outTradeNo, returnCode, resultCode);

    weixinPayLogRepository.save(weixinPayLog);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void savePayLog(String outTradeNo, String returnCode, String resultCode,
                                            String tradeState) {
    WeixinPayLog weixinPayLog = new WeixinPayLog(outTradeNo, returnCode, resultCode, tradeState);
          weixinPayLogRepository.save(weixinPayLog);
     }

}
