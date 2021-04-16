<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script>

var isCustomerRole;

$(document).ready(function() {

    isCustomerRole = ${operator.role.customerRole};
    if(isCustomerRole) {
        $('#customerNoUpdate').show();          
    } else { 
        $('#customerNoUpdate').hide();
    }
});

$(function(){

    $('a').click(function(event) {
        event.preventDefault();
    });

    //Update Operator
    $('#operatorUpdateForm a#updateOperator').click( function() {

        var operatorId = $("#operatorUpdateForm :hidden[name='id']").val();

        $('#locationId').val($('#location').val());
        console.log("operatorId:", operatorId);

        if (operatorId) {

            var loginId = $("#operatorUpdateForm :hidden[name='loginId']").val();
            var customerNo = $("#operatorUpdateForm :[id='customerNoUpdate']").val();

            var pwCheck = false;

            var password = $("#operatorUpdateForm :input[name='password']").val();
            var passwordCheck = $("#operatorUpdateForm :input[name='passwordCheck']").val();

            console.log("password:", password);
            console.log("passwordCheck:", passwordCheck);
            // 패스워드 영숫자 체크
            if (password != '') {
                if ( password.match(/[^0-9A-Za-z]+/) != null) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkAlphabetNumber'/>");
                    return;
                }
            }

            if (password == passwordCheck) {

                var options = {
                    success : operatorUpdateResult,
                    url : '${ctx}/gadget/system/operator/updateOperator.do',
                    type : 'post',
                    datatype : 'json'
                };

                $('#operatorUpdateForm').ajaxSubmit(options);
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.operator.confirm.newpwd" />');
            }
        }// if End

    });

    $('#operatorUpdateForm a#cancelUpdateOperator').click( function() {
        cancelUpdateOperator();
    });

    var chk = "";

    $('#emailYn_1').click( function() {

        chk = "Y";
        setChk();

    });

    $('#emailYn_2').click( function() {
        chk = "N";
        setChk();

    });

    function setChk() {

        if (chk == "Y") {
            $('#emailYn_1').attr("checked", "checked");
            $('#emailYn_2').attr("checked", "");
        } else {
            $('#emailYn_1').attr("checked", "");
            $('#emailYn_2').attr("checked", "checked");
        }
    }

    locationTreeGoGo('treeDivU', 'locationUText', 'location');
    
    getGroups();

});



function operatorUpdateResult(responseText, status) {

    Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);

    if (responseText.errors && responseText.errors.errorCount > 0) {
        var i, fieldErrors = responseText.errors.fieldErrors;
        for (i = 0 ; i < fieldErrors.length; i++) {
            var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
            $(temp).val(''+fieldErrors[i].defaultMessage);
        }
    } else {
        getOperatorListGrid({roleId : '${roleId }'});
        $("#operatorInfo").load("${ctx}/gadget/system/operator/detailOperator.do?operatorId=${operatorId}&roleId=${roleId}");
    }
    operatorTabListener();
}

function cancelUpdateOperator(){
    $("#operatorInfo").load("${ctx}/gadget/system/operator/detailOperator.do?operatorId=${operatorId}&roleId=${roleId}");
}

function getGroups() {

    $.getJSON('${ctx}/gadget/system/user_group_max.do?param=groups', { roleId:${roleId} , supplierId:supplierId },
        function(json) {
    		
	    	for(var i =0; i< json.rolegroups.length; i++) {				
				if(json.rolegroups[i].customerRole == "true") { // customer role
					json.rolegroups.splice(i, 1);
				}
			}
	    	
            $('#roleName1').pureSelect(json.rolegroups);
            //사용자 그룹명 선택
            $("#roleName1 option[value=" + ${roleId} + "]").attr("selected", "true");
            //style 적용
            $('#roleName1').selectbox();
        }
    );
}

