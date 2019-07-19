define([
	"framework/Model/HttpService",
	"framework/Util/ObjectUtils"
],
function(HttpService, Utils) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	var JSON = Ext.util.JSON;
	var JsonStore = Ext.data.JsonStore;
	var HttpProxy = Ext.data.HttpProxy;
	
	var DayType = {
		HOUR: "HOUR",
		DAY: "DAILY",
		DAY: "DAY",
		MONTH: "MONTHLY",
		MONTH: "MONTH",
		YEAR: "YEAR"
	};

	var MeterMap = {};
	
	var Type = {
		EM: {
			type: "EM",
			title: I18N["aimir.energymeter"]
		},
		GM: {
			type: "GM",
			title: I18N["aimir.gas"]
		},
		WM: {
			type: "WM",
			title: I18N["aimir.water"]
		},
		HM: {
			type: "HM",
			title: I18N["aimir.heatmeter"]
		},
		SPM: {
			type: "SPM",
			title: I18N["aimir.solarmeter"]
		},
		VC: {
			type: "VC",
			title: ''
		}
	};
	
	var MeterNameMap = {
		"EnergyMeter": Type.EM,
		"GasMeter": Type.GM,
		"WaterMeter": Type.WM,
		"HeatMeter": Type.HM,
		"SolarPowerMeter": Type.SPM,
		"VolumeCorrector": Type.VC
	};
	
	var getMeterTypeByMeterName = function(meterIdentify) {
		try {
			return MeterNameMap[meterIdentify];
		}
		catch(notFound) {
			return {};
		}		
	};
	
	var getMeterTypes = function(callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/getMeterTypes.do', 
			method: "GET",
			success: function(res) {
				if(res) {
					var decoded = JSON.decode(res.responseText);
					var codeList = decoded.codeList;
					if(codeList) {
						var meter;
						for(var i=0,len=codeList.length;i<len;i++) {
							meter = codeList[i];
							MeterMap[meter.id] = meter;
						}						
					}
					if(Utils.isFunction(callback)) {
						callback(MeterMap);
					}
				}				
			}, 
			failure: failure
		});
	};
	
	var getManualMeterMdsId = function(supplierId, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/getManualMeterMdsId.do', 
			params: {
				supplierId: supplierId
			},
			method: "GET",
			success: callback, 
			failure: failure
		});
	};
	
	var existsByMdsId = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/device/isMeterDuplicateByMdsId.do', 
			params: {
				mdsId: params.mdsId
			},
			method: "GET",
			success: function(res) {
				res = JSON.decode(res.responseText);
				var ret = (res && res.result === 'false') ? false : true;
				if(typeof callback === 'function') callback(ret);
			}, 
			failure: failure
		});
	};
	
	var MeterActionURLMapping = {
		"EnergyMeter": '/gadget/device/insertEnergyMeter.do',	
		"WaterMeter": '/gadget/device/insertWaterMeter.do',	
		"GasMeter": '/gadget/device/insertGasMeter.do',	
		"HeatMeter": '/gadget/device/insertHeatMeter.do',	
		"SolarPowerMeter": '/gadget/device/insertSolarPowerMeter.do',
		"VolumeCorrector": '/gadget/device/insertVolumeCorrector.do'	
	};
	
	var add = function(option, callback, failure) {
		var errorMsg = [];
		var params = option.params || {};
		
		if(!params["mdsId"]) {
			errorMsg.push(I18N['aimir.alert.selectModel']);
		}
		if(!params["location.id"]) {
			errorMsg.push(I18N['aimir.select.cl']);
		}

		var meterData = MeterMap[option.meterType];
		var actionUrl = "";
		if(!meterData || !meterData.name) {
			actionUrl = MeterActionURLMapping[option.meterType];
			if(!actionUrl) {
				errorMsg.push(I18N['aimir.alert.meterTypeRequired']);
			}
		}
		else {
			actionUrl = MeterActionURLMapping[meterData.name];
		};
		
		if(!actionUrl) {
			errorMsg.push(I18N['aimir.alert.meterTypeRequired']);
		}
		
		if(errorMsg.length > 0) {
			return errorMsg;
		}
		actionUrl = CONTEXT + actionUrl;

		HttpService.ajax({
			url: actionUrl,
			params: params,
			method: "POST",
			success: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res);
			}, 
			failure: failure
		});
		
		return [];
	};
	
	var writeManualMetering = function(params, callback, failure) {
		var errorMsg = [];
		if(!params.meteringDate) {
			errorMsg.push(I18N["aimir.manualmeter.alert.emptyDateString"]);
		}
		if(errorMsg.length > 0) {
			return errorMsg;
		}
		
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/writeManualMetering.do',
			params: params,
			method: "POST",
			success: function(res) {
				res = Ext.util.JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res);
			}, 
			failure: failure
		});
		
		return [];
	};
	
	var modifyManualMetering = function(params, callback, failure) {
		var errorMsg = [];
		if(!params.meteringDate) {
			errorMsg.push(I18N["aimir.manualmeter.alert.emptyDateString"]);
		}
		if(errorMsg.length > 0) {
			return errorMsg;
		}
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/writeManualMetering.do',
			params: params,
			method: "POST",
			success: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res);
			}, 
			failure: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') failure(res);
			}
		});
		
		return [];
	};
	
	var getManualUsageMeteringData = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/getManualUsageMeteringData.do',
			params: {
				supplierId: params.supplierId,
				mdsId: params.mdsId,
				energyType: params.energyType
			},
			method: "GET",
			success: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res);
			}, 
			failure: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') failure(res);
			}
		});
	};
	
	var getManualMeterList = function(params, callback, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/mvm/getManualMeters.do',
			params: {
				supplierId: params.supplierId,
				mdsId: params.mdsId || '',
				energyType: params.energyType || ''
			},
			method: "GET",
			success: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') callback(res);
			}, 
			failure: function(res) {
				res = JSON.decode(res.responseText);
				if(typeof callback === 'function') failure(res);
			}
		});
	}

	var getManualMeterStore = function(params) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
				method: "POST",
                url: CONTEXT + '/gadget/mvm/getManualMeters.do'
            }),
            baseParams: {
            	supplierId: params.supplierId
			},
	        root: 'meterList',
	        idProperty: 'id',
	        fields: [
	            { name: 'id', type: 'string' },
	            { name: 'mdsId', type: 'string' },
	            { name: 'friendlyName', type: 'string' },
	            { name: 'meterType.id', type: 'string' },
	            { name: 'meterType.name', type: 'string' },
	            { name: 'location', type: 'string' }
	        ]
	    });
	};
	var getMeteringStore = function(params) {
		return new JsonStore({
			pageSize: params.pageLimit,
			proxy: new HttpProxy({
                url: CONTEXT + '/gadget/mvm/listManualMetering.do'
            }),
            listeners: params.listeners,
            baseParams: {
            	start:0, 
            	limit: params.pageLimit,
            	supplierId: params.supplierId,
            	isManualMeter: 1,
            	dayType: params.dayType,
            	meterType: params.meterType
			},
	        root: 'meterList',
	        totalProperty: 'totalCount',
	        idProperty: 'id',		        
	        fields: [
	            { name: 'contractId', type: 'string' },
	            { name: 'supplierId', type: 'string' },
	            { name: 'locationName', mapping: 'location.name', type: 'string' },	            
			    { name: 'locationId', mapping: 'location.id', type: 'string' },
			    { name: 'friendlyName', mapping: 'meter.friendlyName', type: 'string' },
			    { name: 'mdsId', mapping: 'meter.mdsId', type: 'string' },
			    { name: 'meteringdate', type: 'string' },
			    { name: 'contractId', type: 'string' },
			    { name: 'endDeviceId', type: 'string' },
			    { name: 'modemId', type: 'string' },
			    { name: 'meteringTime', type: 'string' },
			    { name: 'total', type: 'string' },
			    { name: 'baseValue', type: 'string' }
	        ]		        
	    });
	};
	
	return {
		Type: Type,
		DayType: DayType,
		getMeterTypeByMeterName: getMeterTypeByMeterName,
		getMeterTypes: getMeterTypes,
		existsByMdsId: existsByMdsId,
		add: add,
		getManualUsageMeteringData: getManualUsageMeteringData,
		writeManualMetering: writeManualMetering,
		modifyManualMetering: modifyManualMetering,
		getManualMeterMdsId: getManualMeterMdsId,
		getManualMeterList: getManualMeterList,
		getManualMeterStore: getManualMeterStore,
		getMeteringStore: getMeteringStore
	};
});