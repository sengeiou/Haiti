<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>스케쥴러 관리 미니가젯</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FChartStyle.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
    
        <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>  
    
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
        .white .x-window-body {background-color: white;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }

		html {
				overflow-y: auto;
		}
    </style>
    
    <script type="text/javascript">
    function getFmtMessage() {
    	var fmtMessage = new Array();
		
		fmtMessage[0]  = '<fmt:message key="aimir.all"/>'; //"전체";
		fmtMessage[1]  = '<fmt:message key="aimir.jobName"/>';        //"작업명";	
		fmtMessage[2]  = '<fmt:message key="aimir.checkSchedulerRun"/>';        //"스케쥴러 실행 여부를 확인해 주세요";	

		return fmtMessage;
	}
    
	var isExecute = false;
	
    $(document).ready(function() {
        // Tooltip사용을 위해서 반드시 선언해야 한다.
        Ext.QuickTips.init();
    	jobIntegration();
    });
    
    var jobSummaryGridOn = false;
    var jobSummaryColModel;
    function jobIntegration() {
    	jobSummaryGrid();
    	jobGrid();
    }

	var jobSummaryGrid = function() {
        var width = $("#jobSummaryMiniGridDiv").width();

			var jobSummaryStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0}},
            url : '${ctx}/gadget/system/schedule/getJobDetailList.do',
            baseParams: {},
            root : 'statistic',
            listeners       : {
                load: function(store, options){
                   	var jobData = store.reader.jsonData;
                   	if(jobData.result.length <= 0) {
                   		isExecute = false;
                    } else {
                    	isExecute = true;
                    }
                }
            },
            fields : [
  	                { name: 'TOTAL', type: 'string' },
                      { name: 'BLOCKED', type: 'string' },
                      { name: 'COMPLETE', type: 'string' },
                      { name: 'ERROR', type: 'string' },
                      { name: 'NONE', type: 'string' },
                      { name: 'NORMAL', type: 'string' },
                      { name: 'PAUSED', type: 'string' },
  	                ]

        });

			jobSummaryColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuisabled: true,
                align:'right',
                width: (width-4)/7
            },
            columns: [{
                header: "<fmt:message key='aimir.all'/>",
                dataIndex: 'TOTAL'
            },{
                header: "BLOCKED",
                dataIndex: 'BLOCKED'
            },{
                header: "COMPLETE",
                dataIndex: 'COMPLETE'
            },{
                header: "ERROR",
                dataIndex: 'ERROR'
            },{
                header: "NONE",
                dataIndex: 'NONE'
            },{
                header: "NORMAL",
                dataIndex: 'NORMAL'
            },{
                header: "PAUSED",
                dataIndex: 'PAUSED'
            }]
        });

         if (!jobSummaryGridOn) {
            jobSummaryGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : width,
                height : 50,
                store : jobSummaryStore,
                colModel : jobSummaryColModel,
                stripeRows : true,
                columnLines : true,
                deferRowRender : true,
                autoScroll : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'jobSummaryMiniGridDiv'
            });
            jobSummanryGridOn = true;
        }
	}
	
    var jobGridOn = false;
    var jobGridColModel;
    var jobGrid = function() {

        var width = $("#jobMiniGridDiv").width();
        
        var jobGridStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0}},
            url : '${ctx}/gadget/system/schedule/getJobDetailList.do',
            baseParams: {},
            root : 'result',
            listeners : {
            	load: function() {
            		//스케줄러가 현재 실행중인지 확인 후 경고창을 보임
            		 if(!isExecute) {
						Ext.Msg.alert("Warning","<fmt:message key='aimir.checkSchedulerRun'/>");
					}
            	}
            },
            fields : [
                      { name: 'name', type: 'string' },
                     { name: 'BLOCKED', type: 'string' },
                     { name: 'COMPLETE', type: 'string' },
                     { name: 'ERROR', type: 'string' },
                     { name: 'NONE', type: 'string' },
                     { name: 'NORMAL', type: 'string' },
                     { name: 'PAUSED', type: 'string' }
                     ] 
        });

        jobGridColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true,
                width: (width-4)/7
            },
            columns: [{
                header: "<fmt:message key='aimir.jobName'/>",
                dataIndex: 'name',
                align:'left'
            },{
                header: "BLOCKED",
                dataIndex: 'BLOCKED',
                align:'right'
            },{
                header: "COMPLETE",
                dataIndex: 'COMPLETE',
                align:'right'
            },{
                header: "ERROR",
                dataIndex: 'ERROR',
                align:'right'
            },{
                header: "NONE",
                dataIndex: 'NONE',
                align:'right'
            },{
                header: "NORMAL",
                dataIndex: 'NORMAL',
                align:'right'
            },{
                header: "PAUSED",
                dataIndex: 'PAUSED',
                align:'right'
            }]
        });

         if (!jobGridOn) {
            jobGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : width,
                height : 230,
                store : jobGridStore,
                colModel : jobGridColModel,
                stripeRows : true,
                columnLines : true,
                deferRowRender : true,
                autoScroll : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'jobMiniGridDiv',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                }
            });
            jobGridOn = true;
        }
    };
  
  </script>
</head>

<body>
   <!--          좌측 job 그리드 시작                       -->
	<div class="gadget_body">
	    <div id="jobSummaryMiniGridDiv" style="width: 100%; margin-bottom: 20px"></div>
        <div id="jobMiniGridDiv" style="width: 100%;"></div> 
	</div>
	<!--          좌측 job 그리드 끝                       -->
	
	
</body>
</html>