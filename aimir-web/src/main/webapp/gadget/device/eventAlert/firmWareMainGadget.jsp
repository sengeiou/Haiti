<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", -1); //prevents caching at the proxy
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AIMIR Firmware</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tabs.css" rel="stylesheet" type="text/css" 	media="print, projection, screen"></link>
<link href="${ctx}/css/style_fw.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<!--
    <script type="text/javascript" src="${ctx}/js/multipart/jquery.MetaData.js"></script>
    <script type="text/javascript" src="${ctx}/js/multipart/jquery.MultiFile.js"></script>
    <script type="text/javascript" src="${ctx}/js/multipart/jquery.blockUI.js"></script>
 -->


<script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>

<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.checkbox.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.contextmenu.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.cookie.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.hotkeys.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.metadata.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.themeroller.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.xml_flat.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.xml_nested.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript" src="${ctx}/js/autocomplete/jquery.autocomplete.js"></script>


<style>
#colLine {
 float:left;
 border-top:1px solid #cccccc;
 border-left:1px solid #cccccc;
}

#colLine .ddiv
{
 clear:both;
}

#colLine .ddiv div {
 float:left;
 border-right:1px solid #cccccc;
 border-bottom:1px solid #cccccc;
 width:100px;
}

#colLine .ddiv
{
 clear:both;
}

#colLine .ddiv div {
 float:left;
 border-right:1px solid #cccccc;
 border-bottom:1px solid #cccccc;
 width:100px;
}

#colLine .ddiv
{
 clear:both;
}

#colLine .ddiv div {
 float:left;
 border-right:1px solid #cccccc;
 border-bottom:1px solid #cccccc;
 width:100px;
}

#colLine .ddiv
{
 clear:both;
}

#colLine .ddiv div {
 float:left;
 border-right:1px solid #cccccc;
 border-bottom:1px solid #cccccc;
 width:100px;
}


</style>

