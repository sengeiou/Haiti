<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>

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

    var myLog = "";
    
    //공급사ID
    var supplierId="";
    //로그인한 사용자정보를 조회한다.
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                supplierId = json.supplierId;

                $('#supplierId').val( supplierId );

                myLog = myLog + "supplierid : " + supplierId;
            }
    );
    
    //플렉스객체
    var flex;
    
    $(document).ready(function(){

    	// 브라우저별로 플렉스객체를 초기화한다.
        flex = getFlexObject('dataGrid');
    });
    
    /**
     * 공통 send
     * 조회버튼클릭시 호출하게 된다.
     * commonDateTab.jsp 영역에서 날짜를 선택한후 조회버튼을 클릭할때 처리하는 function
     */
    function send(){
        
        //alert( putParams().toString() );
        if (flex != null) {
            
            flex.requestSendToFlex();
        }
    }

 // 달력 붙이기
    $(function() {
        $("#savingGoalStartDate").datepicker({dateFormat: 'yy-mm-dd', maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
    });

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
        fmtMessage[8] = "<fmt:message key="aimir.day"/>";	// 일
        fmtMessage[9] = "<fmt:message key="aimir.week"/>";	// 주
        fmtMessage[10] = "<fmt:message key="aimir.month"/>";	// 월
        fmtMessage[11] = "<fmt:message key="aimir.year1"/>";	// 년
        fmtMessage[12] = "<fmt:message key="aimir.avg.day"/>";	// 일 평균
        fmtMessage[13] = "<fmt:message key="aimir.avg.week"/>";	// 주 평균
        fmtMessage[14] = "<fmt:message key="aimir.avg.month"/>";	// 월 평균
        fmtMessage[15] = "<fmt:message key="aimir.avg.year"/>";	// 년 평균
        
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
        
//      condArray[4] = $('#searchStartHour').val();
//      condArray[3] = $('#searchEndHour').val();

        myLog = myLog + " , searchStartDate : " + condArray[0];
        myLog = myLog + " , searchEndDate : " + condArray[1];
        myLog = myLog + " , searchDateType : " + condArray[2];
        myLog = myLog + " , supplierId : " + condArray[3];
        myLog = myLog + " , savingGoal : " + condArray[4];
        myLog = myLog + " , savingGoalStartDate : " + condArray[5];

        
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
    
        $('#savingGoal').val( dataArray[0] );
        $('#savingGoalStartDate').val( dataArray[1] );
        $('#savingDayType').text( rtnVal + "<fmt:message key='aimir.comparison'/>" ); // "대비"



        if( "" == $('#savingGoalStartDate').val() ){
            var initDayT = $('#searchStartDate').val();
            var initDay = initDayT.substring(0,4) + "-" + initDayT.substring(4,6) + "-" + initDayT.substring(6,8);
            $('#savingGoalStartDate').val( initDay );
        }
        
        
        var a = Number( dataArray[3] ).toFixed(2);
        if( a >= 0 ){
            
            $('#savingComment').text( "<fmt:message key='aimir.energy.excess'/>" ); //"초과"
            $('#saving').removeClass();
            $('#saving').addClass("value_red");
        } else {
            
            $('#savingComment').text( "<fmt:message key='aimir.energy.economical'/>" ); //"절감"
            $('#saving').removeClass();
            $('#saving').addClass("value_blue");

            a = a * -1;
        }

        //alert( dataArray[3] + " , " + a  + " , " + Math.abs( a ) );
        //$('#saving').text( Math.abs( a ) + "%");
        $('#saving').text(  a  + "%");

        
        //$('#log').text( myLog );
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

    
    //======================================================================================
    
    /*]]>*/
</script>
</head>
<body>
<span id="log"></span>
<div id="wrapper">
 <div id="container2">
            
         <!-- 절감목표 및 기준일 (S) --> 
         <form id="myForm">
         <input type="hidden" id="supplierId" name="supplierId">
		<ul class="header ptb5">
		<li class="hLeft tit_default"><input id="savingGoal" name="savingGoal" size="3" maxlength="3" class="value"></li>
		<li class="hLeft Tbk mt2">% <fmt:message key="aimir.energy.economical.target"/><!-- 절감목표  --></li>    
		<li class="hRight">
			<ul>
				<li class="Tbk mt2"><fmt:message key="aimir.basis.day"/><!-- 기준일 -->&nbsp;</li>
				<li><input id="savingGoalStartDate" name="savingGoalStartDate" class="date"></li>         
				<li class="mleft5"><em class="bems_button"><a href="#" onclick="javascript:savingGoalAdd();"><fmt:message key="aimir.set"/><!-- 설정 --></a></em></li>
			</ul>
			</li>
		  </ul>
         </form>
         <!-- 절감목표 및 기준일  (E) -->
              
         <div class="clear"></div>
                
         <!-- tab (S) -->
         <div class="h30 clear" style="display:block">
         <%@ include file="../commonDateTab.jsp" %>
         </div>
         
         <!-- tab (E) -->
         <div class="result_text clear valueMargin" >
             <em id="savingDayType" class="text"></em>
             <em id="saving" class="value_red"></em><!--절감일 경우 class="value_blue"-->
             <em id="savingComment" class="text"></em>
         </div>   
        
         <div class="Bchart ptrbl10">
          
			<object id="dataGridEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="125">
			<param name="movie" value="${ctx}/flexapp/swf/bems/energySavingGoalMiniGadget.swf" />
			<param name="wmode" value="opaque">
			<!--[if !IE]>-->
			<object id="dataGridOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/energySavingGoalMiniGadget.swf" width="100%" height="125">
			<param name="wmode" value="opaque">
			<!--<![endif]-->
			<div>
			<h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
			<p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
			</div>
			<!--[if !IE]>-->
			</object>
			<!--<![endif]-->
			</object>           
        </div>
                
 </div>
</div>
</body>
</html>

