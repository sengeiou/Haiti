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
        $(function() { $("#chargeHistoryTab").bind("click", function(event) { displayDivTab("chargeHistoryTab"); } ); });
        $(function() { $("#notificationTab").bind("click", function(event) { displayDivTab("notificationTab"); } ); });
        $(function() { $("#customerTariffTab").bind("click", function(event) { displayDivTab("customerTariffTab"); } ); });
        
        getContract();
    
        $("#notifyPeriod").selectbox();
        $("#intervalDaily").selectbox();
        $("#intervalWeekly").selectbox();
    });

    var divTabArray = ["chargeHistoryTab", "notificationTab", "customerTariffTab"];
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
                getChargeHistoryTabInfo();
                break;

            case 1 :
                getAllHours();
                getNotificationTabInfo();
                break;

            case 2 :
                getCustomerTariffTabInfo();
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
                function(json) {
  
                    if(json.isNotService) {  // 해당 가젯에 대한 권한이 없을때
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
                    var contracts = json.contracts;
    
                    // 계약정보 콤보박스 생성
                    $("#contractNumber").pureSelect(contracts);
                    $("#contractNumber").selectbox();

                    // 계약정보 콤보 박스 선택 이벤트
                    $("#contractNumber").bind("change", function(event) { searchTab(); } );
                    displayDivTab("chargeHistoryTab");
                }
            );
    };

    var getAllHours = function() {
        
        var params = {"contractNumber" : $("#contractNumber").val()};
    
        $.getJSON("${ctx}/gadget/prepaymentMgmt/getAllHours.do",
                params,
                function(json) {
                    var hours = json.hours;
    
                    // 시간 콤보박스 생성
                    $("#notifyHour").pureSelect(hours);
                    $("#notifyHour").selectbox();
                }
            );
    };

    function getChargeHistoryTabInfo(){
        
        emergePre();

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getChargeInfo.do"
                ,{contractNumber : $('#contractNumber').val(),
                    serviceType : serviceType}
                ,function(json) {
                    var currentBalance = json.result.currentCredit;
                    var balance = json.result.balance;
                    var lastTokenDate = json.result.lastTokenDate;
                    var nextTokenDate = json.result.nextTokenDate;
                    var emergencyCreditStartTime = json.result.emergencyCreditStartTime;
                    var emergencyCreditAutoChange = json.result.emergencyCreditAutoChange;

                    var currentCreditView = json.result.currentCreditView;
                    var balanceView = json.result.balanceView;
                    var lastTokenDateView = json.result.lastTokenDateView;
                    var nextTokenDateView = json.result.nextTokenDateView;
                    var emergencyCreditStartTimeView = json.result.emergencyCreditStartTimeView;
                    var emergencyCreditMaxDuration = json.result.emergencyCreditMaxDuration;
                    var limitDateView = json.result.limitDateView;
                    var limitDuration = json.result.limitDuration;
                    var creditType = json.result.creditType;
                    var threshold1 = json.result.threshold1;

                    var threshold2 = json.result.threshold2;
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
                    $("#lastTokenDateView").text(lastTokenDateView);
                    //$("#nextTokenDateView").text(nextTokenDateView);
                    $("#nextTokenDateView").text("2011/12/15");
                    //$("#emergencyCreditStartTimeView").text(emergencyCreditStartTimeView);
                    //$("#emergencyCreditMaxDurationView").text(emergencyCreditMaxDuration);
                    $("#limitDateView").text(limitDateView);
                    $("#limitDuration").text(limitDuration);
                    $("#creditType").val(creditType);
                    $("#threshold1").val(threshold1);
                    $("#threshold2").val(threshold2);


                    ecModeButtonControl();

                    // 게이지차트 조립
                    guageChartDataXml = " <chart lowerLimit='0' upperLimit='" + limit + "' showGaugeBorder='0' " + 
                                        " gaugeOuterRadius='100%' gaugeInnerRadius='60%' pivotRadius='2' " + 
                                        " lowerLimitDisplay='0' "  +
                                        " bgColor='ffffff' " +
                                        " baseFont='dotum' baseFontSize='12' baseFontColor='#434343' "+
                    					" gaugeStartAngle='180' gaugeEndAngle='0' " + 
                                        " palette='5' tickValueDistance='20' showValue='1' paletteThemeColor='575757' " +
                                        " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' showBorder='0' " + 
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

                    if($('#chargeHistoryTabDiv').is(':visible')) {
                        fwGuageChartRender();
                    }

                    hide();
                });
    }

    // window resize event
    $(window).resize(function() {
        fwGuageChartRender();
        getCustomerTariffTabInfo();
    });

    function fwGuageChartRender(){ 
        if($('#chargeHistoryTabDiv').is(':visible')) {
            guageDivWidth  = $('#guageChartDiv').width();

            var guageChart = new FusionCharts("${ctx}/flexapp/swf/fusionwidgets/AngularGauge.swf", "myChartId", guageDivWidth, "200", "0", "0");
            guageChart.setDataXML(guageChartDataXml);
            guageChart.setTransparent("transparent");
            guageChart.render("guageChartDiv");
        }
    }  

    // Emergency Credit Mode 버튼 및 메세지 view control
    function ecModeButtonControl() {
        // TEST
        //$("#creditType").val("2.2.2");
        // 인증장비 체크는 제외함(보류)
        if ($("#emergencyCreditAutoChange").val() == "false" && ($("#currentBalance").val() == "" || $("#currentBalance").val() == "0")) {
            $("#ecModeDiv").show();

            // Emergency Credit Mode 인 경우
            if ($("#creditType").val() == "2.2.2") {
                $("#ecModeButton").hide();
                $("#ecModeMsg").show();
            } else {
                $("#ecModeButton").show();
                $("#ecModeMsg").hide();
            }
        } else {
            $("#ecModeDiv").hide();
        }
    }

    // 통보설정 조회
    function getNotificationTabInfo(){
        emergePre();

        $.getJSON("${ctx}/gadget/prepaymentMgmt/getBalanceNotifySetting.do"
                ,{contractNumber : $('#contractNumber').val()}
                ,function(json) {
                    var notificationPeriod         = json.result.notificationPeriod;
                    var notificationInterval       = json.result.notificationInterval;
                    var notificationTime           = json.result.notificationTime;
                    var notificationWeeklyMon      = json.result.notificationWeeklyMon;
                    var notificationWeeklyTue      = json.result.notificationWeeklyTue;
                    var notificationWeeklyWed      = json.result.notificationWeeklyWed;
                    var notificationWeeklyThu      = json.result.notificationWeeklyThu;
                    var notificationWeeklyFri      = json.result.notificationWeeklyFri;
                    var notificationWeeklySat      = json.result.notificationWeeklySat;
                    var notificationWeeklySun      = json.result.notificationWeeklySun;
                    var lastNotificationDate       = json.result.lastNotificationDate;
                    var prepaymentThreshold        = json.result.prepaymentThreshold;
                    var prepaymentThresholdView    = json.result.prepaymentThresholdView;
                    var emergencyCreditAutoChange  = json.result.emergencyCreditAutoChange;
                    var emergencyCreditStartTime   = json.result.emergencyCreditStartTime;
                    var emergencyCreditMaxDuration = json.result.emergencyCreditMaxDuration;
                    var creditType                 = json.result.creditType;
                    var devices                    = json.result.devices;

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

                        //$("input[name=tue]:checkbox").attr("checked", true);
                        //$("#wed").attr("checked", true);
                        //$("#tue").attr("checked", false);
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
                });
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
    function removeComma(src) {
        var val = src.value;
        val = removeChar(val);
        val = removeFstZero(val);
        src.value = val;
        src.focus();
    }

    // inputbox에서 focus가 나가면 comma추가
    function addComma(src) {
        var pattern = /(-?[0-9]+)([0-9]{3})/;
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

    // Change Emergency Credit Mode
    function changeEmergencyModeConfirm() {
        Ext.MessageBox.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.hems.prepayment.changeecmode.confirm"/>"
                             , function(btn) {
                                   if (btn == 'yes') {
                                       changeEmergencyMode();
                                   }
                               });
    }

    // change Emergency Credit Mode
    function changeEmergencyMode() {
        $.getJSON("${ctx}/gadget/prepaymentMgmt/changeEmergencyCreditMode.do",
                {contractNumber : $('#contractNumber').val(),
                 operatorId : operatorId},
                function(json) {
                    if (json.status == "success") {
                        Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.hems.prepayment.changeecmode.success"/>", getChargeHistoryTabInfo);
                    } else {
                        Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.hems.prepayment.changeecmode.fail"/>");
                    }
                }
            );
    }        

    /* 요금단가표 START */
    var tariffData;
    function getCustomerTariffTabInfo(){
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

        emergePre();
        var tariffStore = new Ext.data.JsonStore({
            autoLoad: true,
            data: tariffData,
            fields: ["block", "serviceCharge", "transmissionNetworkCharge", "energyDemandCharge", "rateRebalancingLevy"]//,
        });


        if(tariffGridOn == false) {

            tariffColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "", dataIndex: 'block', width: (width > 640) ? width*(4/16) : 160}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.servicecharge"/>", dataIndex: 'serviceCharge', width: (width > 640) ? width*(3/16) : 120}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.networkcharge"/>", dataIndex: 'transmissionNetworkCharge', width: (width > 640) ? width*(3/16) : 120}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.energycharge"/>", dataIndex: 'energyDemandCharge', width: (width > 640) ? width*(3/16) : 120}
                   ,{header: "<fmt:message key="aimir.hems.prepayment.tariff.envlevy"/>", dataIndex: 'rateRebalancingLevy', width: (width > 640) ? width*(3/16)-4 : 120-4}
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            tariffGrid = new Ext.grid.GridPanel({
                //title: '최근 한달 Demand Response History',
                store: tariffStore,
                colModel : tariffColModel,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                autoScroll:false,
                width: width,
                height: 310,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'tariffDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                }//,
                // paging bar on the bottom
                //bbar: new Ext.PagingToolbar({
                //    pageSize: 10,
                //    store: tariffStore,
                //    displayInfo: true,
                //    displayMsg: ' {0} - {1} / {2}'
                //})
            });
            tariffGridOn = true;
        } else {
            tariffGrid.setWidth(width);
            tariffGrid.reconfigure(tariffStore, tariffColModel);
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

<input type="hidden" name="creditType" id="creditType"/>
<input type="hidden" name="interval" id="interval"/>
<input type="hidden" name="threshold" id="threshold"/>
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
                    <td class="tit_name"><fmt:message key="aimir.hems.label.contractFriendlyName"/></td>
                    <td>
                        <select id="contractNumber"  name="contractNumber" style="width:280px"></select>
                    </td>
                </tr>
            </table>
        </div>

        <div class="top_line"></div>

        <!-- tab -->
        <div class="hems_tab">
            <ul>
                <li><a id="chargeHistoryTab"><fmt:message key='aimir.hems.prepayment.chargehistory'/></a></li>
                <li><a id="notificationTab"><fmt:message key="aimir.setting"/></a></li>
                <li><a id="customerTariffTab"><fmt:message key="aimir.hems.prepayment.chargeprice"/></a></li>
            </ul>
        </div>
        <!--// tab -->

    </div>
    <!--//contract no.-->

    <!-- tab 1 -->
    <!-- <div id="searchDiv"> -->
    <div id="chargeHistoryTabDiv" style="display:none;">

        <div id="guageChartDiv" class="prepayMgmt">gauge chart</div>

        <!-- 현재금액 -->
        <div class="balancebox">
            <div class="balance_border">

                <div class="now_balance">
                    <div class="overflow_hidden">
                        <div class="name_balance"><fmt:message key="aimir.hems.prepayment.currentbalance"/></div>
                        <div id="currentBalanceView" class="amount_balance"></div>
                        <!-- <div class="amount_balance"><span id="currentCreditView">120,000</span><input id="currentCredit" type="hidden" value="120,000" maxlength="2" class="input_dot" title="" readonly style="width:60px;"/></div> -->
                    </div>
                    <div class="lt"></div>
                    <div class="rt"></div>
                </div>

                <div class="balance2">
                    <table>
                        <tr>
                            <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.lastchargedate"/></th>
                            <td id="lastTokenDateView"></td>
                        </tr>
                        <tr>
                            <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key='aimir.price.unit'/>)</th>
                            <td id="balanceView" class="bold"></td>
                        </tr>
                    </table>
                </div>

                <!-- <div class="balance2">
                    <table>
                        <tr>
                            <th><span class="icon_arrow_blue"></span><fmt:message key="aimir.hems.prepayment.nextchargedate"/></th>
                            <td id="nextTokenDateView"></td>
                        </tr>
                    </table>
                </div> -->

            </div>
        </div>
        <!--// 현재금액 -->

        <!-- Change Emergency Credit Mode -->
        <div id="ecModeDiv" class="creditmode_box" style="display:none;">
            <div id="ecModeButton" class="creditmode_btn">
                <a href="#" class="btn_blue" onclick="changeEmergencyModeConfirm();"><span><fmt:message key="aimir.hems.prepayment.changeemergencymode"/></span></a>
            </div>

            <!-- Emergency Credit 유효기간이 <em class="text_orange2 bold" id="limitDuration"></em><em class="text_orange2 bold">일</em> 남았습니다.<br />
                 <em class="underline bold" id="limitDateView"></em>일까지 충전하지 않으면 공급이 차단됩니다. -->
            <div id="ecModeMsg" class="creditmode_msg"><fmt:message key="aimir.hems.prepayment.emergencymessage"/></div>
            
        </div>
        <!--// Change Emergency Credit Mode -->

    <!-- </div> -->
    </div>
    <!--// tab1 -->

    <!-- tab2 -->
    <!-- <div class="balancebox"> -->
    <div id="notificationTabDiv" class="balancebox" style="display:none;">
    <!-- <div class="balancebox"> -->

        <!-- 잔액 통보 -->
        <!-- 잔액통보 -->
        <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.balancenotify"/></div>

        <table class="balance_term">
        	<colgroup>
        	<col width="" />
        	<col width="80%" />
        	</colgroup>
            <!-- 잔액 통보 주기를 설정합니다.<span>(설정된 주기로 잔액을 통보합니다.)</span> -->
            <tr><th colspan="2"><fmt:message key="aimir.hems.prepayment.intervalmsg"/></th>
            </tr>
            <!-- 주기 -->
            <tr><td><fmt:message key="aimir.period2"/></td>
                <td>
                    <select id="notifyPeriod" name="notifyPeriod" style="width:80px" onchange="controlDayofWeek(this);">
                        <option value="1"><fmt:message key="aimir.daily"/></option>
                        <option value="2"><fmt:message key="aimir.weekly"/></option>
                    </select>
                </td>
            </tr>
            <!-- 간격 -->
            <tr><td><fmt:message key="aimir.hems.prepayment.interval"/></td>
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

            <!-- 시간 -->
            <tr><td><fmt:message key="aimir.hour"/></td>
                <td>
                    <select id="notifyHour" name="notifyHour" style="width:140px"></select>
                </td>
            </tr>
            <!-- 잔액 통보 값을 설정합니다.<span>(잔액이 설정된 값에 이르면 통보합니다.)</span> -->
            <tr><th colspan="2" class="notice_value"><fmt:message key="aimir.hems.prepayment.balancemsg"/></th>
            </tr>
            <!-- 통보값 -->
            <tr><td><fmt:message key="aimir.hems.prepayment.notifythreshold"/>(<fmt:message key='aimir.price.unit'/>)</td>
                <td><input id="thresholdView" name="thresholdView" type="text" class="target" style="width:80px" onkeyup="removeComma(this);" onfocus="removeComma(this);" onblur="addComma(this);"/></td>
            </tr>
        </table>

        <div class="basic_rightbtn margin_t5">
            <a href="#" class="btn_blue" onclick="saveNotifySettingConfirm();"><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
            <a href="#" class="btn_blue" onclick="getNotificationTabInfo();"><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
        </div>
        <!--// 잔액 통보 -->

        <!-- Emergency Credit -->
        <div class="title_basic margin_t30"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.emergencycredit"/><!-- Emergency Credit --></div>

        <!-- 잔액이 0원 일 경우, 자동으로 Emergency Credit Mode로 변경되어 전원이 공급됩니다. -->
        <div class="credit_textbox"><fmt:message key="aimir.hems.prepayment.autoecmodemsg"/></div>

        <div class="credit_textbox">
            <span class="text_blue"><fmt:message key="aimir.hems.prepayment.validperiod"/><!-- 유효기간 --> :</span>
            <span id="duration" class="text_orange2 margin_l5 bold"></span>
            <span class="text_orange2 margin_l5"><fmt:message key="aimir.hems.prepayment.days"/></span>
        </div>

        <!-- 장비인증 보류   현재 사용중인 단말기로 Emergency Credit Mode변경을 원하시면 인증 명칭을 입력한 후 인증 버튼을 클릭해 주세요.(복수개 선택) -->
        <%--
        <div class="credit_textbox text_gray7"><fmt:message key="aimir.hems.prepayment.authenticationmsg"/></div>

        <div class="quote_box">
            <div class="quote">
                <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                <span><input id="" name="" type="text" style="width:50px"></span>
                <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
            </div>
            <div class="quote">
                <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                <span><input id="" name="" type="text" style="width:50px"></span>
                <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
            </div>
            <div class="quote">
                <span class="margin_r5"><fmt:message key="aimir.hems.prepayment.authdevice"/><!-- 인증장비 --></span>
                <span><input id="" name="" type="text" style="width:50px"></span>
                <span><a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a></span>
            </div>
        </div>

        <div class="basic_rightbtn margin_t5">
            <a href="" class="btn_blue" ><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
            <a href="" class="btn_blue" ><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
        </div>
        <!--// Emergency Credit -->
        --%>
    <!-- </div> -->
    </div>
    <!--// tab2 -->

    <!-- tab3 -->
    <!-- <div class="balancebox"> -->
    <div id="customerTariffTabDiv" class="customerTariffTabDiv balancebox" style="display:none;">
       <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.chargeprice"/><!-- 요금단가 --></div>
        <div id="tariffDiv" class="balance_grid"></div>
   </div>
