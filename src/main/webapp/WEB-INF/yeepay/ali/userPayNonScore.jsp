<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 16/5/12
  Time: 下午2:28
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="/WEB-INF/commen.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <!--强制以webkit内核来渲染-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
    <!--按设备宽度缩放，并且用户不允许手动缩放-->
    <meta name="format-detection" content="telephone=no">
    <!--不显示拨号链接-->
    <title>富友支付</title>
    <link rel="stylesheet" href="${resourceUrl}/css/common.css">
    <link rel="stylesheet" href="${resourceUrl}/css/useAngPao.css">
</head>
<body>
<!--表单-->
<div id="form-monetary">
    <form action="">
        <p class="form-ttl">消费金额</p>

        <p class="form-name">￥<font>${totalPrice/100.0}</font>
        </p>


        <div class="form-btn">确认支付</div>
    </form>
</div>
<div class="conform">
    <ul>
        <li class="showOut"></li>
        <li><span class="cancel" id="pay-cancel">取消</span><span id="pay-confrim">确认</span>
        </li>
    </ul>
</div>
</body>
<script src="${resourceUrl}/js/jquery-2.0.3.min.js"></script>
<script src="${resourceUrl}/js/lphash.js"></script>
<script src="${resourceUrl}/js/MathContext.js"></script>
<script src="${resourceUrl}/js/BigDecimal.js"></script>
<script>
    document.title = "${merchant.name}";

        //    确认支付按钮
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');

            $(this).empty().text("正在支付,请稍后")
            pay();
        });

   // });
    function bindPay() {
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');

            $(this).empty().text("正在支付,请稍后")
            pay();
        });
    }

    function pay() {
        var totalPrice = ${totalPrice};
        var truePrice = ${totalPrice};
        var trueScore = 0;
        var ext = ljhash(truePrice + " " + trueScore
                         + " ${leJiaUser.userSid} ${merchantId} " + totalPrice, "lepluslife");
        $.post('/pay/yeepay/alipay/offLineOrderForUser', {
            ext: ext
        }, function (res) {
            location.href = res.payurl;
        });

    }



</script>
</html>
