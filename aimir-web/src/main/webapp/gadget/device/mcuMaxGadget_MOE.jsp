<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", -1); //prevents caching at the proxy
%>
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Insert title here</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
     <link href="${ctx}/js/extjs/ux/css/GroupHeaderPlugin.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /* ext-js group grid header 정렬 */
        .x-grid3-header-offset table {
          border-collapse: separate;
          border-spacing: 0px;      
        }  
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }

        html {
            overflow-y: auto;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.jsp"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>   
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>    
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    
    <%-- Ext-JS GroupHeader plugin 추가 --%>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ux/GroupHeaderPlugin.js"></script>
    
    <script type="text/javascript" charset="utf-8">
        //탭초기화
        // 값 0 - 숨김처리
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs     = {hourly:1,daily:0,period:1,weekly:1,monthly:0,monthlyPeriod:1,weekDaily:0,seasonal:0,yearly:0};

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
		
        var red = '#F31523'; // unknown
        var orange = '#FC8F00'; // NA48h
        var gold = '#A99903'; // NA24h
        var blue = '#0718D7'; // A24h
        var setColorFrontTag = "<b style=\"color:";
        var setColorMiddleTag = ";\">"; 
        var setColorBackTag = "</b>";
        
        var supplierId = "${supplierId}";
        var loginId = "${loginId}";
        // 수정권한
        var editAuth = "${editAuth}";
        // Command 권한
        var cmdAuth = "${cmdAuth}";

        var send = function() {
            searchList();
        };

        var mcuRegSysIdCheck;

        var preSysId = '';
        
        $(function() {
            $('.supplierArea').hide();
            $('.space5').hide();
            locationTreeGoGo('treeDivA', 'searchWord', 'sLocationId');
            searchList();
        });

        // 조회
        var searchList = function() {
            getDcuList();
        };

        var getConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#sMcuId').val();            // 집중기번호
            arrayObj[1] = $('#sMcuType').val();          // 집중기유형
            arrayObj[2] = $('#sLocationId').val();       // 지역명
            arrayObj[3] = $('#sSwVersion').val();        // sw version
            arrayObj[4] = $('#sHwVersion').val();        // hw version
            arrayObj[5] = $('#sInstallDateStartHidden').val(); // 설치일
            arrayObj[6] = $('#sInstallDateEndHidden').val();   // 설치일

            arrayObj[8] = $('#filter').val();
            arrayObj[9] = $('#order').val();
            arrayObj[10] = $('#protocol').val();

            //캐쉬로 인한 쓰레기 더미 파라미터
            arrayObj[11] = Math.random();

            //날짜포맷 위한 supplierId
            arrayObj[12] = supplierId;
            
            arrayObj[13] = $('#sMcuStatus').val();        // mcuStatus;

            return arrayObj;
        };

        $(document).ready(function() {
            Ext.QuickTips.init();

            // 수정권한 체크
            if (editAuth == "true") {
                $("#mcuAddBtn").show();
                $("#mcuDelBtn").show();
                $("#mcuLocBtnList").show();
                $("#mcuScheduleBtnList").show();
            } else {
                $("#mcuAddBtn").hide();
                $("#mcuDelBtn").hide();
                $("#mcuLocBtnList").hide();
                $("#mcuScheduleBtnList").hide();
            }

            $("#generalDivTab").show();
            $("#positionDivTab").hide();
            $("#scheduleDivTab").hide();
            $("#logDivTab").hide();
            $("#mcuCodiDivTab").hide();
            $("#signalToNoiseTab").hide();

            $('#generalDivTabId').click(function() { displayDivTab('generalDivTab'); });
            $('#positionDivTabId').click(function() { displayDivTab('positionDivTab'); });
            //$('#scheduleDivTabId').click(function() { displayDivTab('scheduleDivTab'); });
            $('#logDivTabId').click(function() { displayDivTab('logDivTab'); });
            //$('#mcuCodiDivTabId').click(function() { displayDivTab('mcuCodiDivTab'); });
            $('#signalToNoiseTabId').click(function() { displayDivTab('signalToNoiseTab'); });
            $('#singleRegMCUmcuLocalPort').bind("keydown", function(event) {inputOnlyNumberType(event, $(this));});

            $('#tabs').subtabs();

            $("#sMcuType").selectbox();
            $("#protocol").selectbox();
            $("#sSwVersion").selectbox();
            $("#sHwVersion").selectbox();
            $("#filter").selectbox();
            $("#order").selectbox();

            $("#deviceModelI").selectbox();
            $("#locationI").selectbox();
            $("#mcuTypeI").selectbox();
            $("#protocolTypeI").selectbox();
            $("#sysHwVersionI").selectbox();
            $("#sysSwVersionI").selectbox();
            
            $("#sMcuStatus").selectbox();

            mcuRegSysIdCheck = false;
        });

        var mcuId = '';                // 선택된 MCUID
        var mcuName = '';              // 선택된 MCUNAME
        var mcuType = '';              // 선택된 MCUTYPE
        var imgPage = '';              // 선택된 MCUTYPE
        var modemId = '';			   // 선택된 모뎀의 Device Serial
        var divTabArray = [ 'generalDivTab', 'positionDivTab', 'logDivTab', 'signalToNoiseTab' ];
        var divTabArrayLength = divTabArray.length;
        var currentDivTab = '';

        // MCUINFO 탭 숨김/보임
        var displayDivTab = function(_currentDivTab) {
            if(mcuId == '')
                return ;

            if(currentDivTab == _currentDivTab)
                return ;

            if(_currentDivTab == 'scheduleDivTab' && (mcuType != 'Indoor' && mcuType != 'Outdoor' && mcuType != 'DCU' && mcuType != 'DUALGW')) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"MCU Type is wrong!! (Not Indoor or Outdoor)");
                return ;
            }

            if(_currentDivTab == 'mcuCodiDivTab' && (mcuType != 'Indoor' && mcuType != 'Outdoor' && mcuType != 'DUALGW')) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"MCU Type is wrong!! (Not Indoor or Outdoor)");
                return ;
            }

            currentDivTab = _currentDivTab;

            for ( var i = 0; i < divTabArrayLength; i++) {

                if (currentDivTab == divTabArray[i]) {
                    $("#" + divTabArray[i]).show();
                    $("#" + divTabArray[i] + "Id").addClass("current");

                    drawDiv(divTabArray[i]);
                } else {
                    $("#" + divTabArray[i]).hide();
                    $("#" + divTabArray[i] + "Id").removeClass("current");
                }
            }

        };

        // 기본 정보 DIV 그리기
        var drawGeneralDivTab = function(_mcuId) {
            if (mcuId == _mcuId) {
                displayMcuInfoMcuManageDiv('mcuInfoDiv');
                return;
            }
            if (_mcuId != null && _mcuId != '') {
                mcuId = _mcuId;
            }

            imgPage = document.getElementById("imgPage").value;
            if (imgPage == '')
                imgPage = 1;

            var params = {
                "supplierId" : supplierId,
                "mcuId" : mcuId,
                "imgPage" : imgPage
            };

            displayMcuInfoMcuManageDiv('mcuInfoDiv');

            if (currentDivTab!="") {
                $("#"+currentDivTab).hide();
            } else {
                $("#generalDivTabId").click();
            }

            $("#generalDivTab").load("${ctx}/gadget/device/mcuInfo_MOE.do", params, displayDiv);           
        };

        var displayDiv = function() {
            var bak_currentDivTab = currentDivTab;
            currentDivTab='';
            $("#"+bak_currentDivTab+"Id").click();
            $("#"+bak_currentDivTab).show();
            $('#mcuCommand').hide();
        };

        // 달력 붙이기
        $(function() {

            var locDateFormat = "yymmdd";

            $("#sInstallDateStart")         .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#sInstallDateEnd")           .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#communicationLogStartDate") .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#communicationLogEndDate")   .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            //$("#updateLogStartDate")        .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            //$("#updateLogEndDate")          .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#brokenLogStartDate")        .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#brokenLogEndDate")          .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#commandLogStartDate")       .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#commandLogEndDate")         .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

            //Signal Tab
            $('#connectedDeviceStartDate') 	.datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );	
            $('#connectedDeviceEndDate') 	.datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $('#noiseDetailsStartDate') 	.datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );	
            $('#noiseDetailsEndDate') 		.datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();


            if(("" + month).length == 1) month = "0" + month;
            if(("" + day).length == 1) day = "0" + day;

            var setDate      = year + "" + month + "" + day;
            var dateFullName = "";

            // 날짜를 지역 날짜 포맷으로 변경
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        dateFullName = json.localDate;
                        $("#communicationLogStartDate").val(dateFullName);
                        $("#communicationLogEndDate").val(dateFullName);
                        //$("#updateLogStartDate").val(dateFullName);
                        //$("#updateLogEndDate").val(dateFullName);
                        $("#brokenLogStartDate").val(dateFullName);
                        $("#brokenLogEndDate").val(dateFullName);
                        $("#commandLogStartDate").val(dateFullName);
                        $("#commandLogEndDate").val(dateFullName);
                        $('#connectedDeviceStartDate').val(dateFullName);
                        $('#connectedDeviceEndDate').val(dateFullName);
                        $('#noiseDetailsStartDate').val(dateFullName);
                        $('#noiseDetailsEndDate').val(dateFullName);
                    });

            $("#communicationLogStartDateHidden").val(setDate);
            $("#communicationLogEndDateHidden").val(setDate);
            //$("#updateLogStartDate").val(setDate);
            //$("#updateLogEndDate").val(setDate);
            $("#brokenLogStartDateHidden").val(setDate);
            $("#brokenLogEndDateHidden").val(setDate);
            $("#commandLogStartDateHidden").val(setDate);
            $("#commandLogEndDateHidden").val(setDate);
            // SNR LOG HiddenDate
            $('#connectedDeviceStartDateHidden').val(setDate);
            $('#connectedDeviceEndDateHidden').val(setDate);
            $('#noiseDetailsStartDateHidden').val(setDate);
            $('#noiseDetailsEndDateHidden').val(setDate);

        });

        // datagrid headerText
        var getFmtMessage = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";
            fmtMessage[2] = "<fmt:message key="aimir.mcu.name"/>";
            fmtMessage[3] = "<fmt:message key="aimir.mcumobile"/>";
            fmtMessage[4] = "<fmt:message key="aimir.ipaddress"/>";
            fmtMessage[5] = "<fmt:message key="aimir.sw.version"/>";
            fmtMessage[6] = "<fmt:message key="aimir.csq"/>";
            fmtMessage[7] = "<fmt:message key="aimir.installationdate"/>";
            fmtMessage[8] = "<fmt:message key="aimir.lastcomm"/>";
            fmtMessage[9] = "<fmt:message key="aimir.commstatus"/>";
            fmtMessage[10] = "<fmt:message key="aimir.mcutype"/>";
            fmtMessage[11] = "<fmt:message key="aimir.vendor"/>";
            fmtMessage[12] = "<fmt:message key="aimir.model"/>";
            fmtMessage[13] = "<fmt:message key="aimir.fw.hwversion"/>";
            fmtMessage[14] = "<fmt:message key="aimir.view.mcu39"/>";
            fmtMessage[15] = "<fmt:message key="aimir.location"/>";
            fmtMessage[16] = "<fmt:message key="aimir.normal"/>";
            fmtMessage[17] = "<fmt:message key="aimir.commstateYellow"/>";
            fmtMessage[18] = "<fmt:message key="aimir.commstateRed"/>";
            fmtMessage[19] = "<fmt:message key="aimir.24over"/>";
            fmtMessage[20] = "<fmt:message key="aimir.48over"/>";

            return fmtMessage;
        };

        // 연결 장비
        var getFmtMessage1 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.equipid"/>";
            fmtMessage[2] = "<fmt:message key="aimir.equiptype"/>";

            return fmtMessage;
        };

        // 통신 이력
        var getFmtMessage2 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.service.type"/>";
            fmtMessage[2] = "<fmt:message key="aimir.outbytes"/>";
            fmtMessage[3] = "<fmt:message key="aimir.inbytes"/>";
            fmtMessage[4] = "<fmt:message key="aimir.total.bytes"/>";
            fmtMessage[5] = "<fmt:message key="aimir.total.commtime.sec"/>";
            fmtMessage[6] = "<fmt:message key="aimir.excel.mcuCommLog"/>";

            return fmtMessage;
        };

        // 변경 이력
        var getFmtMessage3 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.time"/>";
            fmtMessage[2] = "<fmt:message key="aimir.operator"/>";
            fmtMessage[3] = "<fmt:message key="aimir.attribute"/>";
            fmtMessage[4] = "<fmt:message key="aimir.current2"/>";
            fmtMessage[5] = "<fmt:message key="aimir.beforevalue"/>";

            return fmtMessage;
        };

        //
        var getFmtMessage4 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.majorValue"/>";
            fmtMessage[2] = "<fmt:message key="aimir.message"/>";
            fmtMessage[3] = "<fmt:message key="aimir.location"/>";
            fmtMessage[4] = "<fmt:message key="aimir.gmptime"/>";
            fmtMessage[5] = "<fmt:message key="aimir.closetime"/>";
            fmtMessage[6] = "<fmt:message key="aimir.duration"/>";
            fmtMessage[7] = "<fmt:message key="aimir.excel.mcuBrokenLog"/>";

            return fmtMessage;
        };

        // 명령 이력
        var getFmtMessage5 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.time"/>";
            fmtMessage[2] = "<fmt:message key="aimir.targetType"/>";
            fmtMessage[3] = "<fmt:message key="aimir.operator"/>";
            fmtMessage[4] = "<fmt:message key="aimir.board.running"/>";
            fmtMessage[5] = "<fmt:message key="aimir.equipment"/>";
            fmtMessage[6] = "<fmt:message key="aimir.description"/>";
            fmtMessage[7] = "<fmt:message key="aimir.excel.mcuCommandLog"/>";


            return fmtMessage;
        };
        // 수치정보, 스케줄, 로그 div 그리기
        var drawDiv = function(_divName) {

            if(mcuId == '')
                return ;

            if(_divName == 'positionDivTab') {

                drawPositionDivTab();

                googleMapInit();
                drawMap();
                getConnectedDeviceList();
            } else if(_divName == 'scheduleDivTab') {

                drawScheduleTab();

            } else if(_divName == 'logDivTab') {
                communicationLogSearch();
                brokenLogSearch();
                //commandLogSearch();
            } else if(_divName == 'mcuCodiDivTab') {

                drawCodiTab();
            } else if(_divName== 'signalToNoiseTab'){
            	drawSignalTab();
            }

        };
        
        var getSignalTabCondition = function(_part){
        	var arrayObj = Array();
        	//임시로 체크 고정
        	//$('#LatestDateMask1').attr("checked",true);
        	//$('#LatestDateMask2').attr("checked",true);
        	//cache
        	arrayObj[0] = Math.random();
        	if(_part == 'connected'){
        		if($('#LatestDateMask1').attr('checked')) {
        			// last는 날짜조건 무시한 마지막 데이터, period는 날짜조건에 해당하는 데이터.
        			arrayObj[1] = 'last';
        		} else arrayObj[1] = 'period';
        		if($('#PoorSignalMask').attr('checked')){
        			// poor 는 -2이하의 데이터만 조사, all은 모든 값을 조사.
        			arrayObj[2] = 'poor';
        		} else arrayObj[2] = 'all';

        		var selectedStDate = $('#connectedDeviceStartDateHidden').val();
        		///arrayObj[3] = new Date(selectedStDate).format('Ymd');
                arrayObj[3] = selectedStDate;
        		arrayObj[4] = $('#connectedDeviceStarthour option:selected').text()+'0000';
        		var selectedEDate = $('#connectedDeviceEndDateHidden').val();
        		//arrayObj[5] = new Date(selectedEDate).format('Ymd');
                arrayObj[5] = selectedEDate;
        		arrayObj[6] = $('#connectedDeviceEndhour option:selected').text()+'0000';
        		
        		return arrayObj;
        	}else if(_part == 'details'){
        		if($('#LatestDateMask2').attr('checked')) {
        			arrayObj[1] = 'last';
        		} else arrayObj[1] = 'period';
        		//cache
        		arrayObj[2] = Math.random();
        		//var selectedStDate = $('#noiseDetailsStartDate').datepicker('getDate');
        		var selectedStDate = $('#noiseDetailsStartDateHidden').val();
        		//arrayObj[3] = new Date(selectedStDate).format('Ymd');
                arrayObj[3] = selectedStDate;
        		arrayObj[4] = $('#noiseDetailsStarthour option:selected').text()+'0000';
        		var selectedEDate = $('#noiseDetailsEndDateHidden').val();
        		//arrayObj[5] = new Date(selectedEDate).format('Ymd');
                arrayObj[5] = selectedEDate;
        		arrayObj[6] = $('#noiseDetailsEndhour option:selected').text()+'0000';
        		
        		return arrayObj;
        	}else 
        		return null;
        }
        
        var drawSignalTab = function() {
        	// 시간 주기 설정 : 0시~23시
        	var hourCycle = Array();
        	for(var i=0; i<24; i++){
        		var obj = new Object();
        		obj.name = ('0' + i).slice(-2);
        		obj.id = i;
        		hourCycle[i]=obj;
        	}
        	$('#connectedDeviceStarthour').pureSelect(hourCycle);
        	$('#connectedDeviceStarthour').selectbox();
        	$('#connectedDeviceEndhour').pureSelect(hourCycle);
        	$('#connectedDeviceEndhour option:eq(23)').attr("selected", "selected");
        	$('#connectedDeviceEndhour').selectbox();
        	$('#noiseDetailsStarthour').pureSelect(hourCycle);
        	$('#noiseDetailsStarthour').selectbox();        	
        	$('#noiseDetailsEndhour').pureSelect(hourCycle);
        	$('#noiseDetailsEndhour option:eq(23)').attr("selected", "selected");
        	$('#noiseDetailsEndhour').selectbox();
        	
        	$('#LatestDateMask1Text').text('Display the last data of every modem at [' + mcuName + ']');
        	
        	
        	drawSnrConnectedDeviceGrid();
        	//noisechartdiv 숨김
        	$('#noiseDetailsDiv').hide();
        	
        }
        
                
        var drawSnrConnectedDeviceGrid = function() {
        	//
        	var pageSize = 7;
        	var conditionArray = getSignalTabCondition('connected');
        	var connectedDeviceStore;
        	var connectedDeviceCol;
        	var connectedDeviceGrid;
        	
        	/* connectedDeviceStore = new Ext.ux.data.PagingJsonStore({
        		lastOptions:{params:{start: 0, limit: pageSize}},
                data: _snrDeviceData||{},                
                //root:'result',
                fields: ["date", "device_id", "mcuId", "slast", "savg", "smin", "smax", ],
                listeners: {                    
                    onLoad: function(store, record, options){
                    	debugger;
                        if (record.length <= 0) {
                            //$("#connectedDeviceGrid").hide();
                        }
                    }
                }
        	}); */
        	
        	connectedDeviceStore = new Ext.data.JsonStore({
        		autoLoad: {params: {start:0, limit:pageSize}},
        		url: '${ctx}/gadget/device/getConDevice.do',
        		baseParams: {
        			mcuId : mcuId,
	                isLatest : conditionArray[1],
	                isPoor : conditionArray[2],
	                startDate : conditionArray[3]+conditionArray[4],                    
	                endDate : conditionArray[5]+conditionArray[6],
	                pageSize : pageSize,
                    supplierId:supplierId,
        		},
        		totalProperty : 'totalCount',
        		root : 'result',
        		fields: ["date", "device_id", "mcuId", "slast", "savg", "smin", "smax", ],
                listeners: {
                	beforeload : function(store, options) {
        				options.params || (options.params = {});
        				Ext.apply(options.params, {
        					page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)
        				});
        			},                    
                }
        		
        	}); // -- Store
        	
        	connectedDeviceCol = new Ext.grid.ColumnModel({
        		columns : [
					{header: "<font style='font-weight: bold;'>Date</font>", dataIndex: 'date', renderer:isEmptyCell},
					{header: "<font style='font-weight: bold;'>Modem ID</font>", dataIndex: 'device_id', },
					{header: "<font style='font-weight: bold;'>Real Path</font>", dataIndex: 'mcuId', renderer:realPath, },
					{header: "<font style='font-weight: bold;'>Last</font>", dataIndex: 'slast', renderer:isEmptyCell},
					{header: "<font style='font-weight: bold;'>AVG</font>", dataIndex: 'savg', renderer: cutThePoint},
					{header: "<font style='font-weight: bold;'>MIN</font>", dataIndex: 'smin', renderer:isEmptyCell},
					{header: "<font style='font-weight: bold;'>MAX</font>", dataIndex: 'smax', renderer:isEmptyCell},
        		           ],
	           	defaults : {
	           		sortable : false,
	           		menuDisable : true,
	           		align : 'center',
	           		width: 120
	           	},
	           	rows:[[
	           	       {},
	           	       {},
	           	       {},
	           	       {header:'SNR(dB)', colspan:4, align:'center'}
	           	       ]]
        	});
        	
        	function realPath(val){
        		if(val.search('[)]') >= 0){
        			//val = val.slice(1);
        			return '<p style="color:red;">'+val+'</p>';
        		}
        		if("".match(val) || val==undefined){
        			return "-";
        		}
        		return val;
        	}
        	
        	function isEmptyCell(val){
        		if("".match(val) || val==undefined){
        			return "-";
        		}
        		return val;
        	}
        	
        	function cutThePoint(val){        		
        		if("".match(val) || val==undefined){
        			return "-";
        		}else{
        			return val.toFixed(2);
        		}
        	}
        	
        	//현재 탭이 signal tab인때에 한하여 그리드 생성
        	if(currentDivTab == 'signalToNoiseTab'){
        		$('#connectedDeviceGrid').html('');
        		connectedDeviceGrid = new Ext.grid.GridPanel({
        			store: connectedDeviceStore,
        			colModel: connectedDeviceCol,
        			plugins: [new Ext.ux.plugins.GroupHeaderGrid()],
        			sm: new Ext.grid.RowSelectionModel({
                        singleSelect:true,
                        listeners: {
                            rowselect: function(sm, row, rec) {
                                var data = rec.data;
                                //모뎀별 차트 그리기
                                modemId = data.device_id;
                                $('#LatestDateMask2Text').text('Display the last data of [' + modemId + ']');
                                drawNoiseDetailsGrid();
                                drawNoiseDetailsChart();
                            }
                        }
                    }),
                    autoScroll:false,
                   // width: width,
                    height: 242,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    //renderTo: 'connectedDeviceGrid',
                    viewConfig: {
                    	scrollOffset : 0,
                        forceFit: true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: connectedDeviceStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
        		});
        		
        		connectedDeviceGrid.reconfigure(connectedDeviceStore,connectedDeviceCol);
        		connectedDeviceGrid.render('connectedDeviceGrid');
        	}else {
        		//현재 보고있는 탭이 sinalTab이 아니므로 아무것도 하지않음
        	}        	
        	
        }
        
        
        var drawNoiseDetailsChart = function(){       	
        	//visibility
        	//$('#noiseDetailsDiv').show();
        	var deviceId = modemId;
        	var fcChartDataXml;
        	var conditionArray = getSignalTabCondition('details');
        	$.ajax({
        		type : "POST",
        		data : {
        			modemId : deviceId,
        			isLatest : conditionArray[1],
        			startDate : conditionArray[3]+conditionArray[4],
        			endDate : conditionArray[5]+conditionArray[6],
                    supplierId:supplierId,
        		},
        		dataType : "json",
        		url : "${ctx}/gadget/device/getDetailSnr.do",
        		
        		success : function(json, status) {
        			if(json.result!=null)	var list = json.result.reverse();
        			else var list="";
        			fcChartDataXml = '{ "chart" : { '
        				+ '"caption" : "Signal-to-noise-ratio (dB) Per Date", '
        				//+ '"xaxisname" : "Date", '
        				+ '"yAxisMinValue": "-10", '
        				+ '"showZeroPlane" : "1", '
        				+ '"zeroPlaneThickness": "3",'
        				+ '"showBorder" : "0", '
        				+ '"borderColor" : "#99bbe8",'
        				+ '"anchorRadius" : "5",'
        				+ '"exportEnabled" :"1", '
        				+ '"exportAtClientSide": "1", '
        				+ '"labelDisplay" : "stagger", '
        				+ '"formatNumberScale" : "0", '
        				+ '"theme" : "fint" '
        				+ '},';
        			
        			var chartData = '"data" : [ '
// 						+ '{ "label" : "08:00", "value" : "1001"  },'
// 						+ '{ "label" : "10:00", "value" : "1201"  },'
// 						+ '{ "label" : "12:00", "value" : "1501"  },'
// 						+ '{ "label" : "14:00", "value" : "1701"  },'
// 						+ '{ "label" : "16:00", "value" : "1901"  },'
// 						+ '{ "label" : "22:00", "value" : "2001"  }'
// 						+ '],';        			
        			for(index in list){
        				if(index==0){
        					chartData += '{ "label" : "' + list[index].date + '",'
    							+ ' "value" : "' + list[index].snr + '" }';
        				}else{
        					if(!isNaN(index)) {
        					chartData += ',{ "label" : "' + list[index].date + '",'
        						+ ' "value" : "' + list[index].snr + '" }';
	        				}	
        				}
        			}
        			chartData += '],';

					
        			
        			var trendLines = '"trendlines" : [ '
        			    + '{ "line" : [ '
        			    + '{ "startvalue": "-2", '
                        + '"color": "#ff0000", '
                        + '"displayvalue": "Critical Limit", '
                        + '"dashed":"1", '
                        + '"valueOnRight": "1"'
                        + '} ] } ] }';
        			
        			fcChartDataXml = fcChartDataXml + chartData + trendLines;
        			
        			var noiseDetailsChart = new FusionCharts({
        				type: 'line',
        				renderAt : 'noiseDetailsChartSpace',
        				width : '68%',
        				height : '350',
        				dataFormat : 'json',
        				dataSource : fcChartDataXml
        			});
        			
        			noiseDetailsChart.render();
        		
        		}
        	});        	
        }
                
        var drawNoiseDetailsGrid = function(){
        	var detWidth = $('#dcuGridDiv').width()*0.25;        	
        	var deviceId = modemId;
        	//visibility
        	$('#noiseDetailsDiv').show();
        	
        	var pageSize = 10;
        	var conditionArray = getSignalTabCondition('details');
        	var noiseDetStore;
        	var noiseDetCol;
        	var noiseDetGrid;
        	
        	noiseDetStore = new Ext.data.JsonStore({
        		//autoLoad : {params:{start: 0, limit: pageSize}},
        		autoLoad : true,
        		url: "${ctx}/gadget/device/getDetailSnr.do",
        		baseParams: {
        			modemId : deviceId,
        			isLatest : conditionArray[1],
        			startDate : conditionArray[3]+conditionArray[4],
        			endDate : conditionArray[5]+conditionArray[6],
                    supplierId:supplierId,
        		},
        		idProperty : 'date',
        		root : 'result',
        		fields: ['date', 'snr', 'mcuId'],        		
        	});
        	
        	noiseDetCol = new Ext.grid.ColumnModel({
        		columns : [
					{header: "<font style='font-weight: bold;'>Date</font>", dataIndex: 'date', width:10},
					{header: "<font style='font-weight: bold;'>SNR(dB)</font>", dataIndex: 'snr', width:5},
					{header: "<font style='font-weight: bold;'>Real Path</font>", dataIndex: 'mcuId', width:5},
        		           ],
				defaults : {
					sortable : false,
					menuDisable : true,
					align : 'center',
					width : 120
				},
        	});
        	
        	function isPoorSnr(val){
        		if(val <= -2){
        			return '<p style="color:red;">'+val+'</p>';
        		}
        		return val;
        	}
        	
        	// GRID 출력
        	noiseDetGrid = new Ext.grid.GridPanel({
        		store : noiseDetStore,
        		colModel : noiseDetCol,
        		autoScroll : false,        		
        		height : 350,
        		width : detWidth,
        		//autoExpandColumn : true,
        		stripeRows : true,
        		columnLines : true,
        		loadMask : {
        			msg : 'loadijng...'
        		},
        		viewConfig: {
        			forceFit: true,
        			enableRowBody : true,
        			showPreview : true,
        			emptyText : 'No data to display'
        		},
        		// without paging bar 
        	});
        	
        	$('#noiseDetailsGridSpace').html('');
        	
        	noiseDetGrid.reconfigure(noiseDetStore, noiseDetCol);
        	noiseDetGrid.render('noiseDetailsGridSpace');
        
        }
        
        var connectedDeviceSearch = function(){
        	//search 버튼
        	drawSnrConnectedDeviceGrid();
        	//noisechartdiv 숨김
        	$('#noiseDetailsDiv').hide();
        }
        
        var noiseDetailsSearch = function(){
        	//search 버튼
        	//drawNoiseChart();
        	drawNoiseDetailsGrid();
        	drawNoiseDetailsChart();
        }

        var drawCodiTab = function() {

            $.getJSON('${ctx}/gadget/device/getMCUCodi.do', {'mcuId' : mcuId},

                function(data) {

                    if(data.isNull == 'true') {
                        $('#codiID').val('');
                        $('#codiIndex').val('');
                        $('#codiType').val('');
                        $('#codiShortID').val('');
                        $('#codiFwVer').val('');
                        $('#codiHwVer').val('');
                        $('#codiFwBuild').val('');
                        $('#codiZAIfVer').val('');
                        $('#codiZZIfVer').val('');
                        $('#codiResetKind').val('');
                        $('#codiAutoSetting').val('');
                        $('#codiChannel').val('');
                        $('#codiPanID').val('');
                        $('#codiExtPanId').val('');
                        $('#codiPermit').val('');
                        $('#codiRfPower').val('');
                        $('#codiTxPowerMode').val('');
                        $('#codiEnableEncrypt').val('');
                        $('#codiLinkKey').val('');
                        $('#codiNetworkKey').val('');
                    } else {
                        var codiIndex = data.mcuCodi.codiIndex;
                        var codiIndexName = '';
                        if(codiIndex == 0)
                            codiIndexName = 'CODI#1';
                        else if(codiIndex == 1)
                            codiIndexName = 'CODI#2';

                        var codiType = data.mcuCodi.codiType;
                        var codiTypeName = '';
                        if(codiType == 0)
                            codiTypeName = 'Unknown Type';
                        else if(codiType == 1)
                            codiTypeName = 'Ember Stack 3.3.1';
                        else if(codiType == 2)
                            codiTypeName = 'Ember Statck 3.3.3';
                        else if(codiType == 10)
                            codiTypeName = 'Zigbee Statck 2.5.4';
                        else if(codiType == 11)
                            codiTypeName = 'Zigbee Statck 3.0.x';
                        else if(codiType == 12)
                            codiTypeName = 'Zigbee Statck 3.1.x';
                        else if(codiType == 13)
                            codiTypeName = 'Zigbee Statck 3.2.x';

                        var codiTxPowerMode = data.mcuCodi.codiTxPowerMode;
                        var codiTxPowerModeName = '';
                        if(codiTxPowerMode == 0)
                            codiTxPowerModeName = 'Default';
                        else if(codiTxPowerMode == 1)
                            codiTxPowerModeName = 'Boost';
                        else if(codiTxPowerMode == 2)
                            codiTxPowerModeName = 'Alternate';
                        else if(codiTxPowerMode == 10)
                            codiTxPowerModeName = 'Alternate & Boost';

                        var codiEnableEncrypt = data.mcuCodi.codiEnableEncrypt;
                        var codiEnableEncryptName = '';
                        if(codiEnableEncrypt == 0)
                            codiEnableEncryptName = 'disable';
                        else if(codiEnableEncrypt == 1)
                            codiEnableEncryptName = 'enable';

                        $('#codiID').val(data.mcuCodi.codiID);
                        $('#codiIndex').val(codiIndexName);
                        $('#codiType').val(codiTypeName);
                        $('#codiShortID').val(data.mcuCodi.codiShortID);
                        $('#codiFwVer').val(data.codiFwVer);
                        $('#codiHwVer').val(data.codiHwVer);
                        $('#codiFwBuild').val(data.mcuCodi.codiFwBuild);
                        $('#codiZAIfVer').val(data.codiZAIfVer);
                        $('#codiZZIfVer').val(data.codiZZIfVer);
                        $('#codiResetKind').val(data.mcuCodi.codiResetKind);
                        $('#codiAutoSetting').val(data.mcuCodi.codiAutoSetting);
                        $('#codiChannel').val(data.mcuCodi.codiChannel);
                        $('#codiPanID').val(data.mcuCodi.codiPanID);
                        $('#codiExtPanId').val(data.mcuCodi.codiExtPanId);
                        $('#codiPermit').val(data.mcuCodi.codiPermit);
                        $('#codiRfPower').val(data.mcuCodi.codiRfPower);
                        $('#codiTxPowerMode').val(codiTxPowerModeName);
                        $('#codiEnableEncrypt').val(codiEnableEncryptName);
                        $('#codiLinkKey').val(data.mcuCodi.codiLinkKey);
                        $('#codiNetworkKey').val(data.mcuCodi.codiNetworkKey);
                    }
                }
            );
        };

        var drawScheduleTab = function() {

            $.getJSON('${ctx}/gadget/device/getMCUVar.do', {'mcuId' : mcuId},

                function(data) {

                    var isNull = data.isNull;

                    var varMeterDayMask = '00000000000000000000000000000000';
                    var varEventReadDayMask = '00000000000000000000000000000000';
                    var varMeterTimesyncDayMask = '00000000000000000000000000000000';
                    var varRecoveryDayMask = '00000000000000000000000000000000';

                    var varMeterHourMask = '000000000000000000000000';
                    var varEventReadHourMask = '000000000000000000000000';
                    var varMeterTimesyncHourMask = '000000000000000000000000';
                    var varRecoveryHourMask = '000000000000000000000000';
                    var varMeterUploadCycleHourly = '000000000000000000000000';
                    var varMeterUploadCycleDaily = '0000000000000000000000000000000';

                    var varMeterUploadCycleType = null;

                    if(isNull == 'true') {

                        $("#varEnableReadMeterEvent option[value=true]").attr("selected", "true");
                        $("#varEnableMeterTimesync option[value=true]").attr("selected", "true");
                        $("#varEnableAutoUpload option[value=true]").attr("selected", "true");
                        $("#varEnableRecovery option[value=true]").attr("selected", "true");

                        $('#varMeterStartMin').val('');
                        $('#varMeteringPeriod').val('');
                        $('#varMeteringRetry').val('');
                        $('#varEnableReadMeterEvent').val('');

                        $('#varMeterUploadCycleType').val('');
                        $('#varMeterUploadStartHour').val('');
                        $('#varMeterUploadStartMin').val('');
                        $('#varMeterUploadTryTime').val('');
                        $('#varMeterUploadRetry').val('');
                        $('#varRecoveryStartMin').val('');
                        $('#varRecoveryPeriod').val('');
                        $('#varRecoveryRetry').val('');

                        $("#varEnableReadMeterEvent option[value=true]").attr("selected", "true");
                        $("#varEnableMeterTimesync option[value=true]").attr("selected", "true");
                        $("#varEnableAutoUpload option[value=true]").attr("selected", "true");
                        $("#varEnableRecovery option[value=true]").attr("selected", "true");


                        for(var i = 0; i <= 23; i++) {

                            var varMeterUploadCycleHourlyBool = varMeterUploadCycleHourly.charAt(i);

                            if(varMeterUploadCycleHourlyBool == '0')
                                $('#varMeterUploadCycleHourly_' + i).attr('checked', false);
                            else
                                $('#varMeterUploadCycleHourly_' + i).attr('checked', true);
                        }
                    } else if(isNull == 'false') {

                        varMeterDayMask = data.varMeterDayMask;
                        varEventReadDayMask = data.varEventReadDayMask;
                        varMeterTimesyncDayMask = data.varMeterTimesyncDayMask;
                        varRecoveryDayMask = data.varRecoveryDayMask;

                        varMeterHourMask = data.varMeterHourMask;
                        varEventReadHourMask = data.varEventReadHourMask;
                        varMeterTimesyncHourMask = data.varMeterTimesyncHourMask;

                        varRecoveryHourMask = data.varRecoveryHourMask;

                        var mcuVar = data.mcuVar;
                        $('#varMeterStartMin').val(mcuVar.varMeterStartMin);
                        $('#varMeteringPeriod').val(mcuVar.varMeteringPeriod);
                        $('#varMeteringRetry').val(mcuVar.varMeteringRetry);
                        $('#varEnableReadMeterEvent').val(mcuVar.varEnableReadMeterEvent);

                        $('#varMeterUploadCycleType').val(mcuVar.varMeterUploadCycleType);
                        $('#varMeterUploadStartHour').val(mcuVar.varMeterUploadStartHour);
                        $('#varMeterUploadStartMin').val(mcuVar.varMeterUploadStartMin);
                        $('#varMeterUploadTryTime').val(mcuVar.varMeterUploadTryTime);
                        $('#varMeterUploadRetry').val(mcuVar.varMeterUploadRetry);
                        $('#varRecoveryStartMin').val(mcuVar.varRecoveryStartMin);
                        $('#varRecoveryPeriod').val(mcuVar.varRecoveryPeriod);
                        $('#varRecoveryRetry').val(mcuVar.varRecoveryRetry);

                        var varEnableReadMeterEvent = data.varEnableReadMeterEvent;
                        if(varEnableReadMeterEvent == true) {
                            $("#varEnableReadMeterEvent option[value=true]").attr("selected", "true");
                        } else {
                            $("#varEnableReadMeterEvent option[value=false]").attr("selected", "true");
                        }
                        var varEnableMeterTimesync = data.varEnableMeterTimesync;
                        if(varEnableMeterTimesync == true)
                            $("#varEnableMeterTimesync option[value=true]").attr("selected", "true");
                        else
                            $("#varEnableMeterTimesync option[value=false]").attr("selected", "true");

                        var varEnableAutoUpload = data.varEnableAutoUpload;
                        if(varEnableAutoUpload == true) {
                            $("#varEnableAutoUpload option[value=true]").attr("selected", "true");
                        } else {
                            $("#varEnableAutoUpload option[value=false]").attr("selected", "true");
                        }
                        var varEnableRecovery = data.varEnableRecovery;
                        if(varEnableRecovery == true)
                            $("#varEnableRecovery option[value=true]").attr("selected", "true");
                        else
                            $("#varEnableRecovery option[value=false]").attr("selected", "true");


                        varMeterUploadCycleType = data.mcuVar.varMeterUploadCycleType;

                        if(varMeterUploadCycleType == '4' || varMeterUploadCycleType == '0') {
                            //$('#varMeterUploadCycleHH').attr('disabled', true);
                            varMeterUploadCycleHourly = data.varMeterUploadCycle;

                            for(var i = 0; i <= 23; i++) {

                                var varMeterUploadCycleHourlyBool = varMeterUploadCycleHourly.charAt(i);

                                if(varMeterUploadCycleHourlyBool == '0')
                                    $('#varMeterUploadCycleHourly_' + i).attr('checked', false);
                                else
                                    $('#varMeterUploadCycleHourly_' + i).attr('checked', true);
                            }

                            $("input[name='varMeterUploadCycleHourly']").each(function(){
                                $(this).attr('disabled', false);
                            });
                        } else if(varMeterUploadCycleType == '3') {
                            //$('#varMeterUploadCycleHH').attr('disabled', true);
                            $('#varMeterUploadCycleWeekly_' + data.varMeterUploadCycle).attr('checked', true);
                        } else if(varMeterUploadCycleType == '2') {
                            //$('#varMeterUploadCycleHH').attr('disabled', true);
                            varMeterUploadCycleDaily = data.varMeterUploadCycle;

                            for(var i = 1; i <= 31; i++) {
                                var varMeterUploadCycleDailyBool = varMeterUploadCycleDaily.charAt(i-1);
                                if(varMeterUploadCycleDailyBool == '0')
                                    $('#varMeterUploadCycleDaily_' + i).attr('checked', false);
                                else
                                    $('#varMeterUploadCycleDaily_' + i).attr('checked', true);
                            }

                            $("input[name='varMeterUploadCycleDaily']").each(function(){
                                $(this).attr('disabled', false);
                            });
                        }
                    };

                    for(var i = 1; i <= 31; i++) {

                        var varMeterDayMaskBool = varMeterDayMask.charAt(i - 1);
                        var varEventReadDayMaskBool = varEventReadDayMask.charAt(i - 1);
                        var varMeterTimesyncDayMaskBool = varMeterTimesyncDayMask.charAt(i - 1);
                        var varRecoveryDayMaskBool = varRecoveryDayMask.charAt(i - 1);

                        if(varMeterDayMaskBool == '0')
                            $('#varMeterDayMask_' + i).attr('checked', false);
                        else
                            $('#varMeterDayMask_' + i).attr('checked', true);

                        if(varEventReadDayMaskBool == '0')
                            $('#varEventReadDayMask_' + i).attr('checked', false);
                        else
                            $('#varEventReadDayMask_' + i).attr('checked', true);

                        if(varMeterTimesyncDayMaskBool == '0')
                            $('#varMeterTimesyncDayMask_' + i).attr('checked', false);
                        else
                            $('#varMeterTimesyncDayMask_' + i).attr('checked', true);

                        if(varRecoveryDayMaskBool == '0')
                            $('#varRecoveryDayMask_' + i).attr('checked', false);
                        else
                            $('#varRecoveryDayMask_' + i).attr('checked', true);
                    }

                    for(var i = 0; i <= 23; i++) {

                        var varMeterHourMaskBool = varMeterHourMask.charAt(i);
                        var varEventReadHourMaskBool = varEventReadHourMask.charAt(i);
                        var varMeterTimesyncHourMaskBool = varMeterTimesyncHourMask.charAt(i);
                        var varRecoveryHourMaskBool = varRecoveryHourMask.charAt(i);

                        if(varMeterHourMaskBool == '0')
                            $('#varMeterHourMask_' + i).attr('checked', false);
                        else
                            $('#varMeterHourMask_' + i).attr('checked', true);

                        if(varEventReadHourMaskBool == '0')
                            $('#varEventReadHourMask_' + i).attr('checked', false);
                        else
                            $('#varEventReadHourMask_' + i).attr('checked', true);

                        if(varMeterTimesyncHourMaskBool == '0')
                            $('#varMeterTimesyncHourMask_' + i).attr('checked', false);
                        else
                            $('#varMeterTimesyncHourMask_' + i).attr('checked', true);

                        if(varRecoveryHourMaskBool == '0')
                            $('#varRecoveryHourMask_' + i).attr('checked', false);
                        else
                            $('#varRecoveryHourMask_' + i).attr('checked', true);
                    }

                    $("#varEnableReadMeterEvent").selectbox();
                    $("#varEnableMeterTimesync").selectbox();
                    $("#varEnableAutoUpload").selectbox();
                    $("#varMeterUploadCycle").selectbox();
                    $("#varMeterUploadCycleType").selectbox();
                    $("#varEnableRecovery").selectbox();

                    //0: unknown 1: Immediately(즉시), 2: Daily 3: Weekly 4: hourly
                    if(varMeterUploadCycleType == 4 || varMeterUploadCycleType == 0) {
                        $("#upload_weekTable").hide();
                        $("#upload_dateTable").hide();
                        $("#upload_timeTable").show();
                        $("#uploadStartHour").hide();
                        $("#uploadStartMin").show();
                   } else if(varMeterUploadCycleType == 3) {
                        $("#upload_dateTable").hide();
                        $("#upload_timeTable").hide();
                        $("#upload_weekTable").show();
                        $("#uploadStartHour").show();
                        $("#uploadStartMin").show();
                   } else if(varMeterUploadCycleType == 2) {
                        $("#upload_weekTable").hide();
                        $("#upload_timeTable").hide();
                        $("#upload_dateTable").show();
                        $("#uploadStartHour").show();
                        $("#uploadStartMin").show();
                   } else if(varMeterUploadCycleType == 1){
                        $("#upload_weekTable").hide();
                        $("#upload_dateTable").hide();
                        $("#upload_timeTable").hide();
                        $("#uploadStartHour").hide();
                        $("#uploadStartMin").hide();
                   }

                }
            );
        };

        var changeVarMeterUploadCycleType = function(obj) {

            //0: unknown 1: Immediately(즉시), 2: Daily 3: Weekly 4: hourly
             if(obj.value == 4 || obj.value == 0) {
                $("#upload_weekTable").hide();
                $("#upload_dateTable").hide();
                $("#upload_timeTable").show();
                $("#uploadStartHour").hide();
                $("#uploadStartMin").show();
            } else if(obj.value == 3) {
                $("#upload_dateTable").hide();
                $("#upload_timeTable").hide();
                $("#upload_weekTable").show();
                $("#uploadStartHour").show();
                $("#uploadStartMin").show();
            } else if(obj.value == 2) {
                $("#upload_weekTable").hide();
                $("#upload_timeTable").hide();
                $("#upload_dateTable").show();
                $("#uploadStartHour").show();
                $("#uploadStartMin").show();
            } else if(obj.value == 1) {
                $("#upload_weekTable").hide();
                $("#upload_dateTable").hide();
                $("#upload_timeTable").hide();
                $("#uploadStartHour").hide();
                $("#uploadStartMin").hide();
            }
        };

        var allCheck = function(obj, checkBoxId) {

            if(obj.checked) {
                $("input[name='" + checkBoxId +"']").each(function(){
                    $(this).attr('checked', true);
                });
            } else {
                $("input[name='" + checkBoxId +"']").each(function(){
                    $(this).attr('checked', false);
                });
            }
        };

        // 통신 로그
        var communicationLogSearch = function() {
            getCommunicationLogList();

            $.getJSON("${ctx}/gadget/device/getCommLogData.do"
                    ,{supplierId : supplierId,
                      senderId : mcuName,
                      startDate : $("#communicationLogStartDateHidden").val(),
                      endDate : $("#communicationLogEndDateHidden").val()}
                    ,function(json) {
                        var result = json.result;
                        var totalSend = "0";
                        var maxSend = "0";
                        var minSend = "0";
                        var totalRcv = "0";
                        var maxRcv = "0";
                        var minRcv = "0";

                        if (result != null && result.length > 0) {
                            totalSend = result[0].sendSum;
                            maxSend = result[0].sendMax;
                            minSend = result[0].sendMin;
                            totalRcv = result[0].rcvSum;
                            maxRcv = result[0].rcvMax;
                            minRcv = result[0].rcvMin;
                        }
                        $('#totalSend').html(totalSend);
                        $('#maxSend').html(maxSend);
                        $('#minSend').html(minSend);
                        $('#totalRcv').html(totalRcv);
                        $('#maxRcv').html(maxRcv);
                        $('#minRcv').html(minRcv);
                    });
        };

        // 변경 이력
        var updateLogSearch = function() {

            var arrayObj = Array();
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#updateLogStartDate').val(), searchEndDate:$('#updateLogEndDate').val(), supplierId:supplierId}
                    ,function(json) {
                        arrayObj[0] = json.searchStartDate;
                        arrayObj[1] = json.searchEndDate;
                    });

            arrayObj[2] = mcuName;

            getFlexObject('updateLogFlex').searchList(arrayObj);
        };

        // 장애 이력
        var brokenLogSearch = function() {
            getEventAlertLogList();
        };

        // 명령 이력
        var commandLogSearch = function() {
            getOperationLogList();
        };

        var setMcuName = function(_mcuName, _mcuType) {
            mcuName = _mcuName;
            mcuType = _mcuType;
        };

        // 위치정보 탭의 연결장비 목록
        var drawPositionDivTab = function() {

            $.getJSON('${ctx}/gadget/device/getMcuLocation.do'
                    , {'mcuId' : mcuId}
                    , function(json) {
                        if (json.isNull) {
                            $("#gpioX").val("");
                            $("#gpioY").val("");
                            $("#gpioZ").val("");
                            $("#sysLocationInfo").val("");
                        } else {
                            $("#gpioX").val(json.gpioX);
                            $("#gpioY").val(json.gpioY);
                            $("#gpioZ").val(json.gpioZ);
                            $("#sysLocationInfo").val(json.sysLocation);
                        }
                    });
        };

        var getMcuId = function() {
            return mcuId;
        };

        var mcuInsertDivCancel = function() {
            $('#mcuInsertDiv').hide();
            $('#mcuInfoDiv').show();
        }
        // mcu 추가 div로 gogo..
        var mcuInsertDiv = function() {

            $('#mcuInsertForm').each(function(){
                this.reset();
                });

            // 모뎀유형 초기화
            $.getJSON('${ctx}/gadget/system/getChildCode.do'
                    , {'code' : '1.1.1'}
                    , function (returnData){

                        var result = returnData.code;
                        var arr = Array();
                        for (var i = 0; i < result.length; i++) {
                            var obj = new Object();
                            obj.name=result[i].descr;
                            obj.id=result[i].id;
                            arr[i]=obj
                        };
                        $('#singleRegMCUmcuType').pureSelect(arr);
                        $('#singleRegMCUmcuType').selectbox();
                    });

            // HW버전 초기화
            $.getJSON('${ctx}/gadget/system/getChildCode.do'
                    , {'code' : '1.1.2'}
                    , function (returnData){

                        var result = returnData.code;
                        var arr = Array();
                        for (var i = 0; i < result.length; i++) {
                            var obj = new Object();
                            obj.name=result[i].descr;
                            obj.id=result[i].id;
                            arr[i]=obj
                        };
                        $('#singleRegMCUmcuHwVer').pureSelect(arr);
                        $('#singleRegMCUmcuHwVer').selectbox();
                    });

            // SW버전 초기화
            $.getJSON('${ctx}/gadget/system/getChildCode.do'
                    , {'code' : '1.1.3'}
                    , function (returnData){
                        var result = returnData.code;
                        var arr = Array();
                        for (var i = 0; i < result.length; i++) {
                            var obj = new Object();
                            obj.name=result[i].descr;
                            obj.id=result[i].id;
                            arr[i]=obj
                        };
                        $('#singleRegMCUmcuSwVer').pureSelect(arr);
                        $('#singleRegMCUmcuSwVer').selectbox();
                        //$('#singleRegMCUmcuSwReVer').selectbox();
                    });

            // 제조사 조회
            $.getJSON('${ctx}/gadget/system/vendorlist.do'
                    , {'supplierId' : supplierId}
                    , function (returnData){
                        $('#singleRegMCUmcuVendor').pureSelect(returnData.deviceVendors);
                        $('#singleRegMCUmcuVendor').selectbox();
                        getModelListByVendorMCU();
                    });

            // 통신타입 조회
            $.getJSON('${ctx}/gadget/system/getChildCode.do'
                    , {'code' : '4.6'}
                    , function (returnData){

                        var result = returnData.code;
                        var arr = Array();
                        for (var i = 0; i < result.length; i++) {
                            var obj = new Object();
                            obj.name=result[i].descr;
                            obj.id=result[i].id;
                            arr[i]=obj
                        };
                        $('#singleRegMCUmcuCommType').pureSelect(arr);
                        $('#singleRegMCUmcuCommType').selectbox();
                    });

            // 모델 초기화
            $('#singleRegMCUmcuModel').emptySelect();
            $('#singleRegMCUmcuModel').selectbox();

            locationTreeGoGo('treeDivMcu', 'singleRegMCUmcuLoc', 'locationIdMcu');

            displayMcuInstallUpdateDiv('mcuInsertDiv');
        };

        // 개별등록 > 집중기  - 모델조회
        function getModelListByVendorMCU() {
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                    , {'vendorId' : $('#singleRegMCUmcuVendor').val()
                    ,  'deviceType' : 'MCU'
                    ,  'subDeviceType' : ' '}
                    , function (returnData) {
                        if (returnData.deviceModels.length != 0) {
                            $('#singleRegMCUmcuModel').pureSelect(returnData.deviceModels);
                            $('#singleRegMCUmcuModel').selectbox();
                        } else {
                            $('#singleRegMCUmcuModel').noneSelect(null);
                            $('#singleRegMCUmcuModel').selectbox();
                        }
            });
        }

        // 개별등록 > 모뎀  - 집중기 중복확인
        function singleRegMCUIsMCUDuplicate() {
        	var chkMcuId = $.trim($('#singleRegMCUmcuId').val());
        	$('#singleRegMCUmcuId').val(chkMcuId);
        	preSysId = chkMcuId;
            if ($('#singleRegMCUmcuId').val() == null || $('#singleRegMCUmcuId').val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputDCUId'/>");
                $('#singleRegMCUmcuId').focus();
                mcuRegSysIdCheck = false;
            } else {
                $.getJSON('${ctx}/gadget/device/isMCUDuplicateByMcuId.do'
                        , {  'sysId': $('#singleRegMCUmcuId').val(), 'dummy': Math.random() }
                        , function (returnData) {
                            if (returnData.result == "true") {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>', $('#singleRegMCUmcuId').val()+ " <fmt:message key='aimir.alert.alreadyExist'/>");
                                $('#singleRegMCUmcuId').focus()
                                mcuRegSysIdCheck = false;
                            } else if(returnData.result == 'deleteStatus') {
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>', "<fmt:message key='aimir.cannot.use'/>");
                            	$('#singleRegMCUmcuId').focus()
                                mcuRegSysIdCheck = false;
                            } else {
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>', $('#singleRegMCUmcuId').val()+ " <fmt:message key='aimir.abailableId'/>");
                                mcuRegSysIdCheck = true;
                            }
                        });
            }
        }

        // 집중기  등록
        var mcuInsert = function() {
            // 사전검사
            if ($('#singleRegMCUmcuId').val() == null || $('#singleRegMCUmcuId').val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputMCUid'/>");
                return;
            } else {
                if (mcuRegSysIdCheck == false || preSysId != $('#singleRegMCUmcuId').val()) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateDCUId'/>");
                    return;
                }
            }


            $("#mcuInsertForm :input[id='supplierId']").val(supplierId);

            var deviceModelId   = $("#mcuInsertForm :input[id='singleRegMCUmcuModel']").val();
            var mcuTypeId       = $("#mcuInsertForm :input[id='singleRegMCUmcuType']").val();
            var protocolTypeId  = $("#mcuInsertForm :input[id='singleRegMCUmcuCommType']").val();
            var locationId      = $("#mcuInsertForm :input[id='locationIdMcu']").val();
            var mcuPhoto        = $("#mcuInsertForm :input[id='singleRegMCUmcuPhoto']").val();

            if (deviceModelId == "" || deviceModelId == null) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.mcu'/>: <fmt:message key='aimir.button.check'/> <fmt:message key='aimir.model'/>");
                return;
            }

            if ($('#singleRegMCUmcuSwReVer').val() == "" || $('#singleRegMCUmcuSwReVer').val() == null) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputRevision'/>");
                return;
            }
            if (locationId == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.report.mgmt.msg.validation.location'/>");
                return;
            }

            var params = {  success  : insertMcuResult
                          , url : '${ctx}/gadget/device/insertMCU.do?deviceModelId='
                              + deviceModelId + '&mcuTypeId=' + mcuTypeId + '&protocolTypeId=' + protocolTypeId + '&locationId=' + locationId + '&tempFile' + mcuPhoto
                          , type     : 'post'
                          , datatype : 'application/json'
                      };

              $('#mcuInsertForm').ajaxSubmit(params);

        };

        // 집중기 등록 후처리aimir.RegistrationComplete
        function insertMcuResult(responseText, status) {
            $("#mcuInsertDiv").hide();
            searchList();
            mcuRegSysIdCheck == false;
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.RegistrationComplete'/>");
        }

        // mcu 추가 div로 gogo..
        var mcuUpdateDiv = function() {

            if (mcuId == '') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.mcu.select" />');
                return ;
            }

            displayMcuInstallUpdateDiv('mcuUpdateDiv');

            $.getJSON('${ctx}/gadget/device/getMCU.do',
                {'mcuId' : mcuId},
                function(data) {
                    //$("#mcuUpdateForm :input[name='name']").val(data.mcu.name);
                    $("#mcuUpdateForm :input[name='ipAddr']").val(data.mcu.ipAddr);
                    $("#mcuUpdateForm :input[name='serviceAtm']").val(data.mcu.serviceAtm);
                    $("#mcuUpdateForm :input[name='gpioX']").val(data.mcu.gpioX);
                    $("#mcuUpdateForm :input[name='gpioY']").val(data.mcu.gpioY);
                    $("#mcuUpdateForm :input[name='gpioZ']").val(data.mcu.gpioZ);
                    $("#mcuUpdateForm :input[name='sysID']").val(data.mcu.sysID);
                    $("#mcuUpdateForm :input[name='sysPhoneNumber']").val(data.mcu.sysPhoneNumber);
                    $("#mcuUpdateForm :input[name='sysEtherType']").val(data.mcu.sysEtherType);
                    $("#mcuUpdateForm :input[name='sysMobileType']").val(data.mcu.sysMobileType);
                    $("#mcuUpdateForm :input[name='sysMobileMode']").val(data.mcu.sysMobileMode);
                    $("#mcuUpdateForm :input[name='sysMinTemp']").val(data.mcu.sysMinTemp);
                    $("#mcuUpdateForm :input[name='sysMaxTemp']").val(data.mcu.sysMaxTemp);
                    $("#mcuUpdateForm :input[name='sysServer']").val(data.mcu.sysServer);
                    $("#mcuUpdateForm :input[name='sysServerPort']").val(data.mcu.sysServerPort);
                    $("#mcuUpdateForm :input[name='sysServerAlarmPort']").val(data.mcu.sysServerAlarmPort);
                    $("#mcuUpdateForm :input[name='sysLocalPort']").val(data.mcu.sysLocalPort);
                    $("#mcuUpdateForm :input[name='sysMobileAccessPointName']").val(data.mcu.sysMobileAccessPointName);
                    $("#mcuUpdateForm :input[name='sysSwRevision']").val(data.mcu.sysSwRevision);

                    $("#deviceModelU option[value='" + data.mcu.deviceModel.id+ "']").attr("selected", "true");
                    $("#locationU option[value='" + data.mcu.location.id + "']").attr("selected", "true");
                    $("#mcuTypeU option[value='" + data.mcu.mcuType.id + "']").attr("selected", "true");
                    $("#protocolTypeU option[value='" + data.mcu.protocolType.id + "']").attr("selected", "true");
                    $("#sysHwVersionU option[value='" + data.mcu.sysHwVersion + "']").attr("selected", "true");
                    $("#sysSwVersionU option[value='" + data.mcu.sysSwVersion + "']").attr("selected", "true");

                    $("#deviceModelU").selectbox();
                    $("#locationU").selectbox();
                    $("#mcuTypeU").selectbox();
                    $("#protocolTypeU").selectbox();
                    $("#sysHwVersionU").selectbox();
                    $("#sysSwVersionU").selectbox();
            });
        };

        // mcu 변경
        var mcuUpdate = function() {

            var deviceModelId = $('#deviceModelU').val();
            var mcuTypeId = $('#mcuTypeU').val();
            var protocolTypeId = $('#protocolTypeU').val();
            var locationId = $('#locationU').val();

            if(document.mcuUpdateForm.serviceAtm.value == '') document.mcuUpdateForm.serviceAtm.value = '0';

            var params = {

                success :

                    function(data) {

                        if(data.mcu.id != '') {

                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.update.msg" />');

                            $('#mcuUpdateForm').clearForm();

                            mcuManagerResultHandler();

                        } else {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.erroroccured" />');
                        }

                    },
                url : '${ctx}/gadget/device/updateMCU.do?mcuId=' + mcuId + '&deviceModelId='
                    + deviceModelId + '&mcuTypeId=' + mcuTypeId + '&protocolTypeId=' + protocolTypeId + '&locationId=' + locationId,
                datatype : 'json'
            };

            $('#mcuUpdateForm').ajaxSubmit(params);
        };

        // mcu 삭제
        var mcuDelete = function() {

            if(mcuId == '') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.mcu.select" />');
                return ;
            }

            if(confirm('<fmt:message key="aimir.msg.wantdelete" />')) {

                $.post(
                    '${ctx}/gadget/device/updateDcuStatus.do',
                    {'mcuId' : mcuId},
                    function(data, status) {
                            		
                    	if(data.result == 0) {
                    		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deleteFail' />");
                    	} else {
                    		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deletesuccess'/>");
                    		mcuManagerResultHandler();
                    	}
                    	
                    }
                );

            } else {
            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.canceled" />');
                return ;
            }
        };

        var mcuManagerResultHandler = function() {

            // 조회 조건 초기화
            $('#sMcuId').val('');
            $('#sMcuType').val('');
            $('#sLocationId').val('');
            $('#sSwVersion').val('');
            $('#sHwVersion').val('');
            $('#sInstallDateStart').val('');
            $('#sInstallDateEnd').val('');
            // 페이지는 1 페이지로
            $('#page').val('1');

            // 선택된 MCU 없음
            mcuId = '';

            // mcuGrid 재조회
            searchList();

            // 일반정보 div 초기화
            var htmlText = "<input type='hidden' id='imgPage' value='${paging.page}' />";
            $('#generalDivTab').html(htmlText);

            // 화면에 보여질 display들 초기화
            displayMcuInfoMcuManageDiv('mcuInfoDiv');
            $('#generalDivTab').show();
            $('#positionDivTab').hide();
            $('#scheduleDivTab').hide();
            $('#logDivTab').hide();
            $('#mcuCodiDivTab').hide();
            $('#signalToNoiseTab').hide();

        };

        // mcuInfoDiv와  mcuManageDiv 보임/숨김
        var displayMcuInfoMcuManageDiv = function(_div) {

            if(_div == 'mcuInfoDiv') {
                $('#mcuInfoDiv').show();
                $('#mcuInsertDiv').hide();
            } else if(_div == 'mcuInsertDiv') {
                $('#mcuInfoDiv').hide();
                $('#mcuInsertDiv').show();
            }
        };

        // mcuManageDiv의 mcuInstallDiv와 mcuUpdateDiv 보임/숨김
        var displayMcuInstallUpdateDiv = function(_div) {

            displayMcuInfoMcuManageDiv(_div);

            if(_div == 'mcuInsertDiv') {
                $('#mcuInsertDiv').show();
                $('#mcuInfoDiv').hide();
            } else if(_div == 'mcuInfoDiv') {
                $('#mcuInsertDiv').hide();
                $('#mcuInfoDiv').show();
            }
        };

        var saveSchedule = function() {

            var varMeterDayMask = '';
            var varEventReadDayMask = '';
            var varMeterTimesyncDayMask = '';
            var varRecoveryDayMask = '';

            var varMeterHourMask = '';
            var varEventReadHourMask = '';
            var varMeterTimesyncHourMask = '';
            var varMeterUploadCycleHourly = '';
            var varMeterUploadCycleDaily = '';
            var varMeterUploadCycleWeekly = '';
            var varRecoveryHourMask = '';

            for(var i = 31 ; i >= 1; i--) {

                if($('#varMeterDayMask_' + i).attr('checked')) {
                    varMeterDayMask += '1';
                } else {
                    varMeterDayMask += '0';
                }

                if($('#varEventReadDayMask_' + i).attr('checked')) {
                    varEventReadDayMask += '1';
                } else {
                    varEventReadDayMask += '0';
                }

                if($('#varMeterTimesyncDayMask_' + i).attr('checked')) {
                    varMeterTimesyncDayMask += '1';
                } else {
                    varMeterTimesyncDayMask += '0';
                }

                if($('#varRecoveryDayMask_' + i).attr('checked')) {
                    varRecoveryDayMask += '1';
                } else {
                    varRecoveryDayMask += '0';
                }

                if($('#varMeterUploadCycleDaily_' + i).attr('checked')) {
                    varMeterUploadCycleDaily += '1';
                } else {
                    varMeterUploadCycleDaily += '0';
                }
            }

            for(var i = 23 ; i >= 0; i--) {

                if($('#varMeterHourMask_' + i).attr('checked')) {
                    varMeterHourMask += '1';
                } else {
                    varMeterHourMask += '0';
                }

                if($('#varEventReadHourMask_' + i).attr('checked')) {
                    varEventReadHourMask += '1';
                } else {
                    varEventReadHourMask += '0';
                }

                if($('#varMeterTimesyncHourMask_' + i).attr('checked')) {
                    varMeterTimesyncHourMask += '1';
                } else {
                    varMeterTimesyncHourMask += '0';
                }

                if($('#varMeterUploadCycleHourly_' + i).attr('checked')) {
                    varMeterUploadCycleHourly += '1';
                } else {
                    varMeterUploadCycleHourly += '0';
                }

                if($('#varRecoveryHourMask_' + i).attr('checked')) {
                    varRecoveryHourMask += '1';
                } else {
                    varRecoveryHourMask += '0';
                }
            }

            for(var i = 6 ; i >= 0; i--) {
                if($('#varMeterUploadCycleWeekly_' + i).attr('checked')) {
                    varMeterUploadCycleWeekly = i;
                }
            }

            varMeterDayMask = '0' + varMeterDayMask;
            varEventReadDayMask = '0' + varEventReadDayMask;
            varMeterTimesyncDayMask = '0' + varMeterTimesyncDayMask;
            varRecoveryDayMask = '0' + varRecoveryDayMask;
            varMeterUploadCycleDaily = '0' + varMeterUploadCycleDaily;

            var varMeterStartMin  = $('#varMeterStartMin').val();
            var varMeteringPeriod = $('#varMeteringPeriod').val();
            var varMeteringRetry = $('#varMeteringRetry').val();

            var varEnableReadMeterEvent = $('#varEnableReadMeterEvent').val();

            var varEnableMeterTimesync = $('#varEnableMeterTimesync').val();
            varEnableMeterTimesync ='true';
            var varEnableAutoUpload = $('#varEnableAutoUpload').val();
            var varMeterUploadCycleType = $('#varMeterUploadCycleType').val();
            var varMeterUploadStartHour = $('#varMeterUploadStartHour').val();
            var varMeterUploadStartMin = $('#varMeterUploadStartMin').val();
            var varMeterUploadTryTime = $('#varMeterUploadTryTime').val();
            var varMeterUploadRetry = $('#varMeterUploadRetry').val();

            var varEnableRecovery  = $('#varEnableRecovery').val();
            var varRecoveryStartMin = $('#varRecoveryStartMin').val();
            var varRecoveryPeriod  = $('#varRecoveryPeriod').val();
            var varRecoveryRetry = $('#varRecoveryRetry').val();

            var varMeterUploadCycle = '';

            //0: unknown 1: Immediately(즉시), 2: Daily 3: Weekly 4: hourly
            if(varMeterUploadCycleType == 4 || varMeterUploadCycleType == 0) {
                varMeterUploadCycle = varMeterUploadCycleHourly;
            } else if (varMeterUploadCycleType == 3) {
                varMeterUploadCycle = varMeterUploadCycleWeekly;
            } else if (varMeterUploadCycleType ==  2) {
                varMeterUploadCycle = varMeterUploadCycleDaily;
            } else if (varMeterUploadCycleType ==  1) {
                varMeterUploadCycle = 0;
            }

            var params = {'varMeterDayMask' : varMeterDayMask,
                'varEventReadDayMask' : varEventReadDayMask,
                'varMeterTimesyncDayMask' : varMeterTimesyncDayMask,
                'varRecoveryDayMask' : varRecoveryDayMask,

                'varMeterHourMask' : varMeterHourMask,
                'varEventReadHourMask' : varEventReadHourMask,
                'varMeterTimesyncHourMask' : varMeterTimesyncHourMask,
                'varRecoveryHourMask' : varRecoveryHourMask,
                'varMeterUploadCycle' : varMeterUploadCycle,

                'varMeterStartMin' : varMeterStartMin,
                'varMeteringPeriod' : varMeteringPeriod,
                'varMeteringRetry' : varMeteringRetry,

                'varEnableReadMeterEvent' : varEnableReadMeterEvent,

                'varEnableMeterTimesync' : varEnableMeterTimesync,

                'varEnableAutoUpload' : varEnableAutoUpload,
                'varMeterUploadCycleType' : varMeterUploadCycleType,
                'varMeterUploadStartHour' : varMeterUploadStartHour,
                'varMeterUploadStartMin' : varMeterUploadStartMin,
                'varMeterUploadTryTime' : varMeterUploadTryTime,
                'varMeterUploadRetry' : varMeterUploadRetry,

                'varEnableRecovery' : varEnableRecovery,
                'varRecoveryStartMin' : varRecoveryStartMin,
                'varRecoveryPeriod' : varRecoveryPeriod,
                'varRecoveryRetry' : varRecoveryRetry,
                'mcuId' : mcuId,
                'loginId' : loginId
            };

              $.getJSON('${ctx}/gadget/device/saveSchedule.do', params,
                 function(data) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',data.status);
                }
            );
        };

