<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.customerview"/>(<fmt:message key="aimir.energymeter"/>)</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* Ext-Js Grid Header style 정의. */
        .x-grid3-hd-inner {
            text-align: center;
            font-weight: bold;
        }
    </style>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" >/*<![CDATA[*/

        // 페이지 로드시 로그인한 사용자의 공급사 ID 세팅
        var supplierId = "${supplierId}";
        var serviceType = ServiceType.Electricity;
        var type = "";
        var flex;

        var fcChartDataXml;
        var fcChart;
        var browserWidth="";

        Ext.onReady(function() {
            Ext.QuickTips.init();

            updateFChart();
            browserWidth= $(window).width();
            //미니 그리드 chart fetch from s.s
            getcustomerMiniChartGrid();
        });
        
        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            browserWidth= $(window).width();   // returns width of browser viewport

            //리싸이즈시 패널 인스턴스 kill & reload
            customerMiniChartGridPanel.destroy();

            customerMiniChartGridInstanceOn = false;

            getcustomerMiniChartGrid();
            fcChartRender();
        });

 		//#######customerMiniChart Start

        //customerMiniChartGrid propeties
        var customerMiniChartGridInstanceOn = false;
        var customerMiniChartGrid;
        var customerMiniChartColModel;
        var customerMiniChartCheckSelModel;

        function getcustomerMiniChartGrid() {
            //setting grid panel width
            var gridWidth = $("#customerMiniChartGridDiv").width();
            var condArray = getCondition();

            //### customerMiniChartGrid Store fetch
            var customerMiniChartGridStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/system/getCustomerContractInfo.do",
                //파라매터 설정.
                baseParams: {
                    supplierId : condArray[0],
                    serviceType : condArray[1],
                    type : condArray[2]
                },
                //Total Cnt
                totalProperty: "totalCnt",
                root:'customerContractInfoList',
                fields: ["tariffType", "tariffCount", "serviceType"]
            });//Store End

            var fmtMessage = getFmtMessage();

            // customerMiniChartGrid Model DEfine
            customerMiniChartGridModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[11], dataIndex: 'serviceType', width: gridWidth/3}
                   ,{header: fmtMessage[1], dataIndex: 'tariffType', width: gridWidth/3, renderer: addTooltip}
                   ,{header: fmtMessage[2], dataIndex: 'tariffCount', width: gridWidth/3, align: "right"}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                }
            });

            // header tooltip
            for (var i = 0, colCount = customerMiniChartGridModel.getColumnCount(); i < colCount; i++) {
                customerMiniChartGridModel.setColumnTooltip(i, customerMiniChartGridModel.getColumnHeader(i));
            }

            if (customerMiniChartGridInstanceOn == false) {
                //그리고 패널 정의
                customerMiniChartGridPanel = new Ext.grid.GridPanel({
                    store: customerMiniChartGridStore,
                    colModel : customerMiniChartGridModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width:  gridWidth,
                    //패널 높이 설정
                    height: 120,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'customerMiniChartGridDiv',
                    viewConfig: {
                    	forceFit: true,
                        enableRowBody: true,
                        showPreview: true,
                        emptyText: 'No data to display'
                    }
                });
                customerMiniChartGridInstanceOn = true;
            } else {
                customerMiniChartGridPanel.setWidth( gridWidth);
                customerMiniChartGridPanel.reconfigure(customerMiniChartGridStore, customerMiniChartGridModel);
            }
            hide();
        };//func customerMiniChartGridList End

        /**
         * request send
         */
        function send() {
            if (flex != null) {
                flex.getData();
            }
        }

        /**
         * fmt message
         */
        function getFmtMessage() {
            var cnt = 0;
            var fmtMessage = new Array();

            fmtMessage[cnt++] = "<fmt:message key='aimir.alert'/>";                    // Error!
            fmtMessage[cnt++] = "<fmt:message key='aimir.contract.tariff.type'/>";     // 계약 종별
            fmtMessage[cnt++] = "<fmt:message key='aimir.householdcount'/>";           // 가구수
            fmtMessage[cnt++] = "<fmt:message key='aimir.view.detail'/>";              // 상세 보기

            fmtMessage[cnt++] = "<fmt:message key='aimir.normal'/>";  // 정상 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.temporaryPause'/>";  // 휴지 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.pause'/>";  // 정지 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.cancel2'/>";  // 해지 고객

            fmtMessage[cnt++] = "<fmt:message key='aimir.standard2'/>";                // 기준
            fmtMessage[cnt++] = "<fmt:message key='aimir.household'/>";                // 가구

            fmtMessage[cnt++] = "<fmt:message key='aimir.totalCustomerCount'/>";       // 전체 고객수

            fmtMessage[cnt++] = "<fmt:message key='aimir.service.type'/>";       // 서비스 타입
            return fmtMessage;
        }

        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = supplierId;
            condArray[cnt++] = serviceType;
            condArray[cnt++] = type;

            return condArray;
        }
        
        function updateFChart() {
        	emergePre();
        	
       	    $.getJSON('${ctx}/gadget/system/getCustomerContractInfo.do'
       	    	    ,{supplierId:supplierId,
       	    	    	serviceType:serviceType}
    				,function(json) {
                         var list = json.result.chart;
                         fcChartDataXml = "<chart "
	                         //+ "caption='Emergency' "
	                         + "showValues='1' "
	                         + "showPercentValues='1' "
	                         + "showPercentInToolTip='1' "
	                         + "showZeroPies='1' "
	                         + "showLabels='1' "
	                         + "showLegend='1' "
	                         //+ "legendPosition='RIGHT' "
	                         + "legendPosition='BOTTOM' "
	                         + "manageLabelOverflow='1' "
	                         + "enableSmartLabels='1' "
	                         + "chartLeftMargin = '0' "
	                         + "chartRightMargin = '0' "
	                         + "chartTopMargin = '0' "
	                         + "chartBottomMargin = '0' "
        				     + fChartStyle_Common
                        	 + " baseFont='dotum' baseFontSize='12' baseFontColor='#434343'  "
                             + " showBorder='0' showPlotBorder='0' borderColor='E3E3E3' pieRadius='35' use3DLighting='1' legendBorderColor='ffffff' legendBorderThickness='0' legendBgAlpha='100' legendBgColor='ffffff' legendShadow='0'  "
                             
                             + ">";
                    	 var labels;

                  	 	 /* labels += "<set value='"+list.normal+"' color='"+fChartColor_Step4[0]+"' link='JavaScript:getData(0);' />"
                      	 	+ "<set value='"+list.pause+"' color='"+fChartColor_Step4[1]+"' link='JavaScript:getData(1);' />"
                      	 	+ "<set value='"+list.stop+"' color='"+fChartColor_Step4[2]+"' link='JavaScript:getData(2);' />"
                      	 	+ "<set value='"+list.cancel+"' color='"+fChartColor_Step4[3]+"' link='JavaScript:getData(3);' />";
                            + "<set value='"+list.unknown+"' color='"+fChartColor_Step4[4]+"' link='JavaScript:getData(4);' />"; */

                         labels = "<set value='"+list.normal+"' color='"+fChartColor_Step5[0]+"' link='j-getData-" + ContractStatus.NORMAL + "' />"
                                + "<set value='"+list.suspended+"' color='"+fChartColor_Step5[5]+"' link='j-getData-" + ContractStatus.SUSPENDED + "' />"
                                + "<set value='"+list.stop+"' color='"+fChartColor_Step5[1]+"' link='j-getData-" + ContractStatus.STOP + "' />"
                                + "<set value='"+list.cancel+"' color='"+fChartColor_Step5[2]+"' link='j-getData-" + ContractStatus.CANCEL + "' />"
                                + "<set value='"+list.pause+"' color='"+fChartColor_Step5[3]+"' link='j-getData-" + ContractStatus.PAUSE + "' />"
                                + "<set value='"+list.unknown+"' color='"+fChartColor_Step5[4]+"' link='j-getData-null' />";

                    	 if (list.normal == 0 && list.pause == 0 && list.stop == 0 && list.cancel == 0) {
                        	labels = "<set value='1' color='E9E9E9' toolText='' />";
                         }

                         fcChartDataXml += labels + "</chart>";

                         fcChartRender();

                         $('#lb_today').text(list.today + " <fmt:message key='aimir.standard2'/>");
                         //전체 계약 건수로 교체$('#lb_total').text("<fmt:message key='aimir.totalCustomerCount'/> : " + list.totalCount);
                         $('#lb_total').text("<fmt:message key='aimir.contract.totalCnt'/> : " + list.totalCountFormat);

                         $('#lb_normal').text("<fmt:message key='aimir.normal'/> : " + list.normalFormat);
                         $('#lb_suspended').text("<fmt:message key='aimir.suspended'/> : " + list.suspendedFormat);
                         $('#lb_stop').text("<fmt:message key='aimir.pause'/> : " + list.stopFormat);
                         $('#lb_cancel').text("<fmt:message key='aimir.cancel2'/> : " + list.cancelFormat);
                         $('#lb_pause').text("<fmt:message key='aimir.temporaryPause'/> : " + list.pauseFormat);
                         $('#lb_unknown').text("<fmt:message key='aimir.unknown'/> : " + list.unknownFormat);

                         /*$('#lb_todayNormal').text("<fmt:message key='aimir.contractCustomer.today'/> : " + list.todayNormal);
                         $('#lb_todayCancel').text("<fmt:message key='aimir.cancelCustomer.today'/> : " + list.todayCancel);*/
                    }
       	    );

       		hide();
    	}

        function fcChartRender() {
        	if ($('#fcChartDiv').is(':visible')) {
		    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myChartId", $('#fcChartParentDiv').width() - ($('#fcChartLegend').width() + 10)  , "170", "0", "0");
		    	fcChart.setDataXML(fcChartDataXml);
	            fcChart.setTransparent("transparent");
	            fcChart.render("fcChartDiv");
        	}
        }

        function getData(_type) {
            type = String(_type);
            getcustomerMiniChartGrid();
        }

        // grid column tooltip
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
<div id="fcChartParentDiv" class="lgnd_detail_parent">
	<div id="fcChartDiv" class="floatleft">
	    The chart will appear within this DIV. This text will be replaced by the chart.
	</div>	
    <div id="fcChartLegend" class="lgnd_detail_div" style="height:auto; width:160px;" >
	 <div>
		<ul>
		<li><label id="lb_today"><fmt:message key='aimir.standard2'/></label></li>
		<li class="bluebold12pt"><label id="lb_total"><fmt:message key='aimir.registerAgree'/> : </label></li>
		<li class="lgnd">
		  <table cellpadding="0" cellspacing="0">
			<colgroup>
			<col width="20" />
			<col width="" />
			</colgroup>
			<tr>
			<td><span class="fChartColor_1">&nbsp;</span></td>
			<td><label id="lb_normal"><fmt:message key='aimir.normal'/> : </label></td>
			</tr>
            <tr>
            <td><span class="fChartColor_6"></span></td>
            <td><label id="lb_suspended"><fmt:message key='aimir.suspended'/> : </label></td>
            </tr>
			<tr>
			<td><span class="fChartColor_2"></span></td>
            <td><label id="lb_stop"><fmt:message key='aimir.pause'/> : </label></td>
			</tr>
			<tr>
			<td><span class="fChartColor_3"></span></td>
			<td><label id="lb_cancel"><fmt:message key='aimir.cancel2'/> : </label></td>
			</tr>
			<tr>
			<td><span class="fChartColor_4"></span></td>
            <td><label id="lb_pause"><fmt:message key='aimir.temporaryPause'/> : </label></td>
			</tr>
            <tr>
            <td><span class="fChartColor_5"></span></td>
            <td><label id="lb_unknown"><fmt:message key='aimir.unknown'/> : </label></td>
            </tr>
		  </table>
		</li>
		<!-- <li class="blue11pt"><label id="lb_todayNormal"><fmt:message key='aimir.contractCustomer.today'/> : </label></li>
		<li class="blue11pt"><label id="lb_todayCancel"><fmt:message key='aimir.cancelCustomer.today'/> : </label></li> -->
		</ul>
	 </div>	
	</div>    
</div>
   
    <div id="gadget_body">
	    <div id="customerMiniChartGridDiv"></div>
    </div>
</body>
</html>
