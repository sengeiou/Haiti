/*!
 * Javascript Framework For Gadget
 * Copyright(c) NURI TELECOM CORP.
 */

Ext.gadget.Manager = function() {
	var viewport;
	var headerpanel;
	var centerpanel;
	var tabpanel;
	var jsondata;
	var pub;
	
	//2011-04-01
	var language;

	function getPortal(uid, title, column, json) {
		var tools = new Array();
		tools.push({
			id:'help',qtip: 'Help Panel',
			handler: function(e, target, panel){
//				window.open('../gadget/help/index.html?usermgmt.htm','window','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
				var gadgetCode = panel.gadgetCode;

				errorHelpPageToDefaultPage(language,gadgetCode); // help 링크를 확인해서 오류가 났을경우 default 페이지로 이동한다.	
			}
		});
		tools.push({
			id:'refresh',qtip: 'Reload Panel',
			handler: function(e, target, panel){
				panel.setHeight(panel.getHeight());
				panel.getUpdater().refresh();
			}
		});

		// DashBoard가 My Report인경우, 디폴트 풀 화면으로 제공하므로 Max와 close 기능은 제공하지 않는다.
		if(json[0].gadgetCode != 'gadget.hems.myreport') {
			tools.push({
				id:'maximize', qtip: 'Maximize Panel',
				handler: function(e, target, panel){
					getExtMaximizeColumn(panel.uid, panel.type);
				}
			});
			tools.push({
				id:'close', qtip: 'Close',
				handler: function(e, target, panel){
					deleteItem(panel.uid);
					panel.ownerCt.remove(panel, true);
				}
			});
		}

		// 메인  패널 - 시작
		// Portal 패널은 PortalColumn이 모여 이루어진다.
		// 가젯을 모아 PortalColumn을 만들고 PartalColumn을 모아 Protal패널을 구성하게 된다.
		var portalColumnArr = new Array();

		var colMax = column;
		var n=1/colMax;
		var colWidth = n.toFixed(2);
		for(var i=0; i<colMax; i++) {
			// DashBoard가 My Report인 경우는 가젯을 풀 화면으로 로드한다. updated by eunmiae 07/18/2011
			if(json[0].gadgetCode == "gadget.hems.myreport") {
				portalColumnArr.push(new Ext.ux.PortalColumn({columnWidth:1, style:'margin:10px 0 10px 10px'}));
			} else {
				portalColumnArr.push(new Ext.ux.PortalColumn({columnWidth:colWidth, style:'margin:10px 0 10px 10px'}));
			}

		}

		// Position 에 따른 정렬
		json.sort(function(obj1, obj2){return obj1.position<obj2.position?-1:(obj1.position>obj2.position?1:0);});

		for(var i in json) {
			if (json[i].title != undefined) {
				json[i].tools = tools;
				json[i].listeners = {
					'expand': function(e) {
						if (Ext.isIE) e.getUpdater().refresh();
					}
				};
				if (json[i].xtype == 'iframeportlet') {	// 기존의 MIF처리하던 로직을 변경하여 iframeportlet의 경우 Controller를 이용 직접 iframe으로 변환
					json[i].xtype = 'portlet';
					json[i].autoLoad = {url:'../ajax/getIFrame.do?url='+json[i].defaultSrc};
				}
				if (json[i].help) {	// help에 대한 정보가 있을 경우 help버튼의 URL을 변경하여 준다.
					json[i].tools[0].handler = function(e, target, panel){
						window.open(json[i].help,'help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
					}
				}
				var tmpColumnIndex = json[i].columnIndex;
				if (tmpColumnIndex>colMax-1) {
					portalColumnArr[colMax-1].add(json[i]);
				} else {
					portalColumnArr[tmpColumnIndex].add(json[i]);
				}
			}
		}

		var portalPanel = new Ext.ux.Portal();
		portalPanel.add(portalColumnArr);
		portalPanel.addListener(
			'drop', function(e){
				var tp = tabpanel.getActiveTab().uid;
				var pt = e.portal.items;
				var json=convertItemsPosition(tp, pt);
				e.panel.getUpdater().refresh();
				sendJsonForPosition(json);
			}
		);
		// 메인  패널 - 끝
		
		var panel = new Ext.Panel({
			title:title,
			uid:uid,
			autoHeight: true
		});
		panel.add(portalPanel);
	
		return panel;
	};
	function convertItemsPosition(tabuid, json) {
		var sendjson = new Object();
		sendjson.tuid=tabuid;
		sendjson.data=new Array();
		
		for(var j=0; j<json.items.length; j++) {
			var indata = json.items[j].items.items;
			for(var i=0; i<json.items[j].items.items.length; i++) {
				var temp = new Object();
				temp.uid=indata[i].uid;
				temp.columnIndex=j;
				temp.position=i;
				sendjson.data.push(temp);
			}
		}
		return sendjson;
	};
	function sendJsonForPosition(json) {
		var tuid = json.tuid;
		var data = '{data:'+Ext.util.JSON.encode(json.data).toString()+'}';
		Ext.Ajax.request({
			url : '../ajax/setItemsPosition.do', 
			method: 'POST',
			params: {tuid:tuid, data: data},
			success: function (result, request) {
			},
			failure: function (result, request) {
			}
		});
	}
	function deleteItem(uid) {
		Ext.Ajax.request({
			url : '../ajax/deleteItem.do', 
			method: 'POST',
			params: {uid: uid},
			success: function (result, request) {
			},
			failure: function (result, request) {
			}
		});
	}
	function getMaximizePanel(json) {
		var tools = new Array();
		tools.push({
			id:'help',qtip: 'Help Panel',
			handler: function(e, target, panel){
//				window.open('../gadget/help/index.html?usermgmt.htm','help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
				//2011-04-01 kskim
				var gadgetCode = panel.gadgetCode;
				
				errorHelpPageToDefaultPage(language,gadgetCode); // help 링크를 확인해서 오류가 났을경우 default 페이지로 이동한다.	
			}
		});
		tools.push({
			id:'refresh',qtip: 'Reload Panel',
			handler: function(e, target, panel){
				panel.getUpdater().refresh();
			}
		});
		tools.push({
			id:'minimize', qtip: 'Minimize Panel',
			handler: function(e, target, panel){
				panel.remove();
				Ext.gadget.Manager.setTabMinimize();
			}
		});

		// 메인  패널 - 시작
		// Portal 패널은 PortalColumn이 모여 이루어진다.
		// 가젯을 모아 PortalColumn을 만들고 PartalColumn을 모아 Protal패널을 구성하게 된다.
		var portalColumnArr = new Array();

		portalColumnArr.push(new Ext.ux.PortalColumn({columnWidth:1, style:'margin:10px 10px 10px 10px'}));
		for(var i in json) {
			if (json[i].title != undefined) {
				json[i].tools = tools;
				if (json[i].xtype == 'iframeportlet') {	// 기존의 MIF처리하던 로직을 변경하여 iframeportlet의 경우 Controller를 이용 직접 iframe으로 변환
					json[i].xtype = 'portlet';
					json[i].autoLoad = {url:'../ajax/getIFrame.do?url='+json[i].defaultSrc};
				}
				if (json[i].help) {	// help에 대한 정보가 있을 경우 help버튼의 URL을 변경하여 준다.
					json[i].tools[0].handler = function(e, target, panel){
						window.open(json[i].help,'help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
					}
				}
				portalColumnArr[0].add(json[i]);
			}
		}
		
		var portalPanel = new Ext.ux.Portal();
		portalPanel.add(portalColumnArr);
		// 메인  패널 - 끝

		return portalPanel;
	};
	//private
	function setHeaderPanel(el, id) {
		var thema = '';
		thema += '<form id="styleswitcher">';
		thema += '<select name="styleswitcher_select" id="styleswitcher_select" class="select-colortheme">';
		thema += '<option value="blue" >Blue theme</option>';
		thema += '<option value="green">Green theme</option>';
		thema += '<option value="orange" selected="true">Orange theme</option>';
		thema += '</select>';
		thema += '</form>';

		var html = '<div id="dark_head">';
		html +='<div id="logo"></div>';
		html +='<ul id="dinf_box">';
		html +='<li id="dinf_01">'+id+'</li><li id="dinf_line"></li>';
		html +='<li id="dinf_02"><a href="javascript:setActiveTab(\'account-setting\');">Account</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_03"><a href="javascript:logout();">Logout</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_05"><a href="javascript:;" onClick="#">Help</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_04">'+thema+'</li>';
		html +='</ul>';
		html +='</div>';
		el.body.update(html);
		
		Ext.get('styleswitcher_select').on('change',function(e,select){
	        var name = select[select.selectedIndex].value;
	        setActiveStyleSheet(name);
	    });
	    Ext.get('styleswitcher_select').dom.value=title;
	    
	    
	}
	//private
	function getIFramePanel(id, title, height, url) {
		var panel = new Ext.Panel({
			id: id,
			title: title,
			autoLoad: {url:'../ajax/getIFrame.do?url='+url},
			height: height,
			scroll: false
		});
		return panel;
	}
	//private
	function addPanel(el, o) {
		el.add(o);
	}
	function setActiveTab(id){
		tabpanel.remove("gadget-setting");
		tabpanel.remove("account-setting");

		if (id=="gadget-setting") {
			addPanel(tabpanel, getIFramePanel('gadget-setting', 'Gadget Setting', 660, 'gadget_setting_max.do'));
			tabpanel.hideTabStripItem("gadget-setting");
			tabpanel.setActiveTab("gadget-setting");
		} else if (id=="account-setting") {
			addPanel(tabpanel, getIFramePanel('account-setting', 'Account Setting', 1200, 'accountSetting_customer.do'));
			tabpanel.hideTabStripItem("account-setting");
			tabpanel.setActiveTab("account-setting");
		}else {
			tabpanel.setActiveTab(id);
		}
	}
	pub = {
		init: function() {	// 초기화
			Ext.QuickTips.init();
			viewport = new Ext.Viewport({
				layout : 'ux.center',
				items:[
				       mainpanel= new Ext.gadget.MainPanel()
				]
			});
			headerpanel= new Ext.gadget.HeaderPanel();
			mainpanel.add(headerpanel);
			tabpanel = new Ext.gadget.TabPanel({id: 'tab-panel'});
			mainpanel.add(tabpanel);
			viewport.doLayout();
			viewport.syncSize();
		},
		setHeader: function(id) {	// 로그인 아이디를 받아 상단 화면을 구성
			setHeaderPanel(headerpanel, id);

			// 2011-04-01 kskim
			//상단 help 링크 언어별로 수정
			document.getElementById('dinf_05').innerHTML=
				"<a href=\"javascript:;\" onClick=\"window.open(\'../gadget/help/customer/"+
				language+
				"/index.html\',\'help\',\'location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes\');\">Help</a>";
			

		},
		setTabAll: function(jsonArray) {
			for(var i in jsonArray.result) {
				if (jsonArray.result[i].title != undefined) {
					addPanel(tabpanel, getPortal(jsonArray.result[i].uid, jsonArray.result[i].title, 3, jsonArray.result[i].data));
				}
			}
			if (jsonArray.result[0].title != undefined) {
				setActiveTab(0);
			}
		},
		setTabMaximize: function(jsonArray) {
			var activetab = tabpanel.getActiveTab().items;
			if (activetab.items.length>1) {
				for(var i=1; i<activetab.items.length; i++) {
					activetab.items[i].removeAll();
				}
			}
			activetab.add(new getMaximizePanel(jsonArray));
			// Minimize Panel을 재사용하기 위해 일단 숨겨둔다.
			activetab.items[0].hide();
			//activetab.items[0].show();
			viewport.doLayout();
			viewport.syncSize();
		},
		setTabMinimize: function() {
			var activetab = tabpanel.getActiveTab().items;
			if (activetab.items.length>1) {
				for(var i=1; i<activetab.items.length; i++) {
					activetab.items[i].removeAll();
				}
			}
			activetab.items[0].show();
			viewport.doLayout();
			viewport.syncSize();
		},
		setActiveTab: function(id) {
			setActiveTab(id);
		},
		getActiveTabsID: function(){
			return tabpanel.getActiveTab().id;
		},
		setLanguage: function(lan){ // 2011-04-01 kskim
			
			if(lan==null) {
				language = "default";
				return;
			}
			
			//언어 코드 확인하여 없는 언어일경우 default 로 설정한다.
			var linkUrl = "../gadget/help/customer/"+lan+"/index.html";

			if(isThere(linkUrl)){
				language = lan;
			}else {
				language = "default";
			}
		},
		getLanguage: function(){ // 2011-03-29 kskim
			return language;
		}
	}

	return pub;
}();

function logout(){	// 로그아웃 처리
	alert("logout !!");
	//document.location.href ='../customer/login.do';
	document.location.href ='../customer/logout.do';
}
function setActiveTab(id) {
	Ext.gadget.Manager.setActiveTab(id);
}
//기본 Portal Panel (minimize panel)을 구성한다.
function getExtMinimizeColumn() {

	Ext.Ajax.request({
		//url : '../ajax/dashboardgadgetitems.do', 
		url : '../ajax/dashboardgadgetitemsRole.do',
		method: 'GET',
		//성공 시에 수행될 콜백 함수
		success: function (result, request) {
			var jsonArray = Ext.util.JSON.decode(result.responseText);
			Ext.gadget.Manager.setTabAll(jsonArray);
			
			//2011-04-01 kskim 가젯들이 사용할 언어 코드
			Ext.gadget.Manager.setLanguage(jsonArray.language);
			
			//2011-04-01 kskim 헤더에서 사용할 언어코드 설정
			Ext.gadget.Manager.setHeader(jsonArray.loginId,jsonArray.language);
		},
		//실패 시에 수행될 콜백 함수
		failure: function (result, request) { 
			Ext.MessageBox.alert('Failed', 'Successfully posted form: '+ result.date); 
		}
	});
}
function getExtMaximizeColumn(uid, type) {
	Ext.Ajax.request({
		url : '../ajax/isfullitem.do',
		params : { uid: uid, type: type }, 
		method: 'GET',
		//성공 시에 수행될 콜백 함수
		success: function (result, request) {
			var jsonArray = Ext.util.JSON.decode(result.responseText);
			Ext.gadget.Manager.setTabMaximize(jsonArray);
		},
		//실패 시에 수행될 콜백 함수
		failure: function (result, request) { 
			Ext.MessageBox.alert('Failed', 'Successfully posted form: '+ result.date); 
		} 
	});
}


Ext.EventManager.onDocumentReady(Ext.gadget.Manager.init, Ext.gadget.Manager, true);
Ext.onReady(function(){
	//Ext.gadget.Manager.setHeader('admin');
	var settingHtml ='<div id="dinf_setting"><a href="javascript:setActiveTab(\'gadget-setting\');"></a></div>';
	$('#tab-panel').append(settingHtml);    
	getExtMinimizeColumn();
});

//2011-03-31 kskim  -  해당 주소의 유효성을 확인한다.
function errorHelpPageToDefaultPage(language,gadgetCode){
	if(gadgetCode!=null){
		gadgetCode = gadgetCode.replace(/\./gi,"_");//help 페이지에서는 '.'을 '_' 로 변경된다.
	}
	var url = '../gadget/help/customer/'+language+'/'+gadgetCode+".htm";

	if(isThere(url)){
		window.open(url,'help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
	}else {
		window.open('../gadget/help/customer/'+language+'/index.html','help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
	}
}
function isThere(url) { // 타 사이트의 주소의 경우 보안상 작동이 안될 수도 있다고 함.
	var req= new AJ(); // XMLHttpRequest object
	try {
		req.open("HEAD", url, false);
		req.send(null);		
		return req.status== 200 ? true : false;
	}
	catch (er) {
		return false;
	}
}
function AJ() {
	var obj;
	if (window.XMLHttpRequest) obj= new XMLHttpRequest(); 
	else if (window.ActiveXObject){
		try{
			obj= new ActiveXObject('MSXML2.XMLHTTP.3.0');
		}
		catch(er){
			try{
				obj= new ActiveXObject("Microsoft.XMLHTTP");
			}
			catch(er){
				obj= false;
			}
		}
	}
	return obj;
}
