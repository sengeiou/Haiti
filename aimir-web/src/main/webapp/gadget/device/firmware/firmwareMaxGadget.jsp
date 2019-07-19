<!-- 
	가젯 : Firmware Operation Management Gadget For S-Project
	설명 : 기기별로 펌웨어를 관리하고 업데이트 할 수 있는 가젯
	       단일, 다수 장비의 펌웨어를 일괄 관리 할수 있음
	비고 : 기존 펌웨어 가젯을 대체하여 S-Project용으로 신규 개발
	sejin han
 -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>Firmware Management (MaxGadget)</title>

<!-- STYLE -->
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet"
	type="text/css" title="blue">
<link href="${ctx}/js/extjs/ux/css/Spinner.css" rel="stylesheet"
	type="text/css">

<style type="text/css">
/* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
TABLE {
	border-collapse: collapse;
	width: auto;
}

.remove {
	background-image: url(../../images/allOff.gif) !important;
}

.accept {
	background-image: url(../../images/allOn.png) !important;
}

@media screen and (-webkit-min-device-pixel-ratio:0) {
	.x-grid3-row td.x-grid3-cell {
		padding-left: 0px;
		padding-right: 0px;
	}
}
/* ext-js grid header 정렬 */
.x-grid3-hd-inner {
	text-align: center !important;
	font-weight: bold;
}

/* 그리드의 정렬 및 컬럼 옵션 메뉴를 제거 */
.x-grid3-hd-btn {
	display: none;
	visibility: hidden;
}

#target{
	width: 430px;
	background-color: #D9E5FF
}

#rightDiv2{
	height: 740px;
}

#cloneOnTabDiv{
	
}
</style>

<!-- LIB -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/ext-all.js"></script>
<!--  spinner를 사용하기 위해 추가 -->
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/ux/Spinner.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/ux/SpinnerField.js"></script>
<!-- location(DSO)-->
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/tree/location.tree.js"></script>
</head>

