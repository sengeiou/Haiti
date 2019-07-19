<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.consumptionRanking"/>(<fmt:message key="aimir.gasmeter"/>)</title>

    <link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FChartStyle.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
    <script type="text/javascript">

	    //초기화                                                             
       
        var searchDateType = "1";
        var supplierId;
        var locationId = 1;
        var startDate;
        var endDate;
        var barGlobalData;
        var dailyGlobalData;
        var weeklyGlobalData;
        var monthlyGlobalData;
        var quaterlyGlobalData;
        var energyType="WM";
        var meterTypeCode="1.3.1.2";
        var detailLocationId = 1;

        $( function() {

    		$('a').click( function(event) {
    			event.preventDefault();
    			return false;
    		});

    	
    		/**
    	     * 유저 세션 정보 가져오기
    	     */
    	    $.getJSON('${ctx}/common/getUserInfo.do',
    	            function(json) {
    	                if(json.supplierId != ""){
    	                    supplierId = json.supplierId;
    	                    
    	                    getRootLocationId();
    	                    getBuildingLookUpMax(false);
    	                    
    	                    //alert( "세션에서 가져온 supplerId : " + supplierId );
    	                }
    	            }
    	    );

    	   
    	    $('#locationList').change( function() {
    	    	searchDateType = $('#dateTypeList').val();
            	var locId = $('#locationList').val();

                if(locId==locationId){
                  return;
                }else{
                	locationId=locId;
                }
                
                getBuildingLookUpMax(false);

                getTotals();
    		});


    	 // 주기적으로 refresh
    	    setInterval("getBuildingLookUpMax()", 1000*60*15);
    		

    	});

        function getBuildingLookUpMax(detail){
            
        	searchDateType = "1";
        
            $.getJSON('${ctx}/gadget/bems/getBuildingLookUpMax.do' , {
            	searchDateType : searchDateType,
            	supplierId : supplierId,
            	locationId : locationId,
            	detailLocationId : detailLocationId,
            	energyType:energyType
            	} ,
            	function( json ){

            		
                    var dailyStr=setUsageData(json.myChartDataDay,"day");
                  
                    var chartDatasDayInfo = json.myChartDataDayInfo;
                    var dayArr = new Array();
                    /* dayArr[0]= "<fmt:message key='aimir.day'/>" + "(" + "<fmt:message key='aimir.hour'/>" + ") " + "<fmt:message key='aimir.usage'/>"; */
                    dayArr[0]= "<fmt:message key='aimir.day'/> " + "<fmt:message key='aimir.usage'/>";
                    dayArr[1]= "<fmt:message key='aimir.avgFee'/>";
                    dayArr[2]= "<fmt:message key='aimir.lowUsage'/>";
                    dayArr[3]= "<fmt:message key='aimir.highUsage'/>";
                    
                    if(chartDatasDayInfo.length > 0) {
    	                dayArr[4]= chartDatasDayInfo[0].INFOTOTAL;
    	                dayArr[5]= chartDatasDayInfo[0].INFOCAVGCHARGE + " <fmt:message key='aimir.price.unit'/>";
    	                dayArr[6]= chartDatasDayInfo[0].INFOMINUSETIME + " <fmt:message key='aimir.hour2'/> " + chartDatasDayInfo[0].INFOMINUSE;
    	                dayArr[7]= chartDatasDayInfo[0].INFOMAXUSETIME + " <fmt:message key='aimir.hour2'/> " + chartDatasDayInfo[0].INFOMAXUSE;
                    }else{
                        dayArr[4]= "-";
    	                dayArr[5]= "-";
    	                dayArr[6]= "-";
    	                dayArr[7]= "-";
                    }
                  
                    var weeklyStr=setUsageData(json.myChartDataWeek,"week");
                    var chartDatasWeekInfo = json.myChartDataWeekInfo; 
                  
                     var weekArr= new Array();
                    weekArr[0]= "<fmt:message key='aimir.week'/> " + "<fmt:message key='aimir.usage'/>"; 
                    weekArr[1]= "<fmt:message key='aimir.avgFee'/>"; 
                    weekArr[2]= "<fmt:message key='aimir.lowUsage'/>";
                    weekArr[3]= "<fmt:message key='aimir.highUsage'/>"; 
                    if(chartDatasWeekInfo.length > 0) {
    	                weekArr[4]= chartDatasWeekInfo[0].INFOTOTAL;
    	                weekArr[5]= chartDatasWeekInfo[0].INFOCAVGCHARGE + " <fmt:message key='aimir.price.unit'/>";
    	                weekArr[6]= chartDatasWeekInfo[0].INFOMINUSETIME + " <fmt:message key='aimir.dayofweek'/> " + chartDatasWeekInfo[0].INFOMINUSE;
    	                weekArr[7]= chartDatasWeekInfo[0].INFOMAXUSETIME + " <fmt:message key='aimir.dayofweek'/> " + chartDatasWeekInfo[0].INFOMAXUSE;
                    }else{
                    	weekArr[4]= "-";
    	                weekArr[5]= "-";
    	                weekArr[6]= "-";
    	                weekArr[7]= "-";
                    
                    }
                   
                     var monthlyStr=setUsageData(json.myChartDataMonth,"month");
               
                    chartDatasMonthInfo = json.myChartDataMonthInfo; 
          
     				var monthArr = new Array();
                    monthArr[0]= "<fmt:message key='aimir.locationUsage.month'/> " + "<fmt:message key='aimir.usage'/>"; 
                    monthArr[1]= "<fmt:message key='aimir.avgFee'/>";
                    monthArr[2]= "<fmt:message key='aimir.lowUsage'/>";
                    monthArr[3]= "<fmt:message key='aimir.highUsage'/>"; 
                    if(chartDatasMonthInfo.length > 0) {
    	                monthArr[4]= chartDatasMonthInfo[0].INFOTOTAL;
    	                monthArr[5]= chartDatasMonthInfo[0].INFOCAVGCHARGE + " <fmt:message key='aimir.price.unit'/>";
    	                monthArr[6]= chartDatasMonthInfo[0].INFOMINUSETIME + " <fmt:message key='aimir.day.mon'/> " + chartDatasMonthInfo[0].INFOMINUSE;
    	                monthArr[7]= chartDatasMonthInfo[0].INFOMAXUSETIME + " <fmt:message key='aimir.day.mon'/> " + chartDatasMonthInfo[0].INFOMAXUSE;
                    }else{
                    	monthArr[4]= "-";
    	                monthArr[5]= "-";
    	                monthArr[6]= "-";
    	                monthArr[7]= "-";
                    }
                   
                    var quaterlyStr=setUsageData(json.myChartDataQuarter,"quater");
                
                    var chartDatasQuarterInfo = json.myChartDataQuarterInfo; 
            
                    var quaterArr = new Array();
                    quaterArr[0]= "<fmt:message key='aimir.quarter'/> " + "<fmt:message key='aimir.usage'/>";
                    quaterArr[1]= "<fmt:message key='aimir.avgFee'/>";  
                    quaterArr[2]= "<fmt:message key='aimir.lowUsage'/>"; 
                    quaterArr[3]= "<fmt:message key='aimir.highUsage'/>"; 
                    if(chartDatasQuarterInfo.length > 0) {
    	                quaterArr[4]= chartDatasQuarterInfo[0].INFOTOTAL;
    	                quaterArr[5]= chartDatasQuarterInfo[0].INFOCAVGCHARGE + " <fmt:message key='aimir.price.unit'/>";
    	                quaterArr[6]= chartDatasQuarterInfo[0].INFOMINUSETIME + " <fmt:message key='aimir.quarter'/> " + chartDatasQuarterInfo[0].INFOMINUSE;
    	                quaterArr[7]= chartDatasQuarterInfo[0].INFOMAXUSETIME + " <fmt:message key='aimir.quarter'/> " + chartDatasQuarterInfo[0].INFOMAXUSE;
                    }else{
                        quaterArr[4]= "-";
    	                quaterArr[5]= "-";
    	                quaterArr[6]= "-";
    	                quaterArr[7]= "-";
                    }


                	
                	  var locList = json.returnLocation;

                	  for ( var i in locList) {
                		  locList[i].name = locList[i].label;
                		  locList[i].id = locList[i].data;            		  
                      }
                	
            		 var barData= setBarData(json.myChartDataLocation);
                   
            		if(!detail){	
            			setGraph(true,barData,dailyStr,weeklyStr,monthlyStr,quaterlyStr,dayArr,weekArr,monthArr,quaterArr);  
            			$('#locationList').pureSelect(locList);      		
    	        		setLocationIdx();
    	        		$('#locationList').selectbox();
            		}else{
            			setGraph(false,barData,dailyStr,weeklyStr,monthlyStr,quaterlyStr,dayArr,weekArr,monthArr,quaterArr);  
                    }
            		getCurrentTime();
                });
        }

        function setLocationIdx(){
           
        	$("#locationList option[value="+locationId+"]").attr('selected', 'selected'); 
        	
        }

  

    function getEnergyTypeUnit(){
        if('EM' == energyType){
           return "kWh";
        }else {
     	   return "㎥";
        }
     }
    function getEnergyTypeColor(idx){
	       if('EM' == energyType){
	          return fChartColor_Elec[idx];
	       }else if('GM' == energyType){
	    	   return fChartColor_Gas[idx];
	       }else if('WM' == energyType){
	    	   return fChartColor_Water[idx];
	       }else if('HM' == energyType){
	    	   return fChartColor_Heat[idx];
	       }
	    }
	
	    function getEnergyData(data){
	        if('EM' == energyType){
	           return data.EMSUM;
	        }else if('GM' == energyType){
	     	   return data.GMSUM;
	        }else if('WM' == energyType){
	     	   return data.WMSUM;
	        }else if('HM' == energyType){
		       return data.HMSUM;
		    }
	     }

	    function getOldEnergyData(data){
	        if('EM' == energyType){
	           return data.OLDEMSUM;
	        }else if('GM' == energyType){
	     	   return data.OLDGMSUM;
	        }else if('WM' == energyType){
	     	   return data.OLDWMSUM;
	        }else if('HM' == energyType){
		       return data.OLDHMSUM;
		    }
	     }
	    function fix(data,num){
	    	data = data*1;
	        if(data >0){
	         return data.toFixed(num);
	        }else{
	         return data;
	        }
	     }

	     
	    function addCommas(nStr)
	    {
	    	nStr += '';
	    	x = nStr.split('.');
	    	x1 = x[0];
	    	x2 = x.length > 1 ? '.' + x[1] : '';
	    	var rgx = /(\d+)(\d{3})/;
	    	while (rgx.test(x1)) {
	    		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	    	}
	    	return x1 + x2;
	    }


	   function getLegend(period,term){

	    	
        	if(period=="day"){
	               if(term==0){
	               	return "<fmt:message key='aimir.date.today'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==1){
	               	return "<fmt:message key='aimir.date.yesterday'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==2){
	               	return "<fmt:message key='aimir.date.today'/> <fmt:message key='aimir.co2formula2'/>";
	               }else if(term==3){
	               	return "<fmt:message key='aimir.date.yesterday'/> <fmt:message key='aimir.co2formula2'/>";
	               }
           
        	}else if(period=="week"){
				  if(term==0){
	               	return "<fmt:message key='aimir.date.thisweek'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==1){
	               	return "<fmt:message key='aimir.date.lastweek'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==2){
	               	return "<fmt:message key='aimir.date.thisweek'/> <fmt:message key='aimir.co2formula2'/>";
	               }else if(term==3){
	               	return "<fmt:message key='aimir.date.lastweek'/> <fmt:message key='aimir.co2formula2'/>";
	               }
			}else if(period=="month"){
				if(term==0){
	               	return "<fmt:message key='aimir.thismonth'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==1){
	               	return "<fmt:message key='aimir.lastyear.samemonth'/> <fmt:message key='aimir.usage'/>";
	               }else if(term==2){
	               	return "<fmt:message key='aimir.thismonth'/> <fmt:message key='aimir.co2formula2'/>";
	               }else if(term==3){
	               	return "<fmt:message key='aimir.lastyear.samemonth'/> <fmt:message key='aimir.co2formula2'/>";
	               }
			 }else if(period=="quater"){
				 if(term==0){
		               	return "<fmt:message key='aimir.date.thisYear'/> <fmt:message key='aimir.usage'/>";
		               }else if(term==1){
		               	return "<fmt:message key='aimir.lastyear'/> <fmt:message key='aimir.usage'/>";
		               }else if(term==2){
		               	return "<fmt:message key='aimir.date.thisYear'/> <fmt:message key='aimir.co2formula2'/>";
		               }else if(term==3){
		               	return "<fmt:message key='aimir.lastyear'/> <fmt:message key='aimir.co2formula2'/>";
		               }
			 }
			 
			 return "";
        }

        
	    function barTip(data){

	       
	        var s;
	        s = data.NAME + "{br}";

	        s += "<fmt:message key='aimir.usage'/> : " + addCommas(fix(data.TOTAL,3))+" "+getEnergyTypeUnit();
	        
	        // The value of the Income will always be 100%, 
	        // so exclude adding that to the DataTip. Only 
	        // add percent when the user gets the Profit DataTip.
	        return s;

	        //return e.item.Month + ":<B>$" + e.item.Profit + "</B>";
	     }
	     
	    function setBarData(data){

			var s;		
			var scale=2;
			s=" YAxisName='<fmt:message key='aimir.usage'/> ["+getEnergyTypeUnit()+"]'"   
	  			 +">";
		    	s=s+"<categories>";   
		    	if(data.length>0){
				for ( var i in data){				
						s=s+"<category label='"+data[i].NAME+"' showLabel='1'/>";		
			    }
			
				s=s+"</categories>";
				
				s=s+"<dataset seriesName='' color='"+getEnergyTypeColor(0)+"'>";			
				
				for ( var i in data){					
					s=s+"<set value='"+data[i].TOTAL+"'  showValue='0' tooltext='"+barTip(data[i])+"' link='JavaScript: isJavaScriptCall=true; send("+data[i].LOCATION_ID+");'/>";
				}
				s=s+"</dataset>";	
				
		    	}else{
		     		s=s+"<category label='00' showLabel='1'/>";
		     		s=s+"</categories>";
		     		s=s+"<dataset seriesName='' color='"+getEnergyTypeColor()+"'>";
		     		s=s+"<set value='00' tooltext='00'/>";	
		     		s=s+"</dataset>";
		    	
		    	}
	       s=s+"</chart>";
	      
	      return s;
	  	}  

	    function usageTip(data){


	        if(getEnergyData(data)== null)
	            return;
	        
	        var s;
	        s = data.MYDATE + "{br}";
	      
	        s += "<fmt:message key='aimir.usage'/> : " + addCommas(fix(getEnergyData(data),3))+" "+getEnergyTypeUnit()+"{br}";
	        
	        s += "<fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.CO2SUM,3)) + " kg"

	        // The value of the Income will always be 100%, 
	        // so exclude adding that to the DataTip. Only 
	        // add percent when the user gets the Profit DataTip.
	        return s;

	        //return e.item.Month + ":<B>$" + e.item.Profit + "</B>";
	     }

	   function dailyTip(data){

		 
            var s;
            s =  data.MYDATE + "{br}";
            s += "<fmt:message key='aimir.date.today'/> : " + addCommas(fix(getEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
            s += "<fmt:message key='aimir.date.yesterday'/> : " + addCommas(fix(getOldEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
            s += "<fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.CO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
            s += "<fmt:message key='aimir.date.yesterday'/> <fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.OLDCO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
         
            return s;
    
         }

	   function monthlyTip(data){			 
           var s;
           s =  data.MYDATE + "{br}";
           s += "<fmt:message key='aimir.thismonth'/> : " + addCommas(fix(getEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.lastyear.samemonth'/> : " + addCommas(fix(getOldEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.CO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.lastyear.samemonth'/> <fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.OLDCO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
        
           return s;
   
        }

	   function weeklyTip(data){			 
           var s;
           s =  data.MYDATE + "{br}";
           s += "<fmt:message key='aimir.date.thisweek'/> : " + addCommas(fix(getEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.date.lastweek'/> : " + addCommas(fix(getOldEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.CO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.date.lastweek'/> <fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.OLDCO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
        
           return s;
        }

	   function quaterlyTip(data){			 
           var s;
           s =  data.MYDATE + "{br}";
           s += "<fmt:message key='aimir.date.thisYear'/> : " + addCommas(fix(getEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.lastyear'/> : " + addCommas(fix(getOldEnergyData(data),3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.CO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
           s += "<fmt:message key='aimir.lastyear'/> <fmt:message key='aimir.co2formula2'/> : " + addCommas(fix(data.OLDCO2SUM,3)) + " "+getEnergyTypeUnit()+"{br}";
        
           return s;
        }
        
	    function setCategoryLabel(data, period) {
			var label = '';
	    	
			if (period === 'week') {
				var day = data.MYDATE;
				var date = data.yyyymmdd;
				date = date.substring(6,8);
				label = date + "\n(" + day + ")";
			} else if (period === 'quater') {
				var quater = data.MYDATE;
				var year = data.yyyy;
				label = year + '/' + quater;
			} else {
				label = data.MYDATE;
			}
			
	    	return label;	    	
	    }
	    
	    function setScale(period) {
	    	var scale = 0;
	    	if (period === 'day') {
	    		scale = 2;
	    	} else if (period === 'week') {
	    		scale = 1;
	    	} else if (period === 'month') {
	    		scale = 1;
	    	} else {
	    		scale = 1;
	    	}
	    	return scale;
	    }
	    
	    function setXAxisName(period) {
	    	var axisName = '';
	    	if (period === 'day') {
	    		axisName = '<fmt:message key="aimir.hour2"/>';
	    	} else if (period === 'week') {
	    		axisName = '<fmt:message key="aimir.dayofweek"/>';
	    	} else if (period === 'month') {
	    		axisName = '<fmt:message key="aimir.locationUsage.month"/>';
	    	} else if (period === 'quater') {
	    		axisName = '<fmt:message key="aimir.quarter"/>';
	    	} else {
	    		axisName = '';
	    	}
	    	return axisName;
	    }

	    function setUsageData(data,period){
	    	var s;
			
			var scale= setScale(period);
		    s=" XAxisName='" + setXAxisName(period) + "'" 
		    +" PYAxisName='<fmt:message key='aimir.usage'/> ["+getEnergyTypeUnit()+"]'"  
	 	    +" SYAxisName='<fmt:message key='aimir.co2formula2'/> [kg]'>";
		    	s=s+"<categories>";   
			if(data.length>0){
	       	   
				for ( var i in data){					
				
					if((i%scale)==0){
						s=s+"<category label='"+setCategoryLabel(data[i], period)+"' showLabel='1'/>";		
					}else{
						s=s+"<category label='"+setCategoryLabel(data[i], period)+"' showLabel='0'/>";
					}								
				}
				s=s+"</categories>";
				
				
				//s=s+"<dataset>";
				s=s+"<dataset seriesName='"+getLegend(period,1)+"' color='"+getEnergyTypeColor(1)+"' >";			
				
				for ( var i in data){						
				
					var tool=dailyTip(data[i]);
					if(period=="week"){
					    tool=weeklyTip(data[i]);
					}else if(period=="month"){
					    tool=monthlyTip(data[i]);
					}else if(period=="quater"){
					    tool=quaterlyTip(data[i]);
					}
					s=s+"<set value='"+getOldEnergyData(data[i])+"' tooltext='"+tool+"' showValue='0'/>";
				}
				s=s+"</dataset>";
				//s=s+"</dataset>";
				
				
				//s=s+"<dataset>";
				s=s+"<dataset seriesName='"+getLegend(period,0)+"' color='"+getEnergyTypeColor(0)+"'>";			
				
				for ( var i in data){						
					
					var tool=dailyTip(data[i]);
					if(period=="week"){
					    tool=weeklyTip(data[i]);
					}else if(period=="month"){
					    tool=monthlyTip(data[i]);
					}else if(period=="quater"){
					    tool=quaterlyTip(data[i]);
					}
					s=s+"<set value='"+getEnergyData(data[i])+"' tooltext='"+tool+"' showValue='0'/>";
				}
				s=s+"</dataset>";
				//s=s+"</dataset>";		
				
				
				s=s+"<dataset seriesName='"+getLegend(period,3)+"' parentYAxis='S' color='"+fChartColor_CO2[1]+"' renderAs='Line'>";	
				for ( var i in data){						
					
					var tool=dailyTip(data[i]);
					if(period=="week"){
					    tool=weeklyTip(data[i]);
					}else if(period=="month"){
					    tool=monthlyTip(data[i]);
					}else if(period=="quater"){
					    tool=quaterlyTip(data[i]);
					}
					s=s+"<set value='"+data[i].OLDCO2SUM+"' tooltext='"+tool+"' showValue='0'/>";				
				}
				s=s+"</dataset>";
				
				s=s+"<dataset seriesName='"+getLegend(period,2)+"' parentYAxis='S' color='"+fChartColor_CO2[0]+"' renderAs='Line'>";	
				for ( var i in data){						
					
					var tool=dailyTip(data[i]);
					if(period=="week"){
					    tool=weeklyTip(data[i]);
					}else if(period=="month"){
					    tool=monthlyTip(data[i]);
					}else if(period=="quater"){
					    tool=quaterlyTip(data[i]);
					}
					s=s+"<set value='"+data[i].CO2SUM+"' tooltext='"+tool+"' showValue='0'/>";				
				}
				s=s+"</dataset>";	
				
				
		    	}else{
		     		s=s+"<category label='00' showLabel='1'/>";
		     		s=s+"</categories>";
		     		s=s+"<dataset seriesName='' color='"+getEnergyTypeColor(0)+"'>";
		     		s=s+"<set value='00' tooltext='00'/>";	
		     		s=s+"</dataset>";
		    	
		    	}
	       	s=s+"</chart>";
	
	      	return s;
	  	}  

        /**
        * 가젯이 처음 로딩될때 건물에해당하는 location테이블에서의 키값을 조회함.
        */
        function getRootLocationId(){

            //alert( supplierId );
            
            $.getJSON('${ctx}/gadget/bems/getRootLocationId.do',{ supplierId : supplierId } ,
                    function(json) {
                    //alert("1");
                        if(json != ""){
                            
                            locationId = json.rootLocation[0].ID;

                            getTotals();
                            
                            //alert( "세션에서 가져온 locationId : " + locationId );
                        }
                    }
            );
        }
        
        /**
         * 주기별 total 사용량 , total 탄소배출량. 
         */
         function getTotals(){

             if( searchDateType == "" ) {
                 searchDateType = "1";
             }

             //alert( supplierId + " , " + searchDateType);
              
             $.getJSON('${ctx}/gadget/bems/getTotalUseOfSearchType.do' , 
                     { supplierId : supplierId , 
                       locationId : locationId , 
                       searchDateType : searchDateType,
                       energyType:energyType,
                       meterTypeCode:meterTypeCode } , setTotals );
         }
         function setTotals( json ){

             if(json != ""){
                 var totalUse = json.totalUse;
                 var totalCo2Use = json.totalCo2Use;
                 var averageUsage = json.averageUsage;
                 var averageCo2Usage = json.averageCo2Usage;

                 $("#totalUseId").text( totalUse );
                 $("#totalCo2UseId").text( totalCo2Use );
                 $("#averageUsage").text( averageUsage );
                 $("#averageCo2Usage").text( averageCo2Usage );

                 if('EM' == energyType){
                 	$('#unit0').html("&nbsp;kWh&nbsp;(<fmt:message key='aimir.average'/>&nbsp;");
                     $('#unit1').html("&nbsp;kWh)");     
                  }else if('GM' == energyType){                 
                      $('#unit0').html("&nbsp;㎥&nbsp;(<fmt:message key='aimir.average'/>&nbsp;");
                      $('#unit1').html("&nbsp;㎥)"); 
                  }else if('WM' == energyType){
                 	 $('#unit0').html("&nbsp;㎥&nbsp;(<fmt:message key='aimir.average'/>&nbsp;");
                      $('#unit1').html("&nbsp;㎥)"); 
                  }
                  
             }
         }
        
      // 셀렉트 박스의 값이 변경될때 호출.
     	function send(locId){
     		detailLocationId=locId;
     		getBuildingLookUpMax(true);
      	};

        // 메세지 처리 
        function getFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0]  = "<fmt:message key="aimir.env.error"/>";    // 사용할수 없는 환경입니다
            fmtMessage[1]  = "<fmt:message key="aimir.day"/>" + "(" + "<fmt:message key="aimir.hour"/>" + ") " + "<fmt:message key="aimir.usage"/>"; //"일(시간) 사용량";
            fmtMessage[2]  = "<fmt:message key="aimir.avgFee"/>";        //"평균요금";
            fmtMessage[3]  = "<fmt:message key="aimir.lowUsage"/>";     //"최소 사용량";
            fmtMessage[4]  = "<fmt:message key="aimir.highUsage"/>";     //"최대 사용량";
            
            fmtMessage[5]  = "<fmt:message key="aimir.weeklyusage"/>";       //"주 사용량";
            fmtMessage[6]  = "<fmt:message key="aimir.avgFee"/>";        //"평균요금";
            fmtMessage[7]  = "<fmt:message key="aimir.lowUsage"/>";     //"최소 사용량";
            fmtMessage[8]  = "<fmt:message key="aimir.highUsage"/>";     //"최대 사용량";
            
            fmtMessage[9]  = "<fmt:message key="aimir.monthly.usage"/>";        //"월사용량";
            fmtMessage[10] = "<fmt:message key="aimir.avgFee"/>";        //"평균요금";
            fmtMessage[11] = "<fmt:message key="aimir.lowUsage"/>";     //"최소 사용량";
            fmtMessage[12] = "<fmt:message key="aimir.highUsage"/>";     //"최대 사용량";
            
            fmtMessage[13] = "<fmt:message key="aimir.quarterly"/>" + " " + "<fmt:message key="aimir.usage"/>";      //"분기사용량";
            fmtMessage[14] = "<fmt:message key="aimir.avgFee"/>";        //"평균요금";
            fmtMessage[15] = "<fmt:message key="aimir.lowUsage"/>";     //"최소 사용량";
            fmtMessage[16] = "<fmt:message key="aimir.highUsage"/>";     //"최대 사용량";

            fmtMessage[17] = "<fmt:message key="aimir.price.unit"/>"; //"원";            
            fmtMessage[18] = "<fmt:message key="aimir.hour"/>"; //"시간";		
            fmtMessage[19] = "<fmt:message key="aimir.dayofweek"/>"; //"요일";	
            fmtMessage[20] = "<fmt:message key="aimir.day.mon"/>"; //"월";		
            fmtMessage[21] = "<fmt:message key="aimir.quarter"/>"; //"분기";	

            fmtMessage[22] = "<fmt:message key="aimir.co2formula2"/>"; //"탄소배출량";
            fmtMessage[23] = "<fmt:message key="aimir.locationUsage.usage"/>"; //"사용량";
            fmtMessage[24] = "<fmt:message key="aimir.date.yesterday"/>";	// "전일";
            fmtMessage[25] = "<fmt:message key="aimir.date.today"/>";	// "금일";
            fmtMessage[26] = "<fmt:message key="aimir.lastyear"/>";	// "전년";
            fmtMessage[27] = "<fmt:message key="aimir.year1"/>"; //"당해";
            fmtMessage[28] = "<fmt:message key="aimir.date.lastweek"/>"; //"전주";
            fmtMessage[29] = "<fmt:message key="aimir.date.thisweek"/>";	//"금주";
            fmtMessage[30] = "<fmt:message key="aimir.thismonth"/>";	//"현월";
            fmtMessage[31] = "<fmt:message key="aimir.lastyear.samemonth"/>";	//"전년동월";

            fmtMessage[32] = "<fmt:message key="aimir.day.sun"/>"; //"일";
            fmtMessage[33] = "<fmt:message key="aimir.day.mon"/>"; //"월";
            fmtMessage[34] = "<fmt:message key="aimir.day.tue"/>"; //"화";
            fmtMessage[35] = "<fmt:message key="aimir.day.wed"/>"; //"수";
            fmtMessage[36] = "<fmt:message key="aimir.day.thu"/>"; //"목";
            fmtMessage[37] = "<fmt:message key="aimir.day.fri"/>"; //"금";
            fmtMessage[38] = "<fmt:message key="aimir.day.sat"/>"; //"토";

            fmtMessage[39] = "<fmt:message key="aimir.hour2"/>"; //"시";
            
            
            return fmtMessage;
        }
        
    /**
     * 조회 조건 전달
     * Flex에서 호출하는 함수.
     * Flex 에서 조회조건에 필요한 parameter값을 전달하는 함수.
     */
    function putParams(){
        
        var cnt = 0;
        var condArray = new Array();
        condArray[cnt++] = supplierId;
        condArray[cnt++] = locationId;
        condArray[cnt++] = searchDateType;
        condArray[cnt++] = startDate;
        condArray[cnt++] = endDate;
    
        return condArray;
    }

    /**
     * Flex에서 호출하는 함수.
     * jsp영역에 location 정보 노출을 위해 필요한 parameter값을 전달받는 함수.
     */
    function setLocation(dataArray) {

        locationId = dataArray[0]; 

        getTotals();

        getLocationInfo( locationId );

    };
    function getLocationInfo( locationId ){         
        $.getJSON('${ctx}/gadget/bems/getLocationInfo.do' , { locationId : locationId } , function( json ){

               var lName = json.name;
               var lPrentId = json.parentId;
               
               $("#locationTotalTitle").text( lName );

               if( lPrentId == null || lPrentId == "" ) {
                   $("#locationTotalTitle").text( '<fmt:message key="aimir.all"/>' );
               }
            });
    }

    function setDate(date){
    	getCurrentTime();
    	//$('#basisDate').html(date);
    }


    function getCurrentTime(){

		$.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    currTime = json.currTime;
	                    $('#basisDate').html(currTime);
	                }
	            }
	    );
	} 
	function getPeriodArray(period){

		if(period =="daily")
			return dailyArr;
		else if(period =="weekly")
			return weeklyArr;
		else if(period =="monthly")
			return monthlyArr;
		else if(period =="quaterly")
			return quaterlyArr;
        return dailyArr;
	}

	//BarChart 파라미터 전달
    function getFCStype(){
 
       var style=fChartStyle_Common
       + fChartStyle_Font
       + fChartStyle_StColumn3D_nobg;
       return style;

    }
 	
	// Y축 값에 대한 NumberFormatting
	// ex) 1,000 -> 1K
	function setNumberFormatting() {
		var s = "formatNumberScale='1'"// 왼쪽 Y축
		+" sFormatNumberScale='1' ";// 오른쪽 Y축
		
		return s;
	}
	
    window.onresize = setGraph;
    function setGraph(barEnable,barData,dailyData,weeklyData,monthlyData,quaterlyData,dArr,wArr,mArr,qArr){   	    
                 var usageChartMaringTop = "chartTopMargin='10' ";
                 
		         if(barEnable){
			    	if(!$('#barChartDiv').is(':visible')) {
			             return;
			        }
			
			
			   	    if(barData){
			 	    	 
			   		 barData="<chart chartLeftMargin='10' "
			   	           + "chartRightMargin='10' "
			   	    	   + "chartTopMargin='10' "
			   	    	   + "chartBottomMargin='0' "
			   	    	   + "showLabels='0' "
						   + "labelDisplay = 'NONE' "
						   + "rotateYAxisName='0' "
			   	    	   + fChartStyle_Common
			   	    	   + fChartStyle_Font
			   	           + fChartStyle_StColumn3D_nobg
			           	   + barData;
			
			   		 barGlobalData= barData;
			 	    }   	    
			 	   
				       var barChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "barChartId",($('#barChartDiv').width()),  510, "0", "0");            
				       if(barData)
				       	barChart.setDataXML(barData);
				       else{
				       	barChart.setDataXML(barGlobalData);
				       }
				       barChart.setTransparent("transparent");
				       barChart.render("barChartDiv");
    			}
            if(dailyData){
            	dailyData= "<chart chartLeftMargin='0' "
                    + setNumberFormatting()
            		+ "chartRightMargin='0' "
        			+ usageChartMaringTop
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
		   	        + "showLabels='0' "
					+ "labelDisplay = 'NONE' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                	+ dailyData;
            	dailyGlobalData= dailyData;
    	    }
            var dailyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "dailyChartId",($('#dailyChartDiv').width()),  220, "0", "0");            
            if(dailyData)
            	dailyChart.setDataXML(dailyData);
            else{
            	dailyChart.setDataXML(dailyGlobalData);
            }
            dailyChart.setTransparent("transparent");           
            dailyChart.render("dailyChartDiv");
            
            if(weeklyData){
            	weeklyData= "<chart chartLeftMargin='0' "
                    + setNumberFormatting()
            		+ "chartRightMargin='0' "
        			+ usageChartMaringTop
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + weeklyData;
            	weeklyGlobalData= weeklyData;
    	    }
            var weeklyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "weeklyChartId",($('#weeklyChartDiv').width()-10),  220, "0", "0");            
            if(weeklyData)
            	weeklyChart.setDataXML(weeklyData);
            else{
            	weeklyChart.setDataXML(weeklyGlobalData);
            }
            weeklyChart.setTransparent("transparent");           
            weeklyChart.render("weeklyChartDiv");
            
            if(monthlyData){
            	monthlyData= "<chart chartLeftMargin='0' "
                    + setNumberFormatting()
            		+ "chartRightMargin='0' "
        			+ usageChartMaringTop
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ "sYAxisValueDecimals='3' "    
		   	    	+ "showLabels='0' "
					+ "labelDisplay = 'NONE' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + monthlyData;
            	monthlyGlobalData= monthlyData;
    	    }
            var monthlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "monthlyChartId",($('#monthlyChartDiv').width()),  220, "0", "0");            
            if(monthlyData)
            	monthlyChart.setDataXML(monthlyData);
            else{
            	monthlyChart.setDataXML(monthlyGlobalData);
            }
            monthlyChart.setTransparent("transparent");           
            monthlyChart.render("monthlyChartDiv");

             
            if(quaterlyData){
            	quaterlyData= "<chart chartLeftMargin='0' "
            		+ setNumberFormatting()
            		+ "chartRightMargin='0' "
        			+ usageChartMaringTop
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + quaterlyData;
            	quaterlyGlobalData= quaterlyData;
    	    }
            var quaterlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "quaterlyChartId",($('#quaterlyChartDiv').width()),  220, "0", "0");            
            if(quaterlyData)
            	quaterlyChart.setDataXML(quaterlyData);
            else{
            	quaterlyChart.setDataXML(quaterlyGlobalData);
            }
            quaterlyChart.setTransparent("transparent");           
            quaterlyChart.render("quaterlyChartDiv");

            
            $('#daily0').html(dArr[0]);
            $('#daily1').html(dArr[0]);
            $('#daily2').html("<em class='Tgy_bold'>"+dArr[4]+" </em> ( "+dArr[1]+" : "+dArr[5]+" )");
            $('#daily3').html("<em class='Tbu_normal'><fmt:message key='aimir.highUsage'/></em> : "+dArr[7]+"<br> <em class='Trd_normal'><fmt:message key='aimir.lowUsage'/></em> : "+dArr[6]);

            $('#weekly0').html(wArr[0]);
            $('#weekly1').html(wArr[0]);
            $('#weekly2').html("<em class='Tgy_bold'>"+wArr[4]+" </em> ( "+wArr[1]+" : "+wArr[5]+" )");
            $('#weekly3').html("<em class='Tbu_normal'><fmt:message key='aimir.highUsage'/></em> : "+wArr[7]+"<br> <em class='Trd_normal'><fmt:message key='aimir.lowUsage'/></em> : "+wArr[6]);

            $('#monthly0').html(mArr[0]);
            $('#monthly1').html(mArr[0]);
            $('#monthly2').html("<em class='Tgy_bold'>"+mArr[4]+" </em> ( "+mArr[1]+" : "+mArr[5]+" )");
            $('#monthly3').html("<em class='Tbu_normal'><fmt:message key='aimir.highUsage'/></em> : "+mArr[7]+"<br> <em class='Trd_normal'><fmt:message key='aimir.lowUsage'/></em> : "+mArr[6]);

            $('#quaterly0').html(qArr[0]);
            $('#quaterly1').html(qArr[0]);
            $('#quaterly2').html("<em class='Tgy_bold'>"+qArr[4]+" </em> ( "+qArr[1]+" : "+qArr[5]+" )");
            $('#quaterly3').html("<em class='Tbu_normal'><fmt:message key='aimir.highUsage'/></em> : "+qArr[7]+"<br> <em class='Trd_normal'><fmt:message key='aimir.lowUsage'/></em> : "+qArr[6]);
         
    }   
    </script>
