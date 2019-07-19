<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><fmt:message key="gadget.system008"/></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
        /* ext-js grid 내 button 등이 있을 경우 높이조절 */
        .grid-button-height .x-grid3-cell-inner {
            padding-top: 0.5px;
            padding-bottom: 0.5px;
        }
    </style>
    <script type="text/javascript" charset="utf-8">

        var supplierId = "${supplierId}";
        var searchDate = "${searchDate}";

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            operationLogGrid.destroy();
            operationLogGridOn = false;

            getOperationLogList();
        });

        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
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
        }

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.ranking"/>";
        fmtMessage[1] = "<fmt:message key="aimir.instrumentation"/>";
        fmtMessage[2] = "<fmt:message key="aimir.total"/> (<fmt:message key="aimir.success"/>/<fmt:message key="aimir.failure"/>)";

        function renderGph(value, metadata, record, rowIndex, colIndex, store) {

            var record = store.data.items[rowIndex].data;
            var percentage = (record.successCnt.replace(",","") / record.cnt.replace(",","")) * 100;
            var w = Math.floor(percentage);

            var html = '<div style="margin: 2px 18px 2px 5px; height: 18px;">'+
                       '<div class="x-progress-wrap" style="height: 3px;">'+
                       '<div class="x-progress-inner">'+
                       '<div class="x-progress-bar" style="width:'+w+'%; height: 3px !important;">'+
                       '</div>'+
                       '</div>'+
                       '</div>'+
                       '<div class="x-progress-text x-progress-text-back" style="position: static !important; font-size: 8px !important;">'+
                       '<div>' + record.cnt +'('+record.successCnt+ '/'+record.failCnt+')'+'</div>'+
                       '</div>'+
                       '</div>';

            return html;
        };

        //$(document).ready(function() {
        Ext.onReady(function() {
            Ext.QuickTips.init();
            extColumnResize();

            getOperationLogList();
        });

        var operationLogStore;
        var operationLogColModel;
        var operationLogGridOn = false;
        var operationLogGrid;
        function getOperationLogList() {
            var width = $("#OperationLogDiv").width();
            operationLogStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/device/operationLog/getOperationLogMiniChartData.do",
                root:'chartDatas',
                fields: [
                { name: 'cnt', type: 'String' },
                { name: 'failCnt', type: 'String' },
                { name: 'operation', type: 'String' },
                { name: 'operationCnt', type: 'Integer' },
                { name: 'rank', type: 'String' },
                { name: 'successCnt', type: 'String' },
                { name: 'systemCnt', type: 'Integer' },
                { name: 'userCnt', type: 'Integer' },
                { name: 'width', type: 'Integer' }
                ]
            });

            var colWidth = (width-70)/2 - chromeColAdd;
            operationLogColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[0],
                     dataIndex: 'rank',
                     width: 70 - chromeColAdd
                    }
                   ,{header: fmtMessage[1],
                     dataIndex: 'operation',
                     width: colWidth - 15,
                     renderer: addTooltip
                    },
                    {header: fmtMessage[2],
                     dataIndex: 'successCnt',
                     width: colWidth + 15,
                     tooltip: fmtMessage[2],
                     renderer: renderGph
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-50)/4)-chromeColAdd
                    ,align: 'center'
                }
            });

            if (operationLogGridOn == false) {
                operationLogGrid = new Ext.grid.GridPanel({
                    id : 'OperationLogGrid',
                    store : operationLogStore,
                    colModel : operationLogColModel,
                    autoScroll : false,
                    width : width,
                    height : 280,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'OperationLogDiv',
                    viewConfig : {
                        forceFit : true,
                        scrollOffset : 1,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
                operationLogGridOn = true;
            } else {
                operationLogGrid.setWidth(width);
                operationLogGrid.reconfigure(operationLogStore, operationLogColModel);
            }
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
    </script>
</head>

<body>

    <div id="gadget_body" style="padding: 10px 10px !important;">
        <div id="searchgroup" style="margin-bottom: 5px;">
             <ul>
                <li><fmt:message key="aimir.searchDate"/> : ${searchDate}</li>
            </ul>
        </div>
        <div id="OperationLogDiv" class="grid-button-height"></div>
    </div>

</body>

</html>