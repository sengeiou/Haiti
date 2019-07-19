/**
// @Usage
DateUtil.renderDateSelector({
    supplierId: operator.supplierId, // 공급자 아이디
    renderTo: $("#peek-demand-history .date-form"), // 그릴 영역
    isShowTab: true, // 탭 버튼을 표시한다. (옵션)
    hourly: true, // 시간 검색탭을 표시한다. (옵션)
    daily: true, // 날자별 검색탭을 표시한다. (옵션)
    weekly: true, // 주별 검색탭을 표시한다. (옵션)
    monthly: true, // 월별 검색탭을 표시한다. (옵션)
    dayPeriod: true, // 날자기간 검색탭을 표시한다. (옵션)
    monthlyPeriod: true, // 월별시간 검색탭을 표시한다. (옵션)
    weekdaily: true, // 요일별 검색탭을 표시한다. (옵션)
    seasonal: true, // 계절별 검색탭을 표시한다. (옵션)
    yearly: true, // 연별 검색탭을 표시한다. (옵션)
    prefix: "pd_", // 탭 아이디 프리픽스를 지정한다. (옵션)
    callback: function($tabs) { } // 렌더링 완료 후 호출 될 함수를 지정한다. (옵션)
});
 */
require([ "jquery" ], function(j) {
define([ 
    "framework/Config/CommonConstants",
    GLOBAL_CONTEXT.CONTEXT + "/common/i18nDateLocaleMessage.do",
    "framework/Util/ObjectUtils",
], function(CONST, DateLocale, Util) {
	
    var tempIdSequence = 1;    
    var prefix = 'jquery_date_inputs_';

	// Scope Optimize
	var $ = j;
	
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
    var I18N = $.extend(DateLocale, CONTEXT.I18N);
	var selfParentPath = CONTEXT + "/js/framework/Util/";
    var templateFile = "datepickerTemplete.html";
	
	var locDateFormat = "yymmdd";
    var supplierId = '1';

    var cache = {
   		template: {}
    };
    
    var WeekDayString = {
        "1": I18N["aimir.quartz.week.sun"],
        "2": I18N["aimir.quartz.week.mon"],
        "3": I18N["aimir.quartz.week.tue"],
        "4": I18N["aimir.quartz.week.wed"],
        "5": I18N["aimir.quartz.week.thu"],
        "6": I18N["aimir.quartz.week.fri"],
        "7": I18N["aimir.quartz.week.sat"]
    };

    var SeasonString = {
        Spring: I18N["aimir.spring"],
        Summer: I18N["aimir.summer"],
        Autumn: I18N["aimir.autumn"],
        Winter: I18N["aimir.winter"]
    }

    var setSupplierId = function(val) {
    	supplierId = val;
    };
    
    var setDateFormat = function(val) {
    	locDateFormat = val;
    };

    var hoursLabel = (function() {
        var arr = [];
        for(var i = 0;i<=23;i++) {
            arr.push(i<10?'0'+i:i+'');
        }
        return arr;
    })();

    var getSequenceValue = (function(start, prefix) {
        var i = tempIdSequence;
        return function() {
            return prefix + (i++);
        };
    })(tempIdSequence, prefix);
    
    var getCurrentYear = function(callback) {
    	$.getJSON(
    		CONTEXT + "/common/getYear.do",
    		{supplierId: supplierId},
    		callback
    	);
    };
    
    var modifyDate = function(rawDate, callback){
        $.getJSON(
       		CONTEXT + "/common/convertLocalDate.do",
            {
       			dbDate: rawDate, 
       			supplierId: supplierId
       		},
       		callback);
    };
    
    var convertRawDates = function(localeDate, callback) {
        $.getJSON(
        	CONTEXT + "/common/convertSearchDate.do",
        	{
        		searchStartDate: localeDate[0], 
        		searchEndDate: localeDate[1], 
        		supplierId: supplierId
        	},
        	callback);
    };

    var getDate = function(callback) {
        $.getJSON(
            CONTEXT + "/common/getDate.do",
            {
                searchDate: '',
                addVal: '', 
                supplierId: supplierId
            }, callback);
    };

    var dailyCalc = function(localeDate, val, callback) {
        $.getJSON(
        	CONTEXT + "/common/getDate.do",
        	{
        		searchDate: localeDate,
        		addVal: val, 
        		supplierId: supplierId
        	}, callback);
    };

    var monthlyCalc = function(year, month, val, callback) {
        $.getJSON(
       		CONTEXT + "/common/getYearMonth.do",
       		{
       			year: year,
       			month: month,
       			addVal:val
       		}, callback);

    };

    var period = function(val, callback) {
        $.getJSON(
        	CONTEXT + "/common/getDate.do",
        	{
        		searchDate: '',
        		addVal: val, 
        		supplierId: supplierId
        	}, callback);
    };

    var remainMonth = function(year, callback) {
        $.getJSON(
       		CONTEXT + "/common/getMonth.do",
       		{
       			year: year
       		}, callback);
    };

    var monthPeriod = function(year, month, callback) {
        $.getJSON(
            CONTEXT + "/common/getMonthPeriod.do",
            {
                year: year,
                month: month,
                supplierId: supplierId
            }, callback);
    }

    var weekCountByMonth = function(year, month, callback) {
        $.getJSON(
        	CONTEXT + "/common/getWeek.do",
        	{
        		year: year,
        		month: month
        	}, callback);
    };

    var weekDailyWeekDay = function(year, month, week, callback){
    	$.getJSON(
        	CONTEXT + "/common/getWeekDay.do",
        	{
        		year: year,
        		month: month,
        		week: week
        	}, callback);
    };

    var weekPeriodDay = function(year, month, week, callback){
        $.getJSON(
            CONTEXT + "/common/getWeekPeriod.do",
            {
                year: year,
                month: month,
                week: week,
                supplierId: supplierId
            }, callback);
    };

    var weekDayPeriod = function(year, month, week, weekDay, callback){
        $.getJSON(
            CONTEXT + "/common/getWeekDayPeriod.do",
            {
                year: year,
                month: month,
                week: week,
                weekDay: weekDay,
                supplierId: supplierId
            }, callback);
    };

    var yearPeriod = function(year, callback) {
        $.getJSON(
            CONTEXT + "/common/getYearPeriod.do",
            {
                year: year,
                supplierId: supplierId
            }, callback);
    };

    var seasonalSeason = function(year, callback) {
    	$.getJSON(
        	CONTEXT + "/common/getSeason.do",
        	{ year: year }, callback);
    };
	
    
    // 전달된 돔 엘리먼트에 datepicker를 적용한다.
    var applyDatePicker = function($dom) {
    	$dom.datepicker({
    		maxDate:'+0m',
    		showOn: 'button', 
    		buttonImage: CONTEXT + '/themes/images/default/setting/calendar.gif', 
    		buttonImageOnly: true, 
    		dateFormat:locDateFormat, 
    		onSelect: function(dateText, inst) { 
    			var i = this;
    			modifyDate(dateText, function(res) {
    				$(i).val(res.localDate);
    			});
    		}
    	});
    };

    var renderHourly = function($el, option) {
        var $sin = $el.find("input[name=hourly-sdate]");
        var $ein = $el.find("input[name=hourly-edate]");
        applyDatePicker($sin);
        applyDatePicker($ein);

        var $sh = $el.find("select[name=hourly-shour]");
        var $eh = $el.find("select[name=hourly-ehour]");
        $sh.numericOptions({from:0,to:23,selectedIndex:0,labels:hoursLabel});
        $eh.numericOptions({from:0,to:23,selectedIndex:23,labels:hoursLabel});
        $sh.selectbox();
        $eh.selectbox();     
    };

    var renderWeekly = function($el, option) {
        var $ySel = $el.find("select[name=weekly-year]");
        var $mSel = $el.find("select[name=weekly-month]");
        var $wSel = $el.find("select[name=weekly-week]");
        renderYearCombo($ySel, function() {

            // 폼 값 변경 시 월 셀렉트 박스를 새로 그린다
            $ySel.change(function(e) {
                renderMonthCombo($(this).val(), $mSel, function(v) {
                    $mSel.val(v.monthCount);
                    $mSel.selectbox();

                    $mSel.change(function(e) {                        
                        renderWeekCombo($ySel.val(), $(this).val(), $wSel);                                                
                    });

                    $mSel.trigger("change");                    
                });
            });
            
            $ySel.trigger("change");
        });
    };
    
    var renderDaliy = function($el, option) {
        var $in = $el.find("input[name=daily-day]");
        $el.find(".back").click(function() {
            dailyCalc($in.val(), -1, function(v) {
                $in.val(v.searchDate);
            });
        });
        $el.find(".next").click(function() {
            dailyCalc($in.val(), 1, function(v) {
                $in.val(v.searchDate);
            });
        });        
        applyDatePicker($in);
    };

    var renderDayPeriod = function($el, option) {
        var $sin = $el.find("input[name=period-sdate]");
        var $ein = $el.find("input[name=period-edate]");
        applyDatePicker($sin);
        applyDatePicker($ein);
    };

    var renderMonthlyPeriod = function($el, option) {
        var $sySel = $el.find("select[name=monthlyPeriod-syear]");
        var $smSel = $el.find("select[name=monthlyPeriod-smonth]");
        var $eySel = $el.find("select[name=monthlyPeriod-eyear]");
        var $emSel = $el.find("select[name=monthlyPeriod-emonth]");
        renderYearCombo($sySel, function() {
            $sySel.change(function(e) {
                renderMonthCombo($(this).val(), $smSel, function(v) {
                    $smSel.val(v.monthCount);
                    $smSel.selectbox();
                });
            });

            $sySel.trigger("change");
        });
        renderYearCombo($eySel, function() {          
            $eySel.change(function(e) {
                renderMonthCombo($(this).val(), $emSel, function(v) {
                    $emSel.val(v.monthCount);
                    $emSel.selectbox();
                });
            });

            $eySel.trigger("change");
        });
    };

    var renderWeekdaily = function($el, option) {
        var $ySel = $el.find("select[name=weekdaily-year]");
        var $mSel = $el.find("select[name=weekdaily-month]");
        var $wSel = $el.find("select[name=weekdaily-week]");
        var $dSel = $el.find("select[name=weekdaily-weekday]");
        renderYearCombo($ySel, function() {

            // 폼 값 변경 시 월 셀렉트 박스를 새로 그린다
            $ySel.change(function(e) {
                renderMonthCombo($(this).val(), $mSel, function(v) {
                    $mSel.val(v.monthCount);
                    $mSel.selectbox();

                    $mSel.change(function(e) {                        
                        renderWeekCombo($ySel.val(), $(this).val(), $wSel, function(v) {
                            $wSel.val(v.weekCount);
                            $wSel.selectbox();

                            $wSel.change(function(e) {
                                renderWeekdayCombo($ySel.val(),$mSel.val(),$wSel.val(), $dSel);
                            });

                            $wSel.trigger("change");
                        });                                                
                    });

                    $mSel.trigger("change");                    
                });
            });
            
            $ySel.trigger("change");
        });
    };

    var renderMonthly = function($el, option) {
        var $ySel = $el.find("select[name=monthly-year]");
        var $mSel = $el.find("select[name=monthly-month]");
        renderYearCombo($ySel, function() {

            // 화살표 버튼 이벤트
            $el.find(".back").click(function() {
                monthlyCalc($ySel.val(), $mSel.val(), -1, function(v) {
                    $ySel.val(v.year);
                    $ySel.selectbox();
                    renderMonthCombo(v.year, $mSel, function() {
                        $mSel.val(v.month);
                        $mSel.selectbox();
                    });
                });
            });
            $el.find(".next").click(function() {
                monthlyCalc($ySel.val(), $mSel.val(), 1, function(v) {
                    $ySel.val(v.year);
                    $ySel.selectbox();
                    renderMonthCombo(v.year, $mSel, function() {
                        $mSel.val(v.month);
                        $mSel.selectbox();
                    });
                });
            });

            // 폼 값 변경 시 월 셀렉트 박스를 새로 그린다
            $ySel.change(function(e) {
                renderMonthCombo($(this).val(), $mSel, function(v) {
                    $mSel.val(v.monthCount);
                    $mSel.selectbox();
                });
            });

            $ySel.trigger("change");
        });
    };

    var renderSeason = function($el, option) {
        var $ySel = $el.find("select[name=seasonal-year]");
        var $sSel = $el.find("select[name=seasonal-value]");
        renderYearCombo($ySel, function() {

            // 폼 값 변경 시 월 셀렉트 박스를 새로 그린다
            $ySel.change(function(e) {
                var seasonIndex = {};
                renderSeasonCombo($(this).val(), $sSel);
            });

            $ySel.trigger("change");
        });
    };

    var renderYear = function($el, option) {
        var $ySel = $el.find("select[name=yearly-year]");
        renderYearCombo($ySel);
    };

    var renderYearCombo = function($sel, callback) {
        getCurrentYear(function(r) {
            var startYear = r.year; 
            var endYear = r.currYear;
            var currDate = r.currDate;

            $sel.numericOptions({
                from: startYear,
                to: endYear,
                selectedIndex: 9
            });
            $sel.selectbox();         

            if(Util.isFunction(callback)) {
                callback(r);
            }
        });
    }

    var renderMonthCombo = function(year, $sel, callback) {
        remainMonth(year, function(r) {
            $sel.emptySelect();
            $sel.numericOptions({
                from:1,
                to:r.monthCount,
                selectedIndex: 0
            });            
            $sel.selectbox();

            if(Util.isFunction(callback)) {
                callback(r);
            }
        });
    }

    var renderWeekCombo = function(year, month, $sel, callback) {
        weekCountByMonth(year, month, function(r) {
            $sel.emptySelect();
            $sel.numericOptions({
                from:1,
                to:r.weekCount,
                selectedIndex: 0
            });
            $sel.selectbox();

            if(Util.isFunction(callback)) {
                callback(r);
            }
        });
    }

    var renderWeekdayCombo = function(year, month, week, $sel, callback) {
        weekDailyWeekDay(year, month, week, function(r) {
            $sel.emptySelect();
            $sel.numericOptions({
                from:r.startWeek,
                to:r.endWeek,
                selectedIndex: 0
            });
            $sel.find("option").each(function() {
                $(this).text(WeekDayString[$(this).val()]);
            });
            $sel.selectbox();

            if(Util.isFunction(callback)) {
                callback(r);
            }
        });
    }

    // XXX: 비효율적이다...
    var renderSeasonCombo = function(year, $sel, callback) {
        seasonalSeason(year, function(r) {
            $sel.emptySelect();
            var Season = CONST.Season;
            for(var k in Season) {
                if(Season.hasOwnProperty(k)) {                    
                    var seasonKey;
                    var S = Season[k];
                    if(S === r.Spring.name) seasonKey = "Spring";
                    else if(S === r.Summer.name) seasonKey = "Summer";
                    else if(S === r.Autumn.name) seasonKey = "Autumn";
                    else if(S === r.Winter.name) seasonKey = "Winter";
                    else { continue; }
                    var sv = r[seasonKey];
                    if(sv) { 
                        $op = $("<option data-index="+sv.id+" value='"+
                            sv.startDate + "@" + sv.endDate
                            +"'>" +  
                            SeasonString[seasonKey]
                            + "</option>");
                        $sel.append($op);
                    }
                }
            }
            $sel.selectbox();
        })
    };

    /*
        로케일 메시지 프로퍼티 설정
     */
    var setLocaleMessage = function($content) {        
        $content.find("[data-fmt-message]").each(function(i) {
            $(this).text(I18N[$(this).attr("data-fmt-message")]);
        });  
    };

    var setTempId = function($content) {
        $content.find("input, select").each(
            function() {
                $(this).attr("id", getSequenceValue());
            }
        );
    };

    var renderTemplate = function($tabButtons, $tabItems, spec) {
        var $tabItem = $tabItems.filter(spec.tabSelector).show();                
        $tabButtons.filter(spec.buttonSelector).show();
        return spec.renderFunction($tabItem, spec.option);
    };
    
    var renderDateSelector = function(option) {
        supplierId = option.supplierId;

        // 템플릿 로딩 
        var u = selfParentPath + templateFile;
        var prefix = option.prefix || '';
        var tabType =  option.tabType || "buttontype";

        var $renderTo = option.renderTo;   

        $renderTo.load(u, function(a) {
            cache.template = a;

            var $tabContent = $renderTo.find("div.dynamic-date-tabs");                       
            $tabContent.find("> ul:not(."+tabType + ")").remove();

            var $tabMenu = $tabContent.find(".date-tab-menu");
            var $tabButtons = $tabContent.find(".date-tab-menu li");
            var $tabItems = $tabContent.find(".tab-item");
            var $searchDateType = $renderTo.find("input[name=searchDateType]");
            
            // 탭 프리픽스 조정
            // 가령 프리픽스가 pre 면 모든 템플릿의 엘리먼트 아이디는 pre 가 붙는다.
            if(prefix) {
                $tabButtons.find("a[href]").each(function() {
                    var href = $(this).attr("href");
                    $(this).attr("href", "#" + prefix + href.substring(1));
                })
                $tabItems.each(function() {
                    var id = $(this).attr("id");
                    $(this).attr("id", prefix + id);
                })
            }

            $tabItems.hide();
            $tabButtons.hide();

            setLocaleMessage($tabContent);  
            setTempId($tabContent);    

            var selectingTab = undefined;

            if(option.hourly) {
                if(!selectingTab) selectingTab = "hourly";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-hourly",
                        buttonSelector: ".li-hourly",
                        renderFunction: renderHourly,
                        option: option.hourly
                    }
                );
            }
            if(option.daily) {
                if(!selectingTab) selectingTab = "daily";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-daily",
                        buttonSelector: ".li-daily",
                        renderFunction: renderDaliy,
                        option: option.daily
                    }
                );
            }
            if(option.weekly) {
                if(!selectingTab) selectingTab = "weekly";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-weekly",
                        buttonSelector: ".li-weekly",
                        renderFunction: renderWeekly,
                        option: option.weekly
                    }
                );
            }
            if(option.monthly) {
                if(!selectingTab) selectingTab = "monthly";
                // var $monthlyTabItem = $tabItems.filter("#" + prefix + "tab-monthly").show();
                // $tabButtons.filter(".li-monthly").show();
                // renderMonthly($monthlyTabItem, option.monthly);
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-monthly",
                        buttonSelector: ".li-monthly",
                        renderFunction: renderMonthly,
                        option: option.monthly
                    }
                );
            }
            if(option.dayPeriod) {
                if(!selectingTab) selectingTab = "dayPeriod";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-period",
                        buttonSelector: ".li-period",
                        renderFunction: renderDayPeriod,
                        option: option.dayPeriod
                    }
                );
            }
            if(option.weekdaily) {
                if(!selectingTab) selectingTab = "weekdaily";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-weekdaily",
                        buttonSelector: ".li-weekdaily",
                        renderFunction: renderWeekdaily,
                        option: option.weekdaily
                    }
                );
            }
            if(option.monthlyPeriod) {
                if(!selectingTab) selectingTab = "monthlyPeriod";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-monthlyPeriod",
                        buttonSelector: ".li-monthlyPeriod",
                        renderFunction: renderMonthlyPeriod,
                        option: option.monthlyPeriod
                    }
                );
            }
            if(option.seasonal) {
                if(!selectingTab) selectingTab = "seasonal";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-seasonal",
                        buttonSelector: ".li-seasonal",
                        renderFunction: renderSeason,
                        option: option.seasonal
                    }
                );
            }
            if(option.yearly) {
                if(!selectingTab) selectingTab = "yearly";
                renderTemplate(
                    $tabButtons, $tabItems,
                    {
                        tabSelector: "#" + prefix + "tab-yearly",
                        buttonSelector: ".li-yearly",
                        renderFunction: renderYear,
                        option: option.yearly
                    }
                );
            }

            if(option.additionalCss) {
                $renderTo.css(option.additionalCss);
                if("background" in option.additionalCss) {
                    $tabContent.css(option.additionalCss);
                }
            }

            // 탭 표시여부
            // 탭을 표시하지 않으면 숨긴다.
            if(!option.isShowTab) {
                $tabMenu.hide();
                $tabItems.css("margin-top", "5px");
            }
            // 탭을 표시할 경우 jquery tab 플러그인을 사용한다.
            else {                
                $tabContent.tabs({
                    select: function(e, ui) {
                        if(ui.tab.parentNode) {
                            $searchDateType.val($(ui.tab.parentNode).attr("data-tabtype"));
                        }
                    }
                });

                // 보이는 탭을 선택한다.
                var firstVisible = $tabButtons.filter("[data-tabtype='"+selectingTab+"']")
                var visibleIndex = $tabButtons.index(firstVisible[0]);
                $tabContent.tabs("select", (visibleIndex) ? visibleIndex : 0);
                $searchDateType.val(selectingTab);

                // CSS 조정
                $tabItems.filter(":has(select)").addClass("adjust-margin-bottom");
            }

            $renderTo.append("<div style='clear: both;'></div>");

            // 작업 완료
            $tabContent.show();

            if(Util.isFunction(option.callback)) {
                option.callback($tabContent, selectingTab);
            }
        }); 

    };

    /**
        데이트피커가 포함된 jQuery 확장 폼을 받고,
     */
    var searchDateWithFormValues = function($frm, callback) {
        var params = Util.keyNamePareToObject($frm);
        var searchDateType = params.searchDateType;

        if(params.supplierId) supplierId = params.supplierId;
        params.searchDate = '';

        if(!Util.isFunction(callback)) {
            callback = function(params){}
        }
        if(!searchDateType) {   
            callback(params);
            return;
        }       
        // XXX: weeklyperiod 구현 필요
        switch (searchDateType)   {
            case "hourly": {
                var sh = params["hourly-shour"],
                    eh = params["hourly-ehour"],
                    sdate = params["hourly-sdate"],
                    edate = params["hourly-edate"];
                if(sh < 10) sh = "0" + sh;
                convertRawDates([params["hourly-sdate"], params["hourly-edate"], '' ], function(r) {
                    params.searchDate = 
                        r.searchStartDate + sh +
                        "@" + 
                        r.searchEndDate + eh;
                    callback(params);
                })
                break;
            }
            case "daily": {
                convertRawDates([params["daily-day"], params["daily-day"], '' ], function(r) {
                    params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                    callback(params);
                })
                break;
            }
            case "period": {
                convertRawDates([params["period-sdate"], params["period-edate"], '' ], function(r) {
                    params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                    callback(params);
                })
                break;
            }
            case "weekly": {
                weekPeriodDay(params["weekly-year"], params["weekly-month"], params["weekly-week"], function(r) {
                    convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                        params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                        callback(params);
                    });
                })
                break;
            }
            case "monthly": {
                monthPeriod(params["monthly-year"], params["monthly-month"], function(r) {
                    convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                        params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                        callback(params);
                    });
                })
                break;
            }
            case "weekdaily": {
                var year = params["weekdaily-year"],
                    month = params["weekdaily-month"],
                    week = params["weekdaily-week"],
                    weekday = params["weekdaily-weekday"];
                weekDayPeriod(
                    year, month, week, weekday,
                    function(r) {
                        convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                            params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                            callback(params);
                        });
                    }
                )
                break;
            }
            case "monthlyPeriod": {
                var sy = params["monthlyPeriod-syear"],
                    sm = params["monthlyPeriod-smonth"],
                    ey = params["monthlyPeriod-eyear"],
                    em = params["monthlyPeriod-emonth"];  
                    monthPeriod(sy, sm, function(r) {
                        convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                            params.searchDate = r.searchStartDate+"@";                            
                            monthPeriod(ey, em, function(r) {
                                convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                                    params.searchDate += r.searchEndDate;                            
                                    callback(params);
                                });
                            });
                        });
                    });
                break;
            }
            case "seasonal": {
                params.searchDate = params["seasonal-value"];
                callback(params);
                break;
            }
            case "yearly": {
                yearPeriod(params["yearly-year"], function(r) {
                    convertRawDates([r.startDate, r.endDate, '' ], function(r) {
                        params.searchDate = r.searchStartDate+"@"+r.searchEndDate;
                        callback(params);
                    });
                });
                break;
            }
        }
    };

 	return {
        renderDateSelector: renderDateSelector,
        applyDatePicker: applyDatePicker,
 		setSupplierId: setSupplierId,
 		setDateFormat: setDateFormat,
 		DateType: DateType, 	
        getDate: getDate,
        convertRawDates: convertRawDates,	
        convertLocaleDate: modifyDate,
        searchDateWithFormValues: searchDateWithFormValues
 	};
 	
}); });