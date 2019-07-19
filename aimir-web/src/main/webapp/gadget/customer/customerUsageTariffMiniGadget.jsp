<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<%@ page import="com.aimir.constants.CommonConstants"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Customer Tariff Electricity</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<script type="text/javascript" charset="utf-8"><!--

    var miniTab     = "usageFee";   //탭
    var userId      = "";           //사용자아이디
    var METER_TYPE  = "Electricity";//조회유형
    var condArray   = new Array();  //파마미터
    var loaded      = false;        //selectbox 로딩여부
    var strSUM      = "<fmt:message key="aimir.sum"/>";
    var tariffTypeName = ""; //계약종별 이름

    $(function(){
    	 //탭 클릭
        $(function() { $('#_usageFee') .bind('click',function(event) { changeData("usageFee"); } ); });
        $(function() { $('#_priceList') .bind('click',function(event) { changeData("tariff"); } ); });

        //조회버튼클릭
        $(function() { $('#btnSearch').bind('click',function(event) {
            getTariff();
        } ); });

        //공급유형 변경
        $(function() { $('#supplierType').bind('change',function(event) { changeDiv($('#supplierType option:selected').text()); } ); });

        $("#customerUsageMini").tabs();

       // alert("userId : " + userId + " data 임시 세팅 15로....");
        getData();
    });

    //사용요금, 요금표 선택시
    function changeData(selMiniTab){
        miniTab = selMiniTab;
        getData();
    }

 // 요금표 - 공급타입 선택 시 Div 변경
    function changeDiv(type){

        supplyType = type;
        METER_TYPE = type;

        //alert(supplyType);

        if(supplyType == SupplierType.Electricity){
            $('#divEM').css('display','inline');
            $('#divGM').css('display','none');
            $('#divWM').css('display','none');
        }
        else if(supplyType == SupplierType.Gas || supplyType == SupplierType.Heat){
            $('#divEM').css('display','none');
            $('#divGM').css('display','inline');
            $('#divWM').css('display','none');
        }
        else if(supplyType == SupplierType.Water){
            $('#divEM').css('display','none');
            $('#divGM').css('display','none');
            $('#divWM').css('display','inline');
        }

        // 공급타입별 적용일자 콤보 리스트 로딩
        getYyyymmddList(supplyType);
    }

 // 요금관리 - 공급타입별 적용일자 콤보 리스트 조회
    function getYyyymmddList(supplyType) {

        var str = "";
        $.getJSON('${ctx}/gadget/system/supplier/getRecentYyyymmddList.do', {supplierType:supplyType},
                function(json) {
                    $('#yyyymmddCombo').empty();

                    /*
                    // 전체 리스트를 가져와 선택하여 조회하는 경우 : /gadget/system/supplier/getYyyymmddList.do 호출하고 주석풀면 됨.

                    $('#yyyymmddCombo').append("<option value=''>적용일자</option>");
                    if (json.yyyymmddList != null) {
                        $.each(json.yyyymmddList, function(index, yyyymmdd){
                            $('#yyyymmddCombo').append("<option value='"+yyyymmdd['yyyymmdd']+"'>"+yyyymmdd['yyyymmdd']+"</option>");

                            str = yyyymmdd['yyyymmdd'];
                        });
                    }
                    */

                    $('#yyyymmddCombo').append("<option value='"+json.MaxYyyymmdd+"'>"+json.MaxYyyymmdd+"</option>");

                    $('#yyyymmddCombo').selectbox();

                    getTariff();
        });
    }

    //조회조건 가져오기
    function getCondition(){
        condArray[0]    = miniTab;
        condArray[1]    = userId;
        condArray[2]    = 'Meter';       // DeviceType => MDEV_TYPE
        condArray[3]    = METER_TYPE;

        return condArray;
    }

    //데이터 가져오기
    function getData(){


    	getCondition();

    	if(miniTab == "usageFee"){

	    	$.post(
	                "${ctx}/gadget/customer/getCustomerUsageFee.do",
	                 {"sViewType":condArray[0], "sUserId":condArray[1], "iMdev_type":condArray[2]},
	                 usageFeeData_callback
	        ); //end $.post
    	}else if(miniTab == "tariff"){

    		if(!loaded){
    		  getSupplier();
    		}

    	}
    }


 // 공급사 선택 시 정보를 로딩한다.
    function getSupplier(){

        // 요금관리 - 공급타입 콤보 조회
        $.getJSON('${ctx}/gadget/system/supplier/getSupplierTypes.do', {},
            function(json){
                $.each(json.supplyTypes, function(index, supplyType){
                    $('#supplierType').append("<option value='"+supplyType['typeCode']+"'>"+supplyType['type']+"</option>");
                });
                $('#supplierType').selectbox();
                loaded = true;

                changeDiv($("#supplierType option:selected").text());
        });
    }


    //요금표를 조회한다.
    function getTariff(){

    	getCondition();

        $.post(
                "${ctx}/gadget/customer/getCustomerTariff.do",
                 {"sViewType":condArray[0], "sUserId":condArray[1], "iMdev_type":condArray[2], "METER_TYPE":condArray[3], "yyyymmdd":$('#yyyymmddCombo option:selected').val()},
                 tariffData_callback
        ); //end $.post
    }


    //사용요금-콜백
    function usageFeeData_callback(json, textStatus){

         //alert("json.sEnd : " + json.sEnd);

         var i  = 0;

       //사용요금
         //alert(json.tariff.EM);
         //alert(json.tariff.GM);
         //alert(json.tariff.WM);

         $("#emTariff").html(json.tariff.Electricity);
         $("#gmTariff").html(json.tariff.Gas);
         $("#wmTariff").html(json.tariff.Water);
         //$("#sumTariff1").html(json.tariff.SUM);
         $("#sumTariff2").html(json.tariff.SUM);

         // 전기
         jQuery.each(json.usageFee.usageFeeEm, function(key, obj) {
             jQuery.each(obj, function(key2, obj2){

                 if(i == 0){
                     if(key2 == "basevalue"){
                    	   $("#emThisMonthData").html(obj2);
                     }else if(key2 == "usage"){
                           $("#emUsage").html(obj2);
                     }
                 }else if(i == 1){
                	 if(key2 == "basevalue"){
                		   $("#emPreMonthData").html(obj2);
                	 }else if(key2 == "usage"){
                           $("#emPremonthUsage").html(obj2);
                     }
                 }else if(i == 2){
                	 if(key2 == "usage"){
                		   $("#emLastyearUsage").html(obj2);
                     }
                 }

                 //alert(i + "-> EM : " + key2 + " : " + obj2);

             });

             i++;
         });

         i = 0;
         //가스
         jQuery.each(json.usageFee.usageFeeGm, function(key, obj) {
        	 jQuery.each(obj, function(key2, obj2){

                 if(i == 0){
                     if(key2 == "basevalue"){
                           $("#gmThisMonthData").html(obj2);
                     }else if(key2 == "usage"){
                           $("#gmUsage").html(obj2);
                     }
                 }else if(i == 1){
                     if(key2 == "basevalue"){
                           $("#gmPreMonthData").html(obj2);
                     }else if(key2 == "usage"){
                           $("#gmPremonthUsage").html(obj2);
                     }
                 }else if(i == 2){
                     if(key2 == "usage"){
                           $("#gmLastyearUsage").html(obj2);
                     }
                 }

                 //alert(i + "-> GM : " + key2 + " : " + obj2);

             });

             i++;
         });

         i  = 0;
         //수도
         jQuery.each(json.usageFee.usageFeeWm, function(key, obj) {
        	 jQuery.each(obj, function(key2, obj2){

                 if(i == 0){
                     if(key2 == "basevalue"){
                           $("#wmThisMonthData").html(obj2);
                     }else if(key2 == "usage"){
                           $("#wmUsage").html(obj2);
                     }
                 }else if(i == 1){
                     if(key2 == "basevalue"){
                           $("#wmPreMonthData").html(obj2);
                     }else if(key2 == "usage"){
                           $("#wmPremonthUsage").html(obj2);
                     }
                 }else if(i == 2){
                     if(key2 == "usage"){
                           $("#wmLastyearUsage").html(obj2);
                     }
                 }

                 //alert(i + "-> WM : " + key2 + " : " + obj2);

             });

             i++;
         });
    }

    //요금표-콜백
    function tariffData_callback(json, textStatus){

    	if(json.tariffData.tariffEm != undefined){
    		prcEM(json, textStatus);
    	}else if(json.tariffData.tariffGm != undefined){
            prcGM(json, textStatus);
        }else if(json.tariffData.tariffWm != undefined){
            prcWM(json, textStatus);
        }

    }

    //전기요금표 데이터 표시
    function prcEM(json, textStatus){
    	var tdArray     = new Array();
        var tmpArray    = new Array();

        //추가된 row가 존재한다면 모두 삭제
        $("#dataGridEM  tr[name^='addRow']").each(function(){
            $(this).remove();
        });

       // alert(json.tariffData.tariffEm);

        jQuery.each(json.tariffData.tariffEm, function(key, obj) {
            jQuery.each(obj, function(key2, obj2){

                //alert("-> Tariff GM : " + key2 + " : " + obj2);

                if(obj2 == null){
                    obj2    = "";
                }

                if(key2 == "tariffType"){
                    tariffTypeName    = obj2;
                }

                if(key2 == "season"){
                    tdArray[0]    = obj2;
                }

                if(key2 == "peakType"){
                    tdArray[1]    = obj2;
                }


                //3번재는 displayData()로 가공하여 보여짐.
                if(key2 == "supplySizeMin"){
                    tmpArray[0] = obj2;
                }

                if(key2 == "supplySizeMax"){
                    tmpArray[1] = obj2;
                }

                if(key2 == "supplySizeUnit"){
                    tmpArray[2] = obj2;
                }

                if(key2 == "condition1"){
                    tmpArray[3] = obj2;
                }

                if(key2 == "condition2"){
                    tmpArray[4] = obj2;
                }
                //3번재는 displayData()로 가공하여 보여짐.


/*
                if(key2 == "serviceCharge"){
                    tdArray[3]    = obj2;
                }

                if(key2 == "adminCharge"){
                    tdArray[4]    = obj2;
                }

                if(key2 == "distributionNetworkCharge"){
                    tdArray[5]    = obj2;
                }

                if(key2 == "transmissionNetworkCharge"){
                    tdArray[6]    = obj2;
                }
*/
                if(key2 == "energyDemandCharge"){
                    tdArray[3]    = obj2;
                }

                if(key2 == "activeEnergyCharge"){
                    tdArray[4]    = obj2;
                }

                if(key2 == "reactiveEnergyCharge"){
                    tdArray[5]    = obj2;
                }
/*
                if(key2 == "rateRebalancingLevy"){
                    tdArray[10]    = obj2;
                }
*/
            });

            addRowEM(tdArray, tmpArray);

        });

        $("#tmpRowEM").hide();
    }

     //가스요금표 데이터 표시
    function prcGM(json, textStatus){
    	var td1    = "";
        var td2    = "";
        var td3    = "";
        var td4    = "";
        var td5    = "";
        var td6    = "";

        var tdArray   = new Array();

        //추가된 row가 존재한다면 모두 삭제
        $("#dataGridGM  tr[name^='addRow']").each(function(){
            $(this).remove();
        });


        jQuery.each(json.tariffData.tariffGm, function(key, obj) {
            jQuery.each(obj, function(key2, obj2){

                //alert("-> Tariff GM : " + key2 + " : " + obj2);

            	if(obj2 == null){
                    obj2    = "";
                }

                if(key2 == "tariffType"){
                    tdArray[0]    = obj2;
                }

                if(key2 == "season"){
                    tdArray[1]    = obj2;
                }

                if(key2 == "basicRate"){
                    tdArray[2]    = obj2;
                }

                if(key2 == "usageUnitPrice"){
                    tdArray[3]    = obj2;
                }

                if(key2 == "salePrice"){
                    tdArray[4]    = obj2;
                }

                if(key2 == "adjustmentFactor"){
                    tdArray[5]    = obj2;
                }

            });

            addRowGM(tdArray);

        });

        $("#tmpRowGM").hide();
    }

    //수도요금표 데이터 표시
    function prcWM(json, textStatus){
    	var td1    = "";
        var td2    = "";
        var td3    = "";
        var td4    = "";
        var td5    = "";
        var td6    = "";

        var tdArray   = new Array();
        var tdArray2   = new Array();

        //추가된 row가 존재한다면 모두 삭제
        $("#dataGridWM  tr[name^='addRow']").each(function(){
            $(this).remove();
        });

        $("#dataGridWMCal  tr[name^='addRow']").each(function(){
            $(this).remove();
        });

        jQuery.each(json.tariffData.tariffWm, function(key, obj) {
            jQuery.each(obj, function(key2, obj2){

                //alert("-> Tariff GM : " + key2 + " : " + obj2);

            	if(obj2 == null){
                    obj2    = "";
                }

                if(key2 == "tariffType"){
                    tdArray[0]    = obj2;
                }

                if(key2 == "supplySizeMin"){
                    tdArray[1]    = obj2;
                }

                if(key2 == "supplySizeMax"){
                    tdArray[2]    = obj2;
                }

                if(key2 == "usageUnitPrice"){
                    tdArray[3]    = obj2;
                }

            });

          addRowWM(tdArray);

        });


        jQuery.each(json.tariffData.tariffWmCal, function(key, obj) {
            jQuery.each(obj, function(key2, obj2){

                //alert("-> Tariff GM : " + key2 + " : " + obj2);

            	if(obj2 == null){
                    obj2    = "";
                }

                if(key2 == "caliber"){
                    tdArray2[0]    = obj2;
                }

                if(key2 == "basicRate"){
                    tdArray2[1]    = obj2;
                }

                if(key2 == "basicRateHot"){
                    tdArray2[2]    = obj2;
                }
            });

            addRowWMCal(tdArray2);
        });

        $("#tmpRowWM").hide();
        $("#tmpRowWMCal").hide();
    }

  //그리드 테이블 로우 추가
    function addRowEM(tdArray, tmpArray){

       var sHTML    = "<tr name=\"addRow\" class=\"usagetariff-rates-detail-data\">";

    	jQuery.each(tdArray, function(i) {
        	if(i == 0){
        		sHTML += "<td width=\"15%\" style=\"word-break:break-all\">" + tdArray[i] + "</td>";
        	} else if(i == 2){
        		sHTML += "<td width=\"25%\" style=\"word-break:break-all\">" + displayDataEM(tmpArray) + "</td>";
        	}else{
            	sHTML += "<td width=\"15%\" style=\"word-break:break-all\">" + tdArray[i] + "</td>";
        	}
        });

        sHTML   += "</tr>";

    	$("#dataGridEM tr:last").after(sHTML);
    }

    function addRowGM(tdArray){

        var sHTML    = "<tr name=\"addRow\" class=\"usagetariff-rates-detail-data\">";

         jQuery.each(tdArray, function(i) {
             if(i ==0) sHTML += "<td width=\"15%\">" + tdArray[i] + "</td>";
             else sHTML += "<td width=\"15%\">" + tdArray[i] + "</td>";
         });

         sHTML   += "</tr>";

         $("#dataGridGM tr:last").after(sHTML);
     }

    function addRowWM(tdArray){

        $("#dataGridWM tr:last").after('<tr name="addRow" class="usagetariff-rates-detail-data"><td width="15%">' + tdArray[0] + '</td><td width="15%">' + displayDataWM(tdArray[1], tdArray[2]) + '</td><td width="15%">' + tdArray[3] + '</td></tr>');
    }

    function addRowWMCal(tdArray){

        $("#dataGridWMCal tr:last").after('<tr name="addRow" class="usagetariff-rates-detail-data"><td width="15%">' + tdArray[0] + '</td><td width="15%">' + tdArray[1] + '</td><td width="15%">' + tdArray[2] + '</td></tr>');
    }

    function displayDataWM(min, max) {
        if(max == null){
            return min + "초과";
        }
        else if(min == "0"){
            return min + " ~ " + max + "이하";
        }
        else{
            return min + "초과 ~ " + max + "이하";
        }
    }



    function displayDataEM(tmpArray){
        var min = tmpArray[0];
        var max = tmpArray[1];
        var unit = tmpArray[2];
        var cond1 = tmpArray[3];
        var cond2 = tmpArray[4];

        var ret = "";
        if(min == 0){
            ret =  cond2 + " " + max + " " + unit;
        }
        if(max == 0){
            ret = cond1 + " " + min + " " + unit;
        }
        if(min != 0 && max != 0){
        	 ret = min + " ~ " + max + " " + unit;
        }
		return ret;
    }

    function displayDataWM(min, max) {
        if(max == null){
            return min + "초과";
        }
        else if(min == "0"){
            return min + " ~ " + max + "이하";
        }
        else{
            return min + "초과 ~ " + max + "이하";
        }
    }

    /* 스크롤 이동시 타이틀과 합계 같이 움직이*/
    function autoScroll(obj)
    {
        var objTitle = $("#divTitle");

        if (objTitle)  objTitle.scrollLeft = obj.scrollLeft;
    }


   //--></script>

