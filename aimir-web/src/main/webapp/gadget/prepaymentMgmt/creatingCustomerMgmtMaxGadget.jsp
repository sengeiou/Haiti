<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .excel {
            background-image:url(${ctx}/themes/images/customer/icon_excel.png) !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //공급사ID
        var supplierId = "${supplierId}";
        // 수정권한
        var editAuth = "${editAuth}";

        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.onReady(function() {
            Ext.QuickTips.init();
            var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

            if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
                Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
                Ext.override(Ext.grid.ColumnModel, {
                    getTotalWidth : function(includeHidden) {
                        if (!this.totalWidth) {
                            var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                            this.totalWidth = 0;
                            for (var i = 0, len = this.config.length; i < len; i++) {
                                if (includeHidden || !this.isHidden(i)) {
                                    this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                                }
                            }
                        }
                        return this.totalWidth;
                    }
                });
                chromeColAdd = 2;
            }
        });

        var errorListExl;   // 엑셀리포트 생성 시 사용할 데이터

        $(document).ready(function() {

            // 수정권한 체크
            if (editAuth == "true") {
                $("#btnList").show();
            } else {
                $("#btnList").hide();
            }

            new AjaxUpload('saveBulk', {
                action: '${ctx}/gadget/prepaymentMgmt/saveBulkCreatingCustomer.do',
                data : {},
                responseType : 'json',
                onSubmit : function(file , ext){
                    // Allow only images. You should add security check on the server-side.
                    if (ext && /^(xls|xlsx)$/.test(ext)){
                        /* Setting data */
                        this.setData({
                            'key': 'This string will be send with the file',
                            supplierId : supplierId
                        });

                    } else {
                        return false;
                    }
                },
                onComplete : function(file, response){
                    resetGrid();
                    $('#filename').val(file);

                    if (response != null) {
                        if (response.status == "success") {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.newcustomeradded"/>");
                        } else if (response.status == "failure") {
                            excelDisabled = false;
                            errorListExl = response.errorList;
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>",
                                    function() {renderErrorListGrid(response.errorList);});
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.save.error"/>");
                        }
                    }
                }
            });

            $("#registTabList").tabs();
            
            // Bulk Save Tab 버튼 클릭 이벤트 생성
            $(function() { $('#bulkRegist').bind('click',function(event) {
                renderErrorListGrid(new Array());
            } ); });

            $('#telephoneNo1').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });
            $('#telephoneNo2').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });
            $('#telephoneNo3').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });

            $('#mobileNo1').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });
            $('#mobileNo2').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });
            $('#mobileNo3').bind("keydown", function(event) { inputOnlyNumberType(event, $(this)); });

            $('#customerNo').bind("change", function(event) {
                $("#customerNocheckValue").hide();
                $("#customerNoCheckYn").val("false");
            });
            $('#contractNumber').bind("change", function(event) {
                $("#contractNumbercheckValue").hide();
                $("#contractNumberCheckYn").val("false");
            });

            $('#mobileNo1').bind("change", function(event) {
                $("#certificationCheckYn").val("false");
            });
            $('#mobileNo2').bind("change", function(event) {
                $("#certificationCheckYn").val("false");
            });
            $('#mobileNo3').bind("change", function(event) {
                $("#certificationCheckYn").val("false");
            });

            hide();
        });

        function registConfirm() {
            var msg = Ext.Msg.show({
                title:'<fmt:message key="aimir.message"/>',
                msg: '<fmt:message key="aimir.wouldSave"/>',
                buttons: Ext.Msg.YESNO,
                fn: function(btn) {
                    if (btn == "yes") {
                        regist();
                    }
                }
            });
        }

        function regist() {
            if (!validation()) {
                return;
            }

            var telephoneNo = $("#telephoneNo1").val() + "-" + $("#telephoneNo2").val() + "-" + $("#telephoneNo3").val();
            var mobileNo = $("#mobileNo1").val() + "-" + $("#mobileNo2").val() + "-" + $("#mobileNo3").val();
            var email = "";

            if ($("#email1").val() != "") {
                email = $("#email1").val() + "@" + $("#email2").val();
            }
            var params = {
                customerNo : $("#customerNo").val(),
                customerName : $("#customerName").val(),
                contractNumber : $("#contractNumber").val(),
                telephoneNo : telephoneNo,
                mobileNo : mobileNo,
                email : email,
                barcode : $("#barcode").val(),
                supplierId : supplierId
            };

            emergePre();
            $.post("${ctx}/gadget/prepaymentMgmt/saveCreatingCustomer.do"
                    ,params
                    ,function(json) {
                        if (json != null && json.result == "success") {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.newcustomeradded"/>",
                                    function() {
                                        resetForm();
                                    });
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.save.error"/>");
                        }

                        hide();
                        return;
                    });
        }

        // 필수입력 체크
        function validation() {
            if ( $("#customerNoCheckYn").val() != "true" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.chkdupcustomerno'/>");
                $("#customerNo").focus();
                return false;
            }

            if ($("#customerName").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.insertName'/>");
                $("#customerName").focus();
                return false;
            }

            if ( $("#contractNumberCheckYn").val() != "true" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.chkdupcontractnumber'/>");
                $("#contractNumber").focus();
                return false;
            }

            if ( $("#contractNumberCheckYn").val() != "true" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateId'/>");
                $("#customerNo").focus();
                //$("#customerNo").val('');
                return false;
            }

            if ($("#mobileNo1").val() == "" || $("#mobileNo2").val() == "" || $("#mobileNo3").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputmobilenumber'/>");  // 이동번호를 입력 해 주세요
                $("#mobileNo1").focus();
                return false;
            }

            // 휴대폰번호
            if ( $("#certificationCheckYn").val() != "true" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.mobilecertification'/>");
                $("#mobileNo1").focus();
                return false;
            }

            var email1 = $("#email1").val();
            var email2 = $("#email2").val();
            // email에 값이 존재할때, emial의 유효성 체크를 실시한다.
            if (email1 != "" || email2 != "") {
                var email = email1 + "@" + email2;
                
                var reg = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;

                if (!reg.test(email)) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.invalidemailaddr'/>");  // 유효하지 않은 이메일 주소입니다.
                    $("#email1").focus();
                    return false;
                }
            }

            return true;
        }

        // clear form
        function resetForm() {
            $("#customerNo").val("");
            $("#customerNoCheckYn").val("");
            $("#customerName").val("");
            $("#contractNumber").val("");
            $("#contractNumberCheckYn").val("");
            $("#telephoneNo1").val("");
            $("#telephoneNo2").val("");
            $("#telephoneNo3").val("");
            $("#mobileNo1").val("");
            $("#mobileNo2").val("");
            $("#mobileNo3").val("");
            $("#certificationCheckYn").val("");
            $("#email1").val("");
            $("#email2").val("");
            $("#barcode").val("");

            $("#customerNocheckValue").html("");
            $("#customerNocheckValue").hide();
            $("#contractNumbercheckValue").html("");
            $("#contractNumbercheckValue").hide();
        }

        // 고객 번호 체크
        function customerNoDupCheck() {
            var customerNo = $("#customerNo").val();
            // 공백문자 체크
            var fmt1 = /\s/;
            if (customerNo.length > 0 && fmt1.exec(customerNo)) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.notspace"/>');
                $("#customerNo").focus();
                return;
            }

            if ( customerNo == "" || customerNo.length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.inputcustomerno"/>');
                $("#customerNo").focus();
                return;
            }
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=overlapcheck', { customerNo:customerNo },
                function(json) {
                    if ( json.count == 0 ) {
                        $("#customerNocheckValue").html("<ul><li class='available'><fmt:message key="aimir.dup.available"/></li></ul>");
                        $("#customerNocheckValue").show();
                    } else {
                        $("#customerNocheckValue").html("<ul><li class='reject'><fmt:message key="aimir.dup.inuse"/></li></ul>");
                        $("#customerNocheckValue").show();
                        $("#customerNo").select();
                        $("#customerNo").focus();
                    }
                    $("#customerNoCheckYn").val(json.checkYN);
                }
            );
        }

        //계약 번호 체크
        function contractNumberDupCheck() {
            var contractNumber = $("#contractNumber").val();
            // 공백문자 체크
            var fmt1 = /\s/;
            if (contractNumber.length > 0 && fmt1.exec(contractNumber)) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.notspace"/>');
                $("#contractNumber").focus();
                return;
            }

            if ( contractNumber == "" || contractNumber.length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.enterContractNo"/>");    // 계약번호를 입력해 주세요
                $("#contractNumber").focus();
                return;
            }
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCheckContractNumber', { contractNumber:contractNumber },
                function(json) {
                    var result = json.result;
                    var checkYN = "false";

                    if (result != null) {
                        if (result.exist == "true") {
                            checkYN = "false";
                            $("#contractNumbercheckValue").html("<ul><li class='reject2'><fmt:message key='aimir.dup.inuse'/></li></ul>");
                            $("#contractNumbercheckValue").show();
                            $("#contractNumber").select();
                            $("#contractNumber").focus();
                        } else {
                            checkYN = "true";
                            $("#contractNumbercheckValue").html("<ul><li class='available2'><fmt:message key='aimir.dup.available'/></li></ul>");
                            $("#contractNumbercheckValue").show();
                        }
                    } else {
                        checkYN = "false";
                        $("#contractNumbercheckValue").html("<ul><li class='reject2'><fmt:message key='aimir.erroroccured'/></li></ul>");
                        $("#contractNumbercheckValue").show();
                        $("#contractNumber").val('');
                        $("#contractNumber").focus();
                    }
                    $("#contractNumberCheckYn").val(checkYN);
                }
            );
        }

        /* Error 리스트 START */
        var errorGridOn = false;
        var errorGrid;
        var errorStore;
        var errorColModel;
        function renderErrorListGrid(errorList) {
            var width = $("#gridDiv").width();
            var pageSize = 20;

            errorStore = new Ext.ux.data.PagingArrayStore({
                lastOptions: {params: {start: 0, limit: pageSize}},
                data : errorList,//arrayGrid,
                fields: ["cell0", "cell1", "cell2", "cell3", "errMsg"]
            });
            
            var colWidth = width/5 - chromeColAdd;

            errorColModel = new Ext.grid.ColumnModel({
                columns : [
                    {header: "<fmt:message key="aimir.customerid"/>", dataIndex: 'cell0'}
                   ,{header: "<fmt:message key="aimir.customername"/>", dataIndex: 'cell1'}
                   ,{header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'cell2'}
                   ,{header: "<fmt:message key="aimir.cpno"/>", dataIndex: 'cell3'}
                   ,{header: "<fmt:message key="aimir.sap.errorReason"/>", dataIndex: 'errMsg', width: colWidth - 4, renderer: addTooltip}
                ],
                defaults : {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
                }
            });

            // ExtJS 그리드 생성
            if (errorGridOn == false) {
                errorGrid = new Ext.grid.GridPanel({
                    width: width,
                    height: 520,
                    store: errorStore,
                    colModel : errorColModel,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'gridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:['->'
                          ,'-'
                          ,{
                              text: "<fmt:message key="aimir.button.excel"/>",
                              scope: this,
                              iconCls:'excel',
                              handler: function() {
                                  openExcelReport();
                              }
                          }
                    ],
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: errorStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                errorGridOn = true;
            } else {
                errorGrid.setWidth(width);
                var bottomToolbar = errorGrid.getBottomToolbar();
                errorGrid.reconfigure(errorStore, errorColModel);
                bottomToolbar.bindStore(errorStore);
            }
        }//Fuction End

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        // Clear Error List
        function resetGrid() {
            errorListExl = [];
            errorStore.removeAll();
        }

        // 입력한 key 가 number인지 체크
        function inputOnlyNumberType(ev, src) {
            if (isNaN(src.val())) {
                src.val("");
                return;
            }
            var evCode = (window.netscape) ? ev.which : event.keyCode;

            // Allow: backspace(8), delete(46), tab(9), escape(27), ←(37), →(39), enter(13) and dot(190)
            if (evCode == 8 || evCode == 9 || evCode == 13 || evCode == 27
                    || evCode == 37 || evCode == 39 || evCode == 46) {
                // let it happen, don't do anything
            } else if (evCode == 190) {
                if (src.val() == "" || src.val().indexOf(".") != -1) {
                    ev.preventDefault();
                }
            } else {
                // Ensure that it is a number and stop the keypress
                if (evCode < 48 || evCode > 57) {
                    ev.preventDefault();
                }
            }
        }

        // 휴대폰 번호 인증
        function sendCertificationSMS() {
            var mobileNo1 = $("#mobileNo1").val();
            var mobileNo2 = $("#mobileNo2").val();
            var mobileNo3 = $("#mobileNo3").val();

            if (mobileNo1 == "" || mobileNo2 == "" || mobileNo3 == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputmobilenumber'/>");  // 이동번호를 입력 해 주세요
                $("#mobileNo1").focus();
                return false;
            }

            $.post('${ctx}/gadget/prepaymentMgmt/sendCertificationSMS.do',
                {mobileNo : mobileNo1 + "-" + mobileNo2 + "-" + mobileNo3},
                function(json) {
                    var result = json.result;
                    var checkYN = "false";

                    if (result != null && result == "true") {
                        checkYN = "true";
                        //alert("<fmt:message key='aimir.inputmobilenumber'/>");
                    } else {
                        checkYN = "false";
                        //alert("<fmt:message key='aimir.inputmobilenumber'/>");
                    }
                    $("#certificationCheckYn").val(checkYN);
                }
            );
        }

        // open excel download popup
        var win;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var errorList = new Array();

            if (errorListExl != null && errorListExl.length > 0) {
                var len = errorListExl.length;
                for (var i = 0; i < len; i++) {
                    errorList.push(errorListExl[i].join("|"));
                }

                console.log("errorList1:", errorList);
                var obj = new Object();
                obj.errorList = errorList;
                if(win)
                    win.close();
                win = window.open("${ctx}/gadget/prepaymentMgmt/creatingCustomerExcelDownloadPopup.do", "CreatingCustomerExcel", opts);
                win.opener.obj = obj;
            }
        }

    /*]]>*/
    </script>
