var root = document.documentElement || document.body;
if(root) {
	root.className += " js-support";
}
require([
    GLOBAL_CONTEXT.CONTEXT + "/js/extjs/adapter/ext/ext-base.js",
    GLOBAL_CONTEXT.CONTEXT + "/js/FusionCharts.js",
    GLOBAL_CONTEXT.CONTEXT + "/js/jquery-ui-1.7.2.min.js",
    GLOBAL_CONTEXT.CONTEXT + "/js/jquery.selectbox.js",
    GLOBAL_CONTEXT.CONTEXT + "/js/jquery.form.js"
], function() {
	
	 // Ext 3.4 IE9 Detect Bug Fixed
	if (Ext.isIE6 && /msie 9/.test(navigator.userAgent.toLowerCase())) {
		Ext.isIE6 = Ext.isIE = false;
		Ext.isChrome = Ext.isIE9 = true;
	}
	 
	require([
		GLOBAL_CONTEXT.CONTEXT + "/js/extjs/ext-all-debug.js",
	    GLOBAL_CONTEXT.CONTEXT + "/js/FusionChartsExportComponent.js"
	], function() {

		var RouteControllerMap = {
			"ManualMetering": "ManualMetering"
		};
		
		var mapped = RouteControllerMap[GLOBAL_CONTEXT.GADGET];
		if(!mapped) {
			throw new Error("Undefined [" + mapped + "] Controller File");
		}
		
		require(
			[ "framework/Controller/mvm/" + mapped ], 
			function(Controller) {
				Controller.execute(GLOBAL_CONTEXT);
			}
		);
	});
	
});