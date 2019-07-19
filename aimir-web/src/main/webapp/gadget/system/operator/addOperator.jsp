<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script>

    var roleId = '';
    var checkedLoginId = false;
    var checkedCustomerNo = false;

    var idDupleChk = "<fmt:message key="aimir.chkDuplicateId"/>";
    var passwordChk = "<fmt:message key="aimir.msg.confirmpassword"/>";
    var passwordChkAlphabetNumber = "<fmt:message key='aimir.chkAlphabetNumber'/>";
    var insertIdChk = "<fmt:message key="aimir.alert.inputid"/>";


    var abailableId = "<fmt:message key="aimir.abailableId"/>";
    var existId = "<fmt:message key="aimir.exist"/>";
    var isCustomerRole = false;
    
    var preLoginId = '';
 
    $(document).ready(function() {

        isCustomerRole = ${operator.role.customerRole};
        
        if(isCustomerRole == null)
        	isCustomerRole = false;

        if(isCustomerRole) {
            $('#customerNoTR').show();          
        } else { 
            $('#customerNoTR').hide();
        }
    });
    
    $(function() {

        $('a').click(function(event) {
            event.preventDefault();
        });

        //Add Operator
        $('#operatorAddForm a#addOperator').click( function() {

            var loginId = $.trim($("#operatorAddForm :input[name='loginId']").val());
			$("#operatorAddForm :input[name='loginId']").val(loginId);
            if (loginId) {
				if (!checkedLoginId || preLoginId != loginId) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',idDupleChk); //아이디 중복체크를 해야 합니다.
                } else {
                	if(isCustomerRole) {
                    	if(!checkedCustomerNo) {
                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkValidationCustomerNo'/>".replace("$CUSTOMERNO", "<fmt:message key='aimir.customerid'/>"));
                        	return;
                    	}
                    }

                	var password = $("#operatorAddForm :input[name='password']").val();
                    var passwordCheck = $("#operatorAddForm :input[name='passwordCheck']").val();

                    if (password != '') {
                        if (password.match(/[^0-9A-Za-z]+/) != null) {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',passwordChkAlphabetNumber);
                            return;
                        }
                    }

                    if (password != '' && password == passwordCheck) {

                        var options = {
                            //콜백함수 정의.  operatorAddResult
                            success : operatorAddResult,
                            url : '${ctx}/gadget/system/operator/addOperator.do',
                            type : 'post',
                            datatype : 'json'
                        };
                    } else {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',passwordChk);//"비밀번호를 확인하세요."
                    }
                }
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',insertIdChk); //"아이디를 입력하세요."
            }

            $('#operatorAddForm').ajaxSubmit(options);

        });


        //아이디 중복  check
        $('#operatorAddForm a#checkDuplicateLoginId').click( function() {
            var loginId = $.trim($("#operatorAddForm :input[name='loginId']").val());
			$("#operatorAddForm :input[name='loginId']").val(loginId);
			preLoginId = loginId;
            if (loginId) {

                $.getJSON('${ctx}/gadget/system/operator/checkDuplicateLoginId.do?loginId='+loginId,
                        function(json) {
                            if (json.dupCheck == true) {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',loginId+" " +  abailableId);//"사용가능한 아이디 입니다.");
                                checkedLoginId = true;
                            } else {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',existId);//"이미 사용중입니다.");
                                checkedLoginId= false;
                            }
                        }
                 );
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',insertIdChk);//"아이디를 입력하세요.");
                checkedLoginId= false;
            }
        });
        
        //cutomerNumber 중복  check
        $('#operatorAddForm a#checkValidationCustomerNo').click( function() {
            var customerNo = $("#operatorAddForm :input[name='customerNo']").val();

            if (customerNo) {
                $.getJSON('${ctx}/gadget/system/customerMax.do?param=overlapcheck', { customerNo:customerNo },
                    function(json) {
                        if ( json.count == 0 ) {
                            //존재하지 않는 customerNo. 체크
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.customerNoNotExist'/>".replace("$CUSTOMERNO", "<fmt:message key='aimir.customerid'/>"));
                            $("#operatorAddForm :input[name='customerNo']").val('');
                            $("#operatorAddForm :input[name='customerNo']").focus();
                            checkedCustomerNo=false;
                        } else {
                            $.getJSON('${ctx}/gadget/system/customerMax.do?param=checkCustomerNoLoginMapping', {customerNo:customerNo },
                                    function(json) {
                                        if(json.loginId != null) {
                                            //이미 다른 loginId에 매핑되어 있다.
                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alreadyMapping.customerNo'/>".replace("$CUSTOMERNO", "<fmt:message key='aimir.customerid'/>"));
                                            $("#operatorAddForm :input[name='customerNo']").val('');
                                            $("#operatorAddForm :input[name='customerNo']").focus();
                                            checkedCustomerNo=false;
                                        } else {
                                        	//사용가능
                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',customerNo+" " +  abailableId);

                                            $("#operatorAddForm :input[name='customerNo']").val(customerNo);
                                            $("#operatorAddForm :input[name='name']").val(json.name);
                                            $("#operatorAddForm :input[name='email']").val(json.email);
                                            $("#operatorAddForm :input[name='telNo']").val(json.telNo);
                                            checkedCustomerNo=true;
                                        }
                                    }
                                );
                        }
                    }
                 );
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.invalidcontractno'/>"); //Invalid Contract No.
                checkedCustomerNo=false;
            } 
        });

        $('#operatorAddForm a#cancelAddOperator').click( function() {
            resetOperatorInfo();
        });

        locationTreeGoGo('treeDivA', 'locationAText', 'location');

    });

    //등록후 콜백함수..
    function operatorAddResult(responseText, status) {
        //alert(responseText.result);

        //alert($("#operatorAddForm :input[name='loginId']").val());

        var inputedUserId= $("#operatorAddForm :input[name='loginId']").val();

        //var msg1= " New User {ID} created!";

        //aimir.userreg.newuser
        var msg0= "<fmt:message key='aimir.userreg.name'/>";

        //aimir.userreg.created
        var msg2= "<fmt:message key='aimir.userreg.created'/>";


        //created userid 알림 메세지
        if(responseText.result != "fail") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',msg0 + " {"+ inputedUserId + "} "+ msg2 );
        } else {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',msg0 + " {"+ inputedUserId + "} "+ "<fmt:message key='aimir.failed'/>" );
        }


        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i=0 ; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else {
            getOperatorListGrid({roleId : '${roleId }'});
            resetoperatorAddForm();
        }
    }

    function resetoperatorAddForm() {
        $("#operatorInfo").load("${ctx}/gadget/system/operator/addOperator.do?roleId=${roleId }");
    //       $('#operatorAddForm').resetForm();
    }

