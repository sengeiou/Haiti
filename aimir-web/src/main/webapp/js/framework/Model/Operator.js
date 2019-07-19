define([
   	"framework/Model/HttpService",
    "framework/Util/Storage/LStorage"
],
function(HttpService, storage) {
	
	if(storage.isSupport()) {
		storage.setUpParser(
			Ext.util.JSON,
			Ext.util.JSON.encode,
			Ext.util.JSON.decode
		);
	}
	
	var getUserInfoFromServer = function(callback) {
		
		HttpService.ajax({
			url: GLOBAL_CONTEXT.CONTEXT + '/common/getUserInfo.do',
			success: function(res, req) {
				var j = Ext.util.JSON.decode(res.responseText);
				storage.set("loginOperator", j);
				if(callback) {
					callback(j);
				}
			},
			failure: function(req) {
				// TODO: 에러치리 필요
			}
		});		
	};
	
	// XXX: 유저 정보가 세션에 저장되지 않고 있다. 수정 필요
	var getUserInfo = function(callback) {		
		var op = undefined;
		if(!op) {
			getUserInfoFromServer(callback);			
		}
		else {
			if(callback) {
				callback(op);
			}
		}
		return op;
	};
	
	return {
		getUserInfo: getUserInfo
	};
});