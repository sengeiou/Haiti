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

<script type="text/javascript">

	var operatorId = "${operatorId}";
    var serviceType = "${serviceType}";
	var maxDay;
	
	$(document).ready(function(){

	    emergePre();

        getSelect();

        hide();
	});

	var getSelect = function() {

	    var params = {
	            "operatorId" : operatorId,
	            "serviceType" : serviceType
	    };
	
	    $.getJSON("${ctx}/gadget/energySavingGoal/getSelect.do",
	            params,
	            function(result) {

	    			var contractCount = result.contractCount;
	    			var contracts = result.contracts;

					/*if ( 1 > contractCount ) {

						$("#contractUl").hide();
					} else if ( 1 == contractCount ) {

						$("#contracts").pureSelect(contracts);
						$("#contracts").selectbox();
						$("#contractUl").hide();
					} else {*/

						$("#contracts").pureSelect(contracts);
						$("#contracts").selectbox();
						//$("#contractUl").show();

						$("#contracts").bind("change", function(event) { getContract(); } );
					//}

					getContract();
			    }
	        );
	};

	var getContract = function() {

	    var params = {
	            "operatorContractId" : $("#contracts").val()
	    };
	
	    $.getJSON("${ctx}/gadget/energySavingGoal/getContract.do",
	            params,
	            function(result) {

            		if ( true == result.resultStatus ) {

                		maxDay = result.maxDay;

    	                $("#location").text(result.location);
    	                $("#tariff").text(result.tariffType);
    	                $("#status").text(result.status);
    	                $("#date").text(result.date);

    	                $("#savingTarget").val(result.savingTarget);

    	                if (result.sms == true) {

    	                	$("#sms").attr("checked", "checked");
    	                	$("#address").val(result.smsAddress);
    	                }

    	                if (result.eMail == true) {

    	                	$("#eMail").attr("checked", "checked");
    	                	$("#address").val(result.eMailAddress);
        	            }
        	            
    	                if (result.period1 == true) {

    	                	$("#period1").attr("checked", "checked");
        	            }
        	            
    	                if (result.period2 == true) {

    	                	$("#period2").attr("checked", "checked");
        	                $("#comValue").val(result.comValue);
        	            }
        	            
    	                if (result.period3 == true) {

    	                	$("#period3").attr("checked", "checked");
        	            }
        	            
    	                if (result.period4 == true) {

    	                	$("#period4").attr("checked", "checked");
        	            }
        	            
    	                if (result.period5 == true) {

    	                	$("#period5").attr("checked", "checked");
        	            }
    	                
    	                data[0] = result.savingTarget;
    	                data[1] = result.maxBill;
    	                data[2] = result.lastMonthBill;
    	                data[3] = result.lastYearSameMonthBill;
    	                data[4] = result.forecastBill;

    	                setChartData();
    	                renderChart();
    	                getContractGrid();
            		} else {

     					alert("계약 정보를 찾을 수 없습니다.");
            		}
				}
	        );
	};

	var setSavingTarget = function() {

	    var params = {
	            "operatorContractId" : $("#contracts").val(),
	            "savingTarget" : $("#savingTarget").val(),
	            "maxDay" : maxDay
	    };
	
	    $.getJSON("${ctx}/gadget/energySavingGoal/saveSavingTarget.do",
	            params,
	            function(result) {

	    			if (true == result.resultStatus) {
						data[0] = $("#savingTarget").val();
						array[0][1] = result.savingTarget;
						
						setChartData();
						renderChart();
						renderGrid();
						setNoticeTarget();
						
						if (0 == index) {
							
							setGridChartData();
							renderGridChart();
						}
	    			} else {

		    			alert("저장 실패");
	    			}
	    		}
	    	);
	};
	
    var chartStart = " "
        + " <chart formatNumberScale='0'"
        + fChartStyle_StColumn3D_nobg
        + fChartStyle_Font
        + ">";

	var chartLabel = " "
		+ " <categories> "
		+ " <category label='목표' /> "
		+ " <category label='최고' /> "
		+ " <category label='전월' /> "
		+ " <category label='전년도 동월' /> "
		+ " <category label='이번달 예측 요금' /> "
		+ " </categories> ";

	var chartData;
	
	var data = [0, 0, 0, 0, 0];
			
	var setChartData = function() {

		chartData = " "
			+ " <dataset> "
			+ " <set value='" + data[0] + "' color='" + fChartColor_Step5[0] + "' /> "
			+ " <set value='" + data[1] + "' color='" + fChartColor_Step5[1] + "' /> "
			+ " <set value='" + data[2] + "' color='" + fChartColor_Step5[2] + "' /> "
			+ " <set value='" + data[3] + "' color='" + fChartColor_Step5[3] + "' /> "
			+ " <set value='" + data[4] + "' color='" + fChartColor_Step5[4] + "' /> "
			+ " </dataset> ";
	};

	var gridChartLabel = " "
		+ " <categories> "
		+ " <category label='목표' /> "
		+ " <category label='최고' /> "
		+ " <category label='전월' /> "
		+ " <category label='전년도 동월' /> "
		+ " <category label='사용 요금(예측 요금)' /> "
		+ " </categories> ";

	var gridChartData;
	
	var gridData;

	var index;
	
	var setGridChartData = function() {

		var gridData = array[index];

        $("#girdChartDay").text(gridData[0]);
        $("#girdChartRate").text(gridData[6] + "% 절감");

        gridChartData = " "
			+ " <dataset> "
			+ " <set value='" + num(gridData[1]) + "' color='" + fChartColor_Step5[0] + "' /> "
			+ " <set value='" + num(gridData[2]) + "' color='" + fChartColor_Step5[1] + "' /> "
			+ " <set value='" + num(gridData[3]) + "' color='" + fChartColor_Step5[2] + "' /> "
			+ " <set value='" + num(gridData[4]) + "' color='" + fChartColor_Step5[3] + "' /> "
			+ " <set value='" + num(gridData[5]) + "' color='" + fChartColor_Step5[4] + "' /> "
			+ " </dataset> ";
	};
	
	var num = function(value) {

		return value.replace(/,/gi,"");
	};
	
	var chartFinish = " "
		+ " </chart> ";

	var array = new Array;
	 
	var getContractGrid = function() {
		
	    var params = {
	            "operatorContractId" : $("#contracts").val(),
	            "maxDay" : maxDay
	    };
	
	    $.getJSON("${ctx}/gadget/energySavingGoal/getContractGrid.do",
	            params,
	            function(result) {

            		var count = result.gridData.length;
            		
            		for (var i = 0; i < count; i++) {

						var gridData = result.gridData[i];
						var arrayData = [gridData.day, 
											gridData.savingTarget, 
											gridData.maxBill, 
											gridData.lastMonthBill, 
											gridData.lastYearSameMonthBill, 
											gridData.bill, 
											gridData.rate];
						array[i] = arrayData;
            		}

            		renderGrid();
	    		}
	    	);
	};
		
	$(window).resize(function() {
	    
	    renderChart();
	   // renderGrid();
	});
	
	var renderChart = function() {
		
		var chartXml = chartStart + chartLabel + chartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#chartDiv").width(), "230", "0", "0" );
        myChart.setXMLData(chartXml);
        myChart.setTransparent("transparent");
        myChart.render("chartDiv");
	};

	var renderGridChart = function() {
		
		var gridChartXml = chartStart + gridChartLabel + gridChartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#gridChartDiv").width(), "230", "0", "0" );
        myChart.setXMLData(gridChartXml);
        myChart.setTransparent("transparent");
        myChart.render("gridChartDiv");
	};

	var gridOn = false;
	var grid;
	
	var renderGrid = function() {
		
	    var width = $("#grid").width();
	    
	    var store = new Ext.data.ArrayStore({
	        fields: ["day", "savingTarget", "maxBill", "lastMonthBill", "lastYearSameMonthBill", "bill", {name:"rate", type: "float"}]
	    });
	
	    store.loadData(array);
	    
	    var rate = function(val) {
	        if (val > 0) {
	            return '<span style="color:green;">' + val + '%</span>';
	        } else if (val < 0) {
	            return '<span style="color:red;">' + val + '%</span>';
	        }
	        return val;
	    };
	    
	    var colModel = new Ext.grid.ColumnModel({
	        defaults: {
	            width: width / 7,
	            sortable: true
	        },
	        columns: [
	            { id: "day", header: "일자", dataIndex: "day"},
	            { header: "목표", dataIndex: "savingTarget"},
	            { header: "최고", dataIndex: "maxBill"},
	            { header: "전월", dataIndex: "lastMonthBill"},
	            { header: "전년도 동월", dataIndex: "lastYearSameMonthBill"},
	            { header: "사용요금(예측요금)", dataIndex: "bill"},
	            { header: "절감율", renderer: rate, dataIndex: "rate"}
	        ]
	    });
	    
	    if (gridOn == false) {
	        
	    	grid = new Ext.grid.GridPanel({
	            height: 300,
	            store: store,
	            colModel : colModel,
	            width: width,
	            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
	            title: "그리드"
	        });

	    	grid.render("grid");

            grid.on('rowclick', function(grid, rowIndex, e) {

            	index = rowIndex;
            	setGridChartData();
            	renderGridChart();
            });  

			index = 0;
			
            setGridChartData();
    		renderGridChart();
            
	    	gridOn = true;
	    } else {

	    	grid.setWidth(width);
	    	grid.reconfigure(store, colModel);
	    }
	};

    var checked = function(name1, name2) {

		if ($("#" + name1).is(":checked")) {
			
	        $("#" + name1).removeAttr("checked");
		} else {
			
	        $("#" + name1).attr("checked", "checked");
	        $("#" + name2).removeAttr("checked");
		}
    };

    var clicked = function(name1, name2) {

		if ($("#" + name1).is(":checked")) {
			
			if ($("#" + name2).is(":checked")) {
				
	        	$("#" + name2).removeAttr("checked");
			}
		}
    };
    
    var setNoticeTarget = function() {

	    var params = {
	            "operatorContractId" : $("#contracts").val(),
	            "maxDay" : maxDay,
	            "smsYn" : checkVal("sms"),
	            "eMailYn" : checkVal("eMail"),
	            "period_1" : checkVal("period1"),
	            "period_2" : checkVal("period2"),
	            "period_3" : checkVal("period3"),
	            "period_4" : checkVal("period4"),
	            "period_5" : checkVal("period5"),
	            "smsAddress" : $("#address").val(),
	            "eMailAddress" : $("#address").val(),
	            "conditionValue" : $("#comValue").val()
	    };
	
	    $.getJSON("${ctx}/gadget/energySavingGoal/saveNoticeTarget.do",
	            params,
	            function(result) {

	    			if (true == result.resultStatus) {

						alert("저장 성공");
	    			} else {

		    			alert("저장 실패");
	    			}
	    		}
	    	);
    };

    var checkVal = function(name) {

		if ($("#" + name).is(":checked")) {

			return true;
		} else {

			return false;
		}
    };

