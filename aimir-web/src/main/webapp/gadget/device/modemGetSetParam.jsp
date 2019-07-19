<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        .cmdLineDiv {width:auto;}
    </style>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>

    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
    var content='';
	var obj;
	var otaUpload;
    var supplierName="${supplierName}";
    var target = "";
    var fwType = "";
    var cmdList='';
	var supplierId = "";
	var modemId = "";
	var loginId = "";
	var modemType = "";
	var modemProtocolType = "";
    var extAjaxTimeout = 180000;
    var extAjaxTimeout2 = 18000;
    
    cmdList =[
      	[1, 'Modem Reset Time',            '<fmt:message key='aimir.nicmd.ModemResetTimeDescr'/>'],
      	[2, 'Modem Mode',                  '<fmt:message key='aimir.nicmd.ModemModeDescr'/>'],
      	[3, 'Modem IP Information',        '<fmt:message key='aimir.nicmd.ModemIPInformationDescr'/>'],
      	[4, 'Modem Port Information',      '<fmt:message key='aimir.nicmd.ModemPortInformationDescr'/>'],
      	[5, 'Alarm/Event Command ON_OFF',  '<fmt:message key='aimir.nicmd.AlarmEventCommandOnOffDescr'/>'],
       	[6, 'Transmit Frequency',          '<fmt:message key='aimir.nicmd.TransmitFrequencyDescr'/>']
      	//,[7, 'SNMP Trap Enable/Disable',    '<fmt:message key='aimir.nicmd.SnmpTrapEnable'/>']
      	];
    
    var otaType = "";
    var touType = "";
    $(function(){
    	obj = window.opener.obj;
    });
    
    $(document).ready(function(){
   	 var obj = window.opener.obj;
     loginId = obj.loginId;
     supplierId = obj.supplierId;
     modemId = obj.modemId;
     modemType = obj.modemType;
     modemProtocolType = obj.modemProtocolType;

     if(modemType != "SubGiga"){//RF Modem [SNMP Trap Enable/Disable] function disable
    	 cmdList[6] = [7, 'SNMP Trap Enable/Disable',    '<fmt:message key='aimir.nicmd.SnmpTrapEnable'/>'];
     }
     CMDGrid();

        function CMDGrid() {

            var store = new Ext.data.Store({
                data : cmdList,
                reader : new Ext.data.ArrayReader({
                    id : 'id'
                }, [ 'id', 'parameter', 'description'])
            });

            var grid = new Ext.grid.GridPanel({
                renderTo : 'cmdLine',
                frame : true,
                title : 'Get/Set Parameter',
                height : 300,
                width : 670,
                store : store,
                columns : [ {
                    header : "Parameter",
                    dataIndex : 'parameter',
                    width: 200, align: 'left'
                }, //hidden:true
                {
                    header : "Description",
                    dataIndex : 'description',
                    width: 900, align: 'left'
                } ],
/*                 selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                    	click : function(selectionModel, columnIndex, value) {
                            var param = value.data;


                        }
                            
                    }
                }), */
                listeners: {
                	cellclick: function(grid, rowIndex , columnIndex, e) {                                       
                		var record = grid.getStore().getAt(rowIndex);
                        if ( rowIndex == 0  ){
                        	modemResetTime();
                        }
                        else if (rowIndex == 1){
                        	ModemMode();
                        }
                        else if ( rowIndex == 2){
                        	modemIpInformation();
                    	}
                        else if ( rowIndex == 3){
                        	modemPortInformation();
                    	}
                        else if ( rowIndex == 4 ){
                        	AlarmEventCommandOnOff( );  
                        	
                        }
                        else if ( rowIndex == 5){
                        	transmitFrequency();
                        } else if ( rowIndex == 6){
                            snmpEnableDisable();
                        }
                	}
                }
            });
        }
    	
    });
 
	// Modem Reset Time
    function modemResetTime(cmd)
    {
		var modemResetTimeWin = Ext.getCmp('modemResetTimeWindow');
		if ( modemResetTimeWin != undefined ){
			modemResetTimeWin.close();
		}
        modemResetTimeFormPanel = new Ext.FormPanel({
            id : 'modemResetTimeform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio',
                    fieldLabel : 'Get/Set ',

                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', disabled: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET',  checked:true}
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('resetTimeValue').enable();
                                Ext.getCmp('modemResetTimeBtn').setText('SET Modem Reset Time');
                            }else{
                                //button hide
                                //Ext.getCmp('resetTimeValue').disable();
                                //Ext.getCmp('modemResetTimeBtn').setText('GET Modem Reset Time');
                            }
                        }
                    },
                }, //xtype : radio
                {
                    xtype: 'checkbox'
                   ,id:'noreset' 
                   ,fieldLabel: 'Don\'t Reset'
                   ,name: 'noreset'
                   ,trueText: 'T'
                   ,falseText: 'N'
                   ,listeners: {
                	   change: function() {
                	   if ( this.getValue() == false ){
                		   Ext.getCmp('resetTimeValue').enable();
                	   }
                	   else {
                		   Ext.getCmp('resetTimeValue').disable(); 
                	   }
                	   }
                   }
               },
                {
                    xtype: 'textfield',
                    id : 'resetTimeValue',
                    width : 100,
                    fieldLabel: 'Reset Time',
                    regex: /^\d{1,2}$/,
                    disabled : false,
                },
		        {
		           	xtype: 'label',
		           	id : 'resetTimeInfolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'input 0-23',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'modemResetTimeBtn',
                    text: 'Set Modem Reset Time',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            setmodemResetTimeAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('modemResetTimeWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        modemResetTimeWin = new Ext.Window({
            id     : 'modemResetTimeWindow',
            title : 'Get/Set Meter modemResetTime',
            pageX : 150,
            pageY : 150,
            height : 190,
            width  : 270,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [modemResetTimeFormPanel],
        });

        modemResetTimeWin.show();
    }
    
    function setmodemResetTimeAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio').getValue().inputValue;
        var actionRequestValue = Ext.getCmp('resetTimeValue').getValue();
    	var resetTime = 0;
    	var checkResetTime = false;
        if ( actionRequestType == "SET") {
        	
        	if ( Ext.getCmp('noreset').getValue() == true){
        		resetTime = 0xff;
        		checkResetTime = true;
        	}
        	else if ( actionRequestValue == undefined || actionRequestValue == '' || isNaN(actionRequestValue) ){
        		//
        	}
        	else {
        		resetTime =  parseInt(actionRequestValue);
        		if (  0 <= resetTime && resetTime <= 23 ){
        			checkResetTime = true;
        		}
        	}
        	if ( !checkResetTime ) {
        		Ext.getCmp('resetTimeInfolabel').show();
        		return;
        	}
        	
        }
        $('#commandResult').val('');
        Ext.define('modemResetTimeAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        modemResetTimeAjax.request({
            url :  '${ctx}/gadget/device/command/cmdModemResetTime.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId         : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                resetTime  : resetTime
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                Ext.getCmp('modemResetTimeWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Status : " + jsonData.baudRate +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
                if(jsonData.status.length>0 && jsonData.status=='SUCCESS'){
                	resetModem();
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                Ext.getCmp('modemResetTimeWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }

	// Modem Mode
	var  modemModeArray = ['0x00:Push Mode','0x01:Polling(Bypass) Mode'];
	
    var IpTargetType =  new Ext.data.Store({
 	   reader : new Ext.data.ArrayReader({
            id : 'val'
        }, [ 'id', 'description']),
 	     data : [
 	         [0, "0:DCU(RF Modem)"], 
 	         [1, "1:HES"],
 	         [2, "2:SNMP"],
 	         [3, "3:NTP(MBB Modem)"],
 	         [4, "4:Modem(Ethernet Modem)"]
 	     ]
 	});

    var portTargetType =  new Ext.data.Store({
    	   reader : new Ext.data.ArrayReader({
               id : 'val'
           }, [ 'id', 'description']),
        data : [
            [0, "0:DCU Server(RF Modem)"],
            [1, "1:DCU Client(RF Modem)"],
            [2, "2:HES Server"],
            [3, "3:HES Client"],
            [4, "4:HES Auth"],
            [5, "5:SNMP"],
            [6, "6:Coap"],
            [7, "7:NI"],
            [8, "8:NTP(MBB Modem)"],
            [9, "9:Modem(Ethernet Modem)"]
        ]
    });
    function ModemMode()
    {
		var modemModeWin = Ext.getCmp('modemModeWindow');
		if ( modemModeWin != undefined ){
			modemModeWin.close();
		}
		
        modemModeFormPanel = new Ext.FormPanel({
            id : 'modemModeform',
  //          defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio2',
                    fieldLabel : 'Get/Set ',

                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('modemModeCombo').enable();
                                Ext.getCmp('modemModeBtn').setText('SET Modem Mode');
                            }else{
                                //button hide
                                Ext.getCmp('modemModeCombo').disable();
                                Ext.getCmp('modemModeBtn').setText('GET Modem Mode');
                            }
                        }
                    },
                }, //xtype : radio

                {
                    xtype:'combo',
                    fieldLabel:'Modem Mode',
                    name:'modemMode',
                    id: 'modemModeCombo',
                    queryMode:'local',
                    store:modemModeArray ,
                    displayField:'division',
                    emptyText:'Select a item...',
                    autoSelect:true,
                    forceSelection:true,
                    width: 170,
                    triggerAction : "all",
                    disabled : true
               }, //xtype : radio
   	        {
	           	xtype: 'label',
	           	id : 'modemModeInfolabel',
	           	style : {
	           		background : '#ffff00'
	          	},
	           	text : 'select Modem Mode',
	           	hidden: true
	       }   
            ], // items
            buttons : [
                {
                    id: 'modemModeBtn',
                    text: 'Get Modem Mode',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            modemModeAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('modemModeWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        modemModeWin = new Ext.Window({
            id     : 'modemModeWindow',
            title : 'Get/Set Modem Mode',
            pageX : 150,
            pageY : 150,
            height : 170,
            width  : 330,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [modemModeFormPanel],
        });

        modemModeWin.show();
    }
    
    function modemModeAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio2').getValue().inputValue;
        var selected = Ext.getCmp('modemModeCombo').getValue();
		var actionRequestValue = "";
        if ( actionRequestType == "SET") {
    		if ( selected == modemModeArray[0]) 
    			actionRequestValue = 0;
    		else if ( selected == modemModeArray[1])
    			actionRequestValue = 1;
    		else {
    			Ext.getCmp('modemModeInfolabel').show();
    			return;
    		}
        }
        else {
        	actionRequestValue = 0;
        }
        $('#commandResult').val('');
        Ext.define('modemModeAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        modemModeAjax.request({
            url :  '${ctx}/gadget/device/command/cmdModemMode.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
            	modemId       : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                mode  : actionRequestValue
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                Ext.getCmp('modemModeWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Modem Mode : " + jsonData.modemMode +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('modemModeWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }

    // SNMP Sever IPv6/Port
    var ipAddress_regexp =/^(([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/;
    var ipv6Address_regexp = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
    function serverIpv6Port(cmd)
    {
	
		serverIpv6FormPanel = new Ext.FormPanel({
            id : 'serverIpv6form',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio3',
                    cls: 'x-check-group-alt',
                    itemId: 'myFavorite',
                    fieldLabel : 'Get/Set ',
                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('ipTypeRadio').enable();
                                Ext.getCmp('ipaddrValue').enable();
                                Ext.getCmp('portValue').enable();
                                Ext.getCmp('serverIpv6Btn').setText('SET Server IPv6/Port');
                            }else{
                                //button hide
                                Ext.getCmp('ipTypeRadio').disable();
                                Ext.getCmp('ipaddrValue').disable();
                                Ext.getCmp('portValue').disable();
                                Ext.getCmp('serverIpv6Btn').setText('GET Server IPv6/Port');
                            }
                        }
                    },
                }, //xtype : radio
 
                {
                    xtype : 'radiogroup',
                    id : 'ipTypeRadio',
                    fieldLabel : 'IP Type ',
                    itemId: 'ipType',
                    cls: 'x-check-group-alt',
                    disabled : true,
                    items : [
                        {boxLabel: 'IPv4', name: 'radio-iptype', inputValue:'0', checked:true},
                        {boxLabel: 'IPv6', name: 'radio-iptype', inputValue:'1' }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='0'){
                                //button show
                                var a = Ext.getCmp('ipaddrValue');
                               //a.vtype = 'ip';
                               a.regex = ipAddress_regexp;
                                
                            }else{
                            	var a = Ext.getCmp('ipaddrValue');
                           		a.regex = ipv6Address_regexp;
                            }
                        }
                    },
                }, //xtype : radio      
                {
                    xtype: 'textfield',
                    id : 'ipaddrValue',
                    emptyText: '',
                    fieldLabel: 'IP Address',
                    width: 250,
                    disabled : true,
                },
                {
                    xtype: 'textfield',
                    id : 'portValue',
                    emptyText: '',
                    fieldLabel: 'Port ',
                    regex: /^\d{1,5}$/,
                    disabled : true,
                },
		        {
		           	xtype: 'label',
		           	id : 'ipv6Infolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'invalid IP Address or Port',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'serverIpv6Btn',
                    text: 'Get serverIpv6',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            setserverIpv6Action();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('serverIpv6Window').close();
                        }
                    }
                }
            ] //buttons
        });

        var serverIpv6Win = new Ext.Window({
            id     : 'serverIpv6Window',
            title : 'Get/Set Meter serverIpv6',
            pageX : 150,
            pageY : 150,
            height : 220,
            width  : 410,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [serverIpv6FormPanel],
        });

        serverIpv6Win.show();
    }
    
    function setserverIpv6Action() {
        var actionRequestType = Ext.getCmp('requestTypeRadio3').getValue().inputValue;
        var ipType  =  0; 
        var ipAddress = '';
        var udpPort = 0;
        
        if ( actionRequestType == "SET") {
        	var ipTypeVal = Ext.getCmp('ipTypeRadio').getValue().inputValue;
        	var ipAddressVal = Ext.getCmp('ipaddrValue').getValue();
        	if (  ipTypeVal == '0') { //ipv4        
		         if ( !ipAddress_regexp.exec(ipAddressVal) ){
		        	 Ext.getCmp('ipv6Infolabel').show();
		         	return false;
		         }
        	}
        	else {
		         if ( !ipv6Address_regexp.exec(ipAddressVal) ){
		        	 Ext.getCmp('ipv6Infolabel').show();
			         	return false;		        	 
		         }
        	}
        	ipAddress = ipAddressVal;
        	ipType = parseInt(Ext.getCmp('ipTypeRadio').getValue().inputValue);
        	var udpPortVal = Ext.getCmp('portValue').getValue();
        	if ( udpPortVal == '' || parseInt(udpPortVal) > 65535 ){
	        	 Ext.getCmp('ipv6Infolabel').show();
		         	return false;	
        	}
        	udpPort = parseInt(udpPortVal);
        }
        $('#commandResult').val('');
        Ext.define('serverIpv6Ajax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        serverIpv6Ajax.request({
            url :  '${ctx}/gadget/device/command/cmdSnmpServerIpv6Port.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId        : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                type	  : ipType,
                ipAddress : ipAddress,
                port	: udpPort
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('serverIpv6Window').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # SNMP Sever Ip/Port : " + jsonData.SnmpSeverIpPort +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('serverIpv6Window').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }
 
    function modemIpInformation(cmd)
    {
	
    	var modemIpWin = Ext.getCmp('modemIpWindow');
    	if ( modemIpWin != undefined ){
    		modemIpWin.close();
    	}
		modemIpPanel = new Ext.FormPanel({
            id : 'modemIpform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio3',
                    cls: 'x-check-group-alt',
                    itemId: 'myFavorite',
                    fieldLabel : 'Get/Set ',
                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('ipTypeRadio').enable();
                                Ext.getCmp('ipaddrValue').enable();
                                Ext.getCmp('ipTargetType').enable();
                                Ext.getCmp('modemIpBtn').setText('SET Modem IP Information');
                            }else{
                                //button hide
                              //  Ext.getCmp('ipTypeRadio').disable();
                                Ext.getCmp('ipaddrValue').disable();
                              //  Ext.getCmp('ipTargetType').disable();
                                Ext.getCmp('modemIpBtn').setText('GET Modem IP Information');
                            }
                        }
                    },
                }, //xtype : radio
                    {
                        fieldLabel : 'Target Type',
                        id : 'ipTargetType',
                        xtype : 'combo',
                        triggerAction : 'all',
                        mode : 'local',
                        //store : modemTargetTypeArray,
                        store : IpTargetType,
                        displayField : 'description',
                        valueField : 'id',
                        emptyText:'Select a item...',
                        //disabled : true,       
                        editable : false,
                        anchor : '95%'
                    },

                {
                    xtype : 'radiogroup',
                    id : 'ipTypeRadio',
                    fieldLabel : 'IP Type ',
                    itemId: 'ipType',
                    cls: 'x-check-group-alt',
                    //disabled : true,
                    items : [
                        {boxLabel: 'IPv4(MBB/Ethernet)', name: 'radio-iptype', inputValue:'0', checked:true},
                        {boxLabel: 'IPv6(RF Modem)', name: 'radio-iptype', inputValue:'1' }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='0'){
                                //button show
                                var a = Ext.getCmp('ipaddrValue');
                               //a.vtype = 'ip';
                               a.regex = ipAddress_regexp;
                                
                            }else{
                            	var a = Ext.getCmp('ipaddrValue');
                           		a.regex = ipv6Address_regexp;
                            }
                        }
                    },
                }, //xtype : radio      
                {
                    xtype: 'textfield',
                    id : 'ipaddrValue',
                    emptyText: '',
                    fieldLabel: 'IP Address',
                    width: 250,
                    disabled : true,
                },
                
		        {
		           	xtype: 'label',
		           	id : 'modemIpInfolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'invalid IP Address',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'modemIpBtn',
                    text: 'Get Modem Ip Information',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            modemIpAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('modemIpWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        modemIpWin = new Ext.Window({
            id     : 'modemIpWindow',
            title : 'Get/Set Modem IP Inforamation',
            pageX : 150,
            pageY : 150,
            height : 220,
            width  : 410,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [modemIpPanel],
        });

        modemIpWin.show();
    }
    
    function modemIpAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio3').getValue().inputValue;
        var ipType  =  0; 
        var ipAddress = '';
        var udpPort = 0;
    	var ipTypeVal = Ext.getCmp('ipTypeRadio').getValue().inputValue;
        var ipTargetType = Ext.getCmp('ipTargetType').getValue();
        var label = Ext.getCmp('modemIpInfolabel');
        
        if ( ipTargetType === "" ){
        	label.setText('Select Target Type');
        	label.show();
        	return false;
        }
        if ( ipTargetType == 0 && modemType != "SubGiga" ){
            label.setText('Target Type \'DCU\' is selectable only with RF Modem');
            label.show();
            return false;
        }
        if ( ipTargetType == 3 && ( modemType != "MMIU" || modemProtocolType != "SMS") ){
            label.setText('Target Type \'NTP\' is selectable only with MBB Modem');
            label.show();
            return false;
        }
        if ( ipTargetType == 4 && ( modemType != "MMIU" || modemProtocolType != "IP" )){
            label.setText('Target Type \'Modem\' is selectable only with Ethernet Modem');
            label.show();
            return false;
        }
        if ( ipTypeVal == '0' && modemType == "SubGiga"){// ipv4
            label.setText('IP Type \'IPv4\' is not selectable with RF Modem');
            label.show();
            return false;
        }
        if ( ipTypeVal == '1' && modemType != "SubGiga"){// ipv6
            label.setText('IP Type \'IPv6\' is selectable only with RF Modem');
            label.show();
            return false;
        }
        if ( actionRequestType == "SET") {
        	var ipAddressVal = Ext.getCmp('ipaddrValue').getValue();
        	if (  ipTypeVal == '0') { //ipv4        
                 if ( !ipAddress_regexp.exec(ipAddressVal) ){
                     label.setText('invalid IP Address');
                     label.show();
                     return false;
                 }
        	}
        	else {
                 if ( !ipv6Address_regexp.exec(ipAddressVal) ){
                     label.setText('invalid IP Address');
                     label.show();
                     return false;
		         }
        	}
        	ipAddress = ipAddressVal;
        	ipType = parseInt(Ext.getCmp('ipTypeRadio').getValue().inputValue);
        }
        $('#commandResult').val('');
        Ext.define('modemIpAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        modemIpAjax.request({
            url :  '${ctx}/gadget/device/command/cmdModemIpInformation.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId        : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                targetType : ipTargetType,
                ipType	  : ipTypeVal,
                ipAddress : ipAddress
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('modemIpWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Modem IP Information : " + jsonData.ModemIpInformation +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('modemIpWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }
    // Modem Port Information
    function modemPortInformation(cmd)
    {
	
    	var modemPortWin = Ext.getCmp('modemPortWindow');
    	if ( modemPortWin != undefined ){
    		modemPortWin.close();
    	}
		modemPortPanel = new Ext.FormPanel({
            id : 'modemPortform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio4',
                    cls: 'x-check-group-alt',
                    itemId: 'myFavorite',
                    fieldLabel : 'Get/Set ',
                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('portTargetType').enable();
                                Ext.getCmp('portValue').enable();
                                Ext.getCmp('modemPortBtn').setText('SET Modem Port Information');
                            }else{
                                Ext.getCmp('modemPortBtn').setText('GET Modem Port Information');
                            }
                        }
                    },
                },
                {
                    fieldLabel : 'Target Type',
                    id : 'portTargetType',
                    xtype : 'combo',
                    triggerAction : 'all',
                    mode : 'local',
                    store : portTargetType,
                    displayField : 'description',
                    valueField : 'id',
                    emptyText:'Select a item...',
                    editable : false,
                    anchor : '95%'
                },
                {
                    xtype: 'textfield',
                    id : 'portValue',
                    emptyText: '',
                    fieldLabel: 'Port ',
                    regex: /^\d{1,5}$/,
                    disabled : true,
                },
                     
		        {
		           	xtype: 'label',
		           	id : 'modemPortInfolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'invalid Port Address ',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'modemPortBtn',
                    text: 'Get Modem Port Information',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            modemPortAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('modemPortWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        modemPortWin = new Ext.Window({
            id     : 'modemPortWindow',
            title : 'Get/Set Modem Port Inforamation',
            pageX : 150,
            pageY : 150,
            height : 220,
            width  : 410,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [modemPortPanel],
        });

        modemPortWin.show();
    }
    
    function modemPortAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio4').getValue().inputValue;
        var ipType  =  0; 
        var ipAddress = '';
        var port = 0;
        var targetType = Ext.getCmp('portTargetType').getValue();
        var label = Ext.getCmp('modemPortInfolabel');
        label.hide();
        
        if ( targetType === "" ){
        	label.setText('Select Target Type');
        	label.show();
        	return false;
        }
        
        if ( (targetType == 0  ||targetType == 1) &&  modemType != "SubGiga" ){
            label.setText('Target Type \'DCU Server/Client\' is selectable only with RF Modem');
            label.show();
            return false;
        }
        if ( targetType == 8 && ( modemType != "MMIU" || modemProtocolType != "SMS") ){
            label.setText('Target Type \'NTP\' is selectable only with MBB Modem');
            label.show();
            return false;
        }
        if ( targetType == 9 && ( modemType != "MMIU" || modemProtocolType != "IP" )){
            label.setText('Target Type \'Modem\' is selectable only with Ethernet Modem');
            label.show();
            return false;
        }
        
        if ( actionRequestType == "SET") {
            port = Ext.getCmp('portValue').getValue();
            if (  port == undefined || port === "" ) {
                label.setText('Input Port');
                label.show();
                return false;
            }
            if ( isFinite(port) == false ){
                label.setText('Invalid Port');
                label.show();
                return false;
            }
        }
        $('#commandResult').val('');
        Ext.define('modemPortAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        modemPortAjax.request({
            url :  '${ctx}/gadget/device/command/cmdModemPortInformation.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId        : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                targetType : targetType,
                port	  : port
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('modemPortWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Modem Port Information : " + jsonData.ModemPortInformation +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('modemPortWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }
	// Alarm/Event Command ON_OFF
    var eventarray = [];
    function getEventArray(){
        var earray = [
            { 'typeId':'0x0101','typename':'Power Fail'                      ,'on':true},
            { 'typeId':'0x0102','typename':'Power Restore'                   ,'on':false},
            { 'typeId':'0x0501','typename':'Time Synchronization'            ,'on':false},
            { 'typeId':'0x0502','typename':'Meter Time Synchronization'      ,'on':false},
            { 'typeId':'0x0503','typename':'Meter Upgrade Finished'      ,'on':false},
            { 'typeId':'0x0901','typename':'Equipment Configuration Changed' ,'on':false},
            { 'typeId':'0x0902','typename':'Equipment Installed'             ,'on':false},
            { 'typeId':'0x0907','typename':'Equipment Firmware Update'       ,'on':false},
            { 'typeId':'0x0B02','typename':'Meter No Response'               ,'on':false},
            { 'typeId':'0x0D01','typename':'OTA Download Download'           ,'on':false},
            { 'typeId':'0x0D02','typename':'OTA Download Start'              ,'on':false},
            { 'typeId':'0x0D03','typename':'OTA Download End'                ,'on':false},
            { 'typeId':'0x0D04','typename':'OTA Download Result'	         ,'on':false},
            { 'typeId':'0x0F03','typename':'Malfunction Memory Error'        ,'on':false},
            { 'typeId':'0x1301','typename':'Security Alarm	Metering Fail(HLS)','on':false},
            { 'typeId':'0x1302','typename':'Security Alarm	Communication Fail(TLS/DTLS)','on':false}];
            
        return earray;
    }
	
	function AlarmEventCommandOnOff()
    {
    	var cmdOnOffWin = Ext.getCmp('cmdOnOffWindow');
    	if ( cmdOnOffWin != undefined ){
    		cmdOnOffWin.close();
    	}
		var store = new Ext.data.JsonStore({
                fields : [ 
                {  name : 'typeId'
                },
                {
                    name : 'typename'
                }, {
                    name : 'on'
                } ]
            });
    	

                                                  
        eventarray= getEventArray();                                          
        store.loadData(eventarray);
        
        var colModel = new Ext.grid.ColumnModel({
            columns: [
              {		 
            	    //[sp-1026] 모뎀 버그로 인한 일시적인 기능 제한 
                   	//header: "<div class='am_button' style='background:none'><input type='checkbox' id='allEventCheck' onClick='chkAllEvent()' /></div>",
                   	header: "<div class='am_button' style='background:none'><input type='checkbox' id='allEventCheck' disabled='disabled' onClick='chkAllEvent()' /></div>",
                   	width: 45,
                   	align:'center',
                   	renderer: dataChkEvent
                   	
              },
              {header: "ID", dataIndex:'typeId', width:60},  
                {header: "Alarm/Event", dataIndex: 'typename', width:250, align: 'center'}
                ,               
                {	
                	header: "ON<div class='am_button' style='background:none'><input type='checkbox' id='allCmdCheck' disabled='disabled' onClick='chkAllCmd()' /></div>",
                	width: 65,
                	align:'center',
                	renderer: dataChk
                	
                }
            ]
        });
        var grid = new Ext.grid.GridPanel({
              //title: '최근 한달 Demand Response History',
              store: store,
              colModel : colModel,
              autoScroll:true,
              width: 580,
              height: 380,
              flex: 1,
           
          });
        

        cmdOnOffFormPanel = new Ext.FormPanel({  
            id : 'cmdOnOffform',                 
  //          defaultType : 'fieldset',           
            bodyStyle:'padding:1px 1px 1px 1px',  
            frame : true,   
            labelWidth : 100,                     
            items : [                             
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio4',
                    fieldLabel : 'Get/Set ',

                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        //[sp-1026] 모뎀 버그로 인한 일시적인 기능 제한
                        //{boxLabel: 'SET', name: 'radio-action', inputValue:'SET'  }
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , disabled: true}
                        //
                    ],
                    listeners :{
                    	change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                $("#allCmdCheck").attr('disabled',false);
                                var len = $('[name="cmdOnOff"]').length;
                                for ( var i = 0; i < len; i++){
                                	var item = $('[name="cmdOnOff"]')[i].disabled=false;
                                }
                                Ext.getCmp('cmdOnOffBtn').setText('SET SNMP Alarm/Event Command ON_OFF ');
                            }else{
                                //button hide
                                $("#allCmdCheck").attr('disabled',true);
                                $("#allCmdCheck").attr('checked',false);
                                
                                var len = $('[name="cmdOnOff"]').length;
                                for ( var i = 0; i < len; i++){
                                	var item = $('[name="cmdOnOff"]')[i].disabled=true;
                                	$('[name="cmdOnOff"]')[i].checked=false;
                                }
                                Ext.getCmp('cmdOnOffBtn').setText('GET SNMP Alarm/Event Command ON_OFF');
                            }
                        }
                    },
                }, //xtype : radio
                {
                	fieldLabel : 'Alarm/Event'
                },
				grid ,
		        {
		           	xtype: 'label',
		           	id : 'eventInfolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'select Alarm/Event',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'cmdOnOffBtn',
                    text: 'Get Modem Mode',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            cmdOnOffAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('cmdOnOffWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        cmdOnOffWin = new Ext.Window({
            id     : 'cmdOnOffWindow',
            title : 'Get/Set Alarm/Event Command ON_OFF',
            pageX : 150,
            pageY : 50,
            height : 550,
            width  : 500,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [cmdOnOffFormPanel],
        });

        cmdOnOffWin.show();
        
        }
 	function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {
		return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"cmdOnOff\" name=\"cmdOnOff\" disabled=\"disabled\" value=\"bbb\" /></div>";
	}
 	function dataChkEvent(currentCellValue, metadata, record, rowIndex, colIndex) {
 		//[sp-1026] 모뎀 버그로 인한 일시적인 기능 제한 
 		//return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"eventCheck\" name=\"eventCheck\" value=\"aaa\" /></div>";
 		return "<div class=\"am_button\" style=\"background:none\"><input type=\"checkbox\" id=\"eventCheck\" name=\"eventCheck\" disabled=\"disabled\" checked=\"checked\" value=\"aaa\" /></div>";
	    ////
 	}
 	function chkAllEvent() {
 		if ($("#allEventCheck").is(':checked')) {
 			$("input[name='eventCheck']").attr("checked", "checked");
 		} else {
 		    $("input[name='eventCheck']").attr("checked", false);
 		}
 	}
 	function chkAllCmd() {
 		if ($("#allCmdCheck").is(':checked')) {
 			$("input[name='cmdOnOff']").attr("checked", "checked");
 		} else {
 		    $("input[name='cmdOnOff']").attr("checked", false);
 		}
 	}
    function cmdOnOffAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio4').getValue().inputValue;
		var count = 0;   

		var cmdJson = "";
        if ( actionRequestType == "SET") {
       		count = 0;
    		var tmpJson = '';
            var len = $('[name="eventCheck"]').length;
            for ( var i = 0; i < len; i++){
            	if (  $('[name="eventCheck"]')[i].checked ) {
            		if ( count > 0 )
            			tmpJson = tmpJson + ',';
            		tmpJson = tmpJson + '{"alarmEventTypeId":' + parseInt(eventarray[i].typeId,16) + ',' + '"_statue":';
            		if ( $('[name="cmdOnOff"]')[i].checked ) {
            			tmpJson += '"On"}';
            		}
            		else {
            			tmpJson += '"Off"}';
            		}
            		count++;
            	}
            }
            if ( count == 0 ){
            	Ext.getCmp('eventInfolabel').show();
            	return;
            }
            cmdJson = '[' + tmpJson + ']';
        }
        else { // GET
//         	if ($("#allEventCheck").is(':checked')) {
//         		count = 0;
//         	}
//         	else {
        		count = 0;
        		var first = true;
        		var tmpJson = '';
                var len = $('[name="eventCheck"]').length;
                for ( var i = 0; i < len; i++){
                	if (  $('[name="eventCheck"]')[i].checked ) {
                		if ( count > 0 )
                			tmpJson = tmpJson + ',';
                		tmpJson = tmpJson + '{"alarmEventTypeId":' + parseInt(eventarray[i].typeId,16) + ',' + '"_statue":"Off"}';
                		count++;
                	}
                }
                if ( count == 0 ){
                	Ext.getCmp('eventInfolabel').show();
                	return;
                }
                cmdJson = '[' + tmpJson + ']';
        	}
