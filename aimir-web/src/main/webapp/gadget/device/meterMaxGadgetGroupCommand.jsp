<%--
  @ Sejin-Han
  @ External Popup For MeterMaxGadget.jsp
  @ Date: 16-07-13
  @ If you want to make new group command,
        1. Add command name to #targetCommandCombo as selecebox option.
        2. Add parameter div to #commandParameter as dynamic(visible/invisible) element.


--%>

<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
         contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>MeterMaxGadget - Group Command</title>

    <!-- STYLE -->
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

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
        .x-form-cb-label{
    		margin-left:0px;
        }
        #targetTypeRadio{
        	text-align: center;
        }
         #targetTypeRadio input{
        	width:100%;
        }
        #ondemandTable{
        	margin : 30px;
        	width : calc(100% - 60px);
        	border-color : transparent;
        }
        .am_button{
       		margin-top: 10px;
       	}
    </style>

    <!-- LIB -->
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

</head>
<body>
<!-- SCRIPT -->
<script type="text/javascript" charset="UTF-8">
    // User(Role) Information
    var supplierId = "";
    var roleId = "";
    var loginId = "";
    var roleCommands = undefined;
    // Max Meter Limit
    var maxNumber = 0;
    // System Max Meter Limit
    var systemMaxLimit = "${systemMaxLimit}";
    // Trace <div> Changes
    var currentDiv = 1;
    var prevDiv = 0;
    // Decimal Format (grid)
    var numberFormat = "";
    // Command Summary
    var cpGroupName ="";
    var cpTargetNumber = 0;
    var cpCommandName = "";
    var cpValidationResult = "";
    // Command Running Function Name
    var targetService = "";
    // Group Ajax Handling
    var ajaxSuccessCount = 0;
    var ajaxFailCount = 0;
    var queueName = undefined;
    // Timeout setting (120 seconds)
    var extAjaxTimeout = 120000;
    // Target Meter Array
    var meterArray = new Array;
    // Command Parameters : 1. Ondemand
    var grpOndemandType;
    var grpOnDemandFromDate;
    var grpOnDemandToDate;
    // Command Parameters : 2. LimitPowerUsage
    var grpThresholdValue;
    // Command Parameters : 3. OTA

	var grpCmdDetail = new Array;
	var grpCmdWin2;
    
    // Document Ready
    $(document).ready(function(){
        var obj = window.opener.obj;

        // Get User Information
        supplierId = obj.supplierId;
        roleId = obj.roleId;
        loginId = obj.loginId;
        // Max number of meter that can be manipulated at the same time
        $.getJSON("${ctx}/gadget/system/user_group_max.do?param=myRoleView", { roleId: roleId } , function(json) {
            maxNumber = 0;
            if ( json.role != null ) {
                if ( json.role.maxMeters != null && isNaN(json.role.maxMeters) == false )
                    maxNumber = parseInt(json.role.maxMeters);
                if ( maxNumber==0 ) // maxMeters==0 is unlimited
                    maxNumber = 2000000;
                if ( json.myCommands != null )
                    roleCommands = json.myCommands;
            }
        });

        // Initialize GcStep Div
        currentDiv = 1;
        initGcStepDiv(currentDiv);

    })
	$( window ).resize(function() {
  		window.resizeTo(1416,936);
	});
    // PREV BUTTON
    function prevBtn(){
        if(currentDiv<=1 || prevDiv<=0){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','This is first page');
            return false;
        }
        currentDiv = prevDiv;
        prevDiv = prevDiv-1;
        <%--Ext.Msg.alert('<fmt:message key='aimir.message'/>','prev'+currentDiv);--%>
        changeGcStepDiv(currentDiv);
    }
    // NEXT BUTTON
    function nextBtn(){
        // Change Div
        if(currentDiv>=3){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','This is last page');
            return false;
        }

        // Set Summary & Set Group Parameter
        if(currentDiv==1){
            cpGroupName = $('#targetGroupCombo option:selected').text();
            $('#SelectedTargetGroupName').html(cpGroupName);

            // There are two grid
            if(targetListGridOn){
                cpTargetNumber = targetListStore.getTotalCount();
            }
            else if(chkTargetListGridOn){
                cpTargetNumber = chkTargetListStore.getTotalCount();
            }
            $('#SelectedTargetNumber').html(cpTargetNumber);

        }else if(currentDiv==2){
            cpCommandName = $('#targetCommandCombo option:selected').text();
            $('#SelectedCommandName').html(cpCommandName);

            // Set Command Parameter Values
            var setCP = setCommandParams(cpCommandName);
            // Check invalid Param
            if(!setCP){
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','Invalid Parameter');
                return false;
            }

            // check authorization of role (command execute)
            var checkAU =  checkAuthCommand(cpCommandName);
            if(!checkAU){
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','Your role have no permission.');
                return false;
            }else{
                console.log('permission ok : ' + cpCommandName + ', setCP : ' + setCP);
            }


        }else if(currentDiv==3){

        }

        // system limit
        if(cpTargetNumber > systemMaxLimit){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',
                    'The number of meter for Group command is limited on the web.'
                    + '<br>It follows <b>[System Limitation : ' + systemMaxLimit + ']</b>'
                    + '<br>It could be operated by scheduler.'
                    + '<br>Check the "HELP:SCHEDULER" at the top.');
            return false;
        }
        // compare max meter & target number
        if(cpTargetNumber > maxNumber){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.meterlimit'/>'
                            + '<br><b>[<fmt:message key='aimir.msg.currentConf'/> : ' + maxNumber + ']');
            return false;
        }else if(cpTargetNumber < 1){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.nometer'/>');
            return false;
        }

        // Jump
        prevDiv = currentDiv;
        var nextDiv = parseInt(currentDiv)%3+1;
        <%--Ext.Msg.alert('<fmt:message key='aimir.message'/>','next'+nextDiv);--%>
        currentDiv = nextDiv;
        initGcStepDiv(currentDiv);
    }
    // EXECUTE BUTTON
    function execBtn(){
        // check again -> compare max meter & target number
        if(cpTargetNumber > maxNumber){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.meterlimit'/>');
            return false;
        }

        if(targetService==""){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','command function is null');
        }else if(targetService == "onDemandService()"){
            setTimeout(targetService, 100);
        }
   		else{
            // captcha success action is connected by targetService
            CaptchaPanel();
        }
    }

    // Change Div Element (PREV Button)
    function changeGcStepDiv(stepNumber){
        $('div [name=GcStepDiv]').hide();
        $('ul [name=moveBtn]').hide();
        if(stepNumber==1) {
            $('#GcStep1').show();
            $('#prevBtn').show(), $('#nextBtn').show();
        } else if(stepNumber==2) {
            $('#GcStep2').show();
            $('#prevBtn').show(), $('#nextBtn').show();
        } else if(stepNumber==3) {

        } else {
            return false;
        }
    }

    // Setup Div (NEXT Button)
    function initGcStepDiv(stepNumber){


        $('div [name=GcStepDiv]').hide();
        $('ul [name=moveBtn]').hide();
       if(stepNumber==1){
           $('#GcStep1').show();
           $('#prevBtn').show(), $('#nextBtn').show();

           // SELECTBOX - Target Group
           $('#targetGroupCombo').selectbox();
           // GET AIMIRGROUP LIST
           $.getJSON('${ctx}/gadget/system/getMeterGroupBygroupId.do', {
               'supplierId' : supplierId,
               'groupType' : 'Meter'
           }, function(returnData) {
               var groupList = returnData.NAME;
               for(var v=0; v<groupList.length; v++){
                   $('#targetGroupCombo').append("<option value='"+groupList[v].id
                           +"'>[GROUP] "+groupList[v].name+"</option>");
               }
               $('#targetGroupCombo').selectbox();
               $('#targetGroupCombo').change(function(){changeTargetGroup()});
           });
           // Default Grid
           changeTargetGroup();

       } else if(stepNumber==2){
           $('#GcStep2').show();
           $('#prevBtn').show(), $('#nextBtn').show();

           /**
            * Select Box
            * Put your group-command name to element at bottom
            * Also put the parameter page
            */
           $('#targetCommandCombo').selectbox();
           $('#targetCommandCombo').change(function(){changeTargetCommand()});

           // Default Page
           changeTargetCommand();

       } else if(stepNumber==3){
           $('#GcStep3').show();
           $('#prevBtn').show(), $('#execBtn').show();

           // Draw grpCmdGrid
           drawGrpCmdGrid(cpCommandName);

       } else{
           return false;
       }
    }

    /**
     *  "#targetGroupCombo" Change Event Handler
     */
    function changeTargetGroup(){
        var baseParams;
        var condition = window.opener.obj.searchCondition;
        var selectedTarget = $('#targetGroupCombo option:selected').val();
        if(selectedTarget==0){
            // 0 : Current Search Condition
            baseParams = {
                    sMeterType         : condition[0],
                    sMdsId             : condition[1],
                    sStatus            : condition[2],
                    sMcuName           : condition[3],
                    sLocationId        : condition[4],
                    sConsumLocationId  : condition[5],
                    sVendor            : condition[6],
                    sModel             : condition[7],
                    sInstallStartDate  : condition[8],
                    sInstallEndDate    : condition[9],
                    sModemYN           : condition[10],
                    sCustomerYN        : condition[11],
                    sLastcommStartDate : condition[12],
                    sLastcommEndDate   : condition[13],
                    sOrder             : condition[14],
                    sCommState         : condition[15],
                    supplierId         : condition[16],
                    sMeterGroup        : condition[17],
                    sGroupOndemandYN   : 'N',
                    sCustomerId        : condition[18],
                    sCustomerName      : condition[19],
                    sPermitLocationId  : condition[20],
                    sMeterAddress      : condition[21],
                    sHwVersion         : "",
                    sFwVersion         : ""
            }
            drawTargetListGrid(baseParams);
        }else if(selectedTarget==1){
            // 1 : Checked Items
            meterArray = window.opener.obj.checkedItem;
            // arrayStore
            drawCheckedTargetListGrid();
        }else{
            // else : AIMIRGROUP
            baseParams = {
                sMeterType         : "",
                sMdsId             : "",
                sStatus            : "",
                sMcuName           : "",
                sLocationId        : "",
                sConsumLocationId  : "",
                sVendor            : "",
                sModel             : "",
                sInstallStartDate  : "",
                sInstallEndDate    : "",
                sModemYN           : "",
                sCustomerYN        : "",
                sLastcommStartDate : "",
                sLastcommEndDate   : "",
                sOrder             : "",
                sCommState         : "",
                supplierId         : condition[16],
                sMeterGroup        : selectedTarget,
                sGroupOndemandYN   : 'N',
                sMeterAddress      : "",
                sHwVersion         : "",
                sFwVersion         : ""
            }
            drawTargetListGrid(baseParams);
        }
    }

    /**
     *  Draw Target List Grid  with ajax
     */
    var targetListGridOn = false;
    var targetListStore;
    var targetListColumn;
    var targetListGrid;
    function drawTargetListGrid(baseParams){
        var grWidth = $('#GcStep1').width();
        var pageSize = 30;

        targetListStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: pageSize}},
            url : '${ctx}/gadget/device/getSimpleMeterSearchGrid.do',
            baseParams: baseParams,
            root : 'gridData',
            totalProperty : 'totalCnt',
            idProperty : 'no',
            listeners : {
                beforeload: function(store, options){
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                    });
                },load: function(store, record, options){
                    numberFoamat = store.reader.jsonData.mdNumberPattern;
                },
            },
            fields : [
                { name: 'no', type: 'string' },
                { name: 'meterId', type: 'string'},
                { name: 'meterMds', type: 'string' },
                { name: 'modemId', type: 'string' },
                { name: 'mcuId', type: 'string' },
            ]
        });

        targetListColumn = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true
            },
            columns: [{
                header: "<fmt:message key='aimir.number'/>",
                dataIndex: 'no',
                align:'center',
                width: 64,
                renderer: function(value, me, record, rowNumber, columnIndex, store) {
                    return Ext.util.Format.number(store.totalLength - value + 1, numberFormat);
                }
            },{
                header: "<fmt:message key='aimir.meterid'/>",
                dataIndex: 'meterMds',
                align:'center',
                width: 370
            },{
                header: "<fmt:message key='aimir.modemid'/>",
                dataIndex: 'modemId',
                align:'center',
                width: 370
            }
            ]
        });

        if(!targetListGridOn){
            $('#targetGroupGrid').html('');
            targetListGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : grWidth,
                height : 746,
                store : targetListStore,
                colModel : targetListColumn,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'targetGroupGrid',
                viewConfig : {
                    //forceFit: true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                    //, enableTextSelection : true
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : 30,
                    store : targetListStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            targetListGridOn = true;
            chkTargetListGridOn = false;
        }else{
            targetListGrid.setWidth(grWidth);
            var bottomToolbar = targetListGrid.getBottomToolbar();
            targetListGrid.reconfigure(targetListStore,targetListColumn);
            bottomToolbar.bindStore(targetListStore);
        }

    } //~function drawTargetListGrid

    /**
     * Target Grid for Array_Store of meter which have were checked by the user.
     * No ajax
     */
    var chkTargetListGridOn = false;
    var chkTargetListStore;
    var chkTargetListColumn;
    var chkTargetListGrid;
    function drawCheckedTargetListGrid(){
        var grWidth = $('#GcStep1').width();

        chkTargetListStore = new Ext.data.ArrayStore({
            // store configs
            autoDestroy: true,
            data: meterArray,
            // reader configs
            //idIndex: 0,
            fields : [
                { name: 'meterId', type: 'string' },
                { name: 'meterMds', type: 'string'},
                { name: 'modemId', type: 'string' },
                { name: 'mcuId', type: 'string' },
                { name: 'no', type: 'number' },
            ]
        });

        chkTargetListColumn = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true
            },
            columns: [{
                header: "<fmt:message key='aimir.number'/>",
                dataIndex: 'no',
                align:'center',
                renderer: function(value, me, record, rowNumber, columnIndex, store) {
                    return Ext.util.Format.number(rowNumber+1, numberFormat);
                },
                width: 64
            },{
                header: "<fmt:message key='aimir.meterid'/>",
                dataIndex: 'meterMds',
                align:'center',
                width: 370
            },{
                header: "<fmt:message key='aimir.modemid'/>",
                dataIndex: 'modemId',
                align:'center',
                width: 370
            }
            ]
        });

        if(!chkTargetListGridOn){
            $('#targetGroupGrid').html('');
            chkTargetListGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : grWidth,
                height : 746,
                store : chkTargetListStore,
                colModel : chkTargetListColumn,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'targetGroupGrid',
                viewConfig : {
                    //forceFit: true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                    //, enableTextSelection : true
                },
            });
            chkTargetListGridOn = true;
            targetListGridOn = false;
        }else{
            chkTargetListGrid.setWidth(grWidth);
            chkTargetListGrid.reconfigure(chkTargetListStore,chkTargetListColumn);
        }

    } //~function drawChecked-TargetListGrid

    /**
     * "#targetCommandCombo" Change Event Handler
     */
    function changeTargetCommand(){
        var selectedCommand = $('#targetCommandCombo option:selected').val();
        $('div [name=comParamDiv]').hide();
        if(selectedCommand=='0'){
            $('#cOndemandDiv').html('');
            $('#cOndemandDiv').show();

            targetService = 'onDemandService()';
            InputGrpOnDemand();
        }else if(selectedCommand=='1'){
            $('#cLimitPowerUsageDiv').html('');
            $('#cLimitPowerUsageDiv').show();

            targetService = 'limitPowerUsageService()';
            InputGrpLimitPowerUsage();
        }else if(selectedCommand=='2'){
            $('#cOTADiv').html('');
            $('#cOTADiv').show();

            targetService = 'otaService()';
            InputGrpOta();
        }else if(selectedCommand=='3'){
            $('#cRelayOffDiv').html('');
            $('#cRelayOffDiv').show();

            targetService = "relayOnOffService('off')";
            InputGrpRelayOff();
        }else if(selectedCommand=='4'){
            $('#cRelayOnDiv').html('');
            $('#cRelayOnDiv').show();

            targetService = "relayOnOffService('on')";
            InputGrpRelayOn();
        }else{
            $('#cEmptyDiv').show();
        }

    }

