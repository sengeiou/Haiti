<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title><fmt:message key='aimir.buildingLocation'/> <fmt:message key='aimir.energymeter'/> <fmt:message key='aimir.usage'/></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>

<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

	//탭초기화
	var tabs = {hourly:0,monthlyPeriod:0,yearly:0};
	var tabNames = {};
	
	var flex;
	var searchDateType = "1";
	var supplierId;
	var locationId = 0;
	var startDate;
	var endDate;
	var currTime = "";
	var detailLocationId = 0;
	var barGlobalData;
    var tmGlobalData;
    var usageGlobalData;
    var energyType="GM";
    var meterTypeCode="1.3.1.3";
    
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
	                    getCycleTypeList();
	                    getBuildingLookUp(false);
	                    
	                    //alert( "세션에서 가져온 supplerId : " + supplierId );
	                }
	            }
	    );

	    $('#dateTypeList').change( function() {
	    
        	
        	getBuildingLookUp(false);

            getTotals();
		});

	    $('#locationList').change( function() {
	    	searchDateType = $('#dateTypeList').val();
        	var locId = $('#locationList').val();

            if(locId==locationId){
              return;
            }else{
            	locationId=locId;
            }
            
        	getBuildingLookUp(false);

            getTotals();
		});


	 // 주기적으로 refresh
	    setInterval("getBuildingLookUp()", 1000*60*15);
		

	});
    
    
    function getBuildingLookUp(detail){
       
    	searchDateType = $('#dateTypeList').val();
    
        $.getJSON('${ctx}/gadget/bems/getBuildingLookUp.do' , {
        	searchDateType : searchDateType,
        	supplierId : supplierId,
        	locationId : locationId,
        	detailLocationId : detailLocationId,
        	energyType:energyType
        	} ,
        	function( json ){
            	  var locList = json.returnLocation;

            	  for ( var i in locList) {
            		  locList[i].name = locList[i].label;
            		  locList[i].id = locList[i].data;            		  
                  }
            	
        		 var barData= setBarData(json.grid);
                 var tmData= setTmData(json.sumTHGrid,json.TM_MAX,json.TM_MIN);
        		 var usageData= setUsageData(json.sumGrid);
        		
        		if(!detail){	
        			setGraph(barData,tmData,usageData);  
        			$('#locationList').pureSelect(locList);      		
	        		setLocationIdx();
	        		$('#locationList').selectbox();
        		}else{
        			setDetailGraph(usageData);
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

    function getEnergyTypeColor(){
       if('EM' == energyType){
          return fChartColor_Elec[0];
       }else if('GM' == energyType){
    	   return fChartColor_Gas[0];
       }else if('WM' == energyType){
    	   return fChartColor_Water[0];
       }
    }
   

    function getEnergyData(data){
    	
        if('EM' == energyType){
           return data.EMSUM;   
        }else if('GM' == energyType){
        	return data.GMSUM; 
        }else if('WM' == energyType){
        	return data.WMSUM; 
        }
     }

     

     function fix(data,num){
        if(data >0){
         return data.toFixed(num);
        }else{
         return data;
        }
     }

    function tmHumTip(obj){
	        
	        var s;
             s =  obj.MYDATE + "{br}";
	   
	        if(obj.TM)
	        s += "<fmt:message key='aimir.temperature'/> : " + fix(obj.TM,1) + " ℃{br}";
	        if(obj.HUM)
	        s += "<fmt:message key='aimir.locationUsage.humidity'/> : " + fix(obj.HUM,1) + " %{br}";
	        if(obj.TMMAXVALUE)
	        s += "<fmt:message key='aimir.temperature'/> MAX: " + fix(obj.TMMAXVALUE,1) + " ℃{br}";
	        
	        if(obj.TMMINVALUE)
	        s += "<fmt:message key='aimir.temperature'/> MIN: " + fix(obj.TMMINVALUE,1) + " ℃{br}";
	        
	        if(obj.HUMMAXVALUE)
	        s += "<fmt:message key='aimir.locationUsage.humidity'/> MAX: " + fix(obj.HUMMAXVALUE,1) + " %{br}";
	        
	        if(obj.HUMMINVALUE)
	        s += "<fmt:message key='aimir.locationUsage.humidity'/> MIN: " + fix(obj.HUMMINVALUE,1) + " %{br}";
	        // The value of the Income will always be 100%, 
	        // so exclude adding that to the DataTip. Only 
	        // add percent when the user gets the Profit DataTip.
	        return s;
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
			
			s=s+"<dataset seriesName='' color='"+getEnergyTypeColor()+"'>";			
			
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
    
    function setUsageData(data){
		var s;		
		var scale=2;
		s=" PYAxisName='<fmt:message key='aimir.usage'/> ["+getEnergyTypeUnit()+"]'"  
	    	   +" SYAxisName='<fmt:message key='aimir.co2formula2'/> [kg]'>";
	    	s=s+"<categories>";   
	    	if(data.length>0){
			for ( var i in data){
				if((i%scale)==0){
					s=s+"<category label='"+data[i].MYDATE+"' showLabel='1'/>";		
				}else{
					    s=s+"<category label='"+data[i].MYDATE+"'/>";
				}
		    }
		
			s=s+"</categories>";
			
			s=s+"<dataset seriesName='' color='"+getEnergyTypeColor()+"'>";			
			
			for ( var i in data){					
				s=s+"<set value='"+getEnergyData(data[i])+"'  tooltext='"+usageTip(data[i])+"' showValue='0'/>";
			}
			s=s+"</dataset>";	
			s=s+"<dataset seriesName='' parentYAxis='S' color='"+fChartColor_CO2[0]+"' renderAs='Line'>";	
			for ( var i in data){						
			    s=s+"<set value='"+data[i].CO2SUM+"' tooltext='"+usageTip(data[i]) +"' showValue='0'/>";				
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

  	function setTmData(data,max,min){
		var s;		
		var scale=3;
	
	    	s=" PYAxisName='<fmt:message key='aimir.temperature'/>[℃]'"  
	    		   +" SYAxisName='<fmt:message key='aimir.locationUsage.humidity'/>[%]' PYAxisMinValue='"+min+"'"  
	    	   +" PYAxisMaxValue='"+max+"'>";
	    	s=s+"<categories>";   

	    	
	    	
	    if(data.length>0){
       	    var period = data[0];
       	  	var tempTM = period.TM;
     	    var isTM  = (tempTM!=null?1:0);
     	    
       	    if(!period.TM){
       	    	scale=2;
       	    }
	       	 for ( var i in data){				
					
					if((i%scale)==0){
						s=s+"<category label='"+data[i].MYDATE+"' showLabel='1'/>";
					}else{
						s=s+"<category label='"+data[i].MYDATE+"'/>";
					}		
				}			
			s=s+"</categories>";
			
			if(isTM){
				 
				s=s+"<dataset seriesName='' color='"+fChartColor_Heat[0]+"' >";
			  
				 for ( var i in data){						
				 
				    s=s+"<set value='"+data[i].TM+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";				
											
				}
				s=s+"</dataset>";
				s=s+"<dataset seriesName='' parentYAxis='S' color='"+fChartColor_Humid[0]+"' renderAs='Line'>";
			
				for ( var i in data){	
					s=s+"<set value='"+data[i].HUM+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";			
									
				}
				s=s+"</dataset>";
			}else{
				s=s+"<dataset seriesName='' color='"+fChartColor_Heat[0]+"'>";
			  
				for ( var i in data){	
				     s=s+"<set value='"+data[i].TMMAXVALUE+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";				
														
				}
				s=s+"</dataset>";
				
				s=s+"<dataset seriesName='' color='"+fChartColor_Heat[1]+"'>";
			  
				for ( var i in data){	
				     s=s+"<set value='"+data[i].TMMINVALUE+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";				
														
				}
				s=s+"</dataset>";
				
				s=s+"<dataset seriesName='' parentYAxis='S' color='"+fChartColor_Humid[0]+"' renderAs='Line'>";
			
				for ( var i in data){	
					s=s+"<set value='"+data[i].HUMMAXVALUE+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";			
							
				}
				s=s+"</dataset>";
				
				s=s+"<dataset seriesName='' parentYAxis='S' color='"+fChartColor_Humid[1]+"' renderAs='Line'>";
			
				for ( var i in data){	
					s=s+"<set value='"+data[i].HUMMINVALUE+"'  showValue='0' tooltext='"+tmHumTip(data[i])+"'/>";			
									
				}
				s=s+"</dataset>";
				
				
			    
			
			}
	   		}else{
	     		s=s+"<category label='00' showLabel='1'/>";
	     		s=s+"</categories>";
	     		s=s+"<dataset seriesName='' color='"+fChartColor_Heat[0]+"'>";
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

		//alert( "supplierId = " + supplierId + " , locationId = " + locationId );
		//$("#con").text( supplierId );
		
	    $.getJSON('${ctx}/gadget/bems/getRootLocationId.do',{ supplierId : supplierId } ,
	            function(json) {
            
	                if(json != ""){
		                
	                	locationId = json.rootLocation[0].ID;

	                    getTotals();
	                	
	                    //alert( "세션에서 가져온 supplerId : " + supplierId );
	                }
	            }
	    );
	}


	/**
	* 가젯이 처음 로딩될때 건물에해당하는 location테이블에서의 키값을 조회함.
	*/
	function getCurrentTime(){

		$.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    currTime = json.currTime;
	                    $('#basisDate').text(currTime);
	                }
	            }
	    );
	}
    
    /**
     * 주기 정보
     */
    function getCycleTypeList() {
        
        var cycleType = [{'name':'<fmt:message key="aimir.day"/>' , 'id':'1'},{'name':'<fmt:message key="aimir.week"/>' , 'id':'3'},{'name':'<fmt:message key="aimir.day.mon"/>' , 'id':'4'},{'name':'<fmt:message key="aimir.quarter"/>' , 'id':'9'}];
    
        $('#dateTypeList').pureSelect(cycleType);
       // $("#dateTypeList option:eq(0)").replaceWith("<option value=0><fmt:message key='aimir.msg.choice'/></option>");
        if(supplierId == ""){
            $("#dateTypeList").val(0);
        }else{
            $("#dateTypeList").val(searchDateType);
        }
        $("#dateTypeList").selectbox();
    }

    /**
    * 주기별 total 사용량 , total 탄소배출량. 
    */
    function getTotals(){

        searchDateType = $('#dateTypeList').val();

        if( searchDateType == "" ) {
            searchDateType = "1";
        }

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
		getBuildingLookUp(true);
 	};
	
	/**
	 * fmt message 전달
	 */
	function getFmtMessage(){
	    var fmtMessage = new Array();
	
	    fmtMessage[0] = "<fmt:message key="aimir.env.error"/>";    // 사용할수 없는 환경입니다
	    
	    fmtMessage[1] = "";
	    fmtMessage[2] = "";
	    fmtMessage[3] = "";
	    
	    fmtMessage[4] = "<fmt:message key="aimir.temperature"/>";    // 온도
	    fmtMessage[5] = "<fmt:message key="aimir.locationUsage.humidity"/>";    // 습도
	    fmtMessage[6] = "<fmt:message key="aimir.usage"/>";    // 사용량
	    fmtMessage[7] = "<fmt:message key="aimir.co2formula2"/>";    // 탄소배출량
	    
	
	    return fmtMessage;
	}

    /**
     * Flex에서 호출하는 함수.
     * Flex 에서 조회조건에 필요한 parameter값을 전달하는 함수.
     */
	function putParams(){
	    
	   // $('#basisDate').text(currTime);
	   getCurrentTime();
         
	    searchDateType = $('#dateTypeList').val();
	    
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


    //BarChart 파라미터 전달
    function getFCStype(){
 
       var style=" chartLeftMargin='10' "
           + "chartRightMargin='10' "
    	   + "chartTopMargin='10' "
    	   + "chartBottomMargin='0' "
    	   + "showLabels='0' "
    	   + fChartStyle_Common
    	   + fChartStyle_Font
           + fChartStyle_StColumn3D_nobg;
       return style;

    }
    
    window.onresize = setGraph;
    function setGraph(barData,tmData,usageData){

    	setBarGraph(barData);
    	setTmGraph(tmData)
        setUsageGraph(usageData)
    }

    function setDetailGraph(usageData){
        setUsageGraph(usageData)
    }
    

    function setBarGraph(barData){
       if(!$('#barChartDiv').is(':visible')) {
            return;
       }


  	 if(barData){
	    	 
  		 barData="<chart chartLeftMargin='10' "
  	           + "chartRightMargin='10' "
  	    	   + "chartTopMargin='10' "
  	    	   + "chartBottomMargin='0' "
  	    	   + "showLabels='0' "
  	    	   + "rotateYAxisName='0' "
  	    	   + fChartStyle_Common
  	    	   + fChartStyle_Font
  	           + fChartStyle_StColumn3D_nobg
          	   + barData;

  		 barGlobalData= barData;
	    }   	    
	   
      var barChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "barChartId",($('#barChartDiv').width()),  250, "0", "0");            
      if(barData)
      	barChart.setDataXML(barData);
      else{
      	barChart.setDataXML(barGlobalData);
      }
      barChart.setTransparent("transparent");
      barChart.render("barChartDiv");
    }

    function setTmGraph(tmData){

    	 if(!$('#tmChartDiv').is(':visible')) {
             return;
        }
        
    	if(tmData){
	    	 
	    	 tmData="<chart chartLeftMargin='5' "
	             	+ "chartRightMargin='0' "
	        		+ "chartTopMargin='10' "
	        		+ "chartBottomMargin='0' "
	        		+ "canvasBorderColor='999999' "
	    			+ "canvasBorderThickness='1' "
	    			+ "decimals='3' "
	    			+ "showLabels='0' "
					+ "labelDisplay = 'NONE' "
	    			+ fChartStyle_Common
	                + fChartStyle_Font
	                + fChartStyle_MSCombiDY2D_nobg
           + tmData;

   	    tmGlobalData= tmData;
	    }   	    
	   
       var tmChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "tmChartId",($('#tmChartDiv').width()),  140, "0", "0");            
       if(tmData)
       	tmChart.setDataXML(tmData);
       else{
       	tmChart.setDataXML(tmGlobalData);
       }
       tmChart.setTransparent("transparent");

       //alert(tmData);
       tmChart.render("tmChartDiv");  
   

    }

    // Y축 값에 대한 NumberFormatting
	// ex) 1,000 -> 1K
	function setNumberFormatting() {
		var s = "formatNumberScale='1'"// 왼쪽 Y축
		+" sFormatNumberScale='1' ";// 오른쪽 Y축
		
		return s;
	}

    function setUsageGraph(usageData){


    	 if(!$('#usageChartDiv').is(':visible')) {
             return;
        }
    	if(usageData){
            usageData="<chart chartLeftMargin='5' "
            + setNumberFormatting()
            + "chartRightMargin='0' " 
			+ "chartTopMargin='10' "
			+ "chartBottomMargin='0' "
			+ "decimals='3' "
			+ "showLabels='0' "
			+ "labelDisplay = 'NONE' "
			+ fChartStyle_Common
            + fChartStyle_Font
			+ fChartStyle_StColumn3D_nobg
			+ usageData;
    	    	usageGlobalData= usageData;
    	    }
            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3DLineDY.swf", "usageChartId",($('#usageChartDiv').width()),  140, "0", "0");            
        
            if(usageData){
            	usageChart.setDataXML(usageData);
            }else{
            	usageChart.setDataXML(usageGlobalData);
            }
           
            usageChart.setTransparent("transparent");           
            usageChart.render("usageChartDiv");
    }
    
/*]]>*/

</script>
</head>

<body>
<div id="wrapper">
 
  <div id="container2">
    
     	<!-- 주기 및 날짜 (S) -->
     	<div class="tapBg seachSpace">
         <ul class="header">
            <li class="hLeft tit_default"><fmt:message key="aimir.period"/></li>
            <li class="hLeft">
            <select id="dateTypeList" style="width:60px;""></select>
             </li>
                          
            <li class="hRight dateMargin"><div id='basisDate'></div></li>
		</ul>
        <!-- 주기 및 날짜  (E) -->
       </div>
		
	      <div class="energy_rule">
	       <form name="form1" method="post" action="">
	       <div class="h24">
				<span class="check"></span> 
				<span id="locationTotalTitle"><fmt:message key="aimir.all"/>&nbsp;<fmt:message key="aimir.usage"/>&nbsp;:&nbsp;</span>
				<b><span id="totalUseId">0</span></b>
				<span id="unit0">&nbsp;kWh&nbsp;(<fmt:message key="aimir.average"/>&nbsp;</span> 
				<span id="averageUsage">0</span>
				<span id="unit1">&nbsp;kWh)</span>
			</div>
			<div class="h24 width_100">
				<span class="check"></span> 
				<span><fmt:message key="aimir.co2formula2"/>&nbsp;:&nbsp;</span>
				<b><span id="totalCo2UseId">0</span></b>
				<span>&nbsp;kg&nbsp;(<fmt:message key="aimir.average"/>&nbsp;</span>
				<span id="averageCo2Usage">0</span>
				<span>&nbsp;kg)</span>
			</div>
			</form>
	      </div>
		
        
    
    <!-- 빌딩전력사용량 기본정보 (E) -->
    
    <!-- 빌딩전력사용량 탐색기 및 그래프 (S) -->
    <div class="Bchart allp5"> 
	        <div  class="w_auto">
        	<div class="buld_monitor_left">
        	 
              <div><select id="locationList" style="width:180px;"></select></div>
            	<div id="barChartDiv"></div>   
            </div>
	        <div class="buld_monitor_right">
               	<div id="tmChartDiv" class="width_100"></div>
	            <div id="usageChartDiv" class="width_100"></div>
	        </div>
	    </div>
    </div> 
    <!-- 빌딩전력사용량 탐색기 및 그래프 (E) -->    
  </div>   
</div>
</body>
</html>