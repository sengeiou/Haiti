<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="icon" type="image/png" href="${ctx}/images/favicon2.ico" />
 	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
 	<%-- <%@ include file="/gadget/system/preLoading.jsp"%> --%>
	<title><%-- <fmt:message key="aimir.GroupSchedule"/> --%>Concentrator Group Schedule</title>
	
	<!-- STYLE -->
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >    
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

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
        
        /* 팝업창 레이아웃 */
        #wrapper{
			width: 100%;
			height:500px;
		}
		#popupTitle{
			width: 100%;
			height:30px;
		}
		#topGroup{
			width: 100%;
			height:70px;
			background-color:#E0F0FD;
		}
		#selectGroup{
			width: 24%;
			height:70px;
			float:left;
			background-color:#E0F0FD;
		}
		#span_01{
			padding-top:25px;
			padding-left:10px;
			width: 90px;
			float:left;
		}
		#McuGroupList{
			padding-top:25px;
			width: 180px;
			float:left;
		}
		#topGroupRight{
			width: 76%;
			height:70px;
			background-color:#EBF7FF;
			float:left;
		}
		#templateBtn{
			width: 100%;
			height:40px;
			/* background-color:#E0FFFD; */
			float:left;
		}
		#retryText{
			width: 100%;
			height:30px;
			/* background-color:#FFFFA7; */
			vertical-align:middle;
			float:left;
		}
		#container{
			width: 100%;
			height:600px;
		}
		#mcuList{
			width: 24%;
			height:100%;
			float:left;
		}
		#scheduleTemplate{
			width: 76%;
			height:100%;
			float:left;
			/*background-color:#EBF7FF;*/
		}
		#tempScheduleView{
			width: 100%;
			height:600px;
			/*background-color:#EBF7FF;*/
		}
		#footer{
			width: 100%;
			height:100px;
			/*background-color:#E0F0FD;*/
		}
		        
    </style>
    
    <!-- LIB -->
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>	
	
	<!-- SCRIPT -->
    <script type="text/javascript" charset="utf-8">
    
   	var objs = window.opener.objs;
    var groupNameStore;
    var groupNameCol;
    var groupNameGrid;
    var groupNameGridOn = false;
    
    var selectedGroupTotal;
    var selectedGroupId;
    var SelectedGroupOn = false;
    var mcuSelectedData;
    
    var groupMcuStore;
        
    var timeoutPeriod = 12000000;
	var pageSize = 10;
	var supplierId = objs.supplierId;
	var loginId = objs.loginId;
	var operatorId = objs.operatorId;
    
    $(document).ready(function(){
    	getMcuGroupList();
    	getFileUploadModule();
    });
    
    /* 현재 Group List Selectbox로 출력 */
    var getMcuGroupList = function(){
        $('#sMcuGroup').selectbox();
        $.getJSON('${ctx}/gadget/device/getMcuGroupList.do',{'operatorId':operatorId},
        		function(returnData){
	        		var groupList = returnData.result;
	        		for(var v=0; v<groupList.length; v++){
	        			$('#sMcuGroup').append('<option value="'+groupList[v].id+'">'+groupList[v].name+'</option>');
	        		}
	        		$('#sMcuGroup').selectbox();
	        		changeGroup();
        });
    }
    
    /* Group선택시 List Update */
    function changeGroup(){
    	var sMcuGroup = $('#sMcuGroup').val();
    	selectedGroupId = sMcuGroup;
    	updateMcuList();    	
    }
    
    
    /* Group Mcu List Print Start */
    function updateMcuList() {
    	groupMcuStore = new Ext.data.JsonStore({
    		autoLoad:{params:{start:0, limit:100}},
    		url:'${ctx}/gadget/device/updateSelectedMcuList.do',
    		totalProperty: 'total',
    		root:'result',
    		idProperty : 'rowNo',
    		baseParams:{
    			'groupId': selectedGroupId,
    			'supplierId': supplierId
    			},
    		fields:[
    			{name:'rowNo', type:'Integer'},
    			{name:'MCUID', type:'String'},
    			{name:'SYSID', type:'String'},
    			{name:'LOCATION', type:'String'},
    			],
   			listeners: {
                   beforeload: function(store, options){
                   options.params || (options.params = {});
                   Ext.apply(options.params, {
                                 page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                            });
                  },load: function(store){
                	  	selectedGroupTotal = Ext.encode(store.totalLength);
                  }
    		}
    	});
    	
    	
    	mcuNameCol = new Ext.grid.ColumnModel({
	   		columns:[
				{header:"<font style='font-weight: bold;'><fmt:message key='aimir.number'/></font>", dataIndex: "rowNo", align:"center", width:50}        			
				,{header:"<font style='font-weight: bold;'><fmt:message key='aimir.mcuid'/></font>", dataIndex: "SYSID", align:"center", width:96}        			
				,{header:"<font style='font-weight: bold;'><fmt:message key='aimir.location'/></font>", dataIndex: "LOCATION", align:"center", width:140}        			
	   		],
	   		defaults:{
				align: 'center'        			
	   			,sortable: true
	            ,menuDisabled: true
	            ,width: 290
	   		}
	   	});
    	
    	if(!groupNameGridOn){
           	$('#mcuGroupListGrid').html(' ');
           	groupNameGrid = new Ext.grid.GridPanel({
           		store: groupMcuStore,
           		colModel: mcuNameCol,
           		width:290,
           		height : 530,  		           		
           		renderTo: 'mcuGroupListGrid',
           		autoScroll: false,
           		stripeRows: true,
        		columnLines : true,
        		loadMask: {
        			msg: 'Loading..'
        		},
        		viewConfig:{
       				enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
       			},
     			bbar: new Ext.PagingToolbar({
     					pageSize: 100,
     					store: groupMcuStore,
     					displayInfo: true,
     					displayMsg:' {0} - {1} / {2}'
     				})
        	});
           	groupNameGridOn = true;
           }else{
        	   var bottomToolbar = groupNameGrid.getBottomToolbar();
           		groupNameGrid.reconfigure(groupMcuStore, mcuNameCol);
           		bottomToolbar.bindStore(groupMcuStore);
           }
    }/* Group Mcu List Print End */
    
    var filepath;
    var titleName;

    /* getFileUploadModule Upload 실행 */
	var getFileUploadModule = function(){
		new AjaxUpload('getTempFileName', {
	        action: '${ctx}/gadget/device/getUploadTempFile.do',
	        data : {},
	        responseType :"json",
	        onSubmit : function(file , ext){            	
	            if (ext && /^(xls|xlsx)$/.test(ext)) {
	                this.setData({
	                    'key': 'This string will be send with the file'
	                });
	            } else {
	            	Ext.Msg.alert('<fmt:message key="aimir.message"/>','Please select a valid file format.<br/><b>File format</b> [ xls, xlsx ]');
	                return false;
	            }
	        },
	        onComplete : function(file, responseText){
	        	if(responseText !=null){
	                filePath = responseText.tempFilepath;
    	            titleName = responseText.titleName;
    	            
	                //OK 하기 전에 이름,경로 저장
        	        $('#scheduleTempFilename').val(file);
        	        $('#scheduleTempFilepath').val(filePath);
	        	}else{
	        		alert("Response Error!");
	        	}
	        	setScheduleGrid(file, filePath);
            }
		});
    }
	 
    var scheduleStore;
    var scheduleCol;
    var scheduleGrid;
    var scheduleGridOn = false;
    /* Upload Excel File Grid Start*/
    var setScheduleGrid = function(file, filePath) {
    	
    	scheduleStore = new Ext.data.JsonStore({
    		autoLoad:{params:{start:0, limit:50}},
    		url:'${ctx}/gadget/device/getExcelResult.do',
    		totalProperty:'index',
    		root:'resultList',
    		idProperty:'No',
    		baseParams:{
    			supplierId : supplierId,
		 		filePath   : filePath
    		},fields:[
    			{name:'No', type:'Integer'},
    			{name:'Name', type:'String'},
    			{name:'Condition', type:'String'},
    			{name:'Task', type:'String'},
    			{name:'Suspend', type:'String'}
    		],
    		listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
               }
 			}
    	});
    	
    	var fm = Ext.form;
    	
    	scheduleCol = new Ext.grid.ColumnModel({
    		columns:[
    			{header:"<font style='font-weight: bold;'>No</font>", dataIndex: "No", align:"center", width:30},   
    			{header:"<font style='font-weight: bold;'>Name</font>", dataIndex: "Name", align:"center", width:100, editor:new fm.TextField({allowBlank:false})},   
    			{header:"<font style='font-weight: bold;'>Condition</font>", dataIndex: "Condition", align:"center", width:300, editor:new fm.TextField({allowBlank:false})},   
    			{header:"<font style='font-weight: bold;'>Task</font>", dataIndex: "Task", align:"center", width:450, editor:new fm.TextField({allowBlank:false})},   
    			{header:"<font style='font-weight: bold;'>Suspend</font>", dataIndex: "Suspend", align:"center", width:80, editor:new fm.TextField({allowBlank:false})},   
    		],
    		defaults:{
    			align:'center',
    			sortable:true,
    			menuDisabled:true,
    			width:964
    		}
    	});
   
    	/* Editable Grid */
    	if(!scheduleGridOn){
    		$('#tempScheduleView').html('  ');
    		scheduleGrid = new Ext.grid.EditorGridPanel({
    			store:scheduleStore,
    			colModel:scheduleCol,
    			width:964,
    			height:530,
    			renderTo:'tempScheduleView',
    			clicksToEdit:1,
    			autoScroll:false,
    			stripeRows: true,
    			columnLines:true,
    			loadMask:{
    				msg:'Loading...'
    			},
    			viewConfig:{
    				enableRowBody:true,
    				showPreview:true,
    				emptyText:'No data to display'
    			},
    			listeners:{
	    			afteredit:function(e){
	    				var record = e.record;
	    				var grid = e.grid;
	    				var field = e.field;
	    				var value = e.value;
	    				var originalValue = e.originalValue;
						
	    				if(value == "" || value.trim() == ""){
	    					Ext.Msg.alert('Warning!', 'Please input correct data.');
	    					scheduleStore.data.items[e.row].data[field] = originalValue;
	    					e.record.commit();
	   						scheduleStore.refresh;
	    				}else{
	   						if(value!= originalValue){
		   						e.record.commit();
		   						scheduleStore.data.items[e.row].data[field] = value;
		   						scheduleStore.refresh;
	   						}
	    				}
	    				
	    			}
    			},
    			bbar: new Ext.PagingToolbar({
                    pageSize: 50,
                    store: scheduleStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
    				})
    		});
    		scheduleGridOn = true;    		
    	}
    	
    	
    	/* Just Grid 
    	if(!scheduleGridOn){
    		$('#tempScheduleView').html('  ');
    		scheduleGrid = new Ext.grid.GridPanel({
    			store:scheduleStore,
    			colModel:scheduleCol,
    			width:550,
    			height:300,
    			renderTo:'tempScheduleView',
    			autoScroll:false,
    			stripeRows: true,
    			columnLines:true,
    			loadMask:{
    				msg:'Loading...'
    			},
    			viewConfig:{
    				enableRowBody:true,
    				showPreview:true,
    				emptyText:'No data to display'
    			},
    			bbar: new Ext.PagingToolbar({
                    pageSize: 50,
                    store: scheduleStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
    				})
    		});
    		scheduleGridOn = true;    		
    	} */
    	else{
    		var bottomToolbar = scheduleGrid.getBottomToolbar();
       		scheduleGrid.reconfigure(scheduleStore, scheduleCol);
       		bottomToolbar.bindStore(scheduleStore);
    	}
    }/* Upload Excel File Grid End*/
    
    function onlyNumber(obj){
    	$(obj).keyup(function(){
    		$(this).val($(this).val().replace(/[^0-9]/g,""));
    	})
    }
    
    
    var schDatas; 
    var size;
    var setScheduleGridOn;
    <!-- SetSchedule Button Start-->
    var mcuGroupSetSchedule = function(){
    	var nameArr = [];
        var suspendArr = [];
        var conditionArr = [];
        var taskArr = [];
        var mcuId = [];
        var result = [];
        
        Ext.Ajax.timeout = selectedGroupTotal*1000*60;
        timeoutPeriod = selectedGroupTotal*1000*60;
        
    	var varRetry = $('#varRetryDefault').val().trim();
    	if(!scheduleGridOn){
	    	Ext.Msg.alert('Warning!', 'Please upload schedule excel file.');
    	}else if(varRetry == null || varRetry==''){
	    	Ext.Msg.alert('Warning!', 'Please fill in retry interval count.');
    	}else{
    		Ext.Msg.wait('Waiting for response.', 'Wait !');
    		scheduleStore.load();
    		var size = Ext.encode(scheduleStore.reader.jsonData.resultList.length);
    		for(var i =0; i < size; i++){
    			nameArr[i] = Ext.encode(scheduleStore.data.items[i].data['Name']);
	   			suspendArr[i] = Ext.encode(scheduleStore.data.items[i].data['Suspend']);
	   			conditionArr[i] = Ext.encode(scheduleStore.data.items[i].data['Condition']);
	   			taskArr[i] = Ext.encode(scheduleStore.data.items[i].data['Task']);
    		}
    		
    		var params = {
    				'loginId': loginId,
					'nameArr' : nameArr,
					'suspendArr' : suspendArr,
					'conditionArr' : conditionArr,
					'taskArr' : taskArr,
					'retryCondition': varRetry,
					'groupId': selectedGroupId,
					'operatorId': operatorId,
					'supplierId': supplierId
    			};
   			jQuery.ajaxSettings.traditional = true;
   			
   			setScheduleStore = new Ext.data.JsonStore({
   				autoLoad:{params:{start:0, limit:1000}},
   				url:'${ctx}/gadget/device/mcuGroupSetSchedule.do',
   				totalProperty:size,
   				root:'resultList',
   				idProperty:'mcuId',
   				baseParams:params,
   				fields:[
   					{name:'mcuId', type:'String'},
   	    			{name:'result', type:'String'},
   	    			{name:'retryCon', type:'String'}
   				],
   				listeners:{
   				 	beforeload: function(store, options){
   	                options.params || (options.params = {});
   	                Ext.apply(options.params, {
   	                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
   	                         });
   					}
   				}
   			});

   			setScheduleCol = new Ext.grid.ColumnModel({
   				columns:[
   	    			{header:"<font style='font-weight: bold;'><fmt:message key='aimir.mcuid'/></font>", dataIndex: "mcuId", align:"center", width:100},   
   	    			{header:"<font style='font-weight: bold;'><fmt:message key='aimir.schedule'/>&nbsp;<fmt:message key='aimir.result'/></font>", dataIndex: "result", align:"center", width:300},   
   	    			{header:"<font style='font-weight: bold;'><fmt:message key='aimir.retrycount'/>&nbsp;<fmt:message key='aimir.result'/></font>", dataIndex: "retryCon", align:"center", width:400}   
   	    		],
   	    		defaults:{
   	    			align:'center',
   	    			sortable:true,
   	    			menuDisabled:true,
   	    			width:800
   	    		}
   			});
   			
   			
   			setScheduleGridOn = false;
   			if(!setScheduleGridOn){
   				/* Ext.Msg.wait('Waiting for response.', 'Wait !',{
   		    		text:'<fmt:message key="aimir.maximum"/>' + timeoutPeriod/1000/60 + 'm...',
   		    		scope: this,
   		    	}); */
	   			setScheduleGrid = new Ext.grid.GridPanel({
	   				store:setScheduleStore,
	   				colModel:setScheduleCol,
	   				width:800,
	   				height:300,
					autoScroll:false,
					stripeRows:true,
					columnLines:true,
					loadMask:{
						msg:'<fmt:message key="aimir.maximum"/> '+ timeoutPeriod/1000/60 + 'm...'
					},
					viewConfig:{
	    				enableRowBody:true,
	    				showPreview:true,
	    				emptyText:'No data to display'
	    			},
	    			bbar: new Ext.PagingToolbar({
	                    pageSize: 1000,
	                    store: setScheduleStore,
	                    displayInfo: true,
	                    displayMsg: ' {0} - {1} / {2}'
	    				})
	   			});
	   			setScheduleGridOn = true;
   			}else{
   	    		var bottomToolbar = setScheduleGrid.getBottomToolbar();
   	       		setScheduleGrid.reconfigure(setScheduleStore, setScheduleCol);
   	       		bottomToolbar.bindStore(setScheduleStore);
   	    	}
   			Ext.Msg.hide();
    	
    	var setScheduleWin = new Ext.Window({
    		id:'setScheduleWindow',
    		layout: 'fit',
    		title: 'Group Set Schedule Result',
    		autoScroll: false,
    		items:[setScheduleGrid],
    		listensers:{}
    	});
    	
    	setScheduleWin.show();
    	
    	}
   			
    }<!-- SetSchedule Button End-->
   
    
    <!-- GetSchedule Button Start-->
    var mcuGroupGetSchedule = function(){
    	selectedGroupTotal = parseInt(selectedGroupTotal)+parseInt(3);
    	
    	Ext.Ajax.timeout = (selectedGroupTotal)*1000*60;
        timeoutPeriod = (selectedGroupTotal)*1000*60;
        
    	Ext.Msg.wait('Waiting for response.', 'Wait !',{
    		text:'<fmt:message key="aimir.maximum"/>' + timeoutPeriod/1000/60 + 'm...',
    		scope: this,
    	});
    	
    	$.post('${ctx}/gadget/device/mcuGroupGetScheduleMakeExcel.do'
                , {	"loginId" : loginId,
                 	"supplierId" : supplierId,
                 	"filePath" : "<fmt:message key='aimir.report.fileDownloadDir'/>",
                 	"groupId" : selectedGroupId   
    			}, function(json) {
                    if (json.status == "FAIL") {
                    	Ext.Msg.hide();
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','Get Schedule FAIL!');
                    	return;
                    }else{
                    	$("#filePath").val(json.filePath);
                        $("#fileName").val(json.fileName);
                        fileDown();
                        Ext.Msg.hide();
                    }
                });
        
    }<!-- GetSchedule Button End-->
    
    var fileDown = function(){
    	var url = "${ctx}/common/fileDownload.do";
        var downform = document.getElementsByName("reportDownloadForm")[0];
        downform.action = url;
        downform.submit();
    }
    
    function winClose() {
        window.close();
    }
    
    </script>
    <base target="_self" ></base> 
