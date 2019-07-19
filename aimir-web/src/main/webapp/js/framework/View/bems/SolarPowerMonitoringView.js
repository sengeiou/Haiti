define([
	"jquery",
    "framework/Config/CommonConstants",
    "framework/View/Control/Chart",
    "framework/View/Control/Grid",
    "framework/View/Control/Tree",
    "framework/View/Control/Alert",
    "framework/Util/LocaleDateUtil",
    "framework/Util/ObjectUtils",
    "extjs/treegrid/TreeGrid",
    "FChartStyle"
], function($, CONST, Chart, Grid, Tree, Alert, DateUtil, Utils, TreeGrid) {
	
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
    var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE;

	var imgWin ;
	var grid = undefined;
	var queueName = undefined;
	var gridOn = false;
	
    var PagingToolbar = Ext.PagingToolbar;
    var preloadingMessage = 
        "The chart will appear within this DIV. This text will be replaced by the chart.";

    var FORMMODE = {
        INSERT: 'insert-mode',
        EDIT: 'edit-mode',
        VIEW: 'view-mode'
    };

    var chartColor = {
        EM: fChartColor_Elec,
        GM: fChartColor_Gas,
        WM: fChartColor_Water,
        HM: fChartColor_Heat,
        SPM: fChartColor_Heat,
        VC: fChartColor_Elec,        
        CO2: fChartColor_CO2
    };

    var STYLE = {
        MIN: {},
        MAX: {
            chartWidth: 400,
            chartHeight: 230,
            manualMeterGridHeight: 500
        }
    };

    var $ELEMENTS = {};

	var toolbarPageFormat = '{0} - {1} of {2}';

	var messageBox = function(val, title, el) {
		Alert.info(val, title, el);
	};

    var confirmBox = function(val, title, ok, no) {
        Alert.confirm(val, title, ok, no);
    };

    var renderWeatherStatus = function(option) {

        if(!option.weather || !option.weather.data) {
            throw $.extend(new Error("weather is undefined"), {
                code: 801,
                callee: "renderWeatherStatus",
                message: "Illegal Arguments, weather is undefined",
                msg: "[801] Illegal Arguments, weather is undefined"
            });
        }

        var fW = option.weather.data[0];               
        var imgSrc = option.ImageMap.imageRoot + option.ImageMap.weather[fW.wfEn];
        var $img = $ELEMENTS.weather.find("img");               
        if($img.size() < 1) {
            $img = $("<img alt='" + option.weather.data[0].wfEn + "'/>");
            $ELEMENTS.weather.append($img);
        }
        $img.attr({
            "src": imgSrc,
            "title": option.weather.locationName + " : " + fW.wfEn,
            "alt": option.weather.locationName + " : " + fW.wfEn
        })
        // adjust vertical align Center.
        .bind("load", function() {
            var adjust = (($ELEMENTS.weather.height() - $img.height()) / 2) + 10;
            $img.css("margin-top", adjust + "px");
        });         

        $ELEMENTS.currentDescr.text(fW.wfKor);

        if(Utils.isFunction(option.callback)) {
            option.callback();
        }        
    }

    var updateCurrentGenerationUI = function(res) {
        res = res || {};
        $ELEMENTS.currentElectric.text(res.current || 0);
        $ELEMENTS.accumulatedElectric.text(res.accumated || res.accumated);
        renderGenerationBarChart(res.hourly || {});
    };

    var renderGenerationBarChart = function(list, callback) {
        // 테스트 코드
        var columnDivWidth = $('#electric-generation-chart').width();

        var columnChartDataXml = 
            "<chart shownames='1' "
             + "showValues='0' "
             + "chartBottomMargin='10' "
             + "chartTopMargin='10' "
             + "chartRightMargin='10' "
             + "chartLeftMargin='10' "
             + "legendBorderAlpha='0' "
             + "legendBgColor='ffffff' "
             + "legendShadow='0' "
             + "divLineAlpha='20' "
             + "divLineColor='aaaaaa' "
             + "canvasBaseColor='bbbbbb' "
             + "canvasBaseDepth='5' "
             + "maxColWidth='40' "
             + "borderColor='d9d9d9' "
             + "canvasBgColor='EEEEEE,D7D7D7' "
             + "showCanvasBg='1' "
             + "showCanvasBase='1' "
             + "labelDisplay='NONE' "
             + "labelStep='2' "
             + "showColumnShadow='0'>";

        var categories = "<categories>";
        var amount = 
            "<dataset seriesName='" + 
            I18N["aimir.bems.powerGenerationAmount"] + "'" + 
            " color='" + fChartColor_Elec[0] +" '>";        

        for(var i=0; i < 24; i++) {
            var k = "H" + i;
            var o = list[k];
            if(list.hasOwnProperty(k)) {
                var hh = (i < 10) ? "0"+i : i;
                categories += "<category label='"+hh+"'/>";
                amount     += "<set value='"+list[k]+"'/>";
            }
        }
        categories += "</categories>";
        amount     += "</dataset>";
         
        columnChartDataXml += categories + amount + "</chart>";

        var columnChart = Chart.renderByXML(
            "StackedColumn3DLineDY",
            {
                renderId: "electric-generation-chart",
                chartId: "columnChart",
                width: columnDivWidth,
                height: "130"
            },
            columnChartDataXml
        );
        if(Utils.isFunction(callback)) callback(columnChart);

        return columnChart;
    };

    var renderInverterBarChart = function(data, clickBarHandler) {
        var inverters = data.inverters;
        var chart = $ELEMENTS.generationInverters;
        var chartData = {
            "chart": {
                yaxisname: I18N['aimir.bems.powerGenerationAmount'] + 
                    ' (' + I18N['aimir.unit.kwh'] + ')'
            },
            "categories": [ {"category":[]} ],
            "dataset": [{
                color: '2C75FC',
                data: [],
                showValue: 0                
            }]
        };

        var len = inverters.length;
        for (var i=0; i < len; i++) {                       
            var obj = inverters[i];
            var category = {
                 label: obj.friendlyName || obj.mdsId,
                 showLabel: 1
            };
            chartData.categories[0].category.push(category);
            var iv = {
                label: obj.friendlyName || obj.mdsId,
                value: obj.generationValue,
                showValue: 0
            };

            // XXX: Funtion Chart 이벤트 핸들러 할당에서 인라인 스크립트 
            // 방식밖에 찾지 못했다. 다른 방법이 있을까...
            var gEvt = GLOBAL_CONTEXT.G_EVENTS || {};
            if(Utils.isFunction(gEvt[clickBarHandler])) {
                iv.link = "javascript:GLOBAL_CONTEXT.G_EVENTS['" +
                    clickBarHandler +"'](" +
                        "'" + obj.id + "'," +
                        "'" + i + "'," +
                        "'" + chart.attr("id") + "'" +
                    ")";
            };

            chartData.dataset[0].data.push(iv);            
        }

        var c = Chart.render("MSBar3D", {
            data: chartData,
            id: chart.attr("id"),
            height: len * 95,
            width: chart.width() - 10,
            chartId: chart.attr("id") + "-with-fchart",
            dataFormat: "json"
        }); 
        return c;
    };

    var renderGenerationStatisticsOnDateChart = function(chartData) {
        $("#inverterStatChartArea .chart-title").hide();

        var eType = "SPM";
        var usage = "usage";
        var co2Usage = "co2";
        var usageUnit = I18N['aimir.unit.kwh'];
        var co2Unit = "kg";
        var barColor = chartColor[eType] || {};
        var lineColor = chartColor.CO2 || {};

        var transformed = {
            dayList: {},
            monthList: {},
            seasonList: {},
            weekList: {}
        };

        for(var k in transformed) {  
            if(transformed.hasOwnProperty(k)) {
                var j = {
                    "chart": {
                        showvalues: 0,
                        PYAxisName: usage + ' (' + I18N['aimir.unit.kwh'] + ')'
                    },
                    "categories": [ {"category":[]} ],
                    "dataset": [
                        {
                            seriesname: usage + " (" + usageUnit + ")", 
                            color: barColor[0],
                            data: []    
                        }
                    ]
                };

                var item = chartData[k] || [];
                var len = item.length || 0;

                for (var i=0; i < len; i++) {                       
                    var obj = item[i];
                    category = {
                        label: obj.MYDATE,
                        showLabel: 1
                    };
                    j.categories[0].category.push(category);
                    j.dataset[0].data.push({
                        value: obj[eType + "SUM"],
                        showValue: 0
                    });
                }
                transformed[k] = j;
            }           
        }
        var ret = {};

        for(var type in transformed) {
            if(transformed.hasOwnProperty(type)) {
                var labelStep = 0;
                var chartRenderTo = type + "_chart";

                var width = $ELEMENTS.powerGenerationDateGroupCharts.width();
                var adjustHalf = width/2-20;

                if(type === 'dayList') {
                    labelStep = 2;
                }       

                ret[type] = Chart.render("MSCombiDY2D", {
                    data: transformed[type],
                    id: chartRenderTo,
                    width: adjustHalf,
                    height: STYLE[SIZE].chartHeight,
                    chartId: type + "_chart_flash",
                    dataFormat: "json"
                }, {
                    chartLeftMargin: '0',
                    chartRightMargin: '0',
                    chartTopMargin: '5',
                    chartBottomMargin: '0',
                    useRoundEdges: '1',
                    legendPosition: 'RIGHT',
                    labelDisplay: 'NONE',
                    labelStep: labelStep
                });
            }
        }

        $("#inverterStatChartArea .chart-title").show();

        return ret;
    };
    
    var generationStatisticsDetail = function(params) {
    	console.log("params :" +params);
 
		var colModel = new Ext.grid.ColumnModel({
			defaults : {
				width : 900,
				height : 580,
				sortable : true
			},
			columns : [
			{
			    header: I18N["aimir.number"],
			    align: 'center',
			    width: 80,
			    dataIndex: "num"
			}, {
				 header: I18N["aimir.bems.inverter"] + I18N["aimir.number"],
                 align: 'center',
                 width: 120,
                 dataIndex: "meterNo"
			},{
				header: I18N["aimir.meteringdate"],
				width : 130,
				dataIndex : "meteringTime"
			}, {
                header:"00 " +I18N["aimir.minute"]+
                ' (' + I18N['aimir.unit.kwh'] + ')',
                width: 100,
                align: 'right',
                dataIndex: "channel_1"
			},{
				header: "15 "  +I18N["aimir.minute"]+
                ' (' + I18N['aimir.unit.kwh'] + ')',
                width: 100,
                align: 'right',
                dataIndex: "channel_2"
			},{
				header: "30 " + I18N["aimir.minute"]+
                ' (' + I18N['aimir.unit.kwh'] + ')',
                width: 100,
                align: 'right',
                dataIndex: "channel_3"
			},{
				header: "45 " + I18N["aimir.minute"]+
                ' (' + I18N['aimir.unit.kwh'] + ')',
                width: 100,
                align: 'right',
                dataIndex: "channel_4"
			},{
                header: "총 "+I18N["aimir.bems.powerGenerationAmount"] + 
                    ' (' + I18N['aimir.unit.kwh'] + ')',
                width: 120,
                align: 'right',
                dataIndex: "value"
            }
            ]
		});
		
		//그리드 설정
		if (gridOn == false) {

			grid = new Ext.grid.GridPanel({
				
				id: params.gridId,
				height : 580,
				renderTo : 'solarDetail',
				store : params.store,
				colModel : colModel,
				width : 900
			});

			gridOn = true;
		} else {
			grid.reconfigure(params.store, colModel);
		}
		
		Ext.getCmp(grid.id).getStore().load();
	
		if (!imgWin) {
			imgWin = new Ext.Window({
				title : 'solarDetailView',
				id : 'solarDetailtWinId',
			
				width : 900,
				height : 580,
				pageX : 20,
				pageY : 50,
				items : grid,
				plain: true,
				closeAction : 'hide',
	
			});
			
		} else {
			imgWin.setHeight(620);
			grid.setHeight(620);
		
		}
		
		Ext.getCmp('solarDetailtWinId').show();
	
    }
    var renderGenerationStatistics = function(params) {

        // CSS Bug Fix.
        // Extjs Grid 패널은 width가 명시적으로 지정되어 있지 않을 경우 
        // 정상적으로 fit force를 적용하지 못하기에 명시적으로 지정해준다.
        $ELEMENTS.generationStatisticsGrid.css("width","100%");
      
        var conf = {
            id: params.gridId,
            store: params.store,
            renderTo: $ELEMENTS.generationStatisticsGrid.attr("id"),
            columns: [
                {
                    header: I18N["aimir.number"],
                    align: 'center',
                    width: 60, 
                    dataIndex: "num"
                },
                {
                    header: I18N["aimir.bems.inverter"] + I18N["aimir.id"],
                    align: 'center',
                    width: 135,
                    dataIndex: "meterNo"
                },
                {
                    header: I18N["aimir.bems.inverter"] + I18N["aimir.name"],
                    align: 'center',
                    width: 135,
                    dataIndex: "friendlyName"
                },
                {
                    header: I18N["aimir.meteringdate"],
                    align: 'center',
                    width: 100,
                    dataIndex: "meteringTime"
                },
               
                {
                    header: I18N["aimir.bems.powerGenerationAmount"] + 
                        ' (' + I18N['aimir.unit.kwh'] + ')',
                    width: 150,
                    align: 'right',
                    dataIndex: "value"
                },
                { 
                    xtype: 'actioncolumn',
                    width: 100,
                    align: 'center',
                    items: [{
	                    icon   : GLOBAL_CONTEXT.CONTEXT + '/js/extjs/examples/shared/icons/fam/Detail.png',
	                    tooltip: I18N["aimir.view.detail"],
                    	handler: params.showDetail
	                }]
                }
            ],
            bbar: new PagingToolbar({
                pageSize: params.store.pageSize,
                displayInfo: true,
                displayMsg: toolbarPageFormat,
                store: params.store
            })
        };        
        return Grid.render(conf);
    };

    var renderTopMenuTab = function(option) {
        option = option || {};
        if($ELEMENTS.topMenuTab.size() > 0) {
            $ELEMENTS.topMenuTab.tabs(option);
        }
    };

    // XXX: !! async 오류 발생 소지 있음... !!
    var setTodayInputOnInverterInfomation = function(rawDate) {
        DateUtil.setSupplierId(supplierId);
        $("#date-search-wrapper")
            .find("input[name=daily-day]").val(rawDate);
    };

    var renderDateForm = function(supplierId) {
        
        DateUtil.renderDateSelector({
            supplierId: supplierId,
            renderTo: $("#date-search-wrapper"),
            daily: true,
            prefix: "daliy-only-",
            callback: function($tabs) { }
        });  

        DateUtil.renderDateSelector({
            supplierId: supplierId,
            renderTo: $("#generarion-view .date-form"),
            isShowTab: true,
            daily: true,
            weekly: true,
            monthly: true,
            dayPeriod: true,
            seasonal: true,
            yearly: true,
            prefix: "rd_",
            additionalCss: {
                "background": "#FFFFFF"
            },
            callback: function($tabs) { }
        });
    };

    // jquery selectbox bug fix.
    var __fixSelectWidth = function($sel, data) {
        if($sel.width() != 120) {
            $sel.width(120);
        }
        if(!data || data.length < 1) {
            $sel.width(122);
        }
    };

    var renderVenderForm = function(venders) {
        var $sel = $ELEMENTS.singleRegMeterVendor;
        __fixSelectWidth($sel, venders.deviceVendors);
        $sel.pureSelect(venders.deviceVendors);
        $sel.selectbox();
        return $sel;
    };

    var renderModelForm = function(model) {
        var $sel = $ELEMENTS.singleRegMeterModel;
        __fixSelectWidth($sel, model);
        $sel.pureSelect(model);
        $sel.selectbox();
        return $sel;
    };

    var initializeUI = function() {
        $ELEMENTS = {
            selectboxes: $("select"),
            topMenuTab: $("#wrapper .tab-menu"),
            currentDescr: $("#weather span"),
            weather: $("#weather .current-weather"),
            currentElectric: $("#current-electric"),
            accumulatedElectric: $("#accumulated-electric"),
            electricGenerationChart: $("#electric-generation-chart"),
            generationInverters: $("#power-generation-inverters-chart"),
            powerGenerationDateGroupCharts :$("#power-generation-date-group-charts"),
            generationStatisticsGrid: $("#generation-statistics-grid"),
            singleRegMeterVendor: $("#singleRegMeterVendor"),
            singleRegMeterModel: $("#singleRegMeterModel")
        }; 

        return $ELEMENTS;
    };

    var displayForInverterIdTextStatistics = function(text) {
        var $span = $ELEMENTS.powerGenerationDateGroupCharts.find("h1 > span");
        $span.text(" # " + text);
    };
	
	return {
        initializeUI: initializeUI,
        renderDateForm: renderDateForm,
        setTodayInputOnInverterInfomation: setTodayInputOnInverterInfomation,
        renderTopMenuTab: renderTopMenuTab,
        renderGenerationBarChart: renderGenerationBarChart,
		messageBox: messageBox,
        confirmBox: confirmBox,
        renderVenderForm: renderVenderForm,
        renderModelForm: renderModelForm,
        renderWeatherStatus: renderWeatherStatus,
        updateCurrentGenerationUI: updateCurrentGenerationUI,
        renderInverterBarChart: renderInverterBarChart,
        renderGenerationStatistics: renderGenerationStatistics,
        locationTreeGoGo: Tree.locationTreeGoGo,
        displayForInverterIdTextStatistics: displayForInverterIdTextStatistics,
        renderGenerationStatisticsOnDateChart: renderGenerationStatisticsOnDateChart,
        generationStatisticsDetail:generationStatisticsDetail
	};
	
});