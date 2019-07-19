<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 
        @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }*/
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var supplierId = "${supplierId}";
        var allStr = "<fmt:message key="aimir.all"/>";

        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
            var isIE9 = (navigator.userAgent.indexOf("Trident/5") > -1);

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
        }

        Ext.onReady(function() {
            Ext.QuickTips.init();
            extColumnResize();
            var locDateFormat = "yymmdd";

            getSelectBox();

            $("#startLogDateHidden").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#endLogDateHidden")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            initDateCondition();

            $("#operatorType").selectbox();
            $("#operationCode").selectbox();
            $("#targetCode").selectbox();
            hide();
            //window.setTimeout(getAuditLogList, 500);
            getAuditLogList();
        });

        // datepicker로 선택한 날짜의 포맷 변경
        function modifyDateLocal(setDate, inst) {
            var dateId = '#' + inst.id.substring(0, (inst.id.length-6));

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate: setDate, supplierId: supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                    });
        }

        /**
         * 현재 일자 조회
         */
        function initDateCondition() {
            var params = {
                searchDate : '',
                addVal : 0,
                supplierId:supplierId
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getDate.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("var json=" + jsonText);

            if (json != null) {
                $('#startLogDate').val(json.searchDate);                    
                $('#endLogDate').val(json.searchDate);
                $('#startLogDateHidden').val(json.dbDate);                    
                $('#endLogDateHidden').val(json.dbDate);
            }
        }

        // window resize event
        $(window).resize(function() {
            getAuditLogList();
        });

        /* Audit Log 리스트 START */
        var auditLogGridOn = false;
        var auditLogGrid;
        var auditLogColModel;
        var auditLogCheckSelModel;
        var getAuditLogList = function() {
            var width = $("#auditLogDiv").width();
            var rowSize = 15;

            //emergePre();
            var auditLogStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: rowSize}},
                url: "${ctx}/gadget/system/getAuditLogList.do",
                baseParams: {
                    action : $("#action").val(),
                    equipType : $("#equipType").val(),
                    equipName : $("#equipName").val(),
                    propertyName : $("#propertyName").val(),
                    startDate : $("#startLogDateHidden").val(),
                    endDate : $("#endLogDateHidden").val(),
                    loginId : $("#loginId").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["num", "createdDate", "action", "entityName", "equipName", "propertyName", "previousState", "currentState", "loginId"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            var colWidth = (width-50)/7;
            auditLogColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: '<fmt:message key="aimir.number"/>', dataIndex: 'num', width: 50 - chromeColAdd, align: 'center'}
                   ,{header: '<fmt:message key="aimir.changelogdate"/>', dataIndex: 'createdDate', width: colWidth - chromeColAdd - 50, align: 'center'}
                   ,{header: '<fmt:message key="aimir.audit.action"/>', dataIndex: 'action', width: colWidth * 0.5 - chromeColAdd, align: 'center'}
                   ,{header: '<fmt:message key="aimir.equiptype"/>', dataIndex: 'entityName', width: colWidth * 0.9 - chromeColAdd -50, align: 'center'}
                   ,{header: '<fmt:message key="aimir.equipname"/>', dataIndex: 'equipName', width: colWidth - chromeColAdd}
                   ,{header: '<fmt:message key="aimir.property"/>', dataIndex: 'propertyName', width: colWidth - chromeColAdd}
                   ,{header: '<fmt:message key="aimir.beforevalue"/>', dataIndex: 'previousState', width: colWidth * 1.3 - chromeColAdd - 20}
                   ,{header: '<fmt:message key="aimir.currentvalue"/>', dataIndex: 'currentState', width: colWidth * 1.3 - chromeColAdd - 10}
                   ,{header: '<fmt:message key="aimir.loginId"/>', dataIndex: 'loginId', width: colWidth * 0.7 - chromeColAdd, align: 'center'}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
                   ,renderer : addTooltip
               }
            });

            if (auditLogGridOn == false) {

                auditLogGrid = new Ext.grid.GridPanel({
                    store: auditLogStore,
                    colModel : auditLogColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 405,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'auditLogDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: rowSize,
                        store: auditLogStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                auditLogGridOn = true;
            } else {
                auditLogGrid.setWidth(width);
                var bottomToolbar = auditLogGrid.getBottomToolbar();
                auditLogGrid.reconfigure(auditLogStore, auditLogColModel);
                bottomToolbar.bindStore(auditLogStore);
            }
            //hide();
        };

        // select box 데이터를 조회한다.
        function getSelectBox() {
            $.getJSON("${ctx}/gadget/system/getChangeLogSelectBoxData.do", 
                    function(json) {
                        if(json.action != ""){
                            $('#action').loadSelect(json.action);
                            $("#action option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                            $("#action").val('');
                            $("#action").selectbox();
                            //$("#equipType").selectbox();
                        }
                    }
            );
        }

        // 데이터 조회
        function searchData() {
            if (!validationCheck()) return;

            getAuditLogList();
        }

        // 날짜조건 검증
        function validationCheck() {
            if($('#startLogDateHidden').val() != "" && $('#endLogDateHidden').val() != "") {
                if(Number($('#startLogDateHidden').val()) > Number($('#endLogDateHidden').val())) {
                    Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.season.error"/>');
                    return false;
                }
            }
            return true;
        }
            
        // 날짜조건 삭제
        /* function sDateClear(obj){
            var dateId       = '#' + obj.id;
            var dateHiddenId = '#' + obj.id + 'Hidden';

            $(dateId).val("");       
            $(dateHiddenId).val("");
        } */

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
        /*]]>*/
    </script>
</head>

<body>

    <div id="changeLogDiv" style="width: 100%; float: left;">

        <!-- Search Background (S) -->
        <div class="search-bg-withouttabs">
            <div class="searchoption-container">
                <table class="searchoption wfree" style="width: 100%;">
                    <tr>
                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.audit.action"/></td>
                        <td class="padding-r20px" style="width: 190px;">
                            <select id="action" name="action" style="width:180px;"><option value=""></option></select>
                        </td>

                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.equipname"/></td>
                        <td class="padding-r20px" style="width: 220px;">
                            <input type="text" id="equipName" name="equipName" style="width:180px;"/>
                        </td>

                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.property"/></td>
                        <td><input type="text" id="propertyName" name="propertyName" style="width:180px;"/></td>

                        <td>&nbsp;</td>

                    </tr>

                    <tr>
                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.equiptype"/></td>
                        <td class="padding-r20px" style="width: 190px;">
                            <!-- <select id="equipType" name="equipType" style="width:180px;">
                                <option value=""><fmt:message key="aimir.all"/></option>
                            </select> -->
                            <input type="text" id="equipType" name="equipType" style="width:180px;"/>
                        </td>
                    
                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.changelogdate"/></td>
                        <td class="padding-r20px" style="width: 220px;">
                            <span><input id="startLogDate" class="day" type="text" readonly="readonly"/><input id="startLogDateHidden" type="hidden"/></span>
                            <span><input value="~" class="between" type="text"/></span>
                            <span><input id="endLogDate" class="day" type="text" readonly="readonly"/><input id="endLogDateHidden" type="hidden"/></span>
                        </td>

                        <td class="withinput" style="width: 100px;"><fmt:message key="aimir.loginId"/></td>
                        <td style="width: 200px;"><input type="text" id="loginId" name="loginId" style="width:180px;"/></td>                        

                        <td>
                            <div id="btn">
                                <ul><li><a href="javascript:searchData();" class="on"><fmt:message key="aimir.button.search"/></a></li></ul>
                            </div>
                        </td>
                    </tr>

                </table>
                
            </div>
        </div>

        <div class="gadget_body">
            <div id="auditLogDiv" class="margin-t10px; width: 100%;"></div>
        </div>
    </div>
    <!-- Search Background (E) -->


</body>
</html>