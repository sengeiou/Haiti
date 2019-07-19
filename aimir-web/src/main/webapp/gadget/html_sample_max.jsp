<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>

<link href="../css/gadget_ext.css" rel="stylesheet" type="text/css">

</head>
<body> 

<div class="nuri_max_title_bor_bott35a">
<ul>
<li style="padding:4px 5px 0 0"><h1><fmt:message key="aimir.usergroup" /></h1></li><li><select name="" class="nuri_search_n" style="width:150px; padding:4px">
  <option selected>서울</option>
  <option>한국전력</option>
</select></li><li style="padding:4px 5px 0 15px"><h1><fmt:message key="aimir.id" /></h1></li><li><input name="customer_num" type="text" class="nuri_search" style="width:170px; padding:4px"></li>
</ul>
</div>


<!--상단탭-->
<div class="gad_sub_tab">
 <ul >
  <li><a href="#"><fmt:message key="aimir.group.user.detail" /></a></li>
  <li><a href="#" id="current"><fmt:message key="aimir.list.user" /></a></li>
  <li><a href="#">새 그룹 등록</a></li>
 </ul>
</div>
<!--상단탭 끝-->





<!--오른쪽 가젯리스트-->
<div id="nuri_max_right_s6">
<div class="nuri_max_bor_bott_n5">
<ul style=" border-bottom:1px solid #ccc; width:100%; padding:10px 0 0 0">
<li style="font-weight:bold;">사용자 그룹</li>
</ul>
<ul>
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.id" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li>
</ul>
<ul style="background:#f1f1f1">
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.name" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li>
</ul>
<ul>
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.alias" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li>
</ul>
<ul style="background:#f1f1f1">
<li style="font-weight:bold; padding:0 10px 0 0"><fmt:message key="aimir.password" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li><li style="font-weight:bold; padding:0 10px 0 10px">비밀번호 확인</li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li>
</ul>
<ul>
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.email" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:350px; padding:2px"></li>
</ul>
<ul style="background:#f1f1f1">
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.tel.no" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:350px; padding:2px"></li>
</ul>
<ul>
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.lastmodifiedtime.password" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:150px; padding:2px"></li>
</ul>
<ul style="background:#f1f1f1">
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.user.logindenied.check" /></li><li><input name="" type="radio" value=""></li><li style="width:40px">Yes</li><li><input name="" type="radio" value=""></li><li style="width:40px">No</li>
</ul>
<ul>
<li style="font-weight:bold; width:170px"><fmt:message key="aimir.user.logindenied.reason" /></li><li><input name="customer_num" type="text" class="nuri_search_n" style="width:350px; padding:2px"></li>
</ul>
<div style="padding:10px 0 20px 0; float:left; width:100%;" class="nuri_tl">
<ul style="width:400px; margin:0 auto">
<li style="padding:0 10px 0 0">
<div id="nuri_btn"><ul>
  <li class="input"><a href="#"><span ><fmt:message key="aimir.new.user" /></span></a></li></ul>
</div>
</li>
<li style="padding:0 10px 0 20px">
<div id="nuri_btn"><ul>
  <li class="input"><a href="#"><span ><fmt:message key="aimir.update" /></span></a></li></ul>
</div>
</li>
<li>
<div id="nuri_btn"><ul>
  <li class="nuri_cancel"><a href="#"><span ><fmt:message key="aimir.button.delete" /></span></a></li></ul>
</div>
</li>
</ul>
</div>

</div>
</div>
<!--오른쪽 가젯리스트 끝-->











<!--사용자 그룹 상세정보 왼쪽 시작-->
<div id="niro_max_left_6" >

테이블

</div>




<!--사용자 그룹 상세정보 왼쪽 끝-->








</div>






</div>






</body>
</html>
