<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.customerview"/>(<fmt:message key="aimir.watermeter"/>)</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" >/*<![CDATA[*/

        // 페이지 로드시 로그인한 사용자의 공급사 ID 세팅
        var supplierId = "";
        var serviceType = ServiceType.Water;
        var flex;

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );

        $(function(){
            if($('#customerMiniChartOt')[0] == null){
                flex = $('#customerMiniChartEx')[0];
            }else{
                flex = $('#customerMiniChartOt')[0];
            }
        });

        /**
         * request send
         */
        function send() {
            if (flex != null) {
                flex.customerSearch();
            }
        }

        /**
         * fmt message
         */
        function getFmtMessage(){
            var cnt = 0;
            var fmtMessage = new Array();

            fmtMessage[cnt++] = "<fmt:message key='aimir.alert'/>";                    // Error!
            fmtMessage[cnt++] = "<fmt:message key='aimir.contract.tariff.type'/>";     // 계약 종별
            fmtMessage[cnt++] = "<fmt:message key='aimir.householdcount'/>";           // 가구수
            fmtMessage[cnt++] = "<fmt:message key='aimir.view.detail'/>";              // 상세 보기

            fmtMessage[cnt++] = "<fmt:message key='aimir.normal'/>";  // 정상 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.temporaryPause'/>";  // 휴지 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.pause'/>";  // 정지 고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.cancel2'/>";  // 해지 고객

            fmtMessage[cnt++] = "<fmt:message key='aimir.standard2'/>";                // 기준
            fmtMessage[cnt++] = "<fmt:message key='aimir.household'/>";                // 가구

            fmtMessage[cnt++] = "<fmt:message key='aimir.totalCustomerCount'/>";       // 전체 고객수
            fmtMessage[cnt++] = "<fmt:message key='aimir.contractCustomer.today'/>";   // 금일 신규고객
            fmtMessage[cnt++] = "<fmt:message key='aimir.cancelCustomer.today'/>";     // 금일 해지고객

            return fmtMessage;
        }

        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = supplierId;
            condArray[cnt++] = serviceType;

            return condArray;
        }
    /*]]>*/
    </script>
</head>
<body>

    <div id="gadget_body">
	    <div id="customerChartDiv">
	        <object id="customerMiniChartEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="303">
	            <param name="movie" value="${ctx}/flexapp/swf/customerMiniChart.swf" />
	            <param name="wmode" value="opaque" />
	            <!--[if !IE]>-->
		        <object id="customerMiniChartOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/customerMiniChart.swf" width="100%" height="303">
		            <param name="wmode" value="opaque" />
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

</body>
</html>
