// Map Library (Google Map)
document.write("<script type='text/javascript' charset='utf-8' src='http://maps.google.co.kr/maps?file=api&amp;v=2.x&amp;key=ABQIAAAAP2Erypv9AnbpV_l0V2mAnBSY7GMlJmgaw57r5MpvIQOuUqjY6RSgexPCusqTfL58QeSyjpY4oDreKg'></script>");
//document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/jquery.gmap-1.1.0.js'></script>");

//변수 설정
var map = null;
var geocoder = null;

// 메모리 제거
function unInitMap() {
	GUnload();
}

// 지도 그리기
function initMap(id, gpioX, gpioY, gpioZ) {
	setMarkerWithCoordinate(id, gpioX, gpioY, gpioZ);
}

// 위치 표시 (맵아이디, 경도, 위도, 해발)
/*
function setMarkerWithCoordinate(id, gpioX, gpioY, gpioZ) {
	$("#"+id).gMap({
		latitude: gpioX,
		longitude: gpioY,
		zoom: 15,
		controls: true,
		scrollwheel: true,
		markers: [{ latitude: gpioX, longitude: gpioY }]
	});
}
*/
function setMarkerWithCoordinate(id, gpioX, gpioY, gpioZ) {
	if (!map) unInitMap();	// 메모리 제거
	
	// 지도 초기화
	if (GBrowserIsCompatible()) {
		map = new GMap2(document.getElementById(id));
		map.setCenter(new GLatLng(gpioX, gpioY, gpioZ), 15);
		map.setUIToDefault();
		geocoder = new GClientGeocoder();

//		var icon = new GIcon();
//		icon.image = "../../images/icon_mcu.gif";
//		icon.iconSize = new GSize(20, 34);
//		icon.iconAnchor = new GPoint(6, 20);
//		icon.infoWindowAnchor = new GPoint(5, 1);
		var point = new GLatLng(gpioX, gpioY);
		map.clearOverlays();
		map.setCenter(point,15);
//		map.addOverlay(new GMarker(point, icon));
		map.addOverlay(new GMarker(point));
	}
}

//위치 표시 (맵아이디, 주소)
/*
function setMarkerWithAddress(id, address) {
	$("#"+id).gMap({
		zoom: 15,
		controls: true,
		scrollwheel: true,
		markers: [{ address: address }]
	});
}
*/
function setMarkerWithAddress(id, address) {
	if (geocoder) {
		geocoder.getLatLng(
			address,
			function(point) {
				if (!point) {
					alert(address + " not found");
				} else {
					var point = new GLatLng(point.x, point.y);
					map.clearOverlays();
					map.setCenter(point,15);
					map.addOverlay(new GMarker(point));
				}
			}
		);
	}
}

//주소를 좌표로 변환하여 세팅 (주소, 위도를 세팅할 아이디, 경도를 세팅할 아이디, 해발을 세팅할 아이디)
//주) Ajax 호출이므로 응답이 오는데 시간이 걸린다.
function convertAddressToCoordinate(address, x_id, y_id, z_id) {
	var geocoder = new GClientGeocoder();
	if (geocoder) {
		geocoder.getLatLng(
			address,
			function(point) {
				if (!point) {
					$("#"+x_id).val(0.0);
					$("#"+y_id).val(0.0);
					$("#"+z_id).val(0.0);
					alert(address + " not found");
				} else {
					$("#"+x_id).val(point.y);
					$("#"+y_id).val(point.x);
					$("#"+z_id).val(0.0);
					alert("update complete");
				}
			}
		);
	}
}

// 주소를 좌표로 변환하여 세팅 (맵아이디, 주소, 위도를 세팅할 아이디, 경도를 세팅할 아이디, 해발을 세팅할 아이디)
// 주) Ajax 호출이므로 응답이 오는데 시간이 걸린다.
function convertAddressToCoordinateAndMarker(id, address, x_id, y_id, z_id) {
	var geocoder = new GClientGeocoder();
	if (geocoder) {
		geocoder.getLatLng(
			address,
			function(point) {
				if (!point) {
					$("#"+x_id).val(0.0);
					$("#"+y_id).val(0.0);
					$("#"+z_id).val(0.0);
					alert(address + " not found");
				} else {
					$("#"+x_id).val(point.y);
					$("#"+y_id).val(point.x);
					$("#"+z_id).val(0.0);
					setMarkerWithAddress(id, address);
					alert("update complete");
				}
			}
		);
	}
}

function GeoCodingTest(address) {
	convertAddressToCoordinate(address, "gpioX", "gpioY", "gpioZ");
}