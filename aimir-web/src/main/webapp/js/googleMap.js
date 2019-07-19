//  * 화면 에서 설정
//  <script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.js"></script>

document.write("<script type='text/javascript' src='http://maps.google.com/maps/api/js?sensor=false'></script>");

// GoogleMap API 링크
// http://code.google.com/intl/ko-KR/apis/maps/documentation/javascript/


// 변수설정
var map;
var markersArray = [];
var coordInfoWindowArray = [];

// 외부에서 init할때 GoogleMap초기화 함수명
function googleMapInit(){
	//initialize();
	var mapDiv = document.getElementById('map-canvas');

	try{
		map = new google.maps.Map(mapDiv, {
			center: new google.maps.LatLng(37, 128),
			zoom: 7,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		});
	}catch(err) {
		$("#map-canvas").text("" + err);
		return false;
	}
	return true;
}

// map초기화
function initialize() {

	var mapDiv = document.getElementById('map-canvas');

	try{
		map = new google.maps.Map(mapDiv, {
			center: new google.maps.LatLng(37, 128),
			zoom: 7,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		});
	}catch(err) {
		$("#map-canvas").text("" + err);
	}
}

// Removes the overlays from the map, but keeps them in the array
// 지도상에 모든 표식 삭제
function clearOverlays() {
	
	if (markersArray && markersArray.length > 0){
		for (i =0 ; i < markersArray.length ; i++) {
			markersArray[i].setMap(null);
		}     
		markersArray = [];   
	} 
	
	if (coordInfoWindowArray && coordInfoWindowArray.length > 0) {
		for (i =0 ; i < coordInfoWindowArray.length ; i++) {
			coordInfoWindowArray[i].close(map);
		}
		coordInfoWindowArray = [];
	}
	

}

// Shows any overlays currently in the array
function showOverlays() {


	if (markersArray && markersArray.length > 0){
		for (i =0 ; i < markersArray.length ; i++) {
			markersArray[i].setMap(map);
		}
	}
	if (coordInfoWindowArray && coordInfoWindowArray.length > 0) {
		for (i =0 ; i < coordInfoWindowArray.length ; i++) {
			coordInfoWindowArray[i].open(map);
		}
	}
}


// 화면에 point 표기
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
							
							if(image=='codiIcon'){								
								marker.setIcon('/aimir-web/images/dcu.png');							
							}else if(image=='modemIcon')
								marker.setIcon('/aimir-web/images/modem.png');
							else
								marker.setIcon('/aimir-web/images/meter.png');
							
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
							alert("false");
						}
					}
				});
			} else {
				alert("Geocode was not successful for the following reason:" + status);
			}
		});
	}
}


// 주소를 변경하여, 화면에 출력함
function cvAddressToCoordinate(address , x_id, y_id, z_id) {
	var geocoder = new google.maps.Geocoder();
	if (geocoder) {
		geocoder.geocode({'address': address}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var point = results[0].geometry.location;
				
				
					if (!point) {
						$("#"+x_id).val(0.0);
						$("#"+y_id).val(0.0);
						$("#"+z_id).val(0.0);
						alert(address + " not found");
					} else {
						$("#"+x_id).val(point.	lng());
						$("#"+y_id).val(point.lat());
						$("#"+z_id).val(0.0);
					}

			} else {
				alert("Geocode was not successful for the following reason:" + status);
			}
		});
	}
}

