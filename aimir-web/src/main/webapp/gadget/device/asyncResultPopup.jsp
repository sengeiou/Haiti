<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<!-- STYLE -->
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >

	<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
	</style>
	
<!-- LIB -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>	
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
</head>
<body>
<script type="text/javascript" charset="utf-8">

var target="";
var loginId="";
var device="";

$(document).ready(function () {
	// data
    var obj = window.opener.obj;
	result  = obj.result;
	
	$('#commandResult').val(result);
})

</script>
<div id="wrapper" class="max">
	
	</div>
	<div class="margin10px"><label class="check">Operation Result</label></div>
	<!-- <textarea id = commandReseult class="margin10px padding-b10px border_blu" style="height: 100px;"> -->
	<textarea id="commandResult" name="commandResult" style="height: 240px; width: 410px; margin:10px; padding:10px" readonly="readonly">Operation Result</textarea>
	</div>

</div>
</body>
</html>