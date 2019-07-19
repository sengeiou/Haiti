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
        button.x-btn-text {
            height: 15px !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //공급사ID
        var supplierId="${supplierId}";

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
            window.setTimeout(function(){getPrepaymentChargeList();}, 500);
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

        function send(){
        	getMeteringDataList();
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
                url: "${ctx}/system/prepaymentMgmt/getPrepaymentChargeList.do",
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
                   /* ,{header: "", align: 'center',
                       renderer: function(value, metaData, record, index) {
                           var btnHtml = "<a href='#;' onclick='saveChargeAmount({row});' class='btn_blue'><span><fmt:message key="aimir.save2"/></span></a>";
                           var tplBtn = new Ext.Template(btnHtml);
                           return tplBtn.apply({row: index});
                       }
                   } */
                   ,{header: "", align: 'center', width: 80, renderer: renderSaveBtn}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: (width-80)/8-chromeColAdd
                   ,renderer: addTooltip
                }
            });

            if(prepaymentChargeGridOn == false) {
                prepaymentChargeGrid = new Ext.grid.EditorGridPanel({
                	clicksToEdit: 1,
                    store: prepaymentChargeStore,
                    colModel : prepaymentChargeColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 522,
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

        // grid column 내 button 생성
        function renderSaveBtn(value, meta, rec) {
            var id = Ext.id();
            createGridButton.defer(1, this, ['Save', id, rec]);
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
        	//var rec = prepaymentChargeStore.data.items[row];
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
                            lastTokenId: rec.get("lastTokenId"),
                            contractDemand: rec.get("contractDemand"),
                            tariffCode: rec.get("tariffCode"),
                            amount: chargeAmount,
                            supplierId : supplierId
                        };

                        $.post("${ctx}/system/prepaymentMgmt/savePrepaymentCharge.do",
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

<!-- <div class="margin-t10px"></div> -->

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
                    <a href="javascript:getPrepaymentChargeList();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                </td>
                
            </tr>
        </table>

    </div>
    <!--검색조건 끝-->

</div>
<!-- search-background DIV (E) -->

<div class="margin-t10px">
</div>

<div class="gadget_body2">
    <div id="prepaymentChargeDiv"></div>
</div>

</body>
</html>