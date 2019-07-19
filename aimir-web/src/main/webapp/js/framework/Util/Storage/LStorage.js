/**
 * localStorage, sessionStorage 기반 데이터 저장소
 */
define([
        
], function() {

	var global = window,
		lStorage = global.sessionStorage || false,
		_context = {},
		_encoder = function(){ return ''; };
		_decoder = function(){ return []; };
		
	var cache = {};

	var isSupport = function() {
		return !!lStorage;
	};
	
	var switchLocalStorage = function() {
		lStorage = global.localStorage || false;
	};
	
	var switchSessionStorage = function() {
		lStorage = global.sesionStorage || false;
	};
	
	var toggleStorage = function(val) {
		if(!isSupport()) {
			return;
		}
		if(val === "local") {
			switchLocalStorage();
		}
		else {
			switchSessionStorage();
		}
	};
	
	var setUpParser = function(context, encoder, decoder) {
		_context = context;
		_encoder = encoder;
		_decoder = decoder;
	};
	
	var set = function(key, val) {
		if(key) {
			if(isSupport()) {
				val = (val) ? _encoder.apply(_context, [val]) : undefined;
				lStorage.setItem(key, val);
			}
			else {
				cache[key] = val;
			}
		}
	};

	var get = function(key) {
		var item;
		if(isSupport()) {
			item = lStorage.getItem(key);
			if(item) {			
				item = _decoder.apply(_context, [item]);
			}
		}
		else {
			item = cache[key];
		}
		return (item) ? item : [];
	};

	var remove = function(key) {
		if(key) {
			if(isSupport()) {
				lStorage.removeItem(key);
			}
			else {
				delete cache[key];
			}
		}
	};

	return {		
		toggleStorage: toggleStorage,
		isSupport: isSupport,
		setUpParser: setUpParser,
		set: set,
		get: get,
		remove: remove
	};

});