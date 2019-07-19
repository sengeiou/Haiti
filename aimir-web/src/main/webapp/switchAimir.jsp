<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<style type="text/css">
html{overflow:auto !important}
</style>
<title><fmt:message key='aimir.hems.title.login'/></title>
<script type="text/javascript" charset="utf-8">
    var switchAimir = function() {

        var contextPath = '${ctx}';
        var urll = '${url}';
        var port = '${localPort }';
        var ctx ='/aimir-web';
        var strr = urll.split(contextPath);

        if ("/client" == contextPath) {
            urll = strr[0]+ctx+'/customer/login.jsp';
        }else if ("/admin" == contextPath) {
            urll = strr[0]+ctx+'/admin/login.jsp';       
        }else {
            urll = strr[0]+ctx+'/error.jsp';                 
        }
        document.location.href =urll;
        return;
    };
</script>
</head>
<body onload="javascript:switchAimir()">
</body>
</html>