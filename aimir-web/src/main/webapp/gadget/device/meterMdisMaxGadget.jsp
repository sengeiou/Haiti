<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Meter MaxGadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.cluetip.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* Form Panel 의 Combobox 에서 textfield 와 화살표버튼의 위치를 맞추기 위한 style */
        /*.x-form-arrow-trigger {margin-top: 1px;}*/
        .x-form-arrow-trigger {margin-top: 2px;}
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.jsp"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/cluetip/jquery.cluetip.js"></script>
    <script type="text/javascript" src="${ctx}/js/cluetip/jquery.bgiframe.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/cluetip/jquery.hoverIntent.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var flexMeterSearchChart;
        var flexMeterSearchGrid;
        var flexMeterLogGrid;

        var flexMeterMeteringByMeterGrid;

        var supplierId = ${supplierId};
        var loginId    = "${loginId}";
        var decimalPos = ${decimalPos};

        var fcPieChartDataXml;
        var fcPieChart;
        var fcLogChartDataXml;
        var fcLogChart;
        var fcMeasureChartDataXml;
        var fcMeasureChart;

        var isPrepaidAlertButton = false;   // 버튼 활성화 여부
        var selectedMdisMeter;  // Mdis Meter
        var selectedMeterKind;  // Meter Command Button enable/disable
        var selectedThreshold;  // contract.prepaymentThreshold

        // meter control id
        var onDemandCtrlId          = '${onDemandCtrlId}';
        var relayStatusCtrlId       = '${relayStatusCtrlId}';
        var relayOnCtrlId           = '${relayOnCtrlId}';
        var relayOffCtrlId          = '${relayOffCtrlId}';
        var timeSyncCtrlId          = '${timeSyncCtrlId}';
        var swVerCtrlId             = '${swVerCtrlId}';
        var getTamperingCtrlId      = '${getTamperingCtrlId}';
        var clsTamperingCtrlId      = '${clsTamperingCtrlId}';
        var addPrepaidDepositCtrlId = '${addPrepaidDepositCtrlId}';
        var prepaidRateCtrlId       = '${prepaidRateCtrlId}';
        var getPrepaidDepositCtrlId = '${getPrepaidDepositCtrlId}';
        var lp1TimingCtrlId         = '${lp1TimingCtrlId}';
        var lp2TimingCtrlId         = '${lp2TimingCtrlId}';
        var meterDirectionCtrlId    = '${meterDirectionCtrlId}';
        var meterKindCtrlId         = '${meterKindCtrlId}';
        var prepaidAlertCtrlId      = '${prepaidAlertCtrlId}';
        var displayItemsCtrlId      = '${displayItemsCtrlId}';
        var meterResetCtrlId        = '${meterResetCtrlId}';

        //탭초기화
        // 값 0 - 숨김처리
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs     = {hourly:1,daily:0,period:1,weekly:0,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0,
                        search_hourly:1,search_period:1,search_monthly:1,
                        btn_hourly:0,btn_daily:0,btn_period:1,btn_weekly:0,btn_monthly:1,btn_monthlyPeriod:0,btn_weekDaily:0,btn_seasonal:0,btn_yearly:0,
                        btn_search_period:1,btn_search_monthly:1
                        };

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};



		$(document).ready(function() {
		
		
			//alert(supplierId);
		
		});



        $.ajaxSetup({
            async: false
        });

        $(function() {
            Ext.override(Ext.form.Field, {
               setFieldLabel : function(text) {
                  Ext.fly(this.el.dom.parentNode.previousSibling).update(text);
               }
            });

            // Dialog
            $('#detail_dialog').dialog({
                autoOpen: false,
                resizable: false,
                modal: false
            });
            // flex 설정
            flexMeterSearchChart = getFlexObject('meterSearchChartMdis');
            flexMeterSearchGrid  = getFlexObject('meterSearchGridMdis');
            flexMeterLogGrid     = getFlexObject('meterLogGrid');
            flexMeterMeteringByMeterGrid  = getFlexObject('meterMeteringByMeterGrid');

            $(function() { $('#_general')     .bind('click',function(event) {  } ); });
            $(function() { $('#_meterInfo')   .bind('click',function(event) { checkMeterId(); } ); });
            $(function() { $('#_locationInfo').bind('click',function(event) { checkMeterId(); } ); });
            $(function() { $('#_history')     .bind('click',function(event) { if(checkMeterId()) showMeasureChart(); } ); });
            $(function() { $('#_measurement') .bind('click',function(event) { if(checkMeterId()) showLogChart();  } ); });
            $(function() { $('#_detailInfo')  .bind('click',function(event) { if(checkMeterId()) getMeterDetailInfo();  } ); });

            $("#meterDetailTab").subtabs();

            //
            $('#meterDetailTab').bind('tabsshow', function(event, ui) {
                if (ui.panel.id == "locationInfo")
                    if(meterId != "")
                        viewMeterMap();
            });

            // SelectBox
            $('#sMeterType').selectbox();
            $('#sStatus').selectbox();
            $('#sModemYN').selectbox();
            $('#sCustomerYN').selectbox();
            $('#sModel').selectbox();

            $('#sCmdStatus').selectbox();

            // 검색결과 조건
            $('#sOrder').selectbox();
            $('#sCountperPage').selectbox();
            $('#sCommState').selectbox();
        });

        // setDate
        $(document).ready(function() {
            var locDateFormat = "yymmdd";

            $("#sInstallStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#sInstallEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

            $("#sLastcommStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#sLastcommEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

            $("#onDemandFromDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#onDemandToDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

            $('#sCmdStatus').bind("change", function(event){ viewCondPrepaidDeposit($(this)); });
            $('#sPrepaidDeposit').bind("keyup", function(event){ numericOnlyForInt(event, $(this)); });
            $('#sPrepaidDeposit').bind("focus", function(event){ numericOnlyForInt(event, $(this)); });
            $('#sPrepaidDeposit').bind("blur", function(event){ numericOnlyForInt(event, $(this)); });

            getMeterCommandInitData();
        });

        // 검색조건의 날짜를 Local유형에서 일반 유형으로 변경
        function modifyDateLocal(setDate, inst) {
            var dateId       = '#' + inst.id;
            var dateHiddenId = '#' + inst.id + 'Hidden';

            $(dateHiddenId).val($(dateId).val());

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                    });
        }

        // meterId가 비었을때 선택불가
        function checkMeterId() {
           if (meterId == "") {
               Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Meter is not selected");
               return false;
           }
           return true;
        }

        var meterId   = '';                // 선택된 meterId
        var meterType = '';                // 선택된 meterType
        var meterMds  = '';                // 선택된 meterMds
        var logGrid   = '';                // 선택된 logGrid

        function getMeterMds() {
            return meterMds;
        }
        function getMeterId() {
            return meterId;
        }

        // 미터 상세조회 : 검색결과 Grid에서 선택시 flex에서 실행
        function setMeterId(gridMeterId, gridMeterMds, gridMeterType) {
            var tabs_selectIndex = $('#meterDetailTab').tabs().tabs('option','selected');

            meterId   = gridMeterId;
            meterMds  = gridMeterMds;
            meterType = gridMeterType;

            getMeter();

            switch(tabs_selectIndex) {
                case 1 :
                    if (meterId != "") {
                        viewMeterMap();
                    }
                    break;

                case 2 :
                    meteringByMeterSearch();
                    break;

                case 3 :
                    // Detail Information
                    getMeterDetailInfo();
                    break;
            }
        }

        // commonDateTabButtonType2.jsp에서 검색
        function send2() {
            fcLogChartUpdate();
        }

        function getLogCondition() {
            var arrayObj = Array();

            arrayObj[0]  = meterMds;
            arrayObj[1]  = $('#btn_searchStartDate').val();
            arrayObj[2]  = $('#btn_searchEndDate').val();
            arrayObj[3]  = logGrid;

            return arrayObj;
        }

        // Grid 상단에 건수 표시
        function setMeterSearchGridHeader(totalCnt, pageCnt, curPage) {
            var startPageNum = 0;
            var endPageNum   = 0;

            //if (totalCnt > 0) {
            //    startPageNum = totalCnt - (curPage-1) * pageCnt;
            //    if(totalCnt > curPage)
            //        endPageNum = totalCnt - curPage * pageCnt +1;
            //    else
            //        endPageNum = totalCnt;
            //}

            if (totalCnt > 0) {
                startPageNum = totalCnt - (curPage - 1) * pageCnt;
                if((totalCnt - (curPage * pageCnt)) > 0) {
                    endPageNum = totalCnt - curPage * pageCnt +1;
                } else {
                    endPageNum = 1;
                }
            }

            document.getElementById('meterSearchGridHeader').innerHTML = startPageNum + " - " + endPageNum + " / " + totalCnt;
        }

        function getCondition() {
            if ($('#sInstallStartDate').val() == '') $('#sInstallStartDateHidden').val('');
            if ($('#sInstallEndDate').val() == '') $('#sInstallEndDateHidden').val('');
            if ($('#sLastcommStartDate').val() == '') $('#sLastcommStartDateHidden').val('');
            if ($('#sLastcommEndDate').val() == '') $('#sLastcommEndDateHidden').val('');

            var arrayObj = Array();

            arrayObj[0]  = $('#sMeterType').val();
            arrayObj[1]  = $('#sMdsId').val();
            arrayObj[2]  = $('#sStatus').val();

            arrayObj[3]  = $('#sMcuName').val();
            arrayObj[4]  = $('#sLocationId').val();
            arrayObj[5]  = $('#sConsumLocationId').val();
            arrayObj[6]  = $('#sVendor').val();
            arrayObj[7]  = $('#sModel').val();

            arrayObj[8]  = $('#sInstallStartDateHidden').val();
            arrayObj[9]  = $('#sInstallEndDateHidden').val();

            arrayObj[10] = $('#sModemYN').val();
            arrayObj[11] = $('#sCustomerYN').val();
            arrayObj[12] = $('#sLastcommStartDateHidden').val();
            arrayObj[13] = $('#sLastcommEndDateHidden').val();
            arrayObj[14] = $('#sOrder').val();
            arrayObj[15] = $('#sCommState').val();

            arrayObj[16] = supplierId;

            arrayObj[17]  = $('#sCmdStatus').val();
            arrayObj[18]  = $('#sOperators').val();
            arrayObj[19]  = $('#sPrepaidDeposit').val();

            return arrayObj;
        }

        function searchMeter() {
            if (Number($('#sInstallStartDateHidden').val()) > Number($('#sInstallEndDateHidden').val())) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.season.error'/>");
                return;
            }
            if (Number($('#sLastcommStartDateHidden').val()) > Number($('#sLastcommEndDateHidden').val())) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.season.error'/>");
                return;
            }

            updateFChart();
            flexMeterSearchChart.requestSend();
            flexMeterSearchGrid.requestSend();
        }

        function viewLogGrid(viewlogGrid) {
            logGrid = viewlogGrid;
            flexMeterLogGrid.requestSend();
        }

        function getSearchChart() {
            var fmtMessage = new Array();;

            fmtMessage[0] = "<fmt:message key="aimir.ranking"/>";       // Grid Title
            fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title

            fmtMessage[2] = "<fmt:message key="aimir.normal"/>";            // Pie Title
            fmtMessage[3] = "<fmt:message key="aimir.commstateYellow"/>";   // Pie Title
            fmtMessage[4] = "<fmt:message key="aimir.commstateRed"/>";      // Pie Title

            var dataFild = new Array();
            dataFild[0] = "no";
            dataFild[1] = "mcuSysID";
            dataFild[2] = "value0";
            dataFild[3] = "value1";
            dataFild[4] = "value2";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "left";
            gridAlign[2] = "right";
            gridAlign[3] = "right";
            gridAlign[4] = "right";

            var gridWidth = new Array();
            gridWidth[0] = "500 ";
            gridWidth[1] = "1500";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";
            gridWidth[4] = "1000";

            var dataGrid = new Array();
            dataGrid[0] = fmtMessage;
            dataGrid[1] = dataFild;
            dataGrid[2] = gridAlign;
            dataGrid[3] = gridWidth;

            return dataGrid;
        }

        function getFmtMessageCommAlert() {
            var fmtMessage = new Array();;

            fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
            fmtMessage[1] = "<fmt:message key="aimir.commstateYellow"/>";
            fmtMessage[2] = "<fmt:message key="aimir.commstateRed"/>";

            return fmtMessage;
        }

        function getSearchGridColumn() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.meterid"/>";
            fmtMessage[2] = "<fmt:message key="aimir.metertype"/>";
            fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
            fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";

            fmtMessage[5] = "<fmt:message key="aimir.model"/>";
            fmtMessage[6] = "<fmt:message key="aimir.customer"/>";
            fmtMessage[7] = "<fmt:message key="aimir.installdate"/>";
            fmtMessage[8] = "<fmt:message key="aimir.lastcomm"/>";
            fmtMessage[9] = "<fmt:message key="aimir.location"/>";

            fmtMessage[10]= "<fmt:message key="aimir.state"/>";

            fmtMessage[11]= "<fmt:message key="aimir.meter.switchstatus"/>";
            fmtMessage[12]= "<fmt:message key="aimir.meter.tamperingstatus"/>";
            fmtMessage[13]= "<fmt:message key="aimir.meter.condition.PrepaidDeposit"/>";
            fmtMessage[14]= "<fmt:message key="aimir.lastmeteringvalue"/>";

            return fmtMessage;
        }

        function getLogChart() {
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.commlog"/>";
            fmtMessage[1] = "<fmt:message key="aimir.changehistory"/>";
            fmtMessage[2] = "<fmt:message key="aimir.alerthistory"/>";
            fmtMessage[3] = "<fmt:message key="aimir.view.operationlog"/>";

            var dataFild = new Array();
            dataFild[0] = "commLog";
            dataFild[1] = "updateLog";
            dataFild[2] = "brokenLog";
            dataFild[3] = "operationLog";


            var chartColumn = new Array();
            chartColumn[0] = fmtMessage;
            chartColumn[1] = dataFild;

            return chartColumn;
        }

        function getLogGridComm() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.receiveTime"/>";
            fmtMessage[2] = "<fmt:message key="aimir.datatype"/>";
            fmtMessage[3] = "<fmt:message key="aimir.status"/>";

            var dataFild = new Array();
            dataFild[0] = "no";
            dataFild[1] = "receiveTime";
            dataFild[2] = "dataType";
            dataFild[3] = "status";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "center";
            gridAlign[2] = "center";
            gridAlign[3] = "center";

            var gridWidth = new Array();
            gridWidth[0] = "1000";
            gridWidth[1] = "1000";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";

            var gridColumn = new Array();
            gridColumn[0] = fmtMessage;
            gridColumn[1] = dataFild;
            gridColumn[2] = gridAlign;
            gridColumn[3] = gridWidth;

            return gridColumn;
        }

        function getLogGridUpdate() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.time"/>";
            fmtMessage[2] = "<fmt:message key="aimir.operator"/>";
            fmtMessage[3] = "<fmt:message key="aimir.attribute"/>";
            fmtMessage[4] = "<fmt:message key="aimir.currentvalue"/>";
            fmtMessage[5] = "<fmt:message key="aimir.beforevalue"/>";

            var dataFild = new Array();
            dataFild[0] = "number";
            dataFild[1] = "time";
            dataFild[2] = "operator";
            dataFild[3] = "attribute";
            dataFild[4] = "currentvalue";
            dataFild[5] = "beforevalue";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "center";
            gridAlign[2] = "center";
            gridAlign[3] = "center";
            gridAlign[4] = "center";
            gridAlign[5] = "center";

            var gridWidth = new Array();
            gridWidth[0] = "1000";
            gridWidth[1] = "1000";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";
            gridWidth[4] = "1000";
            gridWidth[5] = "1000";

            var gridColumn = new Array();
            gridColumn[0] = fmtMessage;
            gridColumn[1] = dataFild;
            gridColumn[2] = gridAlign;
            gridColumn[3] = gridWidth;

            return gridColumn;
        }

        function getLogGridBroken() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.majorValue"/>";
            fmtMessage[1] = "<fmt:message key="aimir.number"/>";
            fmtMessage[2] = "<fmt:message key="aimir.message"/>";
            fmtMessage[3] = "<fmt:message key="aimir.address"/>";
            fmtMessage[4] = "<fmt:message key="aimir.location"/>";
            fmtMessage[5] = "<fmt:message key="aimir.gmptime"/>";
            fmtMessage[6] = "<fmt:message key="aimir.closetime"/>";
            fmtMessage[7] = "<fmt:message key="aimir.duration"/>";

            var dataFild = new Array();
            dataFild[0] = "majorValue";
            dataFild[1] = "number";
            dataFild[2] = "message";
            dataFild[3] = "address";
            dataFild[4] = "location";
            dataFild[5] = "gmptime";
            dataFild[6] = "closetime";
            dataFild[7] = "duration";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "center";
            gridAlign[2] = "center";
            gridAlign[3] = "center";
            gridAlign[4] = "center";
            gridAlign[5] = "center";
            gridAlign[6] = "center";
            gridAlign[7] = "center";

            var gridWidth = new Array();
            gridWidth[0] = "1000";
            gridWidth[1] = "1000";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";
            gridWidth[4] = "1000";
            gridWidth[5] = "1000";
            gridWidth[6] = "1000";
            gridWidth[7] = "1000";

            var gridColumn = new Array();
            gridColumn[0] = fmtMessage;
            gridColumn[1] = dataFild;
            gridColumn[2] = gridAlign;
            gridColumn[3] = gridWidth;

            return gridColumn;
        }

        function getLogGridCommand() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.time"/>";
            fmtMessage[2] = "<fmt:message key="aimir.targetType"/>";
            fmtMessage[3] = "<fmt:message key="aimir.operator"/>";
            fmtMessage[4] = "<fmt:message key="aimir.board.running"/>";
            fmtMessage[5] = "<fmt:message key="aimir.status"/>";
            fmtMessage[6] = "<fmt:message key="aimir.description"/>";

            var dataFild = new Array();
            dataFild[0] = "no";
            dataFild[1] = "logTime";
            dataFild[2] = "targetType";
            dataFild[3] = "operator";
            dataFild[4] = "operatorType";
            dataFild[5] = "status";
            dataFild[6] = "description";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "center";
            gridAlign[2] = "center";
            gridAlign[3] = "center";
            gridAlign[4] = "center";
            gridAlign[5] = "center";
            gridAlign[6] = "center";

            var gridWidth = new Array();
            gridWidth[0] = "1000";
            gridWidth[1] = "1000";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";
            gridWidth[4] = "1000";
            gridWidth[5] = "1000";
            gridWidth[6] = "1000";

            var gridColumn = new Array();
            gridColumn[0] = fmtMessage;
            gridColumn[1] = dataFild;
            gridColumn[2] = gridAlign;
            gridColumn[3] = gridWidth;

            return gridColumn;
        }

        function nullCheck(list) {
            if (list==null)
                return false;

            for (var i = 0; i < list.length ; i++) {
                var item = list[i];
                if (item.val()==null || item.val()=='' || item.val().trim()=='-') {
                    item.select();
                    var select = item.parent().find('div[class=selectbox-wrapper]');
                    if (select!=null) {
                        select.show();
                    }
                    return false;
                }
            }
            return true;
        }

        // 미터 기본 등록
        var insertMeterInfo = function() {
            //null 채크할 목록들
            var checkList = new Array();
            checkList.push($('#mdsId'));
            checkList.push($('#meterType_input'));
            checkList.push($('#deviceVendor_input'));
            checkList.push($('#modelId_input'));
            checkList.push($('#searchWord_3'));

            if (!nullCheck(checkList))
                return;

            // select의 값을 hidden변수에 값으로 복사함
            $("#meterInfoFormEdit :input[id='meterTypeHidden']").val($('#meterType').val());
            $("#meterInfoFormEdit :input[id='deviceVendorHidden']").val($('#deviceVendor').val());
            $("#meterInfoFormEdit :input[id='modelIdHidden']").val($('#modelId').val());
            $("#meterInfoFormEdit :input[id='swVersionHidden']").val($('#swVersion').val());
            $("#meterInfoFormEdit :input[id='hwVersionHidden']").val($('#hwVersion').val());
            $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

            // 미터 유형추가
            var meterType = $('#meterType option:selected').text();
            var params    = "";

            if (meterType == "EnergyMeter") {
                  params = {
                            success  : insertMeterInfoResult
                          , url      : '${ctx}/gadget/device/insertEnergyMeterMdis.do'
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if(meterType == "WaterMeter") {
                  params = {
                            success  : insertMeterInfoResult
                          , url      : '${ctx}/gadget/device/insertWaterMeter.do'
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if(meterType == "GasMeter") {
                  params = {
                            success  : insertMeterInfoResult
                          , url      : '${ctx}/gadget/device/insertGasMeter.do'
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if(meterType == "HeatMeter") {
                  params = {
                            success  : insertMeterInfoResult
                          , url      : '${ctx}/gadget/device/insertHeatMeter.do'
                          , type     : 'post'
                          , datatype : 'application/json'
                      };

            } else if(meterType == "VolumeCorrector") {
                  params = {
                            success  : insertMeterInfoResult
                          , url      : '${ctx}/gadget/device/insertVolumeCorrector.do'
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            }

            $('#meterInfoFormEdit').ajaxSubmit(params);

        };

        // 미터 기본 등록 후처리
        function insertMeterInfoResult(responseText, status) {
            // 미터 조회
            var meterId   = responseText.id;
            var meterMds  = $('#mdsIdHidden').val();
            var meterType = $('#meterType option:selected').text();

            var tempMsg = '<fmt:message key="aimir.msg.insertsuccess" />';
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',$('#mdsId').val() + tempMsg.substring( tempMsg.lastIndexOf("_")+1 ) );

            // 등록됨 미터 상세 조회
            setMeterId(meterId, meterMds, meterType);

            // 목록 재조회
            searchMeter();
        }




        // 미터 기본 변경 처리
        var updateMeterInfo = function() {
            // select의 값을 hidden변수에 값으로 복사함
            $("#meterInfoFormEdit :input[id='meterId']").val(meterId);
            $("#meterInfoFormEdit :input[id='meterTypeHidden']").val($('#meterType').val());
            $("#meterInfoFormEdit :input[id='deviceVendorHidden']").val($('#deviceVendor').val());
            $("#meterInfoFormEdit :input[id='modelIdHidden']").val($('#modelId').val());
            $("#meterInfoFormEdit :input[id='swVersionHidden']").val($('#swVersion').val());
            $("#meterInfoFormEdit :input[id='hwVersionHidden']").val($('#hwVersion').val());
            $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

            // 미터 유형추가
            var params = { success  : updateMeterInfoResult
                         , url      : '${ctx}/gadget/device/updateMeterMdis.do'
                         , type     : 'post'
                         , datatype : 'application/json'
                         };

            $('#meterInfoFormEdit').ajaxSubmit(params);
        };

        // 미터 기본 변경 후처리
        function updateMeterInfoResult(responseText, status) {
              var tempMsg = '<fmt:message key="aimir.msg.updatesuccess" />';
              Ext.Msg.alert('<fmt:message key='aimir.message'/>',$('#mdsId').val() + tempMsg.substring( tempMsg.lastIndexOf("_")+1 ) );
              // 미터 정보 재 조회
              var params = {"meterId"   : meterId };
              $("#meterInfoDiv").load("${ctx}/gadget/device/getMeterInfoMdis.do", params);
        }

        // 미터 기본 삭제 처리
        var deleteMeterInfo = function() {
            $("#meterInfoFormEdit :input[id='meterId']").val(meterId);

            params = {
                    success  : deleteMeterInfoResult
                  , url      : '${ctx}/gadget/device/delteMeterMdis.do'
                  , type     : 'post'
                  , datatype : 'application/json'
            };

            $('#meterInfoFormEdit').ajaxSubmit(params);
        }

        // 미터 삭제 후 처리
        function deleteMeterInfoResult(responseText, status) {
            var tempMsg = '<fmt:message key="aimir.msg.deletesuccess" />';
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',meterMds + tempMsg.substring( tempMsg.lastIndexOf("_")+1 ) );

            // 미터 검색 재 조회
            searchMeter();
            // 미터 상세정보 초기화
            initMeterDetail();
        }

        // 미터 상세 정보 초기화
        function initMeterDetail() {
            // js 변수 초기화
            var meterId   = '';
            var meterType = '';
            var meterMds  = '';
            var logGrid   = '';

            // 기본정보 초기화
            $("#meterInfoDiv").load("${ctx}/gadget/device/meterMdisMaxGadgetInfo.do");
            //changeMeterInfo("insert");

            // 설치현황 초기화
            $("#meterInstallDiv").load("${ctx}/gadget/device/meterMdisMaxGadgetEmptyMeter.do");

            // 미터정보 초기화

            // 위치정보 초기화
            $('#meterLocForm').each(function(){
                    this.reset();
                    });

            // 상세>일반 Tab 클릭
            $("#meterDetailTab").tabs().tabs('select',0);
        }

        // 장비기본정보 변경
        function changeMeterInfo(state) {
            if (state == "insert") {
                $('#meterInfoFormEdit').each(function() {
                    this.reset();
                   });
                $('#installDate').val("");

                // 설치현황 초기화
                $("#meterInstallDiv").load("${ctx}/gadget/device/meterMdisMaxGadgetEmptyMeter.do");
            } else {
                // Edit 설정
                $('#mdsId').val($('#mdsIdHidden').val());

                $('#meterType').option($('#meterTypeHidden').val());
                $('#mcuSysId').val($('#mcuSysIdHidden').val());
                $('#deviceVendor').option($('#deviceVendorHidden').val());
                getModelListByVendor(1);

                $('#hwVersion').option($('#hwVersionHidden').val());
                $('#swVersion').option($('#swVersionHidden').val());

                // view 설정
                $('#mdsIdView').val($('#mdsIdHidden').val());
                $('#meterTypeView').val($('#meterType option:selected').text());

                $('#mcuSysIdView').val($('#mcuSysId').val());

                $('#deviceVendorView').val($('#deviceVendor option:selected').text());
                $('#swVersionView').val($('#swVersion option:selected').text());
                $('#hwVersionView').val($('#hwVersion option:selected').text());
                $('#locationView').val($('#searchWord_3').val());

                $('#installDateView').val($('#installDate').val());
            }

            // div 제어
            $('#meterDefaultInfoView').hide();
            $('#meterDefaultInfoEdit').hide();

            if (state == "insert")
                $('#meterDefaultInfoEdit').show();
            else if (state == "view")
                $('#meterDefaultInfoView').show();
            else if (state == "edit")
                $('#meterDefaultInfoEdit').show();

            // 버튼제어
            $('#meterDefaultInfoInsertButton').hide();
            $('#meterDefaultInfoViewButton').hide();
            $('#meterDefaultInfoEditButton').hide();

            if (state == "insert")
                $('#meterDefaultInfoInsertButton').show();
            else if (state == "view")
                $('#meterDefaultInfoViewButton').show();
            else if (state == "edit")
                $('#meterDefaultInfoEditButton').show();

            var modelId = $('#modelIdHidden').val();

            // meter command 버튼 초기화
            meterCommandButtonInit();

            if (modelId != null && modelId != "") {
                // meter command 버튼 표시 여부 설정
                meterCommandButtonAvailable(modelId);

                // 선택한 미터기에 해당하는 실행중인 Meter Command 가 있는지 체크
                $.getJSON('${ctx}/gadget/device/command/getMeterCommandRunCheck.do',
                        {'meterId' : meterId,
                         'loginId' : loginId},
                         function (json) {
                             var result = json.result;

                             if (timeId != null) {
                                 clearInterval(timeId);
                             }

                             if (result.status == "success") {
                                 disableCommandBtn();

                                 resultCtrlId = result.ctrlId;
                                 resultMeterId = result.meterId;
                                 resultWriteDate = result.writeDate;

                                 timeId = setInterval("checkCommandStatus()", intervalSec);
                             } else {
                                 enableCommandBtn();
                                 $('#commandResult').val("Result");
                                 // meter id 로 MDIS Meter 정보를 조회한다.
                                 if (isBulkCommand) {
                                	 getMdisMeterByMeterIdBulkCommand();
                                 } else {
                                     getMdisMeterByMeterId();
                                 }
                             }
                         });
            }
        }

        // meter command 버튼 초기화
        function meterCommandButtonInit() {
            $('#relayStatusButton').hide();
            $('#relayOffButton').hide();
            $('#relayOnButton').hide();
            $('#meterTimeSyncButton').hide();
            $('#tamperingClearButton').hide();
            $('#getTamperingButton').hide();
            $('#getSWVerButton').hide();
            $('#setPrepaidRateButton').hide();
            $('#getPrepaidDepositButton').hide();
            $('#addPrepaidDepositButton').hide();
            $('#setLp1TimingButton').hide();
            $('#setLp2TimingButton').hide();
            $('#setMeterDirectionButton').hide();
            $('#setMeterKindButton').hide();
            $('#setPrepaidAlertButton').hide();
            $('#setMeterDisplayItemsButton').hide();
            $('#setMeterResetButton').hide();
        }

        // meter command 버튼 표시 여부 설정
        function meterCommandButtonAvailable(modelId) {
        	
        	var operationCodes = new Array();
        	operationCodes.push("8.1.8");       // Relay Status
        	operationCodes.push("8.1.9");       // Relay On
        	operationCodes.push("8.1.10");       // Relay Off
        	operationCodes.push("8.1.3");       // Meter Time Synchronization
        	operationCodes.push("8.1.11");       // Tampering Clear ( 8.1.11)
        	operationCodes.push("8.1.12");       // Get Tampering (8.1.12)
        	operationCodes.push("8.1.13");       // Get SW Ver (8.1.13)
        	operationCodes.push("8.1.14");       // Set prepaid rate (8.1.14)
        	operationCodes.push("8.1.15");       // Get prepaid deposit (8.1.15)
        	operationCodes.push("8.1.16");       // Add prepaid deposit (8.1.16)
        	// 추가된 command
        	operationCodes.push("8.1.20");       // Set LP1 timing (8.1.20)
        	operationCodes.push("8.1.21");       // Set LP2 timing (8.1.21)
        	operationCodes.push("8.1.22");       // Set Meter Direction (8.1.22)
        	// 추가된 command (2012.05.02)
        	operationCodes.push("8.1.23");       // Set Meter Kind (8.1.23)
        	operationCodes.push("8.1.24");       // Set Prepaid Alert (8.1.24)
        	operationCodes.push("8.1.25");       // Set Meter Display Items (8.1.25)
        	operationCodes.push("8.1.26");       // Set Meter Reset (8.1.26)

            $.getJSON('${ctx}/gadget/device/operationLog/getOperationListAvailable.do',
                    {modelId : modelId, operationCodes : operationCodes},
                    function(json) {
                        var result = json.result;
                        if (result != null && result.length != null) {
                        	var len = result.length
                        	for (var i = 0; i < len; i++) {
                                if (result[i].code == "8.1.8" && result[i].status == "true") {
                                	$('#relayStatusButton').show();
                                } else if (result[i].code == "8.1.9" && result[i].status == "true") {
                                	$('#relayOnButton').show();
                                } else if (result[i].code == "8.1.10" && result[i].status == "true") {
                                	$('#relayOffButton').show();
                                } else if (result[i].code == "8.1.3" && result[i].status == "true") {
                                	$('#meterTimeSyncButton').show();
                                } else if (result[i].code == "8.1.11" && result[i].status == "true") {
                                	$('#tamperingClearButton').show();
                                } else if (result[i].code == "8.1.12" && result[i].status == "true") {
                                	$('#getTamperingButton').show();
                                } else if (result[i].code == "8.1.13" && result[i].status == "true") {
                                	$('#getSWVerButton').show();
                                } else if (result[i].code == "8.1.14" && result[i].status == "true") {
                                	$('#setPrepaidRateButton').show();
                                } else if (result[i].code == "8.1.15" && result[i].status == "true") {
                                	$('#getPrepaidDepositButton').show();
                                } else if (result[i].code == "8.1.16" && result[i].status == "true") {
                                	$('#addPrepaidDepositButton').show();
                                } else if (result[i].code == "8.1.20" && result[i].status == "true") {
                                	$('#setLp1TimingButton').show();
                                } else if (result[i].code == "8.1.21" && result[i].status == "true") {
                                	$('#setLp2TimingButton').show();
                                } else if (result[i].code == "8.1.22" && result[i].status == "true") {
                                	$('#setMeterDirectionButton').show();
                                } else if (result[i].code == "8.1.23" && result[i].status == "true") {
                                	$('#setMeterKindButton').show();
                                } else if (result[i].code == "8.1.24" && result[i].status == "true") {
                                	$('#setPrepaidAlertButton').show();
                                } else if (result[i].code == "8.1.25" && result[i].status == "true") {
                                	$('#setMeterDisplayItemsButton').show();
                                } else if (result[i].code == "8.1.26" && result[i].status == "true") {
                                	$('#setMeterResetButton').show();
                                }
                        	}
                        }
                    }
              );
        }

		//session supplierId value setting
		var supplierId2= ${sesSupplierId};

        // 미터 설치 현황 변경
        function updateMeterInstallInfo() 
        {
        	//alert(meterId);
        
            $("#meterInstallFormEdit :input[id='meterId']").val(meterId);
            // 미터 유형추가
            var params    = "";


						
			//alert(supplierId2);

			//에너지 미터인 경우
            if (meterType == "EnergyMeter")
            {
            		//alert("EnergyMeter");
                  params = {
                            success  : updateMeterInstallInfoResult
                          , url      : '${ctx}/gadget/device/updateEnergyMeterMdis.do'
                          , data:{ "supplierId"         : supplierId2  }
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if (meterType == "WaterMeter") {
                  params = {
                            success  : updateMeterInstallInfoResult
                          , url      : '${ctx}/gadget/device/updateWaterMeter.do'
                          , data:{ "supplierId"         : supplierId2  }
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if (meterType == "GasMeter") {
                  params = {
                            success  : updateMeterInstallInfoResult
                          , url      : '${ctx}/gadget/device/updateGasMeter.do'
                          , data:{ "supplierId"         : supplierId2  }
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            } else if (meterType == "HeatMeter") {
                  params = {
                            success  : updateMeterInstallInfoResult
                          , url      : '${ctx}/gadget/device/updateHeatMeter.do'
                          ,data:{ "supplierId"         : supplierId2  }
                          , type     : 'post'
                          , datatype : 'application/json'
                      };

            }else if (meterType == "VolumeCorrector") {
                  params = {
                            success  : updateMeterInstallInfoResult
                          , url      : '${ctx}/gadget/device/updateVolumeCorrector.do'
                          , data:{ "supplierId"         : supplierId2  }
                          , type     : 'post'
                          , datatype : 'application/json'
                      };
            }

            $('#meterInstallFormEdit').ajaxSubmit(params);
        }
        
        
        
        //  updateMeterInstallInfo CALLBACK FUNC
        function updateMeterInstallInfoResult(responseText, status) 
        {
        
        	//alert(responseText.resultMsg)
            // 미터 재조회
            getMeter();
        }
        
        

        // 미터 상세조회
        function getMeter() 
        {
            // 미터 기본정보 조회
            var params = {"meterId"   : meterId };
            $("#meterInfoDiv").load("${ctx}/gadget/device/getMeterInfoMdis.do", params);


            // 장비설치현황조회
            params = {
                       "meterId"   : meterId,
                       "meterType" : meterType
                       };

            $("#meterInstallDiv").load("${ctx}/gadget/device/getMeterByTypeMdis.do", params);
        }// end of getMeter
        
        
        
        

        // 설치현황 > 이미지 관련 JS -------------------------------------------------------
        var installImgArray = null;

        // 이미지 저장
        var insertMeterInstallImg = function(orgFileName, saveFileName) {
            $.getJSON('${ctx}/gadget/device/insertMeterInstallImgMdis.do',
               {  'meterId'      : meterId
                , 'orgFileName'  : orgFileName
                , 'saveFileName' : saveFileName
               },

               function(data, status) {
                   installImgArray = data.installImgList;
                   setInstallImgBar(installImgArray.length);
               });
           };

        // 이미지 초기 조회
        function getInstallImg() {
            $.getJSON( '${ctx}/gadget/device/getInsertInstallImgMdis.do'
                    , {'meterId'      : meterId}
                    , function(data) {installImgArray = data.installImgList;
                                      setInstallImgBar(1);
                                     }
                    );
        }

        // 설치 이미지 Bar설정
        function setInstallImgBar(curPage) {
            var imgArray = installImgArray;

            if (imgArray != null && imgArray != "") {
                var meterInstallImgPaging = "<ul><li>";
                var curPageImg            = "";
                var curPageId             = "";

                $.each(imgArray, function(index, imgData) {
                    var indexValue = index+1;

                    meterInstallImgPaging = meterInstallImgPaging + "<a href=\"javascript:setInstallImgBar('" + indexValue + "');\" ";

                    // 선택한 page
                    if (indexValue == curPage) {
                        meterInstallImgPaging = meterInstallImgPaging + " class=\"current\" ";

                        curPageImg   = imgData.saveFileName;
                        curPageId    = imgData.id;
                    }

                    meterInstallImgPaging = meterInstallImgPaging + ">" + indexValue + "</a>";
                });

                meterInstallImgPaging = meterInstallImgPaging + "</ul></li>";

                // 이미지 ID 설정
                $("#meterInstallImgId").val(curPageId);

                // 이미지 설정
                $("#meterInstallImgEdit").html("<img src=\"../../" + curPageImg + "\" >");
                $("#meterInstallImgView").html("<img src=\"../../" + curPageImg + "\" >");

                // 설치 이미지 Bar설정
                $("#meterInstallImgPagingEdit").html(meterInstallImgPaging);
                $("#meterInstallImgPagingView").html(meterInstallImgPaging);
            } else {
                // 이미지 ID 설정
                $("#meterInstallImgId").val("");

                // 이미지 설정
                $("#meterInstallImgEdit").html("<img src='../../uploadImg/default/meterDefaultImg.jpg'>");
                $("#meterInstallImgView").html("<img src='../../uploadImg/default/meterDefaultImg.jpg'>");

                // 설치 이미지 Bar설정
                $("#meterInstallImgPagingEdit").html("");
                $("#meterInstallImgPagingView").html("");
            }
        }

        // installInfoDiv의  hide/show
        var changeInstallDivDisplay = function(target) {

            $('#meterInstallInfoEditDiv').hide();
            $('#meterInstallInfoViewDiv').hide();

            if (target == "edit") {
                $('#meterInstallInfoEditDiv').show();

                // hidden의 값을 복사함 - Meter유형별로 변경필요
                $('#customerNo').val($('#customerNoHidden').val());
                $('#customerName').val($('#customerNameHidden').val());
                $('#supplierId').val($('#supplierIdHidden').val());
                $('#supplierName').val($('#supplierNameHidden').val());

                $('#esolution').val($('#esolutionHidden').val());
                $('#ke').val($('#keHidden').val());
            } else if (target == "view") {
                $('#meterInstallInfoViewDiv').show();
            }
        };

        // 장비설치 > 설치 이미지 등록
        function getModelListByVendor(setState) {
            if($('#deviceVendor').val() != "")
                    $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do'
                    , {'vendorId' : $('#deviceVendor').val() }
                    , function (returnData){
                        $('#modelId').noneSelect(returnData.deviceModels);
                        $('#modelId').selectbox();

                        // getMeter등의 정보로 Model을 설정해야 하는 경우 set을 표시함
                        if(setState == 1){
                            $('#modelId').option($('#modelIdHidden').val());
                            $('#modelIdView').val($('#modelId option:selected').text());
                        }
                    });
           };

         function getDeviceVendorsBySupplierId() {
            $.getJSON('${ctx}/gadget/system/vendorlist.do'
                    , {'supplierId' : supplierId}
                    , function (returnData){
                        $('#sVendor').loadSelect(returnData.deviceVendors);
                        $('#sVendor').selectbox();
                    });
           };

        function getDeviceModelsByVenendorId() {
            if( $('#sVendor').val() != "")
                $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do'
                        , {'vendorId' : $('#sVendor').val() }
                        , function (returnData){
                            $('#sModel').noneSelect(returnData.deviceModels);
                            $('#sModel').selectbox();
                        });
           };


        // 검침데이터 Tab 관련 JS --------------------------------------------------------
        function send() {
            // CommonDateTab에서 send조회
            meteringByMeterSearch();
        }

        function meteringByMeterSearch() {
            fcMeasureChartUpdate();
            flexMeterMeteringByMeterGrid.requestSend();
        }

        function getMeteringByMeterCondition() {
            var arrayObj = Array();

            arrayObj[0]  = meterId;
            arrayObj[1]  = meterType;

            arrayObj[2]  = $('#searchStartDate').val();
            arrayObj[3]  = $('#searchEndDate').val();
            arrayObj[4]  = $('#searchStartHour').val();
            arrayObj[5]  = $('#searchEndHour').val();
            arrayObj[6]  = $('#searchDateType').val();
            arrayObj[7]  = supplierId;

            return arrayObj;
        }

        function getMeteringByMeterGrid() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.meteringtime"/>";
            fmtMessage[2] = "<fmt:message key="aimir.usage"/>";
            fmtMessage[3] = "<fmt:message key="aimir.co2formula"/>";

            var dataFild = new Array();
            dataFild[0] = "No";
            dataFild[1] = "meteringDate";
            dataFild[2] = "usage";
            dataFild[3] = "co2";

            var gridAlign = new Array();
            gridAlign[0] = "center";
            gridAlign[1] = "center";
            gridAlign[2] = "right";
            gridAlign[3] = "right";

            var gridWidth = new Array();
            gridWidth[0] = "500 ";
            gridWidth[1] = "1000";
            gridWidth[2] = "1000";
            gridWidth[3] = "1000";

            var dataGrid = new Array();
            dataGrid[0] = fmtMessage;
            dataGrid[1] = dataFild;
            dataGrid[2] = gridAlign;
            dataGrid[3] = gridWidth;

            return dataGrid;
        }

        // 주소 정보 -> X,Y,Z로 변경함
        function getGeoCoding() {
            cvAddressToCoordinate($('#sysLocation').val(), "gpioX", "gpioY", "gpioZ");
        }

        // 지도의 위도  / 경도 변경
        function updateMeterLoc() {
            $.getJSON('${ctx}/gadget/device/mapUpdate.do'
                    , {'className' : "meter",
                        'name'   : meterMds    ,
                        'pointx' : $('#gpioX').val(),
                        'pointy' : $('#gpioY').val() }
                    , function (returnData){
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Geographic Coords Update");
                    });
        }

        // 주소 업데이트
        function updateMeterAddress() {
            $.getJSON('${ctx}/gadget/device/mapUpdateAddress.do'
                    , {'className' : "meter",
                        'name'     : meterMds    ,
                        'address'  : encodeURIComponent($('#sysLocation').val()) }
                    , function (returnData) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Address Update Complate");
                        });
        }

        function viewMeterMap() {
            // googleMap 초기화
            googleMapInit();
            // meter의 정보를 구함
            showPoints('${ctx}/' + supplierId +'/'+ meterMds +'/meter.do');
        }

        function init() {
            // 위치정보 > 지도 초기화
            //googleMapInit();
            // 검색 > 공급사의 제조사 조회
            getDeviceVendorsBySupplierId();
            // 상세조회 > 일반 초기화
            initMeterDetail();
        }

        var isBulkCommand = false;  // bulk command 여부

        $(function() {
            locationTreeGoGo('treeDivA', 'searchWord_1', 'sLocationId');
        });

        function modifyContract(){
            // 설치정보 Tab의 내용을 계약정보로 변경
            $("#meterInstallDiv").load("${ctx}/gadget/device/meterMaxGadgetContract.do");
        }

        /************ Fusion Chart **************/

        function showLogChart() {
            var colWidth = 0;
            if ($('#fcLogChartDiv').width() > 0) {
                colWidth = $('#fcLogChartDiv').width();
            } else if ($('#fcMeasureChartDiv').width() > 0) {
                colWidth = $('#fcMeasureChartDiv').width();
            } else if ($('#general').width() > 0) {
                colWidth = $('#general').width() - 42;
            } else if ($('#locationInfo').width() > 0) {
                colWidth = $('#locationInfo').width() - 42;
            }

            fcLogChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcLogChartId", colWidth, "250", "0", "0");
            fcLogChart.setDataXML(fcLogChartDataXml);
            fcLogChart.setTransparent("transparent");
            fcLogChart.render("fcLogChartDiv");
        }

        function showMeasureChart() {
            var colWidth = 0;
            if ($('#fcLogChartDiv').width() > 0) {
                colWidth = $('#fcLogChartDiv').width();
            } else if ($('#fcMeasureChartDiv').width() > 0) {
                colWidth = $('#fcMeasureChartDiv').width();
            } else if ($('#general').width() > 0) {
                colWidth = $('#general').width() - 42;
            } else if ($('#locationInfo').width() > 0) {
                colWidth = $('#locationInfo').width() - 42;
            }

            fcMeasureChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcMeasureChartId", colWidth, "250", "0", "0");
            fcMeasureChart.setDataXML(fcMeasureChartDataXml);
            fcMeasureChart.setTransparent("transparent");
            fcMeasureChart.render("fcMeasureChartDiv");
        }

        $(document).ready(function() {
            initMeasureChartData();
            updateFChart();
        });

        function updateFChart() {
            emergePre();

            if ($('#sInstallStartDate').val() == '') $('#sInstallStartDateHidden').val('');
            if ($('#sInstallEndDate').val() == '') $('#sInstallEndDateHidden').val('');
            if ($('#sLastcommStartDate').val() == '') $('#sLastcommStartDateHidden').val('');
            if ($('#sLastcommEndDate').val() == '') $('#sLastcommEndDateHidden').val('');

            $.getJSON('${ctx}/gadget/device/getMeterSearchChartMdis.do'
                ,{sMeterType:$('#sMeterType').val(),
                    sMdsId:$('#sMdsId').val(),
                    sStatus:$('#sStatus').val(),
                    sCmdStatus:$('#sCmdStatus').val(),
                    sOperators:$('#sOperators').val(),
                    sPrepaidDeposit:$('#sPrepaidDeposit').val(),
                    sMcuName:$('#sMcuName').val(),
                    sLocationId:$('#sLocationId').val(),
                    sConsumLocationId:$('#sConsumLocationId').val(),
                    sVendor:$('#sVendor').val(),
                    sModel:$('#sModel').val(),
                    sInstallStartDate:$('#sInstallStartDateHidden').val(),
                    sInstallEndDate:$('#sInstallEndDateHidden').val(),
                    sModemYN:$('#sModemYN').val(),
                    sCustomerYN:$('#sCustomerYN').val(),
                    sLastcommStartDate:$('#sLastcommStartDateHidden').val(),
                    sLastcommEndDate:$('#sLastcommEndDateHidden').val(),
                    supplierId:supplierId}
            ,function(json) {
                    var list = json.chartData;
                    fcPieChartDataXml = "<chart "
                        + "showPercentValues='1' "
                        + "showPercentInToolTip='0' "
                        + "showLabels='0' "
                        + "showValues='0' "
                        + "showLegend='1' "
                        + "legendPosition='RIGHT' "
                        + "manageLabelOverflow='1' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_Pie3D
                        + ">";
                    var labels = "";

                    var emptyFlag = true;
                    for (index in list) {
                        if (list[index].label == "fmtMessage") {
                            labels += "<set label='<fmt:message key='aimir.normal' />' value='" + list[index].data + "' color='"+fChartColor_Step4[0]+"' />";
                            if(list[index].data > 0) emptyFlag = false;
                        } else if (list[index].label == "fmtMessage24") {
                             labels += "<set label='<fmt:message key='aimir.commstateYellow' />' value='" + list[index].data + "' color='"+fChartColor_Step4[2]+"' />";
                            if(list[index].data > 0) emptyFlag = false;
                        } else if (list[index].label == "fmtMessage48") {
                             labels += "<set label='<fmt:message key='aimir.commstateRed' />' value='" + list[index].data + "' color='"+fChartColor_Step4[3]+"' />";
                            if(list[index].data > 0) emptyFlag = false;
                        }
                    }

                    if (list == null || list.length == 0 || emptyFlag) {
                        labels = "<set label='' value='1' color='E9E9E9' toolText='' />";
                    }

                    fcPieChartDataXml += labels + "</chart>";
                    fcChartRender();

                    hide();
                }
            );
        }

        function initLogChartData() {
            fcLogChartDataXml = "<chart "
                   + "showValues='0' "
                   + "showLabels='1' "
                   + "showLegend='1' "
                   + "legendPosition='RIGHT' "
                   + fChartStyle_Common
                   + fChartStyle_Font
                   + fChartStyle_MSColumn3D_nobg
                   + ">"
                + "<categories><category label=' ' /></categories>"
                + "<dataset seriesName='<fmt:message key='aimir.commlog'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.changehistory'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.alerthistory'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.view.operationlog'/>'><set value='0' /></dataset>"
                + "</chart>";
        }

        function fcLogChartUpdate() {
            emergePre();

            $.getJSON('${ctx}/gadget/device/getMeterLogChartMdis.do'
                ,{meterMds:meterMds,
                    startDate:$('#btn_searchStartDate').val(),
                    endDate:$('#btn_searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                    var list = json.chartData;
                    fcLogChartDataXml = "<chart "
                            + "showValues='0' "
                            + "showLabels='1' "
                            + "showLegend='1' "
                            + "legendPosition='RIGHT' "
                            + "labelDisplay = 'WRAP' "
                            + "labelStep='"+(list.length / 8)+"' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_MSColumn3D_nobg
                            + ">";
                    var categories = "<categories>";
                    var dataset1 = "<dataset seriesName='<fmt:message key='aimir.commlog'/>'>";
                    var dataset2 = "<dataset seriesName='<fmt:message key='aimir.changehistory'/>'>";
                    var dataset3 = "<dataset seriesName='<fmt:message key='aimir.alerthistory'/>'>";
                    var dataset4 = "<dataset seriesName='<fmt:message key='aimir.view.operationlog'/>'>";

                    for (index in list) {
                        if (list[index].xTag != null) {
                            categories += "<category label='"+list[index].xTag +"' />";

                            dataset1 += "<set value='"+list[index].commLog+"' link=\"JavaScript:viewLogGrid( 'commLog' );\" />";
                            dataset2 += "<set value='"+list[index].updateLog+"' link=\"JavaScript:viewLogGrid( 'updateLog' );\" />";
                            dataset3 += "<set value='"+list[index].brokenLog+"' link=\"JavaScript:viewLogGrid( 'brokenLog' );\" />";
                            dataset4 += "<set value='"+list[index].operationLog+"' link=\"JavaScript:viewLogGrid( 'operationLog' );\" />";
                        }
                    }

                    if (list == null || list.length == 0) {
                        categories += "<category label=' ' />";

                        dataset1 += "<set value='0' />";
                        dataset2 += "<set value='0' />";
                        dataset3 += "<set value='0' />";
                        dataset4 += "<set value='0' />";
                    }

                    categories += "</categories>";
                    dataset1 += "</dataset>";
                    dataset2 += "</dataset>";
                    dataset3 += "</dataset>";
                    dataset4 += "</dataset>";

                    fcLogChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";
                    fcChartRender();

                    hide();
                }
            );
        }

        function initMeasureChartData() {
            fcMeasureChartDataXml = "<chart "
                   + "showValues='0' "
                   + "showLabels='1' "
                   + "showLegend='1' "
                   + "legendPosition='RIGHT' "
                   + "PYAxisName='<fmt:message key="aimir.usage"/>' "
                   + fChartStyle_Common
                   + fChartStyle_Font
                   + fChartStyle_MSColumn3D_nobg
                   + ">"
                + "<categories><category label=' ' /></categories>"
                + "<dataset seriesName='<fmt:message key='aimir.usage'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.co2formula2'/>' color='"+fChartColor_Step4[1]+"' >  <set value='0' color='"+fChartColor_Step4[1]+"' /></dataset>"
                + "</chart>";
        }

        function fcMeasureChartUpdate() {
            emergePre();

            $.getJSON('${ctx}/gadget/device/getMeteringDataByMeterChartMdis.do'
                ,{meterId:meterId,
                    meterType:meterType,
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    searchStartHour:$('#searchStartHour').val(),
                    searchEndHour:$('#searchEndHour').val(),
                    searchDateType:$('#searchDateType').val(),
                    supplierId:supplierId}
            ,function(json) {
                    var list = json.chartData;

                    fcMeasureChartDataXml = "<chart "
                            + "showValues='0' "
                            + "showLabels='1' "
                            + "showLegend='1' "
                            + "PYAxisName='<fmt:message key="aimir.usage"/>' "
                            + "legendPosition='RIGHT' "
                            + "labelDisplay = 'WRAP' "
                            + "labelStep='"+(list.length / 8)+"' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_MSColumn3D_nobg
                            + ">";
                    var categories = "<categories>";
                    var dataset1 = "<dataset seriesName='<fmt:message key='aimir.usage'/>'>";
                    var dataset2 = "<dataset seriesName='<fmt:message key='aimir.co2formula2' />' color='"+fChartColor_Step4[1]+"' >";

                    for(index in list) {
                        if(list[index].meteringDate != null) {
                            categories += "<category label='"+list[index].meteringDate +"' />";

                            dataset1 += "<set value='"+list[index].usage+"' />";
                            dataset2 += "<set value='"+list[index].co2+"' color='"+fChartColor_Step4[1]+"' />";
                        }
                    }

                    if(list == null || list.length == 0) {
                        categories += "<category label=' ' />";

                        dataset1 += "<set value='0' />";
                        dataset2 += "<set value='0' color='"+fChartColor_Step4[1]+"' />";
                    }

                    categories += "</categories>";
                    dataset1 += "</dataset>";
                    dataset2 += "</dataset>";

                    fcMeasureChartDataXml += categories + dataset1 + dataset2 + "</chart>";
                    fcChartRender();

                    hide();
                }
            );
        }

        window.onresize = fcChartRender;
        function fcChartRender() {
            fcPieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myChartId", $('#fcPieChartDiv').width() , "152", "0", "0");
            fcPieChart.setDataXML(fcPieChartDataXml);
            fcPieChart.setTransparent("transparent");
            fcPieChart.render("fcPieChartDiv");

            var colWidth = 0;
            if ($('#fcLogChartDiv').width() > 0) {
                colWidth = $('#fcLogChartDiv').width();
            } else if ($('#fcMeasureChartDiv').width() > 0) {
                colWidth = $('#fcMeasureChartDiv').width();
            } else if ($('#general').width() > 0) {
                colWidth = $('#general').width() - 42;
            } else if ($('#locationInfo').width() > 0) {
                colWidth = $('#locationInfo').width() - 42;
            }

            fcMeasureChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcMeasureChartId", colWidth, "250", "0", "0");
            fcMeasureChart.setDataXML(fcMeasureChartDataXml);
            fcMeasureChart.setTransparent("transparent");
            fcMeasureChart.render("fcMeasureChartDiv");
        }

        // command button action
        function saveMeterCommand(ctrlId, tagId) {
            if (tagId == null || $("#"+tagId).attr("disabled") != "true") {
                if (isBulkCommand) {
                    //비동기 설정
                    $.ajaxSetup({
                        async : true
                    });
                    Ext.Msg.wait('Waiting for response.', 'Wait !');

                    $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                            ,{'ctrlId' : ctrlId,
                              'meterIds' : bulkCommandMeterIds,
                              'loginId' : loginId}
                            ,function (json) {
                                var result = json.result;
                                saveMeterCommandResult(result);
                             });
                } else {
                    $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                            ,{'ctrlId' : ctrlId,
                              'meterId' : meterId,
                              'loginId' : loginId}
                            ,function (json) {
                                var result = json.result;
                                saveMeterCommandResult(result);
                             });
                }
            }
        }

        // command 실행 결과
        function saveMeterCommandResult(result) {
            $('#commandResult').val("");
            if (isBulkCommand) {
                if (result.status == "success") {
                    disableCommandBtn();
                    resultCtrlId = result.ctrlId;
                    resultMeterIds = result.meterIds;
                    resultWriteDates = result.writeDates;
                    timeId = setInterval("checkCommandStatus()", intervalSec);
                } else {
                    //원래 동기방식으로 설정
                    $.ajaxSetup({
                        async : false
                    });
                    Ext.Msg.hide();

                    var msg = null;
                    if (result.msg == "OUT_OF_RANGE_RATE") {
                        msg = 'Error: <fmt:message key="aimir.meter.command.msg.err.outofrangerate"/>';
                    } else if (result.msg == "OUT_OF_RANGE_DEPOSIT") {
                        msg = 'Error: <fmt:message key="aimir.meter.command.msg.err.outofrangedeposit"/>';
                    } else {
                        msg = result.msg;
                    }
                    $('#commandResult').val(msg);
                }
            } else {
                if (result.status == "success") {
                    disableCommandBtn();
                    resultCtrlId = result.ctrlId;
                    resultMeterId = result.meterId;
                    resultWriteDate = result.writeDate;
                    timeId = setInterval("checkCommandStatus()", intervalSec);
                } else {
                    var msg = null;
                    if (result.msg == "OUT_OF_RANGE_RATE") {
                        msg = 'Error: <fmt:message key="aimir.meter.command.msg.err.outofrangerate"/>';
                    } else if (result.msg == "OUT_OF_RANGE_DEPOSIT") {
                        msg = 'Error: <fmt:message key="aimir.meter.command.msg.err.outofrangedeposit"/>';
                    } else {
                        msg = result.msg;
                    }
                    $('#commandResult').val(msg);
                }
            }
        }

        var timeId;
        var intervalSec = 5000;    // 5초
        var resultCtrlId;
        var resultMeterId;
        var resultMeterIds;
        var resultWriteDate;
        var resultWriteDates;
        // 실행 중인 command 의 결과를 체크한다.
        function checkCommandStatus() {
            if (isBulkCommand) {
                $.getJSON('${ctx}/gadget/device/command/getBulkMeterCommandResultData.do'
                        ,{'ctrlId' : resultCtrlId,
                          'meterIds' : resultMeterIds,
                          'writeDates' : resultWriteDates}
                        ,function (json) {
                            var result = json.result;

                            if (result.status == "complete" || result.status == "error") {
                                //원래 동기방식으로 설정
                                $.ajaxSetup({
                                    async : false
                                });
                                Ext.Msg.hide();

                                enableCommandBtn();
                                $('#commandResult').val(result.msg);
                                clearInterval(timeId);
                                getMdisMeterByMeterIdBulkCommand();    // Bulk Meter Command 완료 후 MDIS Meter 정보를 재조회
                            }
                         });
            } else {
                $.getJSON('${ctx}/gadget/device/command/getMeterCommandResultData.do'
                        ,{'ctrlId' : resultCtrlId,
                          'meterId' : resultMeterId,
                          'writeDate' : resultWriteDate}
                        ,function (json) {
                            var result = json.result;

                            if (result.status == "complete" || result.status == "error") {
                                enableCommandBtn();
                                $('#commandResult').val(result.msg);
                                clearInterval(timeId);
                                getMdisMeterByMeterId();    // command 완료 후 MDIS Meter 정보를 재조회
                            }
                         });
            }
        }

        // disable all command button
        function disableCommandBtn() {
            $(".btn_org").removeClass("btn_org").addClass("btn_gry");
            $(".btn_gry a").attr("disabled", true);
            $("#waitResultImg").show();
        }

        // enable all command button
        function enableCommandBtn(id) {
            $(".btn_gry").removeClass("btn_gry").addClass("btn_org");
            $(".btn_org a").removeAttr("disabled");
            $("#waitResultImg").hide();
        }

        /**
         * 숫자만 입력. 정수
         */
        // 숫자 이외 문자 모두 제거
        function numericOnlyForInt(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.val();
            val = removeCharForInt(val);
            val = removeFstZeroForInt(val);
            src.val(val);
        }

        // 숫자 이외 문자 제거
        function removeCharForInt(val) {
            var num = val.replace(/[\D]/g, '');
            return num;
        }

        // 앞에 0 제거
        function removeFstZeroForInt(val) {
            var pattern = /(^0*)(\d+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        // PrepaidDeposit 조건 필드 enable/disable
        function viewCondPrepaidDeposit(src) {
            if (src.val() == "P") {
                $("#subCond").show();
                $('#sOperators').selectbox();
            } else {
                $("#sOperators option:first").attr("selected", "selected");
                $("#sPrepaidDeposit").val("");
                $("#subCond").hide();
            }
        }

        // detail information 데이터 조회
        function getMeterDetailInfo() {
            $.getJSON("${ctx}/gadget/device/getMeterDetailInfoMdis.do"
                    ,{meterId:meterId, meterType:meterType, supplierId:supplierId}
                    ,function(json) {
                        var result = json.result;
                        // clear input value
                        $("#qualitySide").html("");
                        $("#qualityActivePower").html("");
                        $("#qualityReactivePower").html("");
                        $("#qualityVol").html("");
                        $("#qualityCurrent").html("");
                        $("#qualityKva").html("");
                        $("#qualityPf").html("");
                        $("#qualityFrequencyA").html("");
                        $("#lp1Timing").html("");
                        $("#lp2Pattern").html("");
                        $("#lp2Timing").html("");
                        $("#prepaymentThreshold").html("");
                        $("#prepaidAlertLevel1").html("");
                        $("#prepaidAlertLevel2").html("");
                        $("#prepaidAlertLevel3").html("");
                        $("#prepaidAlertStart").html("");
                        $("#prepaidAlertOff").html("");
                        $("#meterDirection").html("");
                        $("#meterTime").html("");
                        $("#lcdDispContent").html("");
                        $("#meterKind").html("");
                        $("#cpuResetRam").html("");
                        $("#cpuResetRom").html("");
                        $("#wdtResetRam").html("");
                        $("#wdtResetRom").html("");
                        $("#tampBypass").html("");
                        $("#tampEarthLd").html("");
                        $("#tampReverse").html("");
                        $("#tampCoverOp").html("");
                        $("#tampFrontOp").html("");

                        if (result != null) {
                            $("#qualitySide").html(result.qualitySide);
                            $("#qualityActivePower").html(result.qualityActivePower);
                            $("#qualityReactivePower").html(result.qualityReactivePower);
                            $("#qualityVol").html(result.qualityVol);
                            $("#qualityCurrent").html(result.qualityCurrent);
                            $("#qualityKva").html(result.qualityKva);
                            $("#qualityPf").html(result.qualityPf);
                            $("#qualityFrequencyA").html(result.qualityFrequencyA);
                            $("#lp1Timing").html(result.lp1Timing);
                            $("#lp2Pattern").html(result.lp2Pattern);
                            $("#lp2Timing").html(result.lp2Timing);
                            $("#prepaymentThreshold").html(result.prepaymentThreshold);
                            $("#prepaidAlertLevel1").html(result.prepaidAlertLevel1);
                            $("#prepaidAlertLevel2").html(result.prepaidAlertLevel2);
                            $("#prepaidAlertLevel3").html(result.prepaidAlertLevel3);
                            $("#prepaidAlertStart").html(result.prepaidAlertStart);
                            $("#prepaidAlertOff").html(result.prepaidAlertOff);
                            $("#meterDirection").html(result.meterDirection);
                            $("#meterTime").html(result.meterTime);
                            $("#lcdDispContent").html(result.lcdDispContent);
                            $("#meterKind").html(result.meterKind);
                            $("#cpuResetRam").html(result.cpuResetRam);
                            $("#cpuResetRom").html(result.cpuResetRom);
                            $("#wdtResetRam").html(result.wdtResetRam);
                            $("#wdtResetRom").html(result.wdtResetRom);
                            $("#tampBypass").html(result.tampBypass);
                            $("#tampEarthLd").html(result.tampEarthLd);
                            $("#tampReverse").html(result.tampReverse);
                            $("#tampCoverOp").html(result.tampCoverOp);
                            $("#tampFrontOp").html(result.tampFrontOp);
                        }
                    });
        }

        // MdisMeter 정보를 조회(Command Button enable/disable)
        function getMdisMeterByMeterId() {
            $.getJSON('${ctx}/gadget/device/getMdisMeterByMeterId.do',
                      {meterId : meterId},
                      function(json) {
                          selectedThreshold = json.threshold;
                          selectedMdisMeter = json.mdisMeter;
                          var conditions = json.conditions;
                          var switchStatus = json.switchStatus;
                          var meterKindIsNull = false;

                          if (selectedMdisMeter != null) {
                              if (selectedMdisMeter.meterKind == null || selectedMdisMeter.meterKind == "") {
                                  meterKindIsNull = true;
                                  selectedMeterKind = "00";    // meterKind 가 null 일 경우 default : postpaid
                              } else {
                                  selectedMeterKind = selectedMdisMeter.meterKind;
                              }
                          } else {
                              meterKindIsNull = true;
                              selectedMeterKind = "00";    // meterKind 가 null 일 경우 default : postpaid
                          }

                          if (selectedMeterKind == "00") {  // postpaid 이면 button 을 disable
                              $('#getPrepaidDepositButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#getPrepaidDepositButton a').attr("disabled", true);

                              $('#setPrepaidRateButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setPrepaidRateButton a').attr("disabled", true);

                              $('#addPrepaidDepositButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#addPrepaidDepositButton a').attr("disabled", true);

                              $('#setPrepaidAlertButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setPrepaidAlertButton a').attr("disabled", true);
                          } else if (selectedMeterKind == "01") {   // prepaid 이면 button 을 enable
                              $('#getPrepaidDepositButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#getPrepaidDepositButton a').removeAttr("disabled");

                              $('#setPrepaidRateButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#setPrepaidRateButton a').removeAttr("disabled");

                              $('#addPrepaidDepositButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#addPrepaidDepositButton a').removeAttr("disabled");

                              $('#setPrepaidAlertButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#setPrepaidAlertButton a').removeAttr("disabled");
                          }

                          if (meterKindIsNull) {
                              $('#setMeterDisplayItemsButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setMeterDisplayItemsButton a').attr("disabled", true);
                          } else {
                              $('#setMeterDisplayItemsButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#setMeterDisplayItemsButton a').removeAttr("disabled");
                          }

                          if (conditions == null || conditions == "") {   // Normal : disable
                              $('#tamperingClearButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#tamperingClearButton a').attr("disabled", true);
                          } else {      // Tampering Issued : enable
                              $('#tamperingClearButton em').removeClass("btn_gry").addClass("btn_org");
                              $('#tamperingClearButton a').removeAttr("disabled");
                          }

                          if (conditions != null && conditions != "") {   // Tampering Issued 인 경우 relayOn, relayOff 모두 disable
                              $('#relayOnButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#relayOnButton a').attr("disabled", true);

                              $('#relayOffButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#relayOffButton a').attr("disabled", true);
                          } else {
                              if (switchStatus == 0) {     // Deactivation 인 경우만 relayOn enable
                                  $('#relayOnButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#relayOnButton a').removeAttr("disabled");
                              } else {
                                  $('#relayOnButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#relayOnButton a').attr("disabled", true);
                              }

                              if (switchStatus == 1) {  // Activation 인 경우만 relayOff enable
                                  $('#relayOffButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#relayOffButton a').removeAttr("disabled");
                              } else {
                                  $('#relayOffButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#relayOffButton a').attr("disabled", true);
                              }
                          }
                      }
                );
        }

        // Bulk Meter Command MdisMeter 정보를 조회(Command Button enable/disable)
        function getMdisMeterByMeterIdBulkCommand() {
            $.getJSON('${ctx}/gadget/device/getMdisMeterByMeterIdBulkCommand.do',
                      {meterIds : bulkCommandMeterIds},
                      function(json) {
                          //selectedMdisMeter = json.mdisMeter;
                          var conditions = json.conditions;
                          var switchStatus = json.switchStatus;
                          var meterKindIsNull = false;

                          var disablePostpaid = json.disablePostpaid;
                          var disableActivation = json.disableActivation;
                          var disableDeactivation = json.disableDeactivaton;
                          var disableTampering = json.disableTampering;

                          var isPostpaid = json.isPostpaid;
                          var isMeterKindNull = json.isMeterKindNull;
                          var isActivation = json.isActivation;
                          var isDeactivation = json.isDeactivation;
                          var isTampering = json.isTampering;

                          // Bulk Meter Command 에서 Add Prepaid Deposit disable
                          $('#addPrepaidDepositButton em').removeClass("btn_org").addClass("btn_gry");
                          $('#addPrepaidDepositButton a').attr("disabled", true);

                          if (disablePostpaid) {
                              $('#getPrepaidDepositButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#getPrepaidDepositButton a').attr("disabled", true);

                              $('#setPrepaidRateButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setPrepaidRateButton a').attr("disabled", true);

                              $('#setPrepaidAlertButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setPrepaidAlertButton a').attr("disabled", true);

                              $('#setMeterDisplayItemsButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#setMeterDisplayItemsButton a').attr("disabled", true);
                          } else {
                              if (isPostpaid) {
                                  selectedMeterKind = "00";    // postpaid
                                  $('#getPrepaidDepositButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#getPrepaidDepositButton a').attr("disabled", true);

                                  $('#setPrepaidRateButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#setPrepaidRateButton a').attr("disabled", true);

                                  $('#setPrepaidAlertButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#setPrepaidAlertButton a').attr("disabled", true);
                              } else {
                                  selectedMeterKind = "01";    // prepaid
                                  $('#getPrepaidDepositButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#getPrepaidDepositButton a').removeAttr("disabled");

                                  $('#setPrepaidRateButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#setPrepaidRateButton a').removeAttr("disabled");

                                  $('#setPrepaidAlertButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#setPrepaidAlertButton a').removeAttr("disabled");
                              }

                              if (isMeterKindNull) {
                                  $('#setMeterDisplayItemsButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#setMeterDisplayItemsButton a').attr("disabled", true);
                              } else {
                                  $('#setMeterDisplayItemsButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#setMeterDisplayItemsButton a').removeAttr("disabled");
                              }

                          }

                          if (disableTampering) {
                              $('#tamperingClearButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#tamperingClearButton a').attr("disabled", true);

                              $('#relayOnButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#relayOnButton a').attr("disabled", true);

                              $('#relayOffButton em').removeClass("btn_org").addClass("btn_gry");
                              $('#relayOffButton a').attr("disabled", true);
                          } else {
                              if (isTampering) {
                                  $('#tamperingClearButton em').removeClass("btn_gry").addClass("btn_org");
                                  $('#tamperingClearButton a').removeAttr("disabled");

                                  $('#relayOnButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#relayOnButton a').attr("disabled", true);

                                  $('#relayOffButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#relayOffButton a').attr("disabled", true);
                              } else {
                                  $('#tamperingClearButton em').removeClass("btn_org").addClass("btn_gry");
                                  $('#tamperingClearButton a').attr("disabled", true);

                                  if (isDeactivation) {     // Deactivation 인 경우만 relayOn enable
                                      $('#relayOnButton em').removeClass("btn_gry").addClass("btn_org");
                                      $('#relayOnButton a').removeAttr("disabled");
                                  } else {
                                      $('#relayOnButton em').removeClass("btn_org").addClass("btn_gry");
                                      $('#relayOnButton a').attr("disabled", true);
                                  }

                                  if (isActivation) {  // Activation 인 경우만 relayOff enable
                                      $('#relayOffButton em').removeClass("btn_gry").addClass("btn_org");
                                      $('#relayOffButton a').removeAttr("disabled");
                                  } else {
                                      $('#relayOffButton em').removeClass("btn_org").addClass("btn_gry");
                                      $('#relayOffButton a').attr("disabled", true);
                                  }
                              }
                          }
                      }
                );
        }

        // Meter Command 의 입력창 초기생성.(Combobox 등)
        function getMeterCommandInitData() {
            $.getJSON("${ctx}/gadget/device/getMeterCommandInitData.do"
                    ,function(json) {
                        makePrepaidRateWindow();
                        makeAddPrepaidDepositWindow();
                        makeLp1TimingWindow(json.lp1TimingArray);
                        makeLp2TimingWindow(json.lp2PatternArray, json.lp2TimingArray);
                        makeMeterDirectionWindow(json.meterDirectionArray);
                        makeMeterKindWindow(json.meterKindArray);
                        makePrepaidAlertWindow();
                        makeMeterDisplayItemsWindow(json.lcdDispScrollArray);
                        makeMeterResetWindow();
                    });
        }

        var prepaidRateWindow;
        function makePrepaidRateWindow() {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 10,
                frame : true,
                width : 300,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    xtype : 'numberfield',
                    id   : 'fpPrepaidRate',
                    allowBlank : false,
                    allowDecimals : false,
                    allowNegative : false,
                    maxValue : 65535,
                    preventMark : true
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        var prepaidRate = Ext.getCmp('fpPrepaidRate');

                        if (!prepaidRate.isValid()) {
                            // 0 ~ 65,535
                            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.outofrangerate"/>',
                                    function(){prepaidRate.focus();});
                            return;
                        }
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : prepaidRateCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'prepaidRate' : Ext.getCmp('fpPrepaidRate').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('prepaidRateWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : prepaidRateCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'prepaidRate' : Ext.getCmp('fpPrepaidRate').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('prepaidRateWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('prepaidRateWindow').hide();
                    }
                }]
            });

            prepaidRateWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetPrepaidRate"/>',
                id: 'prepaidRateWindow',
                applyTo:'prepaidRateWindowDiv',
                width:315,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var addPrepaidDepositWindow;
        function makeAddPrepaidDepositWindow() {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 100,
                frame : true,
                width : 500,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    layout : 'column',
                    items : [{
                        columnWidth:.425,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.payment"/>',
                            xtype : 'numberfield',
                            id   : 'fpPayment',
                            allowBlank : false,
                            allowDecimals : true,
                            allowNegative : false,
                            decimalPrecision : decimalPos,
                            preventMark : true,
                            anchor : '95%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.adddeposit"/>',
                            xtype : 'textfield',
                            id   : 'fpCalcAddDeposit',
                            readOnly : true,
                            fieldClass : 'x-item-disabled',
                            anchor : '95%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaiddeposit"/>',
                            xtype : 'textfield',
                            id   : 'fpCalcPrepaidDeposit',
                            readOnly : true,
                            fieldClass : 'x-item-disabled',
                            anchor : '95%'
                        }]
                    },{
                        columnWidth:.15,
                        items : [{
                            xtype : 'button',
                            id   : 'fpCalculateBtn',
                            text : '<fmt:message key="aimir.meter.command.calculate"/>',
                            handler : function() {
                                if (!Ext.getCmp("fpPayment").validate()) {
                                    Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.inputpayment"/>', function(){Ext.getCmp("fpPayment").focus();});
                                    return;
                                }
                                var prepaidRate = getUnformatDoubleNumber(Ext.getCmp("fpCalcPrepaidRate").getValue()+"");
                                if (prepaidRate != null && prepaidRate != "" && prepaidRate > 0) {
                                    var payment = getUnformatDoubleNumber(Ext.getCmp("fpPayment").getValue()+"");
                                    var addDeposit = Number(payment) / Number(prepaidRate);
                                    Ext.getCmp("fpCalcAddDeposit").setRawValue(addDeposit);
                                    getMDFormatField("fpCalcAddDeposit");

                                    var remainEng = getUnformatDoubleNumber(Ext.getCmp("fpRemainingEnergy").getValue());
                                    var prepaidDeposit = addDeposit + Number(remainEng);
                                    Ext.getCmp("fpCalcPrepaidDeposit").setValue(prepaidDeposit);
                                    getMDFormatField("fpCalcPrepaidDeposit");
                                } else {
                                    Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.invalidprepaidrate"/>');
                                }
                            },
                            anchor : '95%'
                        }]
                    },{
                        columnWidth:.425,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidrate"/>',
                            xtype : 'textfield',
                            id   : 'fpCalcPrepaidRate',
                            allowBlank : true,
                            readOnly : true,
                            fieldClass : 'x-item-disabled',
                            anchor : '95%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.remainingenergy"/>',
                            xtype : 'textfield',
                            id   : 'fpRemainingEnergy',
                            allowBlank : true,
                            readOnly : true,
                            fieldClass : 'x-item-disabled',
                            anchor : '95%'
                        }]
                    }]
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        var addDeposit = Ext.getCmp('fpCalcAddDeposit');
                        var addDepositValue = getUnformatDoubleNumber(addDeposit.getValue());

                        // validation check
                        if (addDepositValue == null || addDepositValue == "") {
                            Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.calcadddeposit"/>');
                            return;
                        }
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : addPrepaidDepositCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'addPrepaidDeposit' : addDepositValue}
                                    ,function (json) {
                                        Ext.getCmp('addPrepaidDepositWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : addPrepaidDepositCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'addPrepaidDeposit' : addDepositValue}
                                    ,function (json) {
                                        Ext.getCmp('addPrepaidDepositWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('addPrepaidDepositWindow').hide();
                    }
                }]
            });

            addPrepaidDepositWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.AddPrepaidDeposit"/>',
                id: 'addPrepaidDepositWindow',
                applyTo:'addPrepaidDepositWindowDiv',
                width:515,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var lp1TimingWindow;
        function makeLp1TimingWindow(lp1TimingArray) {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 100,
                frame : true,
                width : 300,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    fieldLabel : '<fmt:message key="aimir.meter.command.timing"/>',
                    id : 'fpLp1Timing',
                    xtype : 'combo',
                    triggerAction : 'all',
                    mode : 'local',
                    store : new Ext.data.ArrayStore({
                        id : 0,
                        fields : [
                            'id',
                            'name'
                        ],
                        data : lp1TimingArray
                    }),
                    valueField : 'id',
                    displayField : 'name',
                    editable : false
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : lp1TimingCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'lp1Timing' : Ext.getCmp('fpLp1Timing').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('lp1TimingWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : lp1TimingCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'lp1Timing' : Ext.getCmp('fpLp1Timing').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('lp1TimingWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('lp1TimingWindow').hide();
                    }
                }]
            });

            lp1TimingWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetLp1timing"/>',
                id: 'lp1TimingWindow',
                applyTo:'lp1TimingWindowDiv',
                width:315,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var lp2TimingWindow;
        function makeLp2TimingWindow(lp2PatternArray, lp2TimingArray) {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 100,
                frame : true,
                width : 500,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    layout : 'column',
                    items : [{
                        columnWidth:.5,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.pattern"/>',
                            id : 'fpLp2Pattern',
                            xtype : 'combo',
                            triggerAction : 'all',
                            mode : 'local',
                            store : new Ext.data.ArrayStore({
                                id : 0,
                                fields : [
                                    'id',
                                    'name'
                                ],
                                data : lp2PatternArray
                            }),
                            valueField : 'id',
                            displayField : 'name',
                            editable : false,
                            anchor : '95%'
                        }]
                    },{
                        columnWidth:.5,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.timing"/>',
                            id : 'fpLp2Timing',
                            xtype : 'combo',
                            triggerAction : 'all',
                            mode : 'local',
                            store : new Ext.data.ArrayStore({
                                id : 0,
                                fields : [
                                    'id',
                                    'name'
                                ],
                                data : lp2TimingArray
                            }),
                            valueField : 'id',
                            displayField : 'name',
                            editable : false,
                            anchor : '95%'
                        }]
                    }]
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        var lp2Pattern = Ext.getCmp('fpLp2Pattern');
                        var lp2Timing = Ext.getCmp('fpLp2Timing');

                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : lp2TimingCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'lp2Pattern' : lp2Pattern.getValue(),
                                      'lp2Timing' : lp2Timing.getValue()}
                                    ,function (json) {
                                        Ext.getCmp('lp2TimingWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : lp2TimingCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'lp2Pattern' : lp2Pattern.getValue(),
                                      'lp2Timing' : lp2Timing.getValue()}
                                    ,function (json) {
                                        Ext.getCmp('lp2TimingWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('lp2TimingWindow').hide();
                    }
                }]
            });

            lp2TimingWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetLp2timing"/>',
                id: 'lp2TimingWindow',
                applyTo:'lp2TimingWindowDiv',
                width:515,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var meterDirectionWindow;
        function makeMeterDirectionWindow(meterDirectionArray) {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 10,
                frame : true,
                width : 300,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    id : 'fpMeterDirection',
                    xtype : 'combo',
                    triggerAction : 'all',
                    mode : 'local',
                    store : new Ext.data.ArrayStore({
                        id : 0,
                        fields : [
                            'id',
                            'name'
                        ],
                        data : meterDirectionArray
                    }),
                    valueField : 'id',
                    displayField : 'name',
                    editable : false
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : meterDirectionCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'meterDirection' : Ext.getCmp('fpMeterDirection').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('meterDirectionWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : meterDirectionCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'meterDirection' : Ext.getCmp('fpMeterDirection').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('meterDirectionWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('meterDirectionWindow').hide();
                    }
                }]
            });

            meterDirectionWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetMeterDirection"/>',
                id: 'meterDirectionWindow',
                applyTo:'meterDirectionWindowDiv',
                width:315,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var meterKindWindow;
        function makeMeterKindWindow(meterKindArray) {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 10,
                frame : true,
                width : 300,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                defaultType : 'combo',
                items : [{
                    id : 'fpMeterKind',
                    name : 'fpMeterKind',
                    hiddenName : 'fpMeterKind',
                    triggerAction : 'all',
                    mode : 'local',
                    store : new Ext.data.ArrayStore({
                        id : 0,
                        fields : [
                            'id',
                            'name'
                        ],
                        data : meterKindArray
                    }),
                    valueField : 'id',
                    displayField : 'name',
                    editable : false
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : meterKindCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'meterKind' : Ext.getCmp('fpMeterKind').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('meterKindWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : meterKindCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'meterKind' : Ext.getCmp('fpMeterKind').getValue()}
                                    ,function (json) {
                                        Ext.getCmp('meterKindWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('meterKindWindow').hide();
                    }
                }]
            });

            meterKindWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetMeterKind"/>',
                id: 'meterKindWindow',
                applyTo:'meterKindWindowDiv',
                width:315,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var prepaidAlertWindow;
        function makePrepaidAlertWindow() {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 75,
                frame : true,
                width : 435,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    layout : 'column',
                    items : [{
                        columnWidth:.5,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidalert.level1"/>',
                            xtype : 'numberfield',
                            id   : 'fpPrepaidAlertLevel1',
                            allowBlank : true,
                            allowDecimals : false,
                            allowNegative : false,
                            maxValue : 99999999,
                            anchor : '90%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidalert.level2"/>',
                            xtype : 'numberfield',
                            id   : 'fpPrepaidAlertLevel2',
                            allowBlank : true,
                            allowDecimals : false,
                            allowNegative : false,
                            maxValue : 99999999,
                            anchor : '90%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidalert.level3"/>',
                            xtype : 'numberfield',
                            id   : 'fpPrepaidAlertLevel3',
                            allowBlank : true,
                            allowDecimals : false,
                            allowNegative : false,
                            maxValue : 99999999,
                            anchor : '90%'
                        }]
                    },{
                        columnWidth:.5,
                        layout : 'form',
                        items : [{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidalert.start"/>',
                            xtype : 'numberfield',
                            id   : 'fpPrepaidAlertStart',
                            allowBlank : true,
                            allowDecimals : false,
                            allowNegative : false,
                            maxValue : 65535,
                            anchor : '90%'
                        },{
                            fieldLabel : '<fmt:message key="aimir.meter.command.prepaidalert.off"/>',
                            xtype : 'numberfield',
                            id   : 'fpPrepaidAlertOff',
                            allowBlank : true,
                            allowDecimals : false,
                            allowNegative : false,
                            maxValue : 99999999,
                            anchor : '90%'
                        }]
                    }]
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        var alertLevel1 = Ext.getCmp('fpPrepaidAlertLevel1');
                        var alertLevel2 = Ext.getCmp('fpPrepaidAlertLevel2');
                        var alertLevel3 = Ext.getCmp('fpPrepaidAlertLevel3');
                        var alertStart = Ext.getCmp('fpPrepaidAlertStart');
                        var alertOff = Ext.getCmp('fpPrepaidAlertOff');

                        // validation check
                        if (!(alertLevel1.isValid() && alertLevel2.isValid() && alertLevel3.isValid() && alertOff.isValid())) {
                            // 0 ~ 99,999,999
                            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.outofrangedeposit"/>');
                            return;
                        } else if (!alertStart.isValid()) {
                            // 0 ~ 65,535
                            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.outofrangerate"/>');
                            return;
                        }

                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : prepaidAlertCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'prepaidAlertLevel1' : alertLevel1.getValue(),
                                      'prepaidAlertLevel2' : alertLevel2.getValue(),
                                      'prepaidAlertLevel3' : alertLevel3.getValue(),
                                      'prepaidAlertStart' : alertStart.getValue(),
                                      'prepaidAlertOff' : alertOff.getValue()}
                                    ,function (json) {
                                        Ext.getCmp('prepaidAlertWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : prepaidAlertCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'prepaidAlertLevel1' : alertLevel1.getValue(),
                                      'prepaidAlertLevel2' : alertLevel2.getValue(),
                                      'prepaidAlertLevel3' : alertLevel3.getValue(),
                                      'prepaidAlertStart' : alertStart.getValue(),
                                      'prepaidAlertOff' : alertOff.getValue()}
                                    ,function (json) {
                                        Ext.getCmp('prepaidAlertWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('prepaidAlertWindow').hide();
                    }
                }]
            });

            prepaidAlertWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetPrepaidAlert"/>',
                id: 'prepaidAlertWindow',
                applyTo:'prepaidAlertWindowDiv',
                width:449,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var meterDisplayItemsWindow;
        function makeMeterDisplayItemsWindow(lcdDispScrollArray) {
            var groupItems = [
                <c:forEach var="lcdDispContent" items="${lcdDispContents}" varStatus="status">
                    {boxLabel : '${lcdDispContent}', id : 'fpContent${status.count}'}<c:if test="${not status.last}">,</c:if>
                </c:forEach>
            ];
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 100,
                frame : true,
                width : 400,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                items : [{
                    fieldLabel : '<fmt:message key="aimir.meter.command.scroll"/>',
                    id : 'fpLcdDispScroll',
                    xtype : 'combo',
                    triggerAction : 'all',
                    mode : 'local',
                    store : new Ext.data.ArrayStore({
                        id : 0,
                        fields : [
                            'id',
                            'name'
                        ],
                        data : lcdDispScrollArray
                    }),
                    valueField : 'id',
                    displayField : 'name',
                    editable : false
                },{
                    fieldLabel : '<fmt:message key="aimir.meter.command.cyclepost"/>',
                    xtype : 'numberfield',
                    id : 'fpLcdDispCycle',
                    allowBlank : true,
                    allowDecimals : false,
                    allowNegative : false,
                    minValue : 1,
                    maxValue : 15
                },{
                    fieldLabel: '<fmt:message key="aimir.meter.command.displayitemspost"/>',
                    xtype : 'checkboxgroup',
                    id : 'fpLcdDispContentGroup',
                    // Put all controls in a single column with width 100%
                    columns: 1,
                    items: groupItems
                }],
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        // validation check
                        if (!Ext.getCmp('fpLcdDispCycle').isValid()) {
                            // 1 ~ 15
                            Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.outofrangecycle"/>');
                            return;
                        }

                        var len = Ext.getCmp("fpLcdDispContentGroup").items.length;
                        var contents = "";

                        for (var i = 0; i < len; i++) {
                            if (Ext.getCmp("fpContent" + (i+1)).getValue() == true) {
                                contents += "1";
                            } else {
                                contents += "0";
                            }
                        }

                        if (len < 16) {
                            var ln = 16 - len;

                            for (var i = 0; i < ln; i++) {
                                contents += "0";
                            }
                        }

                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : displayItemsCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId,
                                      'meterKind' : selectedMeterKind,
                                      'lcdDispScroll' : Ext.getCmp('fpLcdDispScroll').getValue(),
                                      'lcdDispCycle' : Ext.getCmp('fpLcdDispCycle').getValue(),
                                      'lcdDispContent' : contents}
                                    ,function (json) {
                                        Ext.getCmp('meterDisplayItemsWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : displayItemsCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId,
                                      'meterKind' : selectedMeterKind,
                                      'lcdDispScroll' : Ext.getCmp('fpLcdDispScroll').getValue(),
                                      'lcdDispCycle' : Ext.getCmp('fpLcdDispCycle').getValue(),
                                      'lcdDispContent' : contents}
                                    ,function (json) {
                                        Ext.getCmp('meterDisplayItemsWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('meterDisplayItemsWindow').hide();
                    }
                }]
            });

            meterDisplayItemsWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetMeterDisplayItems"/>',
                id: 'meterDisplayItemsWindow',
                applyTo:'meterDisplayItemsWindowDiv',
                width:415,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        var meterResetWindow;
        function makeMeterResetWindow() {
            var fp_Add = new Ext.FormPanel ({
                labelWidth : 270,
                frame : true,
                width : 300,
                plain : true,
                bodyStyle : 'padding:5px 5px 5px 5px',
                html : '<fmt:message key="aimir.meter.command.meterreset"/>',
                buttons : [{
                    text : '<fmt:message key="aimir.button.confirm"/>',
                    handler : function() {
                        if (isBulkCommand) {
                            //비동기 설정
                            $.ajaxSetup({
                                async : true
                            });
                            Ext.Msg.wait('Waiting for response.', 'Wait !');

                            $.getJSON('${ctx}/gadget/device/command/saveBulkMeterCommand.do'
                                    ,{'ctrlId' : meterResetCtrlId,
                                      'meterIds' : bulkCommandMeterIds,
                                      'loginId' : loginId}
                                    ,function (json) {
                                        Ext.getCmp('meterResetWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        } else {
                            $.getJSON('${ctx}/gadget/device/command/saveMeterCommand.do'
                                    ,{'ctrlId' : meterResetCtrlId,
                                      'meterId' : meterId,
                                      'loginId' : loginId}
                                    ,function (json) {
                                        Ext.getCmp('meterResetWindow').hide();
                                        var result = json.result;
                                        saveMeterCommandResult(result);
                                     });
                        }
                    }
                },{
                    text : '<fmt:message key="aimir.cancel"/>',
                    handler : function() {
                            Ext.getCmp('meterResetWindow').hide();
                    }
                }]
            });

            meterResetWindow = new Ext.Window({
                title: '<fmt:message key="aimir.meter.command.SetMeterReset"/>',
                id: 'meterResetWindow',
                applyTo:'meterResetWindowDiv',
                width:315,
                height:400,
                autoHeight: true,
                pageX : 300,
                pageY : 130,
                autoScroll: true,
                resizable:true,
                items: [fp_Add],
                closeAction:'hide'
            });
        }

        function showPrepaidRateWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                Ext.getCmp('fpPrepaidRate').setValue();
                prepaidRateWindow.show();
            }
        }

        function showAddPrepaidDepositWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                if (selectedMdisMeter != null) {
                    if (selectedMdisMeter.prepaidDeposit != null && selectedMdisMeter.prepaidDeposit != "" && selectedMdisMeter.prepaidDeposit != "null") {
                        Ext.getCmp('fpRemainingEnergy').setValue(selectedMdisMeter.prepaidDeposit);
                        getMDFormatField('fpRemainingEnergy');
                    } else {
                        Ext.getCmp('fpRemainingEnergy').setValue("");
                    }
                } else {
                    Ext.getCmp('fpRemainingEnergy').setValue("");
                }
                if (selectedThreshold != null && selectedThreshold != "" && selectedThreshold != "null") {
                    Ext.getCmp('fpCalcPrepaidRate').setValue(selectedThreshold);
                    getMDFormatField('fpCalcPrepaidRate');
                } else {
                    Ext.getCmp('fpCalcPrepaidRate').setValue("");
                }
                Ext.getCmp('fpPayment').setValue("");
                Ext.getCmp('fpCalcAddDeposit').setValue("");
                Ext.getCmp('fpCalcPrepaidDeposit').setValue("");
                addPrepaidDepositWindow.show();
            }
        }

        function showLp1TimingWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                var timing = Ext.getCmp('fpLp1Timing');
                if (isBulkCommand) {
                    timing.setValue(timing.store.getAt(0).get(timing.valueField));   // default value : 15
                } else {
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.lp1Timing != null && selectedMdisMeter.lp1Timing != "" && selectedMdisMeter.lp1Timing != "null") {
                            timing.setValue(selectedMdisMeter.lp1Timing);
                        } else {
                            timing.setValue(timing.store.getAt(0).get(timing.valueField));   // default value : 15
                        }
                    } else {
                        timing.setValue(timing.store.getAt(0).get(timing.valueField));   // default value : 15
                    }
                }
                lp1TimingWindow.show();
            }
        }

        function showLp2TimingWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                var pattern = Ext.getCmp('fpLp2Pattern');
                var timing = Ext.getCmp('fpLp2Timing');
                if (isBulkCommand) {
                    pattern.setValue(pattern.store.getAt(0).get(pattern.valueField));   // default value : Pattern A
                    timing.setValue(timing.store.getAt(2).get(timing.valueField));   // default value : 15
                } else {
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.lp2Pattern != null && selectedMdisMeter.lp2Pattern != "" && selectedMdisMeter.lp2Pattern != "null") {
                            pattern.setValue(selectedMdisMeter.lp2Pattern);
                        } else {
                            pattern.setValue(pattern.store.getAt(0).get(pattern.valueField));   // default value : Pattern A
                        }
                    } else {
                        pattern.setValue(pattern.store.getAt(0).get(pattern.valueField));   // default value : Pattern A
                    }
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.lp2Timing != null && selectedMdisMeter.lp2Timing != "" && selectedMdisMeter.lp2Timing != "null") {
                            timing.setValue(selectedMdisMeter.lp2Timing);
                        } else {
                            timing.setValue(timing.store.getAt(2).get(timing.valueField));   // default value : 15
                        }
                    } else {
                        timing.setValue(timing.store.getAt(2).get(timing.valueField));   // default value : 15
                    }
                }
                lp2TimingWindow.show();
            }
        }

        function showMeterDirectionWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                var direct = Ext.getCmp('fpMeterDirection');
                if (isBulkCommand) {
                    direct.setValue(direct.store.getAt(0).get(direct.valueField));  // default value : Active
                } else {
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.meterDirection != null && selectedMdisMeter.meterDirection != "" && selectedMdisMeter.meterDirection != "null") {
                            direct.setValue(selectedMdisMeter.meterDirection);
                        } else {
                            direct.setValue(direct.store.getAt(0).get(direct.valueField));  // default value : Active
                        }
                    } else {
                        direct.setValue(direct.store.getAt(0).get(direct.valueField));  // default value : Active
                    }
                }
                meterDirectionWindow.show();
            }
        }

        function showMeterKindWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                var kind = Ext.getCmp('fpMeterKind');
                if (isBulkCommand) {
                    kind.setValue(kind.store.getAt(0).get(kind.valueField));  // default value : postpaid
                } else {
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.meterKind != null && selectedMdisMeter.meterKind != "") {
                            kind.setValue(selectedMdisMeter.meterKind);
                        } else {
                            kind.setValue(kind.store.getAt(0).get(kind.valueField)); // default value : postpaid
                        }
                    } else {
                        kind.setValue(kind.store.getAt(0).get(kind.valueField));  // default value : postpaid
                    }
                }
                meterKindWindow.show();
            }
        }

        function showPrepaidAlertWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                if (isBulkCommand) {
                    Ext.getCmp('fpPrepaidAlertLevel1').setValue("");
                    Ext.getCmp('fpPrepaidAlertLevel2').setValue("");
                    Ext.getCmp('fpPrepaidAlertLevel3').setValue("");
                    Ext.getCmp('fpPrepaidAlertStart').setValue("");
                    Ext.getCmp('fpPrepaidAlertOff').setValue("");
                } else {
                    if (selectedMdisMeter != null) {
                        Ext.getCmp('fpPrepaidAlertLevel1').setValue(selectedMdisMeter.prepaidAlertLevel1);
                        Ext.getCmp('fpPrepaidAlertLevel2').setValue(selectedMdisMeter.prepaidAlertLevel2);
                        Ext.getCmp('fpPrepaidAlertLevel3').setValue(selectedMdisMeter.prepaidAlertLevel3);
                        Ext.getCmp('fpPrepaidAlertStart').setValue(selectedMdisMeter.prepaidAlertStart);
                        Ext.getCmp('fpPrepaidAlertOff').setValue(selectedMdisMeter.prepaidAlertOff);
                    } else {
                        Ext.getCmp('fpPrepaidAlertLevel1').setValue("");
                        Ext.getCmp('fpPrepaidAlertLevel2').setValue("");
                        Ext.getCmp('fpPrepaidAlertLevel3').setValue("");
                        Ext.getCmp('fpPrepaidAlertStart').setValue("");
                        Ext.getCmp('fpPrepaidAlertOff').setValue("");
                    }
                }
                prepaidAlertWindow.show();
            }
        }

        function showMeterDisplayItemsWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                // change label
                if (selectedMeterKind == "00") {       // postpaid
                    Ext.getCmp("fpLcdDispCycle").setFieldLabel('<fmt:message key="aimir.meter.command.cyclepost"/>');
                    Ext.getCmp("fpLcdDispContentGroup").setFieldLabel('<fmt:message key="aimir.meter.command.displayitemspost"/>');
                } else {       // prepaid
                    Ext.getCmp("fpLcdDispCycle").setFieldLabel('<fmt:message key="aimir.meter.command.cyclepre"/>');
                    Ext.getCmp("fpLcdDispContentGroup").setFieldLabel('<fmt:message key="aimir.meter.command.displayitemspre"/>');
                }

                if (isBulkCommand) {
                    //Ext.getCmp('fpLcdDispScroll').select(0);
                    var scroll = Ext.getCmp('fpLcdDispScroll');
                    scroll.setValue(scroll.store.getAt(0).get(scroll.valueField));
                    Ext.getCmp("fpLcdDispCycle").setValue();

                    var len = Ext.getCmp("fpLcdDispContentGroup").items.length;

                    for (var i = 0; i < len; i++) {
                        Ext.getCmp("fpContent" + (i+1)).setValue(false);
                    }
                } else {
                    if (selectedMdisMeter != null) {
                        if (selectedMdisMeter.lcdDispScroll != null && selectedMdisMeter.lcdDispScroll != "") {
                            Ext.getCmp('fpLcdDispScroll').setValue(selectedMdisMeter.lcdDispScroll);
                        } else {
                            var scroll = Ext.getCmp('fpLcdDispScroll');
                            scroll.setValue(scroll.store.getAt(0).get(scroll.valueField));
                        }
                        var cycle;
                        var contents;
                        if (selectedMeterKind == "00") {       // postpaid
                            cycle = selectedMdisMeter.lcdDispCyclePost;
                            contents = selectedMdisMeter.lcdDispContentPost;
                        } else {       // prepaid
                            cycle = selectedMdisMeter.lcdDispCyclePre;
                            contents = selectedMdisMeter.lcdDispContentPre;
                        }

                        Ext.getCmp("fpLcdDispCycle").setValue(cycle);

                        var len = Ext.getCmp("fpLcdDispContentGroup").items.length;

                        if (contents != null && contents != "") {
                            for (var i = 0; i < len; i++) {
                                if (contents.substring(i, (i+1)) == "1") {
                                    Ext.getCmp("fpContent" + (i+1)).setValue(true);
                                } else {
                                    Ext.getCmp("fpContent" + (i+1)).setValue(false);
                                }
                            }
                        } else {
                            for (var i = 0; i < len; i++) {
                                Ext.getCmp("fpContent" + (i+1)).setValue(false);
                            }
                        }
                    } else {
                        Ext.getCmp('fpLcdDispScroll').select(0);
                        Ext.getCmp("fpLcdDispCycle").setValue();

                        var len = Ext.getCmp("fpLcdDispContentGroup").items.length;

                        for (var i = 0; i < len; i++) {
                            Ext.getCmp("fpContent" + (i+1)).setValue(false);
                        }
                    }
                }
                meterDisplayItemsWindow.show();
            }
        }

        function showMeterResetWindow(tagId) {
            if ($("#"+tagId).attr("disabled") != "true") {
                meterResetWindow.show();
            }
        }

        // 0 ~ 99,999,999 인지 체크한다.
        function validateFunc1(cmp) {
            var isValid = false;

            if (cmp.getValue() == "") {
                isValid = true;
            } else {
                var val = getUnformatIntegerNumber(cmp.getValue());
                if (!isNaN(val)) {
                    var intVal = Number(val);

                    if (intVal >= 0 && intVal <= 99999999) {
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }
            }
            return isValid;
        }

        // 0 ~ 65,535 인지 체크한다.
        function validateFunc2(cmp) {
            var isValid = false;

            if (cmp.getValue() == "") {
                isValid = true;
            } else {
                var val = getUnformatIntegerNumber(cmp.getValue());
                if (!isNaN(val)) {
                    var intVal = Number(val);

                    if (intVal >= 0 && intVal <= 65535) {
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }
            }
            return isValid;
        }


        // focus 나갈때 MD formatting
        function getMDFormatField(id) {
            var number = Ext.getCmp(id).getValue();
            //var number = Ext.getCmp(id).getRawValue();
            if (number == null || number == "") {
                return;
            }
            $.getJSON('${ctx}/gadget/device/getMDFormatNumber.do',
                    {supplierId : supplierId,
                     number : number},
                    function(json) {
                        if(json.result != null){
                            Ext.getCmp(id).setValue(json.result);
                        }
                    }
            );
        }

        // focus 나갈때 Integer formatting
        function getTDFormatField(id) {
            var number = Ext.getCmp(id).getValue();
            if (number == null || number == "") {
                return;
            }
            $.getJSON('${ctx}/gadget/device/getTDFormatNumber.do',
                    {number : number},
                    function(json) {
                        if(json.result != null){
                            Ext.getCmp(id).setValue(json.result);
                        }
                    }
            );
        }

        // Textfield 에서 Integer 타입 unformatting
        function getUnformatIntegerField(id) {
            var number = Ext.getCmp(id).getValue();
            if (number == null || number == "") {
                return;
            }

            Ext.getCmp(id).setValue(getUnformatIntegerNumber(number));
        }

        // Textfield 에서 Double 타입 unformatting
        function getUnformatDoubleField(id) {
            var number = Ext.getCmp(id).getValue();
            if (number == null || number == "") {
                return;
            }

            Ext.getCmp(id).setValue(getUnformatDoubleNumber(number));
        }

        // Integer 타입 unformatting
        function getUnformatIntegerNumber(val) {
            if (val.indexOf(".") != -1) {   // Integer 이므로 소수점이하 제거
                val = val.substring(0, val.indexOf("."));
            }
            return val.replace(/[^\d\-]/g, "");
        }

        // Double 타입 unformatting
        function getUnformatDoubleNumber(val) {
            return val.replace(/[^\d\.\-]/g, "");
        }

        // bulk command flag
        function changeBulkCommandMode(mode) {
            if (mode != null) {
                isBulkCommand = mode;
            } else {
                isBulkCommand = !isBulkCommand;
            }
        }

        var bulkCommandMeterIds = new Array();
        // bulk command Button enable/disable
        function changeBulkCommandButton(meterIds, modelIds) {
        	var len = modelIds.length;
        	var tmpModelId = -1;
        	var isDiffModel = false;
        	for (var i = 0; i < len; i++) {
        		if (tmpModelId != -1 && tmpModelId != modelIds[i]) {
        			isDiffModel = true;
        			break;
        		}
        		tmpModelId = modelIds[i];
        	}

        	// 선택한 Meter 중 modelId 가 다를 경우
        	if (isDiffModel) {
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.noselectdiffmodel"/>', 
                        function(){flexMeterSearchGrid.cancelCheckMeter();});
        	} else {
                bulkCommandMeterIds = meterIds;
                getBulkMeterCommandRunCheck()
        	}
        }

        function getBulkCommand() {
            return isBulkCommand;
        }

        // 선택한 meter 중에 Bulk Meter Command 가 실행 중인지 체크.
        function getBulkMeterCommandRunCheck() {
        	//alert("bulkCommandMeterIds:" + bulkCommandMeterIds);
            $.getJSON('${ctx}/gadget/device/command/getBulkMeterCommandRunCheck.do',
                    {meterIds : bulkCommandMeterIds,
            	     loginId : loginId},
                    function(json) {
         	            //alert("2");
                        var isProgress = json.isProgress;

                        if (isProgress) {   // command 가 진행 중인 meter를 체크했을 경우 체크해제한다.
                        	Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.meter.command.msg.err.noselectcmdmeter"/>', 
                        			function(){flexMeterSearchGrid.cancelCheckMeter();});
                        } else {
                            getMdisMeterByMeterIdBulkCommand();
                        }
                    }
              );
        }

        // open excel download popup
        var win;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            var condArray = getCondition();

            obj.sMeterType         = condArray[0];
            obj.sMdsId             = condArray[1];
            obj.sStatus            = condArray[2];
            obj.sMcuName           = condArray[3];
            obj.sLocationId        = condArray[4];
            obj.sConsumLocationId  = condArray[5];
            obj.sVendor            = condArray[6];
            obj.sModel             = condArray[7];
            obj.sInstallStartDate  = condArray[8];
            obj.sInstallEndDate    = condArray[9];
            obj.sModemYN           = condArray[10];
            obj.sCustomerYN        = condArray[11];
            obj.sLastcommStartDate = condArray[12];
            obj.sLastcommEndDate   = condArray[13];
            obj.sOrder             = condArray[14];
            obj.sCommState         = condArray[15];
            obj.supplierId         = condArray[16];
            obj.sCmdStatus         = condArray[17];
            obj.sOperators         = condArray[18];
            obj.sPrepaidDeposit    = condArray[19];
            obj.commStatusMsg = getFmtMessageCommAlert();
            obj.headerMsg = getSearchGridColumn();

            if(win)
                win.close();
            win = window.open("${ctx}/gadget/device/meterMdisExcelDownloadPopup.do", "MeterMdisExcel", opts);
            win.opener.obj = obj;
        }

    /*]]>*/
    </script>
