<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.aimir.service.mvm.DataGapsManager"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta http-equiv="PRAGMA" content="NO-CACHE"/>
<meta http-equiv="Expires" content="-1"/>

    <title>Data Gaps(${meterTypeCode2})</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css"/>
    <!-- 스타일 추가 extjs css -->
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
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    var fcChartDataXml;
    var fcChart;

    //플렉스객체
    var flex;

    //공급사ID
    var supplierId="${sesSupplierId}";
    //로그인한 사용자정보를 조회한다.
    var browserWidth= "";
    var rowSize = 100;
    // Ondemand 권한
    var ondemandAuth = "${ondemandAuth}";
    
    var initSupplier = function(callback) {
        if ( !supplierId ) {
            $.getJSON("${ctx}/common/getUserInfo.do",
                function(json) {
                    supplierId = json.supplierId;
                    if ( callback ) {
                        callback();
                }
            });
        } else {
            callback();
        }
    };

    $(document).ready(function(){

        initSupplier(function() {
            if (ondemandAuth == "true") {
                $("#chartBtn").show();
            } else {
                $("#chartBtn").hide();
            }

            updateFChart();
            flex = getFlexObject('dataGapsMiniChart');

            browserWidth= $(window).width();

            //미터 카운트 호출.
            getMeterCountOnLoad();

            //그리드 차트 호출.
            getdataGapsMiniChartGrid();
        });
    });

    //윈도우 리싸이즈시 event
    $(window).resize(function() {

        browserWidth= $(window).width();   // returns width of browser viewport
        //alert(browserWidth);

        //리싸이즈시 패널 인스턴스 kill & reload
        dataGapsMiniChartGridPanel.destroy();

        //dataGapsMaxChartGridPanel;
        dataGapsMiniChartGridInstanceOn = false;

        getdataGapsMiniChartGrid();
    });

    // meterCnt fetch from s.s and set value to dom
    function getMeterCountOnLoad() {
        var condArray = new Array();

        condArray= getParams();

        $.ajax({
            type:"POST",
            data:{
                 searchStartDate:condArray[0]
                ,searchEndDate:condArray[1]
                ,searchDateType:condArray[2]
                ,meterType:condArray[3]
                ,supplierId :condArray[4]
                ,'pageSize':rowSize
                ,'page':"1"
            },
            dataType:"json",
            url:"${ctx}/gadget/mvm/getDataGaps2.do",
            success:function(data, status) {
                var allMissingMeterCount= data.allMissingMeterCount;
                var patialMissingMeterCount= data.patialMissingMeterCount;
                var totalMeterCount= data.totalMeterCount;

                $("#meterCnt1").text(totalMeterCount);
                $("#meterCnt2").text(patialMissingMeterCount);
                $("#meterCnt3").text(allMissingMeterCount);
            },
            error:function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"meterCnt fetch  ajax comm failed");
            }
        });
    }// End of getMeterCountOnLoad()

    //################################
    //#######dataGapsMiniChart Start
    //################################

    //dataGapsMiniChartGrid propeties
    var dataGapsMiniChartGridInstanceOn = false;
    var dataGapsMiniChartGrid;
    var dataGapsMiniChartColModel;
    var dataGapsMiniChartCheckSelModel;
    var condArray = new Array();

    function getdataGapsMiniChartGrid() {

        //setting grid panel width
        var gridWidth = (browserWidth-22);

        condArray= getParams();

        //### dataGapsMiniChartGrid Store fetch
        var dataGapsMiniChartGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize }},
            url: "${ctx}/gadget/mvm/getDataGaps2.do",
            method : 'POST',
            baseParams: {
                 searchStartDate:condArray[0]
                ,searchEndDate:condArray[1]
                ,searchDateType:condArray[2]
                ,meterType:condArray[3]
                ,supplierId :condArray[4]
            },
            //Total Cnt
            totalProperty: "totalMeterCount",
            root:'dataGapsList',
            fields: [
                      "no"
                     , "yyyymmdd"
                     , "hh"
                     , "missingCount"
            ],
            listeners: {
                beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                }
            }
        });//Store End

        var fmtMessage = getFmtMessage();

        // dataGapsMiniChartGrid Model DEfine
        dataGapsMiniChartGridModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[3], dataIndex: 'no', width:(gridWidth)/4,resizable: false}
               ,{header: fmtMessage[4], dataIndex: 'yyyymmdd', width:(gridWidth)/4,resizable: false}
               ,{header: fmtMessage[5], dataIndex: 'hh', width:(gridWidth)/4,resizable: false}
               ,{header: fmtMessage[6], dataIndex: 'missingCount', width:(gridWidth)/4-25,resizable: false, align:'right'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        if (dataGapsMiniChartGridInstanceOn == false) {

            //Grid panel instance create
            dataGapsMiniChartGridPanel = new Ext.grid.GridPanel({
                store: dataGapsMiniChartGridStore,
                colModel : dataGapsMiniChartGridModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true
                }),
                autoScroll:false,
                scroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 140,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'dataGapsMiniChartGridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
            });
            dataGapsMiniChartGridInstanceOn = true;
        } else {
            dataGapsMiniChartGridPanel.setWidth( gridWidth);
            dataGapsMiniChartGridPanel.reconfigure(dataGapsMiniChartGridStore, dataGapsMiniChartGridModel);
        }

        hide();
    };//func dataGapsMiniChartGridList End

    //오늘 날짜를 구한다.,
    function getToday() {

        var currentTime = new Date();
        var month = (currentTime.getMonth() + 1);
        var day = currentTime.getDate();//-8
        var year = currentTime.getFullYear();

        if ( day.toString().length == 1) {
            day = "0" + day.toString();
        }

        if ( month.toString().length == 1) {
            month = "0" + month.toString();
        }

        var today = year.toString() + month.toString() + day.toString();
        return today;
    }

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시 호출하게 된다.
     */
    function send() {
        updateFChart();

        //http or flex Request Send
        if (flex != null) {
            flex.requestSend();
        }
    }

    //dailySearch 서치 버튼 이벤트.
    $("#dailySearch").live("click", function(){
        getdataGapsMiniChartGrid();
    });

    //periodSearch
    $("#periodSearch").live("click", function() {
        getdataGapsMiniChartGrid();
    });

    $("#weeklySearch").live("click", function() {
        getdataGapsMiniChartGrid();
    });

    $("#monthlySearch").live("click", function() {
        getdataGapsMiniChartGrid();
    });

    /**
     * Flex 에서 메세지를 조회하기위한 함수
     */
    function getFmtMessage() {
        var fmtMessage = new Array();

        fmtMessage[0] = '<fmt:message key="aimir.all"/>'+' '+'<fmt:message key="aimir.meter"/>'+' '+'<fmt:message key="aimir.count"/>'; //전체미터개수
        fmtMessage[1] = '<fmt:message key="aimir.partialMissing"/>'+' '+'<fmt:message key="aimir.meter"/>'+' '+'<fmt:message key="aimir.count"/>'; //LP 부분누락 미터수";
        fmtMessage[2] = '<fmt:message key="aimir.overallMissing"/>'+' '+'<fmt:message key="aimir.meter"/>'+' '+'<fmt:message key="aimir.count"/>'; //LP 전체누락 미터수";
        fmtMessage[3] = '<fmt:message key="aimir.number"/>';//번호";
        fmtMessage[4] = '<fmt:message key="aimir.date"/>';//"일자";
        fmtMessage[5] = '<fmt:message key="aimir.hour"/>';//"시간";
        fmtMessage[6] = '<fmt:message key="aimir.numberofmissing"/>';//"누락건수";
        fmtMessage[7] = '<fmt:message key="aimir.remetering"/>';//"재검침";

        return fmtMessage;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수
     */
    function getParams() {
        var condArray = new Array();
        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = ${MeterType};
        condArray[4] = supplierId;

        var searchStartDate = $('#searchStartDate').val();
        var searchEndDate = $('#searchEndDate').val();

        //처음 로드시에 날짜가 널일 경우 오늘 날짜로 설정.
        if (searchStartDate == "" && searchEndDate == "") {
            searchStartDate= getToday();
            searchEndDate = getToday();
            searchStartHour="00";
            searchEndHour="23";
        }

        condArray[0] = searchStartDate;
        condArray[1] = searchEndDate;

        return condArray;
    }

    function updateFChart() {
        emergePre();
        $.getJSON('${ctx}/gadget/mvm/getDataGaps.do'
                ,{searchStartDate:$('#searchStartDate').val() ,
                    searchEndDate:$('#searchEndDate').val() ,
                    searchDateType:$('#searchDateType').val() ,
                    meterType:${MeterType} ,
                    supplierId:supplierId }
                ,function(json) {
                     var list = json.result.dataGapsList;
                     fcChartDataXml = "<chart "
                         + "yAxisName='<fmt:message key="aimir.numberofmissing"/>' "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='10' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='0' "
//                        + "labelDisplay='WRAP' labelStep ='2'"
                        + "numberSuffix='  ' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg 
                        + ">";
                     var categories = "<categories>";
                     var dataset = "<dataset seriesName='<fmt:message key="aimir.numberofmissing"/>'>";
                     if (list.length > 0) {
                         for (i = 0; i < list.length; i++) {
                             categories += "<category label='"+list[i].xField+"' />";
                             dataset += "<set value='"+list[i].missingCount+"' color='"+${chartColor}+"' />";
                         }
                     } else {
                         categories += "<category label=' ' />";
                         dataset += "<set value='0' color='"+${chartColor}+"' />";
                     }
                     categories += "</categories>";
                     dataset += "</dataset>";

                     fcChartDataXml += categories + dataset + "</chart>";

                     fcChartRender();

                     hide();
                }
        );
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        if ($('#fcChartDiv').is(':visible')) {
        	fcChart = new FusionCharts({
        		id: 'myChartId',
    			type: 'MSColumn3D',
    			renderAt : 'fcChartDiv',
    			width : $('#fcChartDiv').width(),
    			height : '170',
    			dataSource : fcChartDataXml
    		}).render();
        	
           /*  fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "myChartId", $('#fcChartDiv').width(), "170", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv"); */
            
        }
    }

    //jhkim
    function cmdOnDemandRecollect() {
         $.getJSON('${ctx}/gadget/device/command/cmdOnDemandRecollect.do',
                 {searchStartDate:$('#searchStartDate').val() ,
                searchEndDate:$('#searchEndDate').val() ,
                searchDateType:$('#searchDateType').val() ,
                meterType:${MeterType} ,
                supplierId:supplierId });
         Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.remetering"/>');
    }

    function showResult(responseText, status) {
        //  alert(responseText.result);
        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i = 0; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else {
            var urll = '${url}';
            var port = '${localPort }';
            var ctx ='${ctx}';
            var strr = urll.split(ctx);
            urll = strr[0]+ctx+'/gadget/index.jsp';
            //alert(urll);
            document.location.href =urll;
            return;
        }
    }

    //======================================================================================

/*]]>*/
    </script>
    
      <!-- extJs grid chart style override-->
	<style type="text/css">
    

	 /* html {
	overflow: -moz-scrollbars-vertical; 
	overflow-y: scroll;
	} */
	 

	.x-grid3-hd-inner
	{
		text-align: center;
		font-weight: bold;
	}

	 .x-grid3-row-table
	{
	 text-align: center;
	 
	}
	
	.temp
	{
	 text-align: center;
	}

	.x-grid3-col-1 
	{
	 text-align: center;
	}
	.x-grid3-col-2 
	{
	 text-align: center;
	}

	
	
	</style>
</head>
<body>

<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">
	<div class="dayoptions">
		<%@ include file="../commonDateTab.jsp" %>
	</div>
</div>
<!-- search-background DIV (E) -->

<div id="fcChartDiv" align="left" class="topside10px">
    The chart will appear within this DIV. This text will be replaced by the chart.
</div>
<div class="gadget_body">
		
		<div id="chartBtn" class="floatright margin-r10">
		   <em class="btn_org"><a href="javascript:cmdOnDemandRecollect();" id="bt_search" class="on"><fmt:message key="aimir.remetering" /></a></em>
		</div>
		<%-- <div style="clear: both;">
		      <object id="dataGapsMiniChartEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="197" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
		        <param name="movie" value="${ctx}/flexapp/swf/dataGapsMiniChart.swf">
		        <param name="quality" value="high">
		        <param name="wmode" value="opaque">
		        <param name="swfversion" value="9.0.45.0">
		        <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
		        <param name="expressinstall" value="Scripts/expressInstall.swf">
		        <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
		        <!--[if !IE]>-->
                <object id="dataGapsMiniChartOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/dataGapsMiniChart.swf" width="100%" height="197">
		          <!--<![endif]-->
		          <param name="quality" value="high">
		          <param name="wmode" value="opaque">
		          <param name="swfversion" value="9.0.45.0">
		          <param name="expressinstall" value="Scripts/expressInstall.swf">
		          <param name="allowScriptAccess" value="always">
		          <!-- The browser displays the following alternative content for users with Flash Player 6.0 and older. -->
		          <div>
		            <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
		            <p><a href="http://www.adobe.com/go/getflashplayr"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		          </div>
		          <!--[if !IE]>-->
		        </object>
		        <!--<![endif]-->
		      </object>
		</div>       --%>
</div>
<!-- 미터 카운트 표시하는 div -->
<div class="meterCnt">
	<table border="0" width="1000">
		<!-- all meter Cnt -->
		<tr>
			<td width="200">
				&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.all"/>&nbsp;<fmt:message key="aimir.meter"/>&nbsp;<fmt:message key="aimir.count"/>
			</td>
			<td id="meterCnt1" style="font-weight: bold; color: red">
			 
			</td>
		</tr>
		<!-- LP Partial Missing Meter Count -->
		<tr>
			<td>
	    		&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.partialMissing"/>&nbsp;<fmt:message key="aimir.meter"/>&nbsp;<fmt:message key="aimir.count"/>
			</td>
			<td id="meterCnt2" style="font-weight: bold; color: red">
				
			</td>
		</tr>
		
		<!-- LP Overall Missing Meter Count -->
		<tr>
			<td >
				&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.overallMissing"/>&nbsp;<fmt:message key="aimir.meter"/>&nbsp;<fmt:message key="aimir.count"/>
			</td>
			<td id="meterCnt3" style="font-weight: bold; color: red">
			
			</td>
		</tr>
	
	</table>
</div>

 
<!-- datagaps grid Div-->
<div class="gadget_body2">

	<div id="dataGapsMiniChartGridDiv">
	
	</div>
</div>

</body>
</html>