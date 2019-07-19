<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">

    /*<![CDATA[*/
    

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:1};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    //공급사ID
    var supplierId="";
    var usageGlobalData;
    var usageGlobalData2;

    // 에너지 타입 ( 전기Em : 0 , 가스Gm : 1 , 수도Wm : 2 )
    var myEnergyType = "2";
    var myEnergyTypeStr = "Wm";
    
    //로그인한 사용자정보를 조회한다.
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;

                $('#supplierId').val( supplierId );
                $('#supplierId2').val( supplierId );
                getEnergySavingGoal2Info();
                getEnergySavingGoal2Info2();
            }
    );


    if( "" == supplierId ){

        supplierId = "${supplierId}";

        $('#supplierId').val( supplierId );
        $('#supplierId2').val( supplierId );
    }
    
    function getEnergyTypeUnit(){
        if('Em' == myEnergyTypeStr){
           return "kWh";
        }else {
     	   return "㎥";
        }
     }
    
 	function getEnergySavingGoal2Info(){
        
    	var searchStartDate = $('#searchStartDate').val();
    	var searchEndDate = $('#searchEndDate').val();
    	var searchDateType = $('#searchDateType').val();

    	if(searchDateType=='1'){
    		searchDateType ='4';
        }else{
        	searchDateType ='8';
        }
    	
    	var supId = supplierId;
    	var savingGoal = $('#savingGoal').val();
    	var savingGoalStartDate = $('#savingGoalStartDate').val();
    	var energyType = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
    	var requestType='LeftTop';
    	var fmtMessage= getFmtMessage();
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();
        $.getJSON('${ctx}/gadget/bems/getEnergySavingGoal2Info.do' , { 
            searchStartDate:searchStartDate,
        	searchEndDate : searchEndDate,
        	searchDateType : searchDateType,
        	supplierId:supId,
        	energyType:energyType,
        	savingGoalStartDate:savingGoalStartDate,
        	savingGoal:savingGoal,
        	requestType:requestType,
        	msgAvgYear:encodeURIComponent(fmtMessage[10]),
        	msgGoal:encodeURIComponent(fmtMessage[11]),
        	msgPrediction:encodeURIComponent(fmtMessage[12])
        	} ,
        	function( json ){


        		var obj = json.info[2].chartInfo;
               
        		var s=" YAxisName='<fmt:message key='aimir.usage'/> ["+getEnergyTypeUnit()+"]'"   
 		 			+">";
  					s=s+"<categories>";  
  	              for ( var i in obj){
  				    	s=s+"<category label='"+obj[i].gubun+"' showLabel='1'/>";		
  				  }
  				  s=s+"</categories>";
  				  s=s+"<dataset seriesName=''>";			

  				  for ( var i in obj){
  					  s=s+"<set value='"+obj[i].usage+"' showValue='0' color='"+fChartColor_Step4[i]+"'/>";
  			       }
  				  s=s+"</dataset>";	
  				  s=s+"</chart>";

  				var dataArray = new Array();
  				dataArray[0] = json.info[0].savingGoal;
                dataArray[1] = json.info[0].savingGoalStartDate;
                dataArray[2] = json.info[0].searchDateType;
                dataArray[3] = json.info[1].gubun2;
                dataArray[4] = json.info[0].energyAvgYearCount;
                dataArray[5] = json.info[1].saving;
                setSavingGoalInfo( dataArray );
                setGraph(s);
            });
    }


	function getEnergySavingGoal2Info2(){

    	var searchStartDate = $('#savingGoalStartDateBottom').val();
    	var searchEndDate = $('#savingGoalEndDateBottom').val();
    	var searchDateType = $('#searchDateType').val();

    	
    	var supId = supplierId;
    	var savingGoal = $('#savingGoal').val();
    	var savingGoalStartDate = $('#savingGoalStartDate').val();
    	var energyType = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
    	var requestType='LeftBottom';
    	var fmtMessage= getFmtMessage();
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();
        $.getJSON('${ctx}/gadget/bems/getEnergySavingGoal2Info.do' , { 
            searchStartDate:searchStartDate,
        	searchEndDate : searchEndDate,
        	searchDateType : searchDateType,
        	supplierId:supId,
        	energyType:energyType,
        	savingGoalStartDate:savingGoalStartDate,
        	savingGoal:savingGoal,
        	requestType:requestType,
        	msgAvgYear:encodeURIComponent(fmtMessage[10]),
        	msgGoal:encodeURIComponent(fmtMessage[11]),
        	msgPrediction:encodeURIComponent(fmtMessage[12])
        	} ,
        	function( json ){


        		var obj = json.info[2].chartInfo;
               
        		var s=" YAxisName='<fmt:message key='aimir.usage'/> ["+getEnergyTypeUnit()+"]'"   
		 			+">";
					s=s+"<categories>"; 
  	              for ( var i in obj){
  				    	s=s+"<category label='"+obj[i].gubun+"' showLabel='1'/>";		
  				  }
  				  s=s+"</categories>";
  				  s=s+"<dataset seriesName=''>";			

  				  for ( var i in obj){
  					  s=s+"<set value='"+obj[i].usage+"' showValue='0' color='"+fChartColor_Step4[i]+"'/>";
  			       }
  				  s=s+"</dataset>";	
  				  s=s+"</chart>";

  				var dataArray = new Array();
  				dataArray[0] = json.info[0].savingGoal;
                dataArray[1] = json.info[0].savingGoalStartDate;
                dataArray[2] = json.info[0].searchDateType;
                dataArray[3] = json.info[1].gubun2;
                dataArray[4] = json.info[0].energyAvgYearCount;
                dataArray[5] = json.info[1].saving;
                setSavingGoalInfoBottom( dataArray );
                setGraph2(s);
            });
    }

    window.onresize = setGraph;setGraph2;
    function setGraph(usageData){
    	
            if(usageData){
            usageData="<chart chartLeftMargin='2' "
            + "chartRightMargin='20' " 
			+ "chartTopMargin='10' "
			+ "chartBottomMargin='0' "
			+ "decimals='3' "
			+ fChartStyle_Common
            + fChartStyle_Font
			+ fChartStyle_StColumn3D_nobg
			+ usageData;
    	    	usageGlobalData= usageData;
    	    }
            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "usageChartId",($('#barChartDiv').width()),  250, "0", "0");            
        
            if(usageData){
            	usageChart.setDataXML(usageData);
            }else{
            	usageChart.setDataXML(usageGlobalData);
            }
            usageChart.setTransparent("transparent");           
            usageChart.render("barChartDiv");
    }

    function setGraph2(usageData){
    	
        if(usageData){
        usageData="<chart chartLeftMargin='2' "
        + "chartRightMargin='20' " 
		+ "chartTopMargin='5' "
		+ "chartBottomMargin='0' "
		+ "decimals='3' "
		+ fChartStyle_Common
        + fChartStyle_Font
		+ fChartStyle_StColumn3D_nobg
		+ usageData;
	    	usageGlobalData2= usageData;
	    }
        var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "usageChartId2",($('#barChartDiv2').width()),  250, "0", "0");            
    
        if(usageData){
        	usageChart.setDataXML(usageData);
        }else{
        	usageChart.setDataXML(usageGlobalData2);
        }
        usageChart.setTransparent("transparent");           
        usageChart.render("barChartDiv2");
	}
	
    //플렉스객체
    var flexGoalLeftTop;
    var flexGoalLeftBottom;
    var flexGoalRight;
    var flexAvgLeft;
    var flexAvgRight;
    
    $(document).ready(function(){
        
        // 브라우저별로 플렉스객체를 초기화한다.
        flexGoalLeftTop = getFlexObject('leftGoalChartTop');
        flexGoalLeftBottom = getFlexObject('leftGoalChartBottom');
        flexGoalRight = getFlexObject('rightGoalGrid');

        flexAvgLeft = getFlexObject('dataGridAvgLeft');
        flexAvgRight= getFlexObject('dataGridAvgRight');


        $('#goalMng').show();
        $('#avgMng').hide();
//        $('#goalMng').hide();
//        $('#avgMng').show();
        
        $('#avgTabAvgBtnCreate').show();
        $('#avgTabAvgBtnUpdate').hide();
        $('#avgTabAvgBtnCancel').hide();


        $('#allView').attr("checked", "checked");
        
    });
    
    /**
     * 공통 send
     * 조회버튼클릭시 호출하게 된다.
     * 날짜를 선택한후 조회버튼을 클릭할때 처리하는 function
     */
    function send(){

        //alert( putParams().toString() );
        //http or flex Request Send
        
            $('#savingGoalStartDate').val( $('#searchStartDate').val() );
            $('#savingGoalDateType').val( $('#searchDateType').val() );
            getEnergySavingGoal2Info();
        	//flexGoalLeftTop.requestSendToFlex();
       // }
      //  if (flexGoalLeftBottom != null) {
      		getEnergySavingGoal2Info2();
        	//flexGoalLeftBottom.requestSendToFlex();
       // }
        if (flexGoalRight != null) {
        	flexGoalRight.requestSendToFlex();
        }
        
        if (flexAvgLeft != null) {

            flexAvgLeft.requestSendToFlex();
        }
        if (flexAvgRight!= null) {

            flexAvgRight.requestSendToFlex();
        }
    }

    /**
    * 설정버튼을 클릭하면 로딩된 에너지절감 목표값을 추가 or (생성일과 기준일이 같으면) 수정하게된다.
    */
    function savingGoalAdd(){

        $('#savingGoalDateType').val( $('#searchDateType').val() );
        
        if( valueCheck() ){

        	/*
            var savingGoalDateType = $('#savingGoalDateType').val();
            if( "1" == savingGoalDateType ){

                var a = $('#savingGoalStartDate').val();
                var yyyymm = a.substring( 0, 6); // yyyymm
                var d = new Date();
                var dd = d.getDate();
                if( dd < 10 ) dd = "0" + dd;
                
                $('#savingGoalStartDate').val( yyyymm + dd + "" ); 
                
            }else if( "3" == savingGoalDateType || "4" == savingGoalDateType || "8" == savingGoalDateType ){

                var a = $('#savingGoalStartDate').val();
                var yyyy = a.substring( 0, 4); // yyyy
                
                var d = new Date();
                var mm = (d.getMonth() + 1);
                if( mm < 10 ) mm = "0" + mm;
                var dd = d.getDate();
                if( dd < 10 ) dd = "0" + dd;
                
                $('#savingGoalStartDate').val( yyyy + mm + dd + "" ); 
            }
            */
            
            var params = {
                    success :

                        function(json) {

                            var result = json.result;
            
                            if( "Y" == result ){
                                
                                alert("<fmt:message key='aimir.save'/>"); // 저장되었습니다.

                                send();
                            }else if( "E" == result ){
                                
                            	alert("<fmt:message key='aimir.avgInfo.err'/>" + "\n" + "<fmt:message key='aimir.avgInfo.err'/>"); // 사용할수있는 평균정보가 없습니다.\n 평균관리에서 사용가능한 평균정보를 등록해주세요.
                            } else {
                                
                                alert("<fmt:message key='aimir.save.error'/>"); // 저장되지 않았습니다.
                            }

                        },
                    url : '${ctx}/gadget/bems/setEnergySavingGoal2' + myEnergyTypeStr + '.do',
                    datatype : 'json'
            };

            $('#myForm').ajaxSubmit(params);
            //alert( $('#savingGoalDateType').val() + "  , " + $('#savingGoalStartDate').val() );  
        }
    }
    
    /**
     * Flex 에서 메세지를 조회하기위한 함수
     */
    function getFmtMessage(){
        var fmtMessage = new Array();
    
        //fmtMessage[0] = "<fmt:message key="aimir.board.location"/>";    // 지역
        fmtMessage[0] = "";
        fmtMessage[1] = "";
        fmtMessage[2] = "";
        fmtMessage[3] = "";

        fmtMessage[4] = "<fmt:message key="aimir.date"/>";	// 일자
        fmtMessage[5] = "<fmt:message key="aimir.usage.avg.kwh"/>";	// 평균사용량 (kWh)
        fmtMessage[6] = "<fmt:message key="aimir.goal.rate"/>";	// 목표율
        fmtMessage[7] = "<fmt:message key="aimir.goal.amount"/>";	// 목표량
        fmtMessage[8] = "<fmt:message key="aimir.usage"/>";	// 사용량
        fmtMessage[9] = "<fmt:message key="aimir.energy.economical.rate"/>";	// 절감율
        fmtMessage[10] = "<fmt:message key="aimir.avg.year"/>";	// 년 평균
        fmtMessage[11] = "<fmt:message key="aimir.goal"/>";	// 목표
        fmtMessage[12] = "<fmt:message key="aimir.prediction"/>";	// 예상
        fmtMessage[13] = "<fmt:message key="aimir.week"/>";	// 주
        fmtMessage[14] = "<fmt:message key="aimir.month"/>"; // 월
        fmtMessage[15] = "<fmt:message key="aimir.year1"/>"; // 년

        fmtMessage[16] = "<fmt:message key="aimir.year1.usage"/>";	// 연별 사용량
        fmtMessage[17] = "<fmt:message key="aimir.facilityMgmt.energy"/>";	// 전기 사용량
        fmtMessage[18] = "<fmt:message key="aimir.facilityMgmt.gas"/>";		// 가스 사용량
        fmtMessage[19] = "<fmt:message key="aimir.facilityMgmt.water"/>";	// 수도 사용량
        fmtMessage[20] = "<fmt:message key="aimir.facilityMgmt.heat"/>";	// 열량 사용량
        fmtMessage[21] = "<fmt:message key="aimir.totalusage"/>";	// 총 사용량

        fmtMessage[22] = "<fmt:message key="aimir.average"/>";		// 평균
        fmtMessage[23] = "<fmt:message key="aimir.avg.energy"/>";	// 전력평균
        fmtMessage[24] = "<fmt:message key="aimir.avg.gas"/>";		// 가스평균
        fmtMessage[25] = "<fmt:message key="aimir.avg.water"/>";		// 수도평균
        fmtMessage[26] = "<fmt:message key="aimir.avg.heat"/>";		// 열량평균
        fmtMessage[27] = "<fmt:message key="aimir.avg.total.usage"/>";	// 총 평균사용량

        fmtMessage[28] = "<fmt:message key="aimir.createdate"/>";	// 생성날짜
        fmtMessage[29] = "<fmt:message key="aimir.isUsed"/>";	// 사용유무
        fmtMessage[30] = "<fmt:message key="aimir.description"/>";	// 설명

        fmtMessage[31] = "<fmt:message key="aimir.basis.day"/>";	// 기준일
        fmtMessage[32] = "<fmt:message key="aimir.bems.facilityMgmt.kind"/>";	// 종류
        fmtMessage[33] = "<fmt:message key="aimir.period2"/>";	// 주기
        fmtMessage[34] = "<fmt:message key="aimir.list"/>";		// 리스트  

        fmtMessage[35] = "<fmt:message key="aimir.isUsed.yes"/>";		// 사용
        fmtMessage[36] = "<fmt:message key="aimir.isUsed.no"/>";		// 비사용

        fmtMessage[37] = "<fmt:message key="aimir.daily"/>";	// 일별
        fmtMessage[38] = "<fmt:message key="aimir.weekly"/>";	// 주별
        fmtMessage[39] = "<fmt:message key="aimir.monthly"/>";	// 월별
        fmtMessage[40] = "<fmt:message key="aimir.yearly"/>";	// 연별

        fmtMessage[41] = "<fmt:message key="aimir.electricity"/>";	// 전기
        fmtMessage[42] = "<fmt:message key="aimir.gas"/>";	// 가스
        fmtMessage[43] = "<fmt:message key="aimir.water"/>";	// 수도
        fmtMessage[44] = "<fmt:message key="aimir.heatmeter"/>";	// 열량

        fmtMessage[45] = "kWh";	// 단위
        
        return fmtMessage;
    }

    function getSupplierId(){

   	 $.getJSON('${ctx}/common/getUserInfo.do',
   	            function(json) {
   	                supplierId = json.supplierId;

   	                $('#supplierId').val( supplierId );

   	             
   	            }
   	    );
   }
    /**
     * Flex에서 호출하는 함수.
     * Flex 에서 조회조건에 필요한 parameter값을 전달하는 함수.
     */
    function putParams(){
    	getSupplierId();
        var condArray = new Array();
        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = supplierId;
        condArray[4] = $('#savingGoal').val();
        condArray[5] = $('#savingGoalStartDate').val();
        condArray[6] = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
        condArray[7] = $('#allView').val();
        condArray[8] = $('#avgInfoId').val();
        
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();
        return condArray;
    }

    /**
     * Flex에서 호출하는 함수.
     * jsp 영역에  평균관리 정보를 update하기 위해 필요한 parameter값을 전달받는 함수.
     */
    function updateSetAvgInfo( dataArray ){


        $('#avgTabAvgBtnCreate').hide();
        $('#avgTabAvgBtnUpdate').show();
        $('#avgTabAvgBtnCancel').show();
        
        $('#avgInfoId').val( dataArray[0] ); // id
        $('#descr').val( dataArray[1] ); // descr

        if( dataArray[2] == "true" ){ // used
            $('#used').attr("checked", "checked");
        } else {
        	$('#used').attr("checked", "");
        }
        
        $('#years').val( dataArray[3] ); // years

        if (flexAvgRight != null) {
            
            flexAvgRight.requestSendToFlex();
        }
    }
    
    
    /**
     * Left Bottom chart
     * 평균 관리탭의 오른쪽 목록에서 특정 row를 클릭한경우 해당 일자의 평균 목표 조회 하기위한 parameter 값을 Flex로 넘겨주기위한 함수.
     * Flex에서 호출하는 함수.
     * Flex 에서 조회조건에 필요한 parameter값을 전달하는 함수.
     */
    function putParamsGoalInfo(){
        var condArray = new Array();
        condArray[0] = $('#savingGoalStartDateBottom').val();
        condArray[1] = $('#savingGoalEndDateBottom').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = supplierId;
        condArray[4] = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
        
        return condArray;
    }
    
    /**
     * Left Bottom chart
     * Flex에서 호출하는 함수.
     * jsp 영역에  평균관리 정보를 update하기 위해 필요한 parameter값을 전달받는 함수.
     */
    function clickDayGoalInfo( dataArray ){

        $('#savingGoalStartDateBottom').val( dataArray[0] ); // yyyyMMddStart
        $('#savingGoalEndDateBottom').val( dataArray[1] ); // yyyyMMddEnd

        $('#savingGoalStartDate').val(dataArray[0]);
        
        if (flexGoalLeftBottom != null) {
            flexGoalLeftBottom.requestSendToFlex();
        }
    }

    /**
     * Left Bottom chart
     * Flex에서 호출하는 함수.
     * jsp 영역에  절감목표치와 기준일 노출을 위해 필요한 parameter값을 전달받는 함수.
     */
     function setSavingGoalInfoBottom( dataArray ){


        var gubunType = dataArray[2];
        var rtnVal = "";
        if( "1" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.avg.day'/>"; //일 평균
        } else if( "3" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.avg.week'/>"; //"주 평균";
        } else if( "4" == gubunType ) {
            
                rtnVal = "<fmt:message key='aimir.avg.month'/>"; //"월 평균";
        } else if( "8" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.avg.year'/>"; //"년 평균";
        }
    
        $('#savingGoalTarget').val( dataArray[0] );

        $('#leftGoalChartBottomSavingCommant').text( dataArray[4] + " <fmt:message key='aimir.years'/>  " + dataArray[3] + rtnVal + " <fmt:message key='aimir.contrast'/>" ); //"대비"
        
        
        var a = Number( dataArray[5] ).toFixed(2);
        if( a > 0 ){
            
            $('#leftGoalChartBottomSaving').removeClass();
            $('#leftGoalChartBottomSaving').addClass("Trd_bold right");
            $('#leftGoalChartBottomSaving').text(  a  + "% <fmt:message key='aimir.energy.excess'/>"); //"초과"
        } else if( a == 0 ){

            $('#leftGoalChartBottomSaving').removeClass();
            $('#leftGoalChartBottomSaving').addClass("Tbk_bold right");
            $('#leftGoalChartBottomSaving').text(  a  + "%");
        } else {

            $('#leftGoalChartBottomSaving').removeClass();
            $('#leftGoalChartBottomSaving').addClass("Tbu_bold right");

            a = a * -1;
            $('#leftGoalChartBottomSaving').text(  a  + "% <fmt:message key='aimir.energy.economical'/>"); //"절감"
        }

        //alert( dataArray[3] + " , " + a  + " , " + Math.abs( a ) );
        //$('#saving').text( Math.abs( a ) + "%");

        //alert( dataArray[0] + " , " + dataArray[1] + " , " + dataArray[2] + " , " + dataArray[3] + " , " + dataArray[4] + " , " + dataArray[5] );
        
        //$('#log').text( myLog );
     }
    
    /**
     * Flex에서 호출하는 함수.
     * jsp 영역에  절감목표치와 기준일 노출을 위해 필요한 parameter값을 전달받는 함수.
     */
     function setSavingGoalInfo( dataArray ){

         var gubunType = dataArray[2];
         var rtnVal = "";
         if( "1" == gubunType ) {
                 
                 rtnVal = "<fmt:message key='aimir.avg.day'/>"; //일 평균
         } else if( "3" == gubunType ) {
                 
                 rtnVal = "<fmt:message key='aimir.avg.week'/>"; //"주 평균";
         } else if( "4" == gubunType ) {
             
                 rtnVal = "<fmt:message key='aimir.avg.month'/>"; //"월 평균";
         } else if( "8" == gubunType ) {
                 
                 rtnVal = "<fmt:message key='aimir.avg.year'/>"; //"년 평균";
         }
     
         $('#savingGoal').val( dataArray[0] );
         $('#savingGoalStartDate').val( dataArray[1] );

         $('#avgComment').text( dataArray[4] + " <fmt:message key='aimir.years'/>  " + dataArray[3] + rtnVal + " <fmt:message key='aimir.contrast'/>" ); //"대비"


         if( "" == $('#savingGoalStartDate').val() ){
             var initDayT = $('#searchStartDate').val();
             //var initDay = initDayT.substring(0,4) + "-" + initDayT.substring(4,6) + "-" + initDayT.substring(6,8);
             $('#savingGoalStartDate').val( initDayT );
         }
         
         
         var a = Number( dataArray[5] ).toFixed(2);
         if( a > 0 ){
             
             $('#savingComment').removeClass();
             $('#savingComment').addClass("Trd_bold right");
             $('#savingComment').text(  a  + "% <fmt:message key='aimir.energy.excess'/>"); //"초과"
         } else if( a == 0 ){

             $('#savingComment').removeClass();
             $('#savingComment').addClass("Tbk_bold right");
             $('#savingComment').text(  a  + "%");
         } else {

             $('#savingComment').removeClass();
             $('#savingComment').addClass("Tbu_bold right");

             a = a * -1;
             $('#savingComment').text(  a  + "% <fmt:message key='aimir.energy.economical'/>"); //"절감"
         }

         //alert( dataArray[3] + " , " + a  + " , " + Math.abs( a ) );
         //$('#saving').text( Math.abs( a ) + "%");

         
         //$('#log').text( myLog );
     }
    
    function valueCheck(){

        var check = true;

        if( "" == $('#savingGoalTarget').val() || "0" == $('#savingGoalTarget').val() ){
            alert("<fmt:message key='aimir.savingGoal.empty'/>"); // "절감 목표 값을 입력해주세요!"
            check = false;
            return check;
        }

        if( "" == $('#savingGoalStartDate').val() ){
            alert("<fmt:message key='aimir.savingGoal.startDate.empty'/>"); // "기준일을 입력해주세요! YYYY-MM-DD"
            check = false;
            return check;
        }
        
        if( "" == $('#savingGoalDateType').val() ){
            alert("<fmt:message key='aimir.savingGoal.period.empty'/>"); // 주기 정보를 입력해주세요.
            check = false;
            return check;
        }
        
        return check;
    }

    /**
     * Flex에서 호출하는 함수.
     * jsp 영역에  절감목표치와 기준일 노출을 위해 필요한 parameter값을 전달받는 함수.
     */
    function setYearsSumChecked( dataArray ){

        var yearsString = dataArray[0];

        $('#years').val( yearsString );
         
    }
    function valueCheck2(){

        var check = true;

        if($('#used').is(':checked')){

        	$('#used').val("true");
        }else {

        	$('#used').val("false");
        }
        
        if( "" == $('#descr').val() ){
            alert("<fmt:message key='aimir.savingGoal.input.empty1'/>"); // 평균관리 항목의 설명을 입력하세요.
            check = false;
            return check;
        }

        if( "" == $('#years').val() ){

           
            check = false;
            return check;
        }
        
        return check;
    }

    function avgCreate(){


        if( valueCheck2() ){


            var params = {
                    success :

                        function(json) {

                            var result = json.result;
            
                            if( "Y" == result ){
                                alert("<fmt:message key='aimir.save'/>"); // 저장되었습니다.

                                if (flexAvgLeft != null) {

                                    flexAvgLeft.requestSendToFlex();
                                }
                                
                            } else {
                                alert("<fmt:message key='aimir.save.error'/>"); // 저장되지 않았습니다.
                            }

                            $('#avgInfoId').val( "" ); // id
                            $('#descr').val( "" ); // descr
                            $('#used').attr("checked", "");
                            $('#years').val( "" ); // years
                            
                            $('#avgTabAvgBtnCreate').show();
                            $('#avgTabAvgBtnUpdate').hide();
                            $('#avgTabAvgBtnCancel').hide();

                        },
                    url : '${ctx}/gadget/bems/setEnergyAvg2.do',
                    datatype : 'json'
            };

            $('#myForm2').ajaxSubmit(params);
                
        }
    }

    function avgUpdate(){


        if( valueCheck2() ){


            var params = {
                    success :

                        function(json) {

                            var result = json.result;
            
                            if( "Y" == result ){
                                alert("<fmt:message key='aimir.alert.groupMgmt.msg1'/>"); // 수정되었습니다.

                                if (flexAvgLeft != null) {

                                    flexAvgLeft.requestSendToFlex();
                                }
                                
                            } else {
                                alert("<fmt:message key='aimir.alert.groupMgmt.msg1.err'/>"); // 수정되지 않았습니다.
                            }

                        },
                    url : '${ctx}/gadget/bems/setEnergyAvg2.do',
                    datatype : 'json'
            };

            $('#myForm2').ajaxSubmit(params);

            $('#avgInfoId').val( "" ); // id
            $('#descr').val( "" ); // descr
            $('#used').attr("checked", "");
            $('#years').val( "" ); // years
            
            $('#avgTabAvgBtnCreate').show();
            $('#avgTabAvgBtnUpdate').hide();
            $('#avgTabAvgBtnCancel').hide();
                
        }else{

            $('#avgTabAvgBtnCreate').hide();
            $('#avgTabAvgBtnUpdate').show();
            $('#avgTabAvgBtnCancel').show();
        }

 
    }

    function avgCancel(){

        $('#avgInfoId').val( "" ); // id
        $('#descr').val( "" ); // descr
        $('#used').attr("checked", "");
        $('#years').val( "" ); // years
        
        $('#avgTabAvgBtnCreate').show();
        $('#avgTabAvgBtnUpdate').hide();
        $('#avgTabAvgBtnCancel').hide();
    }

    /**
    * 탭 변경
    */
    function changeTab( tabName ){

    	if(tabName == "BASIC" ){

            $('#sub_tab1').addClass("current");
            $('#sub_tab2').removeClass("current");

            $('#goalMng').show();
            $('#avgMng').hide();
    		
    	}else if(tabName == "MGMT" ){

            $('#sub_tab1').removeClass("current");
            $('#sub_tab2').addClass("current");

            $('#goalMng').hide();
            $('#avgMng').show();
    	}else{

            $('#sub_tab1').addClass("current");
            $('#sub_tab2').removeClass("current");

            $('#goalMng').show();
            $('#avgMng').hide();
    	}
    	
    }

    /**
    * 전체보기 첵크 유무 판별
    */
    function allViewCehck(){


    	if($('#allView').is(':checked')){ 

    		$('#allView').val("");
        }else{
            
    		$('#allView').val("Y");
    	}

        flexAvgRight.requestSendToFlex();
        
    }
    //======================================================================================
    
    /*]]>*/
