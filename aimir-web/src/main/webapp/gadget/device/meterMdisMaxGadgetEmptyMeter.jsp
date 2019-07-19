<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>

<!-- 2st Blue-gradation : 장비설치현황 (S) -->
<div class="headspace"><span><label class="check"><fmt:message key="aimir.equipstatistics"/></label></span><span class="nocheck orange11pt"> <fmt:message key="aimir.data.notexist"/></span></div>

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
					<th class="withinput"><fmt:message key="aimir.customerid"/></th>
					<td class="padding-r20px"></td>
					<th class="withinput"><fmt:message key="aimir.customername"/></th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput"><fmt:message key="aimir.supplier"/></th>
					<td class="padding-r20px"></td>
					<th class="withinput"><fmt:message key="aimir.supplier.name"/></th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<th class="withinput">&nbsp;</th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<td class="withinput">&nbsp;</td>
					<td></td>
				</tr>
				<tr>
					<th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<td class="graybold11pt withinput">&nbsp;</td>
					<td></td>
				</tr>
				<tr><th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<th class="withinput">&nbsp;</th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<th class="withinput">&nbsp;</th>
					<td></td>
				</tr>
				<tr>
					<th class="withinput">&nbsp;</th>
					<td class="padding-r20px"></td>
					<td class="withinput">&nbsp;</td>
					<td></td>
				</tr>
			</table>
               <!-- 장비설치정보_조회 (E) -->
		</div>
	</div>
</div>






