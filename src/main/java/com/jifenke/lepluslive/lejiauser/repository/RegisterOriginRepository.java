package com.jifenke.lepluslive.lejiauser.repository;


import com.jifenke.lepluslive.lejiauser.domain.entities.RegisterOrigin;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wcg on 16/3/24.
 */
public interface RegisterOriginRepository extends JpaRepository<RegisterOrigin,Long>{

  RegisterOrigin findByMerchantAndOriginType(Merchant merchant, Integer originType);
}