</script>
</head>
<body>

	<div id="wrapper">
	
	    <div id="energy">
	
		    <!-- tab (S) -->
		    <div class="bldg_sub_tab">
		      <ul>
		        <li><a href="javascript:changeTab('BASIC')" name="sub_tab1"  id="sub_tab1" class='current'><fmt:message key='aimir.savingGoal.goalMgmt'/></a></li>
		        <li><a href="javascript:changeTab('MGMT');" name="sub_tab2" id="sub_tab2"><fmt:message key='aimir.savingGoal.avgMgmt'/></a></li>
		      </ul>
		    </div>
		    <!-- tab (E) -->
		     
            <!-- 목표관리-->
	        <div id="goalMng" class="goal">
	        
	        	<div  class="h35 borderBottomDashed mt5">
					<%-- include file="../commonDateTabButtonType3.jsp" --%>
	                <%@include file="./selectBoxDate.jsp" %><%-- var tabs 에 날짜 선택모드로 셋팅. --%>
					<script type="text/javascript">sendRequest(DateType.DAILY); </script>	
				</div>	
				
				<!-- left -->
				<div class="left2 clear">
				  	<div class="ptrbl10">
				      	 <div class="ptrbl10 pt10">
				     	      <span id="avgComment" class="Tbk_bold left">0<fmt:message key='aimir.years'/> 0<fmt:message key='aimir.month'/> <fmt:message key='aimir.avgContrast'/>&nbsp;</span>
				              <span id="leftGoalChartTopSavingGoal" class="Tbk_bold left"><input type="text" id="savingGoal" name="savingGoal" class="value" size="4" maxlength="4" readonly="readonly" />%&nbsp;</span>
				              <span class="Tbk_bold left"><fmt:message key='aimir.energy.economical.target'/></span>
                              <!-- <span class="left">&nbsp;<em class="bems_button"><a href="#" onclick="javascript:savingGoalAdd();"><fmt:message key="aimir.set"/></a></em></span> -->
				              <span id="savingComment" class="right">0%<fmt:message key='aimir.energy.economical'/></span>
				          </div>
				          <div id="barChartDiv" class="pt12">
				            
				          </div>
				          
				      	 <div class="ptrbl10 mt10">
                            <form id="myForm" >
	                          <input type="hidden" id="supplierId" name="supplierId">
	                          <input type="hidden" id="savingGoalDateType" name="savingGoalDateType">
	                          <input type="hidden" id="savingGoalStartDate" name="savingGoalStartDate">
                              <input type="hidden" id="savingGoalStartDateBottom" name="savingGoalStartDateBottom">
                              <input type="hidden" id="savingGoalEndDateBottom" name="savingGoalEndDateBottom">
				              <span id="leftGoalChartBottomSavingCommant" class="Tbk_bold left">0<fmt:message key='aimir.years'/> 0<fmt:message key='aimir.month'/> <fmt:message key='aimir.avgContrast'/>&nbsp;</span>
				              <span class="Tbk_bold left"><input type="text" id="savingGoalTarget" name="savingGoalTarget" class="value" size="4" maxlength="4" />%&nbsp;</span>
				              <span class="Tbk_bold left"><fmt:message key='aimir.energy.economical.target'/></span>
				              <span class="left">&nbsp;<em class="bems_button"><a href="#" onClick="javascript:savingGoalAdd();"><fmt:message key="aimir.set"/></a></em></span>
				              <span id="leftGoalChartBottomSaving" class="right">0%<fmt:message key='aimir.energy.excess'/></span>
			              	</form>
				          </div>
				          <div id="barChartDiv2" class="pt12">
				              
				         </div>
				  </div>
				</div>
				  <!--// left -->  
				
				  <!-- right -->
				<div class="right2 mt10">
				  <div class="mlr20">
				              <object id="rightGoalGridEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="545">
				              <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoal2RightMaxGoalGadget.swf" />
				              <param name="wmode" value="opaque">
				              <!--[if !IE]>-->
				              <object id="rightGoalGridOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoal2RightMaxGoalGadget.swf" width="100%" height="545">
				              <param name="wmode" value="opaque">
				              <!--<![endif]-->
				              <div>
				              <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
				              <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" 
				              alt="Get Adobe Flash player" width="112" height="33" /></a></p>
				              </div>
				              <!--[if !IE]>-->
				              </object>
				              <!--<![endif]-->
				              </object>  
				   </div>
				</div>
				<!--// right -->	        
	
	        </div>
	        
	        
	        
	        <!-- 평균관리-->
	        <div  id="avgMng" class="goal">
	          <div class="width_100 clear">
		        <!-- left -->
		        <div class="left">
		          <div class="mlr20 clear pt10">
		          <div class="searchBox"> 
		          <form id="myForm2" method="post" >
                      <input type="hidden" id="years" name="years">
                      <input type="hidden" id="avgInfoId" name="avgInfoId">
                      <input type="hidden" id="supplierId2" name="supplierId2">	
                        	
			           <ul class="header">
		                <li class="hRight">
						 <em id="avgTabAvgBtnCreate" class="bems_button"><a href="#" onClick="javascript:avgCreate();"><fmt:message key='aimir.savingGoal.avgMgmt.create'/></a></em>
						 <em id="avgTabAvgBtnUpdate" class="bems_button"><a href="#" onClick="javascript:avgUpdate();"><fmt:message key='aimir.savingGoal.avgMgmt.update'/></a></em>
						 <em id="avgTabAvgBtnCancel" class="bems_button"><a href="#" onClick="javascript:avgCancel();"><fmt:message key='aimir.savingGoal.avgMgmt.cancel'/></a></em>
		                </li>
			           </ul>
			           
			           <table>
	                       	<colgroup>
							<col width="60"/>
							<col width=""/>
							</colgroup>
	                        <tr>
	                        <td height="24" class="pr2"><fmt:message key='aimir.description'/></td>
	                        <td><input type="text" name="descr" id="descr" style="width:100%"></td>
	                        </tr>
	                        <tr>
	                        <td height="24"><fmt:message key='aimir.isUsed'/></td>
	                        <td><input type="checkbox" name="used" id="used" class="checkbox"><fmt:message key='aimir.savingGoal.message1'/></td>
	                        </tr>
	                   </table>
	                </form>
					</div>
		
		            <div class="clear pt10">
                            <object id="dataGridAvgLeftEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="600">
                            <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoal2LeftMaxAvgGadget.swf" />
                            <param name="wmode" value="opaque">
                            <!--[if !IE]>-->
                            <object id="dataGridAvgLeftOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoal2LeftMaxAvgGadget.swf" width="100%" height="600">
                            <param name="wmode" value="opaque">
                            <!--<![endif]-->
                            <div>
                            <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
                            <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" 
                            alt="Get Adobe Flash player" width="112" height="33" /></a></p>
                            </div>
                            <!--[if !IE]>-->
                            </object>
                            <!--<![endif]-->
                            </object>
		            </div>
		          </div>
		        </div>
		        <!--// left -->  
		
		        <!-- right -->
		        <div class="right">
		        	<div class="mlr20 pt10">
				        <label class="icon"><fmt:message key='aimir.savingGoal.goalMgmt.details'/></label>
				        <ul class="right">
		                    <li><input id="allView" type="checkbox" onClick="allViewCehck()" class="checkbox"></li>
		                    <li><fmt:message key='aimir.savingGoal.viewAll'/> ( <fmt:message key='aimir.savingGoal.message2'/> )</li>
		                </ul>
		            </div>
		           		       
		            <div class="mlr20 clear">
		                    <object id="dataGridAvgRightEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="525">
		                    <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoal2RightMaxAvgGadget.swf" />
		                    <param name="wmode" value="opaque">
		                    <!--[if !IE]>-->
		                    <object id="dataGridAvgRightOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoal2RightMaxAvgGadget.swf" width="100%" height="525">
		                    <param name="wmode" value="opaque">
		                    <!--<![endif]-->
		                    <div>
		                    <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
		                    <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" 
		                    alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		                    </div>
		                    <!--[if !IE]>-->
		                    </object>
		                    <!--<![endif]-->
		                    </object>  
		            </div>
		            
		        </div>
		        <!--// right -->
		    
	        </div>
	      </div>
	    </div>
	</div>
</body>
</html>