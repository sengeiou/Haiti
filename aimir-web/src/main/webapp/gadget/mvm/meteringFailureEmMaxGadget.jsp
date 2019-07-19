<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>검침실패(전기)</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
        <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.Search.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.RowActions.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/src/widgets/grid/GridPanel.js"></script>
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
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs = {hourly:0,daily:1,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    //플렉스객체
    // var flex;

    var fcFailureColumnChartDataXml;
    var fcFailureColumnChart;

    var fcFailurePieChartDataXml;
    var fcFailurePieChart;

    var locationId = "";
    var locationType = "";
    var locationIdforEvent;

    var viewType = 0 ;
    var chromeColAdd = 2;
    //공급사ID
    var supplierId = "";
    // Ondemand 권한
    var ondemandAuth = "${ondemandAuth}";
    //로그인한 사용자정보를 조회한다.

    var supplierName = "";


    $(document).ready(function() {
        Ext.QuickTips.init();

        if (ondemandAuth == "true") {
            $("#groupOndemand").show();
        } else {
            $("#groupOndemand").hide();
        }
       $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;
                getmeteringFailureMaxGrid();
                // getmeteringFailureDetailGrid();
                 $('#detail').hide();
                 $('#MeteringfailureDetailDiv').hide();
                locationTreeGoGo('treeDiv2', 'searchWord', 'locationId');
                updateFailureChart();
                supplierName = json.supplierName;
       			if(json.supplierName=='MOE'){
       				// 공급사가 이라크 MOE인 경우 그룹온디맨드 버튼 숨김
       				$("#groupOndemand").hide();
       			}         
             }
        );
       
    });

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시,조회데이터 변경시 최종적으로 호출하게 된다.
     */
    function send() {
        //개별조회조건 검증 함수 호출
    
        if (viewType == 0) {
            locationId   = "";
            locationType = "";
            getmeteringFailureMaxGrid();
            updateFailureChart();
        } else {
            getmeteringFailureDetailGrid();
        }

    }

    /**
     * 상세조회조건을 숨기기위한 함수
     */
    function hideDetailSearch() {
        viewType = 0;
        $('#fcChartDiv').show();
        $('#showDetailBtn').show();
        $('#detail').hide();
        $('#showChartBtn').hide();
        
        viewChart();
       
    }

    function viewChart(){
        $('#MeteringfailureGridDiv').show();
        $('#MeteringfailureDetailDiv').hide();
    }
    //======================================================================================

    function updateFailureChart(_locationId, _locationType) {
    
        locationId = "";
        locationType = "";

        if (_locationId != null) locationId = _locationId;
        if (_locationType != null) locationType = _locationType;

        $.getJSON('${ctx}/gadget/mvm/getMeteringFailureRateListByLocation.do'
                ,{searchStartDate : $('#searchStartDate').val(),
                  searchEndDate : $('#searchEndDate').val(),
                  meterType : MeterType.EM,
                  locationId : locationId,
                  locationType : locationType,
                  supplierId : supplierId}
                ,function(json) {
                    var list = json.result;
                    fcFailureColumnChartDataXml = "<chart "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='15' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "legendPosition='RIGHT' "
                        + "yAxisMaxValue='100' "
                        + "yAxisMinValue='0' "
                        + "numberSuffix=' %' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_Column3D_nobg
                        + ">";
                    var categories = "<categories>";
                    var dataset = "<dataset seriesName='<fmt:message key="aimir.meteringfailurerate"/>' color='"+fChartColor_Step2[1]+"' >";

                    for (var index = 0; index < list.length; index++) {
                        if (list[index].locationName != null && list[index].locationName != 'undefined') {
                            categories += "<category label='"+list[index].locationName+"' />";
                        }

                        if (list[index].children != null && list[index].children.length > 0) {
                            dataset += "<set value='"+list[index].success.replace(",","")+"' link=\"JavaScript:getChild('"+list[index].locationId+"');\" />";
                        } else {
                            dataset += "<set value='"+list[index].success.replace(",","")+"' />";
                        }

                        if (index == 0) {
                            locationIdforEvent = list[index].locationId;
                        }
                    }
                    categories += "</categories>";
                    dataset += "</dataset>";
                    fcFailureColumnChartDataXml += categories + dataset + "</chart>";

                    /***** Pie Chart ****/
                    fcFailurePieChartDataXml = "<chart "
                                     + "showValues='1' "
                                     + "showPercentValues='1' "
                                     + "showPercentInToolTip='0' "
                                     + "showZeroPies='1' "
                                     + "showLabels='0' "
                                     + "showLegend='1' "
                                     + "legendPosition='RIGHT' "
                                     + "manageLabelOverflow='1' "
                                     + "numberSuffix=' ' "
                                     + fChartStyle_Common
                                     + fChartStyle_Font
                                     + fChartStyle_Pie3D
                                     + ">";

                    var pielabels = "";
                    var cause1 = 0;
                    var cause2 = 0;
                    var etc = 0;
                    
                    if (list != null && list.length > 0) {
                        for (var i = 0; i < list.length; i++) {
                            if (list[i].failureCountByCause1 != null && list[i].failureCountByCause1 != "") {
                                cause1Temp = list[i].failureCountByCause1.replace(",","");
                            	cause1 = cause1 + Number(cause1Temp);
                            }
                            if (list[i].failureCountByCause2 != null && list[i].failureCountByCause2 != "") {
                                cause2 = list[i].failureCountByCause2.replace(",","");
                            }
                            if (list[i].failureCountByEtc != null && list[i].failureCountByEtc != "") {
                                etc = list[i].failureCountByEtc.replace(",","");
                            }
                        }
                    }

                    if (cause1 != 0 || cause2 != 0 || etc != 0) {
                        pielabels = "<set label='<fmt:message key="aimir.failure.cause1"/>' value='" + cause1 + "' />"
                                  + "<set label='<fmt:message key="aimir.failure.cause2"/>' value='" + cause2 + "' />"
                                  + "<set label='<fmt:message key="aimir.etc"/>' value='" + etc + "' />";
                    } else {
                        pielabels = "<set label='' value='1' color='E9E9E9' toolText='<fmt:message key='aimir.data.notexist'/>' />"
                                  + "<set label='<fmt:message key="aimir.failure.cause1"/>' value='0' />"
                                  + "<set label='<fmt:message key="aimir.failure.cause2"/>' value='0' />"
                                  + "<set label='<fmt:message key="aimir.etc"/>' value='0' />";
                    }

                    fcFailurePieChartDataXml += pielabels + "</chart>";

                    /*********/
                    fcChartRender();
                	
                }
        );
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        if (viewType == 0) {
            if ($('#fcFailureColumnChartDiv').is(':visible')) {
            	if ( FusionCharts( "fcFailureColumnChartId" ) ) FusionCharts( "fcFailureColumnChartId" ).dispose();
        		fcChart = new FusionCharts({
            		id: 'fcFailureColumnChartId',
        			type: 'MSColumn3D',
        			renderAt : 'fcFailureColumnChartDiv',
        			width : $('#failureChartDiv').width()-480,
        			height : '300',
        			dataSource : fcFailureColumnChartDataXml
        		}).render();
            }

            if ($('#fcFailurePieChartDiv').is(':visible')) {
            	if ( FusionCharts( "fcFailurePieChartId" ) ) FusionCharts( "fcFailurePieChartId" ).dispose();
        		fcChart = new FusionCharts({
            		id: 'fcFailurePieChartId',
        			type: 'Pie3D',
        			renderAt : 'fcFailurePieChartDiv',
        			width : 440,
        			height : 260,
        			dataSource : fcFailurePieChartDataXml
        		}).render();
            }
        }
    }

    function cmdOnDemandMeteringFailureRecollect(str) {
        //jhkim
        var meterIdStr = str;

        $.getJSON('${ctx}/gadget/device/command/cmdOnDemandMeteringFailureRecollect.do',
                 {searchStartDate : $('#searchStartDate').val() , //searchStartDate
                  searchEndDate : $('#searchEndDate').val() ,//$('#searchEndDate').val() ,
                  searchDateType : $('#searchDateType').val(),//$('#searchDateType').val() ,
                  meterType : MeterType.EM , //MeterType.EM ,
                  supplierId : supplierId , //supplierId
                  meterIdStr : meterIdStr });

        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.onDemand.Metering"/>');
    }

    //report window(Excel)
    var win;
    function openExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();

        obj.condition  = getParams();
        obj.fmtMessage = getFmtMessage();

        if(win)
            win.close();
        win = window.open("${ctx}/gadget/mvm/meteringFailureExcelDownloadPopup.do", "meterginFailureEmExcel", opts);
        
        win.opener.obj = obj;
    }

    /**
     * 메세지를 조회하기위한 함수
     */
    function getFmtMessage() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.alert"/>";                 // 장애
        fmtMessage[1] = "<fmt:message key="aimir.location"/>";              // 지역
        fmtMessage[2] = "<fmt:message key="aimir.meteringfailurerate"/>";   // 검침실패율
        fmtMessage[3] = "<fmt:message key="aimir.mvm.totalCount"/>";        // 전체건수
        fmtMessage[4] = "<fmt:message key="aimir.mvm.failureCount"/>";      // 실패건수
        fmtMessage[5] = "<fmt:message key="aimir.reason"/>";                // 장애유형
        fmtMessage[6] = "<fmt:message key="aimir.list.meteringfailure"/>";  // 검침실패목록
        fmtMessage[7] = "<fmt:message key="aimir.view.detail"/>";           // 상세보기
        fmtMessage[8] = "<fmt:message key="aimir.back"/>";                  // 차트보기
        fmtMessage[9] = "<fmt:message key="aimir.list.meteringfailure"/>";  // 검침실패목록
        fmtMessage[10] = "<fmt:message key="aimir.location"/>";             // 지역
        fmtMessage[11] = "<fmt:message key="aimir.contractNumber"/>";       // 계약번호
        fmtMessage[12] = "<fmt:message key="aimir.meterid"/>";              // 미터번호
        fmtMessage[13] = "<fmt:message key="aimir.mcuid"/>";                // 집중기 아이디
        fmtMessage[14] = "<fmt:message key="aimir.customername"/>";         // 고객명
        fmtMessage[15] = "<fmt:message key="aimir.customeraddress"/>";      // 고객주소
        fmtMessage[16] = "<fmt:message key="aimir.meter.address"/>";        // 미터주소
        fmtMessage[17] = "<fmt:message key="aimir.lastmeteringtime"/>";     // 최종검침시각
        fmtMessage[18] = "<fmt:message key="aimir.meteringdata"/>";         // 검침값
        fmtMessage[19] = "<fmt:message key="aimir.rawdata"/>";              // 로우데이터
        fmtMessage[20] = "<fmt:message key="aimir.execute"/>";              // 실행
        fmtMessage[21] = "<fmt:message key="aimir.select"/>";               // 선택
        fmtMessage[22] = "<fmt:message key="aimir.view.detail"/>";          // 세부정보
        fmtMessage[23] = "<fmt:message key="aimir.modemid"/>";              // 모뎀ID
        fmtMessage[24] = "<fmt:message key="aimir.parent.location"/>";      // 상위지역

        fmtMessage[25] = "<fmt:message key="aimir.metering.NotComm"/>";
        fmtMessage[26] = "<fmt:message key="aimir.metering.CommstateYellow"/>";
        fmtMessage[27] = "<fmt:message key="aimir.metering.MeteringFormatError"/>";
        fmtMessage[28] = "<fmt:message key="aimir.metering.MeterChange"/>";
        fmtMessage[29] = "<fmt:message key="aimir.metering.MeterStatusError"/>";
        fmtMessage[30] = "<fmt:message key="aimir.metering.MeterTimeError"/>";
        fmtMessage[31] = "<fmt:message key="aimir.metering.Success"/>";

        fmtMessage[32] = "<fmt:message key="aimir.excel.meteringFailure"/>";

        fmtMessage[33] = "<fmt:message key="aimir.failure.cause1"/>";       // Failure Cause1
        fmtMessage[34] = "<fmt:message key="aimir.failure.cause2"/>";       // Failure Cause2
        fmtMessage[35] = "<fmt:message key="aimir.etc"/>";                  // Etc

        return fmtMessage;
    }

    /**
     * 조회조건을 조회하기위한 함수
     */
    function getParams() {
        var condArray = new Array();
        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = MeterType.EM;
        condArray[4] = $('#locationId').val();
        condArray[5] = $('#customerId').val();
        condArray[6] = $('#meterId').val();
        condArray[7] = $('#mcuId').val();
        condArray[8] = supplierId;

        return condArray;
    }
        //컬럼 Tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }

    function getParent() {
        $('#locationId').val(locationIdforEvent);
        updateFailureChart(locationIdforEvent, "1");

        locationType="1";
        getmeteringFailureMaxGrid();
    }

    function getChild(_locationId) {
        $('#locationId').val(_locationId);
        updateFailureChart(_locationId, "-1");
        var locationId = $('#locationId').val();

        if(locationId == "" || locationId == null){
            locationType="1";
        }else{
            locationType="-1";
        }

       getmeteringFailureMaxGrid();
    }

    var meteringFailureMaxGridStore;
    var meteringFailureMaxGridColModel;
    var meteringFailureTreeRootNode;
    var meteringFailureMaxGridOn = false;
    var meteringFailureMaxGrid;
    //meteringFailureMax 그리드
    function getmeteringFailureMaxGrid(){
      
        var arrayObj = getParams();
        var width = $("#MeteringfailureGridDiv").width(); 

         meteringFailureMaxGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/mvm/getMeteringFailureRateListByLocation.do",
            baseParams:{

                searchStartDate  : arrayObj[0],
                searchEndDate    : arrayObj[1],
                searchDateType   : arrayObj[2],
                meterType        : arrayObj[3],
                locationId       : arrayObj[4],
                customerId       : arrayObj[5],
                meterId          : arrayObj[6],
                mcuId            : arrayObj[7],
                supplierId       : arrayObj[8],
                locationType     : locationType
            },
           // totalProperty: 'total',
            reader: new Ext.data.JsonReader({
               
                root:'result',
                 fields: [
                { name: 'locationName', type: 'String' },
                { name: 'success', type: 'String' },
                { name: 'totalCount', type: 'String' },
                { name: 'successCount', type: 'String' },
                { name: 'failureCountByCause1', type: 'String' },
                { name: 'failureCountByCause2', type: 'String' },
                { name: 'failureCountByEtc', type: 'String' }
            ]}),
            root:'result',
             fields: [
                { name: 'locationName', type: 'String' },
                { name: 'success', type: 'String' },
                { name: 'totalCount', type: 'String' },
                { name: 'successCount', type: 'String' },
                { name: 'failureCountByCause1', type: 'String' },
                { name: 'failureCountByCause2', type: 'String' },
                { name: 'failureCountByEtc', type: 'String' }
            ],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                          page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
                },
                load: function(store, record, options){
                    makeMeteringFailureGridTree();
                }
            }
        });
        
    };

        // treegrid column tooltip
    function FailureRatePercent(value, values) {

        if (value != null && value != "") {
            value = value + "%";
        }
        return value;
    }

    function makeMeteringFailureGridTree(){

         var message  = getFmtMessage();
         var width = $("#MeteringfailureGridDiv").width(); 
         meteringFailureMaxGridColModel = [

                {
                    header:message[1],
                    tooltip:message[1],
                    dataIndex:'locationName',
                    width: width/7-5,
                    align:'center'
                  
                 }
                 ,{
                    header:message[2],
                    tooltip:message[2],
                    dataIndex:'success',
                    width: width/7-5,
                    align:'right',
                    tpl: new Ext.XTemplate('{success:this.viewToolTip}', {
                        viewToolTip: FailureRatePercent
                    })
                }
                ,{
                    header:message[3],
                    tooltip:message[3],
                    dataIndex:'totalCount',
                    width: width/7-5,
                    align:'right'
                }
                ,{
                    header:message[4],
                    tooltip:message[4],
                    dataIndex:'successCount',
                    width: width/7-5,
                    align:'right'
                }
                ,{
                    header:message[33],
                    tooltip:message[33],
                    dataIndex:'failureCountByCause1',
                    width: width/7-5,
                    align:'right'
                }
                ,{
                    header:message[34],
                    tooltip:message[34],
                    dataIndex:'failureCountByCause2',
                    width: width/7-5,
                    align:'right'
                }
                ,{
                    header:message[35],
                    tooltip:message[35],
                    dataIndex:'failureCountByEtc',
                    width: width/7-5,
                    align:'right'
                }
            ];

            meteringFailureTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'result',
                allowChildren: true,
                draggable:true,
                expended:false,
                children: meteringFailureMaxGridStore.reader.jsonData.result
            });

            if (!meteringFailureMaxGridOn) {
                meteringFailureMaxGrid = new Ext.ux.tree.TreeGrid({
                width: width,
                height: 325,
                useArrows: true,
                renderTo: "MeteringfailureGridDiv",
                store:meteringFailureMaxGridStore,
                enableDD: true,
                columns: meteringFailureMaxGridColModel,
                root: meteringFailureTreeRootNode,
                bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meteringFailureMaxGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
                meteringFailureMaxGrid.on("click", selectMeteringFailDetail);
                meteringFailureMaxGridOn = true;
            } else {
                meteringFailureMaxGrid.setWidth(width);
                meteringFailureMaxGrid.setRootNode(meteringFailureTreeRootNode);
                var bottomToolbar = meteringFailureMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(meteringFailureMaxGridStore);
                meteringFailureMaxGrid.render();
            }
    };
         /**
         * 상세조회조건을 보이기위한 함수
         */
        function selectMeteringFailDetail(node, e){
          
          var locationId    = node.attributes.locationId;
          var locationName  = node.attributes.locationName;
          setLocation(locationId,locationName);
          showDetailSearch();
          
        }
        
        function showDetailSearch() {
            viewType = 1;
            $('#fcChartDiv').hide();
            $('#showDetailBtn').hide();
            $('#detail').show();
            $('#showChartBtn').show();

            viewDetail();
            getmeteringFailureDetailGrid();
        }

        function viewDetail(){
            $('#MeteringfailureGridDiv').hide();
            $('#MeteringfailureDetailDiv').show();
        }

        function setLocation(locationId, locationName) {

            $('#locationId').val(locationId);
            if (locationName != null) {
                $('#searchWord').val(locationName);
            }
        }

        function failureCauseMessage(value,metadata){
            var causemessage;
            var message  = getFmtMessage();
            if (value != null && value != "") {
               switch(value){
                case "0" :
                    causemessage = message[25];
                    break;
                case "1":
                    causemessage = message[26];
                    break;
                case "2" :
                    causemessage = message[27];
                    break;
                case "3" :
                    causemessage = message[28];
                    break;
                case "4" :
                    causemessage = message[29];
                    break;
                case "5" :
                    causemessage = message[30];
                    break;
                case "6" :
                    causemessage = message[31];
                    break;

               }
            }
            return causemessage; 
        }
    
        function getCheckedRow(){
             var checkedArr = meteringFailureDetailCheckSelModel.getSelections();
             var mdsIdStr="";
             console.log("checkedArr",checkedArr);
             for(var i=0;i<checkedArr.length;i++){
               mdsIdStr = mdsIdStr + checkedArr[i].data.mdsId + ":";
             }

             if(mdsIdStr == null || mdsIdStr == ""){
                Ext.Msg.alert("",
                            "Please select items", null, null);
             }else{
            	mdsIdStr = mdsIdStr.slice(0,-1);
                cmdOnDemandMeteringFailureRecollect(mdsIdStr);
             }
        }

        var meteringFailureDetailGridStore;
        var meteringFailureDetailGridColModel;
        var meteringFailureDetailGridOn = false;
        var meteringFailureDetailGrid;
        var meteringFailureDetailCheckSelModel;
        //meteringFailureDetailGrid 
        function getmeteringFailureDetailGrid(){
 
            var arrayObj = getParams();
            var message  = getFmtMessage();
  
            var width = $("#MeteringfailureDetailGridDiv").width(); 

            meteringFailureDetailGridStore = new Ext.data.JsonStore({
               
                autoLoad: {params:{start: 0, limit: 20}},
                url: "${ctx}/gadget/mvm/getMeteringFailureMeter.do",
                baseParams:{
                   searchStartDate: arrayObj[0],
                   searchEndDate  : arrayObj[1],
                   searchDateType : arrayObj[2],
                   meterType      : arrayObj[3],
                   locationId     : arrayObj[4],
                   customerId     : arrayObj[5],
                   meterId        : arrayObj[6],
                   mcuId          : arrayObj[7],
                   supplierId     : arrayObj[8]
                },
                totalProperty: 'total',
                 root:'result',
                 fields: [
                { name: 'customerId'   , type: 'String'},
                { name: 'mcuId'        , type: 'String' },
                { name: 'modemId'      , type: 'String' },
                { name: 'mdsId'        , type: 'String' },
                { name: 'meterAddress' , type: 'String' },
                { name: 'customerName' , type: 'String' },
                { name: 'address'      , type: 'String' },
                { name: 'lastlastReadDate' , type: 'String' },
                { name: 'failureCause'     , type: 'String' }
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              curPage: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
             }
            });
       
            if (meteringFailureDetailGridOn == false) {
                meteringFailureDetailCheckSelModel  = new Ext.grid.CheckboxSelectionModel({
                   singleSelect: false
                });
            }

            if(supplierName=='MOE'){
                meteringFailureDetailGridColModel = new Ext.grid.ColumnModel({               
                    columns: [
                         { header:message[11], tooltip:message[11], dataIndex:'customerId', width: 20, align:'center', hidden: true }  // Contract No.
                        ,{ header:message[13], tooltip:message[13], dataIndex:'mcuId', width: 15, align:'center'}
                        ,{ header:message[23], tooltip:message[23], dataIndex:'modemId', width: 20, align:'center' }
                        ,{ header:message[12], tooltip:message[12], dataIndex:'mdsId', width: 20, align:'center' }
                        ,{ header:message[16], tooltip:message[16], dataIndex:'meterAddress', width: 30, align:'left' }
                        ,{ header:message[14], tooltip:message[14], dataIndex:'customerName', width: 20, align:'center', hidden:true}  // Customer Name
                        ,{ header:message[15], tooltip:message[15], dataIndex:'address',width: 20 , align:'left', hidden:true }        // Customer Address
                        ,{ header:message[17], tooltip:message[17], dataIndex:'lastlastReadDate', width: 20, align:'left' }
                        ,{ header:message[5], tooltip:message[5], dataIndex:'failureCause', width: 30, align:'left', renderer: failureCauseMessage }
                    ],
                    defaults: {
                         sortable: true
                        ,menuDisabled: true
                        ,width: ((width-30)/4)-chromeColAdd
                        // ,renderer: addTooltip
                    }
                });
            }else{
               meteringFailureDetailGridColModel = new Ext.grid.ColumnModel({               
                    columns: [
                          meteringFailureDetailCheckSelModel
                        ,{ header:message[11], tooltip:message[11], dataIndex:'customerId', width: 20, align:'center' }
                        ,{ header:message[13], tooltip:message[13], dataIndex:'mcuId', width: 15, align:'center'}
                        ,{ header:message[23], tooltip:message[23], dataIndex:'modemId', width: 20, align:'center' }
                        ,{ header:message[12], tooltip:message[12], dataIndex:'mdsId', width: 20, align:'center' }
                        ,{ header:message[16], tooltip:message[16], dataIndex:'meterAddress', width: 30, align:'left' }
                        ,{ header:message[14], tooltip:message[14], dataIndex:'customerName', width: 20, align:'center'}
                        ,{ header:message[15], tooltip:message[15], dataIndex:'address',width: 20 , align:'left'  }
                        ,{ header:message[17], tooltip:message[17], dataIndex:'lastlastReadDate', width: 20, align:'left' }
                        ,{ header:message[5], tooltip:message[5], dataIndex:'failureCause', width: 30, align:'left', renderer: failureCauseMessage }
                    ],
                    defaults: {
                         sortable: true
                        ,menuDisabled: true
                        ,width: ((width-30)/4)-chromeColAdd
                        // ,renderer: addTooltip
                    }
                });

            }




            if (meteringFailureDetailGridOn == false) {

                meteringFailureDetailGrid = new Ext.grid.GridPanel({
                   
                    id: 'MeteringfailureDetailMaxGrid',
                    store: meteringFailureDetailGridStore,
                    cm : meteringFailureDetailGridColModel,
                    sm: meteringFailureDetailCheckSelModel,
                    autoScroll: false,
                    width: width,
                    height: 525,               
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'MeteringfailureDetailGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                        scrollOffset: 1,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: meteringFailureDetailGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                meteringFailureDetailGridOn  = true;

            } else {
                
                meteringFailureDetailGrid.setWidth(width);
                meteringFailureDetailGrid.reconfigure(meteringFailureDetailGridStore,
                     meteringFailureDetailGridColModel);
                var bottomToolbar = meteringFailureDetailGrid.getBottomToolbar();                                                             
                bottomToolbar.bindStore(meteringFailureDetailGridStore);
            }
            
        };

    /*]]>*/
    </script>
</head>
<body>

<!-- Buttons - Topright (S) -->
<div class="btn_topright_meterfail">
    <span>
        <div id="detail_btn" class="btn">
            <ul><li id="openExcel"><a href="javascript:openExcelReport();" class="on-bold"><fmt:message key="aimir.button.excel"/></a></li></ul>
            <ul><li id="showDetailBtn" ><a href="javascript:showDetailSearch();" class="on-bold"><fmt:message key="aimir.view.detail"/> </a></li></ul>
            <ul><li id="showChartBtn" style="display:none;"><a href="javascript:hideDetailSearch();" class="on-bold"><fmt:message key="aimir.back"/>    </a></li></ul>
        </div>
    </span>
</div>
<!-- Buttons - Topright (E) -->


<!-- search-background DIV (S) -->
<div class="search-bg-withtabs" style="height:89px;">

    <div class="dayoptions">
    <%@ include file="../commonDateTab.jsp" %>
    </div>
    <div class="dashedline"></div>


    <!--검색조건-->
    <div id="detail" class="searchoption-container">
        <table class="searchoption wfree">
            <tr>
                <td class="withinput"><fmt:message key="aimir.location"/></td>
                <td class="padding-r20px">
                    <input name="searchWord" id='searchWord' style="width:120px" type="text" value='<fmt:message key="aimir.board.location"/>'/>
                    <input type='hidden' id='locationId' value=''></input>
                </td>
                <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                <td class="padding-r20px"><input id="customerId" class="metering-fail-max-contract_no" type="text" /></td>
                <td class="withinput"><fmt:message key="aimir.meterid"/></td>
                <td class="padding-r20px"><input id="meterId" class="metering-fail-max-meter_id" type="text" /></td>
                <td class="withinput"><fmt:message key="aimir.mcuid"/></td>
                <td><input id="mcuId" class="metering-fail-max-equip_no" type="text" /></td>
            </tr>
        </table>
        <div id="treeDiv2Outer" class="tree-billing auto2" style="display:none;">
            <div id="treeDiv2"></div>
        </div>
    </div>
    <!--검색조건 끝-->

</div>
<!-- search-background DIV (E) -->

<div id="failureChartDiv" class="gadget_body margin10px">

    <div id="fcChartDiv">
        <div class="floatleft">
            <label class="bluebold12pt"><fmt:message key="aimir.meteringfailurerate"/></label>
            <div class="high_area"><em class="am_button"><a href="javascript:getParent();"><fmt:message key="aimir.parent.location"/></a></em></div>
            <div id="fcFailureColumnChartDiv">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        <div id="fcChartDiv2" class="floatright">
            <div id="fcFailurePieChartDiv" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
            <div id="failCauseDescr">
                <dt><label class="bluebold11pt"><fmt:message key="aimir.failure.cause1"/> : </label><fmt:message key="aimir.metering.NotComm"/></dt>
                    <dt><label class="bluebold11pt"><fmt:message key="aimir.failure.cause2"/> : </label><fmt:message key="aimir.metering.MeteringFormatError"/></dt>
                    <dt><label class="bluebold11pt"><fmt:message key="aimir.etc"/> : </label>
                    <fmt:message key="aimir.metering.MeterChange"/>,
                    <fmt:message key="aimir.metering.MeterStatusError"/>,
                    <fmt:message key="aimir.metering.MeterTimeError"/></dt>
            </div>
        </div>
    </div>

    <div class="gadget_body">
        <div id="MeteringfailureGridDiv"></div>
        <div id="MeteringfailureDetailDiv">

            <div id="groupOndemand" style="float: right;">
                <table>
                    <tr>
                        <td class="withinput">Group Operation</td>
                        <td>
                            <ul><li>
                                <select id="ondemand" Style="width: 170px;">   
                                        <option value="0" >Group Ondemand</option>
                                </select>
                                <li>
                            </ul>
                        </td>
                        <td class="btn">
                            <ul>
                            <li id="excute">
                              <a href="javascript:getCheckedRow();" class="on-bold">Excute</a>
                            </li>
                            </ul>
                        </td>
                    </tr>
                </table>
            </div>
            <div id="MeteringfailureDetailGridDiv" class="margin-t10px" style="clear:both;"></div>
        </div>
    </div>
</div>

</body>
</html>