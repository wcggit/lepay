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
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><!--强制以webkit内核来渲染-->
  <meta name="viewport"
        content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
  <!--按设备宽度缩放，并且用户不允许手动缩放-->
  <meta name="format-detection" content="telephone=no"><!--不显示拨号链接-->
  <title></title>
  <link rel="stylesheet" href="${resourceUrl}/css/reset.css">
  <link rel="stylesheet" href="${resourceUrl}/css/keyboard.css">
  <link rel="stylesheet" href="${resourceUrl}/alipay/confirm_nomember.css">
  <script src="${policyResourceUrl}/framework/zepto.min.js"></script>
  <script src="${resourceUrl}/js/lphash.js"></script>
  <script src="${resourceUrl}/js/jquery-2.0.3.min.js"></script>
  <script src="${resourceUrl}/js/MathContext.js"></script>
  <script src="${resourceUrl}/js/BigDecimal.js"></script>
</head>
<body>
<!--表单-->
<div class="logo">${merchant.name}</div>
<ul class="paySuccess">
  <li class="info">
    <span>乐＋会员</span>
    <div class="btn-usegulijin" id="user-bind" >使用鼓励金</div>
  </li>
  <li class="info">
    <span>非乐＋会员</span>
    <h3>￥<span>${totalPrice}</span></h3>
  </li>
</ul>
<div class="form-btn" id="confirm-pay" >确认支付</div>
</div>
<!--键盘-->
<div id="keyboard">
  <span>1</span><span>2</span><span>3</span><span>4</span><span>5</span><span>6</span>
  <span>7</span><span>8</span><span>9</span><span>.</span><span>0</span><span><i></i></span>
</div>

</body>
<script>
  document.title = "${merchant.name}";
  function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
  }

  var price = ${totalPrice};
  $('#confirm-pay').on('touchstart', function () {
    if(isNumber(price)){
      if (price==0 ) {
        $('.conform').css({'display': 'block'});
        $('.conform .confirm ').on('touchstart', function () {
          $('.conform').css({'display': 'none'});
        })
        $('.conform .cancel ').on('touchstart', function () {
          $('.conform').css({'display': 'none'});
        })
        return;
      }
      $('#confirm-pay').unbind('touchstart');
      $(this).empty().text("正在支付,请稍后");
      var url = '/pay/yeepay/alipay/offLineOrder';
      $.get(url, {
        truePrice: ${totalPrice},
        merchantId: ${merchant.id},
        userId: "${userId}"
      }, function (res) {
        location.href = res.payurl;
      });
    }else{
      alert("输入正确的金额")
    }

  });

  $('#user-bind').on('touchstart', function () {
    var ext = ljhash("${userId} ${merchant.id} ${totalPrice}", "lepluslife");
    location.href = "/pay/yeepay/alipay/bind?ext="+ext;
  })


</script>
</html>