<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }

        /* no Icon */
        .no-icon {
            display: none;
            background-image:url(${ctx}/js/extjs/resources/images/default/s.gif) !important;
        }

        .x-treegrid-text {
            padding-right:4px !important;
        }
        #ui-datepicker-div {
			z-index: 20000 !important;
		}	
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <%-- Ext-JS 관련 --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
    <script type="text/javascript" charset="utf-8">
    
    //공급사ID와 이름(이름으로 일부 기능 제한)
    var supplierId="${supplierId}";
    var supplierName="${supplierName}";
    
    var modemId="${modemId}";
    
    //선택된 미터 arrayList
    var selectedRows = new Array();
    var selectedRows2 = new Array();

    var mdsIds = new Array();
    var mdsIds2 = new Array();

    var ModemAddMeterGridData = [];
    
    var permitLocationId = "${permitLocationId}";  // location 제한

    var numberFormat = "";
    
    // onload
    $(document).ready(function(){
    	
    	//load Search Condition
    	getDeviceVendorsBySupplierId();
    	getDeviceModelsByVenendorId();
    	getMeterGroupBygroupId();
    	$('#sMeterTypePopup').selectbox();
    	$('#sStatusPopup').selectbox();
        
        locationTreeGoGo('treeDivMe', 'searchWordMePopup', 'sLocationIdMePopup');
    	
    	var locDateFormat = "yymmdd";
        $("#sInstallStartDatePopup")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sInstallEndDatePopup")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        
        //load Grid data
        getModemAddMeterGrid();
        getMeterListByNotModemGrid();
        
		//show Grid
    	$("#meterListByNotModemGridDiv").show();
        $("#modemAddMeterGridDivPopup").show();
    	
    });
    
    
    
    
    //##########################
    //modemAddMeterGridDivPopup 체크 컬럼 모델 정의.
    //##########################
    
    //체크 컬럼 모델 정의.
    var myCboxSelModel = new Ext.grid.CheckboxSelectionModel({
        singleSelect: false
    });

    //체크박스 모델에 리스너 등록.
    myCboxSelModel.addListener( 'rowselect', funcRowselect);
    myCboxSelModel.addListener( 'rowdeselect', funcRowdeselect);

    //MeterListGrid row SElect Event
    function funcRowselect(selectionmodel, rowIdx, h)
    {
        selectedRows2= selectionmodel.getSelections();

        //reset array
        mdsIds2 = [];

        for(i=0; i<selectedRows2.length; i++)
        {
            mdsIds2[i]= selectedRows2[i].get('mdsId');
        }
    }

    // MeterListGrid row de-select Event
    function funcRowdeselect(selectionmodel, rowIdx, h) {
         selectedRows2= selectionmodel.getSelections();

         //reset array
         mdsIds2 = [];

        for (i = 0; i < selectedRows2.length; i++) {
            mdsIds2[i]= selectedRows2[i].get('mdsId');
            //alert(mdsIds2[i]);
        }
    }
    
    //##########################
    //MeterListGridByNotModem 체크 컬럼 모델 정의.
    //##########################
    var myCboxSelModel2 = new Ext.grid.CheckboxSelectionModel({
        singleSelect: false
    });

    //체크박소 모델에 리스너 등록.
    myCboxSelModel2.addListener( 'rowselect', funcRowselect2);
    myCboxSelModel2.addListener( 'rowdeselect', funcRowDeselect2);

    //미터에 등록 되지 않은 미터 리스트/ row select 이벤트.)
    function funcRowselect2(selectionmodel, rowIdx, h) {

        var row= selectionmodel.getSelected();

        selectedRows= selectionmodel.getSelections();

        //reset array
        mdsIds = [];

        for(i = 0; i < selectedRows.length; i++) {
            mdsIds[i]= selectedRows[i].get('meterMds');
        }
    }

    //미터에 등록 되지 않은 미터 리스트/ row deselect 이벤트.)
    function funcRowDeselect2(selectionmodel, rowIdx, h) {
        var row= selectionmodel.getSelected();

        selectedRows= selectionmodel.getSelections();

        //reset array
        mdsIds = [];

        for(i = 0; i < selectedRows.length; i++) {
            mdsIds[i]= selectedRows[i].get('meterMds');
        }
    }
    

    //##############################
    //#### button Events Start######
    //##############################

    //Add Meter with Modem
    function meterAdd() {
        if(mdsIds.length>0){
	        Ext.MessageBox.confirm('meterAdd', 'Do you want Add Meter?', function(btn){ 
			    if (btn == 'yes') {
			    	meterOk();
			    }
			    else{// btn == 'No'
			    }
	        });
	        
        }else{//Nothing selected
        	Ext.MessageBox.show({
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.nometer'/>',
                icon : Ext.MessageBox.INFO
            });
        }
    }// meterAdd Click End
    

    function meterOk() {
        //#mdsIds의 갯수 만큼 모뎀에 meter를 등록.
        for (var i = 0; i < mdsIds.length; i++) {
            $.ajax({
                type:"POST",
                data:{
                    "mdsId":mdsIds[i]
                    ,"modemId":modemId
                },
                dataType:"json",
                //미터를 모뎀에 등록.
                url:"${ctx}/gadget/device/setModemId.do",
                success:function(data, status) {
                	
                	//reload from s.s
                	getModemAddMeterGrid();
                	getMeterListByNotModemGrid();
                	
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meteraddsucceed'/>");
                },
                error:function(request, status) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meteraddfail'/>");
                }
            }); // Ajax End
        }// for End
        mdsIds = new Array();
    }

    
    function meterDelete() {
    	if(mdsIds2.length>0){
	       	Ext.MessageBox.confirm('meterDelete', 'Do you want Delete Meter?', function(btn){ 
			    if (btn == 'yes') {
	       			for (var i = 0; i < mdsIds2.length; i++) {
	             		$.ajax({
	                    	type:"POST",
	                    	data:{
	                    	    "mdsId":mdsIds2[i]
	                    	},
	                    	dataType:"json",
	                    	// 미터delete
	                    	url:"${ctx}/gadget/device/unsetModemId.do",
	                    	success:function(data, status) {
	                    		// reload from s.s
	        	        		getModemAddMeterGrid();
	        	        		getMeterListByNotModemGrid();

	        	               	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meterdelete'/>");

	                    	},
	                    	error:function(request, status) {
	                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meterdeletefail'/>");
	                    	}
	            		}); // Ajax End
	        		} // for End
	        		mdsIds2 = new Array();
	        	}else{ // btn == 'No'
			    	
			    }
			});
    	}else{//Nothing selected
        	Ext.MessageBox.show({
                //title: '<fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.nometer'/>',
                icon : Ext.MessageBox.INFO
            });
        }
    }
    
    
    //###########################
    //   modemAddMeterGrid__Start
    //###########################

    //Fetch modemAddMeterGrid from S.S.
    // Meter List 를 조회
    function getModemAddMeterGrid() {
        $.getJSON('${ctx}/gadget/device/getMeterListByModem.do'
                , {'modemId' : modemId}
                , function(json) {
                      ModemAddMeterGridData = json;
                      // Grid 생성 function 호출
                      makeModemAddMeterGridPopup();
                  });
        
        
    }

    
    //MeterListGridByNotModem propeties
    var meterListByNotModemGridInstanceOn = false;
    var meterListByNotModemGrid;
    var meterListByNotModemGridModel;
    var meterListByNotModemGridStore = new Ext.data.JsonStore({});
    
    //MeterListGridByNotModem method
    //미터에 등록 되지 않은 미터 리스트 fetch from S.S.
    function getMeterListByNotModemGrid() {
        //setting grid panel width
        var gridWidth = $("#meterListByNotModemGridDiv").width();
        var meterCondition = getConditionForMeter();
        
        if (meterListByNotModemGridInstanceOn == false) {
        
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title
        fmtMessage[2] = "<fmt:message key="aimir.normal"/>";

        // meterListByNotModemGrid Model DEfine
        meterListByNotModemGridModel = new Ext.grid.ColumnModel({
            columns: [
                myCboxSelModel2
                //,{header: "no", dataIndex: 'no', width:50, align: 'center'}
                ,{
                    header: "<fmt:message key='aimir.number'/>",
                    dataIndex: 'no',
                    align:'center',
                    width: 55,
                    renderer: function(value, me, record, rowNumber, columnIndex, store) {
                        return Ext.util.Format.number(store.totalLength - value + 1, numberFormat);
                    },
                    sortable: true
                },{header: "Meter", dataIndex: 'meterMds', width:(gridWidth-50), align: 'center'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        

            //Grid panel instance create
            meterListByNotModemGridPanel = new Ext.grid.GridPanel({
                store: meterListByNotModemGridStore,
                colModel : meterListByNotModemGridModel,
               //selectModel define.
                sm: myCboxSelModel2,
                autoScroll:false,
                //scroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 295,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'meterListByNotModemGridDiv',
                viewConfig: {
                    //forceFit:true,
                    enableRowBody:false,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
                //paging
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : meterListByNotModemGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            meterListByNotModemGridInstanceOn = true;
        } else {
        	
            //### meterListByNotModemGrid Store fetch
            meterListByNotModemGridStore = new Ext.data.JsonStore({
            	
                //Using Meter Search
                autoLoad : {params:{start: 0, limit: 10}},
                url : '${ctx}/gadget/device/getMeterSearchGrid.do',
                baseParams: {
                    sMeterType         : meterCondition[0],
                    sMdsId             : meterCondition[1],
                    sStatus            : meterCondition[2],
                    sMcuName           : '',
                    sLocationId        : meterCondition[4],
                    sConsumLocationId  : meterCondition[5],
                    sVendor            : meterCondition[6],
                    sModel             : meterCondition[7],
                    sInstallStartDate  : meterCondition[8],
                    sInstallEndDate    : meterCondition[9],
                    sModemYN           : 'N',//
                    sCustomerYN        : '',//meterCondition[11],
                    sLastcommStartDate : '',//meterCondition[12],
                    sLastcommEndDate   : '',//meterCondition[13],
                    sOrder             : meterCondition[14],
                    sCommState         : meterCondition[15],
                    supplierId         : supplierId,//
                    sMeterGroup        : meterCondition[17],
                    sGroupOndemandYN   : 'N',
                    sPermitLocationId  : meterCondition[20],
                    sMeterAddress      : '',//meterCondition[21],
                    sHwVersion         : "",
                    sFwVersion         : "",
                    sGs1				: meterCondition[22],
                    sMbusSMYN          : meterCondition[23],
                    sDeviceSerial	   : meterCondition[24],
                    sType : '',
                    fwGadget : 'N',
                    sNotDeleted : 'Y'
                },
                root:'gridData',
                totalProperty : 'totalCnt',
                idProperty : 'no',
                listeners : {
                    beforeload: function(store, options){
                    	Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                        });
                    },load: function(store, record, options){
                    	Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                        });
                    	$("#resultCnt").html(store.reader.jsonData.totalCnt);
                    },
                },
                fields: [
                	{ name: 'no', type: 'string' },//"no",
                    { name: "meterMds", type: "string" },
                    { name: "mdNumberPattern", type: "string" }
                         ]
            });//Store End
        	
            meterListByNotModemGridPanel.setWidth(gridWidth);
            meterListByNotModemGridPanel.setHeight(295);
            meterListByNotModemGridPanel.reconfigure(meterListByNotModemGridStore, meterListByNotModemGridModel);
            var bottomToolbar = meterListByNotModemGridPanel.getBottomToolbar();
            bottomToolbar.bindStore(meterListByNotModemGridStore);
        }

        hide();
    }// End of getMeterListByNotModemGrid

    


    //modemAddMeterGrid propeties
    var modemAddMeterGridPopupInstanceOn = false;
    var modemAddMeterGrid;
    var modemAddMeterGridModel;

    
    function makeModemAddMeterGridPopup() {
        //setting grid panel width
        var gridWidth = $("#modemAddMeterGridDivPopup").width();

        //### modemAddMeterGrid Store fetch
        	var modemAddMeterGridStorePopup = new Ext.data.JsonStore({
            autoLoad: true,
            //url: "${ctx}/gadget/device/getMeterListByModem.do",
            data: ModemAddMeterGridData,
            //파라매터 설정.
            baseParams: {
                modemId:modemId
            },
            root:'gridData2',
            fields: [
                      "no"
                     , "mdsId"
                     ]
        });//Store End

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title
        fmtMessage[2] = "<fmt:message key="aimir.normal"/>";

        // modemAddMeterGrid Model DEfine
        var modemAddMeterGridModelPopup = new Ext.grid.ColumnModel({
            columns: [
               myCboxSelModel,
               {header: "no", dataIndex: 'no', width:50, align: 'center'}
               ,{header: "Meter", dataIndex: 'mdsId', width:(gridWidth-50),  align: 'center', editor:new Ext.form.TextField({allowBlank:false})}
            ],
            defaults: {
                sortable: false
               ,menuDisabled: true
               ,width: 120 
            }
        });

        if (modemAddMeterGridPopupInstanceOn == false) {
        	//Grid panel instance create
            modemAddMeterGridPanelPopup = new Ext.grid.EditorGridPanel({
            	clicksToEdit: 1,
            	store: modemAddMeterGridStorePopup,
                colModel : modemAddMeterGridModelPopup,
                sm: myCboxSelModel,
                
                autoScroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 295,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'modemAddMeterGridDivPopup',
                viewConfig: {
                    //forceFit:true,//
                    enableRowBody:false,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
                //paging
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : modemAddMeterGridStorePopup,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });

            modemAddMeterGridPopupInstanceOn = true;
        }else {
            modemAddMeterGridPanelPopup.setWidth(gridWidth);
            modemAddMeterGridPanelPopup.setHeight(295);
            modemAddMeterGridPanelPopup.reconfigure(modemAddMeterGridStorePopup, modemAddMeterGridModelPopup);
            var bottomToolbar2 = modemAddMeterGridPanelPopup.getBottomToolbar();
            bottomToolbar2.bindStore(modemAddMeterGridStorePopup);
        }
        hide();
    };//func makeModemAddMeterGridPopup End
    

    
    
    
    function getConditionForMeter() {
        var arrayObj = Array();

        arrayObj[0] = $('#sMeterTypePopup').val();
        arrayObj[1] = $('#sMdsIdPopup').val();
        arrayObj[2] = $('#sStatusPopup').val();
        arrayObj[4] = $('#sLocationIdMePopup').val();
        arrayObj[6] = $('#sVendorPopup').val();
        arrayObj[7] = $('#sModelPopup').val();
        arrayObj[8] = $('#sInstallStartDatePopupHidden').val();
        arrayObj[9] = $('#sInstallEndDatePopupHidden').val();

        arrayObj[14] = '2';//$('#sOrder').val();
        arrayObj[15] = $('#sCommStatePopup').val();
        arrayObj[16] = supplierId;
        arrayObj[17] = $('#sMeterGroupPopup').val();
        arrayObj[20] = permitLocationId;
        arrayObj[22] = $('#sGs1Popup').val();
        arrayObj[23] = $('#sMbusSMYNPopup').val();
        arrayObj[24] = $('#sDeviceSerialPopup').val();
      	
        return arrayObj;
    }
    
    //get vendor list
    function getDeviceVendorsBySupplierId() {
    	$.getJSON('${ctx}/gadget/system/vendorlist.do', {
    	    'supplierId' : supplierId
    	}, function(returnData) {
    	   $('#sVendorPopup').loadSelect(returnData.deviceVendors);
    	   $('#sVendorPopup').selectbox();
    	   });
    };
    
    //get Model list by vendor Id
    function getDeviceModelsByVenendorId() {
    if ($('#sVendorPopup').val() != ""){
    	$.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do', {
            'vendorId' : $('#sVendorPopup').val()
        }, function(returnData) {
            $('#sModelPopup').noneSelect(returnData.deviceModels);
            $('#sModelPopup').selectbox();
        });
    }else{    	
    	$("#sModelPopup").find("option").remove();
    	$("#sModelPopup").prepend("<option value=''>-</option>");
    	$('#sModelPopup').selectbox();
    }
        
	};
	
	
	//get meter group
	function getMeterGroupBygroupId() {
        $.getJSON('${ctx}/gadget/system/getMeterGroupBygroupId.do', {
            'supplierId' : supplierId,
            'groupType' : 'Meter'
        }, function(returnData) {
            $('#sMeterGroupPopup').loadSelect(returnData.NAME);
            $('#sMeterGroupPopup').selectbox();
        	});
    	};


    /* function loadMeterSearchCondition(){      	
    	
    	getDeviceVendorsBySupplierId();
    	getDeviceModelsByVenendorId();
    	getMeterGroupBygroupId();
    	$('#sMeterTypePopup').selectbox();
    	$('#sStatusPopup').selectbox();
        
    	// 지역검색
        locationTreeGoGo('treeDivMe', 'searchWordMePopup', 'sLocationIdMePopup');
    	
    	//Date picker in Popup
    	var locDateFormat = "yymmdd";
        $("#sInstallStartDatePopup")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sInstallEndDatePopup")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
		}
     */
    
    function hideDatepicker(){
    	$('input[type=text]#sInstallStartDatePopup').datepicker("hide");
    	$('input[type=text]#sInstallEndDatePopup').datepicker("hide");
    	
    	$('input[type=text]#sInstallStartDate').datepicker("hide");
    	$('input[type=text]#sInstallEndDate').datepicker("hide");
    	
    	$('input[type=text]#sLastcommStartDate').datepicker("hide");
    	$('input[type=text]#sLastcommEndDate').datepicker("hide");
    }
    
    
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
    
    //reset for meter search condition
	function meterSearchReset() {
		// Form Reset
		var $searchForm = $("form[name=search]");
		$searchForm.trigger("reset");

		// Form Reset으로 초기화되지 않는 인자 초기화
		$('#sLocationIdMePopup').val("");
        $("#resultCnt").html("0");
        $('#sInstallStartDatePopupHidden').val('');
        $('#sInstallEndDatePopupHidden').val('');

		// 셀렉트 태그 첫번째 인덱스 선택
		var $selects = $searchForm.find("select");
		$selects.each(function() {
			$(this).selectbox();
		});
		return;
	}
    
    
    
    </script>
</head>
<body>
<div class="mvm-popwin-body" style="width:= 1450px;">
	<!-- 그룹멤버추가 전체 (S)
	<div class="gadget_body" style="padding: 0px;"> -->
	    <!-- meter 추가 (S) -->
	    <div class="groupmanage-create-member blueline bg-blue clear" style="height: 485px !important;">
	        <ul class="width">
	            <li class="padding minustop">
	                <div class="blueline-searchoption">
	                    <!-- Meter 검색조건 (S)-->
	                    <div id="searchMeter">
	                        <form name="search">
	                            <!-- Search Background (S) -->
	                            <!-- <div class="search-bg-withouttabs"> -->
	                            <div class="searchoption-container">
	                                <table class="searchoption wfree" border=0>
	                                    <tr>
	                                        <td class="withinput" style="width: 80px">
	                                            <fmt:message key="aimir.metertype" />
	                                        </td>
	                                        <td class="padding-r20px"><select id="sMeterTypePopup" style="width: 190px;"
	                                                name="select">
	                                                <option value="">
	                                                    <fmt:message key="aimir.all" />
	                                                </option>
	                                                <c:forEach var="meterType" items="${meterType}">
	                                                    <c:choose>
	                                                        <c:when test="${not empty meterType.descr}">
	                                                            <option value="${meterType.name}">${meterType.descr}</option>
	                                                        </c:when>
	                                                        <c:otherwise>
	                                                            <option value="${meterType.name}">${meterType.descr}</option>
	                                                        </c:otherwise>
	                                                    </c:choose>
	                                                </c:forEach>
	                                            </select></td>
	                                        <td class="withinput" style="width: 80px">
	                                            <fmt:message key="aimir.meterid" />
	                                        </td>
	                                        <td class="padding-r20px">
	                                            <input type="text" id="sMdsIdPopup" style="width: 190px;" />
	                                        </td>
	                                        <td class="withinput" style="width: 100px">
	                                            <fmt:message key="aimir.metergroup" />
	                                        </td>
	                                        <td class="padding-r20px"><select id="sMeterGroupPopup" name="select" style="width: 120px;">
	                                                <option value="">
	                                                    <fmt:message key="aimir.all" />
	                                                </option>
	                                            </select></td>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.status" />
	                                        </td>
	
	                                        <td class="padding-r20px"><select id="sStatusPopup" name="select" style="width: 190px;">
	                                                <option value="">
	                                                    <fmt:message key="aimir.all" />
	                                                </option>
	                                                <c:forEach var="meterStatus" items="${meterStatus}">
	                                                    <option value="${meterStatus.id}">${meterStatus.descr}</option>
	                                                </c:forEach>
	                                            </select></td>
	                                    </tr>
	                                    <tr>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.location" />
	                                        </td>
	                                        <td class="padding-r20px"><input name="searchWordMe" id="searchWordMePopup"
	                                                type="text" style="width: 190px" /> <input type="hidden" id="sLocationIdMePopup"
	                                                name="location.id" value=""></input></td>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.vendor" />
	                                        </td>
	                                        <td class="padding-r20px"><select id="sVendorPopup" name="SELECT" style="width: 190px;"
	                                                onChange="javascript:getDeviceModelsByVenendorId();">
	                                                <option value="">
	                                                    <fmt:message key="aimir.all" />
	                                                </option>
	                                            </select></td>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.model" />
	                                        </td>
	                                        <td class="padding-r20px"><select id="sModelPopup" name="select" style="width: 120px;">
	                                                <option value="">
	                                                    <fmt:message key="aimir.all" />
	                                                </option>
	                                            </select></td>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.shipment.gs1" />
	                                        </td>
	                                        <td class="padding-r20px"><input id="sGs1Popup" type="text" style="width: 190px" /></td>
	
	                                    </tr>
	                                    <tr>
	                                        <td class="withinput">
	                                            <fmt:message key="aimir.installationdate" />
	                                        </td>
	                                        <td class="padding-r20px"><span><input id="sInstallStartDatePopup" class="day"
	                                                    type="text"></span>
	                                            <span><input value="~" class="between" type="text"></span>
	                                            <span><input id="sInstallEndDatePopup" class="day" type="text"></span>
	                                            <input id="sInstallStartDatePopupHidden" type="hidden" />
	                                            <input id="sInstallEndDatePopupHidden" type="hidden" />
	                                        </td>
	                                        <td colspan="7" align="right">
	                                            <em class="am_button">
	                                                <a href="javascript:meterSearchReset();">
	                                                    <fmt:message key="aimir.form.reset" /></a>
	                                            </em>&nbsp;
	                                            <em class="am_button">
	                                                <a href="javascript:getMeterListByNotModemGrid()" class="on">
	                                                    <fmt:message key="aimir.button.search" /></a>
	                                            </em>
	                                        </td>
	                                    </tr>
	                                </table>
	
	                                <div id="treeDivMeOuter" class="tree-billing auto" style="display: none;">
	                                    <div id="treeDivMe"></div>
	                                </div>
	                            </div>
	                            <div id="drAlertDataPop"></div>
	                            <div id="drAlertDataPopFailure"></div>
	                            <div id="drAlert"></div>
	                        </form>
	                    </div>
	                    <!-- Meter 검색조건  (E)-->
	                </div>
	                <div class="dashedline-dark clear"></div>
	                <!-- 그리드 (S) -->
	                <div class="flexlist">
	                    <div id="" style="width: 45%; display : inline-block; margin-left:20px;">
	                        <ul>
	                            <li>
	                                <font style="font-weight:bold; color: #676767;">
	                                    <fmt:message key="aimir.searchResult" /> : </font>
	                                <font style="font-weight:bold; color: #FC0000;" id="resultCnt">0</font>
	                            </li>
	                            <br>
	                            <li>
	                                <div id="meterListByNotModemGridDiv"></div>
	                            </li>
	                        </ul>
	                    </div>
	                    <div id="memberSelectBtnDiv" style="width: 5%; height: 325px; display : inline-block;">
	                        <ul id="arrowlist"  style=" display: table; margin-left:30px; margin-top:150px;">
	                            <li class="btn-putin"><a id="memberAddBtn" href="javascript:meterAdd();">
	                                    <!-- 추가 --></a></li>
	                            <li class="btn-putout"><a id="memberDelBtn" href="javascript:meterDelete();">
	                                    <!-- 제거 --></a></li>
	                        </ul>
	                    </div>
	
	                    <div id="" style="width: 45%; display : inline-block; margin-left:20px;">
	                        <ul>
	                            <li style="margin-left:50px;">
	                                <font style="font-weight:bold; color: #676767;">
	                                    <fmt:message key="aimir.mcu.device.connected" />
	                                </font>
	                            </li>
	                            <br>
	                            <li>
	                                <div id="modemAddMeterGridDivPopup"></div>
	                            </li>
	                        </ul>
	                    </div>
	                </div>
	                <div style="clear:both;"></div>
	
	            </li>
	        </ul>
	    </div>
	    <!-- meter 추가 (E) -->
	<!-- </div> -->
</div>

</body>
</html>