<body>
	<!-- SCRIPT -->
	<script type="text/javascript" charset="utf-8">

	var temp_filePath = "";
	var temp_fileName = "";
	var temp_ext = "";
	var temp_finalFilePath = "";
	// var temp_fwDownURL = "";
	var temp_crc = "";
	var temp_firmwareId = "";

	// 웹
	var supplierId = "${supplierId}";
	var locationId = "${locationId}";
	
	// 기본 업로드 정보
	var loginId = '';
	var filePath = '';
	var fileName = '';
	
	// 대상 장비 타입 관리 (default MCU)
	var targetDeviceType = 'dcu';

	// 펌웨어 그리드 - 선택된 아이템 정보 관리
	var sRecord;
	
	// 한번에 OTA 실행할 수 있는 기기수  -> 한페이지에 표시할 기기수 (추후 수정필요)
	var maxNumber = 100; // default
	
	//Excel 이용한 search인지 구분하는 변수
	var excelOn = "OFF"
	
	// 기기 검색시 사용되는 변수들 
	var deviceId="";
	var modelId=""; 
	var fwVersion="";
	var fwVersion2="";
	var hwVersion="";
	var fwRevison="";
	var excelDevcieList
	
	var chkparent = false;   
	var sfilename = "";	
	
	// 펌웨어 파일 검색시 사용되는 변수들
	var sFileName="";
	var sModelName="";
	var modelName="";
	var modelNameStr="";
	var fwVer="";
	var sModelId="";
	var sFwVer="";
	var searchDeviceType="RF";
	
	var firmwareId = "";
	var sfirmwareIssueFileName = "";
	var sfirmwareIssueModelName = "";
	var sfirmwareIssueFwVer = "";	
	var sfirmwareIssueDeviceType = "dcu";
	var sfirmwareIssueLocationId = "";
	
	var sfirmwareIssueHistoryFileName = "";
	var sfirmwareIssueHistoryModelName = "";
	var sfirmwareIssueHistoryFwVer = "";	
	var sfirmwareIssueHistoryDeviceType = "dcu";
	var sfirmwareIssueHistoryStep = "All";
	var sfirmwareIssueHistoryLocationId = "";
	var sfirmwareIssueHistoryTargetId = "";
	var sfirmwareIssueHistoryIssueDate = "";
	var firmwareIssueHistoryLength = 0;
	var sfirmwareIssueHistoryStartDateHidden = "";
	var sfirmwareIssueHistoryEndDateHidden = "";
	
	var firmwareIssueSelect ="false";
	
	//location정보가 없을 때 그리드 출력을 막음
	var chkLocation=1;
	
	//체크박스 수
	var chkCount=0;
	
	//
	var tempModelId="";
	var dcuName="";
	var sDcuName="";
	var fileInfo;
	
	//글자색
    var red = '#F31523'; // security error
    var setColorFrontTag = "<b style=\"color:";
    var setColorMiddleTag = ";\">"; 
    var setColorBackTag = "</b>";
	/**
	 * 공통 모듈
	 */
	 
	//location(DSO) 가져오기
	$(function() {
		locationTreeGoGo('treeDivA', 'searchWord', 'sLocationId');
	});

	$(function() {
		locationTreeGoGo('treeDivB', 'searchWord_FirmwareIssue', 'sfirmwareIssueLocationId');
	});
	
	$(function() {
		locationTreeGoGo('treeDivC', 'searchWord_FirmwareIssueHistory', 'sfirmwareIssueHistoryLocationId');
	});
	
	 $(window).resize(function() {
		 getFwListGrid();
	});
	 
	$(document).ready(function () {
		$.ajaxSetup({
	        async: false
	    });
		
		Ext.Ajax.timeout = 300000;
		
		Ext.QuickTips.init();
		
		// 유저 세션 정보 가져오기
	    $.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    loginId = json.loginId;
	                    isAdmin = json.isAdmin;
	                    if(!isAdmin){ // admin일 경우 지역상관 없이 전부 검색
		                    locationId = json.locationId;
	                    	// 해당기능 사용안함.
		                    if(locationId == "" || locationId == null){ 
		            	    	//chkLocation =0; // locationId가 없으면 chkLocation 변경(0을 넣어서 검색이 안되게)
		            	    	//Ext.Msg.alert('Warn', 'User has no location information.');
		            		}
		                    
		                    $('#searchWord').hide();
		                    //$('#searchWord_FirmwareIssue').hide();
		                    $('#searchWord_FirmwareIssueHistory').hide();
		                    
	                     } else {
	                     }
	                    	 
	                    if(json.maxMeters != null){
	                    	maxNumber = json.maxMeters;
	                    	if(maxNumber == 0)
	                    		maxNumber = 250000;
	                    }else{
	                    	//maxMeters가 null 일때 처리
	                    }
	                    //검색 옵션 초기화
	                    clearSearchOptions();
	                }
	            }
	    );
		
        var locDateFormat = "yymmdd";
        $("#sfirmwareIssueStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sfirmwareIssueEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

        $("#sfirmwareIssueHistoryStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sfirmwareIssueHistoryEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

        $("#executeDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        
        $('#sHour').css('background-color','#A6A6A6');
 		$('#sMin').css('background-color','#A6A6A6');
 		$('#executeDate').css('background-color','#A6A6A6');
 		
		$('#firmwareTab').tabs();
		$('#rightTab').tabs();
		
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
                    $("#sfirmwareIssueStartDate").val(dateFullName);
                    $("#sfirmwareIssueEndDate").val(dateFullName);
                    
                    $("#sfirmwareIssueHistoryStartDate").val(dateFullName);
                    $("#sfirmwareIssueHistoryEndDate").val(dateFullName);
                });

        $("#sfirmwareIssueStartDateHidden").val(setDate);
        $("#sfirmwareIssueEndDateHidden").val(setDate);
        
        $("#sfirmwareIssueHistoryStartDateHidden").val(setDate);
        $("#sfirmwareIssueHistoryEndDateHidden").val(setDate);
        
        $("#executeDateHidden").val(setDate);
        
        sfirmwareIssueHistoryStartDateHidden = $("#sfirmwareIssueHistoryStartDateHidden").val();
        sfirmwareIssueHistoryEndDateHidden = $("#sfirmwareIssueHistoryEndDateHidden").val();
        
        $(function() {
            $('#_firmwareTab').bind('click', function(event) {
            	
            });
        });
        
        $(function() {
            $('#_firmwareHistoryTab').bind('click', function(event) {
            	firmwareHistoryTabInit();
            	getFirmwareIssueGrid();
            	getFirmwareIssueHistoryGrid();
            });
        });
        
        
        $(function() {
            $('#_otaTab').bind('click', function(event) {
            	deviceSearch();
            });
        });

        $(function() {
            $('#_cloneOnTab').bind('click', function(event) {
            	deviceSearch2();
            	$('#executeDate').attr("disabled",true);
         		$('#executeDateHidden').attr("disabled",true);
         		$('#sHour').attr("disabled",true);
         		$('#sMin').attr("disabled",true);
         		$('#executeDate').datepicker("disable");
            });
        });
        
        // Execute Type 클릭 제어
        $(function() {
            $('#runnow').bind('click', function(event) {
            	$("input:radio[value='timesetting']").attr("checked",false);
         		$('#executeDate').attr("disabled",true);
         		$('#executeDateHidden').attr("disabled",true);
         		$('#sHour').attr("disabled",true);
         		$('#sMin').attr("disabled",true);
         		$('#executeDate').val('');
         		$('#executeDateHidden').val('');
         		$('#sHour').val('');
         		$('#sMin').val('');
         		$('#executeDate').datepicker("disable");
            });
        });
        
        $(function() {
            $('#timesetting').bind('click', function(event) {
            	$("input:radio[value='runnow']").attr("checked",false);
         		$('#executeDate').attr("disabled",false);
         		$('#executeDateHidden').attr("disabled",false);
         		$('#sHour').attr("disabled",false);
         		$('#sMin').attr("disabled",false);
         		$('#executeDate').datepicker("enable");
            });
        });
        
        $('#sfirmwareIssueDeviceType').selectbox();
        $('#sfirmwareIssueHistoryDeviceType').selectbox();
        $('#sfirmwareIssueHistoryStep').selectbox();
        $('#sCommandType').selectbox();
	    // 드랍 메뉴
	    $('#deviceType').selectbox();
	    $('#deviceType').change( changeDeviceType() );
	    $('#deviceType2').selectbox();
	    $('#deviceType2').change( changeDeviceType2() );
	    $('#deviceType_history').selectbox();
	    $('#sfirmwareIssueDeviceType').change( changeDeviceType_history() );
	    $('#sCommandType').change( changeExecuteType_history() );
	    $('#uploadWindowDiv').hide();
	    
	    // Item Update 버튼과 우측 체크박스 비활성화
	    $('#otaBtn').hide()
	    $('#fwUpdateButton').hide();
	    $('#fwDeleteButton').hide();
	    $('#dcuName').hide();
	    $('#deviceTypeDiv').hide();
	    $('#dcuNameLabel').hide();
	    
	    //$("#executeType").hide();
	    $(".commandType").hide();
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
	
	function firmwareHistoryTabInit() {
		
    }
	
	function clearSearchOptions() {
		modelId="00"; // 펌웨어 파일을 선택했을 때만 기기목록이 보이게  하기위해서 잘못된 검색조건을 넣어준다 //수정필요
        fwVersion="";
        /* hwVersion="";
        fwRevison=""; */
	}
	
	function changeDeviceType_history(){
		var firmwareIssuDeviceType = $("#sfirmwareIssueDeviceType").val();
		if(firmwareIssuDeviceType=="modem" || firmwareIssuDeviceType=="meter")
			$(".commandType").show();
		else
			$(".commandType").hide();
		searchFirmwareIssueList();
		
		// Refresh History Grid
		firmwareId = '';
        sfirmwareIssueHistoryLocationId = '';
        sfirmwareIssueHistoryFileName = '';
        sfirmwareIssueHistoryModelName = ''; 
        sfirmwareIssueHistoryFwVer = ''; 
        sfirmwareIssueHistoryDeviceType = '';
     	$("#sfirmwareIssueHistoryDeviceType").val('');        
     	$("#sfirmwareIssueHistoryStep").val('All');     	
     	sfirmwareIssueHistoryIssueDate = '';
		$("#sfirmwareIssueHistoryStartDate").val('');
        $("#sfirmwareIssueHistoryEndDate").val('');
        $("#sfirmwareIssueHistoryStartDateHidden").val('');
        $("#sfirmwareIssueHistoryEndDateHidden").val('');
        sfirmwareIssueHistoryStartDateHidden = $("#sfirmwareIssueStartDateHidden").val();
        sfirmwareIssueHistoryEndDateHidden = $("#sfirmwareIssueEndDateHidden").val();
        
        getFirmwareIssueHistoryGrid();
	}

	function changeExecuteType_history(){
		searchFirmwareIssueList();
	}
	
	function changeDeviceType2(){
		searchDeviceType = $('#deviceType2').val();
		getDeviceList();
	}
	
	function changeDeviceType() { 
		targetDeviceType = $('#deviceType').val();	// 항목 변경시 선택된 값을 보관
		$('#otaBtn').hide();
		$('#fwUpdateButton').hide();
		$('#fwDeleteButton').hide();
		
		$('#tdCreator').text("");
		$('#tdModelName').text("");
		$('#tdVersion').text("");
		$('#tdFileName').text("");
		
		modelId = "00";
		chkCount = 0;
		
		$("#rightTab").tabs("select",0);
		
		if (chkLocation != 0) {
			getFwListGrid();
			
			if (targetDeviceType == "dcu" || targetDeviceType == "dcu-kernel" || targetDeviceType == "dcu-coordinate") {
				$('#dcuName').hide();
				 $('#dcuNameLabel').hide();
				 $('#deviceTypeDiv').hide();
				 $('#deviceTypeDivTemp').show();
				 $('#_cloneOnTab').hide();
				 getDeviceList();
				//ibk
			} else if (targetDeviceType == "modem") {
				 $('#dcuName').show();
				 $('#dcuNameLabel').show();
				 $('#deviceTypeDiv').hide();
				 $('#deviceTypeDivTemp').show();
				 $('#sLocationId2').val('');
				 $('#dcuName2').val('');
				 $('#_cloneOnTab').hide();
				 getDeviceList();	
				 getDeviceListModem();		
			} else if (targetDeviceType == "meter"){
				 $('#dcuName').show();
				 $('#dcuNameLabel').show();
				 $('#deviceTypeDiv').show();
				 $('#deviceTypeDivTemp').hide();
				 $('#sLocationId2').val('');
				 $('#dcuName2').val('');
				 $('#firmwareVersiontxt').val('');	
				 $('#sMeterTypeDiv').show();
				 $('#_cloneOnTab').show();
				 getDeviceList();
				 getDeviceListMeter();
			} else {
				 $('#dcuName').show();
				 $('#dcuNameLabel').show();
				 $('#deviceTypeDiv').show();
				 $('#deviceTypeDivTemp').hide();
				 $('#_cloneOnTab').hide();
				 getDeviceList();
			}
		}
	}
	
	function changeDeviceTypeForFirmwareIssueHistory() { 
		firmwareId = "";
	}
	
	// 펌웨어 리스트 그리드
	var fwListStore;
	var fwListCol;
	var fwListGrid;
	var getFwListGrid = function(){
		var grWidth = $('#leftDiv').width()-21;
        var pageSize = 30;
        
        fwListStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/device/firmware/getFirmwareFileList.do",
            baseParams : {
                'supplierId' : supplierId,  
                'equip_kind' : targetDeviceType, // dcu, dcu-kernel, dcu-coordinate, modem, meter
                'fileName' : sFileName,
                'modelName' :  sModelName,
                'modelId' : sModelId,
                'fwVer' : sFwVer
            },
            root: 'rtnStr',
            fields: [
                     'no','filename','modelname','hwver','fwver','fwrev','creationdate', 'creator',
                     'modelId','checkSum','crc','imageKey','filePath', 'fileUrlPath', 'firmwareId', 'fileExists', 'fId'
                     ],
        });
        
        fwListCol = new Ext.grid.ColumnModel({
            columns: [
                      {header: 'No', dataIndex: 'no', width: 40},
                      {header: 'Date of Creation', dataIndex: 'creationdate'},
                      {header: 'File Name', dataIndex: 'filename', editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'FW Ver.', dataIndex: 'fwver', editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Model', dataIndex: 'modelname', editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Manufacturer', dataIndex: 'creator'},
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
                       hideable : false,
                       //width : 120
                  },
         });
        
        fwListGrid = new Ext.grid.EditorGridPanel({
            store : fwListStore,
            colModel : fwListCol,
            sm : new Ext.grid.RowSelectionModel({
    			singleSelect:true,
    			listeners: {
                    rowselect: function(smd, row, rec) {
                    	var data = rec.data;
                        //선택한 항목에 해당하는 내용 출력
                        fwItemSelected(data);
                        
						//모델Id에 해당되는 기기 출력
                        modelId = data.modelId;
						
						// ota시 넘겨주는 modelId
                        tempModelId = data.modelId;
							
						// firmware id를 반환
						temp_firmwareId = data.firmwareId;
                        //changeDeviceType();
                        
                        locationId = "";
                        
  						modelName = data.modelname;
                        
                        //fwVer = data.fwver;
                	    // 선택된 record data 저장 (update에 사용)
                	    /* if(data.fileExists =="No File"){
							sRecord = null;
							modelId = -1;
						}else{
							 sRecord = data;
						} */ //ibk
						fileInfo = data;
                	    deviceSearch();
                	    $('#otaBtn').show();
                	    $('#fwUpdateButton').show();
                	    $('#fwDeleteButton').show();
                	    
                		// 모델명이 "SR" 일때만 Clone on 탭 출력
						modelNameStr = modelName.substr(modelName.length-2,2);
						if(modelNameStr=="SR" && targetDeviceType=="modem"){
							$('#_cloneOnTab').show();
							$('#sMeterTypeDiv').hide();
							deviceSearch2();
						}
						else if(targetDeviceType=="meter"){
							$('#_cloneOnTab').show();
							$('#sMeterTypeDiv').show();
							deviceSearch2();
						}
						else{
							$('#_cloneOnTab').hide();
							$('#sMeterTypeDiv').hide();
					        $("#rightTab").tabs("select",0);
					        deviceSearch();
						}
						
                    }
                }
    		}),
            autoScroll : true,
            height : 560,
            width : grWidth,
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
        
        
        $('#fwListGridDiv').html(' ');
        
        fwListGrid.reconfigure(fwListStore, fwListCol);
        fwListGrid.render('fwListGridDiv');        
	} // 끝; 펌웨어 리스트 그리드
	
	function searchList(){
		sFileName = $('#sFileName').val();
		sModelName = $('#sModelName').val();
		sFwVer = $('#sFwVer').val();
		getFwListGrid();
	}
	
	function searchFirmwareIssueList(){
		sfirmwareIssueFileName = $('#sfirmwareIssueFileName').val();
		sfirmwareIssueModelName = $('#sfirmwareIssueModelName').val();
		sfirmwareIssueFwVer = $('#sfirmwareIssueFwVer').val();
		sfirmwareIssueDeviceType = $('#sfirmwareIssueDeviceType').val();
		sfirmwareIssueLocationId = $('#sfirmwareIssueLocationId').val();
		
		if(sfirmwareIssueLocationId == -1) {
			sfirmwareIssueLocationId = "";
		}
		
		getFirmwareIssueGrid();
	}
	
	function searchFirmwareIssueHistoryList(){
		if(($('#sfirmwareIssueHistoryLocationId').val()) =="" || ($('#sfirmwareIssueHistoryLocationId').val()) == null){
			
		}else
			sfirmwareIssueHistoryLocationId = $('#sfirmwareIssueHistoryLocationId').val();
		sfirmwareIssueHistoryFileName = $('#sfirmwareIssueHistoryFileName').val();
		sfirmwareIssueHistoryModelName = $('#sfirmwareIssueHistoryModelName').val();
		sfirmwareIssueHistoryFwVer = $('#sfirmwareIssueHistoryFwVer').val();
		sfirmwareIssueHistoryDeviceType = $('#sfirmwareIssueHistoryDeviceType').val();
		sfirmwareIssueHistoryStep = $('#sfirmwareIssueHistoryStep').val();
		sfirmwareIssueHistoryTargetId = $('#sfirmwareIssueHistoryTargetId').val();
		//sfirmwareIssueHistoryIssueDate = $('#sfirmwareIssueHistoryIssueDate').val();
        //sfirmwareIssueHistoryStartDateHidden = $("#sfirmwareIssueHistoryStartDateHidden").val();
        //sfirmwareIssueHistoryEndDateHidden = $("#sfirmwareIssueHistoryEndDateHidden").val();
        
		if(sfirmwareIssueHistoryLocationId == -1) {
			sfirmwareIssueHistoryLocationId = "";
		}
		
		getFirmwareIssueHistoryGrid();
	}
	
	function resetFirmwareIssueList(){
		$('#sfirmwareIssueLocationId').val('');
		$('#sfirmwareIssueFileName').val('');
		$('#sfirmwareIssueModelName').val('');
		$('#sfirmwareIssueFwVer').val('');
		$('#sfirmwareIssueDeviceType').val('');
		$('#sfirmwareIssueStartDate').val('');
		$('#sfirmwareIssueEndDate').val('');
		$('#sfirmwareIssueStartDateHidden').val('');
		$('#sfirmwareIssueEndDateHidden').val('');
	}
	
	function resetFirmwareIssueHistoryList(){
		$('#sfirmwareIssueHistoryLocationId').val('');
		$('#sfirmwareIssueHistoryFileName').val('');
		$('#sfirmwareIssueHistoryModelName').val('');
		$('#sfirmwareIssueHistoryFwVer').val('');
		$('#sfirmwareIssueHistoryDeviceType').val('');
		$('#sfirmwareIssueHistoryStep').val('All');
		$('#sfirmwareIssueHistoryTargetId').val('');
		$('#sfirmwareIssueHistoryIssueDate').val('');
		$('#sfirmwareIssueHistoryStartDateHidden').val('');
		$('#sfirmwareIssueHistoryEndDateHidden').val('');
		$('#sfirmwareIssueHistoryStartDate').val('');
		$('#sfirmwareIssueHistoryEndDate').val('');
	}
	
	function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
	
    // FirmwareIssue Grid (S)
   	var firmwareIssueStore;
   	var firmwareIssueCol;
   	var firmwareIssueGrid;
	var firmwareIssueGridOn = false;
   	var getFirmwareIssueGrid = function() {
   		var grWidth = $('#firmwareIssueGridDiv').width();
        var pageSize = 10;
           
        firmwareIssueStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/firmware/getFirmwareIssueList.do",
            baseParams : {
                'supplierId' : supplierId,
                'locationId' : $('#sfirmwareIssueLocationId').val(),
                'fileName'   : sfirmwareIssueFileName,
                'modelName'  : sfirmwareIssueModelName,
                'fwVer'      : sfirmwareIssueFwVer,	
                'targetType' : sfirmwareIssueDeviceType,
                'startDate'  : $('#sfirmwareIssueStartDateHidden').val(),	
                'endDate'    : $('#sfirmwareIssueEndDateHidden').val(),
                'commandType' : $('#sCommandType').val()
            },
            totalProperty: 'totalCount',
            root: 'rtnStr',
            fields: [
                     'no','locationName','location','fileName','issueDate','issueDateFormat','model','fwVer','targetType',
                     'totalCount','step1Count','step2Count','step3Count','step4Count',
                     'step5Count','step6Count','step7Count','fwId','executeType','commandType'
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
        
        firmwareIssueCol = new Ext.grid.ColumnModel({
            columns: [
						{header: 'No.', tooltip: 'No.', dataIndex: 'no', width: 40, align: 'center'},
						{header: 'Location', tooltip: 'Location', dataIndex: 'locationName', width:grWidth/14},
						{header: 'File Name', tooltip: 'File Name', dataIndex: 'fileName', width: grWidth/9,
							renderer : function(value, me, record, rowNumber, columnIndex, store) {
									if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && (record.data.commandType == 1)){
										sfilename = "Clone On";
										return sfilename;
									} else	if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && (record.data.commandType == 2)){
										sfilename = "Clone Off";
										return sfilename;
									} else {
										sfilename = record.data.fileName;
										return sfilename;
									}
                				}},
						{header: 'Issue Date', tooltip: 'Issue Date', dataIndex: 'issueDateFormat', width:grWidth/14},
						{header: 'Model', tooltip: 'Model', dataIndex: 'model', width:grWidth/13},
						{header: 'FW Ver', tooltip: 'FW Ver', dataIndex: 'fwVer', width:grWidth/13},
						{header: 'Target Type', tooltip: 'Target Type', dataIndex: 'targetType', width:grWidth/14},
						{header: 'Execute Type', tooltip: 'Execute Type', dataIndex: 'executeType', align: 'center', width:grWidth/14,
							renderer : function(value, me, record, rowNumber, columnIndex, store) {
             		 		if(record.data.executeType == "0") //Security Error
                 				return "Clone OTA";
             		 		else if(record.data.executeType == "1")
             		 			return "DCU";
             		 		else if(record.data.executeType == "2")
             		 			return "HES";
             		 		else if(record.data.executeType == "3")
             		 			return "Modem";
             		 		else
             		 			return "Unknown";
                				}},
						{header: 'Execute Started', tooltip: 'Job Started', dataIndex: 'step1Count', width:grWidth/10},
						{header: 'Took Command', tooltip: 'Took Command', dataIndex: 'step2Count', width:grWidth/8},
						{header: 'Execute Ended', tooltip: 'Job Ended', dataIndex: 'step3Count', width:grWidth/10},
						{header: 'Result', tooltip: 'Result', dataIndex: 'step4Count', width:grWidth/14},
						{header: 'Firmware Update', tooltip: 'Firmware Update', dataIndex: 'step5Count', width:grWidth/10},
						//{header: 'DCU Frimware Updated', tooltip: 'DCU Frimware Updated', dataIndex: 'step6Count', hidden: true},
						//{header: 'Intergrity Deviation', tooltip: 'Intergrity Deviation', dataIndex: 'step7Count', hidden: true},
						{header: 'Total Count', tooltip: 'Total Count', dataIndex: 'totalCount', align: 'right', width:grWidth/12},
						//{header: 'Command Type', tooltip: 'Command Type', dataIndex: 'commandType', hidden: true},
						
                  ],
			defaults: {
				align : 'center',
				sortable : true,
				menuDisable : true,
				hideable : false,
				renderer : addTooltip
			},
         });

        if(!firmwareIssueGridOn){
            $('#firmwareIssueGridDiv').html(' ');
            firmwareIssueGrid = new Ext.grid.GridPanel({            	
            	store : firmwareIssueStore,
                colModel : firmwareIssueCol,
                sm : new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(smd, row, rec) {
                            var data = rec.data;
                            firmwareIssueSelect = "true";
                            if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && (data.commandType == 1)){
								sfilename = "Clone On";
							} else if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && (data.commandType == 2)){
								sfilename = "Clone Off";
							} else {
								sfilename = data.fileName;
							}
                            rowClickEvent(smd);
                        }
                    }
                }),
                renderTo : 'firmwareIssueGridDiv',
                autoScroll : true,
                height : 290,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    // forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: firmwareIssueStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            firmwareIssueGridOn = true;
        } else {
        	firmwareIssueGrid.setWidth(grWidth);
        	var bottomToolbar = firmwareIssueGrid.getBottomToolbar();
        	firmwareIssueGrid.reconfigure(firmwareIssueStore, firmwareIssueCol);
        	bottomToolbar.bindStore(firmwareIssueStore);
        }
   	}
   	// FirmwareIssue Grid (E)
       
	// FirmwareIssueHistory Grid (S)
   	var firmwareIssueHistoryStore;
   	var firmwareIssueHistoryCol;
   	var firmwareIssueHistoryGrid;	
	var firmwareIssueHistoryGridOn = false;
   	var getFirmwareIssueHistoryGrid = function() {
   		var grWidth = $('#firmwareIssueHistoryGridDiv').width();
        var pageSize = 10;
        sfirmwareIssueHistoryTargetId = $('#sfirmwareIssueHistoryTargetId').val();
        
        if(firmwareIssueSelect == "false")
        	sfirmwareIssueHistoryLocationId ="0"
        firmwareIssueHistoryStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/firmware/getFirmwareIssueHistoryList.do",
            baseParams : {
                'supplierId' : supplierId,
                'firmwareId' : firmwareId,
                'locationId' : sfirmwareIssueHistoryLocationId,
                'fileName'   : sfirmwareIssueHistoryFileName,
                'modelName'  : sfirmwareIssueHistoryModelName,
                'fwVer'      : sfirmwareIssueHistoryFwVer,	
                'targetType' : sfirmwareIssueHistoryDeviceType,
                'step' : sfirmwareIssueHistoryStep,
                'targetId'   : sfirmwareIssueHistoryTargetId,
                // firmware issue 그리드에서 선택한 날짜 정보를 대입
                'startDate'  : sfirmwareIssueHistoryStartDateHidden,
                'endDate'    : sfirmwareIssueHistoryEndDateHidden,
             	// 'startDate'  : $('#sfirmwareIssueHistoryStartDateHidden').val(),
                // 'endDate'    : $('#sfirmwareIssueHistoryEndDateHidden').val(),
                'issueDate'  : sfirmwareIssueHistoryIssueDate,
            },
            totalProperty: 'totalCount',
            root: 'rtnStr',
            fields: [
                     'no','locationName','location','fileName','issueDate','issueDateFormat','model','fwVer',
                     'targetType','targetId','step', 'resultStatus','updateDate'
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
        
        firmwareIssueHistoryCol = new Ext.grid.ColumnModel({
            columns: [
                      {
                      	header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck_firmwareIssueHistory' onClick='chkAll_firmwareIssueHistory()' /></div>",
                      	width: 30,
                      	align:'center',
                      	renderer: dataChk
                      },
                      {header: 'No.', tooltip: 'No.', dataIndex: 'no', width: 40, align: 'center'},
                      {header: 'Location', tooltip: 'Location', dataIndex: 'locationName', width:grWidth/12, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'File Name', tooltip: 'File Name', dataIndex: 'fileName', width:grWidth/8, 
                    	  renderer : function(value, me, record, rowNumber, columnIndex, store) {
								if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && sfilename == "Clone On"){
									return "Clone On";
								} else if(($("#sfirmwareIssueDeviceType").val()=="modem" || $("#sfirmwareIssueDeviceType").val()=="meter") && sfilename == "Clone Off"){
									return "Clone Off";
								} else
									return record.data.fileName;
          				},
                    	  editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Issue Date', tooltip: 'Issue Date', dataIndex: 'issueDateFormat', width:grWidth/9},
                      {header: 'Model', tooltip: 'Model', dataIndex: 'model', width:grWidth/10, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'FW Ver', tooltip: 'FW Ver', dataIndex: 'fwVer', width:grWidth/12, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Target Type', tooltip: 'Target Type', dataIndex: 'targetType', width:grWidth/12, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Target ID', tooltip: 'Target ID', dataIndex: 'targetId', width:grWidth/10, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Step', tooltip: 'Step', dataIndex: 'step', width:grWidth/9, editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Status', tooltip: 'Result Status', dataIndex: 'resultStatus', width:grWidth/2, align:'left', editor:new Ext.form.TextField({allowBlank:false})},
                      {header: 'Update Date', tooltip: 'Update Date', dataIndex: 'updateDate', width:grWidth/9}
                  ],
			defaults: {
        	   align : 'center',
        	   sortable : false,
               menuDisable : true,
               hideable : false,
               renderer : addTooltip
			},
         });
        
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) { 
       		var chk_firmwareId = firmwareId;
        	var chk_location = record.data.location;
        	var chk_targetType = record.data.targetType;
        	var chk_targetId = record.data.targetId;
        	var chk_issueDate = record.data.issueDate;
        	
        	firmwareIssueHistoryLength++;
        	
        	return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" +chk_firmwareId+"\,"+chk_location+"\,"+chk_targetType+"\,"+chk_targetId+"\,"+chk_issueDate+ "\" /></div>";
        }

        if(!firmwareIssueHistoryGridOn){
            $('#firmwareIssueHistoryGridDiv').html(' ');
            firmwareIssueHistoryGrid = new Ext.grid.EditorGridPanel({
            	clicksToEdit: 1,
            	store : firmwareIssueHistoryStore,
                colModel : firmwareIssueHistoryCol,
                sm : new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(smd, row, rec) {
                            var data = rec.data;
                            rowClickEvent_firmwareIssueHistory(smd);
                        }
                    }
                }),
                renderTo : 'firmwareIssueHistoryGridDiv',
                autoScroll : true,
                height : 350,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    // forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                bbar: new Ext.PagingToolbar({
                pageSize: pageSize,
                store: firmwareIssueHistoryStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
	            })
            });
            firmwareIssueHistoryGridOn = true;
        }else{
        	firmwareIssueHistoryGrid.setWidth(grWidth);
        	var bottomToolbar = firmwareIssueHistoryGrid.getBottomToolbar();
        	firmwareIssueHistoryGrid.reconfigure(firmwareIssueHistoryStore, firmwareIssueHistoryCol);
        	bottomToolbar.bindStore(firmwareIssueHistoryStore);
        }

   	}
   	// FirmwareIssueHistory Grid (E)
   	
   	// firmware issue 그리드 - row 선택시
   	function rowClickEvent(selectionModel) {
        var s= selectionModel;
        var row = s.getSelected();
        var selectedTargetType = "";
		
		firmwareId = row.get('fwId');
        sfirmwareIssueHistoryLocationId = row.get('location');
        sfirmwareIssueHistoryFileName = row.get('fileName');
        sfirmwareIssueHistoryModelName = row.get('model'); 
        sfirmwareIssueHistoryFwVer = row.get('fwVer'); 

        sfirmwareIssueHistoryDeviceType = row.get('targetType');
        selectedTargetType = sfirmwareIssueHistoryDeviceType.toLowerCase();
     	$("#sfirmwareIssueHistoryDeviceType").val(selectedTargetType);        
     	$("#sfirmwareIssueHistoryStep").val('All');     	
        
     	sfirmwareIssueHistoryIssueDate = row.get('issueDate');
     	
        // issue 그리드에서 선택한 날짜 데이터, issue history 날짜 데이터에 대입
        $("#sfirmwareIssueHistoryStartDate").val($("#sfirmwareIssueStartDate").val());
        $("#sfirmwareIssueHistoryEndDate").val($("#sfirmwareIssueEndDate").val());
        
        $("#sfirmwareIssueHistoryStartDateHidden").val($("#sfirmwareIssueStartDateHidden").val());
        $("#sfirmwareIssueHistoryEndDateHidden").val($("#sfirmwareIssueEndDateHidden").val());
        
        sfirmwareIssueHistoryStartDateHidden = $("#sfirmwareIssueStartDateHidden").val();
        sfirmwareIssueHistoryEndDateHidden = $("#sfirmwareIssueEndDateHidden").val();
        
        getFirmwareIssueHistoryGrid();
   	}
   	
   	// firmware issue history에서 선택된 데이터 정보
   	function rowClickEvent_firmwareIssueHistory(selectionModel) {
        var s= selectionModel;
        var row = s.getSelected();
         
        // alert("firmwareId : " + firmwareId);
        // alert("row.get('location') : " + row.get('location'));
        // alert("row.get('targetType') : " + row.get('targetType'));
        // alert("row.get('targetId') : " + row.get('targetId'));
        // alert("row.get('issueDate') : " + row.get('issueDate')); 
   	}
   	
   	// retry 버튼 누르면, firmware issue history 체크된 데이터 정보를 가져온다.
   	var targetId=new Array();
   	function retryButtonClick() {
		var selectedDeviceCount = 0;
		var firmwareDataJsonString = "";
		var location="";
		targetId=new Array();
				console.log("checkDeviceValue : ",checkDeviceValue);
		
		for(var i = 0 ; i < firmwareIssueHistoryLength ; i++){
			if($("input:checkbox[id='chkDeviceId"+i+"']").is(":checked") == true){
				var checkDeviceID = "#chkDeviceId"+i;
				var checkDeviceValue = $(checkDeviceID).val();
				var checkDeviceValueSplit = checkDeviceValue.split(',');
				console.log("checkDeviceID : ",checkDeviceID);
				console.log("checkDeviceValue : ",checkDeviceValue);
				/* var firmwareData = {
					firmwareId : checkDeviceValueSplit[0],
					location   : checkDeviceValueSplit[1],
					targetType : checkDeviceValueSplit[2],
					targetId   : checkDeviceValueSplit[3],
					issueDate  : checkDeviceValueSplit[4]
				}; */
				location = checkDeviceValueSplit[1];
				targetId.push(checkDeviceValueSplit[3]);
				targetDeviceType=checkDeviceValueSplit[2].toLowerCase();
				fileInfo = {fId : checkDeviceValueSplit[0]};
				selectedDeviceCount++;
				/* var firmwareDataJson = JSON.stringify(firmwareData);
				firmwareDataJsonString += firmwareDataJson + "/";
				selectedDeviceCount++; */
			}
		}
		
		if(selectedDeviceCount == '0') {
			Ext.Msg.alert('<fmt:message key="aimir.message"/>','Please select the target.');
			return;
		}
		//targetId = targetId.slice(0,-1);
		
		var TargetList = new Array();
		var Target ={
				locationId : location,
				deviceIdList : targetId
		};
		
		TargetList.push(Target);
		//TargetList.push(Target);
		
		target = JSON.stringify(TargetList);
		otaExecute("true");
		// 마지막 '/'제거
		//firmwareDataJsonString = firmwareDataJsonString.slice(0,-1);
		
		
		/* Ext.Msg.confirm('<fmt:message key="aimir.message"/>', 'The number of the selected devices : <b><font color="red">' + selectedDeviceCount + '</font></b>.<br/> Do you want to execute OTA?', 
			function(btn,text){
				if(btn == 'yes') {
					$.ajax({
						type : "POST",
						data : {
							loginId 	           : loginId,
							selectedDeviceCount    : selectedDeviceCount,
							firmwareDataJsonString : firmwareDataJsonString
						},
						dataType : "json",
						url : '${ctx}/gadget/device/command/cmdOTARetryStart.do',
						success : function(data){
							Ext.Msg.alert('<fmt:message key="aimir.message"/>',data.rtnStr);
							
						},
						error : function(){
							Ext.Msg.alert('<fmt:message key="aimir.message"/>','Failed to Ajax Communication');
						}
					});
				} else {
					Ext.Msg.alert('<fmt:message key="aimir.message"/>','<fmt:message key="aimir.canceled" />');
				}
			}
		); */
    }
   	
	var targetId=new Array();
   	function retryButtonClickAll() {
   		var firmwareDataJsonString = "";
		var location="";
		targetId=new Array();
        var params = {
        		supplierId : supplierId,
                firmwareId : firmwareId,
                locationId : sfirmwareIssueHistoryLocationId,
                fileName   : sfirmwareIssueHistoryFileName,
                modelName  : sfirmwareIssueHistoryModelName,
                fwVer      : sfirmwareIssueHistoryFwVer,	
                targetType : sfirmwareIssueHistoryDeviceType,
                step : sfirmwareIssueHistoryStep,
                targetId   : sfirmwareIssueHistoryTargetId,
                // firmware issue 그리드에서 선택한 날짜 정보를 대입
                startDate  : sfirmwareIssueHistoryStartDateHidden,
                endDate    : sfirmwareIssueHistoryEndDateHidden,
             	// 'startDate'  : $('#sfirmwareIssueHistoryStartDateHidden').val(),
                // 'endDate'    : $('#sfirmwareIssueHistoryEndDateHidden').val(),
                issueDate  : sfirmwareIssueHistoryIssueDate,
        };
        var retryList = new Array();
        $.post("${ctx}/gadget/device/firmware/getFirmwareIssueHistoryListAll.do",
                params,
                function(json) {
                    retryList = json.rtnStr;
                    if (retryList == null) {
                    	retryList = [];
                    }
                }
        );
        
        for(var i = 0 ; i < retryList.length ; i++){
				var checkDeviceValue = retryList[i];
				var checkDeviceValueSplit = checkDeviceValue.split(',');
				
				/* var firmwareData = {
					firmwareId : checkDeviceValueSplit[0],
					location   : checkDeviceValueSplit[1],
					targetType : checkDeviceValueSplit[2],
					targetId   : checkDeviceValueSplit[3],
					issueDate  : checkDeviceValueSplit[4]
				}; */
				location = checkDeviceValueSplit[0];
				targetId.push(checkDeviceValueSplit[2]);
				targetDeviceType=checkDeviceValueSplit[1].toLowerCase();
				fileInfo = {fId : firmwareId};
				/* var firmwareDataJson = JSON.stringify(firmwareData);
				firmwareDataJsonString += firmwareDataJson + "/";
				selectedDeviceCount++; */
		}
		
		if(firmwareIssueSelect == 'false') {
			Ext.Msg.alert('<fmt:message key="aimir.message"/>','Please select the target.');
			return;
		}
		//targetId = targetId.slice(0,-1);
		
		var TargetList = new Array();
		var Target ={
				locationId : location,
				deviceIdList : targetId
		};
		
		TargetList.push(Target);
		//TargetList.push(Target);
		
		target = JSON.stringify(TargetList);
		otaExecute("true");
    }
	
	// 펌웨어 리스트에서 항목 선택시 상단 출력칸에 정보 입력
	function fwItemSelected(data) {
		var fw_version = data.hwver==""?'-':data.fwver;
		
		// set value
		$('#tdCreator').val(data.creator);		// 제조사	
		$('#tdModelName').val(data.modelname);	// 모델명
		$('#tdVersion').val(fw_version);		// F/W 버전
		$('#firmwareVersion2').val(fw_version);		// F/W 버전
		$('#firmwareVersiontxt').val(fw_version);		// F/W 버전
		$('#tdFileName').val(data.filename);	// 파일명
		
		// set text
		$('#tdCreator').text($('#tdCreator').val().trim());	
		$('#tdModelName').text($('#tdModelName').val().trim());
		$('#tdVersion').text($('#tdVersion').val().trim());
		$('#firmwareVersion2').text($('#firmwareVersion2').val().trim());
		$('#firmwareVersiontxt').text($('#firmwareVersiontxt').val().trim());
		
		$('#tdFileName').text($('#tdFileName').val().trim());
		
		// Item Update 버튼과 우측 체크박스 활성화 (잠금해제)
	    $('#fwUpdateButton').show();
		$('#fwDeleteButton').show();
	   // $(':input[type="checkbox"]').removeAttr('disabled');
	    
	}
	
	var locationFirstCheck = true;
	
	// 장비 리스트 그리드(MCU)
	var deviceListStore;
	var deviceListCol;
	var deviceListGrid;
	var deviceListGridOn = false;
	var getDeviceListGrid = function(){
	var grWidth = $('#leftDiv').width()-21;
        deviceListStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: maxNumber}},
            url: "${ctx}/gadget/device/getDcuGridData.do", //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
                supplierId : supplierId,
                mcuId : deviceId,
                mcuType : '',
                locationId : locationId,
                swVersion : fwVersion,
                swRevison : ''/* fwRevison */,
                hwVersion : ''/* hwVersion */,
                installDateStart : '',
                installDateEnd : '',
                filter : '',
                order : '',
                protocol : '',
                dummy : '',
            	mcuStatus : '',
            	mcuSerial : '',
            	modelId : modelId
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ['sysID','model','sysHwVersion','sysSwVersion','swrev','mcuId'],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                },
                load: function(store, record, options){
                    /* if (record.length > 0) {
                        // 데이터 load 후 첫번째 row 자동 선택
                        dcuGrid.getSelectionModel().selectFirstRow();
                    } else {
                        $("#mcuInfoDiv").hide();
                    } */
                    
                    // 처음 loading될때 Default Location 정보 출력
					if(locationFirstCheck == true) {
						//getFirmwareSearchLocationName();
						locationFirstCheck = false;
					}
                    
                    if (record.length <= 0) {
                    }
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
					{header: 'Model', dataIndex: 'model', },
					/* {header: 'HW Version', dataIndex: 'sysHwVersion', }, */
					{header: 'FW Version', dataIndex: 'sysSwVersion', },
					/* {header: 'FW Revision', dataIndex: 'swrev', },    */                
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
                height : 620,
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
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: maxNumber,
                    store: deviceListStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })

                //paging bar
            });
        	
        	$('#deviceListGridDiv').html(' ');
            
            //deviceListGrid.reconfigure(deviceListStore, deviceListCol);
            deviceListGrid.render('deviceListGridDiv');
            deviceListGridOn = true;
            modemListGridOn = false;
            meterListGridOn = false;
        }else {
        	deviceListGrid.reconfigure(deviceListStore, deviceListCol);
        	modemListGridOn = false;
        	meterListGridOn = false;
        	var bottomToolbar = deviceListGrid.getBottomToolbar();
        	bottomToolbar.bindStore(deviceListStore);
        }
        clearSearchOptions();
	} // 끝; 장비 리스트 그리드 (MCU)
	
	
	// 장비 리스트 그리드(MODEM)
	var modemListStore;
	var modemListCol;
	var modemListGrid;
	var modemListGridOn = false;
	var getModemListGrid = function(){
		var grWidth = $('#leftDiv').width()-21;
        
        modemListStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: maxNumber}},
            url: "${ctx}/gadget/device/getModemSearchGrid2.do", //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
            	 sModemType:''
            	,sModemId: deviceId
            	,sInstallState:''
            	,sMcuType:''
            	,sMcuName:''
            	,sModemFwVer: fwVersion
            	,sModemSwRev: ''/* fwRevison */
            	,sModemHwVer: ''/* hwVersion */
        		,sModomStatus:''
            	,sInstallStartDate:''
            	,sInstallEndDate:''
            	,sLastcommStartDate:''
            	,sLastcommEndDate:''
            	,sLocationId: locationId
            	,sOrder:''
            	,sCommState:''
            	,supplierId : supplierId
            	,pageSize : maxNumber
            	,gridType :"extjs"
            	,modelId : modelId
            	,sMeterSerial : ''
            	,chkParent: chkparent
            	, sModuleVersion:''
            },
            totalProperty: 'totalCnt',
            root:'gridData',
            fields: ['modemDeviceSerial','deviceName','hwVer','fwVer', 'fwRevison','id'],
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
                height : 620,
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
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: maxNumber,
                    store: modemListStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })

                //paging bar
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
        	var bottomToolbar = modemListGrid.getBottomToolbar();
        	bottomToolbar.bindStore(modemListStore);
        }
        clearSearchOptions();
	} // 끝; 장비 리스트 그리드(MODEM)
	
	// 장비 리스트 그리드(METER)
	var meterListStore;
	var meterListCol;
	var meterListGrid;
	var meterListGridOn = false;
	
	var getMeterListGrid = function(){
		var grWidth = $('#leftDiv').width()-21;
        
		meterListStore = new Ext.data.JsonStore({
			autoLoad: {params:{start: 0, limit: maxNumber}},
            url: "${ctx}/gadget/device/getMeterSearchGrid.do",  //장비리스트는 MAX가젯 그리드와 공유합니다 (주소동일).
            baseParams : {
            	sMeterType         : '',
                sMdsId             : deviceId,
                sStatus            : '',
                sMcuName           : '',
                sLocationId        : locationId,
                sConsumLocationId  : '',
                sVendor            : '',
                sModel             : modelId,
                sInstallStartDate  : '',
                sInstallEndDate    : '',
                sModemYN           : '',
                sCustomerYN        : '',
                sLastcommStartDate : '',
                sLastcommEndDate   : '',
                sOrder             : '',
                sCommState         : '',
                supplierId         : supplierId,
                sMeterGroup        : '',
                sGroupOndemandYN   : 'N',
                sCustomerId        : '',
                sCustomerName      : '',
                sPermitLocationId  : '',
                sMeterAddress      : '',
                sHwVersion         : ''/* hwVersion */,
                sFwVersion         : fwVersion,
                sGs1               : ''
            },
            root : 'gridData',
            totalProperty : 'totalCnt',
            fields: ['meterMds','modelName', 'hwVer','swVer','meterId'],
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
                height : 620,
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
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: maxNumber,
                    store: meterListStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })

                //paging bar
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
        	var bottomToolbar = meterListGrid.getBottomToolbar();
        	bottomToolbar.bindStore(meterListStore);
        }
        
        clearSearchOptions();
	} // 끝; 장비 리스트 그리드(METER) 
	
	// firmware issue history 체크박스 전체 컨트롤
	function chkAll_firmwareIssueHistory() {
 		if ($("#allCheck_firmwareIssueHistory").is(':checked')) {
 			$("input[name='chkDeviceId']").attr("checked", "checked");
 		} else {
 		    $("input[name='chkDeviceId']").attr("checked", false);
 		}
 	}
	
	// 체크박스 전체 컨트롤
	function chkAll() {
 		if ($("#allCheck").is(':checked')) {
 			$("input[name='chkDeviceId']").attr("checked", "checked");
 		} else {
 		    $("input[name='chkDeviceId']").attr("checked", false);
 		}
 	}

	function chkAll2() {
 		if ($("#allCheck2").is(':checked')) {
 			$("input[name='chkDeviceId']").attr("checked", "checked");
 		} else {
 		    $("input[name='chkDeviceId']").attr("checked", false);
 		}
 	}
	
	// 펌웨어 ITEM ADD/UPDATE 패널 그리기
	var uploadWindow; 
 	var uploadFormPanel;
 	var vendorStore;
 	var fwOptionName;
 	
 	function drawUploadPanel(purpose){
 		// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('uploadWindowPanel')){
 			Ext.getCmp('uploadWindowPanel').close();
 		} 		
 		// 현재 선택한 Target Type 상태를 저장 (사용자 실수 등을 방지)
 		targetDeviceType = $('#deviceType option:selected').val();
 		// 선택한 Target Type에 따라 입력할 항목이 달라짐
 		var targetType = targetDeviceType;
 		
 		if (targetType=='dcu' || targetType=='modem'){
 			fwOptionName = 'Image Key';	
 		} else if(targetType=='meter') {
 			fwOptionName = 'Image Key';
 		}
 		
 		if(purpose == 'add') {
 			// Add 버튼 클릭 시 Panel 그리는 영역
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
 	 		            	id : 'fwversion',
 	 		            	width : 240,
 	 		            	//emptyText: 'Set Parameter Value',
 	 		                fieldLabel: 'F/W Version ', 	 
                            disabled : true               
 	 		            }, 		            {
 	 		            	xtype: 'textfield',
 	 		            	id : 'parameter', 
 	 		            	width : 240,
 	 		                // emptyText: 'Set Parameter Value',
 	 		                fieldLabel: fwOptionName,
 	 		                disabled : true
 	 		            },{
 	 		            	xtype: 'textfield',
 	 		            	id : 'filename',
 	 		            	width : 240,
 	 		                // emptyText: '<fmt:message key="aimir.alert.selectFile"/>',
 	 		                fieldLabel: 'File Name ',
 	 		                disabled : true,
 	 		            },/* {
 			            	xtype: 'textfield',
 			            	id : 'filePath',	
 			            	width : 240,
 			                emptyText: 'Set Parameter Value',
 			                fieldLabel: 'File URL',
 			                disabled : true
 			            }, */
 						{
 			            	xtype: 'textfield',
 			            	id : 'crc',	
 			            	width : 240,
 			                // emptyText: '',
 			                fieldLabel: 'CRC',
 			                disabled : true
 		            	},{
 			            	xtype: 'checkbox',
 			            	id: 'overwrite',
 			            	checked: true,
 			            	hideLabel: true,
                            style: {
 			                   marginTop: '3px'
 			                },
 			                boxLabel: 'File Overwrite'
 		            	},{
 	 		            	xtype: 'label',																			
 	 		            	id : 'infolabel',
 	 		            	style : {
 	 		            		background : '#ffff00'
 	 		            	},
 	 		            	text : " * Choose 'Firmware File' using 'File Upload' button.",
 	 		            }
 	 		        ],
 	 		        buttons: [
 	 		            {
 	 		            	id : 'fileform',
 				    	 	text: 'File Upload',
 				    	 	// click => AjaxUpload
 				    	 	
 				        },{
 				            text: 'OK',
 				            listeners: {
 				            	click: function(btn,e){
 				            		startUpload(purpose);
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
    	} else if(purpose == 'update') {
    		// Update 버튼 클릭 시 Panel 그리는 영역
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
 	 		            	id : 'fwversion',
 	 		            	width : 240,
 	 		            	emptyText: 'Set Parameter Value',
 	 		                fieldLabel: 'F/W Version ', 	                
 	 		            }, 		            {
 	 		            	xtype: 'textfield',
 	 		            	id : 'parameter', 
 	 		            	width : 240,
 	 		                // emptyText: 'Set Parameter Value',
 	 		                fieldLabel: fwOptionName,
 	 		                disabled : true
 	 		            },{
 	 		            	xtype: 'textfield',
 	 		            	id : 'filename',
 	 		            	width : 240,
 	 		                // emptyText: '<fmt:message key="aimir.alert.selectFile"/>',
 	 		                fieldLabel: 'File Name ',
 	 		                disabled : true,
 	 		            },/* {
 			            	xtype: 'textfield',
 			            	id : 'filePath',	
 			            	width : 240,
 			                emptyText: 'Set Parameter Value',
 			                fieldLabel: 'File URL',
 			                disabled : true
 			            }, */
 						{
 			            	xtype: 'textfield',
 			            	id : 'crc',	
 			            	width : 240,
 			                // emptyText: '',
 			                fieldLabel: 'CRC',
 			                disabled : true
 		            	}
 	 		        ],
 	 		        buttons: [
 	 		            {
 	 		            	id : 'fileform',
 				    	 	text: 'File Upload',
 				        },{
 				            text: 'OK',
 				            listeners: {
 				            	click: function(btn,e){
 				            		startUpload(purpose);
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
    	}
 		
	    var uploadWindow = new Ext.Window({
	        id     : 'uploadWindowPanel',
	        title  : 'New Firmware File Upload',
	        pageX : 500,
              pageY : 200,
	        height : 320,
	        width  : 400,
	        layout : 'fit',
	        bodyStyle   : 'padding: 10px 10px 10px 10px;',
	        items  : [uploadFormPanel],
	    });
	    
	    uploadWindow.show();
		    
    	if(purpose == 'add') {
    		getFileUploadModule();
    	} else if(purpose == 'update') {
    		uploadWindow.setTitle('Firmware Information Update');
    		
    		// 'File upload'  버튼 비활성화
    		Ext.getCmp('fileform').disable(true);
    		
    		// 기존 선택한 정보 setting
    		/* $('#sVendor').val(sRecord.creator);
    		$('#sModel').val(sRecord.modelname);
    		$('#fwversion').val(sRecord.fwver); */
    		
    		$('#filename').val(sRecord.filename);
    		$('#crc').val(sRecord.crc);
    		if(sRecord.imageKey == null){
    			$('#parameter').val("-");	
    		} else {
    			$('#parameter').val(sRecord.imageKey);
    		}
    	} 		     		
		    getDeviceVendorsBySupplierId();	
 	}
 	
 	function drawDeletePanel(){
 		// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('deleteWindowPanel')){
 			Ext.getCmp('deleteWindowPanel').close();
 		} 	
 		
 		// 현재 선택한 Target Type 상태를 저장 (사용자 실수 등을 방지)
 		targetDeviceType = $('#deviceType option:selected').val();
 		// 선택한 Target Type에 따라 입력할 항목이 달라짐
 		var targetType = targetDeviceType;
 		
 		// delete 버튼 클릭 시 Panel 그리는 영역
 		var deleteFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset',
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        labelWidth  : 100,
 		        items       : [
 		            {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	},
 		            	text : "You cannot retrieve this file once you delete it.",
 		            }
 		        ],
 		        buttons: [
 		            {
			            text: 'Delete',
			            listeners: {
			            	click: function(btn,e){
			            		startDelete();
			            	}
			            }
			        },{
			            text: 'Cancel',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('deleteWindowPanel').close();
	                        }
	                    }
		        }]
 		    });
 		
 		    var deleteWindow = new Ext.Window({
 		        id     : 'deleteWindowPanel',
 		        title  : 'Firmware File Delete',
 		        pageX : 500,
                pageY : 200,
 		        height : 120,
 		        width  : 400,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [deleteFormPanel],
 		    });
 		    
 		    deleteWindow.show();
 		    // getDeviceVendorsBySupplierId();	
 	}
 	
 	// ITEM ADD 버튼
 	function firmwareAdd(){
 		// 패널 생성
 		drawUploadPanel('add');
 	}
 	
	// ITEM UPDATE 버튼
	function firmwareModify() {
		// 패널 생성
		// 펌웨어 그리드 항목이 선택되어 있어야 함
		drawUploadPanel('update');		
	}
	
	// ITEM DELETE 버튼
 	function firmwareDelete() {
 		// 패널 생성
 		drawDeletePanel();
	}
 	
 	// 제조사 리스트 불러오기 (selectbox 항목 채우기)
 	function getDeviceVendorsBySupplierId() {
        $.getJSON('${ctx}/gadget/system/vendorlist.do', {
            'supplierId' : supplierId
        }, function(returnData) {            
            Ext.getCmp('sVendor').bindStore(new Ext.data.JsonStore({
            	fields : ['id', 'name', 'descr', 'code', 'address'],
            	data : returnData.deviceVendors,
            }));
        });
    };
    
    // 모델 정보 리스트 불러오기 (selectbox 항목 채우기) 
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
	
    var advancedSearchFormPanel;
    var advancedSearchWindow;
    function advancedDeviceSearch() {
            // 아직 안닫힌 경우 기존 창은 닫기
            if(Ext.getCmp('advancedSearchWindow')){
            	Ext.getCmp('advancedSearchWindow').close();
            }
           			advancedSearchFormPanel = new Ext.FormPanel({
    	            id : 'advancedSearchform',
    	            defaultType : 'fieldset',
    	            bodyStyle:'padding:1px 1px 1px 1px',
    	            frame : true,
    	            labelWidth : 100,
    	            items : [
    	                {                  
    	                    xtype: 'datefield',
    	                    id : 'installStartDate',
    	                    fieldLabel: 'Install Date',
    	                    format: 'm/d/Y',
    	                    altFormats: 'm/d/Y',
    	                    submitFormat:'ymd'

    	                },
    	                {                  
    	                    xtype: 'datefield',
    	                    id : 'installEndtDate',
    	                    fieldLabel: ' ~',
    	                    labelSeparator : "",
    	                    format: 'm/d/Y',
    	                    altFormats: 'm/d/Y',
    	                    submitFormat:'ymd'

    	                },
    	                {                  
    	                    xtype: 'datefield',
    	                    id : 'lastCommStartDate',
    	                    fieldLabel: 'Comm. Date',
    	                    format: 'm/d/Y',
    	                    altFormats: 'm/d/Y',
    	                    submitFormat:'ymd'

    	                },
    	                {                  
    	                    xtype: 'datefield',
    	                    id : 'lastCommEndDate',
    	                    fieldLabel: ' ~',
    	                    labelSeparator : "",
    	                    format: 'm/d/Y',
    	                    altFormats: 'm/d/Y',
    	                    submitFormat:'ymd'

    	                },
    	                {
    	                    xtype: 'textfield',
    	                    id : 'hwVer',
    	                    width : 110,
    	                    fieldLabel: 'HW Version'
    	                }
    	            ], // items
    	            buttons : [
    	                {
    	                    id: 'advancedSearchBtn',
    	                    text: 'Search',
    	                    listeners: {
    	                        click: function(btn,e){
    	                            //submit action
    	                            deviceSearch("true"); //advanceddeviceSearch 에서 호출하는게 아니면 적용안하도록 설정해야
    	                        }
    	                    }
    	                },
    	                {
    	                    id: 'CancleBtn',
    	                    text: 'Cancel',
    	                    listeners: {
    	                        click: function(btn,e){
    	                            //submit action
    	                        	Ext.getCmp('advancedSearchWindow').close();
    	                        }
    	                    }
    	                }
    	            ] //buttons
    	        });
           			
           			var height = 240;
           	        var width  = 280;
           	        var pageX  = ($('#leftDiv').width())*0.738;
           	        var advancedSearchWin = new Ext.Window({
           	            id     : 'advancedSearchWindow',
           	            title : 'Advanced Search',
           	            pageX : pageX,
           	            pageY : 70,
           	            height : height,
           	            width  : width,
           	            layout : 'fit',
           	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
           	            items  : [advancedSearchFormPanel],
           	        });

           	     	advancedSearchWin.show();
        	
    }
    var excelSearchDeviceId = "";
    var searchFileUploadModule = function(){
    	excelSearchDeviceId = "";
    	// 파일 업로드 관리, SELECT 버튼 
		new AjaxUpload('excelSearchBtn', {
			name : 'excelSearchBtn',
			action: '${ctx}/gadget/device/getTempFileName2.do',
            data : {
            },
            responseType : false,
            onSubmit : function(file , ext){
                // Allow only images. You should add security check on the server-side.
                if (ext && /^(xls|xlsx)$/.test(ext)){
                    /* Setting data */
                    if(this._input.files[0].size > 1048576){
                    	Ext.Msg.alert('<fmt:message key='aimir.message'/>','File size exceeds 1MB.');
                        return false;
                    }else{
	                    this.setData({
	                        'key': 'This string will be send with the file'
	                    });
                    }
                } else {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Invalid file extension.');
                    return false;
                }
            },
            onComplete : function(file, response){
            	var datas = response.split('|');
            	/* var datas = response.split('|');
                $('#filename').val(file);
                $('#filepath').val(datas[0]);
                //insertDeviceBulkFile(file , response);

                gridTitles = datas[1].split(',');
                setGrid(file, datas[0]); */
                
                $.getJSON('${ctx}/gadget/device/getSearchDeviceId.do'
                        ,{ filePath: datas[0],
                           targetDeviceType: targetDeviceType,
                           modelId: modelId,
                           supplierId: supplierId,
                           sType: searchDeviceType
                		/* fileType:$('#batchRegSelect').val(),
                           supplierId:supplierId,
                           detailType:($('#batchRegSelect').val() == 'MCU') ? "DCU" : option.displayName}, // FileType을 선택후 상세 타입 콤보 선택필요  */
            			},
                        function(result) {
            				excelSearchDeviceId =result.result;
            				excelOn = "ON"
            				 if($('#rightDiv2').width() == 0){			//OTA 탭 누른 경우
            				 
            					//id 검색 결과 유효한 device가 있는 경우
	            				if(excelSearchDeviceId != "" && excelSearchDeviceId != null){
		            				deviceId = excelSearchDeviceId;	
		            				//버전정보에 해당하는 기기출력에 사용됨
		            		        if(sRecord!=null && sRecord!='')
		            				{
		            		        	modelId = sRecord.modelId;
		            		        	if($("input:checkbox[id='checkFwV']").is(":checked") == true) {
		            		        		fwVersion=sRecord.fwver;
		            		        	}
		            				}
		            				
		            		        targetDeviceType = $('#deviceType').val();
		            		        versionList = result.versionList;
		            		        
		            		        if(result.invalidSerial != ""){
	                     			   invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
	                         	   	}
		            				
		            				getDeviceList();
		            				
		            			}else{									//id 검색 결과 유효한 device가 없는 경우
		            				if(result.count==null)
		            					result.count='0/0';
	                     		    invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
		            				deviceId = '-';
		            				targetDeviceType = $('#deviceType').val();
		            				getDeviceList();
	                     	   	} 
	            				
	/*              				   //Ext.Msg.alert('<fmt:message key='aimir.message'/>','This File contains invalid Device ID');
	            				   excelSearchDeviceId =result.result;
	            				   //alert(result.result);
	            				   //alert(result.invalidSerial);
	                        	   if(excelSearchDeviceId != ""){
	                        		   deviceSearch("Excel"); //exceldeviceSearch면 다르게 호출하도록 설정해야 변수하나 만들어서 아이디 리스트 넣고 그걸로 검색하게.. 새창열면 변수 초기화 하는 것도 추가할것.
	                        		   if(result.invalidSerial != ""){ //ibk
	                        			   invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
	                            		   //Ext.Msg.alert('<fmt:message key='aimir.message'/>','Invalid IDs: ' + result.invalidSerial);
	                            	   }
	                        	   }else{
	                        		   //Ext.Msg.alert('<fmt:message key='aimir.message'/>','This file has no valid Device id.');
	                        		   invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
	                        		   deviceSearch("None");
	                        	   }  */
            				 
            				 }else{	 /* sp-1004 clone on 탭의 excel search (start) */
            					//id 검색 결과 유효한 device가 있는 경우
	            				if(excelSearchDeviceId != "" && excelSearchDeviceId != null){
	            					
		            				//버전정보에 해당하는 기기출력에 사용됨
		            				targetDeviceType = $('#deviceType').val();
		            		        
		            		        if(result.invalidSerial != ""){
	                     			   invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
	                         	   	}
            						//clone- on excel search 시작
	            					excelDevcieList = excelSearchDeviceId;	
		            		        deviceSearch2();
		            		        searchParentDevice();
	            				}else{
	                   				if(result.count==null)
		            					result.count='0/0';
	                     		    invalidDeviceSearch(result.invalidSerial, targetDeviceType, result.count);
	                     		    excelDevcieList = '-';
		            				targetDeviceType = $('#deviceType').val();
		            				deviceSearch2();
	            					
	            				}
            				 }/* sp-1004 clone on의 excel search (end) */
                        } /* function(result) end */
            	); /* getJson end */
            }
        });
	}
    
	// Excel Import Search
    var excelSearchFormPanel;
    var excelSearchWindow;
    function excelDeviceSearch() {
    	//Ext.getCmp('retryCount').getValue()
    		if(Ext.getCmp('excelSearchWindow')){
            	Ext.getCmp('excelSearchWindow').close();
            }
           			excelSearchFormPanel = new Ext.FormPanel({
    	            id : 'excelSearchform',
    	            defaultType : 'fieldset',
    	            bodyStyle:'padding:1px 1px 1px 1px',
    	            frame : true,
    	            labelWidth : 100,
    	            items : [{
	                    xtype: 'label',
	                    text: 'Please enter below 10,000 devices id',
	                    labelAlign: 'top',
	                    labelStyle: 'font-weight:bold;'
	                }], // items
    	            buttons : [
    	            	{
    	                    id: 'excelTempleteBtn',
    	                    text: 'Template',
    	                    listeners: {
    	                        click: function(btn,e){
    	                            //submit action
    	                            window.open('${ctx}/temp/Excel_search_template.xlsx', "_self")
    	                        }
    	                    }
    	                },
    	                {
    	                    id: 'excelSearchBtn',
    	                    text: 'Search'
    	                },
    	                {
    	                    id: 'CancleBtn',
    	                    text: 'Cancel',
    	                    listeners: {
    	                        click: function(btn,e){
    	                            //submit action
    	                        	Ext.getCmp('excelSearchWindow').close();
    	                        }
    	                    }
    	                }
    	            ] //buttons
    	        });
           			
           			var height = 120;
           	        var width  = 300;
           	        var pageX  = ($('#leftDiv').width())*0.738;
           	        var excelSearchWin = new Ext.Window({
           	            id     : 'excelSearchWindow',
           	            title : 'Excel Search',
           	            pageX : pageX,
           	            pageY : 51,
           	            height : height,
           	            width  : width,
           	            layout : 'fit',
           	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
           	            items  : [excelSearchFormPanel],
           	        });

           	     	excelSearchWin.show();
           	     	searchFileUploadModule();
        	
    }
    
 // Excel Import Search
    var invalidDeviceFormPanel;
    var invalidDeviceWindow;
    function invalidDeviceSearch(invalidSerial, targetDeviceType, count) {
    		//alert(invalidSerial);
    		if(Ext.getCmp('invalidDeviceWindow')){
            	Ext.getCmp('invalidDeviceWindow').close();
            }
    		invalidDeviceFormPanel = new Ext.FormPanel({
    	            id : 'invalidDeviceform',
    	            defaultType : 'fieldset',
    	            bodyStyle:'padding:1px 1px 1px 1px',
    	            frame : true,
    	            labelWidth : 100,
    	            items : [
    	            	{
    	                    xtype     : 'textarea',
    	                    //grow      : true,
    	                    autoScroll : true,
    	                    name      : 'message',
    	                    fieldLabel: 'Invalid ID(' + count + ')' ,
    	                    labelStyle: 'color: #f00;',
    	                    value      : invalidSerial,
    	                    anchor    : '100%'
    	                },
    	                {
    	                    xtype: 'label',
    	                    text: 'ㅤㅤ( No results for these IDs )',
    	                    labelStyle: 'text-align: center;'
    	                }
    	            ]
    	        });
           			
           			var height = 100;
           	        var width  = 300;
           	        var pageX  = ($('#leftDiv').width())*0.738;
           	        var invalidDeviceWin = new Ext.Window({
           	            id     : 'invalidDeviceWindow',
           	            title : 'Validation Check (' +targetDeviceType.toUpperCase() + ')' ,
           	            pageX : pageX,
           	            pageY : 173,
           	            height : height*(1.51),
           	            width  : width,
           	            layout : 'fit',
           	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
           	            items  : [invalidDeviceFormPanel],
           	        });
           	     invalidDeviceWin.show();
    }
    
	// Search 버튼
	var installStartDate_ad ="";
	var installEndtDate_ad ="";
	var lastCommStartDate_ad ="";
	var lastCommEndDate_ad ="";
	var hwVer_ad="";
	function deviceSearch(isAdvanced) {
		
		if(isAdvanced == "Excel"){
			deviceId = excelSearchDeviceId;
		}else if(isAdvanced == "None"){
			deviceId = '-';
		}else
			deviceId = $('#deviceId').val();
		fwVersion = $('#firmwareVersion').val();
		locationId = $('#sLocationId').val();
		sDcuName = $('#dcuName').val();
		
		if(isAdvanced =="true"){
			if(Ext.getCmp('installStartDate').getValue() != "")
				installStartDate_ad = Ext.getCmp('installStartDate').getValue().format('Ymd');
			else
				installStartDate_ad="";
			if(Ext.getCmp('installEndtDate').getValue() != "")
            	installEndtDate_ad = Ext.getCmp('installEndtDate').getValue().format('Ymd');
			else
				installEndtDate_ad="";
			if(Ext.getCmp('lastCommStartDate').getValue() != "")
				lastCommStartDate_ad = Ext.getCmp('lastCommStartDate').getValue().format('Ymd');
			else
				lastCommStartDate_ad="";
			if(Ext.getCmp('lastCommEndDate').getValue() != "")
				lastCommEndDate_ad = Ext.getCmp('lastCommEndDate').getValue().format('Ymd');
			else
				lastCommEndDate_ad="";
			hwVer_ad = Ext.getCmp('hwVer').getValue();
			
			/* alert(installStartDate_ad);
			alert(installEndtDate_ad);
			alert(lastCommStartDate_ad);
			alert(lastCommEndDate_ad);
			alert(hwVer_ad); */
		}else{
			installStartDate_ad ="";
			installEndtDate_ad ="";
			lastCommStartDate_ad ="";
			lastCommEndDate_ad ="";
			hwVer_ad="";
		}
		
		/* if(locationId == "" || locationId == -1){
			Ext.Msg.alert('<fmt:message key='aimir.message'/>','Please select DSO Location');
			$('#searchWord').select();
			return;
		} */
			
		//버전정보에 해당하는 기기출력에 사용됨
        if(sRecord!=null && sRecord!='')
		{
        	modelId = sRecord.modelId;
        	if($("input:checkbox[id='checkFwV']").is(":checked") == true) {
        		fwVersion=sRecord.fwver;
        	}
		}
		
        targetDeviceType = $('#deviceType').val();
		
        chkCount = 0;
        
        if (chkLocation != 0) {
        	if (targetDeviceType == "dcu" || targetDeviceType == "dcu-kernel" || targetDeviceType == "dcu-coordinate") {
        		//ibk
        		getDeviceList();
        	} else if (targetDeviceType == "modem") {
        		//getModemListGrid();
        		getDeviceList();
        	} else {
        		//getMeterListGrid();
        		getDeviceList();
        	}
        	
        } 
        
		// FW버전으로 검색했을때만 OTA버튼이 나타나게
		/* if($("input:checkbox[id='checkFwV']").is(":checked") == true) {
			$('#otaBtn').show();
		} else {
			$('#otaBtn').hide();
		}*/
	}
	
	/* Clone on,off - deviceSearch*/
	function deviceSearch2(){
		
		//버전정보에 해당하는 기기출력에 사용됨
        if(sRecord!=null && sRecord!='')
		{
        	modelId = sRecord.modelId;
        	if($("input:checkbox[id='checkFwV']").is(":checked") == true) {
        		fwVersion=sRecord.fwver;
        	}
		}
		
		chkParent();
		
		targetDeviceType = $('#deviceType').val();	
		fwVersion2 = $('#firmwareVersion2').val();
		locationId = $('#sLocationId2').val();
		sDcuName = $('#dcuName2').val();
		installStartDate_ad ="";
		installEndtDate_ad ="";
		lastCommStartDate_ad ="";
		lastCommEndDate_ad ="";
		hwVer_ad="";
		if(excelOn == "ON"){
			deviceId = excelDevcieList;
			excelOn = "OFF";
		}else{
			deviceId = "";
		}
		
		if(targetDeviceType=="modem")
			getDeviceListModem();
		else if(targetDeviceType=="meter")
			getDeviceListMeter();
	}
	
	/* SP-1004 Clone on탭  parent의 ID/FW_VER 정보 출력 (start) */
	var parentDeviceGridOn = false;
	var parentDeviceGrid;
	var parentDeviceStore;
	var parentDeviceColModel;
	var parentInfoSize;
	
	function searchParentDevice(){
			
			targetDeviceType = $('#deviceType').val();	
			fwVersion2 = $('#firmwareVersion2').val();
			
			parentDeviceStore = new Ext.data.JsonStore({
				autoLoad:true,
				url:'${ctx}/gadget/device/getParentDeviceList_'+targetDeviceType+'.do',
				baseParams:{
					deviceId : deviceId,
					modelId : modelId,
					supplierId : supplierId,
					fwVersion : fwVersion2, 
				},
				totalProperty: 'total',
				root:'result', 
				listeners:{
					load : function(store, records, success, operation){
						parentInfoSize = store.data.length;
						$('#parentTotal').text('total : '+parentInfoSize);
					}
				},
				fields:['DEVICEID','PARENT_ID','PARENT_VER'] 
			});
			
			parentDeviceColModel = new Ext.grid.ColumnModel({
						columns : [
							{
								header : '<fmt:message key="aimir.deviceId"/>',
								dataIndex: 'DEVICEID',
								align : 'center',
								width : 140
							}, {
								header : '<fmt:message key="aimir.deviceId"/> (PARENT)',
								dataIndex : 'PARENT_ID',
								align : 'center',
								width : 140
							}, {
								header : '<fmt:message key="aimir.mcucode.fmversion"/> (PARENT)',
								dataIndex : 'PARENT_VER',
								align : 'center',
								width : 140
							}
						],
						defaults : {
							sortable : false,
						//menuDisabled: true
						},
			});			
			
			
			// 아직 안닫힌 경우 기존 창은 닫기
	        if(Ext.getCmp('parentDeviceWindow')){
	            Ext.getCmp('parentDeviceWindow').close();
	        }
			
    		parentDeviceFormPanel = new Ext.FormPanel({
	            id : 'parentDeviceform',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            labelWidth : 80,
	            html:'<div id="parentDeviceDiv"><li id="parentTotal"></li> </div>',
	            items : [],
	            buttons : [{
	                    id: 'excelDownload',
	                    text: 'downlodad',
	                    listeners: {
	                        click: function(btn,e){
	                        	openExcelReport();
	                        }
	                    }
	                }]
	        });
    		
   	        var parentDeviceWin = new Ext.Window({
   	            id     : 'parentDeviceWindow',
   	            title : 'Unable to Clone-On from Parent',
   	            pageX : $('#leftDiv').width()*0.738,
   	            pageY : 175+ $('#invalidDeviceWindow').height(),
   	            height : 460,
   	            width  : 500,
   	            layout : 'fit',
   	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
   	            items  : [parentDeviceFormPanel],
   	        });
   	        
   	     	parentDeviceWin.show();

				parentDeviceGrid = new Ext.grid.GridPanel({
                    store: parentDeviceStore,
                    colModel : parentDeviceColModel,
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
                    autoScroll:true,
                    scroll:true,
                    height: 350,
                    width: 450,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'parentDeviceDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    }
                });
				
				parentDeviceGridOn = true;
			
	}
	/* SP-1004 Clone on탭  parent의 ID/FW_VER 정보 출력 (end) */
	
    // open excel download popup ( sp-1004 )
    var win;
    function openExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();

        obj.supplierId = supplierId;
        obj.deviceId = deviceId;
        obj.modelId	 = modelId;
        obj.fwVersion = fwVersion2;
        obj.targetDeviceType = targetDeviceType;

        if(win)
            win.close();
        win = window.open("${ctx}/gadget/device/firmwareExcelDownloadPopup.do", "FirmwareExcel", opts);
        win.opener.obj = obj;
    }
	
    
	function chkParent(){
		if ($(parentCheck).is(':checked'))
			chkparent = true;
		else 
			chkparent = false;
		
	}
	// Run OTA 버튼
	var otaWin;
	
	function runOta(){        
        
        /* if($('#sLocationId').val() == -1){
            Ext.Msg.alert('Infromation', 'Please select DSO Location.');
            return;
        } */

		var deviceIdArray = new Array(0);
		var deviceCount=0;
		var deviceIdString="";
		//alert(chkCount);
		for(var i = 0 ; i < chkCount ; i++){
			if($("input:checkbox[id='chkDeviceId"+i+"']").is(":checked") == true){
				var checkDeviceID = "#chkDeviceId"+i
				
				// deviceId들을 ','으로연결한 String 
				deviceIdString += $(checkDeviceID).val()+",";
				
				// deviceId들의 배열
				deviceIdArray[i] = $(checkDeviceID).val();
				
				deviceCount++;
				//alert(deviceIdArray[i]);
			}
		}
		
		//마지막 ','제거
		deviceIdString = deviceIdString.slice(0,-1);
		//alert(deviceCount);
		//팝업 호출		
		var left = ($('#leftDiv').width()*0.55);
		var opts = "width=800px, height=550px, left=" + left + "px, top=200px, resizable=no, status=no, location=no";
		var obj = new Object();
		//var condition = $("input[name='chkDeviceId']:checked");
		if(deviceCount < 1){
			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>'
	  				,'<fmt:message key="aimir.select.row.no"/>');
			return;
		}  
		obj.pageWidth = '800';
		obj.pageHeight = '550';
		obj.deviceModel = tempModelId;
		obj.condition = deviceCount;
		obj.equip_kind = targetDeviceType;
		obj.deviceIdArray = deviceIdArray;
		obj.deviceIdString = deviceIdString;
		obj.loginId = loginId;
        obj.locationId = $('#sLocationId').val();

		if (otaWin){
			otaWin.close();
		}
			
		otaWin = window.open("${ctx}/gadget/device/firmware/firmwareAddPopup.do", 
								"firmwareAdd", opts);
		otaWin.opener.obj = obj;			
	}
	
	// 펌웨어 delete 패널 - Delete 버튼 
	function startDelete() {
		var actionUrl = '${ctx}/gadget/device/firmware/deleteFirmwareItem.do';
	  	var action = "Delete";
	  	
	  	$.ajax({
	  		url: actionUrl,
	  		method : 'post',
	  		data:{
	  			'firmwareId'   : temp_firmwareId
	  		},
	  		beforeSend: function(){
	  			Ext.MessageBox.wait('Updating', 'WAIT', {
	  				text: 'Working...' + action,
	  				scope: this,
	  			});
	  		},
	  		success: function(returnData){
	  			//폼 윈도우를 닫고, 결과 처리
	  			Ext.MessageBox.hide();
	  			Ext.getCmp('deleteWindowPanel').close();
	  			
	  			if (returnData.status == null) { // 실패 메시지 출력
	  				Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', "<fmt:message key="aimir.hhu.failure"/>");
	  			} else if (returnData.status == 'FAIL') {
	  				Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', returnData.rtnStr);
	  			} else { // 성공 라인 출력
		  			Ext.MessageBox.alert("Message", returnData.status);		  			
	  			}
	  		},
	  		error: function(){
	  			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', '<fmt:message key="aimir.firmware.msg10"/>');
	  		},
	  		complete: function(){
	  			//성공, 에러 모두 다 끝났으면 자동으로 grid 새로고침
	  			getFwListGrid();	  			
	  		}
	  	}); 
 	}
	
	// 펌웨어 add/update 패널 - OK 버튼 클릭시
	function startUpload(action) {
		// 입력칸 체크
		var svnd = Ext.getCmp('sVendor').getValue();		// 제조사 ID
	  	var smod = Ext.getCmp('sModel').getValue();			// 모델명 ID
	  	var upfw = Ext.getCmp('fwversion').getValue();		// F/W 버전
	  	var upkey = Ext.getCmp('parameter').getValue();		// 이미지키
		var upfile = Ext.getCmp('filename').getValue();		// 파일명
		var overwrite = Ext.getCmp('overwrite').getValue();	// 오버라이트
		var upfile_url = temp_finalFilePath; 			// File이 저장되는 서버 디렉토리 경로
		var actionUrl = "";
		
		// 요청 주소, 'add'와 'update' 구분
		if (action=='add') {
			if("".search(svnd)==0 || "".search(smod)==0 || "".search(upfw)==0 || "".search(upkey)==0 ){
		  		Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>','<fmt:message key="aimir.msg.allfieldrequired"/>');
		  		return;
		  	} else if ("".search(upfile)==0){
		  		Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>','<fmt:message key="aimir.alert.selectFile"/>');
		  		return;
		  	}
			actionUrl = '${ctx}/gadget/device/firmware/addNewFirmwareItem.do';
		} else if (action=='update') {
			if("".search(svnd)==0 || "".search(smod)==0 || "".search(upfw)==0){
		  		Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>','<fmt:message key="aimir.msg.allfieldrequired"/>');
		  		return;
		  	}
			
			actionUrl = '${ctx}/gadget/device/firmware/updateFirmwareItem.do';
		}
	  	 	
	  	$.ajax({
	  		url: actionUrl,
	  		method : 'post',
	  		data:{
	  			'vendorid'   : svnd,	// 제조사 ID
	  			'modelid'	 : smod,	// 모델 ID
	  			'fwversion'  : upfw,	// F/W 버전
	  			'parameter'  : upkey,	// Image Key
	  			'overwrite'  : overwrite,	// Overwrite
	  			'devicetype' : targetDeviceType,
	  			'loginId'    : loginId,
	  			'firmwareId' : temp_firmwareId
	  		},
	  		beforeSend: function(){
	  			Ext.MessageBox.wait('Updating', 'WAIT', {
	  				text: 'Working...' + action,
	  				scope: this,
	  			});
	  		},
	  		success: function(returnData){
	  			if (returnData.status == 'FAIL') {
	  				Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', returnData.rtnStr);
	  			} else {
	  				//폼 윈도우를 닫고, 결과 처리
		  			Ext.MessageBox.hide();
		  			Ext.getCmp('uploadWindowPanel').close();
		  			Ext.MessageBox.alert("Message", returnData.status);		  			
	  			}
	  		},
	  		error: function(){
	  			Ext.MessageBox.alert('<fmt:message key="aimir.failure"/>', '<fmt:message key="aimir.firmware.msg10"/>');
	  		},
	  		complete: function(){
	  			//성공, 에러 모두 다 끝났으면 자동으로 grid 새로고침
	  			getFwListGrid();	  			
	  		}
	  	}); 
 	}
	
 	// 'File upload' 버튼 클릭시 실행되는 영역
 	var fileStr="";
 	var url ='';
 	var getFileUploadModule = function(){
 		if(targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel'){
 			 url ='${ctx}/gadget/device/firmware/getTempFileName_dcu.do'
 		}else
 			url ='${ctx}/gadget/device/firmware/getTempFileName.do'
		// 파일 업로드 관리, SELECT 버튼 
		new AjaxUpload('fileform', {
			name : 'fileform',
            action: url,
            data : {
            },
            responseType : false,
            onSubmit : function(file , ext){
            	var formatType = "";
            	var upFile = $('#filename').val();            	
				
                var fwVersionIndexStart = file.lastIndexOf('_');                
                /* if(fwVersionIndexStart == -1){
                    $('#infolabel').text('');
                    $('#infolabel').text(' * Unknown F/W Version.');
                    return false;
                } */
                var realExt="";
                var fwVersion="";
                fileStr = file.substring(0, file.lastIndexOf('_'));
                var realExt = file.substring( fileStr.lastIndexOf('.')+1, fwVersionIndexStart);              
                var fwVersion = file.substring(fwVersionIndexStart+1, file.length); 
                
                if(targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel'){
                	realExt = file.substring( fileStr.lastIndexOf('.')+1, fwVersionIndexStart);
                	fwVersion = file.substring(fwVersionIndexStart+1, file.lastIndexOf('.'));
                }
                //alert(realExt);
                //alert(fwVersion);
               //  alert('index=' + fwVersionIndexStart + ', realExt=' + realExt + ', fwVersion=' + fwVersion);

				// 값이 유효한지 확인하는 부분
				$('#upload .text').text('Uploading ' + file);
				
				// mcu/mcu-kernel/modem/meter별 확장자 Check
				if((targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel')){
					formatType = "gz,tar,tar.gz";
					
					if (realExt && /^(gz|tar|tar.gz)$/.test(realExt)){
	                    this.setData({
	                        // request.getParameter를 통해 컨트롤러에서 확인
	                        'upfile': upFile
	                    });
	                } else {
	                	$('#infolabel').text('');
	                	$('#infolabel').text(' * <fmt:message key="aimir.wrong.fileType.excel"/> [' + formatType + ']');
	                    return false;
	                }
					
				} else if(targetDeviceType == 'modem' || targetDeviceType == 'meter' || targetDeviceType == 'dcu-coordinate'){
					formatType = "ebl, dwl, bin, mot";
					
					if (realExt && /^(ebl|EBL|dwl|DWL|bin|BIN|mot|MOT)$/.test(realExt)){
	                    this.setData({
	                        // request.getParameter를 통해 컨트롤러에서 확인
	                        'upfile': upFile
	                    });
	                } else {
	                	$('#infolabel').text(' * <fmt:message key="aimir.wrong.fileType.excel"/> [' + formatType + ']');
	                    return false;
	                }
				} /*else if(targetDeviceType == 'meter'){
					formatType = "dwl, bin, mot, csv";
					if (realExt && /^(dwl|DWL|bin|BIN|mot|MOT|csv|CSV)$/.test(realExt)){
	                    this.setData({
	                        // request.getParameter를 통해 컨트롤러에서 확인
	                        'upfile': upFile
	                    });
	                } else {
	                	$('#infolabel').text(' * <fmt:message key="aimir.wrong.fileType.excel"/> [' + formatType + ']');
	                    return false;
	                }
				} */
            },
            onComplete : function(file, response){
				var datas = response.split('|');
				$('#filename').val(fileStr.substring(0,fileStr.lastIndexOf('.')));
                // 'OK' 버튼 클릭 전 이름,경로 저장
                filePath = datas[0].trim();
            	$.ajax({
                    type:"GET",
                    dataType:"json",
                    url:"${ctx}/gadget/device/firmware/getTempFileInfo.do",
                    success:function(result) {
                    	temp_filePath = result.filePath;
                    	temp_fileName = result.fileName;
                    	temp_ext = result.ext;
                    	temp_finalFilePath = result.finalFilePath;
                    	temp_crc = result.crc;
                        temp_fwVersion = result.fwVersion;
                    	// temp_fwDownURL = result.fwDownURL;
                    	
                    	// DCU          : 파일명에 '.' 포함 허용 O
                    	// DCU Kernel   : 파일명에 '.' 포함 허용 O
                    	// Modem        : 파일명에 '.' 포함 허용 X
                    	// Meter        : 파일명에 '.' 포함 허용 X
                    	// DCU Coordinate     : 파일명에 '.' 포함 허용 X
                    	/* if(!(targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel')){
                    		var splitedFileName = temp_fileName.split('.');
                           	
    	                    if(2 <= splitedFileName.length || splitedFileName.length < 1) {
    	    					Ext.Msg.alert('Error', " * Plese don't use '.' for a file name.");
    	    					Ext.getCmp('uploadWindowPanel').close();
    	    					return;
    	                    }
                    	} */ 
                    	
	                   	// Target Type이 'meter'일 경우에만 Image Key를 출력 및 저장한다. 
	                   	if(targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel' || targetDeviceType == 'modem' || targetDeviceType == 'dcu-coordinate'){
	                   		$('#parameter').val("-");
	                   	} else if(targetDeviceType == 'meter'){
	                   		$('#parameter').val(temp_fileName);
	                   	}
	                   	
	                   	$('#crc').val(temp_crc);
                        $('#fwversion').val(temp_fwVersion);
	                   	// $('#filePath').val(temp_fwDownURL);
	   					$('#infolabel').text(" * Click 'OK' button");
                    }
                });
            }
        });
	}
 	
 	function getFirmwareSearchLocationName() {
		 if(isAdmin == true && locationId == "") {
			 $.ajax({
		 			type:"GET",
		 			async: true,
		 			dataType:"json",
		 			url:"${ctx}/gadget/device/firmware/getFirmwareSearchLocationName.do",
		 			success:function(result) {
		 				var location_Name = result.locationName;
		 				var location_Id = result.locationId;
		 				$('#searchWord').val(location_Name);
		 				$('#sLocationId').val(location_Id);
		 			}
		 	});
		 }
	}
 	
 	
 	// SP-831 
 	
	/* OTA Target Grid에서 보여줄 FW 버전들을 가지고 온다. */
 	var versionList = [];
    function getTotalVersion() {
            var params = {
            		modelId : modelId,
            		fwVersion : fwVersion,
            		sType     : searchDeviceType,
                   	deviceId             : deviceId,
            		locationId 			 : locationId,
            		dcuName              : sDcuName,
            		installStartDate  : installStartDate_ad,
    				installEndtDate   : installEndtDate_ad,
    				lastCommStartDate : lastCommStartDate_ad,
    				lastCommEndDate   : lastCommEndDate_ad,
    				hwVer   : hwVer_ad
            };
            var type ="";
            if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel')
            	type = 'dcu';
            else
            	type = targetDeviceType;
            $.post("${ctx}/gadget/device/getFirmwareVersionList_" + type + ".do",
                    params,
                    function(json) {
                        versionList = json.result;
                        if (versionList == null) {
                        	versionList = [];
                        }
                    }
            );
        }
    
 	
 	/* OTA Target Grid */
    var deviceGridOn = false;
    var deviceGrid;
    var deviceStore;
    var deviceColModel;
    
    var getDeviceList = function() {
    	
    	if(excelOn == "OFF"){
	        getTotalVersion();
    	}else{ 
    		excelOn = "OFF"
    	}
    	
        var fields = ['DSO','DCU'];
        var colWidth
        if(versionList.length <= 6){
        	if(targetDeviceType == 'modem' || targetDeviceType == 'meter')
        		colWidth =  ($('#leftDiv').width()-21) / (versionList.length + 2);
        	else
        		colWidth =  ($('#leftDiv').width()-21) / (versionList.length + 1);
        }
        else
        	colWidth = 145;
        var version = "";
        var versions = "";
        var columns = [];
        var type ="";
        var height=648;
        
        if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel')
        	type = 'dcu';
        else
        	type=targetDeviceType;
        
        if(targetDeviceType=='meter')
        	height=634;
        
        columns.push({header: 'DSO', dataIndex: 'DSO', align: 'left', menuDisabled: true, locked : true, width: colWidth});
        if(targetDeviceType == 'modem' || targetDeviceType == 'meter')
        	columns.push({header: 'DCU', dataIndex: 'DCU', align: 'left', menuDisabled: true, locked : true, width: colWidth});

        for(var i = 0 ; i < (versionList.length) ; i++){
        	columns.push({header: (versionList[i] ? versionList[i] : 'LOW VERSION') +"<input type='checkbox' id='groupByVersion"+i+"'>", dataIndex: "VERSION"+i, align: 'left', menuDisabled: true, width: colWidth, 
            	  renderer : function(value, me, record, rowNumber, columnIndex, store){
            		  return value.substring(0, value.indexOf("/"));
            		  //return value;
            	  }
        	});
        	version = "VERSION"+i;
        	versions += versionList[i]+","
        	fields.push(version);
        } 
        versions = versions.slice(0,-1);
        deviceStore = new Ext.data.JsonStore({
            autoLoad: true,
            url: "${ctx}/gadget/device/getDeviceList_" + type + ".do",
            baseParams: {
            	modelId    : modelId,
            	versions   : versions,
            	deviceId   : deviceId,
            	fwVersion  : fwVersion,
            	locationId : locationId,
            	dcuName    : sDcuName,
            	sType      : searchDeviceType,
            	installStartDate  : installStartDate_ad,
				installEndtDate   : installEndtDate_ad,
				lastCommStartDate : lastCommStartDate_ad,
				lastCommEndDate   : lastCommEndDate_ad,
				hwVer   : hwVer_ad
            },
            root:'result',
            fields: fields
        });

        if (deviceGridOn == false) {
            deviceGrid = new Ext.grid.GridPanel({
                store : deviceStore,
                columns : columns,
                sm: new Ext.grid.CellSelectionModel({ 
                    listeners: {
                        cellselect: function(sm, rowIndex, colIndex){
                            var record = sm.grid.getStore().getAt(rowIndex);  
                            var fieldName = sm.grid.getColumnModel().getDataIndex(colIndex); 
                            var data = record.get(fieldName);
                            
                            if(colIndex != 0){
                            	if(targetDeviceType == 'modem' || targetDeviceType == 'meter'){
                            		if(colIndex != 1){
                            			var strTemp = data.split('/');
                            			if(strTemp[0] != 0)
                                    		deviceListPopup(strTemp[1], strTemp[2], strTemp[3], targetDeviceType);
                            		}
                            	}else{
                            		var strTemp = data.split('/');
                            		if(strTemp[0] != 0)
                            			deviceListPopup(strTemp[1], strTemp[2], "",  targetDeviceType);
                            	}
                            	
                            }
                        }
                    }
                }), 
                autoScroll : true,
                scroll : true,
                width : $('#leftDiv').width()-21,
                height : height,
                stripeRows : true,
                columnLines : true,
                enableLocking: true,
                loadMask : {
                    msg : 'loading...'
                },
                viewConfig : {
                    forceFit : false,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
            });
			$('#deviceListGridDiv2').html(' ');
			deviceGrid.render('deviceListGridDiv2'); 
        }
    };
    
    /* Clone On,Off Target Grid - Modem */
    var getDeviceListModem = function() {
   	
       //getTotalVersion();
      
       var fields = ['DSO','DSO_ID','DCU','MODEMCOUNT'];
       var colWidth;
      	colWidth = 110;
       var version = "";
       var versions = "";
       var columns = [];
       var type ="";
       var height=600;
       
      	type=targetDeviceType;
      	fwVersion2 = $('#firmwareVersion2').val();
     
       deviceStore = new Ext.data.JsonStore({
           autoLoad: true,
           url: "${ctx}/gadget/device/getDeviceList_" + type + "_cloneonoff.do",
           baseParams: {
           	modelId    : modelId,
           	versions   : null,
           	deviceId   : deviceId,
           	fwVersion  : fwVersion2,
           	locationId : locationId,
           	dcuName    : sDcuName,
           	sType      : searchDeviceType,
           	installStartDate  : installStartDate_ad,
			installEndtDate   : installEndtDate_ad,
			lastCommStartDate : lastCommStartDate_ad,
			lastCommEndDate   : lastCommEndDate_ad,
			hwVer   : hwVer_ad,
            chkParent: chkparent
           },
           root:'result',
           fields: fields
       });
	   
       deviceColModel = new Ext.grid.ColumnModel({
           columns: [
		        	   {
			       		    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
			       	       	width: 30,
			       	       	align:'center',
			       	       	renderer: dataChk,
		       			},
		       			{header: 'DSO_ID', dataIndex: 'DSO_ID', align: 'left',  hidden : true,  width: colWidth},
		       			{header: 'DSO', dataIndex: 'DSO', align: 'center',  width: 70},
		       			{header: 'DCU', dataIndex: 'DCU', align: 'center', width: 60,
		       				renderer : function(val){
							 chkCount++;
							 return val;	 
						 }},
		       			{header: 'MODEM COUNT', dataIndex: 'MODEMCOUNT', align: 'center', width: colWidth}
        	   ],
        	   defaults: {
                   sortable: false,
                   //menuDisabled: true
               },
       });
       
       function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {   
           return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.DCU + "\,"+record.data.DSO_ID+ "\" /></div>";
       }
       
       if (deviceGridOn == false) {
           deviceGrid = new Ext.grid.GridPanel({
               store : deviceStore,
               colModel : deviceColModel,
               sm: new Ext.grid.CellSelectionModel({ 
                   listeners: {
                       cellselect: function(sm, rowIndex, colIndex){
                           var record = sm.grid.getStore().getAt(rowIndex);  
                           var fieldName = sm.grid.getColumnModel().getDataIndex(colIndex); 
                           var data = record.get(fieldName);
                           
                           //var cnt = record.get('MODEMCOUNT');
                           var locationId = record.get('DSO_ID').toString();
                           var fwver = $('#firmwareVersion2').val();
                           var dcuId = record.get('DCU');
                           
                           if(colIndex != 0){
                           	if(targetDeviceType == 'modem' || targetDeviceType == 'meter'){
                           		if(colIndex != 1){
                           			//var strTemp = data.split('/');
                           			//if(strTemp[0] != 0)
                                   		deviceListPopup2(locationId, fwver, dcuId, targetDeviceType, chkparent);
                           			
                           		}
                           	}else{
                           		//var strTemp = data.split('/');
                           		//if(strTemp[0] != 0)
                           			deviceListPopup2(locationId, fwver, "",  targetDeviceType, chkparent);
                           	}
                           	
                           }
                       }
                   }
               }), 
               autoScroll : true,
               scroll : true,
               width : 275,
               height : height,
               //renderTo : 'deviceListGridDiv3',
               stripeRows : true,
               columnLines : true,
               layout: 'fit',
               loadMask : {
                   msg : 'loading...'
               },
               viewConfig : {
                   //forceFit : false,
                   //enableRowBody : true,
                   showPreview : true,
                   emptyText : 'No data to display'
               },
           });
           //deviceGrid.reconfigure(deviceStore, columns);
           
			$('#deviceListGridDiv3').html(' ');
			deviceGrid.render('deviceListGridDiv3'); 
       }
   };
   
   /* Clone On,Off Target Grid - Meter */
    var getDeviceListMeter = function() {
   	
       //getTotalVersion();
      
       var fields = ['DSO','DSO_ID','DCU','METERCOUNT'];
       var colWidth;
      	colWidth = 110;
       var version = "";
       var versions = "";
       var columns = [];
       var type ="";
       var height=600;
       
      	type=targetDeviceType;
      	fwVersion2 = $('#firmwareVersion2').val();
     
       deviceStore = new Ext.data.JsonStore({
           autoLoad: true,
           url: "${ctx}/gadget/device/getDeviceList_" + type + "_cloneonoff.do",
           baseParams: {
           	modelId    : modelId,
           	versions   : null,
           	deviceId   : deviceId,
           	fwVersion  : fwVersion2,
           	locationId : locationId,
           	dcuName    : sDcuName,
           	sType      : searchDeviceType,
           	installStartDate  : installStartDate_ad,
			installEndtDate   : installEndtDate_ad,
			lastCommStartDate : lastCommStartDate_ad,
			lastCommEndDate   : lastCommEndDate_ad,
			hwVer   : hwVer_ad,
            chkParent: chkparent
           },
           root:'result',
           fields: fields
       });
	   
       deviceColModel = new Ext.grid.ColumnModel({
           columns: [
		        	   {
			       		    header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
			       	       	width: 30,
			       	       	align:'center',
			       	       	renderer: dataChk,
		       			},
		       			{header: 'DSO_ID', dataIndex: 'DSO_ID', align: 'left',  hidden : true,  width: colWidth},
		       			{header: 'DSO', dataIndex: 'DSO', align: 'center',  width: 70},
		       			{header: 'DCU', dataIndex: 'DCU', align: 'center', width: 60,
		       				renderer : function(val){
							 chkCount++;
							 return val;	 
						 }},
		       			{header: 'METER COUNT', dataIndex: 'METERCOUNT', align: 'center', width: colWidth}
        	   ],
        	   defaults: {
                   sortable: false,
                   //menuDisabled: true
               },
       });
       
       function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {   
           return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"chkDeviceId"+rowIndex+"\" name=\"chkDeviceId\" value=\"" + record.data.DCU + "\,"+record.data.DSO_ID+ "\" /></div>";
       }
       
       if (deviceGridOn == false) {
           deviceGrid = new Ext.grid.GridPanel({
               store : deviceStore,
               colModel : deviceColModel,
               sm: new Ext.grid.CellSelectionModel({ 
                   listeners: {
                       cellselect: function(sm, rowIndex, colIndex){
                           var record = sm.grid.getStore().getAt(rowIndex);  
                           var fieldName = sm.grid.getColumnModel().getDataIndex(colIndex); 
                           var data = record.get(fieldName);
                           
                           //var cnt = record.get('MODEMCOUNT');
                           var locationId = record.get('DSO_ID').toString();
                           var fwver = $('#firmwareVersion2').val();
                           var dcuId = record.get('DCU');
                           
                           if(colIndex != 0){
                           	if(targetDeviceType == 'modem' || targetDeviceType == 'meter'){
                           		if(colIndex != 1){
                           			//var strTemp = data.split('/');
                           			//if(strTemp[0] != 0)
                                   		deviceListPopup2(locationId, fwver, dcuId, targetDeviceType, chkparent);
                           			
                           		}
                           	}else{
                           		//var strTemp = data.split('/');
                           		//if(strTemp[0] != 0)
                           			deviceListPopup2(locationId, fwver, "",  targetDeviceType, chkparent);
                           	}
                           	
                           }
                       }
                   }
               }), 
               autoScroll : true,
               scroll : true,
               width : 275,
               height : height,
               //renderTo : 'deviceListGridDiv3',
               stripeRows : true,
               columnLines : true,
               layout: 'fit',
               loadMask : {
                   msg : 'loading...'
               },
               viewConfig : {
                   //forceFit : false,
                   //enableRowBody : true,
                   showPreview : true,
                   emptyText : 'No data to display'
               },
           });
           //deviceGrid.reconfigure(deviceStore, columns);
           
			$('#deviceListGridDiv3').html(' ');
			deviceGrid.render('deviceListGridDiv3'); 
       }
   };
   
    var locationList = [];
    function getLocations() {
	    $.post("${ctx}/gadget/system/location/getLocationsName.do",
	            function(json) {
	                locationList = json.locations;
	                if (locationList == null) {
	                	locationList = [];
	                }
	            }
	    );
    }
    
	/* 검색조건에 사용되는 Location Check Box */
    var checkFormPanel;
    var checkWin;
    function setDSONames(){
    	getLocations();
    	var LocationIds = ""; 
    	$("#sLocationId").val('');
        if(Ext.getCmp('checkWindow')){
            Ext.getCmp('checkWindow').close();
        }

		checkFormPanel = new Ext.FormPanel({
            id : 'checkform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            buttons : [
                {
                    id: 'checkSendBtn',
                    text: 'Select',
                    listeners: {
                        click: function(btn,e){
                        	for(var i=0; i < locationList.length; i++){
                        		if($(("#"+locationList[i])).is(':checked')){
                        			LocationIds += (locationList[i] +",")
                        		}
                        	}
                        	LocationIds = LocationIds.slice(0,-1);
                        	$("#sLocationId").val(LocationIds);
                        	Ext.getCmp('checkWindow').close();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('checkWindow').close();
                        }
                    }
                }
            ] 
        });
		
    	for(var i=0; i < locationList.length; i++){
    		checkFormPanel.add({
    			xtype : 'checkbox',
                id : locationList[i],
                fieldLabel : locationList[i],
        	})
    	}
    	
        var checkWin = new Ext.Window({
        		id     : 'checkWindow',
	            title : 'DSO',
	            autoScroll: true,
	            pageX : 712,
	            pageY : 70,
	            height:  400,
	            width  : 230,
	            //layout : 'fit',
	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
	            items  : [checkFormPanel],
        });

        checkWin.show();
    }
    
    function setDSONames2(){
    	getLocations();
    	var LocationIds = ""; 
    	$("#sLocationId2").val('');
        if(Ext.getCmp('checkWindow')){
            Ext.getCmp('checkWindow').close();
        }

		checkFormPanel = new Ext.FormPanel({
            id : 'checkform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            buttons : [
                {
                    id: 'checkSendBtn',
                    text: 'Select',
                    listeners: {
                        click: function(btn,e){
                        	for(var i=0; i < locationList.length; i++){
                        		if($(("#"+locationList[i])).is(':checked')){
                        			LocationIds += (locationList[i] +",")
                        		}
                        	}
                        	LocationIds = LocationIds.slice(0,-1);
                        	$("#sLocationId2").val(LocationIds);
                        	Ext.getCmp('checkWindow').close();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('checkWindow').close();
                        }
                    }
                }
            ] 
        });
		
    	for(var i=0; i < locationList.length; i++){
    		checkFormPanel.add({
    			xtype : 'checkbox',
                id : locationList[i],
                fieldLabel : locationList[i],
        	})
    	}
    	
        var checkWin = new Ext.Window({
        		id     : 'checkWindow',
	            title : 'DSO',
	            autoScroll: true,
	            pageX : 712,
	            pageY : 70,
	            height:  400,
	            width  : 230,
	            //layout : 'fit',
	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
	            items  : [checkFormPanel],
        });

        checkWin.show();
    }
    
	var deviceWin;
	
	function deviceListPopup(locationId, fwVersion, dcuName, targetDeviceType){        
		//팝업 호출		
		var left = ($('#leftDiv').width()*0.85);
		var opts = "width=620px, height=590px, left=" + left + "px, top=200px, resizable=no, status=no, location=no";
		var obj = new Object();
		
        obj.locationId        = locationId;
        obj.fwVersion         = fwVersion;
        obj.modelId           = modelId;
		obj.targetDeviceType  = targetDeviceType;
		obj.fileInfo          = fileInfo;
		obj.deviceId          = deviceId;
		obj.dcuName           = dcuName;
		obj.searchDeviceType  = searchDeviceType;
		obj.installStartDate  = installStartDate_ad;
		obj.installEndtDate   = installEndtDate_ad;
		obj.lastCommStartDate = lastCommStartDate_ad;
		obj.lastCommEndDate   = lastCommEndDate_ad;
		obj.hwVer   		  = hwVer_ad;
		
		if (deviceWin){
			deviceWin.close();
		}
			
		deviceWin = window.open("${ctx}/gadget/device/firmware/deviceListPopup.do", 
								"firmwareList", opts);
		deviceWin.opener.obj = obj;			
	}
	
	function deviceListPopup2(locationId, fwVersion, dcuName, targetDeviceType, chkparent){        
		//팝업 호출		
		var left = ($('#leftDiv').width()*0.85);
		var opts = "width=620px, height=590px, left=" + left + "px, top=200px, resizable=no, status=no, location=no";
		var obj = new Object();
		
        obj.locationId        = locationId;
        obj.fwVersion         = fwVersion;
        obj.modelId           = modelId;
		obj.targetDeviceType  = targetDeviceType;
		obj.fileInfo          = fileInfo;
		obj.deviceId          = deviceId;
		obj.dcuName           = dcuName;
		obj.searchDeviceType  = searchDeviceType;
		obj.installStartDate  = installStartDate_ad;
		obj.installEndtDate   = installEndtDate_ad;
		obj.lastCommStartDate = lastCommStartDate_ad;
		obj.lastCommEndDate   = lastCommEndDate_ad;
		obj.hwVer   		  = hwVer_ad;
		obj.chkparent   		  = chkparent;
		
		if (deviceWin){
			deviceWin.close();
		}
			 
		deviceWin = window.open("${ctx}/gadget/device/firmware/deviceListPopup2.do", 
								"firmwareList", opts);
		deviceWin.opener.obj = obj;			
	}
	
	var target;
	var sDeviceList = new Array();
	function groupByVersionOTA(){
		var deviceList;
		var verCnt =0 ;
		var TargetList = new Array();
		var deviceList = new Array();
		sDeviceList = new Array();
		var versionStr = "";
		
		//체크한 버전확인
		for(var i = 0 ; i < versionList.length ; i++){
			if($("input:checkbox[id='groupByVersion"+i+"']").is(":checked") == true){
				versionStr += versionList[i]+","
				verCnt++;
			}
		}
		
		if(verCnt ==0){
			Ext.Msg.alert('Message', "There is nothing selected target.");
			return;
		}
		
		versionStr = versionStr.slice(0,-1);
		
		//alert(versionStr);
		var params = {
            modelId              : modelId,
            fwVersion            : versionStr,
           	deviceId             : deviceId,
    		locationId 			 : locationId,
    		dcuName              : sDcuName,
    		sType                : searchDeviceType,
    		installStartDate  : installStartDate_ad,
			installEndtDate   : installEndtDate_ad,
			lastCommStartDate : lastCommStartDate_ad,
			lastCommEndDate   : lastCommEndDate_ad,
			hwVer   : hwVer_ad
	     };
				
	   	var type ="";
	   	if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel')
	            type = 'dcu';
	   	else
	            type = targetDeviceType;
	   	$.post("${ctx}/gadget/device/getTargetList_" + type + ".do",
	                    params,
	                    function(json) {
	                        sDeviceList = json.result;
	                        if (sDeviceList == null) {
	                        	sDeviceList = [];
	                        }
	                    }
	            );
	            
	   	
	   	var cmpTemp1="";
	   	var cmpTemp2="";
	   	var locId ="";
	   	
	   	// bubble sort처럼 비교해서 Target 만들기.
	   	if(sDeviceList.length == 1){
	   		cmpTemp1 = sDeviceList[0].split('/');
	   		deviceList.push(cmpTemp1[0])
	   		var Target ={
   	   				locationId : cmpTemp1[1],
   	   				deviceIdList : deviceList
   	   			};
	   		TargetList.push(Target);
	   	}else{
	   		for(var i=0; i< sDeviceList.length-1; i++){
		   		cmpTemp1 = sDeviceList[i].split('/');
		   		cmpTemp2 = sDeviceList[i+1].split('/');
		   		deviceList.push(cmpTemp1[0])
		   		locId = cmpTemp1[1];
		   		if(cmpTemp1[1] != cmpTemp2[1]){
		   			var Target ={
		   	   				locationId : locId,
		   	   				deviceIdList : deviceList
		   	   			};
		   			TargetList.push(Target);
		   			deviceList= [];
		   			if(i == sDeviceList.length-2){
		   				deviceList.push(cmpTemp2[0])
		   				var Target ={
			   	   				locationId : cmpTemp2[1],
			   	   				deviceIdList : deviceList
			   	   			};
		   				TargetList.push(Target);
		   				deviceList= [];
			   			break;
		   			}
		   		} 
		   		if(i == (sDeviceList.length-2)){
		   			deviceList.push(cmpTemp2[0])
		   			var Target ={
	   	   				locationId : locId,
	   	   				deviceIdList : deviceList
	   	   			};
		   			TargetList.push(Target);
		   		}
		   	}
	   	}
	   	
	   	// json String 형식으로
	   	target = JSON.stringify(TargetList);
        //alert(target);
	   
/*    		var Target ={
   				locationId : $(locationId).val(),
   				deviceList : deviceList
   			};
    			
    	TargetList.push(Target);
		var target = JSON.stringify(TargetList);
        alert(target); */
        otaExecute();
	}
	
	function groupByClone(){
		var deviceList;
		var verCnt =0 ;
		var TargetList = new Array();
		var deviceList = new Array();
		sDeviceList = new Array();
		
		var deviceIdArray = new Array(0);
		var dsoIdArray = new Array(0);
		var deviceCount=0;
		var deviceIdString="";
		var dsoIdString="";
		// 체크한 DCU 확인
		for(var i = 0 ; i < chkCount ; i++){
			if($("input:checkbox[id='chkDeviceId"+i+"']").is(":checked") == true){
				var checkDeviceID = "#chkDeviceId"+i
				var checkDeviceValue = $(checkDeviceID).val();
				var checkDeviceValueSplit = checkDeviceValue.split(',');
				
				// deviceId들을 ','으로연결한 String 
				deviceIdString +=  checkDeviceValueSplit[0]+",";
				dsoIdString +=  checkDeviceValueSplit[1]+",";
				
				// deviceId들의 배열
				deviceIdArray[i] = checkDeviceValue[0];
				dsoIdArray[i] = checkDeviceValue[1];
			
				deviceCount++;
			}
		}
		
		if(deviceCount ==0){
			Ext.Msg.alert('Message', "There is nothing selected target.");
			return;
		}
		
		//마지막 ','제거
		deviceIdString = deviceIdString.slice(0,-1);
		dsoIdString = dsoIdString.slice(0,-1);
		
		var dcuall = false;
		if($("input:checkbox[id='allCheck2']").is(":checked") == true) {
    		dcuall = true;
    	}else{
    		dcuall = false;
    	}
		
		var params = {
            modelId              : modelId,
            fwVersion            : $('#firmwareVersion2').val(),
           	deviceId             : $('#deviceId').val(),
    		locationId 			 : $('#sLocationId2').val(),
    		locationName 			 : dsoIdString,
    		dcuName              : deviceIdString,
    		sType                : searchDeviceType,
    		installStartDate  : installStartDate_ad,
			installEndtDate   : installEndtDate_ad,
			lastCommStartDate : lastCommStartDate_ad,
			lastCommEndDate   : lastCommEndDate_ad,
			hwVer   : hwVer_ad,
			chkParent : chkparent,
			dcuall : dcuall
	     };
		
	   	var type ="";
	   	if(targetDeviceType=='dcu' || targetDeviceType=='dcu-kernel')
	            type = 'dcu';
	   	else
	            type = targetDeviceType;
	   	$.post("${ctx}/gadget/device/getTargetList_" + type + "_cloneonoff.do",
	                    params,
	                    function(json) {
	                        sDeviceList = json.result;
	                        if (sDeviceList == null) {
	                        	sDeviceList = [];
	                        }
	                    }
	            );
	            
	   	
	   	var cmpTemp1="";
	   	var cmpTemp2="";
	   	var locId ="";
	   	
	   	// bubble sort처럼 비교해서 Target 만들기.
	   	if(sDeviceList.length == 1){
	   		cmpTemp1 = sDeviceList[0].split('/');
	   		deviceList.push(cmpTemp1[0])
	   		var Target ={
   	   				locationId : cmpTemp1[1],
   	   				deviceIdList : deviceList
   	   			};
	   		TargetList.push(Target);
	   	}else{
	   		for(var i=0; i< sDeviceList.length-1; i++){
		   		cmpTemp1 = sDeviceList[i].split('/');
		   		cmpTemp2 = sDeviceList[i+1].split('/');
		   		deviceList.push(cmpTemp1[0])
		   		locId = cmpTemp1[1];
		   		if(cmpTemp1[1] != cmpTemp2[1]){
		   			var Target ={
		   	   				locationId : locId,
		   	   				deviceIdList : deviceList
		   	   			};
		   			TargetList.push(Target);
		   			deviceList= [];
		   			if(i == sDeviceList.length-2){
		   				deviceList.push(cmpTemp2[0])
		   				var Target ={
			   	   				locationId : cmpTemp2[1],
			   	   				deviceIdList : deviceList
			   	   			};
		   				TargetList.push(Target);
		   				deviceList= [];
			   			break;
		   			}
		   		} 
		   		if(i == (sDeviceList.length-2)){
		   			deviceList.push(cmpTemp2[0])
		   			var Target ={
	   	   				locationId : locId,
	   	   				deviceIdList : deviceList
	   	   			};
		   			TargetList.push(Target);
		   		}
		   	}
	   	}
	   	
	   	// json String 형식으로
	   	target = JSON.stringify(TargetList);
        cloneExecute();
	}
	
	var i =0;
	var modemType = "";
	function checkDeviceType(deviceId){
		var params = {
				deviceId : (deviceId.split('/'))[0]
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
                    	checkDeviceType(sDeviceList[i])
                    }
                }
        );
	}
	
	function checkDeviceType2(deviceId){
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
                    	checkDeviceType2(targetId[i])
                    }
                }
        );
	}
	
	function checkDeviceType3(deviceId){
		var params = {
				deviceId : deviceId
        };
		
		$.post("${ctx}/gadget/device/checkDeviceType_meter.do",
                params,
                function(json) {
                    var type = json.result;
                    if (type != null && type != "") {
                    	modemType = type;
                    	i = 0;
                    }else{
                    	i++;
                    	checkDeviceType2(targetId[i])
                    }
                }
        );
	}
	
    // 패널 그리기
    var modemType;
    var otaExecuteFormPanel;
    var otaExecuteWin;
    function otaExecute(isRetry) {
        // 아직 안닫힌 경우 기존 창은 닫기
        try{ // SP-1067
        	Ext.getCmp('otaExecuteWindow').close();
        } catch(e){
        	console.log(e);
        }
//        if(Ext.getCmp('otaExecuteWindow')){
//        	Ext.getCmp('otaExecuteWindow').close();
//        }
        
        if(targetDeviceType == "modem"){
        	if(isRetry =="true")
        		checkDeviceType2(targetId[0]);
        	else
        		checkDeviceType(sDeviceList[0]);
        }else if(targetDeviceType == "meter"){
        	if(isRetry =="true"){
        		checkDeviceType3(targetId[0]);
        	}
        	else
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
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true  }
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
	                        {boxLabel: 'By HES', name: 'radio-action2', inputValue:'2', checked: true, disabled: true  }
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
        var pageX  = ($('#leftDiv').width())*0.9;
        
        
        if(isRetry == "true")
        	pageX  = (($('#firmwareHistoryTabDiv').width())/2)*0.8;
        if(modemType == "RF"){
        	//height = 310;
        	height = 330;
        	width  = 570;
        	pageX  = ($('#leftDiv').width())*0.8;
        	if(isRetry == "true")
            	pageX  = (($('#firmwareHistoryTabDiv').width())/2)*0.8;
        }else if(modemType == "MBB"){
        	height = 305;
            width  = 360;
            pageX  = ($('#leftDiv').width())*0.9;
            if(isRetry == "true")
            	pageX  = (($('#firmwareHistoryTabDiv').width())/2)*0.8;
        }
        
        if(targetDeviceType =="dcu"){
        	height = 260;
        	width  = 360;
        }
        	
        
        var otaExecuteWin = new Ext.Window({
            id     : 'otaExecuteWindow',
            title : 'Execute OTA',
            pageX : pageX,
            pageY : 300,
            height : height,
            width  : width,
            layout : 'fit',
            modal  : true,
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [otaExecuteFormPanel],
        });
        otaExecuteWin.show();
    }
    
	 /* SP-957 Clone on,off */
    function cloneExecute(isRetry){
			
    	targetDeviceType = $('#deviceType option:selected').val();
    	
    	var executeTimeOpt = $("input:radio[name='executetime']:checked").val();
    	var isImmediately ="true"; 
    	if(executeTimeOpt == 'RunNow')
      		 isImmediately = "true";
      	 else
      		 isImmediately = "false";
    	
    	var otaExecuteTime=""
    	var otaExecuteTimeVal = 0;
    	if(isImmediately == "false"){
    		var executeDateHidden = $("#executeDateHidden").val();
    		var sHour = $("#sHour").val();
    		var sMin = $("#sMin").val();
    		if(executeDateHidden=="" || executeDateHidden == null)
    			Ext.Msg.alert('Message', "Please select Execute Date.");
    		else
    			otaExecuteTimeVal++;
    		
    		if(sHour == "" || sHour == null)
    			Ext.Msg.alert('Message', "Please insert Execute Hour.");
    		else
    			otaExecuteTimeVal++;

    		if(sMin == "" || sMin == null)
    			Ext.Msg.alert('Message', "Please insert Execute Min.");
    		else
    			otaExecuteTimeVal++;
    		
    		if(sHour.length<=1){
    			sHour = 0 + sHour; 
    		} else if(sHour.length>=3){
    			Ext.Msg.alert('Message', "Hour format is 00 to 59.");
    			otaExecuteTimeVal--;
    		}
    		if(sMin.length<=1){
    			sMin = 0 + sMin;
    		} else if(sMin.length>=3){
    			Ext.Msg.alert('Message', "Miniute format is 00 to 59.");
    			otaExecuteTimeVal--;
    		}
    		
    		if(otaExecuteTimeVal == 3)
    			otaExecuteTime = $("#executeDateHidden").val() + sHour + sMin;
    		else
    			return;
    		
    	}
    	
    	var otaRetryCount = $("#sRetryCnt").val();
    	var otaRetryCycle = $("#sRetryCycle").val();
    	
        var executeType ="";
        var otaExecuteType ="";
		
		 if(targetDeviceType == "modem"){
	        	if(isRetry =="true")
	        		checkDeviceType2(targetId[0]);
	        	else
	        		checkDeviceType(sDeviceList[0]);
	        }else if(targetDeviceType == "meter"){
	        	if(isRetry =="true"){
	        		checkDeviceType3(targetId[0]);
	        	}
	        	else
	        		modemType=searchDeviceType;
			}
		
	     if((modemType == "Ethernet" || modemType == "MBB" || modemType == "RF") && (targetDeviceType=="meter"||targetDeviceType=="modem")){
	    	 executeType = $("input:radio[name='executeType']:checked").val();
	    	 
	     }
 
	     var autopropagation = "true";
	     if($("input:checkbox[name='propagation']").is(":checked") == true)
	    	 autopropagation = "true";
	     else
	    	 autopropagation = "false";
	    
	     var commandType = $("input:radio[name='commandCheck']:checked").val();
	     if(commandType==0)
	    	 commandType="MODEM_OTA";
	     else if(commandType==1)
	    	 commandType="CLONE_ON";
	     else if(commandType==2)
	    	 commandType="CLONE_OFF";
      
	     
	     var cloningTime = $("#sCloningTime").val();
	     
		Ext.Msg.wait('Waiting for response.', 'Clone on/off');
		Ext.Ajax.request({
			url : '${ctx}/gadget/device/command/cmdCloneOnOffStart.do', // OTA 실행 URL 넣어야함
			method : 'POST',
			timeout : extAjaxTimeout,
			params : {
			    	loginId 	: loginId,
			       	target      : target,
					targetType 	: targetDeviceType,
					fId         : fileInfo.fId,
					otaExecuteType: executeType, //CLONE_OTA(0), EACH_BY_DCU(1), EACH_BY_HES(2), EACH_BY_MODEM(3);
					otaExecuteTime: otaExecuteTime,
					isImmediately : isImmediately,
					otaRetryCount : otaRetryCount,
					otaRetryCycle : otaRetryCycle,
					propagation : autopropagation,
					cloningTime : cloningTime,
					commandType: commandType
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
	     if((modemType == "Ethernet" || modemType == "MBB" || modemType == "RF") && (targetDeviceType=="meter"||targetDeviceType=="modem")){
			otaExecuteType = Ext.getCmp('otaExecuteType').getValue().inputValue;
	     }
	     
	     var otaViaUploadChannel="false"
	     if(modemType == "MBB" && (targetDeviceType=="meter"||targetDeviceType=="modem")){
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
		    			var targetDeviceType2 = targetDeviceType.toUpperCase(); 	
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
 	
</script>

	<div id="firmwareTab">
		<ul>
			<li><a class='topTabType' href="#firmwareTabDiv" id="_firmwareTab">Firmware</a></li>
			<li><a class='topTabType' href="#firmwareHistoryTabDiv" id="_firmwareHistoryTab">Firmware History</a></li>
		</ul>

		<!-- location(DSO) (s)-->
		<div id="treeDivAOuter" class="tree-billing auto" style="display: none;">
			<div id="treeDivA"></div>
		</div>
		<!-- location(DSO) (e)-->
		
		<!--  Firmware Tab (S) -->
		<div id="firmwareTabDiv" class="max">
			<div class="gadget_body">
				<div>
					<label class="check ">Firmware</label>
				</div>
				<!-- LEFT SIDE. 펌웨어를 관리하는 리스트 -->
				<div id="leftDiv" class="width-50 margin-t10px floatleft border_blu">
					<div class="search-bg-basic">
						<ul class="basic-ul"> 
							<li class="basic-li bluebold11pt withinput"><fmt:message
									key="aimir.targetType" /></li>
							<li class="basic-li"><select id="deviceType"
								name="deviceType" class='selectbox' style="width: 130px;"
								onchange="javascript:changeDeviceType();">
									<option value="dcu" selected><fmt:message key="aimir.mcu" /></option>
									<option value="dcu-kernel">DCU Kernel</option>
									<option value="dcu-coordinate">DCU Coordinator</option>
									<option value="modem"><fmt:message key="aimir.modem" /></option>
									<option value="meter"><fmt:message key="aimir.meter" /></option>
							</select></li>
						</ul>
					</div>

					<div class="wfree border-bottom padding10px">
						<table class="searching">
							<tbody>
								<tr>
									<!-- 버튼 -->
									<td class="withinput" style="width: 60px">File Name</td>
									<td><input id="sFileName" type="text"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td class="withinput" style="width: 40px">Model</td>
									<td><input id="sModelName" type="text"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td class="withinput" style="width: 68px">FW Version</td>
									<td><input id="sFwVer" type="text" style="width: 100px;"></td>
									<td class="padding-r5px"></td>
									<td><em class="am_button margin-t2px"><a
											href="javascript:searchList();">Search</a></em></td>
								</tr>
							</tbody>
						</table>
						<tbody>
							<tr>
								<td class="padding-r5px"></td>
								<td><em class="am_button margin-t2px"><a href="javascript:firmwareAdd();">New Firmware File Upload</a></em></td>
								<%-- <td class="padding-r5px"></td> 
								<td><em id="fwUpdateButton" class="am_button margin-t2px"><a href="javascript:firmwareModify();"><fmt:message key="aimir.contextItem.update"/></a></em></td> --%>
								<td class="padding-r5px"></td>
								<td>
									<em id="fwDeleteButton" class="am_button margin-t2px">
									<a href="javascript:firmwareDelete();">Firmware File Delete</a>
									</em>
								</td>
							</tr>
						</tbody>
						</table>
					</div>

					<!-- 펌웨어 그리드에서 선택된 항목 정보 출력 -->
					<div id="parameterDiv" class="margin10px padding-b10px border_blu">
						<table class="wfree margin10px">
							<tr>
								<td class="graybold11pt withinput">Manufacturer</td>
								<td class="padding-r20px withinput"><label id="tdCreator"
									style="width: 150px">-</label></td>
							</tr>
							<tr>
								<td class="graybold11pt withinput">Model</td>
								<td class="padding-r20px withinput"><label id="tdModelName"
									style="width: 150px">-</label></td>
							</tr>
							<tr>
								<td class="graybold11pt withinput">F/W Version</td>
								<td class="padding-r20px withinput"><label id="tdVersion"
									style="width: 150px">-</label></td>
							</tr>
							<tr>
								<td class="graybold11pt withinput">File Name</td>
								<td class="padding-r20px withinput"><label id="tdFileName"
									style="width: 150px">-</label></td>
							</tr>
						</table>
					</div>
					<!-- 펌웨어 리스트 그리드 -->
					<div id="fwListGridDiv" class="margin10px">The Grid will
						appear within here.</div>
				</div>
				<!-- Right Tab (S) -->
				<div id="rightTab">
					<ul style="display: inline-block;">
						<li><a class='' href="#otaTabDiv" id="_otaTab">OTA</a></li>
						<li><a class='' href="#cloneOnTabDiv" id="_cloneOnTab">Clone On</a></li>
					</ul>
				<!-- RIGHT SIDE. 등록된 기기 리스트 -->
				<!--  Firmware Tab (S) -->
				<div id="otaTabDiv" class="max">
				<div id="rightDiv"
					class="width-49 margin-t10px padding-t10px floatright border_blu"
					style="overflow:auto;"
					>
					<label class="check margin-l10">Target List</label>
					<div class="searchoption-container">
						<table class="searchoption wfree">
							<tbody>
								<tr>
									<!-- 버튼 -->
									<td class="withinput" style="width: 30px">DSO</td>
									<td><input id="sLocationId" type="text" onclick="javascript:setDSONames()"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td id="dcuNameLabel" class="withinput" style="width: 45px">DCU ID</td>
									<td><input id="dcuName" type="text"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td class="withinput" style="width: 65px">FW Version</td>
									<td><input id="firmwareVersion" type="text" style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td class="withinput" style="width: 55px">Device ID</td>
									<td><input id="deviceId" type="text" style="width: 100px;"></td>
									<td class="padding-r5px"></td>
									<td><em class="am_button margin-t2px"><a
											href="javascript:deviceSearch();">Search</a></em></td>
									<!-- <td class="padding-r5px"></td>
									<td><em class="am_button margin-t2px"><a
											href="javascript:deviceSearch();">Advanced Search</a></em></td> -->
									<td class="padding-r5px"></td>
									<td><em class="am_button margin-t2px"><a
											href="javascript:groupByVersionOTA();">Run OTA</a></em></td>
											
				                    </br>
						
						
								</tr>
							</tbody>
						</table>
					</div>
					<div id= "deviceTypeDiv" class="searchoption-container">
										</br>
				                        <select id="deviceType2"  
											name="deviceType2" class='selectbox' style="width: 130px; visibility: hidden"
											onchange="javascript:changeDeviceType2();">
												<option value="RF" selected>RF</option>
												<option value="Ethernet">Ethernet</option>
												<option value="MBB">MBB</option>
										</select>
										<td class="padding-r5px"></td>
										<td><em class="am_button margin-t2px"><a
											href="javascript:advancedDeviceSearch();">Advanced Search</a></em></td>
										<td class="padding-r5px"></td>
										<td><em class="am_button margin-t2px"><a
											href="javascript:excelDeviceSearch();">Excel Search</a></em></td>
					</div>
					<div id= "deviceTypeDivTemp" class="searchoption-container">
										<td class="padding-r5px"></td>
										<td><em class="am_button margin-t2px"><a
											href="javascript:advancedDeviceSearch();">Advanced Search</a></em></td>
										<td><em class="am_button margin-t2px"><a
											href="javascript:excelDeviceSearch();">Excel Search</a></em></td>
										</br>
					</div>
					

					<!-- 장비 리스트 그리드 -->
					<div id="deviceListGridDiv2" class="margin10px"></div>
				</div>
				</div>
				<!-- OTA Tab (E) -->
				
				<!-- Clone On Tab (S) -->
				<div id="cloneOnTabDiv" class="max">
					<div id="rightDiv2"
					class="width-49 margin-t10px padding-t10px floatright border_blu"
					style="overflow:auto;"
					>
					<label class="check margin-l10">Target List</label>
					<div class="searchoption-container">
						<table class="searchoption wfree">
							<tbody>
								<tr>
									<!-- 버튼 -->
									<td class="withinput" style="width: 65px">FW Version</td>
									<td class="padding-r20px withinput">
										<input id="firmwareVersiontxt" type="text" style="width: 100px;" readonly="readonly" >
										<input id="firmwareVersion2" type="hidden" >
										<!-- <label id="fw_ver"style="width: 150px">-</label> -->
									</td>
									<td class="padding-r10px"></td>
									<td class="withinput" style="width: 30px">DSO</td>
									<td><input id="sLocationId2" type="text" onclick="javascript:setDSONames2()"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td id="dcuNameLabel" class="withinput" style="width: 45px">DCU ID</td>
									<td><input id="dcuName2" type="text"
										style="width: 100px;"></td>
									<td class="padding-r10px"></td>
									<td><input type='checkbox' id='parentCheck' name='parentCheck' onClick='chkParent()' checked/></td>
									<td class="withinput" style="width: 140px">Remaining Target Parent</td>
									<td class="padding-r10px"></td>
									<td><em class="am_button margin-t2px"><a
											href="javascript:deviceSearch2();">Search</a></em></td>
									<td class="padding-r5px"></td>
									<td><em class="am_button margin-t2px"><a href="javascript:excelDeviceSearch();">Excel Search</a></em></td>
								</tr>
								<tr id="sMeterTypeDiv">
									<td class="padding-r20px withinput" style="width: 30px">Meter Type</td>
									<td><input id="sMeterType" type="text" style="width: 100px;" value="RF" readonly></td>
								</tr>
							</tbody>
						</table>
					</div>
					<table class="wfree margin10px">
						<tr>
							<td class="margin10px" style="padding-left: 12px;">
								<input type='checkbox' id='allCheck2' onClick='javascript:chkAll2()' />All DCU
							</td>
						</tr>
						<tr>
							<td>
								<div id="deviceListGridDiv3" class="margin10px"></div>
							</td>
							<td>
							<div  id="target" class="margin10px padding-b10px border_blu" style='line-height:130%'>
								<div style="height:5px;"></div>
								<table class="wfree margin10px">
									<tr class="padding-r10px " style="height:2px;">
										<label class="check margin-l10">Execute Clone On/Off</label>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Execute Time:</td> 
										<td class="padding-r10px ">
										<input type="radio" id="runnow" name="executetime" value="RunNow" class="trans" checked/>Run Now
										</td>
										<td class="padding-r10px " colspan="3">
										<input type="radio" id="timesetting" name="executetime" value="SET" class="trans" />Time Setting
										</td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Date :</td>
										<td class="margin-r5"><input id="executeDate" class="day" type="text" ></td>
										<td class="margin-t2px"><input type="hidden" id="executeDateHidden" /></td> 
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Hour : </td>
										<td class="padding-r10px"><input type="text" id="sHour" style="width: 30px;" /></td> 
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Min : </td>
										<td class="padding-r10px"><input type="text" id="sMin" style="width: 30px;" /></td> 
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt" style="width: 120px;">Retry Count : </td>
										<td class="padding-r10px"><input type="text" id="sRetryCnt" style="width: 100px;" value="2"/></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Retry Cycle(H) : </td>
										<td class="padding-r10px"><input type="text" id="sRetryCycle" style="width: 100px;" value="3"/></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt" style="width: 120px;">Propagation :</td> 
										<td class="padding-r10px" colspan="2">
										<input type='checkbox' id='typeCheck' name="propagation" checked/>Auto Propagation
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Command :</td> 
										<td class="padding-r10px bluebold11pt">
										<input type='radio' id ="cloneon" name='commandCheck' value="CLONE_ON" class="trans" checked/>Clone On
										</td>
										<td class="padding-r10px bluebold11pt"  colspan="3">
										<input type='radio' id ="cloneoff" name='commandCheck' value="CLONE_OFF" class="trans" />Clone Off
										</td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt"style="width: 110px;">Cloning Time(H) :</td> 
										<td class="padding-r10px"><input type="text" id="sCloningTime" style="width: 100px;" value="24"/></td>
									</tr>
									<tr>
										<td class="padding-r10px graybold11pt">Execute Type :</td> 
										<td class="padding-r10px">
										<input type='radio' id="bydcu" name='executeType' value="1" class="trans" checked/>By DCU
										</td>
										<td class="padding-r10px" colspan="2">
										<input type='radio' id="byhes" name='executeType' value="2" class="trans" />By HES
										</td>
									</tr>
									<tr>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
										<td class="padding-r10px" style="float:right;line-height:100%"><em class="am_button margin-t2px"><a
											href="javascript:groupByClone();">Execute</a></em></td>
									</tr> 
								</table>
								<br>
							</div>
							<div class="margin10px padding-b10px border_blu " style='line-height:110%'>
							<!-- <div style="height:1px;"></div> -->
								<ul class="basic-ul">
									<li class="graybold11pt">1. Execute Time</li> 
									<li class="" style="margin-left:15px;">It can run immediately(Run Now) or special date time(Time Setting).</li>
									<li class="graybold11pt">2.	Retry Count  </li><li style="margin-left:15px;">Maximum retry count from started time.</li>
									<li class="graybold11pt">3.	Retry Cycle   </li><li style="margin-left:15px;">Retry clone on/off after N hours</li>
									<li class="graybold11pt">4.  Remaining Target Parent</li>
									<li class="" style="margin-left:15px;"> Proceed from the node where clone OTA propagation stops.</li>
									<li class="graybold11pt">5. Propagation</li>
									<li class="" style="margin-left:15px;"> Auto Propagation (Default: checked)</li>
									<li class="graybold11pt">6. Cloning Time</li>
									<li class="" style="margin-left:15px;">  means the time value at which to execute the clone, in units of time.</li>
									<li class="" style="margin-left:15px;">  The value is accepted as a valid value from 5 to 24.</li>
									<li class="" style="margin-left:15px;">  (Other than error handling)</li>
								</ul>
							</div>
							</td>
						</tr>
					</table>
				</div>
				</div>
				<!-- Clone On Tab (E) -->
				</div>
				<!-- Right Tab (E) -->
			</div>
		</div>
		<!--  Firmware Tab (E) -->
		
		<div class="clear">
			<div id="treeDiv_0Outer" class="tree-billing auto" style="display: none;">
				<div id="treeDiv_0"></div>
			</div>
		</div>
		
		<!--  Firmware History Tab (S) -->
		<div id="firmwareHistoryTabDiv" class="max">
			<div class="gadget_body">
				<div>
					<label class="check ">Firmware Issue</label>
				</div>
				<!-- Search Background (S) -->
				<!-- location(DSO) (s)-->
				<div id="treeDivBOuter" class="tree-billing auto" style="display: none;">
					<div id="treeDivB"></div>
				</div>
				<!-- location(DSO) (e)-->
				
				<div class="search-bg-withouttabs">
					<div class="searchoption-container">
						<table class="searchoption wfree">
							<tr>
								<td class="withinput" style="width: 90px;">DSO</td>
								<td colspan="1" class="padding-r20px">
								<input name="searchWord" id='searchWord_FirmwareIssue' class="billing-searchword" type="text" style="width: 189px;" />
								<input type='hidden' id='sfirmwareIssueLocationId' value=''></input></td>

								<td class="withinput">Issue Date</td>
								<td colspan="1" name='select' class="padding-r20px">
									<span><input id="sfirmwareIssueStartDate" class="day" type="text"></span>
									<span><input value="~" class="between" type="text"></span>
									<span><input id="sfirmwareIssueEndDate" class="day" type="text"></span>
									<input id="sfirmwareIssueStartDateHidden" type="hidden">
									<input id="sfirmwareIssueEndDateHidden" type="hidden"></td>
								<!-- <td class="padding-r10px"></td> -->

								<td class="withinput" style="width: 90px;">Model</td>
								<td class="padding-r20px"><input id="sfirmwareIssueModelName" type="text" style="width: 130px;"></td>

								<td class="withinput" width="90px" style="width: 90px;">FW Ver.</td>
								<td class="padding-r20px"><input type="text" id="sfirmwareIssueFwVer" style="width: 189px;"></td>
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:resetFirmwareIssueList();" class="on"><fmt:message key="aimir.form.reset" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Reset Button (E) -->

								<!-- Search Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:searchFirmwareIssueList()" class="on"><fmt:message key="aimir.button.search" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Search Button (E) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:retryButtonClickAll()" class="on">Retry</a></li>
										</ul>
									</div>
								</td>
							</tr>

							<tr>
								<td class="withinput" style="width: 90px;"><fmt:message key="aimir.targetType" /></td>
								<td class="padding-r20px">
								<select id="sfirmwareIssueDeviceType" name="sfirmwareIssueDeviceType" style="width: 130px;" onchange="javascript:changeDeviceType_history();">
									<option value="dcu" selected><fmt:message key="aimir.mcu" /></option>
									<option value="dcu-kernel">DCU Kernel</option>
									<option value="dcu-coordinate">DCU Coordinator</option>
									<option value="modem"><fmt:message key="aimir.modem" /></option>
									<option value="meter"><fmt:message key="aimir.meter" /></option>
								</select>
								</td>

								
								<td class="withinput" style="width: 90px;">File Name</td>
								<td class="padding-r20px"><input id="sfirmwareIssueFileName" type="text" style="width: 189px;"></td>
								
								<td class="withinput commandType" style="width: 90px;">Command Type</td>
								<td class="padding-r20px commandType">
									<select id="sCommandType" name="sCommandType" style="width: 130px;" onchange="javascript:changeExecuteType_history();">
										<option value="" selected><fmt:message key="aimir.all" /></option>
										<option value="0">OTA</option>
										<option value="1">Clone On</option>
										<option value="2">Clone Off</option>
									</select>
								</td>
							</tr>
								<td></td>
								<td></td>
								<td></td>
								
								<td></td>
								<td></td>
								<td></td>

								<td></td>
								<td></td>
								<td></td>

								<%-- <!-- Reset Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:resetFirmwareIssueList();" class="on"><fmt:message key="aimir.form.reset" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Reset Button (E) -->

								<!-- Search Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:searchFirmwareIssueList()" class="on"><fmt:message key="aimir.button.search" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Search Button (E) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:retryButtonClickAll()" class="on">Retry</a></li>
										</ul>
									</div>
								</td> --%>
							<!-- </tr> -->
						</table>

						<div class="clear">
							<div id="treeDiv_0Outer" class="tree-billing auto" style="display: none;">
								<div id="treeDiv_0"></div>
							</div>
						</div>

					</div>
				</div>
				
				<div class="clear">
					<div id="treeDiv_1Outer" class="tree-billing auto" style="display: none;">
						<div id="treeDiv_1"></div>
					</div>
				</div>
				<!-- Search Background (E) -->

				<div id="firmwareIssueGridDiv" class="margin10px"></div>

				<br/><br/>
				<div>
					<label class="check ">Firmware Issue History</label>
				</div>
				<!-- Search Background (S) -->
				<!-- location(DSO) (s)-->
				<div id="treeDivCOuter" class="tree-billing auto" style="display: none;">
					<div id="treeDivC"></div>
				</div>
				<!-- location(DSO) (e)-->
				
				<div class="search-bg-withouttabs">
					<div class="searchoption-container">
						<table class="searchoption wfree">
							<%-- <tr>
								<td class="withinput" style="width: 90px;"><fmt:message key="aimir.location" /></td>
								<td colspan="1" class="padding-r20px">
									<input name="searchWord" id='searchWord_FirmwareIssueHistory' class="billing-searchword" type="text" style="width: 189px;" />
									<input type='hidden' id='sfirmwareIssueHistoryLocationId' value=''></input>
								</td>
				
								<!-- <td class="withinput">Issue Date</td>
								<td colspan="1" name='select' class="padding-r20px">
									<span><input id="sfirmwareIssueHistoryStartDate" class="day" type="text"></span>
									<span><input value="~" class="between" type="text"></span>
									<span><input id="sfirmwareIssueHistoryEndDate" class="day" type="text"></span>
									<input id="sfirmwareIssueHistoryStartDateHidden" type="hidden">
									<input id="sfirmwareIssueHistoryEndDateHidden" type="hidden">
								</td> -->
								
								<td class="withinput" style="width: 90px;">Model</td>
								<td class="padding-r20px"><input id="sfirmwareIssueHistoryModelName" type="text" style="width: 189px;"></td>

								<td class="withinput" width="90px" style="width: 90px;">FW Ver.</td>
								<td class="padding-r20px"><input type="text" id="sfirmwareIssueHistoryFwVer" style="width: 189px;"></td>
							</tr> --%>

							<tr>
								<td class="withinput" style="width: 90px;"><fmt:message key="aimir.targetType" /></td>
								<td class="padding-r20px">
									<select id="sfirmwareIssueHistoryDeviceType" name="sfirmwareIssueHistoryDeviceType" style="width: 130px;" onchange="javascript:changeDeviceTypeForFirmwareIssueHistory();">
										<option value="dcu" selected><fmt:message key="aimir.mcu" /></option>
										<option value="dcu-kernel">DCU Kernel</option>
										<option value="dcu-coordinate">DCU Coordinator</option>
										<option value="modem"><fmt:message key="aimir.modem" /></option>
										<option value="meter"><fmt:message key="aimir.meter" /></option>
									</select>
								</td>
								<td class="withinput" style="width: 40px;">Step</td>
								<td class="padding-r20px">
									<select id="sfirmwareIssueHistoryStep" name="sfirmwareIssueHistoryStep" style="width: 150px;">
										<option value="All" selected>All</option>
										<option value="Started writing FW">Execute Started</option>
										<option value="Took OTA Command">Took Command</option>
										<option value="Ended writing FW">Execute Ended</option>
										<option value="OTA Result">Result</option>
										<option value="Firmware Update">Firmware Update</option>
										<option value="Intergrity Deviation">Intergrity Deviation</option>
									</select>
								</td>
								<!-- <td class="withinput" style="width: 90px;">File Name</td>
								<td class="padding-r20px"><input id="sfirmwareIssueHistoryFileName" type="text" style="width: 189px;"></td> -->

								<td class="withinput" width="90px" style="width: 90px;">Target ID</td>
								<td class="padding-r20px"><input type="text" id="sfirmwareIssueHistoryTargetId" style="width: 189px;"></td>
								<td class="padding-r10px"></td>

								<td></td>
								<td></td>
								<td></td>
								
								<!-- Reset Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:resetFirmwareIssueHistoryList();" class="on"><fmt:message key="aimir.form.reset" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Reset Button (E) -->

								<!-- Search Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:searchFirmwareIssueHistoryList()" class="on"><fmt:message key="aimir.button.search" /></a></li>
										</ul>
									</div>
								</td>
								<!-- Search Button (E) -->
								
								<!-- Retry Button (S) -->
								<td>
									<div id="btn">
										<ul style="margin-left: 0px">
											<li><a href="javascript:retryButtonClick()" class="on">Retry</a></li>
										</ul>
									</div>
								</td>
								<!-- Retry Button (E) -->
							</tr>
						</table>

						<div class="clear">
							<div id="treeDiv_0Outer" class="tree-billing auto"
								style="display: none;">
								<div id="treeDiv_0"></div>
							</div>
						</div>

					</div>
				</div>
				
				<div class="clear">
					<div id="treeDiv_2Outer" class="tree-billing auto" style="display: none;">
						<div id="treeDiv_2"></div>
					</div>
				</div>
				<!-- Search Background (E) -->

				<div id="firmwareIssueHistoryGridDiv" class="margin10px"></div>
			</div>
		</div>
		<!--  Firmware History Tab (E) -->
	</div>

	<!-- 파일 업로드 화면 시작 (floating window) -->
	<div id="uploadWindowDiv"></div>
	<!-- 파일 업로드 화면 끝 -->
</body>
</html>
