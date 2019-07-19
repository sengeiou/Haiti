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

		if ( "${param.checked}".length <= 0 ) {

			home();
		} else {

			$("#wrap").show();
			$("#name").focus();
		}
	});

    var next = function() {

        if ($("#name").val().length <= 0) {

            //alert("<fmt:message key='aimir.alert.insertName'/>");

    		//$("#name").focus();

            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.name'/>",
                msg: "<fmt:message key='aimir.alert.insertName'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("name").focus()},
                icon: Ext.MessageBox.WARNING
            });
            
            return;
        }
/*
        if ($("#number").val().length != 13) {

            //alert("<fmt:message key='aimir.hems.alert.inputPucNumber'/>");

    		//$("#number").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.pucNumber'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPucNumber'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("number").focus()},
                icon: Ext.MessageBox.WARNING
            });
            return;
        }
*/
        //if (JuminCheck($("#number").val())){
        if (true){            
           
            var params = {
                "number" : $("#number").val()
            };

            $.getJSON('${ctx}/gadget/system/membership/checkPucNumber.do', params,
            	function(result) {

                	//if (result.status == true) {
                	if (true) {	

                    	//alert("<fmt:message key='aimir.hems.alert.possibleRegist'/>");
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.hems.alert.possibleRegist'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() {                        
                                $("#number1").val($("#number").val().substring(0,6));
                                $("#second").attr("action", "${ctx}/gadget/system/membership/membershipCreate3.jsp");
                                $("#second").submit();},
                            icon: Ext.MessageBox.INFO
                        });
                        //$("#number1").val($("#number").val().substring(0,6));
                        //$("#second").attr("action", "${ctx}/gadget/system/membership/membershipCreate3.jsp");
                        //$("#second").submit();
                	} else {

                    	//alert("<fmt:message key='aimir.hems.alert.impossibleRegist'/>");
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.hems.alert.impossibleRegist'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() {},
                            icon: Ext.MessageBox.ERROR
                        });
                	}
                	
                    return;
            	}
            );
           
        } else {
            
            //alert("<fmt:message key='aimir.hems.alert.searchPucNumbes'/>");
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.name'/>",
                msg: "<fmt:message key='aimir.hems.alert.searchPucNumbes'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("number").focus()},
                icon: Ext.MessageBox.WARNING
            });
            return;
        }
 
    };

    var JuminCheck = function(jumin){
        
    	check = false;
    	total = 0;
    	temp = new Array(13);

    	for(i=0; i<13; i++) {
        	
    		temp[i] = parseInt(jumin.charAt(i));
    	}
    	
    	for(i=0; i<12; i++){
        	
    		k = i + 2;
    		
    		if(k >= 10) {
        		
    			k = k % 10 + 2;
    		}
    		
    		total = total + temp[i] * k;
    	}
    	
    	mm = "" + temp[2] + "" + temp[3];
    	dd = "" + temp[4] + "" + temp[5];

    	totalmod = total % 11;

    	chd = 11 - totalmod;

    	if ((chd + "").length == 2) {
        	
    		chd = chd - 10;
    	}
    	
    	if(chd == temp[12] && mm < 13 && dd < 32) {
        	
    		check = true;
    	}
    	
    	return check;
    };

    var home = function() {
        
		$("#second").attr("action", "${ctx}/customer/login.jsp");
   		$("#second").submit();
    };

    var intValue = function(input) {
        
    	if (isNaN($("#" + input).val())) {
        	
        	//alert("<fmt:message key='aimir.alert.onlydigit'/>");
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.pucNumber'/>",
                msg: "<fmt:message key='aimir.alert.onlydigit'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("number").focus()},
                icon: Ext.MessageBox.WARNING
            });
        	$("#" + input).val("");

        	$("#" + input).focus();
    	} 
    };
</script>
</head>

<body>
<form name="second" id="second" method="post" >
	<input type="hidden" name="checked" id="checked" value="${param.checked}"></input>
	<input type="hidden" name="number1" id="number1" value=""></input>
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
				             <span>01. <fmt:message key='aimir.hems.label.usedMemberText'/></span>
				             <span class="icon_arrow"></span>
				             <span class="on">02. <fmt:message key='aimir.hems.label.checkMemberName'/></span>
				             <span class="icon_arrow"></span>
				             <span>03. <fmt:message key='aimir.hems.label.insertMemberInf'/></span>
				           </div>
				           <!--//step-->
				           <!--confirm-->
				           <div class="margin_t30">
				               <label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.checkMemberName'/></label>
				               <div class="margin_t10 blue_box">
				               	<div class="confirm_box">
				                    <div class="float_left">
										<ul>
											<li class="confirm_title"><fmt:message key='aimir.hems.label.name'/></li>
											<li class="confirm_title"><fmt:message key='aimir.hems.label.pucNumber'/></li>
										</ul>
				                    </div>
				                    <div class="float_left">
										<ul>
											<li class="confirm_title"><input name="name" id="name" type="text" style="IME-MODE:active" /></li>
											<li class="confirm_title"><input name="number" id="number" type="text" style="IME-MODE:disabled" onblur="javascript:intValue('number');" /></li>
										</ul>
				                    </div>
				                    <div class="float_left margin_t30 padding_t2">
				                    	<fmt:message key='aimir.hems.inform.insertNo-'/>
				                    </div>   
				                </div>
				               </div>
				           </div>
				           <!--//confirm-->
				           <!--explain-->
				           <div class="hack_margin_t20 margin_b50">
							<ul class="text_gray7">
								<li class="margin_b3">
									<span class="icon_v"></span><fmt:message key='aimir.hems.inform.explainInf1'/>
								</li>
								<li class="margin_b3">
									<span class="icon_v"></span><fmt:message key='aimir.hems.inform.explainInf2'/>
								</li>
								<li>
									<span class="icon_v"></span><fmt:message key='aimir.hems.inform.explainInf3'/>
									<br><span class="padding_l27"><fmt:message key='aimir.hems.inform.explainInf4'/></span>
								</li>
							</ul>
				           </div>
				           <!--//explain-->
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
</form>
</body>
</html>