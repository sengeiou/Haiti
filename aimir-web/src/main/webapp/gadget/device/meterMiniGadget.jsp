<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>Meter MiniGadget</title>
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

    //var flex;
    var miniTab = "mc";
    var supplierId = "${supplierId}";
    supplierId = supplierId == "" ? -1 : supplierId;
    var permitLocationId = "${permitLocationId}";

    var fcChartDataXml;
    var fcChart;

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

    $(function() {
        $(function() { $('#_meterType').bind('click',function(event) {
        	$('#fcChartDiv').show();
        	$('#ea').show();
            $('#modemMiniChartGridDiv').hide();
        	changeData("mc"); 
        	}); 
        });
        
        $(function() { $('#_commStatus').bind('click',function(event) {
        	$('#meterMiniChartDivGridDiv').show();
            $('#fcChartDiv').hide();
            $('#ea').hide();
        	changeData("cm"); 
        	}); 
        });

        $("#meterMini").tabs();
    });

    function getFmtMessage() {
        var fmtMessage = new Array();;

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
        fmtMessage[4] = "<fmt:message key="aimir.commError"/>";
        fmtMessage[5] = "<fmt:message key="aimir.securityError"/>";
		fmtMessage[6] = "<fmt:message key="aimir.powerDown"/>";
		
        return fmtMessage;
    }

    function getFmtMessageLongCommAlert() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.commstateGreen"/>";
        fmtMessage[1] = "<fmt:message key="aimir.commstateYellow"/>";
        fmtMessage[2] = "<fmt:message key="aimir.commstateRed"/>";
        fmtMessage[3] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";     // 최종검침시간이 없는 경우
        fmtMessage[4] = "<fmt:message key="aimir.commError"/>";
        fmtMessage[5] = "<fmt:message key="aimir.securityError"/>";
        fmtMessage[6] = "<fmt:message key="aimir.powerDown"/>";

        return fmtMessage;
    }

    function getCondition() {
        var condArray = new Array();

        condArray[0] = miniTab;
        condArray[1] = supplierId;

        return condArray;
    }

    //grid 관련 프로퍼티s
    var browserWidth = "";
    var rowSize = 10;
    var chartType = "";

    //Grid model 컬럼  header array
    var columnModelHeader = new Array();
    var fmtMessage = new Array();

    //Alert msg array
    var fmtmessagecommalert = new Array();
    var fmtMessage0 = "";

    //컬럼 길이..
    var colHeaderLength = "";

    $(document).ready(function() {
        updateFChart();
        browserWidth= $(window).width();
        //Grid model fetch from S.S.
        getGridColumnModel();
        //grid show to dom div
        getmeterMiniChartDivGrid();
    });

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
        browserWidth= $(window).width();   // returns width of browser viewport

        //리싸이즈시 패널 인스턴스 kill & reload
        meterMiniChartDivGridPanel.destroy();

        //dataGapsMaxChartGridPanel;
        meterMiniChartDivGridInstanceOn = false;

        //Grid 컬럼 header value 호출
        getGridColumnModel();

        //그리드 chart re -call
        getmeterMiniChartDivGrid();
    });

    //그리드 컬럼 모델 fetch from S.S
    function getGridColumnModel() {
        fmtmessagecommalert = getFmtMessageCommAlert();
        condArray= getCondition();

        $.ajax({
            type:"POST",
            data:{
                meterChart:condArray[0],
                supplierId:supplierId,
                gridType :"extjs",
                permitLocationId : permitLocationId
            },
            dataType:"json",
            url: "${ctx}/gadget/device/getMeterMiniChart.do",
            success: function(data, status) {
            	//해더값 arr리스트.
                var chartSeriesList = data.chartSeries;
                columnModelHeader = new Array();

                $.each(chartSeriesList, function(i) {
                    switch(chartSeriesList[i].displayName) {
                        case "fmtMessage00":
                            columnModelHeader.push(fmtmessagecommalert[0]);
                            break;
                        case "fmtMessage24":
                            columnModelHeader.push(fmtmessagecommalert[1]);
                            break;
                        case "fmtMessage48":
                            columnModelHeader.push(fmtmessagecommalert[2]);
                            break;
                        case "fmtMessage99":
                            columnModelHeader.push(fmtmessagecommalert[3]);
                            break;
                        case "CommError":
                            columnModelHeader.push(fmtmessagecommalert[4]);
                            break;
                        case "SecurityError":
                            columnModelHeader.push(fmtmessagecommalert[5]);
                            break;
                        case "PowerDown":
                            columnModelHeader.push(fmtmessagecommalert[6]);
                            break;
                            
                        default:
                            columnModelHeader.push(chartSeriesList[i].displayName);
                            break;
                    }
                });
                colHeaderLength = columnModelHeader.length;

                if (miniTab == 'mc' || miniTab == "" || miniTab == "ml") {
                    fmtMessage0 = "<fmt:message key="aimir.type2"/>";
                } else if (miniTab == 'lm') {
                    fmtMessage0 = "<fmt:message key="aimir.location"/>";
                } else if (miniTab == 'cl' || miniTab == 'cm') {
                    fmtMessage0 = "<fmt:message key="aimir.status"/>";
                }
            },
            error:function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
            }
        });
    }

    // TabChange Event
    function changeData(selMiniTab) {
        miniTab = selMiniTab;
        updateFChart(selMiniTab);
        //Grid model fetch from S.S.
        getGridColumnModel();
        //grid chart show method calling
        getmeterMiniChartDivGrid();
    }

    //################################
    //#######meterMiniChartDiv Start
    //################################

    //meterMiniChartDivGrid propeties
    var meterMiniChartDivGridInstanceOn = false;
    var meterMiniChartDivGrid;
    var meterMiniChartDivColModel;
    var meterMiniChartDivCheckSelModel;
    var condArray = new Array();

    function getmeterMiniChartDivGrid() {
        //setting grid panel width
        var gridWidth = $("#meterMiniChartDivGridDiv").width();
        fmtmessagecommalert = getFmtMessageCommAlert();
        condArray= getCondition();

        //### meterMiniChartDivGrid Store fetch
        var meterMiniChartDivGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize }},
            url: "${ctx}/gadget/device/getMeterMiniChart.do",
            method : 'POST',
            baseParams: {
				meterChart:condArray[0],
                supplierId:supplierId,
                fmtmessagecommalert:fmtmessagecommalert,
                gridType :"extjs",
                permitLocationId : permitLocationId
            },
            totalProperty: "totalCnt",
            root:'chartData',
            fields: [
                      "xTag"
                     , "xCode"
                     , "value0"
                     , "value1"
                     , "value2"
                     , "value3"
                     , "value4"
                     , "value5"
                     ],
           sortInfo: {
                         field: 'xCode',
                         direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
            },
            /* 
            fields: ["xTag", "xCode"
                    ,"value0", "value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9"
                    ,"value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19"
                    ], 
            */
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
        });//Store End
        
        var tagtooltip;
         if (miniTab == 'mc' || miniTab == "" || miniTab == "ml") {
              tagtooltip = getFmtMessageLongCommAlert();
         } else if (miniTab == 'cl' || miniTab == 'cm') {
              tagtooltip = columnModelHeader;
         }
         
        // dynamic header 생성
        var columns = [];
        columns.push({header: fmtMessage0,  dataIndex: 'xTag', menuDisabled: true, width: (gridWidth/(colHeaderLength+1))-chromeColAdd});
       
        for (var i = 0 ; i < colHeaderLength ; i++) {
            columns.push({header: columnModelHeader[i], tooltip:tagtooltip[i], dataIndex: 'value' + i, menuDisabled: true, width: (gridWidth/(colHeaderLength+1))-chromeColAdd, align:'right'});
        }

        meterMiniChartDivGridModel = new Ext.grid.ColumnModel(columns);

        if (meterMiniChartDivGridInstanceOn == false) {

            //Grid panel instance create
            meterMiniChartDivGridPanel = new Ext.grid.GridPanel({
                store: meterMiniChartDivGridStore,
                colModel : meterMiniChartDivGridModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true
                }),
                autoScroll:false,
                scroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 250,
                // height: 96,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'meterMiniChartDivGridDiv',
                viewConfig: {
                    forceFit:true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
            });
            meterMiniChartDivGridInstanceOn = true;
        } else {
            meterMiniChartDivGridPanel.setWidth(gridWidth);
            meterMiniChartDivGridPanel.reconfigure(meterMiniChartDivGridStore, meterMiniChartDivGridModel);
        }

        hide();
    };//func meterMiniChartDivGridList End

    
 // 3.3 >Bar차트 렌더링 이벤트 (fusion chart 3.9버전)
    function updateFChart(){    	
    	emergePre();
    	$.getJSON('${ctx}/gadget/device/getMeterMiniChart.do',
                {meterChart : miniTab,
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
         				var series5 = '{"seriesname": "SecurityError", "color":"#ff3333", "data":[';
         				var series6 = '{"seriesname": "PowerDown", "color":"#999966", "data":[';
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
         				dataset += series3 +']},'+ series4 +']},'+ series6 +']},'+ series5 +']}'  + ']';
         				
         				fcChartDataXml = fcChartDataXml + categories + dataset + '}';
         				
         				fcChartRender();

                        hide();
         				
                     
                     } //function(json)
                 );
    }
    
    //바차트 show
    /* function updateFChart(selMiniTab) {
        emergePre();
        $.getJSON('${ctx}/gadget/device/getMeterMiniChart.do'
                ,{meterChart : miniTab,
                  supplierId : supplierId,
                  permitLocationId : permitLocationId}
                ,function(json) {
                     var chartSeries = json.chartSeries;
                     var chartData = json.chartData;
                     var fmtmessagelongcommalert = getFmtMessageLongCommAlert();
                     fcChartDataXml =  "<chart "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='5' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "legendNumColumns='2' "
                        + "showToolTip='1'"
                        + "yaxismaxvalue='5'"
                        + "labelDisplay = 'WRAP'"
                        + "numberSuffix='  ' "
                        + "labelDisplay ='vertical'"
                        //+ "numberPrefix ='수량'"
                        + fChartStyle_legendScroll
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg;
                     //if (selMiniTab == 'ml' || selMiniTab == 'cl' || selMiniTab == undefined) {
                     //    fcChartDataXml += "legendNumColumns='2'";
                     //}
                     fcChartDataXml += ">";

                     var categories = "<categories>";
                     var datasets = new Array(chartSeries.length);

                     var size = 0;

                     for (var j = 0; j < chartSeries.length; j++) {
                         if (chartSeries[j].displayName != "") {

                             switch(chartSeries[j].displayName) {
                                 case "fmtMessage00":
                                     datasets[j] = "<dataset seriesName='" + fmtmessagelongcommalert[0] + "'>";
                                     break;
                                 case "fmtMessage24":
                                     datasets[j] = "<dataset seriesName='" + fmtmessagelongcommalert[1] + "'>";
                                     break;
                                 case "fmtMessage48":
                                     datasets[j] = "<dataset seriesName='" + fmtmessagelongcommalert[2] + "'>";
                                     break;
                                 case "fmtMessage99":
                                     datasets[j] = "<dataset seriesName='" + fmtmessagelongcommalert[3] + "'>";
                                     break;
                                 default:
                                     datasets[j] = "<dataset seriesName='" + chartSeries[j].displayName + "'>";
                                     break;
                             }

                             size++;
                         }
                     }

                     for (var j = 0; j < chartData.length; j++) {
                         //if(chartData[j].xTag == "fmtMessage00") categories += "<category label='<fmt:message key='aimir.normal'/>' />";
                         //else if(chartData[j].xTag == "fmtMessage24") categories += "<category label='<fmt:message key='aimir.commstateYellow'/>' />";
                         //else if(chartData[j].xTag == "fmtMessage48") categories += "<category label='<fmt:message key='aimir.commstateRed'/>' />";
                         //else categories += "<category label='"+chartData[j].xTag+"' />";

                         switch(chartData[j].xTag) {
                             case "fmtMessage00":
                                 categories += "<category label='" + fmtmessagelongcommalert[0] + "' />";
                                 break;
                             case "fmtMessage24":
                                 categories += "<category label='" + fmtmessagelongcommalert[1] + "' />";
                                 break;
                             case "fmtMessage48":
                                 categories += "<category label='" + fmtmessagelongcommalert[2] + "' />";
                                 break;
                             case "fmtMessage99":
                                 categories += "<category label='" + fmtmessagelongcommalert[3] + "' />";
                                 break;
                             default:
                                 categories += "<category label='" + chartData[j].xTag + "' />";
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

                     for (var j = 0; j < chartSeries.length; j++) {
                         if (chartSeries[j].displayName != "") {
                            datasets[j] += "</dataset>";
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
    /* function fcChartRender() {
        if ($('#fcChartDiv').is(':visible')) {
            //fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), "150", "0", "0");
            fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), "150", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv");
        }
    } */

    </script>
</head>
<body>

    <div id="meterMini">
        <ul>
            <li><a href="#meterType" id="_meterType"><fmt:message key="aimir.chartData"/></a></li>
            <li><a href="#commStatus" id="_commStatus"><fmt:message key="aimir.gridData"/></a></li>
        </ul>
        <div id="meterType"></div>
        <div id="commStatus"></div>
    </div>


    <div class="div-meter gadget_body2">
        <div id = "ea"><b><fmt:message key='aimir.EA'/></b></div>
        <div id="fcChartDiv" style="height:150px; vertical-align:middle;">
            The chart will appear within this DIV. This text will be replaced by the chart.
        </div>
        <div id="meterMiniChartDivGridDiv" style="display:none;"></div>
    </div>

</body>
</html>