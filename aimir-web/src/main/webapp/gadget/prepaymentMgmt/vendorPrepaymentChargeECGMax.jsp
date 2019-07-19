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
      width: 70px;
    } 
    form span{
      margin-right: 20px;
    }

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }    
    /* ext-js grid 행 높이 고정 
     cancel이 버튼인 row와 텍스트인 경우 row의 높이가 다르므로 임의로 수정 
    */    
    td.x-grid3-col.x-grid3-cell {
      height: 30px;
    }
    #loginWrapper,
    #firstTab, 
    #passwordTab{
      padding-top: 100px;
      padding-left: 30px;
    }
    #managerPwd-confirm{
      padding-top: 100px;
      padding-left: 30px;
    }

    .hidden {
      display: none;
    }
    .no-width {
      width: 0px;
      visibility: hidden;
    }
    .vertical-top {
      vertical-align: top;
    }
    span.bold-font {
      font-weight: bold;
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
</head>
<body>
  <%-- <applet name="MacAddress" code="NetIfApplet.class"
    archive="${ctx}/lib/MacAddress.jar" width="0" height="0"></applet> --%>
  <div id="loginWrapper"> 
    <span>
      <fmt:message key="aimir.id"/>   
    </span>
    <input name="loginId" type="text"/>
    <span>
      <fmt:message key="aimir.password"/>
    </span>
    <input name="password" type="password"/>

    <span id="logIn" class="am_button margin-l10 margin-t1px">
      <a class="on"><fmt:message key="aimir.login.login" /></a>
    </span>    
  </div>

  <div id="menu">
    <ul>
      <li class="chargeTab">
        <a href="#chargeTab">
          <fmt:message key="aimir.charging"/>
        </a>
      </li>
      <li class="historyTab">
        <a href="#historyTab">
          <fmt:message key='aimir.hems.prepayment.chargehistory'/>
        </a>
      </li>
      <li class="infoTab">
        <a href="#infoTab">
          <fmt:message key="aimir.prepayment.balancehistory"/>
        </a>
      </li>
      <li class="managerTab">
        <a href="#managerTab">
          <fmt:message key='aimir.prepayment.casher'/> <fmt:message key="aimir.manager"/>
        </a>
      </li>
      <li class="managerPwdTab">
        <a href="#managerPwdTab">
          <fmt:message key='aimir.prepayment.casher'/> <fmt:message key="aimir.manager"/> <fmt:message key="aimir.password"/>
        </a>
      </li>
      <li class="firstTab">
        <a href="#firstTab">
        </a>
      </li>
      <li class="passwordTab">
        <a href="#passwordTab">
          <fmt:message key='aimir.change.password'/>
        </a>
      </li>   
      <li id="arrearsInfo" style="float: right">
      	<span>
            <input class="alt startDate vertical-top" name="startDateDisplay"  type='text' style="width: 70px;" readOnly/>
            <input name="startDate" class="no-width" type="text"/>    
            <label class="vertical-top">~</label>
            <input class="alt endDate vertical-top" name="endDateDisplay"  type='text' style="width: 70px;" readOnly/>
            <input name="endDate" class="no-width" type="text"/>    
        </span>
      	<span id='arrearsExcel' class="am_button margin-t1px vertical-top" style="margin-right: 10px;">
            <a><fmt:message key="aimir.arrearsinfo.export"/></a>
          </span>          
      </li>
    </ul>
    <div id="payInputCmp" style="width: 430px"></div>
	<div id="CashierChangePwdWin"></div>
    <div id="chargeTab" >

      <!--검색조건-->
      <form id="contractForm" class="searchoption-container">
        <div>
          <span>
            <fmt:message key='aimir.barcode'/>
            <input id="barcodeNumber" type="text" style="width:110px;">
          </span>
        </div>
        <div class="clear-form">
          <span>
            <fmt:message key="aimir.contractNumber"/>
            <input id="contractNumber" type="text" style="width:110px;">
          </span>
          <span>
            <fmt:message key="aimir.customername"/>
            <input id="customerName" type="text" style="width:120px;">
          </span>
          <span>
            <fmt:message key="aimir.customerid"/>
            <input id="customerNo" type="text" style="width:120px;">
          </span>
          <span>
            <fmt:message key="aimir.meterid"/>
            <input id="mdsId" type="text" style="width:120px;">
          </span>
          <span id="contractSearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
          <span id='contractListTotalExcel' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.excel"/></a>
          </span>
          <span class="bold-font hidden current-deposit">
            <fmt:message key="aimir.deposit"/>
            <label class="current_deposit"></label>
          </span>
        </div>
      </form>
      <!--검색조건 끝-->

      <div id="prepaymentChargeDiv"></div>

      <!--검색조건-->
      <form id="historyForm" class="searchoption-container">
        <div class="wrapper">
          <label class="check"><fmt:message key='aimir.hems.prepayment.chargehistory'/></label>
        </div>
        <div class="wrapper">
          <input name="contractNumber" class="hidden" type="text"></input>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate" name="startDateDisplay"  type='text' readOnly/>
            <input name="startDate" class="no-width" type="text"/>    
            <label>~</label>
            <input class="alt endDate" name="endDateDisplay"  type='text' readOnly/>
            <input name="endDate" class="no-width" type="text"/>    
          </span>
          <span id="historySearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>    
        </div>
      </form>
      <!--검색조건 끝-->
	  <div>
	  	<table>
	  		<tr>
	  			<td id="prepaymentChargeHistoryDiv" style="width: 65%">
	  			</td>
	  			<td style="width: 5%">
	  			</td>
	  			<td id="debtHistoryDiv" style="width: 30%">
	  			</td>
	  		</tr>
	  	</table>
	  </div>
      
    </div>   

    <div id="historyTab">
      <!--검색조건-->
      <form id="depositHistory" class="searchoption-container">
        <div>
          <!-- <input name="vendor" type="text" class="hidden"/> -->
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
        </div>
        <div>
          <span class="inline-block" style='margin-right: 1px;'>
            <label><fmt:message key="aimir.vendor"/></label>
          </span>
          <span class="inline-block">
            <select id='vendor' style="width: 120px; display: inline;">
                <c:choose>
                    <c:when test="${role == 'admin'}">
                        <option value=""><fmt:message key="aimir.all" /></option>
                        <c:forEach var="depositVendorList" items="${depositVendorList}">
                            <c:choose>
                                <c:when test="${not empty depositVendorList}">
                                    <option value="${depositVendorList.loginId}">${depositVendorList.loginId}</option>
                                </c:when>
                            </c:choose>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <option value="${vendor}">${vendor}</option>
                    </c:otherwise>
                </c:choose>
            </select>
          </span>
          <span class="inline-block">
            <label><fmt:message key="aimir.board.location"/></label>
            <input name="searchWord" id='searchWord' type="text" style="width: 120px" /> 
            <input type='hidden' id='locationId'></input>
          </span>
          <span>
            <label><fmt:message key="aimir.prepayment.casher"/></label>
            <input name="casherId" type="text"/></input>
          </span>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate" name="startDateDisplay" type='text' readOnly/><input name="startDate" type="hidden"/>
            <label>~</label>
            <input class="alt endDate" name="endDateDisplay" type='text' readOnly/><input name="endDate" type="hidden"/>
          </span>
          <span id='depositHistorySearch' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.search"/></a>
          </span>   
          <span id='depositHistoryExcel' class="am_button margin-l10 margin-t1px margin-r5">
            <a><fmt:message key="aimir.button.excel"/></a>
          </span>     
          <span id='depositHistoryTotalExcel' class="am_button margin-t1px">
            <a><fmt:message key="aimir.total"/> <fmt:message key="aimir.button.excel"/></a>
          </span>   
        </div>        
      </form>
      <!--검색조건 끝-->

      <div id="depositChargeHistoryDiv"></div>
      <div id="treeDiv2Outer" class="tree-billing auto" style="display:none;">
          <div id="treeDiv2"></div>
      </div>
    </div>
    
    <div id="infoTab" >

      <!-- 검색조건 -->
      <form id="infoForm" class="searchoption-container">
        <div class="clear-form">
          <span>
            <fmt:message key="aimir.contractNumber"/>
            <input id="contractNumberInfo" type="text" style="width:110px;">
          </span>
          <span>
            <fmt:message key="aimir.customername"/>
            <input id="customerNameInfo" type="text" style="width:120px;">
          </span>
          <span>
            <fmt:message key="aimir.customerid"/>
            <input id="customerNoInfo" type="text" style="width:120px;">
          </span>
          <span>
            <fmt:message key="aimir.meterid"/>
            <input id="mdsIdInfo" type="text" style="width:120px;">
          </span>
          <span id="contractInfoSearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
        </div>
      </form>
      
      <!-- 검색조건 끝 -->

      <div id="contractInfoDiv"></div>

      <!-- 검색조건 -->
      <form id="infoDetailForm" class="searchoption-container">
        <div class="wrapper">
          <label class="check"><fmt:message key='aimir.prepayment.balancehistory'/></label>
        </div>
        <div class="wrapper">
          <input name="contractNumberInfo" class="hidden" type="text"></input>
          <span>
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate" name="startDateDisplay"  type='text' readOnly/>
            <input name="startDate" class="no-width" type="text"/>    
            <label>~</label>
            <input class="alt endDate" name="endDateDisplay"  type='text' readOnly/>
            <input name="endDate" class="no-width" type="text"/>    
          </span>
          <span id="infoDetailSearch" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
          <span id='infoDetailTotalExcel' class="am_button margin-l10 margin-t1px">
            <a><fmt:message key="aimir.button.excel"/></a>
          </span>
        </div>
      </form>
      <!-- 검색조건 끝 -->
  	<div id="balanceHistoryDiv"></div>
      
    </div> 

    <div id="managerTab">
      <form>
        <span>
          <label>
            <fmt:message key='aimir.prepayment.casher'/>
            <fmt:message key='aimir.id'/>
          </label>
          <input name='casherId' type="text"></input>
          <label>
            <fmt:message key='aimir.name'/>
          </label>
          <input name='name' type='text'/></input>
        </span>
        
        <input name="isManager" class='hidden' type="checkbox"/>
        <span class='hidden'>
          <fmt:message key="aimir.manager"/>
        </span>
        <span id="addCasher" class="am_button margin-l10 margin-t1px">
          <a class="on"><fmt:message key="aimir.add" /></a>
        </span>
      </form>

      <div id="casherManagerDiv"></div>
    </div>
    
    <div id="managerPwdTab">
	    <div id="managerPwd-confirm">
			<label>
				<fmt:message key="aimir.manager"/> <fmt:message key="aimir.password"/>
			</label>
			<input name="managerPwd" type='password'></input>
			
			<span id="managerPwdConfirm" class="am_button margin-l10 margin-t1px">
			  <a class="on"><fmt:message key="aimir.button.confirm" /></a>
			</span> 
	    </div>
   	   <div id="managerPwd-change">
	      <form>
	        <span>
	          <label>
	            <fmt:message key='aimir.prepayment.casher'/>
	            <fmt:message key='aimir.id'/>
	          </label>
	          <input name='searchCashierId' type="text"></input>
	          <label>
	            <fmt:message key='aimir.vendor'/>
	          </label>
	          <input name='searchVendorId' type='text'/></input>
	        </span>
	        
	        <span id="searchManager" class="am_button margin-l10 margin-t1px">
	          <a class="on"><fmt:message key="aimir.button.search" /></a>
	        </span>
	      </form>
          <div id="casherManagerPwdDiv"></div>
      	</div>
    </div>

    <div id="firstTab">
      <form>
        <span>
          <label>
            <fmt:message key='aimir.id' />
          </label>
          <input name="id" type="text"></input>
          <label>
            <fmt:message key='aimir.name'/>
          </label>
          <input name='name' type='text'/>
          <span id="addManager" class="am_button margin-l10 margin-t1px">
            <a class="on"><fmt:message key="aimir.add" /></a>
          </span>          
        </span>
      </form>
    </div>

    <div id="passwordTab">
      <div id="confirm-pwd">
        <label>
          <fmt:message key='aimir.operator.prevpwd'/>
        </label>
        <input name="prevPwd" type='password'></input>
      
        <span id="confirmPwd" class="am_button margin-l10 margin-t1px">
          <a class="on"><fmt:message key="aimir.button.confirm" /></a>
        </span> 
      </div>

      <div id="change-pwd" class="hidden">
        <label>
          <fmt:message key='aimir.newpassword'/>
        </label>
        <input name="newPwd" type='password'></input>
        <label>
          <fmt:message key='aimir.confirmpassword'/>
        </label>
        <input name='rePwd' type='password'></input>

        <span id="changePwd" class="am_button margin-l10 margin-t1px">
          <a class="on"><fmt:message key="aimir.button.confirm" /></a>
        </span> 
      </div>
    </div>

  </div>
  <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
    // vending system 처음 여부
    var isFirst = ${isFirst};

    //공급사ID
    var supplierId="${supplierId}";
    var supplierName="${supplierName}";

    // 3rd party vendor인지 여부
    var isVendor = ${isVendor};

    //vendor(Operator) loginId
    var vendor ="${vendor}";
    
    //vendor 예치금
    var deposit = "${deposit}";

    //casher 처음 login 여부(true인 경우 pwd없이 로그인이 가능하다)
    var isFirstLogIn;

    //client MacAddress
    var clientMacAddress;
    
    //MacAddress 리스트
    var macAddressList;
    var debtInfo;
    
    var payInputWin;

    //기존에 열려있는 popup window
    var receiptPopupWindow;

    //Manager인지 여부 (Casher 고용권한)
    var isManager;

    // vendor의 RoleName
    var vendorRole = "${role}"; 

    var isGetAllManager;
    
    var passChangeWin;
    
    // Contract List 출격인지 = 1, 보고서 출력 양식으로 전체 출력인지 = 2, total만 출력하는지 = 3, 결정하는 flag 
    var excelType;
    var contactListObj;  // Contract List 엑셀출력시 필요 정보 담는 Object
    var logoImg = "${logoImg}"; // 로고 이미지 파일
    
    //분할납부 기능시 사용하는 필드
    var isPartpayment = "${isPartpayment}"; // 분할납부기능 사용여부
    var firstArrears; //처음 입력받았었던 미수금
    var arrearsContractCount; //미수금을 지불하기로 계약했던 Count
    var arrearsPaymentCount; //미수금을 지불한 Count
    var initArrears = "${initArrears}";

    var casherStatus = {
      "0" : "work", // 현재까지 시스템에서 사용 중인 캐셔 
      "1" : "quit"  // 현재 시스템에서 삭제된 캐셔
    };

    // 수정 권한
    var editAuth = "${editAuth}";
    var hiddenCol = (editAuth != "true") ? true : false;    // 권한에 따라 show/hide
    var editCol = (editAuth != "true") ? false : true;      // 권한에 따라 edit 
                                                            // enable/disable

    //var isHiddenCancelBtn = (vendorRole == 'admin') ? false : true; 
    var isHiddenCancelBtn = (vendorRole == 'admin' || vendorRole == 'edh_vendor' || vendorRole == 'ECG vendor') ? false : true; 
    var isHiddenPasswordBtn

    var contractGrid;
    var historyGrid;
    var contractListGrid;
    var balanceHistoryGrid;
    var vendorHistoryGrid;
    var casherManagerGrid;

    var PAGE_SIZE = 6;
    var VENDOR_HISTORY_PAGE_SIZE = 15;

    var storeParams = {
      page: 1,
      start: 0,
      limit: PAGE_SIZE
    };

    var calendarProp = {
      showOn: 'button',
      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
      buttonImageOnly: true,
      dateFormat: 'yymmdd',
      altFormat: ''
    };

    var contractListParams = $.extend(true, {supplierId: supplierId}, storeParams);
    var historyListParams = $.extend(true, {
      contractNumber: null,
      // 0000년 00월 00일 ~ 9999년 99월 99일까지, 즉 전체 기간
      searchStartMonth: '00000000',
      searchEndMonth: '99999999'
    }, storeParams);
    
    var contractInfoParams = $.extend(true, {supplierId: supplierId}, storeParams);

    var balanceHistoryListParams = $.extend(true, {
      contractNumber: null,
      searchStartMonth: '00000000',
      searchEndMonth: '99999999',
      supplierId: supplierId
    }, storeParams);

    
    var debtHistoryListParams = $.extend(true, {
    	prepaymentLogId : null
    }, storeParams)

    var vendorHistoryParams = $.extend(true, {vendor: $("#vendor").val(),
      //vendorRole: vendorRole,
      supplierId: supplierId,
      startDate:$("#depositHistory input[name=startDate]").val(),
      endDate:$("#depositHistory input[name=endDate]").val()}, storeParams);
    vendorHistoryParams.limit = VENDOR_HISTORY_PAGE_SIZE;

    var casherManagerParams = $.extend(true, {vendorId: vendor, 
      supplierId: supplierId, allManager: isGetAllManager}, storeParams);    
    casherManagerParams.limit = VENDOR_HISTORY_PAGE_SIZE;
    
    var casherManagerPwdParams = $.extend(true, {vendorId: vendor, 
        supplierId: supplierId, allManager: isGetAllManager}, storeParams);    
    casherManagerPwdParams.limit = VENDOR_HISTORY_PAGE_SIZE;

    var contractListStore = new Ext.data.JsonStore({
        baseParams: contractListParams,
        url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentChargeListWithDebt.do",
        totalProperty: 'totalCount',
        root: 'result',
        fields: ['CONTRACTNUMBER', 'CUSTOMERNO', 'CUSTOMERNAME', 'MDSID', 
          'ADDRESS', 'LASTTOKENDATE', 'CURRENTCREDIT', 'CURRENTARREARS', //'CONTRACTPRICE', 
          'BARCODE', 'CHARGEAVAILABLE', 'STATUSNAME','DEBTAMOUNT','CUSTOMERNUMBER'],
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
      fields: ["prepaymentLogId","lastTokenDate", "chargedCredit", "chargedArrears", "balance", "arrears",
        "lastTokenId", "authCode", "municipalityCode", "payType", "firstArrears", "arrearsContractCount", "arrearsPaymentCount","partpayInfo"],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
          } else { params.page = 1;}
        },load: function(store, options) {
        	if(store.data.length > 0) {
	        	var data = store.data.items[0].data;
	        	firstArrears = data.firstArrears;
	        	arrearsContractCount = data.arrearsContractCount;
	        	arrearsPaymentCount = data.arrearsPaymentCount;
	        	eventHandler.autoSearchDebtLog(data);
        	} else {
        		var contractNumber = historyListStore.baseParams.contractNumber;
        		if(contractNumber != null) {
        			$.ajax({
                        type : "POST",
                        async : false,
                        data : {
                        	contractNumber : contractNumber,
                        	supplierId	   : supplierId
                        },
                        dataType : "json",
                        url:'${ctx}/gadget/system/getContractByContractNumber.do',
                        success: function(json,status) {
                        	firstArrears = json.contract[0].firstArrears;
               	        	arrearsContractCount = json.contract[0].arrearsContractCount;
               	        	arrearsPaymentCount = json.contract[0].arrearsPaymentCount;
                        }
                    })
        		} else {
       	        	firstArrears = null;
         	        arrearsContractCount = null;
          	        arrearsPaymentCount = null;
        		}
        		var data = {prepaymentLogId:0};
        		eventHandler.autoSearchDebtLog(data);
        	}
        }
      }
    });

    var balanceHistoryListStore = new Ext.data.JsonStore({
      baseParams: balanceHistoryListParams,
      url: "${ctx}/gadget/prepaymentMgmt/getBalanceHistoryList.do",
      totalProperty: 'totalCount',
      root: 'result',
      fields: ["lpTime","writeDate","accUsage","usage", "accBill","bill", "activeImport", "activeExport","lpTime",'balance'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
          } else { params.page = 1;}
        }
      }
    });
    
    var contractInfoStore = new Ext.data.JsonStore({
        baseParams: contractInfoParams,
        url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentChargeList.do",
        totalProperty: 'totalCount',
        root: 'result',
        fields: ['contractNumber', 'customerNo', 'customerName', 'mdsId', 
                 'address', 'lastTokenDate', 'currentCredit', 'currentArrears', //'contractPrice', 
                 'barcode', 'chargeAvailable', 'statusName'],
        listeners: {
          beforeload: function(store, options) {
            var params = options.params;
            if (params.start && params.start > 0) {
              params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
            } else { params.page = 1;}
          }
        }
      });

    
    var debtHistoryListStore = new Ext.data.JsonStore({
        baseParams: debtHistoryListParams,
        url: "${ctx}/gadget/prepaymentMgmt/getDebtArrearsLog.do",
        totalProperty: 'totalCount',
        root: 'debtLogList',
        fields: ["AMOUNT",'TYPE','CHARGED','PARTPAYINFO']
      });

    var vendorHistoryStore = new Ext.data.JsonStore({
      baseParams: vendorHistoryParams,
      url: "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryList.do",
      totalProperty: 'count',
      root: 'list',
      fields: ['vendor', 'casher','contractNo', 'customerId', 'meter', 
        'customerName', 'address', 'changeDate', 'chargeCredit', 'payType', 'chargeDeposit', 'deposit'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + VENDOR_HISTORY_PAGE_SIZE) / VENDOR_HISTORY_PAGE_SIZE);
          } else { params.page = 1;}          
        }
      }
    });

    var casherManagerStore = new Ext.data.JsonStore({
      baseParams: casherManagerParams,
      url: "${ctx}/gadget/prepaymentMgmt/casherManagerList.do",
      totalProperty: 'count',
      root: 'list',
      fields: ['casherId', 'name', 'status', 'lastUpdateDate','isManager'],
      listeners: {
        beforeload: function(store, options) {
          var params = options.params;
          if (params.start && params.start > 0) {
            params.page = ((params.start + VENDOR_HISTORY_PAGE_SIZE) / VENDOR_HISTORY_PAGE_SIZE);
          } else { params.page = 1;}          
        }
      }
    });
    
    var casherManagerPwdStore = new Ext.data.JsonStore({
        baseParams: casherManagerPwdParams,
        url: "${ctx}/gadget/prepaymentMgmt/managerList.do",
        totalProperty: 'count',
        root: 'list',
        fields: ['casherId', 'vendor.loginId', 'status', 'lastUpdateDate','isManager'],
        listeners: {
          beforeload: function(store, options) {
            var params = options.params;
            if (params.start && params.start > 0) {
              params.page = ((params.start + VENDOR_HISTORY_PAGE_SIZE) / VENDOR_HISTORY_PAGE_SIZE);
            } else { params.page = 1;}          
          }
        }
      });

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
      // 취소된 결제내역의 경우, 영수증 버튼이 아닌 메시지가 출력된다. 
      if (rec.json.isCanceled) {
        return "<fmt:message key='aimir.canceled'/>"
      }

      var id = Ext.id();
      var $div = $("<div></div>").attr("id", id);
      var button = function() {
        if( $("#"+id).length > 0 && ($div.children().length < 1)) {
          new Ext.Button({
            text: '<fmt:message key="aimir.receipt"/>',            
            width: 50,
            handler: function(b, e) {
              eventHandler.openReceiptPopup(rec);
            }
          }).render(id);
        } else {
          button.defer(100);
        }        
      };
      button.defer(100);
      return $div[0].outerHTML;
    };

    var deleteCasherBtnArea = function(value, meta, rec) {
      var id = Ext.id();
      var $div = $("<div></div>").attr("id", id);
      var button = function() {
        if( $("#"+id).length > 0 && ($div.children().length < 1)) {
          new Ext.Button({
            text: '<fmt:message key="aimir.button.delete"/>',
            width: 50,
            handler: function(b, e) {
              eventHandler.deleteCasher(rec);
            }
          }).render(id);
        } else {
          button.defer(100);
        }
      };
      button.defer(100);
      return $div[0].outerHTML;      
    }
    
    var updatePasswordBtnArea = function(value, meta, rec) {
        var id = Ext.id();
        var $div = $("<div></div>").attr("id", id);
        var button = function() {
          if( $("#"+id).length > 0 && ($div.children().length < 1)) {
            new Ext.Button({
              text: '<fmt:message key="aimir.button.update"/>',
              width: 50,
              handler: function(b, e) {
                eventHandler.inputPassword(rec);
              }
            }).render(id);
          } else {
            button.defer(100);
          }
        };
        button.defer(100);
        return $div[0].outerHTML;      
      }

    var relayOnBtnArea = function(value, meta, rec) {
      var id = Ext.id();
      var $div = $("<div></div>").attr("id", id);  
      var button = function() {
        if( $("#"+id).length > 0 && ($div.children().length < 1)) {
          new Ext.Button({
            text: '<fmt:message key="aimir.meter.condition.RelayStatusOn"/>',
            width: 60,
            handler: function(b, e) {
              eventHandler.relayOn(rec.json);
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
      var recentId = rec.store.reader.jsonData.id;
      var result = rec.store.reader.jsonData.result;
      var currentId = rec.json.prepaymentLogId;
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
      if ( rec.json.isCanceled != true ) {
        button.defer(100);
        return $div[0].outerHTML;
      }
    };    
    
    var partpayInfoArea = function(value) {
        if(value != null) {
        	return value;
        } else {
        	return '-'
        }
      };    
    
    var casherStatusArea = function(value) {
      return casherStatus[value];
    };

    var contractListModel = new Ext.grid.ColumnModel({
        columns: [
            {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'CONTRACTNUMBER',
            	tooltip: "<fmt:message key='aimir.contractNumber'/>"}
           ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'CUSTOMERNO',
        	   tooltip: "<fmt:message key='aimir.customerid'/>" }
           ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'CUSTOMERNAME',
        	   tooltip: "<fmt:message key='aimir.customername'/>"}
           ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'MDSID',
        	   tooltip: "<fmt:message key='aimir.meterid'/>"}
           ,{header: "<fmt:message key='aimir.address'/>", dataIndex: 'ADDRESS',
        	   tooltip: "<fmt:message key='aimir.address'/>"}
           ,{header: "<fmt:message key='aimir.supplystatus'/>", dataIndex: 'STATUSNAME',
        	   tooltip: "<fmt:message key='aimir.supplystatus'/>"}
           ,{header: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>", dataIndex: 'LASTTOKENDATE', align: 'center',
               tooltip: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>"}
           ,{header: "<fmt:message key='aimir.credit'/>", dataIndex: 'CURRENTCREDIT',  align: 'right',
        	   tooltip: "<fmt:message key='aimir.credit'/>"}
           ,{header: "<fmt:message key='aimir.arrears'/>", dataIndex: 'CURRENTARREARS', align: 'right',
        	   tooltip: "<fmt:message key='aimir.arrears'/>"}
           ,{header: "<fmt:message key='aimir.debt'/>", dataIndex: 'DEBTAMOUNT', align: 'right', tooltip: "<fmt:message key='aimir.debt'/>"}
           ,{header: "<fmt:message key='aimir.amount.paid'/>", align: 'right', dataIndex: 'CHARGEAMOUNT', 
        	   tooltip: "<fmt:message key='aimir.amount.paid'/>",
             renderer: Ext.util.Format.numberRenderer("0,000.0000"),
             editor: new Ext.form.NumberField({
                 id: 'chPrice',
                 allowBlank: true,
                 allowNegative: false
             })
            }
           ,{header: "<fmt:message key='aimir.barcode'/>", align: 'left', dataIndex: 'BARCODE',
        	   tooltip: "<fmt:message key='aimir.barcode'/>",
              editor: new Ext.form.NumberField({
                id: 'barcode',
                allowBlank: true,
                allowNegative: false,
                listeners: {
                  change: function(field, newVal, oldVal) {
                    eventHandler.updateBarcode(field, newVal, oldVal);
                  }
                }
              })
            }         
           ,{header: "", renderer: saveBtnArea}
        ],
        defaults: {
            sortable: true
           ,menuDisabled: true                   
           ,renderer: addTooltip
        }
      });

    var historyListModel = new Ext.grid.ColumnModel({
        columns: [
          {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>", dataIndex:'lastTokenDate',
        	  tooltip: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
          {header: "<fmt:message key='aimir.chargeAmount'/>", align: 'right', dataIndex:'chargedCredit',
        		  tooltip: "<fmt:message key='aimir.chargeAmount'/>"},
          {header: "<fmt:message key='aimir.credit'/>", align: 'right', dataIndex:'balance',
        			  tooltip: "<fmt:message key='aimir.credit'/>"},
          {header: "<fmt:message key='aimir.paymenttype'/>",  align: 'center', dataIndex:'payType',
        				  tooltip: "<fmt:message key='aimir.paymenttype'/>"},
          {header: "", renderer: receiptBtnArea},        
          {header: "", renderer: cancelBtnArea, hidden: isHiddenCancelBtn},
          {header: "", renderer: relayOnBtnArea, hidden: true}
        ],
        defaults: {sortable: true, 
                  menuDisabled: true,
                  renderer: addTooltip
         }       
      });

    var balanceHistoryListModel = new Ext.grid.ColumnModel({
        columns: [
		  {header: "<fmt:message key='aimir.paydate'/>",  align: 'left', dataIndex:'writeDate',
    			tooltip: "<fmt:message key='aimir.paydate'/>"},
          {header: "<fmt:message key='aimir.meter.metertime'/>",  align: 'left', dataIndex:'lpTime',
           		tooltip: "<fmt:message key='aimir.meter.metertime'/>"},
          {header: "<fmt:message key='aimir.accu.usage'/>" + "[<fmt:message key='aimir.unit.kwh'/>]", dataIndex:'accUsage', align: 'right',
            	tooltip: "<fmt:message key='aimir.accu.usage'/>"+"[<fmt:message key='aimir.unit.kwh'/>]"},
          {header: "<fmt:message key='aimir.accu.bill'/>" + "[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'accBill',
            	 tooltip: "<fmt:message key='aimir.accu.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
          {header: "<fmt:message key='aimir.usage'/>" + "[<fmt:message key='aimir.unit.kwh'/>]", dataIndex:'usage', align: 'right',
                 tooltip: "<fmt:message key='aimir.usage'/>"+"[<fmt:message key='aimir.unit.kwh'/>]"},
       	  {header: "<fmt:message key='aimir.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'bill',
           	 	tooltip: "<fmt:message key='aimir.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
          {header: "<fmt:message key='aimir.balance'/>"+"[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'balance',
                tooltip: "<fmt:message key='aimir.balance'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
          {header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.import'/>)",  align: 'right', dataIndex:'activeImport',
                  tooltip: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.import'/>)"},
          {header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.export'/>)",  align: 'right', dataIndex:'activeExport',
                  tooltip:  "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.export'/>)"}
        ],
        defaults: {sortable: true, 
                  menuDisabled: true,
                  renderer: addTooltip
         }       
      });

    var contractInfoModel = new Ext.grid.ColumnModel({
        columns: [
            {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber',
            	tooltip: "<fmt:message key='aimir.contractNumber'/>"}
           ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo',
        	   tooltip: "<fmt:message key='aimir.customerid'/>" }
           ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName',
        	   tooltip: "<fmt:message key='aimir.customername'/>"}
           ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId',
        	   tooltip: "<fmt:message key='aimir.meterid'/>"}
           ,{header: "<fmt:message key='aimir.address'/>", dataIndex: 'address',
        	   tooltip: "<fmt:message key='aimir.address'/>"}
           ,{header: "<fmt:message key='aimir.supplystatus'/>", dataIndex: 'statusName',
        	   tooltip: "<fmt:message key='aimir.supplystatus'/>"}
           ,{header: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>", dataIndex: 'lastTokenDate', align: 'center',
               tooltip: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>"}
           ,{header: "<fmt:message key='aimir.credit'/>", dataIndex: 'currentCredit',  align: 'right',
        	   tooltip: "<fmt:message key='aimir.credit'/>"}
        ],
        defaults: {
            sortable: true
           ,menuDisabled: true                   
           ,renderer: addTooltip
        }
      });

      var debtHistoryListModel = new Ext.grid.ColumnModel({
          columns: [
            {header: "<fmt:message key='aimir.debtType'/>", align: 'right', dataIndex:'TYPE', tooltip: "<fmt:message key='aimir.debtType'/>"},
            {header: "<fmt:message key='aimir.chargedDebt'/>", align: 'right', dataIndex:'CHARGED', tooltip: "<fmt:message key='aimir.chargedDebt'/>"},
            {header: "<fmt:message key='aimir.debt'/>", align: 'right', dataIndex:'AMOUNT', tooltip: "<fmt:message key='aimir.debt'/>"},
            {header: "<fmt:message key='aimir.partpayInfo'/>", renderer: partpayInfoArea, align: 'center', dataIndex:'PARTPAYINFO',
            	tooltip: "<fmt:message key='aimir.partpayInfo'/>"},
          ],
          defaults: {sortable: true, 
                    menuDisabled: true,
                    renderer: addTooltip
           }       
        });

    var vendorHistoryModel = new Ext.grid.ColumnModel({
      columns: [
        {header: "<fmt:message key='aimir.vendor'/>"},
        {header: "<fmt:message key='aimir.prepayment.casher'/>"},
        {header: "<fmt:message key='aimir.buildingMgmt.contractNumber'/>"},
        {header: "<fmt:message key='aimir.customerid'/>"},
        {header: "<fmt:message key='aimir.meterid'/>"},
        {header: "<fmt:message key='aimir.customername'/>"},
        {header: "<fmt:message key='aimir.address'/>"},
        {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
        {header: "<fmt:message key='aimir.deposit.chargecredit'/>", align: 'right'},
        {header: "<fmt:message key='aimir.paymenttype'/>",  align: 'center'},
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

    var casherManagerModel = new Ext.grid.ColumnModel({
      columns: [
        {header: "<fmt:message key='aimir.prepayment.casher'/> <fmt:message key='aimir.id'/>"},
        {header: "<fmt:message key='aimir.name'/>"},
        {header: "<fmt:message key='aimir.status'/>", renderer: casherStatusArea},
        {header: "<fmt:message key='aimir.model.lastmodifieddate'/>"},
        {header: "<fmt:message key='aimir.manager'/>"},
        {header: "<fmt:message key='aimir.change.password'/>", renderer: updatePasswordBtnArea, hidden: isHiddenPasswordBtn},
        {header: "", renderer: deleteCasherBtnArea}
      ],
      defaults : {
        sortable: true,
        menuDisable: true,
        renderer: addTooltip
      }
    });
    
    var casherManagerPwdModel = new Ext.grid.ColumnModel({
        columns: [
          {header: "<fmt:message key='aimir.prepayment.casher'/> <fmt:message key='aimir.id'/>"},
          {header: "<fmt:message key='aimir.vendor'/>"},
          {header: "<fmt:message key='aimir.status'/>", renderer: casherStatusArea},
          {header: "<fmt:message key='aimir.model.lastmodifieddate'/>"},
          {header: "<fmt:message key='aimir.manager'/>"},
          {header: "<fmt:message key='aimir.change.password'/>", renderer: updatePasswordBtnArea, hidden: isGetAllManager}
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
    var contractInfoSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var balanceHistoryListSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var debtHistoryListSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var vendorHistorySm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var casherManagerSM = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var casherManagerPwdSM = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

    var bbar = {
      displayInfo: true,
      displayMsg: ' {0} - {1} / {2}'
    };

    var contractBbar = new Ext.PagingToolbar($.extend(
      true, 
      {}, 
      {store: contractListStore, pageSize: PAGE_SIZE}, 
      bbar));

    var historyBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: historyListStore, pageSize: PAGE_SIZE},
      bbar));
    
    var contractInfoBbar = new Ext.PagingToolbar($.extend(
    	true,
    	{},
    	{store: contractInfoStore, pageSize: PAGE_SIZE},
    	bbar));
    
    var balanceHistoryBbar = new Ext.PagingToolbar($.extend(
 	      true,
 	      {},
 	      {store: balanceHistoryListStore, pageSize: PAGE_SIZE},
 	      bbar));
    	    
    
    var debtHistoryBbar = new Ext.PagingToolbar($.extend(
		true,
		{},
		{store: debtHistoryListStore, pageSize: PAGE_SIZE},
		bbar));
    
    var vendorHistoryBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: vendorHistoryStore, pageSize: VENDOR_HISTORY_PAGE_SIZE},
      bbar));

    var casherManagerBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: casherManagerStore, pageSize: PAGE_SIZE},
      bbar));
    
    var casherManagerPwdBbar = new Ext.PagingToolbar($.extend(
      true,
      {},
      {store: casherManagerPwdStore, pageSize: VENDOR_HISTORY_PAGE_SIZE},
      bbar));

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
      height: 235,
      colModel: contractListModel,
      sm: contractListSm,
      bbar: contractBbar,
      store: contractListStore,
      renderTo: 'prepaymentChargeDiv'
    });

    var historyListProp = $.extend(true, {}, gridProp, {
      height: 235,
      colModel: historyListModel,
      sm: historyListSm,
      bbar: historyBbar,
      store: historyListStore,
      renderTo: "prepaymentChargeHistoryDiv"
    });
    
    var contractInfoProp = $.extend(true, {}, gridProp, {
        height: 235,
        colModel: contractInfoModel,
        sm: contractInfoSm,
        bbar: contractInfoBbar,
        store: contractInfoStore,
        renderTo: 'contractInfoDiv'
      });
    
    var balanceHistoryListProp = $.extend(true, {}, gridProp, {
        height: 235,
        colModel: balanceHistoryListModel,
        sm: balanceHistoryListSm,
        bbar: balanceHistoryBbar,
        store: balanceHistoryListStore,
        renderTo: "balanceHistoryDiv"
      });
    
    var debtHistoryListProp = $.extend(true, {}, gridProp, {
        height: 235,
        colModel: debtHistoryListModel,
        sm: debtHistoryListSm,
        bbar: debtHistoryBbar,
        store: debtHistoryListStore,
        renderTo: "debtHistoryDiv"
      });

    var vendorHistoryProp = $.extend(true, {}, gridProp, {
      height: 540,
      colModel: vendorHistoryModel,
      sm: vendorHistorySm,
      bbar: vendorHistoryBbar,
      store: vendorHistoryStore,
      renderTo: "depositChargeHistoryDiv"
    });

    var casherManagerProp = $.extend(true, {}, gridProp, {
      height: 540,
      colModel: casherManagerModel,
      sm: casherManagerSM,
      bbar: casherManagerBbar,
      store: casherManagerStore,
      renderTo: 'casherManagerDiv'
    });
    
    var casherManagerPwdProp = $.extend(true, {}, gridProp, {
    	height: 540,
    	colModel: casherManagerPwdModel,
    	sm: casherManagerPwdSM,
    	bbar: casherManagerPwdBbar,
    	store: casherManagerPwdStore,
    	renderTo: 'casherManagerPwdDiv'
    })

    var eventHandler = {      
      initDateFormat: function(inst) {
        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: inst.val()},
          function(data) {            
            $("." + inst.attr('name')).val(data.localDate);
          });
      },
      searchDebt: function(customerNo) {
    	  $.post(
                  "${ctx}/gadget/prepaymentMgmt/getDebtInfoByCustomerNo.do",
                      {customerNo: customerNo},
                      function(json) {
  						debtInfo = json.debtInfo;
                      }
                );
      },
      selectionDebtLog: function(rec) {
    	  var data = rec.selections.items[0].data;
    	  
    	  debtHistoryListParams.prepaymentLogId = data.prepaymentLogId;
    	  debtHistoryListStore.baseParams = debtHistoryListParams;
 	      debtHistoryListStore.load({
 	          params: debtHistoryListParams
 	        });
      },
      autoSearchDebtLog: function(data) {
    	  debtHistoryListParams.prepaymentLogId = data.prepaymentLogId;
    	  debtHistoryListStore.baseParams = debtHistoryListParams;
 	      debtHistoryListStore.load({
 	          params: debtHistoryListParams
 	        });
      },
      selectedHistorySearch: function(sm, rowIndex, rec) {
//        historyListModel.setHidden(10, true);
        historyListModel.setHidden(6, true);
        var contractNumber = rec.json.CONTRACTNUMBER;
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
      selectedBalanceHistorySearch: function(sm, rowIndex, rec) {
        var contractNumber = rec.json.contractNumber;
        var params = $.extend(true, {}, balanceHistoryListParams, {
          contractNumber: contractNumber,
          startDate: $("#infoDetailForm input[name=startDate]").val() || '00000000',
          endDate: $("#infoDetailForm input[name=endDate]").val() || '99999999',
          supplierId: supplierId
        });        
        $("#infoDetailForm input[name=contractNumberInfo]").val(contractNumber);

        balanceHistoryListStore.baseParams = params;
        balanceHistoryListStore.load({
          params: params
        });
      },

      historyListSearch: function() {
        var params = $.extend(true, {}, historyListParams, {
          contractNumber: $("#historyForm input[name=contractNumber]").val(),
          searchStartMonth:
            $("#historyForm input[name=startDate]").val() || '00000000',
          searchEndMonth: 
            $("#historyForm input[name=endDate]").val() || '99999999'
        });

        historyListStore.baseParams = params;
        historyListStore.load();
      },
      
      infoDetailSearch: function() {
          var params = $.extend(true, {}, balanceHistoryListParams, {
            contractNumber: $("#infoDetailForm input[name=contractNumberInfo]").val(),
            startDate: $("#infoDetailForm input[name=startDate]").val() || '00000000',
            endDate: $("#infoDetailForm input[name=endDate]").val() || '99999999'
          });

          balanceHistoryListStore.baseParams = params;
          balanceHistoryListStore.load();
        },

      modifiedDateFormat: function(date) {        
        var $this = $(this);

        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: date},
          function(data) {            
            $this.siblings("." + $this.attr('name')).val(data.localDate);
          });
      },

      contractListSearch: function(callback) {
        var params = $.extend(true, {}, contractListParams, {
          barcode: $("#barcodeNumber").val(),
          contractNumber: $("#contractNumber").val(),
          customerNo: $("#customerNo").val(),
          customerName: $("#customerName").val(),
          mdsId: $("#mdsId").val()
        });
        contractListStore.baseParams = params;
        contractListStore.load({
          params: params,
          callback: callback
        });
      },

      contractInfoSearch: function(callback) {
          var params = $.extend(true, {}, contractInfoParams, {
            contractNumber: $("#contractNumberInfo").val(),
            customerNo: $("#customerNoInfo").val(),
            customerName: $("#customerNameInfo").val(),
            mdsId: $("#mdsIdInfo").val()
          });
          contractInfoStore.baseParams = params;
          contractInfoStore.load({
            params: params,
            callback: callback
          });
        },

      depositHistoryListSearch: function() {
        var params = $.extend(true, {}, vendorHistoryParams, {
          vendor: $("#vendor").val(),
          reportType: $("#depositHistory select[name=reportType]").val(),
          subType : $("#depositHistory select[name=subType]").val(),
          contract: $("#depositHistory input[name=contract]").val(),
          customerName: $("#depositHistory input[name=customerName]").val(),
          customerNo: $("#depositHistory input[name=customerId]").val(),
          meterId: $("#depositHistory input[name=meterId]").val(),
          casherId: $("#depositHistory input[name=casherId]").val(),
          startDate: $("#depositHistory input[name=startDate]").val(),
          endDate: $("#depositHistory input[name=endDate]").val(),
          locationId : $("#locationId").val()
        });        
        vendorHistoryStore.baseParams = params;
        vendorHistoryStore.load();
      },

      saveChargeAmount: function(rec) {
    	  $.ajaxSetup({
              async : false
          });
        // 충전가능여부 체크
        var params = {
            contractNumber : rec.get("CONTRACTNUMBER")
        };
        var jsonText = $.ajax({
            type: "POST",
            url: "${ctx}/gadget/prepaymentMgmt/checkChargeAvailable.do",
            data: params,
            async: false
        }).responseText;

        eval("var json=" + jsonText);

        // 충전불가 인 경우
        if (json.result == null || json.result == false) {
            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.msg.notcharge"/>");
            return;
        }
        
        $.ajaxSetup({
            async : false
        });
        
		if(rec.get("CONTRACTNUMBER") != null) {
			$.ajax({
                type : "POST",
                async : false,
                data : {
                	contractNumber : rec.get("CONTRACTNUMBER"),
                	supplierId	   : supplierId
                },
                dataType : "json",
                url:'${ctx}/gadget/system/getContractByContractNumber.do',
                success: function(json,status) {
                	firstArrears = json.contract[0].firstArrears;
       	        	arrearsContractCount = json.contract[0].arrearsContractCount;
       	        	arrearsPaymentCount = json.contract[0].arrearsPaymentCount;
                }
            })
		} 
        
        eventHandler.searchDebt(rec.get("CUSTOMERNO"));
        
        var paidAmount = Number(rec.get("CHARGEAMOUNT"));
        var chargeArrears = Number(rec.get("CHARGEARREARS"));
        var currentArrears = NumberUtil.parseNumber(rec.get("CURRENTARREARS"));
        var currentAmount = NumberUtil.parseNumber(rec.get("CURRENTCREDIT"));
        var currentDebt = NumberUtil.parseNumber(rec.get("DEBTAMOUNT"));
        var contractPrice = Number(rec.get("CONTRACTPRICE"));// init Credit

        //미수금이 존재하는 분할납부 고객의 경우 몇회에 걸쳐 납부할 것인가를 등록해야함
        //초기 미수금인 initArrears의 경우 분할납부 대상이 아님
        if(isPartpayment == true || isPartpayment == 'true') {
        
        	if((arrearsContractCount == null || arrearsContractCount == '') && currentArrears > initArrears) {
        		var tempMsg = "<fmt:message key='aimir.register.arrearsContractCount'/>".replace("$SUPPLIER",supplierName);
               	Ext.Msg.show({
               		title: params.cusomterName,
               		msg: tempMsg,
               		buttons : Ext.MessageBox.OK
               		});
               	return;
        	}
        	
        	for(var i=0; i < debtInfo.length; i++ ) {
	            var temp = debtInfo[i].debtContractCount;
	            if(temp == null || temp == '' || temp == 'null') {
	            var tempMsg = "<fmt:message key='aimir.register.arrearsContractCount'/>".replace("$SUPPLIER",supplierName);
	                Ext.Msg.show({
	                  title: params.cusomterName,
	                  msg: tempMsg,
	                  buttons : Ext.MessageBox.OK
	                  });
	                return;
	            }
          }
        }
        $.ajaxSetup({
            async : true
        });
        
        var params = {
          contractNumber: rec.json.CONTRACTNUMBER,
          casherId: $("#loginWrapper input[name=loginId]").val(),
          contractId: rec.json.CONTRACTID,
          mdsId: rec.json.MDSID,
          contractDemand: rec.json.CONTRACTDEMAND || 0,
          contractPrice: contractPrice || 0, //initCredit
          currentAmount: currentAmount,
          currentArrears: currentArrears,
          currentDebt: currentDebt,
          //debtInfo: debtInfo,
          customerName: rec.json.CUSTOMERNAME || "",
          customerNo: rec.json.CUSTOMERNO || "",
          supplierId : supplierId,
          partpayReset : false,
          isPartpayment : isPartpayment,
          paidAmount: paidAmount || 0,
          chargedCredit : paidAmount,
          chargedDebtArr : ''
        };        

        var saveAction = function() {
          Ext.Msg.confirm("<fmt:message key='aimir.message'/>",
            "<fmt:message key='aimir.wouldSave'/>", function(btn) {
              if (btn == "yes") {
                emergePre();
                $.post(
                  "${ctx}/gadget/prepaymentMgmt/vendorSavePrepaymentChargeECG.do",
                      params,
                      function(json) {
                  
                        var receiptParam = {};
                        if (json != null && json.result == "success") {
                          Ext.Msg.alert("<fmt:message key='aimir.message'/>",
                           "<fmt:message key='aimir.save'/>",
                              function() {
                                contractListStore.reload();

                                $("#historyForm input").val(null);
                                $("#historyForm input[name=contractNumber]").val(params.contractNumber);
                                eventHandler.historyListSearch();
                                eventHandler.refreshDeposit(json.deposit);
                                // 결제를 진행한 후에 credit이 0 이상이고,
                                // 미터가 차단되어 있는 경우 
                                // relay on 수행

                                if(json.isCutOff == true && json.credit > 0) {
                                  eventHandler.relayOn(rec.json, function() {
                                    receiptParam.prepaymentLogId = 
                                      json.prepaymentLogId;
                                    receiptParam.contractId = 
                                      rec.json.contractId;
                                    eventHandler.chargeAfterReceipt(receiptParam);
                                  });
                                } else {
                                  receiptParam.prepaymentLogId = 
                                    json.prepaymentLogId;
                                  receiptParam.contractId = 
                                    rec.json.contractId;
                                  eventHandler.chargeAfterReceipt(receiptParam);
                                }
                              }
                            );

                          } else {
                              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                                json.result);
                          }
                          hide();
                          return;
                      }
                );
              }
          });
        }

        eventHandler.validateSaveAction(params, saveAction);
      },
      
      validateSaveAction: function(params, callback) {
    	  $.ajaxSetup({
              async : false
          });
    	  
        var currentArrears = params.currentArrears;
        var currentDebt = params.currentDebt;
        var currentAmount = params.currentAmount;
        var paidAmount = params.paidAmount;
        var chargeArrears = params.arrears;
        var contractPrice = params.contractPrice;
        var customerName = params.customerName;

        // validation check
        // 지불 미수금이 잔여 미수금 보다 큰 경우 X
        if (!isNaN(chargeArrears) && !isNaN(currentArrears) 
          && chargeArrears > currentArrears) {
          Ext.Msg.alert("<fmt:message key='aimir.alert'/>",
            "<fmt:message key='aimir.msg.check.input.arrears'/>");
          return;
        }

        // 지불 미수금과 지불 creit의 값이 숫자가 아닌 경우 
        if (isNaN(paidAmount) && isNaN(chargeArrears)) {
          return;
        }

        if(paidAmount) {

       	eventHandler.retypePaidAmount({
            title: customerName,
            msg: "<fmt:message key='aimir.retype.amount'/>",  //Please retype paid amount
            amount: paidAmount,
            amountLabel: "<fmt:message key='aimir.amount.paid'/>",
            payTypeLabel: "<fmt:message key='aimir.paymenttype'/>",
            callback: function(payType,checkNo,bankCode) {
     		  params.payTypeId = payType;   // cash or check
     		  params.checkNo = checkNo;
     		  params.bankCode = bankCode;

              if(currentArrears > 0 || currentDebt > 0) {
           	  //분할납부기능 사용여부
           	  //(분할납부 기능을 사용하고) && (초기미수금보다 현재 미수금이 크거나 || 분할납부가 진행중이여서 현재미수금이 초기미수금보다 작아진 경우) 는 분할납부 기능 사용
           	  //조건 다시 재정립필요

               	if((isPartpayment == true || isPartpayment == 'true') 
               			&& ((((currentArrears > initArrears) || currentDebt > 0) 
               					&& (arrearsContractCount != '' || arrearsContractCount != null))
               			|| ((currentArrears <= initArrears) && (Number(arrearsContractCount) > 0))) ) {

                    eventHandler.partPayment(params,callback);
               		
               	} else {
					eventHandler.notPartPayment(params,callback);
					
              	}
              } else {
                	callback();
              }
            }
          });

        }
      },

      retypePaidAmount: function (params) {
    	  $.ajaxSetup({
              async : false
          });
    	  var payTypeValue = '';
          var searchWin = new Ext.Window({
            title: params.title,
            modal: true, closable:true, resizable: true,
            width:250, height:200,
            border:true, plain:false,                      
            items:[{
                xtype: 'panel',
                frame: false, border: false,
                items:{
                  id: 'reTypeAmount_form',
                  xtype: 'form',
                  bodyStyle:'padding:10px',
                  labelWidth: 100,
                  frame: false, border: false,
                  items: [{
                    xtype: 'label', html:'<div style="text-align:left;">' + params.msg +'</div>',  anchor: '100%'
                  }, {
                    xtype: 'textfield', fieldLabel: params.amountLabel, id: 'retypeAmount_id', name: 'retypeAmount_name', anchor: '100%'
                  }, {
                    xtype: 'combo',
                    id:'payType_id', name: 'payType_name', value:'Select...',          
                    fieldLabel: params.payTypeLabel, triggerAction: 'all', editable: false, mode: 'local',
                    store: new Ext.data.JsonStore({
                      url: '${ctx}/gadget/prepaymentMgmt/vendorPrepaymentPayType.do',
                      storeId: 'payTypeListStore',
                      root: 'payTypeList',
                      idProperty: 'id',
                      fields: [{name: 'id', type: 'int'}, 'name','code'],
                      listeners: {
                        load: function(store, records, options){
                          Ext.getCmp('payType_id').setValue(records[0].id); // default : cash
                          payTypeValue =records[0].data.code;
                        }
                      }
                    }),
                    valueField: 'id', displayField: 'name',
                    anchor: '100%',
                    listeners: {
                      render: function() {
                        this.store.load();
                      },
                      select : function(combo, record, index){
                        payTypeValue = record.data.code;
                      }
                    }
                  }]
                }
            }],
            
            buttons: [{
              text: 'Ok',
              handler: function() {
                var reTypeValue = Number(Ext.getCmp('retypeAmount_id').getValue());
                if (!isNaN(params.amount)) {
                  if(reTypeValue != params.amount) {
                    Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.value'/>");
                    return;
                  } else {
                    var payType = Ext.getCmp('payType_id').getValue();
                    if(payType == '' || payType == 'Select...'){
                      Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.value'/>");
                    }else{
                    	if(payTypeValue == '18.2') {
                    		var checkInfoWin = new Ext.Window({
                                title: 'Check',
                                modal: true, closable:true, resizable: true,
                                width:250, height:200,
                                border:true, plain:false,
                                items:[{
                                    xtype: 'panel',
                                    frame: false, border: false,
                                    items:{
                                      id: 'checkInfo_form',
                                      xtype: 'form',
                                      bodyStyle:'padding:10px',
                                      labelWidth: 100,
                                      frame: false, border: false,
                                      items: [{
                                          xtype: 'label', html:'<div style="text-align:left;">' + 'Please Input the check Information' +'</div>',  anchor: '100%'
                                      }, {
                                        xtype: 'textfield', fieldLabel: 'checkNo', id: 'checkNo_id', name: 'checkNo_name', anchor: '100%'
                                      },{
                                        xtype: 'textfield', fieldLabel: 'bankCode', id: 'bankCode_id', name: 'bankCode_name', anchor: '100%'
                                      }]
                                    }
                                }],

                                buttons: [{
                                  text: '<fmt:message key="aimir.ok"/>',
                                  handler: function() {
                                	  var checkNo = Ext.getCmp('checkNo_id').getValue();
                                	  var bankCode = Ext.getCmp('bankCode_id').getValue();

                                	  if(checkNo == '') {
                                		  Ext.Msg.alert("<fmt:message key='aimir.message'/>","Please input the Check No.");
                                		  return;
                                	  }
                                	  if(bankCode =='' || isNaN(bankCode)) {
                                		  Ext.Msg.alert("<fmt:message key='aimir.message'/>","Please input correct value of the Bank Code.");
                                		  return;
                                	  }

                                	  params.callback(payType,checkNo,bankCode);
                                      checkInfoWin.close();

                                  }
                                }, {
                                  text: '<fmt:message key="aimir.cancel"/>',
                                  handler: function() {
                                    checkInfoWin.close();
                                  }
                                }]
                              });

                             checkInfoWin.show(this);
                    	} else {
                    		params.callback(payType,'','');
                    	}
                    }
                  }
                }
                searchWin.close();
              }
            }, {
              text: '<fmt:message key="aimir.cancel"/>',
              handler: function() {
                searchWin.close();
              }
            }]
          });

         searchWin.show(this);
      },

      partPayment: function (params,callback) {
    	  $.ajaxSetup({
              async : false
          });
    	//pratArrears : 한회에 내야할 분할납부금 을 의미
    	var partArrears = 0;
    	if(firstArrears != null && firstArrears != '' && arrearsContractCount > 0) {
    		partArrears = (firstArrears/arrearsContractCount).toFixed(2);
    	}

      	var debtInfoSize = debtInfo.length;
      	for(var i =0; i < debtInfoSize; i++) {
      		if(debtInfo[i].firstDebt != null && debtInfo[i].firstDebt != '') {
      			debtInfo[i].partpay = (debtInfo[i].firstDebt/debtInfo[i].debtContractCount).toFixed(2);
      		} else {
      			debtInfo[i].partpay = 0;
      		}
      	}
        
        //마지막 미수금 지붉순서일 경우
        if((arrearsContractCount-1) == arrearsPaymentCount) {
        	// 남은 미수금액을 모두 지불하도록 한다.(분할납부금액이 소수점단위로 나올 경우를 위함.)
			partArrears = params.currentArrears;
        }
        
        for(var i =0; i < debtInfoSize; i++) {
            if((debtInfo[i].debtContractCount-1) == debtInfo[i].debtContractCount) {
            	debtInfo[i].partpayAmount = debtInfo[i].debtAmount;
            }        	
        }

        
        if(partArrears > params.amount) {
        	for(var i; i<debtInfoSize; i++) {
        		var flag = true;
        		if(debtInfo[i].paymentAmount > params.amount) {
        			flag = true && flag;
        		}
        	}
        	
        	if(flag) {
        		//분할미수금보다 적은 금액을 입력한 경우 경고창
        		var tempMsg = "<fmt:message key='aimir.debt'/>" 
            	Ext.Msg.show({
            		title: params.cusomterName,
            		msg: tempMsg,
            		buttons : Ext.MessageBox.OK
            		});
            	return;
        	}
        }
        eventHandler.payInput(partArrears, params, callback);
      },

      notPartPayment : function (params,callback) {
    	  $.ajaxSetup({
              async : false
          });
    	//이전 grid 초기화
    	  $('#payInputCmp').html('');
    	  $('#payInputCmp').width(430);
    	  
    	  var width = $('#payInputCmp').width();

    	  var store = new Ext.data.ArrayStore({
  			  fields: ['type', 'amount']
  		  });

  		  var storeRecord = Ext.data.Record.create([
			{name:'type', name:'amount'}    		                                            
		  ]);

  		  var storeSub = new Array();
  		  var i;
  		  for(i = 0; i < debtInfo.length; i++) {
  			 storeSub[i] = new storeRecord({
  				 type: debtInfo[i].debtType,
  				 amount: debtInfo[i].debtAmount,
  				 ref: debtInfo[i].debtRef
  			 });
  		  }
  		//Arrears 정보 삽입
  		  storeSub[i++] = new storeRecord({
			 type: '<fmt:message key="aimir.arrears"/>',
			 amount: params.currentArrears,
		 });
  		
  		 store.insert(0,storeSub);
  		 
  		 var checkSelModel = new Ext.grid.CheckboxSelectionModel({
            checkOnly:true
         });
		  
		  var colModel = new Ext.grid.ColumnModel({
			defaults: {
              sortable: true,
              menuDisabled: true,
          },
          columns: [
              {header: '<fmt:message key="aimir.type2"/>', dataIndex: 'type', align: 'left',
              tooltip: '<fmt:message key="aimir.type2"/>', sortable: true, width:width/4},
              {header: '<fmt:message key="aimir.arrears"/>', dataIndex: 'amount', align: 'right',
              tooltip: '<fmt:message key="aimir.arrears"/>', sortable: true, width:width/4},
              {header: "<fmt:message key='aimir.paidDebt'/>", dataIndex: 'payAmount', align: 'right',
              tooltip: "<fmt:message key='aimir.paidDebt'/>", sortable: true, width:width/4,
	        	  renderer: Ext.util.Format.numberRenderer("0,000.0000"),
		              editor: new Ext.form.NumberField({
		                 id: 'payAmount',
		                 allowBlank: true,
		                 allowNegative: false
		             })},
		      checkSelModel
           ]
		  });
		  
		  var debtGrid = new Ext.grid.EditorGridPanel({
	   			 store: store,
	   			 loadMask: true,
	   			 colModel: colModel,
	   			 sm: checkSelModel,
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: width,
	  		      height: 130 ,
	   		  });
   		  // 밑에 경고문 : 적어도 하나이상의 미수금은 충전해야함
   		  // 미수금 전액 남부 체크버튼 클릭해서 해당 박스 체크시 자동으로!
			  var payInputWin = new Ext.Window({
          		   title: "<fmt:message key='aimir.partpayInfo'/>",
                  id: 'payInputCmpId',
                  applyTo:'payInputCmp',
                  width:width+30,
                  height:150,
                  shadow : false,
                  autoHeight: true,
                  pageX : 460,
                  pageY : 130, 
                  resizable:false,
                  plain: true,
                  items: [debtGrid,
                          {xtype:'checkbox',id:'fullPay',name:'fullPay',boxLabel:"FullPay : <fmt:message key='aimir.fullPay.arrears'/>",inputValue:'fullPay',checked:false },
                          {xtype: 'displayfield',
                          value: '',
                          itemId: 'yourSelection',
                          fieldLabel: '<b>You have Selected</b>',
                          anchor: '100%'}],
                  buttons : [{text : '<fmt:message key="aimir.ok"/>',
		            	handler : function(store,b) {		
							//cnt 체크
							//하나라도 충전하도록 체크
							
							var selectArr = checkSelModel.getSelections();
							var fullPayCk = Ext.getCmp('fullPay').checked;
							var flag = eventHandler.validationCheck(selectArr, params, fullPayCk);
							
							if(flag) {
								var confirmMsg = '';
								confirmMsg = "";

								//params.chargedDebtArr = JSON.stringify(debtArrList);
								var chargedDebtArr = JSON.parse(params.chargedDebtArr);
								if(chargedDebtArr.length > 0) {
									confirmMsg = "<fmt:message key='aimir.confirm.arrears.paid'/>";
								}else {
									Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.pay.onemore.arrears'/>");
									return false;
								}
								
								for(var i=0; i<chargedDebtArr.length; i++) {
									var tempArr = chargedDebtArr[i][0];
								
									confirmMsg += "<br/>" + tempArr.debtType + " : " + tempArr.payAmount;
								}

								Ext.Msg.show({
						              title: params.customerName,
						              msg: confirmMsg,
						              buttons: Ext.MessageBox.OKCANCEL,
						              fn : function(btn) {
						                	if(btn == 'ok') {
						                		payInputWin.hide(this);       
												callback();
						                	}
						              }
						            }); 
							}
							
	                	}},
	                	{text : '<fmt:message key="aimir.cancel"/>',
	                	handler : function() {
	                		
	                		payInputWin.hide(this);        		
	                	}}],
                  closeAction:'hide',	                
                  onHide : function(){
                  }       
              });	
			  payInputWin.show(this);
      		  winOn=true;
      },
      payInput: function (partArrears, params, callback) {
    	  $.ajaxSetup({
              async : false
          });
    	  //이전 grid 초기화
    	  $('#payInputCmp').html('');
    	  $('#payInputCmp').width(430);
    	  
    	  var width = $('#payInputCmp').width();

    	  var store = new Ext.data.ArrayStore({
  			  fields: ['type', 'amount','partCredit']
  		  });

  		  var storeRecord = Ext.data.Record.create([
			{name:'type', name:'amount', name:'partCredit'}    		                                            
		  ]);

  		  var storeSub = new Array();
  		  var i;
  		  for(i = 0; i < debtInfo.length; i++) {
  			 storeSub[i] = new storeRecord({
  				 type: debtInfo[i].debtType,
  				 amount: debtInfo[i].debtAmount,
  				 partCredit: debtInfo[i].partpay,
  				 ref: debtInfo[i].debtRef
  			 });
  		  }
  		//Arrears 정보 삽입
  		  storeSub[i++] = new storeRecord({
			 type: '<fmt:message key="aimir.arrears"/>',
			 amount: params.currentArrears,
			 partCredit: partArrears
		 });
  		
  		 store.insert(0,storeSub);
  		 
  		 var checkSelModel = new Ext.grid.CheckboxSelectionModel({
            checkOnly:true
         });
		  
		  var colModel = new Ext.grid.ColumnModel({
			defaults: {
              sortable: true,
              menuDisabled: true,
          },
          columns: [
              {header: '<fmt:message key="aimir.type2"/>', dataIndex: 'type', align: 'left',
              tooltip: '<fmt:message key="aimir.type2"/>', sortable: true, width:width/4},
              {header: '<fmt:message key="aimir.arrears"/>', dataIndex: 'amount', align: 'right',
              tooltip: '<fmt:message key="aimir.arrears"/>', sortable: true, width:width/4},
              {header: "<fmt:message key='aimir.partpayDebt'/>", dataIndex: 'partCredit',align: 'right',
              tooltip: "<fmt:message key='aimir.partpayDebt'/>", sortable: true, width:width/4},
              {header: "<fmt:message key='aimir.paidDebt'/>", dataIndex: 'payAmount', align: 'right',
              tooltip: "<fmt:message key='aimir.paidDebt'/>", sortable: true, width:width/4,
	        	  renderer: Ext.util.Format.numberRenderer("0,000.0000"),
		              editor: new Ext.form.NumberField({
		                 id: 'payAmount',
		                 allowBlank: true,
		                 allowNegative: false
		             })},
		      checkSelModel
           ]
		  });
		  
		  var debtGrid = new Ext.grid.EditorGridPanel({
	   			 store: store,
	   			 loadMask: true,
	   			 colModel: colModel,
	   			 sm: checkSelModel,
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: width,
	  		      height: 130 ,
	   		  });
   		  // 밑에 경고문 : 적어도 하나이상의 미수금은 충전해야함
   		  // 미수금 전액 남부 체크버튼 클릭해서 해당 박스 체크시 자동으로!
			  var payInputWin = new Ext.Window({
          		   title: "<fmt:message key='aimir.partpayInfo'/>",
                  id: 'payInputCmpId',
                  applyTo:'payInputCmp',
                  width:width+30,
                  height:150,
                  shadow : false,
                  autoHeight: true,
                  pageX : 460,
                  pageY : 130, 
                  resizable:false,
                  plain: true,
                  items: [debtGrid,
                          {xtype:'checkbox',id:'fullPay',name:'fullPay',boxLabel:"FullPay : <fmt:message key='aimir.fullPay.arrears'/>",inputValue:'fullPay',checked:false },
                          {xtype: 'displayfield',
                          value: '',
                          itemId: 'yourSelection',
                          fieldLabel: '<b>You have Selected</b>',
                          anchor: '100%'}],
                  buttons : [{text : '<fmt:message key="aimir.ok"/>',
		            	handler : function(store,b) {		
							//cnt 체크
							//하나라도 충전하도록 체크
							
							var selectArr = checkSelModel.getSelections();
							var fullPayCk = Ext.getCmp('fullPay').checked;
							var flag = eventHandler.partpayValidationCheck(selectArr, params, fullPayCk, partArrears);
							
							if(flag) {
								var confirmMsg = '';
								confirmMsg = "";

								//params.chargedDebtArr = JSON.stringify(debtArrList);
								var chargedDebtArr = JSON.parse(params.chargedDebtArr);
								if(chargedDebtArr.length > 0) {
									confirmMsg = "<fmt:message key='aimir.confirm.arrears.paid'/>";
								}else {
									Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.pay.onemore.arrears'/>");
									return false;
								}
								
								for(var i=0; i<chargedDebtArr.length; i++) {
									var tempArr = chargedDebtArr[i][0];

									confirmMsg += "<br/>" + tempArr.debtType + " : " + tempArr.payAmount;
								}

								Ext.Msg.show({
						              title: params.customerName,
						              msg: confirmMsg,
						              buttons: Ext.MessageBox.OKCANCEL,
						              fn : function(btn) {
						                	if(btn == 'ok') {
						                		payInputWin.hide(this);       
												callback();
						                	}
						              }
						            }); 
							}
							
	                	}},
	                	{text : '<fmt:message key="aimir.cancel"/>',
	                	handler : function() {
	                		
	                		payInputWin.hide(this);        		
	                	}}],
                  closeAction:'hide',	                
                  onHide : function(){
                  }       
              });	
			  payInputWin.show(this);
      		  winOn=true;
      },
	  partpayValidationCheck: function(selectArray, params, fullPayCk, partArrears) {
		  $.ajaxSetup({
	            async : false
	        });
		  var arrearsPaySum = 0;
		  var debtArrList = new Array();

		  if(fullPayCk) {
			  
			  arrearsPaySum = params.currentArrears;
			  
			  if(params.paidAmount < arrearsPaySum) {
					Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.amount.short.cannot.full'/>");
					return false;
				}
			  
			  var debtArr = new Array();
			  debtArr.push({
				debtType : 'Arrears',
				debtRef  : '',
				payAmount : params.currentArrears,
				partCredit : params.currentArrears
			  })
			  debtArrList.push(debtArr);
			  for(var j=0;j<debtInfo.length; j++){
				  	var data = debtInfo[j];
				  	debtArr = new Array();
				  
					//arrears,debt를 pay한 갑싱 더 클때
					arrearsPaySum = arrearsPaySum + data.debtAmount;

					if(params.paidAmount < arrearsPaySum) {
						Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.amount.short.cannot.full'/>");
						return false;
					}

					debtArr.push({
						debtType : data.debtType,
						debtRef : data.debtRef,
						payAmount : data.debtAmount,
						partCredit: data.debtAmount
					});

					debtArrList.push(debtArr);
			  }
		  } else {
			  for(var j=0;j<selectArray.length; j++){
				  data = selectArray[j].data;
				  var debtArr = new Array();
				  //숫자인가
				  if(isNaN(data.payAmount)) {
					  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.msg.onlyNumber'/>");
					  return false;
				  }
				  
				  //partpayAmount보다 많은 값을 입력해야한다.
				  if(data.partCredit > data.payAmount) {
					  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.pay.more.debt'/>");
					  return false;
				  }
				  
				  if(data.amount < data.payAmount) {
					  data.payAmount = data.amount;
				  }
				  
				//arrears,debt를 pay한 갑싱 더 클때
				arrearsPaySum = arrearsPaySum + data.payAmount;
				
				debtArr.push({
					debtType : data.type,
					debtRef : data.ref,
					payAmount : data.payAmount,
					partCredit: data.partCredit
				});

				debtArrList.push(debtArr);
			  } 
			  
			  if(arrearsPaySum >  params.paidAmount) {
				  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.more.amount.paidAmount'/>");
				  return false;
			  }
		  }

		  params.chargedCredit = params.chargedCredit - arrearsPaySum;
		  params.chargedDebtArr = JSON.stringify(debtArrList);
		  
	  	 return true;  
      },
      validationCheck: function(selectArray, params, fullPayCk) {
    	  $.ajaxSetup({
              async : false
          });
		  var arrearsPaySum = 0;
		  var debtArrList = new Array();

		  if(fullPayCk) {
			  
			  arrearsPaySum = params.currentArrears;
			  
			  var debtArr = new Array();
			  debtArr.push({
				debtType : 'Arrears',
				debtRef  : '',
				payAmount : params.currentArrears
			  })
			  debtArrList.push(debtArr);
			  for(var j=0;j<debtInfo.length; j++){
				  	var data = debtInfo[j];
				  	debtArr = new Array();
				  
					//arrears,debt를 pay한 갑싱 더 클때
					arrearsPaySum = arrearsPaySum + data.debtAmount;
					
					if(params.paidAmount < arrearsPaySum) {
						Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.amount.short.cannot.full'/>");
						return false;
					}
					
					debtArr.push({
						debtType : data.debtType,
						debtRef : data.debtRef,
						payAmount : data.debtAmount
					});

					debtArrList.push(debtArr);
			  }
		  } else {
			  for(var j=0;j<selectArray.length; j++){
				  data = selectArray[j].data;
				  var debtArr = new Array();
				  //숫자인가
				  if(isNaN(data.payAmount)) {
					  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.msg.onlyNumber'/>");
					  return false;
				  }
				  
				  if(data.amount < data.payAmount) {
					  data.payAmount = data.amount;
				  }
				  
				//arrears,debt를 pay한 갑싱 더 클때
				arrearsPaySum = arrearsPaySum + data.payAmount;
				
				debtArr.push({
					debtType : data.type,
					debtRef : data.ref,
					payAmount : data.payAmount
				});

				debtArrList.push(debtArr);
			  } 
			  
			  if(arrearsPaySum >  params.paidAmount) {
				  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.more.amount.paidAmount'/>");
				  return false;
			  }
		  }

		  params.chargedCredit = params.chargedCredit - arrearsPaySum;
		  params.chargedDebtArr = JSON.stringify(debtArrList);
		  
	  	 return true;  
      },
      relayOn: function (rec, callback) {
        var params = {
          mcuId: rec.mcuId,
          target: rec.meterId,
          loginId: 'admin'
        };

        emergePre();
        $.post("${ctx}/gadget/device/command/cmdRemotePowerOn.do",
          params,
          function(json) {
            Ext.Msg.alert("<fmt:message key='aimir.info'/>",
              json.status);
            if(json.status != 'SUCCESS') {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.error'/>");                     
//              historyListModel.setHidden(10, false);
                historyListModel.setHidden(6, false);
            }
            hide();
            if (callback) {
              callback();
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
        var url = "${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptPopupWithDebt.do";
        var opt = "width=350px, height=615px, resizable=no, status=no";

        var params = {
          vendor: vendor,
          supplierId: supplierId,
          contractId: rec.contractId || rec.json.contractId,
          prepaymentLogId: rec.prepaymentLogId || rec.json.prepaymentLogId
        }
        
        if ( receiptPopupWindow ) {
          receiptPopupWindow.close();
        }
        
        var queryString = CommonUtil.getQueryString(params);
        receiptPopupWindow = window.open(url + queryString, "receiptPopupWindow", opt);
      },        
      
      initChargeTab: function() {
    	$('#arrearsInfo').hide();
        eventHandler.refreshDeposit();        
        contractListStore.reload();
        historyListStore.reload();
      },

      initInfoTab: function() {
      	$('#arrearsInfo').hide();
          //eventHandler.refreshDeposit();        
          contractInfoStore.reload();
      },

      initHistoryTab: function() {
    	 $('#arrearsInfo').show();
        eventHandler.depositHistoryListSearch();
      },      

      updateContractPrice: function(field, newVal, oldVal) {
        var params = {
          contractNumber : field.gridEditor.record.data.contractNumber,
          contractPrice : newVal
        };                 
        $.post("${ctx}/gadget/prepaymentMgmt/vendorSetContractPrice.do",
          params, function(data) {
            if(data.result == 'success'){
              Ext.Msg.alert("<fmt:message key='aimir.message'/>",
                "<fmt:message key='aimir.success'/>");
            } else {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                "<fmt:message key='aimir.erroroccured'/>");
              contractListStore.reload();
            }
        });
      },

      updateBarcode: function(field, newVal, oldVal) {
        var params = {
          contractId: field.gridEditor.record.json.contractId,
          barcode: newVal
        };
        
        var update = function() {
          $.post("${ctx}/gadget/prepaymentMgmt/updateBarcode.do",
            params, function(data) {
              if(data.result == 'success'){
                Ext.Msg.alert("<fmt:message key='aimir.message'/>",
                  "<fmt:message key='aimir.success'/>");
              } else {
                Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                  data.result);                
              }
              contractListStore.reload();
          });        
        }

        Ext.Msg.confirm(field.gridEditor.record.json.customerName,
        "<fmt:message key='aimir.msg.update.barcode'/>", function(btn) {
          if(btn == 'yes') {
            update();
          }
        });
      },

      clearContractListSearchForm: function(callback) {
        $("#contractForm .clear-form input").val(null);
        eventHandler.contractListSearch(callback);
      },

      selectBarcode: function() {
        eventHandler.clearContractListSearchForm(function() {contractListSm.selectFirstRow();});
      },

      logIn: function() {
        var params = {
          casherId: $("#loginWrapper input[name=loginId]").val(),
          pw: $("#loginWrapper input[name=password]").val() || "",
          vendorId: vendor,
          //mac: document.MacAddress.getMacAddress() || ""          
        };

        var logOn = function(json) {
          isHiddenCancelBtn = (vendorRole == 'admin' || vendorRole == 'edh_vendor' || (vendorRole == 'ECG vendor' && isManager)) ? false : true; 
          //historyListModel.setHidden(9, isHiddenCancelBtn);
          historyListModel.setHidden(5, isHiddenCancelBtn);
          //clientMacAddress = document.MacAddress.getMacAddress();
          //macAddressList = document.MacAddress.getMacAddressList();

          $("#menu").show();
          $("#loginWrapper").hide();
          eventHandler.initChargeTab();
          
          if(vendorRole == 'admin') {
        	  $("#menu li.infoTab").show();
          } else {
        	  $("#menu li.infoTab").hide();
          }

          if ( isFirstLogIn ) {
            $("#menu li.passwordTab").show();
            $("#confirm-pwd").hide();
            $("#confirm-pwd input").val(null);
            $("#change-pwd").show();            
            $("#menu li.passwordTab a").trigger('click');
            $("#menu li.chargeTab").hide();
            $("#menu li.historyTab").hide();
            $("#menu li.managerTab").hide();
            $("#menu li.managerPwdTab").hide();
          }

          if ( !isManager ) {
            $("#menu li.managerTab").hide();
          }
          
          if (!isGetAllManager) {
        	  $("#menu li.managerPwdTab").hide();
        	  $("#managerPwd-confirm").hide();
              $("#managerPwd-change").hide();
          } else {
        	  $("#managerPwd-confirm").show();
              $("#managerPwd-change").hide();
          }
        };

        $.post("${ctx}/gadget/prepaymentMgmt/casherLogin.do", params,
          function(json) {
        	var success = "<fmt:message key='aimir.success'/>";
            if (json.result == success) {
              isManager = json.isManager;
              isGetAllManager = (isManager == true) && (vendorRole == 'admin');
              if(isManager) {
            	  isHiddenPasswordBtn = true;  
              }
               
              isFirstLogIn = json.isFirstLogIn != null ? json.isFirstLogIn : true;
              logOn(json);
            } else {
                Ext.Msg.alert("<fmt:message key='aimir.message'/>", json.result);
            }
        });
      },

      addCasher: function() {
        var params = {
          vendor: vendor,
          casherId: $.trim($("#managerTab input[name=casherId]").val()),
          name: $.trim($("#managerTab input[name=name]").val()),
          isManager: $("#managerTab input[name=isManager]").is(":checked") || false,
          lastUpdateDate: $.format.date(new Date(), "yyyyMMddHHmmss")
        };

        var addCasher = function() {
          $.post("${ctx}/gadget/prepaymentMgmt/addCasher.do", params, 
            function(json) {
              Ext.Msg.alert("<fmt:message key='aimir.message'/>", json.result);
              if ( json.result == 'success' ) {
                casherManagerStore.reload();
                $("#managerTab input").val(null);
                $("#managerTab input[name=isManager]").val(false);
              } else {
                Ext.Message.alert("<fmt:message key='aimir.error'/>", json.error);
              }
            });
        };

        var validation = {
          // 입력된 폼에 누락된 값이 없는 지 체크 
          checkMissingValue: function() {
            var isValidate = false;
            var id = $.trim($("#managerTab input[name=casherId]").val());
            var name = $.trim($("#managerTab input[name=name]").val());

            if ( !id ) {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.alert.input.casher.id'/>",
                function() {
                  if ( !name ) {
                    Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                      "<fmt:message key='aimir.alert.input.casher.name'/>");
                  }
                });
            } else if ( !name ) {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.alert.input.casher.name'/>");
            } else {
              isValidate = true;
            }
            return isValidate;
          },

          // id중복 체크            
          validateAndRun : function() {
            $.post("${ctx}/gadget/prepaymentMgmt/isDuplicate.do", params,
              function(json) {
                if( json.result == true ) {
                  Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                    json.error);
                } else {
                  addCasher();
                }
              });
          },
        };

        if ( validation.checkMissingValue() ) {
          validation.validateAndRun();  
        }
      },
      
      deleteCasher: function(rec) {
        var params = {
          id: rec.json.id,
          date : $.format.date(new Date(), "yyyyMMddHHmmss")
        };

        var deleteCasher = function() {
          $.post("${ctx}/gadget/prepaymentMgmt/deleteCasher.do", params, 
            function(json) {
              Ext.Msg.alert("<fmt:message key='aimir.message'/>", json.result);
              if ( json.result == 'success' ) {
                casherManagerStore.reload();
              }
            });
        };

        deleteCasher();
      },
      
      inputPassword: function(rec) {
    	  var changeCashierId = rec.json.casherId;
    	  var changeVendorId = rec.json.vendor.loginId; 

    	  if(Ext.getCmp('CashierChangePwdWinId') == undefined) {
	    	  var passChange = new Ext.FormPanel ({
	   		  	frame:true,
				width: 300,
				height: 160,
				bodyStyle:'padding:5px 5px 5px 5px',
				defaultType: 'textfield',
				items: [{ 
	            	xtype:'fieldset',
	                title: '<fmt:message key="aimir.hems.alert.inputPassword"/>',
	                bodyStyle:'padding:5px 5px 5px 5px',
	                //collapsible: true,
	                autoHeight:true,
	                defaultType: 'textfield', 
				     items : [{
				    	 fieldLabel : "<fmt:message key='aimir.newpassword'/>",
						xtype : 'textfield',
						inputType: 'password',
						id	 : 'fp_password',
						name : 'fp_password'},
						{fieldLabel : "<fmt:message key='aimir.userreg.confirmpassword'/>",
						xtype : 'textfield',
						inputType: 'password',
						id	 : 'fp_confirmPwd',
						name : 'fp_confirmPwd'}]
				}],
				buttons:[{text : '<fmt:message key="aimir.ok"/>',
						handler : function() {
							var pass = Ext.getCmp('fp_password').getValue();
							var checkPass = Ext.getCmp('fp_confirmPwd').getValue();	
							if(pass == checkPass) {
								
								Ext.Msg.show({
										title: changeCashierId,
							            msg:  "<fmt:message key='aimir.wouldChange'/>",
							            buttons: Ext.MessageBox.OKCANCEL,
							            fn: function(btn, text) {
							            	if(btn == 'ok') {
							            		var params = {
					        				          vendor: changeVendorId,
					        				          casherId: changeCashierId, 
					        				          password: checkPass
				        				        };
							            		
							            		$.post("${ctx}/gadget/prepaymentMgmt/changePwd.do", params,
							   				            function(json) {
							   				              if (json.result == "success") {
							   				            	passChangeWin.hide();
						   									Ext.getCmp('fp_password').setValue();
						   									Ext.getCmp('fp_confirmPwd').setValue();
							   				            	Ext.Msg.alert(changeCashierId,"<fmt:message key='aimir.success'/>");
							   				              } else {
							   				            	passChangeWin.hide();
						   									Ext.getCmp('fp_password').setValue();
						   									Ext.getCmp('fp_confirmPwd').setValue();
							   				            	Ext.Msg.alert(changeCashierId,"<fmt:message key='aimir.failed'/>");
							   				              }              
						   				            	}
						          				  );
							            		
							            	} else {
							            		return false;
							            	}
							            }
								});
	          				  
	          			  } else {
	          				Ext.getCmp('fp_password').setValue();
							Ext.getCmp('fp_confirmPwd').setValue();
	          				Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
	          	                "<fmt:message key='aimir.hems.alert.notMatchPassword'/>");
	          				//$('#fp_password').focus();
	          			  }
	
						}},
						{text : '<fmt:message key="aimir.cancel"/>',
						handler : function() {
							passChangeWin.hide();
							Ext.getCmp('fp_password').setValue();
							Ext.getCmp('fp_confirmPwd').setValue();
							
						}}
						]
	    	  });
	    	  
			  passChangeWin = new Ext.Window({
				    title : changeCashierId,
				    id : 'CashierChangePwdWinId',
				    applyTo : 'CashierChangePwdWin',
				    autoScroll : true,
				    width : 330,
					height : 230,
					pageX : 600,
					pageY : 130,
				    items : [passChange],
				    closeAction : 'hide'
				});
				//Ext.getCmp('passChangeWin').show();
				passChangeWin.show(this);
    	  } else {
    		  passChangeWin.show(this);
    	  }
      },
      
      confirmPwd: function() {
        var prevPwd = $("#loginWrapper input[name=password]").val();
        var currPwd = $("#passwordTab input[name=prevPwd]").val();
        
        if ( prevPwd == currPwd ) {
          $("#confirm-pwd").hide();
          $("#confirm-pwd input").val(null);
          $("#change-pwd").show();
        } else {
          Ext.Msg.alert("<fmt:message key='aimir.error'/>",
            "<fmt:message key='aimir.msg.confirmpassword'/>");
        }
      },

      changePwd: function() {
        var params = {
          vendor: vendor,
          casherId: $("#loginWrapper input[name=loginId]").val(), 
          password: $("#passwordTab input[name=rePwd]").val()
        };
        
        var validation = {
          identifyPwd: function() {
            var newPwd = $("#change-pwd input[name=newPwd]").val();
            var rePwd = $("#change-pwd input[name=rePwd]").val();
            
            if ( newPwd != rePwd ) {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.hems.alert.notMatchPassword'/>");
              return false;
            }
            return true;
          },

          result: function() {
            return this.identifyPwd();
          }
        };

        var change = function() {
          $.post("${ctx}/gadget/prepaymentMgmt/changePwd.do", params,
            function(json) {
              if (json.result == "success") {
                isFirstLogIn = false;
                Ext.Msg.alert("<fmt:message key='aimir.message'/>", 
                "<fmt:message key='aimir.success'/>");
                $("#change-pwd").hide();
                $("#change-pwd input").val(null);
                $("#confirm-pwd").show();  
                $("#loginWrapper input[name=password]").val(params.password);
                $("#menu li.chargeTab").show();
                $("#menu li.historyTab").show();
                if (isManager) {
                  $("#menu li.managerTab").show();
                }
                $("#menu li.chargeTab a").trigger("click");
              }              
            });
        };

        if(validation.result()) {
          if ( isFirstLogIn ) { params.macList = macAddressList; }
          change();
        }
      },
      
      managerPwdConfirm : function() {
          var params = {
                  casherId: $("#loginWrapper input[name=loginId]").val(),
                  pw: $("#managerPwd-confirm input[name=managerPwd]").val() || "",
                  vendorId: vendor,
                  //mac: document.MacAddress.getMacAddress() || ""          
                };
          
          $.post("${ctx}/gadget/prepaymentMgmt/casherLogin.do", params,
                  function(json) {
        	  		$("#managerPwd-confirm input").val(null);
                	var success = "<fmt:message key='aimir.success'/>";
                    if (json.result == success) {
                    	$('#managerPwd-confirm').hide();
                        $('#managerPwd-change').show();
                    } else {
                        Ext.Msg.alert("<fmt:message key='aimir.message'/>", json.result);
                    }
                });
          
          
      },

      contractListTotalExcel: function() {  
        excelType = 1;

        var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
        contactListObj = new Object();
        var header = new Array();
        var param = new Array();

        //title
        header[0] = '<fmt:message key="aimir.contractNumber"/>'; // Contract No.
        header[1] = '<fmt:message key="aimir.customerid"/>'; // Customer ID
        header[2] = '<fmt:message key="aimir.customername"/>'; // Customer Name
        header[3] = '<fmt:message key="aimir.meterid"/>'; //Meter ID
        header[4] = '<fmt:message key="aimir.address"/>'; //Address
        header[5] = '<fmt:message key="aimir.supplystatus"/>'; // Supply Status        
        header[6] = '<fmt:message key="aimir.hems.prepayment.lastchargedate"/>'; //Last Charge Date
        header[7] = '<fmt:message key="aimir.credit"/>'; //Credit
        header[8] = '<fmt:message key="aimir.arrears"/>'; //Arrears
        header[9] = '<fmt:message key="aimir.barcode"/>'; //Barcode
        header[10] = '<fmt:message key="aimir.vendorprepayment.contract.list"/>'; //파일명 : Vendor Prepayment Contract List

        //parameter
        param[0] = $("#barcodeNumber").val();
        param[1] = $("#contractNumber").val();
        param[2] = $("#customerNo").val();
        param[3] = $("#customerName").val(); 
        param[4] = $("#mdsId").val();
        param[5] = supplierId;

        contactListObj.fmtMessage = header;
        contactListObj.condition = param ;

        window.open('${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do', "Contract List Excel", opt);
      },

      depositHistoryExcel: function() {
        excelType = 5;
        var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";

        window.open(
          "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do", "depositChargeExcel", opt);
        
      },

      depositHistoryTotalExcel: function() {
        excelType = 6;
        var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";

        window.open(
          "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do", "depositChargeTotalExcel", opt);
      },

      arrearsExcel: function() {
          excelType = 7;
          var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";

          window.open(
            "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do", "arrearsInfoExcel", opt);
        },

        infoDetailTotalExcel: function() {
            excelType = 8;

            var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
            contactListObj = new Object();
            var header = new Array();
            var param = new Array();

            header[0]='Balance History';
            header[1]='<fmt:message key="aimir.contractNumber"/>';

            //parameter
            param[1] = $("#infoDetailForm input[name=contractNumberInfo]").val();
            param[2] = $("#infoDetailForm input[name=startDate]").val() || '00000000';
            param[3] = $("#infoDetailForm input[name=endDate]").val() || '99999999'; 
            param[4] = supplierId;

            contactListObj.fmtMessage = header;
            contactListObj.condition = param ;

            window.open('${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do', "Balance History Excel", opt);
          },

      addManager: function() {
        //clientMacAddress = document.MacAddress.getMacAddress();

        var params = {
          vendor: vendor,
          casherId: $("#firstTab input[name=id]").val(),
          name: $("#firstTab input[name=name]").val(),
          //macAddress: clientMacAddress || document.MacAddress.getMacAddress(),
          isManager: true,
          lastUpdateDate: $.format.date(new Date(), "yyyyMMddHHmmss")
        };

        addFirstManager = function() {
          $.post("${ctx}/gadget/prepaymentMgmt/addCasher.do",params,
            function(json) {
              if ( json.result == 'success' ) {
                $("#loginWrapper input[name=loginId]").val(params.casherId);
                $("#loginWrapper input[name=password]").val("");
                eventHandler.logIn();
              }
            });
        }

        var validation = {
          // 입력된 폼에 누락된 값이 없는 지 체크 
          checkMissingValue: function() {
            var isValidate = false;
            var id = $("#firstTab input[name=id]").val();
            var name = $("#firstTab input[name=name]").val();

            if ( !id ) {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.alert.input.manager.id'/>",
                function() {
                  if ( !name ) {
                    Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                      "<fmt:message key='aimir.alert.input.manager.name'/>");
                  }
                });
            } else if ( !name ) {
              Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                "<fmt:message key='aimir.alert.input.manager.name'/>");
            } else {
              isValidate = true;
            }
            return isValidate;
          },

          //중복되는 id가 없는지 체크
          validateAndRun : function() {
            $.post("${ctx}/gadget/prepaymentMgmt/isDuplicate.do", params,
              function(json) {
                if( json.result == true ) {
                  Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                    json.error);
                } else {
                  addFirstManager();
                }
              });
          },
        };
        
        if (validation.checkMissingValue()) {
          validation.validateAndRun();    
        }
      },

      initManagerTab: function() {
    	$('#arrearsInfo').hide();
        casherManagerStore.reload();
      },
      
      
      initManagerPwdTab: function() {
    	  $('#arrearsInfo').hide();
    	  isGetAllManager = (vendorRole=='admin') && (isManager == true);
    	  casherManagerPwdParams.allManager = isGetAllManager;
    	  casherManagerPwdParams.vendorId = $.trim($("#managerPwdTab input[name=searchVendorId]").val());
    	  casherManagerPwdParams.casherId = $.trim($("#managerPwdTab input[name=searchCashierId]").val());
    	  casherManagerPwdStore.reload();
      },

      initPasswordTab: function() {
    	$('#arrearsInfo').hide();
        if ( isFirstLogIn ) {
          $("#confirm-pwd").hide();
          $("#confirm-pwd input").val(null);
          $("#change-pwd").show();
        }
      },

      cancel: function(rec) {
        var params = {
          id: rec.prepaymentLogId,
          vendor: vendor,
          reason: null
        };

		  var charLenText = 'character : ';
          var refund =  new Ext.Window({
		    title: params.title,
		    modal: true, closable:true, resizable: true,
		    width:300, height:250,
		    border:true, plain:false,                      
		    items:[{
		        xtype: 'panel',
		        frame: false, border: false,
		        items:{
		          id: 'cancel_form',
		          xtype: 'form',
		          bodyStyle:'padding:10px',
		          layout:'fit',
		          width:300,
		          height:250,
		          frame: false, border: false,
		          items: [
		            {xtype: 'label', html:'<div style="text-align:left;">' + "<fmt:message key='aimir.cancel.reason'/>" +'</div>',  anchor: '100%'}, 
		            {xtype: 'textarea', 
		            	id: 'cancelReason_id', name: 'cancelReason_name', 
		            anchor: '100', height:'80', width:'250', 
		            enableKeyEvents:true, 
		            listeners:{
		            	keyup:function(t,s){
		            		var textLen=Ext.getCmp('cancelReason_id').getValue().length;
		            		Ext.getCmp('cancelReason_labelId').setText(charLenText + textLen)
		            	}
		            }},
		            {xtype: 'label', id:'cancelReason_labelId', text:charLenText,  anchor: '100%'},]
		        }
		    }],
		    
		    buttons: [{
		      text: 'Ok',
		      handler: function() { 
		        var cancelText = Ext.getCmp('cancelReason_id').getValue();
		        if(cancelText.length > 255) {
		        	Ext.Msg.alert("<fmt:message key='aimir.warning'/>","<fmt:message key='aimir.limited.char255'/>");
		        	return;
		        } else {
		        	params.reason = cancelText;
                	
                    $.post("${ctx}/gadget/prepaymentMgmt/cancelWithDebt.do", params, function(json) {
                        if (json.result == 'success') {
                          historyListStore.reload();
                          contractListStore.reload();
                          Ext.Msg.alert("<fmt:message key='aimir.message'/>", 
                            "<fmt:message key='aimir.success'/>");
                        } else if (json.result == 'cancelData') {
                            historyListStore.reload();
                            contractListStore.reload();
                         	Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                         	"<fmt:message key='aimir.already.cancelData'/>");
                        } else {
                          Ext.Msg.alert("<fmt:message key='aimir.error'/>", 
                            "<fmt:message key='aimir.msg.fail.update'/>");
                        }
                      }); 
		        }
		        refund.close();
		      }
		    }, {
		      text: '<fmt:message key="aimir.cancel"/>',
		      handler: function() {
		    	  refund.close();
		      }
		    }]
  });

        Ext.Msg.confirm("<fmt:message key='aimir.message'/>", 
          "<fmt:message key='aimir.cancel'/>" + "?", 
          function(btn) {
            if (btn == "yes") refund.show(this);
          });
      },

      tagExcelButton: function() {
        var val = $("#depositHistory select[name=reportType]").val();
        if (val == "all") {
          $("#depositHistoryExcel").hide();
          $("#depositHistoryTotalExcel").hide();
        } else {
          $("#depositHistoryExcel").show();
          $("#depositHistoryTotalExcel").show();
        }
      }
    };

    var bind = function() {
      $("#contractForm span#contractSearch").click(eventHandler.contractListSearch);
      $("#contractForm span#contractListTotalExcel").click(eventHandler.contractListTotalExcel);
      $('#historyForm span#historySearch').click(eventHandler.historyListSearch);
      $("#infoForm span#contractInfoSearch").click(eventHandler.contractInfoSearch);
      $("#infoDetailForm span#infoDetailSearch").click(eventHandler.infoDetailSearch);
      $("#infoDetailForm span#infoDetailTotalExcel").click(eventHandler.infoDetailTotalExcel);
      
      $('#depositHistory span#depositHistorySearch').click(eventHandler.depositHistoryListSearch);
      $('#depositHistory span#depositHistoryExcel').click(eventHandler.depositHistoryExcel);
      $('#depositHistory span#depositHistoryTotalExcel').click(eventHandler.depositHistoryTotalExcel);
      $('#arrearsExcel').click(eventHandler.arrearsExcel);
      $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
      $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
      $("#depositHistory select[name=reportType]").bind('change', eventHandler.tagExcelButton);

      contractListSm.addListener('rowselect', eventHandler.selectedHistorySearch);
      contractInfoSm.addListener('rowselect', eventHandler.selectedBalanceHistorySearch);
      historyListSm.addListener('rowselect', eventHandler.selectionDebtLog);
      $('a[href=#chargeTab]').bind('click', eventHandler.initChargeTab);
      $('a[href=#historyTab]').bind('click', eventHandler.initHistoryTab);
      $('a[href=#infoTab]').bind('click', eventHandler.initInfoTab);
      $('a[href=#managerTab]').bind('click', eventHandler.initManagerTab);
      $('a[href=#managerPwdTab]').bind('click', eventHandler.initManagerPwdTab);
      $('a[href=#passwordTab]').bind('click', eventHandler.initPasswordTab);
      $('#barcodeNumber').bind('change', eventHandler.selectBarcode);
      $("#loginWrapper span#logIn").click(eventHandler.logIn);
      $("#loginWrapper input[name=password]").bind('keyup', function(event) {
          var evCode = (window.netscape) ? event.which : event.keyCode;

          if (evCode == 13) {
              eventHandler.logIn();
          }
      });
      $("#managerTab span#addCasher").click(eventHandler.addCasher);
      $("#managerPwdTab span#searchManager").click(eventHandler.initManagerPwdTab);
      $("#passwordTab span#confirmPwd").click(eventHandler.confirmPwd);
      $("#managerPwdTab span#managerPwdConfirm").click(eventHandler.managerPwdConfirm);
      $("#passwordTab span#changePwd").click(eventHandler.changePwd);
      $("#firstTab span#addManager").click(eventHandler.addManager);
    };

    var renderGrid = function() {
      contractGrid = new Ext.grid.EditorGridPanel(contractListProp);
      historyGrid = new Ext.grid.EditorGridPanel(historyListProp);
      contractListGrid = new Ext.grid.EditorGridPanel(contractInfoProp);
      balanceHistoryGrid = new Ext.grid.EditorGridPanel(balanceHistoryListProp);
      debtHistoryGrid = new Ext.grid.EditorGridPanel(debtHistoryListProp);
      vendorHistoryGrid = new Ext.grid.EditorGridPanel(vendorHistoryProp);      
      casherManagerGrid = new Ext.grid.EditorGridPanel(casherManagerProp);
      casherManagerPwdGrid = new Ext.grid.EditorGridPanel(casherManagerPwdProp);

      contractListStore.load({params: contractListParams});
      historyListStore.load({params: historyListParams});
      contractInfoStore.load({params: contractInfoParams});
      balanceHistoryListStore.load({params: balanceHistoryListParams});
      //vendorHistoryStore.load({params: vendorHistoryParams});
      eventHandler.depositHistoryListSearch();
      casherManagerStore.load({params: casherManagerParams});
      casherManagerPwdStore.load({params: casherManagerPwdParams});
    };

    var initCalendar = function() {
      var startDate = new Date();
      startDate.setMonth(startDate.getMonth() - 1);
      var preStartDate = new Date();
      preStartDate.getDate(preStartDate.getDate() - 1);
      var endDate = new Date();
      var startProp = $.extend(true, calendarProp);
      var endProp = $.extend(true, calendarProp);
      var deStartProp = $.extend(true, calendarProp);
      var deEndProp = $.extend(true, calendarProp);

      $('#historyForm input[name=startDate]').datepicker(startProp);
      $('#historyForm input[name=endDate]').datepicker(endProp);
      $('#infoDetailForm input[name=startDate]').datepicker(startProp);
      $('#infoDetailForm input[name=endDate]').datepicker(endProp);
      $('#depositHistory input[name=startDate]').datepicker(deStartProp);
      $('#depositHistory input[name=endDate]').datepicker(deEndProp);
      $('#arrearsInfo input[name=startDate]').datepicker(deStartProp);
      $('#arrearsInfo input[name=endDate]').datepicker(deEndProp);
      
      $('#historyForm input[name=startDate]').datepicker('setDate', startDate);
      $('#historyForm input[name=endDate]').datepicker('setDate', endDate);
      $('#infoDetailForm input[name=startDate]').datepicker('setDate', preStartDate);
      $('#infoDetailForm input[name=endDate]').datepicker('setDate', endDate);
      $('#depositHistory input[name=startDate]').datepicker('setDate', startDate);
      $('#depositHistory input[name=endDate]').datepicker('setDate', endDate);
      $('#arrearsInfo input[name=startDate]').datepicker('setDate', startDate);
      $('#arrearsInfo input[name=endDate]').datepicker('setDate', endDate);

      var initDateFormat = function(inst ,date) {
        var dbDate = $.datepicker.formatDate('yymmdd', date);
        $.getJSON("${ctx}/common/convertLocalDate.do", 
          {supplierId: supplierId, dbDate: dbDate},
          function(data) {            
            $(inst).siblings("." + inst.attr('name')).val(data.localDate);
          });
      };

      initDateFormat($('input[name=startDate]'), startDate);
      initDateFormat($('input[name=endDate]'), endDate);
      
      initDateFormat($('#infoDetailForm input[name=startDate]'), preStartDate);
      initDateFormat($('#infoDetailForm input[name=endDate]'), endDate);
    };
    
    // 로그인 이전 화면 설정
    var initSettings = function() {
      $("#menu li.firstTab").hide();
      if (isFirst) {
        $("#loginWrapper").hide();
        $("#menu li.firstTab a").trigger('click');
        $("#menu li").hide();
      } else {
        $("#menu").hide();  
      }
    };

    // vendor(Operator)가 3rd party인 경우(role = 'vendor')에만 예치금이 보인다
    var initDepositSettings = function() {
      if ( isVendor ) {
        $(".current-deposit").removeClass("hidden");
        $(".current-deposit").show();
      }
    }
    
    var init = function() {
      Ext.QuickTips.init();
      $("#report-type").selectbox();
      $("#sub-type").selectbox();
      $("#vendor").selectbox();
      initCalendar();
      renderGrid();
      $("#menu").tabs();
      locationTreeGoGo('treeDiv2', 'searchWord', 'locationId');
      initDepositSettings();
      bind();    
      eventHandler.refreshDeposit();
      eventHandler.tagExcelButton(); 
      initSettings();
      hide();
    };

    Ext.onReady(function() {
	  Ext.Ajax.timeout = 60000; //Ext 화면 표시시 타임아웃을 defaults(30초) -> 1분(60초)으로 변경
      init();
    });

    // window resize event
    $(window).resize(function() {
      contractGrid.getView().refresh();
      historyGrid.getView().refresh();
      contractListGrid.getView().refresh();
      balanceHistoryGrid.getView().refresh();
      vendorHistoryGrid.getView().refresh();
      casherManagerGrid.getView().refresh();
    });

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "") {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
    
    function addTooltip2(value, metadata) {
    	if (value != null && value != "" && metadata != null) {
            //metadata.attr = 'ext:qtip="' + value + '"';
            metadata.attr = 'title="' + value + '"';
        }
        return value;
    }

    /*]]>*/
    </script>
</body>
</html>