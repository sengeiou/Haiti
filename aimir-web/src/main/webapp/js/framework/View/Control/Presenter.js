define([
	"framework/RowEditor",
	"framework/SlidingPager" ], 
function(loader) {
	
	var App = new Ext.App({});
	
	Ext.ns('Ext.ux.grid');
	
	var proxy = new Ext.data.HttpProxy({
	    url: 'data'
	});
	
	var reader = new Ext.data.JsonReader({
	    	totalProperty: 'totalCount',
	    	//successProperty: 'success',
	    	//messageProperty: 'message'  // <-- New "messageProperty" meta-data
	    	root: 'items',
		},
		['CUSIP', 'DESCRIPTION', 'COUPONRATE', 'ASKPRICE']
	);
	
	var writer = new Ext.data.JsonWriter({
	    encode: false
	});
	
	var store = new Ext.data.Store({
	    restful: true,
	    proxy: proxy,
	    reader: reader
	    //,writer: writer
	});

	store.load();
	
	Ext.data.DataProxy.addListener('beforewrite', function(proxy, action) {
	    App.setAlert(App.STATUS_NOTICE, "Before " + action);
	});
	
	Ext.data.DataProxy.addListener('write', function(proxy, action, result, res, rs) {
	    App.setAlert(true, action + ':' + res.message);
	});
	
	Ext.data.DataProxy.addListener('exception', function(proxy, type, action, options, res) {
	    App.setAlert(false, "Something bad happend while executing " + action);
	});
	
	var userColumns =  [
	    { header: 'CUSIP', dataIndex: 'CUSIP' },
        { header: 'Description', dataIndex: 'DESCRIPTION', width: 100, editor: new Ext.form.TextField({}) },
        { header: 'COUPONRATE', dataIndex: 'COUPONRATE', width: 100, editor: new Ext.form.TextField({}) },
        { header: 'ASKPRICE', dataIndex: 'ASKPRICE', width: 100, editor: new Ext.form.TextField({}) }
    ];
	
	//Ext.QuickTips.init();
	
	var editor = new Ext.ux.grid.RowEditor({
        saveText: '수정',
        cancelText: '취소'
    });
	
	var userGrid = new Ext.grid.GridPanel({
        store: store,
        plugins: [editor],
        tbar: [{
            text: '새 데이터 추가',
            iconCls: 'silk-add',
            handler: onAdd
        }, '-', {
            text: '선택된 데이터 삭제',
            iconCls: 'silk-delete',
            handler: onDelete
        }, '-'],
        defaults: {
            width: 120,
            sortable: true
        },
        columns: userColumns,
        bbar: new Ext.PagingToolbar({
            pageSize: 10,
            store: store,
            displayInfo: true,

            plugins: new Ext.ux.SlidingPager()
        }),
        viewConfig: {
        	forceFit: true
        },
        renderTo: 'grid-example',
        sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
        width: 600,
        autoHeight: true,
        frame: true,
        title: 'Framed with Row Selection and Horizontal Scrolling'
    }).render();
	
	function onAdd(btn, ev) {
        var u = new userGrid.store.recordType({
        	'CUSIP': '',
        	'DESCRIPTION': '',
        	'ASKPRICE' : ''
        });
        editor.stopEditing();
        userGrid.store.insert(0, u);
        editor.startEditing(0);
    }
    
    function onDelete() {
    	console.log(arguments);
        var rec = userGrid.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        userGrid.store.remove(rec);
    }
});