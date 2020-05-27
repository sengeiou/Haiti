<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>	
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
table.dataTable thead tr th {
    border-top: 1px solid black;
    border-right: 1px solid black;
    border-bottom: 0px;
}
table.dataTable thead tr th:first-child {
    border-left: 1px solid black;
}
table.dataTable thead tr:last-child th {
    border-bottom: 1px solid black;
}
</style>
<script type="text/javascript" src="<%=request.getContextPath()%>/firmware/resources/jquery-3.2.1.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/firmware/resources/bootstrap-theme.min.css"> 
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/firmware/resources/stylsheet.css" title="Style">
<script type="text/javascript" src="<%=request.getContextPath()%>/firmware/resources/script.js"></script>
<script>

function downloadFile(fileName) {
    var url = '${ctx}/firmware/list/fileDownload.do?fileName=' + fileName;
    var downform = document.getElementsByName("excelDownForm")[0];
    downform.action = url;
    downform.submit();
}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Firmware</title>
</head>
<div class="header">
<h1 class="alert alert-success">Firmware</h1>
</div>
<div class="contentContainer">
	<div class="">Multi files upload to server</div>
	<form name="fileForm" action="${ctx}/requestupload.do" method="post" enctype="multipart/form-data">
		<input multiple="multiple" type="file" name="file" style="border: solid 0.5px;"/>
		<!-- <input type="text" name="src" /> -->
		<input type="submit" value="Àü¼Û" />
	</form>
	<br/><br/>
	<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0">
		<!-- <caption><span>Documents</span><span class="tabEnd">&nbsp;</span></caption> -->
		<tbody>
			<tr>
				<th class="colFirst" scope="col">Files</th>
			</tr>
		</tbody>
		<tbody>
		    <c:choose>
		    	<c:when test="${not empty fileDatas}">
		        	<c:forEach var="item" items="${fileDatas}">
				    	<tr class="altColor"><td><li><a href="javascript:downloadFile('${item.fileName}')">${item.fileName}</a></li></td></tr>
				    </c:forEach>
		        </c:when>
		    <c:otherwise>
		    </c:otherwise>
		    </c:choose>
		</tbody>
	</table>
</div>    
</html>

<form name="excelDownForm" id="excelDownForm" method="post" target="downFrame" style="display: none;">
</form>
<iframe name="downFrame" style="display: none;"></iframe>