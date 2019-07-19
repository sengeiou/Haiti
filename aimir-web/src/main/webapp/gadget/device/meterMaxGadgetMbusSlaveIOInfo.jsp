<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<style>
.box-bluegradation-mbusslaveio {
    height: 60px;
    width: 98%;
    border: 1px solid #b4d3f0;
    background: url(${ctx}/themes/images/default/setting/grid_body_bg01.jpg) repeat-x;
    display: block;

    margin-top:10px;
    margin-bottom:10px;
    padding-left:20px;
    padding-top:20px;
    padding-bottom:20px;
    
}
.mbusslaveio-result-text{
    height:80px;
    width: 98%;
    padding-top:10px;
    padding-left:20px;
}
</style>
<script type="text/javascript" charset="utf-8">

    $(function() {
        $('#nsCurrentInfoArea').height($('#nsCurrentInfoDiv').height());
    });

    var msMeterId;
    var msMdsId ="";
    var msLoginId = "";
    var msModelName = "";
    $(document).ready(function() {

        if ( '${result}' != null && '${result}' != '' ) {
            msMdsId = '${result.mdsId}';
            msMeterId = '${result.meterId}';
            msModelName = '${result.modelName}'
            msLoginId = '${loginId}';
            $('#digital1').val('${result.degitalArray[0]}');
            $('#digital2').val('${result.degitalArray[1]}');
            $('#digital3').val('${result.degitalArray[2]}');
            $('#digital4').val('${result.degitalArray[3]}');
            $('#digital5').val('${result.degitalArray[4]}');
            $('#digital6').val('${result.degitalArray[5]}');
            $('#digital7').val('${result.degitalArray[6]}');
            $('#digital8').val('${result.degitalArray[7]}');
            $('#analogCurrent').val('${result.analogCurrent}');
            $('#analogVoltage').val('${result.analogVoltage}');
            $('#convAnalogCurrent').val('${result.analogCurrentCnv}');
            $('#convAnalogVoltage').val( '${result.analogVoltageCnv}');
            $("#commandArea").show();

        }
        else {
             $('#digital1').val(""); 
             $('#digital2').val(""); 
             $('#digital3').val(""); 
             $('#digital4').val(""); 
             $('#digital5').val(""); 
             $('#digital6').val(""); 
             $('#digital7').val(""); 
             $('#digital8').val(""); 
             $("#commandArea").hide();
        } 
    });
    
    var execTimeStore = new Ext.data.ArrayStore({
        fields: [
                 {name: 'execTime'}
            ]
        });
    function popupMbusSlaveScheule(type) {
        if ( type == 'MbusStatus'){
            command = 'Mbus Status Notification Schedule';
        }
        else {
            command = 'Meter Polling Schedule';
        }
        var myData = [];
        execTimeStore.loadData(myData);

         // create the Grid
         var executionTimeGrid = new Ext.grid.GridPanel({
             store: execTimeStore,
             //region: 'west',
             columns: [
                 {
                     id       :'company',
                     header   : 'Execution Time', 
                     width    : 150, 
                     sortable : true, 
                     dataIndex: 'execTime'
                 },
                 {
                     xtype: 'actioncolumn',
                     width: 50,
                     header:'Delete',
                     items: [{
                            //xtype: 'button',
                            //text: 'DELETE ME',
                           icon: '${ctx}/images/allOff.gif',
                         //icon   : '../shared/icons/fam/delete.gif',  // Use a URL in the icon config
                        // tooltip: 'Sell stock',
                         handler: function(executionTimeGrid, rowIndex, colIndex) {
                             var rec = Ext.getCmp('executionTimeGrid').getStore().getAt(rowIndex);
                             Ext.getCmp('executionTimeGrid').getStore().remove(rec);
                           //  alert("Sell " + rec.get('company'));
                         }
                     }]
                 }
             ],
             stripeRows: true,
             autoExpandColumn: 'company',
             height: 200,
             width: 200,
            // title: 'Array Grid',
             // config options for stateful behavior
            // stateful: true,
             id: 'executionTimeGrid',

         });
         var pattern = new RegExp(/^[F0-9]{4}\-[F0-9]{2}\-[F0-9]{2}\s[F0-9]{2}\:[F0-9]{2}:[F0-9]{2}$/);
         var invalidFmtMsg = 'Incorrect format use: YYYY-MM-DD hh:mm:ss.</br> ex.)FFFF-FF-FF FF:00:00'
         var executionTimeForm = new Ext.Panel({
             id: 'executionTime-form',
             frame: true,
             labelAlign: 'left',
             //title: 'Execution Time',
             bodyStyle:'padding:5px',
             width: 520,
             height:200,
             layout: 'border',
             layout: 'column',    // Specifies that the items will now be arranged in columns
             items: [
             executionTimeGrid,
             {
                 xtype: 'fieldset',
                 region: 'east',
                 labelWidth: 100,
                 width:300,
                 title:'Add Execution Time',
                 defaults: {width: 150, border:false},    // Default config options for child items
                 autoHeight: true,
                 bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
                 border: true,
                 style: {
                     "margin-left": "10px", // when you add custom margin in IE 6...
                   "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0",  // you have to adjust for it somewhere else
                 },
                 items: [{
                     fieldLabel: 'Execution Time',
                     name: 'executionTime',
                     xtype: 'textfield',
                     id:'executionTimeText',
                     regex: pattern,
                     regexText: invalidFmtMsg,
                 }, {
                    xtype: 'button',
                   width: '50',
                   text: 'Add',
                   handler: function() {
                       var time =     Ext.getCmp('executionTimeText').getValue();
                       var itemsArray = executionTimeGrid.getStore().data.items;
                       for ( var i = 0; i < itemsArray.length; i++){
                           var rec = itemsArray[i];
                           var temptime = rec.data.execTime;
                           if (temptime == time){
                               Ext.Msg.alert('<fmt:message key='aimir.message'/>','time is duplicate');
                               return;
                           }
                       }
                      
                       if (!pattern.test(time)) {
                           Ext.Msg.alert('<fmt:message key='aimir.message'/>',invalidFmtMsg);
                           return;
                       }
                       var timeType = executionTimeGrid.getStore().recordType;
                       var p = new timeType({
                           execTime: time
                       })
                       var store = Ext.getCmp('executionTimeGrid').getStore();
                       store.insert(store.getCount(), p);
                   }
                 }]
             },

             ]
            
         });

    var mainForm = new Ext.FormPanel({
            id : 'mainForm',
            frame : true,
//            labelAlign : 'left',
//            title : 'Execution Time',
            bodyStyle : 'padding:5px',
            width : 550,
            height : 300,
            // layout: 'border',
            //layout : 'column', // Specifies that the items will now be arranged in columns
            layout: {type:'vbox'},
            items : [executionTimeForm, 
                {
                    xtype : 'container',
                    style : {
                        "margin-top" : "10px"
                    },
                    width : 520,
                    height : 50,
                    layout : {
                        type : 'hbox',
                        align : 'middle',
                        pack : 'end'
                    },
                    items : [
                    {
                        xtype : 'button',
                        text : 'Get',
                        width : 50,
                        handler : function(){
                            console.log("Get=" + msMdsId);
                            getMbusSlaveIOSchedule(msMdsId,type);
                        }
                    }, {
                        xtype : 'button',
                        text : 'Set',
                        width : 50,
                        margin : 10,
                        handler : function(){
                            console.log("Set=" + msMdsId);
                            setMbusSlaveIOSchedule(msMdsId,type);
                        }
                    }, {
                        xtype : 'button',
                        text : 'Cancel',
                        width : 50,
                        margin : 10,
                        handler : function(){
                            Ext.getCmp('scheduleWin').close();
                        }
                    }]
                }
            ]
        });
        if ( !Ext.getCmp('scheduleWin')){
            scheduleWin = new Ext.Window({
            id : 'scheduleWin',
            title : command,
            height : 330,
            width : 600,
            layout : 'fit',
            bodyStyle : 'padding: 10px 10px 10px 10px;',
            items : [ mainForm ],
            });
        }
        scheduleWin.show();
    };
    
    function getMbusSlaveIOSchedule(msMdsId,type){
        console.log("mdsID="+msMdsId);
        var command = '<fmt:message key='aimir.nm.mbusstatus.notify.schedule'/>' +'(Get)';
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        var obisCode = '';
        if (type == 'MbusStatus'){
            obisCode = '0.0.15.0.0.255';
        }
        else if ( type == 'MeterPolling'){
            obisCode = '0.0.15.0.1.255';
        }
        var paramArr = new Array();
            paramArr.push({
                'ACCESSRIGHT' : "RW",
                'ATTRIBUTENAME' : null,
                'ATTRIBUTENO' : '4',
                'CLASSID' : '22',
                'CLASSNAME' : '',
                'DATATYPE' : null,
                'OBISCODE' : obisCode,
                'VALUE' : null
            });
        $
        .getJSON(
                '${ctx}/gadget/device/command/mbusSlaveIOScheduleGetSet.do',
                {
                    'mdsId' : msMdsId,
                    'loginId' : loginId,
                    'modelName' : msModelName,
                    'cmd' : 'cmdMeterParamGet',
                    'parameter' : JSON.stringify(paramArr)
                },
                function(returnData) {
                    $.ajaxSetup({
                        async : false
                    });
                    
                    var result = null;
                    var resultMsg = 'FAIL';
                    var times = null;
                    var resultStr = 'FAIL';
                    if ( returnData.rtnStrList != null && returnData.rtnStrList.length > 0 ){
                        result = returnData.rtnStrList[0];
                        times = result.executionTimes;
                        if ( result.viewMsg != null )
                            resultMsg = result.rtnStr + ":" + JSON.stringify(result.viewMsg);
                        else  
                            resultMsg = result.rtnStr; 
                        resultStr = result.rtnStr;
                    }
                    var times = result.executionTimes;
                    Ext.Msg.hide();
                    var dataArr = new Array();
                    if (times != null ) {
                        for ( var i = 0; i < times.length; i++){
                            var rawArr = new Array();
                            rawArr.push(times[i]);
                            dataArr.push(rawArr);
                        }
                        Ext.getCmp('executionTimeGrid').getStore().loadData(dataArr);
                        if ( times.length > 0 ){
                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', resultStr + '</br>Execution Times were loaded.', null, null);
                        }
                        else {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', resultStr, null, null) ;
                        }
                    } 
                    else {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>', resultStr, null, null);
                    }
                       $('#mbusCommandResult').html(command + ' : ' +resultMsg);
                });
    }
    
    function setMbusSlaveIOSchedule(msMdsId,type){
        console.log("setMbusSlaveIOSchedule mdsID="+msMdsId);
        var itemsArray = Ext.getCmp('executionTimeGrid').getStore().data.items;
        var command = '<fmt:message key='aimir.nm.mbusstatus.notify.schedule'/>' +'(Set)';
        var val = "[";
        for ( var i = 0; i < itemsArray.length; i++){
            var rec = itemsArray[i];
            var temptime = rec.data.execTime; 
            // FFFF-FF-FF FF:00:00 -> FFFFFFFFFF0000
            var time = temptime.substr(0,4)+temptime.substr(5,2)+temptime.substr(8,2)
                    +temptime.substr(11,2)+temptime.substr(14,2)+temptime.substr(17,2);
              val = val + "['" + time + "']";
              if ( i != itemsArray.length - 1)
                  val += ",";
        }
        val = val + "]";
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        var obisCode = '';
        if (type == 'MbusStatus'){
            obisCode = '0.0.15.0.0.255';
        }
        else if ( type == 'MeterPolling'){
            obisCode = '0.0.15.0.1.255';
        }
        var paramArr = new Array();
            paramArr.push({
                'ACCESSRIGHT' : null,
                'ATTRIBUTENAME' : null,
                'ATTRIBUTENO' : '4',
                'CLASSID' : '22',
                'CLASSNAME' : '',
                'DATATYPE' : null,
                'OBISCODE' : obisCode,
                'VALUE' : val
            });
        $
        .getJSON(
                '${ctx}/gadget/device/command/mbusSlaveIOScheduleGetSet.do',
                {
                    'mdsId' : msMdsId,
                    'loginId' : loginId,
                    'cmd' : 'cmdMeterParamSet',
                    'modelName' : msModelName,
                    'parameter' : JSON.stringify(paramArr)
                },
                function(returnData) {
                    $.ajaxSetup({
                        async : false
                    });
                    
                    var result = null;
                    var resultMsg = 'Faild';
                    var times = null;
                    var resultStr = 'Faild';
                    if ( returnData.rtnStrList != null && returnData.rtnStrList.length > 0 ){
                        result = returnData.rtnStrList[0];
                        resultMsg = result.rtnStr + ':' + result.viewMsg != null ? JSON.stringify(result.viewMsg): "";
                        resultStr = result.rtnStr;
                    }
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>', resultStr, null, null);
                       $('#mbusCommandResult').html(command + ' : ' + resultMsg);
                });
    }
 
    function dlmsGetSet(cmd, type, value) {
        console.log("mdsID="+msMdsId);
        var cmdMsg ='<fmt:message key='aimir.nm.change.threshold.current'/>';
        if ( type == 'Voltage') 
            cmdMsg ='<fmt:message key='aimir.nm.change.threshold.voltage'/>';
        cmdMsg += '(' + cmd.substring(13) + ')';

        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        var obisCode = '';
        if (type == 'Current'){
            obisCode = '0.1.94.31.7.255';
        }
        else if ( type == 'Voltage'){
            obisCode = '0.1.94.31.5.255';
        }
        var valueArr = new Array();
        valueArr.push ({'value' : value });
        var paramArr = new Array();
            paramArr.push({
                'ACCESSRIGHT' : null,
                'ATTRIBUTENAME' : null,
                'ATTRIBUTENO' : '2',
                'CLASSID' : '3',
                'CLASSNAME' : '',
                'DATATYPE' : null,
                'OBISCODE' : obisCode,
                'VALUE' : valueArr
            });
        $.getJSON(
                '${ctx}/gadget/device/command/dlmsGetSet.do',
                {
                    'cmd' : cmd,
                   'parameter' : JSON.stringify(paramArr),
                   'mdsId' : msMdsId,
                   'modelName' : msModelName,
                    'loginId' : msLoginId
                },
                function(returnData) {
                    $.ajaxSetup({
                        async : false
                    });
                    var returnStr = "Failed!";
                    var result =  returnStr;
                    var getvalue = "";
                    if ( returnData.rtnStrList != null && returnData.rtnStrList.length > 0 ){
                        var resultArray = returnData.rtnStrList[0].viewMsg;
                        if ( resultArray != null ){
	                        for ( var i = 0; i < resultArray.length; i++ ){
	                            var key = resultArray[i].paramType;
	                            var val = resultArray[i].paramValue;
	                            if ( key == "value"){
	                                getvalue = val;
	                                Ext.getCmp('analogChangeThresValue').setValue(val);
	                            }
	                        }
	                        result =  JSON.stringify(resultArray);
                        }
                        returnStr = returnData.rtnStrList[0].rtnStr;
                       
                    }
                    Ext.Msg.hide();
                    Ext.Msg.alert('', returnStr, null, null);
                       $('#mbusCommandResult').html(cmdMsg + ':' + result);
                });

    }
    var analogChangeThresFormPanel;
    var analogChangeThresWin;
    function analogChangeThres(type) {
        var command = '<fmt:message key='aimir.nm.change.threshold.current'/>'
        if ( type == 'Voltage'){
             command = '<fmt:message key='aimir.nm.change.threshold.voltage'/>';
        }
        if (Ext.getCmp('analogChangeThresWindow')) {
            Ext.getCmp('analogChangeThresWindow').close();
        }
        var pattern = new RegExp(/\d+(\.\d+)?$/);
        var invalidFmtMsg = 'Please input number';
        analogChangeThresFormPanel = new Ext.FormPanel({
            id : 'analogChangeThresForm',
            defaultType : 'fieldset',
            bodyStyle:'padding:1px 1px 1px 1px',
            frame : true,
            width : 400,
            items : [
                {
                    xtype: 'textfield',
                    anchor: '98%',
                    fieldLabel: 'Threshold Value',
                    id : 'analogChangeThresValue',
                    name: 'analogChangeThresValue',
                    regex: pattern,
                    regexText: invalidFmtMsg,
                },

            ], // items
            buttons : [ {
                id : 'analogChangeThresGetButton',
                text : 'Get',
                width : 50,
                listeners : {
                    click : function(btn, e) {
                        // Dialog
                        dlmsGetSet('cmdMeterParamGet', type,''); 
                    }
                }
            }, {
                id : 'analogChangeThresSetButton',
                text : 'Set',
                width : 50,
                listeners : {
                    click : function(btn, e) {
                        var value = Ext.getCmp('analogChangeThresValue').getValue();
                        if (!pattern.test(value)) {
                               Ext.Msg.alert('<fmt:message key='aimir.message'/>',invalidFmtMsg);
                               return;
                        }
                        dlmsGetSet('cmdMeterParamSet', type,value); 
                    }
                }
            }
            , {
                text : 'Cancel',
                width : 50,
                listeners : {
                    click : function(btn, e) {
                        Ext.getCmp('analogChangeThresWindow').close();
                    }
                }
            } ]

        });

        if ( !Ext.getCmp('analogChangeThresWindow')){
                analogChangeThresWin = new Ext.Window({
                id : 'analogChangeThresWindow',
                title : command,
                height : 150,
                width : 400,
                layout : 'fit',
                bodyStyle : 'padding: 10px 10px 10px 10px;',
                items : [ analogChangeThresFormPanel ],
            });
        }
        analogChangeThresWin.show();
    } //~function InputGrpanalogChangeThres()
