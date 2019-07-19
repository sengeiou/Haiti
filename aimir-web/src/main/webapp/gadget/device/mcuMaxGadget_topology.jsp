<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", -1); //prevents caching at the proxy
%>
<html>
<head>
<%@ include file="/gadget/system/preLoading.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<style type="text/css">
/* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
TABLE {
	border-collapse: collapse;
	width: auto;
}
/* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
@media screen and (-webkit-min-device-pixel-ratio:0) {
	.x-grid3-row td.x-grid3-cell {
		padding-left: 0px;
		padding-right: 0px;
	}
}
/* ext-js grid header 정렬 */
.x-grid3-hd-inner {
	text-align: center !important;
}

html {
	overflow-y: auto;
}
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/googleMap.jsp"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<%-- Ext-JS 관련 javascript 파일 두개 추가. --%>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/ext-all.js"></script>

<%-- Topology 관련 javascript 파일 3개 추가. --%>	
<script type="text/javascript" charset="utf-8" src="${ctx}/js/topology/d3.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/topology/d3Tooltip.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/topology/html2canvas.js"></script>

<script type="text/javascript" charset="utf-8">
        //탭초기화
        // 값 0 - 숨김처리
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs     = {hourly:1,daily:0,period:1,weekly:1,monthly:0,monthlyPeriod:1,weekDaily:0,seasonal:0,yearly:0};

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
		
		// IP 유효성 판별 결과를 담는 변수
    	var ipValidation = false;
        
        var red = '#F31523'; // security error
        var orange = '#FC8F00'; // comm error
        var gray = '#BDBDBD';   //  power down
        var yellow = '#C1C115'; // NA48h
        var green = '#2A8B49' // NA24h
        var blue = '#0718D7'; // A24h
        var redbean = '#9A4A81' // unknown
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
        
        var mcuScheduleList = '';

        var preSysId = '';
        
        var getLogResult="";
        
        var tempDeviceId="";
        var tempModelName="";
        var extAjaxTimeout = 60000;
        
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
            arrayObj[14] = $('#sMcuSerial').val();        //mcuSerial
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
            $("#topologyDivTab").hide();

            $('#generalDivTabId').click(function() { displayDivTab('generalDivTab'); });
            $('#positionDivTabId').click(function() { displayDivTab('positionDivTab'); });
            $('#topologyDivTabId').click(function() { displayDivTab('topologyDivTab'); });
            $('#scheduleDivTabId').click(function() { displayDivTab('scheduleDivTab'); });
            $('#logDivTabId').click(function() { displayDivTab('logDivTab'); });
            $('#mcuCodiDivTabId').click(function() { displayDivTab('mcuCodiDivTab'); });
            $('#singleRegMCUmcuLocalPort').bind("keydown", function(event) {inputOnlyNumberType(event, $(this));});

            $('#tabs').subtabs();

            $("#sMcuType").selectbox();
            $("#protocol").selectbox();
            $("#filter").selectbox();
            $("#order").selectbox();
            $("#deviceModelI").selectbox();
            $("#locationI").selectbox();
            $("#mcuTypeI").selectbox();
            $("#protocolTypeI").selectbox();
            $("#sMcuStatus").selectbox();

            mcuRegSysIdCheck = false;
            
            // Topology Tooltip
            $(".tooltipBox").hide();
            $(".tooltip").on({
              "mouseenter" : function(){
                var titleText = $(this).attr("title");
                $(this).data("tooltip", titleText).removeAttr("title");
                // $(this).removeAttr("title");
                $(this).after('<span class="tooltipBox">' + titleText +'</span>').fadeIn("slow");
              },
              "mouseleave" : function(){
                $(this).attr("title", $(this).data("tooltip"));
                $(".tooltipBox").remove();
              }, 
              "mousemove" : function(e){
                var mouseX = e.pageX - 10; 
                var mouseY = e.pageY + 20; 
                $(".tooltipBox").css({
                  "left" : mouseX, 
                  "top" : mouseY
                });
              }
            });
        });

        var mcuId = '';                // 선택된 MCUID
        var mcuName = '';              // 선택된 MCUNAME
        var mcuType = '';              // 선택된 MCUTYPE
        var imgPage = '';              // 선택된 MCUTYPE
        var divTabArray = [ 'generalDivTab', 'positionDivTab', 'scheduleDivTab', 'logDivTab', 'mcuCodiDivTab', 'topologyDivTab' ];
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

            $("#generalDivTab").load("${ctx}/gadget/device/mcuInfo.do", params, displayDiv);
        };

        var displayDiv = function() {
            var bak_currentDivTab = currentDivTab;
            currentDivTab='';
            $("#"+bak_currentDivTab+"Id").click();
            $("#"+bak_currentDivTab).show();
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
                    });

            $("#communicationLogStartDateHidden").val(setDate);
            $("#communicationLogEndDateHidden").val(setDate);
            //$("#updateLogStartDate").val(setDate);
            //$("#updateLogEndDate").val(setDate);
            $("#brokenLogStartDateHidden").val(setDate);
            $("#brokenLogEndDateHidden").val(setDate);
            $("#commandLogStartDateHidden").val(setDate);
            $("#commandLogEndDateHidden").val(setDate);
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
            fmtMessage[16] = "<fmt:message key="aimir.24within"/>";
            fmtMessage[17] = "<fmt:message key="aimir.commstateYellow"/>";
            fmtMessage[18] = "<fmt:message key="aimir.commstateRed"/>";
            fmtMessage[19] = "<fmt:message key="aimir.24over"/>";
            fmtMessage[20] = "<fmt:message key="aimir.48over"/>";
            fmtMessage[21] = "<fmt:message key="aimir.dcuSerial" />";

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
                commandLogSearch();
            } else if(_divName == 'mcuCodiDivTab') {

                drawCodiTab();
            } else if (_divName == 'topologyDivTab'){
            	/*  var json ={"nodes":[{"name":"150","group":3},{"name":"000B12000000102A","group":1},{"name":"000B12000000102B","group":1},{"name":"5100000000000028","group":2},{"name":"5100000000000030","group":2}],"links":[{"source":0,"target":1,"value":1},{"source":0,"target":2,"value":1},{"source":1,"target":3,"value":1},{"source":1,"target":4,"value":1}]}; */
            	 $("#topology").load('${ctx}/gadget/device/topology/topologyPopup.do'); 
            	 //인병규
            	 /* $.ajax({
            		  url: '${ctx}/gadget/device/topology/topologyPopup.do',
            		  data: {'dcuId' : "test"
              			},
            		  dataType: 'html',
            		  success: function(data) {
            		    $("#topology").html(data);
            		  }
            		}); */
            }

        };

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
        	$('#varMeteringSchedule').val('');
        	$('#varRecoverySchedule').val('');
        	$('#varUpgradeSchedule').val('');
        	$('#varUploadSchedule').val('');
        	

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
			// 입력받은 IPv4/IPv6의 형식을 check (S)
			checkIpAddress($("#singleRegMCUmcuIpAddr").val(), $("#singleRegMCUmcuIpAddr_v6").val());
			if(ipValidation == false) {
				return false;
			}
			// 입력받은 IPv4/IPv6의 형식을 check (E)
            
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
			var sysSerialNumber = $("#mcuInsertForm :input[id='singleRegMCUmcuSerialNumber']").val();
            var sysHwVersion = $("#mcuInsertForm :input[id='singleRegMCUmcuHwVer']").val();
            var sysSwVersion = $("#mcuInsertForm :input[id='singleRegMCUmcuSwVer']").val();
            var ipv4= $("#mcuInsertForm :input[id='singleRegMCUmcuIpAddr']").val();
            var ipv6= $("#mcuInsertForm :input[id='singleRegMCUmcuIpAddr_v6']").val();
            var wan= $("#mcuInsertForm :input[id='singleRegamiNetworkAddress']").val();
            var wanV6= $("#mcuInsertForm :input[id='singleRegamiNetworkAddressV6']").val();
            	
			
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
                              + deviceModelId + '&mcuTypeId=' + mcuTypeId + '&protocolTypeId=' + protocolTypeId 
                              + '&locationId=' + locationId + '&tempFile' + mcuPhoto + '&sysSerialNumber=' + sysSerialNumber
                              + '&sysHwVersion=' + sysHwVersion + '&sysSwVersion=' + sysSwVersion 
                              + '&singleRegMCUmcuIpAddr=' + ipv4
                              + '&singleRegMCUmcuIpAddr_v6=' + ipv6
                              + '&singleRegamiNetworkAddress=' + wan
                              + '&singleRegamiNetworkAddressV6=' + wanV6
                          , type     : 'post'
                          , datatype : 'application/json'
                      };

              $('#mcuInsertForm').ajaxSubmit(params);

        };

        function checkIpAddress(ipv4, ipv6) {
        	$.ajax({
        	  url: '${ctx}/gadget/device/command/checkIpAddress.do',
        	  dataType: 'json',
        	  async: false,
        	  data: {'ipv4' : ipv4,
          			'ipv6' : ipv6
        			},
        	  success: function(returnData) {
    			if(returnData.status != "SUCCESS") {
    				Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
    				ipValidation = false;
    				return;
    			} else{
    				ipValidation = true;
    			}
        	  }
        	});
        }
        
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
                    $("#mcuUpdateForm :input[name='ipv6Addr']").val(data.mcu.ipv6Addr);
                    $("#mcuUpdateForm :input[name='ipAddr']").val(data.mcu.amiNetworkAddress); //뿅
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
                    $("#mcuUpdateForm :input[name='sysHwVersion']").val(data.mcu.sysHwVersion);
                    $("#mcuUpdateForm :input[name='sysSwVersion']").val(data.mcu.sysSwVersion);
                    $("#deviceModelU option[value='" + data.mcu.deviceModel.id+ "']").attr("selected", "true");
                    $("#locationU option[value='" + data.mcu.location.id + "']").attr("selected", "true");
                    $("#mcuTypeU option[value='" + data.mcu.mcuType.id + "']").attr("selected", "true");
                    $("#protocolTypeU option[value='" + data.mcu.protocolType.id + "']").attr("selected", "true");
                    $("#deviceModelU").selectbox();
                    $("#locationU").selectbox();
                    $("#mcuTypeU").selectbox();
                    $("#protocolTypeU").selectbox();
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
                var msg = Ext.Msg.show({
      			   title:'<fmt:message key='aimir.message'/>',
      			   msg:'<fmt:message key="aimir.mcu.select" />',
      			   buttons: Ext.Msg.OK,
      		       cls: 'msgbox',
      	           
      			});
         		msg.getDialog().setPosition($("#dcuGridDiv").width()/2-30,250);
                return ;
            }
            CaptchaPanel("delete");
        };
        
        var captchacount=1;	//틀린 횟수 체크
        var incorrectCodeCheck = false; //틀렸을 경우 메세지를 보여주기 위해서
     	function CaptchaPanel(option){
     		
        	// 아직 안닫힌 경우 기존 창은 닫기
     		if(Ext.getCmp('captchaWindowPanel')){
     			Ext.getCmp('captchaWindowPanel').close();
     		} 		
     		
     		var captchaFormPanel =  new Ext.form.FormPanel({ 		      		         		       
     		        id          : 'formpanel',
     		        defaultType : 'fieldset', 		 
     		        bodyStyle:'padding:1px 1px 1px 1px',
     		        frame       : true,
     		        items       : [
     		            {
     		            	xtype: 'panel',
     		            	html: '<center><img src="${ctx}/CaptChaImg.jsp?rand='+ Math.random() + '"/></center></br>',
     		            	align:'left'
     		            	
     		            },
     		            {
     		            	xtype: 'textfield',
     		            	id : 'captchaCode',
     		            	fieldLabel: '<fmt:message key="aimir.captchaCode" />',
     		                emptyText: '<fmt:message key="aimir.enterTheCode" />',
     		                disabled : false,
     		               
     		            },
     		           {
     		            	xtype: 'label',
     		            	id : 'infolabel',
     		            	style : {
     		            		background : '#ffff00'
     		            	},
     		            	text : '*<fmt:message key="aimir.incorrectCode" />',
     		            	hidden: true
     		            }

     		        ],
     		        buttons: [
     		            {
    			    	 	text: '<fmt:message key="aimir.refresh" />',
    			    	 	listeners: {
    			            	click: function(btn,e){
    			            		captchaWindow.load(CaptchaPanel(option));
    			            		
    			            	}
    			            }
    			        },{
    			            text: '<fmt:message key="aimir.submit" />',
    			            listeners: {
    			            	click: function(btn,e){
    			            		if(5==captchacount){
      			              		  window.open('${ctx}/admin/logout.do',"_parent").parent.close();
      			              	  	} 
    			            		$.ajax({
    			                        url: '${ctx}/gadget/report/CaptchaSubmit.do',
    			                        type: 'POST',
    			                        dataType: 'json',
    			                        data: 'answer=' + $('#captchaCode').val(),
    			                        async: false,  
    			                        success: function(data) {
    			                             if(data.capcahResult=="true"){
    			                            	//올바른 코드 입력 시
    			                            	 captchacount = 1;
                                                 if(option=="delete")
                                                     captchaWindow.load(DeleteDCUPanel()); //삭제화면 로딩
                                                 else if(option=="dcuSnmpEnableDisable")
                                                     dcuSnmpEnableDisablePanel();  //SNMP_TRAP_ON_OFF
                                                 Ext.getCmp('captchaWindowPanel').close();
    			                             }else{  
    			                            	 //잘못된 코드 입력
    			                            	 captchacount++;
    			                            	 incorrectCodeCheck = true;
    			                            	 captchaWindow.load(CaptchaPanel(option));
    			                             }
    			                       		}
    			                  		});
    			            	}
    			            }
    			        },{
    			            text: '<fmt:message key="aimir.cancel" />',
    		            	listeners: {
    	                        click: function(btn,e) {
    	                        	Ext.getCmp('captchaWindowPanel').close();
    	                        }
    	                    }
    		        }]
     		    });

            var msg ="";
            if(option=="delete")
                msg = '<fmt:message key="aimir.confirmDCUDelete" />';
            else if(option=="dcuSnmpEnableDisable")
                msg = 'DCU Snmp Enable or Disable';
            var captchaWindow = new Ext.Window({
                id     : 'captchaWindowPanel',
                title  : msg,
                pageX : $("#dcuGridDiv").width()/2-100,
                pageY : 300,
                height : 204,
                width  : 300,
                layout : 'fit',
                bodyStyle   : 'padding: 10px 10px 10px 10px;',
                items  : [captchaFormPanel],
                resizable: false
            });

            captchaWindow.show();
            // 코드가 틀렸을 경우 메세지가 보이게
            if(incorrectCodeCheck == true){
                Ext.getCmp('infolabel').setVisible(true);
                Ext.getCmp('captchaCode').focus(true,100);
                incorrectCodeCheck = false;
            }
     	}
     	
     	function DeleteDCUPanel(){
     		// 아직 안닫힌 경우 기존 창은 닫기
     		if(Ext.getCmp('deleteDCUWindowPanel')){
     			Ext.getCmp('deleteDCUWindowPanel').close();
     		} 		
     		
     		Ext.getCmp('captchaWindowPanel').close();
     		var deleteDCUFormPanel =  new Ext.form.FormPanel({ 		      		         		       
     		        id          : 'formpanel',
     		        defaultType : 'fieldset', 		 
     		        bodyStyle:'padding:1px 1px 1px 1px',
     		        frame       : true,
     		        labelWidth  : 80, 		        
     		        items       : [
     		            {
     		            	xtype: 'label',
     		            	id : 'infolabel',
     		            	style : {
     		            		background : '#ffff00'
     		            	} ,
     		            	text : '<fmt:message key="aimir.msg.wantdelete" />'
     		            }
     		            
     		        ],
     		        buttons: [
     		            {
    			            text: 'YES',
    			            listeners: {
    			            	click: function(btn,e){
    			            		$.post(
    			                            '${ctx}/gadget/device/updateDcuStatus.do',
    			                            {'mcuId' : mcuId},
    			                            function(data, status) {
    			                                    		
    			                            	if(data.result == 0) {
    			                            		var msg = Ext.Msg.show({
 			                            			   title:'<fmt:message key='aimir.message'/>',
 			                            			   msg: "<fmt:message key='aimir.msg.deletesuccess'/>",
 			                            			   buttons: Ext.Msg.OK,
 			                            		       cls: 'msgbox',
 			                            	           
 			                            			});
    			                            		msg.getDialog().setPosition($("#dcuGridDiv").width()/2-30,250);
    			                            	} else {
    			                            		var msg = Ext.Msg.show({
 			                            			   title:'<fmt:message key='aimir.message'/>',
 			                            			   msg: "<fmt:message key='aimir.msg.deletesuccess'/>",
 			                            			   buttons: Ext.Msg.OK,
 			                            		       cls: 'msgbox',
 			                            	           
 			                            			});
    			                            		msg.getDialog().setPosition($("#dcuGridDiv").width()/2-30,250);
    			                            		mcuManagerResultHandler();
    			                            	}
    			                            	
    			                            }
    			                        );
    			            		Ext.getCmp('deleteDCUWindowPanel').close();
    	                        	Ext.getCmp('captchaWindowPanel').close();
    			            }}
    			        },{
    			            text: 'NO',
    		            	listeners: {
    	                        click: function(btn,e) {
    	                        	Ext.getCmp('deleteDCUWindowPanel').close();
    	                        	Ext.getCmp('captchaWindowPanel').close();
    	                        }
    	                    }
    		        }]
     		});
     		    var deleteDCUWindow = new Ext.Window({
     		        id     : 'deleteDCUWindowPanel',
     		        title  : '<fmt:message key="aimir.confirmDCUDelete" />',
     		        pageX : $("#dcuGridDiv").width()/2-100,
                    pageY : 200,
     		        height : 140,
     		        width  : 300,
     		        layout : 'fit',
     		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
     		        items  : [deleteDCUFormPanel],
     		    });
     		   deleteDCUWindow.show();
     	}

        var mcuManagerResultHandler = function() {

            // 조회 조건 초기화
            $('#sMcuId').val('');
            $('#sMcuType').val('');
            $('#sLocationId').val('');
            $('#sSwVersion').val('');
            $('#sHwVersion').val('');
            $('#sInstallDateStart').val('');
            $('#sInstallDateEnd').val('');
            $('sMcuSerial').val('');
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
            $('#topologyDivTab').hide();

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

                        }
                    });
        }
	
        function commandPing(){

        	
        	doPing('64', '3');
        }
        
    	function commandCOAPPing() {

    		doCOAPPing();
    	}
        
        function doPing(packetSize, count) {
        	Ext.Msg.wait('Waiting for response.', 'Wait !');
            $('#commandResult').val("Request MCU Server Ping....");
            $.ajax({
          	  url: '${ctx}/gadget/device/command/cmdMcuPing.do',
          	  dataType: 'json',
          	  async: true,
          	  data: {'target' : mcuId
                  , 'loginId' : loginId
                  , 'packetSize' : packetSize
                  , 'count' : count
                  },
          	  success: function(returnData) {
          		if(!returnData.status){
          			Ext.Msg.hide();
                    $('#commandResult').val("FAIL");
                       return;
                }
                if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                	Ext.Msg.hide();
                    $('#commandResult').val("MCU Ping: "+returnData.status);
                } else {
                	Ext.Msg.hide();
                	$('#commandResult').val(""); 
                    $('#commandResult').val(returnData.jsonString);
                }
          	  }
          	});
        }
	          
    	function doCOAPPing() {
    		Ext.Msg.wait('Waiting for response.', 'Wait !');
    		var modemId = $('#modemId').val();
    		$('#commandResult').val("Request MCU Server COAP-Ping....");
	    	$.ajax({
	    	  url: '${ctx}/gadget/device/command/cmdMcuCOAPPing.do',
	    	  dataType: 'json',
	    	  async: true,
	    	  data: {'target' : mcuId,
	      			'loginId' : loginId
	    			},
	    	  success: function(returnData) {
	    		    Ext.Msg.hide();
	   				$('#commandResult').val("");
	   				$('#commandResult').val(returnData.jsonString);
	    	  }
	    	});
    	}
          
    	var chk = 0;
        function successFail(packetSize, count) {
       		if(chk == 0){
              $('#commandResult').val("Success");
              chk++;
       		}else if(chk == 1){
       		  $('#commandResult').val("Fail");
       		  chk--;
       		}
        }
        
        function commandTraceroute() {
            $('#commandResult').val("Request MCU Traceroute....");
            $.ajax({
          	  url: '${ctx}/gadget/device/command/cmdMcuTraceroute.do',
          	  dataType: 'json',
          	  async: true,
          	  data: {'target' : mcuId
                  , 'loginId' : loginId},
          	  success: function(returnData) {
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
          	  }
          	});
        }
        
     // Get Log [Get Method Only]
        function commandGetLog(){
        	Ext.MessageBox.prompt('Options For Event-Log', 'Please enter the number of log<br>',
        			function(btn, numTxt) {
    		    		// 수집할 로그의 개수 지정 (textbox)		
    		    		var count = numTxt.trim();
        				if(btn=='ok'){
        					$('#commandResult').val("Request DCU Get Log....");
        					// 입력 값 검증
        					if(isNaN(count)==true || count.length < 1){
        						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
        						return false;
        					}
        					
        					// Ajax 정의    			        
        			        Ext.define('dcuEventAjax', {
        			            extend: 'Ext.data.Connection',
        			            singleton: true,
        			            constructor : function(config){
        			                this.callParent([config]);
        			                this.on("beforerequest", function(){
                                        Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                                            text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout/1000 + 's...',
                                            scope: this,
                                        });
        			                });
        			                this.on("requestcomplete", function(){
        			                    Ext.MessageBox.hide();
        			                });
        			            }
        			        });
        			        dcuEventAjax.request({
        			        		url : '${ctx}/gadget/device/command/cmdMcuGetLog.do',
        			        		method : 'POST',
        			        		timeout : extAjaxTimeout,
        			        		params: {
        			        			  target : mcuId
            	                        , loginId : loginId
            	                        , count	: count
            			            },
            			            success: function (result, request){
            			            	var jsonData = Ext.util.JSON.decode( result.responseText );
            			            	if(!jsonData.status){
        	                            	getLogResult="";
        	                                $('#commandResult').val("FAIL");
        	                                   return;
        	                            }
        	                       		if(jsonData.status.length>0 && jsonData.status!='SUCCESS'){
        	                            	$('#commandResult').val("MCU Get Log: "+jsonData.status);
        	                        	}else{
        	                            	$('#commandResult').val("[SUCCESS]\nClick the 'Get Log File' button to download.\n\n"+jsonData.jsonString); 
        	                            	getLogResult=jsonData.jsonString;
        	                        	}
        	                        }
        				});	}
        				}, this, false, '1'); //<prompt> scope=this, multiline=false, default=1    	
                      
        }

        // SNMP_TRAP_ON_OFF [GET/SET] -->> combo array
        var dcuSnmpArray = ['0:Disable', '1:Enable'];
        // SNMP_TRAP_ON_OFF [GET/SET] -->> panel
        var dcuSnmpFormPanel;
        var dcuSnmpWin;
        function dcuSnmpEnableDisablePanel() {
            // Close the current window
            if(Ext.getCmp('dcuSnmpWindow')){
                Ext.getCmp('dcuSnmpWindow').close();
            }
            // Form Panel (item of window)
            dcuSnmpFormPanel = new Ext.FormPanel({
                id : 'dcuSnmpForm',
                defaultType : 'fieldset',
                bodyStyle:'padding:1px 1px 1px 1px',
                frame : true,
                labelWidth : 100,
                items : [
                    {
                        xtype : 'radiogroup',
                        id : 'requestTypeRadio1',
                        fieldLabel : 'GET/SET ',
                        items : [
                            {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                            {boxLabel: 'SET', name: 'radio-action', inputValue:'SET'  }
                        ],
                        listeners :{
                            change: function(thisRadioGroup, checkedItem){
                                if(checkedItem.inputValue=='SET'){
                                    //button show
                                    Ext.getCmp('dcuSnmpCombo').enable();
                                    Ext.getCmp('dcuSnmpBtn').setText('Set DCU SNMP Status');
                                }else{
                                    //button hide
                                    Ext.getCmp('dcuSnmpCombo').disable();
                                    Ext.getCmp('dcuSnmpBtn').setText('Get DCU SNMP Status');
                                }
                            }
                        },
                    }, //xtype : radio
                    {
                        xtype: 'combo',
                        id : 'dcuSnmpCombo',
                        fieldLabel: 'SNMP Status ',
                        queryMode:'local',
                        store : dcuSnmpArray,
                        emptyText:'Select a item...',
                        autoSelect:true,
                        forceSelection:true,
                        width: 160,
                        triggerAction : "all",
                        disabled : true,
                    }
                ], // ~items
                buttons : [
                    {
                        id: 'dcuSnmpBtn',
                        text: 'Get DCU SNMP Status',
                        labelWidth : 60,
                        listeners: {
                            click: function(btn,e){
                                //submit action
                                dcuSnmpEnableDisableAction();
                            }
                        }
                    }, {
                        text: 'Cancel',
                        listeners: {
                            click: function (btn, e) {
                                Ext.getCmp('dcuSnmpWindow').close();
                            }
                        }
                    }
                ] // ~buttons
            });

            dcuSnmpWin = new Ext.Window({
                id     : 'dcuSnmpWindow',
                title : 'Get/Set SNMP Trap Status',
				pageX : $("#dcuGridDiv").width()/2-100,
				pageY : 300,
                height : 190,
                width  : 320,
                layout : 'fit',
                bodyStyle   : 'padding: 10px 10px 10px 10px;',
                items  : [dcuSnmpFormPanel],
            }); // ~window

            dcuSnmpWin.show();

        } // ~ panel ~
        // SNMP_TRAP_ON_OFF [GET/SET] -->> action
        function dcuSnmpEnableDisableAction() {
            var actionRequestType = Ext.getCmp('requestTypeRadio1').getValue().inputValue;
            var actionRequestValue = Ext.getCmp('dcuSnmpCombo').getValue();

            var snmpStatus = '0';
            if ( actionRequestType == "SET") {
                if(actionRequestValue == dcuSnmpArray[0]){
                    actionRequestValue = '0';
                }else if(actionRequestValue == dcuSnmpArray[1]){
                    actionRequestValue = '1';
                }else{
                    return;
                }
                snmpStatus = actionRequestValue.trim();
            }

            $('#commandResult').val('');
            Ext.define('dcuSnmpEnableDisableAjax', {
                extend: 'Ext.data.Connection',
                singleton: true,
                constructor : function(config){
                    this.callParent([config]);
                    this.on("beforerequest", function(){
                        Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                            text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout/1000 + 's...',
                            scope: this,
                        });
                    });
                    this.on("requestcomplete", function(){
                        Ext.MessageBox.hide();
                    });
                }
            });
            dcuSnmpEnableDisableAjax.request({
                url :  '${ctx}/gadget/device/command/cmdDcuSnmpEnableDisable.do',
                method : 'POST',
                timeout : extAjaxTimeout,
                params : {
                    mcuId         : mcuId,
                    loginId       : loginId,
                    requestType   : actionRequestType,
                    snmpStatus    : snmpStatus
                },
                success: function (result, request){
                    Ext.MessageBox.hide();
                    Ext.getCmp('dcuSnmpWindow').close();
                    var jsonData = Ext.util.JSON.decode( result.responseText );
                    var returnString = " # Operation Result : " + jsonData.status +
                            "\n # SNMP Status : " + jsonData.snmpStatus +
                            "\n # Message 1 : " + jsonData.rtnStr +
                            "\n # Message 2 : " + jsonData.cmdResult ;
                    $('#commandResult').val(returnString);
                },
                failure: function(result, request){
                    Ext.MessageBox.hide();
                    Ext.getCmp('dcuSnmpWindow').close();
                    var returnString = " # Operation Result : AJAX FAILURE"
                    $('#commandResult').val(returnString);
                }
            }); //~ajax

		} // ~ action ~


		// schedule 검색 후 
        var cnt=0; // 스케줄 수
        var getCheck = 'false';
        function input_append(i){
          app = document.getElementById("appendSchedule");
          
	      app.innerHTML += "<div class='rightbox margin10px' style='overflow:hidden'><input type='checkbox' id='check_"+i+"' style='width:18px;height:18px;'><input type='text' id='name_" +i+ "' class='values greenbold' style='width:200px;' readonly><input type='text' id='condition_" +i+ "' class='values greenbold' style='width:300px;'>&nbsp;<input type='text' id='task_" +i+ "' class='values greenbold' style='width:500px;'>&nbsp;<input type='text' id='suspend_" +i+ "' class='values greenbold' style='width:60px; text-align:middle; display:none;' readonly>"+"&nbsp;&nbsp;<em id= 'button_"+i+"' class='btn_org' style='margin-right: 5px;'><a id ='toggle_"+i+"'href='#' onClick='suspendToggle("+i+")' style='width: 48px; font-weight:bold;'>Toggle</a></em></div>";
	      cnt++;
        }
        
        // schedule 검색 후 재검색할때 기존에 있던 스케쥴 목록을 지움
        function removeAll(){
        	cnt = 0;
        	newLineCnt = 0;
            app = document.getElementById("appendSchedule");
          	  while (app.firstChild) {
          		  app.removeChild(app.firstChild);
          		}
          	nameArr = new Array();
          	suspendArr = new Array();
          	conditionArr = new Array();
          	taskArr = new Array();
          }
        
        // 다른 DCU 선택시
        function resetSchedule(){
        	removeAll();
        	$("#scheduleText").hide();
        	$("#appendSchedule").hide();
            $("#appendSchedule2").hide();
            $("#runnginTimeExplained").hide();
        }
        
        var newLineCnt =0;
        // Line Add 버튼 클릭시
        function input_appendByButton(){
        	for(var i=0; i<cnt; i++){
            	nameArr[i] = $('#name_'+i).val(); 
            	suspendArr[i] = $('#suspend_'+i).val();
            	conditionArr[i] = $('#condition_'+i).val();
            	taskArr[i] = $('#task_'+i).val();
            }
        	app = document.getElementById("appendSchedule");
        	app.innerHTML += "<div class='rightbox margin10px' style='overflow:hidden'><input type='checkbox' id='check_"+cnt+"' style='width:18px;height:18px;'><input type='text' id='name_" +cnt+ "' class='bold' style='width:200px;' value=''><input type='text' id='condition_" +cnt+ "' class='values bold' style='width:300px;' value=''>&nbsp;<input type='text' id='task_" +cnt+ "' class='values bold' style='width:500px;' value=''>&nbsp;<input type='text' id='suspend_" +cnt+ "' class='values bold' style='width:60px; display:none;' value=''; readonly >"+"&nbsp;&nbsp;<em id='button_"+cnt+"'class='btn_bluegreen' style='margin-right: 5px;'><a id = 'toggle_"+cnt+"' href='#' onClick='suspendToggle("+cnt+")' style='width: 48px; font-weight:bold;'>Active</a></em></div>";
            for(var i=0; i<cnt; i++){
            	$('#name_'+i).val(nameArr[i]); 
            	$('#suspend_'+i).val(suspendArr[i]);
            	$('#condition_'+i).val(conditionArr[i]);
            	$('#task_'+i).val(taskArr[i]);
            }
            cnt++;
            newLineCnt++;
          }
        
        // Line Delete 버튼 클릭시
        function input_delByButton(){
        	if(newLineCnt <= 0){
        		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No line to delete');
        		return;
        	}
        	if(newLineCnt > 0)
        		newLineCnt--;
        	app =document.getElementById('appendSchedule');
        	if(cnt > 0){
        		app.removeChild(app.lastChild);
        		cnt--;
        	}
          }
        
        function suspendToggle(i){
        	if($('#suspend_'+i).val() == "true"){
        		$('#suspend_'+i).val("false");
        		$('#toggle_'+i).html("Active");
        		$('#button_'+i).removeClass("btn_org");
        		$('#button_'+i).addClass("btn_bluegreen");
        	}else{
        		$('#suspend_'+i).val("true");
        		$('#toggle_'+i).html("Inactive");
        		$('#button_'+i).removeClass("btn_bluegreen");
        		$('#button_'+i).addClass("btn_org");
        	}
        	
        	/* $(this).addClass("class_name");

        	$('#button'+i).removeClass("btn_org"); */



        }
		
        // Line Add시 기존 목록이 초기화 되기 때문에 배열에 임시로 담아 두었다가 다시 뿌려줌
        var nameArr = new Array();
        var suspendArr = new Array();
        var conditionArr = new Array();
        var taskArr = new Array();
     	// Search Schedule 
        function commandMCUGetSchedule() {
        	Ext.Msg.wait('Waiting for response.', 'Wait !');
            $.getJSON('${ctx}/gadget/device/command/cmdMcuGetSchedule_.do'
                    , {'target' : mcuId
                    , 'loginId' : loginId}
                    , function (returnData){
                        if(returnData.status == "SUCCESS") {
                        	Ext.Msg.hide();
                        	$("#scheduleText").show();
                        	$("#appendSchedule").show();
                            $("#appendSchedule2").show();
                            $("#runnginTimeExplained").show();
                            //$("#scheduleDelete").show();
                            //$("#scheduleExecute").show();
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',returnData.status);
                            /* $('#schedulForm').each(function() {
                                this.reset();
                            }); */
                            var schedule = returnData.Schedule
                            var len = Object.keys(schedule).length

                            if(getCheck == 'true'){
                            	removeAll();
                            }
							for(var i=0; i<len/4; i++){
                            		input_append(i);
                            		getCheck = 'true';
                            }
                           
                            for(var i=0; i<len/4; i++){
                            	nameArr[i] = schedule["name"+i]
                            	$('#name_'+i).val(nameArr[i]); 
                            	suspendArr[i] = schedule["suspend"+i]
                            	$('#suspend_'+i).val(suspendArr[i]);
                            	conditionArr[i] = schedule["condition"+i]
                            	$('#condition_'+i).val(conditionArr[i]);
                            	taskArr[i] = schedule["task"+i]
                            	$('#task_'+i).val(taskArr[i]);
                            	
                            	if(suspendArr[i]=='true'){
                            		$('#toggle_'+i).html("Inactive");
                            		$('#button_'+i).removeClass("btn_bluegreen");
                            		$('#button_'+i).addClass("btn_org");
                            	}else{
                            		$('#toggle_'+i).html("Active");
                            		$('#button_'+i).removeClass("btn_org");
                            		$('#button_'+i).addClass("btn_bluegreen");
                            	}
                            }
                            $('#varRetryDefault').val(returnData.retry_condition);
                        } else {
                        	Ext.Msg.hide();
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','FAIL');
                        }
                    });

        }        

        function commandMCUSetSchedule() {
            var scheduleNameArr = [];
            var scheduleConditionArr = [];
            var scheduleTaskArr = [];
            var scheduleSuspendArr = [];
            if(cnt == 0)
            	Ext.Msg.alert('Error', 'No Item to set schedule.');
            
            var j = 0 ;
            for(var i=0; i<cnt; i++){
            	if($( '#check_'+i ).is(':checked')){
	            	scheduleNameArr[j] = $('#name_'+i).val().trim();
	            	if(scheduleNameArr[j]==""){
	            		Ext.Msg.alert('Error','Please enter the schedule name');
	            		return;
	            	}
	            	scheduleConditionArr[j] = $('#condition_'+i).val().trim();
	            	scheduleTaskArr[j] = $('#task_'+i).val().trim();
	            	scheduleSuspendArr[j] = $('#suspend_'+i).val().trim();
	            	j++;
            	}
            }
            if(j == 0){
            	Ext.Msg.alert('Error', 'No Item to update schedule.');
            	return;
            }
            
            var varRetryDefault = $('#varRetryDefault').val().trim();
            if (isNaN(varRetryDefault) == true) {
            	// retry interval is presented as number
            	Ext.Msg.alert('<fmt:message key='aimir.message'/>', '[Retry Interval Count] should be number.');
				return false;
            }
            
            var params = {'target' : mcuId,
                    'loginId' : loginId,
                    'scheduleNameArr' : scheduleNameArr,
                    'scheduleConditionArr' : scheduleConditionArr,
                    'scheduleTaskArr' : scheduleTaskArr,
                    'scheduleSuspendArr' : scheduleSuspendArr,
                    'retrycondition' : varRetryDefault
                };            

            jQuery.ajaxSettings.traditional=true;
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            $.post('${ctx}/gadget/device/command/cmdMcuSetSchedule_.do',
                    params,
                    function(data) {
            			Ext.Msg.hide();
                    	Ext.Msg.alert('<fmt:message key='aimir.message'/>',data.status);
                    	}
                   );
        };
        
        function commandMCUDeleteSchedule(){
        	var scheduleName = ''
        	var j = 0 ;
            for(var i=0; i<cnt; i++){
            	if($( '#check_'+i ).is(':checked')){
            		if($('#name_'+i).val().trim() == ""){
	            		Ext.Msg.alert('Error','Please enter the schedule name');
	            		return;
	            	}
            		if(j==0)
            			scheduleName += $('#name_'+i).val().trim();
            		else
            			scheduleName += ","+ ($('#name_'+i).val().trim());
	            	j++;
            	}
            }
            if(j == 0){
            	Ext.Msg.alert('Error', 'No Item to delete schedule.');
            	return;
            }
            if(j > 1){
            	Ext.Msg.alert('Error', 'You can delete one by one, Please check one');
            	return;
            }

        	Ext.Msg.confirm('<fmt:message key="aimir.warning"/>', 'Do you want to delete this schdule?</br>It will be delete schedule permanantely to the DCU.', 
        			function(btn,text){
        		if(btn == 'yes'){
        			Ext.Msg.wait('Waiting for response.', 'Wait !');
        			
                	var params = {'target' : mcuId,
                            'loginId' : loginId,
                            'scheduleName' : scheduleName
                        };
                	
                	$.post('${ctx}/gadget/device/command/cmdMcuDeleteSchedule.do',
                            params,
                            function(data) {
                			    Ext.Msg.hide();
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',data.status);
                            	}
                           );
        		}else {
        			Ext.Msg.alert('<fmt:message key="aimir.warning"/>','<fmt:message key="aimir.canceled" />');
        		}
        	
        	});
        }
        
        function commandMCUExecuteSchedule(){
        	var scheduleName = ''
            var j = 0 ;
                for(var i=0; i<cnt; i++){
                	if($( '#check_'+i ).is(':checked')){
                		if($('#name_'+i).val().trim() == ""){
    	            		Ext.Msg.alert('Error','Please enter the schedule name');
    	            		return;
    	            	}
                		if(j==0)
                			scheduleName += $('#name_'+i).val().trim();
                		else
                			scheduleName += ","+ ($('#name_'+i).val().trim());
    	            	j++;
                	}
                }
                if(j == 0){
                	Ext.Msg.alert('Error', 'No Item to execute schedule.');
                	return;
                }
                if(j > 1){
                	Ext.Msg.alert('Error', 'You can run one by one, Please check one');
                	return;
                }
                
        	Ext.Msg.confirm('<fmt:message key="aimir.warning"/>', 'Do you want to run this schedule?</br>It will be run this schedule from now via DCU', 
        			function(btn,text){
        		if(btn == 'yes'){
        			Ext.Msg.wait('Waiting for response.', 'Wait !');
                	var params = {'target' : mcuId,
                            'loginId' : loginId,
                            'scheduleName' : scheduleName
                        };
                	
                	$.post('${ctx}/gadget/device/command/cmdMcuExecuteSchedule.do',
                            params,
                            function(data) {
                				Ext.Msg.hide();
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',data.status);
                            	}
                           );
        		}else {
        			Ext.Msg.alert('<fmt:message key="aimir.warning"/>','<fmt:message key="aimir.canceled" />');
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
            obj.mcuSerial           = document.getElementById("sMcuSerial").value; // sMcuSerial 이부분은 다시 확인
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
        
        var winMcuEventLog;
        function openGetLogExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var result= getLogResult;
            //result ="log1\nlog2"
            if(result=="" || result==null){
            	Ext.Msg.alert("File Download","First, execute the Get Log");
            	return;
            }
            obj.supplierId = supplierId;
            obj.result	   = result;

            if(winMcuEventLog)
            	winMcuEventLog.close();
            winMcuEventLog = window.open("${ctx}/gadget/device/mcuGetLogExcelDownloadPopup.do", "Log Excel", opts);
            winMcuEventLog.opener.obj = obj;
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
                    filter : conditionArray[8],
                    order : conditionArray[9],
                    protocol : conditionArray[10],
                    dummy : conditionArray[11],
                	mcuStatus : conditionArray[13],
                	mcuSerial : conditionArray[14], // sMcuSerial
                	// firmware max gadget에서 사용하는 condition들
                    swRevison : "",
                    modelId :"",
                },
                totalProperty: 'totalCount',
                root:'result',
                idProperty : 'rowNo',
                fields: ["rowNo", "sysID", "mcuSerial", "sysName", /* "sysPhoneNumber", */ "ipAddr", "sysSwVersion", "installDate", "lastCommDate"
                         , "commState", "mcuId", "mcuTypeName", "dcuType", "vendor", "model", "location", "sysHwVersion", "protocolType","locationId"],
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
                  			if(record.data.commState == "1.1.4.5") //Comm Error
                  				return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
                  			if(record.data.commState == "1.1.4.4") //Security Error
                  				return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                  			if(record.data.commState == "1.1.4.3") //Power Down
                  				return setColorFrontTag + gray + setColorMiddleTag + value + setColorBackTag;
                  			if(record.data.commState == "fmtMessage00")
                  				return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                  			else if(record.data.commState == "fmtMessage24")
                  				return setColorFrontTag + green + setColorMiddleTag + value + setColorBackTag;
                  			else if(record.data.commState == "fmtMessage48")
                  				return setColorFrontTag + yellow + setColorMiddleTag + value + setColorBackTag;
                  			else //unknown
                  				return setColorFrontTag + redbean + setColorMiddleTag + value + setColorBackTag;
                  			
                  		}	
                   }
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[21] + "</font>", dataIndex: 'mcuSerial', renderer: addTooltip, tooltip: fmtMessage[21], width: (width-35)/14} //프로퍼티
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[2] + "</font>", dataIndex: 'sysName', renderer: addTooltip, tooltip: fmtMessage[2], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[11] + "</font>", dataIndex: 'vendor', renderer: addTooltip, tooltip: fmtMessage[11], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[12] + "</font>", dataIndex: 'model', renderer: addTooltip, tooltip: fmtMessage[12], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[15] + "</font>", dataIndex: 'location', renderer: addTooltip, tooltip: fmtMessage[15], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[4] + "</font>", dataIndex: 'ipAddr', renderer: addTooltip, tooltip: fmtMessage[4], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[5] + "</font>", dataIndex: 'sysSwVersion', renderer: addTooltip, tooltip: fmtMessage[5], width: ((width-35)/14) * 0.8}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[13] + "</font>", dataIndex: 'sysHwVersion', renderer: addTooltip, tooltip: fmtMessage[13], width: ((width-35)/14) * 0.8}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[7] + "</font>", dataIndex: 'installDate', renderer: addTooltip, tooltip: fmtMessage[7], width: ((width-35)/14) * 1.4}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[8] + "</font>", dataIndex: 'lastCommDate', renderer: addTooltip, tooltip: fmtMessage[8], width: ((width-35)/14) * 1.4}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[14] + "</font>", dataIndex: 'protocolType', renderer: addTooltip, tooltip: fmtMessage[14], width: (width-35)/14}
                   ,{header: "<font style='font-weight: bold;'>" + fmtMessage[9] + "</font>", dataIndex: 'commState', tooltip: fmtMessage[9], width: (((width-35)/14) * 0.8),
                       renderer: function(value, metaData) { 
                    	   if(value == "1.1.4.5") //Comm Error
                               return setColorFrontTag + orange + setColorMiddleTag + "CommError" + setColorBackTag;
                           if(value == "1.1.4.4")  //Security Error
                     				return setColorFrontTag + red + setColorMiddleTag + "SecurityError" + setColorBackTag;
                           if(value == "1.1.4.3") //Power Down
                        	   return setColorFrontTag + gray + setColorMiddleTag + "Power Down" + setColorBackTag;
                    	   if (value == "fmtMessage00") {
                               return setColorFrontTag + blue + setColorMiddleTag + fmtMessage[16] + setColorBackTag;
                           } else if (value == "fmtMessage24") {
                               return setColorFrontTag + green + setColorMiddleTag + fmtMessage[19] + setColorBackTag;
                           } else if (value == "fmtMessage48") {
                               return setColorFrontTag + yellow + setColorMiddleTag + fmtMessage[20] + setColorBackTag;
                           } else //unknown
                        	   return setColorFrontTag + redbean + setColorMiddleTag + "Unknown" + setColorBackTag;
                       }
                   },
                   /* {header: "<font style='font-weight: bold;'>" + fmtMessage[3] + "</font>", dataIndex: 'sysPhoneNumber', renderer: addTooltip, tooltip: fmtMessage[3], width: (width-35)/14} */
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
                                tempDeviceId=data.mcuSerial;
                                tempModelName=data.model;
                                tempLocId=data.locationId;
                                resetSchedule();
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
        
        var coapWin;
        function coapBrowser(){
    		var opts = "width=450px, height=340px, left=" + 650 + "px, top=200px, resizable=no, status=no, location=no";
    		var obj = new Object();
    		obj.target = mcuId;
    		obj.loginId = loginId;
    		obj.device = "dcu";

    		if (otaWin){
    			otaWin.close();
    		}
    		otaWin = window.open("${ctx}/gadget/device/coap/coapBrowserPopup.do", 
    								"firmwareAdd", opts);
    		otaWin.opener.obj = obj;			
    	}
        
        var nmsWin;
        function NMSPopup(){
    		var opts = "width=1600px, height=700px, left=" + 100 + "px, top=200px, resizable=no, status=no, location=no";
    		var obj = new Object();
    		obj.mcuId = mcuId;
    		
    		if (nmsWin){
    			nmsWin.close();
    		}
    		nmsWin = window.open("${ctx}/gadget/device/NMSMax.do?mcuId="+mcuId, 
    								"nmsPopup", opts);
    		nmsWin.opener.obj = obj;			
    	}
        
        function getNMSInformation(){
    		var login_id = loginId;

    		Ext.Msg.wait('Waiting for response.', 'Wait !');
    		$.ajax({
    			type : "POST",
    			data : {
    				'target' : mcuId,
    				'loginId' : login_id
    			},
    			dataType : "json",
    			async : true,
    			url : '${ctx}/gadget/device/command/getNMSInformation.do',
    			success : function(returnData) {
    				Ext.Msg.hide();
    				var title = "Refresh: ";
    				Ext.Msg.alert('<fmt:message key='aimir.message'/>',title + returnData.rtnStr);
    				//updateGraph(json);
    				displayDivTab('generalDivTab');
    				displayDivTab('topologyDivTab');
    			}
    		});
    	}

        
        function content_print(){
            
            var initBody = document.body.innerHTML;
            /* window.onbeforeprint = function(){
                document.body.innerHTML = document.getElementById('topologyDiv').innerHTML;
            }
            window.onafterprint = function(){
                document.body.innerHTML = initBody;
            } */
            //document.body.innerHTML = document.getElementById('topologyDiv').innerHTML;
            //window.print();     
            var new_window= window.open('','Ratting','width=1200,height=900,0,status=0,resizable=1');
            var div = new_window.document.createElement('div');
            new_window.document.body.appendChild(div);
            div.innerHTML = document.getElementById('topologyDiv').innerHTML;
            new_window.print();
            new_window.close();
        } 
        
    </script>
