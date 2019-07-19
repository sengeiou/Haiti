
<!--
Gadget : LP Report (EM) max
Desc : Metering Rate by LP. It is consider DCU or DSO.
sejin han
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!-- 
	http://172.16.10.111:8085/aimir-web/gadget/report/lpReportMaxGadget.do
 -->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGRA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>LP Report</title>
    
    <!-- STYLE -->
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

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

    <!-- LIB -->
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>    
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>    
</head>

<body>
<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">

var supplierId = "${supplierId}";
var operatorId = "${operatorId}";

/**
 * Ready
 */
 $(document).ready(function () {
     // 유저 세션 정보 가져오기
     $.getJSON('${ctx}/common/getUserInfo.do',
             function(json) {
                 if(json.supplierId != ""){
                     supplierId = json.supplierId;
                     //검색 옵션 초기화
                     clearSearchOptions();
                 }
             }
     );

     // Init
     setInitPage();
 });
 
 // 화면 초기 설정
 var setInitPage = function(){
	// SELECTBOX - Time Type
     $('#TimeTypeCombo').selectbox();
	
	
 }
 
//검색 초기화 버튼
 var clearSearchOptions = function(){
	 var date = new Date();
     var year = date.getFullYear();
     var month = date.getMonth() + 1;
     var day = date.getDate();

     if(("" + month).length == 1) month = "0" + month;
     if(("" + day).length == 1) day = "0" + day;

     var setDate      = year + "" + month + "" + day + "00";
     var dateFullName = "";
     var locDateFormat = "yymmdd";

     // Date
     //$('#SearchStartDate').val(setDate);
     //$('#SearchEndDate').val(setDate);

     // TextBox 초기화
     $('#DcuNameBox').val('');     
 }
 
//입력된 검색 조건 확인
 var getSearchCondition = function(){
     var arrayObj = Array();
     //cache
     arrayObj[0] = Math.random();
     //conditions
     arrayObj[1] = $('#DcuNameBox').val().trim();
     arrayObj[2] = $('#TimeTypeCombo').val();
     arrayObj[3] = $('#SearchStartDate').val();
     arrayObj[4] = $('#SearchEndDate').val();

     return arrayObj;
 }
 

//검색 버튼
 var meteringRateSearch = function(){	
	
     // LP 검침율
     getValidLpRateGrid();
     // 미터 검침율
     getValidMeterRateGrid();
 }
 
 	// LP 검침율
 	var lpRateStore;
    var lpRateCol;
    var lpRateGrid;
    var getValidLpRateGrid = function(){
        var conditionArray = getSearchCondition();
        var lrWidth = $('#LpResultDiv').width()*0.9;
        var pageSize = 10;

        lpRateStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/report/getValidLpRate.do",
            baseParams : {
                'supplierId' : supplierId,
                'dcuName' : conditionArray[1],
                'timeType' : conditionArray[2],
                'startDate' : conditionArray[3],
                'endDate' : conditionArray[4]
            },
            root: 'calc',
            fields: ['SYS_ID','FW_VER','MODEM_CNT','LP_CNT','DEVICEMODEL_ID','FW_REVISION'],
            listeners:{
                load: function(mStore, mRecord, mOptions){
                    //.....

                }
            }
        });

        lpRateCol = new Ext.grid.ColumnModel({
           columns: [
               {header: 'DCU ID', dataIndex: 'SYS_ID', renderer:nullCheck},
               {header: 'MODEM CNT', dataIndex: 'MODEM_CNT', renderer:nullCheck},
               {header: 'F/W Version', dataIndex: 'FW_VER', renderer:nullCheck},
               {header: 'F/W Revision', dataIndex: 'FW_REVISION', renderer:nullCheck},               
               {header: 'LP CNT', dataIndex: 'LP_CNT', renderer:nullCheck}
           ],
            defaults: {
                sortable : false,
                menuDisable : true,
                align : 'center',
                width : 120
            },
        });

        function nullCheck(val){
            if(val == null || val.length<1){
                val = "null";
                return val;
            }
            return val;
        }

        function convertColor(val){
            if(val.search('UP') >= 0){
                return '<p style="color:green;">'+val+'</p>';
            }else if(val.search('DOWN') >= 0){
                return '<p style="color:brown;">'+val+'</p>';
            }
            return val;
        }

        lpRateGrid = new Ext.grid.GridPanel({
            store : lpRateStore,
            colModel : lpRateCol,
            autoScroll : false,
            height : 450,
            width : lrWidth,
            stripeRows : true,
            columnLines : true,
            loadMask : {
                msg : 'Loading...'
            },
            viewConfig: {
                forceFit : true,
                enableRowBody : true,
                showPreview : true,
                emptyText : 'No data to display'
            },

            //paging bar
        });

        $('#LpRateGrid').html(' ');

        lpRateGrid.reconfigure(lpRateStore, lpRateCol);
        lpRateGrid.render('LpRateGrid');

    }
 
 // 미터 검침율
 	var meterRateStore;
    var meterRateCol;
    var meterRateGrid;
    var getValidMeterRateGrid = function(){
        var conditionArray = getSearchCondition();
        var mrWidth = $('#MeterResultDiv').width()*0.9;
        var pageSize = 10;

        meterRateStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/report/getValidMeterRate.do",
            baseParams : {
                'supplierId' : supplierId,
                'dcuName' : conditionArray[1],
                'timeType' : conditionArray[2],
                'startDate' : conditionArray[3],
                'endDate' : conditionArray[4]
            },
            root: 'calc',
            fields: ['SYS_ID','FW_VER','MODEM_CNT','METER_CNT','DEVICEMODEL_ID','FW_REVISION'],
            listeners:{
                load: function(mStore, mRecord, mOptions){
                    //.....

                }
            }
        });

        meterRateCol = new Ext.grid.ColumnModel({
           columns: [
        	   {header: 'DCU ID', dataIndex: 'SYS_ID', renderer:nullCheck},
        	   {header: 'MODEM CNT', dataIndex: 'MODEM_CNT', renderer:nullCheck},
        	   {header: 'F/W Version', dataIndex: 'FW_VER', renderer:nullCheck},
               {header: 'F/W Revision', dataIndex: 'FW_REVISION', renderer:nullCheck},               
               {header: 'METER CNT', dataIndex: 'METER_CNT', renderer:nullCheck}
           ],
            defaults: {
                sortable : false,
                menuDisable : true,
                align : 'center',
                width : 120
            },
        });

        function nullCheck(val){
            if(val == null || val.length<1){
                val = "null";
                return val;
            }
            return val;
        }

        function convertColor(val){
            if(val.search('UP') >= 0){
                return '<p style="color:green;">'+val+'</p>';
            }else if(val.search('DOWN') >= 0){
                return '<p style="color:brown;">'+val+'</p>';
            }
            return val;
        }

        meterRateGrid = new Ext.grid.GridPanel({
            store : meterRateStore,
            colModel : meterRateCol,
            autoScroll : false,
            height : 450,
            width : mrWidth,
            stripeRows : true,
            columnLines : true,
            loadMask : {
                msg : 'Loading...'
            },
            viewConfig: {
                forceFit : true,
                enableRowBody : true,
                showPreview : true,
                emptyText : 'No data to display'
            },

            //paging bar
        });

        $('#MeterRateGrid').html(' ');

        meterRateGrid.reconfigure(meterRateStore, meterRateCol);
        meterRateGrid.render('MeterRateGrid');

    }



