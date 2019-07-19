define([
  	"framework/Model/HttpService",
  	"framework/Util/Storage/LStorage"
], function(HttpService, Locale, Storage) {
	
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	
	var getVenders = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/system/vendorlist.do', 
			method: "GET",
			params: {				
				supplierId: ((params.supplierId === undefined) ? 1 : params.supplierId)
			}, 
			success: callback, 
			failure: failure
		});
	};
	
	var getDeviceModelsByVenendorId = function(venderId, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/system/getDeviceModelsByVenendorId.do',
			params: {				
				vendorId: venderId
			},
			method: "GET",
			success: callback, 
			failure: failure
		});
	};
	
	return {
		getVenders: getVenders,
		getDeviceModelsByVenendorId: getDeviceModelsByVenendorId
	};
});