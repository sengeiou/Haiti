<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8">
    new AjaxUpload('meterInstallImgInsert', {
        action: '${ctx}/gadget/device/saveMeterInstallImgFile.do',
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
        onComplete : function(file, response){

            insertMeterInstallImg(file , response);

        }
    });

    // 장비설치 > 설치 이미지 삭제
    var meterInstallImgDelete = function(){

        $.getJSON( '${ctx}/gadget/device/deleteMeterInstallImg.do'
                , {  'meterId'                : meterId
                   , 'meterInstallImgId'      : $('#meterInstallImgId').val()
                  }
                , function(data) {
                                  installImgArray = data.installImgList;
                                  setInstallImgBar(1);
                                 }
                );

    };
    var meterTypeName = "${meter.meterType.name}";

     $('#status').change( function () {
        var valstatusType = $('#status :selected').val();
        var textstatusType = $('#status :selected').text();
        $('#statusHidden').val(valstatusType);

    });

    $(document).ready(function() {
        // 수정권한 체크
        if (editAuth == "true") {
            $("#installUpdBtn").show();
        } else {
            $("#installUpdBtn").hide();
        }

        $('#esolution').val("${meter.lpInterval}");
        $('#esolution').selectbox();

        $.getJSON('${ctx}/gadget/device/meterStatus.do'
            ,{meterType: meterTypeName}
            , function (returnData){
                    var pure = [];
                    $.each(returnData.meterStatus, function(index, element) {
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

                        pure.push(option);
                    });
                    $('#status').pureSelect(pure);
                    //$('#status').noneSelect(returnData.meterStatus);
                    $('#status').val("${meter.meterStatus.id}");
                    $('#status').selectbox();

        });

     });
</script>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />



<!-- 탭박스 (기본정보탭) (S) -->
<!-- 2st Blue-gradation : 장비설치현황 (S) -->
<div class="headspace">
    <span><label class="check"><fmt:message key="aimir.equipstatistics"/></label></span>
