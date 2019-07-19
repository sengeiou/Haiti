/**
 커뮤니케이션 로그
*/

var commLogGridColModel;
var commLogGridStore;
var commLogGridWith;
var commLogColWith;	
var commLogPagingSize;


// 그리드 로딩
function commLogGridLoad(searchConditionArray){
	commLogGridStore.baseParams = {};
	commLogGridStore.setBaseParam('startDate', searchConditionArray[7]);
	commLogGridStore.setBaseParam('endDate', searchConditionArray[8]);
	commLogGridStore.setBaseParam('supplierId', searchConditionArray[9]);
	commLogGridStore.setBaseParam('operation', searchConditionArray[10]);	
	commLogGridStore.setBaseParam('receiverId', searchConditionArray[3]);	  // 장비아이디
	commLogGridStore.setBaseParam('svcTypeCode', searchConditionArray[17]);
	commLogGridStore.setBaseParam('operationCode', searchConditionArray[15]);
	
	commLogGridStore.load({params:{start: 0, limit: commLogPagingSize}});
}


// 오퍼레이션 코드를 포함해서 리로딩
function commLogGridReLod(timeGap, openTime, codeValue, deviceCode){
	commLogGridStore.setBaseParam('timeGap', timeGap);
	commLogGridStore.setBaseParam('eventTime', openTime);	
	commLogGridStore.setBaseParam('operationCode', codeValue);
	commLogGridStore.setBaseParam('receiverId', deviceCode);	
	commLogGridStore.load({params:{start: 0, limit: commLogPagingSize}});
}

// 그리드 스토어 설정
function initCommLogGridStore(){
	commLogGridStore = new Ext.data.JsonStore({
		url : ctxPath + "/gadget/device/logAnalysis/getCommLogGridData.do",
		totalProperty : 'commloggriddatacount',
		root : 'listcommlog',
		fields: [			 
			{ name: 'idx1', type: 'Integer' },
			{ name: 'time_org', type: 'String' },	
			{ name: 'time', type: 'String' },				
			{ name: 'sender', type: 'String' },
			{ name: 'receiver', type: 'String' },
			{ name: 'receiver_desc', type: 'String' },			
			{ name: 'operationCode', type: 'String' },
			{ name: 'result', type: 'String' }
		]
	});	
}


// 그리드 컬럼모델 생성
function initCommLogGridColModel(fmtCommLogTitle){
	commLogGridColModel = new Ext.grid.ColumnModel({
		columns: [
			{ header: fmtCommLogTitle[0], dataIndex: 'idx1', width: 50}
		   ,{ header: 'time_org', dataIndex: 'time_org', hidden: true}
		   ,{ header: fmtCommLogTitle[1], dataIndex: 'time', width: commLogColWith + 90}		   
		   ,{ header: fmtCommLogTitle[2], dataIndex: 'sender', width: commLogColWith + 30 }
		   ,{ header: 'receiver', dataIndex: 'receiver', width: commLogColWith + 80, hidden: true }
   		   ,{ header: fmtCommLogTitle[3], dataIndex: 'receiver_desc', width: commLogColWith + 80 }
		   ,{ header: fmtCommLogTitle[4], dataIndex: 'operationCode', width: commLogColWith + 70}
		   ,{ header: fmtCommLogTitle[5], dataIndex: 'result',  width: commLogColWith + 70}	   	   
		],
		defaults: {
			 sortable: true
			,menuDisabled: true
			,width: commLogColWith
			,align: 'center'
			,renderer: addTooltip
	   }
	});
}

// 그리드 초기화
function initCommLogGrid(fmtCommLotTitle, gridPagingSize) {
	commLogGridWith = $("#commLogGridDiv").width();
	commLogColWith = (commLogGridWith - 50) / 7 - chromeColAdd;

	commLogPagingSize = gridPagingSize;  // 페이징 사이즈 설정
	initCommLogGridStore();
	initCommLogGridColModel(fmtCommLotTitle);
	
	commLogGrid = new Ext.grid.GridPanel({
		store : commLogGridStore,
		colModel : commLogGridColModel,
		sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
		autoScroll : false,
		width : commLogGridWith - 10,
		height : 420,
		stripeRows : true,
		columnLines : true,			
		loadMask : {
			msg: 'loading...'
		},
		renderTo : 'commLogGridDiv',
		viewConfig : {
			enableRowBody : true,
			showPreview : true,
			emptyText : 'No data to display'
		},
		// paging bar on the bottom
		bbar : new Ext.PagingToolbar({
			pageSize : commLogPagingSize,
			store : commLogGridStore,
			displayInfo : true,
			displayMsg : ' {0} - {1} / {2}',
			emptyMsg: 'No Data'
		}),	
		listeners: {
			rowclick: function(grid, rowIndex, e){
				var record = grid.getSelectionModel().getSelected();
				
				eventLogGridReLod(
                      $('#timeGapSelect').val()     // time gap
					, record.get('time_org')    // Commlog 시간 
					, record.get('receiver')
				); // 장비코드
			}
		}		
	});
};