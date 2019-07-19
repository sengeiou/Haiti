<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>

    <title>Data Gaps(${meterTypeCode2})</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css"/>
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold !important;
        }
    </style>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    var fcChartDataXml;
    var fcChart;

    //플렉스객체
    var flex;

    //공급사ID
    var supplierId="${sesSupplierId}";

    //window width
    var browserWidth= "";
    var rowSize = 12;

    var dataGapsMaxChartGridPanel;

    var lpInterval;
    var mdsId;
    var meterId;

    //로그인한 사용자정보를 조회한다.
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;
            }
    );

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

    Ext.onReady(function() {
        Ext.QuickTips.init();
        extColumnResize();
        $(function() {
            $('#btnSearch').bind('click',function(event) {
                sendRequest($('#searchDateType').val());
            } );
        });

        $('#hourlySearch').parent().hide();
        $('#dailySearch').parent().hide();
        $('#periodSearch').parent().hide();
        $('#weeklySearch').parent().hide();
        $('#monthlySearch').parent().hide();
        $('#monthlyPeriodSearch').parent().hide();
        $('#weekDailySearch').parent().hide();
        $('#seasonalSearch').parent().hide();
        $('#yearlySearch').parent().hide();

        $('#deviceType').selectbox();

        browserWidth= $(window).width();

        //파이차트 호출
        getPieChartOnLoad();

        //그리드 차트 호출..
        getdataGapsMaxChartGrid();
    });

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
        browserWidth = $(window).width();   // returns width of browser viewport

        //리싸이즈시 패널 인스턴스 kill & reload
        dataGapsMaxChartGridPanel.destroy();

        //dataGapsMaxChartGridPanel;
        dataGapsMaxChartGridInstanceOn = false;

        getdataGapsMaxChartGrid();
    });

    function getPieChartOnLoad() {
        var condArray = getParams();

        $.ajax({
            type:"POST",
            data:{
                 'searchStartDate':condArray[0]
                ,'searchEndDate':condArray[1]
                ,'searchDateType':condArray[2]
                ,'meterType':condArray[3]
                ,'supplierId' :condArray[4]
                ,'mdsId':condArray[5]
                ,'deviceType':condArray[6]
                ,'deviceId':condArray[7]
                ,'pageSize':rowSize
                ,'page':"1"
            },
            dataType:"json",
            url:"${ctx}/gadget/mvm/getLpMissingMeters2.do",
            success:function(data, status) {
                var arrayList = data.lpmissingmeterslist;
                var dataMap   = arrayList[0];
                lpInterval = dataMap.lpInterval;
                meterId = dataMap.meterId;
                mdsId = dataMap.mdsId;

                //graph draw
                updateDetailChart( meterId, lpInterval, '','');

                hide();
            },
            error:function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"getPieChart ajax comm failed");
            }
        });
    }

    //#######dataGapsMaxChart Start

    //dataGapsMaxChartGrid propeties
    var dataGapsMaxChartGridInstanceOn = false;
    var dataGapsMaxChartGrid;
    var dataGapsMaxChartColModel;
    var dataGapsMaxChartCheckSelModel;

    function getdataGapsMaxChartGrid() {
        //setting grid panel width
        var width = $("#dataGapsMaxChartGridDiv").width();
        //row Count per page
        var rowSize = 12;
        var condArray = getParams();

        //### dataGapsMaxChartGrid Store fetch
        var dataGapsMaxChartGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize }},
            url: "${ctx}/gadget/mvm/getLpMissingMeters2.do",
            baseParams: {
                 searchStartDate:condArray[0]
                ,searchEndDate:condArray[1]
                ,searchDateType:condArray[2]
                ,meterType:condArray[3]
                ,supplierId :condArray[4]
                ,mdsId:condArray[5]
                ,deviceType:condArray[6]
                ,deviceId:condArray[7]
                ,pageSize:rowSize
            },
            totalProperty: "totalCnt",
            root:'lpmissingmeterslist',
            fields: [
                      "idx"
                     , "customerName"
                     , "deviceNo"
                     , "mdsId"
                     , "missingCnt"
                     , "lastReadDate"
                     ,"lpInterval"
                     ,"meterId"
                     ],
            listeners: {
                beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                },
                afterload : function(store) {
                    lpInterval = store.reader.jsonData.lpmissingmeterslist[0].lpInterval;
                    mdsId = store.reader.jsonData.lpmissingmeterslist[0].mdsId;
                    meterId = store.reader.jsonData.lpmissingmeterslist[0].meterId;
                }
            }
        });//Store End

        var fmtMessage = getFmtMessage();
        var colWidth = (width-40)/5 - chromeColAdd;

        // dataGapsMaxChartGrid Model DEfine
        dataGapsMaxChartGridModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0], dataIndex: 'idx', align: 'center', width: 40 - chromeColAdd}
               ,{header: fmtMessage[1], dataIndex: 'customerName'}
               ,{header: fmtMessage[2], dataIndex: 'deviceNo', align: 'center'}
               ,{header: fmtMessage[3], dataIndex: 'mdsId'}
               ,{header: fmtMessage[4], dataIndex: 'missingCnt', align: 'right'}
               ,{header: fmtMessage[5], dataIndex: 'lastReadDate', width: colWidth - 4}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: colWidth
            }
        });

        if (dataGapsMaxChartGridInstanceOn == false) {
            //Grid panel instance create
            dataGapsMaxChartGridPanel = new Ext.grid.GridPanel({
                store: dataGapsMaxChartGridStore,
                colModel : dataGapsMaxChartGridModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true
                }),
                listeners: {
                    rowclick:rowClickEvent
                },
                autoScroll:false,
                width:  width,
                height: 333,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'dataGapsMaxChartGridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: rowSize,
                    store: dataGapsMaxChartGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            dataGapsMaxChartGridInstanceOn = true;
        } else {
            dataGapsMaxChartGridPanel.setWidth( width);
            var bottomToolbar = dataGapsMaxChartGridPanel.getBottomToolbar();
            dataGapsMaxChartGridPanel.reconfigure(dataGapsMaxChartGridStore, dataGapsMaxChartGridModel);
            bottomToolbar.bindStore(dataGapsMaxChartGridStore);
        }

        hide();
    };//func dataGapsMaxChartGridList End

    //row Click Event 리스너
    function rowClickEvent(grid, rowIndex, e) {
        var s = grid.getSelectionModel();
        var row = s.getSelected();

        //graph draw
        updateDetailChart(row.get('meterId'), row.get('lpInterval'), row.get('mdsId'), '');

        hide();
    }

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시 호출하게 된다.
     */
    function send() {
        getdataGapsMaxChartGrid();
        //차트 업데이트
        updateDetailChart(meterId, lpInterval, $('#mdsId').val(),'');
    }

    /**
     * 메세지를 조회하는 함수
     */
    function getFmtMessage() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";//번호";
        fmtMessage[1] = "<fmt:message key="aimir.customername"/>";//"고객명";
        fmtMessage[2] = "<fmt:message key="aimir.equipment"/>"+" <fmt:message key="aimir.number"/>";//"장비번호";
        fmtMessage[3] = "<fmt:message key="aimir.meterid"/>";//"미터번호";
        fmtMessage[4] = "<fmt:message key="aimir.numberofmissing"/>";//"누락건수";
        fmtMessage[5] = "<fmt:message key="aimir.lastmeteringtime"/>";//"마지막검침시각";
        fmtMessage[6] = "<fmt:message key="aimir.remetering"/>";//"재검침";
        fmtMessage[7] = "<fmt:message key="aimir.select"/>";//"선택";
        fmtMessage[8] = "<fmt:message key="aimir.firmware.msg09"/>"; // excel export 조회데이터 없음.
        return fmtMessage;
    }

    function getFmtMessage1() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";//번호";
        fmtMessage[1] = "<fmt:message key="aimir.customername"/>";//"고객명";
        fmtMessage[2] = "<fmt:message key="aimir.equipment"/>"+"<fmt:message key="aimir.number"/>";//"장비번호";
        fmtMessage[3] = "<fmt:message key="aimir.meterid"/>";//"미터번호";
        fmtMessage[4] = "<fmt:message key="aimir.numberofmissing"/>";//"누락건수";
        fmtMessage[5] = "<fmt:message key="aimir.lastmeteringtime"/>";//"마지막검침시각";
        fmtMessage[6] ="<fmt:message key='aimir.excel.datagaps'/>";

        return fmtMessage;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수
     */
    function getParams() {
        var searchStartDate = $('#searchStartDate').val();
        var searchEndDate = $('#searchEndDate').val();

        //처음 로드시에 날짜가 널일 경우 오늘 날짜로 설정.
        if (searchStartDate == "" && searchEndDate == "") {
            searchStartDate = getToday();
            searchEndDate = getToday();
            searchStartHour = "00";
            searchEndHour = "23";
        }

        var condArray = new Array();
        condArray[0] = searchStartDate;
        condArray[1] = searchEndDate;
        condArray[2] = $('#searchDateType').val();
        condArray[3] = ${MeterType};
        condArray[4] = supplierId;
        condArray[5] = $('#mdsId').val();
        condArray[6] = $('#deviceType').val();
        condArray[7] = $('#deviceId').val();

        return condArray;
    }

    function cmdOnDemandRecollect() {
        //jhkim
         $.getJSON('${ctx}/gadget/device/command/cmdOnDemandRecollect.do',
                 {searchStartDate:$('#searchStartDate').val() ,
                searchEndDate:$('#searchEndDate').val() ,
                searchDateType:$('#searchDateType').val() ,
                meterType:${MeterType},
                supplierId:supplierId });
         Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.remetering"/>');
    }

    //오늘 날짜를 구한다.,
    function getToday() {

        var currentTime = new Date();
        var month = (currentTime.getMonth() + 1);
        var day = currentTime.getDate();//-8
        var year = currentTime.getFullYear();

        if (day.toString().length == 1) {
            day = "0" + day.toString();
        }

        if (month.toString().length == 1) {
            month = "0" + month.toString();
        }

        var today = year.toString() + month.toString() + day.toString();

        return today;
    }

    function updateDetailChart(_meterId, _lpInterval, _mdsId, _yyyymmdd) {
        emergePre();

        if (_meterId != null && _lpInterval != null && _mdsId != null) {
            $.getJSON('${ctx}/gadget/mvm/getLpMissingCount.do'
                    //,{mdsId:$('#mdsId').val() ,
                    ,{mdsId:_mdsId,
                      meterId:_meterId ,
                      meterType:${MeterType},
                      deviceId:$('#deviceId').val() ,
                      deviceType:$('#deviceType').val() ,
                      lpInterval:_lpInterval ,
                      searchStartDate:$('#searchStartDate').val() ,
                      searchEndDate:$('#searchEndDate').val() ,
                      searchDateType:$('#searchDateType').val() ,
                      yyyymmdd:_yyyymmdd ,
                      supplierId:supplierId }
                    ,function(json) {
                         var list = json.result;
                         fcChartDataXml = "<chart "
                                        + "yAxisName='<fmt:message key="aimir.numberofmissing"/>' "
                                        + "chartLeftMargin='0' "
                                        + "chartRightMargin='0' "
                                        + "chartTopMargin='10' "
                                        + "chartBottomMargin='0' "
                                        + "showValues='0' "
                                        + "showLabels='1' "
                                        + "showLegend='0' "
                                        + "numberSuffix='  ' "
                                        + "labelDisplay = 'AUTO' "
                                        + fChartStyle_Common
                                        + fChartStyle_Font
                                        + fChartStyle_MSColumn3D_nobg
                                        + ">";
                         var categories = "<categories>";
                         var dataset = "<dataset seriesName='<fmt:message key="aimir.numberofmissing"/>'>";
                         if (list.length > 0) {
                             for (var i = 0; i < list.length; i++) {
                                 categories += "<category label='"+list[i].xField+"' />";
                                 if (_yyyymmdd == '') {
                                     dataset += "<set value='"+list[i].missingCount+"' link='JavaScript:updateDetailChart(" + _meterId +", "+ _lpInterval +", \""+ _mdsId +"\", "+ list[i].yyyymmdd +");' color='"+${chartColor}+"' />";
                                 } else {
                                     dataset += "<set value='"+list[i].missingCount+"' color='"+${chartColor}+"' />";
                                 }
                             }
                         } else {
                             categories += "<category label=' ' />";
                             dataset += "<set value='0' color='"+${chartColor}+"' />";
                         }
                         categories += "</categories>";
                         dataset += "</dataset>";

                         fcChartDataXml += categories + dataset + "</chart>";
                         fcChartRender();
                         hide();
                     }
            );
        } else {
            // 빈 chart
            fcChartDataXml = "<chart "
                           + "yAxisName='<fmt:message key="aimir.numberofmissing"/>' "
                           + "chartLeftMargin='0' "
                           + "chartRightMargin='0' "
                           + "chartTopMargin='15' "
                           + "chartBottomMargin='0' "
                           + "showValues='0' "
                           + "showLabels='1' "
                           + "showLegend='0' "
                           + "numberSuffix='  ' "
                           + "labelDisplay = 'AUTO' "
                           + fChartStyle_Common
                           + fChartStyle_Font
                           + fChartStyle_MSColumn3D_nobg
                           + ">";
            var categories = "<categories>";
            var dataset = "<dataset seriesName='<fmt:message key="aimir.numberofmissing"/>'>";
            categories += "<category label=' ' /></categories>";
            dataset += "<set value='0' color='"+${chartColor}+"' /></dataset>";

            fcChartDataXml += categories + dataset + "</chart>";
            fcChartRender();
            hide();
        }
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        if ($('#fcChartDiv').is(':visible')) {
        	fcChart = new FusionCharts({
        		id: 'myChartId',
    			type: 'MSColumn3D',
    			renderAt : 'fcChartDiv',
    			width : $('#fcChartDiv').width(),
    			height : '300',
    			dataSource : fcChartDataXml
    		}).render();
        	
/*         	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "myChartId", $('#fcChartDiv').width(), "300", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv"); */
        }
    }

    //report window(Excel)
    var winObj;
    function openExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();

        obj.condition   = getParams();
        obj.fmtMessage  = getFmtMessage1();

        if(winObj)
        	winObj.close();
        winObj = window.open("${ctx}/gadget/mvm/dataGapsExcelDownloadPopup.do", "dataGapsExcel", opts);
        winObj.opener.obj = obj;
    }

