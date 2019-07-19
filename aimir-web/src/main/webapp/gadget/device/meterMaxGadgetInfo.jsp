<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" charset="utf-8">

    var infoJsonState = 0;
    var locDateFormat = "yymmdd";

    var MeterTypeMap = {};

    //setDate
    $(function() {
        //달력 화면을 최상위로 올리기위해 스타일 지정
        $("#ui-datepicker-div").css('z-index','100');

        // 미터 타입
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.3.1'}
                , function (returnData) {
                    var pure = [];
                    $.each(returnData.code, function(index, element) {
                        var option = {};
                        if (element.descr != "null") {
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
                        MeterTypeMap[element.id] = option;
                        pure.push(option);
                    });
                    $('#meterType').pureSelect(pure);
                    $('#meterType').selectbox();
                    infoJsonState++;
                });

        // 제조사 조회
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData) {
                    $('#deviceVendor').noneSelect(returnData.deviceVendors);
                    $('#deviceVendor').selectbox();
                    infoJsonState++;
                });

        // 모델 초기화
        $('#modelId').emptySelect();
        $('#modelId').selectbox();

        getDeviceModelsByVenendorId();
		infoJsonState++;
		infoJsonState++;
		
        $('#meterInfoArea').height($('#meterInfoDiv').height());
    });
    
    //Installation Date Picker 선택후 날짜 포멧 변경
    function localeDateFormat(setDate, inst) {
        var dateId       = '#' + inst.id;
        var dateHiddenId = '#' + inst.id + 'Hidden';

        var d = new Date();

        var time = leadingZeros(d.getHours(), 2) +
        leadingZeros(d.getMinutes(), 2) +
        leadingZeros(d.getSeconds(), 2);

        setDate += time;

        $(dateHiddenId).val(setDate);

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                });
    }

    function leadingZeros(n, digits) {
        var zero = '';
        n = n.toString();

        if (n.length < digits) {
            for (i = 0; i < digits - n.length; i++) {
                zero += '0';
            }
        }
        return zero + n;
    }

    $('#modelId').change( function() {
        var modelName = $("#modelId option:selected").text();
    });

    //수정시 액션
    var getInputValues = function() {
        // Edit 설정
        $('#mdsId').val($('#mdsIdHidden').val());

        $('#meterType').option($('#meterTypeHidden').val());
        $('#mcuSysId').val($('#mcuSysIdHidden').val());

        $('#usageThreshold').val($('#usageThresholdHidden').val());
        $('#deviceVendor').option($('#deviceVendorHidden').val());

        getModelListByVendor(1);
        
 		$('#modemPort').val($('#modemPortHidden').val());
        $('#hwVersion').val($('#hwVersionHidden').val());
        $('#swVersion').val($('#swVersionHidden').val());

        // view 설정
        $('#mdsIdView').val($('#mdsIdHidden').val());
        $('#meterTypeView').val($('#meterType option:selected').text());

        $('#mcuSysIdView').val($('#mcuSysId').val());

        $('#deviceVendorView').val($('#deviceVendor option:selected').text());
        
        $('#modemPortView').val($('#modemPort').val());
        $('#swVersionView').val($('#swVersion').val());
        $('#hwVersionView').val($('#hwVersion').val());
        
        $('#usageThresholdView').val($('#usageThreshold').val());
        $('#locationView').val($('#searchWord_3').val());
        //$('#installDateView').val($('#installDate').val());
        $('#installDateView').val($('#installDateFormat').val());
        
        var modelname = $("#modelId option:selected").text();
        console.log("model name is ", modelname);
    };

    $(document).ready(function() {
        // 수정권한 체크
        if (editAuth == "true") {
            $("#dupIdBtn").show();
            $("#infoBtnList").show();
        } else {
            $("#dupIdBtn").hide();
            $("#infoBtnList").hide();
        }
    });
</script>

<div class="headspace">
    <label class="check"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.info"/></label>
