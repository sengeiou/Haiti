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
	<script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAAP2Erypv9AnbpV_l0V2mAnBSY7GMlJmgaw57r5MpvIQOuUqjY6RSgexPCusqTfL58QeSyjpY4oDreKg"></script>
	<script type="text/javascript">
	google.load("earth", "1.x");
	google.load("maps", "2.x");
	var ge = null;
	var networkLink = null;
	var networkLink2 = null;
	var networkLink3 = null;
	var networkLink4 = null;
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
			success : showResult,
			url : '${ctx}/gadget/device/maplist.do',
			type : 'post',
			datatype : 'json'
		};
		$('#search').ajaxSubmit(options);
	}
	function showResult(responseText, status) {
		var className = responseText.className;
		var placemark = responseText.result.feature.feature;
		var listItems = [];
		var RegExpHG = /\((.*?)\)/g;	// 괄호안 문자 제거
		var host = location.host.toLowerCase();
		listItems.push(
				'<div style="float:left;width:100%;height:20px;">'
				+'<div style="float:left;width:30px;">'
				+'<b>번호</b>'
				+'</div>'
				+'<div style="float:left;width:170px;">'
				+'<b>장비명</b>'
				+'</div>'
				+'<div style="float:left;width:100px;">'
				+'<b>상태</b>'
				+'</div>'
				+'<div style="float:left;">'
				+'<b>연관정보</b>'
				+'</div>'
				+'</div>'
			);
		if (placemark.length>1) {	// 전체보기
			listItems.push(
				'<div style="float:left;width:100%;height:15px;">'
				+'<div style="float:left;width:30px;">'
				+'<b>&nbsp;</b>'
				+'</div>'
				+'<div style="float:left;width:170px;">'
				+'<a href="javascript:;" onclick=showsomethingelse4("http://'+host+'${ctx}/${supplierID}/'+className.toLowerCase()+'.kml")>'
				+'전체보기'
				+'</a>'
				+'</div>'
				+'<div style="float:left;width:100px;">'
				+'</div>'
				+'<div style="float:left;">'
				+'</div>'
				+'</div>'
			);
		}
		for (var i=0; i<placemark.length; i++) {
			var address = placemark[i].description;
			var coordinates = placemark[i].geometry.coordinates;
			var key = placemark[i].name.replace(RegExpHG,'');
			var items = null;
			items = '<div style="float:left;width:100%;height:15px;">';
			items += '<div style="float:left;width:30px;">';
			items += '<b>'+(i+1)+'</b>';
			items += '</div>';
			if (coordinates[0].altitude == "0" && coordinates[0].latitude == "0" && coordinates[0].longitude == "0") {
				if (address=="") {
					items += '<div style="float:left;width:170px;">';
					items += placemark[i].name;
					items += '</div>';
					items += '<div style="float:left;width:100px;">';
					items += '(위치정보없음)';
					items += '</div>';
					items += '<div style="float:left;">';
					items += '</div>';
				} else {
					var cvAddress = address.replace(/(^s*)|(s*$)/g, '');
					items += '<div style="float:left;width:170px;">';
					items += placemark[i].name;
					items += '</div>';
					items += '<div style="float:left;width:100px;">';
					items += '<a href="javascript:;" onclick="cvAddressToCoordinate(\''+className.toLowerCase()+'\',\''+key+'\',\''+cvAddress.toLowerCase()+'\')" >';
					items += '(주소만 있음)';
					items += '</a>';
					items += '</div>';
					items += '<div style="float:left;">';
					items += '</div>';
				}
			} else {
				if (address=="") {
					items += '<div style="float:left;width:170px;">';
					items += '<a href="javascript:;" onclick=showsomethingelse4("http://'+host+'${ctx}/${supplierID}/'+key+'/'+className.toLowerCase()+'.kml")>';
					items += placemark[i].name;
					items += '</a>';
					items += '</div>';
					items += '<div style="float:left;width:100px;">';
					items += '(좌표만있음)';
					items += '</div>';
					items += '<div style="float:left;">';
					items += '<b></b>';
					items += '</div>';
				} else {
					items += '<div style="float:left;width:170px;">';
					items += '<a href="javascript:;" onclick=showsomethingelse4("http://'+host+'${ctx}/${supplierID}/'+key+'/'+className.toLowerCase()+'.kml")>';
					items += placemark[i].name;
					items += '</a>';
					items += '</div>';
					items += '<div style="float:left;width:100px;">';
					items += '(정상)';
					items += '</div>';
					items += '<div style="float:left;">';
					items += '<a href="javascript:;" onclick=showsomethingelse4("http://'+host+'${ctx}/${supplierID}/'+key+'/'+className.toLowerCase()+'+relative.kml")>';
					items += '연결장비보기';
					items += '</a>';
					items += '</div>';
				}
			}
			items += '</div>';
			listItems.push(items);
		}
		$('#lBox').empty();
		$('#lBox').append(listItems.join(''));
	}
	function cvAddressToCoordinate(className, name, address) {
		var geocoder = new GClientGeocoder();
		if (geocoder) {
			geocoder.getLatLng(
				address,
				function(point) {
					if (!point) {
						Ext.Msg.alert('<fmt:message key='aimir.message'/>',address + " <fmt:message key='aimir.noaddress'/>");
					} else {
						$.ajax({
							type:"GET",
							url:"http://"+location.host.toLowerCase()+"${ctx}/gadget/device/mapUpdate.do",
							data:{className:className, name:name, pointx:point.x, pointy:point.y},
							success:function(data, textStatus) {
								if (textStatus=="success") {
									doSubmit();
								} else {
									Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.value.error'/>");
								}
							}
						});
					}
				}
			);
		}
	}
	function init() {
		google.earth.createInstance("map3d", initCB, failureCB);
	}
	function initCB(object) {
		ge = object;
		ge.getWindow().setVisibility(true);
		updateOptions();

		var la = ge.createLookAt('');
		// 초기 시점을 설정한다.
		la.set(37, 128, 500000, ge.ALTITUDE_RELATIVE_TO_GROUND, 0, 0, 99.99);
		ge.getView().setAbstractView(la);
	}
	function failureCB(object) {
		/***
		 * This function will be called if plugin fails to load, in case
		 * you need to handle that error condition.
		 ***/
	}
	function updateOptions() {
		var options = ge.getOptions();

		// 네비게이션 사용 여부
		ge.getNavigationControl().setVisibility(ge.VISIBILITY_SHOW);
		//ge.getNavigationControl().setVisibility(ge.VISIBILITY_HIDE);
		ge.getLayerRoot().enableLayerById(ge.LAYER_BORDERS, true);
		options.setStatusBarVisibility(true);	// 상태 표기 여부
		options.setGridVisibility(false);	// 그리드 표기 여부
		options.setOverviewMapVisibility(false);	// 전체지도 표기 여부
		options.setScaleLegendVisibility(true);	// 축적 표기 여부
		options.setAtmosphereVisibility(true);	// 모름
		options.setMouseNavigationEnabled(true);	// 마우스 컨트롤 여부
	}
	function showsomethingelse() {
		var form = document.daumoptions;
		if (networkLink == null) {
			networkLink = ge.createNetworkLink("");
			networkLink.setDescription("NetworkLink open to fetched content");
			networkLink.setName("Open NetworkLink");
			networkLink.setFlyToView(false);
			var link = ge.createLink("");
			link.setHref("http://aero.sarang.net/dmoge/daum_on_google2.kml");
			networkLink.setLink(link);
		}
		if (form.skyview.checked) {
			ge.getGlobe().getFeatures().appendChild(networkLink);
		} else {
			ge.getGlobe().getFeatures().removeChild(networkLink);
			networkLink = null;
		}
	}
	function showsomethingelse2() {
		var form = document.daumoptions2;
		if (networkLink2 == null) {
			networkLink2 = ge.createNetworkLink("");
			networkLink2.setDescription("NetworkLink open to fetched content");
			networkLink2.setName("Open NetworkLink");
			networkLink2.setFlyToView(false);
			var link = ge.createLink("");
			link.setHref("http://aero.sarang.net/dmoge/daum_on_google.kml");
			networkLink2.setLink(link);
		}
		if (form.overlay.checked) {
			ge.getGlobe().getFeatures().appendChild(networkLink2);
		} else {
			ge.getGlobe().getFeatures().removeChild(networkLink2);
			networkLink2 = null;
		}
	}
	function showsomethingelse3() {
		var form = document.daumoptions3;
		if (networkLink3 == null) {
			networkLink3 = ge.createNetworkLink("");
			networkLink3.setDescription("NetworkLink open to fetched content");
			networkLink3.setName("Open NetworkLink");
			networkLink3.setFlyToView(false);
			var link = ge.createLink("");
			link.setHref("http://aero.sarang.net/dmoge/daum_on_google3.kml");
			networkLink3.setLink(link);
		}
		if (form.map.checked) {
			ge.getGlobe().getFeatures().appendChild(networkLink3);
		} else {
			ge.getGlobe().getFeatures().removeChild(networkLink3);
			networkLink3 = null;
		}
	}
	function showsomethingelse4(href) {
		if (networkLink4 == null) {
			networkLink4 = ge.createNetworkLink("");
			networkLink4.setDescription("NetworkLink open to fetched content");
			networkLink4.setName("Open NetworkLink");
			networkLink4.setFlyToView(true);
			var link = ge.createLink("");
			link.setHref(href);
			networkLink4.setLink(link);
			ge.getGlobe().getFeatures().appendChild(networkLink4);
		} else {
			ge.getGlobe().getFeatures().removeChild(networkLink4);
			networkLink4 = null;
			networkLink4 = ge.createNetworkLink("");
			networkLink4.setDescription("NetworkLink open to fetched content");
			networkLink4.setName("Open NetworkLink");
			networkLink4.setFlyToView(true);
			var link = ge.createLink("");
			link.setHref(href);
			networkLink4.setLink(link);
			ge.getGlobe().getFeatures().appendChild(networkLink4);
		}
	}
	</script>
