<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>그룹 관리</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/ux/css/MultiSelect.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold;
        }
        /* .x-form-field-wrap x-form-field { */
        .x-form-field-wrap .x-form-field {
            margin-top: 0px !important;
        }
        div.blueline    {
		    height:550px;
		    padding-bottom:30px !important;
		    border:1px solid #b4d3f0 !important;
		}
		div.groupmanage-create-tab {
		    height:10px !important;
		    padding-bottom:10px !important;
		    border:1px solid #b4d3f0 !important;
		}
		div.searchModem {
			height: 650px !important;
		}
		div.memberSelectDiv {
			width: 670px !important;
		}
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <%-- <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script> --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/sic.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ux/MultiSelect.js"></script>
    <script language="JavaScript">/*<![CDATA[*/

        var editAuth = "${editAuth}";
        var supplierId = "${supplierId}";
        var operatorId = "${operatorId}";

        var memberName = "<fmt:message key='aimir.membername'/>";
        var groupTypeFstValue = "${groupTypeFstValue}";

        var permitLocationId = "${permitLocationId}";  // location 제한
        var numberFormat = "${numberFormat}";

        var allStr;
        
        $(function() {
        	locationTreeGoGo('treeDivMcu', 'searchWordMcu', 'sLocationIdMcu');
        	locationTreeGoGo('treeDivLoc', 'searchWordLoc', 'sLocationIdLoc');
        	locationTreeGoGo('treeDivMe', 'searchWordMe', 'sLocationIdMe');
	        locationTreeGoGo('treeDivMo', 'searchWordMo', 'sLocationIdMo');
	        locationTreeGoGo('treeDivB', 'locationBText', 'locationB');
	        sicTreeGoGo('treeDivSB', 'sicBText', 'sicIdB', null, 'sicIdsB'); 
	   });
        
        $(document).ready(function() {
        	Ext.QuickTips.init();
            
        	if (editAuth == "true") {
                $("#btnlist").show();
                $("#arrowlist").show();
            } else {
                $("#btnlist").hide();
                $("#arrowlist").hide();
            }

            $('#groupType').selectbox();
            $('#memberType').selectbox();
			
            $("#searchDcu").hide();
        	$("#searchLoc").hide();
        	$("#searchMeter").hide();
        	$("#searchModem").hide();
        	$("#searchContract").hide();
        	$("#searchEnddevice").hide();
        	//DCU
        	$("#sMcuType").selectbox();
            $("#protocol").selectbox();
            $("#filter").selectbox();
            $("#order").selectbox();
            $("#deviceModelI").selectbox();
            $("#locationI").selectbox();
            $("#mcuTypeI").selectbox();
            $("#protocolTypeI").selectbox();
            $("#sMcuStatus").selectbox();
        	//Meter
    		$('#sMeterType').selectbox();
            $('#sMeterGroup').selectbox();
            $('#sStatus').selectbox();
            $('#sModemYN').selectbox();
            $('#sCustomerYN').selectbox();
            $('#sModel').selectbox();
            $('#sVendor').selectbox();
            //Modem
            $('#sModemType').selectbox();
       	    $('#sInstallState').selectbox();
        	$('#mMcuType').selectbox();
        	$('#sModemStatus').selectbox();
        	//Contract
        	allStr = "<fmt:message key="aimir.all"/>";
        	
        	var locDateFormat = "yymmdd";

            $("#sInstallDateStart")         .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#sInstallDateEnd")           .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        
            $("#sInstallStartDate")         .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#sInstallEndDate")           .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        
            $("#sLastcommStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#sLastcommEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        	
            $("#sInstallStart")         .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#sInstallEnd")           .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        
            $("#sLastcommStart")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
            $("#sLastcommEnd")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        	
            $("#startDateB").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
            $("#endDateB").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});

            
            setSelectBox();
            getGridComboData();
            getGroupList();
            resetMemberSelectDatas();
            hide();
            
        });
		
        function setSelectBox() {

            $.getJSON('${ctx}/gadget/system/customerMax.do?param=customerMaxSelectBox',
                function(json) {

                    $('#tariffIndexB').loadSelect(json.tariffTypeEM);
                    $("#tariffIndexB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                    $("#tariffIndexB").val('');
                    $("#tariffIndexB").selectbox();

                    var creditTypeData = json.creditType;
                    var creditTypeArr = Array();
                    
                    for (var i = 0; i < creditTypeData.length; i++) {
                        var obj = new Object();
                        obj.name=creditTypeData[i].descr;
                        obj.id=creditTypeData[i].id;
                        creditTypeArr[i]=obj;
                    };
                    $('#creditTypeB').loadSelect(creditTypeArr);
                    $("#creditTypeB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                    $("#creditTypeB").val('');
                    $("#creditTypeB").selectbox();

                    var statusData = json.status;
                    var statusArr = Array();
                    
                    for (var i = 0; i < statusData.length; i++) {
                        var obj = new Object();
                        obj.name=statusData[i].descr;
                        obj.id=statusData[i].id;
                        statusArr[i]=obj;
                    };
                    $('#statusB').loadSelect(statusArr);
                    $("#statusB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                    $("#statusB").val('');
                    $("#statusB").selectbox();

                    $('#drB').loadSelect(json.dr);
                    $("#drB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                    $("#drB").val('');
                    $("#drB").selectbox();
            });
        }
        function reset() {
	        // Form Reset
	       var $searchForm = $("form[name=search]");
	       $searchForm.trigger("reset");
	       
	       $("#resultCnt").html("0");
	       // 자동 초기화 안되는 요소들 직접 초기화
	       $('#sLocationIdMcu').val('');
	       $('#sLocationIdLoc').val('');
	       $('#sLocationIdMe').val('');
	       $('#sLocationIdMo').val('');
	       $('#locationB').val('');
	       $('#sicIdsB').val('');
	       $('#sInstallStartDateHidden').val('');
	       $('#sInstallEndDateHidden').val('');
	       $('#sInstallDateStartHidden').val('');
	       $('#sInstallDateEndHidden').val('');
	       $('#sLastcommStartDateHidden').val('');
	       $('#sLastcommEndDateHidden').val('');
	       $('#sInstallStartHidden').val('');
	       $('#sInstallEndHidden').val('');
	       $('#sLastcommStartHidden').val('');
	       $('#sLastcommEndHidden').val('');
	       
	       // 셀렉트 태그 첫번째 인덱스 선택
	       var $selects = $searchForm.find("select");
	       $selects.each(function() {
	           $(this).selectbox();
	       });
	       return;
        }
        
        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.onReady(function() {
            Ext.QuickTips.init();
            var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

            if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
                Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
                Ext.override(Ext.grid.ColumnModel, {
                    getTotalWidth : function(includeHidden) {
                        if (!this.totalWidth) {
                            var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                            this.totalWidth = 0;
                            for (var i = 0, len = this.config.length; i < len; i++) {
                                if (includeHidden || !this.isHidden(i)) {
                                    this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                                }
                            }
                        }
                        return this.totalWidth;
                    }
                });
                chromeColAdd = 2;
            }

            Ext.grid.EditorGridPanel.prototype.onEditComplete = function (ed, value, startValue) {
                this.editing = false;
                this.activeEditor = null;
                ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
                var r = ed.record;
                var field = this.colModel.getDataIndex(ed.col);
                // Setup the object to be passed to the validateedit event.
                var e = {
                    grid: this,
                    record: r,
                    field: field,
                    originalValue: startValue,
                    value: value,
                    row: ed.row,
                    column: ed.col,
                    cancel:false
                };
                if (this.fireEvent("validateedit", e) !== false && !e.cancel) {
                    r.set(field, value);
                    delete e.cancel;
                    this.fireEvent("afteredit", e);
                }
                this.view.focusCell(ed.row, ed.col);
            }
        });

        function memberSearch() {
            if (selectedGroupId == '') {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.alert.groupMgmt.msg17'/>");    // 먼저 그룹을 선택해 주세요.
                return;
            }
            getMemberSelectData();
        }

        function delTxt(type) {
            if( type == "group" ){
                $('#groupName').val('');
                flag = true;
            }
            else {
                $('#memberName').val('');
            }
            return;
        }

        var groupTypeComboData = new Array();
        var userAccessComboData = new Array();
        // Group Type Combo Data
        function getGridComboData() {
            //var combodata = null;
            groupTypeComboData = [
            <c:forEach var="groupType" items="${groupType}">
                {id: "${groupType.name}", name: "${groupType.name}"}<c:if test="${not status.last}">,</c:if>
            </c:forEach>
            ];

            userAccessComboData = [
                {id: "Y", name: "Y"},
                {id: "N", name: "N"}
            ];
        }
		
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
            
        function modifyDateLocal(setDate, inst) {
            var dateId = '#' + inst.id;
            var dateHiddenId = '#' + inst.id + 'Hidden';
            $(dateHiddenId).val($(dateId).val());
            $.getJSON("${ctx}/common/convertLocalDate.do", {
                dbDate : setDate,
                supplierId : supplierId
             }, function(json) {
                 $(dateId).val(json.localDate);
             });
        }
		
        /* 그룹타입별 검색조건 START */
		var supplierId = "${supplierId}";
        //DCU
		var searchList = function() {
			getMemberSelectDCU();
        };
        var getConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#sMcuId').val();            // 집중기번호
            arrayObj[1] = $('#sMcuType').val();          // 집중기유형
            arrayObj[2] = $('#sLocationIdMcu').val();       // 지역명
            arrayObj[3] = $('#sSwVersion').val();        // sw version
            arrayObj[4] = $('#sHwVersion').val();        // hw version
            arrayObj[5] = $('#sInstallDateStartHidden').val(); // 설치일
            arrayObj[6] = $('#sInstallDateEndHidden').val();   // 설치일

            //arrayObj[8] = $('#filter').val();
            //arrayObj[9] = $('#order').val();
            arrayObj[10] = $('#protocol').val();

            //캐쉬로 인한 쓰레기 더미 파라미터
            //arrayObj[11] = Math.random();

            //날짜포맷 위한 supplierId
            //arrayObj[12] = supplierId;
            
            arrayObj[13] = $('#sMcuStatus').val();        // mcuStatus;
            arrayObj[14] = $('#sMcuSerial').val();        //mcuSerial
            return arrayObj;
        };  
       // Meter
       function getDeviceVendorsBySupplierId() {
    	$.getJSON('${ctx}/gadget/system/vendorlist.do', {
    	    'supplierId' : supplierId
    	}, function(returnData) {
    	   $('#sVendor').loadSelect(returnData.deviceVendors);
    	   $('#sVendor').selectbox();
    	   });
    	};
       function getMeterGroupBygroupId() {
        $.getJSON('${ctx}/gadget/system/getMeterGroupBygroupId.do', {
            'supplierId' : supplierId,
            'groupType' : 'Meter'
        }, function(returnData) {
            $('#sMeterGroup').loadSelect(returnData.NAME);
            $('#sMeterGroup').selectbox();
        	});
    	};
        function getDeviceModelsByVenendorId() {
        if ($('#sVendor').val() != "")
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do', {
                'vendorId' : $('#sVendor').val()
            }, function(returnData) {
                $('#sModel').noneSelect(returnData.deviceModels);
                $('#sModel').selectbox();
            });
    	};
    	
		function getCondition() {
	        var arrayObj = Array();
	
	        arrayObj[0] = $('#sMeterType').val();
	        arrayObj[1] = $('#sMdsId').val();
	        arrayObj[2] = $('#sStatus').val();
	
	        arrayObj[3] = $('#sMcuName').val();
	        arrayObj[4] = $('#sLocationIdMe').val();
	        arrayObj[5] = $('#sConsumLocationId').val();
	        arrayObj[6] = $('#sVendor').val();
	        arrayObj[7] = $('#sModel').val();
	
	        arrayObj[8] = $('#sInstallStartDateHidden').val();
	        arrayObj[9] = $('#sInstallEndDateHidden').val();
	
	        arrayObj[10] = $('#sModemYN').val();
	        arrayObj[11] = $('#sCustomerYN').val();
	        arrayObj[12] = $('#sLastcommStartDateHidden').val();
	        arrayObj[13] = $('#sLastcommEndDateHidden').val();
	        arrayObj[14] = $('#sOrder').val();
	        arrayObj[15] = $('#sCommState').val();
	
	        arrayObj[16] = supplierId;
	
	        arrayObj[17] = $('#sMeterGroup').val();
	
	        arrayObj[18] = $('#sCustomerId').val();
	        arrayObj[19] = $('#sCustomerName').val();
	
	        arrayObj[20] = permitLocationId;
	        arrayObj[21] = $('#sMeterAddress').val();
	        arrayObj[22] = $('#sGs1').val();
	
	        return arrayObj;
   		 }
		//Modem
		 function getConditionModem() {
	        var arrayObj = Array();
	
	        arrayObj[0]  = $('#sModemType').val();
	        arrayObj[1]  = $('#sModemId').val();
	
	        arrayObj[4]  = $('#sMcuType').val();
	        arrayObj[5]  = $('#sMcuName').val();
	        arrayObj[6]  = $('#sModemFwVer').val();
	        arrayObj[7]  = $('#sModemSwRev').val();
	        arrayObj[8]  = $('#sModemHwVer').val();
	
	        arrayObj[9]  = $('#sInstallStartHidden') .val();
	        arrayObj[10] = $('#sInstallEndHidden')   .val();
	
	        arrayObj[11] = $('#sLastcommStartHidden') .val();
	        arrayObj[12] = $('#sLastcommEndHidden')   .val();
	        arrayObj[13] = $('#sLocationIdMo').val();
	
	        arrayObj[14] = $('#sOrder').val();
	        arrayObj[15] = $('#sCommState').val();
	
	        arrayObj[16] = supplierId;
	        arrayObj[17] = $('#sModemStatus').val();
			arrayObj[18] = $('#sMeterSerial').val();
			
	        return arrayObj;
    	}
		//Contract
		function getConditionContract() {
			var conditionArray = Array();
			 conditionArray[0] = $('#customerNoB').val();
	         conditionArray[1] = $('#customerNameB').val();
	         conditionArray[2] = $('#locationB').val();
	         conditionArray[3] = $('#tariffIndexB').val();
	         conditionArray[4] = $('#contractDemandB').val();
	         conditionArray[5] = $('#creditTypeB').val();
	         conditionArray[6] = $('#mdsIdB').val();
	         conditionArray[7] = $('#statusB').val();
	         conditionArray[8] = $('#drB').val();
	         conditionArray[9] = $('#sicIdsB').val();
	         conditionArray[10] = $('#startDateB').val();
	         conditionArray[11] = $('#endDateB').val();
	         conditionArray[12] = '';                 // 주소
	         conditionArray[13] = '';                 // 공급타입
	         conditionArray[15] = $('#contractNumberB').val();
	         
	         return conditionArray;
		}
		
		function init() {
	        // 검색 > 공급사의 제조사 조회
	        getDeviceVendorsBySupplierId();
	        // 검색 > 미터그룹 리스트
	        getMeterGroupBygroupId();
	
	        // 상세조회 > 일반 초기화
	        //initMeterDetail();
	
	        //hide();
    	}
		
        var selectedGroupId = null;
        var selectedGroupType = null;
        var selectedGroupName = null;

        /* Group 리스트 START */
        var groupGridOn = false;
        var groupGrid;
        var groupStore;
        var groupColModel;
        var groupCheckSelModel;
        var getGroupList = function() {
            var width = $("#groupGridDiv").width();
            selectedGroupId = null;
            selectedGroupType = null;
            selectedGroupName = null;

            emergePre();
            groupStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/system/getGroupListNotHomeGroupIHD.do",
                baseParams: {
                	supplierId : supplierId,
                    operatorId : operatorId,
                    groupType : $("#groupType").val(),
                    groupName : $("#groupName").val()
                },
                root:'result',
                fields: ["groupId", "groupName", "groupType", "allUserAccess", "memCount", "status", 'mobileNo'],
                listeners: {
                    load: function(store, record, options){
                        var len = store.getCount();
                        if (len > 0) {
                            // reload 후 해당 row 자동 선택
                            if (selectedGroupName != null) {
                                for (var i = 0; i < len; i++) {
                                    if (selectedGroupName == store.getAt(i).get("groupName")) {
                                        groupGrid.getSelectionModel().selectRow(i);
                                        selectedGroupName = null;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            var colWidth = width/4 - chromeColAdd;

            Ext.util.Format.comboRenderer = function(combo){
                return function(value){
                    var record = combo.findRecord(combo.valueField, value);
                    return record ? record.get(combo.displayField) : combo.valueNotFoundText;
                };
            };

            // create the combo instance
            var groupTypeCombo = new Ext.form.ComboBox({
                id : "grpType",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : colWidth+1,
                store : new Ext.data.JsonStore({
                    id : 0,
                    data : groupTypeComboData,
                    fields : ["id", "name"]
                }),
                valueField : "id",
                displayField : "name",
                editable : false

            });

            var userAccessCombo = new Ext.form.ComboBox({
                id : "userAccess",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : colWidth+1,
                store : new Ext.data.JsonStore({
                    id : 1,
                    data : userAccessComboData,
                    fields : ["id", "name"]
                }),
                valueField : "id",
                displayField : "name",
                editable : false

            });

            var headerGroupName = "<fmt:message key="aimir.group.name"/>";
            var headerGroupType = "<fmt:message key="aimir.grouptype"/>";
            var headerUserAccess = "<fmt:message key="aimir.allUserAccess"/>";
            var headerMemberCount = "<fmt:message key="aimir.group.membercount"/>";
            var headerMobileNo = "<fmt:message key="aimir.celluarphone"/>";
            var edit = true;

            if (editAuth != "true") {
                edit = false;
            }

            groupColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: headerGroupName, dataIndex: 'groupName', width: colWidth, editable: edit,
                        editor: new Ext.form.TextField({
                            id : 'grpName',
                            allowBlank : false
                        })
                    }
                   ,{header: headerGroupType, dataIndex: 'groupType', width: colWidth, editable: edit,
                        editor: groupTypeCombo, renderer: Ext.util.Format.comboRenderer(groupTypeCombo)
                    }
                   ,{header: headerUserAccess, dataIndex: 'allUserAccess', width: colWidth, align: "center", editable: edit,
                        editor: userAccessCombo, renderer: Ext.util.Format.comboRenderer(userAccessCombo)
                    }
                   ,{header: headerMemberCount, dataIndex: 'memCount', width: colWidth-26, align:'right'}
                   ,{header: headerMobileNo, dataIndex: 'mobileNo', width: colWidth-26, editable: edit,
                       editor: new Ext.form.TextField({
                           id : 'grpmobileNo',
                           allowBlank : true
                       })}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
               }
            });

            if (groupGridOn == false) {
                groupGrid = new Ext.grid.EditorGridPanel({
                    store : groupStore,
                    colModel : groupColModel,
                    sm : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        moveEditorOnEnter : false,
                        listeners : {
                            rowselect : function(sm, row, rec) {
                                selectedGroupId = rec.get("groupId");
                                selectedGroupType = rec.get("groupType");
                                $("#memberName").val(memberName);
                                if (selectedGroupId != "") {
                                    getMemberSelectedData();
                                    getMemberSelectData();
                                } else {
                                    resetMemberSelectDatas();
                                }
                            }
                        }
                    }),
                    autoScroll : true,
                    scroll : true,
                    width : width,
                    height : 306,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    clicksToEdit : 1,
                    renderTo : 'groupGridDiv',
                    viewConfig : {
                        forceFit : true,
                        markDirty: false,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    listeners : {
                        beforeedit : function(e) {
                            var record = e.record;
                            if (e.field == "groupName") {
                                if (record.get("groupId") != "" && record.get("memCount") != "" && record.get("memCount") > 0 && record.get("status") != "copy" ) {
                                    return false;
                                }
                            } else if (e.field == "groupType") {
                                if (record.get("memCount") != "" && record.get("memCount") > 0) {
                                    return false;
                                }
                            }
                            return true;
                        },
                        afteredit : function(e) {
                            var record = e.record;
                            var grid = e.grid;
                            var field = e.field;
                            var value = e.value;
                            var originalValue = e.originalValue;

                            if (record.get("groupName") == "") {
                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg4"/>",
                                        function() {
                                    grid.startEditing(e.row, 0);
                                });
                            } else {
                                if (record.get("status") == "copy") {
                                    copyGroup(grid, record, field, value, e.row);
                                } else if (value != originalValue) {
                                    saveGroup(grid, record, field, value, e.row);
                                }
                            }
                        }
                    }
                });
                groupGridOn = true;
            } else {
                groupGrid.setWidth(width);
                groupGrid.reconfigure(groupStore, groupColModel);
            }
            hide();
        };

        function addRow() {
            var store = groupGrid.getStore();
            var Plant = store.recordType;
            var p = new Plant({
                groupId : "",
                groupName : "",
                groupType : groupTypeFstValue,
                allUserAccess : "Y",
                memCount : "",
                mobileNo : "",
                status : "add"
            });
            var length = store.getCount();
            groupGrid.stopEditing();
            groupStore.insert(length, p);
            groupGrid.startEditing(length, 0);
            groupGrid.getSelectionModel().selectLastRow();
        }

        function copyRow() {
            var record = groupGrid.getSelectionModel().getSelected();

            if (record == null) {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg5"/>");
                return;
            }

            var store = groupGrid.getStore();
            var Plant = store.recordType;
            var p = new Plant({
                groupId : record.get("groupId"),
                groupName : record.get("groupName"),
                groupType : record.get("groupType"),
                allUserAccess : record.get("allUserAccess"),
                memCount : record.get("memCount"),
                mobileNo : record.get("mobileNo"),
                status : "copy"
            });
            var length = store.getCount();
            groupGrid.stopEditing();
            groupStore.insert(length, p);
            groupGrid.startEditing(length, 0);
            groupGrid.getSelectionModel().selectLastRow();
        }

        // Group 삭제
        function deleteRow() {
            var record = groupGrid.getSelectionModel().getSelected();

            if (record == null) {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg6"/>");
                return;
            }

            var status = record.get("status");

            if (status == "add" || status == "copy") {
                groupStore.remove(record);
            } else {
                emergePre();
                // Member 가 있는 경우 삭제안됨
                if (record.get("memCount") != "" && record.get("memCount") > 0) {
                    Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg7"/>");
                    hide();
                    return;
                }

                Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>',
                        function(btn) {
                            if (btn == 'yes') {
                                $.getJSON("${ctx}/gadget/system/deleteGroup.do"
                                        ,{groupId : record.get("groupId")}
                                        ,function(json) {
                                            hide();
                                            if (json.result == "success") {
                                                resetMemberSelectDatas();
                                                groupStore.reload();
                                            } else {
                                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
                                            }
                                });
                                //groupStore.reload();
                                //groupGrid.reconfigure(groupStore, groupColModel);
                                //getGroupList();
                                
                            } else if(btn == 'no'){
                            	hide();
                            }
                        });
            }
                
        }

        // Group Name 중복확인
        function dupCheckGroupName(grid, groupName, row) {
         	var store = grid.getStore();

        	var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/system/dupCheckGroupName.do",
                data: {operatorId : operatorId,
                    groupName : groupName
                },
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);
            
            hide();
            if(result.result == "Y") {
            	Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.duplicateGroupName"/>",
                        function() {
                    grid.startEditing(row, 0);
                });
                hide();
                return false;
            } else {
            	return true;
            } 
        }

        //mobile format확인
        function mobileFormat(grid, mobileNo, row) {
        	var digitPattern = /^[0-9]+$/;
            if ((mobileNo !="" || mobileNo.length != 0) && !(digitPattern.test(mobileNo))) {
                    Ext.Msg.alert("<fmt:message key="aimir.message"/>","<fmt:message key="aimir.group.mobileNo"/>",
                        function() {
                            grid.startEditing(row, 4);
                        }); 
                    hide();
                    return false;
            }
            return true;
        }

        // Group 저장
        function saveGroup(grid, record, field, value, row) {
            emergePre();

            var store = grid.getStore();

            if (field == "groupName" && !dupCheckGroupName(grid, record.get("groupName"), row)) {
                return;
            }

            if(record.get("mobileNo") != null) {
                if(!mobileFormat(grid, record.get("mobileNo"), row)) {
                    return;
                }
            }

            $.getJSON("${ctx}/gadget/system/saveGroup.do"
                    ,{operatorId : operatorId,
                      groupId : record.get("groupId"),
                      groupName : record.get("groupName"),
                      groupType : record.get("groupType"),
                      allUserAccess : record.get("allUserAccess"),
                      mobileNo : "" == record.get("mobileNo") ? null : record.get("mobileNo"),
                      supplierId : supplierId
                      }
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            selectedGroupName = record.get("groupName");
                            store.reload();
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
            });
        }

        // Group 복사
        function copyGroup(grid, record, field, value, row) {
            emergePre();

            var store = grid.getStore();
            if (!dupCheckGroupName(grid, record.get("groupName"), row)) {
                return;
            }

            $.getJSON("${ctx}/gadget/system/copyGroup.do"
                    ,{operatorId : operatorId,
                      groupId : record.get("groupId"),
                      groupName : record.get("groupName"),
                      groupType : record.get("groupType"),
                      allUserAccess : record.get("allUserAccess"),
                      mobileNo : "" == record.get("mobileNo") ? null : record.get("mobileNo"),
                      supplierId : supplierId
                      }
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            selectedGroupName = record.get("groupName");
                            store.reload();
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
            });
        }

        function memberSearchKeyEvent(event) {
            var evKeyup = null;
            if (event)
                // firefox
                evKeyup = event;
            else
                // explorer
                evKeyup = window.event;

            if (evKeyup.keyCode == 13) {
                getMemberSelectData();
            }
        }
		
        function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {
    		var chkHtml = "<div class=\"am_button\" style=\"background:none\">" +
                    "<input type=\"checkbox\" id=\"chkMember\" name=\"chkMember\" value=\"" +
                    record.data +"\" /></div>";
            return chkHtml;
    	}
     	
     	function chkAll() {
     		if ($("#allCheck").is(':checked')) {
     			$("input[name='chkMember']").attr("checked", "checked");
     		} else {
     		    $("input[name='chkMember']").attr("checked", false);
     		}
     	}
     	
        var memberSelectData;
        var memberSelectStore;
        var memberSelectForm;
		var memberSelectModel;
		var memberSelectOn = false;
		var groupMgmtSelModel; // MultiSelect
        // Member select
        // DCU
        function getMemberSelectDCU() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            var conditionArray = getConditionArray();
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectDataDcu.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    supplierId : supplierId,
                    //supplierId : conditionArray[12],
                    mcuId : conditionArray[0],
                    mcuType : conditionArray[1],
                    locationId : conditionArray[2],
                    swVersion : conditionArray[3],
                    hwVersion : conditionArray[4],
                    installDateStart : conditionArray[5],
                    installDateEnd : conditionArray[6],
                    //filter : conditionArray[8],
                    //order : conditionArray[9],
                    protocol : conditionArray[10],
                    //dummy : conditionArray[11],
                	mcuStatus : conditionArray[13],
                	mcuSerial : conditionArray[14] // sMcuSerial
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text", "type", "name"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    }
                }
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    menuDisabled: true
                },
				columns : [
		            groupMgmtSelModel,
		            {
						header : "DCU ID",
						dataIndex : 'text',
						width : 295,
						sortable: true,
					},{
						header : 'Location',
						dataIndex : 'name',
						width : 295,
						sortable: true,
					}
				]
            });
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel, 
					stripeRows : true,
					columnLines : true,
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        // Location
        function getMemberSelectLoc() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectData.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    locationId        : $("#sLocationIdLoc").val(),
                    supplierId : supplierId
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text", "type"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    },
                }
               
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    width: 560,
                    menuDisabled: true
                },
				columns : [
					groupMgmtSelModel,
					{
						header : 'Location',
						dataIndex : 'text',
						width : 590,
						sortable: true,
					}
				]
            });
            
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel,
					stripeRows : true,
					columnLines : true,
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        // Meter
        function getMemberSelectMeter() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            var condition = getCondition();
           
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectDataMeter.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    supplierId : supplierId,
                    sMeterType         : condition[0],
                    sMdsId             : condition[1],
                    sStatus            : condition[2],
                    sMcuName           : condition[3],
                    sLocationId        : condition[4],
                    sConsumLocationId  : condition[5],
                    sVendor            : condition[6],
                    sModel             : condition[7],
                    sInstallStartDate  : condition[8],
                    sInstallEndDate    : condition[9],
                    sModemYN           : condition[10],
                    sCustomerYN        : condition[11],
                    sLastcommStartDate : condition[12],
                    sLastcommEndDate   : condition[13],
                    sOrder             : condition[14],
                    sCommState         : condition[15],
                    //supplierId         : condition[16],
                    sMeterGroup        : condition[17],
                    sGroupOndemandYN   : 'N',
                    sCustomerId        : condition[18],
                    sCustomerName      : condition[19],
                    sPermitLocationId  : condition[20],
                    sMeterAddress      : condition[21],
                    sHwVersion         : "",
                    sFwVersion         : "",
                    sGs1         : condition[22],
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text", "type", "locName"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    }, 
                }
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    menuDisabled: true
                },
				columns : [
					groupMgmtSelModel,
					{
						header : "Meter Serial",
						dataIndex : 'text',
						width : 295,
						sortable: true,
					},{
						header : 'Location',
						dataIndex : 'locName',
						width : 295,
						sortable: true,
					}
				]
            });
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel,
					stripeRows : true,
					columnLines : true,
					loadMask : {
	                    msg : 'loading...'
	                },
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        //Modem
        function getMemberSelectModem() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            var condArray = getConditionModem();
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectDataModem.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    supplierId : supplierId,
                    sModemType:condArray[0]
	                ,sModemId:condArray[1]
	                ,sInstallState:condArray[3]
	                ,sMcuType:condArray[4]
	                ,sMcuName:condArray[5]
	                ,sModemFwVer:condArray[6]
	                ,sModemSwRev:condArray[7]
	                ,sModemHwVer:condArray[8]
	            	,sModomStatus:condArray[17]
	                ,sInstallStartDate:condArray[9]
	                ,sInstallEndDate:condArray[10]
	                ,sLastcommStartDate:condArray[11]
	                ,sLastcommEndDate:condArray[12]
	                ,sLocationId:condArray[13]
	                ,sOrder:condArray[14]
	                ,sCommState:condArray[15]
	                //,supplierId:condArray[16]
	            	,sMeterSerial:condArray[18] 
                	,pageSize :"20"
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["name", "text", "type", "value"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    }
                }
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    menuDisabled: true
                },
				columns : [
					groupMgmtSelModel,
					{
						header : "Modem Serial",
						dataIndex : 'text',
						width : 295,
						sortable: true,
					},{
						header : 'Location',
						dataIndex : 'name',
						width : 295,
						sortable: true,
					}
				]
            });
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel,
					stripeRows : true,
					columnLines : true,
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        //Contract
        function getMemberSelectCon() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            var conditionArray = getConditionContract();
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectDataContract.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    //locationId        : $("#sLocationId").val(),
                    supplierId : supplierId,
                    contractNumber: conditionArray[15],
                    customerNo : conditionArray[0],
                    customerName : conditionArray[1],
                    location : conditionArray[2],
                    tariffIndex : conditionArray[3],
                    contractDemand : conditionArray[4],
                    creditType : conditionArray[5],
                    mdsId : conditionArray[6],
                    status : conditionArray[7],
                    dr : conditionArray[8],
                    sicIds : conditionArray[9],
                    startDate : conditionArray[10],
                    endDate : conditionArray[11],
                    address : conditionArray[12],
                    serviceType : conditionArray[13],
                    serviceTypeTab : conditionArray[14]
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text", "type"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    },
                }
               
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    width: 560,
                    menuDisabled: true
                },
				columns : [
					groupMgmtSelModel,
					{
						header : 'Contract',
						dataIndex : 'text',
						width : 590,
						sortable: true,
					}
				]
            });
            
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel,
					stripeRows : true,
					columnLines : true,
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        //EndDevice
        function getMemberSelectEnd() {
            var width = $("#memberSelectDiv").width();
            var pageSize = 20;
            memberSelectStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectData.do",
                baseParams:{
                	groupId : selectedGroupId == null ? undefined : selectedGroupId,
                    groupType : selectedGroupType == null ? undefined : selectedGroupType,
                    memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                    locationId        : $("#sLocationId").val(),
                    supplierId : supplierId
                },
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text", "type"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    },load: function(store, record, options){
                        var len = store.totalLength;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                    },
                }
               
            });
            
            if (memberSelectOn == false){
            	groupMgmtSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    width: 560,
                    menuDisabled: true
                },
				columns : [
					groupMgmtSelModel,
					{
						header : 'EndDevice',
						dataIndex : 'text',
						width : 570,
						sortable: true,
					}
				]
            });
            
            if(memberSelectOn == false){
	            memberSelectForm = new Ext.grid.GridPanel({
					height : 400,
	            	store : memberSelectStore,
					colModel : memberSelectModel,
					selModel : groupMgmtSelModel,
					stripeRows : true,
					columnLines : true,
					renderTo: 'memberSelectDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectOn = true;
            }else{
            	memberSelectForm.setWidth(width);
            	var bottomToolbar = memberSelectForm.getBottomToolbar();
            	memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
            	bottomToolbar.bindStore(memberSelectStore);
        	}
        }
        var memberSelectedOn = false;
        var memberSelectedData;
        var memberSelectedStore;
        var memberSelectedForm;
		var groupMgmtSelectedModel;
        // Member selected
        function getMemberSelected() {
            var width = $("#memberSelectedDiv").width();
            var pageSize = 20;
            
            memberSelectedStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/getMemberSelectedData.do",
                baseParams:{
                	groupId : selectedGroupId,
                },
                //data: memberSelectedData,
                totalProperty: 'totalCnt',
                root: 'result',
                fields: ["value", "text"],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) 
                        });
                    }
                }, 
            });
		
            if (memberSelectedOn == false){
            	groupMgmtSelectedModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'value'
                });
            }
            
            var colWidth = (width - 50)/5;
            memberSelectedModel = new Ext.grid.ColumnModel({
            	defaults: {
                    sortable: true,
                    width: colWidth
                },
				columns : [
					groupMgmtSelectedModel,
					{
						header : 'Members',
						dataIndex : 'text',
						width : 590
					}
				]
            });
            if(memberSelectedOn == false){
	            memberSelectedForm = new Ext.grid.GridPanel({
	            	height : 400,
	            	store : memberSelectedStore,
					colModel : memberSelectedModel,
					selModel : groupMgmtSelectedModel,
					stripeRows : true,
					columnLines : true,
					loadMask : {
	                    msg : 'loading...'
	                },
					renderTo: 'memberSelectedDiv',	
	                bbar : new Ext.PagingToolbar({
	                   pageSize: pageSize,
	                   store: memberSelectedStore,
	                   displayInfo: true,
	                   displayMsg: ' {0} - {1} / {2}'
	               })
	            });
	            memberSelectedOn = true;
            }else{
            	memberSelectedForm.setWidth(width);
            	var bottomToolbar = memberSelectedForm.getBottomToolbar();
            	memberSelectedForm.reconfigure(memberSelectedStore, memberSelectedModel);
            	bottomToolbar.bindStore(memberSelectedStore);
            }
        }

        function resetMemberSelectDatas() {
            /* memberSelectData = new Array();
            memberSelectedData = new Array();
            if (memberSelectedForm != null) {
                memberSelectedForm.destroy();
            }
            if (memberSelectForm != null) {
                memberSelectForm.destroy();
            } */
            $("#searchDcu").hide();
        	$("#searchLoc").hide();
        	$("#searchMeter").hide();
        	$("#searchModem").hide();
        	$("#searchContract").hide();
        	$("#searchEnddevice").hide();
            $("#resultCnt").html("0");
            //getMemberSelect();
            //getMemberSelected();
        }

        function getMemberSelectData() {
          /*   $.post("${ctx}/gadget/system/getMemberSelectData.do"
                    ,{groupId : selectedGroupId == null ? undefined : selectedGroupId,
                      groupType : selectedGroupType == null ? undefined : selectedGroupType,
                      memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                      supplierId : supplierId, limit : 5, page : 5
                      }
                    ,function(json) {
                        memberSelectData = json.result;
                        if (memberSelectForm != null) {
                            memberSelectForm.destroy();
                        }
                        var len = memberSelectData.length;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat)); */
                        reset();   
                        if(selectedGroupType=="DCU"){
                        	getMemberSelectDCU();
                        	$("#searchDcu").show();
                        	$("#searchLoc").hide();
                        	$("#searchContract").hide();
                        	$("#searchEnddevice").hide();
                        	$("#searchMeter").hide();
                        	$("#searchModem").hide();
                        }else if(selectedGroupType=="Meter"){
                        	getMemberSelectMeter();
                        	$("#searchDcu").hide();
                        	$("#searchLoc").hide();
                        	$("#searchContract").hide();
                        	$("#searchEnddevice").hide();
                        	$("#searchMeter").show();
                        	$("#searchModem").hide();
                        }else if(selectedGroupType=="Modem"){
                        	getMemberSelectModem();
                        	$("#searchDcu").hide();
                        	$("#searchLoc").hide();
                        	$("#searchContract").hide();
                        	$("#searchEnddevice").hide();
                        	$("#searchMeter").hide();
                        	$("#searchModem").show();
                        }else if(selectedGroupType=="Location"){
                        	getMemberSelectLoc();
                        	$("#searchDcu").hide();
                        	$("#searchLoc").show();
                        	$("#searchContract").hide();
                        	$("#searchEnddevice").hide();
                        	$("#searchMeter").hide();
                        	$("#searchModem").hide();
                        }else if(selectedGroupType=="Contract"){
                        	getMemberSelectCon();
                        	$("#searchDcu").hide();
                        	$("#searchLoc").hide();
                        	$("#searchContract").show();
                        	$("#searchEnddevice").hide();
                        	$("#searchMeter").hide();
                        	$("#searchModem").hide();
                        }else if(selectedGroupType=="EndDevice"){
                        	getMemberSelectEnd();
                        	$("#searchDcu").hide();
                        	$("#searchLoc").hide();
                        	$("#searchContract").hide();
                        	$("#searchEnddevice").show();
                        	$("#searchMeter").hide();
                        	$("#searchModem").hide();
                        }
                        /*   }); */
        }

        function getMemberSelectedData() {
            /* $.getJSON("${ctx}/gadget/system/getMemberSelectedData.do"
                    ,{groupId : selectedGroupId}
                    ,function(json) {
                        memberSelectedData = json.result;
                        if (memberSelectedForm != null) {
                            memberSelectedForm.destroy();
                        } */
                        getMemberSelected();
            //});
        }

        function memberAdd() {
            //var members = memberSelectForm.selModel.selections.items[0].data.text;
            var members = "";
            var memberCnt = memberSelectForm.selModel.selections.length;
            for( var i=0; i <memberCnt; i++){
            	members += memberSelectForm.selModel.selections.items[i].data.text +",";
            }
            if (members == null || members == "") {
                return;
            } 
            emergePre();

            $.post("${ctx}/gadget/system/addGroupMembers.do"
                    ,{groupId : selectedGroupId,
                      members : members}
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            //memberSelectForm.getForm().reset();
                            memberSelectForm.reconfigure(memberSelectStore, memberSelectModel);
                            var record = groupGrid.getSelectionModel().getSelected();
                            var memCount = Number(record.get("memCount"));
                            var memberArr = members.split(",");
                            var memberLen = memberArr.length;
                            var selectLen = memberSelectStore.getCount();
                            var selectVal = null;

                            for (var i = 0; i < selectLen; i++) {
                                selectVal = memberSelectStore.getAt(i).get("text");

                                for (var j = 0; j < memberLen; j++) {
                                    if (selectVal == memberArr[j]) {
                                        memberSelectStore.remove(memberSelectStore.getAt(i));
                                        selectLen--;
                                        i--;
                                        memCount++;
                                        break;
                                    }
                                }
                            }
                            //$("#resultCnt").html(Ext.util.Format.number(selectLen,numberFormat));
                            record.set("memCount", memCount);
                            getMemberSelectData();
                            getMemberSelectedData();
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
            });
        }

        function memberDel() {
            //var memberIds = memberSelectedForm.selModel.selections.items[0].data.value.toString();
            var memberIds = "";
            var memberCnt = memberSelectedForm.selModel.selections.length;
            for( var i=0; i <memberCnt; i++){
            	memberIds += memberSelectedForm.selModel.selections.items[i].data.value +",";
            }
            if (memberIds == null || memberIds == "") {
                return;
            }
            emergePre();

            $.getJSON("${ctx}/gadget/system/removeGroupMembers.do"
                    ,{memberIds : memberIds}
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            //memberSelectedForm.getForm().reset();
                            memberSelectedForm.reconfigure(memberSelectedStore, memberSelectedModel);
                            var record = groupGrid.getSelectionModel().getSelected();
                            var memCount = Number(record.get("memCount"));
                            var memberArr = memberIds.split(",");
                            var memberLen = memberArr.length;
                            var selectedLen = memberSelectedStore.getCount();
                            var selectedVal = null;

                            for (var i = 0; i < selectedLen; i++) {
                                selectedVal = memberSelectedStore.getAt(i).get("value");

                                for (var j = 0; j < memberLen; j++) {
                                    if (selectedVal == memberArr[j]) {
                                        memberSelectedStore.remove(memberSelectedStore.getAt(i));
                                        selectedLen--;
                                        i--;
                                        memCount--;
                                        break;
                                    }
                                }
                            }
                            record.set("memCount", memCount);
                            getMemberSelectData();
                            getMemberSelectedData();
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
                        }
            });
        }

    </script>
