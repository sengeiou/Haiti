<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/soundmanager2-nodebug.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/resources/PagingStore.js"></script>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<style type="text/css" media="screen">
    /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
    .x-panel-bbar table {border-collapse: collapse; width:auto;} 
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
     @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }

    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }
    /* ext-js grid 행 높이 고정 
	   cancel이 버튼인 row와 텍스트인 경우 row의 높이가 다르므로 임의로 수정 
    */
     td.x-grid3-col.x-grid3-cell {
      height: 28px;
    }    

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }

</style>

</head>
<body onLoad="javascript:hide()">
<div id="plcTestTab">
    <ul>
        <li><a href="#test" id="_test"><fmt:message key="aimir.poc.test" /></a></li>
        <li><a href="#result" id="_result"><fmt:message key="aimir.poc.result" /></a></li>
    </ul>
    <div id="test" class="tabcontentsbox">
        <div id="test_dialog1"  class="mvm-popwin-iframe-outer ">
	        <table>
	        	<tr>
	        		<td  class="gray11pt" width="80px">
	        			<span><fmt:message key='aimir.zig.name'/></span>
	        		</td>
					<td  class="gray11pt" width="80px">
						<span><input type="text" id="inputTestZigName" name="inputTestZigName" /></span>
					</td>
					<td  class="gray11pt">
						<div id="btn">
				    		<ul><li><a href="javascript:eventHandler.searchZigList();" class="on"><fmt:message key='aimir.button.search'/></a></li></ul>
			    		</div>
					</td>        	
	        	</tr>
	        </table>
	    </div>
	    <div class="dashedline"></div>
   		<div id="test_dialog2" class="floatleft mvm-popwin-iframe-outer" style="width: 30%">
			<label class="check"><fmt:message key="aimir.zigList"/></label>
			<br style="clear:both;">
			<div class="zig-container">
			    <div id="btn" class="btn_right floatright">
					<ul><li><a href="javascript:addZig();" class="on"><fmt:message key='aimir.zig.add'/></a></li></ul>
					<ul><li><a href="javascript:delZig();" class="on"><fmt:message key='aimir.zig.del'/></a></li></ul>
			    </div>
			    <div id="zigList" class="floatleft" style="width: 100%"></div>
			    <div id="btn" class="btn_right margin-t10px floatright">
					<ul><li><a href="javascript:testStart();" class="on"><fmt:message key='aimir.test.start'/></a></li></ul>
                    <ul><li><a href="javascript:testEnd();" class="on"><fmt:message key='aimir.test.end'/></a></li></ul>
			    </div>
			</div>
			<br style="clear:both;">
			<div id="testResult"></div>
   		</div>

   		<div id="test_dialog3" class="floatleft mvm-popwin-iframe-outer" style="width: 60%">
			<label class="check"><fmt:message key='aimir.zig.assetList'/></label>
			<br style="clear:both;">
			<div class="zig-container" style="margin-top: 20px">
			    <div id="assetList"  style="width: 100%"></div>
			</div>
    	</div>
 	</div>
	<div id="addZigPop"></div>
	<div id="fileUpload"></div>
	<input type="hidden" id="verify" name="verify" value="false"/>
	<input type="hidden" id="ext" name="ext" value=""/>
	<input type="hidden" id="saveFileName" name="saveFileName" value=""/>
	<input type="hidden" id="selectedTestZigName" name="selectedTestZigName" value=""/>
	<input type="hidden" id="selectedResultZigName" name="selectedResultZigName" value=""/>
	<div id="result" class="tabcontentsbox">
		<div id="result_dialog1" class="mvm-popwin-iframe-outer">
			<table class="resultoption wfree" >
				<tr>	
					<td class="gray11pt">
						<select id="searchSelect" style="width: 150px;" name="select" onchange="javascript:changeSelect();">
								<option value="all"><fmt:message key="aimir.all" /></option>
								<option value="latest" selected>최신 테스트 결과</option>
                                <option value="complete"><fmt:message key="aimir.detail.completeDate" /></option>
                                <option value="start">테스트 시작 날짜</option>
                        </select>
					</td>
					<td id="completeDate" class="gray11pt" style="display: none;">
						<span><input id="startDate" class="day" type="text"></span>
						<span><input value="~" class="between" type="text"></span>
						<span><input id="endDate" class="day" type="text"></span>
						<input id="startDateHidden" type="hidden" />
						<input id="endDateHidden" type="hidden" />
					</td>
					<td></td>
					<td class="gray11pt withinput">
						<span><fmt:message key='aimir.test.result'/></span>
					</td>
					<td  >
						<select id="selectTestResult" name="selectTestResult" style="width: 90px">
							<option value="all" selected><fmt:message key="aimir.all" /></option>
							<option value="success"><fmt:message key="aimir.success" /></option>
                        	<option value="fail"><fmt:message key="aimir.fail" /></option>
                        	<option value="unKnown"><fmt:message key="aimir.unknown" /></option>
						</select>
					</td>
					<td class="gray11pt withinput">
						<span><fmt:message key='aimir.zig.name'/></span>
					</td>
					<td>
	    				<span><input type="text" id="inputZigName" name="inputZigName" /></span>
					</td>
					<td class="btn_right margin-t10px">
						<div id="btn">
				    		<ul><li><a href="javascript:searchZig();" class="on"><fmt:message key='aimir.button.search'/></a></li></ul>
			    		</div>
					</td>
				</tr>		
			</table>
     </div>
     <div class="dashedline"></div>
     <div id="result_dialog2" >
     	<div id="result_dialog2_list" class="floatleft mvm-popwin-iframe-outer" style="width: 30%">
     		<label class="check"><fmt:message key='aimir.searchResult'/></label>
   			<div id="btn" class="btn_right floatright">
				<ul><li><a href="javascript:exportResult();" class="on"><fmt:message key='aimir.button.excel'/></a></li></ul>
			</div>
     		<div id="zigResult" class="floatleft"  style="width: 100%; margin-top: 10px"></div>
	    </div>
     	<div id="result_dialog2_detail" class="floatleft mvm-popwin-iframe-outer" style="width: 60%">
     		<label class="check floatleft"><fmt:message key='aimir.test.detailResult'/></label>
     		<div style="float: right;">
	     		<b><span id="lastResult" style="font-size: 26px">0/0</span></b>
	       		<div id="btn" class="floatright">
					<ul><li><a href="javascript:exportResultDetail();" class="on"><fmt:message key='aimir.button.excel'/></a></li></ul>
				</div>
			</div>
			<div style="float: right;">
   					<b><span id="minute" style="font-size: 26px;">10</span><span id=":" style="font-size: 26px;">:</span><span id="second" style="font-size: 26px; width: 60px">00</span></b>
   			</div>
			<div id="detailResult" class="floatleft"  style="width: 100%"></div>
     	</div>
     </div>
 </div>