</script>

</head>
<body>
<div id="wrapper">

    <!--contract no.-->
    <div id="contractUl" class="contract_max">
    
    	<table>
			<tr>
				<td class="text_blue"><fmt:message key='aimir.contractNumber'/></td>
				<td>
					 <select id="contracts" style="width:500px" ></select>
				</td>
				<!--<td class="padding_l20 text_blue">
					<fmt:message key='aimir.address'/>
				</td>
				<td class="padding_side"> : </td>
                <td id="address" > </td>-->
			</tr>
		</table>
		
        <!-- ul id="contractUl" style="display:none;" >
            <li class="text_blue"><fmt:message key='aimir.contractNumber'/></li>
            <li>
                <select id="contracts" style="width:200px">
                </select>
            </li>
            <li></li>
        </ul-->
    </div>
    <!-- div class="contract_result">
    <div>
        <table>
            <colgroup>
            <col width="150px" />
            <col width="10px" />
            <col width="" />
            </colgroup>
            <tr>
                <th><span class="icon_square"></span><fmt:message key='aimir.address'/></th>
                <td class="vertical_top">: </td>
                <td id="address" ></td>
            </tr>
           <tr>
                <th class="vertical_top"><span class="icon_square"></span><fmt:message key='aimir.contract.tariff.type'/></th>
                <td class="vertical_top">: </td>
                <td id="tariff" ></td>
            </tr>
            <tr>
                <th class="vertical_top"><span class="icon_square"></span><fmt:message key='aimir.supplystatus'/></th>
                <td class="vertical_top">: </td>
                <td id="status" ></td>
            </tr>
            <tr>
                <th class="vertical_top"><span class="icon_square"></span><fmt:message key='aimir.contract'/> <fmt:message key='aimir.date'/></th>
                <td class="vertical_top">: </td>
                <td id="date" ></td>
            </tr>
        </table>
	</div>
	</div-->
    <!--//contract no.-->
   
    
     
    <div class="overflow_hidden">
    	<!--max left  -->
    	<div class="saving_left">
    	
	    	<!-- left 1:  -->
 	    	<div class="title_basic"><span class="icon_title_blue"></span>목표설정</div>
	    	
	    	<div class="goal2">
	    		<div class="saving_goal">
			 		<table>
				 		<tr>
				 			<td class="txt_saving2">월 절감 목표</td>
				 			<td><input type="text" id="savingTarget"  style="width:100px" class="target"/></td>
				 			<td><a href="javascript:setSavingTarget();" class="btn_blue"><span>설정</span></a></tdi>
				 		</tr>
				 	</table>
			 	</div>
		    </div>
		    
		    <div id="chartDiv"></div>
	    	<!--// left 1  -->
	    	
	    	<!-- left 2 -->
	    	<div class="clear">
	    	
		    	<div class="title_basic"><span class="icon_title_blue"></span>통보설정</div>
		    	
		    	<p class="text_orange margin_b5"> * 목표설정 관리 정보를 SMS/E-Mail로 전송합니다.</p>
	    	
				<div class="setup_box">
					<div class="setup">
						<ul>
							<li class="title_blk"><span class="icon_triangle"></span>통보</li>
							<li class="remark">통보 수단을 설정해 주세요.</li>
							<li>
								<div>
									<span><input type="checkbox" id="sms" onclick="javascript:clicked('sms', 'eMail');" class="checkbox" /><a href="javascript:checked('sms', 'eMail');" >SMS</a></span>
									<span><input type="checkbox" id="eMail" onclick="javascript:clicked('eMail', 'sms');" class="checkbox"/><a href="javascript:checked('eMail', 'sms');" >E-Mail</a></span>
									<span><input type="text" id="address" /></span>
								</div>
							</li>
						</ul>
					</div>
				
					<div class="setup">
						<ul>
							<li class="title_blk"><span class="icon_triangle"></span>전월 실적 관리</li>
							<li class="remark">통보 주기을 설정해 주세요.</li>
							<li>
								<div>
									<span><input type="checkbox" id="period1" class="checkbox"/></span>
									<span> 전월 에너지 절감 목표 실적 통보</span>
								</div>
							</li>
						</ul>
					</div>
				
					<div class="setup">
						<ul>
							<li class="title_blk"><span class="icon_triangle"></span>당월 실적 관리</li>
							<li class="remark">통보 주기을 설정해 주세요.</li>
							<li>
								<div>
									<span><input type="checkbox" id="period2" class="checkbox"/></span>
									<span> 설정 목표</span>
									<span><input type="text" id="comValue" class="settarget" ></span>
									<span> % 초과 시 통보</span>
								</div>
							</li>
							
							<li>
								<div>
									<span><input type="checkbox" id="period3"  class="checkbox"/></span>
									<span> 설정 목표 초과 시 통보</span>
								</div>
							</li>
							<li>
								<div>
									<span><input type="checkbox" id="period4"  class="checkbox"/></span>
									<span> 주 1회 에너지 절감 목표 통보</span>
								</div>
							</li>
							<li>
								<div>
									<span><input type="checkbox" id="period5"  class="checkbox"/></span>
									<span> 2주 1회 에너지 절감 목표 실적 통보</span>
								</div>
							</li>
						</ul>
					</div>
				</div>
			
			    <div class="saving_rightbtn">
					<a href="javascript:setNoticeTarget();" class="btn_blue"><span>설정</span></a>
			    </div>
	    
	    	</div>
	    	<!--// left 2 -->
    	
	    </div>
	    <!--// max left  -->
	    
	    <!-- max right  -->
	    <div class="saving_right">
	    	
	    	<div class="title_basic"><span class="icon_title_blue"></span>목표설정</div>
	    	
    		<div class="goal2">
    			<div class="saving_goal">
		    		<span class="grid_savingtxt"><span id="girdChartDay" ></span></span>
		    		<span class="icon_line"></span>
			    	<span id="girdChartRate" class="grid_savingtxt text_orange2"></span>
			   </div>
		    </div>
		   
			<div id="gridChartDiv"></div>
			
			<p class="saving_comment"> * 날짜를 클릭 하시면 위의 그래프로 사용금액의 절감상태를 비교해 보실수 있습니다.(문구 수정)</p>
			<div id="grid"></div>
	    
	    </div>
    	<!--// max right  -->
    </div>
    
 	
</div>
</body>
</html>