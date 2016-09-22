package com.jifenke.lepluslive.activity.domain.criteria;

/**
 * Created by wcg on 16/9/12.
 */
public class RebateActivityCriteria {

  private Integer offset;

  private String merchant;

  private Integer state; //活动状态

  private Integer bindWxState; //0 未绑定店主公众号 1 绑定店主公众号

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public String getMerchant() {
    return merchant;
  }

  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Integer getBindWxState() {
    return bindWxState;
  }

  public void setBindWxState(Integer bindWxState) {
    this.bindWxState = bindWxState;
  }
}
