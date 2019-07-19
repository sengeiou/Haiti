define([
    "jquery"
], function($) {
	
	var popup = {};
	var GLOBAL = window;
	var parent = GLOBAL.opener || {};
	var ostring = Object.prototype.toString;
	
	var option = {
		width: 430,
		height: 350,
		directories: false,
		location: false,
		status: false,
		menubar: false,
		scrollbars: true,
		resizable: true
	};

	var openPopup = function(url, target, extraOption) { 
		if(popup[target]) {
			try { popup[target].close() }
			catch(ignore) {}
		}
		if(ostring.apply(extraOption) == "[Object object]") {
			var $option = $({}, option);
			extraOption = ($option, extraOption);
		}
		else {
			extraOption = option;
		}
		var k, v;
		var optionString = [];
		for(k in extraOption) {
			if(extraOption.hasOwnProperty(k)) {
				v = extraOption[k];
				if(v === false) {
					v = "no";
				}
				else if(v === true) {
					v = "yes";
				}
				if(v) {
					optionString.push(k+"="+v);
				}
			}
		}
		popup[target] = GLOBAL.open(
			url, target, optionString.join(",")
		);
	};
	
	return {
		open: openPopup
	};
	
});