</head>

<body class="bg">
<div id="wrapper">   
	<div id="bm_max">
		<div class="top">
	      <ul class="header2">
	          <li class="hLeft">
	               <div class="energy_rule">
				       <form name="form1" method="post" action="">
				       	<div class="h20">
							<span class="check"></span> 
							<span id="locationTotalTitle"><fmt:message key="aimir.all"/>&nbsp;<fmt:message key="aimir.usage"/>&nbsp;:&nbsp;</span>
							<b><span id="totalUseId">0</span></b>
							<span id="unit0">&nbsp;kWh&nbsp;(<fmt:message key="aimir.average"/>&nbsp;</span> 
							<span id="averageUsage">0</span>
							<span id="unit1">&nbsp;kWh)</span>
						</div>
						<div class="h20 width_100">
							<span class="check"></span> 
							<span><fmt:message key="aimir.co2formula2"/>&nbsp;:&nbsp;</span>
							<b><span id="totalCo2UseId">0</span></b>
							<span>&nbsp;kg&nbsp;(<fmt:message key="aimir.average"/>&nbsp;</span>
							<span id="averageCo2Usage">0</span>
							<span>&nbsp;kg)</span>
						</div>
						</form>
					  </div>
	            </li> 
	            <li class="hRight mt10"><span id='basisDate'></span></li> 
			</ul>
    	</div>
    	
   	 	<div class="wrap">
        	  <div class="lnb mt20">
           		<div><select id="locationList" style="width:240px;"></select></div>
            	<div id="barChartDiv"></div>   
		      </div>
		    </div>
		
		<div id="bm_max_content">
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><span id='daily0'></span></label>
       				<div id="dailyChartDiv" class="w_auto clear"></div>
       				<div class="w_auto mgn_side10">
       					<table class="data_table">
       						<tr>
       							<th id='daily1' rowspan="2"></th>
       							<td id='daily2'></td>
       						</tr>
       						<tr>
       							<td id='daily3' class="last"> </td>
       						</tr>
       					</table>
          			</div>
       			</div>
       		</div>
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><span id='weekly0'></span></label>
       				<div id="weeklyChartDiv" class="w_auto clear"></div>
       				<div class="w_auto mgn_side10">
       					<table class="data_table">
       						<tr>
       							<th rowspan="2"  id='weekly1'></th>
       							<td id='weekly2'></td>
       						</tr>
       						<tr>
       							<td class="last"  id='weekly3'></td>
       						</tr>
       					</table>
          			</div>
       			</div>
       		</div>
		</div> 
		
		<div id="bm_max_content">
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><span id='monthly0'></span></label>
       				<div id="monthlyChartDiv" class="w_auto clear"></div>
       				<div class="w_auto mgn_side10">
       					<table class="data_table">
       						<tr>
       							<th rowspan="2"  id='monthly1'></th>
       							<td  id='monthly2'></td>
       						</tr>
       						<tr>
       							<td class="last"  id='monthly3'></td>
       						</tr>
       					</table>
          			</div>
       			</div>
       		</div>
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><span id='quaterly0'></span></label>
       				<div id="quaterlyChartDiv" class="w_auto clear"></div>
       				<div class="w_auto mgn_side10">
       					<table class="data_table">
       						<tr>
       							<th rowspan="2"  id='quaterly1'></th>
       							<td id='quaterly2'></td>
       						</tr>
       						<tr>
       							<td  id='quaterly3' class="last"></td>
       						</tr>
       					</table>
          			</div>
       			</div>
       		</div>
		</div>   
      </div>
        
	</div>    

</body>
</html>