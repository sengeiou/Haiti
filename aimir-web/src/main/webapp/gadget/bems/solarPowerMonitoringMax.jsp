<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" 
	contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="${fn:toLowerCase(lang)}">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->	
    <title><fmt:message key="gadget.bems.solarPowerMonitoring"/></title>
    <link href="${ctx}/css/index.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css" />
 	<link href="${ctx}/css/bems/imports_style.css" rel="stylesheet" type="text/css" />

 	<link href="${ctx}/css/bems/solarPowerMonitoring.css" rel="stylesheet" type="text/css" />
    <%@ include file="/gadget/system/preLoading.jsp"%>
</head>
<body>
<div id="wrapper" class="max">
	<div class="tab-menu">
	    <ul>
	        <li>
	        	<a href="#generarion-monitoring">
	        		<fmt:message key='aimir.bems.powerGenerationAmount'/>
	        		<fmt:message key='aimir.monitoring'/>
	        	</a>
	        </li>
	        <li>
	        	<a href="#generarion-view">
	        		<fmt:message key='aimir.bems.view.electricGeneration'/>
	        	</a>
	        </li>
	        <li>
	        	<a href="#regist-inverter">
	        		<fmt:message key='aimir.bems.registInverter'/>
	        	</a>
	        </li>
	    </ul>

	    <div id="generarion-monitoring">
			<form action="searchPowerGenerationByDay">	
				<table class="form-table">
					<tr>
						<td>
							<span class="hLeft tit_default">
								<fmt:message key="aimir.bems.label.searchDate"/>
							</span>
						</td>
						<td><div id="date-search-wrapper"></div></td>
						<td>
							<em class="hm_button">
								<a class="submit">
									<fmt:message key="aimir.button.search"/>
								</a>
							</em>
						</td>
					</tr>
				</table>		
			</form>

			<div class="g_clear"></div>

			<div id="power-generation-inverters-chart" class="js-hidden"></div>
			<div id="power-generation-date-group-charts" class="js-hidden">
				<h1>Inverter Statistics<span></span></h1>
				<ul id="inverterStatChartArea" class="floatlist">
					<li>
						<div class="chart-title">
							<span>
								<fmt:message key='aimir.day'/>
								<fmt:message key='aimir.bems.powerGenerationAmount'/>
							</span>
						</div>
						<div id="dayList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span>
								<fmt:message key='aimir.date.thisweek'/>
								<fmt:message key='aimir.bems.powerGenerationAmount'/>
							</span>
						</div>
						<div id="weekList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span>
								<fmt:message key='aimir.thismonth'/>
								<fmt:message key='aimir.bems.powerGenerationAmount'/>
							</span>
						</div>
						<div id="monthList_chart"></div>
					</li>
					<li>
						<div class="chart-title">
							<span>
								<fmt:message key='aimir.quarterly'/>
								<fmt:message key='aimir.bems.powerGenerationAmount'/>
							</span>
						</div>
						<div id="seasonList_chart"></div>
					</li>
				</ul>				
				<div class="g_clear"></div>
			</div>

			<div class="g_clear"></div>

		</div>

		<div id="generarion-view">
			<form action="searchGeneration">	
				<div class="date-form"></div>
				<div class="dashedline"></div>
				<table class="form-table">
					<tr>
						<th>
							<fmt:message key="aimir.bems.inverter"/>
							<fmt:message key="aimir.id"/>
						</th>
						<td>
							<input type="text" name="inverterId" />
						</td>
						<th>
							<fmt:message key="aimir.bems.inverter"/>
							<fmt:message key="aimir.name"/></th>
						<td>
							<input type="text" name="inverterName" />
						</td>
						<td>							
							<em class="hm_button">
								<a class="submit">
									<fmt:message key="aimir.button.search"/>
								</a>
							</em>
						</td>
					</tr>
				</table>		
			</form>

			<div id="generation-statistics-grid"></div>

		</div>

		<div id="regist-inverter" class="tab-item">
			<form id="addNewInverterForm" action="addNewInverter">	
			<input type="hidden" name="modem.deviceSerial" value="" />			
			<table class="form-table search">
				<tr>
					<th class="form-label">
						<fmt:message key="aimir.bems.inverter"/>
						<fmt:message key="aimir.name"/>
					</th>
					<td>
						<input type="text" name="friendlyName">			
					</td>
					<th class="form-label" data-require="true">
						<fmt:message key="aimir.bems.inverter"/>
						<fmt:message key="aimir.id"/></th>
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
					<td>
						<input type="text" id="singleRegMeterLocDetail" name="address">
						<input type="hidden" id="supplierId" name="supplier.id" value=""/>
					</td>
					
					<td colspan="3"></td>
				</tr> 
			</table>
			
			<div id="treeDivMeterOuter" class="tree-billing auto" style='display:none;'>
            	<div id="treeDivMeter"></div>
            </div>
			<div class="btn-confirm">
				<em class="hm_button"><a class="submit"><fmt:message key="aimir.button.confirm"/></a></em>
				<em class="hm_button"><a class="reset"><fmt:message key="aimir.cancel"/></a></em>
			</div>	
			
			</form>
		</div>	
		<div id="solarDetail"></div>		
	</div>
</div>

<!-- include javascripts -->
<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
<script type="text/javascript">
	GLOBAL_CONTEXT.GADGET = "SolarPowerMonitoring";
	GLOBAL_CONTEXT.SIZE = "MAX";
	GLOBAL_CONTEXT.LANG = "${fn:toLowerCase(lang)}";
	GLOBAL_CONTEXT.G_DATA = {
		solarPowerMeter: ${meterCode}
	};
</script>	
<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>