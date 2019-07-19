<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <meta http-equiv="PRAGMA" content="NO-CACHE">
 <meta http-equiv="Expires" content="-1">
 <title></title>
 <style type="text/css">
     html{overflow:auto !important}
     /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
 	.x-panel-bbar table {border-collapse: collapse; width:auto;}
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
        font-weight: bold !important;
    }  
    form {
      margin-top: 10px;
      margin-bottom: 10px;    
    }
    form div {
      margin-bottom: 10px;
    }      
    form img.ui-datepicker-trigger {
      vertical-align: middle;
    }
    .searchSet{
      display: inline; 
      float: none;
    }
    .alertSet{
      float: right;
      width: 440px;
      margin: 20px 0px 0px 0px;
      padding: 0px;
      overflow:hidden;
    }
    form input.alt {
      width: 70px;
    } 
    form span{
      margin-right: 20px;
    }

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }    
    /* ext-js grid 행 높이 고정 
     cancel이 버튼인 row와 텍스트인 경우 row의 높이가 다르므로 임의로 수정 
    */    

    .hidden {
      display: none;
    }
    .no-width {
      width: 0px;
      visibility: hidden;
    }
    .vertical-top {
      vertical-align: top;
    }
    span.bold-font {
      font-weight: bold;
    }
    button.download {
      cursor: pointer;
    }

    /* selectbox wrapper 관련 margin 제거*/
    div.selectbox-wrapper {
      margin: 0px;
    }
    input.selectbox {
      display: block;
    }
    .inline-block {
      display: inline-block;
    }
    </style>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/util/commonUtil.js"></script>
    <%-- <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script> --%>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var editAuth = "${editAuth}";
        var operatorId = "${operatorId}";
        var supplierId = "${supplierId}";
        var allStr = "<fmt:message key="aimir.all"/>";
        var selectedMeterId = "";
        var selectedMcuId = "";
        var loginId = "${loginId}";
        var isAllLoading = false;
        var PAGE_SIZE = 10;        

        //$(document).ready(function() {
        Ext.onReady(function() {
            if (editAuth == "true") {
                $("#ecBtnList").show();
                $("#notifyBtnList").show();
            } else {
                $("#ecBtnList").hide();
                $("#notifyBtnList").hide();
            }
            
            $('#relayControlButton').hide();

            Ext.QuickTips.init();
            hide();
            //locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
            setSelectBox();
        	$.ajaxSetup({
    	        async: false
    	    });
            searchDivInit();
            initChargeHistoryData();
            isAllLoading = true;

            // 선불고객 grid 조회
            getPrepayContractDivData();
        	$.ajaxSetup({
    	        async: true
    	    });
        	
        	drawContractInfoData();
        	drawBalanceHistoryData();
        	initCalendar();
        	
        	$("#infoForm span#contractInfoSearch").click(eventHandler.contractInfoSearch);
        	$("#infoDetailForm span#infoDetailSearch").click(eventHandler.infoDetailSearch);
            $("#infoDetailForm span#infoDetailTotalExcel").click(eventHandler.infoDetailTotalExcel);
            $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            
        	$("#menu").tabs();

        });
        
        var calendarProp = {
        	      showOn: 'button',
        	      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
        	      buttonImageOnly: true,
        	      dateFormat: 'yymmdd',
        	      altFormat: ''
        	    };
        
        var initCalendar = function() {
            var startDate = new Date();
            startDate.getDate(startDate.getDate() - 1);
            var endDate = new Date();
            var startProp = $.extend(true, calendarProp);
            var endProp = $.extend(true, calendarProp);

            $('#infoDetailForm input[name=startDate]').datepicker(startProp);
            $('#infoDetailForm input[name=endDate]').datepicker(endProp);
            
            $('#infoDetailForm input[name=startDate]').datepicker('setDate', startDate);
            $('#infoDetailForm input[name=endDate]').datepicker('setDate', endDate);

            var initDateFormat = function(inst ,date) {
              var dbDate = $.datepicker.formatDate('yymmdd', date);
              $.getJSON("${ctx}/common/convertLocalDate.do", 
                {supplierId: supplierId, dbDate: dbDate},
                function(data) {            
                  $(inst).siblings("." + inst.attr('name')).val(data.localDate);
                });
            };

            initDateFormat($('#infoDetailForm input[name=startDate]'), startDate);
            initDateFormat($('#infoDetailForm input[name=endDate]'), endDate);
          };

        function setSelectBox() {
            $.post("${ctx}/gadget/prepaymentMgmt/getSelectBoxData.do",
                   //{},
                   function(json) {

                       var statusResult = json.status;
                       var startArr = Array();
                       for (var i = 0; i < statusResult.length; i++) {
                           var obj = new Object();
                           obj.name=statusResult[i].descr;
                           obj.id=statusResult[i].id;
                           startArr[i]=obj
                       };
                       $("#statusCode").loadSelect(startArr);
                       $("#statusCode option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#statusCode").val("");
                       
                       $("#statusCode").selectbox();
                       
                       $("#amountStatus").selectbox();

                       var serviceResult = json.serviceType;
                       var serviceArr = Array();
                       for (var i = 0; i < serviceResult.length; i++) {
                           var obj = new Object();
                           obj.name=serviceResult[i].descr;
                           obj.id=serviceResult[i].id;
                           serviceArr[i]=obj;
                       };
                       $("#serviceTypeCode").loadSelect(serviceArr);
                       $("#serviceTypeCode option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#serviceTypeCode").val("");
                       $("#serviceTypeCode").selectbox();
                   });
        }




        // 날짜조회조건 생성
        function searchDivInit() {

            //일자별,기간별 날짜입력창 변경시
            $(function() { $('#hourlyStartDate')       .bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyEndDate')         .bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyStartHourCombo')  .bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyStartMinuteCombo').bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyEndHourCombo')    .bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyEndMinuteCombo')  .bind('change', function(event) { setSearchDate(); } ); });

            $(function() { $('#lastChargeStartDate')   .bind('change', function(event) { setSearchLastDate(); } ); });
            $(function() { $('#lastChargeEndDate')     .bind('change', function(event) { setSearchLastDate(); } ); });
            
            locDateFormat = "yymmdd";

            //탭별 일자DatePicker 생성
            $("#hourlyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#hourlyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

            $("#lastChargeStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#lastChargeEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            
            $.getJSON("${ctx}/common/getYear.do"
                ,{ supplierId : supplierId }
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear = json.currYear;//currDate.getYear();
                     var currDate = json.currDate;

                     $("#hourlyStartDate").val(currDate);
                     $("#hourlyEndDate").val(currDate);

                     $("#lastChargeStartDate").val(currDate);
                     $("#lastChargeEndDate").val(currDate);
                     
                     var hours = new Array();
                     for(var i = 0;i<=23;i++){
                         hours[i] = i<10?'0'+i:i+'';
                     }
            
                     var minute = new Array();
                     for(var j = 0; j<=59; j++){
                         minute[j] = j<10 ? '0'+ j:j+'';
                     }

                     $('#hourlyStartHourCombo').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
                     $('#hourlyStartMinuteCombo').numericOptions({from:0,to:59,selectedIndex:0,labels:minute});
                     
                     $('#hourlyEndHourCombo').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
                     $('#hourlyEndMinuteCombo').numericOptions({from:0,to:59,selectedIndex:59,labels:minute});
                     
                     $('#hourlyStartHourCombo').selectbox();
                     $('#hourlyStartMinuteCombo').selectbox();
                     
                     $('#hourlyEndHourCombo').selectbox();
                     $('#hourlyEndMinuteCombo').selectbox();
                     
                     setSearchDate();
                     setSearchLastDate();
            });   
        };

        // datepicker로 선택한 날짜의 포맷 변경
        function modifyDate(setDate, inst){
            var dateId = '#' + inst.id;

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{ dbDate : setDate, supplierId : supplierId }
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        $(dateId).trigger('change');
                    });
        }

        function setSearchDate(){
            var startDate = Number($('#hourlyStartHourCombo').val())<10?'0'+$('#hourlyStartHourCombo').val():$('#hourlyStartHourCombo').val();
                startDate += Number($('#hourlyStartMinuteCombo').val())<10?'0'+$('#hourlyStartMinuteCombo').val():$('#hourlyStartMinuteCombo').val();
            
            var endDate = Number($('#hourlyEndHourCombo').val())<10?'0'+$('#hourlyEndHourCombo').val():$('#hourlyEndHourCombo').val();
                endDate += Number($('#hourlyEndMinuteCombo').val())<10?'0'+$('#hourlyEndMinuteCombo').val():$('#hourlyEndMinuteCombo').val();
                
            $('#searchStartDate').val($('#hourlyStartDate').val());
            $('#searchEndDate').val($('#hourlyEndDate').val());

            $('#searchStartHour').val(startDate);
            $('#searchEndHour')  .val(endDate);
            
            convertSearchDate();
        }
        
        function setSearchLastDate(){
            $('#searchLastChargeStartDate').val($('#lastChargeStartDate').val());
            $('#searchLastChargeEndDate').val($('#lastChargeEndDate').val());
            
            convertSearchLastDate();
        }

        /**
         *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
            데이터 유형변경이 비동기식으로 되어있어 처리가 늦어질경우
            에러발생 => 동기식으로 변경해줌.
         */
        function convertSearchDate(){
            $.ajax({
                     type: "POST"
                   , cache: false
                   , async: false        // 동기식
                   , timeout: 10000
                   , dataType: "json"
                   , url: "${ctx}/common/convertSearchDate.do"
                   , data: {
                          searchStartDate : $('#searchStartDate').val()
                        , searchEndDate : $('#searchEndDate').val()
                        , supplierId : supplierId               
                   }
                   , success: function(json){
                       $('#searchStartDate').val(json.searchStartDate);
                       $('#searchEndDate').val(json.searchEndDate);
                   }
                   , error: function(){
                       Ext.Msg.alert("ERROR", "ERROR - Connection error.");
                   }
                });
        }

        function convertSearchLastDate(){
            $.ajax({
                     type: "POST"
                   , cache: false
                   , async: false        // 동기식
                   , timeout: 10000
                   , dataType: "json"
                   , url: "${ctx}/common/convertSearchDate.do"
                   , data: {
                          searchStartDate : $('#searchLastChargeStartDate').val()
                        , searchEndDate : $('#searchLastChargeEndDate').val()
                        , supplierId : supplierId               
                   }
                   , success: function(json){
                       $('#searchLastChargeStartDate').val(json.searchStartDate);
                       $('#searchLastChargeEndDate').val(json.searchEndDate);
                   }
                   , error: function(){
                       Ext.Msg.alert("ERROR", "ERROR - Connection error.");
                   }
                });
        }      

        /**
         * 날짜타입별 조회조건 검증
         */
        function validateSearchCondition(){
            if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
                //시작일이 종료일보다 큽니다
                parent.Ext.Msg.alert('<fmt:message key="aimir.error"/>', '<fmt:message key="aimir.season.error"/>');
                return false;
            }else{
                if(Number($('#searchStartDate').val()) == Number($('#searchEndDate').val())){
                    if(Number($('#searchStartHour').val()) > Number($('#searchEndHour').val())){
                        parent.Ext.Msg.alert('<fmt:message key="aimir.error"/>', '<fmt:message key="aimir.season.error"/>');
                        return false;
                    }
                }           
            }            

            return true;
        }

        function getSearchStartDate(){
            return $('#searchStartDate').val() + $('#searchStartHour').val() + "00"; //startDate            
        }

        function getSearchEndDate(){
            return $('#searchEndDate').val() + $('#searchEndHour').val() + "59";     //endDate            
        }

        // window resize event
        $(window).resize(function() {
            getPrepayContractDivData();
            //getChargeHistoryData();
            initChargeHistoryData();
            initInfoData();
        });

        /* 선불고객 리스트 START */
        var prepayContractGridOn = false;
        var prepayContractGrid;
        var prepayContractColModel;

        var getPrepayContractDivData = function() {
            var width = $("#prepayContract").width()-20;
            var mxwidth = 1200;

            var prepayContractStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 15}},
                url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentContractList.do",
                baseParams: {
                    contractNumber : $("#contractNumber").val(),
                    customerName : $("#customerName").val(),
                    statusCode : $("#statusCode").val(),
                    amountStatus : $("#amountStatus").val(),
                    mdsId		: $("#mdsId").val(),
                    //address : $("#address").val(),
                    locationId : $("#locationId").val(),
                    serviceTypeCode : $("#serviceTypeCode").val(),
                    searchLastChargeDate : $('#selectLastChargeDate').val(),
                    lastChargeStartDate: $("#searchLastChargeStartDate").val(),
                    lastChargeEndDate: $("#searchLastChargeEndDate").val(),
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contractNumber", "customerName", "mdsId", "serviceTypeCode", "serviceTypeName", "creditTypeCode", "creditTypeName", "tariffTypeName","mobileNo",
                         "prepaymentPowerDelay", "lastTokenDate", "currentCredit", "statusName", "emergencyCreditStartTime", "emergencyCreditMaxDuration", "emergencyCreditMaxDate",
                         "meterId", "mcuId", "modelName"],
                listeners: {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    },
                    load: function(store, record, options){
                        if (record.length > 0) {
                            // 데이터 load 후 첫번째 row 자동 선택
                            prepayContractGrid.getSelectionModel().selectFirstRow();
                        } else {
                            // 이전 데이터 모두 지움.
                            selectedContractNumber = "";
                            selectedServiceType = "";
                            getNotificationInfo();
                            //getAuthDeviceData();
                            //getChargeHistoryData();
                            if(chargeHistoryStore != null) {
                                 chargeHistoryStore.removeAll();
                            }
                        }
                    }
                }
            });

            prepayContractColModel = new Ext.grid.ColumnModel({
                columns: [
                     {header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'contractNumber', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.customername"/>", dataIndex: 'customerName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.celluarphone"/>", dataIndex: 'mobileNo', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>", dataIndex: 'lastTokenDate', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'currentCredit', width: (width > mxwidth) ? width*(120/mxwidth) : 120, align:'right'}
                    ,{header: "<fmt:message key="aimir.meterid"/>", dataIndex: 'mdsId', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                //    ,{header: "<fmt:message key="aimir.address"/>", dataIndex: 'address', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.supply.type"/>", dataIndex: 'serviceTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.contract.tariff.type"/>", dataIndex: 'tariffTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                //    ,{header: "<fmt:message key="aimir.hems.prepayment.limitpower"/>(kWh)", dataIndex: 'prepaymentPowerDelay', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.supplystatus"/>", dataIndex: 'statusName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDate', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                    //,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDuration', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 120
                }
            });

            if(prepayContractGridOn == false) {
                prepayContractGrid = new Ext.grid.GridPanel({
                      //title: '최근 한달 Demand Response History',
                      store: prepayContractStore,
                      colModel : prepayContractColModel,
                      //sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                      sm: new Ext.grid.RowSelectionModel({
                          singleSelect:true,
                          listeners: {
                              rowselect: function(sm, row, rec) {
                                  //Ext.getCmp("company-form").getForm().loadRecord(rec);
                                  selectedContractNumber = rec.get("contractNumber");
                                  selectedServiceType = rec.get("serviceTypeCode");
                                  selectedMeterId = rec.get("meterId");
                                  selectedMcuId = rec.get("mcuId");
                                  var selectedModelName = rec.get("modelName");
                                  enableRelay(selectedModelName);
                                  getAllHours();
                                  getNotificationInfo();
                                  //getAuthDeviceData();
                                  if(isAllLoading) getChargeHistoryData();
                              }
                          }
                      }),
                      autoScroll:false,
                      width: width,
                      height: 385,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'prepayContract',
                      viewConfig: {
                         // forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 15,
                          store: prepayContractStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                prepayContractGridOn = true;
                // 행 클릭 이벤트 정의
                //prepayContractGrid.on('rowclick', function(grid, rowIndex, e) {
                //    //index = rowIndex;
                //    // 챠트에 값 설정
                //    //setGridChartData();
                //    // 챠트 생성
                //    //renderGridChart();
                //
                //    var record = grid.getStore().getAt(rowIndex);  // 레코드의 Row를 가져온다.
                //    //var data = record.get("contractNumber");
                //});
            } else {
                prepayContractGrid.setWidth(width);
                var bottomToolbar = prepayContractGrid.getBottomToolbar();
                prepayContractGrid.reconfigure(prepayContractStore, prepayContractColModel);
                bottomToolbar.bindStore(prepayContractStore);
            }
        };

        // 통보설정 조회
        function getNotificationInfo(){

            if (selectedContractNumber == "") {
                $("#emergencyCreditBtn").hide();
                $("#notifySettingBtn").hide();
                $("#notifyPeriod option:eq(0)").attr("selected", "true");

                $("#interval").val("");

                $("#limitPower").val("");
                $("#limitPowerView").val("");
                $("#duration").val("");

                $("input[name=autoChange]").filter('input[value=false]').attr("checked", "checked");
                $("#notifyHour option:eq(0)").attr("selected", "true");

                $("#dayofweek").hide();
                $("#intervalDailyDiv").show();
                $("#intervalWeeklyDiv").hide();

                $("#intervalDaily option:eq(0)").attr("selected", "true");
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");

                $("#notifyPeriod").selectbox();
                $("#intervalDaily").selectbox();
                $("#intervalWeekly").selectbox();
                $("#notifyHour").selectbox();

                $("#threshold").val("");
                $("#thresholdView").val("");
                //$("#duration").text(emergencyCreditMaxDuration);
            } else {
                $("#emergencyCreditBtn").show();
                $("#notifySettingBtn").show();
            }

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getBalanceNotifySetting.do"
                    ,{contractNumber : selectedContractNumber}
                    ,function(json) {
                        var notificationPeriod         = json.result.notificationPeriod;
                        var notificationInterval       = json.result.notificationInterval;
                        var notificationTime           = json.result.notificationTime;
                        //var notificationTimeView       = json.result.notificationTimeView;
                        var notificationWeeklyMon      = json.result.notificationWeeklyMon;
                        var notificationWeeklyTue      = json.result.notificationWeeklyTue;
                        var notificationWeeklyWed      = json.result.notificationWeeklyWed;
                        var notificationWeeklyThu      = json.result.notificationWeeklyThu;
                        var notificationWeeklyFri      = json.result.notificationWeeklyFri;
                        var notificationWeeklySat      = json.result.notificationWeeklySat;
                        var notificationWeeklySun      = json.result.notificationWeeklySun;
                        //var lastNotificationDate       = json.result.lastNotificationDate;
                        var prepaymentThreshold        = json.result.prepaymentThreshold;
                        var prepaymentThresholdView    = json.result.prepaymentThresholdView;
                        var prepaymentPowerDelay       = json.result.prepaymentPowerDelay;
                        var prepaymentPowerDelayView   = json.result.prepaymentPowerDelayView;

                        var emergencyCreditAutoChange  = json.result.emergencyCreditAutoChange;
                        //var emergencyCreditStartTime   = json.result.emergencyCreditStartTime;
                        var emergencyCreditMaxDuration = json.result.emergencyCreditMaxDuration;
                        //var creditType                 = json.result.creditType;
                        //var devices                    = json.result.devices;

                        if (notificationPeriod == null || notificationPeriod == "") {
                            $("#notifyPeriod option:eq(0)").attr("selected", "true");
                        } else {
                            $("#notifyPeriod").val(notificationPeriod);
                        }

                        $("#interval").val(notificationInterval);

                        $("#limitPower").val(prepaymentPowerDelay);
                        $("#limitPowerView").val(prepaymentPowerDelayView);
                        $("#duration").val(emergencyCreditMaxDuration);

                        if (emergencyCreditAutoChange == true) {
                            $("input[name=autoChange]").filter('input[value=true]').attr("checked", "checked");
                        } else {
                            $("input[name=autoChange]").filter('input[value=false]').attr("checked", "checked");
                        }

                        if (notificationTime == null || notificationTime == "") {
                            $("#notifyHour option:eq(0)").attr("selected", "true");
                        } else {
                            $("#notifyHour").val(notificationTime);
                        }

                        if($("#notifyPeriod").val() == "2") {
                            $("#dayofweek").show();
                            $("#intervalDailyDiv").hide();
                            $("#intervalWeeklyDiv").show();

                            if (notificationInterval == null || notificationInterval == "") {
                                $("#intervalWeekly option:eq(0)").attr("selected", "true");
                            } else {
                                $("#intervalWeekly").val(notificationInterval);
                            }

                            $("#mon").attr("checked", notificationWeeklyMon);
                            $("#tue").attr("checked", notificationWeeklyTue);
                            $("#wed").attr("checked", notificationWeeklyWed);
                            $("#thu").attr("checked", notificationWeeklyThu);
                            $("#fri").attr("checked", notificationWeeklyFri);
                            $("#sat").attr("checked", notificationWeeklySat);
                            $("#sun").attr("checked", notificationWeeklySun);

                            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
                        } else {
                            $("#dayofweek").hide();
                            $("#intervalDailyDiv").show();
                            $("#intervalWeeklyDiv").hide();

                            if (notificationInterval == null || notificationInterval == "") {
                                $("#intervalDaily option:eq(0)").attr("selected", "true");
                            } else {
                                $("#intervalDaily").val(notificationInterval);
                            }

                            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
                        }

                        $("#notifyPeriod").selectbox();
                        $("#intervalDaily").selectbox();
                        $("#intervalWeekly").selectbox();
                        $("#notifyHour").selectbox();

                        $("#threshold").val(prepaymentThreshold);
                        $("#thresholdView").val(prepaymentThresholdView);
                        //$("#duration").text(emergencyCreditMaxDuration);

                    });
        }

        <%--
        /* 인증장비 아이디 리스트 START */
        var deviceGridOn = false;
        var deviceGrid;
        var deviceColModel;
        // 인증방식 확정 후 구현
        var getAuthDeviceData = function() {
            var width = $("#authDevice").width();

            var deviceStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/prepaymentMgmt/getAuthDeviceList.do",
                baseParams: {
                    contractNumber : selectedContractNumber
                },
                root:'result',
                fields: ["id", "authKey", "friendlyName", "writeDate"]
            });

            deviceColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key="aimir.hems.prepayment.devicename"/>", dataIndex: 'friendlyName',
                        editor: new Ext.form.TextField({
                            id: 'friendlyName',
                            allowBlank: false
                        })
                    }
                   ,{header: "<fmt:message key="aimir.hems.prepayment.devicekey"/>", dataIndex: 'authKey',
                       editor: new Ext.form.TextField({
                           id: 'authKey',
                           allowBlank: false
                       })
                   }
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 120
               }
            });


            if(deviceGridOn == false) {

                deviceGrid = new Ext.grid.EditorGridPanel({
                    //title: '<fmt:message key="aimir.hems.prepayment.authdevice"/>',
                    store: deviceStore,
                    colModel : deviceColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 100,
                    //stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    clicksToEdit: 1,
                    renderTo: 'authDevice',
                    viewConfig: {
                        forceFit:true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:[{
                        text: "추가",
                        scope: this,
                        handler: function(){
                           var recordObj = deviceGrid.getStore().recordType;
                           var r = new recordObj({
                               friendlyName : "",
                               authKey : "",
                               newRecord : 'yes', // Update할때 트리거로 이용하기 위해서
                               id : -1
                           });
                           deviceGrid.stopEditing();
                           deviceStore.insert(0, r);
                           deviceGrid.startEditing(0, 0); // 추가할 rowIndex, colIndex 위치
                        }
                    }]
                });
                deviceGridOn = true;
            } else {
              //var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
              deviceGrid.reconfigure(deviceStore, deviceColModel);
              //bottomToolbar.bindStore(chargeHistoryStore);
            }
        };
        --%>

        // 저장확인창
        function saveEmergenCreditInfoConfirm() {
            Ext.MessageBox.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>"
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           saveEmergenCreditInfo();
                                       }
                                   });
        }

        // Emergency Credit 정보를 저장
        function saveEmergenCreditInfo() {

            if (selectedContractNumber == "") {
                return;
            }

            var autoChange = $(":input:radio[name=autoChange]:checked").val();
            var duration = $("#duration").val();

            if (autoChange == null) {
                autoChange = "";
            }

            if (duration == "") {
                duration = "0";
            }

            if ($("#limitPowerView").val() == "") {
                $("#limitPower").val(0);
            } else {
                $("#limitPower").val(removeCharForReal($("#limitPowerView").val()));
            }

            $.post("${ctx}/gadget/prepaymentMgmt/updateEmergencyCreditInfo.do",
                  {contractNumber : selectedContractNumber,
                   autoChange     : autoChange,
                   duration       : duration,
                   limitPower     : $("#limitPower").val()
                   },
                  save_callback
           );
        }

        //저장 후 콜백
        function save_callback(json, textStatus) {
            if (json.status == "success") {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationInfo);
            } else {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
            }
        }



        /* 충전이력 리스트 START */
        var selectedContractNumber;
        var selectedServiceType;
        var chargeHistoryStore;
        var chargeHistoryGridOn = false;
        var chargeHistoryGrid;
        var chargeHistoryColModel;
        var initChargeHistoryData = function() {
            var width = $("#balanceHistoryTitle").width();
            var mxwidth = 1210;

            chargeHistoryStore = new Ext.data.JsonStore({
                url: "${ctx}/gadget/prepaymentMgmt/getChargeHistory.do",
                totalProperty: 'totalCount',
                root:'result',
                fields: ["lastTokenDate", "lastTokenDateView", "balance", "balanceView", "chargedCredit", "chargedCreditView", "currentCredit", "usedCost", "usedConsumption",
                         "consumption", "consumptionView", "keyNum", "payment", "authCode", "municipalityCode", "lastTokenId","activeImport","activeExport"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }                    
                }
            });

            chargeHistoryColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key="aimir.hems.prepayment.chargedate"/>", dataIndex: 'lastTokenDateView', width: (width > mxwidth) ? width*(130/mxwidth) : 130}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.usedcredit"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'usedCost', width: (width > mxwidth) ? width*(140/mxwidth) : 140, align:'right'}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.consumption"/>(kWh)", dataIndex: 'usedConsumption', width: (width > mxwidth) ? width*(160/mxwidth) : 160, align:'right'}
                    ,{header: "<fmt:message key="aimir.chargeAmount"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'chargedCreditView', width: (width > mxwidth) ? width*(110/mxwidth) : 110, align:'right'}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'balanceView', width: (width > mxwidth) ? width*(110/mxwidth) : 110, align:'right'}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.transactionNum"/>", dataIndex: 'lastTokenId', width: (width > mxwidth) ? width*(100/mxwidth) : 100}
                    ,{header: "<fmt:message key="aimir.prepayment.authCode"/>", dataIndex: 'authCode', width: (width > mxwidth) ? width*(99/mxwidth) : 99}
                    //,{header: "<fmt:message key="aimir.prepayment.municipalityCode"/>", dataIndex: 'municipalityCode', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                    ,{header: "<fmt:message key="aimir.prepayment.municipalityCode"/>", dataIndex: 'municipalityCode', width: (width > mxwidth) ? width*(99/mxwidth) : 99}
                    ,{header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.import'/>)", dataIndex: 'activeImport', width: (width > mxwidth) ? width*(120/mxwidth) : 120, align:'right'}
                    ,{header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.export'/>)", dataIndex: 'activeExport', width: (width > mxwidth) ? width*(120/mxwidth) : 120, align:'right'}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 120
                }
            });

            if(chargeHistoryGridOn == false) {
                chargeHistoryGrid = new Ext.grid.GridPanel({
                      store: chargeHistoryStore,
                      colModel : chargeHistoryColModel,
                      sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                      autoScroll:false,
                      width: width-20,
                      height: 165,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'chargeHistory',
                      viewConfig: {
                          //forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 5,
                          store: chargeHistoryStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                chargeHistoryGridOn = true;
            } else {
                chargeHistoryGrid.setWidth(width);
                var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
                chargeHistoryGrid.reconfigure(chargeHistoryStore, chargeHistoryColModel);
                bottomToolbar.bindStore(chargeHistoryStore);
            }
        };

        function getChargeHistoryData(){         
//            if(!(typeof(chargeHistoryStore) == 'undefined')){
//               if(isAllLoading){
//                    if(!beforeSendCheck()) return false;
//                }

            if(validateSearchCondition()){
                chargeHistoryStore.baseParams = {};
                chargeHistoryStore.setBaseParam('contractNumber', selectedContractNumber);
                chargeHistoryStore.setBaseParam('serviceType', selectedServiceType);
                chargeHistoryStore.setBaseParam('searchStartMonth', getSearchStartDate());
                chargeHistoryStore.setBaseParam('searchEndMonth', getSearchEndDate());   
                chargeHistoryStore.setBaseParam('allFlag', $('#showAll').is(':checked'));
                chargeHistoryStore.load({params:{start: 0, limit: 5}});                
            }


 //           }
        }
        
        var contractInfoModel;
        var contractInfoGrid;
        var contractInfoGridOn = false;
        var drawContractInfoData = function() {
        	var width=$('#infoForm').width();
        	var contractInfoStore = new Ext.data.JsonStore({
        		autoLoad   : {params:{start: 0, limit: PAGE_SIZE}},
                baseParams: {
                	 supplierId: supplierId,
                	 contractNumber: $("#contractNumberInfo").val(),
                     customerNo: $("#customerNoInfo").val(),
                     customerName: $("#customerNameInfo").val(),
                     mdsId: $("#mdsIdInfo").val()
                },
                url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentChargeList.do",
                totalProperty: 'totalCount',
                root: 'result',
                fields: ['contractNumber', 'customerNo', 'customerName', 'mdsId', 
                         'address', 'lastTokenDate', 'currentCredit', 'currentArrears', //'contractPrice', 
                         'barcode', 'chargeAvailable', 'statusName'],
                listeners: {
                  beforeload: function(store, options) {
                    var params = options.params;
                    if (params.start && params.start > 0) {
                      params.page = ((params.start + (PAGE_SIZE)) / (PAGE_SIZE));
                    } else { params.page = 1;}
                  }
                }
              });
        	
        	contractInfoModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber',
                    	tooltip: "<fmt:message key='aimir.contractNumber'/>"}
                   ,{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo',
                	   tooltip: "<fmt:message key='aimir.customerid'/>" }
                   ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName',
                	   tooltip: "<fmt:message key='aimir.customername'/>"}
                   ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'mdsId',
                	   tooltip: "<fmt:message key='aimir.meterid'/>"}
                   ,{header: "<fmt:message key='aimir.address'/>", dataIndex: 'address',
                	   tooltip: "<fmt:message key='aimir.address'/>"}
                   ,{header: "<fmt:message key='aimir.supplystatus'/>", dataIndex: 'statusName',
                	   tooltip: "<fmt:message key='aimir.supplystatus'/>"}
                   ,{header: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>", dataIndex: 'lastTokenDate', align: 'center',
                       tooltip: "<fmt:message key='aimir.hems.prepayment.lastchargedate'/>"}
                   ,{header: "<fmt:message key='aimir.credit'/>", dataIndex: 'currentCredit',  align: 'right',
                	   tooltip: "<fmt:message key='aimir.credit'/>"}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true                   
                   //,renderer: addTooltip
                }
              });
        	if(contractInfoGridOn == false) {
                contractInfoGrid = new Ext.grid.GridPanel({
                      store: contractInfoStore,
                      colModel : contractInfoModel,
                      sm: new Ext.grid.RowSelectionModel({
                    	  singleSelect:true,
                    	  listeners : {
                    		  rowselect : function(sm, colIndex, rec) {
                    			    var contractNumber = rec.json.contractNumber;
                    		        $("#infoDetailForm input[name=contractNumberInfo]").val(contractNumber);

                    		        drawBalanceHistoryData(contractNumber);
                    		  }
                    	  }
                    	  }),
                      autoScroll:false,
                      width:width,
                      height: 290,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'contractInfoDiv',
                      viewConfig: {
                          forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: PAGE_SIZE,
                          store: contractInfoStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                
                contractInfoGrid.setWidth(width);
                contractInfoGridOn = true;
            } else {
                contractInfoGrid.setWidth(width);
                var bottomToolbar = contractInfoGrid.getBottomToolbar();
                contractInfoGrid.reconfigure(contractInfoStore, contractInfoModel);
                bottomToolbar.bindStore(contractInfoStore);
            }
        }
        
        var balanceHistoryListModel;
        var balanceHistoryListGrid;
        var balanceHistoryListGridOn = false;
        var drawBalanceHistoryData = function(contractNumber) {
        	var width = $('#infoDetailForm').width();

        	var balanceHistoryListStore = new Ext.data.JsonStore({
        		autoLoad   : {params:{start: 0, limit: PAGE_SIZE}},
                baseParams: {
                	contractNumber: contractNumber,
                    startDate: $("#infoDetailForm input[name=startDate]").val() || '00000000',
                    endDate: $("#infoDetailForm input[name=endDate]").val() || '99999999',
                    supplierId: supplierId
                },
        	      url: "${ctx}/gadget/prepaymentMgmt/getBalanceHistoryList.do",
        	      totalProperty: 'totalCount',
        	      root: 'result',
        	      fields: ["lpTime","writeDate","accUsage","usage", "accBill","bill", "activeImport", "activeExport","lpTime",'balance'],
        	      listeners: {
        	        beforeload: function(store, options) {
        	          var params = options.params;
        	          if (params.start && params.start > 0) {
        	            params.page = ((params.start + PAGE_SIZE) / PAGE_SIZE);
        	          } else { params.page = 1;}
        	        }
        	      }
        	    });
        	
        	balanceHistoryListModel = new Ext.grid.ColumnModel({
                columns: [
        		  {header: "<fmt:message key='aimir.paydate'/>",  align: 'left', dataIndex:'writeDate',
            			tooltip: "<fmt:message key='aimir.paydate'/>"},
            	  {header: "<fmt:message key='aimir.meter.metertime'/>",  align: 'left', dataIndex:'lpTime',
               			tooltip: "<fmt:message key='aimir.meter.metertime'/>"},
                  {header: "<fmt:message key='aimir.accu.usage'/>" + "[<fmt:message key='aimir.unit.kwh'/>]", dataIndex:'accUsage', align: 'right',
                    	tooltip: "<fmt:message key='aimir.accu.usage'/>"+"[<fmt:message key='aimir.unit.kwh'/>]"},
                  {header: "<fmt:message key='aimir.accu.bill'/>" + "[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'accBill',
                    	 tooltip: "<fmt:message key='aimir.accu.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
                  {header: "<fmt:message key='aimir.usage'/>" + "[<fmt:message key='aimir.unit.kwh'/>]", dataIndex:'usage', align: 'right',
                    	 tooltip: "<fmt:message key='aimir.usage'/>"+"[<fmt:message key='aimir.unit.kwh'/>]"},
               	  {header: "<fmt:message key='aimir.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'bill',
                   	 	tooltip: "<fmt:message key='aimir.bill'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
                  {header: "<fmt:message key='aimir.balance'/>"+"[<fmt:message key='aimir.price.unit'/>]", align: 'right', dataIndex:'balance',
                       	tooltip: "<fmt:message key='aimir.balance'/>"+"[<fmt:message key='aimir.price.unit'/>]"},
                  {header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.import'/>)",  align: 'right', dataIndex:'activeImport',
                          tooltip: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.import'/>)"},
                  {header: "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.export'/>)",  align: 'right', dataIndex:'activeExport',
                          tooltip:  "<fmt:message key='aimir.meter.value'/>(<fmt:message key='aimir.button.export'/>)"}
                ],
                defaults: {sortable: true, 
                          menuDisabled: true,
                          renderer: addTooltip
                 }       
              });
        	
        	if(balanceHistoryListGridOn == false) {
                balanceHistoryListGrid = new Ext.grid.GridPanel({
                      store: balanceHistoryListStore,
                      colModel : balanceHistoryListModel,
                      autoScroll:false,
                      width:width,
                      height: 290,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'balanceHistoryDiv',
                      viewConfig: {
                          forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: PAGE_SIZE,
                          store: balanceHistoryListStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                
                balanceHistoryListGrid.setWidth(width);
                balanceHistoryListGridOn = true;
            } else {
                balanceHistoryListGrid.setWidth(width);
                var bottomToolbar = balanceHistoryListGrid.getBottomToolbar();
                balanceHistoryListGrid.reconfigure(balanceHistoryListStore, balanceHistoryListModel);
                bottomToolbar.bindStore(balanceHistoryListStore);
            }
        }
        
        
        /**
         * 숫자만 입력. 정수
         * focusin:comma 제거, focusout:comma 추가
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForInt(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForInt(val);
            val = removeFstZeroForInt(val);
            src.value = val;
            //src.focus();
        }

        // 숫자 이외 문자 제거
        function removeCharForInt(val) {
            var num = val.replace(/[\D]/g, '');
            return num;
        }

        // 앞에 0 제거
        function removeFstZeroForInt(val) {
            var pattern = /(^0*)(\d+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        /**
         * 숫자만 입력. 실수
         * focusin:comma 제거, focusout:comma 추가
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForReal(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForReal(val);
            val = removeFstZeroForReal(val);
            src.value = val;
            src.focus();
        }

        // 숫자 이외 문자 제거:소수점 허용
        function removeCharForReal(val) {
            var num = val.replace(/[^\d\.]/g, '');
            var idx = 0;
            var len = 0;

            if (num.indexOf('.', num.indexOf('.')+1) != -1) {
                idx = num.indexOf('.');
                len = num.length;
                num = num.substring(0, idx+1) + num.substring(idx+1, len).replace(/\./g, '');
            }

            return num;
        }

        // 앞에 0 제거:소수점포함
        function removeFstZeroForReal(val) {
            var pattern = /(^0*)([\d\.]+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        // inputbox에서 focus가 나가면 comma추가
        function addComma(src) {
            var pattern = /(^-?[0-9]+)([0-9]{3})/;
            var strVal = src.value;

            while(pattern.test(strVal)) {
                strVal = strVal.replace(pattern, '$1,$2');
            }

            src.value = strVal;
        }
        
        //선택한 계약의 미터모델이 Relay 기능이 가능한 모델인지 검색 후 가능한 모델일 경우 화면에 Relay On/Off 버튼을 보여준다.
        function enableRelay(selectedModelName) {
            
            $.getJSON("${ctx}/gadget/prepaymentMgmt/getRelayEnableModel.do",
                    {devicemodelName : selectedModelName},
                    function(result) {
                        var namesOfContain = result.namesOfContain;
                        if (namesOfContain.length > 0) {
                            for ( var i = 0; i < namesOfContain.length; i++) {
                                switch (namesOfContain[i]) {
                                    case 'relayControl':
                                        $('#relayControlButton').show();
                                        break;
                                }
                            }
                        }
                    }
                );
        }

        // 잔액통보주기 시간 selectbox 데이터를 조회한다.
        var getAllHours = function() {

            var params = {"contractNumber" : selectedContractNumber};

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getAllHours.do",
                    params,
                    function(result) {
                        var hours = result.hours;

                        // 시간 콤보박스 생성
                        $("#notifyHour").pureSelect(hours);
                        $("#notifyHour").selectbox();
                    }
                );
        };

        // 선택된 주기에 따라 요일항목 enable/disable
        function controlDayofWeek() {
            var periodValue = $("#notifyPeriod").val();
            if (periodValue == "2") {
                $("#dayofweek").show();
                $("#intervalDailyDiv").hide();
                $("#intervalWeeklyDiv").show();
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
                $("#intervalDaily option:eq(0)").attr("selected", "true");
                $("#intervalDaily").selectbox();
            } else {
                $("#mon").attr("checked", false);
                $("#tue").attr("checked", false);
                $("#wed").attr("checked", false);
                $("#thu").attr("checked", false);
                $("#fri").attr("checked", false);
                $("#sat").attr("checked", false);
                $("#sun").attr("checked", false);
                $("#dayofweek").hide();
                $("#intervalWeeklyDiv").hide();
                $("#intervalDailyDiv").show();
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
                $("#intervalWeekly option:eq(0)").attr("selected", "true");
                $("#intervalWeekly").selectbox();
            }
        }

        // 통보설정 저장 확인창
        function saveNotifySettingConfirm() {
            Ext.MessageBox.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>"
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           saveNotifySetting();
                                       }
                                   });
        }

        // 통보설정 저장
        function saveNotifySetting() {

            if ($("#notifyPeriod").val() == "2") {
                $("#interval").val($("#intervalWeekly").val());
            } else {
                $("#interval").val($("#intervalDaily").val());
            }

            if ($("#thresholdView").val() == "") {
                $("#threshold").val(0);
            } else {
                $("#threshold").val(removeCharForInt($("#thresholdView").val()));
            }

            $.post("${ctx}/gadget/prepaymentMgmt/updateBalanceNotifySetting.do",
                  {contractNumber : selectedContractNumber,
                   operatorId     : operatorId,
                   serviceType    : selectedServiceType,
                   period         : $("#notifyPeriod").val(),
                   interval       : $("#interval").val(),
                   hour           : $("#notifyHour").val(),
                   threshold      : $("#threshold").val(),
                   mon            : $("#mon").is(":checked"),
                   tue            : $("#tue").is(":checked"),
                   wed            : $("#wed").is(":checked"),
                   thu            : $("#thu").is(":checked"),
                   fri            : $("#fri").is(":checked"),
                   sat            : $("#sat").is(":checked"),
                   sun            : $("#sun").is(":checked")
                   },
                  save_callback
           );
        }

        //저장 후 콜백
        function save_callback(json, textStatus) {
            if (json.status == "success") {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationInfo);
            } else {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
            }
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
        
        function changeChargeDateStatus() {

        	if($('#selectLastChargeDate').val() == 'enable') {
        		$('#searchlastChargeDate_2').show();
        	} else {
        		$('#searchlastChargeDate_2').hide();
        	}

        }

        function cmdRelayOn() {
            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOn.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }
        
        function cmdRelayOff() {
            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOff.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }

        function cmdRelayStatus() {

            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemoteGetStatus.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                var rtnStr = returnData.rtnStr;
                $('#commandResult').val(rtnStr);

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }
        
        var changeCredit = function() {
            $('#duration').val('');
        }
        
      //report window(Excel)

        function openExcelReport(exType) {
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
            var obj = new Object();
            var header = new Array();
            var param = new Array();

            if(exType == 'main_list'){
                obj.excelType = 'main_list'

                //title
                header[0] = '<fmt:message key="aimir.contractNumber"/>'; // Contract No.
                header[1] = '<fmt:message key="aimir.customername"/>'; // Customer Name
                header[2] = '<fmt:message key="aimir.hems.prepayment.lastchargedate"/>'; //Last Charge Date
                header[3] = '<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key="aimir.price.unit"/>)'; //Remaining Credit(Dollar(s))
                header[4] = '<fmt:message key="aimir.meterid"/>'; //Meter ID
                header[5] = '<fmt:message key="aimir.supply.type"/>'; // SupplyType
                header[6] = '<fmt:message key="aimir.contract.tariff.type"/>'; //Tariff Type
                header[7] = '<fmt:message key="aimir.supplystatus"/>'; // Supply Status
                header[8] = '<fmt:message key="aimir.hems.prepayment.validperiod"/>'; // Valid Date
                header[9] = '<fmt:message key="aimir.celluarphone"/>'; //Cellular Phone
                header[10] = '<fmt:message key="aimir.hems.prepayment.prepaymentCustomerList"/>'; // 파일명 : Prepayment Customer List
                

                //parameter
                param[0] = $("#contractNumber").val();
                param[1] = $("#customerName").val();
                param[2] = $("#statusCode").val();
                param[3] = $("#mdsId").val();
                param[4] = $("#locationId").val();
                param[5] = $("#serviceTypeCode").val();
                param[6] = supplierId;
                param[7] = $("#amountStatus").val();
                param[8] = $('#selectLastChargeDate').val();
                param[9] = $("#searchLastChargeStartDate").val();
                param[10] = $("#searchLastChargeEndDate").val();
            }else if(exType == 'history'){
                obj.excelType = 'history'

                //title
                header[0] = '<fmt:message key="aimir.hems.prepayment.chargedate"/>'; //Date
                header[1] = '<fmt:message key="aimir.hems.prepayment.usedcredit"/>(<fmt:message key="aimir.price.unit"/>)'; //Your used Cost(Dollar(s))
                header[2] = '<fmt:message key="aimir.hems.prepayment.consumption"/>(kWh)'; //Your used Energy Consumption(kwh)
                header[3] = '<fmt:message key="aimir.chargeAmount"/>(<fmt:message key="aimir.price.unit"/>)'; //Charged Amount(Dollar(s))
                header[4] = '<fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>(<fmt:message key="aimir.price.unit"/>)'; //Total Balance(Dollar(s))
                header[5] = '<fmt:message key="aimir.hems.prepayment.transactionNum"/>'; //Transaction Number
                header[6] = '<fmt:message key="aimir.prepayment.authCode"/>'; //Authorization Code
                header[7] = '<fmt:message key="aimir.prepayment.municipalityCode"/>'; //Municipality code
                header[8] = '<fmt:message key="aimir.prepayment.balancehistory"/>'; // 파일명 : Balance History
                header[9] = '<fmt:message key="aimir.meter.value"/>'; // Meter Value
                header[10] = '<fmt:message key="aimir.button.import"/>'; // Active Import
                header[11] = '<fmt:message key="aimir.button.export"/>'; // Active Export

                //parameter
                param[0] = selectedContractNumber;
                param[1] = selectedServiceType;
                param[2] = getSearchStartDate();
                param[3] = getSearchEndDate();
                param[4] = $('#showAll').is(':checked');
            }else {
                Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.firmware.msg20"/>');
                return;
            }

            obj.fmtMessage = header;
            obj.condition = param ;

            var winObj = window.open('${ctx}/gadget/prepaymentMgmt/prepaymentBalanceExcelDownloadPopup.do', "Open Excel Report", opts);
            winObj.opener.obj = obj;
        }

      
      var eventHandler = {
   	      contractInfoSearch: function(callback) {
   	    	drawContractInfoData();
   	        },
   	     infoDetailSearch: function() {
   	    	drawBalanceHistoryData($("#infoDetailForm input[name=contractNumberInfo]").val());
   	     },
   	     infoDetailTotalExcel: function() {
   	    	excelType = 8;

            var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
            contactListObj = new Object();
            var header = new Array();
            var param = new Array();

            header[0]='Balance History';
            header[1]='<fmt:message key="aimir.contractNumber"/>';

            //parameter
            param[1] = $("#infoDetailForm input[name=contractNumberInfo]").val();
            param[2] = $("#infoDetailForm input[name=startDate]").val() || '00000000';
            param[3] = $("#infoDetailForm input[name=endDate]").val() || '99999999'; 
            param[4] = supplierId;

            contactListObj.fmtMessage = header;
            contactListObj.condition = param ;

            window.open('${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do', "Balance History Excel", opt);
   	     },
   	  	modifiedDateFormat: function(date) {        
          var $this = $(this);

          $.getJSON("${ctx}/common/convertLocalDate.do", 
            {supplierId: supplierId, dbDate: date},
            function(data) {            
              $this.siblings("." + $this.attr('name')).val(data.localDate);
            });
        },
    	      
      }

        /*]]>*/
    </script>
</head>
<body>
  <div id="menu">
    <ul>
      <li>
        <a href="#defultTab">
          <fmt:message key="aimir.default"/>
        </a>
      </li>
      <li>
        <a href="#infoTab">
          <fmt:message key='aimir.prepayment.balancehistory'/>
        </a>
      </li>
    </ul>

    <div id="defultTab">
		<!--
		<input type="hidden" name="searchStartMonth" id="searchStartMonth"/>
		<input type="hidden" name="searchEndMonth" id="searchEndMonth"/>
		-->
		<input id="searchStartDate" type="hidden"/>
		<input id="searchEndDate" type="hidden" />
		<input id="searchStartHour" type="hidden"/>
		<input id="searchEndHour" type="hidden" />
		<input id="searchLastChargeStartDate" type="hidden" />
		<input id="searchLastChargeEndDate" type="hidden" />
		
		<input type="hidden" name="limitPower" id="limitPower"/>
		<input type="hidden" name="locationId" id="locationId"/>
		<input type="hidden" name="interval" id="interval"/>
		<input type="hidden" name="threshold" id="threshold"/>
		
	    <!--contract no.-->
        <div style="margin-top: 10px; margin-bottom: 10px;">
            <table class="search_basic" style="width: auto;">
                <tr>
                    <td><fmt:message key='aimir.contractNumber'/></td>
                    <td><input id="contractNumber" name="contractNumber" style="width:130px"/></td>
                    <td><fmt:message key="aimir.meterid"/><!-- 미터 아이디 --></td>
                    <td><input name="mdsId" id='mdsId' type="text" style="width:100px"/></td>
                    <td><fmt:message key="aimir.supplystatus"/><!-- 상태 --></td>
                    <td><select id="statusCode" name="statusCode" style="width:110px"><option value=""></option></select></td>
                </tr>
                <tr style="height: 5px"></tr>
                </tr>
                	<td><fmt:message key="aimir.customername"/><!-- 고객명 --></td>
                    <td><input id="customerName" name="customerName" style="width:130px"/></td>
					<td><fmt:message key="aimir.supply.type"/><!-- 공급타입 --></td>
                    <td><select id="serviceTypeCode" name="serviceTypeCode" style="width:100px"><option value=""></option></select></td>
					<td><fmt:message key='aimir.amount.status'/><!-- 금액 상태 --></td>
                    <td>
                    	<select id="amountStatus" name="amountStatus" style="width:80px">
                    		<option value=""><fmt:message key='aimir.all'/></option>
                    		<option value="negative"><fmt:message key='aimir.negative'/></option>
                    		<option value="positive"><fmt:message key='aimir.positive'/></option>
                    	</select>
                    </td>
                    <td><fmt:message key='aimir.hems.prepayment.lastchargedate'/></td>
                    <td>
                    	<div id="searchlastChargeDate_1">
                    		<select id="selectLastChargeDate" name="selectLastChargeDate" style="width:80px" onchange="javascript:changeChargeDateStatus();">
	                    		<option value="disable"><fmt:message key='aimir.disable2'/></option>
                    			<option value="enable"><fmt:message key='aimir.enable2'/></option>
                    		</select>
                    	</div>
                    </td>
                    <td>
                    	<div id="searchlastChargeDate_2" style="display: none;">
                            <ul>
                                <li><input id="lastChargeStartDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="lastChargeEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                            </ul>
                        </div>       
                    </td>
                    <td><a href="#" class="btn_blue" onClick="getPrepayContractDivData();"><span><fmt:message key='aimir.button.search'/></span></a></td>
                    <td><a href="#" class="btn_blue" onClick="openExcelReport('main_list');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
               </tr>
            </table>
        </div>
	    
	    <!--//contract no.-->
	    <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
	        <div id="treeDivA"></div>
	    </div>
	
	    <!-- content1-->
	    <div id="" class="overflow_hidden">
	
	        <!--content1 right -->
	        <div class="prepay_operator_right" style="margin: 20px 0px 0px 0px">
	        <div class="title_basic margin_0"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.emergencycredit"/><!-- Emergency Credit --></div>
	            <div class="blance_term_box">
	                <!-- Emergency Credit  -->
	
	                <ul class="choice_credit">
	                    <li><input type="radio" id="autoChange" name="autoChange" class="radio" value="false" onClick="changeCredit()"/></li>
	                    <li class="textspace"><fmt:message key="aimir.manual"/><!-- 수동 --></li>
	                    <li><input type="radio" id="autoChange" name="autoChange" class="radio" value="true"/></li>
	                    <li class="textspace"><fmt:message key="aimir.auto"/><!-- 자동 --></li>
	                    <li ><fmt:message key="aimir.hems.prepayment.validDay"/><!-- 유효기간 --></li>
	                    <li><input id="duration" name="duration" style="width:35px" class="target" onKeyUp="removeCommaForInt(event, this);" onFocus="removeCommaForInt(event, this);" onBlur="removeCommaForInt(event, this);"/></li>
	                    <li>Days</li>
	                </ul>
	
	<%--
	            <div class="divbox2 padding_t10">
	                <label class="text_blue"><fmt:message key="aimir.hems.prepayment.authdevice"/></label>
	                <div id="authDevice"></div>
	            </div>
	
	            <div class="divbox2 padding_t10">
	            </div>
	--%>
	           <!-- <div class="expect_day margin_t10"> -->
	                <!-- 제한전력 <span class="text_blk bold"><fmt:message key="aimir.hems.prepayment.limitpower"/></span>-->
	                <!-- <span class="margin_l5"><input id="limitPowerView" name="limitPowerView" style="width:100px" class="target" onkeyup="removeCommaForReal(event, this);" onfocus="removeCommaForReal(event, this);" onblur="addComma(this);"/></span>
	                <span class="margin_l5">kWh</span>
	            </div>
	            -->
	                <input type="hidden" id="limitPowerView" name="limitPowerView" style="width:100px" class="target" />
	
	                <div id="ecBtnList">
	                <div id="emergencyCreditBtn" class="basic_rightbtn margin_t5">
	                    <a href="#" class="btn_blue margin_r3" onClick="saveEmergenCreditInfoConfirm();"><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
	                    <a href="#" class="btn_blue" onClick="getNotificationInfo();"><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
	                </div>
	                </div>
	            </div>
	            <!--// Emergency Credit  -->
	
	            <!-- 잔액 통보 설정 -->
	            <div class="title_basic margin_b1"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.balancenotify"/><!-- 잔액정보 설정 --></div>
	
	            <div class="blance_term_box">
	                <table class="balance_term">
	                    <colgroup>
	                    <col width="" />
	                    <col width="80%" />
	                    </colgroup>
	                    <!-- 잔액 통보 주기를 설정합니다.<span>( 설정된 주기로 잔액을 통보합니다.)</span> -->
	                    <tr><th colspan="2"><fmt:message key="aimir.hems.prepayment.intervalmsg"/></th>
	                    </tr>
	                    <tr><td><fmt:message key="aimir.period2"/><!-- 주기 --></td>
	                        <td>
	                            <select id="notifyPeriod" name="notifyPeriod" style="width:80px" onChange="controlDayofWeek(this);">
	                                <option value="1"><fmt:message key="aimir.daily"/></option>
	                                <option value="2"><fmt:message key="aimir.weekly"/></option>
	                            </select>
	                        </td>
	                    </tr>
	                    <tr><td><fmt:message key="aimir.hems.prepayment.interval"/><!-- 간격 --></td>
	                        <td>
	                            <!-- Daily -->
	                            <div id="intervalDailyDiv" style="display:none;">
	                            <select id="intervalDaily" name="intervalDaily" style="width:40px;">
	                            <c:forEach var="i" begin="1" end="6" step="1" varStatus="status">
	                                <option value="${i}">${i}</option>
	                            </c:forEach>
	                            </select>
	                            </div>
	                            <!-- Weekly -->
	                            <div id="intervalWeeklyDiv" style="display:none;">
	                            <select id="intervalWeekly" name="intervalWeekly" style="width:40px;">
	                            <c:forEach var="j" begin="1" end="4" step="1" varStatus="status">
	                                <option value="${j}">${j}</option>
	                            </c:forEach>
	                            </select>
	                            </div>
	                            <!-- Days -->
	                            &nbsp;&nbsp;<span id="intervalUnit"></span>
	                        </td>
	                    </tr>
	                    <!-- weekly 일경우에만 나옴 -->
	                    <tr id="dayofweek" style="display:none;"><td><fmt:message key="aimir.dayofweek"/></td>
	                        <td>
	                            <ul class="prepay_weekly">
	                                <li><input type="checkbox" id="mon" name="mon" class="checkbox2" value="true"/><fmt:message key="aimir.day.mon"/><!-- Mon --></li>
	                                <li><input type="checkbox" id="tue" name="tue" class="checkbox2" value="true"/><fmt:message key="aimir.day.tue"/><!-- Tue --></li>
	                                <li><input type="checkbox" id="wed" name="wed" class="checkbox2" value="true"/><fmt:message key="aimir.day.wed"/><!-- Wed --></li>
	                                <li><input type="checkbox" id="thu" name="thu" class="checkbox2" value="true"/><fmt:message key="aimir.day.thu"/><!-- Thu --></li>
	                                <li><input type="checkbox" id="fri" name="fri" class="checkbox2" value="true"/><fmt:message key="aimir.day.fri"/><!-- Fri --></li>
	                                <li><input type="checkbox" id="sat" name="sat" class="checkbox2" value="true"/><fmt:message key="aimir.day.sat"/><!-- Sat --></li>
	                                <li><input type="checkbox" id="sun" name="sun" class="checkbox2" value="true"/><fmt:message key="aimir.day.sun"/><!-- Sun --></li>
	                            </ul>
	                        </td>
	                    </tr>
	                    <!-- <tr><td><fmt:message key="aimir.hour"/></td>
	                        <td>
	                            <select id="notifyHour" name="notifyHour" style="width:140px"></select>
	                        </td>
	                    </tr> -->
	                    <!-- 잔액 통보 값을 설정합니다.<span>(잔액이 설정된 값에 이르면 통보합니다.)</span> -->
	                    <tr><th colspan="2" class="notice_value"><fmt:message key="aimir.hems.prepayment.balancemsg"/></th>
	                    </tr>
	                    <tr><td><fmt:message key="aimir.hems.prepayment.notifythreshold"/>(<fmt:message key='aimir.price.unit'/>)<!-- 통보값 --></td>
	                        <td><input id="thresholdView" name="thresholdView" type="text" class="target" style="width:80px" onKeyUp="removeCommaForInt(event, this);" onFocus="removeCommaForInt(event, this);" onBlur="addComma(this);"/></td>
	                    </tr>
	                </table>
	
	                <div id="notifyBtnList">
	                <div id="notifySettingBtn" class="basic_rightbtn margin_t5">
	                    <a href="#" class="btn_blue" onClick="saveNotifySettingConfirm();"><span><fmt:message key="aimir.ok"/><!-- OK --></span></a>
	                    <a href="#" class="btn_blue" onClick="getNotificationInfo();"><span><fmt:message key="aimir.cancel"/><!-- Cancel --></span></a>
	                </div>
	                </div>
	            </div>
	            <!--// 잔액 통보 설정 -->
	            <!-- Relay On/Off 버튼 -->
	            <!-- 잔액 통보 설정 -->
	            <div id="relayControlButton" class="basic_rightbtn margin_t5" style="float: left;">
	                    <a href="#" class="btn_blue" style="margin-left: 0px"  onclick="cmdRelayOn();"><span><fmt:message key="aimir.meter.command.RelayOn"/><!-- Relay On --></span></a>
	                    <a href="#" class="btn_blue" onClick="cmdRelayOff();"><span><fmt:message key="aimir.meter.command.RelayOff"/><!-- Relay Off --></span></a>
	                    <a href="#" class="btn_blue" onClick="cmdRelayStatus();"><span><fmt:message key="aimir.meter.command.RelayStatus"/><!-- Relay Status --></span></a>
	                    
	                    <div class="meterinfo-textarea clear">
	                        <ul>
	                            <li><textarea id="commandResult" readonly style="width: 438px; height: 52px;">Result</textarea></li>
	                        </ul>
	                    </div>
	            </div>
	            
	        </div>
	        <!--//content1 right -->
	
	        <!--content1 left -->
	        <div>
	            <div id="prepayContract" class="ext_grid"></div>
	        </div>
	        <!--//content1 left -->
	
	    </div>
	    <!--// content1 -->
	
	    <div class="h_dotline_blue"></div>
	
	    <!-- content2 -->
        <div id="balanceHistoryTitle" class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.prepayment.balancehistory'/></div>
        <div class="saving_goal" style="width: 50%">
             <table>
                <tr>
                    <td class="padding-r20px">
                        <div id="hourly">
                            <ul>
                                <li><input id="hourlyStartDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><select id="hourlyStartHourCombo" class="sm"></select></li>
                                <li><select id="hourlyStartMinuteCombo" class="sm"></select></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="hourlyEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><select id="hourlyEndHourCombo" class="sm"></select></li>
                                <li><select id="hourlyEndMinuteCombo" class="sm"></select></li>
                            </ul>
                        </div>          
                    </td>
<!--
                    <td><select id="fromYearCombo" style="width:60px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.year1" /></label></td>
                    <td><select id="fromMonthCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.month" /></label></td>
                    <td><select id="fromDayCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.day" /></label></td>
                    <td><select id="fromTimeCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.hour" /></label></td>
                    <td><select id="fromMinCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.minute" /></label></td>                                                            
                    <td>~</td>
                    <td><select id="toYearCombo" style="width:60px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.year1" /></label></td>
                    <td><select id="toMonthCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.month" /></label></td>
                    <td><select id="toDayCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.day" /></label></td>
                    <td><select id="toTimeCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.hour" /></label></td>
                    <td><select id="toMinCombo" style="width:40px"></select></td>
                    <td><label class="datetxt"><fmt:message key="aimir.minute" /></label></td>
-->                    
                    <td><input id="showAll" type="checkbox" ></td>
                    <td><label class="datetxt"><fmt:message key="aimir.showall" /></label></td>
                    <td></td>
                    <td class="btnspace"><a href="javascript:getChargeHistoryData();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>

                    <td><a href="#" class="btn_blue" onClick="openExcelReport('history');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
                </tr>
            </table>

        </div>
        <div id="chargeHistory" class="balance_grid margin_t5"></div>
	    <!--// content2 -->
		
    </div>   

    <div id="infoTab">
      
      <!-- 검색조건 -->
      <form id="infoForm" class="searchoption-container margin-t10px margin-b5px">
        <div class="clear-form">
          <span class="searchSet">
            <fmt:message key="aimir.contractNumber"/>
            <input class="searchSet" id="contractNumberInfo" type="text" style="width:110px;">
          </span>
          <span class="searchSet">
            <fmt:message key="aimir.customername"/>
            <input class="searchSet" id="customerNameInfo" type="text" style="width:120px;">
          </span>
          <span class="searchSet">
            <fmt:message key="aimir.customerid"/>
            <input class="searchSet" id="customerNoInfo" type="text" style="width:120px;">
          </span>
          <span class="searchSet">
            <fmt:message key="aimir.meterid"/>
            <input class="searchSet" id="mdsIdInfo" type="text" style="width:120px;">
          </span>
          <span id="contractInfoSearch" class="am_button margin-l10 margin-t1px searchSet">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
        </div>
      </form>
      <!-- 검색조건 끝 -->
      <div id="contractInfoDiv" class="ext_grid"></div>
      
      <div class="h_dotline_blue"></div>

      <!-- 검색조건 -->
      <form id="infoDetailForm" class="searchoption-container margin-t10px margin-b5px">
        <div class="wrapper">
          <label class="check"><fmt:message key='aimir.prepayment.balancehistory'/></label>
        </div>
        <div class="wrapper">
          <input name="contractNumberInfo" class="hidden" type="text"></input>
          <span class="searchSet">
            <label><fmt:message key="aimir.searchDate"/></label>
            <input class="alt startDate searchSet" name="startDateDisplay"  type='text' readOnly/>
            <input name="startDate" class="no-width" type="text"/>    
            <label>~</label>
            <input class="alt endDate searchSet" name="endDateDisplay"  type='text' readOnly/>
            <input name="endDate" class="no-width searchSet" type="text"/>    
          </span>
          <span id="infoDetailSearch" class="am_button margin-l10 margin-t1px searchSet">
            <a class="on"><fmt:message key="aimir.button.search" /></a>
          </span>
          <span id='infoDetailTotalExcel' class="am_button margin-l10 margin-t1px searchSet">
            <a><fmt:message key="aimir.button.excel"/></a>
          </span>
        </div>
      </form>
      <!-- 검색조건 끝 -->
  	<div id="balanceHistoryDiv" class="ext_grid"></div>
    </div>  
  
  </div>
</body>
</html>