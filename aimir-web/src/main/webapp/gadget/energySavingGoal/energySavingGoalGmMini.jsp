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

//	    emergePre();

//        getSelect();

//        hide();

        //Tab 클릭 이벤트 정의
        $("#energySavingGoalsSettingTabId").click(function() { displayDivTab("energySavingGoalsSettingTab"); });        
        $("#energySavingGoalsNotificationSettingTabId").click(function() { displayDivTab("energySavingGoalsNotificationSettingTab"); });

        displayDivTab("energySavingGoalsSettingTab");
	});

    var divTabArray = ["energySavingGoalsSettingTab", "energySavingGoalsNotificationSettingTab"];
    var divTabArrayLength = divTabArray.length;

    var displayDivTab = function(_currentDivTab) {
             
        for ( var i = 0; i < divTabArrayLength; i++) {

            if (_currentDivTab == divTabArray[i]) {
                
                $("#" + divTabArray[i]).show();
                $("#" + divTabArray[i] + "Id").addClass("current");

                searchTab(i);
            } else {
                $("#" + divTabArray[i]).hide();
                $("#" + divTabArray[i] + "Id").removeClass("current");
            }
        }
       
    };

    var searchTab = function(tabSeq) {
        
        emergePre();

        if (tabSeq == null) {

            for (var i = 0; i < divTabArrayLength; i++) {

                if ($("#" + divTabArray[i]).css( "display" ) != "none") {

                    tabSeq = i;
                }
            }
        }

        switch (tabSeq) {

            case 0 :
                getSelect();
                break;

            case 1 :
                getSelect();
                break;

            default :
                break;
        }
        hide();
    };
	
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

    	                //$("#location").text(result.location);
    	                //$("#tariff").text(result.tariffType);
    	                //$("#status").text(result.status);
    	                //$("#date").text(result.date);

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
	
						setChartData();
						renderChart();
	    			} else {

		    			alert("저장 실패");
	    			}
	    		}
	    	);
	};
	
    var chartStart = " "
        + " <chart formatNumberScale='0'"
        + " yAxisValuesStep='2'"
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
		
	var chartFinish = " "
		+ " </chart> ";
		
	$(window).resize(function() {
	    
	    renderChart();
	});
	
	var renderChart = function() {
		
		var chartXml = chartStart + chartLabel + chartData + chartFinish;

        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#chartDiv").width(), "200", "0", "0" );
        myChart.setXMLData(chartXml);
        myChart.setTransparent("transparent");
        myChart.render("chartDiv");
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

    <!--contract no.
    <div class="contract"  id="contractUl" style="display:none;">-->
    <div class="contract"  >    
    	<table>
			<tr>
				<td class="text_blue"><fmt:message key='aimir.contractNumber'/></td>
				<td>
					<select id="contracts" style="width:200px"></select>
				</td>
			</tr>
		</table>
		
     	<!-- <ul id="contractUl" style="display:none;" >
            <li class="text_blue"><fmt:message key='aimir.contractNumber'/></li>
            <li>
                <select id="contracts">
                </select>
            </li>
        </ul>--> 
    </div>

   <!-- 
   <div class="contract_result">
   <div>
        <table>
            <colgroup>
            <col width="40%" />
            <col width="10px" />
            <col width="" />
            </colgroup>
            <tr>
                <th><span class="icon_square"></span><fmt:message key='aimir.contract'/> <fmt:message key='aimir.location'/></th>
                <td class="vertical_top">: </td>
                <td id="location" ></td>
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
    </div>
    //contract result-->
    
    <!-- tab -->
    <div class="hems_tab">
        <ul>
            <li><a id="energySavingGoalsSettingTabId">목표설정</a></li>
            <li><a id="energySavingGoalsNotificationSettingTabId">통보설정</a></li>
       </ul>
    </div>
    <!--// tab -->
    
     <!-- tab 1:  -->
    <div id="energySavingGoalsSettingTab" style="display:block;" >
	   
		<div class="margin_10">
			<div class="title_basic margin_l10"><span class="icon_title_blue"></span><fmt:message key='aimir.locationUsage.usage'/></div>
        </div>
		
		 <div class="goal">
	    	<span class="txt_saving">월 절감 목표</span>
	    	<span><input type="text" id="savingTarget"  style="width:100px;" class="target"/></span>
	    	<span><a href="javascript:setSavingTarget();" class="btn_blue"><span>설정</span></a></span>
	    </div>
	    
	    <div id="chartDiv"  class="margin_10"></div>
		
	</div>	
	<!--// tab 1:  -->
	
    
  <!-- tab 2:  -->
    <div id="energySavingGoalsNotificationSettingTab" style="display:none;" >
		<div class="margin_10 padding_l10">
			<p class="text_orange margin_b5"> * 목표설정 관리 정보를 SMS/E-Mail로 전송합니다.</p>
			
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
			
		   <div class="rightbtn">
				<a href="javascript:setNoticeTarget();" class="btn_blue"><span>설정</span></a>
		    </div>
		    
		</div>	
		
	</div>
	<!--// tab 2:  -->
	
	
	
	
</div>
</body>
</html>