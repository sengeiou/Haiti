<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://www.springmodules.org/tags/commons-validator" prefix="v" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set var="datePattern"><fmt:message key="date.format"/></c:set>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="url" value="${pageContext.request.requestURL}"/>
<c:set var="localPort" value="${pageContext.request.localPort}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="aimir.customerview"/></title>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
    </style>

<script>

    var customerNo_ = $("#customerNo").val();
    var supplier = '';
    var initArrears = $("#initArrears").val();
    var initCurrentBalance = $("#initCurrentBalance").val();
	var initAlertBalance = $("#initAlertBalance").val();
    
    //Contract.meter_id
    var ContractMeterId = "";
    var meter_id = "";
    var contractNumber = "";
    var curPage = 0;
	var checkContractedMeterCnt = 0;
	
	var debtSaveArrA = new Array();

    // Meter ID
    var addContractSearchMdsId = "";
    var addContractSearchGs1 = "";

    //매터 그리드 관련 프로퍼티
    var addContractMeterGridOn = false;
    var addContractMeterGrid;
    var addContractMeterColModel;
    //var addContractMeterCheckSelModel;

    //매터 리스트 가져오기
    function getAddContractMeterGridList() {
        var width = $("#meterDiv").width();
        var rowSize = 3;

        var addContractMeterStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: rowSize}},
            url: "${ctx}/gadget/contract/getMeterGridList.do",
            baseParams: {
                mdsId : addContractSearchMdsId,
                gs1 : addContractSearchGs1
            },
            // totol count value
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
        addContractMeterColModel = new Ext.grid.ColumnModel({
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

        if (addContractMeterGridOn == false) {
            addContractMeterGrid = new Ext.grid.GridPanel({
                store: addContractMeterStore,
                colModel : addContractMeterColModel,
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
                    rowclick: addContractMeterRowClickEvent
                },
                renderTo: "meterDiv",
                viewConfig: {
                    //forceFit:true,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: rowSize,
                    store: addContractMeterStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            addContractMeterGridOn = true;
        } else {
            //미터 그리드 reconfig
            var bottomToolbar = addContractMeterGrid.getBottomToolbar();
            addContractMeterGrid.reconfigure(addContractMeterStore, addContractMeterColModel);
            bottomToolbar.bindStore(addContractMeterStore);
        }

        hide();
    };//func getMeterList End

    //meterId Click Event 리스너
    function addContractMeterRowClickEvent(grid, rowIndex, e) {
        var s= grid.getSelectionModel();
        var row = s.getSelected();

        var mdsId = row.get('mdsId');
        var gs1 = row.get('gs1');

        $("#mdsId").val(mdsId);
        $("#gs1").val(gs1);
    }

    //html onload func
    $(document).ready(function() {
    	//분할납부 기능을 사용하지 않을 때 분할납후 횟수를 입력할 수 없도록 함
    	if(isPartpayment == 'false' || isPartpayment == false) {
        	$('#arrearsContractCount').replaceWith($('#arrearsContractCount').clone().attr('type','hidden'));
        	$('#debtContractCntA').replaceWith($('#debtContractCntA').clone().attr('type','hidden'));
        }
    	
        $("#checkYN1").val('false');
        contractAdd();
        $("#pane-Contract-Add").show();
        $("#tariffIndex").hide();
        $("#contractAddTab").subtabs();

        // Contract Number 수정 시 이벤트 생성
        $('#contractNumber2').bind('keyup change',function(event) {
        	$('#checkYN1').val("false"); 
        });

        addContractSearchMdsId = "";
        addContractSearchGs1 = "";
        $("#meterDiv").show();
        getAddContractMeterGridList();
    });

    //계약추가버튼
    function contractAdd() {
        supplier = $("#supplier").val();

        $.getJSON('${ctx}/gadget/system/customerMax.do?param=serviceType', {supplier:supplier, customerNo:customerNo_},
            function(json) {
                //서비스
                $("#serviceTypeCode").pureSelect(json.energyList);
                $("#serviceTypeCode").selectbox();
                //공급지역
                //$("#location").pureSelect(json.location);
                //$("#location").selectbox();
                //공급상태
                $("#status").pureSelect(json.status);
                $("#status").selectbox();
                //지불타입
                $("#creditType").pureSelect(json.creditType);
                $("#creditType").selectbox();
                
                //지불상태
                $("#creditStatusA").pureSelect(json.creditStatus);
                $("#creditStatusA").selectbox();
                
                //serviceType2 상태
                $("#serviceType2").pureSelect(json.serviceType2);
                $("#serviceType2").selectbox();

                $("#tariffIndex").pureSelect(json.tariffIndex);
                $("#tariffIndex").selectbox();

                $("#contractCustomerNo").val( customerNo_ );

                var debtInfoList = json.debtInfoList;
                var debtInfoArr = Array();
                var debtInfoSizeA = debtInfoList.length;
                for (var i = 0; i < debtInfoSizeA; i++) {
                    var obj = new Object();
                    obj.name=debtInfoList[i].debtType;
                    obj.id=debtInfoList[i].debtRef;
                    debtInfoArr[i]=obj;
                };

                if(debtInfoSizeA == 0 ) {
                	$('#debtTypeA').pureSelect(debtInfoArr);
                	$("#debtTypeA").selectbox();
                	$('#debtAmountA').val("-");
                	$('#debtContractCntA').val("-");
                	$('#debtTypeA').attr("readOnly","readOnly");
                	$('#debtContractCntA').attr("readOnly","readOnly");
                	$('#debtAmountTitleA').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",""));
                	$('#debtAmountTitleA').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",""));
                } else {
                	$('#debtTypeA').attr("readOnly",false);
                	$('#debtContractCntA').attr("readOnly",false);
                	
                	$('#debtTypeA').pureSelect(debtInfoArr);
                	$("#debtTypeA option[value=" + debtInfoList[0].debtType + "]").attr("selected", "true");
                    $("#debtTypeA").selectbox();
                    
                    $('#debtAmountTitleA').html("<fmt:message key='aimir.debtAmount'/>".replace("$DEBTTYPE",debtInfoList[0].debtType)); 
                    $('#debtAmountA').val(debtInfoList[0].debtAmount);
                    
                    $('#debtContractCntTitleA').html("<fmt:message key='aimir.debtContractCnt'/>".replace("$DEBTTYPE",debtInfoList[0].debtType));
                    $('#debtPaymentCntA').val(debtInfoList[0].debtPaymentCount);
                    if(isPartpayment == 'true' && debtInfoList[0].debtPaymentCount != null && debtInfoList[0].debtPaymentCount >= 0 &&
                    		debtInfoList[0].debtContractCount != null && debtInfoList[0].debtContractCount != '') {
                    	$('#debtContractCntA').val(debtInfoList[0].debtContractCount);
                    } else {
                    	$('#debtContractCntA').val("-");
                    }
                    
                    $("#debtTypeA").change(function(value) {
                        changeDebtA();
                    });

                    $("#debtTypeA").change();
                }

            }
        );
        locationTreeGoGo('treeDivADD', 'location', 'locationADD');
    }

    function getTariffList(serviceType) {
        supplier = $("#supplier").val();

        $.getJSON('${ctx}/gadget/system/customerMax.do?param=getTariffList', {serviceType:serviceType, supplier:supplier},
            function(json) {
                $("#tariffIndex").pureSelect(json.tariff);
                $("#tariffIndex").selectbox();
            }
        );
        $("#tariffIndex").show();
    }

	//#프로퍼티
	//계약된 메터카운트 
	contractedMeterCnt= "";

    $("#addContract").click(function() {
        //계약번호
        if ( $("#contractNumber2").val() == "" ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.enterContractNo"/>");      // 계약번호를 입력해 주세요.
            $("#contractNumber2").focus();
            return;
        }

        var checkYN = $("#checkYN1").val();
        if (checkYN != "true") {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.msg.chkvalidationcontractno"/>");    // 계약번호 유효성 체크를 해주십시오.
            $("#contractNumber2").focus();
            //$("#contractNumber2").val('');
            $("#contractNumber2").select();
            return;
        } else {
            contractNumber = $.trim($("#contractNumber2").val()); 
        }

        //계약종별
        if ( $("#tariffIndex").val() == null  || $("#tariffIndex").val() == '') {
        	Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.validation.tariff'/>");
            $("#tariffIndex").focus();
            return;
        }

        // 미터 아이디
        if ( $("#mdsId").val() == "" ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.inputMeterid"/>");       // 미터 아이디를 입력해 주세요.
            $("#mdsId").focus();
            return;
        }

        // 선택한 Meter 가 다른 contract 에 연결되어있는지 체크
        var params = {
                meterNo : $("#mdsId").val(),
                contractId : ($("#contractId").val() != null) ? $("#contractId").val() : ""
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

        //if ($("#prepaymentThreshold").val() == "" ) {
        //    $("#prepaymentThreshold").val(0);
        //}
        //서비스
        if ( $("#serviceType").val() == 0 ) {
            var str2 = "<fmt:message key="aimir.selectService"/>";      // 서비스를 선택 해 주세요
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',str2);
            $("#serviceType").focus();
            return;
        }
        //공급지역
        if ( $("#location").val() == 0 ) {
            var str3 = "<fmt:message key="aimir.supplySelectArea"/>";       // 공급지역을 선택 해 주세요
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.supplySelectArea"/>");       // 공급지역을 선택 해 주세요
            $("#location").focus();
            return;
        }
        
        //공급상태
        if ( $("#status").val() == 0 ) {
            var str6 = "<fmt:message key="aimir.selectService"/>";      // 서비스를 선택 해 주세요
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',str6);
            $("#status").focus();
            return;
        }

        //지불타입
        if ( $("#creditType").val() == 0 ) {
            var str7 = "<fmt:message key="aimir.selectPayment"/>";      // 지불타입을 선택 해 주세요
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',str7);
            $("#creditType").focus();
            return;
        }

        var isReturn = false;
        $.ajax({
            type : "POST",
            async : false,
            data: {codeId:$('#creditType').val()},
            dataType: "json",
            url:'${ctx}/gadget/system/customerMax.do?param=getCreditType',
            success:function(json,status) {
                if(isPartpayment == 'true') {
                if ( json.creditType.code == "2.2.1" ) {
                    //숫자가 아닐경우.
                    if(isNaN($("#currentArrearsB").val())) {
                        $("#currentArrearsB").val('');
                        $("#currentArrearsB").focus();
                        isReturn=true;
                    }

                    if(isNaN($("#currentArrearsA").val())) {
                        $("#currentArrearsA").val('');
                        $("#currentArrearsA").focus();
                        isReturn=true;
                    }

                    if(isNaN($("#arrearsContractCount").val()) 
                    		|| (($("#arrearsContractCount").val() != null && $("#arrearsContractCount").val() != '') 
                    				&& $("#arrearsContractCount").val() <= 0)) {
                        $("#arrearsContractCount").val('');
                        $("#arrearsContractCount").focus();
                        isReturn=true;
                    }

                    if(getArrearsPaymentCount != null && getArrearsPaymentCount != "" && getArrearsPaymentCount != 0
                    		&& Number(getCurrentArrears) != Number($('#currentArrearsA').val())) {
                        //납부한적이 한번이라도 있다면 수정할 수 없음
                        Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.validation.noModify.arrears'/>");
                        isReturn=true;
                    }
                    
                    if(getArrearsPaymentCount != null && getArrearsPaymentCount != "" && getArrearsPaymentCount != 0
                    		&& Number($('#arrearsContractCount').val()) != Number(getArrearsContractCount)) {
                        //납부한적이 한번이라도 있다면 수정할 수 없음
                        Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.validation.notModify.paymentContract'/>");
                        isReturn=true;
                    }

                  //미수금이 없는 경우(init Arrears 제외) 납부 계약 횟수를 수정할 수 없음.
                  if((Number(($('#currentArrearsA').val()) <= Number(initArrears) || ('#currentArrearsA').val() == null || $('#currentArrearsA').val() == ''))
                  && ($('#arrearsContractCount').val() > 0)) {
                    Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.arrears.initialCredit'/>");
                      isReturn=true;
                  }
                 }  else if(json.creditType.code == "2.2.0") {
                     $('#currentArrearsA').val('');
                     $('#currentBalance').val('');
                  }
                } else {
                    if(json.creditType.code == "2.2.0") {
                        $('#currentArrearsA').val('');
                         $('#currentBalance').val('');
                    }
                }
            }
        })
        
        if(isReturn) {
            return;
        }

        if(existContract && $("#currentBalance").val() != ''){
            Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.contract.choose.another'/>");
            $("#currentBalance").val('');
        }

        if($('input[name="chargeAvailable"]:checked').val() == 0 && $("#currentBalance").val() != ''){
            if (isNaN($("#currentBalance").val())) {
                $("#currentBalance").val('');
                return;
            }

            Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.contract.choose.worng'/>");
            return;
        }

        if (existContract) {        // update contract
            $.ajax({
                type:"POST",
                data: {
                    "mdsId" : $("#mdsId").val(),
                    "gs1" : $("#meterGs1U").val(gs1),
                    "contractNumber" : $.trim($("#contractNumber2").val()),
                    "serviceTypeCode" : $("#serviceTypeCode").val(),
                    "tariffIndex" : ($("#tariffIndex").val() != null) ? $("#tariffIndex").val() : "",
                    "locationId2" : $("#locationADD").val(),
                    "contractDemand" : $("#contractDemand").val(),
                    "status" : $("#status").val(),
                    "creditType" : $("#creditType").val(),
                    "creditStatus" : ($("#creditStatusA").val() != null) ? $("#creditStatusA").val() : "",
                    "prepaymentThreshold" : ($("#prepaymentThreshold").val() != null) ? $("#prepaymentThreshold").val() : "",
                    'id' : $("#contractId").val(),
                    'customerId' : $("#customer").val(),
                    'fromInsert' : "true",
                    'serviceType2' : ($("#serviceType2").val() != null) ?  $("#serviceType2").val() : "",
                    'threshold1' : $('#threshold1A').val(),
                    'threshold2' : $('#threshold2A').val(),
                    'threshold3' : $('#threshold3A').val(),
                    'currentArrears' : $('#currentArrearsA').val(),
                    'currentArrears2' : $("#currentArrearsB").val(),
                    'arrearsContractCount' : $('#arrearsContractCount').val(),
                    'chargeAvailable' : $('input[name="chargeAvailable"]:checked').val(),
                    'preMdsId' : $.trim($("#preMdsIdA").val()),
                    "isPartpayment" : isPartpayment,
                    "initArrears" : initArrears
                },
                dataType:"json",
                url:'${ctx}/gadget/system/customerMax.do?param=updateContract',
                success : contractAddResult,
                error:function(request, status) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"contractUpdate ajax comm failed");
                }
            });// ajaxEnd
        } else {        // insert contract
            if($("#currentBalance").val() != '' && $("#currentBalance").val() != null){  // 충전할 Current Balance가 있을 경우            	
        			var options = {			        
        			        url : '${ctx}/gadget/system/customerMax.do?param=createContract',
        			        type : 'post',
        			        data:{
        			              "contractNumber" : $.trim($("#contractNumber2").val())
                                , "currentbalanceValue" : $("#currentBalance").val()
                                , "preMdsId"	: $.trim($("#preMdsIdA").val())
                                , "isPartpayment" : isPartpayment
                                , "debtSaveInfo" : JSON.stringify(debtSaveArrA)
        			        },
        			        datatype : 'json',
                            success : contractAddResult                          
        			};
                    $('#contractForm').ajaxSubmit(options);            		
            //    }       
            
            }else { 

                var options = {
                    success : contractAddResult,
                    //저장 프로세스
                    url : '${ctx}/gadget/system/customerMax.do?param=createContract',
                    type : 'post',
                    data:{
                        "contractNumber" : $.trim($("#contractNumber2").val())
                        , "preMdsId"	: $.trim($("#preMdsIdA").val())
                        , "isPartpayment" : isPartpayment
                    },
                    datatype : 'json'
                };
                $('#contractForm').ajaxSubmit(options);
            }
        }
        
    });

    /* *********
    Current Balance 충전확인
   *********** */
   /*
   var eventHandler = {
     saveChargeAmount: function(paramValues, callBackFunc) {
       var params = {
         currentBalance: NumberUtil.parseNumber(paramValues)
       };        

       var saveAction = function(cValue) {
         Ext.Msg.confirm("<fmt:message key='aimir.message'/>"
        		 , "<fmt:message key='aimir.wouldSave'/>"
        		 , function(btn) {
		             if (btn == "yes") {
		               emergePre();
		               callBackFunc(cValue);
		               hide();
		             }
         });
       }

       eventHandler.validateSaveAction(params, saveAction);
     },
     
     validateSaveAction: function(params, callback) {
       var currentBalance = params.currentBalance;        
       if (isNaN(currentBalance)) return;        // 숫자가 아닌 경우
       if(currentBalance) {         
         eventHandler.retypePaidAmount({
           title: 'Current Balance',
           msg: "<fmt:message key='aimir.retype.amount'/>",
           amount: currentBalance,
           callback: function(prompt) { 
               callback(prompt);
           }
         });
       }
     },

     retypePaidAmount: function (params) {
       Ext.Msg.show({
         title: params.title,
         msg: params.msg,
         buttons: Ext.MessageBox.OKCANCEL,
         prompt: true,
         fn: function(btn, text) {
           var prompt = Number(text);
           if (btn == 'ok' && !isNaN(params.amount)) {
             if(prompt != params.amount) {
               Ext.Msg.alert("<fmt:message key='aimir.alert'/>", "<fmt:message key='aimir.msg.check.input.value'/>");
               return;
             } else { 
               params.callback(prompt);
             }
           } 
         }
       });        
     }

   }        
    */

    function contractAddResult(responseText, status) {
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);
        reset();
        customerSearchAll();  //계약 추가후 조회
    }


    //계약된 미터 아이디 인지 여부를 체크
    /*function checkContractedMeterYn()
    {
    	 $.ajax({
	            type:"POST",
	            data:{"meterId":ContractMeterId},
	            dataType:"json",
	            
	            //계약된 미터 아이디 인지 여부를 체크 action
                url : '${ctx}/gadget/system/checkContractedMeterYn.do',
	            success:function(data, status) 
	            {
             		//data.result  >0 보다 크면 계약이 되어있는 미터
             		//checkContractedMeterCnt= data.result;
             		
             		contractedMeterCnt= data.result;
	            },
	            error:function(request, status) 
	            {
	            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"checkContractedMeterYn async comm failed");
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
	            }
	        });
    }*/

    //Contract 취소버튼
    $("#contractCancel").click(function() {
    	$("#meterDiv").hide();
    	$("#meterDiv2").hide();

    	//MeterGridOn=false;

        $("#pane-Contract-Add").hide();
        $("#pane-Energy").show();
        
        $('#debtSaveInfoA').html('');
        $('#pane-creditType-prepay2_Sub').css("display","none");
        $('#contractInfoTabA').height(270);
        customerDetail();
    });

    //js 계약 번호 중복체크
    /*$("#numberOverlapCheck").click(function() {
        var contractNumber = $("#contractNumber2").val();

        if ( contractNumber == "" || contractNumber.length == 0 ) {
            var str9 = "<fmt:message key="aimir.enterContractNo"/>";//계약번호를 입력해 주세요
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',str9);
            return;
        }
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=numberOverlapcheck', { contractNumber:contractNumber },
            function(json) {
                if ( json.count == 0 ) {
                    $("#checkValue1").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                    $("#checkValue1").show();
                } else {
                    $("#checkValue1").html("<ul><li class='reject'><fmt:message key='aimir.duplicateContractNo'/></li></ul>");
                    $("#checkValue1").show();
                    $("#contractNumber").val('');
                    $("#contractNumber").focus();
                }
                $("#checkYN1").val(json.checkYN);
            }
        );
    });*/

    var existContract = false;
    //계약 번호 체크
    $("#numberOverlapCheck").click(function() {
        var contractNumber = $.trim($("#contractNumber2").val());
        $("#contractNumber2").val(contractNumber);

        if ( contractNumber == "" || contractNumber.length == 0 ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.enterContractNo"/>");    // 계약번호를 입력해 주세요
            return;
        }
        $.getJSON('${ctx}/gadget/system/customerMax.do?param=getCheckContractNumber', { contractNumber:contractNumber },
            function(json) {
                var result = json.result;
                var checkYN = "false";
                existContract = false;
                if (result != null) {
                	if (result.exist == "true") {
                		if (result.linked == "true") {
                			if (confirm("<fmt:message key="aimir.contract.msg.existlinkedcontract"/>\n\n<fmt:message key='aimir.contract.currentlinkedcustomer'/> : " + result.customerName)) {
                                // 해당 contract 정보를 입력
                                inputAddContractInfo(result);
                                existContract = true;
                                checkYN = "true";
                                $("#checkValue1").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                                $("#checkValue1").show();
                			} else {
                				checkYN = "false";
                				$("#contractId").val("");
                                $("#checkValue1").html("<ul><li class='reject2'><fmt:message key='aimir.msg.invalidcontractno'/></li></ul>");
                                $("#checkValue1").show();
                                //$("#contractNumber2").val('');
                                $("#contractNumber2").select();
                                $("#contractNumber2").focus();
                			}
                		} else {
                            if (confirm("<fmt:message key="aimir.contract.msg.existcontract"/>")) {
                                // 해당 contract 정보를 입력
                                inputAddContractInfo(result);
                                existContract = true;
                                checkYN = "true";
                                $("#checkValue1").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                                $("#checkValue1").show();
                            } else {
                            	checkYN = "false";
                            	$("#contractId").val("");
                                $("#checkValue1").html("<ul><li class='reject2'><fmt:message key='aimir.msg.invalidcontractno'/></li></ul>");
                                $("#checkValue1").show();
                                //$("#contractNumber2").val('');
                                $("#contractNumber2").select();
                                $("#contractNumber2").focus();
                            }
                		}

                        // Current Balance 정보 hide
                        //$('#currentBalance').attr('disabled', true);
                        $('#currentBalanceText').hide();
                        $('#currentBalance').val('');
                        $('#currentBalance').hide();


                	} else {
                		checkYN = "true";
                		$("#contractId").val("");
                        $("#checkValue1").html("<ul><li class='available2'><fmt:message key='aimir.availableContractNo'/></li></ul>");
                        $("#checkValue1").show();

                        // Current Balance 정보 hide
                        //$('#currentBalance').attr('disabled', true);
                        $('#currentBalanceText').show();
                        $('#currentBalance').val(initCurrentBalance);
                        $('#currentBalance').show();                        
                	}
                } else {
                	checkYN = "false";
                	$("#contractId").val("");
                    $("#checkValue1").html("<ul><li class='reject2'><fmt:message key='aimir.erroroccured'/></li></ul>");
                    $("#checkValue1").show();
                    $("#contractNumber2").val('');
                    $("#contractNumber2").focus();
                }
                $("#checkYN1").val(checkYN);
            }
        );
    });

    function inputAddContractInfo(info) {
        $("#contractId").val(info.contractId);
        if (info.serviceType == "") {
            $("#serviceTypeCode option:eq(0)").attr("selected", "true");
        } else {
            $("#serviceTypeCode option[value=" + info.serviceType + "]").attr("selected", "true");
        }
        $("#serviceTypeCode").selectbox();
        $("#serviceTypeCode").trigger("change");

    	$("#mdsId").val(info.mdsId);
    	$("#gs1").val(info.gs1);
    	ContractMeterId = info.meterId;

        if (info.status == "") {
            $("#status option:eq(0)").attr("selected", "true");
        } else {
            $("#status option[value=" + info.status + "]").attr("selected", "true");
        }
        $("#status").selectbox();

        $("#locationADD").val(info.locationId);
        $("#location").val(info.locationName);

        $("#contractDemand").val(info.contractDemand);

        $("#amountPaidA").val(info.amountPaid);
        $("#receiptNoA").val(info.receiptNumber);

        if (info.serviceType2 == "") {
            $("#serviceType2 option:eq(0)").attr("selected", "true");
        } else {
            $("#serviceType2 option[value=" + info.serviceType2 + "]").attr("selected", "true");
        }
        $("#serviceType2").selectbox();

        if (info.creditType == "") {
            $("#creditType option:eq(0)").attr("selected", "true");
        } else {
            $("#creditType option[value=" + info.creditType + "]").attr("selected", "true");
        }
        $("#creditType").selectbox();
        $("#creditType").trigger("change");

        if (info.creditStatus == "") {
            $("#creditStatusA option:eq(0)").attr("selected", "true");
        } else {
            $("#creditStatusA option[value=" + info.creditStatus + "]").attr("selected", "true");
        }
        $("#creditStatusA").selectbox();

        $("#prepaymentThreshold").val(info.prepaymentThreshold);

        if (info.tariffType == "") {
            $("#tariffIndex option:eq(0)").attr("selected", "true");
        } else {
            $("#tariffIndex option[value=" + info.tariffType + "]").attr("selected", "true");
        }
        $("#tariffIndex").selectbox();
        $("#currentArrearsA").val(info.currentArrears);
        getCurrentArrears = info.currentArrears;
        
        $("#currentArrearsB").val(info.currentArrears2);
        $("#arrearsContractCount").val(info.arrearsContractCount);
        getArrearsContractCount = info.arrearsContractCount;
        
        getArrearsPaymentCount = info.arrearsPaymentCount;
        
        var chargeAvailable = info.chargeAvailable;

        if (chargeAvailable != null) {
            if (chargeAvailable == true) {
                $("input:radio[name='chargeAvailable']:radio[value='1']").attr("checked", true);
            } else {
                $("input:radio[name='chargeAvailable']:radio[value='0']").attr("checked", true);
            }
        } else {
            $("input:radio[name='chargeAvailable']").removeAttr("checked");
        }


        // Threshold 추가
        $("#threshold1A").val(info.threshold1);
        $("#threshold2A").val(info.threshold2);
        $("#threshold3A").val(info.threshold3);






    }

    //미터 서치버튼 클릭 event
    $("#meterSearchButton").click(function(){
        addContractSearchMdsId = $("#mdsId").val();
        addContractSearchGs1 = $("#gs1").val();
     	//MeterGridOn= true;

     	//미터 리스트 가져오기.
        getAddContractMeterGridList();
    });


    function setDebtContractCntA() {
    	if(isNaN($('#debtContractCntA').val()) || 
    			(($("#debtContractCntA").val() != null || $("#debtContractCntA").val() != '') && $('#debtContractCntA').val() <= 0)) {
    		Ext.Msg.alert("<fmt:message key='aimir.message'/>","<fmt:message key='aimir.msg.onlydigit'/>");
    		$('#debtContractCntA').val('');
    		$('#debtContractCntA').focus();
    		return;
    	}

    	if($('#debtPaymentCntA').val() > 0) {
    		Ext.Msg.alert("<fmt:message key='aimir.message'/>","["+$('#debtTypeA option:selected').text()+"]: <fmt:message key='aimir.cannot.modify.debtPaymentCnt.already'/>");
    		return;
    	}
    	
    	var debtSubKey=$('#debtTypeA option:selected').val();
    	
    	//동일한  DebtType에 대한 수정 값이 존재 할 경우 마지막에 수정한 값으로 덮어씌움. 
    	deleteDebtSubA(debtSubKey);

 		$('#debtSaveInfoA').append("<tr id=debtSaveInfoA_" + debtSubKey + ">" + 
 								  "<td class='bold withinput'><fmt:message key='aimir.debtType'/></td><td id='debtSubTypeA_" + debtSubKey + "' style='width:150px'>" + $('#debtTypeA option:selected').text() + "</td>" + 
  								  "<td class='bold withinput'><fmt:message key='aimir.debtAmount2'/></td><td id='debtSubAmountA_" + debtSubKey + "' style='width:150px'>" + $('#debtAmountA').val() + "</td>"+
    							  "<td class='bold withinput'><fmt:message key='aimir.debtContractCnt2'/></td><td id='debtSubContractCntA_" + debtSubKey +"' style='width:130px'>" + $('#debtContractCntA').val() + "</td>" + 
    							  "<td class='bold withinput'><div id='btn'><ul><li><a href='javascript:;' onClick='javascript:deleteDebtSubA(\""+debtSubKey+"\");' class='on'><fmt:message key='aimir.bemsfacilityMgmt.delete'/></a></li></ul></div></td>" + 
    							  "</tr>");2
 		$('#pane-creditType-prepay2_Sub').css("display","");
 		var tempDebtArr =  {
 				debtType : $('#debtTypeA option:selected').text(),
 				debtContractCnt : $('#debtContractCntA').val(),
 				debtRef : debtSubKey,
 				customerNo : $('#customerNo').val(),
 				debtAmount : $('#debtAmountA').val()
 	 		};
 			
 		debtSaveArrA.push(tempDebtArr);
 		$('#contractInfoTabA').height($('#contractInfoTabA').height() + 28);
    }
    
    function deleteDebtSubA(debtSubKey) {
    	if($('#debtSaveInfoA_' + debtSubKey).length != 0) {
    		$('#debtSaveInfoA_' + debtSubKey).remove();
    		for(var i=0; i < debtSaveArrA.length; i++) {
    			if(debtSaveArrA[i].debtRef == debtSubKey) {
    				debtSaveArrA.pop(i);
    			}
    		}
    		$('#contractInfoTabA').height($('#contractInfoTabA').height() - 28);	
    	}
    	
    	if(!($('#debtSaveInfoA').html().includes("tr"))) { 
			debtSaveArrA = new Array();    		
    		$('#pane-creditType-prepay2_Sub').css("display","none");
    	}
    }
    
