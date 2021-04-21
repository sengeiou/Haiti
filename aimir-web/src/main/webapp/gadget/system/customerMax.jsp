<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ include file="/taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/gadget/system/preLoading.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><fmt:message key="aimir.customerview"/></title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />

<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

<%-- TreeGrid 관련 js --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/util/numberUtil.js"></script>

<%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.Search.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.RowActions.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/src/widgets/grid/GridPanel.js"></script> --%>

<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner {
            text-align: center !important;
            font-weight: bold !important;
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

        .meterDiv2 {
            position: absolute;
            top: 170;
            left: 835;
            z-index: 100;
        }        
        .meterDiv {
            position: absolute;
            top: 170;
            left: 825;
            z-index: 100;
        }          
        .btn-savecontract {
        	right: 320;
        }
</style>
<script>

    var getArrearsPaymentCount;
    var getArrearsContractCount;
    var getCurrentArrears;
    //분할납부기능을 사용하는지 여부
    var isPartpayment = '${isPartpayment}';
    var initArrears = '${initArrears}';

    var customerId = '';
    var contractId = '';
    var selectContractId = '';
    //var serviceType = '';
    var serviceTypeTab = '';
    var tempServiceType = '';
    var customerNo = '';

    var serviceTypeName = "";

    var allStr;
    var contractModifyMeterFlex;

    var fcChartDataXml;
    var fcChart;

    var supplierId = "${supplierId}";
    var editAuth = "${editAuth}";
    var role = "${role}"
    
    var debtSaveArrU = new Array();
    
    if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment) {
        Range.prototype.createContextualFragment = function(html) {
            var frag = document.createDocumentFragment();
            div = document.createElement("div");
            frag.appendChild(div);
            div.outerHTML = html;
            return frag;
        };
    }

    //#####################################
    //ExtJS Meter List fetch func
    //#####################################

    //추가 프로퍼티
    var updContractSearchMdsId = "";
    var updContractSearchGs1 = "";

    // Meter 그리드 관련 프로퍼티
    var updContractMeterGridOn = false;
    var updContractMeterGrid;
    var updContractMeterColModel;
    //var updContractMeterCheckSelModel;

    // Meter 리스트 가져오기
    function getUpdContractMeterGridList() {
        var width = $("#meterDiv2").width();
        var rowSize = 3;

        var updContractMeterStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize}},
            url: "${ctx}/gadget/contract/getMeterGridList.do",
            baseParams: {
                mdsId : updContractSearchMdsId,
                gs1 : updContractSearchGs1
            },
            // total count value
            totalProperty: 'totalCount',
            root: 'result',
            fields: ["mdsId", "id", "gs1"],
            listeners: {
                beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                }
            }
        });//Meter Store End

        //Meter model column setting
        updContractMeterColModel = new Ext.grid.ColumnModel({
            columns: [
                {id: "mdsId", header: '<fmt:message key='aimir.meterid'/>', dataIndex: 'mdsId'},
                {id: "gs1", header: '<fmt:message key='aimir.shipment.gs1'/>', dataIndex: 'gs1'}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 195
           }
        });

        if (updContractMeterGridOn == false) {
            updContractMeterGrid = new Ext.grid.GridPanel({
                store: updContractMeterStore,
                colModel : updContractMeterColModel,
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                autoScroll:false,
                //autoScroll:true,
                //autoExpandColumn: "mdsId",
                width: 400,
                style: 'align:center; ',
                height: 125,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                listeners: {
                    rowclick: updContractMeterRowClickEvent
                },
                renderTo: "meterDiv2",
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: rowSize,
                    store: updContractMeterStore,
                    displayInfo: false,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            updContractMeterGridOn = true;
        } else {
            //미터 그리드 reconfig
            var bottomToolbar = updContractMeterGrid.getBottomToolbar();
            updContractMeterGrid.reconfigure(updContractMeterStore, updContractMeterColModel);
            bottomToolbar.bindStore(updContractMeterStore);
        }

        hide();
    };//func getMeterList End

    //meterId Click Event 리스너
    function updContractMeterRowClickEvent(grid, rowIndex, e) {
        var s= grid.getSelectionModel();
        var row = s.getSelected();

        var mdsId = row.get('mdsId');
        var gs1 = row.get('gs1');

        //$("#mdsId").val(mdsId);
        $("#meterMdsIdU").val(mdsId);
        $("#meterGs1U").val(gs1);

        //ContractMeterId =row.get('id');
        //$("#ConMeterId").val(ContractMeterId);
        //console.log("conmeterid==>"+ ContractMeterId);
    }

    Ext.onReady(function() {
    	Ext.QuickTips.init();
        // editAuth 가 true 가 아니면 CUD 제한
        if (editAuth == "true") {
            $("#pane-Customer-Info-Button").show();
            $(".contractInfoUpdateForm").show();
        } else {
            $("#pane-Customer-Info-Button").hide();
            $(".contractInfoUpdateForm").hide();
        }

        $("#meterSearchButton2").click(function() {
            //검색 조건을 만들어준다.
            updContractSearchMdsId = $("#meterMdsIdU").val();
            updContractSearchGs1 = $("#meterGs1U").val();

            //미터 리스트 가져오기.
            getUpdContractMeterGridList();
        });
    });

    $(function() {
    	//admin Role 일 경우에만 계약을 생성한 operator정보를 보여준다.
        if(role == 'admin') {
        	$('#operatorInfo1').show();
        	$('#operatorInfo2').show();
        }
        
        //고객추가 버튼 click event
        $("#customerAddForm").click(function() {
            $("#pane-Customer-Info-Button").hide();
            $('#pane-tab-Customer').load('${ctx}/gadget/system/customerAddMax.do');
        });

        $("#customerDelete").click(function() {
            if ( confirm("<fmt:message key='aimir.msg.wantdelete' />") ) {
                var options = {
                        success : customerDeleteResult,
                        url : '${ctx}/gadget/system/customerMax.do?param=customerDelete&customerId=' + customerId,
                        type : 'post',
                        datatype : 'json'
                    };
                        $('#customerForm_').ajaxSubmit(options);
                    } else
                        return;
        });

        $('#_allCustomerTab').click(function() { serviceTypeTab = '';  });
        $('#_emCustomerTab').click(function() { serviceTypeTab = 'EM';  });
        $('#_gmCustomerTab').click(function() { serviceTypeTab = 'GM';  });
        $('#_wmCustomerTab').click(function() { serviceTypeTab = 'WM';  });
        $('#_hmCustomerTab').click(function() { serviceTypeTab = 'HM';  });
        $('#_vcCustomerTab').click(function() { serviceTypeTab = 'VC';  });

        // Prepayment History Tab Click Event
        /** 2014.12.29 simhanger
            더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
        $('#_paymentLogTab').click(function() {
            window.setTimeout(function(){getPrepaymentLog();}, 500);
        });
        */
        $('#_contractChangeLogTab').click(function() {
            window.setTimeout(function(){getContractChangeLog();}, 500);
        });

        $('#_billingMonthTab').click(function() {
            window.setTimeout(function(){updateFChart();}, 500);
        });

        $("#customerTab").tabs();
        $.getJSON('${ctx}/gadget/system/supplier/getSupplierTypes.do', {supplierId: supplierId},
                function(json){
                    var isEmTab = false;
                    var isGmTab = false;
                    var isWmTab = false;
                    var isHmTab = false;
                    var isVcTab = false;
                    $.each(json.supplyTypes, function(index, supplyType){
                        if(supplyType['typeCode'] == "3.1") {
                            isEmTab = true;
                        } else if(supplyType['typeCode'] == "3.2") {
                            isWmTab = true;
                        } else if(supplyType['typeCode'] == "3.3") {
                            isGmTab = true;
                        } else if(supplyType['typeCode'] == "3.4") {
                            isHmTab = true;
                        } else if(supplyType['typeCode'] == "3.5") {
                            isVcTab = true;
                        }
                    });

                    if(!isVcTab) $("#customerTab").tabs( "remove" , 5 );
                    if(!isHmTab) $("#customerTab").tabs( "remove" , 4 );
                    if(!isWmTab) $("#customerTab").tabs( "remove" , 3 );
                    if(!isGmTab) $("#customerTab").tabs( "remove" , 2 );
                    if(!isEmTab) $("#customerTab").tabs( "remove" , 1 );

            });

        $("#contractStatusTab").subtabs();
        
        $('#customer_type').selectbox();

        // update 화면 출력
        $("#contractInfoUpdateForm").click(function() {

            if (contractId == '' || contractId == 'undefined') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.contract.select" />');
                return ;
            }

            $("#updCheckValue").html("");
            $("#updCheckValue").hide();

            $('#contractInfoDetail').hide();
            $('#contractInfoUpdate').show();

            //추가 2012-08-31
            $('#customerNoU').val(customerNo);
            $('#contractNumberU').val(contractNumber);

            $.getJSON('${ctx}/gadget/system/customerMax.do?param=contractInfoUpdateForm',
                    {contractId:contractId , serviceType:tempServiceType, customerNo:customerNo},
                    //success 콜백함수.
                    function(json) {
                        var serviceTypeList = json.serviceList;
                        var serviceTypeArr = Array();
                        for (var i = 0; i < serviceTypeList.length; i++) {
                            var obj = new Object();
                            obj.name=serviceTypeList[i].descr;
                            obj.id=serviceTypeList[i].id;
                            serviceTypeArr[i]=obj;
                        };
                        $("#serviceTypeCodeU").pureSelect(serviceTypeArr);
                        if (json.service != null) {
                            $("#serviceTypeCodeU option[value=" + json.service.id + "]").attr("selected", "true");
                        }
                        //공급지역
                        $("#locationU").pureSelect(json.locationList);
                        if (json.location != null) {
                            $("#locationU option[value=" + json.location.id + "]").attr("selected", "true");
                        }

                        //계약종별
                        $("#tariffIndexU").pureSelect(json.tariffTypeList);

                        //tarifftype selected 처리
                        if (json.tariff != null) {
                            $("#tariffIndexU option[value=" + json.tariff.id + "]").attr("selected", "true");
                        }

                        //계약용량
                        if (json.contract.contractDemand == "" || json.contract.contractDemand == "null" || json.contract.contractDemand == null){
                            $("#contractDemandU").attr("value" , "");
                        } else {
                            $("#contractDemandU").attr("value" , json.contract.contractDemand);
                        }
                        //공급상태
                        var statusList = json.statusList;
                        var statusListArr = Array();
                        for (var i = 0; i < statusList.length; i++) {
                            var obj = new Object();
                            obj.name=statusList[i].descr;
                            obj.id=statusList[i].id;
                            statusListArr[i]=obj;
                        };
                        $("#statusU").pureSelect(statusListArr);
                        if (json.status != null) {
                            $("#statusU option[value=" + json.status.id + "]").attr("selected", "true");
                        }

                        //서비스 요청 타입
                        $("#serviceType2U").pureSelect(json.serviceType2List);
                        if (json.contract.serviceType2 != null) {
                            $("#serviceType2U option[value=" + json.contract.serviceType2 + "]").attr("selected", "true");
                        }
/*
                         //Amount Paid
                        if (json.contract.amountPaid == "" || json.contract.amountPaid == "null" || json.contract.amountPaid == null){
                            $("#amountPaidU").attr("value" , "");
                        } else {
                            $("#amountPaidU").attr("value" , json.contract.amountPaid);
                        }

                         //Receipt Number
                        if (json.contract.receiptNumber == "" || json.contract.receiptNumber == "null" 
                                || json.contract.receiptNumber == null || json.contract.receiptNumber == '"null"'){
                            $("#receiptNoU").attr("value" , "");
                        } else {
                            $("#receiptNoU").attr("value" , json.contract.receiptNumber);
                        }
*/
                        // 임계치
                        // Threshold 1
                        if (json.contract.threshold1 == "" || json.contract.threshold1 == "null" || json.contract.threshold1 == null){
                            $("#threshold1U").attr("value" , "");
                        } else {
                            $("#threshold1U").attr("value" , json.contract.threshold1);
                        }                        
                        // Threshold 2
                        if (json.contract.threshold2 == "" || json.contract.threshold2 == "null" || json.contract.threshold2 == null){
                            $("#threshold2U").attr("value" , "");
                        } else {
                            $("#threshold2U").attr("value" , json.contract.threshold2);
                        }    
                        // Threshold 3
                        if (json.contract.threshold3 == "" || json.contract.threshold3 == "null" || json.contract.threshold3 == null){
                            $("#threshold3U").attr("value" , "");
                        } else {
                            $("#threshold3U").attr("value" , json.contract.threshold3);
                        }    



                        //지불타입
                        $('option', $('#creditTypeU')).remove();
                        $.each(json.creditTypeList, function(index, creditType){
                            $('#creditTypeU').append("<option value='"
                                +creditType['id'] + "' id='"+creditType['code']+"' "
                                +">"+creditType['descr']+"</option>");
                        });
                        if (json.creditType != null) {
                            $("#creditTypeU option[value=" + json.creditType.id + "]").attr("selected", "true");
                        }


                        getArrearsPaymentCount = json.contract.arrearsPaymentCount;
                        getArrearsContractCount = json.contract.arrearsContractCount;

                        var tempContractCount = json.contract.arrearsContractCount;
                        //arrearsPaymentCount가 null이면 분할납부를 완료 했다는 의미.
                        if((getArrearsPaymentCount != null && getArrearsPaymentCount != '') &&
                        		(tempContractCount != null && tempContractCount != '"null"'))
                            $('#arrearsContractCountU').val(json.contract.arrearsContractCount);
                        else 
                            $('#arrearsContractCountU').val('');

                        $("#creditTypeU").change(function(value){   
                            var selectedText = document.getElementById('creditTypeU').options[document.getElementById('creditTypeU').selectedIndex].id;
                            if (selectedText == "2.2.1"||selectedText == "2.2.2") {
                                $('#prepaymentStatusTr').show();
                                //$('#prepaymentStatusTr2').show();
                                //$('#prepaymentStatusTr3').show();
                                //$('#prepaymentStatusTr4').show();
                                $('#prepaymentStatusTr5').show();

                                $('.contractUpdate').css('bottom', '0');
                                $('#meterDiv2').css('left',935);

                            } else {
                                $("#prepaymentStatusTr").hide();
                                $("#prepaymentStatusTr2").hide();
                                $("#prepaymentStatusTr3").hide();
                                $("#prepaymentStatusTr4").hide();
                                $('#prepaymentStatusTr5').hide();

                                $(".contractUpdate").css("bottom", "-22");
                                $('#meterDiv2').css('left',835);
                            }
                        });

                        $("#creditTypeU").change();

                        //지불타입이 선불일때 : 선불 2.2.1
                        var creditStatusList = json.creditStatusList;
                        var creditStatusArr = Array();
                        for (var i = 0; i < creditStatusList.length; i++) {
                            var obj = new Object();
                            obj.name=creditStatusList[i].descr;
                            obj.id=creditStatusList[i].id;
                            creditStatusArr[i]=obj;
                        };
                        $("#creditStatusU").pureSelect(creditStatusArr);
                        
                        var debtInfoList = json.debtTypeList;
                        var debtInfoArr = Array();
                        var debtInfoSizeU = debtInfoList.length;
                        for (var i = 0; i < debtInfoSizeU; i++) {
                            var obj = new Object();
                            obj.name=debtInfoList[i].debtType;
                            obj.id=debtInfoList[i].debtRef;
                            debtInfoArr[i]=obj;
                        };

                        if(debtInfoSizeU == 0) {
                        	$('#debtTypeU').pureSelect(debtInfoArr);
                        	$("#debtTypeU").selectbox();
                        	$('#debtAmountU').val("-");
                        	$('#debtContractCntU').val("-");
                        	$('#debtTypeU').attr("readOnly","readOnly");
                        	$('#debtContractCntU').attr("readOnly","readOnly");
                        	$('#debtAmountTitleU').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",""));
                        	$('#debtContractCntTitleU').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",""));
                        } else {
                        	$('#debtTypeU').attr("readOnly",false);
                        	$('#debtContractCntU').attr("readOnly",false);
                        
                        	$('#debtTypeU').pureSelect(debtInfoArr);
                            $("#debtTypeU option[value=" + debtInfoList[0].debtType + "]").attr("selected", "true");
                            
                            $('#debtAmountTitleU').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfoList[0].debtType)); 
                            $('#debtAmountU').val(debtInfoList[0].debtAmount);
                            
                            $('#debtContractCntTitleU').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfoList[0].debtType));
                            $('#debtPaymentCntU').val(debtInfoList[0].debtPaymrntCount);
                            if(isPartpayment == 'true' && debtInfoList[0].debtContractCount != null && debtInfoList[0].debtContractCount != '') {
                            	$('#debtContractCntU').val(debtInfoList[0].debtContractCount);
                            } else {
                            	$('#debtContractCntU').val("");
                            }
                            
                            $("#debtTypeU").change(function(value) {
                                changeDebtU();
                            });

                            $("#debtTypeU").change();
                        }
                        
                        if (json.creditType != null) {
                            var creditType = json.creditType.code;
                            if (creditType == "2.2.0") {        // 후불
                                $("#creditStatusU option:eq(0)").attr("selected", "true");
                                $("#prepaymentThresholdU").val("");
                                $("input:radio[name='chargeAvailable']").removeAttr("checked");
                            } else {                            // 선불/Emergency Credit
                                // 지불상태
                                if (json.creditStatus != null) {
                                    $("#creditStatusU option[value=" + json.creditStatus.id + "]").attr("selected", "true");
                                } else {
                                    $("#creditStatusU option:eq(0)").attr("selected", "true");
                                }

                                var chargeAvailable = json.contract.chargeAvailable;

                                if (chargeAvailable != null && chargeAvailable != "") {
                                    if (chargeAvailable == "true") {
                                        $("input:radio[name='chargeAvailable']:radio[value='1']").attr("checked", true);
                                    } else {
                                        $("input:radio[name='chargeAvailable']:radio[value='0']").attr("checked", true);
                                    }
                                } else {
                                    $("input:radio[name='chargeAvailable']").removeAttr("checked");
                                }
                            }
                        } else {
                            $("#creditStatusU option:eq(0)").attr("selected", "true");
                            $("#prepaymentThresholdU").val("");
                            $("#currentArrearsU").val("");
                            $("input:radio[name='chargeAvailable']").removeAttr("checked");
                        }

                        // old arrears
                        if(json.contract.currentArrears2 == null || json.contract.currentArrears2 == "") {
                            $("#currentArrears2U").val("");
                        } else {
                            $("#currentArrears2U").val(json.contract.currentArrears2);
                        }

                        // Current arrears
                        if(json.contract.currentArrears == null || json.contract.currentArrears == "") {
                        	$("#currentArrearsU").val("");
                        } else {
                        	$("#currentArrearsU").val(json.contract.currentArrears);
                        }
                        
                        // 잔액최소임계치
                        var prepaymentThreshold = json.contract.prepaymentThreshold;
                        if (prepaymentThreshold == null || prepaymentThreshold == "null" || prepaymentThreshold == '"null"') {
                            $("#prepaymentThresholdU").val("");
                        } else {
                            $("#prepaymentThresholdU").val(prepaymentThreshold);
                        }

                        // mdsID
                        if ($('#meterMdsId').val() == "-") {
                            $('#meterMdsIdU').val('');
                        } else {
                            $('#meterMdsIdU').val($('#meterMdsId').val());
                        }

                        // preMdsId
                        if ($('#preMdsId').val() == "-"){
                            $('#preMdsIdU').val('');
                        } else {
                            $('#preMdsIdU').val($('#preMdsId').val());
                        }
                        
                        // meterGs1
                        if ($('#meterGs1').val() == "-"){
                            $('#meterGs1U').val('');
                        } else {
                            $('#meterGs1U').val($('#meterGs1').val());
                        }
                        
                        // sic
/*                         var sicName = $("#sicName").val();
                        var sicId = $("#sicId").val();
                        $("#sicUText").val(sicName);
                        $("#sicIdU").val(sicId); */

                        //location
                        var locationName = $("#locationName").val();
                        var locationId = $("#locationId").val();

                        $("#locationUText").val(locationName);
                        $("#locationU").val(locationId);

                        //css
                        $("#serviceTypeCodeU").selectbox();
                        // $("#locationU").selectbox();
                        $("#tariffIndexU").selectbox();
                        $("#statusU").selectbox();
                        $("#creditTypeU").selectbox();

                        $("#creditStatusU").selectbox();
                        $("#debtTypeU").selectbox();

                        // ApplyDate
                        if (json.applyDate != null && json.applyDate != "") {
                            var obj = $("#startDateTime");
                            modifyDateLocal(json.applyDate, obj);
                        } else {
                            $("#startDateTime").val("");
                            $("#startDateTimeHidden").val("");
                        }

                        // Threshold
                        $("#threshold").val(json.usageThreshold);
                    }); //getJson End

            //var meterDiv= "meterDiv2";

            setSearchCondition();

            //매터 그리드 인스턴스가 존재하면 삭제
            /*if (MeterGridOn == true) {
                MeterGrid.destroy();

                //MeterGrid 인스턴스가 존재 하지 않음으로 설정.
                MeterGridOn = false;
            }*/

            $("#meterDiv2").show();
            //미터리스트 가져오기
            getUpdContractMeterGridList();

        });// CONTracf updateForm End

        function setSearchCondition() {
            updContractSearchMdsId = "";
            updContractSearchGs1 = "";
        }

        $("#contractInfoUpdateCancel").click(function() {
            $('#contractInfoUpdate').hide();
            $('#contractInfoDetail').show();
            $('.contractUpdate').css('bottom', '50');
            clearDebtSave();
        });

        //계약 수정 확인 버튼 event
        $('#contractUpdate').click(function() {
            //계약번호
            if ($("#contractNumberU").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.enterContractNo"/>");      // 계약번호를 입력해 주세요.
                $("#contractNumberU").focus();
                return;
            }

            var checkYN = $("#updCheckYN").val();
            if (($("#contractNumber").val() != $("#contractNumberU").val() || contractId != selectContractId) && checkYN != "true") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.msg.chkvalidationcontractno"/>");  // 계약번호 유효성 체크를 해주십시오.
                $("#contractNumberU").focus();
                $("#contractNumberU").select();
                return;
            }

            // 미터 아이디
            if ($("#meterMdsIdU").val() == "" && $("#meterGs1U").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.inputMeterid"/> (or <fmt:message key="aimir.shipment.gs1"/> )");       // 미터 아이디를 입력해 주세요.
                $("#meterMdsIdU").focus();
                return;
            }
            var meterId = $("#meterMdsIdU").val();

            var preMdsId = $.trim($("#preMdsIdU").val());
            var meterGs1 = $.trim($("#meterGs1U").val());
            /*if ($("#meterMdsIdU").val() != "" && $("#ConMeterId").val() != "") {     // Meter list 에서 선택한 Meter
                meterId = $("#ConMeterId").val();
            } else {
                meterId = $("#meterMdsIdU_id").val();
            }*/

            // 선택한 Meter 가 다른 contract 에 연결되어있는지 체크
            var params = {
                    meterNo : meterId,
                    contractId : ((contractId != null && contractId != "") ? contractId : "")
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/gadget/system/customerMax.do?param=getCheckContractByMeterNo",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("json=" + jsonText);
            var checkResult = json.result;

            if (checkResult != null) {
                if (checkResult.exist == "true") {              // 미터아이디가 존재할 경우
                    if (checkResult.hasContract == "true") {       // Contract 와 연결되어 있는 경우
                        var msgArr = new Array();
                        msgArr.push("<fmt:message key="aimir.contract.msg.checkmeter"/>\n");
                        if (checkResult.customerName != "") {
                            msgArr.push("\n<fmt:message key="aimir.contract.currentlinkedcustomer"/> : ");
                            msgArr.push(checkResult.customerName);
                        }
                        msgArr.push("\n<fmt:message key="aimir.contract.currentlinkedcontract"/> : ");
                        msgArr.push(checkResult.contractNumber);
                        //if (!confirm("<fmt:message key="aimir.contract.msg.checkmeter"/>")) {
                        if (!confirm(msgArr.join(""))) {
                            return;
                        }
                    }
                } else {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.msg.invalidmeterid"/>");          // 유효하지 않은 미터 아이디 입니다.
                    return;
                }
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.erroroccured"/>");
                return;
            }

            if ($('#locationUText').val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.supplySelectArea"/>");       // 공급지역을 선택 해 주세요
                return false;
            }

             var isReturn = false;
             $.ajax({
                 type : "POST",
                 async : false,
                 data : {
                     contractNumber:$('#contractNumberU').val(),
                     supplierId : supplierId
                 },
                 dataType : "json",
                 url:'${ctx}/gadget/system/getPartpayInfoByContractNumber.do',
                 success: function(json,status) {
                     if ( json.contract.creditTypeCode == "2.2.1" || json.contract.creditTypeCode == "2.2.2") {
                             //숫자가 아닐경우.
                             if(isNaN($("#currentArrears2U").val())) {
                                 $("#currentArrears2U").val('');
                                 $("#currentArrears2U").focus();
                                 isReturn=true;
                             }

                             if(isNaN($("#currentArrearsU").val())) {
                                 $("#currentArrearsU").val('');
                                 $("#currentArrearsU").focus();
                                 isReturn=true;
                             }

                             if(isPartpayment == 'true') {
								  var arrearsPaymentCount = json.contract.arrearsPaymentCount;
								  var arrearsContractCount = json.contract.arrearsContractCount;
								  var currentArrears = json.contract.currentArrears;

	                              if(isNaN($("#arrearsContractCountU").val()) 
	                              		|| (($("#arrearsContractCountU").val() != null && $("#arrearsContractCountU").val() != '') 
	                              				&& $("#arrearsContractCountU").val() <= 0)) {
	                                  $("#arrearsContractCountU").val('');
	                                  $("#arrearsContractCountU").focus();
	                                  isReturn=true;
	                              }

	                              if(arrearsPaymentCount != null && arrearsPaymentCount != "" && arrearsPaymentCount != 0
	                              		&& Number($('#currentArrearsU').val()) != Number(currentArrears)) {
	                                  //납부한적이 한번이라도 있다면 수정할 수 없음
	                                  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.validation.noModify.arrears'/>");
	                                  isReturn=true;
	                              }
	                              
	                              if(arrearsPaymentCount != null && arrearsPaymentCount != "" && arrearsPaymentCount != 0
	                              		&& Number($('#arrearsContractCountU').val()) != Number(arrearsContractCount)) {
	                                  //납부한적이 한번이라도 있다면 수정할 수 없음
	                                  Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.validation.notModify.paymentContract'/>");
	                                  isReturn=true;
	                              }

	                              //미수금이 없는 경우(init Arrears 제외) 납부 계약 횟수를 수정할 수 없음.
	                              if((Number($('#currentArrearsU').val()) <= Number(initArrears) || $('#currentArrearsU').val() == '' || $('#currentArrearsU').val() == null)
	                              && ($('#arrearsContractCountU').val() > 0)) {
	                              	Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.arrears.initialCredit'/>");
	                                  isReturn=true;
	                              }
                             }
                         }
                 }
             })
             if(isReturn) {
                 return;
             }
             
            if (contractId != null && contractId != "") {        // update contract
                var chargeAvailable = $('input[name="chargeAvailable"]:checked').val();

                $.ajax({
                    type : "POST",
                    data : {
                        'mdsId': meterId,
                        'contractNumber':$('#contractNumberU').val(),
                        'threshold' : $('#threshold').val(),//추가
                        'serviceType2' : $('#serviceType2U').val(),//추가
                        'threshold1' : $('#threshold1U').val(),
                        'threshold2' : $('#threshold2U').val(),
                        'threshold3' : $('#threshold3U').val(),
                        'serviceTypeCode' : $('#serviceTypeCodeU').val(),
                        'tariffIndex' : ($('#tariffIndexU').val() != null) ? $('#tariffIndexU').val() : '',
                        'locationId2' : $('#locationU').val(),
                        'contractDemand' : $('#contractDemandU').val(),
                        'status' : $('#statusU').val(),
                        'creditType' : $('#creditTypeU').val(),
                        'creditStatus' : $('#creditStatusU').val(),
                        'prepaymentThreshold' : $('#prepaymentThresholdU').val(),
                        'startDatetime' : $('#startDateTimeHidden').val(),
                        'barcode' : $("input[name=barcode]").val(),
                        'id' : contractId,
                        'prevContractId' : selectContractId,
                        'customerId' : customerId,
                        "currentArrears2" : $("#currentArrears2U").val(),
                        'currentArrears' : $('#currentArrearsU').val(),
                        'arrearsContractCount' : $('#arrearsContractCountU').val(),
                        'chargeAvailable' : (chargeAvailable == null) ? "" : chargeAvailable,
                        'preMdsId' : preMdsId,
                        'meterGs1' : $("#meterGs1U").val(),
                        "isPartpayment" : isPartpayment,
                        "debtSaveInfo" : JSON.stringify(debtSaveArrU)
                    },
                    dataType:"json",
                    url:'${ctx}/gadget/system/customerMax.do?param=updateContract',
                    success:function(json, status) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.result);
                        clearDebtSave();
                        if(json.result == 'Modification Success') {
                        
                            tempServiceType = $('#serviceTypeCodeU').val();
                        
                            //IHD에 조인된 미터인 경우 - using the balace of charges 값이 변한 경우 IHD에 메세지 전송
                            if(!($('#meterMdsId').val() == null || $('#meterMdsId').val() == "-" || $('#meterMdsId').val() == "")) {
                                //Contract Consumption값이 바뀌었는지 체크
                                if($('#contractDemand').val() != $('#contractDemandU').val()) {
                                    $.getJSON('${ctx}/gadget/system/getGroupIdbyMember.do'
                                            , {mdsId : $('#meterMdsId').val() }
                                            , function (returnData){
                                                if(returnData.groupId != -1) {
                                                    $.getJSON('${ctx}/gadget/system/schedule/getGroupTypeByGroup.do'
                                                            , {group : returnData.groupId }
                                                            , function (jsonView){
                                                                //IHD 타입인지 체크
                                                                if(jsonView.result == "IHD") {
                                                                    $.getJSON('${ctx}/gadget/system/getCustomerUpdateInfosMessage.do'
                                                                            , {mdsId : $('#meterMdsId').val()
                                                                            , groupId : returnData.groupId}
                                                                            , function (jsonView){
                                                                                Ext.Msg.alert('<fmt:message key='aimir.message'/>','returnData: '+jsonView.status+" rtnStr:"+jsonView.rtnStr);
                                                                            }
                                                                    );
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        }
                        loadCustomerInfo();
                        $('#contractInfoUpdate').hide();
                        $('#contractInfoDetail').show();

                        treeData.reload(treeData.lasgOptions);
                    },
                    error:function(request, status) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"contractUpdate ajax comm failed");
                    }
                });// ajaxEnd
            } else {        // insert contract
                $.ajax({
                    type : "POST",
                    data : {
                        'mdsId' : meterId,
                        'contractNumber' : $('#contractNumberU').val(),
                        'threshold' : $('#threshold').val(),//추가
                        'threshold1' : $('#threshold1U').val(),
                        'threshold2' : $('#threshold2U').val(),
                        'threshold3' : $('#threshold3U').val(),
                        'serviceType2' : $('#serviceType2U').val(),//추가
                        'serviceTypeCode' : $('#serviceTypeCodeU').val(),
                        'tariffIndex' : ($('#tariffIndexU').val() != null) ? $('#tariffIndexU').val() : '',
                        'location.id' : $('#locationU').val(),
                        'contractDemand' : $('#contractDemandU').val(),
                        'status' : $('#statusU').val(),
                        'creditType' : $('#creditTypeU').val(),
                        'creditStatus' : $('#creditStatusU').val(),
                        'prepaymentThreshold' : $('#prepaymentThresholdU').val(),
                        'startDatetime' : $('#startDateTimeHidden').val(),
                        'supplier' : supplierId,
                        "customerId" : customerId,
                        "prevContractId" : selectContractId,
                        "isPartpayment" : isPartpayment,
                        "initArreara" : initArrears
                    },
                    dataType : "json",
                    url : '${ctx}/gadget/system/customerMax.do?param=createContract',
                    success : function(json, status) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.result);
                        loadCustomerInfo();
                        $('#contractInfoUpdate').hide();
                        $('#contractInfoDetail').show();

                        treeData.reload(treeData.lasgOptions);
                    },
                    error : function(request, status) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>',"contract Insert ajax comm failed");
                    }
                });// ajaxEnd
            }
        });

        $("#contractDel").click(function() {

            if(contractId == '' || contractId == 'undefined') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.contract.select" />');
                return ;
            }

            var delStr = "<fmt:message key="aimir.msg.wantdelete"/>";
            if(confirm(delStr)){

                $.getJSON('${ctx}/gadget/system/customerMax.do?param=deleteContract', {contractId:contractId},

                    function(json) {
                        if (json.result == "success") {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.hems.information.successDelete"/>");
                            // 목록 조회
                            //customerExtTreeSearch();
                            treeData.reload(treeData.lasgOptions);
                            hideContractStatusTab();
                        } else {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.hems.alert.failDelete"/>");
                        }
                });


            }else { return; }
        });

        //$("#startDatetime").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});

        $('#searchA').click(function() {
            getContractChangeLog();
        });

        /** 2014.12.29 simhanger
            더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
        $('#searchB').click(function() {
           // getFlexObject('prepaymentLog').search();
            getPrepaymentLog();
        });
        */
        $('#searchC').click(function() {
            updateFChart();
        });

        setDate();

        setSelectBox();

        $('#_allCustomerTab').click(function() { initTab(); });
        $('#_emCustomerTab').click(function() { initTab(); });
        $('#_gmCustomerTab').click(function() { initTab(); });
        $('#_wmCustomerTab').click(function() { initTab(); });
        $('#_hmCustomerTab').click(function() { initTab();  });
        $('#_vcCustomerTab').click(function() { initTab();  });

        $("#startDateA").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#startDateB").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#startDateC").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#startDateD").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#startDateE").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#startDateF").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateA").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateB").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateC").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateD").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateE").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDateF").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});

        allStr = "<fmt:message key="aimir.all"/>";

        // datePicker
        var locDateFormat = "yymmdd";
        $("#startDateTime").datepicker({minDate:'+0m' ,showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat
        , onSelect: function(dateText, inst){modifyDateLocal(dateText, inst);} } );

        $('#startDateTime').click(function() {
            var dateId       = '#' + this.id;
            var dateHiddenId = '#' + this.id + 'Hidden';

            $(dateId).val("");
            $(dateHiddenId).val("");
        });
    });

    // 달력선택 시 호출
    function modifyDateLocal(setDate, inst){
        var dateId       = '#' + inst.id;
        var dateHiddenId = '#' + inst.id + 'Hidden';

        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate: setDate, supplierId: supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                });
    }

    function initTab() {
        $('#contractStatusTab').hide();
        $('#contractAdd').hide();

        setTimeout("lazyLoading()", 1000);
    }

    function lazyLoading(){
        if(serviceTypeTab == ""){
            customerSearchAll();
        }else if(serviceTypeTab == "EM"){
            customerSearch();
        }else if(serviceTypeTab == "GM"){
            customerSearchGM();
        }else if(serviceTypeTab == "WM"){
            customerSearchWM();
        }else if(serviceTypeTab == "HM"){
            customerSearchHM();
        }else if(serviceTypeTab == "VC"){
            customerSearchVC();
        }
    }

    function setSelectBox() {

        $.getJSON('${ctx}/gadget/system/customerMax.do?param=customerMaxSelectBox',

            function(json) {

                var serviceType = json.serviceType;
                var serviceTypeArr = Array();
                
                for (var i = 0; i < serviceType.length; i++) {
                    var obj = new Object();
                    obj.name=serviceType[i].descr;
                    obj.id=serviceType[i].id;
                    serviceTypeArr[i]=obj;
                };
                $('#serviceTypeA').loadSelect(serviceTypeArr);
                $("#serviceTypeA option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#serviceTypeA").val('');
                $("#serviceTypeA").selectbox();

                var operator = json.operator;
                var operatorArr = Array();
                
                for (var i = 0; i < operator.length; i++) {
                    var obj = new Object();
                    obj.name=operator[i].loginId;
                    obj.id=operator[i].id;
                    operatorArr[i]=obj;
                };
                $('#operatorA').loadSelect(operatorArr);
                $("#operatorA option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#operatorA").val('');
                $("#operatorA").selectbox();

                //$('#customerTypeA').loadSelect(json.customerType);
                //$("#customerTypeA option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                //$("#customerTypeA").val('');
                //$("#customerTypeA").selectbox();

                $('#tariffIndexB').loadSelect(json.tariffTypeEM);
                $("#tariffIndexB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexB").val('');
                $("#tariffIndexB").selectbox();

                var creditTypeData = json.creditType;
                var creditTypeArr = Array();
                
                for (var i = 0; i < creditTypeData.length; i++) {
                    var obj = new Object();
                    obj.name=creditTypeData[i].descr;
                    obj.id=creditTypeData[i].id;
                    creditTypeArr[i]=obj;
                };
                $('#creditTypeA').loadSelect(creditTypeArr);
                $("#creditTypeA option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeA").val('');
                $("#creditTypeA").selectbox();
                
                $('#creditTypeB').loadSelect(creditTypeArr);
                $("#creditTypeB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeB").val('');
                $("#creditTypeB").selectbox();

                var statusData = json.status;
                var statusArr = Array();
                
                for (var i = 0; i < statusData.length; i++) {
                    var obj = new Object();
                    obj.name=statusData[i].descr;
                    obj.id=statusData[i].id;
                    statusArr[i]=obj;
                };
                $('#statusB').loadSelect(statusArr);
                $("#statusB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusB").val('');
                $("#statusB").selectbox();

                $('#drB').loadSelect(json.dr);
                $("#drB option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#drB").val('');
                $("#drB").selectbox();

                $('#tariffIndexC').loadSelect(json.tariffTypeGM);
                $("#tariffIndexC option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexC").val('');
                $("#tariffIndexC").selectbox();

                $('#creditTypeC').loadSelect(creditTypeArr);
                $("#creditTypeC option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeC").val('');
                $("#creditTypeC").selectbox();

                $('#statusC').loadSelect(statusArr);
                $("#statusC option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusC").val('');
                $("#statusC").selectbox();

                $('#tariffIndexD').loadSelect(json.tariffTypeWM);
                $("#tariffIndexD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexD").val('');
                $("#tariffIndexD").selectbox();

                $('#creditTypeD').loadSelect(creditTypeArr);
                $("#creditTypeD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeD").val('');
                $("#creditTypeD").selectbox();

                $('#statusD').loadSelect(statusArr);
                $("#statusD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusD").val('');
                $("#statusD").selectbox();

                $('#tariffIndexD').loadSelect(json.tariffTypeWM);
                $("#tariffIndexD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexD").val('');
                $("#tariffIndexD").selectbox();

                $('#creditTypeD').loadSelect(creditTypeArr);
                $("#creditTypeD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeD").val('');
                $("#creditTypeD").selectbox();

                $('#statusD').loadSelect(statusArr);
                $("#statusD option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusD").val('');
                $("#statusD").selectbox();

                $('#tariffIndexE').loadSelect(json.tariffTypeWM);
                $("#tariffIndexE option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexE").val('');
                $("#tariffIndexE").selectbox();

                $('#creditTypeE').loadSelect(creditTypeArr);
                $("#creditTypeE option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeE").val('');
                $("#creditTypeE").selectbox();

                $('#statusE').loadSelect(statusArr);
                $("#statusE option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusE").val('');
                $("#statusE").selectbox();

                $('#tariffIndexF').loadSelect(json.tariffTypeWM);
                $("#tariffIndexF option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#tariffIndexF").val('');
                $("#tariffIndexF").selectbox();

                $('#creditTypeF').loadSelect(creditTypeArr);
                $("#creditTypeF option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#creditTypeF").val('');
                $("#creditTypeF").selectbox();

                $('#statusF').loadSelect(statusArr);
                $("#statusF option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                $("#statusF").val('');
                $("#statusF").selectbox();
        });

        locationTreeGoGo('treeDivA', 'locationAText', 'locationA');
        locationTreeGoGo('treeDivB', 'locationBText', 'locationB');
        locationTreeGoGo('treeDivC', 'locationCText', 'locationC');
        locationTreeGoGo('treeDivD', 'locationDText', 'locationD');
        locationTreeGoGo('treeDivE', 'locationEText', 'locationE');
        locationTreeGoGo('treeDivF', 'locationFText', 'locationF');

        locationTreeGoGo('treeDivU', 'locationUText', 'locationU');

    }

    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;

    function setDate() {

        if(("" + month).length == 1) month = "0" + month;

        calcDateSet(year, month, 0, 'A');
        calcDateSet(year, month, 0, 'B');
        calcDateSet(year, month, 0, 'C');

        $('#oneMonthA').click(function() { calcDateSet(year, month, 0, 'A'); });
        $('#threeMonthA').click(function() { calcDateSet(year, month, -3, 'A'); });
        $('#sixMonthA').click(function() { calcDateSet(year, month, -6, 'A'); });
        $('#oneYearA').click(function() { calcDateSet(year, month, -12, 'A'); });

        $('#oneMonthB').click(function() { calcDateSet(year, month, 0, 'B'); });
        $('#threeMonthB').click(function() { calcDateSet(year, month, -3, 'B'); });
        $('#sixMonthB').click(function() { calcDateSet(year, month, -6, 'B'); });
        $('#oneYearB').click(function() { calcDateSet(year, month, -12, 'B'); });

        $('#oneMonthC').click(function() { calcDateSet(year, month, 0, 'C'); });
        $('#threeMonthC').click(function() { calcDateSet(year, month, -3, 'C'); });
        $('#sixMonthC').click(function() { calcDateSet(year, month, -6, 'C'); });
        $('#oneYearC').click(function() { calcDateSet(year, month, -12, 'C'); });
    };

    function calcDateSet(currYear, currMonth, addMonth, dateType) {

        var startYear = currYear;
        var startMonth = currMonth*1 + addMonth*1;

        if(startMonth < 1) {
            startYear--;
            startMonth = 12 + startMonth;
        }

        if(("" + startMonth).length == 1) startMonth = "0" + startMonth;

        $('#startYear' + dateType).val(startYear);
        $('#startMonth' + dateType).val(startMonth);
        $('#endYear' + dateType).val(year);
        $('#endMonth' + dateType).val(month);
    };

    function customerDeleteResult(responseText, status) {
        //getFlexObject('CustomerList').customerSearch();
        customerExtTreeSearch();
    }

    //검색
    function customerSearchAll() {
        customerExtTreeSearch();
    }

    function customerSearch() {
        getElecCustomerList();
    }

    function customerSearchGM() {
        getGasCustomerList();
    }

    function customerSearchWM() {
        getWaterCustomerList();
    }

    function customerSearchHM() {
        getHeatCustomerList();
    }

    function customerSearchVC() {
        getVolumnCustomerList();
    }

    var getConditionArray = function() {
        var conditionArray = [];

        if (serviceTypeTab == '') {
            conditionArray[0] = $('#customerNoA').val();
            conditionArray[1] = $('#customerNameA').val();
            conditionArray[2] = $('#locationA').val();
            conditionArray[3] = '';
            conditionArray[4] = '';
            conditionArray[5] = '';
            conditionArray[6] = $('#mdsIdA').val();
            conditionArray[7] = '';
            conditionArray[8] = '';
            conditionArray[9] = '';//$('#sicIdsA').val();
            conditionArray[10] = $('#startDateA').val();
            conditionArray[11] = $('#endDateA').val();
            conditionArray[12] = '';//$('#addressA').val();
            conditionArray[13] = $('#serviceTypeA').val();
            conditionArray[15] = $('#contractNumberA').val();
            conditionArray[16] = $('#operatorA').val();
            conditionArray[17] = $('#phoneNumberA').val();
            conditionArray[18] = $('#barcodeA').val();
            conditionArray[19] = $('#oldmdsIdA').val();
            conditionArray[20] = $('#customer_type').val();
            
        } else if (serviceTypeTab == 'EM') {
            conditionArray[0] = $('#customerNoB').val();
            conditionArray[1] = $('#customerNameB').val();
            conditionArray[2] = $('#locationB').val();
            conditionArray[3] = $('#tariffIndexB').val();
            conditionArray[4] = $('#contractDemandB').val();
            conditionArray[5] = $('#creditTypeB').val();
            conditionArray[6] = $('#mdsIdB').val();
            conditionArray[7] = $('#statusB').val();
            conditionArray[8] = $('#drB').val();
            conditionArray[9] = '';//$('#sicIdsB').val();
            conditionArray[10] = $('#startDateB').val();
            conditionArray[11] = $('#endDateB').val();
            conditionArray[12] = '';                 // 주소
            conditionArray[13] = '';                 // 공급타입
            conditionArray[15] = $('#contractNumberB').val();
            conditionArray[16] = $('#gs1B').val();
        } else if (serviceTypeTab == 'GM') {
            conditionArray[0] = $('#customerNoC').val();
            conditionArray[1] = $('#customerNameC').val();
            conditionArray[2] = $('#locationC').val();
            conditionArray[3] = $('#tariffIndexC').val();
            conditionArray[4] = '';
            conditionArray[5] = $('#creditTypeC').val();
            conditionArray[6] = $('#mdsIdC').val();
            conditionArray[7] = $('#statusC').val();
            conditionArray[8] = '';
            conditionArray[9] = '';//$('#sicIdsC').val();
            conditionArray[10] = $('#startDateC').val();
            conditionArray[11] = $('#endDateC').val();
            conditionArray[12] = '';                 // 주소
            conditionArray[13] = '';                 // 공급타입
            conditionArray[15] = $('#contractNumberC').val();
        } else if (serviceTypeTab == 'WM') {
            conditionArray[0] = $('#customerNoD').val();
            conditionArray[1] = $('#customerNameD').val();
            conditionArray[2] = $('#locationD').val();
            conditionArray[3] = $('#tariffIndexD').val();
            conditionArray[4] = '';
            conditionArray[5] = $('#creditTypeD').val();
            conditionArray[6] = $('#mdsIdD').val();
            conditionArray[7] = $('#statusD').val();
            conditionArray[8] = '';
            conditionArray[9] = '';// $('#sicIdsD').val();
            conditionArray[10] = $('#startDateD').val();
            conditionArray[11] = $('#endDateD').val();
            conditionArray[12] = '';                 // 주소
            conditionArray[13] = '';                 // 공급타입
            conditionArray[15] = $('#contractNumberD').val();
            conditionArray[16] = $('#gs1D').val();
        } else if (serviceTypeTab == 'HM') {
            conditionArray[0] = $('#customerNoE').val();
            conditionArray[1] = $('#customerNameE').val();
            conditionArray[2] = $('#locationE').val();
            conditionArray[3] = $('#tariffIndexE').val();
            conditionArray[4] = '';
            conditionArray[5] = $('#creditTypeE').val();
            conditionArray[6] = $('#mdsIdE').val();
            conditionArray[7] = $('#statusE').val();
            conditionArray[8] = '';
            conditionArray[9] = '';//$('#sicIdsE').val();
            conditionArray[10] = $('#startDateE').val();
            conditionArray[11] = $('#endDateE').val();
            conditionArray[12] = '';                 // 주소
            conditionArray[13] = '';                 // 공급타입
            conditionArray[15] = $('#contractNumberE').val();
        } else if (serviceTypeTab == 'VC') {
            conditionArray[0] = $('#customerNoF').val();
            conditionArray[1] = $('#customerNameF').val();
            conditionArray[2] = $('#locationF').val();
            conditionArray[3] = $('#tariffIndexF').val();
            conditionArray[4] = '';
            conditionArray[5] = $('#creditTypeF').val();
            conditionArray[6] = $('#mdsIdF').val();
            conditionArray[7] = $('#statusF').val();
            conditionArray[8] = '';
            conditionArray[9] = '';//$('#sicIdsF').val();
            conditionArray[10] = $('#startDateF').val();
            conditionArray[11] = $('#endDateF').val();
            conditionArray[12] = '';                 // 주소
            conditionArray[13] = '';                 // 공급타입
            conditionArray[15] = $('#contractNumberF').val();
        }

        conditionArray[14] = serviceTypeTab;     // 현재 탭['','EM','GM','WM']
        return conditionArray;
    };

    var showContractStatusTab = function() {
        $('#contractStatusTab').show();
    };

    var hideContractStatusTab = function() {
        $('#contractStatusTab').hide();
    };

    function getContractChangeLogConditionArray() {
        var conditionArray = Array();
        conditionArray[0] = $('#startYearA').val() + $('#startMonthA').val() + '01000000';
        conditionArray[1] = $('#endYearA').val() + $('#endMonthA').val() + '31235959';
        conditionArray[2] = contractId;
        conditionArray[3] = supplierId;

        return conditionArray;
    }

    /** 2014.12.29 simhanger
        더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
    function getPrepaymentLogConditionArray() {
        var conditionArray = Array();
        conditionArray[0] = $('#startYearB').val() + $('#startMonthB').val() + '01000000';
        conditionArray[1] = $('#endYearB').val() + $('#endMonthB').val() + '31000000';
        conditionArray[2] = contractId;
        conditionArray[3] = supplierId;

        return conditionArray;
    }
    */

    function getContractBillingConditionArray() {

        var conditionArray = Array();
        conditionArray[0] = $('#startYearC').val() + $('#startMonthC').val() + '01000000';
        conditionArray[1] = $('#endYearC').val() + $('#endMonthC').val() + '31000000';
        conditionArray[2] = contractId;

        if (serviceTypeTab == "") {
            var serviceType = "";

            if (serviceTypeName == "Electricity") {
                serviceType = "EM";
            } else if (serviceTypeName == "Gas") {
                serviceType = "GM";
            } else if (serviceTypeName == "Water") {
                serviceType = "WM";
            } else if (serviceTypeName == "Heat") {
                serviceType = "HM";
            } else if (serviceTypeName == "VolumeCorrector") {
                serviceType = "VC";
            }

            conditionArray[3] = serviceType;
        } else {
            conditionArray[3] = serviceTypeTab;
        }

        return conditionArray;
    }

    //고객정보 클릭했을 때
    function receiveMsg(value) {
        if (value.id == undefined) {
            customerId = -1;
        } else {
            customerId = value.id;
        }

        serviceType = value.serviceType;
        serviceTypeName = value.serviceTypeName;

        contractId = value.contractId;
        selectContractId = value.contractId;
        tempServiceType = value.serviceType;

        //고객번호
        customerNo = value.customerNo;
        //계약 번호
        contractNumber = value.contractNumber;

        $("#customerNo").val(customerNo);
        $("#contractNumber").val(contractNumber);


        loadCustomerInfo();
    }
     var rightWidth = $('#pane-tab-Customer').width();

    //이전 고객 id
    var prevCustomerId= "";

    //customer 정보를 읽어온다.
    function loadCustomerInfo() {
        // editAuth 가 true 가 아니면 CUD 제한
        if (editAuth == "true") {
            $("#pane-Customer-Info-Button").show();
        } else {
            $("#pane-Customer-Info-Button").hide();
        }

        rightWidth = $('#pane-tab-Customer').width();

        //계약 번호가 널이거나 고객 id다를경우.
        if (contractId == '' || prevCustomerId != customerId) {
            $('#pane-tab-Customer').load('${ctx}/gadget/system/customerMax.do?param=customerDetailMax&customerId=' + customerId);
        }
        prevCustomerId = customerId;

         //contractId가 널일경우..
        if (contractId != undefined && contractId != "") {
            //하단에 계약정보를 불러 온다..
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=getContract',
                    {'contractId' : contractId
                    ,'supplierId' : supplierId
                    ,'customerNo' : $("#customerNo").val()},
                    function(data) {
                    //하단에 계약정보를 셋팅해주는 부분..
                    $('#contractInfoUpdate').hide();
                    $('#contractInfoDetail').show();

                    $('#serviceTypeName').val(data.contract.serviceTypeName);
                    $('#tariffIndexName').val(data.contract.tariffindexName);
                    $('#locationName').val(data.contract.locationName);
                    $('#locationId').val(data.contract.locationId);
                    $('#contractDemand').val(data.contract.contractDemand);
                    $('#statusName').val(data.contract.statusName);
                    $('#creditTypeName').val(data.contract.creditTypeName);
                    $('#meterMdsId').val(data.contract.mdsId);
                    $('#preMdsId').val(data.contract.preMdsId);
                    $('#meterGs1').val(data.contract.gs1);
                    $('#contractNumber').val(data.contract.contractNumber);
                    $('#serviceType2D').val(data.contract.serviceType2);
                    $('#threshold1').val(data.contract.threshold1);
                    $('#threshold2').val(data.contract.threshold2);
                    $('#threshold3').val(data.contract.threshold3);
                    
                    $('#operator').val(data.contract.operator);
                    $('#contractStartDate').val(data.contract.contractDate);
					
                    contractNumber = data.contract.contractNumber;
                    $('#contractNumberU').val(contractNumber);

                    $('#barcode').val(data.contract.barcode);
                    $('#meterMdsId').val(data.contract.mdsId);
                    $('#meterMdsIdU_id').val(data.contract.meterId);

                    var creditTypeCode= data.contract.creditTypeCode;

                    //Payment Stat.가 선불 타입인 경우..
                    if (creditTypeCode == '2.2.1' || creditTypeCode == '2.2.2') {
                        $('#prepaymentTr01_1').show();
                        $('#prepaymentTr01_2').show();
                        $('#prepaymentTr01_3').show();
                        $('#prepaymentTr01_4').show();
                        $('#prepaymentTr01_5').show();
                        $('#prepaymentTr01_6').show();
                        
                        //$('#prepaymentTr02').show();
                        $('#prepaymentTr03').show();
                        //$('#prepaymentTr04').show();
                        //$('#prepaymentTr05').show();

                        $('#currentArrears').val(data.contract.currentArrears);
                        $('#currentArrears2').val(data.contract.currentArrears2);

                        //arrearsPaymentCount값이 null인 경우는 분할납부를 완료한 경우만 가능.
                        if(isPartpayment == 'true' && data.contract.arrearsPaymentCount != null && data.contract.arrearsPaymentCount >= 0 &&
                            data.contract.arrearsContractCount != null && data.contract.arrearsContractCount != '') {
                        	$('#arrearsContractCountD').val(data.contract.arrearsPaymentCount + " / " +  data.contract.arrearsContractCount);
                        } else {
                            $('#arrearsContractCountD').val("-");
                        }

                        var chargeAvailable = data.contract.chargeAvailable;
                        
                        if (chargeAvailable != null) {
                            if (chargeAvailable == true) {
                                $('#chargeAvailable').val("<fmt:message key="aimir.allow"/>");
                            } else {
                                $('#chargeAvailable').val("<fmt:message key="aimir.reject"/>");
                            }
                        } else {
                            $('#chargeAvailable').val("");
                        }

                        var debtInfoList = data.debtInfo;
                        var size = debtInfoList.length;
                        var debtInfoArr = Array();
                        for (var i = 0; i < size; i++) {
                            var obj = new Object();
                            obj.name=debtInfoList[i].debtType;
                            obj.id=debtInfoList[i].debtRef;
                            debtInfoArr[i]=obj;
                        };
                        
                        if(size == 0) {
                        	$('#debtType').pureSelect(debtInfoArr);
                        	$("#debtType").selectbox();
                        	$('#debtAmount').val("-");
                        	$('#debtContractCnt').val("-");
                        	$('#debtType').attr("readOnly","readOnly");
                        	$('#debtContractCnt').attr("readOnly","readOnly");
                        	$('#debtAmountTitle').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",""));
                        	$('#debtContractCntTitle').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",""));
                        } else {
                        	$('#debtType').pureSelect(debtInfoArr);

                            $("#debtType option[value=" + debtInfoList[0].debtType + "]").attr("selected", "true");
                            $("#debtType").selectbox();
                            
                            $('#debtAmountTitle').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfoList[0].debtType)); 
                            $('#debtAmount').val(debtInfoList[0].debtAmount);

                            $('#debtContractCntTitle').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfoList[0].DEBTTYPE));
                            if(isPartpayment == 'true' && debtInfoList[0].debtPaymentCount != null && debtInfoList[0].debtPaymentCount >= 0 &&
                            		debtInfoList[0].debtContractCount != null && debtInfoList[0].debtContractCount != '') {
                            	$('#debtContractCnt').val(debtInfoList[0].debtPaymentCount + "/" + debtInfoList[0].debtContractCount);
                            } else {
                            	$('#debtContractCnt').val("-");
                            }
                            
                            $("#debtType").change(function(value) {
                                changeDebt();
                            });

                            $("#debtType").change();
                        }
                        
                        //fetch Payment_Stat from S.S.
                        $.ajax({
                            type:"POST",
                            data:{contractId:contractId , serviceType:tempServiceType, customerNo:$("#customerNo").val()},
                            dataType:"json",
                            url:"${ctx}/gadget/system/customerMax.do?param=contractInfoUpdateForm",
                            success:function(data, status)
                            {
                                if (data.creditStatus != null && data.creditStatus.descr != null) {
                                    $("#creditStatus").val(  data.creditStatus.descr);
                                } else {
                                    $("#creditStatus").val("");
                                }
                                if (data.contract.prepaymentThreshold != null && data.contract.prepaymentThreshold != "" 
                                        && data.contract.prepaymentThreshold != "null" && data.contract.prepaymentThreshold != '"null"') {
                                    $("#prepaymentThreshold2").val(data.contract.prepaymentThreshold);
                                }
                            },
                            error:function(request, status) {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"creditStatusName fetch failed");
                            }
                        });

                        //버튼 위치 조정
                        //$('.contractInfoUpdateForm').css('bottom', '50');
                    } else {
                        $('#prepaymentTr01_1').hide();
                        $('#prepaymentTr01_2').hide();
                        $('#prepaymentTr01_3').hide();
                        $('#prepaymentTr01_4').hide();
                        $('#prepaymentTr01_5').hide();
                        $('#prepaymentTr01_6').hide();
                        
                        $('#prepaymentTr02').hide();
                        $('#prepaymentTr03').hide();
                        $('#prepaymentTr04').hide();
                        $('#prepaymentTr05').hide();

                        $("#creditStatus").val("");
                        $("#prepaymentThreshold2").val("");
                        $('#currentArrears').val("");
                        $('#chargeAvailable').val("");
                        $('.contractInfoUpdateForm').css('bottom', '-22');
                    }
             });
        }

        if(contractId != undefined && contractId != "") {
            $('#contractStatusTab').show();
            $('#contractAdd').hide();
            if ( $('#fcChartDiv').is(':visible')) {
                updateFChart();
            }
            bottomContractStatusTab();
        } else {
            $('#contractStatusTab').hide();
            $('#contractAdd').hide();
        }
    }

    /* function setContractCount(value) {
        $('#contractCount').html(value);
    }; */

    //폼값 리셋
    function reset() {
        $("#customerNo").val('');
        $("#name").val('');
        $("#address").val('');
        $("#address1").val('');
        $("#address2").val('');
        $("#email_1").val('');
        $("#email_2").val('');
        $("#emailYn_2").filter("input[value=0]").attr("checked", "checked");
        $("#mobileNo_1").val('');
        $("#mobileNo_2").val('');
        $("#mobileNo_3").val('');
        $("#smsYn_2").filter("input[value=0]").attr("checked", "checked");
        $("#telNo_1").val('');
        $("#telNo_2").val('');
        $("#telNo_3").val('');
        $("#checkValue").hide();
    }


    function getFmtMessage(){
        var fmtMessage = new Array();

        //가스,수도 (전기 그리드포함) 그리드
        fmtMessage[0] = "<fmt:message key="aimir.contractNumber"/>";//계약번호
        fmtMessage[1] = "<fmt:message key="aimir.customername"/>";//고객명
        fmtMessage[2] = "<fmt:message key="aimir.location.supplier"/>";//공급지역
        fmtMessage[3] = "<fmt:message key="aimir.contract.tariff.type"/>";//계약종별
        fmtMessage[4] = "<fmt:message key="aimir.paymenttype"/>";//지불타입
        fmtMessage[5] = "<fmt:message key="aimir.meterid"/>";//미터번호
        fmtMessage[6] = "<fmt:message key="aimir.supplystatus"/>";//공급상태
        fmtMessage[7] = "<fmt:message key="aimir.industryClassification"/>";//산업분류
        fmtMessage[8] = "E-Mail";//이메일
        fmtMessage[9] = "<fmt:message key="aimir.landlinePhone"/>";//유선전화
        fmtMessage[10] = "<fmt:message key="aimir.celluarphone"/>";//핸드폰번호

        //전기 그리드 추가 내용
        fmtMessage[12] = "<fmt:message key="aimir.contract.demand"/>";//계약전력
        fmtMessage[13] = "<fmt:message key="aimir.customer.dr"/>";//DR고객

        //하단 - 계약 상세정보 그리드
        fmtMessage[14] = "<fmt:message key="aimir.number"/>";//번호
        fmtMessage[15] = "<fmt:message key="aimir.updatedDate"/>";//변경일
        fmtMessage[16] = "<fmt:message key="aimir.attribute"/>";//속성
        fmtMessage[17] = "<fmt:message key="aimir.beforeChange"/>";//변경전
        fmtMessage[18] = "<fmt:message key="aimir.afterChange"/>";//변경후

        //전체 탭 그리드
        fmtMessage[19] = "<fmt:message key="aimir.customername"/>";//고객명
        fmtMessage[20] = "<fmt:message key="aimir.location.supplier"/>";//공급지역
        fmtMessage[21] = "<fmt:message key="aimir.address"/>";//주소
        fmtMessage[22] = "<fmt:message key="aimir.supply.type"/>";//공급타입
        fmtMessage[23] = "<fmt:message key="aimir.meterid"/>";//미터번호
        fmtMessage[24] = "<fmt:message key="aimir.sic"/>";//산업분류코드

        //fmtMessage[25] = "<fmt:message key="aimir.PrepaidDate"/>" + "(" + "<fmt:message key="aimir.chargeTime"/>" + ")";// 선불일자(충전시각)
        //fmtMessage[26] = "<fmt:message key="aimir.keyno"/>";//키넘버
        //fmtMessage[27] = "<fmt:message key="aimir.chargeAmount"/>";//충전금액[원]
        //fmtMessage[28] = "<fmt:message key="aimir.content"/>";//비고
        // prepayment log
        fmtMessage[25] = "<fmt:message key="aimir.hems.prepayment.chargedate"/>";// 충전일자
        fmtMessage[26] = "<fmt:message key="aimir.chargeAmount"/>[<fmt:message key="aimir.price.unit"/>]";//충전금액[원]
        fmtMessage[27] = "<fmt:message key="aimir.hems.prepayment.balanceaftercharged"/>[<fmt:message key="aimir.price.unit"/>]";//충전 후 잔액[원]
        fmtMessage[28] = "<fmt:message key="aimir.hems.prepayment.transactionNum"/>";//거래번호
        
        fmtMessage[29] = "<fmt:message key="aimir.shipment.gs1"/>";//gs1 = Meter SN

        return fmtMessage;
    }

    function getFmtMessageModifyMeter() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.meter.select.one"/>";         // 미터는 하나만 선택이 가능합니다.
        return fmtMessage;
    }

    var infoDelayInterval;
    // 계약 - 미터 조회시. Flex의 조회가 완료 될때까지. interval을 이용하여, 계속 조회함
    function getContractModifyMeterList() {
        contractModifyMeterFlex = getFlexObject('contractModifyMeter');

        // flex가 로딩 될때 까지 대기함
        infoDelayInterval = setInterval("getContractModifyMeterListDelay()",1000);
    }

    function getContractModifyMeterListDelay() {

        if (contractModifyMeterFlex != "") {
            clearInterval(infoDelayInterval);
            var mdsId = $('#meterMdsId').val();
            contractModifyMeterFlex.requestSend(mdsId);
        }
    }

    function contractModifyMeterGrid() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.meterid"/>";
        fmtMessage[1] = "<fmt:message key="aimir.meter.address"/>";

        var dataFild = new Array();
        dataFild[0] = "MDSID";
        dataFild[1] = "ADDRESS";

        var gridAlign = new Array();
        gridAlign[0] = "left";
        gridAlign[1] = "left";

        var gridWidth = new Array();
        gridWidth[0] = "800";
        gridWidth[1] = "1000";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;

        return dataGrid;
    }

    /*function initFChartData() {
        fcChartDataXml = "<chart "
                + "bgAlpha='0' "
                + "chartLeftMargin='0' "
                + "chartRightMargin='0' "
                + "chartTopMargin='10' "
                + "chartBottomMargin='0' "
                + "showValues='0' "
                + "showLabels='1' "
                + "showLegend='0' "
                + "labelDisplay = 'AUTO' "
                + fChartStyle_Common
                + fChartStyle_Font
                + fChartStyle_MSColumn3D_nobg
                + ">";
         var categories = "<categories><category label=' ' /></categories>";
         var dataset = "<dataset seriesName='<fmt:message key="aimir.usage"/>'><set value='' /></dataset>";

          fcChartDataXml += categories + dataset + "</chart>";

          fcChartRender();
    }*/

    function updateFChart() {
        var startDate = $('#startYearC').val() + $('#startMonthC').val() + '01000000';
        var endDate = $('#endYearC').val() + $('#endMonthC').val() + '31000000';
        var serviceType = "";

        if (serviceTypeTab == "") {
            if (serviceTypeName == "Electricity") {
                serviceType = "EM";
            } else if (serviceTypeName == "Gas") {
                serviceType = "GM";
            } else if (serviceTypeName == "Water") {
                serviceType = "WM";
            } else if (serviceTypeName == "Heat") {
                serviceType = "HM";
            } else if (serviceTypeName == "VolumeCorrector") {
                serviceType = "VC";
            }
        } else {
            serviceType = serviceTypeTab;
        }

        $.getJSON('${ctx}/gadget/system/customerMax.do?param=contractBilling'
                ,{startDate:startDate,
                    endDate:endDate,
                    contractId:contractId,
                    serviceTypeTab:serviceType}
                ,function(json) {
                     var arr = json.chartDatas;
                     fcChartDataXml = "<chart "
                        + "bgAlpha='0' "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='10' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='0' "
                        + "labelDisplay = 'AUTO' "
                        + "PYAxisName='<fmt:message key="aimir.usage"/>' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg
                        + ">";
                     var categories = "<categories>";
                     var dataset = "<dataset seriesName='<fmt:message key="aimir.usage"/>'>";

                     if (arr != null && arr.length > 0) {
                         for (var i = 0; i < arr.length; i++) {
                             var tempString = arr[i].DATE + "";
                             categories += "<category label='"+tempString.substr(0, 4)+". "+Number(tempString.substr(4, 2))+"' />";//date 스크립트 에러
                             dataset += "<set value='"+arr[i].TOTAL+"' />";
                         }
                     } else {
                         categories += "<category label=' ' />";
                         dataset += "<set value='' />";
                     }
                     categories += "</categories>";
                     dataset += "</dataset>";

                     fcChartDataXml += categories + dataset + "</chart>";
                     fcChartRender();
                }
        );
    }

    function fcChartRender() {
    	
        var width = $('#fcChartDiv').width();
        $('#fcChartDiv').show();
        
        fcChart = new FusionCharts({
    		type: 'MSColumn3D',
    		renderAt : 'fcChartDiv',
    		width : width,
    		height : '170',
    		dataSource : fcChartDataXml
    	});
    	fcChart.render();
    }

    var changeLogStore;
    var changeLogGridOn = false;
    var changeLogGrid;
    var changeLogColModel;
    function getContractChangeLog() {
        var pageSize = 5;
        var width = $("#changeLog").width();
        var conditionArray = getContractChangeLogConditionArray();
        var fmtMessage = getFmtMessage();

        changeLogStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/system/getContractChangeLogList.do",
            baseParams:{
                startDate: conditionArray[0],
                endDate: conditionArray[1],
                contractId: conditionArray[2],
                supplierId: conditionArray[3]
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ["startDatetime", "changeField", "beforeValue", "afterValue"],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
        });

        changeLogColModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[14], width:50,
                         renderer: function(value, metaData, record, index) {
                                  //전체 글수 - (시작글수+글의 줄번호)
                                 return (parseInt(changeLogStore.lastOptions.params.start) + parseInt(index) + 1);
                         }
                }
                ,{header: fmtMessage[15], dataIndex: 'startDatetime', align:'center'}
                ,{header: fmtMessage[16], dataIndex: 'changeField'}
                ,{header: fmtMessage[17], dataIndex: 'beforeValue'}
                ,{header: fmtMessage[18], dataIndex: 'afterValue', width: ((width-50)/4)-6}
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-50)/4)
                ,renderer: addTooltip
            }
        });

        if(changeLogGridOn == false) {
            changeLogGrid = new Ext.grid.GridPanel({
                  store: changeLogStore,
                  colModel : changeLogColModel,
                  sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                  autoScroll:false,
                  width: width,
                  height: 173,
                  stripeRows : true,
                  columnLines: true,
                  loadMask:{
                      msg: 'loading...'
                  },
                  renderTo: 'changeLog',
                  viewConfig: {
                      enableRowBody:true,
                      showPreview:true,
                      emptyText: 'No data to display'
                  } ,
                  // paging bar on the bottom
                  bbar: new Ext.PagingToolbar({
                      pageSize: pageSize,
                      store: changeLogStore,
                      displayInfo: true,
                      displayMsg: ' {0} - {1} / {2}'
                  })
              });
            changeLogGridOn = true;
        } else {
            changeLogGrid.setWidth(width);
            var bottomToolbar = changeLogGrid.getBottomToolbar();
            changeLogGrid.reconfigure(changeLogStore, changeLogColModel);
            bottomToolbar.bindStore(changeLogStore);
        }
    };

    var selectedContractNumber;
    var selectedServiceType;

    /** 2014.12.29 simhanger
    더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.


    var chargeHistoryStore;
    var chargeHistoryGridOn = false;
    var chargeHistoryGrid;
    var chargeHistoryColModel;


    function getPrepaymentLog() {
        var pageSize = 5;
        var width = $("#prepaymentLog").width();
        var fmtMessage = getFmtMessage();

        chargeHistoryStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/system/getPrepaymentLog.do",
            baseParams:{supplierId:supplierId,
                        contractId:contractId,
                        startDate:$('#startYearB').val() + $('#startMonthB').val(),
                        endDate:$('#endYearB').val() + $('#endMonthB').val()
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: ["lastTokenDate", "lastTokenDateView", "balance", "balanceView", "chargedCredit", "chargedCreditView", "currentCredit",
                     "consumption", "consumptionView", "keyNum", "payment", "contractId", "minDate", "maxDate", "searchMinDate", "searchMaxDate", "authCode", "municipalityCode", "lastTokenId"],
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
                {header: fmtMessage[14], width:50,
                         renderer: function(value, metaData, record, index) {
                                  //전체 글수 - (시작글수+글의 줄번호)
                                  // return suspectedStore.getTotalCount() - (parseInt(suspectedStore.lastOptions.params.start)+parseInt(index));
                                  //return chargeHistoryStore.getTotalCount() - (parseInt(chargeHistoryStore.lastOptions.params.start)+parseInt(index));
                                 return (parseInt(chargeHistoryStore.lastOptions.params.start) + parseInt(index) + 1);
                         }
                }
                ,{header: fmtMessage[25], dataIndex: 'lastTokenDateView', align:'center'}
                ,{header: fmtMessage[26], dataIndex: 'chargedCreditView', align:'right'}
                ,{header: fmtMessage[27], dataIndex: 'balanceView', align:'right'}
                ,{header: fmtMessage[28], dataIndex: 'lastTokenId', align:'center', width: ((width-50)/4)-6}
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-50)/4)
                ,renderer: addTooltip
            }
        });

        if(chargeHistoryGridOn == false) {
            chargeHistoryGrid = new Ext.grid.GridPanel({
                  store: chargeHistoryStore,
                  colModel : chargeHistoryColModel,
                  sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                  autoScroll:false,
                  width: width,
                  height: 173,
                  stripeRows : true,
                  columnLines: true,
                  loadMask:{
                      msg: 'loading...'
                  },
                  renderTo: 'prepaymentLog',
                  viewConfig: {
                      //forceFit:true,
                      enableRowBody:true,
                      showPreview:true,
                      emptyText: 'No data to display'
                  } ,
                  // paging bar on the bottom
                  bbar: new Ext.PagingToolbar({
                      pageSize: pageSize,
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
    }; */

    var customerTreeGridOn = false;
    var treeData;
    var pagingData;
    var selectedNodeId;
    var selectedNodeCode;
    var selectedNodePath;
    var selectedParentNodeCode;
    var selectedParentNodeId;
    var customerTreeGridOn = false;
    var tree;
    var customerTreeRootNode;

    var total = 0;

    $(window).resize(function() {
        makeCustomerTree();

        if ( $('#elecCustomerDiv').is(':visible')) {
            getElecCustomerList();
        }
        if ( $('#gasCustomerDiv').is(':visible')) {
            getGasCustomerList();
        }
        if ( $('#waterCustomerDiv').is(':visible')) {
            getWaterCustomerList();
        }
        if ( $('#heatCustomerDiv').is(':visible')) {
            getHeatCustomerList();
        }
        if ( $('#volCustomerDiv').is(':visible')) {
            getVolumnCustomerList();
        }

        if ( $('#changeLog').is(':visible')) {
            getContractChangeLog();
        }

        /** 2014.12.29 simhanger
            더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
        if ( $('#prepaymentLog').is(':visible')) {
            getPrepaymentLog();
        }
        */

        if ( $('#fcChartDiv').is(':visible')) {
            fcChartRender();
        }
    });


    var customerNo="";
    var contractNumber= "";

    //트리그리드 JsonStore fetch func
    function customerExtTreeSearch() {    	
        
    	if(window.opener != null && window.opener.customerObj != null){
        	$('#customerNoA').val(window.opener.customerObj.customerId);
        }
    	
        treeData = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/system/customerMax.do?param=customerExtList",
            baseParams:{
                contractNumber: $('#contractNumberA').val(),
                customerNo: $('#customerNoA').val(),
                customerName: $('#customerNameA').val(),
                gs1: $('#gs1').val(),
                location: $('#locationA').val(),
                tariffIndex: '',
                contractDemand: '',
                creditType: '',
                mdsId: $('#mdsIdA').val(),
                status: '',
                dr: '',
                customerType: '',//$('#sicIdsA').val(),
                startDate: $('#startDateA').val(),
                endDate: $('#endDateA').val(),
                address: '',//$('#addressA').val(),
                serviceType: $('#serviceTypeA').val(),
                serviceTypeTab: serviceTypeTab,
                operatorId: $('#operatorA').val(),
                oldMdsId: $('#oldmdsIdA').val(),
                phoneNumber : $('#phoneNumberA').val(),
            	barcode : $('#barcodeA').val(),
            	tariffType : $('#customer_type').val().toString()
            },
            reader: new Ext.data.JsonReader({
                root: 'root',
                fields: ["customerName", "location", "customerNo", "serviceType", "chargedCreditView", "expanded", "gs1",
                         "iconCls", "serviceTypeName", "meterId", "customerId", "address", "contractId", "contractNumber", "sicName"]
            }),
            totalProperty: 'total',
            root:'root',
            fields: [/* "customerName", */ "location", "customerNo", "serviceType", "chargedCreditView", "expanded", "gs1",
                     "iconCls", "serviceTypeName", "meterId", "customerId", "address", "contractId", "contractNumber", "sicName"],

            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) -1
                         });
                },
                load: function(store, record, options){
                    /* vendor에서 링크로 접속 여부 확인 */
                    if(window.opener != null && window.opener.customerObj != null){
                    	customerId = record[0].id;
                    	loadCustomerInfo();
                    }else{
	                    //setContractCount(treeData.reader.jsonData.totalCustomer);
	                    $("#contractCount").html(treeData.reader.jsonData.totalContractCount);
                    }
                    makeCustomerTree();
                }
            }
        });
    }

    /*
    @desc 트리 그리드 panel make func

    */
    function makeCustomerTree() {
        var width = $("#treeGridDiv").width();
        var scrollWidth = 22;
        var tgWidth = width - scrollWidth;
        var customerTreeColModel = [
        	{header: "<fmt:message key='aimir.customerid'/>", dataIndex: 'customerNo', width: tgWidth/10,
                 tpl: new Ext.XTemplate('{customerNo:this.viewToolTip}', {
                     viewToolTip: addTreeTooltip
                 })
             }
        	,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber', width: tgWidth/10 * 2,
                tpl: new Ext.XTemplate('{contractNumber:this.viewToolTip}', {
                    viewToolTip: addTreeTooltip
                })
            } 
             ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName', width: tgWidth/10 * 2,
                 tpl: new Ext.XTemplate('{customerName:this.viewToolTip}', {
                     viewToolTip: addTreeTooltip
                 })
             }             
            ,{header: "<fmt:message key='aimir.address'/>", dataIndex: 'address', width: tgWidth/10 * 4,
                tpl: new Ext.XTemplate('{address:this.viewToolTip}', {
                    viewToolTip: addTreeTooltip
                })
            }
            /* ,{header: "<fmt:message key='aimir.supply.type'/>", dataIndex: 'serviceTypeName', width: tgWidth/10}
            ,{header: "<fmt:message key='aimir.meterid'/>", dataIndex: 'meterId', width: tgWidth/10,
                tpl: new Ext.XTemplate('{meterId:this.viewToolTip}', {
                    viewToolTip: addTreeTooltip
                })
            } */
            ,{header: "<fmt:message key='aimir.shipment.gs1'/>", dataIndex: 'gs1', width: tgWidth/10,
                tpl: new Ext.XTemplate('{gs1:this.viewToolTip}', {
                    viewToolTip: addTreeTooltip
                })
            }
        ];

        customerTreeRootNode = new Ext.tree.AsyncTreeNode({
            text: 'root',
            id: 'root',
            allowChildren: true,
            draggable:true,
            expended:false,
            children: treeData.reader.jsonData.root
        });

        //페이징 툴바 셋팅
        var pagingToolbar = new Ext.PagingToolbar({
            store: treeData,
            pageSize: 10,
            displayInfo: true,
            displayMsg: ' {0} - {1} / {2}'
        });

        if (!customerTreeGridOn) {
            var treeLoader = new Ext.tree.TreeLoader({
                url : "${ctx}/gadget/system/customerMax.do?param=customerTreeChildList",
                baseParams : {
                    contractNumber : $('#contractNumberA').val(),
                    location : $('#locationA').val(),
                    tariffIndex : '',
                    contractDemand : '',
                    creditType : '',
                    mdsId : $('#mdsIdA').val(),
                    status : '',
                    dr : '',
                    gs1 :  $('#gs1').val(),
                    customerType : '',//$('#sicIdsA').val(),
                    startDate : $('#startDateA').val(),
                    endDate : $('#endDateA').val(),
                    serviceType : $('#serviceTypeA').val(),
                    serviceTypeTab : serviceTypeTab,
                    operatorId : $('#operatorA').val()
                }
            });

            treeLoader.on("beforeload", function(treeLoader, node) {
                treeLoader.baseParams.contractNumber = $("#contractNumberA").val();
                treeLoader.baseParams.location = $("#locationA").val();
                treeLoader.baseParams.mdsId = $("#mdsIdA").val();
                treeLoader.baseParams.gs1 = $("#gs1").val();
                treeLoader.baseParams.customerType = '';//$("#sicIdsA").val();
                treeLoader.baseParams.serviceType = $("#serviceTypeA").val();
                treeLoader.baseParams.serviceTypeTab = serviceTypeTab;
                treeLoader.baseParams.operatorId = $("#operatorA").val();
                treeLoader.baseParams.startDate = $("#startDateA").val();
                treeLoader.baseParams.endDate = $("#endDateA").val();
            });

            tree = new Ext.ux.tree.TreeGrid({
                width : width,
                height : 450,
                renderTo : "treeGridDiv",
                enableDD : true,
                columns : customerTreeColModel,
                loader : treeLoader,
                root : customerTreeRootNode,
                bbar : pagingToolbar,
                viewConfig: {
                    //forceFit : true,
                    //enableRowBody : true,
                    emptyText : 'No data to display'
                }
            });

            tree.on("click", selectCustomerTreeNode);
            customerTreeGridOn = true;
        } else {
            tree.setWidth(width);
            tree.setRootNode(customerTreeRootNode);
            var bottomToolbar = tree.getBottomToolbar();
            bottomToolbar.bindStore(treeData);
            tree.render();
        }
    }

    var childrenYn;
    var contractCnt;

    // 클릭 시 선택한 Node 의 정보를 setting
    function selectCustomerTreeNode(node, e) {

    	clearDebtSave();

        if (node.expanded || node.attributes.contractId == '') {
            childrenYn = 'N';
            contractCnt = node.childNodes.length;
        } else {
            childrenYn = 'Y';

        }
        var rtnObj = new Object();
        rtnObj.id = node.attributes.customerId;
        rtnObj.serviceType = node.attributes.serviceType;
        rtnObj.contractId = node.attributes.contractId;

        rtnObj.serviceTypeName = node.attributes.serviceTypeName;

        //######################
        rtnObj.customerNo = node.attributes.customerNo;
        rtnObj.contractNumber= node.attributes.contractNumber;

        receiveMsg(rtnObj);
    }


    var meterGridOn = false;
    var meterGrid;
    var meterData;
    var mdsIdVar= "";

    function meterSearch() {
        if ($('#meterMdsIdU').val() == '-') {
            mdsIdVar = "";
        } else {
            mdsIdVar = $('#meterMdsIdU').val();
        }

        meterData = new Ext.data.JsonStore({
            autoLoad: true,
            url: '${ctx}/gadget/device/getMeterListForContractExtJs.do',
            baseParams:{
                mdsId: mdsIdVar,
                query: ''
            },
            root:'gridData',
            fields: ["MDSID", "ADDRESS"],
            listeners: {
                load: function(store, record, options){
                    makeMeterGrid();
                }
            }
        });
    }

    function makeMeterGrid() {
        var width = $("#meterGridList").width();
        $("#meterGridList").height($("#contractInfoUpdate").height());
        var height = $("#contractInfoUpdate").height();

        var dtsColModel = new Ext.grid.ColumnModel({
            columns: [
                        {header: "<span style='text-align:center;font-weight: bold;'><fmt:message key='aimir.meterid'/></span>", dataIndex: 'MDSID', align:'left', width: width/2}
                        ,{header: "<span style='text-align:center;font-weight: bold;'><fmt:message key='aimir.address'/></span>", dataIndex: 'ADDRESS', align:'left', width: width}
                     ],
                     defaults: {
                         sortable: true
                         ,menuDisabled: false
                         ,width: 150
                     }
        });
        var meterSearchBar =
                new Ext.Toolbar({
                height: height/5
                ,displayInfo:true
        });
        if (!meterGridOn) {
            meterGrid = new Ext.grid.GridPanel({
                 layout:'fit'
                ,border:true
                ,renderTo: 'meterGridList'
                ,stateful:false
                ,height: height
                ,width: width
                ,store: meterData
                ,colModel: dtsColModel
                ,bbar: meterSearchBar
                ,viewConfig: {
                     showPreview:true,
                     emptyText: 'No data to display'
                }
                ,plugins: new Ext.ux.grid.Search({
                    readonlyIndexes:['note']
                    ,disableIndexes:['pctChange']
                    ,minChars:3
                })
            });

            meterGrid.on("rowdblclick", selectMeterGrid);
            meterGridOn = true;
        } else {
            meterGrid.setWidth(width);
            meterGrid.setHeight(height);
            meterGrid.reconfigure(meterData, dtsColModel);
        }
    }

    function selectMeterGrid(t,rowIndex,e) {
        $("#meterMdsIdU").val(meterGrid.getStore().getAt(rowIndex).data.MDSID);
    }

    $(document).ready(function() {
        Ext.QuickTips.init();
        // Ext 3.4 IE9 Detect Bug Fixed

        if (Ext.isIE6 && /msie 9/.test(navigator.userAgent.toLowerCase()))
        {
            Ext.isIE6 = Ext.isIE = false;
            Ext.isChrome = Ext.isIE9 = true;
        }

        hide();

        //분할납부 기능을 사용하지 않을 때 분할납후 횟수를 입력할 수 없도록 함
        if(isPartpayment == 'false' || isPartpayment == false) {
        	$('#arrearsContractCountU').replaceWith($('#arrearsContractCountU').clone().attr('type','hidden'));
        	$('#debtContractCntU').replaceWith($('#debtContractCntU').clone().attr('type','hidden'));
        }
        
        $("#updContractNoCheck").bind("click", function() {
            var contractNumber = $("#contractNumberU").val();

            if ( contractNumber == "" || contractNumber.length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.enterContractNo"/>");    // 계약번호를 입력해 주세요
                return;
            }
            $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCheckContractNumber', { contractNumber:contractNumber },
                function(json) {
                    var result = json.result;
                    var checkYN = "false";
                    updExistContract = false;
                    if (result != null) {
                        if (result.exist == "true") {
                            if (result.linked == "true") {
                                if (confirm("<fmt:message key="aimir.contract.msg.existlinkedcontract"/>\n\n<fmt:message key="aimir.contract.currentlinkedcustomer"/> : " + result.customerName)) {
                                    // 해당 contract 정보를 입력
                                    inputUpdContractInfo(result);
                                    updExistContract = true;
                                    checkYN = "true";
                                    $("#updCheckValue").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                                    $("#updCheckValue").show();
                                } else {
                                    checkYN = "false";
                                    contractId = "";
                                    $("#updCheckValue").html("<ul><li class='reject2'><fmt:message key='aimir.msg.invalidcontractno'/></li></ul>");
                                    $("#updCheckValue").show();
                                    //$("#contractNumber2").val('');
                                    $("#contractNumberU").select();
                                    $("#contractNumberU").focus();
                                }
                            } else {
                                if (confirm("<fmt:message key="aimir.contract.msg.existcontract"/>")) {
                                    // 해당 contract 정보를 입력
                                    inputUpdContractInfo(result);
                                    updExistContract = true;
                                    checkYN = "true";
                                    $("#updCheckValue").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                                    $("#updCheckValue").show();
                                } else {
                                    checkYN = "false";
                                    contractId = "";
                                    $("#updCheckValue").html("<ul><li class='reject2'><fmt:message key='aimir.msg.invalidcontractno'/></li></ul>");
                                    $("#updCheckValue").show();
                                    //$("#contractNumber2").val('');
                                    $("#contractNumberU").select();
                                    $("#contractNumberU").focus();
                                }
                            }
                        } else {
                            checkYN = "true";
                            contractId = "";
                            $("#updCheckValue").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                            $("#updCheckValue").show();
                        }
                    } else {
                        checkYN = "false";
                        contractId = "";
                        $("#updCheckValue").html("<ul><li class='reject2'><fmt:message key='aimir.erroroccured'/></li></ul>");
                        $("#updCheckValue").show();
                        $("#contractNumberU").val('');
                        $("#contractNumberU").focus();
                    }
                    $("#updCheckYN").val(checkYN);
                }
            );
        });
    });

    var updExistContract = false;

    function inputUpdContractInfo(info) {
        //$("#contractId").val(info.contractId);
        contractId = info.contractId;
        if (info.serviceType == "") {
            $("#serviceTypeCodeU option:eq(0)").attr("selected", "true");
        } else {
            $("#serviceTypeCodeU option[value=" + info.serviceType + "]").attr("selected", "true");
        }
        $("#serviceTypeCodeU").selectbox();
        $("#serviceTypeCodeU").trigger("change");

        $("#meterMdsIdU").val(info.mdsId);
        $("#meterMdsIdU_id").val(info.meterId);
        //ContractMeterId = info.meterId;

        if (info.status == "") {
            $("#statusU option:eq(0)").attr("selected", "true");
        } else {
            $("#statusU option[value=" + info.status + "]").attr("selected", "true");
        }
        $("#statusU").selectbox();

        $("#locationU").val(info.locationId);
        $("#locationUText").val(info.locationName);

        $("#contractDemandU").val(info.contractDemand);
        
//        $("#amountPaidU").val(info.amountPaid);
//        $("#receiptNoU").val(info.receiptNumber);
        $('#threshold1U').val(info.threshold1);
        $('#threshold2U').val(info.threshold2);
        $('#threshold3U').val(info.threshold3);

        if (info.serviceType2 == "") {
            $("#serviceType2U option:eq(0)").attr("selected", "true");
        } else {
            $("#serviceType2U option[value=" + info.serviceType2 + "]").attr("selected", "true");
        }
        $("#serviceType2U").selectbox();

        if (info.creditType == "") {
            $("#creditTypeU option:eq(0)").attr("selected", "true");
        } else {
            $("#creditTypeU option[value=" + info.creditType + "]").attr("selected", "true");
        }
        $("#creditTypeU").selectbox();
        $("#creditTypeU").trigger("change");

        if (info.creditStatus == "") {
            $("#creditStatusU option:eq(0)").attr("selected", "true");
        } else {
            $("#creditStatusU option[value=" + info.creditStatus + "]").attr("selected", "true");
        }
        $("#creditStatusU").selectbox();

        $("#prepaymentThresholdU").val(info.prepaymentThreshold);

        if (info.tariffType == "") {
            $("#tariffIndexU option:eq(0)").attr("selected", "true");
        } else {
            $("#tariffIndexU option[value=" + info.tariffType + "]").attr("selected", "true");
        }
        $("#tariffIndexU").selectbox();
        $("#currentArrearsU").val(info.currentArrears);
        var chargeAvailable = info.chargeAvailable;

        if (chargeAvailable != null && chargeAvailable != "") {
            if (chargeAvailable == "true") {
                $("input:radio[name='chargeAvailable']:radio[value='1']").attr("checked", true);
            } else {
                $("input:radio[name='chargeAvailable']:radio[value='0']").attr("checked", true);
            }
        } else {
            $("input:radio[name='chargeAvailable']").removeAttr("checked");
        }

    }

    /* Electricity Customer 리스트 START */
    var elCustomerGridOn = false;
    var elCustomerGrid;
    var elCustomerStore;
    var elCustomerColModel;
    var getElecCustomerList = function() {
        var width = $("#elecCustomerDiv").width();
        var pageSize = 18;
        var conditionArray = getConditionArray();
        var fmtMessage = getFmtMessage();

        var params = {
            contractNumber: conditionArray[15],
            customerNo : conditionArray[0],
            customerName : conditionArray[1],
            location : conditionArray[2],
            tariffIndex : conditionArray[3],
            contractDemand : conditionArray[4],
            creditType : conditionArray[5],
            mdsId : conditionArray[6],
            status : conditionArray[7],
            dr : conditionArray[8],
            sicIds : '',//conditionArray[9],
            startDate : conditionArray[10],
            endDate : conditionArray[11],
            address : conditionArray[12],
            serviceType : conditionArray[13],
            serviceTypeTab : conditionArray[14],
            gs1 : conditionArray[16]
        };

        elCustomerStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/system/customerMax.do?param=customerListByType",
            baseParams: params,
            totalProperty: 'totalCount',
            root:'result',
            fields: ["CONTRACT_NUMBER", "CUSTNAME", "LOCNAME", "TARIFFNAME", "CONTRACTDEMAND", "CREDITTYPENAME",
                     "MDS_ID", "STATUSNAME", "DEMANDRESPONSE", "SICNAME", "EMAIL", "TELEPHONENO", "MOBILENO", "GS1",
                     "CUSTOMERID", "CONTRACTID", "SERVICETYPE", "SERVICETYPE_NAME", "CUSTOMERNO"],
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
                        elCustomerGrid.getSelectionModel().selectFirstRow();
                    }
                }
            }
        });

        elCustomerColModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0],  dataIndex: 'CONTRACT_NUMBER', tooltip: fmtMessage[0]}
               ,{header: fmtMessage[1],  dataIndex: 'CUSTNAME',        tooltip: fmtMessage[1]}
               ,{header: fmtMessage[2],  dataIndex: 'LOCNAME',         tooltip: fmtMessage[2]}
               ,{header: fmtMessage[3],  dataIndex: 'TARIFFNAME',      tooltip: fmtMessage[3]}
               ,{header: fmtMessage[12], dataIndex: 'CONTRACTDEMAND',  tooltip: fmtMessage[12]}
               ,{header: fmtMessage[29],  dataIndex: 'GS1',            tooltip: fmtMessage[29]}
               ,{header: fmtMessage[4],  dataIndex: 'CREDITTYPENAME',  tooltip: fmtMessage[4]}
               ,{header: fmtMessage[5],  dataIndex: 'MDS_ID',          tooltip: fmtMessage[5]}
               ,{header: fmtMessage[6],  dataIndex: 'STATUSNAME',      tooltip: fmtMessage[6]}
               ,{header: fmtMessage[13], dataIndex: 'DEMANDRESPONSE',  tooltip: fmtMessage[13]}
               ,{header: fmtMessage[7],  dataIndex: 'SICNAME',         tooltip: fmtMessage[7]}
               ,{header: fmtMessage[8],  dataIndex: 'EMAIL',           tooltip: fmtMessage[8]}
               ,{header: fmtMessage[9],  dataIndex: 'TELEPHONENO',     tooltip: fmtMessage[9]}
               ,{header: fmtMessage[10], dataIndex: 'MOBILENO',        tooltip: fmtMessage[10]}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: (width/14)
               ,renderer: addTooltip
            }
        });

        if (elCustomerGridOn == false) {
            elCustomerGrid = new Ext.grid.GridPanel({
                store: elCustomerStore,
                colModel : elCustomerColModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(sm, row, rec) {
                            var rtnObj = new Object();
                            rtnObj.id = rec.get("CUSTOMERID");
                            rtnObj.serviceType = rec.get("SERVICETYPE");
                            rtnObj.contractId = rec.get("CONTRACTID");
                            rtnObj.customerNo = rec.get("CUSTOMERNO");
                            rtnObj.serviceTypeName = rec.get("SERVICETYPE_NAME");
                            receiveMsg(rtnObj);
                        }
                    }
                }),
                autoScroll:false,
                width: width,
                height: 470,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'elecCustomerDiv',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: elCustomerStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            elCustomerGridOn = true;
        } else {
            elCustomerGrid.setWidth(width);
            var bottomToolbar = elCustomerGrid.getBottomToolbar();
            elCustomerGrid.reconfigure(elCustomerStore, elCustomerColModel);
            bottomToolbar.bindStore(elCustomerStore);
        }
        hide();
    };

    /* Gas Customer 리스트 START */
    var gasCustomerGridOn = false;
    var gasCustomerGrid;
    var gasCustomerStore;
    var gasCustomerColModel;
    var getGasCustomerList = function() {
        getCustomerListByType("gas");
    };

    /* Water Customer 리스트 START */
    var wtCustomerGridOn = false;
    var wtCustomerGrid;
    var wtCustomerStore;
    var wtCustomerColModel;
    var getWaterCustomerList = function() {
        getCustomerListByType("water");
    };

    /* Heat Customer 리스트 START */
    var htCustomerGridOn = false;
    var htCustomerGrid;
    var htCustomerStore;
    var htCustomerColModel;
    var getHeatCustomerList = function() {
        getCustomerListByType("heat");
    };

    /* Volumn Corrector Customer 리스트 START */
    var volCustomerGridOn = false;
    var volCustomerGrid;
    var volCustomerStore;
    var volCustomerColModel;
    var getVolumnCustomerList = function() {
        getCustomerListByType("volumn");
    };

    // Gas/Water/Heat/Volumn Corrector Customer Grid
    var getCustomerListByType = function(sType) {
        var gridOn = false;
        var grid;
        var store;
        var colModel;
        var gridDiv;

        switch(sType) {
           case "gas":
               gridOn = gasCustomerGridOn;
               grid = gasCustomerGrid;
               store = gasCustomerStore;
               colModel = gasCustomerColModel;
               gridDiv = "gasCustomerDiv";
               break;
           case "water":
               gridOn = wtCustomerGridOn;
               grid = wtCustomerGrid;
               store = wtCustomerStore;
               colModel = wtCustomerColModel;
               gridDiv = "waterCustomerDiv";
               break;
           case "heat":
               gridOn = htCustomerGridOn;
               grid = htCustomerGrid;
               store = htCustomerStore;
               colModel = htCustomerColModel;
               gridDiv = "heatCustomerDiv";
               break;
           case "volumn":
               gridOn = volCustomerGridOn;
               grid = volCustomerGrid;
               store = volCustomerStore;
               colModel = volCustomerColModel;
               gridDiv = "volCustomerDiv";
               break;
        }

        var width = $("#" + gridDiv).width();
        var pageSize = 18;
        var conditionArray = getConditionArray();
        var fmtMessage = getFmtMessage();

        var params = {
            contractNumber: conditionArray[15],
            customerNo : conditionArray[0],
            customerName : conditionArray[1],
            location : conditionArray[2],
            tariffIndex : conditionArray[3],
            contractDemand : conditionArray[4],
            creditType : conditionArray[5],
            mdsId : conditionArray[6],
            status : conditionArray[7],
            dr : conditionArray[8],
            sicIds : conditionArray[9],
            startDate : conditionArray[10],
            endDate : conditionArray[11],
            address : conditionArray[12],
            serviceType : conditionArray[13],
            serviceTypeTab : conditionArray[14],
            gs1 : conditionArray[16],
            //tariffType:
        };

        store = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/system/customerMax.do?param=customerListByType",
            baseParams: params,
            totalProperty: 'totalCount',
            root:'result',
            fields: ["CONTRACT_NUMBER", "CUSTNAME", "LOCNAME", "TARIFFNAME", "CONTRACTDEMAND", "CREDITTYPENAME",
                     "MDS_ID", "STATUSNAME", "DEMANDRESPONSE", "SICNAME", "EMAIL", "TELEPHONENO", "MOBILENO", "GS1",
                     "CUSTOMERID", "CONTRACTID", "SERVICETYPE", "SERVICETYPE_NAME", "CUSTOMERNO"],
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
                        grid.getSelectionModel().selectFirstRow();
                    }
                }
            }
        });

        colModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0],  dataIndex: 'CONTRACT_NUMBER', tooltip: fmtMessage[0]}
               ,{header: fmtMessage[1],  dataIndex: 'CUSTNAME',        tooltip: fmtMessage[1]}
               ,{header: fmtMessage[2],  dataIndex: 'LOCNAME',         tooltip: fmtMessage[2]}
               ,{header: fmtMessage[3],  dataIndex: 'TARIFFNAME',      tooltip: fmtMessage[3]}
               ,{header: fmtMessage[4],  dataIndex: 'CREDITTYPENAME',  tooltip: fmtMessage[4]}
               ,{header: fmtMessage[5],  dataIndex: 'MDS_ID',          tooltip: fmtMessage[5]}
               ,{header: fmtMessage[29],  dataIndex: 'GS1',             tooltip: fmtMessage[29]}
               ,{header: fmtMessage[6],  dataIndex: 'STATUSNAME',      tooltip: fmtMessage[6]}
               ,{header: fmtMessage[7],  dataIndex: 'SICNAME',         tooltip: fmtMessage[7]}
               ,{header: fmtMessage[8],  dataIndex: 'EMAIL',           tooltip: fmtMessage[8]}
               ,{header: fmtMessage[9],  dataIndex: 'TELEPHONENO',     tooltip: fmtMessage[9]}
               ,{header: fmtMessage[10], dataIndex: 'MOBILENO',        tooltip: fmtMessage[10], width: (width/11)-4}
            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: (width/12)
               ,renderer: addTooltip
           }
        });

        if (gridOn == false) {
            grid = new Ext.grid.GridPanel({
                store: store,
                colModel : colModel,
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(sm, row, rec) {
                            var rtnObj = new Object();
                            rtnObj.id = rec.get("CUSTOMERID");
                            rtnObj.serviceType = rec.get("SERVICETYPE");
                            rtnObj.contractId = rec.get("CONTRACTID");
                            rtnObj.customerNo = rec.get("CUSTOMERNO");
                            rtnObj.serviceTypeName = rec.get("SERVICETYPE_NAME");
                            receiveMsg(rtnObj);
                        }
                    }
                }),
                autoScroll:false,
                width: width,
                height: 470,
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: gridDiv,
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: store,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            gridOn = true;
        } else {
            grid.setWidth(width);
            var bottomToolbar = grid.getBottomToolbar();
            grid.reconfigure(store, colModel);
            bottomToolbar.bindStore(store);
        }
        hide();

        switch(sType) {
            case "gas":
                gasCustomerGridOn = gridOn;
                gasCustomerGrid = grid;
                gasCustomerStore = store;
                gasCustomerColModel = colModel;
                break;
            case "water":
                wtCustomerGridOn = gridOn;
                wtCustomerGrid = grid;
                wtCustomerStore = store;
                wtCustomerColModel = colModel;
                break;
            case "heat":
                htCustomerGridOn = gridOn;
                htCustomerGrid = grid;
                htCustomerStore = store;
                htCustomerColModel = colModel;
                break;
            case "volumn":
                volCustomerGridOn = gridOn;
                volCustomerGrid = grid;
                volCustomerStore = store;
                volCustomerColModel = colModel;
                break;
         }
    };

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "") {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }

    // treegrid column tooltip
    function addTreeTooltip(value, values) {
        //if (value != null && value != "" && values != null && values.leaf != null && values.leaf == true) {
        if (value != null && value != "") {
            return '<span ext:qtip="' + value + '">' + value + '</span>';
        } else {
            return value;
        }
    }
        
    var win;
    function customerExcelExport() {
        var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();
        var conditions = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.no"/>";
        fmtMessage[1] = "<fmt:message key="aimir.customerid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
        fmtMessage[3] = "<fmt:message key="aimir.address"/>";

        conditions[0] = $('#contractNumberA').val();
        conditions[1] = $('#customerNoA').val();
        conditions[2] = $('#customerNameA').val();
        conditions[3] = $('#locationA').val();
        conditions[4] = '';
        conditions[5] = '';
        conditions[6] = '';
        conditions[7] = $('#mdsIdA').val();
        conditions[8] = '';
        conditions[9] = '';
        conditions[10] = '';//$('#sicIdsA').val();
        conditions[11] = '';
        conditions[12] = '';
        conditions[13] = '';//$('#addressA').val();
        conditions[14] = $('#serviceTypeA').val();
        conditions[15] = serviceTypeTab;
        conditions[16] = supplierId;
        conditions[17] = $('#gs1').val();
        conditions[18] = $('#phoneNumberA').val();
        conditions[19] = $('#barcodeA').val();
        conditions[20] = $('#oldmdsIdA').val();
        conditions[21] = $('#customer_type').val();
        

        obj.condition = conditions;
        obj.fmtMessage = fmtMessage;
        obj.url = "${ctx}/gadget/system/customerMaxExcelMake.do";

        if(win)
            win.close();

        win = window.open("${ctx}/gadget/system/customerExcelDownloadPopup.do",
                        "meterExcel", opts);
        win.opener.obj = obj;
    }
    
    function changeDebtU() {
    	var selectedDebtRef = $('#debtTypeU option:selected').val();
    
    	$.post(
                "${ctx}/gadget/prepaymentMgmt/getDebtInfoByCustomerNo.do",
                    {customerNo: $('#customerNoU').val(),
                	debtRef: selectedDebtRef},
                    function(json) {
						debtInfo = json.debtInfo[0];

						$('#debtAmountTitleU').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfo.debtType));
						$('#debtAmountU').val(debtInfo.debtAmount);
						$('#debtContractCntTitleU').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfo.debtType));
						$('#debtContractCntU').val(debtInfo.debtContractCount);
						$('#debtPaymentCntU').val(debtInfo.debtPaymentCount);
                    }
              );
    }
    
    function changeDebt() {
    	
    	var selectedDebtRef = $('#debtType option:selected').val();
    
    	$.post(
                "${ctx}/gadget/prepaymentMgmt/getDebtInfoByCustomerNo.do",
                    {customerNo: $('#customerNo').val(),
                	debtRef: selectedDebtRef},
                    function(json) {
						debtInfo = json.debtInfo[0];
						$('#debtAmountTitle').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfo.debtType));
						$('#debtAmount').val(debtInfo.debtAmount);
						$('#debtContractCntTitle').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfo.debtType));

						var tempPaymentCount = debtInfo.debtPaymentCount == null ? 0 : debtInfo.debtPaymentCount;
						if(debtInfo.debtContractCount == null || debtInfo.debtContractCount == '"null"' 
								|| debtInfo.debtContractCount == 0 || debtInfo.debtContractCount == "null") {
							$('#debtContractCnt').val("-");
						} else {
							$('#debtContractCnt').val(tempPaymentCount+"/"+debtInfo.debtContractCount);
						}
						
                    }
              );
    }

    function changeDebtA() {
    	
    	var selectedDebtRef = $('#debtTypeA option:selected').val();
    
    	$.post(
                "${ctx}/gadget/prepaymentMgmt/getDebtInfoByCustomerNo.do",
                    {customerNo: $('#contractCustomerNo').val(),
                	debtRef: selectedDebtRef},
                    function(json) {
                		
						debtInfo = json.debtInfo[0];
						$('#debtAmountTitleA').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfo.debtType));
						$('#debtAmountA').val(debtInfo.debtAmount);
						$('#debtContractCntTitleA').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfo.debtType));
						$('#debtContractCntA').val(debtInfo.debtContractCount);
						$('#debtPaymentCntA').val(debtInfo.debtPaymentCount);
                    }
              );
    }
    
    function changeDebtContractCntU() {
    	
    	if(isNaN($('#debtContractCntU').val()) || 
    			(($("#debtContractCntU").val() != null || $("#debtContractCntU").val() != '') && $('#debtContractCntU').val() <= 0)) {
    		Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.msg.onlydigit'/>");
    		$('#debtContractCntU').val('');
    		$('#debtContractCntU').focus();
    		return;
    	}
    	
    	if($('#debtPaymentCntU').val() > 0) {
    		Ext.Msg.alert("<fmt:message key='aimir.message'/>","["+$('#debtTypeU option:selected').text()+"]: <fmt:message key='aimir.cannot.modify.debtPaymentCnt.already'/>");
    		return;
    	}
    		
    	var debtSubKey=$('#debtTypeU option:selected').val();

    	//동일한  DebtType에 대한 수정 값이 존재 할 경우 마지막에 수정한 값으로 덮어씌움. 
    	deleteDebtSubU(debtSubKey);

 		$('#debtSaveInfoU').append("<tr id=debtSaveInfoU_" + debtSubKey + ">" + 
 								  "<td class='bold withinput'><fmt:message key='aimir.debtType'/></td><td id='debtSubTypeU_" + debtSubKey + "' style='width:150px'>" + $('#debtTypeU option:selected').text() + "</td>" + 
  								  "<td class='bold withinput'><fmt:message key='aimir.debtAmount2'/></td><td id='debtSubAmountU_" + debtSubKey + "' style='width:150px'>" + $('#debtAmountU').val() + "</td>"+
    							  "<td class='bold withinput'><fmt:message key='aimir.debtContractCnt2'/></td><td id='debtSubContractCntU_" + debtSubKey +"' style='width:130px'>" + $('#debtContractCntU').val() + "</td>" + 
    							  "<td class='bold withinput'><div id='btn'><ul><li><a href='javascript:;' onClick='javascript:deleteDebtSubU(\""+debtSubKey+"\");' class='on'><fmt:message key='aimir.bemsfacilityMgmt.delete'/></a></li></ul></div></td>" + 
    							  "</tr>");
 		var tempDebtArr = {
			debtType : $('#debtTypeU option:selected').text(),
			debtContractCnt : $('#debtContractCntU').val(),
			debtRef : debtSubKey,
			customerNo : $('#customerNo').val(),
			debtAmount : $('#debtAmountU').val()
 		};
 		debtSaveArrU.push(tempDebtArr);
 		$('#prepaymentStatusTr4_Sub').css("display","");
 		$('#contractInfoTab').height($('#contractInfoTab').height() + 28);
    }
    
    function deleteDebtSubU(debtSubKey) {
    	if($('#debtSaveInfoU_' + debtSubKey).length != 0) {
    		$('#debtSaveInfoU_' + debtSubKey).remove();
    		for(var i=0; i < debtSaveArrU.length; i++) {
    			if(debtSaveArrU[i].debtRef == debtSubKey) {
    				debtSaveArrU.pop(i);
    			}
    		}
    		$('#contractInfoTab').height($('#contractInfoTab').height() - 28);	
    	}
    	
    	if(!($('#debtSaveInfoU').html().includes("tr"))) { 
			debtSaveArrU = new Array();    		
    		$('#prepaymentStatusTr4_Sub').css("display","none");
    	}
    }
    
    function clearDebtSave() {
    	debtSaveArrU = new Array();
        $('#debtSaveInfoU').html('');
        $('#prepaymentStatusTr4_Sub').css("display","none");
        $('#contractInfoTab').height(290);
    }
    
