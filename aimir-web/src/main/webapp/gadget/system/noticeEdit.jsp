<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
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

//Content는 HTML에 삽입할때 방법이 다르므로 위 방법으로 받았음.

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

	function init() {
		//id, subject, category는 초기 값, 변수명1은 innerHtml값, 변수명2는 임시값
		if(location.search){ //주소중 파라메터 부분이 있으면,
			 var str0=decodeURIComponent(location.search.split("&")[0], "UTF-8"); //첫번째 파라메터를 str0에 담음
			 var str1=decodeURIComponent(location.search.split("&")[1], "UTF-8"); //두번째 파라메터를 str1에 담음
			 var str2=decodeURIComponent(location.search.split("&")[2], "UTF-8"); //세번째 파라메터를 str2에 담음
			 var id=str0.split("=")[1]; //첫번째 파라메터의 값
			 var subject=str1.split("=")[1]; //두번째 파라메터의 값
			 var category=str2.split("=")[1]; //두번째 파라메터의 값
		}
		var id2 = "<input type = hidden name = id value = " + id + ">";		
	    var subject2 = "<input type=text size=40 maxlength=50 name=subject value = " + subject + ">";
		var category2 = "<select name=category>";
         if (category == "공지사항") {
        	 category2 += "<option selected value=공지사항>공지사항</option>";
         } else {
        	 category2 += "<option value=공지사항>공지사항</option>";
         }
         if (category == "긴급사항") {
        	 category2 += "<option selected value=긴급사항>긴급사항</option>";
         } else {
        	 category2 += "<option value=긴급사항>긴급사항</option>";
         }
         if (category == "알림사항") {
        	 category2 += "<option selected value=알림사항>알림사항</option>";
         } else {
        	 category2 += "<option value=알림사항>알림사항</option>";
         }
         category2 += "</select>";

       //  var content2 = "<textarea style=width:590px; height: px name=content id=content>"
       // 	   + content + "</textarea>";
       // 위에서 String으로 처리함.
		$('#id1').html(id2);
		$('#subject1').html(subject2);
		$('#category1').html(category2);
	}
	//Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML)
	initRTE("${ctx}/js/rtef/images/", "${ctx}/js/rtef/", "", true);
	
	// Optional javascript rteSafe function
	// var SAFE_CONTENT = rteSafe(YOUR_CONTENT);
	// writeRichText('rte1', SAFE_CONTENT, '', 500, 150, true, false, false);

</script>

</head>
<body onload="init()";>
<form action="${ctx}/gadget/system/notice/editNotice.do" method="post" id="RTEDemo" name="RTEDemo" onsubmit="return submitForm();">
<span id = id1></span>
<table border=1 bordercolor=#D3E7F7>
	<tr>
		<td width=100 height=30 style='vertical-align:middle;' align=center bgcolor=#EEF5FE>제목</td>
		<td colspan=3 width=200 style='vertical-align:middle;' align=left><span id = subject1></span></td>
		<td width=100 height=30 style='vertical-align:middle;' align=center bgcolor=#EEF5FE>카테고리</td>
		<td width=100 height=30 style='vertical-align:middle;' align=left><span id = category1></span></td>
	</tr>
	<tr>
		<td colspan=6 width=500 height=400>
	<script type="text/javascript"><!--
	//Usage: writeRichText(fieldname, html, css_override, width, height, buttons, readOnly, fullscreen)
	var str3=decodeURIComponent(location.search.split("&")[3], "UTF-8"); //네번째 파라메터를 str3에 담음
    //var content=str3.split("=")[1]; //네번째 파라메터의 값
    var content = str3.replace('content=', '');
	writeRichText('content', content, '', 530, 400, true, false, false);
	//--></script>
	   <noscript>
			<textarea rows="" cols="" style="width:590px; height: px" name="content" id="content">
            </textarea>
	    </noscript>	
		</td>
	</tr>
	<tr align=center>
		<td colspan=3 width=200 height=30 bgcolor=#EEF5FE style='vertical-align:middle;' align=center>
		<div id='btn'><ul><li><input type=submit value=수정></li></ul></div></td>
		<td colspan=3 width=200 height=30 bgcolor=#EEF5FE style='vertical-align:middle;' align=center>
		<div id='btn'><ul><li><input type=button onclick=clear() value=취소></li></ul></div></td>
	</tr>
</table>
</form>
</body>
</html>