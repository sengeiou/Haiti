define(["framework/View/Control/Alert"], function(Alert) {
	return { 
		messageBox: function(val, title, el) {
			Alert.info(val, title, el);
		}
	};
})