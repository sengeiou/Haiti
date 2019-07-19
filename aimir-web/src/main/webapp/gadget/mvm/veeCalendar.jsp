<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<%
	String supplierId = request.getParameter("supplierId") == null ? "": request.getParameter("supplierId");
%>
<%@ page import="java.util.Calendar"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

         var str = "";

         $(document).ready(function(){

            //월별 연도콤보 체인지이벤트 생성
             $(function() { $('#monthlyYearCombo2')   .bind('change', function(event) { getmonthlyMonthCombo2(""); } ); });
             $(function() { $('#monthlyMonthCombo2')  .bind('change', function(event) { getSearchDate2(DateType.MONTHLY); } ); });

             $.getJSON("${ctx}/common/getYear.do"
            		 ,{supplierId:$('#supplierId').val()}
                     ,function(json) {
                          var startYear = json.year;//currDate.getYear()-10;
                          var endYear = json.currYear;//currDate.getYear();
                          var currDate = json.currDate;

                              $('#monthlyYearCombo2')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});
                              $('#monthlyYearCombo2').selectbox();
                              getmonthlyMonthCombo2("",false);

                     });
         });

         /**
          * 월별탭에서 연도콤보 변경시 월콤보 생성
          */
         function getmonthlyMonthCombo2(monthVal,flag){

             $.getJSON("${ctx}/common/getMonth.do"
                     ,{year:$('#monthlyYearCombo2').val(),supplierId:$('#supplierId').val()}
                     ,function(json) {
                         var prevMonth = $('#monthlyMonthCombo2').val();
                         $('#monthlyMonthCombo2').emptySelect();
                         if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                         var idx = Number(prevMonth)-1;
                         $('#monthlyMonthCombo2').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                         if(monthVal!=null&&monthVal!=""){
                             $('#monthlyMonthCombo2').val(monthVal);
                         }
                         $('#monthlyMonthCombo2').selectbox();

                         getSearchDate2(DateType.MONTHLY,flag);
                     });
         }

         function getSearchDate2(_dateType,flag){
             var startDate='';
             var endDate='';

             if(flag == false){
                 $('#_monthly').trigger('click');

                 $('#monthlyYearCombo2').selectbox();
                 $('#monthlyMonthCombo2').selectbox();
             }
         }

        //데이터 가져오기
        function getData(year, month){
             var searchCondition = initCalendarParamSet();

             //alert("beginDate"+year+lPadStr(month,"0",2)+"01"+ ":::" +  "endDate"+year+lPadStr(month,"0",2)+"31"+ ":::" +  "contractId"+searchCondition[6]+ ":::" +  "mdsId"+searchCondition[4]+ ":::" +  "channel"+searchCondition[5]+ ":::" +  "type"+$('#mvmMiniType').val()+ ":::" + supplierId+$('#supplierId').val());
             $.post(
                     "${ctx}/gadget/mvm/getCalData.do",
                      {"beginDate":year+lPadStr(month,"0",2)+"01", "endDate":year+lPadStr(month,"0",2)+"31", "contractId":searchCondition[6], "mdsId":searchCondition[4], "channel":searchCondition[5], "type":$('#mvmMiniType').val(),supplierId:$('#supplierId').val()},
                      setData_callback
             ); //end $.post
         }

      //데이터 가져오기
        function getChart(year, month){
             var searchCondition = initCalendarParamSet();
             $.post(
                     "${ctx}/gadget/mvm/getCalChart.do",
                      {"beginDate":year+lPadStr(month,"0",2)+"01", "endDate":year+lPadStr(month,"0",2)+"31", "contractId":searchCondition[6], "mdsId":searchCondition[4], "channel":searchCondition[5], "type":$('#mvmMiniType').val(),supplierId:$('#supplierId').val()},
                      setChart_callback
             ); //end $.post
         }

         function lPadStr(str, ch, len){
            /*
            *********************************************************************************************************
            *   함수설명  : 문자열을 정해진 길이만큼 왼쪽을 특정 문자로 채운다.
            * str    : 문자열
            * len    : 총길이
            ***********************************************************************************************************
            */
             var strlen = trim(str).length;
             var ret = "";
             var alen = len - strlen;
             var astr = "";

             //부족한 숫자만큼  len 크기로 ch 문자로 채우기
             for (i=0; i<alen; ++i)
             {
              astr = astr + ch;
             }

             ret = astr + trim(str); //앞에서 채우기

             return ret;
            }

         function trim(str){
                /*
                *********************************************************************************************************
                *   함수설명  : 문자열에서 양쪽의 공백을 제거한다.
                * str    : 문자열
                ***********************************************************************************************************
                */
                if( str == "" || str.length ==0 )
                {
                  return str;
                }
                else
                {
                  return(lTrim(rTrim(str)));
                }
            }


         function lTrim(str){
                /*
                *********************************************************************************************************
                *   함수설명  : 문자열에서 왼쪽의 공백을 제거한다.
                * str    : 문자열
                ***********************************************************************************************************
                */
              var i;
              i = 0;
              while (str.substring(i,i+1) == ' ' || str.substring(i,i+1) == '　')  i = i + 1;
              return str.substring(i);
            }

            function rTrim(str){
                /*
                *********************************************************************************************************
                *   함수설명  : 문자열에서 오른쪽의 공백을 제거한다.
                * str    : 문자열
                ***********************************************************************************************************
                */


              var i = str.length - 1;
              while (i >= 0 && (str.substring(i,i+1) == ' ' || str.substring(i,i+1) == '　')) i = i - 1;
              return str.substring(0,i+1);
            }

         function setData_callback(json, textStatus){

             $('div#divHTML').html(json.result.html);
         }

         function setChart_callback(json, textStatus){
             $('div#divHTML').html(json.result.html);
         }

         function showCal(){

             var show_type = $("input:radio[name=show_type]:checked").val();

             var year = $('#monthlyYearCombo2').val();
             var month = $('#monthlyMonthCombo2').val();

             if(show_type == "Figure"){
                 $('#dataTable1').hide();
                 getChart(year, month);
             }else if(show_type == "Data"){
                 $('#dataTable1').show();
                 getData(year, month);
             }
         }


        /*]]>*/
    </script>
