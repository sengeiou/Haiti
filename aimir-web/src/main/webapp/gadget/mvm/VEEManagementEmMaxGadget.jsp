<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title><fmt:message key=""/>(<fmt:message key="aimir.energymeter"/>)</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* grid 안에 button 이 있는 경우 높이조정 */
        /* #VEEDetailMaxDataListDiv .x-grid3-cell-inner, #VEEHistoryMaxDiv .x-grid3-cell-inner {
            padding-top: 0px;
            padding-bottom: 0px;
        } */

        .grid-button-height .x-grid3-cell-inner {
            padding-top: 0px;
            padding-bottom: 0px;
        }

        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold !important;
        }
        /* html {
            overflow-y: auto;
        } */
    </style>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //탭초기화
        var tabs = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:1,seasonal:0,yearly:0};
        var tabNames = {};
        var basicRule = "Validation";
        var supplierId = ${supplierId};
        var operatorId = ${operatorId};
        var serviceType = ServiceType.Electricity;
        var tabType = VEEType.ValidateCheck;

        var veeNumberFormat = "${veeNumberFormat}";

        var bVeeParametersNameList = false;
        var bVeeTalbeItemList = false;
        var bVeeRuleList = false;
        var checkedRowArry = [];
        var table;
        var veeAuth = ${veeEditAuth};
        //var veeAuth = "${veeEditAuth}";
        var lpinterval;

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

        Ext.onReady(function() {
            Ext.QuickTips.init();
            extColumnResize();

            /**
             * 유저 세션 정보 가져오기
             */
            $('#autoEstimation_dialog').dialog({
                autoOpen: false,
                close: function(event, ui) {
                    getVEEAutoEstimationGridData(checkedRowArry[0].mvmType,checkedRowArry[0].table,checkedRowArry[0].yyyymmdd,checkedRowArry[0].channel,checkedRowArry[0].mdevType,checkedRowArry[0].mdevId,checkedRowArry[0].dst,supplierId);

                }
            });

            if (veeAuth) {
                $('#detail_btn').show();
                veeHistoryPeriod = true;
            } else {
                veeHistoryPeriod = false;
            }

            $("#veeParametersNameList").selectbox();
            $('#veeTalbeItemList').selectbox();
            $('#veeEditItemList').selectbox();

            changeTab(tabType);
            // Dialog
            $('#autoEstimation_dialog').dialog({
                autoOpen: false,
                resizable: false,
                modal: false
            });
        });

        //resize
        $(window).resize(function() {
            if(!(veeManagementDataMaxGrid === undefined)){
                veeManagementDataMaxGrid.destroy();
            }
            veeManagementDataMaxGridOn  = false;
            getVEEManagementDataMaxGrid();

            if(!(veeDetailDataMaxGrid === undefined)){
                veeDetailDataMaxGrid.destroy();

                veeDetailDataMaxGridOn  = false;

                if(veeDetailDataMaxGridStore === undefined){
                    getVEEDetailDataMaxGrid();
                }else{
                    makeveeDetailDataMaxGrid();                    
                }
            }

            if(!(veeHistoryMaxGrid === undefined)){
                veeHistoryMaxGrid.destroy();
            }
            veeHistoryMaxGridOn  = false;
            getVEEHistoryMaxGrid();

            if(!(veeParameterMaxGrid === undefined)){
                veeParameterMaxGrid.destroy();
            }
            veeParameterMaxGridOn  = false;
            getVEEParameterMaxGrid();
        });

        //메시지
        function getFmtMessage() {
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.writetime"/>";//"로그시각";
            fmtMessage[1] = "<fmt:message key="aimir.buildingMgmt.contractNumber"/>";//"계약번호";
            fmtMessage[2] = "<fmt:message key="aimir.meterid"/>";//"미터번호";
            fmtMessage[3] = "<fmt:message key="aimir.consumptionlocation"/>";//"소비지역";
            fmtMessage[4] = "<fmt:message key="aimir.datatype"/>";//"데이터유형";
            fmtMessage[5] = "<fmt:message key="aimir.attribute"/>";//"속성";
            fmtMessage[6] = "<fmt:message key="aimir.aftervalue"/>";//"이후값";
            fmtMessage[7] = "<fmt:message key="aimir.beforevalue"/>";//"이전값";
            fmtMessage[8] = "<fmt:message key="aimir.meteringdate"/>";//"검침날짜";
            fmtMessage[9] = "<fmt:message key="aimir.operator"/>";//"사용자";
            fmtMessage[10]= "";//"설명";
            fmtMessage[11]= "No";
            fmtMessage[12]= "<fmt:message key="aimir.gmptime"/>";//"발생시각";
            fmtMessage[13]= "<fmt:message key="aimir.address.equipid"/>";//"장비아이디";
            fmtMessage[14]= "<fmt:message key="aimir.contract"/>" + "<fmt:message key="aimir.number"/>";//"계약번호";
            fmtMessage[15]= "<fmt:message key="aimir.checkItem"/>";//"체크항목";
            fmtMessage[16]= "<fmt:message key="aimir.checkDetail"/>";//"체크사항";
            fmtMessage[17]= "<fmt:message key="aimir.category"/>";//"항목";
            fmtMessage[18]= "<fmt:message key="aimir.add.thresholding"/>";//"추가임계치 설정";
            fmtMessage[19]= "<fmt:message key="aimir.condition2"/>";//"조건";
            fmtMessage[20]= "<fmt:message key="aimir.readingDay"/>";//"검침일";
            fmtMessage[21]= "<fmt:message key="aimir.restore"/>";//"복원";
            fmtMessage[22]= "<fmt:message key="aimir.equipment.type"/>";     // 장비 타입

            fmtMessage[23]= "<fmt:message key="aimir.copy.row.error"/>";    // 1개의 행만 복사 가능합니다.;
            fmtMessage[24]= "<fmt:message key="aimir.select.row.no"/>";     // 선택한 행이 없습니다.;
            fmtMessage[25]= "<fmt:message key="aimir.copy.complete"/>";     // 복사 완료
            fmtMessage[26]= "<fmt:message key="aimir.paste.complete"/>";        // 붙여넣기 완료
            fmtMessage[27]= "<fmt:message key="aimir.number"/>";       // 번호
            fmtMessage[28]= "<fmt:message key="aimir.channel"/>";       // 채널
            return fmtMessage;
        }

        //조건
        function getCondition() {

            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = $('#mvmMiniType').val();// MeterType.EM;
            condArray[cnt++] = tabType;
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = $('#veeTalbeItemList').val();
            condArray[cnt++] = $('#contractNo').val();
            condArray[cnt++] = $('#meterId').val();
            condArray[cnt++] = $('#userId').val();
            condArray[cnt++] = $('#veeParametersNameList').val();
            condArray[cnt++] = basicRule;
            condArray[cnt++] = $('#veeEditAuth').val();
            condArray[cnt++] = operatorId;
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#veeEditItemList').val();

            return condArray;
        }

        function changeTab(tabIdx) {

            var veeSearchCon = document.getElementsByName('VEETab');

            $('#meterId').hide();

            if (VEEType.ValidateCheck == tabIdx) {

                veeSearchCon[0].id="current";
                veeSearchCon[1].id="";
                veeSearchCon[2].id="";

                $('#veeTalbeItemList').parents("td").hide();
                $('#veeEditItemList').parents("td").hide();

                $('#tab1_search').show();
                $('#tab3_search').hide();
                $('#sUser').parents("td").hide();

                $('#veeParametersNameList').parents("td").show();

            }
            else if (VEEType.History == tabIdx) {

                veeSearchCon[0].id="";
                veeSearchCon[1].id="current";
                veeSearchCon[2].id="";

                $('#veeParametersNameList').parents("td").hide();

                $('#tab1_search').show();
                $('#tab3_search').hide();
                $('#sUser').parents("td").show();

                $('#veeTalbeItemList').parents("td").show();

                //검색 조건이 LoadProfile이 아닐 경우에 컬럼 hidden
                $('#veeTalbeItemList').change(function(){
                    var index = $("#veeTalbeItemList option").index(
                        $("#veeTalbeItemList option:selected"));

                    if (veeAuth) {
                        if (index == 0) {
                            veeHistoryPeriod = true;
                        } else {
                            veeHistoryPeriod = false;
                        }
                    }
                });

                $('#veeEditItemList').parents("td").show();
            }
            else if (VEEType.Parameters == tabIdx) {

                veeSearchCon[0].id="";
                veeSearchCon[1].id="";
                veeSearchCon[2].id="current";

                $('#veeTalbeItemList').parents("td").hide();
                $('#veeEditItemList').parents("td").hide();
                $('#veeParametersNameList').parents("td").hide();

                $('#tab1_search').hide();
                $('#tab3_search').show();

                $('#sUser').parents("td").hide();

            }
            else {
                veeSearchCon[0].id="current";
                veeSearchCon[1].id="";
                veeSearchCon[2].id="";

                $('#veeTalbeItemList').parents("td").hide();

                $('#tab1_search').show();
                $('#tab3_search').hide();

                $('#sUser').parents("td").hide();

                $('#veeParametersNameList').parents("td").show();
            }

            tabType=tabIdx;
            $('#veeTalbeItemList').option(0);

            if (VEEType.ValidateCheck == tabIdx) {
                $('#tab1').show();
                $('#tab23').hide();
            } else {
                $('#tab1').hide();
                $('#tab23').show();
            }

            send();
        }

        function searchVEEData() {

            if (VEEType.ValidateCheck == tabType) {
                getVEEManagementDataMaxGrid();
            } else if (VEEType.History == tabType) {
                $('#VEEParameterMaxDiv').hide();
                $('#VEEHistoryMaxDiv').show();
                getVEEHistoryMaxGrid();
            } else if (VEEType.Parameters == tabType){
                $('#VEEHistoryMaxDiv').hide();
                $('#VEEParameterMaxDiv').show();
                getVEEParameterMaxGrid();
            }
        };

        //조회 버튼 이벤트
        function send() {
            searchVEEData();
        };

        function viewAutoEstimation(meterType, table, yyyymmdd, channel, mdevType, mdevId, dst) {
            document.getElementById("autoEstimation_view").src="${ctx}/gadget/mvm/VEEManagementAutoEstimation.do?meterType="+meterType+"&item="+table+"&yyyymmdd="+yyyymmdd+"&channel="+channel+"&mdevType="+mdevType+"&mdevId="+mdevId+"&dst="+dst;
            $('#autoEstimation_dialog').dialog('open');
        }

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        //VEEManagementMaxDataListDiv 그리드
        var veeManagementDataMaxGridStore;
        var veeManagementDataMaxGridColModel;
        var veeManagementDataMaxGridOn = false;
        var veeManagementDataMaxGrid;

        function getVEEManagementDataMaxGrid() {
            var arrayObj = getCondition();
            var fmtMessage = getFmtMessage();
            var width = $("#VEEManagementMaxDataListDiv").width();

            veeManagementDataMaxGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: 10}},
                url : "${ctx}/gadget/mvm/getMaxVEEValidationCheckManager.do",
                baseParams : {
                    meterType      : arrayObj[0],
                    tabType        : arrayObj[1],
                    dateType       : arrayObj[2],
                    startDate      : arrayObj[3],
                    endDate        : arrayObj[4],
                    selectData     : arrayObj[5],
                    contractNo     : arrayObj[6],
                    meterId        : arrayObj[7],
                    userId         : arrayObj[8],
                    ParametersName : arrayObj[9],
                    veeRule        : arrayObj[10],
                    mtrAuthority   : arrayObj[11],
                    operatorId     : arrayObj[12],
                    supplierId     : arrayObj[13],
                    editItem       : arrayObj[14],
                    pageSize       : 10
                },
                root : 'gridData',
                totalProperty : 'totalCnt',
                fields : [
                    {name: 'total'     , type: 'Integer'},
                    {name: 'writeTime' , type: 'String'},
                    {name: 'mdevType'  , type: 'String'},
                    {name: 'mdevId'    , type: 'String'},
                    {name: 'contractNo', type: 'String'},
                    {name: 'checkItem' , type: 'String'},
                    {name: 'table'     , type: 'String'},
                    {name: 'channel'   , type: 'String'},
                    {name: 'yyyymmdd'  , type: 'String'},
                    {name: 'dst'       , type: 'String'}
                ],
                listeners : {
                    beforeload : function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                        });
                    }
                }
            });

            var colWidth = (width-40)/5 - chromeColAdd;
            veeManagementDataMaxGridColModel = new Ext.grid.ColumnModel({
                columns : [
                     {header: fmtMessage[27], tooltip: fmtMessage[27], dataIndex: 'total', width: 40 - chromeColAdd,
                         renderer: function(value, me, record, rowNumber, rowIndex, store) {
                             var st = record.store;
                             if (st.lastOptions.params && st.lastOptions.params.start != undefined && 
                                     st.lastOptions.params.limit != undefined) {
                                 var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                                 var limit = st.lastOptions.params.limit;
                                 return Ext.util.Format.number((limit * page) + rowNumber + 1, veeNumberFormat);
                             }
                         }
                     }
                     ,{header: fmtMessage[12], tooltip: fmtMessage[12], dataIndex: 'writeTime'}
                     ,{header: fmtMessage[22], tooltip: fmtMessage[22], dataIndex: 'mdevType'}
                     ,{header: fmtMessage[13], tooltip: fmtMessage[13], dataIndex: 'mdevId'}
                     ,{header: fmtMessage[14], tooltip: fmtMessage[14], dataIndex: 'contractNo'}
                     ,{header: fmtMessage[15], tooltip: fmtMessage[15], dataIndex: 'checkItem', width: colWidth - 4}
                ],
                defaults : {
                     sortable : true
                    ,menuDisabled : true
                    ,width : colWidth
                    ,renderer : addTooltip
                    ,align : 'center'
                }
            });

            if (veeManagementDataMaxGridOn == false) {
                veeManagementDataMaxGrid = new Ext.grid.GridPanel({
                    id : 'VEEManagementMaxDataListGrid',
                    store : veeManagementDataMaxGridStore,
                    colModel : veeManagementDataMaxGridColModel,
                    selModel : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        listeners : {
                            rowselect : function(selectionModel, columnIndex, value) {
                                var param = value.data;
                                table = param.table;
                                checkedRowArry=[];
                                checkedRowArry.push({
                                    mvmType  : $('#mvmMiniType').val(),
                                    table    : param.table,
                                    yyyymmdd : param.yyyymmdd,
                                    channel  : param.channel,
                                    mdevType : param.mdevType,
                                    mdevId   : param.mdevId,
                                    dst      : param.dst
                                });
                                getVEEAutoEstimationGridData(checkedRowArry[0].mvmType,checkedRowArry[0].table,checkedRowArry[0].yyyymmdd,checkedRowArry[0].channel,checkedRowArry[0].mdevType,checkedRowArry[0].mdevId,checkedRowArry[0].dst,supplierId);
                            }
                        }
                    }),
                    autoScroll : false,
                    width : width,
                    height : 290,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'VEEManagementMaxDataListDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    bbar : new Ext.PagingToolbar({
                        pageSize : 10,
                        store : veeManagementDataMaxGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                veeManagementDataMaxGridOn = true;
            } else {
                veeManagementDataMaxGrid.setWidth(width);
                veeManagementDataMaxGrid.reconfigure(veeManagementDataMaxGridStore, veeManagementDataMaxGridColModel);
                var bottomToolbar = veeManagementDataMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(veeManagementDataMaxGridStore);
            }
        }

        //VEE Detail Data 그리드
        var veeDetailDataMaxGridStore;
        var veeDetailDataMaxGridColModel;
        var veeDetailDataMaxGridOn = false;
        var veeDetailDataMaxGrid;
        var veeDetailDataCheckSelModel;
        var veeDetailDataFormat = '000,000,000.000';

        var veeAutoEstimationGridData = [];
        function getVEEAutoEstimationGridData(metertype,table,yyyymmdd,channel,mdevType,mdevId,dst,supplierId) {

             $.getJSON('${ctx}/gadget/mvm/getLpData.do'
                , { meterType     : metertype   || "",
                    table         : table       || "",
                    yyyymmdd      : yyyymmdd    || "",
                    channel       : channel     || "",
                    mdevType      : mdevType    || "",
                    mdevId        : mdevId      || "",
                    dst           : dst         || "",
                    supplierId    : supplierId  || ""}
                , function(json) {
                      lpinterval = json.lpinterval;
                      veeAutoEstimationGridData = json.gridData;
                      veeDetailDataFormat = json.mdFormat;
                      getVEEDetailDataMaxGrid();
                  });
        }

        function getVEEDetailDataMaxGrid() {

            veeDetailDataMaxGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: 24}},
                data : veeAutoEstimationGridData || {},
                root : '',
                fields : ["yyyymmddhh", "channel", "mdev_type", "mdev_id", "dst", "realData", "yyyymmdd", "hh",
                          "value_00", "value_01", "value_02", "value_03", "value_04", "value_05", "value_06", "value_07", "value_08", "value_09",
                          "value_10", "value_11", "value_12", "value_13", "value_14", "value_15", "value_16", "value_17", "value_18", "value_19",
                          "value_20", "value_21", "value_22", "value_23", "value_24", "value_25", "value_26", "value_27", "value_28", "value_29",
                          "value_30", "value_31", "value_32", "value_33", "value_34", "value_35", "value_36", "value_37", "value_38", "value_39",
                          "value_40", "value_41", "value_42", "value_43", "value_44", "value_45", "value_46", "value_47", "value_48", "value_49",
                          "value_50", "value_51", "value_52", "value_53", "value_54", "value_55", "value_56", "value_57", "value_58", "value_59"
                ],
                listeners : {
                    beforeload : function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                        });
                    }
                }
            });

            makeveeDetailDataMaxGrid();
        }

        veeDetailDataCheckSelModel = new Ext.grid.CheckboxSelectionModel({
            singleSelect : true ,//다수 선택 가능.단 다른 컬럼 선택시 해지됨.
            hidden : !veeAuth,
            checkOnly : true,
            listeners : {
                //체크박스 선택시
                rowselect : function(selectionmodel, rowIndex,store) {
                    var checkedList = selectionmodel.getSelections();

                    $("#CopyBtn").bind('click',function(event) {
                        btnCopy(checkedList,rowIndex,store);
                        $("#PasteBtnli").show();
                    });

                    $("#PasteBtn").bind('click',function(event) {
                        btnPaste(checkedList,rowIndex);
                    });
                }
            }
        });

        function makeveeDetailDataMaxGrid() {
            var width = $("#VEEDetailMaxDataListDiv").width();
            var fmtMessage = getFmtMessage();
            var columns = [];
            columns.push(
                {header: fmtMessage[27], tooltip: fmtMessage[27], align: 'center', width: 40 - chromeColAdd, 
                    renderer: function(value, me, record, rowNumber, rowIndex, store) {
                        return Ext.util.Format.number(rowNumber+1, veeNumberFormat);
                    }
                }
               ,veeDetailDataCheckSelModel
               ,{header: "Apply", tooltip: "Apply", hidden : !veeAuth, align: 'center', width: 60 - chromeColAdd, 
                    renderer: function(value, metaData, record, index) {
                        var btnHtml = "<a href='#;' onclick='ApplyClick();' class='btn_blue'><span>Apply</span></a>";
                        var tplBtn = new Ext.Template(btnHtml);
                        return tplBtn.apply();
                    }
                }
               ,{header: fmtMessage[20], tooltip: fmtMessage[20], dataIndex: 'yyyymmddhh', align: 'center', width: 90 - chromeColAdd}
               ,{header: fmtMessage[28], tooltip: fmtMessage[28], dataIndex: 'channel', align: 'right', width: 60 - chromeColAdd}
               ,{header: "dst", tooltip: "dst", dataIndex: 'dst', align: 'right', width: 50 - chromeColAdd}
               ,{header: "realData", tooltip: "realData", dataIndex: 'realData', align: 'center', width: 60 - chromeColAdd - 4}
            );

            var colinterval;
            if (lpinterval == 1) {
                colinterval = 80;
            } else {
                colinterval = (width-382)/(60/lpinterval);
            }

            if (lpinterval != null) {
                for (var i = 0; i < 60; i = i + Number(lpinterval)) {
                    var index = i < 10 ? '0' + i : i;
                    columns.push({
                        header : index + "m",
                        tooltip : index + "m",
                        editor : {
                            id : 'value_'+ index,
                            allowBlank : false,
                            allowNegative : false} ,
                        dataIndex : 'value_' + index,
                        renderer: function(value) {
                            return Ext.util.Format.number(value, veeDetailDataFormat);
                        },
                        align :'right',
                        width : colinterval - chromeColAdd + 5});
                }
            }
            veeDetailDataMaxGridColModel = new Ext.grid.ColumnModel({
                columns : columns,
                defaults : {
                     sortable : true
                    ,menuDisabled : true
                    ,width : ((width-20)/(15))-chromeColAdd
                    ,renderer : addTooltip
                }
            });

            if (veeDetailDataMaxGridOn == false) {
                 veeDetailDataMaxGrid = new Ext.grid.EditorGridPanel({
                    id : 'VEEDetailMaxDataListGrid',
                    store : veeDetailDataMaxGridStore,
                    colModel : veeDetailDataMaxGridColModel,
                    sm : veeDetailDataCheckSelModel,
                    autoScroll : true,
                    width : width,
                    height : 660,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'VEEDetailMaxDataListDiv',
                    viewConfig : {
                    	forceFit:true,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    } ,
                    bbar : new Ext.PagingToolbar({
                        pageSize : 24,
                        store : veeDetailDataMaxGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                veeDetailDataMaxGridOn  = true;
            } else {
                veeDetailDataMaxGrid.setWidth(width);
                veeDetailDataMaxGrid.reconfigure(veeDetailDataMaxGridStore, veeDetailDataMaxGridColModel);
                var bottomToolbar = veeDetailDataMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(veeDetailDataMaxGridStore);
            }
        }

        //VEEHistoryCheckMax 그리드
        var veeHistoryMaxiGridStore;
        var veeHistoryMaxGridColModel;
        var veeHistoryMaxGridOn = false;
        var veeHistoryMaxGrid;
        var veeHistoryPeriod = true;

        function getVEEHistoryMaxGrid() {
            var arrayObj = getCondition();
            var fmtMessage = getFmtMessage();
            var width = $("#VEEHistoryMaxDiv").width();

            veeHistoryMaxGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: 10}},
                url : "${ctx}/gadget/mvm/getMaxVEEHistoryManager.do",
                baseParams : {
                    meterType       : arrayObj[0],
                    tabType         : arrayObj[1],
                    dateType        : arrayObj[2],
                    startDate       : arrayObj[3],
                    endDate         : arrayObj[4],
                    selectData      : arrayObj[5],
                    contractNo      : arrayObj[6],
                    meterId         : arrayObj[7],
                    userId          : arrayObj[8],
                    ParametersName  : arrayObj[9],
                    veeRule         : arrayObj[10],
                    mtrAuthority    : arrayObj[11],
                    operatorId      : arrayObj[12],
                    supplierId      : arrayObj[13],
                    veeEditItemList : arrayObj[14]
                },
                totalProperty : 'totalCnt',
                root : 'gridData',
                fields : [
                    {name : 'col1' , type : 'String'},
                    {name : 'col2' , type : 'String'},
                    {name : 'col4' , type : 'String'},
                    {name : 'col5' , type : 'String'},
                    {name : 'col6' , type : 'String'},
                    {name : 'col7' , type : 'String'},
                    {name : 'col8' , type : 'String'},
                    {name : 'col9' , type : 'String'},
                    {name : 'col10', type : 'String'},
                    {name : 'col11', type : 'String'},
                    {name : 'col12', type : 'String'},
                    {name : 'col13', type : 'String'},
                    {name : 'col14', type : 'String'},
                    {name : 'col15', type : 'String'},
                    {name : 'col17', type : 'String'},
                    {name : 'col21', type : 'String'},
                    {name : 'col22', type : 'String'},
                    {name : 'col23', type : 'String'},
                    {name : 'col24', type : 'String'}
                ],
                listeners : {
                    beforeload : function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }
            });

            var colWidth = 0;

            if (veeHistoryPeriod) {
                colWidth = (width-65)/9 - chromeColAdd;
                $("#VEEHistoryMaxDiv").addClass("grid-button-height");
            } else {
                colWidth = width/9 - chromeColAdd;
                $("#VEEHistoryMaxDiv").removeClass("grid-button-height");
            }

            var columns = new Array();
            columns.push({header: fmtMessage[0], tooltip: fmtMessage[0], dataIndex: 'col1'});

            if (veeHistoryPeriod) {
                columns.push({header: fmtMessage[21], tooltip: fmtMessage[21], width:65 - chromeColAdd, 
                    renderer: function(value, metaData, record, index) {
                        var btnHtml = "<a href='#;' onclick='RestoreClick(\"{col6}\", \"{col7}\",\"{col8}\",\"{col13}\",\"{col14}\", \"{col15}\",\"{col17}\", \"{col21}\",\"{col22}\", \"{col23}\",\"{col24}\");' class='btn_blue'><span><fmt:message key="aimir.restore"/></span></a>";
                        var tplBtn = new Ext.Template(btnHtml);
                        return tplBtn.apply({col6: record.get('col6'),col7: record.get('col7'),col8: record.get('col8'), col13: record.get('col13'),col14: record.get('col14'),col15: record.get('col15'),col17: record.get('col17'),col21: record.get('col21'),col22: record.get('col22'),col23: record.get('col23'),col24: record.get('col24')});
                    }
                });
            }

            columns.push({header: fmtMessage[1], tooltip: fmtMessage[1], dataIndex: 'col2'});
            columns.push({header: fmtMessage[3], tooltip: fmtMessage[3], dataIndex: 'col4'});
            columns.push({header: fmtMessage[4], tooltip: fmtMessage[4], dataIndex: 'col5'});
            columns.push({header: fmtMessage[5], tooltip: fmtMessage[5], dataIndex: 'col6'});
            columns.push({header: fmtMessage[6], tooltip: fmtMessage[6], dataIndex: 'col7', align:'right'});
            columns.push({header: fmtMessage[7], tooltip: fmtMessage[7], dataIndex: 'col8', align:'right'});
            columns.push({header: fmtMessage[8], tooltip: fmtMessage[8], dataIndex: 'col9'});
            columns.push({header: fmtMessage[9], tooltip: fmtMessage[9], dataIndex: 'col10', width: colWidth - 4});
            
            veeHistoryMaxGridColModel = new Ext.grid.ColumnModel({
                columns : columns,
                defaults : {
                     sortable : true
                    ,menuDisabled : true
                    ,width : colWidth
                    ,align : 'center'
                }
            });

            if (veeHistoryMaxGridOn == false) {
                veeHistoryMaxGrid = new Ext.grid.GridPanel({
                    store : veeHistoryMaxGridStore,
                    colModel : veeHistoryMaxGridColModel,
                    autoScroll : false,
                    width : width,
                    height : 306,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'VEEHistoryMaxDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    } ,
                    bbar : new Ext.PagingToolbar({
                        pageSize : 10,
                        store : veeHistoryMaxGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                veeHistoryMaxGridOn = true;
            } else {
                veeHistoryMaxGrid.setWidth(width);
                veeHistoryMaxGrid.reconfigure(veeHistoryMaxGridStore, veeHistoryMaxGridColModel);
                var bottomToolbar = veeHistoryMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(veeHistoryMaxGridStore);
            }
        }

        function RestoreClick(col6,col7,col8,col13,col14,col15,col17,col21,col22,col23,col24) {
            var arrayObj = getCondition();

            var mtrAuthority = $('#veeEditAuth').val();

            if (mtrAuthority.toString() != "true") {
                return;
            }

            var paramArray = new Array();

            paramArray[0] = col6;
            paramArray[1] = col7;
            paramArray[2] = col8;
            paramArray[3] = col22;

            $.getJSON('${ctx}/gadget/mvm/VEEManagementUpdateData.do',
                {
                    meterType : arrayObj[0],
                    userId    : arrayObj[12],
                    supplierId: supplierId,
                    yyyymmddhh: col23,
                    yyyymmdd  : col17,
                    hh        : col24,
                    channel   : col21,
                    mdevType  : col13,
                    mdevId    : col14,
                    dst       : col15,
                    params    : paramArray
                },function(json) {

                    if(json.result == "SUCCESS"){
                         Ext.Msg.alert("", "Process has been completed.");
                    }else if(json.result == "FAIL"){
                         Ext.Msg.alert("", json.result);
                    }
                }
            );
        }

        //VEEParameterMax 그리드
        var veeParameterMaxGridStore;
        var veeParameterMaxGridColModel;
        var veeParameterMaxGridOn = false;
        var veeParameterMaxGrid;

        function getVEEParameterMaxGrid() {

            var arrayObj = getCondition();
            var fmtMessage = getFmtMessage();
            var width = $("#VEEParameterMaxDiv").width();

            veeParameterMaxGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: 10}},
                url : "${ctx}/gadget/mvm/getMaxVEEParametersManager.do",
                baseParams : {
                    veeRule : arrayObj[10],
                    pageSize : 10
                },
                root : 'gridData',
                fields : [
                    {name : 'chked'       , type : 'Integer'},
                    {name : 'localName'   , type : 'String'},
                    {name : 'item'        , type : 'String'},
                    {name : 'useThreshold', type : 'String'},
                    {name : 'condition'   , type : 'String'}
                ],
                listeners : {
                    beforeload : function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                        });
                    }
                }
            });

            veeParameterMaxGridColModel = new Ext.grid.ColumnModel({
                columns : [
                     {header: fmtMessage[27],
                       tooltip: fmtMessage[27],
                       dataIndex: 'chked',
                       align:'center',
                       width: width/5-150,
                       renderer: function(value, me, record, rowNumber, rowIndex, store) {
                       var st = record.store;
                            if (st.lastOptions.params && st.lastOptions.params.start != undefined &&
                            st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return Ext.util.Format.number((limit*page) + rowNumber+1, veeNumberFormat);
                         }
                        }
                     }
                     ,{header: fmtMessage[16],
                       tooltip: fmtMessage[16],
                       dataIndex: 'localName',
                       align:'center',
                       width:width/5
                     }
                     ,{header: fmtMessage[17],
                       tooltip: fmtMessage[17],
                       dataIndex: 'item',
                       align:'center',
                       width: width/5-100
                     }
                     ,{header: fmtMessage[18],
                       tooltip: fmtMessage[18],
                       dataIndex: 'useThreshold',
                       align:'center',
                       width: width/5
                     }
                     ,{header: fmtMessage[19],
                       tooltip: fmtMessage[19],
                       dataIndex: 'condition',
                       align:'center',
                       width: width/5+200
                     }
                ],
                defaults : {
                     sortable : true
                    ,menuDisabled : true
                    ,width : ((width-10)/5)-chromeColAdd
                    ,renderer : addTooltip
                },
            });

            if (veeParameterMaxGridOn == false) {
                veeParameterMaxGrid = new Ext.grid.GridPanel({
                    id : 'VEEParameterMaxGrid',
                    store : veeParameterMaxGridStore,
                    colModel : veeParameterMaxGridColModel,
                    autoScroll : false,
                    width : width,
                    height : 290,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'VEEParameterMaxDiv',
                    viewConfig : {
                        forceFit : true,
                        scrollOffset : 1,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    } ,
                    bbar : new Ext.PagingToolbar({
                        pageSize : 10,
                        store : veeParameterMaxGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                veeParameterMaxGridOn = true;
            } else {
                veeParameterMaxGrid.setWidth(width);
                veeParameterMaxGrid.reconfigure(veeParameterMaxGridStore, veeParameterMaxGridColModel);
                var bottomToolbar = veeParameterMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(veeParameterMaxGridStore);
            }
        }

        function dataAutoEstimation() {
            var checkedArr = veeDetailDataCheckSelModel.getSelections();
            if (checkedArr.length > 0) {
                if (checkedArr[0].get("mdev_id") != null && checkedArr[0].get("mdev_id") != "") {
                    /*viewAutoEstimation($('#mvmMiniType').val(),table,checkedArr[0].get("yyyymmdd"),checkedArr[0].get("channel"),checkedArr[0].get("mdev_type"),checkedArr[0].get("mdev_id"),checkedArr[0].get("dst"));*/
                    viewAutoEstimation(checkedRowArry[0].mvmType,checkedRowArry[0].table,checkedRowArry[0].yyyymmdd,checkedRowArry[0].channel,checkedRowArry[0].mdevType,checkedRowArry[0].mdevId,checkedRowArry[0].dst);
                }
            } else {
                Ext.Msg.alert("", "<fmt:message key="aimir.select.row.no"/>");
                return;
            }
        }

        var veeDetailData;
        function btnCopy(checkedArr, rowIndex, store) {

            if (checkedArr.length > 0) {
                if (checkedArr.length == 1) {
                    if (checkedArr[0].get("mdev_id") != null && checkedArr[0].get("mdev_id") != "") {
                        veeDetailData = [];
                        veeDetailData.push({
/*                        yyyymmddhh : checkedArr[0].get("yyyymmddhh"),
                        channel : checkedArr[0].get("channel"),
                        mdev_type : checkedArr[0].get("mdev_type"),
                        mdev_id : checkedArr[0].get("mdev_id"),
                        dst : checkedArr[0].get("dst"),
                        yyyymmdd : checkedArr[0].get("yyyymmdd"),
                        hh : checkedArr[0].get("hh"),*/
                            value_00 : checkedArr[0].get("value_00"),
                            value_01 : checkedArr[0].get("value_01"),
                            value_02 : checkedArr[0].get("value_02"),
                            value_03 : checkedArr[0].get("value_03"),
                            value_04 : checkedArr[0].get("value_04"),
                            value_05 : checkedArr[0].get("value_05"),
                            value_06 : checkedArr[0].get("value_06"),
                            value_07 : checkedArr[0].get("value_07"),
                            value_08 : checkedArr[0].get("value_08"),
                            value_09 : checkedArr[0].get("value_09"),
                            value_10 : checkedArr[0].get("value_10"),
                            value_11 : checkedArr[0].get("value_11"),
                            value_12 : checkedArr[0].get("value_12"),
                            value_13 : checkedArr[0].get("value_13"),
                            value_14 : checkedArr[0].get("value_14"),
                            value_15 : checkedArr[0].get("value_15"),
                            value_16 : checkedArr[0].get("value_16"),
                            value_17 : checkedArr[0].get("value_17"),
                            value_18 : checkedArr[0].get("value_18"),
                            value_19 : checkedArr[0].get("value_19"),
                            value_20 : checkedArr[0].get("value_20"),
                            value_21 : checkedArr[0].get("value_21"),
                            value_22 : checkedArr[0].get("value_22"),
                            value_23 : checkedArr[0].get("value_23"),
                            value_24 : checkedArr[0].get("value_24"),
                            value_25 : checkedArr[0].get("value_25"),
                            value_26 : checkedArr[0].get("value_26"),
                            value_27 : checkedArr[0].get("value_27"),
                            value_28 : checkedArr[0].get("value_28"),
                            value_29 : checkedArr[0].get("value_29"),
                            value_30 : checkedArr[0].get("value_30"),
                            value_31 : checkedArr[0].get("value_31"),
                            value_32 : checkedArr[0].get("value_32"),
                            value_33 : checkedArr[0].get("value_33"),
                            value_34 : checkedArr[0].get("value_34"),
                            value_35 : checkedArr[0].get("value_35"),
                            value_36 : checkedArr[0].get("value_36"),
                            value_37 : checkedArr[0].get("value_37"),
                            value_38 : checkedArr[0].get("value_38"),
                            value_39 : checkedArr[0].get("value_39"),
                            value_40 : checkedArr[0].get("value_40"),
                            value_41 : checkedArr[0].get("value_41"),
                            value_42 : checkedArr[0].get("value_42"),
                            value_43 : checkedArr[0].get("value_43"),
                            value_44 : checkedArr[0].get("value_44"),
                            value_45 : checkedArr[0].get("value_45"),
                            value_46 : checkedArr[0].get("value_46"),
                            value_47 : checkedArr[0].get("value_47"),
                            value_48 : checkedArr[0].get("value_48"),
                            value_49 : checkedArr[0].get("value_49"),
                            value_50 : checkedArr[0].get("value_50"),
                            value_51 : checkedArr[0].get("value_51"),
                            value_52 : checkedArr[0].get("value_52"),
                            value_53 : checkedArr[0].get("value_53"),
                            value_54 : checkedArr[0].get("value_54"),
                            value_55 : checkedArr[0].get("value_55"),
                            value_56 : checkedArr[0].get("value_56"),
                            value_57 : checkedArr[0].get("value_57"),
                            value_58 : checkedArr[0].get("value_58"),
                            value_59 : checkedArr[0].get("value_59")
                        });
                    }

                    Ext.Msg.alert("", "<fmt:message key="aimir.copy.complete"/>");
                } else {
                    Ext.Msg.alert("", "<fmt:message key="aimir.copy.row.error"/>");
                    return;
                }

                //1.5초의 시간 지연.
                setTimeout(function() {
                     veeDetailDataCheckSelModel.clearSelections();//전체 체크 해지
                }, 1500);

            } else {
                Ext.Msg.alert("", "<fmt:message key="aimir.select.row.no"/>");
                return;
            }
        }

        function btnPaste(checkedArr, rowIndex) {

            var store = veeDetailDataMaxGrid.getStore();
            var Plant = store.recordType;
            var checkData = checkedArr[0].data;

            if (checkedArr.length > 0) {
                if (checkedArr.length == 1) {

                    var p = new Plant({
                        //기존의 값.
                        yyyymmddhh : checkData.yyyymmddhh,
                        channel    : checkData.channel,
                        mdev_type  : checkData.mdev_type,
                        mdev_id    : checkData.mdev_id,
                        dst        : checkData.dst,
                        yyyymmdd   : checkData.yyyymmdd ,
                        hh         : checkData.hh ,
                        realData   : checkData.realData ,
                        interval   : checkData.interval ,
                        //새로운 데이터값.
                        value_00   : veeDetailData[0].value_00 ,
                        value_01   : veeDetailData[0].value_01 ,
                        value_02   : veeDetailData[0].value_02 ,
                        value_03   : veeDetailData[0].value_03 ,
                        value_04   : veeDetailData[0].value_04 ,
                        value_05   : veeDetailData[0].value_05 ,
                        value_06   : veeDetailData[0].value_06 ,
                        value_07   : veeDetailData[0].value_07 ,
                        value_08   : veeDetailData[0].value_08 ,
                        value_09   : veeDetailData[0].value_09 ,
                        value_10   : veeDetailData[0].value_10 ,
                        value_11   : veeDetailData[0].value_11 ,
                        value_12   : veeDetailData[0].value_12 ,
                        value_13   : veeDetailData[0].value_13 ,
                        value_14   : veeDetailData[0].value_14 ,
                        value_15   : veeDetailData[0].value_15 ,
                        value_16   : veeDetailData[0].value_16 ,
                        value_17   : veeDetailData[0].value_17 ,
                        value_18   : veeDetailData[0].value_18 ,
                        value_19   : veeDetailData[0].value_19 ,
                        value_20   : veeDetailData[0].value_20 ,
                        value_21   : veeDetailData[0].value_21 ,
                        value_22   : veeDetailData[0].value_22 ,
                        value_23   : veeDetailData[0].value_23 ,
                        value_24   : veeDetailData[0].value_24 ,
                        value_25   : veeDetailData[0].value_25 ,
                        value_26   : veeDetailData[0].value_26 ,
                        value_27   : veeDetailData[0].value_27 ,
                        value_28   : veeDetailData[0].value_28 ,
                        value_29   : veeDetailData[0].value_29 ,
                        value_30   : veeDetailData[0].value_30 ,
                        value_31   : veeDetailData[0].value_31 ,
                        value_32   : veeDetailData[0].value_32 ,
                        value_33   : veeDetailData[0].value_33 ,
                        value_34   : veeDetailData[0].value_34 ,
                        value_35   : veeDetailData[0].value_35 ,
                        value_36   : veeDetailData[0].value_36 ,
                        value_37   : veeDetailData[0].value_37 ,
                        value_38   : veeDetailData[0].value_38 ,
                        value_39   : veeDetailData[0].value_39 ,
                        value_40   : veeDetailData[0].value_40 ,
                        value_41   : veeDetailData[0].value_41 ,
                        value_42   : veeDetailData[0].value_42 ,
                        value_43   : veeDetailData[0].value_43 ,
                        value_44   : veeDetailData[0].value_44 ,
                        value_45   : veeDetailData[0].value_45 ,
                        value_46   : veeDetailData[0].value_46 ,
                        value_47   : veeDetailData[0].value_47 ,
                        value_48   : veeDetailData[0].value_48 ,
                        value_49   : veeDetailData[0].value_49 ,
                        value_50   : veeDetailData[0].value_50 ,
                        value_51   : veeDetailData[0].value_51 ,
                        value_52   : veeDetailData[0].value_52 ,
                        value_53   : veeDetailData[0].value_53 ,
                        value_54   : veeDetailData[0].value_54 ,
                        value_55   : veeDetailData[0].value_55 ,
                        value_56   : veeDetailData[0].value_56 ,
                        value_57   : veeDetailData[0].value_57 ,
                        value_58   : veeDetailData[0].value_58 ,
                        value_59   : veeDetailData[0].value_59
                    });
                    //데이터 복사.
                    veeDetailDataMaxGridStore.removeAt(rowIndex);
                    veeDetailDataMaxGridStore.insert(rowIndex, p);
                    // saveDetailMeteringData();

                    Ext.Msg.alert("", "<fmt:message key="aimir.paste.complete"/>");
                } else {
                    Ext.Msg.alert("", "<fmt:message key="aimir.copy.row.error"/>");
                    return;
                }

                 //1.5초의 시간 지연.
                setTimeout(function(){
                    veeDetailDataCheckSelModel.clearSelections();//전체 체크 해지
                },1500);

            } else {
                Ext.Msg.alert("", "<fmt:message key="aimir.select.row.no"/>");
                return;
            }
        }

        function ApplyClick() {

            var changeRecords = veeDetailDataMaxGridStore.getModifiedRecords();
            var recordlen = changeRecords.length;

            var data = changeRecords[recordlen-1].data;
            var arrayObj = getCondition();
            var paramArray = new Array();

            for (var i = 0; i < 60; i++) {
                var index = i<10?'0'+i:i;
                paramArray[i] = eval("data.value_"+index);
            }
            paramArray[60] = data.realData;

            $.getJSON('${ctx}/gadget/mvm/VEEManagementUpdateData.do',
                {
                    meterType : arrayObj[0],
                    userId    : arrayObj[12],
                    supplierId: supplierId,
                    yyyymmddhh: data.yyyymmddhh,
                    yyyymmdd  : data.yyyymmdd,
                    hh        : data.hh,
                    channel   : data.channel,
                    mdevType  : data.mdev_type,
                    mdevId    : data.mdev_id,
                    dst       : data.dst,
                    params    : paramArray
                },function(json) {

                    if (json.result == "SUCCESS") {
                         Ext.Msg.alert("", "Process has been completed.",
                            function () {
                                /*getVEEAutoEstimationGridData(arrayObj[0],table,data.yyyymmdd,data.channel,data.mdev_type,data.mdev_id,data.dst,supplierId);*/
                              getVEEAutoEstimationGridData(checkedRowArry[0].mvmType,checkedRowArry[0].table,checkedRowArry[0].yyyymmdd,checkedRowArry[0].channel,checkedRowArry[0].mdevType,checkedRowArry[0].mdevId,checkedRowArry[0].dst,supplierId);
                            });

                    } else if (json.result == "FAIL") {
                         Ext.Msg.alert("", json.result);
                    }
                }
            );

        }
    /*]]>*/
    </script>
</head>
<body>

<input type="hidden" id="veeEditAuth" value="${veeEditAuth}">
<input type="hidden" id="mvmMiniType" value="${mvmMiniType}">

    <!--상단탭-->
    <div id="gad_sub_tab" >
        <ul>
            <li><a href="javascript:changeTab(VEEType.ValidateCheck)" name="VEETab" id="current"><fmt:message key="aimir.datavalidationcheck"/></a></li>
            <li><a href="javascript:changeTab(VEEType.History)"  name="VEETab" id=""><fmt:message key="aimir.vee.history"/></a></li>
            <li><a href="javascript:changeTab(VEEType.Parameters)"  name="VEETab" id=""><fmt:message key="aimir.vee.ruleset"/></a></li>
        </ul>
    </div>
    <!--상단탭 끝-->




    <div id="tab1_search">


        <!-- search-background DIV (S) -->
        <div class="search-bg-withouttabs with-dayoptions-bt">
            <div class="dayoptions-bt">
            <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
            </div>
            <div class="dashedline"><ul><li></li></ul></div>


            <div class="searchoption-container">
                <table class="searchoption wfree">
                    <tr>
                        <td class="hidden"><input id="fromDate" type="hidden" class="day"></input></td>
                        <td class="hidden"><input id="toDate" type="hidden" class="day"></input></td>
                        <td>
                            <select id="veeTalbeItemList" style="width:120px;">
                            <c:forEach items="${veeTalbeItemList}" var="tableItem" varStatus="idx">
                            <c:set var="index" value="${idx.index}"/>
                            <option value="<c:out value='${veeTalbeItemList[index]}'/>"><c:out value='${veeTalbeItemList[index]}'/></option>
                            <c:out value= "${veeTalbeItemList[index]}"/>
                            </c:forEach>
                            </select>
                        </td>
                        <td>
                            <select id="veeParametersNameList" style="width:160px;">
                            <c:forEach items="${veeParametersNameList}" var="parameter">
                            <option value="<c:out value='${parameter.value}'/>" ><c:out value='${parameter.name}'/></option>
                            <c:out value= "${parameter.name}"/>
                            </c:forEach>
                            </select>
                        </td>
                        <td class="space20"></td>
                        <td>
                            <select id="veeEditItemList" style="width:160px;">
                            <c:forEach items="${edititems}" var="edititems">
                                <option value="${edititems.code}" >${edititems.name}</option>
                            </c:forEach>
                            </select>
                        </td>
                        <td class="space20"></td>
                        <td id="label-gen2" class="gray11pt withinput"><fmt:message key="aimir.contract"/> <fmt:message key="aimir.number"/></td>
                        <td><input id="contractNo" name="contractNo" type="text" class="day" style="width:120px"></input></td>
                        <td class="space20"></td>
                        <!-- <td id="label-gen2" class="gray11pt withinput"><fmt:message key="aimir.meter"/> <fmt:message key="aimir.id"/></td> -->
                        <td><input id="meterId" name="meterId" type="text" class="day" style="width:120px"></input></td>
                        <td class="space20"></td>
                        <td id="sUser">
                            <span id="label-gen2" class="gray11pt withinput"><fmt:message key="aimir.user.id"/></span>
                            <span><input id="userId" name="userId" type="text" class="day" style="width:120px"></input></span>
                        </td>
                       <td><em class="am_button"><a href="javascript:;" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></em></td>
                    </tr>
                </table>
            </div>

        </div>
        <!-- search-background DIV (E) -->

    </div>


    <div id="tab3_search">

    </div>

    <div id="tab1" class="gadget_body">
        <div id="VEEManagementMaxDataListDiv"></div>
        <div style="float: right; margin-right: 3px; margin-bottom: 3px;margin-top: 3px;">
            <span>
            <div id="detail_btn" class="btn" style="display:none">
            <ul><li id="dataAutoEstimationBtn"><a href="javascript:dataAutoEstimation();" class="on-bold">AutoEstimation</a></li></ul>
            <ul><li id ="CopyBtnli"><a href="#" id="CopyBtn" class="on-bold" >Copy</a></li></ul>
            <ul><li id ="PasteBtnli" style="display:none"><a href="#" id="PasteBtn"class="on-bold" >Paste</a></li></ul>
            </div>
            </span>
        </div>
        <div id="VEEDetailMaxDataListDiv" class="clear both grid-button-height"></div>
    </div>

    <!-- <div id="tab23" class="gadget_body"> -->
    <div id="tab23" style="padding:8px 10px;">
    <!-- <div id="tab23"> -->
        <!-- <div id="VEEHistoryMaxDiv" class="grid-button-height" style="margin: 8px 10px;"></div> -->
        <div id="VEEHistoryMaxDiv"></div>
        
        
        <!-- <div style="margin: 8px 10px 8px 10px;">
            <div id="VEEHistoryMaxDiv"></div>
        </div> -->
        
        
        
        
        
        
        <div id="VEEParameterMaxDiv"></div>
    </div>

<div id="autoEstimation_dialog" class="mvm-popwin-iframe-outer" title="Auto Estimation">
    <iframe id="autoEstimation_view" src="" frameborder="0" class="mvm-popwin-iframe" style="height:700px" marginwidth="0" marginheight="0" scrolling="no"></iframe>
</div>
</body>
</html>
