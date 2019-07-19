<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>AiMiR <fmt:message key='aimir.version'/> Operator Login</title>
    <link href="${ctx}/css/login.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tabs.css" rel="stylesheet"  type="text/css" media="print, projection, screen">
    <link rel="icon" type="image/png" href="${ctx}/images/favicon2.ico" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">
    
    function keyEvtHandler(e) {
    // var e = window.event ||window.Event;
    var event = e || window.event;
    var keycode = event.keyCode;
        if (keycode == 13) {      //enter
            loginSubmit();
        } else {
            return false;
        }
    }
    
    function getBrowserLg() {
    	var lang = "un"; //언어 값 받아올 변수. un은 undefined 의 앞 2글자.
    	 
    	 if (navigator.language!=null) //크롬이나 Firefox일 경우
    	 {
    	     lang = navigator.language;
    	 } else if (navigator.userLanguage!=null) { //IE의 경우
    	     lang = navigator.userLanguage;
    	 } else if (navigator.systemLanguage!=null) {
    	     lang = navigator.systemLanguage;
    	 } else { //이도저도 아니면
    	     lang="en";
    	 }
    	 
    	 lang = lang.toLowerCase(); //받아온 값을 소문자로 변경
    	 lang = lang.substring(0, 2); //소문자로 변경한 갚의 앞 2글자만 받아오기
		
    	 return lang;
    }

    function loginSubmit() {
    	if(!checkInput()){
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.enter.idPass'/>");
    		return;
    	}
    	
        if (document.login.remember.checked) {
            register(document.login.accountid.value);
        } else {
            var today = new Date();
            var expires = new Date();
            expires.setTime(today.getTime() - 1000*60*60*24*365);
            setCookie("accountid", document.login.accountid.value, expires);
        }
        
        var lang = getBrowserLg();

        var options = {
            success : showResult,
            url : '${ctx}/admin/login.do',
            type : 'post',
            data : {'lang': lang},
            datatype : 'json'
        };
        
        $('#login').ajaxSubmit(options);
    }

    function checkInput() {
    	var loginId = document.login.accountid.value,
    	    passwd = document.login.password.value;
    	if(loginId.length == 0){  // id 미입력
    		document.login.accountid.focus();
    		return false;
    	}
    	
    	if(passwd.length == 0){ // 패스워드 미입력
    		document.login.password.focus();
    		return false;
    	}
    	return true;
    }
    
    function onloadFirst() {
        var port = '${localPort}';
        if (port != 8443) {
            setCookie("myPort", port);
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',document.cookie);
        }
    }

    function showResult(responseText, status) {
        //  alert(responseText.result);
        
        if(responseText.result != "success") {
        	 Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
        	 document.login.accountid.value = "";
     		 document.login.password.value = "";
     		 document.login.accountid.focus();
        	 return;
        }
        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i = 0; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else {
            var urll = '${url}';
            var port = '${localPort }';
            var ctx ='${ctx}';
            var strr = urll.split(ctx);
            urll = strr[0]+ctx+'/gadget/index.jsp';
            //alert(urll);
            document.location.href =urll;
            return;
        }
    }

    //쿠키 읽기
    function readCookie(key) {
        var cookie = document.cookie;
        var first = cookie.indexOf(key+"=");
        if (first >= 0) {
            var str = cookie.substring(first, cookie.length);
            var last = str.indexOf(";");
            if (last<0) last = str.length;
            str = str.substring(0, last).split("=");
            return unescape(str[1]);
        } else {
            return null;
        }
    }

    function register(cookieValue) {
        var today = new Date();
        var expires = new Date();
        expires.setTime(today.getTime() + 1000*60*60*24*365);
        setCookie("accountid", cookieValue, expires);
    }

    /* function saveChkClick() {
          //var today = new Date();
          //var expires = new Date();
          //expires.setTime(today.getTime() + 1000*60*60*24*365);
          //setCookie("accountid", "", expires);
          if (document.login.remember.checked) {
              var today = new Date();
              var expires = new Date();
              expires.setTime(today.getTime() + 1000*60*60*24*365);
              setCookie("accountid", "", expires);
          } else {
              setCookie("accountid", null);
          }
    } */

    function setCookie(name, value, expire) {
        document.cookie = name + "=" + escape(value) + ((expire == null) ? "" : ("; expires=" + expire.toGMTString()));
    }

    function getCookie(Name) {
        var search = Name + "=";
        if (document.cookie.length > 0) {                    // if there are any cookies
            offset = document.cookie.indexOf(search);
            if (offset != -1) {                                // if cookie exists
                offset += search.length;                       // set index of beginning of value
                end = document.cookie.indexOf(";", offset);    // set index of end of cookie value
                if (end == -1)
                    end = document.cookie.length;
                return unescape(document.cookie.substring(offset, end));
            }
        }
    }

    </script>
</head>
<body>
<form name="login" id="login" method="post" onsubmit="return false">
<div id="logcom_body">
    <div id="logcus_ff" style="display:inline;">
    <ul>
        <li class="floatleft" style="display:inline;left:30px;height:68px;"><span id="logcus_ff_version">VER <fmt:message key='aimir.version'/></span></li>
        <li class="floatleft" style="display:inline;width:400px;">

            <div class="floatleft" style="width:280px;display:block;">
                <div class="login_nuri2">
                    <span class="text_graybold11px margin-t7px width-35px">ID</span>
                    <span class="log_f_left"></span>
                    <span class="log_f_bg"><input name="accountid" type="text" class="f_modi_v12 f_modi_cg" /></span>
                    <span class="log_f_right"></span>
                </div>
                <div class="login_pw_nuri2">
                    <span class="text_graybold11px margin-t7px width-35px">PW</span>
                    <span class="log_f_left"></span>
                    <span class="log_f_bg"><input name="password" type="password" class="f_modi_v12 f_modi_cg" onkeydown="javascript:keyEvtHandler(event);" /></span>
                    <span class="log_f_right"></span>
                </div>
            </div>
            <div class="login_nuri_btn"><a href="javascript:;" onClick="javascript:loginSubmit();" id="loginSubmit">login</a></div>
        </li>

        <li>
            <div class="f_modi_cb f_modi_v10 margin-t15px" style="float:left;width:200px">
                <span><input name="remember" type="checkbox" value="" class="checkbox"/></span>
                <span class="text_bluegreen2">Remember me</span>
            </div>
        </li>
    </ul>
    </div>
</div>
</form>
</body>
<SCRIPT LANGUAGE="JavaScript">
<!--
    var userId = getCookie("accountid") ;

    if (userId != null) {
        document.login.accountid.value = userId;
        document.login.password.focus();
        document.login.remember.checked = true;
    } else {
        document.login.accountid.focus();
    }
//-->
</SCRIPT>

</html>