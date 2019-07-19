<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview"/></title>
<script>

    var customerId = ${customerId};
    var originalLoginId = '';
    var checkedLoginId = '';
    $(function(){
        //고객정보 상세화면
        $("#pane-Customer-Detail").hide();

        init();

        //고객정보 수정화면
        $("#pane-Customer-Update").show();
    });

    function init() {
        $('#customerInfo-0').show('fast', customerUpdate());
        $('#customerInfo-0').tabs(1);
    }

    //고객정보 수정버튼 클릭 : 수정화면 이동
    function customerUpdate() {
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=myCustomerView', {customerId:customerId},
            function(json) {
                var customer = json.customer;
                $("#customerId").val(customer.id);
                $("#customerNo").val(customer.customerNo);

                $("#name").val(customer.name);

                var addr = customer.address;
                var addr1 = customer.address1;
                var addr2 = customer.address2;
                var addr3 = customer.address3;

                if (addr != null && addr != "" && addr != "null" && addr != '"null"') {
                    $("#address").val(addr);
                } else {
                    $("#address").val("");
                }
                if (addr1 != null && addr1 != "" && addr1 != "null" && addr1 != '"null"') {
                    $("#address1").val(addr1);
                } else {
                    $("#address1").val("");
                }
                if (addr2 != null && addr2 != "" && addr2 != "null" && addr2 != '"null"') {
                    $("#address2").val(addr2);
                } else {
                    $("#address2").val("");
                }
                if (addr3 != null && addr3 != "" && addr3 != "null" && addr3 != '"null"') {
                    //console.log("addr3 is not null");
                    $("#address3").val(addr3);
                } else {
                    //console.log("addr3 is null");
                    $("#address3").val("");
                }

                var email = customer.email;
                if (email != "null") {
                    $("#email_1").val(email.substring(0, email.indexOf("@")) );
                    $("#email_2").val(email.substring(email.indexOf("@")+1));
                }

                //유선전화
                var telephoneNo = customer.telephoneNo;

                if (telephoneNo != "null") {
                    $("#telephoneNo").attr("value" , telephoneNo);
                }

                //console.log("emailYn:", customer.emailYn);
                if (customer.emailYn == 1)
                    $("#emailYn_1").filter("input[value=1]").attr("checked", "checked");
                else
                    $("#emailYn_2").filter("input[value=0]").attr("checked", "checked");

                if (customer.demandResponse == "true" )
                    $("#DEMANDRESPONSE_1").filter("input[value=1]").attr("checked", "checked");
                else
                    $("#DEMANDRESPONSE_2").filter("input[value=0]").attr("checked", "checked");

                //이동전화
                var mobileNo = customer.mobileNo;

                if (mobileNo != "null") {
                    $("#mobileNo").val(mobileNo);
                }

                originalLoginId = customer.loginId;
                if(originalLoginId != null && originalLoginId != "" 
                        && originalLoginId != "null" && originalLoginId != '"null"') { 
                    $('#loginId').val(originalLoginId);
                } else {
                    originalLoginId = "";
                }

                $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCustomerRole', {supplier:$("#supplier").val()},
                        function(json) {
                            $("#roleId").pureSelect(json.result);
                            $("#roleId").selectbox();
                        }
                    );
                
                if (customer.smsYn == 0)
                    $("#smsYn_2").filter("input[value=0]").attr("checked", "checked");
                else
                    $("#smsYn_1").filter("input[value=1]").attr("checked", "checked");

                //남아공 필드 추가
                var identityOrCompanyRegNo = customer.identityOrCompanyRegNo;
                var initials = customer.initials;
                var vatNo = customer.vatNo;
                var workTelephone = customer.workTelephone;
                var postalAddress1 = customer.postalAddressLine1;
                var postalAddress2 = customer.postalAddressLine2;
                var postalSuburb = customer.postalSuburb;
                var postalCode = customer.postalCode;

                if (identityOrCompanyRegNo != null && identityOrCompanyRegNo != "null" && identityOrCompanyRegNo != '"null"') {
                    $("#identityOrCompanyRegNo").val(identityOrCompanyRegNo);
                }
                if (initials != null && initials != "null" && initials != '"null"') {
                    $("#initials").val(initials);
                }
                if (vatNo != null && vatNo != "null" && vatNo != '"null"') {
                    $("#vatNo").val(vatNo);
                }
                if (workTelephone != null && workTelephone != "null" && workTelephone != '"null"') {
                    $("#workTelephone").val(customer.workTelephone);
                }
                if (postalAddress1 != null && postalAddress1 != "null" && postalAddress1 != '"null"') {
                    $("#postalAddressLine1").val(postalAddress1);
                }
                if (postalAddress2 != null && postalAddress2 != "null" && postalAddress2 != '"null"') {
                    $("#postalAddressLine2").val(postalAddress2);
                }
                if (postalSuburb != null && postalSuburb != "null" && postalSuburb != '"null"') {
                    $("#postalSuburb").val(postalSuburb);
                }
                if (postalCode != null && postalCode != "null" && postalCode != '"null"') {
                    $("#postalCode").val(postalCode);
                }
        });
    }

    //고객추가정보 필수사항 체크
    function moreInfoCheck() {
        //Identity NO/ Company Reg No
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.identityOrCompanyRegNo'/>");   // Identity NO/ Company Reg No를 입력해 주세요.
        $("#identityOrCompanyRegNo").focus();
        return;
    };
    
  //로그인 아이디 체크
  $(function(){
        $("#loginIdUCheck").click(function() {
            var loginId = $.trim($("#loginId").val());
            $("#loginId").val(loginId);
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=loginIdCheck', { loginId:loginId, customerNo: $("#customerNo").val()},
                function(json) {
                    var loginIdCheckYN = json.loginIdCheckYN;
                
                    if ( json.count == 0 ) {
                        //사용할수 있습니다.
                        $("#loginIdUCheckValue").html("<ul><li class='available'><fmt:message key='aimir.abailableId'/></li></ul>");
                        $("#loginIdUCheckValue").show();
                    } else {
                        $("#loginIdUCheckValue").html("<ul><li class='reject'><fmt:message key='aimir.dupid'/></li></ul>");
                        $("#loginIdUCheckValue").show();
                        loginInfoHide();
                        $("#loginId").val('');
                        $("#loginId").focus();
                    }
                        
                    checkedLoginId = loginId;
                    $("#loginIdUCheckYN").val(loginIdCheckYN);
                    
                }
            );
        });
    });
  
  
  $(function(){
      $("#passwordChange").click(function() {
          var loginIdCheckYN =  $("#loginIdUCheckYN").val();
          if(originalLoginId == $("#loginId").val() || loginIdCheckYN == true || loginIdCheckYN == "true") {
              $("#loginInfoU1").show();
              $("#loginInfoU2").show();
              $("#loginInfoU3").show();
          } else if(originalLoginId != $("#loginId").val() || loginIdCheckYN == false || loginIdCheckYN == "false") {
              loginInfoHide();
          }
      });
  });
  
  function loginInfoHide() {
      $("#loginInfoU1").hide();
      $("#loginInfoU2").hide();
      $("#loginInfoU3").hide();
      $("#password").val('');
      $("#passwordConfirm").val('');
  }
  
    //고객정보 수정 : db update
    $(function(){
        $("#customerUpdate").click(function() {

            var email = "";
            var email_01 = $("#email_1").val();
            var email_02 = $("#email_2").val();
            var telephoneNo = "";
            var mobileNo = "";

            //ecg요청사항
            //우편번호_1
/*            if ( $("#address").val() == "" || $("#address").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputzipcode'/>");   // 우편번호를 입력해 주세요.
                $("#address").focus();
                return;
            }
            //우편번호_2
            if ( $("#address1").val() == "" || $("#address1").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputzipcode'/>");   // 우편번호를 입력해 주세요.
                $("#address1").focus();
                return;
            }*/

            // email 수신 허락 체크시만 email의 필수 체크를 실시한다.
            if ( $("#emailYn_1:checked").length != 0  ) {
	            if ( $("#email_1").val() == "" || $("#email_1").length == 0 ) {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
	                $("#email_1").focus();
	                return;
	            }
	            if ( $("#email_2").val() == "" || $("#email_2").val().length == 0 ) {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
	                $("#email_1").focus();
	                return;
	            }
            }

            if($("#telephoneNo").val().length != 0 ) {
            	var digitPattern = /^[0-9]+$/;
                if (!(digitPattern.test($("#telephoneNo").val()))) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.telephoneno.onlyNumber'/>");  // 숫자만 입력해주세요
                    $("#telephoneNo").focus();
                    return;
                }
            }
            
            //sms 수신 체크는 하고 이동번호를 입력하지 않았을때
            if ( $("#smsYn_1:checked").length != 0  || $("#mobileNo").val().length != 0) {
                if ( $("#mobileNo").val() == "" || $("#mobileNo").val().length == 0 ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputmobilenumber'/>");  // 이동번호를 입력 해 주세요
                    $("#mobileNo").focus();
                    return;
                }
                var digitPattern = /^[0-9]+$/;
                if (!(digitPattern.test($("#mobileNo").val()))) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.group.mobileNo'/>");  // 숫자만 입력해주세요
                    $("#mobileNo").focus();
                    return;
                }
            }
            
           //login Id 사용 가능 여부 체크
           var loginIdCheckYN =  $("#loginIdUCheckYN").val();
            if((originalLoginId != $("#loginId").val()) && loginIdCheckYN == false) {
                if($("#loginId").val() != "" ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateId'/>");
                    $("#loginId").focus();
                    $("#loginId").val('');
                    return;
                } 
            }
           
            var password = $("#password").val();
            
            if (password != '') {
                if (password.match(/[^0-9A-Za-z]+/) != null) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkAlphabetNumber'/>");
                    return;
                }
            }

            if( (($("#loginId").val() != null &&  $("#loginId").val() !="") && password != $("#passwordConfirm").val())
                    || (originalLoginId != $("#loginId").val() && $("#loginId").val() !="" && (password == "" || password == null))) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.confirmpassword'/>");
                return;
            }
            
            if(checkedLoginId != '' &&  (checkedLoginId != $("#loginId").val())) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.check.customer.loginid'/>");
                return;
            }
            
            // email에 값이 존재할때, emial의 유효성 체크를 실시한다.
            if ( email_01.length != 0 || email_02.length != 0) {
                if ( email_01 == "" || email_01.length == 0 ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
                    $("#email_1").focus();
                    return;
                }
                if ( email_02 == "" || email_02.length == 0 ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
                    $("#email_1").focus();
                    return;
                }
	            if ( email_02.indexOf(".") == -1) {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn1'/>");     // '.'이 Email주소에서 누락되었습니다.
	                $("#email_2").focus();
	                return;
	
	            } else if (email_02.indexOf(".") - email_02.indexOf("@") == 1) {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn2'/>");     // '@' 다음에 바로 '.'이 올수 없습니다.
	                $("#email_2").focus();
	                return;
	            } else if (email_02.charAt(email_02.length-1) == '.') {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn3'/>");     // '.'은 Email주소 끝에 올 수 없습니다.
	                $("#email_2").focus();
	                return;
	            } else if (email_02.charAt(email_02.length-1) == '@') {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn4'/>");     // '@'은 Email주소 끝에 올 수 없습니다.
	                $("#email_2").focus();
	                return;
	            }
	            //email_1 체크
	            for(i=0;i<email_01.length;i++) {
	                c = email_01.charAt(i);
	                if((c < '0' || c > '9')&&(c < 'a' || c > 'z')&&(c < 'A' || c > 'Z')&&(c != '-')&&(c != '_')&&(c != '.')) {
	                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn5'/>");     // E-mail은 영어, 숫자, '-', '_','.' 만 가능 합니다.
	                    $("#email_01").focus();
	                    return;
	                }
	            }
	            //email_2 체크
	            for(i=0;i<email_02.length;i++) {
	                f = email_02.charAt(i);
	                if((f < '0' || f > '9')&&(f < 'a' || f > 'z')&&(f < 'A' || f > 'Z')&&(f != '.')&&(f != '-')&&(f != '_')&&(f != '@')) {
	                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn5'/>");     // E-mail은 영어, 숫자, '-', '_','.' 만 가능 합니다.
	                    $("#email_2").focus();
	                    return;
	                }
	            }

	            email = $("#email_1").val() + "@" + $("#email_2").val();
            }

            $("#email").attr("value" , email);

            if ($("#telephoneNo").val().length != 0) {
                telephoneNo = $("#telephoneNo").val();
            }

            if ($("#mobileNo").val().length !=0) {
                mobileNo = $("#mobileNo").val();
            }

            $("#mobileNo").attr("value" , mobileNo);
            $("#telephoneNo").attr("value" , telephoneNo);
            //Identity NO/ Company Reg No
            var type = {'identityOrCompanyRegNo' : $("#identityOrCompanyRegNo").val()};

            if(originalLoginId != "" && ($("#loginId").val() == '' || $("#loginId").val() == null)) {
                if(!confirm("<fmt:message key='aimir.deleteCheck.customerLoginId'/>")) {
                    $("#loginId").val(originalLoginId);
                    $("#loginId").focus();
                    return;                    
                }
            }
            
            if ( confirm('<fmt:message key="aimir.update.want"/>') ) {
            	Ext.Msg.wait('Waiting for response.', 'Wait !');
                var options = {
                        success : customerUpdateResult,
                        error: function(){debugger;Ext.Msg.hide(); Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.fail.update'/>");},
                        url : '${ctx}/gadget/system/customerMax.do?param=update',
                        type : 'post',
                        data : type,
                        datatype : 'json'
                };

                $('#customerForm').ajaxSubmit(options);
            } else
                return;

        });
    });

    function customerUpdateResult(responseText, status) {
    	Ext.Msg.hide();
        customerDetail();
        $("#pane-Customer-Update").hide();
        $("#pane-Customer-Detail").show();

        if (treeData != null) {
            treeData.reload();
        }
    }

    //고객정보 취소
    $("#customerCancel").click(function() {
        detailInit();
        //고객정보
        customerDetail();
    });

    var imgWinUpdate;
    $(function() {
        $("#moreInfoUpdateBtn").click(function() {
            if(Ext.getCmp('moreInfoWinIdUpdate')==undefined) {
                    var fp_Update = new Ext.FormPanel ({
                    labelWidth: 180,
                    frame:true,
                    width: 400,
                    bodyStyle:'padding:5px 5px 5px 5px',
                    defaultType: 'textfield',
                    items: [{ xtype:'fieldset',
                        title: '<fmt:message key="aimir.more.personalDetails"/>',
                        //collapsible: true,
                        autoHeight:true,
                        defaultType: 'textfield',
                                    items :
                                          [{fieldLabel : '<fmt:message key="aimir.more.identityOrCompanyRegNo"/>', // fieldLabel : 필드의 이름표
                                            xtype : 'textfield',
                                            id   : 'fp_identityOrCompanyRegNoUpdate',
                                            name : 'fp_identityOrCompanyRegNoUpdate',// name : 폼데이터가 서버에 보내질때 매개변수 이름으로 사용
                                            value: $("#identityOrCompanyRegNo").val(),
                                            allowBlank : true},// (유효성검증) 필수값 체크
                                            {fieldLabel : '<fmt:message key="aimir.more.initials"/>',
                                             id : 'fp_initialsUpdate',
                                             name : 'fp_initialsUpdate',
                                             value: $("#initials").val(),
                                             allowBlank : true},
                                            {fieldLabel : '<fmt:message key="aimir.more.vatNo"/>',
                                             id : 'fp_vatNoUpdate',
                                             name : 'fp_vatNoUpdate',
                                             value: $("#vatNo").val(),
                                             allowBlank : true}]
                          },{ xtype:'fieldset',
                                title: '<fmt:message key="aimir.more.contractDetails"/>',
                               // collapsible: true,
                                autoHeight:true,
                                defaultType: 'textfield',
                                    items :[
                                            {fieldLabel : '<fmt:message key="aimir.more.workTelephone"/>',
                                             id : 'fp_workTelephoneUpdate',
                                             name : 'fp_workTelephoneUpdate',
                                             value: $("#workTelephone").val(),
                                             allowBlank : true}]
                          },{ xtype:'fieldset',
                                title: '<fmt:message key="aimir.more.postalAddress"/>',
                               // collapsible: true,
                                autoHeight:true,
                                defaultType: 'textfield',
                                    items :[
                                            {fieldLabel : '<fmt:message key="aimir.more.addressLine1"/>',
                                             id : 'fp_postalAddressLineUpdate1',
                                             name : 'fp_postalAddressLineUpdate1',
                                             value: $("#postalAddressLine1").val(),
                                             allowBlank : true},
                                            {fieldLabel : '<fmt:message key="aimir.more.addressLine2"/>',
                                             id : 'fp_postalAddressLineUpdate2',
                                             name : 'fp_postalAddressLineUpdate2',
                                             value: $("#postalAddressLine2").val(),
                                             allowBlank : true},
                                            {fieldLabel : '<fmt:message key="aimir.more.suburb"/>',
                                             id : 'fp_postalSuburbUpdate',
                                             name : 'fp_postalSuburbUpdate',
                                             value: $("#postalSuburb").val(),
                                             allowBlank : true},
                                            {fieldLabel : '<fmt:message key="aimir.more.postalCode"/>',
                                             id : 'fp_postalCodeUpdate',
                                             name : 'fp_postalCodeUpdate',
                                             value: $("#postalCode").val(),
                                             allowBlank : true}
                                    ]// end form Panel items
                    }],

                        buttons : [{
                        text : '<fmt:message key="aimir.ok"/>',
                        handler : function() {
                            $("#identityOrCompanyRegNo").val(Ext.getCmp('fp_identityOrCompanyRegNoUpdate').getValue());
                            $("#initials").val(Ext.getCmp('fp_initialsUpdate').getValue());
                            $("#vatNo").val(Ext.getCmp('fp_vatNoUpdate').getValue());
                            $("#workTelephone").val(Ext.getCmp('fp_workTelephoneUpdate').getValue());
                            $("#postalAddressLine1").val(Ext.getCmp('fp_postalAddressLineUpdate1').getValue());
                            $("#postalAddressLine2").val(Ext.getCmp('fp_postalAddressLineUpdate2').getValue());
                            $("#postalSuburb").val(Ext.getCmp('fp_postalSuburbUpdate').getValue());
                            $("#postalCode").val(Ext.getCmp('fp_postalCodeUpdate').getValue());
                            /*if($("#identityOrCompanyRegNo").val() == "" || $("#identityOrCompanyRegNo").length == 0) {
                                $(moreInfoCheck).call();
                            }*/
                            imgWinUpdate = undefined;
                            fp_Update =undefined;
		            		Ext.getCmp('moreInfoWinIdUpdate').close();
		            		var $body = $('body');
		            		var divField = document.createElement("div");
		            		divField.setAttribute("id", "moreInfoUpdate");
		            		$body.append(divField);
                            }
                        },{
                        text : '<fmt:message key="aimir.cancel"/>',
                        handler : function() {
                                Ext.getCmp('fp_identityOrCompanyRegNoUpdate').setValue($("#identityOrCompanyRegNo").val());
                                Ext.getCmp('fp_initialsUpdate').setValue($("#initials").val());
                                Ext.getCmp('fp_vatNoUpdate').setValue($("#vatNo").val());
                                Ext.getCmp('fp_workTelephoneUpdate').setValue($("#workTelephone").val());
                                Ext.getCmp('fp_postalAddressLineUpdate1').setValue($("#postalAddressLine1").val());
                                Ext.getCmp('fp_postalAddressLineUpdate2').setValue($("#postalAddressLine2").val());
                                Ext.getCmp('fp_postalSuburbUpdate').setValue($("#postalSuburb").val());
                                Ext.getCmp('fp_postalCodeUpdate').setValue($("#postalCode").val());

			            		imgWinUpdate = undefined;
			            		fp_Update =undefined;
			            		Ext.getCmp('moreInfoWinIdUpdate').close();
			            		var $body = $('body');
			            		var divField = document.createElement("div");
			            		divField.setAttribute("id", "moreInfoUpdate");
			            		$body.append(divField);
                            }
                        }]
                 });

                    imgWinUpdate = new Ext.Window({
                    title: '<fmt:message key="aimir.more.info"/>',
                    id: 'moreInfoWinIdUpdate',
                    applyTo:'moreInfoUpdate',
                    width:415,
                    height:400,
                    autoHeight: true,
                    pageX : 300,
                    pageY : 130,
                    resizable:false,
                    plain: true,
                    items: [fp_Update],
                    closeAction:'hide'
                });
            }else{
            }
            Ext.getCmp('moreInfoWinIdUpdate').show();
        });
    });

