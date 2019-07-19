define([
	"jquery",
	"framework/Model/HttpService"
], function($, HttpService) {

	var CONTEXT = GLOBAL_CONTEXT.CONTEXT;

	var Locations = {
		bangBaeOne: "방배1동",
		jungMun: "중문동"
	};

	// 이미지 매핑 객체
	var WEATHER_IMAGEMAP = {
		imageRoot: CONTEXT + "/images/weather/",
		weather: {
			"Clear": "Weather-Clear-64.png",
			"Partly Cloudy": "Weather-Few-Clouds-64.png",
			"Little Cloudy": "Weather-Few-Clouds-64.png",
			"Mostly Cloudy": "Weather-Overcast-64.png",
			"Cloudy": "Weather-Overcast-64.png",
			"Rain": "Weather-Showers-64.png",
			"Snow/Rain": "Weather-Showers-Scattered-64.png",
			"Snow": "Weather-Snow-64.png"
		},
		sky: {
			"1": "Clear",
			"2": "Party Cloudy",
			"3": "Mostly Cloudy",
			"4": "Cloudy"
		},
		pty: {
			"0": "Rain or Snow Nothing",
			"1": "Rain",
			"2": "Rain/Snow",
			"3": "Snow/Rain",
			"4": "Snow"
		}
	};

	var getTownWeather = function(params, success, failure) {
		HttpService.ajax({
			url: CONTEXT + '/gadget/bems/getCurrentWeather.do',
			params: {
				location: Locations[params.location]
			},
			method: "POST",
			success: function(res) {
				if(res) {
					res = Ext.util.JSON.decode(res.responseText);
					if(res.weather) {
						res = res.weather;
					}
				}
				res.locationName = Locations[params.location];
				success(res);				
			}, 
			failure: failure
		});
	};

	var getWeekWeather = function(params, success, failure) {
		throw $.extend(new Error("not Implements"), {
			code: 999,
			callee: "getWeekWeather",
			message: "not Implements",
			msg: "[999] not Implements"
		});
	};
	
	return {
		ImageMap: WEATHER_IMAGEMAP,
		getTownWeather: getTownWeather,
		getWeekWeather: getWeekWeather
	};

})