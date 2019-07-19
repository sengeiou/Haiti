<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8">


var infoJsonState = 0;

        // 미터 타입
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.3.1'}
                , function (returnData){
                    $('#meterType').pureSelect(returnData.code);
                    $('#meterType').selectbox();    
                    infoJsonState++;
                });
        
        // 제조사 조회
        $.getJSON('${ctx}/gadget/system/vendorlist.do'
                , {'supplierId' : supplierId}
                , function (returnData){

                    $('#deviceVendor').noneSelect(returnData.deviceVendors);
                    $('#deviceVendor').selectbox();
                    infoJsonState++;
                });

        // 모델 초기화
        $('#modelId').emptySelect();
        $('#modelId').selectbox();

        getDeviceModelsByVenendorId();

        // HW.Ver
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.3.7'}
                , function (returnData){
                    $('#hwVersion').noneSelect(returnData.code);
                    $('#hwVersion').selectbox();
                    infoJsonState++;
                });
        // SW.Ver
        $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.3.6'}
                , function (returnData){
                    $('#swVersion').noneSelect(returnData.code);
                    $('#swVersion').selectbox();
                    infoJsonState++;
                });
</script>

<div class="headspace">
	<label class="check"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.button.basicinfo"/></label>
</div>

<div class="box-bluegradation-meter">
	<div class="padding20px">
        <form id="meterInfoFormEdit">
           <div class="floatleft" style="width:130px">
                <div class="picbox" id="meterDeiveImgEdit">                
                    <c:choose>
                        <c:when test="${not empty meter}">
                            <img src="../../${meter.model.image}" />
                        </c:when>
                        <c:otherwise>
                            <img src="../../uploadImg/default/meterDefaultImg.jpg" />
                        </c:otherwise>
                    </c:choose>
                 </div>
            </div>
            <div class="w_auto" style="margin-left:135px;">
                <!-- 장비기본정보_조회 (S) -->
                <div id="meterDefaultInfoEdit" style="display:none">
	                <table class="wfree">
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.meterid"/></b></td>
	                        <td><input type="text" id="mdsId" name="mdsId" class="width-140px"/>
	                            <c:choose>
	                                <c:when test="${not empty meter}">
	                                    <input type="hidden" id="mdsIdHidden" value="${meter.mdsId}" />
	                                </c:when>
	                                <c:otherwise>
	                                    <input type="hidden" id="mdsIdHidden" value="" />
	                                </c:otherwise>
	                            </c:choose>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.metertype"/></b></td>
	                        <td><select name="select" id="meterType" class="width-140px"></select>
	                            <c:choose>
	                                <c:when test="${not empty meter && not empty meter.meterType}">
	                                    <input type="hidden" id="meterTypeHidden" name="meterType.id" value="${meter.meterType.id}" />
	                                </c:when>
	                                <c:otherwise>
	                                    <input type="hidden" id="meterTypeHidden" name="meterType.id" value="" />
	                                </c:otherwise>
	                            </c:choose>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.mcuid"/></b></td>
	                        <td><input type="text"   id="mcuSysId" name="modem.mcu.sysid" class="width-140px"/>
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
	                    	<td class="withinput"><b><fmt:message key="aimir.vendor"/></b></td>
	                        <td>
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
	                    	<td class="withinput"><b><fmt:message key="aimir.model"/></b></td>
	                        <td>
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
	                        <td>
	                        	<select name="select" id="swVersion" class="width-140px"></select>
                                <c:choose>
                                    <c:when test="${not empty meter}">
                                        <input type="hidden" id="swVersionHidden" name="swVersion" value="${meter.swVersion}" class="width-140px"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="swVersionHidden" name="swVersion" class="width-140px"/>
                                    </c:otherwise>
                                </c:choose>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.fw.hwversion"/></b></td>
	                        <td>
	                        	<select name="select" id="hwVersion" class="width-140px"></select>
                                <c:choose>
                                    <c:when test="${not empty meter}">
                                        <input type="hidden" id="hwVersionHidden" name="hwVersion"value="${meter.hwVersion}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" id="hwVersionHidden" name="hwVersion"/>
                                    </c:otherwise>
                                </c:choose>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.location"/></b></td>
	                        <td>        
                                <input name="searchWord_3" id='searchWord_3' type="text" value="${meter.location.name}" class="width-140px"/>
                                <input type='hidden' id='infolocationId' name="location.id" value='' class="width-140px"></input>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.installdate"/></b></td>
	                        <td>
                               <c:choose>
                                   <c:when test="${not empty meter}">
                                       <input type="text" id="installDate" name="installDate" value="${meter.installDate}"/>
                                   </c:when>
                                   <c:otherwise>
                                       <input type="text" id="installDate" name="installDate" class="width-140px"/>
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
	                        <td><input type="text" id="mdsIdView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.metertype"/></b></td>
	                        <td><input type="text" id="meterTypeView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.mcuid"/></b></td>
	                        <td><input type="text" id="mcuSysIdView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.vendor"/></b></td>
	                        <td><input type="text" id="deviceVendorView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.model"/></b></td>
	                        <td><input type="text" id="modelIdView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.sw.version"/></b></td>
	                        <td><input type="text" id="swVersionView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.fw.hwversion"/></b></td>
	                        <td><input type="text" id="hwVersionView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <!-- location 정보 추가함 -->
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.location"/></b></td>
	                        <td><input type="text" id="locationView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                    <tr>
	                    	<td class="withinput"><b><fmt:message key="aimir.installdate"/></b></td>
	                        <td><input type="text" id="installDateView" class="border-trans" readonly="readonly"/></td>
	                    </tr>
	                </table>
                </div>
			</div>
			<input type="hidden" id="meterId" name="id" value=""/>
			<input type="hidden" id="supplierId" name="supplier.id" value=""/>
		</form>
	</div>
</div>
<div id="btn" class="meter-info-btn2">
<!-- 버튼이 화면에 보이는 역순으로 작성 -->

    <!-- 등록 -->
	<div id="meterDefaultInfoInsertButton" style="display:block;" >
		<ul><li><a href="javaScript:insertMeterInfo();" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
	</div>

    <!-- 등록 / 변경 / 삭제 -->
    <div id="meterDefaultInfoViewButton" style="display:none">
		<ul><li><a href="javaScript:changeMeterInfo('edit');" class="on"><fmt:message key="aimir.update"/></a></li></ul>
		<ul><li><a href="javaScript:changeMeterInfo('insert');" class="on"><fmt:message key="aimir.button.register"/></a></li></ul>
		<ul><li><a href="javaScript:deleteMeterInfo();"><fmt:message key="aimir.button.delete"/></a></li></ul>
    </div>

    <!-- 변경 / 취소 -->
    <div id="meterDefaultInfoEditButton" style="display:none">
		<ul><li><a href="javaScript:updateMeterInfo();" class="on"><fmt:message key="aimir.ok"/></a></li></ul>
		<ul><li><a href="javaScript:changeMeterInfo('view');"><fmt:message key="aimir.cancel"/></a></li></ul>
    </div>

</div>


<c:choose>
    <c:when test="${not empty meter}">
        <script>function infoDelay(){
		            if(infoJsonState == '4'){
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
   locationTreeGoGo('treeDivB', 'searchWord_3', 'infolocationId', 'basicInfo');
   
</script>

<!-- 1st Blue-gradation : 기본정보 (E) -->
