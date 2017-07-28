package com.jifenke.lepluslive.lejiauser.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.Verify;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhangwen on 2016/4/25.
 */
public interface VerifyRepository extends JpaRepository<Verify, Long> {

  Verify findByPageSid(String pageSid);

  Integer countByUserIdAndState(Long userId, Integer state);

}
