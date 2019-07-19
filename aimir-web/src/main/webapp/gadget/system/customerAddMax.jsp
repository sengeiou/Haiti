<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>고객정보</title>
<script>

    $(function(){
        $("#checkYN").val('false');
        init();
    });

    function init() {
        
        
      //계약추가버튼
        supplier = $("#supplier").val();
    
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCustomerRole', {supplier:supplier},
            function(json) {
                $("#roleId").pureSelect(json.result);
                $("#roleId").selectbox();
            }
        );
        $('#customerInfo-0').show('fast');
        $('#customerInfo-0').tabs(1);     
        //setAddSelectBox();
    }
    
    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                    loginId = json.loginId;
                }
            }
    );


    
    //중복체크
    $(function(){
        $("#idOverlapCheck").click(function() {  
            //var customerNo = $("#customerNo").val();
            var customerNo = $.trim($("#customerNo").val());
            
            $("#customerNo").val(customerNo);
         	// 공백문자 거절
            var fmt1 = /\s/;
            if( customerNo.length > 0 && fmt1.exec(customerNo) ){
            	
            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.notspace"/>');return;

            }
            if ( customerNo == "" || customerNo.length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.inputid"/>');
                return;
            }
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=overlapcheck', { customerNo:customerNo },
                function(json) {
                    if ( json.count == 0 ) {
                        $("#checkValue").html("<ul><li class='available'><fmt:message key="aimir.abailableId"/></li></ul>");
                        $("#checkValue").show();
                    } else {
                        $("#checkValue").html("<ul><li class='reject'><fmt:message key="aimir.dupid"/></li></ul>");
                        $("#checkValue").show();
                        $("#custmerNo").val('');
                        $("#custmerNo").focus();
                    }
                    $("#checkYN").val(json.checkYN);
                }
            );
        });

        $("#createNew").click(function() {
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=createNew', 
                function(json) {
                    $("#customerNo").val(json.customerNumber);  
            });
        });
    });
    
  //로그인 아이디 체크
  
  var checkedLoginId;
    $(function(){
        $("#loginIdCheck").click(function() {
            var loginId = $.trim($("#loginId").val());
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=loginIdCheck', { loginId:loginId },
                function(json) {
                    var loginIdCheckYN = json.loginIdCheckYN;
                    
                    if ( json.count == 0 ) {
                        if($("#loginId").val() == '' || $("#loginId").val() == null || $("#loginId").val() == 'null' || $("#loginId").val() == '"null"') {
                            $("#loginIdCheckValue").html("<ul><li class='available'><fmt:message key='aimir.data.notexist'/></li></ul>");
                            $("#loginIdCheckValue").show();
                            $("#loginId").val('');
                            $("#loginId").focus();
                        } else {
                          //사용할수 있습니다.
                            $("#loginIdCheckValue").html("<ul><li class='available'><fmt:message key='aimir.abailableId'/></li></ul>");
                            $("#loginIdCheckValue").show(); 
                        }
                        
                    } else {
                        $("#loginIdCheckValue").html("<ul><li class='reject'><fmt:message key='aimir.dupid'/></li></ul>");
                        $("#loginIdCheckValue").show();
                        $("#loginId").val('');
                        $("#loginId").focus();
                    }
                        
                    $("#loginIdCheckYN").val(loginIdCheckYN);
                    
                    checkedLoginId = loginId; 
                    
                    if(loginIdCheckYN == true) {
                        $("#loginInfo1").show();
                        $("#loginInfo2").show();
                        $("#loginInfo3").show();
                    }  else if(loginIdCheckYN == false) {
                        $("#password").val('');
                        $("#passwordConfirm").val('');
                        $("#loginInfo1").hide();
                        $("#loginInfo2").hide();
                        $("#loginInfo3").hide();
                    }
                }
            );
        });
    });

  //고객추가정보 필수사항 체크
    function moreInfoCheck() {
  		 //Identity NO/ Company Reg No
          Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.identityOrCompanyRegNo'/>");   // Identity NO/ Company Reg No를 입력해 주세요.
          $("#identityOrCompanyRegNo").focus();
          return;
  };
    
    $(function() {
        $("#customerAdd").click(function() {
            var email = "";
            var email_01 = $("#email_1").val();
            var email_02 = $("#email_2").val();
            var telephoneNo = "";
            var mobileNo = "";
			
            var checkYN = $("#checkYN").val();
            if ( checkYN == "false" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateId'/>");
                $("#customerNo").focus();
                $("#customerNo").val('');
                return;
            }
            //고객명
            if ( $("#name").val() == "" || $("#name").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.insertName'/>");   //
                $("#name").focus();
                return;
            }

            //ecg 요청사항
/*            //우편번호_1
            if ( $("#address").val() == "" || $("#address").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputzipcode'/>");   // 우편번호를 입력해 주세요.
                $("#address").focus();
                return;
            }
            //주소
            if ( $("#address1").val() == "" || $("#address1").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputaddress'/>");   // 주소를 입력해 주세요
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
	            if ( $("#email_2").val() == "" || $("#email_2").length == 0 ) {
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
            if ( $("#smsYn_1:checked").length != 0  || $("#mobileNo").val().length != 0 ) {
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

            // email에 값이 존재할때, emial의 유효성 체크를 실시한다.
            if ( email_01.length != 0 || email_02.length != 0) {

                if ( email_01 == "" || $("#email_1").val().length == 0 ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
                    $("#email_1").focus();
                    return;
                }
                if ( email_02 == "" || $("#email_2").val().length == 0 ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputemailaddress'/>");  // Email주소를 입력 해 주세요
                    $("#email_1").focus();
                    return;
                }

	            if ( email_02.indexOf(".") == -1) {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.emailwarn1'/>");     // '.'이 Email주소에서 누락되었습니다.
	                $("#email_2").focus();
	                return;
	            } else if ((email_02.indexOf(".") - email_02.indexOf("@")) == 1) {
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
            
            if($("#loginIdCheckYN").val() == false) {
                if($("#loginId").val() != "" ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateId'/>");
                    $("#loginId").focus();
                    $("#loginId").val('');
                    return;
                } 
            }
            
            var password = $.trim($("#password").val());
            
            if (password != '') {
                if (password.match(/[^0-9A-Za-z]+/) != null) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkAlphabetNumber'/>");
                    return;
                }
            }

            if(($("#loginId").val() != null &&  $("#loginId").val() !="") && ($("#roleId").val() == '' || $("#roleId").val() == null)) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.report.mgmt.msg.validation.group'/>");
                return;
            }
            
            if( ($("#loginId").val() != null &&  $("#loginId").val() !="") && (password == '' || password != $("#passwordConfirm").val())) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.confirmpassword'/>");
                return;
            }

            if($("#loginId").val() != '' && checkedLoginId != '' &&  checkedLoginId != $("#loginId").val()) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.check.customer.loginid'/>");
                return;
            }
            
            var checkYN = $("#checkYN").val();
            if ( checkYN == "false" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateId'/>");
                $("#customerNo").focus();
                $("#customerNo").val('');
                return;
            }

            $("#email").attr("value" , email);

            if($("#telephoneNo").val().length != 0) {
            	 telephoneNo = $("#telephoneNo").val();
            }

            if($("#mobileNo").val().length !=0) {
            	mobileNo = $("#mobileNo").val();
            }


            $("#mobileNo").attr("value" , mobileNo);
            $("#telephoneNo").attr("value" , telephoneNo);
          	/* 
          	//추가정보입력 필수 체크 (남아공 필수)
          	//Identity NO/ Company Reg No
            if ( $("#identityOrCompanyRegNo").val() == "" || $("#identityOrCompanyRegNo").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.identityOrCompanyRegNo'/>");   // Identity NO/ Company Reg No를 입력해 주세요.
                $("#identityOrCompanyRegNo").focus();
                return;
            } */
            
            //var type = {'type' : $("#customerTypeCode").val()};
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            var options = {
                success : customerAddResult,
                error: function(){Ext.Msg.hide(); Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.alert.failInsert'/>");},
                url : '${ctx}/gadget/system/customerMax.do?param=add&supplierId='+supplierId ,
                type : 'post',
                //data : type,
                datatype : 'json'
            };
            $('#customerForm').ajaxSubmit(options);
        });
        
        $("#customerCancel").click(function() {
            if (customerId != "") {
            	prevCustomerId = "";
            	loadCustomerInfo();
            } else {
            	$("#pane-Customer-Info-Button").show();
            	$('#pane-tab-Customer').html("");
            }
        });

    });

    function customerAddResult(responseText, status) {
    	Ext.Msg.hide();
        reset();
        
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.newcustomeradded'/>");   // 신규 고객이 등록되었습니다.
        customerExtTreeSearch();
        
 //       flex = getFlexObject('CustomerList');
  //      flex.customerSearch();
    }

    //$(function() {
    //    $("#customerCancel").click(function() {
    //        reset();
    //    });
    //});

    //폼값 리셋
    function reset() {
        $("#customerNo").val('');
        $("#name").val('');
        $("#address").val('');
        $("#address1").val('');
        $("#address2").val('');
        $("#email_1").val('');
        $("#email_2").val('');
        $("#emailYn_2").filter("input[value=0]").attr("checked", "checked");
        $("#mobileNo").val('');
        $("#smsYn_2").filter("input[value=0]").attr("checked", "checked");
        $("#telephoneNo").val('');
        $("#checkValue").hide();
        $("#checkValue").val('false');
        $("#checkValue1").val('false');
        
        $("#identityOrCompanyRegNo").val('');
        $("#initials").val('');
        $("#vatNo").val('');
        $("#workTelephone").val('');
        $("#postalAddressLine1").val('');
        $("#postalAddressLine2").val('');
        $("#postalSuburb").val('');
        $("#postalCode").val('');
        
        $("#loginId").val('');
        $("#password").val('');
        $("#passwordConfirm").val('');
        $("#loginInfo1").hide();
        $("#loginInfo2").hide();
        $("#loginInfo3").hide();
        
    }

				
    $(function() {
        $("#moreInfoAddBtn").click(function() {
        	if(Ext.getCmp('moreInfoWinId') == undefined){ 
        		var fp_Add = new Ext.FormPanel ({
        			labelWidth: 180,
                    frame:true,
                    width: 400,
                    plain: true,
                    bodyStyle:'padding:5px 5px 5px 5px',
                    defaultType: 'textfield',
                    items: [{ 
                    	xtype:'fieldset',
                        title: '<fmt:message key="aimir.more.personalDetails"/>',
                        bodyStyle:'padding:5px 5px 5px 5px',
                        //collapsible: true,
                        autoHeight:true,
                        defaultType: 'textfield',        	    
			           	     		items :
					           	          [{fieldLabel : '<fmt:message key="aimir.more.identityOrCompanyRegNo"/>', // fieldLabel : 필드의 이름표
					               	        xtype : 'textfield',
					               	     	id	 : 'fp_identityOrCompanyRegNoAdd',
					           	        	name : 'fp_identityOrCompanyRegNoAdd',// name : 폼데이터가 서버에 보내질때 매개변수 이름으로 사용
					           	        	value: $("#identityOrCompanyRegNo").val(),
					           	        	allowBlank : true},// (유효성검증) 필수값 체크
					           	        	{fieldLabel : '<fmt:message key="aimir.more.initials"/>',
					           	        	 id : 'fp_initialsAdd',
					               	         name : 'fp_initialsAdd',
					               	      	 value: $("#initials").val(),
					               	         allowBlank : true},
					               	        {fieldLabel : '<fmt:message key="aimir.more.vatNo"/>',
					                    	 id	: 'fp_vatNoAdd',
					                   	     name : 'fp_vatNoAdd',
					                   	     value: $("#vatNo").val(),
					                   	     allowBlank : true}]
		                  },{ xtype:'fieldset',
		                        title: '<fmt:message key="aimir.more.contractDetails"/>',
		                        bodyStyle:'padding:5px 5px 5px 5px',
		                       // collapsible: true,
		                        autoHeight:true,
		                        defaultType: 'textfield',        	    
					           	    items :[
					                  	    {fieldLabel : '<fmt:message key="aimir.more.workTelephone"/>',
					                   	     id : 'fp_workTelephoneAdd', 
					                      	 name : 'fp_workTelephoneAdd',
					                      	 value: $("#workTelephone").val(),
					                      	 allowBlank : true}]
		                  },{ xtype:'fieldset',
		                        title: '<fmt:message key="aimir.more.postalAddress"/>',
		                        bodyStyle:'padding:5px 5px 5px 5px',
		                     //   collapsible: true,
		                        autoHeight:true,
		                        defaultType: 'textfield',        	    

					           	    items :[
					                      	{fieldLabel : '<fmt:message key="aimir.more.addressLine1"/>', 
					                      	 id : 'fp_postalAddressLineAdd1',
					                         name : 'fp_postalAddressLineAdd1',
					                         value: $("#postalAddressLine1").val(),
					                         allowBlank : true},
					                        {fieldLabel : '<fmt:message key="aimir.more.addressLine2"/>', 
					                         id : 'fp_postalAddressLineAdd2',
					                         name : 'fp_postalAddressLineAdd2',
					                         value: $("#postalAddressLine2").val(),
					                         allowBlank : true},
					                        {fieldLabel : '<fmt:message key="aimir.more.suburb"/>', 
					                         id : 'fp_postalSuburbAdd',
					                         name : 'fp_postalSuburbAdd',
					                         value: $("#postalSuburb").val(),
					                         allowBlank : true},
					                        {fieldLabel : '<fmt:message key="aimir.more.postalCode"/>',
					                         id : 'fp_postalCodeAdd', 
					                         name : 'fp_postalCodeAdd',
					                         value: $("#postalCode").val(),
					                         allowBlank : true}          	         
		   					  		]// end form Panel items
                    }], 
 					  
   					buttons : [{
		            	text : '<fmt:message key="aimir.ok"/>',
		            	handler : function() {		            		            	
	                		$("#identityOrCompanyRegNo").val(Ext.getCmp('fp_identityOrCompanyRegNoAdd').getValue());
	                		$("#initials").val(Ext.getCmp('fp_initialsAdd').getValue());
	                		$("#vatNo").val(Ext.getCmp('fp_vatNoAdd').getValue());
	                	 	$("#workTelephone").val(Ext.getCmp('fp_workTelephoneAdd').getValue());
	                		$("#postalAddressLine1").val(Ext.getCmp('fp_postalAddressLineAdd1').getValue());
	                		$("#postalAddressLine2").val(Ext.getCmp('fp_postalAddressLineAdd2').getValue());
	                		$("#postalSuburb").val(Ext.getCmp('fp_postalSuburbAdd').getValue());
	                		$("#postalCode").val(Ext.getCmp('fp_postalCodeAdd').getValue()); 
	                			/* 
	                			//추가정보입력 필수 체크 (남아공 필수)
	                			if($("#identityOrCompanyRegNo").val() == "" || $("#identityOrCompanyRegNo").length == 0) {
	                			$(moreInfoCheck).call(); 
	                			} 
	                			*/
	                		
	                		Ext.getCmp('moreInfoWinId').hide();
	                		}
		        		},{
		            	text : '<fmt:message key="aimir.cancel"/>',
		            	handler : function() {
			            		Ext.getCmp('fp_identityOrCompanyRegNoAdd').setValue($("#identityOrCompanyRegNo").val());
			            		Ext.getCmp('fp_initialsAdd').setValue($("#initials").val());
			            		Ext.getCmp('fp_vatNoAdd').setValue($("#vatNo").val());
			            		 Ext.getCmp('fp_workTelephoneAdd').setValue($("#workTelephone").val());
			            		Ext.getCmp('fp_postalAddressLineAdd1').setValue($("#postalAddressLine1").val());
			            		Ext.getCmp('fp_postalAddressLineAdd2').setValue($("#postalAddressLine2").val());
			            		Ext.getCmp('fp_postalSuburbAdd').setValue($("#postalSuburb").val());
			            		Ext.getCmp('fp_postalCodeAdd').setValue($("#postalCode").val());
		            			
			            		Ext.getCmp('moreInfoWinId').hide(); 
		        			}
		        		}]
           	     });

        		var imgWinAdd = new Ext.Window({
	        		title: '<fmt:message key="aimir.more.info"/>',
	                id: 'moreInfoWinId',
	                applyTo:'moreInfoAdd',
	                width:415,
	                height:400,
	                shadow : false,
	                autoHeight: true,
	                pageX : 300,
	                pageY : 130, 
	                resizable:false,
	                plain: true,
	                items: [fp_Add],
	                closeAction:'hide',	                
	                onHide : function(){
	                }       
	        	 
	            });	
        		
	        }else{	 
	        }
	        Ext.getCmp('moreInfoWinId').show();
        });
    });

</script>

</head>

<body>
<div id='moreInfoAdd'></div>

	<!--
	<div id="customerInfo-0">
		<ul style="clear:both;">
			<li><a href="#customerView-1"><fmt:message key="aimir.customerview"/></a></li>
			<li><a href="#customerFromFile-2">파일로부터 가져오기</a></li>
		</ul> -->
		<!-- 상단탭부분 : CustomerMax.jsp파일로 옮겨감(기능동작에 문제가 없는지 체크후 이 부분은 삭제요망), 활성화 시킬때는 클로징 </div> 태그도 함께 활성화 -->


		<div id="customerView-1">
			<div id="paneCustomerAdd">

						<form name='customerForm' id='customerForm' method='post'>
						<input type=hidden id='id' 			name='id' />
						<input type=hidden id='email' 		name='email' />
						<input type=hidden id='checkYN' 	name='checkYN' />
                        <input type=hidden id='loginIdCheckYN'     name='loginIdCheckYN' />

						<!-- 남아공 추가 요구 필드 START -->
						<input type=hidden id='identityOrCompanyRegNo' 	name='identityOrCompanyRegNo' />
						<input type=hidden id='initials' 				name='initials' />
						<input type=hidden id='vatNo' 					name='vatNo' />
						<input type=hidden id='workTelephone' 			name='workTelephone' />
						<input type=hidden id='postalAddressLine1' 		name='postalAddressLine1' />
						<input type=hidden id='postalAddressLine2' 		name='postalAddressLine2' />
						<input type=hidden id='postalSuburb' 			name='postalSuburb' />
						<input type=hidden id='postalCode' 				name='postalCode' />						
						<!-- 남아공 추가 요구 필드 END -->

						<div class="headspace">
							<label class="check"><fmt:message key='aimir.operator.addNewCustomer'/></label>
						</div>

						<table class="customer_detail">
						    <tr align="right">
                                  <td colspan="2" class="blue11pt"><fmt:message key="aimir.hems.inform.requiredField" />
                                  <!-- 필수 항목 표시 --></td>
                            </tr>
							<tr><th class="bluebold11pt"><em class="icon_star"></em><fmt:message key='aimir.customerid'/></th>
								<td>
                                    <span><input name="customerNo" id="customerNo" type="text" style="width:80px;"></span>
									<div id="btn">
                                        <span class="am_button margin-l10 margin-t1px">
                                            <a id="idOverlapCheck" class="on"><fmt:message key="aimir.checkDuplication2" /></a>
                                        </span>
                                        <span class="am_button margin-l10 margin-t1px">
                                            <a id="createNew" class="on"><fmt:message key="aimir.savingGoal.avgMgmt.create"/></a>
                                        </span>
                                    </div>
									<div id="checkValue" class="check-overlap check-overlap-customer" style="width:210px;"></div>
								</td>
							</tr>
							<tr><th class="bluebold11pt"><em class="icon_star"></em><fmt:message key="aimir.customername"/><!--고객명 --></th>
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
							    <!-- <td>
							    	<input name="address1" id="address1" type="text">
							    	<input name="address2" id="address2" type="text">
							    	<input name="address3" id="address3" type="text">
							    	<input name="address" id="address" type="text">
							    </td> -->
							</tr>
							<tr><th class="darkgraybold11pt"><fmt:message key="aimir.email"/><!-- Email --></th>
								<td><input name="email_1" id="email_1" type="text" style="width:60px;">
									<input type="text" value="@" class="between" readonly="readonly">
									<input name="email_2" id="email_2" type="text" style="width:139.5px;"></td>
							</tr>
							<tr><th class="darkgraybold11pt">Email <fmt:message key='aimir.operator.notificationSet'/><!-- 통보설정 --></th>
								<td class="gray11pt">
									<input name="emailYn" id="emailYn_1" type="radio" value="1" class="trans">
									<input type="text" value=<fmt:message key='aimir.allowReceive'/> class="border-trans" style="width:50px;">
									<input name="emailYn" id="emailYn_2" type="radio" value="0" class="trans" checked="checked">
									<input type="text" value=<fmt:message key='aimir.blocked'/> class="border-trans" style="width:80px;"></td>
							</tr>
							<tr><th class="darkgraybold11pt"><fmt:message key='aimir.tel.no'/></th>
								<td class="gray11pt">
									<input name="telephoneNo" id="telephoneNo" type="text"></td>
							</tr>
							<tr><th class="darkgraybold11pt"><fmt:message key='aimir.celluarphone'/></th>
								<td class="gray11pt">
									<input name="mobileNo" id="mobileNo" type="text"></td>
							</tr>
                            <tr><th class="darkgraybold11pt"><fmt:message key='aimir.loginId'/><!-- 로그인 아이디 --></th>
                                <td class="gray11pt">
                                    <span><input name="loginId" id="loginId" type="text" style="width:85px;"></span>
                                    <div id="btn">
                                        <span class="am_button margin-l10 margin-t1px">
                                            <a id="loginIdCheck" class="on"><fmt:message key="aimir.checkDuplication" /></a>
                                        </span>
                                    </div>
                                    <div id="loginIdCheckValue" class="check-overlap check-overlap-customer" style="width:210px;"></div>
                                </td>
                             </tr>
                                <tr id="loginInfo1" style="display: none"><th class="darkgraybold11pt"><fmt:message key='aimir.usergroup'/><!-- userGroup (Role) --></th>
                                    <td class="gray11pt">
                                        <select name="roleId" id="roleId" style="width:150px;"></select>
                                    </td>
                                </tr>
                                <tr id="loginInfo2" style="display: none"><th class="darkgraybold11pt"><fmt:message key='aimir.password'/><!-- password --></th>
                                    <td class="gray11pt">
                                        <span><input name="password" id="password" type="password" style="width:100px;"></span>
                                    </td>
                                </tr>
                                <tr id="loginInfo3" style="display: none"><th class="darkgraybold11pt"><fmt:message key='aimir.userreg.confirmpassword'/><!-- password --></th>
                                    <td class="gray11pt">
                                        <span><input name="passwordConfirm" id="passwordConfirm" type="password" style="width:100px;"></span>
                                    </td>
                                </tr>
							<tr>
								<th class="darkgraybold11pt">SMS <fmt:message key='aimir.operator.receiveSetting'/></th>
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
										<ul><li><a id='moreInfoAddBtn' class="on"><fmt:message key="aimir.add.more.info"/><!-- 정보 추가--></a></li></ul>
									</div>
								</td>
							</tr>				
						</table>

					</form>
			</div>

			<div class="btn_right_bottom">
				<span class="lightgray11pt"><fmt:message key='aimir.operator.customerRegister'/><!-- 위 고객을 신규 등록합니다. --></span>
				<span id="btn">
					<ul><li><a id='customerAdd' class="on"><fmt:message key='aimir.button.register'/><!-- 등록 --></a></li></ul>
				</span>
				<span id="btn">
					<ul><li><a id='customerCancel' class="on"><fmt:message key='aimir.cancel'/><!-- 취소 --></a></li></ul>
				</span>
			</div>

		</div>

		<!-- div id="customerFromFile-2">
			<div class="nuri_max_bor_bott_n" style="width:350px; margin:10px 0 0 0 " id="">파일로 부터 가져오기</div>
		</div-->
	<!-- </div> -->
</body>


</html>


