<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview"/></title>
<script>

	var contractId = ${contractId}; //계약id
	var customerId = ${customerId}; //고객id
	var serviceType = ${serviceType}; //serviceType id

	$(function(){
		init();

		$("#pane-Contract-Info").show();

		
		$("#contractInfoUpdateForm").click(function() 
		{
		
			//매터 그리드 인스턴스가 존재하면 삭제
			if (MeterGridOn == true)
	    	{
	        		MeterGrid.destroy();
	        		
	        		//MeterGrid 인스턴스가 존재 하지 않음으로 설정.
	        		MeterGridOn = false;
	  		}
		
			
		
			detailInit();
			$("#pane-Contract-Info-Update").load('${ctx}/gadget/system/customerMax.do?param=contractUpdateMax&contractId=' + contractId + '&customerId=' + customerId +"&serviceType=" + serviceType);
		});

		$("#contractDel").click(function() {
			if ( confirm("<fmt:message key='aimir.msg.deleteconfirm' />") ) {
				var options = {
						success : contractDeleteResult,
				        url : '${ctx}/gadget/system/customerMax.do?param=contractDelete&contractId=' + contractId,
				        type : 'post',
				        datatype : 'json'
					};
						$('#contract').ajaxSubmit(options);
					} else
						return;
		});
	});

	function contractDeleteResult(responseText, status) {
		Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
		customerDetail();
	}

	function init() {
		$('#contractDetail-0').show('fast', contractDetail());
        $('#contractDetail-0').subtabs(1);
	}

	
	//계약 상세 정보 가져오기
	function contractDetail() {

		$.getJSON('${ctx}/gadget/system/customerMax.do?param=contractInfo', {id:contractId},
			function(json) 
			{
				$("#tariffIndexInfo").html(json.tariff.name);
				$("#locationInfo").html(json.location.name);
				$("#statusInfo").html(json.status.name);
				$("#creditTypeInfo").html(json.creditType.name);
				$("#contractDemandInfo").html(json.contract.contractDemand + "&nbsp;");
				
				//미터 id 값
				$("#mdsMeterId").html(json.contract.meterId + "&nbsp;");

				if ( json.creditType.code == "2.2.1" ) {
					$("#creditStatusInfo").html(json.creditStatus.name);
					$("#currentCreditInfo").html('<fmt:message key="aimir.usingBalanceOfFee" />&nbsp;' + json.contract.currentCredit + '% <fmt:message key="aimir.ifNoticeLessThen" />');
					$("#pane-creditStatus").show();
				}
			}
		);
	}

</script>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">

</head>

<body>
<form name=contract id=contract method=post>
</form>


    <!-- Tab 1-2-3-4 (S) -->
	<div id="contractDetail-0">
		<ul>
			<li><a href="#contract-1"><fmt:message key="aimir.contractInfo" />123123312</a></li>
			<li><a href="#contract-2"><fmt:message key="aimir.contractChange" /></a></li>
			<li><a href="#contract-3"><fmt:message key="aimir.prepaidPlan" /></a></li>
			<li><a href="#contract-4"><fmt:message key="aimir.monthly.usage" /></a></li>
		</ul>

		<div id="contract-1">
			<div id="계약정보">
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.contract.tariff.type"/></li>
					<li style="width:110px"><div id=tariffIndexInfo></div></li>
				</ul>
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.location.supplier"/></li>
					<li style="width:110px"><div id=locationInfo></div></li>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.contract.demand.amount"/></li>
					

					<li><div id=contractDemandInfo></div></li>
				</ul>
				<ul>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.supplystatus"/></li>
					<li style="width:110px"><div id=statusInfo></div></li>
					<li style="width:60px; font-weight:bold"><fmt:message key="aimir.paymenttype"/></li>
					<li><div id=creditTypeInfo></div></li>
				</ul>

				<div id=pane-creditStatus style=display:none>
					<ul>
						<li style="width:60px; font-weight:bold"><fmt:message key="aimir.paymentstatus"/></li>
						<li style="width:110px"><div id=creditStatusInfo></div></li>
					</ul>
					<ul>
						<li><div id=currentCreditInfo></div></li>
					</ul>
				</div>

				<ul>
					<li>
						<div id="btn">
							<ul>
								<li class="input"><a id=contractInfoUpdateForm><span ><fmt:message key="aimir.update" /></span></a></li>
							</ul>
						</div>
					</li>
					<li>
						<div id="btn">
							<ul>
								<li class="nuri_cancel"><a id=contractDel><span><fmt:message key="aimir.button.delete" /></span></a></li>
							</ul>
						</div>
					</li>
				</ul>
			</div>
		</div>

	</div>
    <!-- Tab 1-2-3-4-(E) -->


</body>

</html>