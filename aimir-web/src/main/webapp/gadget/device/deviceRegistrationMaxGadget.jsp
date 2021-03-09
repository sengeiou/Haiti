<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Bulk Registration MaxGadget</title>

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
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

<script type="text/javascript" charset="utf-8">

    var flex;
    var miniTab = "MCU";
    var supplierId = "";

    var gridTitles;

    var arrayGrid;
    var gridOn = false;
    var grid;
    var store;
    var startBool = true;

    var count;
    var gridH = 350;
    var winH = 350;
    var filePath;
    var fileName;
    var chromeColAdd = 2;
    var MeterTypeMap = {};
    var ModemTypeMap = {};
    // 수정권한
    var editAuth = "${editAuth}";
    
    var preSysId = '';
    var preModemId = '';
    var preMeterId = '';
    var preSysIdByModemReg = '';
    var preModemIdByMeterReg = '';
    
    var shipmentImportStartDateHidden = "";
    var shipmentImportEndDateHidden = "";

    //탭초기화 jhkim
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:0,period:1,weekly:0,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
     // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
    /**
     * 유저 세션 정보 가져오기
     */
     
    $.ajaxSetup({
        async: false
    });
    
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                }
            }
    );
    
    //윈도우 리싸이즈시 event
    $(window).resize(function() {
        //리싸이즈시 패널 인스턴스 kill & reload
        if(!(deviceRegLogGrid === undefined)){
            deviceRegLogGrid.destroy();            
        }
        deviceRegLogGridOn = false;        
        getDeviceRegLogGrid();            
    });
    
    
    $(document).ready(function() {
        // 수정권한 체크
        if (editAuth == "true") {
        	$("#shipmentFile").show();
            $("#batchReg").show();
            $("#singleReg").show();
        } else {
        	$("#shipmentFile").hide();
            $("#batchReg").hide();
            $("#singleReg").hide();
        }
        
        new AjaxUpload('getShipmentImportTempFileName', {
            action: '${ctx}/gadget/device/getTempShipmentFileName.do',
            data : {},
            responseType : false,
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
            onComplete : function(file, response){
                var datas = response.split('|');
                $('#shipmentImportFilename').val(file);
                $('#shipmentImportFilepath').val(datas[0]);

                gridTitles = datas[1].split(',');
                setShipmentGrid(file, datas[0]);
            }
        });

        new AjaxUpload('getMsisdnImportTempFileName', {
            action: '${ctx}/gadget/device/getTempShipmentFileName.do',
            data : {},
            responseType : false,
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
            onComplete : function(file, response){
                var datas = response.split('|');
                $('#msisdnImportFilename').val(file);
                $('#msisdnImportFilepath').val(datas[0]);
            }
        });
        
        new AjaxUpload('getSimCardImportTempFileName', {
            action: '${ctx}/gadget/device/getTempShipmentFileName.do',
            data : {},
            responseType : false,
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
            onComplete : function(file, response){
                var datas = response.split('|');
                $('#simCardImportFilename').val(file);
                $('#simCardImportFilepath').val(datas[0]);
            }
        });
        
        new AjaxUpload('getTempFileName', {
            action: '${ctx}/gadget/device/getTempFileName.do',
            data : {
            },
            responseType : false,
            onSubmit : function(file , ext){
                // Allow only images. You should add security check on the server-side.
                if (ext && /^(xls|xlsx)$/.test(ext)){
                    /* Setting data */
                    this.setData({
                        'key': 'This string will be send with the file'
                    });

                } else {
                    return false;
                }
            },
            onComplete : function(file, response){
                var datas = response.split('|');
                $('#filename').val(file);
                $('#filepath').val(datas[0]);
                
                //insertDeviceBulkFile(file , response);

                gridTitles = datas[1].split(',');
                setGrid(file, datas[0]);
            }
        });
        
        if (editAuth == "true") {
        	$(function() {	getImportHistoryGrid();	});
        	//$(function() { $('#_shipmentFile').bind('click',function(event) { getImportHistoryGrid(); } ); });
            $(function() { $('#_batchReg')  .bind('click',function(event) {  } ); });
            $(function() { $('#_singleReg') .bind('click',function(event) { initSingleRegMCU(); } ); });
        }
        
        $(function() { $('#_regHistory').bind('click',function(event) { searchRegLog(); } ); });
        $(function() { $('#_singleMCU')   .bind('click',function(event) { initSingleRegMCU();   } ); });
        $(function() { $('#_singleModem') .bind('click',function(event) { initSingleRegModem(); } ); });
        $(function() { $('#_singleMeter') .bind('click',function(event) { initSingleRegMeter(); } ); });
        
        // MainTabs / SubTabs
        $("#deviceRegMax").tabs();
        $("#singleRegTabs").subtabs();
	    
	    // shipment file download
        $('#shipment_TemplateSelect').selectbox();
        $('#shipment_Select').selectbox();
	    
     	// shipment import
        $('#shipmentImportSelect').selectbox();
     	
     	// shipment import history
     	$('#importHistorySelect').selectbox();
     	
        // batchReg
        $('#batchRegTemplateSelect').selectbox();
        $('#batchRegSelect').selectbox();

        // singleReg > MCU
        // singleReg > Modem
        // singleReg > Meter

        // regHistory
        $('#regLogDeviceType').selectbox();
        $('#regLogSubDeviceType').selectbox();
        $('#regLogVendor').selectbox();
        $('#regLogModel').selectbox();
        $('#regLogRegType').selectbox();
        $('#regLogRegResult').selectbox();

        // 지역설정
        locationTreeGoGo('treeDivMcu', 'singleRegMCUmcuLoc', 'locationIdMcu');
        locationTreeGoGo('treeDivModem', 'singleRegModemLoc', 'locationIdModem', 'location'); //jhkim
        locationTreeGoGo('treeDivMeter', 'searchWord', 'locationId', 'location');

        if (editAuth == "true") {
            templateChange();
            batchRegChange();
            shipmentTemplateChange();	// shipment template file download
            shipmentChange();			// shipment file download
            shipmentImportChange();		// shipment template import
            importHistoryChange();		// shipment import history
            renderGrid();
        } else {
            searchRegLog();
        }
        
        var locDateFormat = "yymmdd";
        $("#shipmentImportStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#shipmentImportEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();

        if(("" + month).length == 1) month = "0" + month;
        if(("" + day).length == 1) day = "0" + day;

        var setDate      = year + "" + month + "" + day;
        var dateFullName = "";
        
     	// 날짜를 국가별 날짜 포맷으로 변경
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    dateFullName = json.localDate;
                    $("#shipmentImportStartDate").val(dateFullName);
                    $("#shipmentImportEndDate").val(dateFullName);
                });

        $("#shipmentImportStartDateHidden").val(setDate);
        $("#shipmentImportEndDateHidden").val(setDate);
        
        shipmentImportStartDateHidden = $("#shipmentImportStartDateHidden").val();
        shipmentImportEndDateHidden = $("#shipmentImportEndDateHidden").val();
    });
    
    function modifyDateLocal(setDate, inst) {
        var dateId       = '#' + inst.id;
        var dateHiddenId = '#' + inst.id + 'Hidden';

        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                });
    }

    function setComboMCU(comboId){
   		// 집중기유형 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.1'}
                , function (returnData){
                	
                	var result = returnData.code;
                    var arr = Array();
                    
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        
                        if(!(result[i].descr == 'Indoor' || result[i].descr == 'Outdoor')) {
                        	obj.name=result[i].descr;
                            obj.id=result[i].id;
                            arr[i]=obj
                        }
                    };
                    
                    eval($('#'+comboId)).pureSelect(arr);
                    eval($('#'+comboId)).selectbox();
                });
    }

     
    function setComboModem(comboId){
    	// 모뎀유형 초기화
        var params = {
                code : "1.2.1"
            };
    	
        // 시간차 문제로 인해 sync 로 변경
        var jsonText = $.ajax({
            type: "POST",
            url: "${ctx}/gadget/system/getChildCode.do",
            data: params,
            async: false
        }).responseText;

        // json string -> json object
        eval("result=" + jsonText);
        var pure = [];
        $.each(result.code, function(index, element) {
            var option = {};
            if (element.descr!="null") {
                option = {
                    id: element.id,
                    name: element.descr,
                    displayName: element.name
                };
            } else {
                option = {
                    id: element.id,
                    name: element.descr,
                    displayName: element.name
                };
            }
            
            if(!(element.name == 'Unknown' || element.name == 'ZEUMBus' || element.name == 'ZEUPLS' || element.name == 'ZRU')) {
            	ModemTypeMap[element.id] = option;
                
            	pure.push(option);	
                
            }
        });
        
        $('#'+comboId).pureSelect(pure);
        $('#'+comboId).selectbox();
    }
    
    function setComboModem_shipment(comboId){
        var params = {
                code : "1.2.1"
            };
    	
        // 시간차 문제로 인해 sync 로 변경
        var jsonText = $.ajax({
            type: "POST",
            url: "${ctx}/gadget/system/getChildCode.do",
            data: params,
            async: false
        }).responseText;

        eval("result=" + jsonText);
        var pure = [];
        $.each(result.code, function(index, element) {
            var option = {};
            if (element.name == 'MMIU') {
            	var mmiuArr = ['Ethernet Modem', 'Ethernet-Converter', 'MBB Modem'];
            	var mmiuChkIdArr = ['eth','con','mbb'];
            	
            	for(var i = 0; i < mmiuArr.length; i++) {
            		var tempId = element.id + mmiuChkIdArr[i]; 
            			
            		option = {
            			id: tempId,
                        name: mmiuArr[i],
                        displayName: mmiuArr[i]
    				};
            		
            		ModemTypeMap[tempId] = option;
                	pure.push(option);
                	
            	}
            } else if (element.name == 'SubGiga') {
            	var  rfModem = "RF Modem";
            	option = {
                        id: element.id,
                        name: rfModem,
                        displayName: rfModem
				};
            	
            	ModemTypeMap[element.id] = option;
            	pure.push(option);
			}
        });
        
        $('#'+comboId).pureSelect(pure);
        $('#'+comboId).selectbox();
    }

    // Meter Type Combobox
    function setComboMeter(comboId){
        var params = {
            code : "1.3.1"
        };
        // 시간차 문제로 인해 sync 로 변경
        var jsonText = $.ajax({
            type: "POST",
            url: "${ctx}/gadget/system/getChildCode.do",
            data: params,
            async: false
        }).responseText;

        // json string -> json object
        eval("result=" + jsonText);
        var pure = [];
		
        $.each(result.code, function(index, element) {
            var option = {};
            if (element.descr!="null") {
                option = {
                    id: element.id,
                    name: element.descr,
                    displayName: element.name
                };
            } else {
                option = {
                    id: element.id,
                    name: element.descr,
                    displayName: element.name
                };
            }
            
            
            if(!(element.name == 'SolarPowerMeter' || element.name == 'VolumeCorrector')) {
            	MeterTypeMap[element.id] = option;
                pure.push(option);	
            }
        });
		
        $('#'+comboId).pureSelect(pure);
        $('#'+comboId).selectbox();
    }

    function setComboTest(comboId){
        var dataList = new Array({name:'Customer',id:'Customer'},{name:'Contract',id:'Contract'});
                    eval($('#'+comboId)).pureSelect(dataList);
                    eval($('#'+comboId)).selectbox();
    }

    function getModelList(comboId) {
		var pure = [];
    
	   $.getJSON('${ctx}/gadget/system/modeltree.do', {'supplierId' : supplierId},
	   function (json) {
	         var data = [];
	         var jsonData = json.jsonTrees;
	         var json_length = jsonData.length;
	         var option = {};
	         
	         option = {
                     id: "",
                     name: "ALL",
                     displayName: "ALL"
				};
	         pure.push(option);	
	         
	         for (var i = 0; i < json_length; i++) {
	             var deviceTypes = jsonData[i].children;
	             var vendors = jsonData[i].children1;
	             var models = jsonData[i].children2;
	             var children = [];
	             var deviceType_length = deviceTypes.length;
	             
	             for (var j = 0; j < deviceType_length; j++) {
	                 var deviceType = deviceTypes[j];
	                 var children1 = [];
	                 var vendor_length= vendors[j].length;
	                 
	                 for (var k = 0; k < vendor_length; k++) {
	                     var vendor = vendors[j][k];
	                     var children2 = [];
	                     var model_length = models[j][k].length;
	                     
	                     for (var l = 0; l < model_length; l++) {
	                         var model = models[j][k][l];
	                         
	                         option = {
                                 id: model.id,
                                 name: model.name,
                                 displayName: model.name
                             };
	                    	 
	                         pure.push(option);	
	                     }
	                 }
	             }
	         }
		});
    	
    	$('#'+comboId).pureSelect(pure);
        $('#'+comboId).selectbox();
    }
    
    var winShipmentFile;
    function openShipmentFileReport() {
    	var type = $('#shipment_Select option:selected').val();
    	var detailType = $('#shipmentSelect option:selected').text();
    	var model = $('#shipmentModelSelect').val(); 
    	var purchaseOrder = $('#shipmentPurchaseOrder').val();
    	var filePath = "<fmt:message key="aimir.report.fileDownloadDir"/>";
    	var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
    	var obj = new Object();
    	
        if(type == 'Modem') {
        	obj.supplierId    = supplierId;
            obj.filePath      = filePath;
            obj.detailType    = detailType;
            obj.model         = model;
            obj.purchaseOrder = purchaseOrder;
            
            if (winShipmentFile) {
            	winShipmentFile.close();
            }
            
            winShipmentFile = window.open("${ctx}/gadget/device/modemShipmentFileExcelDownloadPopup.do", "Shipment File;", opts);
        } else if (type == 'Meter') {
        	obj.supplierId    = supplierId;
            obj.filePath      = filePath;
            obj.detailType    = detailType;
            obj.model         = model;
            
            if (winShipmentFile) {
            	winShipmentFile.close();
            }
            
            winShipmentFile = window.open("${ctx}/gadget/device/meterShipmentFileExcelDownloadPopup.do", "Shipment File;", opts);
        } else {
        	obj.supplierId    = supplierId;
            obj.filePath      = filePath;
            obj.model         = model;
            obj.purchaseOrder = purchaseOrder;
            
            if (winShipmentFile) {
            	winShipmentFile.close();
            }
            
            winShipmentFile = window.open("${ctx}/gadget/device/mcuShipmentFileExcelDownloadPopup.do", "Shipment File;", opts);
        }
        
        winShipmentFile.opener.obj = obj;
    }
    
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
    
    // Import History Grid (S)
    var importHistoryStore;
    var importHistoryCol;
    var importHistoryGrid;
    var importHistoryGridOn = false;
    function getImportHistoryGrid() {
		var targetDetailType = $('#importHistoryDetailSelect').val();
		var targetName = $('#importHistoryDetailSelect option:selected').text();
		
		if (targetDetailType != null) {
			if (targetDetailType.indexOf('eth') != -1 ||
				targetDetailType.indexOf('con') != -1 ||
				targetDetailType.indexOf('mbb') != -1) {
				
				// 문자 제거
				targetDetailType = targetDetailType.replace(/[^0-9]/g,'');
				targetDetailType.trim();
			}
		}
    	
    	$.getJSON('${ctx}/gadget/device/getShipmentImportHistory.do',
    		{ 					
    			'supplierId' 		: supplierId,
    			'targetType' 		: $('#importHistorySelect').val(),
    			'targetName'		: targetName,
    			'targetDetailType'	: targetDetailType,
    			'fileName'   		: $('#importHistoryFileName').val(),
    			'startDate' 		: $('#shipmentImportStartDateHidden').val(),	
    			'endDate'    		: $('#shipmentImportEndDateHidden').val()
    		},
    	 function (json) {
    		 drawImportHistoryGrid(json.result);
    		}
    	);
    }

    function drawImportHistoryGrid(dataList) {
    	var gridWidth = $('#importHistoryGridDiv').width();
    	importHistoryStore = new Ext.ux.data.PagingJsonStore({
    		lastOptions:{params:{start: 0, limit: 20}},
    		data:dataList||{},
    		root: '',
    		fields:[
    			{name : 'num', type: 'String'},
    			{name : 'deviceType', type: 'String'},
    			{name : 'fileName', type: 'String'},
    			{name : 'failCount', type: 'String'},
    			{name : 'importDate', type: 'String'},
    			{name : 'totalCount', type: 'String'},
    			{name : 'successCount', type: 'String'}
    		]
    	});
    	
    	importHistoryCol = new Ext.grid.ColumnModel({
    		columns : [
    			{header: 'No.', dataIndex: 'num', width: 40, align: 'center'},
    			{header: 'Device Type', dataIndex: 'deviceType', width:gridWidth/7, align: 'center'},
    			{header: 'File Name', dataIndex: 'fileName', width: gridWidth/5, align: 'center'},
    			{header: 'Total Count', dataIndex: 'totalCount', width:gridWidth/7, align: 'center'},
    			{header: 'Success Count', dataIndex: 'successCount', width:gridWidth/7, align: 'center'},
    			{header: 'Fail Count', dataIndex: 'failCount', width:gridWidth/7, align: 'center'},
    			{header: 'Import Date', dataIndex: 'importDate', width:gridWidth/5+8, align: 'center'}
    	   ],
    		defaults: {
    			sortable : true,
    			menuDisabled : true,
    			width: ((gridWidth-30)/4)-chromeColAdd
    		},
    	});
    	
    	if(importHistoryGridOn == false){
    		importHistoryGrid = new Ext.grid.GridPanel({
    			id: 'importHistoryMaxGrid',	
    			store: importHistoryStore,
    			cm : importHistoryCol,
    			autoScroll: false,
    			width: gridWidth,
    			height: 480,
    			listeners: {
    				rowdblclick: function(grid,index,e) {
    					// TODO
    				}
    			},
    			stripeRows : true,
    			columnLines: true,
    			loadMask: {
    				msg: 'loading...'
    			},
    			renderTo: 'importHistoryGridDiv',
    			viewConfig: {
    				forceFit:true,
    				scrollOffset: 1,
    				enableRowBody:true,
    				showPreview:true,
    				emptyText: '<fmt:message key="aimir.extjs.empty"/>',                    
    			} ,
    			 bbar: new Ext.PagingToolbar({
    				pageSize: 20,
    				store: importHistoryStore,
    				displayInfo: true,
    				displayMsg: ' {0} - {1} / {2}'
    			})
    		});
    		
    		importHistoryGridOn = true;
    	} else {
    		importHistoryGrid.setWidth(gridWidth);
    		importHistoryGrid.reconfigure(importHistoryStore, importHistoryCol);
    		var bottomToolbar = importHistoryGrid.getBottomToolbar();
    		bottomToolbar.bindStore(importHistoryStore);
    	}
    }
	// Import History Grid (E)
	
    function shipmentTemplateChange() {
        var deviceType = $('#shipment_TemplateSelect option:selected').val();
        var linkSrc;
        
        if (deviceType == "Meter") {
            setComboMeter('shipmentTemplateSelect');
            $('#shipmentTemplateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/EnergyMeter_shimpment_template.xls';
        } else if (deviceType == "Modem") {
        	setComboModem_shipment('shipmentTemplateSelect');
            $('#shipmentTemplateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/EthernetModem_shipment_template.xls';
        } else {
        	setComboMCU('shipmentTemplateSelect');
            $('#shipmentTemplateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/DCU_shimpment_template.xls';
        }

        $('#shipmentTemplateBtnDown').attr('href', linkSrc);
    }

    function shipmentTemplateDetailChange() {
        var detailType = $('#shipmentTemplateSelect option:selected').text();
        var type = $('#shipment_TemplateSelect option:selected').val();
        
        if (type == "Meter") {
            var detailType = $('#shipmentTemplateSelect').val();
            var option = MeterTypeMap[detailType];
            
            if (option.displayName == "EnergyMeter") {
                linkSrc = '${ctx}/temp/EnergyMeter_shimpment_template.xls';
            } else if(option.displayName == "WaterMeter") {
                linkSrc = '${ctx}/temp/WaterMeter_shimpment_template.xls';
            } else if(option.displayName == "GasMeter") {
                linkSrc = '${ctx}/temp/GasMeter_shimpment_template.xls';
            } else {
                linkSrc = '${ctx}/temp/HeatMeter_shimpment_template.xls';
            }
        } else if(type == "Modem") {
        	var detailType = $('#shipmentTemplateSelect').val();
            var option = ModemTypeMap[detailType];
            
            if (option.displayName == "Ethernet Modem") {
            	linkSrc = '${ctx}/temp/EthernetModem_shipment_template.xls';
			} else if (option.displayName == "Ethernet-Converter") {
				linkSrc = '${ctx}/temp/EthernetConverter_shipment_template.xls';
			} else if (option.displayName == "MBB Modem") {
				linkSrc = '${ctx}/temp/MBBModem_shipment_template.xls';
			} else if (option.displayName == "RF Modem") {
				linkSrc = '${ctx}/temp/RFModem_shipment_template.xls';
            }
        } else {
        	linkSrc = '${ctx}/temp/DCU_shimpment_template.xls';
        }
        
        $('#shipmentTemplateBtnDown').attr('href', linkSrc);
    }
    
    function shipmentChange() {
		var deviceType = $('#shipment_Select option:selected').val();
		var linkSrc = "javaScript:openShipmentFileReport();";
		
		if(deviceType == "Meter"){
			setComboMeter('shipmentSelect');
			$('#shipmentSelectDiv').css('display', 'block');
			getModelList('shipmentModelSelect');
			$('#shipmentModelSelectDiv').css('display', 'block');
			document.getElementById('shipmentPurchaseOrder').disabled=true;
		} else if(deviceType == "Modem"){
			setComboModem_shipment('shipmentSelect');
			$('#shipmentSelectDiv').css('display', 'block');
			getModelList('shipmentModelSelect');
			$('#shipmentModelSelectDiv').css('display', 'block');
			document.getElementById('shipmentPurchaseOrder').disabled=false;
		} else {
			setComboMCU('shipmentSelect');
			$('#shipmentSelectDiv').css('display', 'block');
			getModelList('shipmentModelSelect');
			$('#shipmentModelSelectDiv').css('display', 'block');
			document.getElementById('shipmentPurchaseOrder').disabled=false;
		}
	
		$('#shipmentBtnDown').attr('href', linkSrc);
	}
    
    function shipmentDetailChange(){
    	var linkSrc = "javaScript:openShipmentFileReport();";
        $('#shipmentBtnDown').attr('href', linkSrc);
    }
    
    function shipmentImportChange(){
        var deviceType = $('#shipmentImportSelect option:selected').val();
        
        if(deviceType == "Meter"){
            setComboMeter('shipmentImportDetailSelect');
            $('#shipmentImportDetailSelectDiv').css('display', 'block');
        } else if (deviceType == "Modem"){
        	setComboModem_shipment('shipmentImportDetailSelect');
            $('#shipmentImportDetailSelectDiv').css('display', 'block');
        } else {
        	setComboMCU('shipmentImportDetailSelect');
        	$('#shipmentImportDetailSelectDiv').css('display', 'block');
        }
        
        $('#shipmentImportFilename').val('');
        $('#shipmentImportFilepath').val('');
    }
    
    function importHistoryChange(){
        var deviceType = $('#importHistorySelect option:selected').val();
        
        if(deviceType == "Meter"){
            setComboMeter('importHistoryDetailSelect');
            $('#importHistorySelectDiv').css('display', 'block');
        } else if(deviceType == "Modem"){
        	setComboModem_shipment('importHistoryDetailSelect');
            $('#importHistorySelectDiv').css('display', 'block');
        } else {
            setComboMCU('importHistoryDetailSelect');
            $('#importHistorySelectDiv').css('display', 'block');
        }
    }
    
    function importHistoryDetailChange(){
        var detailType = $('#importHistoryDetailSelect option:selected').text();
        var type = $('#importHistorySelect option:selected').val();

        if(type == "Meter"){
            var detailType = $('#importHistoryDetailSelect').val();
            var option = MeterTypeMap[detailType];

            if(option.displayName == "EnergyMeter") {
                linkSrc = '${ctx}/temp/EnergyMeter_template.xls';
            } else if(option.displayName == "WaterMeter") {
                linkSrc = '${ctx}/temp/WaterMeter_template.xls'; 
            } else if(option.displayName == "GasMeter") {
                linkSrc = '${ctx}/temp/GasMeter_template.xls';
            } else if(option.displayName == "HeatMeter") {
                linkSrc = '${ctx}/temp/HeatMeter_template.xls';
            } else {
                linkSrc = '${ctx}/temp/VolumeCorrector_template.xls';
            }
        } else if(type == "Modem"){
        	var detailType = $('#importHistoryDetailSelect').val();
            var option = ModemTypeMap[detailType];
            
            if (option.displayName == "ZRU") {
                linkSrc = '${ctx}/temp/ZRU_template.xls';
            } else if(option.displayName == "ZMU") {
                linkSrc = '${ctx}/temp/ZMU_template.xls';
            } else if(option.displayName == "ZEUPLS") {
                linkSrc = '${ctx}/temp/ZEUPLS_template.xls';
            } else if(option.displayName == "MMIU") {
                linkSrc = '${ctx}/temp/MMIU_template.xls';  
            } else if(option.displayName == "IEIU") {
                linkSrc = '${ctx}/temp/IEIU_template.xls';  
            } else if(option.displayName == "ZEUMBus") {
                linkSrc = '${ctx}/temp/ZEUMBus_template.xls';
            } else if(option.displayName == "IHD") {
                linkSrc = '${ctx}/temp/IHD_template.xls';   
            } else if(option.displayName == "ACD") {
                linkSrc = '${ctx}/temp/ACD_template.xls';   
            } else if(option.displayName == "HMU") {
                linkSrc = '${ctx}/temp/HMU_template.xls';   
            } else if(option.displayName == "PLCIU") {
                linkSrc = '${ctx}/temp/PLCIU_template.xls'; 
            } else if(option.displayName == "ZBRepeater") {
                linkSrc = '${ctx}/temp/ZBRepeater_template.xls';
            } else if(option.displayName == "SubGiga") {
                linkSrc = '${ctx}/temp/SubGiga_template.xls';
            } else if(option.displayName == "Converter") {
                linkSrc = '${ctx}/temp/Converter_template.xls';
            }
        } 
    }

    function templateChange(){
        var deviceType = $('#batchRegTemplateSelect option:selected').val();
        var linkSrc;

        if( deviceType == "Meter" ){
            setComboMeter('templateSelect');
            $('#templateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/EnergyMeter_template.xls';   //xlsx - xls
        }
        else if( deviceType == "Modem" ){
            setComboModem('templateSelect');
            $('#templateSelectDiv').css('display', 'block');
            // linkSrc = '${ctx}/temp/ACD_template.xls';   //xlsx - xls
            linkSrc = '${ctx}/temp/MMIU_template.xls';   //xlsx - xls
        }
        else if( deviceType == "Customer" ){
            setComboTest('templateSelect');
            $('#templateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/customer_template.xls';  //xlsx - xls
        }
        else if( deviceType == "MeterMapper" ){
        	linkSrc = '${ctx}/temp/ModemAndMeterMapper_template.xlsx';
        }
        else {  //MCU
            setComboMCU('templateSelect');
            $('#templateSelectDiv').css('display', 'block');
            linkSrc = '${ctx}/temp/DCU_template.xls';   //xlsx - xls
        }

        $('#btnDown').attr('href', linkSrc);
    }

    function templateDetailChange(){
        var detailType = $('#templateSelect option:selected').text();
        var type = $('#batchRegTemplateSelect option:selected').val();  //jhkim start

        if( type == "Meter" ){

            var detailType = $('#templateSelect').val();
            var option = MeterTypeMap[detailType];

            if(option.displayName == "EnergyMeter") {
                linkSrc = '${ctx}/temp/EnergyMeter_template.xls';   //xlsx - xls
            } else if(option.displayName == "WaterMeter") {
                linkSrc = '${ctx}/temp/WaterMeter_template.xls';    //xlsx - xls
            } else if(option.displayName == "GasMeter") {
                linkSrc = '${ctx}/temp/GasMeter_template.xls';  //xlsx - xls
            } else if(option.displayName == "HeatMeter") {
                linkSrc = '${ctx}/temp/HeatMeter_template.xls'; //xlsx - xls
            } else {
                linkSrc = '${ctx}/temp/VolumeCorrector_template.xls';   //xlsx - xls
            }
        }
        else if( type == "Modem" ){
        	
        	var detailType = $('#templateSelect').val();
            var option = ModemTypeMap[detailType];
            
            if (option.displayName == "ZRU") {
                linkSrc = '${ctx}/temp/ZRU_template.xls';   //xlsx - xls
            } else if(option.displayName == "ZMU") {
                linkSrc = '${ctx}/temp/ZMU_template.xls';   //xlsx - xls
            } else if(option.displayName == "ZEUPLS") {
                linkSrc = '${ctx}/temp/ZEUPLS_template.xls';    //xlsx - xls
            } else if(option.displayName == "MMIU") {
                linkSrc = '${ctx}/temp/MMIU_template.xls';  //xlsx - xls
            } else if(option.displayName == "IEIU") {
                linkSrc = '${ctx}/temp/IEIU_template.xls';  //xlsx - xls
            } else if(option.displayName == "ZEUMBus") {
                linkSrc = '${ctx}/temp/ZEUMBus_template.xls';   //xlsx - xls
            } else if(option.displayName == "IHD") {
                linkSrc = '${ctx}/temp/IHD_template.xls';   //xlsx - xls
            } else if(option.displayName == "ACD") {
                linkSrc = '${ctx}/temp/ACD_template.xls';   //xlsx - xls
            } else if(option.displayName == "HMU") {
                linkSrc = '${ctx}/temp/HMU_template.xls';   //xlsx - xls
            } else if(option.displayName == "PLCIU") {
                linkSrc = '${ctx}/temp/PLCIU_template.xls'; //xlsx - xls
            } else if(option.displayName == "ZBRepeater") {
                linkSrc = '${ctx}/temp/ZBRepeater_template.xls';    //xlsx - xls
            } else if(option.displayName == "SubGiga") {
                linkSrc = '${ctx}/temp/SubGiga_template.xls';   //xlsx - xls
            } else if(option.displayName == "Converter") {
                linkSrc = '${ctx}/temp/Converter_template.xls'; //xlsx - xls
            }
        }else if( type == "Customer" ){
            if(detailType == "Customer"){
                linkSrc = '${ctx}/temp/customer_template.xls';
            }else{
                linkSrc = '${ctx}/temp/contract_template.xls';
            }
        }else if( type == "MeterMapper" ){
        	linkSrc = '${ctx}/temp/ModemAndMeterMapper_template.xlsx';
        }else{
            linkSrc = '${ctx}/temp/DCU_template.xls';   //xlsx - xls
        }                                                                       // jhkim end

        //var linkSrc = '${ctx}/temp/'+detailType+'_template.xls';  //xlsx - xls
        $('#btnDown').attr('href', linkSrc);
    }

    function batchRegChange(){
        var deviceType = $('#batchRegSelect option:selected').val();

        if( deviceType == "Meter" ){
            setComboMeter('detailSelect');
            $('#detailSelectDiv').css('display', 'block');
        }
        else if( deviceType == "Modem" ){
            setComboModem('detailSelect');
            $('#detailSelectDiv').css('display', 'block');
        }else if( deviceType == "Customer" ){
            setComboTest('detailSelect');
            $('#detailSelectDiv').css('display', 'block');
        }
        else{   //jhkim
            setComboMCU('detailSelect');
            $('#detailSelectDiv').css('display', 'block');
//            $('#detailSelectDiv').css('display', 'none');
        }
        $('#filename').val('');
        $('#filepath').val('');
    }
    
    function getBatchRegGridType(){
        return $('#batchRegSelect').val();
    }

    // 등록이력 > 장비종류 변경
    function regLogDeviceTypeChange(){

        var selectedType = $('#regLogDeviceType').val();
        var regLogSubDeviceTypeLabel = "";
        var regLogSubDeviceTypeCode  = "";

        if(selectedType == "MCU"){
            regLogSubDeviceTypeLabel = "<fmt:message key="aimir.mcutype"/>";
            regLogSubDeviceTypeCode  = "1.1.1";    // Code.java
        }else if(selectedType == "Modem"){
            regLogSubDeviceTypeLabel = "<fmt:message key="aimir.modem.type"/>";
            regLogSubDeviceTypeCode  = "1.2.1";

        }else if(selectedType == "Meter"){
            regLogSubDeviceTypeLabel = "<fmt:message key="aimir.metertype"/>";
            regLogSubDeviceTypeCode  = "1.3.1";
        }  else if(selectedType == "Customer"){
            regLogSubDeviceTypeLabel = "<fmt:message key="aimir.customerview"/>";
            regLogSubDeviceTypeCode  = "16";
        }  else{
            regLogSubDeviceTypeLabel = "<fmt:message key="aimir.bulkReg.lable.asset"/> <fmt:message key="aimir.type2"/>";
        }

        // 장비 유형설정
        document.getElementById('regLogSubDeviceTypeLabel').innerHTML = regLogSubDeviceTypeLabel;

        // 유형 조회
        getSubDeviceTypeCode(regLogSubDeviceTypeCode);

        // 제조사, 모델 초기화
        $('#regLogVendor').initSelect();
        $('#regLogModel').initSelect();

        $('#regLogVendor').selectbox();
        $('#regLogModel').selectbox();

    }
    
    // 등록이력 > 장비종류  변경에 따른 장비 유형 조회
    function getSubDeviceTypeCode(code) {
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : code}
                , function (returnData){
                	var pure = [];
                    $.each(returnData.code, function(index, element) {
                        var option = {};
                        if(element.descr!="null"){
                            option = {
                                id: element.id,
                                name: element.descr,
                                displayName: element.name
                            };
                        }else{
                            option = {
                                id: element.id,
                                name: element.descr,
                                displayName: element.name
                            };
                        }
                        if(code == "1.3.1") {		//Meter
                        	MeterTypeMap[element.id] = option;
                        } else if (code == "1.2.1") {	//Modem
                        	ModemTypeMap[element.id] = option;
                        }
                        pure.push(option);
                    });
                    
                    $('#regLogSubDeviceType').loadSelect(pure);
                    $('#regLogSubDeviceType').selectbox();
                });
       };

    // 등록이력 > xx유형에 따른 제조사 조회
    function getVendorListBySubDeviceType() {
        $.getJSON('${ctx}/gadget/device/getVendorListBySubDeviceType.do'
                , { 'deviceType' : $('#regLogDeviceType').val()
                   ,'subDeviceType' : $('#regLogSubDeviceType').val()}
                , function (returnData){
                    $('#regLogVendor').loadSelect(returnData.deviceVendor);
                    $('#regLogVendor').selectbox();
                });
       };


    // 등록이력 > 제조사  변경에 따른 모델 조회
    function getDeviceModelsByVenendorId() {
        $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                , {  'vendorId' : $('#regLogVendor').val()
                    ,'deviceType' : $('#regLogDeviceType').val()
                    ,'subDeviceType' : $('#regLogSubDeviceType').val() }
                , function (returnData){
                    $('#regLogModel').loadSelect(returnData.deviceModels);
                    $('#regLogModel').selectbox();
                });
       };

       var tempFile="";

    // 개별등록  > 집중기 -------------------------------------------------------------
    // 개별등록  > 집중기초기화
    function initSingleRegMCU(){

        $('#mcuInsertForm').each(function(){
            this.reset();
            });

        // 집중기유형 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.1'}
                , function (returnData){
                	var result = returnData.code;
                	var arr = new Array();
                	for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].descr;
                        obj.id=result[i].id;
                        arr[i]=obj
                    };

                    $('#singleRegMCUmcuType').pureSelect(arr);
                    $('#singleRegMCUmcuType').selectbox();
                });
		
        /*
        // HW버전 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.2'}
                , function (returnData){
                    var result = returnData.code;
                    var arr = Array();
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].descr;
                        obj.id=result[i].id;
                        arr[i]=obj
                    };
                    $('#singleRegMCUmcuHwVer').pureSelect(arr);
                    $('#singleRegMCUmcuHwVer').selectbox();
                });

        // SW버전 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.3'}
                , function (returnData){
                	var result = returnData.code;
                    var arr = Array();
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].descr;
                        obj.id=result[i].id;
                        arr[i]=obj
                    };
                    $('#singleRegMCUmcuSwVer').pureSelect(arr);
                    $('#singleRegMCUmcuSwVer').selectbox();
                    //$('#singleRegMCUmcuSwReVer').selectbox();
                });
        */
        
        // 제조사 조회
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData){
                    $('#singleRegMCUmcuVendor').pureSelect(returnData.deviceVendors);
                    $('#singleRegMCUmcuVendor').selectbox();

                    $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                            , {'vendorId' : $('#singleRegMCUmcuVendor').val()
                            ,  'deviceType' : 'MCU'
                            ,  'subDeviceType' : ' '}
                         , function (returnData){
                             if(returnData.deviceModels.length!=0) {
                             $('#singleRegMCUmcuModel').pureSelect(returnData.deviceModels);
                             $('#singleRegMCUmcuModel').selectbox();
                             } else {
                                $('#singleRegMCUmcuModel').noneSelect(null);
                             $('#singleRegMCUmcuModel').selectbox();
                             }
                         });
                });

        // 통신타입 조회
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '4.6'}
                , function (returnData){
                    $('#singleRegMCUmcuCommType').pureSelect(returnData.code);
                    $('#singleRegMCUmcuCommType').selectbox();
                });
