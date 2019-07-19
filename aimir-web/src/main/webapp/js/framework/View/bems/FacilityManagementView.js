define([
	"framework/View/Control/Tree",
	"framework/View/Control/Chart",
	"framework/View/Control/Grid",
	"framework/View/Control/Alert",
	"framework/Util/ObjectUtils",
	"FChartStyle"
], function(Tree, Chart, Grid, Alert, Utils) {
	
	/**
	 * XXX: 현재 많은 부분, 특히 엔드디바이스 추가 변경 삭제 등의 폼을 전부
	 * ExtJs 기반으로 처리하고 있는데, 변경 및 가독이 힘드므로
	 * 정적 HTML로 처리하도록 변경해야 한다.
	 */
	
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE; 

	var renderObjects;
	
	// 트리 아이콘 셋
	var treeIcon = {
		branch: "/aimir-web/images/tree/ic_bldg_closed.gif",	
		leaf: "/aimir-web/images/tree/ic_bldg_opened.gif"	
	};
	
	
	// 페이징 문자열 포맷
	var toolbarPageFormat = '{0} - {1} of {2}';
	var pieChart ;
	function getData(_type) {
            type = String(_type);
            send();
        };
	// 요소 아이디 문자열 상수
	var ID = {
		locationTree: "location_tree",
		endDeviceChart: "end_device_chart",
		endDevicePieChart: "end_device_pie_chart",
		endDeviceChartFlash: "end_device_chart_flash",
		statusFormWindowId: "status_form",
		statusFormId: "status_form_panel",
		situationFormWindowId: "situation_form",
		situationFormId: "situation_form_panel"
	};


	// 스타일 상수
	var STYLE = {
		MIN: {
			endDeviceHeight: 160,
			locationHeight: 160
		},
		MAX: {
			endDeviceHeight: 200,
			locationHeight: 600,
			usedChartHeight: 220
		}
	};
	
	// 탭 공통 설정
	var tabConfig = {
		"MIN": {
			renderTo: 'facility_data',				
	        activeTab: 0,
	        plain: true,
	        defaults: {
	        	autoHeight: true
	        }
		},
		"MAX": {
			renderTo: 'facility_data',
	        activeTab: 0,
	        plain: true,
	        defaults: {
	        	autoHeight: true
	        }
		}
	};
	
	// 폼 윈도우 설정
	// XXX: ExtJS 기반으로 되어 있어 비효율적이므로, 폼을 HTML로 변경하면 삭제한다.
	var BASE = {
		window: {
			layout: 'anchor',
			plain: true,
			autoHeight: true,
	        width: 320,
			closeAction: 'hide',
			modal: true,
	        items: []	        
		},
		form: {
			frame: {
				id:	ID.statusFormId,
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
	
	// 단위 포맷터
	function unitFormat(val, unit) {
		var r = 0;
		if(val) {
			r = new Number(val).toFixed(2);
		}
		return r + ((unit) ? unit : ''); 
	}
	
	function unitEFormat(val, rowIndex, record) {
		return unitFormat(val);
	};
	function unitWGFormat(val, rowIndex, record) {
		return unitFormat(val);
	};
	function unitHFormat(val, rowIndex, record) {
		return unitFormat(val);
	};
	
	// 트리 렌더링 
	var renderLocationTree = function(params) {
		var locRoot = params.data;
		var locTree = Tree.render({
			root: locRoot,
			id: ID.locationTree,
			width: '100%',
			height: STYLE[SIZE].locationHeight,
			listeners: params.listeners
		}, treeIcon);
		var rootNode = locTree.getRootNode();

		if(params.callback && typeof params.callback === 'function') {
			params.callback.apply(this);
		}
		return locTree;
	};

	var renderEndDevicePieChart = function(data, callback) {
		var running = 0;
		var stop = 0;
		var unknown = 0;
		var list= data.endDeviceList;
		for(var i=0;i<list.length;i++){
				running += list[i].running;
				stop += list[i].stop;
				unknown += list[i].unknown;
		}
		var pieDivWidth  = $('#end_device_pie_chart').width();
		var fcChartDataXml = " <chart showPercentValues='1' showPercentInToolTip='0' "+
			"showZeroPies='0' "+
			"showLabels='1'"+
			"showValues='1'"+ 
			"manageLabelOverflow='1'"+  
			fChartStyle_Common+
			fChartStyle_Font+
			"chartBottomMargin='5'"+
			"showBorder='0' "+
			"showPlotBorder='0' "+
			"borderColor='E3E3E3' "+
			"pieRadius='70' "+
			"use3DLighting='1' "+
			"showLegend='1' "+
			"manageLabelOverflow='1' "+
    		"numberSuffix='  ' "+
			"legendPosition='BOTTOM'"+
			"legendBorderColor='ffffff' "+
			"legendBorderThickness='0' "+
			"legendBgAlpha='100' "+
			"legendBgColor='ffffff' "+
			"legendShadow='0' >"+
			"<set label='"+I18N['aimir.bems.facilityMgmt.operation']+"'value='"+running+
			"' color='00A920'/>"+
			"<set label='"+I18N['aimir.stop']+"' value='"+stop+
			"' color='FFDC01'/>"+
			"<set label='"+I18N['aimir.unknown']+"' value='"+unknown+
			"' color='FF6600'/>"+
			"</chart> ";
		
		   pieChart = Chart.renderByXML(
            "Pie3D",
            {
                renderId: "end_device_pie_chart",
                chartId: "pieChart",
                width: pieDivWidth,
                height: "120"
            },
            fcChartDataXml
        );
        if(Utils.isFunction(callback)) callback(pieChart);
        return pieChart;  
	};

	var renderEndDeviceChart = function(data) {
		data = renderEndDeviceChart.transformData(data.endDeviceList);
		var conf = {
			data: data,
			id: ID.endDeviceChart,
			width: '100%',
			height: STYLE[SIZE].endDeviceHeight,
			chartId: ID.endDeviceChartFlash,
			yaxisname: I18N['aimir.bems.facilityMgmt.count'],					
			dataFormat: "json"
		};
		if(SIZE === "MAX") {
			conf.data.chart.caption = I18N['aimir.facilityMgmt.operating.presentCondition'];
		}
		else {
			conf.data.chart.caption = "";
		}
		return Chart.render("StackedColumn3D", conf);
	};
	
	renderEndDeviceChart.transformData = function(data) {
		
		var serieses = [
		    {
		    	name: I18N['aimir.bems.facilityMgmt.operation'],
		    	datakey: "running",
		    	color: "0x" + fChartColor_Step3[0]
		    },
		    {
		    	name: I18N['aimir.stop'],
		    	datakey: "stop",
		    	color: "0x" + fChartColor_Step3[1]
		    },
		    {
		    	name: I18N['aimir.unknown'],
		    	datakey: "unknown",
		    	color: "0x" + fChartColor_Step3[2]
		    }
		];
		
		var usageTip = function(obj) {
			var unitStr = I18N['aimir.bems.facilityMgmt.count'];
			var tip = obj.facilityType + "{br}";
			tip += serieses[0].name + ": " + obj.running + " " + unitStr + "{br}";
			tip += serieses[1].name + ": " + obj.stop + " " + unitStr + "{br}";
			tip += serieses[2].name + ": " + obj.unknown + " " + unitStr;
			return tip;
		};
		
		var len = data.length;
		var ret = {};
		var	j = {
			"chart": {},
			"categories": [],
			"dataset": []
		};
		
		if(!data || data.length < 1) {
			return ret;
		}		
		
		var scale = 2, category;
	    j.categories.push({
	    	"category": []
	    });
		for (var i=0; i < len; i++) {						
			var obj = data[i];
			category = {
				label: obj.facilityType
			};
			if((i%scale) !== 0) {
				category.showLabel = 1;
			}
			j.categories[0].category.push(category);
		}
		
		for (var i = 0; i < len; i++) {						
			var set = data[i];
			for(var z=0, slen = serieses.length; z < slen; z++) {
				if(!j.dataset[z]) {
					j.dataset.push({
						seriesName: serieses[z].name,
						color: serieses[z].color,
						data: []
					});
				}
				var v = set[serieses[z].datakey];
				var dataRow = {};
				if(v) {
					dataRow = {
				    	value: v,
				    	tooltext: usageTip(set),
				    	showValue: 0
				    };
				}
				j.dataset[z].data.push(dataRow);
			}
		}
		return j;
	};
	
	var downloadFacilityExcel = function(params){
		var Type = params.gridType
		//console.log(Type.id);
		if(Type.id === "max_situation_grid"){ 
			downloadSituationExcel(params);
		}else if(Type.id === "max_status_grid"){
			downloadStatusExcel(params); 
			// console.log(Type.id);
		}else if(Type.id === "max_history_grid"){
			downloadHistoryExcel(params);
			// console.log(Type.id);
		}

	};

	var downloadSituationExcel = function(params){
		
		var message ={
			location: I18N["aimir.bems.facilityMgmt.location"],
			type: I18N["aimir.bems.facilityMgmt.kind"],
			friendlyName: I18N["aimir.name"],
			status: I18N["aimir.state"],
			dayEM: I18N["aimir.facilityMgmt.energy"] + "[kWh]",
			dayWM: I18N["aimir.facilityMgmt.water"] + "[㎥]",
			dayGM: I18N["aimir.facilityMgmt.gas"] + "[㎥]", 
			dayHM: I18N["aimir.facilityMgmt.heat"] + "[kCal]",
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
					action: CONTEXT + "/gadget/system/bems/excelFacilitySituation.do",
					params: $params,
					method: "POST",
					target: 'excelpop'
				});
			});
		});  
	};

	var downloadStatusExcel= function(params){

		var message ={
			number: I18N['aimir.number'],
			location: I18N["aimir.bems.facilityMgmt.location"],
			type: I18N["aimir.bems.facilityMgmt.kind"],
			manufacturerer: I18N["aimir.vendor"],
			model: I18N["aimir.model"],
			friendlyName:I18N["aimir.bems.facilityMgmt.name"],
			installDate:I18N["aimir.install.date"],
			powerConsumption:I18N["aimir.facilityMgmt.electricConsumption"],
			modemType:I18N["aimir.mcu.system.connected"],
			modemSerial:I18N["aimir.modemid.connected"],
			filePath: I18N['aimir.report.fileDownloadDir']
		} 
		$params = $.extend({}, params); 
		$params = $.extend(message, $params);
		//console.log($params);
		// 팝업 모듈 로드
		require(["framework/View/Popup"], function(Popup) {
			// 팝업을 열고, 해당 팝업에 서브밋한다.
			Popup.open('', 'excelpop' ); 
			require(["framework/Model/HttpService"], function(http) { 
				http.dynamicFormSubmit({
					action: CONTEXT + "/gadget/system/bems/excelFacilityStatus.do",
					params: $params,
					method: "POST",
					target: 'excelpop'
				});
			});
		});  
	};

	var downloadHistoryExcel =  function(params){
		//console.log("historyexcel");
		var message ={
			location: I18N["aimir.bems.facilityMgmt.location"],
			categoryCode: I18N["aimir.name"],
			friendlyName: I18N["aimir.bems.facilityMgmt.name"],
			preStatusCode: I18N["aimir.beforeChange"],
			statusCode: I18N["aimir.afterChange"],
			writeDatetime: I18N["aimir.bems.facilityMgmt.changeDate"],
			filePath: I18N['aimir.report.fileDownloadDir']
		}
		
		$params = $.extend({}, params); 
		$params = $.extend(message, $params);
		//console.log($params);
		// 팝업 모듈 로드
		require(["framework/View/Popup"], function(Popup) {
			// 팝업을 열고, 해당 팝업에 서브밋한다.
			Popup.open('', 'excelpop' ); 
			require(["framework/Model/HttpService"], function(http) { 
				http.dynamicFormSubmit({
					action: CONTEXT + "/gadget/system/bems/excelFacilityHistory.do",
					params: $params,
					method: "POST",
					target: 'excelpop' 
				});
			}); 
		});  
	};

	var renderStatusGrid = function(params) {
		return renderStatusGrid[SIZE](params);
	};
	
	renderStatusGrid.MIN = function(params) {
		var conf = {
			id: "min_status_grid",
			title: I18N["aimir.facilityMgmt.situation"],
			store: params.store,
	    	selModel: new Ext.grid.RowSelectionModel({
	    		singleSelect: true,
	    		listeners: {
	    			rowselect: params.rowselectHandler
	    		}
	    	}),
			columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	width: 60,
	    	    	dataIndex: "location"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.kind"],
	    	    	width: 60,
	    	    	dataIndex: "type"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.name"],
	    	    	width: 90,
	    	    	dataIndex: "friendlyName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.state"],
	    	    	width: 50,
	    	    	dataIndex: "status"
	    	    }
	    	],
	    	bbar: new Ext.PagingToolbar({
				pageSize: 3,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store
	        })
		};
		return Grid.render(conf);
	};
	
	renderStatusGrid.MAX = function(params) {
		var conf = {
			id: "max_status_grid",
			title: I18N["aimir.facilityMgmt.situation"],
			store: params.store,
			selModel: new Ext.grid.RowSelectionModel({
	    		singleSelect: true,
	    		listeners: {
	    			rowselect: params.rowselectHandler
	    		}
	    	}),
			columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	width: 60,
	    	    	dataIndex: "location"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.kind"],
	    	    	width: 60,
	    	    	dataIndex: "type"
	    	    },
	    	    {
	    	    	header: I18N["aimir.vendor"],
	    	    	width: 70,
	    	    	dataIndex: "manufacturerer"
	    	    },
	    	    {
	    	    	header: I18N["aimir.model"],
	    	    	width: 70,
	    	    	dataIndex: "model"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.name"],
	    	    	width: 80,
	    	    	dataIndex: "friendlyName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.install.date"],
	    	    	width: 100,
	    	    	dataIndex: "installDate"
	    	    },
	    	    {
	    	    	header: I18N["aimir.facilityMgmt.electricConsumption"],
	    	    	width: 70,
	    	    	align:'right',
	    	    	dataIndex: "powerConsumption"
	    	    },
	    	    {
	    	    	header: I18N["aimir.mcu.system.connected"],
	    	    	width: 70,
	    	    	dataIndex: "modemType"
	    	    },
	    	    {
	    	    	header: I18N["aimir.modemid.connected"],
	    	    	width: 70,
	    	    	dataIndex: "modemSerial"
	    	    },
	    	    {
	                xtype: 'actioncolumn',
	                width: 40,
	                align:'center',
	                items: [{
	                    icon   : GLOBAL_CONTEXT.CONTEXT + '/js/extjs/examples/shared/icons/fam/accept.png',
	                    tooltip: I18N['aimir.bems.facilityMgmt.update'],
	                    handler: params.updateEndDeviceWindow
	                }, {
	                    icon   : GLOBAL_CONTEXT.CONTEXT + '/js/extjs/examples/shared/icons/fam/delete.gif',
	                    tooltip: I18N['aimir.bemsfacilityMgmt.delete'],
	                    handler: params.deleteEndDevice
	                }]
	            }
	    	],
	    	bbar: new Ext.PagingToolbar({
				pageSize: 10,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store,
	            items: [{
	                tooltip: I18N['aimir.bems.facilityMgmt.add'],
	                cls: 'x-btn-icon add-device',
	                handler : params.addNewEndDeviceWindow
	            }]
	        })
		};
		return Grid.render(conf);
	};
	
	var renderSituationGrid = function(params) {
		return renderSituationGrid[SIZE](params);
	};
	
	renderSituationGrid.MIN = function(params) {
		var conf = {
			id: "min_situation_grid",
			title: I18N["aimir.facilityMgmt.operating.status"],
			store: params.store,			
			columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	dataIndex: "location"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.kind"],
	    	    	dataIndex: "type"
	    	    },
	    	    {
	    	    	header: I18N["aimir.model"],
	    	    	dataIndex: "modemType"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	dataIndex: "friendlyName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.install.date"],
	    	    	dataIndex: "installDate"
	    	    }
	    	],
	    	bbar: new Ext.PagingToolbar({
				pageSize: 3,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store
	        })
		};
		return Grid.render(conf);
	};
	
	renderSituationGrid.MAX = function(params) {
		var conf = {
			id: "max_situation_grid",
			title: I18N["aimir.facilityMgmt.operating.status"],
			selModel: new Ext.grid.RowSelectionModel({
	    		singleSelect: true,
	    		listeners: {
	    			rowselect: params.rowselectHandler
	    		}
	    	}),
			store: params.store,
			columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	dataIndex: "location",
	    	    	width: 60
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.kind"],
	    	    	dataIndex: "type",
	    	    	width: 60
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	dataIndex: "friendlyName",
	    	    	width: 80
	    	    },
	    	    {
	    	    	header: I18N["aimir.state"],
	    	    	dataIndex: "status",
	    	    	width: 50
	    	    },
	    	    {
	    	    	header: "<div style='text-align: center'>" + 
	    	    		I18N["aimir.facilityMgmt.energy"] + "[kWh]" + "</div>",
	    	    	dataIndex: "dayEM",
	    	    	align:'right',
	    	    	renderer: unitEFormat,
	    	    	width: 90
	    	    },
	    	    {
	    	    	header: "<div style='text-align: center'>" + 
    	    			I18N["aimir.facilityMgmt.water"] + "[㎥]" + "</div>",
	    	    	dataIndex: "dayWM",
	    	    	align:'right',
	    	    	renderer: unitWGFormat,
	    	    	width: 90
	    	    },
	    	    {
	    	    	header: "<div style='text-align: center'>" + 
	    				I18N["aimir.facilityMgmt.gas"] + "[㎥]" + "</div>",
	    	    	dataIndex: "dayGM",
	    	    	align:'right',
	    	    	renderer: unitWGFormat,
	    	    	width: 90
	    	    },
	    	    {
	    	    	header: "<div style='text-align: center'>" + 
	    				I18N["aimir.facilityMgmt.heat"] + "[kCal]" + "</div>",
	    	    	dataIndex: "dayHM",
	    	    	align:'right', 
	    	    	renderer: unitHFormat,
	    	    	width: 90
	    	    },
	    	    {
	                xtype: 'actioncolumn',
	                width: 25,
	                align:'center',
	                items: [{
	                    icon   : GLOBAL_CONTEXT.CONTEXT + '/js/extjs/examples/shared/icons/fam/accept.png',
	                    tooltip: I18N['aimir.bems.facilityMgmt.update'],
	                    handler: params.updateEndDeviceStatusWindow
	                }]
	            }
	    	],
	    	bbar: new Ext.PagingToolbar({
				pageSize: 10,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store
	        })
		};
		return Grid.render(conf);
	};
	
	var renderHistoryGrid = function(params) {
		return renderHistoryGrid[SIZE](params);
	};
	
	renderHistoryGrid.MIN = function(params) {
		var conf = {
			id: "min_history_grid",
			title: I18N["aimir.facilityMgmt.operating.status.history"],
			store: params.store,
		    columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	dataIndex: "locationName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	dataIndex: "categoryCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.name"],
	    	    	dataIndex: "friendlyName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.beforeChange"],
	    	    	dataIndex: "preStatusCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.afterChange"],
	    	    	dataIndex: "statusCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.changeDate"],
	    	    	dataIndex: "writeDatetime"
	    	    }
	    	],
	    	bbar: new Ext.PagingToolbar({
				pageSize: 3,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store
	        })					
		};
		return Grid.render(conf);
	};
	
	renderHistoryGrid.MAX = function(params) {
		var conf = {
			id: "max_history_grid",
			title: I18N["aimir.facilityMgmt.operating.status.history"],
			store: params.store,
			columns: [
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.location"],
	    	    	dataIndex: "locationName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.name"],
	    	    	dataIndex: "categoryCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.name"],
	    	    	dataIndex: "friendlyName"
	    	    },
	    	    {
	    	    	header: I18N["aimir.beforeChange"],
	    	    	dataIndex: "preStatusCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.afterChange"],
	    	    	dataIndex: "statusCode"
	    	    },
	    	    {
	    	    	header: I18N["aimir.bems.facilityMgmt.changeDate"],
	    	    	dataIndex: "writeDatetime"
	    	    }
	    	],					
	    	bbar: new Ext.PagingToolbar({
				pageSize: 10,
	            displayInfo: true,
	            displayMsg: toolbarPageFormat,
	            store: params.store
	        })					
		};
		return Grid.render(conf);
	};
	
	var termTip = function(obj) {
		var ret = '';
		var used = I18N['aimir.usage'];
		var oldStr = I18N['aimir.locationUsage.lastTerm'];
		var currentStr = I18N['aimir.locationUsage.now'];
		var facilityType = obj.facilityType;
		var unitStr = '';
		switch (facilityType) {
			case I18N['aimir.energymeter'] :
				unitStr = "kWh";
				break;
			case I18N['aimir.gas'] :
				unitStr = "㎥";
				break;
			case I18N['aimir.water'] :
				unitStr = "㎥";
				break;
			case I18N['aimir.heatmeter'] :
				unitStr = "kCal";
				break;
			default :
				unitStr = "EA";
		}
		ret += facilityType + "{br}";
		ret += oldStr + ":" + used + ": " + unitFormat(obj.old) + " " + unitStr + "{br}";
	    ret += currentStr + ":" + used + ": " + unitFormat(obj.current) + " " + unitStr + "{br}";
	    return ret;
	};
	
	var renderUsedChart = function(params) {
		var transform = transformTermUsedData(params.data);
		var usedChart = {};
		for(var type in transform) {
			try {
				if(transform.hasOwnProperty(type)) {
					usedChart[type] = renderUsedChart.render(type, transform[type]);
				}
			}
			catch(renderChartError) {
				// XXX: 렌더링 차트 에러 처리.
			}
		}
		return usedChart;
	};
	
	renderUsedChart.render = function(type, data) {
		var conf = {
			data: data,
			id: type,
			width: '100%',
			height: STYLE[SIZE].usedChartHeight,
			chartId: type + "_flash",
			dataFormat: "json",
			caption: data.caption 
		};
		// delete data.caption;
		return Chart.render("MSColumn3D", conf);
	};
	
	var transformTermUsedData = function(data) {
		var yAxisLabel = I18N['aimir.usage']+"[kWh]";
		var caption = {
			"dailyChart": I18N['aimir.facilityMgmt.dailyUsage'],
    		"weeklyChart": I18N['aimir.facilityMgmt.weeklyUsage'],
    		"monthlyChart": I18N['aimir.facilityMgmt.monthlyUsage'],
    		"yearlyChart": I18N['aimir.facilityMgmt.yearlyUsage']
		};
		var result = {};
		var serieses = [
			{
		    	name: {
		    		"dailyChart": I18N['aimir.date.yesterday'],
		    		"weeklyChart": I18N['aimir.date.lastweek'],
		    		"monthlyChart": I18N['aimir.facilityMgmt.beforeMonth'],
		    		"yearlyChart": I18N['aimir.lastyear']
		    	},
		    	datakey: "old",
		    	color: "0x" + fChartColor_Elec[1]
		    },
		    {
		    	name: {
		    		"dailyChart": I18N['aimir.today'],
		    		"weeklyChart": I18N['aimir.date.thisweek'],
		    		"monthlyChart": I18N['aimir.thismonth'],
		    		"yearlyChart": I18N['aimir.date.thisYear']
		    	},
		    	datakey: "current",
		    	color: "0x" + fChartColor_Elec[0]
		    }
		];

		for(var item in data) {			
			if(data.hasOwnProperty(item)) {
				var d = data[item];
				var	j = {
					"chart": {
						yaxisname: yAxisLabel,
						caption : []

					},
					"categories": [ {"category":[]} ],
					"dataset": []
				};
				
				var len = d.length;
				
				for (var i=0; i < len; i++) {						
					var obj = d[i];
					category = {
						label: obj.facilityType,
						showLabel: 1
					};
					j.categories[0].category.push(category);
				}
				
				for (var i = 0; i < len; i++) {						
					var set = d[i];
					for(var z=0, slen = serieses.length; z < slen; z++) {
						if(!j.dataset[z]) {
							j.dataset.push({
								seriesName: serieses[z].name[item],
								color: serieses[z].color,
								data: []
							});
						}
						var v = set[serieses[z].datakey];
						var dataRow = {};
						if(v) {
							dataRow = {
						    	value: unitFormat(set[serieses[z].datakey]),
						    	tooltext: termTip(set),
						    	showValue: 0
						    };
						}
						j.dataset[z].data.push(dataRow);
					}
				}
				result[item] = j;
				result[item].caption = caption[item];
			}
		}
		return result;
	};
	
	var tabConfig = {
		"MIN": {
			renderTo: 'facility_data',				
	        activeTab: 0,
	        plain: true,
	        defaults: {
	        	autoHeight: true
	        }
		},
		"MAX": {
			renderTo: 'facility_data',
	        activeTab: 0,
	        plain: true,
	        defaults: {
	        	autoHeight: true
	        }
		}
	};
	
	var renderTab = function(params) { 
		var conf = tabConfig[SIZE];
		conf.items = params.items;
		conf.listeners = params.listeners || {};
		tabs = new Ext.TabPanel(conf);
		tabs.render();
		return tabs;
	};
	
	var renderStatusForm = function(params) {
		var formItem = [{
            xtype:'combo',
            hiddenName: 'id',
            store: params.stateStore,
            fieldLabel: I18N["aimir.state"]
        }];
		
		var situationForm = Ext.apply({}, BASE.form.frame);
		situationForm.id = ID.situationFormId,
		situationForm.buttons[0].handler = params.handler.saveHandler;
		situationForm.buttons[1].handler = params.handler.cancelHandler;
		
		var len = formItem.length;
		while(len--) {
			formItem[len] = Ext.apply(formItem[len], BASE.form[formItem[len].xtype]);
		};
		situationForm.items = formItem;
		
		var window = Ext.apply({}, BASE.window);
		window.id = ID.situationFormWindowId;
		window.items = new Ext.FormPanel(situationForm);
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
	
	var renderEndDeviceForm = function(params) {
	
		var formItem = [{
            xtype:'combo',
            hiddenName: 'locationId',
            store: params.locationStore,
            fieldLabel: I18N["aimir.bems.facilityMgmt.location"]
        }, {
        	xtype:'combo',
            hiddenName: 'typeId',
            store: params.kindStore,
            fieldLabel: I18N["aimir.bems.facilityMgmt.kind"],
        }, {
            xtype:'textfield',
            fieldLabel: I18N["aimir.equipvendor"],
            name: 'manufacturerer'
        }, {
            xtype:'textfield',
            fieldLabel: I18N["aimir.model"],
            name: 'model'
        }, {
            xtype:'textfield',
            fieldLabel: I18N["aimir.bems.facilityMgmt.name"],
            name: 'friendlyName'
        }, {
            xtype:'datefield',
            fieldLabel: I18N["aimir.install.date"],
            name: 'installDate'
        }, {
            xtype:'datefield',
            fieldLabel: I18N["aimir.facilityMgmt.manufactureDate"],
            name: 'manufactureDate'
        }, {
            xtype:'textfield',
            fieldLabel: I18N["aimir.facilityMgmt.electricConsumption"],                    
            name: 'powerConsumption'
        }, {
        	xtype:'combo',
            hiddenName: 'modemTypeId',
            store: params.modemTypeStore,
            fieldLabel: I18N["aimir.bems.facilityMgmt.kind"]                 
        }, {
            xtype:'textfield',
            fieldLabel: I18N["aimir.modemid.connected"],
            name: 'modemSerial'
        }];
		
		var statusForm = Ext.apply({}, BASE.form.frame);
		statusForm.id = ID.statusFormId,
		
		statusForm.buttons[0].handler = params.handler.saveHandler;
		statusForm.buttons[1].handler = params.handler.cancelHandler;
		
		var len = formItem.length;
		while(len--) {
			formItem[len] = Ext.apply(formItem[len], BASE.form[formItem[len].xtype]);
		};
		statusForm.items = formItem;
		var window = Ext.apply({}, BASE.window);
		window.id = ID.statusFormWindowId;
		window.items = new Ext.FormPanel(statusForm);
		
		var sf = new Ext.Window(window);
    	sf.render('end_device_status_form');
    	sf.setTitle(I18N['aimir.bems.facilityMgmt.add']);
    	
    	if(renderObjects) {
    		renderObjects[window.id] = sf;
    	}
    	
    	return sf;
	};
	
	var messageBox = function(val, title, el) {
		Alert.info(val, title, el);
	};
	
	return {
		renderLocationTree: renderLocationTree,
		renderEndDevicePieChart: renderEndDevicePieChart,
		renderEndDeviceChart: renderEndDeviceChart,
		renderStatusGrid: renderStatusGrid,
		renderSituationGrid: renderSituationGrid,
		renderHistoryGrid: renderHistoryGrid,
		renderUsedChart: renderUsedChart,
		renderEndDeviceForm: renderEndDeviceForm,
		renderStatusForm: renderStatusForm,
		renderTab: renderTab,
		messageBox: messageBox,
		downloadFacilityExcel:downloadFacilityExcel,
		downloadSituationExcel:downloadSituationExcel,
		downloadStatusExcel:downloadStatusExcel,
		downloadHistoryExcel:downloadHistoryExcel
	};
	
});