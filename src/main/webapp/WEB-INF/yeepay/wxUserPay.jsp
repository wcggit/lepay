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
    <title></title>
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
        <label for="monetary">
            使用红包（元）<input type="text" id="monetary" placeholder="不使用红包" readonly><span
                class="close"></span><span class="guangBiao"></span>
        </label>

        <p class="back-hongbao"><span
                class="icon-hongbao"></span>您有<span>￥<font>${scoreA.score/100.0}</font></span>红包余额
        </p>

        <div class="need-pay"><span>还需支付</span><span>￥<font></font></span></div>
        <div class="form-btn">确认支付</div>
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
<!--弹层-->
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
    $(function () {
        var val = ${scoreA.score/100>totalPrice/100}
                ? $('.form-name font').text() : $('.back-hongbao font').text();
        $("#monetary").val(val);
        fontFUn();
        var spans = $('#keyboard button:not(:last-child)');
        spans.each(function (i) {
            spans.eq(i).attr({'unselectable': 'on'}).css({'-webkit-user-select': 'none'}).on('selectstart'), function () {
                return false;
            };
            spans.eq(i).on("touchstart", function () {
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
        })
        $('#keyboard button:last-child').on('touchstart', function () {
            var str = $("#monetary").val();
            $("#monetary").val($("#monetary").val().substring(0, str.length - 1));
            fontFUn();
        });
        $(".close").on("touchstart", function () {
            $("#monetary").val("");
            fontFUn();
        })

        //    判断所输入的值
        function fontFUn() {
            if ($("#monetary").val() == '' || $("#monetary").val() == null) {
                $("#monetary").css({'font-size': '3.2vw'});
                $(".close").css({'display': 'none'});
            } else {
                $("#monetary").css({'font-size': '5.2vw'});
                $(".close").css({'display': 'block'});
                if (eval($("#monetary").val()) > val) {
                    $("#monetary").val(val);
                }
             //   console.log(eval($("#monetary").val()) > eval($(".back-hongbao font").text()));
            }
            $('.need-pay font').text(toDecimal(${totalPrice/100} -$("#monetary").val()));
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

        //    确认支付按钮
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');
            if ($('.need-pay font').text() == 0) {

                $('.conform .showOut').html("确认使用乐+红包支付￥${totalPrice/100}吗?");
                $('.conform').css({'display': 'block'});
                $('.conform .cancel').on('click', function () {
                    $('.conform').css({'display': 'none'});
                })
                return;
            }
            $(this).empty().text("正在支付,请稍后")
            pay();
        });

    });
    function bindPay() {
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');
            if ($('.need-pay font').text() == 0) {

                $('.conform .showOut').html("确认使用乐+红包支付￥${totalPrice/100}吗?");
                $('.conform').css({'display': 'block'});
                $('.conform .cancel').on('click', function () {
                    $('.conform').css({'display': 'none'});
                })
                return;
            }
            $(this).empty().text("正在支付,请稍后")
            pay();
        });
    }
    $("#pay-cancel").on('touchstart', function () {
        bindPay()
    });

    function pay() {
        var totalPrice = ${totalPrice};
        var truePrice = 0;
        if ($('.need-pay font').text() != "" && $('.need-pay font').text() != null) {
            truePrice =
            parseInt(new BigDecimal($('.need-pay font').text()).multiply(new BigDecimal("100")));
        }
        var trueScore = 0;
        if ($("#monetary").val() != "" && $("#monetary").val() != null) {
            trueScore =
            parseInt(new BigDecimal($("#monetary").val()).multiply(new BigDecimal("100")));
        }
        var ext = ljhash(truePrice + " " + trueScore
                         + " ${leJiaUser.userSid} ${merchantId} " + totalPrice
                         + " ${openid}", "lepluslife");
        $.post('/pay/yeepay/offLineOrderForUser', {
            ext: ext
        }, function (res) {
            location.href = res.payurl;
        });
    }

    $("#pay-confrim").on('touchstart', function () {
        $('#pay-confrim').unbind('touchstart');
        var ext = ljhash("${leJiaUser.userSid} ${merchantId} ${totalPrice}", "lepluslife");
        $.post('/pay/yeepay/payByScoreA', {
            ext: ext
        }, function (res) {
            if (res.status == 200) {
                window.location.href = '/pay/wxpay/paySuccess?orderSid=' + res.data.orderSid;
            } else {
                alert(res.msg);
            }
        });
    });

</script>
</html>
