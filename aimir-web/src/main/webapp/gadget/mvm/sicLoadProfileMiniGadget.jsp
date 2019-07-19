<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title>SIC Load Profile Mini Gadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
<%--
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        /*TABLE{border-collapse: collapse; width:auto;}*/
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        /*@media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }*/
--%>
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
        }
        /* no Icon */
        .no-icon {
            display: none;
            background-image:url(${ctx}/js/extjs/resources/images/default/s.gif) !important;
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

        var supplierId = ${supplierId};
        var tabsSnd = {
            period : 1
        };
        var tabNamesSnd = {};

        $(document).ready(function(){
            Ext.QuickTips.init();
            hide();
            getSicCustomerEnergyUsageList();
        });

        // window resize event
        $(window).resize(function() {
        	makeSicContEnergyUsageTree();
        });

        function sendSnd() {
        	getSicCustomerEnergyUsageList();
        }

        /* 리스트 START */
        //var sicEnergyUsageGridOn = false;
        //var sicEnergyUsageGrid;
        //var sicEnergyUsageColModel;
        //var getSicCustomerEnergyUsageList = function() {
        //    var width = $("#sicUsageGridDiv").width();
        //
        //    emergePre();
        //
        //    var sicEnergyUsageStore = new Ext.data.JsonStore({
        //        autoLoad: true,
        //        url: "${ctx}/gadget/mvm/getSicCustomerEnergyUsageList2.do",
        //        baseParams: {
        //            supplierId : supplierId,
        //            searchStartDate : $("#searchStartDateSnd").val(),
        //            searchEndDate : $("#searchEndDateSnd").val()
        //        },
        //        totalProperty: 'totalCount',
        //        root:'result',
        //        fields: ["sicId", "sicCode", "sicName", "customerCount", "usageSum"]
        //    });
        //
        //    sicEnergyUsageColModel = new Ext.grid.ColumnModel({
        //        columns: [
        //            {header: '<fmt:message key="aimir.class"/>', dataIndex: 'sicName', width: (width)/3}
        //           ,{header: '<fmt:message key="aimir.customercount"/>', dataIndex: 'customerCount', width: (width)/3}
        //           ,{header: '<fmt:message key="aimir.usagesum"/>', dataIndex: 'usageSum', width: (width)/3}
        //        ],
        //        defaults: {
        //            sortable: true
        //           ,menuDisabled: true
        //           ,width: 120
        //       }
        //    });
        //
        //    if(sicEnergyUsageGridOn == false) {
        //
        //      sicEnergyUsageGrid = new Ext.grid.GridPanel({
        //            //title: '최근 한달 Demand Response History',
        //            store: sicEnergyUsageStore,
        //            colModel : sicEnergyUsageColModel,
        //            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
        //            autoScroll:true,
        //            width: width,
        //            height: 250,
        //            stripeRows : true,
        //            columnLines: true,
        //            loadMask:{
        //                msg: 'loading...'
        //            },
        //            renderTo: 'sicUsageGridDiv',
        //            viewConfig: {
        //                enableRowBody:true,
        //                showPreview:true,
        //                forceFit:true,
        //                emptyText: 'No data to display'
        //            }
        //        });
        //      sicEnergyUsageGridOn = true;
        //    } else {
        //      sicEnergyUsageGrid.setWidth(width);
        //        sicEnergyUsageGrid.reconfigure(sicEnergyUsageStore, sicEnergyUsageColModel);
        //    }
        //    hide();
        //};

        //var sicEnergyUsageGridOn = false;
        //var sicEnergyUsageGrid;
        //var sicEnergyUsageColModel;

        
        //            supplierId : supplierId,
        //            searchStartDate : $("#searchStartDateSnd").val(),
        //            searchEndDate : $("#searchEndDateSnd").val()

        var treeData;
        // DTS Tree Data 조회
        function getSicCustomerEnergyUsageList() {
            emergePre();
            $.getJSON('${ctx}/gadget/mvm/getSicContEnergyUsageTreeData.do'
                    , { supplierId : supplierId,
                        searchStartDate : $("#searchStartDateSnd").val(),
                        searchEndDate : $("#searchEndDateSnd").val()}
                    , function (json){
                        treeData = json.result
                        hide();
                        makeSicContEnergyUsageTree();
                    });
        };

        /* SIC Tree 생성 */
        var sicUsageTreeGridOn = false;
        var sicUsageTreeGrid;
        var sicUsageTreeRootNode;
        //var sicUsageTreeColModel;
        function makeSicContEnergyUsageTree() {
            var width = $("#sicUsageGridDiv").width();
            var headerClass = "<fmt:message key="aimir.class"/>";
            var headerContract = "<fmt:message key="aimir.customercount"/>";
            var headerUsage = "<fmt:message key="aimir.usagesum"/>";

            var sicUsageTreeColModel = [
                   {header: "<font style='font-weight: bold;'>" + headerClass + "</font>", dataIndex: 'sicName', width: (width / 3) 
                        ,tpl: new Ext.XTemplate('{sicName:this.viewToolTip}', {
                            viewToolTip: addTreeTooltip
                        })
                   }
                   ,{header: "<font style='font-weight: bold;'>" + headerContract + "</font>", dataIndex: 'customerCount', width: (width / 3) - 20, align: "right"}
                   ,{header: "<font style='font-weight: bold;'>" + headerUsage + "</font>", dataIndex: 'usageSum', width: (width / 3), align: "right"}
            ];

            sicUsageTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'root',
                draggable:false,
                expended:true,
                children: treeData
            });

            if (!sicUsageTreeGridOn) {
                var treeLoader = new Ext.tree.TreeLoader({
                    url:'${ctx}/gadget/mvm/getSicContEnergyUsageTreeData.do',
                    baseParams: {
                      supplierId : supplierId,
                      searchStartDate : $("#searchStartDateSnd").val(),
                      searchEndDate : $("#searchEndDateSnd").val()
                    }
                });

                treeLoader.on("beforeload", function(treeLoader, node) {
                    treeLoader.baseParams.searchStartDate = $("#searchStartDateSnd").val();
                    treeLoader.baseParams.searchEndDate = $("#searchEndDateSnd").val();
                });

                sicUsageTreeGrid = new Ext.ux.tree.TreeGrid({
                    width: width,
                    height: 250,
                    renderTo: "sicUsageGridDiv",
                    enableDD: false,
                    enableHdMenu : false,
                    enableSort : false,
                    columns: sicUsageTreeColModel,
                    loader: treeLoader,
                    root: sicUsageTreeRootNode,
                    rootVisible: false
                });

                sicUsageTreeGridOn = true;
            } else {
            	sicUsageTreeGrid.setWidth(width);
            	sicUsageTreeGrid.setRootNode(sicUsageTreeRootNode);
            	sicUsageTreeGrid.render();
            }
        }

        // treegrid column tooltip
        function addTreeTooltip(value, values) {
            if (value != null && value != "" && values != null) {
                return '<span qtip="' + value + '">' + value + '</span>';
            } else {
                return value;
            }
        }
    /*]]>*/
    </script>
</head>
<body>
    <div class="search-bg-basic" style="height:35px !important;">
        <div class="height_30" style="margin:0px 0px 0px 5px;">
        <%@ include file="/gadget/commonDateTabButtonType4.jsp"%>
        </div>
    </div>
    <div id="gadget_body">
        <div id="sicUsageGridDiv"></div>
    </div>
</body>
</html>