</script>
</head>
<body>
    <form id="contractForm" name="contractForm" method="post">
        <input type="hidden" name="customer" id="customer" value="${customerId}" />
        <input type="hidden" name="customerNo" id="customerNo" value="${customerNo}" />
        <input type="hidden" name="supplier" id="supplier" value="${supplierId}" />
        <input type="hidden" name="initArrears" id="initArrears" value="${arrears}" />
        <input type="hidden" name="initCurrentBalance" id="initCurrentBalance" value="${currentBalance}" />
        <input type="hidden" name="initAlertBalance" id="initAlertBalance" value="${alertBalance}" />
        <input type="hidden" name="id" id="contractId" />
        <input type="hidden" id="checkYN1" name="checkYN1" />

        <!-- (하단) 새 계약 추가 (S) -->
        <div id="contractAddTab">

            <div class="headspace">
                <label class="check"><fmt:message key="aimir.operator.contractStatus"/><!-- 계약현황 --></label>
            </div>

            <ul>
               <li><a href="#tabcontentsbox"><fmt:message key="aimir.contractInfo"/><!-- 계약정보--></a></li>
               <li class="nolink"><fmt:message key="aimir.contractChange"/><!-- 계약변경내역--></li>
               <li class="nolink"><fmt:message key="aimir.prepaidPlan"/><!-- 선불내역 --></li>
               <li class="nolink"><fmt:message key="aimir.monthly.usage"/><!-- 월별사용량--></li>
            </ul>

            <!-- (하단) 계약정보 Tab Contents Box (S) -->
            <div class="tabcontentsbox">
                <ul>
                    <li>

                        <!-- 세계약 추가에 계약 정보 탭 -->
                        <div id="contractInfoTabA" class="blueline bg-blue" style="height:270px; " >
                        
                        <ul class="width">
                        <li class="padding">

                            <table class="searchoption wfree" border=0 >

                                <tr>

                                    <td class="bold withinput"><fmt:message key="aimir.customerid"/><!-- 고객번호--></td>
                                    <td><input type="text" id="contractCustomerNo" style="width:150px" readonly="readonly"><!-- <div id=contractCustomerNo class="input-fake" style="width:153px;"></div> --></td>

                                    <td class="bold withinput"><fmt:message key="aimir.contractNumber"/><!-- 계약번호--></td>
                                    <td><div class="check-overlap-btn-contract">
                                        <span><input type="text" id="contractNumber2" name="contractNumber2" style="width:90px"></span>
                                        <span><div id="btn"><ul><li><a id="numberOverlapCheck" class="on"><fmt:message key="aimir.button.check"/></a></li></ul></div></span>
                                        </div>
                                        <div id="checkValue1" class="check-overlap check-overlap-contract" style="width:150px;"></div>
                                    </td>

                                    <!-- 계약종별-->
                                    <td class="bold withinput"><fmt:message key="aimir.contract.tariff.type"/></td>
                                    <td><select name="tariffIndex" id="tariffIndex" style="width:180px;"></select></td>

                                    <!-- <td class="space10">&nbsp;</td> -->
                                    
                                    <!-- Service Type -->
                                    <td class="bold withinput"><fmt:message key="aimir.service.type"/></td>
                                    <td><select name="serviceType2" id="serviceType2" style="width:215px;"></select></td>
                                    


                                </tr>
                                <tr><td class="bold withinput"><fmt:message key="aimir.supply.type"/><!-- 서비스--></td>
                                    <td><select name="serviceTypeCode" style="width:150px" id="serviceTypeCode" onchange="javascript:getTariffList(document.getElementById('serviceTypeCode').options[document.getElementById('serviceTypeCode').selectedIndex].value);"></select></td>


                                    <td class="bold withinput"><fmt:message key="aimir.supplystatus"/><!-- 공급상태--></td>
                                    <td><select name="status" id="status" style="width:150px;"></select></td>


                                    <td class="bold withinput"><fmt:message key="aimir.location.supplier"/><!-- 공급지역--></td>
                                    <td>
                                       <input   style="width:180px;" type="text" id="location" name="location.name" >
                                       <input type="hidden" id="locationADD" name="location.id" value="" />
                                    </td>
                                    
                                    
                                </tr>
