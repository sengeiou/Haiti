<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><fmt:message key="webapp.name"/></title>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <!-- <link rel="shortcut icon" href="${ctx}/images/favicon.ico" type="image/x-icon"/> -->
    <!--  Add favicon by eunmiae. 2014-12-16 -->
    <link rel="icon" type="image/png" href="${ctx}/images/favicon2.ico" />
    <link rel="stylesheet" type="text/css" href="${ctx}/styles/deliciouslyblue/theme.css" title="default" />
    <link rel="alternate stylesheet" type="text/css" href="${ctx}/styles/deliciouslygreen/theme.css" title="green" />
    <script type="text/javascript" src="${ctx}/scripts/prototype.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/scriptaculous.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/stylesheetswitcher.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/global.js"></script>
</head>
<body>
<div id="page">

    <div id="header">
        <h1>AiMiR <fmt:message key='aimir.version'/> SmartGrid System</h1>
        <p><fmt:message key="webapp.tagline"/></p>
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

        <div id="main">
        	<button class="button" onclick="location.href='${ctx}/users.html'">View Demonstration</button>
        </div>


    </div><!-- end content -->

    <div id="footer"></div>
</div>
</body>
</html>