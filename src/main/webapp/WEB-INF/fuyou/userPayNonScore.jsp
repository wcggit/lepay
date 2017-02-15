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
    <title>富友支付</title>
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
            使用红包<input type="text" id="monetary" placeholder="老板比较懒,还没有开通红包功能,快去催催他" readonly><span
                class="close"></span><span class="guangBiao"></span>
        </label>

        <p class="back-hongbao"><span
                class="icon-hongbao"></span>您有<span>￥<font>${scoreA.score/100.0}</font></span>红包余额
        </p>

        <div class="need-pay"><span>还需支付</span><span>${totalPrice/100.0}￥<font></font></span></div>
        <div class="form-btn">确认支付</div>
    </form>
</div>
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

        //    确认支付按钮
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');

            $(this).empty().text("正在支付,请稍后")
            pay();
        });

   // });
    function bindPay() {
        $('.form-btn').on('touchstart', function () {
            $('.form-btn').unbind('touchstart');

            $(this).empty().text("正在支付,请稍后")
            pay();
        });
    }

    function pay() {
        var totalPrice = ${totalPrice};
        var truePrice = ${totalPrice};
        var trueScore = 0;
        var ext = ljhash(truePrice + " " + trueScore
                         + " ${leJiaUser.userSid} ${merchantId} " + totalPrice
                         + " ${openid}", "lepluslife");
        $.post('/pay/wxpay/offLineOrderForUser', {
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
    function weixinPay(result) {

        WeixinJSBridge.invoke('getBrandWCPayRequest', {
            "appId": result.sdk_appid,//"wx2421b1c4370ec43b", //公众号名称，由商户传入
            "timeStamp": result.sdk_timestamp,//"1395712654", //时间戳，自1970年以来的秒数
            "nonceStr": result.sdk_noncestr,//"e61463f8efa94090b1f366cccfbbb444", //随机串
            "package": result.sdk_package,//"prepay_id=u802345jgfjsdfgsdg888",
            "signType": result.sdk_signtype,//"MD5", //微信签名方式:
            "paySign": result.sdk_paysign//"70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名
        }, function (res) { // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。

            if (res.err_msg == "get_brand_wcpay_request:ok") {
                window.location.href = '/pay/wxpay/paySuccess?orderSid=' + result['orderSid'];
            }
            if (res.err_msg == "get_brand_wcpay_request:cancel") {
                $('.form-btn').empty().text("确认支付");
                bindPay();
            }
            if (res.err_msg == "get_brand_wcpay_request:fail") {
                alert("支付失败");
            }
        });
    }

</script>
</html>
