<%
/**
 * Copyright Nuri Telecom Corp.
 * 파일명: firmWareMainGadget.jsp
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역 
 * ============================================================================
 */
 %>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>FirmwareMgmtGadget</title>

<link href="${ctx}/css/style_firmware.css" rel="stylesheet" type="text/css">    
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script> 
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.checkbox.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.xml_flat.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.xml_nested.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/autocomplete/jquery.autocomplete.js"></script>
</head>
<script type="text/javascript" >/*<![CDATA[*/

        var supplierId = "";

    	var loginId = "";
    	var top_equip_type = "";
    	var change_check= "";
    	var top_model_id = "";
    	var top_node = "";
    	var top_vendor = "";
    	var top_model = "";
    	
    	var getfirmwareListDiv_html = "";
		var getfirmwareListpaingStrMake_html = "";
        // Command 권한
        var cmdAuth = "${cmdAuth}";

        $.ajaxSetup({
            async: false
        });
        
	    /**
	     * 유저 세션 정보 가져오기
	     */
	    $.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    loginId = json.loginId;
	                }
	            }
	    );

	      /*
	        * 공통 달력모듈 
	        */
			//setDate
			 $(document).ready(function(){
			 	var locDateFormat = "yymmdd";
			
			     $("#sInstallStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal1(dateText, inst); }} );
			     $("#sSearchFromDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal2(dateText, inst); }} );
			     $("#sSearchToDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal3(dateText, inst); }} );

		         var date = new Date();
		         var year = date.getFullYear();
		         var month = date.getMonth() + 1;
		         var day = date.getDate();

		         if(("" + month).length == 1) month = "0" + month;
		         if(("" + day).length == 1) day = "0" + day;

		         var setFromDate      = year + "" + month + "" + "01";
	             var setToDate      = year + "" + month + "" + day;
		         var dateFromFullName = "";
	             var dateToFullName = "";
		            
		            // 날짜를 지역 날짜 포맷으로 변경
		         $.getJSON("${ctx}/common/convertLocalDate.do"
		                    ,{dbDate:setFromDate, supplierId:supplierId}
		                    ,function(json) {
		                        dateFromFullName = json.localDate;
		                        $("#sSearchFromDate").val(dateFromFullName);
		                    });
                 // 날짜를 지역 날짜 포맷으로 변경
                 $.getJSON("${ctx}/common/convertLocalDate.do"
                            ,{dbDate:setToDate, supplierId:supplierId}
                            ,function(json) {
                                dateToFullName = json.localDate;
                                $("#sSearchToDate").val(dateToFullName);

                            });
		            
		         $("#realSearchFromDate").val(setFromDate);
		         $("#realSearchToDate").val(setToDate);
		            
			     getfirmwareListDiv_html = $('#frmlistDiv').html();
			     getfirmwareListpaingStrMake_html = $('#frmlistPagingDiv').html();

			     if (cmdAuth == "true") {
			         $("#executeBtn").show();
			         $("#newfile").show();
			         $("#firmwareAddBtn").show();
			     } else {
			         $("#executeBtn").hide();
			         $("#newfile").hide();
			         $("#firmwareAddBtn").hide();
			     }
			 });

			 function clearData(){
				 $('#frmlistDiv').html(getfirmwareListDiv_html);
	    	     $('#frmlistPagingDiv').html(getfirmwareListpaingStrMake_html);
	    	     $("#treeDiv").html("");
	    	     
	             var optionA = $("#distributeFromFile > option");
	             var optionB = $("#distributeToFile > option");
	             for(var i=0;i<optionA.length;i++){
	                 $(optionA[i]).remove();
	             }
	             for(var i=0;i<optionB.length;i++){
	                 $(optionB[i]).remove();
	             }
			 }


			 
			 // 검색조건의 날짜를 Local유형에서 일반 유형으로 변경
			 function modifyDateLocal1(setDate, inst){
				 $("#realInstallStartDate").val(setDate);
				 //alert($("#realInstallStartDate").val());
			     var dateId       = '#' + inst.id;
			     var dateHiddenId = '#' + inst.id + 'Hidden';
			
			     $(dateHiddenId).val($(dateId).val());
			
			     $.getJSON("${ctx}/common/convertLocalDate.do"
			             ,{dbDate:setDate, supplierId:supplierId}
			             ,function(json) {
				             //alert(json.localDate);
			                 $(dateId).val(json.localDate);
			             });
			 }
			 function modifyDateLocal2(setDate, inst){
					 $("#realSearchFromDate").val(setDate);
					 //alert($("#realInstallStartDate").val());
				     var dateId       = '#' + inst.id;
				     var dateHiddenId = '#' + inst.id + 'Hidden';
				
				     $(dateHiddenId).val($(dateId).val());
				
				     $.getJSON("${ctx}/common/convertLocalDate.do"
				             ,{dbDate:setDate, supplierId:supplierId}
				             ,function(json) {
					             //alert(json.localDate);
				                 $(dateId).val(json.localDate);
				             });
				 }
			 function modifyDateLocal3(setDate, inst){
					 $("#realSearchToDate").val(setDate);
					 //alert($("#realInstallStartDate").val());
				     var dateId       = '#' + inst.id;
				     var dateHiddenId = '#' + inst.id + 'Hidden';
				
				     $(dateHiddenId).val($(dateId).val());
				
				     $.getJSON("${ctx}/common/convertLocalDate.do"
				             ,{dbDate:setDate, supplierId:supplierId}
				             ,function(json) {
					             //alert(json.localDate);
				                 $(dateId).val(json.localDate);
				             });
				 }

        $(function() {

        	$('a').click( function(event) {
    			event.preventDefault();
    			return false;
    		});

    		$('#search').click( function() {
    			if (!supplierId) {
    				Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg08"/>');
    				return false;
    			}
    			var searchWord = $(":input[name='searchWord']").val();
    			$.tree.focused().search(searchWord);
    	
    		});

    		$('#search2').click( function() {
    			if (!supplierId) {
    				Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg08"/>');
    				return false;
    			}
    			var searchWord = $(":input[name='searchWord2']").val();
    			$.tree.focused().search(searchWord);
    	
    		});

    		$('#search3').click( function() {
    			var searchWord = $(":input[name='searchWord3']").val();
    			$.tree.focused().search(searchWord);
    	
    		});

    		//좌측 트리 json 
    		$.getJSON('${ctx}/gadget/system/getSupplierList.do', function(json) {
    			//alert(json.supplier);
    	    	if(json.supplier != null){
    				supplierId = json.supplier.id;
    				$('#supplierName').text(json.supplier.name);
    				$('#supplierList').hide();
    				viewDeviceTree("1");
    				return;
    	    	}
    	
    			if(json.supplierList != null){
    			   $('#supplierList').loadSelect(json.supplierList);
    			}
    		});



            // 지역검색
            locationTreeGoGo('treeDivA', 'locationsearchWord', 'sLocationId');

           // $("#svcTypeCode").selectbox();
           // $("#protocolCode").selectbox();
           // $("#group").selectbox();

            //$("div div div[id=btn]").hide();
        });


    	function setSearchWord(){
    		$('#searchWord').click( function() {
    			$(this).val('');
    			$('#searchCount').text('');
    		});
    		$('#searchWord2').click( function() {
    			$(this).val('');
    			$('#searchCount2').text('');
    		});
    		$('#searchWord3').click( function() {
    			$(this).val('');
    			$('#searchCount3').text('');
    		});
    		    	
    		$('#searchWord').keypress(function(e) {
    			 var code=(e.keyCode?e.keyCode:e.which);
    			 if(code==13) { // Enter keycode
    			    	$.tree.focused().search($(this).val());
    			    }
    		});
    		$('#searchWord2').keypress(function(e) {
   			 var code=(e.keyCode?e.keyCode:e.which);
   			 if(code==13) { // Enter keycode
   			    	$.tree.focused().search($(this).val());
   			    }
   		    });
    		$('#searchWord3').keypress(function(e) {
      			 var code=(e.keyCode?e.keyCode:e.which);
      			 if(code==13) { // Enter keycode
      			    	$.tree.focused().search($(this).val());
      			    }
      		});
    		
    	}


    //  ■■■■■■■■■ 장비별 Tree start ■■■■■■■■■ 
    var top_equip_kind ="";
    	function viewDeviceTree(vartreeGubun) {
        	//alert("111");
        	var treeGubun = "";
    		if(vartreeGubun == "1"){
    			treeGubun ="basic_html";
        	}else if(vartreeGubun == "2"){
        		treeGubun ="basic_html2";
            }
    		auto='';
    		$('#searchWord').unautocomplete();
    		$("#"+treeGubun+"").tree( {
    	
    			data : {
    				type : 'json',
    				opts : {
    					method : 'GET',
    					url : '${ctx}/gadget/device/firmware/firmwaretree.do'
    				}
    			},
    			types	: {
    				"default" : {
    					deletable : false,
    					renameable : true
    				},
    				"device" : {
    	
    					icon : {
    					   image : '${ctx}/js/tree/themes/default/device2.gif'
    	
    					}
    				},
    				"vendor" : {
    	
    					icon : {
    					   image : '${ctx}/js/tree/themes/default/vendor2.gif'
    	
    					}
    				},
    	
    				"model" : {
    					icon : {
    					   image : '${ctx}/js/tree/themes/default/model2.gif'
    	
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
    	
    		    						if(configs[j][k][l]){
    				    					for(var m in configs[j][k][l]){
    				    						var config = configs[j][k][l][m];
    					    				}
    		    						}
    	
    			    					auto= auto+'^'+model.name;
    	
    	        						children2.push( {
    	    	    						'data' : {
    	    	    							'title' : model.name,
    	    	    							attributes : {
    	    	    								'href' : '#'
    	    	    							}
    	    	    						},
    	    	    						'attributes' : {
    	    	    							'id' :  device.name+'_'+ model.id+'_'+deviceType.name+'_'+vendor.name+'_'+ model.name,'rel':'model'
    	    	    						},
    	    	    						'children' : children3
    	    	    					});
    			    				}
    	
    		    					auto= auto+'^'+vendor.name;
    		    					children1.push( {
    		    						'data' : {
    		    							'title' : vendor.name,
    		    							attributes : {
    		    								'href' : '#'
    		    							}
    		    						},
    		    						'attributes' : {
    		    							'id' :  device.name+'_'+ vendor.id,'rel':'vendor'
    		    						},
    		    						'children' : children2
    		    					});
    		    				}
    	    					auto= auto+'^'+deviceType.descr;
    	    					children.push( {
    	    						'data' : {
    	    							'title' : deviceType.descr,
    	    							attributes : {
    	    								'href' : '#'
    	    							}
    	    						},
    	    						'attributes' : {
    	    							'id' :  device.name+'_'+ deviceType.id+'_'+deviceType.name+'_3','rel':'equip_type'
    	    						},
    	    						'children' : children1
    	    					});
    		    			}
    	    				if(device.name != "Meter" && device.name != "Unknown"){
	    	    				auto= auto+'^'+device.descr;
        	    				if(device.name == "MCU"){
    	    	    				data.push( {
    	    	    					'data' : {
    	    	    						'title' : "DCU",
    	    	    						'attributes' : {
    	    	    							'href' : '#'
    	    	    						}
    	    	    					},
    	    	    					'attributes' : {
    	    	    						'id' :  device.name+'_'+ device.id,'rel':'device'
    	    	    					},
    	    	    					'children' : children
    	    	    				});
            	    				
            	    			}else{
    	    	    				data.push( {
    	    	    					'data' : {
    	    	    						'title' : device.descr,
    	    	    						'attributes' : {
    	    	    							'href' : '#'
    	    	    						}
    	    	    					},
    	    	    					'attributes' : {
    	    	    						'id' :  device.name+'_'+ device.id,'rel':'device'
    	    	    					},
    	    	    					'children' : children
    	    	    				});

                    	    	}
    	    				}
    	    			}
    	
    	    			var autoArr = auto.split('^');
    	                $('#searchWord').autocomplete(autoArr);
    	                $('#searchWord2').autocomplete(autoArr);
    	                setSearchWord();
    	    			return data;
    	
    		},
    		'onselect' : function(n, t) {
    			clearData();
    			top_node = $(n).attr('id');
    			var str1 = top_node.replace(top_node.substr(0,top_node.indexOf('_')+1),"");
    			var str2 = str1.replace(str1.substr(0,str1.indexOf('_')+1),"");
    			var str3 = str2.replace(str2.substr(0,str2.indexOf('_')+1),"");
    			top_vendor = str3.substr(0,str3.indexOf('_'));
    			top_model = str3.substr(str3.indexOf('_')+1,str3.length);  
    			top_node = top_node.replace("_"+top_vendor,"");
    			top_node = top_node.replace("_"+top_model,"");

    			if(top_node.indexOf("MCU") > -1){
    				top_equip_kind = "MCU";
    			}if(top_node.indexOf("Modem") > -1 ){
    				top_equip_kind = "Modem";
           		}if(top_node.indexOf("Codi")> -1 ){
           			top_equip_kind = "Codi";
               	}
           		var tmpType = "";
           		if(top_equip_kind == "Codi"){
           			tmpType = top_node.replace(top_equip_kind,"");
               	}else{
               		tmpType = top_node.replace(top_equip_kind+"_","");
                }
	   			 top_model_id = tmpType.substr(0,tmpType.indexOf('_'));
				 top_equip_type = tmpType.substr(top_model_id.length+1,tmpType.length+1);
				
    			var modelcheck = $(n).attr('rel');

    			if(modelcheck=="model"||top_node.indexOf('_Codi') > -1){
    				getFirmwareList(top_node,"1","D",treeGubun);
        		}
            		
    		},
    		'onsearch' : function(n, t) {
    			t.container.find('.searchResult').removeClass('searchResult');
    			n.addClass('searchResult');
    			$('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
    			$('#searchCount2').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
    		}
    	}
    	
    	});
    	// ■■■■■■■■■    장비별 Tree End    ■■■■■■■■■ 
    	}
        
    	// ■■■■■■■■■ LOCATION Tree START ■■■■■■■■■ //  
    	// 국가코드는  독립 각 시도는 Tree로 구별 되야 함
        var makeTreeHTML = function(_locations) {
            var treeHTML = "<ul>    \n";

            for(var i = 0, size = _locations.length ; i < size ; i++) {

                var location = _locations[i];
                var locatin_top = "";

                if(location.parent.length == 0){
                	locatin_top = "top_";
                }

                var li_id = locatin_top +location.parent+"_"+ location.id;
                	li_id =  li_id.replace(" ","");
       			
//                treeHTML += "   <li id='"+locatin_top +location.parent+"_"+ location.id + "' ><a><ins>&nbsp;</ins></a>" + location.name + "  <br/><div style=\"MARGIN-TOP: 0px\" class=\"last leaf\" id=\"div_"+ location.id +"\"></div>";
                treeHTML += "   <li id='"+li_id+ "' ><a><ins>&nbsp;</ins>" + location.name + "</a><br/><div style=\"MARGIN-TOP: 0px\" class=\"last leaf\" id=\"div_"+ location.id +"\"></div>";
                if(location.children != null && location.children.length > 0) {
                    treeHTML += makeTreeHTML(location.children, treeHTML);
                }

                treeHTML += "   </li>   \n";
            }
            
            treeHTML += "</ul>  \n";
            return treeHTML;
        };

     // ■■■■■■■■■ LOCATION Tree END ■■■■■■■■■ //
</script>

<script type="text/javascript" charset="utf-8">
    //상위 tab 변경 스크립트 
    $(function() {
        $(function() { $('#_menu1').bind('click',function(event) { changeData("m1"); });});
        $(function() { $('#_menu2').bind('click',function(event) { changeData("m2"); });});
        $(function() { $('#_menu3').bind('click',function(event) { changeData("m3"); });});

        $(function() { $('#sub_tab11').bind('click',function(event) { changeData("sum1"); });});
        $(function() { $('#sub_tab12').bind('click',function(event) { changeData("sum2"); });});
        $(function() { $('#sub_tab13').bind('click',function(event) { changeData("sum3"); });});
        // $("#firmwareTab").tabs();
    });

     function changeData(selTab){
         //alert(selMiniTab);
         if(selTab == "m1"){
        	 $('#m1').show();
        	 $('#_menu1').toggleClass("current");
        	 
        	 $('#_menu2').removeClass("current");
        	 $('#_menu3').removeClass("current");
        	 $('#m2').hide();
        	 $('#m3').hide();
         }else if(selTab == "m2"){
        	 $('#m2').show();
        	 $('#_menu2').toggleClass("current");

        	 $('#_menu1').removeClass("current");
        	 $('#_menu3').removeClass("current");
        	 $('#m1').hide();
        	 $('#m3').hide();

        	 initSingleRegMCU();
        	 
         }else if(selTab == "m3"){
        	 $('#m3').show();
        	 $('#_menu3').toggleClass("current");

        	 $('#_menu2').removeClass("current");
        	 $('#_menu1').removeClass("current");
        	 $('#m1').hide();
        	 $('#m2').hide();
        	 viewDeviceTree("2");
         }

         if(selTab == "sum1"){
        	 $('#sum1').show();
        	 $('#sub_tab11').toggleClass("current");
        	 
        	 $('#sub_tab12').removeClass("current");
        	 $('#sub_tab13').removeClass("current");
        	 $('#sum2').hide();
        	 $('#sum3').hide();
         }else if(selTab == "sum2"){
        	 $('#sum2').show();
        	 $('#sub_tab12').toggleClass("current");

        	 $('#sub_tab11').removeClass("current");
        	 $('#sub_tab13').removeClass("current");
        	 $('#sum1').hide();
        	 $('#sum3').hide();
         }else if(selTab == "sum3"){
        	 $('#sum3').show();
        	 $('#sub_tab13').toggleClass("current");

        	 $('#sub_tab11').removeClass("current");
        	 $('#sub_tab12').removeClass("current");
        	 $('#sum1').hide();
        	 $('#sum2').hide();
         }
         
     }        

    
</script>
<script type="text/javascript" charset="utf-8">
    //모델 리스트 json
	var equip_model_id = "";
	function getFirmwareList(varnode,currPage,paramDel,treeGubun) {
		 $("#treeDiv").html("");
	     equip_model_id = varnode;

	     var url = "/gadget/device/firmware/getFirmwareList.do";

	     if(treeGubun == "basic_html"){
	    	 url = "/gadget/device/firmware/getFirmwareList.do";
		 }else if(treeGubun == "basic_html2"){
			 url = "/gadget/device/firmware/getFirmwareFileMgmList.do";
		 }
	     
	     
    	 $.getJSON("${ctx}"+url+"", {equip_model_id:equip_model_id,
        	 						 supplierId:supplierId,currPage:currPage},
           function(json) {
        	 							
    	      var getfirmwareListDiv = json.firmwareListMakeHtml;
    	      var getfirmwareListpaingStrMakeHtml = json.firmwareListpaingStrMakeHtml;

    	         if(json.searchResult == "0"){
					//alert("검색 데이터가 없습니다.");
					//alert('<fmt:message key="aimir.firmware.msg09"/>');
				 }
    		     if(treeGubun == "basic_html"){
    		    	 $('#frmlistDiv').html(getfirmwareListDiv);
    	    	     $('#frmlistPagingDiv').html(getfirmwareListpaingStrMakeHtml);
    			 }else if(treeGubun == "basic_html2"){   
        			 $('#addFrm_Submit').css('display','block');    			 
    				 $('#frmlistDiv2').html(getfirmwareListDiv);
    	    	     $('#frmlistPagingDiv2').html(getfirmwareListpaingStrMakeHtml);
    	    	     
    			 }
    		     addFWReset();
    		     if(treeGubun == "basic_html2"){   
        		     if(top_equip_kind != "Codi"){
            		     $("#addFrmVendor").val(top_vendor);
     					 $("#addFrmModel").val(top_model);
            		 }
    			 }
    		     changeData("sum1");
     	 });

       	if(paramDel == "D"){
       		fromParamStr = "";
       		toParamStr = "";
       	 	$(":hidden[id='hiddenfileA']").val("");
    	 	$(":hidden[id='hiddenfileB']").val("");
	   	  	rmoptionA = $("#distributeFromFile > option");
	  	  	rmoptionB = $("#distributeToFile > option");
	  	  	//현재 있는 옵션 데이터 모두 지우고 다시 세팅..
	  	    for(var i=0;i<rmoptionA.length;i++){
		  		$(rmoptionA[i]).remove();
		  	}
	  	    for(var i=0;i<rmoptionB.length;i++){
		  		$(rmoptionB[i]).remove();
		  	} 
        }
   	}

	//페이지 이동 
   	function go_page(goPage,treeGubun){
   	   	if(treeGubun == "triggerstep1"){
   	   		triggerGoSearch(goPage);
   	   	}else{
   	   		getFirmwareList(equip_model_id,goPage,"",treeGubun);
   	   	}		
	}       
	
	//펌웨어 리스트 체크박스(두개를 선택하면 아래 배포하기세팅 
	var fromParamStr = "";
	var toParamStr = "";
    var fullfromfile = "";
    var fulltofile = "";
   	function frmlistpagingCheck(checkID,varequip_type) {
   	   	//alert(checkID+"="+varequip_type);
    	top_equip_type = varequip_type;   	   	
	   	var paramStr = "";
	   	var hiddenfileA =  $(":hidden[id='hiddenfileA']").val();
	   	var hiddenfileB =  $(":hidden[id='hiddenfileB']").val();

	   	if($("#"+checkID).is(":checked")){
		   	if(hiddenfileA != "" && hiddenfileB != ""){//이미 두개의 파일을 모두 선택 하였을 시 
		   		if(confirm('이미 두개의 파일을 선택 하셨습니다. 기존 파일을 변경 하시겠습니까?')){
			    	var cb = $("input[name=frmlistcheckbox]:checkbox:checked");			   		
		   			if(confirm('확인을 선택하시면 from 취소를 선택하시면 to에 변경 됩니다.')){
				    	for(var i=0;i<cb.length;i++){
							if(($(cb[i]).val()).indexOf(fromParamStr) > -1 ){
								$(cb[i]).attr("checked",false);
							}
					    }
		   				fromParamStr = $("#"+checkID).val();
		   				fullfromfile = fromParamStr;
						$(":hidden[id='hiddenfileA']").val(fromParamStr);
				   	}else{
				    	for(var i=0;i<cb.length;i++){
							if(($(cb[i]).val()).indexOf(toParamStr) > -1 ){
								$(cb[i]).attr("checked",false);
							}
					    }					   	
						toParamStr = $("#"+checkID).val();
						fulltofile = toParamStr;
						$(":hidden[id='hiddenfileB']").val(toParamStr);
					}
			   	}else{
			   		$("#"+checkID).attr("checked",false);
				}
			}else if(hiddenfileA == "" && hiddenfileB == ""){//파일을 하나도 선택 하지 않았을 경우
				fromParamStr = $("#"+checkID).val();
				fullfromfile = fromParamStr;
				$(":hidden[id='hiddenfileA']").val(fromParamStr);
			}else if(hiddenfileA == "" && hiddenfileB != ""){//from을 선택하지 않았을경우
				fromParamStr = $("#"+checkID).val();
				fullfromfile = fromParamStr;
				$(":hidden[id='hiddenfileA']").val(fromParamStr);
			}else if(hiddenfileA != "" && hiddenfileB == ""){//to을 선택하지 않았을경우
				toParamStr = $("#"+checkID).val();
				fulltofile = toParamStr;
				$(":hidden[id='hiddenfileB']").val(toParamStr);
			}
		}else{
			if($("#"+checkID).val() == fromParamStr){
				$(":hidden[id='hiddenfileA']").val("");
				fromParamStr = "";
				fullfromfile = fromParamStr;
			}else if($("#"+checkID).val() == toParamStr){
				$(":hidden[id='hiddenfileB']").val("");
				toParamStr = "";
				fulltofile = toParamStr;
			}
		}
		
        fromToFileSet(fromParamStr,toParamStr);
        setLocationForm("B");
  	}

	function setLocationForm(varfromToType){
		if(varfromToType == "A"){
			vfromParamStr  = toParamStr;//fromParamStr ;
  	    }else if(varfromToType == "B"){
  	    	vfromParamStr  = fromParamStr;//toParamStr ;
  	  	}

  	  	//alert(vfromParamStr);
  	  	
	   	 $.getJSON('${ctx}/gadget/device/firmware/setLocationForm.do', {equip_model_id:equip_model_id,paramStr:vfromParamStr,supplierId:supplierId,equip_type:top_equip_type},
	            function(data) {
	                var treeHTML = "";
	                treeHTML += "<ul>"; 
	                treeHTML += makeTreeHTML(data.locationList);
	                treeHTML += "   </li>";
	                treeHTML += "</ul>";

	                $('#treeDiv').html(treeHTML);

	                setSearchWord();

	                $('#treeDiv').tree({
	                    ui : {
	                        theme_name : "checkbox"
	                    },
	                    plugins : {
	                        checkbox : { three_state : false }
	                    },
	   		    		'onsearch' : function(n, t) {
	            			n.addClass('searchResult');
	            			//alert(n.length);
	            			$('#searchCount3').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
	            		}
		             });
		             $.tree.focused().open_all("#treeDiv");
		 });
	}

	//파일명 초기 세팅 
  	function fromToFileSet(varfromfile,vartofile){
		//varParm: SWAMM_1_1_1_2_1.0_2.2_04_false.ebl|1.0|2.2|04		
		var fromfile = varfromfile.substr(0,varfromfile.indexOf('|'));
		var tofile = vartofile.substr(0,vartofile.indexOf('|'));

  	  	rmoptionA = $("#distributeFromFile > option");
  	  	rmoptionB = $("#distributeToFile > option");

  	  	//현재 있는 옵션 데이터 모두 지우고 다시 세팅..
  	    for(var i=0;i<rmoptionA.length;i++){
	  		$(rmoptionA[i]).remove();
	  	}
  	    for(var i=0;i<rmoptionB.length;i++){
	  		$(rmoptionB[i]).remove();
	  	} 
		
		var divFromStr = "";
		     divFromStr += "<option selected>"+fromfile+"</option>";
		     divFromStr += "<option >"+tofile+"</option>";
		     $("#distributeFromFile").append(divFromStr);
		var divToStr = "";
			divToStr += "<option selected>"+tofile+"</option>";
			divToStr += "<option >"+fromfile+"</option>";
			$("#distributeToFile").append(divToStr);		     		
  	}

  	function trim(value) { 
  		return value.replace(/^\s+|\s+$/g,""); 
	} 

    //파일명 option 병경 시 Event 
  	function changeFromTo(varType){
  	  	//alert($("#distributeFromFile > option:selected").val());
  	  	change_check = varType;
  	  	var fromfile = $("#distributeFromFile > option:selected").val();
  	    var orgFrom = trim(fromParamStr.substr(0,fromParamStr.indexOf('|'))); ;
  	  	if(fromfile != orgFrom){
  	  	  var tmpfromParamStr = trim(fromParamStr);
  	  	  var tmptoParamStr =   trim(toParamStr);
  	  	  fromParamStr = trim(tmptoParamStr);
  	  	  toParamStr = trim(tmpfromParamStr) ;
  	  	}
  	  	//var orgFrom = fromParamStr.substr(0,fromParamStr.indexOf('|')); ;
  	    //alert(orgFrom);
		var tofile = "";
		var setType = "A";

		if(varType=="A"){
			if(orgFrom != fromfile){
				setType = "B";
			}else{
				setType = "A";
			}
		}else {

			if(orgFrom != fromfile){
				setType = "A";
			}else{
				setType = "B";
			}
		}
  	  	
  	  	rmoptionA = $("#distributeFromFile > option");
  	  	rmoptionB = $("#distributeToFile > option");
  	  	
  	  	for(var i=0;i<rmoptionA.length;i++){
  	  	  	if(fromfile != $(rmoptionA[i]).val()){
  	  	  		tofile = $(rmoptionA[i]).val();
  	  	  	}
  	  	}
  	  	//현재 있는 옵션 데이터 모두 지우고 다시 세팅..
  	    for(var i=0;i<rmoptionA.length;i++){
	  		$(rmoptionA[i]).remove();
	  	}
  	    for(var i=0;i<rmoptionB.length;i++){
	  		$(rmoptionB[i]).remove();
	  	}  	  	
  	  	if(varType == "A"){
  			var divFromStr = "";
       		divFromStr += "<option selected>"+fromfile+"</option>";
        	divFromStr += "<option >"+tofile+"</option>";
        	$("#distributeFromFile").append(divFromStr);
	   		 var divToStr = "";
			divToStr += "<option selected>"+tofile+"</option>";
			divToStr += "<option >"+fromfile+"</option>";
			$("#distributeToFile").append(divToStr);  	  	 
	
			 setLocationForm(setType); 	
  		
  	  	}else if (varType == "B"){
	  	  	var divFromStr = "";
	        divFromStr += "<option selected>"+tofile+"</option>";
	        divFromStr += "<option >"+fromfile+"</option>";
	        $("#distributeFromFile").append(divFromStr);
			var divToStr = "";
			divToStr += "<option selected>"+fromfile+"</option>";
			divToStr += "<option >"+tofile+"</option>";
			$("#distributeToFile").append(divToStr);

			 setLocationForm(setType);	
  	    }
  	}
  	
  	//배포>>배포버튼 클릭
  	function distributeExecute(){
        var tmpequipList = Array();		

		if(top_equip_kind=="MCU"){
	        $.tree.plugins.checkbox.get_checked($.tree.reference('#treeDiv')).each(function() {
	            tmpequipList.push(this.id);
	        });
		}else{
	        $.tree.plugins.checkbox.get_checked($.tree.reference('#treeDiv')).each(function() {
	            tmpequipList.push(this.id);
	        });
	    }

        //setting변수 
        var mcuBuild = ""; //modem일 경우만 가지고 온다
		var transferType = $('#transferType option:selected').val();	
		var installType = $('#installType option:selected').val();
		var otaThreadCount = $('#otaThreadCount option:selected').val();
		var maxRetryCount =  $('#maxRetryCount option:selected').val();
		var multicastWriteCount =  $('#multicastWriteCount option:selected').val();
		var sendEUI642 =  $('#sendEUI642 option:selected').val();
		var saveHistory = $('#saveHistory option:selected').val();

		var fromFileNm = $("#distributeFromFile option:selected'").val();
		var toFileNm = $("#distributeToFile option:selected'").val();  
		var oldHwVersion ="";
		var oldFwVersion ="";
		var oldBuild     ="";
		var oldFirmwareId = "";
		var oldArm = "";
		
		var newHwVersion ="";
		var newFwVersion ="";
		var newBuild     ="";
		var newFirmwareId = "";
		var newArm = "";

	    //체크박스 리스트 변수 
		var checkList = $("#otaStepDiv input:checked");
        var checkOTAListResult = "";
		  for (var i = 0; i < checkList.length; i++) {
				  checkOTAListResult += $(checkList[i]).val() + ",";
		  }

		// from To file, viersion , build
	   	var fileA = fullfromfile;
	   	var fileB = fulltofile;

		if(fileA.indexOf(fromFileNm) > -1){
			var ttt = fileA.replace(fromFileNm+"|",'');
			var ttt2 = ttt.substr(0,ttt.indexOf("|"));
			ttt = ttt.replace(ttt2+"|",'');
			var ttt3 = ttt.substr(0,ttt.indexOf("|"));
			ttt = ttt.replace(ttt3+"|",'');
			var ttt4 = ttt.substr(0, ttt.indexOf("|"));
			ttt = ttt.replace(ttt4+"|",'');
			var ttt5 = ttt.substr(0, ttt.indexOf("|"));
			ttt = ttt.replace(ttt5+"|",'');
			var ttt6 = ttt.substr(0,ttt.length );

			oldHwVersion =ttt2;
			oldFwVersion =ttt3;
			oldBuild     =ttt4;
			oldFirmwareId =ttt5;
			oldArm = ttt6;
		
			var aaa = fileB.replace(toFileNm+"|",'');
			var aaa2 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa2+"|",'');
			var aaa3 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa3+"|",'');
			var aaa4 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa4+"|",'');
			var aaa5 = aaa.substr(0, aaa.indexOf("|"));
			aaa = aaa.replace(aaa5+"|",'');
			var aaa6 = aaa.substr(0,aaa.length );
			
			newHwVersion =aaa2;
			newFwVersion =aaa3;
			newBuild     =aaa4;
			newFirmwareId = aaa5;
			newArm = aaa6;

		}else{
			var ttt = fileB.replace(fromFileNm+"|",'');
			var ttt2 = ttt.substr(0,ttt.indexOf("|"));
			ttt = ttt.replace(ttt2+"|",'');
			var ttt3 = ttt.substr(0,ttt.indexOf("|"));
			ttt = ttt.replace(ttt3+"|",'');
			var ttt4 = ttt.substr(0, ttt.indexOf("|"));
			ttt = ttt.replace(ttt4+"|",'');
			var ttt5 = ttt.substr(0,ttt.indexOf("|"));
			ttt = ttt.replace(ttt5+"|",'');
			var ttt6 = ttt.substr(0,ttt.length);

			oldHwVersion =ttt2;
			oldFwVersion =ttt3;
			oldBuild     =ttt4;
			oldFirmwareId =ttt5;
			oldArm =ttt6;

			var aaa = fileA.replace(toFileNm+"|",'');
			var aaa2 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa2+"|",'');
			var aaa3 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa3+"|",'');
			var aaa4 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa4+"|",'');
			var aaa5 = aaa.substr(0,aaa.indexOf("|"));
			aaa = aaa.replace(aaa5+"|",'');
			var aaa6 = aaa.substr(0,aaa.length);

			newHwVersion =aaa2;
			newFwVersion =aaa3;
			newBuild     =aaa4;
			newFirmwareId = aaa5;
			newArm = aaa6;
		}

		var equipList = "";
		for(i=0;i<tmpequipList.length;i++){
			equipList = equipList + tmpequipList[i] +"|";
		}

		//alert("top_equip_type "+top_equip_type);
		//top_equip_type = "MMIU";
		if(top_equip_kind == "MCU"){
			
			 $.getJSON("${ctx}/gadget/device/firmware/getMcuBuild.do", {
				equip_type:top_equip_type,
				supplierId:supplierId,			
				oldHwVersion:oldHwVersion,
				oldFwVersion:oldFwVersion,
				oldBuild:oldBuild,
				model_id:top_model_id,
				equip_kind:top_equip_kind
			 },
			function(json) {
					if(json.mcuBuild == null){
						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg07"/>');
					}else{
						mcuBuild = json.mcuBuild;
						var goUrl = "";
						goUrl = "${ctx}/gadget/device/command/cmdDistribution.do";
						
						$.getJSON(goUrl, {
							//target:"",
							loginId:loginId,
							top_equip_kind:top_equip_kind,
							top_equip_type:top_equip_type,
							supplierId:supplierId,
							checkOTAListResult:checkOTAListResult,
							transferType:transferType,
							installType:installType,
							otaThreadCount:otaThreadCount,
							maxRetryCount:maxRetryCount,
							multicastWriteCount:multicastWriteCount,
							sendEUI642:sendEUI642,
							saveHistory:saveHistory,
							fromFileNm:fromFileNm,
							toFileNm:toFileNm,
							oldHwVersion:oldHwVersion,
							oldFwVersion:oldFwVersion,
							oldBuild:oldBuild,
							newHwVersion:newHwVersion,
							newFwVersion:newFwVersion,
							newBuild:newBuild,
							equipList:encodeURIComponent(equipList),
							oldFirmwareId:oldFirmwareId,
							newFirmwareId:newFirmwareId,
							oldArm:oldArm,
							newArm:newArm,
							vendor:top_vendor,
							model:top_model,
							model_id:top_model_id
							 },
							function(json) {
									if(json.rtnStr == null){
										Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg07"/>');
									}else{
										Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.rtnStr);
									}								
							});
					}					
			});
		}else{
			//mcuBuild = oldBuild;
			var goUrl = "";
			if(top_equip_type == "MMIU" || top_equip_type == "IEIU"){
				goUrl = "${ctx}/gadget/device/command/cmdDistributionMMIU.do";
			}else{
				goUrl = "${ctx}/gadget/device/command/cmdDistribution.do";
			}
			
			$.getJSON(goUrl, {
				//target:"",
				loginId:loginId,
				top_equip_kind:top_equip_kind,
				top_equip_type:top_equip_type,
				supplierId:supplierId,
				checkOTAListResult:checkOTAListResult,
				transferType:transferType,
				installType:installType,
				otaThreadCount:otaThreadCount,
				maxRetryCount:maxRetryCount,
				multicastWriteCount:multicastWriteCount,
				sendEUI642:sendEUI642,
				saveHistory:saveHistory,
				fromFileNm:fromFileNm,
				toFileNm:toFileNm,
				oldHwVersion:oldHwVersion,
				oldFwVersion:oldFwVersion,
				oldBuild:oldBuild,
				newHwVersion:newHwVersion,
				newFwVersion:newFwVersion,
				newBuild:newBuild,
				equipList:encodeURIComponent(equipList),
				oldFirmwareId:oldFirmwareId,
				newFirmwareId:newFirmwareId,
				oldArm:oldArm,
				newArm:newArm,
				vendor:top_vendor,
				model:top_model,
				model_id:top_model_id
				 },
				function(json) {
						if(json.rtnStr == null){
							Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg07"/>');
						}else{
							Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.rtnStr);
						}
						
				});
		}
  	}

    //지역별 model설치 갯수를 클릭 했을때 장비리스트를 트리에 추가해서 뿌려줌.
    function getDistriButeMcuIdList(varequip_kind,varlocaionId,vardevicemodel_id,varswVersion,varhwVersion,varswRevision){
   	    $.getJSON('${ctx}/gadget/device/firmware/getDistriButeMcuIdList.do', {equip_kind:varequip_kind,locaionId:varlocaionId,devicemodel_id:vardevicemodel_id,swVersion:varswVersion,hwVersion:varhwVersion,swRevision:varswRevision,supplierId:supplierId,equip_type:top_equip_type},
             function(json) {
      	      var distributeModelList = json.distributeModelList;
      	     // alert("|"+distributeModelList+"|");
      	      if(distributeModelList != ""){
          	    // alert(varlocaionId);
      	    	$("#div_"+varlocaionId).html(distributeModelList);
             } 
			  
       	});
//		$("#div_"+varlocaionId).html("<li><a><ins>&nbsp;</ins>9879878456465</a></li><li><br/><a><ins>&nbsp;</ins>112132154564</a><br/></li>");			
    }
  	
  	//모뎀 리스트 조회
  	function getDistriButeModemList(varmcuId,varhwVersion,varswVersion,varswRevision,varlocaionId,varequip_kind, var_i){
	   	 $.getJSON('${ctx}/gadget/device/firmware/setDistriButeModemList.do', {equip_model_id:equip_model_id,mcu_Id:varmcuId,hwVersion:varhwVersion,swVersion:varswVersion,
		   	                                                                    swRevision:varswRevision,locaionId:varlocaionId,equip_kind:varequip_kind,supplierId:supplierId,equip_type:top_equip_type},
        function(data) {
        	modemDivList = data.modemDivList;
//        	alert(varmcuId);
        	$("#div_"+var_i+"_"+varmcuId).html(modemDivList);
		});
  	}


  	var dstb_firmware_id ="";
  	var dstb_hwersion ="";
  	var dstb_swersion ="";
  	var dstb_revision ="";
  	function viewDistributeStatus(varfrimware_id,varhwersion,varswersion,varrevision,gubun){
  		dstb_firmware_id = varfrimware_id;
  		dstb_hwersion = varhwersion;
  		dstb_swersion = varswersion;
  		dstb_revision = varrevision;

  		//alert(varfrimware_id);
		changeData("sum2");
		$.getJSON('${ctx}/gadget/device/firmware/distributeStatus.do', {
			top_equip_kind:top_equip_kind,
			top_equip_type:top_equip_type,
			supplierId:supplierId,
			firmware_Id:varfrimware_id,
			hw_version:varhwersion,
			fw_version:varswersion,
			build:varrevision,
			gubun:gubun,
			cmdAuth:cmdAuth
		 },
		function(json) {
			 makeHtml = json.makeHtmlLocationTriger;
			 //alert(makeHtml);
			 if(makeHtml==""){
//				 alert("조회된 데이터가 없습니다.");
				 //alert('<fmt:message key="aimir.firmware.msg09"/>');
		     }else{
		    		$("#sum2").html(makeHtml);
			 }
	        
		}); 	
  	}

    // radiobutton 이벤트가 먹히지 않아 함수 호출로 작업 하였음.
  	function clickViewDistributeStatus(gubun){
  	  	//alert(gubun);
	    if( dstb_firmware_id!=""){ 
 	  	 viewDistributeStatus(dstb_firmware_id,dstb_hwersion,dstb_swersion,dstb_revision,gubun);
     	}    
	 }


	//배포파일관리 리스트에서 클릭시 addform에 (model,vendor,hw,sw,build 세팅)
	function setAddFirmWareForm(pfirmware_id,pequip_type,pvendor,pmodel,phwersion,pswersion,prevision,parm){
		//$("#addFrmVendor").val(top_vendor);
		//$("#addFrmModel").val(top_model);
		$("#addFrmHwVersion").val(phwersion);
		$("#addFrmSwVersion").val(pswersion);
		$("#addFrmBuild").val(prevision);
		$("#addFrmArm").val(parm);
		$("#addFrmEquip_type").val(pequip_type);
		$("#addFrmFirmwareid").val(pfirmware_id);
        $("#supplierId").val(supplierId);
	}
    /*]]>*/
