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
    /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
    .x-panel-bbar table {border-collapse: collapse; width:auto;}

    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }  
    /* ext-js grid toolbar layout css */
    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    } 

    form {
      margin-top: 10px;
      margin-bottom: 10px;    
    }
    form div {
      margin-bottom: 10px;
    }      
    form img.ui-datepicker-trigger {
      vertical-align: middle;
    }
    input, span{
      display: inline; 
      float: none;
    }
    form input.alt {
      width: 60px;
    } 
    form span{
      margin-right: 20px;
    }    
    .tab-render {
      margin-left: 10px;
      margin-right: 10px;
    }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }	

    .no-width {
      width: 0px;
      visibility: hidden;
    }
</style>    
<script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
</head>
<body>
  <div id='contentWrapper'>
    <ul>
      <li><a href="#dailyList"><fmt:message key="aimir.daily"/></a></li>
      <li><a href="#monthlyList"><fmt:message key='aimir.monthly'/></a></li>
    </ul>

    <div id='dailyList' class="tab-render">
      <form id='dailyListForm'>
        <div>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt searchDate" type='text' readOnly></input>
            <input name="searchDate" class="no-width" type="text"></input>    
          </span>
          
           <span>
              <label><fmt:message key="aimir.vendor"/></label>
          </span>
          <span class='inline-block'>
              <select id='depositVendorList' name='depositVendorList' >
              <option value=""><fmt:message key="aimir.all" /></option>
              <c:forEach var="depositVendorList" items="${depositVendorList}">
              <c:choose>
              <c:when test="${not empty depositVendorList}">
              <option value="${depositVendorList.loginId}">${depositVendorList.loginId}</option>
              </c:when>
              </c:choose>
              </c:forEach>
              </select>
          </span>

          <span id='dailyListSearch' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.search"/></a>
          </span>
          <span id='dailyListExcel' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.excel"/></a>
          </span>      
        </div>
      </form>

      <div id="dailyListGrid"></div>      
    </div>

    <div id='monthlyList' class="tab-render">
      <form id='monthlyListForm'>
        <span>
          <label><fmt:message key="aimir.searchDate"/></label>
          <input class="alt startDate" type='text' readOnly></input>
          <input name="startDate" class="no-width" type="text"></input>
          <label>~</label>
          <input class="alt endDate" type='text' readOnly></input>
          <input name="endDate" class="no-width" type="text"></input>
        </span>

         <span>
             <label><fmt:message key="aimir.vendor"/></label>
         </span>
         <span class='inline-block'>
             <select id='depositVendorList' name='depositVendorList' >
             <option value=""><fmt:message key="aimir.all" /></option>
             <c:forEach var="depositVendorList" items="${depositVendorList}">
             <c:choose>
             <c:when test="${not empty depositVendorList}">
             <option value="${depositVendorList.loginId}">${depositVendorList.loginId}</option>
             </c:when>
             </c:choose>
             </c:forEach>
             </select>
         </span>
                        
        <span id='monthlyListSearch' class="am_button margin-l10 margin-t1px">
          <a><fmt:message key="aimir.button.search"/></a>
        </span>
        <span id='monthlyListExcel' class="am_button margin-l10 margin-t1px">
          <a><fmt:message key="aimir.button.excel"/></a>
        </span>      
      </form>

      <div id="monthlyListGrid"></div>
    </div>

  </div>
  <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
  var supplierId;
  
  // excel downloadType: { daily:일간, monthly: 월간 }
  var reportType;

  var DAILY_SIZE = 25;
  var MONTHLY_SIZE = 25;

  var dailyListGrid;
  var monthlyListGrid;

  // Common datepicker property
  var calendarProp = {
    showOn: 'button',
    buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
    buttonImageOnly: true,
    dateFormat: 'yymmdd',
    altFormat: ''
  };  

  // Common Ext.data.JsonStore property
  var storeParams = {
    page: 1, 
    start: 0
  };

  // Common Ext.grid.RowSelectionModel property
  var sm = {
    singleSelect: true,
    moveEditorOnEnter: false
  };

  // Common Ext.PagingToolbar property
  var bbar = {
    displayInfo: true,
    displayMsg: ' {0} - {1} / {2}'
  };

  // Common Ext.grid.EditorGridPanel property
  var gridProp = {
    clicksToEdit: 1,  
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
      emptyText: 'No data to display'
    } 
  };  

  // DailyList Ext.data.JsonStore property
  var dailyListParams = $.extend(true, {}, storeParams, {limit:DAILY_SIZE});
  var monthlyListParmas = $.extend(true, {}, storeParams, {limit:MONTHLY_SIZE});

  var dailyListStore = new Ext.data.JsonStore({
    baseParams: dailyListParams,
    url: "${ctx}/gadget/prepaymentMgmt/addBalanceList.do",
    totalProperty: 'size',
    root: 'data',
    fields: ['lastTokenDate', 'contractNumber', 'customerName', 'mdsId', 'municipalityCode', 
    'lastTokenId','authCode','chargedCredit', 'chargedArrears','currentCredit', 
    'balance', 'arrears', 'vendorName', 'cashierName'],

    listeners: {
      beforeload: function(store, options) {
        var params = options.params;
        if (params.start && params.start > 0) {
          params.page = ((params.start + DAILY_SIZE) / DAILY_SIZE);
        } else { params.page = 1;}
      },  
    }
  });

  var monthlyListStore = new Ext.data.JsonStore({
    baseParams: monthlyListParmas,
    url: "${ctx}/gadget/prepaymentMgmt/monthlyGridDataList.do",
    totalProperty: 'size',
    root: 'data',
    fields: ['contractNumber', 'customerName', 'mdsId', 'municCode', 'startDate', 
    'startBalance', 'endDate', 'endBalance', 'usedEnergy', 'usedCost', 'chargedCredit', 
    'chargedArrears','vendorName', 'cashierName'],
    listeners: {
      beforeload: function(store, options) {
        var params = options.params;
        if (params.start && params.start > 0) {
          params.page = ((params.start + MONTHLY_SIZE) / MONTHLY_SIZE);
        } else { params.page = 1;}
      },  
    }    
  });

  var dailyListModel = new Ext.grid.ColumnModel({
    columns: [
      {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>",
        dataIndex: 'lastTokenDate'},
      {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'},
      {header: "<fmt:message key='aimir.customername'/>" , dataIndex: 'customerName'},
      {header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId'},
      {header: "<fmt:message key='aimir.vendor'/>", dataIndex: 'vendorName'},
      {header: "<fmt:message key='aimir.prepayment.municipalityCode'/>",
        dataIndex: 'municipalityCode'},
      {header: "<fmt:message key='aimir.hems.prepayment.transactionNum'/>", 
        dataIndex: 'lastTokenId'},
      {header: "<fmt:message key='aimir.prepayment.authCode'/>", dataIndex: 'authCode',
        align: 'right'},
      {header: "<fmt:message key='aimir.deposit.chargecredit'/>", dataIndex: 'chargedCredit', 
        align: 'right'},
      {header: "<fmt:message key='aimir.prepayment.chargearrears'/>",
        dataIndex: 'chargedArrears', align: 'right'},  
      {header: "<fmt:message key='aimir.credit'/>", dataIndex: 'balance',
        align: 'right'},
      {header: "<fmt:message key='aimir.arrears'/>", dataIndex: 'arrears',
        align: 'right'}      
    ],
    defaults: {
      sortable: true,
      menuDisable: true
    }
  });  

  var monthlyListModel = new Ext.grid.ColumnModel({
    columns: [
      {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'},
      {header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName'},
      {header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId'},
      {header: "<fmt:message key='aimir.vendor'/>", dataIndex: 'vendorName'},
      {header: "<fmt:message key='aimir.prepayment.municipalityCode'/>", 
        dataIndex: 'municCode'},
      {header: "<fmt:message key='aimir.time.from'/> <fmt:message key='aimir.time.date'/>", 
        dataIndex: 'startDate'},
      {header: "<fmt:message key='aimir.open'/> <fmt:message key='aimir.balance'/>", 
        dataIndex: 'startBalance', align: 'right'},
      {header: "<fmt:message key='aimir.time.to'/> <fmt:message key='aimir.time.date'/>", 
        dataIndex: 'endDate'},
      {header: "<fmt:message key='aimir.board.close'/> <fmt:message key='aimir.balance'/>", 
        dataIndex: 'endBalance', align: 'right'},
      {header: "<fmt:message key='aimir.usage'/>", dataIndex: 'usedEnergy',
        align: 'right'},
      {header: "<fmt:message key='aimir.used.cost'/>", dataIndex: 'usedCost',
        align: 'right'},
      {header: "<fmt:message key='aimir.deposit.chargecredit'/>", dataIndex: 'chargedCredit',
        align: 'right'},
      {header: "<fmt:message key='aimir.prepayment.chargearrears'/>", 
        dataIndex: 'chargedArrears', align: 'right'}
    ],
    defaults: {
      sortable: true,
      menuDisable: true
    }
  });

  var dailyListSM = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
  var monthlyListSM = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

  var dailyListBbar = new Ext.PagingToolbar($.extend(true, 
    {store: dailyListStore, pageSize: DAILY_SIZE}, 
    bbar));

  var monthlyListBbar = new Ext.PagingToolbar($.extend(true, 
    {store: monthlyListStore, pageSize: MONTHLY_SIZE},
    bbar));

  // dailyList Ext.grid.EditorGridPanel property
  var dailyListProp = $.extend(true, {}, gridProp, {
    height: 635,
    colModel: dailyListModel,
    sm: dailyListSM,
    bbar: dailyListBbar,
    store: dailyListStore,
    renderTo: 'dailyListGrid'
  });  

  // monthlyList Ext.grid.EditorGridPanel property
  var monthlyListProp = $.extend(true, {}, gridProp, {
    height: 605,
    colModel: monthlyListModel,
    sm: monthlyListSM,
    bbar: monthlyListBbar,
    store: monthlyListStore,
    renderTo: 'monthlyListGrid'
  });

  var renderGrid = function() {
    dailyListGrid = new Ext.grid.EditorGridPanel(dailyListProp);
    monthlyListGrid = new Ext.grid.EditorGridPanel(monthlyListProp);

    eventHandler.dailyListSearch();
    eventHandler.monthlyListSearch();
  };

  var eventHandler = {
    modifiedDateFormat: function(date) {
      var $this = $(this);

      $.getJSON("${ctx}/common/convertLocalDate.do", 
        {supplierId: supplierId, dbDate: date},
        function(data) {            
          $this.siblings("." + $this.attr('name')).val(data.localDate);
        });
    },

    dailyListSearch: function() {
      var params = $.extend(true, {}, dailyListParams, {
        supplierId: supplierId,
        searchDate: $("input[name=searchDate]").val(),
        vendorId: $("#dailyListForm select[name=depositVendorList]").val()
      });
      dailyListStore.baseParams = params;
      dailyListStore.load();
    },

    monthlyListSearch: function() {
      var params = $.extend(true, {}, monthlyListParmas, {
        supplierId: supplierId,
        startDate: $("input[name=startDate]").val(),
        endDate: $("input[name=endDate]").val(),
        vendorId: $("#monthlyListForm select[name=depositVendorList]").val()
      });
      monthlyListStore.baseParams = params;
      monthlyListStore.load();
    },

    dailyListExcel: function() {
      reportType = 'daily';
      var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
      window.open(
        "${ctx}/gadget/prepaymentMgmt/salesExcelDownloadPopup.do", "DailySalesListExcel", opt);
    },

    monthlyListExcel: function() {
      reportType = 'monthly';
      var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
      window.open(
        "${ctx}/gadget/prepaymentMgmt/salesExcelDownloadPopup.do", "MonthlySalesListExcel", opt);
    },

    dailyListGridInit: function() {
      dailyListGrid.getView().refresh();
    }, 

    monthlyListGridInit: function() {
      monthlyListGrid.getView().refresh();
    }
  };

  var bind = function() {
    $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
    $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
    $('input[name=searchDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});    
    $('a[href=#dailyList]').click(eventHandler.dailyListGridInit);
    $('a[href=#monthlyList]').click(eventHandler.monthlyListGridInit);
    $("#dailyListForm span#dailyListSearch").click(eventHandler.dailyListSearch);
    $("#dailyListForm span#dailyListExcel").click(eventHandler.dailyListExcel);
    $("#monthlyListForm span#monthlyListSearch").click(eventHandler.monthlyListSearch);
    $("#monthlyListForm span#monthlyListExcel").click(eventHandler.monthlyListExcel);
  };

  var getUserInfo = function(callback) {
    $.getJSON("${ctx}/common/getUserInfo.do", 
      function(data) {
        supplierId = data.supplierId;
        if (callback) {
         callback();
        }
    }); 
  };

  /*일간, 월간 달력 폼을 초기화 한다.*/
  var initCalendar = function() {
    var searchDate = new Date();
    var startDate = new Date();
    startDate.setMonth(startDate.getMonth() - 1);

    var searchDateProp = $.extend(true, calendarProp);
    var startDateProp = $.extend(true, calendarProp);
    var endDateProp = $.extend(true, calendarProp);

    $('input[name=searchDate]').datepicker(searchDateProp);
    $('input[name=searchDate]').datepicker('setDate', searchDate);

    $('input[name=startDate]').datepicker(startDateProp);
    $('input[name=startDate]').datepicker('setDate', startDate);

    $('input[name=endDate]').datepicker(endDateProp);
    $('input[name=endDate]').datepicker('setDate', searchDate);

    var initDateFormat = function(inst ,date) {
      // Date객체의 값을 yyyymmdd 형태로 전환 
      var dbDate = $.datepicker.formatDate('yymmdd', date);

      // locale Date 값을 가져와서 input.name객체에 locale Date값을 반환한다. 
      $.getJSON("${ctx}/common/convertLocalDate.do", 
        {supplierId: supplierId, dbDate: dbDate},
        function(data) {            
          $(inst).siblings("." + inst.attr('name')).val(data.localDate);
        });
    };

    initDateFormat($('input[name=searchDate]'), searchDate);
    initDateFormat($('input[name=startDate]'), startDate);    
    initDateFormat($('input[name=endDate]'), searchDate);
  };
  
  var init = function() {
    Ext.QuickTips.init();
    getUserInfo(function() {
      initCalendar();
      renderGrid();
      $("#contentWrapper").tabs();

      bind();
      hide();  
    });
  };

  // window resize event
  $(window).resize(function() {
    dailyListGrid.getView().refresh();
    monthlyListGrid.getView().refresh();
  });  

  $(document).ready(function() {
    init();
  });  
  /*]]>*/    
  </script>
</body>
</html>