define([
	"jquery",
	"framework/Model/HttpService",
	"framework/Util/ObjectUtils"
], function($, HttpService, Util) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var JsonStore = Ext.data.JsonStore;
	var HttpProxy = Ext.data.HttpProxy;
	var JSON = Ext.util.JSON;

	var INVERTERS = null;

	var accumulated = 0;
	
	var getElectricGenerationAmountsDataStore = function(params) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
                url: CONTEXT + '/gadget/bems/getElectricGenerationAmounts.do'
            }),
            listeners: params.listeners,
            baseParams: {
            	start:0, 
            	limit: params.pageLimit,
            	inverterId: params.mdsId,
            	meterType: params.meterType,
            	meteringSF: (params.meteringSF || "s"),            	
            	supplierId: params.supplierId,
            	searchDateType: (params.searchDateType || "DAILY"),
            	searchDate: params.searchDate || "",
            	inverterName: params.meterName,
            	isDetail: (params.isDetail || "")
			},
	        root: 'result',
	        totalProperty: 'totalCount',
	        idProperty: 'num',		        
	        fields: [
	            { name: 'prevValue', type: 'string' },
	            { name: 'customerName', type: 'string' },
	            { name: 'modemId', type: 'string' },
	            { name: 'isManual', type: 'string' },
	            { name: 'num', type: 'string' },
			    { name: 'meterNo', type: 'string' },
			    { name: 'value', type: 'string' },
			    { name: 'friendlyName', type: 'string' },
			    { name: 'meteringTime', type: 'string' },
			    { name: 'contractNumber', type: 'string' },
			    { name: 'channel_1', type: 'string' },
			    { name: 'channel_2', type: 'string' },
			    { name: 'channel_3', type: 'string' },
			    { name: 'channel_4', type: 'string' }
	        ]		        
	    });
	};

	
	var getGenerationInfo = function(params, success, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getGenerationInfo.do',
			method: "GET",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}			
				success(res);				
			}, 
			failure: failure
		});
	};

	var getGenerationAmountData = function(params, success, failure) {

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getElectricGenerationStatistics.do',
			params: {
				supplierId: params.supplierId,
				inverterId: params.inverterId || '',
				today: params.today
			},
			method: "GET",
			success: function(res) {				
				if(res && Util.isFunction(success)) {
					res = JSON.decode(res.responseText);
					success(res.result);
				}
			}, 
			failure: function(res) {
				if(res && Util.isFunction(failure)) {
					res = JSON.decode(res.responseText);
					failure(res);
				}
			}
		});		
	};

	var getInverters = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getInverters.do', 
			params: {
				supplierId: params.supplierId,
				inverterId: params.inverterId || ''
			},
			method: "GET",
			success: function(res) {
				if(res && Util.isFunction(callback)) {
					callback(Ext.util.JSON.decode(res.responseText));
				}
			}, 
			failure: failure
		});
	};

	/*
		먼저 인버터를 모두 얻고, 해당 인버터의 mdsId: value의 맵을 만들어서 매치한
		결과를 반환하는데 비효율적이다.

		한번 얻어온 인버터는 캐시한다.
	*/
	var generationValueByInverter = function(params, callback, failure) {
		getInverters(
			params, 
			function(firstResult) {
				if(firstResult && Util.isFunction(callback)) {
					var inverters = firstResult.inverters;
					if(!INVERTERS) {
						INVERTERS = {};
						$.each(inverters, function(i, inverter) {
							INVERTERS[inverter.mdsId] = inverter;
						});
					}		
					HttpService.ajax({
						url: CONTEXT + '/gadget/bems/generationValueByInverter.do', 
						params: {
							supplierId: params.supplierId,
							inverterId: params.inverterId || '-1',
							today: params.today
						},
						method: "GET",
						success: function(res) {
							if(res) {
								var generationData = 
									Ext.util.JSON.decode(res.responseText);
								var compositeData = [];
								$.each(generationData.value, function(i, value) {		
									if(value) {
										compositeData.push({
											id: INVERTERS[value.mdsId].id,
											mdsId: INVERTERS[value.mdsId].mdsId,
											friendlyName: INVERTERS[value.mdsId].friendlyName,
											generationValue: value.total || 0
										});
									}										
								});									
								callback({
									today: generationData.today,
									inverters: compositeData
								});
							}
						}, 
						failure: failure
					});
				}
			}, 
			failure
		);
	};

	var clearInverterCache = function() { 
		INVERTERS = null; 
	};

	var getInverterFromCache = function() { 
		return INVERTERS; 
	};

	return {
		getInverterFromCache: getInverterFromCache,
		clearInverterCache: clearInverterCache,
		generationValueByInverter: generationValueByInverter,
		getElectricGenerationAmountsDataStore: getElectricGenerationAmountsDataStore,
		getGenerationAmountData: getGenerationAmountData,
		getGenerationInfo: getGenerationInfo,
		getInverters: getInverters
	};

})