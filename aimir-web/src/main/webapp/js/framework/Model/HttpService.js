define(["jquery"], function($) {
	
	var baseAjaxOption = {
		method: "GET"
	};

	var baseFormOption = {
		formId: 'http-form',
		target: '',
		action: '',
		method: 'GET',
		enctype: 'application/x-www-form-urlencoded'
	};
	
	return {
		// @dependency jQuery
		dynamicFormSubmit: function(option) {

			var opt = Ext.apply({}, baseFormOption);
			opt = Ext.apply(opt, option);
			var k, v, $input;

			var $form = $('#'+opt.formId);
			if($form || $form.size() < 1) {
				$form = $('<form>').attr({
					id: opt.formId,
					action: opt.action,
					target: opt.target,
					method: opt.method,
					enctype: opt.enctype
				});
				$(document.body).append($form);
			}
			var params = opt.params;			
			$form.empty();
			for(k in params) {
				if(!params.hasOwnProperty(k)) continue;
				
				v = params[k];
				if(v === undefined || v === null) continue;				
				if(typeof v === 'object' && v.constructor === Array) {
					var len = v.length;
					for(var i=0; len < i; i++) {
						$input = $("<input type='hidden' name='"+k+"' value='"+v+"'>");
						$form.append($input);
					}
				}				
				else {
					$input = $("<input type='hidden' name='"+k+"' value='"+v+"'>");
					$form.append($input);
				}				
			}	
			$form.submit();
		},
		// @dependency ExtJS
		ajax: function(option) {
			var opt = Ext.apply({}, baseAjaxOption);
			opt = Ext.apply(opt, option);
			Ext.Ajax.request({
				url: opt.url,
				method: opt.method,
				params: opt.params,
				success: function(res, req) {
					var c = opt.success;
					if(c && typeof c === 'function') {
						c(res, req);
					}
				},
				failure: function(res, req) {
					var f = opt.failure;
					if(f && typeof f === 'function') {
						var result = (typeof res !== 'object') ? {} : res;
						if(!res.status && !res.statusText) {
							result.status = 555;
							result.statusText = 'Internal Server Error (Unknown)';
						}
						result.result = "fail";
						result.msg = "[" + res.status + "] " + res.statusText;
						f(result, res);
					}
				}
			});
		}
	};
});