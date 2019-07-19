<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${ctx}/js/rtef/lang/en.js"></script>
<script type="text/javascript" src="${ctx}/js/rtef/richtext_compressed.js"></script>
<script type="text/javascript" src="${ctx}/js/rtef/xhtml.js"></script>

<title>공지사항 작성</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<script type="text/javascript">

	function submitForm() {
		//make sure hidden and iframe values are in sync before submitting form
		updateRTEs();
		//change the following line to true to submit form
		//var form = document.RTEDemo; 
	    //form.target = "top"; 
	    //form.submit(); 
	    refresh(); //글이 입력 되고 나서 새로운 글을 반영하기 위한 것
		return true;
	}
	
	function refresh() {
		parent.location.reload();
	}
    //Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML)
    initRTE("${ctx}/js/rtef/images/", "${ctx}/js/rtef/", "", true);

	// Optional javascript rteSafe function
	// var SAFE_CONTENT = rteSafe(YOUR_CONTENT);
	// writeRichText('rte1', SAFE_CONTENT, '', 500, 150, true, false, false);

</script>
</head>
<body>
<form action="${ctx}/gadget/system/notice/addNotice.do" method="post" id="RTEDemo" name="RTEDemo" onsubmit="return submitForm();">
<table border=1 bordercolor=#D3E7F7>
	<tr>
		<td width=50 height=30 align=center bgcolor=#EEF5FE>제목</td>
		<td colspan=3 width=200 align=left><input type=text size=35 maxlength=50 name=subject></td>
		<td width=100 height=30 align=center bgcolor=#EEF5FE>카테고리</td>
		<td width=100 height=30 align=left><select name=category>
			<option value=공지사항>공지사항</option>
			<option value=긴급사항>긴급사항</option>
			<option value=알림사항>알림사항</option>
		</select></td>
	</tr>
	<tr>
		<td colspan=6 width=500 height=400>
	<script type="text/javascript"><!--
	//Usage: writeRichText(fieldname, html, css_override, width, height, buttons, readOnly, fullscreen)
	writeRichText('content', '', '', 530, 400, true, false, false);
	//--></script>
			<noscript>
				<textarea rows="" cols="" style="width:500px; height: px" name="content" id="content">
				</textarea>
			</noscript>
		</td>
	</tr>
	<tr align=center>
		<td colspan=3 width=200 height=30 bgcolor=#EEF5FE>
		<div id='btn'><ul><li><input type=submit value=작성></li></ul></div></td>
		<td colspan=3 width=200 height=30 bgcolor=#EEF5FE>
		<div id='btn'><ul><li><input type=button onclick=clear() value=취소></li></ul></div></td>
	</tr>
</table>
</form>
</body>
</html>