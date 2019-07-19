<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>그룹 관리</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/ux/css/MultiSelect.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold;
        }
        /* .x-form-field-wrap x-form-field { */
        .x-form-field-wrap .x-form-field {
            margin-top: 0px !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script> --%>
    <%-- <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script> --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ux/MultiSelect.js"></script>
    <script language="JavaScript">/*<![CDATA[*/

        var supplierId = "${supplierId}";
        var operatorId = "${operatorId}";

        var memberName = "<fmt:message key='aimir.membername'/>";
        var groupTypeFstValue = "${groupTypeFstValue}";
        // 수정권한
        var editAuth = "${editAuth}";

        var numberFormat = "${numberFormat}";

        $(document).ready(function() {
            $('#groupType').selectbox();
            $('#memberType').selectbox();

            getGridComboData();
            getGroupList();
            resetMemberSelectDatas();
            hide();

            if (editAuth == "true") {
                $("#groupBtnList").show();
                $("#memberBtnList").show();
                $("#arrowList").show();
            } else {
                $("#groupBtnList").hide();
                $("#memberBtnList").hide();
                $("#arrowList").hide();
            }
        });
        
        var loginId;
        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                        loginId = json.loginId;
                        $('.supplierArea').hide();
                        $('.space5').hide();
                    }else{
                        getSupplierList();
                    }
                }
        );
        
        var sysId='';
        var mcuId;
        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.onReady(function() {
            Ext.QuickTips.init();
            var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

            if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
                Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
                Ext.override(Ext.grid.ColumnModel, {
                    getTotalWidth : function(includeHidden) {
                        if (!this.totalWidth) {
                            var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                            this.totalWidth = 0;
                            for (var i = 0, len = this.config.length; i < len; i++) {
                                if (includeHidden || !this.isHidden(i)) {
                                    this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                                }
                            }
                        }
                        return this.totalWidth;
                    }
                });
                chromeColAdd = 2;
            }

            Ext.grid.EditorGridPanel.prototype.onEditComplete = function (ed, value, startValue) {
                this.editing = false;
                this.activeEditor = null;
                ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
                var r = ed.record;
                var field = this.colModel.getDataIndex(ed.col);
                // Setup the object to be passed to the validateedit event.
                var e = {
                    grid: this,
                    record: r,
                    field: field,
                    originalValue: startValue,
                    value: value,
                    row: ed.row,
                    column: ed.col,
                    cancel:false
                };
                if (this.fireEvent("validateedit", e) !== false && !e.cancel) {
                    r.set(field, value);
                    delete e.cancel;
                    this.fireEvent("afteredit", e);
                }
                this.view.focusCell(ed.row, ed.col);
            }
        });

        function memberSearch() {
            if (selectedGroupId == '') {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.alert.groupMgmt.msg17'/>");    // 먼저 그룹을 선택해 주세요.
                return;
            }
            getMemberSelectData();
        }

        function delTxt(type) {
            if( type == "group" ){
                $('#groupName').val('');
                flag = true;
            }
            else {
                $('#memberName').val('');
            }
            return;
        }

        var groupTypeComboData = new Array();
        var userAccessComboData = new Array();
        // Group Type Combo Data
        function getGridComboData() {
            //var combodata = null;
            groupTypeComboData = [
            <c:forEach var="groupType" items="${groupType}">
                {id: "${groupType.name}", name: "${groupType.name}"}<c:if test="${not status.last}">,</c:if>
            </c:forEach>
            ];

        }

        var selectedGroupId = "";
        var selectedGroupType = "";
        var selectedGroupName = "";
        var selectedGroupKey = "";
        var selectedRow = "";

        /* Group 리스트 START */
        var groupGridOn = false;
        var groupGrid;
        var groupStore;
        var groupColModel;
        var groupCheckSelModel;
        var getGroupList = function() {
            var width = $("#groupGridDiv").width();
            selectedGroupId = null;
            selectedGroupType = null;
            selectedGroupName = null;
            selectedGroupKey = null;
            
            emergePre();
            groupStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/system/getHomeGroupList.do",
                baseParams: {
                    operatorId : operatorId,
                    groupType : $("#groupType").val(),
                    groupName : $("#groupName").val(),
                    supplierId : supplierId
                },
                root:'result',
                fields: ["groupId", "groupName", "groupType", "sysId", "memCount", "mcuId"],
                listeners: {
                    load: function(store, record, options){
                        var len = store.getCount();
                        if (len > 0) {
                            // reload 후 해당 row 자동 선택
                            if (selectedGroupName != null) {
                                for (var i = 0; i < len; i++) {
                                    if (selectedGroupName == store.getAt(i).get("groupName")) {
                                        groupGrid.getSelectionModel().selectRow(i);
                                        selectedGroupName = null;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            var colWidth = width/4 - chromeColAdd;

            Ext.util.Format.comboRenderer = function(combo){
                return function(value){
                    var record = combo.findRecord(combo.valueField, value);
                    return record ? record.get(combo.displayField) : combo.valueNotFoundText;
                };
            };

            // create the combo instance
            var groupTypeCombo = new Ext.form.ComboBox({
                id : "grpType",
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                width : colWidth+width/30,
                store : new Ext.data.JsonStore({
                    id : 0,
                    data : groupTypeComboData,
                    fields : ["id", "name"]
                }),
                valueField : "id",
                displayField : "name",
                editable : false

            });

			var check;
            
            var headerGroupName = "<fmt:message key="aimir.group.name"/>";
            var headerGroupType = "<fmt:message key="aimir.grouptype"/>";
            var headerDcuSysId = "<fmt:message key="aimir.mcuid"/>";
            var headerMemberCount = "<fmt:message key="aimir.group.membercount"/>";

            var editCol = true;
            if (editAuth != "true") {
                editCol = false;
            }

            groupColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: headerGroupName, dataIndex: 'groupName', width: width/4, editable: editCol,
                    	editor: new Ext.form.TextField({
                            id : 'grpName'
                        })
                    }
                   ,{header: headerGroupType, dataIndex: 'groupType', width:width/4, editor: groupTypeCombo, editable: editCol,
                	   renderer: Ext.util.Format.comboRenderer(groupTypeCombo)}
                   ,{header: headerDcuSysId, dataIndex: 'sysId', width:width/4*0.5, editable: editCol,
                	   editor: new Ext.form.TextField({
                           id : 'grpSysId'
                       })}
                   ,{header: headerMemberCount, dataIndex: 'memCount', width: width/4, align:'right'}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
               }
            });

            if (groupGridOn == false) {
                groupGrid = new Ext.grid.EditorGridPanel({
                    store : groupStore,
                    colModel : groupColModel,
                    sm : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        moveEditorOnEnter : false,
                        listeners : {
                            rowselect : function(sm, row, rec) {
                                selectedGroupId = rec.get("groupId");
                                selectedGroupType = rec.get("groupType");
                                selectedGroupName = rec.get("groupName");
                                selectedGroupKey = rec.get("groupKey");
                                selectedRow = row;
                                sysId = rec.get("sysId");
                                mcuId = rec.get("mcuId");
                                $("#memberName").val(memberName);
                                if (selectedGroupId != "") {
                                	getMemberSelectAndSelected();
                                } else {
                                    resetMemberSelectDatas();
                                }
                            }
                        }
                    }),
                    autoScroll : true,
                    scroll : true,
                    width : width,
                    //height : 210,
                    height : 306,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    clicksToEdit : 1,
                    renderTo : 'groupGridDiv',
                    viewConfig : {
                        forceFit : true,
                        markDirty: false,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    listeners : {
                        beforeedit : function(e) {
                        	var record = e.record;
                            if (e.field == "groupName") {
                                if (record.get("memCount") != "" && record.get("memCount") > 0) {
                                    return false;
                                }
                            } else if (e.field == "groupType") {
                                if (record.get("memCount") != "" && record.get("memCount") > 0) {
                                    return false;
                                }
                            } else if (e.field == "sysId") {
                                if ((record.get("memCount") != "" && record.get("memCount") > 0)) {
                                    return false;
                                }
                            }
                            return true;
                        }, 
                         afteredit : function(e) {
                            var record = e.record;
                            var grid = e.grid;
                            var value = e.value;
                            var originalValue = e.originalValue;
                            
                            if (value != originalValue && !dupCheckGroupName(e)) {
                                return;
                            } else {
                            	if(value != originalValue) {
                            		saveGroup(grid, record);
                                }
                            }
                        }
                    }
                });
                groupGridOn = true;
            } else {
                groupGrid.setWidth(width);
                groupGrid.reconfigure(groupStore, groupColModel);
            }
            hide();
        };

        function addRow() {
            var store = groupGrid.getStore();
            var Plant = store.recordType;
            var p = new Plant({
                groupId : "",
                groupName : "",
                groupType : groupTypeFstValue,
                memCount : "",
                sysId	: ""
            });
            var length = store.getCount();
            groupGrid.stopEditing();
            groupStore.insert(length, p);
            groupGrid.startEditing(length, 0);
            groupGrid.getSelectionModel().selectLastRow();
        }

        // Group 삭제
        function deleteRow() {
        	
            var record = groupGrid.getSelectionModel().getSelected();

            if (record == null) {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg6"/>");
                return;
            }

            if (record.get("groupId") == null || record.get("groupId") == "") {
                groupStore.remove(record);
            } else {
                emergePre();
                // Member 가 있는 경우 삭제안됨
                if (record.get("memCount") != "" && record.get("memCount") > 0) {
                    Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.groupMgmt.msg7"/>");
                    hide();
                    return;
                }

                Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>',
                        function(btn) {
                            if (btn == 'yes') {
                            	 $.getJSON("${ctx}/gadget/system/deleteGroup.do"
                                         ,{groupId : record.get("groupId")}
                                         ,function(json) {
                                             hide();
                                             if (json.result == "success") {
                                                 resetMemberSelectDatas();
                                                 groupStore.reload();
                                             } else {
                                                 Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
                                             }
                                 });
                            } else if(btn == 'no'){
                            	hide();
                            }
                        });
            } 
        }
        
        function resetMemberSelectDatas() {
            memberSelectData = new Array();
            memberSelectedData = new Array();
            $("#resultCnt").html("0");
            getMemberSelect();
            getMemberSelected();
        }

        // Group Name 중복확인
        function dupCheckGroupName(e) {
        	
			var record = e.record;
			var grid = e.grid;
			var value = e.value;
			var originalValue = e.originalValue;
        	
            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/system/dupCheckGroupName.do",
                data: {operatorId : operatorId,
                    groupName : value
                },
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);
            
            if(result.result == "Y") {
            	Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.duplicateGroupName"/>",
                        function() {
            		record.set("groupName",originalValue);
                    grid.startEditing(e.row, 0);
                });
                hide();
                return false;
            } else {
            	return true;
            }
            
        }
        
        // Group 저장
        function saveGroup(grid, record) {
        	
            var store = grid.getStore();
            
            record.set("sysId",record.get("sysId").trim());
            record.set("groupName",record.get("groupName").trim());
            
            if(record.get("sysId") == "") {
            	Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.alert.inputDCUId"/>",
            			function() {
            		grid.startEditing(selectedRow, 2);
                });
            	return;
            } 
            
            emergePre();
			//cmdUpdateGroup을 사용할 경우 멤버를 추가하면서 그룹이 생성됨.
            $.getJSON("${ctx}/gadget/system/saveIHDHomeGroup.do"
                    ,{supplierId : supplierId,
            		  operatorId : operatorId,
                      groupId : record.get("groupId"),
                      groupName : record.get("groupName"),
                      groupType : record.get("groupType"),
                      mcuId : record.get("sysId")}
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            selectedGroupName = record.get("groupName");
                            store.reload();
                        } else if (json.result == "mcuNull") {
                        	Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.notexistDCUID"/>",
                        			function() {
                        		grid.startEditing(selectedRow, 2);
                            });
                        	return;
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                            store.reload();
                        }
            });
            
        }

        function memberSearchKeyEvent(event) {
            var evKeyup = null;
            if (event)
                // firefox
                evKeyup = event;
            else
                // explorer
                evKeyup = window.event;

            if (evKeyup.keyCode == 13) {
                getMemberSelectData();
            }
        }
        
        function memberTypeSearchKeyEvent(event) {
			if(selectedGroupId == "" || selectedGroupId == null) {
				Ext.Msg.alert("<fmt:message key="aimir.message"/>", "Pleas select the Group.");
				//$("#memberType option:eq(0)").attr("selected","selected");
				$("select[name='memberType'] option[value='']").attr("selected",true);
				return;
			}

			if($("#memberType").val() != "" && $("#memberType").val() != null) {
				selectedType = $("#memberType").val();
			} else {
				selectedType = selectedGroupType;
			}

			$.post("${ctx}/gadget/system/getHomeGroupMemberSelectData.do"
                    ,{mcuId : mcuId,
            		  groupId : selectedGroupId,
                      groupType : selectedType,
                      subType : selectedGroupType,
                      memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                      supplierId : supplierId
                      }
                    ,function(json) {
                        memberSelectData = json.result;
                        var len = memberSelectData.length;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                        getMemberSelect();
            });
            
        }
        
        var memberSelectData;
        var memberSelectStore;
        var memberSelectGrid;
        var memberSelectColModel;
        var memberSelectGridOn=false;
        var memberSelectCheckSelModel;
        
        var headerMemberType	= "<fmt:message key="aimir.memberType"/>";
        var headerMemberName	= "<fmt:message key="aimir.membername"/>";
        var headerLastTimeSync	= "<fmt:message key="aimir.lasttimesync"/>";
        var headerRegResult		= "<fmt:message key="aimir.mcu"/> <fmt:message key="aimir.device.RegResult"/>";

        // Member select
        function getMemberSelect() {
            var width = $("#memberSelectDiv").width();

            memberSelectStore = new Ext.data.ArrayStore({
                autoLoad: true,
                data : memberSelectData,
                fields: ["value", "text", "type"],
            });
            
            if(memberSelectGridOn == false) {
	            memberSelectCheckSelModel = new Ext.grid.CheckboxSelectionModel({
	            	checkOnly:true
	                ,dataIndex: 'text'
	            });
            }

             memberSelectColModel = new Ext.grid.ColumnModel({
                columns: [
					memberSelectCheckSelModel
                    ,{header: headerMemberType, dataIndex: 'type', width: $("#memberSelectDiv").width()/3}
                   ,{header: headerMemberName, dataIndex: 'text', width: $("#memberSelectDiv").width()/3*2}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width:  $("#memberSelectDiv").width()
               }
            });

            if (memberSelectGridOn == false) {
            	memberSelectGrid = new Ext.grid.EditorGridPanel({
                    store : memberSelectStore,
                    colModel : memberSelectColModel,
                    sm : memberSelectCheckSelModel,
                    autoScroll : true,
                    scroll : true,
                    width :  $("#memberSelectDiv").width(),
                    //height : 210,
                    height : 210,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    clicksToEdit : 1,
                    renderTo : 'memberSelectDiv',
                    viewConfig : {
                        forceFit : true,
                        markDirty: false,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
            	memberSelectGridOn = true;
            } else {
            	memberSelectGrid.setWidth( $("#memberSelectDiv").width());
            	memberSelectGrid.reconfigure(memberSelectStore, memberSelectColModel);
            }
        }

        var memberSelectedOn = false;
        var memberSelectedData;
        var memberSelectedStore;
        var memberSelectedGrid;
        var memberSelectedColModel;
        var memberSelectedGridOn=false;
        var memberSelectedCheckSelModel;
        
        // Member selected
        function getMemberSelected() {
            var width = $("#memberSelectedDiv").width();
			
            memberSelectedStore = new Ext.data.ArrayStore({
                autoLoad: true,
                data : memberSelectedData,
                fields: ["id", "text", "type", "isRegistration", 'lastSyncDate']
            });
            
            if(memberSelectedGridOn == false) {
	             memberSelectedCheckSelModel = new Ext.grid.CheckboxSelectionModel({
	            	checkOnly:true
	            }); 
            }
            
            if(memberSelectedGridOn == false) {
	            
            }
            
            memberSelectedColModel = new Ext.grid.ColumnModel({
                columns: [
                    memberSelectedCheckSelModel
                   ,{header: headerMemberType, dataIndex: 'type', width: $("#memberSelectedDiv").width()*2}
                   ,{header: headerMemberName, dataIndex: 'text', width: $("#memberSelectedDiv").width()*3}
                   ,{header: headerRegResult, dataIndex: 'isRegistration', width: $("#memberSelectedDiv").width()*2}
                   ,{header: headerLastTimeSync, dataIndex: 'lastSyncDate', width: $("#memberSelectedDiv").width()*3}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width:  $("#memberSelectedDiv").width()
               }
            });

            if (memberSelectedGridOn == false) {
            	memberSelectedGrid = new Ext.grid.EditorGridPanel({
                    store : memberSelectedStore,
                    colModel : memberSelectedColModel,
                    sm : memberSelectedCheckSelModel,
                    autoScroll : true,
                    scroll : true,
                    width :  $("#memberSelectedDiv").width(),
                    //height : 210,
                    height : 210,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    clicksToEdit : 1,
                    renderTo : 'memberSelectedDiv',
                    viewConfig : {
                        forceFit : true,
                        markDirty: false,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
            	memberSelectedGridOn = true;
            } else {
            	memberSelectedGrid.setWidth( $("#memberSelectedDiv").width());
            	memberSelectedGrid.reconfigure(memberSelectedStore, memberSelectedColModel);
            }
        }
        
        function getMemberSelectAndSelected() {
			var selectedType;
         	
         	if($("#memberType").val() == "" || $("#memberType").val() == null) {
         		selectedType = selectedGroupType;
         	} else {
         		selectedType = $("#memberType").val();
         	}
        
             $.post("${ctx}/gadget/system/getMemberSelectAndSelectedData.do"
                    ,{mcuId : mcuId,
            	      groupId : selectedGroupId,
                      groupType : selectedType,
                      subType : selectedGroupType,
                      memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                      supplierId : supplierId
                      }
                    ,function(json) {
                        memberSelectData = json.selectData;
                        memberSelectedData = json.selectedData;
                        var len = memberSelectData.length;
                        $("#resultCnt").html(Ext.util.Format.number(len,numberFormat));
                        
                        getMemberSelected();
                        getMemberSelect();
            });
        }

        function getMemberSelectData() {
         	var selectedType;
         	
         	if($("#memberType").val() == "" || $("#memberType").val() == null) {
         		selectedType = selectedGroupType;
         	} else {
         		selectedType = $("#memberType").val();
         	}
        
             $.post("${ctx}/gadget/system/getHomeGroupMemberSelectData.do"
                    ,{mcuId : mcuId,
            	      groupId : selectedGroupId,
                      groupType : selectedType,
                      subType : selectedGroupType,
                      memberName : ($("#memberName").val() == memberName) ? "" : $("#memberName").val(),
                      supplierId : supplierId
                      }
                    ,function(json) {
                        memberSelectData = json.result;
                        var len = memberSelectData.length;
                        $("#resultCnt").html(Ext.util.Format.number(len, numberFormat));
                        
                        getMemberSelect();
            });
        }

        function getMemberSelectedData() {
           $.getJSON("${ctx}/gadget/system/getHomeGroupMemberSelectedData.do"
                    ,{groupId : selectedGroupId,
        	 		  supplierId : supplierId
		           }
                    ,function(json) {
                        memberSelectedData = json.result;
                        getMemberSelected();
            });
        }

        function memberAdd() {
        	var memberSelectCheckArray = memberSelectCheckSelModel.getSelections();
        	var memberArray = memberSelectCheckArray[0].get("text");
        	for ( var i = 1; i < memberSelectCheckArray.length; i++) {
				memberArray += ","+memberSelectCheckArray[i].get("text");
			}
        	
            if (memberArray.length < 1) {
                return;
            }

            emergePre();
            
            $.post("${ctx}/gadget/system/addGroupMembers.do"
                    ,{groupId : selectedGroupId,
                      members : memberArray}
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
                            var record = groupGrid.getSelectionModel().getSelected();
                            var memCount = Number(record.get("memCount"));
                            var memberArr = memberArray.split(",");
                            var memberLen = memberArr.length;
                            var selectLen = memberSelectStore.getCount();
                            var selectVal = null;

                            for (var i = 0; i < selectLen; i++) {
                                selectVal = memberSelectStore.getAt(i).get("text");
                                for (var j = 0; j < memberLen; j++) {
                                    if (selectVal == memberArr[j]) {
                                        memberSelectStore.remove(memberSelectStore.getAt(i));
                                        selectLen--;
                                        i--;
                                        memCount++;
                                        break;
                                    }
                                }
                            }
                            $("#resultCnt").html(Ext.util.Format.number(selectLen,numberFormat));
                            record.set("memCount", memCount);
                            getMemberSelectedData();
                        
                        } else {
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
            });
        }
        
        function memberCMDAdd() {
        	
        	var memberSelectedCheckArray = memberSelectedCheckSelModel.getSelections();
        	var memberArray = new Array();
        	var modemIHDType = null;
        	for ( var i = 0; i < memberSelectedCheckArray.length; i++) {
				memberArray.push(memberSelectedCheckArray[i].get("text"));
				if(selectedGroupType =='IHD' && (memberSelectedCheckArray[i].get("type") == "Modem")) {
					modemIHDType = memberSelectedCheckArray[i].get("text");
				}
			}

            if (memberArray.length < 1) {
                return;
            }
        	
        	Ext.Msg.wait('Waiting for response.', 'Wait !');
        	if(modemIHDType != null) {
		   		//IHD 코디등록(cmdSetIHdTable)_2012~2013년 개발당시 IHD에 한함
		   		$.post("${ctx}/gadget/device/command/cmdSetIHDTable.do"
		                 ,{groupId : selectedGroupId,
		                   sensorId : modemIHDType,
		                   loginId : loginId}
		                 ,function(json) {
		                     hide();
		                     if (json.result == "SUCCESS" || json.status == "PASS") {
		                     	//그룹등록
		                     	cmdUpdateGroup(memberArray);
		                     } else {
		                    	 Ext.Msg.hide();
		                         Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
		                     }
		         });
        	} else {
        		cmdUpdateGroup(memberArray);
        	}
	    }
        
        function cmdUpdateGroup(memberArray) {

        	$.post("${ctx}/gadget/device/command/cmdUpdateGroup.do"
                    ,{groupType : selectedGroupType,
                      groupName : selectedGroupName,
                      groupId : selectedGroupId,
                      modemArray : memberArray,
                      loginId : loginId}
                    ,function(json) {
                        //그룹멤버등록
                        if (json.status == "SUCCESS") {
                        	memberSelectGrid.reconfigure(memberSelectStore, memberSelectColModel);
                            var record = groupGrid.getSelectionModel().getSelected();
                            var memCount = Number(record.get("memCount"));
                            var memberLen = memberArray.length;
                            var selectLen = memberSelectStore.getCount();
                            var selectVal = null;

                            for (var i = 0; i < selectLen; i++) {
                                selectVal = memberSelectStore.getAt(i).get("text");

                                for (var j = 0; j < memberLen; j++) {
                                    if (selectVal == memberArray[j]) {
                                        memberSelectStore.remove(memberSelectStore.getAt(i));
                                        selectLen--;
                                        i--;
                                        memCount++;
                                        break;
                                    }
                                }
                            }
                            $("#resultCnt").html(Ext.util.Format.number(selectLen,numberFormat));
                            record.set("memCount", memCount);
                            getMemberSelectedData();
                            Ext.Msg.hide();
                        } else {
                        	Ext.Msg.hide();
                            Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
            });
        }
        
        function memberDel() {
        	
        	var memberSelectedCheckArray = memberSelectedCheckSelModel.getSelections();
        	var memberIds="";
        	
            if (memberSelectedCheckArray.length < 1) {
                return;
            }

        	for ( var i = 0; i < memberSelectedCheckArray.length; i++) {
        		if(memberSelectedCheckArray[i].get("isRegistration") == "<fmt:message key='aimir.button.register'/>") {
        			Ext.Msg.alert("<fmt:message key="aimir.message"/>","<fmt:message key='aimir.homegroup.memberclose'/>");
        			return;        		
        		}
        		memberIds += memberSelectedCheckArray[i].get("id")+",";
			}
            
            $.getJSON("${ctx}/gadget/system/removeGroupMembers.do"
                    ,{memberIds : memberIds}
                    ,function(json) {
                        hide();
                        if (json.result == "success") {
   				             var record = groupGrid.getSelectionModel().getSelected();
   				             var memCount = Number(record.get("memCount"));
   				             var memberArr = memberIds.split(",");
   				             var memberLen = memberArr.length;
   				             var selectLen = memberSelectStore.getCount();
   				             var selectedLen = memberSelectedStore.getCount();
   				             var selectedVal = null;
   				
   				             for (var i = 0; i < selectedLen; i++) {
   				                 selectedVal = memberSelectedStore.getAt(i).get("id");
   				                 for (var j = 0; j < memberLen; j++) {
   				                     if (selectedVal == memberArr[j]) {
   				                         memberSelectedStore.remove(memberSelectedStore.getAt(i));
   				                         selectLen++;
   				                         selectedLen--;
   				                         i--;
   				                         memCount--;
   				                         break;
   				                     }
   				                 }
   				             }
   				             
   				             $("#resultCnt").val(Ext.util.Format.number(selectLen,numberFormat));
   				             record.set("memCount", memCount);
   				             getMemberSelectData();
   			           } else {
   			                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
   			           }
            });
            
        }
        
        function memberCMDDel(memberArray, memberIdArray) {
        	
        	var memberSelectedCheckArray = memberSelectedCheckSelModel.getSelections();
        	var memberArray = new Array();
			var modemIHDType = null;
        	for ( var i = 0; i < memberSelectedCheckArray.length; i++) {
				memberArray.push(memberSelectedCheckArray[i].get("text"));
				if(selectedGroupType == "IHD" && (memberSelectedCheckArray[i].get("type") == "Modem")) {
					modemIHDType = memberSelectedCheckArray[i].get("text");
				}
			}
        	
            if (memberArray.length < 1) {
                return;
            }
            
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            if (modemIHDType != null) {
            	//IHD 코디등록삭제(cmdDelIHdTable)_2012~2013년 개발당시 IHD에 한함
            	$.getJSON("${ctx}/gadget/device/command/cmdDelIHDTable.do"
                        ,{groupId : selectedGroupId,
            			  sensorId : modemIHDType,
                          loginId : loginId}
                        ,function(json) {
                        	Ext.Msg.hide();
                            if (json.status == "SUCCESS" || json.status == "PASS") {
                            	cmdDeleteGroup(memberArray, memberIdArray);
                            } else {
                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
                            }
                });
            } else {
            	cmdDeleteGroup(memberArray, memberIdArray);
            }
        }
        
        function cmdDeleteGroup(memberArray, memberIdArray) {
			$.getJSON("${ctx}/gadget/device/command/cmdDeleteGroup.do"
			           ,{groupType : selectedGroupType,
			             groupName : selectedGroupName,
			             groupId : selectedGroupId, 
			             modemArray : memberArray,
			             loginId : loginId}
			           ,function(json) {
			        	   Ext.Msg.hide();
			        	   if(json.status == "SUCCESS") {
			        		   getMemberSelectedData();
			        	   }
			   });
        }
        
        var detail = new Array();
        function memberSyncTask() {
        	
            var memberSelectedCheckArray = memberSelectedCheckSelModel.getSelections();
            var memberArray = new Array();
            var idList = new Array();
        	for ( var i = 0; i < memberSelectedCheckArray.length; i++) {
        		memberArray.push(memberSelectedCheckArray[i].get("text"));
        		idList.push(memberSelectedCheckArray[i].id);
			}
            if (memberArray.length < 1) {
                return;
            }
        	
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            for ( var i = 0; i < memberArray.length; i++) {
            	//loading 이미지 추가
             	memberSelectedStore.getById(idList[i]).set(
                        'isRegistration',
                        'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
             	
             	memberCMDSync(memberArray[i],i);
            }  
        }
        
         function memberCMDSync(member, index) {
        	 $.getJSON("${ctx}/gadget/device/command/cmdGetGroup.do"
			           ,{mcuId : sysId,
       				 groupType : selectedGroupType,
       				 groupName : selectedGroupName,
       				 sensorId  : member,
			             loginId : loginId}
			           ,function(json) {
			        	   Ext.Msg.hide();
			               if (json.status == "SUCCESS") {
			            	   if(json.result == "registration") {
			            		   memberSelectedStore.getAt(index).set(
			                               'isRegistration',
			                               '<fmt:message key='aimir.button.register'/>');
			            	   } else if(json.result == "close") {
			            		   memberSelectedStore.getAt(index).set(
			                               'isRegistration',
			                               '<fmt:message key='aimir.hems.label.close'/>');
			            	   }
			            	   getMemberSelectedData();
				           }
			   });
        } 
        
    </script>
</head>
<body>
    <!-- search-background DIV (S) -->
    <div class="search-bg-basic">

        <!--검색조건-->
        <div class="searchoption-container" style="margin-top: 5px;">
            <table class="searchoption wfree">
                <tr>
                    <td class="gray11pt withinput"><fmt:message key="aimir.grouptype"/></td>
                    <td>
                        <select id="groupType" style="width:120px">
                            <option value=""><fmt:message key="aimir.all"/></option>
                            <c:forEach var="groupType" items="${groupType}">
                            <option value="${groupType.name}">${groupType.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td class="space20"></td>

                    <td class="withinput"><fmt:message key="aimir.group.name"/></td>
                    <td><input id="groupName" type="text" style="width: 200px;"></td>
                    <td class="space20"></td>

                    <td>
                        <div id="btn">
                            <ul><li><a href="javascript:getGroupList();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <!--검색조건 끝-->

    </div>
    <!-- search-background DIV (E) -->

    <div id="groupBtnList">
        <div id="btn" class="btn_right_top2 margin-t10px">
            <ul><li><a href="javascript:addRow();" class="on-bold"><fmt:message key="aimir.button.addGroup" /></a></li></ul>
            <ul><li><a href="javascript:deleteRow();" class="on-bold"><fmt:message key="aimir.button.delete" /></a></li></ul>
        </div>
    </div>

    <div class="gadget_body">
        <div id="groupGridDiv"></div>
    </div>



    <!-- 그룹멤버추가 전체 (S) -->
    <div class="gadget_body">
    
        <!-- Step 02b - 멤버추가 (S) -->
        <div class="groupmanage-create-tab blueline bg-blue clear" style="border-bottom:none !important;">
            <label class="member bluebold11pt"><fmt:message key="aimir.memberAdd"/></label>
        </div>
    
        <div class="groupmanage-create-member blueline bg-blue clear" style="height: 300px !important;">
            <ul class="width">
            <li class="padding minustop">
    
                <div class="blueline-searchoption">
                    <ul class="row">
                        <li class="col">
                            <div class="searchoption wfree">
                            	<ul>
                            		<li>
				                        <select id="memberType" name="memberType" style="width:120px" onchange="javascript:memberTypeSearchKeyEvent(event);">
				                            <option value=""><fmt:message key="aimir.memberType"/></option>
				                            	<c:forEach var="memberType" items="${memberType}">
				                            <option value="${memberType.name}" >${memberType.name}</option>
				                            </c:forEach>
				                        </select>
				                    </li>
                                </ul>
                                <ul>
                                    <li>
                                        <input id="memberName" type="text" value="<fmt:message key='aimir.membername'/>" onclick="javascript:this.value = '';" onkeydown="javascript:memberSearchKeyEvent(event);"></li>
                                    <li class="search-s1-btn"><a href="javascript:memberSearch();" ></a></li>
                                </ul>
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="dashedline-dark clear" style="height: 9px;"></div>
                <!-- 그리드 (S) -->
                <div class="flexlist">
                    <!-- <div id="" style="width: 43%; float:left;"> -->
                    <div id="" style="width: 45%; float:left; padding-right: 15px;">
                        <ul>
                            <li style="padding-top: 9px">
                                <font style="font-weight:bold; color: #676767;"><fmt:message key="aimir.searchResult"/> : </font>
                                <font style="font-weight:bold; color: #FC0000;" id="resultCnt">0</font>
                            </li>
                            <li><div id="memberSelectDiv" style="margin-left:-1px"></div></li>
                        </ul>
                    </div>
                    <!-- <div id="memberSelectBtnDiv" style="width: 35px; float:left; align: center; valign: middle; padding-left: 10px;"> -->
                    <div id="memberSelectBtnDiv" style="width: 35px; float:left; align: center; valign: middle;">
                        <ul id="arrowList" style="margin-top: 80px;">
                            <li class="btn-putin"><a id="memberAddBtn" href="javascript:memberAdd();"><!-- 추가 --></a></li>
                            <li class="btn-putout"><a id="memberDelBtn" href="javascript:memberDel();"><!-- 제거 --></a></li>
                        </ul>
                    </div>
                    <!-- <div id="" style="width: 53%; float:left;"> -->
                    <div id="" style="width: 50%; float:left;">
                        <div id="memberBtnList">
                            <ul><li style="float: right; font-weight: bold;"><a href='javascript:memberSyncTask()' class='btn_blue' ><span><fmt:message key='aimir.mcu.membersync'/></span></a></li></ul>
                            <ul><li style="float: right; font-weight: bold;"><a href='javascript:memberCMDDel()' class='btn_blue' ><span><fmt:message key='aimir.hems.label.close'/></span></a></li></ul>
                            <ul><li style="float: right; font-weight: bold;"><a href='javascript:memberCMDAdd()' class='btn_blue' ><span><fmt:message key="aimir.button.register"/></span></a></li></ul>
                        </div>
                        <ul>
                            <li style="padding-top: 9px"><font style="font-weight:bold; color: #676767;"><fmt:message key="aimir.addItems"/></font></li>
                            <li><div id="memberSelectedDiv" style="margin-left:-1px"></div></li>
                        </ul>
                    </div>
                </div>
                <div style="clear:both;"></div>
    
                <%-- <div class="margin-t5px floatright">
                    <button type="button" class="sm_current" onclick="javascript:saveMember('false');"><fmt:message key="aimir.save2"/></button>
                    <button type="button" class="sm_current" onclick="javascript:saveMember('true');"><fmt:message key="aimir.button.saveNadd"/></button>
                    <button type="button" class="sm thelast" onclick="javascript:cancel2();"><fmt:message key="aimir.cancel"/></button>
                </div> --%>
    
            </li>
            </ul>
        </div>
        <!-- Step 02b - 멤버추가 (E) -->
    
    </div>
    <!-- 그룹멤버추가 전체 (E) -->

</body>
</html>