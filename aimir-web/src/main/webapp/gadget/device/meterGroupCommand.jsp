<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript">
//  Script for Group Command  
	// Group Ondemand Start
	var grpCmdMeterStoreArray = new Array;
	var meterArray = new Array;
	var grpCmdWin;
	var grpCmdWin2;
	var grpCmdWin3;
	var grpCmdGridOn = false;
	var grpCmdGrid;
	var grpCmdMeterStore;
	var grpCmdDetail = new Array;

	var grpCmdCommand = "";
	
	var ajaxSuccessCount = 0;
	var ajaxFailCount = 0;
	var queueName = undefined;

	var grpOnDemandType = "";
	var grpOnDemandFromDate;
	var grpOnDemandToDate; 
	
	// get checked meters
	function getChekedMeterList() {
	    var meterIdList = new Array();
	    var k = 0;
		for ( var i = 0; i < chkMeterId.length; i++ ){
				if ( chkMeterId[i].checked == true ){
					meterIdList[k++] = chkMeterId[i].value.split(',');
				}
		}
		return meterIdList;
	}
	
	function getData(index) {
	    var record = grpCmdMeterStore.getAt(index);
	    var temp = record.get('status');
	    var noDataHtml = '<html><div  style=\"width: 180px;FONT-SIZE: 36px;  TEXT-ALIGN:center; padding: 50px 0px 50px 0px;\"> No Data!</div></html>';
	
	    if (temp == 'Success') {
	        //Success Window
	        if (!grpCmdWin2) {
	            grpCmdWin2 = new Ext.Window({
	                title : 'Group onDemand DATA',
	                id : 'drAlertWinIdDataPop',
	                applyTo : 'drAlertDataPop',
	                autoScroll : true,
	                pageX : 100,
	                pageY : 50,
	                width : 800,
	                height : 700,
	                closeAction : 'hide',
	                html : grpCmdDetail[index]
	            });
	        } else {
	            grpCmdWin2.update(grpCmdDetail[index]);
	        }
	        Ext.getCmp('drAlertWinIdDataPop').show();
	    } else {
	        // Fail Window
	        if (!grpCmdWin3) {
	            grpCmdWin3 = new Ext.Window({
	                title : 'Group onDemand DATA',
	                id : 'drAlertDataPopFailure',
	                applyTo : 'drAlertDataPopFailure',
	                autoScroll : true,
	                pageX : 100,
	                pageY : 50,
	                width : 200,
	                height : 200,
	                closeAction : 'hide',
	                html : noDataHtml
	            });
	        } else {
	            grpCmdWin3.update(noDataHtml);
	        }
	        Ext.getCmp('drAlertDataPopFailure').show();
	    }
	}
	
	//////////  Group OnDemand  //////////
	var grpOnDemandFormPanel;
	var grpOnDemandWin;	
	function InputGrpOnDemand(title) {
	    if(Ext.getCmp('grpOnDemand')){
	        Ext.getCmp('grpOnDemand').close();
	    }
	//	var starthtml = '<input id="grpOnDemandFromDate" type="text" class="day"/>';
		grpOnDemandFormPanel = new Ext.FormPanel({
	        id : 'grpOnDemandForm',
	        defaultType : 'fieldset',
	        bodyStyle:'padding:1px 1px 1px 1px',
	        frame : true,
	        labelWidth : 100,
	        items : [
	            {
	                xtype : 'radiogroup',
	                id : 'targetTypeRadio',
	                fieldLabel : 'Type ',
	                items : [
	                    {boxLabel: 'MCU', name: 'radio-type', inputValue:'MCU', checked: true},
	                    {boxLabel: 'MODEM', name: 'radio-type', inputValue:'MODEM'},
	                    {boxLabel: 'METER', name: 'radio-type', inputValue:'METER'}
	                ],
	                listeners :{
	                    change: function(thisRadioGroup, checkedItem){
	
	                    }
	                },
	            }, //xtype : radio
	            /*  
				{
				    xtype: 'component',
				    autoEl: {
				      html: starthtml
				    }
	            },
	           {
	                html: starthtml,
	                xtype: "panel"
	                },  
	                */
                {
                    xtype: 'datefield',
                    anchor: '100%',
                    fieldLabel: 'From',
                    id : 'grpOnDemandFromDate',
                    name: 'to_date',
                    format: 'd.m.y',
                    pickerOffset : '[10,10]',
                    value: new Date()  // defaults to today
                },
	            {
	                xtype: 'datefield',
	                id : 'grpOnDemandToDate',
	                anchor: '100%',
	                fieldLabel: 'To',
	                name: 'to_date',
	                format: 'd.m.y',
	                value: new Date()  // defaults to today
	            }
	        ], // items
	        buttons : [
	            {
	                id: 'btnInputGrpOnDemand',
	                text: 'OK',
	                listeners: {
	                    click: function(btn,e){
	                        //submit action
	                        grpCmdExecWin(title, 'onDemandTask()' );
	                        grpOnDemandType = Ext.getCmp('targetTypeRadio').getValue().inputValue;
	                        grpOnDemandFromDate = Ext.getCmp('grpOnDemandFromDate').getValue();
	                        grpOnDemandToDate = Ext.getCmp('grpOnDemandToDate').getValue();
	                        grpOnDemandWin.close();
	                    }
	                }
	            }, {
	                text: 'Cancel',
	                listeners: {
	                    click: function (btn, e) {
	                        Ext.getCmp('grpOnDemand').close();
	                    }
	                }
	            }
	        ] //buttons
	    });
	
	    var grpOnDemandWin = new Ext.Window({
	        id     : 'grpOnDemand',
	        title : title,
	        pageX : 500,
	        pageY : 100,
	        height : 200,
	        width  : 380,
	        layout : 'fit',
	        bodyStyle   : 'padding: 10px 10px 10px 10px;',
	        items  : [grpOnDemandFormPanel],
	    });
	
	    grpOnDemandWin.show();
	}
	
	function onDemandTask() {
	    ajaxSuccessCount = 0;
	    ajaxFailCount = 0;
	    var type =  grpOnDemandType;
	    var from = grpOnDemandFromDate;
	    var to  = grpOnDemandToDate;
	    var ftime = from.getFullYear().toString() + (from.getMonth() + 1).toString()
	            + from.getDate().toString() + '000000';
		var ttime = to.getFullYear().toString() + (to.getMonth() + 1).toString()
        + to.getDate().toString() + '000000';
	    //Requests asynchronously.
	    $.ajaxSetup({
	        async : true
	    });
	
	    //Add image loading in the first item
	    grpCmdMeterStore.getAt(0).set('status',
	            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
	
	    for (var i = 0; i < meterArray.length; i++) {
	
	        //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
	        queueName = $.ajaxQueue({
	            type : "GET",
	            url : '${ctx}/gadget/device/command/cmdOnDemand.do',
	            data : {
	                'target' : meterArray[i][0],
	                'loginId' : loginId,
	                'fromDate' : ftime,
	                'toDate' : ttime,
	                'type' : type
	            },
				error : function(returnData){
		               var i = ajaxSuccessCount + ajaxFailCount;
		                grpCmdGrid.getView().focusRow(i);
		                var record = grpCmdMeterStore.getAt(i);                 
	
		               	record.set('status', 'Fail' );              
		               	ajaxFailCount++;
		                if (meterArray.length != ajaxSuccessCount + ajaxFailCount)
		                    grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
		                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
		                if (window.ajaxQueueCount[queueName] == 1) {
		                    $.ajaxSetup({
		                        async : true
		                    });
		                }
				},
	            success : function(returnData) {
	                var i = ajaxSuccessCount + ajaxFailCount;
	                grpCmdDetail[i] = returnData.detail;
	                grpCmdGrid.getView().focusRow(i);
	                var record = grpCmdMeterStore.getAt(i);
	                if (returnData.rtnStr == 'java.lang.NullPointerException') {
	                    record.set('status', 'Not Found Meter!');
	                } else if (returnData.rtnStr == 'Success') {
	                    record.set('status', returnData.rtnStr);
	                    record.set('view',
	                            "<a href='#' onclick='getData(" + i + ");' class='btn_blue'><span><fmt:message key='aimir.report.mgmt.view'/></span></a>");
	                } else if (returnData.rtnStr == '') {
	                    record.set('status', 'Failure');
	                } else {
	                    record.set('status', returnData.rtnStr);
	                }
	                ajaxSuccessCount++;
	                if (meterArray.length != ajaxSuccessCount  + ajaxFailCount)
	                    grpCmdMeterStore.getAt(ajaxSuccessCount+ + ajaxFailCount).set('status',
	                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
	
	                if (window.ajaxQueueCount[queueName] == 1) {// 맨 마지막 동작일때
	                    $.ajaxSetup({
	                        async : true
	                    });
	                }
	            }
	        });
	    }
	}
	
	function groupOndemandService() {
		grpCmdCommand = "OnDemand";
		groupCommandShowCaptcha("<fmt:message key='aimir.confirmGroupOnDemand'/>",
				"<fmt:message key='aimir.groupondemand'/>",
				"onDemandTask();")
	}
	
	// get target list ( if all meters checked )
	function groupOnDemand(){
	    var meterIdList = new Array();
	    var condition = getCondition();
	
	    var params = {
	        sMeterType          : condition[0],
	        sMdsId              : condition[1],
	        sStatus             : condition[2],
	        sMcuName            : condition[3],
	        sLocationId         : condition[4],
	        sConsumLocationId   : condition[5],
	        sVendor             : condition[6],
	        sModel              : condition[7],
	        sInstallStartDate   : condition[8],
	        sInstallEndDate     : condition[9],
	        sModemYN            : condition[10],
	        sCustomerYN         : condition[11],
	        sLastcommStartDate  : condition[12],
	        sLastcommEndDate    : condition[13],
	        curPage             : 0,
	        sOrder              : condition[14],
	        sCommState          : condition[15],
	        supplierId          : condition[16],
	        sMeterGroup         : condition[17],
	        sGroupOndemandYN    : "Y",
	        sPermitLocationId   : condition[20],
	        sMeterAddress       : condition[21],
	        sHwVersion         : "",
	        sFwVersion         : ""
	    };
	
	    var jsonText = $.ajax({
	        type: "POST",
	        url: "${ctx}/gadget/device/getMeterSearchGrid.do",
	        data: params,
	        async: false
	    }).responseText;
	
	    eval("result=" + jsonText);
	
	    allGridData = result.allGridData;
	
	    if (allGridData.length > 0) {
	        for (var i = 0 ; i < allGridData.length; i++) {
	            meterIdList[i] = [allGridData[i]["meterId"],allGridData[i]["meterMds"],allGridData[i]["mcuSysID"]];
	        }
	    }	
	    return meterIdList;
	}
	
	////////// Limit Power Usage //////////
	var grplmtPwrUsageFormPanel;
	var grplmtPwrUsageWin;	
	function InputGrplmtPwrUsage(title) {
	    if(Ext.getCmp('grplmtPwrUsage')){
	        Ext.getCmp('grplmtPwrUsage').close();
	    }
	//	var starthtml = '<input id="grpOnDemandFromDate" type="text" class="day"/>';
		grplmtPwrUsageFormPanel = new Ext.FormPanel({
	        id : 'grplmtPwrUsageForm',
	        defaultType : 'fieldset',
	        bodyStyle:'padding:1px 1px 1px 1px',
	        frame : true,
	        labelWidth : 70,
	        items : [
	            {
					xtype : 'textfield',
					id : 'grpLimitPowerValue',
					fieldLabel : 'Limit Power Usage'
	            }
	        ], // items
	        buttons : [
	            {
	                id: 'btnInputGrplmtPwrUsage',
	                text: 'It has not implemented!!!',
	                listeners: {
	                    click: function(btn,e){
	                        //submit action
	                        grplmtPwrUsageValue = Ext.getCmp('grpLimitPowerValue').getValue();
	                        grplmtPwrUsageWin.close();
	                        grpCmdExecWin(title, 'limitPowerUsageTask()' );
	                    }
	                }
	            }, {
	                text: 'Cancel',
	                listeners: {
	                    click: function (btn, e) {
	                        Ext.getCmp('grplmtPwrUsage').close();
	                    }
	                }
	            }
	        ] //buttons
	    });
	
	    var grplmtPwrUsageWin = new Ext.Window({
	        id     : 'grplmtPwrUsage',
	        title : title,
	        pageX : 500,
	        pageY : 100,
	        height : 150,
	        width  : 280,
	        layout : 'fit',
	        bodyStyle   : 'padding: 10px 10px 10px 10px;',
	        items  : [grplmtPwrUsageFormPanel],
	    });
	
	    grplmtPwrUsageWin.show();
	}
	
	function groupLimitPowerUsageService() {
		grpCmdCommand = "LimitPowerUsage";
		groupCommandShowCaptcha(
				"<fmt:message key='aimir.confirmGroupLimitPowerUsage'/>",
				"<fmt:message key='aimir.grouplimitpowerusage'/>", "limitPowerUsageTask();");

	}

	function groupOTAService() {
	}

	////////// Group Relay On/Off //////////
	function relayOnOffTask(onoff) {
	    ajaxSuccessCount = 0;
	    ajaxFailCount  = 0;
	    //async call
	    $.ajaxSetup({
	        async : true
	    });
	
	    for (var i = 0; i < grpCmdMeterStore.totalLength ; i++) {
	    	grpCmdMeterStore.getAt(i).set('status','Processing... ' );
	    }
	    //add loading image at first
	    grpCmdMeterStore.getAt(0).set('status',
	            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
		var url;
		if ( onoff == 'on')
			url= '${ctx}/gadget/device/command/cmdRemotePowerOn.do'
		else if ( onoff == 'off' )
			url= '${ctx}/gadget/device/command/cmdRemotePowerOff.do'
		
	    for (var i = 0; i < meterArray.length; i++) {
	        // add request to queue for  sequential processing
	        queueName = $.ajaxQueue({
	            type : "GET",
	            url : url,
	            data : {
	                    'target' : meterArray[i][0],
	                    'mcuId'  : meterArray[i][1],
	                    'loginId' : loginId
	                },
				error : function(returnData){
		               var i = ajaxSuccessCount + ajaxFailCount;
		                grpCmdGrid.getView().focusRow(i);
		                var record = grpCmdMeterStore.getAt(i);                 
	
		               	record.set('status', 'Fail' );              
		               	ajaxFailCount++;
		                if (meterArray.length != ajaxSuccessCount + ajaxFailCount)
		                    grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
		                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
		                if (window.ajaxQueueCount[queueName] == 1) {
		                    $.ajaxSetup({
		                        async : true
		                    });
		                }
				},
	            success : function(returnData) {
	                var i = ajaxSuccessCount + ajaxFailCount;
	                grpCmdGrid.getView().focusRow(i);
	                var record = grpCmdMeterStore.getAt(i);                 
	
	               	record.set('status', returnData.rtnStr );
	                if (returnData.relayStatus != undefined && returnData.relayStatus != ''){
	                    record.set('view' , returnData.relayStatus);
	                }                   
	
	                ajaxSuccessCount++;
	                if (meterArray.length != ajaxSuccessCount + ajaxFailCount)
	                    grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
	                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
	                if (window.ajaxQueueCount[queueName] == 1) {
	                    $.ajaxSetup({
	                        async : true
	                    });
	                }
	            }
	        });
	    }
	}
	
	////// Group Repay Off
	function relayOnTask() {
		relayOnOffTask("on");
	}
	function groupRelayOnService() {
		grpCmdCommand = "RelayOn";
		groupCommandShowCaptcha(
				"<fmt:message key='aimir.confirmGroupRelayOn'/>",
				"<fmt:message key='aimir.grouprelayon'/>", "relayOnTask();");
	}

	////////// Group Repay Off //////////
	function relayOffTask() {
		relayOnOffTask("off");
	}
	
	function groupRelayOffService() {
		grpCmdCommand = "RelayOff";
		groupCommandShowCaptcha(
				"<fmt:message key='aimir.confirmGroupRelayOff'/>",
				"<fmt:message key='aimir.grouprelayoff'/>", "relayOffTask();")
	}

	////////// Group Metering Schedule //////////
	function meteringScheduleTask() {
		ajaxSuccessCount = 0;
	    ajaxFailCount  = 0;
		//async call
		$.ajaxSetup({
			async : true
		});

		for (var i = 0; i < grpCmdMeterStore.totalLength; i++) {
			grpCmdMeterStore.getAt(i).set('status', 'Processing... ');
		}
		//add loading image at first
		grpCmdMeterStore
				.getAt(0)
				.set(
						'status',
						'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
		var url;
		url = '${ctx}/gadget/device/command/cmdRemotePowerOn.do'

		for (var i = 0; i < meterArray.length; i++) {
			// add request to queue for  sequential processing
			queueName = $
					.ajaxQueue({
						type : "GET",
						url : url,
						data : {
							'target' : meterArray[i][0],
							'mcuId' : meterArray[i][1],
							'loginId' : loginId
						},
						error : function(returnData){
				               var i = ajaxSuccessCount + ajaxFailCount;
				                grpCmdGrid.getView().focusRow(i);
				                var record = grpCmdMeterStore.getAt(i);                 
			
				               	record.set('status', 'Fail' );              
				               	ajaxFailCount++;
				                if (meterArray.length != ajaxSuccessCount + ajaxFailCount)
				                    grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
				                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
				                if (window.ajaxQueueCount[queueName] == 1) {
				                    $.ajaxSetup({
				                        async : true
				                    });
				                }
						},
						success : function(returnData) {
							var i = ajaxSuccessCount + ajaxFailCount;
							grpCmdGrid.getView().focusRow(i);
							var record = grpCmdMeterStore.getAt(i);

							record.set('status', returnData.rtnStr);
							if (returnData.relayStatus != undefined
									&& returnData.relayStatus != '') {
								record.set('view', returnData.relayStatus);
							}

			                ajaxSuccessCount++;
			                if (meterArray.length != ajaxSuccessCount + ajaxFailCount)
			                    grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
			                            'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
			                if (window.ajaxQueueCount[queueName] == 1) {
			                    $.ajaxSetup({
			                        async : true
			                    });
			                }
						}
					});
		}
	}
	function groupMeteringScheduleService() {
		grpCmdCommand = "MeteringSchedule";
		groupCommandShowCaptcha(
				"<fmt:message key='aimir.confirmGroupMeteringSchedule'/>",
				"<fmt:message key='aimir.groupmeteringschedule'/>",
				"meteringScheduleTask();");
	}

	//// Check Meter Numbers
	function groupCommandPreCheck()

	{
		if ($("#allCheck").is(':checked')) {
			meterArray = groupOnDemand();
		} else {
			meterArray = getChekedMeterList();
		}
		if (maxMeters > 0 && meterArray.length > maxMeters) {
			Ext.Msg.alert("<fmt:message key='aimir.message'/>",
					"<fmt:message key='aimir.msg.goupcmd.overmaxmeter'/>" + " "
							+ maxMeters);
			return false;
		}
		if (meterArray.length == 0) {
			Ext.Msg.alert("<fmt:message key='aimir.message'/>",
					"<fmt:message key='aimir.msg.goupcmd.selectmeter'/>");
			return false;
		}
		return true;
	}
	
	//// Group Command Show Captcha Window
	function groupCommandShowCaptcha(captitle, cmdtitle, cmdtask) {
		$.ajaxSetup({
			cache : false
		});
		if (false == groupCommandPreCheck())
			return;

		if (meterArray != null) {
			var count = meterArray.length;
			for (var i = 0; i < count; i++) {
				var gridData = meterArray[i][1];
				var arrayData = [ gridData ];
				grpCmdMeterStoreArray[i] = arrayData;
			}
			CaptchaPanel2(captitle, cmdtitle, cmdtask);
		}
//		else {
//			if (imgWin != undefined) {
//				Ext.getCmp('grpCmdExecWinId').hide();
//			}
//		}

		$.ajaxSetup({
			cache : true
		});
	}
	//// Group Command Exec Command Window
	function grpCmdExecWin(title, funcName) {
		var gridH = 100 + Number(meterArray.length * 25);
		var winH = 200 + Number(meterArray.length * 25);
		if (gridH > 600)
			gridH = 600;
		if (winH > 700)
			winH = 700;
		
		
		if (grpCmdMeterStore == undefined) {
			grpCmdMeterStore = new Ext.data.ArrayStore({
				fields : [ {
					name : 'meterMds'
				}, {
					name : 'status'
				} ]
			});
		}
		grpCmdMeterStore.loadData(grpCmdMeterStoreArray);

		var colModel = new Ext.grid.ColumnModel({
			defaults : {
				width : 100,
				height : 100,
				sortable : true
			},
			columns : [ {
				id : "meterMds",
				width : 150,
				header : "Meter ID",
				dataIndex : "meterMds"
			}, {
				header : "Status",
				width : 150,
				dataIndex : "status"
			}, {
				header : "Result",
				width : 70,
				dataIndex : "view"
			} ]
		});

		if (grpCmdGridOn == false) {
			grpCmdGrid = new Ext.grid.GridPanel({
				height : gridH,
				store : grpCmdMeterStore,
				colModel : colModel,
				width : 374
			});

			grpCmdGridOn = true;
		} else {
			grpCmdGrid.reconfigure(grpCmdMeterStore, colModel);
		}

		if (!grpCmdWin) {
			grpCmdWin = new Ext.Window({
				title : title,
				id : 'grpCmdExecWinId',
				//            applyTo : 'drAlert',
				autoScroll : true,
				autoHeight : true,
				pageX : 400,
				pageY : 130,
				width : 389,
				height : winH,
				items : grpCmdGrid,
				buttons : [ {
					text : "<fmt:message key='aimir.execute'/>",
					handler : function() {
						grpCmdMeterStoreArray = new Array;
						setTimeout(funcName, 100);
					}
				}, {
					text : "<fmt:message key='aimir.cancel'/>",
					handler : function() {
						grpCmdWin.hide();
						if (queueName == undefined)
							return;
						$.ajaxQueueStop(queueName);
					}
				} ],
				closeAction : 'hide',
				onHide : function() {
					if (queueName == undefined)
						return;
					$.ajaxQueueStop(queueName);
				}
			});
		} else {
			grpCmdWin.setTitle(title)
			grpCmdWin.setHeight(winH);
			grpCmdGrid.setHeight(gridH);
		}
		Ext.getCmp('grpCmdExecWinId').show();
	}

	// Captcha Window 
	function CaptchaPanel2(captitle, wintitle, winfunc) {
		if (Ext.getCmp('captchaWindowPanel2')) {
			Ext.getCmp('captchaWindowPanel2').close();
		}
		var captchaFormPanel2 = new Ext.form.FormPanel(
				{
					id : 'formpanel',
					defaultType : 'fieldset',
					bodyStyle : 'padding:1px 1px 1px 1px',
					frame : true,
					items : [
							{
								xtype : 'panel',
								html : '<center><img src="${ctx}/CaptChaImg.jsp?rand='
										+ Math.random() + '"/></center></br>',
								align : 'left'
							},
							{
								xtype : 'textfield',
								id : 'captchaCode',
								fieldLabel : '<fmt:message key="aimir.captchaCode" />',
								emptyText : '<fmt:message key="aimir.enterTheCode" />',
								disabled : false,
							},
							{
								xtype : 'label',
								id : 'infolabel',
								style : {
									background : '#ffff00'
								},
								text : '*<fmt:message key="aimir.incorrectCode" />',
								hidden : true
							} ],
					buttons : [
							{
								text : '<fmt:message key="aimir.refresh" />',
								listeners : {
									click : function(btn, e) {
										//captchaFormPanel2.reload();
										CaptchaPanel2(captitle, wintitle,
												winfunc);
									}
								}
							},
							{
								text : '<fmt:message key="aimir.submit" />',
								listeners : {
									click : function(btn, e) {
										if (5 == captchacount) {
											window.open('${ctx}/admin/logout.do',"_parent").parent.close();
										}
										$.ajax({url : '${ctx}/gadget/report/CaptchaSubmit.do',
												type : 'POST',
												dataType : 'json',
												data : 'answer='+ $('#captchaCode').val(),
												async : false,
												success : function(data) {
													if (data.capcahResult == "true") {
														captchacount = 1;
														if (grpCmdCommand == "RelayOn" || grpCmdCommand == "RelayOff") {
															grpCmdExecWin(wintitle,	winfunc);
														} else if (grpCmdCommand == "OnDemand") {
															InputGrpOnDemand(wintitle);
														}
														//else if (  grpCmdCommand == "LimitPowerUsage" ){
														//	InputGrplmtPwrUsage(wintitle);
														//}
														//else if ( grpCmdCommand == "MeteringSchedule"){
														//	
														//}
														else {
															Ext.Msg.alert("", "Not implemented !!");
														}
														captchaWindow2.close();
													} else {
														captchacount++;
														incorrectCodeCheck = true;
														CaptchaPanel2(captitle,	wintitle, winfunc);
													}
												}
										});
									}
								}
							},
							{
								text : '<fmt:message key="aimir.cancel" />',
								listeners : {
									click : function(btn, e) {
										Ext.getCmp('captchaWindowPanel2').close();
									}
								}
							} ]
				});

		var captchaWindow2 = new Ext.Window({
			id : 'captchaWindowPanel2',
			title : captitle,
			pageX : 500,
			pageY : 100,
			height : 206,
			width : 300,
			layout : 'fit',
			bodyStyle : 'padding: 10px 10px 10px 10px;',
			items : [ captchaFormPanel2 ],
			resizable : false
		});

		captchaWindow2.show();
		if (incorrectCodeCheck == true) {
			Ext.getCmp('infolabel').setVisible(true);
			Ext.getCmp('captchaCode').focus(true, 100);
			incorrectCodeCheck = false;
		}
	}
</script>