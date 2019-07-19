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
        var gridHeader = null;
        $(document).ready(function(){
            var obj = window.opener.obj;
            gridHeader = obj.headerMsg;
            emergePre();

            $.getJSON('${ctx}/gadget/device/getMcuOperationLogExportExcel.do',
                    {supplierId : obj.supplierId,
                     targetName : obj.targetName,
                     startDate : obj.startDate,
                     endDate : obj.endDate,
                     headerMsg : getExportExcelHeader(),
                     widths : getExportExcelWidth(),
                     aligns : getExportExcelAlign(),
                     fields : getExportExcelField(),
                     filePath : $("#filePath").val(),
                     title : 'MCU Operation Log'
                    },
                    function(json) {
                        hide();
                        if (json.total == 0) {
                            $("#datalist").hide();
                            $("#nodata").show();
                            return;
                        }

                        $("#fileName").val(json.fileName);
                        $("#zipFileName").val(json.zipFileName);

                        $("#zipFile").text(json.zipFileName);
                        addTableRows(json.fileNames);

                        hide();
                    }
            );
        });

        function fileDown(fname) {
            $("#fileName").val($("#"+fname).val());
            //var url = "${ctx}/common/fileDownload.do";
            var downform = document.getElementsByName("reportDownloadForm")[0];
            //downform.action = url;
            downform.submit();
        }

        // export excel header
        function getExportExcelHeader() {
            var headerMsg = new Array();

            headerMsg.push(gridHeader[0]);
            headerMsg.push(gridHeader[1]);
            headerMsg.push(gridHeader[2]);
            headerMsg.push(gridHeader[3]);
            headerMsg.push(gridHeader[4]);
            headerMsg.push(gridHeader[5]);
            headerMsg.push(gridHeader[6]);
            return headerMsg;
        }

        // excel column width
        function getExportExcelWidth() {
            var widthArray = new Array();
            // 
            widthArray.push(256 * 7);
            widthArray.push(256 * 18);
            widthArray.push(256 * 14);
            widthArray.push(256 * 11);
            widthArray.push(256 * 13);
            widthArray.push(256 * 18);
            widthArray.push(256 * 18);

            return widthArray;
        }

        // excel column align
        function getExportExcelAlign() {
            var alignArray = new Array();
            // c:center, l:left, r:rigth, '':left
            alignArray.push("c");
            alignArray.push("");
            alignArray.push("");
            alignArray.push("");
            alignArray.push("");
            alignArray.push("");
            alignArray.push("");

            return alignArray;
        }

        // excel column data field
        function getExportExcelField() {
            var fieldArray = new Array();
            fieldArray.push("rowNo");
            fieldArray.push("yyyymmddhhmmss");
            fieldArray.push("targetTypeCode");
            fieldArray.push("operatorType");
            fieldArray.push("userId");
            fieldArray.push("targetName");
            fieldArray.push("description");

            return fieldArray;
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