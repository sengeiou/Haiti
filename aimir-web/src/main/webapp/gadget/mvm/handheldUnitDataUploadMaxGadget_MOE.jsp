
<!-- 
	가젯 : Metering Data Manual Upload
	설명 : HHU (Hand-Held Unit)으로 수동으로 검침한 검침 데이터를
	       Aimir Web UI에서 업로드하는 기능
	비고 : 사전 정의된 양식에 맞춰 작성된 엑셀 파일을 사용
	sejin han
 -->
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Metering Data Manual Upload (MaxGadget)</title>

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
	</style>

<!-- LIB -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

</head>
<body>
<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">
	 	
	// 기본 업로드 정보
	var loginId = "";
	var filePath = "";
	var fileName = "";
	var dataType = "";	
	// 재업로드 정보
	var meterSerial = "";
	var uploadHistoryId = "";
	// 웹
	var supplierId = "";
	var chromeColAdd = 2;
 	
	
 	/**
 	  * 공통 모듈  
 	  */ 	
	$(document).ready(function () {
		// 유저 세션 정보 가져오기
		$.getJSON('${ctx}/common/getUserInfo.do',
		        function(json) {
		            if(json.supplierId != ""){
		                supplierId = json.supplierId;
		                loginId = json.loginId;
		                //검색 옵션 초기화
		        		clearSearchOptions();
		            }
		        }
		); 
		
		//실패 이력과 업로드 팝업 숨김
		$('#uploadFailBodyDiv').hide();
		$('#uploadWindowDiv').hide();
		//달력 모듈이 완료될때까지 지연
		setTimeout(function(){getHistoryGrid();}, 1000);
		
	});
 		
 	//검색 옵션 초기화 (달력 모듈 처리)
 	function clearSearchOptions(){ 	  
 	    var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        
        if(("" + month).length == 1) month = "0" + month;
        if(("" + day).length == 1) day = "0" + day;
        
        var setDate      = year + "" + month + "" + day;
 	    var dateFullName = "";
 	    var locDateFormat = "yymmdd";
        
 		// 날짜를 지역 날짜 포맷으로 변경
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    dateFullName = json.localDate;
                    $('#historyStartDate').val(dateFullName);
                    $('#historyEndDate').val(dateFullName);
                });
        $('#historyStartDate').val(setDate);
        $('#historyEndDate').val(setDate);
        $('#historyStartDateHidden').val(setDate);
        $('#historyEndDateHidden').val(setDate);
		 	
        $('#historyStartDate').datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );	
        $('#historyEndDate')  .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        
        // TextBox 초기화
        $('#meteridbox').val('');
        $('#loginidbox').val('');
        
 	}
 	
 	// datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate, inst){
 		var dateId = '#' + inst.id;

 		var dateHiddenId = '#' + inst.id + 'Hidden';
        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                 ,{dbDate:setDate, supplierId:supplierId}
                 ,function(json) {                	 
                      $(dateId).val(json.localDate);
                      $(dateId).trigger('change');
        });
    }
 	
 	// 검침 데이터 업로드 버튼
 	function uploadPopup(){ 		
 		fileName = "";
 		filePath = "";
 		dataType = "";
 		meterSerial = "";
 		uploadHistoryId = "";
 		drawUploadPanel();
 	}
 	
 	// 이력 검색 버튼
 	function historySearch(){
 		getHistoryGrid();
 	}
 	
 	// 엑셀 출력 버튼
 	var winObj;
 	function historyExcel(){ 		 		
 		var conditionArray = getHistoryCondition();
 		var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
	    var obj = new Object();
	    
	    obj.meterId = conditionArray[1];
	    obj.loginId = conditionArray[2];
	    obj.startDate = conditionArray[3];
	    obj.endDate = conditionArray[4];	    
	    obj.title      = "ExportedUploadHistory_";
	    // call Type으로 '엑셀 출력 기능'과 '파일 다운로드 기능'을 구분
	    obj.callType = "hhuExcel";
	
	    if(winObj)
	        winObj.close();
	    winObj = window.open("${ctx}/gadget/mvm/hhuFileDownloadPopup.do", "hhuExcelExportPopup", opts);
	    winObj.opener.obj = obj;
 	}
 	
 	// 파일 업로드 패널 그리기
 	var uploadWindow; 
 	var uploadFormPanel;
 	var vendorStore;
 	function drawUploadPanel(){
 		// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('uploadWindowPanel')){
 			Ext.getCmp('uploadWindowPanel').close();
 		} 		
 		
 		var uploadFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        
 		        labelWidth  : 100, 		        
 		        items       : [
 		            {
 		            	xtype : 'combo',
 		            	id : 'sVendor', 		            	
 		            	displayField : 'name', 		            	
 		            	mode : 'local',
 		            	valueField : 'id',
 		            	store : new Ext.data.JsonStore({
			            	//fields : ['id', 'name', 'descr', 'code', 'address'],			            	
			            }),
			            listeners : {
			                select: function(com,rec,ind) {
			                	getDeviceModelsByVenendorId();
			                }
			            },
			            editable : false,
			            triggerAction:'all',
			            width : 240,
			            emptyText : '<fmt:message key="aimir.select.devicevendor"/>',
 		            	fieldLabel : 'Manufacturer', 		            	
 					},{
 		            	xtype: 'combo',
 		            	id : 'sModel',
 		            	displayField : 'name', 		            	
 		            	mode : 'local',
 		            	valueField : 'id',
 		            	store : new Ext.data.JsonStore({
			            	//fields : ['id', 'name', 'deviceVendor', 'code', 'deviceType'],			            	
			            }),
			            editable : false,
			            triggerAction:'all',			           
 		            	width : 240,
 		            	emptyText : '<fmt:message key="aimir.select.vendorfirst"/>',
 		            	fieldLabel: 'Model Name '
 		            },{
 		            	xtype: 'textfield',
 		            	id : 'mdsid',
 		            	width : 240,
 		            	emptyText: '<fmt:message key="aimir.alert.selectFile"/>',
 		            	//emptyText: 'Eg. 0017260004',
 		                fieldLabel: 'Meter Serial ',
 		                disabled : true,
 		            },{
 		            	xtype: 'textfield',
 		            	id : 'filename',
 		            	width : 240,
 		                emptyText: '<fmt:message key="aimir.alert.selectFile"/>',
 		                fieldLabel: 'File Name ',
 		                disabled : true,
 		            },{
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	},
 		            	text : ' <fmt:message key="aimir.msg.allfieldrequired"/> ',
 		            }
 		            
 		        ],
 		        buttons: [
 		            {
 		            	id : 'fileform',
			    	 	text: 'Select',
			    	 	// click => AjaxUpload
			    	 	
			        },{
			            text: 'Send',
			            listeners: {
			            	click: function(btn,e){
			            		//submit action
			            		startUpload();
			            	}
			            }
			        },{
			            text: 'Cancel',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('uploadWindowPanel').close();
	                        }
	                    }
		        }]
 		    });
 		    
 		    var uploadWindow = new Ext.Window({
 		        id     : 'uploadWindowPanel',
 		        title  : 'HHU Data Upload',
 		        pageX : 500,
                pageY : 200,
 		        height : 230,
 		        width  : 400,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [uploadFormPanel],
 		    });
 		    
 		    uploadWindow.show();
 		    if(filePath.length > 10){
//  		    	Ext.getCmp('sVendor').setValue('<fmt:message key="aimir.upload.ongoing"/>');
//  		    	Ext.getCmp('sModel').setValue('<fmt:message key="aimir.upload.ongoing"/>');
//  		    	Ext.getCmp('sVendor').disable(true);
//  		    	Ext.getCmp('sModel').disable(true);
 		    	Ext.getCmp('mdsid').setValue(meterSerial);
 		    	Ext.getCmp('filename').setValue(fileName);
 		    	Ext.getCmp('fileform').disable(true);
 		    	getDeviceVendorsBySupplierId();	
 		    }else{
 		    	getFileUploadModule(); 		
 	 		    getDeviceVendorsBySupplierId();	
 		    }
 		    
 	}
 	
 	// 파일 업로드 패널 - Send 버튼 - submit action
 	function startUpload() {
	  	// 입력칸 체크
 		var svnd = Ext.getCmp('sVendor').getValue();
	  	var smod = Ext.getCmp('sModel').getValue();
	  	var smts = Ext.getCmp('mdsid').getValue();
	  	var sfile = Ext.getCmp('filename').getValue();
	  	if("".search(svnd)==0 || "".search(smod)==0 || "".search(smts)==0){
	  		Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>'
	  				,'<fmt:message key="aimir.msg.allfieldrequired"/>');
	  		return;
	  	}else if("".search(sfile)==0){
	  		Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>'
	  				,'<fmt:message key="aimir.alert.selectFile"/>');
	  		return;
	  	}
	  	 	
	  	$.ajax({
	  		url: '${ctx}/gadget/mvm/getExcelResult.do',
	  		method : 'post',
	  		data:{
	  			'loginId' : loginId,
	  			'mdsId'   : smts,
	  			'dataType': dataType,
	  			'filePath': filePath,
	  			'supplierId' : supplierId,
	  			'vendorname'  : svnd,
	  			'modelname'   : smod,
	  			'uploadHistoryId' : uploadHistoryId
	  		},
	  		beforeSend: function(){
	  			Ext.MessageBox.wait('Updating', 'WAIT', {
	  				text: 'Updating...',
	  				scope: this,
	  			});
	  		},
	  		success: function(returnData){
	  			//폼 윈도우를 닫고, 결과 처리
	  			Ext.MessageBox.hide();
	  			Ext.getCmp('uploadWindowPanel').close();
	  			var datas = returnData.result;	  			
	  			if(datas==null){
	  				// 실패 메시지 출력
	  				Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', "<fmt:message key="aimir.hhu.failure"/>");
	  			}else{
	  				// 성공 라인 출력		
		  			var rtMsg = 'Total : ' + datas.totalCnt + '<br>Success : ' + datas.successCnt;
		  			rtMsg = rtMsg.concat('<br>Fail : ' + datas.failCnt + '<br>First Metering Time : ' + datas.startDate);
		  			rtMsg = rtMsg.concat('<br>Last Metering Time : '+ datas.endDate);
		  			Ext.MessageBox.alert('<fmt:message key="aimir.device.RegResult"/>',rtMsg);		  			
	  			}
	  			
	  		},
	  		error: function(){
	  			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', '<fmt:message key="aimir.firmware.msg10"/>');
	  		},
	  		complete: function(){
	  			//성공, 에러 모두 다 끝났으면 자동으로 이력 조회 새로고침
	  			getHistoryGrid();	  			
	  		}
	  	});
 	}
 	
 	// 제조사 리스트 불러오기
 	function getDeviceVendorsBySupplierId() {
        $.getJSON('${ctx}/gadget/system/vendorlist.do', {
            'supplierId' : supplierId
        }, function(returnData) {
            //MOE프로젝트의 경우 LSIS의 모델만 화면에 출력하도록 함 (LS요청사항)
            var deviceVendorObject = null;
            for(var vd in returnData.deviceVendors){
            	if(returnData.deviceVendors[vd].name == "LSIS")
            		deviceVendorObject = returnData.deviceVendors[vd];
            }
            Ext.getCmp('sVendor').bindStore(new Ext.data.JsonStore({
            	fields : ['id', 'name', 'descr', 'code', 'address'],
            	data : deviceVendorObject,
            }));
        });
    };
    
    // 모델 정보 리스트 불러오기
    function getDeviceModelsByVenendorId() {
        if ($('#sVendor').val() != "")
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do', {
                'vendorId' : Ext.getCmp('sVendor').getValue()
            }, function(returnData) {
                //$('#sModel').noneSelect(returnData.deviceModels);
                Ext.getCmp('sModel').bindStore(new Ext.data.JsonStore({
                	fields : ['id', 'name', 'deviceVendor', 'code', 'deviceType'],
                	data : returnData.deviceModels,
                }));
                Ext.getCmp('sModel').setValue('<fmt:message key="aimir.select.devicemodel"/>');
            });
    };
 	
 	
 	// 검색 조건 확인
 	function getHistoryCondition(){
 		var arrayObj = Array();
 		//var sd = $('#historyStartDate').datepicker('getDate');
 		//var ed = $('#historyEndDate').datepicker('getDate');
 		//cache
    	arrayObj[0] = Math.random();
 		//conditions
 		arrayObj[1] = $('#meteridbox').val().trim();
 		arrayObj[2] = $('#loginidbox').val().trim();
 		//arrayObj[3] = new Date(sd).format('Ymd');
 		//arrayObj[4] = new Date(ed).format('Ymd');
 		arrayObj[3] = $('#historyStartDateHidden').val();
 		arrayObj[4] = $('#historyEndDateHidden').val();
 		
 		return arrayObj;
 	}	
 	
 	// 업로드 Grid 호출
 	var historyGridStore;
 	var historyGridCol;
 	var historyGridOn = false;
 	var historyGrid; 	
 	function getHistoryGrid(){
 		var conditionArray = getHistoryCondition(); 
 		$.getJSON('${ctx}/gadget/mvm/getDefaultUploadHistory.do',
 				{ 
 					supplierId : supplierId,
 					meterId : conditionArray[1],
 					loginId : conditionArray[2],
 					startDate : conditionArray[3],
 					endDate : conditionArray[4]
 				},
 		 function (json) {
 					drawHistoryGrid(json.result);		
 					//호출 후 하단 fail list는 다시 숨김
 					$('#uploadFailBodyDiv').hide();
 				}
 		);
 	}
 	
 	// 업로드 이력 Grid 그리기
 	function drawHistoryGrid(_historyData){
 		var gridWidth = $('#historyGridDiv').width();
		historyGridStore = new Ext.ux.data.PagingJsonStore({
			lastOptions:{params:{start: 0, limit: 9}},
            data:_historyData||{},
            root: '',
            fields:[
					{ name : 'uploadid', type: 'String'},
					{ name : 'loginid', type: 'String'},
                    { name : 'uploaddate', type: 'String'},
                    { name : 'meterid', type: 'String'},
                    { name : 'meterreg', type: 'String'},
                    { name : 'datatype', type: 'String'},
                    { name : 'startdate', type: 'String'},
                    { name : 'enddate', type: 'String'},
                    { name : 'cnt', type: 'String'},
                    { name : 'filepath', type: 'String'}
                    ]
		});
		
		historyGridCol = new Ext.grid.ColumnModel({
			columns : [
						{ header : 'Login ID',  dataIndex : 'loginid', width : 8 },
			         	{ header : 'Upload Date', 	dataIndex : 'uploaddate', width : 10 },
			         	{ header : 'Meter ID',  dataIndex : 'meterid', width : 10 },
			         	{ header : 'Meter Registration',  dataIndex : 'meterreg', width : 10 , renderer : meterReg},
			         	{ header : 'Data Type',  dataIndex : 'datatype', width : 10 , renderer : dType},
			         	{ header : 'Start Date',  dataIndex : 'startdate', width : 10 },
			         	{ header : 'End Date',  dataIndex : 'enddate', width : 10 },
			         	{ header : 'Total (Success/Failure)',  dataIndex : 'cnt', width : 10, renderer : cntColor },
			         	{ header : 'File',  dataIndex : 'filepath', width : 9 , renderer : fDownload},
			         	{ header : 'Retry',  dataIndex : '', width : 5, renderer : retryUpload}
			           ],
	        defaults: {
	        	align : 'center',
	        	sortable : true,
	        	menuDisabled : true,
	        	width: ((gridWidth-30)/4)-chromeColAdd
	        } 
		});
		
		function meterReg(val){
			if(val==1)	return '<b style="color:blue;">Y</b>';
			else return '<b style="color:red;">N</b>';
		}
		
		function dType(val){
			if(val==1) return 'Energy Load Profile';
			if(val==2) return 'Daily Load Profile';
			if(val==3) return 'Monthly Load Profile';
			if(val==4) return 'Power Load Profile';
		}
		
		function cntColor(val){
			var mstr = val.split(/[(,),/]/); //정규식 '/','(', ')'  문자로 분리
			if(mstr.length < 2) return val;
			var tot = mstr[0];
			var suc = mstr[1];
			var fai = mstr[2];
			var cell;
			if(fai>0){	
				cell = '<b>'+tot+' ( </b><b style="color:blue;">'+suc+'</b><b> / </b><b style="color:red;">'+fai+'</b><b> )</b>';
			}else cell = '<b>'+tot+' ( </b><b style="color:blue;">'+suc+'</b><b> / '+fai+' )</b>';  
			return cell;
		}
		
		// 업로드했던 파일 다운로드
		function fDownload(val, meta, rec){ 								
			var fPath = val.substr(val.lastIndexOf('temp/'));
			fPath = '${ctx}/'+fPath;			
			return '<div class="am_button"><a href="' + fPath + '"><fmt:message key='aimir.report.fileDownload'/></a></div>'; 	
		}
		
		// 미등록 미터 이력의 재업로드 시도
		function retryUpload(val, meta, rec){
			if(rec.json.meterreg < 1){
				var id = Ext.id();
				var $div = $('<div></div>').attr("id", id);
				var button = function() {
					if($("#" + id).length > 0 && ($div.children().length<1)) {
						new Ext.Button({
							text: "<b><fmt:message key='aimir.button.retry'/></b>",
							width: 60,						
							handler: function(b, e) {
								eventHandler.retryPopup(rec);	
							}				
						}).render(id);
					} else {
						button.defer(100);
					}
				};

				button.defer(100);
				return $div[0].outerHTML;	
			}else if(rec.json.meterreg == '-'){
				var id = Ext.id();
				var $div = $('<div></div>').attr("id", id);
				var button = function() {
					if($("#" + id).length > 0 && ($div.children().length<1)) {
						new Ext.Button({
							text: "<b><fmt:message key='aimir.error'/></b>",
							width: 60,						
							handler: function(b, e) {
								eventHandler.errorPopup(rec);	
							}				
						}).render(id);
					} else {
						button.defer(100);
					}
				};

				button.defer(100);
				return $div[0].outerHTML;
			}
			return null;
		}
 		
		if(historyGridOn == false){
			historyGrid = new Ext.grid.GridPanel({
				id: 'deviceRegLogMaxGrid',
                store: historyGridStore,
                cm : historyGridCol,
                autoScroll: false,
                width: gridWidth,
                height: 320,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(sm, row, rec) {                        	
                            var data = rec.data;
                            //선택한 항목에 해당하는 Grid 출력
                            uploadId = data.uploadid;
                            getUploadFailGrid(uploadId);
                        }
                    }
                }),
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'historyGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 9,
                    store: historyGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
			});
			
			historyGridOn = true;
		} else {
			historyGrid.setWidth(gridWidth);
			historyGrid.reconfigure(historyGridStore, historyGridCol);
			var bottomToolbar = historyGrid.getBottomToolbar();
			bottomToolbar.bindStore(historyGridStore);
		}
 	}
 	
 	// 실패 이력의 세부 데이터를 출력하는 윈도우, 텍스트를 생성 하고 윈도우 호출
 	var detWindow;
 	var detPanel;
 	function makeDetailText(textContent){
 		// hidden element를 초기화
 		$('#uploadFailHiddenDiv').html("");
 		var mdTexts = textContent.split(',');
 		var mdContent = "";
 		for(t=0; t<mdTexts.length; t++){
			mdContent += '<b> * ' + mdTexts[t] + '</b><br>';
 		} 		
 		drawDetailWindow(mdContent);
 	}
 	function drawDetailWindow(mdContent){		
 		// 패널
 		detPanel = new Ext.Panel({ 			
 			autoScroll : true,
 			bodyStyle   : 'padding: 10px 10px 10px 10px;',
 			html : mdContent,
 		});
 		
 		// 출력 윈도우
 		detWindow = new Ext.Window({
 	 		id     : 'detailWindowPanel', 	 		
 	 		applyTo : 'uploadFailHiddenDiv',
 	 		title : '<fmt:message key="aimir.meter.detailinfo"/>',
 		    pageX : 500,
 	        pageY : 200,
 	        height : 350,
 	        width  : 430,
 	        layout : 'fit',
 	        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 	        items  : [detPanel],
 	        buttons : [
       		            {
       		            	id : 'closeWin',
      			    	 	text: 'Close',
      			    	 	listeners: {
    	                        click: function(btn,e) {
    	                        	Ext.getCmp('detailWindowPanel').hide();
    	                        }
    	                    }     			    	 	
      			       	}
     		         ]
 	 	}); 		
 		detWindow.show();
 	}
 	
 
 	// 실패 이력 Grid 호출
 	var uploadFailGridStore;
 	var uploadFailGridCol;
 	var uploadFailGridOn = false;
 	var uploadFailGrid; 	
 	function getUploadFailGrid(_uploadId){
 		var conditionArray = getHistoryCondition(); 
 		$.getJSON('${ctx}/gadget/mvm/getFailedUploadHistory.do',
 				{ 					
 					'uploadId' : _uploadId, 
 					//meterId : conditionArray[1],
 				},
 		 function (json) {
 					$('#uploadFailBodyDiv').show();
 					drawUploadFailGrid(json.result);			
 				}
 		);
 	}
 	
	// 업로드 실패 이력 Grid 그리기
 	function drawUploadFailGrid(_failList){
 		var gridWidth = $('#uploadFailGridDiv').width();
 		uploadFailGridStore = new Ext.ux.data.PagingJsonStore({
			lastOptions:{params:{start: 0, limit: 10}},
            data:_failList||{},
            root: '',
            fields:[
                    { name : 'uploadid', type: 'String'},
                    { name : 'rowline', type: 'String'},
                    { name : 'failreason', type: 'String'},
                    { name : 'meteringtime', type: 'String'},
                    { name : 'mdvalue', type: 'String'}
                    ]
		});
		
 		uploadFailGridCol = new Ext.grid.ColumnModel({
			columns : [
			         { header: 'Line', 			dataIndex: 'rowline',	   width: 4, align: 'center' },
			         { header: 'Fail Reason',   dataIndex: 'failreason',   width: 15 },
			         { header: 'Clock', 		dataIndex: 'meteringtime', width: 10, align: 'center'  },
			         { header: 'Metering Data', dataIndex: 'mdvalue',      width: 95 },
			         
			           ],
	        defaults: {
	        	sortable : true,
	        	menuDisabled : true,
	        	width: ((gridWidth-30)/4)-chromeColAdd
	        },
	        
		});
 		
		if(uploadFailGridOn == false){
			uploadFailGrid = new Ext.grid.GridPanel({
				id: 'deviceRegLogMaxGrid',
                store: uploadFailGridStore,
                cm : uploadFailGridCol,
                autoScroll: false,
                width: gridWidth,
                height: 295,       
                listeners: {
                    rowdblclick: function(grid,index,e) {
                    	makeDetailText(grid.getStore().data.get(index).data.mdvalue);
                    }
                },
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'uploadFailGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>',                    
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: uploadFailGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
			});
			
			uploadFailGridOn = true;
		} else {
			uploadFailGrid.setWidth(gridWidth);
			uploadFailGrid.reconfigure(uploadFailGridStore, uploadFailGridCol);
			var bottomToolbar = uploadFailGrid.getBottomToolbar();
			bottomToolbar.bindStore(uploadFailGridStore);
		}
 	}
	
	//그리드에 있는 button들의 event function 관리
	var eventHandler = {
			retryPopup : function(rec){
				if(rec.json.meterreg > 1) return;
				var tData = rec.json;
				filePath = tData.filepath;
				fileName = filePath.substr(filePath.lastIndexOf('_')+1);
				dataType = tData.datatype;
				meterSerial = tData.meterid; 
				uploadHistoryId = tData.uploadid;
				drawUploadPanel();
			},
			
			errorPopup : function(rec){
				if(rec.json.meterreg > 1) return;
				var tData = rec.json;
				
				Ext.MessageBox.alert('<fmt:message key="aimir.info"/>', "<fmt:message key="aimir.hhu.retryerror"/>");
			},
			
			fileDownload : function(rec){
				
// 				var winObj;
// 				var tData = rec.json;
// 				filePath = tData.filepath;
// 				fileName = filePath.substr(filePath.lastIndexOf('_')+1);
				
// 				var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
// 	            var obj = new Object();
// 	            obj.callType = "hhuFile"; //hhuExcel과 구분	            
// 	            obj.filePath = filePath;	            
// 	            obj.fileName = fileName;
	            
// 	            if(winObj) winObj.close();
	            
// 	            winObj = window.open("${ctx}/gadget/mvm/hhuFileDownloadPopup.do", "hhuFile", opts);
// 	            winObj.opener.obj = obj;
	            
			}
	}
	
	var getFileUploadModule = function(){		
		// 파일 업로드 관리, SELECT 버튼
		new AjaxUpload('fileform', {
            action: '${ctx}/gadget/mvm/getTempFileName.do',
            data : {
            },
            responseType : false,
            onSubmit : function(file , ext){
            	var mdsVal = $('#mdsid').val();
				// 값이 유효한지 확인하는 부분
				// do something-
				
                // Allows only xls|xlsx.
                if (ext && /^(xls|xlsx)$/.test(ext)){
                    /* Setting data */
                    this.setData({
                        //'key': 'This string will be send with the file'
                        // request.getParameter를 통해 컨트롤러에서 확인
                        'mdsid': mdsVal
                    });

                } else {
                	$('#infolabel').text(' * <fmt:message key="aimir.wrong.fileType.excel"/> [xls/xlsx]')
                    return false;
                }
            },
            onComplete : function(file, response){
                var datas = response.split('|');
                $('#filename').val(file);                
                //OK 하기 전에 이름,경로 저장
                filePath = datas[0].trim();           		
           		dataType = datas[1].trim();
           		fileName = datas[2].trim();
           		meterSerial = datas[3].trim();
           		$('#infolabel').text(' * Data Type : '+dataType);           		
           		Ext.getCmp('mdsid').setValue(meterSerial);
           		Ext.getCmp('mdsid').disable(true);
            }
        });
	}


