define([
    "jquery"
],
function($) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;
	var I18N = GLOBAL_CONTEXT.I18N;
	
	var getDeviceModelsByDevice = function(venderId, subDeviceType, deviceType, callback) {
		$.ajax({
			url: CONTEXT + '/gadget/system/getDeviceModelsByDevice.do',
			data: { 
				vendorId: venderId,
				deviceType : (deviceType || 'MCU'),
				subDeviceType: subDeviceType,
			},
			success: function(res) {
				callback(res);
			},
			dataType: "json"
		});
    };
	
	return {
		deviceModelsByVenendorId: getDeviceModelsByDevice
	};
});