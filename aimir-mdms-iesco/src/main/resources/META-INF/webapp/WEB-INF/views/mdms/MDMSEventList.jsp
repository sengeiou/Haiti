<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MDMS-HES Integration</title>
	<link rel="stylesheet" type="text/css" href="/js/extjs/resources/css/ext-all.css"/>
	<link rel="stylesheet" href="/css/pure-min.css">
	<link rel="stylesheet" href="/css/grids-responsive-min.css">
	<link rel="stylesheet" href="/css/soap.css">	
	<link rel="stylesheet" href="/css/icon.css">
	<script type="text/javascript" src="/js/jquery-1.11.1.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="/js/jquery.json-2.4.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="/js/ui/jquery.ui.core.js"></script>
	<script type="text/javascript" src="/js/extjs/ext-all.js"></script>
	<script type="text/javascript" src="/js/service/MDMSEventList.js"></script>	
</head>
<body>
<div id="layout" class="pure-g" >
    <div class="sidebar pure-u-1 pure-u-md-1-4" >
        <div class="header">
            <h1 class="brand-title">MDMS Integration</h1>    
            <%@ include file="/common/mdmsMenu.jsp"%>
        </div>
    </div>
	<div class="content pure-u-1 pure-u-md-3-4">
        <div>            
            <div class="posts">
            	<div style="font-size:15px;">MDMS EVENT List</div>
               	<div id="idContent"></div>                
			</div>
        </div>
    </div>
</div>

</body>

</html>