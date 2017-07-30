package com.jifenke.lepluslive.yibao.service;


import com.jifenke.lepluslive.fuyou.util.YbRequestUtils;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.yibao.domain.entities.LedgerTransfer;
import com.jifenke.lepluslive.yibao.domain.entities.LedgerTransferLog;
import com.jifenke.lepluslive.yibao.repository.LedgerTransferRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;


/**
 * Created by zhangwen on 2017/7/12.
 */
@Service
@Transactional(readOnly = true)
public class LedgerTransferService {

  @Inject
  private LedgerTransferRepository ledgerTransferRepository;

  @Inject
  private LedgerTransferLogService ledgerTransferLogService;

  /**
   * 转账  2017/7/19
   *
   * @param ledgerNo  易宝的子商户号
   * @param amount    转账金额（注意：此时单位为分，调用接口时需/100转换为元）
   * @param tradeDate 清算日期
   * @param type      转账类型 1=交易实时转账，2=定时合并转账
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void transfer(String ledgerNo, Long amount, String tradeDate, Integer type) {
    LedgerTransfer transfer = new LedgerTransfer();
    String orderSid = MvUtil.getOrderNumber(10);
    transfer.setActualTransfer(amount);
    transfer.setType(type);
    transfer.setLedgerNo(ledgerNo);
    transfer.setOrderSid(orderSid);
    transfer.setTradeDate(tradeDate);
    LedgerTransferLog log = new LedgerTransferLog();
    log.setAmount(amount);
    log.setLedgerNo(ledgerNo);
    log.setOrderSid(orderSid);
    log.setRequestId(orderSid);
    log.setType(type);
    int state = 0;
    Map<String, String>
        resultMap =
        YbRequestUtils.transfer(ledgerNo, amount, transfer.getOrderSid());
    //注意：错误码为 162005、988888、999999：需要通过补偿查询（5.6 转账查询接口）确认转账状
    //态。状态为成功时：转账已成功；若状态为非终止状态（终止转态：COMPLETE、FAIL）需等状态
    // 为终止状态时，再进行下一步操作
    String code = resultMap.get("code");
    if (!"1".equals(code)) {
      state = Integer.valueOf(code);
      log.setMsg(resultMap.get("msg"));
      //第一次转账异常，给对应人员发送短信或消息 todo: 待完成
      System.out.println("==============第一次转账异常===============");
      if ("162005".equals(code) || "988888".equals(code) || "999999".equals(code)) {
        //补偿查询
        Map<String, String> map = YbRequestUtils.queryTransfer(transfer.getOrderSid());
        if ("1".equals(map.get("code"))) {
          String status = map.get("status");
          if ("COMPLETE".equals(status)) {
            state = 1;
          } else if ("FAIL".equals(status)) {
            state = 2;
          } else { //非终态，全部转账完成后对该状态统一查询一次
            state = 3;
          }
        }
      }
    } else {
      state = 1;
    }
    transfer.setDateCompleted(new Date());
    transfer.setState(state);
    ledgerTransferRepository.save(transfer);
    log.setDateCompleted(new Date());
    log.setState(state);
    ledgerTransferLogService.saveLog(log);
  }
}
