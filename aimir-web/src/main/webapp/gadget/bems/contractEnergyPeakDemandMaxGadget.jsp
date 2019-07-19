<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" 
	contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->
    <title><fmt:message key="gadget.bems.thresh001"/></title>
    <link href="${ctx}/css/index.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css" />
 	<link href="${ctx}/css/bems/imports_style.css" rel="stylesheet" type="text/css" />

 	<link href="${ctx}/css/bems/ContractEnergyPeakDemand.css" rel="stylesheet" type="text/css" />
    <%@ include file="/gadget/system/preLoading.jsp"%>
</head>
<body>

<!-- tag Layer start -->
<div id="tag-layer-popup">
	<div class="control-bar x-toolbar">		
		<em><fmt:message key="aimir.tag"/></em>
	</div>
	<div class="total-tags tags">
		<div class="tag-info">전체 태그</div>
		<select multiple>
			<c:forEach var="tag" items="${tags}">
				<option id="tag_${tag.code}" value="${tag.name}" title="${tag.code} : ${tag.name}">											
					<c:choose>
					 	<c:when test="${tag.descr != ''}">
					 		${tag.descr} (${tag.descr})
					 	</c:when>
						<c:otherwise>${tag.code} (${tag.descr})</c:otherwise>
					</c:choose>
				</option>
			</c:forEach>
		</select>
	</div>	
	<div class="apply-tags tags">
		<div class="tag-info">적용 태그</div>
		<select multiple></select>
	</div>
	<div class="in-out-control">
		<ul>
			<li class="in"></li>
			<li class="out"></li>
			<li class="in-btn">
				<em class="hm_button">
					<a><fmt:message key="aimir.button.apply"/></a>
				</em>
			</li>
			<li class="out-btn">
				<em class="hm_button">
					<a><fmt:message key="aimir.cancel"/></a>
				</em>
			</li>
		</ul>		
	</div>
</div>
<!-- tag Layer end -->

