<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--
    String name = request.getParameter("name") == null ? "<fmt:message key='aimir.groupNmember.name'/>" : request.getParameter("name");
--%>
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>그룹 관리</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script> --%>
    <%-- <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script> --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script language="JavaScript">/*<![CDATA[*/

        var supplierId = "${supplierId}";
        var operatorId = "${operatorId}";

        var groupName = "<fmt:message key="aimir.group.name"/>";

        $(document).ready(function(){
            $('#groupType').selectbox();
            getGroupList();
            hide();
        });

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

        /* Group 리스트 START */
        var groupGridOn = false;
        var groupGrid;
        var groupStore;
        var groupColModel;
        var groupCheckSelModel;
        var getGroupList = function() {
            var width = $("#groupGridDiv").width();

            emergePre();
            groupStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/system/getGroupListNotHomeGroupIHD.do",
                baseParams: {
                	supplierId : supplierId,
                    operatorId : operatorId,
                    groupType : $("#groupType").val(),
                    groupName : ($("#groupName").val() == groupName) ? "" : $("#groupName").val()
                },
                root:'result',
                fields: ["groupId", "groupName", "groupType", "allUserAccess", "memCount", "mobileNo"],
            });

            var colWidth = width/4 - chromeColAdd;

            var headerGroupName = "<fmt:message key="aimir.group.name"/>";
            var headerGroupType = "<fmt:message key="aimir.grouptype"/>";
            var headerUserAccess = "<fmt:message key="aimir.allUserAccess"/>";
            var headerMemberCount = "<fmt:message key="aimir.group.membercount"/>";
            var headerMobileNo = "<fmt:message key="aimir.celluarphone"/>";

            groupColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: headerGroupName, dataIndex: 'groupName', width: colWidth, tooltip: headerGroupName, renderer: addTooltip}
                   ,{header: headerGroupType, dataIndex: 'groupType', width: colWidth, align: "center", tooltip: headerGroupType, renderer: addTooltip}
                   ,{header: headerUserAccess, dataIndex: 'allUserAccess', width: colWidth, align: "center", tooltip: headerUserAccess}
                   ,{header: headerMemberCount, dataIndex: 'memCount', width: colWidth-26, tooltip: headerMemberCount, align:'right'}
                   ,{header: headerMobileNo, dataIndex: 'mobileNo', width: colWidth-26, tooltip: headerMemberCount}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
               }
            });

            if (groupGridOn == false) {
                groupGrid = new Ext.grid.GridPanel({
                    store : groupStore,
                    colModel : groupColModel,
                    sm : new Ext.grid.RowSelectionModel({singleSelect : true}),
                    autoScroll : true,
                    scroll : true,
                    width : width,
                    height : 419,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'groupGridDiv',
                    viewConfig : {
                        forceFit : true,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
                groupGridOn = true;
            } else {
                groupGrid.setWidth(width);
                groupGrid.reconfigure(groupStore, groupColModel);
            }
            hide();
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

    </script>
</head>
<body>
    <!-- search-background DIV (S) -->
    <div class="search-bg-basic">

        <!--검색조건-->
        <div class="searchoption-container" style="margin-top: 5px;">
            <table class="searchoption wfree">
                <tr>
                    <td>
                        <select id="groupType" style="width:120px">
                            <option value=""><fmt:message key="aimir.grouptype"/></option>
                            <c:forEach var="groupType" items="${groupType}">
                            <option value="${groupType.name}">${groupType.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td class="space10"></td>

                    <td><input id="groupName" type="text" style="width: 100px;" value="<fmt:message key="aimir.group.name"/>" onclick="javascript:this.value = '';"/></td>
                    <td class="space10"></td>

                    <td>
                        <div id="btn">
                            <ul><li><a href="javascript:getGroupList();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <!--검색조건 끝-->

    </div>
    <!-- search-background DIV (E) -->

    <div class="gadget_body">
        <div id="groupGridDiv"></div>
    </div>
</body>
</html>