define([
	"framework/Model/HttpService"
],
function(HttpService) {
	
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	
	var getLocationInfo = function(params, callback, failure) {

		HttpService.ajax({
			url: CONTEXT + '/gadget/system/bems/getLocations.do', 
			params: {
				supplierId: params.supplierId
			},
			method: "GET",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}
				callback(res);				
			}, 
			failure: failure
		});
	};
	
	return {
		getLocationInfo: getLocationInfo
	};
});