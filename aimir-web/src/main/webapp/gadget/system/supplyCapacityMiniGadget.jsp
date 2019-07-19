<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Insert title here</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">

        var supplierId = "";
        var loginId    = "";
        // Command 실행권한
        var cmdAuth = "${cmdAuth}";

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                        loginId = json.loginId;
                    }
                }
        );

        $(document).ready(function() {
            // 실행권한 체크
            if (cmdAuth == "true") {
                $("#cmdBtn").show();
                $("#cmdEmptyBtn").hide();
            } else {
                $("#cmdBtn").hide();
                $("#cmdEmptyBtn").show();
            }
            $('#switchStatus').selectbox();
        });

        var search = function() {
            getFlexObject('supplyCapacityMiniGrid').search();
        };

        var supplyCapacityA = function(_enable) {
            getFlexObject('supplyCapacityMiniGrid').save(_enable);
        };

        var getConditionArray = function() {
            var condition = Array();
            condition[0] = $('#switchStatus').val();
            condition[1] = loginId;
            condition[2] = "<fmt:message key="aimir.target.Off"/>";
            condition[3] = "<fmt:message key="aimir.target.On"/>";

            return condition;
        };

        var getFmtMessage = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.checkBox"/>";
            fmtMessage[1] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[2] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[3] = "<fmt:message key="aimir.equipid"/>";
            fmtMessage[4] = "<fmt:message key="aimir.reason.cutOnOff"/>";
            fmtMessage[5] = "<fmt:message key="aimir.status"/>";
            fmtMessage[6] = "<fmt:message key="aimir.mcuid"/>";

            return fmtMessage;
        };

    </script>
</head>

<body>

<!-- 검색옵션 (S) -->
<div class="search-bg-basic">
<ul class="basic-ul">
    <li class="basic-li withinput"><fmt:message key="aimir.target"/></li>
    <li class="basic-li">
        <select id="switchStatus" name="select" style="width:120px;" onchange="javaScript:search();">
            <option value=""><fmt:message key="aimir.all"/></option>
            <option value="Activation"><fmt:message key="aimir.target.Off"/></option>
            <option value="Deactivation"><fmt:message key="aimir.target.On"/></option>
        </select>
    </li>
</ul>
</div>
<!-- 검색옵션 (E) -->

<div id="cmdBtn" class="btn_right_top2 margin-t10px">
    <em class="btn_org"><a href="javaScript:supplyCapacityA('GetStatus');" id="btnSearch"><fmt:message key="aimir.supplystatus"/></a></em>
    <em class="btn_org"><a href="javaScript:supplyCapacityA('Deactivation');" id="btnSearch"><fmt:message key="aimir.supply.cutOff"/></a></em>
    <em class="btn_org"><a href="javaScript:supplyCapacityA('Activation');" id="btnSearch"><fmt:message key="aimir.supply.cutOn"/></a></em>
</div>
<div id="cmdEmptyBtn" class="btn_right_top2 margin-t10px" style="display: none;"></div>

<div class="gadget_body2">
    <object id="supplyCapacityMiniGridEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="400px" >
        <param name="movie" value="${ctx}/flexapp/swf/supplyCapacityMiniGrid.swf" />
        <param name='wmode' value='transparent' />
        <!--[if !IE]>-->
        <object id="supplyCapacityMiniGridOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/supplyCapacityMiniGrid.swf" width="100%" height="400px" >
        <param name='wmode' value='transparent' />
        <!--<![endif]-->
        <!--[if !IE]>-->
        </object>
        <!--<![endif]-->
    </object>
</div>

</body>
</html>