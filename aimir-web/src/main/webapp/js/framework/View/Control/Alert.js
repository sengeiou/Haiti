define([
   "jquery",
   "framework/Util/ObjectUtils"
], function($, Util) {

	var box = function(val, title, el, icon, btns, callback) {
		Ext.MessageBox.show({
			title: title,
			msg: val,
			buttons: btns || Ext.MessageBox.OK,
			animEl: ((!el) ? document.body : el),
			icon: icon,
			fn: callback			
		});
	};
	
	var info = function(val, title, el) {
		box(val, title, el, Ext.MessageBox.INFO);
	};

	var error = function(val, title, el) {
		box(val, title, el, Ext.MessageBox.ERROR);
	};

	var confirm = function(val, title, ok, no) {		
		Ext.MessageBox.confirm(title, val, function(btn) {
			if(btn === 'yes') if(Util.isFunction(ok)) ok();			
			else if(Util.isFunction(ok)) no();
		});
	}
	
	return {
		info: info,
		confirm: confirm,
		error: error
	};
	
});