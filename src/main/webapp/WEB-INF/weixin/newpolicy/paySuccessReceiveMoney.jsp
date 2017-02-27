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
  <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/css/paySuccess_receiveMoney.css">
  <script src="${policyResourceUrl}/framework/zepto.min.js"></script>
</head>
<body>
<div class="top">
  <span class="paySuccess-img"></span>
  <span class="paySuccess-ttl">付款成功</span>
</div>
<ul class="center">
  <li class="pay-num border-1px_bottom ordinry">
    <p class="clearfix"><span class="left">消费金额：</span><span class="right">¥${offLineOrder.totalPrice/100.0}</span></p>
  </li>
  <li class="confirm-code ordinry">
    <p class="clearfix"><span class="left">确认码：</span><span class="right">${offLineOrder.lepayCode}</span></p>
  </li>
</ul>
<div class="bottom">
  <div class="icon"></div>
  <div class="desc">
    <p>乐+生活 会员俱乐部</p>
    <p>你有一笔新鼓励金</p>
  </div>
  <div class="btn">领取</div>
</div>
<div class="modle">
  <div class="modle-wrapper clearfix">
    <div class="modle-main">
      <div class="modle-top">
        <p class="ttl">乐+生活会员<br>每次消费都获得鼓励金</p>
        <p  class="desc">
          <span><a class="text">每笔返</a></span>
          <span><a class="text">随时花</a></span>
          <span><a class="text">不清零</a></span>
        </p>
      </div>
      <div class="modle-bottom">
        <div class="input-wrapper">
          <input class="tel" type="text" placeholder="请输入您的手机号">
        </div>
        <div class="input-wrapper clearfix">
          <input class="code left" type="text" placeholder="请输入您的验证码">
          <span class="btn-getCode right">获取验证码</span>
        </div>
        <div class="input-wrapper">
          <input class="btn-register" type="button" value="立即注册">
        </div>
      </div>
    </div>
  </div>
  <div class="modle-close"></div>
</div>
<script type="text/javascript">
  $('.btn').on('touchstart', function () {
    $('.modle').css({'display':'block'});
  })
  $('.modle-close').on('touchstart', function () {
    $('.modle').css({'display':'none'});
  })
  $('.tel').on('input',function () {
    isEmpty();
  })
  $('.code').on('input',function () {
    isEmpty();
  })
  //        获取验证码
  $('.btn-getCode').on('touchstart',function () {
    var time=60;
    var timer=setInterval(function () {
      $('.btn-getCode').empty().text(time+'s后重新获取').css('color','#B4B4B4');
      time--;
      if(time==-1){
        clearInterval(timer);
        $('.btn-getCode').empty().text('重新获取').css('color','#22A636');
      }
    },1000)
  })
  //
  $('.btn-register').on('touchstart',function () {
    if($('.tel').val()!='' && $('.code').val()!=''){
      clickFun();
    }else {
      return;
    }
  })
  //        判断两个输入框是否都已经填写
  function isEmpty() {
    if($('.tel').val()!=''&&$('.code').val()!=''){
      $('.btn-register').css('background','#22A636');
    }else {
      return;
    }
  }
  //        click事件
  function clickFun() {
    alert('在这里写click事件')
  }

</script>
</body>
</html>