</script>


<div id="divMbusCurrentInfo" class="tabcontentsbox">
    <div class="blueline" style="height: 400px;">
        <ul class="width margin-t1px">
            <li class="padding-bottom">
                <div id="check1"
                    style="padding-top: 10px;">
                    <label class="check"><fmt:message key="aimir.current"/>&nbsp;<fmt:message key="aimir.info"/></label>
                </div>
                <div id="waku" class="box-bluegradation-mbusslaveio">
                    <table id="table" class="infotable">
                        <tbody>
                            <tr>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>1</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital1" name="digital1" style="border:0px!important;background-color:transparent!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>2</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital2" name="digital2" style="border:0px!important;background-color:transparent!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>3</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital3" name="digital3" style="border:0px!important;background-color:transparent!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>4</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital4" name="digital4" style="border:0px!important;background-color:transparent!important"/>
                            </tr>
                            <tr>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>5</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital5" name="digital5" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>6</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital6" name="digital6" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>7</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital7" name="digital7" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.digital"/>8</b></font></td>
                                <td class="padding-l20px"><input type="text" id="digital8" name="digital8" style="border:0px!important"/>
                            </tr>
                            <tr>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.analog.current"/></b></font></td>
                                <td class="padding-l20px"><input type="text" id="analogCurrent" name="analogCurrent" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.converted"/>&nbsp;<fmt:message key="aimir.nm.analog.current"/></b></font></td>
                                <td class="padding-l20px"><input type="text" id="convAnalogCurrent" name="convAnalogCurrent" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.analog.voltage"/></b></font></td>
                                <td class="padding-l20px"><input type="text" id="analogVoltage" name="analogVoltage" style="border:0px!important"/>
                                <td class="withinput" style="width:115px;"><b><fmt:message key="aimir.nm.converted"/>&nbsp;<fmt:message key="aimir.nm.analog.voltage"/></b></font></td>
                                <td class="padding-l20px"><input type="text" id="convAnalogVoltage" name="convAnalogVoltage" style="border:0px!important"/>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </li>
            <li class="padding-bottom">
                <div id="commandArea" style="margin-top:20px">
                    <div class="headspace floatleft">
                        <div class="floatleft margin-r5">
                            <label class="check">Command</label>
                       </div>
                       <br/><br/>
                       <div id="mbusStatusNotificationButton" class="floatleft margin-r5">
                            <em class="btn_org"><a
                                href="javascript:popupMbusSlaveScheule('MbusStatus')"><fmt:message key="aimir.nm.mbusstatus.notify.schedule"/></a></em>
                        </div>
                    </div>
                    <div id="mbusResultArea">
                        <textarea id="mbusCommandResult"  class="mbusslaveio-result-text" readonly></textarea>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</div>
