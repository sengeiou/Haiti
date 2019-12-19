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
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }
	#contentWrapper {
		margin-left: 10px;
		margin-right: 10px;
	}
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }	
    /* ext-js grid 행 높이 고정 
	   cancel이 버튼인 row와 텍스트인 경우 row의 높이가 다르므로 임의로 수정 
    */
    td.x-grid3-col.x-grid3-cell {
      height: 28px;
    }    

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }    
	form {
		margin-top: 10px;
		margin-bottom: 10px;		
	}
	form.tax input {
		width: 30px;
		text-align: right;
		padding-right: 5px;
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

  .hidden {
    display: none;
  }
  .no-width {
    width: 0px;
    visibility: hidden;
  }

  button.download {
    cursor: pointer;
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
    .width49 {
     width: 45px;
    }
    .width25 {
     width: 45px;
    }
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>

</head>
<body>
	<div id="menu">
		<ul>
			<li class="depositTab">
				<a href= "#depositTab">
					<fmt:message key='aimir.deposit'/>
				</a>
			</li>
            <li class="historyTab">
                <a href="#historyTab">
                    <fmt:message key='aimir.hems.prepayment.chargehistory'/>
                </a>
            </li>
			<li class="reportTab">
				<a href= "#reportTab">
					<fmt:message key='aimir.report'/>
				</a>
			</li>
		</ul>

		<div id="depositTab">
			<div id="contentWrapper">
				<form class="tax">
					<span>
						<label>
							<fmt:message key='aimir.tax'/>&nbsp;<fmt:message key='aimir.customer.usage.rate'/>
							<input name='tax'/>
							<span>%</span>
					    <em class='am_button tax-update'>
				      	<a><fmt:message key='aimir.button.update'/></a>
		    			</em>    
						</label>
					</span>
					<span>
						<label>
							<fmt:message key='aimir.prepayment.commission'/>&nbsp;<fmt:message key='aimir.customer.usage.rate'/>
							<input name='commission'/>
							<span>%</span>
					    <em class='am_button commission-update'>
					      <a><fmt:message key='aimir.button.update'/></a>
					    </em>     
						</label>				
					</span>
				</form>
				<form class="vendor_list">
					<span>
						<label><fmt:message key="aimir.vendor"/> <fmt:message key="aimir.id"/></label>
						<input name="id" type="text">
					</span>
					<span>
						<label><fmt:message key="aimir.vendor"/> <fmt:message key="aimir.name"/></label>
						<input name="name" type="text">
					</span>
					<span class="am_button margin-l10 margin-t1px">
						<a><fmt:message key="aimir.button.search"/></a>
					</span>
				</form>
				<div id='vendorList' class="grid-wrapper vendor"></div>	
				
				<form class="deposit_history">
					<div>
						<input name="vendor" type="text" class="hidden"/>
	            <span class='inline-block'> 
	              <select id='report-type' name='reportType'>
	                <option value='all'><fmt:message key='aimir.all'/></option>
	                <option value='deposit'><fmt:message key='aimir.deposit'/></option>
	                <option value='sales'><fmt:message key='aimir.sales'/></option>
	              </select>
	            </span>
						<span>
							<label><fmt:message key='aimir.buildingMgmt.contractNumber'/></label>
							<input name="contract" type="text"/>
						</span>
						<span>
							<label><fmt:message key="aimir.customer"/> <fmt:message key="aimir.userreg.name"/>
							</label><input name="customerName" type="text"/>
						</span>
						<span>
							<label><fmt:message key="aimir.customerid"/></label>
							<input name="customerId" type="text"/>
						</span>
						<span>
							<label><fmt:message key="aimir.meterid"/></label>
							<input name="meterId" type="text"/>		
						</span>
						<span>
							<input name="onlyLoginData" type="checkbox" value="check"/>
							<label><fmt:message key="aimir.onlyLoginData"/></label>
						</span>
					</div>
					<div>
						<span>
							<label><fmt:message key="aimir.searchDate"/></label>
							<input class="alt startDate" type='text' readOnly/>
							<input name="startDate" class="no-width" type="text"/>		
							<label>~</label>
							<input class="alt endDate" type='text' readOnly/>
							<input name="endDate" class="no-width" type="text"/>		
						</sapn>
						<span class="am_button margin-l10 margin-t1px">
							<a><fmt:message key="aimir.button.search"/></a>
						</span>		
					</div>
				</form>
				<div id='depositHisotry' class="grid-wrapper history"></div>
			</div>
		</div>
        
        <div id="historyTab">
            <div id="contentWrapper">
                <!--검색조건-->
                <form id="depositHistory">
                    <div>
                    	<span class='inline-block'>
	                        <select id='sub-type' name='subType'>
	                        	<option value='all'  selected><fmt:message key='aimir.all'/></option>
	                    		<option value='unCancelled'><fmt:message key='aimir.deposit'/></option>
	                    		<option value='cancelled'><fmt:message key='aimir.cancelled'/></option>
	                		</select>
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
                        <span>
                        	<input name="onlyLoginData" type="checkbox" value="check"/>		
							<label><fmt:message key="aimir.onlyLoginData"/></label>
						</span>
                    </div>
                    <div>
                        <span>
                            <label><fmt:message key="aimir.searchDate"/></label>
                            <input class="alt startDate" name="startDateDisplay" type='text' readOnly/>
                            <input name="startDate" class="no-width" type="text"/>    
                            <label>~</label>
                            <input class="alt endDate" name="endDateDisplay" type='text' readOnly/>
                            <input name="endDate" class="no-width" type="text"/>    
                        </span>
                        <span id='depositHistorySearch' class="am_button margin-l10 margin-t1px">
                            <a><fmt:message key="aimir.button.search"/></a>
                        </span>   
                        <span id='depositHistoryExcel' class="am_button margin-l10 margin-t1px">
                            <a><fmt:message key="aimir.button.excel"/></a>
                        </span>             
                        <span id='depositHistoryTotalExcel' class="am_button margin-l10 margin-t1px">
                            <a><fmt:message key="aimir.total"/> <fmt:message key="aimir.button.excel"/></a>
                        </span>
                    </div>        
                </form>
                <!--검색조건 끝-->
                <div id="depositHistoryDiv"  class="grid-wrapper"></div>
            </div>
        </div>  
        
        <div id="reportTab">
           <div id="contentWrapper">   
                <form class="reportSearch">
                    <div> 
                        <span>
                            <label><fmt:message key="aimir.contractNumber"/></label>
                        </span>
                        <span  class='inline-block'>
                            <input name="searchWord" id='searchWord' type="text" style="width: 190px" /> 
                            <!-- <input type='hidden' id='districtLocationId' value='1'></input> -->
                            <td class="withinput" width="120px"></td>
                        </span>
                    </div>
                    <div id="searchReportHtml">
                        <%-- <span class='inline-block'>
                            <label><fmt:message key="aimir.searchDate"/></label>
                        </span> 
                        <span class='inline-block'>
                            <select id='yearCombo' name='yearCombo' style='width: 49px'/>
                        </span>
                        <span class='inline-block'>
                            <select id='monthlyCombo' name='monthlyCombo'  style='width: 25px'/>
                        </span>
                        <span id='monthlyReportSearch' class='am_button marL10'>
                            <a><fmt:message key='aimir.button.search'/></a>
                        </span> --%>
                    </div>
                </form>
                <div> 
                    <span>
                        <div class="downloadbox">
                            <form class="hidden">
                                <input name="fileName" class="hidden"></input>
                                <input name="filePath" class="hidden"></input>
                            </form>
                            <table class="download" id="filelist"></table>
                            <iframe name="downFrame" class="hidden"></iframe>
                        </div>       
                    </span>
                </div>
                <div class="clear">
                        <div id="treeDivAOuter" class="tree-billing auto"
                            style="display: none;">
                            <div id="treeDivA"></div>
                        </div>
                </div>
            </div>
        </div>
	</div>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
var supplierId;
var PAGE_SIZE = 8;
var VENDOR_SIZE = 5;
var HISTORY_SIZE = 10;
var VENDOR_HISTORY_PAGE_SIZE = 15;

// 파일이름 목록
var file_path = "${filePath}";

var isAdmin = ${isAdmin};

var fromDepositGadget = true;

//vendor(Operator) loginId
var vendor ="${vendor}";

// vendor의 RoleName
var vendorRole = "${role}"; 

// 수정권한
var editAuth = "${editAuth}";

//기존에 열려있는 popup window
var receiptPopupWindow;

// 화면 상단 그리드 
var vendorListGrid;
// 화면 하단 그리드
var historyListGrid;

var vendorHistoryGrid;
var vendorHistoryGridOn=false;

var isHiddenCancelBtn = (vendorRole == 'admin') ? false : true;

var storeParams = {
	page: 1, 
	start: 0,
	limit: PAGE_SIZE	
};

var logoImg = "${logoImg}"; // 로고 이미지 파일

var loginIntId="";
var loginId = "";
/**
 * 유저 세션 정보 가져오기
 */
$.getJSON('${ctx}/common/getUserInfo.do', function(json) {
    if (json.supplierId != "") {
        loginIntId = json.operatorId;
        loginId = json.loginId;
    }
});

var vendorListParams = $.extend(true, {}, storeParams, {limit:VENDOR_SIZE, loginId:loginId});
var historyParams = $.extend(true, {}, storeParams, {limit:HISTORY_SIZE});
var vendorHistoryParams = $.extend(true, {vendor: $("#depositHistory select[name=depositVendorList]").val(), 
    vendorRole: vendorRole,
    fromDepositGadget : fromDepositGadget,
    supplierId: supplierId,
    reportType:"deposit"}, storeParams, {limit:VENDOR_HISTORY_PAGE_SIZE});
  
var vendorStore = new Ext.data.JsonStore({
	baseParams: vendorListParams,
	url: "${ctx}/gadget/prepaymentMgmt/operatorList.do",
	totalProperty: 'count',
	root: 'list',
	fields: ['id', 'name', 'tel', 'email', 'location', 'lastChargeDate','deposit'],
	listeners: {
		beforeload: function(store, options) {
			var params = options.params;
			if (params.start && params.start > 0) {
				params.page = ((params.start + VENDOR_SIZE) / VENDOR_SIZE);
			} else { params.page = 1;}
		},	
	}
});

var historyStore = new Ext.data.JsonStore({
	baseParams: historyParams,
	url: "${ctx}/gadget/prepaymentMgmt/historyList.do",	
	totalProperty: 'count',
	root: 'list',
	fields: ['vendor', 'contractNo', 'customerId', 'meter', 'customerName', 'address',
	'changeDate', 'chargeCredit', 'chargeDeposit', 'deposit'],
	listeners: {
		beforeload: function(store, options) {			
			var params = options.params;
			if (params.start && params.start > 0) {
				params.page = ((params.start + HISTORY_SIZE) / HISTORY_SIZE);
			} else { params.page = 1;}
		}
	}
});

var vendorHistoryStore = new Ext.data.JsonStore({
    baseParams: vendorHistoryParams,
    url: "${ctx}/gadget/prepaymentMgmt/historyList.do",
    totalProperty: 'count',
    root: 'list',
    fields: ['vendor', 'changeDate', 'chargeCredit', 'chargeDeposit', 'deposit'],
    listeners: {
      beforeload: function(store, options) {
        var params = options.params;
        if (params.start && params.start > 0) {
          params.page = ((params.start + VENDOR_HISTORY_PAGE_SIZE) / VENDOR_HISTORY_PAGE_SIZE);
        } else { params.page = 1;}          
      }
    }
  });

var saveBtn = function(value, meta, rec) {	
	var id = Ext.id();
	var $div = $("<div></div>").attr("id", id);
	var button = function() {
		if($("#" + id).length > 0 && ($div.children().length<1)) {
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

var receiptBtn = function(value, meta, rec) {
	// 취소된 결제내역의 경우 영수증 버튼이 아닌 메시지가 출력된다. 
	if (rec.json.isCanceledByDeposit) {
		return "<fmt:message key='aimir.canceled'/>";
	}

	var id = Ext.id();
	var $div = $("<div></div>").attr("id", id);
	var button = function() {
		if( $("#"+id).length > 0 && ($div.children().length < 1)) {
			new Ext.Button({
				text: '<fmt:message key="aimir.receipt"/>',            
				width: 50,
				handler: function(b, e) {
					eventHandler.receiptPopUp(rec);
				}
			}).render(id);
		} else {
			button.defer(100);
		}        
	};

	button.defer(100);
	return $div[0].outerHTML;
};

var cancelBtnArea = function(value, meta, rec) {
  var recentId = rec.store.reader.jsonData.recentId;
  var result = rec.store.reader.jsonData.list;
  var prepaymentLogId = rec.json.prepaymentLogId;
  var id = Ext.id();
  var $div = $("<div></div>").attr("id", id);  
  var hasRecentLog = false;

  var button = function() {
    // RenderArea가 document에 append되고 RenderArea에 버튼이 없는 경우         
    if( $("#"+id).length > 0 && ($div.children().length < 1)) {
      new Ext.Button({
        text: '<fmt:message key="aimir.cancel"/>',
        width: 60,
        handler: function(b, e) {
          eventHandler.cancel(rec.json);
        }
      }).render(id);
    } else {
      button.defer(100);
    }        
  };
  
  // 최근 아이디의 record인 경우 버튼을 렌더링한다. 
  if (prepaymentLogId == null && rec.json.isCanceledByDeposit != true) {
    button.defer(100);
    return $div[0].outerHTML;
  }
};   

var hiddenCol = (editAuth != "true") ? true : false;    // 권한에 따라 show/hidden

var vendorModel = new Ext.grid.ColumnModel({
	columns: [
		{header: "<fmt:message key='aimir.id'/>", dataIndex: 'id'},
		{header: "<fmt:message key='aimir.name'/>", dataIndex: 'name'},
		{header: "<fmt:message key='aimir.tel.no'/>", dataIndex: 'tel'},
		{header: "<fmt:message key='aimir.email'/>", dataIndex: 'email'},
		{header: "<fmt:message key='aimir.location'/>", dataIndex: 'location'},
		{header: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>", 
			dataIndex: 'lastChargeDate'},
		{header: "<fmt:message key='aimir.deposit'/>", dataIndex: 'deposit',
			align: 'right'},
		{header: "<fmt:message key='aimir.amount.paid'/>", dataIndex: 'charge', 
			align: 'right',
			renderer: Ext.util.Format.numberRenderer("0,000.0000"),
			editor: new Ext.form.NumberField({
				id: 'charge',
				allowBlank: true,
				allowNegative: true
			})},
		{header: "", renderer:saveBtn}
	],
	defaults: {
		sortable: true,
		menuDisable: true
	}
});

var historyModel = new Ext.grid.ColumnModel({
	columns: [
		{header: "<fmt:message key='aimir.vendor'/>"},
		{header: "<fmt:message key='aimir.buildingMgmt.contractNumber'/>"},
		{header: "<fmt:message key='aimir.customerid'/>"},
		{header: "<fmt:message key='aimir.meterid'/>"},
		{header: "<fmt:message key='aimir.customername'/>"},
		{header: "<fmt:message key='aimir.address'/>"},
		{header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
		{header: "<fmt:message key='aimir.deposit.chargecredit'/>", align: 'right'},
		{header: "<fmt:message key='aimir.netvalue'/>", align: 'right'},
		{header: "<fmt:message key='aimir.deposit'/>", align: 'right'},
		{header: "", renderer:receiptBtn},
		{header: "", renderer:cancelBtnArea, hidden: isHiddenCancelBtn}
	],
	defaults : {
		sortable: true,
		menuDisable: true
	}
});

var vendorHistoryModel = new Ext.grid.ColumnModel({
    columns: [
      {header: "<fmt:message key='aimir.vendor'/>"},
      {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
      {header: "<fmt:message key='aimir.deposit.chargecredit'/>", align: 'right'},
      {header: "<fmt:message key='aimir.deposit.chargedeposit'/>", align: 'right'},
      {header: "<fmt:message key='aimir.deposit'/>", align: 'right', 
        css: 'padding-right:10px'}
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

var vendorSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
var vendorHistorySm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
var historySm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

var _bbar = {	
	pageSize: PAGE_SIZE,
	displayInfo: true,
	displayMsg: ' {0} - {1} / {2}'
};

var vendorBbar = new Ext.PagingToolbar($.extend(true, {}, _bbar, {store: vendorStore, 
	pageSize: VENDOR_SIZE}));
var historyBbar = new Ext.PagingToolbar($.extend(true, {}, _bbar, {store: historyStore, 
	pageSize: HISTORY_SIZE}));
var vendorHistoryBbar = new Ext.PagingToolbar($.extend(true,
        {},_bbar,{store: vendorHistoryStore, pageSize: VENDOR_HISTORY_PAGE_SIZE}));

var calendarProp = {
	showOn: 'button',
	buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
	buttonImageOnly: true
};

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

var vendorProp = $.extend(true, {}, gridProp, {
	height: 152,
	colModel: vendorModel, 
	sm: vendorSm,
	bbar: vendorBbar,
	store: vendorStore,
	renderTo: 'vendorList'
});

var historyProp = $.extend(true, {}, gridProp, {
	height: 376,
	colModel: historyModel,
	sm: historySm,
	bbar: historyBbar,
	store: historyStore,
	renderTo: 'depositHisotry'
});


var vendorHistoryProp = $.extend(true, {}, gridProp, {
    height: 540,
    colModel: vendorHistoryModel,
    sm: vendorHistorySm,
    bbar: vendorHistoryBbar,
    store: vendorHistoryStore,
    renderTo: "depositHistoryDiv"
  });

var eventHandler = {
	vendorSearch:function() {
		var params = $.extend({}, vendorListParams, {
			loginId: $("form.vendor_list input[name=id]").val(),
			name: $("form.vendor_list input[name=name]").val()
		}); 
		vendorStore.baseParams = params;
		vendorStore.load({
			params: params
		});
	},

	historySearch: function() {

		var onlyLoginData = $('form.deposit_history :checkbox:checked');
		if(onlyLoginData.length == 0) {
			onlyLoginData = false;
		} else {
			onlyLoginData = true;
		}

		var params = $.extend({}, historyParams, {
				reportType: $("form.deposit_history select[name=reportType]").val(),
				vendor: $("form.deposit_history input[name=vendor]").val(),
				contract: $("form.deposit_history input[name=contract]").val(),
				customerName: $("form.deposit_history input[name=customerName]").val(),
				customerId: $("form.deposit_history input[name=customerId]").val(),
				meterId: $("form.deposit_history input[name=meterId]").val(),
				startDate: $("form.deposit_history input[name=startDate]").val(),
				endDate: $("form.deposit_history input[name=endDate]").val(),
				onlyLoginData : onlyLoginData,
				loginIntId: loginIntId
		});
		historyStore.baseParams = params;
		historyStore.load({
			params: params
		});
	},

	   
    depositHistoryListSearch: function() {

    	var onlyLoginData = $('#depositHistory :checkbox:checked');
		if(onlyLoginData.length == 0) {
			onlyLoginData = false;
		} else {
			onlyLoginData = true;
		}

        var params = $.extend(true, {}, vendorHistoryParams, {
          vendor : $("#depositHistory select[name=depositVendorList]").val(),
          startDate: $("#depositHistory input[name=startDate]").val(),
          endDate: $("#depositHistory input[name=endDate]").val(),
          onlyLoginData : onlyLoginData,
          subType : $("#depositHistory select[name=subType]").val(),
          loginIntId : loginIntId
        });        
        vendorHistoryStore.baseParams = params;
        vendorHistoryStore.load();
      },
      
  selectedHistorySearch: function(sm, rowIndex, rec) {
  	var params = $.extend(true, {}, historyParams, {
  		vendor: rec.json.id
  	});
  	$("form.deposit_history input[name=vendor]").val(rec.json.id);
  	historyStore.baseParams = params;
  	historyStore.load();
  },

	modifiedDateFormat:function(date) {		
	  var $this = $(this);

	  $.getJSON("${ctx}/common/convertLocalDate.do", 
	    {supplierId: supplierId, dbDate: date},
	    function(data) {            
	      $("#depositHistory ." + $this.attr('name')).val(data.localDate);
	    });
	},

    modifiedDateFormat2:function(date) {     
        var $this = $(this);

        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: date},
          function(data) {            
            $("form.deposit_history ." + $this.attr('name')).val(data.localDate);
          });
    },

  saveChargeAmount: function(rec) {
  	
  	saveAction = function() {
    	if(!rec.data.id || !rec.data.charge || !rec.data.charge <0 )  {
    		return;
    	}
    	$.post("${ctx}/gadget/prepaymentMgmt/chargeDeposit.do",
    		{
    			supplierId: supplierId,
    			vendorId: rec.data.id,
    			amount: rec.data.charge,
    			date: +new Date(),
    			loginId : loginId
    		},
    		function(data) {
    			var hParams = $.extend({}, historyParams, {vendor: rec.data.id});
    			vendorStore.reload();
    			historyStore.baseParams = hParams;
    			historyStore.load({
    				params: hParams
    			});
          if (history = data.depositHistory) {
            var receiptParam = {
              contractId: history.contractId,
              depositHistoryId: history.id,
              prepaymentLogId: history.prepaymentLogId,
              vendorId: history.operator.loginId
            }
            console.log(receiptParam);
            eventHandler.chargeAfterReceipt(receiptParam);
          }
    		}
    	);
  	};    	
  	
  	Ext.Msg.prompt(rec.json.id, '<fmt:message key="aimir.retype.amount"/>', 
      function(btn, text) {
    		var prompt = Number(text);
    		if (btn == 'ok' && rec.data.charge) {
    			if(prompt == rec.data.charge) {
    				saveAction();
    			} else {
    				Ext.Msg.alert("<fmt:message key='aimir.alert'/>", 
    					"<fmt:message key='aimir.msg.check.input.value'/>");
    			}
  		} 
  	});
  },

  chargeAfterReceipt: function(rec) {
    Ext.MessageBox.confirm(
      "<fmt:message key='aimir.button.confirm'/>", 
      "<fmt:message key='aimir.msg.confirm.print.receipt'/>",
      function(result) {
        if (result === "yes") {
         eventHandler.receiptPopUp(rec);
        }
      }
    );
  },

  receiptPopUp: function(rec) {
  	var url;
  	var params;    
    var vendorUrl = "${ctx}/gadget/prepaymentMgmt/depoistChargeReceiptPopup.do";
    var customerUrl = "${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptPopup.do";
    var opt = "width=350px, height=615px, resizable=no, status=no";
    
    if ( rec.json ) {
      rec = rec.json;
    }

    params = {
      supplierId: Number(supplierId) ||0,
      contractId: rec.contractId || 0,
      depositHistoryId: Number(rec.depositHistoryId) || Number(rec.id) ||0,
      prepaymentLogId: Number(rec.prepaymentLogId) ||0,
      vendorId: rec.vendorId || rec.vendor ||""		// vendor의 operator loginId	      
    }		
    

		if (rec.contractNo) {
			url = customerUrl;
  	} else {
  		url = vendorUrl;
  	}

  	if ( receiptPopupWindow ) {
      receiptPopupWindow.close();
    }

    var queryString = CommonUtil.getQueryString(params);
    receiptPopupWindow = window.open(url + queryString, "receiptPopupWindow", opt);
  },

	cancel: function(rec) {
	var params = {
	  depositHistoryId: rec.id,
	  vendor: rec.vendor,
	  supplierId: supplierId
	};

	var refund = function() {
	  $.post("${ctx}/gadget/prepaymentMgmt/depositCancel.do", params, function(json) {
	    if (json.result == 'success') {
	      historyStore.reload();
	      vendorStore.reload();
	      Ext.Msg.alert("<fmt:message key='aimir.message'/>", 
	        "<fmt:message key='aimir.success'/>");
	    } else if (json.result == 'cancelData') {
		   historyStore.reload();
		   vendorStore.reload();
           Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
            "<fmt:message key='aimir.already.cancelData'/>");
        } else {
	      Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
	        "<fmt:message key='aimir.msg.fail.update'/>");
	    }
	  });
	}

	Ext.Msg.confirm("<fmt:message key='aimir.message'/>", 
	  "<fmt:message key='aimir.cancel'/>" + "?", 
	  function(btn) {
	    if (btn == "yes") refund();
	  });
	},

  updateTaxRate: function() {
	  $.post('${ctx}/gadget/system/supplier/updateTaxRate.do',
	  {
	    supplierId: supplierId,
	    taxRate : $('input[name=tax]').val() 
	  },
	  function(data) {
	    if(data.result == "success") {
	        Ext.Msg.alert("<fmt:message key='aimir.success'/>",
	        	"<fmt:message key='aimir.msg.success.update.taxRate'/>");
	    }else {
	        Ext.Msg.alert("<fmt:message key='aimir.error'/>",
	        	"<fmt:message key='aimir.msg.fail.update'/>");
	    }  	
  	});
  },

  updateCommissionRate: function() {
    $.post('${ctx}/gadget/system/supplier/updateCommissionRate.do',
    {
      supplierId: supplierId,
      commissionRate : $('input[name=commission]').val()
    },
    function(data) {
        if(data.result == "success") {
          Ext.Msg.alert("<fmt:message key='aimir.success'/>",
          	"<fmt:message key='aimir.msg.success.update.commissionRate'/>");
        }else {
          Ext.Msg.alert("<fmt:message key='aimir.error'/>",
          	"<fmt:message key='aimir.msg.fail.update'/>");
        }
    });  	
  },

  /*
  //검색조건을 LOCATION 에서 GEOCODE로 변경하면서 주석처리
  //추후 필요없을 경우 삭제처리
  locationSetting : function(_treeDivId, _searchKeyId, _locationId, _prefix) {
      
      $('#' + _treeDivId + 'Outer')
          .bind('mouseleave', function(event) { document.getElementById(_treeDivId + 'Outer').style.display = "none"; });

      var flag = true;

      $('#' + _searchKeyId)
          .bind('click', function(event) {
              var tree_div = $('#' + _treeDivId + 'Outer');
              var searchKey = $('#' + _searchKeyId);
              var tree_div_position = $(this).position();
              tree_div_position.top += searchKey.outerHeight();
              tree_div.css(tree_div_position);

              if(tree_div.width()<searchKey.width()){
                  tree_div.css('min-width',searchKey.width());
              }

              if(flag) {
                  document.getElementById(_searchKeyId).value = '';
                  flag = false;
              }
              autoComplete(_treeDivId, _searchKeyId, _locationId);
          })
          .bind('keyup', function(event) { autoComplete(_treeDivId, _searchKeyId, _locationId);});

      $.get('${ctx}/gadget/prepaymentMgmt/getDisplayLocation.do',
          {'supplierId' : supplierId},
          function(data) {
              var locationData = makeTreeJson(data.locations, _prefix);
              
              $('#' + _treeDivId).tree({
                  data : {
                      type : "json",
                      opts : {
                          static : locationData
                      }
                  },
                  callback : {
                      'onselect' : function(n, t) {
                          var locationName = $('#' + n.id + ' a').html().replace('<INS>&nbsp;</INS>', '').replace('<ins>&nbsp;</ins>', '');
                          document.getElementById(_searchKeyId).value = locationName;

                          var tempId = n.id;
                          var locationId = '';
                          var underBarIndex = tempId.indexOf('_');

                          if(underBarIndex == -1) {
                              locationId = tempId;
                          } else {
                              locationId = tempId.substr(underBarIndex + 1, tempId.length);
                          }

                          document.getElementById(_locationId).value = locationId;
                          document.getElementById(_treeDivId + 'Outer').style.display = "none";
                      },
                      'onsearch' : function(n, t) {
                      }
                  }
              });
          }
      );
  },
*/
  monthlyReportSearch: function() {
      if($('#searchWord').val() != '' && $('#searchWord').val() != null) {
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.post('${ctx}/gadget/prepaymentMgmt/getMonthlyReportExcelByGeocode.do',
              {
                searchYear: $('#yearCombo').val(),
                searchMonth: $('#monthlyCombo').val(),
                geocode: $('#searchWord').val(),
                supplierId: supplierId
              },
              function(data) {
                  if(data.nonData == true) {
                     var tbl = $("#filelist");
                     tbl.html("<fmt:message key='aimir.data.notexist'/>");
                      
                  } else {
                      addMonthlyReportList(data.fileNames);   
                  }
                  Ext.Msg.hide();
            });
      } else {
          Ext.Msg.alert("","<fmt:message key='aimir.enterContractNo'/>",null,null);
      }
  },
    
  downloadExcel: function() {
    var url = "${ctx}/common/fileDownload.do";
    var form = $("#reportTab form");

    $this = $(this);
    var fileName = $this.siblings("input").val();
    form.attr("method", "post");
    form.find("input[name=filePath]").val(file_path);
    form.find("input[name=fileName]").val(fileName);
    form.attr("action", url);
    form.attr("target", "downFrame");
    form.submit();
  },
  
  tagExcelButton: function() {
      var val = $("#depositHistory select[name=subType]").val();
      if (val == "all") {
          $("#depositHistoryExcel").hide();
          $("#depositHistoryTotalExcel").hide();
      } else {
          $("#depositHistoryExcel").show();
          $("#depositHistoryTotalExcel").show();
      }
  },
  
  initHistoryTab: function() {
      if(!vendorHistoryGridOn) {
          vendorHistoryParams = $.extend(vendorHistoryParams,
          {startDate:$("#depositHistory input[name=startDate]").val(),
           endDate:$("#depositHistory input[name=endDate]").val(),
           vendor: $("form.deposit_history input[name=vendor]").val(),
           supplierId: supplierId});
          
          vendorHistoryGrid = new Ext.grid.EditorGridPanel(vendorHistoryProp);
          vendorHistoryStore.load({param: vendorHistoryParams});
          
          vendorHistoryGridOn=true; 
      } else {
          eventHandler.depositHistoryListSearch();
      }
  },
    
  depositHistoryExcel: function() {
      onlyTotal = false;
      vendor = $("#depositHistory select[name=depositVendorList]").val();
      var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
      window.open('${ctx}/gadget/prepaymentMgmt/depositChargeExcelDownloadPopup.do', "popupWindow", opt);
      
      },

  depositHistoryTotalExcel: function() {
      onlyTotal = true;
      vendor = $("#depositHistory select[name=depositVendorList]").val();
      var opt = "width=600px, height=400px, left=100px, top=100px resizable=no, status=no";
      window.open('${ctx}/gadget/prepaymentMgmt/depositChargeExcelDownloadPopup.do', "totalPopupWindow", opt);
      }
};

var getUserInfo = function() {
	$.getJSON("${ctx}/common/getUserInfo.do", 
		function(data) {
			supplierId = data.supplierId;
		    initSelectBox();
			$("#menu").tabs();
			initCalendar(data);
			
			initDepositTab();
			initReportTab();
			
			renderGrid();
			initTaxCommissionRate();	
			eventHandler.tagExcelButton(); 
			bind();
			hide();			
	});	
};

var initSelectBox = function() {
    $("#report-type").selectbox();
    $("#depositVendorList").selectbox();
    $("#sub-type").selectbox();
}

var initTaxCommissionRate = function() {
	var taxRate = ${taxRate};
	var commissionRate = ${commissionRate};
	$("input[name=tax]").val(taxRate);
	$("input[name=commission]").val(commissionRate);
	
	if (!isAdmin) {
		$("form.tax").hide();
	}
};

var initCalendar = function(data) {
  calendarProp.dateFormat = 'yymmdd';
  calendarProp.altFormat = '';
  var startDate = new Date();
  startDate.setMonth(startDate.getMonth() - 1);
  var endDate = new Date();
  var startProp = $.extend(true, {defaultDate: startDate}, calendarProp);
  var endProp = $.extend(true, {defaultDate: endDate}, calendarProp);
  var st = $('input[name=startDate]').datepicker(startProp);
  var end = $('input[name=endDate]').datepicker(endProp);     

  var initDateFormat = function(inst ,date) {
    var dbDate = $.datepicker.formatDate('yymmdd', date);
    inst.val(dbDate);
    $.getJSON("${ctx}/common/convertLocalDate.do", 
      {supplierId: supplierId, dbDate: dbDate},
      function(data) {            
        $("." + inst.attr('name')).val(data.localDate);
      });
  };

  initDateFormat($('input[name=startDate]'), startDate);
  initDateFormat($('input[name=endDate]'), endDate);
};

var initDepositTab = function() {
    vendorListParams = $.extend(vendorListParams, {supplierId: supplierId});
    
    historyParams = $.extend(historyParams, 
            {startDate:$("form.deposit_history input[name=startDate]").val(),
             endDate:$("form.deposit_history input[name=endDate]").val(),
             supplierId: supplierId});
}

var initReportTab = function() {
    var div = $("#searchReportHtml");
    var htmltxt = "<span class='inline-block'><label><fmt:message key='aimir.searchDate'/></label></span>";
        htmltxt+="<span class='inline-block'><select id='yearCombo' name='yearCombo' class='width49'/></span>";
        htmltxt+="<span class='inline-block'><select id='monthlyCombo' name='monthlyCombo' class='width25'/></span>";
        htmltxt+="<span id='monthlyReportSearch' class='am_button marL10'><a><fmt:message key='aimir.button.search'/></a></span>";
    div.html(htmltxt);

    //eventHandler.locationSetting('treeDivA', 'searchWord', 'districtLocationId');
    
    initMonthlyCalendar();
}

var initMonthlyCalendar = function() {
    $.getJSON("${ctx}/common/getYear.do"
            ,{supplierId:supplierId}
            ,function(json) {
                 var startYear = json.year;
                 var endYear = json.currYear;
                 var currDate = json.currDate;

                 $('#yearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                 $('#yearCombo').selectbox();
                 
                 $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#yearCombo').val()}
                ,function(json) {
                    monthVal=$('#monthlyCombo').val();
                    var idx = Number(json.monthCount)-2;
                    if(json.monthCount == 1)
                    	idx = 0;
                   	$('#monthlyCombo').numericOptions({from:1,to:12,selectedIndex:idx});
                    
    
                    if(monthVal!=null&&monthVal!=""){
                        $('#monthlyCombo').val(monthVal);
                    }
                    $('#monthlyCombo').selectbox();

                });
            });
  };

var renderGrid = function() {
    vendorListGrid = new Ext.grid.EditorGridPanel(vendorProp);  
    historyListGrid = new Ext.grid.EditorGridPanel(historyProp);
    vendorStore.load({params: vendorListParams});
    historyStore.load({params: historyParams}); 
};

var addMonthlyReportList = function(fileNames) {
    var tbl = $("#filelist");

    var htmltxt = 
      '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';
    var fileList = fileNames.split(",");
    
    for(var i = 0 ; i < fileList.length ; i++) {
      htmltxt += "<tr><th>"+(i+1)+"</th><td>"+fileList[i]+
       "</td><td class='button'>" +
       "<input type='hidden' name='fileName" + i + "' id='fileName" + i +
        "' value='" + fileList[i] + "'/>" +
       "<button type='button' class='input_button download'  >" +
       '<fmt:message key="aimir.report.fileDownload"/>' + 
        "</button></td></tr>";
    }
    tbl.html(htmltxt);    
    
    $("button.download").click(eventHandler.downloadExcel);
};

var init = function() {
	getUserInfo();
};

var bind = function() {
	$('#depositHistory input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
	$('#depositHistory input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});	
    $('form.deposit_history input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat2});
    $('form.deposit_history input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat2});   
	$("form.vendor_list span.am_button").click(eventHandler.vendorSearch);	
	$("form.deposit_history span.am_button").click(eventHandler.historySearch);
	vendorSm.addListener('rowselect', eventHandler.selectedHistorySearch);
	$("form.tax em.tax-update").click(eventHandler.updateTaxRate);
	$("form.tax em.commission-update").click(eventHandler.updateCommissionRate);
    $('a[href=#historyTab]').bind('click', eventHandler.initHistoryTab);
    $('a[href=#depositTab]').bind('click', eventHandler.historySearch);
    $("#depositHistory select[name=subType]").bind('change', eventHandler.tagExcelButton);
	$('#depositHistory span#depositHistorySearch').click(eventHandler.depositHistoryListSearch);
	$('#depositHistory span#depositHistoryExcel').click(eventHandler.depositHistoryExcel);
    $('#depositHistory span#depositHistoryTotalExcel').click(eventHandler.depositHistoryTotalExcel);
    $('form.reportSearch span#monthlyReportSearch').click(eventHandler.monthlyReportSearch);

};

window.onload = function() {
	init();
}; 

// window resize event
$(window).resize(function() {
	if(!(vendorListGrid === undefined)){
		vendorListGrid.getView().refresh();
	}

	if(!(historyListGrid === undefined)){
		historyListGrid.getView().refresh();
	}

	if(!(vendorHistoryGrid === undefined)){
		vendorHistoryGrid.getView().refresh();
	}
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
</body>
</html>