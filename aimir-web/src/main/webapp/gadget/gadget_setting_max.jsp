<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", -1); //prevents caching at the proxy
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Gadget Setting</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/css/jquery.cluetip.css" rel="stylesheet" type="text/css" />
   <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.jsp"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <style type="text/css">
    	 .x-grid3-cell { vertical-align: middle !important; }
    	 .x-grid3-hd-inner { text-align: center !important; font-weight: bold !important; }
    	 #dashboardName{ color: #4374D9; }
    	 #gadgetName{ color: #2E9AFE; }
    	 #gadgetGroup{
    	 }
    	#sGadget{
    	 	width:45%;
    	 	height:170px;
    	 	float:left;
    	 	border:2px solid #2E9AFE;
    	 	margin: 5px 0px 5px 15px;
    	 	border-radius: 5px;
    	 } 
    	 #sGadget:hover{ background:#E0F2F7; display:block; text-decoration:none; }
    	 #dashboardGadgetName{ color: white; width:200px; text-align:center; }
    	 #removeBtn{ width:10px; height:10px; }
    	 #gadgetImg{
    	 	width:150px;
    	 	height:auto;
    	 	display: block;
    		margin: 0 auto;
    	 }
		#gadgetTitle{ background:#2E9AFE; border-bottom:2px solid #2E9AFE; }
		.gadget_plus a{
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		  background:url(${ctx}/themes/images/orange/setting/btn-plus.gif) no-repeat;
		}
		.gadget_plus:hover a{
		  background-position: -18px 0px;
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		}
		.gadget_modi a{
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		  background:url(${ctx}/themes/images/orange/setting/btn-modi.gif) no-repeat;
		}
		.gadget_modi:hover a{
		  background-position: -18px 0px;
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		}
		.panel1{
			height: 490px;
			width: 510px;
		}
		.scrollbar{
			border:1px solid #a5bde5;
			height: 490px;
			width: 530px;
			background: #FFFFFF;
			overflow-y: scroll;	
			overflow-X: hidden;
			margin-bottom: 25px;
		}
		.scrollbar1{
			border:1px solid #a5bde5;
			height: 490px;
			width: 472px;
			overflow-y: scroll;	
			overflow-X: hidden;
			margin-bottom: 25px;
		}
		#dashboardLists{
			background: #E0F2F7;
		}
		.selected { background: #2E9AFE; color: white; }
	
    </style>
    <script type="text/javascript" charset="utf-8">
        var roleId;
        var supplierId;
        var customerRole;
        var operatorId;
        var selectedId;
        var dashboardObj;
        var currentTab = "";
        //var flex;
        var firstDashboardId=-1;
    	var gadgetObj;
		var gadgetNo = "";
		var gadgetName = "";
		var gadgetIconSrc = "";
		var dashboardId = "";
		var gadgetId = "";
		var dashboardGadgetId = "";
		var dashboardGadgetName = "";
		var dashboardGadgetIconSrc = "";
		var result = false;
		var selectedDashboardId;
		var browserWidth= "";
		
	    $(document).ready(function() {
            Ext.QuickTips.init();
	    });
	    
    	$(function(){

    		/**
        	 * 유저 세션 정보 가져오기
        	 */
        	$.getJSON('${ctx}/common/getUserInfo.do',
        	        function(json) {
        	            if(json.roleId != "" && json.operatorId != ""){
        	            	roleId = parseInt(json.roleId);
        	            	operatorId = parseInt(json.operatorId);
        	            	customerRole = json.customerRole;
        	            	supplierId = parseInt(json.supplierId);
        	            	//customer 일때 system dashboard 를 숨긴다.
    						//if(customerRole==1){
    						//	currentTab = "user";
    						//	$('#systemTab').parent().hide();
    						//	changeTabs('user');
    						//	
    						//}else {
    							currentTab = "system";
    							getDashboards();
    							getGadgetSetting();	
    						//}

        	            }

        	            $('.gadgetset_flex').show();
        	        }
        	);

        	

    	});
    
        function changeTabs(tabType){
            if (tabType=="system") {
            	$('#systemTab').removeClass();
            	$('#systemTab').addClass("dashboardset_tabon");

            	$('#userTab').removeClass();
            	$('#userTab').addClass("dashboardset_taboff");
                currentTab = "system";
            } else if (tabType=="user") {
            	$('#systemTab').removeClass();
            	$('#systemTab').addClass("dashboardset_taboff");

            	$('#userTab').removeClass();
            	$('#userTab').addClass("dashboardset_tabon");
                currentTab = "user";
            }
            getDashboards();
            //flex.callFlex(0);
            $('#dashboardDetail').html("");
        }

        function clearSuvDiv(){
            //$("#dash-add-title").hide();
            //$("#dash-modify-title").hide();
            //$("#dash-detail-title").hide();

            $(".dash-add-title").hide();
            $(".dash-modify-title").hide();
            $(".dash-detail-title").show();

            $("#dashboard-UpdateForm").hide();
            $("#dashboard-AddForm").hide();
            $("#dashboardDetail").show();

            $("#addBtn").hide();
            $("#modifyBtn").hide();
            $("#modeBtn").show();
            
            resetForm();
        }

	    function resetForm() {
	        //$("#dashboardAddForm :hidden[name='name']").val('');
	        $('#addForm').resetForm();
	        $('#updateForm').resetForm();
	    }

	    function submitType(type) {
	        //alert(type);
	        if (type == "add") {
	            if (currentTab == "system") {
	                var options = {
	                    success : dashboardAddResult,
	                    url : '${ctx}/gadget/addDashboard.do',
	                    type : 'post',
	                    datatype : 'json'
	                };
	                $('#addForm').ajaxSubmit(options);
	            } else if (currentTab == "user") {
	                var options = {
	                    success : dashboardAddResult,
	                    url : '${ctx}/gadget/addDashboardWithUser.do',
	                    type : 'post',
	                    datatype : 'json'
	                };
	                $('#addForm').ajaxSubmit(options);
	            }
	        }
	        else if (type == "update") {
	            var options = {
	                success : dashboardUpdateResult,
	                url : '${ctx}/gadget/updateDashboard.do',
	                type : 'post',
	                datatype : 'json'
	            };
	            $('#updateForm').ajaxSubmit(options);
	        }
	    }
		
	  
	    function getDashboards() {
	        $.getJSON('${ctx}/gadget/getDashboards.do', {tabType: currentTab},
	        function(json) {
                var innerHtml = "";

                if (json.dashboard != null && json.dashboard[0] != null) {
                    
                    firstDashboardId = parseInt(json.dashboard[0].id);
                    operatorId = json.operator;

                    $.each(json.dashboard, function(sIndex, dashboard) {
                        innerHtml += "<li class='dlist'>" +
			                                     "        <a href='javascript:callGadgets(" + dashboard['id'] +  ");'>" + dashboard['name'] + 
			                                     "        </a>" +
			                                    "</li>";
                        
                   });
                }
                
                $('#dashboardList').html(innerHtml);
                //첫번째 대쉬보드에 대한 정보 전달
                if(currentTab == "system"){
	               callGadgets(firstDashboardId);
                }
				
	        });
	    }

	    //callFlex 수정
        function callGadgets(dashboardId) {
        	$('.dlist').click(function(){
	    		$('.dlist').removeClass('on');
	    		$('.dlist').css('background-color','#FFFFFF');
	    		$(this).addClass('on');
	    		$('.on').css('background-color','#e0e1e2');
	    	});
            selectedId = dashboardId;
            clearSuvDiv();
            dashboardInfo(dashboardId);
            //dashboardGadgetInfo(dashboardId);
            getGadgetSettingByDB(dashboardId);
          
        }
    	
    	
	    function addDashboard() {
	        clearSuvDiv();

            $(".dash-detail-title").hide();
            $("#dashboardDetail").hide();
            //$(".dashboard_btn").hide();
            $("#modeBtn").hide();

            $(".dash-add-title").show();
            $("#dashboard-AddForm").show();
            $("#addBtn").show();
	        //$("#dashboard-AddForm").load("${ctx}/gadget/addDashboard.do");
	    }

	    function dashboardInfo(dashboardId) {
	        if(dashboardId != null){
	            $("#dash-detail-title").show();
	            $("#dashboardDetail").show();
	            $.getJSON('${ctx}/gadget/getDashboard.do', {dashboardId:dashboardId},
	                    function(json) {
	                        dashboardObj = json.dashboardInfo;
	                        bindingDashboardInfo();
	                        bindingDashboardName();
	                        //$('#container-3').show('fast', vendorTabListener());
	                        //$('#container-4').hide();
	            });
	        }
	    }
		
	    function bindingDashboardName(){
	    	  var innerHtml = "";

	            if (dashboardObj != null) {
	    	        innerHtml += dashboardObj.name + ' Dashboard';
	            }

		        $('#dashboardName').html(innerHtml);
	    }
	    
	    function bindingDashboardInfo() {
	        //$('#dashboardDetail').setForm(dashboard);
	        //$('#dashboardDetail :hidden[name="id"]').val(dashboard.id);
            clearSuvDiv();

	        var innerHtml = "";

            if (dashboardObj != null) {
    	        innerHtml +="<ul>" +
    	        "        <label class='type2'><fmt:message key='aimir.name'/></label>" +
    	        "        <li><input type='text' readonly value='"+dashboardObj.name+"'/></li>" +
    	        "    </ul>" +
    	        "    <ul>" +
    	        "        <label class='type2'><fmt:message key='aimir.orderNo'/></label>" +
    	        "        <li><input type='text' readonly value='"+dashboardObj.orderNo+"' class='type2'/></li>" +
    	        "    </ul>" +
    	        "    <ul>" +
    	        "        <label class='type2'><fmt:message key='dashboard.grid.maxX'/></label>" +
    	        "         <li class='type2'><input class='type2' type='text' readonly value='"+dashboardObj.maxGridX+"'/></li>" +
    	        "    </ul>" +
    	        "    <ul>" +
    	        "     <label class='type2'><fmt:message key='dashboard.grid.maxY'/></label>" +
    	        "        <li class='type2'><input class='type2' type='text' readonly value='"+dashboardObj.maxGridY+"'/></li>" +
    	        "    </ul>" +
    	        "    <ul>" +
    	        "     <label class='type3'><fmt:message key='aimir.description'/></label>" +
    	        "     <li class='type3'><textarea readonly >"+dashboardObj.descr+"</textarea></li>" +
    	        "    </ul>";
            }

	        $('#dashboardDetail').html(innerHtml);
	    }

	    function bindingDashboardUpdateInfo() {
	        $('#updateForm').setForm(dashboardObj);
	        $('#updateForm #maxGridX').val(dashboardObj.maxGridX);
	        $('#updateForm #maxGridY').val(dashboardObj.maxGridY);
	    }

	    function modifyDashboard() {
	        clearSuvDiv();
	        //$("#dashboard_btn").hide();
	        var dashboardId = selectedId;

	        if(dashboardId != null && dashboardId > 0){
                $(".dash-detail-title").hide();
                $("#dashboardDetail").hide();
                //$(".dashboard_btn").hide();
                $("#modeBtn").hide();

                $(".dash-modify-title").show();
                $("#dashboard-UpdateForm").show();
                $("#modifyBtn").show();

	            //$.getJSON('${ctx}/gadget/getDashboard.do', {dashboardId:dashboardId},
	            //        function(json) {
	            //            var dashboard = json.dashboardInfo;
	            //            bindingDashboardUpdateInfo(dashboard);
	            //            //$('#container-3').show('fast', vendorTabListener());
	            //            //$('#container-4').hide();
	            //    });
	            bindingDashboardUpdateInfo();
	        }else{
	            alert("<fmt:message key='dashboard.msg.choose' />");
	        }
	    }
		
	    function dashboardAddResult(responseText, status) {
	        alert(responseText.result);
	        if (responseText.errors && responseText.errors.errorCount > 0) {
	            var i, fieldErrors = responseText.errors.fieldErrors;
	            for (i=0 ; i < fieldErrors.length; i++) {
	                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
	                $(temp).val(''+fieldErrors[i].defaultMessage);
	            }
	        } else {
	         getDashboards();
	         $('#addForm').resetForm();
	        }
	    }

	    function dashboardUpdateResult(responseText, status) {
	        alert(responseText.result);
	        if (responseText.errors && responseText.errors.errorCount > 0) {
	            var i, fieldErrors = responseText.errors.fieldErrors;
	            for (i=0 ; i < fieldErrors.length; i++) {
	                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
	                $(temp).val(''+fieldErrors[i].defaultMessage);
	            }
	        } else {
	            getDashboards();
	            $('#updateForm').resetForm();
	        }
	    }

	    function deleteDashboard() {
	        clearSuvDiv();
	        var dashboardId = selectedId;

	        if(dashboardId != null){
	            $("#dash-detail-title").show();
	            $("#dash-detail-title").hide();
	            Ext.MessageBox.confirm('Dashboard Delete', 'Do you want Delete this Dashboard?', function(btn){ 
	    		    if (btn == 'yes') {
			            $.ajax({
			                url: "${ctx}/gadget/deleteDashboard.do?dashboardId=" + dashboardId,
			                cache: false,
			                success: function(){
			                    //dashboardObj = null;
			                    getDashboards();
			                    //resetDefault();//TODO 목록 갱신
			                }
			            });
	    		    }
	            });
	        }else{
	            alert("<fmt:message key='dashboard.msg.choose' />");
	        }
	    }

		function getOperatorId() {
			return operatorId;
		}

		function getRoleId(){
	        return roleId;
		}

	    function getFmtMessage(){
	        var fmtMessage = new Array();;

	        fmtMessage[1] = "<fmt:message key='aimir.gadget.adddashboard'/>";          // 원하는 기능의 가젯을 추가하세요
	        fmtMessage[2] = "<fmt:message key='aimir.gadget.search.message2'/>";                                                        // 
	        fmtMessage[3] = "<fmt:message key='aimir.search.cond'/>"; 
	        fmtMessage[4] = "<fmt:message key='aimir.memo.search'/>";                                                        
	        fmtMessage[5] = "<fmt:message key='aimir.tag'/>";                  
	        fmtMessage[6] = "<fmt:message key='aimir.gadget.update'/>";
	        fmtMessage[7] = "<fmt:message key='aimir.update'/>"; 
	        fmtMessage[8] = "<fmt:message key='aimir.cancel'/>";
	        fmtMessage[9] = "";
        
	        return fmtMessage;
	    }
	    
	/*대쉬보드 가젯 셋팅*/
	var gadgetSettingStore;
	var gadgetSettingModel;
	var gadgetSettingPanel;
	var gadgetSettingInstanceOn = false;
	function getGadgetSetting(){
		gadgetSettingStore = new Ext.data.JsonStore({
				autoLoad: true,
				url:"${ctx}/gadget/system/getGadgetSetting.do",
				root: 'gridData',
				baseParams:{
					supplierId:supplierId,
			 		roleId:roleId
				}, 
				fields: [
					"id",
					"name",
					//"iconSrc",
					"fullHeight",
					"miniHeight",
					"descr"
				]
		});//Store End
		
		gadgetSettingModel = new Ext.grid.ColumnModel({
			columns:[
				{header:'id', dataIndex:'id',hidden:true},
				{
					header:'Name / Description', 
					dataIndex:'descr',
					width: 380,
					tooltip: 'Name and Description',
					renderer: function(value,metadata,r,index){
						 metadata.attr = 'ext:qtip="' + value + '"';
						 return "<b id='gadgetName'>"+(r.data['name'])+"</b>" + "<br><br>" 
						 			+(r.data['descr'],value);
					},
				},/* {
					header:'Height', 
					dataIndex:'fullHeight',
					width: 130,
					renderer: function(value,p,r){
						 return "<span id='height'>Full.Height : </span>"+ r.data['fullHeight'] + "<br><br>" 
						 			+"<span id='height'>Mini.Height : </span>"+r.data['miniHeight'] ;
					}
				}, */{
					header: 'Add',
					align: 'center',
					width:40,
					tooltip:'Add to Dashboard',
                    renderer: function(value, metaData, record, index) {
                    	var data = record.data;
                    	gadgetId = data.id;
                        var btnHtml = "<div class='gadget_plus'><a href='#;' onclick='addGadget();' ></a></div>";
                        var tplBtn = new Ext.Template(btnHtml);
                        return tplBtn.apply();
                    }
                },{
					header: 'Modify',
					align: 'center',
					width:40,
					tooltip:'Modify Gadget Information',
                    renderer: function(value, metaData, record, index) {
                    	var data = record.data;
                        var btnHtml = "<div class='gadget_modi'><a href='#;' onclick='modifyGadget();' ></a></div>";
                        var tplBtn = new Ext.Template(btnHtml);
                        return tplBtn.apply();
                    }
                }
			],
			defaults: {
				sortable: true,
			}
		});//Column Model
	//Grid Panel
	if(gadgetSettingInstanceOn == false){
		gadgetSettingPanel = new Ext.grid.GridPanel({
			store: gadgetSettingStore,
			colModel: gadgetSettingModel,
			autoScroll: false,
			scroll: false,
			width: 482,
			layout: 'fit',
			stripeRows: true,
			columnLines: true,
			renderTo: 'panel1',
			hideHeaders: true,
			border: false,
			sm : new Ext.grid.RowSelectionModel({
    			singleSelect:true,
    			listeners: {
                    rowselect: function(selectionModel, row, rec) {
                    	var data = rec.data;
                     	gadgetId = data.id;
                    	gadgetName = data.name;
                    	gadgetDescr = data.descr;
                    	gadgetFullHeight = data.fullHeight;
                    	gadgetMiniHeight = data.miniHeight;
                    }
                }
    		}),
		
		});//Grid Panel End
		gadgetSettingInstanceOn = true;
		
	}else{
		 gadgetSettingPanel.reconfigure(gadgetSettingStore, gadgetSettingModel);
	}
}
	//가젯추가
	function addGadget(){
		$.ajax({
	              url: "${ctx}/gadget/system/addGadget.do",
	              datatype : 'json',
	              type: 'post',
	              data:{
	    		  	"gadgetId":gadgetId,
	    			"dashboardId":selectedId
	    			},
	              success: function(responseText, status){
	            	  Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
	            	  selectedDashboardId=selectedId;
	            	  getGadgetSettingByDB(selectedDashboardId);
	            },
	         }); 
	}
	
	//가젯 수정 팝업창
	var gadgetModifyWin;
	function modifyGadget(){
		var opts = "width=400px, height=200px, left=650px, top=100px,  resizable=no, status=no";
		var obj = new Object();
		obj.gadgetId = gadgetId;
    	obj.gadgetName = gadgetName;
    	obj.gadgetDescr = gadgetDescr;
    	obj.gadgetFullHeight = gadgetFullHeight;
    	obj.gadgetMiniHeight = gadgetMiniHeight;
 		
		if(gadgetModifyWin){
			gadgetModifyWin.close();
		} 
		gadgetModifyWin = window.open("${ctx}/gadget/gadgetModifyPopup.jsp", "gadgetModify", opts);
		gadgetModifyWin.opener.obj = obj;
		
		getReturnValue(result);
	}
	
	function getReturnValue(result){
		if(result==true)
			getGadgetSetting();
	}
	
	/*대쉬보드에 따른 가젯 리스트*/
/* 	var gadgetSettingByDBStore;
	var gadgetSettingByDBModel;
	var gadgetSettingByDBPanel;
	var gadgetSettingByDBInstanceOn = false;
	function getGadgetSettingByDB(dashboardId){
		gadgetSettingByDBStore = new Ext.data.JsonStore({
				autoLoad: true,
				url:"${ctx}/gadget/system/getGadgetByDashboard.do",
				root: 'gadgetList',
			 	baseParams:{
					dashboardId:dashboardId
				}, 
				fields: [
					"id",
					"name", 
					"iconSrc"
				]
		});//Store End
		
		gadgetSettingByDBModel = new Ext.grid.ColumnModel({
			columns:[
						{header:'id', dataIndex:'id',hidden: true},
						{
							header:'name', 
							dataIndex:'iconSrc',
							width: 332,
							align: 'center',
							renderer: function(value,p,r){
								var data = r.data;
		                        var btnHtml = "<a href='#;' onclick='removeGadget();' class='btn_blue'><span><b>x</b></span></a>";
		                        var tplBtn = new Ext.Template(btnHtml);
								return "<span id='preview'>"+tplBtn.apply()+"</span>"+"<br><div id='nameimg'><b>"+r.data['name']+"</b><br><br>"
											+"<img src='${ctx}/"+r.data['iconSrc']+"'/></div><br>";
							}
						},
					]
		});//Column Model
	
 	if(gadgetSettingByDBInstanceOn == false){
 		gadgetSettingByDBPanel = new Ext.grid.GridPanel({
			store: gadgetSettingByDBStore,
			colModel: gadgetSettingByDBModel,
			autoScroll: true,
			scroll: true,
			width: 435,
			height: 530,
			stripeRows: true,
			columnLines: true,
			//renderTo: 'panel2',
			hideHeaders: true,
            sm : new Ext.grid.RowSelectionModel({
    			singleSelect:true,
    			listeners: {
                    rowselect: function(selectionModel, row, rec) {
                    	var data = rec.data;
                    	dashboardGadgetId = data.id;
                    }
                }
    		}),
		});//Grid Panel
	
		gadgetSettingByDBInstanceOn = true;
	 }else{
	 	gadgetSettingByDBPanel.reconfigure(gadgetSettingByDBStore, gadgetSettingByDBModel);
	} 
}
 */
	 var gadgetList = [];
	 var gadgetId = [];
	 var gadgetName = [];
	 var gadgetIconSrc = [];
	 var gadgetSettingByDBInstanceOn = false;
	 var i = 0;
	 function getGadgetSettingByDB(dashboardId) {
		 $("#gadgetGroup").html("");
	     $.getJSON('${ctx}/gadget/system/getGadgetByDashboard.do'
	             , {'dashboardId' : dashboardId}
	             , function(json) {
					for(i=0; i<json.gadgetList.length; i++){
						dashboardGadgetId = json.gadgetList[i].id;
						dashboardGadgetName = json.gadgetList[i].name;
						dashboardGadgetIconSrc = json.gadgetList[i].iconSrc;
						$('#gadgetGroup')	.append(
							"<div id='sGadget'>"
								+"<input type='hidden' value='"+dashboardGadgetId+"'/>"
								+"<table id='dashboardGadget'>"
									+"<tr id='gadgetTitle'>"
										+"<td id='dashboardGadgetName'><b>"+dashboardGadgetName+"</b></td>"
				                    	+"<td id='removeBtn'>"
				                    		+"<a href='#;' onclick='removeGadget("+dashboardGadgetId+");' >"
				                    			+"<span style='color:#FFFFFF'><b>x</b></span></a>"
				                    	+"</td>"
			                    	+"</tr>"
									+"<tr>"			                   
			                    		+"<td colspan='2'><br><img id='gadgetImg' src='${ctx}/"+dashboardGadgetIconSrc+"'/></td>"
			                    	+"</tr>"
			                    +"</table>"
		                   	+"</div>"        
						);
					}
	             });
	 } 
	
	
	//가젯 삭제
	function removeGadget(dashboardGadgetId){
		Ext.MessageBox.confirm('Gadget Delete', 'Do you want Delete this Gadget?', function(btn){ 
		    if (btn == 'yes') {
			  	$.ajax({
			              url: "${ctx}/gadget/system/removeGadget.do",
			              datatype : 'json',
			              type: 'post',
			              data :{
			            	  "dashboardGadgetId":dashboardGadgetId
			    			},
			              success: function(responseText){
			            	  Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);  
			            	//새로고침
			            	  selectedDashboardId=selectedId;
			            	  getGadgetSettingByDB(selectedDashboardId);
			              }
			         }); 
		    }
		});
	};
	// GadgetSettingByDashboard End
	
	 var sGadgetName = null;
	 var gadgetSearchStore;
	 function gadgetSearch(value){
		 sGadgetName = value;
		 gadgetSearchStore  = new Ext.data.JsonStore({
				autoLoad: true,
				url:"${ctx}/gadget/system/searchGadget.do",
				root: 'gadgetList',
			 	baseParams:{
			 		gadgetName:sGadgetName,
			 		roleId:roleId
				}, 
				fields: [
					"id",
					"name",
					"iconSrc",
					"fullHeight",
					"miniHeight",
					"descr"
				]
		});//Store End
		 gadgetSettingPanel.reconfigure(gadgetSearchStore, gadgetSettingModel);
	}
	 
	 //var flag;
	 function keyEvent(event,value) {
	        var evKeyup = null;
	        if (event)
	            // firefox
	            evKeyup = event;
	        else
	            // explorer
	            evKeyup = window.event;

	        var l = document.gadgetform;

	        if (evKeyup.keyCode == 13) {
	            	gadgetSearch(value);
				/*
	            	if (l.sGadgetName.value == '') {
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
	                return;
	            } else { 
	            }*/
	        }
	    }
	 
    </script>
