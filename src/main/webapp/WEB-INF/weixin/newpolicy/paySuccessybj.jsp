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
    <link rel="stylesheet" type="text/css" href="${policyResourceUrl}/css/paySuccess.css">
    <script src="${policyResourceUrl}/framework/zepto.min.js"></script>
</head>
<body>
<div class="top">
    <span class="paySuccess-img"></span>
    <span class="paySuccess-ttl">付款成功</span>
</div>
<ul class="center">
    <li class="pay-num border-1px_bottom ordinry">
        <p class="clearfix"><span class="left">消费金额：</span><span class="right">¥68.34</span></p>

        <p>(微信支付¥40.00，鼓励金支付¥28.34)</p>
    </li>
    <li class="confirm-code ordinry">
        <p class="clearfix"><span class="left">确认码：</span><span class="right">5423</span></p>
    </li>
    <li class="confirm-code ordinry">
        <p class="clearfix"><span class="left">鼓励金：<a class="rule">活动规则</a></span><span
                class="right">+ ¥2.5</span></p>
    </li>
</ul>
<div class="hr-style"></div>
<div class="middle">
    <div class="icon"></div>
    <div class="desc">
        <p>金币账户余:¥14.55</p>

        <p>你已获得10金币</p>
    </div>
    <div class="btn">兑换</div>
</div>
<div class="hr-style"></div>
<div class="bottom">
    <div class="ad">
        <img src="${policyResourceUrl}/images/paySuccess_noActivity/rechage_card_banner.png" alt="">
    </div>
</div>
<div class="modle">
    <div class="modle-wrapper clearfix">
        <div class="modle-main">
            <div class="modle-top">
                <img src="${policyResourceUrl}/images/paySuccess/pop_img@2x.png" alt="">
            </div>
            <div class="modle-bottom">
                <p>
                    一、<br>您在乐+生活合作商家的任意一笔消费，乐+生活都会送您一笔鼓励金，每笔都返！
                </p>

                <p>
                    二、<br>当您周四的第一笔消费完成后，周一至周三累计的鼓励金值连同周四第一笔返还的鼓励金将会产生暴击翻倍。必定暴击！
                </p>

                <p>
                    三、<br>没有指定鼓励金核销日！随时可用！
                </p>

                <p>
                    四、<br>鼓励金不过期！直接存入您的鼓励金账户！绝不清零！
                </p>
            </div>
        </div>
    </div>
    <div class="modle-close"></div>
</div>
<div class="modle-week">
    <div class="week-wrapper clearfix">
        <div class="week-main">
            <div class="week-top">
                <img src="${policyResourceUrl}/images/paySuccess/green@2x.png" alt="">
            </div>
            <div class="week-center">
                <ul class="week-list">
                    <li class="week-item clearfix">
                            <span class="left">
                                <a class="day">周一</a>
                                <a class="date icon-yfb">2.20</a>
                            </span>
                        <span class="right money">¥ 0.58</span>
                    </li>
                    <li class="week-item clearfix">
                            <span class="left">
                                <a class="day">周二</a>
                                <a class="date icon-yfb">2.20</a>
                            </span>
                        <span class="right money">¥ 0.58</span>
                    </li>
                    <li class="week-item clearfix">
                            <span class="left">
                                <a class="day">周三</a>
                                <a class="date">2.20</a>
                            </span>
                        <span class="right no-money">未支付</span>
                    </li>
                    <li class="week-item clearfix">
                            <span class="left">
                                <c:if test="${day==5}"> <a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=5}"> <a class="day">周四</a>
                                </c:if>
                                <a class="date icon-yfb">2.20</a>
                            </span>
                        <span class="right money">¥ 0.58</span>
                    </li>
                    <li class="week-item clearfix today">
                            <span class="left">
                                <c:if test="${day==6}"> <a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=6}"> <a class="day">周五</a>
                                </c:if>
                                <a class="date">2.20</a>
                            </span>
                        <span class="right money">¥ 0.58</span>
                    </li>
                    <li class="week-item clearfix">
                            <span class="left">
                                <c:if test="${day==7}"> <a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=7}"> <a class="day">周六</a>
                                </c:if>
                                <a class="date">2.20</a>
                            </span>
                        <span class="right no-money">支付后领取</span>
                    </li>
                    <li class="week-item clearfix">
                            <span class="left">
                                 <c:if test="${day==1}"> <a class="day">今日</a>
                                 </c:if>
                                <c:if test="${day!=1}"> <a class="day">周日</a>
                                </c:if>
                                <a class="date">2.20</a>
                            </span>
                        <span class="right no-money">支付后领取</span>
                    </li>
                </ul>
                <div class="sum clearfix">
                    <span class="left">我的鼓励金</span>
                    <span class="right">¥1.64</span>
                </div>
                <div class="today-money">获得<span>¥0.56</span>鼓励金</div>
            </div>
            <div class="week-bottom">
                <p class="getJB">获得<span>¥2.2</span>金币</p>

                <div class="btn-confirm">确认</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $('.rule').on('touchstart', function () {
        $('.modle').css({'display': 'block'});
    })
    $('.modle-close').on('touchstart', function () {
        $('.modle').css({'display': 'none'});
    })
    $('.btn-confirm').on('touchstart', function () {
        $('.modle-week').css({'display': 'none'});
    })
</script>
</body>
</html>