</script>
<div id="wrapper" class="max">
	
	<!-- 검색 조건 시작 -->
	<div id="uploadSearchDiv" class="search-bg-withouttabs">
	<div class="searchoption-container">	    	
		<table class="searchoption wfree">
			<tr>
				<td class="blue12pt padding-r10px"><fmt:message key="aimir.commlog.datefile"/></td>
				<td><span class="btn_bluegreen margin-r5">
					<a href="javascript:uploadPopup()" id="showUploadPanel"><fmt:message key="aimir.view.mcu18"/></a></span>
				</td>				
			</tr>
			<tr>
				<td class="blue12pt padding-r10px"><fmt:message key="aimir.meterid"/></td>
				<td class="padding-r10px"><span>
					<input type="text" id="meteridbox" name="meteridbox" style="width:120px"/></span>
					</td>
				<td class="blue12pt padding-r10px"><fmt:message key="aimir.loginId"/></td>
				<td class="padding-r10px"><span>
					<input type="text" id="loginidbox" name="loginidbox" style="width:120px"/></span>
					</td>
				<td class="blue12pt padding-r10px"><fmt:message key="aimir.period"/></td>
				<td><input id="historyStartDate" class="day" type="text"></td>	
				<td><input value="~" class="between" type="text"></td>		
				<td class="padding-r10px"><input id="historyEndDate" class="day" type="text"></td>
				<td class="padding-r10px">
					<span class="am_button">
					<a href="javascript:historySearch()"><fmt:message key="aimir.button.search"/></a>
					</span>
					</td>
				<td class="padding-r10px">
					<span class="am_button">
					<a href="javascript:clearSearchOptions()"><fmt:message key="aimir.button.initialize"/></a>
					</span>
					</td>	
										
			</tr>
		</table>
	</div>
	</div>
	<span id="historyStartDateHidden" style="visible:hidden;"></span>
	<span id="historyEndDateHidden" style="visible:hidden;"></span>
	
	<!-- 검색 조건 끝 -->
	
	
	<!-- 업로드 이력 시작 -->
	<div class="gadget_body"> <!-- 나중에 메시지 처리 -->		
		
		<label class="check margin-t2px">Upload History</label>
		
		<span class="am_button" style="float:right; margin-right:2px;">
        <a href="javascript:historyExcel()"><fmt:message key="aimir.button.excel"/></a>
        </span>
        <br><br>
				
		<div id="historyGridDiv" class="margin-t2px"></div>
		
    </div>
    <div class="dashedline"></div>
    <div id="uploadFailBodyDiv" class="gadget_body">
    	<label class="check margin-t2px">Failed Line List</label>
    	<label class="margin-r5px margin-t2px blue12pt">(Double-click an item you want to get detail information)</label>
    	<br><br>
		<div id="uploadFailGridDiv"></div>
    </div>
    <div id="uploadFailHiddenDiv"></div>
	<!-- 업로드 이력 끝 -->
	
	<!-- 파일 업로드 화면 시작 (floating window) -->
	<div id="uploadWindowDiv">
		
	</div>
	<!-- 파일 업로드 화면 끝 -->
	
			
</div>
</body>
</html>



