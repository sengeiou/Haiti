<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>

<!-- 2st Blue-gradation : 장비설치현황 (S) -->
<div class="headspace"><span><label class="check"><fmt:message key="aimir.equipstatistics"/></label></div>

<div class="box-bluegradation-meter">
	<div class="padding20px">
		<div class="floatleft" style="width:145px">
			<div class="picbox">
				<img src="../../uploadImg/default/meterDefaultImg.jpg" />
			</div>
			<div id="meterInstallImgPagingView" class="paging-installimage" style="z-index:10;"></div>
		</div>
		<div  id="meterInstallInfoEdit" class="w_auto" style="margin-left:165px">
			<!-- 장비설치정보_조회 (S) -->
			<table id="meterInstallInfoEditTable" class="wfree">
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.supplier.name"/></b></th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput" width="200px"><b><fmt:message key="aimir.customerid"/></b></th>
					<td class="padding-r20px"></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.customername"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.modemid"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.installProperty"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.resolution"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.ke"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.meter.transformerRatio"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.energymeter.ct"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b><fmt:message key="aimir.status"/></b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b>Weather</b></th>
					<td></td>
				</tr>
				<tr>	
					<th class="withinput" width="200px"><b>Reason For Installation Delay</b></th>
					<td></td>
				</tr>
			</table>
			<!-- 장비설치정보_조회 (E) -->
		</div>
	</div>
</div>






