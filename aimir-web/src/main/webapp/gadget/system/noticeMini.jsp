<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">

    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">


    </script>
</head>

<body>

<div class="margin10px">


    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="133" id="noticeListEx">
        <param name="movie" value="${ctx}/flexapp/swf/NoticeMini.swf" />
       <!--[if !IE]>-->
       <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/NoticeMini.swf" width="100%" height="128" id="noticeListFlexOt">
       <!--<![endif]-->
       <!--[if !IE]>-->
       </object>
       <!--<![endif]-->
    </object>

</div>

</body>
</html>