<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title>Report Management Mini Gadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
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
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var reportFileDir = "${reportFileDir}";
        var reportExportDir = "${reportExportDir}";
        // 수정권한
        var editAuth = "${editAuth}";

        $(function() {
            $("#tabs").tabs();
        });

        var tabs = {
            hourly : 0,
            daily : 1,
            period : 0,
            weekly : 1,
            monthly : 1,
            monthlyPeriod : 0,
            weekDaily : 0,
            seasonal : 0,
            yearly : 0
        };
        var tabNames = {};

        var tabsSnd = {
            //hourly : 0,
            //daily : 0,
            period : 1//--,
            //weekly : 0,
            //monthly : 0,
            //monthlyPeriod : 0,
            //weekDaily : 0,
            //seasonal : 0,
            //yearly : 0
        };
        var tabNamesSnd = {};

        $(document).ready(function(){
            // 권한체크
            if (editAuth == "true") {
                $("#saveBtn").show();
                $("#addresseeBtn").show();
            } else {
                $("#saveBtn").hide();
                $("#addresseeBtn").hide();
            }

            hide();
            $(function() { $('#resultTab').bind('click',function(event) { getReportResultList(); } ); });
            $(function() { $('#settingTab').bind('click',function(event) { getReportSettingData(); } ); });

            locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
            setSelectBox();     // 조건 combobox 생성
            getReportResultList();
            makeExtWindow();        // Email Contacts Window 생성
            makeCronHelpWindow();   // Cron Help Window 생성
        });

        function setSelectBox() {
            $.post("${ctx}/gadget/system/getReportMiniSelectBoxData.do",
                   function(json) {
                       $("#meterTypeCode").loadSelect(json.meterType);
                       $("#meterTypeCode option:eq(0)").remove();
                       $("#meterTypeCode option:eq(0)").attr("selected", "selected");
                       $("#meterTypeCode").selectbox();

                       $("#exportFormat").loadSelect(json.exportFormat);
                       $("#exportFormat option:eq(0)").remove();
                       $("#exportFormat option:eq(0)").attr("selected", "selected");
                       $("#exportFormat").selectbox();
                   });
        }

        // window resize event
        $(window).resize(function() {
            getReportResultList();
        });

        function sendSnd() {
            getReportResultList();
        }

        /* 스케줄결과 리스트 START */
        var reportResultGridOn = false;
        var reportResultGrid;
        var reportResultColModel;
        var reportResultCheckSelModel;
        var getReportResultList = function() {
            var width = $("#reportResultDiv").width();
            var rowSize = 8;

            //emergePre();
            var reportResultStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: rowSize}},
                url: "${ctx}/gadget/system/getReportResultList.do",
                baseParams: {
                    reportName : $("#reportName").val(),
                    startDate : $("#searchStartDateSnd").val(),
                    endDate : $("#searchEndDateSnd").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["resultId", "writeTime", "reportName", "resultLink", "resultFileLink", "metaLink"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            if(reportResultGridOn == false) {

                reportResultCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                    ,dataIndex: 'resultId'
                });
            }

            reportResultColModel = new Ext.grid.ColumnModel({
                columns: [
                    reportResultCheckSelModel
                   ,{header: '<fmt:message key="aimir.date"/>', dataIndex: 'writeTime', width: ((width-20)/3)*1.1}
                   ,{header: '<fmt:message key="aimir.report.mgmt.reporttitle"/>', dataIndex: 'reportName', width: ((width-20)/3)*1.1}
                   ,{header: '<fmt:message key="aimir.report.mgmt.reportview"/>', width: ((width-20)/3)*0.8 - 6,
                       renderer: function(value, metaData, record, index) {
                                     var btnHtml = "<a href='#;' onclick='openReportByData(\"{metaLink}\", \"{resultLink}\");' class='btn_blue'><span><fmt:message key="aimir.report.mgmt.view"/></span></a>"
                                                 + "<a href='#;' onclick='exportReport(\"{resultFileLink}\");' class='btn_blue'><span><strong class='smallfile_report'></strong></span></a>";
                                     var tplBtn = new Ext.Template(btnHtml);
                                     return tplBtn.apply({metaLink: record.get('metaLink'), resultLink: record.get('resultLink'), resultFileLink: record.get('resultFileLink')});
                       }
                   }
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if (reportResultGridOn == false) {
                var tbarDisabled = true;
                if (editAuth == "true") {
                    tbarDisabled = false;
                }

                reportResultGrid = new Ext.grid.GridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: reportResultStore,
                    colModel : reportResultColModel,
                    sm: reportResultCheckSelModel,
                    autoScroll:false,
                    width: width,
                    height: 335,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'reportResultDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:[{
                        text: '<fmt:message key="aimir.button.delete"/>',
                        scope: this,
                        iconCls:'remove',
                        disabled: tbarDisabled,
                        handler: function(){
                            deleteReportResultConfirm(reportResultCheckSelModel.getSelections());
                        }
                    },'-'],
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: rowSize,
                        store: reportResultStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                reportResultGridOn = true;
            } else {
                reportResultGrid.setWidth(width);
                var bottomToolbar = reportResultGrid.getBottomToolbar();
                reportResultGrid.reconfigure(reportResultStore, reportResultColModel);
                bottomToolbar.bindStore(reportResultStore);
            }
            hide();
        };

        // 선택한 Report Result Data Delete 확인창
        function deleteReportResultConfirm(delArray) {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           deleteReportResult(delArray);
                                       }
                                   });
        }

        // 선택한 Report Result Data Delete
        function deleteReportResult(delArray) {
            if (delArray.length > 0) {
                var paramArr = new Array();
                for (var i = 0 ; i < delArray.length ; i++) {
                    paramArr.push(delArray[i].get("resultId"));
                }

                $.post("${ctx}/gadget/system/deleteReportResult.do",
                       {"checkedData" : paramArr.join(",")},
                       function(json) {
                           if (json.result == "success") {
                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.information.successDelete"/>', function() {getReportResultList(); });
                           }else {
                               Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.alert.failDelete"/>', function() { });
                           }
                            
                           return;
                       }
                );
            } else {
                Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.select.row.no"/>');
            }
        }

        // Report Setting Tab 클릭 시 호출
        function getReportSettingData() {
            viewTree();
        }

        // Report Tree 데이터 조회
        function viewTree() {
            $('#basic_html3').tree({

                data : {
                    type : 'json',
                    opts : {
                        method : 'POST',
                        url : '${ctx}/gadget/system/getReportTreeData.do'                }
                },
                types   : {
                    "default" : {
                        deletable : true,
                        renameable : true

                    },

                    "category" : {

                        icon : {
                           image : '${ctx}/js/tree/themes/default/vendor.gif'

                        }
                    },

                    "report" : {
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
                    /* 'onload' : function(t) {
                        t.settings.data.opts.static = false;
                    },*/
                    /*'beforedata' : function (n, t) {
                        return {
                            supplierId : "22"
                        };
                    },*/
                    'ondata' : function(json) {
                        if (json == false) {
                            return;
                        }

                        if (json.data)
                            return json; //create()함수호출시 callback으로 ondata이 호출되어 충돌을 방지하는 코드

                        var data = [];
                        var jsonData = json.result;

                        for ( var i in jsonData) {
                            
                            if (isNaN(i)) continue;

                            if (jsonData[i].categoryItem == null || jsonData[i].categoryItem == false) {
                                data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                                    'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'report','link':jsonData[i].metaLink}});
                            } else if (jsonData[i].children == null) {
                                data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                                    'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'category','link':jsonData[i].metaLink}});
                            } else {
                                data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                                    'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'category','link':jsonData[i].metaLink}, 
                                    'children':makeTreeData(jsonData[i].children)});
                            }
                        }
                        return data;

                    },
                    'onselect' : function(n, t) {
                        $("#reportId").val("");
                        $("#metaLink").val("");
                        var nodeId = $(n).attr('id');
                        var nodeRel = $(n).attr('rel');
                        var link = $(n).attr('link');

                        if (nodeRel == "category") return;

                        $("#reportId").val(nodeId.replace("report_",""));
                        $("#metaLink").val(link);
                    }//,
                    /*'onsearch' : function(n, t) {
                        t.container.find('.searchResult').removeClass('searchResult');
                        n.addClass('searchResult');
                        $('#searchCount').text('<fmt:message key="aimir.searchResult"/>'+':'+n.length);
                    }*/
                }
             });
        }

        // Tree 데이터 생성
        function makeTreeData(jsonData) {
            var data = new Array();

            for (var i in jsonData) {
                if (isNaN(i)) continue;

                if (jsonData[i].categoryItem == null || jsonData[i].categoryItem == false) {    // item
                    data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                        'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'report','link':jsonData[i].metaLink}});
                } else if (jsonData[i].children == null) {
                    data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                        'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'category','link':jsonData[i].metaLink}});
                } else {
                    data.push({'data':{'title':jsonData[i].reportName, 'attributes':{'href':'#' }}, 
                        'attributes':{'id':'report_'+jsonData[i].reportId,'rel':'category','link':jsonData[i].metaLink}, 
                        'children':makeTreeData(jsonData[i].children)});
                }
            }
            return data;
        }

        // subtab handling        
        function changeSubTab(tabId){

            if(tabId == "batchSubTab") {
                if($('#batchSubTabDiv').is(':hidden')) {
                    $('#paramSubTabDiv').hide();
                    $('#paramSubBtnDiv').hide();
                    $('#paramSubTab').removeClass('tabon');

                    $('#batchSubTabDiv').show();
                    $('#batchSubBtnDiv').show();
                    $('#batchSubTab').addClass('tabon');
                    //updateTypeFChart();
                }
            } else {
                if($('#paramSubTabDiv').is(':hidden')) {
                    $('#batchSubTabDiv').hide();
                    $('#batchSubBtnDiv').hide();
                    $('#batchSubTab').removeClass('tabon');

                    $('#paramSubTabDiv').show();
                    $('#paramSubBtnDiv').show();
                    $('#paramSubTab').addClass('tabon');
                }
            }
        }

        // Cron Help Window 생성
        var makeCronHelpWindow = function() {
            var width = $("#cronHelpDiv").width();

            var html = $("#cronHelpContents").val();

            var cronHelpWindow = new Ext.Window({
                title: '<fmt:message key="aimir.report.mgmt.cronhelp"/>',
                id: 'cronHelp',
                applyTo:'cronHelpDiv',
                autoScroll: true,
                resizable: true,
                width: (width-10),
                height: 400,
                html: "<span style='line-height:30px'>"+html+"</span>",
                padding:15,
                closeAction:'hide'
            });
        };

        // Cron Help Window 보기
        function viewCronHelpWindow() {
            //Ext.getCmp('cronHelp').setWidth($("#cronHelpDiv").width()-100);
            Ext.getCmp('cronHelp').show();
        }

        // Email Contacts Window
        var makeExtWindow = function() {
            var width = $("#emailContactsDiv").width();

            var html = '<div id="wrap">'
                    + '<table class="report_mgmt">'
                    + '<colgroup>'
                    + '<col width="120px"/>'
                    + '<col width="145px"/>'
                    + '<col width=""/>'
                    + '</colgroup>'
                    + '<tr><th><select id="searchType" name="searchType" class="email_select_width">'
                    + '<option value="name"><fmt:message key="aimir.name"/></option>'
                    + '<option value="group"><fmt:message key="aimir.group"/></option>'
                    + '</select></th>'
                    + '<td><input type="text" id="searchValue" name="searchValue" class="email_input_width"/></td>'
                    + '<td><a href="#" onclick="getEmailContactsList();" class="btn_blue"><span><fmt:message key="aimir.button.search"/><!-- 검색 --></span></a></td>'
                    + '</tr>'
                    + '<tr><td colspan="3" class="email_dotted"></td></tr>'
                    + '<tr><th><select id="groupCombo" class="email_select_width"></select></th>'
                    + '<td><input type="text" id="groupName" class="email_input_width"/></td>'
                    + '<td><a href="#" onclick="saveContactsGroupConfirm();" class="btn_blue"><span><fmt:message key="aimir.save2"/><!-- 추가 --></span></a>'
                    + '<a href="#" onclick="deleteContactsGroupConfirm();" class="btn_blue"><span><fmt:message key="aimir.button.delete"/><!-- 삭제 --></span></a>'
                    + '</td>'
                    + '</tr>'
                    + '<tr><th><select id="insGroupCombo" class="email_select_width"></select></th>'
                    + '<td><input type="text" id="insName" style="width:40px"/>'
                    + '<input type="text" id="insEmail" style="width:95px"/></td>'
                    + '<td><a href="#" onclick="insertContactsDataConfirm();" class="btn_blue"><span><fmt:message key="aimir.add"/><!-- 추가 --></span></a></td>'
                    + '</tr>'
                    + '<tr><td colspan="3" class="email_dotted"></td></tr>'
                    + '</table>'
                    + '<div id="contactsDiv" class="margin-t10px"></div>'
                    + '</div>';

            var emailContactsWindow = new Ext.Window({
                title: '<fmt:message key="aimir.report.mgmt.emailcontacts"/>',
                id: 'emailContacts',
                //autoLoad: {
                //    url: "${ctx}/gadget/system/reportMgmtEmailContacts.do"
                //},
                applyTo:'emailContactsDiv',
                autoScroll: true,
                resizable: true,
                width: (width-10),
                height: 400,
                html: html,
                closeAction:'hide'
            });
        };

        // Email contacts Window 보이기
        function viewEmailContactsWindow() {
            $("#searchType").selectbox();
            getEmailContactsGroup();
            $(function() { $('#groupCombo').bind('change',function(event) { $('#groupName').val($('#groupCombo option:selected').text()); } ); });
            
            Ext.getCmp('emailContacts').show();
        }

        // Email Contacts Group 가져오기
        function getEmailContactsGroup() {
            $.post("${ctx}/gadget/system/getReportContactsGroupComboData.do",
                   function(json) {
                       groupData = json.result;
                       $("#groupCombo").loadSelect(groupData);
                       $("#groupCombo option:eq(0)").replaceWith('<option value=""><fmt:message key="aimir.group"/></option>');
                       $("#groupCombo").val("");
                       $("#groupCombo").selectbox();

                       $("#insGroupCombo").loadSelect(groupData);
                       $("#insGroupCombo option:eq(0)").replaceWith('<option value=""><fmt:message key="aimir.group"/></option>');
                       $("#insGroupCombo").val("");
                       $("#insGroupCombo").selectbox();

                       getEmailContactsList();
                   });
        }
        
        /* Email Contacts 리스트 START */
        var groupData;
        var contactsGridOn = false;
        var contactsGrid;
        var contactsColModel;
        var contactsCheckSelModel;
        var getEmailContactsList = function() {
            var width = $("#contactsDiv").width();
            var rowSize = 5;

            //emergePre();
            var contactsStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: rowSize}},
                url: "${ctx}/gadget/system/getReportContactsList.do",
                baseParams: {
                    searchType : $("#searchType").val(),
                    searchValue : $("#searchValue").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contactsId", "name", "email", "groupId", "groupName"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            // create reusable renderer
            Ext.util.Format.comboRenderer = function(combo){
                return function(value){
                    var record = combo.findRecord(combo.valueField, value);
                    return record ? record.get(combo.displayField) : combo.valueNotFoundText;
                };
            };

            // create the combo instance
            var combo = new Ext.form.ComboBox({
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                mode: 'local',
                store: new Ext.data.JsonStore({
                    //autoLoad: true,
                    id: 0,
                    data: groupData,
                    fields: ["id", "name"]
                }),
                valueField: "id",
                displayField: "name",
                editable: false
            });

            if(contactsGridOn == false) {
                contactsCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                    ,dataIndex: 'contactsId'
                });
            }

            contactsColModel = new Ext.grid.ColumnModel({
                columns: [
                    contactsCheckSelModel
                   ,{header: '<fmt:message key="aimir.name"/>', dataIndex: 'name', width: ((width-20)/4)*0.7,
                        editor: new Ext.form.TextField({
                            id: 'name',
                            allowBlank: false
                        })
                    }
                   ,{header: '<fmt:message key="aimir.email"/>', dataIndex: 'email', width: ((width-20)/4)*1.3,
                       editor: new Ext.form.TextField({
                           id: 'email',
                           allowBlank: false
                       })
                   }
                   ,{header: '<fmt:message key="aimir.group"/>', dataIndex: 'groupId', width: ((width-20)/4)*0.7,
                       editor: combo, renderer: Ext.util.Format.comboRenderer(combo)
                   }
                   ,{header: '<fmt:message key="aimir.update"/>/<fmt:message key="aimir.button.delete"/>', width: (((width-20)/4)*1.3)-4,
                       renderer: function(value, metaData, record, index) {
                                     var btnHtml = "<a href='#' onclick='updateContactsDataConfirm(\"{contactsId}\", \"{name}\", \"{email}\", \"{groupId}\", \"{flag}\");' "
                                          + "class='btn_blue'><span><fmt:message key="aimir.update"/></span></a>"
                                          + "<a href='#' onclick='deleteContactsDataConfirm(\"{contactsId}\");' "
                                          + "class='btn_blue'><span><fmt:message key="aimir.button.delete"/></span></a>";

                                     var tplBtn = new Ext.Template(btnHtml);
                                     return tplBtn.apply({contactsId: record.get('contactsId'), name: record.get('name'), email: record.get('email'), 
                                                        groupId: record.get('groupId'), flag: record.dirty});
                       }
                   }
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(contactsGridOn == false) {
                contactsGrid = new Ext.grid.EditorGridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: contactsStore,
                    colModel : contactsColModel,
                    sm: contactsCheckSelModel,
                    autoScroll:false,
                    width: width,
                    height: 250,
                    //stripeRows : true,
                    //columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    clicksToEdit: 1,
                    renderTo: 'contactsDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:[{
                        text: '<fmt:message key="aimir.button.apply"/>',
                        scope: this,
                        iconCls:'accept',
                        handler: function(){
                           applySelectedEmail(contactsCheckSelModel.getSelections());
                        }
                    },'-'],
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: rowSize,
                        store: contactsStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                contactsGridOn = true;
            } else {
                contactsGrid.setWidth(width);
                var bottomToolbar = contactsGrid.getBottomToolbar();
                contactsGrid.reconfigure(contactsStore, contactsColModel);
                bottomToolbar.bindStore(contactsStore);
            }
            hide();
        };

        // Email Contacts Group Save 확인창
        function saveContactsGroupConfirm() {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           saveContactsGroupValidationCheck();
                                       }
                                   });
        }

        // Email Contacts Group Save 입력값 체크
        function saveContactsGroupValidationCheck() {
            if ($("#groupName").val().length <= 0) {     // 리포트트리 선택
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.groupname"/>');
            } else {
                saveContactsGroup();
            }
        }
        
        // Email Contacts Group Save
        function saveContactsGroup() {
            var params = {"groupId" : $("#groupCombo").val(),
                    "groupName" : $("#groupName").val()};

            $.post("${ctx}/gadget/system/saveReportContactsGroup.do",
                   params,
                   function(json) {
                       if (json.result == "success") {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>', 
                           function() {
                               // 입력항목 clear
                               $("#groupName").val("");

                               getEmailContactsGroup(); 
                           });
                       }else {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                       }
                        
                       return;
                   }
            );
        }

        // Email Contacts Group Delete 확인창
        function deleteContactsGroupConfirm() {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.groupdelcfm"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           deleteContactsGroupValidationCheck();
                                       }
                                   });
        }

        // Email Contacts Group Delete 키값 체크
        function deleteContactsGroupValidationCheck() {
            if ($("#groupCombo").val().length <= 0) {     // 리포트트리 선택
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.group"/>');
            } else {
                deleteContactsGroup();
            }
        }

        // Email Contacts Group Delete
        function deleteContactsGroup() {
            var params = {"groupId" : $("#groupCombo").val()};

            $.post("${ctx}/gadget/system/deleteReportContactsGroup.do",
                 params,
                 function(json) {
                     if (json.result == "success") {
                         $("#groupName").val("");
                         Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.information.successDelete"/>', function() {getEmailContactsGroup(); });
                     }else {
                         Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.alert.failDelete"/>');
                     }
                      
                     return;
                 }
            );
        }

        // Email Contacts Data Insert 확인창
        function insertContactsDataConfirm() {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           insertContactsDataValidationCheck();
                                       }
                                   });
        }

        // Email Contacts Data Insert 입력값 체크
        function insertContactsDataValidationCheck() {
            if ($("#insGroupCombo").val().length <= 0) {     // 그룹 선택
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.group"/>');
            } else if ($("#insName").val().length <= 0) {     // 이름 입력
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.addresseename"/>');
            } else if ($("#insEmail").val().length <= 0) {     // 이메일 입력
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.email"/>');
            } else {
                insertContactsData();
            }
        }
        
        // Email Contacts Data insert
        function insertContactsData() {
            var params = {"name" : $("#insName").val(),
                          "email" : $("#insEmail").val(),
                          "groupId" : $("#insGroupCombo").val()};

            $.post("${ctx}/gadget/system/insertReportContactsData.do",
                   params,
                   function(json) {
                       if (json.result == "success") {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>', 
                           function() {
                               // 입력항목 clear
                               $("#insName").val("");
                               $("#insEmail").val("");
                               $("#insGroupCombo").option("");

                               getEmailContactsList(); 
                           });
                       }else {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                       }
                        
                       return;
                   }
            );
        }

        // Email Contacts Data Update 확인창
        function updateContactsDataConfirm(contactsId, name, email, groupId, flag) {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           updateContactsData(contactsId, name, email, groupId, flag);
                                       }
                                   });
        }

        // Email Contacts Data Update
        function updateContactsData(contactsId, name, email, groupId, flag) {
            if (flag == "false") {
                Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.nochangedata"/>');
                return;
            }
            var params = {"contactsId" : contactsId,
                          "name" : name,
                          "email" : email,
                          "groupId" : groupId};

            $.post("${ctx}/gadget/system/updateReportContactsData.do",
                   params,
                   function(json) {
                       if (json.result == "success") {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>', function() {getEmailContactsList(); });
                       }else {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                       }
                        
                       return;
                   }
            );
        }

        // Email Contacts Data Delete 확인창
        function deleteContactsDataConfirm(contactsId) {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           deleteContactsData(contactsId);
                                       }
                                   });
        }

        // Email Contacts Data Delete
        function deleteContactsData(contactsId) {
            var params = {"contactsId" : contactsId};

            $.post("${ctx}/gadget/system/deleteReportContactsData.do",
                 params,
                 function(json) {
                     if (json.result == "success") {
                         Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.information.successDelete"/>', function() {getEmailContactsList(); });
                     }else {
                         Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.hems.alert.failDelete"/>');
                     }
                      
                     return;
                 }
            );
        }

        // 선택한 Email 주소를 form 에 입력
        function applySelectedEmail(selArray) {
            if (selArray.length > 0) {
                var paramArr = new Array();
                for (var i = 0 ; i < selArray.length ; i++) {
                    paramArr.push(selArray[i].get("email"));
                }

                $("#email").val(paramArr.join(","));
                Ext.getCmp('emailContacts').hide();
            } else {
                Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.select.row.no"/>');
            }
        }

        // Report Schedule 저장확인창
        function saveReportScheduleConfirm() {
            Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.wouldSave"/>'
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           scheduleValidationCheck();
                                       }
                                   });
        }

        // Report Schedule 입력값 체크
        function scheduleValidationCheck() {
            if ($("#reportId").val().length <= 0) {     // 리포트트리 선택
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.report"/>', function() {});
            } else if ($("#scheduleName").val().length <= 0) {     // 스케줄명
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.schedulename"/>', function() {$("#scheduleName").focus();});
            } else if ($("#cronFormat").val().length <= 0) {        // Cron 형식
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.cron"/>', function() {$("#cronFormat").focus();});
            } else if ($("#exportFormat").val().length <= 0) {      // Export Format
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.exportformat"/>', function() {$("#exportFormat").focus();});
            } else if ($(":radio[name='useEmailYn']:checked").val().length <= 0) {      // 이메일 통보여부
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.emailyn"/>', function() {$("#useEmailYn").focus();});
            } else if ($(":radio[name='useEmailYn']:checked").val() == "true" && $("#email").val().length <= 0) {         // 이메일 수신자
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.email"/>', function() {$("#email").focus();});
            } else if ($("#locationId").val().length <= 0) {        // 지역
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.location"/>', function() {});
            } else {
                saveReportSchedule();
            }
        }

        // Report Schedule 저장
        function saveReportSchedule() {
            var params = {"reportId" : $("#reportId").val(),
                          "scheduleName" : $("#scheduleName").val(),
                          "cronFormat" : $("#cronFormat").val(),
                          "exportFormat" : $("#exportFormat").val(),
                          "useEmailYn" : $(":radio[name='useEmailYn']:checked").val(),
                          "email" : $("#email").val(),
                          "startDate" : $("#searchStartDate").val(),
                          "endDate" : $("#searchEndDate").val(),
                          "locationId" : $("#locationId").val(),
                          "meterType" : $("#meterTypeCode").val()};

            $.post("${ctx}/gadget/system/insertReportSchedule.do",
                   params,
                   function(json) {
                       if (json.result == "success") {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save"/>');
                       }else {
                           Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.save.error"/>');
                       }

                       return;
                   }
            );
        }

        // Report Popup Direct 호출
        var winReportByDirect;
        function openReportByDirect() {
            var report;
            var params = "";

            if ($("#reportId").val().length <= 0) {     // 선택된 리포트가 없음
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.report"/>');
            } else if ($("#metaLink").val().length <= 0) {    // 리포트 파일이 없음
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.noreportlink"/>');
            } else if ($("#locationId").val().length <= 0) {        // 지역
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.location"/>');
            } else {
                var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
                report = reportFileDir + $("#metaLink").val();

                /**** REAL DATA START ****
                params += "&locationId=" + $("#locationId").val;
                params += "&startDate=" + $("#searchStartDate").val;
                params += "&endDate=" + $("#searchEndDate").val;
                paams += "&meterType=" + $("#meterTypeCode").val;
                **** REAL DATA END ****/

                /**** TEST DATA SETTING START ****/
                //report = reportFileDir + "energySavingTemp.rptdesign";
                params = "&supplierId=11&searchYear=2010&temp=가스 요금";
                /**** TEST DATA SETTING END ****/
                if(winReportByDirect)
                    winReportByDirect.close();
                var localport = "<%= request.getLocalPort() %>";
                var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
                winReportByDirect = window.open(birtURL, "ReportByDirectExcel", opts);
            }
        }

        // Report Popup 저장된 Data로 호출
        var winReportByData;
        function openReportByData(metaLink, reportData) {
            var report;
            var params = "";

            if (metaLink.length <= 0) {    // 리포트 파일이 없음
                Ext.Msg.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.report.mgmt.msg.validation.noreportlink"/>');
            } else {
                var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
                report = reportFileDir + metaLink;

                /**** REAL DATA START ****
                params += "&locationId=0&startDate=20110101&endDate=20110101&meterType=dummy";  // 사용하지 않지만 필수입력값이므로 dummy 값 setting
                params += "&link=" + reportData;
                **** REAL DATA START ****/

                /**** TEST DATA START ****/
                params = "&supplierId=11&searchYear=2010&temp=가스 요금&link=" + reportData;
                /**** TEST DATA END ****/
                if(winReportByData)
                    winReportByData.close();
                var localport = "<%= request.getLocalPort() %>";
                var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
                winReportByData = window.open(birtURL, "ReportByDataExcel", opts);
            }
        }

        // Export 된 File 을 download 한다.
        function exportReport(resultFileLink) {
            $("#filePath").val(reportExportDir);
            $("#fileName").val(resultFileLink) ;

            var url = "${ctx}/common/fileDownload.do";
            //var downform = document.getElementsByName("fileDownloadForm")[0];
            var downform = $("#fileDownloadForm")[0];

            downform.action = url;
            downform.submit();
        }
        
    /*]]>*/
    </script>

