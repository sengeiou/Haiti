<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Supply Capacity</title>
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

        var tabs     = {hourly:0,daily:1,period:1,weekly:1,monthly:0,monthlyPeriod:1,weekDaily:0,seasonal:0,yearly:0};
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

        $(document).ready(function() {
            $('#tabs').tabs();

            // 실행권한 체크
            if (cmdAuth == "true") {
                $("#addBtn").show();
                $("#cmdBtnList1").show();
                $("#cmdBtnList2").show();
                $("#savePrepaymentBtn").show();
                $("#saveExceedsThresholdBtn").show();
                $("#saveEmergencyBtn").show();
            } else {
                $("#addBtn").hide();
                $("#cmdBtnList1").hide();
                $("#cmdBtnList2").hide();
                $("#savePrepaymentBtn").hide();
                $("#saveExceedsThresholdBtn").hide();
                $("#saveEmergencyBtn").hide();
            }

            $('#_supplyCapacitySetup').click(function() { drawSetupTab(); });

            $('#aGroupType').selectbox();
            $('#bSwitchStatus').selectbox();
            $('#bGroupType').selectbox();
            $('#bCondition').selectbox();
            $('#bMeterStatus').selectbox();
            $('#cStatus').selectbox();
            $('#cGroupType').selectbox();
            $('#timeUnit1').selectbox();
            $('#timeUnit2').selectbox();
        });

        var drawSetupTab = function() {

            $.getJSON('${ctx}/gadget/system/getCircuitBreakerSetting.do',

                function(data) {

                    if(data.prepayment != null) {
                        $("#prepaymentForm input[name='blockingThreshold']").val(data.prepayment.blockingThreshold);
                        $("#prepaymentForm input[name='alarmThreshold']").val(data.prepayment.alarmThreshold);
                        $("#prepaymentForm input:radio[name=automaticDeactivation]").filter('[value=' + data.prepayment.automaticDeactivation +']').attr("checked", "checked");
                        $("#prepaymentForm input:radio[name=automaticActivation]").filter('[value=' + data.prepayment.automaticActivation +']').attr("checked", "checked");
                        $("#prepaymentForm input:checkbox[name=alarm]").filter('[value=' + data.prepayment.alarm +']').attr("checked", "checked");
                    }

                    if(data.exceedsThreshold != null) {

                        $("#exceedsThresholdForm input[name='blockingThreshold']").val(data.exceedsThreshold.blockingThreshold);
                        $("#exceedsThresholdForm input[name='alarmThreshold']").val(data.exceedsThreshold.alarmThreshold);
                        $("#exceedsThresholdForm input:radio[name=automaticDeactivation]").filter('[value=' + data.exceedsThreshold.automaticDeactivation +']').attr("checked", "checked");
                        $("#exceedsThresholdForm input:radio[name=automaticActivation]").filter('[value=' + data.exceedsThreshold.automaticActivation +']').attr("checked", "checked");
                        $("#exceedsThresholdForm input:checkbox[name=alarm]").filter('[value=' + data.exceedsThreshold.alarm +']').attr("checked", "checked");
                        $("#exceedsThresholdForm input[name=recoveryTime]").val(data.exceedsThreshold.recoveryTime);
                        $("#exceedsThresholdForm input[name=timeUnit]").filter('[value=' + data.exceedsThreshold.alarm +']').attr("selected", "selected");
                    }

                    if(data.emergency != null) {
                        $("#emergencyForm input:radio[name=automaticActivation]").filter('[value=' + data.emergency.automaticActivation +']').attr("checked", "checked");
                        $("#emergencyForm input[name=recoveryTime]").val(data.emergency.recoveryTime);
                        $("#emergencyForm input[name=timeUnit]").filter('[value=' + data.emergency.alarm +']').attr("selected", "selected");
                    }
            });
        };

        var savePrepayment = function() {

            var options = {
                success : saveResult,
                url : '${ctx}/gadget/system/saveCircuitBreakerSettingPrepayment.do',
                type : 'post',
                datatype : 'json'
            };

            $('#prepaymentForm').ajaxSubmit(options);
        };

        var saveExceedsThreshold = function() {

            var options = {
                success : saveResult,
                url : '${ctx}/gadget/system/saveCircuitBreakerSettingExceedsThreshold.do',
                type : 'post',
                datatype : 'json'
            };

            $('#exceedsThresholdForm').ajaxSubmit(options);
        };

        var saveEmergency = function() {

            var options = {
                success : saveResult,
                url : '${ctx}/gadget/system/saveCircuitBreakerSettingEmergency.do',
                type : 'post',
                datatype : 'json'
            };


            $('#emergencyForm').ajaxSubmit(options);
        };

        var saveResult = function(data) {
            alert(data.result);
        };

        var getAConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#aGroupType').val();
            arrayObj[1] = $('#aTarget').val();

            return arrayObj;
        };

        var getBConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#bSwitchStatus').val();
            arrayObj[1] = $('#bGroupType').val();
            arrayObj[2] = $('#bTarget').val();
            arrayObj[3] = $('#bCondition').val();
            arrayObj[4] = $('#bMeterStatus').val();

            return arrayObj;
        };

        var getCConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#searchStartDate').val();
            arrayObj[1] = $('#searchEndDate').val();
            arrayObj[2] = $('#cStatus').val();
            arrayObj[3] = $('#cGroupType').val();
            arrayObj[4] = $('#cTarget').val();

            return arrayObj;
        };

        var aSearch = function() {

            aTextareaClear();

            getFlexObject('emergencyElecBlockGroupTypeGrid').search();
        };

        var bSearch = function() {

            bTextareaClear();

            getFlexObject('elecSupplyCapacityGrid').search();
        };

        var cSearch = function() {
            /*
            if(document["circuitBreakerLogChartOt"] == null){
                window["circuitBreakerLogChart"].search();
            }else{
                document["circuitBreakerLogChartOt"].search();
            }

            if(document["circuitBreakerLogGridOt"] == null){
                window["circuitBreakerLogGrid"].search();
            }else{
                document["circuitBreakerLogGridOt"].search();
            }*/
            getFlexObject('circuitBreakerLogChart').search();
            getFlexObject('circuitBreakerLogGrid').search();
        };

        var supplyCapacityA = function(_enable) {
            getFlexObject('emergencyElecBlockGrid').save(_enable);
        };

        var supplyCapacityB = function(_enable) {
            getFlexObject('elecSupplyCapacityGrid').save(_enable);
        };

        var add = function() {

            var checkedData;

            checkedData = getFlexObject('emergencyElecBlockGroupTypeGrid').getCheckedGridData().source;
            getFlexObject('emergencyElecBlockGrid').add(checkedData);

        };

        var send = function() { };

        var emergencyExcel = function() {
            getFlexObject('emergencyElecBlockGroupTypeGrid').exportExcel();
        };

        var supplyCapacityExcel = function() {
            getFlexObject('elecSupplyCapacityGrid').exportExcel();
        };

        var logExcel = function() {
            getFlexObject('circuitBreakerLogGrid').exportExcel();
        };

        var getFmtMessage1 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.checkBox"/>";
            fmtMessage[1] = "<fmt:message key="aimir.targetType"/>";
            fmtMessage[2] = "<fmt:message key="aimir.target"/>";
            fmtMessage[3] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[4] = "<fmt:message key="aimir.meterid2"/>";
            fmtMessage[5] = "<fmt:message key="aimir.supplystatus"/>";
            fmtMessage[6] = "<fmt:message key="aimir.status"/>";

            return fmtMessage;
        };

        var getFmtMessage2 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.checkBox"/>";
            fmtMessage[1] = "<fmt:message key="aimir.targetType"/>";
            fmtMessage[2] = "<fmt:message key="aimir.target"/>";
            fmtMessage[3] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[4] = "<fmt:message key="aimir.meterid2"/>";
            fmtMessage[5] = "<fmt:message key="aimir.supplystatus"/>";
            fmtMessage[6] = "<fmt:message key="aimir.status"/>";
            fmtMessage[7] = loginId;
            return fmtMessage;
        };

        var getFmtMessage3 = function() {

            var fmtMessage = Array();;

            fmtMessage[0] = "<fmt:message key="aimir.checkBox"/>";
            fmtMessage[1] = "<fmt:message key="aimir.location"/>";
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[3] = "<fmt:message key="aimir.target"/>";
            fmtMessage[4] = "<fmt:message key="aimir.mcuid2"/>";
            fmtMessage[5] = "<fmt:message key="aimir.loadmgmt.supplycapacity"/>";
            fmtMessage[6] = "<fmt:message key="aimir.peakdemand"/>";
            fmtMessage[7] = "<fmt:message key="aimir.reason.cutOnOff"/>";
            fmtMessage[8] = "<fmt:message key="aimir.supplystatus"/>";
            fmtMessage[9] = "<fmt:message key="aimir.status"/>";
            fmtMessage[10] = loginId;
            fmtMessage[11] = "<fmt:message key="aimir.excel.elecSupplyCapacity"/>";
            return fmtMessage;
        };

        var getFmtMessage4 = function() {

            var fmtMessage = Array();

            fmtMessage[0] = "<fmt:message key="aimir.date"/>";
            fmtMessage[1] = "<fmt:message key="aimir.targetType"/>";
            fmtMessage[2] = "<fmt:message key="aimir.target"/>";
            fmtMessage[3] = "<fmt:message key="aimir.reason.cutOnOff"/>";
            fmtMessage[4] = "<fmt:message key="aimir.result"/>";
            fmtMessage[5] = "<fmt:message key="aimir.excel.supplyCapacity"/>";
            return fmtMessage;
        };

        var selectAll = function() {
            getFlexObject('emergencyElecBlockGrid').selectAll();
        };
        var deselectAll = function() {
            getFlexObject('emergencyElecBlockGrid').deselectAll();
        };
        var clear = function() {
            getFlexObject('emergencyElecBlockGrid').deleteRow();
        };
        var clearAll = function() {
            getFlexObject('emergencyElecBlockGrid').init();
            aTextareaClear();
        };

        var aTextareaClear = function() {

            $('#aTextarea').val("");
        };

        var bTextareaClear = function() {

            $('#bTextarea').val("");
        };

        var aTextareaChange = function(_value) {

            if ($('#aTextarea').val() != "") {
                $('#aTextarea').val($('#aTextarea').val() + "\n");
            }

            $('#aTextarea').val($('#aTextarea').val() + _value);
        };

        var bTextareaChange = function(_value) {

            if ($('#bTextarea').val() != "") {
                $('#bTextarea').val($('#bTextarea').val() + "\n");
            }

            $('#bTextarea').val($('#bTextarea').val() + _value);
        };

    </script>
