// -----------------
// Original code by Joe Walnes
// -----------------
/* 
 * modify by rewriter
 * webmq 관련 부분에서 XML HTTP 부분만 사용한다.
 */

var HttpConnection = {
	open : function(url, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("GET",url,true);

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
        		if (ok) {
            		if(handler) handler(httpReq.responseText, cbdata);
    			}
   			}
		};
		httpReq.send(null);
	},
	openXML : function(url, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("GET",url,true);

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
        		if (ok) {
            		if(handler) handler(httpReq.responseXML, cbdata);
    			}
   			}
		};
		httpReq.send(null);
	},
	openAndState : function(url, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("GET",url,true);

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
            	if(handler) handler(ok, httpReq.responseText, cbdata);
   			}
		};
		httpReq.send(null);
	},
	openXMLAndState : function(url, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("GET",url,true);

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
            	if(handler) handler(ok, httpReq.responseXML, cbdata);
   			}
		};
		httpReq.send(null);
	},
	openPost : function(url, data, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("POST",url,true);
		httpReq.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
        		if (ok) {
            		if(handler) handler(httpReq.responseText, cbdata);
    			}
   			}
		};
		httpReq.send(data);
	},
	openPostXML : function(url, data, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("POST",url,true);
		httpReq.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
        		if (ok) {
            		if(handler) handler(httpReq.responseXML, cbdata);
    			}
   			}
		};
		httpReq.send(data);
	},
	openPostAndState : function(url, data, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("POST",url,true);
		httpReq.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
				if(handler) handler(ok, httpReq.responseText, cbdata);
   			}
		};
		httpReq.send(data);
	},
	openPostXMLAndState : function(url, data, handler, cbdata) {
    	var httpReq = this.createHttpControl();

    	httpReq.open("POST",url,true);
		httpReq.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');

    	httpReq.onreadystatechange = function() {     
        	if (httpReq.readyState == 4) {
            	var ok
        		try {
            		ok = httpReq.status && httpReq.status == 200
        		} 
        		catch (e) {
            		ok = false 
        		}
				if(handler) handler(ok, httpReq.responseXML, cbdata);
   			}
		};
		httpReq.send(data);
	},	createHttpControl : function() {
   		try {
      		if (window.XMLHttpRequest) {
         		var req = new XMLHttpRequest()

         		if (req.readyState == null) {
            		req.readyState = 1
            		req.addEventListener("load", function () {
               		req.readyState = 4
               		if (typeof req.onreadystatechange == "function") {
                  		req.onreadystatechange()
               		}
            		}, false)
         		}

         		return req
      		}
      		if (window.ActiveXObject) {
         		return new ActiveXObject(this.getControlPrefix() + ".XmlHttp")
      		}
   		}
   		catch (ex) {}
   		// fell through
   		throw new Error("Your browser does not support XmlHttp objects")
	},
	getControlPrefix : function() {
   		if (this.prefix) {
      		return this.prefix
   		}

   		var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"]
   		var o, o2
   		for (var i = 0; i < prefixes.length; i++) {
      		try {
         		// try to create the objects
         		o = new ActiveXObject(prefixes[i] + ".XmlHttp")
         		o2 = new ActiveXObject(prefixes[i] + ".XmlDom")
         		return this.prefix = prefixes[i]
      		}
      		catch (ex) {}
   		}
   		throw new Error("Could not find an installed XML parser")
	}
}
