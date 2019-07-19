<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta content='IE=8,chrome=1' http-equiv='X-UA-Compatible'/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" >
	var supplierObj;
	var sId;	
	var fmtMessage = new Array();	
	function init() {
		var supplierId;
		
		fmtMessage[0]  = '<fmt:message key="aimir.bems.facilityMgmt.add"/>'; //"추 가";
		fmtMessage[1]  = '<fmt:message key="aimir.bems.facilityMgmt.update"/>';        //"수 정";
		fmtMessage[2]  = '<fmt:message key="aimir.bemsfacilityMgmt.delete"/>';     //"삭제";
		fmtMessage[3]  = '<fmt:message key="aimir.buildingMgmt.move"/>';     //"이동";
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
		fmtMessage[25]  = '<fmt:message key="aimir.buildingMgmt.basicInfo"/>';     //"기본정보";
		fmtMessage[26]  = '<fmt:message key="aimir.buildingMgmt.correct"/>';     //"변경";
		fmtMessage[27]  = '<fmt:message key="aimir.buildingMgmt.supplierFirst"/>';     //"공급사를 먼저 등록해 주세요";
		fmtMessage[28]  = '<fmt:message key="aimir.msg.updatesuccess"/>'; //수정에 성공 하였습니다.
		fmtMessage[29]  = '<fmt:message key="aimir.msg.updatefail"/>'; //수정에 실패하였습니다.
		
		$.getJSON('${ctx}/common/getUserInfo.do',
			function(json) {
				if(json.supplierId != ""){
					supplierId = json.supplierId;
					if(supplierId != "") {
						sId= supplierId;
						getSupplier(supplierId);
					}
				}
		}); 

		changeTab('right');	   
	}
		// 메세지 처리 
	function getFmtMessage(){
		
		
		return fmtMessage;
	}
        
	function updateSupplier(){  

		var address = $(":input[name='address']").val();
		var telno = $(":input[name='telno']").val();
		var area = $(":input[name='area']").val();
		var descr = $(":input[name='descr']").val();			
	
		$.ajax({
			type: 'POST', 
			url: '${ctx}/gadget/system/bems/updateSupplier.do', 
			data: {supplierId: sId, address: address,telno: telno,area: area,descr: descr}, 
			dataType: 'json', 
			success: function(data) {
				
				alert(fmtMessage[28]);
			},
			error: function() { 
				alert(fmtMessage[29]);
			}
		});
	}

	function refreshSubLocation(locationId){
		getFlexObject("SupplierContract").getEndDeviceLocation(locationId);
		getFlexObject("SupplierContract").getEndDeviceByLocationId(locationId);
		
	}		
		
	function refreshLocation(){
		getFlexObject("SupplierContract").getLocationSupplierContract(0);
		getFlexObject("SupplierEndDevice").getEndDeviceLocationCombo(sId);
	
	}

	function setAlert(message){
		alert(message);
	}

	function refreshLog(){
		getFlexObject("SupplyCapacityLog").getSupplyCapacity();
		
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
			innerHtml
			+= "<table>"
				+"<colgroup>"
				+"<col width='65px'/>" 
				+"<col width=''/>" 
				+"</colgroup>"
				+"<thead>"
				+"<tr>"
				+"<th class='tit' colspan='2'><span class='float_left Tbu_bold'><fmt:message key='aimir.buildingMgmt.basicInfo'/></span>"
				+"<span class='float_right'><em class='bems_button'><a href='javascript:updateSupplier()'><fmt:message key='aimir.update'/></a></em></span></th>"
				+"</tr>"
				+"</thead>"
				+"<tbody>"
				+"<tr><th></th><td style='display:none;'></td></tr>"
				+"<tr><th></th><td style='display:none;'></td></tr>"
				+"<tr>"
				+"<th><fmt:message key='aimir.address'/>:</th>"
				+"<td><input type='text' name='address' value='"+supplierObj.address+"' class='input_dashed'/></td>"
				+"</tr>"
				+"<tr>"
				+"<th><fmt:message key='aimir.tel.no'/>:</th>"
				+"<td><input type='text' name='telno' value='"+supplierObj.telno+"' class='input_dashed'/></td>"
				+"</tr>"
				+"<tr>"
				+"<th>"+fmtMessage[4]+":</th>"
				+"<td><input type='text' name='area' value='"+addCommas(supplierObj.area)+"㎡' class='input_dashed'/></td>"
				+"</tr>"
				+"<tr>"
				+"<th><fmt:message key='aimir.description'/>:</th>"
				+"<td><input type='text' name='descr' value='"+supplierObj.descr+"' class='input_dashed'/></td>"
				+"</tr>"
				+"</tbody>"
				+"</table>"
			$('#default').html(innerHtml);  
		}
	}
		// 공급사 reset
	function resetDefault() {
		var innerHtml = "";
		innerHtml += "<ul>" +
		"  <li style='width:100px;height:200px;font-weight:bold'></li>" +
		"</ul>";                                
		$('#default').html(innerHtml);
	}
	/* 탭 관리 */
	function changeTab(tabType){
        
        if("left" == tabType){
        	$('#right').show();
            $('#center').hide();
            $('#left').hide(); 
           
            $('#info').removeClass('info').addClass('info2');  
             
        }
        else if("right" == tabType){
        	$('#right').hide();
            $('#center').show();
            $('#left').show();  
            $('#info').removeClass('info2').addClass('info');                          
        }
              
    }
	</script>
</head>
<body onload="init();">
<div id="wrapper">
 <div id="bldgNavi">
		<!-- 검색 및 정보 (S) -->	
    	<div class="topAll">
			<div class="slide_right" id="right"><a href="javascript:changeTab('right')"><span></span></a></div>
			<div class="search slidesize" style="width: 147px !important; overflow-y:hidden !important;"  id="center">
				<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="150" id="SupplierLocationEx">
					<param name='wmode' value='transparent' />
					<param name="movie" value="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" />
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" width="100%" height="150" id="SupplierLocationOt">
                    <!--<![endif]-->
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object> 
            </div>
			<div class="slide_left" id="left"><a href="javascript:changeTab('left')"><span></span></a></div>
 
		<div id="info" class="info ml170">
			<div id="default"></div> 
   		</div>
   	</div>
  	<!-- 검색 및 정보 (E) -->	

   
    <!-- 빌딩관리 테이블 차트 들어가는 부분 (S) -->
    <div class="Bchart clear">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="140" id="SupplierContractEx">
			<param name='wmode' value='transparent'/>
			<param name="movie" value="${ctx}/flexapp/swf/bems/buildingMgmtMini.swf"/>
			<!--[if !IE]>-->
			<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/buildingMgmtMini.swf" width="100%" height="140" id="SupplierContractOt">
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