/**
	피크 수요 가젯 Min, Max 컨트롤러
	@author Yi, Hanghee (javarouka@gmail.com, @YiHanghee)	
*/
define([ 
    "jquery",
    "framework/Config/CommonConstants",
    "framework/Model/Operator",
    "framework/Model/Energy",  
    "framework/Model/Event",  
    "framework/View/ErrorView",
    "framework/View/bems/ContractEnergyPeakDemandView",
    "framework/Util/LocaleDateUtil",
    "framework/Util/ObjectUtils"
], function(
	$, CONST, Operator, Energy, Event, Error, View, DateUtil, Utils) {

	// Optimize scope searching && shortcut
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var SIZE = GLOBAL_CONTEXT.SIZE;
	var I18N = GLOBAL_CONTEXT.I18N;	

	// 인터벌
	var intevalId;
	var thresholdChageTimeoutId;
 	var refreshInterval = (1000 * 60 * 5);
 	var inputDelayTerm = 1000;

 	// jQuery Extended Array Elements
 	var $ELEMENTS = {};
	var $FORM_BTN = {};	
	var $THRESHOLDS = {};	

    // Operator
    var operator = {};

	// Gadget variables 
    var controlVars = {};

	// Paging variables
	var pageLimit = {
		MIN: 3,
		MAX: 10,
		DRScneario: 10
	};

    // DataStore 생성 모음
    var stores = {
    	peekDemandLogs: null,
    	DRLogs: null,
    	DRScneario: null
    };

    // Ext Grids
    var grids = {
    	peekDemandLogs: null,
    	DRLogs: null,
    	DRScneario: null
    };

    // Fusion Charts
    var charts = {
    	gaugeChart: null,
    	barChart: null
    };

    var TOTAL_TAGS; // 전체 태그 리스트 캐시 변수
    var TOTAL_TAGS_INDEX = {}; // 코드와 캐시의 값을 연결한 인덱싱 객체

    var flag = {}; // 페이지 상태 플래그 변수 객체

	// 이벤트 함수 모음
	var eventSet = {
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
			var actionFunction = eventSet[action];
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
				if(flag.hasOwnProperty) {
					flag[k] = false;
				}
			}

			View.renderTagList([]);
		},
		/**
			자동 차트 리프레쉬 설정
			@millis 간격. 밀리초로 설정
		*/
		setRefreshIntervalTerm: function(millis) {
			if(millis && !isNaN(millis)) {
				refreshInterval = millis;
			}
		},
		/*
			자동 차트 리프레쉬 시작
		*/
		startAutoRefresh: function() {
			eventSet.stopAutoRefresh();
			intevalId = setInterval(
				eventSet.getEnergyPeakDemand, 
				refreshInterval, 
				[ render.gaugeAndBarChart, render.bindText ]
			);
		},		
		// 자동 차트 리프레쉬 중지		
		stopAutoRefresh: function() {
			if(intevalId) {
				clearInterval(intevalId);			
			}
		},	
		// 위험 입력항목 누락시		 
		threshold3ValChk: function(e) {
			View.closeAlert(e);
		 	if (!$.trim($THRESHOLDS.critical.val())) {
				Error.showErrorBox(I18N['aimir.required.critical']);
				$threshold3.val("50");
		     	$threshold3.focus();
		     	return false;
		 	} 
		 	return true;
		},
		// 경고 입력항목 누락시		
		threshold2ValChk: function(e) {
			View.closeAlert(e);
		 	if (!$.trim($THRESHOLDS.warning.val())) {
				Error.showErrorBox(I18N['aimir.required.warning']);
				$threshold2.val("1");
		     	$threshold2.focus();
		     	return false;
		 	}
		 	return true;
		},		
		// 차트 내용을 갱신한다.
		// 여러 이벤트나 직접 호출로 접근되는 메서드라 처리가 약간 복잡하다.
		//
		// 1. input태그에서 키보드 이벤트 핸들러로 사용될 경우,
		// 	숫자키나 엔터키 이외에는 무시한다.
		// 2. 스레쉬홀드값이 유효하지 않을 경우에도 경고 처리하며 리턴된다.
		// 3. 정상 실행의 경우 지연 어베이트 타이머가 초기화되며, 실제 서버에 업데이트하고,
		// 	새로 차트를 렌더링한다.
		update: function(e) {
			if(e.keyCode && e.keyCode !== 13  && (e.keyCode < 48) || (e.keyCode > 57)) {
				return;
			}
			if(Number($.trim($THRESHOLDS.critical.val())) <= Number($.trim($THRESHOLDS.warning.val()))) {
				Error.showErrorBox(I18N['aimir.validation.criticalwarning']);
	            return;
	        } 
			if(eventSet.threshold2ValChk() && eventSet.threshold3ValChk()) {
				if(thresholdChageTimeoutId) {
				 	clearTimeout(thresholdChageTimeoutId);
				}
				var parameter = {
					contractCapacityId: $ELEMENTS.contractCapacityId.val(), 
					threshold1: $THRESHOLDS.good.val(), 
					threshold2: $THRESHOLDS.warning.val(), 
					threshold3: $THRESHOLDS.critical.val()
				}
	        	Energy.updateThreshold(
	        		parameter,
	        		function(res) {
	        			// 이벤트를 발생시켜 새로 차트들을 렌더링한다.
	        			eventSet.getEnergyPeakDemand(
				        	[ render.bindText, render.gaugeAndBarChart ]
				        );
				        if(thresholdChageTimeoutId) {
							clearTimeout(thresholdChageTimeoutId);
						}
	        		},
	        		function(res) {
	        			Error.showErrorBox(res);
	        		}
	        	);
	        }
		},
		// 시나리오 설정 내용을 서버에 전송하고 화면을 업데이트한다.
		selectSettingScenario: function(scenarios) {
			Energy.getPeakDemandthresholdConfigs({}, function(res) {
				View.selectSettingScenario(res.settings);
			});
		},
		// 시나리오 액션 이벤트 라우터 함수
		scenarioAction: function($anchor, $frm, jqEvent) {
			if($anchor.hasClass("regist")) {
				eventSet.registScenario($frm, jqEvent);				
			}
			else if($anchor.hasClass("modify")) {
				eventSet.modifyScenario($frm, jqEvent);
			}
			else if($anchor.hasClass("delete")) {
				eventSet.deleteScenario($frm, jqEvent);
			}			
		},		
		// 시나리오 추가
		// 폼 영역이 삽입 모드가 아니라면 폼을 삽입 모드로 바꾸고
		// 폼을 리셋한다.
		registScenario: function($frm, jqEvent) {				
			if($frm._$$editMode !== View.FORMMODE.INSERT) {			
				eventSet.resetForm($frm);
				View.changeFormMode(View.FORMMODE.INSERT, $frm);
				return;
			}
			var parameters = Utils.keyNamePareToObject($frm);
			if(Utils.isArray(parameters.tag)) parameters.tag = parameters.tag.join(',');
			parameters.supplierId = operator.supplierId;			
			var errArray = Energy.addScenario(
				parameters,
				function(res) {
					if(res.result === 'success') {
						View.messageBox(I18N['aimir.msg.insertsuccess'], I18N['aimir.result'])
						render.drScenarioSelectbox();
						stores.DRScneario.reload();
					}
					else {
						Error.showErrorBox((I18N[res.msg] || res.msg));
					}					
				},
				function(res) {
					Error.showErrorBox(res);
				}
			)
			if(Utils.isArray(errArray) && errArray.length > 0) {
				Error.showErrorBox(errArray);
			}
		},
		// 시나리오 수정
		// 폼 영역이 데이터 모드가 아니면 폼을 에디터 모드로 바꾸고
		// input 값을 적절하게 채운다.
		//
		// TODO: 코드가 난잡하므로 간단하게 dataset 속성으로 전부 교체할 필요가 있다.
		modifyScenario: function($frm, jqEvent) {
			if($frm._$$editMode !== View.FORMMODE.EDIT) {
				var tagString = "";
				var views = $ELEMENTS.scenarioActionForm.find(".view-mode");
				views.each(function() {
					var that = $(this);
					var bind = that.attr("data-bind");		
					if(bind === 'target') {
						tagString = that.find("span").text();
					}
					$frm.find("[name='"+bind+"']").val(that.find("span").text());					
				});
				$frm.find("selectbox").selectbox();

				// 수정에 대비해서 실제 태그 리스트를 그려둔다.
				var tags = $ELEMENTS.scenarioActionForm.find("#csv-tags");
				var targetTag = [];
				if(tags && $.trim(tags.text())) {
					var aryTag = tags.text().split(",");
					for (var i = 0; i < aryTag.length; i++) {
						targetTag.push(TOTAL_TAGS[TOTAL_TAGS_INDEX[aryTag[i]]]);
					};
				}
				View.renderTagList(targetTag);
				View.changeFormMode(View.FORMMODE.EDIT, $frm);

				var tags = tagString.split(",");								
				for (var i = 0; i < tags.length; i++) {
					$("input.tag[value='"+tags[i]+"']", $ELEMENTS.tagListArea).attr("checked", true);
				};				
				return;
			}

			// parameter 정리
			var parameters = Utils.keyNamePareToObject($frm);
			parameters.scenarioId = parseInt($frm.find(".id-area td span:first").text());
			parameters.tag = (Utils.isArray(parameters.tag)) ? parameters.tag.join(','):parameters.tag;
			parameters.supplierId = operator.supplierId;
			parameters.contractLocation = parameters.contractLocationId;

			var errArray = Energy.modifyDRScenario(
				parameters,
				function(res) {
					if(res.result === 'success') {
						View.messageBox(I18N['aimir.msg.updatesuccess'], I18N['aimir.result']);
						render.drScenarioSelectbox();
						stores.DRScneario.reload();
					}
					else {
						Error.showErrorBox((I18N[res.msg] || res.msg));
					}					
				},
				function(res) {
					Error.showErrorBox(res);
				}
			)
			if(Utils.isArray(errArray) && errArray.length > 0) {
				Error.showErrorBox(errArray);
			}
		},
		// 시나리오 삭제
		// 폼 영역이 뷰 모드가 아니라면 폼을 뷰 모드로 바꾼다.
		deleteScenario: function($frm, jqEvent) {
			if($frm._$$editMode !== View.FORMMODE.VIEW) {
				View.changeFormMode(View.FORMMODE.VIEW, $frm);
				return;
			}
			View.confirmBox(
				I18N['aimir.msg.deleteconfirm'], 
				I18N['aimir.bems.view.DRScenario'],
				function() {
					var id = parseInt($frm.find(".id-area td span:first").text());
					var errArray = Energy.deleteDRScenario(
						{
							supplierId: operator.supplierId,
							scenarioId: id
						},
						function(res) {
							try {
								if(res.result === 'success') {
									View.messageBox(I18N['aimir.msg.deletesuccess'], I18N['aimir.result'])
									render.drScenarioSelectbox();
									stores.DRScneario.reload();
								}
								else {
									Error.showErrorBox((I18N[ret.msg] || ret.msg));
								}	
							}	
							catch(e) {
								Error.showErrorBox(e);
							}			
						},
						function(res) {		
							if(Utils.isObject(res) && res.msg)	{
								Error.showErrorBox(res.msg);
							}
							else {
								Error.showErrorBox(res);
							}
						}
					)
					if(Utils.isArray(errArray) && errArray.length > 0) {
						Error.showErrorBox(errArray);
					}
				}
			);
		},		
		// 차트 렌더링에 필요한 EnergyPeakDemand 데이터를 얻는다.
		// callback 인자는 차트를 그리고 난 뒤 실행할 콜백 함수. 
		// 배열로도 줄 수 있다.	배열일 경우 인자 순서대로 실행.				
		getEnergyPeakDemand: function(callback) {		
			var parameter = {
				contractCapacityId: $ELEMENTS.contractCapacityId.val()
			};
			Energy.getEnergyPeakDemand(
				parameter,
				function(res) {
					// 콜백이 배열 함수이면 하나씩 처리.
					if(Utils.isArray(callback)) {
						for(var i=0,len=callback.length; i<len; i++) {
							if(Utils.isFunction(callback[i])) {
								callback[i](res);
							}
						}
					}
					else {					
						if(Utils.isFunction(callback)) callback(res);
					}
				},
				function(res) {
					Error.showErrorBox(res);
				}
			);
		},		
		// 차트 지연 업데이트.
		// 멤버변수에 지정된 시간만큼 조작이 없을 경우 차트를 업데이트한다.
		delayedUpdate: function(e) {			
			thresholdChageTimeoutId = setTimeout(function() {
				eventSet.update(e);
			}, inputDelayTerm);
		},
		// Peak Demand 이벤트를 검색한다.		
		searchPeekDemandHistory: function($frm) {
			var searchParams = Utils.keyNamePareToObject($frm);	
			searchParams = $.extend({
				supplierId: operator.supplierId
			}, searchParams);

			// 날짜 탭을 사용한 폼은 반드시 컨버팅을 거쳐야 한다.
			// XXX: 전혀 직관적이지 않으므로, 추후 반드시 수정이 필요하다.
			DateUtil.searchDateWithFormValues(searchParams, function(params) {
				stores.peekDemandLogs.reload({ 
					params: {
						start: 0,
						limit: pageLimit[SIZE],
						status: params.fm_status,
						locationId: params.location || 1,
						searchDate: params.searchDate,
						supplierId: operator.supplierId
					}
				});
			});
		},
		//	DR 수행 이력을 검색한다.
		searchExcuteDRHistory: function($frm) {
			var searchParams = Utils.keyNamePareToObject($frm);
			searchParams = $.extend({
				supplierId: operator.supplierId
			}, searchParams);

			// 날짜 탭을 사용한 폼은 반드시 컨버팅을 거쳐야 한다.
			// XXX: 전혀 직관적이지 않으므로, 추후 반드시 수정이 필요하다.
			DateUtil.searchDateWithFormValues(searchParams, function(params) {
				stores.DRLogs.reload({ 
					params: {
						start: 0,
						limit: pageLimit[SIZE],
						result: params.dr_status_value,
						scenario: params.dr_scenario_value,
						searchDate: params.searchDate,
						supplierId: operator.supplierId
					}
				});
			});
		},
		// DR 설정
		// 해당 tr의 값들이 하나의 threshold가 되므로,
		// tr 기준으로 그 이하의 폼으로 파라미터를 만든다.
		configDR: function(e) {
			var $tr = $(e.target).parents("tr");
			var params = {};
			if($tr.size() > 0) {
				$tr.find(".threshold").each(function() {
					$that = $(this);
					params[$that.attr("name")] = $that.val();
				});
			}
			params.supplierId = operator.supplierId;			
			params.isAction = (e.data && e.data.isAction === true) ? "true" : "false";

			var errArray = Energy.applyScenario(
				params, 
				function(res) {
					if(res.result === 'success') {
						View.messageBox(I18N['aimir.msg.updatesuccess'], I18N['aimir.result']);
						eventSet.selectSettingScenario();
					}
					else {
						Error.showErrorBox((I18N[res.msg] || res.msg));
					}	
				},
				function(res) {
					Error.showErrorBox(res);
				}
			);
			if(errArray && errArray.length > 0) {
				Error.showErrorBox(errArray);
			}
		},				
		// 화면상의 차트가 전부 그려지면 호출되는 이벤트
		chartsDrawComplete: function(e) {
			View.showBottomLayer();			
			if(charts.barChart) {
				// 이벤트가 반복 실행되지 않게 이벤트를 언바인딩
				charts.barChart.removeEventListener(
					"DrawComplete", eventSet.chartsDrawComplete
				);
			}
		},
		// 전체 태그를 얻어와 특정 변수에 캐시해둔다.		
		setAllTag: function() {
			var $applyDRTags = $ELEMENTS.tagTotalSelectbox.find("option");
			TOTAL_TAGS = [];
			TOTAL_TAGS_INDEX = {};
			$applyDRTags.each(function(i) {
				var $o = $(this);
				TOTAL_TAGS.push({
					id: $o.attr("id"),
					value: $o.attr("value"),
					title: $o.attr("title"),
					descr: $.trim($o.text())
				});
				TOTAL_TAGS_INDEX[$o.attr("value")] = i;
			});
		},
		// 태그들을 이동시킨다.
		moveOptions: function(e) {
			var $me = $(e.target);
			var arrive = ($me.is("li.in")) ? "apply-tags" : "total-tags";
			var depart = (arrive === "apply-tags") ? "total-tags" : "apply-tags";
			View.moveOptions(arrive, depart);			
		},
		// 태그 설정 상태를 폼에 저장한다.
		saveOptionTag: function() {
			var applyDRTags = $ELEMENTS.tagLayer.find(".apply-tags select option");
			var tags = [];
			View.renderTagList(eventSet.transformOptionToTagObject(applyDRTags));				
			render.hideLayer();
		},
		// jQuery option 객체일 경우 태그 오브젝트로 변환한다.
		transformOptionToTagObject: function(options) {
			if(!(options instanceof $)) return;
			var tags = [];
			options.each(function() {
				var $m = $(this);
				tags.push({
		            descr: $.trim($m.text()),
		            id: $m.attr("id"),
		            title: $m.attr("title"),
		            value: $m.attr("value")
		        });
			});
	        return tags;
		}
	};
	
	// UI 렌더링 커맨드 함수 모음
	var render = {
		// Grid Css conflict error fix
		__extCSSConflictBugfix: function(store) {			
			store.on("load", function(dataStore, rows, bool) {                              
				setTimeout(function(){ dataStore.reload(); }, 5000);
            }, null, {single: true});
		},		
		// 각종 셀렉트받스를 렌더링한다.
		selectboxes: function() {
			$(".local-selectbox").selectbox();
			render.drScenarioSelectbox();
		},	
		// dr 시나리오 셀렉트박스를 그린다.
		drScenarioSelectbox: function() {
			Energy.getDRScenarios(
				{ start: 0, limit: 65535, supplierId: operator.supplierId }, 
				function(scenarios) {
					View.renderDRScenarioSelectBox(scenarios.peakDemandScenarios);
					eventSet.selectSettingScenario();					
				}
			);
		},
		// peak demand 탭 렌더링 (jquery-ui)
		// dr 로그 탭을 누르면 자동으로 그리드가 리로드되는 이벤트가 있다.
		// 이펙트는 opacity 토글.
		peekDemandDRTabs: function(e) {
			View.renderPeakDemandDRTabs({
				fx: { opacity: 'toggle' },
				show: function(e, ui) {
					if(ui.panel.id === "dr-execute-history" && stores.DRLogs) {
						stores.DRLogs.reload();
					}
				}
			});			
		},
		// 화면의 차트들을 업데이트한다.		
		// 차트가 로드되기 전에 임시적으로 차트의 영역을 잘못 계산하여
		// 가젯의 아래 영역이 겹쳐 보이는 화면 깨짐을 방지하기 위해
		// 차트가 전부 로드된 다음 아래 영역을 그리게 이벤트를 할당한다.
		gaugeAndBarChart: function(res) {	
			charts.gaugeChart = View.renderGuageChart(res.result); // 게이지 차트
			charts.barChart = View.renderBarChart(res.result.column); // 바 차트			
			if(charts.barChart) {				
				charts.barChart.addEventListener(
					"DrawComplete", eventSet.chartsDrawComplete
				);
			}
			View.lvInfoPopupControll();
			View.lvInfoLightControll();
		},
		// 화면상의 동적 문자열들을 일괄 업데이트한다.
		bindText: function(res) {
			var	r = res.result;
			r.currTime = operator.currTime;
			View.bindTextData(r); // 화면 테스트 데이터 바인딩
		},		
		// peakDemand 로그 그리드를 그린다.
		peekDemandHistoryGrid: function(params) {
			var params = {
				pageLimit: pageLimit[SIZE],
				supplierId: operator.supplierId
			};			
			stores.peekDemandLogs = Event.getPeekDemandLogDataStore(params);
			grids.peekDemandLogs = View.renderPeekDemandHistoryGrid({
				gridId: "peekDemandLogsGrid",
				store: stores.peekDemandLogs
			});
		},
		// DR 데이터 그리드를 그린다.
		drGrid: function(params) {
			var params = {
				pageLimit: pageLimit[SIZE],
				supplierId: operator.supplierId
			};
			stores.DRLogs = Energy.getDRLogDataStore(params);
			grids.DRLogs = View.renderDRGrid({
				gridId: "DRGrid",
				store: stores.DRLogs
			});
		},	
		// dr 시나리오 그리드를 그린다.
		// Extjs Grid에 CSS의 문제인지는 모르겠지만 width force fit 옵션이
		// 적용되지 않는 문제가 있어 데이터를 새로고침하여 임시로 수정해 두었다.
		// 그리드에는 row 클릭시 시나리오 디테일을 볼 수 있는 이벤트가 할당되어 있다.
		drScenarioGrid: function(params) {
			var params = {
				pageLimit: pageLimit.DRScneario,
				supplierId: operator.supplierId
			};

			stores.DRScneario = Energy.getDRScenarioDataStore(params);
			render.__extCSSConflictBugfix(stores.DRScneario);

			grids.DRScneario = View.renderDRScenarioGrid({
				gridId: "DRScenarioGrid",
				store: stores.DRScneario
			});

			grids.DRScneario.on("rowclick", function(g, i, e) {
				var record = g.getStore().getAt(i);
				if(record) {
					render.drScenarioDetail(record.data);
				}
			});
		},
		// 시나리오에 대해 자세히 보여준다.
		// 현재 DR 시나리오 레코드의 선택된 행을 클릭하면 발생하도록 되어 있다.
		drScenarioDetail: function(record) {

			// 폼을 리셋한다.
			eventSet.resetForm($ELEMENTS.scenarioActionForm);

			// 디테일 폼에 선택된 레코드의 데이터를 바인딩한다.
			var views = $ELEMENTS.scenarioActionForm.find(".view-mode");
			views.each(function() {
				var that = $(this);
				var bind = that.attr("data-bind");
				that.find("span").text(record[bind]);
			});			

			// 폼 뷰 모드 변경
			View.changeFormMode(View.FORMMODE.VIEW, $ELEMENTS.scenarioActionForm);
		},
		// 태그 수정 폼을 연다.
		// 이미 적용된 태그는 수정 폼의 적용 태그 셀렉트 박스에 있어야 하므로 
		// 그려진 태그를 얻어와서 태그 수정 폼을 변경한다.
		showLayer: function() {	
			// 태그 수정 폼을 현재 상태에 맞게 업데이트한다.							
			render.updateTagSelector();

			// 레이어를 보여준다.
			View.showTagLayer();
		},
		// 태그 수정폼을 닫는다.
		hideLayer: function() {
			View.hideTagLayer();			
		},
		// 태그 수정 폼을 업데이트한다.
		// 현재 폼에 있는 tag value를 가져와서 option을 전체와 설정 구분하여 업데이트한다.
		// 선택 후 이벤트 트리거를 발생시켜 option을 이동시키는 구조.
		updateTagSelector: function() {
			var aryTag = Utils.keyNamePareToObject($ELEMENTS.scenarioActionForm).tag;
			var tags = '';
			if(aryTag && Utils.isArray(aryTag)) {
				tags = aryTag.join(',');
			}			
			// 폼에 태그가 있다면
			if(tags && $.trim(tags).length > 0) {
				// 해당 코드값의 옵션들을 선택한다.
				var aryTag = tags.split(",");
				for (var i = 0; i < aryTag.length; i++) {					
					$ELEMENTS.tagLayer.find("select option[value='"+$.trim(aryTag[i])+"']").attr("selected", true);
				};		
				// 선택 태그로 이동
				$FORM_BTN.moveApplyTagButton.trigger("click");
			}		
			// 폼에 태그가 없다면	
			else {
				// 모든 값을 선택
				$ELEMENTS.tagApplySelectbox.find("option").attr("selected", true);

				// 전체 태그로 이동
				$FORM_BTN.moveTotalTagButton.trigger("click");
			}
		}
	};
	
	/**
	 * 가젯에 이벤트를 바인딩한다
	 */
	var eventBind = function() {	
		eventBind.common(); // 공통 이벤트		
		eventBind[SIZE](); // 개별 이벤트
	};
	
	eventBind.common = function() {

		// 브라우저의 링크(<a>) 기본동작 막기.
        $("a").click(function(e) {
        	e.preventDefault();
        });

		// 폼 이벤트들
		$FORM_BTN.resets.on("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			eventSet.resetForm($(frm));
		});		
		$FORM_BTN.submits.on("click", function(e) {
			var frm = $(e.target).parents("form")[0];
			eventSet.submitForm($(frm));
		});

		// 계약전력 셀렉트 박스 변경 이벤트
		$ELEMENTS.contractCapacityId.change(function(e) {
			eventSet.getEnergyPeakDemand(
	        	[ render.bindText, render.gaugeAndBarChart, eventSet.startAutoRefresh ]
	        );  
		});

		// 임계치 인풋박스 이벤트
		$THRESHOLDS.good.keyup(eventSet.delayedUpdate).change(eventSet.update);
		$THRESHOLDS.warning.keyup(eventSet.delayedUpdate).change(eventSet.update);
		$THRESHOLDS.critical.keyup(eventSet.delayedUpdate).change(eventSet.update);

		// 경고창 닫기 이벤트
		$FORM_BTN.levelInfoPopupClose.on("click", View.closeAlert);
	}
	
	// MIN 가젯 개별 이벤트 바인딩 함수
	eventBind.MIN = function() {};
	
	// MAX 가젯 개별 이벤트 바인딩 함수
	eventBind.MAX = function() {		

		// 시나리오 적용 이벤트
		$FORM_BTN.applyScenarioButton.click({isAction:true}, eventSet.configDR);
		$FORM_BTN.disableScenarioButton.click({isAction:false}, eventSet.configDR);

		// 시나리오 CRUD 이벤트
		$ELEMENTS.scenarioActionForm.find(".buttons-area a").click(function(e) {
			$anchor = $(e.target);			
			if($anchor.is("a")) {
				eventSet.scenarioAction($anchor, $ELEMENTS.scenarioActionForm, e);
			}			
		});

		// 위치 인풋 폼 동적 트리 이벤트
		View.locationTreeGoGo('treeDivMeter', 'pd-location-input', 'pd-location-hidden', '');

		// 태그리스트 설정 이벤트
		$ELEMENTS.editTagGuide.click(render.showLayer);
		$FORM_BTN.moveTotalTagButton.on("click", eventSet.moveOptions);
		$FORM_BTN.moveApplyTagButton.on("click", eventSet.moveOptions);
		$FORM_BTN.applyTagButton.on("click", eventSet.saveOptionTag);
		$FORM_BTN.cancelTagButton.on("click", render.hideLayer);
	};
	
	var initialize = function() {	
		initialize.common(); // 공통 초기화
        initialize[SIZE](); // 사이즈별 초기화
	};
	initialize.common = function() {  

		// 뷰 초기화
		$ELEMENTS = View.initializeUI();
		$FORM_BTN = $ELEMENTS.FORM_BTN;
		$THRESHOLDS = $ELEMENTS.THRESHOLDS;

		// 셀렉트박스들을 전부 jquery 셀렉트박스화한다.
		render.selectboxes();

		// 날자 선택 탭 렌더링
		View.renderDateTab(operator.supplierId);
		
		// 게이지 및 바 차트, 화면 텍스트 바인딩 
		// 임계치 문자열 바인딩과 차트 그리기이다.
        eventSet.getEnergyPeakDemand(
        	[ render.bindText, render.gaugeAndBarChart, eventSet.startAutoRefresh ]
        );        
	};
	initialize.MIN = function() {};
	initialize.MAX = function() {

		// HTML에 박힌 태그 정보를 사용하기 쉽게 JS 변수에 캐싱해둔다.
		eventSet.setAllTag();

		render.peekDemandDRTabs(); // 시나리오 탭 렌더링
		render.peekDemandHistoryGrid(); // 피크수요 로그 그리드 렌더링
		render.drGrid(); // dr 데이터 그리드 렌더링
		render.drScenarioGrid(); // dr 시나리오 그리드 렌더링

		// 시나리오 입력 폼의 모드를 신규입력 모드로 변경한다.
		View.changeFormMode(View.FORMMODE.INSERT, $ELEMENTS.scenarioActionForm);
	};
	
	// 최초 실행.
	// 유일한 public function
	var execute = function(CONTEXT) {
		$(function() {			
			Operator.getUserInfo(function(u) {

				// 유저 정보를 변수에 캐시한다.
				operator = u;

				Ext.QuickTips.init(); // quickTips activate

				initialize(); // 가젯 초기화
				eventBind(); // 이벤트 바인딩

				// 가젯 로딩 인디케이터를 없앤다
				if(typeof window.hide === 'function') window.hide();
			});
		});
	};
	
	return {
		execute: execute // 공개
	};
});