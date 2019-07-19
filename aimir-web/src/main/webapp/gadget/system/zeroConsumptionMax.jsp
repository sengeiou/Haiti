<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title></title>
    <style type="text/css">
        html{overflow:auto !important}

        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;            
            font-weight: bold !important;
        }
        .zeroConExcelBtn {
            float:right;
            margin-right:10px;
        }
    </style>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery-1.4.2.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery.selectbox.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/autocomplete/jquery.autocomplete.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/commonConstants.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery-ui-1.7.2.min.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery.form.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FusionCharts.js"></script>
<!--    <script type='text/javascript' charset='utf-8' src="${ctx}/js/common.js"></script>    
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FusionChartsExportComponent.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/highcharts.js"></script> -->
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FChartStyle.js"></script> 
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        
        var operatorId = "${operatorId}";
        var supplierId = "${supplierId}";        
        var selectedMeterId = "";
        var selectedMcuId = "";
        var loginId = "${loginId}";
        var selectedContractNumber;
        var selectedServiceType;        


        var fInterval;
        function delayInit(){
            clearInterval(fInterval);
            Ext.QuickTips.init();
            extColumnResize();

            initZeroConsumptionGrid();
            zeroConGridLoad();
            hide();
        }

        $(document).ready(function(){
            fInterval = setInterval("delayInit()", 300);
        });


