<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />


<title><fmt:message key="aimir.customerview" /></title>
<script>

    var customerId = ${customerId};
    var customerNo_ = '';
    var serviceType = '';
    
    $(function(){
        // editAuth 가 true 가 아니면 CUD 제한
        if (editAuth == "true") {
            $(".btn-addcontract").show();
            $(".btn_right_bottom").show();
        } else {
            $(".btn-addcontract").hide();
            $(".btn_right_bottom").hide();
        }

        init();
        detailInit();

        //고객정보 수정버튼 클릭 : 수정화면 이동
        $("#goCustomerUpdatePage").click(function() {
            detailInit();
            $("#pane-Customer-Update").load('${ctx}/gadget/system/customerMax.do?param=customerUpdateMax&customerId=' + customerId);
        });

        var delStr = "<fmt:message key="aimir.wantDelete"/>";//"삭제 하시겠습니까?"

        $("#customerDelete").click(function() {
            //alert(customerId);
            if ( confirm(delStr) ) {
                var options = {
                        success : customerDeleteResult,
                        url : '${ctx}/gadget/system/customerMax.do?param=customerDelete&customerId=' + customerId,
                        type : 'post',
                        datatype : 'json'
                    };
                        $('#customerForm_').ajaxSubmit(options);
                    } else
                        return;
        });

	    //new 계약 추가 버튼 클릭시 이벤트
        $('#contractAddBtn').click(function() {
            $('#contractStatusTab').hide();
            $('#contractAdd').show();

			MeterGridOn= false;

            $("#pane-Contract-Add").load('${ctx}/gadget/system/customerMax.do?param=contractAddMax&customerId=' + customerId + "&customerNo=" + customerNo );
        });
    }); //function End

    function init() {
        $('#customerInfo-0').show('fast', customerDetail());
        $('#customerInfo-0').tabs(1);
    }

    function detailInit() {
        //수정하기
        $("#pane-Customer-Update").hide();
        //내가 가진 에너지
        $("#pane-Contract-Energy").hide();
        //계약정보
        $("#pane-Contract-Info").hide();
        //에너지 리스트
        $("#pane-Energy").hide();
        //계약정보수정화면
        $("#pane-Contract-Info-Update").hide();
        $("#pane-creditType-prepay1").hide();
        $("#pane-creditType-prepay2").hide();
        $("#pane-creditType-prepay3").hide();
        $("#pane-creditType-prepay4").hide();
        $("#pane-Contract-Info").hide();
        $("#pane-Contract-Add").hide();
    }

    //고객상세정보
    function customerDetail() {

        detailInit();
		if(childrenYn == 'Y'){
			$('#contractInProgress').hide();
			$('#contractNoOngoing').hide();
		}else{
			if(contractCnt > 0){
				$('#contractInProgress').show();
				$('#contractNoOngoing').hide();
			}else{
				$('#contractInProgress').hide();
				$('#contractNoOngoing').show();
			}
		}
        
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=myCustomerView', {customerId:customerId},
            function(json) {
                var emailYnInfo = "";
                var smsYnInfo = "";

                if(json.customer != null){

                    emailYnInfo = json.customer.emailYn;
                    smsYnInfo = json.customer.smsYn;

                    customerNo_ = json.customer.customerNo;

                    var _InfoCustomerNo = json.customer.customerNo;

                    if(_InfoCustomerNo.length > 6){
                    	var _star = "";

                        for(var i=6; i<_InfoCustomerNo.length; i++){
                            _star += "*";
                        }

                    	_InfoCustomerNo = _InfoCustomerNo.substring(0,6) + _star;
                    }

                    $("#InfoCustomerNo").html(_InfoCustomerNo) ;
                    $("#InfoName1").html(json.customer.name);
                    $("#InfoName").html(json.customer.name);

                    var addr = json.customer.address;
                    var addr1 = json.customer.address1;
                    var addr2 = json.customer.address2;
                    var addr3 = json.customer.address3;
                    
                    if (addr1 != null && addr1 != "" && addr1 != "null" && addr1 != '"null"') {
                        $("#InfoAddress1").html(addr1.replaceAll("_","'"));
                    } else {
                        $("#InfoAddress1").html("");
                    }
                    if (addr2 != null && addr2 != "" && addr2 != "null" && addr2 != '"null"') {
                        $("#InfoAddress2").html(addr2.replaceAll("_","'"));
                    } else {
                        $("#InfoAddress2").html("");
                    }
                    if (addr3 != null && addr3 != "" && addr3 != "null" && addr3 != '"null"') {
                        $("#InfoAddress3").html(addr3.replaceAll("_","'"));
                    } else {
                        $("#InfoAddress3").html("");
                    }
                    if (addr != null && addr != "" && addr != "null" && addr != '"null"') {
                        $("#InfoAddress").html(addr.replaceAll("_","'"));
                    } else {
                        $("#InfoAddress").html("");
                    }

                    $("#InfoEmail").html(json.customer.email);
                    $("#InfoTelephoneNo").html(json.customer.telephoneNo);
                    $("#InfoMobileNo").html(json.customer.mobileNo);
                    
                    var loginId = json.customer.loginId;
                    if(loginId != null && loginId != "" && loginId != "null" && loginId != '"null"') {
                        $("#InfoLoginId").html(json.customer.loginId);
                    } else {
                        $("#InfoLoginId").html('');
                    }
                    //남아공 필드 추가
                    var identityOrCompanyRegNo = json.customer.identityOrCompanyRegNo;
                    var initials = json.customer.initials;
                    var vatNo = json.customer.vatNo;
                    var workTelephone = json.customer.workTelephone;
                    var postalAddress1 = json.customer.postalAddressLine1;
                    var postalAddress2 = json.customer.postalAddressLine2;
                    var postalSuburb = json.customer.postalSuburb;
                    var postalCode = json.customer.postalCode;
                    
                    if (identityOrCompanyRegNo != null 
                            && identityOrCompanyRegNo != "null" && identityOrCompanyRegNo != '"null"') {
						$("#identityOrCompanyRegNoDetail").attr("value" , identityOrCompanyRegNo);
                    }
					if (initials != null && initials != "null" && initials != '"null"') {
						$("#initialsDetail").attr("value" , initials);
					}
					if (vatNo != null && vatNo != "null" && vatNo != '"null"') {
						$("#vatNoDetail").attr("value" , vatNo);
					}
					if (workTelephone != null && workTelephone != "null" && workTelephone != '"null"') {
						$("#workTelephoneDetail").attr("value" , workTelephone);
					}
					if (postalAddress1 != null && postalAddress1 != "null" && postalAddress1 != '"null"') {
						$("#postalAddressLineDetail1").attr("value" , postalAddress1);
					}
					if (postalAddress2 != null && postalAddress2 != "null" && postalAddress2 != '"null"') {
						$("#postalAddressLineDetail2").attr("value" , postalAddress2);
					}
					if (postalSuburb != null && postalSuburb != "null" && postalSuburb != '"null"') {
						$("#postalSuburbDetail").attr("value" , postalSuburb);
					}
					if (postalCode != null && postalCode != "null" && postalCode != '"null"') {
						$("#postalCodeDetail").attr("value" , postalCode);
					}
                }else{
                    $("#InfoCustomerNo").html("") ;
                    $("#InfoName1").html("");
                    $("#InfoName").html("");
                    $("#InfoAddress1").html("");
                    $("#InfoAddress2").html("");
                    $("#InfoAddress3").html("");
                    $("#InfoAddress").html("");
                    $("#InfoEmail").html("");
                    $("#InfoTelephoneNo").html("");
                    $("#InfoMobileNo").html("");
                    //남아공 필드 추가
                    $("#identityOrCompanyRegNoDetail").html("");
                    $("#initialsDetail").html("");
                    $("#vatNoDetail").html("");
                    $("#workTelephoneDetail").html("");
                    $("#postalAddressLineDetail1").html("");
                    $("#postalAddressLineDetail2").html("");
                    $("#postalSuburbDetail").html("");
                    $("#postalCodeDetail").html("");    
                }

                var str1 = "<fmt:message key="aimir.allowReceive"/>";//수신허용
                var str2 = "<fmt:message key="aimir.blocked"/>";//수신거부

                if ( emailYnInfo == 1 )
                    emailYnInfo = str1;
                else if(emailYnInfo == "")
                    emailYnInfo = "";
                else
                    emailYnInfo = str2;
                $("#InfoEmailYn").html(emailYnInfo);

                if ( smsYnInfo == 1 )
                    smsYnInfo = str1;
                else if(smsYnInfo == "")
                    smsYnInfo = "";
                else
                    smsYnInfo = str2;

                $("#InfoSmsYn").html(smsYnInfo);

                //계약리스트 초기화
                /*
                $("#serviceTypeInfo").html("");

                if ( json.energyList != "" ) {
                    $.each(json.energyList , function(i, list) {
                        $("#serviceTypeInfo").append(
                            "<a style='cursor:hand;text-decoration:none' href='javascript:contractEnergy(" + list.id + ");'>"
                            + list.name + "&nbsp;|&nbsp;</a>"
                        );
                        serviceType = list.id;
                    });
                    $("#pane-Energy").show();
                }
                */
                $("#pane-Customer-Detail").show();
            }
        );
    }

    String.prototype.trim = function()
    {
     return this.replace(/(^\s*)|(\s*$)/gi, "");
    }

    String.prototype.replaceAll = function( str1, str2 )
    {
     var temp_str = this.trim();
     temp_str = temp_str.replace(eval("/" + str1 + "/gi"), str2);
     return temp_str;
    }

    //고객이 계약한 계약 리스트
    function contractEnergy(codeId) {
        $("#contractEnergy").html("");

        $.getJSON('${ctx}/gadget/system/customerMax.do?param=contractEnergy', {customerId:customerId , serviceTypeId:codeId },
                function(json) {
                    $.each(json.myEnergy , function(i, list) {
                        $("#contractEnergy").append(
                            "<a style='cursor:hand;text-decoration:none' href='javascript:contractInfoView("+ list[0] +")'>"
                            + list[1] + "&nbsp;|&nbsp;</a>"
                            );
                    });
                }
            );
        $("#pane-Contract-Energy").show();
    }

    //나의 계약정보
    function contractInfoView(contractId) {
        $('#pane-Contract-Info').load('${ctx}/gadget/system/customerMax.do?param=contractDetailMax&contractId=' + contractId + '&customerId=' + customerId + '&serviceType=' + serviceType);
    }

    //계약추가버튼
    function customerAdd() {
        detailInit();
        $("#pane-Contract-Add").load('${ctx}/gadget/system/customerMax.do?param=contractAddMax&customerId=' + customerId + "&customerNo=" + customerNo_ );
    }

    //폼값 리셋
    function reset() {
        customerAdd();
        $("#contractDemand").val('');
        $("#currentCredit").val('');
    }

    //지불타입 선불/후불 선택시
    function creditTypeSelect(creditId) {
    	//새로 계약생성시 prepayTypeselectBox 변경시 미수금과 충전금액, prepaymentThreshold의 초기화
    	$("#currentBalance").val(initCurrentBalance);
        $("#currentArrearsA").val(initArrears);
        $("#prepaymentThreshold").val(initAlertBalance);       
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCreditType', { codeId:creditId},
            function(json) {
                if ( creditId == 0 )
                    return;
                else {
                    //후불
                    if (  json.creditType.code == "2.2.0" ) {
                    	$('#meterDiv').css("left",825);
                        $("#pane-creditType-prepay1").hide();
                        $("#pane-creditType-prepay2").hide();
                        $("#pane-creditType-prepay3").hide();
                        $("#pane-creditType-prepay4").hide();
                        $("#pane-creditType-prepay-update").hide();
                        
                        //$("meterDiv").css("left",);
                    }
                    //선불
                    if ( json.creditType.code == "2.2.1" ) {
                    	$('#meterDiv').css("left",890);
                        $("#pane-creditType-prepay1").show();
                        $("#pane-creditType-prepay2").show();
                        $("#pane-creditType-prepay3").show();
                        $("#pane-creditType-prepay4").show();
                        $("#pane-creditType-prepay-update").show();
                    }
                }
            }
        );
    }
	
    var imgWinDetail;
    $(function() {
        $("#moreInfoDetailBtn").click(function() {
        	if(Ext.getCmp('moreInfoWinIdDetail') == undefined){
        		  var fp_Detail = new Ext.FormPanel ({
        			labelWidth: 180,
                    frame:true,
                    width: 400,
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
						        [{fieldLabel : '<fmt:message key="aimir.more.identityOrCompanyRegNo"/>',  // fieldLabel : 필드의 이름표
						         xtype : 'textfield',
						         id	 : 'fp_identityOrCompanyRegNoDetail',
						         name : 'fp_identityOrCompanyRegNoDetail',// name : 폼데이터가 서버에 보내질때 매개변수 이름으로 사용
						         value: $("#identityOrCompanyRegNoDetail").val(),
			                     style: {
			                     border: '0px',
			                     backgroundColor:'#DFE8F6',
                                 backgroundImage:'url(../../images/blue-box.png)'
			                     //backgroundImage:'none',
			                     },
						         readOnly:true,
						         allowBlank : true},// (유효성검증) 필수값 체크
						         {fieldLabel : '<fmt:message key="aimir.more.initials"/>' ,
						          id : 'fp_initialsDetail',
						          name : 'fp_initialsDetail',
						          value: $("#initialsDetail").val(),
						          style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
						          readOnly:true,
						          allowBlank : true},
						          {fieldLabel : '<fmt:message key="aimir.more.vatNo"/>',
						          id	: 'fp_vatNoDetail',
						          name : 'fp_vatNoDetail',
						          value: $("#vatNoDetail").val(),
						          style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
						          readOnly:true,
						          allowBlank : true}]
		              } ,{ xtype:'fieldset',
		                   title: '<fmt:message key="aimir.more.contractDetails"/>',
		                   bodyStyle:'padding:5px 5px 5px 5px',
		                   //collapsible: true,
		                   autoHeight:true,
		                   defaultType: 'textfield',        	    
					       items :[
					               {fieldLabel : '<fmt:message key="aimir.more.workTelephone"/>',
					               id : 'fp_workTelephoneDetail', 
					               name : 'fp_workTelephoneDetail',
					               value: $("#workTelephoneDetail").val(),
					               style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
					               readOnly:true,
					               allowBlank : true}]
		               },{ xtype:'fieldset',
		                   title: '<fmt:message key="aimir.more.postalAddress"/>',
		                   bodyStyle:'padding:5px 5px 5px 5px',
		                   //collapsible: true,
		                   autoHeight:true,
		                   defaultType: 'textfield',        	    
					       items :[
					               {fieldLabel : '<fmt:message key="aimir.more.addressLine1"/>',
					                id : 'fp_postalAddressLineDetail1',
					                name : 'fp_postalAddressLineDetail1',
					                value: $("#postalAddressLineDetail1").val(),
					                style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
					                readOnly:true,
					                allowBlank : true},
					                {fieldLabel : '<fmt:message key="aimir.more.addressLine2"/>', 
					                 id : 'fp_postalAddressLineDetail2',
					                 name : 'fp_postalAddressLineDetail2',
					                 value: $("#postalAddressLineDetail2").val(),
					                 style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
					                 readOnly:true,
					                 allowBlank : true},
					                 {fieldLabel : '<fmt:message key="aimir.more.suburb"/>',
					                 id : 'fp_postalSuburbDetail',
					                 name : 'fp_postalSuburbDetail',
					                 value: $("#postalSuburbDetail").val(),
					                 style: {
					                     border: '0px',
					                     backgroundColor:'#DFE8F6',
					                     backgroundImage:'url(../../images/blue-box.png)'
					                     },
					                 readOnly:true,
					                 allowBlank : true},
					                 {fieldLabel : '<fmt:message key="aimir.more.postalCode"/>',
					                  id : 'fp_postalCodeDetail', 
					                  name : 'fp_postalCodeDetail',
					                  value: $("#postalCodeDetail").val(),
					                  style: {
						                     border: '0px',
						                     backgroundColor:'#DFE8F6',
						                     backgroundImage:'url(../../images/blue-box.png)'
						                     },
					                  readOnly:true,
					                  allowBlank : true}          	         
		   				]// end form Panel items
                    }], 
 					  
   					buttons : [{
   						text : '<fmt:message key="aimir.ok"/>',
		            	handler : function() {		   
		            		imgWinDetail = undefined;
		            		fp_Detail =undefined;
		            		Ext.getCmp('moreInfoWinIdDetail').close();
		            		var $body = $('body');
		            		var divField = document.createElement("div");
		            		divField.setAttribute("id", "moreInfoDetail");
		            		$body.append(divField);
	                		}
		        		}]
           	     });                

        		imgWinDetail = new Ext.Window({
					title : '<fmt:message key="aimir.more.info"/>',
					id : 'moreInfoWinIdDetail',
					applyTo : 'moreInfoDetail',
					width : 415,
					height : 400,
					shadow : false,
					autoHeight : true,
					pageX : 300,
					pageY : 130,
					resizable : false,
					plain : true,
					items : [fp_Detail],
					closeAction : 'hide',
					onHide : function() {
					}

				});
			} else {
				Ext.getCmp('fp_identityOrCompanyRegNoDetail').setValue($("#identityOrCompanyRegNoDetail").val());
				Ext.getCmp('fp_initialsDetail').setValue($("#initialsDetail").val());
				Ext.getCmp('fp_vatNoDetail').setValue($("#vatNoDetail").val());
				Ext.getCmp('fp_workTelephoneDetail').setValue($("#workTelephoneDetail").val());
				Ext.getCmp('fp_postalAddressLineDetail1').setValue($("#postalAddressLineDetail1").val());
				Ext.getCmp('fp_postalAddressLineDetail2').setValue($("#postalAddressLineDetail2").val());
				Ext.getCmp('fp_postalSuburbDetail').setValue($("#postalSuburbDetail").val());
				Ext.getCmp('fp_postalCodeDetail').setValue($("#postalCodeDetail").val());
			}  

			Ext.getCmp('moreInfoWinIdDetail').show();
		});
	}); 

