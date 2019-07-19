<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .save {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/save.gif) !important;
        }
        /* grid 내 button 높이 조절 */
        /* td button.x-btn-text {
            height: 11px !important;
        } */
        .x-grid3-cell-inner .x-btn-text {
            height: 11px !important;
        } 
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //공급사ID
        var supplierId = "${supplierId}";

        var chromeColAdd = 0;
        // Chrome 최신버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.onReady(function() {
            var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

            if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
                Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
                Ext.override(Ext.grid.ColumnModel, {
                    getTotalWidth : function(includeHidden) {
                        if (!this.totalWidth) {
                            var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                            this.totalWidth = 0;
                            for (var i = 0, len = this.config.length; i < len; i++) {
                                if (includeHidden || !this.isHidden(i)) {
                                    this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                                }
                            }
                        }
                        return this.totalWidth;
                    }
                });
                chromeColAdd = 2;
            }
        });

        $(document).ready(function(){
            Ext.QuickTips.init();
            searchDivInit();
            window.setTimeout(function(){getPrepaymentChargeList();getChargeHistoryList();}, 500);
        });

        function getFmtMessage() {
            var fmtMessage = new Array();
            var idx = 0;
            fmtMessage[idx++] = "<fmt:message key="aimir.contractNumber"/>";                    // 0. Contract Number
            fmtMessage[idx++] = "<fmt:message key="aimir.customerid"/>";                        // 1. Customer No
            fmtMessage[idx++] = "<fmt:message key="aimir.customername"/>";                      // 2. Customer Name
            fmtMessage[idx++] = "<fmt:message key="aimir.meterid"/>";                           // 3. Meter ID
            fmtMessage[idx++] = "<fmt:message key="aimir.address"/>";                           // 4. 주소
            fmtMessage[idx++] = "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>";    // 5. 마지막 충전일
            fmtMessage[idx++] = "<fmt:message key="aimir.hems.prepayment.currentbalance"/>";    // 6. 현재잔액
            fmtMessage[idx++] = "<fmt:message key="aimir.prepayment.chargeamount"/>";           // 7. 충전 금액 

            return fmtMessage;
        }

        function getHistoryFmtMessage() {
            var fmtMessage = new Array();
            var idx = 0;
            fmtMessage[idx++] = "<fmt:message key="aimir.hems.prepayment.chargedate"/>";                    // 0. 충전일자
            fmtMessage[idx++] = "<fmt:message key="aimir.chargeAmount"/>(<fmt:message key='aimir.price.unit'/>)";                           // 1. 충전금액(원)
            fmtMessage[idx++] = "<fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key='aimir.price.unit'/>)";    // 2. 충전 후 잔액(원)
            fmtMessage[idx++] = "<fmt:message key="aimir.hems.prepayment.transactionNum"/>";                           // 3. 거래 번호
            fmtMessage[idx++] = "<fmt:message key="aimir.prepayment.authCode"/>";                           // 4. Authorization Code
            fmtMessage[idx++] = "<fmt:message key="aimir.prepayment.municipalityCode"/>";    // 5. Municipality Code
            return fmtMessage;
        }

        function send(){
            if (chargeHistoryStore != null) {
                chargeHistoryStore.removeAll();
            }
            getPrepaymentChargeList();
        }

        /* Prepayment Charge 리스트 */
        var prepaymentChargeStore;
        var prepaymentChargeGridOn = false;
        var prepaymentChargeGrid;
        var prepaymentChargeColModel;
        var getPrepaymentChargeList = function() {
        	var width = $("#prepaymentChargeDiv").width();
            var pageSize = 15;
            var fmtMessage = getFmtMessage();

            prepaymentChargeStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentChargeList.do",
                baseParams: {
                    supplierId : supplierId,
                    contractNumber : $("#contractNumber").val(),
                    customerNo : $("#customerNo").val(),
                    customerName : $("#customerName").val(),
                    mdsId : $("#mdsId").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contractNumber", "mdsId", "customerNo", "customerName", "address", "lastTokenDate",
                         "currentCredit", "contractId", "customerId", "meterId", "lastTokenId", "contractDemand", "tariffCode"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            var colWidth = (width-80)/8-chromeColAdd;
            prepaymentChargeColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[0], dataIndex: 'contractNumber'}
                   ,{header: fmtMessage[1], dataIndex: 'customerNo'}
                   ,{header: fmtMessage[3], dataIndex: 'mdsId'}
                   ,{header: fmtMessage[2], dataIndex: 'customerName'}
                   ,{header: fmtMessage[4], dataIndex: 'address'}
                   ,{header: fmtMessage[5], dataIndex: 'lastTokenDate', align: 'center'}
                   ,{header: fmtMessage[6], dataIndex: 'currentCredit', align: 'right'}
                   ,{header: fmtMessage[7], dataIndex: 'chargeAmount', align: 'right', renderer: Ext.util.Format.numberRenderer("0,000"),
                       editor: new Ext.form.NumberField({
                           id: 'chPrice',
                           allowBlank: true,
                           allowNegative: true
                       })
                	}
                   ,{header: "", align: 'center', width: 80-chromeColAdd, renderer: renderSaveBtn}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
                   ,renderer: addTooltip
                }
            });

            if(prepaymentChargeGridOn == false) {
                prepaymentChargeGrid = new Ext.grid.EditorGridPanel({
                	clicksToEdit: 1,
                    store: prepaymentChargeStore,
                    colModel : prepaymentChargeColModel,
                    //sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    sm: new Ext.grid.RowSelectionModel({
                        singleSelect:true,
                        listeners: {
                            rowselect: function(sm, row, rec) {
                                selectedContractNumber = rec.get("contractNumber");
                                getChargeHistoryList();
                            }
                        }
                    }),
                    autoScroll:false,
                    width: width,
                    height: 462,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'prepaymentChargeDiv',
                    viewConfig: {
                    	forceFit: true,
                        scrollOffset: 1,
                        enableRowBody: true,
                        showPreview: true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: prepaymentChargeStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                prepaymentChargeGridOn = true;
            } else {
                prepaymentChargeGrid.setWidth(width);
                var bottomToolbar = prepaymentChargeGrid.getBottomToolbar();
                prepaymentChargeGrid.reconfigure(prepaymentChargeStore, prepaymentChargeColModel);
                bottomToolbar.bindStore(prepaymentChargeStore);
            }
            hide();
        };

        // grid column 내 save button 생성
        function renderSaveBtn(value, meta, rec) {
            var id = Ext.id();
            createGridButton.defer(1, this, ['<fmt:message key="aimir.save2"/>', id, rec]);
            return('<div id="' + id + '" style="margin:0px; padding:0px; height: 0px;"></div>');
        }

        function createGridButton(value, id, rec) {
            new Ext.Button({
                text: value,
                handler : function(btn, e) {
                	saveChargeAmount(rec)
                }
            }).render(document.body, id);
        }
      
        function saveChargeAmount(rec) {
            var isAction = false;
            var chargeAmount = rec.get("chargeAmount");

            if (chargeAmount != null && chargeAmount != "" && Number(chargeAmount) != 0) {
            	isAction = true;
            }

            if (isAction) {
                Ext.Msg.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>", function(btn) {
                    if (btn == "yes") {
                        emergePre();

                        var params = {
                            contractNumber: rec.get("contractNumber"),
                            mdsId: rec.get("mdsId"),
                            lastTokenId: (rec.get("lastTokenId") == null) ? "" : rec.get("lastTokenId"),
                            contractDemand: (rec.get("contractDemand") == null) ? "" : rec.get("contractDemand"),
                            tariffCode: (rec.get("tariffCode") == null) ? "" : rec.get("tariffCode"),
                            amount: chargeAmount,
                            supplierId : supplierId
                        };

                        $.post("${ctx}/gadget/prepaymentMgmt/savePrepaymentCharge.do",
                               params,
                               function(json) {
                                   if (json != null && json.result == "success") {
                                       Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>",
                                               function() {
                                                   prepaymentChargeStore.reload();
                                                   prepaymentChargeStore.rejectChanges();
                                               });
                                   } else {
                                       Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.save.error"/>");
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

        // window resize event
        $(window).resize(function() {
        	getPrepaymentChargeList();
        	getChargeHistoryList();
        });

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        /* 충전이력 리스트 START */
        var selectedContractNumber = "";
        //var selectedServiceType;
        var chargeHistoryStore;
        var chargeHistoryGridOn = false;
        var chargeHistoryGrid;
        var chargeHistoryColModel;
        var getChargeHistoryList = function() {
            var width = $("#chargeHistory").width();
            //var mxwidth = 1210;
            var rowSize = 4;

            chargeHistoryStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: rowSize}},
                url: "${ctx}/gadget/prepaymentMgmt/getChargeHistoryList.do",
                baseParams: {
                    contractNumber : selectedContractNumber,
                    //serviceType : selectedServiceType,
                    searchStartMonth : $("#searchStartMonth").val(),
                    searchEndMonth : $("#searchEndMonth").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["lastTokenDate", "balance", "chargedCredit", "currentCredit", "usedCost", "usedConsumption",
                         "keyNum", "payment", "contractId", "authCode", "municipalityCode", "lastTokenId", "prepaymentLogId"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            var historyMessage = getHistoryFmtMessage();
            var colWidth = (width-80)/6-chromeColAdd;
            chargeHistoryColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: historyMessage[0], dataIndex: 'lastTokenDate'}
                    ,{header: historyMessage[1], dataIndex: 'chargedCredit', align: 'right'}
                    ,{header: historyMessage[2], dataIndex: 'balance', align: 'right'}
                    ,{header: historyMessage[3], dataIndex: 'lastTokenId'}
                    ,{header: historyMessage[4], dataIndex: 'authCode'}
                    ,{header: historyMessage[5], dataIndex: 'municipalityCode'}
                    ,{header: "", align: 'center', width: 80-chromeColAdd-4, renderer: renderReceiptBtn}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: colWidth
                }
            });

            if(chargeHistoryGridOn == false) {
                chargeHistoryGrid = new Ext.grid.GridPanel({
                      store: chargeHistoryStore,
                      colModel : chargeHistoryColModel,
                      sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                      autoScroll:false,
                      width: width,
                      //height: 165,
                      //height: 172,
                      height: 165,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'chargeHistory',
                      viewConfig: {
                          //forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: rowSize,
                          store: chargeHistoryStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                chargeHistoryGridOn = true;
            } else {
                chargeHistoryGrid.setWidth(width);
                var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
                chargeHistoryGrid.reconfigure(chargeHistoryStore, chargeHistoryColModel);
                bottomToolbar.bindStore(chargeHistoryStore);
            }
        };

        // grid column 내 영수증 출력 button 생성
        function renderReceiptBtn(value, meta, rec) {
            var id = Ext.id();
            createGridReceiptButton.defer(1, this, ['<fmt:message key="aimir.receipt"/>', id, rec]);
            return('<div id="' + id + '" style="margin:0px; padding:0px; height: 0px;"></div>');
        }

        function createGridReceiptButton(value, id, rec) {
            new Ext.Button({
                text: value,
                handler : function(btn, e) {
                    openReceiptPopup(rec);
                    //alert("Print Receipt");
                }
            }).render(document.body, id);
        }

        // 날짜조회조건 생성
        var searchDivInit = function() {
            $(function() { $("#fromYearCombo").bind("change", function(event) { getMonthCombo("from", ""); } ); });
            $(function() { $("#toYearCombo").bind("change", function(event) { getMonthCombo("to", ""); } ); });
            $(function() { $("#fromMonthCombo").bind("change", function(event) { getSearchDate("from"); } ); });
            $(function() { $("#toMonthCombo").bind("change", function(event) { getSearchDate("to"); } ); });

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getYear.do"
                    ,{}
                    ,function(json) {
                        var fstYear = json.fstYear;
                        var lstYear = json.lstYear;
                        var fromYear = json.fromYear;
                        var toYear = json.toYear;
                        var fromMonth = json.fromMonth;
                        var toMonth = json.toMonth;

                        $("#fromYearCombo").numericOptions({from:fstYear,to:lstYear,selectedIndex:0});
                        $("#fromYearCombo").val(fromYear);
                        $("#fromYearCombo").selectbox();
                        getMonthCombo("from", fromMonth); // 월 selectBox 내용을 채운다.

                        $("#toYearCombo").numericOptions({from:fstYear,to:lstYear,selectedIndex:0});
                        $("#toYearCombo").val(toYear);
                        $("#toYearCombo").selectbox();
                        getMonthCombo("to", toMonth); // 월 selectBox 내용을 채운다.

                        // 선불고객 grid 조회
                        //getPrepayContractDivData();
                    });
        };

        var getMonthCombo = function(fromto, monthVal) {
            var year = null;

            if (fromto == "from") {
                year = $("#fromYearCombo").val();
            } else {
                year = $("#toYearCombo").val();
            }

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getMonth.do"
                    ,{"year" : year}
                    ,function(json) {
                        var lstMonth = json.lstMonth;
                        var monthObj = null;

                        if (fromto =="from") {
                            monthObj = $("#fromMonthCombo");
                        } else {
                            monthObj = $("#toMonthCombo");
                        }

                        var prevMonth = monthObj.val();

                        monthObj.emptySelect();

                        if( prevMonth == "" || prevMonth == null || Number(prevMonth) > Number(lstMonth) ) {
                            prevMonth = lstMonth;
                        }

                        var idx = Number(prevMonth) - 1;

                        monthObj.numericOptions({from:1,to:lstMonth,selectedIndex:idx});

                        if( monthVal != null && monthVal != "" ){
                            monthObj.val(monthVal);
                        }

                        monthObj.selectbox();

                        getSearchDate(fromto);
                    });
        };

        var getSearchDate = function(fromto) {
            if (fromto == "from") {
                var year = $("#fromYearCombo").val();
                var month = (Number($("#fromMonthCombo").val()) < 10) ? "0" + $("#fromMonthCombo").val() : $("#fromMonthCombo").val();

                $("#searchStartMonth").val(year + "" + month);
            } else {
                var year = $("#toYearCombo").val();
                var month = (Number($("#toMonthCombo").val()) < 10) ? "0" + $("#toMonthCombo").val() : $("#toMonthCombo").val();

                $("#searchEndMonth").val(year + "" + month);
            }
        };

        //receipt window(print)
        var win;
        function openReceiptPopup(rec) {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.supplierId = supplierId;
            obj.contractId = rec.get("contractId");
            obj.prepaymentLogId = rec.get("prepaymentLogId");

            if(win)
                win.close();
            win = window.open("${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptFramePopup.do", "ReceiptPopup", opts);
            win.opener.obj = obj;
        }

    /*]]>*/
    </script>
</head>
<body>

    <!-- search-background DIV (S) -->
    <div class="search-bg-withtabs" style="padding-top: 10px;">

        <!--검색조건-->
        <div class="searchoption-container">
            <table class="searchoption wfree" >
                <tr>
                    <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                    <td class="padding-r20px2"><input id="contractNumber" type="text" style="width:110px;"></td>
                    <td class="withinput"><fmt:message key="aimir.customername"/></td>
                    <td class="padding-r20px2"><input id="customerName" type="text" style="width:120px;"></td>
                    <td class="withinput">Customer No.</td>
                    <td class="padding-r20px2"><input id="customerNo" type="text" style="width:120px;"></td>
                    <td class="withinput">Meter ID</td>
                    <td class="padding-r20px2"><input id="mdsId" type="text" style="width:120px;"></td>
                    <td class="padding-r20px2">
                        <span class="am_button margin-l10 margin-t1px">
                        <a href="javascript:send();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                    </td>
                </tr>
            </table>

        </div>
        <!--검색조건 끝-->

    </div>
    <!-- search-background DIV (E) -->

    <div class="margin-t10px"></div>

    <div class="gadget_body2">
        <div id="prepaymentChargeDiv"></div>
    </div>


    <div class="margin-t20px"></div>
    <div class="gadget_body2">
        <%-- <div class="padding-t7px margin-b10px"><label class="check"><fmt:message key='aimir.hems.prepayment.chargehistory'/></label></div> --%>
        <div class="padding-t7px" style="margin-bottom: 10px;"><label class="check"><fmt:message key='aimir.hems.prepayment.chargehistory'/></label></div>
        <div class="saving_goal">
             <table>
                <tr>
                    <td><select id="fromYearCombo" style="width:60px"></select></td>
                    <td class="chdate"><fmt:message key="aimir.year1" /></td>
                    <td><select id="fromMonthCombo" style="width:35px"></select></td>
                    <td class="chdate"><fmt:message key="aimir.day.mon" /></td>
                    <td class="chdate">~</td>
                    <td><select id="toYearCombo" style="width:60px"></select></td>
                    <td class="chdate"><fmt:message key="aimir.year1" /></td>
                    <td><select id="toMonthCombo" style="width:35px"></select></td>
                    <td class="chdate"><fmt:message key="aimir.day.mon" /></td>
                    
                    <td></td>
                    <td class="btnspace"><a href="javascript:getChargeHistoryList();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>
                </tr>
            </table>

        </div>
        <div class="margin-t5px"></div>

        <div id="chargeHistory"></div>
    </div>
</body>
</html>