<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!--
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" /> -->

    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>

<script type="text/javascript" charset="utf-8">
    new AjaxUpload('installImageInsert', {
        action: "${ctx}/gadget/device/addInstallImg.do?mcuId=" + mcuId,
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

                $('#upload .text').text('Uploading ' + file);

            } else {
                // extension is not allowed
                $('#upload .text').text('Error: only images are allowed');
                // cancel upload
                return false;
            }
        },
        onComplete : function(){
            //insertMeterInstallImg(file , response);
            goImgPage('INSERT');
        }
    });
	
    $(document).ready(function() {
        // 수정권한 체크
        if (editAuth == "true") {
            $("#btnBasic").show();
            $("#installInfoBtn").show();
            $("#installImgBtnList").show();
        } else {
            $("#btnBasic").hide();
            $("#installInfoBtn").hide();
            $("#installImgBtnList").hide();
        }

        // Command 권한 체크
        if (cmdAuth == "true") {
            $("#mcuCommand").show();
        } else {
            $("#mcuCommand").hide();
        }
		
        document.getElementById('gpioX').value = '${mcu.gpioX}';
        document.getElementById('gpioY').value = '${mcu.gpioY}';
        document.getElementById('gpioZ').value = '${mcu.gpioZ}';
        document.getElementById('sysLocation').value = '${mcu.sysLocation}';

        // 지역 검색 트리 초기화
        locationTreeGoGo('treeDivInfo', 'searchWord_info', 'locationId_info');

        // 모뎀유형 초기화
         /* $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.1'}
                , function (returnData){
                    $('#mcuTypes').pureSelect(returnData.code);
                    $('#mcuTypes').selectbox();
                    ;
                });  */
        $('#mcuTypes').selectbox();
        $("#deviceModelId").selectbox();
        $("#protocolType").selectbox();
        $("#mcuStatusInfo").selectbox();

        $('#sysLocalPort').bind("keydown", function(event) {inputOnlyNumberType(event, $(this));});
        
    });
    // 설치정보 수정 폼으로 변경
    var displayInstallInfoModifyForm = function() {

        if (mcuId == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.mcu.select"/>');
            return;
        }

        document.getElementById('installInfo').style.display = 'none';
        document.getElementById('installInfoForm').style.display = 'block';
    };

    // 설치 정보 수정
    var updateMCUInstallInfo = function() {
        var installDate = document.getElementById('installDate').value;
        var lastswUpdateDate = document.getElementById('lastswUpdateDate').value;

        if (installDate != '') {
            if (installDate.length != 14) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.installdate.input14" />');
                return ;
            }
        }

        if (lastswUpdateDate != '') {
            if (lastswUpdateDate.length != 14) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.update.input14" />');
                return ;
            }
        }
        
		// 입력받은 IPv4/IPv6의 형식 check 로직 (S)
		checkIpAddress($("#ipAddr").val(), $("#ipv6Address").val());
		if(ipValidation == false) {
			return false;
		}
		
		checkIpAddress($("#amiNetworkAddress").val(), $("#amiNetworkAddressV6").val());
		if(ipValidation == false) {
			return false;
		}
		// 입력받은 IPv4/IPv6의 형식 check 로직 (E)

        var params = {
            supplierId : supplierId,
            installDate : $("#installDate").val(),
            //swVersion : $("#swVersion").val(),
            hwVersion : $("#hwVersion").val(),
            protocolType : $("#protocolType").val(),
            ipAddr : $("#ipAddr").val(),
            lastswUpdateDate : $("#lastswUpdateDate").val(),
            sysPhoneNumber : $("#sysPhoneNumber").val(),
            mcuId : mcuId,
            imgPage : $("#imgPage").val(),
            locationId : $("#locationId_info").val(),
            sysLocation : $("#sysLocation").val(),
            sysLocalPort : $("#sysLocalPort").val(),
            mcuStatus : $("#mcuStatusInfo").val(),
            sysHwBuild : $("#sysHwBuild").val(),
            sysSerialNumber : $("#sysSerialNumber").val(),
            sysTlsPort : $("#sysTlsPort").val(),
            sysTlsVersion : $("#sysTlsVersion").val(),
            amiNetworkAddress : $("#amiNetworkAddress").val(),
            amiNetworkAddressV6 : $("#amiNetworkAddressV6").val(),
            ipv6Address : $("#ipv6Address").val(),
            macAddr : $("#sysMacAddress").val(),

        };

        $("#generalDivTab").load("${ctx}/gadget/device/updateMCUInstallInfo.do", params,
            function() {
                dcuStore.reload(dcuStore.lasgOptions);  // grid reload
                displayDiv(); // INFO 탭 및 INSTALLINFO 탭 숨김/보이기
                document.getElementById('installInfo').style.display = 'block';
                document.getElementById('installInfoForm').style.display = 'none';
        });
        var params = {
            "supplierId" : supplierId,
            "mcuId" : mcuId,
            "imgPage" : imgPage
        };
        
        $("#generalDivTab").load("${ctx}/gadget/device/mcuInfo.do", params, displayDiv);
    };
	//////////////////////////////////////////////////////////////////
    
    // 설치 정보 수정 폼 취소
    var cancelMCUInstallInfo = function() {
        /*
        document.getElementById('installDate').value = document.getElementById("orgInstallDate").innerHTML;
        document.getElementById('swVersion').value = document.getElementById("orgSwVersion").innerHTML;
        document.getElementById('hwVersion').value = document.getElementById("orgHwVersion").innerHTML;
        document.getElementById('ipAddr').value = document.getElementById("orgIpAddr").innerHTML;
        document.getElementById('sysPhoneNumber').value = document.getElementById("orgSysPhoneNumber").innerHTML;
        */
        document.getElementById('installInfo').style.display = 'block';
        document.getElementById('installInfoForm').style.display = 'none';
    };

    // 설치 이미지 페이지 이동
    var goImgPage = function(_mode, _page, _mcuInstallImgId) {

        if (_page == undefined)
            _page = -1;
        if (_mcuInstallImgId == undefined)
            _mcuInstallImgId = -1;

        var params = {
            "mcuId" : mcuId,
            "imgPage" : _page,
            "mode" : _mode
        };

        $.get("${ctx}/gadget/device/mcuInstallImgPagingInfo.do",
                params,
                function(data) {

                    var innerHTML = '';

                    if (data.paging.prevPage == 'true') {
                        innerHTML += "<a href=\"JavaScript:goImgPage('PAGE', " + (parseInt(data.paging.page) - 1) + ");\" class=\"back\"></a>";
                    }

                    for ( var i = data.paging.startPage; i <= data.paging.endPage; i++) {

                        if (data.paging.page == i) {
                            innerHTML += "<a class=\"current\">" + i + "</a>";
                        } else {
                            innerHTML += "<a href=\"JavaScript:goImgPage('PAGE', " + i + ");\">" + i + "</a>";
                        }
                    }

                    if (data.paging.nextPage == 'true') {
                        innerHTML += "<a href=\"JavaScript:goImgPage('PAGE', " + (parseInt(data.paging.page) + 1) + ");\" class=\"next\"></a>";
                    }


                    if(data.currentTimeMillisName == "")
                        $("#mcuInstallImg").attr("src", '${ctx}' + '/uploadImg/default/mcuDefaultImg.jpg');
                    else
                        $("#mcuInstallImg").attr("src", '${ctx}' + '/' + data.currentTimeMillisName);

                    //$("#mcuInstallImgPaging").html = innerHTML;

                    $('#mcuInstallImgPaging').addClass('paging-installimage');
                    document.getElementById('mcuInstallImgPaging').innerHTML = innerHTML;
                    document.getElementById("imgPage").value = data.paging.page;
                    document.getElementById('mcuInstallImgId').value = data.mcuInstallImgId;
                });
    };

    // 설치 이미지 인서트
    var installImageInsert = function() {

        if($('#installImgFile').val() == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile" />');
            return ;
        }

        if(confirm('<fmt:message key="aimir.wouldSave"/>')) {

            var params = {
                url : "${ctx}/gadget/device/addInstallImg.do?mcuId=" + mcuId,
                type : "post",
                success : function() {
                    goImgPage('INSERT');
                }
            };

            $('#installImgForm').ajaxSubmit(params);
        }

    };

    // 설치 이미지 업데이트
    var installImageUpdate = function() {

        if($('#installImgFile').val() == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.selectFile" />');
            return ;
        }

        if(confirm('<fmt:message key="aimir.update.want" />')) {

            var params = {
                url : "${ctx}/gadget/device/updateInstallImg.do?mcuInstallImgId=" + document.getElementById('mcuInstallImgId').value,
                type : "post",
                success : function() {
                    goImgPage('UPDATE', document.getElementById('imgPage').value);
                }
            };

            $('#installImgForm').ajaxSubmit(params);
        }
    };

    // 설치 이미지 삭제
    var installImageDelete = function() {

        if(confirm('<fmt:message key="aimir.wantDelete" />')) {

            $.getJSON("${ctx}/gadget/device/deleteInstallImg.do",
                    {'mcuInstallImgId' : document.getElementById('mcuInstallImgId').value},
                    function(data) {
                        goImgPage('DELETE');
                    }
                );
            /*
            var params = {
                url : "${ctx}/gadget/device/deleteInstallImg.do?mcuInstallImgId=" + document.getElementById('mcuInstallImgId').value,
                type : "post",
                success : function() {
                    goImgPage('DELETE');
                }
            };

            $('#installImgForm').ajaxSubmit(params);
            */
        }
    };

    var displayBasicDiv = function(_divName) {

        if(_divName == 'mcuBasicInfoTab') {
            $('#mcuBasicInfoTab').hide();
            $('#btnBasic').hide();
            $('#mcuInfoUpdateTab').show();
        } else if(_divName == 'mcuInfoUpdateTab') {
            $('#mcuBasicInfoTab').show();
            $('#btnBasic').show();
            $('#mcuInfoUpdateTab').hide();
        }
    };

    var updateDeviceModel = function() {
        var mcuTypeId = $('#mcuTypes').val();
        var deviceModelId = $('#deviceModelId').val();

        $.getJSON('${ctx}/gadget/device/updateDeviceModel.do',
            {'mcuId' : mcuId, 'mcuTypeId' : mcuTypeId, 'sysSwRevision' : $("#sysSwRevision").val(), 'sysSwVersion' : $("#sysSwVersion").val()},
            function(data) {
                displayBasicDiv('mcuInfoUpdateTab');
                $('#deviceVendorName').val(data.deviceVendorName);
                $('#deviceModelName').val(data.deviceModelName);
                $('#mcuTypeName').val(data.mcuTypeName);
                $("#sysSwVersionVal").val(data.sysSwVersion);
                $('#sysSwRevisionVal').val(data.sysSwRevision);
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',data.result);
                dcuStore.reload(dcuStore.lasgOptions);
            }
        );
    };
    
