<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", -1); //prevents caching at the proxy
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>제조사 모델관리</title>
    
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/css/jquery.tabs.css" rel="stylesheet" type="text/css"    media="print, projection, screen"></link>
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .box-bluegradation-channel-left .x-grid3-scroller {
            width: 268px;
            height: 138px;
        }
        .selectbox-wrapper li{
        	height:auto !important;
        }
        .x-toolbar-cell{
            vertical align: left !important;
        }
        
        #meterSetting li {
		    float:left;
		    display:inline;
		    height:27px;
		    width:100% !important;
		    float:left;
		    display:block;
		}
        
    </style>

    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/map2.js"></script>
    <script type="text/javascript" src="${ctx}/js/gadget/dlmsScreen/dlmsDeviceModelSub.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>

    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.checkbox.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.contextmenu.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.themeroller.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_flat.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_nested.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/autocomplete/jquery.autocomplete.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>

    <script type="text/javascript">/*<![CDATA[*/

        // 수정권한
        var editAuth = "${editAuth}";
        var cmdAuth = "${cmdAuth}";
        var roleName = "${roleName}";
        var roleId = "${roleId}";
        var supplierId = ${supplierId};
        var channelCount = 0;
        var auto;
        var deviceTreeId;
        var DeviceModelSelectId;
        var MeterConfigId;
        
        var obisListAuth;
        
        var obisCodeGrid;
        
        var addGrid;
        var addObisStore;
        var addObisModel;
        var selectedObisRecordInfo;
        //MeterParamSet/Get 싱행시 Detail 버튼을 눌렀는지 아닌지를 체크
        var detailCheck=false;
        
        var addDailyProfileStore;
        var addDailyProfileModel;
        var addDAilyProfileGrid;
        
        var saveDetailParamArrList = new Array();
        var selectAttribute;
        
        var parameterGrid;
        
        var editSetting = false;

        var lastChannelIndex = 0;
        // Meter Program
        var curMeterProgramSettings = "";
        var curMeterProgramKind = "";
        //선택된 channel arrayList
