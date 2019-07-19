/**
 오퍼레이션 로그
*/	
var operLogGridStore;
var operLogGridColModel;
var operGridWith;
var operGridColWith;	
var operGridPagingSize;


// 그리드 로딩
function operationLogGridLoad(searchConditionArray){	
	operLogGridStore.baseParams = {};
	operLogGridStore.setBaseParam('targetName', searchConditionArray[3]);  // 장비 아이디
	operLogGridStore.setBaseParam('startDate', searchConditionArray[7]);
	operLogGridStore.setBaseParam('endDate', searchConditionArray[8]);
	operLogGridStore.setBaseParam('supplierId', searchConditionArray[9]);
	operLogGridStore.setBaseParam('operation', searchConditionArray[10]);
	
	operLogGridStore.load({params:{start: 0, limit: operGridPagingSize}});
}

// 그리드 스토어 설정
function initOperGridStore(){
	operLogGridStore = new Ext.data.JsonStore({
		url: ctxPath + "/gadget/device/logAnalysis/getOperationLogGridData.do",
		totalProperty: 'total',
		root:'gridDatas',
		fields: [
			{ name: 'no', type: 'Integer' },
			{ name: 'openTime_org', type: 'Strign'},
			{ name: 'openTime', type: 'String' },
			{ name: 'targetName', type: 'String' },
			{ name: 'accomplisher', type: 'String' } ,
			{ name: 'operation', type: 'String' } ,
			{ name: 'operationStatus', type: 'String' },
			{ name: 'operationCommandCode.code', type: 'String'}
		]
	});	
}

// 그리드 컬럼모델 생성
function initOperGridColModel(fmtOperTitle){
	operLogGridColModel = new Ext.grid.ColumnModel({
	columns: [
			{ header: fmtOperTitle[0], dataIndex: 'no', width: 50 }       // 순번
		   ,{ header: 'openTime_org', dataIndex: 'openTime_org', hidden: true}      //발생 시각			
		   ,{ header: fmtOperTitle[1], dataIndex: 'openTime',	width: operGridColWith + 90}      //발생 시각
		   ,{ header: fmtOperTitle[2], dataIndex: 'targetName', width: operGridColWith + 30}   // 장비
		   ,{ header: fmtOperTitle[3], dataIndex: 'accomplisher', width: operGridColWith + 30}   // 수행자
		   ,{ header: fmtOperTitle[4], dataIndex: 'operation', width: operGridColWith + 70}     // 명령
		   ,{ header: fmtOperTitle[5], dataIndex: 'operationStatus', width: operGridColWith + 70}  //결과
		   ,{ header: 'OpCode', dataIndex: 'operationCommandCode.code', width: operGridColWith + 70, hidden: true}]  //임시
		, defaults : {
			 sortable: true
			,menuDisabled: true
			,width: operGridColWith
			,align: 'center'
			,renderer: addTooltip
		}
	});	
}

// 그리드 초기화
function initOperationLogGrid(fmtOperTitle, gridPagingSize){
	operGridWith = $("#operLogGridDiv").width();
	operGridColWith = (operGridWith - 50) / 7 - chromeColAdd;	
	
	operGridPagingSize = gridPagingSize;  // 페이징 사이즈 설정
	initOperGridStore();
	initOperGridColModel(fmtOperTitle);
	
	operLogGrig = new Ext.grid.GridPanel({
		id : 'operLogGrid',
		store : operLogGridStore,
		colModel : operLogGridColModel,
		sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
		autoScroll : false,
		width : operGridWith - 10,
		height : 420,
		stripeRows : true,
		columnLines : true,
		loadMask : {
			msg : 'loading...'
		},
		renderTo : 'operLogGridDiv',
		viewConfig : {
			enableRowBody : true,
			showPreview : true,
			emptyText : 'No data to display'
		},
		bbar : new Ext.PagingToolbar({
			pageSize : operGridPagingSize,
			store : operLogGridStore,
			displayInfo : true,
			displayMsg : ' {0} - {1} / {2}',
			emptyMsg: 'No Data'
		}),
		listeners: {
			rowclick: function(grid, rowIndex, e){
				var record = grid.getSelectionModel().getSelected();

				// Operation과 장비코드
				commLogGridReLod(
                      $('#timeGapSelect').val()     // time gap
					, record.get('openTime_org')    // Operation 시간
					, record.get('operationCommandCode.code')  
					, record.get('targetName'));
				
				// Event log 클리어
				eventAlertLogGridStore.removeAll();
			}
		}
	});	
}