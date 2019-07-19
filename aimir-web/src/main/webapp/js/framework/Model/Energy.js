define([
	"framework/Model/HttpService"
],
function(HttpService) {
	
	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var JsonStore = Ext.data.JsonStore;
	var HttpProxy = Ext.data.HttpProxy;
	var I18N = GLOBAL_CONTEXT.I18N;

	var getFMStatus = function(callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getFMStatusCode.do',
			params: {},
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

	var getDRLogDataStore = function(params) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
				method: "POST",
                url: CONTEXT + '/gadget/bems/getDRLogs.do'
            }),
            listeners: params.listeners || {},
            baseParams: {
            	start: 0, 
            	limit: params.pageLimit,
				status: params.result,
				searchDate: params.searchDate,
				scenario: params.scenario || '',
            	supplierId: params.supplierId
			},
	        root: 'peakDemandLogs',
	        totalProperty: 'totalCount',
	        idProperty: 'id',		        
	        fields: [
	            { name: 'id', type: 'integer' },
	            { name: 'senarioId', type: 'integer' },
	            { name: 'senarioName', type: 'string' },
	            { name: 'runTime', type: 'string' },
	            { name: 'level', type: 'string' },
			    { name: 'result', type: 'string' }
	        ]		        
	    });
	};

	var getDRScenarios = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getDRScenarios.do',
			params: {
				start: params.start,
				limit: params.limit,
				contractCapacityId: params.contractCapacityId || '',
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

	var applyScenario = function(params, callback, failure) {

		var errorMsg = [];
		var params = params || {};

		if(!params.supplierId) {
			errorMsg.push(I18N['aimir.form.required.supplierId']);
		}
		// if(!params.scenarioId) {
		// 	errorMsg.push(I18N['aimir.form.required.DRScenario']);
		// }
		if(!params.level) {
			errorMsg.push(I18N['aimir.form.required.level']);
		}		
		if(errorMsg.length > 0) {
			return errorMsg;
		}

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/configDRScenario.do',
			params: {
				scenarioId: params.scenarioId,
				level: params.level,
				isAction: params.isAction,
				supplierId: params.supplierId
			},
			method: "POST",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}
				callback(res);				
			}, 
			failure: failure
		});

		return [];
	}

	var addScenario = function(params, callback, failure) {

		var errorMsg = [];
		var params = params || {};

		if(!params.supplierId) {
			errorMsg.push(I18N['aimir.form.required.supplierId']);
		}
		if(!params.name) {
			errorMsg.push(I18N['aimir.form.required.DRScenario']);
		}
		if(!params.contractLocationId) {
			errorMsg.push(I18N['aimir.form.required.contractLocation']);
		}
		if(!params.tag) {
			errorMsg.push(I18N['aimir.form.required.tag']);
		}		
		if(errorMsg.length > 0) {
			return errorMsg;
		}

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/addDRScenario.do',
			params: {
				scenarioName: params.name,
				contractLocation: params.contractLocationId,
				description: params.description || '',
				tags: params.tag
			},
			method: "POST",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}
				callback(res);				
			}, 
			failure: failure
		});

		return [];
	}

	var deleteDRScenario = function(params, callback, failure) {

		var errorMsg = [];
		var params = params || {};

		if(!params.supplierId) {
			errorMsg.push(I18N['aimir.form.required.supplierId']);
		}
		if(!params.scenarioId) {
			errorMsg.push(I18N['aimir.form.required.DRScenario']);
		}
		if(errorMsg.length > 0) {
			return errorMsg;
		}

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/deleteDRScenario.do',
			params: {
				scenario: params.scenarioId
			},
			method: "POST",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}
				callback(res);				
			}, 
			failure: failure
		});

		return [];
	}

	var modifyDRScenario = function(params, callback, failure) {

		var errorMsg = [];
		var params = params || {};

		if(!params.supplierId) {
			errorMsg.push(I18N['aimir.form.required.supplierId']);
		}
		if(!params.scenarioId) {
			errorMsg.push(I18N['aimir.form.undefined.DRScenario']);
		}
		if(!params.name) {
			errorMsg.push(I18N['aimir.form.required.DRScenario']);
		}
		if(!params.contractLocation) {
			errorMsg.push(I18N['aimir.supplySelectArea']);
		}
		if(errorMsg.length > 0) {
			return errorMsg;
		}

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/modifyDRScenario.do',
			params: {
				scenarioId: params.scenarioId,
				supplierId: params.supplierId,
				scenarioName: params.name,
				contractLocation: params.contractLocation,
				description: params.description || '',
				tags: params.tag
			},
			method: "POST",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
				}
				callback(res);				
			}, 
			failure: failure
		});
	}

	var getDRScenarioDataStore = function(params) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
				method: "POST",
                url: CONTEXT + '/gadget/bems/getDRScenarios.do'
            }),
            listeners: params.listeners || {},
            baseParams: {
				start:0, 
            	limit: params.pageLimit,
            	supplierId: params.supplierId
			},
	        root: 'peakDemandScenarios',
	        totalProperty: 'totalCount',
	        idProperty: 'id',		        
	        fields: [
	            { name: 'id', type: 'integer' },
	            { name: 'name', type: 'string' },
	            { name: 'contractCapacity.contractLocations', type: 'string' },
	            { name: 'description', type: 'string' },
	            { name: 'modifyTime', type: 'string' },
	            { name: 'contractCapacity.capacity', type: 'integer' },
	            { name: 'target', type: 'string' }
	        ]		        
	    });
	}

	var getEnergyPeakDemand = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getEnergyPeakDemand.do', 
			params: {
				contractCapacityId: params.contractCapacityId
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

	var updateThreshold = function(params, callback, failure) {

		var errorMsg = [];
		var params = params || {};

		if(!params.contractCapacityId) {
			errorMsg.push(I18N['aimir.supplySelectArea']);
		}
		var threshold1 = parseInt(params.threshold1, 10);
		if(isNaN(threshold1) || params.threshold1 < 0) {
			errorMsg.push(I18N['aimir.form.undefined.demandValue']);
		}
		var threshold2 = parseInt(params.threshold2, 10);
		if(isNaN(threshold2) || params.threshold2 < 0) {
			errorMsg.push(I18N['aimir.form.required.demandValue']);
		}
		var threshold3 = parseInt(params.threshold3, 10);
		if(isNaN(threshold3) || params.threshold3 < 0) {
			errorMsg.push(I18N['aimir.form.required.demandValue']);
		}
		if(errorMsg.length > 0) {
			return errorMsg;
		}

		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/updateThreshold.do', 
			params: {
				contractCapacityId: params.contractCapacityId,
				threshold1: params.threshold1, 
				threshold2: params.threshold2, 
				threshold3: params.threshold3
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
		
		return [];
	};

	var getTags = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getTags.do', 
			params: {
				contractCapacityId: params.contractCapacityId,
				threshold1: params.threshold1, 
				threshold2: params.threshold2, 
				threshold3: params.threshold3
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

	var getPeakDemandthresholdConfigs = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getPeakDemandthresholdConfigs.do', 
			params: params || {},
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
		getDRLogDataStore: getDRLogDataStore,
		getPeakDemandthresholdConfigs: getPeakDemandthresholdConfigs,
		getDRScenarios: getDRScenarios,		
		applyScenario: applyScenario,
		addScenario: addScenario,
		deleteDRScenario: deleteDRScenario,
		modifyDRScenario: modifyDRScenario,
		getDRScenarioDataStore: getDRScenarioDataStore,
		getFMStatus: getFMStatus,
		getEnergyPeakDemand: getEnergyPeakDemand,
		updateThreshold: updateThreshold,
		getTags: getTags
	};
});