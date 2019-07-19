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
<title>제조사 모델관리</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tabs.css" rel="stylesheet" type="text/css" media="print, projection, screen"></link>

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>


<script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>

<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.xml_flat.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plugins/jquery.tree.xml_nested.js"></script>
<script type="text/javascript"
	src="${ctx}/js/autocomplete/jquery.autocomplete.js"></script>
<script type="text/javascript">

	/*<![CDATA[*/

	var supplierId;
	var auto;

	$( function() {

		$('a').click( function(event) {
			event.preventDefault();
			return false;
		});


		//공급사 목록 클릭시 제조사/모델 정보 트리 보여주기
		$('#container-1 a').click( function() {
			//$(":input:radio:eq('M')").attr('checked', 'checked');// 공급사 선택시 '장비별' 조회
				//선택된 공급사의 ID는 여러 곳에서 쓰인다.
				//var treeType = $(":input:radio[name='treeType']:checked").val();

				/*
				$(this).toggleClass('selected');
				supplierId = $(this).attr("id");
				$(":input[name='supplierId']").val(supplierId);

				viewTree();
				 */
			});
		$(":input:radio").change(
				function() {

					if ($('#supplierList').val() == '0') {
					    alert('<fmt:message key="aimir.buildingMgmt.supplierFirst"/>');

					} else {
						var treeType = $(
								":input:radio[name='treeType']:checked").val();

						if (treeType == 'M')
							viewTree();
						else
							viewDeviceTree();
					}

				});

		$('#search').click( 
				function() {
			if (!supplierId) {
			    alert('<fmt:message key="aimir.buildingMgmt.supplierFirst"/>');
				return false;
			}
			var searchWord = $(":input[name='searchWord']").val();
			$.tree.focused().search(searchWord);

		});

		$.getJSON('${ctx}/gadget/system/getSupplierList.do', function(json) {
			//alert(json.supplier);
	    	if(json.supplier != null){
				supplierId = json.supplier.id;
				$('#supplierName').text(json.supplier.name);
				$('#supplierList').hide();
				viewTree();
				return;
	    	}

			if(json.supplierList != null){
			   $('#supplierList').loadSelect(json.supplierList);
			}
		});
		$('#supplierList').change( function() {
			supplierId = $(this).val();
			$(":input[name='supplierId']").val(supplierId);
			var treeType = $(":input:radio[name='treeType']:checked").val();
			if (treeType == 'M')
				viewTree();
			else
				viewDeviceTree();
		});

	});
	function setSearchWord(){
		$('#searchWord').click( function() {
			$(this).val('');
			$('#searchCount').text('');
		});

		$('#searchWord').keypress(function(e) {
			 var code=(e.keyCode?e.keyCode:e.which);
			 if(code==13) { // Enter keycode
			    	$.tree.focused().search($(this).val());
			    }
		});
        }
	function viewDeviceTree() {
		auto='';
		$('#searchWord').unautocomplete();
		$('#basic_html').tree( {

			data : {
				type : 'json',
				opts : {
					method : 'GET',
					url : '${ctx}/gadget/system/modeltree.do'
				}
			},
			types	: {
				"default" : {
					deletable : false,
					renameable : true
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

			    					auto= auto+'^'+model.name;

            						children2.push( {
        	    						'data' : {
        	    							'title' : model.name,
        	    							attributes : {
        	    								'href' : '#'
        	    							}
        	    						},
        	    						'attributes' : {
        	    							'id' : 'model_' + model.id,'rel':'model'
        	    						}
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
    	    							'id' : 'model_' + vendor.id,'rel':'vendor'
    	    						},
    	    						'children' : children2
    	    					});
		    				}
	    					auto= auto+'^'+deviceType.name;
	    					children.push( {
	    						'data' : {
	    							'title' : deviceType.name,
	    							attributes : {
	    								'href' : '#'
	    							}
	    						},
	    						'attributes' : {
	    							'id' : 'model_' + deviceType.id,'rel':'model'
	    						},
	    						'children' : children1
	    					});
		    			}
	    				auto= auto+'^'+device.name;
	    				data.push( {
	    					'data' : {
	    						'title' : device.name,
	    						'attributes' : {
	    							'href' : '#'
	    						}
	    					},
	    					'attributes' : {
	    						'id' : 'vendor_' + device.id,'rel':'device'
	    					},
	    					'children' : children
	    				});
	    			}

	    			var autoArr = auto.split('^');
	                $('#searchWord').autocomplete(autoArr);
	                setSearchWord();
	    			return data;

		},
		'onselect' : function(n, t) {
		},
		'onsearch' : function(n, t) {
			t.container.find('.searchResult').removeClass('searchResult');
			n.addClass('searchResult');
			$('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+' : '+n.length);

		}
			}

		});
	}

	function viewTree() {
        auto='';
        $('#searchWord').unautocomplete();
		$('#basic_html').tree( {

			data : {
				type : 'json',
				opts : {
					method : 'GET',
					url : '${ctx}/gadget/system/vendortree.do'
				}
			},


			types	: {
				"default" : {
					deletable : false,
					renameable : true
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
				/*
				'onload' : function(t) {
				    t.settings.data.opts.static = false;
				},
				 */
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

		    				var vendor = jsonData[i].data;
		    				var models = jsonData[i].children;
		    				var childJsonData = jsonData[i].children1;
		    				var children = [];

		    				for ( var j in models) {
		    					var model = models[j];
		    					var children1 = [];
		    					if (childJsonData[j] && (childJsonData[j] != "")) {

		    						for ( var k in childJsonData[j]) {

		    							var childModel = childJsonData[j][k];
		    							auto= auto+'^'+childModel.name;
		    							children1.push( {
		    								'data' : {
		    									'title' : childModel.name,
		    									attributes : {
		    								'href':'#'
		    									}
		    								},
		    								'attributes' : {
		    									'id' : 'config_' + childModel.id,'rel':'config'
		    								}
		    							});

		    						}

		    					}
		    					auto= auto+'^'+model.name;
		    					children.push({'data':{'title':model.name, attributes:{'href':'#' }}, 'attributes':{'id':'model_'+model.id,'rel':'model'}, 'children':children1});

		    				}
		    				auto= auto+'^'+vendor.name;
		    				data.push({'data':{'title':vendor.name, 'attributes':{'href':'#' }}, 'attributes':{'id':'vendor_'+vendor.id,'rel':'vendor'}, 'children':children});
		    			}
		    			var autoArr = auto.split('^');
		                $('#searchWord').autocomplete(autoArr);
		                setSearchWord();
		    			return data;



		},
		'onselect' : function(n, t) {
		},
		'onsearch' : function(n, t) {
			t.container.find('.searchResult').removeClass('searchResult');
			n.addClass('searchResult');
			$('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+' : '+n.length);

		}
			}

		});
	}

	/*]]>*/
