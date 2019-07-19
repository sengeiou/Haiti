define([
    "framework/Config/ChartConfig",
    "FChartStyle"
], function(ChartConfig) {
	
	var styleScope = window;
	
	var styleMap = {
		"StackedColumn3D": "json_fChartStyle_StColumn3D_nobg",
		"MSColumn3D": "json_fChartStyle_StColumn3D_nobg",
		"MSCombiDY2D": "json_fChartStyle_Column2D_nobg",
		"AngularGauge": "",
		"Pie3D":"fChartStyle_Pie3D",
		"MSBar3D": "json_fChartStyle_StColumn3D_nobg"
	};
	
	var getConfigStyle = function(type) {
		var k = styleMap[type];
		return styleScope[k];
	};
	
	var createChart = function(opt, appendStyle) {
		var chart = new FusionCharts({
			registerWithJS : true,
			swfUrl: opt.url, 
	        id: opt.chartId,
	        width: opt.width,    
	        height: opt.height,
	        renderAt: opt.id,
	        transparent: opt.transparent,
	        dataFormat: opt.dataFormat
		});
		
		if(!opt.data.chart.caption) {
			opt.data.chart.caption = opt.caption || "";
		}
		if(!opt.data.chart.yaxisname) {
			opt.data.chart.yaxisname = opt.yaxisname || "";
		}
		if(appendStyle) {
			opt.data.chart = Ext.apply(opt.data.chart, appendStyle);
		}

		//console.log(opt);
		
		chart.setJSONData(opt.data);
		chart.render();
        
        return chart;
	};
	
	var render = function(type, spec, appendStyle) {
		var baseInfo = ChartConfig[type];
		if(!baseInfo) {
			throw new Error("invalid chart type");
		}
		var opt = Ext.apply(spec, baseInfo);
		var d = Ext.apply(opt.data.chart, getConfigStyle(type));
		return createChart(opt, appendStyle);
	};

	var renderByXML = function(type, spec, xml) {
		var baseInfo = ChartConfig[type];
		if(!baseInfo) {
			throw new Error("invalid chart type");
		}
		var chart = new FusionCharts(
            baseInfo.url, 
            spec.chartId, 
            spec.width, spec.height, "0", "0");
		chart.setDataXML(xml);          
        chart.setTransparent(baseInfo.transparent);
        chart.render(spec.renderId);

		return chart;
	};
	
	var chartUpdate = function(c, data) {
		c.setJSONData(data);
	};

	return {
		render: render,
		renderByXML: renderByXML,
		update: chartUpdate
	};
});