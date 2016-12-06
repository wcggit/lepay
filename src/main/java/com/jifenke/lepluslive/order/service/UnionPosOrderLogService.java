package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrderLog;
import com.jifenke.lepluslive.order.repository.UnionPosOrderLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import javax.inject.Inject;

/**
 * 银联商务接口调用日志 Created by zhangwen on 16/11/23.
 */
@Service
public class UnionPosOrderLogService {

  @Inject
  private UnionPosOrderLogRepository orderLogRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderLog(String msgSysSn, String params, Integer interfaceType,
                                String reqSerialNo) {
    UnionPosOrderLog unionPosOrderLog = new UnionPosOrderLog();
    unionPosOrderLog.setInterfaceType(interfaceType);
    unionPosOrderLog.setParams(params);
    unionPosOrderLog.setReturnParams(params);
    orderLogRepository.save(unionPosOrderLog);
  }


  /**
   * 保存查询日志
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveLogBySearch(Map map, Integer interfaceType) {
    UnionPosOrderLog log = saveCommonLog(map);

    log.setEncCardNo(String.valueOf(map.get("enc_card_no")));//加密卡号
    log.setPartCardNo(String.valueOf(map.get("part_card_no"))); //部分卡号
    log.setAmount(Long.valueOf(String.valueOf(map.get("amount")))); //消费金额
    log.setCouponNo(String.valueOf(map.get("coupon_no")));//券号
    log.setOrderNo(String.valueOf(map.get("order_no"))); //自己的订单号
    log.setInterfaceType(interfaceType);
    orderLogRepository.save(log);
  }

  /**
   * 保存销账日志
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveLogAfterPay(Map map, Integer interfaceType) {
    UnionPosOrderLog log = saveCommonLog(map);

    log.setOrigReqSerialNo(String.valueOf(map.get("orig_req_serial_no")));//原流水号(上一步流水号)
    log.setEncCardNo(String.valueOf(map.get("enc_card_no")));//加密卡号
    log.setPartCardNo(String.valueOf(map.get("part_card_no"))); //部分卡号
    log.setAcqTermSn(String.valueOf(map.get("acq_term_sn")));//受理终端流水号
    log.setReferNo(String.valueOf(map.get("refer_no")));//检索参考号
    log.setSettDate(String.valueOf(map.get("sett_date")));//清算日期T
    log.setTxnDate(String.valueOf(map.get("txn_date")));//交易日期 YYYYMMDD
    log.setTxnTime(String.valueOf(map.get("txn_time")));//交易时间 HHMMSS
    log.setOrigAmt(Long.valueOf(String.valueOf(map.get("orig_amt")))); //原始金额
    log.setDiscountAmt(Long.valueOf(String.valueOf(map.get("discount_amt")))); //优惠金额
    log.setPayAmt(Long.valueOf(String.valueOf(map.get("pay_amt")))); //支付金额
    log.setPayMode(Integer.valueOf(String.valueOf(map.get("pay_mode")))); //支付方式
    log.setEquityNo(String.valueOf(map.get("equity_no")));//权益号
    log.setOrderNo(String.valueOf(map.get("order_no"))); //自己的订单号
    log.setInterfaceType(interfaceType);
    orderLogRepository.save(log);
  }

  /**
   * 保存冲正日志  16/11/23
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void saveLogReverse(Map map, Integer interfaceType) {
    UnionPosOrderLog log = saveCommonLog(map);

    log.setOrigReqSerialNo(String.valueOf(map.get("orig_req_serial_no")));//原流水号(上一步流水号)
    log.setInterfaceType(interfaceType);
    orderLogRepository.save(log);
  }

  private UnionPosOrderLog saveCommonLog(Map map) {
    UnionPosOrderLog log = new UnionPosOrderLog();
    log.setMsgTxnCode(String.valueOf(map.get("msg_txn_code")));//交易代码
    log.setMsgTime(String.valueOf(map.get("msg_time")));//报文日期
    log.setMsgSysSn(String.valueOf(map.get("msg_sys_sn")));//平台流水号
    log.setReqSerialNo(String.valueOf(map.get("req_serial_no")));//流水号
    log.setShopNo(String.valueOf(map.get("shop_no")));//门店号
    log.setTermNo(String.valueOf(map.get("term_no"))); //终端号
    log.setTransCrrltnNo(String.valueOf(map.get("trans_crrltn_no"))); //贯穿一笔交易(含撤销)的唯一流水号
    return log;
  }

}
