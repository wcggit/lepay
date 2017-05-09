package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.merchant.controller.dto.MerchantDto;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantDetail;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantStoredActivity;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWallet;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWalletLog;
import com.jifenke.lepluslive.merchant.repository.MerchantDetailRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantStoredActivityRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantUserRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantWalletLogRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantWalletRepository;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * Created by wcg on 16/3/17.
 */
@Service
@Transactional(readOnly = true)
public class MerchantService {

  @Inject
  private MerchantRepository merchantRepository;

  @Inject
  private MerchantDetailRepository merchantDetailRepository;

  @Inject
  private MerchantUserRepository merchantUserRepository;

  @Inject
  private EntityManagerFactory entityManagerFactory;

  @Inject
  private MerchantWalletRepository merchantWalletRepository;

  @Inject
  private MerchantWalletLogRepository merchantWalletLogRepository;

  @Inject
  private MerchantStoredActivityRepository merchantStoredActivityRepository;

  /**
   * 根据商户账号名获取商户信息   2016/10/10
   *
   * @param name 账号名称
   */
  public MerchantUser findMerchantUserByName(String name) {
    Optional<MerchantUser> optional = merchantUserRepository.findByName(name);
    if (optional.isPresent()) {
      return optional.get();
    }
    return null;
  }

  /**
   * 获取商家详情
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<Merchant> findMerchantBySId(String sid) {
    return merchantRepository.findByMerchantSid(sid);
  }

  /**
   * 获取商家详情
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Merchant findMerchantById(Long id) {
    return merchantRepository.findOne(id);
  }

  /**
   * 获取商家轮播图
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<MerchantDetail> findAllMerchantDetailByMerchant(Merchant merchant) {
    return merchantDetailRepository.findAllByMerchant(merchant);
  }

  public MerchantWallet findMerchantWalletByMerchant(Merchant merchant) {
    return merchantWalletRepository.findByMerchant(merchant);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(OffLineOrder offLineOrder) {
    MerchantWallet
        merchantWallet =
        findMerchantWalletByMerchant(offLineOrder.getMerchant());
    merchantWallet.setTotalTransferMoney(
        merchantWallet.getTotalTransferMoney() + offLineOrder.getTransferMoney());
    merchantWalletRepository.save(merchantWallet);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<MerchantUser> findMerchantUserByMerchant(Merchant merchant) {
    return merchantUserRepository.findAllByMerchant(merchant);
  }

  /**
   * 获取商户账号信息 16/10/10
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public MerchantUser findMerchantUserById(Long merchantUserId) {
    return merchantUserRepository.findOne(merchantUserId);
  }

  /**
   * 修改商户登录账号密码 16/10/10
   *
   * @param merchantUser 商户账号
   * @param newPwd       新密码
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void resetPwd(MerchantUser merchantUser, String newPwd) {
    merchantUser.setPassword(MD5Util.MD5Encode(newPwd, "utf-8"));
    merchantUserRepository.save(merchantUser);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void shareToMerchant(long shareMoney, Merchant merchant, String orderSid, Long type) {
    if (shareMoney > 0) {
      MerchantWalletLog log = new MerchantWalletLog();

      MerchantWallet merchantWallet = findMerchantWalletByMerchant(merchant);

      Long availableBalance = merchantWallet.getAvailableBalance();

      log.setBeforeChangeMoney(availableBalance);
      long afterShareMoney = availableBalance + shareMoney;

      log.setAfterChangeMoney(afterShareMoney);

      log.setMerchantId(merchant.getId());

      log.setOrderSid(orderSid);

      log.setType(type);

      merchantWallet.setTotalMoney(merchantWallet.getTotalMoney() + shareMoney);

      merchantWallet.setAvailableBalance(afterShareMoney);

      merchantWalletLogRepository.save(log);

      merchantWalletRepository.save(merchantWallet);
    }


  }

  public MerchantUser findBossAccountByMerchant(Merchant merchant) {
    return merchantUserRepository.findByMerchantAndType(merchant, 1);
  }

  /**
   * 全红包支付后增加钱包金额  16/12/19
   *
   * @param merchant      商户
   * @param transferMoney 商户实际入账
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(Merchant merchant, Long transferMoney) {
    MerchantWallet
        merchantWallet =
        findMerchantWalletByMerchant(merchant);
    merchantWallet.setTotalTransferMoney(
        merchantWallet.getTotalTransferMoney() + transferMoney);
    merchantWalletRepository.save(merchantWallet);
  }

  /**
   * 查询某一商户下所有门店  2017/01/24
   *
   * @param merchantUser 商户
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<Merchant> countByMerchantUser(MerchantUser merchantUser) {
    return merchantRepository.findByMerchantUser(merchantUser);
  }

  /**
   * 查询某一账号下所有可管理的门店  2017/04/10
   *
   * @param merchantUserId 账户ID
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<Merchant> countByMerchantUser(Long merchantUserId) {
    return merchantRepository.findByMerchantUser(merchantUserId);
  }



  public MerchantStoredActivity findMerchantStoreActivity(MerchantUser merchantUser) {
    return merchantStoredActivityRepository.findByMerchantUser(merchantUser);
  }
}
