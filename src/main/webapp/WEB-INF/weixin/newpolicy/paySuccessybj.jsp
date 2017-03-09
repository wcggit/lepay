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
        <p class="clearfix"><span class="left">消费金额：</span><span
                class="right">¥${offLineOrder.totalPrice/100.0}</span></p>

        <p>(微信支付¥${offLineOrder.truePay/100.0}，鼓励金支付¥${offLineOrder.trueScore/100.0})</p>
    </li>
    <li class="confirm-code ordinry">
        <p class="clearfix"><span class="left">确认码：</span><span
                class="right">${offLineOrder.lepayCode}</span></p>
    </li>
    <li class="confirm-code ordinry">
        <p class="clearfix"><span class="left">鼓励金：<a class="rule">活动规则</a></span><span
                class="right">+ ¥${offLineOrder.rebate/100.0}</span></p>
    </li>
</ul>
<div class="hr-style"></div>
<div class="middle">
    <div class="icon"></div>
    <div class="desc">
        <p>金币账户余额:¥${scoreC.score/100.0}</p>

        <p>你已获得${offLineOrder.scoreC/100.0}金币</p>
    </div>
    <div class="btn" onclick="window.location.href='http://www.lepluslife.com/front/gold/weixin'">
        兑换
    </div>
</div>
<div class="hr-style"></div>
<div class="bottom">
    <div class="ad">
        <img src="${policyResourceUrl}/images/paySuccess_noActivity/rechage_card_banner.png" alt=""
             onclick="window.location.href='http://www.lepluslife.com/front/order/weixin/recharge'">
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
<div class="modle-week" style="display: none">
    <div class="week-wrapper clearfix">
        <div class="week-main">
            <div class="week-top">
                <img src="${policyResourceUrl}/images/paySuccess/green@2x.png" alt="">
            </div>
            <div class="week-center">
                <ul class="week-list">
                    <c:if test="${day==2}">
                    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=2}">     <li class="week-item clearfix">
                            <span class="left"><a class="day">周一</a>
                                </c:if>
                                <c:if test="${dailyRebate[0]!=null}">
                                <c:if
                                        test="${dailyRebate[0]['value']=='0'}"><a
                                        class="date">${weekends[0]}</a></span><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[0]['value'] !='0'}">
                                    <c:if test="${dailyRebate[0]['value'].contains('×')}"><a
                                            class="date icon-yfb"></c:if>
                                        <c:if test="${!dailyRebate[0]['value'].contains('×')}"><a
                                                class="date"></c:if>
                                                ${weekends[0]}</a></span><span
                                        class="right money">¥${dailyRebate[0]['value']}
                                    </c:if></span></c:if>
                                    <c:if test="${dailyRebate[0]==null}"><a
                                        class="date">${weekends[0]}</a></span><span
                            class="right no-money">支付后领取</span></c:if>
                    </li>
                    <c:if test="${day==3}">
                    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=3}">     <li class="week-item clearfix">
                            <span class="left"><a class="day">周二</a>
                                </c:if>
                                <c:if test="${dailyRebate[1]!=null}">
                                <c:if
                                        test="${dailyRebate[1]['value']=='0'}"><a
                                        class="date">${weekends[1]}</a></span><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[1]['value'] !='0'}">
                                    <c:if test="${dailyRebate[1]['value'].contains('×')}"><a
                                            class="date icon-yfb"></c:if>
                                        <c:if test="${!dailyRebate[1]['value'].contains('×')}"><a
                                                class="date"></c:if>
                                                ${weekends[1]}</a></span><span
                                        class="right money">¥${dailyRebate[1]['value']}
                                    </c:if></span></c:if>
                                    <c:if test="${dailyRebate[1]==null}"><a
                                        class="date">${weekends[1]}</a></span><span
                            class="right no-money">支付后领取</span></c:if>
                    </li>
                    <c:if test="${day==4}">
                    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=4}">     <li class="week-item clearfix">
                            <span class="left"><a class="day">周三</a>
                                </c:if>
                                    <c:if test="${dailyRebate[2]!=null}">
                                <c:if
                                        test="${dailyRebate[2]['value']=='0'}"><a
                                        class="date">${weekends[2]}</a></span><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[2]['value'] !='0'}">
                                    <c:if test="${dailyRebate[2]['value'].contains('×')}"><a
                                            class="date icon-yfb"></c:if>
                                        <c:if test="${!dailyRebate[2]['value'].contains('×')}"><a
                                                class="date"></c:if>
                                                ${weekends[2]}</a></span><span
                                        class="right money">¥${dailyRebate[2]['value']}
                                    </c:if></span></c:if>
                                    <c:if test="${dailyRebate[2]==null}"><a
                                        class="date">${weekends[2]}</a></span><span
                            class="right no-money">支付后领取</span></c:if>
                    </li>

                    <c:if test="${day==5}">
                    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=5}">     <li class="week-item clearfix">
                            <span class="left"><a class="day">周四</a>
                                </c:if>
                                    <c:if test="${dailyRebate[3]!=null}">
                                <c:if
                                        test="${dailyRebate[3]['value']=='0'}"><a
                                        class="date">${weekends[3]}</a></span><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[3]['value'] !='0'}">
                                    <c:if test="${dailyRebate[3]['value'].contains('×')}"><a
                                            class="date icon-yfb"></c:if>
                                        <c:if test="${!dailyRebate[3]['value'].contains('×')}"><a
                                                class="date"></c:if>
                                                ${weekends[3]}</a></span><span
                                        class="right money">¥${dailyRebate[3]['value']}
                                    </c:if></span></c:if>
                                    <c:if test="${dailyRebate[3]==null}"><a
                                        class="date">${weekends[3]}</a></span><span
                            class="right no-money">支付后领取</span></c:if>
                    </li>
                    <c:if test="${day==6}">
                    <li class="week-item clearfix today">
                            <span class="left"> <a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=6}">
                                <li class="week-item clearfix">
                            <span class="left"> <a class="day">周五</a>
                                </c:if>
                                <a class="date">${weekends[4]}</a>
                            </span>
                                    <c:if test="${dailyRebate[4]!=null}">
                                    <c:if
                                            test="${dailyRebate[4]['value']=='0'}"><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[4]['value'] !='0'}"><span
                                            class="right money">¥${dailyRebate[4]['value']}</c:if></span></c:if>
                                    <c:if test="${dailyRebate[4]==null}"> <span
                                            class="right no-money">支付后领取</span></c:if>
                                </li>

                                <c:if test="${day==7}">    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                </c:if>
                                <c:if test="${day!=7}">   <li class="week-item clearfix">
                            <span class="left"> <a class="day">周六</a>
                                </c:if>
                                <a class="date">${weekends[5]}</a>
                            </span>
                                    <c:if test="${dailyRebate[5]!=null}">
                                    <c:if
                                            test="${dailyRebate[5]['value']=='0'}"><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[5]['value'] !='0'}"><span
                                            class="right money">¥${dailyRebate[5]['value']}</c:if></span></c:if>
                                    <c:if test="${dailyRebate[5]==null}"> <span
                                            class="right no-money">支付后领取</span></c:if>
                                </li>
                    <c:if test="${day==1}">
                    <li class="week-item clearfix today">
                            <span class="left"><a class="day">今日</a>
                                 </c:if>
                                <c:if test="${day!=1}"> <li class="week-item clearfix">
                            <span class="left"><a class="day">周日</a>
                                </c:if>
                                <a class="date">${weekends[6]}</a>
                            </span>
                                    <c:if test="${dailyRebate[6]!=null}">
                                    <c:if
                                            test="${dailyRebate[6]['value']=='0'}"><span
                                        class="right no-money">未支付</c:if>
                                    <c:if
                                            test="${dailyRebate[6]['value'] !='0'}"><span
                                            class="right money">¥${dailyRebate[6]['value']}</c:if></span></c:if>
                                    <c:if test="${dailyRebate[6]==null}"> <span
                                            class="right no-money">支付后领取</span></c:if>
                                </li>
                </ul>
                <div class="sum clearfix">
                    <span class="left">我的鼓励金</span>
                    <span class="right">¥${scoreA.score/100.0}</span>
                </div>
                <div class="today-money">获得<span>¥${offLineOrder.rebate/100.0}</span>鼓励金</div>
            </div>
            <div class="week-bottom">
                <p class="getJB">获得<span>¥${offLineOrder.scoreC/100.0}</span>金币</p>

                <div class="btn-confirm">确认</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    document.title = "${offLineOrder.merchant.name}";
    $(document).ready(function () {
        if (${offLineOrder.rebate!=0}) {
            $(".modle-week").show();
        }
    });
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