</head>
<body bgcolor="#FFFFFF">

<form id="f"><input type="hidden" id="supplierId"
	value="<%=supplierId%>"></input>

<div id="monthly"><!-- <label id="monthlyLabel"><fmt:message key="aimir.monthly" /></label> -->
<ul>
	<li>
	<button id="monthlyLeft" type="button" class="back"></button>
	</li>
	<li><select id="monthlyYearCombo2"></select></li>
	<li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
	<li class="space5"></li>
	<li><select id="monthlyMonthCombo2" class="sm"></select></li>
	<li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
	<li>
	<button id="monthlyRight" type="button" class="next"></button>
	</li>
</ul>
<ul>
	<li><input name="show_type" type="radio" value="Data"
		class="trans" onclick="" checked="checked"></li>
	<li class="gray11pt withinput"><fmt:message key="aimir.data" /></li>
	<li><input name="show_type" type="radio" value="Figure"
		class="trans" onclick=""></li>
	<li class="gray11pt withinput"><fmt:message key="aimir.graph" /></li>
</ul>
<div id="btn">
<ul>
	<li><a href="javascript:showCal();" class="on"><fmt:message
		key="aimir.button.search" /></a></li>
</ul>
</div>
</div>
</form>

<div id="dataTable1" class="alert-group">
	<div class="margin-t20px" style="text-align:center">
		<span><button type="button" class="alert5"></button></span>
		<span><fmt:message key="aimir.criticalpeak" /></span>	
		<span><button type="button" class="alert1"></button></span>
		<span><fmt:message key="aimir.peak" /></span>
		<span><button type="button" class="alert2"></button></span>
		<span><fmt:message key="aimir.offpeak" /></span>
		<span><button type="button" class="alert6"></button></span>
		<span><fmt:message key="aimir.total" /></span>
		<span><button type="button" class="alert4"></button></span>
		<span><fmt:message key="aimir.co2formula" /></span>	</div>
</div>

<div id="divHTML"></div>
</body>
</html>