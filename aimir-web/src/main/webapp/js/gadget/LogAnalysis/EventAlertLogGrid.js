/**
 이벤트 알람 로그
*/

var eventAlertLogGridStore;
var eventAlertLogGridColModel;
var eventAlertGridWith;
var eventAlertGridColWith;	
var eventAlertGridPagingSize;


// 그리드 로딩
function eventAlertLogGridLoad(searchConditionArray){	
	eventAlertLogGridStore.baseParams = {};
	eventAlertLogGridStore.setBaseParam('startDate', searchConditionArray[7]);
	eventAlertLogGridStore.setBaseParam('endDate', searchConditionArray[8]);
	eventAlertLogGridStore.setBaseParam('supplierId', searchConditionArray[9]);
	eventAlertLogGridStore.setBaseParam('activatorId', searchConditionArray[3]);   // 장비아이디
	eventAlertLogGridStore.setBaseParam('eventAlertClass', searchConditionArray[18]);//장애 /이벤트 타입  => EnergyLevelChanged 만 조회
	
	eventAlertLogGridStore.load({params:{start: 0, limit: eventAlertGridPagingSize}});
}

// 장비 코드를 포함해서 리로딩
function eventLogGridReLod(timeGap, eventTime, receiver){
	eventAlertLogGridStore.setBaseParam('timeGap', timeGap);
	eventAlertLogGridStore.setBaseParam('eventTime', eventTime);		
	eventAlertLogGridStore.setBaseParam('activatorId', receiver);	
	eventAlertLogGridStore.load({params:{start: 0, limit: eventAlertGridPagingSize}});
}


// 그리드 스토어 설정
function initEventAlertGridStore(){
	eventAlertLogGridStore = new Ext.data.JsonStore({
		url : ctxPath + "/gadget/device/logAnalysis/getEventAlramLogHistory.do",
		totalProperty : 'eventalertloghistorytotal',
		root : 'eventAlertLogList',
		fields: [
			{ name: 'idx', type: 'Integer' },
			{ name: 'openTime', type: 'String' },
			{ name: 'activatorId', type: 'String' },
			{ name: 'message', type: 'String' }
		]
	});
}


// 그리드 컬럼모델 생성
function initEventAlertLogColModel(fmtEvAlLogTitle){
	eventAlertLogGridColModel = new Ext.grid.ColumnModel({
		columns: [
			{header: fmtEvAlLogTitle[0],  dataIndex: 'idx', width: 50}  // 순번
		   ,{header: fmtEvAlLogTitle[1],  dataIndex: 'openTime', width: eventAlertGridColWith + 90}  // 발생시각
		   ,{header: fmtEvAlLogTitle[2],  dataIndex: "activatorId", width: eventAlertGridColWith + 30} // 장비
           ,{header: fmtEvAlLogTitle[3],  dataIndex: 'message', width: eventAlertGridColWith + 70} // 메시지
		],
		defaults: {
			 sortable: true
			,menuDisabled: true
			,width: eventAlertGridColWith
			,align: 'center'
			,renderer: addTooltip
		}
	});
}

// 그리드 초기화
function initEventAlertLogGrid(fmtEvAlTitle, gridPagingSize){
	eventAlertGridWith = $("#eventAlertLogGridDiv").width();
	eventAlertGridColWith = (eventAlertGridWith - 50) / 7 - chromeColAdd;
	
	eventAlertGridPagingSize = gridPagingSize;  // 페이징 사이즈 설정
	initEventAlertGridStore();
	initEventAlertLogColModel(fmtEvAlTitle);	

	eventAlertLogGrid = new Ext.grid.GridPanel({
		store : eventAlertLogGridStore,
		colModel : eventAlertLogGridColModel,
		sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
		autoScroll : false,
		//width : eventAlertGridWith - 10,
		width : eventAlertGridWith,
		height : 420,
		stripeRows : true,
		columnLines : true,
		loadMask : {
			msg: 'loading...'
		},
		renderTo : 'eventAlertLogGridDiv',
		viewConfig : {
			enableRowBody : true,
			showPreview : true,
			emptyText : 'No data to display'
		},
		bbar : new Ext.PagingToolbar({
			pageSize : eventAlertGridPagingSize,
			store : eventAlertLogGridStore,
			displayInfo : true,
			displayMsg : ' {0} - {1} / {2}',
			emptyMsg: 'No Data'
		})
	});
};