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
    	if(supplierName == 'KEMCO' || supplierName == 'S-POWER'){
    		cmdList =[ 
            [1, 'cmdOTAStart', 'OTA Command'],
            [2, 'cmdServerIp', 'Read/Write Server IP ex)cmdServerIP or cmdServerIp/127.0.0.1'],
            [3, 'cmdServerPort', 'Read/Write Server PORT ex)cmdServerPort or cmdServerPort/8198'],
            [4, 'cmdLPInterval', 'Read/Write LP Interval ex)cmdLPInterval or cmdLPInterval/1 (param : 1=15, 2=30, 3=45, 4=60)'],
            [5, 'cmdHWResetInterval', 'Read/Write H/W Reset ex)cmdHWResetInterval or cmdHWResetInterval/1 (param : 1=60, 2=120, 3=180, 4=240)'],
            [6, 'cmdNVReset', 'NV Reset (Factory Setting)'],
            [7, 'cmdMNumber', 'Read Mobile Number'],
            [8, 'cmdHWReset', 'H/W Reset'],
            [9, 'cmdEventLog', 'Read Event Log'],
            [10, 'cmdKeyChange', 'Write Static key Change ex)cmdKeyChange/1234567890123456'],
            [11, 'cmdInverterInfo', 'Read Inverter Information'],
            [12, 'cmdInverterSetup', 'Changing metering whether the status of the inverter. ex)cmdInverterSetup/1/0/0001 (param : Port Number, 0=Disable / 1=Enable, inverterid last 4digit)']
    		]
        }else if(supplierName == 'MOE') {
        	cmdList =[              
            [1,  'cmdResetModem', 'Reset Modem. (second) ex)cmdResetModem/10'],
            [2,  'cmdUploadMeteringData', 'Upload Metering Data'], 
            [3,  'cmdFactorySetting', 'Factory Setting'], 
            [4,  'cmdReadModemConfiguration', 'Read Modem Config'],              
            [5,  'cmdSetTime', 'Set Modem Time'], 
            [6,  'cmdSetModemResetInterval', 'Set Reset Interval (minute) ex) cmdSetModemResetInterval/1440'],
            [7,  'cmdSetMeteringInterval', 'Set Metering Interval (minute) ex) cmdSetMeteringInterval/15'],
            [8, 'cmdSetServerIpPort', 'Set Server IP, Port ex)cmdSetServerIpPort/127.0.0.1/8000'], 
            [9, 'cmdSetApn', 'Set APN addr/ID/Password ex)cmdSetApn/net.asiacell.com/id/passwd'], 
            [10, 'cmdOTAStart', 'Modem OTA Command'],
            [11, 'cmdGetMeterStatus', 'Get Meter Status'],
            [12, 'cmdReadModemEventLog', 'Read Event Log. max 1~200 ex)cmdReadModemEventLog/100'],
            [13, 'cmdMeterOTAStart', 'Meter OTA Command ("true" is take over mode) ex)cmdMeterOTAStart/true'],
            [14, 'cmdGetMeterFWVersion', 'Meter F/W Version']]
            //+ "cmdIdentifyDevice           |   Read Modem/Meter Serial \n " 
            //+ "cmdMeterTimeSync             | set Meter TimeSync \n"
            //+ "cmdCurrentValuesMetering     |   Current values Metering \n"
        }else{
        	cmdList =[
        	[1, 'cmdResetModem', 'Reset Modem '],
        	[2, 'cmdFactorySetting', 'Factory Setting '], 
        	[3, 'cmdReadModemConfiguration', 'Read Modem Config '], 
        	[4, 'cmdIdentifyDevice', 'Read Modem/Meter Serial '],
        	[5, 'cmdSetTime', 'Set Time '],
        	[6, 'cmdSetModemResetInterval', 'Set Reset Interval '],
        	[7, 'cmdSetMeteringInterval', 'Set Metering Interval '],
        	[8, 'cmdServerIpPort', 'Set Server IP, Port '],
        	[9, 'cmdSetApn', 'Set APN addr/ID/Password'],
        	[10, 'cmdOTAStart', 'OTA Command '],
        	[11, 'cmdSetMeterTime', 'set Meter Time ']]
        }
    var otaType = "";
    
    $(function(){
    	obj = window.opener.obj;
    });
    
    $(document).ready(function(){
        CMDGrid();

        function CMDGrid() {

            var store = new Ext.data.Store({
                data : cmdList,
                reader : new Ext.data.ArrayReader({
                    id : 'id'
                }, [ 'id', 'cmd', 'description'])
            });

            var grid = new Ext.grid.GridPanel({
                renderTo : 'cmdLine',
                frame : true,
                title : 'COMMAND LINE',
                height : 300,
                width : 670,
                store : store,
                columns : [ {
                    header : "Command",
                    dataIndex : 'cmd',
                    width: 200, align: 'left'
                }, //hidden:true
                {
                    header : "Description",
                    dataIndex : 'description',
                    width: 900, align: 'left'
                } ],
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            $('#command').val(param.cmd);
                        }
                    }
                }),
            });

        }
    	
        var otaWin = new Ext.Window({
            title : '',
            id : 'otaUploadWin',
            applyTo : 'otaUploadFile',
            autoScroll : true,
            width : 350,
            items : new Ext.FormPanel({
                frame:true,
                defaultType: 'fieldset',
                bodyStyle:'padding:0px 0px 0px 0px',
                items: [{xtype: 'label',
                        fieldLabel : 'Do you want OTA Start?',
                        labelStyle: 'font-weight:bold; width : 200px;'}],
                buttons : [{text : '<font id="otaUploadFile2"><fmt:message key="aimir.ok"/></font>'},
                           {text : '<fmt:message key="aimir.cancel"/>',
                            handler : function() {
                                Ext.getCmp('otaUploadWin').hide();
                                content += "FAIL : Uesr is not want to OTA." + "\n";
                                addContent(content);
                                return false;
                            }
                        }]
            }),
            closeAction : 'hide'
            
        }); 
    	
        if(supplierName == 'KEMCO'){
            target = '${ctx}/gadget/device/command/cmdLineKEMCO.do'
            fwType = 'bin'
        }else if(supplierName == 'MOE'){
            target = '${ctx}/gadget/device/command/cmdLineMOE.do'
            fwType = 'bin'
        }else {
            target = '${ctx}/gadget/device/command/cmdLine.do';
            fwType = 'dwl';            
        }

     	otaUpload = new AjaxUpload('otaUploadFile2', {
            name : 'otaFile',
            responseType : 'json',
            onSubmit : function(file , ext){         
                    //파일 확장자 검색
                    if (!(ext && /^(dwl|DWL|bin|BIN|mot|MOT)$/.test(ext))){
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not OTA file');
                        return false;
                    }

                    this._settings.action = target,
                    this._settings.data = {
                        modemId     : obj.modemId,
                        loginId     : obj.loginId,
                        cmd         : otaType,
                        ext         : ext
                    };

            	Ext.getCmp('otaUploadWin').hide();
            	Ext.Msg.wait('Waiting for response.', 'Wait !');
                return true;
            },
            onComplete : function(file, response){
        		Ext.Msg.hide();
            	content += response.rtnStr + "\n";
                addContent(content);
            }
        });
     	
     	/* $('#ext-gen11').bind('click',function() {
        	Ext.getCmp('otaUploadWin').hide();
        	content += "FAIL : Uesr is not want to OTA." + "\n";
            addContent(content); 
     	}) */
    });
    
    function cmdOTAStart(cmd) {
        otaType = cmd;
		Ext.getCmp('otaUploadWin').show();
    } 

    function cmdStart(cmd) {
    	Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.post(target
                , {	"modemId"		: obj.modemId,
        			"cmd"			: cmd,
        			"loginId"		: obj.loginId
                   	}
                , function(json) {
                	Ext.Msg.hide();
                	content += json.rtnStr + "\n";
                    addContent(content);
                }
        );
    }
    
        function keyEvtHandler(e){

        	var event = e || window.event;
        	var keycode = event.keyCode;

            if(keycode == 13){      //enter
            	var cmd = $('#command').val().trim();

               	if(cmd == 'cmdOTAStart' || cmd.indexOf('cmdMeterOTAStart') == 0) {
               		content += "\n> "+ cmd + "\nWait..." + "\n";
               		addContent(content);
               		cmdOTAStart(cmd);
               	} else if (cmd.indexOf('cmd') == 0) {
               		content += "\n> "+ cmd + "\nWait..." + "\n";
               		addContent(content);
               		cmdStart(cmd);
               	} else if(cmd == 'clear') {
                	content = '';
                	addContent(content);
                } else if(cmd == 'exit') {
                    winClose();
                } else {
                	content += "\n> "+ cmd + "\n InValid Command. Please refer to the table above." + "\n";
                	addContent(content);
                }
            
            }else {
                
                return false;
            }
            
            
        }
        
        function buttonEvtHandler(){
        	var cmd = $('#command').val().trim();
        	if(cmd == 'cmdOTAStart' || cmd.indexOf('cmdMeterOTAStart') == 0) {
           		content += "\n> "+ cmd + "\nWait..." + "\n";
           		addContent(content);
           		cmdOTAStart(cmd);
           	} else if (cmd.indexOf('cmd') == 0) {
           		content += "\n> "+ cmd + "\nWait..." + "\n";
           		addContent(content);
           		cmdStart(cmd);
           	} else if(cmd == 'clear') {
            	content = '';
            	addContent(content);
            } else if(cmd == 'exit') {
                winClose();
            } else {
            	content += "\n> "+ cmd + "\n InValid Command. Please refer to the table above." + "\n";
            	addContent(content);
            }
        }
        
        
        function addContent(content) {
        	$('#content').val(content);
        	$('#content').focus();
        	$('#command').val('');
        	$('#command').focus();
        }
        
        function clickEvt(event) {
        	var firstClick = $('#command').val();
        	if(firstClick == 'Please enter commands here.') {
        		$('#command').val('');
        		$('#command').html("<li><input type='text' id='command' style='width: 580px; margin-left: 5px;' onkeydown='javascript:keyEvtHandler(event);'/></li>");
        	}
        }
        
        

        function winClose() {
            window.close();
        }
    /*]]>*/
    </script>
</head>
<body>
<div id="otaUploadFile"></div>
<div id="cmdLine"></div>
<div class="cmdLineDiv" style="margin: 10px;">
	<ul>
	    <li><textarea id="content" style="margin: 0px; width: 650px; height: 230px; font-size:15px;" readonly="readonly"> </textarea></li>
	</ul>
	<ul>
		<li>
			<input type="text" id="command" style="width: 587px; margin-left: 0px; font-size:15px;" value="Please enter commands here." onkeydown="javascript:keyEvtHandler(event);" onclick="javascript:clickEvt(event);"/>
			<span class="am_button"><a href="javascript:buttonEvtHandler();" >Execute</a></span>
		</li>
	</ul>
</div>
</body>
</html>