</div>

    <!--  ViewDiv -->
    <div id="meterInstallInfoViewDiv">
        <div class="box-bluegradation-meter" >
            <div class="padding20px">
                <div class="floatleft" style="width:145px">
                    <div>
                        <div id="meterInstallImgView" class="picbox"></div>
                        <div id="meterInstallImgPagingView" class="paging-installimage overflow_hidden textalign-center"></div>
                    </div>
                </div>
                <div  id="meterInstallInfoEdit" class="w_auto" style="margin-left:165px">
                    <table id="meterInstallInfoEditTable" class="wfree" >
                        <tr>
                            <th class="withinput" width="150px"><b><fmt:message key="aimir.supplier.name" /></b></th>
                            <c:choose>
                                <c:when test="${not empty meter.supplier.name}">
                                    <td class="padding-r20px border-trans"><span class="input-fake">${meter.supplier.name}</span></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="padding-r20px"><input type="text" class="input-fake" value="-" readonly="readonly">
                                </c:otherwise>
                            </c:choose>
                            <th></th>
                            <td></td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.customerid"/></b></th>
                    <c:choose>
                        <c:when test="${not empty meter.contract.customer.customerNo}">
                            <td class="padding-r20px"><span class="input-fake">${meter.contract.customer.customerNo}</span></td>
                        </c:when>
                        <c:otherwise>
                            <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly">
                        </c:otherwise>
                    </c:choose>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.customername"/></b></th>
                    <c:choose>
                        <c:when test="${not empty meter.contract.customer.name}">
                            <td><span class="input-fake">${meter.contract.customer.name}</span></td>
                        </c:when>
                        <c:otherwise>
                            <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly">
                        </c:otherwise>
                    </c:choose>
                        </tr>
                    <c:choose>
                        <c:when test="${model == 'SENSUS_220C'}">
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.alarmstatus"/></b></th>
                            <c:choose>
                                <c:when test="${not empty meter.alarmStatus}">
                                    <td><span class="input-fake">${alarmStatus}</span></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly">
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        </c:when>
                    </c:choose>
                        <tr>
                            <th width="80px"><b><fmt:message key="aimir.modemid"/></b></th>
                            <c:choose>
                                <c:when test="${not empty meter.modem}">
                            <td><span class="input-fake">${meter.modem.deviceSerial}</span></td>
                            </c:when>
                                <c:otherwise>
                                    <td class="padding-r20px"><input type="text"
                                        class="input-fake" value="-" readonly="readonly">
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                        <th class="withinput"><b><fmt:message key="aimir.installProperty"/></b></th>
                            <c:choose>
                                <c:when test="${not empty meter.installProperty}">
                                    <td class="padding-r20px"><span class="input-fake">${meter.installProperty}</span></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="padding-r20px"><input type="text"
                                        class="input-fake" value="-" readonly="readonly">
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.pulse.constant"/></b></th>
                                <c:choose>
                                    <c:when test="${not empty meter.pulseConstant}">
                                        <td class="padding-r20px"><span class="input-fake">${meter.pulseConstant}</span></td></c:when>
                                    <c:otherwise>
                                        <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly"/>
                                    </c:otherwise>
                                </c:choose>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.qmax"/></b></th>
                                <c:choose>
                                    <c:when test="${not empty meter.QMax}">
                                    <td class="padding-r20px"><span class="input-fake">${meter.QMax}</span></td></c:when>
                                    <c:otherwise>
                                        <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly"/>
                                    </c:otherwise>
                                </c:choose>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.ground.underover"/></b></th>
                                <c:choose>
                                    <c:when test="${not empty meter.underGround}">
                                      <td class="padding-r20px"><span class="input-fake">${meter.underGround}</span></td></c:when>
                                    <c:otherwise>
                                        <td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly"/>
                                    </c:otherwise>
                                </c:choose>
                            <th></th>
                            <td></td>
                        </tr>
                        <c:choose>
                            <c:when test="${meter.model.name == 'SENSUS_220C'}">
                                <tr>
                                    <td class="withinput"><b><fmt:message key="aimir.valveserial"/></b></td>
                                    <c:choose>
                                    	<c:when test="${not empty meter.valveSerial}">
                                    		<td class="padding-r20px"><input type="text" id="valveSerialView" value="${meter.valveSerial}" class="border-trans" readonly="readonly"/></td>
                                    	</c:when>
                                    	<c:otherwise>
                                    		<td class="padding-r20px"><input type="text"    class="input-fake" value="-" readonly="readonly"/>
                                    	</c:otherwise>
                                    </c:choose>
                                </tr>
                            </c:when>
                        </c:choose>
                        <tr>
                        <c:choose>
                        	<c:when test="${meter.model.name == 'SENSUS_220C'}">
                            	<th><b><fmt:message key="aimir.valvestatus" /></b></th>
                            </c:when>
                            <c:otherwise>
                            	<th><b><fmt:message key="aimir.status" /></b></th>
                            </c:otherwise>
                         </c:choose>
                         <c:choose>
                             <c:when test="${not empty meter.meterStatus.name}">
                                 <td><span class="input-fake">${meter.meterStatus.descr}</span></td>
                             </c:when>
                             <c:otherwise>
                                 <td class="padding-r20px"><input type="text"
                                     class="input-fake" value="-" readonly="readonly">
                             </c:otherwise>
                         </c:choose>
                         <th></th>
                         <td></td>
                        </tr>
                        <tr>
							<th width="150px"><b>Weather</b></th>
							<c:choose>
								<c:when test="${not empty meter.meterError}">
									<td class="padding-r20px"><span class="input-fake">${meter.meterError}</span></td>
								</c:when>
								<c:otherwise>
									<td class="padding-r20px"><input type="text" class="input-fake" value="-" readonly="readonly">
								</c:otherwise>
							</c:choose>
							<th></th>
							<td></td>
						</tr>
						
						<tr>
							<th width="150px"><b>Reason For Installation Delay</b></th>
							<c:choose>
								<c:when test="${not empty meter.meterCaution}">
									<td class="padding-r20px"><span class="input-fake">${meter.meterCaution}</span></td>
								</c:when>
								<c:otherwise>
									<td class="padding-r20px"><input type="text" class="input-fake" value="-" readonly="readonly">
								</c:otherwise>
							</c:choose>
							<th></th>
							<td></td>
						</tr>
                    </table>

                </div>
            </div>
        </div>
        <div id="installUpdBtn">
            <div id="btn" class="meter-info-btn2">
                <ul><li><a href="javaScript:changeInstallDivDisplay('edit');"  class="on"><fmt:message key="aimir.update"/></a></li></ul>
            </div>
        </div>
    </div>

        <!--수정 화면  -->
    <div id="meterInstallInfoEditDiv"  style="display:none;">
        <div class="box-bluegradation-meter">
            <div class="padding20px">
                <div class="floatleft" style="width:145px">
                    <div>
                        <div id="meterInstallImgEdit" class="picbox"></div>
                        <input type="hidden" id="meterInstallImgId" />
                        <div id="meterInstallImgPagingEdit" class="paging-installimage overflow_hidden textalign-center"></div>
                        <div class="textalign-center">
                            <em class="am_sm_button" id="meterInstallImgInsert"  style="cursor: pointer;"><fmt:message key="aimir.add"/></em>
                            <em class="am_sm_button" onClick="javascript:meterInstallImgDelete();" style="cursor: pointer;"><fmt:message key="aimir.button.delete"/></em>
                        </div>
                    </div>
                </div>
                <div  id="meterInstallInfoEdit" class="w_auto" style="margin-left:165px">
                    <form id="meterInstallFormEdit">
                        <!-- 장비설치정보_조회 (S) -->
                     <div id="meterInstallInfoEdit">
                        <table class="wfree">
                        <tr>
                                <th width="150px"><b><fmt:message key="aimir.supplier.name" /></b></th>
                                <td class="padding-r20px"><input type="text" id="supplierName" readonly="readonly" class="border-trans" name="supplier.name" value="${meter.supplier.name}" />
                                <input type="hidden" id="supplierNameHidden" value="${meter.supplier.name}" /></td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.customerid"/></b></th>
                            <td class="padding-r20px">
                                <input type="text"   id="customerNo"   readonly="readonly"  class="border-trans" name="contract.customer.customerNo" value="${meter.contract.customer.customerNo}"/>
                                <input type="hidden" id="customerNoHidden" value="${meter.contract.customer.customerNo}"/></td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.customername"/></b></th>
                            <td>
                                <input type="text"   id="customerName"  readonly="readonly"  class="border-trans" name="contract.customer.name" value="${meter.contract.customer.name}" />
                                <input type="hidden" id="customerNameHidden" value="${meter.contract.customer.name}" /></td>
                        </tr>
                       <c:choose>
                         <c:when test="${model == 'SENSUS_220C'}">
                         <tr>
                            <th class="withinput"><b><fmt:message key="aimir.alarmstatus"/></b></th>
                            <td>
                                <input type="text"   id="alarmStatus"  readonly="readonly"  class="border-trans" name="meter.alarmStatus" value="${alarmStatus}" />
                                <input type="hidden" id="alarmStatusHidden" value="${alarmStatus}" /></td>
                        </tr>
                        </c:when>
                       </c:choose>
                        <tr>
                           <th><b><fmt:message key="aimir.modemid" /></b></th>
                            <td>
                                <span><input type="text"   id="modemSysId" readonly="readonly"  class="border-trans"  name="modem.deviceSerial" value="${meter.modem.deviceSerial}" /></span>
                                <span><input type="hidden" id="modemSysIdHidden" value="${meter.modem.deviceSerial}" /></span>
                            </td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.installProperty"/></b></th>
                            <td class="padding-r20px">
                                <input class="textBox-width-140" type="text" id="tR" name ="installProperty" value="${meter.installProperty}" onblur="javaScript:getNumberValidCheck(value);"/>
                            </td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.pulse.constant"/></b></th>
                            <td class="padding-r20px">
	                            <input class="textBox-width-140" type="text"   id="pulseConstant"       value="${meter.pulseConstant}" name="pulseConstant"   onblur="javaScript:getNumberValidCheck(value);"/>
	                            <input type="hidden" id="pulseConstantHidden" value="${meter.pulseConstant}" />
                            </td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.qmax"/></b></th>
                            <td class="padding-r20px">
	                            <input class="textBox-width-140" type="text"   id="qMax"        value="${meter.QMax}" name="QMax"  />
	                            <input type="hidden" id="qMaxtHidden" value="${meter.QMax}" />
                            </td>
                        </tr>
                        <tr>
                            <th class="withinput"><b><fmt:message key="aimir.ground.underover"/></b></th>
                            <td class="padding-r20px">
	                            <input class="textBox-width-140" type="text"   id="underGround"       value="${meter.underGround}" name="underGround"  />
	                            <input type="hidden" id="underGroundHidden" value="${meter.underGround}" />
                            </td>
                            <th></th>
                            <td></td>
                        </tr>
                        <c:choose>
                        	<c:when test="${model == 'SENSUS_220C'}">
		                        <tr id="valveDiv">
		                            <td class="withinput"><b><fmt:message key="aimir.valveserial"/></b></td>
		                            <td class="padding-r20px"><input type="text" id="valveSerial" name="valveSerial" value="${meter.valveSerial }" class="width-140px"/></td>
		                        </tr>
                        	</c:when>
                        </c:choose>
                        <tr>
                        	<c:choose>
                        		<c:when test="${model == 'SENSUS_220C' }">
                        			<th width="80px"><b><fmt:message key="aimir.valvestatus"/></b></th>
                        		</c:when>
                        		<c:otherwise>
                        			<th width="80px"><b><fmt:message key="aimir.status"/></b></th>
                        		</c:otherwise>
                            </c:choose>
                                <td class="padding-r20px">
                                <select name="select" id="status" name="meterStatus.name" class="width-140px"></select>
                                <input type="hidden" id="statusHidden" name="meterStatus.id" value="${meter.meterStatus.id}" />
                                </td>
                            <th></th>
                            <td></td>
                        </tr>
                        <tr>
	                       	<th class="withinput"><b>Weather</b></th>
							<td class="padding-r20px">
								<input class="textBox-width-140" type="text" id="meterError" name ="meterError" value="${meter.meterError}"/>
							</td>
	                       </tr>
						<tr>
	                       	<th class="withinput"><b>Reason For Installation Delay</b></th>
							<td class="padding-r20px">
								<input class="textBox-width-140" type="text" id="meterCaution" name ="meterCaution" value="${meter.meterCaution}"/>
							</td>
                        </tr>
                    </table>
                        <input type="hidden" id="meterId"    name="id" value="${meter.id}"/>
                        </div>
                       <!-- 장비설치정보_조회 (E) -->
                    </form>
                </div>
            </div>
        </div>
        <div id="btn" class="meter-info-btn2">
            <!-- 버튼이 화면에 보이는 역순으로 작성 -->
            <ul><li><a href="javaScript:updateMeterInstallInfo();" class="on"><fmt:message key="aimir.ok"/></a></li></ul>
            <ul><li><a href="javaScript:changeInstallDivDisplay('view');" class="off"><fmt:message key="aimir.cancel"/></a></li></ul>
        </div>
    </div>


    <script>
        // 장비 설치현황 > 설치 이미지 조회
        getInstallImg();
    </script>