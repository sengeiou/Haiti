define([    
	"framework/Config/CommonConstants",
    "framework/Model/Operator",
    "framework/Model/Location",
    "framework/Model/EndDevice",
    "framework/View/ErrorView",
	"framework/View/Control/Alert",
    "framework/View/bems/FacilityManagementView",
    "framework/Util/ObjectUtils"
], function(CONST, Operator, Location, EndDevice, Error, Alert, View, Utils) {

	// Optimize scope searching
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE;
	
	// User Info
	var user = undefined;
	
	// Ext Components
	var stForm = undefined;
	var sForm = undefined;
	var tabs = undefined;
	var locTree = undefined;
	var endDeviceChart = undefined;
	var endDevicePieChart = undefined;
	var usedChart = undefined;
	var grids = {
		situation: undefined,
		status: undefined,
		history: undefined
	};
	
	// Data Stores
	var comboStores = {
		locationStore: undefined,
	    kindStore: undefined,
	    stateStore: undefined,
	    modemTypeStore: undefined	
	};
	var gridStores = {
		situation: undefined,
		status: undefined,
		history: undefined
	};
	
	// metaData
	var metaData = {};
	
	// etc running variables
	var rootId = 1;
	var selectMode = "location";
	
	// control variables
	var params = {		
		locationId: "-1",	
		endDeviceId: "-1",
		typeId : "-1",
		modemTypeId : "-1"	
	};

	// page limits
	var pageLimit = {
		MIN: 3,
		MAX: 10
	};
    
    // rendering functions
    var render = {
		/**
			위치 트리 그리기
			@param callback 그리기 완료 후 실행할 함수
		*/
    	locationTree: function(callback) {
			if(!user) {
				user = Operator.getUserInfo();
			}
			Location.getLocationInfo(
				{
					supplierId: user.supplierId
				},
				function(res) {
					if(!res || !Utils.isArray(res.locationlist)) return;
					// locRoot = res.locationlist[0];
					// rootId = locRoot.id;
					rootId = res.locationlist[0].id;
					locTree = View.renderLocationTree({
						data: res.locationlist,
						listeners: {
				            click: function(node, checked) {
				            	if(selectMode === 'location') {
					            	params["locationId"] = node.id;
					            	events.hideRenderUsedChart();
					            	events.refreshEndDevicePieChart();
					            	events.refreshEndDeviceChart();
					            	events.refreashGrid();
				            	}
				            }
				        },
				        callback: callback 
					});
				},
				function(res, req) {
					Error.showErrorBox(res);
				}
			);
		},
		// 그리드 탭 그리기
		gridTab: function() {
			tabs = View.renderTab({
				items: [
				    render.situationGrid(), 
		            render.statusGrid(), 
		            render.statusHistoryGrid()
		        ]     
			});
		},
		endDevicePieChart: function(){
			EndDevice.getEndDevice(
					params,
					function(res, req) {
						var data = Ext.util.JSON.decode(res.responseText);
						endDevicePieChart = View.renderEndDevicePieChart(data);
					},
					function(res, req) {
						Error.showErrorBox(res);
					}
				);
		},
		// 엔드디바이스 차트 그리기
		endDeviceChart: function() {
			EndDevice.getEndDevice(
				params,
				function(res, req) {
					var data = Ext.util.JSON.decode(res.responseText);
					endDeviceChart = View.renderEndDeviceChart(data);
				},
				function(res, req) {
					Error.showErrorBox(res);
				}
			);
		},
		// 엔드디바이스 수정, 추가 및 상태변경 폼 그리기
		// XXX: 순수 ExtJS 기반으로 되어있어 커스터마이징이 곤란하므로, 
		// 추후 정적 HTML로 그리는걸로 변경해야한다.
		endDeviceForm: function() {
			EndDevice.getMetaData(
				{supplierId: user.supplierId }, 
				function(res, req) {				
					metaData = Ext.util.JSON.decode(res.responseText);
					var comboData = {
					    "locationStore": metaData.locationList,
					    "kindStore": metaData.kindList,
					    "stateStore": metaData.codeList,
					    "modemTypeStore": metaData.mcodeList
					}; 
					var record = Ext.data.Record.create(['id','name']);
					for(var l in comboData) {
						if(comboData.hasOwnProperty(l)) {
							var d = comboData[l];
							comboStores[l] = new Ext.data.ArrayStore({
								fields: [ 'id', 'name' ],
								data: []
							}); 
							for(var i=0,len=d.length;i<len;i++) {
								comboStores[l].add(new record({
									id: d[i].id, name: d[i].name
								}));
							}
						}
					}
					sForm = View.renderEndDeviceForm({
						locationStore: comboStores["locationStore"],
						kindStore: comboStores["kindStore"],
						modemTypeStore: comboStores["modemTypeStore"],
						handler: {
							saveHandler: function() {
			                	if(sForm.title === I18N['aimir.bems.facilityMgmt.add']) {
			                		events.addNewEndDevice();
			                	}
			                	else if(sForm.title === I18N['aimir.bems.facilityMgmt.update']) {
			                		events.updateEndDevice();
			                	}
			                },
			                cancelHandler: function() {
			                	Ext.getCmp(sForm.id).hide();
			                }		                
						}
					});
					stForm = View.renderStatusForm({
						stateStore: comboStores["stateStore"],
						handler: {
							saveHandler: events.updateEndDeviceStatus,
			                cancelHandler: function() {
			                	Ext.getCmp(stForm.id).hide();
			                }		                
						}
					});
				}, 
				function(res, req) {
					Error.showErrorBox(res);
				}
			);
		},
		// 사용량 차트 그리기
		usedChart: function(endDeviceId) {
			if(!endDeviceId) return;
			EndDevice.getEndDeviceCompareData(
				{ endDeviceId: endDeviceId },
				function(res, req) {
					var jsonData = Ext.util.JSON.decode(res.responseText);
					if(jsonData.result !== 'fail') {
						events.showRenderUsedChart();
						usedChart = View.renderUsedChart({
							data: Ext.util.JSON.decode(res.responseText)
						});
					}
					else {
						// XXX: 엔드디바이스 아이디가 잘못 입력된 경우나 존재하지 않을때.
						// 혹은 서버오류
						Error.showErrorBox("cannot load chart");
						events.hideRenderUsedChart();
					}
				},
				function(res, req) {
					events.hideRenderUsedChart();
					Error.showErrorBox(res);
				}
			);
		},
		situationGrid: function() {
			
			gridStores["situation"] = EndDevice.getEndDevicesMeteringStore({
				pageLimit: pageLimit[GLOBAL_CONTEXT.SIZE],

				listeners: {
	            	beforeload: function(store, option) {
	            		var updateParams = {
				    		locationId: (params.locationId) ? params.locationId : rootId,
				    		endDeviceId: params.endDeviceId,
				    	

				    	};
				    	Ext.apply(option.params, updateParams);
	            	}
	            }
			});
			var grid = View.renderSituationGrid({
				store: gridStores["situation"],
				rowselectHandler: function(selModel, rowIndex, record) {
					events.showUsedChardByEndDeviceId(record);
				},
				updateEndDeviceStatusWindow: render.updateEndDeviceStatusWindow
			});
			grids["situation"] = grid;
			return grid;
		},
		statusGrid: function() {
			gridStores["status"] = EndDevice.getEndDevicesMeteringStore({
				pageLimit: pageLimit[GLOBAL_CONTEXT.SIZE],
				listeners: {
	            	beforeload: function(store, option) {
	            		var updateParams = {
				    		locationId: (params.locationId) ? params.locationId : rootId,
				    		endDeviceId: params.endDeviceId,
				    		typeId: params.typeId,
				    		modemTypeId: params.modemTypeId
				    	};
				    	Ext.apply(option.params, updateParams);
	            	}
	            }
			});
			var grid = View.renderStatusGrid({
				store: gridStores["status"],
				rowselectHandler: function(selModel, rowIndex, record) {
					events.showUsedChardByEndDeviceId(record);
				},
				updateEndDeviceWindow: render.updateEndDeviceWindow,
				deleteEndDevice: events.deleteEndDevice,
				addNewEndDeviceWindow: render.addNewEndDeviceWindow
			});
			grids["status"] = grid;
			return grid;
		},
		statusHistoryGrid: function() {
			gridStores["history"] = EndDevice.getEndDeviceLogsStore({
				pageLimit: pageLimit[GLOBAL_CONTEXT.SIZE],
				listeners: {
	            	beforeload: function(store, option) {
	            		var updateParams = {
				    		locationId: (params.locationId) ? params.locationId : rootId,
				    		endDeviceId: params.endDeviceId
				    	};
				    	Ext.apply(option.params, updateParams);
	            	}
	            }
			});
			var grid = View.renderHistoryGrid({
				store: gridStores["history"]
			});
			grids["history"] = grid;
			return grid;
		},
		
	    updateEndDeviceWindow: function(grid, rowIndex, colIndex) {
	    	var aRow = grid.getStore().getAt(rowIndex);
	    	Ext.getCmp('status_form_panel').getForm().loadRecord(aRow);
	    	sForm.setTitle(I18N['aimir.bems.facilityMgmt.update']);
	    	 
			sForm.show(grid);
			sForm.data = aRow.json;
	    },
	    
	    addNewEndDeviceWindow: function(grid, event) {
	    	Ext.getCmp('status_form_panel').getForm().reset();
	    	sForm.setTitle(I18N['aimir.bems.facilityMgmt.add']);
			sForm.show(grid);
	    },
	    updateEndDeviceStatusWindow: function(grid, rowIdx, cellIdx, handlerElement, event) {
			var aRow = grid.getStore().getAt(rowIdx);
			stForm.show(endDevicePieChart);
			stForm.show(endDeviceChart);
			stForm.data = aRow.json;
	    }
    };

    /**
		이벤트 셋.
	*/
    var events = {
		refreashGrid: function(gridId) {
			var updateParams = {
	    		locationId: (params.locationId) ? params.locationId : rootId,
	    		endDeviceId: params.endDeviceId
	    	};
			if(!gridId) {
				var g = [ "situation", "status", "history" ];
				var len = g.length;
				while(len--) {
					gridStores[g[len]].reload({params: updateParams});
				}
			}
			else {
				gridStores[gridId].reload({params: updateParams});
			}
		},
		refreshEndDeviceChart: function() {
			render.endDeviceChart();
		},		
		refreshEndDevicePieChart: function() {
			render.endDevicePieChart();
		},	
		hideRenderUsedChart: function() {
			$('#stat_chart_grid div.used_chart').empty();
			Ext.select(".used_chart").hide();
		},		
		showRenderUsedChart: function() {
			Ext.select(".used_chart").show();
		},
		showUsedChardByEndDeviceId: function(record) {
			if(GLOBAL_CONTEXT.SIZE === 'MIN') return;
			var endDeviceId = record.id;
			render.usedChart(endDeviceId);
		},
		deleteEndDevice: function(grid, rowIndex, colIndex) {
	    	var aRow = grid.getStore().getAt(rowIndex);
	    	Ext.MessageBox.confirm(
				I18N['aimir.bemsfacilityMgmt.delete'], 
				I18N['aimir.msg.deleteconfirm'], 
	            function(r) {
					if(r === 'yes') {
						events.confirmDeleteEndDevice({endDeviceId: aRow.id});
					}
				}
			);
	    },	    
	    confirmDeleteEndDevice: function(params) {
	    	EndDevice.deleteEndDevice(
				params,
				function(res, req) {
					var j = Ext.util.JSON.decode(res.responseText);	
					if(j.deleteResult === 'success') {
						View.messageBox(
							I18N['aimir.msg.deletesuccess'], 
							I18N['aimir.bemsfacilityMgmt.delete'], 
							$('#singleRegMeterMdsId')[0]
						);					
						events.refreashGrid('status');
					}
					else {
						Error.showErrorBox(res);
					}
				},
				function(res, req) {
					Error.showErrorBox(res);
				}
			);
	    },
	    updateEndDeviceStatus: function() {
	    	var endDeviceId = stForm.data.endDeviceId;
	    	var f = Ext.getCmp('situation_form_panel').getForm().findField('id');
	    	if(endDeviceId === undefined) return;
	    	var params = {
	   			endDeviceId: endDeviceId,
	   			codeId: f.getValue()
	    	};
	    	EndDevice.editFacilityStatus(params,
	    		function(req, res) {
	    			events.refreashGrid('situation');
	    			stForm.hide();
	    		},
	    		function(res, req) {
	    			Error.showErrorBox(res);
	    		}
	    	);
	    },
	    addNewEndDevice: function() {
	    	var f = Ext.getCmp('status_form_panel').getForm();
	    	var p = f.getValues();
	    	console.log(p);
	    	var valid = EndDevice.addEndDevice(
	    		{
	    			supplierId: user.supplierId,
	    			locationId: (p.location) ? p.location : rootId,
					codeId: p.typeId,
					manufacturerer: p.manufacturerer,
					model: p.model,
					friendlyName: p.friendlyName,
					installDate: p.installDate,
					manufactureDate: p.manufactureDate,
					powerConsumption: p.powerConsumption,
					modemType: p.modemTypeId,
					modemSerial: p.modemSerial
	    		},
	    		function(res, req) {
	    			params.locationId = rootId;
	    			events.refreashGrid('status');
	    			events.refreshEndDevicePieChart();
	    			events.refreshEndDeviceChart();
	    			sForm.hide();
	    		},
	    		function(res, req) {
	    			Error.showErrorBox(res);	    			
	    		}
	    	);
	    	 
	    	if(valid && !valid.result) {
	    		// XXX: 임시 처리
	    		Error.showErrorBox(valid.key);
	    	}
	    },
	    updateEndDevice: function() {
	    	var f = Ext.getCmp('status_form_panel').getForm();
	    	var p = f.getValues();   
	    	p.endDeviceId = sForm.data.endDeviceId;
	    	delete sForm.data;
	    	var valid = EndDevice.editEndDevice(
	    		{    			
	    			locationId: (p.locationId) ? p.locationId : rootId,
	    			endDeviceId: p.endDeviceId,
					codeId: p.typeId,
					manufacturerer: p.manufacturerer,
					model: p.model,
					friendlyName: p.friendlyName,
					installDate: p.installDate,
					manufactureDate: p.manufactureDate,
					powerConsumption: p.powerConsumption,
					modemType: p.modemTypeId,
					modemSerial: p.modemSerial
	    		},
	    		function(res, req) {
	    			params.locationId = rootId;
	    			events.refreashGrid('status');
	    			events.refreshEndDevicePieChart();
	    			events.refreshEndDeviceChart();
	    			sForm.hide();
	    		},
	    		function(res, req) {
	    			Error.showErrorBox(res);
	    		}
	    	);
	    	if(valid && !valid.result) {
	    		Error.showErrorBox(valid.key);
	    	}
	    },
 
		printFacilityExcel: function(e) {			
			 
			var p ;
			
			p = {
				gridType: tabs.getActiveTab(),
				locationId: (params.locationId) ? params.locationId : rootId,
				endDeviceId: params.endDeviceId,
				limit: 5000,
				start : 0,
				supplierId : 1
			} 
			 View.downloadFacilityExcel(p); 
		}
    };
	
	var initialize = function() {
		$(function() {
			Operator.getUserInfo(function(u) {
				user = u;
				render.locationTree(function() {
					initialize[SIZE]();
					eventBind();
					if(typeof window.hide === 'function') window.hide();
				});
			});
		});		
	};
	
	
	initialize.MIN = function() {
		//render.gridTab();
		render.endDevicePieChart();			
		render.endDeviceChart();		 	 
	};
	
	initialize.MAX = function() {
		render.gridTab();
		render.endDeviceChart();
		//render.usedChart();
		render.endDeviceForm();
	};
	
	var eventBind = function() {
		
		// 개별 이벤트
		eventBind[SIZE]();

	};
	// MIN 가젯 개별 이벤트 바인딩 함수
	eventBind.MIN = function() {};
	
	// MAX 가젯 개별 이벤트 바인딩 함수
	eventBind.MAX = function() {
		// 엑셀 출력.
		$(".chart_and_grid .excel").click(events.printFacilityExcel);
	
	};

	var execute = function() {
		$(function() {
			Ext.QuickTips.init();
			initialize();
		});
	};
	
	return {
		execute: execute
	};
});