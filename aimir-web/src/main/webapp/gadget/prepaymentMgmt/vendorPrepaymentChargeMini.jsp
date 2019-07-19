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
      margin-right: 10px;
    }
    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }    
    span.bold-font {
      font-weight: bold;
    }    
    .hidden {
      display: none;
      width: 0px;
    }
    .no-width {
      visibility: hidden;
      width: 0px;
    }
    /* selectbox wrapper 관련 margin 제거*/
    div.selectbox-wrapper {
      margin: 0px;
    }
    input.selectbox {
      display: block;
    }
    .inline-block {
      display: inline-block;
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
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
  <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
  <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //공급사ID
    var supplierId="${supplierId}";

    // 3rd party vendor인지 여부
    var isVendor = ${isVendor};    

    //vendor loginId
    var vendor ="${vendor}";    
    
    //vendor 예치금
    var deposit = "${deposit}";

    // vendor의 RoleName
    var vendorRole = "${role}";     
    
    var contractGrid;
    var historyGrid;
    var vendorHistoryGrid;
    
    var receiptPopupWindow;

    var PAGE_SIZE = 2;
    var VENDOR_PAGE_SIZE = 9;

    var storeParams = {
      page: 1,
      start: 0,
      limit: PAGE_SIZE
    };

    var calendarProp = {
      showOn: 'button',
      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
      buttonImageOnly: true
    };

    var contractListParams = $.extend(true, {supplierId: supplierId}, storeParams);
    var historyListParams = $.extend(true, {
      contractNumber: null,
      searchStartMonth: '00000000',
      searchEndMonth: '99999999'
    }, storeParams);

    var vendorHistoryParams = $.extend(true, {vendor: (vendorRole == "admin") ? "" : vendor, 
      vendorRole: vendorRole,
      supplierId: supplierId,
      startDate:$("#depositHistory input[name=startDate]").val(),
      endDate:$("#depositHistory input[name=endDate]").val()}, storeParams);

    vendorHistoryParams.limit = VENDOR_PAGE_SIZE;

    var contractListStore = new Ext.data.JsonStore({
      baseParams: contractListParams,
      url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentChargeList.do",
      totalProperty: 'totalCount',
      root: 'result',
      fields: ['contractNumber', 'customerNo', 'currentCredit', 'currentArrears'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
          } else { params.page = 1;}
        }
      }          
    });

    var historyListStore = new Ext.data.JsonStore({
      baseParams: historyListParams,
      url: "${ctx}/gadget/prepaymentMgmt/getChargeHistoryList.do",
      totalProperty: 'totalCount',
      root: 'result',
      fields: ["lastTokenDate", "chargedCredit", "chargedArrears", "balance", "arrears"],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
          } else { params.page = 1;}
        }        
      }  
    });

    var vendorHistoryStore = new Ext.data.JsonStore({
      baseParams: vendorHistoryParams,
      url: "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryList.do",
      totalProperty: 'count',
      root: 'list',
      fields: ['customerId', 'chargeCredit', 'chargeDeposit', 'deposit'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + VENDOR_PAGE_SIZE) / VENDOR_PAGE_SIZE);
          } else { params.page = 1;}          
        }
      }
    })

    var saveBtnArea = function(value, meta, rec) {
      var id  = Ext.id();
      var $div = $("<div></div>").attr("id", id);
      var button = function() {
        if( $("#"+id).length > 0 && ($div.children().length < 1)) {
          new Ext.Button({
            text: "<fmt:message key='aimir.save2'/>",            
            width: 40, 
            handler: function(b, e) {
              eventHandler.saveChargeAmount(rec);
            }
          }).render(id);
        } else {
          button.defer(100);
        }
      };
      button.defer(100);
      return $div[0].outerHTML;
    };

    var receiptBtnArea = function(value, meta, rec) {      
      var id = Ext.id();
      var $div = $("<div></div>").attr("id", id);
      var button = function() {
        if( $("#"+id).length > 0 && ($div.children().length < 1)) {
          new Ext.Button({
            text: '<fmt:message key="aimir.receipt"/>',            
            width: 50,
            handler: function(b, e) {
              eventHandler.openReceiptPopup(rec.json);
            }
          }).render(id);
        } else {
          button.defer(100);
        }        
      };
      button.defer(100);
      return $div[0].outerHTML;
    };

    var contractListModel = new Ext.grid.ColumnModel({
      columns: [
          {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
         ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo'}
         ,{header: "<fmt:message key='aimir.balance'/>", 
           dataIndex: 'currentCredit',  align: 'right'}
         ,{header: "<fmt:message key='aimir.hems.prepayment.currentarrears'/>",
           dataIndex: 'currentArrears', align: 'right', css: "padding-right:5px", 
           hidden: true}  
      ],
      defaults: {
          sortable: true
         ,menuDisabled: true                   
         ,renderer: addTooltip
      }
    });
    
    var historyListModel = new Ext.grid.ColumnModel({
      columns: [
        {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
        {header: "<fmt:message key='aimir.chargeAmount'/>", align: 'right'},
        {header: "<fmt:message key='aimir.prepayment.chargearrears'/>", 
        align: 'right', hidden: true},
        {header: "<fmt:message key='aimir.balance'/>"
          , align: 'right'},
        {header: "<fmt:message key='aimir.hems.prepayment.arrearsaftercharged'/>"
          , align: 'right' ,css: "padding-right:5px", hidden: true}
      ],
      defaults: {sortable: true, 
                menuDisabled: true,
                renderer: addTooltip
       }       
    });

    var vendorHistoryModel = new Ext.grid.ColumnModel({
      columns: [
        {header: "<fmt:message key='aimir.customerid'/>"},
        {header: "<fmt:message key='aimir.deposit.chargecredit'/>", align: 'right'},
        {header: "<fmt:message key='aimir.deposit.chargedeposit'/>", align: 'right'},
        {header: "<fmt:message key='aimir.deposit'/>", align: 'right', 
          css: 'padding-right:5px'}
      ],
      defaults : {
        sortable: true,
        menuDisable: true,
        renderer: addTooltip
      }
    });

    var sm = {
      singleSelect: true,
      moveEditorOnEnter: false
    };
    
    var contractListSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var historyListSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var vendorHistorySm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

    var bbar = {
      pageSize: PAGE_SIZE,
      displayInfo: true,
      displayMsg: ' {0} - {1} / {2}'
    };

    var contractBbar = new Ext.PagingToolbar($.extend(
      true, 
      {}, 
      {store: contractListStore}, 
      bbar));

    var historyBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: historyListStore},
      bbar));
    
    var vendorHistoryBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: vendorHistoryStore},
      bbar,
      {pageSize: VENDOR_PAGE_SIZE}));
    
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

    var contractListProp = $.extend(true, {}, gridProp, {
      height: 102,
      colModel: contractListModel,
      sm: contractListSm,
      bbar: contractBbar,
      store: contractListStore,
      renderTo: 'prepaymentChargeDiv'
    });

    var historyListProp = $.extend(true, {}, gridProp, {
      height: 102,
      colModel: historyListModel,
      sm: historyListSm,
      bbar: historyBbar,
      store: historyListStore,
      renderTo: "prepaymentChargeHistoryDiv"
    });

    var vendorHistoryProp = $.extend(true, {}, gridProp, {
      height: 234,
      colModel: vendorHistoryModel,
      sm: vendorHistorySm,
      bbar: vendorHistoryBbar,
      store: vendorHistoryStore,
      renderTo: "depositChargeHistoryDiv"
    });

    var eventHandler = {
      initDateFormat: function(date, inst) {
        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: date},
          function(data) {            
            $("." + inst.attr('name')).val(data.localDate);
          });
      },

      selectedHistorySearch: function(sm, rowIndex, rec) {
        var contractNumber = rec.json.contractNumber;
        var params = $.extend(true, {}, historyListParams, {
          contractNumber: contractNumber,
          searchStartMonth: $("#historyForm input[name=startDate]").val() || '00000000',
            searchEndMonth: $("#historyForm input[name=endDate]").val() || '99999999'
        });        
        $("#historyForm input[name=contractNumber]").val(contractNumber);

        historyListStore.baseParams = params;
        historyListStore.load({
          params: params
        });
      },

      historyListSearch: function() {
        var params = $.extend(true, {}, historyListParams, {
          contractNumber: $("#historyForm input[name=contractNumber]").val(),
          searchStartMonth: 
            $("#historyForm input[name=startDate]").val() || '00000000',
          searchEndMonth: $("#historyForm input[name=endDate]").val() || '99999999'
        });

        historyListStore.baseParams = params;
        historyListStore.load();
      },

      modifiedDateFormat: function(date) {        
        var $this = $(this);

        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: date},
          function(data) {            
            $("." + $this.attr('name')).val(data.localDate);
          });
      },

      contractListSearch : function() {
        var params = $.extend(true, {}, contractListParams, {
          contractNumber: $("#contractNumber").val(),
          customerNo: $("#customerNo").val(),
          customerName: $("#customerName").val(),
          mdsId: $("#mdsId").val()
        });
        contractListStore.baseParams = params;
        contractListStore.load({
          params: params
        });
      },

      depositHistoryListSearch: function() {
        var params = $.extend(true, {}, vendorHistoryParams, {
          reportType: $("#depositHistory select[name=reportType]").val(),
          subType: $("#depositHistory select[name=subType]").val(),
          contract: $("#depositHistory input[name=contract]").val(),
          customerName: $("#depositHistory input[name=customerName]").val(),
          customerNo: $("#depositHistory input[name=customerId]").val(),
          meterId: $("#depositHistory input[name=meterId]").val(),
          startDate: $("#depositHistory input[name=startDate]").val(),
          endDate: $("#depositHistory input[name=endDate]").val()
        });
        vendorHistoryStore.baseParams = params;
        vendorHistoryStore.load();
      },

      saveChargeAmount: function(rec) {
        var isAction = false;
        var chargeAmount = Number(rec.get("chargeAmount"));
        var chargeArrears = Number(rec.get("chargeArrears"));
        var currentArrears = Number(rec.get("currentArrears"));
        var currentAmount = Number(rec.get("currentCredit"));

        var saveAction = function() {
          if (chargeAmount != null && chargeAmount != "" && Number(chargeAmount) != 0) {
              Ext.Msg.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>", function(btn) {
                  if (btn == "yes") {
                      emergePre();
                      var params = {
                          contractNumber: rec.json.contractNumber,
                          contractId: rec.json.contractId,
                          mdsId: rec.json.mdsId,
                          lastTokenId: rec.json.lastTokenId,
                          contractDemand: rec.json.contractDemand || 0,
                          tariffCode: rec.json.tariffCode,
                          amount: chargeAmount || 0,
                          arrears: chargeArrears || 0,
                          supplierId : supplierId
                      };

                      $.post("${ctx}/gadget/prepaymentMgmt/vendorSavePrepaymentCharge.do",
                            params,
                            function(json) {
                              var receiptParam = {};

                              if (json != null && json.result == "success") {
                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>",
                                        function() {
                                          contractListStore.reload();
                                          var hParams = $.extend(true, {}, 
                                            historyListParams, 
                                            {contractNumber: params.contractNumber});

                                          historyListStore.baseParams = hParams;
                                          historyListStore.load();
                                          eventHandler.refreshDeposit(json.deposit);

                                          receiptParam.prepaymentLogId = json.prepaymentLogId;
                                          receiptParam.contractId = rec.json.contractId;
                                          eventHandler.chargeAfterReceipt(receiptParam);
                                        }
                                  );

                                } else {
                                    Ext.Msg.alert("<fmt:message key="aimir.error"/>", json.result);
                                }
                                hide();
                                return;
                             }
                      );
                  }
              });
          } else {
              Ext.Msg.alert("<fmt:message key="aimir.error"/>","<fmt:message key="aimir.updatedata.notexist"/>");
              hide();
              return;
          }
        }
        if (!isNaN(chargeArrears) && !isNaN(currentArrears) && chargeArrears > currentArrears) {
          Ext.Msg.alert("<fmt:message key='aimir.alert'/>",
            "<fmt:message key='aimir.msg.check.input.arrears'/>");
          return;
        }
        if (isNaN(chargeAmount)) {
          return;
        }
        Ext.Msg.prompt(rec.json.customerName, 'Please retype charge amount.', function(btn, text) {
          var prompt = Number(text);
          if (btn == 'ok' && !isNaN(chargeAmount)) {
            if(prompt == chargeAmount) {
              saveAction();
            } else {
              Ext.Msg.alert("<fmt:message key='aimir.alert'/>", 
                "<fmt:message key='aimir.msg.check.input.value'/>");
            }
          } 
        });        
      },

      refreshDeposit: function (value) {
        if(value || value == 0) {
          var value = NumberUtil.thousandSeparator(Number(value));
          $("label.current_deposit").text(value);
        } else {
          var value = NumberUtil.thousandSeparator(Number(deposit));
          $("label.current_deposit").text(value);
        }
      }, 

      chargeAfterReceipt: function(rec) {
        Ext.MessageBox.confirm(
          "<fmt:message key='aimir.button.confirm'/>", 
          "<fmt:message key='aimir.msg.confirm.print.receipt'/>",
          function(result) {
            if (result === "yes") {
             eventHandler.openReceiptPopup(rec);
            }
          }
        );
      },

      openReceiptPopup: function(rec) {
        var url = "${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptPopup.do";
        var opt = "dialogWidth:360px; dialogHeight:715px;resizable:no;status:no;help:no;center:yes;";
        var params = {
          vendor: vendor,
          supplierId: supplierId,
          contractId: rec.contractId,
          prepaymentLogId: rec.prepaymentLogId
        }
        
        if ( receiptPopupWindow ) {
            receiptPopupWindow.close();
          }
        
        var queryString = CommonUtil.getQueryString(params);
        receiptPopupWindow = window.open(url + queryString, "receiptPopupWindow", opt);
      },

      hideChargeArrears: function(rec) {
        return false;
      },

      initChargingTab: function() {
        contractListStore.reload();
        historyListStore.reload();
      },

      initHistoryTab:function() {
        eventHandler.depositHistoryListSearch();
      }
    };

    var bind = function() {
      $("#contractForm span#contractSearch").click(eventHandler.contractListSearch);
      $('#historyForm span#historySearch').click(eventHandler.historyListSearch);
      $('#depositHistory span#depositHistorySearch').click(eventHandler.depositHistoryListSearch);
      $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
      $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
      contractListSm.addListener('rowselect', eventHandler.selectedHistorySearch);     
      $('a[href=#chargeTab]').bind('click', eventHandler.initChargingTab);
      $('a[href=#historyTab]').bind('click', eventHandler.initHistoryTab);
    };

    var renderGrid = function() {

      contractGrid = new Ext.grid.EditorGridPanel(contractListProp);
      historyGrid = new Ext.grid.EditorGridPanel(historyListProp);
      vendorHistoryGrid = new Ext.grid.EditorGridPanel(vendorHistoryProp);      
      contractListStore.load({params: contractListParams});
      historyListStore.load({params: historyListParams});
      eventHandler.depositHistoryListSearch();
    };

    var initCalendar = function() {
      calendarProp.dateFormat = 'yymmdd';
      calendarProp.altFormat = '';
      var startDate = new Date();
      startDate.setMonth(startDate.getMonth() - 1);
      var endDate = new Date();
      var startProp = $.extend(true, {defaultDate: startDate}, calendarProp);
      var endProp = $.extend(true, {defaultDate: endDate}, calendarProp);
      var st = $('input[name=startDate]').datepicker(startProp);
      var end = $('input[name=endDate]').datepicker(endProp);
      $('input[name=startDate]').datepicker('setDate', startDate);
      $('input[name=endDate]').datepicker('setDate', endDate);

      var initDateFormat = function(inst ,date) {
        var dbDate = $.datepicker.formatDate('yymmdd', date);
        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: dbDate},
          function(data) {            
            $("." + inst.attr('name')).val(data.localDate);
          });
      };

      initDateFormat($('input[name=startDate]'), startDate);
      initDateFormat($('input[name=endDate]'), endDate);
    };

    var init = function() {
      Ext.QuickTips.init();
      $("#report-type").selectbox();
      $("#sub-type").selectbox();
      initCalendar();
      renderGrid();
      $("#menu").tabs();
      eventHandler.refreshDeposit(); 
      bind();           
      hide();
    };

    Ext.onReady(function() {
      init();
    });

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "") {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }

    /*]]>*/
    </script>
