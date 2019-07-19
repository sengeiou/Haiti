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
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" >/*<![CDATA[*/

        // 페이지 로드시 로그인한 사용자의 공급사 ID 세팅
        var supplierId = ${supplierId};
        var serviceType = ServiceType.Electricity;
        var sendFlag = false;
        var today = "";
        var chromeColAdd = 2;

        function dateDelay(){
            if(supplierId != ''){
                clearInterval(dateInterval);
                conditionInit();
            }
        }

        $(document).ready(function(){
            Ext.QuickTips.init();
            hide();
            //공급사ID
            //로그인한 사용자정보를 조회한다.
            $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;

                        $('#supplierList').hide();
                        $('.space5').hide();
                    }else{
                        getSupplierList();
                    }
                }
            );
            dateInterval = setInterval("dateDelay()",100);
            updateFChart();
            getAbnormalContractUsageEmMiniGrid();
        });


             //윈도우 리싸이즈시 event
        $(window).resize(function() {
 
        fcChartRender();
        //리싸이즈시 패널 인스턴스 kill & reload
        abnormalContractUsageEmMiniGrid.destroy();
        abnormalContractUsageEmMiniGridOn = false;
        
        getAbnormalContractUsageEmMiniGrid();
                
        });

        function conditionInit(){

            //getTariffTypeList();
            $("#tariffType").selectbox();

            // 달력붙이기
            locDateFormat = "yymmdd";
            $("#dailyStartDate").datepicker({
                maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
                dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText);}
            });

            $.getJSON("${ctx}/common/getYear.do"
                    ,{supplierId:supplierId}
                    ,function(json) {
                         var currDate  = json.currDate;
                         today = currDate;
						$('#dailyStartDate').val(today);
						convertSearchDate();
            });

            // 일자별,월별 좌우 화살표클릭 이벤트 생성
            $(function() { $('#dailyLeft') .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),-1); } ); });
            $(function() { $('#dailyRight').bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),1); } ); });

            // 콤보 선택 시 request send
            $('#supplierList')   .change(function() { if(sendFlag || $('#supplierList').val() > 0) send(); });
            $('#tariffType')     .change(function() { if(sendFlag || $('#tariffType').val() > 0) send(); });
            $('#searchStartDate').change(function() { if(sendFlag) send(); });

        }

        /**
         * 일별 화살표처리
         */
        function dailyArrow(bfDate,val){
            $.getJSON("${ctx}/common/getDate.do"
                    ,{searchDate:bfDate, addVal:val, supplierId:supplierId}
                    ,function(json) {
                        $('#dailyStartDate').val(json.searchDate);
                        convertSearchDate();
                    });
        }

        // datepicker로 선택한 날짜의 포맷 변경
        function modifyDate(setDate){
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $("#dailyStartDate").val(json.localDate);
                        convertSearchDate();
                    });
        }

        /**
         *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
         */
        function convertSearchDate(){
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#dailyStartDate').val(), searchEndDate:$('#dailyStartDate').val(), supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.searchStartDate);
                        $('#searchEndDate').val(json.searchEndDate);
                        $('#searchStartDate').trigger('change');
                    });
        }

        /**
         * 공급사 콤보 생성
         */
        function getSupplierList() {
            $.getJSON('${ctx}/gadget/mvm/getSuppliers.do',
                    function(json) {
                       $('#supplierList').loadSelect(json.supplierList);
                       $("#supplierList option:eq(0)").replaceWith("<option value=0><fmt:message key='aimir.supplier'/></option>");
                       if(supplierId == ""){
                           $("#supplierList").val(0);
                       }else{
                           $("#supplierList").val(supplierId);
                       }
                       $("#supplierList").selectbox();
                    }
            );
        }

        /**
         * 계약종별 콤보 생성
         */
        function getTariffTypeList() {
            $.getJSON('${ctx}/gadget/mvm/getTariffTypes.do', {serviceType:serviceType, supplierId:supplierId},
                    function(json) {
                       $('#tariffType').loadSelect(json.tariffTypes);
                       $("#tariffType option:eq(0)").replaceWith("<option value=0><fmt:message key='aimir.contract.tariff.type'/></option>");
                       $("#tariffType").val(0);
                       $("#tariffType").selectbox();
                    }
            );
        }

        /**
         * 날짜 포맷팅
         */
        function padZero(num,leng) {
            var zero = leng-(""+num).length;
            if (typeof(num) == "number" && zero > 0) {
                var tmp = "";
                for (var i=0; i<zero; i++) tmp += "0";
                return tmp + num;
            }
            else {
                return num;
            }
        }

        /**
         * request send
         */
        function send() {

            if($('#supplierList').val() > 0){
                supplierId = $('#supplierList').val();
            }
            else{
                $('#supplierList').val(supplierId);
            }

            if($('#dailyStartDate').val() != "") {
            	updateFChart();
	            getAbnormalContractUsageEmMiniGrid();
            }
        }

        /**
         * fmt message
         */
        function getFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";                 // 번호
            fmtMessage[1] = "<fmt:message key="aimir.contract.tariff.type"/>";   // 계약 종별
            fmtMessage[2] = "<fmt:message key="aimir.contract.wattage.over"/>";  // 계약전력 이상
            fmtMessage[3] = "<fmt:message key="aimir.normal"/>";                 // 정상
            fmtMessage[4] = "<fmt:message key="aimir.over"/>";                   // 이상
            fmtMessage[5] = "<fmt:message key="aimir.contract"/>";               // 계약
            fmtMessage[6] = "<fmt:message key="aimir.contract.totalCnt"/>";      // 전체 계약
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

            return fmtMessage;
        }

        /**
         * request parameter
         */
        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            if($('#supplierList').val() > 0){
                condArray[cnt++] = $('#supplierList').val();
            }
            else{
                condArray[cnt++] = supplierId;
                $('#supplierList').val(supplierId);
            }
            condArray[cnt++] = $('#tariffType').val();
            condArray[cnt++] = $('#searchStartDate').val();

            return condArray;
        }

        function getConditionSupplierId() {
        	if($('#supplierList').val() > 0){
                return $('#supplierList').val();
            }
            else{                
                $('#supplierList').val(supplierId);
                return supplierId;
            }
        }

        function sendEnd(){
            if(!sendFlag) sendFlag = true;
        }
        
        function updateFChart() {
        	emergePre();
        	
       	    $.getJSON('${ctx}/gadget/mvm/getAbnormalContractUsageEM.do'
       	    	    ,{supplierId:getConditionSupplierId(),
       	    	    	tariffType:$('#tariffType').val(),
       	    	    	yyyymmdd:$('#searchStartDate').val()}
    				,function(json) {
        				 var total = json.total;
                         var list = json.grid;
                         fcChartDataXml = "<chart "
                        	 + "showValues='1' "
                        	 + "showPercentValues='1' "
        					 + "showPercentInToolTip='0' "
        				     + "showZeroPies='0' "
        				     + "showLabels='1' "
        				     + "showValues='1' "
        				     + "enableSmartLabels='0' "
        				     + "labelDistance='-40' "
        				     + fChartStyle_Common
                        	 + fChartStyle_Font
                             + fChartStyle_Pie3D
                             + ">";
                    	 var labels = "";

 						 var over = 0;
                        
 						 for( var index=0; index<list.length;index++){
						 	over += list[index].count;
                         }
						
						 if(total > 0) {
                  	 		labels += "<set label='<fmt:message key='aimir.normal'/>' value='"+(total - over)+"' />"
                      	 		+ "<set label='<fmt:message key='aimir.over'/>' value='"+over+"' />"
						 } else {
							 labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
						 }
                        	 	
                         fcChartDataXml += labels + "</chart>";
                         
                         fcChartRender();

                         $('#lb_total').text("<fmt:message key='aimir.contract.totalCnt'/> : " + total);
                    }
       	    );

       		hide();
    	}

        function fcChartRender() {
        	if($('#fcChartDiv').is(':visible')) {
		    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myChartId", $('#fcChartParentDiv').width() - ($('#abnormalContractUsageEmChartDiv').width())  , "120", "0", "0");
	            fcChart.setDataXML(fcChartDataXml);
	            fcChart.setTransparent("transparent");
	            fcChart.render("fcChartDiv");
        	}
        }

                //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        function displayCount(value, metaData, record, index){

            var param = record.data;
            var percentage = 0;
            var total = param.total;
            percentage =  Math.round(((value*100)/total ) * 10) / 10;
                
                if(percentage == 0){
                    return "0 / "+ total + " (0%)";
                }
                else{
                    return value+ " / " + total + " (" + percentage + "%)";
                }
        }

        var abnormalContractUsageEmMiniGridStore;
        var abnormalContractUsageEmMiniGridColModel;
        var abnormalContractUsageEmMiniGridOn = false;
        var abnormalContractUsageEmMiniGrid;
        //AbnormalContractUsageEmMini 그리드
        function getAbnormalContractUsageEmMiniGrid(){

            var arrayObj = getCondition();
            var message  = getFmtMessage();

            var width = $("#abnormalContractUsageEmMiniGridDiv").width(); 

             abnormalContractUsageEmMiniGridStore = new Ext.data.JsonStore({
                autoLoad:true,
                url: "${ctx}/gadget/mvm/getAbnormalContractUsageEM.do",
                baseParams:{
                    supplierId    : arrayObj[0],
                    tariffType    : arrayObj[1],
                    yyyymmdd      : arrayObj[2]
                },
                root:'grid',
                 fields: [
                { name: 'no', type: 'Integer' },
                { name: 'tarifftypeName', type: 'String' },
                { name: 'count', type: 'String' },
                { name: 'total', type: 'Integer' }
                ]
            });

            abnormalContractUsageEmMiniGridColModel = new Ext.grid.ColumnModel({
               
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
                        dataIndex:'tarifftypeName',
                        width: 10,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        tooltip:message[2],
                        dataIndex:'count',
                        width: 10 ,
                        align:'center',
                        renderer: displayCount
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                    ,renderer: addTooltip
                }
            });

            if (abnormalContractUsageEmMiniGridOn == false) {
               
                abnormalContractUsageEmMiniGrid = new Ext.grid.GridPanel({
                   
                    id: 'abnormalContractUsageEmMiniGrid',
                    store: abnormalContractUsageEmMiniGridStore,
                    cm : abnormalContractUsageEmMiniGridColModel,
                    autoScroll: true,
                    width: width,
                    height: 130,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'abnormalContractUsageEmMiniGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    }
                });
               
                abnormalContractUsageEmMiniGridOn  = true;

            } else {
                
                abnormalContractUsageEmMiniGrid.setWidth(width);
                abnormalContractUsageEmMiniGrid.reconfigure(abnormalContractUsageEmMiniGridStore, abnormalContractUsageEmMiniGridColModel);
            }
            
        };
    /*]]>*/
    </script>
