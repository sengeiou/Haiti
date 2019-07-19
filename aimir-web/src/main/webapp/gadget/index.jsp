<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html class="htmlbody-scrollbar">
<head>    
	<title>AiMiR <fmt:message key='aimir.version'/> - Portal</title>
	<meta http-equiv="Cache-Control" content="no-store">
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Expires" content="0">
	<meta http-equiv="content-type" content="text/html; charset=utf-8">
	<!--  Add favicon by eunmiae. 2014-12-16 -->
	<link rel="icon" type="image/png" href="${ctx}/images/favicon2.ico" />
	<!--  Standard Includes -->
	<!-- link href="${ctx}/css/style-gadget.css" rel="stylesheet" type="text/css" -->
	<!--  Theme Includes -->
	<link rel="stylesheet" type="text/css" title="blue" href="${ctx}/themes/css/ext-all.css">
	<link rel="stylesheet" type="text/css" title="green" href="${ctx}/themes/css/ext-all-green.css">
	<link rel="stylesheet" type="text/css" title="orange" href="${ctx}/themes/css/ext-all-orange.css">
	
	
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-gadget.js">
	</script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	

	<fmt:setLocale value='${lang}' scope="session"/>


<script>

/* 	alert('<c:out value="${lang}"/>');
	
	alert('<c:out value="${locale}"/>'); */

	
	//close 버튼 메세지 설정
var strMsg1 = "<fmt:message key='aimir.message'/>";
var strMsg2 = "<fmt:message key='aimir.info.gadgetDelete'/>";
//var isAdmin = false;	

