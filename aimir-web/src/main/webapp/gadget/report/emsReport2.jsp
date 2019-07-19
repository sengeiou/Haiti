<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" >



	$(document).ready(function(){

		$(function() { $('#reportType')    .bind('change', function(event) { if($('#reportType').val()!='1'){$('#periodType').option('0');$('#contentType').option('0');$('#Label_periodType').html('');$('#Label_contentType').html('');}else{$('#Label_periodType').html('');$('#Label_contentType').html('');} } ); });
		$(function() { $('#periodType')    .bind('change', function(event) { if($('#reportType').val()!='1'&&$('#periodType').val()!='0'){$('#periodType').val('0');$('#periodType').selectbox();$('#Label_periodType').html('선택할 수 없습니다.');}else{$('#Label_periodType').html('');} } ); });
		$(function() { $('#contentType')   .bind('change', function(event) { if($('#reportType').val()!='1'&&$('#contentType').val()!='0'){$('#contentType').val('0');$('#contentType').selectbox();$('#Label_contentType').html('선택할 수 없습니다.');}else{$('#Label_contentType').html('');} } ); });
        
		
		$("#date")     .datepicker({maxDate:'-1d',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
	    $('#reportType').selectbox();
	    $('#periodType').selectbox();
	    $('#contentType').selectbox();

	    $('#searchWeekOfYear').selectbox();
	    $('#searchMonth').selectbox();
	    $('#searchQuarter').selectbox();
	    $('#searchYear').selectbox();
		
	});
	
	var win;
	function report(){

		var report = "emsReport.rptdesign"; 
        var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
        var params='';

        if(win)
        	win.close();
        if(getPeriodType()) {
			if(getPeriodType() == "1") {
				if(getSearchDate()) {				
		            params = params + '&periodType=' + getPeriodType(); // 1,3,4,8,9 - 일,주,월,년,분기
		            params = params + '&periodTypeName=' + $('#periodType > option:selected').text(); // 1,3,4,8,9 - 일,주,월,년,분기
		            params = params + '&searchDate=' + getSearchDate();
		            params = params + '&year=' + $('#searchYear').val(); //주별,월별,분기별,년도별일경우 사용
		            params = params + '&weekOfYear=' + $('#searchWeekOfYear').val(); //주별일경우 사용 해당년도의 1주부터 5x주
		            params = params + '&month=' + $('#searchMonth').val(); // 월별일경우 사용
		            params = params + '&quarter=' + $('#searchQuarter').val(); // 주기별일경우 사용
		            params = params + '&report1=1';// 에너지별 사용량 보고서 , 값이 1일경우 visible else hidden
		            params = params + '&report2=1';// 공조에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		            params = params + '&report3=1';// 전기에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		            params = params + '&report4=1';// 기타에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		            params = params + '&report5=1';// 이상발생통계보고서 ,  값이 1일경우 visible else hidden
		            
		            var localport = "<%= request.getLocalPort() %>";
		            var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		            win = window.open(birtURL, "EmsReportExcel", opts);
				}
			} else {
				params = params + '&periodType=' + getPeriodType(); // 1,3,4,8,9 - 일,주,월,년,분기
	            params = params + '&periodTypeName=' + $('#periodType > option:selected').text(); // 1,3,4,8,9 - 일,주,월,년,분기
	            params = params + '&searchDate=' + getSearchDate();
	            params = params + '&year=' + $('#searchYear').val(); //주별,월별,분기별,년도별일경우 사용
	            params = params + '&weekOfYear=' + $('#searchWeekOfYear').val(); //주별일경우 사용 해당년도의 1주부터 5x주
	            params = params + '&month=' + $('#searchMonth').val(); // 월별일경우 사용
	            params = params + '&quarter=' + $('#searchQuarter').val(); // 주기별일경우 사용
	            params = params + '&report1=1';// 에너지별 사용량 보고서 , 값이 1일경우 visible else hidden
	            params = params + '&report2=1';// 공조에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
	            params = params + '&report3=1';// 전기에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
	            params = params + '&report4=1';// 기타에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
	            params = params + '&report5=1';// 이상발생통계보고서 ,  값이 1일경우 visible else hidden
	            
	            var localport = "<%= request.getLocalPort() %>";
	            var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
	            win = window.open(birtURL, "EmsReportExcel", opts);
			}
        }
        	        
	}

	function getSearchDate() {
		var searchDate = $("#date").val();
		if(getPeriodType() == "1") { 
			if(searchDate == null || searchDate == "") {
				 alert("날짜를 입력하세요.");
				 return false;
			} else {
				 return searchDate.substr(0, 4) + searchDate.substr(5, 2) + searchDate.substr(8, 2);
			}
		} else {
		    return "20100101";
		}
		
	}

	function getPeriodType() {
        var periodType = $("#periodType").val();
        if(periodType == null || periodType == "0") {
             alert("조회주기를 선택하세요.");
             return false;
        } else {
             return periodType;
        }
    }
		
</script>
</head>
<body>
	<div id="container"><!-- id="bems_manage"  -->
	    <ul>
	       <li class="Tbk">1.출력할 보고서종류를 선택하세요.</li>
	       <li class="h30">
		       <select id="reportType" style="width:150px">
	                <option value="0">에너지 목표관리 보고서</option>
	                <option value="1" selected>사용량 통계 보고서</option>
	                <option value="2">관리 현황 보고서</option>
	           </select>
	       </li>
	       
		   <li class="Tbk">2.조회주기를 선택하세요.</li>
           <li class="h30">
			<select id="periodType" style="width:150px">
			    <option value="0">해당무</option>
	            <option value="1">일간</option>
	            <option value="3">주간</option>
	            <option value="4">월간</option>
	            <option value="9">분기</option>
	            <option value="8">년간</option>
	        </select>
	        <label id="Label_periodType"></label>
	        </li>
	        
	       <li class="Tbk">3.출력할 보고서 구성을 선택하세요.</li>
           <li class="h30">
            <select id="contentType" style="width:150px">
                <option value="0">해당무</option>
                <option value="1">에너지별 사용량 통계</option>
                <option value="2">공조 에너지 사용량 통계</option>
                <option value="3">전기 에너지 사용량 통계</option>
                <option value="4">기타 에너지 사용량 통계</option>
                <option value="5">이상 발생 통계</option>
            </select>
            <label id="Label_contentType" style="color: red"></label>
            </li>
            
            <li class="Tbk">4.조회 기간을 선택하세요</li>
            <li class="h30">일간<input id="date" type="text" class="day" readonly="readonly">
            </li>
            <!-- 임시 -->
            <li class="h30">주간
            <select id="searchWeekOfYear" style="width:50px">
                <option value="35">35주</option>
                <option value="36">36주</option>
                <option value="37">37주</option>
                <option value="38">38주</option>
                <option value="39">39주</option>
                <option value="40">40주</option>
                <option value="41">41주</option>
                <option value="42">42주</option>
                <option value="43" selected>43주</option>
            </select>
            <label id="Label_contentType" style="color: red"></label>
            </li>
            <li class="h30">월간
            <select id="searchMonth" style="width:50px">
                <option value="1">1월</option>
                <option value="2">2월</option>
                <option value="3">3월</option>
                <option value="4">4월</option>
                <option value="5">5월</option>
                <option value="6">6월</option>
                <option value="7">7월</option>
                <option value="8">8월</option>
                <option value="9">9월</option>
                <option value="10" selected>10월</option>
                <option value="11">11월</option>
                <option value="12">12월</option>
            </select>
            <label id="Label_contentType" style="color: red"></label>
            </li>
            <li class="h30">분기
            <select id="searchQuarter" style="width:50px">
                <option value="1">1분기</option>
                <option value="2">2분기</option>
                <option value="3">3분기</option>
                <option value="4" selected>4분기</option>
            </select>
            <label id="Label_contentType" style="color: red"></label>
            </li>
            <li class="h30">연간
            <select id="searchYear" style="width:50px">
                <option value="2010" selected>2010년</option>
            </select>
            <label id="Label_contentType" style="color: red"></label>
            </li>
            <!--------->
            
        </ul>
		<br/>
	    <em class="bems_button"><a href="javascript:report();">보고서 출력</a></em>
	</div>
</body>
</html>