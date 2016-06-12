<%--
  Created by IntelliJ IDEA.
  User: wcg
  Date: 16/4/18
  Time: 下午2:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${resourceUrl}/js/jquery-2.0.3.min.js"></script>
<!--底部菜单-->
<nav class="mui-bar mui-bar-tab">
    <a class="mui-tab-item"  id="topic">
        <span class="mui-icon iconfont">&#xe604;</span>
        <span class="mui-tab-label">乐选</span>
    </a>
    <a class="mui-tab-item"  id="product">
        <span class="mui-icon mui-icon-list"></span>
        <span class="mui-tab-label">分类</span>
    </a>
    <a class="mui-tab-item"  id="cart">
        <span class="mui-icon iconfont">&#xe607;<span class="mui-badge cartNum" style="display: none"  id="cart-number"></span></span>
        <span class="mui-tab-label">购物车</span>
    </a>
    <a class="mui-tab-item"  id="order">
        <span class="mui-icon iconfont">&#xe60d;</span>
        <span class="mui-tab-label">订单</span>
    </a>
</nav>

<script>
    $(function(){
        $.ajax({
           type: "get",
           url: "/weixin/cart/cartNumber?"+  new Date().getTime(),
           contentType: "application/json",
           success: function (data) {
//                       location.href = "/manage/topic";
               if(data.msg!=0){
                   $("#cart-number").attr("style","display:block")
               $("#cart-number").text(data.msg);
               }else{
                   $("#cart-number").attr("style","display:none")
               }
           }
       });
        $("#cart").bind("touchstart",function(){
            localStorage.clear()
            location.href="/weixin/cart"
        });
        $("#product").bind("touchstart",function(){
            localStorage.clear()
            location.href="/weixin/shop"
        });
        $("#order").bind("touchstart",function(){
            localStorage.clear()
            location.href="/weixin/orderDetail"
        });
        $("#topic").bind("touchstart",function(){
            localStorage.clear()
            location.href="/weixin/topic"
        });
        var str  = window.location.pathname;
      var  strs =  str.split("/");
        if(strs[2]=="cart"){
            $("#cart").addClass("mui-active");
        }
        if(strs[2]=="topic"){
            $("#topic").addClass("mui-active");
        }
        if(strs[2]=="shop"){
            $("#product").addClass("mui-active");
        }
        if(strs[2]=="orderDetail"){
            $("#order").addClass("mui-active");
        }

    });
</script>
