<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>AiMiR <fmt:message key='aimir.version'/> - MAP</title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"></meta>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?key=${googleApiMapkey}&language=en"></script>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
	<script type="text/javascript">
		$(function(){
	        $('#deviceType').selectbox();
	        locationTreeGoGo('treeDivA', 'searchWord', 'sLocationId');
	
	        try{
	            //DistanceWidget.prototype = new google.maps.MVCObject();
	           }catch(err) {
	                 $("#map-canvas").text("" + err);
	           }
	    });
	
		function keyEvtHandler(e){
			var event = e || window.event;
			var keycode = event.keyCode;
			if(keycode == 13){      //enter
				doSubmit();
			}else {
				return false;
			}
		}
		function doSubmit(){
			var options = {
				success : showSearchedList,
				url : '${ctx}/gadget/device/maplist.do',
				type : 'post',
				datatype : 'json'
			};
			$('#search').ajaxSubmit(options);
		}
		function showSearchedList(responseText, status) {
			var className = responseText.className;
			var placemark = responseText.result.feature.feature;
			var listItems = [];
			var RegExpHG = /\((.*?)\)/g;	// 괄호안 문자 제거
			var host = location.host.toLowerCase();
			for (var i=0; i<placemark.length; i++) {
				var address = placemark[i].description;
				var coordinates = placemark[i].geometry.coordinates;
				var key = placemark[i].name.replace(RegExpHG,'');
				var items = null;
				items = '<div class="googlemap-searchresult-row clear">';
				items += '<span class="graybold11pt count">';
				items += (i+1);
				items += '</span>';
				if (coordinates[0].altitude == "0" && coordinates[0].latitude == "0" && coordinates[0].longitude == "0") {
					if (address=="") {
						items += '<span class="gray11pt">';
						items += placemark[i].name;
						items += '</span>';
						items += '<span class="gray11pt between">-</span>';
						items += '<span class="lightgray11pt">';
						items += "<fmt:message key="aimir.noaddress"/>";
						items += '</span>';
					} else {
						var cvAddress = address.replace(/(^s*)|(s*$)/g, '');
						items += '<span class="gray11pt">';
						items += placemark[i].name;
						items += '</span>';
						items += '<span class="gray11pt between">-</span>';
						items += '<span class="gray11pt">';
						items += cvAddress;
						items += '</span>';
						items += '<span  class="sm_btn" ><a href="javascript:;" onclick="cvAddressToCoordinate(\''+className.toLowerCase()+'\',\''+key+'\',\''+cvAddress.toLowerCase()+'\')" >';
						//items += '<button type="button" class="sm_btn" >';
						items += '<fmt:message key="aimir.transform.coordinate"/>';
						//items += '</button>';
						items += '</a></span>';
					}
				} else {
					if (address=="") {
						items += '<span><a class="searchresult-matched radius3" href="javascript:;" onclick=showPoints("http://'+host+'${ctx}/${supplierID}/'+key+'/'+className.toLowerCase()+'.do")>';
						items += placemark[i].name;
						items += '</a></span>';
						items += '<span class="gray11pt between">-</span>';
						items += '<span class="gray11pt">';
						items += coordinates[0].latitude + ',' + coordinates[0].longitude + ',' + coordinates[0].altitude;
						items += '</span>';
						items += '<span class="sm_btn" ><a  href="javascript:;" onclick="cvCoordinateToAddress(\''+className.toLowerCase()+'\',\''+key+'\',\''+coordinates[0].latitude+'\',\''+coordinates[0].longitude+'\',\''+coordinates[0].altitude+'\')" >';
						//items += '<button type="button" class="sm_btn" >';
						items += '<fmt:message key="aimir.transform.address"/>';
						//items += '</button>';
						items += '</a></span>';
					} else {
						var cvAddress = address.replace(/(^s*)|(s*$)/g, '');
						items += '<a class="searchresult-matched radius3" href="javascript:;" onclick=showPoints("http://'+host+'${ctx}/${supplierID}/'+key+'/'+className.toLowerCase()+'.do")>';
						items += placemark[i].name;
						items += '</a>';
						items += '<span class="gray11pt between">-</span>';
						items += '<span class="gray11pt">';
						items += cvAddress;
						items += '</span>';
					}
				}
				items += '</div><br />';
				listItems.push(items);
			}
	
			$('#lBox').empty();
			$('#lBox').append(listItems.join(''));
		}
		function cvAddressToCoordinate(className, name, address) {
			var geocoder = new google.maps.Geocoder();
			if (geocoder) {
				geocoder.geocode({'address': address}, function(results, status) {
					if (status == google.maps.GeocoderStatus.OK) {
						var point = results[0].geometry.location;
						$.ajax({
							type:"GET",
							url:"http://"+location.host.toLowerCase()+"${ctx}/gadget/device/mapUpdate.do",
							data:{className:className, name:name, pointx:point.lng(), pointy:point.lat()},
							success:function(data, textStatus) {
								if (textStatus=="success") {
									doSubmit();
								} else {
									Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.value.error'/>");
								}
							}
						});
					} else {
						Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Geocode was not successful for the following reason:" + status);
					}
				});
			}
		}
		function cvCoordinateToAddress(className, name, latitude, longitude, altitude) {
			var latlng = new google.maps.LatLng(latitude, longitude);
			var geocoder = new google.maps.Geocoder();
			if (geocoder) {
				geocoder.geocode({'latLng': latlng}, function(results, status) {
					if (status == google.maps.GeocoderStatus.OK) {
						var address = results[0].formatted_address;
						if (address) {
							$.ajax({
								type:"POST",
								url:"http://"+location.host.toLowerCase()+"${ctx}/gadget/device/mapUpdateAddress.do",
								data:{className:className, name:name, address:address},
								success:function(data, textStatus) {
									if (textStatus=="success") {
										doSubmit();
									} else {
										Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.value.error'/>");
									}
								}
							});
						}
					} else {
						Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Geocoder failed due to: " + status);
					}
				});
			}
		}
		function showPoints(href){
			$.ajax({
				type:"GET",
				url:href,
				success:function(responseText) {
					clearOverlays();
					var kml = responseText.kml;
					var placemark = kml.feature.feature;
					var RegExpHG = /\((.*?)\)/g;	// 괄호안 문자 제거
					for (var i=0; i<placemark.length; i++) {
						var address = placemark[i].description;
						var coordinates = placemark[i].geometry.coordinates;
						var key = placemark[i].name.replace(RegExpHG,'');
						var image = placemark[i].styleUrl;
						
						if (coordinates.length == 1) {
							var latLng = new google.maps.LatLng(coordinates[0].latitude,coordinates[0].longitude,coordinates[0].altitude);
							var marker = new google.maps.Marker({
								position: latLng,
								title: placemark[i].name,
								map: map
							});
	
							// MCU
							if(image=='codiIcon')
								marker.setIcon('${ctx}/images/dcu_normal.png');
	
							// Modem
							if(image=='modemIcon')
								marker.setIcon('${ctx}/images/modem_normal.png');
							
							// Parent Modem
							if(image=='parent_modemIcon')
								marker.setIcon('${ctx}/images/parent_modem.png');
							
							// Meter
							if(image=='meterIcon')
								marker.setIcon('${ctx}/images/meter_normal.png');
							
							/* // MCU
							if(image=='codiIcon')
								marker.setIcon('${ctx}/images/dcu.png');
	
							// Modem
							if(image=='modemIcon')
								marker.setIcon('${ctx}/images/modem.png');
							
							// Parent Modem
							if(image=='parent_modemIcon')
								marker.setIcon('${ctx}/images/parent_modem.png');
							
							// Meter
							if(image=='meterIcon')
								marker.setIcon('${ctx}/images/meter.png'); */
							
							markersArray.push(marker);
							var coordInfoWindow = new google.maps.InfoWindow({
								content: '<b>' + placemark[i].name + '</b><br />' + address
							});
							coordInfoWindow.setPosition(latLng);
							coordInfoWindowArray.push(coordInfoWindow);
							map.setZoom(15);
							map.panTo(latLng);
						}
					}
					showOverlays();
				}
			});
		}
		var map;
		var markersArray = [];
		var coordInfoWindowArray = [];
		function initialize() {
			var mapDiv = document.getElementById('map-canvas');
			map = new google.maps.Map(mapDiv, {
				center: new google.maps.LatLng(59, 10),	// Norway - Oslo
				zoom: 7,
				mapTypeId: google.maps.MapTypeId.ROADMAP
			});
	
		}
	
		// Removes the overlays from the map, but keeps them in the array
		function clearOverlays() {
			if (markersArray && markersArray !='') {
				for (i in markersArray) {
	
					if(markersArray[i] && markersArray[i].setMap)
					markersArray[i].setMap(null);
				}
				markersArray = [];
			}
			if (coordInfoWindowArray && coordInfoWindowArray !='') {
				for (i in coordInfoWindowArray) {
					if(coordInfoWindowArray[i] && coordInfoWindowArray[i].close)
					coordInfoWindowArray[i].close(map);
				}
				coordInfoWindowArray = [];
			}
		}
	
		// Shows any overlays currently in the array
		function showOverlays() {
			if (markersArray) {
				for (i in markersArray) {
					if(markersArray[i] && markersArray[i].setMap){
					markersArray[i].setMap(map);
					//var distanceWidget = new DistanceWidget(map,markersArray[i]);
					}
				}
			}
			if (coordInfoWindowArray) {
				for (i in coordInfoWindowArray) {
					if(coordInfoWindowArray[i] && coordInfoWindowArray[i].close)
					coordInfoWindowArray[i].open(map);
				}
			}
		}
		google.maps.event.addDomListener(window, 'load', initialize);
	</script>
