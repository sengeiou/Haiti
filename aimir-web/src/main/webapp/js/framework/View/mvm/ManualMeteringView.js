define([
    "framework/Config/CommonConstants",
	"framework/View/Control/Tree",
	"framework/View/Control/Chart",
	"framework/View/Control/Grid",
	"framework/View/Control/Alert",	
	"FChartStyle"
], function(CONST, Tree, Chart, Grid, Alert) {
	
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE;
	var PagingToolbar = Ext.PagingToolbar;
	var RowSelectionModel = Ext.grid.RowSelectionModel;
	var TabPanel = Ext.TabPanel;
	var renderObjects;
	var $ELEMENTS = {};

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	
	var STYLE = {
		MIN: {},
		MAX: {
			chartWidth: 400,
			chartHeight: 230,
			manualMeterGridHeight: 500
		}
	};

	var toolbarPageFormat = '{0} - {1} of {2}';
	
	var BASE = {
		window: {
			layout: 'anchor',
			plain: true,
			autoHeight: true,
	        width: 320,
			closeAction: 'close',
			modal: true,
	        items: []	        
		},
		form: {
			frame: {
				id:	'updateMeteringDataView',
		        frame: true,
		        items: [],
		        buttons: [{
		            text: I18N['aimir.save2']
		        },{
		            text: I18N['aimir.cancel']
		        }]
			},
			combo: {
		        typeAhead: true,
		        triggerAction: 'all',
		        lazyRender:true,
		        editable: false,
		        mode: 'local',
		        valueField: 'id',
		        displayField: 'name',       
		        allowBlank: false
			},
			textfield: {
				allowBlank: false
			},
			datefield: {
	            editable: false,
	            format: 'Y/m/d',
	            allowBlank: false
			}
		}
	};

	var tabConfig = {
		renderTo: 'manual_metring_type_grid',				
        activeTab: 0,
        plain: true,
        defaults: {
        	autoHeight: true
        }
	};
	
	var chartColor = {
		EM:	fChartColor_Elec,
		GM:	fChartColor_Gas,
		WM: fChartColor_Water,
		HM: fChartColor_Heat,
		VC: fChartColor_Elec,
		CO2: fChartColor_CO2
	};
	
	var saveObjectForPopup = function(value) {
		if(!GLOBAL_CONTEXT.SHARE) {
			GLOBAL_CONTEXT.SHARE = {};
			GLOBAL_CONTEXT.SHARE["pop"] = value;
		}
	};

	var renderMeteringGrid = function(params) {
		
		// 단위 조정
		var unit = CONST.EnergyUnit[params.meterType];
		unit = (unit) ? " ["+ unit +"]" : '';		
		params.unit = unit;

		return renderMeteringGrid[SIZE](params);
	};
	
	var renderMeteringForm = function(params) {
		var formItem = [{
            xtype:'combo',
            hiddenName: 'id',
            store: params.stateStore,
            fieldLabel: I18N["aimir.state"]
        }];
		
		var MeteringForm = Ext.apply({}, BASE.form.frame);
		MeteringForm.id = ID.situationFormId,
		MeteringForm.buttons[0].handler = params.handler.saveHandler;
		MeteringForm.buttons[1].handler = params.handler.cancelHandler;
		
		var len = formItem.length;
		while(len--) {
			formItem[len] = Ext.apply(formItem[len], BASE.form[formItem[len].xtype]);
		};
		MeteringForm.items = formItem;
		
		var window = Ext.apply({}, BASE.window);
		window.id = ID.situationFormWindowId;
		window.items = new Ext.FormPanel(MeteringForm);
		window.width = 300;
		
    	var sf = new Ext.Window(window);
    	sf.render('end_device_situation_form');
    	sf.setTitle(
    		I18N['aimir.state'] + ' ' + I18N['aimir.bems.facilityMgmt.update']
    	);
    	
    	if(renderObjects) {
    		renderObjects[window.id] = sf;
    	}
    	
    	return sf;
	};
	
	renderMeteringGrid.MIN = function(params) {
		var conf = {
			id: params.gridId,
			title: params.title,
			store: params.store,
			columns: [
	    	    {
	    	    	header: I18N["aimir.meteringdate"],
	    	    	align: 'center',
	    	    	width: 90,
	    	    	dataIndex: "meteringdate"
	    	    },
	    	    {
	    	    	header: I18N["aimir.meterid"],
	    	    	align: 'center',
	    	    	width: 90,
	    	    	dataIndex: "mdsId"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	align: 'center',
	    	    	width: 100,
	    	    	dataIndex: "friendlyName"
	    	    },	    	    
	    	    {
	    	    	header: I18N["aimir.usage"] + params.unit,
	    	    	width: 100,
	    	    	align: 'right',
	    	    	dataIndex: "total"
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

	renderMeteringGrid.MAX = function(params) {
		var conf = {
			id: params.gridId,
			title: params.title,
			store: params.store,
			columns: [
	    	    {
	    	    	header: I18N["aimir.meteringdate"],
	    	    	align: 'center',
	    	    	width: 90,
	    	    	dataIndex: "meteringdate"
	    	    },
	    	    {
	    	    	header: I18N["aimir.meterid"],
	    	    	align: 'center',
	    	    	width: 90,
	    	    	dataIndex: "mdsId"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	align: 'center',
	    	    	width: 100,
	    	    	dataIndex: "friendlyName"
	    	    },	    	    
	    	    {
	    	    	header: I18N["aimir.usage"] + params.unit,
	    	    	width: 100,
	    	    	align: 'right',
	    	    	dataIndex: "total"
	    	    },
	    	    {
	    	    	header: I18N["aimir.thisdaydata"],
	    	    	width: 70,
	    	    	align: 'right',
	    	    	dataIndex: "baseValue",
	    	    	editor:{
                		xtype:'textfield',
                		allowBlank:false
            		}
	    	    },
	    	    {
	                xtype: 'actioncolumn',
	                width: 20,
	                align:'center',
	                items: [{
	                    icon   : GLOBAL_CONTEXT.CONTEXT + '/js/extjs/examples/shared/icons/fam/accept.png',
	                    tooltip: I18N['aimir.bems.facilityMgmt.update'],
	                    handler: params.updateMeteringDataWindow
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
	
	var renderManualMeterListGrid = function(params) {
		return renderManualMeterListGrid[SIZE](params);
	};
	renderManualMeterListGrid.MIN = function(params) {};
	renderManualMeterListGrid.MAX = function(params) {
		var width = $("#"+params.gridId).width();
		var colWidth = (width/2) - 2;
		var conf = {
			id: params.gridId + "ex",
			renderTo: params.gridId,
			title: params.title,
			store: params.store,
			width: width,
			height: STYLE[SIZE].manualMeterGridHeight,
			selModel: new RowSelectionModel({
	    		singleSelect: true
	    	}),
			columns: [
	    	    {
	    	    	header: I18N["aimir.meterid"],
	    	    	align: 'center',
	    	    	width: colWidth,
	    	    	dataIndex: "mdsId"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	width: colWidth,
	    	    	align: 'center',
	    	    	dataIndex: "friendlyName"
	    	    }
	    	]
		};
		return Grid.render(conf);
	};
	
	var renderMeteringChartByManual = function(params) {
		return renderMeteringChartByManual[SIZE](params);
	};
	renderMeteringChartByManual.transform = function(params) {
		var eType = params.energyType || "EM";
		var usage = I18N['aimir.usage'] || "usage";
		var co2Usage = I18N['aimir.co2formula2'] || "co2";
		var usageUnit = CONST.EnergyUnit[eType] || I18N['aimir.unit.kwh'];
		var co2Unit = CONST.EnergyUnit.CO2 || "kg";
		var barColor = chartColor[eType] || {};
		var lineColor = chartColor.CO2 || {};
		var chartData = (params.data) ? params.data.result : {};

		var charts = {
			dayLimitList: {},
			monthList: {},
			seasonList: {},
			weekList: {}
		};
		
		for(var k in charts) {			
			if(charts.hasOwnProperty(k)) {
				var	j = {
					"chart": {
						showvalues: 0,
						PYAxisName: usage + ' (' + usageUnit + ')',
						SYAxisName: co2Usage + ' (kg)'
					},
					"categories": [ {"category":[]} ],
					"dataset": [
					    {
							seriesname: usage + " (" + usageUnit + ")",	
							color: barColor[0],
							data: []	
						},
						{
							seriesname: co2Usage + " (" + co2Unit + ")",
							color: lineColor[0],
							parentYAxis: "S",
							renderas: "Line",
							data: []	
						}
					]
				};
				var item = chartData[k];
				if(!item) {
					// XXX: 없는 데이터 예외처리
				}
				var len = item.length;
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
					j.dataset[1].data.push({
						value: obj["CO2SUM"],
						showValue: 0
					});
				}
				charts[k] = j;
			}			
		}
		return charts;
	};
	renderMeteringChartByManual.MIN = function(params) {};
	renderMeteringChartByManual.MAX = function(params) {
		if(!params) {
			return;
		}
		var transformed = renderMeteringChartByManual.transform(params);
		if(!transformed) {
			return;
		}
		var ret = {};
		for(var type in transformed) {
			if(transformed.hasOwnProperty(type)) {
				var labelStep = 0;
				var chartRenderTo = type + "_chart";
				var width = $("#meterStatChartArea").width();
				var adjustHalf = width/2-12;

				if(type === 'dayLimitList') {
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
		return ret;
	};
	
	var renderGridTab = function(params) {
		tabConfig.items = params.items;
		tabs = new TabPanel(tabConfig);
		tabs.render();
		return tabs;
	};
	
	var renderMenuTab = function(params) { 
		return {
			menuTab: $("#manual-metering-tabs").tabs({
				fx: 'fade',
				show: params.menuTabHandler.show
			}),
			gridNChart: $("#manual_metring_result").tabs({
				show: params.gridNChartHandler.show
			})
		};
	};
	
	var downloadMeteringExcel = function(params) {
		var message = {
			number: I18N['aimir.number'],
			contractNumber: I18N['aimir.contractNumber'],
			customername: I18N['aimir.customername'],
			meteringtime: I18N['aimir.meteringtime'],
			usage: I18N['aimir.usage'],
			previous: I18N['aimir.previous'],
			co2formula: I18N['aimir.co2formula'],
			mcuid2: I18N['aimir.mcuid2'],
			meterid2: I18N['aimir.meterid2'],
			strLocation: I18N['aimir.location'],
			detail: I18N['aimir.view.detail'],
			alert: I18N['aimir.alert'],
			selectContract: I18N['aimir.contract.selectContract'],
			msg09: I18N['aimir.firmware.msg09'],
			thisDayData: I18N["aimir.thisdaydata"],
			choiceContract4: I18N['aimir.alert.metering.choiceContract4'],
			filePath: I18N['aimir.report.fileDownloadDir']
		};
		$params = $.extend({}, params);
		$params = $.extend(message, $params);

		// 팝업 모듈 로드
		require(["framework/View/Popup"], function(Popup) {
			// 팝업을 열고, 해당 팝업에 서브밋한다.
			Popup.open('', 'excelpop' ); 
			require(["framework/Model/HttpService"], function(http) {
				http.dynamicFormSubmit({
					action: CONTEXT + "/gadget/mvm/excelManualMetering.do",
					params: $params,
					method: "POST",
					target: 'excelpop'
				});
			});
		});
	};
	

	var renderMeteringUpdate = function(param) {
	
		var meterId = param.meterId;
		var meteringDate = param.meteringdate;
		var formItem = [
		{
            xtype:'displayfield',
            fieldLabel: I18N["aimir.meterid"],
          	value : meterId,
          	name: 'meterId'
        },
        {
            xtype:'displayfield',
            fieldLabel: I18N["aimir.meteringdate"],
            value : meteringDate,
            name: 'meteringDate'
            
        },
		{
            xtype:'textfield',
            fieldLabel: I18N["aimir.thisdaydata"],
            name :  'thisdaydata'
        }];
		
		var meteringForm = Ext.apply({}, BASE.form.frame);
		meteringForm.id = 'meteringFormId',
		meteringForm.buttons[0].handler = param.handler.saveHandler;
		meteringForm.buttons[1].handler = param.handler.cancelHandler;
		
		var len = formItem.length;
		while(len--) {
			formItem[len] = Ext.apply(formItem[len], BASE.form[formItem[len].xtype]);
		};
		meteringForm.items = formItem;

		var window = Ext.apply({}, BASE.window);
		window.id = 'meteringFormWindowId';
		window.items = new Ext.FormPanel(meteringForm);

		 var sf = new Ext.Window(window);
	     sf.render('manual_metering_update_window');
	     sf.setTitle(
	    		I18N['aimir.thisdaydata'] + ' ' + I18N['aimir.thisdaydata']
	    	);
	
    	Ext.getCmp('meteringFormWindowId').show();

    	if(renderObjects) {
    		renderObjects[window.id] = sf;
    	}
    	
    	return sf;
	};
	var messageBox = function(val, title, el) {
		Alert.info(val, title, el);
	};
	
	var locationTreeGoGo = function(treeDivId, searchKeyId, locationId, prefix) {
		Tree.locationTreeGoGo(treeDivId, searchKeyId, locationId, prefix);
	};

	// XXX: 추후 스타일로 뺀다.
	var checkRequireMark = function() {		
		var $required = $("th").filter("[data-require='true']");
		$required.each(function() { 
			$(this).css({"position": "relative", "margin-right": "5px;"})
				.append("<span style='color:red; position: absolute; top: 0px;'>*</span>");			
		});	
	};

	var initializeUI = function() {
		$ELEMENTS = {
			localSelect: $(".local-select"), // 로컬(스태틱) 셀렉트 박스 집합 n
			addNewMeterForm: $("#addNewMeter"), // 새 수동미터 입력 폼 1
			writeManualMetering: $("#writeManualMetering"), // 검침값 입력 폼
			meterTypeSelectobox: $(".meter-type-select"), // 미터타입 셀렉트박스 집합 n
			singleRegMeterVendor: $("#singleRegMeterVendor"), // 벤더 셀렉트 박스 1
			singleRegMeterModel: $("#singleRegMeterModel"), // 모델 셀렉트 박스 1
			manualMeterSelectbox: $("#manualMeterSelectbox"), // 메뉴얼미터 셀렉트박스
			dateToggleArea: $("#datepicker-area") // date toggle area
		};
		
		return $ELEMENTS;
	};
	
	return {
		locationTreeGoGo: locationTreeGoGo,
		initializeUI: initializeUI,
		messageBox: messageBox,
		downloadMeteringExcel: downloadMeteringExcel,
		renderMeteringGrid: renderMeteringGrid,
		renderMeteringUpdate:renderMeteringUpdate,
		renderMeteringChartByManual: renderMeteringChartByManual,
		renderManualMeterListGrid: renderManualMeterListGrid,
		renderMenuTab: renderMenuTab,
		renderGridTab: renderGridTab,
		checkRequireMark: checkRequireMark
	};
});