//         }
        $('#commandResult').val('');
        Ext.define('cmdOnOffAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        cmdOnOffAjax.request({
            url :  '${ctx}/gadget/device/command/cmdAlarmEventCommandOnOff.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId         : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                count         : count,
                cmds          : cmdJson
                },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('cmdOnOffWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Alarm/Event Command : " + jsonData.AlarmEventCommand +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult
                        "\n cmds : "+ cmdJson;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('cmdOnOffWindow').close();
                var returnString = " # Operation Result!!! : FAIL\n cmds : "+ cmdJson;
                $('#commandResult').val(returnString);
            }
        });

    }

    //Transmit Frequency
    function transmitFrequency()
    {
    	var transmitFrequencyWin = Ext.getCmp('transmitFrequencyWindow');
    	if ( transmitFrequencyWin != undefined ){
    		transmitFrequencyWin.close();
    	}
        transmitFrequencyFormPanel = new Ext.FormPanel({
            id : 'transmitFrequencyform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio5',
                    fieldLabel : 'Get/Set ',

                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('frequency').enable();
                                Ext.getCmp('transmitFrequencyBtn').setText('SET Transmit Frequency');
                            }else{
                                //button hide
                                Ext.getCmp('frequency').disable();
                                Ext.getCmp('transmitFrequencyBtn').setText('GET Transmit Frequency');
                            }
                        }
                    },
                }, //xtype : radio

                {
                    xtype: 'textfield',
                    id : 'frequency',
                    emptyText: '',
                    fieldLabel: 'Transmit Frequency(sec)',
                    regex: /^\d{1,5}$/,
                    disabled : true
                },
		        {
		           	xtype: 'label',
		           	id : 'frequencyInfolabel',
		           	style : {
		           		background : '#ffff00'
		          	},
		           	text : 'input 1 - 65535',
		           	hidden: true
		       }
            ], // items
            buttons : [
                {
                    id: 'transmitFrequencyBtn',
                    text: 'Get Transmit Frequency',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            setTransmitFrequencyAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('transmitFrequencyWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        var transmitFrequencyWin = new Ext.Window({
            id     : 'transmitFrequencyWindow',
            title : 'Get/Set Meter transmitFrequency',
            pageX : 150,
            pageY : 150,
            height : 190,
            width  : 320,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [transmitFrequencyFormPanel],
        });

        transmitFrequencyWin.show();
    }
    
    function setTransmitFrequencyAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio5').getValue().inputValue;
        var actionRequestValue = Ext.getCmp('frequency').getValue();

        var frequency = 0;
        if ( actionRequestType == "SET") {
        	if ( actionRequestValue == '' ||
        			parseInt(actionRequestValue) <= 0 || parseInt(actionRequestValue) > 65535 ){
        		Ext.getCmp('frequencyInfolabel').show();
        		return;
        	}
        	frequency = parseInt(actionRequestValue);
       }
        $('#commandResult').val('');
        Ext.define('transmitFrequencyAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        transmitFrequencyAjax.request({
            url :  '${ctx}/gadget/device/command/cmdTransmitFrequency.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                modemId         : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                second  : frequency 
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('transmitFrequencyWindow').close();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # Transmit Frequency : " + jsonData.transmitFrequency +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult ;
                        $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('transmitFrequencyWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }


    //SNMP Trap Status
    var snmpTrapArray = ['0:Disable', '1:Enable'];
    //SNMP Trap Enable,Disable
    function snmpEnableDisable()
    {
        var snmpEnableDisableWin = Ext.getCmp('snmpEnableDisableWindow');
        if ( snmpEnableDisableWin != undefined ){
            snmpEnableDisableWin.close();
        }
        snmpEnableDisableFormPanel = new Ext.FormPanel({
            id : 'snmpEnableDisableform',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            labelWidth : 100,
            items : [
                {
                    xtype : 'radiogroup',
                    id : 'requestTypeRadio6',
                    fieldLabel : 'Get/Set ',

                    items : [
                        {boxLabel: 'GET', name: 'radio-action', inputValue:'GET', checked: true},
                        {boxLabel: 'SET', name: 'radio-action', inputValue:'SET' , }
                    ],
                    listeners :{
                        change: function(thisRadioGroup, checkedItem){
                            if(checkedItem.inputValue=='SET'){
                                //button show
                                Ext.getCmp('snmpCombo').enable();
                                Ext.getCmp('snmpEnableDisableBtn').setText('SET SNMP Status');
                            }else{
                                //button hide
                                Ext.getCmp('snmpCombo').disable();
                                Ext.getCmp('snmpEnableDisableBtn').setText('GET SNMP Status');
                            }
                        }
                    },
                }, //xtype : radio

                {
                    xtype: 'combo',
                    id : 'snmpCombo',
                    fieldLabel: 'SNMP Status ',
                    queryMode:'local',
                    store:snmpTrapArray ,
                    //displayField:'division',
                    emptyText:'Select a item...',
                    autoSelect:true,
                    forceSelection:true,
                    width: 160,
                    triggerAction : "all",
                    disabled : true
                }
            ], // items
            buttons : [
                {
                    id: 'snmpEnableDisableBtn',
                    text: 'Get SNMP Status',
                    labelWidth : 50,
                    listeners: {
                        click: function(btn,e){
                            //submit action
                            setSnmpEnableDisableAction();
                        }
                    }
                }, {
                    text: 'Cancel',
                    listeners: {
                        click: function (btn, e) {
                            Ext.getCmp('snmpEnableDisableWindow').close();
                        }
                    }
                }
            ] //buttons
        });

        var snmpEnableDisableWin = new Ext.Window({
            id     : 'snmpEnableDisableWindow',
            title : 'Get/Set SNMP Trap Status',
            pageX : 150,
            pageY : 150,
            height : 190,
            width  : 320,
            layout : 'fit',
            bodyStyle   : 'padding: 10px 10px 10px 10px;',
            items  : [snmpEnableDisableFormPanel],
        });

        snmpEnableDisableWin.show();
    }

    function setSnmpEnableDisableAction() {
        var actionRequestType = Ext.getCmp('requestTypeRadio6').getValue().inputValue;
        var actionRequestValue = Ext.getCmp('snmpCombo').getValue();

        var snmpStatus = '0';
        if ( actionRequestType == "SET") {
            if(actionRequestValue == snmpTrapArray[0]){
                actionRequestValue = '0';
            }else if(actionRequestValue == snmpTrapArray[1]){
                actionRequestValue = '1';
            }else{
                return;
            }
            snmpStatus = actionRequestValue.trim();
        }
        $('#commandResult').val('');
        Ext.define('snmpEnableDisableAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("WAIT", '<fmt:message key="aimir.info"/>', {
                        text: 'Get Response From CommandGW...',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        snmpEnableDisableAjax.request({
            url :  '${ctx}/gadget/device/command/cmdModemSnmpTrap.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                mdsId       : modemId,
                loginId       : loginId,
                requestType   : actionRequestType,
                trapStatus  : snmpStatus
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('snmpEnableDisableWindow').close();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var returnString = " # Operation Result : " + jsonData.status +
                        "\n # SNMP Status : " + jsonData.trapStatus +
                        "\n # Message 1 : " + jsonData.rtnStr +
                        "\n # Message 2 : " + jsonData.cmdResult ;
                $('#commandResult').val(returnString);
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                Ext.getCmp('snmpEnableDisableWindow').close();
                var returnString = " # Operation Result : FAIL"
                $('#commandResult').val(returnString);
            }
        });

    }
    /**
	 * resetModem (CoAP)
	 **/
	function resetModem(){
		Ext.Msg.confirm('Modem Reset required to change settings','Reset Modem Now?',
                function(btn, txt){
                    if(btn == 'yes'){
                    	Ext.define('resetModemAjax', {
                            extend: 'Ext.data.Connection',
                            singleton: true,
                            constructor : function(config){
                                this.callParent([config]);
                                this.on("beforerequest", function(){
                                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout2/1000 + 's...',
                                        scope: this,
                                    });
                                });
                                this.on("requestcomplete", function(){
                                    Ext.MessageBox.hide();
                                });
                            }
                        });
                    	$('#commandResult').val("Request Reset Modem....");
                    	resetModemAjax.request({
                	        type : "POST",
                	        timeout : extAjaxTimeout2,
                	        params : {
                	        	  target  			: modemId
                	            , loginId 			: loginId 
                	        },
                	        url : '${ctx}/gadget/device/command/cmdResetModem.do',
                	        success : function (result, request){
                	        	var jsonData = Ext.util.JSON.decode( result.responseText );
                	            if(!jsonData.status){
                	                $('#commandResult').val("[FAIL] " + jsonData.rtnStr);
                	                   return;
                	            }
                	            if(jsonData.status.length>0 && jsonData.status=='SUCCESS'){
                	                $('#commandResult').val("[Success] sending a command\n"+jsonData.rtnStr);
                	            }else{
                	            	$('#commandResult').val("[FAIL] " + jsonData.rtnStr);
                	            }
                	        },
                	        failure: function(result, request){
            	                //폼 윈도우를 닫고, 결과 처리
            	                Ext.MessageBox.hide();
            	                //$('#commandResult').val(result.toString());
            	                if(result.isTimeout){
            	                    $('#commandResult').val("[FAIL]If protocol type is SMS, check the results in Async History tab");
            	                }else{
            	                    $('#commandResult').val("FAIL");
            	                }
            	            }
                	        });
                    }
        });
		
	}



        function winClose() {
            window.close();
        }
        
    /*]]>*/
    </script>
</head>
<body>

<div id="cmdLine"></div>
<div class="cmdLineDiv" style="margin: 10px;">
	<ul>
	    <li><textarea id='commandResult' style="margin: 0px; width: 650px; height: 300px;" readonly="readonly"> </textarea></li>
	</ul>

</div>
</body>
</html>