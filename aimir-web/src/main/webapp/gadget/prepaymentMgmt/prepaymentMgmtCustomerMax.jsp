<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>

<%@ include file="/gadget/system/preLoading.jsp"%>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var operatorId = "${operatorId}";
    var serviceType = "${serviceType}";
    var guageChartDataXml;
    var isNotService;
 
    $(document).ready(function(){
        hide();
        //Tab 클릭 이벤트 정의
        $(function() { $("#chargeSettingTab").bind("click", function(event) { displayDivTab("chargeSettingTab"); } ); });
        $(function() { $("#customerTariffTab").bind("click", function(event) { displayDivTab("customerTariffTab"); } ); });

        $.ajaxSetup({ async: false });
   
        getContract();

        if(isNotService) {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();

            if(serviceType == "3.1") {
                $("#img_isNotService_house").addClass("img_isNotService_elec_house");
            } else if(serviceType == "3.2") {
                $("#img_isNotService_house").addClass("img_isNotService_water_house");
            } else if(serviceType == "3.3") {
                $("#img_isNotService_house").addClass("img_isNotService_gas_house");
            }
            return;
        } else { // 해당 가젯에 대한 권한이 있을때
            $("#isNotService").hide();
        }

        $.ajaxSetup({ async: true });
        searchDivInit();
        drawDetailWin();
        
        $("#notifyPeriod").selectbox();
        $("#intervalDaily").selectbox();
        $("#intervalWeekly").selectbox();
    });

    var divTabArray = ["chargeSettingTab", "customerTariffTab"];
    var divTabArrayLength = divTabArray.length;

    var displayDivTab = function(_currentDivTab) {

        for ( var i = 0 ; i < divTabArrayLength ; i++) {

            if (_currentDivTab == divTabArray[i]) {

                $("#" + divTabArray[i] + "Div").show();
                $("#" + divTabArray[i]).addClass("tabcurrent");

                searchTab(i);
            } else {
                $("#" + divTabArray[i] + "Div").hide();
                $("#" + divTabArray[i]).removeClass("tabcurrent");
            }
        }
    };

    // 탭 선택시, 관련탭에 해당하는 정보 표시
    var searchTab = function(tabSeq) {
        if (tabSeq == null) {
            for (var i = 0; i < divTabArrayLength; i++) {
                if ($("#" + divTabArray[i] + "Div").is(":visible")) {
                    tabSeq = i;
                }
            }
        }

        switch (tabSeq) {
            case 0 :
                getAllHours();
                getChargeSettingTabInfo();
                getChargeHistoryData();
                break;

            case 1 :
                getCustomerTariff();
                getExptUsableDayList();
                break;

            default :
                break;
        }
    };

    var getContract = function() {

        var params = {
                "operatorId" : operatorId,
                "serviceType" : serviceType
        };

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getContract.do",
                params,
                function(result) {

                    isNotService = result.isNotService;

		            if(isNotService) {  // 해당 가젯에 대한 권한이 없을때
		                return;
		            }
                    var contracts = result.contracts;

                    // 계약정보 콤보박스 생성
                    $("#contractNumber").pureSelect(contracts);
                    $("#contractNumber").selectbox();

                    // 계약정보 콤보 박스 선택 이벤트
                    $("#contractNumber").bind("change", function(event) { searchTab(); } );
                    displayDivTab("chargeSettingTab");
                }
            );
    };

    // 잔액통보주기 시간 selectbox 데이터를 조회한다.
    var getAllHours = function() {
        
        var params = {"contractNumber" : $("#contractNumber").val()};
    
        $.getJSON("${ctx}/gadget/prepaymentMgmt/getAllHours.do",
                params,
                function(result) {
                    var hours = result.hours;
    
                    // 시간 콤보박스 생성
                    $("#notifyHour").pureSelect(hours);
                    $("#notifyHour").selectbox();
                }
            );
    };

    // 날짜조회조건 생성
    var searchDivInit = function() {
        $(function() { $("#fromYearCombo").bind("change", function(event) { getMonthCombo("from", ""); } ); });
        $(function() { $("#toYearCombo").bind("change", function(event) { getMonthCombo("to", ""); } ); });
        $(function() { $("#fromMonthCombo").bind("change", function(event) { getSearchDate("from"); } ); });
        $(function() { $("#toMonthCombo").bind("change", function(event) { getSearchDate("to"); } ); });

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getYear.do"
                ,{}
                ,function(json) {
                    var fstYear = json.fstYear;
                    var lstYear = json.lstYear;
                    var fromYear = json.fromYear;
                    var toYear = json.toYear;
                    var fromMonth = json.fromMonth;
                    var toMonth = json.toMonth;
                     
                    $("#fromYearCombo").numericOptions({from:fstYear,to:lstYear,selectedIndex:0});
                    $("#fromYearCombo").val(fromYear);
                    $("#fromYearCombo").selectbox();
                    getMonthCombo("from", fromMonth); // 월 selectBox 내용을 채운다.

                    $("#toYearCombo").numericOptions({from:fstYear,to:lstYear,selectedIndex:0});
                    $("#toYearCombo").val(toYear);
                    $("#toYearCombo").selectbox();
                    getMonthCombo("to", toMonth); // 월 selectBox 내용을 채운다.
                });
    };

    var getMonthCombo = function(fromto, monthVal) {
        var year = null;

        if (fromto == "from") {
            year = $("#fromYearCombo").val();
        } else {
            year = $("#toYearCombo").val();
        }

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getMonth.do"
                ,{"year" : year}
                ,function(json) {
                    var lstMonth = json.lstMonth;
                    var monthObj = null;

                    if (fromto =="from") {
                        monthObj = $("#fromMonthCombo");
                    } else {
                        monthObj = $("#toMonthCombo");
                    }

                    var prevMonth = monthObj.val();
                    
                    monthObj.emptySelect();
                    
                    if( prevMonth == "" || prevMonth == null || Number(prevMonth) > Number(lstMonth) ) {
                        prevMonth = lstMonth;
                    }
                    
                    var idx = Number(prevMonth) - 1;
                    
                    monthObj.numericOptions({from:1,to:lstMonth,selectedIndex:idx});

                    if( monthVal != null && monthVal != "" ){
                        monthObj.val(monthVal);
                    }
                    
                    monthObj.selectbox();

                    getSearchDate(fromto);
                });
    };

    var getSearchDate = function(fromto) {

        if (fromto == "from") {
            var year = $("#fromYearCombo").val();
            var month = (Number($("#fromMonthCombo").val()) < 10) ? "0" + $("#fromMonthCombo").val() : $("#fromMonthCombo").val();

            $("#searchStartMonth").val(year + "" + month);
        } else {
            var year = $("#toYearCombo").val();
            var month = (Number($("#toMonthCombo").val()) < 10) ? "0" + $("#toMonthCombo").val() : $("#toMonthCombo").val();

            $("#searchEndMonth").val(year + "" + month);
        }
    };

    // 충전 및 통보설정 조회
    function getChargeSettingTabInfo(){
        emergePre();

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getChargeSetting.do"
                ,{contractNumber : $('#contractNumber').val(),
                    serviceType : serviceType}
                ,function(json) {
                    hide();
                    var chargeInfo = json.result.chargeInfo;
                    var notifySetting = json.result.notifySetting;

                    getChargeInfo(chargeInfo);
                    getNotifySetting(notifySetting);
                });
    }

    function getNotificationTabInfo(){
        emergePre();

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getBalanceNotifySetting.do"
                ,{contractNumber : $('#contractNumber').val()}
                ,function(json) {
                    var notifySetting = json.result;
                    getNotifySetting(notifySetting);

                    hide();
                });
    }

    // 충전정보
    function getChargeInfo(chargeInfo){
        var currentBalance = chargeInfo.currentCredit;
        var balance = chargeInfo.balance;
        var lastTokenDate = chargeInfo.lastTokenDate;
        var nextTokenDate = chargeInfo.nextTokenDate;
        var emergencyCreditStartTime = chargeInfo.emergencyCreditStartTime;
        var emergencyCreditAutoChange = chargeInfo.emergencyCreditAutoChange;

        var currentCreditView = chargeInfo.currentCreditView;
        var balanceView = chargeInfo.balanceView;
        var lastTokenDateView = chargeInfo.lastTokenDateView;
        var nextTokenDateView = chargeInfo.nextTokenDateView;
        var emergencyCreditStartTimeView = chargeInfo.emergencyCreditStartTimeView;
        var emergencyCreditMaxDuration = chargeInfo.emergencyCreditMaxDuration;
        var limitDateView = chargeInfo.limitDateView;
        var limitDuration = chargeInfo.limitDuration;

        var threshold1 = chargeInfo.threshold1;
        var threshold2 = chargeInfo.threshold2;
        var limit = balance;
        var currentValue = currentBalance;

        if (lastTokenDate == null || lastTokenDate == "") {
            currentBalance = 0;
            balance = 0;
            currentCreditView = 0;
            balanceView = 0;
            threshold1 = 0.2;
            threshold2 = 0.5;
            limit = 1;
            currentValue = 0;
        }

        $("#currentBalance").val(currentBalance);
        $("#balance").val(balance);
        $("#lastTokenDate").val(lastTokenDate);
        $("#nextTokenDate").val(nextTokenDate);
        $("#emergencyCreditStartTime").val(emergencyCreditStartTime);
        $("#emergencyCreditAutoChange").val(emergencyCreditAutoChange);
        $("#currentBalanceView").text(currentCreditView);
        $("#balanceView").text(balanceView);

        $("#balanceView").text(currentCreditView);
        $("#lastTokenDateView").text(lastTokenDateView);
        //$("#nextTokenDateView").text(nextTokenDateView);
        $("#nextTokenDateView").text("2011/12/15");
        //$("#emergencyCreditStartTimeView").text(emergencyCreditStartTimeView);
        //$("#emergencyCreditMaxDurationView").text(emergencyCreditMaxDurationView);
        $("#limitDateView").text(limitDateView);
        $("#limitDuration").text(limitDuration);

        $("#threshold1").val(threshold1);
        $("#threshold2").val(threshold2);

        // 게이지차트 조립 "+Number($('#balance').val())+"
        guageChartDataXml = " <chart lowerLimit='0' upperLimit='" + limit + "' showGaugeBorder='0' " +
                            " gaugeOuterRadius='100%' gaugeInnerRadius='60%' pivotRadius='2' " +
                            " lowerLimitDisplay='0' "  +
                            " bgColor='ffffff' " +
                            " baseFont='dotum' baseFontSize='12' baseFontColor='#434343' " +
                            " gaugeStartAngle='180' gaugeEndAngle='0' " +
                            " palette='5' tickValueDistance='20' showValue='1' paletteThemeColor='575757' " +
                            " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' showBorder='0' chartRightMargin ='50' " +
                            " showPivotBorder='1' pivotBorderThickness='3' pivotBorderColor='CCCCCC' pivotBorderAlpha='0' > ";
        guageChartDataXml += " <colorRange>" +
                             " <color minValue='0' maxValue='" + threshold1 + "' code=' "+ fChartColor_Step3[2] +" '/> " +
                             " <color minValue='" + threshold1 + "' maxValue='" + threshold2 + "' code=' "+ fChartColor_Step3[1] +" '/> " +
                             " <color minValue='" + threshold2 + "' maxValue='" + limit + "' code='"+ fChartColor_Step3[0] +" '/> " +
                             " </colorRange> ";
        guageChartDataXml += " <dials>" +
                             " <dial value='" + currentValue + "' rearExtension='10'/> " +
                             " </dials>";
        guageChartDataXml += " </chart>";

        fwGuageChartRender();

        hide();
    }

    // window resize event
    $(window).resize(function() {
        fwGuageChartRender();
        getChargeHistoryData();
        customerTariffRender();
        exptUsableDayRender();
    });

    function fwGuageChartRender(){
        if($('#chargeSettingTabDiv').is(':visible')) {
            guageDivWidth  = $('#guageChartDiv').width();
            var guageChart = new FusionCharts("${ctx}/flexapp/swf/fusionwidgets/AngularGauge.swf", "myChartId", guageDivWidth, "140", "0", "0");

            guageChart.setDataXML(guageChartDataXml);
            guageChart.setTransparent("transparent");
            guageChart.render("guageChartDiv");
        }
    }

    // 통보설정
    function getNotifySetting(notifySetting){
        var notificationPeriod         = notifySetting.notificationPeriod;
        var notificationInterval       = notifySetting.notificationInterval;
        var notificationTime           = notifySetting.notificationTime;
        var notificationWeeklyMon      = notifySetting.notificationWeeklyMon;
        var notificationWeeklyTue      = notifySetting.notificationWeeklyTue;
        var notificationWeeklyWed      = notifySetting.notificationWeeklyWed;
        var notificationWeeklyThu      = notifySetting.notificationWeeklyThu;
        var notificationWeeklyFri      = notifySetting.notificationWeeklyFri;
        var notificationWeeklySat      = notifySetting.notificationWeeklySat;
        var notificationWeeklySun      = notifySetting.notificationWeeklySun;
        var lastNotificationDate       = notifySetting.lastNotificationDate;
        var prepaymentThreshold        = notifySetting.prepaymentThreshold;
        var prepaymentThresholdView    = notifySetting.prepaymentThresholdView;
        var emergencyCreditAutoChange  = notifySetting.emergencyCreditAutoChange;
        var emergencyCreditStartTime   = notifySetting.emergencyCreditStartTime;
        var emergencyCreditMaxDuration = notifySetting.emergencyCreditMaxDuration;
        var creditType                 = notifySetting.creditType;
        var devices                    = notifySetting.devices;

        if (notificationPeriod == null || notificationPeriod == "") {
            $("#notifyPeriod option:eq(0)").attr("selected", "true");
        } else {
            $("#notifyPeriod").val(notificationPeriod);
        }

        $("#interval").val(notificationInterval);

        if (notificationTime == null || notificationTime == "") {
            $("#notifyHour option:eq(0)").attr("selected", "true");
        } else {
            $("#notifyHour").val(notificationTime);
        }

        if($("#notifyPeriod").val() == "2") {
            $("#dayofweek").show();
            $("#intervalDailyDiv").hide();
            $("#intervalWeeklyDiv").show();

            if (notificationInterval == null || notificationInterval == "") {
                $("#intervalWeekly option:eq(0)").attr("selected", "true");
            } else {
                $("#intervalWeekly").val(notificationInterval);
            }

            $("#mon").attr("checked", notificationWeeklyMon);
            $("#tue").attr("checked", notificationWeeklyTue);
            $("#wed").attr("checked", notificationWeeklyWed);
            $("#thu").attr("checked", notificationWeeklyThu);
            $("#fri").attr("checked", notificationWeeklyFri);
            $("#sat").attr("checked", notificationWeeklySat);
            $("#sun").attr("checked", notificationWeeklySun);

            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
        } else {
            $("#dayofweek").hide();
            $("#intervalDailyDiv").show();
            $("#intervalWeeklyDiv").hide();

            if (notificationInterval == null || notificationInterval == "") {
                $("#intervalDaily option:eq(0)").attr("selected", "true");
            } else {
                $("#intervalDaily").val(notificationInterval);
            }

            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
        }

        $("#notifyPeriod").selectbox();
        $("#intervalDaily").selectbox();
        $("#intervalWeekly").selectbox();
        $("#notifyHour").selectbox();

        $("#threshold").val(prepaymentThreshold);
        $("#thresholdView").val(prepaymentThresholdView);
        $("#duration").text(emergencyCreditMaxDuration);

        hide();
    }

    // 선택된 주기에 따라 요일항목 enable/disable
    function controlDayofWeek() {
        var periodValue = $("#notifyPeriod").val();
        if (periodValue == "2") {
            $("#dayofweek").show();
            $("#intervalDailyDiv").hide();
            $("#intervalWeeklyDiv").show();
            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
            $("#intervalDaily option:eq(0)").attr("selected", "true");
            $("#intervalDaily").selectbox();
        } else {
            $("#mon").attr("checked", false);
            $("#tue").attr("checked", false);
            $("#wed").attr("checked", false);
            $("#thu").attr("checked", false);
            $("#fri").attr("checked", false);
            $("#sat").attr("checked", false);
            $("#sun").attr("checked", false);
            $("#dayofweek").hide();
            $("#intervalWeeklyDiv").hide();
            $("#intervalDailyDiv").show();
            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
            $("#intervalWeekly option:eq(0)").attr("selected", "true");
            $("#intervalWeekly").selectbox();
        }
    }

    // 숫자 이외 문자 제거
    function removeChar(val) {
        var num = val.replace(/[\D]/g, '');
        return num;
    }

    // 앞에 0 제거
    function removeFstZero(val) {
        var pattern = /(^0*)(\d+$)/g;

        if (pattern.test(val)) {
            val = val.replace(pattern, '$2');
        }
        return val;
    }

    // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
    function removeComma(ev, src) {
        var evCode = (window.netscape) ? ev.which : event.keyCode;
        if (evCode >= 37 && evCode <= 40) return;
        var val = src.value;
        val = removeChar(val);
        val = removeFstZero(val);
        src.value = val;
        src.focus();
    }

    // inputbox에서 focus가 나가면 comma추가
    function addComma(src) {
        var pattern = /(^-?[0-9]+)([0-9]{3})/;
        var strVal = src.value;

        while(pattern.test(strVal)) {
            strVal = strVal.replace(pattern, '$1,$2');
        }

        src.value = strVal;
    }

    // 저장확인창
    function saveNotifySettingConfirm() {
        Ext.MessageBox.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>"
                             , function(btn) {
                                   if (btn == 'yes') {
                                       saveNotifySetting();
                                   }
                               });
    }
    
    // 통보설정을 저장
    function saveNotifySetting() {

        if ($("#notifyPeriod").val() == "2") {
            $("#interval").val($("#intervalWeekly").val());
        } else {
            $("#interval").val($("#intervalDaily").val());
        }

        if ($("#thresholdView").val() == "") {
            $("#threshold").val(0);
        } else {
            $("#threshold").val(removeChar($("#thresholdView").val()));
        }

        $.post("${ctx}/gadget/prepaymentMgmt/updateBalanceNotifySetting.do",
              {contractNumber : $('#contractNumber').val(),
               operatorId     : operatorId,
               serviceType    : serviceType,
               period         : $("#notifyPeriod").val(),
               interval       : $("#interval").val(),
               hour           : $("#notifyHour").val(),
               threshold      : $("#threshold").val(),
               mon            : $("#mon").is(":checked"),
               tue            : $("#tue").is(":checked"),
               wed            : $("#wed").is(":checked"),
               thu            : $("#thu").is(":checked"),
               fri            : $("#fri").is(":checked"),
               sat            : $("#sat").is(":checked"),
               sun            : $("#sun").is(":checked")
               },
              save_callback
       );
    }

    //저장 후 콜백
    function save_callback(json, textStatus) {
        if (json.status == "success") {
            Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationTabInfo);
        } else {
            Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
        }
    }

    /* 충전이력 리스트 START */
    var chargeHistoryGridOn = false;
    var chargeHistoryGrid;
    var chargeHistoryColModel;
    var getChargeHistoryData = function() {
        var width = $("#chargeHistory").width();
        var mxwidth = 860;
        // window size 변경 시 grid resize
        /*if (arguments.length && arguments[0] == "resize") {
            chargeHistoryGrid.setWidth(width);
            return;
        }*/

        var chargeHistoryStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 20}},
            url: "${ctx}/gadget/prepaymentMgmt/getChargeHistory.do",
            baseParams: {
                contractNumber : $("#contractNumber").val(),
                serviceType : serviceType,
                searchStartMonth : $("#searchStartMonth").val(),
                searchEndMonth : $("#searchEndMonth").val()
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ["lastTokenDate", "lastTokenDateView", "balance", "balanceView", "usedCredit", "usedCreditView", "chargedCredit", "chargedCreditView", "currentCredit", 
                     "consumption", "consumptionView", "keyNum", "payment", "contractId", "minDate", "maxDate", "searchMinDate", "searchMaxDate", "authCode", "municipalityCode"],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
        });

        // column 사이즈 자동 조절 때문에 여기에 위치함.
        chargeHistoryColModel = new Ext.grid.ColumnModel({
            columns: [
                 {header: "<fmt:message key="aimir.hems.prepayment.chargedate"/>", dataIndex: 'lastTokenDateView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.usedcredit"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'usedCreditView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.chargeAmount"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'chargedCreditView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'balanceView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.consumption"/>(kWh)", dataIndex: 'consumptionView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.availconsumption"/>(kWh)", dataIndex: 'consumptionView', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.paymentnum"/>", dataIndex: 'keyNum', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.payment"/>", dataIndex: 'payment', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.prepayment.authCode"/>", dataIndex: 'authCode', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.prepayment.municipalityCode"/>", dataIndex: 'municipalityCode', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                ,{header: "<fmt:message key="aimir.hems.prepayment.detail"/>", dataIndex: 'contractId', width: (width > mxwidth) ? width*(80/mxwidth)-4 : 80-4, 
                  renderer: function(value, metaData, record, index) {
                                if (record.get('minDate') == "" || record.get('maxDate') == "") {
                                    return "";
                                } else {
                                    var tplBtn = new Ext.Template("<a href='javascript:viewDetailWindow(\"{contractId}\", \"{minDate}\", \"{maxDate}\", \"{searchMinDate}\", \"{searchMaxDate}\", \"{lastTokenDate}\");'><span class='icon_graph'>&nbsp;</span></a>");
                                    return tplBtn.apply({contractId: record.get('contractId'), minDate: record.get('minDate'), maxDate: record.get('maxDate'), 
                                                         searchMinDate: record.get('searchMinDate'), searchMaxDate: record.get('searchMaxDate'), lastTokenDate: record.get('lastTokenDateView')});
                                }
                            }
                 }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                //,width: 100
            }
        });

        if(chargeHistoryGridOn == false) {
            chargeHistoryGrid = new Ext.grid.GridPanel({
                  //title: '최근 한달 Demand Response History',
                  store: chargeHistoryStore,
                  colModel : chargeHistoryColModel,
                  sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                  autoScroll:false,
                  width: width,
                  height: 560,
                  stripeRows : true,
                  columnLines: true,
                  loadMask:{
                      msg: 'loading...'
                  },
                  renderTo: 'chargeHistory',
                  viewConfig: {
                     // forceFit:true,
                      enableRowBody:true,
                      showPreview:true,
                      emptyText: 'No data to display'
                  },
                  // paging bar on the bottom
                  bbar: new Ext.PagingToolbar({
                      pageSize: 20,
                      store: chargeHistoryStore,
                      displayInfo: true,
                      displayMsg: ' {0} - {1} / {2}'
                  })
              });
            chargeHistoryGridOn = true;
        } else {
            chargeHistoryGrid.setWidth(width);
            var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
            chargeHistoryGrid.reconfigure(chargeHistoryStore, chargeHistoryColModel);
            bottomToolbar.bindStore(chargeHistoryStore);
        }
    };

    var detailWin;
    function drawDetailWin() {
        //var width = $("#chargeHistory").width();
        detailWin = new Ext.Window({
            title: "<fmt:message key="aimir.hems.prepayment.detail"/>",
            id: 'detailWinId',
            applyTo:'chargeHistoryDetail',
            autoScroll: true,
            resizable: false,
            width: 700,
            height: 370,
            html: "<div id='detailWindow'></div>",
            closeAction:'hide'
        });
    }

    function viewDetailWindow(contractId, minDate, maxDate, searchMinDate, searchMaxDate, lastTokenDate) {
        Ext.getCmp('detailWinId').show();
        updateDetailFChart(contractId, minDate, maxDate, searchMinDate, searchMaxDate, lastTokenDate);
    }

    var fcChartDataXml;
    function updateDetailFChart(contractId, minDate, maxDate, searchMinDate, searchMaxDate, lastTokenDate) {
        emergePre();
        
        $.getJSON('${ctx}/gadget/prepaymentMgmt/getChargeHistoryDetailChartData.do'
                ,{contractId: contractId,
                    serviceType: serviceType,
                    minDate: minDate,
                    maxDate: maxDate}
                ,function(json) {
                     var list = json.chartDatas;
                     var dataCount = list.length;
                     //var labelStep = (dataCount <= 5) ? 1 : Math.round(dataCount/5);
                     var labelStep = 3;

                     fcChartDataXml = "<chart "
                        + "caption='<fmt:message key="aimir.hems.prepayment.useperiod"/> : " + searchMinDate + " ~ " + searchMaxDate + "' "
                        + "yAxisName='<fmt:message key="aimir.hems.label.usageFeeSymbol"/>' "
                        + "chartLeftMargin='10' "
                        + "chartRightMargin='20' "
                        + "chartTopMargin='20' "
                        + "chartBottomMargin='5' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='0' "
                        + "labelDisplay='WRAP' "
//                        + "numberSuffix='  ' "
                        + "labelStep='" + labelStep + "' "
                        //+ "decimals='3' "
                        //+ "forceDecimals='1' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + xml_fChartStyle_Column2D_nobg
                        + ">";

                     var categories = "<categories>";
                     var dataset = "<dataset seriesName='" + lastTokenDate + "' color='" + fChartColor_CompareElec[2] + "'>";
                     //var labels = "";
                     var indexes = "";
                     if(dataCount > 0) {
                         for( index in list){
                             if(!isNaN(index)) {
                                 categories += "<category label='" + list[index].yyyymmdd + "' />";
                                 //dataset += "<set value='" + list[index].usage + "'/>"
                                 dataset += "<set value='" + list[index].bill + "'/>"
                             }
                         }
                     } else {
                         categories += "<category label=' ' />";
                         dataset += "<set value='0'/>"
                     }

                     categories += "</categories>";
                     dataset += "</dataset>";
                     fcChartDataXml += categories + dataset + "</chart>";
                     fcChartRender();
                     hide();
                }
        );
    }

    //window.onresize = fcChartRender;
    function fcChartRender() {
        if($('#detailWindow').is(':visible')) {
            var detailWidth = $('#detailWindow').width();
            fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/ScrollColumn2D.swf", "myChartId", detailWidth, "365", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("detailWindow");
        }
    }

    /* 요금단가표 START */
    var tariffData;
    function getCustomerTariff(){
        emergePre();

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getPrepaymentTariff.do"
                ,{contractNumber : $("#contractNumber").val(), serviceType : serviceType}
                ,function(json) {
                    tariffData = json.result;
                    customerTariffRender();
                });
    }

    var tariffGridOn = false;
    var tariffGrid;
    var tariffColModel;
    //var checkSelModel;
    var customerTariffRender = function() {
        if (tariffData == null) {
            return;
        }

        var width = $("#tariffDiv").width();

        var tariffStore = new Ext.data.JsonStore({
            autoLoad: true,
            data: tariffData,
            fields: ["block", "serviceCharge", "transmissionNetworkCharge", "energyDemandCharge", "rateRebalancingLevy"]
        });

        tariffColModel = new Ext.grid.ColumnModel({
            columns: [
                {header: "", dataIndex: 'block', width: width*(3/11)}
               ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.servicecharge"/>", dataIndex: 'serviceCharge', width: width*(2/11)}
               ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.networkcharge"/>", dataIndex: 'transmissionNetworkCharge', width: width*(2/11)}
               ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.energycharge"/>", dataIndex: 'energyDemandCharge', width: width*(2/11)}
               ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.envlevy"/>", dataIndex: 'rateRebalancingLevy', width: width*(2/11)-4}
            ],
            defaults: {
                sortable: false
               ,menuDisabled: true
               ,width: 120
           }
        });

        if(tariffGridOn == false) {

            tariffGrid = new Ext.grid.GridPanel({
                title: '<fmt:message key="aimir.hems.prepayment.chargeprice"/>',
                store: tariffStore,
                colModel : tariffColModel,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                autoScroll: false,
                width: width,
                height: 310,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'tariffDiv',
                viewConfig: {
                    //forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                }
            });
            tariffGridOn = true;
        } else {
            tariffGrid.setWidth(width);
            tariffGrid.reconfigure(tariffStore, tariffColModel);
        }
        hide();
    };

    /* 사용가능 예상일수 그리드 START */
   // var dayData;
    var dayData = [
                  ['2,000', '100', '7'],
                  ['3,700', '210', '14'],
                  ['5,500', '330', '21'],
                  ['7,100', '500', '30'],
                  ['13,500', '1150', '60']
              ];

    function getExptUsableDayList(){
        emergePre();

        /* 잠깐 주석처리함 by 은미애
        $.getJSON("${ctx}/gadget/prepaymentMgmt/getExptUsableDayList.do"
                ,{contractNumber : $("#contractNumber").val(), serviceType : serviceType}
                ,function(json) {
                    dayData = json.result;
                    exptUsableDayRender();
                });
        */
        exptUsableDayRender();
    }

    var usableDayGridOn = false;
    var usableDayGrid;
    var usableDayColModel;
    //var checkSelModel;
    var exptUsableDayRender = function() {
        if (dayData == null) {
            return;
        }

        var width = $("#exptUsableDayDiv").width();

       // var usableDayStore = new Ext.data.JsonStore({
        var usableDayStore = new Ext.data.ArrayStore({	
            //autoLoad: true,
            //url: "${ctx}/gadget/prepaymentMgmt/getExptUsableDayList.do",
            //baseParams: {
            //    contractNumber : $("#contractNumber").val(),
            //    serviceType : serviceType
            //},
            //root:'result',
            //data: dayData,
            fields: ["chargedCredit", "consumption", "exptUsableDay"]
        });

        usableDayStore.loadData(dayData);
 
        if(usableDayGridOn == false) {
            usableDayColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key="aimir.hems.prepayment.chargedcredit"/>", dataIndex: 'chargedCredit', width: (width/3)}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.consumption"/>(kWh)", dataIndex: 'consumption', width: (width/3)}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.exptusabledays"/>", dataIndex: 'exptUsableDay', width: (width/3)-4}
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            usableDayGrid = new Ext.grid.GridPanel({
                //title: '',
                store: usableDayStore,
                colModel : usableDayColModel,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                autoScroll:false,
                width: width,
                height: 200,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'exptUsableDayDiv',
                viewConfig: {
                    //forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                }
            });
            usableDayGridOn = true;
        } else {
            usableDayGrid.setWidth(width);
            usableDayGrid.reconfigure(usableDayStore, usableDayColModel);
        }
        hide();
    };

    /*]]>*/
</script>
</head>
<body>
<input type="hidden" name="currentBalance" id="currentBalance"/>
<input type="hidden" name="balance" id="balance"/>
<input type="hidden" name="lastTokenDate" id="lastTokenDate"/>
<input type="hidden" name="nextTokenDate" id="nextTokenDate"/>
<input type="hidden" name="emergencyCreditStartTime" id="emergencyCreditStartTime"/>
<input type="hidden" name="emergencyCreditAutoChange" id="emergencyCreditAutoChange"/>

<input type="hidden" name="threshold1" id="threshold1"/>
<input type="hidden" name="threshold2" id="threshold2"/>

<input type="hidden" name="interval" id="interval"/>
<input type="hidden" name="threshold" id="threshold"/>

<input type="hidden" name="searchStartMonth" id="searchStartMonth"/>
<input type="hidden" name="searchEndMonth" id="searchEndMonth"/>
<div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span id="img_isNotService_house"></span></div>
            <div class="isNotService_today_right">
                <table height='160'>
                <tr>    
                    <td><fmt:message key='aimir.hems.label.isNotService'/></td>
                </tr>
                </table>
            </div>
        </div>
</div>
<div id="wrapper">
    <!--contract no.-->
    <div class="topsearch">

        <div class="contract">
            <table>
                <tr>
                    <td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
                    <td>
                        <select id="contractNumber"  name="contractNumber" style="width:480px"></select>
                    </td>
                </tr>
            </table>
        </div>

        <div class="top_line"></div>

        <!-- tab -->
        <div class="hems_tab">
            <ul>
                <li><a id="chargeSettingTab"><fmt:message key="aimir.hems.prepayment.chargesetting"/></a></li>
                <li><a id="customerTariffTab"><fmt:message key="aimir.hems.prepayment.chargeprice"/></a></li>
            </ul>
        </div>
        <!--// tab -->

    </div>
    <!--//contract no.-->

    <!-- tab 1 -->
    <!-- <div id="searchDiv"> -->
    <div id="chargeSettingTabDiv" style="display:none;">

        <!--tab1 right -->
        <div class="prepay_right">

            <div class="balance_max_left">
                <div id="guageChartDiv" class="prepayMgmt_max">gauge chart</div>
            </div>

            <div class="balance_max_right">
                <!-- 현재금액 -->
                <div class="balance_border">

                    <div class="now_balance">
                        <div class="overflow_hidden">
                            <div class="name_balance"><fmt:message key="aimir.hems.prepayment.currentbalance"/></div>
                            <div id="currentBalanceView" class="amount_balance"></div>
                        </div>
                        <div class="lt"></div>
                        <div class="rt"></div>
                    </div>

                    <div class="balance2">
                        <table>
                            <tr>
                                <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.lastchargedate"/><!-- 마지막 충전일 --></th>
                                <td id="lastTokenDateView"></td>
                            </tr>
                            <tr>
                                <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key='aimir.price.unit'/>)<!-- 충전 후 잔액 --></th>
                                <td id="balanceView" class="bold"></td>
                            </tr>
                        </table>
                    </div>

                    <div class="balance2">
                        <table>
                            <tr>
                                <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.nextchargedate"/><!-- 다음 충전 예상일 --></th>
                                <td id="nextTokenDateView"></td>
                            </tr>
                        </table>
                    </div>

                </div>
                <!--// 현재금액 -->
            </div>

            <!-- 잔액 통보 -->
            <div class="title_basic margin_t20"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.balancenotify"/><!-- 잔액정보 설정 --></div>

            <div class="blance_term_box">
                <table class="balance_term">
                	<colgroup>
		        	<col width="" />
		        	<col width="80%" />
		        	</colgroup>
                    <!-- 잔액 통보 주기를 설정합니다.<span>( 설정된 주기로 잔액을 통보합니다.)</span> -->
                    <tr><th colspan="2"><fmt:message key="aimir.hems.prepayment.intervalmsg"/></th>
                    </tr>
                    <tr><td><fmt:message key="aimir.period2"/><!-- 주기 --></td>
                        <td>
                            <select id="notifyPeriod" name="notifyPeriod" style="width:80px" onchange="controlDayofWeek(this);">
                                <option value="1"><fmt:message key="aimir.daily"/></option>
                                <option value="2"><fmt:message key="aimir.weekly"/></option>
                            </select>
                        </td>
                    </tr>
                    <tr><td><fmt:message key="aimir.hems.prepayment.interval"/><!-- 간격 --></td>
                        <td>
                            <!-- Daily -->
                            <div id="intervalDailyDiv" style="display:none;">
                            <select id="intervalDaily" name="intervalDaily" style="width:40px;">
                            <c:forEach var="i" begin="1" end="6" step="1" varStatus="status">
                                <option value="${i}">${i}</option>
                            </c:forEach>
                            </select>
                            </div>
                            <!-- Weekly -->
                            <div id="intervalWeeklyDiv" style="display:none;">
                            <select id="intervalWeekly" name="intervalWeekly" style="width:40px;">
                            <c:forEach var="j" begin="1" end="4" step="1" varStatus="status">
                                <option value="${j}">${j}</option>
                            </c:forEach>
                            </select>
                            </div>
                            <!-- Days -->
                            &nbsp;&nbsp;<span id="intervalUnit"></span>
                        </td>
                    </tr>
                    <!-- weekly 일경우에만 나옴 -->
                    <tr id="dayofweek" style="display:none;"><td><fmt:message key="aimir.dayofweek"/></td>
                        <td>
                            <ul class="prepay_weekly">
                                <li><input type="checkbox" id="mon" name="mon" class="checkbox2" value="true"/><fmt:message key="aimir.day.mon"/><!-- Mon --></li>
                                <li><input type="checkbox" id="tue" name="tue" class="checkbox2" value="true"/><fmt:message key="aimir.day.tue"/><!-- Tue --></li>
                                <li><input type="checkbox" id="wed" name="wed" class="checkbox2" value="true"/><fmt:message key="aimir.day.wed"/><!-- Wed --></li>
                                <li><input type="checkbox" id="thu" name="thu" class="checkbox2" value="true"/><fmt:message key="aimir.day.thu"/><!-- Thu --></li>
                                <li><input type="checkbox" id="fri" name="fri" class="checkbox2" value="true"/><fmt:message key="aimir.day.fri"/><!-- Fri --></li>
                                <li><input type="checkbox" id="sat" name="sat" class="checkbox2" value="true"/><fmt:message key="aimir.day.sat"/><!-- Sat --></li>
                                <li><input type="checkbox" id="sun" name="sun" class="checkbox2" value="true"/><fmt:message key="aimir.day.sun"/><!-- Sun --></li>
                            </ul>
                        </td>
                    </tr>
                    <tr><td><fmt:message key="aimir.hour"/></td>
                        <td>
                            <select id="notifyHour" name="notifyHour" style="width:140px"></select>
                        </td>
                    </tr>
                    <!-- 잔액 통보 값을 설정합니다.<span>(잔액이 설정된 값에 이르면 통보합니다.)</span> -->
                    <tr><th colspan="2" class="notice_value"><fmt:message key="aimir.hems.prepayment.balancemsg"/></th>
                    </tr>
                    <tr><td><fmt:message key="aimir.hems.prepayment.notifythreshold"/>(<fmt:message key='aimir.price.unit'/>)<!-- 통보값 --></td>
                        <td><input id="thresholdView" name="thresholdView" type="text" class="target" style="width:80px" onkeyup="removeComma(event, this);" onfocus="removeComma(event, this);" onblur="addComma(this);"/></td>
                    </tr>
                </table>

                <div class="basic_rightbtn margin_t5">
                    <a href="#" class="btn_blue" onclick="saveNotifySettingConfirm();"><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
                    <a href="#" class="btn_blue" onclick="getNotificationTabInfo();"><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
                </div>
            </div>
            <!--// 잔액 통보 -->


            <!-- Emergency Credit -->
            <div class="balancebox">

                <!-- Emergency Credit -->
                <div class="title_basic margin_t30"><span class="icon_title_blue"></span><!--<fmt:message key="aimir.hems.prepayment.emergencycredit"/>-->Emergency Credit</div>

                <!-- 잔액이 0원 일 경우, 자동으로 Emergency Credit Mode로 변경되어 전원이 공급됩니다. -->
                <div class="credit_textbox"><fmt:message key="aimir.hems.prepayment.autoecmodemsg"/></div>

                <div class="credit_textbox">
                    <span class="text_blue"><fmt:message key="aimir.hems.prepayment.validperiod"/><!-- 유효기간 --> :</span>
                    <span id="duration" class="text_orange2 margin_l5 bold"></span>
                    <span class="text_orange2 margin_l5"><fmt:message key="aimir.hems.prepayment.days"/></span>
                </div>

