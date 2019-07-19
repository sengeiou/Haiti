
var REGISTER = 3;
var DEMAND_REGISTER = 5;
var PROFILE_GENERIC = 7;
var CLOCK = 8;
var LIMITER = 71;
var MBUS_CLIENT = 72;


// 그리드 로딩
var confirmWin;
function dlmsGuideFunction(rec){
	
	confirmWin = new Ext.Window({
        title: '<b></b>',
        modal: true, closable:true, resizable: true,
        width:340, height:120,
        border:true, plain:false,                      
        items:[{
            xtype: 'panel',
            frame: false, border: false,
            items:{
              id: 'timeSet',
              xtype: 'form',
              bodyStyle:'padding:10px',
              labelWidth: 100,
              frame: false, border: false,
              items: [{
                xtype: 'label', html:'<div style="text-align:left;">' + 'You have to input the parameter.</p>What do you want to action?' +'</div>',  anchor: '100%'
              }]
            }
        }],
        
        buttons: [{
          text: 'Get',
          id : 'confirmWinGetBtn',
          handler: function() {
        	  $('#cmdGetObis').show();
        	  $('#cmdSetObis').hide();
        	  $('#cmdActionObis').hide();
        	  supportHandler.getSupport(rec);
          }
        }, {
          text: 'Set',
          id : 'confirmWinSetBtn',
          handler: function() {
        	  $('#cmdGetObis').hide();
        	  $('#cmdSetObis').show();
        	  $('#cmdActionObis').hide();
        	  supportHandler.setSupport(rec);
          }
        }, {
            text: 'Action',
            id : 'confirmWinActionBtn',
            handler: function() {
          	  $('#cmdGetObis').hide();
          	  $('#cmdSetObis').hide();
          	  $('#cmdActionObis').show();
          	  supportHandler.actSupport(rec);
            }
        }, {
            text: 'Cancle',
            handler: function() {
            	confirmWin.close();
            }
          }]
      });
	if ( "ACTION" ==  rec.data.ACCESSRIGHT ){
		Ext.getCmp('confirmWinGetBtn').hide();
		Ext.getCmp('confirmWinSetBtn').hide();
	}
	if ( "RO" ==  rec.data.ACCESSRIGHT ){
		Ext.getCmp('confirmWinActionBtn').hide();
		Ext.getCmp('confirmWinSetBtn').hide();
	}
	if ( "RW" ==  rec.data.ACCESSRIGHT ){
		Ext.getCmp('confirmWinActionBtn').hide();
	}
	confirmWin.show(this);
}

var supportHandler = {
		getSupport: function(rec) {
			if ("ACTION" == rec.data.ACCESSRIGHT  ){
				supportHandler.notSupport();
			}
			else if(rec.data.CLASSID == REGISTER) { //3 
				if(rec.data.ATTRIBUTENO == 2 || rec.data.ATTRIBUTENO == 3) {
					supportHandler.needNotParam();
				}else {
					//supportHandler.notSupport();
					supportHandler.needNotParam();
				}
			}else if(rec.data.CLASSID == DEMAND_REGISTER) { //5 
				//if(rec.data.ATTRIBUTENO == 8 || rec.data.ATTRIBUTENO == 9) {
				//	supportHandler.needNotParam();
				//}else {
					//supportHandler.notSupport();
					supportHandler.needNotParam();
				//}
			}else if(rec.data.CLASSID == PROFILE_GENERIC) { //7
				if(rec.data.ATTRIBUTENO == 2) {
					if((/^\d+.\d+.99.(98|1|2).\d+.255/g).test(rec.data.OBISCODE) || '1.0.99.97.0.255' == rec.data.OBISCODE 
							|| '0.0.24.3.0.255' == rec.data.OBISCODE ) {
						gridHandler.profileGeneric(rec);
					} else {
						//supportHandler.notSupport();
						supportHandler.needNotParam();
					}
				}else if(rec.data.ATTRIBUTENO == 3 || rec.data.ATTRIBUTENO == 4) {
					supportHandler.needNotParam();
				}else {
					//supportHandler.notSupport();
					supportHandler.needNotParam();
				}
			}else if(rec.data.CLASSID == CLOCK) { //8
				if(rec.data.ATTRIBUTENO == 2) {
					supportHandler.needNotParam();
				}else {
					//supportHandler.notSupport();
					supportHandler.needNotParam();
				}
			}else if(rec.data.CLASSID == LIMITER) { //71
				if(rec.data.ATTRIBUTENO == 4 || rec.data.ATTRIBUTENO == 6) {
					supportHandler.needNotParam();
				}else {
					//supportHandler.notSupport();
					supportHandler.needNotParam();
				}
			}else {
				//supportHandler.notSupport();
				supportHandler.needNotParam();
			}
		},
		setSupport: function(rec) {
			if ("ACTION" == rec.data.ACCESSRIGHT  ){
				supportHandler.notSupport();
			}
			else  if(rec.data.CLASSID == REGISTER) {
				if(rec.data.ATTRIBUTENO == 2 || rec.data.ATTRIBUTENO == 3) {
					gridHandler.register(rec);
				}else {
					"RW" == rec.data.ACCESSRIGHT ? gridHandler.register(rec) : supportHandler.notSupport();
				}
			}else if(rec.data.CLASSID == DEMAND_REGISTER) {
				//if(rec.data.ATTRIBUTENO == 8 || rec.data.ATTRIBUTENO == 9) {
				//	gridHandler.demandRegister(rec);
				//}else {
				"RW" == rec.data.ACCESSRIGHT ? gridHandler.register(rec) : supportHandler.notSupport();
				//}
			}else if(rec.data.CLASSID == PROFILE_GENERIC) {
				if(rec.data.ATTRIBUTENO == 4) {
					gridHandler.profileGeneric(rec);
				}else {
					//supportHandler.notSupport();
					"RW" == rec.data.ACCESSRIGHT ? gridHandler.register(rec) : supportHandler.notSupport();
				}
			}else if(rec.data.CLASSID == CLOCK) {
				if(rec.data.ATTRIBUTENO == 2) {
					gridHandler.clock(rec);
				}else {
					//supportHandler.notSupport();
					"RW" == rec.data.ACCESSRIGHT ? gridHandler.register(rec) : supportHandler.notSupport();
				}
			}else if(rec.data.CLASSID == LIMITER) {
				if(rec.data.ATTRIBUTENO == 4 || rec.data.ATTRIBUTENO == 6) {
					gridHandler.limiter(rec);
				}else {
					//supportHandler.notSupport();
					"RW" == rec.data.ACCESSRIGHT ? gridHandler.register(rec) : supportHandler.notSupport();
				}
			}else {
				//supportHandler.notSupport();
				gridHandler.register(rec);

			}
		},
		actSupport: function(rec) {
			if(rec.data.CLASSID == MBUS_CLIENT && "ACTION" == rec.data.ACCESSRIGHT ) {
				if(rec.data.ATTRIBUTENO == 7 || rec.data.ATTRIBUTENO == 8) {
					gridHandler.key(rec);
				}else if (rec.data.ATTRIBUTENO == 1 || rec.data.ATTRIBUTENO == 2) {
					gridHandler.register(rec);
				}else {
					supportHandler.notSupport();
				}
			}else {
				supportHandler.notSupport();
			}
		},
		notSupport: function() {
			Ext.Msg.alert("Warning","Not Supported Set&Get Command.");
			$('#cmdGetObis').hide();
			$('#cmdSetObis').hide();
			confirmWin.close();
		},
		needNotParam: function(rec) {
			Ext.Msg.alert("Information","The command need not Parameter.");
			detailCheck=true;
			confirmWin.close();
		}
}


