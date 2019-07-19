
<!--
팝업 : OTA 실행
가젯 : Firmware Operation Management Gadget For S-Project
설명 : 펌웨어 가젯에서 OTA를 실행하는 팝업 화면
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
<title>Execute OTA</title>

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
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	
</head>

<body>
<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">
	
	// OTA 파라미터
	var supplierId = "";
	var loginId ="";
	var deviceModel = "";
	var equip_kind  = "";
	var deviceIdArray;
	var deviceIdString;
	var deviceCount = 0;
	var fileInfo;
	var modelName="";
	var locationId="";
	var targetDeviceType="";
	
	// Display 정보
	var pageWidth = "";
	var pageHeight = "";
	
	// 그리드 항목 선택 여부(버튼 실행 조건)
	var isItemSelected = false;
	
	//글자색
    var red = '#F31523'; // security error
    var setColorFrontTag = "<b style=\"color:";
    var setColorMiddleTag = ";\">"; 
    var setColorBackTag = "</b>";

    // EXTJS AJAX - Timeout setting (180 seconds)
    var extAjaxTimeout = 240000;
		
	/**
	 * 공통 모듈
	 */
	$(document).ready(function () {
		$.ajaxSetup({
	        async: false
	    });
		// 유저 세션 정보 가져오기
	    $.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    
	                    // OTA 권한 여부 확인
	                    // ...
	                }
	            }
	    );
	   
	    // data
	    var obj = window.opener.obj;
	    // base
	    pageWidth = obj.pageWidth-40;
	    pageHeight = obj.pageHeight*0.55;
	    // ota info
	    deviceModel = obj.deviceModel;
	    equip_kind = obj.equip_kind
	    deviceIdArray = new Array();
	    if(obj.deviceIdArray != null)
	    	deviceIdArray = (obj.deviceIdArray).filter(Boolean);
	    deviceCount = obj.condition;
	    deviceIdString = obj.deviceIdString;
	    loginId = obj.loginId;
	    modelName = obj.modelName;
	    locationId = obj.locationId;
	    targetDeviceType = obj.targetDeviceType;
	 	// 버튼 초기화
	 	btnInit();
	    /* $('#takeOver').hide();
	    $('#bypass').hide();
	    $('#checkTakeOver').attr('checked', true); */

	    /* if(equip_kind == "modem" || equip_kind == "meter"){
	    	$('#takeOver').show();
	    	$('#bypass').show();
	    } */
	    // grid 호출
	    getOtaListGrid();
	    $("#deviceType").selectbox();
	    if(equip_kind == "modem" || equip_kind == "meter"){
	    	$('.search-ota-basic').hide();
	    }
	    	 		    
	});
	
	function btnInit(){
		// 버튼 출력 전환
    	$('#tdExecute').css('display','');    	    	
	}
	
	/**
	* OTA 가능한 펌웨어 리스트
	*/
	var otaListStore;
	var otaListCol;
	var otaListGrid;
	
	var getOtaListGrid = function(){
		//var conditionArray = getSearchCondition();
		
		if (equip_kind == "modem" || equip_kind == "meter") {
			targetDeviceType = equip_kind;
		}
		
        var pageSize = 30;
        otaListStore = new Ext.data.JsonStore({
        	autoLoad : true,
            url: "${ctx}/gadget/device/firmware/getFirmwareFileList.do",
            baseParams : {
                'supplierId' : supplierId,  
                'equip_kind' : targetDeviceType, // mcu, modem, meter
                'fileName' : "",
                'modelName' :  modelName,
                'modelId' : deviceModel,
                'fwVer' : ""
            },
            root: 'rtnStr',
            fields: [
                     'no', 'filename','modelname','hwver','fwver','fwrev','creationdate', 'creator',
                     'modelId','checkSum','crc','imageKey','filePath', 'fileUrlPath', 'firmwareId', 'fileExists', 'fId'
                     ],
        });
        
        otaListCol = new Ext.grid.ColumnModel({
            columns: [
                      {header: 'No', dataIndex: 'no', width: 40},
                      {header: 'fId', dataIndex: 'fId', hidden: true},
                      {header: 'Date of Creation', dataIndex: 'creationdate', },
                      {header: 'File Name', dataIndex: 'filename', },
                      {header: 'FW Ver.', dataIndex: 'fwver', },
                      {header: 'Model', dataIndex: 'modelname', },
                      {header: 'Manufacturer', dataIndex: 'creator', },
                      {header: 'File Status', dataIndex: 'fileExists', 
                    	  renderer : function(value, me, record, rowNumber, columnIndex, store){
                    		  if(value == "No File")
                    			  return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                    		  else
                    			  return value;
                    	  }
                      },
                  ],
                   defaults: {
                	   
                	   align : 'center',
                	   sortable : false,
                       menuDisable : true,                       
                       //width : 120
                  },
         });
        
        otaListGrid = new Ext.grid.GridPanel({
            store : otaListStore,
            colModel : otaListCol,
            sm : new Ext.grid.RowSelectionModel({
    			singleSelect:true,
    			listeners: {
                    rowselect: function(smd, row, rec) {
                    	var data = rec.data;
                        // 선택한 항목에 해당하는 내용 출력
                        otaItemSelected(data);
                        //ibk
                        /* if(rec.data.fileExists == "No File"){
                        	$('#tdExecute').hide();
                        	$('#tdConfirm ').hide();
                        	$('#tdCancel').hide();
                        } 
                        
                        else */
                        	$('#tdExecute').show();

                        // 버튼 기능 활성화
                        isItemSelected = true;
                        fileInfo = data;
                    }
                }
    		}),
            autoScroll : false,
            height : pageHeight,
            width : pageWidth,
            stripeRows : true,
            columnLines : true,
            loadMask : {
                msg : 'Loading...'
            },
            viewConfig: {
                forceFit : true,
                enableRowBody : true,
                showPreview : true,
                emptyText : 'No data to display'
            },
            
            //paging bar
        });
        
        $('#otaListGridDiv').html(' ');
        
        otaListGrid.reconfigure(otaListStore, otaListCol);
        otaListGrid.render('otaListGridDiv');        
                
	} // 끝; 펌웨어 리스트 그리드
	
	// 펌웨어 리스트 항목 선택 이벤트
	function otaItemSelected(data) {
		//
var fw_version = data.hwver==""?'-':data.fwver;
		
		// set value
		$('#tdCreator').val(data.creator);		// 제조사	
		$('#tdModelName').val(data.modelname);	// 모델명
		$('#tdVersion').val(fw_version);		// F/W 버전
		$('#tdFileName').val(data.filename);	// 파일명
		
		// set text
		$('#tdCreator').text($('#tdCreator').val().trim());	
		$('#tdModelName').text($('#tdModelName').val().trim());
		$('#tdVersion').text($('#tdVersion').val().trim());
		$('#tdFileName').text($('#tdFileName').val().trim());
				
	}
	
	
	
	
	function checkDeviceType(deviceId){
		var params = {
				deviceId : deviceId
        };
		
		$.post("${ctx}/gadget/device/checkDeviceType.do",
                params,
                function(json) {
                    var type = json.result;
                    if (type != null && type != "") {
                    	modemType = type;
                    }else{
                    }
                }
        );
	}
	
	function checkDeviceType2(deviceId){
		var params = {
				deviceId : deviceId
        };
		
		$.post("${ctx}/gadget/device/checkDeviceType_meter.do",
                params,
                function(json) {
                    var type = json.result;
                    if (type != null && type != "") {
                    	modemType = type;
                    }else{
                    }
                }
        );
	}
	
	 var modemType;
	 var otaExecuteFormPanel;
	 var otaExecuteWin;
	 var target;
	// Editing 패널 버튼 이벤트
    function editingButtonClick(eBtn){

		if(!isItemSelected){
			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>'
	  				,'no firmware file selected.');
			return;	
		}
    	
		// 아직 안닫힌 경우 기존 창은 닫기
        if(Ext.getCmp('otaExecuteWindow')){
        	Ext.getCmp('otaExecuteWindow').close();
        }
		
        if(equip_kind == "modem"){
        		checkDeviceType(deviceIdString);
        }else if(equip_kind == "meter"){
        		checkDeviceType2(deviceIdString);
		}
        
		
		
		var TargetList = new Array();
		var deviceIdList = new Array();
		deviceIdList.push(deviceIdString);
		var Target ={
				locationId : +locationId+"",
				deviceIdList : deviceIdList
		};
		
		TargetList.push(Target);
		//TargetList.push(Target);
		target = JSON.stringify(TargetList);
                
        if(equip_kind=='dcu' || equip_kind=='dcu-kernel' || equip_kind=='dcu-coordinate'){
        	otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                }
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "RF" && equip_kind=='modem'){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                    	{boxLabel: 'Clone OTA  ', name: 'radio-action2', inputValue:'0' },
	                        {boxLabel: 'By DCU', name: 'radio-action2', inputValue:'1', checked: true  },
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2'  }
	                    ]
	                },
	                {
	                    xtype     : 'label',
	                    text      : '\n',
	                },
	                {
	                    xtype     : 'label',
	                    text      : '“Clone OTA” is executed in Coordinator. (DCU v1.2 / Coordinator v1.32 or over.(RF))',
	                } 
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "Ethernet" && equip_kind == "modem"){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                        /* {boxLabel: 'By DCU', name: 'radio-action2', inputValue:'1'  }, */
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled : true  }
	                    ]
	                }
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "MBB" && equip_kind == "modem"){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                        /* {boxLabel: 'By DCU', name: 'radio-action2', inputValue:'1'  }, */
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled : true  }
	                    ]
	                },
	                {
	                    xtype : 'checkbox',
	                    id : 'otaViaUploadChannel',
	                    labelSeparator: '',
	                    hideLabel: true,
	                    boxLabel: 'Asynchronously via the upload channel ',
	                    fieldLabel : 'Asynchronously via the upload channel '
	                },
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "RF" && equip_kind=='meter'){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                    	{boxLabel: 'Clone OTA  ', name: 'radio-action2', inputValue:'0' },
	                        {boxLabel: 'By Modem', name: 'radio-action2', inputValue:'3' },
	                        {boxLabel: 'By DCU', name: 'radio-action2', inputValue:'1', checked: true  },
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2'},
	                    ]
	                },
	                {
	                    xtype     : 'label',
	                    text      : '\n',
	                },
	                {
	                    xtype     : 'label',
	                    text      : '“Clone OTA” is executed in Coordinator. (DCU v1.2 / Coordinator v1.32 or over.(RF))',
	                }
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "MBB" && equip_kind == "meter"){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                    	/* {boxLabel: 'By Modem', name: 'radio-action2', inputValue:'3', checked: true  }, */
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true}
	                    ]
	                },
	                {
	                    xtype : 'checkbox',
	                    id : 'otaViaUploadChannel',
	                    labelSeparator: '',
	                    hideLabel: true,
	                    boxLabel: 'Asynchronously via the upload channel ',
	                    fieldLabel : 'Asynchronously via the upload channel '
	                },
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}else if(modemType == "Ethernet" && equip_kind == "meter"){
    		otaExecuteFormPanel = new Ext.FormPanel({
	            id : 'otaExecuteform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 100,
	            items : [
	                {
	                    xtype : 'radiogroup',
	                    id : 'executeTimeOpt',
	                    fieldLabel : 'Execute Time ',
	                    items : [
	                        {boxLabel: 'Run Now', name: 'radio-action1', inputValue:'RunNow', checked: true},
	                        {boxLabel: 'Time Setting', name: 'radio-action1', inputValue:'SET'  }
	                    ],
	                    listeners :{
	                        change: function(thisRadioGroup, checkedItem){
	                            if(checkedItem.inputValue=='SET'){
	                                //button show
	                                Ext.getCmp('otaDate').setValue("");
	                                Ext.getCmp('otaDate').enable();
	                                Ext.getCmp('otaHour').enable();
	                                Ext.getCmp('otaMin').enable();
	                            }else{
	                                //button hide
	                                Ext.getCmp('otaDate').setValue("Run Now");
	                                Ext.getCmp('otaDate').disable();
	                                Ext.getCmp('otaHour').disable();
	                                Ext.getCmp('otaMin').disable();
	                            }
	                        }
	                    },
	                }, //xtype : radio
	                {                  
	                    xtype: 'datefield',
	                    id : 'otaDate',
	                    fieldLabel: 'Date',
	                    format: 'm/d/Y',
	                    altFormats: 'm/d/Y',
	                    disabled : true,
	                    submitFormat:'ymd'

	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaHour',
	                    width : 30,
	                    fieldLabel: 'Hour ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'otaMin',
	                    width : 30,
	                    fieldLabel: 'Min ',
	                    value: '00',
	                    disabled : true,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCount',
	                    width : 200,
	                    fieldLabel: 'Retry Count ',
	                    value: 2,
	                },
	                {
	                    xtype: 'textfield',
	                    id : 'retryCycle',
	                    width : 200,
	                    fieldLabel: 'Retry Cycle(H) ',
	                    value: 3,
	                },
	                {
	                    xtype : 'radiogroup',
	                    id : 'otaExecuteType',
	                    fieldLabel : 'Execute Type ',
	                    items : [
	                    	/* {boxLabel: 'By Modem', name: 'radio-action2', inputValue:'3', checked: true  }, */
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true}
	                    ]
	                }
	            ], // items
	            buttons : [
	                {
	                    id: 'otaExecuteBtn',
	                    text: 'Execute',
	                    listeners: {
	                        click: function(btn,e){
	                            //submit action
	                            otaStart();
	                        }
	                    }
	                }, {
	                    text: 'Cancel',
	                    listeners: {
	                        click: function (btn, e) {
	                            Ext.getCmp('otaExecuteWindow').close();
	                        }
	                    }
	                }
	            ] //buttons
	        });
    	}


        var height = 285;
        var width  = 360;
        var pageX  = ($('#otaListGridDiv').width())*0.3;
        
        if(modemType == "RF"){
        	//height = 310;
        	height = 330;
        	width  = 570; 
        	pageX  = ($('#otaListGridDiv').width())*0.2;
        }else if(modemType == "MBB"){
        	height = 305;
            width  = 360;
            pageX  = ($('#otaListGridDiv').width())*0.3;
        }
        
        if(targetDeviceType =="dcu"){
        	height = 260;
        	width  = 360;
        }
        
        var otaExecuteWin = new Ext.Window({
            id     : 'otaExecuteWindow',
            title : 'Execute OTA',
            pageX : pageX,
            pageY : 150,
            height : height,
            width  : width,
            modal  : true,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [otaExecuteFormPanel],
        });

        otaExecuteWin.show();
    }
    
    
    var extAjaxTimeout = 240000;
    function otaStart(){
   	 var executeTimeOpt = Ext.getCmp('executeTimeOpt').getValue().inputValue;
   	 var isImmediately ="true"; 
   	 if(executeTimeOpt == 'RunNow')
   		 isImmediately = "true";
   	 else
   		 isImmediately = "false";
   	 
   	 var otaExecuteTime=""
        
   	if(isImmediately == "false"){
	    	 otaExecuteTime = Ext.getCmp('otaDate').getValue().format('Ymd') 
	         + Ext.getCmp('otaHour').getValue() 
	         + Ext.getCmp('otaMin').getValue();
   	}
     
         var otaRetryCount  = Ext.getCmp('retryCount').getValue();
	     var otaRetryCycle  = Ext.getCmp('retryCycle').getValue();
	     
	     var otaExecuteType ="";
	     if(modemType == "Ethernet" || modemType == "MBB" || modemType == "RF"){
			otaExecuteType = Ext.getCmp('otaExecuteType').getValue().inputValue;
	     }
	     
	     var otaViaUploadChannel="false"
	     if(modemType == "MBB"){
	    	 if($("input:checkbox[id='otaViaUploadChannel']").is(":checked") == true)
	    		 otaViaUploadChannel ="true";
	    	 else
	    		 otaViaUploadChannel ="false";
	     } 
   		// Confirm
   		Ext.Msg.confirm('<fmt:message key="aimir.warning"/>', '<fmt:message key="aimir.update.want" />', 
   			function(btn,text){
		    		if(btn == 'yes'){
		    			// OTACmdController에 있는 targetType과 맞춰주기위해
		    			var targetDeviceType2 = equip_kind.toUpperCase(); 	
		    			if(targetDeviceType2 == "DCU-KERNEL"){
		    				targetDeviceType2 = "DCU_KERNEL";
		    			} else if(targetDeviceType2 == "DCU-COORDINATE"){
		    				targetDeviceType2 = "DCU_COORDINATE";
		    			}
		    			
		    			if(targetDeviceType2 == 'DCU' || targetDeviceType2 == 'DCU_KERNEL' || targetDeviceType2 == 'DCU_COORDINATE'){
		    				otaExecuteType = 1;
		    			}
		    			
		    			/********************** OTA시 넘겨줄 수 있는 정보들 입니다. ***********************/
		    			var info="";
		        			info = "Debugging용 Alert입니다. "	
		        			+ "\nSupplier ID: " 		+ supplierId                   // supplierId
		        			+ "\nlogin ID: "			+ loginId					   // loginId
		        			//+ "\nModel Id: " 			+ modelId				       // 모델 id
		        			+ "\nDevice Type: " 		+ targetDeviceType2		       // 기기 종류(mcu, modem, meter)
		        			//+ "\nDevice Count: " 		+ deviceCount				   // 기기 수
		        			//+ "\nDevice ID String: " 	+ deviceIdString 			   // ID 값들을 (id,id,id...)형식으로
		        			//+ "\nLocaiont ID: " 	    + locationId     			   // Location ID
		        			+ "\nota Execute Type: " 	+ otaExecuteType     			   
		        			+ "\nisImmediately: " 	    + isImmediately     			  
		        			+ "\notaExecuteTime: "      + otaExecuteTime     			   
		        			+ "\notaRetryCount: " 	    + otaRetryCount     			   
		        			+ "\notaRetryCycle: " 	    + otaRetryCycle  
		        			+ "\notaViaUploadChannel: " + otaViaUploadChannel
		        			+ "\ntarget: "              + target
		        	    /* for(var i = 0 ; i < deviceCount ; i++){
		        	    	info += "\ndeviceIdArray[" + i + "] :" + deviceIdArray[i]; // ID 값들을 배열에
		        	    } */
		        			/* + "\nFile Path: " 			+ fileInfo.filePath			   // FILE PATH
		        			+ "\nImage Key: " 			+ fileInfo.imageKey            // Image Key
		        			+ "\nCRC: " 				+ fileInfo.crc                 // CRC
		        			+ "\nCheck Sum: " 			+ fileInfo.checkSum            // Check Sum
		        			+ "\nFile URL: " 			+ fileInfo.fileUrlPath         // File URL */
		        			+ "\nFile ID: " 			+ fileInfo.fId                 // File URL

		        			
		    			//alert(info);
		    			/********************** OTA시 넘겨줄 수 있는 정보들 입니다. ***********************/
						
		    			
		    			//동적으로 할때는 var deviceIdList = new Array(); 배열만들어서  push로 넣으면 될듯... 여러개 일떄는 
		    			/* var TargetList = new Array();
		    			var Target ={
		    					locationId : locationId,
		    					deviceIdList : deviceIdList
		    			};
		    			
		    			TargetList.push(Target);
		    			//TargetList.push(Target);
		    			
		    			var target = JSON.stringify(TargetList);
		    			alert(target); */
		    			
		    			// OTA 실행 
		    			Ext.Msg.wait('Waiting for response.', 'OTA');
		    			Ext.Ajax.request({
		    				url : '${ctx}/gadget/device/command/cmdOTAStart.do', // OTA 실행 URL 넣어야함
		    		        method : 'POST',
		    		        timeout : extAjaxTimeout,
		    		        params : {
		    		        	loginId 	: loginId,
		    		        	target      : target,
		    		        	//deviceId 	: deviceIdString, 			   // ID 값들을 (id,id,id...)형식으로
		    					targetType 	: targetDeviceType2,
		    					//locationId  : locationId,
		    					fId         : fileInfo.fId,
		    					otaExecuteType: otaExecuteType, //0(CLONE_OTA), 1(EACH_BY_DCU), 2(EACH_BY_HES)
		    					otaExecuteTime: otaExecuteTime,
		    					isImmediately : isImmediately,
		    					otaRetryCount : otaRetryCount,
		    					otaRetryCycle : otaRetryCycle,
		    					otaViaUploadChannel : otaViaUploadChannel
		    		        },
		    		        success : function(result){
		    		        	Ext.MessageBox.hide();
                               var jsonData = Ext.util.JSON.decode(result.responseText);
                               var resultMessage = jsonData.rtnStr;
                               Ext.Msg.alert('<fmt:message key="aimir.message"/>',resultMessage);
		    				},
		    		        failure : function(){
		    					Ext.MessageBox.hide();
		    					Ext.Msg.alert('<fmt:message key="aimir.message"/>','Failed to Ajax Communication');
		    				}
		    		        });
		    		}else {
		    			Ext.MessageBox.hide();
		    			Ext.Msg.alert('<fmt:message key="aimir.warning"/>','<fmt:message key="aimir.canceled" />');
		    			
		    		}
		    	});        		
    }
	
    function changeDeviceType() { 
		targetDeviceType = $('#deviceType').val();	// 항목 변경시 선택된 값을 보관
		equip_kind = $('#deviceType').val();
		$('#otaBtn').hide();
		$('#fwUpdateButton').hide();
		$('#fwDeleteButton').hide();
		
		$('#tdCreator').text("");
		$('#tdModelName').text("");
		$('#tdVersion').text("");
		$('#tdFileName').text("");
		
		modelId = "00";
		chkCount = 0;
		
		//if (chkLocation != 0) {
			getOtaListGrid();			
		//}
	}
    

