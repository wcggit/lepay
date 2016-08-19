package com.jifenke.lepluslive.pospay.domain;

/**
 * Created by wcg on 16/8/5.
 */
public class PosResult {

  private Integer code;

  private String message;

  private PosResultInfo data;




  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public PosResultInfo getData() {
    return data;
  }

  public void setData(PosResultInfo data) {
    this.data = data;
  }
}