var gridHandler = {
		register: function(rec) {
//			if(rec.data.ATTRIBUTENO == 2) {
//				dataHandler.setValue('value',rec);
//			} else if(rec.data.ATTRIBUTENO == 3) {
//				dataHandler.setScalerUnit(rec);
//			}
			if (rec.data.CLASSID == 3 && rec.data.ATTRIBUTENO == 3 ){
				dataHandler.setScalerUnit(rec);
			}
			else {
				dataHandler.setValue('value',rec);
			}
		},
		demandRegister: function(rec) {
			if(rec.data.ATTRIBUTENO == 8) {
				dataHandler.setValue('capture_period',rec);
			}
		},
		profileGeneric: function(rec) {
			if(rec.data.ATTRIBUTENO == 2) {
				$('#cmdSetObis').hide();
				dataHandler.buffer(rec);
			} else if(rec.data.ATTRIBUTENO == 3) {
				dataHandler.setObject(rec);
			} else if(rec.data.ATTRIBUTENO == 4) {
				dataHandler.setValue('capture_period',rec);
			}
		},
		clock: function(rec) {
			if(rec.data.ATTRIBUTENO == 2) {
				dataHandler.setTime(rec);
			}
		},
		activeCalendar: function(rec) {
			if(rec.data.ATTRIBUTENO == 4) {
				dataHandler.dailyProfile(rec);
			}
		},
		limiter: function(rec) {
			if(rec.data.ATTRIBUTENO == 4) {
				dataHandler.setValue('threshold_normal',rec);
			} else if(rec.data.ATTRIBUTENO == 6) {
				dataHandler.setValue('min over threshold_duration',rec);
			}
		},
		key: function(rec){
			if (  "ACTION" == rec.data.ACCESSRIGHT && rec.data.CLASSID == 72 ){
				if ( rec.data.ATTRIBUTENO == 7 ){
					dataHandler.setValue('encryption_key',rec);
				}
				else if (  rec.data.ATTRIBUTENO == 8 ){
					dataHandler.setValue('transfer_key ',rec);
				}
			}
		}
};