</head>
<body>
  <div id="menu">
    <ul>
      <li>
        <a href="#chargeTab">
          <fmt:message key="aimir.charging"/>
        </a>
      </li>
      <li>
        <a href="#historyTab">
          <fmt:message key='aimir.hems.prepayment.chargehistory'/>
        </a>
      </li>
    </ul>

    <div id="chargeTab" >

      <!--검색조건-->
      <form id="contractForm">
        <div>
          <span>
            <fmt:message key="aimir.contractNumber"/>
            <input id="contractNumber" type="text" style="width:60px;">
          </span>
          <span>
            <fmt:message key="aimir.customerid"/>
            <input id="customerNo" type="text" style="width:60px;">
          </span>
          <span id="contractSearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
        </div>
        <div>              
          <span class='bold-font'>
            <fmt:message key="aimir.deposit"/>
            <label class="current_deposit"></label>
          </span>
        </div>
      </form>
      <!--검색조건 끝-->

      <!-- search-background DIV (E) -->

      <div id="prepaymentChargeDiv"></div>

      <form id="historyForm" >
        <div class="wrapper">
          <input name="contractNumber" class="hidden" type="text"></input>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate" type='text' readOnly/><input name="startDate" type="hidden"/>
            <label>~</label>
            <input class="alt endDate" type='text' readOnly/><input name="endDate" type="hidden"/>
          </span>
          <span id="historySearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>    
        </div>
      </form>

      <div id="prepaymentChargeHistoryDiv"></div>
    </div>   

    <div id="historyTab">
     
      <form id="depositHistory" >
        <div>
          <span class='inline-block'> 
            <select id='report-type' name='reportType'>
              <option value='all'><fmt:message key='aimir.all'/></option>
              <option value='deposit'><fmt:message key='aimir.deposit'/></option>
              <option value='sales'><fmt:message key='aimir.sales'/></option>
            </select>
          </span>
          <span class='inline-block'> 
            <select id='sub-type' name='subType'>
              <option value='all'><fmt:message key='aimir.all'/></option>
              <option value='cancelled'><fmt:message key='aimir.cancelled'/></option>
              <option value='unCancelled'><fmt:message key='aimir.uncancelled'/></option>
            </select>
          </span>          
        </div>
        <div>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate" type='text' readOnly/><input name="startDate" type="hidden"/>
            <label>~</label>
            <input class="alt endDate" type='text' readOnly/><input name="endDate" type="hidden"/>
          </span>
          <span id='depositHistorySearch' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.search"/></a>
          </span>   
        </div>        
      </form>

      <div id="depositChargeHistoryDiv"></div>
    </div>  
  
  </div>
</body>
</html>