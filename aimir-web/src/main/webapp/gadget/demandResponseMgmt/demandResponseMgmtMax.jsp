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

    $(document).ready(function(){

        Ext.QuickTips.init();

        if("${isNotService}" == "true") {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();
            hide();
            return;
        } else { // 해당 가젯에 대한 권한이 있을때
            $("#isNotService").hide();
        }

        $("#contractNumber").selectbox();
        $("#demandResponseControlTabId").click(function() { displayDivTab("demandResponseControlTab"); });        
        $("#demandResponseHistoryTabId").click(function() { displayDivTab("demandResponseHistoryTab"); });

        getContract();

        displayDivTab("demandResponseControlTab");

    });
    
    var divTabArray = ["demandResponseControlTab", "demandResponseHistoryTab"];
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

    var changeContract = function() {

        getContract();

        searchTab();
    };

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
                getDeviceSpecificDivData();
                break;

            case 1 :
                getPeriodDivData();
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
                getDeviceSpecificDivData();
                break;
            case 1 :
                getPeriodDivData();
                break;

            default :
                break;
        }
    });

    var getContract = function() {
        $.ajaxSetup({ async: false });
        var params = {
                "contractId" : $("#contractNumber").val()
        };

        $.getJSON("${ctx}/gadget/homeDeviceMgmt/getContract.do",
                params,
                function(result) {

                    //$("#locationTd").text(result.location);
                    //$("#tariffTd").text(result.tariffType);
                    //$("#statusTd").text(result.status);
                    //$("#dateTd").text(result.date);

                    supplierId = result.contract.supplier;
                    homeGroupId = result.homeGroupId;
                }
            );
        $.ajaxSetup({ async: true });
    };

    var mappingGridOn = false;
    var mappingGrid;

    var getDeviceSpecificDivData = function() {
       var width = $("#homeDeviceMapping").width();

       var mappingStore = new Ext.data.GroupingStore({
           autoLoad: true,
           url: "${ctx}/gadget/demandResponseMgmt/getHomeDeviceMappingInfo.do?homeGroupId=" + homeGroupId,
           reader: new Ext.data.JsonReader({
               root:'result',
               fields: ["HOMEDEVICEIMGFILENAME", "FRIENDLYNAME", "HOMEDEVICEGROUPNAME", "SERIALNUMBER", "MODEMID", 
                        "MAPPINGCATEGORYID", "MAPPINGID", "MAPPINGFRIENDLYNAME", "MAPPINGIMGURL", "MAPPINGDRLEVEL", 
                        "MAPPINGDRNAME", "MAPPINGDRPROGRAMMANDATORY", "MAPPINGINSTALLSTATUSCODE"] 
           }),
         //  sortInfo:{field: 'company', direction: "ASC"},
           groupField:'HOMEDEVICEGROUPNAME'
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
          // listClass: 'x-combo-list-small',
           store: new Ext.data.ArrayStore({
               id: 0,
               fields: [ 'value', 'text' ],
               data: [[true, 'Allow'], [false, 'Reject']]
           }),
           valueField: 'value',
           displayField: 'text',
           editable: false,
           listeners:{
               select:function( combo, rec, index){
                       record = mappingGrid.getSelectionModel().getSelected();
                       var params = {
                               "id" : record.get('MAPPINGID'),                    
                               "drProgramMandatory" : combo.getValue()
                       };
                       $.getJSON("${ctx}/gadget/demandResponseMgmt/updateDrProgramMandatory.do",
                               params,
                               function(result) {
                                   if (result.status == true) {
                                       Ext.MessageBox.alert('Home Device Registration', '변경에 성공했습니다..', function() {searchTab(0); });
                                   } else {
                                       Ext.MessageBox.alert('Status', '변경에 실패했습니다.', function() { ;});
                                   }
                               }
                       ); 
               }                    
           }
       });
       
       var mappingColModel = new Ext.grid.ColumnModel({
           columns: [
               {header: "smart plug", dataIndex: 'HOMEDEVICEIMGFILENAME',
                   renderer:  function(value) {
                               var tpl = new Ext.Template("<img src='../../{HOMEDEVICEIMGFILENAME}' width='50px' height='50px'></img>");
                               return tpl.apply({HOMEDEVICEIMGFILENAME: value});
                   }
                },
               {header: "friendly name", dataIndex: 'FRIENDLYNAME', renderer:addTooltip},
               {header: "DR Allow", dataIndex: 'MAPPINGDRPROGRAMMANDATORY', editor:combo,   // MAPPINGIMGURL
                   renderer: Ext.util.Format.comboRenderer(combo)
               }, 
               {header: "Group", hidden: true, dataIndex: 'HOMEDEVICEGROUPNAME'},
               {header: "DR Control", 
                   xtype: 'actioncolumn',
                   items: [{
                       getClass: function(v, meta, rec) {          // Or return a class from a function
                       if (rec.get('MAPPINGDRLEVEL') == "1") {
                          // this.items[0].tooltip = 'Change to On';
                           this.items[0].tooltip = 'Demand Response는 "Y/N"만 입력 가능 합니다.';
                           return 'icon-dr-off';
                       } else {
                           this.items[0].tooltip = 'Change to Off';
                           return 'icon-dr-on';
                       }
                       },
                       handler: function(grid, rowIndex, colIndex) {
                           var rec = grid.getStore().getAt(rowIndex);
                           runDemandResponse(rec);
                       }
                   }]
               }
           ],
           defaults: {
               sortable: false,
               menuDisabled: false,
               width: width / 4
           }
      });

      if (mappingGridOn == false) {
                mappingGrid = new Ext.grid.EditorGridPanel({
                id:'gridpanel',
                store: mappingStore,
                colModel : mappingColModel,
                sm : new Ext.grid.RowSelectionModel({singleSelect: true}),
                view: new Ext.grid.GroupingView({
                    deferEmptyText: false,
                    emptyText: 'No records. You need to register your home devices',
                   // forceFit:true,
                    groupTextTpl: '{group} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
                width: width,
                height: 500,
                loadMask:true,
                renderTo: 'homeDeviceMapping',
                listeners:{
                    cellclick:function( mappingGrid, rowIndex, columnIndex, e){
                        var record = mappingGrid.getStore().getAt(rowIndex); // rowIndex의get record
                        var fieldName = mappingGrid.getColumnModel().getColumnId(columnIndex); // columnIndex
                        var data = record.get(fieldName);
                    }                    
                },
                viewConfig: {
                    emptyText: ' No records. You need to register your home devices'
                }
                ,tbar: [{
                    text: 'All On',
                    tooltip: 'Change to All On',
                    iconCls: 'icon-all-on',
                    handler: function(){
                       groupRunDr('15'); // Relay Off
                    }
                },'-',{
                   // ref: '../removeBtn',
                    text: 'All Off',
                    tooltip: 'Change to All Off',
                    iconCls: 'icon-all-off',
                   // disabled: true,
                    handler: function(){
                      groupRunDr('1'); // Relay On
                    }
                },'-',{
                    xtype: 'checkbox',
                    boxLabel: '<span class="WhiteBoxLabel">All DR Allow</span>',
                    itemCls: 'chb',
                    checked: false,
                    handler: function(){
                      drAllAllow(this.getValue());
                    } 
                }]
            });
         
            mappingGrid.getView().scrollOffset = 2;
            mappingGridOn = true;
      } else { 
    	  mappingGrid.setWidth(width);
          mappingGrid.reconfigure(mappingStore, mappingColModel);
      }

 //     $.ajaxSetup({ async: true });
 //     hide();
    };

    var addTooltip = function(val, cell, record) {
        return '<div qtip="'+ val +'">'+ val +'</div>';     
    };

    var runDrLevel;
    var mappingId;
    var mappingCategoryId;
    var groupDrLevel;

    var runDemandResponse = function(rec) {
   
        if (rec.get('MAPPINGDRLEVEL') == 1) {
            runDrLevel = 15; // Relay Off
        }else runDrLevel = 1; // Relay On

        Ext.MessageBox.prompt("Name"   ,"Please enter your password:"
                , function(btn, text) {
                $.ajaxSetup({ cache: false });
                // run DR
                  if( btn == "ok" && text.length != 0){
                      var params = {
                              "id" : rec.get('MAPPINGID'),                    
                              "drLevel" : runDrLevel,
                              "contractId" : $("#contractNumber").val(),
                              "serialNumber" : rec.get('SERIALNUMBER'),
                              "password" : text
                      };
                      $.getJSON("${ctx}/gadget/demandResponseMgmt/runDemandResponse.do",
                              params,
                              function(result) {
                                  if (result.status == 'SUCCESS') {
                                      Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.info.drMgmt" />', function() {searchTab(0); });
                                  } else if (result.status == "INVALID_PARAMETER") {
                                      Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.errorPassword.drMgmt" />', function() {/*self.location.reload();*/ });
                                  } else {
                                      Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.error.drMgmt" />', function() { });
                                  }
                              }
                      );
                  }else if( btn == "ok" && text.length == 0){
                      Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.alert.inputPassword" />', function() {});
                  }
                  $.ajaxSetup({ cache: true });
           }
        );
    };

    var groupRunDr = function(groupDrLevel) {
        Ext.MessageBox.prompt("Name", "Please enter your password:"
                , function(btn, text){
            $.ajaxSetup({ cache: false });   
            // run DR
            if( btn == "ok" && text.length != 0){
                var params = {
                        "homeGroupId" : homeGroupId,
                        "groupDrLevel" : groupDrLevel,
                        "contractId" : $("#contractNumber").val(),
                        "password" : text
                };
                $.getJSON("${ctx}/gadget/demandResponseMgmt/runGroupDemandResponse.do",
                        params,
                        function(result) {
                            if (result.status == "SUCCESS") {
                                Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.info.drMgmt" />', function() {searchTab(0);});
                            } else if (result.status == "INVALID_PARAMETER") {
                                Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.errorPassword.drMgmt" />', function() {/*self.location.reload();*/ });
                            }  else {
                                Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.error.drMgmt" />', function() { });
                            }
                        }
                ); 
            }else if( btn == "ok" && text.length == 0){
                Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.alert.inputPassword" />', function() {});
            }
            $.ajaxSetup({ cache: true });
        });
    };

    // 모든 제품 DR허가/거부 체크박스 선택시, 이벤트 핸들러
    var drAllAllow = function(value) {
        $.ajaxSetup({ cache: false });
          var params = {
                  "homeGroupId" : homeGroupId,
                  "checkValue" : value
          };
          $.getJSON("${ctx}/gadget/demandResponseMgmt/drAllAllow.do",
                  params,
                  function(result) {
                      if (result.status == "SUCCESS") {
                          Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.info.drMgmt" />', function() {searchTab(0); });
                      }else {
                          Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.error.drMgmt" />', function() { });
                      }
                  }
          );
          $.ajaxSetup({ cache: true });         
    };
     
    /* DR History TAB JavaScript START */
    var drHistoryGridOn = false;
    var drHistoryGrid;
    var drHistoryColModel;
    var periodVal = 1; // 디폴트 주기 일주일
    var getPeriodDivData = function() {

//        emergePre();
//        $.ajaxSetup({ async: false });

       var width = $("#drHistory").width();

       var drHistoryStore = new Ext.data.JsonStore({
           autoLoad: {params:{start: 0, limit: 15}},
           url: "${ctx}/gadget/demandResponseMgmt/getDemandResponseHistoryMax.do?contractId=" + $("#contractNumber").val() + "&period="+periodVal,
           totalProperty: 'totalCount', 
           root:'result',
           fields: ["ID", "ERRORREASON", "TARGETNAME", "RUNDATE", "STATUSNAME", "DESCRIPTION", "DRNAME", "FRIENDLYNAME", "OPERATIONNAME",
                    "NOTIFICATIONTIME", "ENDTIME", "STATUS", "DIV"],
           listeners: {
               beforeload: function(store, options){
               options.params || (options.params = {});
               Ext.apply(options.params, {
                             page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
               } 
           }
       });
 
      if(drHistoryGridOn == false) {
          var comboBox = new Ext.form.ComboBox({
              typeAhead: true,
              triggerAction: 'all',
              lazyRender:true,
              mode: 'local',
              store: new Ext.data.ArrayStore({
                  id: 0,
                  fields: [ 'value', 'text' ],
                  data: [
                         [1, '일주일']
                        ,[2, '한달']
                        ,[3, '3개월']
                        ,[4, '6개월']
                        ,[5, '1년']                      
                        ]
              }),
              valueField: 'value',
              displayField: 'text',
              editable: false,
              forceSelection: true,
              value: '1',
              listeners:{
                  select:function( combo, rec, index){
        	              periodVal = combo.getValue();
        	              getPeriodDivData();
			              /*var params = {  
					              "contractNumber" :  $("#contractNumber option:selected").text()              
			                     ,"period" : combo.getValue()
			              };

                          $.getJSON("${ctx}/gadget/demandResponseMgmt/getDemandResponseHistoryMax.do",
                                  params,
                                  function(result) { }
                          ); */
                  }                    
              }
          });
          var checkSelModel = new Ext.grid.CheckboxSelectionModel({
              checkOnly:true
              ,dataIndex: 'ID'
              ,listeners: {
                  // On selection change, set enabled state of the removeButton
                  // which was placed into the GridPanel using the ref config
                  selectionchange: function(sm) {
                      if (sm.getCount()) {                         
                          drHistoryGrid.removeButton.enable();
                      } else {
                          drHistoryGrid.removeButton.disable();
                      }
                  }
              }
          });

           drHistoryColModel = new Ext.grid.ColumnModel({
              columns: [
                  // checkSelModel
                  {header: "Name", dataIndex: 'OPERATIONNAME', renderer:addTooltip}  
                  ,{header: "Status", dataIndex: 'STATUS' ,
                      renderer:  function(value, metaData, record, index) {

                          if(record.get('DIV') == 'DR') { // DR일경우는 status에 따라서 참여,거부 버튼을 생성한다.
                              if(value ==1) {
                                  var tplBtn = new Ext.Template("<span class='hm_button'><a href='javascript:setDREventOptOutStatus('3', {name});'>Allow</a>"
                                         + "<a href='javascript:setDREventOptOutStatus('4', {name});'>Reject</a></span>");
                                  return tplBtn.apply({name: record.get('ID')});
                              }else if(value == 2) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'OnGoing'});
                              }else if(value == 3) {
                                  var tplBtn = new Ext.Template("<span class='hm_button'><a href='javascript:setDREventOptOutStatus(\"4\", \"{name}\");'>Reject</a></span>");
                                  return tplBtn.apply({name: record.get('ID')});
                             }else if(value == 4) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'Rejected'});
                             }else if(value == 5) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'Completed'});
                             }
                              return;
                          } else { // Direct Load Control일 경우

                              if(value == 0 ) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'SUCCESS'});
                              } else if(value == 1) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'FAIL'});
                              } else if(value == 2) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'INVALID_PARAMETER'});
                              } else if(value == 3) {
                                  return (new Ext.Template("<div >{name}</div>")).apply({name: 'COMMUNICATION_FAIL'});
                              }
                          }
                      }
                  }

                  ,{header: "Notification Time", dataIndex: 'NOTIFICATIONTIME', renderer:addTooltip}
                  ,{header: "Start Time", dataIndex: 'RUNDATE', renderer:addTooltip}
                  ,{header: "End Time", dataIndex: 'ENDTIME', renderer:addTooltip}
                  ,{header: "Mode Value", dataIndex: 'DRNAME', renderer:addTooltip}
                  ,{header: "Device", dataIndex: 'FRIENDLYNAME', renderer:addTooltip}
                  ,{header: "Target Name", dataIndex: 'TARGETNAME', renderer:addTooltip}               
              ],
              defaults: {
                  sortable: false
                  ,menuDisabled: false
                  ,width: 150
              }
         });

          drHistoryGrid = new Ext.grid.GridPanel({
                //title: '최근 한달 Demand Response History',
                store: drHistoryStore,
                colModel : drHistoryColModel,
                sm: checkSelModel,
                autoScroll:false,
                width: width,
                height: 500,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'drHistory',
                viewConfig: {
                   // forceFit:true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: 15,
                    store: drHistoryStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                }),
                tbar:['주기:'
                ,' ', comboBox
               // ,'-',{
               //     text:'Remove Something',
               //     tooltip:'Remove the selected item',
               //     iconCls:'remove',
               //     // Place a reference in the GridPanel
               //     ref: '../removeButton',
               //     disabled: true,
               //     handler: function(){
               //        deleteDrHistory(checkSelModel.getSelections());
               //     }
                //}
                ]
            });
        //  drHistoryGrid.getView().scrollOffset = 2;
          drHistoryGridOn = true;
      } else {
    	  drHistoryGrid.setWidth(width);
          var bottomToolbar = drHistoryGrid.getBottomToolbar();
          drHistoryGrid.reconfigure(drHistoryStore, drHistoryColModel);
          //bottomToolbar.unbind(bottomToolbar.store);
          bottomToolbar.bindStore(drHistoryStore);
      }

