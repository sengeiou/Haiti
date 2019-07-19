<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", -1); //prevents caching at the proxy

%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
    <title>사용자 관리</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >    
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>	
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">
    
    var operatorId='';
    var loginId='';

	$.getJSON('${ctx}/common/getUserInfo.do',
	        function(json) {
	            if(json.operatorId != ""){
	            	operatorId = json.operatorId;
	            }
	        }
	);

    $(function(){
        if(operatorId == '' || operatorId == 'undefined'){
        	operatorId = $("#roleManage :hidden[name='operatorId']").val();
        }

        if(loginId == '' || loginId == 'undefined'){
            loginId = $("#roleManage :hidden[name='loginId']").val();
        }

        $("#operatorInfo").load("${ctx}/gadget/system/operator/detailPersonalOperator.do?operatorId=" + operatorId+"&loginId="+ encodeURIComponent(loginId));
   //     $('#operatorInfo a#operatorDelete').hide();
        operatorTabListener();
        
    });
    
    function openOperatorUpdateView() {
    	//operatorId = $("#roleManage :hidden[name='operatorId']").val();
		$('#operatorDetailForm').resetForm();
		$('#operatorInfo').load("${ctx}/gadget/system/operator/updatePersonalOperator.do?operatorId=" + this.operatorId + "&loginId="+ encodeURIComponent(this.loginId));
    }
    
    function resetOperatorInfo() {
        operatorId='';
        var innerHtml = "";
                                
        $('#operatorInfo').html(innerHtml);

        operatorTabListener();
    }
    
    function operatorTabListener() {
        $('#operatorInfo a#updatePersonalOperator').show();
        $('#operatorInfo a#cancelupdatePersonalOperator').show();
    }

    function operatorUpdateTabListener() {
        $('#operatorInfo a#updatePersonalOperator').show();
        $('#operatorInfo a#cancelupdatePersonalOperator').show();
    }   

    /*
    function getDetailOperator(_operatorId) {
       operatorId = _operatorId;
       $("#operatorInfo").load("${ctx}/gadget/system/operator/detailPersonalOperator.do?operatorId=" + operatorId);
    //   $('#operatorInfo a#operatorDelete').hide();
       operatorTabListener();
    }
    */

    function sendFlex(value){       
      return;
    }

    </script>
</head>
<body>
    <div id="gadget_body" class="width-640px" style="margin:0 auto">
        <form name="roleManage" id="roleManage">
	    <input type="hidden" name="supplierId" value=${supplierId} />
	    <input type="hidden" name="roleId" value=${roleId} />
	    <input type="hidden" name="operatorId" value=${operatorId} />
        <input type="hidden" name="loginId" value='${loginId}' />
	    </form>
		<div id="operatorInfo" class="margin-t40px"></div>
    </div>

</body>
</html>