Ext.gadget.Manager = function() {
	var viewport;
	var headerpanel;
	var centerpanel;
	var tabpanel;
	var jsondata;
	var pub;
	
	// 2011-03-29 kskim
	// 2012-08-24 kyungjoon.go modified.
	var language;
	function getPortal(uid, title, column, json) 
	{
		var tools = new Array();
	
		tools.push({
			id:'help',qtip: 'Help Panel',
			handler: function(e, target, panel){
				// title 이름을 변형하여 URL 링크
				//var helpLink = panel.title.toLowerCase().replace(/ /gi,"_");
				
				// 2011-03-29 kskim - mini가젯들의 help 버튼 액션
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
		tools.push({
			id:'maximize', qtip: 'Maximize Panel',
			handler: function(e, target, panel){
				getExtMaximizeColumn(panel.uid, panel.type);
			}
		});
		
		//메시지 박스 버튼 메세지 정의
		Ext.MessageBox.buttonText.yes = "<fmt:message key='aimir.button.yes'/>";
		Ext.MessageBox.buttonText.no = "<fmt:message key='aimir.button.no'/>";
		
		//가젯 close버튼 클릭시 발생되는 이벤트 정의.
		//if(isAdmin){
		if(!isCustomerRole){
			tools.push({
				  id:'close', qtip: 'Close'
				, handler: function(e, target, panel)
				  {
				   // Add confirm message for delete gadget
				   //Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.info.gadgetDelete"/>',
					Ext.Msg.show({
					   title: strMsg1,
					   msg: strMsg2,
					   width: 280,
					  // minWidth: 250,
					   buttons: Ext.MessageBox.YESNO,
					   //buttonText :  {yes: "예", no: "노"}       , 
					   //multiline: true,
					   fn: 	  function(btn)
	                   {
	                       if (btn == 'yes') 
	                       {
	           				deleteItem(panel.uid);
	        				panel.ownerCt.remove(panel, true);
	                       }
	            		},
					
					   //animEl: 'addAddressBtn',
					   icon: Ext.MessageBox.INFO
					}); //show End 
				 /* 	Ext.MessageBox.confirm(strMsg1, strMsg2,
		                   function(btn)
		                   {
		                       if (btn == 'yes') 
		                       {
		           				deleteItem(panel.uid);
		        				panel.ownerCt.remove(panel, true);
		                       }
	                		}
				 	);   */
				}//핸들러 end
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
			portalColumnArr.push(new Ext.ux.PortalColumn({columnWidth:colWidth, style:'margin:10px 0 10px 10px'}));
		}
	
		// Position 에 따른 정렬
		json.sort(function(obj1, obj2){return obj1.position<obj2.position?-1:(obj1.position>obj2.position?1:0);});
	
		for(var k in json) {
			if (json[k].title != undefined) {
				json[k].tools = tools;
				json[k].listeners = {
					'expand': function(e) {
						if (Ext.isIE) e.getUpdater().refresh();
					}
				};
				if (json[k].xtype == 'iframeportlet') {	// 기존의 MIF처리하던 로직을 변경하여 iframeportlet의 경우 Controller를 이용 직접 iframe으로 변환
					json[k].xtype = 'portlet';
					json[k].autoLoad = {url:'../ajax/getIFrame.do?url='+json[k].defaultSrc};
				}
	
	/*
	* 주의 : 위의 tools의 경우 ajax callback 방식으로 여기에 handler를 세팅하는것은 의미 없음.				
				if (json[k].help) {	// help에 대한 정보가 있을 경우 help버튼의 URL을 변경하여 준다.
					json[k].tools[0].handler = function(e, target, panel){
						window.open(json[k].help,'help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
					}
				}
	*/
				var tmpColumnIndex = json[k].columnIndex;
				if (tmpColumnIndex>colMax-1) {
					portalColumnArr[colMax-1].add(json[k]);
				} else {
					portalColumnArr[tmpColumnIndex].add(json[k]);
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
	}; //end of getPortal


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
	
	//##가젯 최대화시 발생 이벤트.
	function getMaximizePanel(json) {
		
		//alert("getMaximizePanel");
		
		var tools = new Array();
		tools.push({
			id:'help',qtip: 'Help Panel',
			handler: function(e, target, panel){
			// 2011-03-29 kskim - max 가젯의 help 버턴 액션이다. 밑에서 한번더 정의한다.
			errorHelpPageToDefaultPage(language,null);
			
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
				
				// title 이름을 변형하여 URL 링크
				//var helpLink = json[i].title.toLowerCase().replace(/ /gi,"_");
				
				// 2011-03-30 kskim
				//var helpLink = json[i].gadgetCode.replace(/\./gi,"_"); //가젯 코드로 도움말 링크
				
				json[i].tools[0].handler = function(e, target, panel){
					var gadgetCode = panel.gadgetCode;
					// 2011-03-29 kskim  가젯이 max 일때 help 버튼 액션 에 gadgetCode 적용
					errorHelpPageToDefaultPage(language,gadgetCode);
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
	
//		thema += '<a href=\'#\'onClick=\"setActiveStyleSheet(\'blue\');\">TestBlue</a>';
		
		thema += '</form>';

		var html = '<div id="dark_head">';
		html +='<div id="logo"></div>';
		html +='<ul id="dinf_box">';
		html +='<li id="dinf_01">'+id+'</li><li id="dinf_line"></li>';
		html +='<li id="dinf_02"><a href="javascript:setActiveTab(\'account-setting\');">Account</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_03"><a href="javascript:logout();">Logout</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_05"><a href="javascript:;" onClick="window.open(\'../gadget/help/default/index.html\',\'help\',\'location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes\');">Help</a></li><li id="dinf_line"></li>';
		html +='<li id="dinf_04">'+thema+'</li>';
		html +='</ul>';
		html +='</div>'; 
		el.body.update(html);
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
		//사용자가 처음 로그인할 때 비밀번호를 설정할 수 있도록 비밀번호 설정화면으로 가도록한다.
		tabpanel.remove("account-setting-firstLogin");

		if (id=="gadget-setting") {
			addPanel(tabpanel, getIFramePanel('gadget-setting', 'Gadget Setting', 660, 'gadget_setting_max.do'));
			tabpanel.hideTabStripItem("gadget-setting");
			tabpanel.setActiveTab("gadget-setting");
		} else if (id=="account-setting") {
			addPanel(tabpanel, getIFramePanel('account-setting', 'Account Setting', 580, 'accountSetting.do'));
			tabpanel.hideTabStripItem("account-setting");
			tabpanel.setActiveTab("account-setting");
		} else if (id=="account-setting-firstLogin") {
			changePassword(tabpanel);
//			addPanel(tabpanel, getIFramePanel('account-setting-firstLogin', 'Account Setting_FirstLogin', 580, 'accountFirstSetting.do'));
//			tabpanel.hideTabStripItem("account-setting-firstLogin");
//			tabpanel.setActiveTab("account-setting-firstLogin");
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
		},
		setTabAll: function(jsonArray) {
			for(var i in jsonArray.result) {
				if (jsonArray.result[i].title != undefined) {
					addPanel(tabpanel, getPortal(jsonArray.result[i].uid, jsonArray.result[i].title, 3, jsonArray.result[i].data));
				}
			}
			if (jsonArray.result[0] != undefined && jsonArray.result[0].title != undefined) {
				if(_isFirstLogin == null || _isFirstLogin == true) {
					setActiveTab("account-setting-firstLogin");
				} else {
					setActiveTab(0);
				}
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
		setLanguage: function(lan){ // 2011-03-29 kskim
			
			if(lan==null) {
				language = "default";
				return;
			}
			
			//언어 코드 확인하여 없는 언어일경우 default 로 설정한다.
			var linkUrl = "../gadget/help/"+lan+"/index.html";

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
}();// End of Ext.gadget.Manager 

	// change the password.
	function changePassword(tabpanel){
		var grpPwdUsageFormPanel;
		grpPwdUsageFormPanel = new Ext.FormPanel({
	        id : 'grplmtPwrUsageForm',
	        defaultType : 'fieldset',
	        bodyStyle:'padding:1px 1px 1px 1px',
	        frame : true,
	        labelWidth : 170,
	        items : [
	            {
					xtype : 'textfield',
					inputType: 'password',
					id : 'grpPreviousPwdValue',
					fieldLabel : 'Previous password'
	            },
	            {
					xtype : 'textfield',
					inputType: 'password',
					id : 'grpNewPwdValue',
					fieldLabel : 'New password'
	            },
	            {
					xtype : 'textfield',
					inputType: 'password',
					id : 'grpPwdConValue',
					fieldLabel : 'Password Confirmation'
	            }
	        ], // items
	        buttons : [
	            {
	                id: 'btnInputGrplmtPwrUsage',
	                text: 'Save',
	                listeners: {
	                    click: function(btn,e){
	                        //submit action
	                        var oldPassword = Ext.getCmp('grpPreviousPwdValue').getValue();
	                        var password = Ext.getCmp('grpNewPwdValue').getValue();
	                        var passwordCheck = Ext.getCmp('grpPwdConValue').getValue();

	                        //Check the current password.
				        	$.getJSON('${ctx}/gadget/system/operator/checkPassword.do?loginId='+encodeURIComponent(loginId)+'&oldPassword='+oldPassword,
			                    function(json) {
			        		        pwCheck = json.pwCheck;
			                        if ( json.pwCheck ) {
			                        	if((password.length == 0) || (password.length == 0)){
											// If the new passwords do not match, a message display.
		            					    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.operator.confirm.newpwd" />');
	                                        return;
			                        	}
		                                // Check new password.
		                                if(password!='') {
		                                    if ( password.match(/[^0-9A-Za-z]+/) != null){
				        					    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.chkAlphabetNumber" />');
		                                        return;
		                                    }
		                                }
		                                if(password==passwordCheck){
		                                    //Password change processing
									    	$.ajax({
									    		type : "POST",
									    		data : {
									    			'userId' : operatorId,
									                'loginId' : loginId,
													'password' : password
									            },
									    		dataType : "json",
									    		url : "${ctx}/gadget/system/operator/updatePassword.do",
									    		success : function(data, status) {
		            					    		alert(status);
									        		grpPwdUsageWin.close();
									        		tabpanel.setActiveTab(0);
									    		}
											});
		                                }else{
											// If the new passwords do not match, a message display.
		            					    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.operator.confirm.newpwd" />');
		                                }
		
			                        }else{
		        					    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.operator.confirm.wrongprevpwd" />');
			                        }
			                    }
				             );
	                    }
	                }
	            }
	        ] //buttons
	    });

	    var grpPwdUsageWin = new Ext.Window({
	        id     : 'grplmtPwrUsage',
	        pageX : 350,
	        pageY : 100,
	        height : 150,
	        width  : 380,
	        layout : 'fit',
	        bodyStyle   : 'padding: 10px 10px 10px 10px;',
	        modal: true, closable:false, resizable: false,
	        items  : [grpPwdUsageFormPanel],
	    });

	    grpPwdUsageWin.show();
	}

    function getBrowserLg() {
    	var lang = "un"; //언어 값 받아올 변수. un은 undefined 의 앞 2글자.
    	 
    	 if (navigator.language!=null) //크롬이나 Firefox일 경우
    	 {
    	     lang = navigator.language;
    	 } else if (navigator.userLanguage!=null) { //IE의 경우
    	     lang = navigator.userLanguage;
    	 } else if (navigator.systemLanguage!=null) {
    	     lang = navigator.systemLanguage;
    	 } else { //이도저도 아니면
    	     lang="en";
    	 }
    	 
    	 lang = lang.toLowerCase(); //받아온 값을 소문자로 변경
    	 lang = lang.substring(0, 2); //소문자로 변경한 갚의 앞 2글자만 받아오기
		
    	 return lang;
    }

function logout(){	// 로그아웃 처리
	if(isCustomerRole){
		document.location.href ='../customer/logout.do';
	}else {		
		document.location.href ='../admin/logout.do';		
	}

	// document.location.href ='../';		

    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"logout!");
}

function setActiveTab(id) {
	Ext.gadget.Manager.setActiveTab(id);
}
//기본 Portal Panel (minimize panel)을 구성한다.
function getExtMinimizeColumn() {
	Ext.Ajax.request({
		url : '../ajax/dashboardgadgetitems.do', 
		method: 'GET',
		//성공 시에 수행될 콜백 함수
		success: function (result, request) {
			var jsonArray = Ext.util.JSON.decode(result.responseText);

			Ext.gadget.Manager.setTabAll(jsonArray);
			
			//전역 변수에 언어코드 저장.
			Ext.gadget.Manager.setLanguage(jsonArray.language);
			
			// 2011-03-29 kskim
			//상단 help 링크 언어별로 수정
			document.getElementById('dinf_05').innerHTML=
				"<a href=\"javascript:;\" onClick=\"window.open(\'../gadget/help/"+
				Ext.gadget.Manager.getLanguage()+
				"/index.html\',\'help\',\'location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes\');\">Help</a>";

			// temp code jeju icc custom
			// 고객이 로그인할 경우 첫번째 화면이 Max 가젯으로 보이도록 하는 부분
			if(isCustomerRole) {
				var r = jsonArray.result;
				if(r && r.length && r.length > 0 && r[0] && r[0].data) {
					var firstDashbordGadgets = r[0].data;
					if(firstDashbordGadgets && firstDashbordGadgets.length) {
						var c = firstDashbordGadgets[0] || null;
						if(c) {
							getExtMaximizeColumn(c.uid, c.type);
						}					
					}
				}
			}
		},

		//실패 시에 수행될 콜백 함수
		failure: function (result, request) { 
			Ext.MessageBox.alert('Failed', 'Failed posted form: '+ result.date); 
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
			Ext.MessageBox.alert('Failed', 'Failed posted form: '+ result.date); 
		} 
	});
}

var _isFirstLogin;
var isCustomerRole = '';
var isAdmin = '';
var isDashAuth = '';
var loginId = '';
var operatorId = '';
function getUserInfo() {
	Ext.Ajax.request({
		url : '../common/getUserInfo.do',
		params : { }, 
		method: 'GET',
		//성공 시에 수행될 콜백 함수
		success: function (result, request) {
			var jsonArray = Ext.util.JSON.decode(result.responseText);			
			document.getElementById('dinf_01').innerHTML=jsonArray.loginId;
			_isFirstLogin=jsonArray.isFirstLogin;
			isCustomerRole=jsonArray.customerRole;
			isCustomerRole = (isCustomerRole == null || isCustomerRole == '') ? false : true			
			isAdmin = (jsonArray.isAdmin == null || jsonArray.isAdmin != true) ? false : true;
			isDashAuth =jsonArray.isDashAuth;
			if(isDashAuth == false){
				$('#dinf_setting').hide();
			}
			loginId =jsonArray.loginId;
			operatorId =jsonArray.operatorId;
		},
		//실패 시에 수행될 콜백 함수
		failure: function (result, request) { 
			Ext.MessageBox.alert('Failed', 'Failed posted form: '+ result.date); 
		} 
	});
}
Ext.EventManager.onDocumentReady(Ext.gadget.Manager.init, Ext.gadget.Manager, true);

Ext.onReady(function(){
	getUserInfo();
	Ext.gadget.Manager.setHeader('');
	var settingHtml ='<div id="dinf_setting"><a href="javascript:setActiveTab(\'gadget-setting\');"></a></div>';
	$('#tab-panel').append(settingHtml);
    Ext.get('styleswitcher_select').on('change',function(e,select){
        var name = select[select.selectedIndex].value;
        setActiveStyleSheet(name);
    });
    Ext.get('styleswitcher_select').dom.value=title;
	getExtMinimizeColumn();
});

//2011-03-31 kskim  -  해당 주소의 유효성을 확인한다.
function errorHelpPageToDefaultPage(language,gadgetCode){
	if(gadgetCode!=null){
		gadgetCode = gadgetCode.replace(/\./gi,"_");//help 페이지에서는 '.'을 '_' 로 변경된다.
	}
	var url = '../gadget/help/'+language+'/'+gadgetCode+".htm";
	if(isThere(url)){
		window.open(url,'help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
	}else {
		window.open('../gadget/help/'+language+'/index.html','help','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');
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

</script>	
</head>
<body id="gadgetLayoutBody">
</body>
</html>