</script>
<form id='operatorUpdateForm' name="operatorUpdateForm" modelAttribute="operator" >
    <!-- <input name="roleId" type="text"  class="nuri_search_n" style="width:150px; padding:2px" value="${roleId }"/> -->
    <input name="id" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value="${operator.id }"/>
    <input name="customerNo" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=""/>


    <div class="headspace-enter">
        <span><label class="check" id="label-default"><%-- ${operator.loginId} --%></label></span><div class="nocheck gray11pt"><fmt:message key='aimir.view.detail'/><!-- 님의 상세정보 --> <fmt:message key='aimir.update'/></div>
    </div>


    <table class="customer_detail">
        <tr><th class="bluebold11pt"><fmt:message key='aimir.loginId'/></th>
            <td><font class="input-fake blue11pt">${operator.loginId}</font><input name="loginId" type="hidden" value="${operator.loginId}" style="width:200px"/></td>
        </tr>
        <tr id="customerNoUpdate"><th class="bluebold11pt"><fmt:message key='aimir.customerid'/></th>
            <td><font class="input-fake blue11pt">${customerNo}</font></td>
        </tr>
        <tr><th class="bluebold11pt"><fmt:message key='aimir.name.user'/></th>
            <td><input name="name" type="text" value="${operator.name}"/></td>
        </tr>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.alias'/></th>
            <td><input name="aliasName" type="text" value="${operator.aliasName}"/></td>
        </tr>

        <%-- <tr><th class="darkgraybold11pt"><fmt:message key="aimir.operator.prevpwd" /></th>
            <td><input name="oldPassword" type="password"></td>
        </tr> --%>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.newpassword'/></th>
            <td><input name="password" type="password"></td>
        </tr>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.confirmpassword'/></th>
            <td><input name="passwordCheck" type="password"></td>
        </tr>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.email'/></th>
            <td><input name="email" type="text" value="${operator.email}"/></td>
        </tr>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.tel.no'/></th>
            <td><input name="telNo" type="text" value="${operator.telNo}"/></td>
        </tr>
        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.login.deny'/></th>
            <td class="gray11pt">
                <input name="loginDenied" id="emailYn_1" type="radio" value="1" class="trans" ${operator.loginDenied == 'true' ? 'checked=checked' : ''} >
                <input type="text" value="YES" class="border-trans" style="width:50px;">
                <input name="loginDenied" id="emailYn_2" type="radio" value="0" class="trans" ${operator.loginDenied == 'false' ? 'checked=checked' : ''}>
                <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
        </tr>

        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.location'/></th>
            <td class="gray11pt">
                <input type="text" id="locationUText" name="location.name" style="width:300px" value="${operator.location.name}">
                <input type="hidden" id="location" name="location.id" value="${operator.location.id}" />
                <input type="hidden" id="locationId" name="locationId" value="" />
        </tr>

        <tr><th class="darkgraybold11pt"><fmt:message key='aimir.locationlimit'/></th>
            <td class="gray11pt">
                <input name="useLocation" id="useLocation" type="radio" value="1" class="trans" ${operator.useLocation == 'true' ? 'checked=checked' : ''} >
                <input type="text" value="YES" class="border-trans" style="width:50px;">
                <input name="useLocation" id="useLocation1" type="radio" value="0" class="trans" ${operator.useLocation == 'false' ? 'checked=checked' : ''}>
                <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
        </tr>
        <c:if test='${operator.role.customerRole == "false"}'>
		<tr><th class="darkgraybold11pt"><fmt:message key='aimir.role'/></em></th>
            <td class="gray11pt">
                <select name="roleId" id="roleName1"></select>                
            </td>
        </tr>
        </c:if>
        <c:if test='${operator.role.customerRole == "true"}'>
		<input name="roleId" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value="${roleId }"/>
        </c:if>
        <tr class="last-textarea">
            <th class="darkgraybold11pt"><fmt:message key='aimir.login.deny.reason'/></th>
            <td><textarea name="deniedReason" id="textarea-set1" class="deniedreason-edit">${operator.deniedReason}</textarea></td>
        </tr>
    </table>
        <div id="treeDivUOuter" class="tree-billing auto" style="display:none;">
        <div id="treeDivU"></div>
        </div>
    <div id="btn-right" class="btn_right_bottom">
        <ul><li><a href="#" id="cancelUpdateOperator"><fmt:message key='aimir.cancel'/></a></li></ul>

        <!-- 유저 정보 수정 버튼 -->
        <ul><li><a href="#" id="updateOperator" class="on"><fmt:message key="aimir.save2"/></a></li></ul>
    </div>

</form>
