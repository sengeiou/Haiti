<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<style type="text/css" media="screen">
  /* ext-js grid header 정렬 */
  .x-grid3-hd-inner{
      text-align: center !important;
      font-weight: bold !important;
  }  

  tr.x-tree-node-leaf a {
  	margin-left: 20px;
  }

  span, input {
  	float: none;
  }

  #contractNumberTab {
  	margin-left: 10px;
  	margin-top: 10px;
  }

  /* no Icon */
  .no-icon {
    display: none;
    background-image:url(${ctx}/js/extjs/resources/images/default/s.gif) !important;
  }

  /*TreeGrid에서 횡scroll만 허용 */
  #contractMvm div.x-treegrid-root-node {
  	overflow-y: auto;
  	overflow-x: hidden;
  }

  /*달력 생성자에 display: none을 걸어 놓으면 달력의 위치가 비정상적으로 출력된다.*/
  #hourly input.hidden,
  #daily input.hidden {
  	width: 0px;  	
  }
  #contractMvmGrid {
  	margin-left: 10px;
  }
  .dashedline {
  	margin-top: 30px;
  }
  .channelSelect {
  	height: 40px;
  }
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
var datePattern = '${datePattern}';
var supplierId = '${supplierId}';
var GCodeLength = 17;
var WIDTH = 1200;
var HEIGHT = 550;

var channels = {};

var dateMap = {
	hourlyDate: new Date(),
	dailyStartDate: new Date(),
	dailyEndDate: new Date()
};

var calendarProperty = {
			defaultDate: 0,
  		maxDate:'+0m',
	  	showOn: 'button', 
	  	buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
	  	buttonImageOnly: true, 
	  	dateFormat: "yymmdd", 
	  	altFormat: 'dd/mm/yy',
	  	onSelect: function(dateText, inst) {
	  		dateMap[inst.id] = $("#" + inst.id).datepicker("getDate");
	  		
	  		$.getJSON("${ctx}/common/convertLocalDate.do", 
	  			{supplierId: supplierId, dbDate:dateText}, 
	  			function(data) {
	  				var id = $(inst).attr("id");
	  				$("."+id).val(data.localDate);
	  			});
	  	}
	  };

var startDateCalendar = $.extend({}, {defaultDate: dateMap.dailyStartDate, 
	altField: "#startDateView"}, calendarProperty);
var endDateCalendar = $.extend({}, {defaultDate: dateMap.dailyEndDate, 
	altField: "#endDateView"}, calendarProperty);
var hourlyDateCalendar = $.extend({}, {defaultDate: dateMap.hourlyDate, 
	altField: "#hourlyStartDate"}, calendarProperty);

var params = {
	supplierId: supplierId,
	searchStartDate: $.format.date(new Date(), "yyyyMMdd"),
	searchEndDate: $.format.date(new Date(), "yyyyMMdd"),
	channel: "1,2,3",
	type: "EM",
	searchType: "0"
};

var searchType = {
	hourly: 0,
	daily: 1
};

var gridRoot;

var gridStoreParam = {root: "result"};
var chartStoreParam = {};

var treeLoaderParam = {
	url: "${ctx}/gadget/mvm/getContractDetailMeteringDataLpData.do",
	baseParams: params,
};

var treeLoader = new Ext.tree.TreeLoader(treeLoaderParam);

var gridModelParam = {};
var chartModelParam = {};

var grid;

var gridProp = {
	id: "contractMvm",
  width: WIDTH,
  clicksToEdit: 1,  
  autoScroll: false,
  stripeRows: true,
  columnLines: true,
  enableDD: false,
  enableHdMenu : false,
  enableSort : false,
  height: HEIGHT,
  loadMask: {
    msg: "loading..."
  },
  viewConfig: {
    forceFit: true,
    scrollOffset: 1,
    enableRowBody: true,
    showPreview: true,
    emptyText: 'No data to display'
  },
};

