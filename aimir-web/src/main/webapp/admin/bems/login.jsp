<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">    
    <title>Jeju ICC Operator Login</title>
    <style type="text/css">

	body {		
		margin: 0px;
		padding: 0px;
		font-size: 12px;
		font-family: dotum,'돋움', Arial, Tahoma, Helvetica;
		overflow: hidden;
	}

	ul {
		list-style: none;
		padding: 0px;
		margin: 0px;
	}

	.login-outer {
		position: relative;
		width: 100%;
		margin: 0 auto;
		background: url('${ctx}/admin/bems/images/BG.gif') repeat-x;
	}
	.login-inner {
		position: relative;
		width: 1145px;
		height: 720px;
		margin: 0 auto;
		background: url('${ctx}/admin/bems/images/main_bg.gif') no-repeat;
	}
		.login-input {
			position: absolute;
			top: 260px;
			right: 100px;
			width: 436px;
			height: 203px;
		}
		.login-icc-logo {
			top: 200px;
			background: url('${ctx}/admin/bems/images/icc logo.gif') no-repeat top center;	
		}
		.login-input-outer-bg {
			opacity: 0.2;
			filter: alpha(opacity=20);
			z-index: 1;
			background: url('${ctx}/admin/bems/images/login_bg.gif') no-repeat;
		}
		.login-input-outer {
			z-index: 9999;
		}
		.login-input-inner {
			margin: 0 auto;
			width: 87%;
			height: 60px;
			padding: 20px;			
			/*padding-top: 45px;*/
		}
			.login-text {
				position: relative;
				background: url('${ctx}/admin/bems/images/login_line.gif') no-repeat bottom;
				height: 25px; 
				margin-bottom: 10px;
			}
			.login-input-inner ul {
				width: 73%;
				height: 67px;
				float: left;
			}			
				.login-input-inner ul li {
					margin: 5px 0px;
				}
				.login-input-inner ul input {
					padding-left: 3%;
					width: 95%;
				}
				.login-input-inner ul input.checkbox {
					position: relative;
					top: 1px;
					width: 15px;					
				}
				.login-input-inner ul .check-remember-me-wrapper {
					padding: 0px;
					color: #FFF;
					font-weight: bold;
					text-align: right;
					font-size: 14px;
					line-height: 20px;
				}
			.login-input-inner div.login-btn {
				width: 22%;
				height: 67px;
				float: right;
				background: url('${ctx}/admin/bems/images/button_out.gif') no-repeat;
			}
			.login-input-inner div.login-btn:hover {
				background: url('${ctx}/admin/bems/images/button_over.gif') no-repeat;
			}
			.login-aimir-logo {
				clear: both; 
				position: absolute; 
				bottom: 10px; 
				text-align: center; 
				opacity: 0.7; 
				filter: alpha(opacity=70);
				padding-left: 110px;
				margin: 0 auto;
			}
			.login-aimir-copyright {
				position: relative;
				top: 80%;
				text-align: center;			
				opacity: 0.7; 
				filter: alpha(opacity=70);
			}

			/* placeholder CSS */
			::-webkit-input-placeholder {
				font-style: italic;
				color: #CCC;
			}
			::-moz-placeholder {
				font-style: italic;
				color: #CCC;
			}
			::-ms-input-placeholder {
				font-style: italic;
				color: #CCC;
			}

    </style>

</head>
<body>
<form name="login" method="post">
	<div class="login-outer">
		<div class="login-inner">						
			<div class="login-input login-input-outer-bg"></div>
			<div class="login-input login-icc-logo"></div>
			<div class="login-input login-input-outer">								
				<div class="login-input-inner">									
					<div class="login-text">						
						<img src="${ctx}/admin/bems/images/login_text.gif" alt="login"/>
					</div>
					<ul>						
						<li><input name="accountid" type="text" placeholder="id" /></li>
						<li><input name="password" type="password" placeholder="password"/></li>
						<li class="check-remember-me-wrapper">							
							<input id="remember-me" name="remember" 
								class="checkbox" type="checkbox" />
							<label for="remember-me">Remember me</label>
						</li>
					</ul>
					<div class="login-btn"></div>
					<div class="login-aimir-logo" style="">
						<img src="${ctx}/admin/bems/images/bems_logo.gif" />
					</div>
				</div>
			</div>					
			<div class="login-aimir-copyright">
				<img src="${ctx}/admin/bems/images/copyright.gif" 
					alt="Copyright ⓒ 2012 NURI Telecom Co. Ltd. All Right Reserved" />
			</div>	
		</div>				
	</div>
