<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>통신이력관리</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
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
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">

        //탭초기화
        // 값 0 - 숨김처리
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs     = {hourly:1,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

        var pieChartDataXml;
        var pieChart;
        var barChartDataXml;
        var barChart;
        var pieSvcTypeCode = "";    // Bar Chart 클릭 시 선택된 값
        var hourlyStartDate = $("#hourlyStartDate").val();
        var supplierId = ${supplierId};

        //$(document).ready(function() {
        Ext.onReady(function() {
            //$.ajax({
            //    type:"POST",
            //    dataType:"json",
            //    url:"${ctx}/common/getUserInfo.do",
            //    success:function(json, status)
            //    {
            //        //유저정보가 널이 아닌경우..
            //        if(json.supplierId != "")
            //        {
            //                //supplierId = json.supplierId;
            //                //서플라이어 id값 셋팅.
            //                $("#supplierId2").val(json.supplierId);
            //        }
            //    },
            //    error:function(request, status)
            //    {
            //        alert("user info fetch failed");
            //    }
            //});// ajaxEnd

            $("#svcTypeCode").selectbox();
            $("#protocolCode").selectbox();
            $("#senderType").selectbox();           

            //2.검색 조건 값을 가지고 온다.
            var conditionArray = getConditionArray();

            updatePieChart(conditionArray);
            updateBarChart(conditionArray);

            //그리드를 가지고 온다..
            getCommLogList(conditionArray);

            // grid stats 보여주기
            setStatisticsData2();
        });

        //##########################################################################
        //getCommLogList start
        //##########################################################################

        //초기 값은 false
        var commLogGridOn = false;
        var commLogGrid;
        var commLogColModel;
        var commLogCheckSelModel;
        var commLogStore;

        //comm log grid fetch js func
        function getCommLogList(conditionArray) {
            var width = $("#commLogDiv").width();
            var rowSize = 10;

            //json형태로 저장된 그리드 datas
            commLogStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: rowSize}},
                url : "${ctx}/gadget/device/commLog/getCommLogGridData2.do",
                //베이스 파라메터 설정.
                baseParams : {
                    protocolCode : conditionArray[0],
                    senderId : conditionArray[1],
                    receiverId : conditionArray[2],
                    //기간 타입 hourly 일경우 0
                    period : conditionArray[3],
                    startDate : conditionArray[4],
                    endDate : conditionArray[5],
                    group : conditionArray[6],
                    groupData : conditionArray[7],
                    svcTypeCode : conditionArray[8],
                    supplierId : conditionArray[9],
                    pageSize : "10"
                },
                //comm log grid totol 카운트 값
                totalProperty : 'commloggriddatacount',
                root : 'listcommlog',
                //fields : ["idx1", "time", "svcTypeCode.descr", "protocolCode.descr", "senderId", "sender", "receiver",
                //          "result", "strSendBytes", "strReceiverBytes", "strTotalCommTime", "operationCode"],
                fields : ["idx1", "time", "svcTypeCode", "protocolCode", "senderId", "sender", "receiver",
                          "result", "strSendBytes", "strReceiverBytes", "strTotalCommTime", "operationCode"],
                listeners : {
                    beforeload: function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }
            });//comlogStore End

            //comm log 컬럼 모델 설정
            commLogColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: '<fmt:message key="aimir.number"/>', dataIndex: 'idx1', width: 70}
                   ,{header: '<fmt:message key="aimir.time"/>', dataIndex: 'time', width:(width-84)/10}
                   //,{header: '<fmt:message key="aimir.datatype"/>', dataIndex: 'svcTypeCode.descr', width:(width-84)/10}
                   //,{header: '<fmt:message key="aimir.protocol"/>', dataIndex: 'protocolCode.descr', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.commlog.packettype"/>', dataIndex: 'svcTypeCode', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.protocol"/>', dataIndex: 'protocolCode', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.sender"/>', dataIndex: 'sender', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.receiver"/>', dataIndex: "receiver", width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.header.sendbytes"/>', dataIndex: 'strSendBytes', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.header.receivebytes"/>', dataIndex: 'strReceiverBytes', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.result"/>', dataIndex: 'result', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.totalcommtime"/>', dataIndex: 'strTotalCommTime', width:(width-84)/10}
                   ,{header: '<fmt:message key="aimir.operationcode"/>', dataIndex: 'operationCode', width:(width-84)/10}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if (commLogGridOn == false) {

                //그리드 패널 만들어서 express하는 부분.
                commLogGrid = new Ext.grid.GridPanel({
                    store : commLogStore,
                    colModel : commLogColModel,
                    sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll : false,
                    width : width,
                    style : 'align:center;',
                    height : 305,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg: 'loading...'
                    },
                    renderTo : 'commLogDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar : new Ext.PagingToolbar({
                        pageSize : rowSize,
                        store : commLogStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
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
        };//func getCommLogList End

        //### getCommLogList  end ###

        //     엑셀 export func
        var winCommLog;
        function openExcelReport()
        {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            var dateType = new Array("hour","day","period","week","month","","dayWeek","season","year");

            obj.tabType = dateType[$('#searchDateType').val()];

            if($('#searchDateType').val()==0)
            {
                obj.search_from = $('#searchStartDate').val()+$('#searchStartHour').val()
                +"@"+$('#searchEndDate').val()+ $('#searchEndHour').val();
            }
            else
            {
                obj.search_from = $('#searchStartDate').val()+"@"+$('#searchEndDate').val();
            }

            obj.supplierId  = supplierId;
            //obj.supplierId  = $("#supplierId2").val();

            obj.svcTypeCode = $("#svcTypeCode").val();
            obj.protocolCode = $("#protocolCode").val();
            obj.senderId = $("#senderId").val();
            obj.receiverId = $("#receiverId").val();

            //hourly tab 파라메터값 설정
            obj.hourlyStartDate = $("#hourlyStartDate").val();
            obj.hourlyEndDate = $("#hourlyEndDate").val();
            obj.hourlyStartHourCombo_input = $("#hourlyStartHourCombo_input").val();
            obj.hourlyEndHourCombo_input = $("#hourlyEndHourCombo_input").val();

            //period tab 파라메터값 설정
            obj.periodType_input = $("#periodType_input").val();
            obj.periodStartDate = $("#periodStartDate").val();
            obj.periodEndDate = $("#periodEndDate").val();

            //weekly tab 파라메터값 설정
            obj.weeklyYearCombo_input = $("#weeklyYearCombo_input").val();
            obj.weeklyMonthCombo_input = $("#weeklyMonthCombo_input").val();
            obj.weeklyWeekCombo_input = $("#weeklyWeekCombo_input").val();

            //month tab 파라메터값 설정
            obj.monthlyYearCombo_input = $("#monthlyYearCombo_input").val();
            obj.monthlyMonthCombo_input = $("#monthlyMonthCombo_input").val();

            //obj는 파라매터 값 jsp 에서 받아서 처리.
            if(winCommLog)
                winCommLog.close();
            winCommLog = window.open("${ctx}/gadget/device/commLog/commLogExcelDownloadPopup.do", "CommLogExcel", opts);
            winCommLog.opener.obj = obj;
        }

        // grid stats 보여주는 func
        function setStatisticsData2()
        {
            var conditionArray = getConditionArray();

            $.ajax({
                type:"POST",
                data:{protocolCode:conditionArray[0],
                    senderId:conditionArray[1],
                    receiverId:conditionArray[2],
                    period:conditionArray[3],
                    startDate:conditionArray[4],
                    endDate:conditionArray[5],
                    group:conditionArray[6],
                    groupData:conditionArray[7],
                    svcTypeCode:conditionArray[8],
                   // page:"1",
                  //  pageSize:"10",
                    supplierId:conditionArray[9]},

                dataType:"json",
                url:"${ctx}/gadget/device/commLog/getCommLogStatData.do",
                success:function(data, status)
                {

                    //alert("성공");
                    var stats = data.statisticsData;

                    $('#totalSender2').html(stats.totalSender);
                    $('#avgSender2').html(stats.avgSender);
                    $('#totalReceiver2').html(stats.totalReceiver);
                    $('#avgReceiver2').html(stats.avgReceiver);
                },
                error:function(request, status)
                {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"comm status view failed");
                }
            });// ajaxEnd
        }

        function getSupplierId() {
            return supplierId;
        };

        // 검색시 실행되는func
        function searchList() {
            var searchDateType = $('#searchDateType').val();

            if (!validationDateCheck()) {
                return;
            }

            //검색 조건을 가지고 온다.
            var conditionArray = getConditionArray();

            updatePieChart(conditionArray);
            updateBarChart(conditionArray);

            //컴 그리드 가져오기
            getCommLogList(conditionArray);

            //stat 데이타 가져오기
            setStatisticsData2();
        };

        var send = function() {
            searchList();
        };

        function validationDateCheck() {
            var searchStartDate = $('#searchStartDate').val();
            var searchEndDate = $('#searchEndDate').val();
            var searchStartHour = $('#searchStartHour').val();
            var searchEndHour = $('#searchEndHour').val();
            var searchDateType = $('#searchDateType').val();

            if(searchDateType == "" || searchStartDate == "" || searchEndDate == "" ||
                    (searchDateType != DateType.HOURLY && searchDateType != DateType.PERIOD)) {
                return true;
            }

            var sDateObj = null;
            var eDateObj = null;
            var cDateObj = null
            var sreg = /(\d{4})(\d{2})(\d{2})/g;
            var ereg = /(\d{4})(\d{2})(\d{2})/g;

            if (sreg.exec(searchStartDate) != null) {
                sDateObj = new Date(Number(RegExp.$1),(Number(RegExp.$2)-1),Number(RegExp.$3));
                cDateObj = new Date(Number(RegExp.$1),(Number(RegExp.$2)-1),Number(RegExp.$3));
            }

            if (ereg.exec(searchEndDate) != null) {
                eDateObj = new Date(Number(RegExp.$1),(Number(RegExp.$2)-1),Number(RegExp.$3));
            }
            if (cDateObj != null) {
                if (searchDateType == DateType.HOURLY) {
                    // Hourly : 7일만 검색
                    cDateObj.setDate(cDateObj.getDate() + 7);

                    if (eDateObj.getTime() >= cDateObj.getTime()) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.device.onlySearch7days"/>");
                        return false;
                    }
                } else if(searchDateType == DateType.PERIOD) {
                    // Period : 1달만 검색
                    cDateObj.setMonth(cDateObj.getMonth() + 1);

                    if (eDateObj.getTime() > cDateObj.getTime()) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.device.onlySearch1month"/>");
                        return false;
                    }
                }
            }

            return true;
        }

        // Pie Chart click 시 호출되는 function
        function clickPieChart(svcTypeCode) {
            var conditionArray = getConditionArray();
            pieSvcTypeCode = svcTypeCode;
            conditionArray[8] = svcTypeCode;

            updateBarChart(conditionArray);
            getFlexObject('commLogGridFlex').searchList(conditionArray);
        }

        // Bar Chart click 시 호출되는 function
        function clickBarChart(selectDate) {
            var conditionArray = getConditionArray();
            if (pieSvcTypeCode != "") {
                conditionArray[8] = pieSvcTypeCode;
            }

            conditionArray[4] = selectDate;
            conditionArray[5] = selectDate;

            getFlexObject('commLogGridFlex').searchList(conditionArray);
        }



        // 조회시 사용될 검색조건 func
        function getConditionArray()
        {
            var searchStartDate = $('#searchStartDate').val();
            var searchEndDate = $('#searchEndDate').val();

            var searchStartHour = $('#searchStartHour').val();
            var searchEndHour = $('#searchEndHour').val();
            var searchDateType = $('#searchDateType').val();


        /*     alert( "시간"+ searchStartHour );
            Ext.Msg.alert('<fmt:message key='aimir.message'/>', "end시간"+ searchEndHour);
            Ext.Msg.alert('<fmt:message key='aimir.message'/>', searchDateType); */


            //처음 로드시에 날짜가 널값이 초기값 설정
            if(searchStartDate =="" && searchEndDate=="")
            {

                searchStartDate= getToday();
                searchEndDate = getToday();
                searchStartHour="00";
                searchEndHour="23";
            }

            var arrayObj = Array();

            arrayObj[0] = $('#protocolCode').val();
            arrayObj[1] = $('#senderId').val();
            arrayObj[2] = $('#receiverId').val();
            arrayObj[3] = searchDateType;

            //DateType.HOURLY 값은  0
            if (searchDateType == DateType.HOURLY) {
                arrayObj[4] = searchStartDate + searchStartHour;
                arrayObj[5] = searchEndDate + searchEndHour;
            } else if (searchDateType == DateType.MONTHLY) {
                arrayObj[4] = searchStartDate.substring(0,6);
                arrayObj[5] = searchEndDate.substring(0,6);
            } else {
                arrayObj[4] = searchStartDate;
                arrayObj[5] = searchEndDate;
            }

            arrayObj[6] = "";
            arrayObj[7] = "";

            arrayObj[8] = $('#svcTypeCode').val();

            //###서플라이어 id값 셋팅
            arrayObj[9] = supplierId;

            return arrayObj;
        };

        var setStatisticsData = function(_array)
        {


            $('#totalSender').html(_array[0]);
            $('#avgSender').html(_array[1]);
            $('#totalReceiver').html(_array[2]);
            $('#avgReceiver').html(_array[3]);
        };

        //메세지 프로퍼티 헤더값 셋팅.
        function getFmtMessage()
        {
            var fmtMessage = Array();
            var idx = 0;

            fmtMessage[idx++] = "<fmt:message key="aimir.fail"/>";
            fmtMessage[idx++] = "<fmt:message key="aimir.success"/>";
            fmtMessage[idx++] = "<fmt:message key="aimir.number"/>";            // 번호
            fmtMessage[idx++] = "<fmt:message key="aimir.time"/>";              // Time
            fmtMessage[idx++] = "<fmt:message key="aimir.commlog.packettype"/>";          // Data Type
            fmtMessage[idx++] = "<fmt:message key="aimir.protocol"/>";          // Protocol
            fmtMessage[idx++] = "<fmt:message key="aimir.sender"/>";            // Sender
            fmtMessage[idx++] = "<fmt:message key="aimir.receiver"/>";          // Receiver
            fmtMessage[idx++] = "<fmt:message key="aimir.header.sendbytes"/>";        // Send Bytes
            fmtMessage[idx++] = "<fmt:message key="aimir.header.receivebytes"/>";      // Receive Bytes//수신량
            fmtMessage[idx++] = "<fmt:message key="aimir.result"/>";            // Result
            fmtMessage[idx++] = "<fmt:message key="aimir.total.commtime"/>";    // Total Comm Time
            fmtMessage[idx++] = "<fmt:message key="aimir.operationcode"/>";    //
            fmtMessage[idx++] = "<fmt:message key="aimir.env.error"/>";         // 사용할 수 없는 환경입니다.

            return fmtMessage;
        };

        function updatePieChart(conditionArray) {
            emergePre();

            $.getJSON('${ctx}/gadget/device/commLog/getReceivePieChartData.do'
                    ,{protocolCode:conditionArray[0],
                     senderId:conditionArray[1],
                     receiverId:conditionArray[2],
                     period:conditionArray[3],
                     startDate:conditionArray[4],
                     endDate:conditionArray[5],
                     group:conditionArray[6],
                     groupData:conditionArray[7],
                     svcTypeCode:conditionArray[8],
                     supplierId:conditionArray[9]}


                    ,function(json) {
                         var list = json.chartDatas;
                         pieChartDataXml = "<chart "
                             //+ "caption='<fmt:message key="aimir.header.receivebytes"/>' "
                             + "showValues='1' "
                             + "showPercentValues='1' "
                             + "showPercentInToolTip='1' "
                             + "pieRadius='90' "
                             + "showZeroPies='0' "
                             + "showLabels='0' "
                             + "showLegend='1' "
                             + "legendPosition='RIGHT' "
                             + "manageLabelOverflow='1' "
                             + "enableSmartLabels='0' "
                             + fChartStyle_Common
                             + fChartStyle_Font
                             + fChartStyle_Pie3D_nobg
                             + ">";
                         var labels = "";
                         var svcTypeName = "";

                         for( index in list){
                             if(index != "indexOf") {
                                 switch (list[index].svcTypeCode)
                                 {
                                     case "O":
                                         svcTypeName = "<fmt:message key='aimir.pie.command' />";
                                         break;
                                     case "M":
                                         svcTypeName = "<fmt:message key='aimir.pie.metering' />";
                                         break;
                                     case "E":
                                         svcTypeName = "<fmt:message key='aimir.pie.event' />";
                                         break;
                                 }

                                 labels  += "<set label='" + svcTypeName + "' value='" + list[index].cnt + "' toolText='" + svcTypeName + ":{br}" + list[index].cnt + " bytes' "
//                                           + " link='j-clickPieChart-" + list[index].svcTypeCode + "' />";
											 + "/>'";
                             }
                         }

                         if(list.length == 0) {
                             labels = "<set label='' value='1' color='E9E9E9' toolText='' />";
                         }

                         pieChartDataXml += labels + "</chart>";
                         pieChartRender();
                    }
            );//getJson End
        }

        /*
        바 차트 fetch
        */
        function updateBarChart(conditionArray) {
            emergePre();
            $.getJSON('${ctx}/gadget/device/commLog/getBarChartData.do'
                    ,{protocolCode:conditionArray[0],
                     senderId:conditionArray[1],
                     receiverId:conditionArray[2],
                     period:conditionArray[3],
                     startDate:conditionArray[4],
                     endDate:conditionArray[5],
                     group:conditionArray[6],
                     groupData:conditionArray[7],
                     svcTypeCode:conditionArray[8],
                     supplierId:conditionArray[9]}
                    ,function(json) {
                        var list = json.chartDatas;
                        list.sort(function (a, b) {
                            if ( Number(a['date']) > Number(b['date']) ) {
                                return 1;
                            } else {
                                return -1;
                            }
                        });
                        var total =   list.length;
                        var step = (total < 6) ? 1 : ((total%6 == 0)? (total/6) : (total/6 + 1));
                        var step = (total < 6) ? 1 : (total/6);
                        barChartDataXml = "<chart "
                            //타이틀
                            + "caption='<fmt:message key='aimir.sendreceivevolcharts'/>' "
                            + "chartLeftMargin='0' "
                            + "chartRightMargin='0' "
                            + "chartTopMargin='10' "
                            + "chartBottomMargin='0' "
                            + "showValues='0' "
                            + "showLabels='1' "
                            + "showLegend='1' "
                            //서픽스
                            + "yAxisName='byte' "
                            + "numberSuffix=' ' "
                            //+ "labelStep='" + 10 + "' "
                            + "labelDisplay='AUTO' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_MSColumn3D_nobg
                            + ">";
                        var categories = "<categories>";


                        var dataset1 = "<dataset seriesName='<fmt:message key="aimir.header.receivebytes"/>'>";
                        var dataset2 = "<dataset seriesName='<fmt:message key="aimir.header.sendbytes"/>'>";

                        //alert(list.length);

                        if ( list.length > 0 )
                        {


                            for (i=0; i<list.length; i++)
                            {
                                 categories += "<category label='"+list[i].formatDate+"' />";
                                 dataset1 += "<set value='"+list[i].rcvCnt+"' link='j-clickBarChart-" + list[i].date + "' />";
                                 dataset2 += "<set value='"+list[i].sendCnt+"' link='j-clickBarChart-" + list[i].date + "' />";
                            }


                        }
                        else
                        {
//                          alert("데이타없다");
                            categories += "<category label='No data to display' />";
                            dataset1 += "<set value='0' />";
                            dataset2 += "<set value='0' />";

                        }

                        categories += "</categories>";
                        dataset1 += "</dataset>";
                        dataset2 += "</dataset>";

                        barChartDataXml += categories + dataset1 + dataset2 + "</chart>";

                        //alert(categories + dataset1 + dataset2);
                        barChartRender();
                    }
            );
        }

        window.onresize = fcChartRender;
        function fcChartRender() {
            pieChartRender();
            barChartRender();
        }

        function pieChartRender() {
        	pieChart = new FusionCharts({
        		id: 'pieChartId',
    			type: 'Pie3D',
    			renderAt : 'pieChartDiv',
    			width : $('#pieChartDiv').width(),
    			height : '300',
    			dataSource : pieChartDataXml
    		}).render();
        	
        	/* pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId", $('#pieChartDiv').width() , "300", "0", "0");
            pieChart.setDataXML(pieChartDataXml);
            pieChart.setTransparent("transparent");
            pieChart.render("pieChartDiv"); */
        }

        function barChartRender() {
        	barChart = new FusionCharts({
        		id: 'barChartId',
    			type: 'MSColumn3D',
    			renderAt : 'barChartDiv',
    			width : $('#barChartDiv').width(),
    			height : '300',
    			dataSource : barChartDataXml
    		}).render();
        	
        	/* barChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "barChartId", $('#barChartDiv').width() , "300", "0", "0");
            barChart.setDataXML(barChartDataXml);
            barChart.setTransparent("transparent");
            barChart.render("barChartDiv"); */
        }


        //오늘 날짜를 구한다.,
        function getToday()
        {

            var currentTime = new Date();
            var month = (currentTime.getMonth() + 1);
            var day = currentTime.getDate();//-8
            var year = currentTime.getFullYear();

            //alert( day.toString().length );
            if ( day.toString().length == 1)
            {
                day = "0" + day.toString();
            }

            if ( month.toString().length == 1)
            {
                month = "0" + month.toString();
            }



            var today = year.toString() + month.toString() + day.toString();

            return today;

        }



    </script>

    <!-- extJs grid chart 부분 스타일 오버라이드-->
    <style type="text/css">
     .x-grid3-row-table
    {
     text-align: center;

    }

    .temp
    {
     text-align: center;
    }

    .x-grid3-hd-inner
    {
        text-align: center;
        font-weight: bold;
    }


    .x-grid3-col-6
    {
     text-align: right;
    }

    .x-grid3-col-7
    {
     text-align: right;

    }

    .x-grid3-col-9
    {
         text-align: right;
    }


    </style>