</head>

<body>
<input type="hidden" id="reportId" name="reportId"/>
<input type="hidden" id="metaLink" name="metaLink"/>
<input type="hidden" id="cronHelpContents" name="cronHelpContents" value='<fmt:message key="aimir.report.mgmt.cronhelp.contents"/>'/>

    <!-- tab -->
    <div id="tabs">
        <ul>
            <li><a href="#resultDiv" id="resultTab"><fmt:message key="aimir.report.mgmt.scheduledreports"/><!-- Scheduled Reports --></a></li>
            <li><a href="#settingDiv" id="settingTab"><fmt:message key="aimir.report.mgmt.reportsetting"/><!-- Report Setting --></a></li>
        </ul>

        <!-- tabs-1 -->
        <div id="resultDiv">
            <div class="scheduled_report">
                <span class="schedule_tit"><fmt:message key="aimir.report.mgmt.title"/><!-- 제목 --> : &nbsp;</span>
                <span><input type="text" id="reportName" style="width:240px;" /></span>
            </div>
            <div class="dashedline2"></div>

            <div class="height_30">
                <%@ include file="/gadget/commonDateTabButtonType4.jsp"%>
            </div>

            <div id="reportResultDiv" class="margin-t10px clear"></div>
        </div>
        <!--// tabs-1 -->

        <!-- tabs-2 -->
        <div id="settingDiv">
            <div>
                <fieldset id="reportTree" class="tree_report">
                <div id="basic_html3"></div>
                </fieldset>
            </div>

            <div class="sub_tabs">
                <ul>
                    <li><a href="#" onclick="changeSubTab(this.id);" id="paramSubTab" class="tabon"><fmt:message key="aimir.report.mgmt.reportparam"/><!-- Report Parameter --></a></li>
                    <li><a href="#" onclick="changeSubTab(this.id);" id="batchSubTab"><fmt:message key="aimir.report.mgmt.batchreport"/><!-- Batch Report --></a></li>
                </ul>
            </div>
            <div class="sub_bluebox">
                <!-- subtabs-1 -->
                <div id="paramSubTabDiv">
                    <div  id="typeSearch" class="date_module">
                        <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
                    </div>

                    <div class="report_parameter">
                        <div class="dashedline2 margin-b5px"></div>

                        <table  class="search">
                            <tr>
                                <td>
                                    <input type="text" id="searchWord" name="searchWord" style="width:140px" value="<fmt:message key='aimir.location'/>" />
                                    <input type="hidden" id="locationId" value="" />
                                </td>
                                <td><select id="meterTypeCode" name="meterTypeCode" style="width:120px;"><option value=""></option></select></td>
                            </tr>
                        </table>
                        <div id="treeDivAOuter" class="tree-billing auto" style="display:none">
                            <div id="treeDivA"></div>
                        </div>
                    </div>
                </div>
                <!--// subtabs-1 -->
                <!-- subtabs-2 -->
                <div id="batchSubTabDiv" class="batch_report" style="display:none;">
                    <div>
                        <table class="report_mgmt">
                            <colgroup>
                                <col width="130px"/>
                                <col width=""/>
                            </colgroup>
                            <tr><th><fmt:message key="aimir.scheduleName"/><!-- 스캐줄명 --></th>
                                <td><input type="text" id="scheduleName" /></td>
                            </tr>
                            <tr><th><fmt:message key="aimir.cronExpression"/></th>
                                <td><input type="text" id="cronFormat" /><a href="#" onclick="viewCronHelpWindow();" class="btn_blue" >
                                    <span><fmt:message key="aimir.report.mgmt.cronhelp"/><!-- Cron 도움말 --></span></a>
                                </td>
                            </tr>
                            <tr><th><fmt:message key="aimir.report.mgmt.exportformat"/><!-- Export Format --></th>
                                <td><select id="exportFormat" name="exportFormat" style="width:120px;"></select></td>
                            </tr>
                            <tr><th><fmt:message key="aimir.report.mgmt.emailyn"/><!-- 이메일 통보여부 --></th>
                                <td>
                                    <span><input type="radio" id="useEmailYn" name="useEmailYn" value="true" class="radio_space" checked="checked"/></span>
                                    <span class="margin-t3px margin-r10"><fmt:message key="aimir.yes"/><!-- Yes --></span>
                                    <span><input type="radio" id="useEmailYn" name="useEmailYn" value="false" class="radio_space" /></span>
                                    <span class="margin-t3px"><fmt:message key="aimir.no"/><!-- No --></span>
                                </td>
                            </tr>
                            <tr><td><fmt:message key="aimir.report.mgmt.addressee"/><!-- 수신자 --></td>
                                <td rowspan="2"><textarea id="email" name="email"></textarea></td>
                            </tr>
                            <tr><td><a id="addresseeBtn" href="#" onclick="viewEmailContactsWindow();" class="btn_bluebox">
                                    <span><fmt:message key="aimir.report.mgmt.setaddressee"/><!-- 수신자 설정 --></span></a></td>
                            </tr>
                        </table>
                    </div>


                </div>
                <!--// subtabs-2 -->
            </div>

            <!--btn : subtabs-1 -->
            <div id="paramSubBtnDiv" class="report_btn_right">
               <a href="#" onclick="openReportByDirect();" class="btn_blue"><span><fmt:message key="aimir.report.mgmt.onlinereport"/><!-- Online Report --></span></a>
            </div>
            <!--//btn : subtabs-1 -->

            <div id="saveBtn">
                <!--btn : subtabs-2 -->
                <div id="batchSubBtnDiv" class="report_btn_right" style="display:none">
                   <a href="#" onclick="saveReportScheduleConfirm();" class="btn_blue"><span><fmt:message key="aimir.save2"/><!-- 저장 --></span></a>
                </div>
                <!--//btn : subtabs-2 -->
            </div>

        </div>
        <!--// tabs-2 -->
    </div>
    <!--// tab -->
    <div id="cronHelpDiv"></div>
    <div id="emailContactsDiv"></div>
    <!-- <form name="fileDownloadForm" id="fileDownloadForm" method="post" target="downFrame" style="display:none;"> -->
    <form name="fileDownloadForm" id="fileDownloadForm" method="post" target="_self" style="display:none;">
        <input type="hidden" id="filePath" name="filePath" />
        <input type="hidden" id="fileName" name="fileName" />
        <input type="hidden" id="realFileName" name="realFileName" />
    </form>
    <!-- <iframe name="downFrame" style="display:none;"></iframe> -->
</body>

</html>