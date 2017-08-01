<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 16/5/12
  Time: 上午9:35
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title></title>
    <link rel="stylesheet" href="${ossUrl}lepay_common.css">
    <link rel="stylesheet" href="${ossUrl}lepay_index.css">
    <script src="${ossUrl}zepto.min.js"></script>
    <script src="${ossUrl}lphash.js"></script>
    <script src="${ossUrl}MathContext.js"></script>
    <script src="${ossUrl}BigDecimal.js"></script>
</head>
<body>
<!--表单-->
<div id="form-monetary">
    <form action="">
        <p class="form-ttl">乐+签约商户</p>

        <p class="form-name">${merchant.name}</p>
        <label for="monetary">
            消费金额（元）<input type="text" id="monetary" placeholder="问问收银员应该收多少Money？" readonly><span
                class="close"></span><span class="guangBiao"></span>
        </label>
        <c:if test="${merchant.partnership==1}">
            <c:if test="${leJiaUser!=null}">
                <p class="back-hongbao">本笔交易百分百返红包</p>
            </c:if>
        </c:if>
        <div class="form-btn" id="confirm-pay">确认支付</div>
    </form>
</div>
<!--键盘-->
<div id="keyboard">
    <button>1</button>
    <button>2</button>
    <button>3</button>
    <button>4</button>
    <button>5</button>
    <button>6</button>
    <button>7</button>
    <button>8</button>
    <button>9</button>
    <button>.</button>
    <button>0</button>
    <button><i></i></button>
</div>
<div class="conform">
    <ul>
        <li class="showOut">请输入金额</li>
        <li><span class="cancel">取消</span><span class="confirm">知道了</span></li>
    </ul>
</div>
</body>
<script>
    $(function () {
        $('#monetary').focus();
        var spans = $('#keyboard button:not(:last-child)');
        spans.each(function (i) {
            spans.eq(i).attr({'unselectable': 'on'}).css({'-webkit-user-select': 'none'}).on('selectstart'), function () {
                return false;
            };
            spans.eq(i).on("touchstart", function (event) {
                var _this = $(this);
                $(this).addClass('btn-orange');
                setTimeout(function () {
                    _this.removeClass('btn-orange');
                }, 100);
                var getValue = $(this).text();
                if ($("#monetary").val().indexOf(".") != -1) {
                    if ($("#monetary").val() >= 9999.99 || $("#monetary").val().length == 7) {
                        return;
                    }
                } else {
                    if (getValue.indexOf('.') != 0) {
                        if ($("#monetary").val() >= 9999 || $("#monetary").val().length == 4) {
                            return;
                        }
                    }
                }
//                //控制第一个不能输入小数点"."
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
        $('#keyboard button:last-child').on('touchstart', function () {
            var str = $("#monetary").val();
            $("#monetary").val($("#monetary").val().substring(0, str.length - 1));
            fontFUn();
        });
        $(".close").on("touchstart", function () {
            $("#monetary").val("");
            fontFUn();
        })
    });
    function fontFUn() {
        if ($("#monetary").val() == '' || $("#monetary").val() == null) {
            $("#monetary").css({'font-size': '3.2vw'});
            $(".close").css({'display': 'none'});
        } else {
            $("#monetary").css({'font-size': '5.2vw'});
            $(".close").css({'display': 'block'});
        }
    }


</script>
<script type="text/javascript">
    $('body').bind("selectstart", function () {
        return false;
    });
    document.title = "${merchant.name}";

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
        if (${openid!=null}) { //非会员消费
            var url = '/pay/yeepay/offLineOrder';
            if (${pure!=null}) {
                alert("不可使用纯支付码！");
                return
            }
            // 首先提交请求，生成预支付订单
            $.post(url, {
                truePrice: totalPrice,
                merchantId: ${merchant.id},
                openid: "${openid}"
            }, function (res) {
                location.href = res.payurl;
            });
        } else {
            if (${scoreA.score>0}) {
                var ext = ljhash("${leJiaUser.userSid} " + totalPrice
                                 + " ${merchant.id} ${ljopenid}", "lepluslife");
                location.href = "/pay/yeepay/userpay?ext=" + ext;
                $('#confirm-pay').empty().text("确认支付");
            } else {
                totalPrice =
                parseInt(new BigDecimal(totalPrice).multiply(new BigDecimal("100")));
                var ext = ljhash(totalPrice + " 0"
                                 + " ${leJiaUser.userSid} ${merchant.id} " + totalPrice
                                 + " ${ljopenid}", "lepluslife");
                $.post('/pay/yeepay/offLineOrderForUser', {
                    ext: ext
                }, function (res) {
                    location.href = res.payurl;
                });
            }
        }
    }



</script>
</html>