//~~~~~~~~~~~~~~~~~~~~~~ PARAMETER DIVs ~~~~~~~~~~~~~~~~~~~~~~//
    // Parameter Div - 0.Group Ondemand
    var grpOnDemandFormPanel;
    function InputGrpOnDemand() {
        $('#cOndemandDiv').html('<br><b>');
        var foWidth = $('#GcStep2').width();
        grpOnDemandFormPanel = new Ext.FormPanel({
            id : 'grpOnDemandForm',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            width : foWidth,
            labelWidth : foWidth/5,
            renderTo : 'cOndemandDiv',
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'targetTypeRadio',
                    fieldLabel : 'Type ',
                    items : [
                        {boxLabel: 'DCU',   name: 'radio-type', inputValue:'MCU'},
                        {boxLabel: 'MODEM', name: 'radio-type', inputValue:'MODEM'},
                        {boxLabel: 'METER', name: 'radio-type', inputValue:'METER', checked: true}
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){

                        }
                    },
                }, //xtype : radio
                {
                    xtype: 'datefield',
                    anchor: '100%',
                    fieldLabel: 'From',
                    id : 'grpOnDemandFromDate',
                    name: 'to_date',
                    format: 'd.m.y',
                    pickerOffset : '[10,10]',
                    value: new Date()  // defaults to today
                },
                {
                    xtype: 'datefield',
                    id : 'grpOnDemandToDate',
                    anchor: '100%',
                    fieldLabel: 'To',
                    name: 'to_date',
                    format: 'd.m.y',
                    value: new Date()  // defaults to today
                }
            ], // items

        });

    } //~function InputGrpOnDemand()

    // Show Parameter Div - 1.Group LimitPowerUsage
    var grpLimitPowerUsageFormPanel;
    function InputGrpLimitPowerUsage(){
        $('#cLimitPowerUsageDiv').html('<br><b>');
        var foWidth = $('#GcStep2').width()-15;

        grpLimitPowerUsageFormPanel = new Ext.FormPanel({
            id: 'grpLimitPowerUsageForm',
            frame : true,
            title : 'Threshold Normal',
            bodyStyle:'padding:1px 1px 1px 1px',
            width : foWidth,
            renderTo : 'cLimitPowerUsageDiv',
            items : [
                {
                    xtype: 'fieldset',
                    autoHeight: true,
                    defaultType: 'textfield',
                    labelAlign: 'top',
                    items : [
                        {
                            id: 'grpThresholdValue',
                            labelWidth : foWidth/5,
                            fieldLabel: 'Please enter the value of threshold',
                            name: 'threshold',
                            allowBlank:false
                        }
                    ]
                },
            ]
        }); //~form panel
    } //~function InputGrpLimitPowerUsage()

    // Show Parameter Div - 2.Group OTA
    function InputGrpOta(){
        $('#cOTADiv').html('<br><b>Not implement yet');
    }

    // Show Parameter Div - 3.Group RelayOff
    function InputGrpRelayOff(){
        $('#cRelayOffDiv').html('<br><b>No parameter needed');
    }

    // Show Parameter Div - 4.Group RelayOn
    function InputGrpRelayOn(){
        $('#cRelayOnDiv').html('<br><b>No parameter needed');
    }


