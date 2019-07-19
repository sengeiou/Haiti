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

    function deleteNotice(noticeId) {
        $.ajax( {
            url : "${ctx}/gadget/system/notice/deleteNotice.do?noticeId="
                    + noticeId,
            cache : false,
            success : function() {

            }
        });
    }

    function hitsPlus(noticeId) {
        $.ajax( {
            url : "${ctx}/gadget/system/notice/hitsPlus.do?noticeId=" + noticeId
        });
    }

    function writeForm(subject, category, content) {
    	var innerHtml = "";
        innerHtml += "<form name=writeForm action=${ctx}/gadget/system/notice/addNotice.do method=post>"
            + "<input type=hidden name=subject value='" + subject + "'>"
            + "<input type=hidden name=category value='" + category + "'>"
            + "<input type=hidden name=content value='" + content + "'>"
            + "</form>";
        $('#contents').html(innerHtml);
        document.writeForm.submit(); 
    }

    function editForm(id, subject, category, content) {
        var innerHtml = "";
        innerHtml += "<form name=editForm action=${ctx}/gadget/system/notice/editNotice.do method=post>"
        	+ "<input type=hidden name = id value = " + id + ">"
            + "<input type=hidden name=subject value='" + subject + "'>"
            + "<input type=hidden name=category value='" + category + "'>"
            + "<input type=hidden name=content value='" + content + "'>"
            + "</form>";
        $('#contents').html(innerHtml);
        document.editForm.submit(); 
    }

    function searchForm(searchDetail, searchCategory, searchWord, startDate, endDate) {
    	var searchWord = $(":input[name='searchWord']").val();
        var searchDetail = $(":input[name='searchDetail']").val();
        var searchCategory = $(":input[name='searchCategory']").val();

        //한글입력을 위한 인코딩
        searchWord = escape(encodeURIComponent(searchWord));
        searchDetail = escape(encodeURIComponent(searchDetail));
        searchCategory = escape(encodeURIComponent(searchCategory));
        startDate = escape(encodeURIComponent(startDate));
        endDate = escape(encodeURIComponent(endDate));

        $.getJSON("${ctx}/gadget/system/notice/searchNotice.do?searchWord=" + searchWord + "&searchDetail=" + searchDetail
                + "&searchCategory=" + searchCategory + "&startDate=" + startDate + "&endDate=" + endDate,
            function(json) {
                searchObject = null;
                searchObject = json.searchList;
                searchBinding();
        });
    }
    

    </script>
</head>

<body>

<div style="padding : 20px">
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="800" id="noticeListEx">
        <param name="movie" value="${ctx}/flexapp/swf/NoticeMax.swf" />
       <!--[if !IE]>-->
       <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/NoticeMax.swf" width="100%" height="500" id="noticeListFlexOt">
       <!--<![endif]-->
       <!--[if !IE]>-->
       </object>
       <!--<![endif]-->
    </object>

</div>
<div id="contents"></div>

</body>
</html>