</head>
<body>


	<!-- 탭전체 (S) -->
    <div id="customerUsageMini">
        <ul>
            <li><a href="#usageFee"   id="_usageFee" >  <fmt:message key="aimir.usageFee2"/>   </a></li> <!-- 사용요금 -->
            <li><a href="#priceList"  id="_priceList" > <fmt:message key="aimir.pricelist"/>   </a></li> <!-- 요금표 -->
        </ul>


        <!--  1ST 탭 : 사용요금 (S)  -->
		<div id="usageFee">

			<div class="usagetariff-title graybold11pt"><fmt:message key="aimir.pricelist"/><!-- 요금표 --></div>
			<table class="border-2px-sum" border="1">
				<tr class="usagetariff-rates">
					<td>
							<table class="usagetariff-sum">
								<tr><td class="elec11pt bold"><fmt:message key="aimir.energymeter"/><!-- 전기 --></td></tr>
								<tr><td class="gray11pt" id="emTariff">-</td></tr>
							</table>
					</td>
					<td>
							<table class="usagetariff-sum">
								<tr><td class="gas11pt bold"><fmt:message key="aimir.gas"/><!-- 가스 --></td></tr>
								<tr><td class="gray11pt" id="gmTariff">-</td></tr>
							</table>
					</td>
					<td>
							<table class="usagetariff-sum">
								<tr><td class="water11pt bold"><fmt:message key="aimir.water"/><!-- 수도 --></td></tr>
								<tr><td class="gray11pt" id="wmTariff">-</td></tr>
							</table>
					</td>
				</tr>
				<tr class="usagetariff-rates">
					<td colspan="3">

							<table class="usagetariff-sum wfree" align="center">
								<tr><td class="gray11pt" id="sumTariff1"></td>
									<td class="font-sum bold" id="sumTariff2"></td></tr> <!-- id="sumTariff2" -->
							</table>

					</td>
				</tr><!-- 합계 -->
			</table>





			<div class="usagetariff-title elec11pt"><fmt:message key="aimir.energymeter"/><!-- 전기 --></div>
			<table class="border-2px-elec">
				<tr><td class="usagetariff-left-sum">

						<table class="usagetariff-sum">
							<tr><td class="elec11pt"><fmt:message key="aimir.usage"/><!-- 사용량 --></td></tr>
							<tr><td class="elec21pt" id="emUsage">-</td></tr>
						</table>

					</td>
					<td class="usagetariff-right-detail">

						<table class="usagetariff-detail">
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.prevmonthdata"/></td><!-- 전월지침 -->
								<td class="elec11pt dottedline" id="emPreMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.thismonthdata"/></td><!-- 현월지침 -->
								<td class="elec11pt dottedline" id="emThisMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.premonthusage"/></td><!-- 전월 사용량 -->
								<td class="elec11pt dottedline" id="emPremonthUsage">-</td>
							</tr>
							<tr>
								<td><fmt:message key="aimir.lastyear"/><fmt:message key="aimir.samemonth"/><fmt:message key="aimir.usage"/></td><!-- 전년 동월 사용량 -->
								<td class="elec10pt dottedline" id="emLastyearUsage">-</td>
							</tr>
						</table>

					</td></tr>
			</table>


			<div class="usagetariff-title gas11pt"><fmt:message key="aimir.gasmeter"/><!-- 가스 --></div>
			<table class="border-2px-gas">
				<tr><td class="usagetariff-left-sum">

						<table class="usagetariff-sum">
							<tr><td class="gas11pt"><fmt:message key="aimir.usage"/><!-- 사용량 --></td></tr>
							<tr><td class="gas21pt" id="gmUsage">-</td></tr>
						</table>

					</td>
					<td class="usagetariff-right-detail">

						<table class="usagetariff-detail">
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.prevmonthdata"/></td><!-- 전월지침 -->
								<td class="gas11pt dottedline" id="gmPreMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.thismonthdata"/></td><!-- 현월지침 -->
								<td class="gas11pt dottedline" id="gmThisMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.premonthusage"/></td><!-- 전월 사용량 -->
								<td class="gas11pt dottedline" id="gmPremonthUsage">-</td>
							</tr>
							<tr>
								<td><fmt:message key="aimir.lastyear"/><fmt:message key="aimir.samemonth"/><fmt:message key="aimir.usage"/></td><!-- 전년 동월 사용량 -->
								<td class="gas11pt dottedline" id="gmLastyearUsage">-</td>
							</tr>
						</table>

					</td></tr>
			</table>


			<div class="usagetariff-title water11pt"><fmt:message key="aimir.watermeter"/><!-- 수도 --></div>
			<table class="border-2px-water">
				<tr><td class="usagetariff-left-sum">

						<table class="usagetariff-sum">
							<tr><td class="water11pt"><fmt:message key="aimir.usage"/><!-- 사용량 --></td></tr>
							<tr><td class="water21pt" id="wmUsage">-</td></tr>
						</table>

					</td>
					<td class="usagetariff-right-detail">

						<table class="usagetariff-detail">
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.prevmonthdata"/></td><!-- 전월지침 -->
								<td class="water11pt dottedline" id="wmPreMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.thismonthdata"/></td><!-- 현월지침 -->
								<td class="water11pt dottedline" id="wmThisMonthData">-</td>
							</tr>
							<tr>
								<td class="gray11pt"><fmt:message key="aimir.premonthusage"/></td><!-- 전월 사용량 -->
								<td class="water11pt dottedline" id="wmPremonthUsage">-</td>
							</tr>
							<tr>
								<td><fmt:message key="aimir.lastyear"/><fmt:message key="aimir.samemonth"/><fmt:message key="aimir.usage"/></td><!-- 전년 동월 사용량 -->
								<td class="water11pt dottedline" id="wmLastyearUsage">-</td>
							</tr>
						</table>

					</td></tr>
			</table>

			<div class="lightgray11pt margin-t5px">* <fmt:message key="aimir.usagemsg"/> <!-- 일반적인 요금표를 기준으로 한것으로 실제 과금과 약간의 차이가 날 수 있습니다. --></div>
		</div>
        <!--  1ST 탭 : 사용요금 (E)  -->



        <!--  2ND 탭 : 전체 요금표 (S)  -->
		<div id="priceList">

			<div class="search-bg-withouttabs padding-reset-0">
				<div class="searchoption-container">
					<table class="searchoption wfree">
					   <tr>
						   <td><select id="supplierType" style="width:100px;"></select></td>
						   <td><select id="yyyymmddCombo" style="width:100px;"></select></td>
						   <td>
							   <div id="btn">
								   <ul><li><a href="javascript:;" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
							   </div>
						   </td>
					   </tr>
					</table>
				</div>
			</div>


			<!--  전기 (S)  -->
            <form id="dataFormEM">
			<div id="divEM">


				<div class="usagetariff-title graybold11pt"><fmt:message key="aimir.pricelist"/><!-- 요금표 --></div>
				<table id="dataGridEM" class="border-2px-sum">
				
					<colgroup>
						<col width="15%"/>
						<col width="15%"/>
						<col width=""/>
						<col width="15%"/>
						<col width="15%"/>
						<col width="15%"/>
					</colgroup>
					
					
					<tr class="usagetariff-rates-detail-tit border-bottom-2px">
						<!-- <td><fmt:message key="aimir.tariff"/></td> -->
						<td><fmt:message key="aimir.season"/></td>
						<td><fmt:message key="aimir.tou"/></td>
						<td><fmt:message key="aimir.supplySize"/></td>
						<!-- <td><fmt:message key="aimir.serviceCharge"/></td> -->
						<!-- <td><fmt:message key="aimir.adminCharge"/></td> -->
						<!-- <td><fmt:message key="aimir.distributionNetworkCharge"/></td>-->
						<!-- <td><fmt:message key="aimir.transmissionNetworkCharge"/></td>-->
						<td><fmt:message key="aimir.energyDemandCharge"/></td>
						<td><fmt:message key="aimir.activeEnergyCharge"/></td>
						<td><fmt:message key="aimir.reactiveEnergyCharge"/></td>
						<!-- <td><fmt:message key="aimir.rateRevalancingLevy"/></td>-->
					</tr>
					<tr id="tmpRowEM" class="usagetariff-rates-detail-data">
							<!-- <td></td>-->
							<!-- <td></td>-->
							<!-- <td></td>-->
							<!-- <td></td>-->
							<!-- <td></td>-->
							<!-- <td></td>-->
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
					</tr>
				</table>
				
			</div>
            </form>
			<!--  전기 (E)  -->

			<!--  가스 (S)  -->
	        <form id="dataFormGM">
	        <div id="divGM">
				<div class="usagetariff-title graybold11pt"><fmt:message key="aimir.pricelist"/><!-- 요금표 --></div>
	            <table id="dataGridTitle" class="border-2px-sum datagrid-title">
	                
	                <colgroup>
						<col width="15%"/>
						<col width="15%"/>
						<col width=""/>
						<col width="15%"/>
						<col width="15%"/>
						<col width="15%"/>
						
					</colgroup>
					
	                <tr class="usagetariff-rates-detail-tit">
	                    <td><fmt:message key="aimir.tariff"/></td>
	                    <td><fmt:message key="aimir.season"/></td>
	                    <td><fmt:message key="aimir.basicRate"/></td>
	                    <td><fmt:message key="aimir.unitUsage"/></td>
	                    <td><fmt:message key="aimir.salePrice"/></td>
	                    <td><fmt:message key="aimir.adjustmentFactor"/></td>
	                </tr>
	            </table>
	            <div class="scroll-auto">
	                <table id="dataGridGM" class="border-2px-sum datagrid-data">
	                    <tr id="tmpRowGM" class="usagetariff-rates-detail-data">
	                        <td></td>
	                        <td></td>
	                        <td></td>
	                        <td></td>
	                        <td></td>
	                        <td></td>
	                    </tr>
	                </table>
	            </div>
	        </div>
	        </form>
	        <!--  가스 (E)  -->

	        <!--  수도 (S)  -->
	        <form id="dataFormWM">
	        <div id="divWM">
	           <table>
	               <tr>
	                   <td width="49%">

							<div class="usagetariff-title graybold11pt"><fmt:message key="aimir.waterCharge.title1"/></div>
		                    <table id="dataGridTitle" class="border-2px-sum datagrid-title">
		                        <tr class="usagetariff-rates-detail-tit">
		                            <td width="33%"><fmt:message key="aimir.adminCharge"/></td>
		                            <td width="33%"><fmt:message key="aimir.transmissionNetworkCharge"/></td>
		                            <td width="33%"><fmt:message key="aimir.distributionNetworkCharge"/></td>
		                        </tr>
		                    </table>

		                    <div class="scroll-auto">
		                        <table id="dataGridWMCal" class="border-2px-sum datagrid-data">
		                            <tr id="tmpRowWMCal" class="usagetariff-rates-detail-data">
		                                <td></td>
		                                <td></td>
		                                <td></td>
		                            </tr>
		                        </table>
		                    </div>
	                   </td>
	                   <td>&nbsp;</td>
	                   <td width="49%">
							<div class="usagetariff-title graybold11pt"><fmt:message key="aimir.waterCharge.title2"/></div>
                            <table id="dataGridTitle" class="border-2px-sum datagrid-title">
                                <tr class="usagetariff-rates-detail-tit">
                                    <td><fmt:message key="aimir.waterCharge.title3"/></td>
                                    <td><fmt:message key="aimir.waterCharge.title6"/></td>
                                    <td><fmt:message key="aimir.waterCharge.title7"/></td>
                                </tr>
                            </table>

                            <div class="scroll-auto">
                                <table id="dataGridWM" class="border-2px-sum datagrid-data">
                                    <tr id="tmpRowWM" class="usagetariff-rates-detail-data">
                                        <td></td>
										<td></td>
										<td></td>
                                    </tr>
                                </table>
                            </div>
	                   </td>
	               </tr>
	            </table>
	        </div>
	        </form>
			<!--  수도 (E)  -->




        </div>
        <!--  2ND 탭 : 전체 요금표 (E)  -->


    </div>
	<!-- 탭전체 (E) -->

</body>
</html>