</head>

<body class="gadgetsetting">
<!-- Outer Table (S) -->
<table>
    <tr>
    <td class="gadgetsetting_leftwidth">


            <!-- Left Element : bg(top), tab, dashboard list, dashboard details/add/modify (S) -->
            <div class="dashboardset_bg">

                    <!-- bg(top) -->
                    <!-- <div class="dashboardset_bg_topleft"></div>
                    <div class="dashboardset_bg_top"><fmt:message key="dashboard.setting.title"/></div>
                    <div class="dashboardset_bg_topright"></div> -->

                    <div class="dashboardset_bg_topleft"></div>
                    <div class="dashboardset_bg_top"><fmt:message key="dashboard.setting.title"/></div>
                    <div class="dashboardset_bg_topright"></div>

                    <!-- tab -->
                    <div class="dashboardset_tab">
                        <ul>
                            <li>
                                <a href="javascript:changeTabs('system')" name="systemTab" id="systemTab" class="dashboardset_tabon">
                                    <span class="left"></span>
                                    <span class="mid"><fmt:message key="dashboard.default"/>&nbsp; </span>
                                    <span class="right"></span>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:changeTabs('user')" name="userTab" id="userTab" class="dashboardset_taboff">
                                    <span class="left"></span>
                                    <span class="mid"><fmt:message key="dashboard.userdefine"/></span>
                                    <span class="right"></span>
                                </a>
                            </li>

                        </ul>
                    </div>


                    <!-- dashboard list -->
                    <div class="dashboard_list">
                        <div id="dashboardList"></div>
                    </div>


                    <!-- dashboard details -->
                    <div class="dash-detail-title"><fmt:message key="dashboard.detail.title"/></div>
                    <div id="dashboardDetail"></div>



                    <!-- dashboard add -->
                    <div class="dash-add-title"><fmt:message key="dashboard.add.title"/></div>
                    <div id="dashboard-AddForm">
                        <form:form id="addForm" modelAttribute="dashboard">
                            <ul>
                                <label class="type2"><fmt:message key="aimir.name"/></label>
                                <li><input type="text" name="name" maxlength="80"/></li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="aimir.orderNo"/></label>
                                <li><input type="text" name="orderNo" maxlength="80" class="type2"/></li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="dashboard.grid.maxX"/></label>
                                <li class="type2">
                                    <select id="maxGridX" name="maxGridX">
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                    </select>
                                </li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="dashboard.grid.maxY"/></label>
                                <li class="type2">
                                    <select id="maxGridY" name="maxGridY">
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                    </select>
                                </li>
                            </ul>
                            <ul>
                                <label class="type3"><fmt:message key="aimir.description"/></label>
                                <li class="type3"><textarea name="descr"></textarea></li>
                            </ul>
                            <ul id="addBtn" style="display:none;">
                                <li>
                                    <div class="btn-gr">
                                        <ul><li><a href="javascript:submitType('add')" class="on"><fmt:message key="aimir.add"/></a></li></ul>
                                        <ul><li><a href="javascript:clearSuvDiv()" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
                                    </div>
                                </li>
                            </ul>
                    </form:form>
                    </div>




                    <!-- dashboard modify -->
                    <div class="dash-modify-title"><fmt:message key="dashboard.modify.title"/></div>
                    <div id="dashboard-UpdateForm">
                        <form:form id="updateForm" modelAttribute="dashboard">
                            <ul>
                                <label class="type2"><fmt:message key="aimir.name"/></label>
                                <li><input type="text" name="name" maxlength="80" />
                                    <input type="hidden" name="id" /></li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="aimir.orderNo" /></label>
                                <li><input type="text" name="orderNo" maxlength="80" class="type2"/></li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="dashboard.grid.maxX"/></label>
                                <li class="type2"><select id="maxGridX" name="maxGridX">
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                    </select>
                                </li>
                            </ul>
                            <ul>
                                <label class="type2"><fmt:message key="dashboard.grid.maxY"/></label>
                                <li class="type2"><select id="maxGridY" name="maxGridY">
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                    </select>
                                </li>
                            </ul>
                            <ul>
                                <label class="type3"><fmt:message key="aimir.description"/></label>
                                <li class="type3"><textarea name="descr"></textarea></li>
                            </ul>
                            <ul id="modifyBtn" style="display:none;">
                                <li>
                                    <div class="btn-gr">
                                        <ul><li><a href="javascript:submitType('update')" class="on"><fmt:message key="aimir.update"/></a></li></ul>
                                        <ul><li><a href="javascript:clearSuvDiv()" class="on"><fmt:message key="aimir.cancel"/></a></li></ul>
                                    </div>
                                </li>
                            </ul>
                            </form:form>
                        </div>




            </div>
            <!-- Left Element : bg(top), tab, dashboard list, dashboard details/add/modify (E) -->



            <!-- Left Element : buttons, bg(bottom) (S) -->
            <!-- buttons -->
            <div class="dashboard_btn" style="display:block;">
                <ul id="modeBtn">
                    <li class="dashboard_modi"><a href="javascript:modifyDashboard();" title='<fmt:message key="aimir.update"/>'><fmt:message key="aimir.update" /></a></li>
                    <li class="dashboard_del"><a href="javascript:deleteDashboard();" title='<fmt:message key="aimir.button.delete"/>'><fmt:message key="aimir.button.delete" /></a></li>
                    <li class="dashboard_plus"><a href="javascript:addDashboard();" title='<fmt:message key="aimir.add"/>'><fmt:message key="aimir.add" /></a></li>
                </ul>
            </div>

            <!-- bg(bottom) -->
            <div class="dashboardset_bg2">
                <div class="dashboardset_bg_btmleft"></div>
                <div class="dashboardset_bg_btm"></div>
                <div class="dashboardset_bg_btmright"></div>
            </div>
            <!-- Left Element : buttons, bg(bottom) (E) -->





    </td>
    <td>


            <!-- Right Element : 가젯설정,가젯추가 (S) -->
            <!-- right-bg(top) -->
            <div class="gadgetset_header">
                <ul>
                    <li class="gadgetset_header_topleft"></li>
                    <li class="gadgetset_header_top"><fmt:message key="aimir.gadget.setting" /></li>
                    <li class="gadgetset_header_topright"></li>
                </ul>
            </div>

			<!--가젯 셋팅 (Ext JS)  -->
            <div class="gadgetset_flex"  style="display:block;">
				<ul>
				<br>
				<table style="margin-left:80px;width:1100px;">
					<tr>
						<td>
							<span><label class="check"></label></span><b><span id="dashboardName"></span></b>
						</td>
						<td style="padding-bottom: 15px">
						<!-- search -->
						<form name="gadgetform" method="post" onSubmit="return false;">
						    <span>
						    	<a href="javascript:gadgetSearch(document.gadgetform.sGadgetName.value);" >
						    	<img src="${ctx}/assets/images/btn_search.gif" id="searchImg"></img>
						    	</a>
						    </span>
							<input type="text" name="sGadgetName" id="searchGadget" onkeydown="javascript:keyEvent(event,document.gadgetform.sGadgetName.value);"/>
						</form>
							<span style="padding-left:10px;">(<fmt:message key='aimir.gadget.adddashboard'/>)</span>
						</td>
						<br>
					</tr>
					<tr>
						<td width="670">
							<!-- 대쉬보드에 따른 가젯 리스트 -->
							<div class="scrollbar" id="style-1">
								<div id="gadgetGroup"></div>
							</div>
						</td>	
						<td>
							<!-- 가젯 리스트 -->
							<div class="scrollbar1" id="style-1">
									<div id="panel1"></div>
							</div>
						</td>
					</tr>
				</table>
				</ul>
            </div>

            <!-- bg(bottom) -->
            <div class="gadgetset_bg2">
                <ul>
                    <li class="gadgetset_bg2_btmleft"></li>
                    <li class="gadgetset_bg2_btmright"></li>
                </ul>
            </div>
            <!-- Right Element : 가젯설정,가젯추가 (E) -->

    </td>
    </tr>
</table>
<!-- Outer Table (E) -->


</body>
</html>