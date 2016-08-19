package com.jifenke.lepluslive.order.service;

import com.jifenke.lepluslive.order.domain.entities.UnionPosOrderLog;
import com.jifenke.lepluslive.order.repository.UnionPosOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/8/9.
 */
@Service
public class UnionPosOrderLogService {

  @Inject
  private UnionPosOrderRepository unionPosOrderRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void createPosOrderLog(String msgSysSn, String params,Integer interfaceType,String reqSerialNo) {
    UnionPosOrderLog unionPosOrderLog = new UnionPosOrderLog();
    unionPosOrderLog.setInterfaceType(interfaceType);
    unionPosOrderLog.setParams(params);
    unionPosOrderLog.setReturnParams(params);
    unionPosOrderRepository.save(unionPosOrderLog);
  }


}
