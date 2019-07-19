<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>빌딩관리-Full</title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" >
	var supplierObj;
	var sId;
	var fmtMessage = new Array();
	function init() {
		var supplierId;
		$.getJSON('${ctx}/common/getUserInfo.do',
			function(json) {
				if(json.supplierId != ""){
					supplierId = json.supplierId;
					if(supplierId != "") {
						sId= supplierId;
						getSupplier(supplierId);  
						
					}	                        
				}
			}
		);
	}

		// 메세지 처리 
	function getFmtMessage(){ 
			
		fmtMessage[0]  = '<fmt:message key="aimir.contextItem.add"/>'; //"추 가";
		fmtMessage[1]  = '<fmt:message key="aimir.contextItem.update"/>';        //"수 정";
		fmtMessage[2]  = '<fmt:message key="aimir.contextItem.delete"/>';     //"삭제";
		fmtMessage[3]  = '<fmt:message key="aimir.contextItem.move"/>';     //"이동";
		fmtMessage[4] = '<fmt:message key="aimir.buildingMgmt.area"/>';                 // 면적
		fmtMessage[5]  = '<fmt:message key="aimir.buildingMgmt.unUsedEnvironment"/>';               // 사용할수 없는 환경입니다
		
		fmtMessage[6]  = '<fmt:message key="aimir.buildingMgmt.energyContract"/>';     //"에너지 계약";
		fmtMessage[7]  = '<fmt:message key="aimir.buildingMgmt.contractNumber"/>';     //"계약";
		fmtMessage[8]  = '<fmt:message key="aimir.supplier"/>';     //"공급사";
		fmtMessage[9]  = '<fmt:message key="aimir.buildingMgmt.kind"/>';     //"종";
		fmtMessage[10]  = '<fmt:message key="aimir.contract.demand.amount"/>';     //"용량";
		fmtMessage[11]  = '<fmt:message key="aimir.buildingMgmt.applyDate"/>';     //"적용일자";
		fmtMessage[12]  = '<fmt:message key="aimir.bems.facilityMgmt.location"/>';     //"위치";
		fmtMessage[13]  = '<fmt:message key="aimir.buildingMgmt.facilityMgmt"/>';     //"설비현황";
		fmtMessage[14]  = '<fmt:message key="aimir.bems.facilityMgmt.kind"/>';     //"종류";
		fmtMessage[15]  = '<fmt:message key="aimir.equipvendor"/>';     //"제조사";


		fmtMessage[16]  = '<fmt:message key="aimir.model"/>';     //"모델";
		fmtMessage[17]  = '<fmt:message key="aimir.name"/>';     //"이름";
		fmtMessage[18]  = '<fmt:message key="aimir.installdate"/>';     //"설치일";
		fmtMessage[19]  = '<fmt:message key="aimir.facilityMgmt.manufactureDate"/>';     //"제조일";
		fmtMessage[20]  = '<fmt:message key="aimir.facilityMgmt.powerConsumption"/>';     //"소비전력";
		fmtMessage[21]  = '<fmt:message key="aimir.facilityMgmt.control"/>';     //"제어";
		fmtMessage[22]  = '<fmt:message key="aimir.facilityMgmt.controller"/>';     //"제어기";
		fmtMessage[23]  = '<fmt:message key="aimir.buildingMgmt.energyContractHistory"/>';     //"에너지 계약 변경이력";
		fmtMessage[24]  = '<fmt:message key="aimir.bems.facilityMgmt.changeDate"/>';     //"변경일자";


		fmtMessage[25]  = '<fmt:message key="aimir.buildingMgmt.energyEfficiencyRating"/>';     //"에너지 효율 등급";
		
		fmtMessage[26]  = '<fmt:message key="aimir.buildingMgmt.1thenergyusage"/>';     //"단위면적당 1차에너지소요량(KWh/m2∙년)";
		
		fmtMessage[27]  = '<fmt:message key="aimir.buildingMgmt.supplierFirst"/>';     //"공급사를 먼저 등록해 주세요";
		
		fmtMessage[28]  = "1";     //"1등급";
		fmtMessage[29]  = "2";     //"2등급";
		fmtMessage[30]  = "3";     //"3등급";
		fmtMessage[31]  = "4";     //"4등급";
		fmtMessage[32]  = "5";     //"5등급";
		fmtMessage[33]  = '<fmt:message key="aimir.buildingMgmt.co2EmissionPerYear"/>';     //"CO2 배출량";
		fmtMessage[34]  = '<fmt:message key="aimir.locationUsage.emission"/>';     //"배출량";
		fmtMessage[35]  = '<fmt:message key="aimir.buildingMgmt.grade"/>';     //"등급";	
		fmtMessage[36]  = '<fmt:message key="aimir.msg.updatesuccess"/>'; //수정에 성공 하였습니다.
		fmtMessage[37]  = '<fmt:message key="aimir.msg.updatefail"/>'; //수정에 실패하였습니다.
		fmtMessage[38]  = '<fmt:message key="aimir.msg.insertsuccess"/>'; //등록에 성공 하였습니다.
		fmtMessage[39]  = '<fmt:message key="aimir.remove"/>';     //"삭제";
		return fmtMessage;
	}

	function refreshSubLocation(locationId){
		getFlexObject("SupplierContract").getEndDeviceLocation(locationId);
		getFlexObject("SupplierContract").getEndDeviceByLocationId(locationId);
		
	}		
		
	function refreshLocation(){
		getFlexObject("SupplierContract").getSupplierLocation(sId);
		getFlexObject("SupplierContract").getEndDeviceLocation(sId);
	
	}

	function refreshLog(){
		getFlexObject("SupplierContract").getSupplyCapacity();
		
	}

	function updateSupplier(){ 
		
		var address = $(":input[name='address']").val();
		var telno = $(":input[name='telno']").val();
		var area = $(":input[name='area']").val();
		var descr = $(":input[name='descr']").val();			
			
		$.ajax({
			type: 'POST', 
			url: '${ctx}/gadget/system/bems/updateSupplier.do', 
			data: {supplierId: sId,address: address,telno: telno,area: area,descr: descr}, 
			dataType: 'json', 
			success: function(data) { 
				alert(fmtMessage[36]);
			},
			error: function() { 
				alert(fmtMessage[37]);
			}
		});
	}
		
		// 공급사 선택 시 정보를 로딩한다.
	function getSupplier(supplierId){ 
		
		if (supplierId != null) {
			sId = supplierId;
			sLoad = true;
		}
		$.getJSON('${ctx}/gadget/system/getSupplierList.do', {supplierId: sId},
			function(json){
				supplierObj = json.supplier;					
				bindingDefault();
		});
	}

	function addCommas(nStr)
    {
        nStr += '';
        x = nStr.split('.');
        x1 = x[0];
        x2 = x.length > 1 ? '.' + x[1] : '';
        var rgx = /(\d+)(\d{3})/;
        while (rgx.test(x1)) {
            x1 = x1.replace(rgx, '$1' + ',' + '$2');
        }
        return x1 + x2;
    }
		
	function bindingDefault() {
		if (supplierObj == null) {
			resetDefault();
		}else {
				//			$('#supplierDefault').setForm(supplier);
var innerHtml = "";
			
			innerHtml += "<table width='340px'>"
			+"<colgroup><col width='25%' /><col width='' /></colgroup>"
			+"<tbody>"
			+"<tr><th style='text-align:right' class='pr20'><fmt:message key='aimir.address'/>:</td>"
			+"<td><input type='text' name='address' value='"+supplierObj.address+"'/></td></tr>"
			+"<tr><th style='text-align:right' class='pr20'><fmt:message key='aimir.tel.no'/>:</th>"
			+"<td><input type='text' name='telno' value='"+supplierObj.telno+"' /></td></tr>"
			+"<tr><th style='text-align:right' class='pr20'><fmt:message key='aimir.buildingMgmt.area'/>:</th>"
			+"<td><input type='text' name='area' value='"+addCommas(supplierObj.area)+"㎡'/></td></tr>"
 			+"<tr><th style='text-align:right' class='pr20'><fmt:message key='aimir.description'/>:</th>"
 			+"<td><input type='text' name='descr' value='"+supplierObj.descr+"'/></td></tr>"
 			+"</tbody>"
 			+"</table>"
			$('#default').html(innerHtml);  
			
		}
	}
		// 공급사 reset
	function resetDefault() {
		var innerHtml = "";
		innerHtml += "<ul>" +
		"  <li></li>" +
		"</ul>";                                
		$('#default').html(innerHtml);
	}		

	</script>