</head>
<body onLoad="init();">
    <!-- search-background DIV (S) -->
    <div class="search-bg-basic">

        <!--검색조건-->
        <div class="searchoption-container" style="margin-top: 5px;">
            <table class="searchoption wfree">
                <tr>
                    <td class="gray11pt withinput"><fmt:message key="aimir.grouptype"/></td>
                    <td>
                        <select id="groupType" style="width:120px">
                            <option value=""><fmt:message key="aimir.all"/></option>
                            <c:forEach var="groupType" items="${groupType}">
                            <option value="${groupType.name}">${groupType.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td class="space20"></td>

                    <td class="withinput"><fmt:message key="aimir.group.name"/></td>
                    <td><input id="groupName" type="text" style="width: 200px;"></td>
                    <td class="space20"></td>

                    <td>
                        <div id="btn">
                            <ul><li><a href="javascript:getGroupList();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <!--검색조건 끝-->

    </div>
    <!-- search-background DIV (E) -->

    <div id="btnlist">
        <div id="btn" class="btn_right_top2 margin-t10px">
            <ul><li><a href="javascript:addRow();" class="on-bold"><fmt:message key="aimir.button.addGroup" /></a></li></ul>
            <ul><li><a href="javascript:copyRow();" class="on"><fmt:message key="aimir.button.copy" /></a></li></ul>
            <ul><li><a href="javascript:deleteRow();" class="on"><fmt:message key="aimir.button.delete" /></a></li></ul>
        </div>
    </div>

    <div class="gadget_body">
        <div id="groupGridDiv"></div>
    </div>



    <!-- 그룹멤버추가 전체 (S) -->
    <div class="gadget_body">
    
        <!-- Step 02b - 멤버추가 (S) -->
        <div class="groupmanage-create-tab blueline bg-blue clear" style="border-bottom:none !important;">
            <label class="member bluebold11pt"><fmt:message key="aimir.memberAdd"/></label>
        </div>
    
        <div class="groupmanage-create-member blueline bg-blue clear" style="height: 580px !important;">
            <ul class="width">
            <li class="padding minustop">
    		<!-- 그룹타입별 검색조건 -->
                <div class="blueline-searchoption">
                <!-- DCU 검색조건 -->
                	<div id="searchDcu">
		            <form name="search">
						<div class="wfree border-bottom padding20px">
							<table class="searching" >
								<tr>
									<td class="withinput" style="width: 65px"><fmt:message key="aimir.mcutype" /></td>
									<td class="padding-r20px">
									<select id="sMcuType" name="select" style="width: 100px;">
											<option value=""><fmt:message key="aimir.all" /></option>
											<c:forEach var="mcuType" items="${mcuTypeMap}">
												<option value="${mcuType.id}">${mcuType.descr}</option>
											</c:forEach>
									</select></td>
									<td class="withinput" style="width: 80px"><fmt:message
											key="aimir.mcuid" /></td>
									<td><input name="customer_num" id="sMcuId" type="text"
										style="width: 140px;"></td>
									<td class="withinput" style="width: 80px"><fmt:message key="aimir.dcuSerial" /></td>
									<td class="padding-r20px"><input name="serial_num" id="sMcuSerial" type="text" style="width: 140px;"></td>
									<td class="withinput"><fmt:message key="aimir.sw.version" /></td>
									<td class="padding-r20px"><input id="sSwVersion" type="text" style="width: 100px;"></td>
									<td class="withinput"><fmt:message key="aimir.fw.hwversion" /></td>
									<td class="padding-r20px"><input id="sHwVersion" type="text" style="width: 100px;"></td>
									<td class="withinput"><fmt:message key="aimir.installationdate" /></td>
									<td id="search-date"><input id="sInstallDateStart" name="customer_num" type="text" class="day"></td>
									<td><input class="between" value="~" type="text"></td>
									<td id="search-date"><input id="sInstallDateEnd" name="customer_num" type="text" class="day"><input id="sInstallDateStartHidden" type="hidden" /> <input id="sInstallDateEndHidden" type="hidden" /></td>
								</tr>
								<tr>
									<td class="withinput" style="width: 80px"><fmt:message key="aimir.view.mcu39" /> <!-- 프로토콜 타입 --></td>
									<td class="padding-r20px"><select id="protocol" name="select" style="width: 100px;">
											<option value=""><fmt:message key="aimir.all" /></option>
											<c:forEach var="protocol" items="${protocols}">
												<option value="${protocol.id}">${protocol.descr}</option>
											</c:forEach>
									</select>
									</td>
									<td class="withinput"><fmt:message key="aimir.location" /></td>
									<td class="padding-r20px"><input type="text" id="searchWordMcu" name="searchWordMcu" style="width: 140px" /> <input type="hidden" id="sLocationIdMcu" name="location.id" value="" /></td>
									<td class="withinput"><fmt:message key="aimir.status" /></td>
									<td class="padding-r20px">
									<select id="sMcuStatus" name="select" style="width: 140px;">
											<option value=""><fmt:message key="aimir.all" /></option>
											<c:forEach var="mcuStatus" items="${mcuStatus}">
												<option value="${mcuStatus.id}">${mcuStatus.descr}</option>
											</c:forEach>
									</select>
									</td>
									<!-- Reset Button (S) -->
				                    <td>
				                        <div id="btn">
				                            <ul style="margin-left: 0px">
				                            	<li><a href="javascript:reset();" class="on"><fmt:message key="aimir.form.reset"/></a></li>
				                            </ul>
				                        </div>
				                    </td>
				                    <!-- Reset Button (E) -->
				                    <!-- Search Button (S) -->
				                    <td>
				                        <div id="btn">
				                            <ul style="margin-left: 0px">
				                            	<li><a href="javascript:getMemberSelectDCU();" class="on"><fmt:message key="aimir.button.search"/></a></li>
				                            </ul>
				                        </div>
				                    </td>
				                    <!-- Search Button (E) -->
								</tr>
							</table>
							<div id="treeDivMcuOuter" class="tree-billing auto" style="display: none;">
								<div id="treeDivMcu"></div>
							</div>
						</div>
					</form>
		            </div>
		            <!-- DCU 검색조건 -->
		            <!-- Location 검색조건 -->
		            <div id="searchLoc">
		            <table class="searchoption wfree" style='margin-left: 20px;'>
		            	<tr colspan='8'></tr>
		                <tr>
							<td class="gray11pt withinput"><fmt:message key="aimir.location" /></td>
		                    <td class="gray11pt withinputx"><input type="text" id="searchWordLoc" name="searchWordLoc" style="width: 140px" /> <input type="hidden" id="sLocationIdLoc" name="location.id" value="" /></td>
							<td class="space20"></td>
		                    <td>
		                        <div id="btn">
		                            <ul><li><a href="javascript:getMemberSelectLoc();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
		                        </div>
		                    </td>
		                    <td><input class=" x-form-text x-form-field x-item-disabled" 
		                    	placeholder="Search All" id="memberDCU" type="hidden" readonly="readonly" style="width: 200px;"></td>
		                    <td class="space20"></td>
		                </tr>
		                <tr colspan='8'></tr>
		            </table>
		            	<div id="treeDivLocOuter" class="tree-billing auto" style="display: none;">
							<div id="treeDivLoc"></div>
						</div>
		            </div>
		            <!-- Location 검색조건 -->
		            <!-- Meter 검색조건 -->
		            <div id="searchMeter">
		            <form name="search">
			        <!-- Search Background (S) -->
			        <!-- <div class="search-bg-withouttabs"> -->
			            <div class="searchoption-container">
			                <table class="searchoption wfree" border=0>
			                    <tr>
			                        <td class="withinput" style="width: 80px"><fmt:message
			                                key="aimir.metertype" /></td>
			                        <td class="padding-r20px"><select id="sMeterType" style="width: 190px;" name="select">
			                                <option value=""><fmt:message key="aimir.all" /></option>
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
			                        <td class="withinput" style="width: 80px"><fmt:message
			                                key="aimir.meterid" /></td>
			                        <td class="padding-r20px">
			                            <input type="text" id="sMdsId" style="width: 190px;" />
			                        </td>
			                        <!-- Group -->
			                        <td class="withinput" style="width: 100px"><fmt:message
			                                key="aimir.metergroup" /></td>
			                        <td class="padding-r20px"><select id="sMeterGroup" name="select" style="width: 120px;">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                        </select></td>
			                        <td class="withinput"><fmt:message key="aimir.status" /></td>
			                        <td class="padding-r20px"><select id="sStatus" name="select" style="width: 190px;">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                                <c:forEach var="meterStatus" items="${meterStatus}">
			                                    <option value="${meterStatus.id}">${meterStatus.descr}</option>
			                                </c:forEach>
			                        </select></td>
			                    </tr>
			                    <tr>
			                        <td class="withinput"><fmt:message key="aimir.location" /></td>
			                        <td class="padding-r20px"><input name="searchWordMe"
			                            id='searchWordMe' type="text" style="width: 190px""/> <input
			                            type='hidden' id='sLocationIdMe' name="location.id" value=''></input></td>
			                        <td class="withinput" width="120px"><fmt:message
			                                key="aimir.mcuid" /></td>
			                        <td class="padding-r20px"><input type="text" id="sMcuName"
			                            style="width: 190px;" /></td>
			                            
			                        <td class="withinput"><fmt:message key="aimir.vendor" /></td>
			                        <td class="padding-r20px"><select id="sVendor" name="SELECT"style="width: 120px;"onChange="javascript:getDeviceModelsByVenendorId();">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                        </select></td>
			                        <td class="withinput"><fmt:message key="aimir.model" /></td>
			                        <td class="padding-r20px"><select id="sModel" name="select" style="width: 120px;">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                        </select></td>
			                    </tr>
			                    <tr>
			                        <td class="withinput"><fmt:message
			                                key="aimir.installationdate" /></td>
			                        <td class="padding-r20px"><span><input
			                                id="sInstallStartDate" class="day" type="text"></span> <span><input
			                                value="~" class="between" type="text"></span> <span><input
			                                id="sInstallEndDate" class="day" type="text"></span> <input
			                            id="sInstallStartDateHidden" type="hidden" /> <input
			                            id="sInstallEndDateHidden" type="hidden" /></td>
			
			                        <td class="withinput"><fmt:message key="aimir.modem" /></td>
			                        <td class="padding-r20px"><select id="sModemYN" name="select" style="width: 190px;">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                                <option value="Y"><fmt:message key="aimir.yes" /></option>
			                                <option value="N"><fmt:message key="aimir.no" /></option>
			                        </select></td>
			                        <td class="withinput"><fmt:message key="aimir.customer" /></td>
			                        <td class="padding-r20px"><select id="sCustomerYN" name="select" style="width: 120px;">
			                                <option value=""><fmt:message key="aimir.all" /></option>
			                                <option value="Y"><fmt:message key="aimir.yes" /></option>
			                                <option value="N"><fmt:message key="aimir.no" /></option>
			                        </select></td>
			                        <td class="withinput" style="width: 150px"><fmt:message
			                                key="aimir.lastcomm" /></td>
			                        <td class="padding-r20px">
			                            <span><input id="sLastcommStartDate" class="day"
			                                type="text"></span> <span><input value="~"
			                                class="between" type="text"></span>
			                            <span><input id="sLastcommEndDate" class="day" type="text"></span>
			                            <input id="sLastcommStartDateHidden" type="hidden"> <input
			                            id="sLastcommEndDateHidden" type="hidden">
			                        </td>
			                    </tr>
			                    <tr>
			                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
			                        <td class="padding-r20px"><input id='sCustomerId'
			                            type="text" style="width: 190px" /></td>
			                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
			                        <td class="padding-r20px"><input id='sCustomerName'
			                            type="text" style="width: 190px" /></td>
			                        <!-- contract no -->
			                        <td class="withinput"><fmt:message key="aimir.contractNumber" /></td>
			                        <td class="padding-r20px"><input id='sConsumLocationId'
			                            type="text" style="width: 120px" />
			                        </td>
			                        <td class="withinput"><fmt:message key="aimir.customeraddress" /></td>
			                        <td class="padding-r20px">
			                            <input id='sMeterAddress' type="text" style="width: 190px" />
			                        </td>
			                    </tr>
			                     <tr>
			                        <td class="withinput"><fmt:message key="aimir.shipment.gs1" /></td>
			                        <td class="padding-r20px"><input id='sGs1'type="text" style="width: 190px" /></td>
			                        <td colspan="6" align="right">
			                            <em class="am_button">
			                                <a href="javascript:reset();"><fmt:message key="aimir.form.reset"/></a>
			                            </em>&nbsp;
			                            <em class="am_button">
			                                <a href="javascript:getMemberSelectMeter()" class="on"><fmt:message key="aimir.button.search" /></a>
			                            </em>
			                        </td>
			                    </tr>
			                </table>
			                    <div id="treeDivMeOuter" class="tree-billing auto"
			                        style="display: none;">
			                        <div id="treeDivMe"></div>
			                    </div>
			            <!-- </div> -->
			        </div>
			        <div id='drAlertDataPop'></div>
			        <div id='drAlertDataPopFailure'></div>
			        <div id='drAlert'></div>
			    </form>
			    </div>
		        <!-- Meter 검색조건 -->
		        <!-- Modem 검색조건 -->
		        <div id="searchModem">
		        <form name="search">
			    <!-- <input type="hidden" id="viewLogType" value="" />
    			<input type="hidden" id="modemDetailTabValue" value="" /> -->
    			<div class="searchoption-container">
			        <table class="searchoption wfree"  style='margin-left: 20px;'>
		                <tr>
		                    <td class="withinput" style="width: 80px;"><fmt:message key="aimir.modem.type"/></td>
		                    <td class="padding-r20px">
		                        <select id="sModemType" name="select" style="width:140px;">
		                            <option value=""><fmt:message key="aimir.all"/></option>
		                            <c:forEach var="modemType" items="${modemType}">
		                                <option value="${modemType.name}">${modemType.descr}</option>
		                            </c:forEach>
		                        </select>
		                    </td>
		                    <td class="withinput" style="width: 90px;"><fmt:message key="aimir.modemid"/></td>
		                    <td class="padding-r20px">
		                        <input type="text" id="sModemId" style="width:189px;"/>
		                    </td>
		                    <td class="withinput" style="width: 90px;">Meter ID</td>
		                    <td class="padding-r20px">
		                        <input type="text" id="sMeterSerial" style="width:189px;"/>
		                    </td>
		                    <td class="withinput" width="130px"><fmt:message key="aimir.mcucode.fmversion"/></td>
		                    <td class="padding-r20px"><input type="text" id="sModemFwVer" style="width:189px;"/></td>
		                </tr>
		                <tr>
		               		<td class="withinput"  width="90px"><fmt:message key="aimir.fw.hwversion"/></td>
		                    <td class="padding-r20px"><input type="text" id="sModemHwVer" style="width:189px;"/></td>
		                    <td class="withinput"><fmt:message key="aimir.mcutype"/></td>
		                    <td class="padding-r20px">
		                        <select id="mMcuType" name="select" style="width:140px;">
		                            <option value=""><fmt:message key="aimir.all"/></option>
		                            <c:forEach var="mcuType" items="${mcuTypeMap}">
		                                <option value="${mcuType.id}">${mcuType.descr}</option>
		                            </c:forEach>
		                        </select>
		                    </td>
		                    <td class="withinput"><fmt:message key="aimir.mcuid"/></td>
		                    <td class="padding-r20px"><input type="text" id="sMcuName" style="width:189px;"/></td>
		                    <td class="withinput"><fmt:message key="aimir.location"/></td>
		                    <td colspan="1">
		                	<!-- 검색어 표시 -->
		                    <input name="searchWordMo" id='searchWordMo'  type="text" style="width:189px;"/>
		                    <!-- 실제 LocationID값이 저장됨 -->
		                    <input type='hidden' id='sLocationIdMo'  name="location.id" value=''></input>
		                    </td>
		                </tr>
		                <tr>
		                	<td class="withinput" style="width: 80px;"><fmt:message key="aimir.status"/></td>
		                    <td class="padding-r20px">
		                        <select id="sModemStatus" name="select" style="width:140px;">
		                            <option value=""><fmt:message key="aimir.all"/></option>
		                            <c:forEach var="modemStatus" items="${modemStatus}">
		                            	<option value="${modemStatus.id}">${modemStatus.descr}</option>
		                            </c:forEach>
		                        </select>
		                    </td>
		                    <td class="withinput"><fmt:message key="aimir.installationdate"/></td>
		                    <td class="padding-r20px" >
		                        <span><input id="sInstallStart" class="day" type="text" ></span>
		                        <span><input value="~" class="between" type="text"></span><span><input id="sInstallEnd" class="day" type="text" ></span>
		                        <input id="sInstallStartHidden" type="hidden"><input id="sInstallEndHidden"   type="hidden"> </td>
		                    <td class="withinput" ><fmt:message key="aimir.lastcomm" /></td>
		                    <td colspan="1" name ='select'>
		                        <span><input id="sLastcommStart" class="day" type="text"></span>
		                        <span><input value="~" class="between" type="text"></span><span><input id="sLastcommEnd" class="day" type="text"></span>
		                        <input id="sLastcommStartHidden" type="hidden"> <input id="sLastcommEndHidden" type="hidden"></td>
		                    <td class="padding-r10px">
		                         <input type ="hidden" id ='sModemSwRev' value =""/>
		                    </td>
		                    <td></td>
		                    <td></td>
		                    <td></td>
		                    <!-- Reset Button (S) -->
		                    <td>
		                        <div id="btn">
		                            <ul style="margin-left: 0px">
		                            	<li><a href="javascript:reset();" class="on"><fmt:message key="aimir.form.reset"/></a></li>
		                            </ul>
		                        </div>
		                    </td>
		                    <!-- Reset Button (E) -->
		                    <!-- Search Button (S) -->
		                    <td>
		                        <div id="btn">
		                            <ul style="margin-left: 0px">
		                            	<li><a href="javascript:getMemberSelectModem()" class="on"><fmt:message key="aimir.button.search"/></a></li>
		                            </ul>
		                        </div>
		                    </td>
		                    <!-- Search Button (E) -->
		                </tr>
		            </table>
	            	<div id="treeDivMoOuter" class="tree-billing auto" style="display: none;">
						<div id="treeDivMo"></div>
					</div> 
		        </div>
				</form>
				 </div>
                <div id="treeDivAOuter" class="tree-billing auto"
				style="display: none;">
				<div id="treeDivA"></div>
				</div>    
		        <!-- Modem 검색조건 -->
		        <!-- Contract 검색조건 -->
		        <div id="searchContract">
		            <div class="searchoption-container">
		                <table class="searchoption wfree" >
		                    <tr>
		                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
		                        <td><input id="contractNumberB"></td>                                
		                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
		                        <td class="padding-r20px2"><input id="customerNoB" type="text"></td>
		                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
		                        <td class="padding-r20px2"><input id="customerNameB" style="width:125px"></td>
		                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
		                        <td class="padding-r20px2">
		                            <input type="text" id="locationBText" name="location.name" style="width:142px">
		                            <input type="hidden" id="locationB" name="location.id" value="" />
		                        </td>
		                    </tr>
		                    <tr>
		                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
		                        <td class="padding-r20px2"><input id="mdsIdB" type="text"></td>
		                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
		                        <td class="padding-r20px2"><select id="statusB" style="width: 125px"></select></td>
		                        <td class="withinput"><fmt:message key="aimir.customer.dr" /></td>
		                        <td class="padding-r20px2"><select id="drB" style="width: 125px"></select></td>
		                        <td class="withinput"><fmt:message key="aimir.sic" /></td>
		                        <!-- <td><select id="customerTypeB" style="width:270px"></select></td> -->
		                        <td><input name="sicBText" id='sicBText' style="width:270px;" type="text" />
		                            <input type="hidden" id="sicIdB" value=""></input>
		                            <input type="hidden" id="sicIdsB" value=""></input></td>
		                    </tr>
		                    <tr>
		                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
		                        <td><select id="tariffIndexB" style="width: 125px"></select></td>
		                        <td class="withinput"><fmt:message key="aimir.contract.demand" /></td>
		                        <td class="padding-r20px2"><input id="contractDemandB" style="width:80px;"></td>
		                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
		                        <td class="padding-r20px2"><select id="creditTypeB" style="width:125px;"></select></td>
		                        <td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>
		                        <td colspan="4">
		                            <span><input id="startDateB" type="text" style="width:80px;"></span>
		                            <span><input value="~" type="text" class="between"></span>
		                            <span><input id="endDateB" type="text" style="width:80px;"></span>
		                        </td>
		                        <td>
		                            <span class="am_button margin-l10 margin-t1px"><a href="javascript:getMemberSelectCon();" class="on"><fmt:message key="aimir.button.search" /></a></span>
		                        </td>
		                    </tr>
		                </table>
		                <div id="treeDivBOuter" class="tree-billing auto" style="display:none;">
		                    <div id="treeDivB"></div>
		                </div>
		                <div id="treeDivSBOuter" class="tree-billing auto" style="display: none;">
		                    <div id="treeDivSB"></div>
		                </div>
		            </div>
		           <!--  <div class="bodyleft_customer">
		                <ul><li>
		                    <div id="elecCustomerDiv" class="flexlist-customerlist">
		                    </div>
		                </li></ul>
		            </div> -->
		        </div>
		        <!-- Contract 검색조건 -->
		        <!-- EndDevice 검색조건 -->
		        <div id="searchEnddevice">
		        	<form name="search">
						<ul class="row">
	                        <li class="col">
	                            <div class="search-s1">
	                                <ul>
	                                    <li class="search-s1-input">
	                                        <input id="memberName" type="text" value="<fmt:message key='aimir.membername'/>" onclick="javascript:this.value = '';" onkeydown="javascript:memberSearchKeyEvent(event);"></li>
	                                    <li class="search-s1-btn"><a href="javascript:memberSearch();" ></a></li>
	                                </ul>
	                            </div>
	                        </li>
	                    </ul>
                    </form>
                </div>
		        <!-- EndDevice 검색조건 -->
                </div>
                <div class="dashedline-dark clear"></div>
    
                <!-- 그리드 (S) -->
                <div class="flexlist">
                    <div id="" style="width: 48%; float:left;">
                        <ul >
                            <li style="margin-left:30px;">
                                <font style="font-weight:bold; color: #676767;"><fmt:message key="aimir.searchResult"/> : </font>
                                <font style="font-weight:bold; color: #FC0000;" id="resultCnt">0</font>
                            </li >
		                    <br>
                            <li ><div id="memberSelectDiv" style="margin-left:30px; width:600px;"></div></li>
                        </ul>
                    </div>
                    <!-- <div id="memberSelectBtnDiv" style="width: 23px; float:left; align: center; valign: middle;"> -->
                    <div id="memberSelectBtnDiv" style="width: 35px; float:left; align: center; valign: middle;margin-left: 15px;">
                        <ul id="arrowlist" style="margin-top: 180px;">
                            <li class="btn-putin"><a id="memberAddBtn" href="javascript:memberAdd();"><!-- 추가 --></a></li>
                            <li class="btn-putout"><a id="memberDelBtn" href="javascript:memberDel();"><!-- 제거 --></a></li>
                        </ul>
                    </div>
                    <div id="" style="width: 48%; float:left;">
                        <ul>
                            <li style="margin-left:50px;"><font style="font-weight:bold; color: #676767;"><fmt:message key="aimir.addItems"/></font></li>
                            <br>
                            <li><div id="memberSelectedDiv" style="margin-left:50px; width:600px;"></div></li>
                        </ul>
                    </div>
                </div>
                <div style="clear:both;"></div>
    
                <%-- <div class="margin-t5px floatright">
                    <button type="button" class="sm_current" onclick="javascript:saveMember('false');"><fmt:message key="aimir.save2"/></button>
                    <button type="button" class="sm_current" onclick="javascript:saveMember('true');"><fmt:message key="aimir.button.saveNadd"/></button>
                    <button type="button" class="sm thelast" onclick="javascript:cancel2();"><fmt:message key="aimir.cancel"/></button>
                </div> --%>
    
            </li>
            </ul>
        </div>
        <!-- Step 02b - 멤버추가 (E) -->
    
    </div>
    <div id="treeDivAOuter" class="tree-billing auto"
				style="display: none;">
				<div id="treeDivA"></div>
	</div>
    <!-- 그룹멤버추가 전체 (E) -->

</body>
</html>