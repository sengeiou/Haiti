<%@ include file="/taglibs.jsp"%>

<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", -1); //prevents caching at the proxy

%>
<html>
<head>
	<meta content='IE=8,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>사용자 관리</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.checkbox.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.contextmenu.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.themeroller.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_flat.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_nested.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
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
            font-weight: bold;
        }
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }

        .accept {
            background-image:url(../../images/allOn.png) !important;
        }
        .task-master {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/user.png) !important;
            
        }
    </style>
    <script type="text/javascript" charset="utf-8">
    var roleId = '';
    var operatorId='';
    var flexLoginLogGrid;
    var fmtMessage = new Array();
    var fmtMessage2 = new Array();
	var tabId='';

    var grid = undefined;
    var gridOn = false;
    
    var grid1 = undefined;
    var gridOn1 = false;

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:1};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    //윈도우 리싸이즈시 event
    $(window).resize(function() {

        if(tabId === "OperatorInfo"){
            if(!(grid === undefined)){
                grid.destroy();                
            }
            gridOn = false;
        }else{
            if(!(grid1 === undefined)){
                grid1.destroy();
            }
            gridOn1 = false;
        }

        getOperatorList();

            
    });  
    
    fmtMessage[0] = "<fmt:message key="aimir.loginId"/>";    // 아이디
    fmtMessage[1] = "<fmt:message key="aimir.name.user"/>";   // 사용자 이름
    fmtMessage[2] = "<fmt:message key="aimir.tel.no"/>";  // 연락처
    fmtMessage[3] = "<fmt:message key="aimir.email"/>";    // E-mail
    fmtMessage[4] = "<fmt:message key="aimir.user.logindenied"/>";      // 접속제한
    fmtMessage[5] = "<fmt:message key="aimir.board.location"/>";      // 지역
    
    fmtMessage[6] = "<fmt:message key="aimir.number"/>";   //번호
    fmtMessage[7] = "<fmt:message key="aimir.user.id"/>";  //사용자 아이디
    fmtMessage[8] = "<fmt:message key="aimir.user.group"/>"; //사용자 그룹
    fmtMessage[9] = "<fmt:message key="aimir.ipaddress"/>"; //ip 주소

    fmtMessage[10] = "<fmt:message key="aimir.login.login"/>" + " <fmt:message key="aimir.hour"/>"; //로그인 시간
    fmtMessage[11] = "<fmt:message key="aimir.login.logout"/>"+ " <fmt:message key="aimir.hour"/>"; //로그아웃 시간
    fmtMessage[12] = "<fmt:message key="aimir.status"/>"; //상태

    
    fmtMessage2[0] = "<fmt:message key="aimir.groupInfo"/>"; //그룹정보
    fmtMessage2[1] = "<fmt:message key="aimir.view.user"/>"; //사용자정보
    fmtMessage2[2] = "<fmt:message key="aimir.login.history"/>";// 로그인 이력
    
    //초기시 로드 되는 함수.
    function init() {

		//alert('init');
    	roleId = $("#roleManage :hidden[name='roleId']").val();
    	supplierId = $("#roleManage :hidden[name='supplierId']").val();
    	tabId = "GroupInfo";
    	//alert('GroupInfo');
        $("#fragment-11").load("${ctx}/gadget/system/user_group_max.do?param=userDetailInfo&roleId=" + roleId + "&supplierId=" + supplierId);

        $('#operatorGroupInfo').show('fast', operatorTabListener());
        $('#operatorGroupInfo').tabs(1);

        $('#roleName').tabs(1);

    }

     //그룹 리스트를 가지고 온다
    function getOperatorList()
     {
    	//alert('getOperatorList');
        var roleManageObj = document.getElementById("roleManage");
        var selectedIdx = roleManageObj.roleName.selectedIndex;
        this.roleId = roleManageObj.roleName[selectedIdx].value;
        roleManageObj.roleId.value = this.roleId;

        $("#roleManage :hidden[name='roleId']").val(this.roleId);

        if(tabId === "OperatorInfo"){
        	 //사용자 목록 그리드
			//alert('OperatorInfo');
           	getOperatorListGrid({
            	roleId : this.roleId
            });
           
        }else if(tabId === "OperatorLoginLog"){
        	  //alert('OperatorLoginlog');
        	 //사용자 로그인 이력 그리드
        	getOperatorLoginLogGrid({
        		
        		roleId  : $('#roleName').val(),
        		loginId : $('#loginLogLoginId').val(),
        		ipAddr  : $('#loginLogIpAddr').val(),
        		login   : $('#loginLogLogin').is(':checked'),
        		logOut  : $('#loginLogLogOut').is(':checked'),
        		loginFail : $('#loginLogLoginFail').is(':checked'),
        		searchStartDate : $('#searchStartDate').val(),
        		searchEndDate   : $('#searchEndDate').val()
        	
            });
        }
    	openOperatorAddForm();
    }

    //사용자 목록 그리드  그리기.
    var operatorGridStore;
    var rowSize = 20;

    function getOperatorListGrid(params) {
        //alert('params : '+params.roleId);
        operatorGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize}},
            //url: "${ctx}/gadget/system/operator/getOperatorList.do",
            url: "${ctx}/gadget/system/operator/getOperatorListByRole.do",
            baseParams: {
                roleId : params.roleId
            },
            totalProperty: 'totalCount',
            root: 'gridDatas',
            fields: [
                {name: 'id', type: 'string'},
                {name: 'loginId', type: 'string'},
                {name: 'name', type: 'string'},
                {name: 'telNo', type: 'string'},
                {name: 'email', type: 'string'},
                {name: 'loginDenied', type: 'string'},
                {name: 'location', type: 'string'}
            ],
            listeners: {
                beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                },
                load: function(store, record, options) {
                    makeOperatorListGridPanel();
                }
            }
        });
        //console.log(operatorGridStore);
    }



    function makeOperatorListGridPanel() {
        var width = $("#OperatorListDiv").width();

        colModel = new Ext.grid.ColumnModel({
            defaults : {
                width : width,
                height : 443,
                sortable : true
            },
            columns : [
            {
                 header:fmtMessage[0],
                 align: 'left',
                 width: width/5,
                 dataIndex: "loginId"
            },{
                header: fmtMessage[1],
                align: 'left',
                width : width/5,
                dataIndex : "name"
            }, {
                header: fmtMessage[2],
                width: width/5,
                align: 'left',
                dataIndex: "telNo"
            },{
                header: fmtMessage[3],
                width: width/5,
                align: 'left',
                dataIndex: "email"
            },{
                header: fmtMessage[5],
                width: width/5,
                align: 'left',
                dataIndex: "location"
            }
            ]

        });
         //페이징 툴바 셋팅
        var pagingToolbar = new Ext.PagingToolbar({
            store : operatorGridStore,
            displayInfo : true,
            pageSize : rowSize,
            prependButtons : true,
        });
        //그리드 설정
        if (!gridOn) {
            grid = new Ext.grid.GridPanel({
                height : 443,
                renderTo : 'OperatorListDiv',
                store : operatorGridStore,
                colModel : colModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, rowIndex, record) {
                            var param = record.data;
                            getDetailOperator(param.id);
                        }
                    }
                }),
                width :width,
                bbar : pagingToolbar,
                viewConfig : {
                    // forceFit:true,
                     showPreview : true,
                     emptyText : 'No data to display'
                },
            });
            gridOn = true;
            //grid.on('rowclick',getDetailOperator, this);
        } else {
            var bottomToolbar = grid.getBottomToolbar();
            grid.reconfigure(operatorGridStore, colModel);
            bottomToolbar.bindStore(operatorGridStore);
        }
    }
    
    function getDetailOperator(operatorId){
    	 $("#operatorInfo").load("${ctx}/gadget/system/operator/detailOperator.do?operatorId=" + operatorId + "&roleId="+$("#roleManage :hidden[name='roleId']").val());
    	 operatorTabListener();
    }
    
    //사용자 로그인 이력 그리드 
    var operatorLoginlogGridStore;
    
    function getOperatorLoginLogGrid(params){

    	operatorLoginlogGridStore = new Ext.data.JsonStore({
			
			autoLoad: {params:{start: 0, limit: 10}},
			url: "${ctx}/gadget/system/operator/getLoginLogGrid.do",
			 
	        baseParams: {
	        	roleId  : params.roleId,
	        	loginId : params.loginId,
	        	ipAddr  : params.ipAddr,
	        	login   : params.login,
	        	logOut  : params.logOut,
	        	loginFail : params.loginFail,
	        	searchStartDate : params.searchStartDate,
	        	searchEndDate : params.searchEndDate
	        	
				},
			
			totalProperty: 'totalCnt',	
			root: 'gridData',
			idProperty		: 'No',
			fields: [
					   { name: 'no', type: 'string' },
				       { name: 'userId', type: 'string' },
				       { name: 'userName', type: 'string' },
				       { name: 'userGroup', type: 'string' },
				       { name: 'ipAddr', type: 'string' },
				       { name: 'loginTime', type: 'string' },
					   { name: 'logoutTime', type: 'string' },
					   { name: 'status', type: 'string' }
					
				        ],		                
			listeners: {
				 		 beforeload: function(store, options){
		                	options.params || (options.params = {});
		                	Ext.apply(options.params, {
		                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1 
		                         });
		               	 },
				            load: function(store, record, options){
				            	makeOperatorLoginLogGridPanel();
				            }	
			            
			            }
		});
    	//console.log(operatorLoginlogGridStore);
    }
	


    function makeOperatorLoginLogGridPanel(){


		//alert('makeOperatorLoginLogGrid');
		var width = $("#OperatorLoginLogDiv").width()-30;
		
		colModel1 = new Ext.grid.ColumnModel({
			defaults : {
				width : width,
				height : 300,
				sortable : true
			},
			columns : [
			{
				header: fmtMessage[6],
				align: 'center',		
				width : width/8-25,
				dataIndex : "no"
			},{
				header: fmtMessage[7],
				align: 'center',		
				width : width/8,
				dataIndex : "userId"
			}, {
                header: fmtMessage[1],
                width: width/8,
                align: 'center',
                dataIndex: "userName"
			},{
				header: fmtMessage[8],
                width: width/8,
                align: 'center',
                dataIndex: "userGroup"
			},{
				header: fmtMessage[9],
                width: width/8,
                align: 'center',
                dataIndex: "ipAddr"
			},{
				header: fmtMessage[10],
                width: width/8+5,
                align: 'center',
                dataIndex: "loginTime"
			},{
				header: fmtMessage[11],
                width: width/8+5,
                align: 'center',
                dataIndex: "logoutTime"
			},{
				header: fmtMessage[12],
                width: width/8,
                align: 'center',
                dataIndex: "status"
			}
            ]
           
		});
		 //페이징 툴바 셋팅
       var pagingToolbar1 = new Ext.PagingToolbar({
        	store: operatorLoginlogGridStore,
        	displayInfo: true,
        	pageSize:10,
        	prependButtons: true,
        });
		 
       /* var pagingToolbar1 = new Ext.PagingToolbar({
	           pageSize: 10,
	           store: operatorLoginlogGridStore,
	           displayInfo: true,
	           displayMsg: ' {0} - {1} / {2}'
	       });
        */
		//그리드 설정
		if (!gridOn1) {

			grid1 = new Ext.grid.GridPanel({

				height : 300,
				renderTo : 'OperatorLoginLogDiv',
				store : operatorLoginlogGridStore,
				colModel : colModel1,
				width :width,
				bbar: pagingToolbar1,
				viewConfig: {
                    // forceFit:true,
                     showPreview:true,
                     emptyText: 'No data to display'
                }

			});

			gridOn1 = true;
			
		} else {
			var bottomToolbar = grid1.getBottomToolbar();
			grid1.reconfigure(operatorLoginlogGridStore, colModel1);
			bottomToolbar.bindStore(operatorLoginlogGridStore);
		}
		
	} 	
			
    $(function(){
	
    	//그룹 탭 로딩시 
        init();

        // 사용자정보 탭 클릭 시
        $('#userInfoTab').click( function() {
         	
         	//alert("Operatorclick");
         	tabId = "OperatorInfo";
         	//openOperatorAddForm();
         	getOperatorList();
            
         });
        
        // 사용자로그인 로그 탭 클릭 시
        $('#userloginlogTab').click( function() {
         	
        	//alert("loginTabclick");
         	tabId = "OperatorLoginLog";
         	getOperatorList();
            
         });
        
        //사용자 등록 버튼 활성화 
        $('#operatorAddButton a#openOperatorAddForm').click( function() {
           // roleId = $("#roleManage :hidden[name='roleId']").val();
            //alert('roleId : '+ roleId);
           //$("#operatorInfo").load("${ctx}/gadget/system/operator/addOperator.do?roleId="+ roleId);
            openOperatorAddForm();
           
        });

        $('#operatorAddButton a#newGroupAdd_0').click( function() {
        	addPageView();
        });

        $('#operatorAddButton2 a#newGroupAdd_1').click( function() {
            addPageView();
        });
        
        //로그인 이력 조회버튼 클릭
        $('#btnLoginLog').click( function() 
   		{
        	//alert('loginlog검색 버튼 ');
        	 getOperatorList();
        });

        //새그룹 등록시 탭 비활성위해 aimir.groupInfo
        $("#tabDisable").html('<div id="tabDisable" class="ui-tabs-disabled">'
                + '       <ul>'
                + '<li class="leftmargin"><a href="#" name="operTab_disable">' + fmtMessage2[0] + '</a></li>'
                + '<li><a href="#" name="operTab_disable">' +fmtMessage2[1] + '</a></li>'
                + '<li><a href="#" name="operTab_disable">' + fmtMessage2[2] + '</a></li>'
                + '  </ul>'
                + '</div>');
        $("#tabDisable").hide();
        
        //##############################
        //excel 익스포트 클릭 이벤트
        //##############################
        var winObj;
        $('#excelExport2').click( function() {
        	
        	//alert("excelExport2");

        	var arrLoginStatusCheckedValue = new Array();      	
        	var loginStatusCheckedValue= "";      	
        	var roleId= "";
        	
        	
			$.each($("input[name='login_status_group[]']:checked"), function() {	
				//checked_values.push($(this).val());
				arrLoginStatusCheckedValue.push($(this).attr("id"));	
			});
			
			loginStatusCheckedValue = arrLoginStatusCheckedValue[0] + "@"+  arrLoginStatusCheckedValue[1] + "@"+  arrLoginStatusCheckedValue[2];
        	
			roleId = $("#roleManage :hidden[name='roleId']").val();
			
        	
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            var dateType = new Array("hour","day","period","week","month","","dayWeek","season","year");

            //탭타입값 설정 1-daily, 2-피리어드, 3-위클리
            obj.tabType = dateType[$('#searchDateType').val()];
            
            //loginLogLoginId
            obj.loginLogLoginId = $("#loginLogLoginId").val();
            
            //loginLogIpAddr
            obj.loginLogIpAddr= $("#loginLogIpAddr").val();
            
           // alert("탬타입==>"+ obj.tabType);
            
           // alert($('#searchDateType').val());

            if($('#searchDateType').val()==0)
            {
                obj.search_from = $('#searchStartDate').val()+$('#searchStartHour').val()
                +"@"+$('#searchEndDate').val()+ $('#searchEndHour').val();
            } 
            else 
            {
            	//날짜 설정 
                obj.search_from = $('#searchStartDate').val()+"@"+$('#searchEndDate').val();
            }

            obj.supplierId  = supplierId;
            obj.roleId  = roleId;

            //daily   tab 파라메터값 설정
            obj.dailyStartDate = $("#dailyStartDate").val();     

            //period tab 파라메터값 설정
            obj.periodType_input = $("#periodType_input").val();
            obj.periodStartDate = $("#periodStartDate").val();
            obj.periodEndDate = $("#periodEndDate").val();

            //weekly tab 파라메터값 설정
            obj.weeklyYearCombo_input = $("#weeklyYearCombo_input").val();
            obj.weeklyMonthCombo_input = $("#weeklyMonthCombo_input").val();
            obj.weeklyWeekCombo_input = $("#weeklyWeekCombo_input").val();

            //month tab 파라메터값 설정
            obj.monthlyYearCombo_input = $("#monthlyYearCombo_input").val();
            obj.monthlyMonthCombo_input = $("#monthlyMonthCombo_input").val();

           //LoginStatusCheckedValue
           //로긴 상태 체크값
           obj.loginStatusCheckedValue = loginStatusCheckedValue;

           if(winObj)
        	   winObj.close();
            //obj는 파라매터 값 jsp 에서 받아서 처리.
            winObj = window.open("${ctx}/gadget/system/operator/operatorExcelDownloadPopup.do", "LoginLogExcel", opts);
            winObj.opener.obj = obj;
        });
        
        
    });//function End

    function openOperatorAddForm() {
    	//alert('openOperatorAddForm');
	    roleId = $("#roleManage :hidden[name='roleId']").val();
	    //alert('add Form roleId : '+ roleId);
	    $("#operatorInfo").load("${ctx}/gadget/system/operator/addOperator.do?roleId="+ roleId + "&operatorId=" + operatorId);
	    resetOperatorInfo();
    }


    function openOperatorAddView() {
    	//alert('openOperatorAddView');
        roleId = $("#roleManage :hidden[name='roleId']").val();
        $("#operatorInfo").load("${ctx}/gadget/system/operator/addOperator.do?roleId="+ roleId + "&operatorId=" + operatorId);


    }

    function openOperatorUpdateView() {
    	//alert('openOperatorUpdateView');
          $('#operatorDetailForm').resetForm();
          $('#operatorInfo').load("${ctx}/gadget/system/operator/updateOperator.do?operatorId=" + this.operatorId);
    }

    //사용자 정보 초기화
    function resetOperatorInfo() { 
        this.operatorId='';
        var innerHtml = "";

        $('#operatorInfo').html(innerHtml);

        operatorTabListener();        
    }

    function operatorTabListener() {
    	//alert('operatorTabListener');
        $('#fragment-22 a#openOperatorAddForm').show();

        $('#fragment-22 a#addOperator').show();
        $('#fragment-22 a#cancelAddOperator').show();

        $('#fragment-22 a#updateOperator').show();
        $('#fragment-22 a#cancelUpdateOperator').show();
    }

    function operatorAddTabListener() {
          $('#fragment-22 a#addOperator').show();
          $('#fragment-22 a#cancelAddOperator').show();
    }

    function operatorUpdateTabListener() {
        $('#fragment-22 a#updateOperator').show();
        $('#fragment-22 a#cancelUpdateOperator').show();
    }

    //추가페이지로 이동 RoleController.java에서 호출
    //## user_group_max.jsp 페이지 호출 하는 func
    function addPageView() {   
        //추가페이지 이동시 상세보기 숨김.
        $("#pane-user-viewPage").hide();
        
        //뉴 그룹 등록 페이지를 보여준다
        $("#pane-user-addPage").show();
        
        $("#pane-user-addPage").load("${ctx}/gadget/system/user_group_max.do?param=addViewPage");

        $("#tabDisable").show();
        $("#tabEnable").hide();

        $("#fragment-11").hide();
        $("#fragment-22").hide();
        $("#fragment-90").hide();
    }

    // add_user_group_max.jsp 에서 등록화면 닫기 클릭시 ..
    function hiddenAddPage(){
        $("#pane-user-addPage").hide();
        $("#pane-user-viewPage").show();

        $("#tabDisable").hide();
        $("#tabEnable").show();

        $("#fragment-11").show();
        $("#fragment-22").show();
        $("#fragment-90").show();
    }

    </script>
