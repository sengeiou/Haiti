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

<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

<script type="text/javascript">

    var operatorId = "${operatorId}";
    var serviceType = "${serviceType}";
    var maxDay;
    var startDate;
    var endDate;
    var isNotService;

    $(document).ready(function(){

        emergePre();

        $.ajaxSetup({
            async: false
        });

        $(function() { $("#monthlyLeft") .bind("click",  function(event) { monthlyArrow(-1); } ); });
        $(function() { $("#monthlyRight").bind("click",  function(event) { monthlyArrow(1 ); } ); });

        $(function() { $("#monthlyYearCombo") .bind("change", function(event) { getMonthlyMonthCombo(""); } ); });
        $(function() { $("#monthlyMonthCombo").bind("change", function(event) { getSelect(); } ); });
  
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
        } else { // 해당 가젯에 대한 권한이 있을때
           $("#isNotService").hide();
           // 목표및 통보 설정 정보 취득
           getSavingTargetInfo();
        }

        $.ajaxSetup({
            async: true
        });
 
        hide();

    });


    var getContract = function() {

        var params = {
                "operatorId" : operatorId,
                "serviceType" : serviceType
        };

        $.getJSON("${ctx}/gadget/energySavingGoal/getSelect.do",
                params,
                function(result) {

        	        isNotService = result.isNotService;
                    var contractCount = result.contractCount;
                    var contracts = result.contracts;

                    /*if ( 1 > contractCount ) {

                        $("#contractUl").hide();
                    } else if ( 1 == contractCount ) {

                        $("#contracts").pureSelect(contracts);
                        $("#contracts").selectbox();
                        $("#contractUl").hide();
                    } else {*/

                        $("#contracts").pureSelect(contracts);
                        $("#contracts").selectbox();
                        //$("#contractUl").show();

                        $("#contracts").bind("change", function(event) { getSavingTargetInfo(); } );
                    //}

                    //getContract();
                }
            );
    };

    var getSelect = function() {

        var params = {
                "operatorContractId" : $("#contracts").val()
               ,"year" : $("#monthlyYearCombo").val()
               ,"month" : $("#monthlyMonthCombo").val()
        };

        $.getJSON("${ctx}/gadget/energySavingGoal/getContract.do",
                params,
                function(result) {

                    if ( true == result.resultStatus ) {

                        maxDay = result.maxDay;
                        $("#savingTarget").val(result.savingTarget);

                        /*
                        if (result.sms == true) {

                            $("#sms").attr("checked", "checked");
                            $("#address").val(result.smsAddress);
                        }

                        if (result.eMail == true) {

                            $("#eMail").attr("checked", "checked");
                            $("#address").val(result.eMailAddress);
                        }
                        */

                        // 계약번호 선택시 해당하는 통보정보에 맞게 포멧 설정
                        if (result.period1 == true) {

                            $("#period1").attr("checked", "checked");
                        } else {
                            $('#period1').removeAttr("checked");
                        }

                        if (result.period2 == true) {

                            $("#period2").attr("checked", "checked");
                            $("#comValue").val(result.comValue);
                        } else {
                            $('#period2').removeAttr("checked");
                            $("#comValue").val("");
                        }
                        
                        if (result.period3 == true) {

                            $("#period3").attr("checked", "checked");
                        } else {
                            $('#period3').removeAttr("checked");
                        }
                        
                        if (result.period4 == true) {

                            $("#period4").attr("checked", "checked");
                        } else {
                            $('#period4').removeAttr("checked");
                        }
                        
                        if (result.period5 == true) {

                            $("#period5").attr("checked", "checked");
                        } else {
                            $('#period5').removeAttr("checked");
                        }
                        
                        data[0] = result.savingTarget;
                        data[1] = result.maxBill;
                        data[2] = result.lastMonthBill;
                        data[3] = result.lastYearSameMonthBill;
                        data[4] = result.forecastBill;

                        setChartData();
                        renderChart();
                        getContractGrid();
                    } else {

                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                                              "<fmt:message key='aimir.hems.alert.notFoundContract'/>", function() {});
                    }
                }
            );
    };

    var getSavingTargetInfo = function() {
        $.ajaxSetup({
            async: false
        });

        $.getJSON("${ctx}/gadget/energySavingGoal/getYear.do"
                ,{"operatorContractId" : $("#contracts").val()}
                ,function(json) {                 

                    maxDay = json.maxDay;
                    startDate = json.startDate;
                    endDate = json.endDate;

                    $("#monthlyYearCombo").numericOptions({from:startDate.substring(0,4),to:endDate.substring(0,4)});                    
                    $("#monthlyYearCombo").selectbox();
                });

        getMonthlyMonthCombo("", true); // 월 selectBox 내용을 채운다.

        $.ajaxSetup({
            async: true
        });
    };

    var getMonthlyMonthCombo = function(monthVal, flag) {
        $.getJSON("${ctx}/gadget/energySavingGoal/getMonth.do"
                ,{ "year" : $("#monthlyYearCombo").val(), "startDate" : startDate, "endDate" : endDate}
                ,function(json) {

                    var prevMonth = $("#monthlyMonthCombo").val();
                    
                    $("#monthlyMonthCombo").emptySelect();
                    
                    if( prevMonth == ""
                        || prevMonth == null
                        || Number(prevMonth) > Number(json.toMonth) ) {
                        
                        prevMonth = json.toMonth;
                    }
                    
                    var idx = Number(prevMonth) - 1;

                   // $("#monthlyMonthCombo").numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $("#monthlyMonthCombo").numericOptions({from:Number(json.fromMonth),to:json.toMonth});

                    //$("#monthlyMonthCombo").val(monthVal);
                    if( monthVal != null && monthVal != "" ){

                        $("#monthlyMonthCombo").val(monthVal);
                    }

                    $("#monthlyMonthCombo").selectbox();

                    getSelect();
                });

    };
 
    var monthlyArrow = function(val, flag) {

        $.getJSON("${ctx}/gadget/energySavingGoal/getYearMonth.do"
                ,{"year" : $("#monthlyYearCombo").val(), "month" : $("#monthlyMonthCombo").val(), "addVal" : val, "maxDay" : maxDay, "startDate" : startDate, "endDate" : endDate}
                ,function(json) {
                    
                    $("#monthlyYearCombo").val(json.year);
                    $("#monthlyYearCombo").selectbox();

                    getMonthlyMonthCombo(json.month, flag);
                });
    };
 
    var setSavingTarget = function() {

        if($("#savingTarget").val().length <= 0){
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                    "<fmt:message key='aimir.savingGoal.empty'/>", function() {});
            return;
        }
        var params = {
                "operatorContractId" : $("#contracts").val(),
                "savingTarget" : $("#savingTarget").val(),
                "maxDay" : maxDay,
                "year" : $("#monthlyYearCombo").val(),
                "month" : $("#monthlyMonthCombo").val()
        };

        $.getJSON("${ctx}/gadget/energySavingGoal/saveSavingTarget.do",
                params,
                function(result) {
                    if (true == result.resultStatus) {
                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                                "<fmt:message key='aimir.hems.information.successInsert'/>",
                                 function() { }
                        );

                        data[0] = $("#savingTarget").val();
                        array[0][1] = result.savingTarget;

                        /*setChartData();
                        renderChart();
                        renderGrid();
                        setNoticeTarget();

                        if (0 == index) {
                            
                            setGridChartData();
                            renderGridChart();
                        }*/
                        getSelect();
                    } else {

                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                                "<fmt:message key='aimir.hems.alert.failInsert'/>", function() {});
                    }
                }
            );

    };

    var chartStart = " "
        + " <chart formatNumberScale='0'"
        + " yaxisname = '<fmt:message key='aimir.hems.label.usageFeeSymbol'/>'"
        + " yAxisValuesStep='2' "
        + " chartLeftMargin = '0' "
		+ " chartRightMargin= '0' "
		+ " chartTopMargin= '0' "
		+ " chartBottomMargin= '0' "
        + " yAxisValuesStep='2' "
        + fChartStyle_StColumn3D_nobg
        + fChartStyle_Font
        + ">";
      
    var chartLabel = " "
        + " <categories> "
        + " <category label='<fmt:message key='aimir.hems.label.target'/>' /> "            // 목표
        + " <category label='<fmt:message key='aimir.hems.label.maxBill'/>' /> "           // 최고
        + " <category label='<fmt:message key='aimir.hems.label.lastMonthBill'/>' /> "     // 전월
        + " <category label='<fmt:message key='aimir.hems.label.lastYearMonthBill'/>' /> " // 전년도 동월
        + " <category label='<fmt:message key='aimir.hems.label.predictedBill'/>' /> "     // 당월 예측 요금
        + " </categories> ";

    var chartData;
    
    var data = [0, 0, 0, 0, 0];
            
    var setChartData = function() {

        chartData = " "
            + " <dataset> "
            + " <set value='" + data[0] + "' color='" + fChartColor_Step5[4] + "' /> "    // 목표
            + " <set value='" + data[1] + "' color='" + fChartColor_Step5[3] + "' /> "    // 최고
            + " <set value='" + data[2] + "' color='" + fChartColor_Step5[2] + "' /> "    // 전월
            + " <set value='" + data[3] + "' color='" + fChartColor_Step5[1] + "' /> "    // 전년도 동월
            + " <set value='" + data[4] + "' color='" + fChartColor_Step5[0] + "' /> "    // 당월 예측 요금
            + " </dataset> ";
    };

    var gridChartLabel = " "
        + " <categories> "
        + " <category label='<fmt:message key='aimir.hems.label.target'/>' /> "
        + " <category label='<fmt:message key='aimir.hems.label.maxBill'/>' /> "
        + " <category label='<fmt:message key='aimir.hems.label.lastMonthBill'/>' /> "
        + " <category label='<fmt:message key='aimir.hems.label.lastYearMonthBill'/>' /> "
        + " <category label='<fmt:message key='aimir.hems.label.basicUsageFee'/>' /> "
        + " </categories> ";

    var gridChartData;
    
    var gridData;

    var index;
    
    var setGridChartData = function() {

        var gridData = array[index];

        $("#girdChartDay").text(gridData[0]);
        $("#girdChartRate").text(gridData[6] + "% <fmt:message key='aimir.energy.economical'/>");

        gridChartData = " "
            + " <dataset> "
            + " <set value='" + num(gridData[1]) + "' color='" + fChartColor_Step5[4] + "' /> "
            + " <set value='" + num(gridData[2]) + "' color='" + fChartColor_Step5[3] + "' /> "
            + " <set value='" + num(gridData[3]) + "' color='" + fChartColor_Step5[2] + "' /> "
            + " <set value='" + num(gridData[4]) + "' color='" + fChartColor_Step5[1] + "' /> "
            + " <set value='" + num(gridData[5]) + "' color='" + fChartColor_Step5[0] + "' /> "
            + " </dataset> ";
    };
    
    var num = function(value) {

        return value.replace(/,/gi,"");
    };
    
    var chartFinish = " "
        + " </chart> ";

    var array;
     
    var getContractGrid = function() {
        
        var params = {
                "operatorContractId" : $("#contracts").val(),
                "maxDay" : maxDay
        };
    
        $.getJSON("${ctx}/gadget/energySavingGoal/getContractGrid.do",
                params,
                function(result) {

                    var count = result.gridData.length;
                    array = new Array;

                    for (var i = 0; i < count; i++) {

                        var gridData = result.gridData[i];
                        var arrayData = [gridData.day, 
                                            gridData.savingTarget, 
                                            gridData.maxBill, 
                                            gridData.lastMonthBill, 
                                            gridData.lastYearSameMonthBill, 
                                            gridData.bill, 
                                            gridData.rate];
                        array[i] = arrayData;
                    }
                    renderGrid();
                }
            );
    };
        
    $(window).resize(function() {
       // renderChart();
        renderGrid();
    });
    
    var renderChart = function() {
        var chartXml = chartStart + chartLabel + chartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#chartDiv").width(), "230", "0", "0" );
//      var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", "450", "250", "0", "0" );
        myChart.setXMLData(chartXml);
        myChart.setTransparent("transparent");
        myChart.render("chartDiv");
    };

    var renderGridChart = function() {
        var gridChartXml = chartStart + gridChartLabel + gridChartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#gridChartDiv").width(), "230", "0", "0" );
        myChart.setXMLData(gridChartXml);
        myChart.setTransparent("transparent");
        myChart.render("gridChartDiv");
    };

    var gridOn = false;
    var grid;
    var colModel;
    var renderGrid = function() {

        var width = $("#grid").width();

        var store = new Ext.ux.data.PagingArrayStore({
            lastOptions: {params: {start: 0, limit: 10}},
            data : array,
            fields: ["day", 
                        "savingTarget", 
                        "maxBill", 
                        "lastMonthBill", 
                        "lastYearSameMonthBill", 
                        "bill", 
                        {name:"rate", type: "float"}]
        });
        /*
        var store = new Ext.data.ArrayStore({
            fields: ["day", "savingTarget", "maxBill", "lastMonthBill", "lastYearSameMonthBill", "bill", {name:"rate", type: "float"}]
        });
       
        store.loadData(array); */
        
        var rate = function(val) {

            if (val >= 0) {
                return '<span style="color:green;">' + val + '%</span>';
            } else if (val < 0) {
                return '<span style="color:red;">' + val + '%</span>';
            }
            return val;
        };
        
        if (gridOn == false) {

            colModel = new Ext.grid.ColumnModel({
                defaults: {
                    width: (width / 7) -1,
                    sortable: true,
                    menuDisabled: true
                },
                columns: [
                    { id: "day", header: "<fmt:message key='aimir.hems.label.monthPeriod'/>", dataIndex: "day", renderer:addTooltip},
                    { header: "<fmt:message key='aimir.hems.label.target'/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: "savingTarget"},
                    { header: "<fmt:message key='aimir.hems.label.maxBill'/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: "maxBill"},
                    { header: "<fmt:message key='aimir.hems.label.lastMonthBill'/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: "lastMonthBill"},
                    { header: "<fmt:message key='aimir.hems.label.lastYearMonthBill'/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: "lastYearSameMonthBill"},
                    { header: "<fmt:message key='aimir.hems.label.basicUsageFee'/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: "bill"},
                    { header: "<fmt:message key='aimir.energy.economical.rate'/>", renderer: rate, dataIndex: "rate"}
                ]
            });

            grid = new Ext.grid.GridPanel({
                height: 268,
                store: store,
                colModel : colModel,
                width: width,
                stripeRows : true,
                columnLines: true,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: store,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });

            grid.render("grid");

            grid.on('rowclick', function(grid, rowIndex, e) {

                // 페이징 되었을 경우, 선택한 rowIndex는 페이지 시작 (start)과 페이징된 그리드의 rowIndex를 더한 값이 된다.
                index = grid.getStore().lastOptions.params.start + rowIndex;
                //index = rowIndex;
                setGridChartData();
                renderGridChart();
            });  

            index = 0;
            
//            setGridChartData();
//            renderGridChart();
            
            gridOn = true;
        } else {
            grid.setWidth(width);
            grid.reconfigure(store, colModel);
            var bottomToolbar = grid.getBottomToolbar();
            bottomToolbar.bindStore(store);
        }
        setGridChartData();
        renderGridChart();
    };

    var addTooltip = function(val, cell, record) {
        return '<div qtip="'+ val +'">'+ val +'</div>';     
    };
    /*
    var checked = function(name1, name2) {

        if ($("#" + name1).is(":checked")) {
            
            $("#" + name1).removeAttr("checked");
        } else {
            
            $("#" + name1).attr("checked", "checked");
            $("#" + name2).removeAttr("checked");
        }
    };

    var clicked = function(name1, name2) {

        if ($("#" + name1).is(":checked")) {
            
            if ($("#" + name2).is(":checked")) {
                
                $("#" + name2).removeAttr("checked");
            }
        }
    };
    */
    var setNoticeTarget = function() {
        // 초과 %입력, 미체크시는 강제적으로 체크한 후, 등록함
        if(checkVal("period2") == false && $("#comValue").val().length != 0){
            $("#period2").attr("checked", "checked");
        }
 
        var params = {
                "operatorContractId" : $("#contracts").val(),
                "maxDay" : maxDay,
                //"smsYn" : checkVal("sms"),
                //"eMailYn" : checkVal("eMail"),
                "period_1" : checkVal("period1"),
                "period_2" : checkVal("period2"),
                "period_3" : checkVal("period3"),
                "period_4" : checkVal("period4"),
                "period_5" : checkVal("period5"),
                //"smsAddress" : $("#address").val(),
                //"eMailAddress" : $("#address").val(),
                "conditionValue" : $("#comValue").val()
        };

        // 초과 통보 체크, 초과 %미입력시 에러메시지 출력
        if(checkVal("period2") == true && $("#comValue").val().length == 0){
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                    "<fmt:message key='aimir.hems.alert.saveNotificationPeriod2'/>", function() {$("#period2").removeAttr("checked");});           
        } else if($("#comValue").val().length != 0 && ($("#comValue").val()).match(/[^0-9]+/)){ // 초과 % 숫자 체크
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                    "<fmt:message key='aimir.hems.alert.exceedValue.onlyDigit'/>", function() {}); 
        }else {
            $.getJSON("${ctx}/gadget/energySavingGoal/saveNoticeTarget.do",
                    params,
                    function(result) {
    
                        if (true == result.resultStatus) {
    
                            Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                                    "<fmt:message key='aimir.hems.information.successInsert'/>", function() {});
                        } else {
    
                            Ext.MessageBox.alert("<fmt:message key='aimir.hems.label.EnergySavingGoals'/>",
                                    "<fmt:message key='aimir.hems.alert.failInsert'/><fmt:message key='aimir.hems.confirm.insertSavingGoal'/>", function() {});
                        }
                    }
                );
        }
    };

    var checkVal = function(name) {

        if ($("#" + name).is(":checked")) {

            return true;
        } else {

            return false;
        }
    };

</script>

</head>
<body>
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
    <div id="contractUl" class="topsearch">
		<div class="contract borderbottom_blue">
	        <table>
	            <tr>
	                <td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
	                <td><select id="contracts"  style="width:500px" ></select></td>
	            </tr>
	        </table>
        </div>
	</div>
    <!--//contract no.-->
   
   <div class="overflow_hidden">
        <!--max left  -->
        <div class="saving_left">
        
            <!-- left 1:  -->
            <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.savingGoalsMgmt'/></div>
            
            <div class="goal2">
                <div class="saving_goal">
                    <table>
                        <tr>
                        	<td class="prevspace"><span class="icon_prev"><button id="monthlyLeft" type="button"></button></span></td>
				            <td><select id="monthlyYearCombo" style="width:70px"></select></td>
				            <td><label class="datetxt"><fmt:message key="aimir.year1" /></label></td>
				            <td><select id="monthlyMonthCombo" style="width:35px"></select></td>
				            <td><label class="datetxt"><fmt:message key="aimir.day.mon" /></label></td>
				            <td></td>
				            <td><span class="icon_next"><button id="monthlyRight" type="button"></button></span></td>
				            <td><span class="text_lightgray margin_side5">|</span></td>
                            <td><input type="text" id="savingTarget"  style="width:100px" class="target"/></td>
                            <td class="btnspace"><a href="javascript:setSavingTarget();" class="btn_blue" title="<fmt:message key='aimir.hems.inform.mouseover.energySavingGoalSettings'/>"><span><fmt:message key='aimir.set'/></span></a></td>
                        </tr>
                    </table>
                </div>
            </div>
            
            <div id="chartDiv" class="margin_t40"></div>
            <!--// left 1  -->
            
            <!-- left 2 -->
            <div class="clear">
            
                <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.operator.notificationSet'/></div>
                
                <p class="text_orange margin_b5"> * <fmt:message key='aimir.hems.label.savingGoalNotification'/></p>
                
                <div class="setup_box">
                    <!--
                    <div class="setup">
                        <ul>
                            <li class="title_blk"><span class="icon_triangle"></span>통보</li>
                            <li class="remark">통보 수단을 설정해 주세요.</li>
                            <li>
                                <div>
                                    <span><input type="checkbox" id="sms" onclick="javascript:clicked('sms', 'eMail');" class="checkbox" /><a href="javascript:checked('sms', 'eMail');" >SMS</a></span>
                                    <span><input type="checkbox" id="eMail" onclick="javascript:clicked('eMail', 'sms');" class="checkbox"/><a href="javascript:checked('eMail', 'sms');" >E-Mail</a></span>
                                    <span><input type="text" id="address" /></span>
                                </div>
                            </li>
                        </ul>
                    </div>
                    -->
                    <div class="setup">
                        <ul>
                            <li class="title_blk"><span class="icon_triangle"></span><fmt:message key='aimir.hems.label.savingGoalLastMonthResult'/></li>
                            <li class="remark"><fmt:message key='aimir.hems.label.savingGoalSettingNotificationPeriod'/></li>
                            <li>
                                <div>
                                    <span><input type="checkbox" id="period1" class="checkbox2"/></span>
                                    <span> <fmt:message key='aimir.hems.label.savingGoalNotificationPeriod1'/></span>
                                </div>
                            </li>
                        </ul>
                    </div>
                
                    <div class="setup">
                        <ul>
                            <li class="title_blk"><span class="icon_triangle"></span><fmt:message key='aimir.hems.label.savingGoalMonthResult'/></li>
                            <li class="remark"><fmt:message key='aimir.hems.label.savingGoalSettingNotificationPeriod'/></li>
                            <li>
                                <div>
                                    <span><input type="checkbox" id="period2" class="checkbox2"/></span>
                                    <span> <fmt:message key='aimir.hems.label.savingGoalNotificationPeriod2'/></span>
                                    <span><input type="text" id="comValue" class="settarget" ></span>
                                    <span> % <fmt:message key='aimir.hems.label.savingGoalPercentExceedNotification'/></span>
                                </div>
                            </li>
                            
                            <li>
                                <div>
                                    <span><input type="checkbox" id="period3"  class="checkbox2"/></span>
                                    <span> <fmt:message key='aimir.hems.label.savingGoalNotificationPeriod3'/></span>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <span><input type="checkbox" id="period4"  class="checkbox2"/></span>
                                    <span><fmt:message key='aimir.hems.label.savingGoalNotificationPeriod4'/></span>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <span><input type="checkbox" id="period5"  class="checkbox2"/></span>
                                    <span> <fmt:message key='aimir.hems.label.savingGoalNotificationPeriod5'/></span>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            
                <div class="saving_rightbtn">
                    <a href="javascript:setNoticeTarget();" class="btn_blue" title="<fmt:message key='aimir.hems.inform.mouseover.energySavingGoalNotification'/>"><span><fmt:message key='aimir.set'/></span></a>
                </div>
        
            </div>
            <!--// left 2 -->
        
        </div>
        <!--// max left  -->
        
        <!-- max right  -->
        <div class="saving_right">
            
            <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.savingGoalsResult'/></div>
            
            <div class="goal2">
                <div class="saving_goal2">
                    <span class="grid_savingtxt"><span id="girdChartDay" ></span></span>
                    <span class="text_lightgray margin_side">|</span>
                    <span id="girdChartRate" class="grid_savingtxt text_orange2"></span>
               </div>
            </div>
           
            <div id="gridChartDiv" class="margin_t40"></div>
            
            <p class="saving_comment"> * <fmt:message key='aimir.hems.label.savingGoalHistory'/></p>
            <div id="grid"></div>
        
        </div>
        <!--// max right  -->
    </div>
    
</div>
</body>
</html>