//      $.ajaxSetup({ async: false });
//      hide();
    };

    function setDREventOptOutStatus(satus, eventIdentifier) {
        var params = {
                "optOutStatus" : satus ,
                "eventIdentifier" : eventIdentifier
        };

        $.getJSON("${ctx}/gadget/demandResponseMgmt/setDREventOptOutStatus.do",
                params,
                function(result) {

                    //$("#locationTd").text(result.location);
                    //$("#tariffTd").text(result.tariffType);
                    //$("#statusTd").text(result.status);
                    //$("#dateTd").text(result.date);

                    supplierId = result.contract.supplier;
                    homeGroupId = result.homeGroupId;
                }
            );
    }

    var deleteDrHistory = function(strArray) {
        var recordsDR = new Array();
        var recordsLC = new Array();
        for(i=0; i< strArray.length; i++){
            if(strArray[i].get("DIV") == 'DR') {
                recordsDR.push(strArray[i].get("ID"));
            } else {
                recordsLC.push(strArray[i].get("ID"));
            }        
        }

        var params = {
                "recordsDR" : recordsDR
                ,"recordsLC" : recordsLC
        };

        $.post("${ctx}/gadget/demandResponseMgmt/deleteDrHistory.do",
                params,
                function(result) {
                     if (result) {
                         Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.info.drMgmt" />', function() {searchTab(1); });
                     }else {
                         Ext.MessageBox.alert('DR Control', '<fmt:message key="aimir.hems.error.drMgmt" />', function() { });
                     }
                    
                    return;
                },
                "json"
            );
       
    };
    /* Device Registration JavaScript END */
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
						<select name="contractNumber" id="contractNumber" style="width:500px" onchange="javascript:changeContract();" >
		                    <c:forEach var="contract" items="${contracts}">
		                        <option value="${contract.id}">${contract.keyNum}</option>
		                    </c:forEach>
	                	</select>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="top_line"></div>

		<!-- tab -->
		<div class="hems_tab">
		    <ul>
		        <li><a id="demandResponseControlTabId">DR Control</a></li>
		        <li><a id="demandResponseHistoryTabId">DR History</a></li>
		    </ul>
		</div>
		<!--// tab -->
    
     </div>
    <!--//contract no.-->
    
    <!-- search : 기간 별/기기별만 보임 -->
    
    <!-- tab 1: DR Control -->
    <div id="demandResponseControlTab" class="today">
        <div>
            <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.locationUsage.usage'/></div>
        </div>
        <div  id="homeDeviceMapping"></div>
    </div>
    <!--// tab 1: DR Control -->
    
    <!-- tab 2: DR History -->
    <div id="demandResponseHistoryTab" class="today">
      <div class="title_basic"><span class="icon_title_blue"></span>DR History</div>
      <div  id="drHistory"></div>
    </div>
    <!--// tab 2: DR History -->
    

</div>
</body>
</html>