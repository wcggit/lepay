package com.jifenke.lepluslive.fuyou.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuiou.mpay.encrypt.RSAUtils;
import com.jifenke.lepluslive.fuyou.service.FuYouPayService;
import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.Des;
import com.jifenke.lepluslive.global.util.HttpClientUtil;
import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MapUtil;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.lejiauser.domain.entities.AliUser;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.service.AliUserService;
import com.jifenke.lepluslive.lejiauser.service.LeJiaUserService;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.service.MerchantScanPayWayService;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.order.domain.entities.ScanCodeOrder;
import com.jifenke.lepluslive.order.service.ScanCodeOrderService;
import com.jifenke.lepluslive.score.service.ScoreAService;
import com.jifenke.lepluslive.score.service.ScoreCService;
import com.jifenke.lepluslive.wxpay.domain.entities.WeiXinUser;
import com.jifenke.lepluslive.wxpay.service.WeiXinPayService;
import com.jifenke.lepluslive.wxpay.service.WeiXinUserService;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* 富友扫码 Created by zhangwen on 2016/12/6.
*/
@RestController
@RequestMapping("/pay/alipay")
public class ScanCodeOrderAliPayController {

  private static Logger log = LoggerFactory.getLogger(ScanCodeOrderAliPayController.class);

  @Inject
  private String private_ali;

  @Inject
  private String public_ali;

  @Inject
  private AliUserService aliUserService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private ScanCodeOrderService scanCodeOrderService;

  @Inject
  private WeiXinUserService weiXinUserService;

  @Inject
  private MerchantScanPayWayService scanPayWayService;

  @Inject
  private ScoreAService scoreAService;

  @RequestMapping("/userToken")
  public ModelAndView goPayPage(@RequestParam String auth_code, @RequestParam String merchantSid,
                                HttpServletRequest request,
                                Model model) {
    Optional<Merchant> merchantBySId = merchantService.findMerchantBySId(merchantSid);
    model.addAttribute("merchant", merchantBySId.get());
    AlipayClient
        alipayClient =
        new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", Constants.ALIAPPID,
                                private_ali, "json", "utf-8", public_ali, "RSA2");
    AlipaySystemOauthTokenRequest
        alipaySystemOauthTokenRequest =
        new AlipaySystemOauthTokenRequest();
    alipaySystemOauthTokenRequest.setGrantType("authorization_code");
    alipaySystemOauthTokenRequest.setCode(auth_code);
    try {
      Merchant merchant = merchantBySId.get();
      AlipaySystemOauthTokenResponse execute = alipayClient.execute(alipaySystemOauthTokenRequest);
      if (execute.isSuccess()) {
        AliUser aliUser = aliUserService.findUserById(execute.getUserId());
        if (merchantBySId.isPresent()) {
          int way = scanPayWayService.findByMerchantId(merchant.getId());
          model.addAttribute("userId", execute.getUserId());
          if(way==3){
            if(aliUser!=null){
              model.addAttribute("aliUser",aliUser);
              model.addAttribute("scorea",scoreAService.findScoreAByLeJiaUser(aliUser.getLeJiaUser()));
            }
            model.addAttribute("merchant",merchant);
            return MvUtil.go("/yeepay/ali/aliPay");
          }
        } else {
          return null;
        }
      }
    } catch (AlipayApiException e) {
      e.printStackTrace();
    }
    return null;
  }

  @RequestMapping("/pay")
  public ModelAndView pay(@RequestParam(required = true) String ext, HttpServletResponse response,HttpServletRequest request) {
    String results = Des.strDec(ext, "lepluslife", null, null);
    String[] strs = results.split(" ");
    ScanCodeOrder
        order =
        scanCodeOrderService
            .createOrderForNoNMember(strs[2], Long.parseLong(strs[1]), weiXinUserService
                .findWeiXinUserByOpenId("doubles2"), 0, 0, strs[0]);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    SortedMap<String, Object> params = new TreeMap<>();
    params.put("addn_inf", "消费"); //附加数据
    params.put("curr_type", "CNY");
    params.put("goods_des", order.getMerchant().getName()+"消费"); //商品描述
    params.put("goods_detail", order.getMerchant().getName()+"消费");//商品详情, 商品名称明细
    params.put("goods_tag", "");//商品标记
    params.put("ins_cd", Constants.FUYOU_INS_CD);//机构号
    params.put("mchnt_cd", order.getScanCodeOrderExt().getMerchantNum());//商户号
    params.put("mchnt_order_no", order.getOrderSid());
    params.put("notify_url", Constants.WEI_XIN_ROOT_URL + "/pay/wxpay/afterPay");
    params.put("order_amt", order.getTotalPrice());//总金额, 订单总金额，单位为分
    params.put("order_type", "ALIPAY");//
    params.put("random_str", MvUtil.getRandomStr(32));//随机字符串
    params.put("term_id", MvUtil.getRandomStr(8));//终端IP
    params.put("term_ip", getIpAddr(request));//终端IP
    params.put("txn_begin_ts", sdf.format(order.getCreatedDate())); //交易起始时间,格式为yyyyMMddHHmmss
    params.put("version", "1.0");

    String srcSign = MapUtil.mapBlankJoin(params, false, false);

    try {
      String sign = RSAUtils.sign(srcSign.getBytes("GBK"), Constants.FUYOU_PRI_KEY);
      params.put("sign", sign);
      params.put("reserved_limit_pay", "no_credit");//随机字符串
      StringBuffer paramBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?><xml>");
      MapUtil.mapToXML(params, paramBuffer);
      paramBuffer.append("</xml>");
      Map<String, String> content = new HashMap<>();
      content.put("req", URLEncoder.encode(paramBuffer.toString(), "GBK"));
      String result =
          HttpClientUtil.post(Constants.FUYOU_ALiPAY_URL, content, "GBK");
      result = new URLDecoder().decode(result, "GBK");
      Map<String, String> map = MapUtil.xmlStringToMap(result);
      System.out.println(map);
      response.sendRedirect(map.get("qr_code"));
    } catch (Exception e) {

    }
    return null;
  }


  private String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip != null && ip.length() > 15) {
      ip = ip.split(",")[0];
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
  }


}