<div id="wrapper" class="max">
	<div id="top-wrapper">
		<div id="peek-demand-info">
		    <!-- 계약전력과 Peak Demand 기본정보 (S) -->
		    <div id="container2">
		    <div class="tapBg seachSpace">
		       <!-- 주기 및 날짜 (S) -->
		        <ul class="header">
		            <li class="hLeft tit_default">
		            	<fmt:message key="aimir.contract.demand.amount"/>
		            </li>
		            <li class="hLeft">
						<select id="dateTypeList" style="width:75px;" class="local-selectbox">
							<c:forEach var="combo" items="${combo}">
								<option value="${combo.id}">${combo.name}</option>
							</c:forEach>
						</select>
		             </li>              
		           	 <li class="hRight mt5"><div id='basisDate'></div></li>
				</ul>
				</div>
		       <!-- 주기 및 날짜  (E) -->
				
		    </div> 
		    <!-- 계약전력과 Peak Demand 기본정보 (E) -->
		    
		    <!-- 계약전력과 Peak Demand gauge chart (S) -->
		    <div class="pt10 pb10">
		    	<div id="bldg_peak_gaige">
			        <div class="gaige_value">     
			            <ul>
			                <li>
				                <span class="text"><fmt:message key="aimir.critical"/></span>
				                <span>
				                	<input id="threshold3" name="danger" 
				                		type="text" value="0"  maxlength="2" class="input_danger threshold-critical"/>%
				                </span>    
			                	<span id="lv_info_danger">
			                	   	<img id="lamp_danger_on" 
			                	   		src="${ctx}/themes/images/default/setting/ic-warn-on.png" 
			                	   		style="DISPLAY: none;margin-top:4px;">
			                	    <img id="lamp_danger_off" 
			                	    	src="${ctx}/themes/images/default/setting/ic-warn-off.png" 
			                	    	style="DISPLAY: none;margin-top:4px;">
			                    </span>					 
			                </li>
			                <li>
				                <span class="text"><fmt:message key="aimir.warning"/></span>
				                <span>
				                	<input id="threshold2" name="warn" type="text"
				                		value="0" maxlength="2" class="input_warn threshold-warning"/>
				                %</span>
				                <span id="lv_info_warn">
				                	<img id="lamp_warn_on" 
				                		src="${ctx}/themes/images/default/setting/ic-warn-on.png" 
				                		style="DISPLAY: none;margin-top:4px;">
				                	<img id="lamp_warn_off" 
				                		src="${ctx}/themes/images/default/setting/ic-warn-off.png" 
				                		style="DISPLAY: none;margin-top:4px;">
				                </span>
			                </li>
			                <li style="display:none">
			                	<span class="text"><fmt:message key="aimir.normal"/></span>
			                	<span>
			                		<input id="threshold1" class="threshold1" name="good" type="text" value="0" 
			                		maxlength="2" class="input_good threshold-good" readonly/>%
			                	</span> 
			                </li>
			            </ul>
			        </div>
		         
			        <div id="lv_info_popup" class="alert" style="DISPLAY: none;">
						 <ul>
						    <li class="close"><a href="#">X</a></li>
						 	<li class="tit" id="lv_danger" style="DISPLAY: none;">
						 	 	<fmt:message key="aimir.criticallevel"/>
						 	</li>
						 	<li class="tit" id="lv_warn" style="DISPLAY: none;">
						 	 	<fmt:message key="aimir.warninglevel"/>
						 	</li>
						  	<li class="cont"> 
						  	<input id="lastAmount" class="last" size="6" readonly/>kW
						  	 	<fmt:message key="aimir.using"/>
						  	</li>
						  	<li  class="cont mt10">
						  	 	<span>(<fmt:message key="aimir.contract.demand.amount"/></span>
						  	 	<span><input id="lastPercent" class="last" size="4" readonly/></span>
								<span>% <fmt:message key="aimir.approach"/>)</span>
							</li>
					  	</ul>
			        </div>
		         
			        <div class="gaige_graph ptrbl10">
			            <div id="guageChartDiv">
			                The chart will appear within this DIV. This text will be replaced by the chart.
			            </div>
					</div> 
		        
			        <div class="gaigeValue">
			        	<span><fmt:message key="aimir.contract.demand.amount"/></span>
			        	<span>
			        		<input id="contractEnergy" type="text" value="" maxlength="2" 
			        			class="input_dot" readonly style="width:60px;"/>
			        	</span>
			        	<span>kW</span>	           
			        	<span><fmt:message key="aimir.facilityMgmt.powerConsumption"/></span>
			        	<span>
			           		<input id="useAmount" type="text" value="" maxlength="2" 
			           			class="input_dot" readonly style="width:50px;"/>
			           	</span>
			        	<span>kW</span>
			       </div>    
				</div>
			    <!-- 계약전력과 Peak Demand gauge chart (E) -->    

			    <!-- 계약전력과 Peak Demand multiple axes chart (S) -->
			    <div id="bldg_peak_graph">
			        <div id="columnChartDiv" align="left">
			            The chart will appear within this DIV. This text will be replaced by the chart.
			        </div>
			    </div> 
			   	<!-- 계약전력과 Peak Demand multiple axes chart (E) -->

			</div>
		</div>		

		<!-- peek demand and dr history Layer -->
		<div id="peek-demand-dr-history">

			<div id="peek-demand-dr-history-tabs">
				<ul>
					<li>
						<a href="#peek-demand-history">
							<fmt:message key="aimir.bems.view.peekDemandLog"/>
						</a>
					</li>
					<li>
						<a href="#dr-execute-history">
							<fmt:message key="aimir.bems.view.DRexecuteLog"/>
						</a>
					</li>
				</ul>
				<div id="peek-demand-history" class="tab-content">
					<form action="searchPeekDemandHistory">
						<div class="date-form"></div>
						<div class="dashedline"></div>
						<table class="form-table search">
							<tr>
								<th><fmt:message key="aimir.location"/></th>
								<td>
									<input id='pd-location-input' type="text"/>
					         		<input type='hidden' id='pd-location-hidden' name="location" />
								</td>
								<th><fmt:message key="aimir.status"/></th>
								<td>
									<select id="fm_status" name="fm_status" class="local-selectbox">
										<c:forEach var="status" items="${fmStatus}">
											<option value="${status.name}">${status.descr}</option>
										</c:forEach>
									</select>
								</td>
								<td>
									<em class="hm_button">
										<a class="submit"><fmt:message key="aimir.button.search"/></a>
									</em>
								</td>
							</tr>
						</table>
						<div id="treeDivMeterOuter" class="tree-billing auto" style='display:none;'>
                    	<div id="treeDivMeter"></div>
                    </div>
					</form>
					<div class="g_clear"></div>

					<div id="peekDemandLogs" class="mt10 extgrid"></div>
				</div>
				<div id="dr-execute-history" class="tab-content">
					<form action="searchExcuteDRHistory">
						<div class="date-form"></div>
						<div class="dashedline"></div>
						<table class="form-table search">
							<tr>
								<th><fmt:message key="aimir.result"/></th>
								<td>
									<select id="dr_status_select" name="dr_status_value" 
										class="local-selectbox">
										<option value=""><fmt:message key="aimir.all"/></option>
										<option value="success"><fmt:message key="aimir.success"/></option>
										<option value="failed"><fmt:message key="aimir.failed"/></option>
									</select>
								</td>
								<th><fmt:message key="aimir.bems.view.DRScenario"/></th>
								<td>
									<select id="dr_scenario_select" name="dr_scenario_value" 
										class="dr-scenario-selector">
										<option value=""><fmt:message key="aimir.all"/></option>
										<option value="success"><fmt:message key="aimir.success"/></option>
										<option value="failed"><fmt:message key="aimir.failed"/></option>
									</select>
								</td>
								<td>
									<em class="hm_button"><a class="submit">
										<fmt:message key="aimir.button.search"/></a>
									</em>
								</td>
							</tr>
						</table>
					</form>
					<div class="g_clear"></div>

					<div id="drLogs" class="mt10 extgrid"></div>
				</div>
			</div>

		</div>

		<!-- float clear -->
		<div class="g_clear"></div>

	</div>

	<div class="dashedline"></div>

	<div class="g_clear"></div>

	<div class="ui-loading-indicator image">
		<img src="${ctx}/themes/images/default/progress/img_loading.gif" />
	</div>

	<!-- DR Scenario Layer -->
	<div id="bottom-wrapper">		
		<div class="dr-config-left">
			<div id="dr-config" class="drconfig-view-items">
				<div class="chart-title">
					<fmt:message key="aimir.bems.config.DRConfig"/>
				</div>
				<div class="table-body-noheader">
				<table class="dr-config-table b-x-grid3 table ext-form-type">
					<thead>
						<tr class="x-grid3-header x-grid3-hd-row">
							<td class="dr-config-col-no x-grid3-hd-inner">
								<fmt:message key="aimir.number"/>
							</td>
							<td class="dr-config-col-dr-title x-grid3-hd-inner">
								<fmt:message key="aimir.event"/>
							</td>
							<td class="dr-config-col-event x-grid3-hd-inner">
								<fmt:message key="aimir.eventclassname"/>
							</td>							
							<td class="dr-config-col-scenario x-grid3-hd-inner">
								<fmt:message key="aimir.bems.view.DRScenario"/>
							</td>
							<td class="dr-config-col-apply x-grid3-hd-inner">
								<fmt:message key="aimir.setting"/>
							</td>							
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td rowspan="2" class="highlight">Peak Demand Threshold</td>
							<td>
								<fmt:message key="aimir.critical"/>
								<input class="threshold" name="level" type="hidden" value="critical" />
							</td>
							<td>
								<select id="critical-scenario" name="scenarioId" 
									class="threshold dr-scenario-selector"></select>
							</td>
							<td>
								<em class="hm_button">
									<a class="submit">
										<fmt:message key="aimir.button.apply"/>
									</a>
								</em>
								<em class="hm_button">
									<a class="disable">
										<fmt:message key="aimir.cancel"/>
									</a>
								</em>
							</td>
						</tr>
						<tr>
							<td>2</td>
							<td>
								<fmt:message key="aimir.warning"/>
								<input class="threshold" name="level" type="hidden" value="warning" />
							</td>
							<td>
								<select id="warning-scenario" name="scenarioId" 
									class="threshold dr-scenario-selector"></select>
							</td>
							<td>
								<em class="hm_button">
									<a class="submit">
										<fmt:message key="aimir.button.apply"/>
									</a>
								</em>
								<em class="hm_button">
									<a class="disable">
										<fmt:message key="aimir.cancel"/>
									</a>
								</em>
							</td>
						</tr>
						<tr class="x-hidden">
							<td>3</td>
							<td>
								<fmt:message key="aimir.info"/>
								<input class="threshold" name="level" type="hidden" value="good" />
							</td>
							<td>
								<select id="good-scenario" name="scenarioId" 
									class="threshold dr-scenario-selector">
									<option value="fdsgs">dsdd</option>
								</select>
							</td>
							<td>
								<em class="hm_button">
									<a class="submit">
										<fmt:message key="aimir.button.apply"/>
									</a>
								</em>
								<em class="hm_button">
									<a class="disable">
										<fmt:message key="aimir.cancel"/>
									</a>
								</em>
							</td>
						</tr>
					</tbody>
				</table>
				</div>
			</div>
			<div id="dr-scenario-grid" class="drconfig-view-items">
				<div class="chart-title"><fmt:message key="aimir.bems.view.DRScenario"/></div>
			</div>
		</div>

		<div class="dr-config-right">
			<div id="dr-detail" class="drconfig-view-items">
				<div class="chart-title"><fmt:message key="aimir.bems.view.DRScenarioDetail"/></div>
				<form action="scenarioAction">
				<table id="dr-detail-scenario-form" class="form-table">
					<tr class="view-mode edit-mode id-area fmode" data-bind="id">
						<th style="width: 25%;">
							<fmt:message key="aimir.bems.view.DRScenario"/>
							<fmt:message key="aimir.id"/>
						</th>						
						<td colspan="2">
							<span></span>
						</td>						
					</tr>
					<tr>
						<th style="width: 25%;">
							<fmt:message key="aimir.bems.view.DRScenario"/>
							<fmt:message key="aimir.name"/>
						</th>
						<td class="edit-mode insert-mode fmode">
							<input type="text" name="name" style="width: 200px;"
								placeholder='<fmt:message key="aimir.form.required.DRScenario" />'/>
						</td>
						<td class="view-mode fmode" data-bind="name">
							<span></span>
						</td>						
					</tr>
					<tr>
						<th><fmt:message key="aimir.location"/></th>
						<td class="edit-mode insert-mode fmode">
							<select id="sc-contract_location" name="contractLocationId"
								class="local-selectbox" style="width: 200px;">
								<c:forEach var="combo" items="${combo}">
									<option value="${combo.id}">${combo.name}</option>
								</c:forEach>
							</select>
						</td>
						<td class="view-mode fmode" data-bind="contractCapacity.contractLocations">
							<span></span>
						</td>
					</tr>
					<tr>
						<th>
							<fmt:message key="aimir.description"/>
						</th>
						<td class="edit-mode insert-mode fmode">
							<textarea name="description"
								placeholder='<fmt:message key="aimir.desc.message" />'></textarea>
						</td>	
						<td class="view-mode fmode" data-bind="description">
							<span></span>
						</td>					
					</tr>
					<tr>
						<th>
							Tags				
						</th>
						<td class="edit-mode insert-mode fmode">
							<div id="tag-list-area" class="tagList">
								<div class="click-guide edit-mode insert-mode fmode">
									<fmt:message key="aimir.set"/>
								</div>
								<ul class="tag-list-ulist"></ul>
							</div>
							<div class="g_clear"></div>
						</td>
						<td class="view-mode fmode" data-bind="target">
							<span id="csv-tags"></span>
							<div class="g_clear"></div>
						</td>
					</tr>
					<tr>							
						<td class="buttons-area" colspan="3">
							<em class="hm_button view-mode edit-mode fmode">
								<a class="modify">
									<fmt:message key="aimir.button.modifyDRScenario"/>
								</a>
							</em>														
							<em class="hm_button view-mode fmode">
								<a class="delete">
									<fmt:message key="aimir.button.delete"/>
								</a>
							</em>
							<em class="hm_button insert-mode edit-mode view-mode fmode">
								<a class="regist">
									<fmt:message key="aimir.button.addNewDRScenario"/>
								</a>
							</em>
						</td>
					</tr>
				</table>
				</form>
			</div>
		</div>
		<div class="g_clear"></div>
	</div>
</div>

<!-- include javascripts -->
<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
<script type="text/javascript">
	GLOBAL_CONTEXT.GADGET = "ContractEnergyPeakDemand";
	GLOBAL_CONTEXT.SIZE = "MAX";
</script>	
<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>
