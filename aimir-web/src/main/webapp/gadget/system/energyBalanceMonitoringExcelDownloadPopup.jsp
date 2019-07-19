<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <title><fmt:message key="aimir.report.fileDownload"/></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var flex;
        var supplierId = "";

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );

        $(document).ready(function(){
            var obj = window.opener.obj;

            emergePre();
            //$.ajaxSetup({ async: true });
            /*$.post('${ctx}/gadget/mvm/mvmMaxGadgetExcelMake.do'
                    , { 'customer_number'   : obj.customer_number,
                        }
                    , function(json) {
                        hide();
                        if (json.total == 0) {
                            $("#datalist").hide();
                            $("#nodata").show();
                            return;
                        }

                        $("#filePath").val(json.filePath);
                        $("#fileName").val(json.fileName);
                        $("#zipFileName").val(json.zipFileName);

                        $("#zipFile").text(json.zipFileName);
                        addTableRows(json.fileNames);
                    }
            );*/

            $.getJSON('${ctx}/gadget/system/getEbsExportExcel.do',
                    {supplierId : obj.supplierId,
                     searchStartDate : obj.searchStartDate,
                     searchEndDate : obj.searchEndDate,
                     searchDateType : obj.searchDateType,
                     suspected : obj.suspected,
                     locationId : obj.locationId,
                     dtsName : obj.dtsName,
                     threshold : obj.threshold,
                     headerMsg : getExportExcelHeader(),
                     filePath : $("#filePath").val(),
                     week : obj.week,
                     weekMsg : '<fmt:message key="aimir.week" />',
                     title : '<fmt:message key="aimir.ebs.exporttitle" />'
                    },
                    function(json) {
                        hide();
                        if (json.total == 0) {
                            $("#datalist").hide();
                            $("#nodata").show();
                            return;
                        }

                        //$("#filePath").val(json.filePath);
                        $("#fileName").val(json.fileName);
                        $("#zipFileName").val(json.zipFileName);

                        $("#zipFile").text(json.zipFileName);
                        addTableRows(json.fileNames);

                        hide();
                        var result = json.result;
                        if (result == "success") {

                        }
                    }
            );



            //$.ajaxSetup({ async: false });
        });

        function fileDown(fname) {
            $("#fileName").val($("#"+fname).val());
            //var url = "${ctx}/common/fileDownload.do";
            var downform = document.getElementsByName("reportDownloadForm")[0];
            //downform.action = url;
            downform.submit();
        }

        function getFmtMessage(){
            var fmtMessage = new Array();
            var cnt = 0;

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[3] = "<fmt:message key="aimir.meteringtime"/>";
            fmtMessage[4] = "<fmt:message key="aimir.usage"/>";
            fmtMessage[5] = "<fmt:message key="aimir.previous"/>";
            fmtMessage[6] = "<fmt:message key="aimir.co2formula"/>";
            fmtMessage[7] = "<fmt:message key="aimir.mcuid2"/>";
            fmtMessage[8] = "<fmt:message key="aimir.meterid2"/>";
            fmtMessage[9] = "<fmt:message key="aimir.location"/>";
            fmtMessage[10] = "<fmt:message key="aimir.view.detail"/>";
            fmtMessage[11] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[12] = "<fmt:message key="aimir.contract.selectContract"/>";
            fmtMessage[13] = "<fmt:message key="aimir.firmware.msg09"/>";
            fmtMessage[14] = "<fmt:message key="aimir.alert.metering.choiceContract4"/>";   // 선택한 계약이 4개를 초과했습니다.
            fmtMessage[15] = "<fmt:message key="aimir.report.fileDownloadDir"/>";        // File Path

            return fmtMessage;
        }

        // export excel header
        function getExportExcelHeader() {
            var headerMsg = new Array();
            var cnt = 0;

            headerMsg[cnt++] = '<fmt:message key="aimir.number"/>';                         // 번호
            headerMsg[cnt++] = '<fmt:message key="aimir.location"/>';                       // 지역
            headerMsg[cnt++] = '<fmt:message key="aimir.ebs.substationname"/>';             // Substation ID
            headerMsg[cnt++] = '<fmt:message key="aimir.loss"/> [%]';                  // threshold
            headerMsg[cnt++] = '<fmt:message key="aimir.ebs.impenergy"/>';                  // Delivered Energy(kWh)
            headerMsg[cnt++] = '<fmt:message key="aimir.ebs.tolenergy"/>';                  // Tolerance Delivered Energy(kWh)
            headerMsg[cnt++] = '<fmt:message key="aimir.ebs.consumeenergy"/>';              // Consumed Energy(kWh)

            return headerMsg;
        }

        function getParams() {

            var condArray = new Array();
            condArray[0] = periodType;
            condArray[1] = $('#searchEndDate').val();
            condArray[2] = $('#seasonalSeasonCombo').val();
            condArray[3] = supplierId;
            condArray[4] = $('#searchEndDate').val().substr(0,4);

            return condArray;
        }

        function addTableRows(fileNames) {
            var tbl = $("table");

            var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';
            for(var i = 0 ; i < fileNames.length ; i++) {
                //var tbRow = table.insertRow();
                htmltxt += "<tr><th>"+(i+1)+"</th><td>"+fileNames[i]+"</td><td class='button'>" +
                           "<input type='hidden' name='fileName"+i+"' id='fileName"+i+"' value='"+fileNames[i]+"'/>" +
                           "<button type='button' class='input_button' style='cursor:hand;' onclick='javascript:fileDown(\"fileName"+i+"\");'>" +
                           '<fmt:message key="aimir.report.fileDownload"/>' + "</button></td></tr>";
            }
            tbl.html(htmltxt);
        }

        function winClose() {
            window.close();
        }
    /*]]>*/
    </script>
    <base target="_self" ></base>