/*     
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
   	               {
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
   	} */
    var otaWin;
    function runOta(){
		var opts = "width=800px, height=550px, left=" + 200 + "px, top=200px, resizable=no, status=no, location=no";
		var obj = new Object();
		obj.pageWidth = '800';
		obj.pageHeight = '550';
		obj.deviceModel = "";
		obj.modelName = tempModelName;
		obj.condition = "";
		obj.equip_kind = "dcu";
		obj.deviceIdString = tempDeviceId;
		obj.loginId = loginId;
		obj.locationId = tempLocId;
		obj.targetDeviceType = "dcu";

		if (otaWin){
			otaWin.close();
		}
			
		otaWin = window.open("${ctx}/gadget/device/firmware/firmwareAddPopup.do", 
								"firmwareAdd", opts);
		otaWin.opener.obj = obj;			
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
</script>

<ul><li><!-- mucMaxGadget.jsp 로 인클루딩될 때 필요함 -->

    <!-- 탭박스 (기본정보탭) (S) -->
    <div style="height:550px;">
    <ul class="width">
    <li class="padding">



        <!-- Body Left : 기본정보 (S)-->
        <div id="mcuInfoTab" class="bodyleft_mcuinfo">

            <div class="headspace"><label class="check"><fmt:message key="aimir.button.basicinfo"/></label></div>

            <!-- Blue BG Box : 기본정보 (S)-->
            <div class="bigbox-bluegradation-mcu" style="height:340px;">
            <ul><li class="bigbox-bluegradation-mcu-padding" style="height:340px;">
                    <div class="mcuinfo-tabinside-pic2">
                        <div class="pic01">
                            <c:choose>
                                <c:when test="${not empty mcu.deviceModel.image && mcu.deviceModel.image != ''}">
                                    <c:set var="mcuDeviceModelImg" value="${ctx}/${mcu.deviceModel.image}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="mcuDeviceModelImg" value="${ctx}/uploadImg/default/mcuDefaultImg.jpg" />
                                </c:otherwise>
                            </c:choose>
                            <img src="${mcuDeviceModelImg}">
                        </div>
                    </div>


                    <div class="mcuinfo-tabinside-table2" style="padding-top: 0px;">

                        <!-- MCU기본정보_보기 (S) -->
                        <div id="mcuBasicInfoTab">
                            <table class="wfree">
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.mcuid"/></td>
                                    <td><input type="text" value="${mcu.sysID}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                                <tr>
                                	<td class="graybold11pt withinput"><fmt:message key="aimir.mcutype"/></td>
                                    <td><input type="text" id="mcuTypeName" value="${mcu.mcuType.descr}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                                <tr>
								    <td class="graybold11pt withinput"><b>GS1</b></td>
								    <td><input type="text" id="mcuGs1" value="${mcu.gs1}" class="border-trans gray11pt"  readonly/></td>
								</tr>
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.vendor"/></td>
                                    <td><input type="text" id="deviceVendorName" value="${mcu.deviceModel.deviceVendor.name}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.model"/></td>
                                    <td><input type="text" id="deviceModelName" value="${mcu.deviceModel.name}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                                <tr> <td class="graybold11pt withinput"><fmt:message key="aimir.sw.version"/></td>
                                     <td><input type="text" id="sysSwVersionVal" value="${mcu.sysSwVersion}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.mcu.swrevision"/></td>
                                    <td><input type="text" id="sysSwRevisionVal" value="${mcu.sysSwRevision}" class="border-trans gray11pt"  readonly/></td>
                                </tr>
                            </table>
                        </div>
                        <!-- MCU기본정보_보기 (E) -->


                        <!-- MCU기본정보_수정 (S) -->
                        <div id="mcuInfoUpdateTab" style="display:none; width: 255px">
                            <table class="wfree">
                            	<%-- <tr><td class="graybold11pt withinput"><b>GS1</b></td></tr>
                            	<tr>
								    <td><input type="text" id="mcuGs1" value="${mcu.gs1}" class="border-trans gray11pt"  readonly/></td>
								</tr> --%>
                            
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.mcutype"/></td></tr>
                                <tr><td><select id="mcuTypes" name="mcuTypes.id">
                                    <c:forEach var="mcuType" items="${mcuTypes}">
                                        <c:choose>
                                            <c:when test="${mcu.mcuType.id == mcuType.id}">
                                                <option value="${mcuType.id}" selected>${mcuType.descr}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${mcuType.id}">${mcuType.descr}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select></td></tr>
                                <%-- <tr><td class="graybold11pt withinput"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.model"/></td></tr> --%>
                                <%-- <td><select id="deviceModelId" style="width:240px;">
                                            <c:forEach var="deviceModel" items="${deviceModels}">
                                                <c:choose>
                                                    <c:when test="${mcu.deviceModel.id == deviceModel.id}">
                                                        <option value="${deviceModel.id}" selected>${deviceModel.vendorName} - ${deviceModel.modelName}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${deviceModel.id}" >${deviceModel.vendorName} - ${deviceModel.modelName}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                    </td> --%>
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.sw.version"/></td></tr>
                                <tr><td>
                                    <input type="text" id="sysSwVersion" value="${mcu.sysSwVersion}"/>
                                    </td>
                                </tr>
                                </tr>
                                <tr><td class="graybold11pt withinput"><fmt:message key="aimir.mcu.swrevision"/></td></tr>
                                <tr><td>
                                    <input type="text" id="sysSwRevision" value="${mcu.sysSwRevision}"/>
                                    </td>
                                </tr> 
                            </table>
                            <div class="margin-r10" style="float: right; margin-top: 65px;"> 
                                <em class="am_button"><a href="javascript:updateDeviceModel()" class="on"><fmt:message key="aimir.ok"/></a></em>&nbsp;
                                <em class="am_button"><a href="javascript:displayBasicDiv('mcuInfoUpdateTab')"><fmt:message key="aimir.cancel"/></a></em>
                            </div>

                        </div>
                        <!-- MCU기본정보_수정 (E) -->

                    </div>

            </li></ul>
            </div>
            <!-- Blue BG Box : 기본정보 (E)-->

        </div>
        <!-- Body Left : 기본정보 (E)-->


        <!-- Body Right : 설치정보 (S)-->
        <div class="bodyright_mcuinfo">
            <ul><li id="mcu-tab-none" class="bodyright_mcuinfo_leftmargin">

                <div class="headspace"><label class="check"><fmt:message key="aimir.install"/>&nbsp;<fmt:message key="aimir.info"/></label></div>

                <!-- Blue BG Box : 설치정보 (S)-->
                <div class="bigbox-bluegradation-mcu" style="height:340px; margin-bottom: 20px;">
                <ul><li class="bigbox-bluegradation-mcu-padding" style="height:340px; margin-top: 0px;">


                    <!-- Blue BG Box : 설치정보 - 정보내용 (S)-->
                    <div class="bodyright_mcuinfo_tabinside">
                    <ul><li class="bodyright_mcuinfo_tabinside_table">

                            <div id="installInfo">
                                <div style="height:200px;">
                                    <table class="wfree" style="width: 610px">
                                        <tr><td class="graybold11pt withinput"><fmt:message key="aimir.installationdate"/></td>
                                            <td style="width: 155px">
                                                <c:choose>
                                                    <c:when test="${not empty installDateShow}">
                                                        <input type="text" id="orgInstallDate" value="${installDateShow}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgInstallDate" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            <td class="space20" style="width: 7px"></td>
                                            </td>
                                                <fmt:parseDate value="${mcu.lastswUpdateDate}" var="parseLastswUpdateDate" pattern="yyyyMMddHHmmss" />
                                                <fmt:formatDate value="${parseLastswUpdateDate}" type="DATE" pattern="yyyy/MM/dd HH:mm:ss" var="formatLastswUpdateDate" />
                                            <td width="145"  class="graybold11pt withinput"><fmt:message key="aimir.lastversionupdate"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty formatLastswUpdateDate}">
                                                        <input type="text" id="orgLastswUpdateDate" value="${formatLastswUpdateDate}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgLastswUpdateDate" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.fw.hwversion"/></td>
                                            <td><input type="text" id="orgHwVersion" value="${mcu.sysHwVersion}" class="border-trans gray11pt"  readonly/></td>
                                            <td class="space20" style="width: 7px"></td>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.sysHwBuild"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysHwBuild}">
                                                           <input type="text" value="${mcu.sysHwBuild}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr><td class="graybold11pt withinput"><fmt:message key="aimir.view.mcu39"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty mcu.protocolType.descr}">
                                                        <input type="text" id="orgProtocolType" value="${mcu.protocolType.descr}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgProtocolType" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="space20" style="width: 7px"></td>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.portnumber"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysLocalPort}">
                                                        <input type="text" id="orgSysLocalPort" value="${mcu.sysLocalPort}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgSysLocalPort" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput">IPv4 Address</td>
                                            <td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.ipAddr}">
                                                        <input type="text" id="orgIpAddr" style="width: 464px !important;" value="${mcu.ipAddr}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgIpAddr" style="width: 464px !important;" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.ipv6address"/></td>
                                        	<td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.ipv6Addr}">
                                                           <input type="text" id="orgIpv6Address" style="width: 464px !important;" value="${mcu.ipv6Addr}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" id="orgIpv6Address" style="width: 464px !important;" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.wanIpAddress"/></td>
                                            <td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.amiNetworkAddress}">
                                                           <input type="text" style="width: 464px !important;" value="${mcu.amiNetworkAddress}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" style="width: 464px !important;" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.wanIpv6Address"/></td>
                                            <td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.amiNetworkAddressV6}">
                                                           <input type="text" style="width: 464px !important;" value="${mcu.amiNetworkAddressV6}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" style="width: 464px !important;" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.sysSerialNumber"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysSerialNumber}">
                                                           <input type="text" value="${mcu.sysSerialNumber}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.sysTlsPort"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysTlsPort}">
                                                           <input type="text" value="${mcu.sysTlsPort}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                      		<td class="graybold11pt withinput"><fmt:message key="aimir.sysTlsVersion"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysTlsVersion}">
                                                           <input type="text" value="${mcu.sysTlsVersion}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.location"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.location.name}">
                                                           <input type="text" id="orgLoc" value="${mcu.location.name}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" id="orgLoc" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.status"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.mcuStatus.descr}">
                                                           		<input type="text" id="orgMcuStatus" value="${mcu.mcuStatus.descr}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" id="orgMcuStatus" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.mcumobile"/></td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysPhoneNumber}">
                                                        <input type="text" id="orgSysPhoneNumber" value="${mcu.sysPhoneNumber}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgSysPhoneNumber" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                         </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.macaddress"/></td>
                                            <td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.macAddr}">
                                                        <input type="text" style="width: 464px !important;" id="orgSysMacAddress" value="${mcu.macAddr}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" style="width: 464px !important;" id="orgSysMacAddress" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></td>
                                            <td colspan="4">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.sysLocation}">
                                                        <input type="text" id="orgSysLocation" style="width: 464px !important;" value="${mcu.sysLocation}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="orgSysLocation" style="width: 464px !important;" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        
                                        <tr>
                                      		<td class="graybold11pt withinput">Coordinator</td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.mcuCodi.codiID}">
                                                           <input type="text" id="coordinatorId" value="${mcu.mcuCodi.codiID}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text" id="coordinatorId" value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        	<td class="graybold11pt withinput">Coordinator FW Ver.</td>
                                            <td colspan="2">
                                                <c:choose>
                                                    <c:when test="${not empty mcu.mcuCodi.codiFwVer}">
                                                           <input type="text"  value="${mcu.mcuCodi.codiFwVer}" class="border-trans gray11pt"  readonly/>
                                                    </c:when>
                                                    <c:otherwise>
                                                           <input type="text"  value="-" class="border-trans gray11pt"  readonly/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                <div id="installInfoBtn">
                                    <div id="btn" class="mcu-info-btn2 margin-r10" style="top: 95px;">
                                        <ul><li><a href="JavaScript:displayInstallInfoModifyForm();"  class="on"><fmt:message key="aimir.install"/>&nbsp;<fmt:message key="aimir.info"/> <fmt:message key="aimir.update"/></a></li></ul>
                                    </div>
                                </div>
                            </div>

                            <div id="installInfoForm" style="display:none;">
                                <div style="height:200px;">
                                    <table class="wfree" style="width: 610px">
                                    <input type="hidden" id="MCUDeviceModelId" value="${mcu.deviceModelId}" />
                                        <tr><td class="graybold11pt withinput"><fmt:message key="aimir.installationdate"/></td>
                                            <td style="width: 155px;">
                                                <c:choose>
                                                    <c:when test="${not empty installDateShow}">
                                                        <input type="text" id="installDateshow" value="${installDateShow}" style="border: 0" readOnly />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" id="installDateshow" value="-" style="border: 0" readOnly />
                                                    </c:otherwise>
                                                </c:choose>
                                                <input type="hidden" id="installDate" value="${installDate}" />
                                            </td>
                                            
                                            <td class="space20" style="width: 7px"></td>
                                            <td width="145" class="graybold11pt withinput"><fmt:message key="aimir.lastversionupdate"/></td>
                                            <td><input type="text" id="lastswUpdateDate" value="${mcu.lastswUpdateDate}" /></td>
                                        </tr>
                                        <tr>
	                                        <td class="graybold11pt withinput"><fmt:message key="aimir.fw.hwversion"/></td>
                                            <td><input type="text" id="hwVersion" value="${mcu.sysHwVersion}" /></td>
                                            <td class="space20" style="width: 7px"></td>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.sysHwBuild"/></td>
                                            <td><input type="text" id="sysHwBuild" value="${mcu.sysHwBuild}" /></td>
                                        </tr>
                                        <tr><td class="graybold11pt withinput"><fmt:message key="aimir.view.mcu39"/></td>
                                            <td><select id="protocolType" name="select">
                                                    <c:forEach var="protocolCode" items="${protocolCodes}">
                                                        <c:choose>
                                                            <c:when test="${mcu.protocolType.id == protocolCode.id}">
                                                                <option value="${protocolCode.id}" selected>${protocolCode.descr}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${protocolCode.id}">${protocolCode.descr}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                </select></td>
                                            <td class="space20" style="width: 7px"></td>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.portnumber"/></td>
                                            <td><input type="text" id="sysLocalPort" value="${mcu.sysLocalPort}"/></td>
                                        </tr> 
                                        <tr>
                                        	<td class="graybold11pt withinput">IPv4 Address</td>
                                            <td colspan="4"><input style="width: 464px !important;" type="text" id="ipAddr" value="${mcu.ipAddr}" /></td>
                                        </tr>
                                        <tr>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.ipv6address"/></td>
                                            <td colspan="4"><input style="width: 464px !important;" type="text" id="ipv6Address" value="${mcu.ipv6Addr}" /></td>
                                        </tr>
                                        
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.wanIpAddress"/></td>
                                            <td colspan="4"><input style="width: 464px !important;" type="text" id="amiNetworkAddress" value="${mcu.amiNetworkAddress}" /></td>
                                        </tr>
                                        <tr>
	                                        <td class="graybold11pt withinput"><fmt:message key="aimir.wanIpv6Address"/></td>
                                            <td colspan="4"><input style="width: 464px !important;" type="text" id="amiNetworkAddressV6" value="${mcu.amiNetworkAddressV6}" /></td>
                                        </tr>
                                        <tr>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.sysSerialNumber"/></td>
                                            <td><input type="text" id="sysSerialNumber" value="${mcu.sysSerialNumber}" /></td>
                                            <td class="space20" style="width: 7px"></td>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.sysTlsPort"/></td>
                                            <td><input type="text" id="sysTlsPort" value="${mcu.sysTlsPort}" /></td>
                                        </tr> 
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.sysTlsVersion"/></td>
                                            <td><input type="text" id="sysTlsVersion" value="${mcu.sysTlsVersion}" /></td>
                                        	<td class="space20" style="width: 7px"></td>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.location"/></td>
                                            <td colspan="2">
                                                <input name="searchWord_info" id='searchWord_info' type="text" value='${mcu.location.name}'/>
                                                <input type='hidden' id='locationId_info' name="location.id" value='' />
                                            </td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.status"/></td>
                                            <td>
                                            <select id="mcuStatusInfo" name="select">
                                            	<c:choose>
                                            		<c:when test="${not empty mcu.mcuStatus}">
                                            			<c:forEach var="mcuStatus" items="${mcuStatus}">
                                                        <c:choose>
                                                            <c:when test="${mcu.mcuStatus.id == mcuStatus.id}">
                                                                <option value="${mcuStatus.id}" selected>${mcuStatus.descr}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${mcuStatus.id}">${mcuStatus.descr}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                            		</c:when>
                                            		<c:otherwise>
                                            			<option value="" selected>-</option>
                                            			<c:forEach var="mcuStatus" items="${mcuStatus}">
                                                        	<option value="${mcuStatus.id}">${mcuStatus.descr}</option>
                                                    	</c:forEach>
                                            		</c:otherwise>
                                            	</c:choose>
                                                </select>
                                            </td>
                                        	<td class="space20" style="width: 7px"></td>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.mcumobile"/></td>
                                            <td><input type="text" id="sysPhoneNumber" value="${mcu.sysPhoneNumber}"/></td>
                                        </tr>
                                        <tr>
                                        	<td class="graybold11pt withinput"><fmt:message key="aimir.macaddress"/></td>
                                            <td colspan="4"><input style="width: 464px !important;" type="text" id="sysMacAddress" value="${mcu.macAddr}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="graybold11pt withinput"><fmt:message key="aimir.detail"/> <fmt:message key="aimir.location"/></td>
                                            <td colspan="4" >
                                                <input type="text" id="sysLocation" style="width: 464px !important;" value="${mcu.sysLocation}"/>
                                            </td>
                                        </tr>
                                    </table>
                                    <div id="treeDivInfoOuter" class="tree-billing auto" style='display:none;'>
                                        <div id="treeDivInfo" style="width:219px;"></div>
                                    </div>
                                </div>
                                <div>
                                    <div id="btn" class="mcu-info-btn2 margin-r10" style="top: 95px;">
                                        <ul><li><a href="JavaScript:updateMCUInstallInfo();"  class="on"><fmt:message key="aimir.ok"/></a></li></ul>
                                        <ul><li><a href="JavaScript:cancelMCUInstallInfo();"><fmt:message key="aimir.cancel"/></a></li></ul>
                                    </div>
                                </div>
                            </div>


                        </li>
                    </ul>
                    </div>
                    <!-- Blue BG Box : 설치정보 - 정보내용 (E)-->


                    <!-- Blue BG Box : 설치정보 - 사진 (S)-->
                    <div class="bodyleft_mcuinfo_tabinside_pic">
                        <div class="pic01">
                            <c:choose>
                                <c:when test="${not empty currentTimeMillisName && currentTimeMillisName != ''}">
                                    <c:set var="mcuInstallImg" value="${ctx}/${currentTimeMillisName}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="mcuInstallImg" value="${ctx}/uploadImg/default/mcuDefaultImg.jpg" />
                                </c:otherwise>
                            </c:choose>
                            <img id="mcuInstallImg" src="${mcuInstallImg}">
                            <input type="hidden" id="mcuInstallImgId" value="${mcuInstallImgId}" />
                        </div>
                        <input type="hidden" id="imgPage" value="${paging.page}" />
                        <div id="mcuInstallImgPaging" class="paging-installimage">
                            <span>
                                    <c:if test="${paging.prevPage == 'true'}">
                                    <a href="JavaScript:goImgPage('PAGE', parseInt(${paging.page}) - 1);" class="back"></a>
                                    </c:if>
                                    <c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
                                    <c:choose>
                                    <c:when test="${paging.page == i}">
                                    <a class="current">${i}</a>
                                    </c:when>
                                    <c:otherwise>
                                    <a href="JavaScript:goImgPage('PAGE', '${i}');">${i}</a>
                                    </c:otherwise>
                                    </c:choose>
                                    </c:forEach>
                                    <c:if test="${paging.nextPage == 'true'}">
                                    <a href="JavaScript:goImgPage('PAGE', parseInt(${paging.page}) + 1);" class="next"></a>
                                    </c:if>
                            </span>
                        </div>
                        <div id="installImgBtnList" class="margin-t10px textalign-center">
                            <em class="am_sm_button" id="installImageInsert" style="cursor: pointer;"><fmt:message key="aimir.add"/></em>
                            <em class="am_sm_button" onClick="javascript:installImageDelete();" style="cursor: pointer;"><fmt:message key="aimir.button.delete"/></em>
                        </div>
                    </div>
                    <!-- Blue BG Box : 설치정보 - 사진 (E)-->


                </li></ul>
                </div>
                <!-- Blue BG Box : 설치정보 (E)-->

            </li></ul>
        </div>
        <!-- Body Right : Bodyleft (E)-->


        </li>
        <li>
            <div id="btnBasic" class="mcu-info-left-btns">
                <div id="btn" style="right: 23px; margin-right: 10px; margin-top: 80px;">
                    <ul><li><a href="javascript:displayBasicDiv('mcuBasicInfoTab')"class="on"><fmt:message key="aimir.button.basicinfo" /> <fmt:message key="aimir.update" /></a></li></ul>
                </div>
            </div>
        </li>
        </ul>

        <div id="mcu-tab-wide" class="width_auto margin-t60px">
            <div id="mcuCommand">
	            <!-- 장비점검 (S) -->
	            <div class="width_auto">
	                <label class="check"><fmt:message key="aimir.device.check"/></label>
	                <br /><br />
	                <div id = 'monitoringDiv' class="floatleft margin-r5" style="display: none;">
		                <em class="btn_org"><a href="javascript:mcuStatusMonitoring_PKS();"><fmt:message key="aimir.mcu.statusmonitoring"/></a></em>
	                </div>
	                <div id = 'resetDiv' class="floatleft margin-r5" style="display: none;">
		                <em class="btn_org"><a href="javascript:mcuReset();"><fmt:message key="aimir.mcu.reset"/></a></em>
	                </div>
	                <div id = 'pingDiv' class="floatleft margin-r5" style="display: none;">
		                <em class="btn_org"><a href="javascript:commandPing();"><fmt:message key="aimir.ping"/></a></em>
	                </div>
	                <div id = 'tracerouteDiv' class="floatleft margin-r5" style="display: none;">
		                <em class="btn_org"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute"/></a></em>
	                </div>
	                <div id = 'otaDiv' class="floatleft margin-r5" style="display: none;">
		                <em class="btn_org"><a href="javascript:runOta();"><fmt:message key="aimir.ota"/></a></em>
	                </div>
	                <%-- <em class="btn_org"><a href="javascript:mcuScanning();"><fmt:message key="aimir.unit.scanning"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:mcuDiagnosis();"><fmt:message key="aimir.mcu.diagnosis"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:mcuTimeSync();"><fmt:message key="aimir.mcu.timesync"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:sensorScan();"><fmt:message key="aimir.sensor.scanning"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:commandGetLog();"><fmt:message key="aimir.getLog"/></a></em> --%>
					<!-- <em id="getlogBtn" class="btn_org"><a href="javascript:openGetLogExcelReport();">Get Log File</a></em> -->
	                <%-- <em class="btn_org"><a href="javascript:commandCOAPPing();"><fmt:message key="aimir.coapPing"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:coapBrowser();"><fmt:message key="aimir.coapBrowser"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:successFail();"><fmt:message key="aimir.snmpEnableDisable"/></a></em> --%>
	                <!-- GE 미터만 가능한 검침 데이터 복구는 의미 없으므로 주석 처리
	                <ul><li><a href="javascript:alert('API 연결 안됨');" class="on"><fmt:message key="aimir.recovery.metering.data"/></a></li></ul>  -->
	                <%-- <em class="btn_org"><a href="javascript:CaptchaPanel('dcuSnmpEnableDisable');"><fmt:message key="aimir.snmpEnableDisable"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:commandForceUpload();"><fmt:message key="aimir.forceupload"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:commandCODIPing();"><fmt:message key="aimir.pingCODI"/></a></em> --%>
	                <%-- <em class="btn_org"><a href="javascript:execNICommand();"><fmt:message key="aimir.execNICommand"/></a></em> --%>
	            </div>
	            <div class="meterinfo-textarea margin-t20px">
	               <ul><li id="mcu-result"><textarea id="commandResult" readonly><fmt:message key="aimir.operation.result" /></textarea></li></ul>
	            </div>
	            <!-- 장비점검 (E) -->

            </div>

            <!-- 최종통신시각 (S) -->
            <div class="mcu-lastcommdate">
                <button type="button" class="ic-clock"></button>
                <span class="gray11pt"><fmt:message key="aimir.lastcomm"/> : ${lastCommDate} : ${mcu.gpioX}</span>
            </div>
            <!-- 최종통신시각 (E) -->


        </div>
        <!-- 탭박스 (기본정보탭) (E) -->

    </div>
</li></ul><!-- mucMaxGadget.jsp 로 인클루딩될 때 필요함 -->