var eventHandler = {
	modifiDate: function(dbDate, $inst) {
		$.getJSON("${ctx}/common/convertLocalDate.do", 
			{ dbDate: dbDate, supplierId:supplierId }, 
			function(data) {
				$inst.val(data.localDate);
			});
	},

	initHourlyDate: function() {
		var dbDate = $.format.date(dateMap.hourlyDate, "yyyyMMdd")
		$("#hourlyDate").val(dbDate);
		eventHandler.modifiDate(dbDate, $("#hourlyStartDate"));
	},

	initDailyDate: function() {
		var start = $.format.date(dateMap.dailyStartDate, "yyyyMMdd");
		var end = $.format.date(dateMap.dailyEndDate, "yyyyMMdd");
		$("#dailyStartDate").val(start);
		$("#dailyEndDate").val(end);
		eventHandler.modifiDate(start, $("#startDateView"));
		eventHandler.modifiDate(end, $("#endDateView"));
	},

	getMeteringGrid: function() {
		$this = $(this);
		var channelList = [];

		$("#channelList input:checked").each(function() {
			channelList.push($(this).val());
		});
		params.channel = channelList.join();
		params.startGcode = $("input[name=from-contractNumber]").val();
		params.endGcode = $("input[name=to-contractNumber").val();

		if($this.hasClass("hourly")) {
			params.searchType = 0;
			params.searchStartDate = $("#hourlyDate").val();

		} else if ($this.hasClass("daily")) {
			params.searchType = 1; 
			params.searchStartDate = $("#dailyStartDate").val();
			params.searchEndDate = $("#dailyEndDate").val();
		}

		if(!validation.validate()) {
			return;
		};

		/*임시*/
		params.startGcode = "201108220002";
		params.endGcode = "201108220003";
		/*임시*/
		
		emergePre();
		
		var channelNumbers = params.channel.split(",");
		var channelCount = params.channel.split(",").length;
		var fields = [];
		var columnFields = [];
		var columns = [];
		var columnWidth;

		fields.push("meteringTime");

		for (var idx in channelNumbers) {
			if(!isNaN(idx)) {
				fields.push("channel_" + channelNumbers[idx]);
			}
		}
		
		columnFields = $.extend({}, fields);
		columnWidth = WIDTH/(fields.length);

		for (col in columnFields) {
			if (typeof columnFields[col] === 'string') {
				var column = {};

				column.dataIndex = columnFields[col];
				column.width = columnWidth;

				if (columnFields[col].indexOf("channel_") > -1) {
					var colIdx = columnFields[col].replace("channel_", "");
					column.header = channels[colIdx].name;						
				} else {
					column.header = "<fmt:message key='aimir.meteringtime'/>";
				}
				columns.push(column);
			}
		}

		gridProp.columns = columns;
		gridProp.renderTo = "contractMvmGrid";
		gridProp.loader = treeLoader;

		$.get("${ctx}/gadget/mvm/getContractMvmDetailMeteringData.do",
			params, 
			function(data) {
				if(grid && grid.destroy) {
					grid.destroy();
				}
				gridRoot = new Ext.tree.AsyncTreeNode({
					text: "root",
					id: "root",
					expended: true,
					draggable: false,
					rootVisible: false,
					children: data.result
				});
				gridProp.root = gridRoot;
				grid = new Ext.ux.tree.TreeGrid(gridProp);				
				hide();
			});
	},

	channelListToggle: function() {
		if($("#channelItems").is(":visible")) {
			$("#channelItems").hide();
		} else {
			$("#channelItems").show();
		}
	},

	changeDate: function() {
		if($(this).hasClass('back')) {
			dateMap.hourlyDate = new Date(dateMap.hourlyDate.valueOf() - 24 * 60 * 60 * 1000);
			$("#hourlyDate").val($.format.date(dateMap.hourlyDate, "yyyyMMdd"));
		} else if ($(this).hasClass('next')) {
			var next = dateMap.hourlyDate.valueOf() + 24 * 60 * 60 * 1000;			
			if (next < new Date().valueOf()) {
				dateMap.hourlyDate = new Date(dateMap.hourlyDate.valueOf() + 24 * 60 * 60 * 1000);
				$("#hourlyDate").val($.format.date(dateMap.hourlyDate, "yyyyMMdd"));
			} else {
				return;
			}
		}
		eventHandler.modifiDate($("#hourlyDate").val(), $("#hourlyStartDate"));
	}
};

var validation = {
	validLengthTest: function() {
		var gCode = params.startGcode;
		if(gCode.length > GCodeLength) {
      Ext.Msg.alert("<fmt:message key='aimir.error'/>",
  			"<fmt:message key='aimir.err.msg.long.code'/>");
			return;

		} else if(gCode.length < GCodeLength) {
			var paddingSize = GCodeLength - gCode.length;
			for (var i=0; i<paddingSize ;i++) {
				gCode += "0";
			}
			params.startGcode = gCode;
			$("input[name=from-contractNumber]").val(gCode);
		}

		gCode = params.endGcode;
		if(gCode.length > GCodeLength) {
      Ext.Msg.alert("<fmt:message key='aimir.error'/>",
  			"<fmt:message key='aimir.err.msg.long.code'/>");			
			return;

		} else if(gCode.length < GCodeLength) {
			var paddingSize = GCodeLength - gCode.length;
			for (var i=0; i<paddingSize ;i++) {
				gCode += "9";
			}
			params.endGcode = gCode;
			$("input[name=to-contractNumber]").val(gCode);
		}		
	},

	dailyPeriodLengthTest: function() {
		var monthValue = 30 * 24 * 60 * 60 * 1000;
		var result = true;

		if(params.searchType == 1) {
			var startDateValue = $("#dailyStartDate").datepicker("getDate").valueOf();
			var endDateValue = $("#dailyEndDate").datepicker("getDate").valueOf();
			var diff = endDateValue - startDateValue;
			if (diff > monthValue) {
	      Ext.Msg.alert("<fmt:message key='aimir.error'/>",
	  			"Period is too long.");						
	      result = false;
			}
		}
		return result;
	},

	validate: function() {
		var result = true;
		validation.validLengthTest();
		var result = validation.dailyPeriodLengthTest();
		return result;
	}
};

