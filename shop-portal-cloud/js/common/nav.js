var loginFlag = false;

$(function () {
    var v_navHtml = "<nav class=\"navbar navbar-inverse\">\n" +
        "    <div class=\"container-fluid\">\n" +
        "        <!-- Brand and toggle get grouped for better mobile display -->\n" +
        "        <div class=\"navbar-header\">\n" +
        "           <ul> <li><a class=\"navbar-brand\" href='/'>飞狐电商前台-游乐购666</a></li></ul>\n" +
        "        </div>\n" +
        "\n" +
        "        <!-- Collect the nav links, forms, and other content for toggling -->\n" +
        "        <div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-1\">\n" +
        "            <ul class=\"nav navbar-nav navbar-right\">\n" +
        "                <li id=\"loginInfo\"><a href=\"/login.html\">登录</a></li>\n" +
        "                <li><a href=\"/reg.html\">注册</a></li>\n" +
        "                <li><a href=\"/cart.html\">购物车</a></li>\n" +
        "            </ul>\n" +
        "        </div><!-- /.navbar-collapse -->\n" +
        "    </div><!-- /.container-fluid -->\n" +
        "</nav>";

    $("#navDiv").html(v_navHtml);

    $.ajaxSetup({
        beforeSend: function(xhr) {
            var v_token = $.cookie("fh_token");
            console.log(v_token);
            xhr.setRequestHeader("x-auth", v_token);
        }
    })

    $.ajax({
        type:"get",
        url:"http://api.shop.com:8086/members",
        async:false,
        success:function (result) {
            if (result.code == 200) {
                loginFlag = true;
                $("#loginInfo").html("<a>欢迎"+result.data+"登录！！！</a><a href='#' onclick='logout();'>退出</a>")
            }
        }

    })

})

var loginDivHtml = '\n' +
    '    <form class="form-horizontal" >\n' +
    '        <div class="form-group">\n' +
    '            <label  class="col-sm-2 control-label">用户名</label>\n' +
    '            <div class="col-sm-8">\n' +
    '                <input type="text" class="form-control"   id="memberName" placeholder="请输入登录名...">\n' +
    '            </div>\n' +
    '        </div>\n' +
    '\n' +
    '        <div class="form-group">\n' +
    '            <label  class="col-sm-2 control-label">密码</label>\n' +
    '            <div class="col-sm-8">\n' +
    '                <input type="password" class="form-control"  id="password" placeholder="请输入密码...">\n' +
    '            </div>\n' +
    '        </div>\n' +
    '\n' +
    '    </form>\n' +
    '';

function login(param) {
    $.ajax({
        type:"post",
        url:"http://api.shop.com:8086/members/login",
        data:param,
        success:function (result) {
            if (result.code == 200) {
                // 将信息临时存入cookie
                $.cookie("fh_token", result.data);
                loginFlag = true;
                buyProduct(param.productId, "add");
            } else {
                alert(result.msg);
            }
        }
    })
}

function buyProduct(productId, action, flag) {
    var count;
    if (action == "add") {
        count = 1;
    } else {
        count = -1;
    }
    if (loginFlag) {
        $.ajax({
            type:"post",
            url:"http://api.shop.com:8086/carts",
            data:{"productId":productId,"count":count},
            success:function (result) {
                if (result.code == 200) {
                    if (flag == 1) {
                        // 局部刷新
                        initCart();
                    } else {
                        // 刷新整个页面
                        location.href="/cart.html";
                    }

                }
            }
        })
    }else {
        // 弹出登录框
        var v_loginDlg = bootbox.dialog({
            title: '登录',
            message: loginDivHtml,
            backdrop:false,
            //size:"middle",
            buttons: {
                confirm: {
                    label: '<span class="glyphicon glyphicon-ok"></span>确认',
                    className: 'btn-primary',
                    callback: function(){
                        var v_param = {};
                        v_param.memberName = $("#memberName", v_loginDlg).val();
                        v_param.password = $("#password", v_loginDlg).val();
                        v_param.productId = productId;
                        console.log(v_param);
                        login(v_param);
                    }
                },
                cancel: {
                    label: '<span class="glyphicon glyphicon-remove"></span>取消',
                    className: 'btn-danger'
                }
            }
        });
    }
}


function initCart() {
    // 如果用户没登录
    if (!loginFlag) {
        $("#cartDiv").html("<div style='text-align: center;'><h1>没有登录，请先<a href='/login.html'>登录</a>或者<a href='reg.html'>注册</a></h1></div>");
    }
    // 如果用户登录了
    if (loginFlag) {
        $.ajax({
            type:"get",
            url:"http://api.shop.com:8086/carts",
            success:function (result) {
                if (result.code == 6002) {
                    // 购物车为空，赶紧去购买吧
                    $("#cartDiv").html("<div style='text-align: center;'><h1><a href='/'>购物车为空，赶紧购买吧！！！</a> </h1></div>");
                } else if (result.code == 200) {
                    // 购物车不为空，直接显示内容
                    console.log(result.data);
                    var v_cart = result.data;
                    var v_itemArr =  v_cart.cartItemVoList;
                    var v_cartTemplate = $("#cartItemTemplate").html();
                    $("#cartBody").html("");
                    for (var i = 0; i < v_itemArr.length; i++) {
                        var v_item = v_itemArr[i];
                        var v_itemResult = v_cartTemplate.replace(/##image##/g, v_item.image)
                            .replace(/##productName##/g, v_item.productName)
                            .replace(/##price##/g, v_item.price)
                            .replace(/##count##/g, v_item.count)
                            .replace(/##productId##/g, v_item.productId)
                            .replace(/##subTotalPrice##/g, v_item.subTotalPrice);

                        $("#cartBody").append(v_itemResult);
                    }
                    var v_cartDivHtml = $("#cartTemplate").html();
                    var v_result = v_cartDivHtml.replace(/##totalCount##/g, v_cart.totalCount)
                        .replace(/##totalPrice##/g, v_cart.totalPrice);
                    $("#cartInfoDiv").html(v_result);
                }
            }
        })
    }
}

function logout() {
    $.ajax({
        type:"get",
        url:"http://api.shop.com:8086/members/logout",
        success:function (result) {
            if (result.code == 200) {
                // 清空cookie
                $.removeCookie("fh_token");
                // 刷新
                location.href="/";
            }
        }
    })
}