</div>
<div class="box-bluegradation-meter" style="height:310px;">
    <div class="padding20px" >
        <form id="meterInfoFormEdit">
           <div class="floatleft" style="width:130px;" >
                <div class="picbox" id="meterDeiveImgEdit">
                    <c:choose>
                        <c:when test="${not empty meter.model.image}">
                            <img src="../../${meter.model.image}" />
                        </c:when>
                        <c:otherwise>
                            <img src="../../uploadImg/default/meterDefaultImg.jpg" />
                        </c:otherwise>
                    </c:choose>
                 </div>
            </div>
            <input type="hidden" id="protocolType" value="${meter.modem.protocolType}"/>
            <input type="hidden" id="modemId" value="${meter.modem.id}"/>
            <div class="w_auto" style="margin-left:135px;">
                <!-- 장비정보 등록 화면  -->
                <div id="meterRegistarionView">
                    <table>
                        <tr>
                            <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.meterid"/></b><font color="red">*</font></td>
                            <td class="padding-l20px"><input type="text" id="mdsId" name="mdsId" class="width-140px"/>
                                <c:choose>
                                    <c:when test="${not empty meter}">
                                        <input type="hidden" id="mdsIdHidden" value="${meter.mdsId}" />
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="mdsIdHidden" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <!-- <span><em id="dupIdBtn" class="am_button"><a a href="#" onclick="singleRegMeterIsMeterDuplicate();" id="checkDuplicateMeterId"><fmt:message key="aimir.checkDuplication" /></a></em></span> -->
                                <span><em id="dupIdBtn" class="am_button"><a onclick="singleRegMeterIsMeterDuplicate();" id="checkDuplicateMeterId"><fmt:message key="aimir.checkDuplication" /></a></em></span>
                            </td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.metertype"/></b><font color="red">*</font></td>
                            <td class="padding-l20px"><select name="select" id="meterType" class="width-140px"></select>
                                <c:choose>
                                    <c:when test="${not empty meter && not empty meter.meterType}">
                                        <input type="hidden" id="meterTypeHidden" name="meterType.descr" value="${meter.meterType.id}" />
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="meterTypeHidden" name="meterType.id" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </table>
                </div>

                <!-- 장비 정보 수정 화면 -->
                <div id="meterUpdateView">
                     <table class="wfree">
                        <tr>
                          <td class="withinput" style="width:105px;"><b><fmt:message key="aimir.meterid"/></b></td>
                          <td class="padding-l20px"><input type="text" id="mdsIdViewEdit" class="border-trans" readonly="readonly" value="${meter.mdsId}"/></td>
                        </tr>
                        <tr>
                          <td class="withinput"><b><fmt:message key="aimir.metertype"/></b></td>
                          <td class="padding-l20px"><input type="text" id="meterTypeViewEdit" class="border-trans" readonly="readonly" value="${meter.meterType.descr}"/></td>
                        </tr>
                        <tr>
                          <td class="withinput" style="width:105px;"><b><fmt:message key="aimir.mcuid"/></b></td>
                          <td class="padding-l20px"><input type="text"   id="mcuSysId" name="modem.mcu.sysid" class="border-trans" readonly="readonly" />
                              <c:choose>
                                  <c:when test="${not empty meter.modem  && not empty meter.modem.mcu  }">
                                     <input type="hidden" id="mcuSysIdHidden" value="${meter.modem.mcu.sysID}"/>
                                  </c:when>
                                  <c:otherwise>
                                     <input type="hidden" id="mcuSysIdHidden" value="" class="width-140px"/>
                                  </c:otherwise>
                              </c:choose>
                          </td>
                        </tr>
						<tr>
						    <td class="withinput" style="width:105px;"><b>GS1</b></td>
						    <td class="padding-l20px"><input type="text" class="border-trans" readonly="readonly" value="${meter.gs1}"/></td>
						</tr>
						<!-- 
						<tr>
						    <td class="withinput" style="width:105px;"><b>MBUS Address</b></td>
						    <td class="padding-l20px"><input type="text" class="border-trans" readonly="readonly" value="${meter.modemPort}"/></td>
						</tr>
						 -->
                    </table>
                </div>

                <div id="meterDefaultInfoEdit" style="display:none">
                        <table>
                        <tr>
						    <td class="withinput"><b><fmt:message key="aimir.mbus.address"/></b></td>
						    <td class="padding-l20px"><input type="text" id="modemPort" name="modemPort"  class="width-140px"/>                                    <c:choose>
                                    <c:when test="${not empty meter  && not empty meter.modemPort}">
                                       <input type="hidden" id="modemPortHidden" value="${meter.modemPort}"/>
                                    </c:when>
                                    <c:otherwise>
                                       <input type="hidden" id="modemPortHidden" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
						    </td>
						</tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.vendor"/></b><font color="red">*</font></td>
                            <td class="padding-l20px">
                                <select name="select" id="deviceVendor" onChange="javascript:getModelListByVendor(0);" class="width-140px"></select>
                                <c:choose>
                                    <c:when test="${not empty meter && not empty meter.model && not empty meter.model.deviceVendor}">
                                        <input type="hidden" id="deviceVendorHidden" name="model.deviceVendor.id" value="${meter.model.deviceVendor.id}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="deviceVendorHidden" name="model.deviceVendor.id" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.model"/></b><font color="red">*</font></td>
                            <td class="padding-l20px">
                                <select name="select" id="modelId" class="width-140px"></select>
                                <c:choose>
                                    <c:when test="${not empty meter && not empty meter.model}">
                                        <input type="hidden" id="modelIdHidden" name="model.id" value="${meter.model.id}" class="width-140px"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="modelIdHidden" name="model.id" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
						<tr>
                            <td class="withinput"><b><fmt:message key="aimir.sw.version"/></b></td>
                            <td class="padding-l20px"><input type="text" id="swVersion" name="swVersion" class="width-140px"/>
                                <c:choose>
                                    <c:when test="${not empty meter  && not empty meter.swVersion  }">
                                       <input type="hidden" id="swVersionHidden" value="${meter.swVersion}"/>
                                    </c:when>
                                    <c:otherwise>
                                       <input type="hidden" id="swVersionHidden" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        
						<tr>
                            <td class="withinput"><b><fmt:message key="aimir.fw.hwversion"/></b></td>
                            <td class="padding-l20px"><input type="text" id="hwVersion" name="hwVersion" class="width-140px"/>
                                <c:choose>
                                    <c:when test="${not empty meter  && not empty meter.hwVersion  }">
                                       <input type="hidden" id="hwVersionHidden" value="${meter.hwVersion}"/>
                                    </c:when>
                                    <c:otherwise>
                                       <input type="hidden" id="hwVersionHidden" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
						
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.threshold"/></b></td>
                            <td class="padding-l20px"><input type="text" id="usageThreshold" name="usageThreshold" class="width-140px"/>
                                <c:choose>
                                    <c:when test="${not empty meter  && not empty meter.usageThreshold  }">
                                       <input type="hidden" id="usageThresholdHidden" value="${meter.usageThreshold}"/>
                                    </c:when>
                                    <c:otherwise>
                                       <input type="hidden" id="usageThresholdHidden" value="" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>

                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.location"/></b><font color="red">*</font></td>
                            <td class="padding-l20px">
                                <input name="searchWord_3" id='searchWord_3' type="text" value="${meter.location.name}" class="width-140px"/>
                                <input type='hidden' id='infolocationId' name="location.id" value='' class="width-140px"></input>
                            </td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.installationdate"/></b></td>
                            <td class="padding-l20px">
                               <c:choose>
                                   <c:when test="${not empty meter}">
                                       <input type="text" id="installDate" name="installDate" class="width-140px" value="${meter.installDate}"/>
                                       <input type="hidden" id="installDateFormat" name="installDateFormat" value="${meter.installDateHidden}"/>
                                   </c:when>
                                   <c:otherwise>
                                       <input type="text" id="installDate" name="installDate" class="width-140px"/>
                                       <input type="hidden" id="installDateFormat" name="installDateFormat"/>
                                   </c:otherwise>
                               </c:choose>
                            </td>
                        </tr>
                    </table>
                    <div class="clear">
                        <div id="treeDivBOuter" class="tree-billing auto" style="display:none;">
                            <div id="treeDivB"></div>
                        </div>
                    </div>
                <!-- 장비기본정보_조회 (E) -->
                </div>
                <div id="meterDefaultInfoView">
                    <table class="wfree">
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.meterid"/></b></td>
                            <td class="padding-l20px"><input type="text" id="mdsIdView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.metertype"/></b></td>
                            <td class="padding-l20px"><input type="text" id="meterTypeView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.mcuid"/></b></td>
                            <td class="padding-l20px"><input type="text" id="mcuSysIdView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b>GS1</b></td>
                            <td class="padding-l20px"><input type="text" class="border-trans" readonly="readonly" value="${meter.gs1}"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.mbus.address"/></b></td>
                            <td class="padding-l20px"><input type="text" id="modemPortView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.vendor"/></b></td>
                            <td class="padding-l20px"><input type="text" id="deviceVendorView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.model"/></b></td>
                            <td class="padding-l20px"><input type="text" id="modelIdView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.sw.version"/></b></td>
                            <td class="padding-l20px"><input type="text" id="swVersionView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.fw.hwversion"/></b></td>
                            <td class="padding-l20px"><input type="text" id="hwVersionView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.threshold"/></b></td>
                            <td class="padding-l20px"><input type="text" id="usageThresholdView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <!-- location 정보 추가함 -->
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.location"/></b></td>
                            <td class="padding-l20px"><input type="text" id="locationView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                        <tr>
                            <td class="withinput"><b><fmt:message key="aimir.installationdate"/></b></td>
                            <td class="padding-l20px"><input type="text" id="installDateView" class="border-trans" readonly="readonly"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <input type="hidden" id="meterId" name="id" value=""/>
            <input type="hidden" id="supplierId" name="supplier.id" value=""/>
        </form>
    </div>
