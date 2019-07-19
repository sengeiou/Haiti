define([ 
    "jquery",
    "framework/Config/CommonConstants",
    "framework/Model/Operator",
    "framework/Model/Meter",
    "framework/Model/Modem",
    "framework/Model/DeviceModel",
    "framework/Model/System",
    "framework/View/ErrorView",
    "framework/View/mvm/ManualMeteringView",
    "framework/Util/LocaleDateUtil",
    "framework/Util/ObjectUtils"
], function(
	$, CONST,
	Operator, Meter, Modem, DeviceModel, System, 
	Error, View, 
	DateUtil, CommonUtils) {

	// Optimize scope searching && shortcut
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var SIZE = GLOBAL_CONTEXT.SIZE;
	var I18N = GLOBAL_CONTEXT.I18N;
	var JSON = Ext.util.JSON;

	var user = {}; // Userinfo
	
	var menuTabs = {}; // Top Tabs UI (jQuery)
	var meterTypeTabs = undefined; // Grid Tabs UI (ExtJS)
	
	var grids = {}; // Grids
	var charts = {}; // Charts
	var stores = {}; // Data Stores
	var MeterMap = {}; // Meter Data Map
	
	var metaData = {}; // MetaData (etc...)
	
	var controlVars = {}; // Gadget variables 

	var mtForm = undefined;
	var stForm = undefined;

	// Paging variables
	var pageLimit = {
		MIN: 15,
		MAX: 10
	};
	
	// flag variables
	var flag = {
		checkMdsIdDup: false
	};

	var $ELEMENTS = {};	
	
	// 이벤트 함수 모음
	var eventSet = {

		refreashGrid: function() {
			
		},
		// 폼의 액션 속성이 eventSet의 이벤트 함수 이름이 된다.
		submitForm: function($frm) {
			if(!$frm || $frm.size() < 1) {
				return;
			}
			var action = $frm.attr("action");
			var actionFunction = eventSet[action];
			if(typeof actionFunction === 'function') {
				actionFunction($frm);
			}
		},
		// 폼을 리셋한다.
		resetForm: function($frm) {
			
			// 폼을 초기화하고, 모든 셀렉트박스를 다시 그린다.
			$frm.trigger("reset");
			$frm.find("select").each(function() {
				$(this).selectbox();
			});
			
			// 날자 선택 폼을 초기화한다.
			if($("#metering-day-type").size() > 0) {
				$("#metering-day-type").trigger("aimir:daytypechage");
			}
			
			// 벤더별 모델 셀렉트박스가 있다면 옵션을 비운뒤 다시 초기화한다.
			if($ELEMENTS.singleRegMeterModel.size() > 0) {
				$ELEMENTS.singleRegMeterModel.emptySelect();
				$ELEMENTS.singleRegMeterModel.selectbox();
			}
			
			// 폼에 속한 플래그 변수들도 초기화한다.
			for(var k in flag) {
				if(flag.hasOwnProperty) {
					flag[k] = false;
				}
			}
			
			// 검색결과 보존을 위한 데이터도 없앤다
			controlVars.meteringGrid = null;
		},	
		// 에너지 타입 셀렉트박스가 변경되면 검침단위도 자동 변경한다.	
		autoApplyMeteringUnit: function(e) {
			var meterValue = $(this).find("option:selected").val();
			if(meterValue in MeterMap) {
				var m = Meter.getMeterTypeByMeterName(MeterMap[meterValue].name);						
				if(m && m.type) {
					var u = CONST.EnergyUnit[m.type];	
					$("#metering-unit-selector").val(u).selectbox();
				}
			}
		},
		// 날짜 입력에 따라 날자 입력 폼을 토글한다.
		toggleDateInput: function(e) {
			var $area = $ELEMENTS.dateToggleArea;
			if($(this).val() === CONST.DateType.DAILY) {				
				DateUtil.renderDateSelector({
					supplierId: user.supplierId,
					renderTo: $area,
					daily: true
				});
			}
			else if($(this).val() === CONST.DateType.MONTHLY) {
				DateUtil.renderDateSelector({
					supplierId: user.supplierId,
					renderTo: $area,
					monthly: true
				});
			}
		},
		// 장비모델을 자동으로 현재 값에 따라 자동 바인딩한다.
		bindDeviceModels: function() {
			var venderId = $ELEMENTS.addNewMeterForm
				.find("#singleRegMeterVendor option:selected").val();
			var meterTypeCode = $ELEMENTS.addNewMeterForm
				.find("select.meter-type-select option:selected").val();
			if(!venderId || !meterTypeCode) return;
			DeviceModel.deviceModelsByVenendorId(
				venderId,
				meterTypeCode,
				null,							
				function(r) {				
					$ELEMENTS.singleRegMeterModel.pureSelect(r.deviceModels);
					$ELEMENTS.singleRegMeterModel.selectbox();
				}
			);
		},
		// 미터아이디(mdsId) 중복 여부를 검사한다.
		meterDuplicate: function(e) {
			var id = $("#singleRegMeterMdsId").val();
			if(!id) {
				Error.showErrorBox(I18N["aimir.inputid"]);
				return;
			}
			Meter.existsByMdsId(
				{ mdsId: id },
				function(exists) {
					if(!exists) {
						flag.checkMdsIdDup = true;
						View.messageBox(
							id + I18N["aimir.abailableId"], 
							I18N["aimir.info"]
						);
					}
					else {
						flag.checkMdsIdDup = false;
						View.messageBox(
							I18N["aimir.notAvailableId"], 
							I18N["aimir.warning"]
						);
					}
				}
			);
		},
		/**
		 * 새로운 수검침 항목을 입력한다.
		 * @param $frm
		 */
		writeManualMetering: function($frm) {
			var params = CommonUtils.keyNamePareToObject($frm);			
			var localeDateString = "";
			var requireConvertDate = false;

			// XXX: 일별 선택이 UI에서 삭제됨에 따른 강제 일별 지정
			params.dayType = DateUtil.DateType.DAILY;

			if(params.dayType == DateUtil.DateType.DAILY) {
				localeDateString = params["daily-day"];
				requireConvertDate = true;
			}
			else if(params.dayType == DateUtil.DateType.MONTHLY) {
				requireConvertDate = false;
				var y = params["monthly-year"];
				var m = params["monthly-month"];
				if(m.length < 2) m = "0" + m;
				localeDateString = y + m;
			}

			// 실제 프로세스 함수
			function sendRequest(rawDate) {				
				params.meteringDate = rawDate;
				params.supplierId = user.supplierId;
				var errArray = Meter.writeManualMetering(
					params,
					function(result) {
						if(result.result == "success") {
							View.messageBox(
								I18N['aimir.msg.insertsuccess'], I18N["aimir.info"]
							);							
						}
						else {							
							var msgAry = result.msg;
							var error = CommonUtils.sharpErrorConveter('aimir.manualmeter.', msgAry);
							Error.showErrorBox(error);
						}
					},
					function(result) {
						if(result) {
							Error.showErrorBox(
								"[" + result.status + "] " + result.statusText
							);
						}
						else {
							Error.showErrorBox(unknown)
						}
					}
				);
				if(errArray && errArray.length > 0) {
					Error.showErrorBox(errArray);
				}
			}

			// 일별은 포매팅을 거친다.
			if(requireConvertDate) {
				DateUtil.convertRawDates([ localeDateString, '' ], function(r) {
					if(r) {		
						sendRequest(r.searchStartDate);			
					}				
				});
			}
			// 월별은 포매팅할 필요가 없다.
			else {
				sendRequest(localeDateString);
			}
		},
		/**
		 * 수검침 항목을 수정한다.
		 * @param $frm
		 */
		modifyManualMetering: function($frm) {
			return eventSet.writeManualMetering($frm);
		},

		updateMeteringData: function() {

			var f = Ext.getCmp('meteringFormId').getForm();
			var p = f.getValues();

			var localeDateString = "";
			var requireConvertDate = false;
			var params = {
				dayType : DateUtil.DateType.DAILY,
				mdsId : mtForm.meter.mdsId,
				supplierId : mtForm.supplierId,
				meteringDate : mtForm.meteringdate,
				meteringValue : p.thisdaydata
			};

			if(params.dayType == DateUtil.DateType.DAILY) {
				localeDateString = params.meteringDate;
				requireConvertDate = true;
			}

			// 일별은 포매팅을 거친다.
			if(requireConvertDate) {
				DateUtil.convertRawDates([ localeDateString, '' ], function(r) {
					if(r) {		
						sendRequest(r.searchStartDate);			
					}				
				});
			}
			// 월별은 포매팅할 필요가 없다.
			else {
				sendRequest(localeDateString);
			}

			// 실제 프로세스 함수
			function sendRequest(rawDate) {				
				params.meteringDate = rawDate;
				
				var errArray = Meter.writeManualMetering(
					params,
					function(result) {
						if(result.result == "success") {
							View.messageBox(
								I18N['aimir.msg.insertsuccess'], I18N["aimir.info"]
							);
							Ext.getCmp('meteringFormWindowId').close();
						}
						else {							
							var msgAry = result.msg;
							var error = CommonUtils.sharpErrorConveter('aimir.manualmeter.', msgAry);
							Error.showErrorBox(error);
						}
					},
					function(result) {
						if(result) {
							Error.showErrorBox(
								"[" + result.status + "] " + result.statusText
							);
						}
						else {
							Error.showErrorBox(unknown)
						}
					}
				);
				if(errArray && errArray.length > 0) {
					Error.showErrorBox(errArray);
				}
			}

			
	    },
		/**
		 * 수검침 목록 엑셀 출력
		 * @param e jquery extends event 객체
		 */
		printMeteringExcel: function(e) {			
			if(!controlVars.meteringGrid) {				
				controlVars.meteringGrid = {};
			}
			var p = $.extend({}, controlVars.meteringGrid);
			var $frm = $("#metering-search form[action='searchMeteringGrid']");
			var searchParams = CommonUtils.keyNamePareToObject($frm);
			var m = MeterMap[searchParams.meterType];
			var meterObject = Meter.getMeterTypeByMeterName(m.name);	
			
			p = {
				gridType: tabs.getActiveTab(),
				supplierId: user.supplierId,
				dayType: 'DAY',
				meterType: meterObject.type,
				isManualMeter: 1
				
			}
			if(searchParams["period-sdate"] && searchParams["period-edate"]) { // 미터링 시작, 끝 범위
				p.sdate = searchParams["period-sdate"];
				p.edate = searchParams["period-edate"];
			}
			
			View.downloadMeteringExcel(p);
		},
		/**
		 * 새 미터를 추가한다.
		 * @param $frm
		 */
		addNewMeter: function($frm) {
			if(!flag.checkMdsIdDup) {
				View.messageBox(
					I18N["aimir.chkDuplicateMeterId"], 
					I18N["aimir.info"]
				);
				$ELEMENTS.addNewMeterForm.find("input[name='mdsId']").focus();
				return;
			}	
			$ELEMENTS.addNewMeterForm.find("input[name='supplier.id']").val(user.supplierId);
			var meterType = 
				$ELEMENTS.addNewMeterForm.find("select.meter-type-select option:selected").val();
			var isManualMeter = $ELEMENTS.addNewMeterForm.find("input#manual-meter-check");
			
			var option = {
				meterType: meterType,
				params: CommonUtils.keyNamePareToObject($frm)
			};

			option.params["isManualMeter"] = (isManualMeter.is(":checked")) ? "1" : undefined;
			var errArray = Meter.add(
				option, 
				function(result) {
					View.messageBox(I18N["aimir.msg.add"], I18N["aimir.info"]);

					// 매뉴얼미터 셀렉트박스를 갱신한다.
					render.manualMeterSelectbox();
				},
				function(result) {
					Error.showErrorBox(I18N['aimir.failed']);
				}
			);
			
			if(errArray && errArray.length > 0) {
				Error.showErrorBox(errArray);
			}
		},
		/**
		 * 수검침 그리드를 갱신한다.
		 * 
		 * @param $frm	검색폼 jQuery extendArray
		 */ 
		searchMeteringGrid: function($frm) {
			var searchParams = CommonUtils.keyNamePareToObject($frm);
			var meterName = "";
			var storeKey = "GridStore"; 
			var focusGrid = "Grid";		
			var m = MeterMap[searchParams.meterType];

			// 미터의 단축 이름을 구한다.
			var meterObject = Meter.getMeterTypeByMeterName(m.name);
			
			// 만일 전기 그리드 스토어라면, stores[EMGridStore]
			storeKey = meterObject.type + storeKey;

			// 해당 그리드 탭 아이디 얻기 
			// (검색 시 그 그리드 탭으로 변경해 주기 위함)
			focusGrid = meterObject.type + focusGrid;

			meterTypeTabs.setActiveTab(focusGrid);		
			
			// 검색 파라미터를 만든다.
			var params = { // 기본 검색 파라미터
            	supplierId: user.supplierId,
            	isManualMeter: 1,
            	dayType: Meter.DayType.DAY,            	
            	meterType: meterObject.type
			};
			if(searchParams.mdsId) { // 미터아이디
				params.mdsId = searchParams.mdsId;
			}
			if(searchParams.friendlyName) { // 미터 이름
				params.meterName = searchParams.friendlyName;
			}
			if(searchParams["period-sdate"] && searchParams["period-edate"]) { // 미터링 시작, 끝 범위
				params.sdate = searchParams["period-sdate"];
				params.edate = searchParams["period-edate"];
			}
			// 검색 파라미터 만들기 끝.
			
			// 벰버변수로 공유한다.
			controlVars.meteringGrid = params;
			var s = stores[storeKey];
			
			// 그리드 로드
			if(s) {
				s.load();
			}
			else {
				Error.showErrorBox(meterName + "'data is not exists or loaded");
			}
		},
		/**
		 * 탭메뉴 변경 이벤트 핸들러들.
		 */
		menuTabChange: function(event, ui) { },
		gridTabChange: function(event, ui) { }
	};
	
	// UI 렌더링 커맨드 함수 모음
	var render = {
		menuTab: function() {
			menuTabs = View.renderMenuTab({
				menuTabHandler: {
					show: eventSet.menuTabChange
				},
				gridNChartHandler: {
					show: eventSet.gridTabChange
				}
			});
		},
		gridTabs: function() {
			meterTypeTabs = View.renderGridTab({
				items: [				    
				    render.meteringGrid(Meter.Type.GM.type, Meter.DayType.DAY),
				    render.meteringGrid(Meter.Type.HM.type, Meter.DayType.DAY),
				    render.meteringGrid(Meter.Type.WM.type, Meter.DayType.DAY),
				    render.meteringGrid(Meter.Type.EM.type, Meter.DayType.DAY)
		        ]
			});
		},
		selectboxes: function() {
			
			// 벤더리스트 셀렉트박스를 초기화한다.
			System.getVenders(
				{ supplierId: user.supplierId },
				function(res) {
					var p = CommonUtils.keyNamePareToObject($ELEMENTS.addNewMeterForm);					
					$ELEMENTS.singleRegMeterModel.selectbox();
					var venders = JSON.decode(res.responseText);
					$ELEMENTS.singleRegMeterVendor.pureSelect(venders.deviceVendors);
					$ELEMENTS.singleRegMeterVendor.selectbox();
					$ELEMENTS.singleRegMeterVendor.change(eventSet.bindDeviceModels);
				}
			);
			
			// 미터타입 셀렉트박스를 초기화한다
			Meter.getMeterTypes(
				function(res) {					
					MeterMap = res;
					var combo = [];
					// 미터 맵을 콤보박스에 맞게 변환한다.
					for(var k in MeterMap) {
						if(MeterMap.hasOwnProperty(k)) {
							if(MeterMap[k].descr != "null") {
								combo.push({
									id: MeterMap[k].id,
									name: MeterMap[k].descr
								});
							}
							else {
								combo.push({
									id: MeterMap[k].id,
									name: MeterMap[k].descr
								});
							}
						}
					}
					
					$ELEMENTS.meterTypeSelectobox.pureSelect(combo);
					$ELEMENTS.meterTypeSelectobox.selectbox();
					$ELEMENTS.meterTypeSelectobox.change(eventSet.bindDeviceModels);
				}
			);
			
			var $unitSelect = $("#metering-unit-selector");
			$.each(CONST.EnergyUnit, function(index, value) {
				var exists = !!$unitSelect.find("option[value="+value+"]").size();
				if(!exists) {
					$unitSelect.append("<option value="+value+">"+value+"</option>");
				}
			});
			
			// 매뉴얼미터 셀렉트박스를 초기화한다.
			render.manualMeterSelectbox();

			// 로컬 셀렉트박스를 초기화한다		
			$ELEMENTS.localSelect.selectbox();
		},
		manualMeterSelectbox: function(){
			Meter.getManualMeterList(
				{ supplierId: user.supplierId },
				function(res) {
					var ops = [];
					var meterList = res.meterList;
					if(meterList && CommonUtils.isArray(meterList)) {
						for(var i=0,len=meterList.length; i<len; i++){
							var mdsId = meterList[i].mdsId;
							var name = meterList[i].friendlyName;
							ops.push({
								id: mdsId,
								name: mdsId + ((name) ? " (" + name + ")" : ''),
								order: (i+1)
							});
						}
						$ELEMENTS.manualMeterSelectbox.pureSelect(ops);
						$ELEMENTS.manualMeterSelectbox.selectbox();
					}
				}
			);
		},
		/**
		 * 수검침 그리드를 그린다
		 *  
		 * @param meterType 검침 타입
		 * @param dayType 날자 타입
		 * @param title 그리드 제목
		 * @returns
		 */
		meteringGrid: function(meterType, dayType, title) {
			var storeKey = meterType + "GridStore";
			var gridKey = meterType + "Grid";
			if(stores[storeKey]) {
				return stores[storeKey];
			}
			var store = Meter.getMeteringStore({
				pageLimit: pageLimit[SIZE],
				supplierId: user.supplierId,
				dayType: dayType,
				meterType: meterType,
				listeners: {
	            	beforeload: function(store, option) { // 검색 결과 유지
	            		if(controlVars.meteringGrid) {
	            			$.extend(option.params, controlVars.meteringGrid);
	            		}
	            	},
	            	load: function(store, record, option) {
	            		if(store && store.reader && store.reader.jsonData) {
	            			var resData = store.reader.jsonData;
	            			$searchForm = $('#period-search-datepicker');	            			
	            			DateUtil.convertLocaleDate(resData.sdate.substring(0, 8), function(r) {
	            			 	$searchForm.find("input[name='period-sdate']").val(r.localDate);        				
	            			});
	            			DateUtil.convertLocaleDate(resData.edate.substring(0, 8), function(r) {
	            			 	$searchForm.find("input[name='period-edate']").val(r.localDate);
	            			});
	            		}
	            	}
	            }
			});
			stores[storeKey] = store;
			var grid = View.renderMeteringGrid({
				gridId: gridKey,
				meterType: meterType,
				title: Meter.Type[meterType].title,
				store: store,
				updateMeteringDataWindow: render.updateMeteringDataWindow
			});
			grids[gridKey] = grid;
			return grid;
		},
		/**
		 * 수검침 전용 그리드를 그린다
		 * 
		 * 데이터스토어가 로드되고, 스토어에 데이터가 있다면 
		 * 첫번째 데이터로 차트를 그리는 이벤트를 바인딩한다.
		 * 
		 * 그리드에는 행 클릭 이벤트를 바인딩한다.
		 * 
		 * @param params
		 * @returns
		 */
		manualMeterGrid: function(params) {
			var storeKey = "manualMeterStore";
			var gridKey = "manualMeterGrid";

			var store = Meter.getManualMeterStore({
				supplierId: user.supplierId
			});
			var grid = View.renderManualMeterListGrid({
				gridId: gridKey,
				title: I18N["aimir.manualmeter"],
				store: store
			});			
			grids[gridKey] = grid;
			stores[storeKey] = store;
			
			// 수동 미터가 선택되면, 해당 미터의 사용량 차트를 표시하는 이벤트 핸들러를 호출하고 핸들러에
			// 레코드 데이터를 전달한다.
			store.on("load", function(s, r, o) {
				if(s && s.data && s.data.length > 0) {
					if(s.data.items[0].data){
						render.meteringChart(s.data.items[0].data);
					}
				}
				// 한번 수행되면 다시는 수행할 필요가 없다.
				stores[storeKey].un("load");
			});
			grid.on("rowclick", function(g, i, e) {
				var record = g.getStore().getAt(i);
				if(record) {
					render.meteringChart(record.data);
				}
			});			
			return grid;
		},
		/**
		 * 수동미터에 해당하는 기간별 사용량 차트를 그린다.
		 * @param data 수동미터 정보 객체
		 */
		meteringChart: function(data) {
			var shortTypeInfo = Meter.getMeterTypeByMeterName(data['meterType.name']);
			if(data && data.id && shortTypeInfo) {
				Meter.getManualUsageMeteringData(
					{						
						supplierId: user.supplierId,
						mdsId: data.mdsId,
						energyType: data['meterType.name'],
					},
					function(data) {
						charts = View.renderMeteringChartByManual({
							energyType: shortTypeInfo.type,
							data: data
						});
					}
				);
			}
			else {
				View.renderMeteringChartByManual();
			}
		}, // 함수 조건문 end
		
		updateMeteringDataWindow: function(grid, rowIdx, cellIdx, handlerElement, event) {
			
			var aRow = grid.getStore().getAt(rowIdx);
	//		console.log(aRow.json);
			mtForm = undefined;
			
			mtForm = aRow.json;
			delete sForm;
			stForm = View.renderMeteringUpdate({
				//store: detailstore,
	    		meterId : mtForm.meter.mdsId,
	    		meteringdate : mtForm.meteringdate,
	    		//meteringvalue : mtForm.baseValue,
	    		handler: {
							saveHandler: eventSet.updateMeteringData,
			                cancelHandler: function() {
			                	Ext.getCmp('meteringFormWindowId').close();
			                }		                
						}
			});	
			//eventSet.refreashGrid();
	    }

	};
	/**
	 * 가젯에 이벤트를 바인딩한다
	 */
	var eventBind = function() {
		
		// 개별 이벤트
		eventBind[SIZE]();
		
		// 공통 이벤트
		eventBind.common();
	};
	
	eventBind.common = function() {

		// 폼 이벤트들
		$("form a.reset").live("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			eventSet.resetForm($(frm));
		});		
		$("form a.submit").live("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			eventSet.submitForm($(frm));
		});

		// 검침값 등록 폼의 일/월 셀렉트박스 변경시마다 입력 폼 변경
		$("#metering-day-type").bind("aimir:daytypechage", eventSet.toggleDateInput);

		// 단위 자동선택 기능
		$("#regist-meter-type").change(eventSet.autoApplyMeteringUnit);


		// 미터등록 시 datepicker 날자 변경 커스텀 이벤트
		// 강제로 일별만 들어가도록 적용하게 되어 삭제됨.
		// $("#metering-day-type").change(function() {
		// 	$("#metering-day-type").trigger("aimir:daytypechage");
		// });

		// 날자 입력 폼 초기화를 위해 이벤트를 한번 발생시킴 
		// $("#metering-day-type").trigger("aimir:daytypechage");

		DateUtil.renderDateSelector({
			supplierId: user.supplierId,
			renderTo: $ELEMENTS.dateToggleArea,
			daily: true,
			callback: function($tabs, selectingTab) {
                // 오늘 날자 표시
                DateUtil.getDate(function(r) {
                    $tabs.find("input[name=daily-day]").val(r.currDate);
                });
            }
		});

		// 왜 동작 안하는가!!!!!
		$ELEMENTS.meterTypeSelectobox.trigger("change");
	}
	
	// MIN 가젯 개별 이벤트 바인딩 함수
	eventBind.MIN = function() {};
	
	// MAX 가젯 개별 이벤트 바인딩 함수
	eventBind.MAX = function() {
		
		// 기간 데이트픽커를 그린다
		DateUtil.renderDateSelector({
			supplierId: user.supplierId,
			renderTo: $('#period-search-datepicker'),
			dayPeriod: true
		});

		// 폼 이벤트 바인딩 미터, 모뎀 중북 체크 및 키 입력시 중복체크 해지
		// 중복체크를 했더라도, 입력 폼에 다시 키다운 이벤트가 들어갈 경우 다시 중복체크 필요하도록 지정함
		$ELEMENTS.addNewMeterForm.find("a#meterDuplicate").click(eventSet.meterDuplicate);
		$ELEMENTS.addNewMeterForm.find("input[name='mdsId']").keydown(function(){ 
			flag.checkMdsIdDup = false; 
		});
		
		// 기존 소스에서 가져온 것. 미터들의 아이디를 가져오지만, 동작 구현은 되어있지 않다.
		$("input#singleRegMeterModemSerial").keyup(eventSet.modemSerialAutoComplete);
		
		// 엑셀 출력.
		$("#metering-search .excel").click(eventSet.printMeteringExcel);
		
		// 특정 폼 클릭시 로케이션 트리 표시.
		View.locationTreeGoGo('treeDivMeter', 'searchWord', 'locationId', 'location');
	};
	
	var initialize = function() {
		
		// 일단 유저 정보를 얻기 전까지는 아무것도 하지 않는다.
		Operator.getUserInfo(function(u) {
			
			user = u; // 유저 정보를 멤버변수에 할당
			
			// UI를 초기화하고 jQuery 확장집합으로 만든다.
			$ELEMENTS = View.initializeUI();

			View.checkRequireMark(); // 필수 항목 체크
			initialize[SIZE]();	// 가젯 초기화 시작한다.

			eventBind(); // 이벤트 바인딩

			// 가젯 로딩 인디케이터를 없앤다
			if(CommonUtils.isFunction(window.hide)) window.hide();
		});
	};
	
	initialize.MIN = function() {
		render.menuTab(); // 상단메뉴 탭을 초기화한다.
		render.gridTabs(); // 그리드 탭을 초기화한다
		render.selectboxes(); // 셀렉트박스들을 초기화한다.
	};
	
	initialize.MAX = function() {
		render.menuTab(); // 상단메뉴 탭을 초기화한다.
		render.gridTabs(); // 그리드 탭을 초기화한다
		render.manualMeterGrid({}); // 수동미터 그리드를 그린다.
		render.meteringChart({}); // 수검침 차트를 그린다.
		render.selectboxes(); // 셀렉트박스들을 초기화한다.
	};	
	
	// 최초 실행
	var execute = function(CONTEXT) {
		$(document).ready(function() {
			Ext.QuickTips.init(); // quickTips activate

			initialize(); // 가젯 초기화
			
		});
	};
	
	return {
		execute: execute // 공개
	};
});