<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title></title>

<%@ include file="/gadget/system/preLoading.jsp"%>
    
<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>

<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

<script type="text/javascript">

    var operatorId = "${operatorId}";    // 고객 PK
    var serviceType = "${serviceType}";  // 서비스 타입 (전기/가스/수도)
    var isNotService;
    $(document).ready(function(){
        // ExtJS의 tooltip초기화
        Ext.QuickTips.init();
        $.ajaxSetup({
            async: false
        });
 
        emergePre();
        // 계약 정보 취득및 콤보 박스 생성
        getContract();

        hide();

        $.ajaxSetup({
            async: true
        });

    });


    // 계약 정보 취득및 콤보박스 생성
    var getContract = function() {

        var params = {
                "operatorId" : operatorId,
                "serviceType" : serviceType
        };

        $.getJSON("${ctx}/gadget/energySavingGoal/getSelect.do",
                params,
                function(result) {
            
                    isNotService = result.isNotService;
                    
                    var contracts = result.contracts;

                    // 계약정보 콤보박스 생성
                    $("#contracts").pureSelect(contracts);
                    $("#contracts").selectbox();

                    // 계약정보 콤보 박스 선택 이벤트
                    $("#contracts").bind("change", function(event) { getEnergySavingGoalResult(); } );
                }
            );

        if(isNotService) {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();

            if(serviceType == "3.1") {
                $("#img_isNotService_house").addClass("img_isNotService_elec_house");
            } else if(serviceType == "3.2") {
                $("#img_isNotService_house").addClass("img_isNotService_water_house");
            } else if(serviceType == "3.3") {
                $("#img_isNotService_house").addClass("img_isNotService_gas_house");
            }
            
           return;
       } else { // 해당 가젯에 대한 권한이 있을때
           $("#isNotService").hide();
           // 에너지 절감 실적 정보 취득
           getEnergySavingGoalResult();
       }
    };

    // 챠트 레이아웃 속성 설정
    var chartStart = " "
        + " <chart formatNumberScale='0'"
        + " yAxisValuesStep='2' "
        + " yaxisname = '<fmt:message key='aimir.hems.label.usageFeeSymbol'/>'"
        + " chartLeftMargin = '0' "
		+ " chartRightMargin= '0' "
		+ " chartTopMargin= '0' "
		+ " chartBottomMargin= '0' "
        + fChartStyle_StColumn3D_nobg
        + fChartStyle_Font
        + ">";

    // 챠트 값 초기화
   // var data = [0, 0, 0, 0, 0];
    var array;

    // 에너지 절감 실적 정보 취득
    var getEnergySavingGoalResult = function() {

    	// 맥스와 동일한 컨트롤러 사용으로 인해 maxDay파라메터를 더미값으로 설정하여 넘겨 준다.
        var params = {
                "operatorContractId" : $("#contracts").val(),
                "maxDay" : ""
        };

        $.getJSON("${ctx}/gadget/energySavingGoal/getContractGrid.do",
                params,
                function(result) {

                    var count = result.gridData.length;
                    array = new Array;

                    for (var i = 0; i < count; i++) {

                        var gridData = result.gridData[i];
                        // 그리드의 값을 설정한다.
                        var arrayData = [gridData.day, 
                                            gridData.savingTarget, 
                                            gridData.maxBill, 
                                            gridData.lastMonthBill, 
                                            gridData.lastYearSameMonthBill, 
                                            gridData.bill, 
                                            gridData.rate];
                        array[i] = arrayData;
                    }
                    // 그리드를 생성한다.
                    renderGrid();
                }
            );
    };

    $(window).resize(function() {
       renderGrid();
    });

    var rate = function(val) {
        if (val >= 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    };

    var gridOn = false;
    var grid;
    var colModel;
    // 그리드 생성
    var renderGrid = function() {

    	// 그리드 넓이 설정
        var width = $("#grid").width();

        var store = new Ext.ux.data.PagingArrayStore({
            lastOptions: {params: {start: 0, limit: 10}},
            data : array,
            fields: ["day", 
                        "savingTarget", 
                        "maxBill", 
                        "lastMonthBill", 
                        "lastYearSameMonthBill", 
                        "bill", 
                        {name:"rate", type: "float"}]
        });

        /*
        store.load({
            params:{
                start:0,
                limit:5,
                dir: 'ASC'
            }
        });  

        var store = new Ext.data.ArrayStore({
            totalProperty : array.length,
            autoLoad : {params:{start:0, limit:5}},
            fields: ["day", "savingTarget", "maxBill", "lastMonthBill", "lastYearSameMonthBill", "bill", {name:"rate", type: "float"}]
        });

        store.loadData(array);
        */
        
        if (gridOn == false) {

            colModel = new Ext.grid.ColumnModel({
                defaults: {
                    //width: width / 4,
                    sortable: true,
                    menuDisabled: true
                },
                columns: [
                    //일자의 사이즈가 길기 때문에전체 그리드 넓이의 2/5를 차지하도록  width를 설정함
                    { id: "day", header: "<fmt:message key='aimir.hems.label.monthPeriod'/>", width: width*(2/5), dataIndex: "day", renderer:addTooltip}, 
                    { header: "<fmt:message key='aimir.hems.label.target'/>(<fmt:message key='aimir.price.unit'/>)", width: width/5, dataIndex: "savingTarget"}, 
                    { header: "<fmt:message key='aimir.hems.label.indicateMonth'/>(<fmt:message key='aimir.price.unit'/>)", width: width/5, dataIndex: "bill"},
                    { header: "<fmt:message key='aimir.energy.economical.rate'/>", width:(width/5)-4, renderer: rate, dataIndex: "rate"}
                ]
            });

            // ExtJS 그리드 생성
            grid = new Ext.grid.GridPanel({
                height: 200,
                store: store,
                colModel : colModel,
                width: width,
                stripeRows : true,
                columnLines: true,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
	            // paging bar on the bottom
	            bbar: new Ext.PagingToolbar({
	                pageSize: 10,
	                store: store,
	                displayInfo: true,
	                displayMsg: ' {0} - {1} / {2}'
	            })
            });

            grid.render("grid");

            // 행 클릭 이벤트 정의
            grid.on('rowclick', function(grid, rowIndex, e) {

                //index = rowIndex;
                // 페이징 되었을 경우, 선택한 rowIndex는 페이지 시작 (start)과 페이징된 그리드의 rowIndex를 더한 값이 된다.
                index = grid.getStore().lastOptions.params.start + rowIndex;
                // 챠트에 값 설정
                setGridChartData();
                // 챠트 생성
                renderGridChart();
            });  

            index = 0;
            gridOn = true;
        } else {
            grid.setWidth(width);
            grid.reconfigure(store, colModel);

            var bottomToolbar = grid.getBottomToolbar();
            bottomToolbar.bindStore(store);
        }
        // 챠트에 값 설정
        setGridChartData();
        // 챠트 생성
        renderGridChart();
    };

    var addTooltip = function(val, cell, record) {
        return '<div qtip="'+ val +'">'+ val +'</div>';     
    };

    // 챠트의 Y축 라벨 설정
    var gridChartLabel = " "
        + " <categories> "
        + " <category label='<fmt:message key='aimir.hems.label.target'/>' /> "              // 목표
        + " <category label='<fmt:message key='aimir.hems.label.maxBill'/>' /> "             // 최고
        + " <category label='<fmt:message key='aimir.hems.label.lastMonthBill'/>' /> "       // 전월
        + " <category label='<fmt:message key='aimir.hems.label.lastYearMonthBill'/>' /> "   // 전년도 동월
        + " <category label='<fmt:message key='aimir.hems.label.indicateMonth'/>' /> "       // 당월
        + " </categories> ";

    var gridChartData;
    
    var gridData;

    var index;
    // 그리드 값과 컬러 설정
    var setGridChartData = function() {

        var gridData = array[index];

        $("#girdChartDay").text(gridData[0]);
        $("#girdChartRate").text(gridData[6] + "% <fmt:message key='aimir.energy.economical'/>");

        gridChartData = " "
            + " <dataset> "
            + " <set value='" + num(gridData[1]) + "' color='" + fChartColor_Step5[4] + "' /> "    // 목표
            + " <set value='" + num(gridData[2]) + "' color='" + fChartColor_Step5[3] + "' /> "    // 최고
            + " <set value='" + num(gridData[3]) + "' color='" + fChartColor_Step5[2] + "' /> "    // 전월
            + " <set value='" + num(gridData[4]) + "' color='" + fChartColor_Step5[1] + "' /> "    // 전년도 동월
            + " <set value='" + num(gridData[5]) + "' color='" + fChartColor_Step5[0] + "' /> "    // 당월
            + " </dataset> ";
    };

    var num = function(value) {

        return value.replace(/,/gi,"");
    };

    var chartFinish = " "
        + " </chart> ";

        // 그리드 생성
    var renderGridChart = function() {
        var gridChartXml = chartStart + gridChartLabel + gridChartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#gridChartDiv").width(), "200", "0", "0" );
        myChart.setXMLData(gridChartXml);
        myChart.setTransparent("transparent");
        myChart.render("gridChartDiv");
    };

</script>

</head>
<body>
<div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span id="img_isNotService_house"></span></div>
            <div class="isNotService_today_right">
                <table height='160'>
                <tr>    
                    <td><fmt:message key='aimir.hems.label.isNotService'/></td>
                </tr>
                </table>
            </div>
        </div>
</div>
<div id="wrapper">

    <!--contract no.
    <div class="contract"  id="contractUl" style="display:none;">-->
	<div class="topsearch">
		<div class="contract borderbottom_blue">
		   	<table>
				<tr>
					<td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
					<td>
						<select id="contracts" style="width:280px"></select>
					</td>
				</tr>
			</table>
		</div>

		<!--<div class="top_line"></div>-->
	
		 <!--tab
		<div class="hems_tab">
		    <ul>
		        <li><a id="energySavingGoalsSettingTabId" title="<fmt:message key='aimir.hems.inform.mouseover.energySavingGoalsSettingTab'/>"><fmt:message key='aimir.hems.label.savingGoalsMgmt'/></a></li>
		        <li><a id="energySavingGoalsNotificationSettingTabId" title="<fmt:message key='aimir.hems.inform.mouseover.energySavingGoalsNotificationSettingTab'/>"><fmt:message key='aimir.operator.notificationSet'/></a></li>
		   </ul>
		</div>
		// tab -->
 
	</div>
	<!--contract no.-->

    <div class="today">
            <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.savingGoalsResult'/></div>
            <div class="goal2">
                <div class="saving_goal2">
                    <span class="grid_savingtxt"><span id="girdChartDay" ></span></span>
                    <span class="text_lightgray margin_side">|</span>
                    <span id="girdChartRate" class="grid_savingtxt text_orange2"></span>
               </div>
            </div>

            <div id="gridChartDiv" class="margin_t20"></div>

            <p class="saving_comment"> * <fmt:message key='aimir.hems.label.savingGoalHistory'/></p>
            <div id="grid"></div>

    </div>  

</div>

</body>
</html>