/*        var selectedRows = new Array();
        var selectedRows2 = new Array();*/
        
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
        });

        var channels = new Array();  
        var channels2 = new Array();
        var params = {      
            DCU: "",   
            Meter: "",
            Modem : "",
            Unknown : ""  
        };
        // meterProgramTemplate
        var meterProgramTemplate = new Array();

        $(document).ready(function() {

        	getCommands();

            // 권한 체크
            if (editAuth == "true") {
                $("#addBtn").show();
                $("#attachFile").show();
                $("#btnList1").show();
                $("#btnList2").show();
                $("#btn-putinout").show();
                $("#channelListNotMeter").show();
                $("#meterProgramBtnList").show();
                $('#addObisRowATag').show();
                editSetting = true;
            } else {
                $("#addBtn").hide();
                $("#attachFile").hide();
                $("#btnList1").hide();
                $("#btnList2").hide();
                $("#btn-putinout").hide();
                $("#channelListNotMeter").hide();
                $("#meterProgramBtnList").hide();
                $('#addObisRowATag').hide();
                $("#meterProgramBtnList").hide();
                editSetting = false;
            }
        });

        $(function(){

            init();

            $('a').click(function(event) {
                event.preventDefault();
            });

            // MeterProgramKind Combo
            $.getJSON('${ctx}/gadget/system/getMeterProgramKindComboData.do',
                    function(json) {
                        if (json.result != null) {
                            $('#meterProgramKind').noneSelect(json.result);

							$.each(json.result,function(i, object){
								meterProgramTemplate[i]=object.template;
							});
							
                            $('#meterProgramKind').change(function(){
                                var index =  $('#meterProgramKind').attr('selectedIndex');
                                var template = meterProgramTemplate[index-1];

                                if(template==undefined)
                                	template='';
								$('#settings').val(template);
                            });
                            $('#meterProgramKind').selectbox();
                        }
                    }
            );
            
            $('#meterProtocol').selectbox();         

            new AjaxUpload('attachFile', {
                action: '${ctx}/common/deviceImgUpload.do',
                data : {
                    // Additional data to send
                    //'key1' : "This data won't",
                },
                responseType : false,
                onSubmit : function(file , ext){
                    // Allow only images. You should add security check on the server-side.
                    if (ext && /^(jpg|png|jpeg|gif)$/.test(ext)){
                        /* Setting data */
                        this.setData({
                            'key': 'This string will be send with the file'
                        });
                       // $('#upload .text').text('Uploading ' + file);
                        //image path 에 '/n' 내용 삭제.
                        $('#upload .text').text('Uploading ' + file.trim());
                    } else {
                        // extension is not allowed
                        $('#upload .text').text('Error: only images are allowed');
                        // cancel upload
                        return false;
                    }
                },
                onComplete : function(file, response){
                	 //image path 에 '/n' 내용 삭제.
                    $('#upload .text').text(file.trim());
                    $(":input[name='image']").val(response);
                    $('#photo img').attr('src', '${ctx}/'+response);
                    updateModelForm();
                }
            });

            //공급사 목록 클릭시 제조사/모델 정보 트리 보여주기
            $('#container-1 a').click( function () {
                $(":input:radio:eq('M')").attr('checked', 'checked');// 공급사 선택시 '장비별' 조회
            });

            function supplyCheck(){
                if ($('#supplierList').val()=='0') {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.buildingMgmt.supplierFirst"/>');
                    return false;
                }
                return true;
            }

            function treeTypeView() {
                 var treeType = $(":input:radio[name='treeType']:checked").val();

                 if (treeType =='M') {
                     viewTree();
                 } else {
                     viewDeviceTree();
                 }
            }

            function treeTypeCheck() {

                var treeType = $(":input:radio[name='treeType']:checked").val();

                if (treeType == 'M') {
                    return false;
                }

                return true;
            }

            $(":input:radio").change(function(){
                if (!supplyCheck()) return;

                treeTypeView();

            });

            $.getJSON('${ctx}/gadget/system/getSupplierList.do',
                    function(json) {

                        if (json.supplier != null) {
                            supplierId = json.supplier.id;
                            $('#supplierName').text(json.supplier.name);
                            $('#supplierList').hide();
                            viewTree();
                            return;
                        }

                        if (json.supplierList != null) {
                           $('#supplierList').loadSelect(json.supplierList);
                        }
                    }
            );

            $('#supplierList').change( function () {
                supplierId = $(this).val();
                $(":input[name='supplierId']").val(supplierId);
                treeTypeView();

            });

            $('#search').click( function () {
                if (!supplyCheck()) return;
                var searchWord = $(":input[name='searchWord']").val();
                $.tree.focused().search(searchWord);

            });

            $('#deviceType').change( function () {
                var deviceType = $('#deviceType :selected').text();
            });

            // vendor add button eventHandler
            $('#viewVendorForm').click( function () {
                if (!supplyCheck()) return;
                resetVendorForm();
                $('#container-3').show('fast', vendorTabListener());
                $('#container-4').hide();
                //$('#container-5').hide();
            });

            // model add button eventHandler
            $('#viewModelForm').click( function () {
                if (!supplyCheck()) return;
                resetModelForm();

                $('#container-3').hide();
                $('#container-4').show('fast', modelTabListener());

                $("#deviceVendor_input").val($("#deviceVendor option:selected").text())
                $("#mainDeviceType_input").val($("#mainDeviceType option:selected").text())
                $("#mainDeviceType_input").show();

                getReferenceVendors($('#vendorForm :hidden[name="id"]').val());
            });

            // channel add
            $('#modelForm a#addChannel').click( function() {
                //alert("addChannel");
                var count = channelCount+1;
                //alert("channelCount:" + channelCount + ", count:" + count);
                if (count > 8) return;
                channelView(count);
            });

            // channel delete
            $('#modelForm a#deleteChannel').click( function() {
                var count = channelCount-1;
                if (count < 0) return;
                $('#channelType-'+count+' option:first').attr("selected", "selected");
                channelHide(count);
            });

            // vendor add/update/delete
            $('#vendorForm a#add1').click( function() {
                if (!supplyCheck()) return;
                $("#vendorForm :hidden[name='supplierId']").val(supplierId);
                var options = {
                    success : vendorAddResult,
                    url : '${ctx}/gadget/system/devicevendoradd.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#vendorForm').ajaxSubmit(options);

            });

            $('#vendorForm a#update1').click( function() {
                $("#vendorForm :hidden[name='supplierId']").val(supplierId);
                var devicevendorId = $("#vendorForm :hidden[name='id']").val();  //#vendorForm --> this
                if (devicevendorId) {
                    var options = {
                        success : vendorUpdateResult,
                        url : '${ctx}/gadget/system/devicevendoredit.do',
                        type : 'post',
                        datatype : 'json'
                    };
                    $('#vendorForm').ajaxSubmit(options);
                }
            });

            $('#vendorForm a#delete1').click( function() {
                var devicevendorId = $("#vendorForm :hidden[name='id']").val();

                if (devicevendorId) {
                    var options = {
                        success : vendorDeleteResult,
                        url : '${ctx}/gadget/system/devicevendordelete.do?devicevendorId='+devicevendorId,  //추가/수정에 따른 url변경
                        datatype : 'json'
                    };
                    $('#vendorForm').ajaxSubmit(options);
                }
            });

            // modelForm add/update/delete
            $('#modelForm a#add2').click( function() {
                $("#modelForm :hidden[name='supplierId']").val(supplierId);
                $("#modelForm :hidden[name='code']").val(0);
                var conName = $("#modelForm :input[name='modelName']").val();
                 $("#modelForm :hidden[name='configName']").val(conName);
                               //image path 에 '/n' 내용 삭제.
                var image = $("#modelForm :hidden[name='image']").val();
                $("#modelForm :hidden[name='image']").val(image.trim());

                setChannelList();
               // console.log("add channel : ",setchannellist);

                var options = {
                    data: {
                        displayidlist:setchannellist[1]
                        ,namelist:setchannellist[2]
                        ,indexlist:setchannellist[3]
                        ,typelist:setchannellist[4]
                        ,subType :$("#deviceType").val()
                         },
                    success : modelAddResult,
                    //url : '${ctx}/gadget/system/devicemodeladd.do',
                    url : '${ctx}/gadget/system/addDeviceModelConfig.do',
                    type : 'post',
                    datatype : 'application/json'
                };

                $('#modelForm').ajaxSubmit(options);
            });
            var setchannellist = new Array();
            function setChannelList() {
   
                setchannellist = [];
                //console.log("MeterAddChannelGridData:",MeterAddChannelGridData);
                setchannellist[0] = ""; //channelconifgId
                setchannellist[1] = ""; //displayId
                setchannellist[2] = "";
                setchannellist[3] = "";
                setchannellist[4] = "";

                for (var i = 0; i < MeterAddChannelGridData.length; i++) {
                    var id=MeterAddChannelGridData[i].id;
                    if (id == "" || id == null) {
                        id = 0;
                    }
                    setchannellist[0] += (id+"@");
                    setchannellist[1] += (MeterAddChannelGridData[i].displayid+"@");
                    setchannellist[2] += (MeterAddChannelGridData[i].name+"@");
                    setchannellist[3] += (MeterAddChannelGridData[i].channelIndex+"@");
                    setchannellist[4] += (MeterAddChannelGridData[i].displayType+"@");
                }
             
            }

            function updateModelForm() {
                var devicemodelId = $("#modelForm :hidden[name='modelId']").val();
                $("#modelForm :hidden[name='supplierId']").val(supplierId);
                $("#modelForm :hidden[name='code']").val(0);
                
                var conName = $("#modelForm :input[name='modelName']").val();
                 $("#modelForm :hidden[name='configName']").val(conName);
                 setChannelList();
                 //console.log("update channel : ",setchannellist);

                if (devicemodelId != null && devicemodelId != "") {
                    var options = {
                            data: {
                            idlist:setchannellist[0]
                            ,displayidlist:setchannellist[1]
                            ,namelist:setchannellist[2]
                            ,indexlist:setchannellist[3]
                            ,typelist:setchannellist[4]
                            ,subType :$("#deviceType").val()
                             },
                            success : modelUpdateResult,
                            url : '${ctx}/gadget/system/updateDeviceModelConfig.do',
                            type : 'post',
                            datatype : 'json'
                    };

                    $('#modelForm').ajaxSubmit(options);
                }
            }

            $('#modelForm a#update2').click( updateModelForm );

            $('#modelForm a#delete2').click( function() {
                var devicemodelId = $("#modelForm :hidden[name='modelId']").val();

                if (devicemodelId) {
                    var options = {
                            success : modelDeleteResult,
                            url : '${ctx}/gadget/system/deleteDeviceModelConfig.do',
                            datatype : 'json'
                        };

                    $('#modelForm').ajaxSubmit(options);
                }
            });

            $("#meterProgramTab").bind('click',function(event) {
            	viewApplyInput(false);
                isRetry = false;
                isApply = false;

            	// button control
            	if ($("#configId").val() == null || $("#configId").val() == "") {
            		$("#meterProgramBtn").hide();
            	} else {
            	    $("#meterProgramBtn").show();
            	    $("#meterProgramReg").show();
            	    $("#meterProgramApply").hide();
            	    $("#meterProgramRetry").hide();
            	    $("#regOk").hide();
            	    $("#regCancel").hide();
            	}
            	searchList();
            });

            // 등록 버튼 클릭 시
            $("#meterProgramReg").bind('click',function(event) {
            	curMeterProgramSettings = $("#settings").val();
            	curMeterProgramKind = $("#meterProgramKind").val();
                $("#settings").val("");
                $("#meterProgramKind option:first").attr("selected", "selected");
                $("#meterProgramReg").hide();
                $("#meterProgramApply").hide();
                $("#meterProgramRetry").hide();
                $("#regOk").show();
                $("#regCancel").show();
                // clear grid row selection
                meterProgramGrid.getSelectionModel().clearSelections();
            });

            // 적용 버튼 클릭 시
            $("#meterProgramApply").bind('click',function(event) {
            	// 팝업 떠서 cron 설정 입력
                viewApplyInput(true);
                $("#meterProgramReg").hide();
                $("#meterProgramApply").hide();
            });

            // 재시도 버튼 클릭 시
            $("#meterProgramRetry").bind('click',function(event) {
            	// 스케줄 등록
            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.button.retry'/>");
            });

            // 등록확인 버튼 클릭 시
            $("#regOk").bind('click',function(event) {

            	if ($("#settings").val().length <= 0) {
            		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
            		$("#settings").focus();
            		return;
            	}

            	var configId = $('#configId').val();

                $.post("${ctx}/gadget/system/saveMeterProgram.do",
                        {configId : configId,
                	     settings : $("#settings").val(),
                	     meterProgramKind : $("#meterProgramKind").val()},
                        function(json) {
                	    	 if (json != null && json.result == "success") {
                                 Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.save'/>");
                                 searchList();

                                 $("#meterProgramReg").show();
                                 $("#meterProgramApply").hide();
                                 $("#meterProgramRetry").hide();
                                 $("#regOk").hide();
                                 $("#regCancel").hide();
                	    	 } else {
                                 Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.save.error'/>");
                	    	 }
                        });
            });

            // 등록취소 버튼 클릭 시
            $("#regCancel").bind('click',function(event) {
            	$("#settings").val(curMeterProgramSettings);
            	$("#meterProgramKind").val(curMeterProgramKind);
                // button control
                $("#meterProgramReg").show();
                
                //alert("isRetry:"+isRetry+", isApply:"+isApply);
                if (isRetry) {
                    $("#meterProgramRetry").show();
                } else {
                    $("#meterProgramRetry").hide();
                }
                
                if (isApply) {
                    $("#meterProgramApply").show();
                } else {
                    $("#meterProgramApply").hide();
                }

                $("#regOk").hide();
                $("#regCancel").hide();
            });

            // 적용확인 버튼 클릭 시
            $("#applyOk").bind('click',function(event) {
                if ($("#cronSetting").val().length <= 0) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
                    $("#cronSetting").focus();
                    return;
                }

            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"apply");
            });

            // 적용취소 버튼 클릭 시
            $("#applyCancel").bind('click',function(event) {
                viewApplyInput(false);
                // button control
                $("#meterProgramReg").show();
                $("#meterProgramRetry").hide();
                $("#meterProgramApply").show();
                $("#regOk").hide();
                $("#regCancel").hide();
            });
            
            $("#roleTab").bind('click',function(event) {
            	$.ajaxSetup({
                    async : false
                });
            	
            	var temp = $('#command').height();
            	$('#commandsTree').height(temp-20);
            	
            	$.ajaxSetup({
                    async : true
                });

            });
            
            $("#obisCodeTab").bind('click',function(event) {
            	obisMgmtHandler.drawObisCodeGrid();
            });
            
            $("#meterSettingTab").bind('click',function(event) {
            	$.ajaxSetup({
                    async : false
                });
            	$('#meterGroupDiv').show();
    			$('#meterDiv').hide();
    			$('#meterSetId').val('');
    			
            	var meterGroupWidth = $('#meterGroup_container').width();
            	$('#meterGroup').width(meterGroupWidth);
           		$.post('${ctx}/gadget/system/getObisCodeGroup.do',{
       	            modelId : DeviceModelSelectId
                   }, function(json) {
                	    var obisCodeList = json.result;
						
	                       var arr = new Array();
	                       for (var i = 0; i < json.totalCnt; i++) {
	                           var obj = new Object();
	                           obj.name="obisCode["+obisCodeList[i].OBISCODE + "], classId["+obisCodeList[i].CLASSID+"]";
	                           obj.id= obisCodeList[i].OBISCODE+","+obisCodeList[i].CLASSID;
	                           obj.title = obisCodeList[i].ATTRIBUTENAME;
	                           arr[i]=obj
	                       };
	                   	$('#obisCmdList').noneSelect(arr);
                        $('#obisCmdList').selectbox();
                        
                        $('#parameterSetDiv').empty();
                 });

           		$("input:radio[name='selectMeter']:radio[value='1']").attr("checked", true);
           		
            	 $.post('${ctx}/gadget/system/getMeterGroupBygroupId.do', {
                        supplierId : supplierId,
                        groupType : 'Meter'
                    }, function(returnData) {
                        $('#meterGroup').noneSelect(returnData.NAME);
                        $('#meterGroup').selectbox();
                    });
            	 
            	 $.ajaxSetup({
                     async : true
                 });
            });

        });

        function init() {
            $('#container-3').tabs();

            $('#container-4').tabs();
            
            $('#container-3').show('fast', vendorTabListener());
            $('#container-4').hide();
            //$('#container-5').hide();

            $(":input:radio").filter("input[value='M']").attr("checked", "checked");
            /****** 2011. 10. 18 문동규 추가 소스 Start *********************/
            $("#mainDeviceType").bind('change',function(event) { 
				$("#mainDeviceType").selectbox();
                getSubDeviceTypeCombo($("#mainDeviceType option:selected").text()); 
                viewDeviceConfig($("#mainDeviceType option:selected").text());
            });

            getVendorCombo();
            getMainDeviceTypeCombo();
            getPhaseList();
            getMeterProtocol();

            initChannel();
        }
        
        function getCommands() {
            $('#commandsTree').tree({
                ui : {
                    theme_name : "checkbox"
                },
                plugins : {
                    checkbox : {
                    }
                }
            });
        }

        // vendor list init
        function getVendorCombo() {
             $.getJSON('${ctx}/gadget/system/vendorlist.do', {supplierId:supplierId},
                    function(data) {
                       $('#deviceVendor').loadSelect(data.deviceVendors);
                       $('#deviceVendor').selectbox();
                    }
            );
        }

        function initChannel() {
            $('#channelType').val('All');
            $('#channelType option:first').attr("selected", "selected");
        }

        // vendor list reference data
        function getReferenceVendors(vendor) {
            if (vendor) {
                $('#deviceVendor').val(vendor);
            }
        }

        // Main Device Type Combo
        function getMainDeviceTypeCombo() {
            $.getJSON('${ctx}/gadget/system/getMainDeviceTypeComboData.do', {supplierId:supplierId},
                   function(json) {
                       $('#mainDeviceType').loadSelect(json.result);
                       $("#mainDeviceType option:first").remove();
                       $("#mainDeviceType option:first").attr("selected", "selected");

                       $("#mainDeviceType").trigger("change");

                       var maintype = json.result;
                       //console.log(maintype);
                       for (var i = 0; i < maintype.length; i++) {
                           if (maintype[i].name == "DCU") {
                               params["DCU"] = maintype[i].code;
                           } else if (maintype[i].name == "Meter") {
                               params["Meter"] = maintype[i].code;
                           } else {
                               params["Modem"] = maintype[i].code;
                           }
                       }
                   }
            );
        }

        // Sub Device Type Combo
        function getSubDeviceTypeCombo(mainTypeName, subType) {
            //console.log(params);
            $.getJSON('${ctx}/gadget/system/getSubDeviceTypeComboData.do', 
                {mainTypeCode: params[mainTypeName]},
                   function(json) {
                    var result = json.result;
                    var arr = Array();
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].descr;
                        obj.id=result[i].id;
                        arr[i]=obj
                    };

                       $('#deviceType').loadSelect(arr);
                       $("#deviceType option:first").remove();
                       if (subType != null) {
                           $("#deviceType").val(subType);
                           $('#deviceType').selectbox();
                       } else {
                    	   $('#deviceType').selectbox();
                           $("#deviceType option:first").attr("selected", "selected");
                       }
                   }
            );
        }

        function viewDeviceConfig(mainTypeName) {

            if (mainTypeName == "Modem") {
                $("#deviceConfigDiv").show();          
                $("#meterConfigDiv").hide();
            } else if (mainTypeName == "Meter") {
                $("#deviceConfigDiv").show();              
                $("#meterConfigDiv").show();
            } else {
                $("#deviceConfigDiv").hide(); 
                $("#meterConfigDiv").hide();
            }
        }

    
        var PhaseTypeMap = {};
        function getPhaseList() {
            var code ='1.3.1.1.1';
            $.getJSON('${ctx}/gadget/system/codelist.do',{code:code},
                     function(data) {   
                       var pure = [];
                        $.each(data.meterChannel, function(index, element) {
                        var option = {};
                       
                         option = {
                           id: element.name,
                           name: element.descr
                         }; 

                        PhaseTypeMap[element.desct] = option;
                        pure.push(option);
                         
                        });
                     
                    $('#phase').pureSelect(pure);
                    $('#phase').selectbox();    
                    });

        }
        
        function getMeterProtocol() {
        	$.getJSON('${ctx}/gadget/system/getMeterProtocol.do',{},
                    function(json) {
        				var protocolArr = json.data;
        				var tempPush = [];
        				for(var i=0; i<protocolArr.length; i++) {
        					var temp = {
        							id : protocolArr[i],
        							name : protocolArr[i]
        					}
        					tempPush.push(temp);
        				}
        				
        				$('#meterProtocol').noneSelect(tempPush);
        				$('#meterProtocol').selectbox();
        				
                   });
        }

        function inputChannelCodeList(channels) {
            var ChannelTypeMap = {};
            var pure = [];
            if (channels != null) { 
                var len = channels.length;
                for (var i = 0 ; i < len ; i++) {

                    var option = {};

                    option = {
                        id: channels[i].displayType,
                        name: channels[i].displayType
                    };

                    ChannelTypeMap[channels[i].displayType] = option;
                    pure.push(option);
                }
            }
            $('#channelType').pureSelect(pure);
           // channelView(len);
        }

        // vendor callback
        function vendorAddResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
            }

            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;

                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                resetVendorForm();

                var vendor = responseText.vendorForm;
                $(":input[name='supplierId']").val(supplierId);
                var treeType = $(":input:radio[name='treeType']:checked").val();

                $(":input:radio").filter("input[value='M']").attr("checked", "checked");

                if (treeType == 'V') {
                    viewTree();
                } else {
                   $.tree.focused().create({data:{title:vendor.name, attributes:{href:'#'}}, attributes:{id:'vendor_'+vendor.id,'rel':'vendor'}}, -1);
                   $.tree.focused().select_branch('#vendor_'+vendor.id);
                }

            }
        }

        function vendorUpdateResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                var vendor = responseText.vendorForm;

                $.tree.focused().rename('#vendor_'+deviceTreeId, vendor.name);
            }
        }

        function vendorDeleteResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
            }

            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                //.replace(/^kfm2_node_/,'')
                resetVendorForm();
                vendorTabListener();

                // TODO tree UI 변경
                var devicevendorId = responseText.id;

                //$.tree.focused().remove('#vendor_'+devicevendorId);
                $.tree.focused().remove('#vendor_'+deviceTreeId);
            }
        }

        // model add callback
        function modelAddResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");

                var vendor = responseText.deviceVendor;
                var model = responseText.deviceModel;
                var treeType = $(":input:radio[name='treeType']:checked").val();

                if (treeType == 'M') {

                    $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id,'rel':'model'}}, '#vendor_'+model.deviceVendor, -1);
                    $.tree.focused().select_branch('#model_'+model.id);
                    $.tree.focused().refresh('#model_'+model.id);
                } else {
                    var select_node = $.tree.focused().get_node('#vendor_'+model.deviceVendor+'_'+model.deviceType);

                    if (!select_node.size()) {
                        $.tree.focused().create({data:{title:vendor.name, attributes:{href:'#'}}, attributes:{id:'vendor_'+model.deviceVendor+'_'+model.deviceType,'rel':'vendor'}}, '#deviceType_'+model.deviceType, -1);
                        $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id+'_'+model.deviceVendor,'rel':'model'}}, '#vendor_'+model.deviceVendor+'_'+model.deviceType, -1);
                    } else {
                        $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id+'_'+model.deviceVendor,'rel':'model'}}, '#vendor_'+model.deviceVendor+'_'+model.deviceType, -1);
                    }
                    $.tree.focused().select_branch('#model_'+model.id+'_'+model.deviceVendor);
                    $.tree.focused().refresh('#model_'+model.id+'_'+model.deviceVendor);
                }

                resetModelForm();

            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");

                if (responseText.errors && responseText.errors.errorCount > 0) {
                    var i, fieldErrors = responseText.errors.fieldErrors;

                    for (i = 0; i < fieldErrors.length; i++) {
                        var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                        $(temp).val(''+fieldErrors[i].defaultMessage);

                        if (fieldErrors[i].field == 'deviceType' || fieldErrors[i].field == 'deviceVendor') {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',fieldErrors[i].field+' '+fieldErrors[i].defaultMessage);
                        }
                    }
                }
            }

            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;

                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);

                    if (fieldErrors[i].field == 'deviceType' || fieldErrors[i].field == 'deviceVendor') {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',fieldErrors[i].field+' '+fieldErrors[i].defaultMessage);
                    }
                }
            } else {
                var vendor = responseText.deviceVendor;
                var model = responseText.deviceModel;
                var treeType = $(":input:radio[name='treeType']:checked").val();

                if (treeType == 'M') {
                    $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id,'rel':'model'}}, '#vendor_'+model.deviceVendor, -1);
                    $.tree.focused().select_branch('#model_'+model.id);
                    //$.tree.focused().select_node('#model_'+model.id);
                } else {
                    var select_node = $.tree.focused().get_node('#vendor_'+model.deviceVendor+'_'+model.deviceType);

                    if (!select_node.size()) {
                        $.tree.focused().create({data:{title:vendor.name, attributes:{href:'#'}}, attributes:{id:'vendor_'+model.deviceVendor+'_'+model.deviceType,'rel':'vendor'}}, '#deviceType_'+model.deviceType, -1);
                        $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id+'_'+model.deviceVendor,'rel':'model'}}, '#vendor_'+model.deviceVendor+'_'+model.deviceType, -1);
                    } else {
                        $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id+'_'+model.deviceVendor,'rel':'model'}}, '#vendor_'+model.deviceVendor+'_'+model.deviceType, -1);
                    }
                    $.tree.focused().select_branch('#model_'+model.id+'_'+model.deviceVendor);

                }

                resetModelForm();

            }
        }

        function modelUpdateResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");

                var model = responseText.deviceModel;
                var treeType = $(":input:radio[name='treeType']:checked").val();
                var vendor = $.tree.focused().parent('#model_' + deviceTreeId);

                if (treeType =='M') {
                    //var vendor = $.tree.focused().parent('#model_'+deviceTreeId);
                    var resultVendorId = 'vendor_' + model.deviceVendor;
                    var vendorId = vendor.attr('id');

                    if (vendorId == resultVendorId) {
                        $.tree.focused().rename('#model_' + deviceTreeId, model.name);
                    } else {
                        $.tree.focused().remove('#model_' + deviceTreeId);

                        if (vendor.children("ul").size() == 0) {
                            $.tree.focused().remove('#' + vendor.attr('id'));
                        }

                        $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_' + model.id, 'rel':'model'}}, '#vendor_' + model.deviceVendor, -1);

                        $.tree.focused().open_branch('#vendor_' + model.deviceVendor);
                        $.tree.focused().select_branch('#model_' + model.id);
                        $.tree.focused().refresh('#model_' + model.id);
                    }
                } else {
                    //var vendor = $.tree.focused().parent('#model_'+deviceTreeId);
                    var v = vendor.attr('id');

                    v = v.replace(/^vendor_/, '');
                    var vendorId = v.substring(0, v.indexOf("_"));

                    var deviceType = $.tree.focused().parent('#' + vendor.attr('id'));
                    var deviceTypeId = deviceType.attr('id');
                    var resultDeviceTypeId = 'deviceType_' + model.deviceType;

                    if (vendorId == model.deviceVendor && deviceTypeId == resultDeviceTypeId) {
                        $.tree.focused().rename('#model_' + deviceTreeId, model.name);
                    } else {
                        $.tree.focused().remove('#model_' + deviceTreeId);

                        if (vendor.children("ul").size() == 0) {
                            $.tree.focused().remove('#' + vendor.attr('id'));
                        }

                        var select_node = $.tree.focused().get_node('#vendor_' + model.deviceVendor + '_' + model.deviceType);

                        if (!select_node.size()) {
                            $.tree.focused().create({data:{title:responseText.deviceVendor.name, attributes:{href:'#'}}, attributes:{id:'vendor_' + model.deviceVendor + '_' + model.deviceType, 'rel':'vendor'}}, '#deviceType_' + model.deviceType, -1);
                            $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_' + model.id + '_' + model.deviceVendor, 'rel':'model'}}, '#vendor_' + model.deviceVendor+'_' + model.deviceType, -1);
                        } else {
                            $.tree.focused().create({data:{title:model.name, attributes:{href:'#'}}, attributes:{id:'model_'+model.id+'_'+model.deviceVendor,'rel':'model'}}, '#vendor_'+model.deviceVendor+'_'+model.deviceType, -1);
                        }

                        var device = $.tree.focused().parent('#deviceType_'+model.deviceType);
                        $.tree.focused().open_branch('#'+device.attr('id'));
                        $.tree.focused().open_branch('#deviceType_'+model.deviceType);
                        $.tree.focused().open_branch('#vendor_'+model.deviceVendor+'_'+model.deviceType);

                        $.tree.focused().select_branch('#model_'+model.id+'_'+model.deviceVendor);
                        $.tree.focused().refresh('#model_'+model.id+'_'+model.deviceVendor);
                    }
                }

                modelTabListener();
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");

                if (responseText.errors && responseText.errors.errorCount > 0) {
                    var i, fieldErrors = responseText.errors.fieldErrors;

                    for (i = 0; i < fieldErrors.length; i++) {
                        var temp = '#'+fieldErrors[i].objectName+' :input[name=\"' + fieldErrors[i].field +'\"]';
                        $(temp).val(''+fieldErrors[i].defaultMessage);

                        if (fieldErrors[i].field == 'deviceType' || fieldErrors[i].field == 'deviceVendor') {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',fieldErrors[i].field + ' ' + fieldErrors[i].defaultMessage);
                        }
                    }
                }
            }
        }

        function modelDeleteResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");

                resetModelForm();
                //alert(responseText.id);
                var modelId = responseText.modelId;

                $.tree.focused().remove('#model_' + deviceTreeId);
                modelTabListener();
                //$('#container-4').disableTab(2);
                $("#deviceVendor_input").val($("#deviceVendor option:selected").text())
                $("#mainDeviceType_input").val($("#mainDeviceType option:selected").text())
                $("#mainDeviceType_input").show();
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");

                if (responseText.errors && responseText.errors.errorCount > 0) {
                    var i, fieldErrors = responseText.errors.fieldErrors;
                    for (i = 0; i < fieldErrors.length; i++) {
                        var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                        $(temp).val(''+fieldErrors[i].defaultMessage);
                    }
                }
            }
        }

     
        // vendor tab active
        function vendorTabListener() {

            var devicevendorId = $("#vendorForm :hidden[name='id']").val();

            if (devicevendorId) {
                $('#fragment-1 #add1').hide();
                $('#fragment-1 #update1').show();
                $('#fragment-1 #delete1').show();
            } else {
                $('#fragment-1 #add1').show();
                $('#fragment-1 #update1').hide();
                $('#fragment-1 #delete1').hide();
            }
        }

        // model tab active
        function modelTabListener() {

            //var devicemodelId = $("#modelForm :hidden[name='id']").val();
            var devicemodelId = $("#modelForm :hidden[name='modelId']").val();
            //alert(devicemodelId);
            if (devicemodelId != null && devicemodelId != "") {
                $('#fragment-2 #add2').hide();
                $('#fragment-2 #update2').show();
                $('#fragment-2 #delete2').show();
            } else {
                $('#fragment-2 #add2').show();
                $('#fragment-2 #update2').hide();
                $('#fragment-2 #delete2').hide();
            }
        }

        // vendor form reset
        function resetVendorForm() {
            $("#vendorForm :hidden[name='id']").val('');
            $('#vendorForm').resetForm();
        }

        // model form 초기화
        function resetModelForm() {
            $('#photo img').attr('src', '');
            $("#modelForm :hidden[name='modelId']").val('');
            $('#upload .text').text('');
            $("#mainDeviceTypeView").val("");
            $("#mainDeviceTypeView").hide();
            $("#mainDeviceType_input").hide();
            $("#mainDeviceType").hide();
            $('#modelForm').resetForm();
            MeterAddChannelGridData = [];
            DeviceModelSelectId =0;
            MeterConfigId=0;
            lastChannelIndex = 0;
            getMeterAddChannelGrid();
            getChannelListByNotMeterGrid();
            var mainDeviceTypeName = $("#mainDeviceType option:selected").text();
            getSubDeviceTypeCombo(mainDeviceTypeName);
            viewDeviceConfig(mainDeviceTypeName);
        }

        function setSearchWord(){
            $('#searchWord').click( function() {
                $(this).val('');
                $('#searchCount').text('');
            });

            $('#searchWord').keypress(function(e) {
                var code=(e.keyCode?e.keyCode:e.which);
                if (code == 13) { // Enter keycode
                    $.tree.focused().search($(this).val());
                }
            });
        }

        function viewDeviceTree(sel) {
            auto='';
            $('#searchWord').unautocomplete();
            $('#basic_html').tree( {

                data : {
                    type : 'json',
                    opts : {
                        method : 'GET',
                        url : '${ctx}/gadget/system/modeltree.do'
                    }
                },
                types   : {
                    "default" : {
                        deletable : false,
                        renameable : true,
                        icon : {
                           image : '${ctx}/js/tree/themes/default/vendor.gif'

                        }
                    },
                    "device" : {

                        icon : {
                           image : '${ctx}/js/tree/themes/default/device.gif'

                        }
                    },
                    "vendor" : {

                        icon : {
                           image : '${ctx}/js/tree/themes/default/vendor.gif'

                        }
                    },

                    "model" : {
                        icon : {
                           image : '${ctx}/js/tree/themes/default/model.gif'

                        }
                    },
                    "config" : {
                        icon : {
                           image : '${ctx}/js/tree/themes/default/config.gif'

                        }
                    }
                },
                callback : {

                    'onload' : function(t) {
                        t.settings.data.opts.static = false;
                    },

                    'beforedata' : function(n, t) {
                        return {
                            supplierId : supplierId
                        };
                    },
                    'ondata' : function(json) {
                        if (json == false) {
                            return;
                        }

                        if (json.data)
                            return json; //create()함수호출시 callback으로 ondata이 호출되어 충돌을 방지하는 코드

                        var data = [];
                        var jsonData = json.jsonTrees;
              
                        var jlen = jsonData.length;
                        for (var i = 0; i < jlen; i++) {

                            var device = jsonData[i].data;
                            var deviceTypes = jsonData[i].children;
                            var vendors = jsonData[i].children1;
                            var models = jsonData[i].children2;
                            var configs = jsonData[i].children3;

                            var children = [];
                            var dlen = deviceTypes.length;
                            for (var j = 0; j < dlen; j++) {

                                var deviceType = deviceTypes[j];

                                var children1 = [];
                                var vlen= vendors[j].length;
                                for (var k = 0; k < vlen; k++) {

                                    var vendor = vendors[j][k];

                                    var children2 = [];
                                    var mlen = models[j][k].length;
                                    for (var l = 0; l < mlen; l++) {
                                        var model = models[j][k][l];
                                        //var children3 = [];
                                    
                                        auto= auto+'^'+model.name;

                                        children2.push( {
                                            'data' : {
                                                'title' : model.name,
                                                attributes : {
                                                    'href' : '#'
                                                }
                                            },
                                            'attributes' : {
                                                'id' : 'model_' + model.id+'_'+ vendor.id,'rel':'model'
                                            }//,
                                            //'children' : children3
                                        });
                                    }

                                    auto= auto+'^'+vendor.name;
                                    children1.push( {
                                        'data' : {
                                            'title' : vendor.name,
                                            attributes : {
                                                'href' : '#'
                                            }
                                        },
                                        'attributes' : {
                                            'id' : 'vendor_' + vendor.id+'_'+ deviceType.id,'rel':'vendor'
                                        },
                                        'children' : children2
                                    });
                                }
                                auto= auto+'^'+deviceType.name;
                                children.push( {
                                    'data' : {
                                        'title' : deviceType.name,
                                        attributes : {
                                            'href' : '#'
                                        }
                                    },
                                    'attributes' : {
                                        'id' : 'deviceType_' + deviceType.id,'rel':'model'
                                    },
                                    'children' : children1
                                });
                            }
                            auto= auto+'^'+device.name;
                            data.push( {
                                'data' : {
                                    'title' : device.name,
                                    'attributes' : {
                                        'href' : '#'
                                    }
                                },
                                'attributes' : {
                                    'id' : 'device_' + device.id,'rel':'device'
                                },
                                'children' : children
                            });
                        }

                        var autoArr = auto.split('^');
                        $('#searchWord').autocomplete(autoArr);
                        setSearchWord();
                        return data;

                    },

                    'onselect' : function(n, t) {
                        var node = $(n).attr('id');

                        if (node.indexOf('vendor_') > -1) {
                            var v = node.replace(/^vendor_/, '');
                            deviceTreeId = v;
                            if (v.indexOf('_') > -1) {
                                viewDeviceVendorTab(v.substring(0,v.indexOf("_")));
                            } else {
                                viewDeviceVendorTab(v);
                            }
                        } else if (node.indexOf('model_') > -1) {
                            var m = node.replace(/^model_/, '');
                            deviceTreeId = m;

                            if (m.indexOf('_') > -1) {
                                viewDeviceModelTab(m.substring(0,m.indexOf("_")));
                            } else {
                                viewDeviceModelTab(m);
                            }
                        //} else if (node.indexOf('config_') > -1){
                        //    viewDeviceConfigTab(node.replace(/^config_/, ''));
                        //} else {
                            //alert('incorrect data');
                        }
                    },
                    'onsearch' : function(n, t) {
                        t.container.find('.searchResult').removeClass('searchResult');
                        n.addClass('searchResult');
                        $('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
                    }
                }

             });
        }

        function viewTree() {
            auto='';
            $('#searchWord').unautocomplete();
            $('#basic_html').tree({

                data : {
                    type : 'json',
                    opts : {
                        method : 'POST',
                        url : '${ctx}/gadget/system/vendortree.do'}
                },
                types   : {
                    "default" : {
                        deletable : true,
                        renameable : true

                    },

                    "vendor" : {

                        icon : {
                           image : '${ctx}/js/tree/themes/default/vendor.gif'

                        }
                    },

                    "model" : {
                        icon : {
                           image : '${ctx}/js/tree/themes/default/model.gif'

                        }
                    },
                    "config" : {
                        icon : {
                           image : '${ctx}/js/tree/themes/default/config.gif'

                        }
                    }
                },
                callback : {
                    /* 'onload' : function(t) {
                        t.settings.data.opts.static = false;
                    },*/
                    'beforedata' : function (n, t) {
                        return {
                            supplierId : supplierId
                        };
                    },
                    'ondata' : function(json) {
                        if (json == false) {
                            return;
                        }

                        if (json.data)
                            return json; //create()함수호출시 callback으로 ondata이 호출되어 충돌을 방지하는 코드

                        var data = [];
                        var jsonData = json.jsonTrees;
                        var jlen = jsonData.length;
                        for (var i = 0; i < jlen; i++) {

                            var vendor = jsonData[i].data;
                         
                            var models = jsonData[i].children;
                            var childJsonData = jsonData[i].children1;
                            var children = [];
                            var mlen = models.length;
                            for (var j = 0; j < mlen; j++) {
                                var model = models[j];

                                auto= auto+'^'+model.name;
                                children.push({'data':{'title':model.name, attributes:{'href':'#' }}, 'attributes':{'id':'model_'+model.id,'rel':'model'}});

                            }
                            auto= auto+'^'+vendor.name;
                            data.push({'data':{'title':vendor.name, 'attributes':{'href':'#' }}, 'attributes':{'id':'vendor_'+vendor.id,'rel':'vendor'}, 'children':children});
                        }
                        var autoArr = auto.split('^');
                        $('#searchWord').autocomplete(autoArr);
                        setSearchWord();
                        return data;

            },
                    'onselect' : function(n, t) {
                        var node = $(n).attr('id');

                        if (node.indexOf('vendor_') > -1) {
                            var v = node.replace(/^vendor_/, '');
                            deviceTreeId = v;
                            if (v.indexOf('_') > -1) {
                                viewDeviceVendorTab(v.substring(0,v.indexOf("_")));
                            } else {
                                viewDeviceVendorTab(v);
                            }
                        } else if (node.indexOf('model_') > -1){
                            var m = node.replace(/^model_/, '');
                            deviceTreeId = m;

                            if (m.indexOf('_') > -1) {
                                viewDeviceModelTab(m.substring(0,m.indexOf("_")));
                            } else {
                                viewDeviceModelTab(m);
                            }
                        }
                    },
                    'onsearch' : function(n, t) {
                        t.container.find('.searchResult').removeClass('searchResult');
                        n.addClass('searchResult');
                        $('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
                    }
                }

             });
        }

        // vendor click eventHandler
        function viewDeviceVendorTab(devicevendorId) {
            resetVendorForm();

            $.getJSON('${ctx}/gadget/system/vendorinfo.do', {devicevendorId:devicevendorId},
                  function(json) {
                      var deviceVendor = json.deviceVendor;
                      bindingVendorInfo(deviceVendor);
                      $('#container-3').show('fast', vendorTabListener());

                      $('#container-4').hide();

                      //$('#container-5').hide();
                    resetModelForm();
                    //resetConfigForm();
            });
        }

        // model click eventHandler
        function viewDeviceModelTab(devicemodelId) {

            resetVendorForm();
            //resetConfigForm();
            resetModelForm();
            DeviceModelSelectId = devicemodelId;
            $.getJSON('${ctx}/gadget/system/modelinfo.do', {devicemodelId:devicemodelId, supplierId:supplierId},
                function(json) {

                    var deviceModelConfig = json.deviceModelConfig;
                    var channels = json.channels;
                    var mainDeviceTypeDesc=json.mainDeviceTypeDesc;
                    lastChannelIndex = json.lastchannelIndex;
                    MeterConfigId = deviceModelConfig.configId;
                    var meterProtocol = deviceModelConfig.meterProtocol;
                    if(meterProtocol == 'DLMS') {
                    	$('#obisCodeTab').show();
                    	if(cmdAuth == "true") {
                    		$('#meterSettingTab').show();
                        } else {
                        	$('#meterSettingTab').hide();
                        }
                    } else {
                    	$("#roleTab").hide();
                    	$('#obisCodeTab').hide();
                    	$('#meterSettingTab').hide();
                    }
                    
                    $('#container-3').hide();
                    bindingModelConfigInfo(deviceModelConfig, channels, mainDeviceTypeDesc);
                    bindingPhaseInfo(deviceModelConfig.phase);

                    $('#container-4').show('fast', modelTabListener()); //최대한 늦춰야 한다.
                    $("#meterProtocol").val(meterProtocol);
                    $("#meterProtocol").selectbox();

                    $("#modelinfoTab").click();
                }
            );
        }

        // vendor data setting
        function bindingVendorInfo(deviceVendor) {
            $('#vendorForm').setForm(deviceVendor);
        }

        // phase data setting
        function bindingPhaseInfo(phase) {
            $('#phase').val(phase);
            $('#phase').selectbox();
        }

        // model data setting
        function bindingModelInfo(deviceModel) {
            $('#modelForm').setForm(deviceModel);
            $('#photo img').attr('src', "${ctx}/"+deviceModel.image);
            getReferenceVendors(deviceModel.deviceVendor);
        }

        // model config data setting
        function bindingModelConfigInfo(deviceModelConfig, channels, mainDeviceTypeDesc) {
			$("#deviceVendor option[value="+deviceModelConfig.deviceVendor+"]").attr("selected","selected");
			$("#deviceVendor_input").val($("#deviceVendor option:selected").text())
            $('#modelForm').setForm(deviceModelConfig);

            $('#photo img').attr('src', "${ctx}/"+deviceModelConfig.image);

            getSubDeviceTypeCombo(deviceModelConfig.mainDeviceTypeName, deviceModelConfig.deviceType);
            $("#mainDeviceType").hide();

            $("#mainDeviceTypeView").val(mainDeviceTypeDesc);
            $("#mainDeviceTypeView").show();
            viewDeviceConfig(deviceModelConfig.mainDeviceTypeName);
 
            MeterAddChannelGridData = channels;
            getMeterAddChannelGrid();
            getChannelListByNotMeterGrid();
        }

        /**
         * 숫자만 입력. 정수
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForInt(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForInt(val);
            val = removeFstZeroForInt(val);
            src.value = val;
            //src.focus();
        }

        // 숫자 이외 문자 제거
        function removeCharForInt(val) {
            var num = val.replace(/[\D]/g, '');
            return num;
        }

        // 앞에 0 제거
        function removeFstZeroForInt(val) {
            var pattern = /(^0*)(\d+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        /**
         * 숫자만 입력. 실수
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForReal(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForReal(val);
            val = removeFstZeroForReal(val);
            src.value = val;
            //src.focus();
        }

        // 숫자 이외 문자 제거:소수점 허용
        function removeCharForReal(val) {
            var num = val.replace(/[^\d\.]/g, '');
            var idx = 0;
            var len = 0;

            if (num.indexOf('.', num.indexOf('.')+1) != -1) {
                idx = num.indexOf('.');
                len = num.length;
                num = num.substring(0, idx+1) + num.substring(idx+1, len).replace(/\./g, '');
            }

            return num;
        }

        // 앞에 0 제거:소수점포함
        function removeFstZeroForReal(val) {
            var pattern = /(^0*)([\d\.]+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        // TOU Profile
        
        // 타입별 조회시 사용될 검색조건
        var getConditionArray = function() {
            var arrayObj = Array();
            var idx = 0;
            arrayObj[idx++] = $('#configId').val();
            return arrayObj;
        };

        function getFmtMessage() {
            var fmtMessage = new Array();
            var idx = 0;
            
            //fmtMessage[idx++] = "<fmt:message key="aimir.number"/>";                // 번호
            fmtMessage[idx++] = "<fmt:message key="aimir.model.lastmodifieddate"/>";    // 최종변경일
            fmtMessage[idx++] = "<fmt:message key="aimir.success.count"/>";             // 성공 개수
            fmtMessage[idx++] = "<fmt:message key="aimir.failure.count"/>";             // 실패 개수
            fmtMessage[idx++] = "<fmt:message key="aimir.try.count"/>";                 // 시도 개수
            fmtMessage[idx++] = "<fmt:message key="aimir.env.error"/>";                 // 사용할 수 없는 환경입니다.
            
            return fmtMessage;
        }

        function searchList() {
            getMeterProgramLogList();
        }

        // grid 조회 후 화면 초기화 
        function clearMeterProgramSettings() {
            $("#settings").val("");
            $("#meterProgramKind option:first").attr("selected", "selected");
        }

        var isRetry = false;
        var isApply = false;
        // grid row 클릭 시 settings 데이터 조회
        function searchMeterProgramSettings(meterProgramId, meterProgramKind, sCount, fCount) {
        	$("#meterProgramId").val(meterProgramId);
        	$("#meterProgramKind").val(meterProgramKind);

            $.post("${ctx}/gadget/system/getMeterProgramSettingsData.do",
                   {meterProgramId:meterProgramId},
                   function(json) {
                       $("#settings").val(json.result);
                       viewApplyInput(false);
                       
                       if (fCount > 0) {    // 재시도
                           $("#meterProgramReg").show();
                           $("#meterProgramApply").hide();
                           $("#meterProgramRetry").show();
                           $("#regOk").hide();
                           $("#regCancel").hide();
                           isRetry = true;
                           isApply = false;
                       } else if (sCount == 0, fCount == 0) {   // 적용
                           $("#meterProgramReg").show();
                           $("#meterProgramApply").show();
                           $("#meterProgramRetry").hide();
                           $("#regOk").hide();
                           $("#regCancel").hide();
                           isRetry = false;
                           isApply = true;
                       } else {
                           $("#meterProgramReg").show();
                           $("#meterProgramApply").hide();
                           $("#meterProgramRetry").hide();
                           $("#regOk").hide();
                           $("#regCancel").hide();
                           isRetry = false;
                           isApply = false;
                       }
                   });
        }
        
        var normalSettingHeight = 342;  // 353 -> 303
        var applySettingHeigth = 313;   // 324 -> 274
        function viewApplyInput(val) {
        	if (val) {
        		$("#settings").height(applySettingHeigth);
        		$("#cron").show();
        		$("#cronSetting").val("");
                $("#applyOk").show();
                $("#applyCancel").show();
        	} else {
                $("#settings").height(normalSettingHeight);
                $("#cron").hide();
                $("#applyOk").hide();
                $("#applyCancel").hide();
        	}
        }

    //############################
    //ChannelListGrid check 컬럼 정의
    //############################
    
    
    //체크 컬럼 모델 정의.    
     var myCboxSelChannel = new Ext.grid.CheckboxSelectionModel({
         singleSelect: false
    });
  
    //ChannelListGrid row SElect Event
    function funcRowselect()
    {
         var  selectedRows2= myCboxSelChannel.getSelections();
        //reset array

        //console.log("channels2 selectedRows2 ",selectedRows2);
        channels2 = []; 

        for(i=0; i<selectedRows2.length; i++)
        {
            
            channels2[i] = new Array();           
            channels2[i][0]= selectedRows2[i].get('displayid');
            channels2[i][1]= selectedRows2[i].get('name');
            channels2[i][2]= selectedRows2[i].get('channelIndex')-1;
            channels2[i][3]= selectedRows2[i].get('displayType');

        }   
            
    }
    
    // MeterListGrid row de-select Event
    function funcRowdeselect()
    {
         var  selectedRows2= myCboxSelChannel.getSelections();
        //reset array

        channels2 = []; 
        
        for(i=0; i<selectedRows2.length; i++)
        {
            
            channels2[i] = new Array();           
            channels2[i][0]= selectedRows2[i].get('displayid');
            channels2[i][1]= selectedRows2[i].get('name');
            channels2[i][2]= selectedRows2[i].get('channelIndex')-1;
            channels2[i][3]= selectedRows2[i].get('displayType');
        }   
    } 

    var displayTypeData =["SaveAndDisplay","SaveOnly","DisplayOnly"];
    var displayTypeComboData = [];
 
    for(d=0;d<displayTypeData.length; d++){
        displayTypeComboData.push({
            id: displayTypeData[d],
            name: displayTypeData[d]});
    }

 // create reusable renderer
    Ext.util.Format.comboRenderer = function(combo){
        return function(value, metadata){
            var record = combo.findRecord(combo.valueField, value);
            var returnValue;
            if(record) {
            	returnValue = record.get(combo.displayField)
            } else {
            	returnValue = combo.valueNotFoundText
            }
            metadata.attr = 'ext:qtip="' + returnValue + '"';
            return returnValue;
        };
    };

   var combo = new Ext.form.ComboBox({
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                mode: 'local',
                store: new Ext.data.JsonStore({
                    id: 0,
                    data: displayTypeComboData,
                    fields: ["id", "name"]
                }),
                valueField: "id",
                displayField: "name",
                editable: false
    });

    var MeterAddChannelGridData = [];

    //meterAddChannelGrid propeties
    var meterAddChannelGridInstanceOn = false;
    var meterAddChannelGrid;
    var meterAddChannelGridModel;
 
    function getMeterAddChannelGrid(){
         
         var gridWidth = 270;

         var meterAddChannelGridStore = new Ext.data.JsonStore({
            autoLoad: true,
            data: MeterAddChannelGridData || {},
            root:'',
            fields: [
                      "id" 
                      ,"displayid" 
                      ,"name"
                      ,"channelIndex"
                      ,"displayType"
                     ],
           //EXTJS 그리드 필드별 정렬
           sortInfo: { field: "channelIndex", direction: "ASC" }
         });

           meterAddChannelGridModel = new Ext.grid.ColumnModel({
            columns: [
                myCboxSelChannel
               ,{header: "Index", dataIndex: 'channelIndex'
               ,editor: new Ext.form.NumberField({
                       id: 'channelIndex',
                       allowBlank: false,
                       allowNegative: false}) 
               ,width:50, align:'center'}
               ,{header: "Channel", dataIndex: 'name', width:(gridWidth-50)/2}
               ,{header: "DisplayType", dataIndex: 'displayType', 
               editor: combo, renderer: Ext.util.Format.comboRenderer(combo),width:(gridWidth-50)/2}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 125
            }
        });

        if (meterAddChannelGridInstanceOn == false) {
            //Grid panel instance create
            meterAddChannelGridPanel = new Ext.grid.EditorGridPanel({
            store: meterAddChannelGridStore,
            colModel : meterAddChannelGridModel,
            sm: myCboxSelChannel,
            autoScroll:false,
            width:  gridWidth,
                //
                //패널 높이 설정
               // autoHeight: true,
            height: 140,
            stripeRows : true,
            columnLines: true,
            loadMask:{
                    msg: 'loading...'
            },
                //랜더링 디비전
            renderTo: 'MeterAddChannelGridDiv',
            viewConfig: {
               
             showPreview:true,
             emptyText: 'No data to display'
             },
         });

         meterAddChannelGridInstanceOn = true;
        meterAddChannelGridPanel.on('afteredit', updateEventHandler,this);
        } else {
            meterAddChannelGridPanel.setWidth( gridWidth);
            meterAddChannelGridPanel.reconfigure(meterAddChannelGridStore, meterAddChannelGridModel);
        }
    };

     function updateEventHandler(e){
            var gridrecord = e.record;
            var gridData = gridrecord.data;
            var change = ChannelUpdate(gridData);
/*
            if(change == 1){
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Update Channel Success.");
                getMeterAddChannelGrid();
            }else{
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Update Channel Cancel.");
            }*/
            
      
     }

     var MeterExceptChannelGridData = [];

    var channelListByNotMeterGridInstanceOn = false;
    var channelListByNotMeterGrid;
    var channelListByNotMeterGridModel;

     function getChannelListByNotMeterGrid(){

        $.getJSON('${ctx}/gadget/system/getExceptDisplayChannelList.do'
                , {id:MeterConfigId, supplierId:supplierId}
                , function(json) {
                      MeterExceptChannelGridData = json.exceptdisplaychannelList;
                      makeChannelListByNotMeterGrid();
                  });

     }

     function makeChannelListByNotMeterGrid() {

        var gridWidth = 270;

        var channelListByNotMeterGridStore = new Ext.data.JsonStore({
            autoLoad: true,
            data: MeterExceptChannelGridData || {},
            root:'',
            fields: [
                      "id"
                     , "no"
                     , "name"
                     , "serviceType"
                     , "unit"
                     , "chmethod"
                     ]
        });//Store End

        // channelListByNotMeterGrid Model DEfine
        channelListByNotMeterGridModel = new Ext.grid.ColumnModel({
            columns: [
                myCboxSelChannel2
                ,{header: "no", dataIndex: 'no', width:38, align:'center'}
                ,{header: "name", dataIndex: 'name', width:150}
                ,{header: "ServieType", dataIndex: 'serviceType', width:(gridWidth-150)/3}
                ,{header: "unit", dataIndex: 'unit', width:(gridWidth-150)/3}
                ,{header: "method", dataIndex: 'chmethod', width:(gridWidth-150)/3}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 130
            }
        });

        if (channelListByNotMeterGridInstanceOn == false) {

            //Grid panel instance create
            channelListByNotMeterGridPanel = new Ext.grid.GridPanel({
                store: channelListByNotMeterGridStore,
                colModel : channelListByNotMeterGridModel,
               //selectModel define.
                sm: myCboxSelChannel2,
                autoScroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                height: 145,
                //autoHeight: true,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'ChannelListByNotMeterGridDiv',
                viewConfig: {
                    //orceFit:true,
                    enableRowBody:false,
                    showPreview:true,
                    emptyText: 'No data to display'
                }
            });
            channelListByNotMeterGridInstanceOn = true;
        } else {
            channelListByNotMeterGridPanel.setWidth(gridWidth);
            channelListByNotMeterGridPanel.reconfigure(channelListByNotMeterGridStore, channelListByNotMeterGridModel);
        }
    };

     var myCboxSelChannel2 = new Ext.grid.CheckboxSelectionModel({
     
      singleSelect: false
    });
 
   
    //미터에 등록에 선택된 채널 리스트/ row select 이벤트.)
    function funcRowselect2()
    {           
       
        var  selectedRows= myCboxSelChannel2.getSelections();
        //reset array

        channels= [];  
        
        for(i=0; i<selectedRows.length; i++)
        {
            
            channels[i] = new Array();           
            channels[i][0]= selectedRows[i].get('id');
            channels[i][1]= selectedRows[i].get('name');
            channels[i][2]= selectedRows[i].get('no')-1;
        }   

    }
   
    //선택된 채널 리스트 중에 선택 해지가 제외된 채널리스트 / row deselect 이벤트.)
    function funcRowDeselect2()
    {          
     
        var  selectedRows= myCboxSelChannel2.getSelections();
        //reset array

        channels= [];  
        
        for(i=0; i<selectedRows.length; i++)
        {
            
            channels[i] = new Array();           
            channels[i][0]= selectedRows[i].get('id');
            channels[i][1]= selectedRows[i].get('name');
            channels[i][2]= selectedRows[i].get('no')-1;

        }   
      
    }
    /*
     meterconfig의 채널을 추가한다. */
    function ChannelAdd(){

        funcRowselect2(); //선택된 채널 리스트
        //ChannelListByNotMeterGridDiv.hide
        if(channels.length == 0){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.notChooseAddChannel'/>");
        }else{
        //##channel id의 갯수 만큼 미터에 채널을 등록.
            
            var deletedarray = new Array();

            for(var i=0; i < channels.length; i++){

                /*channel Delete */

                deletedarray.push(MeterExceptChannelGridData[channels[i][2]]);

                /*channel Add */
                MeterAddChannelGridData.push({  
                    id : ""//채널 아이디             
                    ,displayid : channels[i][0]//채널 아이디
                    ,name : channels[i][1]//채널 이름
                    ,channelIndex : ++lastChannelIndex //채널 index
                    ,displayType : "SaveAndDisplay" //채널 displayType : 디폴트값 
               });

            }

            if (deletedarray.length > 0) {
                for (var i = 0; i < deletedarray.length; i++) {
                    MeterExceptChannelGridData.remove(deletedarray[i]);
                }
            }

            for (var i = 0; i < MeterExceptChannelGridData.length; i++) {
                MeterExceptChannelGridData[i].no = i + 1;
            }
            
            /*console.log("channel add confirm : ",MeterAddChannelGridData);
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.addChannel'/>");*/
            getMeterAddChannelGrid();           
            makeChannelListByNotMeterGrid();
        }

    }

    function ChannelUpdate(griddata){
        
        if(griddata!=null){
            var newchannelIndex = griddata.channelIndex;
            var newdisplayType  = griddata.displayType; 
            var change = 0 ;
  
            for(var i=0;i<MeterAddChannelGridData.length;i++){
                if(MeterAddChannelGridData[i].name == griddata.name){
                    MeterAddChannelGridData[i].channelIndex= newchannelIndex;
                    MeterAddChannelGridData[i].displayType= newdisplayType;
                    change = 1;
                }
            }
        }
        return change;
    }

    var singleExceptChannelData;
    function ChannelDelete(){
        funcRowselect();//선택이 해지된 채널 리스트
         //console.log("channels2 ",channels2);
         if(channels2.length == 0){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.notChooseDelChannel'/>");
        }else{
            var deletedarray = new Array();
             /*channel Delete */
             for(var i=0; i < channels2.length; i++){
                deletedarray.push(MeterAddChannelGridData[channels2[i][2]]);

                var addBynotMeter = new Array();
                singleExceptChannelData = getsingleExceptChannelData(channels2[i][0]);
               
                MeterExceptChannelGridData.push({
                    id          :channels2[i][0]
                    ,no         :MeterExceptChannelGridData.length+1
                    ,name       :channels2[i][1]
                    ,serviceType:singleExceptChannelData.serviceType
                    ,unit       :singleExceptChannelData.unit
                    ,chmethod   :singleExceptChannelData.chmethod 
                });
                --lastChannelIndex;
            }

            if (deletedarray.length > 0) {
                for (var i = 0; i < deletedarray.length; i++) {
                    MeterAddChannelGridData.remove(deletedarray[i]);
                }
            }

            for (var i = 0; i < MeterAddChannelGridData.length; i++) {
                MeterAddChannelGridData[i].channelIndex = i + 1;
            }
            /*console.log("channel DELETE 후  confirm : ", MeterAddChannelGridData);
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.deleteChannel'/>");*/
            getMeterAddChannelGrid();           
            makeChannelListByNotMeterGrid();
        }
    }

    function getsingleExceptChannelData(id) {

        var jsonchannel = $.ajax({
            url : '${ctx}/gadget/system/getSingleExceptDisplayChannel.do',
            data : {
                id : id,
                supplierId : supplierId
            },
            async : false
        }).responseText;
        eval("result="+jsonchannel);

        return result.singleExceptChannelData;
    }

    /* Meter Program Log 리스트 START */
    var meterProgramGridOn = false;
    var meterProgramStore;
    var meterProgramGrid;
    var meterProgramColModel;

    var getMeterProgramLogList = function() {
        var width = $("#meterProgramGridDiv").width();
        var pageSize = 15;

        meterProgramStore = new Ext.data.JsonStore({
            autoLoad : {params : {start : 0, limit : pageSize}},
            url : "${ctx}/gadget/system/getMeterProgramLogListRenew.do",
            baseParams : {
                supplierId : supplierId,
                configId : $('#configId').val()
            },
            totalProperty : 'totalCount',
            root : 'result',
            fields : ["lastModifiedDate", "meterProgramId", "meterProgramKind", "successCount", "failureCount", "tryCount"],
            listeners : {
                beforeload : function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                },
                load: function(store, record, options){
                    // 이전 데이터 모두 지움.
                    $("#meterProgramKind").val("");
                    $("#meterProgramKind").selectbox();
                    $("#settings").val("");
                }

            }
        });

        var colWidth = width/4 - chromeColAdd;
        var fmtMessage = getFmtMessage();

        meterProgramColModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0], dataIndex: 'lastModifiedDate', tooltip: fmtMessage[0], renderer: addTooltip}
               ,{header: fmtMessage[1], dataIndex: 'successCount', tooltip: fmtMessage[1], align: "right"}
               ,{header: fmtMessage[2], dataIndex: 'failureCount', tooltip: fmtMessage[2], align: "right"}
               ,{header: fmtMessage[3], dataIndex: 'tryCount', tooltip: fmtMessage[3], align: "right", width: colWidth - 4}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: colWidth
           }
        });

        if (meterProgramGridOn == false) {
            meterProgramGrid = new Ext.grid.GridPanel({
                store: meterProgramStore,
                colModel : meterProgramColModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(sm, row, rec) {
                            searchMeterProgramSettings(rec.get("meterProgramId"), rec.get("meterProgramKind"), rec.get("successCount"), rec.get("failureCount"));
                        }
                    }
                }),
                autoScroll: false,
                width: width,
                height: 403,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meterProgramGridDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: meterProgramStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });

            meterProgramGridOn = true;
        } else {
            meterProgramGrid.setWidth(width);
            var bottomToolbar = meterProgramGrid.getBottomToolbar();
            meterProgramGrid.reconfigure(meterProgramStore, meterProgramColModel);
            bottomToolbar.bindStore(meterProgramStore);
        }
    };

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
    
    var deleteBtn = function(value,meta,rec) {
    	var id = Ext.id();
    	var $div = $("<div></div>").attr("id", id);
    	var button = function() {
    		if($("#" + id).length > 0 && ($div.children().length<1)) {
    			new Ext.Button({
    				text: "<fmt:message key='aimir.button.delete'/>",
    				width: 40,
    				handler: function(b, e) {
    					obisMgmtHandler.deleteObis(rec, false);	
    				}				
    			}).render(id);
    		} else {
    			button.defer(100);
    		}
    	};

    	button.defer(100);
    	return $div[0].outerHTML;
    }
    
    var updateBtn = function(value,meta,rec) {
    	var id = Ext.id();
    	var $div = $("<div></div>").attr("id", id);
    	var button = function() {
    		if($("#" + id).length > 0 && ($div.children().length<1)) {
    			new Ext.Button({
    				text: "<fmt:message key='aimir.button.update'/>",
    				width: 40,
    				handler: function(b, e) {
    					obisMgmtHandler.updateObis(rec);	
    				}				
    			}).render(id);
    		} else {
    			button.defer(100);
    		}
    	};

    	button.defer(100);
    	return $div[0].outerHTML;
    }
    
    var dataTypeComboData;
    var dataTypeCombo;
    var meterEventComboData;
    var meterEventCombo;
    var accessComboData;
    var accessCombo;
    var obisCodeGridOn=false;
    var obisMgmtHandler = {
        drawObisCodeGrid: function() {
        	
            $.ajaxSetup({
                async : false
            });

        	var width = $('#obisCodeMgmt').width();

        	var widthColumn;
        	if(editSetting) {
        		widthColumn = (width)/11;
        	} else {
        		widthColumn = (width)/9;
        	}
           
            accessComboData =  [{id: "-", accessRight: "-"},
                                    {id: "RO", accessRight: "RO"},
                                    {id: "RW", accessRight: "RW"},
                                    {id: "ACTION", accessRight: "ACTION"}
                                ];
            
            meterEventComboData =  [{id: "Y", name: "<fmt:message key='aimir.button.yes'/>"},
                                {id: "N", name: "<fmt:message key='aimir.button.no'/>"}
                            ];
            
            // create the combo instance
            accessCombo = new Ext.form.ComboBox({
                id : "accessRight",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : widthColumn-5,
                store : new Ext.data.JsonStore({
                    id : 0,
                    data : accessComboData,
                    fields : ["id","accessRight"]
                }),
                valueField : "id",
                displayField : "accessRight",
                editable : false

            });
            
            meterEventCombo = new Ext.form.ComboBox({
                id : "meterEvent",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : widthColumn-10,
                store : new Ext.data.JsonStore({
                    id : 0,
                    data : meterEventComboData,
                    fields : ["id","name"]
                }),
                valueField : "id",
                displayField : "name",
                editable : false
            });
            
            $.post("${ctx}/gadget/system/getDataType.do",
                    function(json) {
            			dataTypeComboData = json.result;
                    });
            
            dataTypeCombo = new Ext.form.ComboBox({
                id : "dataType",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : widthColumn-20,
                store : new Ext.data.JsonStore({
                	id : 0,
                	data : dataTypeComboData,
                    fields : ["code","name","display"]
                }),
                valueField : "name",
                displayField : "display",
                editable : false
            });
            
            var obisCodeStore = new Ext.data.JsonStore({
            	autoLoad: {params:{start: 0, limit: 15}},
                url: "${ctx}/gadget/system/getObisCodeInfo.do",
                baseParams: {
                	modelId: DeviceModelSelectId,
            		obisCode: $('#obisCode').val(),
            		classId: $('#classId').val(),
            		attributeNo: $('#attributeNo').val()
                },
                root: 'result',
                totalProperty: 'totalCnt',
                fields: ['ID','OBISCODE','CLASSNAME','CLASSID','ATTRIBUTENAME','ATTRIBUTENO','DATATYPE','ACCESSRIGHT','DESCR','MODELID','METEREVENT'],
                listeners : {
                    beforeload: function(store, options) {
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                },
            });
            
            $.ajaxSetup({
                async : true
            });
            
            var columns = [];
            
            columns.push({header: "<span style='color:red'>* <fmt:message key='aimir.model.obisCode'/></span>", tooltip:"<fmt:message key='aimir.model.obisCode'/>", 
	 					dataIndex: 'OBISCODE', width:widthColumn+15, sortable: true, menuDisabled: true, renderer: addTooltip});
            
            columns.push({header: "<fmt:message key='aimir.model.className'/>", tooltip:"<fmt:message key='aimir.model.className'/>", 
                    dataIndex: 'CLASSNAME', width:widthColumn-5, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                    editor: new Ext.form.TextField({id: 'className', allowNegative: false})});
            
            columns.push({header: "<span style='color:red'>* <fmt:message key='aimir.model.classId'/></span>", tooltip:"<fmt:message key='aimir.model.classId'/>", 
                    dataIndex: 'CLASSID', width:widthColumn-5, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                    editor: new Ext.form.TextField({id: 'classId', allowBlank: false, allowNegative: false})});
            
            columns.push({header: "<fmt:message key='aimir.model.attributeName'/>", tooltip:"<fmt:message key='aimir.model.attributeName'/>", 
                    dataIndex: 'ATTRIBUTENAME', width:widthColumn, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                    editor: new Ext.form.TextField({id: 'attributeName', allowNegative: false})});
            
            columns.push({header: "<span style='color:red'>* <fmt:message key='aimir.model.attributeNo'/>/Method No</span>", tooltip:"<fmt:message key='aimir.model.attributeNo'/>/Method No",
                dataIndex: 'ATTRIBUTENO', width:widthColumn-5, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                editor: new Ext.form.TextField({id: 'attributeNo', allowBlank: false, allowNegative: false})});

            columns.push({header: "<fmt:message key='aimir.model.dataType'/>", tooltip:"<fmt:message key='aimir.model.dataType'/>",
                dataIndex: 'DATATYPE', width:widthColumn+5, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                editor: dataTypeCombo, renderer: Ext.util.Format.comboRenderer(dataTypeCombo)});
                //editor: new Ext.form.TextField({id: 'dataType', allowNegative: false})});
            
            columns.push({header: "<span style='color:blue'>* <fmt:message key='aimir.model.access'/></span>",tooltip:"<fmt:message key='aimir.model.access'/>",
                dataIndex: 'ACCESSRIGHT', width:widthColumn-5, sortable: true, menuDisabled: true, editable: editSetting,
                editor: accessCombo, renderer: Ext.util.Format.comboRenderer(accessCombo)});
            
            columns.push({header: "<fmt:message key='aimir.model.descr'/>", tooltip:"<fmt:message key='aimir.model.descr'/>",
                dataIndex: 'DESCR', width:widthColumn+10, sortable: true, menuDisabled: true, editable: editSetting, renderer: addTooltip,
                editor: new Ext.form.TextField({id: 'descr', allowNegative: false})});
            
            columns.push({header: "<fmt:message key='aimir.event'/>", tooltip:"<fmt:message key='aimir.event'/>",
                dataIndex: 'METEREVENT', width:widthColumn-15, sortable: true, menuDisabled: true, editable: editSetting,
                editor: meterEventCombo, renderer: Ext.util.Format.comboRenderer(meterEventCombo)});
            
            if(editSetting) {
            	columns.push({header: "<fmt:message key='aimir.setting'/>", width:widthColumn-10, editable: false, renderer: updateBtn,
            		sortable: true, menuDisabled: true});
            	
            	columns.push({header: "", width:widthColumn-10, editable: false, renderer: deleteBtn,
            		sortable: true, menuDisabled: true});
        	}
            
            var obisCodeModel=columns;

           $('#obisCodeListDiv').empty();
            
            obisCodeGrid = new Ext.grid.EditorGridPanel({
            	layout: 'fit',
            	width: width,
            	height:550,
            	store: obisCodeStore,
            	columns: obisCodeModel,
            	autoScroll: false,
            	stripeRows: true,
            	columnLines: true,
            	loadMask: {
            		msg: 'loading...'
            	},
            	renderTo: 'obisCodeListDiv',
            	viewConfig: {
            		forceFir: true,
            		enableRowBody: true,
            		showPreview: true,
            		emptyText: "<fmt:message key='aimir.extjs.empty'/>"
            	},
                bbar : new Ext.PagingToolbar({
                    pageSize : 20,
                    store : obisCodeStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            
        },
        checkDuplicate: function(recData) {
        	var flag = false;
        	if(DeviceModelSelectId != null && recData.obisCode != null && recData.classId != null  && recData.attributeNo
        			&& DeviceModelSelectId != "" && recData.obisCode != "" && recData.classId != "" && recData.attributeNo != "") {
        		$.ajax({
                    url: '${ctx}/gadget/system/checkDuplicate.do',
                    type: 'POST',
                    dataType: 'json',
                    data : {
                    	'modelId': DeviceModelSelectId,
                    	'obisCode': recData.obisCode,
            			'classId': recData.classId,
            			'attributeNo': recData.attributeNo,
            			'accessRight' : recData.accessRight
                    },
                    async: false,  
                    success: function(data) {
                    	if(data.result=='duplicate') {
                    		Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "<fmt:message key='aimir.duplicateerror'/>",
            	    				function() {
                    			flag = false;
            	    		});
                    	} else {
                    		flag = true;
                    	}
                   	}
              		});
        	}
        	return flag;
        },
        deleteObis: function(rec, isDel) {
        	Ext.Msg.show({
	              title: "<fmt:message key='aimir.warning'/>",
	              msg: "<fmt:message key='aimir.msg.deleteconfirm'/>",
	              buttons: Ext.MessageBox.YESNO,
                  fn: function(btn) {
                      if(btn == 'yes') {
                    	  $.post('${ctx}/gadget/system/deleteObisCode.do',{
                      		obisCodeId : rec.data.ID,
                      		modelId : DeviceModelSelectId,
                      		isDel  : isDel
                          }, function(json) {
                          	if(json.result == 'success') {
                          		Ext.Msg.show({
              			              title: "<fmt:message key='aimir.info'/>",
              			              msg: "<fmt:message key='aimir.success'/>",
              			              buttons: Ext.MessageBox.OK
              			            }); 
                          	}else {
                          		Ext.Msg.show({
              			              title: "<fmt:message key='aimir.info'/>",
              			              msg: "<fmt:message key='aimir.fail'/>",
              			              buttons: Ext.MessageBox.OK
              			            }); 
                          	}
                          	obisMgmtHandler.drawObisCodeGrid();
                          });
                      }

                  }
	            }); 
        	
        },
        updateObis: function(rec) {
        	var flag = true;
        	var obisCode = rec.data.OBISCODE;
        	if(obisCode != null && obisCode !="" && 
						((obisCode.split(".").length != 6) || (obisCode.split(".").length == 6 && obisCode.split(".")[5] == ""))) {
				Ext.Msg.alert("<fmt:message key='aimir.warning'/>","<fmt:message key='aimir.metering.MeteringFormatError'/>\nx.x.x.x.x.x");
				flag = false;
			}
        	var updateRow = obisCodeGrid.selModel.selection.cell[0];
			if(flag) {
				flag = obisMgmtHandler.validateObisM(obisCodeGrid,updateRow,obisCode,"<fmt:message key='aimir.mandatoryValue'/>",0);
			}
        	
        	if(flag) {
        		flag = obisMgmtHandler.validateObisM(obisCodeGrid,updateRow,rec.data.CLASSID,"<fmt:message key='aimir.mandatoryValue'/>",2);
        	}
        	
        	if(flag) {
        		flag = obisMgmtHandler.validateObisM(obisCodeGrid,updateRow,rec.data.ATTRIBUTENO,"<fmt:message key='aimir.mandatoryValue'/>",4);
        	}
        	
        	if(rec.data.METEREVENT == 'Y' && flag) {
        		flag = obisMgmtHandler.validateObisM(obisCodeGrid,updateRow,rec.data.DESCR,"<fmt:message key='aimir.mandatoryValue'/>",7);
        	}
        	
        	if(flag) {
        		var data = {
            			obisCode: obisCode,
            			classId: rec.data.CLASSID,
            			attributeNo: rec.data.ATTRIBUTENO,
            			accessRight: rec.data.ACCESSRIGHT
            	}
            	if(rec.json.OBISCODE != data.obisCode || rec.json.CLASSID != data.classId || rec.json.ATTRIBUTENO != data.attributeNo || rec.json.ACCESSRIGHT != data.accessRight) {
            		flag = obisMgmtHandler.checkDuplicate(data);
            		if(!flag) {
            			rec.store.rejectChanges();
            		}
            	}
            	if(flag) {
            		rec.data.METERTYPE=$('#deviceType').val();
                	
                	Ext.Msg.show({
        	              title: "<fmt:message key='aimir.warning'/>",
        	              msg: "<fmt:message key='aimir.update.want'/>",
        	              buttons: Ext.MessageBox.YESNO,
                      fn: function(btn) {
                          if(btn == 'yes') {
                        	  $.post('${ctx}/gadget/system/updateObisCode.do',{
                      			updateObisArr: JSON.stringify(rec.data),
                      			modelId : DeviceModelSelectId,
                          		roleId : roleId
                              }, function(json) {
                              	if(json.result == 'success') {
                              		Ext.Msg.alert("<fmt:message key='aimir.info'/>","<fmt:message key='aimir.success'/>");
                              	} else if(json.result == 'meterEventFail') {
                              		Ext.Msg.alert("<fmt:message key='aimir.info'/>","ObisCode information is updated. but MeterEvent update fail.");
                              	} else {
                              		Ext.Msg.alert("<fmt:message key='aimir.info'/>","<fmt:message key='aimir.fail'/>");
                  	          	}
                              	obisMgmtHandler.drawObisCodeGrid();
                              });
                          }
                      }
              		})
            	}
        	}
    		
        },
        addRecordData: function() {
        	$.ajaxSetup({
                async : false
            });
			var flag = true;
			
			var store = addGrid.getStore();
			
        	if(store.data.length > 0) {
	        	var preRecord = store.data.last().data;
                
	        	flag = obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,preRecord.obisCode,"<fmt:message key='aimir.mandatoryValue'/>",0);
                
                if(flag) {
                	obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,preRecord.classId,"<fmt:message key='aimir.mandatoryValue'/>",2);
                }

                if(flag) {
                	obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,preRecord.attributeNo,"<fmt:message key='aimir.mandatoryValue'/>",4);
                }
                
	        	if(flag && preRecord.meterEvent == 'Y') {
	        		flag = obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,preRecord.descr, "<fmt:message key='aimir.need.descr'/>", 7);
	        	}
	        	
	        	if(flag) {
	        		flag = obisMgmtHandler.checkDuplicate(preRecord);
	        	}
	        	
        	}

        	if(flag) {
                var Plant = store.recordType;
	            var p = new Plant({
	                obisCode : "",
	                className : "",
	                classId : "",
	                attributeName : "",
	                attributeNo : "",
	                dataType : "",
	                accessRight : "-",
	                descr : "",
	                meterEvent : "N"
	            });
	            var length = store.getCount();
	            addGrid.stopEditing();
	            addObisStore.insert(length, p);
	            addGrid.startEditing(length, 0);
	            addGrid.getSelectionModel().selectLastRow();
        	}
        },
        delRecordData: function() {
            addGrid.stopEditing();
            var s = addGrid.getSelectionModel().selection.record
            addObisStore.remove(s);  
        },
        validateObisM: function(grid,rec,data,msg,row) {
        	if(data == null || data == "") {
	        	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", msg,
	    				function() {
	    			grid.startEditing(rec, row);
	        		return false;
	    		});
        	} else {
        		return true;
        	}
        },
        addObisRow: function() {
      	  $.ajaxSetup({
              async : false
          });
    	
    	  $('#addObisCmp').html('');
    	  
    	  var width = $('#addObisCmp').width()-30;

    	  addObisStore = new Ext.data.ArrayStore({
  			  fields: ['obisCode','className','classId','attributeName','attributeNo','dataType','accessRight','descr']
  		  });

          addObisModel = new Ext.grid.ColumnModel({
          	columns:[
          		{header: "<span style='color:red'>* <fmt:message key='aimir.model.obisCode'/></span>", tooltip:"<fmt:message key='aimir.model.obisCode'/>", dataIndex: 'obisCode', 
          			editor: new Ext.form.TextField({
                        id : 'addObisCode'
                    })
          		},
          		{header: "<fmt:message key='aimir.model.className'/>", tooltip:"<fmt:message key='aimir.model.className'/>", dataIndex: 'className',
          			editor: new Ext.form.TextField({
                        id : 'addClassName'
                    })
          		},
          		{header: "<span style='color:red'>* <fmt:message key='aimir.model.classId'/></span>", tooltip:"<fmt:message key='aimir.model.classId'/>", dataIndex: 'classId',
          			editor: new Ext.form.TextField({
                        id : 'addClassId'
                    })
          		},
          		{header: "<fmt:message key='aimir.model.attributeName'/>", tooltip:"<fmt:message key='aimir.model.attributeName'/>", dataIndex: 'attributeName',
          			editor: new Ext.form.TextField({
                        id : 'addAttrName'
                    })
          		},
          		{header: "<span style='color:red'>* <fmt:message key='aimir.model.attributeNo'/>/Method No</span>", tooltip:"<fmt:message key='aimir.model.attributeNo'/>/Method No", dataIndex: 'attributeNo',
          			editor: new Ext.form.TextField({
                        id : 'addAttrNo'
                    })
          		},
          		{header: "<fmt:message key='aimir.model.dataType'/>", tooltip:"<fmt:message key='aimir.model.dataType'/>", dataIndex: 'dataType',
          			editor: dataTypeCombo, renderer: Ext.util.Format.comboRenderer(dataTypeCombo)
          		},
          		{header: "<span style='color:blue'>* <fmt:message key='aimir.model.access'/></span>", tooltip:"<fmt:message key='aimir.model.access'/>", dataIndex: 'accessRight',
          			editor: accessCombo, renderer: Ext.util.Format.comboRenderer(accessCombo)
          		},
          		{header: "<fmt:message key='aimir.model.descr'/>", tooltip:"<fmt:message key='aimir.model.descr'/>", dataIndex: 'descr',
          			editor: new Ext.form.TextField({
                        id : 'addDescr'
                    })
          		},
          		{header: "<fmt:message key='aimir.event'/>", tooltip:"<fmt:message key='aimir.event'/>", dataIndex: 'meterEvent',
          			editor: meterEventCombo, renderer: Ext.util.Format.comboRenderer(meterEventCombo)
          		},
          	],
          	defaults:{
          		sortable: true,
          		menuDisabled:true,
          		editable: editSetting,
          		renderer: addTooltip
          	}
          });
		  
		  addGrid = new Ext.grid.EditorGridPanel({
	   			 store: addObisStore,
	   			 autoScroll : true,
	   			 loadMask: true,
	   			 colModel: addObisModel,
	   			 listeners: {
	   				afteredit: function(e) {
	   					var data = e.record.data;
	   					var obisCode = data.obisCode;
	   					if(obisCode != null && obisCode !="" && 
	   							((obisCode.split(".").length != 6) || (obisCode.split(".").length == 6 && obisCode.split(".")[5] == ""))) {
	   						Ext.Msg.alert("<fmt:message key='aimir.warning'/>","<fmt:message key='aimir.metering.MeteringFormatError'/>\nx.x.x.x.x.x");
	   						addGrid.startEditing(e.row, 0);
	   						e.record.reject();
	   						return false;
	   					}
	   					
	   					if(addGrid.store.data.length != 0 && (addGrid.store.data.length-1 != addGrid.lastEdit.row)) {
	   			        	flag = obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,data.obisCode,"<fmt:message key='aimir.mandatoryValue'/>",0);
	   		                if(flag) {
	   		                	obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,data.classId,"<fmt:message key='aimir.mandatoryValue'/>",2);
	   		                } else {
	   		                	e.record.reject();	
	   		                }

	   		                if(flag) {
	   		                	obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,data.attributeNo,"<fmt:message key='aimir.mandatoryValue'/>",4);
	   		                } else {
	   		                	e.record.reject();
	   		                }
	   		                
	   			        	if(flag && data.meterEvent == 'Y') {
	   			        		flag = obisMgmtHandler.validateObisM(addGrid,addGrid.lastEdit.row,data.descr, "<fmt:message key='aimir.need.descr'/>", 7);
	   			        	}
	   			        	
	   			        	if(flag) {
	   			        		flag = obisMgmtHandler.checkDuplicate(data);
	   			        		if(!flag) {
	   			        			e.record.reject();
	   			        		}
	   			        	}
	   					}
	   					
	   					//modify된 내용이 저장되지 않아 강제로 저장.
	   					if(e.column == 0) {
	   						e.record.modified.obisCode = e.value;
	   					} else if(e.column == 1) {
	   						e.record.modified.className = e.value;
	   					} else if(e.column == 2) {
	   						e.record.modified.classId = e.value;
	   					} else if(e.column == 3) {
	   						e.record.modified.attributeName = e.value;
	   					} else if(e.column == 4) {
	   						e.record.modified.attributeNo = e.value;
	   					} else if(e.column == 5) {
	   						e.record.modified.dataType = e.value;
	   					} else if(e.column == 6) {
	   						e.record.modified.accessRight = e.value;
	   					} else if(e.column == 7) {
	   						e.record.modified.descr = e.value;
	   					} else if(e.column == 8) {
	   						e.record.modified.meterEvent = e.value;
	   					}
	   					
	   				}
	   			 },
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: width,
	  		      height: 300,
	  		      tbar:[{
	  		    	  iconCls: 'icon-obis-add',
	  		    	  text: "<b><fmt:message key='aimir.add.obis'/></b>",
	  		    	  handler: function() {
	  		    		obisMgmtHandler.addRecordData();
	  		    	  }
	  		      },{
	  		    	  iconCls: 'icon-obis-delete',
	  		    	  text: "<b><fmt:message key='aimir.delObis'/></b>",
	  		    	  handler: function() {
	  		    		obisMgmtHandler.delRecordData();
	  		    	  }
	  		      }]
	   		  });
        	

        	var width = $('#obisCodeMgmt').width();
            
        	
        	var addRowWin = new Ext.Window({
        		title: "<fmt:message key='aimir.add.obis'/>",
        		id: 'addObisCmp',
        		applyTo: 'addObisCmp',
        		width:width-20, 
                shadow : false,
                autoHeight: true,
                //clicksToEdit : 1,
                pageX : 460,
                pageY : 130, 
                plain: true,
                items: [addGrid],
                buttons : [{text : '<fmt:message key="aimir.save2"/>',
		            	handler : function() {	
		            		
		            		var records = addObisStore.data.items;
		            		var flag = false;
		            		
		            		//마지막라인에 대해서 유효성 체크
		            		if(records.length-1 < 0) {
		            			Ext.Msg.alert("<fmt:message key='aimir.error'/>","<fmt:message key='aimir.data.empty'/>");
		            			flag = false;
		            			return false;
		            		}
		            		var lastRecord = records[records.length-1].data;
		            		
		            		flag = obisMgmtHandler.validateObisM(addGrid,records.length-1,lastRecord.obisCode,"<fmt:message key='aimir.mandatoryValue'/>",0);

	        	        	if(flag) {
	        	        		flag = obisMgmtHandler.validateObisM(addGrid,records.length-1,lastRecord.classId, "<fmt:message key='aimir.mandatoryValue'/>", 2);
	        	        	}
	        	        	
	        	        	if(flag) {
	        	        		flag = obisMgmtHandler.validateObisM(addGrid,records.length-1,lastRecord.attributeNo, "<fmt:message key='aimir.mandatoryValue'/>", 4);
	        	        	}
	        	        	
	        	        	if(flag && lastRecord.meterEvent == 'Y') {
	        	        		flag = obisMgmtHandler.validateObisM(addGrid,records.length-1,lastRecord.descr, "<fmt:message key='aimir.need.descr'/>", 7);
	        	        	}
	        	        	
	        	        	if(flag) {
	        	        		flag=obisMgmtHandler.checkDuplicate(lastRecord);
	        	        	}

	        	        	if(flag) {
	        	        		$.ajaxSetup({
	                                async : false
	                            });
	        	        		var saveArrList = new Array();
			            		for(var i = 0; i<records.length; i++) {
			            			var saveArr = new Array();
			            			saveArr.push({
			            				obisCode : records[i].data.obisCode,
					                    className : records[i].data.className,
					                    classId : records[i].data.classId,
					                    attributeName : records[i].data.attributeName,
					                    attributeNo : records[i].data.attributeNo,
					                    dataType : records[i].data.dataType,
					                    accessRight : records[i].data.accessRight,
					                    descr : records[i].data.descr,
					                    meterEvent : records[i].data.meterEvent,
					                    modelId : DeviceModelSelectId,
					                    meterType : $('#deviceType').val()
				            		})
				            		
				            		saveArrList.push(saveArr);
			            		}
			            		Ext.Msg.wait('Waiting for response.', 'Wait !');			            		
			            		$.post('${ctx}/gadget/system/saveObisCodes.do',{
			            			saveObisArr : JSON.stringify(saveArrList)
			                    }, function(json) {
			                    	$.ajaxSetup({
		                                async : true
		                            });
			                    	Ext.Msg.hide();
			                    	if(json.result == 'success') {
			                    		Ext.Msg.show({
								              title: "<fmt:message key='aimir.info'/>",
								              msg: "<fmt:message key='aimir.save'/>",
								              buttons: Ext.MessageBox.OK,
								              fn : function(btn) {
								            	  addObisStore.clearData();
								            	  addGrid.reconfigure(addObisStore,addObisModel);
								            	  obisMgmtHandler.searchObis();
								              }
								            }); 
			                    		
			                    	} else if(json.result == 'eventSavefail') {
			                    		Ext.Msg.show({
								              title: "<fmt:message key='aimir.error'/>",
								              msg: "ObisCode save is success but meter event save $일부 or 전체 미터이벤트 저장 도중 오류가 발생했습니다.",
								              buttons: Ext.MessageBox.OK,
								              fn : function(btn) {
								            	  addObisStore.clearData();
								            	  addGrid.reconfigure(addObisStore,addObisModel);
								            	  obisMgmtHandler.searchObis();
								              }
								            }); 
			                    	} else {
			                    		Ext.Msg.show({
								              title: "<fmt:message key='aimir.error'/>",
								              msg: "<fmt:message key='aimir.hems.error.drMgmt'/>",
								              buttons: Ext.MessageBox.OK
								            }); 
			                    	}
			                    });
	        	        	}
	                	}},
	                	{text : '<fmt:message key="aimir.board.close"/>',
	                	handler : function() {
	                		
	                		addRowWin.hide(this);        		
	                	}}],
                closeAction:'hide',	                
                onHide : function(){
                }       
        	});
        	addRowWin.show();
        },
        searchObis: function() {
        	obisMgmtHandler.drawObisCodeGrid();
        }

    }
    
    var grid;
    var ajaxSuccessCount = 0; // ajaxQueue의 요청이 완료된 count
    var mmiuDataList = new Array();
    var meterSettingHandler = {
    	changeMeterType: function() {
    		var selectMeterFlag =$('input[name="selectMeter"]:checked').val(); 
    		if(selectMeterFlag == 1) {
    			$('#meterGroupDiv').show();
    			$('#meterDiv').hide();
    			$('#meterSetId').val('');
    		} else {
    			$('#meterDiv').show();
    			$('#meterGroupDiv').hide();
    			$('#meterGroup option:first').attr("selected", "selected");
    			document.getElementById("meterGroup_input").value = '-';
    		}
    	},
    	changeCommand: function() {
    		var obisCodeId = $('#obisCmdList').val().split(",");
    		if(obisCodeId[0] == null || obisCodeId.length < 1 || obisCodeId[1] == null || obisCodeId[1].length < 1) {
    			$('#classNameMS').val('');
    			parameterGrid.store.clearData()
    			saveDetailParamArrList = new Array();
    		} else {
    			$.post("${ctx}/gadget/system/getObisCode.do",{
        			modelId: DeviceModelSelectId,
        			obisCode: obisCodeId[0],
        			classId : obisCodeId[1]
        		},function(json) {
        			var obisInfo = json.result[0];
        			
        			$('#classNameMS').val(obisInfo.CLASSNAME);

        			meterSettingHandler.drawParameter(json.result);
        			
        			saveDetailParamArrList = new Array();
        		});
    		}
    		
    		
    	},
    	changeAttr: function() {
    		var recordId = $('input[name="attrRadio"]:checked').val();
    		selectAttribute = parameterGrid.store.getById(recordId).data;
    	},
    	drawParameter: function(attributeList) {
    		$.ajaxSetup({
                async : false
            });
    		
    		var width = $('#parameterSetDiv').width();
    		
    		var checkSelModel = new Ext.grid.CheckboxSelectionModel({
                checkOnly:true
             });
    		
    		var parameterStore = new Ext.data.JsonStore({
    			data : attributeList,
                fields: ['ID','OBISCODE','CLASSNAME','CLASSID','ATTRIBUTENAME','ATTRIBUTENO','DATATYPE','ACCESSRIGHT','VALUE']
            });
    		
            var columns = [];
            
            columns.push({header:'',stopSelection:false,width:20, dataIndex:'ID',
            	renderer: function(value,meta,record) {
            		selectAttribute = record;
            		return "<center><input type='radio' name='attrRadio' id='attrRadio_"+record.id+"' value="+ record.id +" onClick='javascript:meterSettingHandler.changeAttr()' ></center>"
            	}
            	})
            
            columns.push({header: "<fmt:message key='aimir.model.attributeNo'/>/Method No", tooltip:"<fmt:message key='aimir.model.attributeNo'/>/Method No", 
    		 			dataIndex: 'ATTRIBUTENO', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 - 20});
            
            columns.push({header: "<fmt:message key='aimir.model.attributeName'/>", tooltip:"<fmt:message key='aimir.model.attributeName'/>", 
                    dataIndex: 'ATTRIBUTENAME', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 -5});
            
            columns.push({header: "<fmt:message key='aimir.model.dataType'/>", tooltip:"<fmt:message key='aimir.model.dataType'/>",
                dataIndex: 'DATATYPE', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 -5});

            columns.push({header: "<fmt:message key='aimir.model.access'/> ",tooltip:"<fmt:message key='aimir.model.access'/>",
                dataIndex: 'ACCESSRIGHT', sortable: true, menuDisabled: true, width:width/5 -5});
            
            columns.push({header: "<fmt:message key='aimir.value'/>", tooltip:"<fmt:message key='aimir.value'/>", 
                dataIndex: 'VALUE', sortable: true, menuDisabled: true, renderer: meterSettingHandler.detailBtn, width:width/5,
                editor: meterSettingHandler.editFunction});
            
            var parameterModel=columns;

            $('#parameterSetDiv').empty();
            
            parameterGrid = new Ext.grid.EditorGridPanel({
            	layout: 'fit',
            	width: width,
            	height:400,
            	store: parameterStore,
            	columns: parameterModel,
            	autoScroll: false,
            	stripeRows: true,
            	columnLines: true,
            	loadMask: {
            		msg: 'loading...'
            	},
            	renderTo: 'parameterSetDiv',
            	viewConfig: {
            		forceFir: true,
            		enableRowBody: true,
            		showPreview: true,
            		emptyText: "<fmt:message key='aimir.extjs.empty'/>"
            	},
                bbar : new Ext.PagingToolbar({
                    pageSize : 20,
                    store : parameterStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
    	},
    	editFunction: function(value,meta,rec) {
    		if(rec.data.DATATYPE != "array") {
        		return new Ext.Editor(new Ext.form.TextField({id: 'value'}));
        	} else {
        		return '';
        	}
    	},
    	detailBtn: function(value,meta,rec) {
        		var id = Ext.id();
            	var $div = $("<div></div>").attr("id", id);
            	var button = function() {
            		if($("#" + id).length > 0 && ($div.children().length<1)) {
            			new Ext.Button({
            				text: "<fmt:message key='aimir.view.detail'/>",
            				width: 40,
            				handler: function(b, e) {
            					saveDetailParamArrList = new Array();
            					dlmsGuideFunction(rec);
            					//rec.json.VALUE = rec.data.VALUE;
            				}				
            			}).render(id);
            		} else {
            			button.defer(100);
            		}
            	};

            	button.defer(100);
            	return $div[0].outerHTML;
        },
    	cmdObis: function(cmd) {
    		//비동기 설정
            $.ajaxSetup({
                async : true
            });
    		
    		if( $(":input:radio[name='attrRadio']:checked").val() == null) {
    			return Ext.Msg.alert("Warning", "Please select attribute.",
	    				function() { return false;});
    		}
    		
    		if(detailCheck) {
    			var paramArr = new Array();
       			paramArr.push({
    					'ACCESSRIGHT' : selectAttribute.ACCESSRIGHT,
    					'ATTRIBUTENAME' : selectAttribute.ATTRIBUTENAME,
    					'ATTRIBUTENO' : selectAttribute.ATTRIBUTENO,
    					'CLASSID' : selectAttribute.CLASSID,
    					'CLASSNAME' : selectAttribute.CLASSNAME,
    					'DATATYPE' : selectAttribute.DATATYPE,
    					'OBISCODE' : selectAttribute.OBISCODE,
    					'VALUE' : selectAttribute.VALUE
    				});
       			detailCheck = false;
    			selectAttribute.VALUE='';

        		if(($('#meterSetId').val() != undefined && $('#meterSetId').val() != '') || ($('#meterGroup').val() != undefined && $('#meterGroup').val() != '')) {
        			meterSettingHandler.groupGetSetService(cmd,paramArr);
        		} else {
        			return Ext.Msg.alert("Warning", "Please select Meter",
    	    				function() { return false;});
        		}
    		} else {
    			return Ext.Msg.alert("Warning", "Please click [Detail] button for attribute's parameter.",
	    				function() { return false;});
    		}
    		
    	},
    	checkCmdGetLog : function(cmd, obisCode, classId, attrId){
    		var getLogObisList = [['0.0.99.98.0.255','7','2', 'cmdMeterParamGet', 'cmdGetStandardEventLog'],
                                  ['0.0.99.98.1.255', '7','2','cmdMeterParamGet', 'cmdGetTamperingLog' ],
        					      ['1.0.99.97.0.255', '7','2','cmdMeterParamGet', 'cmdGetPowerFailureLog' ],
        					      ['0.0.99.98.2.255', '7','2','cmdMeterParamGet', 'cmdGetControlLog' ],
        					      ['0.0.99.98.3.255', '7','2','cmdMeterParamGet', 'cmdGetPQLog' ],
        					      ['0.0.99.98.4.255', '7','2','cmdMeterParamGet', 'cmdGetFWUpgradeLog' ]
            ];
    		var cmdName = "";
    		for ( var i = 0; i < getLogObisList.length; i++){
    			if ( obisCode == getLogObisList[i][0] && 
    					classId == getLogObisList[i][1] &&
    					attrId == getLogObisList[i][2] &&
    					cmd == getLogObisList[i][3] )
    			{
    				cmdName = getLogObisList[i][4];
    				break;
    			}
    		}
			return cmdName;    		
    	},
    	cmdGo : function(cmd, paramArr, meterArray) {
    		mmiuDataList = new Array();
    		ajaxSuccessCount = 0;//요청 완료시 counting된다.
            //비동기 방식으로 요청한다.
            $.ajaxSetup({
                async : true
            });

            //처음 항목에 loading 이미지 추가
            grid.store.getAt(0).set('rtnStr',
                    'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

    		var fromDate = "";
    		var toDate = "";
    		var paramdata = {};
    		var getLogCmdName = "";
    		var isGetLogCmd = false;
    		var url = '${ctx}/gadget/device/command/dlmsGetSet.do';

    		if ( typeof(paramArr[0]) != 'undefined' && 
    				(getLogCmdName =  meterSettingHandler.checkCmdGetLog (cmd, paramArr[0].OBISCODE, paramArr[0].CLASSID, paramArr[0].ATTRIBUTENO )) != ""){
				isGetLogCmd = true;
    			
    			var tval = paramArr[0].VALUE[0];
        		if ( typeof(tval) != 'undefined') {
        			fromDate = tval.fYear + tval.fMonth+ tval.fDayOfMonth + tval.fHh + tval.fMm + tval.fSs;
        		 	toDate = tval.tYear + tval.tMonth+ tval.tDayOfMonth + tval.tHh + tval.tMm + tval.tSs;
        		}
    		}
            
            for (var i = 0; i < meterArray.length; i++) {
            	if ( isGetLogCmd ){
        			paramdata = {
                        	'cmd' : getLogCmdName,
        					'mdsId' : meterArray[i].meterId,
        					'fromDate' : fromDate,
        					'toDate' : toDate,
        					'modelName' :  $("#modelForm :input[name='modelName']").val()
                        };
        			url = '${ctx}/gadget/device/command/dlmsGetLog.do';
            	}
            	else {
            		url = '${ctx}/gadget/device/command/dlmsGetSet.do';
            		paramdata =  {
                        	'cmd' : cmd,
        					'parameter' : JSON.stringify(paramArr),
        					'mdsId' : meterArray[i].meterId,
        					'modelName' :  $("#modelForm :input[name='modelName']").val()
                        };
            	}
                //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
               var queueName = $.ajaxQueue({
                    type : "GET",
                    url : url,
                    data : paramdata ,
                    success : function(returnData) {
                    	saveDetailParamArrList = new Array();
                        var i = ajaxSuccessCount;
                        grid.getView().focusRow(i);
                        var record = grid.store.getAt(i);
                        if (returnData.rtnStrList[0].rtnStr == 'Next Step Processing...') {
                        	grid.store.getAt(ajaxSuccessCount).set('rtnStr',
                            'Next Step Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                        	var mmiuData = {
                        			meterId : returnData.rtnStrList[0].meterId,
                        			trId : returnData.rtnStrList[0].trId,
                        			recordId : i
                        	}
                        	mmiuDataList.push(mmiuData); 
                        }else {
                        	record.set('rtnStr', returnData.rtnStrList[0].rtnStr);
                        	if(returnData.rtnStrList[0].rtnStr.indexOf("FAIL") < 0) {
                        		record.set('detail',
                                        "<a href='#' onclick='meterSettingHandler.successResult(" + JSON.stringify(returnData.rtnStrList[0].viewMsg) + ");' class='btn_blue'><span>Detail</span></a>");							
                        	}
                            
                        }
                        ajaxSuccessCount++;
                        if (meterArray.length != ajaxSuccessCount)
                            grid.store.getAt(ajaxSuccessCount).set('status','Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

                        if (window.ajaxQueueCount[queueName] == 1) {// 맨 마지막 동작일때
                        	if(mmiuDataList.length > 0) {
                        		$.post('${ctx}/gadget/device/command/getAsyncLog.do',{
                            		meterInfoArr : JSON.stringify(mmiuDataList)
    		                    }, function(json) {
    		                    	record = grid.store.getAt(json.rtnStrList[0].recordId);
    		                    	record.set('rtnStr', json.rtnStrList[0].rtnStr);
    		                    	if(json.rtnStrList[0].rtnStr.indexOf("FAIL") < 0) {
    		                    		record.set('detail',
    		                                    "<a href='#' onclick='meterSettingHandler.successResult(" + JSON.stringify(json.rtnStrList[0].viewMsg) + ");' class='btn_blue'><span>Detail</span></a>");
    		                    	}
    	                            
    		                    });
                        	}
                        	
                            $.ajaxSetup({
                                async : true
                            });
                        }
                    }
                });
            }
    	},
    	groupGetSetService: function(cmd,paramArr) {
    		$.ajaxSetup({
                cache : false
            });
            //해당 그룹 미터정보 얻기
            var meterArray = new Array();
            
            var selectMeterFlag =$('input[name="selectMeter"]:checked').val(); 
    		if(selectMeterFlag == 1 && ($('#meterGroup').val() != undefined && $('#meterGroup').val() != '')) {
    			meterArray = meterSettingHandler.groupService();
    		} else if(selectMeterFlag == 0 && $('#meterSetId').val() != undefined && $('#meterSetId').val() != '' ){
    			var tempArr = new Array();
    			tempArr[0] = $('#meterSetId').val();
    			meterArray.push(tempArr); 
    		}
    		
            //그리드, 윈도우 높이 구하기
            this.gridH = 100 + Number(meterArray.length * 25);
            this.winH = 200 + Number(meterArray.length * 25);
            if (this.gridH > 600)
                this.gridH = 600;
            if (this.winH > 700)
                this.winH = 700;

            var array = new Array();
            //그리드 데이터 생성
            if (meterArray != null) {
                count = meterArray.length;
                for (var i = 0; i < count; i++) {
                    var gridData = meterArray[i];
                    var arrayData = {'meterId' : gridData[0],
                    				'rtnStr' : 'Processing...'}
                    	
                    array[i] = arrayData;
                }

                meterSettingHandler.makeAlertWindow(array,cmd,paramArr);
            } else {
                if (imgWin != undefined) {
                    Ext.getCmp('drAlertWinId').hide();
                }
            }

            $.ajaxSetup({
                cache : true
            });
    	},
    	groupService: function() {
    		var meterIdList = new Array();

            var params = {
            		meterGroupId : $('#meterGroup').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/system/getGroupMemberList.do",
                data: params,
                async: false
            }).responseText;

            eval("result=" + jsonText);

            var meterList = result.meterList;
			var meterIdList = new Array();
            if (meterList.length > 0) {
                for (var i = 0 ; i < meterList.length; i++) {
                    meterIdList[i] = [meterList[i]];
                }
            }

            return meterIdList;
    	},
    	makeAlertWindow: function(rtnStrList,cmd,paramArr) {
           var store = new Ext.data.JsonStore({
                    fields : [ {name:'meterId'},{name:'rtnStr'}]
                });
           store.loadData(rtnStrList);
           var colModel = new Ext.grid.ColumnModel({
                defaults : {
                    width : 100,
                    height : 100,
                    sortable : true
                },
                columns : [{
                    id : "meterId",
                    width : 150,
                    header : "Meter ID",
                    dataIndex : "meterId",
                    renderer: addTooltip
                }, {
                    header : "Status",
                    width : 200,
                    dataIndex : "rtnStr",
                    renderer: addTooltip
                }, {
                    header : "Result",
                    width : 60,
                    dataIndex : "detail"
                }]
            });
            grid = new Ext.grid.GridPanel({
                   height : 300,
                   store : store,
                   colModel : colModel,
                   width : 420
               });items : grid,

            $('#resultDiv').empty();
            
            var imgWin = new Ext.Window({
                title : 'Result',
                id : 'resultDivWinId',
                applyTo : 'resultDiv',
                autoScroll : true,
                autoHeight : true,
                pageX : 450,
                pageY : 130,
                width : 430,
                height : 300,
                items : grid,
                closeAction : 'hide',
                onHide : function() {
                }
            });
            Ext.getCmp('resultDivWinId').show();
            setTimeout(function() {meterSettingHandler.cmdGo(cmd,paramArr,rtnStrList);}, 100);
    	},
    	successResult: function(viewMsg) {
    		//성공 윈도우
    		var store = new Ext.data.JsonStore({
                fields: ['paramType','paramValue']
            });
    		store.loadData(viewMsg);
    		
            var colModel = new Ext.grid.ColumnModel({
                defaults : {
                    sortable : true
                },
                columns : [{
                    id : "name",
                    width : 150,
                    header : "name",
                    dataIndex : "paramType",
                    renderer: addTooltip
                }, {
                	id : "value",
                    header : "value",
                    width : 330,
                    dataIndex : "paramValue",
                    renderer: addTooltip
                }]
            });
    		
            var resultGrid = new Ext.grid.GridPanel({
                height : 300,
                store : store,
                colModel : colModel,
                width : 500
            });

            $('#resultDetailDiv').empty();

            var imgWin2 = new Ext.Window({
                title : 'Success Result',
                id : 'resultDetailDivWinId',
                applyTo : 'resultDetailDiv',
                autoScroll : true,
                pageX : 490,
                pageY : 150,
                width : 530,
                height : 300,
                items : resultGrid,
                closeAction : 'hide',
                onHide : function() {
                }
            });
            Ext.getCmp('resultDetailDivWinId').show();
    	}
    }
    
    /*]]>*/
    </script>
</head>
<body>
<input type="hidden" id="deviceTreeId" />

<!-- Supplier Lists (S) -->
<div class="search-bg-basic">
<ul class="basic-ul">
    <li class="basic-li graybold11pt withinput" id="supplierName"></li>
    <li class="basic-li"><select id="supplierList" name="supplierList"></select></li>
</ul>
</div>
<!-- Supplier Lists (E) -->

<!-- Gadget Body (S) -->
<div class="gadget_body">


    <!-- 제조사추가, 장비추가 버튼 -->
    <div id="addBtn" class="btn btn-devicemodel">
        <ul><li><input type="button" id="viewVendorForm" value="<fmt:message key='aimir.vendor.add' />" /></li></ul>
        <ul><li><input type="button" id="viewModelForm" value="<fmt:message key='aimir.equip.add'/>" /></li></ul>
    </div>

    <div id="container-0">
        <div class="searchoption select-treetype">
            <ul>
                <li><input class="trans" name="treeType" type="radio" value="V"></li>
                <li class="blue11pt withinput"><fmt:message key="aimir.sortby.equip" /></li>
                <li><input class="trans" name="treeType" type="radio" value="M"></li>
                <li class="blue11pt withinput"><fmt:message key="aimir.sortby.vendor" /></li>
            </ul>
        </div>
    </div>


    <div class="bodyright_devicemodel">
        <ul>
            <li class="bodyright_devicemodel_leftmargin">

                <div class="container-all border-blue">

                    <!-- 제조사추가 Tab (S) -->
                    <div id="container-3">
                        <ul>
                            <li><a href="#fragment-1"><fmt:message key="aimir.vendor.info" /></a></li>
                        </ul>

                        <div id="fragment-1">
                            <form id="vendorForm"><input type="hidden" name="id" /> <input type="hidden" name="supplierId" />

                            <ul>
                                <label><fmt:message key="aimir.vendor" /></label>
                                <li><input type="text" name="name" maxlength="80" /></li>
                            </ul>
                            <ul>
                                <label><fmt:message key="aimir.equip.privatecode" /></label>
                                <li><input type="text" name="code" onkeyup="removeCommaForInt(event, this);" onfocus="removeCommaForInt(event, this);" onblur="removeCommaForInt(event, this);"/></li>
                            </ul>
                            <ul>
                                <label><fmt:message key="aimir.address" /></label>
                                <li><input type="text" name="address" /></li>
                            </ul>
                            <ul>
                                <label><fmt:message key="aimir.description" /></label>
                                <li><textarea type="text" name="descr" maxlength="80" /></textarea></li>
                            </ul>

                            <div id="btnList1">
                                <div id="btn" class="right">
                                    <a href="#" id="add1" class="btn_blue"><span><fmt:message key="aimir.button.register" /></span></a>
                                    <a href="#" id="update1" class="btn_blue"><span><fmt:message key="aimir.update" /></span></a>
                                    <a href="#" id="delete1" class="btn_blue"><span><fmt:message key="aimir.button.delete" /></span></a>&nbsp;
                                </div>
                            </div>
                            </form>
                        </div>
                    </div>
                    <!-- 제조사추가 Tab (E) -->


                    <!-- 장비추가 Tab (S) -->
                    <div id="container-4">
                        <ul>
                            <li><a href="#fragment-2" id="modelinfoTab"><fmt:message key="aimir.model.info" /></a></li>
                            <li><a href="#meterProgram" id="meterProgramTab"><fmt:message key="aimir.model.meterprogram" /></a></li>
                           	<li><a href="#obisCodeMgmt" id="obisCodeTab"><fmt:message key="aimir.obisCode.Setting" /></a></li>
                           	<li><a href="#meterSetting" id="meterSettingTab"><fmt:message key="aimir.meterSetting" /></a></li>
                        </ul>

                        <div id="fragment-2">
                        <form id="modelForm">
                            
                            <%-- **************** 2011. 10. 19 문동규 수정 후 소스 Start ************ --%>
                            <input type="hidden" name="supplierId" /><input type="hidden" name="modelId" /><input type="hidden" name="image" />
                            <input type="hidden" name="configId" id="configId"/><input type="hidden" name="channelidarray" />
                            
                            <div id="fragment-left">
                                <ul>
                                    <label><fmt:message key="aimir.vendor" /></label>
                                    <li><select id="deviceVendor" name="deviceVendor"></select></li>
                                </ul>
                                <ul>
                                <input type="hidden" name="code"/>
                                <ul>
                                    <label><fmt:message key="aimir.model" /></label>
                                    <li><input type="text" name="modelName" size="30" maxlength="80"
                                        class="nuri_search_n" /></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.type2" /></label>
                                    <li><select id="mainDeviceType" name="mainDeviceType"></select>
                                        <input type="text" id="mainDeviceTypeView" name="mainDeviceTypeView" readonly="readonly" style="display:none;" class="border-trans"/></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.device.subtype" /></label>
                                    <li><select id="deviceType" name="deviceType"></select></li>
                                </ul>
                                <ul>
                                    <label> <fmt:message key="aimir.description" /></label>
                                    <li><textarea type="text" name="description" rows="3" maxlength="300" style="width:100%;"></textarea></li>
                                </ul>
                            </div>
                            <div id="fragment-right">
                                <ul>
                                    <label><fmt:message key="aimir.img.edit" /></label>
                                    <li id="photo"><img src=""></li>
                                    <li id="upload">
    
                                        <div>
                                            <a href="#" id="attachFile" class="btn_blue"><span><fmt:message key="aimir.img.find" /></span></a>
                                            <!--  ul>
                                                <li><a href="#" id="attachFile" class="on"><fmt:message key="aimir.img.find" /></a></li>
                                            </ul-->
                                            <ul class="red11pt">
                                                <li class="red11pt">
                                                <p class="text"></p>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>
                                </ul>

                            </div>
                            <div id="deviceConfigDiv" style="clear:both;display:none;">
                              <input type="hidden" name="configName" size="30" maxlength="80" />
                             
                                <ul>
                                    <label><fmt:message key="aimir.modem.parsername" /></label>
                                    <li><input type="text" id="parserName" name="parserName" /></li>`
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.modem.savername" /></label>
                                    <li><input type="text" id="saverName" name="saverName" /></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.modem.ondemandpasername" /></label>
                                    <li><input type="text" id="ondemandParserName" name="ondemandParserName" /></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.modem.ondemandsavername" /></label>
                                    <li><input type="text" id="ondemandSaverName" name="ondemandSaverName" /></li>
                                </ul> 
                             
                            </div>
                            <div id="meterConfigDiv" style="display:none;">
                                <ul>
                                    <label><fmt:message key="aimir.meter.class" /></label>
                                    <li><input type="text" id="meterClass" name="meterClass" /></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.meterProtocol" /></label>
                                    <li>
                                    	<select style="width:235px !important;" id="meterProtocol" name="meterProtocol"></select>
                                    </li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.supply.powerspec" /></label>
                                    <li><input type="text" name="powerSupplySpec" size="30" maxlength="80" /></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.phasetype2" /></label>
                                    <li class="channel-select"><select style="width:235px !important;" id="phase" name="phase"></select></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.resolution" /></label>
                                    <li><input type="text" name="lpInterval" size="30" maxlength="80" onkeyup="removeCommaForInt(event, this);" onfocus="removeCommaForInt(event, this);" onblur="removeCommaForInt(event, this);"/></li>
                                </ul>
                                <ul>
                                    <label><fmt:message key="aimir.pulse.constant" /></label>
                                    <li><input type="text" name="pulseConst" size="30" maxlength="80" onkeyup="removeCommaForReal(event, this);" onfocus="removeCommaForReal(event, this);" onblur="removeCommaForReal(event, this);"/><!--m3/h--></li>
                                </ul>
                                
                                <ul class="channel-config">
                                    <label><fmt:message key="aimir.channel" /></label>
                                    <li>
                                        <div class="bodyleft_channelinfo">
                                        <div class="box-bluegradation-channel-left">
                                            <ul>
                                                <li class="box-bluegradation-channel-padding">
                                                    <div id="MeterAddChannelGridDiv">
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                        </div>

                                        <div id="btn-putinout"  class="meter-info-btn3">
                                         <ul id="ChannelDeleteUl"><li class="btn-putin"><a id="ChannelDelete" href="#;" onClick="ChannelDelete();" class="on"></a></li></ul>
                                        <ul id="ChannelAddUl"><li class="btn-putout"><a id="ChannelAdd" href="#;" onClick="ChannelAdd();" class="on"></a></li></ul>     
                                        </div>

                                        <div id="channelListNotMeter" class="bodyleft_channelinfo">
                                        <div class="box-bluegradation-channel-left ">
                                            <ul>
                                                <li class="box-bluegradation-channel-padding">
                                                   
                                                    <div id="ChannelListByNotMeterGridDiv">
                                                    </div> 
                                                </li>
                                            </ul>
                                        </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                            
                            <div id="btnList2" style="clear:both;">
                                <!-- <div id="btn" class="right" style="clear:both;margin-top:10px;"> -->
                                <div id="btn" class="right" style="margin-top:30px;">
                                    <a href="#" id="add2" class="btn_blue"><span><fmt:message key="aimir.button.register" /></span></a>
                                    <a href="#" id="update2" class="btn_blue"><span><fmt:message key="aimir.update" /></span></a>
                                    <a href="#" id="delete2" class="btn_blue"><span><fmt:message key="aimir.button.delete" /></span></a>
                                </div>
                            </div>
                            <%-- **************** 2011. 10. 19 문동규 수정 후 소스 End ************** --%>

                        </form>
                        </div>

                        <div id="meterProgram">
                            <div id="meterProgram-left">
	                            <label id="reportTitle" class="check" style="height:20px;"><fmt:message key="aimir.model.meterprogramlog"/></label>
                                <div id="meterProgramGridDiv" style="width: 100%; float: left;"></div>
                            </div>
                            <div id="meterProgram-right">
                                <label class="ic_tringle" style="height:20px;"><fmt:message key="aimir.model.meterprogramkind" /><!-- Meter Program Kind --></label>
                                <select id="meterProgramKind" name="meterProgramKind" style="width:160px"></select>

                                <span style="padding: 10px; height: 15px;"></span>
                                
                                <label class="ic_tringle" style="height:20px;"><fmt:message key="aimir.model.meterprogramsettings" /><!-- MeterProgram Settings --></label>
                                <textarea id="settings" name="settings" style="width: 98%; height: 342px;"></textarea>

                                <ul id="cron" style="margin-top:10px;">
                                    <label style="width:30% !important;"><fmt:message key="aimir.cronExpression" /></label>
                                    <li style="width:70% !important;"><input type="text" id="cronSetting" name="cronSetting" /></li>
                                </ul>

                            </div>
                            <!-- <div id="btn2" class="right" style="clear:both;"> -->
                            <div id="meterProgramBtnList">
                            <div id="meterProgramBtn" class="right floatright" style="clear:both; padding-right:10px; padding-top: 10px;">
                                <a href="#" id="meterProgramReg" class="btn_blue"><span><fmt:message key="aimir.button.register" /></span></a>
                                <a href="#" id="meterProgramApply" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.button.apply" /></span></a>
                                <a href="#" id="meterProgramRetry" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.button.retry" /></span></a>
                                <a href="#" id="regOk" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.ok" /></span></a>
                                <a href="#" id="regCancel" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.cancel" /></span></a>
                                <a href="#" id="applyOk" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.ok" /></span></a>
                                <a href="#" id="applyCancel" class="btn_blue" style="display:none;"><span><fmt:message key="aimir.cancel" /></span></a>
                            </div>
                            </div>

                        </div>

						<div id='obisCodeMgmt'>
							<div>
								<ul>
									<li class="floatleft withinput"><label><b><fmt:message key='aimir.model.obisCode'/></b></label></li>
									<li class="floatleft"><input type='text' id='obisCode' name='obisCode'></li>
									<li class="floatleft withinput"><label><b><fmt:message key='aimir.model.classId'/></b><label></li>
									<li class="floatleft"><input type='text' id='classId' name='classId' size="10px"></li>
									<li class="floatleft withinput"><label><b>Attribute/Method No</b></label></li>
									<li class="floatleft"><input type='text' id='attributeNo' name=attributeNo></li>
									
									<li class="floatleft">
										<em class="am_button">
                        					<a href="javascript;" onClick="javascript:obisMgmtHandler.searchObis();" class="on"><fmt:message key="aimir.button.search" /></a>
                        				</em>
									</li>
									<li class="floatright">
										<em class="am_button">
                        					<a id="addObisRowATag" href="javascript;" onClick="javascript:obisMgmtHandler.addObisRow();" class="on"><fmt:message key="aimir.add.obis" /></a>
                        				</em>
                        			</li>
									<br/><br/>							
									<li class="floatleft margin-b5px"><div id='obisCodeListDiv'></div></li>
								</ul>
							</div>
							<div id="addObisCmp" name='addObisCmp'></div>
                        </div>
                        
                        <div id='meterSetting'>
                        	<div id="resultDiv"></div>
                        	<div id='resultDetailDiv'></div>
                        	<div id='detailViewDiv'></div>
                        	<div id="fragment-left" style="width: 80% !important;">
                        		<ul>
                        			<li>
                       					<input name="selectMeter" id="selectMeter1" type="radio" value="1" class="trans" onchange="javascript:meterSettingHandler.changeMeterType();" />
                       					<label><fmt:message key='aimir.metergroup'/></label>
                                        <%-- <input type="text" value="<b><fmt:message key="aimir.metergroup"/></b>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/> --%>
                                        <input name="selectMeter" id="selectMeter0" type="radio" value="0" class="trans" onchange="javascript:meterSettingHandler.changeMeterType();"/>
                                        <label><fmt:message key='aimir.meterid'/></label>
                                        <%-- <input type="text" value="<b><fmt:message key="aimir.meterid"/></b>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/> --%>
	                        		</li>
                        			<li>
                        				<div id="meterGroupDiv">
											<label><fmt:message key='aimir.metergroup'/></label>
                        					<span>
                        					<select id="meterGroup" name="meterGroup">
                        						<option value=""><fmt:message key="aimir.all" /></option>
                        					</select>
                        					</span>                        				
                        				</div>
                       					<div id="meterDiv" style="display: none;">
                       						<label><fmt:message key='aimir.meterid'/></label>
                        					<input type="text" id="meterSetId" name="meterSetId"/>
                       					</div>
	                        		</li>
	                        		<br/><br/>
	                        		<li>
                       					<label><fmt:message key='aimir.instrumentation'/></label>
                        				<span><select style="width: 300px" id="obisCmdList" name="obisCmdList" onchange="javascript:meterSettingHandler.changeCommand();"></select></span>
                        			</li>
                        			<br/><br/>
                        			<li>
                        				<label><fmt:message key='aimir.model.className'/></label>
                        				<span><input type='text' id='classNameMS' name='classNameMS' readOnly style="border: 0; width: 300px"/></span>
                        			</li>
                        			<br/><br/>
                        			<li>
                        				<label><fmt:message key='aimir.parameter.set'/></label>
                        			</li>
                        			<br/><br/>
                        			<li style="padding-left: 40px">
                        				<div id="parameterSetDiv"></div>
                        				<br/>
                        				<div style="float: right;">
	                        				<em class="am_button">
	                        					<a href="javascript;" id="cmdGetObis" onClick="javascript:meterSettingHandler.cmdObis('cmdMeterParamGet');" class="on"><fmt:message key='aimir.get'/></a>
	                        				</em>
	                        				<em class="am_button">
	                        					<a href="javascript;" id="cmdSetObis" onClick="javascript:meterSettingHandler.cmdObis('cmdMeterParamSet');" class="on"><fmt:message key='aimir.set'/></a>
	                        				</em>
                        				</div>
                        			</li>
                        		</ul>
                        	</div>
                        	</br></br>
                        	<div id="addObjCmp" name='addObjCmp'></div>
                        </div>
                    </div>
                    <!-- 장비추가 Tab (E) -->
                </div>

            </li>
        </ul>
    </div>


    <div class="clear-width100"></div>
    <div class="bodyleft_devicemodel">

        <!-- 제조사/장비별 Tree (S) -->
        <div id="container-2">

            <form id="searchForm-max" onsubmit="return false;">

            <!-- search -->
            <div class="search-s1">
                <ul>
                    <li class="search-s1-input"><input name="searchWord" id="searchWord" type="text" value="Search"></li>
                    <li class="search-s1-btn"><a href="#" id="search"></a></li>
                </ul>
                <ul class="search-s1-result2  margin-t3px"">
                    <li><font id="searchCount"></font></li>
                </ul>
            </div>

            <!-- 제조사별 장비 Tree -->
            <fieldset id="vendor-tree">
            <div id="basic_html"></div>
            </fieldset>


            </form>
        </div>
        <!-- 제조사/장비별 Tree (E) -->

    </div>


</div>
<!-- Gadget Body (E) -->


</body>
</html>
