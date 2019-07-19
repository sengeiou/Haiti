<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8">
    new AjaxUpload('meterInstallImgInsert', {
        action: '${ctx}/gadget/device/saveMeterInstallImgFileMdis.do',
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

        $.getJSON( '${ctx}/gadget/device/deleteMeterInstallImgMdis.do'
                , {  'meterId'                : meterId
                   , 'meterInstallImgId'      : $('#meterInstallImgId').val()
                  }
                , function(data) {
                                  installImgArray = data.installImgList;
                                  setInstallImgBar(1);
                                 }
                );

    };
</script>


<!-- 탭박스 (기본정보탭) (S) -->

<!-- 2st Blue-gradation : 장비설치현황 (S) -->
<div class="headspace clear">
	<span><label class="check"><fmt:message key="aimir.equipstatistics"/></label></span>
	<span class="nocheck orange11pt"> Energy </span>
</div>

<!--  ViewDiv -->
<div id="meterInstallInfoViewDiv">
	<div class="box-bluegradation-meter">
		<div class="padding20px">
			<div class="floatleft" style="width:145px">
				<div>
					<div id="meterInstallImgView" class="picbox"></div>
					<div id="meterInstallImgPagingView" class="paging-installimage overflow_hidden textalign-center"></div>
				</div>
			</div>
			<div  id="meterInstallInfoEdit" class="w_auto" style="margin-left:165px">
				<!-- 장비설치정보_조회 (S) -->
				<table id="meterInstallInfoEditTable" class="wfree">
					<tr>
						<th><fmt:message key="aimir.customerid"/></th>
						<td class="padding-r20px"><span class="input-fake">${meter.contract.customer.customerNo}</span></td>
						<th><fmt:message key="aimir.customername"/></th>
						<td><span class="input-fake">${meter.contract.customer.name}</span></td>
					</tr>
					<tr>
						<th><fmt:message key="aimir.supplier.name"/></th>
                        <td class="padding-r20px"><span class="input-fake">${meter.supplier.name}</span></td>
						<th></th>
                        <td></td>
					</tr>
					<tr>
						<th><fmt:message key="aimir.resolution"/></th>
						<td class="padding-r20px"><span class="input-fake">${meter.lpInterval}</span></td>
						<th><fmt:message key="aimir.ke"/></th>
						<td><span class="input-fake">${meter.pulseConstant}</span></td>
					</tr>
					<tr>
						<th><fmt:message key="aimir.meter.transformerRatio"/></th>
						<td><span class="input-fake">${meter.transformerRatio}</span></td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
				</table>
                <!-- 장비설치정보_조회 (E) -->
			</div>
		</div>
	</div>
	<div id="btn" class="meter-info-btn2">
    	<ul><li><a href="javaScript:changeInstallDivDisplay('edit');"  class="on"><fmt:message key="aimir.update"/></a></li></ul>
	</div>
	
  </div>

	
	<div id="meterInstallInfoEditDiv" style="display:none;">
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
				<div id="meterInstallInfoEdit" class="w_auto" style="margin-left:165px">
					<form id="meterInstallFormEdit">
		                <!-- 장비설치정보_조회 (S) -->
		                <div id="meterInstallInfoEdit">
			                <table class="wfree">
			                    <tr>
			                    	<th><fmt:message key="aimir.customerid"/><br></br></th>
			                        <td class="padding-r20px"><input type="text"   id="customerNo"   name="contract.customer.customerNo" value="${meter.contract.customer.customerNo}"/>
										<input type="hidden" id="customerNoHidden" value="${meter.contract.customer.customerNo}"/></td>
			                        <th><fmt:message key="aimir.customername" /></th>
			                       	<td>
			                        	<span><input type="text"   id="customerName" name="contract.customer.name" value="${meter.contract.customer.name}" /></span>
										<span><input type="hidden" id="customerNameHidden" value="${meter.contract.customer.name}" /></span>
										<span class="am_button"><a href="javascript:modifyContract();" ><fmt:message key="aimir.contractInfo"/> <fmt:message key="aimir.update"/> </a></span>
									</td>
			                    </tr>
			                    <tr>
			                    	<th><fmt:message key="aimir.supplier.name"/><br></br></th>
		                            <td class="padding-r20px">
		                            	<input type="text"   id="supplierName"       name="supplierId.name" value="${meter.supplier.name}"/>
		                                <input type="hidden" id="supplierNameHidden" value="${meter.supplier.name}"/>
		                            </td>
			                        <th></th>
		                            <td></td>
			                    </tr>
		
				                 <tr>
				                 	<th><fmt:message key="aimir.resolution"/></th>
			                        <td  class="padding-r20px">
			                        	<input type="text"   id="esolution"       name="lpInterval" value="${meter.lpInterval}"    />
			                            <input type="hidden" id="esolutionHidden" value="${meter.lpInterval}"    />
			                        </td>
			                        <th><fmt:message key="aimir.ke"/></th>
			                        <td>
			                        	<input type="text"    id="ke"           name = "pulseConstant" value="${meter.pulseConstant}"    />
			                            <input type="hidden"  id="keHidden"     value="${meter.pulseConstant}"    />
			                        </td>
			                    </tr>
			                    <tr>
			                    	<th><fmt:message key="aimir.meter.transformerRatio"/></th>
									<td class="padding-r20px">
										<input type="text" id="tR" name ="transformerRatio" value="${meter.transformerRatio}"	/>
									</td>
									<th></th>
									<td></td>
		                        </tr>
		                        <tr>
		                        	<th></th>
									<td></td>
									<th></th>
									<td></td>
		                        </tr>
		                        <tr>
		                        	<th></th>
									<td></td>
									<th></th>
									<td></td>
								</tr>
		                        <tr>
		                        	<th></th>
									<td></td>
									<th></th>
									<td></td>
		                        </tr>
		                        <tr>
		                        	<th></th>
									<td></td>
									<th></th>
									<td></td>
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

