<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="application/vnd.ms-excel;charset=utf-8" %>
<%
 response.setHeader("Content-Disposition", "inline; filename=onDemandReport.xls");
 response.setHeader("Content-Description", "JSP Generated Data");
 response.setCharacterEncoding("utf-8");
 String excelData = request.getParameter("excelData"); 
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Excel출력</title>
</head>
<body>
<%= excelData %>
</body>
</html>

