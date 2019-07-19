<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title>Report Management Mini Gadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />

    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .expandAll {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam//icon_expandAll.png) !important;
        }
        .collapseAll {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam//icon_collapseAll.png) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }
        .save {
            background-image:url(../../themes/images/default/setting/ico_file.gif) !important;
        }
        .task-folder {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/folder_go.png) !important;
        }
        .task {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/user.png) !important;
        }
        .task-meter {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam//application_go.png) !important;
        }
        .task-phase {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam//application_go.png) !important;
        }
        .task-substation {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam//icon_server.png) !important;
        }
        .excel {
            background-image:url(${ctx}/themes/images/customer/icon_excel.png) !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>

    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        //탭초기화
        var tabs = {hourly:0,monthlyPeriod:0,weekDaily:0,yearly:0,seasonal:0};
        var tabNames = {};

        var supplierId = ${supplierId};
        var guageChartDataXml;
        // 수정권한
        var editAuth = "${editAuth}";

        $(document).ready(function(){
            Ext.QuickTips.init();

            if (editAuth  == "true") {
                $("#addDtsBtnList").show();
            } else {
                $("#addDtsBtnList").hide();
            }

            $('#tabDiv').tabs();
            var locDateFormat = "yymmdd";
            $("#installStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#installEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

            // 날짜조건에 있는 search 버튼을 감춘다.
            if(tabClickExec){$('#dailySearch').parent().css('display','none');};
            if(tabClickExec){$('#periodSearch').parent().css('display','none');};
            if(tabClickExec){$('#weeklySearch').parent().css('display','none');};
            if(tabClickExec){$('#monthlySearch').parent().css('display','none');};

            // New DTS 버튼 클릭 이벤트 생성
            $(function() { $('#newDtsPage').bind('click',function(event) { changeSearchAddDiv('add'); } ); });
            // Search Page 버튼 클릭 이벤트 생성
            $(function() { $('#searchPage').bind('click',function(event) { changeSearchAddDiv('search'); } ); });

            // Tab 버튼 클릭 이벤트 생성
            $(function() { $('#dtsTab').bind('click',function(event) {
                changeSpaceDiv("empty");
                getEbsDtsList();
            } ); });
            $(function() { $('#meterTab').bind('click',function(event) {
                changeSpaceDiv("add");
                changeAddButton("meter");
                getEbsMeterList();
            } ); });
            $(function() { $('#contractTab').bind('click',function(event) {
                changeSpaceDiv("add");
                changeAddButton("contract");
                getEbsContractMeterList();
            } ); });

            // DTS 등록 버튼 클릭 이벤트 생성
            $(function() { $('#btnAddDts').bind('click',function(event) { insertEbsDtsValidationCheck(); } ); });
            // Tree 조회버튼 클릭 이벤트 생성
            $(function() { $('#btnTreeSearch').bind('click',function(event) { getEbsDtsTree(); } ); });
            // DTS 조회버튼 클릭 이벤트 생성
            $(function() { $('#btnDtsSearch').bind('click',function(event) { getEbsDtsList(); } ); });
            // Meter 조회버튼 클릭 이벤트 생성
            $(function() { $('#btnMeterSearch').bind('click',function(event) { getEbsMeterList(); } ); });
            // Contract 조회버튼 클릭 이벤트 생성
            $(function() { $('#btnContSearch').bind('click',function(event) { getEbsContractMeterList(); } ); });
            // DTS 중복체크버튼 클릭 이벤트 생성
            $(function() { $('#btnDupCheck').bind('click',function(event) { getEbsDtsDupCheck(); } ); });

            // DTS 중복체크버튼 클릭 이벤트 생성
            $(function() { $('#addDtsName').bind('change',function(event) { $("#dupCheckDtsName").val(""); } ); });

            // DTS 에 Meter 등록 버튼 클릭 이벤트 생성
            $(function() { $('#btnAddDtsMeterNode').bind('click',function(event) { addEbsMeterNode(); } ); });

            // DTS 에 Meter 등록 버튼 클릭 이벤트 생성
            $(function() { $('#btnAddDtsContractNode').bind('click',function(event) { addEbsContractMeterNode(); } ); });

            // 조회조건필드 임계치의 입력값 체크(float만 입력 가능)
            $('#threshold').bind("keyup", function(event){ inputNumericOnly(event, $(this)); });
            // 입력필드 임계치의 입력값 체크(float만 입력 가능)
            $('#addThreshold').bind("keyup", function(event){ inputNumericOnly(event, $(this)); });
            // 입력필드 임계치의 입력값 체크(float만 입력 가능)
            $('#dtsThreshold').bind("keyup", function(event){ inputNumericOnly(event, $(this)); });

            locationTreeGoGo('mainTreeDiv', 'searchWord', 'locationId');
            locationTreeGoGo('dtsTreeDiv', 'dtsSearchWord', 'dtsLocationId');
            locationTreeGoGo('meterTreeDiv', 'meterSearchWord', 'meterLocationId');
            locationTreeGoGo('contTreeDiv', 'contSearchWord', 'contLocationId');
            locationTreeGoGo('addTreeDiv', 'addSearchWord', 'addLocationId');

            getMeterGroupBygroupId();           // Meter Group Combo
            getDeviceVendorsBySupplierId();     // Vendor Combo
            getContractGroup();                 // Contract Group Combo
            $("#deviceModel").selectbox();
            getDefaultThreshold();              // Default Threshold

            getEbsDtsTree();
            getEbsDtsList();

            // Chart Window 생성
            makeChartWindow();
        });

        // 입력한 key 가 number인지 체크
        function inputOnlyNumberType(ev, src) {
            // Allow: backspace(8), delete(46), tab(9), escape(27), ←(37), →(39), enter(13) and dot(190)
            if (ev.keyCode == 8 || ev.keyCode == 9 || ev.keyCode == 13 || ev.keyCode == 27
                    || ev.keyCode == 37 || ev.keyCode == 39 || ev.keyCode == 46) {
                // let it happen, don't do anything
            } else if (ev.keyCode == 190) {
                if (src.val() == "" || src.val().indexOf(".") != -1) {
                    ev.preventDefault();
                }
            } else {
                // Ensure that it is a number and stop the keypress
                if (ev.keyCode < 48 || ev.keyCode > 57) {
                    ev.preventDefault();
                }
            }
        }

        // 숫자 이외 문자 제거:소수점 허용
        function removeCharForReal2(src) {
            var val = src.val();
            var num = val.replace(/[^\d\.]/g, '');
            return src.val(num);
        }

        /**
         * 숫자만 입력. 정수
         */
        // 숫자 이외 문자 모두 제거
        function inputNumericOnly(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            // 방향키
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.val();
            val = removeCharForReal(val);
            val = removeFstZeroForReal(val);
            src.val(val);
            //src.focus();
        }

        // 숫자 이외 문자 제거:소수점 허용
        function removeCharForReal(val) {
            //var num = val.replace(/[\D]/g, '');
            var num = val.replace(/[^\d\.]/g, '');

            if (num.indexOf('.', num.indexOf('.')+1) != -1) {
                idx = num.indexOf('.');
                len = num.length;
                num = num.substring(0, idx+1) + num.substring(idx+1, len).replace(/\./g, '');
            }

            return num;
        }

        // 앞에 0 제거:소수점포함
        function removeFstZeroForReal(val) {
            //var pattern = /(^0*)(\d+$)/g;
            var pattern = /(^0*)([\d\.]+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        // 검색조건의 날짜를 Local유형에서 일반 유형으로 변경
        function modifyDateLocal(setDate, inst){
            var dateId       = '#' + inst.id;
            var dateHiddenId = '#' + inst.id + 'Hidden';

            $(dateHiddenId).val($(dateId).val());

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                    });
        }

        // 미터 그룹 콤보박스
        function getMeterGroupBygroupId() {
            $.getJSON('${ctx}/gadget/system/getMeterGroupBygroupId.do'
                    , {'supplierId' : supplierId,
            			'groupType' : 'Meter'}
                    , function(json) {
                          $('#meterGroup').loadSelect(json.NAME);
                          $('#meterGroup').selectbox();
                      });
        };

        // 계약 그룹 콤보박스
        function getContractGroup() {
            $.getJSON('${ctx}/gadget/system/getContractGroup.do'
                    , {'supplierId' : supplierId}
                    , function(json) {
                          $('#contractGroup').loadSelect(json.NAME);
                          $('#contractGroup').selectbox();
                      });
        };

        // 벤더 콤보박스
        function getDeviceVendorsBySupplierId() {
            $.getJSON('${ctx}/gadget/system/vendorlist.do'
                    , {'supplierId' : supplierId}
                    , function(json) {
                          $('#deviceVendor').loadSelect(json.deviceVendors);
                          $('#deviceVendor').selectbox();
                      });
        };

        // 모델 콤보박스
        function getDeviceModelsByVendorId() {
            if( $('#deviceVendor').val() != "") {
                $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do'
                        , {'vendorId' : $('#deviceVendor').val() }
                        , function (json){
                            $('#deviceModel').noneSelect(json.deviceModels);
                            $('#deviceModel').selectbox();
                        });
            }
        };

        var defaultThreshold;
        // Substation의 Default임계치를 설정파일에서 취득한다.
        function getDefaultThreshold() {
             $.getJSON('${ctx}/gadget/system/getDefaultThreshold.do'
                     ,''
                     , function(json) {
                         defaultThreshold = json.defaultThreshold;
                       });
        }

        var treeData;
        // DTS Tree Data 조회
        function getEbsDtsTree() {
            emergePre();
            var getData = function() {
                $.getJSON('${ctx}/gadget/system/getEbsDtsTreeData.do'
                    , {
                        supplierId : supplierId,
                        searchStartDate : $("#searchStartDate").val(),
                        searchEndDate : $("#searchEndDate").val(),
                        searchBasicDate : $("#searchBasicDate").val(),
                        suspected : $("#suspected").is(":checked") ? "true" : "",
                        locationId : $("#locationId").val(),
                        dtsName : $("#treeDtsName").val(),
                        threshold : $("#threshold").val()
                    }
                    , function (json){
                        treeData = json.result
                        hide();
                        makeEbsDtsTree();
                    });
            }
            var startDate = $("#searchStartDate").val();
            var endDate = $("#searchEndDate").val();
            var basicDate = $("#searchBasicDate").val();
            if ( startDate && endDate && basicDate) {
                getData();
            } else { 
                getEbsDtsTree.defer(300);
            }

        };

        // window resize event
        $(window).resize(function() {
            makeEbsDtsTree();
            if ( $('#dtsGridDiv').is(':visible')) {
                getEbsDtsList();
            }
            if ( $('#meterGridDiv').is(':visible')) {
                getEbsMeterList();
            }
            if ( $('#contractGridDiv').is(':visible')) {
                getEbsContractMeterList();
            }
        });

        /* DTS Tree 생성 */
        var dtsTreeGridOn = false;
        var dtsTreeGrid;
        var dtsTreeRootNode;
        //var dtsTreeColModel;
        function makeEbsDtsTree() {
            var width = $("#treeGridDiv").width();
            var headerImport = "<fmt:message key="aimir.ebs.currentmonthimport"/> [kWh]<br/>(<fmt:message key="aimir.ebs.toleranceDeliveredEnergy"/>)";
            var headerConsume = "<fmt:message key="aimir.ebs.currentmonthconsume"/> <br/>[kWh]";

            var dtsTreeColModel = [
                   {header: '&nbsp;<br/>&nbsp;', dataIndex: 'nodeName', width: (width / 3 ) + 50 
                        ,tpl: new Ext.XTemplate('{nodeName:this.viewToolTip}', {
                            viewToolTip: addTreeTooltip
                        })
                   }
                   ,{header: "<font style='font-weight: bold;'>" + headerImport + "</font>", dataIndex: 'impEnergy', width: (width / 3) - 50, align: "right"
                       ,tpl: new Ext.XTemplate('{impEnergy:this.chgColor}', {
                           chgColor: function(v, values) {
                               if (v != null && values.suspectedYn == "true") {
                                   return "<font style='color:#FF0000; margin-right:5px;'>" + v + "</font>";
                               } else {
                                   return "<span style='margin-right:5px;'>" + v + "</span>";
                               }
                           }
                       })
                   }
                   ,{header: "<font style='font-weight: bold;'>" + headerConsume + "</font>", dataIndex: 'consumeEnergy', width: (width  / 3) - 42, align: "right"
                       ,tpl: new Ext.XTemplate('{consumeEnergy:this.chgColor}', {
                           chgColor: function(v, values) {
                               if (v != null && values.suspectedYn == "true") {
                                   return "<font style='color:#FF0000; margin-right:5px;'>" + v + "</font>";
                               } else {
                                   return "<span style='margin-right:5px;'>" + v + "</span>";
                               }
                           }
                       })
                   }
                   ,{header: '&nbsp;<br/>&nbsp;', dataIndex: 'id', width: 37, enableSort: false                      
                           ,tpl: new Ext.XTemplate('{id:this.chartBtn}', {
                               chartBtn: function(v) {
                                   var idArr = v.split("_");
                                   var len = idArr.length;
                                   var nodeNamePath = "";                                   

                                   if (len == 2) {    // DTS
                                	   var dtsNode = dtsTreeGrid.getNodeById(v);
                                	   var locNode = dtsNode.parentNode;
                                       nodeNamePath = locNode.attributes.nodeName + "/ " + dtsNode.attributes.nodeName;
                                   } else if (len == 3) {    // Meter
                                       var meterNode = dtsTreeGrid.getNodeById(v);
                                       var dtsNode = meterNode.parentNode;
                                       var locNode = dtsNode.parentNode;
                                       nodeNamePath = locNode.attributes.nodeName + "/ " + dtsNode.attributes.nodeName + "/ " + meterNode.attributes.nodeName;
                                   } else if (len == 4) {    // Phase
                                       var phaseNode = dtsTreeGrid.getNodeById(v);
                                       var meterNode = phaseNode.parentNode;
                                       var dtsNode = meterNode.parentNode;
                                       var locNode = dtsNode.parentNode;
                                       nodeNamePath = locNode.attributes.nodeName + "/ " + dtsNode.attributes.nodeName + "/ " + meterNode.attributes.nodeName + "/ " + phaseNode.attributes.nodeName;
                                   } else if (len == 5) {    // Contract Meter
                                       var contMeterNode = dtsTreeGrid.getNodeById(v);
                                       var phaseNode = contMeterNode.parentNode;
                                       var meterNode = phaseNode.parentNode;
                                       var dtsNode = meterNode.parentNode;
                                       var locNode = dtsNode.parentNode;
                                       nodeNamePath = locNode.attributes.nodeName + "/ " + dtsNode.attributes.nodeName + "/ " + meterNode.attributes.nodeName + "/ " + phaseNode.attributes.nodeName + "/ " + contMeterNode.attributes.nodeName;
                                   }
                                   
                                   if (len == 2 || len == 3 || len == 4) {    // DTS, Meter, Phase
                                       return "<a href='#;' onclick='viewImportChartWindow(\"" + v + "\", \"" + nodeNamePath + "\");'><span class='icon_graph'>&nbsp;</span></a>";
                                   } else if (len == 5){      // Contract
                                       return "<a href='#;' onclick='viewConsumeChartWindow(\"" + v + "\", \"" + nodeNamePath + "\");'><span class='icon_graph'>&nbsp;</span></a>";
                                   } else {
                                       return "";
                                   }

                               }
                           })
                    }
                ];

            dtsTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'root',
                draggable:false,
                expended:true,
                children: treeData
            });


            if (!dtsTreeGridOn) {
                var treeLoader = new Ext.tree.TreeLoader({
                    url:'${ctx}/gadget/system/getEbsDtsTreeContractMeterNodeData.do',
                    baseParams: {
                      supplierId : supplierId,
                      searchStartDate : $("#searchStartDate").val(),
                      searchEndDate : $("#searchEndDate").val(),
                      searchBasicDate : $("#searchBasicDate").val(),
                      suspectedYn : false
                    }
                });

                treeLoader.on("beforeload", function(treeLoader, node) {
                    treeLoader.baseParams.searchStartDate = $("#searchStartDate").val();
                    treeLoader.baseParams.searchEndDate = $("#searchEndDate").val();
                    treeLoader.baseParams.suspectedYn = node.parentNode.attributes.suspectedYn;
                })

                var bbarDisabled = false;
                if (editAuth != "true") {
                    bbarDisabled = true;
                }

                dtsTreeGrid = new Ext.ux.tree.TreeGrid({
                    width: width,
                    height: 517,
                    renderTo: "treeGridDiv",
                    enableDD: false,
                    enableHdMenu : false,
                    enableSort : false,
                    columns: dtsTreeColModel,
                    loader: treeLoader,
                    //dataUrl:'treegrid-data2.json'
                    root: dtsTreeRootNode,
                    rootVisible: false,
                    tbar:[{text : "Expand All",
                           iconCls:'expandAll',
                           handler: function() {
                              dtsTreeGrid.expandAll();
                        }
                    },'-'
                    ,{
                        text : "Collapse All",
                        iconCls:'collapseAll',
                        handler: function() {
                           dtsTreeGrid.collapseAll();
                        }
                    },'-'
                    ,'->'
                    ,'-'
                    ,{
                        text: "<fmt:message key="aimir.button.excel"/>",
                        scope: this,
                        iconCls:'excel',
                        handler: function() {
                            openExcelReport();
                        }
                    },
                    ],
                    bbar : [{
                     text: "<fmt:message key="aimir.button.delete"/>",
                     scope: this,
                     // iconCls:'remove',
                     disabled: bbarDisabled,
                     handler: function() {
                         deleteEbsDtsTreeNode();
                     }
                 },'-',
                 ]
                });

                dtsTreeGrid.on("click", selectEbsDtsTreeNode);
                dtsTreeGridOn = true;
            } else {
                dtsTreeGrid.setWidth(width);
                dtsTreeGrid.setRootNode(dtsTreeRootNode);
                dtsTreeGrid.render();
            }

            if (updatedNodePath != null) {
                dtsTreeGrid.selectPath(updatedNodePath);
                updatedNodePath = null;
            } else {
                selectedNodeId = null;
                selectedNodeCode = null;
                selectedNodePath = null;
                selectedParentNodeCode = null;
            }
        }

        // treegrid column tooltip
        function addTreeTooltip(value, values) {
            if (value != null && value != "" && values != null && values.leaf != null && values.leaf == true) {
                var text = "<fmt:message key="aimir.meterid"/> : " + values.mdsId;

                if (values.contractNumber != null && values.contractNumber != "") {
                    text += "<br/><fmt:message key="aimir.contractNumber"/> : " + values.contractNumber;
                }
                return '<span qtip="' + text + '">' + value + '</span>';
            } else {
                return value;
            }
        }

        var selectedNodeId;
        var selectedNodeCode;
        var selectedNodePath;
        var selectedParentNodeCode;
        var selectedParentNodeId;

        // DTS Tree 클릭 시 선택한 Node 의 정보를 setting
        function selectEbsDtsTreeNode(node, e) {
            selectedNodeId = node.id;
            selectedNodeCode = node.attributes.nodeCode;
            selectedParentNodeId = node.parentNode.id;
            selectedParentNodeCode = node.parentNode.attributes.nodeCode;
            selectedNodePath = node.getPath();
        }

        // Search 화면/DTS 등록 화면 change
        function changeSearchAddDiv(val) {
            if (val == 'add') {
                $("#searchPageUl").show();
                $("#rightAddDiv").show();
                $("#newDtsPageUl").hide();
                $("#rightSearchDiv").hide();
                $("#addThreshold").val(defaultThreshold);
                $("#dspThreshold").text(defaultThreshold + "%");
                
            } else {
                $("#newDtsPageUl").show();
                $("#rightSearchDiv").show();
                $("#searchPageUl").hide();
                $("#rightAddDiv").hide();
            }
        }

        // Add Node 버튼 show/hide
        function changeSpaceDiv(val) {
            if (editAuth == "true")  {
                if (val == 'empty') {
                    $("#addBtnSpace").hide();
                    $("#searchDiv").css("width", "100%");
                } else {
                    $("#addBtnSpace").show();
                    $("#searchDiv").css("width", "96%");
                }
            }
        }

        // Meter Node/Contract Node Add 버튼 change
        function changeAddButton(val) {
            if (editAuth == "true") {
                if (val == 'meter') {
                    $("#btnAddDtsMeterNode").show();
                    $("#btnAddDtsContractNode").hide();
                } else {
                    $("#btnAddDtsMeterNode").hide();
                    $("#btnAddDtsContractNode").show();
                }
            }
        }

        /* DTS 리스트 START */
        var dtsGridOn = false;
        var dtsStore;
        var dtsGrid;
        var dtsColModel;

        var getEbsDtsList = function() {
            var width = $("#dtsGridDiv").width();
            var pageSize = 20;

            emergePre();
            dtsStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/system/getEbsDtsList.do",
                baseParams: {
                    supplierId : supplierId,
                    dtsName : $("#dtsName").val(),
                    locationId : $("#dtsLocationId").val(),
                    threshold : $("#dtsThreshold").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["dtsId", "dtsName", "threshold", "locationId", "location", "address", "description"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            var editable = true;
            if (editAuth != "true") {
                editable = false;
            }

            dtsColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key='aimir.ebs.dtsname'/>", dataIndex: 'dtsName', width: width/5}
                   ,{header: "<fmt:message key='aimir.loss'/> [%]<em class='icon_star'></em>", dataIndex: 'threshold',
                        width: width/5, align: "right", editable: editable,
                        editor: new Ext.form.NumberField({
                            id: 'updThreshold',
                            allowBlank: false
                        })
                    }
                   ,{header: "<fmt:message key='aimir.location'/>", dataIndex: 'location', width: width/5, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.address'/><em class='icon_star'></em>", dataIndex: 'address',
                        width: width/5, renderer: addTooltip, editable: editable,
                        editor: new Ext.form.TextField({
                            id: 'updAddress',
                            allowBlank: true
                        })
                    }
                   ,{header: "<fmt:message key='aimir.description'/><em class='icon_star'></em>", dataIndex: 'description',
                       width: (width/5) - 7, renderer: addTooltip, editable: editable,
                       editor: new Ext.form.TextField({
                           id: 'updDescription',
                           allowBlank: true
                       })
                   }
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if (dtsGridOn == false) {
                var tbarDisabled = false;
                if (editAuth != "true") {
                    tbarDisabled = true;
                }

                dtsGrid = new Ext.grid.EditorGridPanel({
                    store: dtsStore,
                    colModel : dtsColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll: false,
                    width: width,
                    height: 517,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    clicksToEdit: 1,
                    renderTo: 'dtsGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:[{
                        text: "<fmt:message key="aimir.save2"/>",
                        scope: this,
                        //iconCls:'save',
                        disabled: tbarDisabled,
                        handler: function() {
                           var modifyRecords = dtsStore.getModifiedRecords(); //수정된 레코드 모두 찾기
                           var modifyLen = modifyRecords.length;

                           if (modifyLen > 0) {
                               Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>',
                                       function(btn) {
                                           if (btn == 'yes') {
                                               updateEbsDtsList(modifyRecords);
                                           }
                                       });
                           }
                        }
                    },'-'
                    ],
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: dtsStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });

                dtsGridOn = true;
            } else {
                dtsGrid.setWidth(width);
                var bottomToolbar = dtsGrid.getBottomToolbar();
                dtsGrid.reconfigure(dtsStore, dtsColModel);
                bottomToolbar.bindStore(dtsStore);
            }
            hide();
        };

        /* Meter 리스트 START */
        var meterGridOn = false;
        var meterGrid;
        var meterColModel;
        var meterCheckSelModel;
        var getEbsMeterList = function() {
            var width = $("#meterGridDiv").width();
            var pageSize = 20;

            var meterStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/system/getEbsMeterList.do",
                baseParams: {
                    supplierId : supplierId,
                    mdsId : $("#mdsId").val(),
                    locationId : $("#meterLocationId").val(),
                    meterGroup : $("#meterGroup").val(),
                    installStartDate : $("#installStartDateHidden").val(),
                    installEndDate : $("#installEndDateHidden").val(),
                    deviceVendor : $("#deviceVendor").val(),
                    deviceModel : $("#deviceModel").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["meterId", "mdsId", "location", "model", "installDate", "meterStatus"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            if(meterGridOn == false) {
                meterCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                    ,dataIndex: 'meterId'
                });
            }

            meterColModel = new Ext.grid.ColumnModel({
                columns: [
                    meterCheckSelModel
                   ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId', width: (width-20)/5}
                   ,{header: "<fmt:message key='aimir.location'/>", dataIndex: 'location', width: (width-20)/5, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.model'/>", dataIndex: 'model', width: (width-20)/5}
                   ,{header: "<fmt:message key='aimir.installationdate'/>", dataIndex: 'installDate', width: (width-20)*1.4/5}
                   ,{header: "<fmt:message key='aimir.status'/>", dataIndex: 'meterStatus', width: ((width-20) * 0.6/5)-6, renderer: addTooltip}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(meterGridOn == false) {
                meterGrid = new Ext.grid.GridPanel({
                    store: meterStore,
                    colModel : meterColModel,
                    sm: meterCheckSelModel,
                    autoScroll:false,
                    width: width,
                    height: 517,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'meterGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: meterStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                meterGridOn = true;
            } else {
                meterGrid.setWidth(width);
                var bottomToolbar = meterGrid.getBottomToolbar();
                meterGrid.reconfigure(meterStore, meterColModel);
                bottomToolbar.bindStore(meterStore);
            }
            hide();
        };

        /* Contract Meter 리스트 START */
        var contGridOn = false;
        var contGrid;
        var contColModel;
        var contCheckSelModel;
        var getEbsContractMeterList = function() {
            var width = $("#contractGridDiv").width();
            var pageSize = 20;

            var contStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/system/getEbsContractMeterList.do",
                baseParams: {
                    supplierId : supplierId,
                    customerId : $("#customerId").val(),
                    customerName : $("#customerName").val(),
                    contractNumber : $("#contractNumber").val(),
                    contractGroup : $("#contractGroup").val(),
                    locationId : $("#contLocationId").val(),
                    mdsId : $("#contMdsId").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contMeterId", "mdsId", "customerNo", "customerName", "contractNumber", "location", "tariffType"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            if(contGridOn == false) {
                contCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'contMeterId'
                });
            }

            contColModel = new Ext.grid.ColumnModel({
                columns: [
                    contCheckSelModel
                   ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId', width: (width-20)/6, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName', width: (width-20)/6 + 15, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo', width: (width-20)/6, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber', width: (width-20)/6, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.location'/>", dataIndex: 'location', width: (width-20)/6 - 15, renderer: addTooltip}
                   ,{header: "<fmt:message key='aimir.contract.tariff.type'/>", dataIndex: 'tariffType', width: ((width-20)/6) - 6, renderer: addTooltip}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
               }
            });

            if(contGridOn == false) {
                contGrid = new Ext.grid.GridPanel({
                    store: contStore,
                    colModel : contColModel,
                    sm: contCheckSelModel,
                    autoScroll:false,
                    width: width,
                    height: 517,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'contractGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: contStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                contGridOn = true;
            } else {
                contGrid.setWidth(width);
                var bottomToolbar = contGrid.getBottomToolbar();
                contGrid.reconfigure(contStore, contColModel);
                bottomToolbar.bindStore(contStore);
            }
            hide();
        };

        // DTS Tree 에서 선택한 Node 를 삭제한다.
        function deleteEbsDtsTreeNode() {
            if (selectedNodeId == null || selectedNodeId == "") {
                // 선택한 Node 가 없을 경우
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.noselectednode"/>");
                return;
            } else {
                // 선택한 Node 가 삭제 가능한 Node 인지 체크
                if (selectedNodeId.indexOf("_") == -1) {
                    Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.selectavailnode"/>");
                    return;
                }

                Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.child.deleteall"/><br><fmt:message key="aimir.msg.wantdelete"/>',
                        function(btn) {
                            if (btn == 'yes') {
                                var params = {"supplierId" : supplierId,
                                              "deleteNodeId" : selectedNodeId
                                             };

                                $.post("${ctx}/gadget/system/deleteEbsDtsTreeNode.do",
                                       params,
                                       function(json) {
                                           if (json.result == "success") {
                                               Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.hems.information.successDelete"/>",
                                                       function() {
                                                           var parentNode = dtsTreeGrid.getNodeById(selectedParentNodeId);   // parentNode
                                                           updatedNodePath = parentNode.getPath();

                                                           // Grid 재조회
                                                           if ( $('#dtsGridDiv').is(':visible')) {
                                                               getEbsDtsList();
                                                           }
                                                           if ( $('#meterGridDiv').is(':visible')) {
                                                               getEbsMeterList();
                                                           }
                                                           if ( $('#contractGridDiv').is(':visible')) {
                                                               getEbsContractMeterList();
                                                           }

                                                           getEbsDtsTree();        // tree 재조회
                                                       });
                                           } else {
                                               Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>", function() {});
                                           }

                                           return;
                                       }
                                );
                            }
                        });
            }
        }

        var updatedNodePath;    // 하위 Node 를 추가한 Node 의 path. tree 재조회 후 선택 할때 사용.
        // DTS 에 Meter 를 추가한다.
        function addEbsMeterNode() {
            if (selectedNodeId == null || selectedNodeId == "") {
                // 선택한 Node 가 없을 경우
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.noselectednode"/>");
                return;
            } else {
                var re = /^(\d+)_(\d+)$/;

                // 선택한 Node 가 DTS Node 인지 체크
                if (selectedNodeId.search(re) == -1) {
                    Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.selectdtsnode"/>");
                    return;
                }

                var checkedArr = meterCheckSelModel.getSelections();
                var len = checkedArr.length;

                if (len > 0) {
                    Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wouldAdd"/>',
                            function(btn) {
                                if (btn == 'yes') {
                                    var paramArr = new Array();
                                    for (var i = 0 ; i < len ; i++) {
                                        paramArr.push(checkedArr[i].get("meterId"));
                                    }

                                    var params = {supplierId : supplierId,
                                            "dtsId" : selectedNodeCode,
                                            "meterIds" : paramArr,
                                            };

                                    $.post("${ctx}/gadget/system/addEbsMeterNode.do",
                                           params,
                                           function(json) {
                                               if (json.result == "success") {
                                                   Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.msg.add'/>",
                                                           function() {
                                                               meterCheckSelModel.clearSelections();   // checkbox clear
                                                               updatedNodePath = selectedNodePath;
                                                               getEbsMeterList();
                                                               getEbsDtsTree();        // tree 재조회
                                                           });
                                               } else {
                                                   Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.msg.add.error'/>", function() {});
                                               }

                                               return;
                                           }
                                    );
                                }
                            });
                } else {
                    // 체크한 미터가 없을 경우
                    Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.select.row.no"/>", function() {});
                }
            }
        }

        // DTS Meter 에 Contract Meter 를 추가한다.
        function addEbsContractMeterNode() {

            if (selectedNodeId == null || selectedNodeId == "") {
                // 선택한 Node 가 없을 경우
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.noselectednode"/>");
                return;
            } else {
                var re = /^(\d+)_(\d+)_(\d+)_(\d+)$/;

                // 선택한 Node 가 phase Node 인지 체크
                if (selectedNodeId.search(re) == -1) {
                    Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.ebs.msg.validation.selectphasenode"/>");
                    return;
                }

                var checkedArr = contCheckSelModel.getSelections();
                var len = checkedArr.length;

                if (len > 0) {
                    Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wouldAdd"/>',
                            function(btn) {
                                if (btn == 'yes') {
                                    var paramArr = new Array();
                                    for (var i = 0 ; i < len ; i++) {
                                        paramArr.push(checkedArr[i].get("contMeterId"));
                                    }

                                    var params = {supplierId : supplierId,
                                            "meterId" : selectedParentNodeCode,
                                            "phaseId" : selectedNodeCode,
                                            "contMeterIds" : paramArr,
                                            };

                                    $.post("${ctx}/gadget/system/addEbsContractMeterNode.do",
                                           params,
                                           function(json) {
                                               if (json.result == "success") {
                                                   Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.msg.add'/>",
                                                           function() {
                                                               contCheckSelModel.clearSelections();   // checkbox clear
                                                               updatedNodePath = selectedNodePath;
                                                               getEbsContractMeterList();
                                                               getEbsDtsTree();        // tree 재조회
                                                           });
                                               } else {
                                                   Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.msg.add.error'/>", function() {});
                                               }

                                               return;
                                           }
                                    );
                                }
                            });
                } else {
                    // 체크한 contract 가 없을 경우
                    Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.select.row.no"/>", function() {});
                }
            }
        }

        // DTS Insert 입력값 체크
        function insertEbsDtsValidationCheck() {
            if ($("#addDtsName").val().length <= 0) {     // DTS Name
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.dtsname"/>');
            } else if ($("#dupCheckDtsName").val() != "true") {     // DTS Name 중복체크
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.dupcheckdtsname"/>');
            } else if ($("#addThreshold").val().length <= 0) {     // 임계치
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.loss"/>');
            } else if ($("#addLocationId").val().length <= 0) {     // 지역
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.location"/>');
            } else if($("#addThreshold").val().length != 0 && ($("#addThreshold").val()).match(/[^0-9.]+/)){ // 임계치(%)의 숫자 체크
                Ext.MessageBox.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.ebs.Loss.number'/>");
            }else {

                Ext.Msg.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>',
                        function(btn) {
                            if (btn == "yes") {
                                insertDts();
                            }
                        });
            }
        }

        // DTS insert
        function insertDts() {
            var params = {"supplierId" : supplierId,
                          "dtsName" : $("#addDtsName").val(),
                          "threshold" : $("#addThreshold").val(),
                          "locationId" : $("#addLocationId").val(),
                          "address" : $("#addAddress").val(),
                          "description" : $("#addDescription").val()};

            $.post("${ctx}/gadget/system/insertEbsDts.do",
                   params,
                   function(json) {
                       if (json.result == "success") {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>',
                           function() {
                               // 입력항목 clear
                               $("#addDtsName").val("");
                               $("#addThreshold").val("");
                               $("#addLocationId").val("");
                               $("#addSearchWord").val("");
                               $("#addAddress").val("");
                               $("#addDescription").val("");

                               // Threshold폼에 Default값 설정
                               $("#addThreshold").val(defaultThreshold);

                               getEbsDtsTree();
                           });
                       } else {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                       }

                       return;
                   }
            );
        }

        // DTS Name Dup Check
        function getEbsDtsDupCheck() {
            // substaion name의 필수 체크를 먼저 실시 한다.
            if ($("#addDtsName").val().length <= 0) {     // DTS Name
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.dtsname"/>');
                return;
            }

            $.getJSON('${ctx}/gadget/system/getEbsDtsNameDup.do'
                    , {'supplierId' : supplierId,
                       'dtsName' : $("#addDtsName").val()}
                    , function(json) {
                          if (json.result == "false") {
                              $("#dupCheckDtsName").val("true");
                              Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.availdtsname"/>');
                          } else {
                              //$("#addDtsName").val("");
                              $("#dupCheckDtsName").val("");
                              Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.dupdtsname"/>');
                          }
                      });
        }

        // DTS List Update
        function updateEbsDtsList(record) {
            var recordLen = record.length;
            var dtsIds = new Array();
            var dtsNames = new Array();
            var thresholds = new Array();
            var locationIds = new Array();
            var addresses = new Array();
            var descriptions = new Array();

            for (var i = 0 ; i < recordLen ; i++) {
                dtsIds.push(record[i].get('dtsId'));
                dtsNames.push(record[i].get('dtsName'));
                thresholds.push(record[i].get('threshold'));
                locationIds.push(record[i].get('locationId'));
                addresses.push(record[i].get('address'));
                descriptions.push(record[i].get('description'));
            }

            var params = {"supplierId" : supplierId,
                          "dtsIds" : dtsIds,
                          "dtsNames" : dtsNames,
                          "thresholds" : thresholds,
                          "locationIds" : locationIds,
                          "addresses" : addresses,
                          "descriptions" : descriptions};

            $.getJSON("${ctx}/gadget/system/updateEbsDtsList.do",
                     params,
                     function(json) {
                         if (json.result == "success") {
                             Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>',
                             function() {
                                 dtsStore.commitChanges();
                                 getEbsDtsTree();
                             });
                         } else {
                             Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                         }

                         return;
                     }
            );
        }

        var energyChartWindow;
        // Chart Window
        var makeChartWindow = function() {
            var width = $("#chartWindowDiv").width();

            var html = '<div id="wrap">'
                     + '<div id="chartDiv" class="margin-t10px clear" ></div>'
                     //+ '<input type="hidden" id="chartType">'
                     + '</div>';

            energyChartWindow = new Ext.Window({
                title: '<fmt:message key="aimir.view.chart"/>',
                id: 'energyChart',
                applyTo:'chartWindowDiv',
                autoScroll: true,
                resizable: true,
                width: (width-10),
                height: 420,
                html: html,
                closeAction:'hide'
            });
        };

        // DTS/Meter/phase Chart Window 보이기
        function viewImportChartWindow(nodeId, nodePath) {
            Ext.getCmp('energyChart').show();
            getEbsDtsImportChartData(nodeId, nodePath);
        }

        // Contract Chart Window 보이기
        function viewConsumeChartWindow(nodeId, nodePath) {
            Ext.getCmp('energyChart').show();
            getEbsDtsConsumeChartData(nodeId, nodePath);
        }

        // Import Chart Data 조회
        function getEbsDtsImportChartData(nodeId, nodePath) {
            emergePre();

            $.getJSON('${ctx}/gadget/system/getEbsDtsImportChartData.do'
                    ,{supplierId: supplierId,
                      nodeId: nodeId,
                      searchStartDate: $("#searchStartDate").val(),
                      searchEndDate: $("#searchEndDate").val()}
                    ,function(json) {
                         updateImportChart(json, nodePath);
                    }
            );
        }

        var importChartDataXml;
        // Import Chart 생성
        function updateImportChart(data, nodePath) {
            var curMonImp = data.curMonImport;
            var curMonCon = data.curMonConsume;
            var lastMonCon = data.lastMonConsume;
            var lastYearCon = data.lastYearConsume;
            var thisMonth = data.thisMonth;
            var lastMonth = data.lastMonth;
            var lastYearMonth = data.lastYearMonth;

            //var labelStep = (dataCount <= 5) ? 1 : Math.round(dataCount/5);
            var labelStep = 5;

            importChartDataXml = "<chart "
                              // + "caption='<fmt:message key="aimir.ebs.importchart"/>' "
                               + "yAxisName='<fmt:message key="aimir.usage.kwh"/>' "
                               //+ "chartLeftMargin='10' "
                               //+ "chartRightMargin='20' "
                               //+ "chartTopMargin='20' "
                               //+ "chartBottomMargin='5' "
                               + "showValues='0' "
                               + "showLabels='1' "
                               + "showLegend='1' "
                               + "legendPosition='BOTTOM' "
                               + "labelDisplay='WRAP' "
                               // + "numberSuffix='  ' "
                               + "labelStep='" + labelStep + "' "
                               + "legendNumColumns='2' "
                               // + "decimals='3' "
                               // + "forceDecimals='1' "
                               + fChartStyle_Common
                               + fChartStyle_Font
                               //+ xml_fChartStyle_Column2D_nobg
                               + fChartStyle_MSCombiDY2D_nobg
                               + fChartStyle_legendScroll
                               + ">";

            var categories = new Array();
            var datasetCurMonImp = new Array();
            var datasetCurMonCon = new Array();
            var datasetLastMonCon = new Array();
            var datasetLastYearCon = new Array();

            categories.push("<categories>");
            //var dataset = "<dataset seriesName='" + lastTokenDate + "' color='" + fChartColor_CompareElec[2] + "'>";
            datasetCurMonImp.push("<dataset seriesName='" + thisMonth + " <fmt:message key="aimir.ebs.currentmonthimport"/>'>");
            datasetCurMonCon.push("<dataset seriesName='" + thisMonth +" <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");
            datasetLastMonCon.push("<dataset seriesName='" + lastMonth + " <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");
            datasetLastYearCon.push("<dataset seriesName='" + lastYearMonth + " <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");

            //datasetLastMonCon.push("<dataset seriesName='" + lastMonth + " <fmt:message key="aimir.ebs.lastmonthconsume"/>' renderAs='Line' lineThickness='3'>");
            //datasetLastYearCon.push("<dataset seriesName='" + lastYearMonth + " <fmt:message key="aimir.ebs.lastyearconsume"/>' renderAs='Line' lineThickness='3'>");

            var curMonImpLen = curMonImp.length;
            var curMonConLen = curMonCon.length;
            var lastMonConLen = lastMonCon.length;
            var lastYearConLen = lastYearCon.length;
            var ttext = "";

            for (var i = 0 ; i < 31 ; i++) {
                categories.push("<category label='" + (i+1) + "' />");
                ttext = ""
                if (i < curMonImpLen) {
                    datasetCurMonImp.push("<set value='" + curMonImp[i].energySum + "' tooltext='" + curMonImp[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.currentmonthimport"/>, " + curMonImp[i].energySumFormat + " '/>");
                } else {
                    datasetCurMonImp.push("<set value='0' tooltext=''/>");
                }

                if (i < curMonConLen) {
                    datasetCurMonCon.push("<set value='" + curMonCon[i].energySum + "' tooltext='" + curMonCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.currentmonthconsume"/>, " + curMonCon[i].energySumFormat + " '/>");
                } else {
                    datasetCurMonCon.push("<set value='0' tooltext=''/>");
                }

                if (i < lastMonConLen) {
                    datasetLastMonCon.push("<set value='" + lastMonCon[i].energySum + "' tooltext='" + lastMonCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.lastmonthconsume"/>, " + lastMonCon[i].energySumFormat + " '/>");
                } else {
                    datasetLastMonCon.push("<set value='0' tooltext=''/>");
                }

                if (i < lastYearConLen) {
                    datasetLastYearCon.push("<set value='" + lastYearCon[i].energySum + "' tooltext='" + lastYearCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.lastyearconsume"/>, " + lastYearCon[i].energySumFormat + " '/>");
                } else {
                    datasetLastYearCon.push("<set value='0' tooltext=''/>");
                }
            }

            categories.push("</categories>");
            datasetCurMonImp.push("</dataset>");
            datasetCurMonCon.push("</dataset>");
            datasetLastMonCon.push("</dataset>");
            datasetLastYearCon.push("</dataset>");

            importChartDataXml += categories.join("") + datasetCurMonImp.join("") + datasetCurMonCon.join("")
                               + datasetLastMonCon.join("") + datasetLastYearCon.join("") + "</chart>";

            // 챠트 타이틀 설정
            //energyChartWindow.setTitle("Chart - " + data.dtsLocation + "/ " +data.dtsName);
            energyChartWindow.setTitle("Chart - " + nodePath);
            importChartRender();
            hide();
        }

        //window.onresize = fcChartRender;
        function importChartRender() {
            if($('#chartDiv').is(':visible')) {
                var width = $('#chartDiv').width();
                //impChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/ScrollCombi2D.swf", "myChartId", width, "365", "0", "0");
                impChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombi2D.swf", "myChartId", width, "365", "0", "0");
                impChart.setDataXML(importChartDataXml);
                impChart.setTransparent("transparent");
                impChart.render("chartDiv");
            }
        }

        // Consume Chart Data 조회
        function getEbsDtsConsumeChartData(nodeId, nodePath) {
            emergePre();

            $.getJSON('${ctx}/gadget/system/getEbsDtsConsumeChartData.do'
                    ,{supplierId: supplierId,
                      nodeId: nodeId,
                      searchStartDate: $("#searchStartDate").val(),
                      searchEndDate: $("#searchEndDate").val()}
                    ,function(json) {
                         updateConsumeChart(json, nodePath);
                    }
            );
        }

        var consumeChartDataXml;
        // Consume Chart 생성
        function updateConsumeChart(data, nodePath) {
            var curMonCon = data.curMonConsume;
            var lastMonCon = data.lastMonConsume;
            var lastYearCon = data.lastYearConsume;
            var thisMonth = data.thisMonth;
            var lastMonth = data.lastMonth;
            var lastYearMonth = data.lastYearMonth;

            //var labelStep = (dataCount <= 5) ? 1 : Math.round(dataCount/5);
            var labelStep = 5;

            consumeChartDataXml = "<chart "
                               // + "caption='<fmt:message key="aimir.ebs.consumechart"/>' "
                                + "yAxisName='<fmt:message key="aimir.usage.kwh"/>' "
                                + "chartLeftMargin='10' "
                                + "chartRightMargin='20' "
                                + "chartTopMargin='20' "
                                + "chartBottomMargin='5' "
                                + "showValues='0' "
                                + "showLabels='1' "
                                + "showLegend='1' "
                                + "legendPosition='BOTTOM' "
                                + "legendNumColumns='3' "
                                + "labelDisplay='WRAP' "
                                // + "numberSuffix='  ' "
                                + "labelStep='" + labelStep + "' "
                                // + "decimals='3' "
                                // + "forceDecimals='1' "
                                + fChartStyle_Common
                                + fChartStyle_Font
                                //+ xml_fChartStyle_Column2D_nobg
                                //+ fChartStyle_MSLine_nobg
                                + fChartStyle_MSCombiDY2D_nobg
                                + fChartStyle_legendScroll
                                + ">";

            var categories = new Array();
            var datasetCurMonCon = new Array();
            var datasetLastMonCon = new Array();
            var datasetLastYearCon = new Array();

            categories.push("<categories>");
            //var dataset = "<dataset seriesName='" + lastTokenDate + "' color='" + fChartColor_CompareElec[2] + "'>";
            datasetCurMonCon.push("<dataset seriesName='" + thisMonth + " <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");
            datasetLastMonCon.push("<dataset seriesName='" + lastMonth + " <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");
            datasetLastYearCon.push("<dataset seriesName='" + lastYearMonth + " <fmt:message key="aimir.ebs.currentmonthconsume"/>' renderAs='Line' lineThickness='3'>");

            var curMonConLen = curMonCon.length;
            var lastMonConLen = lastMonCon.length;
            var lastYearConLen = lastYearCon.length;

            for (var i = 0 ; i < 31 ; i++) {
                categories.push("<category label='" + (i+1) + "' />");

                if (i < curMonConLen) {
                    datasetCurMonCon.push("<set value='" + curMonCon[i].energySum + "' tooltext='" + curMonCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.currentmonthconsume"/>, " + curMonCon[i].energySumFormat + " '/>");
                } else {
                    datasetCurMonCon.push("<set value='0'/>");
                }

                if (i < lastMonConLen) {
                    datasetLastMonCon.push("<set value='" + lastMonCon[i].energySum + "' tooltext='" + lastMonCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.lastmonthconsume"/>, " + lastMonCon[i].energySumFormat + " '/>");
                } else {
                    datasetLastMonCon.push("<set value='0'/>");
                }

                if (i < lastYearConLen) {
                    datasetLastYearCon.push("<set value='" + lastYearCon[i].energySum + "' tooltext='" + lastYearCon[i].yyyymmddFormat +
                            ", <fmt:message key="aimir.ebs.lastyearconsume"/>, " + lastYearCon[i].energySumFormat + " '/>");
                } else {
                    datasetLastYearCon.push("<set value='0'/>");
                }
            }

            categories.push("</categories>");
            datasetCurMonCon.push("</dataset>");
            datasetLastMonCon.push("</dataset>");
            datasetLastYearCon.push("</dataset>");
            consumeChartDataXml += categories.join("") + datasetCurMonCon.join("") + datasetLastMonCon.join("")
                                 + datasetLastYearCon.join("") + "</chart>";

            // 챠트 타이틀 설정
            //energyChartWindow.setTitle("Chart - " + data.dtsLocation + "/ " + data.dtsName);
            energyChartWindow.setTitle("Chart - " + nodePath);
            consumeChartRender();
            hide();
        }

        //window.onresize = fcChartRender;
        function consumeChartRender() {
            if($('#chartDiv').is(':visible')) {
                var width = $('#chartDiv').width();
                //conChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/ScrollCombi2D.swf", "myChartId", width, "365", "0", "0");
                //conChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/ScrollLine2D.swf", "myChartId", width, "365", "0", "0");
                //conChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", width, "365", "0", "0");
                conChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombi2D.swf", "myChartId", width, "365", "0", "0");
                conChart.setDataXML(consumeChartDataXml);
                conChart.setTransparent("transparent");
                conChart.render("chartDiv");
            }
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        // open excel download popup
        var win;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.supplierId = supplierId;
            obj.searchStartDate = $("#searchStartDate").val();
            obj.searchEndDate = $("#searchEndDate").val();
            obj.searchDateType = $("#searchDateType").val();
            obj.suspected = $("#suspected").is(":checked") ? "true" : "";
            obj.locationId = $("#locationId").val();
            obj.dtsName = $("#treeDtsName").val();
            obj.threshold = $("#threshold").val();
            obj.week = $("weeklyWeekCombo").val();

            if(win)
                win.close();
            win = window.open("${ctx}/gadget/system/energyBalanceMonitoringExcelDownloadPopup.do", "EnergyBalanceMonitoringExcel", opts);
            win.opener.obj = obj;
        }

    /*]]>*/
    </script>
</head>
<body>
<form name="emsExportExcelForm" id="emsExportExcelForm" action="${ctx}/common/fileDownload.do" method="post" target="downFrame" style="display:none;">
<input type="hidden" id="filePath" name="filePath" value="<fmt:message key="aimir.report.fileDownloadDir"/>"/>
<input type="hidden" id="fileName" name="fileName" />
</form>
<iframe name="downFrame" style="display:none;"></iframe>

<div style="padding:5px;">
    <!-- <div style="float: left; width: 49.5%; height: 700px;" class="border-blue"> -->
    <div style="float: left; width: 48%; height: 670px;" class="border-blue">
        <div class="search-bg-withtabs">
            <div class="dayoptions">
            <%@ include file="/gadget/commonDateTabBasicDate.jsp"%>
            </div>

            <div class="dashedline"><ul><li></li></ul></div>

            <div class="searchoption-container">
                <table class="searchoption wfree">
                    <tr>
                        <td class="padding-r20px" width="35%"  >
                            <input name="searchWord" id='searchWord' type="text" value='<fmt:message key="aimir.board.location"/>'/>
                            <input type='hidden' id='locationId' value=''></input>
                        </td>

                        <td class="padding-r10px" width="20%"><fmt:message key="aimir.ebs.dtsname"/>
                        </td>
                        <td class="padding-r20px" colspan="2" width="25%"><input type="text" id='treeDtsName' />
                        </td>

                        <td width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.ebs.suspecteddts"/><input type="checkbox" class="checkbox" id="suspected" value="Y" checked="checked"/>
                        </td>

                        <td class="padding-r10px"><fmt:message key="aimir.ebs.overLoss"/>
                        </td>

                        <td class="padding-r5px" ><input type="text" id="threshold" />
                        </td>
                        <td class="padding-r10px">%
                        </td>

                        <td >
                           <div id="btn-right">
                               <ul><li><a href="#;" id="btnTreeSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                           </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="mainTreeDivOuter" class="tree-billing auto" style="display:none;">
                <div id="mainTreeDiv"></div>
            </div>
        </div>
        <div id="treeGridDiv" style="heigth:auto;margin:10px"></div>
    </div>

    <div id="emptySpace" style="float:left; width:4px; height:670px;"></div>

    <div id="rightSearchDiv" style="float: left; width: 51.5%; height: 670px;">
        <div id="addBtnSpace" style="float: left; width:23px; height:670px; padding:220px 0 0 0px; display:none;">
            <ul>
                <li class="btn-putout"><a id="btnAddDtsMeterNode" href="#;" title="<fmt:message key="aimir.add"/>"><!-- Meter 추가 --></a></li>
                <li class="btn-putout"><a id="btnAddDtsContractNode" href="#;" title="<fmt:message key="aimir.add"/>"><!-- Contract 추가 --></a></li>
            </ul>
        </div>

        <!-- <div id="rightSearchDiv" style="float: left; width: 51%; height: 700px;" class="border-blue"> -->
        <div id="searchDiv" style="float: left; width: 100%; height: 100%;" class="border-blue">

            <div id="tabDiv">
                <ul>
                    <li><a href="#dtsTabDiv" id="dtsTab" ><fmt:message key="aimir.ebs.dts"/></a></li>
                    <li><a href="#meterTabDiv" id="meterTab" ><fmt:message key="aimir.meter"/></a></li>
                    <li><a href="#contractTabDiv" id="contractTab"><fmt:message key="aimir.contract"/></a></li>
                </ul>

                <div id="dtsTabDiv"  style="padding:13px 1px 5px 5px;">
                    <!-- <div style="height:30px;"> -->
                    <div style="height:54px;">
                            <table class="searchoption wfree" >
                                <tr>
                                    <td class="padding-r10px" width="15%">
                                        <input name="dtsSearchWord" id='dtsSearchWord' type="text" value='<fmt:message key="aimir.board.location"/>'/>
                                        <input type='hidden' id='dtsLocationId' value=''></input>
                                    </td>
                                    <td class="padding-left10px padding-r10px; " width="15%"><fmt:message key="aimir.ebs.dtsname"/>
                                    </td>
                                    <td class="padding-r10px" width="20%"><input type="text" id='dtsName' />
                                    </td>
                                    <td class="padding-left10px padding-r10px; " width="15%"><fmt:message key="aimir.ebs.overLoss"/>
                                    </td>
                                    <td class="padding-r5px" width="10%"><input type="text" id="dtsThreshold" style="width: 50px;"/>
                                    </td>
                                    <td class="padding-r10px" width="15%">%
                                    </td>
                                </tr>
                                <tr>
                                <!-- <td class="padding-r10px"><fmt:message key="aimir.ebs.overLoss"/>
                                    </td>
                                    <td class="padding-r5px" ><input type="text" id="dtsThreshold" />
                                    </td>
                                    <td class="padding-r10px">%
                                    </td>
                                    <td colspan="2"></td> -->

                                    <td align="right" colspan="6">
                                       <div id="btn-right" >
                                           <ul ><li><a href="#;" id="btnDtsSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                       </div>
                                    </td>
                                </tr>
                            </table>
                        <div id="dtsTreeDivOuter" class="tree-billing auto" style="display:none;">
                            <div id="dtsTreeDiv"></div>
                        </div>
                    </div>
                    <div style="height:20px;text-align:right;padding:10px 5px 0 0;color:#777"><fmt:message key="aimir.ebs.msg.availmodifyindicate"/></div>
                    <div id="dtsGridDiv" style="padding:0px 5px 0 0;"></div>
                </div>

                <div id="meterTabDiv" style="padding:7px 5px 5px 5px;">
                    <!-- <div style="height:65px;"> -->
                    <div style="height:89px;">
                            <table class="searchoption wfree" >
                                <tr>
                                    <td class="withinput" width="15%"><fmt:message key="aimir.meterid"/></td>
                                    <td class="padding-left10px padding-r10px" width="30%"><input type="text" id='mdsId' />
                                    </td>
                                    <td class="withinput" width="15%"><fmt:message key="aimir.metergroup"/></td>
                                    <td class="padding-left10px padding-r10px" width="25%">
                                        <select id="meterGroup" name="select" style="width:128px" >
                                            <option value=""><fmt:message key="aimir.all"/></option>
                                        </select>
                                    </td>
                                    <td width="10%"></td>
                                </tr>
                                <tr>
                                    <td class="withinput"><fmt:message key="aimir.vendor"/></td>
                                    <td class="padding-left10px padding-r10px">
                                        <select id="deviceVendor" onchange="javascript:getDeviceModelsByVendorId();" style="width:128px">
                                            <option value=""><fmt:message key="aimir.all"/></option>
                                        </select>
                                    </td>
                                    <td class="withinput"><fmt:message key="aimir.model"/></td>
                                    <td class="padding-left10px padding-r10px">
                                        <select id="deviceModel" style="width:128px">
                                            <option value=""><fmt:message key="aimir.all"/></option>
                                        </select>
                                    </td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="withinput"><fmt:message key="aimir.location"/></td>
                                    <td class="padding-left10px padding-r10px">
                                        <input name="meterSearchWord" id='meterSearchWord' type="text" value='<fmt:message key="aimir.board.location"/>'/>
                                        <input type='hidden' id='meterLocationId' value=''></input>
                                    </td>
                                    <td colspan="2"></td>
                                    <td >
                                       <div id="btn-right" style="float:right;">
                                           <ul><li><a href="#;" id="btnMeterSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                       </div>
                                    </td>
                                </tr>
                            </table>

                        <div id="meterTreeDivOuter" class="tree-billing auto" style="display:none;">
                            <div id="meterTreeDiv"></div>
                        </div>
                    </div>
                    <div id="meterGridDiv"></div>
                </div>

                <div id="contractTabDiv"  style="padding:7px 5px 5px 5px;" >
                    <div style="height:89px;">
                            <table class="searchoption wfree" >
                                <tr>
                                    <td class="withinput" width="25%"><fmt:message key="aimir.customername"/></td>
                                    <td class="padding-left10px padding-r20px" width="20%"><input type="text" id='customerName' />
                                    </td>
                                    <td class="withinput" width="20%"><fmt:message key="aimir.customerid"/></td>
                                    <td class="padding-left10px padding-r10px" width="20%"><input type="text" id='customerId' >
                                    </td>
                                    <td width="10%"></td>
                                </tr>
                                <tr>
                                    <td class="withinput"><fmt:message key="aimir.customergroup"/></td>
                                    <td class="padding-left10px padding-r20px">
                                        <select id="contractGroup" style="width:128px">
                                            <option value=""><fmt:message key="aimir.all"/></option>
                                        </select>
                                    </td>
                                    <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                                    <td class="padding-left10px padding-r10px"><input type="text" id='contractNumber' />
                                    </td>

                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="withinput"><fmt:message key="aimir.location"/></td>
                                    <td class="padding-left10px padding-r20px">
                                        <input name="contSearchWord" id='contSearchWord' type="text" value='<fmt:message key="aimir.board.location"/>'/>
                                        <input type='hidden' id='contLocationId' value=''/>
                                    </td>
                                    <td class="withinput"><fmt:message key="aimir.meterid"/></td>
                                    <td class="padding-left10px padding-r10px"><input type="text" id='contMdsId' />
                                    </td>
                                    <td>
                                       <div id="btn-right" class="textalign-left">
                                           <ul><li><a href="#;" id="btnContSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                       </div>
                                    </td>
                                </tr>
                                <!--
                                <tr>
                                    <td class="withinput"><fmt:message key="aimir.location"/></td>
                                    <td class="padding-r10px">
                                        <input name="contSearchWord" id='contSearchWord' class="billing-searchword" type="text" style="width:130px;" value='<fmt:message key="aimir.board.location"/>'/>
                                        <input type='hidden' id='contLocationId' value=''></input>
                                    </td>

                                    </td>
                                    <td>
                                       <div id="btn" style="float:right;">
                                           <ul><li><a href="#;" id="btnContSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                       </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="3"></td>
                                    <td>
                                       <div id="btn" style="float:right;">
                                           <ul><li><a href="#;" id="btnContSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                       </div>
                                    </td>
                                </tr>
                                -->
                            </table>
                        <div id="contTreeDivOuter" class="tree-billing auto" style="display:none;">
                            <div id="contTreeDiv" ></div>
                        </div>
                    </div>
                    <div id="contractGridDiv"></div>
                </div>

            </div>

        </div>
    </div>

    <div id="addDtsBtnList">
        <div id="btn-right" class="btn_topright_substation">
            <ul id="newDtsPageUl"><li><a id="newDtsPage" href="#;"><span class="greenbold11pt"><fmt:message key="aimir.ebs.newdts"/><!-- New DTS --></span></a></li></ul>
            <ul id="searchPageUl" style="display:none;"><li><a id="searchPage" href="#;"><span class="greenbold11pt"><fmt:message key="aimir.button.search"/></span><!-- Search --></a></li></ul>
        </div>
    </div>

    <div id="rightAddDiv" style="float:left; width:51.2%; height:670px; display:none;" class="border-blue">

        <div class="customerUpdate" style="width:70%; padding:10px; text-align:center;">
            <div class="headspace">
                <label class="check"><fmt:message key="aimir.ebs.substationReg"/><!-- Substation정보 수정 --></label>
            </div>
            <div style="height:20px;text-align:right;padding:72px 5px 0 0;color:#777"><fmt:message key='aimir.hems.inform.requiredField'/></div>
            <table class="dts_regist">
                <tr><th class="blue11pt"><fmt:message key="aimir.ebs.dtsname"/><em class="icon_star">&nbsp;</em><!-- DTS Name--></th>
                    <td>
                        <input type="text" id="addDtsName" style="width: 200px;"/>
                        <div id="btn">
                            <ul><li><a href="#;" id="btnDupCheck" class="on"><fmt:message key="aimir.checkDuplication2" /></a></li></ul>
                        </div>
                        <input type="hidden" id="dupCheckDtsName" style="display:none;"/>
                    </td>
                </tr>
                <tr><th class="blue11pt"><fmt:message key="aimir.loss"/> [%]<em class="icon_star">&nbsp;</em><!-- 임계치 --></th>
                    <td class="blue11pt"><input type="text" id="addThreshold" style="width: 100px;"/>
                      <span class="blue11pt" style="overflow:hidden;">&nbsp;&nbsp;&nbsp;&nbsp;(&nbsp;<fmt:message key="aimir.default"/>&nbsp;<fmt:message key="aimir.loss"/>&nbsp;:&nbsp;</span>
                      <span class="blue11pt" style="overflow:hidden;" id="dspThreshold"></span>
                      <span class="blue11pt" style="overflow:hidden;">)</span>
                    </td>
                </tr>
                <tr><th class="blue11pt"><fmt:message key="aimir.location"/><em class="icon_star">&nbsp;</em><!-- 지역 --></th>
                    <td>
                        <input name="addSearchWord" id='addSearchWord' class="billing-searchword" type="text" style="width:130px;" value='<fmt:message key="aimir.board.location"/>'/>
                        <input type='hidden' id='addLocationId' value=''></input>
                    </td>
                </tr>
                <tr><th class="blue11pt"><fmt:message key="aimir.address"/><!-- 주소 --></th>
                    <td><input type="text" id="addAddress"/></td>
                </tr>
                <tr><th class="blue11pt"><fmt:message key="aimir.description"/><!-- 설명 --></th>
                    <td align="left"><textarea id="addDescription" rows="3" style="width:95%" ></textarea></td>
                </tr>
            </table>
            <div id="btn" class="btn_right_bottom">
               <ul><li><a href="#;" id="btnAddDts" class="on"><fmt:message key="aimir.button.register" /></a></li></ul>
            </div>

        </div>
        <div id="addTreeDivOuter" class="tree-billing auto" style="display:none;">
            <div id="addTreeDiv"></div>
        </div>

    </div>
</div>
<div id="chartWindowDiv" style="width : 750px;"></div>

</body>
</html>