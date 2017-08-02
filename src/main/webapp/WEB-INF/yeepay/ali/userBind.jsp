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
  <link rel="stylesheet" href="${cdnUrl}lepay/css/reset.css">
  <link rel="stylesheet" href="${cdnUrl}lepay/css/keyboard.css">
  <link rel="stylesheet" href="${cdnUrl}lepay/alipay/zhuce.css">
  <script src="${cdnUrl}js/lphash.js"></script>
  <script src="${cdnUrl}js/zepto.min.js"></script>
</head>
<body>
<div class="logo"></div>
<ul class="paySuccess">
  <li class="info">
    <span class="name"></span><input id="phone" type="text" placeholder="请输入您在注册时绑定的手机号"/>
  </li>
  <li class="info">
    <span class="passWord"></span><input type="password" placeholder="请输入验证码"/>
    <div class="btn-yanzhengma">获取验证码</div>
  </li>
</ul>
<h3 class="errTitle">*您的手机号还没有注册成为乐+会员</h3>
<!--<h3 class="errTitle">*验证码错误</h3>-->
<!--<h3 class="errTitle">*请输入正确手机号</h3>-->
<div class="form-btn">身份验证</div>

<script>
  var time=60;
  var flag = true;
  function phoneValidate(n){
    return n.match(/\d/g).length===11;
  }

  function bindUser(code){
    $.post("/pay/yeepay/alipay/bind_confirm", {
      code: code,
      phone: $("#phone").val(),
      userId:${userId}
    }, function (res) {
      if(res.status!=200){
        errShow(res.msg)
        $(".form-btn").on("touchstart",function(){
          var code = $(".btn-yanzhengma").val()
          if(code!=null){
            $(".form-btn").unbind("touchstart")
            bindUser(code)
          }else{
            errShow("请输入验证码")
          }
        })
      }else{
        var ext = ljhash(res.data +
                          " ${totalPrice} ${merchantId}", "lepluslife");
        location.href = "/pay/yeepay/alipay/userpay?ext=" + ext;
      }
    });
  }

  $(".form-btn").on("touchstart",function(){
    var code = $(".btn-yanzhengma").val()
    if(code!=null){
      $(".form-btn").unbind("touchstart")
      bindUser(code)
    }else{
      errShow("请输入验证码")
    }
  })
  $(".btn-yanzhengma").on("touchstart", function () {
    if($("#phone").val()!=null){
      if(phoneValidate($("#phone").val())){
        if(flag){
          console.log(1)
          flag=false

          $.post("http://www.lepluslife.com/code/sign/send", {
            phoneNumber: $("#phone").val(),
            type: 3,
            source: 'WEB',
            pageSid: '${pageSid}'
          }, function (res) {
            if (res.status != 200) {
              alert(res.msg);
            }
          });

          if ($(this).text() == "获取验证码") {
            $(this).text(time + "s后重新获取");
            var a = setInterval(function () {
              time--;
              $(".btn-yanzhengma").text(time + "s后重新获取");
              if (time == 0) {
                $(".btn-yanzhengma").text("获取验证码");
                time = 60;
                flag=true
                clearInterval(a);
              }
            }, 1000);
          }
        }
      }else{
        errShow("*输入正确的手机号");
      }
    }

  });
  function errShow(s) {
    $(".errTitle").html(s);
    $(".errTitle").css({"display": "block"});
    setTimeout(function () {
      $(".errTitle").css({"opacity": 1});
    }, 1);
    setTimeout(function () {
      errHide();
    }, 5000)
  }
  function errHide() {
    $(".errTitle").css({"opacity": 0});
    setTimeout(function () {
      $(".errTitle").css({"display": "none"});
    }, 500);
  }
</script>
</body>
</html>