//~~~~~~~~~~~~~~~~~~~~~~ COMMAND SETTING ~~~~~~~~~~~~~~~~~~~~~~//
    // Set Command Parameter
    function setCommandParams(_comName){
        // Command Parameter [ _comName == $('#targetCommandCombo option:selected').text(); ]
        if(_comName=='Ondemand'){
            grpOndemandType = Ext.getCmp('targetTypeRadio').getValue().inputValue;
            grpOnDemandFromDate = Ext.getCmp('grpOnDemandFromDate').getValue();
            grpOnDemandToDate = Ext.getCmp('grpOnDemandToDate').getValue();
        }else if(_comName=='Limit PowerUsage'){
            grpThresholdValue = Ext.getCmp('grpThresholdValue').getValue();
            if(isNaN(grpThresholdValue)==true || grpThresholdValue.length < 1){
                // Input value is empty or not number format.
                return false;
            }
        }

        return true;
    }

    // Check Authorization of current role
    function checkAuthCommand(_comName){
        var cmdCode = '0.0.0';
        // set code
        if(_comName=='Ondemand' || 'Ondemand'.includes(_comName)) {
            if (grpOndemandType == 'DCU' || grpOndemandType == 'MCU') {
                cmdCode = '8.1.1';
            } else if (grpOndemandType == 'MODEM'){
                cmdCode = '8.1.2';
            }else if(grpOndemandType=='METER'){
                cmdCode = '8.1.3';
            }
        }else if(_comName=='Limit PowerUsage' || 'Limit PowerUsage'.includes(_comName)){
            cmdCode = '8.1.12';
        }else if(_comName=='Relay Off' || 'Relay Off'.includes(_comName)){
            cmdCode = '8.1.6';
        }else if(_comName=='Relay On' || 'Relay On'.includes(_comName)){
            cmdCode = '8.1.5';
        }

        // check match
        if(roleCommands.length > 0){
            for(var r in roleCommands)
            {
                if(!isNaN(r) && roleCommands[r].code==cmdCode)
                    return true;
            }
        }

        return false;
    }

