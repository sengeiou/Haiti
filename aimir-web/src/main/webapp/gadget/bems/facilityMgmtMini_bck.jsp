<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" >
		var supplierObj;
		var sId;
		var fmtMessage = new Array();
		
		
		function init() {
			fmtMessage[0]  = "설비수";   //설비수;
			fmtMessage[1]  = "운전";     //운전;
			fmtMessage[2]  = "정지";     //정지;
			fmtMessage[3]  = "모름";     //모름;
			fmtMessage[4]  = "위치";     // 위치

			fmtMessage[5]  = "종류";     // 종류			
			fmtMessage[6]  = "설비명";    //설비명;
			fmtMessage[7]  = "상태";      //상태;
			fmtMessage[8]  = "전기";      //전기;
			fmtMessage[9]  = "수도";      //수도;
			
			fmtMessage[10]  = "가스";     //가스;			
			fmtMessage[11]  = "열량";     //열량;
			fmtMessage[12]  = "일시";     //일시;
			fmtMessage[13]  = "제어";     //제어;
			fmtMessage[14]  = "이력";     //이력;		
			
			var supplierId;
			$.getJSON('${ctx}/common/getUserInfo.do',
	                function(json) {
	                    if(json.supplierId != ""){
	                        supplierId = json.supplierId;
	                        if(supplierId != "") {
	                        	sId= supplierId;
	            				getSupplier(supplierId);
	            			}
	                        $('#BASIC').show();
	                        $('#MGMT').hide();	                       	
	                    }
	                }
	        );			
		}
		
		function refreshSubLocation(locationId) {			
	    	if(document["SupplierFacilityControlOt"]==null){	
	    		window["EndDeviceChartEx"].getChartLocation(locationId);       
		        window["SupplierFacilityControlEx"].getEndDeviceByLocationId(locationId);
		    }else{
		    	document["EndDeviceChartOt"].getChartLocation(locationId);
		    	document["SupplierFacilityControlOt"].getEndDeviceByLocationId(locationId);
		    }
		}	
		
		function refreshLocation(){
			if(document["SupplierContractOt"]==null){
		        window["SupplierContractEx"].getContractLocation(sId);
		        window["SupplierEndDeviceEx"].getEndDeviceLocationCombo(sId);
		    }else{
		    	document["SupplierContractOt"].getContractLocation(sId);
		    	document["SupplierEndDeviceOt"].getEndDeviceLocationCombo(sId);
		    }
		}

		function refreshLog(){
			if(document["SupplyCapacityLogOt"]==null){
		        window["SupplyCapacityLogEx"].getContractLocation(sId);		        		        
		    }else{
		    	document["SupplyCapacityLogOt"].getContractLocation(sId);		    	
		    }
		}

		function refreshDevice(locationId){
			if(document["SupplierFacilityHistoryOt"]==null){
		        window["SupplierFacilityHistoryEx"].getEndDeviceLog();
		        window["EndDeviceChartEx"].getChartLocation(locationId);		        		        
		    }else{
		    	document["SupplierFacilityHistoryOt"].getEndDeviceLog();
		    	document["EndDeviceChartOt"].getChartLocation(locationId);		    	
		    }
		}

		function refreshCompareChart(endDeviceId){			
			if(document["SupplierFacilityMeteringOt"]==null){
		        window["SupplierFacilityMeteringEx"].getMeteringChart(endDeviceId);
		    }else{
		    	document["SupplierFacilityMeteringOt"].getMeteringChart(endDeviceId);
		    }
		}

		function getLocationFlex() {
	    	if(document["SupplierLocationOt"]==null){
		        window["SupplierLocationEx"].getLocation(sId);
		    }else{
		    	document["SupplierLocationOt"].getLocation(sId);
		    }
		}

		function getLocationComboFlex() {			
	    	if(document["SupplierContractOt"]==null){		    	
		        window["SupplierContractEx"].getLocationCombo(sId);
		    }else{
		    	document["SupplierContractOt"].getLocationCombo(sId);
		    }
		}
		// 공급사 선택 시 정보를 로딩한다.
		function updateSupplier(){ 
			var attribute = $(":input[name='attribute']").val();
			var address = $(":input[name='address']").val();
			var telno = $(":input[name='telno']").val();
			var descr = $(":input[name='descr']").val();			
			
			$.ajax({
			    type: 'POST', 
			    url: '${ctx}/gadget/system/bems/updateSupplier.do', 
			    data: {supplierId: sId,attribute: attribute,address: address,telno: telno,descr: descr}, 
			    dataType: 'json', 
			    success: function(data) { 
			        alert(data.result);
			    },
			    error: function() { 
			        alert('fail');
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
			});
		}
		
		    /* 탭 관리 */
		function changeTab(tabType){	            
			if("BASIC" == tabType){
				$('#sub_tab1').addClass('current');
				$('#sub_tab2').removeClass('current');
				$('#BASIC').show();
				$('#MGMT').hide();
			}
			else if("MGMT" == tabType){
				$('#sub_tab1').removeClass('current');
				$('#sub_tab2').addClass('current');	            	
				$('#BASIC').hide();
				$('#MGMT').show();
			}	                      
		}

		// 메세지 처리 
		function getFmtMessage(){			
			return fmtMessage;
		}
	</script>
</head>
<body onload="init();">
<div id="wrapper">
 <div id="container2"> 
	<div class="max_left">
		<div class="max_search">
           <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="220" id="SupplierLocationEx">
	           <param name='wmode' value='transparent' />
	           <param name="movie" value="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" />
	           <!--[if !IE]>-->
	           <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/BuildingMgmt.swf" width="100%" height="220" id="SupplierLocationOt">
	           <!--<![endif]-->
	           <!--[if !IE]>-->
	           </object>
	           <!--<![endif]-->
     		</object>
	   </div>
	</div>
   		
    <div id="max_right">    
    	<div class="all">
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="180" id="EndDeviceChartEx">
				<param name='wmode' value='transparent' />
				<param name="movie" value="${ctx}/flexapp/swf/bems/Bems_EndDevice_chart.swf" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/Bems_EndDevice_chart.swf" width="100%" height="220" id="EndDeviceChartOt">
				<!--<![endif]-->
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
			</object>
       </div>

        <!-- tab (S) -->
		<div class="bldg_sub_tab">
		  <ul>
		   <li><a href="javascript:changeTab('BASIC')" name="sub_tab1"  id="sub_tab1" class='current'>제어</a></li>
		   <li><a href="javascript:changeTab('MGMT');" name="sub_tab2" id="sub_tab2">이력</a></li>
		  </ul>
	  	</div>
		<!-- tab (E) -->

		
	    <div id="BASIC">
	        <div class="gridChart">
			    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="180" id="SupplierFacilityControlEx">
                <param name='wmode' value='transparent' />
                <param name="movie" value="${ctx}/flexapp/swf/bems/Bems_facility_control.swf" />
                <!--[if !IE]>-->
                <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/Bems_facility_control.swf" width="100%" height="220" id="SupplierFacilityControlOt">
                <!--<![endif]-->
                <!--[if !IE]>-->
                </object>
                <!--<![endif]-->
	            </object>
			</div>
		</div>		
		       
		<div id="MGMT">
		    <div class="gridChart"> 
				<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="180" id="SupplierFacilityHistoryEx">
				<param name='wmode' value='transparent' />
				<param name="movie" value="${ctx}/flexapp/swf/bems/Bems_facility_history.swf" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/Bems_facility_history.swf" width="100%" height="220" id="SupplierFacilityHistoryOt">
				<!--<![endif]-->
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
				</object>
			</div>
		</div>	
		
	    <div class="gridChart">        
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="180" id="SupplierFacilityMeteringEx">	                
				<param name='wmode' value='transparent' />
				<param name="movie" value="${ctx}/flexapp/swf/bems/Bems_EndDeviceMetering_chart.swf" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/Bems_EndDeviceMetering_chart.swf" width="100%" height="220" id="SupplierFacilityMeteringOt">
				<!--<![endif]-->
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
			</object>
		</div>
		
	</div>
	
 </div>
</div>
</body>
</html>