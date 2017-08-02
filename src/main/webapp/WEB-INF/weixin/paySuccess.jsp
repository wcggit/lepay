<%--
  Created by IntelliJ IDEA.
  User: zhangwen
  Date: 16/9/13
  Time: 上午09:13
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
    <link rel="stylesheet" href="${cdnUrl}css/swiper.min.css">
    <link rel="stylesheet" href="${cdnUrl}lepay/weixin/success/paySuccess2.css">
</head>
<body>
<div id="main">
    <div class="header">
        <div class="top">
            <div class="left"></div>
            <div class="right">支付成功</div>
        </div>
        <div class="money">${offLineOrder.totalPrice/100}元</div>
        <c:if test="${offLineOrder.truePay==0}">
            <div class="pile">红包支付${offLineOrder.trueScore/100}元</div>
        </c:if>
        <c:if test="${offLineOrder.trueScore==0}">
            <div class="pile">微信支付${offLineOrder.truePay/100}元</div>
        </c:if>
        <c:if test="${offLineOrder.trueScore!=0&&offLineOrder.truePay!=0}">
            <div class="pile">红包支付${offLineOrder.trueScore/100}元 微信支付${offLineOrder.truePay/100}元
            </div>
        </c:if>
        <ul>
            <c:if test="${offLineOrder.rebate!=0}">
                <li>
                    <span>获得红包</span>
                    <span>${offLineOrder.rebate/100}元</span>
                </li>
            </c:if>
            <c:if test="${offLineOrder.scoreB!=0}">
                <li>
                    <span>获得积分</span>
                    <span>${offLineOrder.scoreB}积分</span>
                </li>
            </c:if>
            <li>
                <span>支付时间</span>
                <span><fmt:formatDate value="${offLineOrder.completeDate}" type="both"/></span>
            </li>
            <li>
                <span>乐付确认码</span>
                <span>${offLineOrder.lepayCode}</span>
            </li>
        </ul>
    </div>
    <!-- Swiper -->
    <div class="swiper-all">
        <c:if test="${map.currScoreA != null && map.currScoreA > 0}">
            <div class="div-out">
                <div class="ttl">
                    您有<font color="#f54339">${map.currScoreA/100}</font>元红包，可在乐店抵现
                    <a href="http://www.lepluslife.com/merchant/index">全部</a>
                </div>
                <div class="swiper-container1">
                    <div class="swiper-wrapper" id="swiper">

                    </div>
                </div>
            </div>
        </c:if>

        <div class="shadow-small">
            <div class="money-god-small">
                <div class="god-top"></div>
                <div class="god-bottom">
                    <div class="bottom-ttl">邀请您成为乐加会员</div>
                    <div class="bottom-money" id="score2"></div>
                    <div class="bottom-reward">新人体验红包</div>
                    <div class="swifter-out">
                        <div class="swifter-in">存入余额</div>
                        <div class="swifter-track"></div>
                    </div>
                </div>
            </div>
            <div class="btn-close"></div>
        </div>
    </div>
    <!--shadow-->
    <div class="shadow-big">
        <div class="money-god-big">
            <div class="god-top"></div>
            <div class="god-bottom">
                <div class="bottom-ttl">支付成功 奖励红包</div>
                <div class="bottom-money" id="score1"></div>
                <div class="bottom-reward" id="totalScoreA"></div>
                <div class="swifter-out">
                    <div class="swifter-in">存入余额</div>
                    <div class="swifter-track"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${cdnUrl}js/swiper.min.js"></script>
