<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<%@ page import="com.aimir.constants.CommonConstants"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>CustomerUsage MiniGadget</title>
<style type="text/css">
    html{overflow:auto !important}
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }
</style>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>

<script type="text/javascript" charset="utf-8">

	//탭초기화
	var tabs = {hourly:0,monthly:0,weekly:0,weekDaily:0,seasonal:0};
	var tabNames = {};

	var HOURLY      = <%=CommonConstants.DateType.valueOf("HOURLY").getCode()%>;
	var DAILY       = <%=CommonConstants.DateType.valueOf("DAILY").getCode()%>;
	var MONTHLY     = <%=CommonConstants.DateType.valueOf("MONTHLY").getCode()%>;
	var YEARLY      = <%=CommonConstants.DateType.valueOf("YEARLY").getCode()%>;

	var supplierId = "";
    var miniTab     = HOURLY;
    var userId      = "";
    var iStand      = 0;
    var condArray   = new Array();

    var fcChartDataXml;
    var fcChart;

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                }
            }
    );

    $(function(){
        $("#customerUsageMini").tabs();
    });
    
    
    function getInfo() {
        $.getJSON('${ctx}/gadget/customer/getContractInfo.do'
                ,{contractId:$('#contractSelecter').val()}
                ,function(json) {
                    $("#meterAddress").html("<span class='input-fake'>"+json.address+"</span>");
                    $("#meterId").html("<span class='input-fake'>"+json.mdsId+"</span>");
                }
        );
    }

    //메시지 properties 설정 - flex에서 호출
    function getFmtMessageCommAlert(){
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.day"/>";           //일
        fmtMessage[1] = "<fmt:message key="aimir.dailyusage"/>";    //일별사용량
        fmtMessage[2] = "<fmt:message key="aimir.daily.fee"/>";    //일별요금

        fmtMessage[3] = "<fmt:message key="aimir.day.mon"/>";           //월
        fmtMessage[4] = "<fmt:message key="aimir.monthly.usage"/>";    //월별사용량
        fmtMessage[5] = "<fmt:message key="aimir.monthly.fee"/>";    //월별요금

        fmtMessage[6] = "<fmt:message key="aimir.year1"/>";           //연
        fmtMessage[7] = "<fmt:message key="aimir.year1.usage"/>";    //연별사용량
        fmtMessage[8] = "<fmt:message key="aimir.year1.fee"/>";    //연별요금

        fmtMessage[9] = "<fmt:message key="aimir.watermeter"/>";    //수도
        fmtMessage[10] = "<fmt:message key="aimir.usageFee"/>";   //사용량 및 요금

        fmtMessage[11] = "<fmt:message key="aimir.hour"/>";    //시간
        fmtMessage[12] = "<fmt:message key="aimir.hour1.usage"/>";    //시간별 사용량

        return fmtMessage;
    }
    
    function send() {
		updateFChart();
    }

    
    //콜백
    function setData_callback(json, textStatus){

         var td1    = "";
         var td2    = "";
         var td3    = "";

//alert("json.sEndData.sEnd : " + json.sEndData.sEnd);

         setDisplay(json.sEndData.sEnd);

         //추가된 row가 존재한다면 모두 삭제
         $("#dataGrid  tr[name^='addRow']").each(function(){
             $(this).remove();
         });

         jQuery.each(json.useageData, function(key, obj) {
             jQuery.each(obj, function(key2, obj2){

                 //alert(key2 + " : " + obj2);

                 if(key2 == "yyyymmdd"){
                     td1    = obj2;
                 }

                 if(key2 == "usage"){
                     td2    = obj2;
                 }

                 if(key2 == "price"){
                     td3    = obj2;
                 }

               //시간별
                 if(key2 == "YYYYMMDD"){
                	 td1 = lpad((obj2-1), 2, "0") + "~" + lpad(obj2, 2, "0") + "(h)";
                     //td1    = obj2;
                 }

                 if(key2 == "USAGE"){
                     td2    = obj2;
                 }

                 if(key2 == "PRICE"){
                     td3    = obj2;
                 }

             });

//             addRow(td1, td2, td3);
             addRow(td1, td2);
         });

         $("#tmpRow").hide();
    }

  //자리수 채우기
    function lpad(str,n,ch) {
        str = String(str);
        var result = "";
        var len = str.length;
        if ( len < n ) {
              for ( var i=0; i<(n-len); i++ ) {
                    result += ch;
                    }
                result += str;
                }
        else {
            result = str;
            }
        return result;
    }

  //문구표시
    function setDisplay(yyyymm){

        var msg = getFmtMessageCommAlert();

        var divDisplay  = "";

        if(miniTab == DAILY){
            divDisplay    = "divDisplay_d";
            //str           = " " + yyyymm.substr(0,4) + msg[6] +" " + yyyymm.substr(4,2) + msg[3] + " "  + yyyymm.substr(6,2) + msg[0] + " " + msg[9] + " " + msg[10];
            str           = " " + yyyymm.substr(0,4) + "/" + yyyymm.substr(4,2) + "/"  + yyyymm.substr(6,2) + " " + msg[9] + " " + msg[10];
        }else if(miniTab == MONTHLY){
            divDisplay    = "divDisplay_m";
            //str           = " " + yyyymm.substr(0,4) + msg[6] + " " + yyyymm.substr(4,2) + msg[3] + " " + " " + msg[9] + " " + msg[10];
            str           = " " + yyyymm.substr(0,4) +"/" + yyyymm.substr(4,2) + " " + msg[9] + " " + msg[10];
        }else if(miniTab == YEARLY){
            divDisplay    = "divDisplay_y";
            //str           = " " + yyyymm.substr(0,4) + msg[6] + " " + " " + msg[9] + " " + msg[10];
            str           = " " + yyyymm.substr(0,4) + " " + msg[9] + " " + msg[10];
        }else if(miniTab == HOURLY){
            divDisplay    = "divDisplay_h";
            //str           = " " + yyyymm.substr(0,4) + msg[6] +" " + yyyymm.substr(4,2) + msg[3] + " "  + yyyymm.substr(6,2) + msg[0] + " " + msg[9] + " " + msg[10];
            str           = " " + yyyymm.substr(0,4) + "/" + yyyymm.substr(4,2) + "/"  + yyyymm.substr(6,2) + " " + msg[9] + " " + msg[10];
        }

        $('#'+divDisplay).html(str);
    }

    //그리드 테이블 로우 추가
    function addRow(td1, td2, td3){
//        $("#dataGrid tr:last").after('<tr class="datagrid-data-row" name="addRow"><td>' + td1 + '</td><td>' + td2 + '</td><td class="water11pt accent">' + td3 + '</td></tr>');
        $("#dataGrid tr:last").after('<tr class="datagrid-data-row" name="addRow"><td>' + td1 + '</td><td style="text-align:right;">' + td2 + ' ㎥</td></tr>');
    }

    $(document).ready(function() {		    
    	updateFChart();
    	$('#dataGrid').tableScroll({height:200});
    });

    function updateFChart() {
    	$.ajaxSetup({
	        async: false
	    });
          
   	    $.getJSON('${ctx}/gadget/customer/getCustomerUsageMiniChartbySearchDate.do'
   	    	    ,{sUserId:userId, 
   	    	    	iMdev_type:"Meter",
   	    	    	METER_TYPE:"WaterMeter", 
   	    	    	searchType:$('#searchDateType').val(),
   	    	    	startDate:$('#searchStartDate').val(), 
   	    	    	endDate:$('#searchEndDate').val(),
   	    	    	contractId:$('#contractSelecter').val(),
   	    	    	supplierId:supplierId}
				,function(json) {
				    
				    $('#currentCreditWm').val(json.currentCredit);
                    $('#monthCreditWm').val(json.monthlyCredit);
                    $('#monthlyBill').html("<label class='check'><fmt:message key='aimir.monthlyBill'/><br/>&nbsp;&nbsp;&nbsp;&nbsp;${currentDate}</label>");
				    
                     var list = json.useageData;
                     fcChartDataXml = "<chart "
                    	 + "showValues='0' "
                         + "showLegend='0' "
     					 + "labelDisplay = 'AUTO' "
                         + fChartStyle_Common
                         + fChartStyle_Font
                         + fChartStyle_MSColumn3D_nobg
                         + ">";
                     var categories = "<categories>";
                	 var dataset = "<dataset seriesName='<fmt:message key="aimir.usage"/>' color='"+fChartColor_Water[0]+"'>";
                	 if(list == null || list.length == 0) {
                		 categories += "<category label=' ' />";
                	 } else {
	                     for( index in list){
                        	 categories += "<category label='"+list[index].yyyymmdd+"' />";
                        	 dataset += "<set value='"+list[index].usage+"' />";
	                     }
                	 }
                     categories += "</categories>";
                     dataset += "</dataset>";
                     
                     fcChartDataXml += categories + dataset + "</chart>";                     
                     fcChartRender();

                     $.post('${ctx}/gadget/customer/getCustomerUsageMiniChartbySearchDate.do'
                	    	    ,{sUserId:userId, 
                	    	    	iMdev_type:"Meter",
                	    	    	METER_TYPE:"WaterMeter", 
                	    	    	searchType:$('#searchDateType').val(),
                	    	    	startDate:$('#searchStartDate').val(), 
                	    	    	endDate:$('#searchEndDate').val(),
                	    	    	contractId:$('#contractSelecter').val(),
                	    	    	supplierId:supplierId}
            	    	    	,setData_callback);
                }
   	    );

   	 	$.ajaxSetup({
	        async: true
	    });
	}

    window.onresize = fcChartRender;
    function fcChartRender() {
    	if($('#fcChartDiv').is(':visible')) {
	    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "myChartId", $('#fcChartDiv').width(), "210", "0", "0");
	        fcChart.setDataXML(fcChartDataXml);
	        fcChart.setTransparent("transparent");
	        fcChart.render("fcChartDiv");
    	}
    }    
    </script>

