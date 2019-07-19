<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

    <title><fmt:message key=""/>(<fmt:message key="aimir.energymeter"/>)</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
     <style type="text/css">
 
    TABLE{border-collapse: collapse; width:auto;}
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
        .tree .ltr ins { margin:0 4px 0 5px !important;}
    }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold;
    }
     .x-grid3-row td.x-grid3-cell-meterEventName{
       padding-left: 30px !important;
       font-weight: bold !important;
    }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //탭초기화
        var tabs = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:1,seasonal:0,yearly:0};
        var tabNames = {};

        var supplierId = ${supplierId};
        var serviceType = ServiceType.Electricity;
        var tabType = VEEType.ValidateCheck;
        var chromeColAdd = 2;

        $(document).ready(function(){
            changeTab(VEEType.ValidateCheck);
        });

         $(window).resize(function() {
            if(!(veeValidationCheckMiniGrid === undefined)){
                veeValidationCheckMiniGrid.destroy();
            }
            veeValidationCheckMiniGridOn  = false;
            getVEEValidationCheckMiniGrid();

            if(!(veeHistoryMiniGrid === undefined)){
                veeHistoryMiniGrid.destroy();                
            }

            veeHistoryMiniGridOn  = false;
            getVEEHistoryMiniGrid();

         });

        //전체 조회
        function send(){
             if(tabType == VEEType.ValidateCheck) {
                getVEEValidationCheckMiniGrid();

            }else{
                getVEEHistoryMiniGrid();

            }
        };

        function getFmtMessage(){
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.number"/>";//번호
            fmtMessage[1] = "<fmt:message key="aimir.category"/>";//항목
            fmtMessage[2] = "<fmt:message key="aimir.count"/>";//건수
            fmtMessage[3] = "<fmt:message key="aimir.state"/>";//상태
            return fmtMessage;
        }


        // meterType, tabType, dataType, startDate, EndDate, table_name
        function getCondition(){

            var cnt = 0;
            var condArray = new Array();
            condArray[cnt++] = $('#mvmMiniType').val();// MeterType.EM;
            condArray[cnt++] = tabType;
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = $('#searchType').val();

            return condArray;

        }

        function changeTab(tabIdx){

            var veeSearchCon = document.getElementsByName('VEETab');

            if(VEEType.ValidateCheck == tabIdx){

                veeSearchCon[0].id="current";
                veeSearchCon[1].id="";
                $('#searchcondition').hide();
                
                $('#VEEHistoryMiniDiv').hide();
                $('#VEEValidationCheckMiniDiv').show();

                getVEEValidationCheckMiniGrid();
            }
            else if(VEEType.History == tabIdx){

                veeSearchCon[0].id="";
                veeSearchCon[1].id="current";
                $('#searchcondition').show();
                $('#searchType').selectbox();
                $('#searchType').option('0');
                
                $('#VEEValidationCheckMiniDiv').hide();
                $('#VEEHistoryMiniDiv').show();

                getVEEHistoryMiniGrid();
            }
            else {
                veeSearchCon[0].id="current";
                veeSearchCon[1].id="";
                $('#searchcondition').hide();
            }
            tabType=tabIdx;
        }

        //VEEValidationCheckMini 그리드
        var veeValidationCheckMiniGridStore;
        var veeValidationCheckMiniGridColModel;
        var veeValidationCheckMiniGridOn = false;
        var veeValidationCheckMiniGrid;
     
        function getVEEValidationCheckMiniGrid(){
            var arrayObj     = getCondition();
            var fmtMessage  = getFmtMessage();
            var width = $("#VEEValidationCheckMiniDiv").width(); 

             veeValidationCheckMiniGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/device/getMiniVEEValidationCheckManager.do",
                baseParams:{
                    meterType    : arrayObj[0],
                    tabType      : arrayObj[1],
                    dateType     : arrayObj[2],
                    startDate    : arrayObj[3],
                    endDate      : arrayObj[4],
                    selectData   : arrayObj[5],
                    pageSize     : 10
                },
                totalProperty: 'totalCnt',
                root:'gridData',
                 fields: [
                { name: 'rownum'      , type: 'String'},
                { name: 'item'        , type: 'String'},
                { name: 'count'       , type: 'String'}
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                    page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
            }
            });

            veeValidationCheckMiniGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[0],
                      tooltip: fmtMessage[0],
                       dataIndex: 'rownum', 
                       align:'center', 
                       width: 20,
                       renderer: function(value, me, record, rowNumber, rowIndex, store) {
                       var st = record.store;
                            if (st.lastOptions.params && st.lastOptions.params.start != undefined && 
                            st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return (limit*page) + rowNumber+1;
                         }
                        }
                     }
                     ,{header: fmtMessage[1], 
                       tooltip: fmtMessage[1],
                       dataIndex: 'item', 
                       align:'center', 
                       width: width/3-80 
                     }
                     ,{header: fmtMessage[2], 
                       tooltip: fmtMessage[2],
                       dataIndex: 'count', 
                       align:'center', 
                       width: width/3-80 
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/2)-chromeColAdd
                
                },

            });

            if(veeValidationCheckMiniGridOn){
                veeValidationCheckMiniGrid.destroy();
            }

            veeValidationCheckMiniGrid = new Ext.grid.GridPanel({
                id: 'VEEValidationCheckMiniGrid',
                store: veeValidationCheckMiniGridStore,
                colModel : veeValidationCheckMiniGridColModel,    
                autoScroll: false,
                width: width,
                height: 190,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'VEEValidationCheckMiniDiv',
                viewConfig: {
                    forceFit:true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: veeValidationCheckMiniGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'                
                })
            });

            veeValidationCheckMiniGridOn  = true;
        }


        //VEEHistoryCheckMini 그리드
        var veeHistoryMiniGridStore;
        var veeHistoryMiniGridColModel;
        var veeHistoryMiniGridOn = false;
        var veeHistoryMiniGrid;
     
        function getVEEHistoryMiniGrid(){
            var arrayObj     = getCondition();
            var fmtMessage  = getFmtMessage();
            var width = $("#VEEHistoryMiniDiv").width(); 

             veeHistoryMiniGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/device/getMiniHistoryManager.do",
                baseParams:{
                    meterType    : arrayObj[0],
                    tabType      : arrayObj[1],
                    dateType     : arrayObj[2],
                    startDate    : arrayObj[3],
                    endDate      : arrayObj[4],
                    selectData   : arrayObj[5],
                    pageSize     : 10
                },
                totalProperty: 'totalCnt',
                root:'gridData',
                 fields: [
                { name: 'rownum'      , type: 'String'},
                { name: 'item'        , type: 'String'},
                { name: 'count'       , type: 'String'}
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                    page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
            }
            });

            veeHistoryMiniGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[0],
                      tooltip: fmtMessage[0],
                       dataIndex: 'rownum', 
                       align:'center', 
                       width: 20,
                       renderer: function(value, me, record, rowNumber, rowIndex, store) {
                       var st = record.store;
                            if (st.lastOptions.params && st.lastOptions.params.start != undefined && 
                            st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return (limit*page) + rowNumber+1;
                         }
                        }
                     }
                     ,{header: fmtMessage[1], 
                       tooltip: fmtMessage[1],
                       dataIndex: 'item', 
                       align:'center', 
                       width: width/3-80 
                     }
                     ,{header: fmtMessage[2], 
                       tooltip: fmtMessage[2],
                       dataIndex: 'count', 
                       align:'right', 
                       width: width/3-80 
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/2)-chromeColAdd
                
                },

            });


            if(veeHistoryMiniGridOn){
                veeHistoryMiniGrid.destroy();
            }
            veeHistoryMiniGrid = new Ext.grid.GridPanel({
                id: 'VEEHistoryMiniGrid',
                store: veeHistoryMiniGridStore,
                colModel : veeHistoryMiniGridColModel,    
                autoScroll: false,
                width: width,
                height: 190,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'VEEHistoryMiniDiv',
                viewConfig: {
                    forceFit:true,
                    //scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: veeHistoryMiniGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
            veeHistoryMiniGridOn  = true;
        }

    /*]]>*/
    </script>
</head>
<body>

<input type="hidden" id="mvmMiniType" value="${mvmMiniType}">

    <!--상단탭-->
    <div id="gad_sub_tab">
        <ul>
            <li><a href="javascript:changeTab(VEEType.ValidateCheck)" name="VEETab" id="current"><fmt:message key="aimir.datavalidationcheck"/></a></li>
            <li><a href="javascript:changeTab(VEEType.History)"       name="VEETab" id=""><fmt:message key="aimir.vee.history"/></a></li>
        </ul>
    </div>
    <!--상단탭 끝-->

    <!-- search-background DIV (S) -->
    <div class="data_tap_search">
        <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
     </div> 
        <div class="data_tap_btn">
         <div id="searchcondition" class="floatleft">
            <select id="searchType" style="width:90px;">
            <c:forEach items="${veeTalbeItemList}" var="tableItem" varStatus="idx">
            <c:set var="index" value="${idx.index}"/><option value="<c:out value='${index}'/>" /><c:out value= "${veeTalbeItemList[index]}"/>
            </c:forEach>
            </select>
         </div>
        
         <a href="javascript:;" id="btnSearch" class="btn_blue"><span><fmt:message key="aimir.button.search" /></span></a>
    </div>

      
 
    <!-- search-background DIV (E) -->

    <div class="gadget_body3">
        <div id="VEEValidationCheckMiniDiv"></div>
        <div id="VEEHistoryMiniDiv"></div>
    </div>

</body>
</html>
