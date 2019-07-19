<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>

<%@ include file="/gadget/system/preLoading.jsp"%>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>

<script type="text/javascript">

    var supplierId = "${supplierId}";
    var homeGroupId = "${homeGroupId}";
    var homeDeviceImgUrl = 'uploadImg/default/homeDeviceDefaultImg.jpg';
    var operatorId = "${operatorId}";

    //################## Drag & Drop Plugin START ######################//
     Ext.ux.CellFieldDropZone = Ext.extend(Ext.dd.DropZone, {
        constructor: function(){},
        init: function(grid) {
            if (grid.rendered) {
                this.grid = grid;
                this.view = grid.getView();
                //this.store = grid.getStore();
                Ext.ux.CellFieldDropZone.superclass.constructor.call(this, this.view.scroller);
            } else {
                grid.on('render', this.init, this);
            }
        },

    //  Scroll the main configured Element when we drag close to the edge
        containerScroll: true,

        getTargetFromEvent: function(e) {
    //      Ascertain whether the mousemove is within a grid cell
            var t = e.getTarget(this.view.cellSelector);
            if (t) {
                var rowIndex = this.view.findRowIndex(t);
                var columnIndex = this.view.findCellIndex(t);
                if ((rowIndex !== false) && (columnIndex !== false)) {
                    return {
                        node: t,
                        record: this.grid.getStore().getAt(rowIndex),
                        fieldName: this.grid.getColumnModel().getDataIndex(columnIndex)
                    };
                }
            }
        },
    
    //  On Node enter, see if it is valid for us to drop the field on that type of column.
        onNodeEnter: function(target, dd, e, dragData) {
            delete this.dropOK;
            if (!target) {
                return;
            }

    //      Check that a field is being dragged.
            var f = dragData.field;
            if (!f) {
                return;
            }

    //      Check whether the data type of the column being dropped on accepts the
    //      dragged field type. If so, set dropOK flag, and highlight the target node.
            if( target.fieldName == "MAPPINGIMGURL" && target.record.get("HOMEDEVICEIMGFILENAME") !="" && dragData.record.get("HOMEDEVICEGROUPNAME") == target.record.get("HOMEDEVICEGROUPNAME"))
            {
                this.dropOK = true;
                Ext.fly(target.node).addClass('x-drop-target-active');
            }else{
                this.dropOK = false;
                return;
            }
        },

    //  Return the class name to add to the drag proxy. This provides a visual indication
    //  of drop allowed or not allowed.
        onNodeOver: function(target, dd, e, dragData) {
            return this.dropOK ? this.dropAllowed : this.dropNotAllowed;
        },
    
    //   nhighlight the target node.
        onNodeOut: function(target, dd, e, dragData) {
            Ext.fly(target.node).removeClass('x-drop-target-active');
        },
    
    //  Process the drop event if we have previously ascertained that a drop is OK.
        onNodeDrop: function(target, dd, e, dragData) {
            if (this.dropOK) {
                if(chkDuplicatedMapping(target.record.get("ID"),dragData.record.get("MODEMID"), target.record.get("HOMEDEVICEGROUPNAME"))){
                    var targetE1 = Ext.get(target.node); 
                    targetE1.update("<img src='../../"+dragData.record.get("HOMEDEVICEIMGFILENAME")+"' width='50px' height='50px'></img>");
                    updateMappingInfo(target.record.get("ID"),dragData.record.get("MODEMID"), target.record.get("HOMEDEVICEGROUPNAME"));
                }
                return true;
            }
        }
    });
    
    //  A class which makes Fields within a Panel draggable.
    //  the dragData delivered to a coooperating DropZone's methods contains
    //  the dragged Field in the property "field".
    Ext.ux.CellFieldDragZone = Ext.extend(Ext.dd.DragZone, {
        constructor: function(){},
    
    //  Call the DRagZone's constructor. The Panel must have been rendered.
        init: function(grid) {
             if (grid.nodeType) {
                 Ext.ux.CellFieldDragZone.superclass.init.apply(this, arguments);
            } else {
                if (grid.rendered) {
                    this.grid = grid;
                    this.view = grid.getView();
                    //this.store = grid.getStore();
                    Ext.ux.CellFieldDragZone.superclass.constructor.call(this, this.view.scroller);
                } else {
                    grid.on('afterlayout', this.init, this);
                }
            }
        },
    
        scroll: false,
    
    //  On mousedown, we ascertain whether it is on one of our draggable Fields.
    //  If so, we collect data about the draggable object, and return a drag data
    //  object which contains our own data, plus a "ddel" property which is a DOM
    //  node which provides a "view" of the dragged data.
        getDragData: function(e) {
            var t = e.getTarget(this.view.cellSelector);

            if (t) {
               // e.stopEvent();
                if (Ext.isOpera) {
                    Ext.fly(t).on('mousemove', function(e1){
                        t.style.visibility = 'hidden';
                        (function(){
                            t.style.visibility = '';
                        }).defer(1);
                    }, null, {single:true});
                }
                var rowIndex = this.view.findRowIndex(t);
                var columnIndex = this.view.findCellIndex(t);
                var columnName = this.grid.getColumnModel().getDataIndex(columnIndex);
                var record = this.grid.getStore().getAt(rowIndex);

                if ( columnName == "HOMEDEVICEIMGFILENAME" && record.get(columnName) != "" ){
                    var d =  document.createElement('img');
                    d.setAttribute('src', '../../'+record.get(columnName));
                    Ext.fly(d).setWidth('50px');
                    Ext.fly(d).setHeight('50px');
                    if ((rowIndex !== false) && (columnIndex !== false)) {
                        return {
                        field: t,
                        repairXY: Ext.fly(t).getXY(),
                        ddel: d,
                        record: record
                        }
                    }
                }
            }
        },
    //  The coordinates to slide the drag proxy back to on failed drop.
        getRepairXY: function() {
            return this.dragData.repairXY;
        }
    });
    //################## Drag & Drop Plugin END ######################//

    $(document).ready(function(){
        // ExtJS의 tooltip초기화
    	Ext.QuickTips.init();

        if("${isNotService}" == "true") {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();
            hide();
            return;
        } else { // 해당 가젯에 대한 권한이 있을때
            $("#isNotService").hide();
        }
    	// 계약 번호 콤보박스 생성
        $("#contractNumber").selectbox();

        // 맵핑탭 클릭 이벤트 정의
        $("#mappingTabId").click(function() { displayDivTab("mappingTab"); });     

        // 제품 조회 클릭 이벤트 정의  (현재는 미표시)
        $("#deviceSearchTabId").click(function() { displayDivTab("deviceSearchTab"); });

        // 제품 등록탭 클릭 이벤트 정의
        $("#deviceRegistrationTabId").click(function() { displayDivTab("deviceRegistrationTab"); });

        // 계약정보 취득
        getContract();

        // 필요여부 확인 요
       /* if (result == 'noData'){
            $("#noData").html("no data to search!!!");
            return;
        }*/

        // 디폴트 탭 표시(맵핑탭이 디폴트로 표시된다.)
        displayDivTab("deviceSearchTab");

    });
    
    var divTabArray = [ "deviceSearchTab", "deviceRegistrationTab", "mappingTab" ];
    var divTabArrayLength = divTabArray.length;
    
    var displayDivTab = function(_currentDivTab) {
        
        for ( var i = 0; i < divTabArrayLength; i++) {

            if (_currentDivTab == divTabArray[i]) {
                
                $("#" + divTabArray[i]).show();
                $("#" + divTabArray[i] + "Id").addClass("tabcurrent");

                searchTab(i);
            } else {
                
                $("#" + divTabArray[i]).hide();
                $("#" + divTabArray[i] + "Id").removeClass("tabcurrent");
            }
        }
    };

    // 계약번호 콤보박스에서 계약번호 선택시 발생하는 이벤트
    var changeContract = function() {
        // 계약 정보 취득
        getContract();

        // 현재 활성화된 탭 재 표시
        searchTab();
    };

    // 탭 표시
    var searchTab = function(tabSeq) {
        emergePre();
        $.ajaxSetup({ async: false });

        if (tabSeq == null) {

            for (var i = 0; i < divTabArrayLength; i++) {

                if ($("#" + divTabArray[i]).css( "display" ) != "none") {

                    tabSeq = i;
                }
            }
        }

        switch (tabSeq) {

            case 0 :
                getDeviceSearch();
                break;

            case 1 :
            	deviceRegistration();
                break;

            case 2 :
                mapping();
                break;

            default :
                break;
        }
        $.ajaxSetup({  async: true  });
        hide();
    };


    $(window).resize(function() {
        var tabSeq;
        for (var i = 0; i < divTabArrayLength; i++) {

            if ($("#" + divTabArray[i]).css( "display" ) != "none") {

                tabSeq = i;
            }
        }

        switch (tabSeq) {

            case 0 :
            	getHomeDeviceGrid();
                break;

            /*case 1 :
                deviceRegistration();
                break;
            */
            case 2 :
            	mapping();
                break;

            default :
                break;
        }
    });

    // 계약 정보 취득
    // 계약번호 콤보박스에서 선택된 계약번호에 해당하는 계약 정보를 취득한다.
    var getContract = function() {
        $.ajaxSetup({ async: false });
        var params = {
                "contractId" : $("#contractNumber").val()
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/getContract.do",
                params,
                function(result) {

                    supplierId = result.contract.supplier;
                    homeGroupId = result.homeGroupId;
                }
            );
     $.ajaxSetup({ async: true });
    };

    /*****************************************************************/
    /* 조회 탭 관련 함수
    /* 1. getDeviceSearch   : 조회 탭 클릭 이벤트
    /* 2. makeSelectbox     : 조회 조건 콤보박스 생성 
    /* 3. getHomeDeviceGrid : 제품 조회 그리드 생성
    /*****************************************************************/
    // 조회탭 클릭, 제품정보를 취득하여 해당 탭에 표시
    var getDeviceSearch = function() {

        // 조회 조건 콤보 박스 생성
        makeSelectbox();

        // 조회 결과 그리드 추출
        getHomeDeviceGrid();

    };

    //  콤보박스 생성
    var makeSelectbox = function() {
        $.ajaxSetup({ cache: false });
        $.getJSON("${ctx}/gadget/homeDeviceMgmt/makeSelectBox.do",
                 {"homeGroupId":homeGroupId},
                 function(result) {
                      // 제품 그룹 리스트 박스 생성 
                      $('#selectHomeDeviceGroup').emptySelect().initSelect(); // 콤보박스에 All 설정
                      $.each(result.selectHomeDeviceGroup, 
                              function(index, optionData) {
                                $("#selectHomeDeviceGroup").append('<option value="' + optionData.NAME + '">' + optionData.NAME + '</option>');
                              }); 
                      $("#selectHomeDeviceGroup").selectbox();

                      // 제품 유형 리스트 박스 생성
                      $('#selectHomeDeviceCategory').emptySelect().initSelect(); // 콤보박스에 All 설정
                      $.each(result.selectHomeDeviceCategory, 
                              function(index, optionData) {
                                $("#selectHomeDeviceCategory").append('<option value="' + optionData.ID + '">' + optionData.NAME + '</option>');
                              }); 
                      $("#selectHomeDeviceCategory").selectbox();
                }
            );  
       $.ajaxSetup({ cache: true }); 
    };

    var homeDeviceGridOn = false;
    var homeDeviceGrid;

    // 제품조회 그리드 생성
    var getHomeDeviceGrid = function () {

        var width = $("#homeDeviceGrid").width();
        var store = new Ext.data.GroupingStore({
            autoLoad: true,
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceInfo.do?homeGroupId=" + homeGroupId + "&homeDeviceGroupName=" + $("#selectHomeDeviceGroup").val() + "&homeDeviceCategory=" + $("#selectHomeDeviceCategory").val(),
            reader: new Ext.data.JsonReader({
                root:'result',
                fields: ["ID", "HOMEDEVICEIMGFILENAME", "HOMEDEVICEGROUPNAME", "FRIENDLYNAME", "MAPPINGFRIENDLYNAME", "MAPPINGDRNAME", "USAGE"]
            }),
            groupField:'HOMEDEVICEGROUPNAME'
        });

        var colModel = new Ext.grid.ColumnModel({
            defaults: {
                width: (width / 4) - 1,
                menuDisabled: true
            },
            columns: [
                { id: "ID", header: "", dataIndex: "HOMEDEVICEIMGFILENAME",
                    renderer: function(value) {
                            var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                            return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                        } 
                },
                { header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/>", dataIndex: "FRIENDLYNAME", renderer:addTooltip},
                //{ header: "<fmt:message key='aimir.status'/>", dataIndex: "MAPPINGFRIENDLYNAME", renderer: isConnected},
                { header: "<fmt:message key='aimir.usage'/>(kWh)", dataIndex: "USAGE"},
                { header: "<fmt:message key='aimir.hems.label.levelDR'/>", dataIndex: "MAPPINGDRNAME"},
                { header: "", hidden:true, dataIndex: "HOMEDEVICEGROUPNAME"}
            ]
        });

        if (homeDeviceGridOn == false) {
            homeDeviceGrid = new Ext.grid.GridPanel({
                height: 432,
                store: store,
                colModel : colModel,
                columnLines: true,
                view: new Ext.grid.GroupingView({
                    deferEmptyText: false,
                    emptyText: "<fmt:message key='aimir.hems.homeDeviceMgmt.info.noRecords'/>",
                    //forceFit:true,
                    groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
                width: width
            });
            homeDeviceGrid.render("homeDeviceGrid");
            homeDeviceGridOn = true;
        } else {
        	homeDeviceGrid.setWidth(width);
            homeDeviceGrid.reconfigure(store, colModel);
        }
        homeDeviceGrid.getView().scrollOffset = 2;
    };

    var addTooltip = function(val, cell, record) {
        return '<div qtip="'+ val +'">'+ val +'</div>';     
    };

   /* var isConnected = function(val) {
        if (val.length == 0) {
            return "";
        } else if (val.length != 0) {
            return "<fmt:message key='aimir.hems.homeDeviceMgmt.label.endMapping'/>";
        }
        return val;
    };*/
    /*****************************************************************/
    /* 맵핑 탭 관련 함수
    /* 1. mapping              : 맵핑 탭 클릭 이벤트
    /* 2. chkDuplicatedMapping : 맵핑 중복 체크 
    /* 3. updateMappingInfo    : 맵핑 정보 갱신
    /* 4. resetMappingInfo     : 맵핑 해제시, 맵핑정보 리셋
    /*****************************************************************/
    var mappingGridOn = false;
    var gridLeft;
    var colModelLeft;
    var colModelRight;
    var gridRight;
    var displayPanel;

    // 맵핑탭에 표시할 정보를 생성 START
    var mapping = function() {
        var height = 460;
        var width = $("#homeDeviceMapping").width();
        var leftGridWidth = (width/2);
        var rightGridWidth = (width/2) + 17;  // 17 -> 스크롤의 폭을 더한 넓이로 설정

        var storeLeft = new Ext.data.GroupingStore({
            autoLoad: true,
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoLeft.do?homeGroupId=" + homeGroupId,
            reader: new Ext.data.JsonReader({
                root:'result',
                fields: ["MODEMID", "HOMEDEVICEIMGFILENAME", "HOMEDEVICEGROUPNAME", "FRIENDLYNAME"] 
            }),
            groupField:'HOMEDEVICEGROUPNAME'
        });
 
        var storeRight = new Ext.data.GroupingStore({
            autoLoad: true,
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoRight.do?homeGroupId=" + homeGroupId,
            reader: new Ext.data.JsonReader({
                root:'result',
                fields: ["ID", "HOMEDEVICEIMGFILENAME", "HOMEDEVICEGROUPNAME", "FRIENDLYNAME", "MAPPINGFRIENDLYNAME", "MAPPINGIMGURL", "MODEMID", "USAGE"] 
            }),
            groupField:'HOMEDEVICEGROUPNAME'
        });

        if (mappingGridOn == false) {
            colModelLeft = new Ext.grid.ColumnModel({
                columns: [
                        {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.smartConcent'/>", dataIndex: 'HOMEDEVICEIMGFILENAME', tooltip:"<fmt:message key='aimir.hems.homeDeviceMgmt.tooltip.dragDevice'/>", css:'background-color: #fff;',
                            renderer:  function(value,metaData) {
                                        if ( value !="") {
                                            var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                                            return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                                        }else{
                                            metaData.css ="x-grid-row-height";
                                        }
                                   }
                        },
                        {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/>", dataIndex: 'FRIENDLYNAME', renderer:addTooltip},
                        {header: "", hidden: true, dataIndex: 'HOMEDEVICEGROUPNAME'}
                    ],
                defaults: {
                    menuDisabled: true
                   ,width: (leftGridWidth / 2)
                }
            });

            gridLeft = new Ext.grid.GridPanel({
	            title: 'Drag Zone',
	            store: storeLeft,
	            colModel : colModelLeft,
	            plugins: new Ext.ux.CellFieldDragZone(),
	            view: new Ext.grid.GroupingView({
	                deferEmptyText: false,
	                emptyText: "<fmt:message key='aimir.hems.homeDeviceMgmt.info.noRecords'/>",
	                groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
	            }),
	            width: leftGridWidth,
	            height: height,
	            loadMask: true,
	            columnLines: true,
	            listeners:{
	                cellclick:function( gridLeft, rowIndex, columnIndex, e){
	                    var record = gridLeft.getStore().getAt(rowIndex); // rowIndex의get record
	                    var fieldName = gridLeft.getColumnModel().getColumnId(columnIndex); // columnIndex
	                    var data = record.get(fieldName);
	                    //Ext.get(e.target).setStyle("background-color","#FFCC00");
	                },
	                bodyscroll:function(x, y){
	                     gridRight.getView().restoreScroll({ left: x, top: y }); 
	                }
	            }
            });

            colModelRight = new Ext.grid.ColumnModel({
                columns: [
                          {id:'ID',header: "제품", dataIndex: 'HOMEDEVICEIMGFILENAME',
                              renderer:  function(value, metaData) {
                                          if ( value !="") {
                                              var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                                              return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                                          }else{
                                              metaData.css ="x-grid-row-height";  // 이미지가 없을 경우, 높이를 지정하여 다른 ROW와 맞춘다.
                                          }
                              }
                           },
                          {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.smartConcent'/>", dataIndex: 'MAPPINGIMGURL',tooltip:"<fmt:message key='aimir.hems.homeDeviceMgmt.tooltip.dropDevice'/>",   // MAPPINGIMGURL
                              renderer:  function(value, cell, record) {
                                          if ( value !="") {
                                              var tpl = new Ext.Template("<a href='javascript:resetMappingInfo("+record.data.ID+");' title='<fmt:message key="aimir.hems.homeDeviceMgmt.tooltip.deleteMapping"/>'>" + 
                                                      "<img src='../../images/cross.gif' /></a>" + 
                                                      "<img src='../../{MAPPINGIMGURL}' width='50px' height='50px' title='" +record.data.MAPPINGFRIENDLYNAME+"'></img>");
                                              return tpl.apply({MAPPINGIMGURL: value});
                                          }//else {
                                              //var tpl = new Ext.Template("Drop Home Device here!");
                                          //}
                              } 
                          },
                         // {header: "<fmt:message key='aimir.hems.homeDeviceMgmt.label.smartConcentName'/>", width: 70, dataIndex: 'MAPPINGFRIENDLYNAME'},
                          {header: "<fmt:message key='aimir.usage'/>(kWh)", dataIndex: 'USAGE', tooltip:"<fmt:message key='aimir.usage'/>(kWh)", renderer:addTooltip},
                          {header: "", hidden: true, dataIndex: 'HOMEDEVICEGROUPNAME'}
                      ],
                defaults: {
                    menuDisabled: true
                    ,width: (rightGridWidth / 3) + 10
                }
            });

            gridRight = new Ext.grid.GridPanel({
                title: 'Drop Zone',
                id:'gridpanel',
                store: storeRight,
                colModel : colModelRight,
                plugins: new Ext.ux.CellFieldDropZone(),
                view: new Ext.grid.GroupingView({
                    deferEmptyText: false,
                    emptyText: "<fmt:message key='aimir.hems.homeDeviceMgmt.info.noRecords'/>",
                    //forceFit:true,
                    groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
                width: rightGridWidth,
                height: height,
                loadMask: true,
                columnLines: true,
                listeners:{
                    cellclick:function( gridRight, rowIndex, columnIndex, e){
                        var record = gridRight.getStore().getAt(rowIndex); // rowIndex의get record
                        var fieldName = gridRight.getColumnModel().getColumnId(columnIndex); // columnIndex
                        var data = record.get(fieldName);
                    },
                    bodyscroll:function(x, y){
                            gridLeft.getView().restoreScroll({ left: x, top: y }); 
                    }
                }
            });

            //Simple 'border layout' panel to house both grids
            displayPanel = new Ext.Panel({
                width        : '100%',
                height       : height,
                border       : false,
                layout       : 'hbox',
                defaults     : { flex : 1 }, //auto stretch
                layoutConfig : { align : 'stretch' },
                items        : [
                    gridLeft,
                    gridRight
                ],
                renderTo: 'homeDeviceMapping'
            });
            mappingGridOn = true;
            gridRight.setPosition(gridLeft.getWidth() - 17,0);
        } else {

        	gridLeft.setWidth(leftGridWidth);
        	gridRight.setWidth(rightGridWidth);
            displayPanel.setWidth(width);
            gridRight.setPosition(gridLeft.getWidth() - 17,0);

            gridLeft.reconfigure(storeLeft, colModelLeft);
            gridRight.reconfigure(storeRight, colModelRight);
        } // if (mappingGridOn == false) END
    }; // mapping END
    // 맵핑탭에 표시할 정보를 생성 END

    // 중복 맵핑 체크
    var chkDuplicatedMapping = function(id, modemId, homeDeviceGroupName) {

        var storeRight = gridRight.getStore();
        var foundItem = storeRight.findExact('HOMEDEVICEGROUPNAME', homeDeviceGroupName);
        for ( var i = foundItem; i<storeRight.getCount(); i++)
        {
            var record = storeRight.getAt(i);
            if (record.get('HOMEDEVICEGROUPNAME') == homeDeviceGroupName && record.get('MODEMID') == modemId){
                alert(modemId);
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.smartConcentMapping'/>",  "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.connected'/>", function() {});
                return false;
            }else if (record.get('HOMEDEVICEGROUPNAME') != homeDeviceGroupName){
                return true;
            }
        }
        return true;
    };

    //  맵핑정보 갱신
    var updateMappingInfo = function(id, modemId, homeDeviceGroupName) {
        var params = {
                "id" : id,
                "modemId" : modemId
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/updateMappingInfo.do",
                params,
                function(result) {
                     if (result.status == "SUCCESS") {
                         Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.smartConcentMapping'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.info.successMapping'/>", function(){searchTab(2);});
                     }else {
                         Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.smartConcentMapping'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.info.failMapping'/>", function(){ });
                     }
                }
        );
    };

    // 맵핑 해제시, 맵핑정보 리셋
    var resetMappingInfo = function(endDeviceId) {
        $.ajaxSetup({ cache: false });        
        var params = {
                "endDeviceId" : endDeviceId
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/resetMappingInfo.do",
                params,
                function(result) {
                     if (result.status == "SUCCESS") {
                         Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.smartConcentMapping'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.info.disconnect'/>", function(){searchTab(2);});
                     }else {
                         Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.smartConcentMapping'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.info.failDisconnect'/>", function(){ });
                     }
                }
        );
        $.ajaxSetup({ cache: true });     
    };

    /*****************************************************************/
    /* 등록 탭 관련 함수
    /* 1. deviceRegistration  : 등록 탭 클릭 이벤트
    /* 2. registerHomeDevice  : 등록 버튼 클릭 이벤트 
    /* 3. checkInput          : 입력항목의 유효성 체크
    /* 4. changeID            : 식별아이디 변경시, 유효성 플러그 초기화
    /* 5. checkID             : 식별아이디의 유효성 체크
    /* 6. reset               : 입력 폼 값 리셋
    /* 7. changeHomeDeviceImg : 이미지 변경 버튼 클릭 이벤트
    /*****************************************************************/
    // 제품 정보 등록 탭  START
    var array;
    var deviceRegistration = function() {
        // 디폴트 이미지 셋팅
       $("#homeDeviceImgView").html("<img src='../../"+homeDeviceImgUrl+"' width='80px' height='93px'>");

       // 제품 그룹 리스트 박스 생성
       $('#homeDeviceGroupName').selectbox();
 
       // 제품 유형 리스트 박스 생성
       $.getJSON('${ctx}/gadget/system/getChildCode.do'
               , {'code': '13'}
               , function (returnData){
                   array = returnData.code;
                   $('#homeDeviceCategory').pureSelect(returnData.code);
                   $('#homeDeviceCategory').selectbox();
       });
       changeRegisterForm();
    };
    // 제품 정보 등록 탭 END 
    var isGeneralAP;
    var changeRegisterForm = function() {
        var count = array.length;

        for (var i = 0; i < count; i++) {

            if(array[i].id == $('#homeDeviceCategory').val() && array[i].code == "13.2") { // 제품 유형이 일반 가전일 경우

            	$("#idTR").hide();
            	$("#btn_chkID").hide();
            	$("#checkBTN").show();
            	isGeneralAP = true;
            	return;
            }else{
                $("#idTR").show();
                $("#btn_chkID").show();
                $("#checkBTN").hide();
                isGeneralAP = false;
            }
        }
    };

    // 등록 버튼 클릭 이벤트
    var registerHomeDevice = function() {
 
        // 입력값 체크
        if ( checkInput() ) {
            var params = {
                    "homeGroupId" : homeGroupId,                    
                    "homeDeviceGroupName" : $("#homeDeviceGroupName").val(),
                    "homeDeviceCategory" : $("#homeDeviceCategory").val(),
                    "identificationID" : $("#identificationID").val(),                    
                    "friendlyName" : $("#friendlyName").val(),
                    "homeDeviceImg" : homeDeviceImgUrl,
                   // "supplierID" : supplierId,
                    "operatorId" : operatorId,
                    "contractId" : $("#contractNumber").val()
            };

            $.post("${ctx}/gadget/homeDeviceMgmt/registerHomeDevice.do",
            	params,
                function(result, status) {
	            	 if (result.status == "SUCCESS") {
	                     homeGroupId = result.homeGroupId;
	                     Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.information.successInsert'/>", function() {reset();});
	                 } else {
	                     Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.alert.failInsert'/>", function() { });
	                 }
                },
                "json"
            );
            

            /*$.getJSON("${ctx}/gadget/homeDeviceMgmt/registerHomeDevice.do",
                    params,
                    function(result) {
                        if (result.status == "success") {
                            homeGroupId = result.homeGroupId;
                            Ext.MessageBox.alert('Home Device Registration', '성공적으로 등록 했습니다.', function() {reset();});
                        } else {
                            Ext.MessageBox.alert('Home Device Registration', '등록에 실패했습니다.', function() { });
                        }
                    }
            );    */       
        } 
    };

    // 입력항목의  유효성 체크
    var checkInput = function() {

        if(!isGeneralAP) { // 제품 유형이 일반 가전이 아닐경우
        	 // 식별ID의 필수 체크
            if ( $("#identificationID").val().length <= 0 ) {
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputIdentificationID'/>", function() {$("#identificationID").focus(); });
                return false;
            } 

            // 식별ID의 유효성 체크 여부 체크
           /* if ( idChecked == false ) {
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.checkIdentificationID'/>", function() {$("#identificationID").focus(); });
                return false;
            } 
            */
        } else {
            // 식별ID의 유효성 체크 여부 체크
            if ( nameChecked == false ) {
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.checkDeviceName'/>", function() {$("#friendlyName").focus(); });
                return false;
            }  
        }

    	// 제품 명칭 필수 체크
        if ( $("#friendlyName").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputDeviceName'/>", function() { $("#friendlyName").focus();});
            return false;
        }
        return true;
    };

    var idChecked = false;

    // 식별ID변경시, 유효성 체크 플러그의 초기화
 /*   var changeID = function() {

        idChecked = false;
    };
    */

    // 입력한 식별 ID의 유효성 체크 START
    var checkID = function() {

    	// 식별아이디 필수 체크
        if ( $("#identificationID").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputIdentificationID'/>", function() {$("#identificationID").focus(); });
            return;
        } else {
            // 식별ID의 유효성 체크
            if ( $("#identificationID").val().match(/[^0-9A-Za-z]+/) != null){
                Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.onlyAlphNumIdentificationID'/>", function() {$("#identificationID").focus(); });
                return;
            }
            /*
            var params = {
                "identificationID" : $("#identificationID").val(),
                "homeDeviceCategory" : $('#homeDeviceCategory').val()
            };

            // 식별ID의 중복 체크
            $.getJSON('${ctx}/gadget/homeDeviceMgmt/checkId.do', params,
                function(result) {
                    if (result.status == "duplicate") {
                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.existIdentificationID'/>", function() {$("#identificationID").focus(); });
                        idChecked = false;
                    } else {
                        Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.availableIdentificationID'/>", function() { });
                        idChecked = true;
                    }
                    return;
                }
            );
            */
        }
    };
    // 입력한 식별 ID의 유효성 체크 END

    var nameChecked = false;

    // 제품 유형 변경시, 유효성 체크 플러그의 초기화
    var changeName = function() {

    	nameChecked = false;
    };
    
    var checkName = function() {
        
        // 제품 명칭 필수 체크
        if ( $("#friendlyName").val().length <= 0 ) {
            Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.inputDeviceName'/>", function() {$("#friendlyName").focus(); });
            return;
        } else {

            var params = {
                "friendlyName" : $("#friendlyName").val(),
               // "homeDeviceCategory" : $('#homeDeviceCategory').val(),
                "contractId" : $("#contractNumber").val()
            };
            // 제품 유형이 일반 가전일 경우, 중보 체크를 실시한다.
            $.post("${ctx}/gadget/homeDeviceMgmt/checkName.do",
                    params,
                    function(result, status) {
		                if (result.status == "duplicate") {
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alertduplicatedDeviceName'/>", function() {$("#friendlyName").focus(); });
		                    nameChecked = false;
		                    
		                } else {
		                    
		                    Ext.MessageBox.alert("<fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/>", "<fmt:message key='aimir.hems.homeDeviceMgmt.alert.availableDeviceName'/>", function() { });
		                    nameChecked = true;
		                }
		                return;
                    },
                    "json"
            );
        }       
    };

    //제품 등록 폼 값 리셋
    function reset() {
        // 디폴트 이미지 설정
        homeDeviceImgUrl = 'uploadImg/default/homeDeviceDefaultImg.jpg';
        $("#homeDeviceGroupName option:eq(0)").attr("selected", "selected");
        $("#homeDeviceGroupName").selectbox();
        $("#homeDeviceCategory option:eq(0)").attr("selected", "selected");
        $("#homeDeviceCategory").selectbox();
        $("#identificationID").val('');
        $("#friendlyName").val('');
        $("#homeDeviceImgView").html("<img src='../../"+homeDeviceImgUrl+"' width='80px' height='93px'>");
        // 선택된 제품 유형에 맞게 변경
        changeRegisterForm();
    }

    var imgWin;
    // 제품 등록탭 에서 이미지 변경 클릭 이벤트 START
    var changeHomeDeviceImg = function() {   
        var store = new Ext.data.JsonStore({
            autoLoad: true,
            url: "${ctx}/gadget/homeDeviceMgmt/getHomeDeviceImgName.do" ,            
            root: 'images',
            fields: ['imgName','imgUrl']
        });

        var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="thumb-wrap" id="{imgName}">',
                    '<div class="thumb"><img src="../../{imgUrl}" ></div>',
                    //'<span>{imgName}</span></div>',
                    '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
        );

        if(!imgWin){
            imgWin = new Ext.Window({
                title: "<fmt:message key='aimir.hems.homeDeviceMgmt.title.changeImg'/>",
                id: 'imgWinId',
                applyTo:'images-view',
                autoScroll: true,
                resizable: false,
                width:350,
                height:370,
                closeAction:'hide',
                items: new Ext.DataView({
                    store: store,
                    tpl: tpl,
                    autoHeight:true,
                    singleSelect: true,
                    overClass:'x-view-over',
                    itemSelector:'div.thumb-wrap',
                    emptyText: 'No images to display',
                    listeners: {
                        click: {
                            fn: function(dv,index,node, e){
                                homeDeviceImgUrl = dv.getRecord(node).get('imgUrl');
                                $("#homeDeviceImgView").html("<img src='../../"+homeDeviceImgUrl+"' width='80px' height='60px'>");
                                //imgWin.hide(this);
                                Ext.getCmp('imgWinId').hide();
                            }
                        }
                    }
                })
            });
        } // if(!imgWin){ END

        // 이미지 변경 윈도우 표시
        Ext.getCmp('imgWinId').show();
    };
    // 제품 등록탭 에서 이미지 변경 클릭 이벤트 END

</script>      
</head>

<body>
<div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span class="img_isNotService_elec_house"></span></div>
            <div class="isNotService_today_right">
                <table height='160'>
                <tr>    
                    <td><fmt:message key='aimir.hems.label.isNotService'/></td>
                </tr>
                </table>
            </div>
        </div>
</div>

<div id="wrapper">
    <!--contract no.-->
    <div class="topsearch">
		<div class="contract">
	    	<table>
				<tr>
					<td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
					<td>
						<select name="contractNumber" id="contractNumber" style="width:280px" onchange="javascript:changeContract();" >
		                    <c:forEach var="contract" items="${contracts}">
		                       <option value="${contract.id}">${contract.keyNum}</option>
		                    </c:forEach>
	                	</select>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="top_line"></div>	
 
	   <div id='noData'></div>
	    
	    <!-- tab : J query를 이용한 Tab메뉴로 해주세요!-->
	    <div class="hems_tab">
	        <ul>
	            <li><a id="deviceSearchTabId"><fmt:message key='aimir.hems.tabName.homeDeviceSearch'/></a></li>
	            <li><a id="mappingTabId"><fmt:message key='aimir.hems.tabName.smartConcentMapping'/></a></li>
	            <li><a id="deviceRegistrationTabId"><fmt:message key='aimir.hems.tabName.homeDeviceRegistration'/></a></li>
	        </ul>
	    </div>
	    <!--// tab -->
    
    </div>
    <!--//contract no.-->

    <!-- tab 1: 제품 조회 -->
    <div id="deviceSearchTab" class="today">
    	<div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.homeDeviceList'/></div>

        <div class="home_header">
            <table>
            
            <tr>
                <th><span class="icon_triangle"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceGroup'/> </th>
                <td class="vertical_top"> : </td>
                <td id="statusTd">
                	<select id="selectHomeDeviceGroup" name="selectHomeDeviceGroup" style="width:80px;" onchange="javascript:getHomeDeviceGrid();"></select>
                </td>
                <th class="padding_l10"><span class="icon_triangle"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceCategory'/> </th>
                <td class="vertical_top"> : </td>
                <td id="statusTd"> 
                	<select id="selectHomeDeviceCategory" name="selectHomeDeviceCategory" style="width:100px;" onchange="javascript:getHomeDeviceGrid();"></select>  
                </td>                    
            </tr>
        	</table>   
        </div>
        
        <div id="homeDeviceGrid"></div>
        
      <div id="main"></div>
        
    </div>
    <!--// tab 1: 제품 조회 -->

    <!-- tab 2: 스마트 콘센트 맵핑 -->
    <div id="mappingTab"  class="today">
        <div>
            <div class="title_basic" title="<fmt:message key='aimir.hems.homeDeviceMgmt.Info.mapping'/>"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.mappingList'/></div>
        </div>
        <div id="homeDeviceMapping"></div>
    </div>
    <!--// tab 2: 스마트 콘센트 맵핑 -->

    <!-- tab 3: 제품 등록 -->
    <div id="deviceRegistrationTab" class="today">
        <div>
            <div class="title_basic" ><span class="icon_title_blue"></span><fmt:message key='aimir.hems.homeDeviceMgmt.label.inputHomeDevice'/></div>
        </div>
        <div class="homedevice">

           <div class="align_right text_gray7"><fmt:message key='aimir.hems.inform.requiredField'/></div>
            <!-- term data table -->
           <table class="member_join">
           <colgroup>
           		<col width="30%" />
           		<col width="" />
           	</colgroup>
            <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceGroup'/><em class="icon_star">&nbsp;</em></th>
               <td><span>   
                  <select name="homeDeviceGroupName" id="homeDeviceGroupName" style="width:140px">                      
                      <c:forEach var="homeDeviceGroup" items="${homeDeviceGroupList}" varStatus="idx" >
                          <option value="${homeDeviceGroupList[idx.index]}">${homeDeviceGroupList[idx.index]}</option>
                      </c:forEach> 
                  </select>
                </span>
                </td>
            </tr>
            <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.HomeDeviceCategory'/><em class="icon_star">&nbsp;</em></th>
                <td ><select name="homeDeviceCategory" id="homeDeviceCategory" style="width:140px" onchange="javascript:changeRegisterForm();" ></select></td>
            </tr>
            <tr id='idTR'><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.idenficationID'/><em class="icon_star">&nbsp;</em></th>
                <td>
                <!-- <span class="margin_r5"><input type="text" name="identificationID" id="identificationID" style="IME-MODE:disabled" onchange="javascript:changeID();" /></span> -->
                <span class="margin_r5"><input type="text" name="identificationID" id="identificationID" style="IME-MODE:disabled" /></span>
                <span class="hm_button" id='btn_chkID'><a href="javascript:checkID();"><fmt:message key='aimir.hems.label.checkId'/></a></span>
                </td>
            </tr>
            <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.friendlyName'/><em class="icon_star">&nbsp;</em></th>
                <td>
                <span class="margin_r5"><input name="friendlyName" id="friendlyName" type="text"  onchange="javascript:changeName();" /></span>
                <span id='checkBTN' class="hm_button"><a href="javascript:checkName();"><fmt:message key='aimir.checkDuplication'/></a></span>
                </td>
            </tr>
            
            <tr><th><fmt:message key='aimir.hems.homeDeviceMgmt.label.img'/></th>
                <td><div id="homeDeviceImgView"  class="imgview"></div>
                <span class="hm_button"><a href="javascript:changeHomeDeviceImg();"><fmt:message key='aimir.hems.homeDeviceMgmt.btn.changeImg'/></a></span>
                </td>
            </tr>           
        </table>

        <!--button-->
         <div class="rightbtn margin_t30">
              <a href="javascript:registerHomeDevice();" class="btn_blue" ><span><fmt:message key='aimir.button.register'/></span></a>
         </div>
        <!--//button-->

        <!--change home Device Image Window-->        
        <div id="images-view" class='x-hidden'></div>
        <!--//change home Device Image Window-->
        </div>
    </div>
    <!--// tab 3: 제품 등록 -->
</div>
</body>
</html>