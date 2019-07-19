var searchForm;

Ext.onReady(function() {
	
	var url; 
	
    Ext.define('DataThread', {
        extend: 'Ext.data.Model',
        fields: [
            'MDEV_ID'
			, 'METEREVENT_ID'
			, 'OPEN_TIME'
			, 'MESSAGE'			
			, 'TRANSFER_DATETIME'
			, 'INSERT_DATETIME'
			, 'BATCH_ID'
			, 'VALUE'
        ]
    });
    
    store = Ext.create('Ext.data.Store', {
        pageSize: 30,
        model: 'DataThread',
        remoteSort: false,
        proxy: {
            type: 'ajax',
            url: '/mdms/getMDMSEventList',
            reader: {
            	root: 'resultGrid',
                totalProperty: 'totalCount'
            },
            simpleSortMode: false
        }
    });
    
    var transferYNStore = Ext.create('Ext.data.Store', {
	    fields: ['code', 'name'],
	    data : [
	        {"code":"", "name":"ALL"}
	        , {"code":"Y", "name":"Y"}
	        , {"code":"N", "name":"N"}
	    ]
	});
    
    var comboTransferYN = Ext.create('Ext.form.ComboBox', {
        store: transferYNStore,
        name: 'transfer_yn', 
        fieldLabel: 'transfer',
        width: 200,
        displayField: 'name',
        valueField: 'code',
        emptyText: 'select',
        editable: false,
        margin: '0 0 0 10'
    });
    
    var batchYNStore = Ext.create('Ext.data.Store', {
	    fields: ['code', 'name'],
	    data : [
	        {"code":"", "name":"ALL"}
	        , {"code":"Y", "name":"Y"}
	        , {"code":"N", "name":"N"}
	    ]
	});
    var comboBatchYN = Ext.create('Ext.form.ComboBox', {
        store: batchYNStore,
        name: 'batch_yn', 
        fieldLabel: 'batch',
        width: 200,
        displayField: 'name',
        valueField: 'code',
        emptyText: 'select',
        editable: false,
        margin: '0 0 0 10'
    });

    var columns = [
        { 	xtype: 'rownumberer', header : 'No', align: 'center', width: 50},
        {
			dataIndex : 'MDEV_ID',
			text : 'MDEV_ID',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'OPEN_TIME',
			text : 'OPEN_TIME',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'BATCH_ID',
			text : 'BATCH_ID',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'METEREVENT_ID',
			text : 'EVENT',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'TRANSFER_DATETIME',
			text : 'TRANSFER_DATETIME',
			width : 150,
			align : 'center',
			sortable : false
		}, {
			dataIndex : 'INSERT_DATETIME',
			text : 'INSERT_DATETIME',
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
			        	name: 'insert_datetime',
			        	fieldLabel: 'insert date',
			        	width: 200,
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
			        comboTransferYN,
			        comboBatchYN,
			        {
			        	xtype: 'textfield',
			        	name: 'batch_id',
			        	fieldLabel: 'batch id',
			        	width: 200,
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
			        }
		        ]
	    	} // end row 1
	    	, {
	    		xtype: 'fieldcontainer',            
		        layout: 'hbox',         
		        defaultType: 'textfield',
		        items: [
		        	{
			        	xtype: 'datefield',
			        	name: 'yyyymmdd',
			        	fieldLabel: 'yyyymmdd',
			        	width: 200,
			            margin: '0 0 0 0',
			            format: 'Ymd',
				        submitFormat: 'Ymd',
				        submitValue: true,
				        value: '',
				        listeners:  {
			                specialkey: function (f,e) {    
			                     if (e.getKey() == e.ENTER) {
			                    	 reload();
			                    }
			                }
			            }
			        },	{
			        	xtype: 'textfield',
			        	name: 'mdev_id',
			        	fieldLabel: 'mdev_id',
			        	width: 200,
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
			        }, {
			        	xtype: 'textfield',
			        	name: 'transfer_date',
			        	fieldLabel: 'transfer_date',
			        	width: 200,
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
			        }, {
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
					}
		        ]
	    	} // end row 2
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