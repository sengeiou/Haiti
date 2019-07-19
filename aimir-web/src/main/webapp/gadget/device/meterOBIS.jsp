<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<%@ page import="java.util.HashMap"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        .cmdLineDiv {width:auto;}
    </style>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/gadget/dlmsScreen/dlmsDeviceModelSub.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
    var content='';
	var obj;
	var otaUpload;
    var supplierId ="";
    var meterId="";
    var modelId = "";
    var modemDeviceSerial = "";
    var loginId = "";
    var target = "";
    var fwType = "";
    var cmdList='';
    var DeviceModelSelectId = "";
   var  modelName = "";
    var MeterConfigId;
    
    var obisListAuth;
    
    var obisCodeGrid;
    
    var addGrid;
    var addObisStore;
    var addObisModel;
    var selectedObisRecordInfo;
    //MeterParamSet/Get 싱행시 Detail 버튼을 눌렀는지 아닌지를 체크
    var detailCheck=false;
    
    var addDailyProfileStore;
    var addDailyProfileModel;
    var addDAilyProfileGrid;
    
    var saveDetailParamArrList = new Array();
    var selectAttribute;
    
    var parameterGrid;
    
    var editSetting = false;
    
    var winWidth = $('.cmdLineDiv').width();
    var obisCodeParam;
    var classIdParam;

    var getLogObisList = [['0.0.99.98.0.255','7','2', 'cmdMeterParamGet', 'cmdGetStandardEventLog'],
                          ['0.0.99.98.1.255', '7','2','cmdMeterParamGet', 'cmdGetTamperingLog' ],
					      ['1.0.99.97.0.255', '7','2','cmdMeterParamGet', 'cmdGetPowerFailureLog' ],
					      ['0.0.99.98.2.255', '7','2','cmdMeterParamGet', 'cmdGetControlLog' ],
					      ['0.0.99.98.3.255', '7','2','cmdMeterParamGet', 'cmdGetPQLog' ],
					      ['0.0.99.98.4.255', '7','2','cmdMeterParamGet', 'cmdGetFWUpgradeLog' ]
    ];
  
    var lastChannelIndex = 0;
    // Meter Program
    var curMeterProgramSettings = "";
    var curMeterProgramKind = "";
    var getObisCodeUrl = "${ctx}/gadget/system/getObisCode.do";
    cmdList =[
          	[1, 'eventMeterInstall', 'Install meter event'],
          	[2, 'eventMeterUninstall', 'Un-install meter event '], 
          	[3, 'eventMeterStartup', 'Start-up meter event '], 
          	[4, 'eventMeterShutdown', 'Shut-down meter event ']
          	]
    
    var otaType = "";
    var touType = "";
    $(function(){
    	obj = window.opener.obj;
    });
    
    $(document).ready(function(){
        var obj = window.opener.obj;
        supplierId = obj.supplierId;
        meterId = obj.meterId;
        modelId = obj.modelId;
        mdsId = obj.mdsId;
        modelName = obj.modelName;
        loginId = obj.loginId;
        $('#meterSetId').val(meterId);
        $('#mdsIdView').val(mdsId);
        DeviceModelSelectId  = modelId;
        $.ajaxSetup({
            async : false
        });     

        getObisCodeGroup();//처음에 나오는 obisCode List
    });
    
    var obisCodeStore;
    var obisCodeCol;
    var obisCodeGrid;
    var getObisCodeGroup = function(){
		$('#obisCodeGridDiv').html('');
		$('#parameterSetDiv').html('');
		
    	var width = $('#obisCodeGridDiv').width();
    	
    	obisCodeStore = new Ext.data.JsonStore({
    		autoLoad : true,
    		url : '${ctx}/gadget/system/getObisCodeGroup.do',
    		baseParams : {
    			modelId: modelId,
    		},
    		root : 'result',
    		fields : ['OBISCODE', 'CLASSID', 'CLASSNAME']
    	});
    	
    	
    	obisCodeCol = new Ext.grid.ColumnModel({
    		columns : [
    			{header:'Class Name', dataIndex:'CLASSNAME', width: width/2, align:'left'},
    			{header:'Obis Code', dataIndex:'OBISCODE', width: width/4},
    			{header:'Class Id', dataIndex:'CLASSID', width: width/5}
    		],
    		defaults:{align:'center', sortable:true}
    	});
    	
    	
    	obisCodeGrid = new Ext.grid.GridPanel({
    		store		: obisCodeStore,
    		colModel	: obisCodeCol,
    		sm			:new Ext.grid.RowSelectionModel({
    			listeners : {
    				rowselect: function(smd, row, rec){
    					var data = rec.data;
    					obisCodeParam = data.OBISCODE;
    					classIdParam = data.CLASSID;

    					meterSettingHandler.changeCommand();
    				}
    			}
    		}),
    		autoScroll	: true,
    		columnLines: true,
    		stripeRows: true,
    		height		: 400,
    		width		: $('#obisCodeGridDiv').width(),
    		loadMask	: {
    			msg : 'Loading...'
    		},
    		renderTo	: 'obisCodeGridDiv',
            viewConfig: {
                forceFit : true,
                enableRowBody : true,
                showPreview : true,
                emptyText : 'No data to display'
            },
            bbar : new Ext.PagingToolbar({
                pageSize : 20,
                store : obisCodeStore,
                displayInfo : true,
                displayMsg : ' {0} - {1} / {2}'
            })
    	});
    	
    	
    }
    
    var grid;
    var ajaxSuccessCount = 0; // ajaxQueue의 요청이 완료된 count
    var mmiuDataList = new Array();
    var meterSettingHandler = {
    	changeMeterType: function() {
    		var selectMeterFlag =$('input[name="selectMeter"]:checked').val(); 
    		if(selectMeterFlag == 1) {
    			$('#meterGroupDiv').show();
    			$('#meterDiv').hide();
    			$('#meterSetId').val('');
    		} else {
    			$('#meterDiv').show();
    			$('#meterGroupDiv').hide();
    			$('#meterGroup option:first').attr("selected", "selected");
    			document.getElementById("meterGroup_input").value = '-';
    		}
    	},
    	changeCommand: function() {
    		//var obisCodeId = $('#obisCmdList').val().split(",");
    		if(obisCodeParam == null || obisCodeParam < 1 || classIdParam == null || classIdParam.length < 1) {
    			//$('#classNameMS').val('');
    			parameterGrid.store.clearData()
    			saveDetailParamArrList = new Array();
    		} else {
    			$.post("${ctx}/gadget/system/getObisCode.do",{
        			modelId: modelId,
        			obisCode: obisCodeParam,
        			classId : classIdParam
        		},function(json) {
        			var obisInfo = json.result[0];
        			
        			//$('#classNameMS').val(obisInfo.CLASSNAME);

        			meterSettingHandler.drawParameter(json.result);
        			
        			saveDetailParamArrList = new Array();
        		});
    		}
    		
    		
    	},
    	changeAttr: function() {
    		var recordId = $('input[name="attrRadio"]:checked').val();
    		selectAttribute = parameterGrid.store.getById(recordId).data;
    	},
    	drawParameter: function(attributeList) {
    		$.ajaxSetup({
                async : false
            });
    		
    		var width = $('#parameterSetDiv').width();
    		
    		var checkSelModel = new Ext.grid.CheckboxSelectionModel({
                checkOnly:true
             });
    		
    		var parameterStore = new Ext.data.JsonStore({
    			data : attributeList,
                fields: ['ID','OBISCODE','CLASSNAME','CLASSID','ATTRIBUTENAME','ATTRIBUTENO','DATATYPE','ACCESSRIGHT','VALUE']
            });
    		
            var columns = [];
            
            columns.push({header:'',stopSelection:false,width:20, dataIndex:'ID',
            	renderer: function(value,meta,record) {
            		selectAttribute = record;
            		return "<center><input type='radio' name='attrRadio' id='attrRadio_"+record.id+"' value="+ record.id +" onClick='javascript:meterSettingHandler.changeAttr()' ></center>"
            	}
            	})
            
            columns.push({header: "<fmt:message key='aimir.model.attributeNo'/> / Method No", tooltip:"<fmt:message key='aimir.model.attributeNo'/>/Method No", 
    		 			dataIndex: 'ATTRIBUTENO', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 - 20});
            
            columns.push({header: "<fmt:message key='aimir.model.attributeName'/>", tooltip:"<fmt:message key='aimir.model.attributeName'/>", 
                    dataIndex: 'ATTRIBUTENAME', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 -5});
            
            columns.push({header: "<fmt:message key='aimir.model.dataType'/>", tooltip:"<fmt:message key='aimir.model.dataType'/>",
                dataIndex: 'DATATYPE', sortable: true, menuDisabled: true, renderer: addTooltip, width:width/5 -5});

            columns.push({header: "<fmt:message key='aimir.model.access'/> ",tooltip:"<fmt:message key='aimir.model.access'/>",
                dataIndex: 'ACCESSRIGHT', sortable: true, menuDisabled: true, width:width/5 -5});
            
            columns.push({header: "<fmt:message key='aimir.value'/>", tooltip:"<fmt:message key='aimir.value'/>", 
                dataIndex: 'VALUE', sortable: true, menuDisabled: true, renderer: meterSettingHandler.detailBtn, width:width/5,
                editor: meterSettingHandler.editFunction});
            
            var parameterModel=columns;

            $('#parameterSetDiv').empty();
            
            parameterGrid = new Ext.grid.EditorGridPanel({
            	layout: 'fit',
            	width: width,
            	height:400,
            	store: parameterStore,
            	columns: parameterModel,
            	autoScroll: false,
            	stripeRows: true,
            	columnLines: true,
            	loadMask: {
            		msg: 'loading...'
            	},
            	renderTo: 'parameterSetDiv',
            	viewConfig: {
            		forceFir: true,
            		enableRowBody: true,
            		showPreview: true,
            		emptyText: "<fmt:message key='aimir.extjs.empty'/>"
            	},
                bbar : new Ext.PagingToolbar({
                    pageSize : 20,
                    store : parameterStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
    	},
    	editFunction: function(value,meta,rec) {
    		if(rec.data.DATATYPE != "array") {
        		return new Ext.Editor(new Ext.form.TextField({id: 'value'}));
        	} else {
        		return '';
        	}
    	},
    	detailBtn: function(value,meta,rec) {
        		var id = Ext.id();
            	var $div = $("<div></div>").attr("id", id);
            	var button = function() {
            		if($("#" + id).length > 0 && ($div.children().length<1)) {
            			new Ext.Button({
            				text: "<fmt:message key='aimir.view.detail'/>",
            				width: 40,
            				handler: function(b, e) {	
            					saveDetailParamArrList = new Array();
            					dlmsGuideFunction(rec);
			            		var json = rec.json.VALUE;
			            		var data = rec.data.VALUE;
            				}				
            			}).render(id);
            		} else {
            			button.defer(100);
            		}
            	};

            	button.defer(100);
            	return $div[0].outerHTML;
        },
    	cmdObis: function(cmd) {
    		//비동기 설정
            $.ajaxSetup({
                async : false
            });
    		
    		if( $(":input:radio[name='attrRadio']:checked").val() == null) {
    			return Ext.Msg.alert("Warning", "Please select attribute.",
	    			function() { return false;}, this);
    			
    		}
    		
    		if(detailCheck) {
    			var paramArr = new Array();
       			paramArr.push({
    					'ACCESSRIGHT' : selectAttribute.ACCESSRIGHT,
    					'ATTRIBUTENAME' : selectAttribute.ATTRIBUTENAME,
    					'ATTRIBUTENO' : selectAttribute.ATTRIBUTENO,
    					'CLASSID' : selectAttribute.CLASSID,
    					'CLASSNAME' : selectAttribute.CLASSNAME,
    					'DATATYPE' : selectAttribute.DATATYPE,
    					'OBISCODE' : selectAttribute.OBISCODE,
    					'VALUE' : selectAttribute.VALUE
    				});
       			detailCheck = false;
    			selectAttribute.VALUE='';

        		if(($('#meterSetId').val() != undefined && $('#meterSetId').val() != '') || ($('#meterGroup').val() != undefined && $('#meterGroup').val() != '')) {
        			meterSettingHandler.groupGetSetService(cmd,paramArr);
        		} else {
        			return Ext.Msg.alert("Warning", "Please select Meter",
    	    				function() { return false;});
        		}
    		} else {
    			return Ext.Msg.alert("Warning", "Please click [Detail] button for attribute's parameter.",
	    				function() { return false;});
    		}
    		
    	},
    	checkCmdGetLog : function(cmd, obisCode, classId, attrId){
    		var cmdName = "";
    		for ( var i = 0; i < getLogObisList.length; i++){
    			if ( obisCode == getLogObisList[i][0] && 
    					classId == getLogObisList[i][1] &&
    					attrId == getLogObisList[i][2] &&
    					cmd == getLogObisList[i][3] )
    			{
    				cmdName = getLogObisList[i][4];
    				break;
    			}
    		}
			return cmdName;    		
    	},
 
    	cmdGo : function(cmd, paramArr, meterArray) {
    		mmiuDataList = new Array();
    		ajaxSuccessCount = 0;//요청 완료시 counting된다.

            grid.store.getAt(0).set('rtnStr',
                    'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

            
            var obisCode = paramArr[0].OBISCODE;
            var classId =  paramArr[0].CLASSID;
    		var attrId = paramArr[0].ATTRIBUTENO;
    		var tval = paramArr[0].VALUE[0];
    		var fromDate = "";
    		var toDate = "";
    		var paramdata = {};
    		var getLogCmdName = "";
    		var url = '${ctx}/gadget/device/command/dlmsGetSet.do';
    		if ( typeof(tval) != 'undefined') {
    			fromDate = tval.fYear + tval.fMonth+ tval.fDayOfMonth + tval.fHh + tval.fMm + tval.fSs;
    		 	toDate = tval.tYear + tval.tMonth+ tval.tDayOfMonth + tval.tHh + tval.tMm + tval.tSs;
    		}
    		if ( (getLogCmdName =  meterSettingHandler.checkCmdGetLog (cmd, obisCode, classId, attrId )) != ""){
    			url = '${ctx}/gadget/device/command/dlmsGetLog.do';
    			paramdata = {
                    	'cmd' : getLogCmdName,
    					'mdsId' : mdsId,
    					'fromDate' : fromDate,
    					'toDate' : toDate,
    					'loginId' : loginId
                    };
    		}
    		else {
    			paramdata = {
            		   'cmd' : cmd,
				       'parameter' : JSON.stringify(paramArr),
				       'mdsId' : mdsId,
				       'modelName' :  modelName,
   						'loginId' : loginId
    			}
    		}

            $.ajaxSetup({
            	async : true
            });

            for (var i = 0; i < meterArray.length; i++) {
                //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
               var queueName = $.ajaxQueue({
                    type : "GET",
                    url : url,
                    data : paramdata,
                    success : function(returnData) {
                    	saveDetailParamArrList = new Array();
                        var i = ajaxSuccessCount;
                        grid.getView().focusRow(i);
                        var record = grid.store.getAt(i);
                        if (returnData.rtnStrList[0].rtnStr == 'Next Step Processing...') {
                        	grid.store.getAt(ajaxSuccessCount).set('rtnStr',
                            'Next Step Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                        	var mmiuData = {
                        			meterId : returnData.rtnStrList[0].meterId,
                        			trId : returnData.rtnStrList[0].trId,
                        			recordId : i
                        	}
                        	mmiuDataList.push(mmiuData); 
                        }else {
                        	record.set('rtnStr', returnData.rtnStrList[0].rtnStr);
                        	if(returnData.rtnStrList[0].rtnStr.indexOf("FAIL") < 0) {
                        		record.set('detail',
                                        "<a href='#' onclick='meterSettingHandler.successResult(" + JSON.stringify(returnData.rtnStrList[0].viewMsg) + ");' class='btn_blue'><span>Detail</span></a>");							
                        	}
                            
                        }
                        ajaxSuccessCount++;
                        if (meterArray.length != ajaxSuccessCount)
                            grid.store.getAt(ajaxSuccessCount).set('status','Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

                        if (window.ajaxQueueCount[queueName] == 1) {// 맨 마지막 동작일때
                        	if(mmiuDataList.length > 0) {
                        		$.post('${ctx}/gadget/device/command/getAsyncLog.do',{
                            		meterInfoArr : JSON.stringify(mmiuDataList)
    		                    }, function(json) {
    		                    	record = grid.store.getAt(json.rtnStrList[0].recordId);
    		                    	record.set('rtnStr', json.rtnStrList[0].rtnStr);
    		                    	if(json.rtnStrList[0].rtnStr.indexOf("FAIL") < 0) {
    		                    		record.set('detail',
    		                                    "<a href='#' onclick='meterSettingHandler.successResult(" + JSON.stringify(json.rtnStrList[0].viewMsg) + ");' class='btn_blue'><span>Detail</span></a>");
    		                    	}
    	                            
    		                    });
                        	}
                        	
                            $.ajaxSetup({
                                async : true
                            });
                        }
                    }
                });
            }
    	},
    	groupGetSetService: function(cmd,paramArr) {
    		$.ajaxSetup({
                cache : false
            });
            //해당 그룹 미터정보 얻기
            var meterArray = new Array();
            
//            var selectMeterFlag =$('input[name="selectMeter"]:checked').val(); 
//    		if(selectMeterFlag == 1 && ($('#meterGroup').val() != undefined && $('#meterGroup').val() != '')) {
//    			meterArray = meterSettingHandler.groupService();
//    		} else if(selectMeterFlag == 0 && $('#meterSetId').val() != undefined && $('#meterSetId').val() != '' ){
    			var tempArr = new Array();
    			tempArr[0] = mdsId;
    			meterArray.push(tempArr); 
//    		}
    		
            //그리드, 윈도우 높이 구하기
            this.gridH = 100 + Number(meterArray.length * 25);
            this.winH = 200 + Number(meterArray.length * 25);
            if (this.gridH > 600)
                this.gridH = 600;
            if (this.winH > 700)
                this.winH = 700;

            var array = new Array();
            //그리드 데이터 생성
            if (meterArray != null) {
                count = meterArray.length;
                for (var i = 0; i < count; i++) {
                    var gridData = meterArray[i];
                    var arrayData = {'meterId' : gridData[0],
                    				'rtnStr' : 'Processing...'}
                    	
                    array[i] = arrayData;
                }

                meterSettingHandler.makeAlertWindow(array,cmd,paramArr);
            } else {
                if (imgWin != undefined) {
                    Ext.getCmp('drAlertWinId').hide();
                }
            }

            $.ajaxSetup({
                cache : true
            });
    	},
    	groupService: function() {
    		var meterIdList = new Array();

            var params = {
            		meterGroupId : $('#meterGroup').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/system/getGroupMemberList.do",
                data: params,
                async: false
            }).responseText;

            eval("result=" + jsonText);

            var meterList = result.meterList;
			var meterIdList = new Array();
            if (meterList.length > 0) {
                for (var i = 0 ; i < meterList.length; i++) {
                    meterIdList[i] = [meterList[i]];
                }
            }

            return meterIdList;
    	},
    	makeAlertWindow: function(rtnStrList,cmd,paramArr) {
           var store = new Ext.data.JsonStore({
                    fields : [ {name:'meterId'},{name:'rtnStr'}]
                });
           store.loadData(rtnStrList);
           var colModel = new Ext.grid.ColumnModel({
                defaults : {
                    width : 100,
                    height : 100,
                    sortable : true
                },
                columns : [{
                    id : "meterId",
                    width : 150,
                    header : "Meter ID",
                    dataIndex : "meterId",
                    renderer: addTooltip
                }, {
                    header : "Status",
                    width : 200,
                    dataIndex : "rtnStr",
                    renderer: addTooltip
                }, {
                    header : "Result",
                    width : 60,
                    dataIndex : "detail"
                }]
            });
            grid = new Ext.grid.GridPanel({
                   height : 300,
                   store : store,
                   colModel : colModel,
                   width : 420
               });items : grid,

            $('#resultDiv').empty();
            
            var imgWin = new Ext.Window({
                title : 'Result',
                id : 'resultDivWinId',
                applyTo : 'resultDiv',
                autoScroll : true,
                autoHeight : true,
                pageX : 100,
                pageY : 100,
                width : 430,
                height : 300,
                items : grid,
                closeAction : 'hide',
                onHide : function() {
                }
            });
            Ext.getCmp('resultDivWinId').show();
            setTimeout(function() {meterSettingHandler.cmdGo(cmd,paramArr,rtnStrList);}, 100);
    	},
    	successResult: function(viewMsg) {
    		//성공 윈도우
    		var store = new Ext.data.JsonStore({
                fields: ['paramType','paramValue']
            });
    		store.loadData(viewMsg);
    		
            var colModel = new Ext.grid.ColumnModel({
                defaults : {
                    sortable : true
                },
                columns : [{
                    id : "name",
                    width : 150,
                    header : "name",
                    dataIndex : "paramType",
                    renderer: addTooltip
                }, {
                	id : "value",
                    header : "value",
                    width : 330,
                    dataIndex : "paramValue",
                    renderer: addTooltip
                }]
            });
    		
            var resultGrid = new Ext.grid.GridPanel({
                height : 300,
                store : store,
                colModel : colModel,
                width : 500
            });

            $('#resultDetailDiv').empty();

            var imgWin2 = new Ext.Window({
                title : 'Success Result',
                id : 'resultDetailDivWinId',
                applyTo : 'resultDetailDiv',
                autoScroll : true,
                pageX : 80,
                pageY : 120,
                width : 530,
                height : 300,
                items : resultGrid,
                closeAction : 'hide',
                onHide : function() {
                }
            });
            Ext.getCmp('resultDetailDivWinId').show();
    	}
    }
    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }    
    
	$(window).resize(function() {
		getObisCodeGroup();
	});
	
    /*
        function buttonEvtHandler(){
        	var cmd = $('#command').val().trim();
        	if(cmd == 'cmdOTAStart' || cmd.indexOf('cmdMeterOTAStart') == 0) {
           		content += "\n> "+ cmd + "\nWait..." + "\n";
           		addContent(content);
           		cmdOTAStart(cmd);
           	} else if(cmd == 'cmdTOUSet'){
           		content += "\n> "+ cmd + "\nTOU Setting" + "\n";
           		addContent(content);
           		cmdTOUSet(cmd);
           	} else if (cmd.indexOf('cmd') == 0) {
           		content += "\n> "+ cmd + "\nWait..." + "\n";
           		addContent(content);
           		cmdStart(cmd);
           	} else if(cmd == 'clear') {
            	content = '';
            	addContent(content);
            } else if(cmd == 'exit') {
                winClose();
            } else if(cmd == 'eventMeterInstall') {
            	content += "\n> "+ cmd + "\n Success." + "\n";
            	addContent(content);
            } else if(cmd == 'eventMeterUninstall') {
            	content += "\n> "+ cmd + "\n Fail." + "\n";
            	addContent(content);
            } else if(cmd == 'eventMeterStartup') {
            	content += "\n> "+ cmd + "\n Security Error." + "\n";
            	addContent(content);
            } else if(cmd == 'eventMeterShutdown') {
            	content += "\n> "+ cmd + "\n Comm Error." + "\n";
            	addContent(content);
            } else {
            	content += "\n> "+ cmd + "\n InValid Command. Please refer to the table above." + "\n";
            	addContent(content);
            }
        }
   
        function addContent(content) {
        	$('#content').val(content);
        	$('#content').focus();
        	$('#command').val('');
        	$('#command').focus();
        }
        
        function clickEvt(event) {
        	var firstClick = $('#command').val();
        	if(firstClick == 'Please enter commands here.') {
        		$('#command').val('');
        		$('#command').html("<li><input type='text' id='command' style='width: 580px; margin-left: 5px;' onkeydown='javascript:keyEvtHandler(event);'/></li>");
        	}
        }
        */
        function winClose() {
            window.close();
        }
        
    /*]]>*/
    </script>
</head>
<body>

<div id="cmdLine"></div>
<div class="cmdLineDiv" style="margin: 10px;">

       <div  style="height:490px; border:1px solid #b4d3f0;">
                <div id='meterSetting'>
               	<div id="resultDiv"></div>
               	<div id='resultDetailDiv'></div>
               	<div id='detailViewDiv'></div>
               	<div id="fragment-left" style="width: 100% !important;">
<%--                	<ul style="padding-left: 10px">
               		<li>
                      	<label class="withinput"><b><fmt:message key="aimir.meterid"/></b></label>
                      	<span><input type="text" id="mdsIdView" class="border-trans" readonly="readonly"/></span>
                      	<input type="hidden" id="meterSetId"/>
                  </li>

         	  	   <li>
              			<label><fmt:message key='aimir.instrumentation'/></label>
               			<span><select style="width: 300px" id="obisCmdList" name="obisCmdList" onchange="javascript:meterSettingHandler.changeCommand();"></select></span>
               	   </li>

               		<li>
               			<label class="withinput"><fmt:message key='aimir.model.className'/></label>
               			<span><input type='text' id='classNameMS' name='classNameMS' readOnly style="border: 0; width: 300px"/></span>
               		</li>

               		 
               		<li>
               			<label><fmt:message key='aimir.parameter.set'/></label>
               		</li>
               			<li style="padding-left: 10px">
               			</li>
               		</ul> --%>
               		<div style="padding: 10px">
               			<label style="width: 10% !important"><b><fmt:message key="aimir.meterid"/></b></label>
                      	<span><input type="text" id="mdsIdView" class="border-trans" readonly="readonly"/></span>
                      	<input type="hidden" id="meterSetId"/>
               		</div>
               		
               		<div style="padding: 10px; padding-top: 20px" >
		            	<div id="obisCodeGridDiv" style="width: 46%; display: inline-block;"></div>
		            	<div id="parameterSetDiv" style="width: 50%; padding-left: 3%; display: inline-block; "></div>
               		</div>

	               	<div style="float: right; padding: 10px">
	                	<em class="am_button">
	                	<a id="cmdGetObis" onClick="javascript:meterSettingHandler.cmdObis('cmdMeterParamGet');" class="on"><fmt:message key='aimir.get'/></a>
	                	</em>
	                	<em class="am_button">
	                	<a id="cmdSetObis" onClick="javascript:meterSettingHandler.cmdObis('cmdMeterParamSet');" class="on"><fmt:message key='aimir.set'/></a>
	                	</em>
	                	<em class="am_button">
	                	<a id="cmdActionObis" onClick="javascript:meterSettingHandler.cmdObis('cmdMeterParamAct');" class="on">Action</a>
	                	</em>
               		</div>
               	</div>
               	</br></br>
               	<div id="addObjCmp" name='addObjCmp'></div>
               </div>
               </div>
</div>
</body>
</html>