</script>
</head>
<body>
<!-- input type="hidden" id="ConMeterId" -->
<input type="hidden" id="updCheckYN" name="updCheckYN" />
<form name="customerForm_" id="customerForm_" method="post">
</form>
<!-- (전체) Contract Tab (S) -->
<div id="contractTab">
    <!-- 버튼 - 신규고객추가 (S) -->
    <div id="pane-Customer-Info-Button">
        <div id="btn">
            <ul><li class="arrup"><a id="customerAddForm" href="#" class="on-green-bold"><fmt:message key="aimir.new.customer"/></a></li></ul>
        </div>
    </div>
    <!-- 버튼 - 신규고객추가 (E) -->

    <!-- (상단) 고객목록 탭 전체 (S) -->
    <div id="customerTab">

        <ul>
            <li><a href="#allCustomerTab" id="_allCustomerTab"><fmt:message key="aimir.customerlist.all" /></a></li>
            <li><a href="#emCustomerTab" id="_emCustomerTab"><fmt:message key="aimir.customerlist.energy" /></a></li>
            <li><a href="#gmCustomerTab" id="_gmCustomerTab"><fmt:message key="aimir.customerlist.gas" /></a></li>
            <li><a href="#wmCustomerTab" id="_wmCustomerTab"><fmt:message key="aimir.customerlist.water" /></a></li>
            <li><a href="#hmCustomerTab" id="_hmCustomerTab"><fmt:message key="aimir.customerlist.heat" /></a></li>
            <li><a href="#vcCustomerTab" id="_vcCustomerTab"><fmt:message key="aimir.customerlist.volumecorrector" /></a></li>
        </ul>

        <!-- 트리 고객 계약 목록 start -->
        <div id="allCustomerTab">
            <div class="searchbox">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberA"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.customername"/></td>
                        <td class="padding-r20px2"><input id="customerNameA" type="text"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td class="padding-r20px2">
                            <input type="text" id="locationAText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationA" name="location.id" value="" />
                        </td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td class="padding-r20px2"><select id="creditTypeA" style="width:125px;"></select></td>
                        <%-- <td class="withinput"><fmt:message key="aimir.address" /></td>
                        <td><input id="addressA"></td> --%>
                        <%-- <c:if test="${role == 'admin'}">
                        	<td class="withinput"><fmt:message key="aimir.operator" /></td>
                        	<td colspan="4" class="padding-r20px2"><select id="operatorA"></select></td>

                        </c:if> --%>
						<td class="gray11pt withinput" style="width:60px;"><fmt:message key="aimir.contract.tariff.type"/></td>
						<td><form:select id="customer_type"  path="tariffType" items="${tariffType}" style="width:230px;"/></td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.customerid"/></td>
                        <td class="padding-r20px2"><input id="customerNoA" type="text"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.phoneNumber"/></td>
                        <td class="padding-r20px2"><input id="phoneNumberA"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.barcode"/></td>
                        <td class="padding-r20px2"><input id="barcodeA"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.supply.type" /></td>
                        <td class="padding-r20px2"><select id="serviceTypeA" style="width:142px"></select></td>
                    </tr>
                    <tr>
                    	<td class="withinput"><fmt:message key="aimir.shipment.gs1"/></td>
                        <td class="padding-r20px2"><input id="gs1" type="text"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdA"></td>
                        
                        <td class="withinput"><fmt:message key="aimir.oldGs1" /></td>
                        <td class="padding-r20px2"><input id="oldmdsIdA"></td>
                        
                        <c:if test="${role == 'admin'}">
                        	<td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>
	                        <td>
	                            <span><input id="startDateA" type="text" style="width:80px;"></span>
	                            <span><input value="~" type="text" class="between"></span>
	                            <span><input id="endDateA" type="text" style="width:80px;"></span>
	                        </td>
                        </c:if>
                        <td>
                        	<span class="am_button margin-l10 margin-t1px">
                        	<a href="javascript:customerSearchAll();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>
                    </tr>
                </table>
                <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivA"></div>
                </div>
                <div id="treeDivSAOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivSA"></div>
                </div>
            </div>
            <div class="bodyleft_customer">
                <ul><li>
                    <div class="headspace-descr">
                        <span class="result-text"><fmt:message key="aimir.contract.totalCnt" /></span>
                        <span class="result-num" id='contractCount'>0</span>
                        <span style="float: right">
                           <a href="#" onClick="customerExcelExport();" class="btn_blue"> <span><fmt:message key="aimir.button.excel" /></span>
                           </a>
                         </span>
                    </div>
                   <!-- 트리 그리드 div  start -->
                    <div id="treeGridDiv" style="margin-top: 5px;"></div>

                <!-- 트리 그리드 div  End -->
                </li></ul>
            </div>
        </div>
        <!-- (상단) 고객목록 Tab : 1ST (E) -->

        <!-- (상단) 고객목록 Tab : 2ND (S) -->
        <div id="emCustomerTab">
            <div class="searchbox">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberB"></td>                                
                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px2"><input id="customerNoB" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px2"><input id="customerNameB" style="width:125px"></td>
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td class="padding-r20px2">
                            <input type="text" id="locationBText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationB" name="location.id" value="" />
                        </td>

                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdB" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.shipment.gs1" /></td>
                        <td class="padding-r20px2"><input id="gs1B" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
                        <td class="padding-r20px2"><select id="statusB" style="width: 125px"></select></td>
                        <%-- <td class="withinput"><fmt:message key="aimir.customer.dr" /></td>
                        <td class="padding-r20px2"><select id="drB" style="width: 125px"></select></td> --%>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                        <td><select id="tariffIndexB" style="width: 125px"></select></td>
                        <td class="withinput"><fmt:message key="aimir.contract.demand" /></td>
                        <td class="padding-r20px2"><input id="contractDemandB" style="width:80px;"></td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td class="padding-r20px2"><select id="creditTypeB" style="width:125px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>
                        <td>
                            <span><input id="startDateB" type="text" style="width:80px;"></span>
                            <span><input value="~" type="text" class="between"></span>
                            <span><input id="endDateB" type="text" style="width:80px;"></span>
                            <span class="am_button margin-l10 margin-t1px"><a href="javascript:customerSearch();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>
                    </tr>
                </table>
                <div id="treeDivBOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivB"></div>
                </div>
                <div id="treeDivSBOuter" class="tree-billing auto" style="display: none;">
                    <div id="treeDivSB"></div>
                </div>
            </div>

            <div class="bodyleft_customer">
                <ul><li>
                    <div id="elecCustomerDiv" class="flexlist-customerlist">
                    </div>
                </li></ul>
            </div>

        </div>
        <!-- (상단) 고객목록 Tab : 2ND (E) -->


        <!-- (상단) 고객목록 Tab : 3RD (S) -->
        <div id="gmCustomerTab">
            <div  class="searchbox">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberC"></td>                                
                        <td class=" withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px2"><input id="customerNoC" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px2"><input id="customerNameC" style="width:125px;"></td>
                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                        <td class="padding-r20px2" style="width:120px;"><select id="tariffIndexC" style="width:97px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td><select id="creditTypeC" style="width:80px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td>
                            <input type="text" id="locationCText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationC" name="location.id" value="" />
                        </td>                                
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdC"></td>
                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
                        <td class="padding-r20px2"><select id="statusC" style="width:125px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.sic" /></td>
                        <td class="withinput">
                            <fmt:message key="aimir.contract"/><fmt:message key="aimir.day" />
                        </td>
                        <td colspan="3">
                            <span><input id="startDateC" type="text" style="width:80px"></span>
                            <span><input value="~" type="text" class="between"></span>
                            <span><input id="endDateC" type="text" style="width:80px"></span>
                            <span class="am_button margin-l10 margin-t1px">
                            <a href="javascript:customerSearchGM();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>
                    </tr>
                </table>
                <div id="treeDivCOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivC"></div>
                </div>
              <div id="treeDivSCOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivSC"></div>
                </div>
            </div>

            <div class="bodyleft_customer">
            <ul><li>
                <div id="gasCustomerDiv" class="flexlist-customerlist">
                </div>
                </li></ul>
            </div>

        </div>
        <!-- (상단) 고객목록 Tab : 3RD (E) -->

        <!-- (상단) 고객목록 Tab : 4TH (S) -->
        <div id="wmCustomerTab">
            <div class="searchbox">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberD"></td>                                
                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px2"><input id="customerNoD" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px2"><input id="customerNameD" style="width:125px;"></td>
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td class="padding-r20px2">
                            <input type="text" id="locationDText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationD" name="location.id" value="" />
                        </td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdD"></td>
                        <td class="withinput"><fmt:message key="aimir.shipment.gs1" /></td>
                        <td class="padding-r20px2"><input id="gs1D"></td>
                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
                        <td class="padding-r20px2"><select id="statusD" style="width:125px;"></select></td>
                        
                                                        <!-- <td class="padding-r20px2"><select id="customerTypeD" style="width:270px;"></select></td> -->
                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                        <td class="padding-r20px2"><select id="tariffIndexD" style="width:97px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td><select id="creditTypeD" style="width:80px;"></select></td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>                                
                        <td colspan="3">
                            <span><input id="startDateD" type="text" style="width:80px"></span>
                            <span><input value="~" type="text" class="between"></span>
                            <span><input id="endDateD" type="text" style="width:80px"></span>
                            <span class="am_button margin-l10 margin-t1px"><a href="javascript:customerSearchWM();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>                                
                    </tr>
                </table>
                <div id="treeDivDOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivD"></div>
                </div>
                                        <div id="treeDivSDOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivSD"></div>
                </div>
            </div>

            <div class="bodyleft_customer">
                <ul><li>
                <div id="waterCustomerDiv" class="flexlist-customerlist">
                </div>
                </li></ul>
            </div>

        </div>
        <!-- (상단) 고객목록 Tab : 4TH (E) -->

        <!-- (상단) 고객목록 Tab : 5TH (S) -->
        <div id="hmCustomerTab">
            <div class="searchbox2">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberE"></td>                                
                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px2"><input id="customerNoE" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px2"><input id="customerNameE" style="width:125px;"></td>
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td class="padding-r20px2">
                            <input type="text" id="locationEText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationE" name="location.id" value="" />
                        </td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdE"></td>
                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
                        <td class="padding-r20px2"><select id="statusE" style="width:125px;"></select></td>

                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                        <td class="padding-r20px2"><select id="tariffIndexE" style="width:97px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td><select id="creditTypeE" style="width:80px;"></select></td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>
                        <td colspan="3">
                            <span><input id="startDateE" type="text" style="width:80px"></span>
                            <span><input value="~" type="text" class="between"></span>
                            <span><input id="endDateE" type="text" style="width:80px"></span>
                            <span class="am_button margin-l10 margin-t1px"><a href="javascript:customerSearchHM();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>                                
                    </tr>
                </table>
                <div id="treeDivEOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivE"></div>
                </div>
                <div id="treeDivSEOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivSE"></div>
                </div>
            </div>

            <div class="bodyleft_customer">
                <ul><li>
                    <div id="heatCustomerDiv" class="flexlist-customerlist">
                    </div>
                </li></ul>
            </div>
        </div>
        <!-- (상단) 고객목록 Tab : 5TH (E) -->

        <!-- (상단) 고객목록 Tab : 6TH (S) -->
        <div id="vcCustomerTab">
            <div  class="searchbox2">
                <table class="searching">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
                        <td><input id="contractNumberF"></td>                                
                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px2"><input id="customerNoF" type="text"></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px2"><input id="customerNameF" style="width:125px;"></td>
                        <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                        <td class="padding-r20px2">
                            <input type="text" id="locationFText" name="location.name" style="width:142px">
                            <input type="hidden" id="locationF" name="location.id" value="" />

                        </td>
                        <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                        <td class="padding-r20px2"><select id="tariffIndexF" style="width:97px;"></select></td>
                        <td class="withinput"><fmt:message key="aimir.paymenttype" /></td>
                        <td><select id="creditTypeF" style="width:80px;"></select></td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td class="padding-r20px2"><input id="mdsIdF"></td>
                        <td class="withinput"><fmt:message key="aimir.supplystatus" /></td>
                        <td class="padding-r20px2"><select id="statusF" style="width:125px;"></select></td>
                        <td class="padding-r20px2">
                        <td class="withinput"><fmt:message key="aimir.contract"/><fmt:message key="aimir.day" /></td>
                        <td colspan="3">
                            <span><input id="startDateF" type="text" style="width:80px"></span>
                            <span><input value="~" type="text" class="between"></span>
                            <span><input id="endDateF" type="text" style="width:80px"></span>
                            <span class="am_button margin-l10 margin-t1px"><a href="javascript:customerSearchVC();" class="on"><fmt:message key="aimir.button.search" /></a></span>
                        </td>
                    </tr>
                </table>
                <div id="treeDivFOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivF"></div>
                </div>
                <div id="treeDivSFOuter" class="tree-billing auto" style="display:none;">
                    <div id="treeDivSF"></div>
                </div>
            </div>

            <div class="bodyleft_customer">
                <ul><li>
                    <div id="volCustomerDiv" class="flexlist-customerlist">
                    </div>
                </li></ul>
            </div>
        </div>
        <!-- (상단) 고객목록 Tab : 6TH (E) -->

        <!-- (상단우측) 신규등록 및 상세정보 (S) -->
        <div class="bodyright_customer">
            <div id="pane-tab-Customer"></div>
        </div>
        <!-- (상단우측) 신규등록 및 상세정보 (E) -->

    </div>
    <!-- (상단) 고객목록 탭 전체 (E) -->

    <!-- (하단) 계약정보 (S) -->
    <div id="contractStatusTab" style="display: none; top: 630px;">
        <div class="headspace"><label class="check"><fmt:message key="aimir.contractInfo" /></label></div>

        <ul>
            <li><a href="#contractInfoTab" id="_contractInfoTab"><fmt:message key="aimir.contractInfo" /></a></li>
            <li><a href="#contractChangeLogTab" id="_contractChangeLogTab"><fmt:message key="aimir.buildingMgmt.energyContractHistory" /></a></li>