</head>

<body onLoad="init();">
	<!--상단검색-->
	<form name="search">
		<div class="wfree border-bottom padding10px">
			<table class="searching">
				<tr>
					<td class="withinput" style="width: 65px"><fmt:message key="aimir.mcutype" /></td>
					<td class="padding-r20px">
					<select id="sMcuType" name="select" style="width: 100px;">
							<option value=""><fmt:message key="aimir.all" /></option>
							<c:forEach var="mcuType" items="${mcuTypeMap}">
								<option value="${mcuType.id}">${mcuType.descr}</option>
							</c:forEach>
					</select></td>
					<td class="withinput" style="width: 80px"><fmt:message
							key="aimir.mcuid" /></td>
					<td><input name="customer_num" id="sMcuId" type="text"
						style="width: 140px;"></td>
					<td class="withinput" style="width: 80px"><fmt:message key="aimir.dcuSerial" /></td>
					<td class="padding-r20px"><input name="serial_num" id="sMcuSerial" type="text" style="width: 140px;"></td>
					<td class="withinput"><fmt:message key="aimir.sw.version" /></td>
					<td class="padding-r20px"><input id="sSwVersion" type="text" style="width: 100px;"></td>
					<td class="withinput"><fmt:message key="aimir.fw.hwversion" /></td>
					<td class="padding-r20px"><input id="sHwVersion" type="text" style="width: 100px;"></td>
					<td class="withinput"><fmt:message key="aimir.installationdate" /></td>
					<td id="search-date"><input id="sInstallDateStart" name="customer_num" type="text" class="day"></td>
					<td><input class="between" value="~" type="text"></td>
					<td id="search-date"><input id="sInstallDateEnd" name="customer_num" type="text" class="day"><input id="sInstallDateStartHidden" type="hidden" /> <input id="sInstallDateEndHidden" type="hidden" /></td>
				</tr>
				<tr>
					<td class="withinput" style="width: 80px"><fmt:message key="aimir.view.mcu39" /> <!-- 프로토콜 타입 --></td>
					<td class="padding-r20px"><select id="protocol" name="select" style="width: 100px;">
							<option value=""><fmt:message key="aimir.all" /></option>
							<c:forEach var="protocol" items="${protocols}">
								<option value="${protocol.id}">${protocol.descr}</option>
							</c:forEach>
					</select>
					</td>
					<td class="withinput"><fmt:message key="aimir.location" /></td>
					<td class="padding-r20px"><input type="text" id="searchWord" name="searchWord" style="width: 140px" /> <input type="hidden" id="sLocationId" name="location.id" value="" /></td>
					<td class="withinput"><fmt:message key="aimir.status" /></td>
					<td class="padding-r20px">
					<select id="sMcuStatus" name="select" style="width: 140px;">
							<option value=""><fmt:message key="aimir.all" /></option>
							<c:forEach var="mcuStatus" items="${mcuStatus}">
								<option value="${mcuStatus.id}">${mcuStatus.descr}</option>
							</c:forEach>
					</select>
					</td>
					<!-- Reset Button (S) -->
                    <td>
                        <div id="btn">
                            <ul style="margin-left: 0px">
                            	<li><a href="javascript:reset();" class="on"><fmt:message key="aimir.form.reset"/></a></li>
                            </ul>
                        </div>
                    </td>
                    <!-- Reset Button (E) -->
                    
                    <!-- Search Button (S) -->
                    <td>
                        <div id="btn">
                            <ul style="margin-left: 0px">
                            	<li><a href="javascript:searchList();" class="on"><fmt:message key="aimir.button.search"/></a></li>
                            </ul>
                        </div>
                    </td>
                    <!-- Search Button (E) -->
				</tr>
			</table>
			<div id="treeDivAOuter" class="tree-billing auto" style="display: none;">
				<div id="treeDivA"></div>
			</div>
		</div>
	</form>
	<!--상단검색 끝-->

	<div class="btn_left_top2 margin-t10px">
		<span class="withinput"> <fmt:message key="aimir.filtering" />
		</span> <span> <!-- name 추가해줘야 할부분 --> <select id="filter"
			style="width: 170px" onchange="javascript:searchList();">
				<option value=""><fmt:message key="aimir.all" /></option>
				<option value="normal"><fmt:message
						key="aimir.commstateGreen" /></option>
				<option value="commStateYellow"><fmt:message
						key="aimir.commstateYellow" /></option>
				<option value="commStateRed"><fmt:message
						key="aimir.commstateRed" /></option>
		</select>
		</span> <span> <select id="order" style="width: 240px"
			onchange="javascript:searchList();">
				<!-- name 추가해줘야 할부분 -->
				<option value="lastCommDesc"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.desc" /></option>
				<option value="lastCommAsc"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.asc" /></option>
				<option value="installDateDesc"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.desc" /></option>
				<option value="installDateAsc"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.asc" /></option>
		</select>
		</span>
	</div>
	
	<div id="btn" class="btn_right_top2 margin-t10px">
		<ul id="mcuAddBtn">
			<li><a href="JavaScript:mcuInsertDiv();" class="on"><fmt:message
						key="aimir.add" /></a></li>
		</ul>
		<!-- <ul><li><a href="JavaScript:mcuUpdateDiv();" class="on"><fmt:message key="aimir.update"/></a></li></ul>  -->
		<ul id="mcuDelBtn">
			<li><a href="JavaScript:mcuDelete();" class="on"><fmt:message
						key="aimir.button.delete" /></a></li>
		</ul>
		<ul>
			<li><a href="javaScript:openExcelReport();" class="on"><fmt:message
						key="aimir.button.excel" /></a></li>
		</ul>
	</div>
	
	<!-- Status 가이드 색상 테이블 -->
	<div class="lgnd_detail_div2" id="statusColorTable" style="height:auto; width:600px;">
	 <div>
		<ul>
		<li class="lgnd">
		  <table cellpadding="0" cellspacing="0">
			<colgroup>
			<col width="20">
			<col width="">
			</colgroup>
			<tbody>
			<td><span class="fChartColor_1">&nbsp;</span></td> <td><label><fmt:message key="aimir.24within"/> &nbsp;</label></td>
			<td><span class="fChartColor_2">&nbsp;</span></td> <td><label><fmt:message key="aimir.24over"/> &nbsp;</label></td>
            <td><span class="fChartColor_3">&nbsp;</span></td> <td><label><fmt:message key="aimir.48over"/> &nbsp;</label></td>
            <td><span class="fChartColor_6">&nbsp;</span></td> <td><label><fmt:message key="aimir.unknown"/> &nbsp;</label></td>
			<td><span class="fChartColor_7">&nbsp;</span></td> <td><label><fmt:message key="aimir.powerDown"/> &nbsp;</label></td>
			<td><span class="fChartColor_4">&nbsp;</span></td> <td><label><fmt:message key="aimir.commError" />&nbsp;</label></td>
            <td><span class="fChartColor_5">&nbsp;</span></td> <td><label><fmt:message key="aimir.securityError" /></label></td>
		  </tbody></table>
		</li>
		</ul>
	 </div>	
	</div>
	
	<div id="dcuGridDiv" class="gadget_body2"></div>

	<div id="mcuInsertDiv" class="width_auto margin10px"
		style="display: none;">
		<div id="singleRegTabs">

			<form id="mcuInsertForm" name="mcuInsertForm">
				<div class="border_blu padding20px">
					<table class="search">
						<li style="position: absolute; right: 0; padding-right: 20px"><fmt:message key="aimir.hems.inform.requiredField" /></li>
						<tr>
							<th><fmt:message key="aimir.mcutype" /><font color="red">*</font></th>
							<td><select id="singleRegMCUmcuType" name="sysType"></select></td>
							<th><fmt:message key="aimir.mcuid" /><font color="red">*</font></th>
							<th><input type="text" id="singleRegMCUmcuId" name="sysID" /></th>
							<td><em class="am_button"><a onclick="javascript:singleRegMCUIsMCUDuplicate();"><fmt:message key="aimir.checkDuplication" /></a></em></td>
							<th><fmt:message key="aimir.mcu.name" /></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuName" name="sysName" /></td>
						</tr>
						<tr>
							<th>IPv4 Address</th>
							<td><input type="text" id="singleRegMCUmcuIpAddr" name="ipAddr" /></td>
							<th>IPv6 Address</th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuIpAddr_v6" name="ipv6Addr" /></td>
							<th><fmt:message key="aimir.vendor" /><font color="red">*</font></th>
							<td><select id="singleRegMCUmcuVendor" name="model.deviceVendor.id" onchange="javascript:getModelListByVendorMCU();"></select></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.wanIpAddress"/></th>
							<td><input type="text" id="singleRegamiNetworkAddress" name="amiNetworkAddress" /></td>
							<th><fmt:message key="aimir.wanIpv6Address"/></th>
							<td colspan="2"><input type="text" id="singleRegamiNetworkAddressV6" name="amiNetworkAddressV6" /></td></td>
							<th>Serial No.<font color="red">*</font></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuSerialNumber" value="${mcu.sysSerialNumber}" /></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.fw.hwversion" /><font color="red">*</font></th>
							<td><input type="text" id="singleRegMCUmcuHwVer"/></td>
							<th><fmt:message key="aimir.sw.version" /><font color="red">*</font></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuSwVer"/></td>
							<th><fmt:message key="aimir.mcu.swrevision" /><font color="red">*</font></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuSwReVer" name="sysSwRevision" /></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.latitude" /></th>
							<td><input type="text" id="singleRegMCUmcuLatitude" name="gpioX" /></td>
							<th><fmt:message key="aimir.logitude" /></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuLogitude" name="gpioY" /></td>
							<th><fmt:message key="aimir.altitude" /></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuAltitude" name="gpioZ" /></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.location" /><font color="red">*</font></th>
							<td><input type="text" id="singleRegMCUmcuLoc" name="singleRegMCUmcuLoc" /> <input type="hidden" id="locationIdMcu" name="location.id" value="" /></td>
							<th><fmt:message key="aimir.mcumobile" /></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuMobile" name="sysPhoneNumber" /></td>
							<th><fmt:message key="aimir.install.pic" /></th>
							<th><input type="text" style="width: 200px" id="singleRegMCUmcuPhoto" /></th>
							<td><em class="am_button"><a href="#"><fmt:message key="aimir.button.search" /></a></em></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.detail" /> <fmt:message key="aimir.location" /></th>
							<td><input type="text" id="singleRegMCUmcuDetailLoc" name="sysLocation" /></td>
							<th><fmt:message key="aimir.portnumber" /></th>
							<td colspan="2"><input type="text" id="singleRegMCUmcuLocalPort" name="sysLocalPort" /></td>
							<th><fmt:message key="aimir.view.mcu39" /><font color="red">*</font></th>
							<td><select id="singleRegMCUmcuCommType" name="protocolType.id"></select></td>
						</tr>
						<tr>
							<th><fmt:message key="aimir.model" /><font color="red">*</font></th>
							<td colspan="2"><select id="singleRegMCUmcuModel" name="model.id"></select></td>
						</tr>
					</table>
					<div id="treeDivMcuOuter" class="tree-billing auto" style="display: none;">
						<div id="treeDivMcu"></div>
					</div>
				</div>

				<div class="width-100 margin-t20px textalign-right"
					style="margin-top: 5px">
					<em class="am_button"><a href="javascript:mcuInsert();" class="on"><fmt:message key="aimir.button.confirm" /></a></em> 
					<em class="am_button"><a href="javascript:mcuInsertDiv();mcuInsertDivCancel()" class="on"><fmt:message key="aimir.cancel" /></a></em>
				</div>

			</form>

		</div>
	</div>


	<div id="mcuInfoDiv" class="width_auto margin10px"
		style="display: none;">

		<!-- sub tab -->
		<div id="mcuDetailTabs" class="am_sub_tab">
			<ul>
				<li><a id="generalDivTabId"><fmt:message
							key="aimir.button.basicinfo" /></a></li>
				<li><a id="positionDivTabId"><fmt:message
							key="aimir.location.info" /></a></li>
				<li><a id="scheduleDivTabId"><fmt:message
							key="aimir.schedule" /></a></li>
				<li><a id="logDivTabId"><fmt:message key="aimir.history" /></a></li>
				<%-- <li><a id="mcuCodiDivTabId"><fmt:message
							key="aimir.mcucodi" /></a></li> --%> 
				<li><a id="topologyDivTabId"><fmt:message key="aimir.topology" /></a></li>
			</ul>
		</div>
		<!--// sub tab -->


		<div class="box_blu padding20px">
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
					<div class="width_auto padding10px"
						style="display: table-cell; padding-top: 7px !important; padding-bottom: 7px !important;">
						<table width="100%" class="searching2">
							<caption>
								<label class="check"><fmt:message
										key="aimir.location.info" /></label>
							</caption>
							<tr>
								<td class="padding-r20px"><fmt:message key="aimir.latitude" /></td>
								<td class="padding-r20px" align="right"><input type="text"
									id="gpioX" value="${gpioX}" /></td>
								<td><fmt:message key="aimir.address" /></td>
							</tr>
							<tr>
								<td class="padding-r20px"><fmt:message key="aimir.logitude" /></td>
								<td class="padding-r20px" align="right"><input type="text"
									id="gpioY" value="${gpioY}" /></td>
								<td rowspan="2" valign="bottom"><textarea
										name="customer_num3" id="sysLocationInfo"
										style="width: 380px; height: 50px;">${sysLocation}</textarea></td>
							</tr>
							<tr>
								<td class="padding-r20px"><fmt:message key="aimir.altitude" /></td>
								<td class="padding-r20px" align="right"><input type="text"
									id="gpioZ" value="${gpioZ}" /></td>
							</tr>
							<tr id="mcuLocBtnList">
								<td colspan="2" align="right" class="padding-r25px"><em
									class="am_button"><a href="javascript:updateMcuLoc()"
										class="on"><fmt:message key="aimir.mcu.coordinate.update" /></a></em>
								</td>
								<td align="right"><em class="am_button"><a
										href="javascript:updateMcuAddress()" class="on"><fmt:message
												key="aimir.mcu.adress.update" /></a></em> <em class="am_button"><a
										href="javascript:getGeoCoding();" class="on"><fmt:message
												key="aimir.address" />-<fmt:message
												key="aimir.mcu.coordinate" /></a></em></td>
							</tr>
						</table>
					</div>

					<div class="bottomside10px width_auto">
						<ul>
							<li class="margin-b5px" style="display: table-cell"><label
								class="check"><fmt:message
										key="aimir.mcu.device.connected" /></label></li>
							<li><div id="connectedDeviceGridDiv"></div></li>
						</ul>

					</div>

				</div>

			</div>

			<!-- // sub tab2 -->

			<!-- sub tab3 -->
			<div id="scheduleDivTab">
				<div class="wfree border-bottom padding10px">
					<em class="big_button" style="margin-right: 5px"><a
						href="javascript:commandMCUGetSchedule()">Get <fmt:message key="aimir.schedule"/></a></em> 
				</div>
				</br>
				</br>
				<div style="display:none;" id ="scheduleText">
					<div><label class="check">Schedule List</label></div>
					</br>
					<span style="padding:2px 30px 2px 2px" ></span>
					<span class="bluebold11pt">Name</span>
					<span style="padding:2px 165px 2px 2px" ></span>
					<span class="bluebold11pt">Running Time</span>
					<span style="padding:2px 215px 2px 2px" ></span>
					<span class="bluebold11pt">Parameters</span>
					<span style="padding:2px 438px 2px 2px" ></span>
					<span class="bluebold11pt">Status(Toggle)</span>
					<span style="padding:2px 10px 2px 2px" ></span>
					<em class="am_button margin-t1px" style="margin-right: 5px display: none;">
					</em>
				</div>
			<div style="display:none;" id ="appendSchedule"></div>
             <div style="display:none;" id="appendSchedule2">
             	<div class="width-auto margin-t10px">
             		<span style="padding:1px 996px 1px 1px" ></span>
                        <span>
                        <em class="am_button margin-t1px" style="margin-right: 5px;">
						<a href="#" onClick="input_appendByButton()"><b>+</b></a>
						</em>
                        <em class="am_button margin-t1px" style="margin-right: 5px;">
						<a href="#" onClick="input_delByButton()"><b>-</b></a>
						</em>
						</span>
             	</div>
             	</br>
             	<div class="width-auto margin-t10px">
             			<span style="padding:2px 10px 2px 2px" ></span>
                        <span class="bluebold11pt">Retry Interval Count</span>
                        <span style="padding:2px 9px 2px 2px" ></span>
                        <div class="rightbox margin10px">
                            <input type='text' id='varRetryDefault' class="values greenbold" style="width:37px;">
                        </div>
                        <span style="padding:2px 10px 2px 2px" ></span>
						<em class="am_button margin-t1px" style="margin-right: 5px;">
							<a href="#" onClick="commandMCUSetSchedule()">Update</a>
						</em>
						<em class='am_button margin-t1px' style='margin-right: 5px;'>
						<a href='#' onClick='commandMCUDeleteSchedule()'>Delete</a>
						</em>
						<em class='am_button margin-t1px' style='margin-right: 5px;'>
						<a href='#' onClick='commandMCUExecuteSchedule()'>Run Now</a>
						</em>
						<div class="wfree border-bottom padding10px"></div>
				</div>
             </div>
             	</br>
				</br>
                <div style="display:none;" id ="runnginTimeExplained">
					<label class="check">Running Time explained</label></br></br></tr>
					<span class="blue11pt">* * * * *</span></br>
					<span class="blue11pt">As you can see there are 5 stars. The stars represent different date parts in the following order:</span></br>	
					&nbsp; &nbsp; * minute (from 0 to 59)</br>
					&nbsp; &nbsp; * hour (from 0 to 23)</br>
					&nbsp; &nbsp; * day of month (from 1 to 31)</br>
					&nbsp; &nbsp; * month (from 1 to 12)</br>
					&nbsp; &nbsp; * day of week (from 0 to 6) (0=Sunday)</br>
					&nbsp; &nbsp; If you leave the star or asterisk, it means every</br></br>
					</br><span class="blue11pt">Example</span></br>
					&nbsp; &nbsp; Execute every Friday 1AM : 0 1 * * 5</br>
					&nbsp; &nbsp; Execute on workdays 1AM : 0 1 * * 1-5</br>
					&nbsp; &nbsp; Execute 10 past after every hour on the 1st of every month : 10 * 1 * *</br>
					&nbsp; &nbsp; Execute every 10 minutes : 0,10,20,30,40,50 * * * * or */10 * * * *</br>
                </div>
				
			</div>
			<!--// sub tab3 -->

			<!--sub tab4-->
			<div class="width-auto" id="logDivTab">
				<!--통신로그 -->
				<div class="width-100">
					<label class="check"><fmt:message key="aimir.commlog" /></label>
					<div id="communcationLogDiv" class="width-auto margin-t10px">
						<span class="margin-t5px margin-r5"><fmt:message key="aimir.logdate" /></span> <span><input
							id="communicationLogStartDate" class="day" type="text"></span> <span><input
							value="~" class="between" type="text"></span> <span
							class="margin-r5"><input id="communicationLogEndDate"
							class="day" type="text"></span> <span
							class="margin-t2px am_button"><a
							href="javascript:communicationLogSearch()"><fmt:message
									key="aimir.button.search" /></a></span> <input type="hidden"
							id="communicationLogStartDateHidden" /> <input type="hidden"
							id="communicationLogEndDateHidden" />
					</div>
					<div class="width-100 clear">
						<ul>
							<li class="floatleft margin-t10px"><span class="blue11pt"><fmt:message
										key="aimir.sendbyte.total" /> :</span> <span class="bluebold11pt"
								id="totalSend">0</span> <span class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message
										key="aimir.sendbyte.max" /> :
							</span> <span class="bluebold11pt" id="maxSend">0</span> <span
								class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message
										key="aimir.sendbyte.min" /> :
							</span> <span class="bluebold11pt" id="minSend">0</span> <span
								class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message
										key="aimir.rcvbyte.total" /> :
							</span> <span class="bluebold11pt" id="totalRcv">0</span> <span
								class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message
										key="aimir.sendrcvbyte.max" /> :
							</span> <span class="bluebold11pt" id="maxRcv">0</span> <span
								class="blue11pt">&nbsp;&nbsp;&nbsp;<fmt:message
										key="aimir.sendrcvbyte.min" /> :
							</span> <span class="bluebold11pt" id="minRcv">0</span></li>
							<li class="floatright margin-t5px padding-r2 margin-b3px"><span
								class="am_button"><a
									href="javaScript:openCommLogExcelReport();"><fmt:message
											key="aimir.button.excel" /></a></span></li>
						</ul>
					</div>
					<div id="commLogGridDiv" class="width-100 clear"
						style="margin-bottom: 10px;"></div>
				</div>

				<!--// 통신로그 -->
				<!--장애이력 -->
				<div class="width-100">
					<label class="check"><fmt:message key="aimir.alerthistory" /></label>
					<div id="brokenLogDiv" class="width-auto margin-t10px">
						<ul>
							<li class="floatleft margin-b3px"><span
								class="margin-t5px margin-r5"><fmt:message
										key="aimir.logdate" /></span> <span><input
									id="brokenLogStartDate" class="day" type="text"></span> <span><input
									value="~" class="between" type="text"></span> <span
								class="margin-r5"><input id="brokenLogEndDate"
									class="day" type="text"></span> <span
								class="margin-t2px am_button"><a
									href="javascript:brokenLogSearch()"><fmt:message
											key="aimir.button.search" /></a></span> <input type="hidden"
								id="brokenLogStartDateHidden" /> <input type="hidden"
								id="brokenLogEndDateHidden" /></li>
							<li class="floatright margin-t5px padding-r2 margin-b3px"><span
								class="am_button"><a
									href="javaScript:openEventAlertLogExcelReport();"><fmt:message
											key="aimir.button.excel" /></a></span></li>
						</ul>
					</div>
					<div id="eventAlertLogGridDiv" class="width-100 clear"
						style="margin-bottom: 10px;"></div>
				</div>
				<!--// 장애이력 -->
				<!--명령이력 -->
				<div class="width-100">
					<label class="check"><fmt:message
							key="aimir.view.operationlog" /></label>
					<div id="commandLogDiv" class="width-auto margin-t10px">
						<ul>
							<li class="floatleft margin-b3px"><span
								class="margin-t5px margin-r5"><fmt:message
										key="aimir.logdate" /></span> <span><input
									id="commandLogStartDate" class="day" type="text"></span> <span><input
									value="~" class="between" type="text"></span> <span
								class="margin-r5"><input id="commandLogEndDate"
									class="day" type="text"></span> <span
								class="margin-t2px am_button"><a
									href="javascript:commandLogSearch()"><fmt:message
											key="aimir.button.search" /></a></span> <input type="hidden"
								id="commandLogStartDateHidden" /> <input type="hidden"
								id="commandLogEndDateHidden" /></li>
							<li class="floatright margin-t5px padding-r2 margin-b3px"><span
								class="am_button"><a
									href="javaScript:openOperationLogExcelReport();"><fmt:message
											key="aimir.button.excel" /></a></span></li>
						</ul>
					</div>
					<div id="operationLogGridDiv" class="width-100 clear"></div>
				</div>
				<!--//명령이력 -->
			</div>
			<!--// sub tab4-->
			<!--sub tab5-->
			<div id="mcuCodiDivTab">
				<table class="info">
					<colgroup>
						<col width="15%" />
						<col width="" />
						<col width="15%" />
						<col width="" />
						<col width="15%" />
						<col width="" />
					</colgroup>
					<caption>
						<label class="check"><fmt:message key="aimir.setting.info" /></label>
					</caption>
					<tr class="topline">
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucodi.id" /></th>
						<td class="padding-r20px"><input type="text" id="codiID"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucodi.index" /></th>
						<td class="padding-r20px"><input type="text" id="codiIndex"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucodi.type" /></th>
						<td><input type="text" id="codiType"
							class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucodi.shortid" /></th>
						<td class="padding-r20px"><input type="text" id="codiShortID"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucode.fmversion" /></th>
						<td class="padding-r20px"><input type="text" id="codiFwVer"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.fw.hwversion" /></th>
						<td class="min90pxwidth-130"><input type="text"
							id="codiHwVer" class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucode.fmbuild" /></th>
						<td class="padding-r20px"><input type="text" id="codiFwBuild"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucode.zzifversion" /></th>
						<td class="padding-r20px"><input type="text" id="codiZAIfVer"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucode.zaifversion" /></th>
						<td><input type="text" id="codiZZIfVer"
							class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucode.resetkind" /></th>
						<td class="padding-r20px"><input type="text"
							id="codiResetKind" class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.mcucodi.autoset" /></th>
						<td class="padding-r20px"><input type="text"
							id="codiAutoSetting" class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.channelid" /></th>
						<td><input type="text" id="codiChannel"
							class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.panid" /></th>
						<td class="padding-r20px"><input type="text" id="codiPanID"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.panid.ext" /></th>
						<td class="padding-r20px"><input type="text"
							id="codiExtPanId" class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.permission" /></th>
						<td><input type="text" id="codiPermit"
							class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.rfpower" /></th>
						<td class="padding-r20px"><input type="text" id="codiRfPower"
							class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.encrypt.enable" /></th>
						<td class="padding-r20px"><input type="text"
							id="codiEnableEncrypt" class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.link.key" /></th>
						<td><input type="text" id="codiLinkKey"
							class="border-trans gray11pt" readonly /></td>
					</tr>
					<tr>
						<th class="withinput titlewidth"><fmt:message
								key="aimir.network.key" /></th>
						<td class="padding-r20px"><input type="text"
							id="codiNetworkKey" class="border-trans gray11pt" readonly /></td>
						<th class="withinput titlewidth"></th>
						<td class="padding-r20px"></td>
						<th class="withinput titlewidth"></th>
						<td>&nbsp;</td>
					</tr>
				</table>
			</div>
			<!--// sub tab5-->
			
			<!-- sub tab6-->
			<div id="topologyDivTab" class = "max">
				<div style="margin-left: 10px;">
				<ul>
				<li><input id="search"></li>
				<li><div id="btn">
					<ul id="searchDevice">
					<li><a href="JavaScript:searchNode();" class="on"><fmt:message key='aimir.button.search'/></a></li>
					</ul>
					<ul><li><acronym class="tooltip" title="Update information of topology"><a href="JavaScript:getNMSInformation();" class="on"><fmt:message key='aimir.topology.update'/></a></acronym></li></ul>
			 		<ul><li><acronym class="tooltip" title="On/Off ID labels"><a href="JavaScript:labelOnOff();" class="on"><fmt:message key='aimir.topology.label'/></a></acronym></li></ul>
			 		<ul><li><acronym class="tooltip" title="Release fixed nodes "><a href="JavaScript:unpin();" class="on"><fmt:message key='aimir.topology.unpin'/></a></acronym></li></ul>
			 		<ul><li><acronym class="tooltip" title="Take a snapshot"><a href="JavaScript:content_print();" class="on"><fmt:message key='aimir.topology.snapshot'/></a></acronym></li></ul>
			 		<ul><li><acronym class="tooltip" title="Network Management System with google map"><a href="JavaScript:NMSPopup();" class="on"><fmt:message key='aimir.topology.nms'/></a></acronym></li></ul>
				</div>
				</li>
				</ul>
				</div>
			 	<div class="gadget_body">
				<div class="width-100 margin-t10px floatleft border_blu" id="topology">
				</div>
				</div>
			</div>
			<!-- // sub tab6 -->
		</div>
	</div>
</body>
</html>