var addObjModel;
var addObjGrid;
var dataHandler = {
		buffer: function(rec) {
			var dateSet = '';
	          var searchWin = new Ext.Window({
	            title: '<b>Buffer Period Setting</b>',
	            modal: true, closable:true, resizable: true,
	            width:300, height:250,
	            border:true, plain:false,                      
	            items:[{
	                xtype: 'panel',
	                frame: false, border: false,
	                items:{
	                  id: 'timeSet',
	                  xtype: 'form',
	                  bodyStyle:'padding:10px',
	                  labelWidth: 100,
	                  frame: false, border: false,
	                  items: [{
	                    xtype: 'label', html:'<div style="text-align:left;">' + 'Please input Date-Time.' +'</div>',  anchor: '100%'
	                  }, {
	                    xtype: 'datefield', fieldLabel: 'From Date', id: 'fDate_id', name: 'fDate_name', anchor: '100%'
	                  }, {
	                	  xtype: 'textfield', fieldLabel: 'From Time(HH:MM:SS)', id: 'fTime_id', name: 'fTime_name', anchor: '100%'
	                  }, {
	                    xtype: 'datefield', fieldLabel: 'To Date', id: 'tDate_id', name: 'tDate_name', anchor: '100%'
	                  }, {
	                	  xtype: 'textfield', fieldLabel: 'To Time(HH:MM:SS)', id: 'tTime_id', name: 'tTime_name', anchor: '100%'
	                  }]
	                }
	            }],
	            
	            buttons: [{
	              text: 'Ok',
	              handler: function() {
					  var flag = true            	  
	            	  if(flag && Ext.getCmp('fDate_id').getValue() == null || Ext.getCmp('fDate_id').getValue() == '') {
	            		  Ext.Msg.alert("","Please select From Date");
	            		  flag = false;
	            		  return flag;
	            	  }
	            	  if(flag && (Ext.getCmp('fTime_id').getValue() == null || Ext.getCmp('fTime_id').getValue() == '')) {
	            		  Ext.Msg.alert("","Please input From Time");
	            		  flag = false;
	            		  return flag;
	            	  }
	            	  if(flag && Ext.getCmp('tDate_id').getValue() == null || Ext.getCmp('tDate_id').getValue() == '') {
	            		  Ext.Msg.alert("","Please select To Date");
	            		  flag = false;
	            		  return flag;
	            	  }
	            	  if(flag && (Ext.getCmp('tTime_id').getValue() == null || Ext.getCmp('tTime_id').getValue() == '')) {
	            		  Ext.Msg.alert("","Please input To Time");
	            		  flag = false;
	            		  return flag;
	            	  }
	                  
	            	  if(flag) {
	            		   var pattern = new RegExp(/^[0-2][0-9]:[0-5][0-9]:[0-5][0-9]$/);
	                      	if(pattern.test(Ext.getCmp('fTime_id').getValue())) {
	                      		flag = true;
	                      	} else {
	                      		flag = false;
	      	        			Ext.Msg.alert("Warning", "From Time Format [HH:MM:SS]",
	      			    				function() {return flag;});
	                      	}
	                      	
	                      	var splitTime = (Ext.getCmp('fTime_id').getValue()).split(":");
	                      	if(splitTime[0] > 23) {
	                      		Ext.Msg.alert("Warning", "Please check fTime's Hour. Hour's Range [0~23]",
	      			    				function() {return flag;});
	                      	} else {
	                      		flag = true;
	                      	}
	                      	
	                      	if(splitTime[1] > 59 || splitTime[2] > 59) {
	                      		Ext.Msg.alert("Warning", "Please check fTime's Minutes or second. Range [0~59]",
	      			    				function() {return flag;});
	                      	} else {
	                      		flag = true;
	                      	}
	                      	
	                      	if(pattern.test(Ext.getCmp('tTime_id').getValue())) {
	                      		flag = true;
	                      	} else {
	                      		flag = false;
	      	        			Ext.Msg.alert("Warning", "To Time Format [HH:MM:SS]",
	      			    				function() {return flag;});
	                      	}
	                      	
	                      	var splitTime = (Ext.getCmp('tTime_id').getValue()).split(":");
	                      	if(splitTime[0] > 23) {
	                      		Ext.Msg.alert("Warning", "Please check tTime's Hour. Hour's Range [0~23]",
	      			    				function() {return flag;});
	                      	} else {
	                      		flag = true;
	                      	}
	                      	
	                      	if(splitTime[1] > 59 || splitTime[2] > 59) {
	                      		Ext.Msg.alert("Warning", "Please check tTime's Minuts or second. Range [0~59]",
	      			    				function() {return flag;});
	                      	} else {
	                      		flag = true;
	                      	}
	                      	
	            	  }
	            	  
	            	  if(flag) {
	            		var fYear = Ext.getCmp('fDate_id').getValue().format('Y');
	            		var fMonth = Ext.getCmp('fDate_id').getValue().format('m');
	            		var fDayOfMonth = Ext.getCmp('fDate_id').getValue().format('d');
	            		var fDayOfWeek = Ext.getCmp('fDate_id').getValue().format('w');
	            		var fTimeSplit = Ext.getCmp('fTime_id').getValue().split(":");
	            		
	            		var tYear = Ext.getCmp('tDate_id').getValue().format('Y');
	            		var tMonth = Ext.getCmp('tDate_id').getValue().format('m');
	            		var tDayOfMonth = Ext.getCmp('tDate_id').getValue().format('d');
	            		var tDayOfWeek = Ext.getCmp('tDate_id').getValue().format('w');
	            		var tTimeSplit = Ext.getCmp('tTime_id').getValue().split(":");
	            		
	            		var url;
	            		if ( typeof(getObisCodeUrl) == "undefined"){
	            			url = "getObisCode.do";
	            		}
	            		else {
	            			url = getObisCodeUrl;
	            		}
	            		$.post(url,{
	            			modelId: DeviceModelSelectId,
	            			classId : "8",
	            			attributeNo : "2"
	            		},function(json) {
	            			var obisInfo = json.result[0];
	            			
	            			if(obisInfo.OBISCODE == null || obisInfo.OBISCODE == '') {
	            				flag = false;
	      	        			Ext.Msg.alert("Warning", "Please register CLOCK(class_id is 8) ObisCode.",
	      			    				function() {return flag;});
	            			}
	            			if(flag) {
	            				saveDetailParamArrList = new Array();
		    	        		
			            		var saveArr = {
			            				option: "1",
			            				clockObis: obisInfo.OBISCODE,
			        					fYear : fYear,
					                    fMonth : fMonth,
					                    fDayOfMonth : fDayOfMonth,
					                    fDayOfWeek : fDayOfWeek,
					                    fHh: fTimeSplit[0],
					                    fMm: fTimeSplit[1],
					                    fSs: fTimeSplit[2],
					                    tYear : tYear,
					                    tMonth : tMonth,
					                    tDayOfMonth : tDayOfMonth,
					                    tDayOfWeek : tDayOfWeek,
					                    tHh: tTimeSplit[0],
					                    tMm: tTimeSplit[1],
					                    tSs: tTimeSplit[2]
			            		}
				            	saveDetailParamArrList.push(saveArr);
			            		rec.data.VALUE = saveDetailParamArrList;
			            		rec.json.VALUE = saveDetailParamArrList;
	            			}
	            			
	            		});
	            		
	            		detailCheck=true;
	            		confirmWin.close();
	            		searchWin.close();
	            	  }
	            	  
	              }
	            }, {
	              text: 'Cancel',
	              handler: function() {
	            	  searchWin.close();
	              }
	            }]
	          });

	          searchWin.show(this);
		},
		setObject: function(rec) {
			$('#addObjCmp').empty();
			dataHandler.addObjectRow(rec);
		},
		addObjectRow: function(rec) {
			var addObjStore = new Ext.data.ArrayStore({
	  			  fields: ['no','obisCode','classId','attributeNo','dataIndex']
	  		  });
			var addObjModel = new Ext.grid.ColumnModel({
	          	columns:[
					{header: "No", tooltip:"No", dataIndex: 'no'},
	          		{header: "ObisCode", tooltip:"ObisCode", dataIndex: 'obisCode', 
	          			editor: new Ext.form.TextField({
	                        id : 'objCode'
	                    })
	          		},
	          		{header: "ClassId", tooltip:"ClassId", dataIndex: 'classId',
	          			editor: new Ext.form.TextField({
	                        id : 'objClassId'
	                    })
	          		},
	          		{header: "Attribute No", tooltip:"Attribute No", dataIndex: 'attributeNo',
	          			editor: new Ext.form.TextField({
	                        id : 'objAttrNo'
	                    })
	          		},
	          		{header: "Data Index", tooltip:"Data Index", dataIndex: 'dataIndex',
	          			editor: new Ext.form.TextField({
	                        id : 'objDataIndex'
	                    })
	          		}
	          	],
	          	defaults:{
	          		sortable: true,
	          		menuDisabled:true,
	          		editable: editSetting,
	          		renderer: addTooltip
	          	}
	          });
			addObjGrid = new Ext.grid.EditorGridPanel({
	   			 store: addObjStore,
	   			 autoScroll : true,
	   			 loadMask: true,
	   			 colModel: addObjModel,
	   			 listeners: {
	   				afteredit: function(e) {
	   					var data = e.record.data;
	   					var obisCode = data.obisCode;
	   					if(obisCode != null && obisCode !="" && 
	   							((obisCode.split(".").length != 6) || (obisCode.split(".").length == 6 && obisCode.split(".")[5] == ""))) {
	   						Ext.Msg.alert("Warning","Data Format Error\nx.x.x.x.x.x");
	   						addObjGrid.startEditing(e.row, 1);
	   						e.record.reject();
	   						return false;
	   					}
	   					
	   					
	   					if(addObjGrid.store.data.length != 0 && (addObjGrid.store.data.length-1 != addObjGrid.lastEdit.row)) {
	   			        	flag = dataHandler.validateObisM(addObjGrid,addGrid.lastEdit.row,data.obisCode,"Please enter the mandatory value.",1);
	   		                if(flag) {
	   		                	flag = dataHandler.validateObisM(addObjGrid,addGrid.lastEdit.row,data.classId,"Please enter the mandatory value.",2);
	   		                } else {
	   		                	e.record.reject();	
	   		                }

	   		                if(flag) {
	   		                	flag = dataHandler.validateObisM(addObjGrid,addGrid.lastEdit.row,data.attributeNo,"Please enter the mandatory value.",3);
	   		                } else {
	   		                	e.record.reject();
	   		                }
	   		                
	   		                if(flag && !(data.dataIndex == null || data.dataIndex.length == 0) && !(/^\d+/g).test(data.dataIndex)) {
	   		                	flas = false;
	   		                	Ext.Msg.alert("Warning", "Please enter the positive value.",
	   		 	    				function() {
	   		                			grid.startEditing(addGrid.lastEdit.row, 4);
	   		                			return false;
	   		                		});
	   		                } else {
	   		                	e.record.reject();
	   		                }
	   		                
	   			        	if(flag) {
	   			        		flag = dataHandler.checkDuplicate(data);
	   			        		if(!flag) {
	   			        			e.record.reject();
	   			        		}
	   			        	}
	   					}
	   					
	   					//modify된 내용이 저장되지 않아 강제로 저장.
	   					if(e.column == 1) {
	   						e.record.modified.obisCode = e.value;
	   					} else if(e.column == 2) {
	   						e.record.modified.classId = e.value;
	   					} else if(e.column == 3) {
	   						e.record.modified.attributeNo = e.value;
	   					} else if(e.column == 4) {
	   						e.record.modified.dataIndex = e.value;
	   					}  
	   					
	   				}
	   			 },
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: 500,
	  		      height: 300,
	  		      tbar:[{
	  		    	  iconCls: 'icon-obis-add',
	  		    	  text: "Add",
	  		    	  handler: function() {
	  		    		dataHandler.addObjData();
	  		    	  }
	  		      },{
	  		    	  iconCls: 'icon-obis-delete',
	  		    	  text: "Delete",
	  		    	  handler: function() {
	  		    		dataHandler.delObjData();
	  		    	  }
	  		      }]
	   		  });
			
			$.ajaxSetup({
                async : false
            });
			
			var addObjWin = new Ext.Window({
        		title: "Channel Setting",
        		id: 'addObjCmp',
        		applyTo: 'addObjCmp',
        		width:500, 
                shadow : false,
                autoHeight: true,
                //clicksToEdit : 1,
                pageX : 460,
                pageY : 130, 
                plain: true,
                items: [addObjGrid],
                buttons : [{text : 'Save',
		            	handler : function() {	
		            		
		            		var records = addObjStore.data.items;
		            		var flag = false;
		            		
		            		//마지막라인에 대해서 유효성 체크
		            		if(records.length-1 < 0) {
		            			Ext.Msg.alert("Error","Data is Empty.");
		            			flag = false;
		            			return false;
		            		}
		            		var lastRecord = records[records.length-1].data;
		            		
		            		flag = dataHandler.validateObisM(addObjGrid,records.length-1,lastRecord.obisCode,"Please enter the mandatory value.",1);

	        	        	if(flag) {
	        	        		flag = dataHandler.validateObisM(addObjGrid,records.length-1,lastRecord.classId, "Please enter the mandatory value.", 2);
	        	        	}
	        	        	
	        	        	if(flag) {
	        	        		flag = dataHandler.validateObisM(addObjGrid,records.length-1,lastRecord.attributeNo, "Please enter the mandatory value.", 3);
	        	        	}

	        	        	if(flag && !(lastRecord.dataIndex == null || lastRecord.dataIndex.length == 0) && !(/^\d+/g).test(lastRecord.dataIndex)) {
	        	        		flag = false;
	        	        		Ext.Msg.alert("Warning", "Please enter the positive value.",
	            	    				function() {
	                        				addObjGrid.startEditing(addObjGrid.lastRecord.dataIndex, 4);
	            	    					return false;
	            	    		});
	        	        		
	        	        	}
	        	        	
	        	        	if(flag) {
	        	        		saveDetailParamArrList = new Array();
		    	        		
	        	        		for(var i = 0; i<records.length; i++) {
			            			var saveArr = new Array();
			            			saveArr.push({
			            				no : records[i].data.no,
			            				obisCode : records[i].data.obisCode,
					                    classId : records[i].data.classId,
					                    attributeNo : records[i].data.attributeNo,
					                    dataIndex : (records[i].data.dataIndex == null || records[i].data.dataIndex.length == 0) ? 0 : records[i].data.dataIndex
				            		})
				            		
				            		saveDetailParamArrList.push(saveArr);
			            		}

			            		rec.data.VALUE = saveDetailParamArrList;
			            		rec.json.VALUE = saveDetailParamArrList;
			            		
			            		detailCheck=true;
			            		addObjWin.close();
		                    	confirmWin.close();
	        	        	}
			                    	
	                    }
            		},
                	{text : 'Close',
                	handler : function() {
                		addObjWin.hide(this);        		
                	}}],
                closeAction:'hide',	                
                onHide : function(){
                }       
        	});
			
			addObjWin.show(this);
		},
		addObjData: function() {
        	$.ajaxSetup({
                async : false
            });
			var flag = true;
			
			var store = addObjGrid.getStore();
			
        	if(store.data.length > 0) {
	        	var preRecord = store.data.last().data;
                
	        	flag = dataHandler.validateObisM(addObjGrid,addObjGrid.lastEdit.row,preRecord.obisCode,"Please enter the mandatory value.",1);
	        	
                if(flag) {
                	flag = dataHandler.validateObisM(addObjGrid,addObjGrid.lastEdit.row,preRecord.classId,"Please enter the mandatory value.",2);
                }

                if(flag) {
                	flag = dataHandler.validateObisM(addObjGrid,addObjGrid.lastEdit.row,preRecord.attributeNo,"Please enter the mandatory value.",3);
                }

                if(flag && !(preRecord.dataIndex == null || preRecord.dataIndex.length == 0) && !(/^\d+/g).test(preRecord.dataIndex)) {
                	flag = false;
                	Ext.Msg.alert("Warning", "Please enter the positive value.",
    	    				function() {
                				addObjGrid.startEditing(addObjGrid.lastEdit.row, 4);
    	    					return false;
    	    		});
                }
	        	
        	}

        	if(flag) {
                var Plant = store.recordType;
	            var p = new Plant({
	            	no : store.data.getCount() + 1,
	                obisCode : "",
	                classId : "",
	                attributeNo : "",
	                descr : ""
	            });
	            var length = store.getCount();
	            addObjGrid.stopEditing();
	            store.insert(length, p);
	            addObjGrid.startEditing(length, 1);
	            addObjGrid.getSelectionModel().selectLastRow();
        	}
        },
        delObjData: function() {
        	addObjGrid.stopEditing();
            var s = addObjGrid.getSelectionModel().selection.record
            addObjGrid.store.remove(s);  
        },
        validateObisM: function(grid,rec,data,msg,row) {
        	if(data == null || data == "") {
	        	Ext.Msg.alert("Warning", msg,
	    				function() {
	    			grid.startEditing(rec, row);
	        		return false;
	    		});
        	} else {
        		return true;
        	}
        },
		setValue: function(title, rec) {
			var dateSet = '';
	          var searchWin = new Ext.Window({
	            title: '<b>Setting</b>',
	            modal: true, closable:true, resizable: true,
	            width:380, height:130,
	            border:true, plain:false,                      
	            items:[{
	                xtype: 'panel',
	                frame: false, border: false,
	                items:{
	                  id: 'valueSet',
	                  xtype: 'form',
	                  bodyStyle:'padding:10px',
	                  labelWidth: 100,
	                  frame: false, border: false,
	                  items: [{
	                    xtype: 'label', html:'<div style="text-align:left;">' + 'Please input '+ title +'</div>',  anchor: '100%'
	                  }, {
	                	  xtype: 'textfield', fieldLabel: title, id: 'title_id', name: 'title_name', anchor: '100%'
	                  }]
	                }
	            }],
	            
	            buttons: [{
	              text: 'Ok',
	              handler: function() {
					  var flag = true     
					  if(rec.data.CLASSID == MBUS_CLIENT && "ACTION" == rec.data.ACCESSRIGHT ) {
						  if(rec.data.ATTRIBUTENO == 7 || rec.data.ATTRIBUTENO == 8) {
							  var val = Ext.getCmp('title_id').getValue();
							  var pattern = new RegExp(/^[0-9A-Fa-f]{0,32}$/);
							  if(pattern.test(Ext.getCmp('title_id').getValue())) {
								  flag = true;
							  } else {
								  flag = false;
								  Ext.Msg.alert("","Please input 32 or less hex char for " + title);
								  return flag;
							  }
							  if (rec.data.ATTRIBUTENO == 8){
								  if ( val == null || val == '' ){
				            		  flag = false;
									  Ext.Msg.alert("","Please input  " + title);
				            		  return flag;
								  }
							  }
						  }
						  else if (rec.data.ATTRIBUTENO == 1) {
							  var pattern = new RegExp(/^[0-9]+$/);
							  if(pattern.test(Ext.getCmp('title_id').getValue())) {
								  flag = true;
							  } else {
								  flag = false;
								  Ext.Msg.alert("","Please input number. " + title);
								  return flag;
							  }
						  }
						  else if (rec.data.ATTRIBUTENO == 2) {
							  var pattern = new RegExp(/^0$/);
							  if(pattern.test(Ext.getCmp('title_id').getValue())) {
								  flag = true;
							  } else {
								  flag = false;
								  Ext.Msg.alert("","Please input 0. " + title);
								  return flag;
							  }
						  }
					  }   
					  else if(flag && Ext.getCmp('title_id').getValue() == null || Ext.getCmp('title_id').getValue() == '') {
	            		  Ext.Msg.alert("","Please input " + title);
	            		  flag = false;
	            		  return flag;
	            	  }
	                  
	            	  if(flag) {
	            		saveDetailParamArrList = new Array();
    	        		
	            		var saveArr = {
	        					'value' : Ext.getCmp('title_id').getValue()
	            		}
	            		
  		
	            		
		            	saveDetailParamArrList.push(saveArr);
	            		rec.data.VALUE = saveDetailParamArrList;
	            		rec.json.VALUE = saveDetailParamArrList;
	            		
	            		detailCheck=true;
	            		confirmWin.close();
	            		searchWin.close();
	            	  }
	            	  
	              }
	            }, {
	              text: 'Cancel',
	              handler: function() {
	            	  searchWin.close();
	              }
	            }]
	          });

	          searchWin.show(this);
		},
		setScalerUnit: function(rec) {
			var unitCode = '';
			var dateSet = '';
	          var searchWin = new Ext.Window({
	            title: '<b>Setting</b>',
	            modal: true, closable:true, resizable: true,
	            width:300, height:130,
	            border:true, plain:false,                      
	            items:[{
	                xtype: 'panel',
	                frame: false, border: false,
	                items:{
	                  id: 'valueSet',
	                  xtype: 'form',
	                  bodyStyle:'padding:10px',
	                  labelWidth: 100,
	                  frame: false, border: false,
	                  items: [{
	                	  xtype: 'textfield', fieldLabel: 'Scaler', id: 'scaler_id', name: 'scaler_name', anchor: '100%'
	                  },{
	                      xtype: 'combo',
	                      id:'unit_id', name: 'unit_name', value:'Select...',          
	                      fieldLabel: 'Unit', triggerAction: 'all', editable: false, mode: 'local',
	                      store: new Ext.data.JsonStore({
	                        url: 'getUnitForKaifa.do',
	                        storeId: 'unitStore',
	                        root: 'result',
	                        idProperty: 'name',
	                        fields: ['name',{name: 'code', type: 'int'}],
	                        listeners: {
	                          load: function(store, records, options){
	                            Ext.getCmp('unit_id').setValue(records[0].data.name);
	                            unitCode = records[0].data.code;
	                          }
	                        }
	                      }),
	                      valueField: 'id', displayField: 'name',
	                      anchor: '100%',
	                      listeners: {
	                        render: function() {
	                          this.store.load();
	                        },
	                        select : function(combo, record, index){
	                          Ext.getCmp('unit_id').setValue(record.data.name);
	                          unitCode = record.data.code;
	                        }
	                      }
	                    }]
	                }
	            }],
	            
	            buttons: [{
	              text: 'Ok',
	              handler: function() {
					  var flag = true      
					  if(flag && Ext.getCmp('scaler_id').getValue() == null || Ext.getCmp('scaler_id').getValue() == '') {
	            		  Ext.Msg.alert("","Please input scaler");
	            		  flag = false;
	            		  return flag;
	            	  }
					  
					  if(flag && (Ext.getCmp('scaler_id').getValue() >= -127 && Ext.getCmp('scaler_id').getValue() <= 128)
							  && (isNaN(Ext.getCmp('scaler_id').getValue()) || Ext.getCmp('scaler_id').getValue().indexOf(".") >= 0)) {
	            		  Ext.Msg.alert("","PleaExt.getCmp('scaler_id').getValue()se check scaler range [-127 ~ 128] ");
	            		  flag = false;
	            		  return flag;
	            	  }
					  
	            	  if(flag && Ext.getCmp('unit_id').getValue() == null || Ext.getCmp('unit_id').getValue() == '') {
	            		  Ext.Msg.alert("","Please select Unit");
	            		  flag = false;
	            		  return flag;
	            	  }
	                  
	            	  if(flag) {
	            		saveDetailParamArrList = new Array();
    	        		
	            		var saveArr = {
	        					'scaler' : Ext.getCmp('scaler_id').getValue(),
	        					'unit' : unitCode
	            		}
		            	saveDetailParamArrList.push(saveArr);
	            		rec.data.VALUE = saveDetailParamArrList;
	            		rec.json.VALUE = saveDetailParamArrList;
	            		
	            		detailCheck=true;
	            		confirmWin.close();
	            		searchWin.close();
	            	  }
	            	  
	              }
	            }, {
	              text: 'Cancel',
	              handler: function() {
	            	  searchWin.close();
	              }
	            }]
	          });

	          searchWin.show(this);
		},
		setTime: function(rec) {
			var dateSet = '';
	          var searchWin = new Ext.Window({
	            title: '<b>Time Setting</b>',
	            modal: true, closable:true, resizable: true,
	            width:300, height:200,
	            border:true, plain:false,                      
	            items:[{
	                xtype: 'panel',
	                frame: false, border: false,
	                items:{
	                  id: 'timeSet',
	                  xtype: 'form',
	                  bodyStyle:'padding:10px',
	                  labelWidth: 100,
	                  frame: false, border: false,
	                  items: [{
	                    xtype: 'label', html:'<div style="text-align:left;">' + 'Please input Date-Time.' +'</div>',  anchor: '100%'
	                  }, {
	                    xtype: 'datefield', fieldLabel: 'Date', id: 'date_id', name: 'date_name', anchor: '100%'
	                  }, {
	                	  xtype: 'textfield', fieldLabel: 'Time(HH:MM:SS)', id: 'time_id', name: 'time_name', anchor: '100%'
	                  }, {
	                	  xtype: 'checkbox', boxLabel: 'Daylight saving active', checked:false, inputValue:'daylight', allowBlank:true, id:'daylight_id', anchor: '100%'
	                  }, {
	                	  xtype: 'checkbox', boxLabel: 'Use PC datetime', checked:false, inputValue:'pcTime', allowBlank:true, id:'pcTime_id', anchor: '100%'
	                  }]
	                }
	            }],
	            
	            buttons: [{
	              text: 'Ok',
	              handler: function() {
	            	  var yyyy = '';
	                  var month = '';
	            	  var dayOfMonth = '';
	            	  var dayOfWeek = '';
	            	  var timeSplit = '';
	            	  
	            	  var flag = true;
	            	  var pcTime = Ext.getCmp('pcTime_id').getValue() == true ? 'true' : 'false';
	            	  if(pcTime != 'true') {
	            		  if(flag && Ext.getCmp('date_id').getValue() == null || Ext.getCmp('date_id').getValue() == '') {
		            		  Ext.Msg.alert("","Please select Date");
		            		  flag = false;
		            		  return flag;
		            	  }
		            	  if(flag && (Ext.getCmp('time_id').getValue() == null || Ext.getCmp('time_id').getValue() == '')) {
		            		  Ext.Msg.alert("","Please input time");
		            		  flag = false;
		            		  return flag;
		            	  }
		                  
		            	  if(flag) {
		            		   var pattern = new RegExp(/^[0-2][0-9]:[0-5][0-9]:[0-5][0-9]$/);
		                      	if(pattern.test(Ext.getCmp('time_id').getValue())) {
		                      		flag = true;
		                      	} else {
		                      		flag = false;
		      	        			Ext.Msg.alert("Warning", "Start Time Format [HH:MM:SS]",
		      			    				function() {return false;});
		                      	}
		            	  }
		            	  
		            	  var splitTime = (Ext.getCmp('time_id').getValue()).split(":");
		                	if(splitTime[0] > 23) {
		                		Ext.Msg.alert("Warning", "Please check fTime's Hour. Hour's Range [0~23]",
		      			    				function() {return false;});
		                	} else {
		                		flag = true;
		                	}
		                	
		                	if(splitTime[1] > 59 || splitTime[2] > 59) {
		                		Ext.Msg.alert("Warning", "Please check fTime's Minutes or second. Range [0~59]",
		      			    				function() {return false;});
		                  	} else {
		                  		flag = true;
		                  	}
		                	
		                	yyyy = Ext.getCmp('date_id').getValue().format('Y');
		            		month = Ext.getCmp('date_id').getValue().format('m');
		            		dayOfMonth = Ext.getCmp('date_id').getValue().format('d');
		            		dayOfWeek = Ext.getCmp('date_id').getValue().format('w');
		            		timeSplit = Ext.getCmp('time_id').getValue().split(":");
		                	
	            	  }
	            	  
	            	  var daylight = Ext.getCmp('daylight_id').getValue() == true ? 1 : 0;
	            	  
	            	  if(flag) {
	            		
	            		saveDetailParamArrList = new Array();
      	        		
	            		var saveArr = {
	        					year : yyyy,
			                    month : month,
			                    dayOfMonth : dayOfMonth,
			                    dayOfWeek : dayOfWeek,
			                    hh: timeSplit == "" ? "" : timeSplit[0],
			                    mm: timeSplit == "" ? "" : timeSplit[1],
			                    ss: timeSplit == "" ? "" : timeSplit[2],
			                    daylight:daylight,
			                    pcTime:pcTime,
	            		}
		            	saveDetailParamArrList.push(saveArr);
	            		rec.data.VALUE = saveDetailParamArrList;
	            		rec.json.VALUE = saveDetailParamArrList;
	            		
	            		detailCheck=true;
	            		confirmWin.close();
	            		searchWin.close();
	            	  }
	            	  
	              }
	            }, {
	              text: 'Cancel',
	              handler: function() {
	            	  searchWin.close();
	              }
	            }]
	          });

	          searchWin.show(this);
		},
		dailyProfile: function(rec) {
			//현재 dayProfile만 화면 개발됨
       	  	$.ajaxSetup({
                async : false
            });
       	
	      	  addDailyProfileStore = new Ext.data.ArrayStore({
	    			  fields: ['dayId','startTime','scriptLogicalName','scriptSelector']
	    		  });
	
	      	  addDailyProfileModel = new Ext.grid.ColumnModel({
	            	columns:[
	            		{header: "Day Id", tooltip:"Day Id", dataIndex: 'dayId', editable:true,
	            			editor: new Ext.form.TextField({
	                          id : 'dayId'
	                      })
	            		},
	            		{header: "Start Time", tooltip:"Start Time", dataIndex: 'startTime', editable:true,
	            			editor: new Ext.form.TextField({
	                          id : 'startTime'
	                      })
	            		},
	            		{header: "Script Logical Name", tooltip:"Script Logical Name", dataIndex: 'scriptLogicalName', editable:false},
	            		{header: "Script Selector", tooltip:"Script Selector", dataIndex: 'scriptSelector', editable:true,
	            			editor: new Ext.form.TextField({
	                          id : 'scriptSelector'
	                      })
	            		}
	            	],
	            	defaults:{
	            		sortable: true,
	            		menuDisabled:true,
	            		renderer: addTooltip
	            	}
	            });
	  		  
	      	  $('#detailViewDiv').empty();
	            addDailyProfileGrid = new Ext.grid.EditorGridPanel({
	  	   			 store: addDailyProfileStore,
	  	   			 autoScroll : true,
	  	   			 loadMask: true,
	  	   			 colModel: addDailyProfileModel,
	  	   			 viewConfig: {forceFit: true},
	  	   			 autoScroll : true,
	  	             scroll : true,
	  	             stripeRows : true,
	  	             columnLines : true,
	  	             renderTo : 'detailViewDiv',
	  	             loadMask : {
	  	                msg : 'loading...'
	  	             },
	  	  		      width: 670,
	  	  		      height: 200,
	  	  		      tbar:[{
	  	  		    	  iconCls: 'icon-obis-add',
	  	  		    	  text: "<b>Add</b>",
	  	  		    	  handler: function() {
	  	  		    		dataHandler.addDailyProfile(rec.data.OBISCODE);
	  	  		    	  }
	  	  		      },{
	  	  		    	  iconCls: 'icon-obis-delete',
	  	  		    	  text: "<b>Delete</b>",
	  	  		    	  handler: function() {
	  	  		    		dataHandler.delDailyProfile();
	  	  		    	  }
	  	  		      }]
	  	   		  });
	            
	            var detailViewWin = new Ext.Window({
	                title : 'Detail View',
	                id : 'detailViewWinId',
	                applyTo : 'detailViewDiv',
	                autoScroll : true,
	                autoHeight : true,
	                pageX : 400,
	                pageY : 130,
	                width : 670,
	                height : 200,
	                items : addDailyProfileGrid,
	                buttons : [{text : 'Save',
		            	handler : function() {	

		            		var records = addDailyProfileStore.data.items;
		            		var flag = false;
		            		
		            		//마지막라인에 대해서 유효성 체크
		            		if(records.length-1 < 0) {
		            			Ext.Msg.alert("Error","Data is Empty.");
		            			flag = false;
		            			return false;
		            		}
		            		var lastRecord = records[records.length-1].data;
		            		flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,lastRecord.dayId,"Please enter the mandatory value",0);
		            		
		            		if(flag) {
	        	        		var pattern = new RegExp(/^[0-9]+$/);
	        	        		if((lastRecord.dayId >= 1 || lastRecord.dayId <= 16) && pattern.test(lastRecord.dayId)) {
	        	        			flag = true;
	        	        		} else {
	        	        			flag = false;
	        	        			Ext.Msg.alert("Warning", "Day Id's Range : 1 ~ 16",
	        			    				function() {addDailyProfileGrid.startEditing(addDailyProfileGrid.lastEdit.row, 0); return false;});
	        	        		}
	        	        	}
	        	        	if(flag) {
	        	        		flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,lastRecord.startTime, "Please enter the mandatory value Start Time Format [HH:MM:SS]", 1);
	        	        	}
	        	        	
	        	        	 if(flag) {
	        	        		 var pattern = new RegExp(/^[0-2][0-9]:[0-5][0-9]:[0-5][0-9]$/);
		                        	if(pattern.test(lastRecord.startTime)) {
		                        		flag = true;
		                        	} else {
		                        		flag = false;
		        	        			Ext.Msg.alert("Warning", "Start Time Format [HH:MM:SS]",
		        			    				function() {addDailyProfileGrid.startEditing(addDailyProfileGrid.lastEdit, 1); return false;});
		                        	}
		                        }
	        	        	if(flag) {
	        	        		flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,lastRecord.scriptSelector, "Please enter the mandatory value", 3);
	        	        	}
	        	        	if(flag) {
	        	        		saveDetailParamArrList = new Array();
	        	        		
			            		for(var i = 0; i<records.length; i++) {
			            			var saveArr = {
		            					dayId : records[i].data.dayId,
					                    scriptLogicalName : records[i].data.scriptLogicalName,
					                    scriptSelector : records[i].data.scriptSelector,
					                    startTime : records[i].data.startTime	
			            			}
				            		saveDetailParamArrList.push(saveArr);
			            			rec.data.VALUE = saveDetailParamArrList;
			            			rec.json.VALUE = saveDetailParamArrList;
			            		}
			            		detailCheck=true;
			            		confirmWin.close();
			            		detailViewWin.hide(this);
	        	        	}
	        	        	
	                	}},
	                	{text : 'Close',
	                	handler : function() {
	                		saveDetailParamArrList = undefined;
	                		detailViewWin.hide(this);        		
	                	}}],
	                closeAction : 'hide',
	                onHide : function() {
	                	saveDetailParamArrList = undefined;
	                }
	            });
	            Ext.getCmp('detailViewWinId').show();
		},
        addDailyProfile: function(obisCode) {
        	$.ajaxSetup({
                async : false
            });
			var flag = true;
			
			var store = addDailyProfileGrid.getStore();
        	if(store.data.length > 0) {
	        	var preRecord = store.data.last().data;
                
	        	flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,preRecord.dayId,"Please enter the mandatory value.",0);
	        	if(flag) {
	        		var pattern = new RegExp(/^[0-9]+$/);
	        		if((preRecord.dayId >= 1 || preRecord.dayId <= 16) && pattern.test(preRecord.dayId)) {
	        			flag = true;
	        		} else {
	        			flag = false;
	        			Ext.Msg.alert("Warning","Please enter the mandatory value. Day Id's Range : 1 ~ 16",
			    				function() {addDailyProfileGrid.startEditing(addDailyProfileGrid.lastEdit.row, 0); return false;});
	        		}
	        	}
	        	
                if(flag) {
                	flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,preRecord.startTime,"Please enter the mandatory value. Start Time Format [HH:MM:SS]",1);
                }
                
                if(flag) {
                	var pattern = new RegExp(/^[0-2][0-9]:[0-5][0-9]:[0-5][0-9]$/);
                	if(pattern.test(preRecord.startTime)) {
                		flag = true;
                	} else {
                		flag = false;
	        			Ext.Msg.alert("Warning","Please enter the mandatory value. Start Time Format [HH:MM:SS]",
			    				function() {addDailyProfileGrid.startEditing(addDailyProfileGrid.lastEdit.row, 1); return false;});
                	}
                }
                
                if(flag) {
                	flag = dataHandler.validateM(addDailyProfileGrid.lastEdit.row,preRecord.scriptSelector,"Please enter the mandatory value.",3);
                }

        	}
        	

        	if(flag) {
                var Plant = store.recordType;
	            var p = new Plant({
	                dayId : "",
	                startTime : "",
	                scriptLogicalName : obisCode, //obisCode로 고정
	                scriptSelector : ""
	            });
	            var length = store.getCount();
	            addDailyProfileGrid.stopEditing();
	            addDailyProfileStore.insert(length, p);
	            addDailyProfileGrid.startEditing(length, 0);
	            addDailyProfileGrid.getSelectionModel().selectLastRow();
        	}
        },
        delDailyProfile: function() {
	        addDailyProfileGrid.stopEditing();
	        var s = addDailyProfileGrid.getSelectionModel().selection.record
	        addDailyProfileStore.remove(s);  
        },
    	validateM: function(rec,data,msg,row) {
    		if(data == null || data == "") {
	        	Ext.Msg.alert("Warning", msg,
	    				function() {
	    			addDailyProfileGrid.startEditing(rec, row);
	        		return false;
	    		});
        	} else {
        		return true;
        	}
    	},
        editFunction: function(value,meta,rec) {
        	if(rec.data.DATATYPE == "array") {
        		return false;
        	} else {
        		return new Ext.form.TextField({id: 'value', allowNegative: false})
        	}
        },
};
