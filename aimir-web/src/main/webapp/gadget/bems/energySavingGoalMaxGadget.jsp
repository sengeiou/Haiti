<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">

    /*<![CDATA[*/
    

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:0,weekly:0,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    //공급사ID
    var supplierId="";
    //로그인한 사용자정보를 조회한다.
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;

                $('#supplierId').val( supplierId );
                $('#supplierId2').val( supplierId );
            }
    );

    if( "" == supplierId ){

        supplierId = "${supplierId}";

        $('#supplierId').val( supplierId );
        $('#supplierId2').val( supplierId );
    }

    
    //플렉스객체
    var flexDay;
    var flexWeek;
    var flexMonth;
    var flexYear;
    var flexAvg;
    
    $(document).ready(function(){
        
        // 브라우저별로 플렉스객체를 초기화한다.
        flexDay     = getFlexObject('dataGridDay');
        flexWeek    = getFlexObject('dataGridWeek');
        flexMonth   = getFlexObject('dataGridMonth');
        flexYear    = getFlexObject('dataGridYear');
        flexAvg     = getFlexObject('dataGridAvg');


        $('#avgBtnCreate').show();
        $('#avgBtnUpdate').hide();
        $('#avgBtnCancel').hide();

        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:supplierId}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear   = json.currYear;//currDate.getYear();
                     var currDate  = json.currDate;

                         $("#dailyStartDate").val(currDate);
                });
    });
    
    /**
     * 공통 send
     * 조회버튼클릭시 호출하게 된다.
     * commonDate.jsp 영역에서 날짜를 선택한후 조회버튼을 클릭할때 처리하는 function
     */
    function send(){        
        //http or flex Request Send
        if (flexDay != null) {
            
        	$('#searchDateType').val("1");
            flexDay.requestSendToFlex();
        }
        if (flexWeek != null) {
            
            $('#searchDateType').val("3");
            flexWeek.requestSendToFlex();
        }
        if (flexMonth != null) {
            
            $('#searchDateType').val("4");
            flexMonth.requestSendToFlex();
        }
        if (flexYear != null) {

            $('#searchDateType').val("8");
            flexYear.requestSendToFlex();
        }
        if (flexAvg != null) {

            flexAvg.requestSendToFlex();
        }
    }

    /**
    * 설정버튼을 클릭하면 로딩된 에너지절감 목표값을 추가 or (생성일과 기준일이 같으면) 수정하게된다.
    */
    function savingGoalAdd(){

        if( valueCheck() ){


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
                    url : '${ctx}/gadget/bems/setEnergySavingGoal.do',
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


        fmtMessage[4] = "<fmt:message key="aimir.classification"/>";	// 구분
        fmtMessage[5] = "<fmt:message key="aimir.usage"/>";	// 사용량
        fmtMessage[6] = "<fmt:message key="aimir.co2formula"/>";	// 탄소 배출량
        fmtMessage[7] = "<fmt:message key="aimir.energy.economical.rate"/>";	// 절감율
        fmtMessage[8] = "<fmt:message key="aimir.year1"/>";	// 년
        fmtMessage[9] = "<fmt:message key="aimir.avg.day"/>";	// 일 평균
        fmtMessage[10] = "<fmt:message key="aimir.avg.week"/>";	// 주 평균
        fmtMessage[11] = "<fmt:message key="aimir.avg.month"/>";	// 월 평균
        fmtMessage[12] = "<fmt:message key="aimir.avg.year"/>";	// 년 평균

        fmtMessage[13] = "<fmt:message key="aimir.year1.usage"/>";	// 연별 사용량
        fmtMessage[14] = "<fmt:message key="aimir.facilityMgmt.energy"/>";	// 전기 사용량
        fmtMessage[15] = "<fmt:message key="aimir.facilityMgmt.gas"/>";		// 가스 사용량
        fmtMessage[16] = "<fmt:message key="aimir.facilityMgmt.water"/>";	// 수도 사용량
        fmtMessage[17] = "<fmt:message key="aimir.totalusage"/>";	// 총 사용량

        fmtMessage[18] = "<fmt:message key="aimir.average"/>";		// 평균
        fmtMessage[19] = "<fmt:message key="aimir.avg.energy"/>";	// 전력평균
        fmtMessage[20] = "<fmt:message key="aimir.avg.gas"/>";		// 가스평균
        fmtMessage[21] = "<fmt:message key="aimir.avg.water"/>";		// 수도평균
        fmtMessage[22] = "<fmt:message key="aimir.avg.total.usage"/>";	// 총 평균사용량

        fmtMessage[23] = "<fmt:message key="aimir.createdate"/>";	// 생성날짜
        fmtMessage[24] = "<fmt:message key="aimir.isUsed"/>";	// 사용유무
        fmtMessage[25] = "<fmt:message key="aimir.description"/>";	// 설명
        fmtMessage[26] = "<fmt:message key="aimir.basis.day"/>";	// 기준일
        fmtMessage[27] = "<fmt:message key="aimir.avgusage"/>";	// 평균 사용량
        fmtMessage[28] = "<fmt:message key="aimir.goal"/>";		// 목표
        fmtMessage[29] = "<fmt:message key="aimir.list"/>";		// 리스트  

        fmtMessage[30] = "<fmt:message key="aimir.day"/>";	// 일
        fmtMessage[31] = "<fmt:message key="aimir.week"/>";	// 주
        fmtMessage[32] = "<fmt:message key="aimir.month"/>";	// 월
        fmtMessage[33] = "<fmt:message key="aimir.savingGoal.goalMgmt.details"/>";	// 목표내역

        fmtMessage[34] = "<fmt:message key="aimir.isUsed.yes"/>";		// 사용
        fmtMessage[35] = "<fmt:message key="aimir.isUsed.no"/>";		// 비사용
        
        return fmtMessage;
    }
    
    /**
     * Flex에서 호출하는 함수.
     * Flex 에서 조회조건에 필요한 parameter값을 전달하는 함수.
     */
    function putParams(){
        var condArray = new Array();
        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = supplierId;
        condArray[4] = $('#savingGoal').val();
        condArray[5] = $('#savingGoalStartDate').val();

        //alert( "putParams() : " + condArray );
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();
        return condArray;
    }
    
    /**
     * Flex에서 호출하는 함수.
     * jsp 영역에  평균관리 정보를 update하기 위해 필요한 parameter값을 전달받는 함수.
     */
    function updateSetAvgInfo( dataArray ){


        $('#avgBtnCreate').hide();
        $('#avgBtnUpdate').show();
        $('#avgBtnCancel').show();
        
        $('#avgInfoId').val( dataArray[0] ); // id
        $('#descr').val( dataArray[1] ); // descr

        if( dataArray[2] == "true" ){ // used
            $('#used').attr("checked", "checked");
        } else {
        	$('#used').attr("checked", "");
        }
        
        $('#years').val( dataArray[3] ); // years

    }
    
    /**
     * Flex에서 호출하는 함수.
     * jsp 영역에  절감목표치와 기준일 노출을 위해 필요한 parameter값을 전달받는 함수.
     */
    function setSavingGoalInfo( dataArray ){

        var gubunType = dataArray[2];
        var yearsSize = dataArray[4];
        var rtnVal = "";
        if( "1" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.day'/>"; //일
                $('#savingDayTypeDay').text( rtnVal + " <fmt:message key='aimir.energy.economical.present'/>" ); // "절감 현황"
        } else if( "3" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.week'/>"; //주
                $('#savingDayTypeWeek').text( rtnVal + " <fmt:message key='aimir.energy.economical.present'/>" ); // "절감 현황"
        } else if( "4" == gubunType ) {
            
                rtnVal = "<fmt:message key='aimir.month'/>"; //월
                $('#savingDayTypeMonth').text( rtnVal + " <fmt:message key='aimir.energy.economical.present'/>" ); // "절감 현황"
        } else if( "8" == gubunType ) {
                
                rtnVal = "<fmt:message key='aimir.year1'/>"; //년
                $('#savingDayTypeYear').text( rtnVal + " <fmt:message key='aimir.energy.economical.present'/>" ); // "절감 현황"
        }

        $('#savingGoal').val( dataArray[0] );
        $('#savingGoalStartDate').val( dataArray[1] );



        if( "" == $('#savingGoalStartDate').val() ){
	        var initDayT = $('#searchStartDate').val();
	        var initDay = initDayT.substring(0,4) + "-" + initDayT.substring(4,6) + "-" + initDayT.substring(6,8);
	        $('#savingGoalStartDate').val( initDay );
        }
        

        var a = Number( dataArray[3] ).toFixed(2);
        
        
        if( "1" == gubunType ) {

            $('#yearsSizeDay').text( yearsSize + "<fmt:message key='aimir.year1'/> <fmt:message key='aimir.avgContrast'/>" );
            
            if( a > 0 ){
                
                $('#savingCommentDay').text( "<fmt:message key='aimir.energy.excess'/>" ); //"초과"
                $('#savingDay').removeClass();
                $('#savingDay').addClass("Trd_bold");
                $('#savingCommentDay').removeClass();
                $('#savingCommentDay').addClass("Trd_bold");
            } else if( a == 0 ){
                
                $('#savingCommentDay').text( "" );
                $('#savingDay').removeClass();
                $('#savingDay').addClass("Tbk_bold");
            }else {
                
                $('#savingCommentDay').text( "<fmt:message key='aimir.energy.economical'/>" ); //"절감"
                $('#savingDay').removeClass();
                $('#savingDay').addClass("Tbu_bold");
                $('#savingCommentDay').removeClass();
                $('#savingCommentDay').addClass("Tbu_bold");
                
                a = a * -1;
            }

            $('#savingDay').text( a  + "%");
            
        } else if( "3" == gubunType ) {

            $('#yearsSizeWeek').text( yearsSize + "<fmt:message key='aimir.year1'/> <fmt:message key='aimir.avgContrast'/>" );
            
            if( a > 0 ){
                
                $('#savingCommentWeek').text( "<fmt:message key='aimir.energy.excess'/>" ); //"초과"
                $('#savingWeek').removeClass();
                $('#savingWeek').addClass("Trd_bold");
                $('#savingCommentWeek').removeClass();
                $('#savingCommentWeek').addClass("Trd_bold");
            } else if( a == 0 ){
                
                $('#savingCommentWeek').text( "" );
                $('#savingWeek').removeClass();
                $('#savingWeek').addClass("Tbk_bold");
            } else {
                
                $('#savingCommentWeek').text( "<fmt:message key='aimir.energy.economical'/>" ); //"절감"
                $('#savingWeek').removeClass();
                $('#savingWeek').addClass("Tbu_bold");
                $('#savingCommentWeek').removeClass();
                $('#savingCommentWeek').addClass("Tbu_bold");
                
                a = a * -1;
            }

            $('#savingWeek').text( a  + "%");
            
        } else if( "4" == gubunType ) {
            
            $('#yearsSizeMonth').text( yearsSize + "<fmt:message key='aimir.year1'/> <fmt:message key='aimir.avgContrast'/>" );

            if( a > 0 ){
                
                $('#savingCommentMonth').text( "<fmt:message key='aimir.energy.excess'/>" ); //"초과"
                $('#savingMonth').removeClass();
                $('#savingMonth').addClass("Trd_bold");
                $('#savingCommentMonth').removeClass();
                $('#savingCommentMonth').addClass("Trd_bold");
            } else if( a == 0 ){
                
                $('#savingCommentMonth').text( "" );
                $('#savingMonth').removeClass();
                $('#savingMonth').addClass("Tbk_bold");
            } else {
                
                $('#savingCommentMonth').text( "<fmt:message key='aimir.energy.economical'/>" ); //"절감"
                $('#savingMonth').removeClass();
                $('#savingMonth').addClass("Tbu_bold");
                $('#savingCommentMonth').removeClass();
                $('#savingCommentMonth').addClass("Tbu_bold");
                
                a = a * -1;
            }

            $('#savingMonth').text( a  + "%");
            
        } else if( "8" == gubunType ) {

            $('#savingYear').text( a  + "%");
            $('#yearsSizeYear').text( yearsSize + "<fmt:message key='aimir.year1'/> <fmt:message key='aimir.avgContrast'/>" );
            if( a > 0 ){

                $('#savingCommentYear').text( "<fmt:message key='aimir.energy.excess'/>" ); //"초과"
                $('#savingYear').removeClass();
                $('#savingYear').addClass("Trd_bold");
                $('#savingCommentYear').removeClass();
                $('#savingCommentYear').addClass("Trd_bold");
            } else if( a == 0 ){

                $('#savingCommentYear').text( "" );
                $('#savingYear').removeClass();
                $('#savingYear').addClass("Tbk_bold");
            } else { 
																																																																																																																																															                $('#savingYear').addClass("value_red");
                $('#savingCommentYear').text( "<fmt:message key='aimir.energy.economical'/>" ); //"절감"
                $('#savingYear').removeClass();
                $('#savingYear').addClass("Tbu_bold");
                $('#savingCommentYear').removeClass();
                $('#savingCommentYear').addClass("Tbu_bold");
                
                a = a * -1;
            }

            $('#savingYear').text( a  + "%");
        }
        

    }
    
    function valueCheck(){

        var check = true;

        if( "" == $('#savingGoal').val() || "0" == $('#savingGoal').val() ){
            alert("<fmt:message key='aimir.savingGoal.empty'/>"); // "절감 목표 값을 입력해주세요!"
            check = false;
            return check;
        }

        if( "" == $('#savingGoalStartDate').val() ){
            alert("<fmt:message key='aimir.savingGoal.startDate.empty'/>"); // "기준일을 입력해주세요! YYYY-MM-DD"
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

            alert("<fmt:message key='aimir.savingGoal.input.empty2'/>"); // 목표 설정에 사용할 년도별 사용량을 첵크해 주세요.
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

                                if (flexAvg != null) {

                                    flexAvg.requestSendToFlex();
                                }
                                
                            } else {
                                alert("<fmt:message key='aimir.save.error'/>"); // 저장되지 않았습니다.
                            }

                            $('#avgInfoId').val( "" ); // id
                            $('#descr').val( "" ); // descr
                            $('#used').attr("checked", "");
                            $('#years').val( "" ); // years
                            
                            $('#avgBtnCreate').show();
                            $('#avgBtnUpdate').hide();
                            $('#avgBtnCancel').hide();

                        },
                    url : '${ctx}/gadget/bems/setEnergyAvg.do',
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

                                if (flexAvg != null) {

                                    flexAvg.requestSendToFlex();
                                }
                                
                            } else {
                                alert("<fmt:message key='aimir.alert.groupMgmt.msg1.err'/>"); // 수정되지 않았습니다.
                            }

                        },
                    url : '${ctx}/gadget/bems/setEnergyAvg.do',
                    datatype : 'json'
            };

            $('#myForm2').ajaxSubmit(params);

            $('#avgInfoId').val( "" ); // id
            $('#descr').val( "" ); // descr
            $('#used').attr("checked", "");
            $('#years').val( "" ); // years
            
            $('#avgBtnCreate').show();
            $('#avgBtnUpdate').hide();
            $('#avgBtnCancel').hide();
                
        }else{

            $('#avgBtnCreate').hide();
            $('#avgBtnUpdate').show();
            $('#avgBtnCancel').show();
        }

 
    }

    function avgCancel(){

        $('#avgInfoId').val( "" ); // id
        $('#descr').val( "" ); // descr
        $('#used').attr("checked", "");
        $('#years').val( "" ); // years
        
        $('#avgBtnCreate').show();
        $('#avgBtnUpdate').hide();
        $('#avgBtnCancel').hide();
    }



    
    // 달력 붙이기
    $(function() {
        $("#dailyStartDate").datepicker({dateFormat: 'y. m. d', maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, onClose: function(dateText, inst) {dailyArrow(dateText,0); }});
        $('#dailySearch').trigger('click');

        $("#savingGoalStartDate").datepicker({dateFormat: 'yy-mm-dd', maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
    });
    

    $(function() { $('#dailyLeft')      .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),-1); } ); });
    $(function() { $('#dailyRight')     .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),1); } ); });



    
    /**
     * 일별 화살표처리
     */
    function dailyArrow(bfDate,val,flag){

        bfDate = bfDate.replace('/','').replace('/','');
    	//alert( "supplierId:" + supplierId + " , bfDate:" + bfDate + " , val:" + val ); 
        //alert( "A:" + $('#searchStartDate').val() );
        //alert( "B:" + $('#dailyStartDate').val() );
        
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate,addVal:val,supplierId:supplierId}
                ,function(json) {
                    modifyDbDate( json.searchDate );
                    $('#dailyStartDate').val(json.searchDate);
                });

        //alert( "C:" + $('#searchStartDate').val() );
        //alert( "D:" + $('#dailyStartDate').val() );
    }

    // LocalDateType ▶ YYYYMMDD
    function modifyDbDate(targetDate){
        $.getJSON("${ctx}/common/convertDBDate.do"
               ,{localDate:targetDate, supplierId:supplierId}
               ,function(json) {
            	   var returnDate = json.dbDate;

                   $('#searchStartDate').val( returnDate.replace('/','').replace('/',''));
                   $('#searchEndDate').val( returnDate.replace('/','').replace('/',''));
                });

    }

    // YYYYMMDD ▶ LocalDateType
    function modifyLocalDate(targetDate){
        
        $.getJSON("${ctx}/common/convertLocalDate.do"
               ,{dbDate:targetDate, supplierId:supplierId}
               ,function(json) {
                    alert(json.localDate);
                });
    }

    
    $(function() { $('#dailySearch')        .bind('click',function(event) { send(); } ); });


    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertSearchDate(){
        $.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#searchStartDate').val(), searchEndDate:$('#searchEndDate').val(), supplierId:supplierId}
                ,function(json) {
                    $('#searchStartDate').val(json.searchStartDate);
                    $('#searchEndDate').val(json.searchEndDate);
                });
    }
    //======================================================================================
    
    /*]]>*/