//~~~~~~~~~~~~~~~~~~~~~~ Run the Command Service ~~~~~~~~~~~~~~~~~~~~~~//
    // Group Command Execution - GRID
    var grpCmdMeterStore;
    var grpCmdMeterCol;
    var grpCmdMeterGrid;
    var grpCmdMeterGridOn = false;
    function drawGrpCmdGrid(funcName) {
        var grWidth = $('#GcStep3').width();

        if(targetListGridOn){
            grpCmdMeterStore = new Ext.data.JsonStore({
                autoLoad : true,
                url : '${ctx}/gadget/device/getSimpleMeterSearchGrid.do',
                baseParams : targetListStore.baseParams,
                root : 'gridData',
                totalProperty : 'totalCnt',
                idProperty : 'meterMds',
                listeners : {
                    beforeload: function(store, options){
                        Ext.apply(options.params, {
                            sGroupOndemandYN: 'Y'
                        });
                    },load: function(store, record, options){
                        numberFoamat = store.reader.jsonData.mdNumberPattern;
                    },
                },
                fields : [
                    { name: 'meterMds', type: 'string' },
                    { name: 'modemId', type: 'string' },
                ]
            });
        }else if(chkTargetListGridOn){
            grpCmdMeterStore = new Ext.data.ArrayStore({
                // store configs
                autoDestroy: true,
                data: meterArray,
                // reader configs
                //idIndex: 0,
                fields : [
                    { name: 'meterId', type: 'string' },
                    { name: 'meterMds', type: 'string'},
                    { name: 'modemId', type: 'string' },
                    { name: 'mcuId', type: 'string' },
                    { name: 'no', type: 'number' },
                ]
            });
        }

        if (grpCmdMeterStore == undefined) {
            grpCmdMeterStore = new Ext.data.JsonStore({
                fields : [ {
                    name : 'meterMds'
                }, {
                    name : 'status'
                } ]
            });
        }

        grpCmdMeterCol = new Ext.grid.ColumnModel({
            defaults : {
                width : 100,
                height : 100,
                sortable : true
            },
            columns : [ {
                id : "meterMds",
                width : 180,
                header : "Meter ID",
                dataIndex : "meterMds",
                align : 'center'
            }, {
                header : "Status",
                width : 524,
                dataIndex : "status"
            }, {
                header : "Result",
                width : 100,
                dataIndex : "view",
                align : 'center'
            } ]
        });

        if(grpCmdMeterGridOn == false){
            grpCmdMeterGrid = new Ext.grid.GridPanel({
                store : grpCmdMeterStore,
                colModel : grpCmdMeterCol,
                width : grWidth,
                height : 770,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'commandResultGrid',
                viewConfig : {
                    //forceFit: true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                }
            });
            grpCmdMeterGridOn = true;
        }else{
            grpCmdMeterGrid.reconfigure(grpCmdMeterStore, grpCmdMeterCol);
        }


    } //~drawGrpCmdGrid()

//~~~~~~~~~~~~~~~~~~~~~~ Each Command Service ~~~~~~~~~~~~~~~~~~~~~~//
    // Service - 0. Group Ondemand
    function onDemandService() {
        ajaxSuccessCount = 0;
        ajaxFailCount = 0;
        var ondemandType = grpOndemandType;
        var from = grpOnDemandFromDate;
        var to  = grpOnDemandToDate;
        var ftime = from.getFullYear().toString();
        ftime += (from.getMonth()+1 < 10) ? ('0'+(from.getMonth() + 1).toString()) : ((from.getMonth() + 1).toString());
        ftime += from.getDate().toString() + '000000';
        var ttime = to.getFullYear().toString();
        ttime += (to.getMonth()+1 < 10) ? ('0'+(to.getMonth() + 1).toString()) : ((to.getMonth() + 1).toString());
        ttime += to.getDate().toString() + '235959';
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        grpCmdMeterStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i = 0; i < grpCmdMeterStore.getCount(); i++) {

            //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
            queueName = $.ajaxQueue({
                type : "GET",
                url : '${ctx}/gadget/device/command/cmdGrpOnDemand.do',
                timeout : extAjaxTimeout,
                data : {
                    'meterMds' : grpCmdMeterStore.getAt(i).data.meterMds,
                    'loginId' : loginId,
                    'type' : ondemandType,
                    'fromDate' : ftime,
                    'toDate' : ttime,
                },
                error : function(returnData, errorMsg){
                    var i = ajaxSuccessCount + ajaxFailCount;
                    grpCmdMeterGrid.getView().focusRow(i);
                    var record = grpCmdMeterStore.getAt(i);

                    record.set('status', 'Fail' );
                    ajaxFailCount++;
                    if (errorMsg=='timeout'){
                        record.set('view', 'Timeout');
                    }
                    if (grpCmdMeterStore.getCount() != ajaxSuccessCount + ajaxFailCount)
                        grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    if (window.ajaxQueueCount[queueName] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                    }
                },
                success : function(returnData) {
                    var i = ajaxSuccessCount + ajaxFailCount;
                    grpCmdDetail[i] = returnData.detail;
                    grpCmdMeterGrid.getView().focusRow(i);
                    var record = grpCmdMeterStore.getAt(i);
                    record.set('view', returnData.detail);
                    if (returnData.rtnStr == 'java.lang.NullPointerException') {
                        record.set('status', 'Not Found Meter!');
                    } else if (returnData.rtnStr == 'Success') {
                        record.set('status', returnData.rtnStr);
                        record.set('view',
                                "<a href='#' onclick='getData(" + i + ");' class='btn_blue'><span><fmt:message key='aimir.report.mgmt.view'/></span></a>");
                    } else if (returnData.rtnStr == '') {
                        record.set('status', 'Failure');
                    } else {
                        record.set('status', returnData.rtnStr);
                    }
                    ajaxSuccessCount++;
                    if (grpCmdMeterStore.getCount() != ajaxSuccessCount + ajaxFailCount)
                        grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

                    if (window.ajaxQueueCount[queueName] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                    }
                }
            });
        }
    } //~function onDemandTask()
    function getData(index) {
	    var record = grpCmdMeterStore.getAt(index);
	    var temp = record.get('status');
	    var noDataHtml = '<html><div  style=\"width: 180px;FONT-SIZE: 36px;  TEXT-ALIGN:center; padding: 50px 0px 50px 0px;\"> No Data!</div></html>';
	
	    if (temp == 'Success') {
	        //Success Window
	        if (!grpCmdWin2) {
	            grpCmdWin2 = new Ext.Window({
	                title : 'Group onDemand DATA',
	                id : 'drAlertWinIdDataPop',
	                applyTo : 'drAlertDataPop',
	                autoScroll : true,
	                pageX : 100,
	                pageY : 50,
	                width : 800,
	                height : 700,
	                closeAction : 'hide',
	                html : grpCmdDetail[index]
	            });
	        } else {
	            grpCmdWin2.update(grpCmdDetail[index]);
	        }
	        Ext.getCmp('drAlertWinIdDataPop').show();
	    } else {
	        // Fail Window
	        if (!grpCmdWin3) {
	            grpCmdWin3 = new Ext.Window({
	                title : 'Group onDemand DATA',
	                id : 'drAlertDataPopFailure',
	                applyTo : 'drAlertDataPopFailure',
	                autoScroll : true,
	                pageX : 100,
	                pageY : 50,
	                width : 200,
	                height : 200,
	                closeAction : 'hide',
	                html : noDataHtml
	            });
	        } else {
	            grpCmdWin3.update(noDataHtml);
	        }
	        Ext.getCmp('drAlertDataPopFailure').show();
	    }
	}
    // Service - 1.Group LimitPowerUsage
    function limitPowerUsageService(){
        var ajaxSuccessCount = 0;
        var ajaxFailCount = 0;
        var storeCount = grpCmdMeterStore.getCount();
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        grpCmdMeterStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i=0; i < storeCount; i++){
            // ajax queue, plugin for sequential processing
            cmdQueue = $.ajaxQueue({
                type: "POST",
                url: "${ctx}/gadget/device/command/cmdGroupLimitPowerUsage.do",
                timeout : extAjaxTimeout,
                data: {
                    meterMds : grpCmdMeterStore.getAt(i).data.meterMds,
                    loginId : loginId,
                    thresholdNormal : grpThresholdValue
                },
                error: function(returnData, errorMsg){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    grpCmdMeterGrid.getView().focusRow(q);
                    var record = grpCmdMeterStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxFailCount+=1;
                    if (errorMsg=='timeout'){
                        record.set('view', 'Timeout');
                    }
                    if((q+1) < storeCount){
                        grpCmdMeterStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }

                },
                success: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    grpCmdMeterGrid.getView().focusRow(q);
                    var record = grpCmdMeterStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxSuccessCount+=1;
                    if ((q+1) < storeCount){
                        grpCmdMeterStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  Post-processing
                         */
                        console.log('total['+storeCount+'] success['+ajaxSuccessCount+'] fail['+ajaxFailCount+']');
                    }
                } //~success

            });
        } //~for
    }

    // Service - 2.Group OTA
    //function....

    // Service - 3.Group RelayOff, 4.Group RelayOn
    function relayOnOffService(onoff) {
        ajaxSuccessCount = 0;
        ajaxFailCount  = 0;
        //async call
        $.ajaxSetup({
            async : true
        });

        for (var i = 0; i < grpCmdMeterStore.totalLength ; i++) {
            grpCmdMeterStore.getAt(i).set('status','Processing... ' );
        }
        //add loading image at first
        grpCmdMeterStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
        var url;
        if ( onoff == 'on')
            url= '${ctx}/gadget/device/command/cmdRemotePowerOn.do'
        else if ( onoff == 'off' )
            url= '${ctx}/gadget/device/command/cmdRemotePowerOff.do'

        for (var i = 0; i < grpCmdMeterStore.getCount(); i++) {
            var targetId = grpCmdMeterStore.getAt(i).json.meterId;
            if(targetId==undefined){
                targetId = grpCmdMeterStore.getAt(i).data.meterId;
            }
            var targetMcuId = grpCmdMeterStore.getAt(i).json.mcuId;
            if(targetMcuId==undefined){
                targetMcuId = grpCmdMeterStore.getAt(i).data.mcuId;
            }
            // add request to queue for sequential processing
            queueName = $.ajaxQueue({
                type : "GET",
                url : url,
                timeout : extAjaxTimeout,
                data : {
                    'target' : targetId,
                    'mcuId'  : targetMcuId,
                    'loginId' : loginId
                },
                error : function(returnData, errorMsg){
                    var i = ajaxSuccessCount + ajaxFailCount;
                    grpCmdMeterGrid.getView().focusRow(i);
                    var record = grpCmdMeterStore.getAt(i);

                    record.set('status', 'Fail' );
                    ajaxFailCount++;
                    if (errorMsg=='timeout'){
                        record.set('view', 'Timeout');
                    }
                    if (grpCmdMeterStore.getCount() != ajaxSuccessCount + ajaxFailCount)
                        grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    if (window.ajaxQueueCount[queueName] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                    }
                },
                success : function(returnData) {
                    var i = ajaxSuccessCount + ajaxFailCount;
                    grpCmdMeterGrid.getView().focusRow(i);
                    var record = grpCmdMeterStore.getAt(i);
                    record.set('status', returnData.status );
                    if (returnData.relayStatus != undefined && returnData.relayStatus != ''){
                        record.set('view' , returnData.relayStatus);
                    }else{
                        record.set('view' , returnData.rtnStr);
                    }

                    ajaxSuccessCount++;
                    if (grpCmdMeterStore.getCount() != ajaxSuccessCount + ajaxFailCount)
                        grpCmdMeterStore.getAt(ajaxSuccessCount + ajaxFailCount).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    if (window.ajaxQueueCount[queueName] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                    }
                }
            });
        }
    }