//----------------- 지도정보

        // 지도 정보 조회
        function drawMap(){
            // mcu의 정보를 구함
            showPoints('${ctx}/' + supplierId +'/'+ mcuName +'/mcu.do');
        }

        // 주소 정보 -> X,Y,Z로 변경함
        function getGeoCoding(){
            cvAddressToCoordinate($('#sysLocationInfo').val(), "gpioX", "gpioY", "gpioZ");
        }

        // 지도의 위도  / 경도 변경
        function updateMcuLoc(){
            $.getJSON('${ctx}/gadget/device/mapUpdate.do'
                    , {'className' : "mcu" ,
                        'name'     : mcuName ,
                        'pointx'   : $('#gpioX').val(),
                        'pointy'   : $('#gpioY').val() }
                    , function (returnData){
                        //alert("Geographic Coords Update");
                        Ext.MessageBox.show({
                            title : '<fmt:message key='aimir.meter'/> <fmt:message key='aimir.info'/>',
                            buttons : Ext.MessageBox.OK,
                            msg : '<fmt:message key='aimir.alert.geographicUpdate'/>',
                            icon : Ext.MessageBox.INFO,
                            fn : function() {
                                drawMap();
                            }
                        });

                    });

            drawMap();
        }


        // 주소 업데이트
        function updateMcuAddress(){

            $.getJSON('${ctx}/gadget/device/mapUpdateAddress.do'
                    , {'className' : "mcu",
                        'name'     : mcuName    ,
                        'address'  : encodeURIComponent($('#sysLocationInfo').val()) }
                    , function (returnData){
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.addressUpdate'/>");
                        });
        }

        function modifyDate(setDate, inst){
            var dateId = '#' + inst.id;

            var dateHiddenId = '#' + inst.id + 'Hidden';
            $(dateHiddenId).val($(dateId).val());

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        $(dateId).trigger('change');
                    });
        }

        function modifyDateInit(setDate, inst){
            var dateId = '#' + inst;
            var dateHiddenId = '#' + inst + 'Hidden';
            $(dateHiddenId).val($(dateId).val());

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        $(dateId).trigger('change');
                    });
        }

        function init(){
        }

        function mcuReset() {
            if(confirm("Are you Sure?")){
                $('#commandResult').val("Request MCU Reset....");
                $.getJSON('${ctx}/gadget/device/command/cmdMcuReset.do'
                        , {'target' : mcuId
                        , 'loginId' : loginId}
                        , function (returnData){
                            if(!returnData.status){
                                $('#commandResult').val("FAIL");
                                   return;
                            }
                            if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                                $('#commandResult').val("MCU Reset: "+returnData.status);
                            }else{
                                $('#commandResult').val("MCU Reset: "+returnData.status);
                            }
                        });
            }
        }

        function mcuTimeSync() {
            if(confirm("Are you Sure?")){
                $('#commandResult').val("Request MCU Time Sync....");
                $.getJSON('${ctx}/gadget/device/command/cmdMcuSetTime.do'
                        , {'target' : mcuId
                        , 'loginId' : loginId}
                        , function (returnData){
                            if(!returnData.status){
                                $('#commandResult').val("FAIL");
                                   return;
                            }
                            if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                                $('#commandResult').val("MCU Time Sync: "+returnData.status);
                            }else{
                                $('#commandResult').val("MCU Time Sync: "+returnData.status);
                            }
                        });
            }
        }

        function sensorScan() {
            $('#commandResult').val("Request Modem Info....");
            $.getJSON('${ctx}/gadget/device/command/mcuSensorScan.do'
                        , {'target' : mcuId
                        , 'loginId' : loginId}
                        , function (returnData){
                            if(!returnData.status){
                                $('#commandResult').val("FAIL");
                                   return;
                            }
                            if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                                $('#commandResult').val("Sensor Scan: "+returnData.status);
                            }else{
                                $('#commandResult').val("Sensor Scan: "+returnData.status);
                            }
                        });
        }

        function mcuDiagnosis() {
            $('#commandResult').val("Request MCU Diagnosis....");
            $.getJSON('${ctx}/gadget/device/command/cmdMcuDiagnosis.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(!returnData.status){
                            $('#commandResult').val("FAIL");
                               return;
                        }
                        if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                            $('#commandResult').val("MCU Diagnosis: "+returnData.status);
                        }else{
                            $('#commandResult').val("");
                            $('#commandResult').val($('#commandResult').val() + "MCU State: "+returnData.mcuState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Sink State: "+returnData.sinkState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Power State: "+returnData.powerState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Battery State: "+returnData.batteryState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Temperature State: "+returnData.temperatureState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Memory State: "+returnData.memoryState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Flash State: "+returnData.flashState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "GSM State: "+returnData.gsmState+"\n");
                            $('#commandResult').val($('#commandResult').val() + "Ethernet State: "+returnData.ethernetState);

                        }
                    });

        }

        function mcuStatusMonitoring() {
            $('#commandResult').val("Request MCU Status....");
            $.getJSON('${ctx}/gadget/device/command/getMCUStatus.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(!returnData.status){
                            $('#commandResult').val("FAIL");
                               return;
                        }
                        if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                            $('#commandResult').val("MCU Status Monitoring: "+returnData.status);
                        }else{
                            $('#commandResult').val("");
                            $('#commandResult').val(returnData.jsonString);//cmdMcuStatus.do에서 결과값을 만들어서 스트링으로 만듬 집중기마다 정보가 다르기 때문에 메시지를 고정할 수없음
                            /*
                            $('#commandResult').val($('#commandResult').val() + "Power State: "+(returnData.gpioPowerFail=!null && returnData.gpioPowerFail=="0" ? "Normal":"Power Fail")+"\n");//Power Fail (정전) 상태 (0:Normal, 1:Power Fail)
                            $('#commandResult').val($('#commandResult').val() + "Low Battery: "+(returnData.gpioLowBattery!=null && returnData.gpioLowBattery=="1" ? "Normal":"Low Battery")+"\n");//Low Battery 상태 (0=Low Battery, 1:Normal)
                            $('#commandResult').val($('#commandResult').val() + "Flash Total Size(KB): "+KiloForamt(returnData.flashTotalSize)+"\n");//총 Flash 메모리 크기 (KB)
                            $('#commandResult').val($('#commandResult').val() + "Flash Use Size(KB): "+KiloForamt(returnData.flashUseSize)+"\n");//사용중인 Flash 메모리 크기 (KB)
                            $('#commandResult').val($('#commandResult').val() + "Memory Total Size: "+MegaForamt(returnData.memTotalSize)+"\n");//총 메모리 크기
                            $('#commandResult').val($('#commandResult').val() + "Memory Use Size: "+MegaForamt(returnData.memUseSize)+"\n");//사용중인 메모리 크기
                            $('#commandResult').val($('#commandResult').val() + "Current Temp.: "+TempFormat(returnData.sysCurTemp)+"\n");//집중기 현재 온도 (현재 온도에 * 10 한값)
                            $('#commandResult').val($('#commandResult').val() + "System Time: "+DateType(returnData.sysTime)+"\n");//시스템 시간
                            $('#commandResult').val($('#commandResult').val() + "Sink Neighbor Node: "+(returnData.sinkNeighborNode==undefined?"":returnData.sinkNeighborNode)+"\n");//SINK Neighbor Node Count
                            $('#commandResult').val($('#commandResult').val() + "Sink State: "+(returnData.sinkState!=null && returnData.sinkState=="1" ? "Normal":"Abnormal"));//SINK 상태 (0:비정상, 1:정상)
                            */
                        }
                    });

        }

        function mcuScanning() {
            $('#commandResult').val("Request MCU Unit Scanning (System information)....");
            $.getJSON('${ctx}/gadget/device/command/cmdMcuScanning.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(!returnData.status){
                            $('#commandResult').val("FAIL");
                               return;
                        }
                        if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                            $('#commandResult').val("MCU Scanning: "+returnData.status);
                        }else{
                        	$('#commandResult').val(""); 
                            $('#commandResult').val(returnData.jsonString); //cmdMcuScanning.do에서 결과값을 만들어서 스트링으로 만듬 집중기마다 정보가 다르기 때문에 메시지를 고정할 수없음
                            /*
                            if(returnData.sysName)
                            $('#commandResult').val($('#commandResult').val() + "System Name: "+returnData.sysName+"\n");//mcu name
                            if(returnData.sysType)
                            $('#commandResult').val($('#commandResult').val() + "System Type: "+returnData.sysType+"\n");//mcu type
                            if(returnData.sysPhoneNumber)
                            $('#commandResult').val($('#commandResult').val() + "System Phone Number: "+returnData.sysPhoneNumber+"\n");//mobile phonenumber
                            if(returnData.sysUpTime)
                            $('#commandResult').val($('#commandResult').val() + "System Up Time: "+returnData.sysUpTime+"\n");//Boot time has elapsed
                            if(returnData.sysEtherType)
                            $('#commandResult').val($('#commandResult').val() + "System Ethernet Type: "+returnData.sysEtherType+"\n");//ethernet type
                            if(returnData.sysMobileType)
                            $('#commandResult').val($('#commandResult').val() + "System Mobile Type: "+returnData.sysMobileType+"\n");//mobile type
                            if(returnData.sysMobileMode)
                            $('#commandResult').val($('#commandResult').val() + "System Mobile Mode: "+returnData.sysMobileMode+"\n");//mobile connect type
                            if(returnData.hwVersion)
                            $('#commandResult').val($('#commandResult').val() + "System HW Version: "+returnData.hwVersion+"\n");//hw version
                            if(returnData.swVersion)
                            $('#commandResult').val($('#commandResult').val() + "System SW Version: "+returnData.swVersion);//sw version
                            */

                        }
                    });

        }
        
        function commandPing(){
        	Ext.Msg.prompt('Options For Ping', 'Please enter packet-size and echo-count.<br/><br/>Desc) packet-size, echo-count <br/>',
        			function(btn, text) {
        				if(btn=='ok'){
        					var data = text;
        					var packetSize = 0;
        					var count = 0;
        					if(data != null) {
        						var trimedData = data.replaceAll(" ", "");
								var splitedData = trimedData.split(',');
								
								if(2 < splitedData.length || splitedData.length < 1) {
									Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
									return false;
								}
								
								// 입력된 값이 숫자가 아닐 경우 오류 처리
								if(isNaN(splitedData[0]) == true || isNaN(splitedData[1]) == true) {
									Ext.Msg.alert('Error', 'Please enter number.');
									return false;
								}else{
									doPing(splitedData[0], splitedData[1]);	
								}
								
        					} else {
        						Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
    							return false;
        					}
        					
        				}
        	},this, false, '64, 3');
        }
        
        function doPing(packetSize, count) {
            $('#commandResult').val("Request MCU Server Ping....");
            $.getJSON('${ctx}/gadget/device/command/cmdMcuPing.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId
                    , 'packetSize' : packetSize
                    , 'count' : count
                    }
                    , function (returnData){
                        if(!returnData.status){
                            $('#commandResult').val("FAIL");
                               return;
                        }
                        if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                            $('#commandResult').val("MCU Ping: "+returnData.status);
                        }else{
                        	$('#commandResult').val(""); 
                            $('#commandResult').val(returnData.jsonString); 
                        }
                    });
        }
        
        function commandTraceroute() {
            $('#commandResult').val("Request MCU Traceroute....");
            $.getJSON('${ctx}/gadget/device/command/cmdMcuTraceroute.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(!returnData.status){
                            $('#commandResult').val("FAIL");
                               return;
                        }
                        if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                            $('#commandResult').val("MCU Traceroute: "+returnData.status);
                        }else{
                        	$('#commandResult').val(""); 
                            $('#commandResult').val(returnData.jsonString); 
                        }
                    });
        }
        
        //스케줄러 정보 가져오기
        function importSchedule() {
            $.getJSON('${ctx}/gadget/device/command/importSchedule.do'
                    , {'mcuId' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(returnData.status == "SUCCESS") {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',returnData.status);
                            $('#schedulForm').each(function() {
                                this.reset();
                            });
                            var k = 1;
                            for(var i = 31; i >= 1; i--) {
                                if((returnData.varMeterDayMask).substring(i-1, i) == '1') {
                                    $('#varMeterDayMask_' + k).attr('checked', true);
                                }
                                if((returnData.varEventReadDayMask).substring(i-1, i) == '1') {
                                    $('#varEventReadDayMask_' + k).attr('checked', true);
                                }
                                if((returnData.varMeterTimesyncDayMask).substring(i-1, i) == '1') {
                                    $('#varMeterTimesyncDayMask_' + k).attr('checked', true);
                                }
                                if((returnData.varRecoveryDayMask).substring(i-1, i) == '1') {
                                    $('#varRecoveryDayMask_' + k).attr('checked', true);
                                }
                                if(returnData.varMeterUploadCycleType == '2') {
                                    $('#varMeterUploadCycleDaily_' + k).attr('disabled', false);
                                    if((returnData.varMeterUploadCycleDaily).substring(i-1, i) == '1') {
                                        $('#varMeterUploadCycleDaily_' + k).attr('checked', true);
                                    }
                                }
                                k++;
                            }

                            k = 0;
                            for(var j = 23; j >= 0; j--) {
                                if((returnData.varMeterHourMask).substring(j, j+1) == '1') {
                                    $('#varMeterHourMask_' + k).attr('checked', true);
                                }
                                if((returnData.varEventReadHourMask).substring(j, j+1) == '1') {
                                    $('#varEventReadHourMask_' + k).attr('checked', true);
                                }
                                if((returnData.varMeterTimesyncHourMask).substring(j, j+1) == '1') {
                                    $('#varMeterTimesyncHourMask_' + k).attr('checked', true);
                                }
                                if(returnData.varMeterUploadCycleType == '4' || returnData.varMeterUploadCycleType == '0') {
                                    $('#varMeterUploadCycleHourly_' + k).attr('disabled', false);
                                    if((returnData.varMeterUploadCycleHourly).substring(j, j+1) == '1') {
                                        $('#varMeterUploadCycleHourly_' + k).attr('checked', true);
                                    }
                                }
                                if((returnData.varRecoveryHourMask).substring(j, j+1) == '1') {
                                    $('#varRecoveryHourMask_' + k).attr('checked', true);
                                }
                                k++;
                            }

                            if(returnData.varMeterUploadCycleType == '3') {
                                $('#varMeterUploadCycleWeekly_' + k).attr('disabled', false);
                                $('#varMeterUploadCycleWeekly_' + returnData.varMeterUploadCycleWeekly).attr('checked', true);
                            }

                            //검침스케줄
                            $('#varMeterStartMin').val(returnData.varMeterStartMin);
                            $('#varMeteringPeriod').val(returnData.varMeteringPeriod);
                            $('#varMeteringRetry').val(returnData.varMeteringRetry);

                            //검침 이벤트 로그 읽기 스케줄
                            $("#varEnableReadMeterEvent option[value=" + returnData.varEnableReadMeterEvent + "]").attr("selected", "selected");
                            $("#varEnableReadMeterEvent").selectbox();

                            //미터 시간 동기화 스케줄
                            //검침 데이터 업로드
                            $("#varMeterUploadCycleType option[value=" + returnData.varMeterUploadCycleType + "]").attr("selected", "selected");
                            $("#varMeterUploadCycleType").selectbox();
                            $("#varEnableAutoUpload option[value=" + returnData.varEnableAutoUpload + "]").attr("selected", "selected");
                            $("#varEnableAutoUpload").selectbox();

                            $('#varMeterUploadStartHour').val(returnData.varMeterUploadStartHour);
                            $('#varMeterUploadStartMin').val(returnData.varMeterUploadStartMin);
                            $('#varMeterUploadTryTime').val(returnData.varUploadTryTime);
                            $('#varMeterUploadRetry').val(returnData.varMeterUploadRetry);


                            //검침실패 데이터 복구 스케줄
                            $("#varEnableRecovery option[value=" + returnData.varEnableRecovery + "]").attr("selected", "selected");
                            $('#varRecoveryStartMin').val(returnData.varRecoveryStartMin);
                            $('#varRecoveryPeriod').val(returnData.varRecoveryPeriod);
                            $('#varRecoveryRetry').val(returnData.varMeteringRetry);
                            $("#varEnableRecovery").selectbox();
                        } else {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',returnData.status);
                        }


                        //0: unknown 1: Immediately(즉시), 2: Daily 3: Weekly 4: hourly
                        if($("#varMeterUploadCycleType").val() == 4 || $("#varMeterUploadCycleType").val() == 0) {
                            $("#upload_weekTable").hide();
                            $("#upload_dateTable").hide();
                            $("#upload_timeTable").show();
                            $("#uploadStartHour").hide();
                            $("#uploadStartMin").show();
                       } else if($("#varMeterUploadCycleType").val() == 3) {
                            $("#upload_dateTable").hide();
                            $("#upload_timeTable").hide();
                            $("#upload_weekTable").show();
                            $("#uploadStartHour").show();
                            $("#uploadStartMin").show();
                       } else if($("#varMeterUploadCycleType").val() == 2) {
                            $("#upload_weekTable").hide();
                            $("#upload_timeTable").hide();
                            $("#upload_dateTable").show();
                            $("#uploadStartHour").show();
                            $("#uploadStartMin").show();
                       } else if($("#varMeterUploadCycleType").val() == 1) {
                            $("#upload_weekTable").hide();
                            $("#upload_dateTable").hide();
                            $("#upload_timeTable").hide();
                            $("#uploadStartHour").hide();
                            $("#uploadStartMin").hide();
                       }
                    });

        }

        function KiloForamt(str)
        {
            if(str=="")
                return str
            return Math.round(str / 1024 * 10)/10 + "KB";
        }
        function MegaForamt(str)
        {
            if(str=="")
                return str
            return Math.round(str / 1024 / 1000 * 10) / 10 + "MB";
        }
        function TempFormat(str)
        {
            return str.substring(0,2)+"."+str.substring(2);
        }
        function DateType(date)
        {
          if(date.length==8)
          {
            var year=date.substring(0,4);
            var month=date.substring(4,6);
            var day=date.substring(6,8);
            return year+"-"+month+"-"+day;
          }else if(date.length==14)
          {
            var year=date.substring(0,4);
            var month=date.substring(4,6);
            var day=date.substring(6,8);
            var hour=date.substring(8,10);
            var min=date.substring(10,12);
            var sec=date.substring(12,14);
            return year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
          }else{
            return date;
          }
        }

        function reset() {
           // Form Reset
           var $searchForm = $("form[name=search]");
           $searchForm.trigger("reset");

           //hidden 값 Reset
          $("#sInstallDateStartHidden").val("");
          $("#sInstallDateEndHidden").val("");
          $("#sLocationId").val("");

           // 셀렉트 태그 첫번째 인덱스 선택
           var $selects = $searchForm.find("select");
           $selects.each(function() {
               $(this).selectbox();
           });

           return;
        }

        //report window(Excel)
        var winMcuList;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            obj.supplierId          = supplierId;
            obj.mcuId               = document.getElementById("sMcuId").value;
            obj.mcuType             = document.getElementById("sMcuType").value;
            obj.locationId          = document.getElementById("sLocationId").value;
            obj.swVersion           = document.getElementById("sSwVersion").value;
            obj.hwVersion           = document.getElementById("sHwVersion").value;
            obj.installDateStart    = document.getElementById("sInstallDateStart").value;
            obj.installDateEnd      = document.getElementById("sInstallDateEnd").value;
            obj.filter              = document.getElementById("filter").value;
            obj.order               = document.getElementById("order").value;
            obj.protocol            = document.getElementById("protocol").value;
            obj.mcuStatus			= document.getElementById("sMcuStatus").value;
            obj.title               ="<fmt:message key='aimir.excel.concentrator'/>";

            if(winMcuList)
                winMcuList.close();
            winMcuList = window.open("${ctx}/gadget/device/mcuMaxExcelDownloadPopup.do", "MCUListExce;", opts);
            winMcuList.opener.obj = obj;
        }

        //report window(Excel)
        var winMcuCommLog;
        function openCommLogExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            obj.supplierId = supplierId;
            obj.senderId   = mcuName;
            obj.startDate  = $("#communicationLogStartDateHidden").val();
            obj.endDate    = $("#communicationLogEndDateHidden").val();
            obj.headerMsg  = getFmtMessage2();

            if(winMcuCommLog)
                winMcuCommLog.close();
            winMcuCommLog = window.open("${ctx}/gadget/device/mcuCommLogExcelDownloadPopup.do", "McuCommLogExcel", opts);
            winMcuCommLog.opener.obj = obj;
        }

        //report window(Excel)
        var winMcuEventAlertLogExcel;
        function openEventAlertLogExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            obj.supplierId = supplierId;
            obj.mcuId      = mcuName;
            obj.startDate  = $("#brokenLogStartDateHidden").val();
            obj.endDate    = $("#brokenLogEndDateHidden").val();
            obj.headerMsg  = getFmtMessage4(); 

            if(winMcuEventAlertLogExcel)
                winMcuEventAlertLogExcel.close();
            winMcuEventAlertLogExcel = window.open("${ctx}/gadget/device/mcuAlertLogExcelDownloadPopup.do", "McuEventAlertLogExcel", opts);
            winMcuEventAlertLogExcel.opener.obj = obj;
        }

        //report window(Excel)
        var winMcuOperationLog;
        function openOperationLogExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            obj.supplierId = supplierId;
            obj.targetName = mcuName;
            obj.startDate  = $("#commandLogStartDateHidden").val();
            obj.endDate    = $("#commandLogEndDateHidden").val();
            obj.headerMsg  = getFmtMessage5();

            if(winMcuOperationLog)
                winMcuOperationLog.close();
            winMcuOperationLog = window.open("${ctx}/gadget/device/mcuOperLogExcelDownloadPopup.do", "McuOperationLogExcel", opts);
            winMcuOperationLog.opener.obj = obj;
        }

        /* 집중기 리스트 */
        var dcuStore;
        var dcuGridOn = false;
        var dcuGrid;
        var dcuColModel;
        var getDcuList = function() {
            var width = $("#dcuGridDiv").width();
            var pageSize = 5;
            var conditionArray = getConditionArray();
            var fmtMessage = getFmtMessage();
            emergePre();
            dcuStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getDcuGridData.do",
                baseParams: {
                    supplierId : conditionArray[12],
                    mcuId : conditionArray[0],
                    mcuType : conditionArray[1],
                    locationId : conditionArray[2],
                    swVersion : conditionArray[3],
                    hwVersion : conditionArray[4],
                    installDateStart : conditionArray[5],
                    installDateEnd : conditionArray[6],
                    //page = pageNav.curPage + '';
                    filter : conditionArray[8],
                    order : conditionArray[9],
                    protocol : conditionArray[10],
                    //pageSize = pageNav.pageSize + "";
                    dummy : conditionArray[11],
                	mcuStatus : conditionArray[13]
                },
                totalProperty: 'totalCount',
                root:'result',
                idProperty : 'rowNo',
                fields: ["rowNo", "sysID", "sysName", "sysPhoneNumber", "ipAddr", "sysSwVersion", "installDate", "lastCommDate"
                         , "commState", "mcuId", "mcuTypeName", "dcuType", "vendor", "model", "location", "sysHwVersion", "protocolType"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    },
                    load: function(store, record, options){
                        /* if (record.length > 0) {
                            // 데이터 load 후 첫번째 row 자동 선택
                            dcuGrid.getSelectionModel().selectFirstRow();
                        } else {
                            $("#mcuInfoDiv").hide();
                        } */
                        if (record.length <= 0) {
                            $("#mcuInfoDiv").hide();
                        }
                    }
                }
            });

            dcuColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<font style='font-weight: bold;'>" + fmtMessage[0] + "</font>", dataIndex: 'rowNo', renderer: addTooltip, tooltip: fmtMessage[0], align: "center", width: 35}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[10] + "</font>", dataIndex: 'dcuType', renderer: addTooltip, tooltip: fmtMessage[10], width: ((width-35)/14) * 0.9}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[1] + "</font>", dataIndex: 'sysID', renderer: addTooltip, tooltip: fmtMessage[1], width: ((width-35)/14) * 0.9,
                  		renderer: function(value, me, record, rowNumber, columnIndex, store){
                  			if(record.data.commState == "fmtMessage00")
                  				return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                  			else if(record.data.commState == "fmtMessage24")
                  				return setColorFrontTag + gold + setColorMiddleTag + value + setColorBackTag;
                  			else if(record.data.commState == "fmtMessage48")
                  				return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
                  			else
                  				return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                  		}
                   }
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'sysName', renderer: addTooltip, tooltip: fmtMessage[2], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[11] + "</font>", dataIndex: 'vendor', renderer: addTooltip, tooltip: fmtMessage[11], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[12] + "</font>", dataIndex: 'model', renderer: addTooltip, tooltip: fmtMessage[12], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[3] + "</font>", dataIndex: 'sysPhoneNumber', renderer: addTooltip, tooltip: fmtMessage[3], width: (width-35)/14, hidden: true}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[15] + "</font>", dataIndex: 'location', renderer: addTooltip, tooltip: fmtMessage[15], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[4] + "</font>", dataIndex: 'ipAddr', renderer: addTooltip, tooltip: fmtMessage[4], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[5] + "</font>", dataIndex: 'sysSwVersion', renderer: addTooltip, tooltip: fmtMessage[5], width: ((width-35)/14) * 0.8}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[13] + "</font>", dataIndex: 'sysHwVersion', renderer: addTooltip, tooltip: fmtMessage[13], width: ((width-35)/14) * 0.8}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[7] + "</font>", dataIndex: 'installDate', renderer: addTooltip, tooltip: fmtMessage[7], width: ((width-35)/14) * 1.4}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[8] + "</font>", dataIndex: 'lastCommDate', renderer: addTooltip, tooltip: fmtMessage[8], width: ((width-35)/14) * 1.4}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[14] + "</font>", dataIndex: 'protocolType', renderer: addTooltip, tooltip: fmtMessage[14], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[9] + "</font>", dataIndex: 'commState', tooltip: fmtMessage[9], width: (((width-35)/14) * 0.8),
                	   renderer: function(value, metaData) {
                           if (value == "fmtMessage00") {
                               return setColorFrontTag + blue + setColorMiddleTag + fmtMessage[16] + setColorBackTag;
                           } else if (value == "fmtMessage24") {
                               return setColorFrontTag + gold + setColorMiddleTag + fmtMessage[19] + setColorBackTag;
                           } else if (value == "fmtMessage48") {
                               return setColorFrontTag + orange + setColorMiddleTag + fmtMessage[20] + setColorBackTag;
                           } else {
                               return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                           }
                       }
                   }
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if(dcuGridOn == false) {
                dcuGrid = new Ext.grid.GridPanel({
                    store: dcuStore,
                    colModel : dcuColModel,
                    sm: new Ext.grid.RowSelectionModel({
                        singleSelect:true,
                        listeners: {
                            rowselect: function(sm, row, rec) {
                                var data = rec.data;
                                setMcuName(data.sysID, data.mcuTypeName);
                                drawGeneralDivTab(data.mcuId);
                            }
                        }
                    }),
                    autoScroll:false,
                    width: width,
                    height: 172,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'dcuGridDiv',
                    viewConfig: {
                        forceFit: true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: dcuStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                dcuGridOn = true;
            } else {
                dcuGrid.setWidth(width);
                var bottomToolbar = dcuGrid.getBottomToolbar();
                dcuGrid.reconfigure(dcuStore, dcuColModel);
                bottomToolbar.bindStore(dcuStore);
            }
            hide();
        };

        /* Connected Device Info 리스트 */
        var connectedGridOn = false;
        var connectedGrid;
        var connectedColModel;
        var getConnectedDeviceList = function() {
            var width = $("#connectedDeviceGridDiv").width();
            var pageSize = 5;
            var fmtMessage = getFmtMessage1();
            emergePre();
            var connectedStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getConnectedDeviceList.do",
                baseParams: {
                    mcuId : mcuId
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["rowNo", "deviceSerial", "modemType"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            connectedColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<font style='font-weight: bold;'>" + fmtMessage[0] + "</font>", dataIndex: 'rowNo', align: "center", width: 50}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[1] + "</font>", dataIndex: 'deviceSerial', width: (width-50)/2}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'modemType', width: ((width-50)/2) - 6}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if(connectedGridOn == false) {
                connectedGrid = new Ext.grid.GridPanel({
                    store: connectedStore,
                    colModel : connectedColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 172,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'connectedDeviceGridDiv',
                    viewConfig: {
                        //forceFit: true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: connectedStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                connectedGridOn = true;
            } else {
                connectedGrid.setWidth(width);
                var bottomToolbar = connectedGrid.getBottomToolbar();
                connectedGrid.reconfigure(connectedStore, connectedColModel);
                bottomToolbar.bindStore(connectedStore);
            }
            hide();
        };

        /* Communication Log 리스트 */
        var commLogGridOn = false;
        var commLogGrid;
        var commLogColModel;
        var getCommunicationLogList = function() {
            var width = $("#commLogGridDiv").width();
            var pageSize = 5;
            var fmtMessage = getFmtMessage2();
            emergePre();

            var commLogStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getCommLogList.do",
                baseParams: {
                    supplierId : supplierId,
                    senderId : mcuName,
                    startDate : $("#communicationLogStartDateHidden").val(),
                    endDate : $("#communicationLogEndDateHidden").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["rowNo", "svcTypeCode", "sendBytes", "rcvBytes", "totalBytes", "totalCommTime"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            commLogColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<font style='font-weight: bold;'>" + fmtMessage[0] + "</font>", dataIndex: 'rowNo', align: "center", width: 50}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[1] + "</font>", dataIndex: 'svcTypeCode', width: (width-50)/5}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'sendBytes', align: "right", width: (width-50)/5}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[3] + "</font>", dataIndex: 'rcvBytes', align: "right", width: (width-50)/5}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[4] + "</font>", dataIndex: 'totalBytes', align: "right", width: (width-50)/5}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[5] + "</font>", dataIndex: 'totalCommTime', align: "right", width: ((width-50)/5) - 7}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if(commLogGridOn == false) {
                commLogGrid = new Ext.grid.GridPanel({
                    store: commLogStore,
                    colModel : commLogColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 172,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'commLogGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: commLogStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                commLogGridOn = true;
            } else {
                commLogGrid.setWidth(width);
                var bottomToolbar = commLogGrid.getBottomToolbar();
                commLogGrid.reconfigure(commLogStore, commLogColModel);
                bottomToolbar.bindStore(commLogStore);
            }
            hide();
        };

        /* Event Alert Log 리스트 */
        var eventAlertLogGridOn = false;
        var eventAlertLogGrid;
        var eventAlertLogColModel;
        var getEventAlertLogList = function() {
            var width = $("#eventAlertLogGridDiv").width();
            var pageSize = 5;
            var fmtMessage = getFmtMessage4();
            emergePre();

            var eventAlertLogStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getEventAlertLogList.do",
                baseParams: {
                    supplierId : supplierId,
                    mcuId : mcuName,
                    startDate : $("#brokenLogStartDateHidden").val(),
                    endDate : $("#brokenLogEndDateHidden").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["rowNo", "severity", "message", "locationName", "openTime", "closeTime", "duration"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            eventAlertLogColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<font style='font-weight: bold;'>" + fmtMessage[0] + "</font>", dataIndex: 'rowNo', align: "center", width: 50}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[1] + "</font>", dataIndex: 'severity', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'message', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[3] + "</font>", dataIndex: 'locationName', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[4] + "</font>", dataIndex: 'openTime', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[5] + "</font>", dataIndex: 'closeTime', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[6] + "</font>", dataIndex: 'duration', align: "right", width: ((width-50)/6) - 6}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if(eventAlertLogGridOn == false) {
                eventAlertLogGrid = new Ext.grid.GridPanel({
                    store: eventAlertLogStore,
                    colModel : eventAlertLogColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 172,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'eventAlertLogGridDiv',
                    viewConfig: {
                        //forceFit: true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: eventAlertLogStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                eventAlertLogGridOn = true;
            } else {
                eventAlertLogGrid.setWidth(width);
                var bottomToolbar = eventAlertLogGrid.getBottomToolbar();
                eventAlertLogGrid.reconfigure(eventAlertLogStore, eventAlertLogColModel);
                bottomToolbar.bindStore(eventAlertLogStore);
            }
            hide();
        };

        /* Operation Log 리스트 */
        var operationLogGridOn = false;
        var operationLogGrid;
        var operationLogColModel;
        var getOperationLogList = function() {
            var width = $("#operationLogGridDiv").width();
            var pageSize = 5;
            var fmtMessage = getFmtMessage5();
            emergePre();

            var operationLogStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getOperationLogList.do",
                baseParams: {
                    supplierId : supplierId,
                    targetName : mcuName,
                    startDate : $("#commandLogStartDateHidden").val(),
                    endDate : $("#commandLogEndDateHidden").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["rowNo", "yyyymmddhhmmss", "targetTypeCode", "operatorType", "userId", "targetName", "description"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            operationLogColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<font style='font-weight: bold;'>" + fmtMessage[0] + "</font>", dataIndex: 'rowNo', align: "center", width: 50}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[1] + "</font>", dataIndex: 'yyyymmddhhmmss', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'targetTypeCode', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[3] + "</font>", dataIndex: 'operatorType', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[4] + "</font>", dataIndex: 'userId', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[5] + "</font>", dataIndex: 'targetName', width: (width-50)/6}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[6] + "</font>", dataIndex: 'description', width: ((width-50)/6) - 6}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if(operationLogGridOn == false) {
                operationLogGrid = new Ext.grid.GridPanel({
                    store: operationLogStore,
                    colModel : operationLogColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 172,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'operationLogGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: operationLogStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                operationLogGridOn = true;
            } else {
                operationLogGrid.setWidth(width);
                var bottomToolbar = operationLogGrid.getBottomToolbar();
                operationLogGrid.reconfigure(operationLogStore, operationLogColModel);
                bottomToolbar.bindStore(operationLogStore);
            }
            hide();
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        // 입력한 key 가 number인지 체크
        function inputOnlyNumberType(ev, src) {
            if (isNaN(src.val())) {
                src.val("");
                return;
            }
            var evCode = (window.netscape) ? ev.which : event.keyCode;

            // Allow: backspace(8), delete(46), tab(9), escape(27), ←(37), →(39), enter(13) and dot(190)
            if (evCode == 8 || evCode == 9 || evCode == 13 || evCode == 27
                    || evCode == 37 || evCode == 39 || evCode == 46) {
                // let it happen, don't do anything
            } else if (evCode == 190) {
                if (src.val() == "" || src.val().indexOf(".") != -1) {
                    ev.preventDefault();
                }
            } else {
                // Ensure that it is a number and stop the keypress
                if (evCode < 48 || evCode > 57) {
                    ev.preventDefault();
                }
            }
        }
    </script>
    
</head>

<body onLoad="init();">
    <!--상단검색-->
    <form name="search">
    <em class="am_button margin-t2px" style="position: absolute; right: 13px; margin-top: 13px">
        <a href="javascript:reset();"><fmt:message key="aimir.form.reset"/></a>
    </em>
    <div class="wfree border-bottom padding10px">
        <table class="searching">
        <tr>
            <td class="withinput" style="width: 65px"><fmt:message key="aimir.mcutype"/></td>
            <td class="padding-r20px">
                <select id="sMcuType" name="select" style="width:100px;" ">
                <option value=""><fmt:message key="aimir.all"/></option>
                <c:forEach var="mcuType" items="${mcuTypeMap}">
                <option value="${mcuType.id}">${mcuType.descr}</option>
                </c:forEach>
                </select>
            </td>
            <td class="withinput" style="width: 80px"><fmt:message key="aimir.mcuid"/></td>
            <td>
                <input name="customer_num" id="sMcuId" type="text" style="width:140px;">
            </td>
            <td class="withinput"><fmt:message key="aimir.sw.version"/></td>
            <td  class="padding-r20px">
                <select id="sSwVersion" name="select" style="width:65px;">
                <option value=""><fmt:message key="aimir.all"/></option>
                <c:forEach var="swVersion" items="${swVersions}">
                <option value="${swVersion.name}">${swVersion.descr}</option>
                </c:forEach>
                </select>
            </td>
            <td class="withinput"><fmt:message key="aimir.fw.hwversion"/></td>
            <td class="padding-r20px">
                <select id="sHwVersion" name="select" style="width:65px;">
                <option value=""><fmt:message key="aimir.all"/></option>
                <c:forEach var="hwVersion" items="${hwVersions}">
                <option value="${hwVersion.name}">${hwVersion.descr}</option>
                </c:forEach>
                </select>
            </td>
            <td class="withinput"><fmt:message key="aimir.installationdate"/></td>
            <td id="search-date">
            <input id="sInstallDateStart" name="customer_num" type="text" class="day">
            </td>
            <td><input class="between" value="~" type="text"></td>
            <td id="search-date">
                <input id="sInstallDateEnd" name="customer_num" type="text" class="day">
                <input id="sInstallDateStartHidden" type="hidden" />
                <input id="sInstallDateEndHidden" type="hidden" />
            </td>
        </tr>
        <tr>
            <td class="withinput" style="width: 80px"><fmt:message key="aimir.view.mcu39"/><!-- 프로토콜 타입 --></td>
            <td  class="padding-r20px">
                <select id="protocol" name="select" style="width:100px;">
                <option value=""><fmt:message key="aimir.all"/></option>
                <c:forEach var="protocol" items="${protocols}">
                <option value="${protocol.id}">${protocol.descr}</option>
                </c:forEach>
                </select>
            </td>
            <td class="withinput"><fmt:message key="aimir.location"/></td>
            <td class="padding-r20px">
                <input type="text" id="searchWord" name="searchWord" style="width:140px" />
                <input type="hidden" id="sLocationId" name="location.id" value="" />
            </td>
            <td class="withinput"><fmt:message key="aimir.status"/></td>
            <td class="padding-r20px">
                <select id="sMcuStatus" name="select" style="width:65px;">
                <option value=""><fmt:message key="aimir.all"/></option>
                <c:forEach var="mcuStatus" items="${mcuStatus}">
                <option value="${mcuStatus.id}">${mcuStatus.descr}</option>
                </c:forEach>
                </select>
            </td>
            <td colspan="2">
                <em class="am_button margin-t2px"><a href="javascript:searchList();"><fmt:message key="aimir.button.search" /></a></em>&nbsp;
            </td>
        </tr>
        </table>
        <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
            <div id="treeDivA"></div>
        </div>
    </div>
    </form>
    <!--상단검색 끝-->

    <div class="btn_left_top2 margin-t10px">
        <span class="withinput">
            <fmt:message key="aimir.filtering" />
        </span>
        <span>
            <!-- name 추가해줘야 할부분 -->
            <select id="filter" style="width:170px" onchange="javascript:searchList();">
            <option value=""><fmt:message key="aimir.all"/></option>
            <option value="normal"><fmt:message key="aimir.commstateGreen"/></option>
            <option value="commStateYellow"><fmt:message key="aimir.commstateYellow"/></option>
            <option value="commStateRed"><fmt:message key="aimir.commstateRed"/></option>
            </select>
        </span>
        <span>
            <select id="order" style="width:240px" onchange="javascript:searchList();">
            <!-- name 추가해줘야 할부분 -->
            <option value="lastCommDesc"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.desc"/></option>
            <option value="lastCommAsc"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.asc"/></option>
            <option value="installDateDesc"><fmt:message key="aimir.installationdate"/> <fmt:message key="aimir.search.desc"/></option>
            <option value="installDateAsc"><fmt:message key="aimir.installationdate"/> <fmt:message key="aimir.search.asc"/></option>
            </select>
       </span>

    </div>

    <div id="btn" class="btn_right_top2 margin-t10px">
        <ul id="mcuAddBtn"><li><a href="JavaScript:mcuInsertDiv();" class="on"><fmt:message key="aimir.add"/></a></li></ul>
        <!-- <ul><li><a href="JavaScript:mcuUpdateDiv();" class="on"><fmt:message key="aimir.update"/></a></li></ul>  -->
        <ul id="mcuDelBtn"><li><a href="JavaScript:mcuDelete();" class="on"><fmt:message key="aimir.button.delete"/></a></li></ul>
        <ul><li><a href="javaScript:openExcelReport();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
    </div>

    <div id="dcuGridDiv" class="gadget_body2"></div>

    <div id="mcuInsertDiv" class="width_auto margin10px" style="display:none;" >
        <div id="singleRegTabs" >

            <form id="mcuInsertForm" name="mcuInsertForm" >
            <div class="border_blu padding20px">


                <table class="search">
                    <li style="position: absolute; right: 0; padding-right: 20px"><fmt:message key="aimir.hems.inform.requiredField"/></li>
                    <tr>
                        <th><fmt:message key="aimir.mcutype"/><font color="red">*</font></th>
                        <td><select id="singleRegMCUmcuType" name="sysType"></select></td>
                        <th><fmt:message key="aimir.mcuid"/><font color="red">*</font></th>
                        <th><input type="text" id="singleRegMCUmcuId" name="sysID"/></th>
                        <td><em class="am_button"><a onclick="javascript:singleRegMCUIsMCUDuplicate();"><fmt:message key="aimir.checkDuplication"/></a></em></td>
                        <th><fmt:message key="aimir.mcu.name"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuName" name="sysName"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="aimir.view.mcu39"/><font color="red">*</font></th>
                        <td><select id="singleRegMCUmcuCommType" name="protocolType.id"></select></td>
                        <th><fmt:message key="aimir.ipaddress"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuIpAddr" name="ipAddr"/></td>
                        <th><fmt:message key="aimir.portnumber"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuLocalPort" name="sysLocalPort"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="aimir.vendor"/><font color="red">*</font></th>
                        <td><select id="singleRegMCUmcuVendor" name="model.deviceVendor.id" onchange="javascript:getModelListByVendorMCU();"></select></td>
                        <th><fmt:message key="aimir.model"/><font color="red">*</font></th>
                        <td colspan="2"><select id="singleRegMCUmcuModel" name="model.id"></select></td>
                        <th><fmt:message key="aimir.mcumobile"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuMobile" name="sysPhoneNumber"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="aimir.fw.hwversion"/><font color="red">*</font></th>
                        <td><select id="singleRegMCUmcuHwVer" name="sysHwVersion"></select></td>
                        <th><fmt:message key="aimir.sw.version"/><font color="red">*</font></th>
                        <td colspan="2"><select id="singleRegMCUmcuSwVer" name="sysSwVersion"></select></td>
                        <th><fmt:message key="aimir.mcu.swrevision"/><font color="red">*</font></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuSwReVer" name="sysSwRevision"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="aimir.latitude"/></th>
                        <td><input type="text" id="singleRegMCUmcuLatitude" name="gpioX"/></td>
                        <th><fmt:message key="aimir.logitude"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuLogitude" name="gpioY"/></td>
                        <th><fmt:message key="aimir.altitude"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuAltitude" name="gpioZ"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="aimir.location"/><font color="red">*</font></th>
                        <td>
                            <input type="text" id="singleRegMCUmcuLoc" name="singleRegMCUmcuLoc"/>
                            <input type="hidden" id="locationIdMcu" name="location.id" value="" />
                        </td>
                        <th><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></th>
                        <td colspan="2"><input type="text" id="singleRegMCUmcuDetailLoc" name="sysLocation"/></td>
                        <th><fmt:message key="aimir.install.pic" /></th>
                        <th><input type="text" style="width:200px" id="singleRegMCUmcuPhoto"/></th>
                        <td><em class="am_button"><a href="#"><fmt:message key="aimir.button.search" /></a></em></td>
                    </tr>
                </table>
                <div id="treeDivMcuOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivMcu"></div>
                </div>
            </div>

            <div class="width-100 margin-t20px textalign-right" style="margin-top:5px">
                <em class="am_button"><a href="javascript:mcuInsert();" class="on"><fmt:message key="aimir.button.confirm"/></a></em>
                <em class="am_button"><a href="javascript:mcuInsertDiv();mcuInsertDivCancel()" class="on"><fmt:message key="aimir.cancel"/></a></em>
            </div>

            </form>

        </div>
    </div>


    <div id="mcuInfoDiv" class="width_auto margin10px" style="display:none;" >

     <!-- sub tab -->
     <div id="mcuDetailTabs" class="am_sub_tab">
      <ul>
        <li><a id="generalDivTabId"><fmt:message key="aimir.button.basicinfo"/></a></li>
        <li><a id="positionDivTabId"><fmt:message key="aimir.location.info"/></a></li>
   <!-- <li><a id="scheduleDivTabId"><fmt:message key="aimir.schedule"/></a></li> -->
        <li><a id="logDivTabId"><fmt:message key="aimir.history"/></a></li>
   <!-- <li><a id="mcuCodiDivTabId"><fmt:message key="aimir.mcucodi"/></a></li>  -->
        <li><a id="signalToNoiseTabId"><fmt:message key="aimir.signaltonoise"/></a></li>
      </ul>
     </div>
     <!--// sub tab -->


     <div class="box_blu padding20px" >
        <!-- sub tab1 -->
        <div id="generalDivTab">
        <input type="hidden" id="imgPage" value="${paging.page}" />
        </div>
        <!-- // sub tab1 -->
        <!-- sub tab2-->
         <div id="positionDivTab">
            <div class="map_box">
                <div id="map-canvas" class="border-blue-3px"></div>
            </div>

            <div class="coordinate_box">
                <div class="width_auto padding10px"  style="display:table-cell; padding-top:7px !important; padding-bottom:7px !important;">
                  <table width="100%" class="searching2">
                    <caption><label class="check"><fmt:message key="aimir.location.info"/></label></caption>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.latitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioX" value="${gpioX}"/></td>
                        <td><fmt:message key="aimir.address"/></td>
                      </tr>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.logitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioY" value="${gpioY}"/></td>
                        <td rowspan="2" valign="bottom"><textarea name="customer_num3" id="sysLocationInfo" style="width:380px; height:50px;">${sysLocation}</textarea></td>
                      </tr>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.altitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioZ" value="${gpioZ}"/></td>
                      </tr>
                      <tr id="mcuLocBtnList">
                        <td colspan="2" align="right" class="padding-r25px">
                            <em class="am_button"><a href="javascript:updateMcuLoc()" class="on"><fmt:message key="aimir.mcu.coordinate.update"/></a></em>
                        </td>
                        <td align="right">
                            <em class="am_button"><a href="javascript:updateMcuAddress()" class="on"><fmt:message key="aimir.mcu.adress.update"/></a></em>
                            <em class="am_button"><a href="javascript:getGeoCoding();" class="on"><fmt:message key="aimir.address"/>-<fmt:message key="aimir.mcu.coordinate"/></a></em>
                        </td>
                      </tr>
                    </table>
                </div>

                <div  class="bottomside10px width_auto">
                  <ul>
                      <li class="margin-b5px" style="display:table-cell"><label class="check"><fmt:message key="aimir.mcu.device.connected"/></label></li>
                      <li><div id="connectedDeviceGridDiv"></div>
                      </li>
                  </ul>

                </div>

            </div>

        </div>

     <!-- // sub tab2 -->

    <!-- sub tab3 -->
    <form id="schedulForm">
    <div id="scheduleDivTab">
        <!-- 1.검침 스케줄-->
        <div class="width-auto">
            <table class="date_table">
            <caption><label class="check"><fmt:message key="aimir.meteringschedule"/></label></caption>
                <tr>
                    <th rowspan="2" width="100px"><fmt:message key="aimir.meteringdate"/></th>
                    <th><fmt:message key="aimir.all"/></th>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <th>${status.index}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterDayMask')"/></td>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <td><input type="checkbox" class="transonly" name="varMeterDayMask" id="varMeterDayMask_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>
        <br/>
        <div class="width-auto margin-t10px">
            <table class="time_table">
            <tr>
                <th rowspan="2" width="100px"><fmt:message key="aimir.meteringhour"/></th>
                <th><fmt:message key="aimir.all"/></th>
                <c:forEach begin="0" end="23" varStatus="status">
                <th>${status.index}</th>
                </c:forEach>
            </tr>
            <tr>
                <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterHourMask')" /></td>
                <c:forEach begin="0" end="23" varStatus="status">
                <td><input type="checkbox" class="transonly" name="varMeterHourMask" id="varMeterHourMask_${status.index}" /></td>
                </c:forEach>
            </tr>
            </table>
        </div>
        <div class="rightbox margin10px">
            <span class="graybold11pt margin-r20">&nbsp;</span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.startmin" /> : </span>
            <span><input type='text' id='varMeterStartMin' class="values greenbold_center">&nbsp;&nbsp;&nbsp; </span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.lpPeriod" /> : </span>
            <span><input type='text' id='varMeteringPeriod' class="values greenbold_center"></span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.minute" />&nbsp;&nbsp;&nbsp; </span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.retrycount" /> : </span>
            <span><input type='text' id='varMeteringRetry' class="values greenbold_center"></span>
        </div>
        <!--// 1.검침 스케줄-->

        <!-- 2.검침 이벤트 로그 읽기 스케줄-->
            <div class="width-auto margin-t10px">
                <span class="margin-t5px margin-r5">
                    <caption><label class="check"><fmt:message key="aimir.eventlogreadschedule"/></label></caption>
                </span>
                <span>
                    <select id='varEnableReadMeterEvent' style="width:100px">
                        <option value='true'><fmt:message key="aimir.enable2" /></option>
                        <option value='false'><fmt:message key="aimir.disable2" /></option>
                    </select>
               </span>
            </div>
            <br/>
            <div class="width-auto margin-t10px" style="clear:both;">
                <table class="date_table">

                    <tr>
                        <th rowspan="2" width="100px"><fmt:message key="aimir.meteringdate"/></th>
                        <th><fmt:message key="aimir.all"/></th>
                        <c:forEach begin="1" end="31" varStatus="status">
                        <th>${status.index}</th>
                        </c:forEach>
                    </tr>
                    <tr>
                        <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varEventReadDayMask')"/></td>
                        <c:forEach begin="1" end="31" varStatus="status">
                        <td><input type="checkbox" class="transonly" name="varEventReadDayMask" id="varEventReadDayMask_${status.index}" /></td>
                        </c:forEach>
                   </tr>
                </table>
            </div>

           <div class="width-auto margin-t10px">
                <table class="time_table">
                    <tr>
                        <th rowspan="2" width="100px"><fmt:message key="aimir.meteringhour"/></th>
                        <th><fmt:message key="aimir.all"/></th>
                        <c:forEach begin="0" end="23" varStatus="status">
                        <th>${status.index}</th>
                        </c:forEach>
                    </tr>
                    <tr>
                        <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varEventReadHourMask')" /></td>
                        <c:forEach begin="0" end="23" varStatus="status">
                        <td><input type="checkbox" class="transonly" name="varEventReadHourMask" id="varEventReadHourMask_${status.index}" /></td>
                        </c:forEach>
                    </tr>
                </table>
            </div>

        <!--// 2.검침 이벤트 로그 읽기 스케줄-->

        <!-- 3.검침 시간 동기화 스케줄-->
        <div  class="width-auto  margin-t20px">
            <table class="date_table">
            <caption><label class="check"><fmt:message key="aimir.timesyncschedule"/></label></caption>
                <tr>
                    <th rowspan="2" width="100px"><fmt:message key="aimir.meteringdate"/></th>
                    <th><fmt:message key="aimir.all"/></th>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <th>${status.index}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterTimesyncDayMask')"/></td>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <td><input type="checkbox" class="transonly" name="varMeterTimesyncDayMask" id="varMeterTimesyncDayMask_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>
        <br/>
        <div  class="width-auto margin-t10px">
            <table class="time_table">
                <tr>
                    <th rowspan="2" width="100px"><fmt:message key="aimir.meteringhour"/></th>
                    <th><fmt:message key="aimir.all"/></th>
                    <c:forEach begin="0" end="23" varStatus="status">
                    <th>${status.index}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterTimesyncHourMask')" /></td>
                    <c:forEach begin="0" end="23" varStatus="status">
                    <td><input type="checkbox" class="transonly" name="varMeterTimesyncHourMask" id="varMeterTimesyncHourMask_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>
         <!--// 3.검침 시간 동기화 스케줄-->

        <!-- 4.검침 데이터 업로드-->
        <div class="width-auto margin-t10px">
            <span class="margin-t5px margin-r5">
                <caption><label class="check"><fmt:message key="aimir.view.mcu18"/></label></caption>
            </span>
            <span>
                <select id='varEnableAutoUpload'>
                            <option value='true'><fmt:message key="aimir.enable2" /></option>
                            <option value='false'><fmt:message key="aimir.disable2" /></option>
                </select>
           </span>
        </div>
        <br/>
       <div class="width-auto margin-t10px" style="clear:both;" id="upload_weekTable">
            <table class="date_table">

                <tr>
                    <%-- <th rowspan="2" width="100px"><fmt:message key="aimir.meteringweek"/></th> --%>
                    <th rowspan="2" width="100px">Metering Week</th>
                    <th><fmt:message key = "aimir.day.sun"/></th>
                    <th><fmt:message key = "aimir.day.mon"/></th>
                    <th><fmt:message key = "aimir.day.tue"/></th>
                    <th><fmt:message key = "aimir.day.wed"/></th>
                    <th><fmt:message key = "aimir.day.thu"/></th>
                    <th><fmt:message key = "aimir.day.fri"/></th>
                    <th><fmt:message key = "aimir.day.sat"/></th>
                </tr>
                <tr>
                    <c:forEach begin="0" end="6" varStatus="status">
                    <td><input type="radio" class="transonly" name="varMeterUploadCycleWeekly" id="varMeterUploadCycleWeekly_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>

         <div class="width-auto margin-t10px" style="clear:both;" id="upload_dateTable">
            <table class="date_table">

                <tr>
                    <th rowspan="2" width="100px"><fmt:message key="aimir.meteringdate"/></th>
                    <th><fmt:message key="aimir.all"/></th>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <th>${status.index}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterUploadCycleDaily')"/></td>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <td><input type="checkbox" class="transonly" name="varMeterUploadCycleDaily" id="varMeterUploadCycleDaily_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>

        <div class="width-auto margin-t10px" style="clear:both;" id="upload_timeTable">

        <table class="time_table">

             <tr>
                 <th rowspan="2" width="100px"><fmt:message key="aimir.meteringhour"/></th>
                 <th><fmt:message key="aimir.all"/></th>
                 <c:forEach begin="0" end="23" varStatus="status">
                 <th>${status.index}</th>
                 </c:forEach>
             </tr>
             <tr>
                 <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varMeterUploadCycleHourly')" /></td>
                 <c:forEach begin="0" end="23" varStatus="status">
                 <td><input type="checkbox" class="transonly" name="varMeterUploadCycleHourly" id="varMeterUploadCycleHourly_${status.index}" /></td>
                 </c:forEach>
             </tr>
        </table>
        </div>
        <div class="rightbox margin10px">
            <span class="graybold11pt margin-r20"></span>
            <div id="uploadStartHour">
                <span class="margin-t5px margin-r5">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.starthour" /></span>
                <span><input type='text' id='varMeterUploadStartHour' class="values greenbold_center"></span>
            </div>
            <div id="uploadStartMin">
                <span class="margin-t5px margin-r5">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.startmin" /></span>
                <span><input type='text' id='varMeterUploadStartMin' class="values greenbold_center"></span>
            </div>
            <span class="margin-t5px margin-r5">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.uploadperiod" /></span>
            <span><input type='text' id='varMeterUploadTryTime' class="values greenbold_center"></span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.minute" />&nbsp;&nbsp;&nbsp; </span>
            <span class="margin-t5px margin-r5"> <fmt:message key="aimir.retrycount" /></span>
            <span><input type='text' id='varMeterUploadRetry' class="values greenbold_center"></span>

                <span class="graybold11pt margin-r20"><fmt:message key="aimir.lpPeriod" /></span>
                <span>
                    <select id='varMeterUploadCycleType' onChange="changeVarMeterUploadCycleType(this)">
                    <option value='0'><fmt:message key="aimir.unknown" /></option>
                    <option value='1'><fmt:message key="aimir.immediately" /></option>
                    <option value='2'><fmt:message key="aimir.daily" /></option>
                    <option value='3'><fmt:message key="aimir.weekly" /></option>
                    <option value='4'><fmt:message key="aimir.hourly" /></option>
                    </select>
                </span>
          </div>
        <!--// 4.검침 데이터 업로드-->
        <!--5.미검침 데이터 복구 스케줄-->

        <div class="width-auto margin-t10px">
                <span class="margin-t5px margin-r5">
                    <caption><label class="check"><fmt:message key="aimir.missingdatarecovery"/></label></caption>
                </span>
                <span>
                    <select id='varEnableRecovery'>
                        <option value='true'><fmt:message key="aimir.enable2" /></option>
                        <option value='false'><fmt:message key="aimir.disable2" /></option>
                    </select>
            </span>
        </div>
        <br/>
        <div class="width-auto margin-t10px" style="clear:both;">
            <table class="date_table">

                <tr>
                    <th rowspan="2" width="100px"><fmt:message key="aimir.meteringdate"/></th>
                    <th><fmt:message key="aimir.all"/></th>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <th>${status.index}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varRecoveryDayMask')"/></td>
                    <c:forEach begin="1" end="31" varStatus="status">
                    <td><input type="checkbox" class="transonly" name="varRecoveryDayMask" id="varRecoveryDayMask_${status.index}" /></td>
                    </c:forEach>
                </tr>
            </table>
        </div>
        <div class="width-auto  margin-t10px">
        <table class="time_table">
            <tr>
                <th rowspan="2" width="100px"><fmt:message key="aimir.meteringhour"/></th>
                <th><fmt:message key="aimir.all"/></th>
                <c:forEach begin="0" end="23" varStatus="status">
                <th>${status.index}</th>
                </c:forEach>
            </tr>
            <tr>
                <td><input type="checkbox" class="transonly" onClick="allCheck(this, 'varRecoveryHourMask')" /></td>
                <c:forEach begin="0" end="23" varStatus="status">
                <td><input type="checkbox" class="transonly" name="varRecoveryHourMask" id="varRecoveryHourMask_${status.index}" /></td>
                </c:forEach>
            </tr>
        </table>
        </div>
        <div class="rightbox margin10px">
            <span class="graybold11pt margin-r20"></span>

            <span class="margin-t5px margin-r5"><fmt:message key="aimir.startmin" /></span>
            <span><input type='text' id='varRecoveryStartMin' class="values greenbold_center"></span>
            <span class="margin-t5px margin-r5">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.lpPeriod" /></span>
            <span><input type='text' id='varRecoveryPeriod' class="values greenbold_center"></span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.minute" />&nbsp;&nbsp;&nbsp;</span>
            <span class="margin-t5px margin-r5"><fmt:message key="aimir.retrycount" /></span>
            <span><input type='text' id='varRecoveryRetry' class="values greenbold_center"></span>
         </div>
        <!--// 미검침 데이터 복구 스케줄-->
            <div id="mcuScheduleBtnList" class="padding-b10px textalign-center">
            <em class="big_button" style="margin-right: 5px"><a href="javascript:importSchedule()"><fmt:message key = "aimir.schedule"/> <fmt:message key = "aimir.locationUsage.search"/></a></em>
            <em class="big_button"><a href="#" onClick="saveSchedule()"><fmt:message key="aimir.save2"/></a></em>
            <!-- ul><li><a href="#" onClick="saveSchedule()" class="on"><fmt:message key="aimir.save2"/></a></li></ul-->
            </div>
        </div>
        </form>
        <!--// sub tab3 -->

        <!--sub tab4-->
        <div class="width-auto"  id="logDivTab">
        <!--통신로그 -->
            <div class="width-100">
                <label class="check"><fmt:message key="aimir.commlog"/></label>
                <div id="communcationLogDiv" class="width-auto margin-t10px" >
                    <span class="margin-t5px margin-r5"><fmt:message key="aimir.logdate"/></span>
                    <span><input id="communicationLogStartDate" class="day" type="text"></span>
                    <span><input value="~" class="between" type="text"></span>
                    <span class="margin-r5"><input id="communicationLogEndDate" class="day" type="text"></span>
                    <span class="margin-t2px am_button"><a href="javascript:communicationLogSearch()"><fmt:message key="aimir.button.search"/></a></span>
                    <input type="hidden" id="communicationLogStartDateHidden"/>
                    <input type="hidden" id="communicationLogEndDateHidden"/>
                </div>
                <div class="width-100 clear">
                    <ul>
                        <li class="floatleft margin-t10px">
                            <span class="blue11pt"><fmt:message key="aimir.sendbyte.total" /> :</span>
                            <span class="bluebold11pt" id="totalSend">0</span>
                            <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.sendbyte.max" /> : </span>
                            <span class="bluebold11pt" id="maxSend">0</span>
                            <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.sendbyte.min" /> : </span>
                            <span class="bluebold11pt" id="minSend">0</span>
                            <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.rcvbyte.total" /> : </span>
                            <span class="bluebold11pt" id="totalRcv">0</span>
                            <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.sendrcvbyte.max" /> : </span>
                            <span class="bluebold11pt" id="maxRcv">0</span>
                            <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.sendrcvbyte.min" /> : </span>
                            <span class="bluebold11pt" id="minRcv">0</span>
                        </li>
                        <li class="floatright margin-t5px padding-r2 margin-b3px">
                            <span class="am_button"><a href="javaScript:openCommLogExcelReport();"><fmt:message key="aimir.button.excel"/></a></span>
                        </li>
                    </ul>
                </div>
                <div id="commLogGridDiv" class="width-100 clear" style="margin-bottom: 10px;"></div>
            </div>

        <!--// 통신로그 -->
        <!--장애이력 -->
            <div class="width-100">
                <label class="check"><fmt:message key="aimir.alerthistory"/></label>
                <div id="brokenLogDiv" class="width-auto margin-t10px">
                    <ul>
                        <li class="floatleft margin-b3px">
                            <span class="margin-t5px margin-r5"><fmt:message key="aimir.logdate"/></span>
                            <span><input id="brokenLogStartDate" class="day" type="text"></span>
                            <span><input value="~" class="between" type="text"></span>
                            <span class="margin-r5"><input id="brokenLogEndDate" class="day" type="text"></span>
                            <span class="margin-t2px am_button"><a href="javascript:brokenLogSearch()"><fmt:message key="aimir.button.search"/></a></span>
                            <input type="hidden" id="brokenLogStartDateHidden"/>
                            <input type="hidden" id="brokenLogEndDateHidden"/>
                        </li>
                        <li class="floatright margin-t5px padding-r2 margin-b3px">
                            <span class="am_button"><a href="javaScript:openEventAlertLogExcelReport();"><fmt:message key="aimir.button.excel"/></a></span>
                        </li>
                    </ul>
                </div>
                <div id="eventAlertLogGridDiv" class="width-100 clear" style="margin-bottom: 10px;"></div>
            </div>
            <!--// 장애이력 -->
            <!--명령이력 -->
            <!-- 명령 이력이 임시로 제거됩니다.
            <div class="width-100">
                <label class="check"><fmt:message key="aimir.view.operationlog"/></label>
                <div id="commandLogDiv" class="width-auto margin-t10px">
                    <ul>
                        <li class="floatleft margin-b3px">
                            <span class="margin-t5px margin-r5"><fmt:message key="aimir.logdate"/></span>
                            <span><input id="commandLogStartDate" class="day" type="text"></span>
                            <span><input value="~" class="between" type="text"></span>
                            <span class="margin-r5"><input id="commandLogEndDate" class="day" type="text"></span>
                            <span class="margin-t2px am_button"><a href="javascript:commandLogSearch()"><fmt:message key="aimir.button.search"/></a></span>
                            <input type="hidden" id="commandLogStartDateHidden"/>
                            <input type="hidden" id="commandLogEndDateHidden"/>
                        </li>
                        <li class="floatright margin-t5px padding-r2 margin-b3px">
                            <span class="am_button"><a href="javaScript:openOperationLogExcelReport();"><fmt:message key="aimir.button.excel"/></a></span>
                        </li>
                    </ul>
                </div>
                <div id="operationLogGridDiv" class="width-100 clear"></div>
            </div>
        -->
        <!--//명령이력 -->
        </div>
        <!--// sub tab4-->
        <!--sub tab5-->
        <div  id="mcuCodiDivTab">
        <table class="info">
        <colgroup>
        <col width="15%"/>
        <col width=""/>
        <col width="15%"/>
        <col width=""/>
        <col width="15%"/>
        <col width=""/>
        </colgroup>
        <caption><label class="check"><fmt:message key="aimir.setting.info" /></label></caption>
            <tr class="topline">
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucodi.id" /></th>
                <td class="padding-r20px"><input type="text" id="codiID" class="border-trans gray11pt" readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucodi.index" /></th>
                <td class="padding-r20px" ><input type="text" id="codiIndex" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucodi.type" /></th>
                <td><input type="text" id="codiType" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucodi.shortid" /></th>
                <td class="padding-r20px"><input type="text" id="codiShortID" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucode.fmversion" /></th>
                <td class="padding-r20px"><input type="text" id="codiFwVer" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.fw.hwversion" /></th>
                <td class="min90pxwidth-130"><input type="text" id="codiHwVer" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucode.fmbuild" /></th>
                <td class="padding-r20px"><input type="text" id="codiFwBuild" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucode.zzifversion" /></th>
                <td class="padding-r20px"><input type="text" id="codiZAIfVer" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucode.zaifversion" /></th>
                <td><input type="text" id="codiZZIfVer" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucode.resetkind" /></th>
                <td class="padding-r20px"><input type="text" id="codiResetKind" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.mcucodi.autoset" /></th>
                <td class="padding-r20px"><input type="text" id="codiAutoSetting" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.channelid" /></th>
                <td><input type="text" id="codiChannel" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.panid" /> </th>
                <td class="padding-r20px"><input type="text" id="codiPanID" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.panid.ext" /></th>
                <td class="padding-r20px"><input type="text" id="codiExtPanId" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.permission" /></th>
                <td><input type="text" id="codiPermit" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.rfpower" /></th>
                <td class="padding-r20px"><input type="text" id="codiRfPower" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.encrypt.enable" /></th>
                <td class="padding-r20px"><input type="text" id="codiEnableEncrypt" class="border-trans gray11pt"  readonly/></td>
                <th class="withinput titlewidth"><fmt:message key="aimir.link.key" /></th>
                <td><input type="text" id="codiLinkKey" class="border-trans gray11pt"  readonly/></td>
            </tr>
            <tr>
                <th class="withinput titlewidth"><fmt:message key="aimir.network.key" /></th>
                <td class="padding-r20px"><input type="text" id="codiNetworkKey" class="border-trans gray11pt" readonly /></td>
                <th class="withinput titlewidth"></th>
                <td class="padding-r20px"></td>
                <th class="withinput titlewidth"></th>
                <td>&nbsp;</td>
            </tr>
        </table>
        </div>
        <!--// sub tab5-->
        <!-- sub tab6 -->
        <div class="width-auto"  id="signalToNoiseTab">
       	    <div class="width-100 margin-t10px margin-r5">        			
       			<input type="checkbox" class="transonly" id="LatestDateMask1" onchange="javascript:connectedDeviceSearch();"/>
       			<span id="LatestDateMask1Text" class="blue11pt">Display the last data of every modem at [dcu name]</span>        			
       		</div>
       		<br>
       		<div class="width-100 margin-t10px margin-r5" style="display: none;">        			
       			<input type="checkbox" class="transonly" id="PoorSignalMask" />
       			<span class="blue11pt">Poor Signal Only (-2 dB under)</span>        			
       		</div>
       		<br>
       		<div id="connectedDeviceDiv" class="width-auto margin-t10px">        			
       			<span class="margin-t2px margin-r5"><fmt:message key="aimir.period"/></span>
       			<span><input id="connectedDeviceStartDate" class="day" type="text" readonly="readonly"></span>
      			<span class="margin-l5"><select id="connectedDeviceStarthour" class="border_blu" style="width:40px;"></select></span>
            	<span><input value="~" class="between" type="text"></span>
            	<span><input id="connectedDeviceEndDate" class="day" type="text" readonly="readonly"></span>
            	<span class="margin-l5"><select id="connectedDeviceEndhour" class="border_blu margin-r5" style="width:40px;"></select></span>                                        
            	<span class="am_button margin-r5"><a href="javascript:connectedDeviceSearch()"><fmt:message key="aimir.button.search"/></a></span>
        <!-- 	<span class="am_button margin-r5"><a href="javascript:connectedDeviceExcel()"><fmt:message key="aimir.button.excel"/></a></span>   -->
        		<input type="hidden" id="connectedDeviceStartDateHidden"/>
                <input type="hidden" id="connectedDeviceEndDateHidden"/>
       		</div>
       		<br><br><br>
       		<label class="check margin-t2px"><fmt:message key="aimir.mcu.device.connected"/></label>
       		<div id="connectedDeviceGrid" class="width-auto margin-t10px">
       		-
       		</div>
       		
       		<div id="noiseDetailsDiv" class="margin-t10px border_blu padding-t7px padding-left3px">
       			<label class="check"><fmt:message key="aimir.view.detail"/></label>
       			<div class="margin-t10px margin-r5">        			
	       			<input type="checkbox" class="transonly" id="LatestDateMask2" onchange="javascript:noiseDetailsSearch();"/>
	       			<span id="LatestDateMask2Text" class="blue11pt">Display the last data of [modemID]</span>
	       			<br>
		       			<div id="noiseDetailsDate" class="width-auto margin-t10px">        			
		       			<span class="margin-t2px margin-r5"><fmt:message key="aimir.period"/></span>
		       			<span><input id="noiseDetailsStartDate" class="day" type="text" readonly="readonly"></span>
		      			<span class="margin-l5"><select id="noiseDetailsStarthour" class="border_blu" style="width:40px;"></select></span>
		            	<span><input value="~" class="between" type="text"></span>
		            	<span><input id="noiseDetailsEndDate" class="day" type="text" readonly="readonly"></span>
		            	<span class="margin-l5"><select id="noiseDetailsEndhour" class="border_blu margin-r5" style="width:40px;"></select></span>                                        
		            	<span class="am_button margin-r5"><a href="javascript:noiseDetailsSearch()"><fmt:message key="aimir.button.search"/></a></span>
		         <!--  	<span class="am_button margin-r5"><a href="javascript:noiseDetailsExcel()"><fmt:message key="aimir.button.excel"/></a></span>   -->
		       			<input type="hidden" id="noiseDetailsStartDateHidden"/>
                		<input type="hidden" id="noiseDetailsEndDateHidden"/>
		       			</div>
	       			<br><br>
	       			<div id="noiseDetailsChartGrid" class="width-auto margin-t10px" style="height:380px;">
	       				<span class="border-blue-2px margin-r5" id="noiseDetailsChartSpace">chart space</span>
	       				<span class="border-blue margin-l5" id="noiseDetailsGridSpace">grid space</span>
	       			</div>
       			</div>
       		</div>
        	
        	
        </div>
        <!-- // sub tab6 -->
       </div>
    </div>
</body>
</html>

