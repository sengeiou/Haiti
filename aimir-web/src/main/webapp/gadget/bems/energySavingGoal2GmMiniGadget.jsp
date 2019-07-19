<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>

<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
<script type="text/javascript" charset="utf-8">

    /*<![CDATA[*/
    

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:1};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    var myLog = "";
    
    //공급사ID
    var supplierId="";
    var usageGlobalData;

    // 에너지 타입 ( 전기Em : 0 , 가스Gm : 1 , 수도Wm : 2 )
    var myEnergyType = "1";
    var myEnergyTypeStr = "Gm";
    
    //로그인한 사용자정보를 조회한다.
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;

                $('#supplierId').val( supplierId );
                getEnergySavingGoal2Info();
                myLog = myLog + "supplierid : " + supplierId;
            }
    );


    if( "" == supplierId ){

        supplierId = "${supplierId}";

        $('#supplierId').val( supplierId );
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
    	var supId = supplierId;
    	var savingGoalTarget = $('#savingGoalTarget').val();
    	var savingGoalStartDate = $('#savingGoalStartDate').val();
    	var energyType = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
    	var requestType='';
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
        	savingGoal:savingGoalTarget,
        	requestType:requestType,
        	msgAvgYear:encodeURIComponent(fmtMessage[4]),
        	msgGoal:encodeURIComponent(fmtMessage[5]),
        	msgPrediction:encodeURIComponent(fmtMessage[6])
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

    window.onresize = setGraph;
    function setGraph(usageData){
    	
            if(usageData){
            usageData="<chart chartLeftMargin='5' "
            + "chartRightMargin='20' " 
			+ "chartTopMargin='5' "
			+ "chartBottomMargin='0' "
			+ "decimals='3' "
			+ fChartStyle_Common
            + fChartStyle_Font
			+ fChartStyle_StColumn3D_nobg
			+ usageData;
    	    	usageGlobalData= usageData;
    	    }
            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "usageChartId",($('#barChartDiv').width()),  200, "0", "0");            
        
            if(usageData){
            	usageChart.setDataXML(usageData);
            }else{
            	usageChart.setDataXML(usageGlobalData);
            }
            usageChart.setTransparent("transparent");           
            usageChart.render("barChartDiv");
    }
    
    //플렉스객체
    var flex;
    
    $(document).ready(function(){
        
    	// 브라우저별로 플렉스객체를 초기화한다.
        flex = getFlexObject('dataGridDay2');
        
        //getCycleTypeList();
    });

    /**
     * 주기 정보
     */
    function getCycleTypeList() {
        
    	var cycleType = [{'name':'<fmt:message key="aimir.day"/>' , 'id':'1'},{'name':'<fmt:message key="aimir.week"/>' , 'id':'3'},{'name':'<fmt:message key="aimir.day.mon"/>' , 'id':'4'},{'name':'<fmt:message key="aimir.quarter"/>' , 'id':'9'}];
    
        $('#dateTypeList').loadSelect(cycleType);
        $("#dateTypeList option:eq(0)").replaceWith("<option value=1>" + '<fmt:message key="aimir.day"/>' + "</option>");
        if(supplierId == ""){
            $("#dateTypeList").val(0);
        }else{
            $("#dateTypeList").val(searchDateType);
        }
        $("#dateTypeList").selectbox();
    }

    
    /**
     * 공통 send
     * commonDateTab.jsp 영역에서 날짜를 선택한후 조회버튼을 클릭할때 처리하는 function
     */
    function send(){
        
        //alert( putParams().toString() );
       // if (flex != null) {

        	$('#savingGoalStartDate').val( $('#searchStartDate').val() );
        	$('#savingGoalDateType').val( $('#searchDateType').val() );
        	 getEnergySavingGoal2Info();
            //flex.requestSendToFlex();
       // }
    }

    /**
    * 설정버튼을 클릭하면 로딩된 에너지절감 목표값을 추가 or (생성일과 기준일이 같으면) 수정하게된다.
    */
    function savingGoalAdd(){

        $('#savingGoalDateType').val( $('#searchDateType').val() );
        $('#savingGoalStartDate').val( $('#searchStartDate').val() );
        
        if( valueCheck() ){

            var params = {
                    success :

                        function(json) {

                            var result = json.result;
            
                            if( "Y" == result ){
                                alert("<fmt:message key='aimir.save'/>"); // 저장되었습니다.
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

        fmtMessage[4] = "<fmt:message key="aimir.avg.year"/>";	// 년 평균
        fmtMessage[5] = "<fmt:message key="aimir.goal"/>";	// 목표
        fmtMessage[6] = "<fmt:message key="aimir.prediction"/>";	// 예상
        fmtMessage[7] = "㎥";	// 단위
        
        return fmtMessage;
    }
    function getSupplierId(){

   	 $.getJSON('${ctx}/common/getUserInfo.do',
   	            function(json) {
   	                supplierId = json.supplierId;

   	                $('#supplierId').val( supplierId );

   	                myLog = myLog + "supplierid : " + supplierId;
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
        condArray[4] = $('#savingGoalTarget').val();
        condArray[5] = $('#savingGoalStartDate').val();
        condArray[6] = myEnergyType; // energyType : 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )
        
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();

        myLog = "";
        myLog = myLog + " , searchStartDate : " + condArray[0];
        myLog = myLog + " , searchEndDate : " + condArray[1];
        myLog = myLog + " , searchDateType : " + condArray[2];
        myLog = myLog + " , supplierId : " + condArray[3];
        myLog = myLog + " , savingGoalTarget : " + condArray[4];
        myLog = myLog + " , savingGoalStartDate : " + condArray[5];

        //alert( myLog );
        return condArray;
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
    
        $('#savingGoalTarget').val( dataArray[0] );
        $('#savingGoalStartDate').val( dataArray[1] );

        $('#avgComment').text( dataArray[4] + " <fmt:message key='aimir.years'/>  " + dataArray[3] + rtnVal + " <fmt:message key='aimir.contrast'/>" ); //"대비"


        if( "" == $('#savingGoalStartDate').val() ){
            var initDayT = $('#searchStartDate').val();
           // var initDay = initDayT.substring(0,4) + "-" + initDayT.substring(4,6) + "-" + initDayT.substring(6,8);
            $('#savingGoalStartDate').val( initDayT );
        }
        
        
        var a = Number( dataArray[5] ).toFixed(2);
        $('#savingComment').removeClass();
        if( a > 0 ){
            $('#savingComment').addClass("value_red t_center clear");
            $('#savingComment').text(  a  + "% <fmt:message key='aimir.energy.excess'/>"); //"초과"
        } else if(a == 0) {
        	$('#savingComment').addClass("Tbk_bold14 t_center clear");
            $('#savingComment').text(  a  + "%"); 			//"0.00 %"
        } else {
            $('#savingComment').addClass("value_blue t_center clear");
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

    
    //======================================================================================
    
    /*]]>*/
</script>
</head>

<body>
<div id="wrapper">

 <div id="energy">
   
    <!-- 목표관리-->
    <div class="goal">
    
        <div >
            <%@ include file="../commonDateTab.jsp" %><%-- var tabs 에 날짜 선택모드로 셋팅. --%>
            <script type="text/javascript"> 
                    sendRequest(DateType.DAILY); 
            </script>
        </div>
        <div class="ptrbl10 clear">
	            <ul class="valueMargin2">
	                <li>
		                <form id="myForm" >
                            <input type="hidden" id="supplierId" name="supplierId">
                            <input type="hidden" id="savingGoalDateType" name="savingGoalDateType">
                            <input type="hidden" id="savingGoalStartDate" name="savingGoalStartDate">
		                    <span id="avgComment" class="black left">0<fmt:message key='aimir.years'/> 0<fmt:message key='aimir.month'/> <fmt:message key='aimir.avgContrast'/></span>
		                    <span class="left"><input id="savingGoalTarget" name="savingGoalTarget" type="text" class="value" size="4" maxlength="4">%&nbsp;</span>
		                    <span class="left"><fmt:message key='aimir.energy.economical.target'/></span>
		                    <span class="right">&nbsp;<em class="bems_button"><a href="#" onclick="javascript:savingGoalAdd();"><fmt:message key="aimir.set"/><!-- 설정 --></a></em></span>
	                    </form>
	                 </li>
	                 </ul>
	                
	                <ul class="clear t_center pt10" >  
	                 <li id="savingComment" class="Tbk_bold14 t_center"></li>
	                 
	                <li>
	                  <div id="barChartDiv"></div>
	                </li>
	            </ul>
         </div>
     
     
    
    </div>
    <!--// 목표관리-->        
    

 
 </div>
</div>

</body>

</html>

