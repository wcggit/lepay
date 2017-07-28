package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.lejiauser.domain.entities.Verify;
import com.jifenke.lepluslive.lejiauser.repository.VerifyRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by zhangwen on 2017/6/29.
 */
@Service
@Transactional(readOnly = true)
public class VerifyService {

  @Inject
  private VerifyRepository repository;

  public Verify findByPageSidAndUserId(String pageSid, Long userId) {

    Verify verify = repository.findByPageSid(pageSid);
    if (verify != null && verify.getState() == 0 && verify.getUserId().equals(userId)) {
      return verify;
    }
    return null;
  }

}