var captchacount=1;	//틀린 횟수 체크
var incorrectCodeCheck = false; //틀렸을 경우 메세지를 보여주기 위해서
function CaptchaPanel(){
    	// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('captchaWindowPanel')){
 			Ext.getCmp('captchaWindowPanel').close();
 		} 		
 		
 		var captchaFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        items       : [
 		            {
 		            	xtype: 'panel',
 		            	html: '<center><img src="${ctx}/CaptChaImg.jsp?rand='+ Math.random() + '"/></center></br>',
 		            	align:'left'
 		            	
 		            },
 		            {
 		            	xtype: 'textfield',
 		            	id : 'captchaCode',
 		            	fieldLabel: '<fmt:message key="aimir.captchaCode" />',
 		                emptyText: '<fmt:message key="aimir.enterTheCode" />',
 		                disabled : false,
 		               
 		            },
 		           {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	},
 		            	text : '*<fmt:message key="aimir.incorrectCode" />',
 		            	hidden: true
 		            }

 		        ],
 		        buttons: [
 		            {
			    	 	text: '<fmt:message key="aimir.refresh" />',
			    	 	listeners: {
			            	click: function(btn,e){
			            		captchaWindow.load(CaptchaPanel());
			            		
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.submit" />',
			            listeners: {
			            	click: function(btn,e){
			            		if(5==captchacount){
  			              		  window.open('${ctx}/admin/logout.do',"_parent").parent.close();
  			              	  	} 
			            		$.ajax({
			                        url: '${ctx}/gadget/report/CaptchaSubmit.do',
			                        type: 'POST',
			                        dataType: 'json',
			                        data: 'answer=' + $('#captchaCode').val(),
			                        async: false,  
			                        success: function(data) {
			                             if(data.capcahResult=="true"){
			                            	//올바른 코드 입력 시
			                            	 captchacount = 1;
			                            	Ext.getCmp('captchaWindowPanel').close();
			                            	setTimeout(targetService, 100);			                            		
			                             }else{  
			                            	 //잘못된 코드 입력
			                            	 captchacount++;
			                            	 incorrectCodeCheck = true;
			                            	 captchaWindow.load(CaptchaPanel());
			                             }
			                       		}
			                  		});
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.cancel" />',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('captchaWindowPanel').close();
	                        }
	                    }
		        }]
 		    });
 		    var captchaWindow = new Ext.Window({
 		        id     : 'captchaWindowPanel',
 		        title  : cpCommandName,
 		        pageX : 100,
                pageY : 100,
 		        height : 206, 
 		        width  : 300,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [captchaFormPanel],
 		        resizable: false
 		    });
 		    
 		    captchaWindow.show();
 			// 코드가 틀렸을 경우 메세지가 보이게
     		if(incorrectCodeCheck == true){
     			Ext.getCmp('infolabel').setVisible(true);
     			Ext.getCmp('captchaCode').focus(true,100);
     			incorrectCodeCheck = false;	
     		}
 	}

    function openExcelReport2() {
		var com = document.getElementById("ondemandTable");
		f.excelData.value = com.outerHTML;
		f.action = "excelView.jsp";
		f.target = "_blank";
		console.log(f);
		f.submit();
    }
