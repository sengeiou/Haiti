/**
 * @depencency jquery.form plugin
 *
 */
define(function() {

	var pstring = Object.prototype.toString;

 	var isArray = function(obj) {
		return pstring.apply(obj) === "[object Array]";
 	};

 	var isFunction = function(obj) {
		return (
			pstring.apply(obj) === "[object Function]" && obj.prototype
		);
 	};

 	var isString = function(obj) {
		return pstring.apply(obj) === "[object String]";
 	};

 	var isNumber = function(obj) {
		return pstring.apply(obj) === "[object Number]";
 	};

 	var isDate = function(obj) {
		return pstring.apply(obj) === "[object Date]";
 	}; 

 	var isObject = function(obj) {
		return pstring.apply(obj) === "[object Object]";
 	}; 	

 	var addedProtoypeFunction = function() {
 		if (!isFunction(String.prototype.startsWith)) {
			String.prototype.startsWith = function (str) {
				return this.slice(0, str.length) === str;
			};
		}

		if (!isFunction(String.prototype.endsWith)) {
			String.prototype.endsWith = function (str) {
	 			return this.slice(-str.length) === str;
			};
		}
 	}

 	addedProtoypeFunction();

	return {
		isObject: isObject,
		isArray: isArray,
		isNumber: isNumber,
		isString: isString,
		isDate: isDate,
		isFunction: isFunction,
		random: function(max, min) {
			return Math.floor(Math.random() * (max - min + 1)) + min;
		},
		falseValueToEmpty: function(v, replace) {
	 		if(!v) {
	 			return "";
	 		}
	 		else {	
	 			return v;
	 		}
	 	},	 	
		addCommas: function(nStr) {
		    nStr += '';
		    x = nStr.split('.');
		    x1 = x[0];
		    x2 = x.length > 1 ? '.' + x[1] : '';
		    var rgx = /(\d+)(\d{3})/;
		    while (rgx.test(x1)) {
		        x1 = x1.replace(rgx, '$1' + ',' + '$2');
		    }
		    return x1 + x2;
		},
		sharpErrorConveter: function(prefix, msgAry) {
			var unknown = "[000] unknown error";
			var error = [];			
			if(msgAry) {
				if(!isArray(msgAry)) {
					error = (msgAry) ? msgAry : unknown;
					return error;
				}
				for(var i=0,len=msgAry.length; i < len; i++) {
					var tmp = msgAry[i];
					if(tmp && tmp.indexOf("##") != -1) {
						tmp = I18N[prefix+$.trim(tmp.split("##")[0])];
						tmp = (tmp && tmp.length > 0) ? $.trim(tmp) : unknown;
						error.push(tmp);
					}
					else {
						error.push(tmp);
					}
				}
			}
			else {
				error.push(unknown);
			}
			return error.join("<br/>");
		},
		keyNamePareToObject: function(pare) {
			if(pare.$$converted) return pare;
			if(pare.is("form")) {
				pare = pare.formToArray();
			}			
			var ret = {};
			var formField = null;
			for(var i=0,len=pare.length; i < len; i++) {
				formField = pare[i];
				if(formField.name) {
					if(ret[formField.name]) {						
						if(!isArray(ret[formField.name])) {
							var tmp = [ ret[formField.name] ];
							ret[formField.name] = tmp;							
						}
						ret[formField.name].push(formField.value);
					}
					else {
						ret[formField.name] = formField.value;
					}
				}
			}
			ret.$$converted = true;
			return ret;
		}
	};
});