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
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
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
        $.post('/lepay/wxpay/offLineOrderForUser', {
            ext: ext
        }, function (res) {
            $(this).removeClass('btn-disabled');
//            调用微信支付js-api接口
            if (res['err_msg'] != null && res['err_msg'] != "") {
                alert(res['err_msg']);
                return;
            } else {
                weixinPay(res);
                return;
            }
        });

    }

    $("#pay-confrim").on('touchstart', function () {
        $('#pay-confrim').unbind('touchstart');
        var ext = ljhash("${leJiaUser.userSid} ${merchantId} ${totalPrice}", "lepluslife");
        $.post('/lepay/wxpay/payByScoreA', {
            ext: ext
        }, function (res) {
            if (res.status == 200) {
                <%--if (res.data.rebateWay == 1) {--%>
                <%--window.location.href =--%>
                <%--'${wxRootUrl}/lepay/wxpay/paySuccess?orderSid=' + res.data.orderSid;--%>
                <%--} else {--%>
                window.location.href =
                '/lepay/wxpay/paySuccess?orderSid=' + res.data.orderSid;
                // }
            } else {
                alert(res.msg)
            }
        });
    });
    <%--wx.config({--%>
    <%--debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。--%>
    <%--appId: '${wxConfig['appId']}', // 必填，公众号的唯一标识--%>
    <%--timestamp: ${wxConfig['timestamp']}, // 必填，生成签名的时间戳--%>
    <%--nonceStr: '${wxConfig['noncestr']}', // 必填，生成签名的随机串--%>
    <%--signature: '${wxConfig['signature']}',// 必填，签名，见附录1--%>
    <%--jsApiList: [--%>
    <%--'chooseWXPay'--%>
    <%--] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2--%>
    <%--});--%>
    <%--wx.ready(function () {--%>
    <%--// config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。--%>
    <%--//       隐藏菜单--%>
    <%--wx.hideOptionMenu();--%>

    <%--});--%>
    <%--wx.error(function (res) {--%>
    <%--// config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。--%>

    <%--});--%>
    <%--$('#confirm-pay').on('click', function () {--%>
    <%--var totalPrice = $("#monetary").val();--%>
    <%--if (${openid!=null}) {--%>
    <%--// 首先提交请求，生成预支付订单--%>
    <%--$.post('${wxRootUrl}/lepay/wxpay/offLineOrder', {--%>
    <%--truePrice: totalPrice,--%>
    <%--merchantId: ${merchantId},--%>
    <%--openid:${openid!=null}--%>
    <%--}, function (res) {--%>
    <%--$(this).removeClass('btn-disabled');--%>
    <%--//            调用微信支付js-api接口--%>
    <%--if (res['err_msg'] != null && res['err_msg'] != "") {--%>
    <%--alert(res['err_msg']);--%>
    <%--return;--%>
    <%--} else {--%>
    <%--weixinPay(res);--%>
    <%--return;--%>
    <%--}--%>
    <%--});--%>
    <%--} else {--%>
    <%--if (${scoreA.score>0}) {--%>
    <%--var ext = ljhash("${leJiaUser.userSid} " + totalPrice + " ${merchant.id}",--%>
    <%--"lepluslife");--%>
    <%--location.href = "www.lepluslife.com/lepay/wxpay/userpay?ext=" + ext;--%>
    <%--} else {--%>

    <%--}--%>
    <%--}--%>
    <%--});--%>
    function weixinPay(res) {
//        wx.chooseWXPay({
//                           timestamp: res['timeStamp'], // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
//                           nonceStr: res['nonceStr'], // 支付签名随机串，不长于 32 位
//                           package: res['package'], // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
//                           signType: res['signType'], // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
//                           paySign: res['sign'], // 支付签名
//                           success: function (data) {
//                               window.location.href =
//                               '/lepay/wxpay/paySuccess?orderSid='
//                               + res['orderSid'];
//                           },
//                           cancel: function (res) {
//                               $('.form-btn').empty().text("确认支付");
//                               bindPay();
//                           },
//                           fail: function (res) {
//                           }
//                       });
        WeixinJSBridge.invoke(
                'getBrandWCPayRequest', {
                    "appId":     res['appId'] + "",     //公众号名称，由商户传入
                    "timeStamp": res['timeStamp'] + "", // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                    "nonceStr":  res['nonceStr'] + "", // 支付签名随机串，不长于 32 位
                    "package":   res['package'] + "", // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
                    "signType":  res['signType'] + "", // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
                    "paySign":   res['sign'] + "" // 支付签名
                },
                function (res) {
                    if (res.err_msg == "get_brand_wcpay_request：ok") {
                        window.location.href =
                        '/lepay/wxpay/paySuccess?orderSid='
                        + res['orderSid'];
                    }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    else {
                        $('.form-btn').empty().text("确认支付");
                        bindPay();
                    }
                }
        );
        if (typeof WeixinJSBridge == "undefined") {
            if (document.addEventListener) {
                document.addEventListener('WeixinJSBridgeReady', weixinPay, false);
            } else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', weixinPay);
                document.attachEvent('onWeixinJSBridgeReady', weixinPay);
            }
        } else {
            weixinPay();
        }
    }
</script>
</html>
