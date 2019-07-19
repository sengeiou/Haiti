<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/css/jquery.cluetip.css" rel="stylesheet" type="text/css" />
    
   <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.jsp"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

	<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
	</style>
</head>
<body>
<script type="text/javascript" charset="utf-8">

	$(document).ready(function () {
		
		var result = false;
		// data
	    var obj = window.opener.obj;
		id = obj.gadgetId;
		name = obj.gadgetName;
		descr = obj.gadgetDescr;
		fullHeight = obj.gadgetFullHeight;
		miniHeight = obj.gadgetMiniHeight;
		
		$('#id').val(id);
		$('#gadgetName').val(name);
		$('#fullHeight').val(fullHeight);
		$('#minHeight').val(miniHeight);
		$('#gadgetDescr').val(descr);
	})
	
	function updateGadget(){
		 //$("#gadgetInfoEdit").load("${ctx}/gadget/system/updateGadget.do");
		var options = {
                success : gadgetUpdateResult,
                url : '${ctx}/gadget/system/updateGadget.do',
                type : 'post',
                datatype : 'json'
            };
            $('#gadgetInfoEditForm').ajaxSubmit(options);
	}
	
	function cancelGadget(){
		window.close();
		result=false;
		window.opener.getReturnValue(result);
	}

	function gadgetUpdateResult(responseText, status) {
        alert(responseText.result);
        window.close();
        if (responseText.errors && responseText.errors.errorCount > 0) {
            var i, fieldErrors = responseText.errors.fieldErrors;
            for (i=0 ; i < fieldErrors.length; i++) {
                var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                $(temp).val(''+fieldErrors[i].defaultMessage);
            }
        } else {
            $('#gadgetInfoEditForm').resetForm();
        }
        result=true;
        window.opener.getReturnValue(result);
    }
</script>
<div id="wrapper" class="max" style="margin-left: 50px;margin-top: 20px" >
	<div id="gadgetInfoEdit" style="display: block;" >
	 	<form:form id="gadgetInfoEditForm" modelAttribute="gadget">
			<span class="padding-r10px"><b>Name</b></span><input type="text" id="gadgetName" name="name" ><input type="hidden" id="id" name='id' /></li><br><br>
			<span class="padding-r10px"><b>Descr</b></span><textarea id="gadgetDescr" style="width: 250px;" name="descr"></textarea><br><br>
			<span class="padding-r10px"><b>Min.Height</b></span><input type="number" id="minHeight" name="miniHeight" style="width: 43px" max="1548" value="1">
		    <span class="padding-r10px" style="margin-left:10px;"><b>Full.Height</b></span><input type="number" id="fullHeight" name="fullHeight" style="width: 43px;" max="1548" value="1"><br><br>
		    <ul id="modeBtn" style="margin-left: 210px">
				<li ><a href="javascript:updateGadget()" class='btn_blue'><span>Update</span></a></li>
				<li style="margin-left:10px;"><a href="javascript:cancelGadget()" class='btn_blue'><span>Cancel</span></a></li>
			</ul>
		</form:form>
	</div>
<!-- 
	<div id="gadgetInfoView" style="display: block; border:1; ">
		<span>Name</span><input type="text" id="Name" readonly="readonly" title="name" ><br><br>
		<span>Descr</span><textarea style="width: 250px;" readonly="readonly" ></textarea><br><br>
		<span>Min.Height</span><input type="number" id="MinHeight" style="width: 40px" max="1548" value="1" readonly="readonly" >
		<span>Full.Height</span><input type="number" id="FullHeight" style="width: 40px" max="1548" value="1" readonly="readonly" ><br><br>
	   <a href="javascript:modifyGadget('modify')"><input type="button" id="modifyBtn" value="Modify" style="display: block;"></a>
	   <a href="javascript:modifyGadget('update')"><input type="button" id="updateBtn" value="Update" style="display: none;"></a>
	   <a href="javascript:modifyGadget('cancel')"><input type="button" id="cancelBtn" value="Cancel" style="display: none;"></a>
	</div>		
 -->
</div>
</body>
</html>