<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key='aimir.hems.title.login'/></title>
    <link href="${ctx}/css/login.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tabs.css" rel="stylesheet"  type="text/css" media="print, projection, screen">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
    <script type="text/javascript" charset="utf-8">

    function keyEvtHandler(e){

    	var event = e || window.event;
    	var keycode = event.keyCode;

        if(keycode == 13){      //enter
            
            loginSubmit();
        }else {
            
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

    function loginSubmit(){

        if ( $("#accountid").val().length <= 0 ) {
            
           // alert("<fmt:message key='aimir.alert.inputid'/>");
            
           // $("#accountid").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.login.login'/>",
                msg: "<fmt:message key='aimir.alert.inputid'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("accountid").focus()},
                icon: Ext.MessageBox.WARNING
            });
			return;
        }
        
        if ( $("#password").val().length <= 0 ) {

        	//alert("<fmt:message key='aimir.hems.alert.inputPassword'/>");
            
            //$("#password").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.login.login'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPassword'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("password").focus()},
                icon: Ext.MessageBox.WARNING
            });
            return;
        }
                
		if (document.hemsLogin.remember.checked) {
			
			register(document.hemsLogin.accountid.value);
		}
		
		var lang = getBrowserLg();
		
        var options = {
                
            success : showResult,
            url : '${ctx}/customer/login.do',
            type : 'post',
            data : {'lang' : lang},
            datatype : 'json'
        };
        
        $('#hemsLogin').ajaxSubmit(options);
    }

    function onloadFirst(){
        
        var port = '${localPort}';

        if(port !=8443){
            
            setCookie("myPort", port);
            //alert(document.cookie);
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.login.login'/>",
                msg: document.cookie,
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {},
                icon: Ext.MessageBox.WARNING
            });
        }
    }

    function showResult(responseText, status) {

        if (responseText.errors && responseText.errors.errorCount > 0) {

        	var i, fieldErrors = responseText.errors.fieldErrors;
        	
            for (i=0 ; i < fieldErrors.length; i++) {
                
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else if (responseText.result != "success") {

        	if (responseText.result != "Contract No") {
            	
	        	//alert("<fmt:message key='aimir.hems.error.loginByAccount'/>");
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.login.loginFail'/>",
                    msg: "<fmt:message key='aimir.hems.error.loginByAccount'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {},
                    icon: Ext.MessageBox.ERROR
                });	        	
	        	return;
        	} else {
                
	        	//alert("<fmt:message key='aimir.hems.error.loginByContractNo'/>");
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.login.loginFail'/>",
                    msg: "<fmt:message key='aimir.hems.error.loginByContractNo'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {},
                    icon: Ext.MessageBox.ERROR
                });	
	        	return;
            }
    	} else {

        	var urll = '${url}';
            var port = '${localPort}';
            var ctx ='${ctx}';
            var strr = urll.split(ctx);
            
            urll = strr[0]+ctx+'/gadget/index_customer.jsp';
            document.location.href =urll;
            
            return;
        }
    }

    //쿠키 읽기
    function readCookie(key) {
        
        var cookie = document.cookie;
        var first = cookie.indexOf(key+"=");
        
        if(first >= 0) {
            
            var str = cookie.substring(first, cookie.length);
            var last = str.indexOf(";");
            
            if(last<0) {
                
                 last = str.length;
            }
            
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
	  	setCookie("HemsId", cookieValue, expires);
	}
		

	function saveChkClick() {
		
		var today = new Date();
		var expires = new Date();

		expires.setTime(today.getTime() + 1000*60*60*24*365);
		setCookie("HemsId", "", expires);
	}

	function setCookie(name, value, expire) {

		document.cookie = name + "=" + escape(value) + ((expire == null) ? "" : ("; expires=" + expire.toGMTString()));
	}
	 
	function getCookie(Name) {
		
		var search = Name + "=";
		
		if (document.cookie.length > 0) {                    // if there are any cookies

			offset = document.cookie.indexOf(search);

			if (offset != -1){                                // if cookie exists
				
				offset += search.length;                       // set index of beginning of value
				end = document.cookie.indexOf(";", offset);    // set index of end of cookie value

				if (end == -1) {
					end = document.cookie.length;
				}
				
				return unescape(document.cookie.substring(offset, end));
			}
		}
	}

	var membershipCreate = function() {
		
		$("#hemsLogin").attr("action", "${ctx}/gadget/system/membership/membershipCreate.jsp");
   		$("#hemsLogin").submit();
	};

	var membershipFind = function() {

		$("#hemsLogin").attr("action", "${ctx}/gadget/system/membership/idPasswdFind.jsp");
   		$("#hemsLogin").submit();
	};

	var accountId = function() {
		$("#accountid").val("");	 
	};
	 
	var passWord = function() {
		$("#password").val("");	 
	};
	 
    </script>
</head>

<body>
<form name="hemsLogin" id="hemsLogin" method="post" onsubmit="return false">
<input type="hidden" name="title" id="title" value="HEMS" />
<div id="logcus_body">
    <div id="logcus_ff_customer">
    
	    <div class="customerlogin">
		    <dl>
		    	<dt><fmt:message key='aimir.id'/></dt>
		        <dd><input name="accountid" id="accountid" type="text" value="" onmousedown="javascript:accountId();" class="noframe" style="IME-MODE:disabled"/></dd>
		        <dt><fmt:message key='aimir.password'/></dt>
		        <dd><input name="password" id="password" type="password" value="" onmousedown="javascript:passWord();" class="noframe" onkeydown="javascript:keyEvtHandler(event);" /></dd>
		    </dl>
		    <dl class="log_btn"><em class="log_button"><a href="javascript:;" onClick="javascript:loginSubmit();" id="loginSubmit"><fmt:message key='aimir.login.login'/></a></em></dl>
	    </div>
    
	    <div class="saveid">
	    	<span><input name="remember" type="checkbox" value="" class="checkbox" onclick="javascript:saveChkClick();"/></span>
	    	<span class="text_bluegreen"><fmt:message key='aimir.hems.inform.rememberMe'/></span>
	    </div>
    
	    <div class="account_menu">
	 	    <a href="javaScript:membershipCreate();"><fmt:message key='aimir.hems.label.registMember'/></a>
	       	<span class="menu_bar"> | </span>
	       	<a href="javaScript:membershipFind()"><fmt:message key='aimir.hems.label.findIdPw'/></a>
	   	</div>
   	
   		<div class="comment"><fmt:message key='aimir.hems.inform.reqRegistration'/></div>
   
  </div>
</div> 	
    
    <!--ul>
        <li class="floatleft display_inline" style="width:400px;">

            <div class="floatleft" style="width:280px;display:block;">
                <div class="login_nuri2">
                	<span ><fmt:message key='aimir.id'/></span>
                    <span class="log_f_left"></span>
					<span class="log_f_bg"><input name="accountid" id="accountid" type="text" value="아이디111" onmousedown="javascript:accountId();" class="f_modi_v12 f_modi_cg" style="IME-MODE:disabled"/></span>
                    <span class="log_f_right"></span>
                </div>
                <div class="login_pw_nuri2">
                	<span class="text_graybold11px margin-t7px width-35px"><fmt:message key='aimir.password'/></span>
                    <span class="log_f_left"></span>
					<span class="log_f_bg"><input name="password" id="password" type="password" value="비밀번호111" onmousedown="javascript:passWord();" class="f_modi_v12 f_modi_cg" onkeydown="javascript:keyEvtHandler(event);" /></span>
                    <span class="log_f_right"></span>
                </div>
            </div>
            <div class="login_nuri_btn"><a href="javascript:;" onClick="javascript:loginSubmit();" id="loginSubmit"><fmt:message key='aimir.login.login'/></a></div>
        </li>

        <li>
            <div class="f_modi_cb f_modi_v10 margin-t10px" style="float:left;width:200px">
            <span><input name="remember" type="checkbox" value="" class="transonly" onclick="javascript:saveChkClick();"/></span>
            <span class="text_bluegreen"><fmt:message key='aimir.hems.inform.rememberMe'/></span>
            </div>
            
        </li>
        <li class="clear margin-t5px">
        	<div class="floatright">
	        	<span><a href="javaScript:membershipCreate();"><fmt:message key='aimir.hems.label.registMember'/></a></span>
	        	<span class="spaceline"> | </span>
	        	<span><a href="javaScript:membershipFind()"><fmt:message key='aimir.hems.label.findIdPw'/></a></span>
        	</div>
         </li>
        <li class="floatright padding-t7px text_gray11px"><fmt:message key='aimir.hems.inform.reqRegistration'/></li>
    </ul-->
    
</form>
</body>
<SCRIPT LANGUAGE="JavaScript"> 
<!--
	var userId = getCookie("HemsId") ;

	if (userId != null) {
		
		document.hemsLogin.accountid.value = userId;
		document.hemsLogin.password.focus();
		document.hemsLogin.remember.checked = true;
	} else {
		
		document.hemsLogin.accountid.focus();
	}
//-->
</SCRIPT>
</html>