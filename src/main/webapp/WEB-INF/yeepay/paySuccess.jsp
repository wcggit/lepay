<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 16/5/16
  Time: 上午10:41
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/commen.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <!--强制以webkit内核来渲染-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
    <!--按设备宽度缩放，并且用户不允许手动缩放-->
    <meta name="format-detection" content="telephone=no">
    <!--不显示拨号链接-->
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" href="${cdnUrl}lepay/weixin/success/paySuccess.css">
</head>
<body>
<ul id="paySuccess">
    <li>
        <span class="logo-yes"></span>
        支付成功
    </li>
    <li class="info">
        <span class="left">消费金额</span>
        <span class="right red border">￥${order.totalPrice/100}</span>
        <c:if test="${order.truePay==0}">
            <p>红包支付 ¥${order.trueScore/100}</p>
        </c:if>
        <c:if test="${order.trueScore==0}">
            <p> 微信支付 ￥${order.truePay/100}</p>
        </c:if>
        <c:if test="${order.trueScore!=0&&order.truePay!=0}">
            <p>红包支付 ¥${order.trueScore/100} + 微信支付 ￥${order.truePay/100}</p>
        </c:if>
    </li>
    <li class="info">
        <span class="left">消费门店</span>
        <span class="right">${order.merchant.name}</span>
    </li>
    <li class="info">
        <span class="left">乐付确认码</span>
        <span class="right">${order.lePayCode}</span>
    </li>
    <li class="info">
        <span class="left">支付单号</span>
        <span class="right">${order.orderSid}</span>
    </li>
    <li class="info">
        <span class="left">时间</span>
        <span class="right"><fmt:formatDate
                value="${order.createdDate}" type="both"/></span>
    </li>
</ul>
<p class="ttl">恭喜您获得￥${order.rebate/100+order.scoreB}大礼包！</p>
<c:if test="${order.rebate!=0}">
    <div class="list">
        <div class="left hongbao">
            <p>红包</p>

            <p>￥${order.rebate/100}</p>

            <p>待使用</p>
        </div>
        <div class="right">
            <p>乐＋生活</p>

            <p>在所有乐＋商户中均可使用</p>
        </div>
    </div>
</c:if>
<div class="list">
    <div class="left jifen">
        <p>积分</p>

        <p>￥${order.scoreB}</p>

        <p>待使用</p>
    </div>
    <div class="right">
        <p>乐＋生活</p>

        <p>可在乐＋商城中消费使用</p>

        <p class="btn"><span class="right-btn" onclick="goLePlusLife()">立即使用<font>></font></span>
        </p>
    </div>
</div>
</body>
<script>
    document.title = "乐加支付";
    function goLePlusLife() {
        location.href = "http://www.lepluslife.com/weixin/shop"
    }
</script>
</html>
