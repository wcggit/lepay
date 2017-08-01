package com.jifenke.lepluslive.yibao.service;

import com.jifenke.lepluslive.yibao.domain.entities.LedgerTransferLog;
import com.jifenke.lepluslive.yibao.repository.LedgerTransferLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by zhangwen on 2017/7/21.
 */
@Service
@Transactional(readOnly = true)
public class LedgerTransferLogService {

  @Inject
  private LedgerTransferLogRepository repository;

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveLog(LedgerTransferLog log) {
    repository.save(log);
  }

}