</head>
<body>
	<!-- search-background DIV (S) -->
	<div class="search-bg-withtabs">
		<div class="dayoptions">
			<%@ include file="/gadget/commonDateTab.jsp"%>
		</div>
	</div>
    <!-- search-background DIV (E) -->
    <div class="search-bg-basic"  style="height: 100px">
        <div id="contractList">
            <ul class="basic-ul">
                <table>
                    <tr>
                        <td class="gray11pt withinput">
                            <fmt:message key="aimir.contractNumber"/>
                        </td>
                        <td>
                            <select id="contractSelecter" name="contractSelecter" onChange="javascript:getInfo()">
                                <c:forEach var="contract" items="${contractList}">
                                   <c:choose>
                                    <c:when test="${contract == '-'}">
                                        <option value="-" selected>-</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${contract.id}" >${contract.name}</option>
                                    </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="gray11pt withinput">
                            <fmt:message key="aimir.meterid"/>
                        </td>
                        <td>
                            <div id="meterId">
                                <span class="input-fake">${meterId }</span>
                            </div>
                            <%-- <input type="text" id="meterId"  style="border: 0px; background: transparent; background-color: transparent;" value="${meterId }" readonly="readonly"/></span> --%>
                        </td>
                    </tr>
                    <tr class="gray11pt withinput">
                        <td class="withinput" style="width: 90px">
                            <fmt:message key="aimir.meter.address"/>
                        </td>
                        <td>
                            <div id="meterAddress">
                                <span class="input-fake">${address }</span>
                            </div>
                        </td>
                    </tr>

                    <tr class="gray11pt withinput">
                        <td class="withinput" style="width: 90px">
                            <fmt:message key="aimir.customeraddress"/>
                        </td>
                        <td>
                            <div id="customerAddress">
                                <span class="input-fake">${customerAddress }</span>
                            </div>
                        </td>
                    </tr>

                </table>
            </ul>
        </div>
    </div>

    <div class="gadget_body">
        <table>
            <tr style="width: 250px;">
                <th style="width: 150px;text-align: left;"><label class="check" ><fmt:message key="aimir.lastmeteringvalue"/></label></th>
                <td style="width: 75px"><span class="datavalue_rt">
                    <input type="text" id="lastMeteringData" style="border: 0px; text-align: right; width: 70px" value="${lastMeteringData }"   readonly="readonly"/></span>
                </td>
                <td style="padding-top: 5px"><fmt:message key="aimir.unit.kwh"/></td>
            </tr>                
            <tr style="width: 250px;">    
                <th style="width: 150px;text-align: left;"><label class="check"><fmt:message key="aimir.hems.prepayment.currentbalance"/></label></th>
                <td style="width: 75px"><span class="datavalue_rt">
                    <input type="text" id="currentCreditWm" style="border: 0px; text-align: right; width: 70px" value="${currentCredit }"   readonly="readonly"/></span>
                </td>
                <td style="padding-top: 5px"><fmt:message key='aimir.price.unit'/></td>
            </tr>
            <tr style="width: 250px;">
                <th style="width: 150px;text-align: left;"><div id="monthlyBill"><label class="check"><fmt:message key="aimir.monthlyBill"/><br/>&nbsp;&nbsp;&nbsp;&nbsp;${currentDate}</label></div></th>
                <td style="width: 75px"><span class="datavalu_rt">
                    <input type="text" id="monthCreditWm"  style="border: 0px; text-align: right; width: 70px" value="${monthlyCredit }" readonly="readonly"/></span>
                </td>
                <td style="padding-top: 5px"><fmt:message key='aimir.price.unit'/></td>
            </tr>
        </table>
    </div>

	<div id="fcChartDiv" class="margin-t5px margin-l5 margin-r5">
		The chart will appear within this DIV. This text will be replaced by the chart.
	</div>

    <div class="gadget_body">
		<!-- 데이터 출력 (S) -->
		<form id="dataForm">
			
			<div>
				<table id="dataGridTitle" class="border-2px-water datagrid-title">
                <colgroup>
                <col width="152px" />
                <col width="" />
                </colgroup>
					<tr class="datagrid-title-row">
						<td class="bold"><fmt:message key="aimir.period"/></td>
						<td class="bold"><fmt:message key="aimir.usage"/></td>
						<!--td class="water11pt bold"><fmt:message key="aimir.usageFee2"/></td-->
					</tr>
				</table>
			</div>
			<div class="border-2px-water datagrid-data">
				<table id="dataGrid" style="border:1px solid #D7DADB">
                    <colgroup>
                    <col width="150px" />
                    <col width="" />
                    </colgroup>
                    <tr id="tmpRow">
                        <td></td>
                        <td></td>
                    </tr>
                </table>
			</div>
            <!--  
            <input type="button" id="_googleCall" value="jsp-call">
            <div id="pnlDisplay">여기에 출력</div>
             -->
		</form>
		<!-- 데이터 출력 (E) -->
	</div>
</body>
</html>
