package com.jifenke.lepluslive.lejiauser.repository;

import com.jifenke.lepluslive.lejiauser.domain.entities.BankCard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * Created by wcg on 16/8/2.
 */
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

   Optional<BankCard> findByNumberAndState(String number,Integer state);

}
