
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
	var modelId    = "";
	var locationId = "";
	var loginId    = "";
	var fwVersion  = "";
	var maxNumber  = 250000;
	var chkCount = 0;
	var dcuName ="";
	var targetDeviceType ="";
	var fileInfo;
	var otaExecuteType="";
	var deviceIdArray = new Array(0);
	var deviceCount=0;
	var deviceIdString="";
	// EXTJS AJAX - Timeout setting (180 seconds)
    var extAjaxTimeout = 240000;
	var searchDeviceType ="RF";
	var installStartDate  = "";
	var installEndtDate   = "";
	var lastCommStartDate = "";
	var lastCommEndDate   = "";
	var hwVer   		  = "";
    var pageSize = 300;
	
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
	                    loginId = json.loginId;
	                    // OTA 권한 여부 확인
	                    // ...
	                }
	                
	                if(json.maxMeters != null){
                    	maxNumber = json.maxMeters;
                    	if(maxNumber == 0)
                    		maxNumber = 250000;
                    }else{
                    	//maxMeters가 null 일때 처리
                    	maxNumber = 250000;
                    }
	            }
	    );
	   
	    // data
	    var obj = window.opener.obj;
	    // ota info
	    modelId           = obj.modelId;
	    locationId        = obj.locationId;
	    fwVersion         = obj.fwVersion;
	    targetDeviceType  = obj.targetDeviceType;
	    fileInfo          = obj.fileInfo;
	    deviceId          = obj.deviceId;
	    dcuName           = obj.dcuName;
	    searchDeviceType  = obj.searchDeviceType
	    installStartDate  = obj.installStartDate;
		installEndtDate   = obj.installEndtDate;
		lastCommStartDate = obj.lastCommStartDate;
		lastCommEndDate   = obj.lastCommEndDate;
		hwVer   		  = obj.hwVer;
		
	    if(dcuName==""){
	    	dcuName ="-"
	    }
	    
	    if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel')
	    	getDeviceListGrid();
	    else if(targetDeviceType=='dcu-coordinate')
	    	getCodiListGrid();
	    else if(targetDeviceType=='modem')
	    	getModemListGrid();
	    else if(targetDeviceType=='meter'){
	    	getMeterListGrid();
	    }
	    
	});
	
	var locationFirstCheck = true;
	
	// 장비 리스트 그리드(MCU)
	var deviceListStore;
	var deviceListCol;
	var deviceListGrid;
	var deviceListGridOn = false;
	var getDeviceListGrid = function(){
	var grWidth = 600;
        deviceListStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getDcuGridData.do", //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
                supplierId : supplierId,
                mcuId : deviceId,
                mcuType : '',
                locationId : locationId,
                swVersion : fwVersion,
                swRevison : ''/* fwRevison */,
                hwVersion : hwVer/* hwVersion */,
                installDateStart : installStartDate,
                installDateEnd : installEndtDate,
                lastcommStartDate:lastCommStartDate,
            	lastcommEndDate:lastCommEndDate,
                filter : '',
                order : '',
                protocol : '',
                dummy : '',
            	mcuStatus : '',
            	mcuSerial : '',
            	modelId : modelId,
            	fwGadget: 'Y'
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ['sysID','model','sysHwVersion','sysSwVersion','swrev','mcuId', 'lastCommDate'],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
                
            }
        	    
        });
        deviceListCol = new Ext.grid.ColumnModel({
            columns: [
					{
					    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
		            	width: 30,
		            	align:'center',
		            	renderer: dataChk 
					},
					{header: 'Device ID', dataIndex: 'sysID', 
					 renderer : function(val){
						 chkCount++;
						 return val;	 
					 }
					},                      
					{header: 'Model', dataIndex: 'model'},
					/* {header: 'HW Version', dataIndex: 'sysHwVersion', }, */
					{header: 'FW Version', dataIndex: 'sysSwVersion'},
					/* {header: 'FW Revision', dataIndex: 'swrev', },    */   
                    {header: 'Last Comm. Date', dataIndex: 'lastCommDate'}
                  ],

                   defaults: {
                       sortable : false,
                       menuDisable : true,
                       hideable : false,
                       align : 'center',
                       width : 120
                  },
         });
        
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {   
            return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.sysID + "\" /></div>";
        }
        
        if(!deviceListGridOn){
        	deviceListGrid = new Ext.grid.GridPanel({
                store : deviceListStore,
                cm : deviceListCol,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                        rowselect: function(smd, row, rec) {
                        	var data = rec.data;
                            //선택한 항목에 해당하는 내용 출력
                            //eventItemSelected(data);
                        }
                    }
        		}),
                autoScroll : true,
                height : 500,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                //paging bar
                bbar: new Ext.PagingToolbar({
                pageSize: pageSize,
                store: deviceListStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
                })  
            });
        	
        	$('#deviceListGridDiv').html(' ');
            
            //deviceListGrid.reconfigure(deviceListStore, deviceListCol);
            deviceListGrid.render('deviceListGridDiv');
            deviceListGridOn = true;
            /* modemListGridOn = false;
            meterListGridOn = false; */
        }else {
        	deviceListGrid.reconfigure(deviceListStore, deviceListCol);
        	/* modemListGridOn = false;
        	meterListGridOn = false; */
        }
	} // 끝; 장비 리스트 그리드 (MCU)
	
	// 장비 리스트 그리드(MCU-Codi)
	var codiListStore;
	var codiListCol;
	var codiListGrid;
	var codiListGridOn = false;
	var getCodiListGrid = function(){
	var grWidth = 600;
		codiListStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getDcuCodiGridData.do", //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
                supplierId : supplierId,
                mcuId : deviceId,
                mcuType : '',
                locationId : locationId,
                swVersion : fwVersion,
                swRevison : ''/* fwRevison */,
                hwVersion : hwVer/* hwVersion */,
                installDateStart : installStartDate,
                installDateEnd : installEndtDate,
                lastcommStartDate:lastCommStartDate,
            	lastcommEndDate:lastCommEndDate,
                filter : '',
                order : '',
                protocol : '',
                dummy : '',
            	mcuStatus : '',
            	mcuSerial : '',
            	modelId : modelId,
            	fwGadget: 'Y'
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ['sysID','model','sysHwVersion','sysSwVersion','swrev','mcuId', 'lastCommDate', 'codiFwVer'],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
                
            }
        	    
        });
		codiListCol = new Ext.grid.ColumnModel({
            columns: [
					{
					    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
		            	width: 30,
		            	align:'center',
		            	renderer: dataChk 
					},
					{header: 'Device ID', dataIndex: 'sysID', 
					 renderer : function(val){
						 chkCount++;
						 return val;	 
					 }
					},                      
					{header: 'Model', dataIndex: 'model'},
					/* {header: 'HW Version', dataIndex: 'sysHwVersion', }, */
					{header: 'FW Version', dataIndex: 'sysSwVersion'},
					{header: '(Codi) FW Version', dataIndex: 'codiFwVer'},
					/* {header: 'FW Revision', dataIndex: 'swrev', },    */   
                    {header: 'Last Comm. Date', dataIndex: 'lastCommDate'}
                  ],

                   defaults: {
                       sortable : false,
                       menuDisable : true,
                       hideable : false,
                       align : 'center',
                       width : 120
                  },
         });
        
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {   
            return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.sysID + "\" /></div>";
        }
        
        if(!codiListGridOn){
        	codiListGrid = new Ext.grid.GridPanel({
                store : codiListStore,
                cm : codiListCol,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                        rowselect: function(smd, row, rec) {
                        	var data = rec.data;
                            //선택한 항목에 해당하는 내용 출력
                            //eventItemSelected(data);
                        }
                    }
        		}),
                autoScroll : true,
                height : 500,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                //paging bar
                bbar: new Ext.PagingToolbar({
                pageSize: pageSize,
                store: codiListStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
                })  
            });
        	
        	$('#deviceListGridDiv').html(' ');
            
            //deviceListGrid.reconfigure(deviceListStore, deviceListCol);
            codiListGrid.render('deviceListGridDiv');
            codiListGridOn = true;
            /* modemListGridOn = false;
            meterListGridOn = false; */
        }else {
        	codiListGrid.reconfigure(codiListStore, codiListCol);
        	/* modemListGridOn = false;
        	meterListGridOn = false; */
        }
	} // 끝; 장비 리스트 그리드 (MCU-Codi)
	
	// 장비 리스트 그리드(MODEM)
	var modemListStore;
	var modemListCol;
	var modemListGrid;
	var modemListGridOn = false;    
	var getModemListGrid = function(){
		var grWidth = 600;        

        modemListStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getModemSearchGrid2.do", //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
            	 sModemType:''
            	,sModemId: deviceId
            	,sInstallState:''
            	,sMcuType:''
            	,sMcuName:dcuName
            	,sModemFwVer: fwVersion
            	,sModemSwRev: ''/* fwRevison */
            	,sModemHwVer: hwVer/* hwVersion */
        		,sModomStatus:''
            	,sInstallStartDate:installStartDate
            	,sInstallEndDate:installEndtDate
            	,sLastcommStartDate:lastCommStartDate
            	,sLastcommEndDate:lastCommEndDate
            	,sLocationId: locationId
            	,sOrder:''
            	,sCommState:''
            	,supplierId : supplierId
            	,pageSize : pageSize
            	,gridType :"extjs"
            	,modelId : modelId
            	,sMeterSerial : ''
            	,fwGadget : 'Y'
            	, sModuleBuild:''
            },
            totalProperty: 'totalCnt',
            root:'gridData',
            fields: ['modemDeviceSerial','deviceName','hwVer','fwVer', 'fwRevison','id', 'lastCommDate'],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
        	    
        });
        
        modemListCol = new Ext.grid.ColumnModel({
            columns: [
					{
					    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
		            	width: 30,
		            	align:'center',
		            	renderer: dataChk 
					},
					{header: 'Device ID', dataIndex: 'modemDeviceSerial', 
						renderer : function(val){
							 chkCount++;
							 return val;	 
						 }	
					},                      
					{header: 'Model', dataIndex: 'deviceName', },
					/* {header: 'HW Version', dataIndex: 'hwVer', }, */
					{header: 'FW Version', dataIndex: 'fwVer', },
					/* {header: 'FW Revision', dataIndex: 'fwRevison', },   */   
                    {header: 'Last Comm. Date', dataIndex: 'lastCommDate'} 
                  ],

                   defaults: {
                       sortable : false,
                       menuDisable : true,
                       hideable : false,
                       align : 'center',
                       width : 120
                  },
         });
        
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {
//        	return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.id + "\" /></div>";
            return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.modemDeviceSerial + "\" /></div>";
        }
        
        if(!modemListGridOn){
        	modemListGrid = new Ext.grid.GridPanel({
                store : modemListStore,
                cm : modemListCol,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                        rowselect: function(smd, row, rec) {
                        	var data = rec.data;
                            //선택한 항목에 해당하는 내용 출력
                            //eventItemSelected(data);
                        }
                    }
        		}),
                autoScroll : true,
                height : 500,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                //paging bar
                bbar: new Ext.PagingToolbar({
                pageSize: pageSize,
                store: modemListStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
                })  
            });
        	
        	$('#deviceListGridDiv').html(' ');
            
            //deviceListGrid.reconfigure(deviceListStore, deviceListCol);
            modemListGrid.render('deviceListGridDiv');
            modemListGridOn = true;
            deviceListGridOn = false;
            meterListGridOn = false;
        }else {
        	modemListGrid.reconfigure(modemListStore, modemListCol);
        	deviceListGridOn = false;
        	meterListGridOn = false;
        }
	} // 끝; 장비 리스트 그리드(MODEM)
	
	// 장비 리스트 그리드(METER)
	var meterListStore;
	var meterListCol;
	var meterListGrid;
	var meterListGridOn = false;
	
	var getMeterListGrid = function(){
		var grWidth = 600;
		meterListStore = new Ext.data.JsonStore({
			autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getMeterSearchGrid.do",  //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
            	sMeterType         : '',
                sMdsId             : deviceId,
                sStatus            : '',
                sMcuName           : dcuName,
                sLocationId        : locationId,
                sConsumLocationId  : '',
                sVendor            : '',
                sModel             : modelId,
                sInstallStartDate  : installStartDate,
                sInstallEndDate    : installEndtDate,
                sModemYN           : '',
                sCustomerYN        : '',
                sLastcommStartDate : lastCommStartDate,
                sLastcommEndDate   : lastCommEndDate,
                sOrder             : '',
                sCommState         : '',
                supplierId         : supplierId,
                sMeterGroup        : '',
                sGroupOndemandYN   : 'N',
                sCustomerId        : '',
                sCustomerName      : '',
                sPermitLocationId  : '',
                sMeterAddress      : '',
                sHwVersion         : hwVer/* hwVersion */,
                sFwVersion         : fwVersion,
                sGs1               : '',
                sType			   : searchDeviceType,
                fwGadget : 'Y'
            },
            root : 'gridData',
            totalProperty : 'totalCnt',
            fields: ['meterMds','modelName', 'hwVer','swVer','meterId','lastCommDate'],
            listeners : {
                beforeload: function(store, options){
                	options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) -1
                    });
                }
            }
        	    
        });
        
		meterListCol = new Ext.grid.ColumnModel({
            columns: [
					{
					    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
		            	width: 30,
		            	align:'center',
		            	renderer: dataChk 
					},
					{header: 'Device ID', dataIndex: 'meterMds', 
					 renderer : function(val){
							chkCount++;
							return val;	 
						 }	
					},                      
					{header: 'Model', dataIndex: 'modelName', },
					/* {header: 'HW Version', dataIndex: 'hwVer', }, */
					{header: 'FW Version', dataIndex: 'swVer', },
                    {header: 'Last Comm. Date', dataIndex: 'lastCommDate'}
                  ],

                   defaults: {
                       sortable : false,
                       menuDisable : true,
                       hideable : false,
                       align : 'center',
                       width : 120
                  },
         });
		
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {
    		return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.meterMds + "\" /></div>";
    	}
        
        if(!meterListGridOn){
        	meterListGrid = new Ext.grid.GridPanel({
                store : meterListStore,
                cm : meterListCol,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                        rowselect: function(smd, row, rec) {
                        	var data = rec.data;
                            //선택한 항목에 해당하는 내용 출력
                            //eventItemSelected(data);
                        }
                    }
        		}),
                autoScroll : true,
                height : 500,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit: true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                //paging bar
                bbar: new Ext.PagingToolbar({
                pageSize: pageSize,
                store: meterListStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
                }) 
            });
        	
        	$('#deviceListGridDiv').html(' ');
            
            //deviceListGrid.reconfigure(deviceListStore, deviceListCol);
            meterListGrid.render('deviceListGridDiv');
            meterListGridOn = true;
            modemListGridOn = false;
            deviceListGridOn = false;
        }else {
        	meterListGrid.reconfigure(meterListStore, meterListCol);
        	modemListGridOn = false;
        	deviceListGridOn = false;
        }
	} // 끝; 장비 리스트 그리드(METER)
	
	
	// 체크박스 전체 컨트롤
	function chkAll() {
 		if ($("#allCheck").is(':checked')) {
 			$("input[name='chkDeviceId']").attr("checked", "checked");
 		} else {
 		    $("input[name='chkDeviceId']").attr("checked", false);
 		}
 	}
	
	// Run OTA 버튼
	var otaWin;
	var deviceIdList = new Array();
	
	function runOta(){
        Ext.Msg.wait('Waiting for get list.', 'Infromation');

		deviceIdList = new Array();
		deviceIdArray  = new Array();
        /* if($('#sLocationId').val() == -1){
            Ext.Msg.alert('Infromation', 'Please select DSO Location.');
            return;
        } */
		var j =0;
		for(var i = 0 ; i < chkCount ; i++){
			if($("input:checkbox[id='chkDeviceId"+i+"']").is(":checked") == true){
				var checkDeviceID = "#chkDeviceId"+i
				// deviceId들을 ','으로연결한 String 
				deviceIdString += $(checkDeviceID).val()+",";
				
				// deviceId들의 배열
				deviceIdArray[j++] = $(checkDeviceID).val();
				
				// deviceId List
				deviceIdList.push($(checkDeviceID).val());
				
				deviceCount++;
			}
		}
        //alert(deviceIdList.length);
        if(deviceIdList.length == 0){
        	Ext.Msg.alert('', 'There is nothing selected');
        	return;
        }
		//마지막 ','제거
		deviceIdString = deviceIdString.slice(0,-1);
/* 		//alert(deviceCount);
		//팝업 호출		
		var left = ($('#deviceListGridDiv').width());
		var opts = "width=400px, height=250px, left=" + left + "px, top=300px, resizable=no, status=no, location=no";
		var obj = new Object();
		//var condition = $("input[name='chkDeviceId']:checked");
		if(deviceCount < 1){
			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>'
	  				,'<fmt:message key="aimir.select.row.no"/>');
			return;
		}  
		obj.deviceModel    = modelId;
		obj.condition      = deviceCount;
		obj.equip_kind     = targetDeviceType;
		obj.deviceIdArray  = deviceIdArray;
		obj.deviceIdString = deviceIdString;
        obj.locationId = locationId;
        obj.fileInfo = fileInfo;
        loginId = loginId;

		if (otaWin){
			otaWin.close();
		}
			
		otaWin = window.open("${ctx}/gadget/device/firmware/firmwareAddPopup.do", 
								"firmwareAdd", opts);
		otaWin.opener.obj = obj;	 */
		
        Ext.MessageBox.hide();

		otaExecute();
	}
	
	var i =0;
	var modemType = "";
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
                    	i = 0;
                    }else{
                    	i++;
                    	checkDeviceType(deviceIdArray[i])
                    }
                }
        );
	}
	
	
	 // Meter Baud Rate Panel[Get/Set Method] draw panel
    // 패널 그리기
    var otaExecuteFormPanel;
    var otaExecuteWin;
    function otaExecute() {
        // 아직 안닫힌 경우 기존 창은 닫기
        if(Ext.getCmp('otaExecuteWindow')){
        	modemType = Ext.getCmp('otaExecuteWindow').close();
        }
        
        
        
        if(targetDeviceType == "modem"){
        	checkDeviceType(deviceIdArray[0]);
        }else if(targetDeviceType == "meter"){
        	modemType=searchDeviceType;
		}
        
        if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel' || targetDeviceType=='dcu-coordinate'){
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
    	}else if(modemType == "RF" && targetDeviceType == "modem"){
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
	                    	{boxLabel: 'Clone OTA  ', name: 'radio-action2', inputValue:'0'},
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
    	}else if(modemType == "Ethernet" && targetDeviceType == "modem"){
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
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true  }
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
    	}else if(modemType == "MBB" && targetDeviceType == "modem"){
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
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true}
	                    ]
	                },
	                {
	                    xtype : 'checkbox',
	                    id : 'otaViaUploadChannel',
	                    labelSeparator: '',
	                    hideLabel: true,
	                    boxLabel: 'Asynchronously via the upload channel ',
	                    fieldLabel : 'Asynchronously via the upload channel ',
                        checked : true
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
    	}else if(modemType == "RF" && targetDeviceType == "meter"){
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
	                    	{boxLabel: 'Clone OTA  ', name: 'radio-action2', inputValue:'0'},
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
    	}else if(modemType == "MBB" && targetDeviceType == "meter"){
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
    	}else if(modemType == "Ethernet" && targetDeviceType == "meter"){
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
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true  }
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
        var pageX  = ($('#deviceListGridDiv').width())/4;
        
        if(modemType == "RF"){
        	//height = 310;
        	height = 330;
        	width  = 570;
        	pageX  = ($('#deviceListGridDiv').width())/14;
        }else if(modemType == "MBB"){
        	height = 305;
            width  = 360;
            pageX  = ($('#deviceListGridDiv').width())/4;
        }
        
        if(targetDeviceType =="dcu"){
        	height = 260;
        	width  = 360;
        }
        var otaExecuteWin = new Ext.Window({
            id     : 'otaExecuteWindow',
            title : 'Execute OTA',
            pageX : pageX,
            pageY : 200,
            height : height,
            width  : width,
            modal  : true,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [otaExecuteFormPanel],
        });

        otaExecuteWin.show();
    }
    
	
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
		    			targetDeviceType = targetDeviceType.toUpperCase(); 	
		    			if(targetDeviceType == "DCU-KERNEL"){
		    				targetDeviceType = "DCU_KERNEL";
		    			} else if(targetDeviceType == "DCU-COORDINATE"){
		    				targetDeviceType = "DCU_COORDINATE";
		    			}
		    			
		    			if(targetDeviceType == 'DCU' || targetDeviceType == 'DCU_KERNEL' || targetDeviceType == 'DCU_COORDINATE'){
		    				otaExecuteType = 1;
		    			}
		    			
		    			//동적으로 할때는 var deviceIdList = new Array(); 배열만들어서  push로 넣으면 될듯... 여러개 일떄는 
		    			var TargetList = new Array();
		    			var Target ={
		    					locationId : locationId,
		    					deviceIdList : deviceIdList
		    			};
		    			
		    			TargetList.push(Target);
		    			//TargetList.push(Target);
		    			
		    			var target = JSON.stringify(TargetList);
		    			//alert(target);
		    			
		    			/********************** OTA시 넘겨줄 수 있는 정보들 입니다. ***********************/
		    			var info="";
		        			info = "Debugging용 Alert입니다. "	
		        			+ "\nSupplier ID: " 		+ supplierId                   // supplierId
		        			+ "\nlogin ID: "			+ loginId					   // loginId
		        			//+ "\nModel Id: " 			+ modelId				       // 모델 id
		        			+ "\nDevice Type: " 		+ targetDeviceType		       // 기기 종류(mcu, modem, meter)
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
		    					targetType 	: targetDeviceType,
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
	<div id="deviceListGridDiv" class="margin10px">
		The Grid will appear within here. 
	</div>

	 <!-- confirm button -->
	<div class="margin10px">	
		<em id="tdExecute" class="am_button"><a href="javascript:runOta()"  class="on">Run OTA</a></em>
	</div>
</div>
</html>