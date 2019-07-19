define([
	"framework/Model/HttpService"
],
function(HttpService) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	
	var existsBySerial = function(deviceSerial, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/device/isModemDuplicateByDeviceSerial.do', 
			params: {
				deviceSerial: deviceSerial
			},
			method: "GET",
			success: function(res) {
				res = Ext.util.JSON.decode(res.responseText);
				var ret = (res && res.result === 'false') ? false : true;
				if(typeof callback === 'function') callback(ret);
			}, 
			failure: failure
		});
	};
	
	/**
	 * 반환형은 다음의 배열이 반환된다
	 * [
	 * 		0: 모뎀 시리얼 번호
	 * 		1: 모뎀 타입
	 * ]
	 */
	var startsWithSerial = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/device/getModemSerialList.do', 
			params: {
				modemSerial: params.modemSerial,
				supplierId: params.supplierId
			},
			method: "GET",
			success: function(res) {
				res = Ext.util.JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res.rtnModemSerials);
			}, 
			failure: failure
		});
	};
	
	return {
		existsBySerial: existsBySerial,
		startsWithSerial: startsWithSerial
	};
});