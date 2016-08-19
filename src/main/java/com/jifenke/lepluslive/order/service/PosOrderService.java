package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.merchant.domain.entities.MerchantPos;
import com.jifenke.lepluslive.merchant.service.MerchantPosService;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.order.domain.entities.PosOrder;
import com.jifenke.lepluslive.order.domain.entities.PosOrderLog;
import com.jifenke.lepluslive.order.repository.PosOrderLogRepository;
import com.jifenke.lepluslive.order.repository.PosOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/4.
 */
@Service
public class PosOrderService {

  @Inject
  private PosOrderRepository posOrderRepository;

  @Inject
  private MerchantPosService merchantPosService;

  @Inject
  private PosOrderLogRepository posOrderLogRepository;


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderForNoNMember(String posId, String orderNo,
                                         String orderTime, String orderPrice) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    PosOrder posOrder = new PosOrder();
    posOrder.setMerchantPos(merchantPos);
    try {
      posOrder.setCreatedDate(sdf.parse(orderTime));
      posOrder.setOrderSid(orderNo);
      long price = new BigDecimal(orderPrice).multiply(new BigDecimal(100)).longValue();
      posOrder.setTotalPrice(price);
      posOrder.setRebateWay(0);
      posOrder.setTruePay(price);
      posOrderRepository.save(posOrder);
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public void nonMemberAfterPay(String posId, String orderNo, String paidTime, String orderPrice,
                                String paidPoints, String paidMoney, Integer tradeFlag) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    PosOrder posOrder = posOrderRepository.findByOrderSid(orderNo);
    MerchantPos merchantPos = merchantPosService.findMerchantPosByPosId(posId);
    BigDecimal ljCommission;
    try {
      posOrder.setCompleteDate(sdf.parse(paidTime));

      if (tradeFlag == 0) {//支付宝
        ljCommission =
            merchantPos.getAliCommission().multiply(new BigDecimal(paidMoney));
        posOrder.setPayWay(new PayWay(3L));
      } else if (tradeFlag == 3) { //刷卡
        posOrder.setPayWay(new PayWay(4L));
        ljCommission =
            merchantPos.getPosCommission().multiply(new BigDecimal(paidMoney));
        if (merchantPos.getType() == 1) {//封顶pos
          if (ljCommission.longValue() >= merchantPos.getCeil()) {
            ljCommission = new BigDecimal(merchantPos.getCeil());
          }
        }
      } else if (tradeFlag == 4) { //微信
        posOrder.setPayWay(new PayWay(1L));
        ljCommission =
            merchantPos.getWxCommission().multiply(new BigDecimal(paidMoney));
      } else { //现金
        posOrder.setPayWay(new PayWay(5L));
        ljCommission = new BigDecimal(0);
      }
      posOrder.setLjCommission(ljCommission.longValue());
      posOrder.setTransferMoney(new BigDecimal(paidMoney).multiply(new BigDecimal(100))
                                    .subtract(ljCommission).longValue());
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderLog(String orderSid, String params) {
    PosOrderLog posOrderlog = new PosOrderLog();
    posOrderlog.setOrderSid(orderSid);
    posOrderlog.setParams(params);
    posOrderLogRepository.save(posOrderlog);
  }


}
