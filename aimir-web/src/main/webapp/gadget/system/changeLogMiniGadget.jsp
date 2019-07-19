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
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        $(document).ready(function(){
            hide();
            Ext.QuickTips.init ();
            getAuditLogRankingList();
        });

        // window resize event
        $(window).resize(function() {
            getAuditLogRankingList();
        });

        /* Audit Log Ranking 리스트 START */
        var auditLogRankingGridOn = false;
        var auditLogRankingGrid;
        var auditLogRankingColModel;
        var auditLogRankingCheckSelModel;
        var getAuditLogRankingList = function() {
            var width = $("#auditLogRankingDiv").width();
            var rowSize = 10;

            //emergePre();
            var auditLogRankingStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: rowSize}},
                url: "${ctx}/gadget/system/getAuditLogRankingList.do",
                totalProperty: 'totalCount',
                root:'result',
                fields: ["ranking", "entityName", "propertyName", "action", "count"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            auditLogRankingColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: '<fmt:message key="aimir.ranking"/>', dataIndex: 'ranking', width: (width/5) * 0.5}
                   ,{header: '<fmt:message key="aimir.audit.entity"/>', dataIndex: 'entityName', width: (width/5) * 1.5,
                       renderer: addTooltip}
                   ,{header: '<fmt:message key="aimir.property"/>', dataIndex: 'propertyName', width: (width/5) * 1.5,
                       renderer: addTooltip}
                   ,{header: '<fmt:message key="aimir.audit.action"/>', dataIndex: 'action', width: (width/5) * 0.9}
                   ,{header: '<fmt:message key="aimir.count"/>', dataIndex: 'count', width: ((width/5) * 0.6) - 7}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(auditLogRankingGridOn == false) {

                auditLogRankingGrid = new Ext.grid.GridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: auditLogRankingStore,
                    colModel : auditLogRankingColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 290,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'auditLogRankingDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: rowSize,
                        store: auditLogRankingStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                auditLogRankingGridOn = true;
            } else {
                auditLogRankingGrid.setWidth(width);
                var bottomToolbar = auditLogRankingGrid.getBottomToolbar();
                auditLogRankingGrid.reconfigure(auditLogRankingStore, auditLogRankingColModel);
                bottomToolbar.bindStore(auditLogRankingStore);
            }
            //hide();
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

    /*]]>*/
    </script>
</head>
<body>
    <div id="gadget_body">
        <div id="auditLogRankingDiv" class="margin-t10px; width: 100%;"></div>
    </div>
</body>
</html>