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
    <link rel="stylesheet" href="${resourceUrl}/css/common.css">
    <link rel="stylesheet" href="${resourceUrl}/css/index.css">
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
<script src="${resourceUrl}/js/jquery-2.0.3.min.js"></script>
<script src="${resourceUrl}/js/lphash.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script src="${resourceUrl}/js/MathContext.js"></script>
<script src="${resourceUrl}/js/BigDecimal.js"></script>
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
    wx.config({
                  debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                  appId: '${wxConfig['appId']}', // 必填，公众号的唯一标识
                  timestamp: ${wxConfig['timestamp']}, // 必填，生成签名的时间戳
                  nonceStr: '${wxConfig['noncestr']}', // 必填，生成签名的随机串
                  signature: '${wxConfig['signature']}',// 必填，签名，见附录1
                  jsApiList: [
                      'chooseWXPay'
                  ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
              });
    wx.ready(function () {
        // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
//       隐藏菜单
        wx.hideOptionMenu();

    });
    wx.error(function (res) {
        // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。

    });

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
        if (${openid!=null}) {
            // 首先提交请求，生成预支付订单
            $.post('/lepay/wxpay/offLineOrder', {
                truePrice: totalPrice,
                merchantId: ${merchant.id},
                openid: "${openid}"
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
        } else {
            if (${scoreA.score>0}) {
                var ext = ljhash("${leJiaUser.userSid} " + totalPrice
                                 + " ${merchant.id} ${ljopenid}", "lepluslife");
                location.href = "/lepay/wxpay/userpay?ext=" + ext;
                $('#confirm-pay').empty().text("确认支付");
            } else {
                <%--var ext = ljhash("${leJiaUser.userSid} " + totalPrice--%>
                <%--+ " ${merchant.id} ${ljopenid}", "lepluslife");--%>
                totalPrice =
                parseInt(new BigDecimal(totalPrice).multiply(new BigDecimal("100")));
                var ext = ljhash(totalPrice + " 0"
                                 + " ${leJiaUser.userSid} ${merchant.id} " + totalPrice
                                 + " ${ljopenid}", "lepluslife");
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
        }
    }

    function weixinPay(res) {
        wx.chooseWXPay({
                           timestamp: res['timeStamp'], // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                           nonceStr: res['nonceStr'], // 支付签名随机串，不长于 32 位
                           package: res['package'], // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
                           signType: res['signType'], // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
                           paySign: res['sign'], // 支付签名
                           success: function (data) {
                               window.location.href =
                               '/lepay/wxpay/paySuccess?orderSid='
                               + res['orderSid'];
                           },
                           cancel: function (res) {
                               $('#confirm-pay').empty().text("确认支付");
                               $('#confirm-pay').on('touchstart', function () {
                                   $('#confirm-pay').unbind('touchstart');
                                   $(this).empty().text("正在支付,请稍后");
                                   pay();
                               });
                           },
                           fail: function (res) {
                           }
                       });
    }
</script>
</html>
