define([
  	"framework/Model/HttpService",
  	"framework/Model/Locale",
  	"framework/Util/Storage/LStorage"
], function(HttpService, Locale, Storage) {
	
	// private
	function nullOrEmptyCheck(params, ignore) {
		var k, v;
		var copy = Ext.apply({}, params);
		for(var i=0,len=ignore.length; len--; ) {
			delete copy[i];
		}
		for(k in copy) {
			if(!copy.hasOwnProperty(k)) {
				continue;
			}
			v = copy[k];
			if(v === undefined || v === null || v === '') {
				return {
					result: false,
					key: k
				};
			}
		}
		return {
			result: true
		};
	}
	
	// private
	var _send = function(url, params, callback, failure) {
		HttpService.ajax({
			url: url,
			params: params,
			success: callback,
			failure: failure
		});
	};
	
	var addEndDevice = function(params, callback, failure) {
		
		var valid = nullOrEmptyCheck(params, ['modemType', 'modemSerial']);
		if(!valid.result) {
			return valid;
		}
		
		HttpService.ajax({
			url: GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/addEndDevice.do', 
			method: "POST",
			params: {				
				supplierId: ((params.supplierId === undefined) ? 1 : params.supplierId),
				locationId: params.locationId,
				codeId: params.codeId,
				manufacturerer: params.manufacturerer,
				model: params.model,
				friendlyName: params.friendlyName,
				installDate: params.installDate.replace(/\//gi, ''),
				manufactureDate: params.manufactureDate.replace(/\//gi, ''),
				powerConsumption: params.powerConsumption,
				modemType: params.modemType,
				modemSerial: params.modemSerial
			}, 
			success: callback, 
			failure: failure
		});
	};
	
	var editEndDevice = function(params, callback, failure) {
		
		var valid = nullOrEmptyCheck(params, ['modemType', 'modemSerial']);
		if(!valid.result) {
			return valid;
		}
		
		HttpService.ajax({
			url: GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/editEndDevice.do',
			method: "POST",
			params: {		
				endDeviceId: params.endDeviceId,
				supplierId: ((params.supplierId === undefined) ? 1 : params.supplierId),
				locationId: params.locationId,
				codeId: params.codeId,
				manufacturerer: params.manufacturerer,
				model: params.model,
				friendlyName: params.friendlyName,
				installDate: params.installDate.replace(/\//gi, ''),
				manufactureDate: params.manufactureDate.replace(/\//gi, ''),
				powerConsumption: params.powerConsumption,
				modemType: params.modemType,
				modemSerial: params.modemSerial
			}, 
			success: callback, 
			failure: failure
		});
	};
	
	var deleteEndDevice = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/deleteEndDevice.do', 
			{
				endDeviceId: params.endDeviceId
			}, 
			callback, failure);
	};
	
	var editFacilityStatus = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/editFacilityStatus.do', 
			{
				endDeviceId: params.endDeviceId,
				codeId: params.codeId
			}, 
			callback, failure);
	};
	
	var getMetaData = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/getMetaData.do', 
			params, callback, failure);
	};
	
	
	var getEndDeviceCompareData = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/endDeviceCompareChart.do', 
			params, callback, failure);
	};
	
	var getEndDeviceLog = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/getEndDeviceLog.do', 
			params, callback, failure);
	};
	
	var getEndDeviceConditionGrid = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + 
				'/gadget/system/bems/getEndDevicesVOByLocationIdMetering.do', 
			params, callback, failure);
	};
	
	var getEndDevice = function(params, callback, failure) {
		_send(
			GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/getEndDeviceChart.do', 
			params, callback, failure);
	};

	var getEndDevicesMeteringStore = function(params) {
		return new Ext.data.JsonStore({
			pageSize: params.pageLimit,
			proxy: new Ext.data.HttpProxy({
                url: GLOBAL_CONTEXT.CONTEXT + 
					'/gadget/system/bems/getEndDevicesVOByLocationIdMetering.do'
            }),
			baseParams: {
				start:0, 
            	limit: params.pageLimit,
				locationId: 1,
	    		endDeviceId: -1
			},
			listeners: params.listeners || {},
			root: 'endDeviceList',
	        totalProperty: 'listCount',
	        idProperty: 'endDeviceId',
	        fields: [
	            { name: 'location', type: 'string' },
	            { name: 'locationId', type: 'int' },
			    { name: 'type', type: 'string' },
			    { name: 'typeId', type: 'int' },
			    { name: 'friendlyName', type: 'string' },
			    { name: 'status', type: 'string' },
			    { name: 'model', type: 'string' },
			    { name: 'modemId', type: 'int' },
			    { name: 'modemSerial', type: 'string' },
			    { name: 'powerConsumption', type: 'string' },
			    { name: 'manufacturerer', type: 'string' },
			    { name: 'modemType', type: 'string' },
			    { name: 'modemTypeId', type: 'int' },
			    { name: 'installDate', type: 'string' },
			    { name: 'manufactureDate', type: 'string' },
			    { name: 'dayEM', type: 'string' },
			    { name: 'dayWM', type: 'string' },
			    { name: 'dayGM', type: 'string' },
			    { name: 'dayHM', type: 'string' }
	        ]
	    });
	};

	var getEndDeviceLogsStore = function(params) {
		return new Ext.data.JsonStore({
			pageSize: params.pageLimit,
			proxy: new Ext.data.HttpProxy({
                url: GLOBAL_CONTEXT.CONTEXT + '/gadget/system/bems/getEndDeviceLog.do'
            }),
            listeners: params.listeners || {},
            baseParams: {
            	start:0, 
            	limit: params.pageLimit,
				locationId: 1,
	    		endDeviceId: -1,
	    		supplierId : 1
			},
	        root: 'endDeviceLogList',
	        totalProperty: 'listCount',
	        idProperty: 'id',
	        fields: [
	            { name: 'locationName', type: 'string' },
			    { name: 'categoryCode', type: 'string' },
			    { name: 'friendlyName', type: 'string' },
			    { name: 'preStatusCode', type: 'string' },
			    { name: 'statusCode', type: 'string' },
			    { name: 'writeDatetime', type: 'string' }
	        ]
	    });
	};
	
	return {
		addEndDevice: addEndDevice,
		editEndDevice: editEndDevice,
		deleteEndDevice: deleteEndDevice,
		editFacilityStatus: editFacilityStatus,
		getMetaData: getMetaData,
		getEndDeviceCompareData: getEndDeviceCompareData,
		getEndDeviceLog: getEndDeviceLog,
		getEndDeviceConditionGrid: getEndDeviceConditionGrid,
		getEndDevice: getEndDevice,
		getEndDevicesMeteringStore: getEndDevicesMeteringStore,
		getEndDeviceLogsStore: getEndDeviceLogsStore
	};
});