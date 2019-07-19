<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="PRAGMA" content="NO-CACHE">
  <meta http-equiv="Expires" content="-1">
  <link href="${ctx}/js/extjs/ux/css/GroupHeaderPlugin.css" rel="stylesheet" type="text/css"/> 
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

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    } 

    /* ext-js group grid header 정렬 */
    .x-grid3-header-offset table {
      border-collapse: separate;
      border-spacing: 0px;      
    }
    
    #search-condition {
      margin-top: 20px;
      margin-left: 20px;
      margin-bottom: 20px;
    }
      #search-condition span,
      #search-condition input {
        display: inline-block;        
        margin-right: 20px;
      }
      #search-condition span {
        vertical-align: text-bottom;
      }
      #calendar span {
        margin: 0px;
      }
      input.no-width {
        width: 0px;
        visibility: hidden;
      }
      td.sag-voltage {
        background-color: #FFFF00;
      }
      td.swell-voltage {
        background-color: #FF0000;
      }
      input.sag-swell {
        text-align: right;
        width: 20px;
      }
      .sag-desc {
        width: 30px;
        background-color: #FFFF00;
      }
      .swell-desc {
        width: 30px;
        background-color: #FF0000; 
      }

    #powerQualityDiv {
      margin-left: 20px;
      margin-right: 20px;
    }
  </style>    
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
  <script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>  
  <%@ include file="/gadget/system/preLoading.jsp"%>
  <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js">
  </script>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ux/GroupHeaderPlugin.js"></script>
  <script type="text/javascript" charset="utf-8">
    var supplierId;
    var PAGE_SIZE = 20;
    var NOMALIC_VOLTAGE = 220;
    var powerQualityGrid;

    var storeParams = { 
      page: 1,
      start: 0,
      limit: PAGE_SIZE
    };

    var gridProp = {
      width: 'auto',
      height: 545,
      autoScroll: false,
      stripeRows: true,
      columnLines: true,
      loadMask: {
        msg: "loading..."
      },
      viewConfig: {
        forceFit: true,
        scrollOffset: 1,
        enableRowBody: true,
        showPreview: true,
        emptyText: "No data to display"
      }
    };

    var powerQualityStore = new Ext.data.JsonStore({
      baseParams: storeParams,
      url: "${ctx}/gadget/mvm/dailyPowerQualityData.do",
      totalProperty: 'size',
      root: 'data',
      fields: ['hhmm', 'mdsId', 'contractNumber', 'line_ab', 'line_ac',
        'vol_a', 'curr_a', 'vol_thd_a', 'pf_a', 'vol_angle_a',
        'vol_b', 'curr_b', 'vol_thd_b', 'pf_b', 'vol_angle_b',
        'vol_c', 'curr_c', 'vol_thd_c', 'pf_c', 'vol_angle_c'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if ( params.start && params.start > 0 ) {
            params.page = (params.start + PAGE_SIZE) / PAGE_SIZE;
          } else {
            params.page = 1;
          }
        }
      }
    });

    var voltageCell = function(val, metaData, rowIdx, colIdx, store) {
      var json = powerQualityStore.reader.jsonData;
      var sag = json.sag;
      var swell = json.swell;

      if (sag && val <= sag && val > 0) {
        metaData.css = 'sag-voltage';
      }
      if (swell && val >= swell) {
        metaData.css = 'swell-voltage';
      }
      return val;
    };

    var powerQualityModel = new Ext.grid.ColumnModel({
      columns: [
        {header: "<fmt:message key='aimir.hour'/>", dataIndex: 'hhmm'},
        {header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId'},
        {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'},
        {header: "A-B", dataIndex: 'line_ab', align: 'right'},
        {header: "A-C", dataIndex: 'line_ac', align: 'right'},
        {header: "<fmt:message key='aimir.voltage'/>", dataIndex: 'vol_a',
         renderer: voltageCell, align: 'right'},
        {header: "<fmt:message key='aimir.current'/>", dataIndex: 'curr_a', align: 'right'},
        {header: "<fmt:message key='aimir.thd'/>", dataIndex: 'vol_thd_a', align: 'right'},
        {header: "<fmt:message key='aimir.power.factor'/>", dataIndex: 'pf_a', align: 'right'},
        {header: "<fmt:message key='aimir.phase.angle'/>", dataIndex: 'vol_angle_a',
         align: 'right'},
        {header: "<fmt:message key='aimir.voltage'/>", dataIndex: 'vol_b',
         renderer: voltageCell, align: 'right'},
        {header: "<fmt:message key='aimir.current'/>", dataIndex: 'curr_b', align: 'right'},
        {header: "<fmt:message key='aimir.thd'/>", dataIndex: 'vol_thd_b', align: 'right'},
        {header: "<fmt:message key='aimir.power.factor'/>", dataIndex: 'pf_b', align: 'right'},
        {header: "<fmt:message key='aimir.phase.angle'/>", dataIndex: 'vol_angle_b',
         align: 'right'},
        {header: "<fmt:message key='aimir.voltage'/>", dataIndex: 'vol_c',
         renderer: voltageCell, align: 'right'},
        {header: "<fmt:message key='aimir.current'/>", dataIndex: 'curr_c', align: 'right'},
        {header: "<fmt:message key='aimir.thd'/>", dataIndex: 'vol_thd_c', align: 'right'},
        {header: "<fmt:message key='aimir.power.factor'/>", dataIndex: 'pf_c', align: 'right'},
        {header: "<fmt:message key='aimir.phase.angle'/>", dataIndex: 'vol_angle_c',
         align: 'right'}
      ],
      rows: [
        [
          {},
          {},
          {},
          {header: "<fmt:message key='aimir.voltage'/> <fmt:message key='aimir.phase.angle'/>",
           colspan: 2, align: 'center'},
          {header: "<fmt:message key='aimir.a.phase'/>", colspan: 5, align: 'center'},
          {header: "<fmt:message key='aimir.b.phase'/>", colspan: 5, align: 'center'},
          {header: "<fmt:message key='aimir.c.phase'/>", colspan: 5, align: 'center'}
        ]
      ],      
      defaults : {
        sortable: true,
        menuDisable: true,
        renderer: addToolTip
      }
    });
    
    var groupHeader = new Ext.ux.plugins.GroupHeaderGrid({
        rows: [
          [
            {},
            {},
            {},
            {header: "<fmt:message key='aimir.voltage'/> <fmt:message key='aimir.phase.angle'/>",
             colspan: 2, align: 'center'},
            {header: "<fmt:message key='aimir.a.phase'/>", colspan: 5, align: 'center'},
            {header: "<fmt:message key='aimir.b.phase'/>", colspan: 5, align: 'center'},
            {header: "<fmt:message key='aimir.c.phase'/>", colspan: 5, align: 'center'}
          ]
        ],
        hierarchicalColMenu: true
      });

    var sm = new Ext.grid.RowSelectionModel({
      singleSelect: true,
      moveEditorOnEnter: false
    });

    var bbar = new Ext.PagingToolbar({
      pageSize: PAGE_SIZE,
      displayInfo: true,
      displayMsg: '{0} - {1} / {2}',
      store: powerQualityStore
    });

    var calendarProp = {
      showOn: 'button',
      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
      buttonImageOnly: true
    };
    
    var powerQualityProp = $.extend(true, {}, gridProp, {
      colModel: powerQualityModel,
      //plugins: [groupHeader],
      plugins: [new Ext.ux.plugins.GroupHeaderGrid()],
      sm: sm,
      bbar: bbar,
      store: powerQualityStore,
      enableColumnMove: false,
      renderTo: 'powerQualityDiv'
    });

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

      searchPowerQuality: function() {
        storeParams.sag = $('input[name=sag]').val();
        storeParams.swell = $('input[name=swell]').val();
        storeParams.date = $("#search-date").val();
        powerQualityStore.load(storeParams);
      },

      windowResize: function() {
        powerQualityGrid.getView().refresh();
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
      $("span#search").click(eventHandler.searchPowerQuality);
      $(window).resize(eventHandler.windowResize);
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

    var renderGrid = function() {
      powerQualityGrid = new Ext.grid.GridPanel(powerQualityProp);
      storeParams.date = $("#search-date").val();
      
      /* 고정 parameter*/
      storeParams.vol = NOMALIC_VOLTAGE;
      storeParams.supplierId = supplierId;

      powerQualityStore.load(storeParams);
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

    var init = function() {
      initSupplier(function() {
        hide();
        initCalendar(renderGrid);
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

    <span class='sag-desc'>
      <fmt:message key='aimir.sag'/>
    </span>
    &nbsp;
    <span>
      <fmt:message key='aimir.nominal.voltage'/>
    </span>
    <input type='text' name='sag' class='sag-swell'></input>
    <span>
      %
      <fmt:message key='aimir.less'/>
    </span>

    <span class='swell-desc'>
      <fmt:message key='aimir.swell'/>
    </span>
    &nbsp;
    <span>
      <fmt:message key='aimir.nominal.voltage'/>
    </span>
    <input type='text' name='swell' class='sag-swell'></input>
    <span>
      %
      <fmt:message key='aimr.morethan'/>
    </span>

    <span id="search" class="am_button margin-l10 margin-t1px">
      <a class='on'>
        <fmt:message key="aimir.button.search" />
      </a>
    </span>
    <div class='clear'></div>
  </div>

  <div id='powerQualityDiv'></div>
</bodY>