</head>
<body>
  <!--검색-->
  <div class="w_auto height25px margin-t10px margin-l10">
    <form name="roleManage" id = "roleManage">
    <input type="hidden" name="supplierId" value=${supplierId} />
    <input type="hidden" name="roleId" value=${roleId} />
     <!-- Group&nbsp; --><select name="roleName" id="roleName" onchange="javascript:getOperatorList();" style="width:180px;"></select>
    </form>
  </div>
  <!--검색 끝-->

  <!-- 탭 박스 (S) -->
  <!-- 상단 탭 그룹 부분 -->
  <div id="operatorGroupInfo">
    <ul>   
      <div id="tabEnable">
        <ul>
          <li class="leftmargin"><a href="#fragment-11" name="operTab"><fmt:message key="aimir.role"/></a></li>
          <li><a href="#fragment-22" name="operTab" id="userInfoTab"><fmt:message key="aimir.tab.user"/></a></li>
          <li><a href="#fragment-90" name="operTab" id="userloginlogTab"><fmt:message key="aimir.login.history"/></a></li>
        </ul>
      </div>
	  <div id="tabDisable"></div>
	</ul>

    <!--추가 페이지  -->
    <div id="pane-user-addPage"></div>
    <!--추가 페이지  끝-->

    <!-- Tab 1ST : 그룹정보 (S) -->
    <div id="fragment-11"></div>
    <!-- Tab 1ST : 그룹정보 (E) -->


    <!-- Tab 2ND : 그룹사용자목록 (S) -->
    <div id="fragment-22">
        <form id="operatorAddButton">
          <div id="btn-right" class="btn_topright_operator">
            <ul><li><a href="#" id="openOperatorAddForm"><span class="greenbold11pt"><fmt:message key="aimir.user"/>&nbsp;<fmt:message key="aimir.button.register"/></span><!-- 사용자 그룹 등록 --></a></li></ul>
          </div>
        </form>

         <!--오른쪽 가젯리스트-->

        <div id="operatorInfo" class="floatright padding-t3px" style="width:450px;"></div>
        <!--오른쪽 가젯리스트 끝-->
         
        <div class="w_auto padding-t3px" style="margin-right:480px">
          <ul><li>
              <div class="headspace-enter"><label class="check"><fmt:message key="aimir.list.user"/></label></div>
        	  <div id="OperatorListDiv" style="margin-top:18px"></div>
        	  </li>
        	  </ul>
        </div>
    </div>
  <!-- Tab 2ND : 그룹사용자목록 (E) -->
  
  
  
	<!-- Tab 3RD : 로그인 이력 (S) -->
	<div id="fragment-90">

    <form id="operatorAddButton2">
    <div id="btn-right" class="btn_topright_operator">

    </div>
    </form>


        <!-- search-background DIV (S) -->
        <div class="search-bg-withouttabs with-dayoptions-bt">

          <div class="dayoptions-bt">
             <%@ include file="/gadget/commonDateTabButtonType6.jsp"%>
          </div>
          <div class="dashedline"><ul><li></li></ul></div>

          <div class="searchoption-container">
          
          <!-- 서치 옵션 부분.Login Logout, Login failed -->
            <table class="searchoption wfree">
              <tr>
                <td class="withinput"><fmt:message key="aimir.user.id"/></td>
                <td class="padding-r20px"><input id="loginLogLoginId" type="text"></input></td>
                <td class="withinput"><fmt:message key="aimir.ipaddress"/></td>
                <td class="padding-r20px"><input id="loginLogIpAddr" type="text"></input></td>
                <td class="padding-r20px">
                <div class="margin-t3px">
                
                <!-- 체크 박스 부분 -->
                <!-- 서치 옵션 부분.Login Logout, Login failed -->
                	<span><input id="loginLogLogin" name="login_status_group[]"     type="checkbox" class="checkbox_space" checked></span>
                  <span class="margin-t2px"><fmt:message key="aimir.login.login"/></span>
                  <span><input id="loginLogLogOut" name="login_status_group[]"    type="checkbox" class="checkbox_space" checked></span>
                  <span class="margin-t2px"><fmt:message key="aimir.login.logout"/></span>
                  <span><input id="loginLogLoginFail" name="login_status_group[]" type="checkbox" class="checkbox_space" checked></span>
                  <span class="margin-t2px"><fmt:message key="aimir.login.loginFail"/></span>
               </div>
               
                 </td>
                <td class="padding-r20px">
                	<span class="am_button"><a href="#" class="on" id="btnLoginLog"><fmt:message key="aimir.button.search" /></a></span>
                </td>
              </tr>
            </table>
          </div>
        </div>
        <!-- search-background DIV (E) -->

					<div id="gadget_body">

						<!-- 로그인 이력 플렉스 그리드 부분. -->
						<!-- 엑셀 export -->
					    <div id="btn" class="btn_right_top2 margin-t10px">
					        <ul><li><a id="excelExport2" href="#" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
					    </div>
					    <div id="OperatorLoginLogDiv" class="margin-t40px"></div>
					</div>
				</div>
				<!-- Tab 3RD : 로그인 이력 (E) -->

		</div>
		<!-- 탭 박스 (E) -->

</body>
</html>