</script>

<form id='operatorAddTmp'>
<input name="roleId" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${roleId}>
</form>
<form id='operatorAddForm' name="operatorAddForm" modelAttribute="operator" >
<input name="role" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${operator.role }>
<input name="supplier" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${operator.supplier }>
<input name="id" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=${operator.id }>
<!-- <input name="customerNo" type="hidden"  class="nuri_search_n" style="width:150px; padding:2px" value=''> -->



        <div class="headspace-enter">
            <label class="check"><%-- <fmt:message key="aimir.user"/>&nbsp; --%><fmt:message key="aimir.tab.newuser"/><!-- 사용자 등록 --></label>
        </div>

        <div class="textalign-right gray11pt" ><fmt:message key='aimir.userreg.Required'/></div>

        <table class="customer_detail">
            <tr><th class="bluebold11pt"><fmt:message key='aimir.loginId'/><em class="icon_star"></em></th>
                <td><span><input name="loginId" type="text" style="width:170px"/></span>
                    <span class="btn_margin"><em class="am_button"><a href="#" id="checkDuplicateLoginId"><fmt:message key="aimir.checkDuplication" /></a></em></span>
                </td>
            </tr>

            <tr id="customerNoTR"><th class="bluebold11pt"><fmt:message key='aimir.customerid'/><em class="icon_star"></em></th>
                <td><span><input name="customerNo" type="text" style="width:170px"/></span>
                    <span class="btn_margin"><em class="am_button"><a href="#" id="checkValidationCustomerNo"><fmt:message key="aimir.validation.check" /></a></em></span>
                </td>
            </tr>
            <tr><th class="bluebold11pt"><fmt:message key='aimir.userreg.name'/>            
            <em class="icon_star"></em></th>
                <td><input name="name" type="text"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.userreg.nickname'/></th>
                <td><input name="aliasName" type="text"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.password'/><em class="icon_star"></em></th>
                <td><input name="password" type="password"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.userreg.confirmpassword'/><em class="icon_star"></em></th>
                <td><input name="passwordCheck" type="password"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.email'/></th>
                <td><input name="email" type="text"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.tel.no'/></th>
                <td><input name="telNo" type="text"/></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.login.deny'/><em class="icon_star"></em></th>
                <td class="gray11pt">
                    <input name="loginDenied" type="radio" value="1" class="trans" checked>
                    <input type="text" value="YES" class="border-trans" style="width:50px;">
                    <input name="loginDenied" type="radio" value="0" class="trans">
                    <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.location'/></th>
                <td class="gray11pt">
                    <input type="text" id="locationAText" name="location.name" >
                    <input type="hidden" id="location" name="location.id" value="" />
            </tr>
            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.locationlimit'/></em></th>
                <td class="gray11pt">
                    <input name="useLocation" type="radio" value="1" class="trans"/>
                    <input type="text" value="YES" class="border-trans" style="width:50px;"/>
                    <input name="useLocation" type="radio" value="0" class="trans" checked/>
                    <input type="text" value="NO" class="border-trans" style="width:80px;"/></td>
            </tr>
            <tr class="last-textarea">
                <th class="darkgraybold11pt"><fmt:message key='aimir.login.deny.reason'/></th>
                <td><textarea name="deniedReason" id="textarea-set1" class="deniedreason-edit"></textarea></td>
            </tr>
        </table>
        <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
            <div id="treeDivA"></div>
        </div>
        <div id="btn-right" class="btn_right_bottom">
            <ul><li><a href="#" id="cancelAddOperator"><fmt:message key='aimir.cancel'/></a></li></ul>
            <!-- 등록 버튼 in user -->
            <ul><li><a href="#" id="addOperator" ><fmt:message key="aimir.save2"/><!-- 저장 --></a></li></ul>
        </div>


</form>
