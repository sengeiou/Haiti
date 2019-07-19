<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
   
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><fmt:message key="gadget.system008"/></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/SerarchCondtion.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/OperationLogGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/CommLogGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/gadget/LogAnalysis/EventAlertLogGrid.js"></script>    
    
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* Ext-Js Grid Header style 정의. */
        .x-grid3-hd-inner {
            text-align: center;
            font-weight: bold;
        }
        /* context menu 의 style 이 어긋나는 부분 수정 */
        .x-menu-list-item span, .x-menu-list-item a {
            float:none !important;
        }
        /* context menu 의 icon 공간을 삭제 */
        div.no-icon-menu a.x-menu-item {
            padding-left: 0 !important;
        }
        .x-menu-item-text {
            padding-left: 10px !important;
        }
    </style>

    <script type="text/javascript" charset="utf-8">
		var ctxPath = "${ctx}";
        var _supplierId = "${supplierId}";
		var err_title = '<fmt:message key="aimir.error"/>';
		var err_msg = '<fmt:message key="aimir.season.error"/>';
		
		
		// Operation Log
        var fmtOperMessage = new Array();
        fmtOperMessage[0] = "<fmt:message key='aimir.orderNo'/>";    // 순번
        fmtOperMessage[1] = "<fmt:message key='aimir.opentime'/>";   // 발생 시각
        fmtOperMessage[2] = "<fmt:message key='aimir.equipment'/>";      // 대상 => 장비
        fmtOperMessage[3] = "<fmt:message key='aimir.operator'/>";      // 수행자
        fmtOperMessage[4] = "<fmt:message key='aimir.instrumentation'/>";  // 명령
        fmtOperMessage[5] = "<fmt:message key='aimir.result'/>";     // 결과

		
		var fmtCommMessage = new Array();
        fmtCommMessage[0] = "<fmt:message key='aimir.orderNo'/>";   // 번호 => 순번
        fmtCommMessage[1] = "<fmt:message key='aimir.opentime'/>";  // 로그 시각 => 발생 시각
		fmtCommMessage[2] = "<fmt:message key='aimir.send'/>";       // Sender => 송신
		fmtCommMessage[3] = "<fmt:message key='aimir.equipment'/>";      // 근원지 => 장비
		fmtCommMessage[4] = "<fmt:message key='aimir.instrumentation'/>";  // 오퍼레이션 코드  => 명령
		fmtCommMessage[5] = "<fmt:message key='aimir.result'/>";      // 결과		
        
		
		var fmtEventAlertMessage = new Array();
	    fmtEventAlertMessage[0] = "<fmt:message key='aimir.orderNo'/>";   // 번호 => 순번
		fmtEventAlertMessage[1] = "<fmt:message key='aimir.opentime'/>";      // 발생시각 => 
		fmtEventAlertMessage[2] = "<fmt:message key='aimir.equipment'/>";      // 근원지 => 장비
		fmtEventAlertMessage[3] = "<fmt:message key='aimir.message'/>";       // Message
	
		
        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
            var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

            if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
                Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
                Ext.override(Ext.grid.ColumnModel, {
                    getTotalWidth : function(includeHidden) {
                        if (!this.totalWidth) {
                            var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                            this.totalWidth = 0;
                            for (var i = 0, len = this.config.length; i < len; i++) {
                                if (includeHidden || !this.isHidden(i)) {
                                    this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                                }
                            }
                        }
                        return this.totalWidth;
                    }
                });
                chromeColAdd = 2;
            }
        }		



        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }		
		
		
		var allGridPagingSize = 15;
		
        Ext.onReady(function() {
			commonDateTabInit();  // 검색 조건 초기화작업		
			
			// Operation Log Gird 초기화
			initOperationLogGrid(fmtOperMessage, allGridPagingSize);
			
			// Comm Log Grid 초기화
			initCommLogGrid(fmtCommMessage, allGridPagingSize);
			
			// Event Alert Log Grid 초기화
			initEventAlertLogGrid(fmtEventAlertMessage, allGridPagingSize);
			
			Ext.QuickTips.init();	
			extColumnResize();
			
					
        });	
		
        //윈도우 리싸이즈시 event
        $(window).resize(function() {
			sendRequest();
		}); 
		
		
		//검색 버튼 클릭
		function send(){
			var searchConditionArray = getSearchConditionArray();  // 검색 조건			
			
			operationLogGridLoad(searchConditionArray);
			commLogGridLoad(searchConditionArray);			
			eventAlertLogGridLoad(searchConditionArray);
		}
		

    </script>
    

</head>

<body>
    <div class="search-bg-basic">
    	<div class="searchoption-container">
			<table class="searchoption wfree">
            	<tr>                    
                    <td class="padding-r20px">
                        <div id="hourly">
                            <ul>
                                <li><input id="hourlyStartDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><select id="hourlyStartHourCombo" class="sm"></select></li>
                                <li><select id="hourlyStartMinuteCombo" class="sm"></select></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="hourlyEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><select id="hourlyEndHourCombo" class="sm"></select></li>
                                <li><select id="hourlyEndMinuteCombo" class="sm"></select></li>
                            </ul>
                        </div>      	
                    </td>
                    
                    <!-- 오퍼레이션 코드 -->         
                    <td class="withinput padding-r20px"><fmt:message key="aimir.operationcode" /></td>
                    <td class="padding-r20px">
                        <select id="operationCombo" name="operationCombo" style="width:200px;">
                            <option value="" codeValue=""><fmt:message key="aimir.all" /></option>
                                <c:forEach var="operation" items="${operations}">
                                    <option value="${operation.id}" codeValue="${operation.code}">${operation.descr}</option>
                                </c:forEach>
                        </select>   
                    </td>
                    
                    <!-- 장비 아이디 -->
                    <td class="withinput padding-r20px"><fmt:message key="aimir.deviceId" /></td>
                    <td class="padding-r10px"><input id="deviceId" name="deviceId" type="text" /></td>
					<td><em class="am_button"><a href="javascript:sendRequest();" class="on"><fmt:message key='aimir.button.search'/></a></em></td>
                                        
                    <!-- 검색 시간 범위 -->   
                    <td class="padding-r10px">
                    <td class="padding-r10px">
                    <td class="withinput padding-r20px"><fmt:message key="aimir.loganalysis.timegap" /></td>
                    <td class="padding-r20px">
                        <select id="timeGapSelect" name="timeGap" style="width:50px;">
                            <c:forEach var="timeGap" items="${timeGap}">
                                <option value="${timeGap}">${timeGap}</option>
                            </c:forEach>
                        </select>    
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

    <div class="gadget_body">
    	<!-- 오퍼레이션 로그 -->
        <div class="floatleft" style="width: 35%;">
            <div class="operator_tit_space"><label class="check"><fmt:message key="aimir.operationlog" /></label></div>
            <div id="operLogGridDiv" class="floatleft grid-button-height" style="width: 100%;"></div>
        </div>
     
     	<!-- 통신 로그 -->
        <div class="floatleft" style="width: 35%;">
            <div class="operator_tit_space"><label class="check"><fmt:message key="aimir.commlog" /></label></div>
            <div id="commLogGridDiv" class="floatright grid-button-height" style="width: 100%;"></div>
        </div>
        
        <!-- 이벤트 알람 로그-->
        <div class="floatleft" style="width: 30%;">
            <div class="operator_tit_space"><label class="check"><fmt:message key="gadget.device005" /></label></div>
            <div id="eventAlertLogGridDiv" class="floatright grid-button-height" style="width: 100%;"></div>
        </div>   
    </div>
 
</body>

</html>