<%@ include file="/taglibs.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html>
<title><fmt:message key="aimir.list.user"/></title>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <link rel="shortcut icon" href="${ctx}/images/favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/styles/deliciouslyblue/theme.css" title="default" />
    <link rel="alternate stylesheet" type="text/css" href="${ctx}/styles/deliciouslygreen/theme.css" title="green" />
    <script type="text/javascript" src="${ctx}/scripts/prototype.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/scriptaculous.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/stylesheetswitcher.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/global.js"></script>
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

<button onclick="location.href='userform.html'"style="float: right; margin-top: 30px; width: 100px">Add User</button>

<display:table name="userList" class="table" requestURI="" id="userList" export="true" pagesize="10">
    <display:setProperty name="export.pdf.filename" value="users.pdf"/>
    <display:column property="id" sortable="true" href="userform.do" media="html"
        paramId="id" paramProperty="id" titleKey="user.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="user.id"/>
    <display:column property="firstName" sortable="true" titleKey="user.firstName" escapeXml="true"/>
    <display:column property="lastName" sortable="true" titleKey="user.lastName" escapeXml="true"/>
    <display:column titleKey="user.birthday" sortable="true" sortProperty="birthday" escapeXml="true">
        <fmt:formatDate value="${userList.birthday}" pattern="${datePattern}"/>
    </display:column>
</display:table>

    </div><!-- end content -->
</div>

<script type="text/javascript">highlightTableRows("userList");</script>
</body>
</html>