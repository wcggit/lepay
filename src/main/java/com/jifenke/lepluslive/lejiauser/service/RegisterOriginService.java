package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.RegisterOrigin;
import com.jifenke.lepluslive.lejiauser.repository.RegisterOriginRepository;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 用户注册来源 Created by zhangwen on 2017/2/27.
 */
@Service
@Transactional(readOnly = true)
public class RegisterOriginService {

  @Inject
  private RegisterOriginRepository repository;

  /**
   * 查找对应的注册来源|没有就创建一个  2017/02/27
   *
   * @param merchant   商户
   * @param originType 类型 0=微信注册|1=app注册2=商户注册|3=线下支付完成页
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public RegisterOrigin findAndSaveByMerchantAndOriginType(Merchant merchant, Integer originType) {

    RegisterOrigin origin = repository.findByMerchantAndOriginType(merchant, originType);
    if (origin == null) {
      origin = new RegisterOrigin();
      origin.setMerchant(merchant);
      origin.setOriginType(3);
      repository.save(origin);
    }
    return origin;
  }

}