<!--    
        2014.12.29 simhanger
        더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
        
        <li><a href="#paymentLogTab" id="_paymentLogTab"><fmt:message key="aimir.prepaidPlan" /></a></li>
-->
            <li><a href="#billingMonthTab" id="_billingMonthTab"><fmt:message key="aimir.monthly.usage" /></a></li>
        </ul>

        <div class="tabcontentsbox">
            <ul>
                <li>
                    <!-- 계약정보 tab start -->
                    <div id="contractInfoTab" class="blueline bg-blue" style="height: 290px;">
                        <ul class="width">

                            <!-- 계약정보 Detail tab start -->

                            <li class="contractInfoDetail" id="contractInfoDetail">
                                <table class="searchoption wfree" border=0>
                                    <tr>

                                        <!-- 고객번호-->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.customerid"/>
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="customerNo" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>

                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contractNumber"/>
                                            <!-- 계약번호-->
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="contractNumber" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>

                                        <!-- 계약 종별 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contract.tariff.type" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="tariffIndexName" readonly class="border-trans bg-trans" style="width: 200px;"/>
                                        </td>

                                        <td class="bold withinput"><fmt:message key="aimir.service.type" /></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="serviceType2D" readonly class="border-trans bg-trans" style="width: 200px;"/>
                                        </td>

                                    </tr>
                                    <tr>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.supply.type" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="serviceTypeName" readonly class="border-trans bg-trans"/>
                                        </td>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.supplystatus" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="statusName" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
            
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.location.supplier" />
                                        </td>
                                        <td>
                                            <input type="text" id="locationName" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                            <input type="hidden" id="locationId" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>

                                        
                                    </tr>
