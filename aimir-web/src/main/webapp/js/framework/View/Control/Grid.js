define(function() {
	
	var GridConfig = {
		trackMouseOver: false,
        loadMask: true,    
    	viewConfig: {
    		forceFit: true
    	},
    	enableColumnHide: false, // 그리드 숨기기 기능 끄기
    	enableHdMenu: false, // 그리드 헤더 메뉴 숨기기
    	stripeRows: true
	};

	var displayGrid = function(conf, notAutoload) {
		var grid = undefined;
		var mixConf = Ext.apply(conf, GridConfig);
		if(mixConf.clicksToEdit) {
			grid = new Ext.grid.EditorGridPanel(mixConf);
		}	
		else {
			grid = new Ext.grid.GridPanel(mixConf);
		}
		
		if(!notAutoload) {
			Ext.getCmp(mixConf.id).getStore().load();
		}
		
		return grid;
	};
	
	var render = function(spec, notAutoload) {
		return displayGrid(spec, notAutoload);
	};

	return {
		render: render
	};
});