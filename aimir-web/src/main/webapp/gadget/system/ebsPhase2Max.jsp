<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Bulk Registration MaxGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/js/extjs/ux/css/GroupHeaderPlugin.css" rel="stylesheet" type="text/css"/>

<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* Ext-Js Grid Header style 정의. */
        .x-grid3-hd-inner {
            text-align: center;
            font-weight: bold;
        }
         .selectrow a.x-tree-node-anchor span{
        color : #FF0000 !important;
        font-weight: bold;
    }
        .main_incomer {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/green.png) !important;
        }.incomer {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/blue.png) !important;
        }.feeder {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/yellow.png) !important;
        }.bulk {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/orange.png) !important;
        }.mini_sub {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/purple.png) !important;
        }
        
</style>

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<!-- Group Header -->
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ux/GroupHeaderPlugin.js"></script>

 <%-- TreeGrid 관련 js --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>

<script type="text/javascript" charset="utf-8">

    var supplierId = "";
    var mid = "";
    var gridPageSize = 20;
   //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:0,weekly:0,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;

                    // EBS Monitoring Grid
                    getEbsMonitoringGrid();

                    //getEbsInfoGrid();
                }
            }
    );

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
        //리싸이즈시 패널 인스턴스 kill & reload
        if(!(ebsInfoGrid === undefined)){
            ebsInfoGrid.destroy();            
        }

        if(!(ebsMonitoringGrid === undefined)){
            ebsMonitoringGrid.destroy();            
        }

        ebsInfoGridOn = false;      
        ebsMonitoringGridOn = false;
        getEbsInfoGrid();  
        getEbsMonitoringGrid();   

        // if ( $('#ebsInfoGridDiv').is(':visible')) {
        //     getEbsInfoGrid();
        // }

        // if ( $('#ebsMonitoringGridDiv').is(':visible')) {
        //     getEbsMonitoringGrid();
        // }

    });

    $(document).ready(function() {
        Ext.QuickTips.init();
        // 수정권한 체크
        $("#ebsManagement").show();

        $(function() { $('#_ebsMonitoring').bind('click',function(event) { initEbsMonitoring(); } ); });
        $(function() { $('#_ebsManagement') .bind('click',function(event) { initEbsManagement(); } ); });
        $(function() { $('#_newItem') .bind('click',function(event) { alert('click newitem'); initNewItem(); } ); });
        $(function() { $('#_modifyItem') .bind('click',function(event) { initSingleRegMeter(); } ); });

        // DTS 등록 버튼 클릭 이벤트 생성
        $(function() { $('#btnAddEbsDevice').bind('click',function(event) { addEbsDevice(); } ); });
        $(function() { $('#btnModifyEbsDevice').bind('click',function(event) { modifyEbsDevice(); } ); });
        
        // MainTabs / SubTabs
        $("#ebsMax").tabs();
    //    $("#ebsManagementTabs").subtabs(1);
        $("#addModifyTabs").tabs();
        
        // 지역설정
        locationTreeGoGo('treeDivEbs', 'ebsLoc', 's_locationIdEbs');

        // EBS Type Combobox 생성
        setEbsTypeCombo('s_ebsTypeSelect', true);

        getDefaultThreshold();
    });

    // Create EBS Type Combobox 
    function setEbsTypeCombo(comboId, isAll) {
        // EBS Device Type 초기화
        $.getJSON('${ctx}/gadget/system/getChildCodeOrderBy.do'
                , {'code' : '19.1', 'orderBy' : 'id'}
                , function (returnData){
                    var result = returnData.code;
                    var arr = Array();
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].descr;
                        obj.id=result[i].id;
                        arr[i]=obj
                    };

                  if(isAll){
                        eval($('#' + comboId)).loadSelect(arr);
                    }else{
                         eval($('#' + comboId)).pureSelect(arr);
                    }                    
                    eval($('#' + comboId)).selectbox();
                });
    }

    function getChannelData(mid){
         $.getJSON('${ctx}/gadget/system/getChannelList.do'
                , {'meterId' : mid}
                , function (returnData){
                    var result = returnData.channelList;
                    var arr = Array();
                    for (var i = 0; i < result.length; i++) {
                        var obj = new Object();
                        obj.name=result[i].codeName;
                        obj.id=result[i].codeId;
                        arr[i]=obj
                    };                 
                         
                    eval($('#channelTypeSelect')).pureSelect(arr);                 
                    eval($('#channelTypeSelect')).selectbox();
                });
    }

    function searchEbsDeviceInfo(){
        getEbsInfoGrid();
    }

    function channelChange(channelId){
        // 트리 재구성
        getEbsMonitoringTreeStore(ebsMonitoringGridStore.reader.jsonData.result[0].mid, channelId)
    }

    var meterGridOn = false;
    var meterStore;
    var meterColModel;
    var modelCombo;
    var searchMeterWin;
    var container;
    var searchForm;
    var selGubun ='';
    var searchType ='';
    function searchMeter(gubun, searchGubun) {

        var winWidth = 630;
        var formWidth = 600;
        selGubun = gubun;
        searchType = searchGubun;
       	if(modelCombo == undefined) {
            	modelCombo = new Ext.form.ComboBox(
                    {
                        fieldLabel : '<fmt:message key="aimir.model"/>', // fieldLabel : 필드의 이름표
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'local',
                        id: 'modelCombo',
                        name: 'modelCombo',
                        autoSelect:true,
                        typeAhead : true,
                        triggerAction : 'all',
                        lazyRender : true,
                        listeners:{
                            afterrender: function(combo) {
                            	combo.setValue('All');
                            }
                        },
                        store: new Ext.data.JsonStore({
                            autoLoad : true,
                            url: "${ctx}/gadget/system/getDeviceModelsByVenendorName.do",
                            baseParams: {
                                supplierId : supplierId,
                                'vendorName' : 'Elster',
                                selGubun : selGubun 
                            },
                            root:'deviceModels',
                            fields: ["id", "name"]
                        })
                    });
        } else {
        	Ext.getCmp('modelCombo').setValue('All');
        }
        if(searchForm  == undefined ) {
            	searchForm = new Ext.FormPanel ({
                   labelWidth: 100,
                   frame:true,
                   width: formWidth,
                   plain: true,
                   bodyStyle:'padding:5px 5px 5px 5px',
                   defaultType: 'textfield',
                   items: [{ 
                       xtype:'fieldset',
                       title: '<fmt:message key="aimir.more.personalDetails"/>',
                       bodyStyle:'padding:5px 5px 5px 5px',
                       autoHeight:true,
                       defaultType: 'textfield',                
                       items :[
                            modelCombo,
                            {
                                 fieldLabel : '<fmt:message key="aimir.meterid"/>',
                                 id : 'meterId',
                                 name : 'meterId',
                                 allowBlank : true
                            }
                        ]
                      },{
                    	  xtype:'label',
                    	  text:'if you want to select the meter, please double click cell as follows grid. ',
                    	  name:'info'
                    	  //labelStyle:'font-weight:bold;'
                      }], 
                    buttons : [{
                    text : '<fmt:message key="aimir.button.search"/>',
                    handler : function(me) {
                         var meterStore = new Ext.data.JsonStore({
                            autoLoad: {params:{start: 0, limit: pageSize}},
                            url: "${ctx}/gadget/system/getEbsMeterList2.do",
                            baseParams: {
                                supplierId : supplierId,
                                vendorName    :'Elster',
                                deviceModelId   :(Ext.getCmp('modelCombo').getValue()=='' || Ext.getCmp('modelCombo').getValue()=='All') ? 0 : Ext.getCmp('modelCombo').getValue(),
                                mdsId   : Ext.getCmp('meterId').getValue(),
                                selGubun: selGubun
                            },
                            totalProperty: 'totalCount',
                            root:'result',
                            fields: ["meterId", "mdsId", "location", "model", "installDate", "meterStatus"],
                            listeners: {
                                beforeload: function(store, options){

                                	//Ext.getCmp('modelCombo').getValue()
                                options.params || (options.params = {});
                                Ext.apply(options.params, {
                                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                                         });
                                },
                                load: function(store, options){
                                   /* options.params || (options.params = {});
                                Ext.apply(options.params, {
                                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                                         });*/
                                    var grid = Ext.getCmp('winMeterGrid');
                                    var bottomToolbar = grid.getBottomToolbar();
                                    grid.reconfigure(store, meterColModel);
                                    bottomToolbar.bindStore(store);
                                   // bottomToolbar.onLoad();
                                }
                            }
                        });
                        }
                    }]
                 });
            }
           var pageSize = 20;
           meterStore = new Ext.data.JsonStore({
                            autoLoad: {params:{start: 0, limit: pageSize}},
                            url: "${ctx}/gadget/system/getEbsMeterList2.do",
                            baseParams: {
                                supplierId : supplierId,
                                vendorName      :'Elster',
                                deviceModelId   :(Ext.getCmp('modelCombo').getValue()=='' || Ext.getCmp('modelCombo').getValue()=='All')? 0 : Ext.getCmp('modelCombo').getValue(),
                                mdsId   : Ext.getCmp('meterId').getValue(),
                                selGubun : selGubun
                            },
                            totalProperty: 'totalCount',
                            root:'result',
                            fields: ["meterId", "mdsId", "location", "model", "installDate", "meterStatus"],
                            listeners: {
                                beforeload: function(store, options){
                                options.params || (options.params = {});
                                Ext.apply(options.params, {
                                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                                         });
                                },
                                load: function(store, options){
                                  console.log(store, store.data);
                                 
                                }
                            }
                        });

            meterColModel = new Ext.grid.ColumnModel({
                columns: [
                   {header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId', width: (formWidth-20)/5}
                   ,{header: "<fmt:message key='aimir.location'/>", dataIndex: 'location', width: (formWidth-20)/5}
                   ,{header: "<fmt:message key='aimir.model'/>", dataIndex: 'model', width: (formWidth-20)/5}
                   ,{header: "<fmt:message key='aimir.installationdate'/>", dataIndex: 'installDate', width: (formWidth-20)*1.4/5}
                   ,{header: "<fmt:message key='aimir.status'/>", dataIndex: 'meterStatus', width: ((formWidth-20) * 0.6/5)-6}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(meterGridOn == false) {
                meterGrid = new Ext.grid.GridPanel({
                    store: meterStore,
                    id :'winMeterGrid',
                    name: 'winMeterGrid',
                    colModel : meterColModel,
                    autoScroll:false,
                    width: formWidth,
                    height: 300,
                    stripeRows : true,
                    columnLines: true,
                    selGubun : '',
                    loadMask:{
                        msg: 'loading...'
                    },
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: meterStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    }),
                    listeners:{
                    	rowdblclick : function(me,rowIndex) {
                            var selMdsId = me.getStore().getAt(rowIndex).data.mdsId;
                            setMeterId(selMdsId, searchType);
                            Ext.getCmp('searchMeterWinId').hide();
                            return;
                    	}
                    }
                });
                meterGridOn = true;
            } else {
                meterGrid.setWidth(formWidth);
                meterGrid.reconfigure(meterStore, meterColModel);
            }

            if(container == undefined) {
           		container = new Ext.Container({
                        //itemId : 'CustomerPanel',
                        frame : false,
                        width: winWidth-20,
                        autoScroll : true,
                        padding : '5 5 5 5',
                        items:[
                               searchForm,
                               meterGrid
                               ]
                    });
            }
            if(searchMeterWin== undefined) {
            	searchMeterWin = new Ext.Window({
                title: '<fmt:message key="aimir.more.info"/>',
                id: 'searchMeterWinId',
                applyTo:'searchMeterWinDiv',
                width:winWidth,
                height:400,
                shadow : false,
                autoHeight: true,
                pageX : 400,
                pageY : 130, 
                resizable:false,
                plain: true,
                items: [container],
                closeAction:'hide',                 
                onHide : function(){
                }
            }); 
            }
        Ext.getCmp('searchMeterWinId').show();
    }

    function setMeterId(selMdsId, searchType){
		if(selGubun == 'P'){
        	$("#"+searchType+"ParentMeterId").val(selMdsId);
        }else {
            $("#"+searchType+"MeterId").val(selMdsId);
        }
    }

    // EBS Monitoring Grid
    var ebsMonitoringGridStore;
    var ebsMonitoringGridColModel;
    var ebsMonitoringGrid;
    var ebsMonitoringGridOn = false;
    
    function getEbsMonitoringGrid(){
        //gridWidth = $("#ebsMonitoringGridDiv").width();
        ebsMonitoringGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: gridPageSize}},
            url: "${ctx}/gadget/system/getEbsMonitoringList.do",
            baseParams: {
               supplierId: supplierId,
               type      : 788,
               locationId: $("#s_locationIdEbs").val(),
               meterId   : $('#s_meterId').val(),
               searchType : $('#searchDateType').val() ,
               yyyymmdd  : $('#searchStartDate').val()
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ["MID", "LOC_NAME","THRESHOLD", "IMP_ACTIVE_KWH", "IMP_Q1_REAC_KVARH", "IMP_Q2_REAC_KVARH", "IMP_KVH",
                     "EXP_ACTIVE_KWH", "EXP_Q1_REAC_KVARH", "EXP_Q2_REAC_KVARH", "EXP_KVH"],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                },
                 load: function(store, record, options){
                     $.ajaxSetup({
                         async : false
                     });
                    if(record.length != 0){
                    	getChannelData(record[0].data.MID);
                    	getEbsMonitoringTreeStore(record[0].data.MID, $('#channelTypeSelect').val() );
                    }else{
                    	getChannelData(0);
                    	getEbsMonitoringTreeStore(0, 0);
                    }
                    $.ajaxSetup({
                        async : true
                    });
                }
            }
        });

        ebsMonitoringGridColModel = new Ext.grid.ColumnModel({
            columns: [
                 {header:"<fmt:message key='aimir.ebs.mainincomer'/>" + " " + "<fmt:message key='aimir.meter'/>", dataIndex:'MID', flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.board.location'/>", dataIndex:'LOC_NAME', flex: 1 ,align:'center'}
                ,{header:"<fmt:message key='aimir.threshold'/>" + "[%]" , dataIndex:'THRESHOLD',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.imp'/>", dataIndex:'IMP_ACTIVE_KWH', flex: 1 ,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.exp'/>", dataIndex:'EXP_ACTIVE_KWH', flex: 1 ,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.imp'/>" , dataIndex:'IMP_Q1_REAC_KVARH', flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.exp'/>" , dataIndex:'EXP_Q1_REAC_KVARH', flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.imp'/>" , dataIndex:'IMP_Q2_REAC_KVARH',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.exp'/>" , dataIndex:'EXP_Q2_REAC_KVARH',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.imp'/>" , dataIndex:'IMP_KVH',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.ebs.exp'/>" , dataIndex:'EXP_KVH',flex: 1,align:'center'}
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
            },
            rows:[[
                 {}
                ,{}
                ,{}
                ,{header:'ACTIVE_KWH',colspan:2,align:'center'}
                ,{header:'Q1_REAC_KVARH',colspan:2,align:'center'}
                ,{header:'Q2_REAC_KVARH',colspan:2,align:'center'}
                ,{header:'KVA',colspan:2,align:'center'}
                ]
                ]
        });

        if (ebsMonitoringGridOn == false) {

               ebsMonitoringGrid = new Ext.grid.GridPanel({
                id: 'ebsMonitoringGrid',
                store: ebsMonitoringGridStore,
                cm : ebsMonitoringGridColModel,
                autoScroll: false,
                layout:'fit',
               // width: gridWidth,
                height: 520,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'ebsMonitoringGridDiv',
                viewConfig: {
                    enableTextSelection : true,
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                plugins: [new Ext.ux.plugins.GroupHeaderGrid()],
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: gridPageSize,
                    store: ebsMonitoringGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                }),
                listeners:{
                    rowclick : function(me, rowIndex){

                        var record = me.getStore().getAt(rowIndex).data;
                        getEbsMonitoringTreeStore(record.MID, $('#channelTypeSelect').val() );
                    }
                }
            });
            ebsMonitoringGridOn  = true;

        } else {
            //ebsMonitoringGrid.setWidth(gridWidth);
            ebsMonitoringGrid.reconfigure(ebsMonitoringGridStore, ebsMonitoringGridColModel);
            var bottomToolbar = ebsMonitoringGrid.getBottomToolbar();
            bottomToolbar.bindStore(ebsMonitoringGridStore);
        }
    };

    function getEbsMonitoringTreeStore(mid, channelId){
          ebsMonitoringGridStore = new Ext.data.JsonStore({
            // autoLoad: {params:{start: 0, limit: 10}},
            autoLoad: true,
            url: '${ctx}/gadget/system/getEbsMonitoringTree.do',
            baseParams: {
               meterId   : mid,
               searchType : $('#searchDateType').val(),
               yyyymmdd  : $('#searchStartDate').val(),
               channel : channelId
            },
            reader: new Ext.data.JsonReader({
                root:'result',
                 fields: [
                { name: 'mid', type: 'String' },
                { name: 'total' },
                { name: 'expTotal'},
                { name: 'estiTotal'},
                { name: 'text', type: 'String' },
                { name: 'descr', type: 'String' },
                { name :'iconCls'}
            ]}),
            root:'result',
             fields: [
                { name: 'MID', type: 'String' },
                { name: 'total' },
                { name: 'expTotal'},
                { name: 'estiTotal'},
                { name: 'text', type: 'String' },
                { name: 'descr', type: 'String' },
                { name :'iconCls'}
            ],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                          page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
                },
                load: function(store, record, options){
                	getEbsMonitoringTreeGrid();
                }
            }
        });
    }
    
    // EBS Monitoring Grid
    var ebsMonitoringGridStore;
    var ebsMonitoringGridColModel;
    var ebsMonitoringGrid;
    var ebsMonitoringTreeGridOn = false;
    function getEbsMonitoringTreeGrid(mid){

         var width = $("#ebsChartDiv").width();
         codeMiniGridColModel = [

                {
                    header:"<fmt:message key='aimir.meterid'/>",                 
                    dataIndex:'mid',
                    width: width/4+60,
                    align:'left',
                 }
                 ,{
                    header:"<fmt:message key='aimir.ebs.impValue'/>",                 
                    dataIndex:'total',
                    width: width/4-25,
                    align:'right'
                 }
                 ,{
                    header:"<fmt:message key='aimir.ebs.expValue'/>",                 
                    dataIndex:'expTotal',
                    width: width/4-25,
                    align:'right'
                 }
                 ,{
                    header:"estimation value",                 
                    dataIndex:'estiTotal',
                    width: width/4-25,
                    align:'right'
                 }
            ];

            codeTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'result',
                allowChildren: true,
                draggable:false,
                expanded: true,
                children:ebsMonitoringGridStore.reader.jsonData.result
            });
            
            if (!ebsMonitoringTreeGridOn) {
                codeMiniGrid =  new Ext.ux.tree.TreeGrid({
                renderTo: Ext.getBody(),//
                width: width,
                height: 262,
                store : ebsMonitoringGridStore,
                enableDD: false,
                root: codeTreeRootNode,               
                columns: codeMiniGridColModel,
                useArrows: true,  
                renderTo: "ebsChartDiv",
                });
                ebsMonitoringTreeGridOn = true;
            } else{
                codeMiniGrid.setWidth(width);
                codeMiniGrid.setRootNode(codeTreeRootNode);
                codeMiniGrid.render();
            }
    };

    // EBS Information Grid
    var ebsInfoGridStore;
    var ebsInfoGridColModel;
    var ebsInfoGrid;
    var ebsInfoGridOn = false;
    function getEbsInfoGrid(){
    	var ebsInfoWidth = $("#ebsInfoGridDiv").width();

        ebsInfoGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: gridPageSize}},
            url: "${ctx}/gadget/system/getEbsDeviceList.do",
            baseParams: {
               supplierId: supplierId,
               type      : $('#s_ebsTypeSelect').val(),
               locationId: $("#s_locationIdEbs").val(),
               meterId   : $('#s_meterId').val()
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ["ID", "LOC_ID", "TYPE_NAME", "MID", "PARENT_MID", "LOSS", "LOC_NAME", "ADDR", "DESCR"],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                },
                load: function(){
                	ebsInfoWidth = $("#ebsInfoGridDiv").width();
                	ebsInfoGrid.setWidth(ebsInfoWidth);
                }
            }
        });

        if(ebsInfoGridOn == false) {
               checkSelModel = new Ext.grid.CheckboxSelectionModel({
                checkOnly:true
                ,dataIndex: 'ID'
            });
        }

        ebsInfoGridColModel = new Ext.grid.ColumnModel({
            columns: [
                 checkSelModel
                ,{header:"<fmt:message key='aimir.header.type'/>", dataIndex:'TYPE_NAME', flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.meterid'/>", dataIndex:'MID', flex: 1 ,align:'center'}
                ,{header:"parent " + "<fmt:message key='aimir.meterid'/>" , dataIndex:'PARENT_MID', flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.loss'/>" , dataIndex:'LOSS',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.location'/>" , dataIndex:'LOC_NAME',flex: 1,align:'center'}
                ,{header:"<fmt:message key='aimir.address'/>" , dataIndex:'ADDR',flex: 1 ,align:'center'}
                ,{header:"<fmt:message key='aimir.description'/>" , dataIndex:'DESCR',flex: 20 ,align:'center'}
               
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                //,width: ((width-30)/4)-chromeColAdd
            }
        });

        if (ebsInfoGridOn == false) {
               ebsInfoGrid = new Ext.grid.GridPanel({
                id: 'ebsInfoGrid',
                layout:'fit',
                store: ebsInfoGridStore,
                cm : ebsInfoGridColModel,
                sm: checkSelModel,
                autoScroll: false,
                width: ebsInfoWidth,
                height: 520,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'ebsInfoGridDiv',
                viewConfig: {
                    enableTextSelection : true,
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: gridPageSize,
                    store: ebsInfoGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}',
                    items:[
                        '->',{
                        // xtype : 'button',
                         text : '<fmt:message key='aimir.button.delete'/>',
                         handler: function() {
                           deleteEbsDevice();
                        }
                    }]
                }),
                listeners:{
                    rowclick : function(me, rowIndex){
                       
                       var record = me.getStore().getAt(rowIndex).data;
                       // 변경 폼 생성
                       modifyItem(record);
                    }
                }
            });
            ebsInfoGridOn  = true;

        } else {
            ebsInfoGrid.setWidth(ebsInfoWidth);
            ebsInfoGrid.reconfigure(ebsInfoGridStore, ebsInfoGridColModel);
            var bottomToolbar = ebsInfoGrid.getBottomToolbar();
            bottomToolbar.bindStore(ebsInfoGridStore);
        }
    };

    function addEbsDevice(){
        if(validationChk($("#addMeterId").val(), $("#addParentMeterId").val(), $("#addThreshold").val(), $("#locationIdAddEbs"), $('#addEbsTypeSelect option:selected').text())){
             Ext.Msg.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>',
                    function(btn) {
                         if (btn == "yes") {
                                //var meterType =$('#singleRegMeterMeterType option:selected').val();
                                var params = {"supplierId" : supplierId,
                                              "type" : $("#addEbsTypeSelect").val(),
                                              "meterId" : $("#addMeterId").val(),
                                              "parentMeterId": $("#addParentMeterId").val(),
                                              "threshold" : $("#addThreshold").val(),
                                              "locationId" : $("#locationIdAddEbs").val(),
                                              "address" : $("#addAddress").val(),
                                              "description" : $("#addDescription").val()};

                                $.post("${ctx}/gadget/system/insertEbsDevice.do",
                                       params,
                                       function(json) {
                                           if (json.result == "success") {
                                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>',
                                               function() {
                                                   // 입력항목 clear
                                                   reset();

                                                   // 그리드 store reload
                                                   getEbsInfoGrid();
                                               });
                                           } else {
                                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                                           }

                                           return;
                                       }
                                );
                         }
                    });
        }
    }

    function modifyEbsDevice(){

        if(validationChk($("#modifyMeterId").val(),$("#modifyParentMeterId").val(),$("#modifyThreshold").val(), $("#locationIdModifyEbs"), $("#modifyEbsTypeSelect").val())){
             Ext.Msg.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>',
                     function(btn) {
                         if (btn == "yes") {
                                //var meterType =$('#singleRegMeterMeterType option:selected').val();
                                var params = {"supplierId" : supplierId,
                                              "id" : $('#id').val(),
                                              "meterId" : $("#modifyMeterId").val(),
                                              "parentMeterId": $("#modifyParentMeterId").val(),
                                              "threshold" : $("#modifyThreshold").val(),
                                              "locationId" : $("#locationIdModifyEbs").val(),
                                              "address" : $("#modifyAddress").val(),
                                              "description" : $("#modifyDescription").val()};

                                $.post("${ctx}/gadget/system/modifyEbsDevice.do",
                                       params,
                                       function(json) {
                                           if (json.result == "success") {
                                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>',
                                               function() {
                                                   // 입력항목 clear
                                                   reset();

                                                   // 그리드 store reload
                                                   getEbsInfoGrid();
                                               });
                                           } else {
                                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                                           }

                                           return;
                                       }
                                );
                         }
                     });
        }
    }