</head>
<body>
<form name="reportDownloadForm" id="reportDownloadForm" method="post" target="_self" style="display:none;">
<input type="hidden" id="filePath" name="filePath" />
<input type="hidden" id="fileName" name="fileName" />3
</form>
<iframe name="downFrame" style="display:none;"></iframe>
<div id="wrapper" style="height: 500px;">

	<div id="popupTitle">
		<!-- title -->
		<div class="search-bg-basic">
			<ul class="basic-ul">   
				<li class="basic-li bluebold11pt withinput"><fmt:message key="aimir.concentrator"/>&nbsp;<fmt:message key="aimir.group"/>&nbsp;<fmt:message key="aimir.schedule"/></li>                
		       </ul>
		</div>
	</div>

	<div id="topGroup">
		<!-- Mcu Group List Select Box -->
		<div id="selectGroup">
			<div id="span_01">
				<span class="bluebold11pt"><fmt:message key="aimir.select"/>&nbsp;<fmt:message key="aimir.group"/>&nbsp;&nbsp;</span>
			</div>
			<div id="McuGroupList" >
				<select id="sMcuGroup" name="sMcuGroup" class="selectbox" style="width:180px;" onChange="javascript:changeGroup();">
				</select>
			</div>
		</div>

		<div id="topGroupRight">
			<!-- Template Btn List -->
			<div id="templateBtn">
				<div id="btn" class="btn_left_top2 margin-t5px">
				<ul id="mcuScheduleDownload">
					<li><a href="${ctx}/temp/ScheduleTemplate.xlsx" class="on">
					<fmt:message key="aimir.template" />&nbsp;<fmt:message key="aimir.download" /></a></li>
				</ul>
				<span><input type="text" id="scheduleTempFilename" name="scheduleTempFilename" style="width:250px" readonly="readonly"/></span>
	                       <span><input type="hidden" id="scheduleTempFilepath" style="width:200px"/></span>
				<span class="am_button margin-r5"><a href="#" id="getTempFileName" class="on"><fmt:message key="aimir.file.upload"/></a></span>
				</div>
			</div>
			<!-- Retry Interval Count -->
			<div id="retryText">
				<div class="margin-t5px margin-l20">
					<span class="bluebold11pt"><fmt:message key="aimir.retryIntervalTime"/> &nbsp;&nbsp;&nbsp;</span>
					<span class="bluebold11pt"></span>
					<div id="retryBox">
						<input type="text" id="varRetryDefault" class="values greenbold" style="width:37px;" value="1" onkeypress="javascript:onlyNumber(this);"/>
					</div>
				</div>
				
				<!-- group command Button -->
				<div id="btn" class="btn_right_top2">
					<ul id="mcuGroupGetScheduleBtn">
						<li><a href="JavaScript:mcuGroupGetSchedule();" class="on">
						<fmt:message key="aimir.get" />&nbsp;<fmt:message key="aimir.schedule" /></a></li>
					</ul>
					<ul id="mcuGroupSetScheduleBtn">
						<li><a href="JavaScript:mcuGroupSetSchedule();" class="on">
						<fmt:message key="aimir.set" />&nbsp;<fmt:message key="aimir.schedule" /></a></li>
					</ul>
					<ul id="mcuGroupCancelBtn">
						<li><a href="JavaScript:winClose();" class="on">
						<fmt:message key="aimir.cancel" /></a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>

	<div id="container">
		<!-- Mcu Group List Grid -->
		<div id="mcuList">
			<div id="mcuGroupListGrid" class="margin-l15 margin-t10px">
			</div>
		</div>

		<!-- Schedule List Grid -->
		<div id="scheduleTemplate">
			<div align="center" id="tempScheduleView" class="margin-t10px">
				
			</div>
		</div>
	</div>
	
	<div id="footer">

	</div>
</div>


</body>
</html>