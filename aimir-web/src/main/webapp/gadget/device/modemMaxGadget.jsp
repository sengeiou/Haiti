<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>

    <meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Modem MaxGadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/css/jquery.cluetip.css" rel="stylesheet" type="text/css" />

    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .ui-dialog{
        	width: 1240px !important;
        	height: 580px !important;
        }
        .mvm-popwin-iframe{
        	width: 1200px !important;
        	height: 500px !important;
        }
        .btn_bluegreen, .btn_bluegreen a, .btn_bluegreen button, .btn_bluegreen input {
		    background-repeat:  repeat-x !important;
		}
		.meterinfo-textarea {
		    border:1px solid white;
		    height:75px;
		}
		.meterinfo-textarea textarea {
		    border:1px solid #b4d3f0;
		    background-color:white;
		    width:99%;
		    padding:10px;
		    height:150px;
		    LINE-HEIGHT: 1.3em;
		}
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">
	
    var asyncRowNo = "";
    var asyncDeviceId = "";
    var asyncTrid = "";
    var asyncState= "";
    var asyncCommand = "";
    var fcPieChartDataXml;
    var fcPieChart;

    // IP 유효성 판별 결과를 담는 변수
	var ipValidation = false;
    
    //공급사ID와 이름(이름으로 일부 기능 제한)
    var supplierId="${supplierId}";
    var supplierName="${supplierName}";

    //window width
    var browserWidth= "";
    var loginId       = "";
    var logType = "";
    var roleId = "";

    var mdsId;

    //선택된 미터 arrayList
    var selectedRows = new Array();
    var selectedRows2 = new Array();

    var mdsIds = new Array();
    var mdsIds2 = new Array();

    var meterList;

    var frUploadOthers;
    var frUploadSMS;

    var oldHwVersion = "";
    var oldFwVersion = "";
    var oldBuildNumber = "";
    var oldBinaryFileName = "";
    var newHwVersion = "";
    var newFwVersion = "";
    var newBuildNumber = "";
    var transferType = "";
    // 수정권한
    var editAuth = "${editAuth}";
    // Command권한 (Command Permission)
    var cmdAuth = "${cmdAuth}";

    var cmdLineWin;

    // EXTJS AJAX - Timeout setting (180 seconds)
    var extAjaxTimeout = 120000;
    var extAjaxTimeout2 = 60000;
    var getLogResult="";
 
    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0,
                    search_period:1, search_weekly:1,search_monthly:1};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
	
    var red = '#F31523'; // security error
    var orange = '#FC8F00'; // comm error
    var yellow = '#C1C115'; // NA48h
    var green = '#2A8B49' // NA24h
    var blue = '#0718D7'; // A24h
    var redbean = '#9A4A81' // unknown
    var setColorFrontTag = "<b style=\"color:";
    var setColorMiddleTag = ";\">"; 
    var setColorBackTag = "</b>";
    
    var tempDeviceId="";
    var tempModelName="";
    
    $.ajaxSetup({
        async: false
    });

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
        function(json) {
            if(json.supplierId != ""){
                //supplierId = json.supplierId;
                loginId = json.loginId;
            }
            if ( json.roleId != ""){
            	roleId = parseInt(json.roleId);
            }
        }
    );

    $(function() {
        Ext.QuickTips.init();
        // Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.override(Ext.grid.ColumnModel, {
            getColumnWidth : function(col) {
                var width = this.config[col].width;
                var stsize = 4;
                var chsize = stsize/this.config.length;

                if (typeof width != 'number') {
                    width = this.defaultWidth;
                }

                width = width - chsize;
                return width;
            }
        });

        // reset hidden field
        $("input[type=reset]").click(function(e) {
            $("input[type=hidden]").val('');
        });

        $(function() { $('#_general')  .bind('click',function(event) {
            modemDetailTab= "_general";
            $("#modemDetailTabValue").val(modemDetailTab);
            // hide 된 div width 는 0 으로 인식되므로 시간차를 두고 호출.
            window.setTimeout(function(){makeModemAddMeterGrid();}, 100);
        } ); });
        $(function() { $('#_schedule') .bind('click',function(event) {

            modemDetailTab= "_schedule";
            $("#modemDetailTabValue").val(modemDetailTab);
            var rtnValue = checkModemType();

            if(rtnValue == 1){
                $("#schedule").html("No Data to Show");
                return;
            }
            // after check modem type
            $("#schedule").load("${ctx}/gadget/device/modemMaxGadgetScheduleTab.do");
            // initialize flags : it used for 'SET request' handling.
            mtrIntervalFlag = false;
            retryCountFlag = false;
            $('#intervalUpdateResult').html("Update Result : ");

        } ); });
        $(function() { $('#_history')     .bind('click',function(event) {

            modemDetailTab= "_history";
            $("#modemDetailTabValue").val("_history");
        } ); });
        $(function() { 
        	$('#_locationInfo').bind('click',function(event) {
        		
                //탭정보 저장
                modemDetailTab= "_locationInfo";
                $("#modemDetailTabValue").val(modemDetailTab);
                
                checkModemId();
                
//                 var rtnValue = checkModemType();
//                 if(rtnValue == 1){
//                     return;
//                 }
            }
        ); });

        /* $(function() { $('#_commenv') .bind('click',function(event) {
            modemDetailTab= "_commenv";
            $("#modemDetailTabValue").val(modemDetailTab);

            var rtnValue = checkModemAndVersionType();

            if(rtnValue == 1){
                $('#commenv').hide();
                return;
            }else{
                $('#commenv').show();
            }
        } ); }); */
        
        $(function() { $('#_asyncHistory') .bind('click',function(event) {
            modemDetailTab= "_asyncHistory";
            $("#modemDetailTabValue").val(modemDetailTab);
            getAsyncHistoryGrid();
        } ); });


        $("#modemDetailTab").subtabs();

        // googleMap의 위치 정보를 수정함
        $('#modemDetailTab').bind('tabsshow', function(event, ui) {
            if (ui.panel.id == "locationInfo")
                viewModemMap();
        });

        // 검색조건
        $('#sModemType').selectbox();
        $('#sInstallState').selectbox();
        $('#sMcuType').selectbox();
        //$('#sModemSwVer').selectbox();
        //$('#sModemSwRev').selectbox();
        //$('#sModemHwVer').selectbox();
        $('#sModemStatus').selectbox();

        // 검색결과 조건
        $('#sOrder').selectbox();
        $('#sCountperPage').selectbox();
        $('#sCommState').selectbox();

        // 상세조회
        $('#meterType.id').selectbox();
        $('#model.deviceVendor.id').selectbox();
        $('#model.id').selectbox();
        $('#meterStatus.id').selectbox();

        // 스케쥴
        $('#lpPeriod').selectbox();
        $('#alarmFlag').selectbox();
        $('#lpChoice').selectbox();

        // 설정
        $('#commspeed').selectbox();
        $('#atcommand').selectbox();
        $('#sendingFlag1').selectbox();
        $('#sendingFlag2').selectbox();
        $('#vendor').selectbox();
        $('#model').selectbox();

        // 지역검색
        locationTreeGoGo('treeDiv_0', 'searchWord', 'sLocationId');
        
     	// Dialog
        $('#detail_dialog').dialog({
            autoOpen: false,
            resizable: false,
            //height: 800,
            //width: "1500px",
            iframe:true,
            modal: false,
            close: function() {
            	// reload from s.s
        		getModemAddMeterGrid();
          }
    	});
    });

    // onload
    $(document).ready(function(){
        if (editAuth == "true") {
            $("#modemInfoBtnList").show();
            $("#modemLocBtnList").show();
            $("#scheduleBtnList").show();
        } else {
            $("#modemInfoBtnList").hide();
            $("#modemLocBtnList").hide();
            $("#scheduleBtnList").hide();
        }

        if (cmdAuth == "true") {
            $("#modemCommand").show();
            //$("#settingPhoneBtn").show();
            $("#settingCommBtn").show();
            $("#settingGprsBtn").show();
            $("#settingSendBtn").show();
            $("#settingVendorBtn").show();
            $("#settingSntpBtn").show();
            $("#settingUploadBtn").show();
            $("#settingApnBtn").show();
            $("#settingSmsBtn").show();
        } else {
            $("#modemCommand").hide();
            $("#settingPhoneBtn").hide();
            $("#settingCommBtn").hide();
            $("#settingGprsBtn").hide();
            $("#settingSendBtn").hide();
            $("#settingVendorBtn").hide();
            $("#settingSntpBtn").hide();
            $("#settingUploadBtn").hide();
            $("#settingApnBtn").hide();
            $("#settingSmsBtn").hide();
        }

        var locDateFormat = "yymmdd";

        $("#sInstallStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sInstallEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

        $("#asyncCommandHistoryStartDate") .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#asyncCommandHistoryEndDate")   .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        
        $("#sLastcommStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );
        $("#sLastcommEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst); }} );

        
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
                    $("#asyncCommandHistoryStartDate").val(dateFullName);
                    $("#asyncCommandHistoryEndDate").val(dateFullName);
                });

        $("#asyncCommandHistoryStartDateHidden").val(setDate);
        $("#asyncCommandHistoryEndDateHidden").val(setDate);
        
        getmodemSearchGrid();
        getTopLeftModemSearchChartGrid();

        //조회 초기
        logType = "";

        //Extjs 윈도우 설정
        var details_form = new Ext.FormPanel({
            frame:true,
            defaultType: 'fieldset',
            bodyStyle:'padding:0px 0px 0px 0px',
            items: [{xtype: 'label',
                    fieldLabel : "<fmt:message key='aimir.msg.allfieldrequired'/>",
                     labelStyle: 'font-weight:bold; width : 200px; color:red;'
                     },
                    { title: '<fmt:message key="aimir.fw.sourceversioninfo"/>',
                        labelWidth : 150,
                        collapsible: true,
                          autoHeight:true,
                          bodyStyle:'padding:20px 0px 10px 10px',
                          defaultType: 'textfield',
                          items:[{ fieldLabel: "<fmt:message key='aimir.fw.sourcehw'/>",
                            autoHeight:true,
                            id   : 'oldHwVersion',
                            name : 'oldHwVersion',
                            collapsible: true
                        }, { fieldLabel: "<fmt:message key='aimir.fw.sourcefw'/>",
                            autoHeight:true,
                            id   : 'oldFwVersion',
                            name : 'oldFwVersion',
                            collapsible: true
                        }, { fieldLabel: "<fmt:message key='aimir.fw.sourcefwbuild'/>",
                            autoHeight:true,
                            id   : 'oldBuildNumber',
                            name : 'oldBuildNumber',
                            collapsible: true
                        }, { fieldLabel: '<fmt:message key="aimir.fw.binaryfilename"/>',
                            autoHeight:true,
                            id   : 'oldBinaryFileName',
                            name : 'oldBinaryFileName',
                            collapsible: true
                        }]
                    },
                    { title: '<fmt:message key="aimir.fw.targetversioninfo"/>',
                        labelWidth : 150,
                        collapsible: true,
                         autoHeight:true,
                         bodyStyle:'padding:20px 0px 10px 10px',
                         defaultType: 'textfield',
                         items:[{ fieldLabel: '<fmt:message key="aimir.fw.targethw"/>',
                            autoHeight:true,
                            id   : 'newHwVersion',
                            name : 'newHwVersion',
                            collapsible: true
                        }, { fieldLabel: '<fmt:message key="aimir.fw.targetfw"/>',
                            autoHeight:true,
                            id   : 'newFwVersion',
                                name : 'newFwVersion',
                            collapsible: true
                        }, { fieldLabel: '<fmt:message key="aimir.fw.targetfwbuild"/>',
                            autoHeight:true,
                            id   : 'newBuildNumber',
                                name : 'newBuildNumber',
                            collapsible: true
                        }]
                    },
                    {title: '<fmt:message key="aimir.fw.otainfo"/>',
                    labelWidth : 150,
                    collapsible: true,
                    autoHeight:true,
                    bodyStyle:'padding:20px 0px 10px 10px',  //top, right, bottom, left
                    defaultType: 'textfield',
                    items : [{ fieldLabel: '<fmt:message key="aimir.fw.instype"/>',
                            autoHeight:false,
                            id   : 'installType',
                            name : 'installType',
                            collapsible: true
                    }, { fieldLabel: '<fmt:message key="aimir.fw.transfertype"/>',
                        autoHeight:true,
                        id   : 'transferType',
                        name : 'transferType',// name : 폼데이터가 서버에 보내질때 매개변수 이름으로 사용
                        collapsible: true
                    }, { fieldLabel: '<fmt:message key="aimir.fw.otastep"/>',
                        autoHeight:true,
                        id   : 'otaStep',
                        name : 'otaStep',
                        collapsible: true
                    }, { fieldLabel: '<fmt:message key="aimir.fw.otacount"/>',
                        autoHeight:false,
                        id   : 'otaThreadCount',
                        name : 'otaThreadCount',
                        collapsible: true
                    }, { fieldLabel: '<fmt:message key="aimir.fw.retrycount"/>',
                        autoHeight:false,
                        id   : 'maxRetryCount',
                        name : 'maxRetryCount',
                        collapsible: true
                    }, { fieldLabel: '<fmt:message key="aimir.fw.multicastcount"/>',
                        autoHeight:false,
                        id   : 'multiWriteCount',
                        name : 'multiWriteCount',
                        collapsible: true
                    }, {xtype : 'radiogroup',
                        fieldLabel: '<fmt:message key="aimir.fw.difile"/>',
                        items: [{
                            id : 'yes',
                            name : 'diff',
                            boxLabel : 'Yes'
                        },{ id : 'no',
                            name : 'diff',
                            boxLabel : 'No',
                            checked : true
                        }]
                    }]
                }],
            buttons : [{text : '<font id="fwUploadOthers"><fmt:message key="aimir.ok"/></font>'
                    },{text : '<fmt:message key="aimir.cancel"/>',
                        handler : function() {
                            Ext.getCmp('transferType').reset();
                            Ext.getCmp('otaStep').reset();
                            Ext.getCmp('multiWriteCount').reset();
                            Ext.getCmp('maxRetryCount').reset();
                            Ext.getCmp('otaThreadCount').reset();
                            Ext.getCmp('installType').reset();
                            Ext.getCmp('newHwVersion').reset();
                            Ext.getCmp('newFwVersion').reset();
                            Ext.getCmp('newBuildNumber').reset();
                            Ext.getCmp('oldHwVersion').reset();
                            Ext.getCmp('oldFwVersion').reset();
                            Ext.getCmp('oldBuildNumber').reset();
                            Ext.getCmp('oldBinaryFileName').reset();
                            Ext.getCmp('yes').reset();
                            Ext.getCmp('no').reset();
                            Ext.getCmp('drAlertWinIdFWOptionPop').hide();
                        }
                    }]
        });

        var otaWin = new Ext.Window({
                title : '<fmt:message key="aimir.fw.upgradeoption"/>',
                id : 'drAlertWinIdFWOptionPop',
                applyTo : 'drAlertFWOptionPop',
                autoScroll : true,
                pageX : 500,
                pageY : 200,
                width : 350,
                height : 650,
                items : [details_form],
                closeAction : 'hide'
            });

        //펌웨어 업로드 버튼 설정
        frUploadOthers = new AjaxUpload('fwUploadOthers', {
            action: '${ctx}/gadget/device/command/cmd_Modem_Distribution.do',
            data : {
                loginId : loginId,
                modemId : modemId,
                supplierId : supplierId,
                otaStep : otaStep,
                multiWriteCount : '',
                maxRetryCount : '',
                otaThreadCount : '',
                installType : '',
                oldHwVersion : oldHwVersion,
                oldFwVersion : oldFwVersion,
                oldBuild : oldBuildNumber,
                oldBinaryFileName : oldBinaryFileName,
                newHwVersion : newHwVersion,
                newFwVersion : newFwVersion,
                newBuild : newBuildNumber,
                isDiff : false
            },
            responseType : 'json',
            onSubmit : function(file , ext){

                //파일 확장자 검색
                if (!(ext && /^(\ebl|EBL)$/.test(ext))){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.notebl"/>');
                    return false;
                }

                if (!checkFWOptionParam()) {
                    return false;
                }

                Ext.getCmp('drAlertWinIdFWOptionPop').hide();
                Ext.Msg.wait('Waiting for response.', 'Wait !');

                Ext.getCmp('transferType').reset();
                Ext.getCmp('otaStep').reset();
                Ext.getCmp('multiWriteCount').reset();
                Ext.getCmp('maxRetryCount').reset();
                Ext.getCmp('otaThreadCount').reset();
                Ext.getCmp('installType').reset();
                Ext.getCmp('newHwVersion').reset();
                Ext.getCmp('newFwVersion').reset();
                Ext.getCmp('newBuildNumber').reset();
                Ext.getCmp('oldHwVersion').reset();
                Ext.getCmp('oldFwVersion').reset();
                Ext.getCmp('oldBuildNumber').reset();
                Ext.getCmp('oldBinaryFileName').reset();
                Ext.getCmp('yes').reset();
                Ext.getCmp('no').reset();

                return true;
            },
            onComplete : function(file, response){
                Ext.Msg.hide();

                if(response.rtnStr!=undefined);
                    $('#commandResult').val(response.status+ " : "+response.rtnStr);
            }
        });

        frUploadSMS = new AjaxUpload('fwUploadSMS', {
            action: '${ctx}/gadget/device/command/smsFirmwareUpdate.do',
            data : {
                loginId : loginId,
                modemId : modemId,
                ext : 'dwl'
            },
            responseType : 'json',
            onSubmit : function(file , ext){
                //파일 확장자 검색
                if (!(ext && /^(dwl|DWL)$/.test(ext))){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.notdwl"/>');
                    return false;
                }

                Ext.Msg.wait('Waiting for response.', 'Wait !');

                return true;
            },
            onComplete : function(file, response){
                Ext.Msg.hide();

                if(response.rtnStr!=undefined);
                    $('#commandResult').val(response.status+ " : "+response.rtnStr);
            }
        });
        // Modem 기본정보 초기화면(등록화면) 호출
        init();
    });

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

   function modifyDateInit(setDate, inst){
       var dateId = '#' + inst;
       var dateHiddenId = '#' + inst + 'Hidden';
       $(dateHiddenId).val($(dateId).val());

       $.getJSON("${ctx}/common/convertLocalDate.do"
               ,{dbDate:setDate, supplierId:supplierId}
               ,function(json) {
                   $(dateId).val(json.localDate);
                   $(dateId).trigger('change');
               });
   }
   
    function fwOption() {
        if(modemId == ""){
            Ext.MessageBox.show({
                title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.alert.modemIsNotSelected'/>',
                icon : Ext.MessageBox.INFO
            });
        } else {
            Ext.getCmp('drAlertWinIdFWOptionPop').show();
        }
    }

    // F/W에 필요한 정보 값 체크
    function checkFWOptionParam() {
        transferType = Ext.getCmp('transferType').getValue();
        otaStep = Ext.getCmp('otaStep').getValue();
        multiWriteCount = Ext.getCmp('multiWriteCount').getValue();
        maxRetryCount = Ext.getCmp('maxRetryCount').getValue();
        otaThreadCount = Ext.getCmp('otaThreadCount').getValue();
        installType = Ext.getCmp('installType').getValue();
        newHwVersion = Ext.getCmp('newHwVersion').getValue();
        newFwVersion = Ext.getCmp('newFwVersion').getValue();
        newBuildNumber = Ext.getCmp('newBuildNumber').getValue();
        oldHwVersion = Ext.getCmp('oldHwVersion').getValue();
        oldFwVersion = Ext.getCmp('oldFwVersion').getValue();
        oldBuildNumber = Ext.getCmp('oldBuildNumber').getValue();
        oldBinaryFileName = Ext.getCmp('oldBinaryFileName').getValue();

        //필수 체크
        //값을 입력하지 않았거나 소수점 형식이 아닐경우 alert창 띄움
        if(oldHwVersion == "" || oldHwVersion == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.sourcehw'/>");
            $("#oldHwVersion").focus();
            return false;
        }
        if(oldFwVersion == "" || oldFwVersion == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.sourcefw'/>");
            $("#oldFwVersion").focus();
            return false;
        }
        if(oldBuildNumber == "" || oldBuildNumber == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.sourcefwbuild'/>");
            $("#oldBuildNumber").focus();
            return false;
        }
        //확장자도 써줘야한다.
        if(oldBinaryFileName == "" || oldBinaryFileName == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.binaryfilename'/>");
            $("#oldBinaryFileName").focus();
            return false;
        }  else if(!((/^(.[^\s]+)\.ebl$/.test(oldBinaryFileName)) || (/^(.[^\s]+)\.EBL$/.test(oldBinaryFileName)))){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.binaryfilename2'/>");
            $("#oldBinaryFileName").focus();
            return false;
        }
        if(newHwVersion == "" || newHwVersion == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.targethw'/>");
            $("#newHwVersion").focus();
            return false;
        }
        if(newFwVersion == "" || newFwVersion == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.targetfw'/>");
            $("#newFwVersion").focus();
            return false;
        }
        if(newBuildNumber == "" || newBuildNumber == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.targetbuild'/>");
            $("#newBuildNumber").focus();
            return false;
        }

        /**
         * 0 : AUTO, 같은 버전 설치 안함 (현재 버전이 New Version과 동일하면 설치 안함)
         * 1 : REINSTALL, 무조건 재설치, 설치 테스트 및 다시 내릴 경우, 버전이 같아도 내려짐.
         * 2 : MATCH, Old H/W, S/W, Build Version이 모두 동일하면 New Version으로 설치
         * 3 : FORCE, REINSTALL 처럼 무조건 재설치를 하고 File도 무조건 Base file을 다운 받는다
         */
        if(installType == "" || installType == null ||
                !(installType == 0 || installType == 1 || installType == 2 || installType == 3)) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.instype'/>");
            $("#installType").focus();
            return false;
        }

        /**
         * 0 : Auto, 50대 기준으로 Multicast/Unicast가 자동 결정됨
         * 1 : Multicast (펌웨어 전송만 Multicast, Verify, Install은 Unicast로 진행)
         * 2 : Unicast
         */
        if(transferType == "" || transferType == null ||
                !(installType == 0 || installType == 1 || installType == 2)) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.transfertype'/>");
            $("#transferType").focus();
            return false;
        }

        /**
         * 0x01 = 센서 정보 수집 및 버전 확인 (필수)
         * 0x02 = 파일 전송 (Data Send)
         * 0x04 = 전송 이미지 확인(Verify)
         * 0x08 = 설치 (Install)
         * 0x10 = 업그레이드된 버전 확인 (Scan)
         * ALL  = 0x1F (남은 비트는 확장 가능 영역으로 남김)
         */
        if(otaStep == "" || otaStep == null ||
                !(otaStep == "0x01" || otaStep == "0X01" ||
                otaStep == "0x02" || otaStep == "0X02" ||
                otaStep == "0x04" || otaStep == "0X04" ||
                otaStep == "0x08" || otaStep == "0X08" ||
                otaStep == "0x1f" || otaStep == "0X1F") ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.otastep'/>");
            $("#otaStep").focus();
            return false;
        }
        if(otaThreadCount == "" || otaThreadCount == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.otacount'/>");
            $("#otaThreadCount").focus();
            return false;
        }
        if(maxRetryCount == "" || maxRetryCount == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.retrycount'/>");
            $("#maxRetryCount").focus();
            return false;
        }
        if(multiWriteCount == "" || multiWriteCount == null) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.confirm.multicastcount'/>");
            $("#multiWriteCount").focus();
            return false;
        }

        frUploadOthers._settings.data.supplierId = supplierId;
        frUploadOthers._settings.data.transferType = transferType;
        frUploadOthers._settings.data.otaStep = otaStep;
        frUploadOthers._settings.data.multiWriteCount = multiWriteCount;
        frUploadOthers._settings.data.maxRetryCount = maxRetryCount;
        frUploadOthers._settings.data.otaThreadCount = otaThreadCount;
        frUploadOthers._settings.data.installType = installType;
        frUploadOthers._settings.data.oldHwVersion = oldHwVersion;
        frUploadOthers._settings.data.oldFwVersion = oldFwVersion;
        frUploadOthers._settings.data.oldBuild = oldBuildNumber;
        frUploadOthers._settings.data.oldBinaryFileName = oldBinaryFileName;
        frUploadOthers._settings.data.newHwVersion = newHwVersion;
        frUploadOthers._settings.data.newFwVersion = newFwVersion;
        frUploadOthers._settings.data.newBuild = newBuildNumber;
        frUploadOthers._settings.data.modemId = modemId;
        frUploadOthers._settings.data.isDiff = Ext.getCmp('yes').getValue();

        return true;
    }

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
            browserWidth= $(window).width();   // returns width of browser viewport
            //alert(browserWidth);

            //리싸이즈시 패널 인스턴스 kill & reload
            modemSearchGridPanel.destroy();


            //dataGapsMaxChartGridPanel;
            modemSearchGridInstanceOn = false;

            //draw modemSearchGrid to division
            getmodemSearchGrid();
    });

    //############################
    //MeterListGrid check 컬럼 정의
    //############################

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

    //##############################
    //#### button Events Start######
    //##############################

    function meterAdd() {
        ///gadget/device/getMeterListByNotModem
        //###매터에 등록 되지 않은 리스트를 가지고 온다.
        $("#modemAddMeterGridDiv").hide();
        $("#meterListByNotModemGridDiv").show();
        changeMeterButton("add");
        getMeterListByNotModemGrid();
    }// meterAdd Click End

    function meterOk() {
        //meterListByNotModemGridDiv.hide
        //$("#meterListByNotModemGridDiv").hide();

        //meterOkCancleBtn
        //$("#meterOkCancleBtn").hide();

        //##mdsIds의 갯수 만큼 모뎀에 meter를 등록.
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
                    mdsIds = new Array();
                },
                error:function(request, status) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meteraddfail'/>");
                    mdsIds = new Array();
                }
            }); // Ajax End
        }// for End

        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meteraddsucceed'/>");

        $("#meterListByNotModemGridDiv").hide();
        $("#modemAddMeterGridDiv").show();
        changeMeterButton("modify");

        //reload from s.s
        getModemAddMeterGrid();
    }

    function meterCancel() {
        $("#meterListByNotModemGridDiv").hide();
        $("#modemAddMeterGridDiv").show();
        changeMeterButton("modify");
    }

    function meterDelete() {
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
                        	//alert("미터 delete success");
                        	mdsIds2 = new Array();
                    	},
                    	error:function(request, status) {
                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meterdeletefail'/>");
                        	mdsIds2 = new Array();
                    	}
            		}); // Ajax End
        		} // for End
               	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meterdelete'/>");
				// $("#modemAddMeterGridDiv").hide();
        		$("#meterListByNotModemGridDiv").hide();
        		$("#modemAddMeterGridDiv").show();

        		// reload from s.s
        		getModemAddMeterGrid();
        	}else{ // btn == 'No'
		    	
		    }
		});
    }

    function changeMeterButton(state) {
        if (editAuth != "true") {
            return;
        }
        switch(state) {
            case "modify":
                $(".meter-info-btn3").empty().append("<ul id='meterDeleteUl'><li><a id='meterDelete' href='#;' onClick='meterDelete();' class='on'><fmt:message key='aimir.button.delete'/></a></li></ul>");
                $(".meter-info-btn3").append("<ul id='meterAddUl'><li><a id='meterAdd' href='#;' onClick='meterAdd();' class='on'><fmt:message key='aimir.add'/></a></li></ul>");
                break;
            case "add":
                $(".meter-info-btn3").empty().append("<ul id='meterCancelUl' style=''><li><a id='meterCancel' onclick='meterCancel();' href='#;' class='on'>&nbsp;&nbsp;<fmt:message key='aimir.cancel'/></a></li></ul>");
                $(".meter-info-btn3").append("<ul id='meterOkUl' style=''><li><a id='meterOk' onclick='meterOk();' href='#;' class='on'><fmt:message key='aimir.ok'/></a></li></ul>");
                break;
        }
    }

    //MeterListGridByNotModem propeties
    var meterListByNotModemGridInstanceOn = false;
    var meterListByNotModemGrid;
    var meterListByNotModemGridModel;

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
            mdsIds[i]= selectedRows[i].get('mdsId');
        }
    }

    //미터에 등록 되지 않은 미터 리스트/ row deselect 이벤트.)
    function funcRowDeselect2(selectionmodel, rowIdx, h) {
        var row= selectionmodel.getSelected();

        selectedRows= selectionmodel.getSelections();

        //reset array
        mdsIds = [];

        for(i = 0; i < selectedRows.length; i++) {
            mdsIds[i]= selectedRows[i].get('mdsId');
        }
    }

    //MeterListGridByNotModem method
    //미터에 등록 되지 않은 미터 리스트 fetch from S.S.
    function getMeterListByNotModemGrid() {
        //setting grid panel width
        var gridWidth = $("#meterListByNotModemGridDiv").width();

        //### meterListByNotModemGrid Store fetch
        var meterListByNotModemGridStore = new Ext.data.JsonStore({
            autoLoad: true,
            url: "${ctx}/gadget/device/getMeterListByNotModem.do",
            //파라매터 설정.
            baseParams: {
                supplierId:supplierId
            },
            root:'gridData',
            fields: [
                      "no"
                     , "mdsId"
                     ]
        });//Store End

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title
        fmtMessage[2] = "<fmt:message key="aimir.normal"/>";

        // meterListByNotModemGrid Model DEfine
        meterListByNotModemGridModel = new Ext.grid.ColumnModel({
            columns: [
                myCboxSelModel2
                ,{header: "no", dataIndex: 'no', width:50, align: 'center'}
                ,{header: "Meter", dataIndex: 'mdsId', width:(gridWidth-50), align: 'center'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        if (meterListByNotModemGridInstanceOn == false) {

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
                height: 120,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'meterListByNotModemGridDiv',
                viewConfig: {
                    forceFit:true,
                    enableRowBody:false,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                }
            });
            meterListByNotModemGridInstanceOn = true;
        } else {
            meterListByNotModemGridPanel.setWidth( gridWidth);
            meterListByNotModemGridPanel.reconfigure(meterListByNotModemGridStore, meterListByNotModemGridModel);
        }

        hide();
    }// End of getMeterListByNotModemGrid

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
                      makeModemAddMeterGrid();
                  });
    }

    var ModemAddMeterGridData = [];

    //modemAddMeterGrid propeties
    var modemAddMeterGridInstanceOn = false;
    var modemAddMeterGrid;
    var modemAddMeterGridModel;

    // 조회한 Data 로 Meter Grid 생성  
    function makeModemAddMeterGrid() {
        //setting grid panel width
        var gridWidth = $("#modemAddMeterGridDiv").width();

        //### modemAddMeterGrid Store fetch
        var modemAddMeterGridStore = new Ext.data.JsonStore({
            autoLoad: true,
            //url: "${ctx}/gadget/device/getMeterListByModem.do",
            data: ModemAddMeterGridData,
            //파라매터 설정.
            baseParams: {
                modemId:modemId
            },
            root:'gridData2',
            fields: [ "no", "mdsId", "gs1"]
        });//Store End

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title
        fmtMessage[2] = "<fmt:message key="aimir.normal"/>";

        // modemAddMeterGrid Model DEfine
        modemAddMeterGridModel = new Ext.grid.ColumnModel({
            columns: [
                //myCboxSelModel,
               {header: "no", dataIndex: 'no', width:50, align: 'center'}
               ,{header: "Meter", dataIndex: 'mdsId', width:(gridWidth-50)/2, align: 'center', editor:new Ext.form.TextField({allowBlank:false,width:200})}
               ,{header: "Meter SN", dataIndex: 'gs1', width:(gridWidth-50)/2, align: 'center', editor:new Ext.form.TextField({allowBlank:false,width:200})}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        if (modemAddMeterGridInstanceOn == false) {
            //Grid panel instance create
            modemAddMeterGridPanel = new Ext.grid.EditorGridPanel({
            	clicksToEdit: 1,
                store: modemAddMeterGridStore,
                colModel : modemAddMeterGridModel,
                //sm: myCboxSelModel,
                autoScroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 125,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'modemAddMeterGridDiv',
                viewConfig: {
                    //forceFit:true,
                    enableRowBody:false,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
            });
            modemAddMeterGridInstanceOn = true;
        } else {
            modemAddMeterGridPanel.setWidth( gridWidth);
            modemAddMeterGridPanel.reconfigure(modemAddMeterGridStore, modemAddMeterGridModel);
        }

        hide();
    };//func modemAddMeterGridList End

    //###############################
    //#######topLeftModemSearchChartGrid Start
    //###############################

    //topLeftModemSearchChartGrid propeties
    var topLeftModemSearchChartGridInstanceOn = false;
    var topLeftModemSearchChartGrid;
    var topLeftModemSearchChartGridModel;

    //topLeftModemSearchChartGrid
    function getTopLeftModemSearchChartGrid() {
        //setting grid panel width
        var gridWidth = $("#topLeftModemSearchChartGridDiv").width();
        
        var condArray = getCondition();
        var pageSize = 5;
        //### topLeftModemSearchChartGrid Store fetch
        var topLeftModemSearchChartGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getModemSearchChart.do",
            //파라매터 설정.
            baseParams: {
                sModemType:condArray[0]
                ,sModemId:condArray[1]
                ,sMcuType:condArray[4]
                ,sMcuName:condArray[5]
                ,sModemFwVer:condArray[6]
                ,sModemSwRev:condArray[7]
                ,sModemHwVer:condArray[8]
                ,sInstallStartDate:condArray[9]
                ,sInstallEndDate:condArray[10]
                ,sLastcommStartDate:condArray[11]
                ,sLastcommEndDate:condArray[12]
                ,sLocationId:condArray[13]
                ,sOrder:condArray[14]
                ,sCommState:condArray[15]
                ,supplierId :condArray[16]
                ,sModemStatus:condArray[17]
                ,pageSize :"10"
                ,gridType :"extjs"
               	,sMeterSerial:condArray[18]
           		,sModuleBuild:condArray[19]
                ,sGs1:condArray[20]
            
            },
            root:'gridData',
            totalProperty : 'totalCnt',
            listeners : {
                beforeload: function(store, options) {
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                }
            },
            fields: ["no", "mcuSysId", "value0", "value1", "value2", "value3", "value4", "value5"]
        });//Store End

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title
        fmtMessage[2] = "<fmt:message key="aimir.24within"/>";
        fmtMessage[3] = "<fmt:message key="aimir.24over"/>";
        fmtMessage[4] = "<fmt:message key="aimir.48over"/>";
        fmtMessage[5] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";
        fmtMessage[6] = "CommError"; 
        fmtMessage[7] = "SecurityError";

        var colWidth = (gridWidth - 50)/5;
        // topLeftModemSearchChartGrid Model DEfine
        topLeftModemSearchChartGridModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0],
                 tooltip: "<fmt:message key='aimir.number'/>",
                 dataIndex: 'no', width: 50, align: 'center'}
               ,{header: fmtMessage[1],
                 tooltip: "<fmt:message key='aimir.mcuid'/>",
                 dataIndex: 'mcuSysId', align: 'center'}
               ,{header: fmtMessage[2],
                 tooltip: "<fmt:message key='aimir.commstateGreen'/>",
                 dataIndex: 'value0', align: 'right'}
               ,{header: fmtMessage[3],
                 tooltip: "<fmt:message key='aimir.commstateYellow'/>",
                 dataIndex: 'value1', align: 'right'}
               ,{header: fmtMessage[4],
                 tooltip: "<fmt:message key='aimir.commstateRed'/>",
                 dataIndex: 'value2', align: 'right'}
               ,{header: fmtMessage[5],
                 tooltip: "<fmt:message key='aimir.bems.facilityMgmt.unknown'/>",
                 dataIndex: 'value3', align: 'right'}
               ,{header: fmtMessage[6] ,
                   tooltip: "CommError",
                   dataIndex: 'value4', align: 'right'}
               ,{header: fmtMessage[7],
                   tooltip: "SecurityError",
                   dataIndex: 'value5', align: 'right'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: colWidth
            }
        });

        if (topLeftModemSearchChartGridInstanceOn == false) {
            //Grid panel instance create
            topLeftModemSearchChartGridPanel = new Ext.grid.GridPanel({
                store: topLeftModemSearchChartGridStore,
                colModel : topLeftModemSearchChartGridModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            $('#sMcuName').val(param.mcuSysId);
                            $("#sCommState option:eq(0)").attr("selected", "selected");
                            $('#sCommState').selectbox();

                            $("#sOrder option:eq(0)").attr("selected", "selected");
                            $('#sOrder').selectbox();
                             getmodemSearchGrid();
                        }
                    }
                }),
                autoScroll:false,
                width:  gridWidth,
                height: 140,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'topLeftModemSearchChartGridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : pageSize,
                    store : topLeftModemSearchChartGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            topLeftModemSearchChartGridInstanceOn = true;
        } else {
            topLeftModemSearchChartGridPanel.setWidth( gridWidth);
            topLeftModemSearchChartGridPanel.reconfigure(topLeftModemSearchChartGridStore, topLeftModemSearchChartGridModel);
            var bottomToolbar = topLeftModemSearchChartGridPanel.getBottomToolbar();
            bottomToolbar.bindStore(topLeftModemSearchChartGridStore);
        }

        hide();
    };//func topLeftModemSearchChartGridList End

    //###############################
    //#######modemSearchGrid Start
    //###############################

    //modemSearchGrid propeties
    var modemSearchGridInstanceOn = false;
    var modemSearchGrid;
    var modemSearchGridModel;
    var modemSearchGridStore;
    //modemSearchGrid
    function getmodemSearchGrid() {

        //setting grid panel width
        //var gridWidth2 = ($(window).width()-490);
        var gridWidth2 = $("#modemSearchGridDiv").width();

        //row Count per page
        var rowSize = 10;

        var condArray = new Array();
        condArray= getCondition();

        //### modemSearchGrid Store fetch
        modemSearchGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize }},
            url: "${ctx}/gadget/device/getModemSearchGrid2.do",
            //파라매터 설정.
            baseParams: {
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
                ,supplierId:condArray[16]
            	,sMeterSerial:condArray[18] 
        		,sModuleBuild:condArray[19] 
                ,sGs1:condArray[20] 
                ,pageSize :"10"
                ,gridType :"extjs"
                ,modelId : "" // firmware management에서 사용
               	,fwGadget : 'N'
            },
            //Total Cnt
            totalProperty: "totalCnt",
            root:'gridData',
            fields: [
                      "idx"
                     ,"id"
                     ,"modemType"
                     ,"modemTypeCodeName"
                     ,"modemDeviceSerial"//modemID
                     ,"mcuSysId"                     //DCU ID
                     ,"vendorName"                     //VENDOR
                     ,"deviceName"//MODEL
                     ,"ver"
                     ,"lastCommDate"
                     ,"activityStatus"
                     ,"modemStatusByCode"
                     ,"locationId"
                     ,"module"
                     ],

            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
        });//Store End

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.modemid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.modem.type"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";
        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.fw.hw.ver"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lastcomm"/>";
        fmtMessage[8] = "<fmt:message key="aimir.module.version.build"/>";

        var colWidth = (gridWidth2-50)/8;
        // modemSearchGrid Model DEfine
        modemSearchGridModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0], dataIndex: 'idx', width: 50, align: 'center'}
               ,{header: fmtMessage[1], dataIndex: 'modemDeviceSerial', align: 'center',
            	 renderer : function(value, me, record, rowNumber, columnIndex, store) {
            		 		if(record.data.modemStatusByCode == "1.2.7.5") //Security Error
                				return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                			if(record.data.modemStatusByCode == "1.2.7.6") //Comm Error
                				return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
            		 		if(record.data.activityStatus == "A24h") 
                       			return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                       		else if(record.data.activityStatus == "NA24h")
                       			return setColorFrontTag + green + setColorMiddleTag + value + setColorBackTag;
                       		else if(record.data.activityStatus == "NA48h")
                       			return setColorFrontTag + yellow + setColorMiddleTag + value + setColorBackTag;
                       		else if(record.data.activityStatus == "unknown")
                       			return setColorFrontTag + redbean + setColorMiddleTag + value + setColorBackTag;
               				}
                }
               ,{header: fmtMessage[2], dataIndex: 'modemType', align: 'center'}
               ,{header: fmtMessage[3], dataIndex: 'mcuSysId', align: 'center'}
               ,{header: fmtMessage[4], dataIndex: 'vendorName'}
               ,{header: fmtMessage[5], dataIndex: 'deviceName'}
               ,{header: fmtMessage[6], dataIndex: 'ver'}
               ,{header: fmtMessage[8], dataIndex: 'module'}
               ,{header: fmtMessage[7], dataIndex: 'lastCommDate'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: colWidth
            }
        });

        if (modemSearchGridInstanceOn == false) {
            //Grid panel instance create
           modemSearchGridPanel = new Ext.grid.GridPanel({
                store : modemSearchGridStore,
                colModel : modemSearchGridModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                        	$("#modemDetailTab").show();
                        	var param = value.data;
                        	rowClickEvent(selectionModel);
                        	tempDeviceId=param.modemDeviceSerial;
                            tempModelName=param.deviceName;
                            tempLocId=param.locationId;
                        }
                    }
                }),
                autoScroll : false,
                scroll : false,
                width : gridWidth2,
                style : 'align:center;',
                height : 295,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                //랜더링 디비전
                renderTo : 'modemSearchGridDiv',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : '<fmt:message key="aimir.extjs.empty"/>'
                },
                // paging bar on the bottom
                bbar : new Ext.PagingToolbar({
                    pageSize : rowSize,
                    store : modemSearchGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            modemSearchGridInstanceOn = true;
        } else {
            modemSearchGridPanel.setWidth( gridWidth2);
            var bottomToolbar = modemSearchGridPanel.getBottomToolbar();
            modemSearchGridPanel.reconfigure(modemSearchGridStore, modemSearchGridModel);
            bottomToolbar.bindStore(modemSearchGridStore);
        }

        hide();

    };//func modemSearchGridList End

    //rowclick Event Click Event 리스너
    function rowClickEvent(selectionModel) {
        var s= selectionModel;
        var row = s.getSelected();

        var modemDeviceSerial = row.get('modemDeviceSerial');

        var id= row.get('id');

        var modemType= row.get('modemTypeCodeName');
        var deviceSerial= row.get('modemDeviceSerial');

        //모뎀 id setting
        modemId= id;
        mdsId= id;

        setModemId( id, modemType, deviceSerial);

        //tab info save
        var modemDetailTab = $("#modemDetailTabValue").val();
        if(typeof(cmdLineWin) != 'undefined'){
            cmdLineWin.close();            
        }
        
    }

    //anRowColCount 만큼만 한줄에 표시되도록 source 테이블을  재정렬한다.
    var reSortTable = function(source,anRowColCount) {

        //테이블 내용 백업
        var t = $('#'+source);
        var tableBk = t.find('tr:first').clone();
        var eCount = tableBk.find('td').length;
        var option = anRowColCount;

        //테이블 내용 삭제
        t.empty();

        for(var i=0;i<eCount;i++){
            if(i==0||i%option==0){
                t.append("<tr></tr>");
            }
            var tr = t.find("tr:last");
            var th = tableBk.find("th:first");
            var td = tableBk.find("td:first");

            tr.append(th);
            tr.append(td);
        }
        tableBk.remove();
        t.show();
    };

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

    var modemId   = '';                // 선택된 modemId
    var modemType = '';                // 선택된 modemType
    var logGrid   = '';                // 선택된 logGrid
    var deviceSerial = '';             //

    // 검색된 모뎀클릭
    function setModemId(gridModemId, gridModemType, gridDeviceSerial) {
        modemId      = gridModemId;
        modemType    = gridModemType;
        deviceSerial = gridDeviceSerial;

        getModem();

        if(modemType == "LTE"){    // EMnV System LTE : *프로토콜타입이 SMS인것만 먹음.
            $("#command").show();
            $("#lpLog").hide();
            $("#eventHistoryLog").hide();
            $("#unitScanning").hide();
            $("#monitoring").hide();
            $("#FwUploadSMS").hide();
            //$("#cmdLine").hide();
        }else{
            var modelName = $('#modelName').val();

            if (modelName != null && modelName == "NAMR-P114GP_MX2") {
                $("#command").show();
                $("#lpLog").hide();
                $("#eventHistoryLog").hide();
                $("#unitScanning").hide();
                $("#monitoring").hide();
            }  else if(modelName != null && modelName.length > 12 && modelName.substring(0, 11) == "NAMR-P114EC") {
                $("#command").hide();
            }  else {
                $("#command").show();
                $("#lpLog").show();
                $("#eventHistoryLog").show();
                $("#unitScanning").show();
                $("#monitoring").show();
            }         
        }        

        // Modem에 소속된 meter조회
        //getMeterListByModem();

        //선택된 모뎀이 바뀔때마다 파일 업로드 파라미터 갱신.
        frUploadSMS._settings.data.modemId = modemId;
    }

    function getModemId(){
        return modemId;
    }

    //
    /* function getMeterListByModem(){
        flexModemAddMeter.requestSendMeterListByModem();
    } */

    // modemId가 비었을때 선택불가
    function checkModemId() {
       if (modemId == "") {
           Ext.MessageBox.show({
                title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.alert.modemIsNotSelected'/>',
                icon : Ext.MessageBox.INFO
            });
           return false;
       }
       return true;
    }

    function getCondition() {
        var arrayObj = Array();

        arrayObj[0]  = $('#sModemType').val();
        arrayObj[1]  = $('#sModemId').val();

        arrayObj[4]  = $('#sMcuType').val();
        arrayObj[5]  = $('#sMcuName').val();
        arrayObj[6]  = $('#sModemFwVer').val();
        arrayObj[7]  = $('#sModemSwRev').val();
        arrayObj[8]  = $('#sModemHwVer').val();

        arrayObj[9]  = $('#sInstallStartDateHidden') .val();
        arrayObj[10] = $('#sInstallEndDateHidden')   .val();

        arrayObj[11] = $('#sLastcommStartDateHidden') .val();
        arrayObj[12] = $('#sLastcommEndDateHidden')   .val();
        arrayObj[13] = $('#sLocationId').val();

        arrayObj[14] = $('#sOrder').val();
        arrayObj[15] = $('#sCommState').val();

        arrayObj[16] = supplierId;
        arrayObj[17] = $('#sModemStatus').val();
		arrayObj[18] = $('#sMeterSerial').val();
		arrayObj[19] = $('#sModuleBuild').val();
		arrayObj[20] = $('#sGs1').val();
		
        return arrayObj;
    }

    //검색 버튼 클릭시 실행 event
    function searchModem() {
        //modemId = "";

        if(Number($('#sInstallStartDateHidden').val()) > Number($('#sInstallEndDateHidden').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Error: Invalid <fmt:message key='aimir.installdate'/> value");
            return;
        }
        if(Number($('#sLastcommStartDateHidden').val()) > Number($('#sLastcommEndDateHidden').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Error: Invalid <fmt:message key='aimir.lastcomm'/> value");
            return;
        }

        //바차트 draw
        updateFChart();
        //왼쪽 그리드
        getTopLeftModemSearchChartGrid();

        //modemSearchGrid 랜더링 to div
        getmodemSearchGrid();
    }

     //핕터링 selectbox change event-1
    $("#sCommState").live("change", function() {
        //recall grid with new condition
        getmodemSearchGrid();
    });

    //핕터링 selectbox change event-2
    $("#sOrder").live("change", function() {
        //recall grid with new condition
        getmodemSearchGrid();
    });

    /* function viewLogGrid(viewlogGrid) {
        logGrid = viewlogGrid;

        flexModemLogGrid.requestSend();
    } */

    function getViewEachLogType() {
        return logGrid;
    }

    function getSearchChart() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";       // Grid Title
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";         // Grid Title

        return fmtMessage;
    }

    function getFmtMessageCommAlert() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.24within"/>";
        fmtMessage[1] = "<fmt:message key="aimir.24over"/>";
        fmtMessage[2] = "<fmt:message key="aimir.48over"/>";

        return fmtMessage;
    }

    function getSearchGridColumn() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.modemid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.modem.type"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";
        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.fw.hw.ver"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lastcomm"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "modemDeviceSerial";
        dataFild[2] = "modemType";
        dataFild[3] = "mcuSysId";
        dataFild[4] = "vendorName";

        dataFild[5] = "deviceName";
        dataFild[6] = "ver";
        dataFild[7] = "lastCommDate";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "left";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "left";

        gridAlign[5] = "left";
        gridAlign[6] = "center";
        gridAlign[7] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "400 ";
        gridWidth[1] = "1300";
        gridWidth[2] = "800";
        gridWidth[3] = "800";
        gridWidth[4] = "800";
        gridWidth[5] = "1000";
        gridWidth[6] = "900 ";
        gridWidth[7] = "1300";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;

        return dataGrid;
    }

    function getLogCondition() {

        var arrayObj = Array();

        arrayObj[0]  = modemId;

        var searchStartDate = $('#searchStartDate').val();
        var searchEndDate = $('#searchEndDate').val();

        //처음 로드시에 날짜가 널일 경우 오늘 날짜로 설정.
        if (searchStartDate == "" && searchEndDate == "") {
            searchStartDate= getToday();
            searchEndDate = getToday();
            searchStartHour="00";
            searchEndHour="23";
        }

        arrayObj[1] = searchStartDate;
        arrayObj[2] = searchEndDate;

        arrayObj[3]  = logGrid;

        return arrayObj;
    }


    //오늘 날짜를 구한다.,
    function getToday() {

        var currentTime = new Date();
        var month = (currentTime.getMonth() + 1);
        var day = currentTime.getDate();//-8
        var year = currentTime.getFullYear();

        //alert( day.toString().length );
        if (day.toString().length == 1) {
            day = "0" + day.toString();
        }

        if (month.toString().length == 1) {
            month = "0" + month.toString();
        }

        var today = year.toString() + month.toString() + day.toString();

        return today;
    }

    // commonDateTabButtonType.jsp 실행
    function send() {
        updateFColumnChart();
//        flexModemLogChart.requestSend();
    }

    function getLogChart() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.commlog"/>";
        fmtMessage[1] = "<fmt:message key="aimir.changehistory"/>";
        fmtMessage[2] = "<fmt:message key="aimir.alerthistory"/>";
        fmtMessage[3] = "<fmt:message key="aimir.view.operationlog"/>";
        fmtMessage[4] = "<fmt:message key="aimir.sensor.battlog"/>";

        var dataFild = new Array();
        dataFild[0] = "commLog";
        dataFild[1] = "updateLog";
        dataFild[2] = "brokenLog";
        dataFild[3] = "operationLog";
        dataFild[4] = "modemPowerLog";

        var chartColumn = new Array();
        chartColumn[0] = fmtMessage;
        chartColumn[1] = dataFild;

        return chartColumn;
    }

    function getLogGridComm() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.receiveTime"/>";
        fmtMessage[2] = "<fmt:message key="aimir.datatype"/>";
        fmtMessage[3] = "<fmt:message key="aimir.status"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "receiveTime";
        dataFild[2] = "dataType";
        dataFild[3] = "status";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridUpdate() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.time"/>";
        fmtMessage[2] = "<fmt:message key="aimir.operator"/>";
        fmtMessage[3] = "<fmt:message key="aimir.attribute"/>";
        fmtMessage[4] = "<fmt:message key="aimir.currentvalue"/>";
        fmtMessage[5] = "<fmt:message key="aimir.beforevalue"/>";

        var dataFild = new Array();
        dataFild[0] = "number";
        dataFild[1] = "time";
        dataFild[2] = "operator";
        dataFild[3] = "attribute";
        dataFild[4] = "currentvalue";
        dataFild[5] = "beforevalue";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridBroken() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.majorValue"/>";
        fmtMessage[1] = "<fmt:message key="aimir.number"/>";
        fmtMessage[2] = "<fmt:message key="aimir.message"/>";
        fmtMessage[3] = "<fmt:message key="aimir.address"/>";
        fmtMessage[4] = "<fmt:message key="aimir.location"/>";
        fmtMessage[5] = "<fmt:message key="aimir.gmptime"/>";
        fmtMessage[6] = "<fmt:message key="aimir.closetime"/>";
        fmtMessage[7] = "<fmt:message key="aimir.duration"/>";

        var dataFild = new Array();
        dataFild[0] = "majorValue";
        dataFild[1] = "number";
        dataFild[2] = "message";
        dataFild[3] = "address";
        dataFild[4] = "location";
        dataFild[5] = "gmptime";
        dataFild[6] = "closetime";
        dataFild[7] = "duration";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";
        gridAlign[6] = "center";
        gridAlign[7] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";
        gridWidth[6] = "1000";
        gridWidth[7] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridCommand() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.time"/>";
        fmtMessage[2] = "<fmt:message key="aimir.targetType"/>";
        fmtMessage[3] = "<fmt:message key="aimir.operator"/>";
        fmtMessage[4] = "<fmt:message key="aimir.board.running"/>";
        fmtMessage[5] = "<fmt:message key="aimir.status"/>";
        fmtMessage[6] = "<fmt:message key="aimir.description"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "logTime";
        dataFild[2] = "targetType";
        dataFild[3] = "operator";
        dataFild[4] = "operatorType";
        dataFild[5] = "status";
        dataFild[6] = "description";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";
        gridAlign[6] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";
        gridWidth[6] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getAddMeterGrid() {

        var fmtMessage = new Array();
        fmtMessage[0]  = "<fmt:message key="aimir.number"/>";
        fmtMessage[1]  = "<fmt:message key="aimir.meter"/>";

        var dataFild = new Array();
        dataFild[0]  = "no";
        dataFild[1]  = "mdsId";

        var gridAlign = new Array();
        gridAlign[0]  = "center";
        gridAlign[1]  = "center";

        var gridWidth = new Array();
        gridWidth[0]  = "100";
        gridWidth[1]  = "300";

        var gridColumn = new Array();
        gridColumn[0]  = fmtMessage;
        gridColumn[1]  = dataFild;
        gridColumn[2]  = gridAlign;
        gridColumn[3]  = gridWidth;

        return gridColumn;
    }

    // modemInfo에서 사용하는 JS 

    // vendor에 따른 model조회
    function getModelListByVendor(setState) {

        if ($('#deviceVendor').val() != "")
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do'
            , {'vendorId' : $('#deviceVendor').val() }
            , function (returnData){
                $('#modelId').noneSelect(returnData.deviceModels);
                $('#modelId').selectbox();

                // getMeter등의 정보로 Model을 설정해야 하는 경우 set을 표시함
                if(setState == 1){
                    $('#modelId').option($('#modelIdHidden').val());
                    $('#modelIdView').val($('#modelId option:selected').text());
                }
            });
    }

    // 모뎀 등록
    var insertModem = function() {
    	// 프로토콜 타입이 IP, SMS일 경우 입력받은 IPv4/IPv6의 형식을 check (S)
    	var selectedValue = $('#protocolType option:selected').text();
    	if(selectedValue == "IP" || selectedValue == "SMS" || selectedValue == "GPRS"){
    		checkIpAddress($("#ipAddr").val(), $("#ipv6Address").val());
    		
    		if(ipValidation == false) {
    			return false;
    		}
    	}
		// 입력받은 IPv4/IPv6의 형식을 check (E)
    	
        if ($('#modemId').val() == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputModemid'/>");
            $('#modemId').select();
            return;
        }

        if (!modemIdValidation()) {     // Modem ID 중복체크
            $('#modemId').select();
            return;
        }

        if ($('#protocolType option:selected').text() == '-') {
          Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.select.protocoltype"/>');
          return;
        }
        if ($('#deviceVendor option:selected').text() == '-') {
          Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.select.devicevendor"/>');
          return;
        }
        if ($('#modelId option:selected').text() == '-') {
          Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.select.devicemodel"/>');
          return;
        }
        
        function checkIpAddress(ipv4, ipv6) {
        	$.ajax({
        	  url: '${ctx}/gadget/device/command/checkIpAddress.do',
        	  dataType: 'json',
        	  async: false,
        	  data: {'ipv4' : ipv4,
          			'ipv6' : ipv6
        			},
        	  success: function(returnData) {
    			if(returnData.status != "SUCCESS") {
    				Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
    				ipValidation = false;
    				return;
    			} else{
    				ipValidation = true;
    			}
        	  }
        	});
        }

        $("#modemInfoForm :input[id='supplierId']").val(supplierId);
        $("#modemInfoForm :input[id='modemTypeName']").val($('#modemType option:selected').val());
        $("#modemInfoForm :input[id='protocolTypeName']").val($('#protocolType option:selected').get(0).id);

        $("#modemInfoForm :input[id='hwVerName']").val($('#hwVersion').val());
        $("#modemInfoForm :input[id='fwVerName']").val($('#fwVersion').val());
        
        var chkModemId = $.trim($("#modemInfoForm :input[id='modemId']").val());
        $("#modemInfoForm :input[id='modemId']").val(chkModemId);
        
        if(chkModemId == null || chkModemId == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.inputid'/>");
            $("#modemInfoForm :input[id='modemId']").val('');
            $("#modemInfoForm :input[id='modemId']").focus();
            return;
        } else {
            $.getJSON('${ctx}/gadget/device/isModemDuplicateByDeviceSerial.do'
                    , {  'deviceSerial': chkModemId}
                    , function (returnData){
                    	if(returnData.result == "delete") {
                    		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                            $("#modemInfoForm :input[id='modemId']").val('');
                            $("#modemInfoForm :input[id='modemId']").focus();
                            return;
                    	} else if(returnData.result == "true"){
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.alreadyExist'/>");
                            $("#modemInfoForm :input[id='modemId']").val('');
                            $("#modemInfoForm :input[id='modemId']").focus();
                            return;
                        }
                    });
        }

        var modemType = $('#modemType option:selected').val();
        var params = "";

        var url = "${ctx}/gadget/device/insertModem" + modemType + ".do";
		params = {
			success : insertModemResult,
			url : url,
			type : 'post',
			datatype : 'application/json'
		};
        
        $('#modemInfoForm').ajaxSubmit(params);

        $('#modemId').val('');
        $('#mcuId').val('');
        initModemInfoDiv();

    };

    // Modem ID 중복확인. true : 정상, false : 중복
    function modemIdValidation() {
        var valid = false;
        if ($('#modemId').val() == null || $('#modemId').val() == "") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.inputModemid'/>");
            valid = false;
        } else {
            var params = {
                deviceSerial:$('#modemId').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/device/isModemDuplicateByDeviceSerial.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("returnData=" + jsonText);

            if (returnData.result == "true") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.dupmodemid'/>");
                valid = false;
            } else if (returnData.result == "delete") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.cannot.use'/>");
                valid = false;            	
            } else {
                valid = true;
            }
        }
        return valid;
    }

    //미터 리스트를 임시 저장한다.
    function setMeterList(params) {
        if (params != null) {
            meterList = params;
        }
    }

    // 모뎀 등록 후처리
    function insertModemResult(responseText, status) {

        var modemId      = responseText.id;
        var modemType    = $('#modemType option:selected').val();
        var deviceSerial = $('#modemId').val();

        //모뎀 추가 전에 등록한 미터가 있을경우 모뎀에 미터를 추가한다.
        if (meterList != null) {
            //미터등록
            var param = {mdsId : meterList, modemId:modemId};
            $.ajax({
                type : "POST",
                url: "${ctx}/gadget/device/setModemId.do",
                data : param,
                success:function(json){
                    if (!json.result)
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.meteraddfail'/>");

                    var tempMsg = ' <fmt:message key="aimir.msg.insertsuccess" />';
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',deviceSerial + tempMsg.substring(tempMsg.lastIndexOf("_") + 1));

                    // 목록조회
                    getmodemSearchGrid();

                    // 등록됨 모뎀 상세 조회
                    setModemId(modemId, modemType, deviceSerial);
                },
                dataType:"json",
                traditional:true
            });
        } else {
            var tempMsg = '<fmt:message key="aimir.msg.insertsuccess" />';
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',deviceSerial + tempMsg.substring(tempMsg.lastIndexOf("_") + 1));

            // 목록조회
            getmodemSearchGrid();

            // 등록됨 모뎀 상세 조회
            setModemId(modemId, modemType, deviceSerial);
        }

        meterList = null;
    }

    // 모뎀 조회
    function getModem() {
        var params = {  "modemId"   : modemId,
                        "modemType" : modemType,
                        "supplierId" : supplierId
                     };

        $("#modemInfoDiv").load("${ctx}/gadget/device/getModemByType.do", params);
        meterCancel();
        getModemAddMeterGrid();
        getAsyncHistoryGrid();
    }
	
    function getModemForGetInfo(info) {
        var params = {  "modemId"   : modemId,
                        "modemType" : modemType,
                        "supplierId" : supplierId
                     };

        $("#modemInfoDiv").load("${ctx}/gadget/device/getModemByType.do", params);
        meterCancel();
        getModemAddMeterGrid();
        $('#commandResult').val("[Modem Info] \n"+info);
    }
   
    var initModemInfoState = 0;
    function initModemInfoDiv() {
        initModemInfoState = 0;

        // 모뎀 타입
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.2.1'}
                , function (returnData){
                    var moemTypeCode = returnData.code;
                    var moemTypeCodeArr = Array();
                    for (var i = 0; i < moemTypeCode.length; i++) {
                        var obj = new Object();
                        obj.name=moemTypeCode[i].descr;
                        obj.id=moemTypeCode[i].name;
                        moemTypeCodeArr[i]=obj;
                    };
                    $('#modemType').pureSelect(moemTypeCodeArr);
                    $('#modemType').selectbox();
                    initModemInfoState++;
                });

        // 제조사 조회
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData) {
                    $('#deviceVendor').noneSelect(returnData.deviceVendors);
                    $('#deviceVendor').selectbox();
                    initModemInfoState++;
                });

        // 모델 초기화
        $('#modelId').emptySelect();
        $('#modelId').selectbox();
        
        initModemInfoState++;

        // protocol Ver
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '4.6'}
                , function (returnData) {

                  $('option', $('#protocolType')).remove();
                  $('#protocolType').append("<option value='-' id='-'>-</option>");
                    $.each(returnData.code, function(index, ProtocolVer){
                        $('#protocolType').append("<option value='"
                            +ProtocolVer['id'] + "' id='"+ProtocolVer['name']+"' "
                            +">"+ProtocolVer['descr']+"</option>");
                    });

                    $('#protocolType').selectbox();
                    initModemInfoState++;
                });
        
        // modemStatus Ver
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.2.7'}
                , function (returnData) {
                  $('option', $('#modemStatusEdit')).remove();
                  $('#modemStatusEdit').append("<option value='-' id='-'>-</option>");
                    $.each(returnData.code, function(index, ModemStatus){
                        $('#modemStatusEdit').append("<option value='"
                            +ModemStatus['id'] + "' id='"+ModemStatus['name']+"' "
                            +">"+ModemStatus['descr']+"</option>");
                    });

                    $('#modemStatusEdit').selectbox();
                    initModemInfoState++;
                });
    }

    // json이 완료 대기
    function infoDelay() {
        if (initModemInfoState == '5') {
            //clearInterval(infoDelayInterval);
            for (var i = 1; i <= infoDelayInterval; i++)
            	clearInterval(i);

            // 모뎀 기본 정보 조회
            modemInfoDefaultLoad();

            // 모뎀 추가 정보 조회
            modemInfoDetailLoad();

            // 버튼 변경
            changeModemInfoDiv('view');
        }
    }
	
    // 모뎀 공통정보 설정
    function modemInfoDefaultLoad() {
        $('#modemId').val($('#modemIdHidden').val());
        $('#modemIdView').val($('#modemIdHidden').val());

        $('#modemType').val($('#modemTypeHidden').val());
        $('#modemType').selectbox();
        $('#modemTypeView').val($('#modemType option:selected').text());

        $('#mcuId').val($('#mcuIdHidden').val());
        $('#mcuIdView').val($('#mcuIdHidden').val());

        $('#hwVersion').val($('#hwVersionHidden').val());
        $('#hwVersionView').val($('#hwVersionHidden').val());

        $('fwVersion').val($('fwVersionHidden').val());
        $('fwVersionView').val($('fwVersionHidden').val());
        
        $('#protocolVersionView').val($('#protocolVersionHidden').val());
        
        /* if ($('#protocolTypeHidden').val() == 'SMS') {
        	if($('#modemTypeHidden').val() == 'MMIU') {
        		$('#divFwUploadOthers').hide();
                $('#divFwUploadPLC').hide();
                $('#divFwUploadSMS').hide();
                $('#divModemCommandSMS_MMIU').show();
                $('#divMMIUonSMSorGPRSCmd').show();
                $('#divModemCommand').hide();
                
                if($('#modemTypeHidden').val()) {
                	$('#cmdLine').show();	
                } else {
                	$('#cmdLine').hide();
                }
        	} else {
	        	$('#divFwUploadOthers').hide();
	            $('#divFwUploadPLC').hide();
	            $('#divFwUploadSMS').hide();
	            $('#divModemCommandSMS_MMIU').hide();
	            $('#divModemCommand').hide();
	            
	            if($('#modemTypeHidden').val() == 'GPRS'){
	            	$('#divMMIUonSMSorGPRSCmd').show();
	            } else {
	            	$('#divMMIUonSMSorGPRSCmd').hide();
	            }
	            
	            if($('#modemTypeHidden').val()) {
	            	$('#cmdLine').show();	
	            } else {
	            	$('#cmdLine').hide();
	            }
	        }
        } else if($('#protocolTypeHidden').val() == 'PLC'){
            $('#divFwUploadSMS').hide();
            $('#divFwUploadOthers').hide();
            $('#cmdLine').hide();
            $('#divFwUploadPLC').show();
            $('#divModemCommand').hide();
            $('#divMMIUonSMSorGPRSCmd').hide();
            $('#divModemCommandSMS_MMIU').hide();
        } else if ($('#protocolTypeHidden').val() == 'IP' || $('#protocolTypeHidden').val() == 'GPRS') { 
        	$('#divFwUploadSMS').hide();
            $('#divFwUploadOthers').hide();
            $('#cmdLine').hide();
            $('#divFwUploadPLC').hide();
            $('#divModemCommand').show();
            $('#divMMIUonSMSorGPRSCmd').hide();
            $('#divModemCommandSMS_MMIU').hide();
        } else {
            $('#divFwUploadSMS').hide();
            $('#divFwUploadPLC').hide();
            $('#divFwUploadOthers').show();
            $('#cmdLine').hide();
            $('#divModemCommand').hide();
            $('#divMMIUonSMSorGPRSCmd').hide();
            $('#divModemCommandSMS_MMIU').hide();
        } */
        
        
   		if (supplierName.search('MOE') >= 0){
	    	// 공급사가 이라크MOE인 경우 커맨드 출력하지 않음.
	    	// 일부 기능 추가 예정
	    	$("#command").hide();
   		}

        var protocolTypeHidden = $('#protocolTypeHidden').val();
        for (var i = 0; i < $('#protocolType option').size(); i++) {
            if ($("#protocolType option:eq("+i+")").get(0).id == protocolTypeHidden) {
                $("#protocolType option:eq("+i+")").attr("selected", "selected");
                break;
            }
        }
        $('#protocolType').selectbox();
        $('#protocolTypeView').val($('#protocolType option:selected').text());

        $('#deviceVendor').option($('#deviceVendorHidden').val());
        $('#deviceVendorView').val($('#deviceVendor option:selected').text());

        getModelListByVendor(1);

        $('#locationView').val($('#searchWord_1').val());

        $('#installDate').val($('#installDateHidden').val());
        $('#installDateView').val($('#installDateHidden').val());

        $('#modemStatusEdit').val($('#modemStatusCodeId').val());
        $('#modemStatusEdit').selectbox();
        $('#modemStatusView').val($('#modemStatusEdit option:selected').text());

        // 설치 지역 초기화
        locationTreeGoGo('treeDiv_1', 'searchWord_1', 'infolocationId');
    }

    // 기본정보 버튼 변경
    function changeModemInfoDiv(state) {
    	// result 창 초기화
        $('#commandResult').val('');

        // 버튼 div
        $('#modemInitButton').hide();
        $('#modemInsertButton').hide();
        $('#modemViewButton').hide();
        $('#modemEditButton').hide();
        // 등록 / 초기 조회
        if (state == "insert") {
            $('#modemInsertButton').show();

            // 초기화
            //modemId   = '';
            //modemType = '';

            $("#modemInfoDiv").load("${ctx}/gadget/device/modemMaxGadgetInfo.do");
            //changeMeterButton("add");
            //meterAdd();
            $(".meter-info-btn3").empty();
        } else if(state == "cancel") {    // 등록취소
            getModem();
            //changeMeterButton("modify");
            //meterCancel();
            return;
        // 조회
        } else if(state == "view") {
            $('#modemViewButton').show();

            $('#modemInfoEdit').hide();
            $('#modemInfoView').show();
        // 수정
        } else if(state == "edit") {
            $('#modemEditButton').show();

            $('#modemInfoEdit').show();
            $('#modemInfoView').hide();
        }
        var modelId = $('#modelIdHidden').val();
        enableButton(modelId);
    }
    
 // 모뎀 모델에 따라 표시될 버튼을 결정한다.
    var enableButton = function(modelId) {
    	//result 창 초기화
        $('#commandResult').val('');

        //기존 버튼 초기화
        $('#pingDiv').hide();
        $('#traceRouteDiv').hide();
        $('#otaDiv').hide();
    	
    	 // Command 권한 체크
        if (cmdAuth != "true") {
            return;
        }
    	//
        if(modelId=="" || modelId == null)
        	return;
    	
        
        //DeviceModelController.java
        $.getJSON('${ctx}/gadget/system/modelinfo2.do', {
            devicemodelId : modelId,
            supplierId : supplierId,
            roleId : roleId
        }, function(json) {

            if (json.namesOfContain.length > 0) {
                // 해당 모델이 사용 가능한 명령어 목록을 읽어와 버튼을 표시한다.
                // 모델목록과 명령은 CommonConstants.java Enum by EnableCommandModel 에 정의 되어있다.
                // **해당 role의 read/write 권한을 연계하여 write관련 버튼은 표시제어함.(editAuth)
                for ( var i = 0; i < json.namesOfContain.length; i++) {
                    switch (json.namesOfContain[i]) {
	                    case 'Ping':
	                        $('#pingDiv').show();
	                        break;
	                    case 'Trace Route':
	                        $('#traceRouteDiv').show();
	                        break;
                        case 'Modem OTA':
                            $('#otaDiv').show();
                            break;
                    }
                }
            }
        });
    };

    // 모뎀 삭제
    function deleteModemInfo() {
    	//CaptchaPanel("delete");
    	DeleteModemPanel();
    }
    
    var captchacount=1;	//틀린 횟수 체크
    var incorrectCodeCheck = false; //틀렸을 경우 메세지를 보여주기 위해서
 	function CaptchaPanel(option){
 		
    	// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('captchaWindowPanel')){
 			Ext.getCmp('captchaWindowPanel').close();
 		} 		
 		
 		var captchaFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        items       : [
 		            {
 		            	xtype: 'panel',
 		            	html: '<center><img src="${ctx}/CaptChaImg.jsp?rand='+ Math.random() + '"/></center></br>',
 		            	align:'left'
 		            	
 		            },
 		            {
 		            	xtype: 'textfield',
 		            	id : 'captchaCode',
 		            	fieldLabel: '<fmt:message key="aimir.captchaCode" />',
 		                emptyText: '<fmt:message key="aimir.enterTheCode" />',
 		                disabled : false,
 		               
 		            },
 		           {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	},
 		            	text : '*<fmt:message key="aimir.incorrectCode" />',
 		            	hidden: true
 		            }

 		        ],
 		        buttons: [
 		            {
			    	 	text: '<fmt:message key="aimir.refresh" />',
			    	 	listeners: {
			            	click: function(btn,e){
			            		captchaWindow.load(CaptchaPanel(option));
			            		
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.submit" />',
			            listeners: {
			            	click: function(btn,e){
			            		if(5==captchacount){
  			              		  window.open('${ctx}/admin/logout.do',"_parent").parent.close();
  			              	  	} 
			            		$.ajax({
			                        url: '${ctx}/gadget/report/CaptchaSubmit.do',
			                        type: 'POST',
			                        dataType: 'json',
			                        data: 'answer=' + $('#captchaCode').val(),
			                        async: false,  
			                        success: function(data) {
			                             if(data.capcahResult=="true"){
			                            	//올바른 코드 입력 시
			                            	 captchacount = 1;
			                            	 if(option=="delete")
			                            	 	captchaWindow.load(DeleteModemPanel()); //삭제화면 로딩
			                            	 else if(option=="resetModem")
			                            		 resetModem();
			                            	 else if(option=="setBaudrate")
			                            		 setBaudRate();
			                            	 else if(option=="getSetParam")
			                            		 getSetParameter();
			                            	 Ext.getCmp('captchaWindowPanel').close();
			                             }else{  
			                            	 //잘못된 코드 입력
			                            	 captchacount++;
			                            	 incorrectCodeCheck = true;
			                            	 captchaWindow.load(CaptchaPanel(option));
			                             }
			                       		}
			                  		});
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.cancel" />',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('captchaWindowPanel').close();
	                        }
	                    }
		        }]
 		    });
 			var msg ="";
 			if(option=="delete")
            	 	msg = '<fmt:message key="aimir.confirmModemDelete" />'; 
            else if(option=="resetModem")
            		msg = 'Modem Reset';
            else if(option=="setBaudrate")
            		msg = 'Set Baud Rate';
            else if(option=="getSetParam")
            		msg = 'Get/Set Parameter';
 		    var captchaWindow = new Ext.Window({
 		        id     : 'captchaWindowPanel',
 		        title  : msg,
 		        pageX : $("#gadget_body").width()/2-100,
                pageY : 400,
 		        height : 206, 
 		        width  : 300,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [captchaFormPanel],
 		        resizable: false
 		    });
 		    
 		    captchaWindow.show();
 			// 코드가 틀렸을 경우 메세지가 보이게
     		if(incorrectCodeCheck == true){
     			Ext.getCmp('infolabel').setVisible(true);
     			Ext.getCmp('captchaCode').focus(true,100);
     			incorrectCodeCheck = false;	
     		}
 	}
 	
 	function DeleteModemPanel(){
 		// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('deleteModemWindowPanel')){
 			Ext.getCmp('deleteModemWindowPanel').close();
 		} 		
 		
 		//Ext.getCmp('captchaWindowPanel').close();
 		var deleteModemFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        labelWidth  : 80, 		        
 		        items       : [
 		            {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	} ,
 		            	text : '<fmt:message key="aimir.msg.wantdelete" />'
 		            }
 		            
 		        ],
 		        buttons: [
 		            {
			            text: 'YES',
			            listeners: {
			            	click: function(btn,e){
			            		
			            		$.getJSON('${ctx}/gadget/device/deleteModem.do'
			        	                , {'modemId' : modemId}
			        	                , function (returnData) {
			        	                    if (returnData.result == 0) {
			        	                        var msg = Ext.Msg.show({
			                            			   title:'<fmt:message key='aimir.message'/>',
			                            			   msg: "<fmt:message key='aimir.msg.deleteFail' />",
			                            			   buttons: Ext.Msg.OK,
			                            		       cls: 'msgbox',
			                            	           
			                            			});
 			                            		msg.getDialog().setPosition($("#gadget_body").width()/2-30,250);
			        	                    } else {
												var msg = Ext.Msg.show({
			                            			   title:'<fmt:message key='aimir.message'/>',
			                            			   msg: "<fmt:message key='aimir.msg.deletesuccess'/>",
			                            			   buttons: Ext.Msg.OK,
			                            		       cls: 'msgbox',
			                            	           
			                            			});
 			                            		msg.getDialog().setPosition($("#gadget_body").width()/2-30,250);
 			                            		
			        	                        // 겁색조건 재 조회
			        	                        searchModem();

			        	                        // JS변수 초기화
			        	                        modemId   = '';
			        	                        modemType = '';

			        	                        // Info화면 초기화
			        	                        $("#modemInfoDiv").load("${ctx}/gadget/device/modemMaxGadgetInfo.do");
			        	                    }
			        	                });
			        	           
			            		
			            		Ext.getCmp('deleteModemWindowPanel').close();
	                        	Ext.getCmp('captchaWindowPanel').close();
			            }}
			        },{
			            text: 'NO',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('deleteModemWindowPanel').close();
	                        	Ext.getCmp('captchaWindowPanel').close();
	                        }
	                    }
		        }]
 		});
 		    var deleteModemWindow = new Ext.Window({
 		        id     : 'deleteModemWindowPanel',
 		        title  : '<fmt:message key="aimir.confirmModemDelete" />',
 		        pageX : $("#gadget_body").width()/2-100,
                pageY : 200,
 		        height : 140,
 		        width  : 300,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [deleteModemFormPanel],
 		    });
 		   deleteModemWindow.show();
 	}

    // 모뎀  변경
    function updateModem() {
       	// 프로토콜 타입이 IP, SMS일 경우 입력받은 IPv4/IPv6의 형식을 check (S)
       	var selectedValue = $('#protocolType option:selected').text();
       	if(selectedValue == "IP" || selectedValue == "SMS" || selectedValue == "GPRS"){
       		checkIpAddress($("#ipAddr").val(), $("#ipv6Address").val());
       		
       		if(ipValidation == false) {
       			return false;
       		}
       	}
   		// 입력받은 IPv4/IPv6의 형식을 check (E)
   		
        $("#modemInfoForm :input[id='modemTableId']").val(modemId);
        $("#modemInfoForm :input[id='supplierId']").val(supplierId);
        $("#modemInfoForm :input[id='modemTypeName']").val($('#modemType option:selected').val());
        if($('#protocolType option:selected').text() != "-") {
            $("#modemInfoForm :input[id='protocolTypeName']").val($('#protocolType option:selected').get(0).id);
        }
        $("#modemInfoForm :input[id='protocolVersion']").val($('#protocolVersion').val());
        $("#modemInfoForm :input[id='hwVerName']").val($('#hwVersion').val());
        $("#modemInfoForm :input[id='fwVerName']").val($('#fwVersion').val());
        
        var tempModemStatus = $('#modemStatusEdit option:selected').val() == '-' ? "" : $('#modemStatusEdit option:selected').val(); 
        $("#modemInfoForm :input[id='modemStatusCodeId']").val(tempModemStatus);
        
        var modemType = $('#modemType option:selected').val();
        var url = "${ctx}/gadget/device/updateModem" + modemType + ".do";
		params = {
			success : updateModemResult,
			url : url,
			type : 'post',
			datatype : 'application/json'
		};

        $('#modemInfoForm').ajaxSubmit(params);
    }
    
    function checkIpAddress(ipv4, ipv6) {
    	$.ajax({
    	  url: '${ctx}/gadget/device/command/checkIpAddress.do',
    	  dataType: 'json',
    	  async: false,
    	  data: {'ipv4' : ipv4,
      			'ipv6' : ipv6
    			},
    	  success: function(returnData) {
			if(returnData.status != "SUCCESS") {
				Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
				ipValidation = false;
				return;
			} else{
				ipValidation = true;
			}
    	  }
    	});
    }
	
    function updateModemResult(responseText, status) {

        // 미터 조회
        var modemId      = responseText.id;
        var modemType    = $('#modemType option:selected').val();
        var deviceSerial = $('#modemId').val();

        var tempMsg = ' <fmt:message key="aimir.msg.updatesuccess" />';
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',deviceSerial + tempMsg.substring(tempMsg.lastIndexOf("_") + 1));

        // 목록조회
        modemSearchGridStore.reload(modemSearchGridStore.lasgOptions);

        // 등록됨 모뎀 상세 조회
        setModemId(modemId, modemType, deviceSerial);
    }
    // 스케쥴에서 사용하는 JS

    // 스케쥴링하는 모뎀 타입인지 확인
    function checkModemType() {

        if (modemId == "") {
            Ext.MessageBox.show({
                title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.alert.modemIsNotSelected'/>',
                icon : Ext.MessageBox.INFO
            });

            //$("modemDetailTab#schedule").hide();
            $("div.modemDetailTab").hide();

            //$("div.tabs").hide();
            //$("div#tabs1").show();

            return 1;
        }
        if (modemType !== "ZEUPLS" && modemType !== "ZEUMBus" && modemType !== "ZBRepeater" && modemType !== "ACD"
                                  && modemType !== "HMU" && modemType !== "MMIU" && modemType !== "ZRU" && modemType !== "Converter" && modemType !== "SubGiga") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',modemType + " <fmt:message key='aimir.alert.isNotScheduled'/>");

            $("#modemDetailTab").tabs().tabs('select',0);

            return 1;
        }

        return 0;
    }
    //환경설정하는 모뎀 타입인지 확인
    function checkModemAndVersionType() {

        if (modemId == "") {

            Ext.MessageBox.show({
                title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: '<fmt:message key='aimir.msg.choice'/>',
                icon : Ext.MessageBox.INFO
            });

            //$("modemDetailTab#schedule").hide();
            $("div.modemDetailTab").hide();

            //$("div.tabs").hide();
            //$("div#tabs1").show();

            return 1;
        }
        if (modemType !== "MMIU") {

            Ext.MessageBox.show({
                title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                buttons : Ext.MessageBox.OK,
                msg: modemType + ' <fmt:message key='aimir.alert.isNotSetting'/>',
                icon : Ext.MessageBox.INFO
            });

            $("#modemDetailTab").tabs().tabs('select',0);

            return 1;
        }

        return 0;
    }

    // update
    function updateSchedule() {

        var dStr = "0";
        var hStr = "";
        var lpChoice  = $('#lpChoice').val();
        var lpPeriod  = $('#lpPeriod').val();
        var alarmFlag  = $('#alarmFlag').val();
        // 날짜 연산
        for (var i = 1; i < 32; i++) {
            var idName = "#d" + i;

            if ($( idName ).is(':checked'))
                dStr = dStr + "1";
            else
                dStr = dStr + "0";
        }

        // 시간 연산
        for (var i = 0; i < 98; i++) {
            var idName = "#h" + i;

            if ($( idName ).is(':checked'))
                hStr = hStr + "1";
            else
                hStr = hStr + "0";
        }

        // 필요값 설정
        $("#modemScheduleForm :input[id='modemId']").val(modemId);
        $("#modemScheduleForm :input[id='meteringDay']").val('0'+dStr);
        $("#modemScheduleForm :input[id='meteringHour']").val('0'+hStr);

        $.getJSON('${ctx}/gadget/device/updateModemSchedule.do'
                , {'modemId' : modemId,
                    'modemType'   : modemType,
                    'meteringDay' : '0'+dStr,
                    'meteringHour' : '0'+hStr,
                    'lpChoice' : lpChoice}
                , function (returnData) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.scheduleUpdateComplete'/>");
                });
    }

    function updateModemSchedule() {
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.scheduleUpdateComplete'/>");
    }

    function initModemSchedule() {
        $('#modemScheduleForm').each(function(){
            this.reset();
            });
    }

    var dayStrBack;
    var timeStrBack;

    function setModemSchedule() {

        var dStr = dayStrBack = $("#modemScheduleForm :input[id='meteringDay']").val();
        var hStr = timeStrBack = $("#modemScheduleForm :input[id='meteringHour']").val();

        // 날짜 연산
        for (var i = 1; i < 32; i++) {

            var idName = "#d" + i;

            if (dStr.charAt(i) == 1)
                $(idName).attr('checked','checked');
        }

        // 시간 연산
        for (var i = 0; i < 98; i++) {
            var idName = "#h" + i;

            if (hStr.charAt(i) == 1)
                $(idName).attr('checked','checked');
        }
    }

    function setModemScheduleReload() {

        var dStr = dayStrBack;
        var hStr = timeStrBack;

        // 날짜 연산
        for (var i = 1; i < 32; i++) {

            var idName = "#d" + i;

            if (dStr.charAt(i) == 1)
                $(idName).attr('checked','checked');
            else
                $(idName).attr('checked','');
        }

        // 시간 연산
        for (var i = 0; i < 98; i++) {
            var idName = "#h" + i;

            if (hStr.charAt(i) == 1)
                $(idName).attr('checked','checked');
            else
                $(idName).attr('checked','');
        }
        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.cancel"/>');
    }

    // 시 전체 선택
    function dayAll() {

        if ($('#dAll').is(':checked'))
            for (var i = 1; i < 32; i++) {
                var idName = "#d" + i;
                $( idName ).attr('checked','checked');
            }
        else
            for (var i = 1; i < 32; i++) {
                var idName = "#d" + i;
                $( idName ).attr('checked','');
            }
    }

    // Metering Day 모두 체크시  All 체크박스 체크.
    function dayCheck() {
        var tempInt = 0;
        //alert("test??");
        for (var i = 1; i < 32; i++) {
            var idName = "#d" + i;
            if ($(idName).is(':checked'))
                tempInt++;
        }
        if (tempInt == 31)
            $('#dAll').attr('checked','checked');
        else
            $('#dAll').attr('checked','');
    }

    // 분 전체 선택
    function minAll() {
        if ($('#hAll').is(':checked'))
            for (var i = 1; i < 98; i++) {
                var idName = "#h" + i;
                $( idName ).attr('checked','checked');
            }
        else
            for (var i = 1; i < 98; i++) {
                var idName = "#h" + i;
                $( idName ).attr('checked','');
            }
    }

    // Metering Time 모두 체크시  All 체크박스 체크.
    function timeCheck() {
        var tempInt = 0;
        //alert("test??");
        for (var i = 1; i < 97; i++) {
            var idName = "#h" + i;
            if ($(idName).is(':checked'))
                tempInt++;
        }
        if (tempInt == 96)
            $('#hAll').attr('checked','checked');
        else
            $('#hAll').attr('checked','');
    }

    // 최종통신날짜 및 조건 검색에 대한 reset 설정
    function reset() {
        // Form Reset
       var $searchForm = $("form[name=search]");
       $searchForm.trigger("reset");
       
       // 자동 초기화 안되는 요소들 직접 초기화
       $('#sLocationId').val('');
       $('#sInstallStartDateHidden').val('');
       $('#sInstallEndDateHidden').val('');
       $('#sLastcommStartDateHidden').val('');
       $('#sLastcommEndDateHidden').val('');
       
       // 셀렉트 태그 첫번째 인덱스 선택
       var $selects = $searchForm.find("select");
       $selects.each(function() {
           $(this).selectbox();
       });
/*      $("#sCommState option:eq(0)").attr("selected", "selected");
      // $('#sCommState').val('');
      $('#sCommState').selectbox();

      $("#sOrder option:eq(0)").attr("selected", "selected");
      $('#sOrder').selectbox();*/
       return;
    }

    // 위치정보 관련 JS  --------------------
    // 주소 정보 -> X,Y,Z로 변경함
    function getGeoCoding() {
        cvAddressToCoordinate($('#sysLocation').val(), "gpioX", "gpioY", "gpioZ");
    }

    // 지도의 위도  / 경도 변경
    function updateModemLoc() {

        $.getJSON('${ctx}/gadget/device/mapUpdate.do'
                , {'className' : "modem",
                    'name'   : deviceSerial    ,
                    'pointx' : $('#gpioX').val(),
                    'pointy' : $('#gpioY').val() }
                , function (returnData){
                    Ext.MessageBox.show({
                        title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                        buttons : Ext.MessageBox.OK,
                        msg: '<fmt:message key='aimir.alert.geographicUpdate'/>',
                        icon : Ext.MessageBox.INFO
                    });
                    viewModemMap();
                });
    }

    // 주소 업데이트
    function updateModemAddress() {

        $.getJSON('${ctx}/gadget/device/mapUpdateAddress.do'
                , {'className' : "modem",
                    'name'     : deviceSerial    ,
                    'address'  : encodeURIComponent($('#sysLocation').val()) }
                , function (returnData) {
                    Ext.MessageBox.show({
                        title: '<fmt:message key='aimir.modem'/> <fmt:message key='aimir.info'/>',
                        buttons : Ext.MessageBox.OK,
                        msg: '<fmt:message key='aimir.alert.addressUpdate'/>',
                        icon : Ext.MessageBox.INFO
                    })
                    });
    }

    function viewModemMap() {

        if (googleMapInit()) {
            // modem의 정보를 구함
            showPoints('${ctx}/' + supplierId +'/'+ deviceSerial +'/modem.do');
        }
    }

    function setModemLocInfo(gpioX, gpioY, gpioZ, address) {

        // 위치정보 설정
        $('#gpioX').val(gpioX);
        $('#gpioY').val(gpioY);
        $('#gpioZ').val(gpioZ);

        // 주소 정보 할당
        $('#sysLocation').val(address);

        // 지도 조회
        viewModemMap();
    }

    // 화면 초기화
    function init() {
        // 초기 Blank 목적
        $("#modemInfoDiv").load("${ctx}/gadget/device/modemMaxGadgetInfo.do");

        //$("#meterAdd").trigger("click");
        //meterAdd();
        getModemAddMeterGrid();
        $("#modemDetailTab").hide();
        
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.charset = 'utf-8';
        script.src = '${ctx}/js/googleMap.jsp';

        head.appendChild(script);
    }

    // 사용자가 날짜 삭제시 hidden값의 초기화
    function sDateChange(obj) {

        var dateId       = '#' + obj.id;
        var dateHiddenId = '#' + obj.id + 'Hidden';

        $(dateHiddenId).val('');
    }

    function getFmtMessageAddMeter() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.updatedata.notexist"/>";             // 추가 가능한 미터가 없습니다.

        return fmtMessage;
    }

    /************ Fusion Chart **************/ 
    $(document).ready(function() {
        //initFColumnChartData();
        updateFChart();
    });

    function updateFChart() {
        emergePre();
		
        $.getJSON('${ctx}/gadget/device/getModemSearchChart.do'
            ,{sModemType:$('#sModemType').val(),
                sModemId:$('#sModemId').val(),
                sMcuType:$('#sMcuType').val(),
                sMcuName:$('#sMcuName').val(),
                sModemFwVer:$('#sModemFwVer').val(),
                sModemSwRev:$('#sModemSwRev').val(),
                sModemHwVer:$('#sModemHwVer').val(),
                sInstallStartDate:$('#sInstallStartDateHidden').val(),
                sInstallEndDate:$('#sInstallEndDateHidden').val(),
                sLastcommStartDate:$('#sLastcommStartDateHidden').val(),
                sLastcommEndDate:$('#sLastcommEndDateHidden').val(),
                sLocationId:$('#sLocationId').val(),
                sModemStatus:$('#sModemStatus').val(),
                sMeterSerial:$('#sMeterSerial').val(),
                sModuleBuild:$('#sModuleBuild').val(),
                sGs1:$('#sGs1').val(),
                supplierId:supplierId}
        ,function(json) {
                var list = json.chartData;
                fcPieChartDataXml = "<chart "
                    + "showPercentValues='1' "
                    + "showPercentInToolTip='0' "
                    + "showLabels='0' "
                    + "showValues='1' "
                    + "showLegend='1' "
                    + "legendPosition='Right' "
                    + "manageLabelOverflow='0' "
                    + ">";
                var labels = "";

                var emptyFlag = true;
                for (index in list) {
                    if(list[index].label == "fmtMessage00") {
                    	labels += "<set label='activity within &lt;br /&gt; 24 hours' value='" + list[index].data + "' color='"+blue+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } else if(list[index].label == "fmtMessage24") {
                         labels += "<set label='<fmt:message key='aimir.commstateYellow' />' value='" + list[index].data + "' color='"+green+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } else if(list[index].label == "fmtMessage48") {
                         labels += "<set label='<fmt:message key='aimir.commstateRed' />' value='" + list[index].data + "' color='"+yellow+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } else if(list[index].label == "fmtMessage99") {
                         labels += "<set label='<fmt:message key='aimir.bems.facilityMgmt.unknown' />' value='" + list[index].data + "' color='"+redbean+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } else if(list[index].label == "CommError") {
                         labels += "<set label='CommError' value='" + list[index].data + "' color='"+orange+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } else if(list[index].label == "SecurityError") {
                         labels += "<set label='SecurityError' value='" + list[index].data + "' color='"+red+"' />";
                        if(list[index].data > 0) emptyFlag = false;
                    } 
                }

                if (list == null || list.length == 0 || emptyFlag) {
                    labels = "<set label='' value='1' color='E9E9E9' toolText='' />";
                }

                fcPieChartDataXml += labels + "</chart>";
                fcChartRender();

                hide();
            }
        );
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        fcPieChart= new FusionCharts({
            type: 'pie3d',
            width: $('#fcPieChartDiv').width(),
            height: '165'})
        fcPieChart.setDataXML(fcPieChartDataXml);
        fcPieChart.setTransparent("transparent");
        fcPieChart.render("fcPieChartDiv");

        if($('#general').width() > 0){
            colWidth = $('#general').width() - 42;
        } else if($('#locationInfo').width() > 0){
            colWidth = $('#locationInfo').width() - 42;
        } else if($('#schedule').width() > 0){
            colWidth = $('#schedule').width() - 42;
        }
    }

    function commandModem(command) {

        //$('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdGPRSModem.do'
                , {'target' : modemId
                , 'loginId' : loginId
                , 'param' : command}
                , function (returnData){
                //  $('#commandResult').val(returnData.rtnStr);
                });
    }

    function commandGetLPLog() {
        var mcuId=$('#mcuId').val();
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdGetLPLog.do'
                , {'supplierId':supplierId
                ,  'mcuId' : mcuId
                ,  'modemId' : modemId
                , 'loginId' : loginId}
                , function (returnData) {
                    if (!returnData.status) {
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if (returnData.status.length>0 && returnData.status!='SUCCESS') {
                        $('#commandResult').val("LP Log: "+returnData.status);
                    } else {
                        $('#commandResult').val("");

                        if (returnData.lpDate)
                            $('#commandResult').val($('#commandResult').val() + "LP Date: "+returnData.lpDate+"\n");
                        if (returnData.basePulse)
                            $('#commandResult').val($('#commandResult').val() + "Base Pulse: "+returnData.basePulse+"\n");
                        if (returnData.period)
                            $('#commandResult').val($('#commandResult').val() + "Period: "+returnData.period+"\n");
                        if (returnData.pointer)
                            $('#commandResult').val($('#commandResult').val() + "Pointer: "+returnData.pointer+"\n");
                        if (returnData.lp) {
                            for ( var i in returnData.lp) {
                                $('#commandResult').val($('#commandResult').val() + "LP "+i+": "+returnData.lp[i]+"\n");
                            }
                        }
                    }
                });
    }

    function commandUnitScanning() {
        var mcuId=$('#mcuId').val();
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdSensorScanning.do'
                , {'mcuId' : mcuId
                , 'modemId' : modemId
                , 'modemType' : modemType
                , 'loginId' : loginId}
                , function (returnData){
                    if (!returnData.status) {
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if (returnData.status.length>0 && returnData.status!='SUCCESS') {
                        $('#commandResult').val("Unit scanning: "+returnData.status);
                    } else {
                        $('#commandResult').val("");

                        if (returnData.installDate)
                            $('#commandResult').val($('#commandResult').val() + "InstallDate: "+returnData.installDate+"\n");
                        if (returnData.linkKey)
                            $('#commandResult').val($('#commandResult').val() + "LinkKey: "+returnData.linkKey+"\n");
                        if (returnData.networkKey)
                            $('#commandResult').val($('#commandResult').val() + "NetworkKey: "+returnData.networkKey+"\n");
                        if (returnData.extPanId)
                            $('#commandResult').val($('#commandResult').val() + "ExtPanId: "+returnData.extPanId+"\n");
                        if (returnData.channelId)
                            $('#commandResult').val($('#commandResult').val() + "ChannelId: "+returnData.channelId+"\n");
                        if (returnData.manualEnable)
                            $('#commandResult').val($('#commandResult').val() + "ManualEnable: "+returnData.manualEnable+"\n");
                        if (returnData.panId)
                            $('#commandResult').val($('#commandResult').val() + "PanId: "+returnData.panId+"\n");
                        if (returnData.securityEnable)
                            $('#commandResult').val($('#commandResult').val() + "SecurityEnable: "+returnData.securityEnable+"\n");
                        if (returnData.hwVer)
                            $('#commandResult').val($('#commandResult').val() + "HwVer: "+returnData.hwVer+"\n");
                        if (returnData.nodeKind)
                            $('#commandResult').val($('#commandResult').val() + "NodeKind: "+returnData.nodeKind+"\n");
                        if (returnData.protocolVersion)
                            $('#commandResult').val($('#commandResult').val() + "ProtocolVersion: "+returnData.protocolVersion+"\n");
                        if (returnData.resetCount)
                            $('#commandResult').val($('#commandResult').val() + "ResetCount: "+returnData.resetCount+"\n");
                        if (returnData.lastResetCode)
                            $('#commandResult').val($('#commandResult').val() + "LastResetCode: "+returnData.lastResetCode+"\n");
                        if (returnData.swVer)
                            $('#commandResult').val($('#commandResult').val() + "SwVer: "+returnData.swVer+"\n");
                        if (returnData.fwVer)
                            $('#commandResult').val($('#commandResult').val() + "FwVer: "+returnData.fwVer+"\n");
                        if (returnData.fwRevision)
                            $('#commandResult').val($('#commandResult').val() + "FwRevision: "+returnData.fwRevision+"\n");
                        if (returnData.zdzdIfVersion)
                            $('#commandResult').val($('#commandResult').val() + "ZdzdIfVersion: "+returnData.zdzdIfVersion+"\n");
                        if (returnData.solarADV)
                            $('#commandResult').val($('#commandResult').val() + "SolarADV: "+returnData.solarADV+"\n");
                        if (returnData.solarChgBV)
                            $('#commandResult').val($('#commandResult').val() + "SolarChgBV: "+returnData.solarChgBV+"\n");
                        if (returnData.solarBDCV)
                            $('#commandResult').val($('#commandResult').val() + "SolarBDCV: "+returnData.solarBDCV+"\n");
                        if (returnData.testFlag)
                            $('#commandResult').val($('#commandResult').val() + "TestFlag: "+returnData.testFlag+"\n");
                        if (returnData.fixedReset)
                            $('#commandResult').val($('#commandResult').val() + "FixedReset: "+returnData.fixedReset+"\n");
                        if (returnData.meteringDay)
                            $('#commandResult').val($('#commandResult').val() + "MeteringDay: "+returnData.meteringDay+"\n");
                        if (returnData.meteringHour)
                            $('#commandResult').val($('#commandResult').val() + "MeteringHour: "+returnData.meteringHour+"\n");
                        if (returnData.lpChoice)
                            $('#commandResult').val($('#commandResult').val() + "LpChoice: "+returnData.lpChoice+"\n");

                        //모뎀 상세 정보 reflush
                        setModemId( modemId, modemType, deviceSerial);
                    }
                });
    }

    function commandMonitoring() {
        var mcuId=$('#mcuId').val();
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdSensorStatus.do'
                , {'mcuId' : mcuId
                , 'modemId' : modemId
                , 'modemType' : modemType
                , 'loginId' : loginId}
                , function (returnData){
                    if(!returnData.status){
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                        $('#commandResult').val("Monitoring: "+returnData.status);
                    }else{
                        $('#commandResult').val("");

                        if(returnData.fwVer)
                        $('#commandResult').val($('#commandResult').val() + "fwVer: "+returnData.fwVer+"\n");
                        if(returnData.hwVer)
                        $('#commandResult').val($('#commandResult').val() + "hwVer: "+returnData.hwVer+"\n");
                        if(returnData.nodeKind)
                        $('#commandResult').val($('#commandResult').val() + "nodeKind: "+returnData.nodeKind+"\n");
                   		if(returnData.protocolVersion)
                        $('#commandResult').val($('#commandResult').val() + "protocolVersion: "+returnData.protocolVersion+"\n");
                        if(returnData.resetCount)
                        $('#commandResult').val($('#commandResult').val() + "resetCount: "+returnData.resetCount+"\n");
                        if(returnData.lastResetCode)
                        $('#commandResult').val($('#commandResult').val() + "lastResetCode: "+returnData.lastResetCode+"\n");
                        if(returnData.commState)
                        $('#commandResult').val($('#commandResult').val() + "commState: "+returnData.commState+"\n");

                    }
                });
    }

    function commandEventLog() {
        var mcuId=$('#mcuId').val();
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdSensorEventLog.do'
                , {'mcuId' : mcuId
                , 'modemId' : modemId
                , 'modemType' : modemType
                , 'loginId' : loginId}
                , function (returnData){
                    if(!returnData.status){
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                        $('#commandResult').val("Event Log: "+returnData.status);
                    }else{
                        $('#commandResult').val("Log Count="+returnData.eventLog.length+"\n");

                        for ( var i in returnData.eventLog) {
                            $('#commandResult').val($('#commandResult').val() + "["+i+"]GMT time["+returnData.eventLog[i].gmtTime+"]");
                            $('#commandResult').val($('#commandResult').val() + ", message["+returnData.eventLog[i].eventMsg+"]");
                            $('#commandResult').val($('#commandResult').val() + ", status["+returnData.eventLog[i].eventStatus+"]");
                            $('#commandResult').val($('#commandResult').val() + ", description["+returnData.eventLog[i].eventDescr+"]");

                    if(returnData.eventLog[i].firmwareBuild){
                                $('#commandResult').val($('#commandResult').val() + ", firmware Build["+returnData.eventLog[i].firmwareBuild+"]");
                    }
                    if(returnData.eventLog[i].firmwareVersion){
                                $('#commandResult').val($('#commandResult').val() + ", firmware Version["+returnData.eventLog[i].firmwareVersion+"]");
                    }
                            $('#commandResult').val($('#commandResult').val() + ", type["+returnData.eventLog[i].eventType+"]\n");


                        }

                    }
                });
    }

    function commandTOUCalendar() {
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdBypassTOUCalendar.do'
                , {'modemId' : modemId,
                    'loginId' : loginId}
                , function (returnData){
                    if(!returnData.status){
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    $('#commandResult').val(returnData.rtnStr);

                });
    }
    
    function cmdLine() {
    	var left = ($("#fcPieChartDiv").width());
	    var opts="width=670px, height=600px, left=" + left + "px, top=200px, resizable=no, status=no, location=no";
            var obj = new Object();
            var condition = new Array();

            obj.modemId   = modemId;
            obj.loginId	  = loginId;

            if(cmdLineWin)
                cmdLineWin.close();
            cmdLineWin = window.open("${ctx}/gadget/device/modemCMDLine.do", "modemCMDLine", opts);
            cmdLineWin.opener.obj = obj;
    }
    
    function commandPing(){
    	/* Ext.Msg.prompt('Options For Ping', 'Please enter packet-size and echo-count.<br/><br/>Desc) packet-size, echo-count <br/>',
    			function(btn, text) {
    				if(btn=='ok'){
    					var data = text;
    					var packetSize = 0;
    					var count = 0;
    					if(data != null) {
    						var trimedData = data.replaceAll(" ", "");
							var splitedData = trimedData.split(',');
				
							if(2 < splitedData.length || splitedData.length < 1) {
								Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
								return false;
							}
							
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if(isNaN(splitedData[0]) == true || isNaN(splitedData[1]) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							} else {
								doPing(splitedData[0], splitedData[1]);
							}
						} else {
    						Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
							return false;
    					}
    				}
    	},this, false, '64, 3'); */
    	
    	doPing('64', '3');
    }
    
	function commandCOAPPing(count){
		/* Ext.Msg.prompt('Options For COAP Ping', 'Please enter the echo-count.<br/>',
    			function(btn, text) {
    				if(btn=='ok'){
    					var count = text;
    					if(count != null) {
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if(isNaN(count) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							}else{
								doCOAPPing(count);
							}
    					} else {
    						Ext.Msg.alert('Error', 'Please re-enter the echo-count.');
							return false;
    					}
    				}
    	},this, false, '1'); */
		doCOAPPing();
	}
    
    function doPing(packetSize, count) {
    	$('#commandResult').val("Request MODEM Server Ping....");
    	$.ajax({
    	  url: '${ctx}/gadget/device/command/cmdModemPing.do',
    	  dataType: 'json',
    	  async: true,
    	  data: {'target' : modemId
              , 'loginId' : loginId
              , 'packetSize' : packetSize
              , 'count' : count
              , 'device' : 'modem'
              },
    	  success: function(returnData) {
	   		  if(!returnData.status){
	                 $('#commandResult').val("Ping FAIL");
	                    return;
	             }
	             if(returnData.status.length>0 && returnData.status!='SUCCESS'){
	                 $('#commandResult').val("Ping "+ returnData.status);
	             } else {
	             	$('#commandResult').val(""); 
	                $('#commandResult').val(returnData.jsonString); 
	             }
    	  }
    	});
    }
    
	function doCOAPPing() {
		$('#commandResult').val("Request MODEM Server COAP-Ping....");
		Ext.define('pingAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout2/1000 + 's...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		pingAjax.request({
	    	  url: '${ctx}/gadget/device/command/cmdModemCOAPPing.do',
	    	  type : "POST",
	    	  timeout : extAjaxTimeout2,
		      params : {
		    	      target : modemId
	                , loginId : loginId 
	                , device : 'modem'
		      },
	    	  success: function(result, request) {
	    		 	Ext.MessageBox.hide();
		        	var jsonData = Ext.util.JSON.decode(result.responseText);
					$('#commandResult').val(jsonData.jsonString);
	    	  },
	    	  failure: function(result, request){
	                //폼 윈도우를 닫고, 결과 처리
	                Ext.MessageBox.hide();
	                //$('#commandResult').val(result.toString());
	                /* if(result.isTimeout){
	                    $('#commandResult').val("[FAIL]If protocol type is SMS, check the results in Async History tab");
	                }else{
	                    $('#commandResult').val("FAIL");
	                } */
	                $('#commandResult').val("CoAP Ping FAIL");
	            }
	    	}); 
	}
	
	function setInitModem() {
		var timeoutPeriod = 30000;
		
		$('#commandResult').val("Request Setting the initial modem...");
		
		Ext.define('runAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Request to Set Init Modem...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		
		runAjax.request({
	    	  url: '${ctx}/gadget/device/command/setInitModem.do',
	    	  type : "POST",
	    	  timeout : timeoutPeriod,
		      params : {
		    	      target : modemId
	                , loginId : loginId 
		      },
	    	  success: function(result, request) {
	    		 	Ext.MessageBox.hide();
		        	var returnData = Ext.util.JSON.decode(result.responseText);
					$('#commandResult').val(returnData.rtnStr);
	    	  },
	    	  failure: function(result, request){
	                //폼 윈도우를 닫고, 결과 처리
	                Ext.MessageBox.hide();
	                //$('#commandResult').val(result.toString());
	                if(result.isTimeout){
	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
	                }else{
	                    $('#commandResult').val("FAIL");
	                }
	            }
	    	}); 
	}
		
	
	function restartCommStack() {
		var timeoutPeriod = 30000;
		
		$('#commandResult').val("Request Restarting The Communication Stack...");
		Ext.define('runAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Request to Restart Comm. Stack...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		
		runAjax.request({
			url: '${ctx}/gadget/device/command/restartCommStack.do',
	    	  type : "POST",
	    	  timeout : timeoutPeriod,
		      params : {
		    	      target : modemId
	                , loginId : loginId 
		      },
	    	  success: function(result, request) {
	    		 	Ext.MessageBox.hide();
		        	var returnData = Ext.util.JSON.decode(result.responseText);
					
					if(returnData.status == "SUCCESS"){
						$('#commandResult').val(returnData.status);
					} else {
		    			  $('#commandResult').val(returnData.rtnStr);
					}
	    	  },
	    	  failure: function(result, request){
	                //폼 윈도우를 닫고, 결과 처리
	                Ext.MessageBox.hide();
	                //$('#commandResult').val(result.toString());
	                if(result.isTimeout){
	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
	                }else{
	                    $('#commandResult').val("FAIL");
	                }
	            }
	    	}); 
	}
	
	function restartCommModem(){
		var timeoutPeriod = 30000;
		
		$('#commandResult').val("Request Restarting The Communication Modem...");
		Ext.define('runAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Request to Restart Comm. Modem...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		
		runAjax.request({
			  url: '${ctx}/gadget/device/command/restartCommModem.do',
	    	  type : "POST",
	    	  timeout : timeoutPeriod,
		      params : {
		    	      target : modemId
	                , loginId : loginId 
		      },
	    	  success: function(result, request) {
	    		 	Ext.MessageBox.hide();
		        	var returnData = Ext.util.JSON.decode(result.responseText);
					
					if(returnData.status == "SUCCESS"){
						$('#commandResult').val(returnData.status);
					} else {
		    			  $('#commandResult').val(returnData.rtnStr);
					}
	    	  },
	    	  failure: function(result, request){
	                //폼 윈도우를 닫고, 결과 처리
	                Ext.MessageBox.hide();
	                //$('#commandResult').val(result.toString());
	                if(result.isTimeout){
	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
	                }else{
	                    $('#commandResult').val("FAIL");
	                }
	            }
	    	}); 
	}
	
	function setAPN() {
		var apnName = "";
		var apnId = "";
		var apnPassword = "";
		var apnCheckPassword = "";
		var timeoutPeriod = 30000;
		
		// 아직 안닫힌 경우 기존 창은 닫기
        if(Ext.getCmp('toggleWindow')){
            Ext.getCmp('toggleWindow').close();
        }

        toggleFormPanel = new Ext.FormPanel({
            id : 'toggleform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'textfield',
                    id : 'apnName',
                    fieldLabel : 'APN Name',
                    listeners :{
                    	change: function(){
                    		apnName = toggleFormPanel.getForm().findField('apnName').getValue();
                    	}
                    },
                },
                {
                    xtype : 'textfield',
                    id : 'apnId',
                    fieldLabel : 'APN Id',
                    listeners :{
                    	change: function(){
                    		apnId = toggleFormPanel.getForm().findField('apnId').getValue();
                    	}
                    },
                },
                {
                    xtype : 'textfield',
                    id : 'apnPassword',
                    fieldLabel : 'APN Password',
                    listeners :{
                    	change: function(){
                    		apnPassword = toggleFormPanel.getForm().findField('apnPassword').getValue();
                    	}
                    },
                },
                {
                    xtype : 'textfield',
                    id : 'apnCheckPassword',
                    fieldLabel : 'Check Password',
                    listeners :{
                    	change: function(){
                    		apnCheckPassword = toggleFormPanel.getForm().findField('apnCheckPassword').getValue();
                    	}
                    },
                }
            ],
            buttons : [
                {
                    id: 'toggleSendBtn',
                    text: 'Submit',
                    listeners: {
                        click: function(btn,e){
                    		Ext.define('setApnAjax', {
                                extend: 'Ext.data.Connection',
                                singleton: true,
                                constructor : function(config){
                                    this.callParent([config]);
                                    this.on("beforerequest", function(){
                                    	if(apnName == "") {
                                    		Ext.Msg.alert('Error', 'Please enter APN Name.');
            								return false;
                                    	}
                                    	
                                    	if(apnPassword != apnCheckPassword) {
                                    		Ext.Msg.alert('Error', 'The password is different.');
            								return false;
                                    	}
                                    	
                                        Ext.MessageBox.wait("Request to Set APN...", '<fmt:message key="aimir.info"/>', {
                                            text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
                                            scope: this,
                                        });
                                    });
                                    this.on("requestcomplete", function(){
                                        Ext.MessageBox.hide();
                                    });
                                }
                            });
                    		setApnAjax.request({
                    			  url: '${ctx}/gadget/device/command/setAPN.do',
                    			  dataType: 'json',
                    	    	  async: true,
                    	    	  timeout : extAjaxTimeout2,
                    		      params : {
                    	    		  'target' : modemId, 
                    	    		  'loginId' : loginId,
                    	    		  'apnName' : apnName,
                    	    		  'apnId' : apnId,
                    	    		  'apnPassword' : apnPassword
                    		      },
                    		      success: function(result, request) {
                    	    		 	Ext.MessageBox.hide();
                    		        	var returnData = Ext.util.JSON.decode(result.responseText);
                    					
                    					if(returnData.status == "SUCCESS"){
                    						$('#commandResult').val(returnData.status);
                    					} else {
                    		    			  $('#commandResult').val(returnData.rtnStr);
                    					}
  								  },
                    	    	  failure: function(result, request){
                  	                //폼 윈도우를 닫고, 결과 처리
                  	                Ext.MessageBox.hide();
                  	                //$('#commandResult').val(result.toString());
                  	                if(result.isTimeout){
                  	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
                  	                }else{
                  	                    $('#commandResult').val("FAIL");
                  	                }
                  	            }
                    	    	}); 
                        	Ext.getCmp('toggleWindow').close();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('toggleWindow').close();
                        }
                    }
                }
            ]
        });

        var toggleWin = new Ext.Window({
            id     : 'toggleWindow',
            title : 'Set APN',
            height : 220,
            width  : 320,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [toggleFormPanel],
        });

        toggleWin.show();
	}
	
	function doWatchdog() {
		var timeoutPeriod = 30000;
		
		$('#commandResult').val("Request to do the Watchdog...");
		Ext.define('runAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Request to Watchdog...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		
		runAjax.request({
			  url: '${ctx}/gadget/device/command/doWatchdog.do',
	    	  type : "POST",
	    	  timeout : timeoutPeriod,
		      params : {
		    	      target : modemId
	                , loginId : loginId 
		      },
	    	  success: function(result, request) {
	    		 	Ext.MessageBox.hide();
		        	var returnData = Ext.util.JSON.decode(result.responseText);
					
					if(returnData.status == "SUCCESS"){
						$('#commandResult').val(returnData.status);
					} else {
		    			  $('#commandResult').val(returnData.rtnStr);
					}
	    	  },
	    	  failure: function(result, request){
	                //폼 윈도우를 닫고, 결과 처리
	                Ext.MessageBox.hide();
	                //$('#commandResult').val(result.toString());
	                if(result.isTimeout){
	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
	                }else{
	                    $('#commandResult').val("FAIL");
	                }
	            }
	    	}); 
	}
	
	// GetInfo Toggle (ModemMaxGadget)
	// 체크된 항목만 보여줍니다.
    var toggleFormPanel;
    var toggleWin;
    var toggle = new Array();
    toggle= [false,false,false,false,false,false,false,false];
    function getInfoToggle(){
        // 아직 안닫힌 경우 기존 창은 닫기
        if(Ext.getCmp('toggleWindow')){
            Ext.getCmp('toggleWindow').close();
        }
	if($('#modemTypeHidden').val() == 'MMIU') {
        toggleFormPanel = new Ext.FormPanel({
            id : 'toggleform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'checkbox',
                    id : 'selectAll',
                    fieldLabel : 'All',
                    labelSeparator : "",
                    handler: function() {
                    	if($("#selectAll").is(':checked')){
	                        Ext.getCmp('cpuUsage').setValue(true);
	                        Ext.getCmp('memoryUsage').setValue(true);
	                        Ext.getCmp('txSize').setValue(true);
	                        //Ext.getCmp('RSSI').setValue(true);
	                        //Ext.getCmp('LQI').setValue(true);
	                        //Ext.getCmp('ETX').setValue(true);
	                        Ext.getCmp('LastCommDate').setValue(true);
	                        //Ext.getCmp('parentId').setValue(true);
	                        toggle= [true,true,true,false,false,false,true,false];
        				}else{
        					Ext.getCmp('cpuUsage').setValue(false);
        		 			Ext.getCmp('memoryUsage').setValue(false);
        		 			Ext.getCmp('txSize').setValue(false);
        		 			Ext.getCmp('RSSI').setValue(false);
        		 			Ext.getCmp('LQI').setValue(false);
        		 			Ext.getCmp('ETX').setValue(false);
        		 			Ext.getCmp('LastCommDate').setValue(false);
        		 			Ext.getCmp('parentId').setValue(false);
        		 			toggle= [false,false,false,false,false,false,false,false];
        				}
                    }	
                },
                {
                    xtype : 'checkbox',
                    id : 'cpuUsage',
                    fieldLabel : 'CPU Usage',
                    listeners :{
                    	change: function(){
                    		if(!toggle[0])
                    			toggle[0]=true;
                    		else
                    			toggle[0]=false;
                    	}
                    },
                },
                {
                    xtype : 'checkbox',
                    id : 'memoryUsage',
                    fieldLabel : 'Memory Usage',
                    listeners :{
                    	change: function(){
                    		if(!toggle[1])
                    			toggle[1]=true;
                    		else
                    			toggle[1]=false;
                    	}
                    },
                },
                {
				    xtype : 'checkbox',
				    id : 'txSize',
				    fieldLabel : 'Total TX Size',
				    listeners :{
				    	change: function(){
                    		if(!toggle[2])
                    			toggle[2]=true;
                    		else
                    			toggle[2]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'RSSI',
				    fieldLabel : 'RSSI',
				    hidden : true,
				    listeners :{
				    	change: function(){
                    		if(!toggle[3])
                    			toggle[3]=true;
                    		else
                    			toggle[3]=false;
                    	}
				    },
				},
		 	{
				    xtype : 'checkbox',
				    id : 'LQI',
				    fieldLabel : 'LQI',
				    hidden : true,
				    listeners :{
				    	change: function(){
                    		if(!toggle[4])
                    			toggle[4]=true;
                    		else
                    			toggle[4]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'ETX',
				    fieldLabel : 'ETX',
				    hidden : true,
				    listeners :{
				    	change: function(){
                    		if(!toggle[5])
                    			toggle[5]=true;
                    		else
                    			toggle[5]=false;
                    	}
				    },
				}, 
				{
				    xtype : 'checkbox',
				    id : 'LastCommDate',
				    fieldLabel : 'Last Comm. Time',
				    listeners :{
				    	change: function(){
                    		if(!toggle[6])
                    			toggle[6]=true;
                    		else
                    			toggle[6]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'parentId',
				    fieldLabel : 'Parent Node Id',
				    hidden : true,
				    listeners :{
				    	change: function(){
                    		if(!toggle[7])
                    			toggle[7]=true;
                    		else
                    			toggle[7]=false;
                    	}
				    },
				} 
            ], // items
            buttons : [
                {
                    id: 'toggleSendBtn',
                    text: 'Get Info',
                    listeners: {
                        click: function(btn,e){
                        	Ext.getCmp('toggleWindow').close();
                        	modemCoAP(toggle); //Get Info!
                        	toggle= [false,false,false,false,false,false,false,false];
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('toggleWindow').close();
                            toggle= [false,false,false,false,false,false,false,false];
                        }
                    }
                }
            ] //buttons
        });
	}else{
		toggleFormPanel = new Ext.FormPanel({
            id : 'toggleform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
            	  {
                      xtype : 'checkbox',
                      id : 'selectAll',
                      fieldLabel : 'All',
                      labelSeparator : "",
                      handler: function() {
                    	  if($("#selectAll").is(':checked')){
  	                        Ext.getCmp('cpuUsage').setValue(true);
  	                        Ext.getCmp('memoryUsage').setValue(true);
  	                        Ext.getCmp('txSize').setValue(true);
  	                        Ext.getCmp('RSSI').setValue(true);
  	                        Ext.getCmp('LQI').setValue(true);
  	                        Ext.getCmp('ETX').setValue(true);
  	                        Ext.getCmp('LastCommDate').setValue(true);
  	                        Ext.getCmp('parentId').setValue(true);
  	                       toggle= [true,true,true,true,true,true,true,true];
          				}else{
          					Ext.getCmp('cpuUsage').setValue(false);
          		 			Ext.getCmp('memoryUsage').setValue(false);
          		 			Ext.getCmp('txSize').setValue(false);
          		 			Ext.getCmp('RSSI').setValue(false);
          		 			Ext.getCmp('LQI').setValue(false);
          		 			Ext.getCmp('ETX').setValue(false);
          		 			Ext.getCmp('LastCommDate').setValue(false);
          		 			Ext.getCmp('parentId').setValue(false);
          		 			toggle= [false,false,false,false,false,false,false,false];
          				}
                      }	
                  },
            	{
                    xtype : 'checkbox',
                    id : 'cpuUsage',
                    fieldLabel : 'CPU Usage',
                    listeners :{
                    	change: function(){
                    		if(!toggle[0])
                    			toggle[0]=true;
                    		else
                    			toggle[0]=false;
                    	}
                    },
                },
                {
                    xtype : 'checkbox',
                    id : 'memoryUsage',
                    fieldLabel : 'Memory Usage',
                    listeners :{
                    	change: function(){
                    		if(!toggle[1])
                    			toggle[1]=true;
                    		else
                    			toggle[1]=false;
                    	}
                    },
                },
                {
				    xtype : 'checkbox',
				    id : 'txSize',
				    fieldLabel : 'Total TX Size',
				    listeners :{
				    	change: function(){
                    		if(!toggle[2])
                    			toggle[2]=true;
                    		else
                    			toggle[2]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'RSSI',
				    fieldLabel : 'RSSI',
				    listeners :{
				    	change: function(){
                    		if(!toggle[3])
                    			toggle[3]=true;
                    		else
                    			toggle[3]=false;
                    	}
				    },
				},
		 	{
				    xtype : 'checkbox',
				    id : 'LQI',
				    fieldLabel : 'LQI',
				    listeners :{
				    	change: function(){
                    		if(!toggle[4])
                    			toggle[4]=true;
                    		else
                    			toggle[4]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'ETX',
				    fieldLabel : 'ETX',
				    listeners :{
				    	change: function(){
                    		if(!toggle[5])
                    			toggle[5]=true;
                    		else
                    			toggle[5]=false;
                    	}
				    },
				}, 
				{
				    xtype : 'checkbox',
				    id : 'LastCommDate',
				    fieldLabel : 'Last Comm. Time',
				    listeners :{
				    	change: function(){
                    		if(!toggle[6])
                    			toggle[6]=true;
                    		else
                    			toggle[6]=false;
                    	}
				    },
				},
				{
				    xtype : 'checkbox',
				    id : 'parentId',
				    fieldLabel : 'Parent Node Id',
				    listeners :{
				    	change: function(){
                    		if(!toggle[7])
                    			toggle[7]=true;
                    		else
                    			toggle[7]=false;
                    	}
				    },
				}
            ], // items
            buttons : [
                {
                    id: 'toggleSendBtn',
                    text: 'Get Info',
                    listeners: {
                        click: function(btn,e){
                        	Ext.getCmp('toggleWindow').close();
                        	modemCoAP(toggle); //Get Info!
                        	toggle= [false,false,false,false,false,false,false,false];
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('toggleWindow').close();
                            toggle= [false,false,false,false,false,false,false,false];
                        }
                    }
                }
            ] //buttons
        });
	}
        var toggleWin = new Ext.Window({
        		id     : 'toggleWindow',
	            title : 'Get Info',
	            autoHeight: true,
	            width  : 210,
	            //layout : 'fit',
	            bodyStyle   : 'padding: 10px 10px 10px 10px;',
	            items  : [toggleFormPanel],
        });

        toggleWin.show();
    }

	/**
	 * GetInfo (CoAP)
	 **/
	function modemCoAP(toggle){
		Ext.define('modemInfoAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout2/1000 + 's...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
		$('#commandResult').val("Request modem info ....");
		modemInfoAjax.request({
	        type : "POST",
	        timeout : extAjaxTimeout2,
	        params : {
	        	    target  			: modemId
	            ,   loginId 			: loginId 
	            ,   chkCPU  			: toggle[0]       // 체크된 항목만 보여줍니다.
				,   chkMemory  		    : toggle[1]
	        	,   chkTxSize  		    : toggle[2]
				,   chkRSSI  			: toggle[3]
				,   chkLQI  			: toggle[4]
				,	chkETX			    : toggle[5]
				,   chkLastCommDate     : toggle[6]
				,   chkNodeId  		    : toggle[7]
            },
	        url : '${ctx}/gadget/device/command/cmdModemCoAP.do',
	        success : function (result, request){
	        	Ext.MessageBox.hide();
	        	var jsonData = Ext.util.JSON.decode( result.responseText );
	        	if(!jsonData.status){
	                $('#commandResult').val("[FAIL] " + jsonData.rtnStr);
	                   return;
	            }
	            if(jsonData.status.length>0 && jsonData.status=='SUCCESS'){
	                $('#commandResult').val("[Modem Info] \n"+jsonData.rtnStr);
	            }else{
	            	$('#commandResult').val("[FAIL] " + jsonData.rtnStr);
	            }
	        },
	        failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                //$('#commandResult').val(result.toString());
                if(result.isTimeout){
                    $('#commandResult').val("[FAIL]If protocol type is SMS, check the results in Async History tab");
                }else{
                    $('#commandResult').val("FAIL");
                }
            }

	        });
	}
	
	/**
	 * resetModem (CoAP)
	 **/
	function resetModem(){
		Ext.Msg.confirm('Reset Modem',
                'Are you sure to send a command?',
                function(btn, txt){
                    if(btn == 'yes'){
                    	Ext.define('resetModemAjax', {
                            extend: 'Ext.data.Connection',
                            singleton: true,
                            constructor : function(config){
                                this.callParent([config]);
                                this.on("beforerequest", function(){
                                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout2/1000 + 's...',
                                        scope: this,
                                    });
                                });
                                this.on("requestcomplete", function(){
                                    Ext.MessageBox.hide();
                                });
                            }
                        });
                    	$('#commandResult').val("Request Reset Modem....");
                    	resetModemAjax.request({
                	        type : "POST",
                	        timeout : extAjaxTimeout2,
                	        params : {
                	        	  target  			: modemId
                	            , loginId 			: loginId 
                	        },
                	        url : '${ctx}/gadget/device/command/cmdResetModem.do',
                	        success : function (result, request){
                	        	var jsonData = Ext.util.JSON.decode( result.responseText );
                	            if(!jsonData.status){
                	                $('#commandResult').val("[FAIL] " + jsonData.rtnStr);
                	                   return;
                	            }
                	            if(jsonData.status.length>0 && jsonData.status=='SUCCESS'){
                	                $('#commandResult').val("[Success] sending a command\n"+jsonData.rtnStr);
                	            }else{
                	            	$('#commandResult').val("[FAIL] " + jsonData.rtnStr);
                	            }
                	        },
                	        failure: function(result, request){
            	                //폼 윈도우를 닫고, 결과 처리
            	                Ext.MessageBox.hide();
            	                //$('#commandResult').val(result.toString());
            	                if(result.isTimeout){
            	                    $('#commandResult').val("[FAIL]If protocol type is SMS, check the results in Async History tab");
            	                }else{
            	                    $('#commandResult').val("FAIL");
            	                }
            	            }
                	        });
                    }
        });
		
	}
	
	function commandTraceroute() {
		Ext.Msg.prompt('Options For Traceroute', 'Please enter hop count.<br/>The maximum number of hops for the target search.<br/>',
				function (btn, text) {
					if (btn=='ok') {
						if (text != null) {
							if (3 < text.length || text.length < 1 || text == 0 || text > 30) {
								Ext.Msg.alert('Error', 'The maximum hop count is 30.<br/>Please re-enter hop count.');
								return false;
							}
							
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if (isNaN(text) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							} else {
								doTraceroute(text);	
							}
						} else {
							Ext.Msg.alert('Error', 'Please re-enter hop count.');
							return false;
						}							
					}
		},this, false, '5');
	}
	
    function doTraceroute(hopCount) {
    	Ext.Msg.wait('Waiting for the response.', 'Message');        	
        $('#commandResult').val("Request Modem Traceroute....");
        $.ajax({
      	  url: '${ctx}/gadget/device/command/cmdModemTraceroute.do',
      	  dataType: 'json',
      	  async: true,
      	  data: {'target' : modemId
              ,'loginId' : loginId
              ,'hopCount' : hopCount},
      	  success: function(returnData) {
      		if (!returnData.status) {
      			Ext.Msg.hide();
                $('#commandResult').val("FAIL");
                return;
            }
      		
            if (returnData.status.length>0 && returnData.status!='SUCCESS') {
            	Ext.Msg.hide();
                $('#commandResult').val("Modem Traceroute: " + returnData.status);
            } else {
            	Ext.Msg.hide();
            	$('#commandResult').val(""); 
                $('#commandResult').val(returnData.jsonString); 
            }
      	  }
      	});
    }
    //fcColumnChartDiv

    //report window(Excel)
    var win;
    function openExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px,  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.modemid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.modem.type"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";
        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.fw.hw.ver"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lastcomm"/>";
        fmtMessage[8] = "<fmt:message key="aimir.excel.modemList"/>"
        fmtMessage[9] = "Protocol Type"
        fmtMessage[10] = "<fmt:message key="aimir.phoneNumber"/>"
        fmtMessage[11] = "<fmt:message key="aimir.macaddress"/>"

        obj.condition   = getCondition();
        obj.fmtMessage  = fmtMessage;

        if(win)
            win.close();
        win = window.open("${ctx}/gadget/device/modemMaxExcelDownloadPopup.do", "ModemListExcel", opts);
        win.opener.obj = obj;

    }

    var winPopup;
    function openCommInfoExcelReport() {
    	var opts = "width=600px, height=400px, left=100px, top=100px,  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();
        
        fmtMessage[0] = "Communication Info.";
        fmtMessage[1] = "<fmt:message key="aimir.number"/>";
        fmtMessage[2] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[3] = "<fmt:message key="aimir.24within"/>";
        fmtMessage[4] = "<fmt:message key="aimir.24over"/>";
        fmtMessage[5] = "<fmt:message key="aimir.48over"/>";
        fmtMessage[6] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";
        fmtMessage[7] = "CommError";
        fmtMessage[8] = "SecurityError";
    	
        obj.condition   = getCondition();
        obj.fmtMessage  = fmtMessage;

        if(winPopup) {
        	winPopup.close();
        }
            
        winPopup = window.open("${ctx}/gadget/device/modemCommInfoExcelDownloadPopup.do", "ModemListExcel", opts);
        winPopup.opener.obj = obj;
    }
  
    var chk = 0;
    function successFail(packetSize, count) {
   		if(chk == 0){
          $('#commandResult').val("Success");
          chk++;
   		}else if(chk == 1){
   		  $('#commandResult').val("Fail");
   		  chk--;
   		}
    }

    // Meter Baud Rate Panel[Get/Set Method] draw panel
    // 패널 그리기
    var baudRateFormPanel;
    var baudRateWin;
    function setBaudRate() {
        // 아직 안닫힌 경우 기존 창은 닫기
        if(Ext.getCmp('baudRateWindow')){
            Ext.getCmp('baudRateWindow').close();
        }

        baudRateFormPanel = new Ext.FormPanel({
            id : 'baudrateform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio',
                    fieldLabel : 'Get/Set ',
                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET'  }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('rateValueText').setValue('38400');
                                Ext.getCmp('rateValueText').enable();
                                Ext.getCmp('baudSendBtn').setText('SET BaudRate');
                            }else{
                                //button hide
                                Ext.getCmp('rateValueText').setValue('38400');
                                Ext.getCmp('rateValueText').disable();
                                Ext.getCmp('baudSendBtn').setText('GET BaudRate');
                            }
                        }
                    },
                }, //xtype : radio
                {
                    xtype: 'textfield',
                    id : 'rateValueText',
                    width : 100,
                    //emptyText: '38400',
                    fieldLabel: 'Set Value ',
                    disabled : true,
                }
            ], // items
            buttons : [
                {
                    id: 'baudSendBtn',
                    text: 'Get BaudRate',
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            setBaudRateAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('baudRateWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        var baudRateWin = new Ext.Window({
            id     : 'baudRateWindow',
            title : 'Get/Set Meter BaudRate',
            pageX : $("#gadget_body").width()/2-100,
            pageY : 400,
            height : 150,
            width  : 270,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [baudRateFormPanel],
        });

        baudRateWin.show();
    }

    // Meter Baud Rate Action [Get/Set Method] change parameters as radio button
    // 라디오버튼 선택에 따라 ajax 파라미터 변경
    function setBaudRateAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio').getValue().inputValue;
        var actionRequestValue = Ext.getCmp('rateValueText').getValue();

        Ext.define('baudRateAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        // Ajax Call (timeout : 180 seconds -> important)
        baudRateAjax.request({
            url :  '${ctx}/gadget/device/command/cmdMeterBaudRate.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                mdsId         : mdsId,
                loginId       : loginId,
                requestType   : actionRequestType,
                requestValue  : actionRequestValue
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                Ext.getCmp('baudRateWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Baud Rate : " + jsonData.baudRate +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                //$('#commandResult').val(result.toString());
                if(result.isTimeout){
                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#commandResult').val(" "+request.statusText);
                }
            }
        });

    }

    // SORIA-MBB(SMS) Request SNMP Daemon Open (161port)
    function snmpDaemon(){
        // Ajax 정의
        Ext.define('mbbSnmpDaemonAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout/1000 + 's...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });

        Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                'Reqeust SNMP-OPEN to ['+mdsId+'] ',
                function(btn, txt){
                    if(btn != 'yes'){
                        return false;
                    }else{
                        // Ajax Call (timeout : 180 seconds -> important)
                        mbbSnmpDaemonAjax.request({
                            url : "${ctx}/gadget/device/command/cmdSnmpDaemon.do",
                            method : 'POST',
                            timeout : extAjaxTimeout,
                            params: {
                                modemId : mdsId,
                                loginId : loginId,
                            },
                            success: function (result, request){
                                //폼 윈도우를 닫고, 결과 처리
                                Ext.MessageBox.hide();
                                var jsonData = Ext.util.JSON.decode( result.responseText );
                                if(jsonData.status == undefined){
                                    $('#commandResult').val(" "+jsonData.rtnStr);
                                }else{
                                    $('#commandResult').val(" "+jsonData.cmdResult);
                                }

                            },
                            failure: function(result, request){
                                //폼 윈도우를 닫고, 결과 처리
                                Ext.MessageBox.hide();
                                if(result.isTimeout){
                                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                }else{
                                    $('#commandResult').val(" "+request.statusText);
                                }
                            }
                        });
                    } //~confirm inner function
                }); //~Ext.Msg.confirm
    }

    // Get Log [Get Method Only]
    function getModemEventLog(){
    	var getLogTimeout = 300000;
    	Ext.MessageBox.prompt('Options For Modem-Event-Log', 'Please enter the number of log<br>',
    			function(btn, numTxt) {
		    		// 수집할 로그의 개수 지정 (textbox)		
		    		var count = numTxt.trim();
    				if(btn=='ok'){
    					// 입력 값 검증
    					if(isNaN(count)==true || count.length < 1){
    						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
    						return false;
    					}
    					// Ajax 정의    			        
    			        Ext.define('modemEventAjax', {
    			            extend: 'Ext.data.Connection',
    			            singleton: true,
    			            constructor : function(config){
    			                this.callParent([config]);
    			                this.on("beforerequest", function(){
                                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                                        text: '<fmt:message key="aimir.maximum"/> '+ getLogTimeout/1000 + 's...',
                                        scope: this,
                                    });
    			                });
    			                this.on("requestcomplete", function(){
    			                    Ext.MessageBox.hide();
    			                });
    			            }
    			        });
    					// Ajax call (timeout : 180 seconds -> important)
    			        modemEventAjax.request({
    			            url : "${ctx}/gadget/device/command/cmdModemEventLog.do",
    			            method : 'POST',
                            timeout : getLogTimeout,
    			            params: {
    			                mdsId : mdsId,
    			                loginId : loginId,
    			                count : count
    			            },
    			            success: function (result, request){
    			                //폼 윈도우를 닫고, 결과 처리
    			                Ext.MessageBox.hide();
    			                var jsonData = Ext.util.JSON.decode( result.responseText );
    			                var returnString="";
    			                if(jsonData.eventLogs != null){
                                    returnString += "Click the 'Get Log File' button to download.\n\n"
                                }
                                if(jsonData.rtnStr == undefined){
                                    returnString +=
                                            '# Operation Result : ' + jsonData.status +
                                            '\n# Logs : ' + jsonData.eventLogs;
                                }else{
                                    returnString +=
                                            '# Operation Result : ' + jsonData.status +
                                            '\n# Message 1 : ' + jsonData.rtnStr +
                                            '\n# Message 2 : ' + jsonData.cmdResult +
                                            '\n# Logs : ' + jsonData.eventLogs;
                                }
    			                $('#commandResult').val(returnString);
    			                getLogResult=jsonData.eventLogs;
    			            },
    			            failure: function(result, request){
    			                //폼 윈도우를 닫고, 결과 처리
    			                Ext.MessageBox.hide();
    			                //$('#commandResult').val(result.toString());
                                if(result.isTimeout){
                                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                }else{
                                    $('#commandResult').val(" "+request.statusText);
                                }
    			            }
    			        });
    				}	
    				}, this, false, '1'); //<prompt> scope=this, multiline=false, default=1    	
                  
    }
    
    ////Export to file(get log)
    var winMcuEventLog;
    function openGetLogExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var result= getLogResult;
        if(result=="" || result==null){
        	Ext.Msg.alert("File Download","First, execute the Get Log");
        	return;
        }
        obj.supplierId = supplierId;
        obj.result	   = result;

        if(winMcuEventLog)
        	winMcuEventLog.close();
        winMcuEventLog = window.open("${ctx}/gadget/device/modemGetLogExcelDownloadPopup.do", "Log Excel", opts);
        winMcuEventLog.opener.obj = obj;
    }

    // Schedule Tab : NI Command, RetryCount [GET]
    // retryCountFlag used to flag. (GET action shold success before SET action)
    var retryCountFlag = false;
    function getRetryCountAction(){
        // Ajax Definition
        Ext.define('retryCountAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    retryCountFlag = false;
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });

        // Ajax Call (timeout : 180 seconds -> important)
        retryCountAjax.request({
            url : "${ctx}/gadget/device/command/cmdRetryCount.do",
            method : 'POST',
            timeout : extAjaxTimeout,
            params: {
                modemId : mdsId,
                loginId : loginId,
                requestType : 'GET',
                requestValue : '0'
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                if(jsonData.status == undefined){
                    $('#retryComment').html(" "+jsonData.rtnStr);
                }else{
                    $('#retryComment').html(" "+jsonData.cmdResult);
                    $('#retryCountInput').val(jsonData.retryCount);
                }

                // Set true when retryCount is comming
                if(jsonData.status.toUpperCase()=='SUCCESS'){
                    retryCountFlag = true;
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                retryCountFlag = false;
                if(result.isTimeout){
                    $('#retryComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#retryComment').html(" "+request.statusText);
                }
            }
        });


    }

    // Schedule Tab : NI Command, RetryCount [SET]
    function setRetryCountAction() {
        if(!retryCountFlag){
            // [GET] first, then update function is possible.
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','Check the current value by GET button before update');
            return false;
        }
        // User Input
        var retryInput = $('#retryCountInput').val().trim();
        // Validation check
        if(isNaN(retryInput)==true || retryInput.length < 1){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
            return false;
        }else{
            Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update RetryCount to ['+retryInput+'] ',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Ajax Call (timeout : 180 seconds -> important)
                            retryCountAjax.request({
                                url : "${ctx}/gadget/device/command/cmdRetryCount.do",
                                method : 'POST',
                                timeout : extAjaxTimeout,
                                params: {
                                    modemId : mdsId,
                                    loginId : loginId,
                                    requestType : 'SET',
                                    requestValue : retryInput
                                },
                                success: function (result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    retryCountFlag = true;
                                    var jsonData = Ext.util.JSON.decode( result.responseText );
                                    if(jsonData.status == undefined){
                                        $('#retryComment').html(" "+jsonData.rtnStr);
                                    }else{
                                        $('#retryComment').html(" "+jsonData.cmdResult);
                                        $('#retryCountInput').val(jsonData.retryCount);
                                    }

                                },
                                failure: function(result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    retryCountFlag = false;
                                    if(result.isTimeout){
                                        $('#retryComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                    }else{
                                        $('#retryComment').html(" "+request.statusText);
                                    }
                                }
                            });
                        } //~confirm inner function
            }); //~Ext.Msg.confirm
        } //~validation check

    }

    // Schedule Tab : NI Command, Metering Interval
    // mtrIntervalFlag used to flag. (GET action shold success before SET action)
    var mtrIntervalFlag = false;
    function getMtrIntervalAction(){
        // Ajax Definition
        Ext.define('mtrIntervalAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    mtrIntervalFlag = false;
                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout/1000 + 's...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });

        // Ajax Call (timeout : 180 seconds -> important)
        mtrIntervalAjax.request({
            url : "${ctx}/gadget/device/command/cmdMeteringInterval.do",
            method : 'POST',
            timeout : extAjaxTimeout,
            params: {
                modemId : mdsId,
                loginId : loginId,
                requestType : 'GET',
                requestValue : '0'
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                if(jsonData.status == undefined){
                    $('#intervalComment').html(" "+jsonData.rtnStr);
                }else{
                    $('#intervalComment').html(" "+jsonData.cmdResult);
                }
                // Set true when meteringInterval is comming
                if(jsonData.status.toUpperCase()=='SUCCESS'){
                    mtrIntervalFlag = true;
                    var mtrIntervalArray = (jsonData.interval).split(':');
                    $('#intervalHour').val(mtrIntervalArray[0]);
                    $('#intervalMinute').val(mtrIntervalArray[1]);
                    $('#intervalSecond').val(mtrIntervalArray[2]);
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                mtrIntervalFlag = false;
                if(result.isTimeout){
                    $('#intervalComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#intervalComment').html(" "+request.statusText);
                }
            }
        });
    }

    // Metering Interval Action [SET]
    function setMtrIntervalAction(){
        if(!mtrIntervalFlag){
            // [GET] request should success first, then update function is possible.
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','Check the current value by GET button before update');
            return false;
        }
        // User Input
        var itvHour = $('#intervalHour').val().trim();
        var itvMin = $('#intervalMinute').val().trim();
        var itvSec = $('#intervalSecond').val().trim();
        var itvTotal = 0;
        // Validation Check
        if(isNaN(itvHour) || isNaN(itvMin) || isNaN(itvSec)){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
            return false;
        }else{
            itvTotal = parseInt(itvHour)*3600 + parseInt(itvMin)*60 + parseInt(itvSec);
            Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update MeteringInterval to ['+itvTotal+'] seconds',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Ajax Call (timeout : 60 seconds -> important)
                            mtrIntervalAjax.request({
                                url : "${ctx}/gadget/device/command/cmdMeteringInterval.do",
                                method : 'POST',
                                timeout : extAjaxTimeout,
                                params: {
                                    modemId : mdsId,
                                    loginId : loginId,
                                    requestType : 'SET',
                                    requestValue : parseInt(itvTotal)
                                },
                                success: function (result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    mtrIntervalFlag = true;
                                    var jsonData = Ext.util.JSON.decode( result.responseText );
                                    if(jsonData.status == undefined){
                                        $('#intervalComment').html(" "+jsonData.rtnStr);
                                    }else{
                                        $('#intervalComment').html(" "+jsonData.cmdResult);
                                        $('#intervalUpdateResult').html("Update Result : " + jsonData.intervalStatus);
                                    }

                                },
                                failure: function(result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    mtrIntervalFlag = false;
                                    if(result.isTimeout){
                                        $('#intervalComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                    }else{
                                        $('#intervalComment').html(" "+request.statusText);
                                    }
                                }
                            });
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        } //~validation check

    }

        
        var uploadWindow; 
       	var uploadFormPanel;
       	function drawUploadPanel(){
       		// 아직 안닫힌 경우 기존 창은 닫기
       		if(Ext.getCmp('uploadWindowPanel')){
       			Ext.getCmp('uploadWindowPanel').close();
       		} 		
       		
       		var uploadFormPanel =  new Ext.form.FormPanel({ 		      		         		       
       		        id          : 'formpanel',
       		        defaultType : 'fieldset', 		 
       		        bodyStyle:'padding:0px 0px 0px 0px',
       		        frame       : true,
       		        
       		        labelWidth  : 100, 		        
       		        items       : [
       	               /* {
       		            	xtype: 'textfield',
       		            	id : 'id',
       		            	width : 240,
       		            	emptyText: 'test',
       		                fieldLabel: ' take over ',
       		                disabled : true,
       		            }, */{
       		            	xtype: 'label',
       		            	id : 'infolabel',
       		            	text : ' Are you sure to upload OTA File ?',
       		            }
       		            
       		        ],
       		        buttons: [
       		            {
       		            	id : 'ota',
      			    	 	text: ' OK ',
      			    	 	// click => AjaxUpload
      			    	 	
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
       		        title  : ' OTA ',
       		        pageX : 600,
                    pageY : 500,
       		        height : 120,
       		        width  : 290,
       		        layout : 'fit',
       		        bodyStyle   : 'padding: 5px 5px 5px 5px;',
       		        items  : [uploadFormPanel],
       		    });
       		    
       		    uploadWindow.show();
       		 new AjaxUpload('ota', {
    	            name : 'otaFile',
    	            responseType : 'json',
    	            onSubmit : function(file , ext){         
    	                    //파일 확장자 검색
    	                    if (!(ext && /^(dwl|DWL|bin|BIN|mot|MOT)$/.test(ext))){
    	                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not OTA file');
    	                        return false;
    	                    }

    	                    this._settings.action = '${ctx}/gadget/device/command/cmdLineSORIA.do', //아직 구현안됨
    	                    this._settings.data = {
    	                    	modemId     : modemId,
    	                        loginId     : loginId,
    	                        cmd         : "cmdOTAStart",
    	                        ext         : ext 
    	                    };

    	            	Ext.Msg.wait('Waiting for response.', 'Wait !');
    	                return true;
    	            },
    	            onComplete : function(file, response){
    	            	Ext.Msg.hide();
    	            	Ext.Msg.alert('OTA', response.rtnStr);
    	            }
    	        });
       	}
       	
       	var modemOTA = function(){
       		
       	}
       	
    	function getAPN() {
			if ($('#protocolTypeHidden').val() == 'SMS') {
				if($('#modemTypeHidden').val() == 'MMIU') {
					var timeoutPeriod = 30000;
					
					$('#commandResult').val("Request Get APN...");
					Ext.define('runAjax', {
			            extend: 'Ext.data.Connection',
			            singleton: true,
			            constructor : function(config){
			                this.callParent([config]);
			                this.on("beforerequest", function(){
			                    Ext.MessageBox.wait("Request Get APN...", '<fmt:message key="aimir.info"/>', {
			                        text: '<fmt:message key="aimir.maximum"/> '+ (timeoutPeriod-10000)/1000 + ' second...',
			                        scope: this,
			                    });
			                });
			                this.on("requestcomplete", function(){
			                    Ext.MessageBox.hide();
			                });
			            }
			        });
					
					runAjax.request({
						  url : '${ctx}/gadget/device/command/getUseddAPN.do', 
				    	  type : "POST",
				    	  timeout : timeoutPeriod,
					      params : {
					    	      target : modemId
				                , loginId : loginId 
					      },
				    	  success: function(result, request) {
				    		 	Ext.MessageBox.hide();
					        	var returnData = Ext.util.JSON.decode(result.responseText);
								
								if(returnData.status == "SUCCESS"){
									var apnInfo = "APN NAME\t : " + returnData.apnName + "\n"
												+ "APN ID\t : " + returnData.apnId + "\n"
												+ "APN PW\t : " + returnData.apnPw;		
												
									$('#commandResult').val(apnInfo);
								} else {
									$('#commandResult').val(returnData.rtnStr);
								}
				    	  },
				    	  failure: function(result, request){
				                //폼 윈도우를 닫고, 결과 처리
				                Ext.MessageBox.hide();
				                //$('#commandResult').val(result.toString());
				                if(result.isTimeout){
				                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
				                }else{
				                    $('#commandResult').val("FAIL");
				                }
				            }
				    }); 
            	}
			} else {
				$('#commandResult').val("Request get APN....");
	    		Ext.Msg.wait('Waiting for response.', 'Wait !');
	    		$.ajax({
	    	    	  url: '${ctx}/gadget/device/command/cmdGetApn.do',
	    	          method : 'POST',
	    	    	  dataType: 'json',
	    	    	  async: true,
	    	    	  data: {'mdsId' : mdsId
	    	                , 'loginId' : loginId 
	    	                , 'requestType' : 'GET'
	    	                , 'requestValue' : ''
	    	                },
	    	    	  success: function(returnData) {
	    	    		  Ext.Msg.hide();
	    	    		  if(!returnData.status){
	                          $('#commandResult').val("FAIL");
	                             return;
	                      }
	                      if(returnData.status.length>0 && returnData.status!='SUCCESS'){
	                          $('#commandResult').val("Get APN: "+returnData.status);
	                      }else{
	                      	$('#commandResult').val(""); 
	                          $('#commandResult').val(returnData.jsonString);
	                      }
	    	    	  },
	    	    	  failure: function(result, request){
	  	                //폼 윈도우를 닫고, 결과 처리
	  	                Ext.MessageBox.hide();
	  	                //$('#commandResult').val(result.toString());
	  	                if(result.isTimeout){
	  	                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
	  	                }else{
	  	                    $('#commandResult').val("FAIL");
	  	                }
	  	            },
	    	    	  error: function(response){
	    	    		  Ext.Msg.hide();
	    	    		   $('#commandResult').val("Get APN: FAIL");
	    	    	  }
	    	    	});
			}
    	}
    	
    	function getSetParameterCaptcha(){
    		//CaptchaPanel("getSetParam");
    		getSetParameter();
    	}
    	// Get/Set Parameter
		// 0x2002 Modem Reset Time
		// 0x2003 Modem Mode
		// 0x2010 SNMP Sever IPv6/Port
		// 0x2011 Alarm/Event Command ON_OFF
		// 0x2013 Transmit Frequency
        // 0x2015 SNMP Trap ON_OFF
        // 패널 그리기
    	var getSetParamWin;
        function getSetParameter() {

       		var cmdLineWin;
       		var opts = "width=670px, height=650px, left=100px,"
       				+ " top=200px, resizable=no, status=no, location=no";

       		var obj = new Object();
       		var condition = new Array();

    		obj.modemId = modemId;
    		obj.loginId = loginId;
			obj.modemType = modemType;
			obj.modemProtocolType = $('#protocolTypeHidden').val();
    		obj.supplierId = supplierId ;

       		if (getSetParamWin) {
       			getSetParamWin.close();
       		}
       		
       		if ( mdsId == undefined || mdsId == "" ){
                   Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.selectmeter'/>');
                   return false;
       		}
       		getSetParamWin = window.open("${ctx}/gadget/device/modemGetSetParam.jsp"
       				, "Get/Set Parameter",
       				opts);
       		getSetParamWin.opener.obj = obj;

        }
        var otaWin;
        function runOta(){
    		var opts = "width=800px, height=550px, left=" + 200 + "px, top=200px, resizable=no, status=no, location=no";
    		var obj = new Object();
    		obj.pageWidth = '800';
    		obj.pageHeight = '550';
    		obj.deviceModel = "";
    		obj.modelName = tempModelName;
    		obj.condition = "";
    		obj.equip_kind = "modem";
    		obj.deviceIdString = tempDeviceId;
    		obj.loginId = loginId
    		obj.locationId = tempLocId;
    		obj.targetDeviceType = "";
    		if (otaWin){
    			otaWin.close();
    		}
    			
    		otaWin = window.open("${ctx}/gadget/device/firmware/firmwareAddPopup.do", 
    								"firmwareAdd", opts);
    		otaWin.opener.obj = obj;			
    	}
        
        var coapWin;
        function coapBrowser(){
    		var opts = "width=450px, height=340px, left=" + 650 + "px, top=200px, resizable=no, status=no, location=no";
    		var obj = new Object();
    		obj.target = modemId;
    		obj.loginId = loginId;

    		if (otaWin){
    			otaWin.close();
    		}
    		otaWin = window.open("${ctx}/gadget/device/coap/coapBrowserPopup.do", 
    								"firmwareAdd", opts);
    		otaWin.opener.obj = obj;			
    	}

		var niWin;
        function execNICommand(){
    		var opts = "width=450px, height=340px, left=" + 650 + "px, top=200px, resizable=no, status=no, location=no";
    		var obj = new Object();
    		obj.target = modemId;
    		obj.loginId = loginId;

    		if (niWin){
    			niWin.close();
    		}
    		niWin = window.open("${ctx}/gadget/device/modemNIPopup.jsp", 
    								"Execute NI Command", opts);
    		niWin.opener.obj = obj;			
    	}


		// Async History
        var asyncHistoryGridStore;
        var asyncHistoryGridModel;
        var asyncHistoryGridPanel;
        var asyncHistoryGridInstanceOn = false;
        function getAsyncHistoryGrid(){
        	
        	checkModemId();
        	
            //setting grid panel width
            var gridWidth = $("#gadget_body").width()-20;
            var pageSize = 5;
            asyncHistoryGridStore = new Ext.data.JsonStore({
            	autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/device/getAsyncLogList.do",
                //파라매터 설정.
                baseParams: {
                	modemId: modemId,
                	startDate : $("#asyncCommandHistoryStartDateHidden").val(),
                    endDate : $("#asyncCommandHistoryEndDateHidden").val(),
                    loginId : loginId
                },
                totalProperty: 'totalCount',
                root:'rtnStr',
                fields: [
						   "rowNo"
                         , "command"
                         , "requestTime"
                         , "state"
                         , "deviceSerial"
                         , "trid"
                ],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
                
            });//Store End

            asyncHistoryGridModel = new Ext.grid.ColumnModel({
                columns: [
					 {header: "No.", dataIndex: 'rowNo', width: 100, align: 'center'}
					,{header: "Transaction ID", dataIndex: 'trid', width:gridWidth/6, align: 'center'}
					,{header: "Request Time", dataIndex: 'requestTime', width:gridWidth/6, align: 'center'}
                    ,{header: "Command", dataIndex: 'command', width:gridWidth/6, align: 'center'}
                    ,{header: "Modem Serial", dataIndex: 'deviceSerial', width:gridWidth/6, align: 'center'}
                    ,{header: "State", dataIndex: 'state', width:gridWidth/6, align: 'center',
                    	 renderer : function(value, me, record, rowNumber, columnIndex, store) {
             		 		if(record.data.state == "0") //Security Error
                 				return "Success";
             		 		else if(record.data.state == "1")
             		 			return "Waiting";
             		 		else if(record.data.state == "2")
             		 			return "Running";
             		 		else if(record.data.state == "4")
             		 			return "Terminate";
             		 		else if(record.data.state == "8")
             		 			return "Delete";
             		 		else if(record.data.state == "255")
             		 			return "Unknown";
             		 		else
             		 			return record.data.state
                 			
                				}	
                    }
                    ,{header: "Result", align: 'center', width:gridWidth/6 -105,
                        renderer: function(value, metaData, record, index) {
                        	var data = record.data;
                        	asyncRowNo = data.rowNo;
                        	asyncDeviceId = data.deviceSerial;
                            asyncTrid = data.trid;
                            asyncState = data.state;
                            asyncCommand = data.command;
                            var btnHtml = "<a href='#;' onclick='asyncResultCheck();' class='btn_blue'><span>Result</span></a>";
                            var tplBtn = new Ext.Template(btnHtml);
                            return tplBtn.apply();
                        }
                    }

                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if (asyncHistoryGridInstanceOn == false) {

                //Grid panel instance create
                asyncHistoryGridPanel = new Ext.grid.GridPanel({
                    store: asyncHistoryGridStore,
                    colModel : asyncHistoryGridModel,
                   //selectModel define.
                    singleSelect:true,
                    sm : new Ext.grid.RowSelectionModel({
            			singleSelect:true,
            			listeners: {
                            rowselect: function(smd, row, rec) {
                            	var data = rec.data;
                            	asyncRowNo = data.rowNo;
                            	asyncDeviceId = data.deviceSerial;
                                asyncTrid = data.trid;
                                asyncState = data.state;
                                asyncCommand = data.command;
                            }
                        }
            		}),
                    autoScroll:true,
                    height : 190, 
                    //scroll:false,
                    width:  gridWidth,
                    style: 'align:center;',
                    //패널 높이 설정
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    //랜더링 디비전
                    renderTo: 'asyncHistoryGrid',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                    },
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: asyncHistoryGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                asyncHistoryGridInstanceOn = true;
            } else {
                asyncHistoryGridPanel.setWidth(gridWidth);
                var bottomToolbar = asyncHistoryGridPanel.getBottomToolbar();
                asyncHistoryGridPanel.reconfigure(asyncHistoryGridStore, asyncHistoryGridModel);
                bottomToolbar.bindStore(asyncHistoryGridStore);
            }

        }// End of getMeterListByNotModemGrid
        
        var asyncWin;
        function asyncResultCheck(){
        	if(asyncState != 0)
        		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
        	else{
        		$.getJSON('${ctx}/gadget/device/getAsyncResult.do'
        	            , { 'deviceSerial' : asyncDeviceId,
        	            	'trid' : asyncTrid,
        	            	'command' : asyncCommand
        				}
        	            , function (returnData){
        	            	if(returnData.result=="")
        	            		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
        	            	else{
        	            		var opts = "width=450px, height=340px, left=" + 650 + "px, top=200px, resizable=no, status=no, location=no";
        	            		var obj = new Object();
        	            		obj.result = returnData.result;
        	            		if (asyncWin){
        	            			asyncWin.close();
        	    	    		}
        	            		asyncWin = window.open("${ctx}/gadget/device/asyncResultPopup.do", 
        	    	    								"firmwareAdd", opts);
        	            		asyncWin.opener.obj = obj;	
        	            		//Ext.Msg.alert('RESULT',returnData.result);
        	            	}
        	            });
        	}
        		
        }
        // view Popup for connected meter
        function viewConnectedMeterPopup() {
        	var path = "${ctx}/gadget/device/modemMaxGadgetPopup.do";
        	
            var params = {modemId : modemId,
                    supplierId : supplierId
                };
            $('#detail_dialog').dialog('open');
			sendDetailPostMethod(path, params, "post", "detail_view");
     }
        
        function sendDetailPostMethod(path, params, method, target) {
            $("#detailForm input[name=modemId]").val(params.modemId);
			$("#detailForm input[name=supplierId]").val(params.supplierId);            
			
            var $detailForm = document.getElementById("detailForm");
            $detailForm.action=path;

            $detailForm.submit();
        }
        
    </script>
</head>

<form name="search">
    <input type="hidden" id="viewLogType" value="" />
    <input type="hidden" id="modemDetailTabValue" value="" />

<!-- todo: 여기에 height 조절 -->



<!-- Search Background (S) -->
<div class="search-bg-withouttabs">

        <div class="searchoption-container">
              <table class="searchoption wfree">
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
                    
                    <td class="withinput" style="width: 90px;"><fmt:message key="aimir.shipment.gs1"/></td>
                    <td class="padding-r20px">
                        <input type="text" id="sGs1" style="width:189px;"/>
                    </td>
                    
                    <td class="withinput" width="130px"><fmt:message key="aimir.mcucode.fmversion"/></td>
                    <td class="padding-r20px"><input type="text" id="sModemFwVer" style="width:189px;"/></td>
                </tr>

                <tr>
               		<td class="withinput"  width="90px"><fmt:message key="aimir.fw.hwversion"/></td>
                    <td class="padding-r20px"><input type="text" id="sModemHwVer" style="width:189px;"/></td>
                
                
                    <td class="withinput"><fmt:message key="aimir.mcutype"/></td>
                    <td class="padding-r20px">
                        <select id="sMcuType" name="select" style="width:140px;">
                            <option value=""><fmt:message key="aimir.all"/></option>
                            <c:forEach var="mcuType" items="${mcuType}">
                                <option value="${mcuType.id}">${mcuType.descr}</option>
                            </c:forEach>
                        </select>
                    </td>

                    <td class="withinput"><fmt:message key="aimir.mcuid"/></td>
                    <td class="padding-r20px"><input type="text" id="sMcuName" style="width:189px;"/></td>
                    
                    <td class="withinput"><fmt:message key="aimir.location"/></td>
                    <td colspan="1">
                	
                	<!-- 검색어 표시 -->
                    <input name="searchWord" id='searchWord' class="billing-searchword" type="text" style="width:189px;"/>
                    <!-- 실제 LocationID값이 저장됨 -->
                    <input type='hidden' id='sLocationId' value=''></input>
                    </td>
                    
                </tr>

                <tr>
               		<td class="withinput"  width="90px"><fmt:message key="aimir.module.build"/></td>
                    <td class="padding-r20px"><input type="text" id="sModuleBuild" style="width:189px;"/></td>
                    
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
                        <span><input name="select"  id="sInstallStartDate" class="day" type="text" onChange="sDateChange(this)"></span>
                        <span><input value="~" class="between" type="text"></span>
                        <span><input name="select"  id="sInstallEndDate" class="day" type="text" onChange="sDateChange(this)"></span>

                        <input id="sInstallStartDateHidden" type="hidden">
                        <input id="sInstallEndDateHidden"   type="hidden">

                    </td>
                    <td class="withinput" ><fmt:message key="aimir.lastcomm" /></td>
                    <td colspan="1" name ='select'>
                        <!--span><input id="sLastcommStartDate" class="day" type="text" value="${yesterday}"></span-->
                        <span><input id="sLastcommStartDate" class="day" type="text"></span>
                        <span><input value="~" class="between" type="text"></span> <!--span><input id="sLastcommEndDate" class="day" type="text" value="${today}"></span-->
                        <span><input id="sLastcommEndDate" class="day" type="text"></span>
                        <input id="sLastcommStartDateHidden" type="hidden">
                        <input id="sLastcommEndDateHidden" type="hidden">
                    </td>
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
                            	<li><a href="javascript:searchModem()" class="on"><fmt:message key="aimir.button.search"/></a></li>
                            </ul>
                        </div>
                    </td>
                    <!-- Search Button (E) -->
                </tr>
            </table>

            <div class="clear">
              <div id="treeDiv_0Outer" class="tree-billing auto" style="display:none;">
                  <div id="treeDiv_0"></div>
              </div>
            </div>

        </div>
</div>
</form>
<!-- Search Background (E) -->



<div id="gadget_body">
    <div class="bodyleft_meterchart">
        <div id="fcPieChartDiv">
            The chart will appear within this DIV. This text will be replaced by the chart.
        </div>
		
		<ul>
			<li>
				<a href="#" onClick="openCommInfoExcelReport();" class="btn_blue" style="float: right;">
					<span style="padding-bottom: 5px;"><fmt:message key="aimir.button.excel"/></span>
				</a>
			</li>
		</ul>

        <!-- extjs 그리드 차트 -->
		<div class="gadget_body2">
            <div id="topLeftModemSearchChartGridDiv"></div>
		</div>
    </div>

    <div class="bodyright_meterchart">
    <ul><li class="bodyright_meterchart_leftmargin">


            <!-- 검색결과 정렬방식 (S) -->
            <!-- todo: "전체" or 아래 내용을 감싸는 div필요 -->
            <!-- :: <fmt:message key="aimir.all"/> -->

            <div id="search-default" >
                <ul class="search-modem">
                     <!--  todo : 필터링 영역은 오른쪽으로 이동 -->
                    <li class="gray11pt withinput"><fmt:message key="aimir.filtering"/> </li>
                    <li>
                        <!-- todo: 검색종류 추가  / 조회조건 -->
                        <select id="sCommState" style="width:170px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                        <option value="0"><fmt:message key="aimir.commstateGreen"/></option>
                        <option value="1"><fmt:message key="aimir.commstateYellow"/></option>
                        <option value="2"><fmt:message key="aimir.commstateRed"/></option>
                        </select>
                    </li>
                    <li class="space20"></li>
                     <li>
                        <!-- todo: 검색종류 추가 / 검색1 -->
                        <select id="sOrder" name="select" style="width:220px;">
                            <option value="1"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.desc"/></option>
                            <option value="2"><fmt:message key="aimir.mcu.lastcomm"/> <fmt:message key="aimir.search.asc"/></option>
                            <option value="3"><fmt:message key="aimir.installationdate"/>  <fmt:message key="aimir.search.desc"/></option>
                            <option value="4"><fmt:message key="aimir.installationdate"/>  <fmt:message key="aimir.search.asc"/></option>
                        </select>
                    </li>
                    <li class="space20"></li>

                    <span class="padding-r5px"  style="float: right">
                    <li>
                        <a href="#" onClick="openExcelReport();" class="btn_blue" >
                            <span><fmt:message key="aimir.button.excel"/></span>
                        </a>
                    </li>
                    </span>

                    <li class="gray10pt" id="modemSearchGridHeader"></li>
                </ul>
            </div>
            <!-- 검색결과 정렬방식 (E) -->

             <!-- extjs Grid -->
             <div class="gadget_body">
                <div id="modemSearchGridDiv" >

                </div>
            </div>

    </li></ul>
    </div>

</div>




<!-- Tab 1,2,3 (S) -->
<div id="modemDetailTab">
    <ul>
        <li><a href="#general" id="_general"><fmt:message key="aimir.general"/></a></li>
        <li><a href="#locationInfo"  id="_locationInfo"><fmt:message key="aimir.location.info"/></a></li>
        <li><a href="#schedule" id="_schedule"><fmt:message key="aimir.schedule"/></a></li>
        <%-- <li><a href="#history" id="_history"><fmt:message key="aimir.history"/></a></li> --%>
        <%-- <li><a href="#commenv" id="_commenv"><fmt:message key="aimir.setting.info"/></a></li> --%>
        <li><a href="#asyncHistory" id="_asyncHistory">Async Command History</a></li>
    </ul>


    <!-- Tab 1 : modemDetailTab (S) -->
    <div id="general" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:600px;">
        <ul class="width" style="overflow:hidden; padding-bottom: 0px;"">
        <li class="padding">
            <!-- 미터 목록 (S) -->
            <div class="bodyright_modeminfo">
                <div class="headspace"><label class="check"><fmt:message key="aimir.meter"/></label></div>

                <div class="box-bluegradation-meter" style = "height:100%">
                <ul><li class="box-bluegradation-meter-padding">

                    <div class="flexlist">
                        <!--modemAddMeterDiv  -->
                        <div id="modemAddMeterGridDiv">
                        </div>

                        <!-- <div id="meterListByNotModemGridDiv"></div>-->

                    </div>
                </li></ul>
                </div>

                <!-- <div id="btn-right" class="meter-info-btn3"></div>-->
				<div id="btn-right" class="meter-info-btn2" style="margin-bottom: 4px;">
					<a href='#;' onclick='viewConnectedMeterPopup();' class='btn_blue'><span><fmt:message key="aimir.update"/></span></a>
                </div>
            </div>
            <!-- 미터 목록 (E) -->

            <div id='drAlertFWOptionPop'></div>

            <!-- 기본정보 (S) -->
            <div class="bodyleft_modeminfo">
                <ul class="bodyleft_modeminfo_rightmargin">
                    <li class="bodyleft_modeminfo_width100">

                        <div class="headspace"><label class="check"><fmt:message key="aimir.button.basicinfo"/></label></div>
                        <div class="box-bluegradation-modem">
                            <ul><li id="modemInfoDiv" class="box-bluegradation-modem-padding"></li></ul>
                        </div>

                        <div id="modemInfoBtnList">
                            <div id="btn-right" class="meter-info-btn2" style="margin-bottom: 4px;">
                                <!-- 버튼이 화면에 보이는 역순으로 출력할 것 -->
                                <!-- 등록 첫화면 -->
                                <div id="modemInitButton" style="display:block;">
                                    <ul><li><a href="javaScript:insertModem();" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
                                </div>
                                <!-- 등록 -->
                                <div id="modemInsertButton" style="display:none;">
                                    <ul><li><a href="javaScript:changeModemInfoDiv('cancel');" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
                                    <ul><li><a href="javaScript:insertModem();" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
                                </div>

                                <!-- 등록 / 변경 / 삭제 -->
                                <div id="modemViewButton" style="display:none">
                                    <ul><li><a href="javaScript:deleteModemInfo();" class="on"><fmt:message key="aimir.button.delete"/></a></li></ul>
                                    <ul><li><a href="javaScript:changeModemInfoDiv('edit');" class="on"><fmt:message key="aimir.update"/></a></li></ul>
                                    <ul><li><a href="javaScript:changeModemInfoDiv('insert');" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
                                </div>

                                <!-- 변경 / 취소 -->
                                <div id="modemEditButton" style="display:none">
                                    <ul><li><a href="javaScript:changeModemInfoDiv('view');" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
                                    <ul><li><a href="javaScript:updateModem();" class="on"><fmt:message key="aimir.update"/></a></li></ul>
                                </div>
                            </div>
                        </div>

                    </li>
                </ul>
            </div>
            <!-- 기본정보 (E) -->
        </li>
        </ul>
        
        <ul class="width" style="padding-top: 0px;">
        <li class="padding-bottom">
            <div id="modemCommand">
            <!-- 3rd : 명령 (S) -->
            <!-- <br/><br/><br/> -->
            <div class="headspace floatleft" style="margin-bottom: 0px;"><label class="check" style="margin-top: 5px;"><fmt:message key="aimir.instrumentation"/></label></div>
            <div class="headspace floatleft margin-l5" id="command" style="margin-top: 10px;">
                    
            	<div id = 'pingDiv'  class="floatleft margin-r5" style="display: none;">
            	    <em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
            	</div>
            	<div id = 'traceRouteDiv'  class="floatleft margin-r5" style="display: none;">
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
            	</div>
            	<div id = 'otaDiv'  class="floatleft margin-r5" style="display: none;">
                    <em class="btn_bluegreen"><a href="javascript:runOta()"><fmt:message key="aimir.ota"/></a></em>
            	</div>

                <div id='divFwUploadSMS' style="display: none;">
                    <!-- <em class="btn_bluegreen" id="lpLog"><a href="javascript:commandGetLPLog();">LP LOG</a></em> -->
                    <!--<em class="btn_bluegreen" id="eventHistoryLog"><a href="javascript:commandEventLog()"><fmt:message key="aimir.eventhistorylog"/></a></em>-->
                    <%-- <em class="btn_bluegreen" id="unitScanning"><a href="javascript:commandUnitScanning()" ><fmt:message key="aimir.unit.scanning"/></a></em>
                    <em class="btn_bluegreen" id="monitoring"><a href="javascript:commandMonitoring()"><fmt:message key="aimir.monitoring"/></a></em> --%>
                    <!-- <em class="btn_bluegreen"><a href="javascript:commandTOUCalendar();">TOUCalendar</a></em> -->
                    <!-- Firmware update SMS-->
                    <!-- <em class="btn_bluegreen" id="FwUploadSMS"><a id="fwUploadSMS">Firmware Update</a></em>
                   	<em class="btn_bluegreen" id="cmdLine"><a href="javascript:cmdLine();">CMD LINE</a></em> -->
                    <%-- <em class="btn_bluegreen"><a href="javascript:getModemEventLog()"><fmt:message key="aimir.getLog"/></a></em>
                    <em id="getlogBtn" class="btn_bluegreen"><a href="javascript:openGetLogExcelReport();"><fmt:message key="aimir.getLogFile"/></a></em>
               	    <em class="btn_bluegreen"><a href="javascript:commandCOAPPing();"><fmt:message key="aimir.coapPing"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getInfoToggle();"><fmt:message key="aimir.getInfo"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('resetModem');"><fmt:message key="aimir.resetModem"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:coapBrowser()"><fmt:message key="aimir.coapBrowser"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('setBaudrate')"><fmt:message key="aimir.setBaudRate"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:successFail()"><fmt:message key="aimir.snmpEnableDisable"/></a></em>
					<em class="btn_bluegreen"><a href="javascript:getAPN()"><fmt:message key="aimir.getAPN"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getSetParameterCaptcha()"><fmt:message key="aimir.getSetParameter"/></a></em> --%>
                    <em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:runOta()"><fmt:message key="aimir.ota"/></a></em>
                </div>
                
                <div id='divModemCommandSMS_MMIU' style="display: none;">
                	<!-- <em class="btn_bluegreen" id="lpLog"><a href="javascript:commandGetLPLog();">LP LOG</a></em> -->
                    <%-- <em class="btn_bluegreen" id="eventHistoryLog"><a href="javascript:commandEventLog()"><fmt:message key="aimir.eventhistorylog"/></a></em>
                    <em class="btn_bluegreen" id="unitScanning"><a href="javascript:commandUnitScanning()" ><fmt:message key="aimir.unit.scanning"/></a></em>
                    <em class="btn_bluegreen" id="monitoring"><a href="javascript:commandMonitoring()"><fmt:message key="aimir.monitoring"/></a></em> --%>
                    <%-- <em class="btn_bluegreen"><a href="javascript:getModemEventLog()"><fmt:message key="aimir.getLog"/></a></em>
                    <em id="getlogBtn" class="btn_bluegreen"><a href="javascript:openGetLogExcelReport();"><fmt:message key="aimir.getLogFile"/></a></em>
               	    <em class="btn_bluegreen"><a href="javascript:commandCOAPPing();"><fmt:message key="aimir.coapPing"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getInfoToggle();"><fmt:message key="aimir.getInfo"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('resetModem');"><fmt:message key="aimir.resetModem"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:coapBrowser()"><fmt:message key="aimir.coapBrowser"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('setBaudrate')"><fmt:message key="aimir.setBaudRate"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:snmpDaemon()"><fmt:message key="aimir.mbb.snmpdaemon"/></a></em>
					<em class="btn_bluegreen"><a href="javascript:getAPN()"><fmt:message key="aimir.getAPN"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getSetParameterCaptcha()"><fmt:message key="aimir.getSetParameter"/></a></em>
					<em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
					<em class="btn_bluegreen"><a href="javascript:setInitModem()">Set Init Modem</a></em>
                	<em class="btn_bluegreen"><a href="javascript:restartCommModem()">Restart Comm. Modem</a></em>
                    <em class="btn_bluegreen"><a href="javascript:restartCommStack()">Restart Comm. Stack</a></em>
                    <em class="btn_bluegreen"><a href="javascript:setAPN()">Set APN</a></em>
                    <em class="btn_bluegreen"><a href="javascript:doWatchdog()">Watchdog</a></em>
                    <em class="btn_bluegreen"><a href="javascript:execNICommand()"><fmt:message key="aimir.execNICommand"/></a></em> --%>
                    
                    <em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:runOta()"><fmt:message key="aimir.ota"/></a></em>
                </div>
                
                <div id='divFwUploadOthers' style="display: none;">
                    <em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:runOta()"><fmt:message key="aimir.ota"/></a></em>
                	<em class="btn_bluegreen" id="FwUploadSMS" ><a id="fwUploadSMS">Firmware Update</a></em>
                   	<!-- <em class="btn_bluegreen" id="cmdLine" ><a href="javascript:cmdLine();">CMD LINE</a></em> -->
                    <%-- <em class="btn_bluegreen" id="lpLog"><a href="javascript:commandGetLPLog()">LP LOG</a></em>
                    <em class="btn_bluegreen" id="eventHistoryLog"><a href="javascript:commandEventLog()"><fmt:message key="aimir.eventhistorylog"/></a></em>
                    <em class="btn_bluegreen" id="unitScanning"><a href="javascript:commandUnitScanning()" ><fmt:message key="aimir.unit.scanning"/></a></em>
                    <em class="btn_bluegreen" id="monitoring"><a href="javascript:commandMonitoring();"><fmt:message key="aimir.monitoring"/></a></em> --%>
                     <!-- <em class="btn_bluegreen"><a href="javascript:commandTOUCalendar();">TOUCalendar</a></em> -->
                     <!-- Firmware update Others-->
                    <!-- <em class="btn_bluegreen" id="FwUploadOthers"><a id="fwUploadOthers" href="javascript:fwOption()">Firmware Update</a></em> -->
                </div>
                <div id='divFwUploadPLC' style="display: none;">
                   <%--  <em class="btn_bluegreen" id="unitScanning"><a href="javascript:commandUnitScanning()" ><fmt:message key="aimir.unit.scanning"/></a></em>
                    <em class="btn_bluegreen" id="FwUploadOthers"><a id="fwUploadOthers" href="javascript:fwOption()">Firmware Update</a></em> --%>
                </div>
                
                <div id='divModemCommand' style="display: none;">
    	            <em class="btn_bluegreen" id="cmdPing"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
                	<em class="btn_bluegreen" id="cmdTraceroute"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:runOta()"><fmt:message key="aimir.ota"/></a></em>
                    <%-- <em class="btn_bluegreen"><a href="javascript:getModemEventLog()"><fmt:message key="aimir.getLog"/></a></em>
                    <em id="getlogBtn" class="btn_bluegreen"><a href="javascript:openGetLogExcelReport();"><fmt:message key="aimir.getLogFile"/></a></em>
               	    <em class="btn_bluegreen"><a href="javascript:commandCOAPPing()"><fmt:message key="aimir.coapPing"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getInfoToggle()"><fmt:message key="aimir.getInfo"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('resetModem');"><fmt:message key="aimir.resetModem"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:coapBrowser()"><fmt:message key="aimir.coapBrowser"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:CaptchaPanel('setBaudrate')"><fmt:message key="aimir.setBaudRate"/></a></em>
                    <em class="btn_bluegreen"><a href="javascript:getSetParameterCaptcha()"><fmt:message key="aimir.getSetParameter"/></a></em> for test
                    <em class="btn_bluegreen"><a href="javascript:execNICommand()"><fmt:message key="aimir.execNICommand"/></a></em> --%>
                    <%-- <em class="btn_bluegreen"><a href="javascript:successFail()"><fmt:message key="aimir.watchdogReset"/></a></em> --%>
                    <%--<em class="btn_bluegreen"><a href="javascript:successFail()"><fmt:message key="aimir.snmpEnableDisable"/></a></em>--%>

                </div>
            </div>
            <br/>
            <div class="meterinfo-textarea" style="height: 200px;margin-top: 30px;">
               <ul><li><textarea id="commandResult" name="commandResult" style="height: 150px;" readonly="readonly"><fmt:message key="aimir.operation.result" /></textarea></li></ul>
            </div>
            <!-- 3rd : 명령 (E) -->
            </div>

        </li>
        </ul>
        </div>
        <!-- search-default (E) -->


    </li></ul>
    </div>
    <!-- Tab 1 : modemDetailTab (E) -->

    <!-- Tab 2 : locationInfo (S) -->
    <div id="locationInfo" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:400px;">
        <ul class="width">
        <li class="padding">
          <div class="map_box">
          <div id="map-canvas" class="border-blue-3px"></div>
          </div>
            <div class="coordinate_box margin-t10px">
                <div class="width_auto padding20px"  style="display:table-cell;">
                <form id="modemLocForm">

                  <table width="100%" class="searching2">
                    <caption><label class="check"><fmt:message key="aimir.location.info"/></label></caption>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.latitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioX" value="${gpioX}" class="width-140px"/></td>
                        <td><fmt:message key="aimir.address"/></td>
                      </tr>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.logitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioY" value="${gpioY}" class="width-140px"/></td>
                        <td rowspan="2"  valign="bottom"><textarea name="customer_num3" id="sysLocation" style="width:260px; height:50px;">${sysLocation}</textarea></td>
                      </tr>
                      <tr>
                        <td class="padding-r20px"><fmt:message key="aimir.altitude"/></td>
                        <td class="padding-r20px" align="right"><input type="text" id="gpioZ" value="${gpioZ}" class="width-140px"/></td>
                      </tr>
                      <tr id="modemLocBtnList">
                        <td colspan="2" align="right" class="padding-r25px"><em class="am_button"><a href="javascript:updateModemLoc()" class="on"><fmt:message key="aimir.mcu.coordinate.update"/></a></em></td>
                        <td align="right">
                        <em class="am_button"><a href="javascript:updateModemAddress()" class="on"><fmt:message key="aimir.mcu.adress.update"/></a></em>
                        <em class="am_button"><a href="javascript:getGeoCoding();" class="on"><fmt:message key="aimir.address"/>-<fmt:message key="aimir.mcu.coordinate"/></a></em>
                        </td>
                      </tr>
                     </table>

                 </form>
                </div>

             </div>


        </li></ul>
        </div>
        <!-- search-default (E) -->
    </li></ul>
    </div>
    <!-- Tab 2 : locationInfo (E) -->

    <!-- Tab 3 : schedule (S) -->
    <div id="schedule" class="tabcontentsbox">
    <!-- Sub page will be loaded (device/modemMaxGadgetScheduleTab.jsp) -->
    </div>
    <!-- Tab 3 : schedule (E) -->

    <!-- Tab 4 : Communication Environment (S) -->
    <%-- <div id="commenv" class="tabcontentsbox">
    <ul><li>
        <!-- search-default (S) -->
        <div class="blueline" style="height:400px;">
        <ul class="width">
        <li class="padding">
            <div class="bodyright_comm">
                <ul><li class="bodyright_comm_leftmargin">

                        <!-- 통신설정 정보  -->
                        <div class="headspace"><label class="check"><fmt:message key="aimir.setting.info"/></label></div>

                        <div class="box-bluegradation-meter" style="height:304px;">
                        <ul><li class="box-bluegradation-meter-padding">
                            <form id="commenvForm">
                              <table class="commenvinfo">
                                <colgroup>
                                <col width="20%"/>
                                <col width="65%"/>
                                <col width=""/>
                                </colgroup>

                                      <tr>
                                        <td class="padding-r10px"><label class="check">phone number</label></td>
                                        <td class="padding-r20px"><span><input type="text" id="phoneNumber" name="phoneNumber"/></span></td>
                                        <td><em id="settingPhoneBtn" class="btn_org"><a href="javascript:commandModem('CM16');">*Setting</a></em></td>
                                      </tr>

                                      <tr>
                                        <td class="padding-r10px"><label class="check">Comm Speed</label></td>
                                        <td class="padding-r20px hack_select">
                                            <select id="commspeed" name="commspeed" class="width-140px">
                                                <option value="1200">1200</option>
                                                <option value="2400">2400</option>
                                                <option value="4800">4800</option>
                                                <option value="9600">9600</option>
                                                <option value="19200">19200</option>
                                                <option value="38400">38400</option>
                                                <option value="115200">115200</option>
                                            </select>
                                        </td>
                                        <td><em id="settingCommBtn" class="btn_org"><a href="javascript:commandModem('CM05');">*Setting</a></em></td>
                                      </tr>

                                      <tr>
                                        <td class="padding-r10px"><label class="check">GPRS AT Command</label></td>
                                        <td class="padding-r20px hack_select">
                                            <select id="atcommand" name="atcommand" style="width:160px">
                                                <option value="0">ATD*99***1#</option>
                                                <option value="1">ATD*99#</option>
                                            </select>
                                        </td>
                                        <td><em id="settingGprsBtn" class="btn_org"><a href="javascript:commandModem('CM18');">*Setting</a></em></td>
                                      </tr>

                                      <tr>
                                        <td class="padding-r10px"><label class="check">Sending Flag</label></td>
                                        <td class="padding-r20px hack_select">
                                            <span>
                                                <select id="sendingFlag1" name="sendingFlag1"  style="width:220px">
                                                <option value="0">Immediately transfer the event data</option>
                                                <option value="1">Event data transfer when regular metering</option>
                                                </select>
                                            </span>
                                            <span>
                                                <select id="sendingFlag2" name="sendingFlag2"  style="width:220px">
                                                <option value="1">SIM number modem key used to</option>
                                                <option value="0">Phone number modem key used to</option>
                                                </select>
                                            </span>
                                        </td>
                                        <td><em id="settingSendBtn" class="btn_org"><a href="javascript:commandModem('CM17');">*Setting</a></em></td>
                                      </tr>

                                      <tr>
                                        <td class="padding-r10px"><label class="check">Meter Vendor & Model</label></td>
                                        <td class="padding-r20px hack_select">
                                            <span>
                                                <select id="vendor" name="sendingFlag1" style="width:220px">
                                                <option value="01">Kamstrup</option>
                                                </select>
                                                </span>
                                            <span>
                                                <select id="model" name="sendingFlag2" style="width:220px">
                                                <option value="02">Kamstrup382</option>
                                                <option value="03">Kamstrup351</option>
                                                </select>
                                            </span>
                                        </td>
                                        <td><em id="settingVendorBtn" class="btn_org"><a href="javascript:commandModem('CM10');">*Setting</a></em></td>
                                      </tr>
                                      <tr>
                                        <td class="padding-r10px"><label class="check">SNTP Server IP</label></td>
                                        <td class="padding-r20px">
                                            <span><input size="3" type="text" id="sntp1" name="sntp1"/></span>
                                            <span><input size="3" type="text" id="sntp2" name="sntp2"/></span>
                                            <span><input size="3" type="text" id="sntp3" name="sntp3"/></span>
                                            <span><input size="3" type="text" id="sntp4" name="sntp4"/></span>
                                        </td>
                                        <td><em id="settingSntpBtn" class="btn_org"><a href="javascript:commandModem('CM12');">*Setting</a></em></td>
                                      </tr>
                                      <tr>
                                        <td class="padding-r10px"><label class="check">Upload Server IP & Port</label></td>
                                        <td class="padding-r20px">
                                            <span><input size="3" type="text" id="serverip1" name="serverip1"/></span>
                                            <span><input size="3" type="text" id="serverip1" name="serverip1"/></span>
                                            <span><input size="3" type="text" id="serverip1" name="serverip1"/></span>
                                            <span><input size="3" type="text" id="serverip1" name="serverip1"/></span>
                                            <span><input size="5" type="text" id="serverPort" name="serverPort"/></span>
                                        </td>
                                        <td><em id="settingUploadBtn" class="btn_org"><a href="javascript:commandModem('CM06');">*Setting</a></em></td>
                                      </tr>
                                      <tr>
                                        <td class="padding-r10px"><label class="check">APN ID & Password</label></td>
                                        <td class="padding-r20px">
                                            <span><input type="text" id="apnName" name="serverIp"/></span>
                                            <span><input type="text" id="apnId" name="serverPort"/></span>
                                            <span><input type="text" id="apnPwd" name="serverPort"/></span>
                                        </td>
                                        <td><em id="settingApnBtn" class="btn_org"><a href="javascript:commandModem('CM09');">*Setting</a></em></td>
                                      </tr>
                                      <tr>
                                        <td class="padding-r10px"><label class="check">SMS Service Center Address</label></td>
                                        <td class="padding-r20px"><input type="text" id="apnName" name="serverIp"/></td>
                                        <td><em id="settingSmsBtn" class="btn_org"><a href="javascript:commandModem('CM11');">*Setting</a></em></td>
                                      </tr>

                                    </table>
                                </form>

                            </li></ul>
                        </div>
                        <!-- 통신설정 정보  -->

                </li></ul>
            </div>

        </li></ul>
        </div>
        <!-- search-default (E) -->
    </li></ul>
    </div> --%>
    <!-- Tab 4 : Communication Environment (E) -->
    <!-- Tab 5 : Async Command History (S) -->
    <div id="asyncHistory" class="tabcontentsbox">
    	<ul><li>
        <div class="blueline" style="height:400px;">
        <!-- Search Date (S) -->
        <div class="width-100" style="padding-top: 20px; padding-left: 20px; padding-right: 20px; padding-bottom: 20px;">
			<label class="check">Async Comm. Log</label>
			<div class="width-auto margin-t10px">
				<span class="margin-t5px margin-r5">Search Date</span>
				<span><input id="asyncCommandHistoryStartDate" class="day" type="text"></span>
				<span><input value="~" class="between" type="text"></span>
				<span class="margin-r5"><input id="asyncCommandHistoryEndDate" class="day" type="text"></span>
				<span class="margin-t2px am_button"><a href="javascript:getAsyncHistoryGrid()"><fmt:message key="aimir.button.search" /></a></span>
				<input type="hidden" id="asyncCommandHistoryStartDateHidden" />
				<input type="hidden" id="asyncCommandHistoryEndDateHidden" />
			</div>
		</div>
		</br></br>
        <!-- Search Date (E) -->
        <!-- Command History (S) -->
        <div id="padding-10" style="padding-left: 10px; padding-right: 10px;">
			<div id="asyncHistoryGrid" class="tabcontentsbox"></div>
		</div>
		<!-- Command History (E) -->
		</div>
		</li></ul>
	</div>
    <!-- Tab 5 : Async Command History (E) -->
</div>
<!-- Connected Meter Dialog -->
<div id="detail_dialog" class="mvm-popwin-iframe-outer" style="width:1200px important!;" title="<fmt:message key='aimir.mcu.device.connected'/>">
    <iframe id="detail_view" name="detail_view" src="" frameborder="0"  class="mvm-popwin-iframe" marginwidth="0" marginheight="0" scrolling="no"></iframe>
</div>
<form id='detailForm' name='detailForm' method='post' action='' target='detail_view'>
	<input type='hidden' name='modemId' value=''>
	<input type='hidden' name='supplierId' value=''>
</form>
</body>
</html>