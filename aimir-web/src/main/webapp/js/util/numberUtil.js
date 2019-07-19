var NumberUtil = {
	thousandSeparator: function (number) {
		number = Number(number).toFixed(0);
		var regularExp = /(\d+)(\d{3})/;
		String(number).replace(/^\d+/, function(w) {
			while ( regularExp.test(w) ) {
				w = w.replace(regularExp, '$1,$2');
			}
			result = w;
		});		
		return result;
	},

	parseNumber: function (str) {
		str = str.replace(/,/g,"");
		return Number(str);
	},

	to2Digit: function (number)	 {
		number = "" + number;
		if ( number < 10 ) {
			number += "0";
		}
		return number;
	}
};