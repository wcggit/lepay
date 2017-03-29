package com.jifenke.lepluslive.merchant.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by sunxingfei on 2017/2/15.
 */
@Entity
@Table(name = "TEMPORARY_MERCHANT_USER_SHOP")
public class TemporaryMerchantUserShop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Merchant merchant;

    @OneToOne
    private MerchantUser merchantUser;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public MerchantUser getMerchantUser() {
        return merchantUser;
    }

    public void setMerchantUser(MerchantUser merchantUser) {
        this.merchantUser = merchantUser;
    }
}
