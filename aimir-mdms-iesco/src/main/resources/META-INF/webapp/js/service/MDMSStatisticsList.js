var searchForm;

Ext.onReady(function() {
	
	var url; 
	
    Ext.define('DataThread', {
        extend: 'Ext.data.Model',
        fields: [
            'batch_type'
			, 'tot_cnt'
			, 'wait_cnt'
			, 'wait_rate'
			, 'succ_cnt'
			, 'succ_rate'
        ]
    });
	
    store = Ext.create('Ext.data.Store', {
        pageSize: 30,
        model: 'DataThread',
        remoteSort: false,
        proxy: {
            type: 'ajax',
            url: '/mdms/getMDMSStatistics',
            reader: {
            	root: 'resultGrid'
            },
            simpleSortMode: false
        }
    });

    var columns = [
        { 	xtype: 'rownumberer', header : 'No', align: 'center', width: 50},
        {
			dataIndex : 'batch_type',
			text : 'batch type',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'tot_cnt',
			text : 'total cnt',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: Ext.util.Format.numberRenderer('0,000')
		}, {
			dataIndex : 'wait_cnt',
			text : 'wait cnt',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: Ext.util.Format.numberRenderer('0,000')
		}, {
			dataIndex : 'wait_rate',
			text : 'wait rate',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: function(data) {
				return "<font color='red'>"+ data + " %</font>";
			}
		}, {
			dataIndex : 'succ_cnt',
			text : 'success cnt',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: Ext.util.Format.numberRenderer('0,000')
		}, {
			dataIndex : 'succ_rate',
			text : 'success rate',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: function(data) {
				return "<font color='blue'>"+ data + " %</font>";
			}
		}
	];   
        
    searchForm = Ext.create('Ext.form.Panel', {		
    	id: 'searchForm',
    	bodyPadding: 10,
    	layout: {
            type: 'vbox'
        },
        fieldDefaults: {		            
    		labelAlign: 'right',
    		labelWidth: 90,
    		labelSeparator: "",
    	    enableKeyEvents: true
    	},
	    items: [
	    	{
		        xtype: 'fieldcontainer',            
		        layout: 'hbox',         
		        defaultType: 'textfield',
		        items: [{
		        	xtype: 'datefield',
		        	name: 'yyyymmdd',
		        	width: 100,
		            margin: '0 0 0 0',
		            format: 'Ymd',
			        submitFormat: 'Ymd',
			        submitValue: true,
			        value: getToday(),
			        listeners:  {
		                specialkey: function (f,e) {    
		                     if (e.getKey() == e.ENTER) {
		                    	 reload();
		                    }
		                }
		            }
		        },
		        {
				    xtype: 'button',
				    text: 'Search',
				    iconCls : 'icon-search',
				    margin: '0 0 0 10',
					handler: function() {
						reload();
					}
				}, {
				    xtype: 'button',
				    iconCls : 'icon-reset',
				    text: 'reset',
				    margin: '0 0 0 3',
					handler: function() {
						reset();
					}
				}, {
			    	xtype: 'tbfill'
			    }]
			}
	    ]
	});
    
	Ext.create('Ext.panel.Panel', {
		id:'Content',	    
	    height: document.documentElement.scrollHeight-150,
	    renderTo: 'idContent',
	    layout: {
	        type: 'vbox',       // Arrange child items vertically
	        align: 'stretch',    // Each takes up full width
	        padding: 5
	    },
	    items: [
	        searchForm,
	    {
	        xtype: 'splitter'   // A splitter between the two child items
	    },		
	    {               // Results grid specified as a config object with an xtype of 'grid'
	        xtype: 'grid',
	        columns: columns,// One header just for show. There's no data,
	        store: store, // A dummy empty data store
	        flex: 1,                                       // Use 1/3 of Container's height (hint to Box layout)
	        id: 'grid',
	        name: 'grid',
	        scroll: 'both'
	    }]
	});
	
	// form reload
	function reload() {
		
		var form = Ext.getCmp('searchForm').getForm();
		
		store.getProxy().extraParams = form.getValues();
		store.loadPage(1);
	}
	
	function reset() {
		var form = Ext.getCmp('searchForm').getForm();
		form.reset();
	}
	
});

function getToday() {
	var now = new Date();
    var nowDate = now.getDate();
    now.setDate( nowDate);     
    return Ext.Date.format(now,'Ymd');
}