</head>
<body onload='init()'>
<div id="map3d_container" style="float:left;width:60%;height:300px;">
	<div id="map3d" style="float:left;width:100%;height:100%;"></div>
</div>
<div style="float:left;width:2%">&nbsp;</div>
<div style="float:left;width:38%;height:300px;">
	<div style="float:left;width:100%;height:20px;">
		<form id="search" name="search" onsubmit="return false">
			<input type="hidden" name="supplierID" value="${supplierID}"/>
			<select name="class">
				<option value="mcu">DCU</option>
				<option value="modem">Modem</option>
				<option value="meter">Meter</option>
			</select>
			<input name="field" type="text" style="width:50px"/>
			<input type="submit" onClick="javascript:doSubmit();" value="검색"/>
		</form>
	</div>
	<br style="clear:both" />
	<div style="float:left;width:100%;height:270px;overflow-x:hidden;overflow-y:scroll;border:1px solid red;">
		<div id="lBox" style="float:left;"></div>
	</div>
</div>
<br style="clear:both" />
<div id="options_container" style="float:left;width:60%;">
	<div style="float:left;">
		<form name="daumoptions" action='javascript:showsomethingelse();'>
			<input type="checkbox" onclick='showsomethingelse()' name="skyview" /> 다음 스카이뷰(Daum Skyview)
		</form>
	</div>
	<br style="clear:both" />
	<div style="float:left;">
		<form name="daumoptions2" action='javascript:showsomethingelse2();'>
			<input type="checkbox" onclick='showsomethingelse2()' name="overlay" /> 다음 오버레이(Daum Overlay)
		</form>
	</div>
	<br style="clear:both" />
	<div style="float:left;">
		<form name="daumoptions3" action='javascript:showsomethingelse3();'>
			<input type="checkbox" onclick='showsomethingelse3()' name="map" /> 다음 일반지도(Daum Map)
		</form>
	</div>
</div>  
</body>
</html>