<%--
                // 장비인증 보류
                <!-- 현재 사용중인 단말기로 Emergency Credit Mode변경을 원하시면 인증 명칭을 입력한 후 인증 버튼을 클릭해 주세요.(복수개 선택) -->
                <div class="credit_textbox text_gray7"><fmt:message key="aimir.hems.prepayment.authenticationmsg"/></div>

                <!-- 인증장비 -->
                <div class="quote_box">
                    <div class="quote">
                        <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                        <span><input id="" type="text" style="width:50px"></span>
                        <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
                    </div>
                    <div class="quote">
                        <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                        <span><input id="" type="text" style="width:50px"></span>
                        <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
                    </div>
                    <div class="quote">
                        <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                        <span><input id="" type="text" style="width:50px"></span>
                        <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
                    </div>
                </div>
                <!--// 인증장비 -->

                <div class="basic_rightbtn margin_t5">
                    <a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
                    <a href="" class="btn_blue" ><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
                </div>
--%>
            </div>
            <!--// Emergency Credit -->

        </div>
        <!--//tab1 right -->

        <!--tab1 left -->
        <div class="prepay_left">
            <!-- <div id="monthly">
                <ul class="usage">
                    <li class="icon_prev"><button id="monthlyLeft" type="button"></button></li>
                    <li><select id="monthlyYearCombo"></select></li>
                    <li><label class="datetxt"><fmt:message key="aimir.year1" /></label></li>
                    <li><select id="monthlyMonthCombo" style="width:30px"></select></li>
                    <li><label class="datetxt"><fmt:message key="aimir.day.mon" /></label></li>
                    <li class="icon_next"><button id="monthlyRight" type="button"></button></li>
                    <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
                </ul>
            </div> -->
            <div class="saving_goal prepay_searchbox">
                <table>
                    <tr>
                        <td><select id="fromYearCombo" style="width:60px"></select></td>
                        <td><label class="datetxt"><fmt:message key="aimir.year1" /></label></td>
                        <td><select id="fromMonthCombo" style="width:35px"></select></td>
                        <td><label class="datetxt"><fmt:message key="aimir.day.mon" /></label></td>
                        <td>~</td>
                        <td><select id="toYearCombo" style="width:60px"></select></td>
                        <td><label class="datetxt"><fmt:message key="aimir.year1" /></label></td>
                        <td><select id="toMonthCombo" style="width:35px"></select></td>
                        <td><label class="datetxt"><fmt:message key="aimir.day.mon" /></label></td>
                        <td></td>
                        <td class="btnspace"><a href="javascript:getChargeHistoryData();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>
                    </tr>
                </table>
            </div>

            <div id="chargeHistory" class="ext_grid"></div>
            <div id="chargeHistoryDetail" class="ext_grid"></div>

        </div>
        <!--//tab1 left -->

    </div>
    <!--// tab1 -->


    <!--<div class="clear"></div>탭 부분작업 하신후에  삭제해 주세요 -->


    <!-- tab2 -->
    <div id="customerTariffTabDiv" style="display:none;">
        <div class="prepay_right">
            <div class="calc">
                <div class="title_calctitle"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.calctitle"/><!-- 충전 금액과 사용 가능 예산 일수 --></div>
                <div class="expect_day">
                    <ul>
                        <li><span class="icon_star margin_t2">&nbsp;</span>
                            <span class="itemline"><fmt:message key="aimir.hems.prepayment.calcmsg"/><!-- 충전금액을 입력하시면 사용가능 일수가 계산됩니다. --></span></li>
                        <li class="bg_h_line hr"></li>
                        <li><span class="text_blk bold margin_t2"><fmt:message key="aimir.hems.prepayment.chargedcredit"/><!-- 충전금액(원) --></span>
                            <span class="margin_l5"><input type="text" id="savingTarget"  style="width:100px" class="target"/></span>
                            <span><a href="#" class="btn_blue"><span><fmt:message key="aimir.hems.prepayment.calc"/><!-- 계산 --></span></a></span>
                        </li>
                        <li class="padding_l5 padding_t5"><span class="icon_arrow_gray"> </span>
                            <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.consumption"/><!-- 사용전력 --> : </span>
                            <span class="text_blue bold">50 kWh</span>
                        </li>
                        <li class="padding_l5"><span class="icon_arrow_gray"> </span>
                            <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.exptusabledays"/><!-- 예상 사용 일수 --> : </span>
                            <span class="text_blue bold">5 <fmt:message key="aimir.hems.prepayment.days"/><!-- 일 --> </span>
                        </li>
                    </ul>
                </div>
                <div id="exptUsableDayDiv" class="balance_grid margin_t20"></div>
            </div>
        </div>

        <div class="prepay_left">
            
            <!-- <div class="padding_t20">Horizontal Linear Gauge</div> -->
            <div id="tariffDiv" class="margin_t20"></div>
        </div>
    </div>
    <!--// tab2 -->


</div>
</body>
</html>