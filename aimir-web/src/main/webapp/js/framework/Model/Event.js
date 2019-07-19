define([
	"jquery",
	"framework/Model/HttpService"
], function($, HttpService) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var JsonStore = Ext.data.JsonStore;
	var HttpProxy = Ext.data.HttpProxy;
	
	var getPeekDemandLogDataStore = function(params, callback, failure) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
				method: "POST",
                url: CONTEXT + '/gadget/bems/getPeekDemandLogs.do'
            }),
            listeners: params.listeners,
            baseParams: {
				start:0, 
            	limit: params.pageLimit,
            	supplierId: params.supplierId
			},
	        root: 'eventAlertLogs',
	        totalProperty: 'totalCount',
	        idProperty: 'id',		        
	        fields: [
	            { name: 'id', type: 'integer' },
	            { name: 'EventAlert', type: 'string' },
	            { name: 'eventAlertName', type: 'string' },
	            { name: 'severity', type: 'string' },
	            { name: 'supplier', type: 'string' },
	            { name: 'openTime', type: 'string' },
	            { name: 'closeTime', type: 'string' },
			    { name: 'writeTime', type: 'string' },
			    { name: 'duration', type: 'string' },
			    { name: 'message', type: 'string' },
			    { name: 'status', type: 'string' },
			    { name: 'occurCnt', type: 'string' },
			    { name: 'location', type: 'string' },
			    { name: 'activatorType', type: 'string' },
			    { name: 'activatorIp', type: 'string' },
			    { name: 'activatorId', type: 'string' }
	        ]		        
	    });
	};

	return {
		getPeekDemandLogDataStore: getPeekDemandLogDataStore
	};

})