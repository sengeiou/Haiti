define([
	"framework/Model/HttpService"
],
function(HttpService) {
	
	var _send = function(url, params, callback, failure) {
		HttpService.ajax({
			url: url,
			params: params,
			success: callback,
			failure: failure
		});
	};
	
	var convertLocalDate = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/aimir-web/common/convertLocalDate.do', 
			{
				supplierId: params.supplierId,
				dbDate: params.dbDate
			},
			callback, failure);
	};
	
	return {
		getLocationInfo: convertLocalDate
	};
});