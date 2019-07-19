<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script>

var operatorId = '';
$(function(){

    $('a').click(function(event) {
        event.preventDefault();
    });


    $('#operatorDetailForm a#openOperatorUpdateForm').click( function() {

    	operatorId = $("#operatorDetailForm :hidden[name='operator_id']").val();
    	loginId = $("#operatorDetailForm :hidden[name='login_id']").val();

    	$("#operatorInfo").load("${ctx}/gadget/system/operator/updatePersonalOperator.do?operatorId=" + operatorId+"&loginId="+ encodeURIComponent(loginId));

    });

    $('#operatorDetailForm a#operatorDelete').click( function() {
    	operatorId = $("#operatorDetailForm :hidden[name='operator_id']").val();
        function deleteOperator() {
            if(operatorId != '') {
                $.ajax({
                    url: "${ctx}/gadget/system/operator/deleteOperator.do?operatorId=" + operatorId,
                    cache: false,
                    success: operatorDeleteResult
                });
            }
        }
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

    function operatorDeleteResult(responseText, status) {
        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i=0 ; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else {
            getOperatorList();
            resetOperatorInfo();
        }
    }

});

</script>

<div class="align-center	width-640px">
    <form id="operatorDetailForm">
    <input name="operator_id" type="hidden" style="width:150px; padding:2px" value="${operator.id}">
    <input name="login_id" type="hidden" style="width:150px; padding:2px" value="${operator.loginId}">

    <ul>
		<li  class="height25px">
			<button type="button" class="member margin-r5"> </button> 
			<label id="label-default" class="floatleft bold padding-t3px">${operator.loginId}</label>
			<label id="label-default1" class="floatleft bold">&nbsp;<fmt:message key='aimir.operator.userDetail'/></label>
		</li>
	</ul>

    <table class="account">
	
	<colgroup>
		<col width="190"/>
		<col width=""/>
	</colgroup>

        <tr class="line">
            <th><fmt:message key='aimir.id'/></th>
            <td><input type="text" readonly value="${operator.loginId}" class="width-50 transonly"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.name.user'/></th>
            <td><input type="text" readonly value="${operator.name}" class="width-50 transonly"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.alias'/></th>
            <td><input type="text" readonly value="${operator.aliasName}"  class="width-50 transonly"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.email'/></th>
            <td><input type="text" readonly value="${operator.email}"/  class="width-80 transonly"></td>
        </tr>
            <tr>
            <th><fmt:message key='aimir.tel.no'/></th>
            <td><input type="text" readonly value="${operator.telNo}" class="transonly"/></td>
        </tr>
        <tr>
            <th><fmt:message key='aimir.lastmodifiedtime.password'/></th>
            <td><input type="text" readonly value="${operator.lastPasswordChangeTimeLocale}" class="transonly"/></td>
        </tr>
        <tr>
            <th><fmt:message key='use.default.dashboard'/></th>
            <td>
            	<input readonly name="showDefaultDashboard" id="showDefaultDashboardYn_1" type="radio" value="1" ${operator.showDefaultDashboard == 'true' ? 'checked=checked' : ''} class="transonly">
				<input readonly type="text" value="YES" style="width:50px;"  class="trans">
				<input readonly name="showDefaultDashboard" id="showDefaultDashboardYn_2" type="radio" value="0" ${operator.showDefaultDashboard == 'false' ? 'checked=checked' : ''} class="transonly">
				<input readonly type="text" value="NO" style="width:80px;"  class="trans">
				<br><br>
				<fmt:message key='use.default.dashboard.guide'/>
				
            </td>
        </tr>
    </table>

    <div id="btn" class="floatright margin-t15px">
        <ul>
            <li ><a href="#" id="openOperatorUpdateForm"><fmt:message key="aimir.update"/></a></li>
        </ul>
        <ul>
            <%-- <li><a href="#" id="operatorDelete"><fmt:message key="aimir.button.delete" /></a></li> --%>
        </ul>
	</div>



</form>
</div>