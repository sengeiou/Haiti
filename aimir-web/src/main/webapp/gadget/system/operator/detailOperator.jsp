<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview" /></title>

<script>
    var roleId = '';
    var operatorId = '';
    
    var isCustomerRole = false;
    
    $(document).ready(function() {
        isCustomerRole = ${operator.role.customerRole};

        if(isCustomerRole == null)
        	isCustomerRole = false;

        if(isCustomerRole) {
            $('#customerNoDetail').show();          
        } else { 
            $('#customerNoDetail').hide();
        }
    	
        $('a').click(function(event) {
            event.preventDefault();
        });

        $('#operatorDetailForm a#openOperatorUpdateForm').click( function() {

            operatorId = $("#operatorDetailForm :hidden[name='operator_id']").val();

            $("#operatorInfo").load("${ctx}/gadget/system/operator/updateOperator.do?operatorId=" + operatorId +"&customerRole=" + isCustomerRole);

        });

        $('#operatorDetailForm a#operatorDelete').click( function() {

            operatorId = $("#operatorDetailForm :hidden[name='operator_id']").val();

            if(operatorId != null && operatorId != "" && operatorId.length !=0){
                deleteOperator();
            }


        });

        //var chk = "";

        //$('#emailYn_1').click( function() {
        //    chk = "Y";
        //    setChk();
        //});

        //$('#emailYn_2').click( function() {
        //    chk = "N";
        //    setChk();
        //});

        //function setChk(){
        //    if(chk == "Y"){
        //        $('#emailYn_1').attr("checked", "checked");
        //        $('#emailYn_2').attr("checked", "");
        //    }else{
        //        $('#emailYn_1').attr("checked", "");
        //        $('#emailYn_2').attr("checked", "checked");
        //    }
        //}

        function noClick() {
            return false;
        }

        function deleteOperator() {
            if(operatorId != '') {
                $.ajax({
                    url: "${ctx}/gadget/system/operator/deleteOperator.do?operatorId=" + operatorId + "&customerRole="+isCustomerRole,
                    cache: false,
                    success: operatorDeleteResult
                });
            }
        }

        function operatorDeleteResult(responseText, status) {

            if(responseText.result != "success") {
                      Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.information.successDelete' />");
                getOperatorList();
                resetOperatorInfo();
            }
             /*if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getOperatorList();
                resetOperatorInfo();
            }*/
        }

    });

</script>

</head>
<body>
    <form id="operatorDetailForm">
        <input name="operator_id" type="hidden" style="width:150px; padding:2px" value="${operator.id}">


        <div class="headspace-enter">
            <div class="nocheck gray11pt">&nbsp;

                <span>
                 <label class="check" id="label-default">
                 <!-- details of -->
                    <fmt:message key='aimir.useroper.detailsof'/>   ${operator.loginId}
                </label>
                </span>
            </div>
        </div>

        <table class="customer_detail">
            <colgroup>
            <col width="35%"  />
            <col width="" />
            </colgroup>
            <tr><th class="bluebold11pt"><fmt:message key='aimir.loginId'/></th>
                <td><font class="input-fake blue11pt">${operator.loginId}</font></td>
            </tr>
            <tr id="customerNoDetail"><th class="bluebold11pt"><fmt:message key='aimir.customerid'/></th>
                <td><font class="input-fake blue11pt">${customerNo}</font></td>
            </tr>
            <tr><th class="bluebold11pt"><fmt:message key='aimir.name.user'/></th>
                <td><font class="input-fake blue11pt">${operator.name}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.alias'/></th>
                <td><font class="input-fake gray11pt">${operator.aliasName}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.email'/></th>
                <td><font class="input-fake gray11pt">${operator.email}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.tel.no'/></th>
                <td><font class="input-fake gray11pt">${operator.telNo}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.lastmodifiedtime.password'/></th>
                <td><font class="input-fake gray11pt">${operator.lastPasswordChangeTimeLocale}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.login.deny'/></th>
                <td class="gray11pt">
                    <input name="loginDenied" id="loginDenied" type="radio" value="1" class="trans" ${operator.loginDenied == 'true' ? 'checked=checked' : ''} onclick="return false;"/>
                    <input type="text" value="YES" class="border-trans" style="width:50px;">
                    <input name="loginDenied" id="loginDenied" type="radio" value="0" class="trans" ${operator.loginDenied == 'false' ? 'checked=checked' : ''} onclick="return false;"/>
                    <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.location'/></th>
                <td><font class="input-fake gray11pt">${operator.location.name}</font></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.locationlimit'/></th>
                <td class="gray11pt">
                    <input name="updUseLocation" id="updUseLocation" type="radio" value="1" class="trans" ${operator.useLocation == 'true' ? 'checked=checked' : ''} onclick="return false;"/>
                    <input type="text" value="YES" class="border-trans" style="width:50px;">
                    <input name="updUseLocation" id="updUseLocation" type="radio" value="0" class="trans" ${operator.useLocation == 'false' ? 'checked=checked' : ''} onclick="return false;"/>
                    <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
            </tr>
            <tr class="last-textarea">
                <th class="darkgraybold11pt"><fmt:message key='aimir.login.deny.reason'/></th>
                <td><textarea name="deniedReason" id="textarea-set2" class="deniedreason" readonly>${operator.deniedReason}</textarea></td>
            </tr>
        </table>


        <div id="btn-right" class="btn_right_bottom">
            <ul><li><a href="#" id="operatorDelete"><fmt:message key="aimir.button.delete" /></a></li></ul>
            <ul><li><a href="#" id="openOperatorUpdateForm" ><fmt:message key="aimir.update"/></a></li></ul>
        </div>

    </form>

</body>
</html>