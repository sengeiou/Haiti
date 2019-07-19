<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

	var SeasonalBtn = "false";
	var seasonData;
    var _supplierId='';
    $(document).ready(function(){
        commonDateTabButtonInterval = setInterval("commonDateTabButtonDelay()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId = json.supplierId;
                }
        );
    });

    function commonDateTabButtonDelay(){
        if(_supplierId != ''){
            clearInterval(commonDateTabButtonInterval);
            commonDateTabButtonInit();
          }
    }

    function commonDateTabButtonInit(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_hourly')        .bind('click',function(event) { $('#searchDateType').val(DateType.HOURLY        );getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#_daily')         .bind('click',function(event) { $('#searchDateType').val(DateType.DAILY         );getSearchDate(DateType.DAILY); } ); });
        $(function() { $('#_period')        .bind('click',function(event) { $('#searchDateType').val(DateType.PERIOD        );getSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#_weekly')        .bind('click',function(event) { $('#searchDateType').val(DateType.WEEKLY        );getSearchDate(DateType.WEEKLY); } ); });
        $(function() { $('#_monthly')       .bind('click',function(event) { $('#searchDateType').val(DateType.MONTHLY       );getSearchDate(DateType.MONTHLY); } ); });
        $(function() { $('#_monthlyPeriod') .bind('click',function(event) { $('#searchDateType').val(DateType.MONTHLYPERIOD );getSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#_weekdaily')     .bind('click',function(event) { $('#searchDateType').val(DateType.WEEKDAILY     );getSearchDate(DateType.WEEKDAILY); } ); });
        $(function() { $('#_seasonal')      .bind('click',function(event) { $('#searchDateType').val(DateType.SEASONAL      );getSearchDate(DateType.SEASONAL); } ); });
        $(function() { $('#_yearly')        .bind('click',function(event) { $('#searchDateType').val(DateType.YEARLY        );getSearchDate(DateType.YEARLY); } ); });

        //조회버튼클릭 이벤트 생성
        $(function() { $('#btnSearch').bind('click',function(event) { sendRequest($('#searchDateType').val()); } ); });

        $(function() { $('#hourlySearch')       .bind('click',function(event) { sendRequest(DateType.HOURLY      ); } ); });
        $(function() { $('#dailySearch')        .bind('click',function(event) { sendRequest(DateType.DAILY      ); } ); });
        $(function() { $('#periodSearch')       .bind('click',function(event) { sendRequest(DateType.PERIOD     ); } ); });
        $(function() { $('#weeklySearch')       .bind('click',function(event) { sendRequest(DateType.WEEKLY     ); } ); });
        $(function() { $('#monthlySearch')      .bind('click',function(event) { sendRequest(DateType.MONTHLY    ); } ); });
        $(function() { $('#monthlyPeriodSearch').bind('click',function(event) { sendRequest(DateType.MONTHLYPERIOD    ); } ); });
        $(function() { $('#weekDailySearch')    .bind('click',function(event) { sendRequest(DateType.WEEKDAILY  ); } ); });
		$(function() {
			$('#seasonalSearch').bind('click', function(event) {
				sendRequest(DateType.SEASONAL);
			});
		});
		$(function() {
			$('#yearlySearch').bind('click', function(event) {
				sendRequest(DateType.YEARLY);
			});
		});

		//기간별조회 버튼클릭 이벤트 생성
		$(function() {
			$('#periodType').bind('change', function(event) {
				var idx = $('#periodType').val();
				if (idx == 0) {
					setPeriod(0);
				} else if (idx == 1) {
					setPeriod(-1);
				} else if (idx == 2) {
					setPeriod(-2);
				} else if (idx == 3) {
					setPeriod(-6);
				} else if (idx == 4) {
					setPeriodOneMonth(-30);
				}

			});
		});

		//$(function() { $('#todayBtn')       .bind('click',function(event) { setPeriod(0); } ); });
		//$(function() { $('#yesterdayBtn')   .bind('click',function(event) { setPeriod(-1); } ); });
		//$(function() { $('#threeDayBtn')    .bind('click',function(event) { setPeriod(-2); } ); });
		//$(function() { $('#weekBtn')        .bind('click',function(event) { setPeriod(-6); } ); });
		//$(function() { $('#monthBtn')       .bind('click',function(event) { setPeriod(-29); } ); });

		//일자별,기간별 날짜입력창 변경시
		$(function() {
			$('#dailyStartDate').bind('change', function(event) {
				getSearchDate(DateType.DAILY);
			});
		});
		$(function() {
			$('#periodStartDate').bind('change', function(event) {
				getSearchDate(DateType.PERIOD);
			});
		});
		$(function() {
			$('#periodEndDate').bind('change', function(event) {
				getSearchDate(DateType.PERIOD);
			});
		});
		$(function() {
			$('#hourlyStartDate').bind('change', function(event) {
				getSearchDate(DateType.HOURLY);
			});
		});
		$(function() {
			$('#hourlyEndDate').bind('change', function(event) {
				getSearchDate(DateType.HOURLY);
			});
		});
		$(function() {
			$('#hourlyStartHourCombo').bind('change', function(event) {
				getSearchDate(DateType.HOURLY);
			});
		});
		$(function() {
			$('#hourlyEndHourCombo').bind('change', function(event) {
				getSearchDate(DateType.HOURLY);
			});
		});

		//일자별,월별 좌우 화살표클릭 이벤트 생성
		$(function() {
			$('#dailyLeft').bind('click', function(event) {
				dailyArrow($('#dailyStartDate').val(), -1);
			});
		});
		$(function() {
			$('#dailyRight').bind('click', function(event) {
				dailyArrow($('#dailyStartDate').val(), 1);
			});
		});
		$(function() {
			$('#monthlyLeft').bind('click', function(event) {
				monthlyArrow(-1);
			});
		});
		$(function() {
			$('#monthlyRight').bind('click', function(event) {
				monthlyArrow(1);
			});
		});

		//주별 연도,월 콤보 체인지이벤트 생성
		$(function() {
			$('#weeklyYearCombo').bind('change', function(event) {
				getWeeklyMonthCombo();
			});
		});
		$(function() {
			$('#weeklyMonthCombo').bind('change', function(event) {
				getWeeklyWeekCombo();
			});
		});
		$(function() {
			$('#weeklyWeekCombo').bind('change', function(event) {
				getSearchDate(DateType.WEEKLY);
			});
		});

		//월별 연도콤보 체인지이벤트 생성
		$(function() {
			$('#monthlyYearCombo').bind('change', function(event) {
				getMonthlyMonthCombo();
			});
		});
		$(function() {
			$('#monthlyMonthCombo').bind('change', function(event) {
				getSearchDate(DateType.MONTHLY);
			});
		});

		//월별(기간) 년도,월콤보 체인지 이벤트
		$(function() {
			$('#monthlyPeriodStartYearCombo').bind('change', function(event) {
				getMonthlyPeriodStartMonthCombo();
			});
		});
		$(function() {
			$('#monthlyPeriodStartMonthCombo').bind('change', function(event) {
				getSearchDate(DateType.MONTHLYPERIOD);
			});
		});
		$(function() {
			$('#monthlyPeriodEndYearCombo').bind('change', function(event) {
				getMonthlyPeriodEndMonthCombo();
			});
		});
		$(function() {
			$('#monthlyPeriodEndMonthCombo').bind('change', function(event) {
				getSearchDate(DateType.MONTHLYPERIOD);
			});
		});

		//요일별 연도,월,주 콤보 체인지이벤트 생성
		$(function() {
			$('#weekDailyYearCombo').bind('change', function(event) {
				getWeekDailyMonthCombo();
			});
		});
		$(function() {
			$('#weekDailyMonthCombo').bind('change', function(event) {
				getWeekDailyWeekCombo();
			});
		});
		$(function() {
			$('#weekDailyWeekCombo').bind('change', function(event) {
				getWeekDailyWeekDayCombo();
			});
		});
		$(function() {
			$('#weekDailyWeekDayCombo').bind('change', function(event) {
				getSearchDate(DateType.WEEKDAILY);
			});
		});

		//계절별 연도,계절 콤보 체인지 이벤트 생성
		$(function() {
			$('#seasonalYearCombo').bind('change', function(event) {
				getSeasonalSeasonCombo();
			});
		});

		$(function() {
			$('#seasonalSeasonCombo').bind('change', function(event) {
				getSearchDate(DateType.SEASONAL);
			});
		});

		// Seasonal tab이 눌렸을 경우를 판별하는 함수	
		$(function() {
			$('#_seasonal').bind("click", function() {
				SeasonalBtn = "true";
			});

			$('#_weekdaily').bind("click", function() {
				SeasonalBtn = "false";
			});

			$('#_monthly').bind("click", function() {
				SeasonalBtn = "false";
			});

			$('#_weekly').bind("click", function() {
				SeasonalBtn = "false";
			});

			$('#_dailyl').bind("click", function() {
				SeasonalBtn = "false";
			});
		});

		$(function() {
			$('.am_button').bind("click", function() {
				if (SeasonalBtn == "true") {
					// $('#searchStartDate').val(null);
					// $('#searchEndDate').val(null);
					getCurrentDate();
					infoSeason();
				}
			});
		});

		function infoSeason() {
			var selectedSeason = $('#seasonalSeasonCombo').val();
			var info_Season = "";

			if (selectedSeason == 1) {
				info_Season = "Spring is from 3 to 5";
				$("#info_Season").text(info_Season)
			} else if (selectedSeason == 2) {
				info_Season = "Summer is from 6 to 8";
				$("#info_Season").text(info_Season)
			} else if (selectedSeason == 3) {
				info_Season = "Autumn is from 9 to 11";
				$("#info_Season").text(info_Season)
			} else if (selectedSeason == 4) {
				info_Season = "Winter is from 12 to 2";
				$("#info_Season").text(info_Season)
			} else if (selectedSeason == null) {
				//계절데이터가 없습니다.
				Ext.Msg
						.alert(
								"<fmt:message key='aimir.message'/>",
								'<div style="width: 150px"><fmt:message key="aimir.season.select.error"/></div>');
			} else {
				info_Season = "";
				$("#info_Season").text(info_Season)
			}
		}

		// 현재 날짜 가져오는 함수
		function getCurrentDate() {
			var today = new Date();
			var current_year = today.getFullYear();
			var current_month = today.getMonth() + 1;

			compareDate(current_year, current_month);
		}

		// 선택된 날짜와 현재 날짜 비교하는 함수
		function compareDate(current_year, current_month) {
			var selectedYear = $('#seasonalYearCombo').val();
			var selectedSeason = $('#seasonalSeasonCombo').val();
			var spring = 3;
			var summer = 6;
			var autumn = 9;
			var winter = 12;
			var noData = "0";
			
			/* 
			봄 : 3월 ~ 5월		
			여름  : 6월 ~ 8월
			가을  : 9월 ~ 11월
			겨울  : 12월 ~ 2월 	
			 */

			 if (selectedYear == current_year) {
				if(current_month == 12 || current_month == 1 || current_month == 2){
					$('#searchStartDate').val(noData);
					$('#searchEndDate').val(noData);
					Ext.Msg.alert("<fmt:message key='aimir.message'/>", "There is no data yet.");
				}
				if(current_month == 3 || current_month == 4 || current_month == 5){
					// 현재 월이 3,4,5월 일 때 여름(6월 ~ 8월),가을(9월 ~ 11월),겨울(12월 ~ 2월 ) season 데이터 조회 할 경우
					if(selectedSeason == 2 || selectedSeason == 3 || selectedSeason == 4){
						$('#searchStartDate').val(noData);
						$('#searchEndDate').val(noData);
						Ext.Msg.alert("<fmt:message key='aimir.message'/>", "There is no data yet.");
					}
				}
				if(current_month == 6 || current_month == 7 || current_month == 8){
					// 현재 월이 6,7,8월 일 때 가을(9월 ~ 11월),겨울(12월 ~ 2월 ) season 데이터 조회 할 경우
					if(selectedSeason == 3 || selectedSeason == 4){
						$('#searchStartDate').val(noData);
						$('#searchEndDate').val(noData);
						Ext.Msg.alert("<fmt:message key='aimir.message'/>", "There is no data yet.");
					}
				}
				if(current_month == 9 || current_month == 10 || current_month == 11){
					// 현재 월이 9,10,11월 일 때 겨울(12월 ~ 2월 ) season 데이터 조회 할 경우
					if(selectedSeason == 4){
						$('#searchStartDate').val(noData);
						$('#searchEndDate').val(noData);
						Ext.Msg.alert("<fmt:message key='aimir.message'/>", "There is no data yet.");
					}
				}
			}
		}

		//연별 연도콤보 체인지 이벤트
		$(function() {
			$('#yearlyYearCombo').bind('change', function(event) {
				getSearchDate(DateType.YEARLY);
			});
		});

		var locDateFormat = "yymmdd";

		//탭별 일자DatePicker 생성
		$("#hourlyStartDate").datepicker({
			maxDate : '+0m',
			showOn : 'button',
			buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
			buttonImageOnly : true,
			dateFormat : locDateFormat,
			onSelect : function(dateText, inst) {
				modifyDate(dateText, inst);
			}
		});
		$("#hourlyEndDate").datepicker({
			maxDate : '+0m',
			showOn : 'button',
			buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
			buttonImageOnly : true,
			dateFormat : locDateFormat,
			onSelect : function(dateText, inst) {
				modifyDate(dateText, inst);
			}
		});
		$("#dailyStartDate").datepicker({
			maxDate : '+0m',
			showOn : 'button',
			buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
			buttonImageOnly : true,
			dateFormat : locDateFormat,
			onSelect : function(dateText, inst) {
				modifyDate(dateText, inst);
			}
		});
		$("#periodStartDate").datepicker({
			maxDate : '+0m',
			showOn : 'button',
			buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
			buttonImageOnly : true,
			dateFormat : locDateFormat,
			onSelect : function(dateText, inst) {
				modifyDate(dateText, inst);
			}
		});
		$("#periodEndDate").datepicker({
			maxDate : '+0m',
			showOn : 'button',
			buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
			buttonImageOnly : true,
			dateFormat : locDateFormat,
			onSelect : function(dateText, inst) {
				modifyDate(dateText, inst);
			}
		});

		$.getJSON("${ctx}/common/getYear.do", {
			supplierId : _supplierId
		}, function(json) {
			var startYear = json.year;//currDate.getYear()-10;
			var endYear = json.currYear;//currDate.getYear();
			var currDate = json.currDate;

			if (tabs.hourly != 0) {
				$("#hourlyStartDate").val(currDate);
				$("#hourlyEndDate").val(currDate);

				var hours = new Array();
				for (var i = 0; i <= 23; i++) {
					hours[i] = i < 10 ? '0' + i : i + '';
				}
				$('#hourlyStartHourCombo').numericOptions({
					from : 0,
					to : 23,
					selectedIndex : 0,
					labels : hours
				});
				$('#hourlyEndHourCombo').numericOptions({
					from : 0,
					to : 23,
					selectedIndex : 23,
					labels : hours
				});
				$('#hourlyStartHourCombo').selectbox();
				$('#hourlyEndHourCombo').selectbox();
				getSearchDate(DateType.HOURLY);
			}
			//탭별 현재일자 설정
			if (tabs.daily != 0)
				dailyArrow('', 0, false);
			if (tabs.period != 0)
				setPeriod(0, false);

			if (tabs.weekly != 0) {
				$('#weeklyYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#weeklyYearCombo').selectbox();
				getWeeklyMonthCombo(false);
			}

			if (tabs.monthly != 0) {
				$('#monthlyYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#monthlyYearCombo').selectbox();
				getMonthlyMonthCombo("", false);
			}

			if (tabs.monthlyPeriod != 0) {
				$('#monthlyPeriodStartYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#monthlyPeriodEndYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});

				$('#monthlyPeriodStartYearCombo').selectbox();
				$('#monthlyPeriodEndYearCombo').selectbox();
				getMonthlyPeriodStartMonthCombo(false);
				getMonthlyPeriodEndMonthCombo(false);
			}

			if (tabs.weekDaily != 0) {
				$('#weekDailyYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#weekDailyYearCombo').selectbox();
				getWeekDailyMonthCombo(false);
			}

			if (tabs.seasonal != 0) {
				$('#seasonalYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#seasonalYearCombo').selectbox();
				getSeasonalSeasonCombo(false);
			}

			if (tabs.yearly != 0) {
				$('#yearlyYearCombo').numericOptions({
					from : startYear,
					to : endYear,
					selectedIndex : 9
				});
				$('#yearlyYearCombo').selectbox();
			}

		});

		var peroidLabels = new Array();
		peroidLabels[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
		peroidLabels[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
		peroidLabels[2] = '<fmt:message key="aimir.threedays"/>';//3일''
		peroidLabels[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
		peroidLabels[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

		$("#periodType").numericOptions({
			from : 0,
			to : 4,
			selectedIndex : 0,
			labels : peroidLabels
		});
		$("#periodType").selectbox();

		//탭생성
		$("#datetab").hide();
		$("#datetab").tabs();
		$("#datetab").show();

		//탭숨김처리
		if (tabs.yearly == 0)
			hideTab(DateType.YEARLY);
		if (tabs.seasonal == 0)
			hideTab(DateType.SEASONAL);
		if (tabs.weekDaily == 0)
			hideTab(DateType.WEEKDAILY);
		if (tabs.monthlyPeriod == 0)
			hideTab(DateType.MONTHLYPERIOD);
		if (tabs.monthly == 0)
			hideTab(DateType.MONTHLY);
		if (tabs.weekly == 0)
			hideTab(DateType.WEEKLY);
		if (tabs.period == 0)
			hideTab(DateType.PERIOD);
		if (tabs.daily == 0)
			hideTab(DateType.DAILY);
		if (tabs.hourly == 0)
			hideTab(DateType.HOURLY);

		if (tabNames.hourly != null && tabNames.hourly != ''
				&& tabNames.hourly != 'undefined')
			$('#_hourly').html(tabNames.hourly);
		if (tabNames.daily != null && tabNames.daily != ''
				&& tabNames.daily != 'undefined')
			$('#_daily').html(tabNames.daily);
		if (tabNames.period != null && tabNames.period != ''
				&& tabNames.period != 'undefined')
			$('#_period').html(tabNames.period);
		if (tabNames.weekly != null && tabNames.weekly != ''
				&& tabNames.weekly != 'undefined')
			$('#_weekly').html(tabNames.weekly);
		if (tabNames.monthly != null && tabNames.monthly != ''
				&& tabNames.monthly != 'undefined')
			$('#_monthly').html(tabNames.monthly);
		if (tabNames.monthlyPeriod != null && tabNames.monthlyPeriod != ''
				&& tabNames.monthlyPeriod != 'undefined')
			$('#_monthlyPeriod').html(tabNames.monthlyPeriod);
		if (tabNames.weekDaily != null && tabNames.weekDaily != ''
				&& tabNames.weekDaily != 'undefined')
			$('#_weekDaily').html(tabNames.weekDaily);
		if (tabNames.seasonal != null && tabNames.seasonal != ''
				&& tabNames.seasonal != 'undefined')
			$('#_seasonal').html(tabNames.seasonal);
		if (tabNames.yearly != null && tabNames.yearly != ''
				&& tabNames.yearly != 'undefined')
			$('#_yearly').html(tabNames.yearly);

		// 검색버튼 표시
		if (tabs.search_yearly == 1)
			showSearchBtn('yearlySearch');
		if (tabs.search_seasonal == 1)
			showSearchBtn('seasonalSearch');
		if (tabs.search_weekDaily == 1)
			showSearchBtn('weekDailySearch');
		if (tabs.search_monthlyPeriod == 1)
			showSearchBtn('monthlyPeriodSearch');
		if (tabs.search_monthly == 1)
			showSearchBtn('monthlySearch');
		if (tabs.search_weekly == 1)
			showSearchBtn('weeklySearch');
		if (tabs.search_period == 1)
			showSearchBtn('periodSearch');
		if (tabs.search_daily == 1)
			showSearchBtn('dailySearch');
		if (tabs.search_hourly == 1)
			showSearchBtn('hourlySearch');

	}

	// datepicker로 선택한 날짜의 포맷 변경
	function modifyDate(setDate, inst) {
		var dateId = '#' + inst.id;

		$.getJSON("${ctx}/common/convertLocalDate.do", {
			dbDate : setDate,
			supplierId : _supplierId
		}, function(json) {
			$(dateId).val(json.localDate);
			$(dateId).trigger('change');
		});
	}

	/**
	 * 조회버튼 클릭시 조회조건 검증후 거래호출
	 */
	function sendRequest(_dateType) {
		// 조회조건 검증
		if (!validateSearchCondition(_dateType))
			return false;
		send();
	}

	/**
	 *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
	 */
	function convertSearchDate() {
		$.getJSON("${ctx}/common/convertSearchDate.do", {
			searchStartDate : $('#searchStartDate').val(),
			searchEndDate : $('#searchEndDate').val(),
			supplierId : _supplierId
		}, function(json) {
			$('#searchStartDate').val(json.searchStartDate);
			$('#searchEndDate').val(json.searchEndDate);
		});
	}

	/**
	 * 일별 화살표처리
	 */
	function dailyArrow(bfDate, val, flag) {

		$.getJSON("${ctx}/common/getDate.do", {
			searchDate : bfDate,
			addVal : val,
			supplierId : _supplierId
		}, function(json) {
			$('#dailyStartDate').val(json.searchDate);
			getSearchDate(DateType.DAILY, flag);
		});
	}

	/**
	 * 월별 화살표처리
	 */
	function monthlyArrow(val) {
		$.getJSON("${ctx}/common/getYearMonth.do", {
			year : $('#monthlyYearCombo').val(),
			month : $('#monthlyMonthCombo').val(),
			addVal : val
		}, function(json) {
			$('#monthlyYearCombo').val(json.year);
			$('#monthlyYearCombo').selectbox();
			getMonthlyMonthCombo(json.month);
		});
	}

	/**
	 * 기간별 버튼처리
	 */
	function setPeriod(val, flag) {
		$.getJSON("${ctx}/common/getDate.do", {
			searchDate : '',
			addVal : val,
			supplierId : _supplierId
		}, function(json) {
			$('#periodStartDate').val(json.searchDate);
			$('#periodEndDate').val(json.currDate);
			getSearchDate(DateType.PERIOD, flag);
		});
	}

	/**
	 * 기간별 버튼처리 - 한달
	 */
	function setPeriodOneMonth(val, flag) {
		$.getJSON("${ctx}/common/getDate.do", {
			searchDate : '',
			addVal : val,
			supplierId : _supplierId,
			monthCalc : true
		}, function(json) {
			$('#periodStartDate').val(json.searchDate);
			$('#periodEndDate').val(json.currDate);
			getSearchDate(DateType.PERIOD, flag);
		});
	}

	/**
	 * 주별탭에서 연도콤보 변경시 월콤보 생성
	 */
	function getWeeklyMonthCombo(flag) {

		$.getJSON("${ctx}/common/getMonth.do", {
			year : $('#weeklyYearCombo').val()
		}, function(json) {
			var prevMonth = $('#weeklyMonthCombo').val();
			$('#weeklyMonthCombo').emptySelect();
			if (prevMonth == "" || prevMonth == null
					|| Number(prevMonth) > Number(json.monthCount))
				prevMonth = json.monthCount;
			var idx = Number(prevMonth) - 1;
			$('#weeklyMonthCombo').numericOptions({
				from : 1,
				to : json.monthCount,
				selectedIndex : idx
			});
			$('#weeklyMonthCombo').selectbox();
			getWeeklyWeekCombo(flag);
		});
	}

	/**
	 * 주별탭에서 월콤보 변경시 주콤보 생성
	 */
	function getWeeklyWeekCombo(flag) {
		$.getJSON("${ctx}/common/getWeek.do", {
			year : $('#weeklyYearCombo').val(),
			month : $('#weeklyMonthCombo').val()
		},
				function(json) {

					var prevWeek = $('#weeklyWeekCombo').val();
					$('#weeklyWeekCombo').emptySelect();
					if (prevWeek == "" || prevWeek == null
							|| prevWeek > json.weekCount)
						prevWeek = json.weekCount;
					var idx = Number(prevWeek) - 1;
					$('#weeklyWeekCombo').numericOptions({
						from : 1,
						to : json.weekCount,
						selectedIndex : idx
					});
					$('#weeklyWeekCombo').selectbox();
					getSearchDate(DateType.WEEKLY, flag);
				});
	}

	/**
	 * 월별탭에서 연도콤보 변경시 월콤보 생성
	 */
	function getMonthlyMonthCombo(monthVal, flag) {
		$.getJSON("${ctx}/common/getMonth.do", {
			year : $('#monthlyYearCombo').val()
		}, function(json) {
			var prevMonth = $('#monthlyMonthCombo').val();
			$('#monthlyMonthCombo').emptySelect();
			if (prevMonth == "" || prevMonth == null
					|| Number(prevMonth) > Number(json.monthCount))
				prevMonth = json.monthCount;
			var idx = Number(prevMonth) - 1;
			$('#monthlyMonthCombo').numericOptions({
				from : 1,
				to : json.monthCount,
				selectedIndex : idx
			});

			if (monthVal != null && monthVal != "") {
				$('#monthlyMonthCombo').val(monthVal);
			}
			$('#monthlyMonthCombo').selectbox();
			getSearchDate(DateType.MONTHLY, flag);
		});
	}

	/**
	 * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
	 */
	function getMonthlyPeriodStartMonthCombo(flag) {
		$.getJSON("${ctx}/common/getMonth.do", {
			year : $('#monthlyPeriodStartYearCombo').val()
		}, function(json) {
			var prevMonth = $('#monthlyPeriodStartMonthCombo').val();
			$('#monthlyPeriodStartMonthCombo').emptySelect();
			if (prevMonth == "" || prevMonth == null
					|| Number(prevMonth) > Number(json.monthCount))
				prevMonth = json.monthCount;
			var idx = Number(prevMonth) - 1;
			$('#monthlyPeriodStartMonthCombo').numericOptions({
				from : 1,
				to : json.monthCount,
				selectedIndex : idx
			});

			$('#monthlyPeriodStartMonthCombo').selectbox();
			getSearchDate(DateType.MONTHLYPERIOD, flag);
		});
	}

	/**
	 * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
	 */
	function getMonthlyPeriodEndMonthCombo(flag) {
		$.getJSON("${ctx}/common/getMonth.do", {
			year : $('#monthlyPeriodEndYearCombo').val()
		}, function(json) {
			var prevMonth = $('#monthlyPeriodEndMonthCombo').val();
			$('#monthlyPeriodEndMonthCombo').emptySelect();
			if (prevMonth == "" || prevMonth == null
					|| Number(prevMonth) > Number(json.monthCount))
				prevMonth = json.monthCount;
			var idx = Number(prevMonth) - 1;
			$('#monthlyPeriodEndMonthCombo').numericOptions({
				from : 1,
				to : json.monthCount,
				selectedIndex : idx
			});

			$('#monthlyPeriodEndMonthCombo').selectbox();
			getSearchDate(DateType.MONTHLYPERIOD, flag);
		});
	}

	/**
	 * 요일별탭에서 연도콤보 변경시 월콤보 생성
	 */
	function getWeekDailyMonthCombo(flag) {
		$.getJSON("${ctx}/common/getMonth.do", {
			year : $('#weekDailyYearCombo').val()
		}, function(json) {
			var prevMonth = $('#weekDailyMonthCombo').val();
			$('#weekDailyMonthCombo').emptySelect();
			if (prevMonth == "" || prevMonth == null
					|| Number(prevMonth) > Number(json.monthCount))
				prevMonth = json.monthCount;
			var idx = Number(prevMonth) - 1;
			$('#weekDailyMonthCombo').numericOptions({
				from : 1,
				to : json.monthCount,
				selectedIndex : idx
			});
			$('#weekDailyMonthCombo').selectbox();
			getWeekDailyWeekCombo(flag);
		});
	}

	/**
	 * 요일별탭에서 월콤보 변경시 주콤보 생성
	 */
	function getWeekDailyWeekCombo(flag) {
		$.getJSON("${ctx}/common/getWeek.do", {
			year : $('#weekDailyYearCombo').val(),
			month : $('#weekDailyMonthCombo').val()
		}, function(json) {

			var prevWeek = $('#weekDailyWeekCombo').val();
			$('#weekDailyWeekCombo').emptySelect();
			if (prevWeek == "" || prevWeek == null
					|| Number(prevWeek) > Number(json.weekCount))
				prevWeek = json.weekCount;
			var idx = Number(prevWeek) - 1;
			$('#weekDailyWeekCombo').numericOptions({
				from : 1,
				to : json.weekCount,
				selectedIndex : idx
			});
			$('#weekDailyWeekCombo').selectbox();
			getWeekDailyWeekDayCombo(flag);
		});
	}

	/**
	 * 요일별탭에서 주차콤보 변경시 요일콤보 생성
	 * 시작은 일요일
	 */
	function getWeekDailyWeekDayCombo(flag) {
		//년도,월,주차 입력받아 현재주이면 오늘날짜까지의 요일만 표시
		$
				.getJSON(
						"${ctx}/common/getWeekDay.do",
						{
							year : $('#weekDailyYearCombo').val(),
							month : $('#weekDailyMonthCombo').val(),
							week : $('#weekDailyWeekCombo').val()
						},
						function(json) {

							var prevWeek = $('#weekDailyWeekDayCombo').val();
							$('#weekDailyWeekDayCombo').emptySelect();

							var dayofweeks = new Array();

							dayofweeks[0] = '<fmt:message key="aimir.quartz.week.sun"/>';
							dayofweeks[1] = '<fmt:message key="aimir.quartz.week.mon"/>';
							dayofweeks[2] = '<fmt:message key="aimir.quartz.week.tue"/>';
							dayofweeks[3] = '<fmt:message key="aimir.quartz.week.wed"/>';
							dayofweeks[4] = '<fmt:message key="aimir.quartz.week.thu"/>';
							dayofweeks[5] = '<fmt:message key="aimir.quartz.week.fri"/>';
							dayofweeks[6] = '<fmt:message key="aimir.quartz.week.sat"/>';

							if (prevWeek == ""
									|| prevWeek == null
									|| (Number(prevWeek) < Number(json.startWeek) && Number(prevWeek) > Number(json.endWeek)))
								prevWeek = json.endWeek;

							var dayofweek = new Array();
							var idx = 0;
							for (var i = Number(json.startWeek); i <= Number(json.endWeek); i++) {
								dayofweek[idx] = dayofweeks[i - 1];
								idx++;
							}

							$('#weekDailyWeekDayCombo').numericOptions({
								from : json.startWeek,
								to : json.endWeek,
								labels : dayofweek
							});
							$('#weekDailyWeekDayCombo').val(prevWeek);
							$('#weekDailyWeekDayCombo').selectbox();
							getSearchDate(DateType.WEEKDAILY, flag);
						});
	}

	/**
	 * 계절별탭에서 년도 변경시 계절콤보 조회
	 */
	function getSeasonalSeasonCombo(flag) {
		$.getJSON("${ctx}/common/getSeason.do", {
			year : $('#seasonalYearCombo').val()
		}, function(json) {

			seasonData = json;

			var prevSeason = $('#seasonalSeasonCombo').val();
			$('#seasonalSeasonCombo').emptySelect();

			var season = new Array();

			var excludeIdx = 0;
			var excludeIdxs = new Array();

			var includeIdx = 0;
			var includeIdxs = new Array();

			var seasonIdxs = 0;

			// Seasonal 검색시 오류 수정 전 원본
			/* if (json.Spring.name == Season.SPRING) {
				season[seasonIdxs++] = '<fmt:message key="aimir.spring"/>';
				includeIdxs[includeIdx++] = 1;
			} else {
				excludeIdxs[excludeIdx++] = 1;
			}

			if (json.Summer.name == Season.SUMMER) {
				season[seasonIdxs++] = '<fmt:message key="aimir.summer"/>';
				includeIdxs[includeIdx++] = 2;
			} else {
				excludeIdxs[excludeIdx++] = 2;
			}

			if (json.Autumn.name == Season.AUTUMN) {
				season[seasonIdxs++] = '<fmt:message key="aimir.autumn"/>';
				includeIdxs[includeIdx++] = 3;
			} else {
				excludeIdxs[excludeIdx++] = 3;
			}

			if (json.Winter.name == Season.WINTER) {
				season[seasonIdxs++] = '<fmt:message key="aimir.winter"/>';
				includeIdxs[includeIdx++] = 4;
			} else {
				excludeIdxs[excludeIdx++] = 4;
			} */

			if (json.Spring.name == Season.SPRING) {
				season[seasonIdxs++] = '<fmt:message key="aimir.spring"/>';
				includeIdxs[includeIdx++] = 1;
			} else {
				season[seasonIdxs++] = '<fmt:message key="aimir.spring"/>';
				excludeIdxs[excludeIdx++] = 1;
			}

			if (json.Summer.name == Season.SUMMER) {
				season[seasonIdxs++] = '<fmt:message key="aimir.summer"/>';
				includeIdxs[includeIdx++] = 2;
			} else {
				season[seasonIdxs++] = '<fmt:message key="aimir.summer"/>';
				excludeIdxs[excludeIdx++] = 2;
			}

			if (json.Autumn.name == Season.AUTUMN) {
				season[seasonIdxs++] = '<fmt:message key="aimir.autumn"/>';
				includeIdxs[includeIdx++] = 3;
			} else {
				season[seasonIdxs++] = '<fmt:message key="aimir.autumn"/>';
				excludeIdxs[excludeIdx++] = 3;
			}

			if (json.Winter.name == Season.WINTER) {
				season[seasonIdxs++] = '<fmt:message key="aimir.winter"/>';
				includeIdxs[includeIdx++] = 4;
			} else {
				season[seasonIdxs++] = '<fmt:message key="aimir.winter"/>';
				excludeIdxs[excludeIdx++] = 4;
			}

			// Seasonal 검색시 오류 수정 전 원본
			/* if (prevSeason == "" || prevSeason == null) {
				prevSeason = includeIdxs[includeIdx - 1];
			} else {
				for (var i = 0; i < excludeIdx; i++) {
					if (excludeIdxs[i] == prevSeason) {
						prevSeason = includeIdxs[includeIdx - 1];
					}
				}
			} 
			
			$('#seasonalSeasonCombo').numericOptions({
				from : 1,
				to : 4,
				labels : season,
				exclude : excludeIdxs
			}); */

			$('#seasonalSeasonCombo').numericOptions({
				from : 1,
				to : 4,
				labels : season
			});
			$('#seasonalSeasonCombo').val(prevSeason);
			$('#seasonalSeasonCombo').selectbox();
			getSearchDate(DateType.SEASONAL, flag);
		});
	}

	/**
	 * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
	 * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
	 * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
	 */
	function getSearchDate(_dateType, flag) {
		var startDate = '';
		var endDate = '';

		// 화면첫로딩시일경우에는
		// 첫번째탭 클릭 이벤트를 호출한다.
		// ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
		// 첫번째 탭의 날짜를 설정하도록 한다.
		if (flag == false) {
			$('div#datetab > ul:first > li:first > a:first').trigger('click');
			return;
		}

		if (DateType.HOURLY == _dateType) {
			if (tabs.hourly == 0)
				return;
			$('#searchStartDate').val($('#hourlyStartDate').val());
			$('#searchEndDate').val($('#hourlyEndDate').val());

			$('#searchStartHour').val(
					Number($('#hourlyStartHourCombo').val()) < 10 ? '0'
							+ $('#hourlyStartHourCombo').val() : $(
							'#hourlyStartHourCombo').val());
			$('#searchEndHour').val(
					Number($('#hourlyEndHourCombo').val()) < 10 ? '0'
							+ $('#hourlyEndHourCombo').val() : $(
							'#hourlyEndHourCombo').val());
			convertSearchDate();

		} else if (DateType.DAILY == _dateType) {
			if (tabs.daily == 0)
				return;
			$('#searchStartDate').val($('#dailyStartDate').val());
			$('#searchEndDate').val($('#dailyStartDate').val());

			convertSearchDate();
		} else if (DateType.PERIOD == _dateType) {
			if (tabs.period == 0)
				return;
			$('#searchStartDate').val($('#periodStartDate').val());
			$('#searchEndDate').val($('#periodEndDate').val());

			convertSearchDate();
		} else if (DateType.WEEKLY == _dateType) {
			if (tabs.weekly == 0)
				return;
			$.getJSON("${ctx}/common/getWeekPeriod.do", {
				year : $('#weeklyYearCombo').val(),
				month : $('#weeklyMonthCombo').val(),
				week : $('#weeklyWeekCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchStartDate').val(json.startDate);
				$('#searchEndDate').val(json.endDate);
				convertSearchDate();
			});
		} else if (DateType.MONTHLY == _dateType) {
			if (tabs.monthly == 0)
				return;
			$.getJSON("${ctx}/common/getMonthPeriod.do", {
				year : $('#monthlyYearCombo').val(),
				month : $('#monthlyMonthCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchStartDate').val(json.startDate);
				$('#searchEndDate').val(json.endDate);
				convertSearchDate();
			});
		} else if (DateType.MONTHLYPERIOD == _dateType) {
			if (tabs.monthlyPeriod == 0)
				return;

			$.getJSON("${ctx}/common/getMonthPeriod.do", {
				year : $('#monthlyPeriodStartYearCombo').val(),
				month : $('#monthlyPeriodStartMonthCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchStartDate').val(json.startDate);
			});
			$.getJSON("${ctx}/common/getMonthPeriod.do", {
				year : $('#monthlyPeriodEndYearCombo').val(),
				month : $('#monthlyPeriodEndMonthCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchEndDate').val(json.endDate);
				convertSearchDate();
			});

		} else if (DateType.WEEKDAILY == _dateType) {
			if (tabs.weekDaily == 0)
				return;
			$.getJSON("${ctx}/common/getWeekDayPeriod.do", {
				year : $('#weekDailyYearCombo').val(),
				month : $('#weekDailyMonthCombo').val(),
				week : $('#weekDailyWeekCombo').val(),
				weekDay : $('#weekDailyWeekDayCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchStartDate').val(json.startDate);
				$('#searchEndDate').val(json.endDate);
				convertSearchDate();
			});
		} else if (DateType.SEASONAL == _dateType) {
			if (tabs.seasonal == 0)
				return;
			var seasonIdx = $('#seasonalSeasonCombo').val();

			var season;
			if (seasonIdx == 1) {
				season = seasonData.Spring;
			} else if (seasonIdx == 2) {
				season = seasonData.Summer;
			} else if (seasonIdx == 3) {
				season = seasonData.Autumn;
			} else if (seasonIdx == 4) {
				season = seasonData.Winter;
			} else {
				season = {
					startDate : '',
					endDate : ''
				};
			}

			$('#searchStartDate').val(season.startDate);
			$('#searchEndDate').val(season.endDate);

		} else if (DateType.YEARLY == _dateType) {
			if (tabs.yearly == 0)
				return;

			$.getJSON("${ctx}/common/getYearPeriod.do", {
				year : $('#yearlyYearCombo').val(),
				supplierId : _supplierId
			}, function(json) {
				$('#searchStartDate').val(json.startDate);
				$('#searchEndDate').val(json.endDate);
				convertSearchDate();
			});
		}

	}

	/**
	 * 날짜타입탭을 숨김처리한다.
	 */
	function hideTab(_idx) {
		$("#datetab").tabs("remove", _idx);
		$("#datetab").tabs("select", 0);
	}

	/**
	 * 조회버튼을 표시한다.
	 */
	function showSearchBtn(btnName) {

		var liName = '#' + btnName + 'Li';
		$(liName).css('display', 'block');

	}

	/**
	 * 날짜타입별 조회조건 검증
	 */
	function validateSearchCondition(_dateType) {

		if (DateType.HOURLY == _dateType) {
			if (Number($('#searchStartDate').val()) == Number($(
					'#searchEndDate').val())) {
				if (Number($('#searchStartHour').val()) > Number($(
						'#searchEndHour').val())) {
					Ext.Msg.alert("<fmt:message key='aimir.message'/>",
							'<fmt:message key="aimir.season.error"/>');
					return false;
				}
			}
			return true;
		} else if (DateType.DAILY == _dateType) {
			//시작일 필수 체크
			return true;
		} else if (DateType.PERIOD == _dateType) {
			//시작일,종료일 필수 체크
			//시작일,종료일 선후 체크
			//시작일,종료일 기간체크(31일 이내)
			if (Number($('#searchStartDate').val()) > Number($('#searchEndDate')
					.val())) {
				//시작일이 종료일보다 큽니다
				Ext.Msg.alert("<fmt:message key='aimir.message'/>",
						'<fmt:message key="aimir.season.error"/>');
				return false;
			}

			if (Number($('#searchEndDate').val())
					- Number($('#searchStartDate').val()) > 30) {
				//최대 31일까지만 조회 가능합니다.
				//alert('<fmt:message key="aimir.season.error"/>');
				//return false;
			}
			return true;
		} else if (DateType.WEEKLY == _dateType) {
			//조회월,조회주차 필수체크
			return true;
		} else if (DateType.MONTHLY == _dateType) {
			//조회월 필수체크
			return true;
		} else if (DateType.MONTHLYPERIOD == _dateType) {
			if (Number($('#searchStartDate').val()) > Number($('#searchEndDate')
					.val())) {
				//시작일이 종료일보다 큽니다
				Ext.Msg.alert("<fmt:message key='aimir.message'/>",
						'<fmt:message key="aimir.season.error"/>');
				return false;
			}
			return true;
		} else if (DateType.WEEKDAILY == _dateType) {
			//조회월,주차,요일 필수체크
			return true;
		} else if (DateType.SEASONAL == _dateType) {
			//조회연도,계절 필수체크
			if ($('#seasonalSeasonCombo').val() == null
					|| $('#seasonalSeasonCombo').val() == '') {
				//계절데이터가 없습니다.
				Ext.Msg
						.alert(
								"<fmt:message key='aimir.message'/>",
								'<div style="width: 150px"><fmt:message key="aimir.season.select.error"/></div>');
				return false;
			}
			return true;
		} else if (DateType.YEARLY == _dateType) {

			return true;
		} else {
			//날짜타입오류
			return false;
		}
	}

	//======================================================================================

	/*]]>*/
</script>

<!-- Tab (S) -->
<div id="datetab" class="buttontype" style="display:none">
    <ul class="buttontype">
        <li><a href="#hourly" id="_hourly"><fmt:message key="aimir.hourly"/></a></li>
        <li><a href="#daily" id="_daily"><fmt:message key="aimir.daily"/></a></li>
        <li><a href="#period" id="_period"><fmt:message key="aimir.period"/></a></li>
        <li><a href="#weekly" id="_weekly"><fmt:message key="aimir.weekly"/></a></li>
        <li><a href="#monthly" id="_monthly"><fmt:message key="aimir.monthly"/></a></li>
        <li><a href="#monthlyPeriod" id="_monthlyPeriod"><fmt:message key="aimir.monthly"/>(<fmt:message key="aimir.period"/>)</a></li>
        <li><a href="#weekdaily" id="_weekdaily"><fmt:message key="aimir.weekdaily"/></a></li>
        <li><a href="#seasonal" id="_seasonal"><fmt:message key="aimir.seasonal"/></a></li>
        <li><a href="#yearly" id="_yearly"><fmt:message key="aimir.yearly"/></a></li>
    </ul>



    <div id="hourly">
	    <!-- <label><fmt:message key="aimir.hourly"/></label> -->
	    <ul>
	        <li><input id="hourlyStartDate" class="day" type="text" readonly="readonly"></li>
	        
	        <li><select id="hourlyStartHourCombo" class="sm"></select></li>
	        <li><input value="~" class="between" type="text"></li>
	        <li><input id="hourlyEndDate" class="day" type="text" readonly="readonly"></li>
	        
	        <li><select id="hourlyEndHourCombo" class="sm"></select></li>
	    </ul>
	    <div id="btn">
	        <ul><li style="display:none" id="hourlySearchLi"><a href="#;" id="hourlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	    </div>
    </div>

    <div id="daily">
        <!-- <label><fmt:message key="aimir.date" /></label> -->
        <ul>
            <li><button id="dailyLeft" type="button" class="back"></button></li>
            <li><input id="dailyStartDate" type="text" class="day" readonly="readonly"></li>
            <li><button id="dailyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="dailySearchLi"><a href="#;" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="period">
        <!-- <label><fmt:message key="aimir.period"/></label> -->
        <ul>
            <li><select id="periodType" class="date-w90"></select></li>
            
            <li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="periodSearchLi"><a href="#;" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <!--div id="period">
        <ul class="align">
            <li class="btnleft">
                <button id="todayBtn" type="button" class="sm">오늘</button>
                <button id="yesterdayBtn" type="button" class="sm">어제</button>
                <button id="threeDayBtn" type="button" class="sm">3일</button>
                <button id="weekBtn" type="button" class="sm">1주</button>
                <button id="monthBtn"type="button" class="sm">1개월</button>
            </li>
            <li>
                <label><fmt:message key="aimir.period"/></label>
                <ul>
                    <li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
                    <li><input value="~" class="between" type="text"></li>
                    <li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
                </ul>
                <div id="btn">
                    <ul><li><a href="#;" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                </div>
           </li>
        </ul>
    </div-->

    <div id="weekly">
        <!-- <label><fmt:message key="aimir.weekly"/></label> -->
        <ul>
            <li><select id="weeklyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="weeklyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            
            <li><select id="weeklyWeekCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="weeklySearchLi"><a href="#;" id="weeklySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthly">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><button id="monthlyLeft" type="button" class="back"></button></li>
            <li><select id="monthlyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="monthlyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="monthlySearchLi"><a href="#;" id="monthlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthlyPeriod">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><select id="monthlyPeriodStartYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyPeriodStartMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="monthlyPeriodEndYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyPeriodEndMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="monthlyPeriodSearchLi"><a href="#;" id="monthlyPeriodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="weekdaily">
        <!-- <label><fmt:message key="aimir.weekdaily"/></label> -->
        <ul>
            <li><select id="weekDailyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="weekDailyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            
            <li><select id="weekDailyWeekCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
            
            <li><select id="weekDailyWeekDayCombo" class="date-w80"></select></li>
        </ul>
        <div id="btn" style="display:none">
            <ul><li id="weekDailySearchLi"><a href="#;" id="weekDailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="seasonal">
        <!-- <label><fmt:message key="aimir.seasonal" /></label> -->
        <ul>
            <li><select id="seasonalYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="seasonalSeasonCombo" class="date-w80"></select></li>
        </ul>
        <div id="btn">
            <ul>
            <li style="display:none" id="seasonalSearchLi"><a href="#;" id="seasonalSearch" class="on"><fmt:message key="aimir.button.search" /></a></li>
            </ul>
            <div>&nbsp;</div>
            <p id="info_Season" style="color:#0F7CD0; padding-top: 1%;"></p>
        </div>
    </div>
    <div id="yearly">
        <!-- <label><fmt:message key="aimir.yearly" /></label> -->
        <ul>
            <li><select id="yearlyYearCombo"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="yearlySearchLi"><a href="#;" id="yearlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
	    <input id="searchStartDate" type="hidden"/>
	    <input id="searchEndDate" type="hidden" />
	    <input id="searchStartHour" type="hidden"/>
	    <input id="searchEndHour" type="hidden" />
	    <input id="searchDateType" type="hidden" value="0"/>
</div>
<!-- Tab (E) -->