/*        Ext.onReady(function() { 
            Ext.QuickTips.init();
            extColumnResize();

            initZeroConsumptionGrid();

            hide();

            //setTimeout(zeroConGridLoad(), 30000); // 최초 한번 로딩
        });
*/
        // window resize event
        $(window).resize(function() {
         //   initZeroConsumptionGrid();
        });

        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
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
        }

        /*****************
            검색 옵션용
        ******************/
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs = {hourly:0,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:1,yearly:0};
        
        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'', daily:'', period:'', weekly:'', monthly:'', monthlyPeriod:'', weekDaily:'', seasonal:'', yearly:''};

        var inputDate = new Object();
        inputDate._dateType = DateType.DAILY;  // 최초 조회탭
        inputDate.dailyStartDate = "${formatDate}";
        inputDate.searchStartDate = "${currentDate}";
        inputDate.searchEndDate   = "${currentDate}";
        tabs.InputDate = inputDate;

        // 공통조회화면 필수 function
        function send() {
            zeroConGridLoad();
        }


        var zeroConGrid;
        var zeroConGridColModel;
        var zeroConGridStore;
        var zeroConPagingSize = 15;


        // 그리드 로딩
        function zeroConGridLoad(){
            zeroConGridStore.baseParams = {};
            zeroConGridStore.setBaseParam('supplierId', supplierId);
            zeroConGridStore.setBaseParam('searchDateType', $("#searchDateType").val());
            zeroConGridStore.setBaseParam('searchStartDate', $("#searchStartDate").val());
//            zeroConGridStore.setBaseParam('searchStartHour', $("#searchStartHour").val());
            zeroConGridStore.setBaseParam('searchEndDate', $("#searchEndDate").val());
//            zeroConGridStore.setBaseParam('searchEndHour', $("#searchEndHour").val());
            zeroConGridStore.setBaseParam('searchWeek', $("#weeklyWeekCombo").val());

            zeroConGridStore.load({params:{start: 0, limit: zeroConPagingSize}});
        }

        // 그리드 스토어 설정
        function initZeroConGridStore(){
            zeroConGridStore = new Ext.data.JsonStore({
                url : "${ctx}/gadget/system/getZeroConsumptionCustomerContracList.do",
                totalProperty : 'totalCount',
                root : 'result',
                fields: [            
                    { name: 'contractNumber', type: 'String' },
                    { name: 'customerName', type: 'String' },   
                    { name: 'mdsId', type: 'String' },               
                    { name: 'serviceTypeCode', type: 'String' },
                    { name: 'serviceTypeName', type: 'String' },
                    { name: 'creditTypeCode', type: 'String' },          
                    { name: 'creditTypeName', type: 'String' },
                    { name: 'tariffTypeName', type: 'String' },
                    { name: 'prepaymentPowerDelay', type: 'String' },
                    { name: 'lastTokenDate', type: 'String' },
                    { name: 'currentCredit', type: 'String' },
                    { name: 'statusName', type: 'String' },
                    { name: 'emergencyCreditStartTime', type: 'String' },
                    { name: 'emergencyCreditMaxDuration', type: 'String' },
                    { name: 'emergencyCreditMaxDate', type: 'String' },
                    { name: 'meterId', type: 'String' },
                    { name: 'mcuId', type: 'String' },
                    { name: 'modelName', type: 'String' }
                ],
                listeners: {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    },
                    load: function(store, record, options){
                        if (record.length > 0) {
                            // 데이터 load 후 첫번째 row 자동 선택
                            zeroConGrid.getSelectionModel().selectFirstRow();
                        } else {
                            // 이전 데이터 모두 지움.
                            selectedContractNumber = "";
                            selectedServiceType = "";
                            selectedMeterId = "";
                            selectedMcuId = "";
                        }
                    }
                }        
            }); 
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }        

        // 그리드 컬럼모델 생성
        function initZeroConGridColModel(){
            var mxwidth = 1200;
            var width = $("#zeroConGridDiv").width();

            zeroConGridColModel = new Ext.grid.ColumnModel({
                columns: [
                     {header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'contractNumber', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.customername"/>", dataIndex: 'customerName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>", dataIndex: 'lastTokenDate', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'currentCredit', width: (width > mxwidth) ? width*(120/mxwidth) : 120, align:'right'}
                    ,{header: "<fmt:message key="aimir.meterid"/>", dataIndex: 'mdsId', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                  //,{header: "<fmt:message key="aimir.address"/>", dataIndex: 'address', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.supply.type"/>", dataIndex: 'serviceTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.contract.tariff.type"/>", dataIndex: 'tariffTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                  //,{header: "<fmt:message key="aimir.hems.prepayment.limitpower"/>(kWh)", dataIndex: 'prepaymentPowerDelay', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.supplystatus"/>", dataIndex: 'statusName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDate', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                  //,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDuration', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 120
                    ,align: 'center'
                    ,renderer: addTooltip
               }
            });
        }

        // 그리드 초기화
        function initZeroConsumptionGrid() {
            var zeroConGridWith = $("#zeroConGridDiv").width();

            initZeroConGridStore();
            initZeroConGridColModel();
            
            zeroConGrid = new Ext.grid.GridPanel({
                store : zeroConGridStore,
                colModel : zeroConGridColModel,
                sm: new Ext.grid.RowSelectionModel({
                  singleSelect:true,
                  listeners: {
                      rowselect: function(sm, row, rec) {
                          var selectedModelName = rec.get("modelName");
                          enableRelay(selectedModelName);
                          selectedMeterId = rec.get("meterId");
                          selectedMcuId = rec.get("mcuId");
                          selectedContractNumber = rec.get("contractNumber");
                          selectedServiceType = rec.get("serviceTypeCode");
                      }
                  }
                }),
                autoScroll : false,
                width : zeroConGridWith,
                height : 300,
                stripeRows : true,
                columnLines : true,         
                loadMask : {
                    msg: 'loading...'
                },
                renderTo : 'zeroConGridDiv',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                // paging bar on the bottom
                bbar : new Ext.PagingToolbar({
                    pageSize : zeroConPagingSize,
                    store : zeroConGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}',
                    emptyMsg: 'No Data'
                }) 
            });
        };

        function cmdRelayOn() {
            if(selectedMeterId == '' || typeof(selectedMeterId) == 'undefined') return;

            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOn.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }
        
        function cmdRelayOff() {
            if(selectedMeterId == '' || typeof(selectedMeterId) == 'undefined') return;

            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOff.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }

        function cmdRelayStatus() {  
            if(selectedMeterId == '' || typeof(selectedMeterId) == 'undefined') return;

            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemoteGetStatus.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }

        //선택한 계약의 미터모델이 Relay 기능이 가능한 모델인지 검색 후 가능한 모델일 경우 화면에 Relay On/Off 버튼을 보여준다.
        function enableRelay(selectedModelName) {
            
            $.getJSON("${ctx}/gadget/prepaymentMgmt/getRelayEnableModel.do",
                    {devicemodelName : selectedModelName},
                    function(result) {
                        var namesOfContain = result.namesOfContain;
                        if (namesOfContain.length > 0) {
                            for ( var i = 0; i < namesOfContain.length; i++) {
                                switch (namesOfContain[i]) {
                                    case 'relayControl':
                                        $('#relayControlButton').show();
                                        break;
                                }
                            }
                        }
                    }
                );
        }        


        function openExcelReport() {
            //var opts="width=430px;height:350px;resizable:no;status:no;help:no;center:yes;";
            var opts="width=570,height=350,resizable=no,status=no,help=no,center=yes";
            var obj = new Object();
            var header = new Array();
            var param = new Array();

            //title
            header[0] = "<fmt:message key='aimir.contractNumber'/>"; // Contract NO.
            header[1] = "<fmt:message key='aimir.customername'/>";   // Customer Name
            header[2] = "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>"; // Last Charge Date
            header[3] = "<fmt:message key='aimir.hems.prepayment.currentbalance'/>(<fmt:message key='aimir.price.unit'/>)"; // Remaining Credit(Dollar(s))
            header[4] = "<fmt:message key='aimir.meterid'/>";     // Meter ID
            header[5] = "<fmt:message key='aimir.supply.type'/>"; // Supply Type
            header[6] = "<fmt:message key='aimir.contract.tariff.type'/>";        // Tariff Type
            header[7] = "<fmt:message key='aimir.supplystatus'/>";                // Supply Status
            header[8] = "<fmt:message key='aimir.hems.prepayment.validperiod'/>"; // Valid Date
            header[9] = "<fmt:message key='aimir.comsumption.zero'/> <fmt:message key='aimir.customer'/> <fmt:message key='aimir.contract'/>"; // Title = Zero Consumptioin Customer Contract

            //parameter
            param[0] = supplierId;
            param[1] = $("#searchDateType").val();     // commonDateTab.jsp 파일에 있음.
            param[2] = $("#searchStartDate").val();    // commonDateTab.jsp 파일에 있음.
            param[3] = $("#searchEndDate").val();      // commonDateTab.jsp 파일에 있음.
            param[4] = $("#weeklyWeekCombo").val();    // commonDateTab.jsp 파일에 있음.

            obj.fmtMessage = header;
            obj.condition = param ;

            var winObj = window.open("${ctx}/gadget/system/getZeroConsumptionCustomerExcelDownloadPopup.do", "Open Excel Report", opts);
            winObj.opener.obj = obj;

        }        
        /*]]>*/
    </script>
</head>
    <body>
        <!-- <input type="hidden" id="sicIds" name="sicIds"/>  -->

        <!-- search-background DIV (S) -->
        <div class="search-bg-withtabs">
            <div class="dayoptions">
                <%@ include file="/gadget/commonDateTab.jsp"%>

                <div id="btn" class="zeroConExcelBtn">
                    <ul><li><a href="javascript:openExcelReport()" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
                </div>            
            </div>
        </div>
        <!-- search-background DIV (E) -->

        <div id="gadget_body">
            <div id="zeroConGridDiv"></div>
        </div>

        <!-- Relay On/Off 버튼 -->
        <!-- 잔액 통보 설정 -->
        <div id="relayControlButton" style="margin: 10px;">
                <a href="#" class="btn_blue" style="margin-left: 0px"  onclick="cmdRelayOn();"><span><fmt:message key="aimir.meter.command.RelayOn"/><!-- Relay On --></span></a>
                <a href="#" class="btn_blue" onClick="cmdRelayOff();"><span><fmt:message key="aimir.meter.command.RelayOff"/><!-- Relay Off --></span></a>
                <a href="#" class="btn_blue" onClick="cmdRelayStatus();"><span><fmt:message key="aimir.meter.command.RelayStatus"/><!-- Relay Status --></span></a>
                
                <div class="meterinfo-textarea clear">
                    <ul>
                        <!-- <li><textarea id="commandResult" readonly style="width: 500px; height: 60px;">Result</textarea></li>  -->
                        <li><textarea id="commandResult" readonly>Result</textarea></li>
                    </ul>
                </div>
        </div>

    </body>
</html>