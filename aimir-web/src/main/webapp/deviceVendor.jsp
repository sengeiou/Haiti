<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hibernate</title>
    <script type="text/javascript" src="${ctx}/scripts/prototype.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/scriptaculous.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/stylesheetswitcher.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/global.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctx}/styles/deliciouslyblue/theme.css" title="default" />
    <link rel="alternate stylesheet" type="text/css" href="${ctx}/styles/deliciouslygreen/theme.css" title="green" />
	<style>
	body{overflow:hidden}
	</style>
<script>
var rooturl ="http://localhost:8080/${ctx}/";

//Get방식 Test
function doAjaxTestGet(eForm) {
	var url = rooturl + eForm.action.value;
	new Ajax.Request(url, {
		method:'get',
		parameters:{ id: eForm.id.value },
		onSuccess:
			function(transport){
				responseText = transport.responseText;
				$("result").innerHTML = responseText;
			},
		// 실패시 실행
		onFailure:
			function() {
				alert("ajax error");
			}
		}
	);
}
</script>
</head>
<body>
<div id="page">
    <div id="header">
        <h1>AiMiR <fmt:message key='aimir.version'/> SmartGrid System</h1>
    </div>
    <div id="content">
        <div id="nav">
            <div class="wrapper">
                <h2 class="accessibility">Navigation</h2>
                <ul class="clearfix">
                    <li><a href="${ctx}/" title="Home"><span>Home</span></a></li>
                    <li><a href="${ctx}/users.do" title="View Users"><span>Users</span></a></li>
                    <li><a href="${ctx}/device.do" title="View Users"><span>sample</span></a></li>
                    <li><a href="${ctx}/gadget" title="View Users"><span>gadget</span></a></li>
                </ul>
            </div>
        </div><!-- end nav -->

		<div id=restGet style="clear:both;"><b>REST Get</b>
			<form name=restGet>
				<input type=text name=id />
				<input type=hidden name=action value=deviceVendor.do />
				<input type=button value=Submit onclick=javascript:doAjaxTestGet(this.form); />
			</form>
		</div>
		<div id=result></div>
	
		<div id=restPost style="clear:both;"><b>REST Post</b>
			<form name=restPost action=deviceVendor.do method=post >
				ID <input type=text name=id style="width:50px;" />
				Name <input type=text name=name style="width:50px;" />
				Address <input type=text name=address />
				Description <input type=text name=descr />
				<input type=submit value=등록 />
			</form>
		</div>
	</div>
</div>
</body>
</html>