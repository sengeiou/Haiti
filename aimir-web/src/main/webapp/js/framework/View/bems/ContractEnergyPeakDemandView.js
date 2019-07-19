define([
	"jquery",
    "framework/Config/CommonConstants",
    "framework/View/Control/Chart",
    "framework/View/Control/Grid",
    "framework/View/Control/Tree",
    "framework/View/Control/Alert",
    "framework/Util/LocaleDateUtil",
    "framework/Util/ObjectUtils",
    "FChartStyle"
], function($, CONST, Chart, Grid, Tree, Alert, DateUtil, Utils) {
	
	var GLOBAL_CONTEXT = window.GLOBAL_CONTEXT;
    var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var SIZE = GLOBAL_CONTEXT.SIZE;
    var PagingToolbar = Ext.PagingToolbar;

    var $ELEMENTS;

    var FORMMODE = {
        INSERT: 'insert-mode',
        EDIT: 'edit-mode',
        VIEW: 'view-mode'
    };

    var guageChart;
    var columnChart;

	// 페이징 문자열 포맷
	var toolbarPageFormat = '{0} - {1} of {2}';

    var EMPTY_TEXT = "----------------------------";

	var messageBox = function(val, title, el) {
		Alert.info(val, title, el);
	};

    var confirmBox = function(val, title, ok, no) {
        Alert.confirm(val, title, ok, no);
    };

    var bindTextData = function(data) {
        var t1 = (!data.gauge.threshold1) ? 0 : data.gauge.threshold1;
        var t2 = (!data.gauge.threshold2) ? 0 : data.gauge.threshold2;
        var t3 = (!data.gauge.threshold3) ? 0 : data.gauge.threshold3;
        $ELEMENTS.thresholdGood.val(t1);
        $ELEMENTS.thresholdWarning.val(t2);
        $ELEMENTS.thresholdCritical.val(t3);

        $('#lastAmount').val(data.gauge.lastAmount);
        $('#lastPercent').val(data.gauge.lastPercent);
        $('#contractEnergy').val(Utils.addCommas(data.gauge.capacity));
        $('#useAmount').val(data.gauge.lastAmount);
        $('#basisDate').html(data.currTime);
    };

    var __createTag = function(tagOption) {
        if(!tagOption) return;
        return $(
            "<li><input type='hidden' name='tag' value='" + 
            tagOption.value + "'>" +
            "<label id='" + tagOption.id + 
            "' value='" + tagOption.value + 
            "' title='" + tagOption.title + "'>"+
            tagOption.descr + "</label></li>"
        );        
    };

    var renderTagList = function(tagListOptions) {
        var $p = $ELEMENTS.tagUList.empty();
        if(!tagListOptions || !Utils.isArray(tagListOptions)) {           
            return;
        }        
        var len = tagListOptions.length || ((tagListOptions.size) ? tagListOptions.size() : 0);
        for(var i=0;i<len;i++) {
            var t = __createTag(tagListOptions[i]);
            if(t) {
                $p.append(t);
            }
        }
    };

    var showTagLayer = function() {
        $ELEMENTS.tagLayer.find(".tags select option:even").addClass("even");
        $ELEMENTS.tagLayer.css({
            "top": $ELEMENTS.DRDetailScenarioForm.offset().top + "px",
            "left": $ELEMENTS.DRDetailScenarioForm.offset().left + "px",
            "width": $ELEMENTS.DRDetailScenarioForm.width() + "px",
            "height": $ELEMENTS.DRDetailScenarioForm.height() + "px",
            "max-height": '350px'
        });
        $ELEMENTS.tagLayer.show('normal');  
    };

    var hideTagLayer = function() {
        $ELEMENTS.tagLayer.hide('normal');
    };

    var moveOptions = function(arrive, depart) {
        var selected = $ELEMENTS.tagLayer.find("." + arrive + " option:selected");
        $ELEMENTS.tagLayer.find("." + depart + " select").append(selected);
        if(selected) {
            selected.attr("selected", false);
            $ELEMENTS.tagLayer.find(".tags select option").removeClass("even")
                .filter(":even").addClass("even");
        }
    };

	var renderGuageChart = function(data, callback) {

		var guageDivWidth  = $('#guageChartDiv').width();

		var threshold1 = data.gauge.threshold1;
        var threshold2 = data.gauge.threshold2;
        var threshold3 = data.gauge.threshold3;
        var lastAmount = data.gauge.lastAmount;
        var lastPercent = data.gauge.lastPercent;
        var contractEnergy = data.gauge.capacity;
        var useAmount = data.gauge.lastAmount;

 		var guageChartDataXml = 
        	" <chart lowerLimit='0' upperLimit='100' showGaugeBorder='0' " + 
            " gaugeOuterRadius='100%' gaugeInnerRadius='60%' pivotRadius='2' " + 
            " lowerLimitDisplay='0%' "  +
            " gaugeStartAngle='180' gaugeEndAngle='0' " + 
            " palette='5' numberSuffix='%' tickValueDistance='20' " +
            " showValue='1' paletteThemeColor='575757' " +
            " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' showBorder='0' " + 
            " showPivotBorder='1' pivotBorderThickness='3' pivotBorderColor='CCCCCC' " +
            " pivotBorderAlpha='0' > " + 
            " <colorRange>" + 
            " <color minValue='0' maxValue='"+Number(threshold2-1)+"' code=' " +  
            fChartColor_Step3[0] +" '/> " + 
            " <color minValue='"+Number(threshold2) + 
            "' maxValue='"+Number(threshold3-1)+"' code=' " + 
            fChartColor_Step3[1] +" '/> " + 
            " <color minValue='"+Number(threshold3-1)+"' maxValue='100' code='" + 
            fChartColor_Step3[2] +" '/> " +
            " </colorRange> " + 
            " <dials>" + 
            " <dial value='"+lastPercent+"' rearExtension='10'/> " + 
            " </dials>" + 
            " </chart>";

        guageChart = Chart.renderByXML(
            "AngularGauge",
            {
                renderId: "guageChartDiv",
                chartId: "guageChart",
                width: guageDivWidth,
                height: "120"
            },
            guageChartDataXml
        );
        if(Utils.isFunction(callback)) callback(guageChart);
        return guageChart;        
	};

	var renderBarChart = function(list, callback) {
        var columnDivWidth = $('#columnChartDiv').width();

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
    	 	I18N["aimir.facilityMgmt.powerConsumption"] +"(kW)' " + 
            "color='" + fChartColor_Elec[0] +" '>";
    	var percent = 
            "<dataset seriesName='Peak(%)' parentYAxis='S' " + 
            "lineThickness='3' color='"+ fChartColor_CO2[0] +" ' >";     

        for(var index in list) {
            if(list.hasOwnProperty(index)) {
                var hh = $.trim(list[index].hh);
                // 시간 문자열에서 :00 을 제거한다.
                if(hh) {
                    hh = hh.replace(/\:00$/, '');
                }
                categories += "<category label='"+hh+"'/>";
                amount     += "<set value='"+list[index].amount+"'/>";
                percent    += "<set value='"+list[index].percent+"'/>"; 
            }
        }
        categories += "</categories>";
        amount     += "</dataset>";
        percent    += "</dataset>"; 
         
        columnChartDataXml += categories + amount + percent + "</chart>";

        columnChart = Chart.renderByXML(
            "StackedColumn3DLineDY",
            {
                renderId: "columnChartDiv",
                chartId: "columnChart",
                width: columnDivWidth,
                height: "200"
            },
            columnChartDataXml
        );
        if(Utils.isFunction(callback)) callback(columnChart);
        return columnChart;
	};

    // 경고 위험 수준 팝업 보이기
    var lvInfoPopupControll = function() {         
        if (Number($('#lastPercent').val()) >= Number($('#threshold2').val())) {
            if (Number($('#lastPercent').val()) >= Number($('#threshold3').val())) {
                $ELEMENTS.levelInfoPopup.show("fast");
                $("#lv_danger").show("fast");
                $("#lv_warn").hide("fast");
            } else{
                $ELEMENTS.levelInfoPopup.show("fast");
                $("#lv_danger").hide("fast");
                $("#lv_warn").show("fast");
            }
        } 
        else{
            $ELEMENTS.levelInfoPopup.hide("fast");
        }           
    }

    //위험(경고) 수준 information light on-off controll : button event 
    var lvInfoLightControll = function() {
        if (Number($('#lastPercent').val()) >= Number($('#threshold2').val())) {
            if (Number($('#lastPercent').val()) >= Number($('#threshold3').val())) {
                $("#lamp_danger_on").show('fast');
                $("#lamp_danger_off").hide('fast');
                $("#lamp_warn_off").show('fast');
                $("#lamp_warn_on").hide('fast');
            } else{
                $("#lamp_warn_on").show('fast');
                $("#lamp_warn_off").hide('fast');
                $("#lamp_danger_off").show('fast');
                $("#lamp_danger_on").hide('fast');
            }       
        } 
        else {
            $("#lamp_danger_off").show('fast');
            $("#lamp_warn_off").show('fast');
            $("#lamp_danger_on").hide('fast');
            $("#lamp_warn_on").hide('fast');
        }                       
    };

    var selectSettingScenario = function(settings) {
        $(".dr-config-table td select.dr-scenario-selector").each(function() {
            var id = this.id.split("-")[0].toUpperCase();
            if(settings[id] && settings[id].isAction === 'true' && settings[id].scenario) {
                var scId = settings[id].scenario.id;
                if(scId) {
                    $(this).val(scId).selectbox();
                    return;
                }                
            }
           $(this).setText(EMPTY_TEXT);
           $(this).selectbox();
        });
    };

    var renderDRScenarioSelectBox = function(scenarios) {
        scenarios.push({
            id: "",
            name: EMPTY_TEXT
        })
        $ELEMENTS.drScenarioSelectboxes.pureSelect(scenarios);
        $ELEMENTS.drScenarioSelectboxes.selectbox();       
    };

    var renderPeekDemandHistoryGrid = function(params) {
        var conf = {
            id: params.gridId,
            store: params.store,
            autoHeight: true,
            listeners: params.listeners || {},
            columns: [
                {
                    header: I18N["aimir.number"],
                    align: 'center',
                    width: 30,
                    dataIndex: "id"
                },
                {
                    header: I18N["aimir.severity"],
                    align: 'center',
                    width: 70,
                    dataIndex: "severity"
                },
                {
                    header: I18N["aimir.location"],
                    align: 'center',
                    width: 60,
                    dataIndex: "location"
                },
                {
                    header: I18N["aimir.message"],
                    width: 150,
                    dataIndex: "message"
                },
                {
                    header: I18N["aimir.status"],
                    width: 50,
                    align: 'center',
                    dataIndex: "status"
                },
                {
                    header: I18N["aimir.opentime"],
                    width: 120,
                    align: 'left',
                    renderer: Utils.falseValueToEmpty,
                    dataIndex: "openTime"
                },
                {
                    header: I18N["aimir.closetime"],
                    width: 120,
                    align:'left',
                    renderer: Utils.falseValueToEmpty,
                    dataIndex: "closeTime"
                },
                {
                    header: I18N["aimir.duration"],
                    width: 120,
                    align:'left',
                    renderer: Utils.falseValueToEmpty,
                    dataIndex: "duration"
                }
            ],
            bbar: new PagingToolbar({
                pageSize: params.store.pageSize,
                displayInfo: true,
                displayMsg: toolbarPageFormat,
                store: params.store
            })
        };
        conf.renderTo = 'peekDemandLogs';
        return Grid.render(conf);
    };

    var renderDRGrid = function(params) {
        var conf = {
            id: params.gridId,
            store: params.store,
            columns: [
                {
                    header: I18N["aimir.number"],
                    align: 'center',
                    width: 50,
                    dataIndex: "id"
                },
                {
                    header: I18N["aimir.bems.view.DRScenario"],
                    align: 'left',
                    width: 90,
                    dataIndex: "senarioName"
                },
                {
                    header: I18N["aimir.execute"] + I18N['aimir.hour'],
                    align: 'left',
                    width: 100,
                    dataIndex: "runTime"
                },
                {
                    header: I18N["aimir.buildingMgmt.grade"],
                    align: 'center',
                    width: 90,
                    dataIndex: "level"
                },
                {
                    header: I18N["aimir.result"],
                    width: 100,
                    align: 'center',
                    dataIndex: "result"
                }
            ],
            bbar: new PagingToolbar({
                pageSize: params.store.pageSize,
                displayInfo: true,
                displayMsg: toolbarPageFormat,
                store: params.store
            })
        };
        conf.renderTo = 'drLogs';
        return Grid.render(conf);
    };

    var renderDRScenarioGrid = function(params) {
        var conf = {
            id: params.gridId,
            store: params.store,
            columns: [
                {
                    header: I18N["aimir.number"],
                    align: 'center',
                    width: 50,
                    dataIndex: "id"
                },
                {
                    header: I18N["aimir.bems.view.DRScenario"],
                    align: 'left',
                    width: 120,
                    dataIndex: "name"
                },
                {
                    header: I18N["aimir.location"],
                    align: 'left',
                    width: 100,
                    dataIndex: "contractCapacity.contractLocations"
                },
                {
                    header: I18N["aimir.contract.demand.amount"] + "("+I18N['aimir.unit.kwh']+")",
                    width: 100,
                    align: 'right',
                    dataIndex: "contractCapacity.capacity"
                },
                {
                    header: I18N["aimir.tag"],
                    width: 150,
                    align: 'left',
                    dataIndex: "target"
                }
            ],
            bbar: new PagingToolbar({
                pageSize: params.store.pageSize,
                displayInfo: true,
                displayMsg: toolbarPageFormat,
                store: params.store
            })
        };
        conf.renderTo = 'dr-scenario-grid';
        return Grid.render(conf);
    };
  
    function changeFormMode(mode, $frm) {          
        var $fmode = $frm.find(".fmode").addClass("absolute-hidden");
        $frm.find("." + mode).removeClass("absolute-hidden");
        $frm._$$editMode = mode;          
    };

    /** layer popup */
    function openAlert(e) {
        if(e) e.preventDefault();
        $('#popup').css('filter','alpha(opacity=30)');
        $ELEMENTS.levelInfoPopup.fadeIn('fast');
    };

    function closeAlert(e) {
        if(e) e.preventDefault();
        $ELEMENTS.levelInfoPopup.fadeOut('fast');
    };

    var showBottomLayer = function() {
        // 하단 영역을 보이게 한다.
        if($ELEMENTS.bottomWrapper.size() > 0) {
            $ELEMENTS.UILoadingIndicator.hide();
            $ELEMENTS.bottomWrapper.fadeIn();
        }
    };

    var renderPeakDemandDRTabs = function(option) {
        $ELEMENTS.peakDemandDRTabs.tabs(option);
    };

    // 날자 입력 탭 적용.
    // 탭 두개에 개별 적용한다.
    // Peak Demand 이력조회 탭에 적용
    var renderDateTab = function(supplierId) {        
        DateUtil.renderDateSelector({
            supplierId: supplierId,
            renderTo: $("#peek-demand-history .date-form"),
            isShowTab: true,
            daily: true,
            weekly: true,
            monthly: true,
            dayPeriod: true,
            seasonal: true,
            yearly: true,
            prefix: "pd_",
            callback: function($tabs, selectingTab) { 
                // 오늘 날자 표시
                DateUtil.getDate(function(r) {
                    $tabs.find("input[name=daily-day]").val(r.currDate);
                });
            }
        });

        // DR 수행 이력조회 탭에 적용
        DateUtil.renderDateSelector({
            supplierId: supplierId,
            renderTo: $("#dr-execute-history .date-form"),
            isShowTab: true,
            daily: true,
            weekly: true,
            monthly: true,
            dayPeriod: true,
            seasonal: true,
            yearly: true,
            prefix: "rd_",
            callback: function($tabs, selectingTab) {
                // 오늘 날자 표시
                DateUtil.getDate(function(r) {
                    $tabs.find("input[name=daily-day]").val(r.currDate);
                });
            }
        });
    };

    var initializeUI = function() {
        $ELEMENTS = {
            dateTypeList: $('#dateTypeList'), // 데이트탭 리스트
            thresholdGood: $('.threshold-good'), // 차트 임계치 안전 인풋
            thresholdWarning: $('.threshold-warning'), // 차트 임계치 경고 인풋
            thresholdCritical: $('.threshold-critical'), // 차트 임계치 위험 인풋
            levelInfoPopup: $("#lv_info_popup"), // 레이어 팝업            
            peakDemandDRTabs: $("#peek-demand-dr-history-tabs"), // 피크 수요 탭
            bottomWrapper: $("#bottom-wrapper"), //  하단 래퍼 영억
            UILoadingIndicator: $(".ui-loading-indicator"), // 하단 로딩 인디케이터
            drScenarioSelectboxes: $(".dr-scenario-selector"), // 시나리오 셀렉터 집합
            DRDetailScenarioForm: $("#dr-detail-scenario-form"), // DR시나리오 디테일 입력 폼
            scenarioActionForm: $("#dr-detail form[action='scenarioAction']"), // DR 시나리오 액션 폼
            tagListArea: $("#tag-list-area"), // 태그리스트
            editTagGuide: $("#tag-list-area div.click-guide"),
            tagUList: $(".tag-list-ulist"),
            contractCapacityId: $('#dateTypeList'), // 데이트탭 리스트
            tagLayer: $("#tag-layer-popup"), // 태그 레이어            
            tagTotalSelectbox: $("#tag-layer-popup .total-tags select"), // 모든 태그
            tagApplySelectbox: $("#tag-layer-popup .apply-tags select"), // 적용 태그
            FORM_BTN: {
                submits: $("form a.submit"),
                resets: $("form a.reset"),
                levelInfoPopupClose: $("#lv_info_popup li.close a"), // 레이어 팝업 닫기
                moveApplyTagButton: $("#tag-layer-popup .in-out-control li.out"),
                moveTotalTagButton: $("#tag-layer-popup .in-out-control li.in"),
                applyTagButton: $("#tag-layer-popup .in-out-control li.in-btn"),
                cancelTagButton: $("#tag-layer-popup .in-out-control li.out-btn"),
                applyScenarioButton: $("#dr-config .dr-config-table .submit"),
                disableScenarioButton: $("#dr-config .dr-config-table .disable")
            },
            THRESHOLDS: {
                good: $('#threshold1'), // 차트 임계치 안전 인풋
                warning: $('#threshold2'), // 차트 임계치 경고 인풋
                critical: $('#threshold3') // 차트 임계치 위험 인풋
            }
        };  

        // 하단 레이아웃이 깨지지 않게 감추고 완전히 로드되기 전까지 사용자에게 알려줄 
        // 로딩 인디케이터를 보여준다.
        if($ELEMENTS.bottomWrapper.size() > 0) {
            $ELEMENTS.UILoadingIndicator.show();
            $ELEMENTS.bottomWrapper.hide();      
        }      

        return $ELEMENTS;
    };
	
	return {        
        initializeUI: initializeUI,        
        FORMMODE: FORMMODE,
        changeFormMode: changeFormMode,
        showBottomLayer: showBottomLayer,
        moveOptions: moveOptions,
        renderDateTab: renderDateTab,
        showTagLayer: showTagLayer,
        hideTagLayer: hideTagLayer,
        renderTagList: renderTagList,
        renderPeakDemandDRTabs: renderPeakDemandDRTabs,
        renderDRScenarioSelectBox: renderDRScenarioSelectBox,
        selectSettingScenario: selectSettingScenario,
        locationTreeGoGo: Tree.locationTreeGoGo,
        bindTextData: bindTextData,
		renderGuageChart: renderGuageChart,
		renderBarChart: renderBarChart,
        lvInfoPopupControll: lvInfoPopupControll,
        lvInfoLightControll: lvInfoLightControll,
        renderPeekDemandHistoryGrid: renderPeekDemandHistoryGrid,        
        renderDRGrid: renderDRGrid,
        renderDRScenarioGrid: renderDRScenarioGrid,
        openAlert: openAlert,
        closeAlert: closeAlert,
		messageBox: messageBox,
        confirmBox: confirmBox
	};
	
});