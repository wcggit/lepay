<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 2017/2/24
  Time: 上午10:09
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/commen.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
  <title>Title</title>
  <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/framework/reset.css">
  <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/css/paySuccess_noActivity.css">
</head>
<body>
<div class="top">
  <span class="paySuccess-img"></span>
  <span class="paySuccess-ttl">付款成功</span>
</div>
<ul class="center">
  <li class="pay-num border-1px_bottom ordinry">
    <p class="clearfix"><span class="left">消费金额：</span><span class="right">¥${offLineOrder.totalPrice/100.0}</span></p>
    <p>(微信支付¥${offLineOrder.truePay/100.0}，鼓励金支付¥${offLineOrder.trueScore/100.0})</p>
  </li>
  <li class="confirm-code ordinry">
    <p class="clearfix"><span class="left">确认码：</span><span class="right">${offLineOrder.lepayCode}</span></p>
  </li>
  <li class="point">
    <span>【${offLineOrder.merchant.name}】</span>未参与乐+活动，本笔未获得鼓励金和金币
    可进入“乐加生活”公众号，查询附近活动门店。
  </li>
</ul>
<div class="hr-style"></div>
<div class="bottom">
  <div class="ad">
    <img src="${policyResourceUrl}/images/paySuccess_noActivity/rechage_card_banner.png" alt="">
  </div>
</div>

</body>
</html>