</script>
</body>
<div id="wrapper" class="max">
	
	<!-- title -->
	<div class="search-bg-basic">
		<ul class="basic-ul">   
			<li class="basic-li bluebold11pt withinput">OTA</li>                
        </ul>        
	</div>
	
	<!-- available firmware list grid -->
	<div>
		<table>
			<tr>
				<td>
					<div class="search-ota-basic">					
						<span class="withinput"> 
							<fmt:message key="aimir.targetType" />
						</span> 
						<span>
							<select id="deviceType" name="deviceType" class='selectbox' style="width: 130px;" onchange="javascript:changeDeviceType();">
								<option value="dcu" selected><fmt:message key="aimir.mcu" /></option>
								<option value="dcu-kernel">DCU Kernel</option>
								<option value="dcu-coordinate">DCU Coordinator</option>
							</select>
						</span>
					</div>	
				</td>
			</tr>
			<tr>
				<td>
					<div id="otaListGridDiv" class="margin10px">
						The Grid will appear within here. 
					</div>
				</td>
			</tr>
		</table>
	
	</div>
	
	<div id="parameterDiv" class="margin10px padding-b10px border_blu">
		<table class="wfree margin10px">
			<tr>
				<td class="graybold11pt withinput">Manufacturer  </td><td class="padding-r20px withinput"><label id="tdCreator" style="width:150px">-</label></td>
			</tr>
			<tr>
				<td class="graybold11pt withinput">Model Name  </td><td class="padding-r20px withinput"><label id="tdModelName" style="width:150px">-</label></td>
			</tr>
			<tr>
				<td class="graybold11pt withinput">F/W Version  </td><td class="padding-r20px withinput"><label id="tdVersion"  style="width:150px">-</label></td>				
			</tr>
			<tr>
				<td class="graybold11pt withinput">File Name  </td><td class="padding-r20px withinput"><label id="tdFileName"  style="width:150px">-</label></td>							
			</tr>
		</table>
		<table>
		<!-- <tr id="takeOver">
		<td><input type="checkbox" id="checkTakeOver" class="transonly margin-l10"/><em class="bluebold11pt">Continue downloading the data from where it stopped</em></td>
		</tr>
		<tr id="bypass">
		<td><input type="checkbox" id="checkBypass" class="transonly margin-l10"/><em class="bluebold11pt">Directly</em></td>
		</tr> -->
		</table>
		
		
	
		</div>
	
	<!-- confirm button -->
	<div class="margin10px">	
		<em id="tdExecute" class="am_button"><a href="javascript:editingButtonClick('Execute')"  class="on"><fmt:message key='aimir.execute'/></a></em>
	</div>
</div>
</html>