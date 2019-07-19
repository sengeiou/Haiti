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

        $(document).ready(function(){
            var obj = window.opener.obj;

            emergePre();

            console.log("errorList2:", obj.errorList);

            $.post('${ctx}/gadget/prepaymentMgmt/getErrorListExportExcel.do',
                    {errorList : obj.errorList,
                     headerMsg : getExportExcelHeader(),
                     filePath : $("#filePath").val(),
                     title : '<fmt:message key="aimir.ebs.exporttitle" />'
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
                        var result = json.result;
                        if (result == "success") {

                        }
                    }
            );
        });

        function fileDown(fname) {
            $("#fileName").val($("#"+fname).val());
            var downform = document.getElementsByName("reportDownloadForm")[0];
            downform.submit();
        }

        // export excel header
        function getExportExcelHeader() {
            var headerMsg = new Array();
            var cnt = 0;

            headerMsg[cnt++] = '<fmt:message key="aimir.customerid"/>';
            headerMsg[cnt++] = '<fmt:message key="aimir.customername"/>';
            headerMsg[cnt++] = '<fmt:message key="aimir.contractNumber"/>';
            headerMsg[cnt++] = '<fmt:message key="aimir.cpno"/>';
            headerMsg[cnt++] = '<fmt:message key="aimir.sap.errorReason"/>';

            return headerMsg;
        }

        function addTableRows(fileNames) {
            var tbl = $("table");

            var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';

            for (var i = 0; i < fileNames.length; i++) {
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