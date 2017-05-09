package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;
import com.jifenke.lepluslive.order.domain.entities.OrderStored;
import com.jifenke.lepluslive.order.domain.entities.PayWay;
import com.jifenke.lepluslive.score.domain.entities.ScoreD;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcg on 2017/4/13.
 */
@Service
public class StoredPayService {

  @Inject
  private MerchantService merchantService;

  /**
   * 纯储值支付
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paidByStored(String truePrice, Long merchantId,
                           String trueScore,
                           String totalPrice,String sumPrice,
                           LeJiaUser leJiaUser) {
    OffLineOrder offLineOrder = new OffLineOrder();
    Merchant merchant = merchantService.findMerchantById(merchantId);
    long truePay = Long.parseLong(truePrice);
    long total = Long.parseLong(totalPrice);
    long sum = Long.parseLong(sumPrice);
    long scoreA = Long.parseLong(trueScore);
    offLineOrder.setSumPrice(sum);
    offLineOrder.setLeJiaUser(leJiaUser);
    offLineOrder.setTotalPrice(total);
    offLineOrder.setTrueScore(scoreA);
    offLineOrder.setTruePay(truePay);
    offLineOrder.setCreatedDate(new Date());
    offLineOrder.setPayWay(new PayWay(5L));
    offLineOrder.setMerchant(merchant);
    offLineOrder.setRebateWay(1);

  }

  /**
   * 返给会员的金币与分润金额
   */
  public OrderStored rebateToUser(Long useStored, ScoreD scoreD) {
    OrderStored orderStored = new OrderStored();
    orderStored.setNumber(useStored);
    if (scoreD.getScore() > useStored) {
      Long score = scoreD.getScore(); //剩余储值
      double rate = useStored * 1.0 / score;
      orderStored
          .setScoreC((long) Math.floor((scoreD.getTotalScoreC() - scoreD.getScoreCToUser()) * rate)); //统一向下取整
      orderStored.setShareMoney(
          (long) Math.floor((scoreD.getTotalShareMoney() - scoreD.getSharedMoney()) * rate));
    } else if (scoreD.getScore() == useStored) {
      orderStored.setScoreC(scoreD.getTotalScoreC() - scoreD.getScoreCToUser());
      orderStored.setShareMoney(scoreD.getTotalShareMoney() - scoreD.getSharedMoney());
    } else {
      throw new RuntimeException("用户储值账户异常");
    }
    return orderStored;
  }

}