</head>
<body>

<div class="gadget_body">
	<div id="map-canvas" class="blueline-3px googlemap-height width-auto"></div>
	<!--  
	<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
			<div id="treeDivA"></div>
		</div>
	
	<div class="box-bluegradation2 googlemap-search">
		<ul><li class="padding">
			<form id="search" name="search" onsubmit="return false">
				<span><input type="hidden" name="supplierID" value="${supplierID}"/></span>
				<span><input type="hidden" id="locationName" name="locationName" value="${locationName}"/></span>
				
				<span>

							<input type="text" id="searchWord" name="searchWord" style="width:120px" value='<fmt:message key="aimir.board.location"/>'>
							<input type="hidden" id="sLocationId" name="locationID" value="-1" />

				</span>
				
				<span class="select-gadgetsearchtype">
					<select id="deviceType" name="class" style="width:80px;">
						<option value="mcu"><fmt:message key="aimir.mcu"/></option>
						<option value="modem"><fmt:message key="aimir.modem"/></option>
						<option value="meter"><fmt:message key="aimir.meter"/></option>
					</select>
				</span>
				<span>
					<div class="search-s1">
						<ul style="width:140px;">
							<li class="search-s1-input" ><input name="field" type="text" onclick="javascript:delAllGadgetTxt();"></li>
							<li class="search-s1-btn"><a id="allGadgetSearch" href="javascript:doSubmit();"></a></li>
						</ul>
					</div>
				</span>
			</form>

			<div class="height5px"></div>
			<div id="lBox" class="clear blueline googlemap-searchresult"></div>
		</li></ul>
	</div>
	-->
</div>

</body>
</html>