</div>
<div id="infoBtnList">
    <div id="btn" class="meter-info-btn2">
    <!-- 버튼이 화면에 보이는 역순으로 작성 -->

        <!-- 등록 -->
        <div id="meterDefaultInfoInsertButton" style="display:block;" >
            <ul><li><a href="javaScript:insertMeterInfo();" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
            <ul><li><a href="javaScript:changeMeterInfo('cancel');" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
        </div>

        <!-- 등록 / 변경 / 삭제 -->
        <div id="meterDefaultInfoViewButton" style="display:none">
            <ul><li><a href="javaScript:changeMeterInfo('edit');" class="on"><fmt:message key="aimir.update"/></a></li></ul>
            <ul><li><a href="javaScript:changeMeterInfo('insert');" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
            <ul><li><a href="javaScript:deleteMeterInfo();" class="on"><fmt:message key="aimir.button.delete"/></a></li></ul>
        </div>

        <!-- 변경 / 취소 -->
        <div id="meterDefaultInfoEditButton" style="display:none">
            <ul><li><a href="javaScript:updateMeterInfo();" class="on"><fmt:message key="aimir.ok"/></a></li></ul>
            <ul><li><a href="javaScript:changeMeterInfo('view');" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
        </div>

    </div>
</div>

<c:choose>
    <c:when test="${not empty meter}">
        <script>function infoDelay() {
                    if (infoJsonState == '4') {
                        clearInterval(infoDelayInterval);
                        changeMeterInfo("view");
                      }
                }
                infoDelayInterval = setInterval("infoDelay()",100);

                $("#meterLocForm :input[id='gpioX']").val( "${meter.gpioX}" );
                $("#meterLocForm :input[id='gpioY']").val( "${meter.gpioY}" );
                $("#meterLocForm :input[id='gpioZ']").val( "${meter.gpioZ}" );

                $("#meterLocForm :input[id='sysLocation']").val( "${meter.address}" );
        </script>



    </c:when>
    <c:otherwise>
        <script>function infoDelay(){
                    if(infoJsonState == '4'){
                        clearInterval(infoDelayInterval);
                        changeMeterInfo("insert");
                      }
                }

                infoDelayInterval = setInterval("infoDelay()",100);
        </script>
    </c:otherwise>
</c:choose>

<script>
   //locationTreeGoGo('treeDivB', 'searchWord_3', 'infolocationId', 'basicInfo');
   if (permitLocationId != null && permitLocationId != "") {
       locationTreeForPermitLocation('treeDivB', 'searchWord_3', 'infolocationId', permitLocationId, 'basicInfo');
   } else {
       locationTreeGoGo('treeDivB', 'searchWord_3', 'infolocationId', 'basicInfo');
   }
</script>

<!-- 1st Blue-gradation : 기본정보 (E) -->