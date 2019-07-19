define(function() {
	if(!NotImplementError && typeof Error === 'function') {
		var NotImplementError = function(err) {			
			Error.apply(this, arguments);			
			if(typeof err === 'object') {
				this.callee = err.callee || "unknown";
				this.code = err.code || "000";
			}
		};
		NotImplementError.prototype = new Error("not Implements");
		NotImplementError.fn = NotImplementError.prototype;
	}
	return NotImplementError;	
});