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
  <link rel="stylesheet" href="${resourceUrl}/css/pay.css">
  <script src="${policyResourceUrl}/framework/zepto.min.js"></script>
  <script src="${resourceUrl}/js/lphash.js"></script>
  <script src="${resourceUrl}/js/jquery-2.0.3.min.js"></script>
  <script src="${resourceUrl}/js/MathContext.js"></script>
  <script src="${resourceUrl}/js/BigDecimal.js"></script>
</head>
<body>
<!--表单-->
<div id="form-monetary">
  <form action="">
    <p class="form-ttl">${merchant.name}</p>
    <label for="monetary">
      消费金额（元）
      <input type="text" id="monetary" placeholder="问问收银员应付多少钱？" readonly>
      <span class="close"></span>
    </label>
    <div class="form-btn" id="confirm-pay" >确认支付</div>
  </form>
</div>
<!--键盘-->
<div id="keyboard">
  <span>1</span><span>2</span><span>3</span><span>4</span><span>5</span><span>6</span>
  <span>7</span><span>8</span><span>9</span><span>.</span><span>0</span><span><i></i></span>
</div>
<!--弹层-->
<div class="conform">
  <ul>
    <li class="showOut">请输入金额</li>
    <li><span class="cancel">取消</span><span class="confirm">知道了</span></li>
  </ul>
</div>
</body>
<script>
  document.title = "${merchant.name}";
  $(function () {
    fontFUn();
    var spans = $('#keyboard span:not(:last-child)');
    spans.each(function (i) {
      spans.eq(i).on("touchstart", function () {
        var getValue = $(this).text();
        //控制第一个不能输入小数点"."
        if ($("#monetary").val().length == 1 && $("#monetary").val() == 0
            && getValue.indexOf('.') != 0) {
          return;
        }
        if ($("#monetary").val().length == 0
            && getValue.indexOf('.') != -1) {
          return;
        }
        //控制只能输入一个小数点"."
        if ($("#monetary").val().indexOf('.') != -1 && getValue.indexOf('.') != -1) {
          return;
        }
        var str = $("#monetary").val() + $(this).text();
        var strNum = $("#monetary").val().toString().indexOf('.');
        $("#monetary").val(str.replace(/^(.*\..{2}).*$/, "$1"));
        fontFUn();
      })
    });
    $('#keyboard span:last-child').eq(0).on('touchstart', function () {
      var str = $("#monetary").val();
      $("#monetary").val($("#monetary").val().substring(0, str.length - 1));
      fontFUn();
    });
    $(".close").eq(0).on("touchstart", function () {
      $("#monetary").val("");
      fontFUn();
    })
  });


  //    判断所输入的值
  function fontFUn() {
    if ($("#monetary").val() == '' || $("#monetary").val() == null) {
      $("#monetary").css({'font-size': '3.2vw'});
      $(".close").css({'display': 'none'});
    } else {
      $("#monetary").css({'font-size': '4.8vw', "color": "#FB991A"});
      $(".close").css({'display': 'block'});
    }
  }
  //强制保留两位小数
  function toDecimal(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return false;
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  }

  $('#confirm-pay').on('touchstart', function () {
    if ($('#monetary').val() == 0) {
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
    pay();
  });

  function pay() {
    if ($('#monetary').val() == 0) {
      $('.conform').css({'display': 'block'});
      $('.conform .cancel').on('touchstart', function () {
        $('.conform').css({'display': 'none'});
      })
      $('.conform .confirm').on('touchstart', function () {
        $('.conform').css({'display': 'none'});
      })
      return;
    }
    var totalPrice = $("#monetary").val();
    var ext = ljhash("${userId} ${merchant.id} " + totalPrice, "lepluslife");
    location.href = "/pay/alipay/pay?ext=" + ext;
  }
</script>
</html>