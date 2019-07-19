<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview"/></title>

<%@ include file="/gadget/system/preLoading.jsp"%>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >

<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>

<script type="text/javascript">

//################## Drag & Drop START Plugin START ######################//
Ext.ux.CellFieldDropZone = Ext.extend(Ext.dd.DropZone, {
   constructor: function(){},

   init: function(grid) {
       if (grid.rendered) {
           this.grid = grid;
           this.view = grid.getView();
           Ext.ux.CellFieldDropZone.superclass.constructor.call(this, this.view.scroller);
       } else {
           grid.on('render', this.init, this);
       }
   },

//  Scroll the main configured Element when we drag close to the edge
   containerScroll: true,

   getTargetFromEvent: function(e) {
//      Ascertain whether the mousemove is within a grid cell
       var t = e.getTarget(this.view.cellSelector);
       if (t) {

           var rowIndex = this.view.findRowIndex(t);
           var columnIndex = this.view.findCellIndex(t);
           if ((rowIndex !== false) && (columnIndex !== false)) {
               return {
                   node: t,
                   record: this.grid.getStore().getAt(rowIndex),
                   fieldName: this.grid.getColumnModel().getDataIndex(columnIndex)
               };
           }
       }
   },

//  On Node enter, see if it is valid for us to drop the field on that type of column.
   onNodeEnter: function(target, dd, e, dragData) {
       delete this.dropOK;
       if (!target) {
           return;
       }

//      Check that a field is being dragged.
       var f = dragData.field;
       if (!f) {
           return;
       }

//      Check whether the data type of the column being dropped on accepts the
//      dragged field type. If so, set dropOK flag, and highlight the target node.
       if( target.fieldName == "MAPPINGIMGURL" && target.record.get("HOMEDEVICEIMGFILENAME") !="" && dragData.record.get("HOMEDEVICEGROUPNAME") == target.record.get("HOMEDEVICEGROUPNAME"))
       {
           this.dropOK = true;
           Ext.fly(target.node).addClass('x-drop-target-active');
       }else{
           this.dropOK = false;
           return;
       }
   },

//  Return the class name to add to the drag proxy. This provides a visual indication
//  of drop allowed or not allowed.
   onNodeOver: function(target, dd, e, dragData) {
       return this.dropOK ? this.dropAllowed : this.dropNotAllowed;
   },

//   nhighlight the target node.
   onNodeOut: function(target, dd, e, dragData) {
       Ext.fly(target.node).removeClass('x-drop-target-active');
   },

//  Process the drop event if we have previously ascertained that a drop is OK.
   onNodeDrop: function(target, dd, e, dragData) {
       if (this.dropOK) {
           if(chkDuplicatedMapping(target.record.get("ID"),dragData.record.get("MODEMID"), target.record.get("HOMEDEVICEGROUPNAME"))){
               var targetE1 = Ext.get(target.node); 
               targetE1.update("<img src='../../"+dragData.record.get("HOMEDEVICEIMGFILENAME")+"' width='50px' height='50px'></img>");
               updateMappingInfo(target.record.get("ID"),dragData.record.get("MODEMID"), target.record.get("HOMEDEVICEGROUPNAME"));
           }
           return true;
       }
   }
});

//  A class which makes Fields within a Panel draggable.
//  the dragData delivered to a coooperating DropZone's methods contains
//  the dragged Field in the property "field".
Ext.ux.CellFieldDragZone = Ext.extend(Ext.dd.DragZone, {
   constructor: function(){},

//  Call the DRagZone's constructor. The Panel must have been rendered.
   init: function(grid) {
        if (grid.nodeType) {
            Ext.ux.CellFieldDragZone.superclass.init.apply(this, arguments);
       } else {
           if (grid.rendered) {
               this.grid = grid;
               this.view = grid.getView();
               Ext.ux.CellFieldDragZone.superclass.constructor.call(this, this.view.scroller);
           } else {
               grid.on('afterlayout', this.init, this);
           }
       }
   },

   scroll: false,

//  On mousedown, we ascertain whether it is on one of our draggable Fields.
//  If so, we collect data about the draggable object, and return a drag data
//  object which contains our own data, plus a "ddel" property which is a DOM
//  node which provides a "view" of the dragged data.
   getDragData: function(e) {

       var t = e.getTarget(this.view.cellSelector);
       if (t) {
           //e.stopEvent();

           if (Ext.isOpera) {
               Ext.fly(t).on('mousemove', function(e1){
                   t.style.visibility = 'hidden';
                   (function(){
                       t.style.visibility = '';
                   }).defer(1);
               }, null, {single:true});
           }

           var rowIndex = this.view.findRowIndex(t);
           var columnIndex = this.view.findCellIndex(t);
           var columnName = this.grid.getColumnModel().getDataIndex(columnIndex);
           var record = this.grid.getStore().getAt(rowIndex);
           if ( columnName == "HOMEDEVICEIMGFILENAME" && record.get(columnName) != "" ){
               var d =  document.createElement('img');
               d.setAttribute('src', '../../'+record.get(columnName));
               Ext.fly(d).setWidth('50px');
               Ext.fly(d).setHeight('50px');
               if ((rowIndex !== false) && (columnIndex !== false)) {
                   return {
                   field: t,
                   repairXY: Ext.fly(t).getXY(),
                   ddel: d,
                   record: record
                   }
               }
           }
       }
   },
//  The coordinates to slide the drag proxy back to on failed drop.
   getRepairXY: function() {
       return this.dragData.repairXY;
   }
});   