<%-- 
            <!--  전기 (S)  -->
            <form id="dataFormEM">
            <div id="divEM" style="display:none;">
                <table id="dataGridEM" class="border-2px-sum">
                
                    <colgroup>
                        <col width="15%"/>
                        <col width="15%"/>
                        <col width=""/>
                        <col width="15%"/>
                        <col width="15%"/>
                        <col width="15%"/>
                    </colgroup>
                    
                    
                    <tr class="usagetariff-rates-detail-tit border-bottom-2px">
                        <!-- <td><fmt:message key="aimir.tariff"/></td> -->
                        <td><fmt:message key="aimir.season"/></td>
                        <td><fmt:message key="aimir.tou"/></td>
                        <td><fmt:message key="aimir.supplySize"/></td>
                        <!-- <td><fmt:message key="aimir.serviceCharge"/></td> -->
                        <!-- <td><fmt:message key="aimir.adminCharge"/></td> -->
                        <!-- <td><fmt:message key="aimir.distributionNetworkCharge"/></td>-->
                        <!-- <td><fmt:message key="aimir.transmissionNetworkCharge"/></td>-->
                        <td><fmt:message key="aimir.energyDemandCharge"/></td>
                        <td><fmt:message key="aimir.activeEnergyCharge"/></td>
                        <td><fmt:message key="aimir.reactiveEnergyCharge"/></td>
                        <!-- <td><fmt:message key="aimir.rateRevalancingLevy"/></td>-->
                    </tr>
                    <tr id="tmpRowEM" class="usagetariff-rates-detail-data">
                            <!-- <td></td>-->
                            <!-- <td></td>-->
                            <!-- <td></td>-->
                            <!-- <td></td>-->
                            <!-- <td></td>-->
                            <!-- <td></td>-->
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                    </tr>
                </table>
                
            </div>
            </form>
            <!--  전기 (E)  -->

            <!--  가스 (S)  -->
            <div id="divGM" style="display:none;">
                <table id="dataGridTitle" class="border-2px-sum datagrid-title">
                    
                    <colgroup>
                        <col width="15%"/>
                        <col width="15%"/>
                        <col width=""/>
                        <col width="15%"/>
                        <col width="15%"/>
                        <col width="15%"/>
                        
                    </colgroup>
                    
                    <tr class="usagetariff-rates-detail-tit">
                        <td><fmt:message key="aimir.tariff"/></td>
                        <td><fmt:message key="aimir.season"/></td>
                        <td><fmt:message key="aimir.basicRate"/></td>
                        <td><fmt:message key="aimir.unitUsage"/></td>
                        <td><fmt:message key="aimir.salePrice"/></td>
                        <td><fmt:message key="aimir.adjustmentFactor"/></td>
                    </tr>
                </table>
                <div class="scroll-auto">
                    <table id="dataGridGM" class="border-2px-sum datagrid-data">
                        <tr id="tmpRowGM" class="usagetariff-rates-detail-data">
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                    </table>
                </div>
            </div>
            <!--  가스 (E)  -->

            <!--  수도 (S)  -->
            <div id="divWM" style="display:none;">
               <table>
                   <tr>
                       <td width="49%">

                            <div class="usagetariff-title graybold11pt"><fmt:message key="aimir.waterCharge.title1"/></div>
                            <table id="dataGridTitle" class="border-2px-sum datagrid-title">
                                <tr class="usagetariff-rates-detail-tit">
                                    <td width="33%"><fmt:message key="aimir.adminCharge"/></td>
                                    <td width="33%"><fmt:message key="aimir.transmissionNetworkCharge"/></td>
                                    <td width="33%"><fmt:message key="aimir.distributionNetworkCharge"/></td>
                                </tr>
                            </table>

                            <div class="scroll-auto">
                                <table id="dataGridWMCal" class="border-2px-sum datagrid-data">
                                    <tr id="tmpRowWMCal" class="usagetariff-rates-detail-data">
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                    </tr>
                                </table>
                            </div>
                       </td>
                       <td>&nbsp;</td>
                       <td width="49%">
                            <div class="usagetariff-title graybold11pt"><fmt:message key="aimir.waterCharge.title2"/></div>
                            <table id="dataGridTitle" class="border-2px-sum datagrid-title">
                                <tr class="usagetariff-rates-detail-tit">
                                    <td><fmt:message key="aimir.waterCharge.title3"/></td>
                                    <td><fmt:message key="aimir.waterCharge.title6"/></td>
                                    <td><fmt:message key="aimir.waterCharge.title7"/></td>
                                </tr>
                            </table>

                            <div class="scroll-auto">
                                <table id="dataGridWM" class="border-2px-sum datagrid-data">
                                    <tr id="tmpRowWM" class="usagetariff-rates-detail-data">
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                    </tr>
                                </table>
                            </div>
                       </td>
                   </tr>
                </table>
            </div>
            <!--  수도 (E)  -->
 --%>
    <!--// tab3 -->

</div>
</body>
</html>