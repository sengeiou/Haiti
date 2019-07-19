<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    //String name = request.getParameter("name") == null ? "<fmt:message key='aimir.groupNmember.name'/>" : request.getParameter("name");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>부하 관리</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script language="JavaScript">/*<![CDATA[*/

    var flex;

    var supplierId     = "";
    var operatorId     = "";
    var selectedType   = "";
    var selectedId     = "";

    //var groupName = "<fmt:message key='aimir.groupNmember.name'/>";
    //var memberName = "<fmt:message key='aimir.membername'/>";
    var gadgetTitle = "그룹별 고객 목록";
    // 수정권한
    var editAuth = "${editAuth}";
    console.log("mini editAuth:", editAuth);

    function getEditAuth() {
        return editAuth;
    }

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != "" && json.operatorId != ""){
                    supplierId = json.supplierId;
                    operatorId = parseInt(json.operatorId);
                }
            }
    );

    /**
     * 플렉스 객체 참조
     */
    $(document).ready(function() {
        //hide();
        flex = getFlexObject('loadMgmtFlex');

        $('#groupType').selectbox();
//        $('#memberType').selectbox();
    });

    function getOperatorId() {
        return operatorId;
    }

    var flag;

    // ==== 액션스크립트에서 호출하는 함수 ====

    /**
    * fmt message
    */
    function getFmtMessage(){
        var cnt = 0;
        var fmtMessage = new Array();

        // 아래 메시지  xls 에 추가 필요
        fmtMessage[cnt++] = "<fmt:message key='aimir.dr.groupNmember.name' />"; // 그룹명(DR고객 멤버)
        fmtMessage[cnt++] = "<fmt:message key='aimir.dr.suppliablePower' />";   // 공급전력
        fmtMessage[cnt++] = "<fmt:message key='aimir.dr.thresholdPower' />";     // 임계치
        fmtMessage[cnt++] = "<fmt:message key='aimir.dr.currentDemand' />";     // 현재 수요
        fmtMessage[cnt++] = "<fmt:message key='aimir.dr.desc' />";              // 설명

        return fmtMessage;
    }

    function groupSearch() {
        if ( $('#groupName').val() == '' ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg15'/>");
            return;
        }
        var gtype;
        if($('#groupType').val() == ''){
            gtype = null;
        }else{
            gtype = $('#groupType option:selected').text();
        }
        flex.groupSearch($('#groupName').val(), gtype);
    }

    function delTxt(el){
        $('#'+el).val('');
    }

    function keyEvent(event, type) {
        var evKeyup = null;
        if(event)
            // firefox
            evKeyup = event;
        else
            // explorer
            evKeyup = window.event;

        if ( evKeyup.keyCode == 13 ) {
            var searchData;
            if( type == "group" ){
                if ( $('#groupName').val() == '' || !flag ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg15'/>");
                    return;
                } else {
                    var gtype;
                    if($('#groupType').val() == ''){
                        gtype = null;
                    }else{
                        gtype = $('#groupType option:selected').text();
                    }
                    flex.groupSearch($('#groupName').val(), gtype);
                }
            }
            else {
                flex2.requestSend();
            }

        }
    }

    /*]]>*/
</script>
</head>
<body>
<!-- 검색옵션 (S) -->
<div class="search-bg-basic">
<ul class="basic-ul">
    <li class="basic-li">
        <select id="groupType" name="select" style="width:120px;">
            <option value=""><fmt:message key="aimir.grouptype"/></option>
            <c:forEach var="groupType" items="${groupType}">
            <option value="${groupType.id}">${groupType.name}</option>
            </c:forEach>
        </select>
    </li>
    <li class="basic-li">
        <form name="searchform" method="post" onSubmit="return false;">
            <input type=hidden id="id"/>
            <div class="search-s1">
                <ul>
                    <li class="search-s1-input"><input id="groupName" type="text" value="<fmt:message key='aimir.groupNmember.name'/>" onclick="javascript:delTxt('groupName');" onkeydown="javascript:keyEvent(event, 'group');"></li>
                    <li class="search-s1-btn"><a href="javascript:groupSearch();" ></a></li>
                </ul>
            </div>
        </form>
</li></ul>
</div>
<!-- 검색옵션 (E) -->

<!-- 플렉스그리드 (S) -->
<div class="gadget_body">
    <object id="loadMgmtFlexEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%">
        <param name="movie" value="${ctx}/flexapp/swf/loadManagementMiniGrid.swf" />
        <param name="wmode" value="opaque">
        <!--[if !IE]>-->
        <object id="loadMgmtFlexOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/loadManagementMiniGrid.swf" width="100%" height="100%">
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

</body>
</html>