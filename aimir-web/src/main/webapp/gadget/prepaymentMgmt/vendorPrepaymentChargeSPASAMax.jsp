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
            width: 60px;
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

        .hidden {
            display: none;
        }
        .no-width {
            width: 0px;
            visibility: hidden;
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
<%--     <applet name="MacAddress" code="NetIfApplet.class"
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
            <li class="managerTab">
                <a href="#managerTab">
                    <fmt:message key='aimir.prepayment.casher'/> <fmt:message key="aimir.manager"/>
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
        </ul>

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
                        <input class="alt endDate" name="startDateDisplay"  type='text' readOnly/>
                        <input name="endDate" class="no-width" type="text"/>
                    </span>
                    <span id="historySearch" class="am_button margin-l10 margin-t1px">
                        <a class="on"><fmt:message key="aimir.button.search" /></a>
                    </span>
                </div>
            </form>
            <!--검색조건 끝-->

            <div id="prepaymentChargeHistoryDiv"></div>
        </div>

        <div id="historyTab">
            <!--검색조건-->
            <form id="depositHistory" class="searchoption-container">
                <div>
                    <input name="vendor" type="text" class="hidden"/>
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
                    <span>
                        <label><fmt:message key="aimir.prepayment.casher"/></labe>
                        <input name="casherId" type="text"/></input>
                    </span>
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

            <div id="depositChargeHistoryDiv"></div>
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

        //기존에 열려있는 popup window
        var receiptPopupWindow;

        //Manager인지 여부 (Casher 고용권한)
        var isManager;

        // vendor의 RoleName
        var vendorRole = "${role}";

        // 보고서 출력 양식으로 전체 출력인지 total만 출력하는지 결정하는 flag
        var onlyTotal;

        var casherStatus = {
            "0" : "work", // 현재까지 시스템에서 사용 중인 캐셔
            "1" : "quit"  // 현재 시스템에서 삭제된 캐셔
        };
        
        var logoImg = "${logoImg}"; // 로고 이미지 파일

        // 수정 권한
        var editAuth = "${editAuth}";
        var hiddenCol = (editAuth != "true") ? true : false;    // 권한에 따라 show/hide
        var editCol = (editAuth != "true") ? false : true;      // 권한에 따라 edit
                                                                // enable/disable

        var isHiddenCancelBtn = (vendorRole == 'admin') ? false : true;

        var contractGrid;
        var historyGrid;
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

        var vendorHistoryParams = $.extend(true, {vendor: vendor,
            vendorRole: vendorRole,
            supplierId: supplierId,
            startDate:$("#depositHistory input[name=startDate]").val(),
            endDate:$("#depositHistory input[name=endDate]").val()}, storeParams);
        vendorHistoryParams.limit = VENDOR_HISTORY_PAGE_SIZE;

        var casherManagerParams = $.extend(true, {vendorId: vendor,
            supplierId: supplierId}, storeParams);
        casherManagerParams.limit = VENDOR_HISTORY_PAGE_SIZE;

        var contractListStore = new Ext.data.JsonStore({
            baseParams: contractListParams,
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

        var historyListStore = new Ext.data.JsonStore({
            baseParams: historyListParams,
            url: "${ctx}/gadget/prepaymentMgmt/getChargeHistoryList.do",
            totalProperty: 'totalCount',
            root: 'result',
            fields: ["lastTokenDate", "chargedCredit", "chargedArrears", "balance", "arrears",
                "lastTokenId", "authCode", "municipalityCode"],
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
            url: "${ctx}/gadget/prepaymentMgmt/historyList.do",
            totalProperty: 'count',
            root: 'list',
            fields: ['vendor', 'casher','contractNo', 'customerId', 'meter',
                'customerName', 'address', 'changeDate', 'chargeCredit', 'chargeDeposit', 'deposit'],
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

        var casherStatusArea = function(value) {
            return casherStatus[value];
        };

        var contractListModel = new Ext.grid.ColumnModel({
            columns: [
                {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
               ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo'}
               ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName'}
               ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId'}
               ,{header: "<fmt:message key='aimir.address'/>", dataIndex: 'address'}
               ,{header: "<fmt:message key='aimir.supplystatus'/>", dataIndex: 'statusName'}
               ,{header: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>", dataIndex: 'lastTokenDate', align: 'center',
                   tooltip: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>"}
               ,{header: "<fmt:message key='aimir.credit'/>", dataIndex: 'currentCredit',  align: 'right'}
               ,{header: "<fmt:message key='aimir.arrears'/>", dataIndex: 'currentArrears', align: 'right'}
               ,{header: "<fmt:message key='aimir.amount.paid'/>", align: 'right', dataIndex: 'chargeAmount',
                 renderer: Ext.util.Format.numberRenderer("0,000.0000"),
                 editor: new Ext.form.NumberField({
                     id: 'chPrice',
                     allowBlank: true,
                     allowNegative: false
                 })
                }
               ,{header: "<fmt:message key='aimir.barcode'/>", align: 'left', dataIndex: 'barcode',
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
                {header: "<fmt:message key='aimir.hems.prepayment.chargedate'/>"},
                {header: "<fmt:message key='aimir.chargeAmount'/>", align: 'right'},
                {header: "<fmt:message key='aimir.prepayment.chargearrears'/>",
                  align: 'right'},
                {header: "<fmt:message key='aimir.credit'/>",
                  align: 'right'},
                {header: "<fmt:message key='aimir.arrears'/>",
                  align: 'right'},
                {header: "<fmt:message key='aimir.hems.prepayment.transactionNum'/>"},
                {header: "<fmt:message key='aimir.prepayment.authCode'/>"},
                {header: "<fmt:message key='aimir.prepayment.municipalityCode'/>"},
                {header: "", renderer: receiptBtnArea},
                {header: "", renderer: cancelBtnArea, hidden: isHiddenCancelBtn},
                {header: "", renderer: relayOnBtnArea, hidden: true}
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
                {header: "", renderer: deleteCasherBtnArea}
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
        var casherManagerSM = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));

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

        var eventHandler = {
            initDateFormat: function(inst) {
                $.getJSON("${ctx}/common/convertLocalDate.do",
                  {supplierId: supplierId, dbDate: inst.val()},
                  function(data) {
                      $("." + inst.attr('name')).val(data.localDate);
                  });
            },

            selectedHistorySearch: function(sm, rowIndex, rec) {
                historyListModel.setHidden(10, true);
                var contractNumber = rec.json.contractNumber;
                var params = $.extend(true, {}, historyListParams, {
                    contractNumber: contractNumber
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
                    searchEndMonth:
                        $("#historyForm input[name=endDate]").val() || '99999999'
                });

                historyListStore.baseParams = params;
                historyListStore.load();
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

            depositHistoryListSearch: function() {
                var params = $.extend(true, {}, vendorHistoryParams, {
                    reportType: $("#depositHistory select[name=reportType]").val(),
                    subType : $("#depositHistory select[name=subType]").val(),
                    contract: $("#depositHistory input[name=contract]").val(),
                    customerName: $("#depositHistory input[name=customerName]").val(),
                    customerNo: $("#depositHistory input[name=customerId]").val(),
                    meterId: $("#depositHistory input[name=meterId]").val(),
                    casherId: $("#depositHistory input[name=casherId]").val(),
                    startDate: $("#depositHistory input[name=startDate]").val(),
                    endDate: $("#depositHistory input[name=endDate]").val()
                });
                vendorHistoryStore.baseParams = params;
                vendorHistoryStore.load();
            },

            saveChargeAmount: function(rec) {

                // 충전가능여부 체크
                var params = {
                    contractNumber : rec.get("contractNumber")
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

                var chargeAmount = Number(rec.get("chargeAmount"));
                var chargeArrears = Number(rec.get("chargeArrears"));
                var currentArrears = NumberUtil.parseNumber(rec.get("currentArrears"));
                var currentAmount = NumberUtil.parseNumber(rec.get("currentCredit"));
                var contractPrice = Number(rec.get("contractPrice"));// init Credit

                var params = {
                    contractNumber: rec.json.contractNumber,
                    casherId: $("#loginWrapper input[name=loginId]").val(),
                    contractId: rec.json.contractId,
                    mdsId: rec.json.mdsId,
                    lastTokenId: rec.json.lastTokenId,
                    contractDemand: rec.json.contractDemand || 0,
                    tariffCode: rec.json.tariffCode,
                    amount: chargeAmount || 0,
                    arrears: chargeArrears || 0,
                    contractPrice: contractPrice || 0, //initCredit
                    currentAmount: currentAmount,
                    currentArrears: currentArrears,
                    customerName: rec.json.customerName || "",
                    supplierId : supplierId,
                    authCode : ""
                };

                var saveAction = function() {
                    Ext.Msg.confirm("<fmt:message key='aimir.message'/>",
                        "<fmt:message key='aimir.wouldSave'/>", function(btn) {
                        if (btn == "yes") {
                            emergePre();

                            $.post("${ctx}/gadget/prepaymentMgmt/vendorSavePrepaymentChargeSPASA.do",
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

                                            if (json.isCutOff == true && json.credit > 0) {
                                                eventHandler.relayOn(rec.json, function() {
                                                    receiptParam.prepaymentLogId = json.prepaymentLogId;
                                                    receiptParam.contractId = rec.json.contractId;
                                                    eventHandler.chargeAfterReceipt(receiptParam);
                                                });
                                            } else {
                                                receiptParam.prepaymentLogId = json.prepaymentLogId;
                                                receiptParam.contractId = rec.json.contractId;
                                                eventHandler.chargeAfterReceipt(receiptParam);
                                            }
                                        });

                                    } else {
                                        Ext.Msg.alert("<fmt:message key='aimir.error'/>", json.result);
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
                var currentArrears = params.currentArrears;
                //var currentAmount = params.currentAmount;
                var chargeAmount = params.amount;
                var chargeArrears = params.arrears;
                //var contractPrice = params.contractPrice;
                var customerName = params.customerName;

                // validation check
                // 지불 미수금이 잔여 미수금 보다 큰 경우 X
                if (!isNaN(chargeArrears) && !isNaN(currentArrears)
                        && chargeArrears > currentArrears) {
                    Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.arrears'/>");
                    return;
                }

                // 지불 미수금과 지불 creit의 값이 숫자가 아닌 경우
                if (isNaN(chargeAmount) && isNaN(chargeArrears)) {
                    return;
                }

                if (chargeAmount) {

                    eventHandler.retypePaidAmount({
                        title : customerName,
                        msg : "<fmt:message key='aimir.retype.amount'/>",
                        amount : chargeAmount,
                        callback : function() {
                            var typeAuthCode = function(collback) {
                                Ext.Msg.show({
                                    title : customerName,
                                    msg : "<fmt:message key='aimir.msg.prepayment.inputauthcode'/>",
                                    buttons : Ext.MessageBox.OKCANCEL,
                                    prompt : true,
                                    fn : function(btn, text) {
                                        if (btn == "ok") {
                                            if (text != "") {
                                                params.authCode = text;

                                                eventHandler.retypeAuthCode({
                                                    title : customerName,
                                                    amount : text,
                                                    msg : "<fmt:message key='aimir.msg.prepayment.retypeauthcode'/>",
                                                    callback : function() {
                                                        collback();
                                                    }
                                                });
                                            } else {
                                                Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.notspace'/>");
                                            }
                                        }
                                    }
                                });
                            };

                            if (currentArrears > 0) {
                                Ext.Msg.show({
                                    title : customerName,
                                    msg : "<fmt:message key='aimir.msg.pay.amount.arrears'/>",
                                    buttons : Ext.MessageBox.OKCANCEL,
                                    prompt : true,
                                    fn : function(btn, text) {
                                        var arrearsAmount = Number(text);

                                        var cb = function(arrearsAmount, callback) {
                                            if (btn == 'ok'
                                                    && !isNaN(arrearsAmount)
                                                    && (params.amount >= arrearsAmount)) {

                                                // 입력한 미수금 금액이 남은 미수금 금액보다 큰경우
                                                // 미수금 금액은 남은 미수금 금액이 된다.
                                                if (currentArrears < arrearsAmount) {
                                                    arrearsAmount = currentArrears;
                                                }
                                                params.amount -= arrearsAmount;
                                                params.arrears = arrearsAmount;
                                                typeAuthCode(callback);
                                            } else {
                                                Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.error'/>");
                                            }
                                        };

                                        eventHandler.retypePaidAmount({
                                            title : customerName,
                                            amount : arrearsAmount,
                                            msg : "<fmt:message key='aimir.retype.arrears'/>",
                                            callback : function() {
                                                cb(arrearsAmount, callback);
                                            }
                                        });
                                    }
                                });
                            } else {
                                typeAuthCode(function(){callback();});
                            }
                        }
                    });
                }
            },

            retypePaidAmount: function (params) {
                Ext.Msg.show({
                    title : params.title,
                    msg : params.msg,
                    buttons : Ext.MessageBox.OKCANCEL,
                    prompt : true,
                    fn : function(btn, text) {
                        var prompt = Number(text);
                        if (btn == 'ok' && !isNaN(params.amount)) {
                            if (prompt != params.amount) {
                                Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.value'/>");
                                return;
                            } else {
                                params.callback();
                            }
                        }
                    }
                });
            },

            retypeAuthCode: function (params) {
                Ext.Msg.show({
                    title : params.title,
                    msg : params.msg,
                    buttons : Ext.MessageBox.OKCANCEL,
                    prompt : true,
                    fn : function(btn, text) {
                        if (btn == 'ok') {
                            if (text != params.amount) {
                                Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.value'/>");
                                return;
                            } else {
                                params.callback();
                            }
                        }
                    }
                });
            },

            relayOn : function (rec, callback) {
                var params = {
                    mcuId : rec.mcuId,
                    target : rec.meterId,
                    loginId : 'admin'
                };

                emergePre();
                $.post("${ctx}/gadget/device/command/cmdRemotePowerOn.do",
                    params,
                    function(json) {
                    Ext.Msg.alert("<fmt:message key='aimir.info'/>", json.status);
                    if (json.status != 'SUCCESS') {
                        Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.error'/>");
                        historyListModel.setHidden(10, false);
                    }
                    hide();
                    if (callback) {
                        callback();
                    }
                });
            },

            refreshDeposit : function (value) {
                if (value || value == 0) {
                    var value = NumberUtil.thousandSeparator(Number(value));
                    $("label.current_deposit").text(value);
                } else {
                    var value = NumberUtil.thousandSeparator(Number(deposit));
                    $("label.current_deposit").text(value);
                }
            },

            chargeAfterReceipt : function(rec) {
                Ext.MessageBox.confirm(
                  "<fmt:message key='aimir.button.confirm'/>",
                  "<fmt:message key='aimir.msg.confirm.print.receipt'/>",
                  function(result) {
                    if (result === "yes") {
                        eventHandler.openReceiptPopup(rec);
                    }
                });
            },

            openReceiptPopup: function(rec) {
                var url = "${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptPopup.do";
                var opt = "width=350px, height=615px, resizable=no, status=no";

                var params = {
                    vendor: vendor,
                    supplierId: supplierId,
                    contractId: rec.contractId || rec.json.contractId,
                    prepaymentLogId: rec.prepaymentLogId || rec.json.prepaymentLogId
                }

                if (receiptPopupWindow) {
                    receiptPopupWindow.close();
                }

                var queryString = CommonUtil.getQueryString(params);
                receiptPopupWindow = window.open(url + queryString, "receiptPopupWindow", opt);
            },

            initChargeTab: function() {
                eventHandler.refreshDeposit();
                contractListStore.reload();
                historyListStore.reload();
            },

            initHistoryTab: function() {
                eventHandler.depositHistoryListSearch();
            },

            updateContractPrice: function(field, newVal, oldVal) {
                var params = {
                    contractNumber : field.gridEditor.record.data.contractNumber,
                    contractPrice : newVal
                };
                $.post("${ctx}/gadget/prepaymentMgmt/vendorSetContractPrice.do",
                   params, function(data) {
                    if (data.result == 'success') {
                        Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.success'/>");
                    } else {
                        Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.erroroccured'/>");
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
                        if (data.result == 'success') {
                            Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.success'/>");
                        } else {
                            Ext.Msg.alert("<fmt:message key='aimir.error'/>", data.result);
                        }
                        contractListStore.reload();
                    });
                }

                Ext.Msg.confirm(field.gridEditor.record.json.customerName,
                    "<fmt:message key='aimir.msg.update.barcode'/>", function(btn) {
                    if (btn == 'yes') {
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
                    //clientMacAddress = document.MacAddress.getMacAddress();
                    //macAddressList = document.MacAddress.getMacAddressList();

                    $("#menu").show();
                    $("#loginWrapper").hide();
                    eventHandler.initChargeTab();

                    if (isFirstLogIn) {
                        $("#menu li.passwordTab").show();
                        $("#confirm-pwd").hide();
                        $("#confirm-pwd input").val(null);
                        $("#change-pwd").show();
                        $("#menu li.passwordTab a").trigger('click');
                        $("#menu li.chargeTab").hide();
                        $("#menu li.historyTab").hide();
                        $("#menu li.managerTab").hide();
                    }

                    if (!isManager) {
                        $("#menu li.managerTab").hide();
                    }
                };

                $.post("${ctx}/gadget/prepaymentMgmt/casherLogin.do", params,
                  function(json) {
                	var success = "<fmt:message key='aimir.success'/>";
                    if (json.result == success) {
                        isManager = json.isManager;
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
                    casherId: $("#managerTab input[name=casherId]").val(),
                    name: $("#managerTab input[name=name]").val(),
                    isManager: $("#managerTab input[name=isManager]").is(":checked") || false,
                    lastUpdateDate: $.format.date(new Date(), "yyyyMMddHHmmss")
                };

                var addCasher = function() {
                  $.post("${ctx}/gadget/prepaymentMgmt/addCasher.do", params,
                    function(json) {
                        Ext.Msg.alert("<fmt:message key='aimir.message'/>", json.result);
                        if (json.result == 'success') {
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
                        var id = $("#managerTab input[name=casherId]").val();
                        var name = $("#managerTab input[name=name]").val();

                        if (!id) {
                            Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                              "<fmt:message key='aimir.alert.input.casher.id'/>",
                              function() {
                                if (!name) {
                                    Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.alert.input.casher.name'/>");
                                }
                            });
                        } else if (!name) {
                            Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.alert.input.casher.name'/>");
                        } else {
                            isValidate = true;
                        }
                        return isValidate;
                    },

                    // id중복 체크
                    validateAndRun : function() {
                        $.post("${ctx}/gadget/prepaymentMgmt/isDuplicate.do", params,
                          function(json) {
                            if(json.result == true) {
                                Ext.Msg.alert("<fmt:message key='aimir.error'/>", json.error);
                            } else {
                                addCasher();
                            }
                        });
                    },
                };

                if (validation.checkMissingValue()) {
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
                        if (json.result == 'success') {
                            casherManagerStore.reload();
                        }
                    });
                };

                deleteCasher();
            },

            confirmPwd: function() {
                var prevPwd = $("#loginWrapper input[name=password]").val();
                var currPwd = $("#passwordTab input[name=prevPwd]").val();

                if (prevPwd == currPwd) {
                    $("#confirm-pwd").hide();
                    $("#confirm-pwd input").val(null);
                    $("#change-pwd").show();
                } else {
                    Ext.Msg.alert("<fmt:message key='aimir.error'/>", "<fmt:message key='aimir.msg.confirmpassword'/>");
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

            depositHistoryExcel: function() {
            	excelType = 2;
                onlyTotal = false;
                var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";

                window.open("${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do", "depositChargeSPASAExcel", opt);
            },

            depositHistoryTotalExcel: function() {
            	excelType = 3;
                onlyTotal = true;
                var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";

                window.open("${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do", "depositChargeSPASATotalExcel", opt);
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
                        if (json.result == 'success') {
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

                        if (!id) {
                            Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                              "<fmt:message key='aimir.alert.input.manager.id'/>",
                              function() {
                                if (!name) {
                                    Ext.Msg.alert("<fmt:message key='aimir.error'/>",
                                      "<fmt:message key='aimir.alert.input.manager.name'/>");
                                }
                            });
                        } else if (!name) {
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
                                Ext.Msg.alert("<fmt:message key='aimir.error'/>", json.error);
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
                casherManagerStore.reload();
            },

            initPasswordTab: function() {
                if (isFirstLogIn) {
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
                    	
                        $.post("${ctx}/gadget/prepaymentMgmt/cancel.do", params, function(json) {
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
    		      text: 'Cancel',
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
            $('#historyForm span#historySearch').click(eventHandler.historyListSearch);
            $('#depositHistory span#depositHistorySearch').click(eventHandler.depositHistoryListSearch);
            $('#depositHistory span#depositHistoryExcel').click(eventHandler.depositHistoryExcel);
            $('#depositHistory span#depositHistoryTotalExcel').click(
              eventHandler.depositHistoryTotalExcel);
            $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            $("#depositHistory select[name=reportType]").bind('change', eventHandler.tagExcelButton);

            contractListSm.addListener('rowselect', eventHandler.selectedHistorySearch);
            $('a[href=#chargeTab]').bind('click', eventHandler.initChargeTab);
            $('a[href=#historyTab]').bind('click', eventHandler.initHistoryTab);
            $('a[href=#managerTab]').bind('click', eventHandler.initManagerTab);
            $('a[href=#passwordTab]').bind('click', eventHandler.initPasswordTab);
            $('#barcodeNumber').bind('change', eventHandler.selectBarcode);
            $("#loginWrapper span#logIn").click(eventHandler.logIn);
            $("#managerTab span#addCasher").click(eventHandler.addCasher);
            $("#passwordTab span#confirmPwd").click(eventHandler.confirmPwd);
            $("#passwordTab span#changePwd").click(eventHandler.changePwd);
            $("#firstTab span#addManager").click(eventHandler.addManager);
        };

        var renderGrid = function() {
            contractGrid = new Ext.grid.EditorGridPanel(contractListProp);
            historyGrid = new Ext.grid.EditorGridPanel(historyListProp);
            vendorHistoryGrid = new Ext.grid.EditorGridPanel(vendorHistoryProp);
            casherManagerGrid = new Ext.grid.EditorGridPanel(casherManagerProp);

            contractListStore.load({params: contractListParams});
            historyListStore.load({params: historyListParams});
            vendorHistoryStore.load({params: vendorHistoryParams});
            casherManagerStore.load({params: casherManagerParams});
        };

        var initCalendar = function() {
            var startDate = new Date();
            startDate.setMonth(startDate.getMonth() - 1);
            var endDate = new Date();
            var startProp = $.extend(true, calendarProp);
            var endProp = $.extend(true, calendarProp);
            var deStartProp = $.extend(true, calendarProp);
            var deEndProp = $.extend(true, calendarProp);

            $('#historyForm input[name=startDate]').datepicker(startProp);
            $('#historyForm input[name=endDate]').datepicker(endProp);
            $('#depositHistory input[name=startDate]').datepicker(deStartProp);
            $('#depositHistory input[name=endDate]').datepicker(deEndProp);
            $('#historyForm input[name=startDate]').datepicker('setDate', startDate);
            $('#historyForm input[name=endDate]').datepicker('setDate', endDate);
            $('#depositHistory input[name=startDate]').datepicker('setDate', startDate);
            $('#depositHistory input[name=endDate]').datepicker('setDate', endDate);

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
            if (isVendor) {
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
            initDepositSettings();
            bind();
            eventHandler.refreshDeposit();
            eventHandler.tagExcelButton();
            initSettings();
            hide();
        };

        $(document).ready(function() {
            init();
        });

        // window resize event
        $(window).resize(function() {
          contractGrid.getView().refresh();
          historyGrid.getView().refresh();
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

        /*]]>*/
    </script>
</body>
</html>