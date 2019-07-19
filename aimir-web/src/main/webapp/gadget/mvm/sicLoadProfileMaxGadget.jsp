<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title>SIC Load Profile Max Gadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
<%--
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지
        TABLE{border-collapse: collapse; width:auto;} */                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            aaaaaaaaaaaaaaaaaaaaaaa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완
        @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        } */
--%>
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
        }
        /* no Icon */
        .no-icon {
            display: none;
            background-image:url(${ctx}/js/extjs/resources/images/default/s.gif) !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var supplierId = ${supplierId};
        var tabsSnd = {
            //hourly : 0,
            //daily : 0,
            period : 1//--,
            //weekly : 0,
            //monthly : 0,
            //monthlyPeriod : 0,
            //weekDaily : 0,
            //seasonal : 0,
            //yearly : 0
        };
        var tabNamesSnd = {};
        
        var lpChart;
        var totalLpChart;

        $(document).ready(function(){
            Ext.QuickTips.init();
        	$(function() { $('#lpChart input:radio').bind('change',function(event) { updateLpChart(); } ); });
            hide();
            getSicCustomerEnergyUsageList();
        });

        // window resize event
        $(window).resize(function() {
        	isResize = true;
        	//getSicCustomerEnergyUsageList();
        	makeSicContEnergyUsageTree();

        	if($('#totalLpChartDiv').is(':visible')) {
        		totalLpChartRender();
        	} else {
        		lpChartRender();
        	}
        });

        function sendSnd() {
        	getSicCustomerEnergyUsageList();
        }

        var sicCode = "";     // grid 에서 선택한 row 의 SIC Code
        /* SIC 사용량통계 리스트 START */
        //var sicEnergyUsageGridOn = false;
        //var sicEnergyUsageGrid;
        //var sicEnergyUsageColModel;
        //
        ////에너지 사용량 grid 가져오기
        //function getSicCustomerEnergyUsageList() {
        //    var width = $("#sicUsageGridDiv").width();
        //    //var rowSize = 8;
        //
        //    emergePre();
        //    var sicEnergyUsageStore = new Ext.data.JsonStore({
        //        autoLoad: true,
        //        url: "${ctx}/gadget/mvm/getSicCustomerEnergyUsageList2.do",
        //        baseParams: {
        //            supplierId : supplierId,
        //            searchStartDate : $("#searchStartDateSnd").val(),
        //            searchEndDate : $("#searchEndDateSnd").val()
        //        },
        //        totalProperty: 'totalCount',
        //        root:'result',
        //        fields: ["sicId", "sicCode", "sicName", "customerCount", "usageSum"],
        //        listeners: {
        //            load: function(store, record, options) {
        //                if (record.length > 0) {
        //                    // 데이터 load 후 첫번째 row 자동 선택
        //                    sicEnergyUsageGrid.getSelectionModel().selectFirstRow();
        //                } else {
        //                	sicCode = "";
        //                	changeChartDiv(false);
        //                	getLpChartData();
        //                }
        //            }
        //        }//리스너 end
        //    });
        //
        //    sicEnergyUsageColModel = new Ext.grid.ColumnModel({
        //        columns: [
        //            {header: '<fmt:message key="aimir.class"/>', dataIndex: 'sicName', width: (width)/3}
        //           ,{header: '<fmt:message key="aimir.customercount"/>', dataIndex: 'customerCount', width: (width)/3}
        //           ,{header: '<fmt:message key="aimir.usagesum"/>', dataIndex: 'usageSum', width: (width)/3}
        //        ],
        //        defaults: {
        //            sortable: true
        //           ,menuDisabled: true
        //           ,width: 120
        //       }
        //    });
        //
        //    if(sicEnergyUsageGridOn == false) {
        //
        //    	sicEnergyUsageGrid = new Ext.grid.GridPanel({
        //            //title: '최근 한달 Demand Response History',
        //            store: sicEnergyUsageStore,
        //            colModel : sicEnergyUsageColModel,
        //            //sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
        //            sm: new Ext.grid.RowSelectionModel({
        //                singleSelect:true,
        //                listeners: {
        //                    rowselect: function(sm, row, rec) {
        //                        sicCode = rec.get("sicCode");
        //
        //                    	if (rec.get("sicId") == 0) {
        //                    		changeChartDiv(true);
        //                    		getTotalLpChartData();
        //                    	} else {
        //                    		changeChartDiv(false);
        //                            getLpChartData();
        //                    	}
        //                    }//end of rowselect
        //                }
        //            }),
        //            autoScroll:true,
        //            width: width,
        //            height: 500,
        //            stripeRows : true,
        //            columnLines: true,
        //            loadMask:{
        //                msg: 'loading...'
        //            },
        //            renderTo: 'sicUsageGridDiv',
        //            viewConfig: {
        //                enableRowBody:true,
        //                showPreview:true,
        //                forceFit:true,
        //                emptyText: 'No data to display'
        //            }
        //        });
        //    	sicEnergyUsageGridOn = true;
        //    } else {
        //    	sicEnergyUsageGrid.setWidth(width);
        //        sicEnergyUsageGrid.reconfigure(sicEnergyUsageStore, sicEnergyUsageColModel);
        //    }
        //    hide();
        //};

        var treeData;
        // DTS Tree Data 조회
        function getSicCustomerEnergyUsageList() {
            emergePre();
            $.getJSON('${ctx}/gadget/mvm/getSicContEnergyUsageTreeData.do'
                    , { supplierId : supplierId,
                        searchStartDate : $("#searchStartDateSnd").val(),
                        searchEndDate : $("#searchEndDateSnd").val()}
                    , function (json){
                        treeData = json.result
                        hide();
                        makeSicContEnergyUsageTree();
                    });
        };

        /* SIC Tree 생성 */
        var sicUsageTreeGridOn = false;
        var sicUsageTreeGrid;
        var sicUsageTreeRootNode;
        //var sicUsageTreeColModel;
        function makeSicContEnergyUsageTree() {
            var width = $("#sicUsageGridDiv").width();
            var headerClass = "<fmt:message key="aimir.class"/>";
            var headerContract = "<fmt:message key="aimir.customercount"/>";
            var headerUsage = "<fmt:message key="aimir.usagesum"/>";

            var sicUsageTreeColModel = [
                   {header: "<font style='font-weight: bold;'>" + headerClass + "</font>", dataIndex: 'sicName', width: (width / 3) 
                        ,tpl: new Ext.XTemplate('{sicName:this.viewToolTip}', {
                            viewToolTip: addTreeTooltip
                        })
                   }
                   ,{header: "<font style='font-weight: bold;'>" + headerContract + "</font>", dataIndex: 'customerCount', width: (width / 3) - 20, align: "right"}
                   ,{header: "<font style='font-weight: bold;'>" + headerUsage + "</font>", dataIndex: 'usageSum', width: (width / 3), align: "right"}
            ];

            sicUsageTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'root',
                draggable:false,
                expended:true,
                children: treeData
            });

            if (!sicUsageTreeGridOn) {
                var treeLoader = new Ext.tree.TreeLoader({
                    url:'${ctx}/gadget/mvm/getSicContEnergyUsageTreeData.do',
                    baseParams: {
                      supplierId : supplierId,
                      searchStartDate : $("#searchStartDateSnd").val(),
                      searchEndDate : $("#searchEndDateSnd").val()
                    }
                });

                treeLoader.on("beforeload", function(treeLoader, node) {
                    treeLoader.baseParams.searchStartDate = $("#searchStartDateSnd").val();
                    treeLoader.baseParams.searchEndDate = $("#searchEndDateSnd").val();
                })

                sicUsageTreeGrid = new Ext.ux.tree.TreeGrid({
                    width: width,
                    height: 500,
                    renderTo: "sicUsageGridDiv",
                    enableDD: false,
                    enableHdMenu : false,
                    enableSort : false,
                    columns: sicUsageTreeColModel,
                    loader: treeLoader,
                    root: sicUsageTreeRootNode,
                    rootVisible: false
                });

                sicUsageTreeGrid.on("click", selectSicUsageTreeNode);
                sicUsageTreeGridOn = true;
            } else {
                sicUsageTreeGrid.setWidth(width);
                sicUsageTreeGrid.setRootNode(sicUsageTreeRootNode);
                sicUsageTreeGrid.render();
            }

            selectTreeNodeAfterLoad();
        }

        // treegrid column tooltip
        function addTreeTooltip(value, values) {
            if (value != null && value != "" && values != null) {
                return '<span qtip="' + value + '">' + value + '</span>';
            } else {
                return value;
            }
        }

        // SIC Tree 클릭 시 선택한 Node 의 정보를 setting
        function selectSicUsageTreeNode(node, e) {
            if (node.attributes.isClick == "true") {
                sicCode = node.attributes.sicCode;

                if (node.attributes.sicId == 0) {
                    changeChartDiv(true);
                    getTotalLpChartData();
                } else {
                    changeChartDiv(false);
                    getLpChartData();
                }
            }
        }

        var isResize = false;
        // SIC Tree 클릭 시 선택한 Node 의 정보를 setting
        function selectTreeNodeAfterLoad() {
            if (isResize) {
            	isResize = false;
            	return;
            }

            var rootNode = sicUsageTreeGrid.getRootNode();
            var fstChildNodes = rootNode.childNodes;

            if (fstChildNodes != null && fstChildNodes.length > 0) {
                var flen = fstChildNodes.length;

                for (var i = 0; i < flen; i++) {
                	if (fstChildNodes[i].hasChildNodes()) {
                		var sndChildNode = fstChildNodes[i].firstChild;
                		sndChildNode.fireEvent("click", sndChildNode);
                		break;
                	}
                }
            }
        }

        var lpChartData;
        // Load Profile Chart Data 조회
        function getLpChartData() {
            emergePre();
            $.getJSON('${ctx}/gadget/mvm/getSicLoadProfileChartData.do'
                    ,{supplierId : supplierId,
            	      sicCode : sicCode,
                      searchStartDate : $("#searchStartDateSnd").val(),
                      searchEndDate : $("#searchEndDateSnd").val()}
                    ,function(json) {
                    	 lpChartData = json;
                         updateLpChart();
                    }
            );
        }

        var lpChartDataXml;
        // LP Chart 생성
        function updateLpChart() {
            var timeData = lpChartData.timeList;
            var workLpData;
            var satLpData;
            var sunLpData;
            var holiLpData;
            var peakLpData;

            var isAvg = false;

            if ($("#chartType:checked").val() == "avg") {
                isAvg = true;
            }

            var labelStep = 2;
            var yName;
            if (isAvg) {
                yName = "<fmt:message key="aimir.demandcustomer"/> [kW]";
            } else {
                yName = "<fmt:message key="aimir.demand"/> [kW]";
            }

            lpChartDataXml = "<chart "
                           + "xAxisName='<fmt:message key="aimir.lp.time"/>' "
                           + "yAxisName='" + yName + "' "
                           + "yAxisMaxValue='1' "
                           + "chartLeftMargin='10' "
                           + "chartRightMargin='20' "
                           + "chartTopMargin='20' "
                           + "chartBottomMargin='5' "
                           + "showValues='0' "
                           + "showLabels='1' "
                           + "showLegend='1' "
                           //+ "legendPosition='BOTTOM' "
                           + "legendPosition='RIGHT' "
                           //+ "labelDisplay='WRAP' "
                           + "labelDisplay='ROTATE' "
                           // + "numberSuffix='  ' "
                           //+ "labelStep='" + labelStep + "' "
                           + "decimals='3' "
                           // + "forceDecimals='1' "
                           + fChartStyle_Common
                           + fChartStyle_Font
                           //+ xml_fChartStyle_Column2D_nobg
                           + fChartStyle_MSCombiDY2D_nobg
                           + fChartStyle_legendScroll
                           + ">";

            var categories = new Array();
            var datasetWorkLpData = new Array();
            var datasetSatLpData = new Array();
            var datasetSunLpData = new Array();
            var datasetHoliLpData = new Array();
            var datasetPeakLpData = new Array();

            categories.push("<categories>");
            datasetWorkLpData.push("<dataset seriesName='<fmt:message key="aimir.workingday"/>' renderAs='Line' lineThickness='3'>");
            datasetSatLpData.push("<dataset seriesName='<fmt:message key="aimir.saturday"/>' renderAs='Line' lineThickness='3'>");
            datasetSunLpData.push("<dataset seriesName='<fmt:message key="aimir.sunday"/>' renderAs='Line' lineThickness='3'>");
            datasetHoliLpData.push("<dataset seriesName='<fmt:message key="aimir.holiday"/>' renderAs='Line' lineThickness='3'>");

            if (isAvg) {
                datasetPeakLpData.push("<dataset seriesName='<fmt:message key="aimir.peakday"/>' renderAs='Line' lineThickness='3'>");
            }

            if (isAvg) {
                workLpData = lpChartData.workAvgList;
                satLpData = lpChartData.satAvgList;
                sunLpData = lpChartData.sunAvgList;
                holiLpData = lpChartData.holiAvgList;
                peakLpData = lpChartData.peakList;
            } else {
                workLpData = lpChartData.workSumList;
                satLpData = lpChartData.satSumList;
                sunLpData = lpChartData.sunSumList;
                holiLpData = lpChartData.holiSumList;
                peakLpData = new Array();
            }

            var timeDataLen = timeData.length;
            var workLpDataLen = workLpData.length;
            var satLpDataLne = satLpData.length;
            var sunLpDataLen = sunLpData.length;
            var holiLpDataLen = holiLpData.length;
            var peakLpDataLen = peakLpData.length;

            for (var i = 0 ; i < timeDataLen ; i++) {
                categories.push("<category label='" + timeData[i] + "' />");
                if (workLpDataLen > 0) {
                    datasetWorkLpData.push("<set value='" + workLpData[i] + "'/>");
                } else {
                    datasetWorkLpData.push("<set value='0'/>");
                }

                if (satLpDataLne > 0) {
                    datasetSatLpData.push("<set value='" + satLpData[i] + "'/>");
                } else {
                    datasetSatLpData.push("<set value='0'/>");
                }

                if (sunLpDataLen > 0) {
                    datasetSunLpData.push("<set value='" + sunLpData[i] + "'/>");
                } else {
                    datasetSunLpData.push("<set value='0'/>");
                }

                if (holiLpDataLen > 0) {
                    datasetHoliLpData.push("<set value='" + holiLpData[i] + "'/>");
                } else {
                    datasetHoliLpData.push("<set value='0'/>");
                }

                if (isAvg) {
                    if (peakLpDataLen > 0) {
                        datasetPeakLpData.push("<set value='" + peakLpData[i] + "'/>");
                    } else {
                        datasetPeakLpData.push("<set value='0'/>");
                    }
                }
            }

            categories.push("</categories>\n");
            datasetWorkLpData.push("</dataset>\n");
            datasetSatLpData.push("</dataset>\n");
            datasetSunLpData.push("</dataset>\n");
            datasetHoliLpData.push("</dataset>\n");
            if (isAvg) {
                datasetPeakLpData.push("</dataset>\n");
            }

            lpChartDataXml += categories.join("") + datasetWorkLpData.join("") + datasetSatLpData.join("")
                               + datasetSunLpData.join("") + datasetHoliLpData.join("");

            if (isAvg) {
            	lpChartDataXml += datasetPeakLpData.join("");
            }

            lpChartDataXml += "</chart>";
            lpChartRender();
            hide();
        }

        function lpChartRender() {
            if($('#lpChartDiv').is(':visible')) {
                var width = $('#lpChartDiv').width();
                //lpChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "lpChartId", width, "500", "0", "0");
                lpChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombi2D.swf", "lpChartId", width, "500", "0", "0");
                lpChart.setDataXML(lpChartDataXml);
                lpChart.setTransparent("transparent");
                lpChart.render("lpChartDiv");
            }
        }

        // Total Load Profile Chart Data 조회
        function getTotalLpChartData() {
            emergePre();

            $.getJSON('${ctx}/gadget/mvm/getSicTotalLoadProfileChartData.do'
                    ,{supplierId : supplierId,
                      searchStartDate : $("#searchStartDateSnd").val(),
                      searchEndDate : $("#searchEndDateSnd").val()}
                    ,function(json) {
                         updateTotalLpChart(json);
                    }
            );
        }

        var totalLpChartDataXml;
        // Total LP Chart 생성
        function updateTotalLpChart(data) {
            var timeData =  data.timeList;
            var totalLpData = data.totalList;

            totalLpChartDataXml = "<chart "
                           // + "caption='<fmt:message key="aimir.ebs.importchart"/>' "
                           + "xAxisName='<fmt:message key="aimir.lp.time"/>' "
                           + "yAxisName='<fmt:message key="aimir.demand"/> [MW]' "
                           //+ "yAxisMaxValue='1' "
                           + "chartLeftMargin='10' "
                           + "chartRightMargin='20' "
                           + "chartTopMargin='20' "
                           + "chartBottomMargin='5' "
                           + "showValues='0' "
                           + "showLabels='1' "
                           + "showLegend='1' "
                           //+ "legendPosition='BOTTOM' "
                           + "legendPosition='RIGHT' "
                           //+ "labelDisplay='WRAP' "
                           + "labelDisplay='ROTATE' "
                           // + "numberSuffix='  ' "
                           //+ "labelStep='" + labelStep + "' "
                           + "decimals='3' "
                           // + "forceDecimals='1' "
                           + fChartStyle_Common
                           + fChartStyle_Font
                           //+ xml_fChartStyle_Column2D_nobg
                           + fChartStyle_MSCombiDY2D_nobg
                           + fChartStyle_legendScroll
                           + ">";

            var categories = new Array();
            var datasetTotalLpData = null;
            var lpData = new Array();

            categories.push("<categories>");

            var timeDataLen = timeData.length;
            var totalLpDataLen = totalLpData.length;
            
            //alert("timeDataLen:" + timeDataLen + ", totalLpDataLen:" + totalLpDataLen);
            var lpDataLen = 0;

            for (var i = 0 ; i < timeDataLen ; i++) {
                categories.push("<category label='" + timeData[i] + "' />");
            }

            categories.push("</categories>\n");
            totalLpChartDataXml += categories.join("");

            if (totalLpDataLen == 0) {
            	datasetTotalLpData = new Array();
                datasetTotalLpData.push("<dataset seriesName='' renderAs='Area'>");

                for (var i = 0 ; i < timeDataLen ; i++) {
                    datasetTotalLpData.push("<set value='0'/>");
                }

                datasetTotalLpData.push("</dataset>\n");
                totalLpChartDataXml += datasetTotalLpData.join("");
            } else {
                for (var i = 0 ; i < totalLpDataLen ; i++) {
                    datasetTotalLpData = new Array();
                    datasetTotalLpData.push("<dataset seriesName='" + totalLpData[i].name + "' renderAs='Area'>");

                    lpData = totalLpData[i].list;
                    lpDataLen = lpData.length;

                    for (var j = 0 ; j < lpDataLen ; j++) {
                        datasetTotalLpData.push("<set value='" + lpData[j] + "'/>");
                    }

                    datasetTotalLpData.push("</dataset>\n");
                    totalLpChartDataXml += datasetTotalLpData.join("");
                }
            }

            totalLpChartDataXml += "</chart>";
            totalLpChartRender();
            hide();
        }

        function totalLpChartRender() {
            if($('#totalLpChartDiv').is(':visible')) {
                var width = $('#totalLpChartDiv').width();
                totalLpChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombi2D.swf", "totalLpChartId", width, "500", "0", "0");
                totalLpChart.setDataXML(totalLpChartDataXml);
                totalLpChart.setTransparent("transparent");
                totalLpChart.render("totalLpChartDiv");
            }
        }

        function changeChartDiv(istotal) {
            if (istotal) {
                $("#lpChart").hide();
                $("#totalLpChartDiv").show();
            } else {
                $("#lpChart").show();
                $("#totalLpChartDiv").hide();
            }
        }
    /*]]>*/
    </script>
