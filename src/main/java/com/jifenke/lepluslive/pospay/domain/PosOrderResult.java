package com.jifenke.lepluslive.pospay.domain;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wcg on 16/8/5.
 */
public class PosOrderResult {

  private Integer code;

  private String message;

  private Map data;

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

  public Map getData() {
    return data;
  }

  public void setData(Map data) {
    this.data = data;
  }
}