//jhkim 이미지 업로드 추가
        new AjaxUpload('uploadTempImg', {
            action: "${ctx}/gadget/device/uploadTempImg.do",
            data : {

            },
            responseType : false,
            onSubmit : function(file, ext){

                if (ext && /^(jpg|png|jpeg|gif)$/.test(ext)){

                    this.setData({
                        'key': 'This string will be send with the file'
                    });
                } else {

                    return false;
                }
            },
            onComplete : function(file, response){
                var datas = response.split('|');

                $('#singleRegMCUmcuPhoto').val(file);

                tempFile = datas[0];
            }
        });
    }

    // 개별등록 > 집중기  - 모델조회
    function getModelListByVendorMCU() {
        $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                , {'vendorId' : $('#singleRegMCUmcuVendor').val()
                ,  'deviceType' : 'MCU'
                ,  'subDeviceType' : ' '}
                , function (returnData){
                    if(returnData.deviceModels.length!=0) {
                        $('#singleRegMCUmcuModel').pureSelect(returnData.deviceModels);
                        $('#singleRegMCUmcuModel').selectbox();
                     } else {
                        $('#singleRegMCUmcuModel').noneSelect(null);
                        $('#singleRegMCUmcuModel').selectbox();
                     }
                });
       };

    // 개별등록 > 모뎀  - 집중기 중복확인
    function singleRegMCUIsMCUDuplicate(){
    	var sysId = $.trim($('#singleRegMCUmcuId').val());
    	$('#singleRegMCUmcuId').val(sysId);
    	preSysId = sysId;
        if($('#singleRegMCUmcuId').val() == null || $('#singleRegMCUmcuId').val() == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputDCUId'/>");
            $('#singleRegMCUmcuId').focus();
            mcuRegSysIdCheck = false;
        } else {
             $.getJSON('${ctx}/gadget/device/isMCUDuplicateByMcuId.do'
                        , {  'sysId': $('#singleRegMCUmcuId').val()}
                        , function (returnData){
                            if(returnData.result == "true"){
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.alreadyExist'/>");
                                $('#singleRegMCUmcuId').focus();
                                mcuRegSysIdCheck = false;
                            } else if(returnData.result == 'deleteStatus') {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                                $('#singleRegMCUmcuId').focus();
                                mcuRegSysIdCheck = false;
                            } else{
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.abailableId'/>");
                                mcuRegSysIdCheck = true;
                            }
                        });
        }
    }

    var mcuRegSysIdCheck = false;

    // 집중기  등록
    var mcuInsert = function() {

        // 사전검사
        if($("#singleRegMCUmcuId").val() == "" || $("#singleRegMCUmcuId").val() == null){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputMCUid'/>");
            return;
        }

        if(!mcuRegSysIdCheck || (preSysId != $("#singleRegMCUmcuId").val())){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateDCUId'/>");
            return;
        }
        if($("#singleRegMCUmcuModel").val() == "" || $("#singleRegMCUmcuModel").val() == null){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.selectModel'/>");
            return;
        }

        if($("#singleRegMCUmcuSwReVer").val() == "" || $("#singleRegMCUmcuSwReVer").val() == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputRevision'/>");
            return;
        }
        
        //HW ver check
        if($("#singleRegMCUmcuHwVer").val() == "" || $("#singleRegMCUmcuHwVer").val() == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Please enter the HW ver.");
            return;
        }
        //SW ver check
        if($("#singleRegMCUmcuSwVer").val() == "" || $("#singleRegMCUmcuSwVer").val() == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Please enter the SW ver.");
            return;
        }

        if($("#locationIdMcu").val() == ""){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.report.mgmt.msg.validation.location'/>");
            return;
        }

        $("#mcuInsertForm :input[id='supplierId']").val(supplierId);

        var deviceModelId   = $("#mcuInsertForm :input[id='singleRegMCUmcuModel']").val();
        var mcuTypeId       = $("#mcuInsertForm :input[id='singleRegMCUmcuType']").val();
        var protocolTypeId  = $("#mcuInsertForm :input[id='singleRegMCUmcuCommType']").val();
        var locationId      = $("#mcuInsertForm :input[id='locationIdMcu']").val();

        var params = {  success  : insertMcuResult
                      , url : '${ctx}/gadget/device/insertMCU.do?deviceModelId='
                          + deviceModelId + '&mcuTypeId=' + mcuTypeId + '&protocolTypeId=' + protocolTypeId + '&locationId=' + locationId
                          + '&tempFile=' + tempFile //jhkim 이미지 업로드 추가
                      , type     : 'post'
                      , datatype : 'application/json'
                  };

          $('#mcuInsertForm').ajaxSubmit(params);

    };

    // 집중기 등록 후처리
    function insertMcuResult(responseText, status) {

        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i=0 ; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        }

        if( $('#singleRegMcuCheck').is(':checked')){
            initSingleRegMCU();
        }else{
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.firmware.msg04'/>");
        }

        mcuRegSysIdCheck == false;
    }

    // 개별등록  > 모뎀
    // 개별등록  > 모뎀 초기화

    var modemRegDeviceSerialCheck       = false;
    var modemRegMcuIdCheck              = false;

    function initSingleRegModem(){

        $('#modemInfoForm').each(function(){
            this.reset();
            });

        // 모뎀유형 초기화. Unknown 은 제외함.
        $.getJSON('${ctx}/gadget/system/getChildCodeSelective.do'
                , {'code' : '1.2.1', 'excludeCode' : '1.2.1.255'}
                , function (returnData){
                    var pure = [];
                    $.each(returnData.code, function(index, element) {
                        var option = {};
                        if(element.descr!="null"){
                            option = {
                                id: element.id,
                                name: element.descr,
                                displayName: element.name
                            };
                        }else{
                            option = {
                           		id: element.id,
                                name: element.descr,
                                displayName: element.name
                            };
                        }
                        ModemTypeMap[element.id] = option;
                        pure.push(option);
                    });  
                    
                    $('#singleRegModemModemType').pureSelect(pure);
                    $('#singleRegModemModemType').selectbox();
                });

        // HW버전 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.2.4'}
                , function (returnData){
                	var result = returnData.code;
                	var arr = Array();
                	var result = returnData.code;
                	var arr = Array();
                	for (var i = 0; i < result.length; i++) {
                		var obj = new Object();
                		obj.name=result[i].descr;
                		obj.id=result[i].id;
                		arr[i]=obj
                	};
                	                    
                    $('#singleRegModemHwVersion').pureSelect(arr);
                    $('#singleRegModemHwVersion').selectbox();
                });

        // SW버전 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.2.6'}
                , function (returnData){
                	var result = returnData.code;
                	var arr = Array();
                	var result = returnData.code;
                	var arr = Array();
                	for (var i = 0; i < result.length; i++) {
                		var obj = new Object();
                		obj.name=result[i].descr;
                		obj.id=result[i].id;
                		arr[i]=obj
                	};
                    $('#singleRegModemSwVersion').pureSelect(arr);
                    $('#singleRegModemSwVersion').selectbox();
                });

        // SW Revision초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.2.3'}
                , function (returnData){
                    $('#singleRegModemSWRevision').pureSelect(returnData.code);
                    $('#singleRegModemSWRevision').selectbox();
                });

     // 제조사 조회 jhkim
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData){
                    $('#singleRegModemVendor').pureSelect(returnData.deviceVendors);
                    $('#singleRegModemVendor').selectbox();

                    //모델 초기화
                    $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                            , {'vendorId' : $('#singleRegModemVendor').val(),
                                'deviceType' : 'Modem',
                                'subDeviceType' : $('#singleRegModemModemType').val()}
                            , function (returnData){
                                if(returnData.deviceModels.length!=0) {
                                    $('#singleRegModemModel').pureSelect(returnData.deviceModels);
                                    $('#singleRegModemModel').selectbox();
                                } else {
                                    $('#singleRegModemModel').noneSelect(null);
                                    $('#singleRegModemModel').selectbox();
                                }
                            });
                });
    }

    // 개별등록 > 모뎀  - 모델조회  jhkim
    function getModelListByVendorModem() {
        $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                , {'vendorId' : $('#singleRegModemVendor').val(),
                    'deviceType' : 'Modem',
                    'subDeviceType' : $('#singleRegModemModemType').val()}
                , function (returnData){
                    if(returnData.deviceModels.length!=0) {
                        $('#singleRegModemModel').pureSelect(returnData.deviceModels);
                        $('#singleRegModemModel').selectbox();
                    } else {
                        $('#singleRegModemModel').noneSelect(null);
                        $('#singleRegModemModel').selectbox();
                    }
                });
       };

    // 개별등록 > 모뎀  - 모뎀번호 중복확인
    function singleRegModemIsModemDuplicate(){
    	var modemId = $.trim($('#singleRegModemDeviceSerial').val());
    	$('#singleRegModemDeviceSerial').val(modemId);
    	preModemId = modemId;
        if(modemId == null || modemId == "") {
        	preModemId = modemId;
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputid'/>");
            $('#singleRegModemDeviceSerial').focus();
            mcuRegSysIdCheck = false;
        } else {
            $.getJSON('${ctx}/gadget/device/isModemDuplicateByDeviceSerial.do'
                    , {  'deviceSerial': $('#singleRegModemDeviceSerial').val()}
                    , function (returnData){
                        if(returnData.result == "true"){
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.alreadyExist'/>");
                            $('#singleRegModemDeviceSerial').focus();
                            modemRegDeviceSerialCheck = false;
                        } else if(returnData.result == "delete") {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                            $('#singleRegModemDeviceSerial').focus();
                            modemRegDeviceSerialCheck = false;
                        } else{
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.abailableId'/>");
                            modemRegDeviceSerialCheck = true;
                        }
                    });
        }

    }

    // 개별등록 > 모뎀  - 집중기 중복확인
    function singleRegModemIsMCUDuplicate(){
    	var sysId = $.trim($('#singleRegModemMcuId').val());
    	$('#singleRegModemMcuId').val(sysId);
    	preSysIdByModemReg = sysId;
        if($('#singleRegModemMcuId').val() == null || $('#singleRegModemMcuId').val() == "") {
            mcuRegSysIdCheck = true;
        } else {
            $.getJSON('${ctx}/gadget/device/isMCUDuplicateByMcuId.do'
                    , {  'sysId': $('#singleRegModemMcuId').val()}
                    , function (returnData){
                        if(returnData.result == "true"){
                             Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.abailableId'/>");
                             modemRegMcuIdCheck = true;
                        }else{
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkValidationMCUId'/>");
                            $('#singleRegModemMcuId').focus();
                            modemRegMcuIdCheck = false;
                        }
                    });
        }
    }

    // 개별등록 > 모뎀  - 집중기 Name 자동완성
    function singleRegModemMcuIdChange(){
        $.getJSON('${ctx}/gadget/device/getMCUmcuIdList.do'
                , {  'sysId'       : $('#singleRegModemMcuId').val()
                    ,'supplierId' : supplierId}
                , function (returnData){

                    var dataString = "";
                    dataJson   = returnData.mcuNameList;

                    for ( var i in dataJson){
                        var MCUData = dataJson[i];
                        dataString = dataString + MCUData + "^";
                    }

                    var data = dataString.split('^');
                    $('#singleRegModemMcuId').autocomplete(data);

                });
    }

    // 모뎀 등록
    var insertModem = function() {
        var detailTypeModem = $('#singleRegModemModemType option:selected').val();
    	var option = ModemTypeMap[detailTypeModem];
            
        var modemType = option.displayName;
        var params = "";

        // 사전검사
        if(modemType != "MMIU" && modemType != "IEIU" && modemType !="Converter"){
            //모뎀아이디 체크
            if($("#singleRegModemDeviceSerial").val() == "") {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputModemid'/>");
                    $("#singleRegModemDeviceSerial").focus();
                    return;
            }
            // 중복확인 체크
            if(!modemRegDeviceSerialCheck || preModemId != $("#singleRegModemDeviceSerial").val()){
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateModemId'/>");
                return;
            }

        }

        //집중기번호 유효성 체크
        if($.trim($("#singleRegModemMcuId").val()) != "") {
            if(!modemRegMcuIdCheck || preSysIdByModemReg != $("#singleRegModemMcuId").val()){
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkValidationMCUId'/>");
                return;
            }
        }

    //jhkim
        if($("#singleRegModemModel").val() == "" || $("#singleRegModemModel").val() == null){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.selectModel'/>");
            return;
        }
        if($("#locationIdModem").val() == ""){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.report.mgmt.msg.validation.location'/>");
            return;
        }

        $("#modemInfoForm :input[id='supplierId']").val(supplierId);
        $("#modemInfoForm :input[id='modemTypeName']").val(modemType);

        params = {
                success  : insertModemResult
              , url      : "${ctx}/gadget/device/insertModem" + modemType + ".do"
              , type     : "post"
              , datatype : "application/json"
        };

        $('#modemInfoForm').ajaxSubmit(params);

    };

    // 모뎀 등록 후처리
    function insertModemResult(responseText, status) {
        if( $('#singleRegModemCheck').is(':checked')){
            initSingleRegModem();
        }else{
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.firmware.msg04'/>");
        }

        meterRegDeviceSerialCheck   = false;
        meterRegModemIdCheck        = false;

    }

    // 개별등록  > 미터
    // 개별등록  > 미터 초기화

    var meterRegMdsIdCheck    = false;
    var meterRegModemIdCheck  = false;

    function initSingleRegMeter(){

        $('#meterInfoFormEdit').each(function(){
            this.reset();
            });

        setComboMeter("singleRegMeterMeterType");

        // 제조사 조회
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData){
                    $('#singleRegMeterVendor').pureSelect(returnData.deviceVendors);
                    $('#singleRegMeterVendor').selectbox();

                    //모델 초기화
                    $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                            , {'vendorId' : $('#singleRegMeterVendor').val(),
                                'deviceType' : 'MCU',
                                'subDeviceType' : $('#singleRegMeterMeterType').val()}
                            , function (returnData){
                                if (returnData.deviceModels.length != 0) {
                                    $('#singleRegMeterModel').pureSelect(returnData.deviceModels);
                                    $('#singleRegMeterModel').selectbox();
                                 } else {
                                    $('#singleRegMeterModel').noneSelect(null);
                                    $('#singleRegMeterModel').selectbox();
                                }
                            });
                });

    }

    // 개별등록 > 미터  - 모델조회
    function getModelListByVendor() {
        $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                , {'vendorId' : $('#singleRegMeterVendor').val(),
                    'deviceType' : 'MCU',
                    'subDeviceType' : $('#singleRegMeterMeterType').val()}
                , function (returnData){
                    if(returnData.deviceModels.length!=0) {
                        $('#singleRegMeterModel').pureSelect(returnData.deviceModels);
                        $('#singleRegMeterModel').selectbox();
                     } else {
                        $('#singleRegMeterModel').noneSelect(null);
                        $('#singleRegMeterModel').selectbox();
                    }
                });
       };

    var dataJson = "";

    // 개별등록  > 미터  - 모뎀ID 자동완성
    function singleRegMeterModemSerialChange(){
        $.getJSON('${ctx}/gadget/device/getModemSerialList.do'
                , {  'modemSerial': $('#singleRegMeterModemSerial').val()
                   , 'supplierId' : supplierId
                  }
                , function (returnData){

                     var dataString = "";
                     dataJson   = returnData.rtnModemSerials;

                     for ( var i in dataJson){
                         var modemData = dataJson[i];
                         dataString = dataString + modemData[0] + "^";
                     }

                     var data = dataString.split('^');
                     $('#singleRegMeterModemSerial').autocomplete(data);
                });
    }

    // 개별등록 > 미터  - 모뎀ID 유형구분
    function singleRegMeterModemSerialOnBlur(){
        var modemSerial = $('#singleRegMeterModemSerial').val();
        for ( var i in dataJson){
            var modemData = dataJson[i];

            if(modemData[0] == modemSerial)
                if(modemData[1] == "ZEUMBus"){
                    $('#singleRegMeterPort').val('');
                    $('#singleRegMeterPort').attr("disabled", "disabled");
                }else{
                    $('#singleRegMeterPort').removeAttr("disabled");
                }

        }
    }

    // 개별등록 > 미터  - 미터아이디 중복확인
    function singleRegMeterIsMeterDuplicate(){
    	var meterId = $.trim($('#singleRegMeterMdsId').val());
    	$('#singleRegMeterMdsId').val(meterId);
    	preMeterId = meterId;
    	
        if(meterId == null || meterId == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputMeterid'/>");
            $('#singleRegMeterMdsId').focus();
            mcuRegSysIdCheck = false;
        } else {
            $.getJSON('${ctx}/gadget/device/isMeterDuplicateByMdsId.do'
                    , {  'mdsId': $('#singleRegMeterMdsId').val()}
                    , function (returnData){
                        if(returnData.result == "true"){
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.alreadyExist'/>");
                            $('#singleRegMeterMdsId').focus();
                            meterRegMdsIdCheck = false;
                        }else if (returnData.result=="delete") {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                            $('#singleRegMeterMdsId').focus();
                            meterRegMdsIdCheck = false;
                        }else{
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.abailableId'/>");
                            meterRegMdsIdCheck = true;
                        }

                    });
        }

    }

    // 개별등록 > 미터  - 모뎀아이디 유효성체크
    function singleRegMeterIsModemDuplicate(){
    	var modemId = $.trim($('#singleRegMeterModemSerial').val());
    	$('#singleRegMeterModemSerial').val(modemId);
    	preModemIdByMeterReg = modemId;
        if($('#singleRegMeterModemSerial').val() == null || $('#singleRegMeterModemSerial').val() == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputModemid'/>");
            $('#singleRegMeterModemSerial').focus();
            mcuRegSysIdCheck = false;
        } else {
            $.getJSON('${ctx}/gadget/device/isModemValidateCkByDeviceSerial.do'
                    , {  'deviceSerial': $('#singleRegMeterModemSerial').val()}
                    , function (returnData){

                        if(returnData.result == "true"){
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.abailableId'/>");
                            meterRegModemIdCheck = true;
                        } else if (returnData.result == "delete") {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                            meterRegModemIdCheck = true;
                        } else{
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkValidationModemId'/>");
                        	$('#singleRegMeterModemSerial').focus();
                            meterRegModemIdCheck = false;
                        }

                    });
        }

    }

    function insertMeterInfo() {

        // 사전검사
        if ($.trim($("#singleRegMeterMdsId").val()) == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputMeterid'/>");
            return;
        }

        if (!meterRegMdsIdCheck || preMeterId != $("#singleRegMeterMdsId").val()) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkDuplicateMeterId'/>");
            return;
        }

        //모뎀 아이디 유효성 체크
        if ($("#singleRegMeterModemSerial").val() != "") {
            if (!meterRegModemIdCheck || preModemIdByMeterReg != $("#singleRegMeterModemSerial").val()) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.chkValidationModemId'/>");
                return;
            }
        }

        if ($("#singleRegMeterModel").val() == "" || $("#singleRegMeterModel").val() == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.selectModel'/>");
            return;
        }

        if ($("#locationId").val() == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.report.mgmt.msg.validation.location'/>");
            return;
        }

        $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

        // 미터 유형추가
        var meterType =$('#singleRegMeterMeterType option:selected').val();

        var option = MeterTypeMap[meterType];
        var params    = "";

        if (option) {
            var url = '${ctx}/gadget/device/insert' + option.displayName + '.do';
            var params = "";
            params = {
                success : insertMeterInfoResult,
                url : url,
                type : 'post',
                datatype : 'application/json'
            };

            $('#meterInfoFormEdit').ajaxSubmit(params);
        }
    };

    function insertMeterInfoResult(){


        if( $('#singleRegMeterCheck').is(':checked')){
            initSingleRegMeter();
        }else{
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.firmware.msg04'/>");
        }

        meterRegMdsIdCheck    = false;
        meterRegModemIdCheck  = false;

    }

    // 개별등록 > 미터  - 확인 = 장비등록
    function singleRegMeterSubmit(){

        // Meter 등록
        $.getJSON('${ctx}/gadget/device/insertMeter.do'
        , {   'meterType'   : $('#singleRegMeterMeterType').val()
            , 'mdsId'       : $('#singleRegMeterMdsId').val()
            , 'modemSerial' : $('#singleRegMeterModemSerial').val()

            , 'vendor'      : $('#singleRegMeterVendor').val()
            , 'model'       : $('#singleRegMeterModel').val()
            , 'port'        : $('#singleRegMeterPort').val()

            , 'loc'         : $('#singleRegMeterLoc').val()
            , 'locDetail'   : $('#singleRegMeterLocDetail').val()
            , 'supplierId '  : supplierId
          }

        , function (returnData){

            if(returnData.result == "true")
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertsuccess'/>");
            else
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertfail'/>");
        });
    }
    
    // RegLog
    function searchRegLog(){
         var arrayObj = getRegLogCondition();
        $.getJSON('${ctx}/gadget/device/getDeviceRegLog.do'
        ,{
            deviceType      : arrayObj[0],
            subDeviceType   : arrayObj[1],
            vendor          : arrayObj[2],
            model           : arrayObj[3],
            deviceID        : arrayObj[4],
            regType         : arrayObj[5],
            regResult       : arrayObj[6],
            searchStartDate : arrayObj[7],
            searchEndDate   : arrayObj[8],
            supplierId      : arrayObj[9]
        }, // FileType을 선택후 상세 타입 콤보 선택필요
        function (json){
                getDeviceRegLogGrid(json.gridData);
        });
        // getDeviceRegLogGrid();

    }

    function getRegLogCondition(){


        var arrayObj = Array();

        var allText = "<fmt:message key='aimir.all'/>";
        var allTextJquery = "All";

        arrayObj[0]  = $('#regLogDeviceType').val();

       // 초기에는 fmtMessage를 통해서 출력 / jQuery에서는 "All"로 화면에 표시함
       // -> jQeury에서 fmt를 적용된다면, allTextJquery는 삭제가능함
       if(allText == $('#regLogSubDeviceType option:selected').text() ||
         allTextJquery == $('#regLogSubDeviceType option:selected').text())
        arrayObj[1]  = "";
       else {
    	   if(arrayObj[0] == "Meter") {
    		   var detailType = $('#regLogSubDeviceType').val();
               var option = MeterTypeMap[detailType];
               arrayObj[1] = option.displayName;
    	   } else if(arrayObj[0] == "Modem") {
    		   var detailType = $('#regLogSubDeviceType').val();
               var option = ModemTypeMap[detailType];
               arrayObj[1] = option.displayName;
    	   }
       }

        arrayObj[2]  = $('#regLogVendor').val();
        arrayObj[3]  = $('#regLogModel').val();
        arrayObj[4]  = $('#regLogDeviceID').val();
        arrayObj[5]  = $('#regLogRegType').val();
        arrayObj[6]  = $('#regLogRegResult').val();

        arrayObj[7]  = $('#searchStartDate').val();
         if(!arrayObj[7]) {
            arrayObj[7] = $("#periodStartDate").val();
        }

        arrayObj[8]  = $('#searchEndDate').val();
         if(!arrayObj[8]) {
            arrayObj[8] = $("#periodEndDate").val();
        }

        arrayObj[9] = supplierId; //jhkim

        return arrayObj;
    }

    // 등록이력 메시지
    function getRegLogGridFmtMessage(){

        var fmtMessage = new Array();
        fmtMessage[0] = "No.";
        fmtMessage[1] = "<fmt:message key="aimir.bulkReg.lable.asset"/>";
        fmtMessage[2] = "<fmt:message key="aimir.bulkReg.lable.asset"/> "+
                    "<fmt:message key="aimir.type2"/>";
        fmtMessage[3] = "<fmt:message key="aimir.bulkReg.lable.asset"/> "+"<fmt:message key="aimir.id"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";
        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.device.RegistrationType"/>";
        fmtMessage[7] = "<fmt:message key="aimir.device.RegResult"/>";
        fmtMessage[8] = "<fmt:message key="aimir.installdate"/>"; //jhkim

        return fmtMessage;

    }

    var deviceRegLogGridStore;
    var deviceRegLogGridColModel;
    var deviceRegLogGridOn = false;
    var deviceRegLogGrid;

    //Voltage Level 그리드
    function getDeviceRegLogGrid(reglogData){
        var message  = getRegLogGridFmtMessage();

        var width = $("#deviceRegLogGridDiv").width();

         deviceRegLogGridStore = new Ext.ux.data.PagingJsonStore({
            lastOptions:{params:{start: 0, limit: 20}},
            data:reglogData||{},
            /*autoLoad: {params:{start: 0, limit: 20}},
            url: "${ctx}/gadget/device/getDeviceRegLog.do",
            baseParams:{
                deviceType      : arrayObj[0],
                subDeviceType   : arrayObj[1],
                vendor          : arrayObj[2],
                model           : arrayObj[3],
                deviceID        : arrayObj[4],
                regType         : arrayObj[5],
                regResult       : arrayObj[6],
                searchStartDate : arrayObj[7],
                searchEndDate   : arrayObj[8],
                supplierId      : arrayObj[9]
            },*/
            // totalProperty: 'totalCnt',
            root:'',
             fields: [
            { name: 'no', type: 'String' },
            { name: 'type', type: 'String' },
            { name: 'deviceType', type: 'String' },
            { name: 'deviceName', type: 'String' },
            { name: 'vendorName', type: 'String' },
            { name: 'modelName', type: 'String' } ,
            { name: 'regType', type: 'String' } ,
            { name: 'result', type: 'String' } ,
            { name: 'installdate', type: 'String' }
            ],
        });

        deviceRegLogGridColModel = new Ext.grid.ColumnModel({

            columns: [

                {
                    header:message[0],
                    dataIndex:'no',
                    width: 10 ,
                    align:'center'
                 }
                 ,{
                    header:message[1],
                    dataIndex:'type',
                    width: 20,
                    align:'center'

                }
                ,{
                    header:message[2],
                    dataIndex:'deviceType',
                    width: 20 ,
                    align:'center'
                }
                ,{
                    header:message[3],
                    dataIndex:'deviceName',
                    width: 20,
                    align:'center'
                }
                ,{
                    header:message[4],
                    dataIndex:'vendorName',
                    width: 20,
                    align:'center'
                }
                ,{
                    header:message[5],
                    dataIndex:'modelName',
                    width: 20,
                    align:'center'
                }
                ,{
                    header:message[6],
                    dataIndex:'regType',
                    width: 20 ,
                    align:'center'
                }
                ,{
                    header:message[7],
                    dataIndex:'result',
                    width: 20 ,
                    align:'center'
                }
                ,{
                    header:message[8],
                    dataIndex:'installdate',
                    width: 30 ,
                    align:'center'
                }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-30)/4)-chromeColAdd
            }
        });

        if (deviceRegLogGridOn == false) {

            deviceRegLogGrid = new Ext.grid.GridPanel({

                id: 'deviceRegLogMaxGrid',
                store: deviceRegLogGridStore,
                cm : deviceRegLogGridColModel,
                autoScroll: false,
                width: width,
                height: 520,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'deviceRegLogGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 20,
                    store: deviceRegLogGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });

            deviceRegLogGridOn  = true;

        } else {

            deviceRegLogGrid.setWidth(width);
            deviceRegLogGrid.reconfigure(deviceRegLogGridStore, deviceRegLogGridColModel);
            var bottomToolbar = deviceRegLogGrid.getBottomToolbar();
            bottomToolbar.bindStore(deviceRegLogGridStore);
        }

    };

    // Flex 명칭 설정 

    // 일괄등록
    function getBatchRegGrid(){

        var fmtMessage = new Array();
        var dataFild = new Array();

        fmtMessage[0] = "state";
        dataFild[0] = "state";

        for(var i=1; i<gridTitles.length; i++){
            fmtMessage[i]   = gridTitles[i-1];
            dataFild[i]     = gridTitles[i-1];
        }

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;

        return dataGrid;

    }
    
    // 엑셀파일 등록
    function registExcelFile() {
        if ($('#filepath').val() == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile"/>');
            return;
        }

        if ($('#detailSelect').val() == 'Customer') {   //customer ADD
            $.getJSON('${ctx}/gadget/system/insertCustomerBulkFile.do',
                    {'filePath' : $('#filepath').val(),
                     'supplierId' : supplierId},
                     function(result) {
                         Ext.Msg.alert('<fmt:message key='aimir.message'/>',result.resultMsg);
                     }
            );
        } else if ($('#detailSelect').val() == 'Contract') {//contract ADD END
            $.getJSON('${ctx}/gadget/system/insertContractBulkFile.do',
                    {'filePath' : $('#filepath').val(),
                     'supplierId' : supplierId},
                     function(result) {
                         Ext.Msg.alert('<fmt:message key='aimir.message'/>',result.resultMsg);
                     }
            );
        }//customer & contract ADD END
        else {
            if ($('#batchRegSelect').val() == 'Meter') {
                var detailType = $('#detailSelect').val();
                var option = MeterTypeMap[detailType];

                $.getJSON('${ctx}/gadget/device/insertDeviceBulkFile.do',
                         {filePath : $('#filepath').val(),
                          fileType : $('#batchRegSelect').val(),
                          supplierId : supplierId,
                          detailType : ($('#batchRegSelect').val() == 'MCU') ? "" : option.displayName}, // FileType을 선택후 상세 타입 콤보 선택필요
                          function(result, status) {
                              //deviceRenderGrid(result.resultList, result.headerList);
                              Ext.Msg.alert('<fmt:message key='aimir.message'/>',result.resultMsg);
                          }
                    );//getJSON End
            } else if ($('#batchRegSelect').val() == 'Modem') {
            	var detailType = $('#detailSelect').val();
                var option = ModemTypeMap[detailType];
                $.getJSON('${ctx}/gadget/device/insertDeviceBulkFile.do',
                		{filePath : $('#filepath').val(),
	                    fileType : $('#batchRegSelect').val(),
	                    supplierId : supplierId,
	                    detailType : ($('#batchRegSelect').val() == 'MCU') ? "" : option.displayName}, // FileType을 선택후 상세 타입 콤보 선택필요
	                    function(result, status) {
	                        //deviceRenderGrid(result.resultList, result.headerList);
	                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',result.resultMsg);
	                    }
                    );//getJSON End
            }else if ($('#batchRegSelect').val() == 'MeterMapper') {
            	var detailType = $('#detailSelect').val();
                var option = ModemTypeMap[detailType];
                $.getJSON('${ctx}/gadget/device/insertDeviceBulkFile.do',
                           {filePath:$('#filepath').val(),
                            fileType:$('#batchRegSelect').val(),
                            supplierId:supplierId,
                            detailType:$('#batchRegSelect').val()}, // FileType을 선택후 상세 타입 콤보 선택필요
                           function(result, status) {
                            	if (result.resultMsg == "success") {
                                    Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.newcustomeradded"/>");
                                } else if (result.resultMsg == "failure") {
                                    errorListExl = result.errorList;
                                    Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>",
                                            function() {renderErrorListGrid(result.errorList);});
                                } else {
                                    Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.save.error"/>");
                                }
                           }
                    );//getJSON End
            } else {
                $.getJSON('${ctx}/gadget/device/insertDeviceBulkFile.do',
                       {filePath:$('#filepath').val(),
                        fileType:$('#batchRegSelect').val(),
                        supplierId:supplierId,
                        detailType:($('#batchRegSelect').val() == 'MCU') ? "" : $('#detailSelect option:selected').text()}, // FileType을 선택후 상세 타입 콤보 선택필요
                       function(result, status) {
                            //deviceRenderGrid(result.resultList, result.headerList);
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',result.resultMsg);
                       }
                );
            }
        } // else end
    } // registExcelFile End
    
    /* Error 리스트 START */
    var errorGrid;
    var errorStore;
    var errorColModel;
    function renderErrorListGrid(errorList) {
        var width = $("#gridDiv").width();
        var pageSize = 20;

        errorStore = new Ext.ux.data.PagingArrayStore({
            lastOptions: {params: {start: 0, limit: pageSize}},
            data : errorList,//arrayGrid,
            fields: ["cell0", "cell1", "errMsg"]
        });
        
        var colWidth = width/3 - chromeColAdd;

        errorColModel = new Ext.grid.ColumnModel({
            columns : [
                {header: "Modem Serial number", dataIndex: 'cell0'}
               ,{header: "Meter Printed Number", dataIndex: 'cell1'}
               ,{header: "<fmt:message key="aimir.sap.errorReason"/>", dataIndex: 'errMsg', width: colWidth - 4, renderer: addTooltip}
            ],
            defaults : {
                sortable: true
               ,menuDisabled: true
               ,width: colWidth
            }
        });

        // ExtJS 그리드 생성
            errorGrid = new Ext.grid.GridPanel({
                width: width,
                height: 520,
                store: errorStore,
                colModel : errorColModel,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'gridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: errorStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            errorGridOn = true;
    }//Fuction End

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
    
    function importExcelFile() {
        var filePath = $('#shipmentImportFilepath').val();
        var fileType = $('#shipmentImportSelect').val();
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
    	var obj = new Object();
        
    	if ($('#shipmentImportFilepath').val() == '') {
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile"/>');
    		return;
    	} else {
    		obj.supplierId  = supplierId;
    		obj.filePath    = filePath;
    		obj.fileType    = fileType;
    		obj.detailType      = ($('#shipmentImportSelect').val() == 'MCU') ? "DCU" : $('#shipmentImportDetailSelect option:selected').text();
    		
    		if (winShipmentFile) {
    			winShipmentFile.close();
    		}
    		
    		winShipmentFile = window.open("${ctx}/gadget/device/shipmentFileImportPopup.do", "Shipment File Import;", opts);
    		winShipmentFile.opener.obj = obj;
    	}
    }
    
    function importMsisdnExcelFile() {
        var filePath = $('#msisdnImportFilepath').val();
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
    	var obj = new Object();
        
    	if ($('#msisdnImportFilepath').val() == '') {
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile"/>');
    		return;
    	} else {
    		obj.supplierId  = supplierId;
    		obj.filePath    = filePath;
    		
    		if (winShipmentFile) {
    			winShipmentFile.close();
    		}
    		
    		winShipmentFile = window.open("${ctx}/gadget/device/shipmentMsisdnFileImportPopup.do", "MSISDN File Import;", opts);
    		winShipmentFile.opener.obj = obj;
    	}
    }
    
    function importSimCardExcelFile() {
        var filePath = $('#simCardImportFilepath').val();
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
    	var obj = new Object();
        
    	if ($('#simCardImportFilepath').val() == '') {
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile"/>');
    		return;
    	} else {
    		obj.supplierId  = supplierId;
    		obj.filePath    = filePath;
    		
    		if (winShipmentFile) {
    			winShipmentFile.close();
    		}
    		
    		winShipmentFile = window.open("${ctx}/gadget/device/shipmentSimCardFileImportPopup.do", "SIM Card File Import;", opts);
    		winShipmentFile.opener.obj = obj;
    	}
    }
    
    // Excel Data 체크 후 Grid 에 보여줌.
    function setGrid(file, path) {
        if ($('#detailSelect').val() == 'Customer') {
            // Customer Data 초기화
            $.getJSON('${ctx}/gadget/system/getCustomerBulkFile.do'
                    , {'filePath' : path}
                    , function (result) {
                        var count = result.resultList.length;
                        arrayGrid = new Array;
                        var gridData   = null;
                        var arrayData  = null;
                        for (var i = 0; i < count; i++) {
                            gridData = result.resultList[i];
                            // 그리드의 값을 설정한다.   ** 순서 중요!!
                            arrayData = [gridData.supplier,
                                         gridData.customerNo,
                                         gridData.name,
                                         gridData.loginId,
                                         gridData.passwd,                                         
                                         gridData.address,
                                         gridData.address1,
                                         gridData.address2,
                                         gridData.address3,
                                         gridData.email,
                                         gridData.emailYn,
                                         gridData.telephoneNo,
                                         gridData.mobileNo,
                                         gridData.smsYn,
                                         gridData.demandResponse,
                                         //gridData.customTypeCode, Contract 로 이동하여 삭제됨
                                         gridData.Status,
                                         gridData.identityOrCompanyRegNo,
                                         gridData.initials,
                                         gridData.vatNo,
                                         gridData.workTelephone,
                                         gridData.postalAddressLine1,
                                         gridData.postalAddressLine2,
                                         gridData.postalSuburb,
                                         gridData.postalCode];
                            arrayGrid[i] = arrayData;
                        }

                        // 그리드를 생성한다.
                        renderGrid();
                    });
        } else if ($('#detailSelect').val() == 'Contract') {
            //Contract Data 초기화
            $.getJSON('${ctx}/gadget/system/getContractBulkFile.do'
                    , {'filePath' : path
                    ,  'supplierId' : supplierId}
                    , function(result) {
                        var count = result.resultList.length;
                        arrayGrid = new Array;
                        var gridData   = null;
                        var arrayData  = null;
                        for (var i = 0; i < count; i++) {
                            gridData = result.resultList[i];
                            // 그리드의 값을 설정한다.
                            arrayData = [gridData.customerNumber,
                                         gridData.meaNumber,
                                         gridData.serviceTypeCode,
                                         gridData.supplyStatus,
                                         gridData.location,
                                         gridData.contractDemand,
                                         gridData.paymentType,
                                         gridData.supplier,
                                         gridData.tariffIndex,
                                         gridData.paymentStatus,
                                         gridData.prepaymentThreshold,
                                         gridData.meterDeviceSerial,
                                         gridData.SIC,
                                         gridData.Status];
                            arrayGrid[i] = arrayData;
                        }

                        // 그리드를 생성한다.
                        renderGrid();
                    });
        } else {
            if ($('#batchRegSelect').val() == 'Meter') {
                var detailType = $('#detailSelect').val();
                var option = MeterTypeMap[detailType];
    
                $.getJSON('${ctx}/gadget/device/getDeviceBulkFile.do'
                        ,{ filePath:$('#filepath').val(),
                           fileType:$('#batchRegSelect').val(),
                           supplierId:supplierId,
                           detailType:($('#batchRegSelect').val() == 'MCU') ? "DCU" : option.displayName}, // FileType을 선택후 상세 타입 콤보 선택필요
                        function(result) {
                            deviceRenderGrid(result.resultList, result.headerList);
                        });
            } else if ($('#batchRegSelect').val() == 'Modem') {
            	var detailType = $('#detailSelect').val();
                var option = ModemTypeMap[detailType];
    
                $.getJSON('${ctx}/gadget/device/getDeviceBulkFile.do'
                        ,{ filePath:$('#filepath').val(),
                           fileType:$('#batchRegSelect').val(),
                           supplierId:supplierId,
                           detailType:($('#batchRegSelect').val() == 'MCU') ? "DCU" : option.displayName}, // FileType을 선택후 상세 타입 콤보 선택필요
                        function (result){
                                deviceRenderGrid(result.resultList, result.headerList);
                        });
            }else if ($('#batchRegSelect').val() == 'MeterMapper') {
            	var detailType = $('#detailSelect').val();
                var option = ModemTypeMap[detailType];
    
                $.getJSON('${ctx}/gadget/device/getDeviceBulkFile.do'
                        ,{ filePath:$('#filepath').val(),
                           fileType:$('#batchRegSelect').val(),
                           supplierId:supplierId,
                           detailType:$('#batchRegSelect').val()},
                        function (result){
                                deviceRenderGrid(result.resultList, result.headerList);
                        });
            } else {
                $.getJSON('${ctx}/gadget/device/getDeviceBulkFile.do'
                        ,{ filePath:$('#filepath').val(),
                           fileType:$('#batchRegSelect').val(),
                           supplierId:supplierId,
                           detailType:($('#batchRegSelect').val() == 'MCU') ? "DCU" : $('#detailSelect option:selected').text()}, // FileType을 선택후 상세 타입 콤보 선택필요
                        function (result){
                                deviceRenderGrid(result.resultList, result.headerList);
                        });
            }
        }

    }
    
    function setShipmentGrid(file, path) {
    	$.getJSON('${ctx}/gadget/device/getDeviceBulkFile.do'
                ,{
					supplierId : supplierId,
			 		filePath   : $('#shipmentImportFilepath').val(),
					fileType   : $('#shipmentImportSelect').val(),
					detailType : ($('#shipmentImportSelect').val() == 'MCU') ? "DCU" : $('#shipmentImportDetailSelect option:selected').text()
                 }, function (result){
					deviceRenderGrid(result.resultList, result.headerList);
                });
    }
    
	// === Device Registration Render Start === //
    var deviceRenderGrid = function(resultList, headerList) {

        var store   = null;
        store = new Ext.ux.data.PagingArrayStore({
            lastOptions: {params: {start: 0, limit: 50}},
            data : resultList,//arrayGrid,
            fields: headerList
            });

        var cols = [];
        cols.push({
            header: "Data Status",
            width: 80,
            sortable: true,
            renderer: result,
            dataIndex: headerList[0]+""
        });
        for(var i=0; i<headerList.length-1; i++){
            cols.push({
                header: headerList[i+1]+"",
                width: 100,
                sortable: true,
                renderer: cell_grid,
                dataIndex: headerList[i+1]+""
            });

        }

        colModel =new Ext.grid.ColumnModel(cols);

        $('#gridDiv').empty();
        // ExtJS 그리드 생성
        grid = new Ext.grid.GridPanel({
            height: 550,
            store: store,
            colModel : colModel,
            stripeRows : true,
            columnLines: true,
            loadMask:{
                msg: 'loading...'
            },
            renderTo: 'gridDiv',
            viewConfig: {
                enableRowBody:true,
                showPreview:true,
                emptyText: 'No data to display'
            },
            // paging bar on the bottom
            bbar: new Ext.PagingToolbar({
                pageSize: 50,
                store: store,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
            })
        });

        index = 0;
    }//Fuction End

/////////////////   Device Registration Render End   ////////////////////

//////////////   Render Start (Customer, Contract, 1st)   ///////////////

    var renderGrid = function() {
        var store = null;
        if ($('#detailSelect').val() == 'Customer') {
            store = new Ext.ux.data.PagingArrayStore({
            lastOptions : {params: {start: 0, limit: 50}},
            data : arrayGrid,
            fields : ["supplier",
                     "customerNo",
                     "name",
                     "loginId",
                     "passwd",
                     "address",
                     "address1",
                     "address2",
                     "address3",
                     "email",
                     "emailYn",
                     "telephoneNo",
                     "mobileNo",
                     "smsYn",
                     "demandResponse",
                     "Status",
                     "identityOrCompanyRegNo",
                     "initials",
                     "vatNo",
                     "workTelephone",
                     "postalAddressLine1",
                     "postalAddressLine2",
                     "postalSuburb",
                     "postalCode"]
            });

            colModel = new Ext.grid.ColumnModel({
                defaults: {
                    //width: width / 4,
                    sortable: true,
                    menuDisabled: true
                },
                columns: [{
                    header: "<fmt:message key='aimir.datastatus'/>",
                    dataIndex: 'Status',
                    width: 100,
                    renderer: result,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.supplier'/>",
                    dataIndex: 'supplier',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.customerid'/>",
                    dataIndex: 'customerNo',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.customername'/>",
                    dataIndex: 'name',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Login Id",
                    dataIndex: 'loginId',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Password",
                    dataIndex: 'passwd',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.customeraddress'/>",
                    dataIndex: 'address1',
                    width: 300,
                    renderer: address_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.email'/>",
                    dataIndex: 'email',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "E-Mail <fmt:message key='aimir.operator.notificationSet'/>",
                    dataIndex: 'emailYn',
                    width: 70,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.tel.no'/>",
                    dataIndex: 'telephoneNo',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.celluarphone'/>",
                    dataIndex: 'mobileNo',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "SMS <fmt:message key='aimir.operator.receiveSetting'/>",
                    dataIndex: 'smsYn',
                    width: 70,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.demandResponse'/>",
                    dataIndex: 'demandResponse',
                    width: 120,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "identityOrCompanyRegNo",
                    dataIndex: 'identityOrCompanyRegNo',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Initials",
                    dataIndex: 'initials',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Vat No",
                    dataIndex: 'vatNo',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Work Telephone",
                    dataIndex: 'workTelephone',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Postal AddressLine1",
                    dataIndex: 'postalAddressLine1',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Postal AddressLine2",
                    dataIndex: 'postalAddressLine2',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Postal Suburb",
                    dataIndex: 'postalSuburb',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "Postal Code",
                    dataIndex: 'postalCode',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                }]
            });

        }else if($('#detailSelect').val()=='Contract'){

            store = new Ext.ux.data.PagingArrayStore({
            lastOptions: {params: {start: 0, limit: 50}},
            data : arrayGrid,
            fields: ["customerNumber",
                     "contractNumber",
                     "serviceTypeCode",
                     "supplyStatus",
                     "location",
                     "contractDemand",
                     "paymentType",
                     "supplier",
                     "tariffIndex",
                     "paymentStatus",
                     "prepaymentThreshold",
                     "meterDeviceSerial",
                     "SIC",
                     "Status"]
              });

              colModel = new Ext.grid.ColumnModel({
                defaults: {
                    //width: width / 4,
                    sortable: true,
                    menuDisabled: true
                },
                columns: [{
                    header: "<fmt:message key='aimir.datastatus'/>",
                    dataIndex: 'Status',
                    width: 80,
                    renderer: result,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.customerid'/>",
                    dataIndex: 'customerNumber',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.contractNumber'/>",
                    dataIndex: 'contractNumber',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.service.type'/>",
                    dataIndex: 'serviceTypeCode',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.location.supplier'/>",
                    dataIndex: 'location',
                    width: 130,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.supplystatus'/>",
                    dataIndex: 'supplyStatus',
                    width: 90,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.paymenttype'/>",
                    dataIndex: 'paymentType',
                    width: 100,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.supplier'/>",
                    dataIndex: 'supplier',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.contract.tariff.type'/>",
                    dataIndex: 'tariffIndex',
                    width: 90,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.contract.demand.amount'/>",
                    dataIndex: 'contractDemand',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.paymentstatus'/>",
                    dataIndex: 'paymentStatus',
                    width: 90,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.usingBalanceOfFee'/>",
                    dataIndex: 'prepaymentThreshold',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.meterdeviceserial'/>",
                    dataIndex: 'meterDeviceSerial',
                    width: 200,
                    renderer: cell_grid,
                    sortable: true
                },{
                    header: "<fmt:message key='aimir.sic'/>",
                    dataIndex: 'SIC',
                    width: 110,
                    renderer: cell_grid,
                    sortable: true
                }]
            });

        }else if(startBool == true && $('#batchRegSelect').val()=='MCU'){

            store = new Ext.ux.data.PagingArrayStore({
                lastOptions: {params: {start: 0, limit: 50}},
                data : new Array,
                fields: []
                  });

                  colModel = new Ext.grid.ColumnModel({
                    defaults: {
                        //width: width / 4,
                        sortable: true,
                        menuDisabled: true
                    },
                    columns: []
                });

             startBool = false;
        }

            $('#gridDiv').empty();
            // ExtJS 그리드 생성
            grid = new Ext.grid.GridPanel({
                height: 550,
                store: store,
                colModel : colModel,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'gridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: 50,
                    store: store,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });

            index = 0;
    };

//////////////Render End (Customer, Contract, 1st)   ///////////////


//////////////////////   Grid Function   /////////////////////
    var result = function(val) {

        if (val == "Failure") {
            return '<span style="color:red;">' + val + '</span>';
        } else if(val == ""){
            return '<span style="color:red;">Failure</span>';
        }
        return val;
    };

    var cell_grid = function(currentCellValue, metadata, record, rowIndex, colIndex, doctorOrderStore) {

        var docRecords = doctorOrderStore.getAt(rowIndex);
        var val = docRecords.data.Status+"";
        if (val == "Failure") {
            if (currentCellValue == null) {
                 return "";
            } else {
                return '<span style="color:red;">' + currentCellValue + '</span>';
            }
        }
        return currentCellValue;
    };

    var address_grid = function(currentCellValue, metadata, record, rowIndex, colIndex, doctorOrderStore) {

        var docRecords = doctorOrderStore.getAt(rowIndex);
        var address = docRecords.data.address1+" "+docRecords.data.address2+" "+docRecords.data.address3+" ("+docRecords.data.address+")";

        var val = docRecords.data.Status+"";
        if (val == "Failure") {
            return '<span style="color:red;">' + address + '</span>';
        }
        return address;
    };
    
    </script>

</head>
<body>

    <!-- 탭 전체 (S) -->
    <div id="deviceRegMax">
        <ul>
        <c:if test="${editAuth == 'true'}">
        	<li><a href="#shipmentFile" id="_shipmentFile">Shipment File Management</a></li>
            <li><a href="#batchReg"   id="_batchReg"><fmt:message key="aimir.device.batchRegistration"/></a></li>
            <li><a href="#singleReg"  id="_singleReg"><fmt:message key="aimir.device.singleRegistration"/></a></li>
        </c:if>
            <li><a href="#regHistory" id="_regHistory"><fmt:message key="aimir.device.registrationHistory"/></a></li>
        </ul>

		<!-- Shipment File TAB (S) -->
		<div id="shipmentFile" style="padding-top: 17px;">
			<div id="shipmentFileTabs" class="tabcontentsbox">
			<label class="check ">File Download</label>
			
			<!-- File Download (S) -->
			<table class="searchoption wfree">
				<tr>
					<td class="blue12pt padding-r10px" style="padding-left: 10px;">Shipment Template File download</td>
			
					<td class="padding-r10px"><fmt:message key="aimir.bulkReg.lable.asset"/></td>
					<td>
						<select name="select" id="shipment_TemplateSelect" onChange="javascript:shipmentTemplateChange();" style="width:120px">
							<option value="MCU"  ><fmt:message key="aimir.mcu"/></option>
							<option value="Modem"><fmt:message key="aimir.modem"/></option>
							<option value="Meter"><fmt:message key="aimir.meter"/></option>
						</select>
					</td>
					<td colspan="3" id="shipmentTemplateSelectDiv" class="margin-t1px" style="display:none">
						<span>
							<select name="select" id="shipmentTemplateSelect" onChange="javascript:shipmentTemplateDetailChange();" style="width:120px"></select>
						</span>
					</td>
					
					<td colspan="3" id="shipmentTemplateSelectDiv" class="margin-t1px">
						<span class="am_button margin-t1px">
							<a id="shipmentTemplateBtnDown" href='${ctx}/temp/DCU_shimpment_template.xls'><fmt:message key="aimir.download"/></a>
						</span>
					</td>
				</tr>
				
				<tr>
					<td class="blue12pt padding-r10px" style="padding-left: 10px;">Shipment File download</td>
					
					<td class="padding-r10px"><fmt:message key="aimir.bulkReg.lable.asset"/></td>
					<td>
						<select name="select" id="shipment_Select" onChange="javascript:shipmentChange();" style="width:120px">
							<option value="MCU"  ><fmt:message key="aimir.mcu"/></option>
							<option value="Modem"><fmt:message key="aimir.modem"/></option>
							<option value="Meter"><fmt:message key="aimir.meter"/></option>
						</select>
					</td>
					<td colspan="3" id="shipmentSelectDiv" class="margin-t1px">
						<span>
							<select name="select" id="shipmentSelect" onChange="javascript:shipmentDetailChange();" style="width:120px"></select>
						</span>
					</td>
					
					<td class="margin-t1px" style="width:40px;padding-left: 10px;padding-right: 10px;">Model</td>
					<td colspan="3" id="shipmentModelSelectDiv" class="margin-t1px">
						<span>
							<select name="select" id="shipmentModelSelect" style="width:150px"></select>
						</span>
					</td>
					
					<td class="margin-t1px" style="width:90px;padding-left: 10px;padding-right: 10px;">Purchase Order Number</td>
					<td><input id="shipmentPurchaseOrder" type="text" style="width: 100px;"></td>
			
					<td colspan="3" id="shipmentSelectDiv" class="margin-t1px">
						<span class="am_button margin-t1px">
							<a id="shipmentBtnDown" href="javaScript:openMSISDN_FileReport();"><fmt:message key="aimir.download"/></a>
						</span>
					</td>
				</tr>
				
				<tr>
					<td class="blue12pt padding-r10px" style="padding-left: 10px;">MSISDN Template File download</td>
					<td colspan="3" class="margin-t1px">
						<span class="am_button margin-t1px">
							<a href='${ctx}/temp/MSISDN_shipment_template.xls'><fmt:message key="aimir.download"/></a>
						</span>
					</td>
				</tr>
				
				<tr>
					<td class="blue12pt padding-r10px" style="padding-left: 10px;">SIM Card Template File download</td>
					<td colspan="3" class="margin-t1px">
						<span class="am_button margin-t1px">
							<a href='${ctx}/temp/SIM_card_shipment_template.xls'><fmt:message key="aimir.download"/></a>
						</span>
					</td>
				</tr>
			</table>
			<!-- File Download (E) -->
			
			<!-- File Import (S) -->
			<br/><br/>
			<label class="check ">File Import</label>
			<table class="searchoption wfree">
				<tr>
                    <td class="blue12pt padding-r10px" align="right" style="padding-left: 10px;">Shipment File Import</td>
                    <td class="padding-r10px">Device</td>
                    <td>
                        <select name="select" id="shipmentImportSelect" onChange="javaScript:shipmentImportChange();"  style="width:120px">
                            <option value="MCU"  ><fmt:message key="aimir.mcu"/> </option>
                            <option value="Modem"><fmt:message key="aimir.modem"/></option>
                            <option value="Meter"><fmt:message key="aimir.meter"/></option>
                        </select>
                    </td>
                    <td id="shipmentImportDetailSelectDiv" class="margin-t1px" style="display:none">
                        <span><select name="select" id="shipmentImportDetailSelect" style="width:120px"></select></span>
                        <span><input type="text" id="shipmentImportFilename" name="shipmentImportFilename" style="width:250px" readonly="readonly"/></span>
                        <span><input type="hidden" id="shipmentImportFilepath" style="width:200px"/></span>
                    </td>
                    <td>
                        <span class="am_button margin-r5"><a href="#" id="getShipmentImportTempFileName"><fmt:message key="aimir.button.search"/></a></span>
                        <span class="am_button"><a onclick="importExcelFile();" class="on">Import</a></span>
                    </td>
                </tr>
			</table>
			<table class="searchoption wfree">
			<tr>
                <td class="blue12pt padding-r10px" align="right" style="padding-left: 10px;">MSISDN File Import</td>
                <td id="msisdnImportDetailSelectDiv" class="margin-t1px" style="padding-left: 5px;">
                	<span><input type="text" id="msisdnImportFilename" name="msisdnImportFilename" style="width:250px" readonly="readonly"/></span>
                	<span><input type="hidden" id="msisdnImportFilepath" style="width:200px"/></span>
                </td>
                <td>
                    <span class="am_button margin-r5"><a href="#" id="getMsisdnImportTempFileName"><fmt:message key="aimir.button.search"/></a></span>
                    <span class="am_button"><a onclick="importMsisdnExcelFile();" class="on">Import</a></span>
                </td>
			</tr>
			</table>
			<table class="searchoption wfree">
			<tr>
                <td class="blue12pt padding-r10px" align="right" style="padding-left: 10px;">SIM Card File Import</td>
                <td class="margin-t1px">
                	<span><input type="text" id="simCardImportFilename" name="simCardImportFilename" style="width:250px" readonly="readonly"/></span>
                	<span><input type="hidden" id="simCardImportFilepath" style="width:200px"/></span>
                </td>
                <td>
                    <span class="am_button margin-r5"><a href="#" id="getSimCardImportTempFileName"><fmt:message key="aimir.button.search"/></a></span>
                    <span class="am_button"><a onclick="importSimCardExcelFile();" class="on">Import</a></span>
                </td>
			</tr>
			</table>
			<!-- File Import (E) -->
			
			<!-- Import History (S) -->
			<br/><br/>
			<label class="check ">Import History</label>
			<table class="searchoption wfree">
				<tr>				
					<td class="padding-r10px" style="padding-left: 10px;">Search Date</td>
					<td colspan="1" name='select' class="padding-r20px">
						<span><input id="shipmentImportStartDate" class="day" type="text"></span>
						<span><input value="~" class="between" type="text"></span>
						<span><input id="shipmentImportEndDate" class="day" type="text"></span>
						<input id="shipmentImportStartDateHidden" type="hidden">
						<input id="shipmentImportEndDateHidden" type="hidden">
					</td>
					
					<td class="padding-r10px"><fmt:message key="aimir.bulkReg.lable.asset"/></td>
					<td>
						<select name="select" id="importHistorySelect" onChange="javascript:importHistoryChange();" style="width:120px">
							<option value="MCU"  ><fmt:message key="aimir.mcu"/></option>
							<option value="Modem"><fmt:message key="aimir.modem"/></option>
							<option value="Meter"><fmt:message key="aimir.meter"/></option>
						</select>
					</td>
					<td colspan="3" id="importHistorySelectDiv" class="margin-t1px">
						<span>
							<select name="select" id="importHistoryDetailSelect" onChange="javascript:importHistoryDetailChange();" style="width:120px"></select>
						</span>
					</td>
					<td class="margin-t1px" style="width:70px;padding-left: 10px;">File Name</td>
					<td><input id="importHistoryFileName" type="text" style="width: 120px;"></td>
					<td>
                        <span class="am_button margin-r5"><a href="javascript:getImportHistoryGrid()" class="on"><fmt:message key="aimir.button.search"/></a></span>
                    </td>
				</tr>
			</table>
			<div id="importHistoryGridDiv" class="margin10px"></div>
			<!-- Import History (E) -->
			
			</div>
		</div>

        <!-- 1ST 탭 : 일괄등록 (S) -->
        <div id="batchReg">

                <!-- 1ST 탭의 내용 : 검색조건 (S) -->
                <div class="search-bg-withouttabs">
                    <div class="searchoption-container">
                        <table class="searchoption wfree">
                            <tr>
                                <td class="blue12pt padding-r10px ">
                                    <fmt:message key="aimir.device.batchRegistration"/>
                                    <fmt:message key="aimir.template"/>
                                    <fmt:message key="aimir.download"/>
                                </td>

                                <td class="padding-r10px">
                                    <fmt:message key="aimir.bulkReg.lable.asset"/>
                                </td>
                                <td>
                                    <select name="select" id="batchRegTemplateSelect" onChange="javascript:templateChange();" style="width:120px">
                                        <option value="MCU"  ><fmt:message key="aimir.mcu"/></option>
                                        <option value="Modem"><fmt:message key="aimir.modem"/></option>
                                        <option value="Meter"><fmt:message key="aimir.meter"/></option>
                                        <option value="Customer"><fmt:message key="aimir.customer"/></option>
                                        <option value="MeterMapper">MeterMapper</option>
                                    </select>
                                </td>
                                <td colspan="3" id="templateSelectDiv" class="margin-t1px" style="display:none">
                                    <span>
                                        <select name="select" id="templateSelect" onChange="javascript:templateDetailChange();" style="width:120px"></select>
                                    </span>
                                    <span class="am_button margin-t1px">
                                        <a id="btnDown" href='${ctx}/temp/MCU_template.xls'><fmt:message key="aimir.download"/></a>
                                    </span>
                                </td>
                                <!-- <td><input type="hidden" id="templateSelect" style="width:100px"/></td> -->
                            </tr>
                            <tr>
                                <td class="blue12pt  padding-r10px" align="right"><fmt:message key="aimir.device.batchRegistration"/></td>
                                <td class="padding-r10px"><fmt:message key="aimir.bulkReg.lable.asset"/></td>
                                <td>
                                    <select name="select" id="batchRegSelect" onChange="javaScript:batchRegChange();"  style="width:120px">
                                        <option value="MCU"  ><fmt:message key="aimir.mcu"/> </option>
                                        <option value="Modem"><fmt:message key="aimir.modem"/></option>
                                        <option value="Meter"><fmt:message key="aimir.meter"/></option>
                                        <option value="Customer"><fmt:message key="aimir.customer"/></option>
                                        <option value="MeterMapper">MeterMapper</option>
                                    </select>
                                </td>
                                <td id="detailSelectDiv" class="margin-t1px" style="display:none">
                                    <span><select name="select" id="detailSelect" style="width:120px"></select></span>
                                    <span><input type="text" id="filename" name="filename" style="width:250px"/></span>
                                    <span><input type="hidden" id="filepath" style="width:200px"/></span>
                                </td>
                                <td>
                                    <span class="am_button margin-r5"><a href="#" id="getTempFileName"><fmt:message key="aimir.button.search"/></a></span>
                                    <span class="am_button"><a onclick="registExcelFile();" class="on"><fmt:message key="aimir.button.register"/></a></span>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <!-- 1ST 탭의 내용 : 검색조건 (E) -->



                <!--Flexlist 등록결과-->
                <!--<div id='flexDivTop' class="height10px"></div>-->
                <div id='gridDiv' style="min-height: 400px; height: 400px; padding-right: 7px; padding-left: 7px; padding-bottom: 7px; padding-top: 2px;"></div>

            </div>
        <!-- 1ST 탭 : 일괄등록 (E) -->




        <!-- 2ND 탭 : 개별등록 (S) -->
        <div id="singleReg">


            <!-- 2ND 탭 : 서브탭 전체 (S)-->
            <div id="singleRegTabs">
                <div style="position: absolute; right: 0; padding-right: 20px"><fmt:message key="aimir.hems.inform.requiredField"/></div>
                <ul>
                    <li><a href="#singleMCU"   id="_singleMCU"  ><fmt:message key="aimir.mcu"/> </a></li>
                    <li><a href="#singleModem" id="_singleModem"><fmt:message key="aimir.modem"/></a></li>
                    <li><a href="#singleMeter" id="_singleMeter"><fmt:message key="aimir.meter"/></a></li>
                </ul>

                <!-- 2ND 탭 : 서브탭 (1st) : 집중기 (S)-->
                <div id="singleMCU" class="tabcontentsbox">

                    <form id="mcuInsertForm">
                    <div class="blueline">
                        <ul class="width"><!-- <ul class="deviceregist_option_list"> -->
                        <li class="padding">

                            <table class="search">
                                <tr>
                                    <th><fmt:message key="aimir.mcutype"/><font color="red">*</font></th>
                                    <td><select id="singleRegMCUmcuType" name="sysType" onchange="javascript:getModelListByVendorMCU();"></select></td>
                                    <th><fmt:message key="aimir.mcuid"/><font color="red">*</font></th>
                                    <th><input type="text" id="singleRegMCUmcuId" name="sysID"></th>
                                    <td><em class="am_button"><a onclick="javascript:singleRegMCUIsMCUDuplicate();"><fmt:message key="aimir.checkDuplication"/></a></em></td>
                                    <td><fmt:message key="aimir.mcu.name"/></td>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuName" name="sysName"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.view.mcu39"/><font color="red">*</font></th>
                                    <td><select id="singleRegMCUmcuCommType" name="protocolType.id"></select></td>
                                    <th><fmt:message key="aimir.ipaddress"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuIpAddr" name="ipAddr"></td>
                                    <th><fmt:message key="aimir.mcumobile"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuMobile"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.vendor"/><font color="red">*</font></th>
                                    <td><select id="singleRegMCUmcuVendor" name="model.deviceVendor.id" onchange="javascript:getModelListByVendorMCU();"></select></td>
                                    <th><fmt:message key="aimir.model"/><font color="red">*</font></th>
                                    <td colspan="2"><select id="singleRegMCUmcuModel" name="model.id"></select></td>
                                    <td colspan="3"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.fw.hwversion"/><font color="red">*</font></th>
                                    <!-- <td><select id="singleRegMCUmcuHwVer" name="sysHwVersion"></select></td> -->
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuHwVer" name="sysHwVersion"></td>
                                    <th><fmt:message key="aimir.sw.version"/><font color="red">*</font></th>
                                    <!-- <td colspan="2"><select id="singleRegMCUmcuSwVer" name="sysSwVersion"></select></td> -->
                                     <td colspan="2"><input type="text" id="singleRegMCUmcuSwVer" name="sysSwVersion"></td>
                                    <th>SW Revision<font color="red">*</font></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuSwReVer" name="sysSwRevision"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.latitude"/></th>
                                    <td><input type="text" id="singleRegMCUmcuLatitude" name="gpioX"></td>
                                    <th><fmt:message key="aimir.logitude"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuLogitude" name="gpioY"></td>
                                    <th><fmt:message key="aimir.altitude"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuAltitude" name="gpioZ"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.location"/><font color="red">*</font></th>
                                    <td>
                                        <input type="text" id="singleRegMCUmcuLoc" name="singleRegMCUmcuLoc">
                                        <input type="hidden" id="locationIdMcu" name="location.id" value="" />
                                    </td>
                                    <th><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMCUmcuDetailLoc" name="sysLocation"></td>

                                    <th><fmt:message key="aimir.install.pic" /></th>
                                    <th><input type="text" style="width:200px" id="singleRegMCUmcuPhoto"></th>
                                    <td><em class="am_button"><a href="#" id="uploadTempImg" class="on"><fmt:message key="aimir.button.search" /></a></em></td>
                                </tr>
                            </table>
                            <div id="treeDivMcuOuter" class="tree-billing auto" style="display:none;">
                                <div id="treeDivMcu"></div>
                            </div>
                        </li></ul>
                        <ul class="width">
                        <li class="padding">

                            <div class="btn-confirm">
                                <em class="am_button"><a onClick="javascript:mcuInsert();"><fmt:message key="aimir.button.confirm"/></a></em>&nbsp;
                                <em class="am_button"><a onClick="javascript:initSingleRegMCU();"><fmt:message key="aimir.cancel"/></a></em>
                            </div>

                        </li></ul>
                    </div>
                    </form>

                </div>
                <!-- 2ND 탭 : 서브탭 (1st) : 집중기 (E)-->


                <!-- 2ND 탭 : 서브탭 (2nd) : 모뎀 (S)-->
                <div id="singleModem" class="tabcontentsbox">

                    <form id="modemInfoForm">
                    <div class="blueline">
                        <ul class="width"><!-- <ul class="deviceregist_option_list"> -->
                        <li class="padding">

                            <table class="search">
                                <tr>
                                    <th><fmt:message key="aimir.modem.type"/><font color="red">*</font></th>
                                    <td><select id="singleRegModemModemType" onChange="javascript:getModelListByVendorModem()"></select></td>
                                    <th><fmt:message key="aimir.modemid"/><font color="red">*</font></th>
                                    <td width="145px" style="padding-right: 5px"><input type="text" id="singleRegModemDeviceSerial" name="deviceSerial"></td>
                                    <td><em class="am_button"><a onclick="javascript:singleRegModemIsModemDuplicate();"><fmt:message key="aimir.checkDuplication"/></a></em></td>
                                    <th><fmt:message key="aimir.mcuid"/></th>
                                    <th><input type="text" id="singleRegModemMcuId" name="mcu.sysId" onkeyup="javascript:singleRegModemMcuIdChange();"></th>
                                    <td><em class="am_button"><a onClick="javascript:singleRegModemIsMCUDuplicate();"><fmt:message key="aimir.validation.check"/></a></em></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.fw.hwversion"/></th>
                                    <td><select id="singleRegModemHwVersion" name="hwVer"></select></td>
                                    <th><fmt:message key="aimir.sw.version"/></th>
                                    <td colspan="2"><select id="singleRegModemSwVersion" name="swVer"></select></td>
                                    <th>BUILD</th>
                                    <td colspan="2"><select id="singleRegModemSWRevision" name="fwRevision"></select></td>
                                </tr>
            <!-- jhkim vendor & model 추가 -->
                                <tr>
                                    <th><fmt:message key="aimir.vendor"/><font color="red">*</font></th>
                                    <td><select id="singleRegModemVendor" name="model.deviceVendor.id" onChange="javascript:getModelListByVendorModem();"></select></td>
                                    <th><fmt:message key="aimir.model"/><font color="red">*</font></th>
                                    <td colspan="2"><select id="singleRegModemModel" name="model.id"></select></td>
                                </tr>

                                <tr>
                                    <th><fmt:message key="aimir.location"/><font color="red">*</font></th>
                                    <td>
                                        <input name="singleRegModemLoc" id='singleRegModemLoc' class="billing-searchword" type="text"/>
                                        <input type='hidden' id='locationIdModem' name="location.id" value='' />
                                    </td>
                                    <th><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></th>
                                    <td width="145px" style="padding-right: 5px"><input type="text" id="singleRegModemLocDetail" name="address"></td>
                                    <td colspan="2">&nbsp;</td>
                                    <td colspan="2">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="8" class="lightgray11pt">
                                    <input type="hidden" id="supplierId"    name="supplier.id"   value=""/>
                                    <input type="hidden" id="modemTypeName" name="modemTypeName" value=""/>
                                    <span><input type="checkbox" id="singleRegModemCheck" class="trans"></span><fmt:message key="aimir.device.singlereg.check" />.</td>
                                </tr>
                            </table>
                            <div id="treeDivModemOuter" class="tree-billing auto" style='display:none;'>
                                <div id="treeDivModem"></div>
                            </div>

                        </li></ul>
                        <ul class="width">
                        <li class="padding">

                            <div class="btn-confirm">
                                <em class="am_button"><a onClick="javascript:insertModem();"><fmt:message key="aimir.button.confirm"/></a></em>&nbsp;
                                <em class="am_button"><a onClick="javascript:initSingleRegModem();"><fmt:message key="aimir.cancel"/></a></em>
                            </div>

                        </li></ul>
                    </div>
                    </form>

                </div>
                <!-- 2ND 탭 : 서브탭 (2nd) : 모뎀 (E)-->


                <!-- 2ND 탭 : 서브탭 (3rd) : 미터 (S)-->
                <div id="singleMeter" class="tabcontentsbox">

                    <form id="meterInfoFormEdit">
                    <div class="blueline">
                        <ul class="width"><!-- <ul class="deviceregist_option_list"> -->
                        <li class="padding">

                            <table class="search">
                                <tr>
                                    <th><fmt:message key="aimir.metertype"/><font color="red">*</font></th>
                                    <td><select id="singleRegMeterMeterType" name="meterType.id" onChange="javascript:getModelListByVendor();"></select></td>
                                    <th><fmt:message key="aimir.meterid"/><font color="red">*</font></th>
                                    <th><input type="text" id="singleRegMeterMdsId" name="mdsId"></th>
                                    <td><em class="am_button"><a onclick="javascript:singleRegMeterIsMeterDuplicate();"><fmt:message key="aimir.checkDuplication"/></a></em></td>
                                    <th><fmt:message key="aimir.modemid"/></th>
                                    <th><input type="text" id="singleRegMeterModemSerial" name="modem.deviceSerial" onkeyup="javascript:singleRegMeterModemSerialChange();" onblur="javascript:singleRegMeterModemSerialOnBlur();"></th>
                                    <td><em class="am_button"><a onClick="javascript:singleRegMeterIsModemDuplicate();"><fmt:message key="aimir.validation.check" /></a></em></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.vendor"/><font color="red">*</font></th>
                                    <td><select id="singleRegMeterVendor" name="model.deviceVendor.id" onChange="javascript:getModelListByVendor();"></select></td>
                                    <th><fmt:message key="aimir.model"/><font color="red">*</font></th>
                                    <td colspan="2"><select id="singleRegMeterModel" name="model.id"></select></td>
                                    <th><fmt:message key="aimir.portnumber"/></th>
                                    <td colspan="2"><input type="text" id="singleRegMeterPort" name="modemPort"></td>
                                </tr>
                                <tr>
                                    <th><fmt:message key="aimir.location"/><font color="red">*</font></th>
                                    <td>
                                        <input name="searchWord" id='searchWord' class="billing-searchword" type="text"/>
                                        <input type='hidden' id='locationId' name="location.id" value='' />
                                    </td>
                                    <th><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></th>
                                    <th><input type="text" id="singleRegMeterLocDetail" name="address"></th>
                                    <td colspan="2">&nbsp;</td>
                                    <td colspan="2">&nbsp;</td>
                                </tr>
                                <tr></tr>
                                <tr>
                                    <td colspan="8" class="lightgray11pt">
                                    <input type="hidden" id="supplierId" name="supplier.id" value=""/>
                                    <span><input type="checkbox" id="singleRegMeterCheck" class="trans"></span><fmt:message key="aimir.device.singlereg.check" />.
                                    </td>
                                </tr>
                            </table>
                            <div id="treeDivMeterOuter" class="tree-billing auto" style='display:none;'>
                                <div id="treeDivMeter"></div>
                            </div>
                        </li></ul>
                        <ul class="width">
                        <li class="padding">

                            <div class="btn-confirm">
                                <em class="am_button"><a onClick="javascript:insertMeterInfo();"><fmt:message key="aimir.button.confirm"/></a></em>&nbsp;
                                <em class="am_button"><a onClick="javascript:initSingleRegMeter();"><fmt:message key="aimir.cancel"/></a></em>
                            </div>

                        </li></ul>
                    </div>
                    </form>

                </div>
                <!-- 2ND 탭 : 서브탭 (3rd) : 미터 (E)-->

            </div>
            <!-- 2ND 탭 : 서브탭 전체 (E)-->

        </div>
        <!-- 2ND 탭 : 개별등록 (E) -->




        <!-- 3RD 탭 : 등록이력 (S) -->
        <div id="regHistory">

            <!-- 3RD 탭의 내용 : 검색조건 (S) -->
            <div class="search-bg-withouttabs">
                <!-- jhkim 날짜 검색 추가 -->
                <div class="dayoptions-bt">
                <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
                </div>
                <div class="dashedline"></div>

                <div class="searchoption-container">
                    <table class="searchoption wfree">
                        <tr>
                            <td class="padding-r20px"><fmt:message key="aimir.bulkReg.lable.asset"/></td>
                            <td class="padding-r20px">
                                <select id="regLogDeviceType" onChange="javascript:regLogDeviceTypeChange();" style="width:130px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                    <option value="Customer"><fmt:message key="aimir.customer"/></option>
                                    <option value="MCU"><fmt:message key="aimir.mcu"/></option>
                                    <option value="Meter"><fmt:message key="aimir.meter"/></option>
                                    <option value="Modem"><fmt:message key="aimir.modem"/></option>
                                </select>
                            </td>

                            <td id="regLogSubDeviceTypeLabel" class="padding-r20px"><fmt:message key="aimir.bulkReg.lable.asset"/> <fmt:message key="aimir.type2"/></td>
                            <td class="padding-r20px">
                                <select id="regLogSubDeviceType" onChange="javascript:getVendorListBySubDeviceType();" style="width:180px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                </select>
                            </td>

                            <td class="padding-r20px"><fmt:message key="aimir.vendor"/></td>
                            <td class="padding-r20px">
                                <select id="regLogVendor" onChange="javascript:getDeviceModelsByVenendorId();" style="width:160px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                </select>
                            </td>

                            <td class="padding-r20px"><fmt:message key="aimir.model"/></td>
                            <td>
                                <select id="regLogModel" style="width:130px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                </select>
                            </td>
                            <td></td>
                        </tr>
                        <tr>
                            <td class="padding-r20px"><fmt:message key="aimir.bulkReg.lable.asset"/> <fmt:message key="aimir.id"/></td>
                            <td class="padding-r20px"><input id="regLogDeviceID" type="text" style="width:130px"></td>

                            <td class="padding-r20px"><fmt:message key="aimir.device.RegistrationType"/></td>
                            <td class="padding-r20px">
                                <select id="regLogRegType" style="width:180px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                    <option value="Auto"><fmt:message key="aimir.autoReg"/></option>
                                    <option value="Bulk"><fmt:message key="aimir.bulkReg"/></option>
                                    <option value="Intergration"><fmt:message key="aimir.integrationReg"/></option>
                                    <option value="Manual"><fmt:message key="aimir.manualReg"/></option>

                                </select>
                            </td>

                            <td class="padding-r20px"><fmt:message key="aimir.device.RegResult"/></td>
                            <td class="padding-r20px">
                                <select id="regLogRegResult" style="width:160px">
                                    <option value=""><fmt:message key="aimir.all"/></option>
                                    <option value="SUCCESS"><fmt:message key="aimir.success"/></option>
                                    <option value="FAIL"><fmt:message key="aimir.failure"/></option>
                                </select>
                            </td>

                            <td>
                                <div id="btn"><ul style="margin-left: 0px"><li><a href="javascript:searchRegLog()" class="on"><fmt:message key="aimir.button.search" /></a></li></ul></div>
                            </td>
                        </tr>
                    </table>
                </div>

            </div>
            <!-- 3RD 탭의 내용 : 검색조건 (E) -->


            <!--Flexlist 등록결과-->
            <div class="height10px"></div>
            <div class="gadget_body">
                <div id="deviceRegLogGridDiv"></div>
            </div>
        </div>
        <!-- 3RD 탭 : 등록이력 (E) -->


    </div>
    <!-- 탭 전체 (E) -->



    </body>
</html>