</script>


</head>
<body>

<!-- Supplier Lists (S) -->
<div class="search-bg-basic">
	<ul class="basic-ul">
		<li class="basic-li graybold11pt withinput" id="supplierName"></li>
		<li class="basic-li"><select id="supplierList" name="supplierList"></select></li>
	</ul>
</div>
<!-- Supplier Lists (E) -->


<!-- Gadget Body (S) -->
<div class="gadget_body">


	<div id="container-0">
		<div class="searchoption select-treetype">
			<ul>
				<li><input 	class="radio_space" name="treeType" type="radio" value="V"></li>
				<li 		class="blue11pt margin-t3px"><fmt:message key="aimir.sortby.equip" /></li>
				<li><input 	class="radio_space" name="treeType" type="radio" value="M" checked></li>
				<li 		class="blue11pt margin-t3px"><fmt:message key="aimir.sortby.vendor" /></li>
			</ul>
		</div>
	</div>


	<div class="clear-width100"></div>
    <div class="bodyleft_devicemodel_mini">


		<!-- 제조사/장비별 Tree -->
		<div id="container-2">
			<form id="searchForm" onsubmit="return false;" style="height:238px"><!-- search -->
				<div class="search-s1">
					<ul>
						<li class="search-s1-input"><input name="searchWord" id='searchWord' type="text" value="Search"></li>
						<li class="search-s1-btn"><a href="#" id="search"></a></li>
					</ul>
					<ul class="search-s1-result2 margin-t3px">
						<li><font id="searchCount"></font></li>
					</ul>
				</div>

				<!-- 제조사별 장비 Tree -->
				<fieldset id="vendor-tree" style="height:180px !important">
					<div id="basic_html"></div>
				</fieldset>

			</form>
		</div>
		<!-- 제조사/장비별 Tree (E) -->

    </div>


</div>
<!-- Gadget Body (E) -->


</body>
</html>

