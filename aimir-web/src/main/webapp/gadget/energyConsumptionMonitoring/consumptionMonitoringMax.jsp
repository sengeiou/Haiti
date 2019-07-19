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
<style type="text/css">

/*달력 UI CSS*/

.x-panel-body {border:none  !important;}
.x-date-picker {border:none  !important;}
.x-date-inner a:hover, .x-date-inner .x-date-disabled a:hover {width:13px !important; height:15px !important;}
.x-date-inner .x-date-selected a{border-color:#c3dcf3 !important; background:#e9f2ff !important;width:13px !important; height:15px !important; text-align:center;}
.x-date-right a {background-image: url(${ctx}/js/extjs/resources/images/default/shared/right-btn2.gif) !important;}
.x-date-left a{background-image: url(${ctx}/js/extjs/resources/images/default/shared/left-btn2.gif) !important;}
.x-date-middle .x-btn-mc em.x-btn-arrow {background-image:url(${ctx}/js/extjs/resources/images/default/toolbar/btn-arrow-light2.gif) !important;}
.x-date-inner th{color:#007bd5 !important; font-weight:bold !important; border:none !important; background:url(${ctx}/js/extjs/resources/images/default/shared/glass-bg2.gif) repeat-x !important;}
.x-date-inner .x-date-today a{border-color:#c3dcf3 !important;}
.x-btn-noicon .x-btn-small .x-btn-text {height:18px !important;}
.ext-gecko .x-btn button, .ext-webkit .x-btn button { padding:0 3px !important;}
table.x-date-inner{ width: 175px;}
table.x-date-inner td{ height:24px !important}
.x-date-inner a:hover, .x-date-inner .x-date-disabled a:hover{width:14px; height:16px;}
.x-date-bottom {border:none !important}
.x-date-highlight_EM {background-color:#85c1ee;}
.x-date-highlight_GM {background-color:#ffd496;}
.x-date-highlight_WM {background-color:#c7e588;}


</style>
<script type="text/javascript">

	var operatorId = "${operatorId}";
    var serviceTypeEM = "${serviceTypeEM}";
    var serviceTypeGM = "${serviceTypeGM}";
    var serviceTypeWM = "${serviceTypeWM}";
    var selDate = "";
    var billDateEM = "";
    var billDateGM = "";
    var billDateWM = "";

	$(document).ready(function(){
	    emergePre();
        $.ajaxSetup({
            async: false
        });

        $(function() { $("#contractsEM").bind("change", function(event) { selDate = ""; getContract(serviceTypeEM); } ); });
        $(function() { $("#contractsGM").bind("change", function(event) { selDate = ""; getContract(serviceTypeGM); } ); });
        $(function() { $("#contractsWM").bind("change", function(event) { selDate = ""; getContract(serviceTypeWM); } ); });

        getSelect(serviceTypeEM);
	    getSelect(serviceTypeGM);
	    getSelect(serviceTypeWM);

	    //getGoogleWeather();

	    // 달력 생성
	    getCalendar();

        $.ajaxSetup({
            async: true
        });
        hide();
	});

    var getSelect = function(serviceType) {

        var params = {
                "operatorId" : operatorId,
                "serviceType" : serviceType
        };

        $.getJSON("${ctx}/gadget/energyConsumptionMonitoring/getSelect.do",
                params,
                function(result) {

                    var contractCount = result.contractCount;
                    var contracts = result.contracts;
                    var select;
                    var ul;

                    if ( contractCount <= 0 ) {
                        if (serviceTypeEM == serviceType) {

                            $("#contractsUlEMDiv").hide();

                        } else if (serviceTypeGM == serviceType) {
                            
                            $("#contractsUlGMDiv").hide();
                        } else if (serviceTypeWM == serviceType) {

                            $("#contractsUlWMDiv").hide();
                        }
                        //ul.hide();
                    } else if ( contractCount > 0) {
                        if (serviceTypeEM == serviceType) {

                            ul = $("#contractsUlEM");
                            select = $("#contractsEM");
                        } else if (serviceTypeGM == serviceType) {
                            
                            ul = $("#contractsUlGM");
                            select = $("#contractsGM");
                        } else if (serviceTypeWM == serviceType) {

                            ul = $("#contractsUlWM");
                            select = $("#contractsWM");
                        }
                        select.pureSelect(result.contracts);
                        select.selectbox();

                        getContract(serviceType);

                        ul.show();
                    }/* else {

                        select.pureSelect(result.contracts);
                        select.selectbox();

                        getContract(serviceType);
                        
                        ul.show();//--수정--//
                    }*/
                }
            );
    };

    var targetMsg;
	var getContract = function(serviceType) {

		var service;
		//var max;
		//var totalMax;
		var unit;

		if (serviceTypeEM == serviceType) {
		        
            service = "EM";
        } else if (serviceTypeGM == serviceType) {
             
            service = "GM";
        } else if (serviceTypeWM == serviceType) {

            service = "WM";
        }
 
	    var params = {
	            "contractId" : $("#contracts" + service).val()
	           ,"operatorId" : operatorId
	           ,"selDate" : selDate 
	    };

	    $.getJSON("${ctx}/gadget/energyConsumptionMonitoring/getContract.do",
	            params,
	            function(result) {

			        if (serviceTypeEM == serviceType) {
		                
			            billDateEM = result.billDate;
			        } else if (serviceTypeGM == serviceType) {
			             
			        	billDateGM = result.billDate;
			        } else if (serviceTypeWM == serviceType) {
		
			        	billDateWM = result.billDate;
			        }

	                $("#lastDay" + service).text(result.lastDay);
	                $("#usage" + service).text(result.usage);
	                $("#bill" + service).text(result.bill);
                    $("#beforeUsage" + service).text(result.beforeUsage);
                    $("#beforeBill" + service).text(result.beforeBill);
                    $("#targetBill" + service).text(result.dailySavingTarget);

	                $("#period" + service).text(result.period);
                    $("#totalUsage" + service).text(result.totalUsage);
                    $("#totalBill" + service).text(result.totalBill);
	                $("#beforeTotalUsage" + service).text(result.beforeTotalUsage);
	                $("#beforeTotalBill" + service).text(result.beforeTotalBill);
	                $("#totalTargetBill" + service).text(result.monthlySavingTarget);
	                
	                $("#displayDate").text(result.displayDate);
	                if(result.target == true) {// 월 사용 그래프에서 월절감 목표 미설정된 에너지원에는 "전년도 최고"를 타겟라벨으로 설정한다.
		                targetMsg = "<fmt:message key='aimir.hems.savings.goal'/>";
	                	$("#targetLabel" + service).text("<fmt:message key='aimir.hems.savings.goal'/>(<fmt:message key='aimir.price.unit'/>)");
	                }else{ // 월절감 목표가 설정된 에너지원에는 "절감 목표"를 타겟라벨으로 설정한다.
	                	targetMsg = "<fmt:message key='aimir.hems.label.lastYearMax'/>";
	                	$("#targetLabel" + service).text("<fmt:message key='aimir.hems.label.lastYearMax'/>(<fmt:message key='aimir.price.unit'/>)");
	                }
                    //dattePickerMonthYear = result.datePickerMonthYear;

					//getChart1(max, unit, result.beforeUsageNumber, result.usageNumber, service);
					//getChart2(totalMax, unit, result.beforeTotalUsageNumber, result.totalUsageNumber, service);
                    getChart1(result.beforeBillNumber, result.billNumber, result.dailySavingTargetNumber, service, result.yAxisMaxValue);
                    getChart2(result.beforeTotalBillNumber, result.totalBillNumber, result.monthlySavingTargetNumber, service, result.totalYAxisMaxValue);                   
	    		}
	        );
	   };

       var getChart1 = function(beforeUsageNumber, usageNumber, dailySavingTarget, service, yMaxValue) {
           var dataXml;
           var color1;
           var color2;
           dataXml = " <chart showValues='0' "
               + fChartStyle_Font
               + "labelDisplay='WRAP' "
               + "showBorder='0' "
               + "chartLeftMargin='20' "
               + "chartRightMargin='20' "
               + "chartTopMargin='20' "
               + "chartBottomMargin='10' " 
               + "showToolTip='1' "
               + "xAxisName='<fmt:message key="aimir.hems.label.unit"/>' ";
              
               if(yMaxValue != 0 ) {
            	   dataXml += ' yAxisMaxValue ="' + yMaxValue + '" ';
               }

           dataXml += '> ' ;

           if(service == "EM") {
               color1= fChartColor_CompareElec[1];
               color2= fChartColor_CompareElec[2];

           }else if(service == "GM") {
        	   color1 = fChartColor_CompareGas[1];
        	   color2 = fChartColor_CompareGas[2];
           }else {
               color1 = fChartColor_CompareWater[1];
               color2 = fChartColor_CompareWater[2];
//               dataXml += fChartStyle_bullet_nobg 
//               + '> ' ;
           }
           dataXml += ' <set label="<fmt:message key='aimir.date.yesterday'/>" value="' + beforeUsageNumber + '"  color="' + color1 + '" /> '
                    + ' <set label="<fmt:message key='aimir.hems.label.indicateDay'/>" value="'+ usageNumber + '"   color="' + color2 + '" />'
                    + ' <trendLines> '
                    + ' <line startValue="' +dailySavingTarget + '" color="FF0000" thickness="3" showOnTop="1" /> '
                    + ' </trendLines>'
                    + ' </chart> ';  
  

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/Column2D.swf", "myChartId", "140", "200", "0", "0");
            myChart.setDataXML(dataXml);
            myChart.render("chart1" + service);
        };

        var getChart2 = function(beforeTotalUsageNumber, totalUsageNumber, monthlySavingTarget, service, yMaxValue) {
            var dataXml;
            var color1;
            var color2;
            dataXml = " <chart showValues='0' "
                + fChartStyle_Font
                + "labelDisplay='WRAP' "
                + "showBorder='0' "
                + "chartLeftMargin='20' "
                + "chartRightMargin='20' "
                + "chartTopMargin='20' "
                + "chartBottomMargin='10' "
                + "xAxisName='<fmt:message key="aimir.hems.label.unit"/>' ";
               
                if(yMaxValue != 0 ) {
                    dataXml += ' yAxisMaxValue ="' + yMaxValue + '" ';
                }
             dataXml += '> ' ;
             if(service == "EM") {
                 color1= fChartColor_CompareElec[1];
                 color2= fChartColor_CompareElec[2];

             }else if(service == "GM") {
                 color1 = fChartColor_CompareGas[1];
                 color2 = fChartColor_CompareGas[2];
             }else {
                 color1 = fChartColor_CompareWater[1];
                 color2 = fChartColor_CompareWater[2];
//                 dataXml += fChartStyle_bullet_nobg 
//                 + '> ' ;
             }
            dataXml += ' <set label="<fmt:message key='aimir.hems.label.lastMonthBill'/>" value="' + beforeTotalUsageNumber + '"  color="' + color1 + '"  /> '
            + ' <set label="<fmt:message key='aimir.hems.label.indicateMonth'/>" value="'+ totalUsageNumber + '"   color="' + color2 + '" />'
            + ' <trendLines> '
            + ' <line startValue="' +monthlySavingTarget + '" color="FF0000" thickness="3" showOnTop="1"  /> '            
            + ' </trendLines>'
            + ' </chart> '; 
            
            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/Column2D.swf", "myChartId", "140", "200", "0", "0");
            myChart.setDataXML(dataXml);
            myChart.render("chart2" + service);

        };

	   /*
       var getChart1 = function(beforeUsageNumber, usageNumber, dailySavingTarget, service) {
           var dataXml;
           if(service == "EM") {

               //dataXml = ' <chart lowerLimit="0" upperLimit="0" numberSuffix="<fmt:message key='aimir.price.unit'/>" showValue="1" targetColor="FF0000" '
               dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000" '
               + ' plotFillColor="' + fChartColor_Elec2[0] + '" '
               + fChartStyle_bullet_nobg 
               + fChartStyle_Font
               + "showBorder='0' "
               + "chartLeftMargin='20' "
               + "chartRightMargin='20' "
               + "chartTopMargin='20' "
               + "chartBottomMargin='10' " 
               + "showToolTip='1' "
               + '> '
               + ' <colorRange> '
               + '  <color minValue="0" maxValue="' + beforeUsageNumber + '" code="85c1ee" /> '
               + '  <color minValue="'+ beforeUsageNumber + '" code="c8d4de"/>'                           
               + ' </colorRange> ';

           }else if(service == "GM") {

               dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000" '
               + ' plotFillColor="' + fChartColor_Gas2[0] + '" '
               + fChartStyle_bullet_nobg 
               + fChartStyle_Font
               + "showBorder='0' "
               + "chartLeftMargin='20' "
               + "chartRightMargin='20' "
               + "chartTopMargin='20' "
               + "chartBottomMargin='10' " 
               + '> '
               + ' <colorRange> '
               + '  <color minValue="0" maxValue="' + beforeUsageNumber + '" code="FED597"/>'
               + '  <color minValue="'+ beforeUsageNumber + '" code="c8d4de"/>'
               + ' </colorRange> ';

           }else {

               dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000"  plotBorderThickness="1" '
               + ' plotFillColor="' + fChartColor_Water2[0] + '" '
               +  fChartStyle_bullet_nobg 
               +  fChartStyle_Font
               + "showBorder='0' "
               + "chartLeftMargin='20' "
               + "chartRightMargin='20' "
               + "chartTopMargin='20' "
               + "chartBottomMargin='10' " 
               + '> '     
               + ' <colorRange> '
               + '  <color minValue="0" maxValue="' +beforeUsageNumber + '" code="C9E38A"/>'
               + '  <color minValue="'+ beforeUsageNumber + '" code="c8d4de"/>'
               + ' </colorRange> ';
           }
           dataXml += '  <value>' + usageNumber + '</value> '
                    + '  <target>' + dailySavingTarget + '</target> '
                    + ' </chart> ';  

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fusionwidgets/VBullet.swf", "myChartId", "140", "200", "0", "0");
            myChart.setDataXML(dataXml);
            myChart.render("chart1" + service);
        };

        var getChart2 = function(beforeTotalUsageNumber, totalUsageNumber, monthlySavingTarget, service) {
            var dataXml;
            if(service == "EM") {
                dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000" ' 
                + ' plotFillColor="' + fChartColor_Elec2[1] + '" '
                +  fChartStyle_bullet_nobg
                +  fChartStyle_Font
                + "showBorder='0' "
                + "chartLeftMargin='20' "
                + "chartRightMargin='20' "
                + "chartTopMargin='20' "
                + "chartBottomMargin='10' "  
                + '> '      
                + ' <colorRange> '
                + '  <color minValue="0" maxValue="' + beforeTotalUsageNumber + '" code="85c1ee"/>'
                + '  <color minValue="'+ beforeTotalUsageNumber + '" code="c8d4de"/>'
                + ' </colorRange> ';            
            }else if(service == "GM") {
                dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000" ' 
                + ' plotFillColor="' + fChartColor_Gas2[1] + '" '
                +  fChartStyle_bullet_nobg 
                +  fChartStyle_Font
                + "showBorder='0' "
                + "chartLeftMargin='20' "
                + "chartRightMargin='20' "
                + "chartTopMargin='20' "
                + "chartBottomMargin='10' " 
                + '> '      
                + ' <colorRange> '
                + '  <color minValue="0" maxValue="' + beforeTotalUsageNumber + '" code="FED68E"/>'
                + '  <color minValue="'+ beforeTotalUsageNumber + '" code="c8d4de"/>'
                + ' </colorRange> ';            
            }else {
                dataXml = ' <chart lowerLimit="0" upperLimit="0" showValue="0" targetColor="FF0000" ' 
                + ' plotFillColor="' + fChartColor_Water2[1] + '" '
                +  fChartStyle_bullet_nobg
                +  fChartStyle_Font 
                + "showBorder='0' "
                + "chartLeftMargin='20' "
                //+ "chartRightMargin='20' "
                + "chartTopMargin='20' "
                + "chartBottomMargin='10' " 
                + '> '      
                + ' <colorRange> '
                + '  <color minValue="0" maxValue="' + beforeTotalUsageNumber + '" code="C9E38A"/>'
                + '  <color minValue="'+ beforeTotalUsageNumber + '" code="c8d4de"/>'
                + ' </colorRange> ';        
            }

            dataXml += '  <value>' + totalUsageNumber + '</value> '
                     + '  <target>' + monthlySavingTarget + '</target> '
                     + ' </chart> ';

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fusionwidgets/VBullet.swf", "myChartId", "140", "200", "0", "0");
            myChart.setDataXML(dataXml);
            myChart.render("chart2" + service);

        };
        */

	Ext.ns('Ext.ux');  
	/* 
	 * @class Ext.ux.DatePicker 
	 * @extends Ext.DatePicker 
	 * Ext.ux.DatePicker 
	 */  
	Ext.ux.DatePicker = Ext.extend(Ext.DatePicker,{  
	      
	    // bind this component to normal <input/> tag  
	    bindToInput : false,  
	      
	    initComponent:function(){  
	        Ext.ux.DatePicker.superclass.initComponent.call(this);  
	        if(this.bindToInput){//create container to render  
	            this.createAlignToContainer();  
	            this.render(this.alignToContainerId);  
	        }  
	    },  

	    // private  
	    onRender : function(container, position){  
	        Ext.ux.DatePicker.superclass.onRender.call(this, container, position);  
	        if(this.bindToInput){  
	            Ext.getDoc().on('mousedown',function(e,t,o){  
	                if(t == this.bindTo.dom || e.getTarget('div.x-date-picker')){  
	                    //do nothing  
	                }else{// hide this component when click other area except <input/> tag and datepicker area   
	                    this.hide();  
	                }  
	            },this);  
	        }  
	    },  
	    // private
	    update : function(date, forceRefresh){
	        var vd = this.activeDate;
	        this.activeDate = date;
	        if(!forceRefresh && vd && this.el){
	            var t = date.getTime();
	            if(vd.getMonth() == date.getMonth() && vd.getFullYear() == date.getFullYear()){
	                this.cells.removeClass("x-date-selected");
	                this.cells.each(function(c){
	                   if(c.dom.firstChild.dateValue == t){
	                       c.addClass("x-date-selected");
	                       setTimeout(function(){
	                            try{c.dom.firstChild.focus();}catch(e){}
	                       }, 50);
	                       return false;
	                   }
	                });
	                return;
	            }
	        }
	        var days = date.getDaysInMonth();
	        var firstOfMonth = date.getFirstDateOfMonth();
	        var startingPos = firstOfMonth.getDay()-this.startDay;
	        if(startingPos <= this.startDay){
	            startingPos += 7;
	        }
	        var pm = date.add("mo", -1);
	        var prevStart = pm.getDaysInMonth()-startingPos;
	        var cells = this.cells.elements;
	        var textEls = this.textNodes;
	        var el = this.el;
	        days += startingPos;
	        var day = 86400000;
	        var d = (new Date(pm.getFullYear(), pm.getMonth(), prevStart)).clearTime();
	        var today = new Date().clearTime().getTime();
	        var sel = date.clearTime().getTime();
	        var min = this.minDate ? this.minDate.clearTime() : Number.NEGATIVE_INFINITY;
	        var max = this.maxDate ? this.maxDate.clearTime() : Number.POSITIVE_INFINITY;
	        var ddMatch = this.disabledDatesRE;
	        var ddText = this.disabledDatesText;
	        var ddays = this.disabledDays ? this.disabledDays.join("") : false;
	        var ddaysText = this.disabledDaysText;
	//        var hdates = this.highlightDates ? ';' + this.highlightDates.join(';') + ';' : false;
	        var hdateEM = this.highlightEM ? ';' + this.highlightEM + ';' : false;
	        var hdateGM = this.highlightGM ? ';' + this.highlightGM + ';' : false;
	        var hdateWM = this.highlightWM ? ';' + this.highlightWM + ';' : false;        
	        var format = this.format;
	        if(this.showToday){
	            var td = new Date().clearTime();
	            var disable = ((td < min) || (td > max) ||
	                (ddMatch && format && ddMatch.test(td.dateFormat(format))) ||
	                (ddays && ddays.indexOf(td.getDay()) != -1));
	            this.todayBtn.setDisabled(disable);
	            this.todayKeyListener[disable ? 'disable' : 'enable']();
	        }
	        var setCellClass = function(cal, cell, isActiveDay, textEls, intDay){
	            cell.title = "";
	            var t = d.getTime();
	            cell.firstChild.dateValue = t;
	            if(t == today){
	                cell.className += " x-date-today";
	                cell.title = cal.todayText;
	            }
	            if(t == sel){
	                cell.className += " x-date-selected";
	                setTimeout(function(){
	                    try{cell.firstChild.focus();}catch(e){}
	                }, 50);
	            }
	
	            if(t < min) {
	                cell.className = " x-date-disabled";
	                cell.title = cal.minText;
	                return;
	            }
	            if(t > max) {
	                cell.className = " x-date-disabled";
	                cell.title = cal.maxText;
	                return;
	            }
	            if(ddays){
	                if(ddays.indexOf(d.getDay()) != -1){
	                    cell.title = ddaysText;
	                    cell.className = " x-date-disabled";
	                    
	                }
	            }
	            if(ddMatch && format){
	                var fvalue = d.dateFormat(format);
	                if(ddMatch.test(fvalue)){
	                    cell.title = ddText.replace("%0", fvalue);
	                    cell.className = " x-date-disabled";
	                }
	            }

	            var billDateTitle = "";
	            if(isActiveDay) {
		            var fvalue = d.dateFormat(format);
		           
		            var billDate = fvalue.substring(6,8); 
		            strInnerHTML = "<ul id='test' class='charge_box'>";
		            if(hdateEM){

		                if(hdateEM.indexOf(';' + billDate + ';') != -1){
			                strInnerHTML +="<li class='elec'></li>"
		                    billDateTitle = "<fmt:message key='aimirhems.title.emBillDate'/>";
		                    cell.title = billDateTitle;
		                }
		            }
		            if(hdateGM){
		                if(hdateGM.indexOf(';' + billDate + ';') != -1){
		                    strInnerHTML +="<li class='gas'></li>"
		                    billDateTitle.length != 0 ? (billDateTitle += ", <fmt:message key='aimirhems.title.gmBillDate'/>") : billDateTitle += "<fmt:message key='aimirhems.title.gmBillDate'/>";
		                    cell.title = billDateTitle;
		                }
		            }
		            if(hdateWM){
		                if(hdateWM.indexOf(';' + billDate + ';') != -1){
		                    strInnerHTML +="<li class='water'></li>"
		                    billDateTitle.length != 0 ? (billDateTitle += ", <fmt:message key='aimirhems.title.wmBillDate'/>") : billDateTitle += "<fmt:message key='aimirhems.title.wmBillDate'/>";
		                    cell.title = billDateTitle;
		                }
		            }
	
		            if(billDateTitle.length != 0) {

		            	textEls.innerHTML = strInnerHTML + intDay+"</ul>";
		            }
		            strInnerHTML = "";
		            billDateTitle = "";
	            }
	        };
	        var i = 0;
	        for(; i < startingPos; i++) {
	            textEls[i].innerHTML = (++prevStart);
	            d.setDate(d.getDate()+1);
	            cells[i].className = "x-date-prevday";
	            setCellClass(this, cells[i], false);
	        }
	        for(; i < days; i++){
	            var intDay = i - startingPos + 1;
	            textEls[i].innerHTML = (intDay);
	            d.setDate(d.getDate()+1);
	            cells[i].className = "x-date-active";
	            setCellClass(this, cells[i], true, textEls[i],intDay);
	        }
	        var extraDays = 0;
	        for(; i < 42; i++) {
	             textEls[i].innerHTML = (++extraDays);
	             d.setDate(d.getDate()+1);
	             cells[i].className = "x-date-nextday";
	             setCellClass(this, cells[i], false);
	        }
	       // this.mbtn.setText(this.monthNames[date.getMonth()] + " " + date.getFullYear());
	        this.mbtn.setText(date.getFullYear() + ". " + this.monthNames[date.getMonth()]);
	        if(!this.internalRender){
	            var main = this.el.dom.firstChild;
	            var w = main.offsetWidth;
	            this.el.setWidth(w + this.el.getBorderWidth("lr"));
	            Ext.fly(main).setWidth(w);
	            this.internalRender = true;
	            if(Ext.isOpera && !this.secondPass){
	                main.rows[0].cells[1].style.width = (w - (main.rows[0].cells[0].offsetWidth+main.rows[0].cells[2].offsetWidth)) + "px";
	                this.secondPass = true;
	                this.update.defer(10, this, [date]);
	            }
	        }
	    },

	    // create container  
	    createAlignToContainer : function(){  
	        var divElement = document.createElement('div');  
	        this.alignToContainerId = Ext.id();  
	        document.body.appendChild(divElement);  
	        divElement.setAttribute('id',this.alignToContainerId)  
	        this.alignToContainer = Ext.get(this.alignToContainerId);  
	        this.alignToContainer.applyStyles("position:absolute");  
	        this.alignToContainer.applyStyles("z-index:99999");  
	    },

		// create monthPicker
	    createMonthPicker : function(){
   	         if(!this.monthPicker.dom.firstChild){
   	             var buf = ['<table border="0" cellspacing="0">'];
   	             for(var i = 0; i < 6; i++){
   	                 buf.push(
   	                     '<tr><td class="x-date-mp-month"><a href="#">', this.monthNames[i].substr(0, 3), '</a></td>',
   	                     '<td class="x-date-mp-month x-date-mp-sep"><a href="#">', this.monthNames[i+6].substr(0, 3), '</a></td>',
   	                     i == 0 ?
   	                     '<td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-prev"></a></td><td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-next"></a></td></tr>' :
   	                     '<td class="x-date-mp-year"><a href="#"></a></td><td class="x-date-mp-year"><a href="#"></a></td></tr>'
   	                 );
   	             }
   	             buf.push(
   	                 '<tr class="x-date-mp-btns"><td colspan="4"><button type="button" class="x-date-mp-ok">',
   	                     this.okText,
   	                    '</button><button type="button" class="x-date-mp-cancel">',
   	                     this.cancelText,
   	                     '</button></td></tr>',
   	                 '</table>'
   	             );
   	             this.monthPicker.update(buf.join(''));
   	            
   	             this.mon(this.monthPicker, 'click', this.onMonthClick, this);
   	             this.mon(this.monthPicker, 'dblclick', this.onMonthDblClick, this);
   	 
   	             this.mpMonths = this.monthPicker.select('td.x-date-mp-month');
   	             this.mpYears = this.monthPicker.select('td.x-date-mp-year');
   	 
   	             this.mpMonths.each(function(m, a, i){
   	                 i += 1;
   	                 if((i%2) == 0){
   	                     m.dom.xmonth = 5 + Math.round(i * .5);
   	                 }else{
   	                     m.dom.xmonth = Math.round((i-1) * .5);
   	                 }
   	             });
   	         }
   	     },  
	    // override  
	    showMonthPicker : function(){  
	        if(!this.disabled){  
	            this.createMonthPicker();  
	            var size = this.el.getSize();  
	            this.monthPicker.setSize(size);  
	            this.monthPicker.child('table').setSize(size);  
	  
	            this.mpSelMonth = (this.activeDate || this.value).getMonth();  
	            this.updateMPMonth(this.mpSelMonth);  
	            this.mpSelYear = (this.activeDate || this.value).getFullYear();  
	            this.updateMPYear(this.mpSelYear);  
	  
	            if(this.format.indexOf('d') != -1){// format with days  
	                this.monthPicker.slideIn('t', {duration:0.2});  
	            }else{//format no days  
	                this.monthPicker.show();  
	                this.monthPicker.child('> table',false).setWidth(this.el.getWidth() - 2);  
	                this.monthPicker.setWidth(this.el.getWidth() - 2);  
	            }  
	        }  
	    },  
	      
	    // override  
	    show : function(){  
	        Ext.ux.DatePicker.superclass.show.call(this);  
	        if(this.format.indexOf('d') == -1){  
	            this.showMonthPicker();  
	        }  
	    },  
	      
	    // override  
	    onMonthClick : function(e, t){  
	        e.stopEvent();  
	        var el = new Ext.Element(t), pn;  
	        if(el.is('button.x-date-mp-cancel')){  
	            if(this.format.indexOf('d') == -1){  
	                this.hide();  
	            }else{  
	                this.hideMonthPicker();  
	            }  
	        }  
	        else if(el.is('button.x-date-mp-ok')){  
	            var d = new Date(this.mpSelYear, this.mpSelMonth, (this.activeDate || this.value).getDate());  
	            if(d.getMonth() != this.mpSelMonth){  
	                // 'fix' the JS rolling date conversion if needed  
	                d = new Date(this.mpSelYear, this.mpSelMonth, 1).getLastDateOfMonth();  
	            }  
	            this.update(d);  
	            if(this.format.indexOf('d') == -1){  
	                this.bindTo.dom.value = d.format(this.format);  
	                this.setValue(Date.parseDate(d.format(this.format),this.format),true);  
	                this.hide();  
	                if(this.fireEvent('select', this, this.value) == true){  
	                    this.validateDate();  
	                }  
	            }else{  
	                this.hideMonthPicker();  
	            }  
	        }  
	        else if((pn = el.up('td.x-date-mp-month', 2))){  
	            this.mpMonths.removeClass('x-date-mp-sel');  
	            pn.addClass('x-date-mp-sel');  
	            this.mpSelMonth = pn.dom.xmonth;  
	        }  
	        else if((pn = el.up('td.x-date-mp-year', 2))){  
	            this.mpYears.removeClass('x-date-mp-sel');  
	            pn.addClass('x-date-mp-sel');  
	            this.mpSelYear = pn.dom.xyear;  
	        }  
	        else if(el.is('a.x-date-mp-prev')){  
	            this.updateMPYear(this.mpyear-10);  
	        }  
	        else if(el.is('a.x-date-mp-next')){  
	            this.updateMPYear(this.mpyear+10);  
	        }  
	    },  
	  
	    // override  
	    onMonthDblClick : function(e, t){  
	        e.stopEvent();  
	        var el = new Ext.Element(t), pn, d;  
	        if((pn = el.up('td.x-date-mp-month', 2))){  
	            d = new Date(this.mpSelYear, pn.dom.xmonth, (this.activeDate || this.value).getDate());  
	            this.update(d);  
	              
	            if(this.format.indexOf('d') == -1){  
	                this.bindTo.dom.value = d.format(this.format);  
	                this.setValue(Date.parseDate(d.format(this.format),this.format),true);  
	                this.hide();  
	                if(this.fireEvent('select', this, this.value) == true){  
	                    this.validateDate();  
	                }  
	            }else{  
	                this.hideMonthPicker();  
	            }  
	        }  
	        else if((pn = el.up('td.x-date-mp-year', 2))){  
	            d = new Date(pn.dom.xyear, this.mpSelMonth, (this.activeDate || this.value).getDate());  
	            this.update(d);  
	              
	            if(this.format.indexOf('d') == -1){  
	                this.bindTo.dom.value = d.format(this.format);  
	                this.setValue(Date.parseDate(d.format(this.format),this.format),true);  
	                this.hide();  
	                if(this.fireEvent('select', this, this.value) == true){  
	                    this.validateDate();  
	                }  
	            }else{  
	                this.hideMonthPicker();  
	            }  
	        }  
	    },  
	      
	    // private  
	    handleDateClick : function(e, t){  
	        e.stopEvent();  
	        if(!this.disabled && t.dateValue && !Ext.fly(t.parentNode).hasClass('x-date-disabled')){  
	            this.cancelFocus = this.focusOnSelect === false;  
	            this.setValue(new Date(t.dateValue));  
	            delete this.cancelFocus;  
	            if(this.fireEvent('select', this, this.value) == true){  
	                this.validateDate();  
	            }  
	        }  
	    },  
	  
	    // private  
	    selectToday : function(){  
	        if(this.todayBtn && !this.todayBtn.disabled){  
	            this.setValue(new Date().clearTime());  
	            if(this.fireEvent('select', this, this.value) == true){  
	                this.validateDate();  
	            }  
	        }  
	    }
	});

	var selectHandler  = function(myDP, date) {

        selDate = date.format('Ymd'); // format : yyyymmdd

        getSelect(serviceTypeEM);
        getSelect(serviceTypeGM);
        getSelect(serviceTypeWM);
	}

	var getCalendar = function() {
		var dayArray = ["<fmt:message key='aimir.hems.day.sun'/>",
			        "<fmt:message key='aimir.hems.day.mon'/>",
			        "<fmt:message key='aimir.hems.day.tue'/>",
			        "<fmt:message key='aimir.hems.day.wed'/>",
			        "<fmt:message key='aimir.hems.day.thu'/>",
			        "<fmt:message key='aimir.hems.day.fri'/>",
			        "<fmt:message key='aimir.hems.day.sat'/>"];
		
		var monthArray = ["<fmt:message key='aimir.hems.month.1'/>",
		     		"<fmt:message key='aimir.hems.month.2'/>",
		     		"<fmt:message key='aimir.hems.month.3'/>",
		     		"<fmt:message key='aimir.hems.month.4'/>",
		     		"<fmt:message key='aimir.hems.month.5'/>",
		     		"<fmt:message key='aimir.hems.month.6'/>",
		     		"<fmt:message key='aimir.hems.month.7'/>",
		     		"<fmt:message key='aimir.hems.month.8'/>",
		     		"<fmt:message key='aimir.hems.month.9'/>",
		     		"<fmt:message key='aimir.hems.month.10'/>",
		     		"<fmt:message key='aimir.hems.month.11'/>",
		     		"<fmt:message key='aimir.hems.month.12'/>",];

		
 		var billDateArray = [];
        //create the date picker
        var myDP = new Ext.ux.DatePicker(
          { 
             startDay: 0// 월요일 부터 시작하도록 설정
             ,format : "Ymd"
            ,todayText : "<fmt:message key='aimir.today'/>"
            ,nextText : "<fmt:message key='aimir.next'/>"
            ,prevText : "<fmt:message key='aimir.previous'/>"
            ,dayNames : dayArray
            ,monthNames : monthArray
            ,highlightEM : billDateEM
            ,highlightGM : billDateGM
            ,highlightWM : billDateWM           
            ,listeners: {
              'select':selectHandler
            }            
          }
        );

        myDP.render('calendar');
        myDP.show();
	};
</script>
</head>

<body>
<div id="wrapper">
<div class="report_right">
	<div class="bg_date monitor_date"><div id="displayDate" class="text_date"></div></div>
	<div class="clear"></div>
	<div id="calendar" class=""></div>
	<div class="calender_label">
		<ul>
			<li><span class="label_elec"></span><fmt:message key='aimirhems.title.emBillDate'/></li>
			<li><span class="label_gas"></span><fmt:message key='aimirhems.title.gmBillDate'/></li>
			<li><span class="label_water"></span><fmt:message key='aimirhems.title.wmBillDate'/></li>
		</ul>
	</div>
</div>

<div class="report_left">

	<!-- em -->
	<div class="overflow_hidden" id="contractsUlEMDiv">
		<!-- em box -->
		<div id="contractsUlEM"  class="contract_no">
			<span class="title_report"><span class="icon_title_report"></span><fmt:message key='aimir.hems.label.elecUseInfo'/></span>
			<span class="icon_line"></span>
			<span class="contract_txt"><fmt:message key='aimir.hems.label.contractFriendlyName'/></span>
			<span><select id="contractsEM" style="width:540px" ></select></span>
		</div>

		<div class="monitor_box">
			<div class="img_elec_house house_space"></div>
			
				<!--em contents-->
				<div class="monitor_report">
			    	<div class="left_half">

						<!-- em : today -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit padding_t10">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.usedDay'/> : </span>
		    				  <span class="monitoring_dateterm" id="lastDayEM"></span>
			    			</div>
			    			
		    				<div class="dataright">
		    					<div id="chart1EM"></div>
		    				</div>
			    			
		    				<div class="dataleft">
			    				<!-- EM today box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="billEM" ><!-- span class="money_symbol">원</span--></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>
			    						
			    						<tr><td><fmt:message key='aimir.usage'/>(kWh)</td>
			    							<td id="usageEM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastdayFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeBillEM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastday'/>(kWh)</td>
			    							<td class="figure2" id="beforeUsageEM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span><fmt:message key='aimir.hems.label.lastYearMax'/>(<fmt:message key='aimir.price.unit'/>)</span></td>
			    							<td class="figure2" id="targetBillEM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// EM today box -->
		    				</div>
		    			</div>
		    			<!--// em : today -->
			    	</div>
		
			    	<div class="right_half">
			    	
			    	
						<!-- em : month -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.monthPeriod'/> : </span>
		    				  <span class="monitoring_dateterm" id="periodEM"></span>
		    				</div>
		    				<div class="dataright">
		    					<div id="chart2EM"></div>
		    				</div>
		    				<div class="dataleft">
		    				
			    				<!-- EM month box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="totalBillEM" ></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>
			    						
			    						<tr><td><fmt:message key='aimir.usage'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="totalUsageEM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonthFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeTotalBillEM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonth'/>(kWh)</td>
			    							<td class="figure2" id="beforeTotalUsageEM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span id='targetLabelEM'></span></td>
			    							<td class="figure2" id="totalTargetBillEM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// EM month box -->
		    				</div>
		    			</div>
		    			<!--// em : month -->
			    
			    	</div>
			    </div>
				<!--//em contents -->
			
		</div>
	</div>
	<!--// em -->



	<!-- gm -->
	<div class="overflow_hidden" id="contractsUlGMDiv">
		<!-- gm box -->
		<div id="contractsUlGM"  class="contract_no">
			<span class="title_report"><span class="icon_title_report"></span><fmt:message key='aimir.hems.label.gasUseInfo'/></span>
			<span class="icon_line"></span>
			<span class="contract_txt"><fmt:message key='aimir.hems.label.contractFriendlyName'/></span>
			<span><select id="contractsGM" style="width:540px" ></select></span>
		</div>
		
		<div class="monitor_box">
			<div class="img_gas_house house_space"></div>
			
				<!--gm contents-->
				<div class="monitor_report">
			    	<div class="left_half">

						<!-- gm : today -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit padding_t10">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.usedDay'/> : </span>
		    				  <span class="monitoring_dateterm" id="lastDayGM"></span>
			    			</div>
			    			
		    				<div class="dataright">
		    					<div id="chart1GM"></div>
		    				</div>
			    			
		    				<div class="dataleft">
			    				<!-- GM today box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="billGM" ><!-- span class="money_symbol">원</span--></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>
			    						
			    						<tr><td><fmt:message key='aimir.usage'/>(㎥)</td>
			    							<td id="usageGM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastdayFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeBillGM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastday'/>(㎥)</td>
			    							<td class="figure2" id="beforeUsageGM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span><fmt:message key='aimir.hems.label.lastYearMax'/>(<fmt:message key='aimir.price.unit'/>)</span></td>
			    							<td class="figure2" id="targetBillGM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// GM today box -->
		    				</div>
		    			</div>
		    			<!--// gm : today -->
			    	</div>
		
			    	<div class="right_half">
			    				    	
						<!-- gm : month -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.monthPeriod'/> : </span>
		    				  <span class="monitoring_dateterm" id="periodGM"></span>
		    				</div>
		    				<div class="dataright">
		    					<div id="chart2GM"></div>
		    				</div>
		    				<div class="dataleft">
		    				
			    				<!-- GM month box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="totalBillGM" ></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>
			    						
			    						<tr><td><fmt:message key='aimir.usage'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="totalUsageGM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonthFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeTotalBillGM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonth'/>(㎥)</td>
			    							<td class="figure2" id="beforeTotalUsageGM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span id='targetLabelGM'></span></td>
			    							<td class="figure2" id="totalTargetBillGM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// GM month box -->
		    				</div>
		    			</div>
		    			<!--// gm : month -->
			    
			    	</div>
			    </div>
				<!--//gm contents -->
			
		</div>
	</div>
	<!--// gm -->



	<!-- wm -->
	<div class="overflow_hidden" id="contractsUlWMDiv">
		<!-- wm box -->
		<div id="contractsUlWM"  class="contract_no">
			<span class="title_report"><span class="icon_title_report"></span><fmt:message key='aimir.hems.label.waterUseInfo'/></span>
			<span class="icon_line"></span>
			<span class="contract_txt"><fmt:message key='aimir.hems.label.contractFriendlyName'/></span>
			<span><select id="contractsWM" style="width:540px" ></select></span>
		</div>

		<div class="monitor_box_last">
			<div class="img_water_house house_space"></div>

				<!--wm contents-->
				<div class="monitor_report">
			    	<div class="left_half">
						<!-- wm : today -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit padding_t10">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.usedDay'/> : </span>
		    				  <span class="monitoring_dateterm" id="lastDayWM"></span>
			    			</div>

		    				<div class="dataright">
		    					<div id="chart1WM"></div>
		    				</div>

		    				<div class="dataleft">
			    				<!-- WM today box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="billWM" ></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>

			    						<tr><td><fmt:message key='aimir.usage'/>(㎥)</td>
			    							<td id="usageWM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastdayFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeBillWM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastday'/>(㎥)</td>
			    							<td class="figure2" id="beforeUsageWM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span><fmt:message key='aimir.hems.label.lastYearMax'/>(<fmt:message key='aimir.price.unit'/>)</span></td>
			    							<td class="figure2" id="targetBillWM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// WM today box -->
		    				</div>
		    			</div>
		    			<!--// wm : today -->
			    	</div>

			    	<div class="right_half">
						<!-- wm : month -->
		    			<div class="mycontent">
		    				<div class="monitoring_tit">
		    				  <span class="icon_arrow2"></span>
		    				  <span class="monitoring_dateterm bold"><fmt:message key='aimir.hems.label.monthPeriod'/> : </span>
		    				  <span class="monitoring_dateterm" id="periodWM"></span>
		    				</div>
		    				<div class="dataright">
		    					<div id="chart2WM"></div>
		    				</div>
		    				<div class="dataleft">
		    				
			    				<!-- WM month box -->
			    				<div class="monitor_bluebox">
								    <table class="data_report">
								    	<tr><th colspan="2" class="nowbalance">
								    			<div class="monitor_balance">
												    <div class="overflow_hidden">
												    	<div class="name_balance"><fmt:message key='aimir.usageFee2'/>(<fmt:message key='aimir.price.unit'/>)</div>
												        <div class="amount_balance" id="totalBillWM" ></div>
												    </div>
												    <div class="lt"></div>
												    <div class="rt"></div>
												</div>
								    		</th>
								    	</tr>
			    						
			    						<tr><td><fmt:message key='aimir.usage'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="totalUsageWM" class="figure2"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonthFee'/>(<fmt:message key='aimir.price.unit'/>)</td>
			    							<td id="beforeTotalBillWM" class="figure"></td>
			    						</tr>
			    						<tr><td><fmt:message key='aimir.hems.label.lastMonth'/>(㎥)</td>
			    							<td class="figure2" id="beforeTotalUsageWM"></td>
			    						</tr>
			    						<tr><td><span class="label_max">&nbsp;</span><span id='targetLabelWM'></span></td>
			    							<td class="figure2" id="totalTargetBillWM"></td>
			    						</tr>
			    					</table>
								</div>
								<!--// WM month box -->
		    				</div>
		    			</div>
		    			<!--// wm : month -->
			    
			    	</div>
			    </div>
				<!--//wm contents -->
			
		</div>
	</div>
	<!--// wm -->

	
	
</div>
</div>
</body>
</html>