<script type="text/javascript">/*<![CDATA[*/

	var supplierId ;
	var channelCount;
	var auto;
	var deviceTreeId;
		$(function(){
            
		    init();

		    //초기화면 세팅  
			function init() {
	            $('#container-3').tabs();
	            $('#container-3').show('fast', vendorTabListener());
	            $('#container-4').hide();
	            $('#container-5').hide();

	            //$(":input:radio").filter("input[value='M']").attr("checked", "checked");
	            initChannel();
			}

			//초기화면 세팅 
			function initChannel() {
	            for(var i=0;i<8;i++){
	            	$("#channel-"+i).hide();
	            }
			}

			//SupplierList 가지고옮
            $.getJSON('${ctx}/gadget/system/getSupplierList.do', function(json) {
	            	if(json.supplier != null){
	    				supplierId = json.supplier.id;
	    				//$('#supplierName').text(json.supplier.name);
	    				//$('#supplierList').hide();
	    				treeTypeView();//viewTree();
	    				return;
	    	    	}

	    			if(json.supplierList != null){
	    			   $('#supplierList').loadSelect(json.supplierList);
	    			}
               }
            );
		    
            //Tree세팅 
            function treeTypeView(){
            	 viewDeviceTree();
            }

            //모델에 대한 Tree세팅 
            function viewDeviceTree() {
        		auto='';
        		$('#searchWord').unautocomplete();
        		$('#basic_html').tree( {
        			data : {
        				type : 'json',
        				opts : {
        					method : 'GET',
        					url : '${ctx}/gadget/device/firmwaretree.do'
        				}
        			},
        			types	: {
        				"default" : {
        					deletable : false,
        					renameable : true,
        					icon : {
        					   image : '${ctx}/js/tree/themes/default/vendor.gif'

        					}
        				},
        				"device" : {

        					icon : {
        					   image : '${ctx}/js/tree/themes/default/device.gif'

        					}
        				},
        				"vendor" : {

        					icon : {
        					   image : '${ctx}/js/tree/themes/default/vendor.gif'

        					}
        				},

        				"model" : {
        					icon : {
        					   image : '${ctx}/js/tree/themes/default/model.gif'

        					}
        				},
        				"config" : {
        					icon : {
        					   image : '${ctx}/js/tree/themes/default/config.gif'

        					}
        				}
        		    },	
        			callback : {

        				'onload' : function(t) {

        				    t.settings.data.opts.static = false;
        				},

        				'beforedata' : function(n, t) {
        					return {
        						supplierId : supplierId
        					};
        				},
        				'ondata' : function(json) {
        					if (json == false) {
        						return;
        					}
     
        					if (json.data)
        						return json; //create()함수호출시 callback으로 ondata이 호출되어 충돌을 방지하는 코드

        					var data = [];
        	    			var jsonData = json.jsonTrees;
        	    			///alert("tree="+jsonData);
        	    			
        	    			for ( var i in jsonData) {

        	    				var device = jsonData[i].data;
        	    				var deviceTypes = jsonData[i].children;
        	    				var vendors = jsonData[i].children1;
        	    				var models = jsonData[i].children2;
        	    				var configs = jsonData[i].children3;

        	    				var children = [];

        	    				for ( var j in deviceTypes) {

        	    					var deviceType = deviceTypes[j];
        	    					var children1 = [];
        	    					for(var k in vendors[j]){

        	    						var vendor = vendors[j][k];

        		    					var children2 = [];
        		    					for(var l in models[j][k]){
        		    						var model = models[j][k][l];
        			    					var children3 = [];

        		    						/*if(configs[j][k][l]){
        				    					for(var m in configs[j][k][l]){
        				    						var config = configs[j][k][l][m];

        				    						auto= auto+'^'+config.name;
        		            						children3.push( {
        		        	    						'data' : {
        		        	    							'title' : config.name,
        		        	    							attributes : {
        		        	    								'href' : '#'
        		        	    							}
        		        	    						},
        		        	    						'attributes' : {
        		        	    							'id' : 'config_' + config.id,'rel':'config'
        		        	    						}

        		        	    					});

        					    				}
        		    						}*/
        		    						if(device.name != "MCU" ){
        			    					auto= auto+'^'+model.name;
	                    						children2.push( {
	                	    						'data' : {
	                	    							'title' : model.name,
	                	    							attributes : {
	                	    								'href' : '#'
	                	    							}
	                	    						},
	                	    						'attributes' : {
	                	    							'id' : 'model_' + model.id+'_'+ vendor.id,'rel':'model'
	                	    						},
	                	    						'children' : children3
	                	    					});
        		    						}
        			    				}
        			    				//vender 추가 
        			    				/////////////////////////////////////////////////////////////////
        			    				// 하단 MCU부분은 프로퍼티 또는 데이터 베이스에서 가지고오는 방식으로 변경 필요.//
        			    				/////////////////////////////////////////////////////////////////
                                        if(device.name != "MCU"){
            		    					auto= auto+'^'+vendor.name;
            		    					children1.push( {
                	    						'data' : {
                	    							'title' : vendor.name,
                	    							attributes : {
                	    								'href' : '#'
                	    							}
                	    						},
                	    						'attributes' : {
                	    							'id' : 'vendor_' + vendor.id+'_'+ deviceType.id,'rel':'vendor'
                	    						},
                	    						'children' : children2
                	    					});
                                        }
        		    				}
        	    					auto= auto+'^'+deviceType.name;
        	    					//alert(auto);
        	    					children.push( {
        	    						'data' : {
        	    							'title' : deviceType.name,
        	    							attributes : {
        	    								'href' : '#'
        	    							}
        	    						},
        	    						'attributes' : {
        	    							'id' : 'deviceType_' + deviceType.id+'_'+device.name,'rel':'model'
        	    						},
        	    						'children' : children1
        	    					});
        		    			}
        		    			if(device.name != "Modem"){
            	    				auto= auto+'^'+device.name;
            	    				data.push( {
            	    					'data' : {
            	    						'title' : device.name,
            	    						'attributes' : {
            	    							'href' : '#'
            	    						}
            	    					},
            	    					'attributes' : {
            	    						'id' : 'device_' + device.id,'rel':'device'
            	    					},
            	    					'children' : children
            	    				});
            		    		}
        	    			}

        	    			var autoArr = auto.split('^');
        	                $('#searchWord').autocomplete(autoArr);
        	                //setSearchWord();
        	    			return data;

        		        },

                        'onselect' : function(n, t) {
                        	var node = $(n).attr('id');
							//alert(node);
                        	if (node.indexOf('vendor_') > -1) {
                            	var v = node.replace(/^vendor_/, '');
                            	deviceTreeId = v;
                            	if(v.indexOf('_') > -1){
	                            	viewDeviceVendorTab(v.substring(0,v.indexOf("_")));
                            	}else{
                            		viewDeviceVendorTab(v);
                            	}
                            } else if (node.indexOf('model_') > -1){
                            	var m = node.replace(/^model_/, '');
                            	deviceTreeId = m;
                            	if(m.indexOf('_') > -1){
	                            	viewDeviceModelTab(m.substring(0,m.indexOf("_")));
                            	}else{
                            		viewDeviceModelTab(m);
                            	}
                            } else if (node.indexOf('config_') > -1){
                            	viewDeviceConfigTab(node.replace(/^config_/, ''));
                            } else if (node.indexOf('deviceType_') > -1 && node.indexOf('MCU') ){
                            	var v = node.replace(/^deviceType_/, '');
                            	var vv = v.replace(/_MCU/, '');
                            	detailViewFirmWare(vv);
                            } else {
                                //alert('incorrect data');
                            }
                        }
                    }

                 });
    		}
		}); 
		//End $(function(){ });

        // vendor list reference data
        function getReferenceVendors(vendor) {
             $.getJSON('${ctx}/gadget/system/vendorlist.do', {supplierId:supplierId},function(data) {
                       $('#deviceVendor').loadSelect(data.deviceVendors);
                       if(vendor){
                    	   $('#deviceVendor').val(vendor);
                          }
                    }
            );
        }

        // deviceType reference data
        function getReferenceTypes(deviceType) {
            $.getJSON('${ctx}/gadget/system/vendormodeltypelist.do',function(json) {
            		$('#deviceType').loadSelect(json.deviceType);
            		if(deviceType)
                 	   $('#deviceType').val(deviceType);
                   }

                );
        }

		// vendor tab active
		function vendorTabListener() {
		    var devicevendorId = $("#vendorForm :hidden[name='id']").val();

		    if (devicevendorId) {
                $('#fragment-1 #addbtn1').hide();
                $('#fragment-1 #updatebtn1').show();
                $('#fragment-1 #deletebtn1').show();
		    } else {
                $('#fragment-1 #addbtn1').show();
                $('#fragment-1 #updatebtn1').hide();
                $('#fragment-1 #deletebtn1').hide();
		    }
		}

		// model tab active
		function modelTabListener() {
		    var devicemodelId = $("#modelForm :hidden[name='id']").val();
		    //alert(devicemodelId);
		    if (devicemodelId) {
                $('#fragment-2 #addbtn2').hide();
                $('#fragment-2 #updatebtn2').show();
                $('#fragment-2 #deletebtn2').show();
		    } else {
                $('#fragment-2 #addbtn2').show();
                $('#fragment-2 #updatebtn2').hide();
                $('#fragment-2 #deletebtn2').hide();
		    }

		}

		// vendor click eventHandler
	      function viewDeviceVendorTab(devicevendorId) {}

	      // model click eventHandler
	      function viewDeviceModelTab(devicemodelId) {}

	      //click eventHandler
	      function detailViewFirmWare(deviceId) {
		      //alert(devicemodelId);
	          resetListFirmwreForm();
              
	          $.getJSON('${ctx}/gadget/device/detailViewFirmware.do', {deviceId:deviceId},
	                function(json) {

	        	  			
			        	  var fwMCUDetailList = json.firmWareMCUDetailList;
			        	  var tmpColStr = "";
			        	  var colCnt = 1;
			        	  var tmpRowStr = "";
			        	  var rowCnt = 1;
			        	  var arrCol = new Array();
			        	  var arrRow = new Array();
			        	  
			        	  //alert(fwMCUDetailList.length);
			        	  if(fwMCUDetailList.length > 0 ){

				        	  for(i=0 ; i < fwMCUDetailList.length ; i++){
				        		  if(i == 0){
				        			  tmpColStr = fwMCUDetailList[i].sysHwVersion;
				        			  arrCol[i]    = fwMCUDetailList[i].sysHwVersion;
					        		  tmpRowStr = fwMCUDetailList[i].sysSwVersion;
					        		  arrRow[i]    = fwMCUDetailList[i].sysSwVersion;
					        	  }
					        	  //alert(tmpColStr +"="+ fwMCUDetailList[i].sysHwVersion);
				        		  if( tmpColStr != fwMCUDetailList[i].sysHwVersion ){
				        			  colCnt++;
				        			  tmpColStr = fwMCUDetailList[i].sysHwVersion;
				        			  arrCol[i] = fwMCUDetailList[i].sysHwVersion;
				        			  //alert(arrCol[i]);
					        	  }
				        		  if( tmpRowStr != fwMCUDetailList[i].sysSwVersion ){
				        			  rowCnt++;
				        			  tmpRowStr = fwMCUDetailList[i].sysSwVersion;
				        			  arrRow[i] = fwMCUDetailList[i].sysSwVersion;
					        	  }					        	  
						      }

						      makeTable(colCnt,rowCnt,arrCol,arrRow);
						      
			        		  bindingListFirmWare(fwMCUDetailList);
				        	  
						  }else{
							Ext.Msg.alert('<fmt:message key='aimir.message'/>',"조회된 데이터가 없습니다.");
						  }
	          });
		  }
	      
		  // form reset
		 function resetListFirmwreForm() {
		 	$("#firmWareListForm :hidden[name='id']").val('');
		   	$("#firmWareListForm :input[name='name']").val('');
	        $('#firmWareListForm').resetForm();
		 }	  

	      function  makeTable(colCnt,rowCnt,arrCol,arrRow){
		      //alert("colCnt= "+colCnt + " rowCnt= "+rowCnt);
		      //rowCnt=0;
		     //var formInt = 0;
		      var colInt = 0;
		      var rowInt = 0;
		      
		      if(colCnt > 1 || rowCnt > 1){//row , col의 변화가 필요할 경우.

		    	 // formInt++;
		    	  //+ formInt +"_"+arrCol[0]+"_"+ arrRow[0] +"
		    	  var setinnerHtml = "";
		    	    setinnerHtml += ""
					+" <div class=\"ddiv\"> " 
					+"   <div>   F/\H  </div> "
					for(i=0 ; i  < colCnt ; i++){
					setinnerHtml += ""		
					+"   <div>   "+arrCol[i]+"   </div> ";
					}
					setinnerHtml += ""
					+"	 <div>  HW Unknown  </div> "
					+" </div> " ;

				  var addInnerHtml = "";
					for(r=0 ; r < rowCnt ; r++){
					//formInt++;
					addInnerHtml += ""
					+" <div class=\"ddiv\"> " 
					+"   <div>   "+arrRow[r]+"  </div> ";
					for(j=0 ; j  < colCnt ; j++){
					addInnerHtml += ""							
					+"   <div id=\""+replaceStr(arrRow[r])+"_"+replaceStr(arrCol[j])+"\"></div> ";
					}
					addInnerHtml += ""
					+"	 <div id=\""+replaceStr(arrRow[r])+"_unKnown\" ></div> "
					+" </div> ";
					}			
					//alert(addInnerHtml);							
	                //formInt++;	
				  var endInnerHtml = "";
				    endInnerHtml += ""
					+" <div class=\"ddiv\"> " 
					+"   <div>  FW Unknown </div> ";
					for(k=0 ; k< colCnt ; k++){
					endInnerHtml += ""							
					+"   <div id=\"unKnown"+"_"+replaceStr(arrCol[k])+"\">  </div> ";
					}
					endInnerHtml += ""
					+"	 <div>   </div> "
					+" </div> " ;
	                //alert(formInt);
	                //alert(setinnerHtml+addInnerHtml+endInnerHtml);
		          $('#colLine').html(setinnerHtml+addInnerHtml+endInnerHtml);
			  }
		  }

	      // model data setting
	      function bindingListFirmWare(fwMCUDetailList) {
		      var innerHtml = "";
        	  for(i=0 ; i < fwMCUDetailList.length ; i++){
    		    var fw = replaceStr(fwMCUDetailList[i].sysSwVersion);
        		var hw = replaceStr(fwMCUDetailList[i].sysHwVersion);
        		if(fw == "null") fw = "unKnwon";
        		if(hw == "null") hw = "unKnwon";
				var xy = "#" + fw + "_" + hw;
				innerHtml += "&nbsp;Build\: "+ fwMCUDetailList[i].sysSwRevision + "<br>";
			    $(xy).html(innerHtml);
	          }
	      }			

	      function replaceStr(str){
	    	  return str.replace(/\./g,"dot");
		  } 
	      		  

	      /*]]>*/
	</script>
</head>
<body>


<table  width="100%" border="1">
	 <colsgroup>
	  <col width="20%"/>
	  <col width="40%"/>
	  <col width="40%"/>
	 </colsgroup>
	
	 <tr>
	  <td rowspan="3">
	  		<div id="container-2">
				<form id="searchForm-max" onsubmit="return false;">
					<!-- 제조사별 장비 Tree -->
					<fieldset id="vendor-tree">
					<div id="basic_html"></div>
					</fieldset>
				</form>
			</div>
	  </td>
	  <td colspan="">&nbsp;&nbsp;&nbsp;&nbsp;
      	<table  width="90%">
		 <tr>
		  <td>
			<div id="colLine">
				<div class="ddiv">
				  <div>    </div>
				  <div>    </div>
				  <div>    </div>
				  <div>     </div>
				 </div> 
				 
				 <div class="ddiv">  
				 <div>    </div>
				  <div>    </div>
				  <div>    </div>
				  <div>    </div>
				 </div>
			</div>
		  </td>
		 </tr>
		  <table>
      </td>
	 </tr>
	<table>

</body>
</html>