</script>
<!-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -->
<!-- Group Command Page -->
<div class="popup_title">Group-Command Configuration
    <%--<label class="margin-r5 blue12pt">Group-Command Configuration</label>--%>

    <!-- <em><a href='#;' class="btn_green margin-r5" style="float:right; line-height: 14px;" onclick='' >
        <span >HELP:SCHEDULER</span></a></em> -->
</div>

<div class="gadget_body">
<div id='drAlertDataPop'></div>
<div id='drAlertDataPopFailure'></div>
<!-- LEFT SIDE : step -->
<div class="width-60 margin-t10px floatleft border_blu" style="height:810px;">

    <!-- Step 1 : Select Group Category & Target Meter -->
    <div name="GcStepDiv" id="GcStep1" class="margin10px ">
        <div><label class="check ">1. Select the target list</label></div>
        <div class="margin-t5px overflow_hidden">
            <select id="targetGroupCombo" name="targetGroup" class="selectbox" style="width:200px">
            <option value="0" selected>[OPTION] Searched Meter</option>
            <option value="1" >[OPTION] Checked Meter</option>
        </select></div>
        <!-- Meter list -->
        <div class="margin-t5px" id="targetGroupGrid">

        </div>
    </div>
    <!-- Step 2 : Select Command & Set Request Parameters -->
    <div name="GcStepDiv" id="GcStep2" class="margin10px ">
        <div><label class="check ">2. Select the command</label></div>
        <div class="margin-t5px overflow_hidden"><select id="targetCommandCombo" name="targetGroup" class="selectbox" style="width:200px;">
            <!-- Put Command Name In Here -->
            <option value="0" selected>Ondemand</option>
            <option value="1" >Limit PowerUsage</option>
            <!-- <option value="2" >OTA</option> -->
            <option value="3" >Relay Off</option>
            <option value="4" >Relay On</option>
            <!-- <option value="5" >Test</option>  -->
            <!-- <option value="#+1" >Command Name</option> -->
        </select></div>
        <div id="commandParameter" class="margin-t5px floatnone">
            <!-- Put Your Own Parameter Menus In Here -->
            <li><div name="comParamDiv" id="cOndemandDiv" style="display:hidden;"> </div></li>
            <li><div name="comParamDiv" id="cLimitPowerUsageDiv" style="display:hidden;"> </div></li>
            <li><div name="comParamDiv" id="cOTADiv" style="display:hidden;"> </div></li>
            <li><div name="comParamDiv" id="cRelayOffDiv" style="display:hidden;"> </div></li>
            <li><div name="comParamDiv" id="cRelayOnDiv" style="display:hidden;"> </div></li>
            <li><div name="comParamDiv" id="cEmptyDiv" style="display:hidden;">No Data To Show</div></li>
            <!--<div id="c~CommandName~Div" style="display:hidden;"> </div>-->
        </div>
    </div>

    <!-- Step 3 : Command Result -->
    <div name="GcStepDiv" id="GcStep3" class="margin10px ">
        <div><label class="check ">3. Wait for result</label></div>
        <!-- Result list -->
        <div class="margin-t5px" id="commandResultGrid">

        </div>
    </div>
