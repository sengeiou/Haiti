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
    /* grid 안에 button 이 있는 경우 높이조정 */
    .x-grid3-cell-inner {
        padding-top: 0px;
        padding-bottom: 0px;
    }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>
<%-- Ext-JS 관련 --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/Exporter-all.js"></script>
<%-- TreeGrid 관련 js --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8">

    var supplierId = "${supplierId}";

    $(function() {
        $('#_test').bind('click', function(event) {
        });
        $('#_result').bind('click', function(event) {
        });
        $("#pocTestTab").subtabs();
        getTargetNodes();
        $('#weather').selectbox();
        var locDateFormat = "yymmdd";
        $("#sExecutionStartDate").datepicker({showOn: 'both', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#sExecutionEndDate").datepicker({showOn: 'both', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#sExecutionStartDate").datepicker("setDate", -1);
        $("#sExecutionEndDate").datepicker("setDate",  new Date());
        modifyDate($("#sExecutionStartDate").val(), $("#sExecutionStartDate")[0]);
        modifyDate($("#sExecutionEndDate").val(), $("#sExecutionEndDate")[0]);
    });

    function modifyDate(setDate, inst){
        var dateId = '#' + inst.id;

        var dateHiddenId = '#' + inst.id + 'Hidden';
        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                    $(dateId).trigger('change');
                });
    }

    function getTargetNodes() {
        var path = "${ctx}/gadget/test/getTargetNodes.do";
        var params = { supplierId : supplierId };
        getTargetNodesPostMethod(path, params,"post","");
    }

    function getTargetNodesPostMethod(path, params, method, target) {
        $.getJSON(path,
            params,
            function(json) {
                var list = json;
                for (var mcuSysId in list) {
                    $('#targetNode').append($('<option>', { 
                        value: list[mcuSysId].mcu.mcuType + ":" + mcuSysId,
                        text : "GW - " + mcuSysId
                    }));
                    for(var modem in list[mcuSysId].modems) {
                    	if(list[mcuSysId].modems.hasOwnProperty(modem)) {
                            $('#targetNode').append($('<option>', { 
                                value: list[mcuSysId].modems[modem].modemType+":"+list[mcuSysId].modems[modem].deviceSerial ,
                                text : "- NODE - " + list[mcuSysId].modems[modem].deviceSerial
                            }));
                        }
                    }
                    $('#targetNode').selectbox();
                }
            }
        );
    }

    function getCondition() {

        var arrayObj = Array();

        arrayObj[0] = $('#targetNode').val().split(":")[0];
        arrayObj[1] = $('#targetNode').val().split(":")[1];
        arrayObj[2] = $('#pingCount').val();
        arrayObj[3] = $('#pingBytes').val();
        arrayObj[4] = $('#temperature').val();
        arrayObj[5] = $('#weather').val();
        arrayObj[6] = $('#antAttenuation').val();
        arrayObj[7] = supplierId;

        return arrayObj;
    }

    function executeICMP6Ping() {
        var targetId = $("#targetNode").val();
        var path = "${pocWSURL}/ping";
        var params = {};
        executeICMP6PingPostMethod(path, params,"post","");
    }

    function changeNameMessage(val) {
    	if(val == "targetNode") {
            return "<fmt:message key='aimir.poc.targetNode'/>";
        } else if(val == "ipAddr") {
            return "<fmt:message key='aimir.ipv6address'/>";
        } else if(val == "dateTime") {
            return "<fmt:message key='aimir.poc.dateTime'/>";
        } else if(val == "hops") {
            return "<fmt:message key='aimir.poc.res.hops'/>";
        } else if(val == "loss") {
            return "<fmt:message key='aimir.poc.res.loss'/>";
        } else if(val == "rssi") {
            return "<fmt:message key='aimir.poc.res.rssi'/>";
        } else if(val == "rtt") {
            return "<fmt:message key='aimir.poc.res.rtt'/>";
        } else if(val == "ttl") {
            return "<fmt:message key='aimir.poc.res.ttl'/>";
        } else if(val == "linkBudget") {
            return "<fmt:message key='aimir.poc.res.linkBudget'/>";
        } else if(val == "txPower") {
            return "<fmt:message key='aimir.poc.res.txPower'/>";
        } else if(val == "txPower") {
            return "<fmt:message key='aimir.poc.temperature.c'/>";
        } else if(val == "weather") {
            return "<fmt:message key='aimir.poc.weather'/>";
        } else if(val == "coapResponseTime") {
            return "<fmt:message key='aimir.poc.res.coapResponseTime'/>";
        } else if(val == "obisCode") {
            return "<fmt:message key='aimir.poc.res.obisCode'/>";
        } else if(val == "obisResult") {
            return "<fmt:message key='aimir.poc.res.obisResult'/>";
        } else if(val == "obisValue") {
            return "<fmt:message key='aimir.poc.res.obisValue'/>";
        } else if(val == "obisResponseTime") {
            return "<fmt:message key='aimir.poc.res.obisResponseTime'/>";
        } else {
            return val;
        }
    }

    function changeValue(val) {
        if(val == null || val == 'null') {
            return "<fmt:message key='aimir.poc.res.null'/>";
        } else {
        	return val;
        }
    }

    function changeValue2(val) {
        if(val == null || val == 'null') {
            return "-";
        } else {
        	return val;
        }
    }

    function executeICMP6PingPostMethod(path, params, method, target) {
        $('#testResult').empty();
        $('#testResult').append("<p><fmt:message key='aimir.poc.test.res'/></p><br style='clear:both;'/><p><fmt:message key='aimir.poc.res.icmp6title'/></p><br style='clear:both;'/>");
        var condition = getCondition();
        emergePre();

        $.getJSON(path+'/'+condition[6]+'/'+condition[4]+'/'+condition[5]+'/'+condition[1]+'/'+condition[2]+'/'+condition[3],
            params,
            function(json) {
                if(json == null) {
                	hide();
                	return;
                }
        	    var rt = Ext.data.Record.create([
                    {name: 'name'},
                    {name: 'value'}
                ]);
        	    var ICMP6PingGridStore = new Ext.data.Store({
                    reader: new Ext.data.ArrayReader(
                        {
                            idIndex: 0
                        },
                        rt
                    )
                });
 				var ICMP6Data = [
 				                 ["targetNode",  json.targetNode],
 				                 ["ipAddr",      json.ipAddr],
 				                 ["dateTime",    json.dateTime],
 				                 ["hops",        json.hops ],
 				                 //["temperature", json.temperature ],
 				                 //["weather",     json.weather ],
 				                 ["loss",        json.loss ],
 				                 ["rtt",         json.rtt ],
 				                 ["rssi",        json.rssi ],
 				                 ["linkBudget",  json.linkBudget ],
 				                 ["txPower",     json.txPower ]
 				];
 				ICMP6PingGridStore.loadData(ICMP6Data);

 				var ICMP6PingGridColModel = new Ext.grid.ColumnModel({
 		            defaults: {
 		                menuDisabled: true
 		            },
 		            columns: [{
 		                header: "<fmt:message key='aimir.poc.res.name'/>",
 		                dataIndex: 'name',
 		                align:'left',
 		                width: 30,
 		                sortable: false,
 		                renderer: changeNameMessage
 		            },{
 		                header: "<fmt:message key='aimir.poc.res.value'/>",
 		                dataIndex: 'value',
 		                align:'center',
 		                width: 35,
 		                sortable: false,
 		                renderer: changeValue
 		            }]
 		        });

 		       	var ICMP6PingGrid = new Ext.grid.GridPanel({
 		            height       : 230,
 		            width        : 400,
 		            store        : ICMP6PingGridStore,
 		            colModel     : ICMP6PingGridColModel,
 		            stripeRows   : true,
 		            columnLines  : true,
 		            loadMask     :{
 		                msg: 'loading...'
 		            },
 		            renderTo     : 'testResult',
 		            viewConfig   : {
 		                forceFit         :true,
 		                enableRowBody    :true,
 		                showPreview      :true,
 		                emptyText        : 'No data to display'
 		            }
 		        });
 		       	hide();
            }
        );
    }

    function executeCOAPPing() {
        var path = "${pocWSURL}/coap";
        var params = {};
        executeCOAPPingPostMethod(path, params, "post", "");
    }

    function executeCOAPPingPostMethod(path, params, method, target) {
        $('#testResult').empty();
        $('#testResult').append("<p><fmt:message key='aimir.poc.test.res'/></p><br style='clear:both;'/><p><fmt:message key='aimir.poc.res.coaptitle'/></p><br style='clear:both;'/>");
        var condition = getCondition();
        emergePre();

        $.getJSON(path+'/'+condition[6]+'/'+condition[4]+'/'+condition[5]+'/'+condition[1]+'/'+condition[2],
                params,
                function(json) {
                    if(json == null) {
            	        hide();
            	        return;
                    }
            	    var rt = Ext.data.Record.create([
                        {name: 'name'},
                        {name: 'value'}
                    ]);
            	    var COAPPingGridStore = new Ext.data.Store({
                        reader: new Ext.data.ArrayReader(
                            {
                                idIndex: 0
                            },
                            rt
                        )
                    });
     				var COAPData = [
     				                 ["targetNode",       json.targetNode],
     				                 ["ipAddr",           json.ipAddr],
     				                 ["dateTime",         json.dateTime],
     				                 ["hops",             json.hops ],
     				                 ["rssi",             json.rssi ],
     				                 ["linkBudget",       json.linkBudget ],
     				                 ["txPower",          json.txPower ],
     				                 ["coapResponseTime", json.coapResponseTime ]
     				];
     				COAPPingGridStore.loadData(COAPData);

     				var COAPPingGridColModel = new Ext.grid.ColumnModel({
     		            defaults: {
     		                menuDisabled: true
     		            },
     		            columns: [{
     		                header: "<fmt:message key='aimir.poc.res.name'/>",
     		                dataIndex: 'name',
     		                align:'left',
     		                width: 30,
     		                sortable: false,
     		                renderer: changeNameMessage
     		            },{
     		                header: "<fmt:message key='aimir.poc.res.value'/>",
     		                dataIndex: 'value',
     		                align:'center',
     		                width: 35,
     		                sortable: false,
     		                renderer: changeValue
     		            }]
     		        });

     		       	var COAPPingGrid = new Ext.grid.GridPanel({
     		            height       : 230,
     		            width        : 400,
     		            store        : COAPPingGridStore,
     		            colModel     : COAPPingGridColModel,
     		            stripeRows   : true,
     		            columnLines  : true,
     		            loadMask     :{
     		                msg: 'loading...'
     		            },
     		            renderTo     : 'testResult',
     		            viewConfig   : {
     		                forceFit         :true,
     		                enableRowBody    :true,
     		                showPreview      :true,
     		                emptyText        : 'No data to display'
     		            }
     		        });
     		       	hide();
                }
            );
    }

    function executeOBISCodeReading() {
        var path = "${pocWSURL}/metering"
        var params = {};
        executeOBISCodeReadingPostMethod(path, params,"post","obiscodereading_view");
    }

    function executeOBISCodeReadingPostMethod(path, params, method, target) {
        $('#testResult').empty();
        $('#testResult').append("<p><fmt:message key='aimir.poc.test.res'/></p><br style='clear:both;'/><p><fmt:message key='aimir.poc.res.obistitle'/></p><br style='clear:both;'/>");
        var condition = getCondition();
        emergePre();

        $.getJSON(path+'/'+condition[6]+'/'+condition[4]+'/'+condition[5]+'/'+condition[1]+'/'+condition[2],
            params,
            function(json) {
                if(json == null) {
                    hide();
                    return;
                }
         	    var rt = Ext.data.Record.create([
                     {name: 'name'},
                     {name: 'value'}
                ]);
         	    var OBISCodeGridStore = new Ext.data.Store({
                     reader: new Ext.data.ArrayReader(
                         {
                             idIndex: 0
                         },
                         rt
                     )
                });
  				var OBISData = [
                     ["targetNode",  json.targetNode],
                     ["ipAddr",      json.ipAddr],
                     ["dateTime",    json.dateTime],
	                 ["hops",        json.hops ],
                     ["rssi",        json.rssi ],
                     ["linkBudget",  json.linkBudget ],
                     ["txPower",     json.txPower ],
                     //["obisCode",    json.obisCode ],
                     //["obisResult",  json.obisResult ],
                     ["obisValue",   json.obisValue ],
                     ["obisResponseTime",   json.obisResponseTime ],
  				];
  				OBISCodeGridStore.loadData(OBISData);

  				var OBISCodeGridColModel = new Ext.grid.ColumnModel({
  		            defaults: {
  		                menuDisabled: true
  		            },
  		            columns: [{
  		                header: "<fmt:message key='aimir.poc.res.name'/>",
  		                dataIndex: 'name',
  		                align:'left',
  		                width: 90,
  		                sortable: false,
  		                renderer: changeNameMessage
  		            },{
  		                header: "<fmt:message key='aimir.poc.res.value'/>",
  		                dataIndex: 'value',
  		                align:'center',
  		                width: 60,
  		                sortable: false,
  		                renderer: changeValue
  		            }]
  		        });

  		       	var OBISCodeGrid = new Ext.grid.GridPanel({
  		            height       : 230,
  		            width        : 450,
  		            store        : OBISCodeGridStore,
  		            colModel     : OBISCodeGridColModel,
  		            stripeRows   : true,
  		            columnLines  : true,
  		            loadMask     :{
  		                msg: 'loading...'
  		            },
  		            renderTo     : 'testResult',
  		            viewConfig   : {
  		                forceFit         :true,
  		                enableRowBody    :true,
  		                showPreview      :true,
  		                emptyText        : 'No data to display'
  		            }
  		        });
  		       	hide();
             }
         );
    }

    function searchResult() {
        var command = $("#command").val();
        var startDate = $("#sExecutionStartDateHidden").val();
        var endDate = $("#sExecutionEndDateHidden").val();
        //http://187.1.10.200:8083/networklog/all/20150610/20150623/2/10
    	var path = "${pocWSURL}/networklog";
    	var params = {command : command, startDate : startDate, endDate : endDate};
    	getResultHistory(path, params);
    }

    //meterSearchGrid
    //var meterGridOn = false;
    //var meterGrid;

    function getResultHistory(path, params) {
        $('#resultHistory').empty();

        var renderGrid = function() {
            var width = $("#resultHistory").width();
            var condition = getCondition();

            var meterGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: 10}},
                proxy : new Ext.data.HttpProxy({
                    method: 'GET',
                    //pageParam: false, //to remove param "page"
                    //startParam: false, //to remove param "start"
                    //limitParam: false, //to remove param "limit"
                    //noCache: false, //to remove param "_dc"
                    url: '${pocWSURL}/networklog/'+params.command+'/'+params.startDate+'/'+params.endDate

                }),
                root : 'list',
                totalProperty : 'total',
                idProperty : 'no',
                listeners : {
                    beforeload: function(store, options){
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                        });
                    },load: function(store, record, options){
                    },
                },
                fields : [
                    { name: 'targetNode', type: 'string' },
                    { name: 'ipAddr', type: 'string' },
                    { name: 'dateTime', type: 'string' },
                    { name: 'command', type: 'string' },
                    { name: 'antAttenuation', type: 'string' },
                    { name: 'hops', type: 'string' },
                    { name: 'loss', type: 'string' },
                    { name: 'rssi', type: 'string' },
                    { name: 'rtt', type: 'string' },
                    { name: 'txPower', type: 'string' },
                    { name: 'temperature', type: 'string' },
                    { name: 'weather', type: 'string' },
                    { name: 'linkBudget', type: 'string' },
                    { name: 'coapResponseTime', type: 'string' },
                    { name: 'obisValue', type: 'string' },
                    { name: 'obisResponseTime', type: 'string' }
                ]
            });

            var meterGridColModel = new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    menuDisabled: true
                },
                columns: [{
                    header: "<fmt:message key='aimir.poc.targetNode'/>",
                    dataIndex: 'targetNode',
                    align:'center',
                    width: 55,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.ipv6address'/>",
                    dataIndex: 'ipAddr',
                    align:'center',
                    width: 55,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.dateTime'/>",
                    dataIndex: 'dateTime',
                    align:'center',
                    width: 85,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.command'/>",
                    dataIndex: 'command',
                    align:'center',
                    width: 85,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.antAttenuation'/>",
                    dataIndex: 'antAttenuation',
                    align:'center',
                    width: 75,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.hops'/>",
                    dataIndex: 'hops',
                    align:'center',
                    width: 95,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.loss'/>",
                    dataIndex: 'loss',
                    align:'center',
                    width: 110,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.rssi'/>",
                    dataIndex: 'rssi',
                    align:'center',
                    width: 95,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.rtt'/>",
                    dataIndex: 'rtt',
                    align:'center',
                    width: 100,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.txPower'/>",
                    dataIndex: 'txPower',
                    align:'center',
                    width: 75,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.temperature.c'/>",
                    dataIndex: 'temperature',
                    align:'center',
                    width: 95,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.weather'/>",
                    dataIndex: 'weather',
                    align:'center',
                    width: 125,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.linkBudget'/>",
                    dataIndex: 'linkBudget',
                    align:'center',
                    width: 75,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.coapResponseTime'/>",
                    dataIndex: 'coapResponseTime',
                    align:'center',
                    width: 95,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.obisValue'/>",
                    dataIndex: 'obisValue',
                    align:'center',
                    width: 95,
                    sortable: true,
		            renderer: changeValue2
                },{
                    header: "<fmt:message key='aimir.poc.res.obisResponseTime'/>",
                    dataIndex: 'obisResponseTime',
                    align:'center',
                    width: 110,
                    sortable: true,
		            renderer: changeValue2
                }]
            });

            var meterGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : width,
                height : 350,
                store : meterGridStore,
                colModel : meterGridColModel,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'resultHistory',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : meterGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            var exportButton = new Ext.ux.Exporter.Button({
                component : meterGrid,
                text      : "Download as .xls"
            });
              
            meterGrid.getBottomToolbar().add(exportButton);
            
        };
        renderGrid();
    }
