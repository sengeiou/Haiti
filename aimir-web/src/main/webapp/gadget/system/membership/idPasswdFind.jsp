<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title><fmt:message key='aimir.hems.title.findIdPass'/></title>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		if ( "${param.title}".length <= 0 ) {
			
			login();
		} else {
		
			$("#wrap").show();
			$("#findId").show();
			$("#searchId").hide();
			$("#findPw").show();
			$("#searchPw").hide();
		}
	});

	var findId = function() {

		if ($("#findIdName").val().length <= 0) {

			//alert("<fmt:message key='aimir.alert.insertName'/>");
			
    		//$("#findIdName").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.alert.insertName'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("findIdName").focus();},
                icon: Ext.MessageBox.WARNING
            });           
			return;
		} else if ($("#findIdNumber").val().length <= 0) {

			//alert("<fmt:message key='aimir.hems.alert.inputPucNumber'/>");
			
    		//$("#findIdNumber").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPucNumber'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("findIdNumber").focus();},
                icon: Ext.MessageBox.WARNING
            });            
			return;
		}

		$.post("${ctx}/gadget/system/membership/findId.do",
				$("#findIdForm").serialize(),
				function(result, status) {

					if (result.status == true) {

						$("#resultId").val(result.loginId);
						
						$("#findId").hide();
						$("#searchId").show();
					} else {

						//alert("<fmt:message key='aimir.hems.alert.notExistId'/>");
						Ext.MessageBox.buttonText.yes = "<fmt:message key='aimir.hems.label.registMember'/>"
                        Ext.MessageBox.buttonText.no = "<fmt:message key='aimir.ok'/>"
			            Ext.MessageBox.show({
			                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
			                msg: "<fmt:message key='aimir.hems.alert.notExistId'/>",
			                buttons: Ext.MessageBox.YESNO,
			                minWidth:300,
			                fn: login,
			                icon: Ext.MessageBox.INFO
			            });
					}
				},
				"json"
			);
	};

	var login = function(btn) {
		if ( btn != "no" ) {
			  $("#findIdForm").attr("action", "${ctx}/customer/login.jsp");
			   $("#findIdForm").submit();
		}
	};

	var membershipCreate = function() {
		
		$("#findIdForm").attr("action", "${ctx}/gadget/system/membership/membershipCreate.jsp");
   		$("#findIdForm").submit();
	};

	var findPw = function() {

		if ($("#findPwId").val().length <= 0) {

			//alert("<fmt:message key='aimir.alert.inputid'/>");
			
			//$("#findPwId").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.alert.inputid'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("findPwId").focus();},
                icon: Ext.MessageBox.WARNING
            });
			return;
		} else if ($("#findPwName").val().length <= 0) {

			//alert("<fmt:message key='aimir.alert.insertName'/>");
			
    		//$("#findPwName").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.alert.insertName'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("findPwName").focus();},
                icon: Ext.MessageBox.WARNING
            });           
			return;
		} else if ($("#findPwNumber").val().length <= 0) {

			//alert("<fmt:message key='aimir.hems.alert.inputPucNumber'/>");
			
    		//$("#findPwNumber").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPucNumber'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("findPwNumber").focus();},
                icon: Ext.MessageBox.WARNING
            });            
			return;
		}
		
		$.post("${ctx}/gadget/system/membership/findPw.do",
				$("#findPwForm").serialize(),
				function(result, status) {

					if (result.status == true) {

						var text = "";

						if ($("input:radio[name='sendType']:checked").val() == "1") {
							
							text = "<fmt:message key='aimir.hems.alert.sendEmail'/>";
						} else {
							
							text = "<fmt:message key='aimir.hems.alert.sendCell'/>";
						}

				    	contractHtml = text + "<div>" + result.password + "</div>";
						 
						$(contractHtml).appendTo("#resultPw");

						$("#findPw").hide();
						$("#searchPw").show();
					} else {

						//alert("<fmt:message key='aimir.hems.alert.notExistInformation'/>");
			            Ext.MessageBox.show({
			                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
			                msg: "<fmt:message key='aimir.hems.alert.notExistInformation'/>",
			                buttons: Ext.MessageBox.OK,
			                minWidth:300,
			                fn: function() {},
			                icon: Ext.MessageBox.WARNING
			            });   
					}
				},
				"json"
			);
	};
	
    var intValue = function(input) {
        
    	if (isNaN($("#" + input).val())) {
        	
        	//alert("<fmt:message key='aimir.alert.onlydigit'/>");
        	
        	//$("#" + input).focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.findIdPw'/>",
                msg: "<fmt:message key='aimir.alert.onlydigit'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {
                    Ext.get(input).focus();
                    $("#" + input).val("");  },
                icon: Ext.MessageBox.WARNING
            });
    	
    	} 
    };

    var checked = function(name, value) {

        $("input:radio[name=" + name + "]").filter("input:radio[value=" + value + "]").attr("checked", "checked");
    };

