<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="PRAGMA" content="NO-CACHE">
  <meta http-equiv="Expires" content="-1">
  <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
  <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
  <style type="text/css">
    /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
    .x-panel-bbar table {border-collapse: collapse; width:auto;}
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }  
    
    /* ext-js grid header group bottom */
    td.ux-grid-hd-group-cell {
      border-bottom: 1px #d0d0d0 solid;
    }

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }   
    .no-width {
      visibility: hidden;
      width: 0px;
    }

    div#search-condition {
      margin-top: 10px;
      margin-left: 15px;
    }

    div#description {
      margin-top: 10px;
      margin-left: 15px;
    }
      div#description div {
        margin-bottom: 3px;
      }
        div#description input, 
        div#description span { 
          float: none;
        }    
        div#description .desc {
          display: inline-block;
          width: 40px;  
        }
          div#description .sag-desc {
            background-color: #FFFF00;
          }
          div#description .swell-desc {
            background-color: #FF0000;
          }
          div#description .normal-desc {
            background-color: #00FF00;
          }

  </style>    
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>
  <script type="text/javascript" src="${ctx}/js/util/numberUtil.js"></script>
  <script type="text/javascript" src="${ctx}/js/util/commonUtil.js"></script>
  <%@ include file="/gadget/system/preLoading.jsp"%>
  <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js">
  </script>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>  
  <script type="text/javascript" charset="utf-8">
    var supplierId; 
    var PAGE_SIZE = 6;
    var NOMALIC_VOLTAGE = 220;
    var SAG_VOLTAGE = 176;
    var SWELL_VOLTAGE = 264;
    var powerQualityChart;
    var dataFormat = {
      sag: 'Sag',
      swell: 'Swell',
      normal: 'Normal'
    };

    var calendarProp = {
      showOn: 'button',
      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
      buttonImageOnly: true
    };
    
    var powerQualityChartParam = {
      chart: {
        showvalues: 0,
        baseFont: '돋움',
        baseFontSize: 12
      }
    };

    var eventHandler = {
      changeCalendarDate: function(date) {
        date = $.datepicker.parseDate('yymmdd', date);
        $('#search-date').datepicker('setDate', date);
        eventHandler.modifiedDateFormat(date, $('#date-display'));
      },

      modifiedDateFormat: function(date, $target) {   
        date = $.datepicker.formatDate('yymmdd', date);
        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, 
            dbDate: date},
          function(data) {   
            $target.val(data.localDate);
          });
      },      

      modifyDataFormat: function(data) {
        $.each(data, function(index, row) {
          row.label = dataFormat[row.label];
        })
      },

      moveDate: function() {
        var $this = $(this);
        var mvDate;

        if ($this.hasClass('back')) mvDate = -1;
        else if ($this.hasClass('next')) mvDate = 1;

        $.getJSON("${ctx}/common/getDate.do", 
          { searchDate: $('#date-display').val(),
            addVal: mvDate,
            supplierId: supplierId}, 
            function(json) {
              var date = json.searchDate;
              var dbDate = json.dbDate;
              dateObj = $.datepicker.parseDate("yymmdd", dbDate);
              $("#search-date").datepicker("setDate", dateObj);
              $('#date-display').val(date);
            }
          );
      },

      updatePowerQualityChart: function(callback) {
        $.getJSON("${ctx}/gadget/mvm/dailyPowerQualityChartData.do", {
          date: $('#search-date').val() || $.datepicker.formatDate("yymmdd", new Date()),
          sag: SAG_VOLTAGE,
          swell: SWELL_VOLTAGE
        }, function (json) {
          eventHandler.modifyDataFormat(json.data);
          powerQualityChartParam.data = json.data;
          powerQualityChart.setJSONData(powerQualityChartParam);
          if ( callback ) {
            callback();
          }
        });
      }
    };

    var addToolTip = function(value, metaData) {
      if ( value != null && value != "" ) {
        metaData.attr = "ext:qtip='" + value + "'";
      }
      return value;
    };

    var bind = function() {
      $('#calendar .back, #calendar .next').click(eventHandler.moveDate);
      $('#search-date').datepicker('option',{onSelect:eventHandler.changeCalendarDate});
      $("#search").click(eventHandler.updatePowerQualityChart);
    };

    var initCalendar = function(callback) {
      var today = new Date();
      calendarProp.dateFormat = 'yymmdd';
      calendarProp.altFormat = '';
      calendarProp.defaultDate = today;

      $('#search-date').datepicker(calendarProp);
      $('#search-date').datepicker('setDate', today);
      eventHandler.modifiedDateFormat(today, $('#date-display'));
      if ( callback ) {
        callback();
      }
    };

    var initSupplier = function(callback) {
      $.getJSON("${ctx}/common/getUserInfo.do",
        function(json) {
          supplierId = json.supplierId;
          if ( callback ) {
            callback();
          }
        });
    };

    var initPowerQualityChart = function() {
      powerQualityChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", 
        "powerQualityChartId", "400", "250", "0", "0");
      eventHandler.updatePowerQualityChart( function () {
        powerQualityChart.render("powerQualityChartDiv");
      });      
    };

    var init = function() {
      initSupplier(function() {
        hide();
        initCalendar(initPowerQualityChart);
        bind();  
      });
    };

    Ext.onReady(init);
  </script>
</head>
<body>
  <div id='search-condition'>
    <span id='calendar'>
      <button type="button" class="back"></button>
      <span>
        <input id="date-display" type="text" class="day" readonly="readonly"></input>
        <input id="search-date" type="text" class="no-width"></input>
      </span>
      <button type="button" class="next"></button>
    </span>

    <span id="search" class="am_button margin-l10 margin-t1px">
      <a class='on'>
        <fmt:message key="aimir.button.search" />
      </a>
    </span>
    <div class='clear'></div>
  </div>

  <div id="description">
    <div>
      <span class="sag-desc desc"><fmt:message key='aimir.sag'/></span>
      <span>: 176(V)(80%<fmt:message key='aimir.less'/>)</span>
    </div>
    <div>
      <span class="swell-desc desc"><fmt:message key='aimir.swell'/></span>
      <span>: 264(V)(120%<fmt:message key='aimr.morethan'/>)</span>
    </div>
    <div>
      <span class="normal-desc desc"><fmt:message key='aimir.normal'/></span>
      <span>: 220(V)</span>
    </div>
  </div>

  <div id='powerQualityChartDiv'></div>
</bodY>