</script>

<!-- PAGE -->
<div id="wrapper" class="max" style="height:800px; overflow:auto;">
    <div id="SearchDiv" class="border_blu margin10px padding10px">
<!-- Section 1. 검색 [DCU-SysId, TimeType, SearchDate] -->        
        <label class="check"><fmt:message key="aimir.button.search"/></label>
        <!-- 검색 조건 시작 -->
        <div class="searchoption-container margin-t10px">
            <table class="searchoption wfree">
                <tr>
                    <td class="blue12pt padding-r10px">DCU</td>
                    
                    <td class="padding-r10px"><span>
	        		<input type="text" id="DcuNameBox" name="DcuNameBox" /></span>
                    </td>
                    
                    <td class="blue12pt padding-r10px">Time Type</td>
                    <td class="padding-r10px">
                    	<select id="TimeTypeCombo" name="TimeTypeCombo" class="selectbox" style="width:160px">
    					    <option value="0" selected>LP Time(metering)</option>
    					    <option value="1" >Save Time(writedate)</option>
    				</td>
                    
                    <td class="blue12pt padding-r10px">Search Date</td>
                    <td><input id="SearchStartDate" class="day" type="text"></td>
                    <td class="blue12pt">~ </td>
                    <td><input id="SearchEndDate" class="day" type="text"></td>
                                        
                </tr>
            </table>
        </div>
        <span id="SearchStartDateHidden" style="visible:hidden;"></span>
        <span id="SearchEndDateHidden" style="visible:hidden;"></span>
        <div class="dashedline"></div>
        
        <span class="padding-r10px"><span class="am_button">
			<a href="javascript:meteringRateSearch()"><fmt:message key="aimir.button.search"/></a></span>
        </span>
	                       
        <span class="padding-r10px"><span class="am_button">
			<a href="javascript:clearSearchOptions()"><fmt:message key="aimir.button.initialize"/></a></span>
        </span>
	

            <div class="margin-t10px padding-t7px padding-left3px"></div>
	</div>
<!-- 검색 조건 끝 -->        

<!-- Section 2. LP 기준 검침율 리스트 -->
	<div id="LpResultDiv" class="border_blu margin10px padding10px">
       
            <label class="check">LP Rate</label>
            <div id="LpRateGrid" class="margin-t10px padding-t7px padding-left3px">
                <label class="graybold11pt"><fmt:message key="aimir.extjs.empty"/></label>
            </div>
       
    </div>

<!-- Section 3. Meter 기준 검침율 리스트 -->
	<div id="MeterResultDiv" class="border_blu margin10px padding10px">
      
            <label class="check">Meter Rate </label>
            <div id="MeterRateGrid" class="margin-t10px padding-t7px padding-left3px">
                <label class="graybold11pt"><fmt:message key="aimir.extjs.empty"/></label>
            </div>
      
    </div>

</div>


</body>
</html>

