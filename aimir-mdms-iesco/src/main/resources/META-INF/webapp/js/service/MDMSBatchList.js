var searchForm;

Ext.onReady(function() {
	
	var url; 
	
    Ext.define('DataThread', {
        extend: 'Ext.data.Model',
        fields: [
            'BATCH_ID'
			, 'BATCH_TYPE'
			, 'BATCH_STATUS'
			, 'BATCH_CNT'
			, 'BATCH_DATETIME'
        ]
    });
    
    var batchTypeStore = Ext.create('Ext.data.Store', {
	    fields: ['code', 'name'],
	    data : [
	        {"code":"", "name":"ALL"}
	        , {"code":"LP_EM", "name":"LP_EM"}      
	        , {"code":"BILLING_DAY_EM", "name":"BILLING_DAY_EM"}
	        , {"code":"BILLING_MONTH_EM", "name":"BILLING_MONTH_EM"}	        	          
	        , {"code":"METEREVENT_LOG", "name":"METEREVENT_LOG"}
	    ]
	});
    
    var comboBatchType = Ext.create('Ext.form.ComboBox', {
        store: batchTypeStore,
        name: 'batch_type', 
        fieldLabel: 'batch type',
        width: 250,
        displayField: 'name',
        valueField: 'code',
        emptyText: 'select',
        editable: false,
        margin: '0 0 0 10'
    });
	
    store = Ext.create('Ext.data.Store', {
        pageSize: 30,
        model: 'DataThread',
        remoteSort: false,
        proxy: {
            type: 'ajax',
            url: '/mdms/getMDMSBatchList',
            reader: {
            	root: 'resultGrid',
                totalProperty: 'totalCount'
            },
            simpleSortMode: false
        }
    });

    var columns = [
        { 	xtype: 'rownumberer', header : 'No', align: 'center', width: 50},
        {
			dataIndex : 'BATCH_ID',
			text : 'BATCH ID',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'BATCH_TYPE',
			text : 'BATCH TYPE',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'BATCH_STATUS',
			text : 'BATCH STATUS',
			width : 150,
			align : 'center',
			sortable : false,
			renderer: function(dat) {
				if(dat == "1") {
					return "<font color='red'>waiting</font>";
				} else {
					return "<font color='blue'>transfer</font>";
				}
			}
		}, {
			dataIndex : 'BATCH_CNT',
			text : 'BATCH COUNT',
			width : 150,
			align : 'right',
			sortable : false,
			renderer: Ext.util.Format.numberRenderer('0,000')
		}, {
			dataIndex : 'BATCH_DATETIME',
			text : 'BATCH DATETIME',
			width : 150,
			align : 'center',
			sortable : false
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
		        items: [		        	
		        {
		        	xtype: 'datefield',
		        	name: 'yyyymmdd',
		        	fieldLabel: 'batch date',
		        	width: 250,
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
		        comboBatchType,
		        {
		        	xtype: 'textfield',
		        	name: 'batch_id',
		        	fieldLabel: 'batch id',
		        	width: 250,
		            margin: '0 0 0 10',		            
			        submitValue: true,
			        value: '',
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
	        scroll: 'both',
	        bbar: Ext.create('Ext.PagingToolbar', {
				pageSize: 30,
				store: store,
				displayInfo: true,
				displayMsg: '{0} - {1} of {2}',
				emptyMsg: "No data"
			})
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