</head>

<body>

    <!-- 탭 전체 (S) -->
    <div id="tabs">
        <ul>
            <li><a href="#emergencyElecBlock" id="_emergencyElecBlock"><fmt:message key="aimir.emergency.cutOn"/></a></li>
            <li><a href="#elecSupplyCapacity" id="_elecSupplyCapacity"><fmt:message key="aimir.supply.cutOnOf"/></a></li>
            <li><a href="#supplyCppacityLog" id="_supplyCppacityLog"><fmt:message key="aimir.history.cutOnOff"/></a></li>
            <li><a href="#supplyCapacitySetup" id="_supplyCapacitySetup"><fmt:message key="aimir.setting.cutOnOff"/></a></li>
        </ul>
        <!-- 1ST 탭 : 비상전기차단 (S) -->
        <div id="emergencyElecBlock">

            <!-- 1ST 탭의 내용 : 검색조건 (S) -->
            <div class="search-bg-withouttabs">

                <div class="searchoption-container">
                    <table class="searchoption wfree">
                        <tr>
                            <td>
                                <select style="width:120px;" id="aGroupType">
                                <option value=""><fmt:message key="aimir.targetType"/></option>
                                <c:forEach var="groupType" items="${groupTypes}">
                                    <option value="${groupType}">${groupType}</option>
                                </c:forEach>
                                </select>
                            </td>
                            <td><input type="text" id="aTarget" /></td>
                            <td>
                                <div id="btn">
                                    <ul><li><a href="javaScript:aSearch();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>

            </div>
            <!-- 1ST 탭의 내용 : 검색조건 (E) -->

            <div id="addBtn">
                <div id="btn" class="btn_right_top2 margin-t10px">
                    <ul><li><a href="javaScript:add();" class="on" id="btnSearch"><fmt:message key="aimir.add"/></a></li></ul>
                </div>
            </div>
            <div class="gadget_body2">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="180px" id="emergencyElecBlockGroupTypeGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/emergencyElecBlockGroupTypeGrid.swf" />
                    <param name="wmode" value="opaque">
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/emergencyElecBlockGroupTypeGrid.swf" width="100%" height="180px" id="emergencyElecBlockGroupTypeGridOt">
                    <param name="wmode" value="opaque">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>

            <div id="cmdBtnList1" class="btn_right_top2">
                <em class="btn_blu"><a href="javaScript:selectAll();" id="btnSearch"><fmt:message key="aimir.selectall"/></a></em>
                <em class="btn_blu"><a href="javaScript:deselectAll();" id="btnSearch"><fmt:message key="aimir.deselectall"/></a></em>
                <em class="btn_blu"><a href="javaScript:clear();" id="btnSearch"><fmt:message key="aimir.button.delete"/></a></em>
                <em class="btn_blu"><a href="javaScript:clearAll();" id="btnSearch"><fmt:message key="aimir.button.initialize"/></a></em>
                <em class="btn_org"><a href="javaScript:supplyCapacityA('GetStatus');" id="btnSearch"><fmt:message key="aimir.supplystatus"/></a></em>
                <em class="btn_org"><a href="javaScript:supplyCapacityA('Deactivation');" id="btnSearch"><fmt:message key="aimir.supply.cutOff"/></a></em>
                <em class="btn_org"><a href="javaScript:supplyCapacityA('Activation');" id="btnSearch"><fmt:message key="aimir.supply.cutOn"/></a></em>
            </div>
            <div class="gadget_body2">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="254px" id="emergencyElecBlockGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/emergencyElecBlockGrid.swf" />
                    <param name="wmode" value="opaque">
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/emergencyElecBlockGrid.swf" width="100%" height="254px" id="emergencyElecBlockGridOt">
                    <param name="wmode" value="opaque">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>

            <div class="capacity-textarea gadget_body margin-t10px">
               <ul><li><textarea id="aTextarea" readonly></textarea></li></ul>
            </div>


        </div>
        <!-- 1ST 탭 : 비상전기차단 (E) -->




        <!-- 2ND 탭 : 전기공급 차단/복구 (S) -->
        <div id="elecSupplyCapacity">

            <!-- 2ND 탭의 내용 : 검색조건 (S) -->
            <div class="search-bg-withouttabs">

                <div class="searchoption-container">
                    <table class="searchoption wfree">
                        <tr>
                            <td>
                                <select id="bSwitchStatus" name="select" style="width:120px;" onchange="javaScript:search();">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                    <option value="Activation"><fmt:message key="aimir.target.Off"/></option>
                                    <option value="Deactivation"><fmt:message key="aimir.target.On"/></option>
                                </select>
                            </td>
                            <td>
                                <select style="width:120px;" id="bGroupType">
                                <option value=""><fmt:message key="aimir.targetType"/></option>
                                <c:forEach var="groupType" items="${groupTypes}">
                                    <option value="${groupType}">${groupType}</option>
                                </c:forEach>
                                </select>
                            </td>
                            <td><input type="text" id="bTarget"/></td>
                            <td>
                                <select style="width:150px;" id="bCondition">
                                    <option value=""><fmt:message key="aimir.reason.cutOnOff"/></option>
                                    <option value="Prepayment"><fmt:message key="aimir.prepayment"/></option>
                                    <option value="ExceedsThreshold"><fmt:message key="aimir.exceed.supplyThreshold"/></option>
                                </select>
                            </td>
                            <td>
                                <select style="width:120px;" id="bMeterStatus">
                                <option value=""><fmt:message key="aimir.supplystatus"/></option>
                                <c:forEach var="meterStatus" items="${meterStatusMap}">
                                    <option value="${meterStatus.key}">${meterStatus.value}</option>
                                </c:forEach>
                                </select>
                            </td>
                            <td>
                                <div id="btn">
                                    <ul><li><a href="javaScript:bSearch();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>

            </div>
            <!-- 2ND 탭의 내용 : 검색조건 (E) -->


            <div id="cmdBtnList2" class="btn_right_top2 margin-t10px">
                <em class="btn_org"><a href="javaScript:supplyCapacityB('GetStatus');" id="btnSearch"><fmt:message key="aimir.supplystatus"/></a></em>
                <em class="btn_org"><a href="javaScript:supplyCapacityB('Deactivation');" id="btnSearch"><fmt:message key="aimir.supply.cutOff"/></a></em>
                <em class="btn_org"><a href="javaScript:supplyCapacityB('Activation');" id="btnSearch"><fmt:message key="aimir.supply.cutOn"/></a></em>
                <em class="btn_blu"><a href="javaScript:supplyCapacityExcel();" class="on"><fmt:message key="aimir.button.excel"/></a></em>
            </div>
            <div class="gadget_body2">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="475px" id="elecSupplyCapacityGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/elecSupplyCapacityGrid.swf" />
                    <param name="wmode" value="opaque">
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/elecSupplyCapacityGrid.swf" width="100%" height="475px" id="elecSupplyCapacityGridOt">
                    <param name="wmode" value="opaque">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>

            <div class="capacity-textarea gadget_body margin-t10px">
               <ul><li><textarea id="bTextarea" readonly></textarea></li></ul>
            </div>

        </div>
        <!-- 2ND 탭 : 전기공급 차단/복구 (E) -->




        <!-- 3RD 탭 : 차단해제이력 (S) -->
        <div id="supplyCppacityLog">

            <!-- 3RD 탭의 내용 : 검색조건 (S) -->
            <div class="search-bg-withouttabs with-dayoptions-bt">

                <div class="dayoptions-bt">
                   <%@ include file="/gadget/commonDateTabButtonType.jsp" %>
                </div>
                <div class="dashedline"><ul><li></li></ul></div>

                <div class="searchoption-container">
                    <table class="searchoption wfree">
                        <tr>
                            <td>
                                <select id="cStatus" name="select" style="width:120px;" onchange="javaScript:search();">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                    <option value="Activation"><fmt:message key="aimir.target.Off"/></option>
                                    <option value="Deactivation"><fmt:message key="aimir.target.On"/></option>
                                </select>
                            </td>
                            <td>
                                <select style="width:120px;" id="cGroupType">
                                <option value=""><fmt:message key="aimir.targetType"/></option>
                                <c:forEach var="groupType" items="${groupTypes}">
                                    <option value="${groupType}">${groupType}</option>
                                </c:forEach>
                                </select>
                            </td>
                            <td><input type="text" id="cTarget"/></td>
                            <td>
                                <div id="btn">
                                    <ul><li><a href="javaScript:cSearch();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>

            </div>
            <!-- 3RD 탭의 내용 : 검색조건 (E) -->


            <div class="gadget_body">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="200px" id="circuitBreakerLogChartEx">
                    <param name="movie" value="${ctx}/flexapp/swf/circuitBreakerLogChart.swf" />
                    <param name="wmode" value="opaque">
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/circuitBreakerLogChart.swf" width="100%" height="200px" id="circuitBreakerLogChartOt">
                    <param name="wmode" value="opaque">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>

            <div id="btn" class="btn_right_top2">
                <ul><li><a href="javaScript:logExcel();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
            </div>
            <div class="gadget_body2">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="220px" id="circuitBreakerLogGridEx">
                    <param name="movie" value="${ctx}/flexapp/swf/circuitBreakerLogGrid.swf" />
                    <param name="wmode" value="opaque">
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/circuitBreakerLogGrid.swf" width="100%" height="220px" id="circuitBreakerLogGridOt">
                    <param name="wmode" value="opaque">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>


        </div>
        <!-- 3RD 탭 : 차단해제이력 (E) -->




        <!-- 4TH 탭 : 차단해제설정 (S) -->
        <div id="supplyCapacitySetup">

            <!-- 4TH 탭의 내용(1) (S) -->
            <div class="headspace_2ndline2"><label class="check"><fmt:message key="aimir.repaidThresholding"/></label></div>
            <div class="box-bluegradation2">
                <form id="prepaymentForm">
                <input type="hidden" name="condition" value="Prepayment" />
                <table>
                <tr><td class="percentage"><input type="text" name="blockingThreshold"/></td>
                    <td width="90px" class="gray11pt withinput">% <fmt:message key="aimir.less"/></td>
                    <td><span><input type="radio" class="trans withinput" name="automaticDeactivation" value="true" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.automatic.cutOff"/> class="border-trans bg-trans" readonly style="width:70px;" /></span>
                        <span><input type="radio" class="trans withinput" name="automaticDeactivation" value="false" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.manual.cutOff"/> class="border-trans bg-trans" readonly style="width:70px;" /></span></td>
                </tr>
                <tr><td class="percentage"><input type="text" name="alarmThreshold"/></td>
                    <td class="gray11pt withinput">% <fmt:message key="aimir.less"/></td>
                    <td class="gray11pt">
                        <span><input type="checkbox" class="trans withinput" name="alarm" value="true"></span>
                        <span><input type="text" value="<fmt:message key="aimir.alarm"/>" class="border-trans bg-trans" readonly style="width:70px;"></span></td>
                </tr>
                <tr><td class="gray11pt withinput" colspan="2"><fmt:message key="aimir.complePrepay"/></td>
                    <td class="gray11pt">
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="true"/></span>
                        <span><input type="text" value=<fmt:message key="aimir.automatic.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span>
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="false"/></span>
                        <span><input type="text" value=<fmt:message key="aimir.manual.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span></td>
                </tr>
                </table>
                </form>
            </div>
            <div id="savePrepaymentBtn">
                <div id="btn" class="btn_right_bottom">
                    <ul><li><a href="javaScript:savePrepayment()" class="on" id="btnSearch"><fmt:message key="aimir.save2"/></a></li></ul>
                </div>
            </div>
            <!-- 4TH 탭의 내용(1) (E) -->

            <!-- 4TH 탭의 내용(2) (S) -->
            <div class="headspace_2ndline2 clear"><label class="check"><fmt:message key="aimir.supply.setting.threshold"/></label></div>
            <div class="box-bluegradation2">
                <form id="exceedsThresholdForm">
                <input type="hidden" name="condition" value="ExceedsThreshold" />
                <table>
                <tr><td class="percentage"><input type="text" name="blockingThreshold"/></td>
                    <td width="90px" class="gray11pt withinput">% <fmt:message key="aimr.morethan"/></td>
                    <td class="gray11pt">
                        <span><input type="radio" class="trans withinput" name="automaticDeactivation" value="true" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.automatic.cutOff"/> class="border-trans bg-trans" readonly style="width:70px;" /></span>
                        <span><input type="radio" class="trans withinput" name="automaticDeactivation" value="false"/></span>
                        <span><input type="text" value=<fmt:message key="aimir.manual.cutOff"/> class="border-trans bg-trans" readonly style="width:70px;" /></span></td>
                </tr>
                <tr><td class="percentage"><input type="text" name="alarmThreshold"/></td>
                    <td class="gray11pt withinput">% <fmt:message key="aimr.morethan"/></td>
                    <td class="gray11pt">
                        <span><input type="checkbox" class="trans withinput" name="alarm" value="true"></span>
                        <span><input type="text" value="<fmt:message key="aimir.alarm"/>" class="border-trans bg-trans" readonly style="width:70px;" ></span></td>
                </tr>
                <tr><td class="percentage"><input type="text" name="recoveryTime"/></td>
                    <td width="90px">
                        <select style="width:60px;" name="timeUnit" id="timeUnit1">
                            <option value="1"><fmt:message key="aimir.sec"/></option>
                        </select>
                    </td>
                    <td class="gray11pt">
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="true" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.automatic.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span>
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="false" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.manual.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span></td>
                </tr>
                </table>
                </form>
            </div>
            <div id="saveExceedsThresholdBtn">
                <div id="btn" class="btn_right_bottom">
                    <ul><li><a href="javaScript:saveExceedsThreshold()" class="on" id="btnSearch"><fmt:message key="aimir.save2"/></a></li></ul>
                </div>
            </div>
            <!-- 4TH 탭의 내용(2) (E) -->


            <!-- 4TH 탭의 내용(3) (S) -->
            <div class="headspace_2ndline2 clear"><label class="check"><fmt:message key="aimir.emergency.setting.cutOnOff"/></label></div>
            <div class="box-bluegradation2">
                <form id="emergencyForm">
                <input type="hidden" name="condition" value="Emergency" />
                <table>
                <tr><td class="percentage"><input type="text" name="recoveryTime"/></td>
                    <td width="90px">
                        <select style="width:60px;" name="timeUnit" id="timeUnit2">
                            <option value="1"><fmt:message key="aimir.sec"/></option>
                        </select>
                    </td>
                    <td class="gray11pt">
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="true" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.automatic.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span>
                        <span><input type="radio" class="trans withinput" name="automaticActivation" value="false" /></span>
                        <span><input type="text" value=<fmt:message key="aimir.manual.cutOn"/> class="border-trans bg-trans" readonly style="width:70px;" /></span></td>
                </tr>
                </table>
                </form>
            </div>
            <div id="saveEmergencyBtn">
                <div id="btn" class="btn_right_bottom">
                    <ul><li><a href="javaScript:saveEmergency()" class="on" id="btnSearch"><fmt:message key="aimir.save2"/></a></li></ul>
                </div>
            </div>
            <!-- 4TH 탭의 내용(2) (E) -->


        </div>
        <!-- 4TH 탭 : 차단해제설정 (E) -->





    </div>
    <!-- 탭 전체 (E) -->

</body>
</html>