define([
    "jquery",
    "framework/View/Control/Alert",
], function($, Alert) {
	
	var render = function(params) { 
		if(console && typeof console.log === 'function') {
			console.log(params);
		}
	};
	
	var showErrorBox = function(err, el) {
		if(!err) return;
		if(typeof err == 'object' && err.constructor === Array) {
			err = err.join("<br/>");
		}
		Alert.error(err, "Error", el);
	};
	
	return {
		showErrorBox: showErrorBox,
		render: render
	};
	
});