function deleteEbsDevice(){
    
     Ext.Msg.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>',
             function(btn) {
                 if (btn == "yes") {
                    var chkSelModel = checkSelModel.getSelections();
                    if(chkSelModel.length == 0){
                        Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.delete"/>');
                        return false;
                    }else{
                        var delEbsIds = new Array();
                        for(i=0; i< chkSelModel.length; i++){
                            delEbsIds.push(chkSelModel[i].get("ID"));
                        }

                        var params = {
                                "delEbsIds" : delEbsIds
                        };

                       $.post("${ctx}/gadget/system/deleteEbsDevice.do",
                                params,
                                function(result) {
                    	   			Ext.MessageBox.alert('Delete', "<fmt:message key='aimir.total'/> : " + chkSelModel.length + ", <fmt:message key='aimir.success.count'/> : " + result.successCnt);
                    	   			getEbsInfoGrid()
                                    
                                    return;
                                },
                                "json"
                            );
                    }
                 }
             });
    }

    // DTS Insert 입력값 체크
    function validationChk(meterId, parentId, threshold, locationId, type) {
        if (meterId.length <= 0) {     // Meter ID
            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.select.type"/>');
            return false;
        } else if(type != 'Main Incomer' && parentId == "") {	//Main Incomer가 아닐경우 parent MeterID 를 가져야 한다.
        	Ext.MessageBox.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.ebs.enter.parentMdsId'/>");
            return false;
        } else if(meterId == parentId) {	//동일 미터 아이디가 parent 일 수 없다.
            Ext.MessageBox.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.ebs.select.anotherMdsId'/>");
            return false;
        } else if (threshold.length <= 0) {     // 임계치
            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.loss"/>');
            return false;
        }  else if (locationId.val().length <= 0) {     // 지역
            Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.ebs.msg.validation.location"/>');
            return false;
        } else if(threshold.length != 0 && (threshold).match(/[^0-9.]+/)){ // 임계치(%)의 숫자 체크
            Ext.MessageBox.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.ebs.Loss.number'/>");
            return false;
        } else {
            return true;
        }
    }

    var defaultThreshold;
    // Substation의 Default임계치를 설정파일에서 취득한다.
    function getDefaultThreshold() {
         $.getJSON('${ctx}/gadget/system/getDefaultThreshold.do'
                 ,''
                 , function(json) {
                     defaultThreshold = json.defaultThreshold;
                   });
    }

    // 최종통신날짜 및 조건 검색에 대한 reset 설정
    function reset() {
        // Form Reset
        var $searchForm = $("form[name=newItem]");
        $searchForm.trigger("reset");

        // 셀렉트 태그 첫번째 인덱스 선택
        var $selects = $searchForm.find("select");
        $selects.each(function() {
            $(this).selectbox();
        });

        // Threshold폼에 Default값 설정
        $("#addThreshold").val(defaultThreshold);

        return;
    }

    function initEbsMonitoring(){
        //getEbsMonitoringGrid();
        //searchEbsMainIncomerInfo();
    }

    function initEbsManagement(){
        // new item tab 초기화
        initNewItem();

        // grid 초기화
        getEbsInfoGrid();

    }

    function initNewItem() {
        setEbsTypeCombo('addEbsTypeSelect');

        // 지역설정
        locationTreeGoGo('treeDivAddEbs', 'addEbsLoc', 'locationIdAddEbs');
    }

    function modifyItem(record) {

        $("#addModifyTabs").tabs("select", 1);

        // 폼 설정
        $('#id').val(record.ID);
        $('#modifyEbsTypeSelect').val(record.TYPE_NAME);
        $('#modifyMeterId').val(record.MID);
        $('#modifyParentMeterId').val(record.PARENT_MID);
        $('#modifyThreshold').val(record.LOSS);
        $('#modifyEbsLoc').val(record.LOC_NAME);
        $('#locationIdModifyEbs').val(record.LOC_ID);
        $('#modifyAddress').val(record.ADDR);
        $('#modifyDescription').val(record.DESCR);

        // 지역설정
        locationTreeGoGo('treeDivModifyEbs', 'modifyEbsLoc', 'locationIdModifyEbs');
    }

    function searchEbsMonitoringList() {

        getEbsMonitoringGrid();
    }
    </script>