</head>
<body>

    <!-- 개통정보 Tab (S) -->
    <div id="registTabList">
        <ul>
            <li><a href="#singleRegistTab" id="singleRegist"><fmt:message key="aimir.device.singleRegistration"/></a></li>
            <li><a href="#bulkRegistTab" id="bulkRegist"><fmt:message key="aimir.device.batchRegistration"/></a></li>
        </ul>

        <!-- 개별 등록탭 -->
        <div id="singleRegistTab">
            <div style="float: right; right: 0; padding-right: 20px"><fmt:message key="aimir.hems.inform.requiredField"/></div>
            <div class="blueline" style="clear: both; height: 230px;">
                <ul class="width">
                    <li class="padding">

                        <!-- <table class="search" style="width: 100% !important; height: 100px !important;"> -->
                        <!-- <table style="width: 100% !important; height: 100px !important;"> -->
                        <table class="searchoption wfree">
                            <tr>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.customerid"/><font color="red">*</font>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="customerNo"/><input type="hidden" id="customerNoCheckYn"/>
                                    <div id="btn" style="width: 230px;"><ul><li><a onclick="javascript:customerNoDupCheck();" class="on"><fmt:message key="aimir.checkDuplication2" /></a></li></ul></div>
                                    <div id="customerNocheckValue" class="check-overlap check-overlap-customer" style="width:210px;"></div>
                                </td>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.customername"/><font color="red">*</font>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="customerName"/>
                                </td>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.contractNumber"/><font color="red">*</font>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="contractNumber"/><input type="hidden" id="contractNumberCheckYn"/>
                                    <div id="btn" style="width: 230px;"><ul><li><a onclick="javascript:contractNumberDupCheck();" class="on"><fmt:message key="aimir.checkDuplication2" /></a></li></ul></div>
                                    <div id="contractNumbercheckValue" class="check-overlap check-overlap-customer" style="width:210px;"></div>
                                </td>
                            </tr>
                            <tr>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.tel.no"/>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="telephoneNo1" style="width:40px;"/><input type="text" value="-" class="between" readonly="readonly" tabindex="-1"/>
                                    <input type="text" id="telephoneNo2" style="width:50px;"/><input type="text" value="-" class="between" readonly="readonly" tabindex="-1"/>
                                    <input type="text" id="telephoneNo3" style="width:50px;"/>
                                </td>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.cpno"/><font color="red">*</font>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="mobileNo1" style="width:40px;"/><input type="text" value="-" class="between" readonly="readonly" tabindex="-1"/>
                                    <input type="text" id="mobileNo2" style="width:50px;"/><input type="text" value="-" class="between" readonly="readonly" tabindex="-1"/>
                                    <input type="text" id="mobileNo3" style="width:50px;"/><input type="hidden" id="certificationCheckYn"/>
                                    <div id="btn" style="width: 280px;"><ul><li><a onclick="javascript:sendCertificationSMS();" class="on"><fmt:message key="aimir.certification"/></a></li></ul></div>
                                </td>
                                <th class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <fmt:message key="aimir.email"/>
                                </th>
                                <td class="padding-r10px" style="padding-bottom: 7px !important;">
                                    <input type="text" id="email1" style="width:80px;"/>
                                    <input type="text" value="@" class="between" readonly="readonly"/>
                                    <input type="text" id="email2" style="width:120px;"/>
                                </td>
                            </tr>
                            <tr>
                                <th class="padding-r10px"><fmt:message key="aimir.barcode"/></th>
                                <td class="padding-r10px" colspan="5"><input type="text" id="barcode"/></td>
                            </tr>
                        </table>
                    </li>
                </ul>
                <!-- <ul class="width"> -->
                <ul style="padding: 0px 20px 20px 20px;">
                    <li class="padding">
                        <div class="btn-confirm" >
                            <em class="am_button"><a onClick="javascript:registConfirm();"><fmt:message key="aimir.button.register"/></a></em>&nbsp;
                            <em class="am_button"><a onClick="javascript:resetForm();"><fmt:message key="aimir.cancel"/></a></em>
                        </div>
                    </li>
                </ul>
            </div>
        </div>


        <!-- 벌크 등록탭 -->
        <div id="bulkRegistTab" style="padding: 0px;">
            <div class="search-bg-withouttabs">
                <div class="searchoption-container">
                    <table class="searchoption wfree">
                        <tr>
                            <td class="blue12pt padding-r10px ">
                                <fmt:message key="aimir.template"/>
                                <fmt:message key="aimir.download"/>
                            </td>
                            <td colspan="2" id="templateSelectDiv" class="margin-t1px">
                                <span class="am_button margin-t1px">
                                    <a id="btnDown" href='${ctx}/template/BulkCreatingCustomerTemplate.xls'><fmt:message key="aimir.download"/></a>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td class="blue12pt padding-r10px" align="right"><fmt:message key="aimir.device.batchRegistration"/></td>
                            <td id="detailSelectDiv" class="margin-t1px">
                                <span><input type="text" id="filename" name="filename" style="width:250px"/></span>
                                <span><input type="hidden" id="filepath" style="width:200px"/></span>
                            </td>
                            <td>
                                <span class="am_button margin-r5"><a href="#" id="saveBulk"><fmt:message key="aimir.button.register"/></a></span>
                                <%-- <span class="am_button margin-r5"><a href="#" id="excel"><fmt:message key="aimir.button.excel"/></a></span> --%>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <!--Flexlist 등록결과-->
            <div  style="margin: 10px 0px 10px 3px;">
                <label class="check"><fmt:message key="aimir.errorlist"/><!-- Error List --></label>
            </div>
            <div id='gridDiv'></div>
        </div>

    </div>
    <!-- 제조사추가 Tab (E) -->

</body>
</html>