</head>
<body onload="init();" class="bg">

<div id="wrapper">

	<!-- 빌딩관리 탐색부분과 기본정보 (S) -->
	<div id="bm_max">
	
	<div class="wrap">
      <div class="lnb">
      	<div id="currDate"></div>
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="600" id="SupplierLocationEx">
		<param name='wmode' value='transparent' />
		<param name="movie" value="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" />
		<!--[if !IE]>-->
		<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" width="100%" height="600" id="SupplierLocationOt">
		<!--<![endif]-->
		<!--[if !IE]>-->
		</object>
		<!--<![endif]-->
		</object>
       </div>
     </div>
     
     <div id="bm_max_content" class="width_100">
     	<div class="sub_content mr20">
   			<ul class="header borderBottom pb5">
	            <li class="hLeft tit_default"><fmt:message key='aimir.buildingMgmt.basicInfo'/></li>
	            <li><em class="bems_button"><a href="javascript:updateSupplier()"><fmt:message key='aimir.update'/></a></em></li>
	            <li class="hRight"><span class="mt2"><div id="currDate"></div></span></li>
			</ul>
			<div class="sub_content">
   			<div id="default" class="info pt10"></div>
    		<div class="energyRate">
				<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="150" id="efficiencyEx">
				<param name='wmode' value='transparent' />
				<param name="movie" value="${ctx}/flexapp/swf/bems/bems_efficiency_chart.swf" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/bems_efficiency_chart.swf"  width="100%" height="150" id="efficiencyOt">
				<!--<![endif]-->
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
				</object>
           	</div>
           </div>
     	</div>
     </div>
 	
 	 <div id="bm_max_content" style="float:none;">
     	<div class="sub_content mr20">
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="400" id="SupplierContractEx">
			<param name='wmode' value='transparent' />
			<param name="movie" value="${ctx}/flexapp/swf/bems/bems_energyContract_grid.swf" />
			<!--[if !IE]>-->
			<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/bems_energyContract_grid.swf" width="100%" height="400" id="SupplierContractOt">
			<!--<![endif]-->
			<!--[if !IE]>-->
			</object>
			<!--<![endif]-->
			</object>
     	</div>
     </div>
     
     

		

</div>
</body>
</html>