</div>
<!-- RIGHT SIDE : summary -->
<div class="width-39 margin-t10px floatright border_blu">
    <div id="GcSummary" class="margin10px ">
        <li><label class="check ">Configuration summary</label></li>
        <div>
        <li class="margin-t5px blue12pt">* Target Group </li>
        <li id="SelectedTargetGroupName" class="margin-t3px margin-l10"> </li>
        <li class="margin-t5px blue12pt">* Number of Target</li>
        <li id="SelectedTargetNumber" class="margin-t3px margin-l10"> </li>
        <li class="margin-t5px blue12pt">* Command Name</li>
        <li id="SelectedCommandName" class="margin-t3px margin-l10"> </li>
        </div>
    </div>
</div>

<!-- Bottom SIDE : Buttons -->
<div class="width-39 margin-t10px floatright ">
    <ul>
        <em name="moveBtn" id="prevBtn" class="am_button margin-t2px"><a href="javascript:prevBtn();">PREV</a></em>
        <em name="moveBtn" id="nextBtn" class="am_button margin-t2px"><a href="javascript:nextBtn();">NEXT</a></em>
        <em name="moveBtn" id="execBtn" class="am_button margin-t2px"><a href="javascript:execBtn();">EXECUTE</a></em>
    </ul>
</div>
</div>
</body>
</html>