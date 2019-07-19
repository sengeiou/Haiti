<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<style type="text/css">
html{overflow:auto !important}
</style>
<title><fmt:message key='aimir.hems.title.registration'/></title>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
	
		if ( "${param.title}".length <= 0 ) {

			home();
		} else {
	
			$("#wrap").show();
		}
	});

    var next = function() {
        
    	if ($("#check1").is(":checked") && $("#check2").is(":checked")) {

    		$("#checked").val("Y");
    		$("#first").attr("action", "${ctx}/gadget/system/membership/membershipCreate2.jsp");
	   		$("#first").submit();
    	} else {
        	
    		$("#checked").val("N");
    		
        	//alert("<fmt:message key='aimir.hems.alert.agreeChecked'/>");
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.usedMemberText'/>",
                msg: "<fmt:message key='aimir.hems.alert.agreeChecked'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {},
                icon: Ext.MessageBox.WARNING
            });
    	}
    };

    var home = function() {
        
		$("#first").attr("action", "${ctx}/customer/login.jsp");
   		$("#first").submit();
    };
</script>
</head>

<body>
<form name="first" id="first" method="post" >
	<input type="hidden" name="checked" id="checked" value="N"></input>
</form>
	<div id="wrap" style="display:none" >
		<div class="overflow_hidden">
			<a href="javascript:home();"><span class="logo_aimir"></span></a>
		</div>
		<div class="bigbox_black">
			<div class="top_roundboxbg" >
				<!--top-->
				<div class="top_roundbox">
				    <div class="top_contentbox">
				    	<!-- tab -->
						<div class="hm_tab padding_t30">
							<ul>
								<li class="current"><fmt:message key='aimir.hems.label.registMember'/></li>
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
		                
		                    <!--step-->
		                    <div class="overflow_hidden step">
	                            <span class="on">01. <fmt:message key='aimir.hems.label.usedMemberText'/></span>
	                            <span class="icon_arrow"></span>
	                            <span>02. <fmt:message key='aimir.hems.label.checkMemberName'/></span>
	                            <span class="icon_arrow"></span>
	                            <span>03. <fmt:message key='aimir.hems.label.insertMemberInf'/></span>
		                    </div>
		                    <!--//step-->
		                    
		                    <!--agreement 1-->
		                    <div class="margin_t30">
		                        <label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.usedMemberText'/></label>
		                        <div class="margin_t10 agree_box">
		                        	<div class="margin_20">
		                        		<fmt:message key='aimir.hems.text.usedMemberText'/>
		                            </div>
		                        </div>
		                        <div class="float_right">
		                        	<span class="hack_margin_t3"><input id="check1" type="checkbox" title=""  class="hems_check"></span> 
		                            <span class="margin_t2 text_gray7"><fmt:message key='aimir.hems.inform.agreeUsedMemberText'/></span>
		                        </div>
		                    </div>
		                    <!--//agreement 1-->
		                    
		                    <!--agreement 2-->
		                    <div class="margin_t30 clear">
		                        <label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.collectedInf'/></label>
		                        <div class="margin_t10 agree_box">
		                            <div class="margin_20">
		                            	<fmt:message key='aimir.hems.text.collectedInf'/>
		                            </div>
		                        </div>
		                        <div class="float_right">
		                        	<span class="hack_margin_t3"><input id="check2" type="checkbox" title=""  class="hems_check"></span>
		                            <span class="margin_t2 text_gray7"><fmt:message key='aimir.hems.inform.agreeCollectedInf'/></span>
		                        </div>
		                    </div>
		                    <!--//agreement 2-->
		                    
		                    <!--button-->
		                    <div class="margin_t50 align_center">
		                    	<em class="hm_button_bold"><a href="javascript:next();"><fmt:message key='aimir.hems.label.nextLevel'/></a></em>
		                        <em class="hm_button margin_l3"><a href="javascript:home();"><fmt:message key='aimir.hems.label.cancelRegist'/></a></em>
		                    </div>
		                    <!--//button-->
		                    
		                </div>
		            </div>
		        </div>
		        <!--//Content-->
			</div>
		</div>
	</div>  
</body>
</html>