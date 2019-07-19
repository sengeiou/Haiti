<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->
	<title>ManualMetering</title>
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/mvm/imports_style.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/mvm/manualMetering.css"/>
</head>
<body>
	<div id="wrapper" class="min">
	
		<section class="top-wrapper">
		
		 	<div id="manual-metering-tabs">
		        <ul>
		            <li><a href="#manual_metering_search"><fmt:message key='aimir.button.register'/></a></li>
		        </ul>
				<div id="manual_metering_search" class="tab-item">
					<form action="writeManualMetering" id="manual_metering_regist_form" class="dynamic-form">
					<table id="metering-common-template" class="form-table search">
						<tbody>
							<tr>
								<th>
									<!-- <select id="metering-day-type" class="local-select select" name="dayType">
										<option value="1"><fmt:message key='aimir.daily'/></option>
										<option value="4"><fmt:message key='aimir.day.mon'/></option>
									</select> -->
									<fmt:message key='aimir.daily'/>
								</th>
								<td id="datepicker-area" colspan="2"></td>
							</tr>
							
							<tr>
								<th class="form-label" data-require="true">
										<fmt:message key='aimir.meterid'/>
									</th>
									<td colspan="2">
										<select id="manualMeterSelectbox" name="mdsId" class="select"></select>
									</td>
								</tr>
							<tr>
								<th class="form-label" data-require="true"><fmt:message key='aimir.mvm.manualmeter.meteringvalue'/></th>
								<td>
									<input type="text" name="meteringValue">
								</td>
								
								<td>
								</td>
							</tr>
						</tbody>
					</table>
					<div class="btn-confirm">
						<em class="hm_button"><a class="submit"><fmt:message key="aimir.button.confirm"/></a></em>&nbsp;
						<em class="hm_button"><a class="reset"><fmt:message key="aimir.cancel"/></a></em>
					</div>
					</form>
				</div>
			</div>
		</section>
		
		<section class="bottom-wrapper">
			<div id="manual_metring_type_grid"></div>
		</section>
	</div>
	
	<!-- include javascripts -->
	<script type="text/javascript" src="${ctx}/js/framework/Config/mvm/fmtMessage.do"></script>
	<script type="text/javascript">
		GLOBAL_CONTEXT.GADGET = "ManualMetering";
		GLOBAL_CONTEXT.SIZE = "MIN";
	</script>	
	<script data-main="${ctx}/js/mvm_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
	
</body>
</html>