<!--                            <tr><td class="bold withinput"><fmt:message key="aimir.contract.receioptNo"/></td>
                                    <td><input name="receiptNumber" style="width:150px" id="receiptNoA" type="text"/></td>
                                    
                                    <td class="bold withinput"><fmt:message key="aimir.contract.amountPaid"/></td>
                                    <td><input name="amountPaid" style="width:150px" id="amountPaidA" type="text"/></td>
                                    
                                    <td class="bold withinput"><fmt:message key="aimir.service.type"/></td>
                                    <td><select name="serviceType2" id="serviceType2" style="width:180px;"></select></td>
                                </tr>   -->
                                <!--  임계치 설정 -->
                                <tr>
<%--                                     <td class="bold withinput"><fmt:message key="aimir.threshold1"/></td>
                                    <td><input name="threshold1" style="width:150px" id="threshold1A" type="text"/></td>

                                    <td class="bold withinput"><fmt:message key="aimir.threshold2"/></td>
                                    <td><input name="threshold2" style="width:150px" id="threshold2A" type="text"/></td>

                                    <td class="bold withinput"><fmt:message key="aimir.threshold3"/></td>
                                    <td><input name="threshold3" style="width:180px" id="threshold3A" type="text"/></td> --%>
                                    
                                    <!-- 예전 미터 시리얼 -->
                                    <td class="bold withinput"><fmt:message key="aimir.preMeterid"/></td>
                                    <td >
                                        <input name="preMdsIdA" id="preMdsIdA" type="text" style="width:150px;"/>
                                    </td>
                                    <!-- meter id -->
                                    <td class="bold withinput"><fmt:message key="aimir.meterid"/></td>
                                    <td><input name="mdsId" id="mdsId" type="text" style="width:150px;"/></td>
                                        
                                    <td class="bold withinput"><fmt:message key="aimir.shipment.gs1"/></td>
                                    <td><input name="gs1" style="width:150px" id="gs1" type="text"/></td>
                                        
                                    <td >

                                    <!-- search button -->
                                    	<span class="am_button margin-l10 margin-t1px">
                                        <a id="meterSearchButton" href="#" class="on"><fmt:message key="aimir.button.search" /></a></span>
                                    </td>        
                                </tr>     
                                <tr>
                                    <td class="bold withinput"><fmt:message key="aimir.paymenttype"/><!-- 지불타입--></td>
                                    <td><select name="creditType" id="creditType" style="width:150px;" onchange="javascript:creditTypeSelect(document.getElementById('creditType').options[document.getElementById('creditType').selectedIndex].value);"></select></td>

                                    <td class="bold withinput"><fmt:message key="aimir.contract.demand.amount"/><!-- 계약용량--></td>
                                    <td><input name="contractDemand" style="width:180px" id="contractDemand" type="text"/></td>

                                </tr>

                                <tr id="pane-creditType-prepay1" style="display:none;">
                                    <td class="bold withinput"><fmt:message key="aimir.arrearsA"/><!-- 미수금 --></td>
                                    <td><input name="currentArrears" id="currentArrearsA" style="width:150px" type="text" value="${arrears}"/></td>
                                    <td class="bold withinput"><fmt:message key="aimir.arrearsB"/></td>
                                    <td><input name="currentArrears2" id="currentArrearsB" style="width:150px" type="text" value="${arrears2}"/></td>
                                    <td class="bold withinput"><fmt:message key="aimir.payment.contractCnt"/><!--지불납부횟수--></td>
                                    <td><input type="text" name="arrearsContractCount" id="arrearsContractCount" style="width:180px;"></td>
                                </tr>

								<tr id="pane-creditType-prepay2"style="display: none;">
									<td class="bold withinput"><fmt:message key='aimir.debtType'/></td>
                                    <td><select name="debtTypeA" id="debtTypeA" style="width:150px;"></select></td>
                                    <td id="debtAmountTitleA" class="bold withinput"><fmt:message key='aimir.debtAmount'/></td>
                                    <td><input name="debtAmountA" id="debtAmountA" style="width:150px" type="text" readonly/></td>
                                    <td id="debtContractCntTitleA" class="bold withinput"><fmt:message key='aimir.debtContractCnt'/></td>
                                    <td>
                                    	<input type="text" name="debtContractCntA" id="debtContractCntA" style="width:180px;" onchange="setDebtContractCntA();">
                                    	<input type="hidden" name="debtPaymentCntA" id="debtPaymentCntA"/>
                                    </td>
								</tr>
								
								<tr id="pane-creditType-prepay2_Sub" style="display: none;">
                                	<td colspan="6">
                                		<table id="debtSaveInfoA">
                                		</table>
                                	</td>
                                </tr>

                                <tr id="pane-creditType-prepay3" style="display:none;">
                                	<td class="bold withinput"><fmt:message key="aimir.charging"/><!-- 충전 --></td>        
                                    <td colspan="1">
                                        <input name="chargeAvailable" id="chargeAvailableA1" type="radio" value="1" class="trans" checked="checked"/>
                                        <input type="text" value="<fmt:message key="aimir.allow"/>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/>
                                        <input name="chargeAvailable" id="chargeAvailableA0" type="radio" value="0" class="trans"/>
                                        <input type="text" value="<fmt:message key="aimir.reject"/>" readonly="readonly" class="border-trans bg-trans" style="width:40px;"/>
                                    </td>                   
                                    <td class="bold withinput"><fmt:message key="aimir.paymentstatus"/> <!--지불상태--></td>
                                    <td><select name="creditStatus" id="creditStatusA" style="width:150px;"></select></td>
                                    <td id="currentBalanceText" class="bold withinput"><fmt:message key="aimir.prepayment.currentbalance"/></td><!-- 최초 충전금액 -->
                                    <td><input name="currentBalance" style="width:180px" id="currentBalance" type="text" value="${currentBalance}"/></td>           
                                </tr>

                                <tr id="pane-creditType-prepay4" style="display:none;">
                                    <td colspan="6">
                                        <span class="gray11pt withinput"><fmt:message key="aimir.usingBalanceOfFee"/><!-- 잔액이 사용요금의--> </span>
                                        <span><input name="prepaymentThreshold" id="prepaymentThreshold" type="text" value="${alertBalance}" style="width:50px"/>&nbsp;</span>
                                        <span class="gray11pt withinput"><fmt:message key="aimir.ifNoticeLessThen"/><!-- 이하인 경우 통보--></span>
                                    </td>
                                </tr>

                                <tr>
                                   <!-- 저장 취소 버튼 -->
                                    <td class="bold withinput" colspan=9>

                                        <div id="btn" class="btn-savecontract">
                                            <ul><li><a id="addContract" class="on"><fmt:message key="aimir.save2"/><!-- 저장 --></a></li></ul>
                                            <ul><li><a id="contractCancel" class="on"><fmt:message key="aimir.cancel"/><!-- 취소--></a></li></ul>
                                        </div>

                                    </td>
                                </tr>

                            </table>
                            <div id="treeDivADDOuter" class="tree-billing auto" style="display:none;">
                                <div id="treeDivADD"></div>
                            </div>
                            <div id="treeDivSIOuter" class="tree-billing auto" style="display:none;">
                                <div id="treeDivSI"></div>
                            </div>

                            <!--  meter list Div -->
                            <div id="meterDiv"  class="meterDiv"></div>

                        </li>
                        </ul>
                        </div>

                        <!-- (하단) 계약정보 Tab : 1ST (E) -->

                    </li>
                </ul>
            </div>
            <!-- (하단) 계약정보 Tab Contents Box (E) -->

        </div>
        <!-- (하단) 새 계약 추가 (S) -->

    </form>
</body>
</html>