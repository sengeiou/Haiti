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
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
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
		height: 15px;
	}
	form input.alt {
		width: 60px;
	}	
	form.vendor_list input {
		width: 40px;
	}
	.hidden {
		display: none;
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
<script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
<%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script> --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
var supplierId;
var PAGE_SIZE = 3;

//수정권한
var editAuth = "${editAuth}";

var storeParams = {
	page: 1, 
	start: 0,
	limit: PAGE_SIZE	
};

var vendorListParams = $.extend(true, {}, storeParams);
var historyParams = $.extend(true, {}, storeParams);

// 화면 상단 그리드 
var vendorListGrid;
// 화면 하단 그리드
var historyListGrid;

var vendorStore = new Ext.data.JsonStore({
	baseParams: vendorListParams,
	url: "${ctx}/gadget/prepaymentMgmt/operatorList.do",
	totalProperty: 'count',
	root: 'list',
	fields: ['id', 'deposit'],
	listeners: {
		beforeload: function(store, options) {
			var params = options.params;
			if (params.start && params.start > 0) {
				params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
			} else { params.page = 1;}
		},	
	}
});

var historyStore = new Ext.data.JsonStore({
	baseParams: historyParams,
	url: "${ctx}/gadget/prepaymentMgmt/historyList.do",	
	totalProperty: 'count',
	root: 'list',
	fields: ['vendor', 'contractNo', 'customerId', 
		'chargeCredit', 'chargeDeposit', 'deposit'],
	listeners: {
		beforeload: function(store, options) {			
			var params = options.params;
			if (params.start && params.start > 0) {
				params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
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
	}
	button.defer(100);
	return $div[0].outerHTML;
};

var hiddenCol = (editAuth != "true") ? true : false;    // 권한에 따라 show/hidden

var vendorModel = new Ext.grid.ColumnModel({
	columns: [
		{header: "<fmt:message key='aimir.id'/>", dataIndex: 'id'},
		{header: "<fmt:message key='aimir.deposit'/>", 
			align: 'right',
			dataIndex: 'deposit'},
		{header: "<fmt:message key='aimir.amount.paid'/>", dataIndex: 'charge', 
			align: 'right',
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
		{header: "<fmt:message key='aimir.deposit.chargecredit'/>", align: 'right'},
		{header: "<fmt:message key='aimir.netvalue'/>", align: 'right'},
		{header: "<fmt:message key='aimir.deposit'/>", align: 'right'}
	],
	defaults : {
		sortable: true,
		menuDisable: true
	}
});

var sm = {
	singleSelect: true,
	moveEditorOnEnter: false
};

var vendorSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
var historySm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

var _bbar = {	
	pageSize: PAGE_SIZE,
	displayInfo: true,
	displayMsg: ' {0} - {1} / {2}'
};

var vendorBbar = new Ext.PagingToolbar($.extend(true, {}, {store: vendorStore}, _bbar));
var historyBbar = new Ext.PagingToolbar($.extend(true, {}, {store: historyStore}, _bbar));

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

var calendarProp = {
	showOn: 'button',
	buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
	buttonImageOnly: true
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
	height: 125,
	colModel: historyModel,
	sm: historySm,
	bbar: historyBbar,
	store: historyStore,
	renderTo: 'depositHisotry'
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
		var params = $.extend({}, historyParams, {
			reportType: $("form.deposit_history select[name=reportType]").val(),
			vendor: $("form.deposit_history input[name=vendor]").val(),
			contract: $("form.deposit_history input[name=contract]").val(),
			customerName: $("form.deposit_history input[name=customerName]").val(),
			customerId: $("form.deposit_history input[name=customerId]").val(),
			meterId: $("form.deposit_history input[name=meterId]").val(),
			startDate: $("form.deposit_history input[name=startDate]").val(),
			endDate: $("form.deposit_history input[name=endDate]").val()
		});
		console.log(params);
		historyStore.baseParams = params;
		historyStore.load({
			params: params
		});
	},

	selectedHistorySearch: function(sm, rowIndex, rec) {
  		var params = $.extend(true, {}, historyParams, {
  			vendor: rec.json.id
  		});
  		$("form.deposit_history input[name=vendor]").val(rec.json.id);
  		historyStore.baseParams = params;
  		historyStore.load({
  			params: params
  		});
	},

	modifiedDateFormat:function(date) {		
	  var $this = $(this);

	  $.getJSON("${ctx}/common/convertLocalDate.do", 
	    {supplierId: supplierId, dbDate: date},
	    function(data) {            
	      $("." + $this.attr('name')).val(data.localDate);
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
    			date: +new Date()
    		},
    		function(data) {
    			var hParams = $.extend({}, historyParams, {vendor: rec.data.id});
    			vendorStore.reload();
    			historyStore.baseParams = hParams;
    			historyStore.load({
    				params: hParams
    			});
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
  }
};

var getUserInfo = function() {
	$.getJSON("${ctx}/common/getUserInfo.do", 
		function(data) {
			supplierId = data.supplierId;
			$("#report-type").selectbox();
			initCalendar(data);
			vendorListParams = $.extend(vendorListParams, {supplierId: supplierId});
			historyParams = $.extend(historyParams, 
		            {startDate:$("form.deposit_history input[name=startDate]").val(),
		             endDate:$("form.deposit_history input[name=endDate]").val(),
		             supplierId: supplierId});
			renderGrid();
			bind();
			hide();
	});	
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

var renderGrid = function() {
	vendorListGrid = new Ext.grid.EditorGridPanel(vendorProp);	
	historyListGrid = new Ext.grid.EditorGridPanel(historyProp);

	vendorStore.load({params: vendorListParams});
	historyParams = $.extend(historyParams, 
            {vendor: $("form.deposit_history input[name=vendor]").val()});
	historyStore.load({params: historyParams});
};

var init = function() {
	getUserInfo();	
};

var bind = function() {
  	$('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
    $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});	
	$("form.vendor_list span.am_button").click(eventHandler.vendorSearch);	
	$("form.deposit_history span.am_button").click(eventHandler.historySearch);
	vendorSm.addListener('rowselect', eventHandler.selectedHistorySearch);
};

window.onload = function() {
	init();	
}; 

// window resize event
$(window).resize(function() {
	vendorListGrid.getView().refresh();
	historyListGrid.getView().refresh();
});
/*]]>*/
</script>
</head>
<body>
	<div id="contentWrapper">
		<form class="vendor_list">
			<label><fmt:message key="aimir.vendor"/> <fmt:message key="aimir.id"/></label>
			<input name="id" type="text">
			<label><fmt:message key="aimir.vendor"/> <fmt:message key="aimir.name"/></label>
			<input name="name" type="text">
			<span class="am_button margin-l10 margin-t1px">
				<a><fmt:message key="aimir.button.search"/></a>
			</span>
		</form>
		<div id='vendorList' class="grid-wrapper vendor"></div>	
		
		<form class="deposit_history">
			<div>
				<div>
					<input name="vendor" type="text" class="hidden"/>
		            <span class='inline-block'> 
		            	<select id='report-type' name='reportType'>
		                	<option value='all'><fmt:message key='aimir.all'/></option>
		                	<option value='deposit'><fmt:message key='aimir.deposit'/></option>
		                	<option value='sales'><fmt:message key='aimir.sales'/></option>
		            	</select>
		            </span>
				</div>
				<label><fmt:message key="aimir.searchDate"/></label>
				<input class="alt startDate" type='text' readOnly/>
				<input name="startDate" class="no-width" type="text"/>		
				<label>~</label>
				<input class="alt endDate" type='text' readOnly/>
				<input name="endDate" class="no-width" type="text"/>		
				<span class="am_button margin-l10 margin-t1px">
					<a><fmt:message key="aimir.button.search"/></a>
				</span>		
			</div>
		</form>
		<div id='depositHisotry' class="grid-wrapper history"/>
	</div>
</body>
</html>