</head>
<body>
<form name="reportDownloadForm" id="reportDownloadForm" action="${ctx}/common/fileDownload.do" method="post" target="downFrame" style="display:none;">
<input type="hidden" id="filePath" name="filePath" value="<fmt:message key="aimir.report.fileDownloadDir"/>"/>
<input type="hidden" id="fileName" name="fileName" />
<input type="hidden" id="zipFileName" name="zipFileName" />
</form>
<iframe name="downFrame" style="display:none;"></iframe>
<div id="wrapper">
    <div class="popup_title"><span class="icon_download"></span><fmt:message key="aimir.report.fileDownload"/></div>

    <div id="datalist" class="overflow_hidden"  style="display:block">
        <div class="download_zip">
            <ul>
            <!-- <li class="file">billingday20110418(1).zip</li> -->
            <li class="file" id="zipFile"></li>
            <li class="downbtn">
                <em class="am_button">
                    <div class="divbutton" onclick="javascript:fileDown('zipFileName');">
                        <fmt:message key="aimir.report.fileDownload"/>
                    </div>
                </em>
            </li>
            </ul>
        </div>

        <div class="downloadbox">
            <table class="download" id="filelist">

            </table>
        </div>

        <div class="overflow_hidden textalign-center">
            <em class="am_button">
                <div class="divbutton" onclick="winClose();" style="cursor:hand;">
                    <fmt:message key="aimir.board.close"/>
                </div>
            </em>
        </div>
    </div>
    <div id="nodata" class="nodata"  style="display:none">
        <ul>
            <li class="text"><fmt:message key="aimir.data.notexist"/></li>
            <li class="close"><em class="am_button">
                <!-- <div class="divbutton" onclick="javascript:window.close();" > -->
                <div class="divbutton" onclick="winClose();" style="cursor:hand;">
                    <fmt:message key="aimir.board.close"/>
                </div>
            </em></li>
        </ul>
    </div>

</div>

</body>
</html>