</head>
<body>

    <div class="search-bg-basic">
		<ul class="basic-ul">
            <li class="basic-li"><input id="searchStartDate" type="hidden"/></li>
            <li class="basic-li"><input id="searchEndDate" type="hidden" /></li>
			<li class="basic-li"><select id="supplierList" style="width:140px;"></select></li>
			<li class="basic-li space5"></li>
			<li class="basic-li">
                <select id="tariffType" style="width:220px;">
                    <option value=0><fmt:message key='aimir.contract.tariff.type'/></option>
                    <c:forEach var="tariffTypes" items="${tariffTypes}">
                    <option value="${tariffTypes.id}">${tariffTypes.name}</option>
                    </c:forEach>
                </select>
			</li>
			<li class="basic-li"><button id="dailyLeft" type="button" class="back"></button></li>
			<li class="basic-li"><input id="dailyStartDate" type="text" class="day" readonly="readonly" onChange="javascript:send();"></li>
			<li class="basic-li"><button id="dailyRight" type="button" class="next"></button></li>
		</ul>
    </div>

    <div id="gadget_body">
    	<div id="fcChartParentDiv">
			<div class="textalign-center">
				<div class="width-auto margin-r10">
			    	<div id="fcChartDiv" >
					    The chart will appear within this DIV. This text will be replaced by the chart.	
					</div>
					<div class="lgnd_box bluebold11pt">
						<label id="lb_total"><fmt:message key="aimir.contract.totalCnt"/> : </label>
					</div>
				</div>
			</div>				

            <div id="abnormalContractUsageEmMiniGridDiv" class="margin-t5px"></div>
	</div>

</body>
</html>
