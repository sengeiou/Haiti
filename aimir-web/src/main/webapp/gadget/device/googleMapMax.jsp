<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.HashMap"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>AiMiR <fmt:message key='aimir.version' /> - MAP
</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8"></meta>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?key=${googleApiMapkey}&language=en"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/geoxml3.js"></script>
<script type="text/javascript">
	/**
	 * 유저 세션 정보 가져오기
	 */
	 var supplierId = "${supplierId}";
	var modemProtocolType = "";
	var modeType = "";

	// 장비의 통신상태를 담는 전역 변수
	var deviceStatus = "";

	// 좌표값 정보를 담는 전역 변수
	var gpioX = "";
	var gpioY = "";
	var gpioZ = "";
	
// 	// meter 클릭시, hops count 정보를 담는 전역 변수
// 	var countHops = "";
// 	// MCU에 물려있는 미터의 갯수 정보를 담는 전역 변수
// 	var countMeterWithRelativeMcu = "";
	
	$(document).ready(function () {
		$('#sLocation').selectbox();
		$('#sMsa').selectbox();
		}
	); 
	
	$.getJSON('${ctx}/common/getUserInfo.do', function(json) {
		if (json.supplierId != "") {
			supplierId = json.supplierId;
			loginId = json.loginId;
		}
	});

	$(function() {
		$('#deviceType').selectbox();
		locationTreeGoGo('treeDivA', 'searchWord', 'sLocationId');

		try {
			//DistanceWidget.prototype = new google.maps.MVCObject();
		} catch (err) {
			$("#map-canvas").text("" + err);
		}
	});

	function openExcelReport2() {
		var com = document.getElementById("ondemandTable");
		f.excelData.value = com.outerHTML;
		f.action = "excelView.jsp";
		f.target = "_blank";
		f.submit();
	}
	
	var infoWin = null;
	var clickFromTrigger = false;
	var infoWinPlaceMaker = null;
	function createMarker(placemark, doc) {
		var image;

		if ( placemark.name.indexOf("DCU")== 0 ){
			image = {
				  url: placemark.style.icon.url,
				//  size: new google.maps.Size(24, 24),
				  origin: new google.maps.Point(0, 0),
				  anchor: new google.maps.Point(12, 12),
				  scaledSize: new google.maps.Size(24, 24)
			};
		}
		else {
			image = {
					  url: placemark.style.icon.url,
					//  size: new google.maps.Size(24, 24),
					  origin: new google.maps.Point(0, 0),
					  anchor: new google.maps.Point(4.5, 4.5),
					  scaledSize: new google.maps.Size(9, 9)
				};
		}
			
	//	placemark.style.icon.anchor = new google.maps.Point(4.5, 4.5);
	   var markerOptions = {
//	     optimized: false,
		      position: placemark.latlng,
		      style:placemark.style,
		      title:placemark.name,
		      //icon:placemark.style.icon,
		      icon : image,
		      map: map
	    };

 	    // Create the marker on the map
 	    var marker = new google.maps.Marker(markerOptions);
	    marker.infoWindow = new google.maps.InfoWindow();
	    
	    // Event Listener On 'click'
	    google.maps.event.addListener(marker, 'click', function(E) 
	    {    
	        var description = "";
	        var contents= "";
	        var meterMarker = false;
	        
	        contents = placemark.description;
	        
	        // DCU PlaceMark
	        if ( placemark.name.indexOf("DCU")== 0 ){
	        	if ( placemark.mcu ){
					description = placemark.description;
					if ( mapKind == "location") // Location Map
	        			contents = makeMcuInfoWinContentsOnLocation(placemark.mcu.sysId, placemark.mcu.id, description, placemark.latlng.lat(), placemark.latlng.lng());
					else // MCU Map
						contents = makeMcuInfoWinContents(placemark.mcu.sysId, placemark.mcu.id,  description,  placemark.mcu.location , placemark.latlng.lat(),placemark.latlng.lng());
	        	}
	        }
	        // Meter PlaceMark
	        else if ( placemark.name.indexOf("Meter")== 0 ){
	        	meterMarker = true;
	        	if ( (currentClassName == "mcu" || // MCU Map ,searched by DCU
	        			currentClassName == "meter" || // MCU Map, searched by Meter
	        			currentClassName == null // Location Map
	        			)){
	        		if ( !placemark.meter || placemark.meter.id == ""){ // no modem meter(no <ExtendedData> tag)
	        			contents = placemark.description;
	        		}
	        		else {// Create infoWindow from <EtendedData><Data name="meter"> tag
	        			contents = makeMeterInfoWinContents(placemark);
	        			description = placemark.description;
	        		}
	        	}
	        	else if ( currentClassName == "modem" ){ // MCU Map, searched by Modem
	        		if (!placemark.modem ||  placemark.modem.id == ""){ //no modem meter(no <ExtendedData> tag)
	        			contents = placemark.description;
	        		}
	        		else { // Create infoWindow from <EtendedData><Data name="modem"> tag
	        			contents = makeModemInfoWinContents(placemark);
	        			description = makeModemBasicInfo(placemark);
	        		}
	        	}
	        }
//	        marker.infoWindow.mouseOver = false;
	  		marker.infoWindow.setContent(contents);
	  		marker.infoWindow.setPosition(marker.getPosition());
	  		if ( infoWin != null ){
//	  			infoWin.mouseOver = null;
	  			infoWin.close();
	  		}
	  		infoWin = marker.infoWindow;
	  		infoWinPlaceMaker = placemark;
	  		infoWin.open(map ,marker);
 	  		if ( clickFromTrigger ){ // Click Event From showMcuMap() or showLocationMap()
 	  			map.setCenter(marker.getPosition());
 	  			if ( placemark.name.indexOf("DCU")== 0 ){
 	  			//	map.setZoom(15);
 	  			}
 	  			else {
	 	  			map.setZoom(15);
 	  			}
 	  			clickFromTrigger = false;
 	  		}	  		
			$('#cmdResult').html("");
			$('#basicInfo').html(description);
	    });

	    
	    placemark.mapkind = mapKind; // Set mapKind("location" or "mcu")
	    placemark.marker = marker; // add marker to placemark ( to delete it later)
	    
	    // If marker is selected now, send click event to open infoWindow
	    if( currentClassName == "mcu"){ 
	    	if ( currentDeviceId && placemark.mcu && (currentDeviceId == placemark.mcu.id )){
	    		clickFromTrigger = true;
    	    	google.maps.event.trigger(placemark.marker, 'click');
	    	}
	    }
	    else if( currentClassName == "meter") {
	    	if ( currentDeviceId && placemark.meter && (currentDeviceId == placemark.meter.id )){
	    		clickFromTrigger = true;
	    	    google.maps.event.trigger(placemark.marker, 'click');
	    	} 
	    }
	    else if ( currentClassName == "modem") {
	    	if ( currentDeviceId && placemark.modem && ( currentDeviceId == placemark.modem.id )){
	    		clickFromTrigger = true;
    	    	google.maps.event.trigger(placemark.marker, 'click');
    		} 
	    }
	    
		return marker;
	  }

	function makeMcuInfoWinContents(sysId, mcuId,description, locName, lat,lng)
	{
		var content ='<b>DCU: ' + sysId + '</b></br>'
			+ '<em class="btn_bluegreen"><a href="javascript:getMcuInfo('
			+ mcuId + ')">Event Time</a></em>'
			+ '&nbsp;'
			+ '<em class="btn_bluegreen"><a href="javascript:getSignalQuality('
			+ mcuId + ')">Signal Quality</a></em>'
			+ '&nbsp;'
			+ '<em class="btn_bluegreen"><a href="javascript:getNMSInformation('
			+ mcuId + ')">Refresh</a></em></br>'
			+ '<a href="javascript:showDsoMap(\'mcu\',' + mcuId + ',\'' + locName + '\',\'\',' + lat + ',' + lng + ',0)" style="text-decoration: underline">'
			+ 'Location:' + locName + '</a>'
			+ '<br/>'
			+ description;
			return content;
	}
	
	function setMeterInfoWin(mdsId)
	{
		var placemark = infoWinPlaceMaker;
		if ( !placemark.meter || placemark.meter.id == ""){ 
			infoWin.setContent(placemark.description);
		}
		else {
			var contents = makeMeterInfoWinContents(placemark);
			infoWin.setContent(contents);
		}
		$('#cmdResult').html("");
		$('#basicInfo').html(placemark.description);
	}
	function setModemInfoWin(){
		//var name = placemark.modem.deviceSerial;
		var placemark = infoWinPlaceMaker;
		if (!placemark.modem ||  placemark.modem.id == ""){
			infoWin.setContent(placemark.description);
			$('#basicInfo').html(placemark.description);
		}
		else {
 			var contents = makeModemInfoWinContents(placemark);
 			infoWin.setContent(contents)
 			$('#basicInfo').html(makeModemBasicInfo(placemark));
		};
		$('#cmdResult').html("");
		
	}
	function makeMcuInfoWinContentsOnLocation(sysId ,mcuId, description, lat, lng)
	{
		var content ='<b>'
			+ '<a href="javascript:makeMcuMap(\'mcu\',' + mcuId + ',\'' + sysId + '\',' + lat + ',' + lng + ',0)" style="text-decoration: underline">'
			+ 'DCU: ' + sysId + '</a>'
			+ '<br/>'
			+ '<em class="btn_bluegreen"><a href="javascript:getMcuInfo('
			+ mcuId + ')">Event Time</a></em>'
			+ '&nbsp;'
			+ '<em class="btn_bluegreen"><a href="javascript:getSignalQuality('
			+ mcuId + ')">Signal Quality</a></em>'
			+ '&nbsp;'
			+ '<em class="btn_bluegreen"><a href="javascript:getNMSInformation('
			+ mcuId + ')">Refresh</a></em></br>'
			+ description;
			return content;
	}
	function makeMeterInfoWinContents(placemark)
	{
		var description = placemark.description;
		if (placemark.modem != null ){
			var replacestr = '<a href="javascript:setModemInfoWin(\'' + placemark.modem.deviceSerial +'\')" style="text-decoration: underline">Modem: $1</a>';
			description = description.replace(/Modem:\s(\w+)/g, replacestr);
		}
		var type = "SubGiga";
		if( placemark.modem && placemark.modem.type == "MMIU" && placemark.modem.protocol == "SMS")
			type = "MMIU";
		var content  = '<b>Meter: '
			+ placemark.meter.mdsId
			+ '</b><br/>'
			+ '<em class="btn_bluegreen"><a href="javascript:getMeterEventTime('
			+ placemark.meter.id + ')">Event Time</a></em>' 
			+ '&nbsp'
			+ '<em class="btn_bluegreen"><a href="javascript:cmdPing('
			+  placemark.modem.id + ')">Ping</a></em>'
			+ '&nbsp'	
			+ '<em class="btn_bluegreen"><a href="javascript:onDemand('
			+ placemark.meter.id + ',\'' + type + '\')">On-Demand Metering</a></em>'
			+'<br/>'
			+ description;
			return content;
	}

	function makeModemBasicInfo(placemark)
	{
		var name = placemark.modem.deviceSerial;
		var rssi = "</br>RSSI: ";
		if ( placemark.modem.rssi != undefined) rssi =  '</br>RSSI: ' + placemark.modem.rssi;
		var parent ="</br>Parent: ";
		var modemtype = "SubGiga";
		var plcInfo = "";
		if( placemark.modem && placemark.modem.type == "MMIU" && placemark.modem.protocol == "SMS")  modemtype = "MMIU";
		if( placemark.modem && placemark.modem.type == "PLCIU"){
			modemtype = "PLCIU";
			plcInfo = '<br/>LQI: ' + placemark.modem.lqi	
						+ '<br/>TMR: ' + placemark.modem.tmr
						+ '<br/>Modulation: ' + placemark.modem.modulation	
						+ '<br/>Band plan: ' + placemark.modem.bandPlan;	
		}
		
		if ( placemark.modem.parent != undefined ) parent =  '</br>Parent: ' + placemark.modem.parent ;
		var description = "<b>Device ID: </b>" + placemark.modem.deviceSerial  
							+ '</br>Meter ID: ' + placemark.meter.mdsId
							+ '</br>Status: ' + placemark.modem.status
							+ '</br>FW Ver: ' + placemark.modem.fwver
							+ '</br>GpioX: ' + placemark.modem.x 
							+ '<br/>GpioY: ' + placemark.modem.y
							+ rssi
							+ plcInfo
							+ parent
							+ '</br>';
		return description;
	}
	function makeModemInfoWinContents(placemark)
	{
		var contents = "";
		var name = placemark.modem.deviceSerial;
		var rssi = "</br>RSSI: ";
		if ( placemark.modem.rssi != undefined) rssi =  '</br>RSSI: ' + placemark.modem.rssi;
		var parent ="</br>Parent: ";
		var modemtype = "SubGiga";
		var plcInfo = "";
		if( placemark.modem && placemark.modem.type == "MMIU" && placemark.modem.protocol == "SMS")  modemtype = "MMIU";
		if( placemark.modem && placemark.modem.type == "PLCIU"){
			modemtype = "PLCIU";
			plcInfo = '<br/>LQI: ' + placemark.modem.lqi	
						+ '<br/>TMR: ' + placemark.modem.tmr
						+ '<br/>Modulation: ' + placemark.modem.modulation	
						+ '<br/>Band plan: ' + placemark.modem.bandPlan;	
		}
		
		if ( placemark.modem.parent != undefined ) parent =  '</br>Parent: ' + placemark.modem.parent ;
		var description = "<b>Device ID: </b>" + name + '</br>'
							+'<a href="javascript:setMeterInfoWin(\''+ placemark.meter.mdsId + '\')" style="text-decoration: underline">'
							+ 'Meter ID: ' + placemark.meter.mdsId +'</a>'
							+ '</br>Status: ' + placemark.modem.status
							+ '</br>FW Ver: ' + placemark.modem.fwver
							+ '</br>GpioX: ' + placemark.modem.x 
							+ '<br/>GpioY: ' + placemark.modem.y
							+ rssi
							+ plcInfo
							+ parent
							+ '</br>';
		if(modemtype == "MMIU") {
			contents =  '<b>Modem: ' + placemark.modem.deviceSerial + '</b>'+ '<br/>'
					+ '<em class="btn_bluegreen"><a href="javascript:getInfoToggle('
					+ placemark.modem.id
					+ ')"><fmt:message key="aimir.getInfo"/></a></em>'
					+ '&nbsp;'
					+ '<em class="btn_bluegreen"><a href="javascript:getMeterInfo('
					+ placemark.modem.id + ')">Meter Info</a></em>' 
					+ '&nbsp;'
					+ '<em class="btn_bluegreen"><a href="javascript:getMBB_ConnectionStatus('
					+ placemark.modem.id + ')">MBB Conn. Status</a></em>'
					+ '<br/>'
					+ description;
		} else {
			contents = '<b>Modem: '+ name + '</b>'	+ '<br/><em class="btn_bluegreen"><a href="javascript:';
			if("NAMR-C108SR" == placemark.modem.device_name || "NAMR-C105SR" == placemark.modem.device_name){
				contents += 'getNBPLCInfoToggle('	
			} else {
				contents += 'getInfoToggle('
			}
			contents += placemark.modem.id + ')"><fmt:message key="aimir.getInfo"/></a></em>'
					+ '&nbsp;'
					+ '<em class="btn_bluegreen"><a href="javascript:getMeterInfo('
					+ placemark.modem.id + ')">Meter Info</a></em>'
					+ '&nbsp;'
					+ '<em class="btn_bluegreen"><a href="javascript:cloneOTA('
					+ placemark.modem.id + ')">Clone OTA</a></em>' 
					+ '</br>'
					+ description;
		}
		return contents;
	}
	
	var cmdWin;
	function cmdMcu(id,sysId){
		var cmdLineWin;
       	var opts = "width=500px, height=650px, left=100px,"
       				+ " top=200px, resizable=no, status=no, location=no";

       	var obj = new Object();
       	var condition = new Array();

    	obj.mcuId = id;
    	obj.sysId = sysId;
    	obj.loginId = loginId;
    	obj.supplierId = supplierId ;

       	if (cmdWin) {
       		cmdWin.close();
       	}
       	cmdWin = window.open("${ctx}/gadget/device/googleMapMaxMcuCmdPopup.jsp"
       				,"DCU Command",
       				opts);
       	cmdWin.opener.obj = obj;
	}
	
	function cmdMeter(id,mdsId,modemId){
		var cmdLineWin;
       	var opts = "width=500px, height=650px, left=100px,"
       				+ " top=200px, resizable=no, status=no, location=no";

       	var obj = new Object();
       	var condition = new Array();

    	obj.meterId = id;
    	obj.mdsId = mdsId;
    	obj.loginId = loginId;
    	ibj.modemId = modemId;
    	obj.supplierId = supplierId ;

       	if (cmdWin) {
       		cmdWin.close();
       	}
       	cmdWin = window.open("${ctx}/gadget/device/googleMapMaxMeterCmdPopup.jsp"
       				,"Meter Command",
       				opts);
       	cmdWin.opener.obj = obj;
	}

    function getLocationList() {
    	$.getJSON('${ctx}/gadget/device/getRootLocationList.do', {
                'supplierId' : "${supplierID}"
            }, function(returnData) {
                $('#sLocation').noneSelect(returnData.locations);
                $('#sLocation').selectbox();
            });
    };
    
    function getMsaByLocationName() {
        var target = document.getElementById("sLocation");
        var locationName =target.options[target.selectedIndex].text;
        $.getJSON('${ctx}/gadget/device/getMsaListByLocationName.do', {
            'locationName' : locationName
        }, function(returnData) {
            $('#sMsa').noneSelect(returnData.msas);
            $('#sMsa').selectbox();
        });
    };
    
    if ($('#sVendor').val() != "")
    
    var locParser = null;
    var mapKind = null;
    function showLocationMap(locName){
    	var locationId = $('#sLocation').val();
        var target = document.getElementById("sLocation");
        var locationName =target.options[target.selectedIndex].text;
        if ( locName != "" )
        	locationName = locName;
        if ( locationName == "-"){
			Ext.Msg.alert('<fmt:message key="aimir.message"/>',
			'<fmt:message key="aimir.ebs.msg.validation.location"/>');
			return;
        }
        var smaTarget = document.getElementById("sMsa");
        var msa =  smaTarget.options[smaTarget.selectedIndex].text;
        if ( msa == ' ' || msa == '-' || msa == ''){ msa = '';}
        var checkmsa = msa.replace(/ /g, '').replace(/\//g, '');

   	    var kmlUrl = '${ctx}/kml/data/map_' + locationName + '_' + checkmsa + '.kml';
   	    currentClassName = null;
   	    currentDeviceId = null;
   	    clearMap();
   	    if ( locParser == null){
   	    	locParser = new geoXML3.parser({map : map, /*afterParse:S,*/
   	     		createMarker: createMarker, pmParseFn: parsePlacemark, suppressInfoWindows: true});
   	    }
   	    mapKind = "location";
   	 	locParser.parse(kmlUrl);
	}
    
	function showDsoMap(deviceClass, id, locName, msa, latitude, longitude,altitude){
		// Delete Marks and Lines
		clearMap();
		clickFromTrigger = true;
		// Road New kml file
		var checkmsa = msa.replace(/ /g, '').replace(/\//g, '');
   	    var kmlUrl = '${ctx}/kml/data/map_' + locName + '_' + checkmsa + '.kml';
   	    currentClassName = deviceClass;
   	    currentDeviceId = id;
		mapKind = "location";
   	 	if ( locParser == null ){
   	    	locParser = new geoXML3.parser({map : map, /*afterParse:S,*/
   	     		createMarker: createMarker, pmParseFn: parsePlacemark, suppressInfoWindows: true});
   	 	}
   	 	locParser.parse(kmlUrl);
   	 	
 		var latLng = new google.maps.LatLng(
 					latitude,longitude,altitude);
 		setTimeout(function() {
 			map.panTo(latLng);
 			map.setZoom(10);
 			}, 200);
 			//	map.setZoom(15);
 			//	map.setCenter(latLng); 
   	 	
	}
	function clearMap()
	{
		currentClassName = null;
		currentDeviceId = null;
		if ( infoWin )
			infoWin.close();
		
		//if ( $('#chkClearMarkersOnLoad').is(':checked')){
	   	    if ( locParser != null){
			    Ext.each(locParser.docs, function(doc){
			        Ext.each(doc.gpolylines, function(gpolylines){
			        	gpolylines.setMap(null);
			        });
			        Ext.each(doc.placemarks, function(placemark){
			            if (placemark.marker != undefined) {
			            	placemark.marker.setMap(null);
			            	placemark.marker.infoWindow.close();
			            }
			        });
			    });
	   	    }
			if ( McuParser != null ){
				
			    Ext.each(McuParser.docs, function(doc){
			        Ext.each(doc.gpolylines, function(gpolylines){
			        	gpolylines.setMap(null);
			        });
			        Ext.each(doc.placemarks, function(placemark){
			            if (placemark.marker != undefined) {
			            	placemark.marker.setMap(null);
			            	placemark.marker.infoWindow.close();
			            }
			        });
			    });
			}
		//}
	}

	
	function getSearchedList(){
		var location = $('#sLocation').val();
		var field = $('#deviceSearch').val();
		if ( ( location == null || location.length == 0) && 
			 ( field == null || field.length == 0 )){
			Ext.Msg.alert('<fmt:message key="aimir.message"/>',
					'<fmt:message key="aimir.msg.selectlocationorinputdevice"/>');
		}
		else {
	        var smaTarget = document.getElementById("sMsa");
	        var msa =  smaTarget.options[smaTarget.selectedIndex].text;
	        if ( msa == '-') msa = '';
	    	$.getJSON('${ctx}/gadget/device/getMapList.do', {
	            'class' : $('#deviceType').val(),
	            'locationID' :$('#sLocation').val(),
	            'msa' : msa,
	            'supplierID' :"${supplierID}",
	            'field' : $('#deviceSearch').val()
	        }, function(returnData) {
	        	showSearchedList2(returnData);
	        }); 
		}
	}
	function showSearchedList2(responseText, status) {
		var result = responseText.result;
		var className = responseText.className;
		var listItems = [];
		for (var i = 0; i < result.length; i++) {
			var node =  result[i];
			var address = node.address;
			var name = node.name;
			var id = node.id;
			var kmlFile = node.kmlFile;
			var coordinates = node.coordinates;
			
			var items = null;
			items = '<div class="googlemap-searchresult-row clear">';
			items += '<span class="graybold11pt count">';
			items += (i + 1);
			items += '</span>';
			var cvAddress = "<fmt:message key="aimir.noaddress"/>"
			if ( address != null && address != "" ){
				cvAddress = address.replace(/(^s*)|(s*$)/g, '');
			}
			
			if ( !kmlFile || kmlFile== ""){
				items += '<span class="gray11pt">';
				items += name;
				items += '</span>';
				items += '<span class="gray11pt between">-</span>';
				items += '<span class="gray11pt">';
				items += cvAddress;
				items += '</span>';
			}
			else if ( kmlFile == "DCU"){
				items += '<span class="gray11pt">';
				items += name;
				items += '</span>'
				items += '<span class="gray11pt between">-</span>';
				items += '<span class="lightgray11pt">';
				items += cvAddress;
				items += '</span>';
				items += '<span class="sm_btn" ><a href="javascript:;" onclick=makeMcuMap('
					+ '\'' + className +'\',' + 
					+ id + ',\'' 
					+ node.sysID +'\',\''
					+ node.latitude+'\',\''
					+ node.longitude + '\',\''
					+ node.altitude +'\')>';
				items += 'Map(' + node.sysID+ '(DCU))';
				items += '</a></span>';
			}
			else if ( kmlFile == "DSO"){
				 var checkmsa = node.msa.replace(/ /g, '').replace(/\//g, '');
				items += '<span class="gray11pt">';
				items += name;
				items += '</span>'
				items += '<span class="gray11pt between">-</span>';
				items += '<span class="lightgray11pt">';
				items += cvAddress;
				items += '</span>';
				items += '<span class="sm_btn" ><a href="javascript:;" onclick=showDsoMap('
					+ '\'' + className +'\',' + 
					+ id + ',\'' 
					+ node.location +'\',\''
					+ checkmsa + '\',\''
					+ node.latitude+'\',\''
					+ node.longitude + '\',\''
					+ node.altitude +'\')>';
				items += 'Map(' + node.location+ ':' + node.msa + '(MSA))';
				items += '</a></span>';
			}
		
			items += '</div><br />';
			listItems.push(items);
		}

		$('#lBox').empty();
		$('#lBox').append(listItems.join(''));
		
		Ext.Msg.hide();
	}

	var McuParser = null;

	var currentDeviceId = null;
	var currentClassName = null;
	
	function makeMcuMap(deviceClass, id, sysId, latitude, longitude,altitude){
		$.getJSON('${ctx}/gadget/device/makeMcuMap.do', {
			'sysId' : sysId
        }, function(returnData) {
        	showMcuMap(deviceClass, id, sysId, latitude, longitude,altitude)
        }); 
	}

	function showMcuMap(deviceClass, id, sysId, latitude, longitude,altitude){
		// Delete Marks and Lines
		clearMap();
		clickFromTrigger = false;
		// Road New kml file
   	    var kmlUrl = '${ctx}/kml/data/map_' + sysId + '.kml';
   	    currentClassName = deviceClass;
   	    currentDeviceId = id;
		mapKind = "mcu";
   	 	if ( McuParser == null ){
   	 	 	McuParser = new geoXML3.parser({map : map, /*afterParse:setZoom(deviceClass, id, sysId, latitude, longitude,altitude),*/
      	     createMarker: createMarker, pmParseFn: parsePlacemark,  suppressInfoWindows: true,zoom : false});
   	 	}
   	 	McuParser.parse(kmlUrl);
   	 	
 		var latLng = new google.maps.LatLng(
 					latitude,longitude,altitude);
 
 		 setTimeout(function() {
				map.setZoom(15);
 				map.setCenter(latLng);
 		 			}, 200);
	}
	
	function parsePlacemark (node, placemark) {
	    var extDataNodes = node.getElementsByTagName('ExtendedData');
	    if (!!extDataNodes && extDataNodes.length > 0) {
	        var dataNodes = extDataNodes[0].getElementsByTagName('Data');
	        for (var d = 0; d < dataNodes.length; d++) {
	            var dn    = dataNodes[d];
	            var name  = dn.getAttribute('name');
	            if (!name) continue;
	            var val   = geoXML3.nodeValue(dn.getElementsByTagName('value')[0]);
	            if ( name == 'mcu' && val != null){
	            	placemark.mcu =  JSON.parse(val);
	            }
	            if ( name == 'meter' && val != null){
	            	placemark.meter = JSON.parse(val);
	            }
	            if ( name == 'modem' && val != null){
	            	placemark.modem = JSON.parse(val);
	            }
	        }
	    }
	}
	var infoWin = null;

	var map;
	var infoWin;

	var poly = new google.maps.Polyline({
		strokeColor : '#FF0000',
		strokeOpacity : 1.0,
		strokeWeight : 1
	});

	var polygon;

	function getMcuInfo(mcuId) {
		var login_id = loginId;

		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : mcuId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/cmdEventTime.do',
			success : function(returnData) {
				if (!returnData.status) {
					Ext.Msg.hide();
					Ext.Msg.alert("[DCU Info]", returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
					return;
				}
				
				if (returnData.status.length > 0 && returnData.status == 'SUCCESS') {
					Ext.Msg.hide();
					Ext.Msg.alert("[DCU Info]", returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				} else {
					Ext.Msg.hide();
					Ext.Msg.alert("[DCU Info]", returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	}
	
	function getSignalQuality(mcuId) {
		var login_id = loginId;

		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : mcuId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getSignalQuality.do',
			error : function(){
				Ext.Msg.hide();
				$('#cmdResult').html("<b>Signal Quality: </b> FAIL" );
				return;
			},
			success : function(returnData) {
				if (!returnData.status) {
					Ext.Msg.hide();
					$('#cmdResult').html("<b>Signal Quality: </b>" + returnData.rtnStr);
					return;
				}
				
				if (returnData.status.length > 0 && returnData.status == 'SUCCESS') {
					Ext.Msg.hide();
					var title = "<b>Signal Quality: </b>";
					var description = 
						"</br>" + "</br>" 
						+ "<b>Signal Quality Description</b>" + "</br>"
						+ "0&nbsp;&nbsp;&nbsp; : -113dBm or less" + "</br>"
						+ "1&nbsp;&nbsp;&nbsp; : -111dBm" + "</br>"
						+ "2~30 : -109dBm ~ -53dBm / 2dBm per step" + "</br>"
						+ "31&nbsp;&nbsp; : (-51)dBm or greater" + "</br>"
						+ "99&nbsp;&nbsp; : not known or not detectable" + "</br>";
					
					$('#cmdResult').html(title + returnData.csq + description);
				} else {
					Ext.Msg.hide();
					// $('#cmdResult').html(returnData.rtnStr);
					$('#cmdResult').html("<b>Signal Quality: </b> Can't connect to DCU");
				}
			}
		});
	}
	
	function getNMSInformation(mcuId) {
		var login_id = loginId;

		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : mcuId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getNMSInformation.do',
			error : function(){
				Ext.Msg.hide();
				$('#cmdResult').html("<b>Refresh: </b> FAIL" );
				return;
			},
			success : function(returnData) {
				Ext.Msg.hide();
				var title = "<b>Refresh: </b>";
				$('#cmdResult').html(title + returnData.rtnStr);
			}
		});
	}
	/*
	function getStatus(targetId, device) {
		var login_id = loginId;
		$.ajax({
			type : "POST",
			data : {
				'target' : targetId,
				'device' : device
			},
			dataType : "json",
			async : false,
			url : '${ctx}/gadget/device/command/cmdDevieceStatus.do',
			success : function(returnData) {
				deviceStatus = returnData.deviceStatus;
			}
		});
	}

	function getModemProperties(modemId) {
		$.ajax({
			type : "POST",
			data : {
				'modemId' : modemId
			},
			dataType : "json",
			async : false,
			url : '${ctx}/gadget/device/getModemProperties.do',
			success : function(returnData) {
				modemProtocolType = returnData.protocolType;
				modemType = returnData.modemType;
			}
		});
	}
	
	function getMeterInfoForCountMeter(mcuId) {
		var login_id = loginId;
		// Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : mcuId,
				'loginId' : login_id
			},
			dataType : "json",
			async : false,
			url : '${ctx}/gadget/device/command/cmdCountMetersRelativeMcu.do',
			success : function(returnData) {
				//Ext.Msg.hide();
				//return returnData.rtnStr;
				countMeterWithRelativeMcu = returnData.rtnStr;
			}
		});
	}

	function getCordinate(targetId, device) {
		var login_id = loginId;
		// Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : targetId,
				'loginId' : login_id,
				'device' : device
			},
			dataType : "json",
			async : false,
			url : '${ctx}/gadget/device/command/cmdCoordinate.do',
			success : function(returnData) {
				//Ext.Msg.hide();
				gpioX = returnData.gpioX;
				gpioY = returnData.gpioY;
				gpioZ = returnData.gpioZ;
			}
		});
	}
*/
	// GetInfo Toggle (NMS)
	// 체크된 항목만 보여줍니다.
	var toggleFormPanel;
	var toggleWin;
	var toggle = new Array();
	toggle = [ false, false, false, false, false, false, false ];
	function getInfoToggle(modemId) {
		// 아직 안닫힌 경우 기존 창은 닫기
		if (Ext.getCmp('toggleWindow')) {
			Ext.getCmp('toggleWindow').close();
		}

		toggleFormPanel = new Ext.FormPanel({
			id : 'toggleform',
			defaultType : 'fieldset',
			bodyStyle : 'padding:1px 1px 1px 1px',
			frame : true,
			labelWidth : 100,
			items : [ {
				xtype : 'checkbox',
				id : 'RSSI',
				fieldLabel : 'RSSI',
				listeners : {
					change : function() {
						if (!toggle[0])
							toggle[0] = true;
						else
							toggle[0] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'lqi',
				fieldLabel : 'LQI',
				listeners : {
					change : function() {
						if (!toggle[1])
							toggle[1] = true;
						else
							toggle[1] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'etx',
				fieldLabel : 'ETX',
				listeners : {
					change : function() {
						if (!toggle[2])
							toggle[2] = true;
						else
							toggle[2] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'LastCommDate',
				fieldLabel : 'Last Comm. Time',
				listeners : {
					change : function() {
						if (!toggle[3])
							toggle[3] = true;
						else
							toggle[3] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'cpuUsage',
				fieldLabel : 'CPU Usage',
				listeners : {
					change : function() {
						if (!toggle[4])
							toggle[4] = true;
						else
							toggle[4] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'memoryUsage',
				fieldLabel : 'Memory Usage',
				listeners : {
					change : function() {
						if (!toggle[5])
							toggle[5] = true;
						else
							toggle[5] = false;
					}
				},
			}, {
				xtype : 'checkbox',
				id : 'txSize',
				fieldLabel : 'Total TX Size',
				listeners : {
					change : function() {
						if (!toggle[6])
							toggle[6] = true;
						else
							toggle[6] = false;
					}
				},
			}], // items
			buttons : [ {
				id : 'toggleSendBtn',
				text : 'Get Info',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						getModemInfo(toggle, modemId); //Get Info!
						toggle = [ false, false, false, false, false, false, false ];
					}
				}
			}, {
				text : 'Cancel',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						toggle = [ false, false, false, false, false, false, false ];
					}
				}
			} ]
		//buttons
		});

		var toggleWin = new Ext.Window({
			id : 'toggleWindow',
			title : 'Get Info',
			height : 270,
			width : 210,
			layout : 'fit',
			bodyStyle : 'padding: 10px 10px 10px 10px;',
			items : [ toggleFormPanel ],
		});

		toggleWin.show();
	}

	//해외수출형 과제) NB_PLC는 선택없이 바로 업데이트 요청	
	function getNBPLCInfoToggle(modemId) {
		getModemInfo(toggle, modemId); //Get Info!
	}
	
	function getModemInfo(toggle, modemId) {
		var login_id = loginId;
		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id,
				'chkRSSI' : toggle[0], // 체크된 항목만 보여줍니다.
				'chkLQI' : toggle[1],
				'chkETX' : toggle[2],
				'chkLastCommDate' : toggle[3],
				'chkCPU' : toggle[4],
				'chkMemory' : toggle[5],
				'chkTxSize' : toggle[6]
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/cmdNMSCoAP.do',
			error : function(){
				Ext.Msg.hide();
				$('#cmdResult').html("<b>Modem Info: </b> FAIL" );
				return;
			},
			success : function(returnData) {
				if (!returnData.status) {
					Ext.Msg.hide();
					Ext.Msg
							.alert("[Modem Info]", "[FAIL] "
									+ returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
					return;
				}
				if (returnData.status.length > 0
						&& returnData.status == 'SUCCESS') {
					Ext.Msg.hide();
					Ext.Msg.alert("[Modem Info]", returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				} else {
					Ext.Msg.hide();
					Ext.Msg
							.alert("[Modem Info]", "[FAIL] "
									+ returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	};

	function getMeterInfo(modemId) {
		var login_id = loginId;
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/meterInfo.do',
			error : function(){
				Ext.Msg.hide();
				$('#cmdResult').html("<b>Meter Info: </b> FAIL" );
				return;
			},
			success : function(returnData) {
				Ext.Msg.alert("[Meter Info]", returnData.rtnStr);
				$('#cmdResult').html(returnData.rtnStr);
			}
		});
	};
	
	function getMBB_ConnectionStatus(modemId) {
		if (Ext.getCmp('toggleWindow')) {
			Ext.getCmp('toggleWindow').close();
		}

		toggleFormPanel = new Ext.FormPanel({
			id : 'toggleform',
			defaultType : 'fieldset',
			bodyStyle : 'padding:1px 1px 1px 1px',
			frame : true,
			labelWidth : 150,
			items : [ {
				xtype : 'button',
				text : 'Send SMS',
				id : 'accessTechnology',
				fieldLabel : 'Access Technology',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						getAccessTechnology(modemId);
					}
				},
			}, {
				xtype : 'button',
				text : 'Send SMS',
				id : 'rssi',
				fieldLabel : 'RSSI',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						getRSSI(modemId);
					}
				},
			}, {
				xtype : 'button',
				text : 'Send SMS',
				id : 'useddApn',
				fieldLabel : 'used APN',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						getUseddAPN(modemId);
					}
				},
			}, {
				xtype : 'button',
				text : 'Send SMS',
				id : 'ipAddress',
				fieldLabel : 'IP Address',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('toggleWindow').close();
						getIpAddress(modemId);
					}
				},
			}]
		});

		var toggleWin = new Ext.Window({
			id : 'toggleWindow',
			title : 'Get Conn. Status',
			height : 180,
			width : 270,
			layout : 'fit',
			bodyStyle : 'padding: 10px 10px 10px 10px;',
			items : [ toggleFormPanel ],
		});

		toggleWin.show();
		
	}
	
	//------------- Clone OTA ------------------
	var cloneOTAFormPanel;
	var cloneOTAWin;
	function cloneOTA(modemId) {
		if (Ext.getCmp('cloneOTAWindow')) {
			Ext.getCmp('cloneOTAWindow').close();
		}

		if (! Ext.getCmp('cloneOTAWindow')) {
			cloneOTAFormPanel = new Ext.FormPanel({
				id : 'cloneOTAform',
				defaultType : 'fieldset',
				bodyStyle : 'padding:1px 1px 1px 1px',
				frame : true,
				labelWidth : 130,
				items : [ 
					{ 
						xtype: 'numberfield',
						fieldLabel: 'Activate Time (hour)',
						name:'cloneHour',
						 minValue: 0,
						id : 'cloneHourText',
						width:50
					}
	
				], // items
				buttons : [ {
					id : 'toggleSendBtn',
					text : 'Clone OTA',
					listeners : {
						click : function(btn, e) {
							var hours = Ext.getCmp('cloneHourText').getValue();
							if ( hours < 0 ){
								Ext.Msg.alert("Clone OTA", "Please input a number greater than 0");
								return;
							}
							Ext.getCmp('cloneOTAWindow').close();
							cmdCloneOTA(modemId, hours);
						}
					}
				}, {
					text : 'Cancel',
					listeners : {
						click : function(btn, e) {
							Ext.getCmp('cloneOTAWindow').close();
						}
					}
				} ]
			//buttons
			});
		}
		if (!Ext.getCmp('cloneOTAWindow')) {
			cloneOTAWin = new Ext.Window({
				id : 'cloneOTAWindow',
				title : 'Clone OTA',
				height : 130,
				width : 250,
				layout : 'fit',
				bodyStyle : 'padding: 10px 10px 10px 10px;',
				items : [ cloneOTAFormPanel ],
			});
		}

		cloneOTAWin.show();
	}
	
	function cmdCloneOTA(modemId, hour){
		var count = hour * 4;
		var login_id = loginId;

		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'modemId' : modemId,
				'loginId' : login_id,
				'count' : count
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/cmdSetCloneOnOff.do',
			success : function(returnData) {
				if (!returnData.status) {
					Ext.Msg.hide();
					Ext.Msg
							.alert("[Clone OTA]", "[FAIL] "
									+ returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
					return;
				}
				if (returnData.status.length > 0
						&& returnData.status == 'SUCCESS') {
					Ext.Msg.hide();
					Ext.Msg.alert("[Clone OTA]", returnData.rtnStr);
					$('#cmdResult').html("<b>Clone OTA</b>:" + returnData.rtnStr);
				} else {
					Ext.Msg.hide();
					Ext.Msg
							.alert("[Clone OTA]", "[FAIL] "
									+ returnData.rtnStr);
					$('#cmdResult').html("<b>Clone OTA</b>:" +returnData.rtnStr);
				}
			},
			error : function(){
				Ext.Msg.hide();
			}
		});
	};
	
	function getAccessTechnology(modemId) {
		var login_id = loginId;
		Ext.Msg.wait('Maximum 20 second...', '<fmt:message key="aimir.info"/>');
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getAccessTechnology.do', 
			success : function(returnData) {
				Ext.Msg.hide();
				if (returnData.status == "SUCCESS") {
					$('#cmdResult').html(returnData.rtnStr);
				} else {
					Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	}
	
	function getRSSI(modemId) {
		var login_id = loginId;
		Ext.Msg.wait('Maximum 20 second...', '<fmt:message key="aimir.info"/>');
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getRSSI.do', 
			success : function(returnData) {
				Ext.Msg.hide();
				if (returnData.status == "SUCCESS") {
					$('#cmdResult').html(returnData.rtnStr);
				} else {
					Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	}
	
	function getUseddAPN(modemId) {
		var login_id = loginId;
		Ext.Msg.wait('Maximum 20 second...', '<fmt:message key="aimir.info"/>');
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getUseddAPN.do', 
			success : function(returnData) {
				Ext.Msg.hide();
				if (returnData.status == "SUCCESS") {
					var apnInfo = "<b>APN NAME : </b>" + returnData.apnName + '<br/>'
					+ "<b>APN ID : </b>" + returnData.apnId + '<br/>'
					+ "<b>APN PW : </b>" + returnData.apnPw;
					
					$('#cmdResult').html(apnInfo);
				} else {
					Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	}

	function getIpAddress(modemId) {
		var login_id = loginId;
		Ext.Msg.wait('Maximum 20 second...', '<fmt:message key="aimir.info"/>');
		$.ajax({
			type : "POST",
			data : {
				'target' : modemId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/command/getIpAddress.do', 
			success : function(returnData) {
				Ext.Msg.hide();
				if (returnData.status == "SUCCESS") {
					$('#cmdResult').html(returnData.rtnStr);
				} else {
					Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.rtnStr);
					$('#cmdResult').html(returnData.rtnStr);
				}
			}
		});
	}
	
	function attachInfoAboutMeter(marker, id, name, address, meterId) {
		// Hops Count 데이터 반환 function 호출 - 전역변수 countHops로 결과를 반환한다. 
		getMeterInfoForCountHops(meterId);

		getCordinate(meterId, "Meter");
		getStatus(meterId, "Meter");

		var basicInfoData = "<b>Device ID : </b>" + name + '<br/>'
				+ "<b>Adrress : </b>" + address + '<br/>'
				+ "<b>Count Hops : </b>" + countHops + '<br/>'
				+ "<b>Status : </b>" + deviceStatus + '<br/>'
				+ "<b>GpioX : </b>" + gpioX + '<br/>' + "<b>GpioY : </b>"
				+ gpioY + '<br/>' + "<b>GpioZ : </b>" + gpioZ + '<br/>';

		google.maps.event.addListener(marker, 'click', function() {
			$('#cmdResult').html("");
			$('#basicInfo').html(basicInfoData);
		});

		var infowindow = new google.maps.InfoWindow(
				{
					content : '<b>'
							+ name
							+ '</b><br/>'
							+ '<em class="btn_bluegreen"><a href="javascript:getMeterEventTime('
							+ meterId + ')">Event Time</a></em>' + '<br/>'
							+ '<br/>' + address + '<br/>' + "Count Hops : "
							+ countHops + '<br/>' + "Status : " + deviceStatus
							+ '<br/>' + "GpioX : " + gpioX + '<br/>'
							+ "GpioY : " + gpioY + '<br/>' + "GpioZ : " + gpioZ
							+ '<br/>'
				});

		google.maps.event.addListener(marker, 'click', function() {
			if (infoWin) {
				infoWin.close();
			}
			infoWin = infowindow;
			infowindow.open(map, marker);
		});
	}

	///// Command Functions
	function getMeterEventTime(meterId) {
		var login_id = loginId;
		$.ajax({
			type : "POST",
			data : {
				'target' : meterId,
				'loginId' : login_id
			},
			dataType : "json",
			async : true,
			url : '${ctx}/gadget/device/meterEventTime.do',
			error : function(){
				Ext.Msg.hide();
				$('#cmdResult').html("<b>Event Time: </b> FAIL" );
				return;
			},
			success : function(returnData) {
				Ext.Msg.alert("[Event Time]", returnData.rtnStr);
				$('#cmdResult').html(returnData.rtnStr);
			}
		});
	}
	
	//------------- Ping ------------------
	function cmdPing(modemId){
		doPing(modemId, '64', '3');
	}

	function doPing(modemId, packetSize, count) {
		$('#cmdResult').html(""); 
		//비동기 설정
		$.ajaxSetup({
			async : true
		});

        Ext.Msg.wait('Waiting for response.', 'Wait !');
        
		$.ajax({
    	  url: '${ctx}/gadget/device/command/cmdModemPing.do',
    	  dataType: 'json',
    	  async: true,
    	  data: {'target' : modemId
              , 'loginId' : loginId
              , 'packetSize' : packetSize
              , 'count' : count
              , 'device' : 'meter'
              },
    	  success: function(returnData) {
    		  Ext.Msg.hide();
	   		  if(!returnData.status){
	   			 	Ext.Msg.alert("[Ping]", "FAIL");
	                 $('#cmdResult').html("<b>Ping</b> : FAIL");
	                    return;
	             }
	             if(returnData.status.length>0 && returnData.status!='SUCCESS'){
	            	 var errMsg = returnData.status;
	            	 if ( returnData.rtnStr.length > 0 ) errMsg += '</br>' + returnData.rtnStr;
	                 $('#cmdResult').html("<b>Ping</b>:" + errMsg);
	             } else {
	             	$('#cmdResult').html(""); 
	                 $('#cmdResult').html("<b>Ping</b>: " + returnData.jsonString); 
	             }
    	  },
    	  error: function(){
    		  Ext.Msg.hide();
    		  Ext.Msg.alert("[Ping]", "FAIL");
    		  $('#cmdResult').html("<b>Ping</b>: FAIL"); 
    	  }
    	});
	}
	
    //---------- On-Demand Metering ---------------------
    var onDemandFormPanel;
    var onDemandWin;
    function onDemand(meterId, type) {

		if (Ext.getCmp('onDemandWindow')) {
			Ext.getCmp('onDemandWindow').close();
		}
		
		var radioItems;
		if ( type == "MMIU"){
			radioItems = [
	             {boxLabel: 'MODEM', name: 'radio-type', inputValue:'MODEM'},
	             {boxLabel: 'METER', name: 'radio-type', inputValue:'METER', checked: true}
	         ];
		}
		else {
			radioItems = [
			      {boxLabel: 'DCU',   name: 'radio-type', inputValue:'MCU'},
			      {boxLabel: 'MODEM', name: 'radio-type', inputValue:'MODEM'},
			      {boxLabel: 'METER', name: 'radio-type', inputValue:'METER', checked: true}
			  ];
		}
        onDemandFormPanel = new Ext.FormPanel({
            id : 'onDemandForm',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            width : 400,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'targetTypeRadio',
                    fieldLabel : 'Type ',
                    items : radioItems,
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){

                        }
                    },
                }, //xtype : radio
                {
                    xtype: 'datefield',
                    anchor: '98%',
                    fieldLabel: 'From',
                    id : 'onDemandFromDate',
                    name: 'to_date',
                    format: 'd.m.y',
                    pickerOffset : '[10,10]',
//                    enableKeyEvents: true,
                    value: new Date()  // defaults to today
//                     ,
//                     renderer : function(value){ 
//                     	var val = value},
//                     listeners : {
//                         change: function(field) {
//                             var fi  = field;
//                         }
//                     }
                },
                {
                    xtype: 'datefield',
                    id : 'onDemandToDate',
                    anchor: '98%',
                    fieldLabel: 'To',
                    name: 'to_date',
                    format: 'd.m.y',
                    value: new Date()  // defaults to today
                }
            ], // items
			buttons : [ {
				id : 'onDemandButton',
				text : 'On-Demand',
				listeners : {
					click : function(btn, e) {
			            var type = Ext.getCmp('targetTypeRadio').getValue().inputValue;
			            var fromDate = Ext.getCmp('onDemandFromDate').getValue();
			            var toDate = Ext.getCmp('onDemandToDate').getValue();
						cmdOnDemand(meterId, type, fromDate, toDate); //Get Info!
					}
				}
			}, {
				text : 'Cancel',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('onDemandWindow').close();
					}
				}
			} ]

        });

		if ( !Ext.getCmp('onDemandWindow')){
				onDemandWin = new Ext.Window({
				id : 'onDemandWindow',
				title : 'On-Demand Metering',
				height : 200,
				width : 400,
				layout : 'fit',
				bodyStyle : 'padding: 10px 10px 10px 10px;',
				items : [ onDemandFormPanel ],
			});
		}
		onDemandWin.show();
    } //~function InputGrpOnDemand()
    
    var detailFormPanel;
    var detailWin;
    var detailContents ="";
    function showDetail(){
    	var winH = 600;
    	var winW = 700;
    	// $('#detail_dial').dialog({height:300, width:300});
    	 
 		if (Ext.getCmp('detailWindow')) {
			Ext.getCmp('detailWindow').close();
		}
 		if ( !Ext.getCmp('onDemandForm')){
	        detailFormPanel = new Ext.FormPanel({
	            id : 'onDemandForm',
	            defaultType : 'fieldset',
	            bodyStyle:'padding:1px 1px 1px 1px',
	            frame : true,
	            width : winW,
	            items : [
	                {
	                	autoScroll: 'true',
	                	height : winH-100,
	                	width : winW-50,
	                    xtype : 'panel',
	                    id : 'detail',
	                    html : detailContents,
	                 }
	        		],
	        	buttons : [ {
	    			id : 'detailButton',
	    			text : 'OK',
	    				listeners : {
	    					click : function(btn, e) {
	    						Ext.getCmp('detailWindow').close();
	    					}
	    				}
	        	}]
	        });
 		}
 		if (! Ext.getCmp('detailWindow')) {
			detailWin = new Ext.Window({
				id : 'detailWindow',
				title : 'On-Demand Metering',
				height : winH,
				width : winW,
				layout : 'fit',
				bodyStyle : 'padding: 10px 10px 10px 10px;',
				items : [ detailFormPanel],
			});
 		}
		detailWin.show();
    }
    
    function cmdOnDemand(meterId, ondemandType, from, to ) {
    	detailContents = "";
        var ftime = from.getFullYear().toString() 
        		  +	((from.getMonth()+1 < 10) ? "0" + (from.getMonth() + 1).toString() : (from.getMonth() + 1).toString())
              	  + ((from.getDate() < 10 )? "0" + from.getDate().toString() : from.getDate().toString())
              	  + '000000';
              	  
        var ttime = to.getFullYear().toString() 
        		+ ((to.getMonth() + 1  < 10) ? "0" + (to.getMonth() + 1).toString() : (to.getMonth() + 1).toString() )
                + ((to.getDate() < 10 ) ? "0" + to.getDate().toString() : to.getDate())
                + '235959';
        var startDateDt = new Date(from.getFullYear().toString(), from.getMonth(), from.getDate());
        var endDateDt = new Date(to.getFullYear().toString(), to.getMonth(), to.getDate());
        var diffDay = (endDateDt.getTime() - startDateDt.getTime()) / (1000 * 60 * 60 * 24) ;
        if ( diffDay < 0 ) {       
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.season.error'/>");
            return;
        } else if ( diffDay > 6 ){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.device.onlySearch7days'/>");
			return;
        }
        
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $
        .getJSON(
                '${ctx}/gadget/device/command/cmdOnDemand.do',
                {
                    'target' : meterId,
                    'loginId' : loginId,
                    'fromDate' :ftime,
                    'toDate' : ttime,
                    'type' : ondemandType	
                },
                function(returnData) {
                    //원래 동기방식으로 설정
                    $.ajaxSetup({
                        async : false
                    });
                    var result = "<b>On-Demand Metering : </b>" + returnData.rtnStr + '</br>';
                    Ext.Msg.hide();
                    if (returnData.rtnStr == 'Success') {
                        //Ext.Msg.alert('', 'Success!', null, null);
                        var detailHtml = returnData.detail;
                        var repstart = detailHtml.indexOf("<textarea name='excelData'>");
                        var repend = detailHtml.indexOf("<\/textarea>");
                        var repHtml = detailHtml.substring(0, repstart) + "<textarea name='excelData' style='display:none'>" + detailHtml.substring(repend);
                        document.getElementById("detail_view").innerHTML = returnData.detail;
                        detailContents = repHtml;
                        
                        if (returnData.detail != null
                                && returnData.detail != "<html></html>") {
                        	result += "<a href='#' onclick='showDetail();' class='btn_blue'><span><fmt:message key='aimir.report.mgmt.view'/></span></a>"
//                             if ($("#isMx2").val() == "true") {
//                                 $("#phasorDiagramTbl").show();
//                                 var svgLink = "<img src='${ctx}/gadget/device/viewPhasorDiagram.do"
//                                         + "?volAng_a="
//                                         + $("#volAng_a").val()
//                                         + "&volAng_b="
//                                         + $("#volAng_b").val()
//                                         + "&volAng_c="
//                                         + $("#volAng_c").val()
//                                         + "&curAng_a="
//                                         + $("#curAng_a").val()
//                                         + "&curAng_b="
//                                         + $("#curAng_b").val()
//                                         + "&curAng_c="
//                                         + $("#curAng_c").val() + "'>";
//                                 $("#phasorDiagram").html(svgLink);

//                                 var arrays = new Array();
//                                 arrays[0] = [ $("#volAng_a").val(),
//                                         $("#volAng_b").val(),
//                                         $("#volAng_c").val(),
//                                         $("#curAng_a").val(),
//                                         $("#curAng_b").val(),
//                                         $("#curAng_c").val() ];
//                                 viewPhaseAngle(arrays);
//                             }
                        }
                    } else {
                        Ext.Msg.alert('', returnData.rtnStr, null, null);
                    }
                   	$('#cmdResult').html(result);
                });
		if (Ext.getCmp('onDemandWindow')) {
			Ext.getCmp('onDemandWindow').close();
		}
    }
    
	function getMeterInfoForCountHops(meterId) {
		var login_id = loginId;
		Ext.Msg.wait('Waiting for response.', 'Wait !');
		$.ajax({
			type : "POST",
			data : {
				'target' : meterId,
				'loginId' : login_id
			},
			dataType : "json",
			async : false,
			url : '${ctx}/gadget/device/command/cmdCountHops.do',
			success : function(returnData) {
				Ext.Msg.hide();
				//return returnData.rtnStr;
				countHops = returnData.rtnStr;
			}
		});
	}

	function initialize() {
		var mapDiv = document.getElementById('map-canvas');
		map = new google.maps.Map(mapDiv, {
				center: new google.maps.LatLng(59, 10),	// Norway - Oslo
				zoom : 7,
				mapTypeId : google.maps.MapTypeId.ROADMAP,
				zoomControl:true,
				panControl: true
			}); 
			
		// Full Screen Button
		// map.controls[google.maps.ControlPosition.TOP_RIGHT].push(new FullScreenControl(map,"Enter Text","Exit Text"));
		map.controls[google.maps.ControlPosition.TOP_RIGHT].push(new FullScreenControl(map));
		poly.setMap(map);
		getLocationList();
	}

	// Removes the overlays from the map, but keeps them in the array
	function clearOverlays() {
		if (markersArray && markersArray != '') {
			for (i in markersArray) {
				if (markersArray[i] && markersArray[i].setMap)
					markersArray[i].setMap(null);
			}
			markersArray = [];
		}
		if (coordInfoWindowArray && coordInfoWindowArray != '') {
			for (i in coordInfoWindowArray) {
				if (coordInfoWindowArray[i] && coordInfoWindowArray[i].close)
					coordInfoWindowArray[i].close(map);
			}
			coordInfoWindowArray = [];
		}
		if (coordinatesArray && coordinatesArray != '') {
			var path = poly.getPath();
			var pathPolygon = polygon.getPath();

			for (i = path.length; i > -1; i--) {
				path.removeAt(i);
			}
			for (i = pathPolygon.length; i > -1; i--) {
				pathPolygon.removeAt(i);
			}
			coordinatesArray = [];
		}
	}



	google.maps.event.addDomListener(window, 'load', initialize);

	function FullScreenControl(map, enterFull, exitFull) {
	    if(typeof(enterFull)==='undefined') enterFull = "Full Screen";
	    if(typeof(exitFull)==='undefined') exitFull = "Exit Full Screen";
	     
	    var controlDiv = document.createElement("div");
	    controlDiv.className = "fullScreen";
	    controlDiv.index = 1;
	    controlDiv.style.padding = "10px";

	    // Set CSS for the control border.
	    var controlUI = document.createElement("div");
	    controlUI.style.backgroundColor = '#fff';
	    controlUI.style.border = '2px solid #fff';
	    controlUI.style.borderRadius = '3px';
	    controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';
	    controlUI.style.cursor = 'pointer';
	    controlUI.style.marginBottom = '22px';
	    controlUI.style.textAlign = 'center';
	    
	    controlDiv.appendChild(controlUI);
	 
	    // Set CSS for the control interior.
	    var controlText = document.createElement("div");
	    controlText.style.color = 'rgb(25,25,25)';
	    controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
	    controlText.style.fontSize = '13px';
	    controlText.style.lineHeight = '25px';
	    controlText.style.paddingLeft = '5px';
	    controlText.style.paddingRight = '5px';
	    
	    
	    controlText.innerHTML = "<strong>" + enterFull + "</strong>";
	    controlUI.appendChild(controlText);
	 
	    // set print CSS so the control is hidden
	    var head = document.getElementsByTagName("head")[0];
	    var newStyle = document.createElement("style");
	    newStyle.setAttribute("type", "text/css");
	    newStyle.setAttribute("media", "print");
	    var cssText = ".fullScreen { display: none;}";
	    var texNode = document.createTextNode(cssText);
	    try  {
	        newStyle.appendChild(texNode);
	    } catch (e) {
	        // IE8 hack
	        newStyle.styleSheet.cssText = cssText;
	    }
	    head.appendChild(newStyle);
	 
	    var fullScreen = false;
	    var interval;
	    var mapDiv = map.getDiv();
	    var divStyle = mapDiv.style;
	    if (mapDiv.runtimeStyle) {
	        divStyle = mapDiv.runtimeStyle;
	    }
	    var originalPos = divStyle.position;
	    var originalWidth = divStyle.width;
	    var originalHeight = divStyle.height;
	 
	    // IE8 hack
	    if (originalWidth === "") {
	        originalWidth = mapDiv.style.width;
	    }
	    if (originalHeight === "") {
	        originalHeight = mapDiv.style.height;
	    }
	 
	    var originalTop = divStyle.top;
	    var originalLeft = divStyle.left;
	    var originalZIndex = divStyle.zIndex;
	 
	    var bodyStyle = document.body.style;
	    if (document.body.runtimeStyle) {
	        bodyStyle = document.body.runtimeStyle;
	    }
	    var originalOverflow = bodyStyle.overflow;
	 
	    var goFullScreen = function () {
	        var center = map.getCenter();
	        
	        mapDiv.style.position = "fixed";
	        mapDiv.style.width = "100%";
	        mapDiv.style.height = "100%";
	        mapDiv.style.top = "0";
	        mapDiv.style.left = "0";
	        mapDiv.style.zIndex = "100";
	        document.body.style.overflow = "hidden";
	        controlText.innerHTML = "<strong>" + exitFull + "</strong>";
	        fullScreen = true;
	        google.maps.event.trigger(map, "resize");
	        map.setCenter(center);
	 
	        // this works around street view causing the map to disappear, which is caused by Google Maps setting the
	        // CSS position back to relative. There is no event triggered when Street View is shown hence the use of setInterval
	        interval = setInterval(function () {
	            //console.log("Triger");
	            if (mapDiv.style.position !== "fixed") {
	                mapDiv.style.position = "fixed";
	                google.maps.event.trigger(map, "resize");
	                 
	            }
	        }, 100);
	    };
	 
	    var exitFullScreen = function () {
	        var center = map.getCenter();
	        if (originalPos === "") {
	            mapDiv.style.position = "relative";
	        } else {
	            mapDiv.style.position = originalPos;
	        }
	        mapDiv.style.width = originalWidth;
	        mapDiv.style.height = originalHeight;
	        mapDiv.style.top = originalTop;
	        mapDiv.style.left = originalLeft;
	        mapDiv.style.zIndex = originalZIndex;
	        document.body.style.overflow = originalOverflow;
	        controlText.innerHTML = "<strong>" + enterFull + "</strong>";
	        fullScreen = false;
	        google.maps.event.trigger(map, "resize");
	        map.setCenter(center);
	        clearInterval(interval);
	    };
	 
	    // Setup the click event listener
	    google.maps.event.addDomListener(controlUI, "click", function () {
	        if (!fullScreen) {
	            goFullScreen();
	        } else {
	            exitFullScreen();
	        }
	    });
	 
	    return controlDiv;
	}
</script>
</head>
<body>
	<div class="gadget_body">
		<!-- <div id="map-canvas" class="blueline-3px googlemap-height-max width-auto"></div> --> 
		<div id="map-canvas" class="blueline-3px fullScreen" style="width:auto !important; height:600px;"></div>
		
		<div id="treeDivAOuter" class="tree-billing auto" style="display: none;">
			<div id="treeDivA"></div>
		</div>
		<div class="box-bluegradation2 googlemap-search" style="height: 220px;">
			<ul>
				<li class="padding">
					<span class="withinput"><fmt:message key="aimir.location" /></span>
					<span>
						<select id="sLocation" name="select" onChange="javascript:getMsaByLocationName();" style="width: 120px;">
                                <option value="">"Select Location"</option>
                        </select>        
                    </span>
					<span class="withinput"><fmt:message key="aimir.nms.msa" /></span>
					<span>
						<select id="sMsa" name="select" style="width: 240px;">
							<option value="">-</option>
						</select>
					</span>
					<span>
						<div id="btn">
							<ul><li><a href="javaScript:showLocationMap('');" class="on" id="showLocationBtn">View MSA Map</a></li></ul>
    						</div>
						</span>
						<div class="height5px"></div> 
					<form id="search" name="search" onsubmit="return false">
						<span class="select-gadgetsearchtype margin-r20"> <select
							id="deviceType" name="class" style="width: 100px;">
								<option value="mcu"><fmt:message key="aimir.mcu" /></option>
								<option value="modem"><fmt:message key="aimir.modem" /></option>
								<option value="meter"><fmt:message key="aimir.meter" /></option>
						</select>
						</span>
						<div class="search-s1 margin-r20">
							<ul style="width: 180px;">
								<li class="search-s1-input"><input id="deviceSearch" name="field" type="text"></li>
								<li class="search-s1-btn"><a id="allGadgetSearch"
									href="javascript:getSearchedList();"></a></li>
							</ul>
						</div> 
						<div class="lgnd_detail_div2 margin-r20" id="statusColorTable"
							style="height: auto; width: 550px; border: 1px solid #b4d3f0">
							<table cellpadding="0" cellspacing="0">
								<tbody>
									<td><label><b> &nbsp;MCU Status : </b></label></td>
									<td><span class="fChartColor_1">&nbsp;</span></td>
									<td><label>Normal &nbsp;</label></td>
									<td><span class="fChartColor_6">&nbsp;</span></td>
									<td><label>Unknown &nbsp;</label></td>
									<td><span class="fChartColor_7">&nbsp;</span></td>
									<td><label>Power Down &nbsp;</label></td>
									<td><span class="fChartColor_4">&nbsp;</span></td>
									<td><label>Comm. Error&nbsp;</label></td>
									<td><span class="fChartColor_5">&nbsp;</span></td>
									<td><label>Security Error</label></td>
								</tbody>
							</table>
						</div>
 <!--
						<div>
							<ul>
								<li>Clear Markers On Load<input
									type="checkbox" id="chkClearMarkersOnLoad" name="chkClearMarkersOnLoad" checked="true" /></li>
							</ul>
						</div>
  -->						
					</form>

					<div class="height5px"></div>
					<div id="lBox" class="blueline googlemap-searchresult width-49"
						style="float: left;"></div>
					<div class="blueline googlemap-searchresult width-49"
						style="float: right;">

						<div class="width-45"
							style="float: left; padding-left: 12px; height: 15%; color: #2da021;">
							<b>Basic Information</b>
						</div>
						<div class="width-49"
							style="float: left; height: 15%; color: #2da021;">
							<b>Command Result</b>
						</div>

						<!-- Basic Information -->
						<div id="basicInfo" class="googlemap-searchresult width-45"
							style="float: left; padding-left: 12px;"></div>
						<!-- Command Result -->
						<div id="cmdResult" class="googlemap-searchresult width-49"
							style="float: left;"></div>
					</div>
				</li>
			</ul>
		</div>
		<div id="detail_dial" title="ondemand">
              <div id="detail_view" style="overflow-y: auto"></div>
        </div>
	</div>
</body>
</html>