//################## Drag & Drop  Plugin END ######################//

    var supplierId = "${supplierId}";
    var homeGroupId = "${homeGroupId}";
    var homeDeviceImgUrl = 'uploadImg/default/homeDeviceDefaultImg.jpg';
    var operatorId = "${operatorId}";

    $(document).ready(function(){

    	// ExtJS의 Tip초기화
        Ext.QuickTips.init();

        if("${isNotService}" == "true") {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();
            hide();
            return;
        } else { // 해당 가젯에 대한 권한이 있을때
            $("#isNotService").hide();
        }
        
    	/* 버튼 이벤트 정의 */
        //Home Device 추가 버튼을 클릭, 등록 폼 리셋
        $("#registerHomeDeviceForm").click(function() { reset(); });

        // Home Device 등록 버튼 클릭, 제품 정보 등록
        $("#registerHomeDevice").click(function() { registerHomeDevice(); });

        // Home Device 변경 버튼 클릭, 제품 정보 갱신 
        $("#modifyHomeDevice").click(function() {modifyHomeDevice();});

        // Home Device 삭제 버튼 클릭 , 제품 정보 삭제
        $("#deleteHomeDevice").click(function() {deleteHomeDevice();});

        // 프로세스 초기화
        initProcess();
    });

    // 프로세스 초기화
    var initProcess = function() {
    	$.ajaxSetup({ async: false });
    	// 계약 정보 취득
        getContract();

    	// 콤보 박스 생성(제품 그룹, 제품 유형)
        makeSelectbox();

    	// 제품 정보 그리드 작성
        getGridData();

        // 제품정보 등록 폼 리셋
        reset();
        $.ajaxSetup({ async: true });
    };

    // 계약 정보 취득
    var getContract = function() {
        //$.ajaxSetup({ async: false });
        var params = {
                "contractId" : $("#contractNumber").val()
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/getContract.do",
                params,
                function(result) {
                    supplierId = result.contract.supplier;
                    homeGroupId = result.homeGroupId;
                }
            );
        //$.ajaxSetup({ async: true });
    };

    // 계약번호 콤보박스에서 계약번호 선택시 발생하는 이벤트
    var changeContract = function() {
    	$.ajaxSetup({ async: false });
    	// 계약 정보 취득
        getContract();

    	// 그리드 데이터 취득
        getGridData();

    	// 제품정보 등록 폼 리셋
        reset();
        $.ajaxSetup({ async: true });
    };

    // 등록 버튼 클릭 이벤트
    var registerHomeDevice = function() {
        // 입력값 체크
        if ( checkInput() ) { 

            var params = {
                    "homeGroupId" : homeGroupId,                    
                    "homeDeviceGroupName" : $("#homeDeviceGroupName").val(),
                    "homeDeviceCategory" : $("#homeDeviceCategory").val(),
                    "identificationID" : $("#identificationID").val(),                    
                    "friendlyName" : $("#friendlyName").val(),
                    "homeDeviceImg" : homeDeviceImgUrl,
                   // "supplierID" : supplierId,
                    "contractId" : $("#contractNumber").val(),
                    "operatorId" : operatorId
            };

            $.post("${ctx}/gadget/homeDeviceMgmt/registerHomeDevice.do",
                    params,
                    function(result, status) {
		                if (result.status == "SUCCESS") {
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.information.successInsert'/>", function() {getGridData(); reset();});
		                } else {
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.alert.failInsert'/>", function() { });
		                }
                    },
                    "json"
            );
        } 
    };

    // 수정 버튼 클릭 이벤트
    var modifyHomeDevice = function(){
        // 입력값 체크
        if ( checkInput() ) {

            var params = {
                    "id" :  $("#id").val(),
                    "homeGroupMemberId" :  $("#groupMemberId").val(),
                    "modemId" :  $("#modemId").val(),
                    "homeGroupId" : homeGroupId,                    
                    "homeDeviceGroupName" : $("#homeDeviceGroupName").val(),
                    "identificationID" : $("#identificationID").val(),
                    "friendlyName" : $("#friendlyName").val(),
                    "homeDeviceImg" : homeDeviceImgUrl,
                    "orgHomeDeviceGroupName" : $("#orgHomeDeviceGroupName").val(),
                    "orgHomeDeviceCategory" : $("#orgHomeDeviceCategory").val(),
                    "orgIdentificationID" : $("#orgIdentificationID").val()
                   // "supplierID" : supplierId
            };

            $.post("${ctx}/gadget/homeDeviceMgmt/modifyHomeDevice.do",
                    params,
                    function(result, status) {
		                if (result.status == "SUCCESS") {
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.information.successModify'/>", function() {getGridData(); reset();});
		                } else {
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.alert.failModify'/>", function() { });
		                }
                    },
                    "json"
            );
        }           
    };

    // 삭제 버튼 클릭 이벤트
    var deleteHomeDevice = function(){
            var params = {
                    "id" :  $("#id").val(),
                    "homeGroupMemberId" :  $("#groupMemberId").val(),
                    "modemId" :  $("#modemId").val(),
                    "homeGroupId" : homeGroupId,                    
                    "homeDeviceCategory" : $("#orgHomeDeviceCategory").val()
            };

            $.getJSON("${ctx}/gadget/homeDeviceMgmt/deleteHomeDevice.do",
                    params,
                    function(result) {
                        if (result.status == "SUCCESS") {
                           Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.information.successDelete'/>", function() {getGridData(); reset();});
                        } else {
                            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.alert.failDelete'/>", function() { });
                        }
                    }
            );
    };

    // 입력항목의  유효성 체크
    var checkInput = function() {

        /*if ($("#identificationID").val() != $("#_identificationID").val()) {
            if ( $("#identificationID").val().length <= 0 ) {
                Ext.MessageBox.alert('Status', '식별 ID를 입력해 주세요.', function() {$("#identificationID").focus(); });
                return false;
            } 

            if ( idChecked == false ) {
                Ext.MessageBox.alert('Status', '식별 ID의 유효성 체크를 해 주세요.', function() {$("#identificationID").focus(); });
                return false;
            } 
        }*/

        if(!isGeneralAP) { // 제품 유형이 일반 가전이 아닐경우
            // 식별ID의 필수 체크
	 //       if ($("#identificationID").val() != $("#orgIdentificationID").val()) { // 식별아이디가 변경됬을 경우, 아이디를 체크한다.
            if ( $("#identificationID").val().length <= 0 ) {
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputIdentificationID'/>", function() {$("#identificationID").focus(); });
                return false;
            } 

            if ( idChecked == false ) {
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.checkIdentificationID'/>", function() {$("#identificationID").focus(); });
                return false;
            } 
	  //      } 
       } else {
           // 식별ID의 유효성 체크 여부 체크
           if ( nameChecked == false ) {
               Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.checkDeviceName'/>", function() {$("#friendlyName").focus(); });
               return false;
           }  
       }

        // 제품 명칭 필수 체크
        if ( $("#friendlyName").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputDeviceName'/>", function() { $("#friendlyName").focus();});
            return false;
        }
        return true;
    };

    var idChecked = true;
    // 식별ID변경시, 유효성 체크 플러그의 초기화
   /* var changeID = function() {

    	// 체크 플러그 초기화
        idChecked = false;
    };*/

    // 입력한 식별 ID의 유효성 체크 START
    var checkID = function() {

        if ( $("#identificationID").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputIdentificationID'/>", function() {$("#identificationID").focus(); });
            return;
        } else {

            if ( $("#identificationID").val().match(/[^0-9A-Za-z]+/) != null){
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.onlyAlphNumIdentificationID'/>", function() {$("#identificationID").focus(); });
                return;
            }

            /*
             var params = {
                "identificationID" : $("#identificationID").val(),
                "homeDeviceCategory" : $('#homeDeviceCategory').val()
            };

            $.getJSON('${ctx}/gadget/homeDeviceMgmt/checkId.do', params,
                function(result) {

                    if (result.status == "duplicate" && $("#identificationID").val() != $("#orgIdentificationID").val()) {
                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.existIdentificationID'/>", function() {$("#identificationID").focus(); });
                        idChecked = false;

                    } else {
                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.availableIdentificationID'/>", function() { });
                        idChecked = true;
                    }
                    return;
                }
            );
            */
            

        }
    };
    // 입력한 식별 ID의 유효성 체크 END

    var nameChecked = true;

    // 제품 유형 변경시, 유효성 체크 플러그의 초기화
    var changeName = function() {
        nameChecked = false;
    };

    // 제품 유형이 가전일경우 제품 명칭 유효성 체크 실시
    var checkName = function() {
        
        // 식별아이디 필수 체크
        if ( $("#friendlyName").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputDeviceName'/>", function() {$("#friendlyName").focus(); });
            return;
        } else {

            var params = {
                "friendlyName" : $("#friendlyName").val(),
               // "homeDeviceCategory" : $('#homeDeviceCategory').val(),
                "contractId" : $("#contractNumber").val()
            };

            // 제품 유형이 일반 가전일 경우, 중복 체크를 실시한다.
            $.post("${ctx}/gadget/homeDeviceMgmt/checkName.do",
                    params,
                    function(result, status) {
                        if (result.status == "duplicate" && $("#friendlyName").val() != $("#orgFriendlyName").val()) {
                            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alertduplicatedDeviceName'/>", function() {$("#friendlyName").focus(); });
                            nameChecked = false;
                        } else {
                            
                            Ext.MessageBox.alert("<fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceMgmt'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.availableDeviceName'/>", function() { });
                            nameChecked = true;
                        }
                        return;
                    },
                    "json"
            );
        }       
    };
 
    // 그리드 라인 선택시, 제품정보 수정을 위해서 폼값을 셋팅한다.
    function setHomeDeviceInfo(record) {
        // PK설정
        $("#id").val(record.get('ID'));
        $("#groupMemberId").val(record.get('GROUPMEMBERID'));
        $("#modemId").val(record.get('MODEMID'));
        $("#homeDeviceGroupName").val(record.get('HOMEDEVICEGROUPNAME'));
        $("#homeDeviceGroupName").selectbox();

        $("#orgHomeDeviceGroupName").val(record.get('HOMEDEVICEGROUPNAME'));
        $("#orgHomeDeviceCategory").val(record.get('CATEGORYID'));
        $("#orgFriendlyName").val(record.get('FRIENDLYNAME'));
        // 식별 아이디 설정
        $("#orgIdentificationID").val(record.get('SERIALNUMBER')); 

        $("#_homeDeviceCategoryName").val($("#homeDeviceCategory > option[value="+record.get('CATEGORYID')+"]").text());

        // 입력폼에 식별 아이디 설정
        $("#identificationID").val(record.get('SERIALNUMBER'));

        $("#friendlyName").val(record.get('FRIENDLYNAME'));
        $("#homeDeviceImgView").html("<img src='../../"+record.get('HOMEDEVICEIMGFILENAME')+"' width='60px' height='80px'>");
        homeDeviceImgUrl = record.get('HOMEDEVICEIMGFILENAME');

        changeRegisterForm(record.get('CATEGORYID'));

        // 등록 상태가 완료일때는 식별 아이디 변경 불가능
        if(record.get('INSTALLSTATUSCODE') == '1.9.3.3') {
            $('#identificationID').attr("disabled", "disabled");
            $("#register-homeDevice-checkID").hide();
        }else{         // 상태가 완료가 아닐때는 식별 아이디 변경 가능
            $('#identificationID').removeAttr("disabled");
            $("#register-homeDevice-checkID").show();
        }

        // 버튼 표시/비표시 설정
        $("#pane-Customer-Info-Button").show();
        $("#pane-HomeDevice-Update-Button").show();
        $("#pane-HomeDevice-Register-Button").hide();
        $("#register-homeDevice-category").hide();
        $("#modify-homeDevice-category").show();

        $("#divProcessTitle").text("<fmt:message key='aimir.hems.label.homeDeviceModify'/>");


    }

    //폼값 리셋
    function reset() {

         homeDeviceImgUrl = 'uploadImg/default/homeDeviceDefaultImg.jpg';
        $("#homeDeviceGroupName option:eq(0)").attr("selected", "selected");
        $("#homeDeviceGroupName").selectbox();
        $("#homeDeviceCategory option:eq(0)").attr("selected", "selected");
        $("#homeDeviceCategory").selectbox();
        $('#identificationID').removeAttr("disabled");
        $("#identificationID").val('');
        $("#orgIdentificationID").val('');
        $("#friendlyName").val('');
        $("#homeDeviceImgView").html("<img src='../../"+homeDeviceImgUrl+"' width='80px' height='93px'>");
 
        // 버튼 표시/비표시 설정
        $("#pane-Customer-Info-Button").hide();
        $("#pane-HomeDevice-Update-Button").hide();
        //$("#pane-HomeDevice-Delete-Button").hide();
        $("#pane-HomeDevice-Register-Button").show();
        $("#register-homeDevice-category").show();
        $("#modify-homeDevice-category").hide();
        $("#register-homeDevice-checkID").show();

        //aimir.hems.label.homeDeviceModify
        $("#divProcessTitle").text("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>");

        changeRegisterForm(array[0].id); // 제품 유형 상위의 아이디로 폼 변경
    }

    // 제품 정보 등록 폼을 변경한다.
    // 1. 일반가전일 경우 : 식별아이디 입력 불요, 제품 명칭 중복확인 버튼 추가
    // 2. 일반 가전 외 : 식별아이디 입력 요, 제품 명칭 중복확인 버튼 불요
    var isGeneralAP;
    var changeRegisterForm = function(val) {
        var count = array.length;
        for (var i = 0; i < count; i++) {

            if(array[i].id == val && array[i].code == "13.2") { // 제품 유형이 일반 가전일 경우
                $("#idTR").hide();     // 식별아이디 입력폼 숨김
                $("#register-homeDevice-checkID").hide();
                $("#checkBTN").show(); // 제품 명칭 중복확인 버튼 표시
                isGeneralAP = true;
                return;
            }else{
                $("#idTR").show();     // 식별아이디 입력폼 표시
                $("#register-homeDevice-checkID").show();
                $("#checkBTN").hide(); // 제품 명칭 중복확인 버튼 숨김
                isGeneralAP = false;
            }
        }
    };

    $(window).resize(function() {
    	getGridData();
     });

    var mappingGridOn = false;
    var gridLeft;
    var gridRight;
    var displayPanel;

    // 그리드 표시
    var getGridData = function() {
        emergePre();
        $.ajaxSetup({ async: false });
        $.ajaxSetup({ cache: false });

        var width = $("#homeDeviceMapping").width();
        var leftGridWidth = width*(2/5);
        var rightGridWidth = width*(3/5) + 17;  // 17 -> 스크롤의 폭을 더한 넓이로 설정

        var storeLeft = new Ext.data.GroupingStore({
            autoLoad: true,
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoLeft.do?homeGroupId=" + homeGroupId,
            reader: new Ext.data.JsonReader({
                root:'result',
                fields: ["ID", "HOMEDEVICEGROUPNAME", "CATEGORYID", "SERIALNUMBER", "FRIENDLYNAME", "HOMEDEVICEIMGFILENAME", "GROUPMEMBERID", "MODEMID", "INSTALLSTATUS", "INSTALLSTATUSCODE"] 
            }),
            groupField:'HOMEDEVICEGROUPNAME'
        });

       var storeRight = new Ext.data.GroupingStore({
           autoLoad: true,
           url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoRight.do?homeGroupId=" + homeGroupId,
           reader: new Ext.data.JsonReader({
               root:'result',
               fields: ["ID", "HOMEDEVICEGROUPNAME", "CATEGORYID", "SERIALNUMBER", "FRIENDLYNAME", "HOMEDEVICEIMGFILENAME", "MAPPINGFRIENDLYNAME", "MAPPINGIMGURL", "MAPPINGDRNAME", "MODEMID", "GROUPMEMBERID", "USAGE"] 
           }),
           groupField:'HOMEDEVICEGROUPNAME'
       });

       var colModelLeft = new Ext.grid.ColumnModel({
            columns: [
                      {id:'ID',header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.smartConcent'/>", dataIndex: 'HOMEDEVICEIMGFILENAME',tooltip:"<fmt:message key='aimir.hems.homeDeviceMgmt.tooltip.dragDevice'/>",//css:'background-color: #EEFFAA;',
                          renderer:  function(value, metaData) {
                                      if ( value !="") {
                                          var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                                          return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                                      }else{
                                          metaData.css ="x-grid-row-height";
                                      }
                                  }
                      },
                      {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/>", dataIndex: 'FRIENDLYNAME', renderer:addTooltip},
                      {header: "<fmt:message key='aimir.commstate'/>", dataIndex: 'INSTALLSTATUS', renderer:addTooltip},
                      {header: "", hidden: true, dataIndex: 'HOMEDEVICEGROUPNAME'}
                  ],
            defaults: {
                menuDisabled: true
                ,width: (leftGridWidth / 3)
            }
       });

         var colModelRight = new Ext.grid.ColumnModel({
              columns: [
                        {id:'ID',header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.img'/>", dataIndex: 'HOMEDEVICEIMGFILENAME',
                            renderer:  function(value, metaData) {
                                        if ( value !="") {
                                            var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                                            return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                                        }else{
                                            metaData.css ="x-grid-row-height";
                                        }
                            }
                         },
                        {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/>", dataIndex: 'FRIENDLYNAME', renderer:addTooltip},
                        {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.smartConcent'/>", dataIndex: 'MAPPINGIMGURL' ,tooltip:"<fmt:message key='aimir.hems.homeDeviceMgmt.tooltip.dropDevice'/>",   // MAPPINGIMGURL
                             renderer:  function(value, cell, record) {
                                 if ( value !="") {
                                     var tpl = new Ext.Template("<a href='javascript:resetMappingInfo("+record.data.ID+");'  title='<fmt:message key="aimir.hems.homeDeviceMgmt.tooltip.deleteMapping"/>'>" +
                                             "<img src='../../images/cross.gif' /></a>" + 
                                             "<img src='../../{MAPPINGIMGURL}' width='50px' height='50px' title='" +record.data.MAPPINGFRIENDLYNAME+"'></img>");
                                     return tpl.apply({MAPPINGIMGURL: value});
                                 }//else {
                                     //var tpl = new Ext.Template("Drop Home Device here!");
                                 //}
                             }
                        },

                        {header: "<fmt:message key='aimir.usage'/>(kWh)", dataIndex: 'USAGE'},
                        {header: "<fmt:message key='aimir.hems.label.levelDR'/>", dataIndex: 'MAPPINGDRNAME'},
                        {header: "", hidden: true, dataIndex: 'HOMEDEVICEGROUPNAME'}
                    ],
              defaults: {
                  menuDisabled: true
                  ,width: (rightGridWidth / 5)
              }
       });
          
      if (mappingGridOn == false) {
                gridLeft = new Ext.grid.GridPanel({
                title: 'Drag Zone',
                store: storeLeft,
                colModel : colModelLeft,
                plugins: new Ext.ux.CellFieldDragZone(),
                view: new Ext.grid.GroupingView({
                    deferEmptyText: false,
                    emptyText: "<fmt:message key='aimir.hems.homeDeviceMgmt.info.noRecords'/>",
                    //forceFit:true,
                    groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
                width: leftGridWidth,
                height: 500,
                stripeRows : true,
                loadMask: true,
                columnLines: true,
                listeners:{
                    cellclick:function( gridLeft, rowIndex, columnIndex, e){
                        var record = gridLeft.getStore().getAt(rowIndex); // rowIndex의get record
                        if(record.get("HOMEDEVICEIMGFILENAME") != ""){
                            setHomeDeviceInfo(record);
                        }                      
                    },
                    bodyscroll:function(x, y){
                            gridRight.getView().restoreScroll({ left: x, top: y }); 
                    }
                }
            });

            gridRight = new Ext.grid.GridPanel({
                title: 'Drop Zone',
                store: storeRight,
                colModel : colModelRight,
                plugins: new Ext.ux.CellFieldDropZone(),
                view: new Ext.grid.GroupingView({
                    deferEmptyText: false,
                    emptyText: "<fmt:message key='aimir.hems.homeDeviceMgmt.info.noRecords'/>",
                    //forceFit:true,
                    groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
                width: rightGridWidth,
                height: 500,
                stripeRows : true,
                loadMask: true,
                columnLines: true,
                listeners:{
                    cellclick:function( gridRight, rowIndex, columnIndex, e){
                        var record = gridRight.getStore().getAt(rowIndex); // rowIndex의get record
                        var fieldName = gridRight.getColumnModel().getColumnId(columnIndex); // columnIndex
                        var data = record.get(fieldName);
                        if(record.get("HOMEDEVICEIMGFILENAME") != ""){
                            setHomeDeviceInfo(record);
                        }
                    },
                    bodyscroll:function(x, y){
                            gridLeft.getView().restoreScroll({ left: x, top: y }); 
                    }
                }
            });
           // gridRight.getView().scrollOffset = 2;

            //Simple 'border layout' panel to house both grids
            displayPanel = new Ext.Panel({
                width        : width,
                height       : 400,
                layout       : 'hbox',
                defaults     : { flex : 1 }, //auto stretch
                layoutConfig : { align : 'stretch' },
                items        : [
                    gridLeft,
                    gridRight
                ],
                renderTo: 'homeDeviceMapping'
            });
            //gridRight.setPosition(381,0);
            gridRight.setPosition(gridLeft.getWidth() - 17,0);
            mappingGridOn = true;
      } else {
    	  gridLeft.setWidth(leftGridWidth);
    	  gridRight.setWidth(rightGridWidth);
    	  displayPanel.setWidth(width);
    	  gridRight.setPosition(gridLeft.getWidth() - 17,0);
          gridLeft.reconfigure(storeLeft, colModelLeft);
          gridRight.reconfigure(storeRight, colModelRight);
      } 
      $.ajaxSetup({ cache: true });    
      $.ajaxSetup({ async: true });
      hide();
    };

    var addTooltip = function(val, cell, record) {
        return '<div qtip="'+ val +'">'+ val +'</div>';     
    };
 
    // 콤보 박스 생성
    var array;
    var makeSelectbox = function() {

        $("#contractNumber").selectbox();
        $('#homeDeviceGroupName').selectbox();

        // 제품 유형 초기화
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '13'}
                , function (returnData){
                	array = returnData.code;
                    $('#homeDeviceCategory').pureSelect(returnData.code);
                    $('#homeDeviceCategory').selectbox();
        });
    };

    var imgWin;
    // 제품 등록탭 에서 이미지 변경 클릭 이벤트 START
    var changeHomeDeviceImg = function() {   
        // create the window on the first click and reuse on subsequent clicks
        var store = new Ext.data.JsonStore({
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceImgName.do" ,            
            root: 'images',
            fields: ['imgName','imgUrl']
        });
        store.load();

        var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="thumb-wrap" id="{imgName}">',
                    '<div class="thumb"><img src="../../{imgUrl}" ></div>',
                    //'<span class="x-editable">{imgName}</span></div>',
                    '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
        );
        
        if(!imgWin){
            imgWin = new Ext.Window({
                title: "<fmt:message key='aimir.hems.homeDeviceMgmt.title.changeImg'/>",
                id: 'imgWinId',
                applyTo:'images-view',
                autoScroll : true,
                width:350,
                height:370,
                closeAction: 'hide',
                items: new Ext.DataView({
                    store: store,
                    tpl: tpl,
                    autoHeight:true,
                    singleSelect: true,
                    overClass:'x-view-over',
                    itemSelector:'div.thumb-wrap',
                    emptyText: 'No images to display',
                    listeners: {
                        click: {
                            fn: function(dv,index,node, e){
                                homeDeviceImgUrl = dv.getRecord(node).get('imgUrl');
                                $("#homeDeviceImgView").html("<img src='../../"+homeDeviceImgUrl+"' width='80px' height='60px'>");
                                Ext.getCmp('imgWinId').hide();
                            }
                        }
                    }
                })
            });
        } // if(!imgWin) END

        // 이미지 변경 윈도우 표시
        Ext.getCmp('imgWinId').show();
    };
    // 제품 등록탭 에서 이미지 변경 클릭 이벤트 END

    // 중복 맵핑 체크
    var chkDuplicatedMapping = function(id, modemId, homeDeviceGroupName) {
           var storeRight = gridRight.getStore();
           var foundItem = storeRight.findExact('HOMEDEVICEGROUPNAME', homeDeviceGroupName);
           for ( var i = foundItem; i<storeRight.getCount(); i++)
           {
               var record = storeRight.getAt(i);
               if (record.get('HOMEDEVICEGROUPNAME') == homeDeviceGroupName && record.get('MODEMID') == modemId){
                   Ext.MessageBox.alert('Home Device Registration',  "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.connected'/>", function() {});
                   return false;
               }else if (record.get('HOMEDEVICEGROUPNAME') != homeDeviceGroupName){
                   return true;
               }
           }
           return true;
        };
    
    //  맵핑정보 갱신
    var updateMappingInfo = function(id, modemId, homeDeviceGroupName) {
       $.ajaxSetup({ cache: false });   
       var params = {
               "id" : id,
               "modemId" : modemId
       };

       $.getJSON("${ctx}/gadget/homeDeviceMgmt/updateMappingInfo.do",
               params,
               function(result) {
                    if (result.status == "SUCCESS") {
                        Ext.MessageBox.alert('Home Device Registration', "<fmt:message key='aimir.hems.homeDeviceMgmt.info.successMapping'/>", function(){getGridData();});
                    }else {
                        Ext.MessageBox.alert('Home Device Registration', "<fmt:message key='aimir.hems.homeDeviceMgmt.info.failMapping'/>", function(){ });
                    }
               }
       );
       $.ajaxSetup({ cache: true });
    };

    // 맵핑 해제시, 맵핑정보 리셋
    var resetMappingInfo = function(endDeviceId) {
        $.ajaxSetup({ cache: false }); 
        var params = {
                "endDeviceId" : endDeviceId
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/resetMappingInfo.do",
                params,
                function(result) {
                     if (result.status == "SUCCESS") {
                         Ext.MessageBox.alert('Home Device Registration', "<fmt:message key='aimir.hems.homeDeviceMgmt.info.disconnect'/>", function(){getGridData();});
                     }else {
                         Ext.MessageBox.alert('Home Device Registration', "<fmt:message key='aimir.hems.homeDeviceMgmt.info.failDisconnect'/>", function(){ });
                     }
                }
        );
        $.ajaxSetup({ cache: true });     
    };
</script>

</head>

<body>
    <div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span class="img_isNotService_elec_house"></span></div>
            <div class="isNotService_today_right">
                <table height='160'>
                <tr>    
                    <td><fmt:message key='aimir.hems.label.isNotService'/></td>
                </tr>
                </table>
            </div>
        </div>
    </div>
    <!-- (전체) Contract Tab (S) -->
    <div id="wrapper">
            <!-- 버튼 - 신규제품추가 (S) -->
            <div id="pane-Customer-Info-Button">
                <div id="btn">
                  <ul><li class="arrup"><a id="registerHomeDeviceForm" class="on-green-bold"><fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/></a></li></ul>
                </div>
            </div>
            <!-- 버튼 - 신규제품추가 (E) -->

            <!--contract no.-->
            <div id="contractUl" class="topsearch">
				<div class="contract borderbottom_blue">
	                <table>
		                <tr>
		                    <td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
		                    <td class="padding_r5">
		                        <select name="contractNumber" id="contractNumber" style="width:500px" onchange="javascript:changeContract();" >
		                            <c:forEach var="contract" items="${contracts}">
		                                <option value="${contract.id}">${contract.keyNum}</option>
		                            </c:forEach>
		                        </select>
		                    </td>
		                   <!-- <td><span class="hm_button"><span class="icon_excel"></span><a ><fmt:message key='aimir.button.excel'/></a></span></td>
		                     <td class="hm_button"><span class="icon_print"></span><a ><fmt:message key='aimir.report.print'/></a></td> -->
		                </tr>
	                </table>
            	</div>
            </div>
    
            <!-- (상단우측) 신규등록 및 상세정보 (S) -->
            <div class="max3_right"><!--  class="bodyright_customer"  -->
                <div class="title_basic"><span class="icon_title_blue"></span><span id='divProcessTitle'></span></div>
                <input type="hidden" name="id" id="id" />
                <input type="hidden" name="groupMemberId" id="groupMemberId" />
                <input type="hidden" name="modemId" id="modemId" />
                <input type="hidden" name="orgHomeDeviceGroupName" id="orgHomeDeviceGroupName" />
                <input type="hidden" id="orgHomeDeviceCategory" name="orgHomeDeviceCategory"  />
                <input type="hidden" id="orgIdentificationID" name="orgIdentificationID"  />
                <input type="hidden" id="orgFriendlyName" name="orgFriendlyName"  />
                <input type="hidden" id="installStatus" name="installStatus" />
 
                <table class="member_join">
                	<colgroup>
                		<col width="30%" />
                		<col width="" />
                	</colgroup>
                   <tr><th class="bluebold11pt"><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceGroup'/><em class="icon_star">&nbsp;</em></th>
                      <td>   
                       <span><select name="homeDeviceGroupName" id="homeDeviceGroupName"  style="width:140px" >                      
                         <c:forEach var="homeDeviceGroup" items="${homeDeviceGroupList}" varStatus="idx" >
                            <option value="${homeDeviceGroupList[idx.index]}">${homeDeviceGroupList[idx.index]}</option>
                         </c:forEach> 
                        </select></span>
                      </td>
                   </tr> 
                   <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceCategory'/><em class="icon_star">&nbsp;</em></th>
                          <td>
                          <div id="register-homeDevice-category"><select name="homeDeviceCategory" id="homeDeviceCategory" style="width:140px" onchange="javascript:changeRegisterForm(this.value);"></select></div>
                          <div id="modify-homeDevice-category">
                          <input type="text" id="_homeDeviceCategoryName" name="_homeDeviceCategoryName" disabled /></div>
                          </td>
                   </tr>
                   <tr id='idTR'><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.idenficationID'/><em class="icon_star">&nbsp;</em></th>
                          <td>
                          <!-- <span class="margin_r5"><input type="text" name="identificationID" id="identificationID" style="IME-MODE:disabled" onchange="javascript:changeID();" /></span> -->
                          <span class="margin_r5"><input type="text" name="identificationID" id="identificationID" style="IME-MODE:disabled" /></span>
                          <span class="hm_button" id="register-homeDevice-checkID"><a href="javascript:checkID();"><fmt:message key='aimir.hems.label.checkId'/></a></span>
                          </td>
                   </tr>                       
                   <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/><em class="icon_star">&nbsp;</em></th>
                          <td>                         
                          <span class="margin_r5"><input name="friendlyName" id="friendlyName" type="text" onchange="javascript:changeName();" /></span>
                          <span id='checkBTN' class="hm_button"><a href="javascript:checkName();"><fmt:message key='aimir.checkDuplication'/></a></span>
                          </td>
                   </tr>
                   <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.img'/></th>
                          <td><div id="homeDeviceImgView" class="imgview"></div>
                          <a  href="javascript:changeHomeDeviceImg();" class="btn_blue"><span><fmt:message key='aimir.hems.homeDeviceMgmt.btn.changeImg'/></span></a>
                          </td>
                   </tr>
                </table>

                <div class="align_right text_gray7"><fmt:message key='aimir.hems.inform.requiredField'/></div>


 				<!--우측 제품 정보 관리 버튼 (S) -->             
                <div class="rightbtn">
                  <div id="pane-HomeDevice-Register-Button">
                      <a id='registerHomeDevice' class="btn_blue" ><span><fmt:message key='aimir.button.register'/><!-- 등록 --></span></a>
                  </div>
                  <div id="pane-HomeDevice-Update-Button">
                      <a id='modifyHomeDevice' class="btn_blue"><span><fmt:message key='aimir.button.update'/><!-- 수정 --></span></a>
                      <a id='deleteHomeDevice' class="btn_blue"><span><fmt:message key='aimir.button.delete'/><!-- 삭제 --></span></a>
                  </div>                                 
                </div>
                <!--우측 제품 정보 관리 버튼 (E) -->

                <!--change homeDevice Image Window-->        
                <div id="images-view" class="x-hidden"></div>
                <!--//change homeDevice Image Window-->
            </div>
            <!-- (상단우측) 신규등록 및 상세정보 (E) -->      
            
             <!-- 스마트 콘센트 맵핑 그리드 (S)-->
            <div class="max3_left" >
                <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceList'/></div>
                <div id="homeDeviceMapping"></div>
            </div>                         
            <!--// 스마트 콘센트 맵핑 그리드 (E) -->


    </div>
    <!-- (전체) Contract Tab (E) -->   
</body>
</html>

