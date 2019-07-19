<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview"/></title>
<script>

	var contractId = ${contractId};
	var serviceType = ${serviceType}; //serviceType id
	var supplier = ${supplier};

	$(document).ready(function() {
		updateForm();
		$("#writeDatetime").datepicker(  { position: 'bottom' } );
	});

	//계약정보에서 수정하기 눌렀을 때
	function updateForm() {
	
		$("#pane-Contract-Info-Update").show();
		$("#pane-creditType-prepay-update").hide();
		//ㄱㅖ약정보 DIV
		$("#pane-Contract-Info").hide();

		$.getJSON('${ctx}/gadget/system/customerMax.do?param=contractInfoUpdate'
		
		//data 
		, {contractId:contractId 
		, serviceType:serviceType},
			function(json) {
				//서비스타입
				$("#serviceTypeCode1").pureSelect(json.serviceList);
				$("#serviceTypeCode1 option[value=" + json.service.id + "]").attr("selected", "true");
				//공급지역
				$("#location1").pureSelect(json.locationList);
				$("#location1 option[value=" + json.location.id + "]").attr("selected", "true");
				//계약종별
				$("#tariffIndex1").pureSelect(json.tariffTypeList);
				
				
				
				
				$("#tariffIndex1 option[value=" + json.tariff.id + "]").attr("selected", "true");
				//계약용량
				$("#contractDemand1").attr("value" , json.contract.contractDemand);
				//공급상태
				$("#status1").pureSelect(json.statusList);
				$("#status1 option[value=" + json.status.id + "]").attr("selected", "true");
				//지불타입
				$("#creditType1").pureSelect(json.creditTypeList);
				$("#creditType1 option[value=" + json.creditType.id + "]").attr("selected", "true");

				//지불타입이 선불일때 : 선불 2.2.1
				$("#creditStatus1").pureSelect(json.creditStatusList);

				if (json.creditType.code == "2.2.1" ) {
					//지불상태
					$("#creditStatus1 option[value=" + json.creditStatus.id + "]").attr("selected", "true");
					//잔액
					$("#currentCredit1").attr("value" , json.contract.currentCredit);
					$("#pane-creditType-prepay-update").show();
				}
			}
		);
	}

	function getUpdateTariffList(serviceType) {

		$.getJSON('${ctx}/gadget/system/customerMax.do?param=getTariffList', {serviceType:serviceType, supplier:supplier},
			function(json) {
				$("#tariffIndex1").pureSelect(json.tariff);
			}
		);
	}

	//계약정보 수정화면 취소버튼
	$("#cancel").click(function() {
		//$("#pane-Contract-Info-Update").hide();
		//ㄱㅖ약정보 DIV
		//$("#pane-Contract-Info").show();
		customerDetail();
	});
	

	//수정하기
	$("#update").click(function() {

		var intHours, intMinutes, intSeconds;
		var today;
		today = new Date();
		intHours = today.getHours();
		intMinutes = today.getMinutes();
		intSeconds = today.getSeconds();

		if ( $("#writeDatetime").val() == "" || $("#writeDatetime").length == 0 ) {
			Ext.Msg.alert('<fmt:message key='aimir.message'/>',"수정일을 선택 해 주세요");
			$("#writeDatetime").focus();
			return;
		}

		var writeDatetime = $("#writeDatetime").val().replace("/", "").replace("/","") + intHours+intMinutes+intSeconds;
		$("#writeDatetime").attr("value" , writeDatetime);

		if ( confirm('<fmt:message key="aimir.update.want"/>') ) {
			if ($("#currentCredit1").val() == "" ) {
				$("#currentCredit1").val(0);
			}
			var options = {
				success : contractUpdateResult,
		        url : '${ctx}/gadget/system/customerMax.do?param=contractUpdate',
		        type : 'post',
		        datatype : 'json'
			};
			$('#contractFormUpdate').ajaxSubmit(options);
		} else
			return;
	})


	function contractUpdateResult(responseText, status) {
		Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
		$("#pane-Contract-Info-Update").hide();
		customerDetail();
	}

	
</script>

</head>

<body>
<form id="contractFormUpdate" name="contractFormUpdate" method="post">
<input type="hidden" name="id" id="contractId" value="${contractId}" />
<input type="hidden" name="customer" id="customer" value="${customerId}" />

<!-- 계약 정보 수정 시작 -->
	<div style="border:1px solid #000; float:left; width:100%; border-top:none; display:block; padding:0">
		<h2 style="padding:15px 0 10px 10px; float:left; width:100%"><fmt:message key="aimir.contractInfo"/> <fmt:message key="aimir.update"/></h2>
		<div style="padding:0 10px 0 10px">
			<div class="nuri_max_bor_bott_n" style="width:350px; margin:10px 0 0 0" id="">
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.buildingMgmt.applyDate"/></li>
					<li>
						<input id="writeDatetime" name="writeDatetime" type="text" class="nuri_search" style="width: 100px; padding: 5px 5px 6px 5px;" readonly>
					</li>
				</ul>

				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.supply.type"/></li>
					<li>
						<select name="serviceTypeCode" id="serviceTypeCode1" class="nuri_search_n" style="width:94px; padding:4px"
						onchange="javascript:getUpdateTariffList(document.getElementById('serviceTypeCode1').options[document.getElementById('serviceTypeCode1').selectedIndex].value);"
						></select>
					</li>
				</ul>
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.location.supplier"/></li>
					<li style="width:110px">
						<select name="location" id="location1" class="nuri_search_n" style="width:94px; padding:2px"></select>
					</li>
				</ul>


				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.contract.tariff.type"/></li>
					<li style="width:110px">
						<select name="tariffIndex" id="tariffIndex1" class="nuri_search_n" style="width:104px; padding:2px"

						></select>
					</li>
				</ul>

				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.contract.demand.amount"/></li>
					<li><input name="contractDemand" id="contractDemand1" type="text" class="nuri_search_n" style="width:70px; padding:2px"> </li>
				</ul>
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.supplystatus"/></li>
					<li style="width:110px">
						<select name="status" id="status1" class="nuri_search_n" style="width:94px; padding:2px"></select>
					</li>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.paymenttype"/></li>
					<li style="width:110px">
						<select name="creditType" id="creditType1" class="nuri_search_n" style="width:94px; padding:2px"
						onchange="javascript:creditTypeSelect(document.getElementById('creditType1').options[document.getElementById('creditType1').selectedIndex].value);"></select>
					</li>
				</ul>

				<div id=pane-creditType-prepay-update style=display:none>
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.paymentstatus"/></li>
					<li>
						<select name="creditStatus" id="creditStatus1" class="nuri_search_n" style="width:94px; padding:2px"></select>
					</li>
				</ul>
				<ul>
					<li><fmt:message key="aimir.usingBalanceOfFee"/>
					  <input name="currentCredit" id="currentCredit1" type="text" class="nuri_search_n" style="width:40px; padding:2px">
					<fmt:message key="aimir.ifNoticeLessThen"/></li>
				</ul>
				</div>

				<ul>
					<li style="padding:0 10px 0 120px">
						<div id="btn">
							<ul>
								<li class="input"><a id=update><span><fmt:message key="aimir.save2" /></span></a></li>
							</ul>
						</div>
					</li>
					<li>
						<div id="btn">
							<ul>
								<li class="nuri_cancel"><a id=cancel><span ><fmt:message key="aimir.cancel" /></span></a></li>
							</ul>
						</div>
					</li>
				</ul>
				<ul><li>&nbsp;</li></ul>
			</div>
		</div>
	</div>
<!-- 계약 정보 수정 끝 -->
</form>

</body>
</html>