/*]]>*/
    </script>
</head>
<body>


<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">

    <div class="dayoptions">
    <%@ include file="../commonDateTab.jsp" %>
    </div>
    <div class="dashedline"><ul><li></li></ul></div>


    <!--검색조건-->
    <div class="searchoption-container">
        <table class="searchoption wfree">
            <tr>
                <td class="gray11pt withinput"><fmt:message key="aimir.meterid"/></td>
                <td><input id="mdsId" type="text" ></td>
                <td class="space20"></td>
                <td class="gray11pt withinput"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.type2"/></td>
                <td><select id="deviceType" style="width:120px;">
                        <option value='0'><fmt:message key="aimir.mcu"/></option>
                        <option value='1'><fmt:message key="aimir.modem"/></option>
                    </select></td>
                <td class="space20"></td>
                <td class="gray11pt withinput"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.number"/></td>
                <td><input id="deviceId" type="text" ></td>
                <td>
                   <div id="btn">
                       <ul><li><a href="javascript:;" id="btnSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                   </div>
                </td>
                <td>
                    <div id="btn">
                       <ul><li><a href="javascript:openExcelReport();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
                   </div>
                </td>
            </tr>
        </table>
    </div>
    <!--검색조건 끝-->

</div>
<!-- search-background DIV (E) -->

<div class="gadget_body">
    <div id="fcChartDiv" class="margin10px" style="height:300px">
        <!-- The chart will appear within this DIV. This text will be replaced by the chart. -->
    </div>
    <div id="dataGapsMaxChartGridDiv"></div>
</div>

</body>
</html>