</script>

</head>

<body>

        <div class="customerUpdate">
            <form id=customerForm method=post>
                <input type=hidden id=customerId name=id />
                <input type=hidden id=customerNo name=customerNo />
                <input type=hidden id=email name=email />
                <input type=hidden id='loginIdUCheckYN'     name='loginIdUCheckYN' />

                 <!-- 남아공 추가 요구 필드 START -->
                <input type=hidden id=identityOrCompanyRegNo    name=identityOrCompanyRegNo/>
                <input type=hidden id=initials                  name=initials  />
                <input type=hidden id=vatNo                     name=vatNo />
                <input type=hidden id=workTelephone             name=workTelephone />
                <input type=hidden id=postalAddressLine1        name=postalAddressLine1 />
                <input type=hidden id=postalAddressLine2        name=postalAddressLine2 />
                <input type=hidden id=postalSuburb              name=postalSuburb />
                <input type=hidden id=postalCode                name=postalCode />
                <!-- 남아공 추가 요구 필드 END -->


                <div class="headspace">
                    <label class="check"><fmt:message key="aimir.customerview"/><fmt:message key="aimir.update"/><!-- 고객정보 수정 --></label>
                </div>
                <br><br>
                
                <table class="customer_detail">
                    <tr align="right">
                        <td colspan="2" class="blue11pt"><fmt:message key="aimir.hems.inform.requiredField" />
                                  <!-- 필수 항목 표시 --></td>
                    </tr>
                    <tr><th class="bluebold11pt"><em class="icon_star"></em><fmt:message key="aimir.name"/><!--이름--></th>
                        <td><input name="name" id="name" type="text"></td>
                    </tr>
                    <tr>
                        <th class="darkgraybold11pt">
                        <!-- <em class="icon_star"></em> -->
                        <fmt:message key="aimir.customeraddress"/><!--고객주소 -->
                        </th>
                        <td style="padding: 0;">
                            <table>
                                <tr><td style="border-color: #FFF"><input name="address" id="address" type="text"></td></tr>
                                <tr><td style="border-color: #FFF"><input name="address1" id="address1" type="text" size="40"></td></tr>
                                <tr><td style="border-color: #FFF"><input name="address2" id="address2" type="text"></td></tr>
                                <tr><td style="border-color: #FFF"><input name="address3" id="address3" type="text"></td></tr>
                            </table>
                        </td>
                    </tr>
                    <tr><th class="darkgraybold11pt">Email <fmt:message key="aimir.address"/><!-- 주소--></th>
                        <td><input name="email_1" id="email_1" type="text" style="width:60px;">
                            <input type="text" value="@" class="between" readonly="readonly">
                            <input name="email_2" id="email_2" type="text" style="width:142px;"></td>
                    </tr>
                    <tr><th class="darkgraybold11pt">Email <fmt:message key='aimir.operator.notificationSet'/><!-- 통보설정 --></th>
                        <td class="gray11pt">
                            <input name="emailYn" id="emailYn_1" type="radio" value="1" class="trans">
                            <input type="text" value=<fmt:message key='aimir.allowReceive'/> class="border-trans" style="width:50px;">
                            <input name="emailYn" id="emailYn_2" type="radio" value="0" class="trans" checked="checked">
                            <input type="text" value=<fmt:message key='aimir.blocked'/> class="border-trans" style="width:80px;"></td>
                    </tr>
                    <tr><th class="darkgraybold11pt"><fmt:message key='aimir.tel.no'/><!-- 유선전화 --></th>
                        <td class="gray11pt">
                            <input name="telephoneNo" id="telephoneNo" type="text"></td>
                    </tr>
                    <tr><th class="darkgraybold11pt"><fmt:message key='aimir.celluarphone'/><!-- 핸드폰번호 --></th>
                        <td class="gray11pt">
                            <input name="mobileNo" id="mobileNo" type="text"></td>
                    </tr>
                    <tr><th class="darkgraybold11pt"><fmt:message key='aimir.loginId'/><!-- 로그인 아이디 --></th>
                        <td class="gray11pt">
                            <span><input name="loginId" id="loginId" type="text" style="width:85px;"></span>
                            <div id="btn">
                                <span class="am_button margin-l10 margin-t1px">
                                    <a id="loginIdUCheck" class="on"><fmt:message key="aimir.checkDuplication" /></a>
                                </span>
                            </div>
                            <div id="loginIdUCheckValue" class="check-overlap check-overlap-customer" style="width:210px;"></div>
                        </td>
                    </tr>
                    <tr id="loginInfoU"><th class="darkgraybold11pt"><fmt:message key='aimir.change.password'/></th>
                        <td>
                            <div id="btn">
                                <ul><li><a id='passwordChange' class="on"><fmt:message key="aimir.change.password"/></a></li></ul>
                            </div>
                        </td>
                    </tr>
                    <tr id="loginInfoU1" style="display: none;"><th class="darkgraybold11pt"><fmt:message key='aimir.usergroup'/><!-- userGroup (Role) --></th>
                        <td class="gray11pt">
                            <select name="roleId" id="roleId" style="width:150px;"></select>
                        </td>
                    </tr>
                    <tr id="loginInfoU2" style="display: none;"><th class="darkgraybold11pt"><fmt:message key='aimir.password'/><!-- password --></th>
                        <td class="gray11pt">
                            <span><input name="password" id="password" type="password" style="width:100px;"></span>
                        </td>
                    </tr>
                    <tr id="loginInfoU3" style="display: none;"><th class="darkgraybold11pt"><fmt:message key='aimir.confirmpassword'/><!-- password --></th>
                        <td class="gray11pt">
                            <span><input name="passwordConfirm" id="passwordConfirm" type="password" style="width:100px;"></span>
                        </td>
                    </tr>
                    <tr>
                        <th class="darkgraybold11pt">SMS <fmt:message key='aimir.operator.receiveSetting'/><!-- 수신설정 --></th>
                        <td class="gray11pt">
                            <input name="smsYn" id="smsYn_1" type="radio" value="1" class="trans">
                            <input type="text" value=<fmt:message key='aimir.allowReceive'/> class="border-trans" style="width:50px;">
                            <input name="smsYn" id="smsYn_2" type="radio" value="0" class="trans" checked="checked">
                            <input type="text" value=<fmt:message key='aimir.blocked'/> class="border-trans" style="width:80px;"></td>
                    </tr>
                    <tr>
                        <th class="darkgraybold11pt"><fmt:message key='aimir.demandResponse'/></th>
                        <td class="gray11pt">
                            <input name="demandResponse" id="DEMANDRESPONSE_1" type="radio" value="1" class="trans">
                            <input type="text" value=<fmt:message key='aimir.allow'/> class="border-trans" style="width:50px;">
                            <input name="demandResponse" id="DEMANDRESPONSE_2" type="radio" value="0" class="trans">
                            <input type="text" value=<fmt:message key='aimir.reject'/> class="border-trans" style="width:80px;"></td>
                    </tr>
                    <%--
                    <tr>
                        <th class="darkgraybold11pt"><fmt:message key="aimir.sic" /><!-- 산업분류코드 --></th>
                        <td class="gray11pt"><select id="customerTypeCode" style="width:150px"></select></td>
                    </tr>
                    --%>
                    <tr class="last">
                        <th class="bluebold11pt"><fmt:message key="aimir.more.info"/><!--추가 설정--></th>
                        <td>
                            <div id="btn">
                                <ul><li><a id='moreInfoUpdateBtn' class="on"><fmt:message key="aimir.update.more.info"/><!-- 정보 추가--></a></li></ul>
                            </div>
                        </td>
                    </tr>
                </table>

            </form>
    </div>

    <div class="btn_right_bottom">
        <span class="lightgray11pt"><fmt:message key='aimir.editApply'/><!-- 수정한 내용을 적용합니다.--></span>
        <span id="btn">
            <ul><li><a id="customerUpdate" class="on"><fmt:message key='aimir.update'/><!-- 수정 --></a></li></ul>
        </span>
        <span id="btn">
            <ul><li><a id="customerCancel" class="on"><fmt:message key='aimir.cancel'/><!-- 취소 --></a></li></ul>
        </span>
    </div>

</body>
</html>