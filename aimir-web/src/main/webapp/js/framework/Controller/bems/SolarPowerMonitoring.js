define([    
	"framework/Config/CommonConstants",
    "framework/Model/Operator",
    "framework/Model/Weather",
    "framework/Model/Meter",
    "framework/Model/ElectricGenerator",    
    "framework/View/bems/SolarPowerMonitoringView",   
    "framework/View/ErrorView", 
    "framework/Util/ObjectUtils"
], function(CONST, Operator, Weather, Meter, ElecGenerator, View, Error, Util) {

	// Optimize scope searching
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
	var G_DATA = GLOBAL_CONTEXT.G_DATA;
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE;
	var JSON = Ext.util.JSON;

	var $ELEMENTS = {};	

	// User Info
	var user = undefined;

	var CONTROLS = {
		topMenuTab: null
	};

	var STORES = {};

	var PAGE_VARS = {
		refreshIntervelId: null,
		weatherRefreshTermMillis: 1000 * 60 * 60 * 3
	};	

	var flag = {};

	var PAGE_LIMIT = {
		MIN: 3,
		MAX: 15
	};

	// HQ
	var SEARCH_LOC = "jungMun";

	var errorHandler = function(err) {		
		require([ "framework/View/ErrorView" ], function(Error) {
			Error.showErrorBox((err && err.msg) ? err.msg : err);
		})				
	};

	// 가젯 내의 UI 그리기 메서드
	var render = {	

		
		// 상단 메뉴 탭 그리기
		topMenuTab: function() {			
			CONTROLS.topMenuTab = View.renderTopMenuTab({
				select: function(e, ui) {
					if(ui.panel.id === "generarion-view") {
						STORES.generationStatistics.reload();						
					}
				}
			});			
		},		

		// 날자 입력 탭 그리기
		dateForm: function() {
			View.renderDateForm(user.supplierId);
		},	

		// 시간별 발전기 정보 바 차트 그리기
		generationByInverterBarChart: function(e, option) {
			option = option || {};
			ElecGenerator.generationValueByInverter(
				{
					supplierId: user.supplierId,
					today: (option.today) ? option.today : ''
				},
				function(res) {
					View.renderInverterBarChart(res, "onClickInverterBarChartItem");
					//View.setTodayInputOnInverterInfomation(res.today);
				},
				errorHandler
			);
		},

		// 날씨 정보 표시하기
		weatherDescription: function() {
			events.getWeather(function(weather) {
				View.renderWeatherStatus({
					weather: weather,
					ImageMap: Weather.ImageMap,
					callback: events.startRefreshWeatherContent			
				});
			});
		},

		// 현재 발전량 정보 문자열 갱신
		currentGeneration: function() {
			ElecGenerator.getGenerationInfo(
				{},
				View.updateCurrentGenerationUI,
				errorHandler
			);
		},
		
		generationStatisticsOnDateChart: function(params) {
			params = params || {};
			ElecGenerator.getGenerationAmountData(
				{						
					supplierId: user.supplierId,
					inverterId: params.inverterId || ''
				},
				function(data) {
					var text = "All Inverters";
					if(params.inverterId) {
						var inverters = ElecGenerator.getInverterFromCache();
						if(inverters) {
							for(var k in inverters) {
								if(inverters[k].id === parseInt(params.inverterId, 10)) {
									text = inverters[k].friendlyName || inverters[k].mdsId;
									break;
								}
							}
						}
					}
					View.displayForInverterIdTextStatistics(text);
					View.renderGenerationStatisticsOnDateChart(data);
				},				
				errorHandler
			);
		},
		generationStatisticsGrid: function() {
			var gridKey = "generationStatisticsGrid";

			if(STORES.generationStatistics) {
				return STORES.generationStatistics;
			}

			var store = ElecGenerator.getElectricGenerationAmountsDataStore({
				pageLimit: PAGE_LIMIT[SIZE],
				supplierId: user.supplierId,
				searchDateType: "DAILY",
				meterType: "SPM",
				listeners: {
	            	beforeload: function(store, option) { // 검색 결과 유지
	            		if(PAGE_VARS.generationStatisticsGrid) {
	            			$.extend(
	            				option.params, 
	            				PAGE_VARS.generationStatisticsGrid
	            			);
	            		}
	            	}
	            }
			});

			STORES.generationStatistics = store;
			var grid = View.renderGenerationStatistics({
				gridId: gridKey,
				meterType: "EM",
				store: store,
				showDetail :  render.showDetail
			});			

			CONTROLS.generationStatisticsGrid = grid;			
		},
		venderSelectbox: function() {
			console.log("venderSelectbox");
			require(["framework/Model/System"], function(System) {
				System.getVenders(
					{ supplierId: user.supplierId },
					function(res) {
						var s = View.renderVenderForm(JSON.decode(res.responseText));
						s.change(render.deviceModelsSelectbox);
						s.trigger("change");
					}
				);	
			});
		},
		deviceModelsSelectbox: function(e) {
			console.log("deviceModelsSelectbox");
			$addNewInverterForm = $("#addNewInverterForm");
			var venderId = $addNewInverterForm
				.find("#singleRegMeterVendor option:selected").val();
			console.log("venderId",venderId);
			var sMeter = (G_DATA.solarPowerMeter) ? G_DATA.solarPowerMeter : {};
			console.log("G_DATA.solarPowerMeter",G_DATA.solarPowerMeter);
			var meterTypeCode = sMeter.id;
			console.log("meterTypeCode",meterTypeCode);
			if(!venderId || !meterTypeCode) return;

			require(["framework/Model/DeviceModel"], function(DeviceModel) {
				DeviceModel.deviceModelsByVenendorId(
					venderId,
					meterTypeCode,
					null,							
					function(r) {	
						View.renderModelForm(r.deviceModels);						
					}
				);
			});
		},
		
		showDetail: function(grid, rowIndex, colIndex) {
	    	var aRow = grid.getStore().getAt(rowIndex);
	    	var valueRow = aRow.json;
	    	var detailstore = ElecGenerator.getElectricGenerationAmountsDataStore({
				pageLimit: 24,
				supplierId: user.supplierId,
				searchDateType: "HOURLY",
				meterType: "SPM",
				meterName: valueRow.meterNo,
				searchDate : valueRow.meteringTime,
				isDetail: 1,
				listeners: {
	            	beforeload: function(store, option) { // 검색 결과 유지
	            		if(PAGE_VARS.generationStatisticsGrid) {
	            			$.extend(
	            				option.params, 
	            				PAGE_VARS.generationStatisticsGrid
	            			);
	            		}
	            	}
	            }
			});
	    	
	    	View.generationStatisticsDetail({
				store: detailstore,
	    		gridId: "generationStatisticsDetailView"
			});			
		}
	};

	// 가젯 내의 이벤트 셋.
	var events = {	
		/* 
			모든 폼에 바인딩 되는 함수.
			폼의 액션 속성이 eventSet의 이벤트 함수 이름이 된다.

			<form action="doSome">...</form>
			=> eventSet[doSome](jQueryFormElement객체);
		*/
		submitForm: function($frm) {
			if(!$frm || $frm.size() < 1) {
				return;
			}
			var action = $frm.attr("action");
			var actionFunction = events[action];
			if(typeof actionFunction === 'function') {
				actionFunction($frm);
			}
		},
		/* 
			폼을 리셋한다.
		*/
		resetForm: function($frm) {
			
			// 폼을 초기화하고, 모든 셀렉트박스를 다시 그린다.
			$frm.trigger("reset");
			$frm.find("select").each(function() {
				$(this).selectbox();
			});
			$frm.find(":checkbox").attr("checked", false);
			
			// 폼에 속한 플래그 변수들도 초기화한다.
			for(var k in flag) {
				if(flag.hasOwnProperty(k)) {
					flag[k] = false;
				}
			}
		},	
		/*
			새 인버터를 추가한다.
		*/
		addNewInverter: function($frm) {
			var sMeter = G_DATA.solarPowerMeter || {};
			$addNewInverterForm = $("#addNewInverterForm");

			if(!flag.checkMdsIdDup) {
				View.messageBox(
					I18N["aimir.chkDuplicateMeterId"], 
					I18N["aimir.info"]
				);
				$addNewInverterForm.find("input[name='mdsId']").focus();
				return;
			}	
			$addNewInverterForm.find("input[name='supplier.id']").val(user.supplierId);
			var p = Util.keyNamePareToObject($addNewInverterForm);

			var option = {
				meterType: "SolarPowerMeter",
				params: p
			};
			p["meterType.id"] = sMeter.id;

			var errArray = Meter.add(
				option, 
				function(result) {
					if(result.status === 'success') {
						View.messageBox(I18N["aimir.msg.add"], I18N["aimir.info"]);
						render.generationByInverterBarChart();
						render.generationStatisticsOnDateChart();
					}
					else {
						Error.showErrorBox(result.msg || I18N['aimir.failed']);
					}
				},
				function(result) {
					Error.showErrorBox(I18N['aimir.failed']);
				}
			);
			
			if(errArray && errArray.length > 0) {
				Error.showErrorBox(errArray);
			}
		},
		searchGeneration: function($frm) {
			if(!STORES.generationStatistics) return;
			require(["framework/Util/LocaleDateUtil"], function(DateUtil) {
				DateUtil.searchDateWithFormValues($frm, function(params) {
					var dateType = params.searchDateType;
					if(dateType === "period") {
						dateType = "daily";
					}
					PAGE_VARS.generationStatisticsGrid = { 
						params: {
							start: 0,
							limit: PAGE_LIMIT[SIZE],
							inverterId: params.inverterId,
							inverterName: params.inverterName,
							searchDate: params.searchDate,
							searchDateType: dateType,
							supplierId: user.supplierId
						}
					};
					STORES.generationStatistics.reload(
						PAGE_VARS.generationStatisticsGrid
					);
				});
			});
		},
		searchPowerGenerationByDay: function($frm) {
			require(["framework/Util/LocaleDateUtil"], function(DateUtil) {
				DateUtil.searchDateWithFormValues($frm, function(params) {
					DateUtil.convertRawDates([params["daily-day"],''], function(r) {
						render.generationByInverterBarChart(null, {
							today: r.searchStartDate
						});
					});
				});
			});
		},
		// 자동으로 날씨를 갱신하고 현재 발전량 및 일별 발전 현황 차트를 업데이트
		// 하는 타이머를 시작한다.
		startRefreshWeatherContent: function(rightNow) {

			// 일단 무조건 현재 타이머 정지.
			events.stopRefreshWeatherContent();

			function f() {
				render.weatherDescription();
				render.currentGeneration();
			}

			// 바로 시작하라는 명령이 있으면 일단 시작을 하고 본다.
			if(rightNow) {
				f();
			}

			// 타이머를 멤버변수에 지정된 텀마다 수행한다.
			PAGE_VARS.refreshIntervelId = setInterval(f, PAGE_VARS.weatherRefreshTermMillis);
		},

		// 타이머를 중단시킨다.
		stopRefreshWeatherContent: function() {
			if(PAGE_VARS.refreshIntervelId) {
				clearInterval(PAGE_VARS.refreshIntervelId);
			}
			PAGE_VARS.refreshIntervelId = null;
		},	

		// 날씨를 얻는다.
		getWeather: function(callback) {
		 	Weather.getTownWeather(
		 		{ location: SEARCH_LOC },
		 		callback,
		 		errorHandler
		 	);
		},
		clickInverterBarChartItem: function(inverterId, order, chartId) {			
			render.generationStatisticsOnDateChart({
			  	inverterId: inverterId
			});
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
							id + " " + I18N["aimir.abailableId"], 
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
		}
	};

	// 가젯 내의 이벤트를 바인딩한다.
	var eventBind = function() {

		// 브라우저의 링크(<a>) 기본동작 막기.
        $("a").click(function(e) {
        	e.preventDefault();
        });

		// 폼 이벤트들
		$("form a.reset").live("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			events.resetForm($(frm));
		});		
		$("form a.submit").live("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			events.submitForm($(frm));
		});

		eventBind[SIZE]();
	};
	eventBind.MIN = function() {
	};
	eventBind.MAX = function() {	
		$("#meterDuplicate").click(events.meterDuplicate);	

		// 특정 폼 클릭시 로케이션 트리 표시.
		View.locationTreeGoGo('treeDivMeter', 'searchWord', 'locationId', 'location');
	};
	
	// 가젯을 초기화한다.
	// 연관된 뷰들도 초기화를 수행한다.
	var initialize = function() {
		$ELEMENTS = View.initializeUI();		
		initialize[SIZE]();
	};
	initialize.MIN = function() {		
		events.startRefreshWeatherContent(true); // 렌더 타이머를 시작
	};	
	initialize.MAX = function() {

		// 퓨전 차트를 위한 글로벌 이벤트 등록
		// 가급적 전역 변수를 지양하는게 좋지만 현재 방법을 찾을 수 없었다.
		// 더 좋은 방법이 있을까...
		GLOBAL_CONTEXT.G_EVENTS = {};
		GLOBAL_CONTEXT.G_EVENTS["onClickInverterBarChartItem"] = events.clickInverterBarChartItem;

		render.topMenuTab(); // 탑 메뉴 초기화

		render.generationByInverterBarChart(); // 인버터별 발전량 차트 초기화
		render.dateForm(); //날자 입력 폼 초기화
		render.generationStatisticsOnDateChart(); // 인버터별 발전량 기간 통계 차트 초기화
		render.generationStatisticsGrid(); // 그리드 
		render.venderSelectbox(); // 벤더 셀렉트박스

		$singleRegMeterVendor = $("#singleRegMeterVendor"); // 벤더 셀렉트 박스 1
		$singleRegMeterModel = $("#singleRegMeterModel"); // 모델 셀렉트 박스 1
	};

	// public 메서드.
	// 컨트롤러의 진입점이 된다.
	var execute = function() {

		// 보통은 문서 로딩시까지 필요하지 않지만 스크립트 위치 변경에 대비하였다.
		$(document).ready(function() {

			// 모든 가젯은 현재 오퍼레이터 정보가 필요하다. 
			Operator.getUserInfo(function(u) {

				// 유저 정보는 멤버 변수에 할당해둔다.
				user = u;				

				initialize(); // 가젯 초기화 수행
				eventBind(); // 가젯 이벤트 바인딩 수행

				// 가젯 로딩 인디케이터를 없앤다.
				if(Util.isFunction(window.hide)) {
					window.hide();
				}
			});
		});
	};
	
	return {
		execute: execute
	};

});