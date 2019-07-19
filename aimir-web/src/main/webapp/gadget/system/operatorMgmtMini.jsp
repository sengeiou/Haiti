<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">

    <title>사용자 관리</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
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
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        $(function(){
            $('#roleSelecter').selectbox();
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
 
            grid.destroy();
            gridOn = false;
            
            getOperatorList();
                
        });  

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.id"/>";    // 아이디
        fmtMessage[1] = "<fmt:message key="aimir.name"/>";   // 사용자 이름
        fmtMessage[2] = "<fmt:message key="aimir.tel.no"/>";  // 연락처
        fmtMessage[3] = "<fmt:message key="aimir.email"/>";    // E-mail
        fmtMessage[4] = "<fmt:message key="aimir.approach"/>";      // 접속제한
        fmtMessage[5] = "<fmt:message key="aimir.board.location"/>";      // 지역

        function getFmtMessage() {
            return fmtMessage;
        }

        function getOperatorList() {
            var roleManageObj = document.getElementById("roleManage");
            var selectedIdx = roleManageObj.roleSelecter.selectedIndex;
            this.roleId = roleManageObj.roleSelecter[selectedIdx].value;
            //alert(this.roleId);
            getOperatorListGrid({
                roleId : this.roleId
            });
        }

        var operatorGridStore;

        function getOperatorListGrid(params) {
            operatorGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                //url: "${ctx}/gadget/system/operator/getOperatorList.do",
                url: "${ctx}/gadget/system/operator/getOperatorListByRole.do",
                baseParams: {
                    roleId : params.roleId
                },
                totalProperty: 'totalCount',
                root: 'gridDatas',
                fields: [
                    { name: 'id', type: 'string' },
                    { name: 'loginId', type: 'string' },
                    { name: 'name', type: 'string' },
                    { name: 'telNo', type: 'string' },
                    { name: 'email', type: 'string' },
                    { name: 'loginDenied', type: 'string' },
                    { name: 'location', type: 'string' }
                ],
                listeners: {
                    beforeload: function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    },
                    load: function(store, record, options) {
                        makeGridPanel();
                    }
                }
            });
            //console.log(operatorGridStore);
        }

        var grid = undefined;
        var gridOn = false;
        function makeGridPanel() {
            var width = $("#OperatorListDiv").width();

            var colModel = new Ext.grid.ColumnModel({
                defaults : {
                    width : 370,
                    height : 270,
                    sortable : true
                },
                columns : [
                {
                     header:fmtMessage[0],
                     align: 'left',
                     width: width/6,
                     dataIndex: "loginId",
                     renderer: addTooltip
                },{
                    header: fmtMessage[1],
                    align: 'left',
                    width : width/6,
                    dataIndex : "name",
                    renderer: addTooltip
                }, {
                    header: fmtMessage[2],
                    //width: width/6+5,
                    width: width/6,
                    align: 'left',
                    dataIndex: "telNo",
                    renderer: addTooltip
                },{
                    header: fmtMessage[3],
                    width: width/6,
                    align: 'left',
                    dataIndex: "email",
                    renderer: addTooltip
                },{
                    header: fmtMessage[4],
                    //width: width/6-5,
                    width: width/6,
                    align: 'left',
                    dataIndex: "loginDenied"
                },{
                    header: fmtMessage[5],
                    //width: width/6+5,
                    width: width/6-5,
                    align: 'left',
                    dataIndex: "location",
                    renderer: addTooltip
                }]
            });
             //페이징 툴바 셋팅
            var pagingToolbar = new Ext.PagingToolbar({
                store: operatorGridStore,
                displayInfo: true,
                pageSize:10,
                prependButtons: true,
                displayMsg: ' {0} - {1} / {2}'
            });
            //그리드 설정
            if (!gridOn) {
                grid = new Ext.grid.GridPanel({
                    //height : 270,
                    height : 278,
                    renderTo : 'OperatorListDiv',
                    store : operatorGridStore,
                    colModel : colModel,
                    width :width,
                    bbar: pagingToolbar,
                    viewConfig: {
                        // forceFit:true,
                         showPreview:true,
                         emptyText: 'No data to display'
                    }
                });

                gridOn = true;
            } else {
                var bottomToolbar = grid.getBottomToolbar();
                grid.reconfigure(operatorGridStore, colModel);
                bottomToolbar.bindStore(operatorGridStore);
            }
        }

        $(document).ready(function() {
            Ext.QuickTips.init();
            getOperatorList();
        });

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
    /*]]>*/
    </script>
    <style type="text/css">


/*      html {
    overflow: -moz-scrollbars-vertical;
    }


    .x-grid3-hd-inner
    {
        text-align: center;
        font-weight: bold;
    } */
    </style>
</head>
<body>


    <form name="roleManage" id = "roleManage">
        <input type="hidden" name="supplierId" />
        <input type="hidden" name="roleId" value="${roleId}" />
        <div id="usergroup1" class="search-bg-basic">
            <ul class="basic-ul">
                <li class="basic-li gray11pt withinput"><fmt:message key="aimir.user.group"/></li><!-- 사용자 그룹 -->
                <li class="basic-li">
                    <select id="roleSelecter" name="roleSelecter" style="width:130px;" onchange="javascript:getOperatorList();">
                        <c:forEach var="role" items="${roleList}">
                           <c:choose>
                            <c:when test="${role.id == roleId}">
                                <option value="${role.id}" selected>${role.name}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${role.id}" >${role.name}</option>
                            </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </li>
            </ul>
        </div>
    </form>



    <div id="gadget_body">

        <div id="OperatorListDiv"></div>

    </div>

</body>
</html>