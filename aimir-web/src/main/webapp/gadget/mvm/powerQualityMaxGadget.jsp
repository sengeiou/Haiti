<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.powerQuality"/></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/ux/css/GroupHeaderPlugin.css" rel="stylesheet" type="text/css"/>
   
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
        /* ext-js group grid header 정렬 */
        .x-grid3-header-offset table {
          border-collapse: separate;
          border-spacing: 0px;      
        }        
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ux/GroupHeaderPlugin.js"></script>
    <script type="text/javascript">


	    //탭초기화
	    //commonDateTabButtonType2.jsp 사용시 탭초기화
        var tabs = {btn_daily:0,btn_hourly:0,btn_monthlyPeriod:0,btn_weekDaily:0,btn_seasonal:0,btn_yearly:0, daily:0,hourly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
        var tabNames = {};

        var tabs8 = {btn_daily:0,btn_hourly:0,btn_monthlyPeriod:0,btn_weekDaily:0,btn_seasonal:0,btn_yearly:0, daily:0,hourly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
        var tabNames8 = {};

        var fcChartDataXml;
        var fcChart;

        var supplierId =  ${supplierId};
        var serviceType = ServiceType.Electricity;

        var deviceType_power ;
        var deviceId_power ;
        var lastReadDate ;
        var chromeColAdd = 2;

        var firstLoadVol=true;
        var firstLoadPower=true;

        $(document).ready(function(){
            // Tooltip사용을 위해서 반드시 선언해야 한다.
            Ext.QuickTips.init();
            Ext.Ajax.timeout = 240000; //defaul : 30초   -> 2분으로 변경
            
            changeEquipType_vol();
            changeEquipType_power();

            $("#powerQualityTab").tabs();
            $("#powerDetailTab").subtabs();

            // Tab Click 이벤트
            // 로딩 시 부하를 가져와 Tab클릭시 그리드정보를 다시 불러오지 않고 사용자가 검색시에만 로딩하도록 변경
            /* 
            $(function() { $('#_voltage').bind('click',function(event) {
                        $('#tabType').val(1);
                        getPowerQualityGrid();
                        
                });
            });
            */
            var firstTabClickPower = true;
            var firstLoadPowerTab = $(function() { $('#_power')  .bind('click',function(event) {
                    $('#tabType').val(2);
                    if(firstTabClickPower) {
                    //첫 화면 로딩시 그리드를 표시하기 위해 Tab클릭시 한번만 그리드를 호출하도록 firstTabClickpower변수 설정
                    	getPowerInstrumentGrid();
                    	firstTabClickPower=false;
                    } else {
                    	$('#_power').unbind('click',firstLoadPowerTab);
                    }
                    
                });
            });
            //조회버튼클릭 이벤트 생성
            
            //서치 버튼 이벤트 
            $(function() 
           	{ 
           		$('#btnSearch2').bind('click',function(event) 
       			{ 
       				send2($('#btn_searchDateType').val());       			
       			}); 
           	
           	});

            getDeviceType();
            $("#typeView").selectbox();
            getPowerQualityGrid();//voltage Levels 탭 선택

        });

         $(window).resize(function() {
            fcChartRender();

            if(!(powerQualityGrid === undefined)){
                powerQualityGrid.destroy();
            }
            powerQualityGridOn = false;
            getPowerQualityGrid();

            if(!(powerInstrumentGrid === undefined)){
                powerInstrumentGrid.destroy();
            }
            powerInstrumentGridOn = false;
            getPowerInstrumentGrid();

            if(!(powerInstrumentDetailGrid === undefined)){
                powerInstrumentDetailGrid.destroy();
            }
            powerInstrumentDetailGridOn = false;
            getPowerInstrumentDetailGrid();

         });

        //Equip Type 콤보박스 load
        function getDeviceType() {
            $.getJSON('${ctx}/gadget/mvm/getDeviceTypeByComm.do',
                    function(json) {
                       var deviceTypeCode = json.deviceType;
                       $('#deviceType_vol').loadSelect(deviceTypeCode); 
                       $('#deviceType_vol').selectbox();
                       $('#deviceType_power').loadSelect(deviceTypeCode);
                       $('#deviceType_power').selectbox();
                    }
            );
        }
        
        ////////////////////////////   Volatage Levels 탭 ///////////////////////////////////////
        // 메세지 처리 powerQuality
        function getpowerQualityFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0]  = "<fmt:message key="aimir.date"/>";   // 일자
            fmtMessage[1]  = "<fmt:message key="aimir.equiptype"/>"; // 장비타입
            fmtMessage[2]  = "<fmt:message key="aimir.equipid"/>";  // 장비아이디
            fmtMessage[3]  = "<fmt:message key="aimir.customername"/>"; // 고객명
            fmtMessage[4]  = "<fmt:message key="aimir.powerQuality.voltage.A"/>"; // Voltage(A)
            fmtMessage[5]  = "<fmt:message key="aimir.powerQuality.voltage.B"/>"; // Voltage(B)
            fmtMessage[6]  = "<fmt:message key="aimir.powerQuality.voltage.C"/>"; // Voltage(C)
            fmtMessage[7]  = "<fmt:message key="aimir.min"/>";  // Min
            fmtMessage[8]  = "<fmt:message key="aimir.max"/>";  // Max
            fmtMessage[9]  = "<fmt:message key="aimir.avg"/>";  // Avg
            fmtMessage[10] = "<fmt:message key="aimir.powerQuality.voltage.angle.A"/>"; // Voltage Angle(A)
            fmtMessage[11] = "<fmt:message key="aimir.powerQuality.voltage.angle.B"/>"; // Voltage Angle(B)
            fmtMessage[12] = "<fmt:message key="aimir.powerQuality.voltage.angle.C"/>"; // Voltage Angle(C)
            fmtMessage[13] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[14]  = "<fmt:message key="aimir.firmware.msg09"/>"; // excel export 조회데이터 없음.
            
            return fmtMessage;
        }

        // 조회 조건 전달 powerQuality
        function getpowerQualityCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#tabType').val();
            condArray[cnt++] = $(':input:radio[name=selectType]:checked').val();
            condArray[cnt++] = $('#deviceType_vol option:selected').val() == "" ? -1 : $('#deviceType_vol option:selected').val();
            condArray[cnt++] = $('#deviation').val();
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = $('#equipId_vol').val();
            condArray[cnt++] = $('#vendor_vol option:selected').val() == "" ? -1 : $('#vendor_vol option:selected').val();
            condArray[cnt++] = $('#model_vol option:selected').val() == "" ? -1 : $('#model_vol option:selected').val();

            return condArray;
        }

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        var powerQualityGridStore;
        var powerQualityGridColModel;
        var powerQualityGridOn = false;
        var powerQualityGrid;

        //Voltage Level 그리드
        function getPowerQualityGrid(){

            var arrayObj = getpowerQualityCondition();
            var message  = getpowerQualityFmtMessage();

            var width = $("#powerQaulityGridDiv").width(); 
			if(firstLoadVol) {
				powerQualityGridStore=new Ext.data.JsonStore();
				firstLoadVol=false;
			} else {
             powerQualityGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 20}},
                url: "${ctx}/gadget/mvm/getPowerQualityList.do",
                baseParams:{
                    supplierId      : arrayObj[0],
                    tabType         : arrayObj[1],
                    selectType      : arrayObj[2],
                    deviceType      : arrayObj[3],
                    deviation       : arrayObj[4],
                    dateType        : arrayObj[5],
                    fromDate        : arrayObj[6],
                    toDate          : arrayObj[7],
                    equipId			: arrayObj[8],
                    vendorId  	    : arrayObj[9],
                    modelId	    	: arrayObj[10]
                },
                totalProperty: 'total',
                root:'grid',
                 fields: [
                { name: 'yyyymmdd', type: 'String' },
                { name: 'deviceType', type: 'String' },
                { name: 'deviceId', type: 'String' },
                { name: 'customerName', type: 'String' },
                { name: 'volA_min', type: 'String' },
                { name: 'volA_max', type: 'String' } ,
                { name: 'volA_avg', type: 'String' } ,
                { name: 'vol_angleA_min', type: 'String' } ,
                { name: 'vol_angleA_max', type: 'String' },
                { name: 'vol_angleA_avg', type: 'String' },
                { name: 'volB_min', type: 'String' },
                { name: 'volB_max', type: 'String' },
                { name: 'volB_avg', type: 'String' },
                { name: 'vol_angleB_min', type: 'String' },
                { name: 'vol_angleB_max', type: 'String' },
                { name: 'vol_angleB_avg', type: 'String' },
                { name: 'volC_min', type: 'String' },
                { name: 'volC_max', type: 'String' },
                { name: 'volC_avg', type: 'String' },
                { name: 'vol_angleC_min', type: 'String' },
                { name: 'vol_angleC_max', type: 'String' },
                { name: 'vol_angleC_avg', type: 'String' }
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
            }
            });
			}
            powerQualityGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[0],
                        dataIndex:'yyyymmdd',
                        width: 20 ,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        dataIndex:'deviceType',
                        width: 20,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        dataIndex:'deviceId',
                        width: 20 ,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        dataIndex:'customerName',
                        width: 20,
                        align:'center'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'volA_min',
                        width: 20,
                        align:'right'
                    }
                    ,{
                        header:message[8],
                        dataIndex:'volA_max',
                        width: 20,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'volA_avg',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'vol_angleA_min',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[8],
                        dataIndex:'vol_angleA_max',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'vol_angleA_avg',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'volB_min',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[8],
                        dataIndex:'volB_max',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'volB_avg',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'vol_angleB_min',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[8],
                        dataIndex:'vol_angleB_max',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'vol_angleB_avg',
                        width: 20,
                        align:'right'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'volC_min',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[8],
                        dataIndex:'volC_max',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'volC_avg',
                        width: 20,
                        align:'right'
                    }
                    ,{
                        header:message[7],
                        dataIndex:'vol_angleC_min',
                         width: 20 ,
                         align:'right'
                    }
                    ,{
                        header:message[8], 
                        dataIndex:'vol_angleC_max',
                        width: 20,
                        align:'right'
                    }
                    ,{
                        header:message[9],
                        dataIndex:'vol_angleC_avg',
                        width: 20,
                        align:'right'
                    } 
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                    ,renderer: addTooltip
                },
             rows:[[
              
                 {}
                ,{}
                ,{}
                ,{}
                ,{header:message[4],colspan:3,align:'center'}
                ,{header:message[10],colspan:3,align:'center'}
                ,{header:message[5],colspan:3,align:'center'}
                ,{header:message[11],colspan:3,align:'center'}
                ,{header:message[6],colspan:3,align:'center'}
                ,{header:message[12],colspan:3,align:'center'}
                ]
                ]
            });

            if (powerQualityGridOn == false) {
               
                powerQualityGrid = new Ext.grid.GridPanel({
                   
                    id: 'powerQaulityMaxGrid',
                    store: powerQualityGridStore,
                     cm : powerQualityGridColModel,
                     plugins: [new Ext.ux.plugins.GroupHeaderGrid()],
                    autoScroll: false,
                    width: width,
                    height: 548,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'powerQaulityGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: powerQualityGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                powerQualityGridOn  = true;

            } else {
                
                powerQualityGrid.setWidth(width);
                powerQualityGrid.reconfigure(powerQualityGridStore, powerQualityGridColModel);
                var bottomToolbar = powerQualityGrid.getBottomToolbar();                                                             
                bottomToolbar.bindStore(powerQualityGridStore);
            }
            
        };
        //Excel 출력 이벤트
        var winVoltageLevels;
        function openPowerQualityExcel() {

            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var fmtMessage1 = new Array();
            var condition1  = new Array();

            fmtMessage1 = getpowerQualityFmtMessage();
            condition1 = getpowerQualityCondition();

            obj.condition = condition1;
            obj.fmtMessage = fmtMessage1;
            obj.url = '${ctx}/gadget/mvm/voltageLevelMaxExcelMake.do';
            if(winVoltageLevels)
                winVoltageLevels.close();
            winVoltageLevels = window
                    .open("${ctx}/gadget/mvm/powerQualityexcelDownloadPopup.do",
                            "VoltageLevelsExcel", opts);
            winVoltageLevels.opener.obj = obj;

        };

        ////////////////////////////   Power Instrument 탭 ///////////////////////////////////////
        

        //조회조건 절달 Power Instrument
        function getpowerInstrumentCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#tabType').val();
            condArray[cnt++] = $(':input:radio[name=selectType_power]:checked').val();
            condArray[cnt++] = $('#deviceType_power option:selected').val() == "" ? -1 : $('#deviceType_power option:selected').val();
            condArray[cnt++] = $('#deviation_power').val();
            condArray[cnt++] = $('#searchDateType8').val();
            condArray[cnt++] = $('#searchStartDate8').val();
            condArray[cnt++] = $('#searchEndDate8').val();
            condArray[cnt++] = $('#equipId_power').val();
            condArray[cnt++] = $('#vendor_power option:selected').val() == "" ? -1  : $('#vendor_power option:selected').val();
            condArray[cnt++] = $('#model_power option:selected').val() == "" ? -1 : $('#model_power option:selected').val();

            return condArray;
        }

        // 메세지 처리 power Instrument
        function getpowerInstrumentFmtMessage(){
            var fmtMessage = new Array();
            fmtMessage[0]  = "<fmt:message key="aimir.number"/>";                // 번호
            fmtMessage[1]  = "<fmt:message key="aimir.equiptype"/>";                 // 장비타입
            fmtMessage[2]  = "<fmt:message key="aimir.equipid"/>";               // 장비아이디
            fmtMessage[3]  = "<fmt:message key="aimir.lastreaddate"/>";          // 최종 검침시각
            fmtMessage[4]  = "<fmt:message key="aimir.contractNumber"/>";           // 계약아이디
            fmtMessage[5]  = "<fmt:message key="aimir.customername"/>";         // 고객명
            fmtMessage[6]  = "<fmt:message key="aimir.powerQuality.voltage"/>";  // Voltage
            fmtMessage[7]  = "<fmt:message key="aimir.current3"/>";              // Current
            fmtMessage[8]  = "<fmt:message key="aimir.powerQuality.linevoltage"/>"; //line voltage
            fmtMessage[9]  = "<fmt:message key="aimir.firmware.msg09"/>"; // excel export 조회데이터 없음.
                    
            return fmtMessage;
        }

        var powerInstrumentGridStore;
        var powerInstrumentGridColModel;
        var powerInstrumentGridOn = false;
        var powerInstrumentGrid;

        //Power Instrument 그리드
        function getPowerInstrumentGrid(){

            var arrayObj = getpowerInstrumentCondition();
            var message  = getpowerInstrumentFmtMessage();

            var width = $("#powerInstrumentGridDiv").width(); 
			if(firstLoadPower) {
				powerInstrumentGridStore = new Ext.data.JsonStore();
				firstLoadPower=false;
			} else {
             powerInstrumentGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getPowerInstrumentList.do",
                baseParams:{
                    supplierId      : arrayObj[0],
                    tabType         : arrayObj[1],
                    selectType      : arrayObj[2],
                    deviceType      : arrayObj[3],
                    deviation       : arrayObj[4],
                    dateType        : arrayObj[5],
                    fromDate        : arrayObj[6],
                    toDate          : arrayObj[7],
                    equipId         : arrayObj[8],
                    vendorId        : arrayObj[9],
                    modelId         : arrayObj[10]
                },
                totalProperty: 'total',
                root:'grid',
                // idProperty      : 'no', 
                 fields: [
                { name: 'no', type: 'String' },
                { name: 'yyyymmdd', type: 'String' },
                { name: 'deviceType', type: 'String' },
                { name: 'deviceId', type: 'String' },
                { name: 'lastReadDate', type: 'String' },
                { name: 'contractId', type: 'String' },
                { name: 'customerName', type: 'String' },
                { name: 'voltA', type: 'String' } ,
                { name: 'voltB', type: 'String' } ,
                { name: 'voltC', type: 'String' } ,
                { name: 'currA', type: 'String' },
                { name: 'currB', type: 'String' },
                { name: 'currC', type: 'String' },
                { name: 'line_AB', type: 'String' },
                { name: 'line_CA', type: 'String' },
                { name: 'line_BC', type: 'String' }
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
            }
            });
			}

            powerInstrumentGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[0],
                        dataIndex: 'no',
                        width: 20 ,
                        align:'center'
                    
                     }
                     ,{
                        header:message[1],
                        dataIndex:'deviceType',
                        width: 20,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        dataIndex:'deviceId',
                        width: 20 ,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        dataIndex:'lastReadDate',
                        width: 20,
                        align:'center'
                    }
                    ,{
                        header:message[4],
                        dataIndex:'contractId',
                        width: 20,
                        align:'center'
                    }
                    ,{
                        header:message[5],
                        dataIndex:'customerName',
                        width: 20,
                        align:'center'
                    }
                    ,{
                        header:"A",
                        dataIndex:'voltA',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"B",
                        dataIndex:'voltB',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"C",
                        dataIndex:'voltC',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"A",
                        dataIndex:'currA',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"B",
                        dataIndex:'currB',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"C",
                        dataIndex:'currC',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"AB",
                        dataIndex:'line_AB',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"CA",
                        dataIndex:'line_CA',
                        width: 20 ,
                        align:'right'
                    }
                    ,{
                        header:"BC",
                        dataIndex:'line_BC',
                        width: 20 ,
                        align:'right'
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                    ,renderer: addTooltip
                },
             rows:[[
              
                 {}
                ,{}
                ,{}
                ,{}
                ,{}
                ,{}
                ,{header:message[6],colspan:3,align:'center'}
                ,{header:message[7],colspan:3,align:'center'}
                ,{header:message[8],colspan:3,align:'center'}
                ]
                ]
            });

            if (powerInstrumentGridOn == false) {
               
                powerInstrumentGrid = new Ext.grid.GridPanel({
                   
                    id: 'powerInstrumentMaxGrid',
                    store: powerInstrumentGridStore,
                     cm : powerInstrumentGridColModel,
                     plugins: [new Ext.ux.plugins.GroupHeaderGrid()],
                     selModel    : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        listeners    : {
                            rowselect : function(selectionModel, columnIndex, value) {
                              
                                var param = value.data;
                                gridItemClick(param.deviceType,param.deviceId
                                    ,param.yyyymmdd);
                            }
                        }
                     }),
                    autoScroll: false,
                    width: width,
                    height: 310,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'powerInstrumentGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: powerInstrumentGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                powerInstrumentGridOn  = true;

            } else {
                
                powerInstrumentGrid.setWidth(width);
                powerInstrumentGrid.reconfigure(powerInstrumentGridStore, powerInstrumentGridColModel);
                var bottomToolbar = powerInstrumentGrid.getBottomToolbar();                                                             
                bottomToolbar.bindStore(powerInstrumentGridStore);
            }
            
        };
    

        /**
         * Grid Item 최초 선택시 power Instrument Detail 조회
         * Period, Weekly, Monthly 세가지 검색 조건만 설정됨(조건 추가시 날짜 설정 추가 필요).
         * @param
         * val1 --> type
         * val2 --> 장비 id
         * val3 --> 측정한 날짜 (클릭한 item의 date value)
         */
        function gridItemClick(val1, val2, val3){
            
                              
            $('#btn_periodStartDate').val(val3);
            $('#btn_periodEndDate').val(val3);
            
            deviceType_power = val1;
            deviceId_power = val2;
            lastReadDate = val3.substring(0,8);
   
            //db쿼리 형식의 날짜로 변환.
            var dbDate;
            
            $.ajax({
	            type:"POST",
	            data:{
	            	supplierId:supplierId,
	            	localDate:val3
				},
	            dataType:"json",
	            async:false,
	            url:"${ctx}/common/convertDBDate.do",
	            
	            success:function(json, status) {
	            	dbDate = json.dbDate;
	            }
        	})

            $('#detail').css('display','block');
            
            $('#btn_searchStartDate').val(dbDate);
            $('#btn_searchEndDate').val(dbDate);
            
            
            updatePowerDetailChart();
            fcChartRender();

            getPowerInstrumentDetailGrid();

        } // End of gridItemClick

        var winPowerInstrument;
        function openPowerInstrumentExcel() {
        	
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var fmtMessage1 = new Array();
            var condition1  = new Array();

            fmtMessage1 = getpowerInstrumentFmtMessage();
            condition1 = getpowerInstrumentCondition();

            obj.condition = condition1;
            obj.fmtMessage = fmtMessage1;
            obj.url = '${ctx}/gadget/mvm/powerInstrumentMaxExcelMake.do';

            if(winPowerInstrument)
                winPowerInstrument.close();
            winPowerInstrument = window
                    .open("${ctx}/gadget/mvm/powerQualityexcelDownloadPopup.do",
                            "PowerInstrumentExcel", opts);
            winPowerInstrument.opener.obj = obj;

            
        };
        ////////////////////////////   Volatage Levels 탭  End ///////////////////////////////////////

       function send(){
            $('#detail').css('display','none');

            if($('#tabType').val() == 1){ 
                getPowerQualityGrid();
            }
            else if($('#tabType').val() == 2){
               getPowerInstrumentGrid();
            }
        };

        function send2(_dateType)
       	{
            // 조회조건 검증
            if(!btnValidateSearchCondition(_dateType))
            {

            	return false;
            }
            updatePowerDetailChart();
            getPowerInstrumentDetailGrid();
        }

        ////////////////////////////   Power Instrumnet Detail 탭  /////////////////////////////////
        // 조회 조건 전달 Power Instrument Detail
        function getpowerInstrumentDetailCondition(){
            var cnt = 0;
            var condArray = new Array();
           
            condArray[cnt++] = supplierId;
            condArray[cnt++] = deviceType_power;
            condArray[cnt++] = deviceId_power;
            condArray[cnt++] = $('#deviation_power').val();
            condArray[cnt++] = $('#typeView').val();
            condArray[cnt++] = $('#btn_searchDateType').val();
            
            
            condArray[cnt++] = $('#btn_searchStartDate').val();
            condArray[cnt++] = $('#btn_searchEndDate').val();
            return condArray;
        }

        // 메세지 처리 power Instrument Detail
        function getpowerInstrumentDetailFmtMessage(){
            var cnt = 0;
            var fmtMessage = new Array();

            fmtMessage[cnt++]  = "<fmt:message key="aimir.voltage.v"/>"; // Voltage(V)
            fmtMessage[cnt++]  = "<fmt:message key="aimir.avg"/>";      // Avg
            fmtMessage[cnt++]  = "<fmt:message key="aimir.date"/>";      // 일자

            var type = $('#typeView').val();
            if(type == TypeView.Voltage){
	            fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.A"/>";
	            fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.B"/>";
	            fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.C"/>";
	            fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.unbalance"/>";
            }
            else if(type == TypeView.Current){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.c"/>";
            }
            else if(type == TypeView.VoltageAngle){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.angle.A"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.angle.B"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.angle.C"/>";
            }
            else if(type == TypeView.CurrentAngle){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.angle.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.angle.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.angle.c"/>";
            }
            else if(type == TypeView.VoltageTHD){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.voltage.thd.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.voltage.thd.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.voltage.thd.c"/>";
            }
            else if(type == TypeView.CurrentTHD){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.THD.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.THD.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.current.THD.c"/>";
            }
            else if(type == TypeView.TDD){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.tdd.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.tdd.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.tdd.c"/>";
            }
            else if(type == TypeView.KW){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kw.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kw.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kw.c"/>";
            }
            else if(type == TypeView.KVA){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kva.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kva.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kva.c"/>";
            }
            else if(type == TypeView.KVAR){
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kvar.a"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kvar.b"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.kvar.c"/>";
            }
            //2011.05.17 jhkim 추가
            else if(type == TypeView.DistortionKVA){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionkva.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionkva.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionkva.c"/>";
            }
            else if(type == TypeView.PF){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.pf.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.pf.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.pf.c"/>";
            }
            else if(type == TypeView.DistortionPF){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionpf.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionpf.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.distortionpf.c"/>";
            }
            else if(type == TypeView.vol_1st_harmonic_mag){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol1stharmonicmag.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol1stharmonicmag.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol1stharmonicmag.c"/>";
            }
            else if(type == TypeView.vol_2nd_harmonic_mag){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonicmag.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonicmag.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonicmag.c"/>";
            }
            else if(type == TypeView.curr_1st_harmonic_mag){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr1stharmonicmag.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr1stharmonicmag.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr1stharmonicmag.c"/>";
            }
            else if(type == TypeView.curr_2nd_harmonic_mag){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr2ndharmonicmag.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr2ndharmonicmag.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.curr2ndharmonicmag.c"/>";
            }
            else if(type == TypeView.vol_2nd_harmonic){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonic.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonic.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.vol2ndharmonic.c"/>";
            }
            else if(type == TypeView.CurrentHarmonic){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.currentHarmonic.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.currentHarmonic.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.currentHarmonic.c"/>";
            }
            else if(type == TypeView.ph_fund_vol){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundvol.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundvol.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundvol.c"/>";
            }
            else if(type == TypeView.ph_vol_pqm){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phvolpqm.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phvolpqm.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phvolpqm.c"/>";
            }
            else if(type == TypeView.ph_fund_curr){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundcurr.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundcurr.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phfundcurr.c"/>";
            }
            else if(type == TypeView.ph_curr_pqm){
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phcurrpqm.a"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phcurrpqm.b"/>";
            	fmtMessage[cnt++]  = "<fmt:message key="aimir.phcurrpqm.c"/>";
            }
            else{
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.A"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.B"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.voltage.C"/>";
                fmtMessage[cnt++]  = "<fmt:message key="aimir.powerQuality.unbalance"/>";
            }
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";
            return fmtMessage;
        }

        var powerInstrumentDetailGridStore;
        var powerInstrumentDetailGridColModel;
        var powerInstrumentDetailGridOn = false;
        var powerInstrumentDetailGrid;

        //Power Instrument Detail그리드
        function getPowerInstrumentDetailGrid(){

            var arrayObj = getpowerInstrumentDetailCondition();
            var message  = getpowerInstrumentDetailFmtMessage();

            var width = $("#powerInstrumentDetailGridDiv").width(); 
			
             powerInstrumentDetailGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getPowerDetailList.do",
                baseParams:{
                    supplierId      : arrayObj[0],
                    deviceType      : arrayObj[1],
                    deviceId        : arrayObj[2],
                    deviation       : arrayObj[3],
                    typeView        : arrayObj[4],
                    dateType        : arrayObj[5],
                    fromDate        : arrayObj[6],
                    toDate          : arrayObj[7]
                },
                totalProperty: 'total',
                root:'grid',
                 fields: [
                { name: 'date', type: 'String' },
                { name: 'decimalA', type: 'String' },
                { name: 'decimalB', type: 'String' },
                { name: 'decimalC', type: 'String' },
                { name: 'decimalD', type: 'String' }
                ],
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
            }
            });
			
            if(arrayObj[4] == 1){

            powerInstrumentDetailGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[2],
                        dataIndex: 'date',
                        width: 20 ,
                        align:'center'
                    
                     }
                     ,{
                        header:message[3],
                        dataIndex:'decimalA',
                        width: 20,
                        align:'center'
                        
                    }
                    ,{
                        header:message[4],
                        dataIndex:'decimalB',
                        width: 20 ,
                        align:'center'
                    }
                    ,{
                        header:message[5],
                        dataIndex:'decimalC',
                        width: 20,
                        align:'center'
                    }
                    ,{
                        header:message[6],
                        dataIndex:'decimalD',
                        width: 20,
                        align:'center'
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                }
            });
            }else{
                 powerInstrumentDetailGridColModel = new Ext.grid.ColumnModel({
               
                    columns: [
                        {
                            header:message[2],
                            dataIndex: 'date',
                            width: 20 ,
                            align:'center'
                        
                         }
                         ,{
                            header:message[3],
                            dataIndex:'decimalA',
                            width: 20,
                            align:'center'
                            
                        }
                        ,{
                            header:message[4],
                            dataIndex:'decimalB',
                            width: 20 ,
                            align:'center'
                        }
                        ,{
                            header:message[5],
                            dataIndex:'decimalC',
                            width: 20,
                            align:'center'
                        }
                    ],
                    defaults: {
                         sortable: true
                        ,menuDisabled: true
                        ,width: ((width-30)/4)-chromeColAdd
                    }
            });
            }
          

            if(powerInstrumentDetailGrid){
                powerInstrumentDetailGrid.destroy();
                powerInstrumentDetailGridOn = false;
            }
            if (powerInstrumentDetailGridOn == false) {
               
                powerInstrumentDetailGrid = new Ext.grid.GridPanel({
                   
                    id: 'powerInstrumentDetailMaxGrid',
                    store: powerInstrumentDetailGridStore,
                    cm : powerInstrumentDetailGridColModel,
                    autoScroll: false,
                    width: width,
                    height: 290,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'powerInstrumentDetailGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: powerInstrumentDetailGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                powerInstrumentDetailGridOn  = true;

            } else {
                
                powerInstrumentDetailGrid.setWidth(width);
                powerInstrumentDetailGrid.reconfigure(powerInstrumentDetailGridStore, powerInstrumentDetailGridColModel);
                var bottomToolbar = powerInstrumentDetailGrid.getBottomToolba
                bottomToolbar.bindStore(powerInstrumentDetailGridStore);
            }
            
        };
        
        function exportExcel() {
            if($('#tabType').val() == 1){
               openPowerQualityExcel();
            }
            else if($('#tabType').val() == 2){
               openPowerInstrumentExcel();
            }
        }

        
        function updatePowerDetailChart() {
            
           	$.ajax({
		            type:"POST",
		            data:{
		            	supplierId:supplierId
           	    	    ,deviceType:deviceType_power
           	    	    ,deviceId:deviceId_power
           	    	    ,deviation:$('#deviation_power').val()
           	    	    ,typeView:$('#typeView').val()
           	    	    ,dateType:$('#btn_searchDateType').val()
           	    	    ,fromDate:$('#btn_searchStartDate').val()
           	    	    ,toDate:$('#btn_searchEndDate').val()

					},
		            dataType:"json",
		            url:"${ctx}/gadget/mvm/getPowerDetailListAll.do",
		            
		            success:function(json, status) {
		            	
		            	 var list = json.result.grid;
    	                fcChartDataXml = "<chart "
    	                    + "showValues='0' "
    	                    + "showLabels='1' "
    	                    + "showLegend='1' "
    	                    + "labelStep='"+(list.length / 6)+"' "
    	                    + "labelDisplay='STAGGER' "
    	                    + "useEllipsesWhenOverflow='1' "
    	                    + fChartStyle_Common
    	                    + fChartStyle_Font
    	                    + fChartStyle_MSCol3DLine_nobg
    	                    + ">";
    	                    
    		            var categories = "<categories>";
    		            var dataset1 = "";
    		            var dataset2 = "";
    		            var dataset3 = "";
    		            var dataset4 = "";


    		            var type = $('#typeView').val();
    		            if(type == TypeView.Voltage){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.A'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.B'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.C'/>'>";
    		            	dataset4  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.unbalance'/>'>";
    		            }
    		            else if(type == TypeView.Current){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.current.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.current.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.current.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.VoltageAngle){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.angle.A'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.angle.B'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.angle.C'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.CurrentAngle){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.current.angle.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.current.angle.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.current.angle.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.VoltageTHD){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.voltage.thd.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.voltage.thd.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.voltage.thd.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.CurrentTHD){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.current.THD.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.current.THD.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.current.THD.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.TDD){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.tdd.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.tdd.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.tdd.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.KW){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.kw.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.kw.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.kw.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.KVA){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.kva.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.kva.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.kva.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.KVAR){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.kvar.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.kvar.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.kvar.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            //2011.05.17 jhkim 조건 추가  s
    		            else if(type == TypeView.DistortionKVA){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.distortionkva.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.distortionkva.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.distortionkva.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.PF){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.pf.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.pf.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.pf.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.DistortionPF){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.distortionpf.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.distortionpf.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.distortionpf.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.vol_1st_harmonic_mag){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.vol1stharmonicmag.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.vol1stharmonicmag.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.vol1stharmonicmag.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.vol_2nd_harmonic_mag){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonicmag.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonicmag.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonicmag.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.curr_1st_harmonic_mag){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.curr1stharmonicmag.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.curr1stharmonicmag.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.curr1stharmonicmag.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.curr_2nd_harmonic_mag){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.curr2ndharmonicmag.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.curr2ndharmonicmag.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.curr2ndharmonicmag.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.vol_2nd_harmonic){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonic.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonic.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.vol2ndharmonic.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.CurrentHarmonic){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.currentHarmonic.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.currentHarmonic.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.currentHarmonic.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.ph_fund_vol){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.phfundvol.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.phfundvol.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.phfundvol.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.ph_vol_pqm){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.phvolpqm.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.phvolpqm.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.phvolpqm.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.ph_fund_curr){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.phfundcurr.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.phfundcurr.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.phfundcurr.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            else if(type == TypeView.ph_curr_pqm){
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.phcurrpqm.a'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.phcurrpqm.b'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.phcurrpqm.c'/>'>";
    		            	dataset4  = null;
    		            }
    		            
    		            //조건 추가 e
    		            else{
    		            	dataset1  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.A'/>'>";
    		            	dataset2  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.B'/>'>";
    		            	dataset3  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.voltage.C'/>'>";
    		            	dataset4  = "<dataset seriesName='<fmt:message key='aimir.powerQuality.unbalance'/>'>";
    		            }    
    		            
    	                for( index in list){
    	                    if(index != "indexOf") {
        	                    //alert(list[index].a +", "+list[index].b +", "+list[index].c +", ");
	    	                  	categories += "<category label='"+list[index].date+"' />";
	    	                   	dataset1 += "<set value='"+list[index].decimalA+"' />";
	    	                   	dataset2 += "<set value='"+list[index].decimalB+"' />";
	    	                   	dataset3 += "<set value='"+list[index].decimalC+"' />";
	    	                   	if(dataset4 != null) {
	    	                   		dataset4 += "<set value='"+list[index].decimalD+"' />";
	    	                   	}
    	                    }
    	                }
    	                categories += "</categories>";
    	                dataset1 += "</dataset>";
    	                dataset2 += "</dataset>";
    	                dataset3 += "</dataset>";
    	                if(dataset4 != null) {
    	                	dataset4 += "</dataset>";
    	                } else {
    	                	dataset4 = "";
    	                }

    	                if(list.length == 0) {
    						categories = "<categories><category label=' ' /></categories>";
    		                dataset1 = "<dataset seriesName=' '>0</dataset>";
    		                dataset2 = "";
    		                dataset3 = "";
    		                dataset4 = "";
    	                }
    	               
    	                fcChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";

    	                fcChartRender();

    	                // hide();

		            	
		            },// success call back End 
		            error:function(request, status) {
		            	
		            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"grid fetch error!")
		                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
		            }
	        });

    	}

        function fcChartRender() 
       	{
        	if($('#fcChartDiv').is(':visible')) {
	        	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#gadget_body').width() / 2, "330", "0", "0");
	            fcChart.setDataXML(fcChartDataXml);
	            fcChart.setTransparent("transparent");
	            fcChart.render("fcChartDiv");
        	}
        }
        
        function changeEquipType_vol() {
        	
        	var deviceType = $('#deviceType_vol option:selected').text();
            
            if(deviceType == 'All' || deviceType == '') {
            	$('#equipIdTd_vol').hide();
            	$('#equipIdInput_vol').hide();
            	$('#vendorTd_vol').hide();
            	$('#vendorSelect_vol').hide();
            	$('#modelTd_vol').hide();
            	$('#modelSelect_vol').hide();
            } else if(deviceType == 'EndDevice' || deviceType == 'MCU') {
              	$('#equipIdTd_vol').show();
            	$('#equipIdInput_vol').show();
            	$('#vendorTd_vol').hide();
            	$('#vendorSelect_vol').hide();
            	$('#modelTd_vol').hide();
            	$('#modelSelect_vol').hide();
            } else {
            	getVendorListBySubDeviceType_vol(deviceType);
            	
             	$('#equipIdTd_vol').show();
            	$('#equipIdInput_vol').show();
            	$('#vendorTd_vol').show();
            	$('#vendorSelect_vol').show();
            	$('#modelTd_vol').show();
            	$('#modelSelect_vol').show();
            }
            $('#equipId_vol').val('');
        }
        
                
        // voltage 제조사 조회
        function getVendorListBySubDeviceType_vol(deviceType) {
            $.getJSON('${ctx}/gadget/device/getVendorListBySubDeviceType.do'
                    , { 'deviceType' : deviceType
                       ,'subDeviceType' : ""}
                    , function (returnData){
                        var vendorTypeCode = returnData.deviceVendor;
                        $('#vendor_vol').loadSelect(vendorTypeCode);
                        $('#vendor_vol').selectbox();
                        
                        getDeviceModelsByVenendorId_vol();
                    });
           };
           
        // voltage 모델 조회
        function getDeviceModelsByVenendorId_vol() {
        	var deviceType = $('#deviceType_vol option:selected').text();
        	var vendorId = $("#vendor_vol option:selected").val()
        	if(vendorId == "")
        		vendorId = 0;
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                    , {  'vendorId' : vendorId
                        ,'deviceType' : deviceType
                        ,'subDeviceType' : '' }
                    , function (returnData){
                        $('#model_vol').loadSelect(returnData.deviceModels);
                        $('#model_vol').selectbox();
                    });
           };
           
        function changeEquipType_power() {
        	var deviceType = $('#deviceType_power option:selected').text();
            
            if(deviceType == 'All' || deviceType == '') {
            	$('#equipIdTd_power').hide();
            	$('#equipIdInput_power').hide();
            	$('#vendorTd_power').hide();
            	$('#vendorSelect_power').hide();
            	$('#modelTd_power').hide();
            	$('#modelSelect_power').hide();
            } else if(deviceType == 'EndDevice' || deviceType == 'MCU') {
            	$('#equipIdTd_power').show();
            	$('#equipIdInput_power').show();
            	$('#vendorTd_power').hide();
            	$('#vendorSelect_power').hide();
            	$('#modelTd_power').hide();
            	$('#modelSelect_power').hide();
            } else {
            	getVendorListBySubDeviceType_power(deviceType);
            	
            	$('#equipIdTd_power').show();
            	$('#equipIdInput_power').show();
            	$('#vendorTd_power').show();
            	$('#vendorSelect_power').show();
            	$('#modelTd_power').show();
            	$('#modelSelect_power').show();
            }
            $('#equipId_power').val('');
        }

        //power 제조사 조회   
        function getVendorListBySubDeviceType_power(deviceType) {
            $.getJSON('${ctx}/gadget/device/getVendorListBySubDeviceType.do'
                    , { 'deviceType' : deviceType
                       ,'subDeviceType' : ""}
                    , function (returnData){
                        var vendorTypeCode = returnData.deviceVendor;
                        $('#vendor_power').loadSelect(vendorTypeCode);
                        $('#vendor_power').selectbox();
                        
                        getDeviceModelsByVenendorId_power();
                    });
           };

        //power 모델 조회
        function getDeviceModelsByVenendorId_power() {
        	
        	var deviceType = $('#deviceType_power option:selected').text();
        	var vendorId = $("#vendor_power option:selected").val()
        	if(vendorId == "")
        		vendorId = 0;
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByDevice.do'
                    , {  'vendorId' : vendorId
                        ,'deviceType' : deviceType
                        ,'subDeviceType' : '' }
                    , function (returnData){
                        $('#model_power').loadSelect(returnData.deviceModels);
                        $('#model_power').selectbox();
                    });
           };

       

    </script>
    
    <style type="text/css">
    
     html {
	overflow: -moz-scrollbars-vertical; 
	overflow-y: scroll;
	}
	 
    
    
    </style>