</script>
</head>

<body>
	<div id="wrap" style="display:none" >
		<div class="overflow_hidden">
			<a href="javascript:login();"><span class="logo_aimir"></span></a>
		</div>
		<div class="bigbox_black">
			<div class="top_roundboxbg" >
				<!--top-->
				<div class="top_roundbox">
					<div class="top_contentbox">
					
						<!-- tab -->
						<div class="hm_tab padding_t30">
						  	<ul>
						  		<li class="current"><fmt:message key='aimir.hems.label.findIdPw'/></li>
						  	</ul>
						</div>
						<!--// tab -->
						
				 	</div>
				</div>
		     	<!--// top-->
	        	
				<!--Content-->
				<div class="roundbox">
				    <div class="contents">
				        <div class="margin_10">
				        	<div class="overflow_hidden margin_t30 margin_b50">
				        	
				        		<!--find id -->
				        		<form name="findIdForm" id="findIdForm" method="post" >
								<input type="hidden" name="title" id="title" value="HEMS" />
					        	<div class="w_50 float_left" id="findId">
					        		<div class="margin_b10">
					        			<label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.findId'/></label>
					        		</div>
					        		<!--id box-->
					        		<div class="blue_box margin_r20">
					        			<div class="login_box">
											<div class="float_left">
												<ul>
													<li class="confirm_title"><fmt:message key='aimir.hems.label.name'/></li>
													<li class="confirm_title"><b><fmt:message key='aimir.hems.label.onlyPucNumber'/></b></li>
												</ul>
											</div>
											<div class="float_left">
												<ul>
													<li class="confirm_title"><input type="text" name="findIdName" id="findIdName" style="width:160px"/></li>
													<li class="confirm_title"><input type="text" name="findIdNumber" id="findIdNumber" style="width:160px;IME-MODE:disabled;" onblur="javascript:intValue('findIdNumber');" /></li>
												</ul>
											</div>
											<div class="refer"><fmt:message key='aimir.hems.inform.findText'/></div>
										</div>
					        		</div>
					        		<!--id box-->
					        		<!--button-->
						            <div class="hack_margin_t20 align_center">
										<em class="hm_button_bold"><a href="javascript:findId();"><fmt:message key='aimir.hems.label.findId'/></a></em>
									</div>
						           <!--//button-->
					        	</div>
					        	</form>
					        	<!--//find id -->
					        	
					        	<!--result id -->
					        	<div class="w_50 float_left" id="searchId">
					        		<div class="margin_b10">
					        			<label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.findId'/></label>
					        		</div>
					        		<!--id box-->
					        		<div class="blue_box margin_r20">
					        			<div class="login_box">
					        				<div class="margin_b30 padding_t20">
					        					<fmt:message key='aimir.hems.inform.resultId'/>
					        					<input type="text" name="resultId" id="resultId" class="input_result" />
					        				</div>
					        				<div class="refer"><fmt:message key='aimir.hems.inform.resultLogin'/></div>
										</div>
					        		</div>
					        		<!--id box-->
					        		<!--button-->
						            <div class="hack_margin_t20 align_center">
										<em class="hm_button_bold"><a href="javascript:login();"><fmt:message key='aimir.hems.label.login'/></a></em>
									</div>
						           <!--//button-->
					        	</div>
					        	<!--//result id -->
					        	
					        	<!-- find pw -->
				        		<form name="findPwForm" id="findPwForm" method="post" >
					        	<div class="w_50 float_left centerline" id="findPw" >
					        		<div class="margin_b10 margin_l20">
					        			<label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.findPw'/></label>
					        		</div>
					        		<!--pw box-->
					        		<div class="blue_box margin_l20">
					        			<div class="login_box">
											<div class="float_left">
												<ul>
													<li class="confirm_title"><b><fmt:message key='aimir.id'/></b></li>
													<li class="confirm_title"><fmt:message key='aimir.hems.label.name'/></li>
													<li class="confirm_title"><b><fmt:message key='aimir.hems.label.onlyPucNumber'/></b></li>
													<li class="confirm_title"><b><fmt:message key='aimir.hems.choiceReceive'/></b></li>
												</ul>
											</div>
											<div class="float_left">
												<ul>
													<li class="confirm_title"><input type="text" name="findPwId" id="findPwId" style="width:160px;IME-MODE:disabled;"/></li>
													<li class="confirm_title"><input type="text" name="findPwName" id="findPwName" style="width:160px"/></li>
													<li class="confirm_title"><input type="text" name="findPwNumber" id="findPwNumber" style="width:160px;IME-MODE:disabled;" onblur="javascript:intValue('findPwNumber');" /></li>
													<li class="confirm_title hack_align_left">
													<div class="link">
														<a href="javascript:checked('sendType', '1');" >
															<span><input type="radio" name="sendType" value="1" class="radio" checked></span>
															<span class="padding_r10"><fmt:message key='aimir.email'/></span>
														</a>
														<a href="javascript:checked('sendType', '2');" >
															<span><input type="radio" name="sendType" value="2" class="radio"></span>
															<span><fmt:message key='aimir.hems.label.sms'/></span>
														</a>
													</div>
													</li>
												</ul>
											</div>
											<div class="refer"><fmt:message key='aimir.hems.inform.findText'/></div>
										</div>
					        		</div>
					        		<!--pw box-->
					        		<!--button-->
						            <div class="hack_margin_t20 align_center">
										<em class="hm_button_bold"><a href="javascript:findPw();"><fmt:message key='aimir.hems.label.findPw'/></a></em>
									</div>
						           <!--//button-->
					        	</div>
					        	</form>
					        	<!-- //find pw -->
					        	
					        	<!--result pw -->
					        	<div class="w_50 float_left centerline" id="searchPw" >
					        		<div class="margin_b10 margin_l20">
					        			<label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.findPw'/></label>
					        		</div>
					        		<!--pw box-->
					        		<div class="blue_box margin_l20">
					        			<div class="login_box">
					        				<div class="margin_b30 padding_t20" id="resultPw" >
					        				</div>
											<div class="refer"><fmt:message key='aimir.hems.inform.resultLogin'/></div>
										</div>
					        		</div>
					        		<!--pw box-->
					        		<!--button-->
						            <div class="hack_margin_t20 align_center">
										<em class="hm_button_bold"><a href="javascript:login();"><fmt:message key='aimir.hems.label.login'/></a></em>
									</div>
						           <!--//button-->
					        	</div>
					        	<!--//result pw -->
					        	
					        </div>
				        </div>
				    </div>
				</div>
				<!--//Content-->
			</div>
		</div>
	</div>  
</body>
</html>