</form>
</body>

<script type="text/javascript" src="${ctx}/js/require-jquery.js"></script>
<script type="text/javascript"> 

// async jQuery module load
require(["jquery"], function($) {
	
	var $window = $(window); // 윈도우 jQuery Extend

	var $loginOuter = $("div.login-outer"); // 로그인 아우터 영역 jQuery Extend

	var $form = $("form[name='login']"); // 로그인 폼 jQuery Extend
	var $accountid = $form.find("input[name=accountid]"); // 계정 아이디 jQuery Extend
	var $password = $form.find("input[name=password]"); // 계정 PW jQuery Extend
	var $rememberMe = $form.find("#remember-me"); // 아이디 기억 jQuery Extend
	var $loginButton = $form.find("div.login-btn"); // 로그인 Button jQuery Extend

	var preImageload = function() {

	};

	var adjustScreen = function(e) {
		var m = ($window.height() - $loginOuter.height()) / 2;
		$loginOuter.css("margin-top", ((m > 0) ? m : 0) + "px");
	};

	function keyEvtHandler(e) {
		var keycode = e.keyCode;
		if(keycode == 13) {
			loginSubmit();
		}
	};
	
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

	var loginSubmit = function(e) {

		if (document.login.remember.checked) {
			register(document.login.accountid.value);
		}		
		var lang = getBrowserLg();
		$.ajax({
			type: 'POST',
			url: '${ctx}/admin/login.do',
			data: {
				accountid: $accountid.val(),
				password: $password.val(),
				lang	: lang
			},
			success: showResult,
			dataType: 'json'
		});
	};

	var showResult = function(responseText, status) {
		if (responseText.errors && responseText.errors.errorCount > 0) {
			var i, fieldErrors = responseText.errors.fieldErrors;
			for (i=0 ; i < fieldErrors.length; i++) {
				var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
				$(temp).val(''+fieldErrors[i].defaultMessage);
			}
		} 
		else {
			var urll = '${url}';
			var port = '${localPort }';
			var ctx ='${ctx}';
			var strr = urll.split(ctx);
			urll = strr[0]+ctx+'/gadget/index.jsp';
			document.location.href =urll;
			return;
		}
	};

	//쿠키 읽기
	var readCookie = function(key) {
		var cookie = document.cookie;
		var first = cookie.indexOf(key+"=");
		if(first >= 0) {
			var str = cookie.substring(first, cookie.length);
			var last = str.indexOf(";");
			if(last<0) last = str.length;
			str = str.substring(0, last).split("=");
			return unescape(str[1]);
		} 
		else {
			return null;
		}
	};

	var register = function(cookieValue) {
		var today = new Date();
		var expires = new Date();
		expires.setTime(today.getTime() + 1000*60*60*24*365);
		setCookie("accountid", cookieValue, expires);
	};
		

	var saveChkClick = function() {
		  var today = new Date();
		  var expires = new Date();
		  expires.setTime(today.getTime() + 1000*60*60*24*365);
		  setCookie("accountid", "", expires);
	};

	var setCookie = function(name, value, expire) {
		  document.cookie = name + "=" + escape(value) + ((expire == null) ? 
		  		"" : ("; expires=" + expire.toGMTString()));
	};
	 
	var getCookie = function(Name) {
		var search = Name + "=";
		if (document.cookie.length > 0) { 
			offset = document.cookie.indexOf(search);
			if (offset != -1) {
				offset += search.length;
				end = document.cookie.indexOf(";", offset);
				if (end == -1) {
				   end = document.cookie.length;
				}
				return unescape(document.cookie.substring(offset, end));
			}
		}
	};

	var eventBind = function() {

		// 상하좌우 중앙 픽스
		$window.resize(adjustScreen);

		// 패스워드 키 다운
		$password.keydown(keyEvtHandler);

		// 로그인 
		$loginButton.click(loginSubmit);

		// Remember Me
		$rememberMe.click(saveChkClick);
	};

	var initialize = function() {

		eventBind();

		$window.trigger("resize");

		var userId = getCookie("accountid");

		if (userId != null) {
			$accountid.val(userId);
			$password.focus();
			$rememberMe.attr("checked", true);
		} 
		else {
			$accountid.focus();
		}

	};

	// 페이지 초기화.
	initialize();
	
});
</script>
</html>