package com.jifenke.lepluslive.merchant.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 门店对应的富友结算规则 Created by zhangwen on 16/11/25.
 */
@Entity
@Table(name = "MERCHANT_SETTLEMENT_STORE")
public class MerchantSettlementStore {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Integer type = 0;  //签约类型  0=普通协议|1=联盟协议  废弃不用 ，使用partnership

  @Column(nullable = false)
  private Long commonSettlementId;  //普通协议ID=MerchantSettlement.id

  @Column(nullable = false)
  private Long allianceSettlementId;  //联盟协议ID=MerchantSettlement.id

  @Column(nullable = false, unique = true)
  private Long merchantId;  //门店ID=Merchant.id

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getCommonSettlementId() {
    return commonSettlementId;
  }

  public void setCommonSettlementId(Long commonSettlementId) {
    this.commonSettlementId = commonSettlementId;
  }

  public Long getAllianceSettlementId() {
    return allianceSettlementId;
  }

  public void setAllianceSettlementId(Long allianceSettlementId) {
    this.allianceSettlementId = allianceSettlementId;
  }

  public Long getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(Long merchantId) {
    this.merchantId = merchantId;
  }
}
