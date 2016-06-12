<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 16/4/21
  Time: 下午2:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<script src="http://www.lepluslife.com/resource/js/jquery-1.11.3.min.js"></script>
<head>
    <title></title>
</head>
<body>
<input type="button" value="提交" onclick="testM()">
</body>

<script>
    function testM() {
        $.ajax({
                   type: "post",
                   url: "/user/detail",
                   contentType: "application/json",
                   data: {token: "0184391249659"},
                   success: function (data) {
                       if (data.status == 200) {
                           location.href = "${wxRootUrl}/weixin/order/" + data.msg;
                       } else {
                           alert("出现未知错误");
                       }
                   }
               });
    }
</script>
</html>
