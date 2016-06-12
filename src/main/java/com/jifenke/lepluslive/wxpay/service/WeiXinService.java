package com.jifenke.lepluslive.wxpay.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jifenke.lepluslive.global.util.CookieUtils;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by wcg on 16/3/18.
 */
@Service
@Transactional(readOnly = true)
public class WeiXinService {

  @Value("${weixin.appSecret}")
  private String secret;

  @Value("${weixin.appId}")
  private String appid;

  @Value("${weixin.grantType}")
  private String grantType;

  @Inject
  private WeiXinUserService weiXinUserService;


  public Map<String, Object> getSnsAccessToken(String code) {
    String
        getUrl =
        "https://api.weixin.qq.com/sns/oauth2/access_token?secret=" + secret + "&appid=" + appid
        + "&code=" + code + "&grant_type=" + grantType;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(getUrl);
    httpGet.addHeader("Content-Type", "application/json");
    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(),"utf-8")), Map.class);
      EntityUtils.consume(entity);
      response.close();
      return map;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Map<String, Object> getDetailWeiXinUser(String accessToken,String openid) {
    String
        getUrl =
        "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken+"&openid="+openid;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(getUrl);
    httpGet.addHeader("Content-Type", "application/json;charset=utf8mb4");
    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> userDetail = mapper.readValue(new BufferedReader(new InputStreamReader(entity.getContent(),"utf-8")), Map.class);
      EntityUtils.consume(entity);
      response.close();
      return userDetail;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public WeiXinUser getCurrentWeiXinUser(HttpServletRequest request){
    String openId = CookieUtils.getCookieValue(request, appid + "-user-open-id");
  return  weiXinUserService.findWeiXinUserByOpenId(openId);
  }

}
