<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <style>
        arrow_left.hilite { background:yellow; }
    </style>
  
    <style>
        div { padding: 15px;}
        p { margin-left:10px; }
        q { margin-left:150px; }
        r { right:15px; }
    </style>  

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<%@ include file="/gadget/system/preLoading.jsp"%>

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" >
	
	/*
	 * getUserInfo.do에서 supplierId를 가지고 온다 
	 */
	var supplierId=${supplierId};
	var zoneId=${zoneId};

	function getcondition(){
		var arrayObj = new Array();
		arrayObj[0] = supplierId;
		arrayObj[1] = zoneId;

		return arrayObj;
	}
	/**
	 * Flex 에서 메세지를 조회하기위한 함수
	 */
	function getFmtMessage(){
	    var fmtMessage = new Array();
	
	    fmtMessage[0] = '<fmt:message key="aimir.alert"/>';                 // 장애
	    fmtMessage[1] = '<fmt:message key="aimir.bems.facilityMgmt.count"/>';//설비(대수)
	    fmtMessage[2] = '<fmt:message key="aimir.bems.facilityMgmt.operation"/>';//운영
	    fmtMessage[3] = '<fmt:message key="aimir.bems.facilityMgmt.stop"/>';//정지
	    fmtMessage[4] = '<fmt:message key="aimir.bems.facilityMgmt.unknown"/>';//모름
	    fmtMessage[5] = '<fmt:message key="aimir.bems.facilityMgmt.location"/>';//위치
	    fmtMessage[6] = '<fmt:message key="aimir.bems.facilityMgmt.kind"/>';//종류
	    fmtMessage[7] = '<fmt:message key="aimir.electricity"/>';//"전기";
	    fmtMessage[8] = '<fmt:message key="aimir.gas"/>';//"가스";
	    fmtMessage[9] = '<fmt:message key="aimir.water"/>';//"수도";
	    fmtMessage[10] = '<fmt:message key="aimir.bems.facilityMgmt.name"/>';//설비명
	    fmtMessage[11] = '<fmt:message key="aimir.bems.facilityMgmt.changeDate"/>';//변경 일시
	    fmtMessage[12] = '<fmt:message key="aimir.status"/>';//상태
	    fmtMessage[13] = '<fmt:message key="aimir.serialNumber"/>';//일련번호
        fmtMessage[14] = '<fmt:message key="aimir.facilityMgmt.situation"/>';//설비 현황
        fmtMessage[15] = '<fmt:message key="aimir.facilityMgmt.operating.status"/>';//설비운영상태
        fmtMessage[16] = '<fmt:message key="aimir.facilityMgmt.operating.status.history"/>';//설비 운영 상태 변경 이력
        fmtMessage[17] = '<fmt:message key="aimir.heatmeter"/>'; //열량 
        
	    return fmtMessage;
	}	

</script>
</head>
<body>
    <div id="wrapper"> 
    <div id="container">
        <!-- 탐색기 및 사용량 테이블 (S) -->
           
                    <!-- 탐색기 및 사용량 테이블 (S) -->
        <div class="Bchart ptrbl10">
            <object id="ZoneMgmtMaxEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="769" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
                <param name="movie" value="${ctx}/flexapp/swf/bems/zoneMgmtMax.swf">
                <param name="quality" value="high">
                <param name="wmode" value="opaque">
                <param name="swfversion" value="9.0.45.0">
                <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
                <param name="expressinstall" value="Scripts/expressInstall.swf">
                <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
                <!--[if !IE]>-->
                <object id="ZoneMgmtMaxOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/zoneMgmtMax.swf" width="100%" height="769">
                  <!--<![endif]-->
                  <param name="quality" value="high">
                  <param name="wmode" value="opaque">
                  <param name="swfversion" value="9.0.45.0">
                  <param name="expressinstall" value="Scripts/expressInstall.swf">
                  <param name="allowScriptAccess" value="always">
                  <!-- The browser displays the following alternative content for users with Flash Player 6.0 and older. -->
                  <div>
                    <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
                    <p><a href="http://www.adobe.com/go/getflashplayr"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
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