</head>

<body>
<input type="hidden" id="supplierId2" value="" >
    <!-- search-background DIV (S) -->
    <div class="search-bg-withtabs">

        <div class="dayoptions">
        <%@ include file="../commonDateTab2.jsp" %>


        </div>
        <div class="dashedline"></div>

        <!--검색조건-->
        <div class="searchoption-container">
            <table class="searchoption wfree">
                <tr>

                <!-- packet Type selectbox-->
                    <td class="gray11pt withinput"><fmt:message key="aimir.commlog.packettype"/></td>

                    <td>

                        <select id="svcTypeCode" name="svcTypeCode"  style="width:100px;">
                            <option value=""><fmt:message key="aimir.all"/></option>

                          <c:forEach var="packetlist" items="${packetTypeList}">
                            <option value="${packetlist.shortName}">${packetlist.name}</option>
                          </c:forEach>



                        </select>
                    </td>
                    <td class="space20">
                    </td>

                    <td class="gray11pt withinput">
                        <fmt:message key="aimir.protocol"/>
                    </td>
                    <td>
                        <select id="protocolCode" name="protocolCode" id="system" style="width:100px;">
                          <option value=""><fmt:message key="aimir.all"/></option>
                          <c:forEach var="protocolCode" items="${protocolCodes}">
                          <option value="${protocolCode.id}">${protocolCode.name}</option>
                          </c:forEach>
                        </select>
                    </td>
                    <td class="space20"></td>
                    
                    <td class="withinput"><fmt:message key="aimir.sendertype"/></td>
                    <td>
                    	<select id="senderType" name="senderType" style="width:100px;">
                    	<option value=""><fmt:message key="aimir.all"/></option>
                        <c:forEach var="senderType" items="${senderTypes}">
                        <option value="${senderType.id}">${senderType.name}</option>
                        </c:forEach>
                        </select>
                    </td>
                    <td class="space20"></td>

                    <td class="withinput"><fmt:message key="aimir.senderid"/></td>
                    <td><input id="senderId" name="senderId" type="text" value=""></td>
                    <td class="space20"></td>

                    <td class="withinput"><fmt:message key="aimir.receiverid"/></td>
                    <td><input id="receiverId" name="receiverId" type="text"></td>
                    <td class="space20"></td>

                    <td>
                        <div id="btn">
                            <ul><li><a href="javascript:send();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <!--검색조건 끝-->

    </div>
    <!-- search-background DIV (E) -->

    <!-- 파이 차트 div 부분. -->
    <div class="gadget_body">
        <div class="chart_left" id="pieChartDiv"></div>
        <div class="chart_right" id="barChartDiv"></div>
    </div>

    <%-- <div id="gadget_body2" class="commlog-total margin-t10px">
        <ul>
            <span class="blue11pt"><fmt:message key="aimir.sendbyte.total"/> : </span>
            <span><label class="blue11pt" id="totalSender">0</label></span>
            <span class="blue11pt"><fmt:message key="aimir.avg"/> <fmt:message key="aimir.send.bytes"/> : </span>
            <span><label class="blue11pt" id="avgSender">0</label></span>
            <span class="blue11pt"><fmt:message key="aimir.rcvbyte.total"/> : </span>
            <span><label class="blue11pt" id="totalReceiver">0</label></span>
            <span class="blue11pt"><fmt:message key="aimir.avg"/> <fmt:message key="aimir.receivebytes"/> : </span>
            <span><label class="blue11pt" id="avgReceiver">0</label></span>
        </ul>
    </div>
    --%>

    <!-- flex object part -->

    <%--
    <div id="gadget_body2"  >
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="280" id="commLogGridFlexEx">
            <param name='wmode' value='transparent' />
            <param name="movie" value="${ctx}/flexapp/swf/commLogGrid.swf" />
            <!--[if !IE]>-->
            <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/commLogGrid.swf" width="100%" height="280" id="commLogGridFlexOt">
            <!--<![endif]-->
            <!--[if !IE]>-->
            </object>
            <!--<![endif]-->
        </object>
    </div> --%>


  <!-- stats 2 part -->
     <div id="gadget_body2" class="commlog-total margin-t10px">
        <ul>
            <span class="blue11pt">
                <fmt:message key="aimir.totalsendbytes"/> :
            </span>
            <span>
                <label class="blue11pt" id="totalSender2">0</label>
            </span>
            <span class="blue11pt">
                <fmt:message key="aimir.avgsendvolume"/> :
            </span>
            <span>
                <label class="blue11pt" id="avgSender2">0</label>
            </span>
            <span class="blue11pt">
                <fmt:message key="aimir.totalreceivebytes"/> :
            </span>
            <span>
                <label class="blue11pt" id="totalReceiver2">0</label>
            </span>
            <span class="blue11pt">
                <fmt:message key="aimir.avgreceivevolume"/> :
            </span>
            <span>
                <label class="blue11pt" id="avgReceiver2">0</label>
            </span>
        </ul>
    </div>

    <!-- 엑셀 export -->
    <div id="btn" class="btn_right_top2 margin-t10px">
        <ul><li><a href="javaScript:openExcelReport();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
    </div>


    <!-- extjs grid part add -->
    <!-- extjs grid part add -->
    <div class="gadget_body2">
        <div id="commLogDiv" class="margin-t10px" style="width: 100%;" ></div>
    </div>

</body>
