package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.ValidateCode;
import com.jifenke.lepluslive.lejiauser.repository.ValidateCodeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by zhangwen on 2016/4/25.
 */
@Service
@Transactional(readOnly = true)
public class ValidateCodeService {

  @Inject
  private ValidateCodeRepository validateCodeRepository;

  /**
   * 判断验证码是否正确
   *
   * @param phoneNumber 手机号码
   * @param code        验证码
   * @return true=验证码正确  false=验证码错误
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Boolean findByPhoneNumberAndCode(String phoneNumber, String code) {
    ValidateCode
        validateCode =
        validateCodeRepository.findByPhoneNumberAndCodeAndStatus(phoneNumber, code, 0);
    return validateCode != null;
  }


}