</div>
<script type="text/javascript" charset="utf-8">
                 
    var selectedZigTestRows = new Array();
	var DEFAULT_SIZE = 10;                        
	var supplierId = "${supplierId}";
	var init=true;
    var numberFormat = "";
    
    var checkTimer;
    var timer;
    var html_minute;
    var html_second;
    var minute = 10;
    var second = 0;
    
    var testZigName;

	var exportWin;
	var exportDetailWin;
	
	var addZigUpload;
	var addZigWin;
	
	var zigListGrid;
	var zigListStore;
	var zigListParams;
	var zigWidth;

	var assetListGrid;
	var assetListStore;
	var assetListParams;
	var assetWidth;
	
	var zigResultGrid;
	var zigResultStore;
	var zigResultParams;
	var zigResultWidth;
	
	var detailResultGrid;
	var detailResultStore;
	var detailResultParams;
	var detailWidth;

	var defaultStoreParams = {
			page: 1, 
			start: 0,
			limit: DEFAULT_SIZE	
		};
	
    var sm = {
    		singleSelect: true,
    		moveEditorOnEnter: false
    	};
    var defaultSm = new Ext.grid.RowSelectionModel($.extend(true, {}, sm));
    var defaultGridProp = {
    		clicksToEdit: 1,	
    		autoScroll: false,
    		stripeRows: true,
    		columnLines: true,
    		loadMask: {
    			msg: "loading..."
    		},
    		viewConfig: {
    			forceFit: true,
    			scrollOffset: 1,
    			enableRowBody: true,
    			showPreview: true,
    			emptyText: "<fmt:message key='aimir.extjs.empty'/>"
    		}	
    	};
    
    function addTooltip(value, metadata) {
    	if (value != null && value != "" && metadata != null) {
            //metadata.attr = 'ext:qtip="' + value + '"';
            metadata.attr = 'title="' + value + '"';
        }
        return value;
    }
    
    function addRedTooltip(value, metadata, record) {
    	if (value != null && value != "" && metadata != null) {
            //metadata.attr = 'ext:qtip="' + value + '"';
            metadata.attr = 'title="' + value + '"';

            var data = record.data;
            if(data.testResult == 0 || data.testResult == false) {
            	return "<span style='color:red'>"+value+"</span>";
            } else if(data.testResult == 1 || data.testResult==true) {
            	return "<span style='color:blue'><b>"+value+"</b></span>";
            } else {
            	return value;
            }
        }
		return value;        
    }

    var renderGrid = function() {
        zigListParams = $.extend(true, {}, defaultStoreParams, {supplierId:supplierId, zigName:$('#inputTestZigName').val()});
        assetListParams = $.extend(true, {}, defaultStoreParams, {zigName:"zigName", supplierId:supplierId});
        zigResultParams = $.extend(true, {}, {zigName:$("#inputZigName").val(), startDate:$('#startDateHidden').val(), endDate:$('#endDateHidden').val(), supplierId:supplierId, searchType : $('#searchType').val(), testResult : $('#selectTestResult').val()});
        detailResultParams = $.extend(true, {}, {zigId:'', startDate:$('#startDateHidden').val(), endDate:$('#endDateHidden').val(), supplierId:supplierId, searchType : $('#searchType').val(), testResult : $('#selectTestResult').val()});
        
    	eventHandler.initTestTab();
    };
	
    function modifyDate(setDate, inst){
        var dateId = '#' + inst.id;

        var dateHiddenId = '#' + inst.id + 'Hidden';
        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                    $(dateId).trigger('change');
                });
    }

	function addZig() {
		if(Ext.getCmp('addZigWinId') == undefined){
			var fp_Detail = new Ext.FormPanel ({
				frame:true,
				width: 300,
				bodyStyle:'padding:5px 5px 5px 5px',
				defaultType: 'textfield',
				items: [{
					 fieldLabel : "<fmt:message key='aimir.zig.name'/>",
			         xtype : 'textfield',
			         id	 : 'fp_zigName',
			         name : 'fp_zigName'},
			         {fieldLabel : "<fmt:message key='aimir.file.name'/>" ,
			          id : 'fp_fileName',
			          name : 'fp_fileName',
			          style: {
		                     border: '0px',
		                     backgroundColor:'#DFE8F6',
		                     backgroundImage:'url(../../images/blue-box.png)'
		                     },
			          readOnly:true,
			          allowBlank : true
				}],
				buttons : [
				    {text : '<font id="uploadFile"><fmt:message key="aimir.file.upload"/></font>'
					},{text : '<fmt:message key="aimir.ok"/>',
		    			handler : function() {

		    				if($("#fp_zigName").val() == "" || $("#fp_zigName").val() == undefined) {
		    					Ext.MessageBox.show({
		      	                  msg: "<fmt:message key='aimir.warn.zigName'/>",
		      	                  buttons: Ext.MessageBox.OK,
		      	                  minWidth:250,
		      	                  icon: Ext.MessageBox.WARNING
		      	              	});
		    					return;
		    				}

		    				if($("#fp_fileName").val() == "" || $("#fp_fileName").val() == undefined) {
		    					Ext.MessageBox.show({
		      	                  msg: "<fmt:message key='aimir.warn.register.notfile'/>",
		      	                  buttons: Ext.MessageBox.OK,
		      	                  minWidth:300,
		      	                  icon: Ext.MessageBox.WARNING
		      	              	});
		    					return;
		    				}

		    				$("#verify").val(true);
		    		        $.ajax({
		    		            type : "POST",
		    		            data : {
		    				    	zigName : Ext.getCmp('fp_zigName').getValue(),
		    				    	verify : $("#verify").val(),
		    				    	ext : $("#ext").val(),
		    				    	fileName : $("#saveFileName").val()
		    		            },
		    		            dataType : "json",
		    		            url : '${ctx}/gadget/test/saveZigUploadFile.do',
		    		            success : function(request, status) {
				    				$("#verify").val("false");
				    				Ext.getCmp('fp_zigName').setValue();
				                    Ext.getCmp('fp_fileName').setValue();
				                    $("#saveFileName").val('');
				                    $("#ext").val('');
				                    addZigWin.hide();
				                    
		    		            	if(request.result == 'success') {
			    		            	Ext.MessageBox.show({
				  	      	                  msg: "<fmt:message key='aimir.msg.insertsuccess'/>",
				  	      	                  buttons: Ext.MessageBox.OK,
				  	      	                  minWidth:200,
				  	      	                  fn: function() {
				  	      	                	  eventHandler.searchZigList();
				  							  },
				  	      	              	});
		    		            		eventHandler.searchZigList();
		    		            	} else {
		    		            		if(request.reason == 'duplicate') {
		    		            			Ext.MessageBox.show({
					  	      	                  msg: "<fmt:message key='aimir.duplicate.zigName'/>",
					  	      	                  buttons: Ext.MessageBox.OK,
					  	      	                  minWidth:300,
					  	      	                  icon: Ext.MessageBox.ERROR
					  	      	              	});
		    		            		} else {
		    		            			Ext.MessageBox.show({
					  	      	                  msg: "<fmt:message key='aimir.msg.insertfail'/>",
					  	      	                  buttons: Ext.MessageBox.OK,
					  	      	                  minWidth:200,
					  	      	                  icon: Ext.MessageBox.ERROR
					  	      	              	});
		    		            		}
		    		            	}
				                    
		    		            	
		    		            },
		    		            error : function(request, status) {
		    		            	Ext.getCmp('fp_fileName').setValue();
		    		            	Ext.MessageBox.show({
		  	      	                  msg: "<fmt:message key='aimir.warn.add.zig'/>",
		  	      	                  buttons: Ext.MessageBox.OK,
		  	      	                  minWidth:300,
		  	      	                  fn: function() {
		  	      	                	  eventHandler.searchZigList();
		  							  },
		  	      	                  icon: Ext.MessageBox.ERROR
		  	      	              	});
		    		            }
		    		        });
		    		        $('#fp_zigName').attr("readonly",false);
		       			}
					},{text : '<fmt:message key="aimir.cancel"/>',
		    			handler : function() {
		    				$("#verify").val("false");
		    				Ext.getCmp('fp_zigName').setValue();
		                    Ext.getCmp('fp_fileName').setValue();
		                    addZigWin.hide();
		                    $('#fp_zigName').attr("readonly",false);
		           		}
					}]
			});
			
			var uploadWin = new Ext.Window({
			    title : '',
			    id : 'addZigUploadWin',
			    applyTo : 'fileUpload',
			    autoScroll : true,
			    width : 350,
			    items : [fp_Detail],
			    closeAction : 'hide'
			    
			});
			
			addZigUpload = new AjaxUpload('uploadFile', {
			    action: '${ctx}/gadget/test/saveZigUploadFile.do',
			    data : {
			    	zigName : $('#fp_zigName').val(),
			    	verify : $("#verify").val(),
			    },
			    responseType : 'json',
			    onSubmit : function(file , ext){
			    	if($('#fp_zigName').val() == '') {
                        Ext.MessageBox.show({
	      	                  msg: "<fmt:message key='aimir.warn.zigName'/>",
	      	                  buttons: Ext.MessageBox.OK,
	      	                  minWidth:300,
	      	                  icon: Ext.MessageBox.WARNING
	      	              	});
			    		return false;
			    	}

			    	Ext.getCmp('addZigUploadWin').hide();
			    	addZigUpload._settings.data.zigName=Ext.getCmp('fp_zigName').getValue();
			    	addZigUpload._settings.data.verify=$("#verify").val();
			    	addZigUpload._settings.data.ext=ext;
                    
			    	if (!(ext && /^(xls|xlsx|XLS|XLSX)$/.test(ext))){
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not excel file');
                        Ext.MessageBox.show({
	      	                  msg: "<fmt:message key='aimir.wrong.fileType.excel'/>" + "xls, xlsx",
	      	                  buttons: Ext.MessageBox.OK,
	      	                  minWidth:300,
	      	                  fn: function() {
	      	                	  eventHandler.searchZigList();
							  },
	      	                  icon: Ext.MessageBox.ERROR
	      	              	});
                        return false;
                    }
			    },
			    onComplete : function(file, response){
			    	if(response.result == "success") {
			    		$('#fp_zigName').attr("readonly",true);
			    		Ext.getCmp('fp_fileName').setValue(response.fileName);
			    		$('#ext').val(response.ext);
			    		$('#saveFileName').val(response.saveFileName);
			    	} else {
			    		Ext.MessageBox.show({
	      	                  msg: "<fmt:message key='aimir.cannot.register'/>",
	      	                  buttons: Ext.MessageBox.OK,
	      	                  minWidth:300,
	      	                  fn: function() {
	      	                	  eventHandler.searchZigList();
							  },
	      	                  icon: Ext.MessageBox.ERROR
	      	              	});
			    	}
			    	
			    }
			});
			
			addZigWin = new Ext.Window({
				title: "<fmt:message key='aimir.zig.add'/>",
				id : 'addZigWinId',
				applyTo : 'addZigPop',
				width : 315,
				height : 400,
				shadow : false,
				autoHeight : true,
				pageX : 300,
				pageY : 130,
				resizable : false,
				plain : true,
				items : [fp_Detail],
				closeAction : 'hide',
				onHide : function() {
				}
			});
	
			addZigWin.show(this);
		} else {
			addZigWin.show(this);
		}
	}
	
	var delZig = function() {
		Ext.Msg.show({
            title: "<fmt:message key='aimir.zig.del'/>",
            msg:  "[" + $('#selectedTestZigName').val() + "] <fmt:message key='aimir.msg.deleteconfirm'/>",
            buttons: Ext.MessageBox.OKCANCEL,
            fn: function(btn, text) {
	            if(btn == 'ok') {
	        		$.ajax({
	      	          type: "post",
	      	          url : '${ctx}/gadget/test/delZigUploadFile.do',
	      	          data: {
	      	        	  zigName : $("#selectedTestZigName").val() 
	      	          },
	      	          success: function(data,status) {
	      	        	  if(data.isSuccess) {
	      	        		Ext.MessageBox.show({
	      	                  msg: "<fmt:message key='aimir.zig.del.success'/>",
	      	                  buttons: Ext.MessageBox.OK,
	      	                  minWidth:100,
	      	                  fn: function() {
	      	                	  eventHandler.searchZigList();
							  },
	      	              	});
	      	        	  } else {
	      	        		Ext.MessageBox.show({
		      	                  msg: "<fmp:message key='aimir.zig.del.fail'/>"+data.reason,
		      	                  buttons: Ext.MessageBox.OK,
		      	                  minWidth:300,
		      	                  icon: Ext.MessageBox.ERROR
		      	              });
	      	        	  }
	      	          },
	      	          error: function(request,status) {
	      	        	Ext.MessageBox.show({
	      	                  msg: "<fmp:message key='aimir.zig.del.fail'/>"+"Unknown.",
	      	                  buttons: Ext.MessageBox.OK,
	      	                  minWidth:300,
	      	                  icon: Ext.MessageBox.WARNING
	      	              });	      	        	  
	      	          }
	      	      });
	            }
            }
		});
	};
	
	function testStart() {

		//Ext.Msg.wait('Waiting for response.', 'Wait !');

		if(checkTimer != undefined && checkTimer != 'undefined' && checkTimer != '') {
	  	  	Ext.MessageBox.show({
	            msg: "아직 테스트가 끝나지 않았습니다.",
	            buttons: Ext.MessageBox.OK,
	            minWidth:400,
	            icon: Ext.MessageBox.WARNING
	        });
    	} else {
    		emergePre();
			var testReadyArray = new Array();
	
			testReadyArray.push($('#selectedTestZigName').val());
			if(testReadyArray.length < 1) {
				//Ext.Msg.hide();
				hide();
		    	Ext.MessageBox.show({
		              msg: "<fmt:message key='aimir.warn.not.testready.zig'/>",
		              buttons: Ext.MessageBox.OK,
		              minWidth:200,
		              icon: Ext.MessageBox.WARNING
		          });
		    	return false;
			}
	
			$.post("${ctx}/gadget/test/testStart.do"
			        ,{zigList : testReadyArray}
			          ,function(json) {
			          		//Ext.Msg.hide();
			          		hide();
			          		timer = setInterval(function(){stopWatch()},1000);
		    	        	Ext.MessageBox.show({
		    	                  msg: "<fmt:message key='aimir.warn.testend.start'/>",
		    	                  buttons: Ext.MessageBox.OK,
		    	                  minWidth:400,
		    	                  icon: Ext.MessageBox.WARNING,
		    	                  fn: function() {
		    	                	  eventHandler.searchZigList();
		    	                	  testZigName = testReadyArray[0];
		    	                	  checkTimer = setInterval("eventHandler.checkResult()", 1000*15*1);
		    	                  }
		    	              });
			  });
    	}
		
	}
	
	   function testEnd() {
		    clearInterval(checkTimer);
		    checkTimer = undefined;
		    stopTimer();
	        //Ext.Msg.wait('Waiting for response.', 'Wait !');
	        emergePre();

		  	var testingArray = new Array();
		  	
			var tempZigListData = zigListStore.reader.jsonData.result;
			var dataSize = zigListStore.reader.jsonData.totalCnt;
			for(var i = 0; i < dataSize; i++) {
				if(tempZigListData[i].zigName == $('#selectedTestZigName').val() && (tempZigListData[i].testYN != "테스트 준비")) {
					testingArray.push(tempZigListData[i].zigName);
				}
			}
			
			if(testingArray.length < 1) {
				//Ext.Msg.hide();
				hide();
		    	Ext.MessageBox.show({
		              msg: "<fmt:message key='aimir.notexist.testingzig'/>",
		              buttons: Ext.MessageBox.OK,
		              minWidth:200,
		              icon: Ext.MessageBox.WARNING
		          });
		    	initTimer();
		    	return false;
			}

			$.post("${ctx}/gadget/test/testEnd.do"
			        ,{zigList : testingArray}
			          ,function(json) {
			          		//Ext.Msg.hide();
			          		hide();
		    	        	Ext.MessageBox.show({
		    	                  msg: "<fmt:message key='aimir.test.end'/>",
		    	                  buttons: Ext.MessageBox.OK,
		    	                  minWidth:200,
		    	                  icon: Ext.MessageBox.WARNING,
		    	                  fn: function() {
		    	                	  initTimer();
		    	                	  eventHandler.searchZigList();
		    	                  }
		    	              });
	    	        	//TODO ZIG LIST의 'Test Result'의 상태값이 테스트 종료로 바뀌어야 함
			  });
	    }
	
	function stopWatch() {
		second--;
		if(0 > second) {
			if(0 > minute) {
				clearTimeout(timer)
			} else {
				minute--;
				second = 59;
			}
		}

		html_minute=document.getElementById('minute');
		if(0 != second) {
			if(minute < 10) {
				html_minute.innerHTML = "0"+minute;
			} else {
				html_minute.innerHTML = minute;
			}
			
		}
		html_second = document.getElementById('second');
		if(second < 10) {
			html_second.innerHTML = "0"+second;
		} else {
			html_second.innerHTML = second;			
		}
		
	}
	
	function stopTimer() {
		clearTimeout(timer);
	    timer = undefined;
	}
	
	function initTimer() {
	    minute=10;
	    second=0;
	    html_minute.innerHTML=minute;
	    html_second.innerHTML="00";
	}
	   
	function searchZig() {
   		eventHandler.searchZigResult();
	}
	
	function exportResult() {
		var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key='aimir.number'/>";
        fmtMessage[1] = "<fmt:message key='aimir.zig.name'/>";
        fmtMessage[2] = "<fmt:message key='aimir.successfail'/>";
        fmtMessage[3] = "<fmt:message key='aimir.completeDate'/>";

		var condition = new Array();
		condition[0] =$('#inputZigName').val();
		if($('#searchSelect').val() == 'all' || $('#searchSelect').val() == 'latest') {
			condition[1] = "";
			condition[2] = "";
		} else {
			condition[1] = $("#startDateHidden").val();
			condition[2] = $("#endDateHidden").val();
		}
		condition[3] = supplierId;
		condition[4] = $('#searchSelect').val();
		condition[5] = $('#selectTestResult').val();
        
		obj.url = '${ctx}/gadget/test/exportResult.do';
        obj.condition = condition;
        obj.fmtMessage = fmtMessage;

        if(exportWin)
        	exportWin.close();

        exportWin = window.open("${ctx}/gadget/test/exportExcelPopup.do",
                        "ExcelPopup", opts);
        exportWin.opener.obj = obj;
	}
	
	function exportResultDetail() {
	
        var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key='aimir.number'/>";
        fmtMessage[1] = "<fmt:message key='aimir.test.result'/>";
        fmtMessage[2] = "<fmt:message key='aimir.meterSerialNo'/>";
        fmtMessage[3] = "<fmt:message key='aimir.modemSerialNo'/>";
        fmtMessage[4] = "<fmt:message key='aimir.hwver'/>";
        fmtMessage[5] = "<fmt:message key='aimir.swver'/>";
        fmtMessage[6] = "<fmt:message key='aimir.swBuild'/>";
        fmtMessage[7] = "<fmt:message key='aimir.report.mgmt.failreason'/>";
        fmtMessage[8] = "<fmt:message key='aimir.detail.completeDate'/>";
        fmtMessage[9] = "<fmt:message key='aimir.success'/>";
        fmtMessage[10] = "<fmt:message key='aimir.fail'/>";

		var condition = new Array();
		condition[0] = detailResultParams.zigId;
		condition[1] = supplierId;
		if($('#searchSelect').val() == 'all' || $('#searchSelect').val() == 'latest') {
			condition[2] = "";
			condition[3] = "";
		} else {
			condition[2] = $("#startDateHidden").val();
			condition[3] = $("#endDateHidden").val();
		}
        
		condition[4] = $("#searchSelect").val();
		condition[5] = $('#selectTestResult').val();
		
		obj.url = '${ctx}/gadget/test/exportResultDetail.do';
        obj.condition = condition;
        obj.fmtMessage = fmtMessage;

        if(exportDetailWin)
        	exportDetailWin.close();

        exportDetailWin = window.open("${ctx}/gadget/test/exportExcelPopup.do",
                        "ExcelPopup", opts);
        exportDetailWin.opener.obj = obj;
	}

    var eventHandler = {
		initTestTab: function() {
		    eventHandler.searchZigList();
		},
		searchZigList: function() {
			zigListParams.zigName = $('#inputTestZigName').val();
			
	        zigListStore = new Ext.data.JsonStore({
	        	autoLoad : defaultStoreParams,
	        	baseParams : zigListParams,
	        	url: "${ctx}/gadget/test/zigList.do",	
	        	totalProperty: 'totalCnt',
	        	root: 'result',
	        	fields: ['zigName', 'testYN'],
	        	listeners: {
	        		load: function(store, record, options){
	        			var zigList = store.reader.jsonData.result;
	        			if(zigList.length > 0) {
	        				var firstZigData = zigList[0];
	        				$('#selectedTestZigName').val(firstZigData.zigName);
		                    eventHandler.createAssetList(firstZigData.zigName);
	        			} else {
	        				$('#selectedTestZigName').val(-1);
	        				eventHandler.createAssetList(-1);
	        			}
	                },
	        	}
	        });
	        
	        var zigListModel = new Ext.grid.ColumnModel({
	        	columns: [
    	    		{header: "<fmt:message key='aimir.zig.name'/>", tooltip:"<fmt:message key='aimir.testYN'/>", dataIndex: 'zigName'},
    	    		{header: "<fmt:message key='aimir.testYN'/>", tooltip:"<fmt:message key='aimir.testYN'/>", dataIndex: 'testYN'}
    	    	],
    	    	defaults : {
    	    		sortable: true,
    	    		menuDisable: true,
    	    		renderer: addTooltip
    	    	}
	        });
	        $('#zigList').empty();

            zigListGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : zigListWidth,
                height : 376,
                store : zigListStore,
                colModel : zigListModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            $('#selectedTestZigName').val(param.zigName);
                            eventHandler.createAssetList(param.zigName);
                        }
                    }
                }),
                //clicksToEdit : 1,
                autoScroll: false,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'zigList',
                viewConfig : {
        			forceFit: true,
	    			//scrollOffset: 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : "<fmt:message key='aimir.extjs.empty'/>"
                }
                ,bbar : new Ext.PagingToolbar({
                    pageSize : DEFAULT_SIZE,
                    store : zigListStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
		},
		createAssetList: function(zigName) {
	    	assetListParams.zigName = zigName;

	        assetListStore = new Ext.data.JsonStore({
	        	autoLoad : defaultStoreParams,
	        	baseParams : assetListParams,
	        	url: "${ctx}/gadget/test/assetList.do",	
	        	totalProperty: 'totalCnt',
	        	root: 'result',
	        	fields: ['no', 'zigName', 'meterSerial', 'modemSerial']
	        });

	        var assetListModel = new Ext.grid.ColumnModel({
	        	columns: [
    	    		{header: "<fmt:message key='aimir.number'/>", tooltip:"<fmt:message key='aimir.number'/>", dataIndex: 'no', width: 30},
    	    		{header: "<fmt:message key='aimir.zig.name'/>", tooltip:"<fmt:message key='aimir.zig.name'/>", dataIndex: 'zigName'},
    	    		{header: "<fmt:message key='aimir.meterSerialNo'/>", tooltip:"<fmt:message key='aimir.meterSerialNo'/>", dataIndex: 'meterSerial'},
    	    		{header: "<fmt:message key='aimir.modemSerialNo'/>", tooltip:"<fmt:message key='aimir.modemSerialNo'/>", dataIndex: 'modemSerial'}
    	    	],
    	    	defaults : {
    	    		sortable: true,
    	    		menuDisable: true,
    	    	    renderer: addTooltip
    	    	}
	        });
	        $('#assetList').empty();
            assetListGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : assetListWidth,
                height : 376,
                store : assetListStore,
                colModel : assetListModel,
                clicksToEdit : 1,
                autoScroll: false,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'assetList',
                viewConfig : {
        			forceFit: true,
	    			scrollOffset: 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : "<fmt:message key='aimir.extjs.empty'/>"
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : DEFAULT_SIZE,
                    store : assetListStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
		},
		initResultTab: function() {
		    eventHandler.searchZigResult();

		    $("#searchSelect option[value='latest']").attr('selected', 'selected');
		},
		searchZigResult: function() {
			zigResultParams.zigName = $("#inputZigName").val();
			if($('#searchSelect').val() == 'all' || $('#searchSelect').val() == 'latest') {
				zigResultParams.startDate = "";
				zigResultParams.endDate = "";
			} else {
				zigResultParams.startDate = $("#startDateHidden").val();
				zigResultParams.endDate = $("#endDateHidden").val();
			}
			zigResultParams.searchType = $('#searchSelect').val();
			zigResultParams.testResult = $('#selectTestResult').val();

			$('#zigResult').empty();

	        zigResultStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: DEFAULT_SIZE}},
	        	baseParams : zigResultParams,
	        	url: "${ctx}/gadget/test/zigResult.do",	
	        	totalProperty: 'totalCount',
	        	root: 'result',
	        	fields: ['zigId', 'zigName', 'completeDate', 'resultCnt'],
	        	getTotalCount: function () {
	                return this.totalLength || 0;
	        	},
	            loadData : function(o, append){
	                var r = this.reader.readRecords(o);
	                this.loadRecords(r, {add: append}, true);
	            },
	            loadRecords : function(o, options, success){
	                var i, len;

	                if (this.isDestroyed === true) {
	                    return;
	                }
	                if(!o || success === false){
	                    if(success !== false){
	                        this.fireEvent('load', this, [], options);
	                    }
	                    if(options.callback){
	                        options.callback.call(options.scope || this, [], options, false, o);
	                    }
	                    return;
	                }
	                var r = o.records, t = o.totalRecords || r.length;
	                if(!options || options.add !== true){
	                    if(this.pruneModifiedRecords){
	                        this.modified = [];
	                    }
	                    for(i = 0, len = r.length; i < len; i++){
	                        r[i].join(this);
	                    }
	                    if(this.snapshot){
	                        this.data = this.snapshot;
	                        delete this.snapshot;
	                    }
	                    this.clearData();
	                    this.data.addAll(r);
	                    this.totalLength = t;
	                    this.applySort();
	                    this.fireEvent('datachanged', this);
	                }else{
	                    var toAdd = [],
	                        rec,
	                        cnt = 0;
	                    for(i = 0, len = r.length; i < len; ++i){
	                        rec = r[i];
	                        if(this.indexOfId(rec.id) > -1){
	                            this.doUpdate(rec);
	                        }else{
	                            toAdd.push(rec);
	                            ++cnt;
	                        }
	                    }
	                    this.totalLength = Math.max(t, this.data.length + cnt);
	                    this.add(toAdd);
	                }
	                this.fireEvent('load', this, r, options);
	                if(options.callback){
	                    options.callback.call(options.scope || this, r, options, true);
	                }
	            },
	        	listeners: {
	        		beforeload: function(store, options) {
	                    Ext.apply(options.params, {
	                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) -1
	                    });
	        		},
		    		load: function(store, record, options){
		    			var zigResult = store.reader.jsonData.result;
		    			if(zigResult.length > 0) {
		    				var firstZigResult = zigResult[0];
			                eventHandler.searchDetailResult(firstZigResult.zigId);
		    			} else {
		    				eventHandler.searchDetailResult(-1);
		    			}
		    			
		            }
	        	}
	        });

	        var zigResultModel = new Ext.grid.ColumnModel({
	        	columns: [
	        		{header: "<fmt:message key='aimir.zig.name'/>", tooltip:"<fmt:message key='aimir.zig.name'/>", dataIndex: 'zigName'},
	        		{header: "<fmt:message key='aimir.successfail'/>", tooltip:"<fmt:message key='aimir.successfail'/>", dataIndex: 'resultCnt'},
	        		{header: "<fmt:message key='aimir.completeDate'/>", tooltip:"<fmt:message key='aimir.completeDate'/>", dataIndex: 'completeDate'}
	        	],
	        	defaults : {
	        		sortable: true,
	        		menuDisable: true,
    	    	    renderer: addTooltip
	        	}
	        });
	        

            zigResultGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : zigResultWidth,
                height : 376,
                store : zigResultStore,
                colModel : zigResultModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            eventHandler.searchDetailResult(param.zigId);
                        }
                    }
                }),
                //clicksToEdit : 1,
                autoScroll: false,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'zigResult',
                viewConfig : {
        			forceFit: true,
	    			scrollOffset: 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : "<fmt:message key='aimir.extjs.empty'/>"
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : DEFAULT_SIZE,
                    store : zigResultStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });

		},
		searchDetailResult: function (zigId) {
			detailResultParams.zigId = zigId;

			if($('#searchSelect').val() == 'all' || $('#searchSelect').val() == 'latest') {
				detailResultParams.startDate = "";
				detailResultParams.endDate = "";
			} else {
				detailResultParams.startDate = $("#startDateHidden").val();
				detailResultParams.endDate = $("#endDateHidden").val();
			}
			
			detailResultParams.searchType = $('#searchSelect').val();
			detailResultParams.testResult = $('#selectTestResult').val();

			$('#detailResult').empty();

			detailResultStore = new Ext.data.JsonStore({
				autoLoad : {params:{start: 0, limit: DEFAULT_SIZE}},
	        	baseParams : detailResultParams,
	        	url: "${ctx}/gadget/test/detailResult.do",
	        	totalProperty: 'totalCount',
	            idProperty : 'no',
	        	root: 'result',
	        	getTotalCount: function () {
	                return this.totalLength || 0;
	        	},
	            loadData : function(o, append){
	                var r = this.reader.readRecords(o);
	                this.loadRecords(r, {add: append}, true);
	            },
	            loadRecords : function(o, options, success){
	                var i, len;

	                if (this.isDestroyed === true) {
	                    return;
	                }
	                if(!o || success === false){
	                    if(success !== false){
	                        this.fireEvent('load', this, [], options);
	                    }
	                    if(options.callback){
	                        options.callback.call(options.scope || this, [], options, false, o);
	                    }
	                    return;
	                }
	                var r = o.records, t = o.totalRecords || r.length;
	                if(!options || options.add !== true){
	                    if(this.pruneModifiedRecords){
	                        this.modified = [];
	                    }
	                    for(i = 0, len = r.length; i < len; i++){
	                        r[i].join(this);
	                    }
	                    if(this.snapshot){
	                        this.data = this.snapshot;
	                        delete this.snapshot;
	                    }
	                    this.clearData();
	                    this.data.addAll(r);
	                    this.totalLength = t;
	                    this.applySort();
	                    this.fireEvent('datachanged', this);
	                }else{
	                    var toAdd = [],
	                        rec,
	                        cnt = 0;
	                    for(i = 0, len = r.length; i < len; ++i){
	                        rec = r[i];
	                        if(this.indexOfId(rec.id) > -1){
	                            this.doUpdate(rec);
	                        }else{
	                            toAdd.push(rec);
	                            ++cnt;
	                        }
	                    }
	                    this.totalLength = Math.max(t, this.data.length + cnt);
	                    this.add(toAdd);
	                }
	                this.fireEvent('load', this, r, options);
	                if(options.callback){
	                    options.callback.call(options.scope || this, r, options, true);
	                }
	            },
	        	fields: ['no', 'testResult', 'meterSerial', 'modemSerial', 'hwVer', 'swVer', 'swBuild', 'failReason', 'completeDate'],
	        	listeners: {
	        		beforeload: function(store, options) {
	                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) -1 
                   	 	});
	        		}
	        	}
	        });
	        var detailResultModel = new Ext.grid.ColumnModel({
	        	columns: [
	        		{header: "<fmt:message key='aimir.number'/>", tooltip:"<fmt:message key='aimir.number'/>", dataIndex: 'no', width:5},
	        		{header: "<fmt:message key='aimir.detail.completeDate'/>", tooltip:"<fmt:message key='aimir.detail.completeDate'/>", dataIndex: 'completeDate', width:20},
	        		{header: "<fmt:message key='aimir.test.result'/>", tooltip:"<fmt:message key='aimir.test.result'/>", width:8,  dataIndex: 'testResult', 
	        			renderer : function(value, metadata) {
	        				if(value == 1 || value == true){
	        					metadata.attr = 'title="' + value + '"';
	        					return "<span style='color:blue;'><b><fmt:message key='aimir.success'/></b></span>";
	        				} else if(value == 0 || value == false) {
	        					metadata.attr = "title='<fmt:message key='aimir.fail'/>'";
	        					return "<span style='color:red;'><fmt:message key='aimir.fail'/></span>";
	        				} else {
	        					return '';
	        				}
	        			}
	        		},
	        		{header: "<fmt:message key='aimir.meterSerialNo'/>", tooltip:"<fmt:message key='aimir.meterSerialNo'/>", dataIndex: 'meterSerial', width:19},
	        		{header: "<fmt:message key='aimir.modemSerialNo'/>", tooltip:"<fmt:message key='aimir.modemSerialNo'/>", dataIndex: 'modemSerial', width:20},
	        		{header: "<fmt:message key='aimir.hwver'/>", tooltip:"<fmt:message key='aimir.hwver'/>", dataIndex: 'hwVer', width:5},
	        		{header: "<fmt:message key='aimir.swver'/>", tooltip:"<fmt:message key='aimir.swver'/>", dataIndex: 'swVer', width:5},
	        		{header: "<fmt:message key='aimir.swBuild'/>", tooltip:"<fmt:message key='aimir.swBuild'/>", dataIndex: 'swBuild', width:5},
	        		{header: "<fmt:message key='aimir.report.mgmt.failreason'/>", tooltip:"<fmt:message key='aimir.report.mgmt.failreason'/>", dataIndex: 'failReason', width:13}
	        	],
	        	defaults : {
	        		sortable: true,
	        		menuDisable: true,
    	    	    renderer: addRedTooltip
	        	}
	        });

            detailResultGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : detailResultWidth,
                height : 376,
                store : detailResultStore,
                colModel : detailResultModel,
                clicksToEdit : 1,
                autoScroll: false,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'detailResult',
                viewConfig : {
        			forceFit: true,
	    			scrollOffset: 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : "<fmt:message key='aimir.extjs.empty'/>"
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : DEFAULT_SIZE,
                    store : detailResultStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            eventHandler.getSummaryCount();
		}, 
		getSummaryCount : function() {
			$.ajax({
   	          type: "post",
   	          url : '${ctx}/gadget/test/getSummaryInfo.do',
   	          data: {
   	        	  'zigId' : detailResultParams.zigId,
   	              'startDate' : detailResultParams.startDate,
   	              'endDate' : detailResultParams.endDate,
   	              'searchType' : detailResultParams.searchType,
   	              'testResult' : detailResultParams.testResult
   	          },
   	          success: function (data,status) {
   	        	 var html_summary=document.getElementById('lastResult');	
   	        	 var summary = data.summary;
   	        	 html_summary.innerHTML = summary.totalSuccess + "/" + summary.totalFail + "(성공/실패)";
   	          }
			});
			
		},
		checkResult : function() {
			$.ajax({
     	          type: "post",
     	          url : '${ctx}/gadget/test/checkResult.do',
     	          data: {'zigName' : testZigName},
     	          success: function (data,status) {
     	        	  var checkResultList = data.checkData;
     	        	  if(checkResultList.length < 1) {
     	        		 clearInterval(checkTimer);
     	        		 checkTimer = undefined;
     	        		 stopTimer();
     	        		 if(testZigName == 1) {
     	        			createSound(); 
     	        		 } else {
     	        			createSound2();
     	        		 }
     	        		 
     	        		 Ext.MessageBox.show({
	     	                  msg: "<fmt:message key='aimir.zigtest.complete'/>".replace('$ZIGNAME', testZigName),
	     	                  buttons: Ext.MessageBox.OK,
	     	                  minWidth:300,
	     	                  fn: function() {
	     	                	  initTimer();
	     	                	  eventHandler.searchZigResult();
	     	                  }
	     	              });
     	        	  } else {
	     	        	  for (var i = 0; i < checkResultList.length; i++) {
	     	        		 var detailData = checkResultList[i];
	     	        		 if(detailData.nullCnt < 1) {
	     	        			clearInterval(checkTimer);
	     	        			checkTimer = undefined;
	     	        			stopTimer();
	        	        		 if(testZigName == 1) {
	          	        			createSound(); 
	          	        		 } else {
	          	        			createSound2();
	          	        		 }
	     	        			Ext.MessageBox.show({
	     	     	                  msg: "<fmt:message key='aimir.zigtest.complete'/>".replace('$ZIGNAME', testZigName),
	     	     	                  buttons: Ext.MessageBox.OK,
	     	     	                  minWidth:300,
	     	     	                  fn: function() {
	     	     	                	initTimer();
	     	     	                	eventHandler.searchZigResult();
	     	     	                  }
	     	     	              });

	     	        		 } else if(detailData.timeout == true) {
	     	        			clearInterval(checkTimer);
	     	        			checkTimer = undefined;
	     	        			stopTimer();
	     	        			html_minute.innerHTML = 10;
	     	        			html_second.innerHTML = "0"+0;
	        	        		 if(testZigName == 1) {
	          	        			createSound(); 
	          	        		 } else {
	          	        			createSound2();
	          	        		 }
	     	        			Ext.MessageBox.show({
	     	     	                  msg: "<fmt:message key='aimir.timeout.complete'/>".replace('$ZIGNAME', testZigName),
	     	     	                  buttons: Ext.MessageBox.OK,
	     	     	                  minWidth:300,
	     	     	                  icon: Ext.MessageBox.WARNING,
	     	     	                  fn: function() {
	     	     	                	  //null인 미터들의 fail로 저장하고 이유를 can not communication 로 바꿈
	     	     	                	  $.ajax({
	     	     	                		  type: "post",
	     	     	                		  url : '${ctx}/gadget/test/changeNullResult.do',
	     	     	                		  data: {
	     	     	                			  'zigId' : detailData.zigId,
	     	     	                			  'testStartDate' : detailData.testStartDate
	     	     	                			  },
	     	     	                		  success: function (data, status) {
	     	     	                			initTimer();
	     	     	                			eventHandler.searchZigResult();
	     	     	                		  }
	     	     	                	  })
	     	     	                  }
	     	     	              });
	     	        		 } else {
	     	        			eventHandler.searchZigResult();
	     	        		 }
						  }
     	        	  }
     	          }
     	      });
		}
    };
    
    function changeSelect() {
    	if($('#searchSelect').val() == 'latest') {
    		$('#completeDate').attr('style','display: none;')
    	} else if($('#searchSelect').val() == 'all') {
    		$('#completeDate').attr('style','display: none;')
    	} else {
    		$('#completeDate').attr('style','display: inherit;')
    	}
    }
    
    function createSound() {
    	soundManager.createSound({
    		id : 'alarm',
    		url: '${ctx}/sound/alarm_moeTest.mp3'
    	});
    	soundManager.play('alarm');
    }
    
    function createSound2() {
    	soundManager.createSound({
    		id : 'alarm',
    		url: '${ctx}/sound/alarm_moeTest2.wav'
    	});
    	soundManager.play('alarm');
    }
    
	$(function() {
        var locDateFormat = "yymmdd";
		$("#startDate").datepicker({showOn: 'both', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
		$("#endDate").datepicker({showOn: 'both', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
		$("#startDate").datepicker("setDate", new Date());
		$("#endDate").datepicker("setDate",  new Date());
		modifyDate($("#startDate").val(), $("#startDate")[0]);
		modifyDate($("#endDate").val(), $("#endDate")[0]);

		zigListWidth = $('#zigList').width();
		assetListWidth = $('#assetList').width();
		zigResultWidth = $('#zigResult').width();
		detailResultWidth = $('#detailResult').width();
		renderGrid();

	    $('#_test').bind('click', eventHandler.initTestTab);
	    $('#_result').bind('click', eventHandler.initResultTab);
	    $("#plcTestTab").subtabs();
	    
	    $('#searchSelect').selectbox();
	    $('#selectTestResult').selectbox();
	    
	    soundManager.setupOptions.useConsole=false;
	    
		hide();		
	});
</script>	
</body>
</html>