<!--                          
                                    <tr>
                                        <td class="bold withinput"><fmt:message key="aimir.contract.receioptNo"/></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="receiptNoD" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                          
                                        <td class="bold withinput"><fmt:message key="aimir.contract.amountPaid"/></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="amountPaidD" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                          
                                        <td class="bold withinput"><fmt:message key="aimir.service.type" /></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="serviceType2D" readonly class="border-trans bg-trans" style="width: 200px;"/>
                                        </td>
                                    </tr>
-->
                                    <!--  임계치 설정 -->
                                    <tr>
<%--                                         <td class="bold withinput"><fmt:message key="aimir.threshold1"/></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="threshold1" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                          
                                        <td class="bold withinput"><fmt:message key="aimir.threshold2"/></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="threshold2" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                          
                                        <td class="bold withinput"><fmt:message key="aimir.threshold3"/></td>
                                        <td class="padding-r20px2"> 
                                            <input type="text" id="threshold3" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td> --%>
                                                                                
										<!-- 이전 미터 id -->
										<td class="bold withinput"><fmt:message key="aimir.preMeterid"/></td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="preMdsId" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                                        <!-- 미터id -->
                                        <td class="bold withinput"><fmt:message key="aimir.meterid" /></td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="meterMdsId" readonly class="border-trans bg-trans" style="width: 120px; text-align: left;"/>
                                        </td>   
                                                                                                                        
                                        <!-- gs1 -->
                                        <td class="bold withinput"><fmt:message key="aimir.shipment.gs1" /></td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="meterGs1" readonly class="border-trans bg-trans" style="width: 120px; text-align: left;"/>
                                        </td>  
                                    </tr>     
                                    <tr>
                                        <!-- 지불타입 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.paymenttype" />
                                        </td>
                                        <td>
                                            <input type="text" id="creditTypeName" readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>

                                        <!-- 계약전력 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contract.demand.amount" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="contractDemand" readonly class="border-trans bg-trans"/>
                                        </td>

                                       
                                        <!-- operator 정보 -->
                                        <td id="operatorInfo1" class="bold withinput" style="display: none;">
                                            <fmt:message key="aimir.operator" />
                                        </td>
                                        <td id="operatorInfo2" class="padding-r20px2" style="display: none;">
                                            <input type="text" id="operator" readonly class="border-trans bg-trans"/>
                                        </td> 
                                    </tr>

                                    <!-- prepayment일 경우 보여주는 div -->
                                    <tr id="prepaymentTr01" >
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contractDate" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="contractStartDate" readonly="readonly" class="border-trans bg-trans"/>
                                        </td> 
										<!-- 미수금 -->
                                        <td id="prepaymentTr01_3" style="display: none;" class="bold withinput">
                                            <fmt:message key="aimir.arrearsA" />
                                        </td>
                                        <td id="prepaymentTr01_4" style="display: none;"  class="padding-r20px2">
                                            <input type="text" id="currentArrears" readonly="readonly" class="border-trans bg-trans"/>
                                        </td>
                                        <td id="prepaymentTr01_1" style="display: none;" class="bold withinput">
                                            <fmt:message key="aimir.arrearsB" />
                                        </td>
                                        <td id="prepaymentTr01_2" style="display: none;" class="padding-r20px2">
                                            <input type="text" id="currentArrears2" readonly="readonly" class="border-trans bg-trans"/>
                                        </td> 
                                        <!-- 미수금 납부상태-->
                                        <td id="prepaymentTr01_5" style="display: none;"  class="bold withinput">
											<fmt:message key='aimir.payment.contractCnt'/>
                                        </td>
                                        <td id="prepaymentTr01_6" style="display: none;"  class="padding-r20px2">
                                            <input type="text" id="arrearsContractCountD" readonly="readonly" class="border-trans bg-trans"/>
                                        </td>
                                    </tr>
                               
                                    <tr id="prepaymentTr02" style="display: none;">
                                        <!-- 바코드 -->
                                        <td class="bold withinput">
                                            <fmt:message key='aimir.debtType'/>
                                        </td>
                                        <td class="padding-r20px2">
                                            <span><select id="debtType"></select></span>
                                        </td>
                                        <td id="debtAmountTitle" class="bold withinput">
                                            <fmt:message key='aimir.debtAmount'/>
                                        </td>
                                        <td class="padding-r20px2">
                                            <span>
                                                <input name="debtAmount" id="debtAmount" type="text" style="width: 100px" readonly class="border-trans bg-trans"/>
                                            </span>
                                        </td>
                                        <td id="debtContractCntTitle" class="bold withinput">
                                            <fmt:message key='aimir.debtContractCnt'/>
                                        </td>
                                        <td class="padding-r20px2">
                                            <span>
                                                <input name="debtContractCnt" id="debtContractCnt" type="text" style="width: 100px" readonly class="border-trans bg-trans"/>
                                            </span>
                                        </td>
                                    </tr>
                                    
                                    <tr id="prepaymentTr03" style="display: none;">
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.barcode"/>
                                        </td>
                                        <td>
                                            <input type='text' id='barcode' readonly class="border-trans bg-trans" style="width: 120px;"/>
                                        </td>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.charging" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="chargeAvailable" readonly="readonly" class="border-trans bg-trans"/>
                                        </td>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.paymentstat" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <span>
                                                <input name="creditStatus" id="creditStatus" type="text" style="width: 100px" readonly class="border-trans bg-trans"/>
                                            </span>
                                        </td>

                                    </tr>
                                    
                                    <tr id="prepaymentTr04" style="display: none;">
                                        <td colspan="7">
                                            <span class="withinput" style="font-weight: bold;">
                                                <fmt:message key="aimir.msg.balanceIsLessThan" />
                                            </span>
                                            <span >
                                                <input name="prepaymentThreshold2" id="prepaymentThreshold2" type="text" style="width: 100px" readonly class="border-trans bg-trans"/>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr id="prepaymentTr05" style="display: none;">
                                        <td colspan="7">
                                            <span class="withinput" style="font-weight: bold;">
                                                <fmt:message key="aimir.msg.balanceIsLessThan2" />
                                            </span>
                                        </td>
                                    </tr>

                                <!-- prepayment일 경우 보여주는 div End -->
                                </table>
                                <!-- 수정 삭제 버튼 -->
                                <div id="btn" class="contractInfoUpdateForm">
                                    <ul>
                                        <li class="input">
                                            <a id="contractInfoUpdateForm" class="on">
                                                <fmt:message key="aimir.update" />
                                            </a>
                                        </li>
                                    </ul>
                                    <ul>
                                        <li class="input">
                                            <a id="contractDel" class="on">
                                                <fmt:message key="aimir.button.delete" />
                                            </a>
                                        </li>
                                    </ul>
                                </div>

                            </li>

                            <!-- 계약정보 Detail tab End -->


                            <!-- 계약정보 수정 탭 start -->
        
                            <li class="padding" id="contractInfoUpdate" style="display: none">
        
                                <table class="searchoption wfree"  border=0 >

                                    <tr>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.customerid"/>
                                            <!-- 고객번호-->
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="customerNoU"  style="width: 150px;" readonly/>
                                        </td>

                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contractNumber"/>
                                            <!-- 계약번호-->
                                        </td>
                                        <td>
                                            <div class="check-overlap-btn-contract">
                                                <span><input type="text" id="contractNumberU" style="width:90px"/></span>
                                                <span><div id="btn"><ul><li><a id="updContractNoCheck" class="on"><fmt:message key="aimir.button.check"/></a></li></ul></div></span>
                                                <!-- <span id="updContractNoCheck"><div id="btn"><ul><li onclick="alert('test2');"><fmt:message key="aimir.button.check"/></li></ul></div></span> -->
                                            </div>
                                            <div id="updCheckValue" class="check-overlap check-overlap-contract" style="width:150px; overflow:visible;"></div>
                                        </td>

                                        <!-- 계약종별 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contract.tariff.type" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <select id="tariffIndexU" style="width:180px">
                                            </select>
                                        </td>
                                        
                                        <td class="bold withinput"><fmt:message key="aimir.service.type" /></td>
                                        <td><select name="serviceType2U" id="serviceType2U" style="width:215px;"></select></td>

                                    </tr>
                                    <tr>
                                        <!-- 공급타입 -->
                                        <td class="darkgraybold11pt withinput">
                                            <fmt:message key="aimir.supply.type" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <select id="serviceTypeCodeU" style="width:150px;">
                                            </select>
                                        </td>

                                        <!-- 공급상태 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.supplystatus" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <select id="statusU" style="width:150px;">
                                            </select>
                                        </td>

                                        <!-- 공급지역 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.location.supplier" />
                                        </td>
                                        <td>
                                            <input type="text" id="locationUText" name="location.name" style="width:180px"/>
                                            <input type="hidden" id="locationU" name="location.id" value="" />
                                        </td>
                                    </tr>
