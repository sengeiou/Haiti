<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

   <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
   <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
   <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />

   <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
   <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
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

        /*.x-grid3-scroller {overflow-y: scroll;}*/
       
    </style>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var supplierId="";
        var operatorId = "";
        var lpinterval;
        var meterType;
        var item;
        var yyyymmdd;
        var channel;
        var mdevType;
        var mdevId;
        var dst;
        var chromeColAdd = 2;
        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                        operatorId = parseInt(json.operatorId);
                        getVEEAutoEstimationGridData();
                    }
                }
        );

        $.ajaxSetup({
	        async: false
	    });

        $(document).ready(function(){
        	Ext.QuickTips.init();
			showTip();
        });
        
        function getFmtMessage(){
            var fmtMessage = new Array();
            fmtMessage[0]= "<fmt:message key="aimir.readingDay"/>";//"검침일";
            fmtMessage[1]= "<fmt:message key="aimir.number"/>";       // 번호
            fmtMessage[2]= "<fmt:message key="aimir.readingDay"/>";//"검침일";
            fmtMessage[3]= "<fmt:message key="aimir.channel"/>";       // 채널
            return fmtMessage;
        }

        function getLpCondition() {

        	var cnt = 0;
        	var condArray = new Array();
        	
            condArray[cnt++] = $('#meterType').val();
            condArray[cnt++] = $('#item').val();
            condArray[cnt++] = $('#yyyymmdd').val();
            condArray[cnt++] = $('#channel').val();
            condArray[cnt++] = $('#mdevType').val();
            condArray[cnt++] = $('#mdevId').val();
            condArray[cnt++] = $('#dst').val();
            condArray[cnt++] = supplierId;            
            
            return condArray;
        }

        function getAutoEstimationCondition() {

        	var cnt = 0;
        	var condArray = new Array();
        	
            condArray[cnt++] = $('#meterType').val();
            condArray[cnt++] = $('#item').val();
            condArray[cnt++] = $('#yyyymmdd').val();
            condArray[cnt++] = $('#channel').val();
            condArray[cnt++] = $('#mdevType').val();
            condArray[cnt++] = $('#mdevId').val();
            condArray[cnt++] = $('#dst').val();
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#type1').val();
            condArray[cnt++] = $('#type2').val();
            condArray[cnt++] = operatorId;
            
            return condArray;
        }

        function showTip() {
			if($('#type1').val() == "0") {
				$('#descTip').text("Last Day Mode");
			} else if($('#type1').val() == "1") {
				$('#descTip').text("Last Week Mode");
			} else if($('#type1').val() == "2") {
				$('#descTip').text("Last Month Mode");
			} else if($('#type1').val() == "3") {
				$('#descTip').text("Current Month Mode");
			}
        }

        function preView() {
          var arrayObj = getAutoEstimationCondition();
        	
          $.getJSON('${ctx}/gadget/mvm/getPreviewAutoEstimation.do',
                {
                    meterType  : arrayObj[0],
                    item       : arrayObj[1],
                    yyyymmdd   : arrayObj[2],
                    channel    : arrayObj[3],
                    mdevType   : arrayObj[4],
                    mdevId     : arrayObj[5],
                    dst        : arrayObj[6],
                    supplierId : arrayObj[7],
                    type1      : arrayObj[8],
                    type2      : arrayObj[9],
                    userId     : arrayObj[10]
                },function(json) {
  
                    if(json.result.lpList != null){
                        lpinterval = json.result.lpInterval;
                        veeAutoEstimationGridData = json.result.lpList;
                        getVEEAutoEstimationMaxGrid();
                    }else if(json.result.lpList == null){
                         Ext.Msg.alert("", "There is no data in the previous interval.");
                    }
                }
            );
        }

        function updateData() {
          var arrayObj = getAutoEstimationCondition();
        	$.getJSON('${ctx}/gadget/mvm/updateVEEAutoEstimationData.do',
                {
                    meterType  : arrayObj[0],
                    item       : arrayObj[1],
                    yyyymmdd   : arrayObj[2],
                    channel    : arrayObj[3],
                    mdevType   : arrayObj[4],
                    mdevId     : arrayObj[5],
                    dst        : arrayObj[6],
                    supplierId : arrayObj[7],
                    type1      : arrayObj[8],
                    type2      : arrayObj[9],
                    userId     : arrayObj[10]
                },function(json) {

                    if(json.result.lpList != null){
                        lpinterval = json.result.lpInterval;
                        veeAutoEstimationGridData = json.result.lpList;
                        Ext.Msg.alert("", "Success");
                        getVEEAutoEstimationGridData();
                    }else if(json.result.lpList == null){
                         Ext.Msg.alert("", "Fail");
                    }
                }
            );
        }

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        //VEEAutoEstimationMax그리드
        var veeAutoEstimationMaxGridStore;
        var veeAutoEstimationMaxGridColModel;
        var veeAutoEstimationMaxGridOn = false;
        var veeAutoEstimationMaxGrid;

        var veeAutoEstimationGridData = [];
        function getVEEAutoEstimationGridData(){
            var arrayObj = getLpCondition();
             $.getJSON('${ctx}/gadget/mvm/getLpData.do'
                , { meterType     : arrayObj[0],
                    table         : arrayObj[1],
                    yyyymmdd      : arrayObj[2],
                    channel       : arrayObj[3],
                    mdevType      : arrayObj[4],
                    mdevId        : arrayObj[5],
                    dst           : arrayObj[6],
                    supplierId    : arrayObj[7]}
                , function(json) {
                      lpinterval = json.lpinterval;
                      veeAutoEstimationGridData = json.gridData;
                      getVEEAutoEstimationMaxGrid();
                  });
        }

        function getVEEAutoEstimationMaxGrid(){


        	 var arrayObj = getLpCondition();
             veeAutoEstimationMaxGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 24}},
                data: veeAutoEstimationGridData,
                root:'',
                 fields: ["yyyymmddhh","channel","mdev_type","mdev_id","dst","realData",
                 "yyyymmdd","hh",
                 "value_00","value_01","value_02","value_03","value_04","value_05","value_06","value_07","value_08","value_09",
                 "value_10","value_11","value_12","value_13","value_14","value_15","value_16","value_17","value_18","value_19",
                  "value_20","value_21","value_22","value_23","value_24","value_25","value_26","value_27","value_28","value_29",
                  "value_30","value_31","value_32","value_33","value_34","value_35","value_36","value_37","value_38","value_39",
                  "value_40","value_41","value_42","value_43","value_44","value_45","value_46","value_47","value_48","value_49",
                  "value_50","value_51","value_52","value_53","value_54","value_55","value_56","value_57","value_58","value_59"
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

            makeveeAutoEstimationMaxGrid();
        }

        function makeveeAutoEstimationMaxGrid(){

            var fmtMessage  = getFmtMessage();
            var width = $("#VEEAutoEstimationDiv").width(); 

           var columns = [];
             columns.push(
                {header: fmtMessage[1], 
                 tooltip: fmtMessage[1],
                 align:'center', 
                 width: 50,
                 renderer: function(value, me, record, rowNumber, rowIndex, store) {
                    return rowNumber+1;
                 }
               }
               ,{header: fmtMessage[2], 
                   tooltip: fmtMessage[2],
                   dataIndex: 'yyyymmddhh', 
                   align:'center', 
                   width:90
                }

                ,{header: fmtMessage[3], 
                   tooltip: fmtMessage[3],
                   dataIndex: 'channel', 
                   align:'center', 
                   width: 60
                }
                ,{header: "dst", 
                   tooltip: "dst",
                   dataIndex: 'dst', 
                   align:'center', 
                   width: 50
                }
                ,{header: "realData", 
                   tooltip: "realData",
                   dataIndex: 'realData', 
                   align:'center', 
                   width: 60
                });

  
            var colinterval;
            if(lpinterval == 1){
                colinterval = 80;
            }else{
                colinterval = (width-310)/(60/lpinterval);
            }
        
            if(lpinterval != null){
                for(var i=0;i<60; i=i+Number(lpinterval)){
                    var index = i<10?'0'+i:i;
                    columns.push({
                        header: index+"MM", 
                        tooltip:index+"MM", 
                        dataIndex: 'value_' + index,
                        align:'center', 
                        width: colinterval});
                    
                   
                }
            }

            veeAutoEstimationMaxGridColModel = new Ext.grid.ColumnModel({
                columns:columns,
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-20)/(15))-chromeColAdd
                    ,renderer: addTooltip
                }
            });

            if (veeAutoEstimationMaxGridOn == false) {
                
                veeAutoEstimationMaxGrid = new Ext.grid.GridPanel({
                    layout: 'fit',
                    id: 'VEEAutoEstimationGrid',
                    store: veeAutoEstimationMaxGridStore,
                    colModel : veeAutoEstimationMaxGridColModel, 
                    autoScroll: true,
                    width: width,
                    height: 610,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'VEEAutoEstimationDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } 
                });
                veeAutoEstimationMaxGridOn  = true;
            } else {
                
                veeAutoEstimationMaxGrid.setWidth(width);
                veeAutoEstimationMaxGrid.reconfigure(veeAutoEstimationMaxGridStore, veeAutoEstimationMaxGridColModel);
            }
        }
        /*]]>*/
    </script>

