<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.contract.wattage.over"/> <fmt:message key="aimir.customer"/></title>
   
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css"> 
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
     <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
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
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" >/*<![CDATA[*/

	    //탭초기화
	    var tabs = {hourly:0,daily:0,monthlyPeriod:0,yearly:0};
        var tabNames = {};
        var chromeColAdd = 2;

        var supplierId = ${supplierId};
        var serviceType = ServiceType.Electricity;

    
        $(function(){
            Ext.QuickTips.init();
     
            //달력붙이기
            $("#fromDate").datepicker({
                showOn: 'button', buttonImage: '${ctx}/images/calendar.gif', buttonImageOnly: true
            });
            $("#toDate").datepicker({
                showOn: 'button', buttonImage: '${ctx}/images/calendar.gif', buttonImageOnly: true
            });
           
            getLocations();        
            getTariffTypes();

            getAbnormalContractUsageEmMaxGrid();
   
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {

            //리싸이즈시 패널 인스턴스 kill & reload
            abnormalContractUsageEmMaxGrid.destroy();
            abnormalContractUsageEmMaxGridOn = false;
            
            getAbnormalContractUsageEmMaxGrid();
                
        });
        function getLocations() {
            $.getJSON('${ctx}/gadget/mvm/getLocations.do', {supplierId:supplierId},
                    function(json) {
                        $('#locationId').loadSelect(json.locations);
                        $('#locationId').selectbox();
                    }
            );
        }

        function getTariffTypes() {
            $.getJSON('${ctx}/gadget/mvm/getTariffTypes.do', {serviceType:serviceType, supplierId:supplierId},
                    function(json) {
                        $('#tariffIndexId').loadSelect(json.tariffTypes);
                        $('#tariffIndexId').selectbox();
                    }
            );
        }

        function send(){
            getAbnormalContractUsageEmMaxGrid();
        };


        // 메세지 처리
        function getFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";                // 번호
            fmtMessage[1] = "<fmt:message key="aimir.date"/>";                  // 일자
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";          // 고객명
            fmtMessage[3] = "<fmt:message key="aimir.contract"/>"+" "+"<fmt:message key="aimir.number"/>"; // 계약 번호
            fmtMessage[4] = "<fmt:message key="aimir.contract.tariff.type"/>";  // 계약 종별
            fmtMessage[5] = "<fmt:message key="aimir.contract.demand"/>";      // 계약 전력
            fmtMessage[6] = "<fmt:message key="aimir.demand.wattage"/>";        // 수요 전력
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[10] = "<fmt:message key="aimir.firmware.msg09"/>"; // excel export 조회데이터 없음.
            fmtMessage[11] = "<fmt:message key="aimir.excel.abnormalContractUsage"/>" // Title
            return fmtMessage;
        }

        // 조회 조건 전달
        function getCondition(){
            var cnt = 0; 
            var condArray = new Array();

            condArray[cnt++] = supplierId;
            condArray[cnt++] = ($('#locationId').val() == null || $('#locationId').val()=="") ? 0 : $('#locationId').val();
            condArray[cnt++] = ($('#tariffIndexId').val() == null || $('#tariffIndexId').val()=="") ? 0 : $('#tariffIndexId').val();
            condArray[cnt++] = encodeURIComponent($('#customerName').val());
            condArray[cnt++] = $('#contractNo').val();
            condArray[cnt++] = $('#wattage').val();
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();

            return condArray;
        }

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        var win;
        function exportExcel(){
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var fmtMessage1 = new Array();
            var condition1  = new Array();

            fmtMessage1 = getFmtMessage();
            condition1 = getCondition();

            obj.condition = condition1;
            obj.fmtMessage = fmtMessage1;
            obj.url = '${ctx}/gadget/mvm/abnormalContractUsageMaxExcelMake.do';
            
            if(win)
                win.close();
            win = window.open("${ctx}/gadget/ExcelDownloadPopup.do",
                            "AbnormalContractUsageExcel", opts);
            win.opener.obj = obj;

        };
        
        var abnormalContractUsageEmMaxGridStore;
        var abnormalContractUsageEmMaxGridColModel;
        var abnormalContractUsageEmMaxGridOn = false;
        var abnormalContractUsageEmMaxGrid;
        //AbnormalContractUsageEmMax 그리드
        function getAbnormalContractUsageEmMaxGrid(){

            var arrayObj = getCondition();
            var message  = getFmtMessage();

            var width = $("#abnormalContractUsageEmMaxGridDiv").width(); 

             abnormalContractUsageEmMaxGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 20}},
                url: "${ctx}/gadget/mvm/getAbnormalContractUsageEMList.do",
                baseParams:{
                    supplierId    : arrayObj[0],
                    locationId    : arrayObj[1],
                    tariffType    : arrayObj[2],
                    customerName  : arrayObj[3],
                    contractNo    : arrayObj[4],
                    wattage       : arrayObj[5],
                    dateType      : arrayObj[6],
                    fromDate      : arrayObj[7],
                    toDate        : arrayObj[8],
                    pageSize      : 20
                },
                totalProperty: 'total',
                root:'grid',
                 fields: [
                { name: 'no', type: 'Integer' },
                { name: 'yyyymmdd', type: 'String' },
                { name: 'customerName', type: 'String' },
                { name: 'contractNo', type: 'String' },
                { name: 'tariffName', type: 'String' },
                { name: 'contractUsage', type: 'String' },
                { name: 'demandUsage', type: 'String' }
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                     }
                }
            });

            abnormalContractUsageEmMaxGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[0],
                        tooltip:message[0],
                        dataIndex:'no',
                        width: 5 ,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        tooltip:message[1],
                        dataIndex:'yyyymmdd',
                        width: 10,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        tooltip:message[2],
                        dataIndex:'customerName',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        tooltip:message[3],
                        dataIndex:'contractNo',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[4],
                        tooltip:message[4],
                        dataIndex:'tariffName',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[5],
                        tooltip:message[5],
                        dataIndex:'contractUsage',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[6],
                        tooltip:message[6],
                        dataIndex:'demandUsage',
                        width: 10 ,
                        align:'center'
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                    ,renderer: addTooltip
                }
            });

            if (abnormalContractUsageEmMaxGridOn == false) {
               
                abnormalContractUsageEmMaxGrid = new Ext.grid.GridPanel({
                   
                    id: 'abnormalContractUsageEmMaxGrid',
                    store: abnormalContractUsageEmMaxGridStore,
                    cm : abnormalContractUsageEmMaxGridColModel,
                    autoScroll: true,
                    width: width,
                    height: 525,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'abnormalContractUsageEmMaxGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: abnormalContractUsageEmMaxGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                abnormalContractUsageEmMaxGridOn  = true;

            } else {
                
                abnormalContractUsageEmMaxGrid.setWidth(width);
                abnormalContractUsageEmMaxGrid.reconfigure(abnormalContractUsageEmMaxGridStore, abnormalContractUsageEmMaxGridColModel);
                 var bottomToolbar = abnormalContractUsageEmMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(abnormalContractUsageEmMaxGridStore);
            }
            
        };
        /*]]>*/
    </script>
</head>
<body>



<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">

	<div class="dayoptions">
	<%@ include file="/gadget/commonDateTab.jsp"%>
	</div>
	<div class="dashedline"><ul><li></td></ul></div>


	<!--검색조건-->
	<div class="searchoption-container">
		<table class="searchoption wfree">
			<tr>
	            <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
	            <td class="padding-r20px"><select id="locationId" name="locationId" style="width:150px;"></select></td>
	            <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
	            <td class="padding-r20px"><select id="tariffIndexId" name="tariffIndexId" style="width:230px;"></select></td>
	            <td class="withinput"><fmt:message key="aimir.customername" /></td>
	            <td class="padding-r20px"><input id="customerName" name="customerName" type="text" class="day"></td>
	            <td class="withinput"><fmt:message key="aimir.contract"/> <fmt:message key="aimir.number"/></td>
	            <td class="padding-r20px"><input id="contractNo"name="contractNo" type="text" class="day"></td>
	            <td class="withinput"><fmt:message key="aimir.contract.wattage" /></td>
	            <td><input id="wattage"name="wattage" type="text" class="day"></td>
	        </tr>
		</table>
	</div>
	<!--검색조건 끝-->

</div>
<!-- search-background DIV (E) -->



<div id="gadget_body">
    <div id="btn" class="btn_right_top2 margin-t10px">
        <ul><li><a href="javaScript:exportExcel();" class="on">Excel</a></li></ul>
    </div>
    <div class="gadget_body2" id="abnormalContractUsageEmMaxGridDiv"></div>
</div>

</body>
</html>
