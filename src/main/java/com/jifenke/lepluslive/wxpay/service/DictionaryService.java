package com.jifenke.lepluslive.wxpay.service;


import com.jifenke.lepluslive.wxpay.domain.entities.Dictionary;
import com.jifenke.lepluslive.wxpay.repository.DictionaryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by zhangwen on 2016/5/25.
 */
@Service
@Transactional(readOnly = true)
public class DictionaryService {

  @Inject
  private DictionaryRepository dictionaryRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Dictionary findDictionaryById(Long id) {

    return dictionaryRepository.findOne(id);
  }

}
