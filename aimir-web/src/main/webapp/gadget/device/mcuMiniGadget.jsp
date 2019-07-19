<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/gadget/system/preLoading.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>MCU MiniGadget</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
<!-- 스타일 추가 extjs css -->
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
    /* Ext-Js Grid Header style 정의. */
    .x-grid3-hd-inner {
        text-align: center;
        font-weight: bold;
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
    //var miniTab    = "ml";
    var miniTab = "mc";

    var fcChartDataXml;
    var fcChart;

    //공급사ID
    var supplierId = "${supplierId}";
    //로그인한 사용자정보를 조회한다.

    // row size per page
    var rowSize = 100;

    $.ajaxSetup({
        async: false
    });
    /**
     * 유저 세션 정보 가져오기
     */
    /* $.getJSON('${ctx}/common/getUserInfo.do',
        function(json) {
            if (json.supplierId != "") {
                supplierId = json.supplierId;
            }
        }
    ); */

    var chromeColAdd = 0;
    // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
    Ext.onReady(function() {
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
    });

    //mcuMiniChartDiv
    //################################
    //#######mcuMiniChart Start
    //################################

    //mcuMiniChartGrid propeties
    var mcuMiniChartGridInstanceOn = false;
    var mcuMiniChartGrid;
    var mcuMiniChartColModel;
    var mcuMiniChartGridModel;
    var mcuMiniChartCheckSelModel;
    var mcuMiniChartGridPanel;
    var condArray = new Array();

    //Grid ColumnModel properties
    var columnModelHeader = new Array();
    var mcuChartType = "";
    var fmtmessagecommalert = new Array();

    //change tab 호출시 event
    function changeData(selMiniTab) {
        miniTab = selMiniTab;
        if(selMiniTab == "mc"){ 
        	chartType = selMiniTab;
        	// 차트 호출..
            updateFChart();
        } else if(selMiniTab == "cm"){
        	chartType = selMiniTab;
        	// 차트 호출..
            updateFChart();
            chartType = selMiniTab;

            //Grid 컬럼 header value calling from s.s
            getGridColumnModel();

            //리싸이즈시 패널 인스턴스 kill & reload
            mcuMiniChartGridPanel.destroy();

            mcuMiniChartGridInstanceOn = false;

            //그리드 chart re -call
            getMCUMiniChartGrid();
        }
            }

    $(document).ready(function() {
        $('#_mcuType').bind('click',function(event) {
        	$('#ea').show();
        	$('#fcChartDiv').show();
        	$('#mcuMiniChartGridDiv').hide();
        	changeData("mc");
        });

        $('#_commStatus').bind('click',function(event) {
        	$('#ea').hide();
        	$('#fcChartDiv').hide();
        	$('#mcuMiniChartGridDiv').show();
        	changeData("cm");
        });
        
        $('#ea').show();
    	$('#fcChartDiv').show();
        $("#mcuMini").tabs();
        //파이차트 호출.
        updateFChart();

        //Grid 컬럼 header value 호출
        getGridColumnModel();

        //그리드 chart calling
        getMCUMiniChartGrid();
    });

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
        //리싸이즈시 패널 인스턴스 kill & reload
        mcuMiniChartGridPanel.destroy();

        //dataGapsMaxChartGridPanel;
        mcuMiniChartGridInstanceOn = false;

        //Grid 컬럼 header value 호출
        getGridColumnModel();

        //그리드 chart re -call
        getMCUMiniChartGrid();
    });

    var columnModelHeaderSize = 0;

    //Grid  ColumnModel fetch and setting  from Server-side
    function getGridColumnModel() {
        condArray= getCondition();

        fmtmessagecommalert = getFmtMessageCommAlert();

        $.ajax({
            type : "POST",
            data : {
                mcuChart  : condArray[0],
                supplierId  : condArray[1],
                message :fmtmessagecommalert
            },
            dataType : "json",
            url : "${ctx}/gadget/device/getMCUMiniChart.do",
            success : function(data, status) {
                //해더값 arr리스트.
                var chartSeriesList = data.chartSeries;

                //mcuChartType
                mcuChartType = data.mcuChartType;

                columnModelHeaderSize = chartSeriesList.length;

                $.each(chartSeriesList, function(i) {
                    columnModelHeader[i] = chartSeriesList[i].displayName;

                    switch (columnModelHeader[i]) {
                        case "fmtMessage00":
                            columnModelHeader[i] = "<fmt:message key="aimir.commstateGreen"/>";
                            break;
                        case "fmtMessage24":
                            columnModelHeader[i] = "<fmt:message key="aimir.24over"/>";
                            break;
                        case "fmtMessage48":
                            columnModelHeader[i] = "<fmt:message key="aimir.48over"/>";
                            break;
                        case "fmtMessage99":
                            columnModelHeader[i] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";
                            break;
                        case "CommError":
                            columnModelHeader[i] = "<fmt:message key="aimir.commError"/>";
                            break;
                        case "PowerDown":
                            columnModelHeader[i] = "<fmt:message key="aimir.powerDown"/>";
                            break;
                        case "SecurityError":
                            columnModelHeader[i] = "<fmt:message key="aimir.securityError"/>";
                            break;
                    }
                });
            },
            error : function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
            }
        });
    }

    //FETCH GRID CHART FROM S.S
    function getMCUMiniChartGrid() {
        //setting grid panel width
        var width = $("#mcuMiniChartGridDiv").width();

        condArray = getCondition();

        var fmtmessagecommalert = new Array();
        fmtmessagecommalert = getFmtMessageCommAlert();
       
        var ct="";
        if(condArray[0]=="mc") ct = "bar";
        else ct = "grid";
        
        //### mcuMiniChartGrid Store fetch
        var mcuMiniChartGridStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: rowSize}},
            url : "${ctx}/gadget/device/getMCUMiniChart.do",
            baseParams : {
                mcuChart : condArray[0],
                supplierId : condArray[1],
                message : fmtmessagecommalert,
                chartType : ct
            },
            //Total Cnt
            totalProperty : "totalCnt",
            root : 'chartData',
            fields : [
                      "xCode"
                     , "xTag"
                     , "value0"
                     , "value1"
                     , "value2"
                     , "value3"
                     , "value4"
                     , "value5"
                     , "value6"
                     ],
            sortInfo: {
                         field: 'xCode',
                         direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
            },
            listeners : {
               beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                }
            }
        });

        var fmtMessage = new Array();
        var modeldataIndex;

        if (mcuChartType == 'cm') {
            fmtMessage = getFmtMessage2();
            modeldataIndex = "xTag";
        } else {
            fmtMessage = getFmtMessage();
            modeldataIndex = "xCode";
        }

        var tagtooltip;
         if (miniTab == 'mc' || miniTab == "" || miniTab == "ml") {
              tagtooltip = getFmtMessageLongCommAlert();
         }else if (miniTab == 'cl' || miniTab == 'cm') {
              tagtooltip = columnModelHeader;
         }

        // dynamic header 생성
        var columns = [];
        columns.push({header: fmtMessage[0], dataIndex: modeldataIndex, menuDisabled: true, width: (width/(columnModelHeaderSize+1))-chromeColAdd});

        for (var i = 0; i < columnModelHeaderSize; i++) {
            columns.push({header: columnModelHeader[i], tooltip:tagtooltip[i], dataIndex: 'value' + i, menuDisabled: true, width: (width/(columnModelHeaderSize+1))-chromeColAdd, align:'right'});
        }

        mcuMiniChartGridModel = new Ext.grid.ColumnModel(columns);

        if (mcuMiniChartGridInstanceOn == false) {
            //Grid panel instance create
            mcuMiniChartGridPanel = new Ext.grid.GridPanel({
                store : mcuMiniChartGridStore,
                colModel : mcuMiniChartGridModel,
                autoScroll : false,
                scroll : false,
                width : width,
                height : 250,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'mcuMiniChartGridDiv',
                viewConfig : {
                    forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                }
            });
            mcuMiniChartGridInstanceOn = true;
        } else {
            mcuMiniChartGridPanel.setWidth(width);
            mcuMiniChartGridPanel.reconfigure(mcuMiniChartGridStore, mcuMiniChartGridModel);
        }

        hide();
    };//func mcuMiniChartGridList End

    function getFmtMessage() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.type2"/>";

        return fmtMessage;
    }

    function getFmtMessage1() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.location"/>";

        return fmtMessage;
    }
    function getFmtMessage2() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.status"/>";

        return fmtMessage;
    }

    function getFmtMessageCommAlert() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.24within"/>";
        fmtMessage[1] = "<fmt:message key="aimir.24over"/>";
        fmtMessage[2] = "<fmt:message key="aimir.48over"/>";
        fmtMessage[3] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";     // 최종검침시간이 없는 경우

        return fmtMessage;
    }

    function getFmtMessageLongCommAlert() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.commstateGreen"/>";
        fmtMessage[1] = "<fmt:message key="aimir.commstateYellow"/>";
        fmtMessage[2] = "<fmt:message key="aimir.commstateRed"/>";
        fmtMessage[3] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";     // 최종검침시간이 없는 경우

        return fmtMessage;
    }

    function getCondition() {
        var condArray = new Array();

        condArray[0] = miniTab;
        condArray[1] = supplierId;

        return condArray;
    }
    
    // 3.3 >Bar차트 렌더링 이벤트 (fusion chart 3.9버전)
    function updateFChart(){    	
    	emergePre();
    	$.getJSON('${ctx}/gadget/device/getMCUMiniChart.do',
                {mcuChart : miniTab,
                  supplierId : supplierId,
                  chartType : "bar"
                 },
                 function(json) {
                     var chartSeries = json.chartSeries;
                     var chartData = json.chartData;
                     var maxValue = json.totalCnt+1;
                     fcChartDataXml = '{ "chart" : { '         				
         				+ '"yAxisMinValue": "0", '
         				+ '"yAxisMaxValue": "'+maxValue+'", '
         				+ '"yFormatNumber": "0",'
         				+ '"rotatevalues": "0",'
         				+ '"showBorder" : "0", '
         				+ '"formatNumberScale" : "0", '
         				+ '"labelDisplay" : "wrap", '         				
         				+ '"adjustDiv" : "0",'
         				+ '"numDivLines" : "'+json.totalCnt+'",'
         				+ '"plotFillAlpha" : "80",'
         				+ '"showPlotBorder": "1",'
         				+ '"plotBorderColor" : "#ffffff",'
         				+ '"valueBgColor " : "#f3f3f3",'
         				+ '"valueBorderColor" : "#f3f3f3",'
         				+ '"valueFontColor": "#000000",'
         				+ '"valueFontSize": "12",'
         				+ '"canvasBgColor": "#f3f3f3",'
         				+ '"theme" : "fint" '
         				+ '},';
         			 
         				// "CATEGORIES" 
         				var categories = '"categories":[{"category":[';           				         				
         				for(index in chartData){
         					if (chartData[index].xTag != "") {
         						if(isNaN(index)) continue;
         						var dTag = chartData[index].xTag;         						
         						         						
	                            if(index!=0 ){                            	
	                            	 // 인덱스가 0이 아닌경우 ,를 앞에 붙여준다
	                            	categories += ',{"label": "'+dTag+'"}';
	                            }else{
	                            	categories += '{"label": "'+dTag+'"}';	
	                            }	                            	                            
	                         
         					}	                    	 
         				}
         				categories += ']}],';
         				// "DATASET, SERIES" ~~나중에 메시지 프로퍼티 처리함         				
         				var dataset = '"dataset":[';
         				var series0 = '{"seriesname": "A24H", "color":"#4d4dff", "data":[';
         				var series1 = '{"seriesname": "N24H", "color":"#33cc33", "data":[';
         				var series2 = '{"seriesname": "N48H", "color":"#ffff00", "data":[';
         				var series3 = '{"seriesname": "Unknown", "color":"#993333", "data":[';
         				var series4 = '{"seriesname": "CommError", "color":"#ff9900", "data":[';
         				var series5 = '{"seriesname": "PowerDown", "color":"#999966", "data":[';
         				var series6 = '{"seriesname": "SecurityError", "color":"#ff4d4d", "data":[';
         				for(index in chartData){      	
         					if(index==0){
         						series0 += '{"value":"' + chartData[index].value0 + '"}';
             					series1 += '{"value":"' + chartData[index].value1 + '"}';
             					series2 += '{"value":"' + chartData[index].value2 + '"}';
             					series3 += '{"value":"' + chartData[index].value3 + '"}';
             					series4 += '{"value":"' + chartData[index].value4 + '"}';
             					series5 += '{"value":"' + chartData[index].value5 + '"}';
             					series6 += '{"value":"' + chartData[index].value6 + '"}';
         					}else{
         						if(isNaN(index)) continue;
	         					series0 += ',{"value":"' + chartData[index].value0 + '"}';
	         					series1 += ',{"value":"' + chartData[index].value1 + '"}';
	         					series2 += ',{"value":"' + chartData[index].value2 + '"}';
	         					series3 += ',{"value":"' + chartData[index].value3 + '"}';
	         					series4 += ',{"value":"' + chartData[index].value4 + '"}';
	         					series5 += ',{"value":"' + chartData[index].value5 + '"}';
	         					series6 += ',{"value":"' + chartData[index].value6 + '"}';
         					}
         				}
         				dataset += series0 +']},'+ series1 +']},'+ series2 +']},';
         				dataset += series3 +']},'+ series4 +']},'+ series5 +']},' + series6 +']}' + ']';
         				
         				fcChartDataXml = fcChartDataXml + categories + dataset + '}';
         				
         				fcChartRender();

                        hide();
         				
                     
                     } //function(json)
                 );
    }
    
    

    //Bar차트 랜더링 이벤
    /* function updateFChart2() {
        emergePre();
        $.getJSON('${ctx}/gadget/device/getMCUMiniChart.do'
                ,{mcuChart : miniTab,
                  supplierId : supplierId,
                  chartType : "bar"
                 }
                ,function(json) {
                     var chartSeries = json.chartSeries;
                     var chartData = json.chartData;
                     fcChartDataXml = "<chart "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='5' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "legendNumColumns='2' "
                        + "showLegend='1' "
                        + "yaxismaxvalue='5'"
                        + "numberSuffix='  ' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_StColumn3D_nobg
                        + ">";
                     var categories = "<categories>";
                     var datasets = new Array(chartSeries.length);

                     var size = 0;

                     for (index in chartSeries) {
                         if (chartSeries[index].displayName != "") {
                             switch (chartSeries[index].displayName) {
                                 case "fmtMessage00":
                                     datasets[index] = "<dataset seriesName='<fmt:message key='aimir.commstateGreen'/>'>";
                                     break;
                                 case "fmtMessage24":
                                     datasets[index] = "<dataset seriesName='<fmt:message key='aimir.commstateYellow'/>'>";
                                     break;
                                 case "fmtMessage48":
                                     datasets[index] = "<dataset seriesName='<fmt:message key='aimir.commstateRed'/>'>";
                                     break;
                                 case "fmtMessage99":
                                     datasets[index] = "<dataset seriesName='<fmt:message key='aimir.bems.facilityMgmt.unknown'/>'>";
                                     break;
                                 default:
                                     datasets[index] = "<dataset seriesName='"+chartSeries[index].displayName+"'>";
                                     break;
                             }
                             size++;
                         }
                     }

                     for (var j = 0; j < chartData.length; j++) {
                         switch (chartData[j].xTag) {
                             case "fmtMessage00":
                                 categories += "<category label='<fmt:message key='aimir.commstateGreen'/>' />";
                                 break;
                             case "fmtMessage24":
                                 categories += "<category label='<fmt:message key='aimir.commstateYellow'/>' />";
                                 break;
                             case "fmtMessage48":
                                 categories += "<category label='<fmt:message key='aimir.commstateRed'/>' />";
                                 break;
                             case "fmtMessage99":
                                 categories += "<category label='<fmt:message key='aimir.bems.facilityMgmt.unknown'/>' />";
                                 break;
                             default:
                                 categories += "<category label='"+chartData[j].xTag+"' />";
                                 break;
                         }

                         for (var i = 0; i < size; i++) {
                        	 var chartDataj = eval("chartData[j].value" + i);
                             if(chartDataj != null && chartDataj != "") {
                                chartDataj = chartDataj.replace(",","");
                                if (chartDataj > 0)
                                    datasets[i] += "<set value='"+chartDataj+"' />";
                                else
                                    datasets[i] += "<set value='' />";
                            }
                             
                         }
                     }

                     categories += "</categories>";

                     for (index in chartSeries) {
                         if (chartSeries[index].displayName != "") {
                            datasets[index] += "</dataset>";
                         }
                     }

                     fcChartDataXml += categories;

                     for (var j = 0; j < chartSeries.length; j++) {
                         if (chartSeries[j].displayName != "") {
                             fcChartDataXml += datasets[j];
                         }
                     }
                     fcChartDataXml += "</chart>";
                     fcChartRender();

                     hide();
                }
        );
    } */

    window.onresize = fcChartRender;
    function fcChartRender() {
    	var chartWidth = $('#fcChartDiv').width();
    	if ($('#fcChartDiv').is(':visible')) {
    		fcChart = new FusionCharts({
    			type: 'mscolumn2d',
    			renderAt : 'fcChartDiv',
    			width : chartWidth,
    			height : '250',
    			dataFormat : 'json',
    			dataSource : fcChartDataXml
    		});
    		fcChart.render();
    	}
    }
   /*  function fcChartRender2() {
        if ($('#fcChartDiv').is(':visible')) {
            fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), "150", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv");
        }
    } */

    </script>
</head>
<body>

    <div id="mcuMini">
        <ul>
            <li><a href="#mcuType"  id="_mcuType" ><fmt:message key="aimir.chartData"/></a></li>
            <li><a href="#commStatus" id="_commStatus"><fmt:message key="aimir.gridData"/></a></li>
        </ul>

        <!--  탭이하 내용(S)  -->
        <div id="mcuType" class="mcuMiniLayer"></div>
        <div id="commStatus" class="mcuMiniLayer"></div>
        <!--  탭이하 내용(E)  -->
    </div>


    <div class="div-mcu gadget_body2">
        <div id = "ea"><b><fmt:message key='aimir.EA'/></b></div>
        <div id="fcChartDiv" style="height:150px; display:none; vertical-align:middle;">
            The chart will appear within this DIV. This text will be replaced by the chart.
        </div>
        <!-- extjs div grid div -->
        <div id="mcuMiniChartGridDiv" style="display:none">
        </div>
    </div>

</body>
</html>
