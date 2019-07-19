<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">


    <title>Design Guide</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

</head>
<body>

                <!-- tab (S) -->
                <div id="gad_sub_tab">
                    <ul>
                        <li><a id="current">Tab1</a></li>
                        <li><a id="">Tab2</a></li>
                        <li><a id="">Tab3</a></li>
                        <li><a id="">Tab4</a></li>
                        <li><a id="">Tab5</a></li>
                    </ul>
                </div>
				<!-- tab (E) -->


<div id="gadget_body">



<br/>


				<!-- search (S) -->
				<div id="search-s1">
					<ul>
						<li><input name="name" type="text" value="Search" onclick='javascript:delTxt();' onkeydown="javascript:keyEvent(codeform.name.value);"></li>
						<li id="search-s1-btn"><a href="javascript:codeSearch(this.codeform.name.value);" ></a></li>
					</ul>
				</div>
				<!-- search (E) -->



				<!-- gadget buttons (S) -->
				<div id="gadget_btn">
					<ul>
						<li id="gadget_modi"><a href="" title='<fmt:message key='aimir.update'/>'>����</a></li>
						<li id="gadget_del"><a href="" title='<fmt:message key='aimir.button.delete'/>'>����</a></li>
						<li id="gadget_plus"><a href="" title='<fmt:message key='aimir.add'/>'>�߰�</a></li>
					</ul>
				</div>
				<!-- gadget buttons (E) -->

<br/>
<br/>





<br/>




				<!-- input (S) -->
				<li><input class="blue" type="text" value="input box"></li>
				<!-- input (E) -->



<br/>
<br/>


				<!-- common buttons : Align Left (S) -->
				<div id="btn">
					<ul><li><a href="" class="on"><fmt:message key="aimir.add"/></a></li></ul>
					<ul><li><a href="" class="on"><fmt:message key="aimir.update"/></a></li></ul>
					<ul><li><a href=""><fmt:message key="aimir.cancel"/></a></li></ul>
				</div>
				<!-- common buttons : Align Left (S) -->


				<!-- common buttons : Align Right (S) -->
				<div id="btn-right">
					<ul><li><a href=""><fmt:message key="aimir.cancel"/></a></li></ul>
					<ul><li><a href="" class="on"><fmt:message key="aimir.update"/></a></li></ul>
					<ul><li><a href="" class="on"><fmt:message key="aimir.add"/></a></li></ul>
				</div>
				<!-- common buttons : Align Right (E) -->




				<!-- Searching options (S) -->
				<div id="period">
					<label><fmt:message key="aimir.period"/></label>
					<ul>
						<li><select id="periodType"></select></li>
						<li class="space5"></li>
						<li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
						<li><input value="~" class="between" type="text"></li>
						<li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
					</ul>
					<div id="btn">
								<ul><li><a href="#" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
					</div>
				</div><br/><br/>

				<div id="period">
					<ul class="align">
						<li class="btnleft">
							<button id="todayBtn" type="button" class="sm">small</button>
							<button id="yesterdayBtn" type="button" class="sm">button</button>
							<button id="threeDayBtn" type="button" class="sm">30</button>
							<button id="weekBtn" type="button" class="sm">40</button>
							<button id="monthBtn"type="button" class="sm">50</button>
						</li>
						<li>
							<label><fmt:message key="aimir.period"/></label>
							<ul>
								<li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
								<li><input value="~" class="between" type="text"></li>
								<li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
							</ul>
							<div id="btn">
								<ul><li><a href="#" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
							</div>
					   </li>
					</ul>
				</div><br/><br/><br/>


				<div id="weekly">
					<label><fmt:message key="aimir.weekly"/></label>
					<ul>
						<li><select id="weeklyWeekOrder">
								<option>2010</option>
								<option>2010</option>
								<option>2010</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder" class="sm">
								<option>03</option>
								<option>04</option>
								<option>05</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder">
								<option>1st</option>
								<option>2nd</option>
							</select>
						</li>
					</ul>

					<div id="btn">
						<ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
					</div>
				</div><br/><br/>



				<div id="monthly">
					<label><fmt:message key="aimir.monthly" /></label>
					<ul>
						<button type="button" class="back"></button>
						<li><select id="weeklyWeekOrder">
								<option>2010</option>
								<option>2010</option>
								<option>2010</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder" class="sm">
								<option>03</option>
								<option>04</option>
								<option>05</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<button type="button" class="next"></button>
					</ul>

					<div id="btn">
						<ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
					</div>
				</div><br/><br/>



				<div id="weekdaily">
					<label><fmt:message key="aimir.weekdaily"/></label>
					<ul>
						<li><select id="weeklyWeekOrder">
								<option>2010</option>
								<option>2010</option>
								<option>2010</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder" class="sm">
								<option>03</option>
								<option>04</option>
								<option>05</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder">
								<option>1st</option>
								<option>2nd</option>
							</select>
						</li>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder">
								<option>Sun</option>
								<option>Mon</option>
							</select>
						</li>
					</ul>

					<div id="btn">
						<ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
					</div>
				</div><br/><br/>



				<div id="seasonal">
					<label><fmt:message key="aimir.seasonal" /></label>
					<ul>
						<li><select id="weeklyWeekOrder">
								<option>2010</option>
								<option>2010</option>
								<option>2010</option>
							</select>
						</li>
						<label class="descr"><fmt:message key="aimir.day.mon" /></label>
						<li class="space5"></li>
						<li><select id="weeklyWeekOrder">
								<option>Sun</option>
								<option>Mon</option>
							</select>
						</li>
					</ul>

					<div id="btn">
						<ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
					</div>
				</div>
				<!-- Searching options (E) -->






<br/><br/>





				<!-- select box 200 (S) -->
				<div class="select-200">
					<div class="select open">
						<span class="ctrl"></span>
						<button type="button" class="myValue"><fmt:message key="aimir.supplier.list"/></button>

						<ul class="aList">
							<li><a href="#1">Link_1</a></li>
							<li><a href="#2">Link_2</a></li>
							<li><a href="#3">Link_3</a></li>
							<li><a href="#4">Link_4</a></li>
							<li><a href="#5">Link_5</a></li>
							<li><a href="#6">Link_6</a></li>
							<li><a href="#3">Link_7</a></li>
							<li><a href="#4">Link_8</a></li>
							<li><a href="#5">Link_9</a></li>
							<li><a href="#6">Link_10</a></li>
							<li><a href="#4">Link_11</a></li>
							<li><a href="#5">Link_12</a></li>
							<li><a href="#6">Link_13</a></li>
						</ul>
					</div>
				</div>
				<!-- select box 200 (E) -->



</div>





<table class="customer_detail">
	<tr><th>test1</th>
		<td>test1</td></tr>
	<tr><th>test1</th>
		<td>test1</td></tr>
	<tr><th>test1</th>
		<td>test1</td></tr>
<table>
<br/><br/><br/><br/>



<table class="test">
	<tr><th>test1</th>
		<td>test1</td></tr>
	<tr><th>test1</th>
		<td>test1</td></tr>
	<tr><th>test1</th>
		<td>test1</td></tr>
<table>
<br/><br/><br/><br/>


</body>
</html>