<%--
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
    <style type="text/css">
	
	.x-grid3-header-offset {
	    padding-left: 1px;
	    text-align: center;
	    width: 650px;
	}
	
	.x-grid3-col-0 {
    
    text-align: center;
	}
	
	.x-grid3-col-1 {
    text-align: right;
	}
	
	.x-grid3-col-2 {
    text-align: right;
	}
	
	.x-grid3-td-2 {
		width: 198px;
	} 
	</style>
--%>
</head>
<body>
    <div class="search-bg-basic" style="height:35px !important;">
        <div class="height_30" style="margin:0px 0px 0px 5px;">
        <%@ include file="/gadget/commonDateTabButtonType4.jsp"%>
        </div>
    </div>
    <div class="gadget_body3">
        <div class="overflow_hidden">
            <div id="sicUsageGridDiv" class="chart_left" style="width:33% !important;"></div>
            <div id="lpChart" class="chart_right" style="width:65% !important;">
                <div class="add_search">
                    <table class="billing">
                        <tr>
                            <td>
                                <span><input id="chartType" type="radio" name="chartType" value="avg" class="radio" checked="checked"/></span>
                                <span class="margin-t3px margin-r10">Average</span>
                                <span><input id="chartType" type="radio" name="chartType" value="usage" class="radio"/></span>
                                <span class="margin-t3px">Usage</span>
                                
                            </td>
                        </tr>
                    </table>
                </div>
                <div id="lpChartDiv" style="width:100%;"></div>
            </div>
            <div id="totalLpChartDiv" class="chart_right" style="width:65% !important;display:none;"></div>
        </div>
    </div>
</body>
</html>