</head>

<body onload="init();">

<!-- Search Background (S) -->
<div class="search-bg-withouttabs">
    <div class="searchoption-container">
        <table class="searchoption wfree">
            <tr>
                <td class="withinput"><fmt:message key="aimir.metertype"/></td>
                <td class="padding-r20px">
                    <select id="sMeterType" name="select" style="width:170px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <c:forEach var="meterType" items="${meterType}">
                            <option value="${meterType.name}">${meterType.name}</option>
                        </c:forEach>
                    </select>
                </td>

                <td class="withinput"><fmt:message key="aimir.meterid"/></td>
                <td class="padding-r20px"><input type="text" id="sMdsId" style="width:90px;"/></td>

                <td class="withinput"><fmt:message key="aimir.state"/></td>
                <td>
                    <select id="sStatus" name="select" style="width:120px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <c:forEach var="meterStatus" items="${meterStatus}">
                            <option value="${meterStatus.id}">${meterStatus.name}</option>
                        </c:forEach>
                    </select>
                </td>

                <td class="withinput"><fmt:message key="aimir.meter.command.commandstatus"/></td>
                <td class="padding-r20px">
                    <select id="sCmdStatus" name="select" style="width:150px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <option value="R"><fmt:message key="aimir.meter.condition.RelayStatusOff"/></option>
                        <option value="T"><fmt:message key="aimir.meter.condition.TamperingStatusIssued"/></option>
                        <option value="P"><fmt:message key="aimir.meter.condition.PrepaidDeposit"/></option>
                    </select>
                </td>

                <td class="padding-r20px" colspan="2">
                    <span id="subCond" style="width: 130px; display:none;">
                        <select id="sOperators" name="select" style="float: left; width:50px; margin-right: 20px">
                            <!-- <option value="0">=</option>
                            <option value="1">&gt;</option>
                            <option value="2">&lt;</option> -->
                            <option value="=">=</option>
                            <option value="&gt;">&gt;</option>
                            <option value="&lt;">&lt;</option>
                        </select>&nbsp;
                        <input id="sPrepaidDeposit" type="text" style="float: right; width:60px;"/>
                    </span>
                </td>

            </tr>
            <tr>
                <td class="withinput"><fmt:message key="aimir.location"/></td>
                <td class="padding-r20px">
                    <input name="searchWord_1" id='searchWord_1' type="text" style="width:170px" />
                    <input type='hidden' id='sLocationId' value=''></input>
                </td>
                <td class="withinput"><fmt:message key="aimir.mcuid"/></td>
                <td class="padding-r20px"><input type="text" id="sMcuName" style="width:90px;"/></td>
                <td class="withinput"><fmt:message key="aimir.consumptionlocation"/></td>
                <td class="padding-r20px"><input id='sConsumLocationId' type="text"  style="width:120px" /></td>
                <td class="withinput"><fmt:message key="aimir.vendor"/></td>
                <td class="padding-r20px">
                    <select id="sVendor" name="SELECT" style="width:164px;" onChange="javascript:getDeviceModelsByVenendorId();">
                        <option value=""><fmt:message key="aimir.all"/></option>
                    </select>
                </td>
                <td class="withinput"><fmt:message key="aimir.model"/></td>
                <td class="padding-r20px">
                    <select id="sModel" name="select" style="width:120px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="withinput"><fmt:message key="aimir.installdate"/></td>
                <td class="padding-r20px">
                    <span><input id="sInstallStartDate" class="day" type="text" ></span>
                    <span><input value="~" class="between" type="text"></span>
                    <span><input id="sInstallEndDate"   class="day" type="text" ></span>
                    <input id="sInstallStartDateHidden" type="hidden" />
                    <input id="sInstallEndDateHidden"   type="hidden" />
                </td>

                <td class="withinput"><fmt:message key="aimir.modem"/></td>
                <td class="padding-r20px">
                    <select id="sModemYN" name="select" style="width:90px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <option value="Y"><fmt:message key="aimir.yes"/></option>
                        <option value="N"><fmt:message key="aimir.no"/></option>
                    </select>
                </td>
                <td class="withinput"><fmt:message key="aimir.customer"/></td>
                <td class="padding-r20px">
                    <select id="sCustomerYN" name="select" style="width:120px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <option value="Y"><fmt:message key="aimir.yes"/></option>
                        <option value="N"><fmt:message key="aimir.no"/></option>
                    </select>
                </td>
                <td class="withinput"><fmt:message key="aimir.lastcomm"/></td>
                <td colspan="2">
                    <!--span><input id="sLastcommStartDate" class="day" type="text" value="${yesterday}"></span-->
                    <span><input id="sLastcommStartDate" class="day" type="text"></span>
                    <span><input value="~" class="between" type="text"></span>
                    <!--span><input id="sLastcommEndDate" class="day" type="text" value="${today}"></span-->
                    <span><input id="sLastcommEndDate" class="day" type="text"></span>
                    <input id="sLastcommStartDateHidden" type="hidden">
                    <input id="sLastcommEndDateHidden"   type="hidden">
                </td>
                <td>
                    <em class="am_button"><a href="javascript:searchMeter()" class="on"><fmt:message key="aimir.button.search"/></a></em>
                </td>
            </tr>
        </table>
        <div class="clear">
          <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
              <div id="treeDivA"></div>
          </div>
        </div>
    </div>
