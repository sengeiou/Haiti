<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<head>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >    
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>	
</head>
<script>

$(function(){

    $('a').click(function(event) {
        event.preventDefault();
    });

    //Update Operator
    $('#operatorUpdateForm a#updateOperator').click( function() {

        var operatorId = $("#operatorUpdateForm :hidden[name='id']").val();

     //   alert("operatorId :"+ operatorId);

        if (operatorId) {

        	var loginId = $("#operatorUpdateForm :hidden[name='loginId']").val();
        	var oldPassword = $("#operatorUpdateForm :input[name='oldPassword']").val();
            if(oldPassword==''){
                alert('<fmt:message key="aimir.operator.confirm.prevpwd" />');
            }else{
                var pwCheck = false;
	        	$.getJSON('${ctx}/gadget/system/operator/checkPassword.do?loginId='+encodeURIComponent(loginId)+'&oldPassword='+oldPassword,
	                    function(json) {
	        		        pwCheck = json.pwCheck;
	                        if ( json.pwCheck ) {
	                        	var password = $("#operatorUpdateForm :input[name='password']").val();
                                var passwordCheck = $("#operatorUpdateForm :input[name='passwordCheck']").val();

                                // 패스워드 영숫자 체크
                                if(password!='') {
                                    if ( password.match(/[^0-9A-Za-z]+/) != null){
                                        alert("<fmt:message key='aimir.chkAlphabetNumber'/>");
                                        return;
                                    } 
                                }
                                if(password==passwordCheck){
                                    if(password==''){
                                         $("#operatorUpdateForm :input[name='password']").val(oldPassword);
                                    }

                                    var options = {
                                        success : operatorUpdateResult,
                                        url : '${ctx}/gadget/system/operator/updatePersonalOperator.do',
                                        type : 'post',
                                        datatype : 'json'
                                    };

                                    $('#operatorUpdateForm').ajaxSubmit(options);
                                }else{
                                    alert('<fmt:message key="aimir.operator.confirm.newpwd" />');
                                }

	                        }else{
	                        	alert('<fmt:message key="aimir.operator.confirm.wrongprevpwd" />');
	                        }
	                    }
	             );
            }
        }

    });

    $('#operatorUpdateForm a#cancelUpdateOperator').click( function() {
    	cancelUpdateOperator();
    });

    var chk = "";

    $('#showDefaultDashboardYn_1').click( function() {

        chk = "Y";
        setChk();

    });

    $('#showDefaultDashboardYn_2').click( function() {
        chk = "N";
        setChk();

     });

    function setChk(){

        if(chk == "Y"){
            $('#showDefaultDashboardYn_1').attr("checked", "checked");
            $('#showDefaultDashboardYn_2').attr("checked", "");
        }else{
            $('#showDefaultDashboardYn_1').attr("checked", "");
            $('#showDefaultDashboardYn_2').attr("checked", "checked");
        }
    }
});

function operatorUpdateResult(responseText, status) {
	if(responseText.result =='nopermission'){
		Ext.Msg.alert('<fmt:message key='aimir.message'/>', "<fmt:message key='aimir.firmware.msg02'/>");
	}else{
		Ext.Msg.alert('<fmt:message key='aimir.message'/>', responseText.result);
	}
	
    if (responseText.errors && responseText.errors.errorCount > 0) {
        var i, fieldErrors = responseText.errors.fieldErrors;
        for (i=0 ; i < fieldErrors.length; i++) {
            var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
            $(temp).val(''+fieldErrors[i].defaultMessage);
        }
    } else {
        sendFlex('${roleId }');
        $("#operatorInfo").load("${ctx}/gadget/system/operator/detailPersonalOperator.do?operatorId=" + ${operatorId}+"&loginId="+ encodeURIComponent(loginId));
    }
    operatorTabListener();
}

function cancelUpdateOperator(){
	$("#operatorInfo").load("${ctx}/gadget/system/operator/detailPersonalOperator.do?operatorId=" + ${operatorId}+"&loginId="+ encodeURIComponent(loginId));
}

</script>

<div class="align-center	width-640px">
<form id='operatorUpdateForm' name="operatorUpdateForm" modelAttribute="operator" >

<input name="roleId" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${roleId }>
<input name="id" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${operator.id }>
<ul>
	<li class="height25px">
	
	<button type="button" class="member margin-r5"> </button>
	<label id="label-default" class="floatleft bold padding-t3px">${operator.loginId}&nbsp;</label>
	<label id="label-default1" class="floatleft bold"><fmt:message key='aimir.operator.userDetail'/>&nbsp;<fmt:message key='aimir.update'/></label>
	</li>
</ul>
		
<table class="account">
	<colgroup>
		<col width="190"/>
		<col width=""/>
	</colgroup>

        <tr class="line">
            <th><fmt:message key='aimir.id'/></th>
            <td><input type="text" readonly value="${operator.loginId}"/>
            <input name="loginId" type="hidden" value="${operator.loginId}"/>
            </td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.name.user'/></th>
            <td><input name="name" type="text" value="${operator.name}"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.alias'/></th>
            <td><input name="aliasName" type="text" value="${operator.aliasName}"/></td>
        </tr>
        <tr>
            <th><fmt:message key="aimir.operator.prevpwd"/></th>
            <td><input name="oldPassword" type="password"></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.newpassword'/></th>
            <td><input name="password" type="password"></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.confirmpassword'/></th>
            <td><input name="passwordCheck" type="password"></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.email'/></th>
            <td><input name="email" type="text" value="${operator.email}" class="width-80"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.tel.no'/></th>
            <td><input name="telNo" type="text" value="${operator.telNo}"/></td>
        </tr>
        <tr>
            <th><fmt:message key='use.default.dashboard'/></th>
            <td>
            	<input name="showDefaultDashboard" id="showDefaultDashboardYn_1" type="radio" value="1" id="" ${operator.showDefaultDashboard == 'true' ? 'checked=checked' : ''} class="transonly" >
				<input type="text" value="YES" style="width:50px;"  class="transonly">
				<input name="showDefaultDashboard" id="showDefaultDashboardYn_2" type="radio" value="0" id="" ${operator.showDefaultDashboard == 'false' ? 'checked=checked' : ''}  class="transonly">
				<input type="text" value="NO" style="width:80px;"  class="transonly">
            </td>
        </tr>
        
    </table>

    <div id="btn" class="floatright margin-t15px">
        <ul>
            <li class="on"><a href="#" id="updateOperator"><fmt:message key="aimir.save2"/></a></li>
        </ul>
        <ul>
            <li><a href="#" id="cancelUpdateOperator"><span><fmt:message key='aimir.cancel'/></span></a></li>
        </ul>
    </div>

</form>
</div>