</script>
</head>
<body onLoad="javascript:hide()">

<div id="pocTestTab">
    <ul>
        <li><a href="#test" id="_test"><fmt:message key="aimir.poc.test" /></a></li>
        <li><a href="#result" id="_result"><fmt:message key="aimir.poc.result" /></a></li>
    </ul>

    <div id="test" class="tabcontentsbox">
        <div id="test_dialog" class="mvm-popwin-iframe-outer" title="<fmt:message key='aimir.poc.test'/>">
            <div class="headspace">
                <label class="check"><fmt:message key="aimir.poc.option"/></label>
            </div>
            <br style="clear:both;">
            <div class="testoption-container search-bg-withtabs">
                <table class="testoption wfree" >
                    <tr>
                        <td class="gray11pt withinput" style="width: 90px"><fmt:message key="aimir.poc.targetNode"/></td>
                        <td>
                            <select id="targetNode" name="select" style="width:200px;">
                            </select>
                        </td>
                        <td class="space20"></td>
                        <td class="gray11pt withinput" style="width:80px;"><fmt:message key="aimir.poc.pingCount"/></td>
                        <td><input id="pingCount" type="text" value=1 style="width:120px;"></td>
                        <td class="space20"></td>
                        <td class="gray11pt withinput" style="width: 95px"><fmt:message key="aimir.poc.temperature.c"/></td>
                        <td><input id="temperature" type="text" value=0 style="width:120px;"></td>
                    </tr>
                    <tr>
                        <td class="gray11pt withinput" style="width: 95px"><fmt:message key="aimir.poc.antAttenuation"/></td>
                        <td><input id="antAttenuation" type="text" value=0 style="width:120px;"></td>
                        <td class="space20"></td>
                        <td class="gray11pt withinput" style="width: 85px"><fmt:message key="aimir.poc.pingBytes"/></td>
                        <td><input id="pingBytes" type="text" value=56 style="width:120px;">
                        <td class="space20"></td>
                        <td class="gray11pt withinput" style="width:90px"><fmt:message key="aimir.poc.weather"/></td>
                        <td>
                            <select id="weather" name="select" style="width:120px;">
                                <option value='Sunny'>Sunny</option>
                                <option value='Cloudy'>Cloudy</option>
                                <option value='Rainy'>Rainy</option>
                                <option value='Stormy'>Stormy</option>
                                <option value='Snow'>Snow</option>
                                <option value='Hot'>Hot</option>
                                <option value='Cold'>Cold</option>
                                <option value='Dry'>Dry</option>
                                <option value='Windy'>Windy</option>
                                <option value='Wet'>Wet</option>
                            </select>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="headspace">
                <label class="check"><fmt:message key="aimir.poc.command"/></label>
            </div>
            <br style="clear:both;">
            <div class="testcommand-container">
                <div id="btn" class="btn_left margin-t10px">
                    <ul><li><a href="javascript:executeICMP6Ping();" class="on"><fmt:message key="aimir.poc.command.icmp6"/></a></li></ul>
                    <ul><li><a href="javascript:executeCOAPPing();" class="on"><fmt:message key="aimir.poc.command.coap"/></a></li></ul>
                    <ul><li><a href="javascript:executeOBISCodeReading();" class="on"><fmt:message key="aimir.poc.command.obis"/></a></li></ul>
                </div>
            </div>
            <br style="clear:both;">
            <div id="testResult">
            </div>
        </div>
    </div>

    <div id="result" class="tabcontentsbox">
        <div id="result_dialog" class="mvm-popwin-iframe-outer" title="<fmt:message key='aimir.poc.result'/>">
            <div class="headspace">
                <label class="check"><fmt:message key="aimir.poc.result"/></label>
            </div>
            <br style="clear:both;">
            <div class="resultoption-container search-bg-withtabs">
                <table class="resultoption wfree" >
                    <tr>
                        <td class="gray11pt withinput" style="width: 90px"><fmt:message key="aimir.poc.command"/></td>
                        <td>
                            <select id="command" name="select" style="width:120px;">
                                 <option value="ALL"><fmt:message key="aimir.all"/></option>
                                 <option value="ICMP"><fmt:message key="aimir.poc.command.icmp6"/></option>
                                 <option value="COAP"><fmt:message key="aimir.poc.command.coap"/></option>
                                 <option value="OBIS"><fmt:message key="aimir.poc.command.obis"/></option>
                            </select>
                        </td>
                        <td class="space20"></td>
                        <td class="gray11pt withinput" style="width:90px;"><fmt:message key="aimir.poc.executionDate"/></td>
                        <td>
                            <span><input id="sExecutionStartDate" class="day" type="text"></span>
                            <span><input value="~" class="between" type="text"></span>
                            <span><input id="sExecutionEndDate" class="day" type="text"></span>
                            <input id="sExecutionStartDateHidden" type="hidden" />
                            <input id="sExecutionEndDateHidden" type="hidden" />
                        </td>
                        <td>
                            <div id="btn" class="btn_left">
                                <ul><li><a href="javascript:searchResult();" class="on"><fmt:message key="aimir.button.search"/></a></li></ul>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <br style="clear:both;">
            <div id="resultHistory">
            </div>
        </div>
    </div>

</div>
</body>
</html>
