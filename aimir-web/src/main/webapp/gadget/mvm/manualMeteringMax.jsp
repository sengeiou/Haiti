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
	<div id="wrapper" class="max">
		<section class="top-wrapper">		
		 	<div id="manual-metering-tabs">
		        <ul>
		            <li><a href="#manual_metering_search"><fmt:message key='aimir.button.register'/></a></li>
		            <li><a href="#regist_manual_metering"><fmt:message key='aimir.mvm.manualmeter.registmeter'/></a></li>
		        </ul>
				<div id="manual_metering_search" class="tab-item">
					<form action="writeManualMetering" id="manual_metering_regist_form" 
						class="dynamic-form">
					<table id="metering-common-template" class="form-table search">
						<tbody>
							<tr>
								<th>
									<fmt:message key='aimir.daily'/>
								</th>
								<td id="datepicker-area">
								</td>
								<th class="form-label" data-require="true">
									<fmt:message key='aimir.meterid'/>
								</th>
								<td>
									<select id="manualMeterSelectbox" name="mdsId" class="select">
									</select>
								</td>
								<th class="form-label" data-require="true"><fmt:message key='aimir.mvm.manualmeter.meteringvalue'/></th>
								<td>
									<input id="manualValue" type="text" name="meteringValue">
								</td>
								<td>
									<em class="hm_button">
										<a class="submit">
											<fmt:message key="aimir.button.confirm"/>
										</a>
									</em>
									<em class="hm_button">
										<a class="reset">
											<fmt:message key="aimir.cancel"/>
										</a>
									</em>
								</td>
								<td class="table-end-space"></td>
							</tr>
						</tbody>
					</table>					
					</form>
				</div>
				<div id="regist_manual_metering" class="tab-item">
					<form id="addNewMeter" action="addNewMeter">	
					<input type="hidden" name="modem.deviceSerial" value="" />			
					<table class="form-table search">
						<tr>
							<th class="form-label" data-require="true"><fmt:message key="aimir.metertype"/></th>
							<td>
								<select class="meter-type-select" id="singleRegMeterMeterType" name="meterType.id"></select>
							</td>
							<th class="form-label" data-require="true"><fmt:message key="aimir.meterid"/></th>
							<td>
								<input type="text" id="singleRegMeterMdsId" name="mdsId">
							</td>
							<td>
								<em class="hm_button">
									<a id="meterDuplicate"><fmt:message key="aimir.checkDuplication"/></a>
								</em>
							</td>	
							<td colspan="2"></td>						
						</tr>
						<tr>
							<th class="form-label" data-require="true"><fmt:message key="aimir.vendor"/></th>
							<td>
								<select id="singleRegMeterVendor" name="model.deviceVendor.id"></select>
							</td>
							<th class="form-label"><fmt:message key="aimir.model"/></th>
							<td data-require="true" colspan="2">
								<select id="singleRegMeterModel" name="model.id"></select>
							</td>
							<th class="form-label"><fmt:message key="aimir.portnumber"/></th>
							<td colspan="2">
								<input type="text" id="singleRegMeterPort" name="modemPort">
							</td>
						</tr>
						<tr>
							<th class="form-label" data-require="true"><fmt:message key="aimir.location"/></th>
							<td>
							    <input name="searchWord" id='searchWord' class="billing-searchword" type="text"/>
					         	<input type='hidden' id='locationId' name="location.id" value='' />
					        </td>
							<th class="form-label">
								<fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/>
							</th>							
							<td colspan="2">
								<input type="text" id="singleRegMeterLocDetail" name="address">
								<input type="hidden" id="supplierId" name="supplier.id" value=""/>
							</td>
							<th class="form-label">
								<fmt:message key="aimir.name"/>
							</th>
							<td colspan="2">
								<input type="text" name="friendlyName">			
							</td>
						</tr>
					</table>
					<div>
						<input id="manual-meter-check" type="checkbox" checked="checked" /><fmt:message key="aimir.manualmeter.registManualMeter"/>
					</div>
					<div id="treeDivMeterOuter" class="tree-billing auto" style='display:none;'>
                    	<div id="treeDivMeter"></div>
                    </div>
					<div class="btn-confirm">
						<em class="hm_button"><a class="submit"><fmt:message key="aimir.button.confirm"/></a></em>
						<em class="hm_button"><a class="reset"><fmt:message key="aimir.cancel"/></a></em>
					</div>
					
					</form>
				</div>
				
			</div>
		</section>

		<div class="dashedline"></div>
		
		<section class="bottom-wrapper">
			<div id="manual_metring_result">
			<ul>
	            <li><a href="#metering-search"><fmt:message key='aimir.mvm.manualmeter.search'/></a></li>
	            <li><a href="#metering-chart">
	            	<fmt:message key='aimir.mvm.manualmeter.usagechart'/>		            	
	            </a></li>
	        </ul>
			<div id="metering-search" class="tab-item">
				<form action="searchMeteringGrid">
				<table id="manual-metering-grid" class="form-table search">
					<tr>
						<th>
							<fmt:message key='aimir.mvm.manualmeter.duration'/>							
						</th>
						<td id="period-search-datepicker">
						</td>
						<th class="form-label">
							<fmt:message key='aimir.metertype'/>
						</th>
						<td>
							<select name="meterType" class="meter-type-select select"></select>
						</td>
						<th class="form-label">
							<fmt:message key='aimir.meterid'/>
						</th>
						<td>
							<input type="text" name="mdsId">
						</td>
						<th class="form-label">
							<fmt:message key='aimir.mvm.manualmeter.name'/>							
						</th>
						<td>
							<input type="text" name="friendlyName">
						</td>
						<td>
							<em class="hm_button">
								<a class="submit"><fmt:message key="aimir.button.search"/></a>
							</em>							
							<em class="hm_button">
								<a class="reset"><fmt:message key="aimir.cancel"/></a>
							</em>
						</td>
					</tr>
				</table>
				<div class="btn-confirm excel_button">
					<em class="hm_button">
						<a class="excel"><fmt:message key="aimir.button.excel"/></a>
					</em>
				</div>
				</form>
				
				<div id="manual_metring_type_grid"></div>
			</div>
			
			<div id="manual_metering_update_window"></div>
			
			<div id="metering-chart" class="tab-item">
				<div id="manualMeterGrid" class="manual-meter-selector">
				</div>			
				<ul id="meterStatChartArea" class="meter-stat-charts floatlist">
					<li>
						<div class="chart-title">
							<span><fmt:message key='aimir.day'/> <fmt:message key='aimir.usage'/></span>
						</div>
						<div id="dayLimitList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span><fmt:message key='aimir.weeklyusage'/></span>
						</div>
						<div id="weekList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span><fmt:message key='aimir.monthly.usage'/></span>
						</div>
						<div id="monthList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span><fmt:message key='aimir.quarterly'/> <fmt:message key='aimir.usage'/></span>
						</div>
						<div id="seasonList_chart"></div>
					</li>
				</ul>				
				<div class="g_clear"></div>
			</div>
			</div>
		
		</section>
	</div>
	
	<!-- include javascripts -->
	<script type="text/javascript" src="${ctx}/js/framework/Config/mvm/fmtMessage.do"></script>
	<script type="text/javascript">
		GLOBAL_CONTEXT.GADGET = "ManualMetering";
		GLOBAL_CONTEXT.SIZE = "MAX";
	</script>
	<script data-main="${ctx}/js/mvm_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>

</body>
</html>