</script>
</head>
<body>

	<div id="wrapper">
	
	    <div id="container2">
	
	        <div class="goal">
	            <!-- left -->
	            <div class="left">
	                <div class="mlr20">
	               
				     <label class="block">
						 <span class="icon_tit"></span>
						 <span class="Tbu_bold mt2"><fmt:message key='aimir.savingGoal.goalMgmt'/></span>
					 </label>

	                
	                   <div class="searchBox">
	                    <form id="myForm" >
	              				<input type="hidden" id="supplierId" name="supplierId" />
		                    <!-- 절감목표 및 기준일 (S) -->
					            <input id="searchStartDate" type="hidden"/>
						        <input id="searchEndDate" type="hidden" />
						        <input id="searchStartHour" type="hidden"/>
						        <input id="searchEndHour" type="hidden" />
						        <input id="searchDateType" type="hidden" value="0"/>
							<ul class="header ptrbl10">
								<li class="hLeft tit_default"><input id="savingGoal" name="savingGoal" size="3" maxlength="3" class="value"></li>
								<li class="hLeft Tbk mt2">%<fmt:message key="aimir.energy.economical.target"/><!-- 절감목표  --></li>    
								<li class="hRight">
									<ul>
										<li class="mt2 Tbk"><fmt:message key="aimir.basis.day"/><!-- 기준일 -->&nbsp;</li>
										<li><input id="savingGoalStartDate" name="savingGoalStartDate" class="date"></li>         
										<li class="mleft5"><em class="bems_button"><a href="#" onclick="javascript:savingGoalAdd();"><fmt:message key="aimir.set"/><!-- 설정 --></a></em></li>
									</ul>
								</li>
							  </ul>
					        
					         <!-- 절감목표 및 기준일  (E) -->
		                 </form>
	                   
	                    <div class="clear"></div>
	                    
	                    <div>
	                       <%--
	                       <%@ include file="../commonDate.jsp" %>
					         --%>
					         <ul>
	                       		<li><button id="dailyLeft" type="button" class="backicon"></button></li>
					            <li style="width:150px;"><input id="dailyStartDate" type="text" readonly="readonly"></li>
					            <li><button id="dailyRight" type="button" class="nexticon"></button></li>
					            <li><em class="bems_button"><a href="#" id="dailySearch"><fmt:message key="aimir.button.search" /></a></em></li>
					       </ul>
                        </div>
	                   	</div> 
	                  	<div class="chart pt15">
                           	<span id="savingDayTypeDay"    class="Tbk_bold pr5"><fmt:message key="aimir.day"/><fmt:message key="aimir.energy.economical.present"/>&nbsp;</span>
                            <span id="yearsSizeDay"        class="Tgy_bold pr5">0<fmt:message key="aimir.year1"/><fmt:message key="aimir.avgContrast"/>&nbsp;</span>
                            <span id="savingDay"           class="Tbu_bold pr5">0%</span>
                            <span id="savingCommentDay"    class="Tbu_bold"></span>
	                   </div>

 				  
	                   <div class="chart">    
                            <object id="dataGridDayEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120">
                            <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" />
                            <param name="wmode" value="opaque">
                            <!--[if !IE]>-->
                            <object id="dataGridDayOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" width="100%" height="120">
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
                    	<div class="chart pt15">
                            <span id="savingDayTypeWeek"   class="Tbk_bold pr5"><fmt:message key="aimir.week"/><fmt:message key="aimir.energy.economical.present"/>&nbsp; </span>
                            <span id="yearsSizeWeek"       class="Tgy_bold pr5">0<fmt:message key="aimir.year1"/><fmt:message key="aimir.avgContrast"/>&nbsp;</span>
                            <span id="savingWeek"          class="Tbu_bold pr5">0%</span>
                            <span id="savingCommentWeek"   class="Tbu_bold pr5"></span>
	                     </div>
	                     <div class="chart">
                            <object id="dataGridWeekEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120">
                            <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" />
                            <param name="wmode" value="opaque">
                            <!--[if !IE]>-->
                            <object id="dataGridWeekOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" width="100%" height="120">
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
                       <div class="chart pt15">
                            <span id="savingDayTypeMonth"  class="Tbk_bold pr5"><fmt:message key="aimir.month"/><fmt:message key="aimir.energy.economical.present"/>&nbsp; </span>
                            <span id="yearsSizeMonth"      class="Tgy_bold pr5">0<fmt:message key="aimir.year1"/><fmt:message key="aimir.avgContrast"/>&nbsp;</span>
                            <span id="savingMonth"         class="Tbu_bold pr5">0%</span>
                            <span id="savingCommentMonth"  class="Tbu_bold"></span>
                       </div>
                       <div class="chart">
                            <object id="dataGridMonthEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120">
                            <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" />
                            <param name="wmode" value="opaque">
                            <!--[if !IE]>-->
                            <object id="dataGridMonthOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" width="100%" height="120">
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
                        <div class="chart pt15">
                            <span id="savingDayTypeYear"   class="Tbk_bold pr5"><fmt:message key="aimir.year1"/><fmt:message key="aimir.energy.economical.present"/>&nbsp; </span>
                            <span id="yearsSizeYear"       class="Tgy_bold pr5">0<fmt:message key="aimir.year1"/><fmt:message key="aimir.avgContrast"/>&nbsp; </span>
                            <span id="savingYear"          class="Tbu_bold pr5">0%</span>
                            <span id="savingCommentYear"   class="Tbu_bold"></span>
                        </div>
                        <div class="chart">
                            <object id="dataGridYearEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120">
                            <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" />
                            <param name="wmode" value="opaque">
                            <!--[if !IE]>-->
                            <object id="dataGridYearOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMaxGadget.swf" width="100%" height="120">
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
	                <div class="mlr20">

					 <label class="block">
						 <span class="icon_tit"></span>
						 <span class="Tbu_bold mt2"><fmt:message key='aimir.savingGoal.avgMgmt'/></span>
					 </label>

	                  
                        <div class="searchBox"> 
                       
                        <ul class="header">
					      <li class="hRight">
							   <em id="avgBtnCreate" class="bems_button"><a href="#" onClick="javascript:avgCreate();"><fmt:message key='aimir.savingGoal.avgMgmt.create'/></a></em>
		                       <em id="avgBtnUpdate" class="bems_button"><a href="#" onClick="javascript:avgUpdate();"><fmt:message key='aimir.savingGoal.avgMgmt.update'/></a></em>
		                       <em id="avgBtnCancel" class="bems_button"><a href="#" onClick="javascript:avgCancel();"><fmt:message key='aimir.savingGoal.avgMgmt.cancel'/></a></em>
						  </li>
						</ul>
						<form id="myForm2" method="post">
                            <input type="hidden" id="years" name="years">
                            <input type="hidden" id="avgInfoId" name="avgInfoId">
                            <input type="hidden" id="supplierId2" name="supplierId2">
	                        <table>
	                        	<colgroup>
								<col width="60"/>
								<col width=""/>
								</colgroup>
		                        <tr>
		                        <td height="24"><fmt:message key='aimir.description'/></td>
		                        <td><input type="text" name="descr" id="descr" style="width:100%"></td>
		                        </tr>
		                        <tr>
		                        <td height="24"><fmt:message key='aimir.isUsed'/></td>
		                        <td><input type="checkbox" name="used" id="used" class="checkbox"><fmt:message key='aimir.savingGoal.message1'/></td>
		                        </tr>
	                        </table>
	                      
                        </form>
                        </div> 
	                </div>   
	                
	                <div class="mlr20 clear">
	                        <object id="dataGridAvgEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="700">
	                        <param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMaxAvgGadget.swf" />
	                        <param name="wmode" value="opaque">
	                        <!--[if !IE]>-->
	                        <object id="dataGridAvgOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMaxAvgGadget.swf" width="100%" height="700">
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
</body>
</html>