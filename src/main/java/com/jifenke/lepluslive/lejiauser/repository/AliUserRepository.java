package com.jifenke.lepluslive.lejiauser.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.AliUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wcg on 16/3/24.
 */
public interface AliUserRepository extends JpaRepository<AliUser, Long> {


  AliUser findByUserId(String userId);
}