</div>
<!-- Search Background (E) -->

<div id="gadget_body">
    <div class="bodyleft_meterchart">
        <div id="fcPieChartDiv">
            The chart will appear within this DIV. This text will be replaced by the chart.
        </div>
        <!-- Flex : meterSearchChart (S) -->
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="110px" id="meterSearchChartMdisEx">
                <param name="movie" value="${ctx}/flexapp/swf/meterSearchChartMdis.swf" />
                <param name='wmode' value='transparent' />
                <!--[if !IE]>-->
                <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterSearchChartMdis.swf" width="100%" height="110px" id="meterSearchChartMdisOt">
                <param name='wmode' value='transparent' />
                <!--<![endif]-->
                <p>Alternative content</p>
                <!--[if !IE]>-->
                </object>
                <!--<![endif]-->
        </object>
        <!-- Flex : meterSearchChart (E) -->

    </div>
    <div class="bodyright_meterchart">
    <ul><li class="bodyright_meterchart_leftmargin">

        <!-- 검색결과 정렬방식 (S) -->
        <!-- todo: "전체" or 아래 내용을 감싸는 div필요 -->
        <!-- :: <fmt:message key="aimir.all"/> -->

        <div id="search-default" style="height:26px; #margin-bottom:-2px;">
            <ul class="search-modem">
                <li>
                    <!-- todo: 검색종류 추가 / 검색1 -->
                    <select id="sOrder" name="select" onChange="javascript=flexMeterSearchGrid.requestSend();" Style="width:210px;">
                        <option value="1"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.desc"/></option>
                        <option value="2"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.asc"/></option>
                        <option value="3"><fmt:message key="aimir.installdate"/>  <fmt:message key="aimir.search.desc"/></option>
                        <option value="4"><fmt:message key="aimir.installdate"/>  <fmt:message key="aimir.search.asc"/></option>
                    </select>
                </li>
                <li class="space10"></li>
                <li style="width:90px;" class="gray10pt" id="meterSearchGridHeader"></li>

                <li class="withinput"><fmt:message key="aimir.filtering"/></li>
                <li>
                    <!-- todo: 검색종류 추가  / 조회조건 -->
                    <select id="sCommState" onChange="javascript=flexMeterSearchGrid.requestSend();" Style="width:170px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <option value="0"><fmt:message key="aimir.normal"/></option>
                        <option value="1"><fmt:message key="aimir.commstateYellow"/></option>
                        <option value="2"><fmt:message key="aimir.commstateRed"/></option>
                    </select>
                </li>
                <span style="float: right">
                <li> 
                    <a href="#" onclick="openExcelReport();" class="btn_blue" >
                        <span><fmt:message key="aimir.button.excel"/></span>
                    </a>
                </li>
                </span>                              

            </ul>
        </div>
        <!-- 검색결과 정렬방식 (E) -->

        <!-- Flex : meterSearchGrid (S) 260px -->
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="295px" id="meterSearchGridMdisEx">
            <param name="movie" value="${ctx}/flexapp/swf/meterSearchGridMdis.swf" />
            <param name='wmode' value='transparent' />
            <!--[if !IE]>-->
            <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterSearchGridMdis.swf" width="100%" height="295px" id="meterSearchGridMdisOt">
            <param name='wmode' value='transparent' />
            <!--<![endif]-->
            <p>Alternative content</p>
            <!--[if !IE]>-->
            </object>
            <!--<![endif]-->
        </object>
        <!-- Flex : meterSearchGrid (E) -->

    </li></ul>
    </div>