</head>
<body>
   <div id='searchMeterWinDiv'></div>
    <!-- 탭 전체 (S) -->
    <div id="ebsMax">
        <ul>
         <li><a href="#ebsMonitoring" id="_ebsMonitoring" ><fmt:message key="aimir.monitoring"/></a></li>
         <li><a href="#ebsManagement"  id="_ebsManagement"><fmt:message key="aimir.management"/></a></li>
        </ul>

        <!-- 1RD 탭 : 모니터링 (S) -->
        <div id="ebsMonitoring">
            <!-- 1RD 탭의 내용 : 검색조건 (S) -->
            <div class="search-bg-withouttabs">
                <!-- 날짜 검색 추가 -->
                <div >
                <%@ include file="/gadget/commonDateTabButtonType.jsp"%>

                 <div id="btn"><ul style="margin-left: 0px"><li><a href="javascript:searchEbsMonitoringList()" class="on"><fmt:message key="aimir.button.search" /></a></li></ul></div>
                </div>
                <div class="height5px"></div>
            </div>
            <!-- 1RD 탭의 내용 : 검색조건 (E) -->

            <!-- 1RD 탭의 내용 : Monitoring Tab (S) -->
            <div class="monitoringleft_ebs">
                <ul><li>
                     <!--  검색 결과 grid -->
                     <div id="ebsMonitoringGridDiv"></div>

                </li></ul>
            </div>
            <div class='monitoringright_ebs'>
                <ul><li>
                    <div><select id="channelTypeSelect" style="width:240px;" onChange="javascript:channelChange(this.value);"></select></div>
                    <div class="height5px"></div>
                    <div class="margin-r10" id ='ebsChartDiv'></div>
                </li></ul>
            </div>
        </div>
        <!-- 1RD 탭 : Monitoring Tab (E) -->

        <!-- 2ND 탭 : 관리 (S) -->
        <div id="ebsManagement">
            <!-- 2ND 탭 : 서브탭 전체 (S)-->
             <div class="search-bg-withouttabs">
                <div class="padding-b3px">You can add or modify a main incomer/incomer/feeder/mini Sub information.</div>
                <!-- 
                <ul>
                    <li><a href="#addModify" id="_addModify"  >$$Add/Modify </a></li>
                  <li><a href="#bulkProcessing" id="_bulkProcessing">$$Bulk Processing</a></li>
                </ul>
                -->
                <div class="searchbox" style="padding:7px 5px 5px 5px;">
                    <table class="searching">
                        <tr>
                            <td class="withinput"><fmt:message key="aimir.header.type"/></td>
                            <td id="s_ebsTypeSelectDiv" class="margin-t1px" >
                            <span><select name="select" id="s_ebsTypeSelect" style="width:120px"></select></span>
                            </td>
                            <td class="withinput"><fmt:message key="aimir.meterid"/></td>
                            <td><input type="text" id="s_meterId" name="ebsLoc"></td>    
                            <td class="withinput"><fmt:message key="aimir.location"/></td>
                            <td>
                            <input type="text" id="ebsLoc" name="ebsLoc">
                            <input type="hidden" id="s_locationIdEbs" name="location.id" value="" />
                            </td>
                            <td>
                                <div id="btn"><ul style="margin-left: 0px"><li><a href="javascript:searchEbsDeviceInfo()" class="on"><fmt:message key="aimir.button.search" /></a></li></ul></div>
                            </td>
                        </tr>
                    </table>
                    <div id="treeDivEbsOuter" class="tree-billing auto" style='display:none;'>
                        <div id="treeDivEbs"></div>
                    </div>
                </div>

                <div class="monitoringleft_ebs" id='bodyleft_ebs'>
                    <ul><li>
                        <div>If you want to modify information, please double click cell as follows grid.</div>
                        <div class="height5px"></div>
                         <!--  검색 결과 grid -->
                        <div class="margin-r10">
                            <div id="ebsInfoGridDiv"></div>
                        </div>
                        
                    </li></ul>
                </div>

                <div id="addModifyTabs" class="monitoringright_ebs" >
                                <div style="position: absolute; right: 0; padding-right: 20px"><fmt:message key="aimir.hems.inform.requiredField"/></div>
                                <ul>
                                  <li><a href="#newItem"><fmt:message key='aimir.newItem'/></a></li>
                                  <li><a href="#modifyItem"><fmt:message key='aimir.modifyItem'/></a></li>
                                </ul>

                                <div id="newItem" style="width:95%; padding:10px; text-align:center;">
                                    <form name="newItem">
                                          <div style="height:54px;">
                                           <table class="dts_regist">
                                            <tr><th class="blue11pt"><fmt:message key="aimir.header.type"/><em class="icon_star">&nbsp;</em><!-- EBS Device Type--></th>
                                                <td>
                                                    <select id="addEbsTypeSelect" ></select>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.meterid"/><em class="icon_star">&nbsp;</em><!-- Meter Number --></th>
                                                <td>
                                                    <input type="text" id="addMeterId" style="width: 150px;"/>
                                                    <div id="btn">
                                                        <ul><li><a href="javascript:searchMeter('C','add')" id="btnSearchMeter" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.parentMdsId"/><!-- Parent Meter Number --></th>
                                                <td>
                                                    <input type="text" id="addParentMeterId" style="width: 150px;"/>
                                                    <div id="btn">
                                                        <ul><li><a href="javascript:searchMeter('P','add')" id="btnSearchMeter" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                                    </div>
                                                </td>
                                            </tr>
                                           
                                            <tr><th class="blue11pt"><fmt:message key="aimir.loss"/> [%]<em class="icon_star">&nbsp;</em><!-- 임계치 --></th>
                                                <td class="blue11pt"><input type="text" id="addThreshold" style="width: 100px;"/>
                                                  <span class="blue11pt" style="overflow:hidden;">&nbsp;&nbsp;&nbsp;&nbsp;(&nbsp;<fmt:message key="aimir.default"/>&nbsp;<fmt:message key="aimir.loss"/>&nbsp;:&nbsp;</span>
                                                  <span class="blue11pt" style="overflow:hidden;" id="dspThreshold"></span>
                                                  <span class="blue11pt" style="overflow:hidden;">)</span>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.location"/><em class="icon_star">&nbsp;</em><!-- 지역 --></th>
                                                <td>
                                                    <input name="addSearchWord" id='addEbsLoc' class="billing-searchword" type="text" style="width:130px;" value='<fmt:message key="aimir.board.location"/>'/>
                                                    <input type='hidden' id='locationIdAddEbs' value=''></input>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.address"/><!-- 주소 --></th>
                                                <td><input type="text" id="addAddress"/></td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.description"/><!-- 설명 --></th>
                                                <td align="left"><textarea id="addDescription" rows="3" style="width:95%" ></textarea></td>
                                            </tr>
                                        </table>
                                        <div id="treeDivAddEbsOuter" class="tree-billing auto" style='display:none;'>
                                            <div id="treeDivAddEbs"></div>
                                        </div>
                                        <div id="btn" class="btn_right_bottom">
                                           <ul><li><a href="#;" id="btnAddEbsDevice" class="on"><fmt:message key="aimir.button.register" /></a></li></ul>
                                        </div>
                                    </div>
                                </form>
                                </div>
                                
                                <div id="modifyItem"  style='display:block;'>
                                    <form name="modifyItem">
                                          <div style="height:54px;">
                                           <table class="dts_regist">
                                            <tr><th class="blue11pt"><fmt:message key="aimir.header.type"/><em class="icon_star">&nbsp;</em><!-- EBS Device Type--></th>
                                                <td>
                                                    <input type="text" id="modifyEbsTypeSelect" style="width: 150px;" readonly/>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.meterid"/><em class="icon_star">&nbsp;</em><!-- Meter Number --></th>
                                                <td>
                                                    <input type="text" id="modifyMeterId" style="width: 150px;"/>
                                                    <input type="hidden" id="id">
                                                    <div id="btn">
                                                        <ul><li><a href="javascript:searchMeter('C','modify')" id="btnSearchMeter" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.parentMdsId"/><!-- Parent Meter Number --></th>
                                                <td>
                                                    <input type="text" id="modifyParentMeterId" style="width: 150px;"/>
                                                    <div id="btn">
                                                        <ul><li><a href="javascript:searchMeter('P','modify')" id="btnSearchMeter" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                                                    </div>
                                                </td>
                                            </tr>
                                           
                                            <tr><th class="blue11pt"><fmt:message key="aimir.loss"/> [%]<em class="icon_star">&nbsp;</em><!-- 임계치 --></th>
                                                <td class="blue11pt"><input type="text" id="modifyThreshold" style="width: 100px;"/>
                                                  <span class="blue11pt" style="overflow:hidden;">&nbsp;&nbsp;&nbsp;&nbsp;(&nbsp;<fmt:message key="aimir.default"/>&nbsp;<fmt:message key="aimir.loss"/>&nbsp;:&nbsp;</span>
                                                  <span class="blue11pt" style="overflow:hidden;" id="dspThreshold"></span>
                                                  <span class="blue11pt" style="overflow:hidden;">)</span>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.location"/><em class="icon_star">&nbsp;</em><!-- 지역 --></th>
                                                <td>
                                                    <input name="addSearchWord" id='modifyEbsLoc' class="billing-searchword" type="text" style="width:130px;" value='<fmt:message key="aimir.board.location"/>'/>
                                                    <input type='hidden' id='locationIdModifyEbs' value=''></input>
                                                </td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.address"/><!-- 주소 --></th>
                                                <td><input type="text" id="modifyAddress"/></td>
                                            </tr>
                                            <tr><th class="blue11pt"><fmt:message key="aimir.description"/><!-- 설명 --></th>
                                                <td align="left"><textarea id="modifyDescription" rows="3" style="width:95%" ></textarea></td>
                                            </tr>
                                        </table>
                                        <div id="treeDivModifyEbsOuter" class="tree-billing auto" style='display:none;'>
                                            <div id="treeDivModifyEbs"></div>
                                        </div>
                                        <div id="btn" class="btn_right_bottom">
                                           <ul><li><a href="#;" id="btnModifyEbsDevice" class="on"><fmt:message key="aimir.bems.facilityMgmt.update" /></a></li></ul>
                                        </div>
                                    </div>
                                </form>
                                </div>         
                            </div>
              

             
            </div>
            <!-- 2ND 탭 : 서브탭 전체 (E)-->
        </div>
        <!-- 2ND 탭 : Management (E) -->

    </div>
    <!-- 탭 전체 (E) -->



    </body>
</html>