<!--
                                    <tr>
                                        <td class="bold withinput"><fmt:message key="aimir.contract.receioptNo"/></td>
                                        <td><input name="receiptNoU" style="width:150px" id="receiptNoU" type="text"/></td>

                                        <td class="bold withinput"><fmt:message key="aimir.contract.amountPaid"/></td>
                                        <td><input name="amountPaidU" style="width:150px" id="amountPaidU" type="text"/></td>

                                        <td class="bold withinput"><fmt:message key="aimir.service.type" /></td>
                                        <td><select name="serviceType2U" id="serviceType2U" style="width:200px;"></select></td>
                                    </tr>
-->                                    
                                    <!--  임계치 설정 -->
                                    <tr>
<%--                                         <td class="bold withinput"><fmt:message key="aimir.threshold1"/></td>
                                        <td><input name="threshold1U" style="width:150px" id="threshold1U" type="text"/></td>

                                        <td class="bold withinput"><fmt:message key="aimir.threshold2"/></td>
                                        <td><input name="threshold2U" style="width:150px" id="threshold2U" type="text"/></td>

                                        <td class="bold withinput"><fmt:message key="aimir.threshold3"/></td>
                                        <td><input name="threshold3U" style="width:180px" id="threshold3U" type="text"/></td> --%>
                                        
                                        <!-- 예전미터 시리얼 -->
                                        <td class="bold withinput"><fmt:message key="aimir.preMeterid"/></td>
                                        <td><input name="preMdsIdU" style="width:150px" id="preMdsIdU" type="text"/></td>
                                        
                                         <!-- 미터id U-->
                                        <td class="bold withinput" ><fmt:message key="aimir.meterid" /></td>
                                        <td class="padding-r20px2">
                                            <input type="text" id="meterMdsIdU" style="width: 150px;"/>
                                            <input type="hidden" id="meterMdsIdU_id" style="width: 150px;"/>
 
                                        </td>
                                        
                                        <td class="bold withinput"><fmt:message key="aimir.shipment.gs1"/></td>
                                        <td><input name="meterGs1U" style="width:180px" id="meterGs1U" type="text"/></td>
                                        
                                        <td>
                                            <!-- search button2 -->
                                            <span class="am_button margin-l10 margin-t1px">
                                                <a id="meterSearchButton2" href="#" class="on"><fmt:message key="aimir.button.search" /></a>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <!-- 지불 타입 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.paymenttype" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <select id="creditTypeU" style="width: 150px;">
                                            </select>
                                        </td>

										<!-- 계약전력 -->
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.contract.demand.amount" />
                                        </td>
                                        <td class="padding-r20px2">
                                            <input type="text" id='contractDemandU' style="width: 150px;"/>
                                        </td>
                                    </tr>

                                    <!-- 지불상태(선불을 선택했을 경우 보여주는 div -->
                                    <tr id="prepaymentStatusTr">
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.arrearsA"/>
                                        </td>
                                        <td>
                                            <input type='text' id='currentArrearsU' name="currentArrears" style="width: 150px"/>
                                        </td>
                                     	<td class="bold withinput">
                                            <fmt:message key="aimir.arrearsB"/>
                                        </td>
                                        <td>
                                            <input type='text' id='currentArrears2U' name="currentArrears2U" style="width: 150px"/>
                                        </td>
                                        
                                        <td class="bold withinput">
											<fmt:message key='aimir.payment.contractCnt'/>
                                        </td>                      
                                        <td>
                                            <input type='text' id='arrearsContractCountU' name="arrearsContractCountU" style="width: 180px"/>
                                        </td>
                                    </tr>
                                    
                                    <tr id="prepaymentStatusTr4" style="display: none;">
                                    	<td class="bold withinput">
                                    		<fmt:message key='aimir.debtType'/>
                                    	</td>
                                    	<td>
                                    		<span>
                                                <select id="debtTypeU">
                                                </select>
                                            </span>
                                    	</td>
                                    	<td id="debtAmountTitleU" class="bold withinput">
                                    		<fmt:message key='aimir.debtAmount'/>
                                    	</td>
										<td>                                    	
                                    		<input type='text' id='debtAmountU' name='debtAmount' style='width: 150px' readonly/>
                                    	</td>
                                    	<td id = "debtContractCntTitleU"class="bold withinput">
                                    		<fmt:message key='aimir.debtContractCnt'/>
                                    	</td>
                                    	<td>
                                    		<input type='text' id='debtContractCntU' name='debtContractCntU' style='width: 180px' onchange="changeDebtContractCntU();"/>
                                    		<input type='hidden' id='debtPaymentCntU' name='debtPaymentCntU'/>
                                    	</td>
                                    </tr>
                                    
                                    <tr id="prepaymentStatusTr4_Sub" style="display: none;">
                                    	<td colspan="6">
                                    		<table id="debtSaveInfoU">
                                    		</table>
                                    	</td>
                                    </tr>

                                    <tr id="prepaymentStatusTr5">
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.charging" />
                                        </td>
                                        <td>
                                            <input name="chargeAvailable" id="chargeAvailableU1" type="radio" value="1" class="trans"/>
                                            <input type="text" value="<fmt:message key="aimir.allow"/>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/>
                                            <input name="chargeAvailable" id="chargeAvailableU0" type="radio" value="0" class="trans"/>
                                            <input type="text" value="<fmt:message key="aimir.reject"/>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/>
                                        </td>
                                        
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.paymentstat" />
                                        </td>
                                        <td >
                                            <span>
                                                <select id="creditStatusU">
                                                </select>
                                            </span>
                                        </td>
                                        <td class="bold withinput">
                                            <fmt:message key="aimir.barcode"/>
                                        </td>                      
                                        <td>
                                            <input type='text' name='barcode' style="width: 180px"/>
                                        </td>
                                        <!-- <td colspan="3"></td> -->
                                    </tr>

                                    <tr id="prepaymentStatusTr2" style="display:none;">
                                        <td colspan="7">
                                            <span class="withinput" style="font-weight: bold;">
                                                <fmt:message key="aimir.usingBalanceOfFee" />
                                            </span>
                                            <span >
                                                <input name="prepaymentThresholdU" id="prepaymentThresholdU" type="text" style="width: 100px"/>
                                                &nbsp;
                                            </span>
                                        </td>
                                    </tr>
                                    <tr id="prepaymentStatusTr3" style="display:none;">
                                        <td colspan="7">
                                            <span class="withinput" style="font-weight: bold;">
                                                %
                                                <fmt:message key="aimir.ifNoticeLessThen" />
                                            </span>
                                        </td>
                                    </tr>

                                </table>

                                <div id="treeDivUOuter" class="tree-billing auto" style="display:none;">
                                    <div id="treeDivU"></div>
                                </div>
                                <div id="treeDivSUOuter" class="tree-billing auto" style="display:none;">
                                    <div id="treeDivSU"></div>
                                </div>
                                <div id="meterDiv2" class="meterDiv2"></div>
                                
                                <!--업데이트 /캔슬 버튼-->
                                <div id="btn" class="contractUpdate" style="left: 680px;">
                                    <ul>
                                        <li class="input">
                                            <a id="contractUpdate" class="on">
                                                <fmt:message key="aimir.button.confirm" />
                                            </a>
                                        </li>
                                    </ul>
                                    <ul>
                                        <li class="input">
                                            <a id="contractInfoUpdateCancel" class="on">
                                                <fmt:message key="aimir.cancel" />
                                            </a>
                                        </li>
                                    </ul>
                                </div>

                            </li>

                        </ul>

                    </div>

                    <!-- 계약정보 수정 tab End -->

                    <!-- 계약정보 변경 이력 tab start -->
                    <div id="contractChangeLogTab" class="blueline bg-blue" style="height: 250px;">
                        <ul class="width">
                            <li class="padding">
                                <div id="period">
                                    <ul class="align">
                                        <li>
                                            <button id="oneMonthA" type="button" class="sm">
                                                <fmt:message key="aimir.thismonth" />
                                            </button>
                                            <button id="threeMonthA" type="button" class="sm">
                                                <fmt:message key="aimir.threemonths" />
                                            </button>
                                            <button id="sixMonthA" type="button" class="sm">
                                                <fmt:message key="aimir.sixmonths" />
                                            </button>
                                            <button id="oneYearA" type="button" class="sm">
                                                <fmt:message key="aimir.oneyear" />
                                            </button>
                                        </li>
                                    </ul>
                                    <ul class="align">
                                        <li>
                                            <input type="text" id="startYearA" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="startMonthA" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                                ~
                                            </span>
                                            <input type="text" id="endYearA" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="endMonthA" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                            </span>
                                        </li>
                                        <li>
                                            <div id="btn">
                                                <ul>
                                                    <li>
                                                        <a id="searchA" class="on">
                                                            <fmt:message key="aimir.button.search" />
                                                        </a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </li>
                                    </ul>
                                </div>

                                <div class="flexlist">
                                    <div id="changeLog"></div>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <!-- 계약정보 변경 이력 tab End-->


                    <!-- 선불 내역 tab start -->
    <!--    
            2014.12.29 simhanger
            더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.

                   <div id="paymentLogTab" class="blueline bg-blue" style="height: 250px;">
                        <ul class="width">
                            <li class="padding">

                                <div id="period">
                                    <ul class="align">
                                        <li>
                                            <button id="oneMonthB" type="button" class="sm">
                                                <fmt:message key="aimir.thismonth" />
                                            </button>
                                            <button id="threeMonthB" type="button" class="sm">
                                                <fmt:message key="aimir.threemonths" />
                                            </button>
                                            <button id="sixMonthB" type="button" class="sm">
                                                <fmt:message key="aimir.sixmonths" />
                                            </button>
                                            <button id="oneYearB" type="button" class="sm">
                                                <fmt:message key="aimir.oneyear" />
                                            </button>
                                        </li>
                                    </ul>
                                    <ul class="align">
                                        <li>
                                            <input type="text" id="startYearB" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="startMonthB" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                                ~
                                            </span>
                                            <input type="text" id="endYearB" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="endMonthB" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                            </span>
                                        </li>
                                        <li>
                                            <div id="btn">
                                                <ul>
                                                    <li>
                                                        <a id="searchB" class="on">
                                                            <fmt:message key="aimir.button.search" />
                                                        </a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </li>
                                    </ul>
                                </div>

                                <div class="flexlist">
                                    <div id="prepaymentLog" >
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>   -->
                    <!-- 선불 내역 tab End -->


                    <!-- 월별 사용량 tab start -->
                    <div id="billingMonthTab" class="blueline bg-blue" style="height: 250px;">
                        <ul class="width">
                            <li class="padding">
                                <div id="period">
                                    <ul class="align">
                                        <li>
                                            <button id="oneMonthC" type="button" class="sm">
                                                <fmt:message key="aimir.thismonth" />
                                            </button>
                                            <button id="threeMonthC" type="button" class="sm">
                                                <fmt:message key="aimir.threemonths" />
                                            </button>
                                            <button id="sixMonthC" type="button" class="sm">
                                                <fmt:message key="aimir.sixmonths" />
                                            </button>
                                            <button id="oneYearC" type="button" class="sm">
                                                <fmt:message key="aimir.oneyear" />
                                            </button>
                                        </li>
                                    </ul>
                                    <ul class="align">
                                        <li>
                                            <input type="text" id="startYearC" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="startMonthC" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                                ~
                                            </span>
                                            <input type="text" id="endYearC" size="4" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.year" />
                                            </span>
                                            <input type="text" id="endMonthC" size="2" />
                                            <span class="withinput">
                                                <fmt:message key="aimir.month" />
                                            </span>
                                        </li>
                                        <li>
                                            <div id="btn">
                                                <ul>
                                                    <li>
                                                        <a id="searchC" class="on">
                                                            <fmt:message key="aimir.button.search" />
                                                        </a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                                <div id="fcChartDiv" style="z-index: 99999;">
                                    The chart will appear within this DIV. This text will be replaced by the chart.
                                </div>
                            </li>
                        </ul>
                    </div>
                    <!-- 월별 사용량 tab End -->

                </li>
            </ul>

        </div>

    <!-- (하단) 계약정보 Tab Contents Box (E) -->
    </div>

    <!-- 하단 계약정보 start -->
    <div id="contractAdd" style="display: none;">
        <div id="pane-Contract-Add" style="margin-top: 60px;"></div>
    </div>
    <!-- 하단 계약정보 End -->

</div>
<!-- (전체) Contract Tab (S) -->

<script>
customerExtTreeSearch();

function bottomContractStatusTab(){

    if(contractId == '' || contractId == 'undefined') {
        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.contract.select" />');
        return ;
    }

    $('#contractInfoUpdate').show();
    $('#contractInfoDetail').hide();

    $.getJSON('${ctx}/gadget/system/customerMax.do?param=contractInfoUpdateForm', {contractId: contractId , serviceType: tempServiceType, customerNo:$("#customerNo").val()},

        function(json)
        {
            var serviceList = json.serviceList;
            var serviceListArr = Array();
            for (var i = 0; i < serviceList.length; i++) {
                var obj = new Object();
                obj.name=serviceList[i].descr;
                obj.id=serviceList[i].id;
                serviceListArr[i]=obj;
            };
            $("#serviceTypeCodeU").pureSelect(serviceListArr);
            if (json.service != null) {
                $("#serviceTypeCodeU option[value=" + json.service.id + "]").attr("selected", "true");
            }
            //공급지역
           // $("#locationU").pureSelect(json.locationList);
           // if (json.location != null) {
            //    $("#locationU option[value=" + json.location.id + "]").attr("selected", "true");
            //}
            //계약종별
            $("#tariffIndexU").pureSelect(json.tariffTypeList);
            if (json.tariff != null) {
                $("#tariffIndexU option[value=" + json.tariff.id + "]").attr("selected", "true");
            }
            //계약용량
            if(json.contract.contractDemand == "" || json.contract.contractDemand == "null" || json.contract.contractDemand == null){
                $("#contractDemandU").attr("value" , "");
            }else{
                $("#contractDemandU").attr("value" , json.contract.contractDemand);
            }
            //공급상태
            var statusList = json.statusList;
            var statusListArr = Array();
            for (var i = 0; i < statusList.length; i++) {
                var obj = new Object();
                obj.name=statusList[i].descr;
                obj.id=statusList[i].id;
                statusListArr[i]=obj;
            };
            $("#statusU").pureSelect(statusListArr);
            if (json.status != null) {
                $("#statusU option[value=" + json.status.id + "]").attr("selected", "true");
            }

            //서비스 요청 타입
            $("#serviceType2U").pureSelect(json.serviceType2List);
            if (json.contract.serviceType2 != null) {
                $("#serviceType2U option[value=" + json.contract.serviceType2 + "]").attr("selected", "true");
            }
            
            //지불타입
            $('option', $('#creditTypeU')).remove();
            $.each(json.creditTypeList, function(index, creditType){
                $('#creditTypeU').append("<option value='"
                    +creditType['id'] + "' id='"+creditType['code']+"' "
                    +">"+creditType['descr']+"</option>");
            });
            $('#creditTypeU').selectbox();
            if (json.creditType != null) {
                $("#creditTypeU option[value=" + json.creditType.id + "]").attr("selected", "true");
            }
            $("#creditTypeU").change(function(value) {
                //var selectedText = document.getElementById('creditTypeU').options[document.getElementById('creditTypeU').selectedIndex].text;
                var selectedText = $("#creditTypeU option:selected").get(0).id;
                if (selectedText == '2.2.1'||selectedText == '2.2.2') { //prepay
                    $('#prepaymentStatusTr').show();
                } else {
                    $('#prepaymentStatusTr').hide();
                }
            });

            $("#creditTypeU").change();

            //지불타입이 선불일때 : 선불 2.2.1
            var creditStatusList = json.creditStatusList;
            var creditStatusArr = Array();
            for (var i = 0; i < creditStatusList.length; i++) {
                var obj = new Object();
                obj.name=creditStatusList[i].descr;
                obj.id=creditStatusList[i].id;
                creditStatusArr[i]=obj;
            };
            $("#creditStatusU").pureSelect(creditStatusArr);
            if (json.creditType != null) {
                var creditType = json.creditType.code;
                $("input[name=barcode]").val($("#barcode").val());

                if (creditType == "2.2.0") {        // 후불
                    $("#creditStatusU option:eq(0)").attr("selected", "true");
                    $("#prepaymentThresholdU").val("");
                    $("#currentArrearsU").val("");
                    $("input:radio[name='chargeAvailable']").removeAttr("checked");
                } else {                            // 선불/Emergency Credit
                    // 지불상태
                    if (json.creditStatus != null) {
                        $("#creditStatusU option[value=" + json.creditStatus.id + "]").attr("selected", "true");
                    } else {
                        $("#creditStatusU option:eq(0)").attr("selected", "true");
                    }

                    // 잔액최소임계치
                    var prepaymentThreshold = json.contract.prepaymentThreshold;
                    if (prepaymentThreshold == null || prepaymentThreshold == "null" || prepaymentThreshold == '"null"') {
                        $("#prepaymentThresholdU").val("");
                    } else {
                        $("#prepaymentThresholdU").val(prepaymentThreshold);
                    }

                    $("#currentArrearsU").val(json.contract.currentArrears);

                    var chargeAvailable = json.contract.chargeAvailable;
                    if (chargeAvailable != null && chargeAvailable != "") {
                        if (chargeAvailable == "true") {
                            $("input:radio[name='chargeAvailable']:radio[value='1']").attr("checked", true);
                        } else {
                            $("input:radio[name='chargeAvailable']:radio[value='0']").attr("checked", true);
                        }
                    } else {
                        $("input:radio[name='chargeAvailable']").removeAttr("checked");
                    }
                }
            } else {
                $("#creditStatusU option:eq(0)").attr("selected", "true");
                $("#prepaymentThresholdU").val("");
                $("#currentArrearsU").val("");
                $("input:radio[name='chargeAvailable']").removeAttr("checked");
            }

            // mdsID
            $('#meterMdsIdU').val($('#meterMdsId').val());

            //css
            $("#serviceTypeCodeU").selectbox();
            $("#serviceType2U").selectbox();
            $("#tariffIndexU").selectbox();
            $("#statusU").selectbox();
            $("#creditTypeU").selectbox();
            $("#creditStatusU").selectbox();

            // ApplyDate
            if (json.applyDate != null && json.applyDate != "") {
                var obj = $("#startDateTime");
                modifyDateLocal(json.applyDate, obj);
            } else {
                $("#startDateTime").val("");
                $("#startDateTimeHidden").val("");
            }

            // Threshold
            $("#threshold").val(json.usageThreshold);
            // Meter Grid 조회
              meterSearch();
        });
    }

</script>
</body>
</html>

