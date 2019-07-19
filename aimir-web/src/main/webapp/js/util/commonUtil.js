var CommonUtil = {
	// String이 "null"인 경우 null로 반환한다.
	nullToNull: function (val) {
		if(val === "null") {
			val = null;
		}
		return val;
	},

	getQueryString: function (param) {
		var result = "?";
		$.each(param, function(key, value) {
			result = result + key + "=" +value + "&"
		});		
		return String(result).substring(0, result.length -1);
	},

	// yyyy -> yy, yy -> y, MM -> M, 
	minimalDatePattern: function (pattern) {
		if(pattern.indexOf("yyyy") > -1) {
			pattern = pattern.replace("yyyy", "yy");
		} else if (pattern.indexOf("yy") > -1) {
			pattern = pattern.replace("yyyy", "y");
		}

		if(pattern.indexOf("MM") > -1) {
			pattern = pattern.replace("MM", "M");
		}
		return pattern;
	}
}


/* HashMap 객체 생성 */
var JqMap = function(){
    this.map = new Object();
}
 
JqMap.prototype = {
    /* key, value 값으로 구성된 데이터를 추가 */
    put: function (key, value) {
        this.map[key] = value;
    },
    /* 지정한 key값의 value값 반환 */
    get: function (key) {
        return this.map[key];
    },
    /* 구성된 key 값 존재여부 반환 */
    containsKey: function (key) {
        return key in this.map;
    },
    /* 구성된 value 값 존재여부 반환 */
    containsValue: function (value) {
        for (var prop in this.map) {
            if (this.map[prop] == value) {
                return true;
            }
        }
        return false;
    },
    /* 구성된 데이터 초기화 */
    clear: function () {
        for (var prop in this.map) {
            delete this.map[prop];
        }
    },
    /*  key에 해당하는 데이터 삭제 */
    remove: function (key) {
        delete this.map[key];
    },
    /* 배열로 key 반환 */
    keys: function () {
        var arKey = new Array();
        for (var prop in this.map) {
            arKey.push(prop);
        }
        return arKey;
    },
    /* 배열로 value 반환 */
    values: function () {
        var arVal = new Array();
        for (var prop in this.map) {
            arVal.push(this.map[prop]);
        }
        return arVal;
    },
    /* Map에 구성된 개수 반환 */
    size: function () {
        var count = 0;
        for (var prop in this.map) {
            count++;
        }
        return count;
    }
}