</script>
</head>


<body>
<div id='moreInfoDetail'></div>
<div id='moreInfoUpdate'></div>
	<!--
	<div id="customerInfo-0">
		<ul style="clear:both;">
			<li><a href="#customerView-1"><fmt:message key="aimir.customerview"/></a></li>
			<li><a href="#customerFromFile-2">파일로부터 가져오기</a></li>
		</ul> -->
	<!-- 상단탭부분 : CustomerMax.jsp파일로 옮겨감(기능동작에 문제가 없는지 체크후 이 부분은 삭제요망), 활성화 시킬때는 클로징 </div> 태그도 함께 활성화 -->


	<!-- customerView-1 (S) -->
	<div id="customerView-1">
		<div id="pane-Customer-Detail">

						<!-- 남아공 추가 요구 필드 START -->
						<input type=hidden id='identityOrCompanyRegNoDetail' 	name='identityOrCompanyRegNoDetail' />
						<input type=hidden id='initialsDetail' 					name='initialsDetail' />
						<input type=hidden id='vatNoDetail' 					name='vatNoDetail' />
						<input type=hidden id='workTelephoneDetail' 			name='workTelephoneDetail' />
						<input type=hidden id='postalAddressLineDetail1' 		name='postalAddressLineDetail1' />
						<input type=hidden id='postalAddressLineDetail2' 		name='postalAddressLineDetail2' />
						<input type=hidden id='postalSuburbDetail' 				name='postalSuburbDetail' />
						<input type=hidden id='postalCodeDetail' 				name='postalCodeDetail' />	 					
						<!-- 남아공 추가 요구 필드 END -->

			<div class="headspace">
				<span><label class="check" id="InfoName1"></label></span><span
					class="nocheck gray11pt"><fmt:message
						key='aimir.operator.userDetail' />
					<!-- 님의 상세정보 --></span>
			</div>

			<table class="customer_detail" style="width: 350px;">
				<tr>
					<th class="bluebold11pt"><fmt:message key="aimir.customerid" />
						<!-- 고객번호 --></th>
					<td><div id="InfoCustomerNo" class="input-fake blue11pt"></div></td>
				</tr>
				<tr>
					<th class="bluebold11pt"><fmt:message key="aimir.customername" />
						<!--고객명 --></th>
					<td><div id="InfoName" class="input-fake blue11pt"></div></td>
				</tr>
				<tr>
					<th class="darkgraybold11pt"><fmt:message
							key="aimir.customeraddress" />
						<!--고객주소 --></th>
					<td style="padding: 0">
						<table>
							<tr><td style="border-color: #FFF"><div id="InfoAddress" class="input-fake"></div></td></tr>
							<tr><td style="border-color: #FFF"><div id="InfoAddress1" class="input-fake"></div></td></tr>
							<tr><td style="border-color: #FFF"><div id="InfoAddress2" class="input-fake"></div></td></tr>
							<tr><td style="border-color: #FFF"><div id="InfoAddress3" class="input-fake"></div></td></tr>
						</table>
					</td>
				</tr>
				<tr>
					<th class="darkgraybold11pt"><fmt:message key="aimir.email" />
						<!-- Email --></th>
					<td><div id="InfoEmail" class="input-fake"></div></td>
				</tr>
				<tr>
					<th class="darkgraybold11pt"><fmt:message key='aimir.email' />
						<fmt:message key='aimir.operator.notificationSet' />
						<!-- 통보설정 --></th>
					<td class="gray11pt"><div id="InfoEmailYn" class="input-fake"></div></td>
				</tr>
				<tr>
					<th class="darkgraybold11pt"><fmt:message key="aimir.tel.no" />
						<!--유선전화 --></th>
					<td class="gray11pt"><div id="InfoTelephoneNo"
							class="input-fake"></div></td>
				</tr>
				<tr>
					<th class="darkgraybold11pt"><fmt:message
							key="aimir.celluarphone" />
						<!--핸드폰번호--></th>
					<td class="gray11pt"><div id="InfoMobileNo" class="input-fake"></div></td>
				</tr>
                <tr>
                    <th class="darkgraybold11pt"><fmt:message key="aimir.loginId" /><!--로그인 아이디--></th>
                    <td class="gray11pt"><div id="InfoLoginId" class="input-fake"></div></td>
                </tr>
				<tr>
					<th class="darkgraybold11pt">SMS <fmt:message
							key='aimir.operator.receiveSetting' /></th>
					<td class="gray11pt"><div id="InfoSmsYn" class="input-fake"></div></td>
				</tr>
				<tr>
					<th class="bluebold11pt"><fmt:message key="aimir.more.info" />
					<td style="padding: 0">
						<div id="btn" style="padding-left: 5px">
							<ul>
								<li><a id='moreInfoDetailBtn' class="on"><fmt:message key="aimir.see.more.info" /><!-- 정보 추가--></a></li>
							</ul>
						</div>
					</td>
				</tr>
				<tr class="last">
					<th class="bluebold11pt"><fmt:message key="aimir.operator.contractStatus" />
						<!--계약현황--></th>
					<td>
						<div id=contractInProgress>
							<span class="blue11pt"><fmt:message key="aimir.contractInProgress" /></span>
							<!--진행중인 계약이 있습니다.-->
						</div>
						<div id="contractNoOngoing" style="display: none;">
							<span class="gray11pt"><fmt:message key="aimir.contractNoOngoing" />
								<!-- 진행중인 계약이 없습니다. --> </span>
						</div>
						<div id="btn" class="btn-addcontract">
							<ul>
								<li><a id='contractAddBtn' class="on-bold"><fmt:message key="aimir.newAgreement" />
										<!-- 새 계약 추가--></a></li>
							</ul>
						</div>

					</td>
				</tr>
			</table>

			<div class="btn_right_bottom"
				style="position: relative; z-index: 100 !important;">
				<span id="btn">
					<ul>
						<li><a id='goCustomerUpdatePage' class="on"><fmt:message key="aimir.update" />
								<!-- 수정 --></a></li>
					</ul>
				</span> <span id="btn">
					<ul>
						<li><a id='customerDelete' class="on"><fmt:message key="aimir.button.delete" />
								<!-- 삭제 --></a></li>
					</ul>
				</span>
			</div>

		</div>



	</div>
	<!-- customerView-1 (S) -->








	<!--계약추가-->
	<!-- <div id="pane-Contract-Add"></div> -->
	<!--계약추가끝-->

	<!-- 계약 정보 시작 -->
	<div id="pane-Contract-Info"></div>
	<!-- 계약 정보 끝 -->

	<!-- 계약정보 수정 화면 -->
	<div id="pane-Contract-Info-Update"></div>
	<!-- 계약정보 수정 화면끝 -->

	<!-- 고객 정보 수정  -->
	<div id="pane-Customer-Update"></div>
	<!-- 고객 정보 수정  끝-->

	<!-- div id="fragment-1" end -->

	<!-- div id="customerFromFile-2">
			<div class="nuri_max_bor_bott_n" style="width:350px; margin:10px 0 0 0 " id="">파일로 부터 가져오기</div>
		</div-->



</body>
</html>