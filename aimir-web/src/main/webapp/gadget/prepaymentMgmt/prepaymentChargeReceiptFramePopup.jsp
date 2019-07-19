<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <%-- <%@ include file="/gadget/system/preLoading.jsp"%> --%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <style type="text/css">
        body {
            margin: 0px;
            padding: 0px;
            width: 750px;
            height: 685px;
        }
        span {
            float:none !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var obj = window.opener.obj;

        $(document).ready(function(){
            var url = "${ctx}/gadget/prepaymentMgmt/prepaymentChargeReceiptPopup.do";
            url += "?supplierId=" + obj.supplierId;
            url += "&contractId=" + obj.contractId;
            url += "&prepaymentLogId=" + obj.prepaymentLogId;
            receipPopup.location.href = url;
        });

    /*]]>*/
    </script>
</head>
<body>
    <iframe name="receipPopup" style="margin: 0px; padding: 0px; width: 100%; height: 100%; border: 0px;"></iframe>

    <div style="padding-left: 20px; align: center;">
        <center>
        <span class="am_button margin-l10 margin-t1px">
            <a href="javascript:receipPopup.print();" class="on"><fmt:message key="aimir.button.print" /></a>
        </span>
        <span class="am_button margin-l10 margin-t1px">
            <a href="javascript:this.close();" class="on"><fmt:message key="aimir.board.close" /></a>
        </span>
        </center>
    </div>


    <div style="height: 60px"></div>
</body>
</html>