var bind = function() {
	$("div.search").click(eventHandler.getMeteringGrid);
	$("#channelList .select-header").click(eventHandler.channelListToggle);	
	$("#hourly button").click(eventHandler.changeDate);
};

var initChannels = function() {
	var channelTr = $("#channelItems table tr");
	for(var idx = 0; idx < channelTr.length ; idx++) {
		var name = $(channelTr[idx]).find("input[name=channelName]").val();
		var channelCode = $(channelTr[idx]).find("input[name=channel]").val();
		channels[channelCode] = {code: channelCode, name: name};
	}
};

var initCalendars = function() {
	$("#dailyStartDate").datepicker(startDateCalendar);
  $("#dailyEndDate").datepicker(endDateCalendar); 
  $("#hourlyDate").datepicker(hourlyDateCalendar);
};

var init = function() {
	Ext.QuickTips.init();
	initChannels();
	initCalendars();
	eventHandler.initHourlyDate();
	eventHandler.initDailyDate();
	$("#dateTab").tabs();
	bind();
	hide();
};

$(document).ready(function() {
	init();
});

/*]]>*/
</script>
<html>
<div id="contentsWrapper">
	<div id="contractNumberTab">
		<span>
			<label>
				<fmt:message key="aimir.contractNumber"/>
			</label>
		</span>		
		<input name="from-contractNumber" type="text"></input>
		<label>
			~
		</label>
		<input name="to-contractNumber" type="text"></input>
	</div>
	<div id="dateTab">
		<ul>
      <li><a href="#hourly" id="_hourly"><fmt:message key="aimir.hourly"/></a></li>
      <li><a href="#daily" id="_daily"><fmt:message key="aimir.daily"/></a></li>
		</ul>
    
    <div id="hourly">
        <ul>
            <li><button id="hourlyLeft" type="button" class="back"></button></li>
            <li><input id="hourlyStartDate" type="text" class="day hourlyDate" readonly="readonly"></li>
            <li><input id="hourlyDate" type="text" class="day hidden"></li>
            <li><button id="hourlyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn" class="search hourly">
            <ul><li><a href="#" id="hourlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="daily">
	    <ul>
	        <li>
	        	<input id="startDateView" class="day dailyStartDate" type="text" readonly="readonly"></input>
	        	<input id="dailyStartDate" class="hidden" type="text"></input>
	        </li>
	        <li><input value="~" class="between" type="text" readonly="readonly"></li>
	        <li>
	        	<input id="endDateView" class="day dailyEndDate" type="text" readonly="readonly"></input>
	        	<input id="dailyEndDate" class="hidden" type="text"></input>
	        </li>
	    </ul>
	    <div id="btn" class="search daily">
	        <ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	    </div>
    </div>
	</div>

	<div class="dashedline"></div>

	<div class="channelSelect">
		<div class= "mvm-multiselect border-blue" id="channelList" style="position: absolute;
		z-index: 1;">
		  <table class="wfree select-header" style="width:100%">
		    <tr><td class="space10"></td>
		        <td class="graybold11pt withinput"><fmt:message key="aimir.channelid"/></td>
		    </tr>
		  </table>	
			<div id="channelItems" class="line" style="display:none;">
		    <table class="wfree">
		      <c:forEach items="${channelList}" var="channel" varStatus="idx">
		      <tr>
		        <td><input class="checkbox_space2" id="channelCode" name="channelCode" type="checkbox" checked="checked" value="${channel.codeId}"></td>
		        <td class="gray11pt withinput">${channel.codeName}
		          <input id="channelName${idx.count-1}" name="channelName" type="hidden" value="${channel.codeName}"></input>
		          <input id="channel${idx.count-1}" name="channel"  type="hidden" value="${channel.codeId}"></input>
		        </td>
		      </tr>
		      </c:forEach>
		    </table>
			</div>
		</div>    		
	</div>

  <div class="wrapper">
  	<div id="contractMvmGrid"></div>
  </div>
</div>	

</html>
