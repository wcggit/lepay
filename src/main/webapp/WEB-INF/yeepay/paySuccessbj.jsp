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
    <title></title>
    <link rel="stylesheet" type="text/css" href="${cdnUrl}lepay/weixin/success/reset.css">
    <link rel="stylesheet" href="${cdnUrl}lepay/weixin/success/paySuccess3.css">
    <script src="${cdnUrl}js/zepto.min.js"></script>
</head>
<body>
<div class="top">
    <span class="paySuccess-img"></span>
    <span class="paySuccess-ttl">付款成功</span>
</div>
<ul class="center">
    <li class="pay-num border-1px_bottom ordinry">
        <p class="clearfix"><span class="left">消费金额：</span><span
                class="right">¥${order.totalPrice/100.0}</span></p>

        <p>(微信支付¥${order.truePay/100.0},鼓励金支付¥${order.trueScore/100.0})</p>
    </li>
    <li class="confirm-code ordinry">
        <p class="clearfix"><span class="left">确认码：</span><span
                class="right">${order.lePayCode}</span></p>
    </li>
    <c:if test="${order.rebate!=0}">
        <li class="confirm-code ordinry">
            <p class="clearfix"><span class="left">鼓励金：</span><span
                    class="right">+ ¥${order.rebate/100.0}</span></p>
        </li>
    </c:if>
</ul>
<div class="hr-style"></div>
<div class="middle">
    <div class="icon"></div>
    <div class="desc">
        <p>金币账户余额:¥${scoreC.score/100.0}</p>

        <p>你已获得${order.scoreC/100.0}金币</p>
    </div>
    <div class="btn" onclick="window.location.href='http://www.lepluslife.com/front/gold/weixin'">兑换</div>
</div>
<div class="hr-style"></div>
<div class="bottom">
    <div class="ad">
        <img src="${policyResourceUrl}/images/paySuccess_noActivity/rechage_card_banner.png" alt="" onclick="window.location.href='http://www.lepluslife.com/front/order/weixin/recharge'">
    </div>
</div>

<script type="text/javascript">
    document.title = "${order.merchant.name}";

</script>
</body>
</html>