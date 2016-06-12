package com.jifenke.lepluslive.wxpay.controller.dto;

/**
 * Created by wcg on 16/3/20.
 */
public class OrderDto {

  private Long productId;
  private int productNum;
  private Long totalPrice;
  private Long totalScore;
  private Long productSpec;



  public Long getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(Long productSpec) {
    this.productSpec = productSpec;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public int getProductNum() {
    return productNum;
  }

  public void setProductNum(int productNum) {
    this.productNum = productNum;
  }

  public Long getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Long getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Long totalScore) {
    this.totalScore = totalScore;
  }
}
