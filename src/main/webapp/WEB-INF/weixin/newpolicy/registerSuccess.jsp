<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 2017/2/24
  Time: 下午2:45
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
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/framework/reset.css">
    <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/css/registerSuccess.css">
    <script src="${policyResourceUrl}/framework/zepto.min.js"></script>
</head>
<body>
<div class="top">
    <div class="paySuccess-img"></div>
    <p class="paySuccess-ttl">注册成功</p>

    <p class="over">本次消费获得鼓励金：¥<fmt:formatNumber type="number"
                                                 value="${backA/100}"
                                                 pattern="0.00"
                                                 maxFractionDigits="2"/></p>

    <p class="paySuccess-desc">以后在乐+活动商家消费，每笔都能获得鼓励金</p>
</div>
<div class="bottom">
    <p class="bottom-ttl">请认准乐+支付扫码牌</p>

    <div class="img">
        <img src="${policyResourceUrl}/images/paySuccess/smp.png" alt="">
    </div>
</div>
</body>
</html>