<script src="${cdnUrl}js/jquery-2.0.3.min.js"></script>
<script>
    document.title = "${offLineOrder.merchant.name}";
    var swifterOut1 = CN('swifter-out')[0];
    var swifterIn1 = CN('swifter-in')[0];
    var trackImg1 = CN('track-img')[0];
    var swifterOut2 = CN('swifter-out')[1];
    var swifterIn2 = CN('swifter-in')[1];
    var trackImg2 = CN('track-img')[1];
    var shadowBig = CN('shadow-big')[0];
    var shadowSmall = CN('shadow-small')[0];
    var btnClose = CN('btn-close')[0];
    var status = '${map.status}';
    if (status == 1) {
        $("#score1").html("${map.score/100}元");
        $("#totalScoreA").html("累计奖${map.totalScoreA/100}元");
        final1();
    } else if (status == 2) {
        $("#score2").html("${map.score/100}元");
        shadowSmall.style.display = 'block';
    }
    //        获取className
    function CN(t) {
        return document.getElementsByClassName(t);
    }

    //        swiper
    function swiper() {
        var swiper = new Swiper('.swiper-container1', {
            pagination: '.swiper-pagination',
            slidesPerView: 'auto',
            spaceBetween: 10
        });
    }
    function swiper2() {
        var swiper2 = new Swiper('.swiper-container2', {
            pagination: '.swiper-pagination',
            slidesPerView: 'auto',
            spaceBetween: 10
        });
    }

    sliderFun(swifterOut1, swifterIn1, final1);
    sliderFun(swifterOut2, swifterIn2, final2);
    //        滑动效果
    function sliderFun(outDiv, inDiv, funName) {
        var startX, endX, distance = 0;
        var dis = outDiv.clientWidth - inDiv.clientWidth;
        inDiv.addEventListener('touchstart', function (e) {
            startX = e.touches[0].clientX;
        }, false)
        inDiv.addEventListener('touchmove', function (e) {
            endX = e.touches[0].clientX;
            distance = endX - startX;
            if (distance <= dis && distance >= 0) {
                inDiv.style.left = distance + 'px';
            }
        }, false)
        inDiv.addEventListener('touchend', function (e) {
            if (distance <= dis && distance >= 0) {
                inDiv.style.left = '0px';
            } else if (distance >= dis) {
                funName();
            }
        }, false)
    }
    function final2() {
        //  alert("阴影将要消失了！");
        shadowBig.style.display = 'none';
    }
    function final1() {
        // alert("阴影将要消失了！");
        shadowSmall.style.display = 'none';
    }
    btnClose.addEventListener('touchend', function (e) {
        shadowSmall.style.display = 'none';
    }, false)
</script>

<script>
    var currA = '${map.currScoreA}', currB = '${map.currScoreB}';
    $(function () {
        var merchantId = '${offLineOrder.merchant.id}';
        if (currA != null && currA > 0) {
            $.get('http://www.tiegancrm.com/merchant/payList/' + merchantId, function (res) {
                //  $.get('${wxRootUrl}/merchant/pay/'+merchantId, function (res) {
                var status = res.msg, list = res.data, i = 0, content = '';
                if (status == 0) { //没有距离
                    for (i; i < list.length; i++) {
                        content +=
                        '<div class="swiper-slide" onclick="showMerchant(' + list[i].id
                        + ')"><div class="top-img" style="background: url(\''
                        + list[i].picture +
                        '\') no-repeat;background-size: 100% 100%;"></div><ul><li>' + list[i].name
                        + '</li> <li>' + list[i].area
                        + ' | ' + list[i].typeName
                        + '</li>';
                        if (list[i].perSale != null && list[i].perSale != 0) {
                            content += '<li class="price">' + list[i].perSale / 100 + '元/人</li>';
                        }
                        content += '</ul></div>';
                    }
                } else {
                    for (i; i < list.length; i++) {
                        content +=
                        '<div class="swiper-slide" onclick="showMerchant(' + list[i].id
                        + ')"><div class="top-img" style="background: url("'
                        + list[i].picture +
                        '") no-repeat;background-size: 100% 100%;"></div><ul><li>' + list[i].name
                        + '</li> <li>' + list[i].area
                        + ' | ' + list[i].perSale < 1000 ? list[i].perSale : (list[i].perSale / 100
                                                                              + "k") + 'm | '
                                                                             + list[i].typeName
                                                                             + '</li>';
                        if (list[i].perSale != null && list[i].perSale != 0) {
                            content += '<li class="price">' + list[i].perSale / 100 + '元/人</li>';
                        }
                        content += '</ul></div>';
                    }
                }
                $("#swiper").html(content);
                swiper();
            });
        }
        <%--if (currB != null && currB > 0) {--%>
        <%--$.get('http://127.0.0.1:8080/product/payList/' + merchantId, function (res) {--%>
        <%--//  $.get('${wxRootUrl}/merchant/pay/'+merchantId, function (res) {--%>
        <%--var status = res.msg;--%>
        <%--if (status == 0) { //没有距离--%>

        <%--}--%>
        <%--});--%>
        <%--}--%>
    });

    function showMerchant(merchantId) {
            location.href = "http://www.lepluslife.com/merchant/info/"+ merchantId;
    }
    function goLePlusLife() {
        location.href = "http://www.lepluslife.com/weixin/shop"
    }
</script>
</body>
</html>