</div>

<!-- Tab 1,2,3,4,5 (S) -->
<div id="meterDetailTab">
    <ul>
        <li><a href="#general"       id="_general"><fmt:message      key="aimir.general"/></a></li>
        <!--  <li><a href="#meterInfo"     id="_meterInfo"><fmt:message    key="aimir.meter.info"/></a></li>  -->
        <li><a href="#locationInfo"  id="_locationInfo"><fmt:message key="aimir.location.info"/></a></li>
        <!-- <li><a href="#history"       id="_history"><fmt:message      key="aimir.history"/></a></li>  -->
        <li><a href="#measurement"   id="_measurement"><fmt:message  key="aimir.measurement"/></a></li>

        <li><a href="#detailInfo"   id="_detailInfo"><fmt:message  key="aimir.meter.detailinfo"/></a></li>
    </ul>

    <!-- Tab 1 : modemDetailTab (S) -->
    <div id="general" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <!-- <div class="blueline" style="height:470px;"> -->
        <div class="blueline" style="height:530px;">
        <ul class="width">
        <li class="padding">

            <!--  JSP DIV -->
            <div id="meterInfoDiv" class="bodyleft_meterinfo"></div>
            <!--  JSP Div 종료 -->
            <div class="bodyright_meterinfo">
                <ul><li id="meterInstallDiv" class="bodyright_meterinfo_leftmargin"></li></ul>
            </div>

        </li>
        </ul>

        <ul class="width margin-t10px">
            <li class="padding-bottom">


                <!-- 3rd : 명령 (S) -->
                <div class="headspace floatleft">
                    <div class="floatleft margin-r5" style="width: 100%"><LABEL class="check"><fmt:message key="aimir.instrumentation"/></LABEL></div>

                    <div id="waitResultImg" class="floatleft margin-r5" style="display:none;">
                        <img src="${ctx}/themes/images/access/grid/loading.gif"/>
                    </div>

                    <div id="onDemandButton" class="floatleft margin-r5" style="display:block">
                        <em class="btn_org">
                            <a id="OD" href="#;" onclick="saveMeterCommand(onDemandCtrlId, this.id);"><fmt:message key="aimir.meter.command.OnDemandMetering"/></a>
                        </em>
                    </div>

                    <div id="relayStatusButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="RS" href="#;" onclick="saveMeterCommand(relayStatusCtrlId, this.id);"><fmt:message key="aimir.meter.command.RelayStatus"/><!-- Relay Status --></a>
                        </em>
                    </div>

                    <div id="relayOffButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="RF" href="#;" onclick="saveMeterCommand(relayOffCtrlId, this.id);"><fmt:message key="aimir.meter.command.RelayOff"/><!-- Relay Off --></a>
                        </em>
                    </div>

                    <div id="relayOnButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="RN" href="#;" onclick="saveMeterCommand(relayOnCtrlId, this.id);"><fmt:message key="aimir.meter.command.RelayOn"/><!-- Relay On --></a>
                        </em>
                    </div>

                    <div id="tamperingClearButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="TC" href="#;" onclick="saveMeterCommand(clsTamperingCtrlId, this.id);"><fmt:message key="aimir.meter.command.ClearTamperingStatus"/><!-- Tampering Clear --></a>
                        </em>
                    </div>

                    <div id="getTamperingButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="TS" href="#;" onclick="saveMeterCommand(getTamperingCtrlId, this.id);"><fmt:message key="aimir.meter.command.GetTamperingStatus"/><!-- Get Tampering --></a>
                        </em>
                    </div>

                    <div id="meterTimeSyncButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="TMS" href="#;" onclick="saveMeterCommand(timeSyncCtrlId, this.id);"><fmt:message key="aimir.meter.command.TimeSettings"/><!-- Time Sync --></a>
                        </em>
                    </div>

                    <div id="getSWVerButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="SW" href="#;" onclick="saveMeterCommand(swVerCtrlId, this.id);"><fmt:message key="aimir.meter.command.GetSWVersion"/><!-- Get SW Ver --></a>
                        </em>
                    </div>

                    <div id="getPrepaidDepositButton" class="floatleft margin-r5" style="display:block;">
                        <em class="btn_org">
                            <a id="PG" href="#;" onclick="saveMeterCommand(getPrepaidDepositCtrlId, this.id);"><fmt:message key="aimir.meter.command.GetPrepaidDeposit"/><!-- Get prepaid deposit --></a>
                        </em>
                    </div>

                    <div style="width: 100%; float: left; padding: 2px;"></div>

                    <div id="setPrepaidRateButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="PS" href="#;" onclick="showPrepaidRateWindow(this.id);">* <fmt:message key="aimir.meter.command.SetPrepaidRate"/><!-- Set prepaid rate --></a>
                            </em>
                        </div>
                    </div>

                    <div id="addPrepaidDepositButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="PA" href="#;" onclick="showAddPrepaidDepositWindow(this.id);">* <fmt:message key="aimir.meter.command.AddPrepaidDeposit"/><!-- Add prepaid deposit --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setLp1TimingButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="LP1" href="#;" onclick="showLp1TimingWindow(this.id);">* <fmt:message key="aimir.meter.command.SetLp1timing"/><!-- Set LP1 timing --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setLp2TimingButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="LP2" href="#;" onclick="showLp2TimingWindow(this.id);">* <fmt:message key="aimir.meter.command.SetLp2timing"/><!-- Set LP2 timing --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setMeterDirectionButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="MD" href="#;" onclick="showMeterDirectionWindow(this.id);">* <fmt:message key="aimir.meter.command.SetMeterDirection"/><!-- Set Meter Direction --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setMeterKindButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="MK" href="#;" onclick="showMeterKindWindow(this.id);">* <fmt:message key="aimir.meter.command.SetMeterKind"/><!-- Set Meter Kind --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setPrepaidAlertButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_gry">
                                <a id="PAS" href="#;" onclick="showPrepaidAlertWindow(this.id);">* <fmt:message key="aimir.meter.command.SetPrepaidAlert"/><!-- Set Meter Kind --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setMeterDisplayItemsButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="MDI" href="#;" onclick="showMeterDisplayItemsWindow(this.id);">* <fmt:message key="aimir.meter.command.SetMeterDisplayItems"/><!-- Set Meter Kind --></a>
                            </em>
                        </div>
                    </div>

                    <div id="setMeterResetButton" class="floatleft margin-r5" style="display:block;">
                        <div class="floatleft">
                            <em class="btn_org">
                                <a id="MR" href="#;" onclick="showMeterResetWindow(this.id);">* <fmt:message key="aimir.meter.command.SetMeterReset"/><!-- Set Meter Kind --></a>
                            </em>
                        </div>
                    </div>

                </div>

                <!-- <div class="meterinfo-textarea clear">
                   <ul><li><textarea id="commandResult" readonly>Result</textarea></li></ul>
                </div> -->
                <div style="width: 100%;">
                   <ul><li><textarea id="commandResult" style="height: 73px; width: 100%;" readonly>Result</textarea></li></ul>
                </div>

                <div id="detail_dialog" class="mvm-popwin-iframe-outer" title="ondemand">
                    <div id="detail_view" style="overflow-y:auto" ></div><!--scrolling="yes"-->
                    <!--
                    <iframe id="detail_view" src="" frameborder="0"  class="mvm-popwin-iframe" marginwidth="0" marginheight="0" scrolling="no">
                    </iframe>
                     -->
                </div>
                <!-- 3rd : 명령 (E) -->
            </li>
        </ul>
    </div>
    <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 1 : modemDetailTab (E) -->


    <!-- Tab 2 : meterInfo (S) -->
    <div id="meterInfo" class="tabcontentsbox" style="display:none;">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:470px;">
        <ul class="width">
        <li class="padding">

            <!-- 기본정보 -->
            <div class="headspace clear"><label class="check"><fmt:message key="aimir.button.basicinfo"/></label></div>
            <div class="meterinfo-textarea">
               <ul><li><textarea>미정의</textarea></li></ul>
            </div>

            <!-- 검침정보 -->
            <div class="headspace_2ndline2 clear-width100 floatleft"><label class="check"><fmt:message key="aimir.button.metering"/></label></div>
            <div class="meterinfo-textarea">
               <ul><li><textarea>미정의</textarea></li></ul>
            </div>

            <!-- 기타정보 -->
            <div class="headspace_2ndline2 clear-width100 floatleft"><label class="check"><fmt:message key="aimir.etc"/></label></div>
            <div class="meterinfo-textarea">
               <ul><li><textarea>미정의</textarea></li></ul>
            </div>


        </li></ul>
        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 2 : meterInfo (E) -->




    <!-- Tab 3 : locationInfo (S) -->
    <div id="locationInfo" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:400px;">
        <ul class="width">
        <li class="padding">


            <div class="map_box">
                <div id="map-canvas" class="border-blue-3px" style="position: absolute;"></div>
            </div>

            <div class="coordinate_box">
                <div class="width_auto padding20px"  style="display:table-cell;">
                <form id="meterLocForm">
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
                        <td rowspan="2"  valign="bottom"><textarea name="customer_num3" id="sysLocation" style="width:260px; height:50px;">${sysLocation}</textarea></td>
                      </tr>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.altitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioZ" value="${gpioZ}"/></td>
                      </tr>
                      <tr>
                        <td colspan="2" align="right" class="padding-r25px"><em class="am_button"><a href="javascript:updateMeterLoc()" class="on"><fmt:message key="aimir.mcu.coordinate.update"/></a></em></td>
                        <td align="right">
                        <em class="am_button"><a href="javascript:updateMeterAddress()" class="on"><fmt:message key="aimir.mcu.adress.update"/></a></em>
                        <em class="am_button"><a href="javascript:getGeoCoding();" class="on"><fmt:message key="aimir.address"/>-<fmt:message key="aimir.mcu.coordinate"/></a></em>
                        </td>
                      </tr>
                     </table>
                   </form>
                </div>
             </div>

        </li></ul>
        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 3 : locationInfo (E) -->

    <!-- Tab 4 : history (S) -->
    <div id="history" class="tabcontentsbox" style="display:none;">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:590px;">

            <div class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row0">
                <div class="dayoptions-bt searchoption-container">
                    <%@ include file="/gadget/commonDateTabButtonType2.jsp"%>
                </div>
            </div>

        <ul class="width" style="padding-top:0;">
        <li class="padding">
            <div class="flexlist">
                <div id="fcLogChartDiv" style="margin:0 0 5px 0">
                    The chart will appear within this DIV. This text will be replaced by the chart.
                </div>
          </div>

            <div class="flexlist">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="250px" id="meterLogGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/meterLogGridMdis.swf" />
                    <param name="wmode" value="opaque" />
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterLogGridMdis.swf" width="100%" height="250px" id="meterLogGridOt">
                    <param name="wmode" value="opaque" />
                    <!--<![endif]-->
                    <p>Alternative content</p>
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>


        </li></ul>
        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 4 : history (E) -->

    <!-- Tab 5 : measurement (S) -->
    <div id="measurement" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:590px;">

            <div class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row0">
                <div class="dayoptions-bt searchoption-container">
                    <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
                </div>
            </div>

        <ul class="width" style="padding-top:0;">
        <li class="padding">

            <div class="flexlist">
                <div id="fcMeasureChartDiv" style="margin:0 0 5px 0">
                    The chart will appear within this DIV. This text will be replaced by the chart.
                </div>
            </div>

            <div class="flexlist">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="260px" id="meterMeteringByMeterGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/meterMeteringByMeterGridMdis.swf" />
                    <param name="wmode" value="opaque" />
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterMeteringByMeterGridMdis.swf" width="100%" height="260px" id="meterMeteringByMeterGridOt">
                    <param name="wmode" value="opaque" />
                    <!--<![endif]-->
                    <p>Alternative content</p>
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>

        </li>
        </ul>
        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 5 : measurement (E) -->

    <!-- Tab 6 : meterDetailInfoTab (S) -->
    <div id="detailInfo" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" >
        <ul class="width">
        <li class="padding">

            <!-- 상세정보 (S) -->

            <div class="headspace"><label class="check"><fmt:message key="aimir.meter.detailinfo"/></label></div>
            <div class="box-bluegradation-modem">
                <ul><li id="modemInfoDiv" class="box-bluegradation-modem-padding">
                    <table class="wfree verticalalign-middle" style="width: 100%;">
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.measurementside"/></th>
                        <td id="qualitySide" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.activepower.kw"/></th>
                        <td id="qualityActivePower" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.reactivepower.var"/></th>
                        <td id="qualityReactivePower" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.voltage.v"/></th>
                        <td id="qualityVol" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.current.a"/></th>
                        <td id="qualityCurrent" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.apparentpower.va"/></th>
                        <td id="qualityKva" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.powerfactor"/></th>
                        <td id="qualityPf" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.frequency"/></th>
                        <td id="qualityFrequencyA" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.lp1timing"/></th>
                        <td id="lp1Timing" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.lp2pattern"/></th>
                        <td id="lp2Pattern" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.lp2timing"/></th>
                        <td id="lp2Timing" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidrate"/></th>
                        <td id="prepaymentThreshold" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidalert.level1"/></th>
                        <td id="prepaidAlertLevel1" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidalert.level2"/></th>
                        <td id="prepaidAlertLevel2" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidalert.level3"/></th>
                        <td id="prepaidAlertLevel3" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidalert.start"/></th>
                        <td id="prepaidAlertStart" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.prepaidalert.off"/></th>
                        <td id="prepaidAlertOff" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.meteringdirection"/></th>
                        <td id="meterDirection" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.metertime"/></th>
                        <td id="meterTime" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.lcddpcontents"/></th>
                        <td id="lcdDispContent" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.meteringkind"/></th>
                        <td id="meterKind" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.cpuresetram"/></th>
                        <td id="cpuResetRam" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.cpuresetrom"/></th>
                        <td id="cpuResetRom" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.wdtresetram"/></th>
                        <td id="wdtResetRam" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.wdtresetrom"/></th>
                        <td id="wdtResetRom" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.bypassstatus"/></th>
                        <td id="tampBypass" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.earthload"/></th>
                        <td id="tampEarthLd" style="width:19%;"></td>
                    </tr>
                    <tr>
                        <th style="width:14%;"><fmt:message key="aimir.meter.reverse"/></th>
                        <td id="tampReverse" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.terminalcoveropen"/></th>
                        <td id="tampCoverOp" class="padding-r20px" style="width:19%;"></td>
                        <th style="width:14%;"><fmt:message key="aimir.meter.frontcoveropen"/></th>
                        <td id="tampFrontOp" style="width:19%;"></td>
                    </tr>

                    </table>

                </li></ul>
            </div>

            <!-- 상세정보 (E) -->

        </li>
        </ul>

        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- Tab 6 : meterDetailInfoTab (E) -->

</div>
<!-- Meter Command Parameter Popup Div -->
<div id="prepaidRateWindowDiv"></div>
<div id="addPrepaidDepositWindowDiv"></div>
<div id="lp1TimingWindowDiv"></div>
<div id="lp2TimingWindowDiv"></div>
<div id="meterDirectionWindowDiv"></div>
<div id="meterKindWindowDiv"></div>
<div id="prepaidAlertWindowDiv"></div>
<div id="meterDisplayItemsWindowDiv"></div>
<div id="meterResetWindowDiv"></div>
</body>
</html>