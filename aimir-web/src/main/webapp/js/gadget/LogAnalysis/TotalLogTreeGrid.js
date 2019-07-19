/**
 Total 로그
*/	
var treeData;
var totalLogTreeGrid;

function totalLogTreeGridLoad(searchConditionArray){
	emergePre();
	$.ajax({
		 type: "POST"
	   , cache: false
//	   , async: false        // 동기식
//	   , timeout: 10000
	   , dataType: "json"
	   , url: ctxPath + "/gadget/device/logAnalysis/getTotalLogTreeGridOper.do"
	   , data: {
			  startDate : searchConditionArray[0]
			, endDate : searchConditionArray[1]
	   }
	   , success: function(json){
			treeData = json.treeList;
			
			if(typeof(treeData) != 'undefined'){
				var totalLogTreeRootNode = new Ext.tree.AsyncTreeNode({
					text: 'root',
					id: 'root',
					draggable:false,
					expended:true,
					children: treeData
				});
			
				totalLogTreeGrid.setRootNode(totalLogTreeRootNode);
				totalLogTreeGrid.render();
			}
			hide();
	   }
	   , error: function(){
		   Ext.Msg.alert("ERROR", "ERROR - Connection error.");
		   hide();
	   }
	});
}

// treegrid column tooltip
function addTreeTooltip(value, values) {
	/*
	if (value != null && value != "" && values != null && values.leaf != null && values.leaf == true) {
		var text = "<fmt:message key="aimir.meterid"/> : " + values.mdsId;

		if (values.contractNumber != null && values.contractNumber != "") {
			text += "<br/><fmt:message key="aimir.contractNumber"/> : " + values.contractNumber;
		}
		return '<span qtip="' + text + '">' + value + '</span>';
	} else {
		return value;
	}
	*/
}



// 그리드 초기화
function initTotalLogTreeGrid(fmtTotalTitle, searchConditionArray){

// 그리드 컬럼모델 생성
	var totalLogTreeGridColModel = [
		    { header: fmtTotalTitle[3], dataIndex: 'dateByView', width: 180, align: 'center'}   // 날짜(표기용)
		   ,{ header: fmtTotalTitle[4], dataIndex: 'logType', width: 50, align: 'center'}     // 로그타입
		//   ,{ header: fmtTotalTitle[5], dataIndex: 'senderId', width: 50, hidden:true}  // 송신(comm log : hidden)
		   ,{ header: fmtTotalTitle[6], dataIndex: 'device', width: 100, align: 'center'}  // 장비
		//   ,{ header: fmtTotalTitle[7], dataIndex: 'userId', width: 30, hidden: true}  // 수행자(oper log : hidden)
		   ,{ header: fmtTotalTitle[8], dataIndex: 'operationCode', width: 150, align: 'center'}  // 명령
		   ,{ header: fmtTotalTitle[9], dataIndex: 'result', width: 150, align: 'center'}  //결과	
		   ,{ header: fmtTotalTitle[10], dataIndex: 'message', width: 150, align: 'center'}  // 메시지 (event log : hidden)
		];


	var totalLogTreeLoader = new Ext.tree.TreeLoader({
	      url: ctxPath + "/gadget/device/logAnalysis/getTotalLogTreeGridData.do"
		, baseParams: {
			  startDate : searchConditionArray[0]
			, endDate : searchConditionArray[1]
			, svcTypeCode : searchConditionArray[2]     // 'C'
			, eventAlertClass : searchConditionArray[3] //33 => EnergyLevelChanged 만 조회
			, timeGap : searchConditionArray[4]
			, device : ''
		}
		, listeners: {
			beforeload: function(loader, node, callback){
				loader.baseParams.startDate = node.attributes.dateByView;
				loader.baseParams.endDate = searchConditionArray[1];
				loader.baseParams.device = node.attributes.device;
			}
			, load: function(loader, node, response){
				//alert('로딩됨 : ' + response.responseText);
			}
//			, loadexception: function(loader, node, response){
//				Ext.Msg.alert('ERROR', response.statusText);
//			}
		}
	});

	totalLogTreeGrid = new Ext.ux.tree.TreeGrid({
//			  anchor: '100%'
			  width : $("#TotalLogGridDiv").width()
			, height: 275
			, renderTo: "TotalLogGridDiv"
			, enableDD: false
			, enableHdMenu : false
			, enableSort : false
			, columns: totalLogTreeGridColModel
			, loader: totalLogTreeLoader
			, rootVisible: false
//			, autoScroll: true
			, loadMask : {
				msg : 'loading...'
			}		
			, listeners:{ 
				click: function(node, e){
/*					selectedNodeId = node.id;
					selectedNodeCode = node.attributes.nodeCode;
					selectedParentNodeId = node.parentNode.id;
					selectedParentNodeCode = node.parentNode.attributes.nodeCode;
					selectedNodePath = node.getPath();					*/
				}
			}
	});
	
}