</head>
<body>

	<div class="mvm-popwin-body" style="width:860px;">
	
	
		<!-- search-background DIV (S) -->
		<div class="search-bg-withtabs">	
			<div class="searchoption-container">
				<table class="searchoption wfree">
					<tr>
						<td>
							<select id="type1" style="width:160px;" onchange="javascript:showTip();">
								<option value="0"><fmt:message key="aimir.vee.autoestimation.lastDay"/></option>
								<option value="1"><fmt:message key="aimir.vee.autoestimation.lastWeek"/></option>
								<option value="2"><fmt:message key="aimir.vee.autoestimation.lastMonth"/></option>
								<option value="3"><fmt:message key="aimir.vee.autoestimation.currMonth"/></option>
							</select>
						</td>
						<td class="space20"></td>
						<td>
							<select id="type2" style="width:160px;">								
								<option value="same"><fmt:message key="aimir.vee.autoestimation.same"/></option>
								<option value="avg"><fmt:message key="aimir.vee.autoestimation.avg"/></option>
							</select>
						</td>
						<td class="space20"></td>
						<td>
							<div class="btn"><ul><li><a href="javascript:preView();" class="on" id="btnPre"><fmt:message key="aimir.vee.autoestimation.btn.preview"/></a></li></ul></div>
						</td>
						<td>
							<div class="btn"><ul><li><a href="javascript:updateData();" class="on" id="btnUpdate"><fmt:message key="aimir.vee.autoestimation.btn.update"/></a></li></ul></div>
						</td>
					</tr>
					<tr style="height:50px">
						<td>
							<label id="descTip"></label>
						</td>
					</tr>
				</table>
			</div>		
		</div>
		<!--상세검색 끝-->
	
		<div class="gadget_body">
			<div id="VEEAutoEstimationDiv"></div>
		</div>
	
		<input type="hidden" id="meterType" value="${meterType}" />
		<input type="hidden" id="item" value="${item}" />
		<input type="hidden" id="yyyymmdd" value="${yyyymmdd}" />
		<input type="hidden" id="channel" value="${channel}" />
		<input type="hidden" id="mdevType" value="${mdevType}" />
		<input type="hidden" id="mdevId" value="${mdevId}" />
		<input type="hidden" id="dst" value="${dst}" />
	
	</div>
</body>
</html>
