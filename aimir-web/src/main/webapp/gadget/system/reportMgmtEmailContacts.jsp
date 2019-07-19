<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<!--	<//%@ include file="/gadget/system/preLoading.jsp"%> -->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Insert title here</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
</head>
<body>
<div id="wrap">
<table class="report_mgmt">
	<colgroup>
		<col width="120px"/>
		<col width="145px"/>
		<col width=""/>
	</colgroup>
	<tr><th><select id="" class="email_select_width"></select></th>
		<td><input type="text" id="" class="email_input_width"/></td>
		<td><a href="#" class="btn_blue"><span>검색</span></a></td>
	</tr>
	<tr><td colspan="3" class="email_dotted"></td></tr>
	<tr><th><select id="" class="email_select_width"></select></th>
		<td><input type="text" id="" class="email_input_width"/></td>
		<td><a href="#" class="btn_blue"><span>추가</span></a>
			<a href="#" class="btn_blue"><span>삭제</span></a>
		</td>
	</tr>
	<tr><th><select id="" class="email_select_width"></select></th>
		<td><span><input type="text" id="" style="width:40px"/></span>
			<span><input type="text" id="" style="width:95px"/></span></td>
		<td><a href="#" class="btn_blue"><span>추가</span></a></td>
	</tr>
	<tr><td colspan="3" class="email_dotted"></td></tr>
</table>

<div class="margin-t10px">Ext-js Grid</div>

</div>
</body>
</html>