<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<style type="text/css">
	span {
		float:none;
	}
	div.logo-wrapper,
	div.contents-wrapper {
		border: 1px #000 solid;
	}
	#receipt-form {
		padding: 10px;
		width: 280px;
		height: 325px;		
	}
	#receipt-form tr{
		height: 20px;
	}
	#receipt-form td {
		padding-left: 5px;
	}
    img.logo {
        width: 100px;
        height: 85px;
        float: right;
    }
</style>
<style type="text/css" media="print">
	@page {
		margin: none;
	}	
	#receipt-form {
		font-size: 9pt;
	}	
	.hidden {
		display: none;
	}
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
	var eventHandler = {
		receiptPrint: function() {
			document.jzebra.findPrinter();
			/*document.jzebra.findPrinter("SEWOO Lite #1");*/
			document.jzebra.setEncoding("UTF-8");
			document.jzebra.setEndOfDocument("\r\n");

			var logo = "<img src=\"";
			logo += window.location.origin + "${ctx}/images/MOE_logo.jpg\" width=\"75\" height=\"60\" float=\"right\"  />";

			var print = function() {
				if(document.jzebra && document.jzebra.findPrinter) {
					var html = "<html><div style='font-size:9pt;'>" + $("#receipt-form").html() 
						+ "</div></html>";
					
					if(html.indexOf('<img class="logo" src="${ctx}/images/MOE_logo.jpg">') > -1) {					
						html = html.replace('<img class="logo" src="${ctx}/images/MOE_logo.jpg">', logo);
					}
					document.jzebra.appendHTML(html);
					document.jzebra.printHTML();
				} else {
					window.print();
				}
			}
			print();
		},

		close: function() {
			window.close();
		}
	};
	var bind = function () {
		$("span.print").click(eventHandler.receiptPrint);
		$("span.close").click(eventHandler.close);
	};
	var init = function () {
		window.resizeTo(320, 450);
		bind();
	};
	window.onload = function() {
		init();
	};
/*]]>*/
</script>	
</head>
<body>
	<applet name="jzebra" code="jzebra.PrintApplet.class" 
		archive="${ctx}/lib/jzebra.jar" width="0" height="0"></applet>	
	<div id="receipt-form">
		<div class="logo-wrapper">
			<table border="1" width="200"><tr><td>
			<table>
				<tr>
					<td>
						Electricity Company Of Iraq
					</td>
					<td rowspan=4>
						<img class="logo" src="${ctx}/images/MOE_logo.jpg"/>
					</td>
				</tr>
				<tr>
					<td>${name}</td>
				</tr>
				<tr>
					<td>${location}</td>
				</tr>
				<tr>
					<td>
						ORIGINAL
					</td>
				</tr>
			</table>
			</td></tr></table>
		</div>
		<div>
			<span>
				Dreggh-Comm 4 V.S.
			</span>
		</div>
		<div class="contents-wrapper">
			<table border="1" width="200"><tr><td>
			<table>
				<tr>
					<td>
						<fmt:message key='aimir.date'/>
					</td>
					<td>
						${date}
					</td>
				</tr>
				<tr>
					<td>
						<!-- <fmt:message key='aimir.receipt'/>&nbsp;<fmt:message key='aimir.number'/> -->
						<fmt:message key='aimir.contract.receioptNo'/>
					</td>
					<td>
						${receiptNo}
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.amount.paid'/>
					</td>
					<td>
						${amount}
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.prepayment.commission'/>
					</td>
					<td>
						${commission} %
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.prepayment.commission'/>&nbsp;
						<fmt:message key='aimir.value'/>
					</td>
					<td>
						${commissionValue}
					</td>
				</tr>				
				<tr>
					<td>
						<fmt:message key='aimir.total.amount'/>&nbsp;
					</td>
					<td>
						${value}
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.tax'/>&nbsp;<fmt:message key='aimir.customer.usage.rate'/>
					</td>
					<td>
						${taxRate} %
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.tax'/>
					</td>
					<td>
						${tax}
					</td>
				</tr>
				<tr>
					<td>
						<fmt:message key='aimir.netvalue'/>
					</td>
					<td>
						${netValue}
					</td>
				</tr>
			</table>
			</td></tr></table>
		</div>
	</div>
	<div class="control-form hidden">
    <center>
    <span class="am_button margin-l10 margin-t1px print">
        <a class="on"><fmt:message key="aimir.button.print" /></a>
    </span>
    <span class="am_button margin-l10 margin-t1px close">
        <a class="on"><fmt:message key="aimir.board.close" /></a>
    </span>
    </center>
	</div>
</body>	
</html>