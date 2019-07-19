// Form Rest
// UseAge : $('form').clearForm();
// UseAge2 : $(':input').clearForm();
$.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form')
			return $(':input',this).clearForm();
		if (type == 'text' || type == 'password' || tag == 'textarea')
			this.value = '';
		else if (type == 'checkbox' || type == 'radio')
			this.checked = false;
		else if (tag == 'select')
			this.selectedIndex = -1;
	});
};

//Form Auto Setting
//UseAge : $('form').setForm(Object);
$.fn.setForm = function(obj) {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form') {
			for (var i in obj) {
				$(':input[name=\"'+i+'\"]').val(obj[i]);
			}
		}
	});
};

/*
	IFrame auto set Height
	UseAge :
		First. <body><div id="contents">...</div></body>
		Second. <body onLoad="setIframeAutoHeight('contents',0);">
*/
function setIframeAutoHeight(id, margin){
	var height = document.getElementById(id).offsetHeight+margin;
	var o = parent.document.getElementsByTagName('iframe');
	for(i=0;i<o.length;i++){
		if (o[i].src == self.location) {
			o[i].height=height;
		}
	}
}

function getFlexObject(flexName){

	var Browser = {
		    a : navigator.userAgent.toLowerCase()
		}
	Browser = {
	    ie : /*@cc_on true || @*/ false,
	    ie6 : Browser.a.indexOf('msie 6') != -1,
	    ie7 : Browser.a.indexOf('msie 7') != -1,
	    ie8 : Browser.a.indexOf('msie 8') != -1,
	    ie9 : Browser.a.indexOf('msie 9') != -1,
	    mozilla : Browser.a.indexOf('mozilla') != -1,
	    opera : !!window.opera,
	    safari : Browser.a.indexOf('safari') != -1,
	    safari3 : Browser.a.indexOf('applewebkit/5') != -1,
	    mac : Browser.a.indexOf('mac') != -1,
	    chrome : Browser.a.indexOf('chrome') != -1,
	    firefox : Browser.a.indexOf('firefox') != -1
	}

	if (Browser.chrome) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.ie6) {
		flex = $('#'+flexName+'Ex')[0];
	} else if (Browser.ie7) {
		flex = $('#'+flexName+'Ex')[0];
	} else if (Browser.ie8) {
		flex = $('#'+flexName+'Ex')[0];
	} else if (Browser.ie9) {
		flex = $('#'+flexName+'Ex')[0];
	} else if (Browser.mozilla) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.opera) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.safari) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.safari3) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.mac) {
		flex = $('#'+flexName+'Ot')[0];
	} else if (Browser.firefox) {
		flex = $('#'+flexName+'Ot')[0];
	} else {
	}
	
	return flex;
}
