<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Log Analysis MinGadget</title>
    
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>    
    
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/SerarchCondtionMin.js"></script>    
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/TotalLogTreeGrid.js"></script>
    <style type="text/css">
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }

    </style>
    <script type="text/javascript" charset="utf-8">
		$("#timeGapSelect").selectbox();
		
		var ctxPath = "${ctx}";
        var supplierId = "${supplierId}";
		var err_title = '<fmt:message key="aimir.error"/>';
		var err_msg = '<fmt:message key="aimir.season.error"/>';		
		
        var fmtTotalTitle = new Array();
        fmtTotalTitle[0] = "<fmt:message key='aimir.orderNo'/>";    // 순번
        fmtTotalTitle[2] = "dateByGroup";    // 날짜(그룹핑용)
        fmtTotalTitle[3] = "<fmt:message key='aimir.opentime'/>";    // 날짜(표기용)						
        fmtTotalTitle[4] = "<fmt:message key='aimir.type2'/>";   // 로그타입
        fmtTotalTitle[5] = "<fmt:message key='aimir.send'/>";  // 송신(comm log : hidden)
        fmtTotalTitle[6] = "<fmt:message key='aimir.equipment'/>";   // 장비
        fmtTotalTitle[7] = "<fmt:message key='aimir.operator'/>";  // 수행자(oper log : hidden)
        fmtTotalTitle[8] = "<fmt:message key='aimir.instrumentation'/>";     // 명령
        fmtTotalTitle[9] = "<fmt:message key='aimir.result'/>";     // 결과
        fmtTotalTitle[10] = "<fmt:message key='aimir.message'/>";     // 메시지 (event log : hidden)		


		//검색 조건.
		function getSearchConditionArray(){
			var arrayObj = Array();
			arrayObj[0] = $('#searchStartDate').val() + $('#searchStartHour').val() + "00"; //startDate
			arrayObj[1] = $('#searchEndDate').val() + $('#searchEndHour').val() + "59";     //endDate			
			arrayObj[2] = 'C';   // (C)Command.	
			arrayObj[3] = '33'   // EnergyLevelChanged = 33
			arrayObj[4] = $('#timeGapSelect').val(); //time gap
			
			//console.log(arrayObj);
			
			return arrayObj;
		};
		
		//검색 버튼 클릭
		function send(){
			totalLogTreeGridLoad(getSearchConditionArray());  
		}
		
		/**
		*    OnReady
		*/
        Ext.onReady(function() {
			commonDateTabInit();  // 검색 조건 초기화작업	
			
			initTotalLogTreeGrid(fmtTotalTitle, getSearchConditionArray());  // 초기화

			Ext.QuickTips.init();	
			
            sendRequest();			   // 최초 한번 검색
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
			sendRequest();
		}); 
			
		/**
		 * 조회버튼 클릭시 조회조건 검증후 거래호출
		 */
		function sendRequest(){ 
			getSearchDate(DateType.HOURLY);
			
			// 조회조건 검증
			if(!validateSearchCondition(DateType.HOURLY))return false;
			send();
		}
    </script>
</head>

<body>
    <div class="search-bg-basic">
    	<div class="searchoption-container">
			<table class="searchoption wfree">
            	<tr>                    
                    <td>
                        <div id="hourly">
                            <ul>
                                <li><input id="hourlyStartDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="hourlyEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li class="date-space"></li>
                                
                                <!-- 검색 시간 범위 -->   
                                <li style="position:relative; right:0px;top:4px;">&nbsp;<fmt:message key="aimir.loganalysis.timegap" /></li>
                                <li>
                                    <select id="timeGapSelect" name="timeGap" style="width:40px;">
                                        <c:forEach var="timeGap" items="${timeGap}">
                                            <option value="${timeGap}">${timeGap}</option>
                                        </c:forEach>
                                    </select>                            
                                </li>
                                <li style="position:relative; top:4px;"><fmt:message key="aimir.minute"/></li>
                                <!-- 검색 버튼 -->
                                <li>
	                                <em class="am_button" style="margin-left: 15px;"><a href="javascript:sendRequest();" class="on"><fmt:message key='aimir.button.search'/></a></em>
                                </li>
                                
                            </ul>
                        </div>      	
                    </td>
                                      
                    <td><input id="searchStartDate" type="hidden"/></td>
                    <td><input id="searchEndDate" type="hidden" /></td>
                    <td><input id="searchStartHour" type="hidden"/></td>
                    <td><input id="searchEndHour" type="hidden" /></td>
                    <td><input id="searchDateType" type="hidden" value="0"/></td>                
				</tr>
            </table>
    	</div>        
    </div> 
    <div id="gadget_body" style="padding: 10px 10px !important;">
        <div id="TotalLogGridDiv" style="margin-bottom: 5px;" />
    </div>

</body>

</html>