</script>

<body>
 <div id="wrapper">
	<!-- tab -->
	<div id="firmwareTab" class="fw_tab">
	 <ul>
	 <li><a href="#m1"  id="_menu1" class="current">Distribution</a></li>
	 <li><a href="#m2"  id="_menu2" >Distribution History</a></li>
	 <li><a href="#m3"  id="_menu3" >Firmware File Management</a></li>
	 </ul>
	</div>
	<!--// tab -->
    
	<div class="clear"></div>
	<!--//all tab -->
	<div class="mgn_all20">
    
    <!-- menu 1 -->
	<div id="m1" class="container">
	 <!-- left tree -->
	<div class="lnb w230" style="height:351px">
	<form id="searchForm" onSubmit="return false;">
				<!-- search -->
				<div class="search-s1 mgn_t10 mgn_lft12">
                    <ul class="w202">
                    <li class="search-s1-input"><input name="searchWord" id='searchWord' type="text" value="Search"></li>
                    <li class="search-s1-btn"><a href="#" id="search"></a></li>
                    </ul>
                    <ul class="search-s1-result2">
                    <li><font id="searchCount"></font></li>
                    </ul>
				</div>
				<div class="clear"></div>
				<!-- 제조사별 장비 Tree -->
				<fieldset id="vendor-tree" style="width:230px !important"><div id="basic_html"></div></fieldset>
	</form>	 
	</div>	
	<!--// left tree -->
	<!-- contents -->   
	<div class="content mgn_lft250">
	 <div class="width_auto mgn_b20">
	  <div class="width_auto" id="frmlistDiv">
	
        <table class="fw_table">
        <colgroup>
        <col width="22"/>
        <col width="5%"/>
        <col width="6%"/>
        <col width="12%"/>
        <col width="10%"/>
        <col width="10%"/>
        <col width="10%"/>
        <col width="6%"/>
        <col width="8%"/>
        <col width="8%"/>
        <col width="8%"/>
        <col width="8%"/>
        <col width="8%"/>
        <col width=""/>
        </colgroup>
        <tr>
         <th><input type="checkbox" class="checkbox"></th>
         <th>H/W</th>
         <th>FW Ver</th>
         <th>FW Rev</th>
         <th>Write Date</th>
         <th>Release</th>
         <th>Build Name</th>
         <th>Status</th>
         <th>Total</th>
         <th>Success</th>
         <th>Executing</th>
         <th>Error</th>
         <th>Writer</th>
         <th>Descr</th>
        </tr>
        <tr>
         <td><input type="checkbox" class="checkbox"></td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
         <td>-</td>
        </tr>
       </table>
     </div>
     <!-- paginate -->
     <div class="mgn_t10"  id="frmlistPagingDiv">
     <table class="fw_page" align="center">
     <tr>
        <td class="pv"><a href="#"><span class="prev2"> </span></a></td>
        <td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
        <td class="on"><a href="#">1</a></td>
        <td class="nv"><a href="#"><span class="next"> </span></a></td>
        <td class="nnv"><a href="#"><span class="next2"> </span></a></td>
     </tr>
     </table>
     </div>
     <!--// paginate -->
		
     <div class="width_auto">
     <!-- sub tab -->
     <div class="fw_sub_tab">
      <ul>
      <li><a href="#sum1" name="sub_tab11"  id="sub_tab11" class="current">Distribute</a></li>
      <li><a href="#sum2" name="sub_tab12" id="sub_tab12">Distribution Status</a></li>
      <li><a href="#sum3" name="sub_tab13" id="sub_tab13">Distribution Preferences</a></li>
      </ul>
     </div>
	 <!--// sub tab -->
		 
     <div class="box_blu pdg_all15" >
        <!-- sub tab1 -->
        <div class="float_none"  id="sum1">
            <div class="box_gry pdg_all15">
            <em class="blu12_bold">From : </em>
            <select id="distributeFromFile" style="min-width:280px;*width:380px" onchange="javascript:changeFromTo('A');" >
            </select> 
            <em class="blu12_bold mgn_lft30">To : </em>
            <select id="distributeToFile" style="min-width:280px;*width:380px" onchange="javascript:changeFromTo('B');" >
            </select>
            
            <ul class="fw_inline mgn_t10">
            <li><input type="radio" name="a1" class="radio" > <span>By MCU</span></li>
            <li><input type="radio" name="a1" class="radio" checked> <span>By Location</span></li>
            <!-- li class="mgn_r70"><input type="radio" name="a1" class="radio"> <span>By Equip</span></li-->
             <li><input type="text" name="searchWord3"id='searchWord3' value="Search" style="width:200px"><em class="fw_button"><a href="#" id="search3">Search</a></em></li>
            </ul>
            </div>
            <div class="float_none"></div>
            <div class="boxborder_gry"><div id="treeDiv"></div></div>
            <div id="clearBtn" class="btn_right"><em class="fw_btn_org"><a onclick="javascript:clearData();">Clear</a></em></div>
            <div id="executeBtn" class="btn_right"><em class="fw_btn_org"><a onclick="javascript:distributeExecute();">Execute</a></em></div>
        </div>
		<!-- // sub tab1 -->

        <!-- sub  tab2 -->
        <div class="float_none" id="sum2" style="display:none;">
            
            <ul class="fw_inline h30" >
            <li class="mgn_r20" ><input type="radio" name="a2" class="radio" id="distributeStatusA" onclick="javascript:clickViewDistributeStatus('A')"> By MCU</li>
            <li class="mgn_r20"><input type="radio" name="a2" class="radio" id="distributeStatusB" onclick="javascript:clickViewDistributeStatus('B')"> By Location</li>
            </ul>

            <div class="float_none"></div>
        
            <table class="fw_table">
            <colgroup>
            <col width="10%"/>
            <col width=""/>
            <col width="15%"/>
            <col width="10%"/>
            <col width="15%"/>
            <col width="10%"/>
            <col width="10%"/>
            <col width="10%"/>
            </colgroup>
            <tr>
             <th>MCU ID</th>
             <th>Trigger ID</th>
             <th>Total</th>
             <th>Sucess</th>
             <th>Executing</th>
             <th>Cancel</th>
             <th>Error</th>
             <th>Retry</th>
            </tr>
            <tr>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
            </tr>
            <tr>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
             <td>-</td>
            </tr>
           </table>
             
        <!-- paginate -->
        <div class="mgn_t10" style="display:none;">
        <table class="fw_page" align="center" >
        <tr>
            <td class="pv"><a href="#"><span class="prev2"> </span></a></td>
            <td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
            <td class="on"><a href="#">1</a></td>
            <td><a href="#">2</a></td>
            <td><a href="#">3</a></td>
            <td><a href="#">4</a></td>
            <td><a href="#">5</a></td>
            <td><a href="#">6</a></td>
            <td><a href="#">7</a></td>
            <td><a href="#">8</a></td>
            <td><a href="#">9</a></td>
            <td class="last"><a href="#">10</a></td>            
            <td class="nv"><a href="#"><span class="next"> </span></a></td>
            <td class="nnv"><a href="#"><span class="next2"> </span></a></td>
        </tr>
        </table>
        </div>
       <!--// paginate -->

        <table class="fw_table mgn_t30"  style="display:none;">
        <caption><span class="ico_title"></span>장비목록</caption>
        <colgroup>
        <col width="40"/>
        <col width="10%"/>
        <col width="25%"/>
        <col width="25%"/>
        <col width=""/>
        <col width="15%"/>

        </colgroup>
        <tr>
         <th>NO</th>
         <th>MCU ID</th>
         <th>Last Firmware Ver</th>
         <th>Success</th>
         <th>Error</th>
         <th>Retry</th>
        </tr>
        <tr>
         <td>1</td>
         <td>1001</td>
         <td>01</td>
         <td>0010ab00123123</td>
         <td>20100010ab0012</td>
         <td><em class="fw_button_gry"><a href="#">Retry</a></em></td>
        </tr>
        <tr>
         <td>1</td>
         <td>1001</td>
         <td>01</td>
         <td>0010ab00123123</td>
         <td>20100010ab0012</td>
         <td><em class="fw_button_gry"><a href="#">Retry</a></em></td>
        </tr>
       </table>
            
            
        <!-- paginate -->
        <div class="mgn_t10"  style="display:none;">
        <table class="fw_page" align="center">
        <tr>
            <td class="pv"><a href="#"><span class="prev2"> </span></a></td>
            <td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
            <td class="on"><a href="#">1</a></td>
            <td><a href="#">2</a></td>
            <td><a href="#">3</a></td>
            <td><a href="#">4</a></td>
            <td><a href="#">5</a></td>
            <td><a href="#">6</a></td>
            <td><a href="#">7</a></td>
            <td><a href="#">8</a></td>
            <td><a href="#">9</a></td>
            <td class="last"><a href="#">10</a></td>
            <td class="nv"><a href="#"><span class="next"> </span></a></td>
            <td class="nnv"><a href="#"><span class="next2"> </span></a></td>
        </tr>
        </table>
        </div>
       <!--// paginate -->

     </div>	
     <!-- // sub tab2 -->

        <!-- sub tab3 -->
        <div class="float_none"  id="sum3" style="display:none;">
            <table class="fw_table_write">
            <caption><span class="ico_title"></span>Option Setting</caption>
            <colgroup>
            <col width="140"/>
            <col width=""/>
            <col width="140"/>
            <col width=""/>
            </colgroup>
            <tr>
            <th>Transfer Type</th>
            <td>
            <select name=transferType id="transferType" style="width:100px;">                                                                                                                             
				<option value='0'>Auto</option>                                                                                                                              
				<option value='1'>Multicast</option>                                                                                                                         
				<option value='2' selected>Unicast</option>                                                                                                                  
		    </select> 
		    </td>
            <th>Install Type</th>
            <td>
			<select name=installType id="installType"  style="width:100px;">                                                                                                                                      
        		<option value='0' selected>Auto</option>                                                                                                                       
        		<option value='1'>ReInstall</option>                                                                                                                           
        		<option value='2'>Match</option>                                                                                                                               
        	</select>            
            </td>
            </tr>
             <tr>
            <th>OTA Thread Count</th>
            <td>
			<select name=otaThreadCount id="otaThreadCount" style="width:100px;">                                                                                                                                   
        		<option value='1'>1</option>                                                                                                                                   
        		<option value='2' selected>2</option>                                                                                                                          
        	</select>                               
            </td>
            <th>Max Retry Count</th>
            <td >
 			<select name=maxRetryCount id="maxRetryCount" style="width:100px;">                                                                                                                                    
				<option value='1' selected>1</option>                                                                                                                          
				<option value='2'>2</option>                                                                                                                                   
				<option value='3'>3</option>                                                                                                                                   
				<option value='4'>4</option>                                                                                                                                   
				<option value='5'>5</option>                                                                                                                                   
				<option value='6'>6</option>                                                                                                                                   
				<option value='7'>7</option>                                                                                                                                   
				<option value='8'>8</option>                                                                                                                                   
				<option value='9'>9</option>                                                                                                                                   
				<option value='10'>10</option>                                                                                                                                 
			 </select>
			</td>
            </tr>
             <tr>
            <th>Multicast Write Count</th>
            <td>
			  <select name=multicastWriteCount id="multicastWriteCount" style="width:100px;">                                                                                                                              
				<option value='1' selected>1</option>                                                                                                                          
				<option value='2'>2</option>                                                                                                                                   
				<option value='3'>3</option>                                                                                                                                   
				<option value='4'>4</option>                                                                                                                                   
				<option value='5'>5</option>                                                                                                                                   
				<option value='6'>6</option>                                                                                                                                   
				<option value='7'>7</option>                                                                                                                                   
				<option value='8'>8</option>                                                                                                                                   
				<option value='9'>9</option>                                                                                                                                   
				<option value='10'>10</option>                                                                                                                                 
			  </select>                
            </td>
            <th>Send EUI64</th>
            <td>
        	  <select name=sendEUI64 id=sendEUI642 onchange='' style="width:100px;">                                                                                                              
        		<option value='true'>Yes</option>                                                                                                                              
        		<option value='false'>No</option>                                                                                                                              
        	  </select>             
            </td>
            </tr>
             <tr>
            <th>Save History</th>
            <td>
			  <select name=saveHistory id=saveHistory onchange='changeHistory(this.value)' style="width:100px;">                                                                                 
				<option value='true' selected>Yes</option>                                                                                                                     
				<option value='false'>No</option>                                                                                                                              
			  </select>	            
            </td>
            <th>OTA Step</th>
            <td>
            
             <div id="otaStepDiv" >
	             <ul class="fw_inline">  
		             <li><input name='otaStep' id="otaStep"  type="checkbox" class="checkbox" value=1 checked> <span>Init</span></li>
		             <li><input name='otaStep' id="otaStep"  type="checkbox" class="checkbox" value=2 checked> <span>Data Send</span></li>
		             <li><input name='otaStep' id="otaStep"  type="checkbox" class="checkbox" value=4 checked> <span>Verify</span></li>
		             <li><input name='otaStep' id="otaStep"  type="checkbox" class="checkbox" value=8 checked> <span>Install</span></li>
		             <li><input name='otaStep' id="otaStep"  type="checkbox" class="checkbox" value=16 checked> <span>Scan</span></li>
	             </ul>   
             </div>
           
            </td>
            </tr>
            </table>
            <!-- div class="btn_right"><em class="fw_button"><a href="#">변경</a></em></div-->
        </div>
        <!--// sub tab3 -->
        
      </div>
     </div>
    </div>
   </div>	
  <!--// contents -->
  </div>
  <!--// menu 1 -->
    
    <!-- menu 2 -->
	<div  id="m2" class="container" style="display:none;">
    <p class="blu12_bold mgn_b15"><span class="ico_title"></span>Trigger List</p>
    <div class="fw_search">
        <table class="fw_basic">
        <colgroup>
        <col width="90px" />
        <col width="120px" />
        <col width="90px" />
        <col width="120px" />
        <col width="120px" />
        <col width="" />
        </colgroup>
        <tr>
        <th width="10%">Equip Kind:</th>
        <td width="20%">
            <select name="trequip_kind" id="trequip_kind" onchange="javascript:equipTypeChange();" >
            <option selected="selected">MCU</option>
            <option>Modem</option>
            <option>Codi</option>
            </select>
        </td>
        <th width="10%">Equip Type:</th>
        <td width="20%">
            <select id="singleEquipType" name="singleEquipType" style="width:120px" onchange="javascript:getVendorListByEquipType();"></select>
        </td>
        <th width="10%">Location: </th>
        <td>
             <input name="locationsearchWord" id='locationsearchWord' class="billing-searchword" type="text" value='<fmt:message key="aimir.supplySelectArea"/>' />      
             
        </td>
        </tr>
        <tr>
        <th>Vendor</th>
        <td>
            <select id="regLogVendor" name="regLogVendor" style="width:120px" onchange="javascript:getDeviceModelsByVenendorId();"></select>
        </td>
        <th>Model:</th>
        <td>
            <select name="sModel" id="sModel" style="width:120px" ></select>
        </td>
        <th>State: </th>
        <td>
            <select name="trState" id="trState" >
            <option selected="selected">All</option>
            <option>Succ</option>
            <option>Exec</option>
            <option>Cancel</option>
            <option>Error</option>
            </select>
        </td>
        </tr>
        <tr>
        <th>Trigger ID:</th>
        <td><input type="text" name="trTriggerID" id="trTriggerID" /></td>
        <th>Equip. ID:</th>
        <td><input type="text" name="trEquipID" id="trEquipID" /></td>
        <th>Issue Date:</th>
        <td>
            <ul class="fw_inline">
            <li><input id="sSearchFromDate" name="sSearchFromDate" class="day" type="text"></li>
             <li class="mgn_all3"> ~ </li>
            <li><input id="sSearchToDate" name="sSearchToDate" class="day" type="text"></li>
            
            <li class="mgn_lft30"><em class="fw_button"><a onclick="javascript:triggerGoSearch('1');"><fmt:message key="aimir.button.search"/></a></em></li>
            </ul>
        </td>
        </tr>
        </table>
        <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
	    	<div id="treeDivA"></div>
	    </div>
    </div>  


    <div id="triggerListStep1Div">
	    <!-- sub title -->
	    <div class="mgn_t20"><em class="blu12_bold">Total.</em>1  /  23 Page </div>
	    <!--// sub title -->
	    	<!-- table -->
	    
                <table   class="fw_table">
                
                <tr>
                <th rowspan="2">No</th>
                <th rowspan="2">Trigger ID</th>
                <th rowspan="2">Equip Kind</th>
                <th rowspan="2">Equip Type</th>
                <th colspan="3">Source Version</th>
                <th colspan="3">Target Version</th>
                <th  colspan="5">State</th>
               </tr>
                <tr>
                <td class="subtit">H/W Version</td>
                <td class="subtit">F/W Version</td>
                <td class="subtit">F/W Build</td>
                <td class="subtit">H/W Version</td>
                <td class="subtit">F/W Version</td>
                <td class="subtit">F/W Build</td>
                <td class="subtit">Total</td>
                <td class="subtit">Success</td>
                <td class="subtit">Executing</td>
                <td class="subtit">Cancel</td>
                <td class="subtit">Error</td>
                </tr>
                <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                </tr>
                </table>
    		<!--// table-->
     </div>
    
        <!-- paginate -->
     <div  id="triggerListStep1PageBarDiv" class="mgn_t10 mgn_b20">
        <table class="fw_page" align="center">
        <tr>
            <td class="pv"><a href="#"><span class="prev2"> </span></a></td>
            <td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
            <td class="on"><a href="#">1</a></td>
            <td class="nv"><a href="#"><span class="next"> </span></a></td>
            <td class="nnv"><a href="#"><span class="next2"> </span></a></td>
        </tr>
        </table>
     </div>
        <!--// paginate -->
    
    
      <div id="triggerListStep2Div" class="fw_viewbox">
        <table class="fw_basic_gry">
        <colgroup>
            <col width="15%"/>
            <col width="42%"/>
            <col width="15%"/>
            <col width=""/>
            </colgroup>
        <tr>
        <th>Trigger ID / MCU ID</th>
        <td></td>
        <th>State / Operation</th>
        <td>        
        <select name="select5" id="select5" >
        <option selected="selected">All</option>
        </select>
         / 
         <em class="fw_btn_org"><a href="#">Retry</a></em>
        </td>
        </tr>
        <tr>
        <th>Start Time / End Time</th>
        <td></td>
        <th>MCU Type / Location</th>
        <td></td>
        </tr>
        <tr>
        <th>Source Firmware</th>
        <td></td>
        <th>Target Firmware</th>
        <td></td>
        </tr>
        <tr>
        <th>Source HW/FW/Build Ver</th>
        <td></td>
        <th>Target HW/FW/Build Ver</th>
        <td></td>
        </tr>
        </table>
      </div>  
            

     <!-- sub title -->
     <!-- div class="mgn_t20"><em class="blu12_bold">Total.</em>  1 /  0 Page</div-->
     <!--// sub title -->
    	<!-- table -->
    	<div id="triggerListStep3Div" class="mgn_t20">
         <table   class="fw_table">
        <colgroup>
        <col width="50" />
        <col width="25%" />
        <col width="" />
        <col width="" />
         <col width="" />
        <col width="" />
        <col width="" />
        </colgroup>
        <tr>
        <th>No</th>
        <th>ID</th>
        <th>State</th>
        <th>Trigger Step</th>
        <th>Trigger Count</th>
        <th>Error</th>
        <th>Retry</th>
       </tr>
        <tr>
        <td>-</td>
        <td>-</td>
        <td>-</td>
        <td>-</td>
        <td>-</td>
        <td>-</td>
        <td><em class="fw_button_gry"><a href="#">Wait</a></em></td>
         </tr>
        </table>
        </div>
   		<!--// table-->
   
        <!-- paginate -->
        <!-- div class="mgn_t10">
        <table class="fw_page" align="center">
        <tr>
            <td class="pv"><a href="#"><span class="prev2"> </span></a></td>
            <td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
            <td class="on"><a href="#">1</a></td>
            <td class="nv"><a href="#"><span class="next"> </span></a></td>
            <td class="nnv"><a href="#"><span class="next2"> </span></a></td>
        </tr>
        </table>
       </div-->
       <!--// paginate -->
    </div>
 	<!--// menu 2 -->
 	
 	<form name="TriggerListForm">
 		<input type="hidden" name="realSearchFromDate" id="realSearchFromDate" ></input>
 		<input type="hidden" name="realSearchToDate" id="realSearchToDate" ></input>
          <input type='hidden' id='sLocationId' value=''></input><!-- 실제 LocationID값이 저장됨 -->
 	</form>
    
    <!-- menu 3-->
	<div  id="m3" class="container" style="display:none;">
	 	<!-- left tree -->
        <div class="lnb w230" style="height:351px">
        <form id="searchForm2" onSubmit="return false;">
            <!-- search -->
            <div class="search-s1 mgn_t10 mgn_lft12">
                <ul class="w202">
                <li class="search-s1-input"><input name="searchWord2" id='searchWord2' type="text" value="Search"></li>
                <li class="search-s1-btn"><a href="#" id="search2"></a></li>
                </ul>
                <ul class="search-s1-result2">
                <li><font id="searchCount2"></font></li>
                </ul>
            </div>
           <div class="clear"></div>
            <!-- 제조사별 장비 Tree -->
            <fieldset id="vendor-tree" style="width:230px !important"><div id="basic_html2"></div></fieldset>
        </form>	 
        </div>	
	 	<!--// left tree -->
     
	 <!-- contents -->
	 <div class="content mgn_lft250" >
		<div class="width_auto mgn_b20">
		
		<div class="width_auto" id ="frmlistDiv2">
		  <table class="fw_table">
            <colgroup>
            <col width="50"/>
            <col width="8%"/>
            <col width="10%"/>
            <col width="8%"/>
            <col width="10%"/>
            <col width="10%"/>
            <col width="10%"/>
            <col width=""/>
            <col width="8%"/>
            <col width="8%"/>
            </colgroup>
            <tr>
              <th>No</th>
              <th>Equip Kind</th>
              <th>Equip Type</th>
              <th>H/W</th>
              <th>F/W</th>
              <th>FW Rev</th>
              <th>Title</th>
              <th>Release D...</th>
              <th>Write Date</th>
              <th>Writer</th>
            </tr>
            <tr>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
            </tr>
          </table>
		</div>
        <div class="width_auto" style="overflow:auto" >
		
        <!-- paginate -->
		   <div class="float_left pagenum mgn_t10 mgn_b15" id ="frmlistPagingDiv2" >
			<table class="fw_page" align="center">
			<tr>
				<td class="pv"><a href="#"><span class="prev2"> </span></a></td>
				<td class="ppv"><a href="#"><span class="prev"> </span></a></td>   
				<td class="on"><a href="#">1</a></td>
				<td class="nv"><a href="#"><span class="next"> </span></a></td>
				<td class="nnv"><a href="#"><span class="next2"> </span></a></td>
			</tr>
			</table>
		  </div>
		  <!--// paginate -->
          
            <div class="float_right pagenum_btn mgn_t10" style="display:none;">
            <em class="fw_button"><a href="#">Add</a></em>
            <em class="fw_button"><a href="#">Modify</a></em>
            <em class="fw_button"><a href="#">Delete</a></em>
            </div>
        </div>
        <form id="form_input" name="form_input" method="post" enctype="multipart/form-data">
        <!-- sub tab3 -->
        <div>
          <table class="fw_table_write">
            <caption><span class="ico_title"></span>Add F/W</caption>
            <colgroup>
            <col width="20%"/>
            <col width=""/>
            <col width="15%"/>
            <col width=""/>
            </colgroup>
          
            <tr>
            <th>Vendor</th>
            <td><input type="text" id="addFrmVendor" name="addFrmVendor" ></input></td>
            <th>model</th>
            <td><input type="text" id="addFrmModel" name="addFrmModel" ></input></td>
            </tr>
            <tr>
            <th><span class="org_bold">*</span>Binery Version</th>
            <td colspan="3"><input type="file" id="newfile" name="newfile" style="width:57%;height:20px;"  /></td>
            
            </tr>
             <tr>
            <th><span class="org_bold">*</span>H/W Version</th>
            <td><input type="text" id="addFrmHwVersion" name="addFrmHwVersion"></input></td>
            <th><span class="org_bold">*</span>FW Rev</th>
            <td><input type="text" id="addFrmBuild" name="addFrmBuild"></input></td>
            </tr>
             <tr>
            <th><span class="org_bold">*</span>F/W Version</th>
            <td colspan="3"><input type="text" id="addFrmSwVersion" name="addFrmSwVersion"></input></td>
            </tr>
             <tr>
            <th><span class="org_bold">*</span>Release Date</th>
            <td colspan="3"> <input id="sInstallStartDate" name="sInstallStartDate" class="day" type="text" readonly="readonly">
            </tr>
             <tr>
			<!-- Constraints 있던 자리 -->
           </tr>
            
           <tr>
           <th>Title</th>
           <td colspan="3"><input type="text" name="addFrmTitle" id="addFrmTitle"  style="width:90%"/></td>
           </tr>
           <tr>
           <th>Content</th>
           <td colspan="3" class="space"><textarea name="addFrmContent" id="addFrmContent" rows="5" style="width:95%"></textarea></td>
           </tr>
          </table>

          <div id="firmwareAddBtn">
          <div class="btn_right" id="addFrm_Submit" style="display: none;"><em class="fw_button"><a href="#"  id="add_submit">Firmware Add</a></em> <!-- em class="fw_button"><a href="#">취소</a></em --></div>
          </div>
        </div>
        <!--// sub tab3 -->
        <input type="hidden" name="fileFormbuild" value=""></input>
        <input type="hidden" name="isOverwrite"  id="isOverwrite" value="false"></input>
 		<input type="hidden" name="isOverwriteCheck"  id="isOverwriteCheck" value=""></input>
 		<input type="hidden" name="addFrmArm"  id="addFrmArm" value="0"></input>
 		<input type="hidden" name="addFrmEquip_type"  id="addFrmEquip_type" value=""></input>
 		<input type="hidden" name="addFrmEquip_kind"  id="addFrmEquip_kind" value=""></input>
 		<input type="hidden" name="realInstallStartDate"  id="realInstallStartDate" value=""></input>
 		<input type="hidden" name="addFrmSupplierId"  id="addFrmSupplierId" value=""></input>
 		<input type="hidden" name="addFrmFirmwareid"  id="addFrmFirmwareid" value=""></input>
 		<input type="hidden" name="model_id"  id="model_id" value=""></input>
        </form>
	 </div>
	 </div>	
	 <!--// contents -->
	</div>
    <!--// menu 3 -->
   </div>
   <!--//all tab -->
  
 </div>
 <input type="hidden" id="hiddenfileA" value=""></input>
 <input type="hidden" id="hiddenfileB" value=""></input>
 
 <script language='javascript'><!--
      $(document).ready(function(){
         //----------------------------- 파일업로드 설정 부분---------------------------------    
        $("#form_input").ajaxForm({
            success:function(responseText, statusText, xhr, $form){
                //alert(responseText);
                if(responseText.indexOf("Error") > 0){
                    //alert("FileUpload시 에러가 발생하였습니다. \n관리자에게 문의 바랍니다.");
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg10"/>');
                }else{
                    //alert("파일을 성공적으로 등록하였습니다.");
                    //alert(responseText);
                    if(responseText.indexOf("|")>-1){
                    	var splitA = responseText.substring(0,responseText.indexOf("|"));
    					var isOverwriteCheck = responseText.substring(responseText.indexOf("|")+1,responseText.length);

                        if( $.trim(isOverwriteCheck) == "true"){
    						//if(confirm("펌웨어와 같은 폴더가 존재합니다. 파일을 덮어쓸까요?")){
    						if(confirm("Same foldername already exists. Overwrite?")){
    								$("#isOverwriteCheck").val("true");
    								$("#isOverwrite").val("true");
    								$("#add_submit").click();
    					    }
                        }else if( $.trim(isOverwriteCheck) == "endSuccess"){
    					    //alert("firmware등록 호출할것.. diff 작업도 같이 하것..");
    					    //alert("등록이 완료 되었습니다!");
    					    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg04"/>');
    					    addFWReset();
    					    getFirmwareList(top_node,"1","D","basic_html2");
							//return;
						}
                    }else{
                        //alert(responseText);                        
                        document.form_input.reset();
						return; 
                    }
                }
            }
        });

        $("#form_input").submit(function(){return false;});//일단 전송중지 시킨다

        //파일전송부분
        $("#add_submit").click(function(){
            //입력값 null 체크 
        	if(!addFrmSubmitCheck()) return;

            /*
		        파일 업로드 확장자
			mcu = tar.gz
			modem = ebl
			arm = bin
		    */
			var checkFileName = $("#newfile").val();
			var checkVal = "tar.gz";
			if(top_equip_kind == "MCU"){
				checkVal = "tar.gz";
			}else if(top_equip_kind == "Modem"||(top_equip_kind.indexOf("Codi") > -1)){
				if(top_equip_type == "MMIU") {
					checkVal = "dwl";
				} else if(top_equip_type == "PLCIU" || top_equip_type == "SubGiga") {
					checkVal = "bin";
				} else {
					checkVal = "ebl";
				}
				
			}else if(top_equip_kind == "arm"){
				checkVal = "bin";
			}
			
		    if(checkFileName.indexOf(checkVal) < 0){
			    var msg = '<fmt:message key="aimir.firmware.msg04"/>';
			    msg = msg.replaceAll("{$file}",checkVal);
				Ext.Msg.alert('<fmt:message key='aimir.message'/>',msg);
				return; 
			}

		    if($("#isOverwriteCheck").val() == ""){
//	            if(confirm("펌웨어를 등록 하시겠습니까?")){
	            if(confirm('<fmt:message key="aimir.firmware.msg12"/>')){
	                $("#form_input").attr("action","${ctx}/gadget/device/firmware/file_upload.do");
	                $("#form_input").submit();
	            }
			}else{
                $("#form_input").attr("action","${ctx}/gadget/device/firmware/file_upload.do");
                $("#form_input").submit();
			}
        	
        });
      });

      function addFrmSubmitCheck(){
    	  $("#addFrmEquip_kind").val(top_equip_kind);
    	  $("#addFrmEquip_type").val(top_equip_type);
    	  $("#model_id").val(top_model_id);

       	  var checkFileName = $("#newfile").val();
    	  
    	  /*if($("#addFrmVendor").val() == "" || $("#addFrmModel").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>',"펌웨어 리스트를 조회하여 원하는 장비의 H/W를 클릭하세요.");
    			$("#addFrmVendor").focus();
    			return false;
          }*/          
    	  if($("#newfile").val() == "" ){
			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg13"/>');
			$("#newfile").focus();
			return false;
    	  }
    	  if($("#addFrmHwVersion").val() == "" ){
  			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg14"/>');
  			$("#addFrmHwVersion").focus();
  			return false;
      	  }
    	  if($("#addFrmSwVersion").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg15"/>');
    			$("#addFrmSwVersion").focus();
    			return false;
          }
          //mmiu,ieiu일경우 build를 체크하지 말 것 
    	  /*if($("#addFrmBuild").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Fw Rev을 입력하세요.");
    			$("#addFrmBuild").focus();
    			return false;
          }*/
    	  if($("#sInstallStartDate").val() == "" ){
  			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg16"/>');
  			$("#sInstallStartDate").focus();
  			return false;
          }
      	  /*if($("#addFrmConstBuild").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Constraints (H/W F/W Build)를  입력하세요.");
    			$("#addFrmConstBuild").focus();
    			return false;
          } */           
    	  if($("#addFrmTitle").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg17"/>');
    			$("#addFrmTitle").focus();
    			return false;
          }
      	  if($("#addFrmContent").val() == "" ){
    			Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg18"/>');
    			$("#addFrmContent").focus();
    			return false;
          }
    	return true;
      }
	  //Add F/W 화면의 데이터를 초기화 시킨다.
      function addFWReset(){

    	  $("#addFrmVendor").val("");
    	  $("#addFrmModel").val("");
    	  $("#addFrmHwVersion").val("");
    	  $("#addFrmSwVersion").val("");
    	  $("#addFrmBuild").val("");
    	  $("#sInstallStartDate").val("");
    	  //$("#addFrmConstBuild").val("");
    	  $("#addFrmTitle").val(""); 
    	  $("#addFrmContent").val("");    	
    	  $("#addFrmFirmwareid").val("");    	
    	  $("#isOverwrite").val("");    	
    	  $("#isOverwriteCheck").val("");
    	  //document.form_input.newfile.select();
    	  //document.selection.clear();
      	  document.form_input.reset();
    	    
      }

    //Add F/W 화면의 리스트 제목을 클릭시 내용을 보여주는 modal window
      function viewFrmContent(brdContents){
    	 $('#mw_temp3').css('display','block');

    	 var temp_tag = "<table cellpadding=\"1\" cellspacing=\"1\" style=\"table-layout:fixed;word-break:break-all;\" width=\"100%\" >   "
						+ "<tr><td>" 
						+ brdContents 
						+ "</td></tr></table>";
        	 
       	 $("#mw_temp4").html(temp_tag); 
      }

	   //trigger list equiptype Change
      function equipTypeChange(){
          var equipType = $('#trequip_kind option:selected').text();
          if( equipType == "MCU" ){
        	  initSingleRegMCU();
          }else if( equipType == "Modem" ){
        	  initSingleRegModem();
          }else if( equipType == "Codi" ){
			  initSingleRegCodi();
          }
          resetInnerHtml('triggerListStep2Div');
          resetInnerHtml('triggerListStep1Div');
      }

	  function initSingleRegCodi(){
          $('#singleEquipType').initSelect();
          $('#singleEquipType').selectbox();
          $('#regLogVendor').initSelect();
          $('#regLogVendor').selectbox();
          $('#sModel').initSelect();
          $('#sModel').selectbox();
	  }
	   
      function initSingleRegMCU(){
          //MCU
          $.getJSON('${ctx}/gadget/system/getChildCode.do'
                  , {'code' : '1.1.1'}
                  , function (returnData){
//                      $('#singleEquipType').pureSelect(returnData.code);
                      var result = returnData.code;
                      var arr = Array();
                      for (var i = 0; i < result.length; i++) {
                          var obj = new Object();
                          obj.name=result[i].descr;
                          obj.id=result[i].id;
                          arr[i]=obj;
                      };
                      
                      $('#singleEquipType').loadSelect(arr);
                      $('#singleEquipType').selectbox();
                  });
      }
      
      function initSingleRegModem(){
          // 모뎀유형 초기화
          $.getJSON('${ctx}/gadget/system/getChildCode.do'
                  , {'code' : '1.2.1'}
                  , function (returnData){
//                      $('#singleEquipType').pureSelect(returnData.code);
                      var result = returnData.code;
                      var arr = Array();
                      for (var i = 0; i < result.length; i++) {
                          var obj = new Object();
                          obj.name=result[i].descr;
                          obj.id=result[i].id;
                          arr[i]=obj;
                      };
                      $('#singleEquipType').loadSelect(arr);
                      $('#singleEquipType').selectbox();
                  });
      }  

      function getVendorListByEquipType() {
  	    $.getJSON('${ctx}/gadget/device/getVendorListBySubDeviceType.do'
	            , { 'deviceType' : $('#trequip_kind option:selected').text()
	               ,'subDeviceType' : $('#singleEquipType').val()}
	            , function (returnData){
//	                $('#regLogVendor').pureSelect(returnData.deviceVendor);
	                $('#regLogVendor').loadSelect(returnData.deviceVendor);
	                $('#regLogVendor').selectbox();
	            });
  	  		//getDeviceModelsByVenendorId();
         };

       function getDeviceModelsByVenendorId() {
             $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do'
                     , {'vendorId' : $('#regLogVendor').val() }
                     , function (returnData){
//                         $('#sModel').pureSelect(returnData.deviceModels);
                         $('#sModel').loadSelect(returnData.deviceModels);
                         $('#sModel').selectbox();
                     });
       };         

       function triggerGoSearch(varcurPage){
    	   var trEquip_kind = $('#trequip_kind').val();
    	   var trEquip_Type = $('#singleEquipType').val();
    	   var trVendor = $('#regLogVendor').val();
    	   var trModel = $('#sModel').val();
    	   var trState = $('#trState').val();
    	   var trTriggerID = $('#trTriggerID').val();
    	   var trEquipID = $('#trEquipID').val();
    	   var trfromDate = $('#realSearchFromDate').val();
    	   var trtoDate = $('#realSearchToDate').val();
    	   var trLocationId = $('#sLocationId').val();
           resetInnerHtml('triggerListStep1Div');
           resetInnerHtml('triggerListStep2Div');
           resetInnerHtml('triggerListStep3Div');           
    	   if(triggerSearchCheck()==true){
    		   $.getJSON('${ctx}/gadget/device/firmware/getTriggerListStep1.do', {		
    		   					trEquip_kind:trEquip_kind,
    		   					trEquip_Type:trEquip_Type,
    		   					trVendor:trVendor,
    		   					trModel:trModel,
    		   					trState:trState,
    		   					trTriggerID:trTriggerID,
    		   					trEquipID:trEquipID,
    		   					trfromDate:trfromDate,
    		   					trtoDate:trtoDate,
    		   					trLocationId:trLocationId,
    		   					curPage:varcurPage
    		   			 },
    		   			function(json) {
    		   					if(json.rtnStr == null){
    		   						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
    		   					}else if(json.rtnStr == "success"){
    		   						$('#triggerListStep1Div').html(json.triggerListStep1InnerHtml);
    		   						$('#triggerListStep1PageBarDiv').html(json.paingStr);
    		   					}
    		   					
    		   			});    
           }
    	   //alert("trEquip_kind="+trEquip_kind+" trEquip_Type="+trEquip_Type+" trVendor="+trVendor+" trModel="+trModel+" trState="+trState+" trTriggerID="+trTriggerID+" trEquipID="+trEquipID+" trfromDate="+trfromDate+" trtoDate="+trtoDate+" trLocationId="+trLocationId);
    	   
       }

       function triggerSearchCheck(){
    	   var trEquip_kind = $('#trequip_kind').val();
    	   var trEquip_Type = $('#singleEquipType').val();
    	   var trVendor = $('#regLogVendor').val();
    	   var trModel = $('#sModel').val();
    	   var trState = $('#trState').val();
    	   var trTriggerID = $('#trTriggerID').val();
    	   var trEquipID = $('#trEquipID').val();
    	   var trfromDate = $('#realSearchFromDate').val();
    	   var trtoDate = $('#realSearchToDate').val();
    	   var trLocationId = $('#sLocationId').val();
    	   
    	   //if(trLocationId=="" || trLocationId == null){
		   //	alert('<fmt:message key="aimir.firmware.msg21"/>');
		   //	return false;
           //}

    	  if(trfromDate=="" || trfromDate == null){
    		   Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg16"/>');
				return false;
          }

    	  if(trtoDate=="" || trtoDate == null){
    		   Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg16"/>');
				return false;
          }
          return true;
       }

   	   var triggerListStep3DivAll = "";
   	   var triggerListStep3DivSucc = "";
   	   var triggerListStep3DivCancel = "";
   	   var triggerListStep3DivError = "";
   	   var triggerListStep3DivExec = "";
       function TriggerInfoSetp2(trTriggerID,trLocationId, trEquip_Kind, trExec, trequip_type){
   		   $.getJSON('${ctx}/gadget/device/firmware/getTriggerListStep2.do', {		
   		   					trTriggerID:trTriggerID,
   		   					trLocationId:trLocationId,
   		   					trEquip_Kind:trEquip_Kind,
   		   				    trExec:trExec,
   		   				    trequip_type:trequip_type,
   		   				    cmdAuth:cmdAuth
   		   			 },
   		   			function(json) {
   		   					if(json.rtnStr == null){
   		   						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
   		   					}else if(json.rtnStr == "success"){
   		   						$('#triggerListStep2Div').html(json.triggerListStep2InnerHtml);
   		   						$('#triggerListStep3Div').html(json.triggerListStep3InnerHtml);
   		   					    triggerListStep3DivAll = json.triggerListStep3InnerHtml;
	   		   			   	    triggerListStep3DivSucc = json.triggerStep3ListDivSucc;
		   		   		   	    triggerListStep3DivCancel = json.triggerStep3ListDivCancell;
		   		   		   	    triggerListStep3DivError = json.triggerStep3ListDivError;
		   		   		   	    triggerListStep3DivExec = json.triggerStep3ListDivExec;
   		   					}
   		   			});    
       }      

       function step3SelectChenge(){
           var varval = $("#step3Select option:selected").text();
           if(varval=="All"){
        	   $('#triggerListStep3Div').html(triggerListStep3DivAll);
           }else if(varval=="Succ"){
        	   $('#triggerListStep3Div').html(triggerListStep3DivSucc);
           }else if(varval=="Exec"){
        	   $('#triggerListStep3Div').html(triggerListStep3DivExec);
           }else if(varval=="Cancel"){
        	   $('#triggerListStep3Div').html(triggerListStep3DivCancel);
           }else if(varval=="Error"){
        	   $('#triggerListStep3Div').html(triggerListStep3DivError);
           }
       } 

       function goRedistStep2(varGubun, varTargetFirmware, varTr_id, varBuild,varEquip_kind, 
    	       					vartrigger_step, vartrigger_state, varota_step, varota_state, varmcu_id, varequip_type ){

           $.getJSON('${ctx}/gadget/device/firmware/goRedistStep2.do', {		
        	   		varGubun:varGubun,
        	   		varTargetFirmware:varTargetFirmware,
        	   		varTr_id:varTr_id,
        	   		varEquip_kind:varEquip_kind,
        	   		vartrigger_step:vartrigger_step, 
        	   		vartrigger_state:vartrigger_state, 
        	   		varota_step:varota_step, 
        	   		varota_state:varota_state,
        	   		varmcu_id:varmcu_id,
        	   		varequip_type:varequip_type
 			 },
 			function(json) {
 					if(json.rtnStr == "fail"){
 						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
 					}else if(json.rtnStr == "success"){
 	 					var jsonDistList = json.reDistList;
 	 					var retr_id               = "";
 	 					var remcu_id              = "";
 	 					var reequip_kind          = "";
 	 					var reequip_type          = "";
 	 					var reequip_vendor        = "";
 	 					var reequip_model         = "";
 	 					var reequip_id            = "";
 	 					var resrc_firmware        = "";
 	 					var retarget_firmware     = "";
 	 					var resrcfilename         = "";
 	 					var retargetfilename      = "";
 	 					var retrigger_cnt         = "";
 	 					var retrigger_history     = "";
 	 					var modem_mcu_id          = "";
 	 					var reOtaStep             = "";
 	 					var reTransferType        = "";
 	 					var reIInstallType        = "";
 	 					var reOtaThread           = "";
 	 					var reMaxRetryCount       = "";
 	 					var reMulticastWriteCount = "";
 	 					var reSendEUI642          = "";
 	 					var reSaveHistory         = "";
 	 					var redevice_serial       = "";

 	 					if(varEquip_kind == "Modem"){
 	 						for(var i=0 ; i< jsonDistList.length ; i++){
 	 	 						retr_id = jsonDistList[i][0];
 	 	 						remcu_id = jsonDistList[i][1];
 	 	 	 					reequip_kind = jsonDistList[i][2];
 	 	 	 					reequip_type = jsonDistList[i][3];
 	 	 	 					reequip_vendor = jsonDistList[i][4];
 	 	 	 					reequip_model = jsonDistList[i][5];
 	 	 	 					reequip_id = jsonDistList[i][6];
 	 	 	 					resrc_firmware = jsonDistList[i][7];
 	 	 	 					retarget_firmware = jsonDistList[i][8];
 	 	 	 					resrcfilename = jsonDistList[i][9];
 	 	 	 					retargetfilename = jsonDistList[i][10];
 	 	 	 					retrigger_cnt = jsonDistList[i][11];
 	 	 	 					retrigger_history = jsonDistList[i][12];
 	 	 						modem_mcu_id = jsonDistList[i][14];
 	 	 	 					reOtaStep = '0x1f';
 	 	 	 					reTransferType = '2';
 	 	 	 					reIInstallType = '1';
 	 	 	 					reOtaThread = '1';
 	 	 	 					reMaxRetryCount = '10';
 	 	 	 					reMulticastWriteCount = '1';
 	 	 	 					reSendEUI642 = '';
 	 	 	 					reSaveHistory = '';
 	 	 	 					if(varequip_type=="MMIU"||varequip_type=="IEIU"){
 	 	 						redevice_serial = remcu_id;
 	 	 	 					}else{
 	 	 	 					redevice_serial += ","+jsonDistList[i][13];
 	 	 	 	 				}
 	 						}
 	 	 	 			}else{
 	 						for(var i=0 ; i< jsonDistList.length ; i++){
 	 	 						retr_id = jsonDistList[i][0];
 	 	 						remcu_id = jsonDistList[i][1];
 	 	 	 					reequip_kind = jsonDistList[i][2];
 	 	 	 					reequip_type = jsonDistList[i][3];
 	 	 	 					reequip_vendor = jsonDistList[i][4];
 	 	 	 					reequip_model = jsonDistList[i][5];
 	 	 	 					reequip_id = jsonDistList[i][6];
 	 	 	 					resrc_firmware = jsonDistList[i][7];
 	 	 	 					retarget_firmware = jsonDistList[i][8];
 	 	 	 					resrcfilename = jsonDistList[i][9];
 	 	 	 					retargetfilename = jsonDistList[i][10];
 	 	 	 					retrigger_cnt = jsonDistList[i][11];
 	 	 	 					retrigger_history = jsonDistList[i][12];
 	 	 						//modem_mcu_id = jsonDistList[i][14];
 	 	 	 					reOtaStep = '0x1f';
 	 	 	 					reTransferType = '2';
 	 	 	 					reIInstallType = '1';
 	 	 	 					reOtaThread = '1';
 	 	 	 					reMaxRetryCount = '10';
 	 	 	 					reMulticastWriteCount = '1';
 	 	 	 					reSendEUI642 = '';
 	 	 	 					reSaveHistory = '';
 	 	 						//redevice_serial += ","+jsonDistList[i][13];
 	 						}
 	 	 	 	 	 	}

 	 	 	 	 			
 	 	 	 	 			//if(i == jsonDistList.length){
							//	alert("재배포 완료 여부");
 	 	 	 	 			//}else if(i < jsonDistList.length){
 	 	 	 	 				//varBuild = 3555;//test용
 	 	 	 	 				//varBuild ="111";
 	 	 	 	 				
		 	 	 	 	 	   if(varequip_type == "MMIU" || varequip_type == "IEIU"){
		 	 	 	 	 		   goUrl = "${ctx}/gadget/device/command/cmdReDistributionMMIU.do";
		 	 	 	 	 	   }else{
		 	 	 	 	 		goUrl = "${ctx}/gadget/device/command/cmdReDistribution.do";
							   }

 	 	 					$.getJSON(goUrl, {
 	 	 						loginId:loginId,
 	 	 						top_equip_kind:reequip_kind,
 	 	 						top_equip_type:reequip_type,
 	 	 						supplierId:supplierId,
 	 	 						transferType:reTransferType,
 	 	 						installType:reIInstallType,
 	 	 						otaThreadCount:reOtaThread,
 	 	 						maxRetryCount:reMaxRetryCount,
 	 	 						multicastWriteCount:reMulticastWriteCount,
 	 	 						sendEUI642:reSendEUI642,
 	 	 						saveHistory:reSaveHistory,
 	 	 						fromFileNm:resrcfilename,
 	 	 						toFileNm:retargetfilename,
 	 	 						equipList:encodeURIComponent(reequip_id),
 	 	 						vendor:reequip_vendor,
 	 	 						model:reequip_model,
 	 	 						device_serial:redevice_serial,
 	 	 						tr_id:retr_id,
 	 	 						src_firmware:resrc_firmware,
 	 	 						target_firmware:retarget_firmware,
 	 	 						retrigger_cnt:retrigger_cnt,
 	 	 						retrigger_history:retrigger_history,
 	 	 						remcu_id:remcu_id,
 	 	 						modem_mcu_id:modem_mcu_id
 	 	 						},
 	 	 						function(json) {
 	 	 								if(json.rtnStr == null){
 	 	 									Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg07"/>');
 	 	 								}else{
 	 	 									Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.rtnStr);
 	 	 									self.location.reload();
 	 	 									//changeData("m2");
 	 	 								}
 	 	 						});
 	 	 	 	 	 		//}
 	 	 				//}
 					}
 			});    
       }

       function goCanceldistStep2(varmcuid, vartr_id, varsys_id){
    	   $.getJSON('${ctx}/gadget/device/command/cmdDistributionCancel.do', {		
    		   target:varmcuid,
 					triggerId:vartr_id,
 					loginId:loginId,
 					varsys_id:varsys_id
 			 },
 			function(json) {
 					if(json.rtnStr == null){
 						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
 					}else{
							if(json.rtnStr == ""){
									Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
							}else{
									Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.rtnStr);
									self.location.reload();
									changeData("m2");
							}
 					}
 			});    
       }

       function goStatedistStep2(varmcuid, vartr_id, varsys_id){
    	   $.getJSON('${ctx}/gadget/device/command/cmdDistributionState.do', {		
    		   target:varmcuid,
 					triggerId:vartr_id,
 					loginId:loginId,
 					varsys_id:varsys_id
 			 },
 			function(json) {
 					if(json.rtnStr == null){
 						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
 					}else {
							if(json.rtnStr == ""){
									Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg19"/>');
							}else{
									Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.rtnStr);
									self.location.reload();
									changeData("m2");
							}
 					}
 			});    
       }

       function resetInnerHtml(htmlGubun){
    	   $.getJSON('${ctx}/gadget/device/firmware/resetInnerHtml.do', {htmlGubun:htmlGubun},
 			function(json) {
 					if(json.rtnStr == "null"){
 						//alert("'<fmt:message key="aimir.firmware.msg19"/>'");
 					}else if(json.rtnStr == "success"){
 						$("#"+htmlGubun).html(json.returnInnerHtml);
 					}
 			});    

       }
      
</script>

<div id="mw_temp3" class="mw" style="display: none">
    <div class="bg"></div>
    <div class="fg" style="width:300px; height:150px; overflow:hidden">
        <div class="float_right"><a href="#"  onClick="document.getElementById('mw_temp3').style.display='none'"><span class="ico_close">&nbsp;</span></a></div>
        <div class="width_auto float_none" id="mw_temp4">
         <div class="btn_right"><em class="fw_button"><a href="#">Confirm</a></em>&nbsp;<em class="fw_button"><a href="#">Cancel</a></em></div>
    </div>
    </div>
</div>


</body>
</html>