</head>
<body>
<input type="hidden" id="formSearchStartDate"/>

<input type="hidden" id="formSearchEndDate"/>


	<!-- 전체 탭 (S) -->


	<!--power instrumnetal tab start-->
	<div id="powerQualityTab">
        <ul>
           <li><a class='topTabType' href="#voltage" id="_voltage"  >Voltage Levels</a></li>
           <li><a class='topTabType' href="#power" id="_power"  >Power Instrument</a></li>
        </ul>
		<td><input id="tabType" type="hidden" value="1"></td>
		<!-- 1st 탭 : VoltageLevels (S) -->
		<div id="voltage">
		<div>
			<!-- search-background DIV (S) -->
			<div class="search-bg-withouttabs with-dayoptions-bt">
			<div class="dayoptions-bt">
			   <%@ include file="/gadget/commonDateTabButtonType9.jsp"%>
			</div>
			<div class="dashedline"><ul><li></li></ul></div>

			<div class="searchoption-container">
				<table class="searchoption wfree">
					<tr>
					   <td class="gray11pt withinput"><fmt:message key="aimir.equiptype"/></td>
					   <td class="space5"></td>
					   <td><select id="deviceType_vol" style="width:120px" onchange="javascript:changeEquipType_vol();" ></select></td>
					   <td class="space5"></td>
					   <td id="vendorTd_vol" class="gray11pt withinput"><fmt:message key='aimir.vendor'/></td>
					   <td id="vendorSelect_vol">
							<select id="vendor_vol" onChange="javascript:getDeviceModelsByVenendorId_vol(event);" style="width:120px">
                            	<option value=""><fmt:message key="aimir.all"/></option>
                            </select>
						</td>
					   <td class="space5"></td>
					   <td id="modelTd_vol" class="gray11pt withinput"><fmt:message key='aimir.model'/></td>
					   <td id="modelSelect_vol">
							<select id="model_vol" style="width:120px">
                            	<option value=""><fmt:message key="aimir.all"/></option>
                            </select>
						</td>
					   <td class="space5"></td>
					   <td id="equipIdTd_vol" class="gray11pt withinput"><fmt:message key='aimir.equipid'/></td>
					   <td id="equipIdInput_vol" ><input id="equipId_vol" type="text" style="width:120px" ></input></td>
					   <td class="space20"></td>
					   <td><input name="selectType" type="radio" value="1" checked class="trans"></td>
					   <td class="gray11pt withinput"><fmt:message key="aimir.powerQuality.deviation"/></td>
					   <td class="space5"></td>
					   <td><input id="deviation" type="text" class="day textalign-center blubold" style="width:30px" value="0"></input></td>
					   <td class="space20"></td>
					   <td>
						   <div id="btn">
						   <!--상단 서치 버튼-->
							   <ul><li><a href="javascript:;" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
						   </div>
					   </td>
					</tr>
				</table>
			</div>
			</div>
			<!-- search-background DIV (E) -->
		</div>
			<div id="btn" class="btn_right_top2 margin-t10px">
				<ul><li><a href="javaScript:exportExcel();" class="on">Excel</a></li></ul>
			</div>
			<div class="gadget_body2">
				<div id ="powerQaulityGridDiv">
                </div>
			</div>
		</div>
		<!-- 1st 탭 : VoltageLevels (E) -->


		<!-- 2nd 탭 : PowerInstrument (S) -->
		<div id="power">
		<div>
			<!-- search-background DIV (S) -->
			<div class="search-bg-withouttabs with-dayoptions-bt">
			<div class="dayoptions-bt">
			   <%@ include file="/gadget/commonDateTabButtonType8.jsp"%>
			</div>
			<div class="dashedline"><ul><li></li></ul></div>

			<div class="searchoption-container">
				<table class="searchoption wfree">
					<tr>
					   <td class="gray11pt withinput"><fmt:message key="aimir.equiptype"/></td>
					   <td class="space5"></td>
					   <td><select id="deviceType_power" style="width:120px" onchange="javascript:changeEquipType_power();"></select></td>
					   <td class="space5"></td>
					   <td id="vendorTd_power" class="gray11pt withinput"><fmt:message key='aimir.vendor'/></td>
					   <td id="vendorSelect_power">
							<select id="vendor_power" onChange="javascript:getDeviceModelsByVenendorId_power(event);" style="width:120px">
                            	<option value=""><fmt:message key="aimir.all"/></option>
                            </select>
						</td>
					   <td class="space5"></td>
					   <td id="modelTd_power" class="gray11pt withinput"><fmt:message key='aimir.model'/></td>
					   <td id="modelSelect_power">
							<select id="model_power" style="width:120px">
                            	<option value=""><fmt:message key="aimir.all"/></option>
                            </select>
						</td>
					   <td class="space5"></td>
					   <td id="equipIdTd_power" class="gray11pt withinput"><fmt:message key='aimir.equipid'/></td>
   					   <td id="equipIdInput_power" ><input id="equipId_power" type="text" style="width:120px" ></input></td>
					   <td class="space20"></td>
					   <td><input name="selectType_power" type="radio" value="1" checked class="trans"></td>
					   <td class="gray11pt withinput"><fmt:message key="aimir.powerQuality.deviation"/></td>
					   <td class="space5"></td>
					   <td><input id="deviation_power" type="text" class="day textalign-center blubold" style="width:30px" value="0"></input></td>
					   <td class="space5"></td>
					   <td class="gray11pt withinput"><fmt:message key="aimir.powerQuality.over"/></td>
					   <td id="hideArea1" class="space5"></td>
					   <td id="hideArea2"><input name="selectType_power" type="radio" value="2" class="trans"></td>
					   <td id="hideArea3" class="gray11pt withinput"><fmt:message key="aimir.powerQuality.reverseAngle"/></td>
					   <td class="space20"></td>

					   <td>
						   <div id="btn">
						   <!--상단 서치 버튼-->
							   <ul><li><a href="javascript:;" class="on" id="btnSearch8"><fmt:message key="aimir.button.search" /></a></li></ul>
						   </div>
					   </td>
					</tr>
				</table>
			</div>
			</div>
			<!-- search-background DIV (E) -->
		</div>
			<div id="btn" class="btn_right_top2 margin-t10px">
				<ul><li><a href="javaScript:exportExcel();" class="on">Excel</a></li></ul>
			</div>
			<div class="gadget_body2">
                <div id="powerInstrumentGridDiv"></div>
			</div>


			<!-- Subtab : 전체 (S) -->
			<div id="powerDetailTab">
				<ul>
				   <li><a href="#detail" id="_detail"><fmt:message key="aimir.view.detail"/></a></li>
				</ul>

				<!-- Subtab : 1st (상세검색) (S) -->
				<div id="detail" class="tabcontentsbox" style="display:none;">
					<div class="blueline" style="height:450px;">
						<ul><li>

							<div class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row1">
  								<div class="dayoptions-bt">
								   <%@ include file="/gadget/commonDateTabButtonType7.jsp"%>
								</div>
								<div class="dashedline"><ul><li></li></ul></div>

								<div class="searchoption-container">
									<table class="searchoption wfree">
										<tr>
										   <td class="gray11pt withinput"><fmt:message key="aimir.view.type"/></td>
										   <td>
											  <select id="typeView" style="width:180px">
												  <c:forEach var="combo" items="${combo}">
												  <option value="${combo.id}">${combo.name}</option>
												  </c:forEach>
											  </select>
										   </td>
										   	<td class="space20"></td>
										   <td><div id="btn">
												   <ul><li><a href="javascript:;" class="on" id="btnSearch2"><fmt:message key="aimir.button.search" /></a></li></ul>
											   </div>
										   </td>
										</tr>
									</table>
								</div>
							</div>

							<div id="gadget_body">
								<div id="fcChartDiv" class="floatleft width-50">
								    The chart will appear within this DIV. This text will be replaced by the chart.
								</div>
								<div class="floatright width-49 margin-t15px">
                                    <div id="powerInstrumentDetailGridDiv"></div>
									
								</div>
							</div>

						</li></ul>
						</div>
						<!-- (상세검색 : Detail) (E) -->

				</div>
				<!-- Subtab : 1st (상세검색) (E) -->

			</div>
			<!-- Subtab : 전체 (E) -->

		</div>
		<!-- 2nd 탭 : PowerInstrument (E) -->
    </div>
	<!-- 전체 탭 (E) -->
</body>
</html>
