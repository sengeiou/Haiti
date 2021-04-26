<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/themes/css/print-common.css" rel="stylesheet" type="text/css">
<style type="text/css" media="print">
    @page {
        margin:0;
    }
    #receipt-form {
        padding: 0px;
        font-size: 10pt;
    }
    .contents-wrapper {
        margin-bottom: 20px;
    }
    .hidden {
        display: none;
    }
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jZebra/html2canvas.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jZebra/jquery.plugin.html2canvas.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jZebra/PluginDetect.js"></script>
</head>
<body>
    <applet name="jzebra" code="jzebra.PrintApplet.class"
    archive="${ctx}/lib/jzebra.jar" width="0" height="0"></applet>
    <div id="receipt-form">

            <div class="logo-wrapper" >
                <table border="1" width="200"><tr><td>
                    <table border="0" width="180">
                        <tr>
                            <td width="110">
                                Electricité d'Haï
                            </td>
                            <td rowspan=4 width="70">
                               <!--  <img class="logo" src="/aimir-web/images/ECG_logo.gif"/> -->
                                 <img class="logo" src="/aimir-web/images/HAITI_logo.jpg"/>
                            </td>
                        </tr>
                        <tr/><tr/><tr/>
                        <tr>
	                        <td>
	                            <fmt:message key='aimir.receipt.date'/>
	                        </td>
	                        <td>
	                            ${date}
	                        </td>
	                    </tr>
                        <tr>
                        	<td>
	                            <fmt:message key='aimir.cahierId'/>
	                        </td>
                            <td width="110">
                                ${casherId}
                            </td>
                        </tr>
                        <tr>
                        	<td>
	                            Total Sales
	                        </td>
                            <td width="110">
                                ${totalAmountPaidSum}
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
   <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var initCredit = Number(${initCredit});
        var eventHandler = {
            receiptPrint: function() {
           /*   document.jzebra.findPrinter();
                document.jzebra.findPrinter("SEWOO Lite #1");
                document.jzebra.setEncoding("UTF-8");
                document.jzebra.setEndOfDocument("\r\n");		*/

                //var logo = "<img src=\"" + window.location.origin + "/aimir-web/images/space.gif\" width=\"22\" height=\"60\">";
                var logo = "<img src=\"";
               // logo += window.location.origin + "/aimir-web/images/ECG_logo.gif\" width=\"60\" height=\"60\" />";
                  logo += window.location.origin + "/aimir-web/images/HAITI_logo.jpg\" width=\"75\" height=\"60\" />";
                var print = function() {
                    if(document.jzebra && document.jzebra.findPrinter) {
                        var html = "<html><div style='font-size:9pt;'>" + $("#receipt-form").html()
                            + "</div></html>";

                      //  if(html.indexOf('<img class="logo" src="/aimir-web/images/ECG_logo.gif">') > -1) {
                      //      html = html.replace('<img class="logo" src="/aimir-web/images/ECG_logo.gif">', logo);
                      
                        if(html.indexOf('<img class="logo" src="/aimir-web/images/HAITI_logo.jpg">') > -1) {
                          html = html.replace('<img class="logo" src="/aimir-web/images/HAITI_logo.jpg">', logo);
                        }
                        document.jzebra.appendHTML(html);
                        document.jzebra.printHTML();
                    } else {
                        window.print();
                    }
                }
                window.print();
            },
            close: function() {
                window.close();
            },
            insertInitCredit: function() {
                if(!isNaN(initCredit)) {

                    var $tr =
                        $("<tr><td><fmt:message key='aimir.prepayment.init.credit'/></td><td></td></tr>");
                    var $val = $("<td></td>");
                    $val.addClass("table-value");
                    $val.text(initCredit);
                    $tr.append($val);
                    $tr.insertAfter(".tr-credit");

                    var chargeAmount = Number($('.charge-amount').text());
                    chargeAmount += initCredit;
                    var chargeAmountFix = chargeAmount.toFixed(2);
                    $('.charge-amount').text(chargeAmountFix);
                }
            }
        };
        var bind = function () {
            $("span.print").click(eventHandler.receiptPrint);
            $("span.close").click(eventHandler.close);
        };
        var init = function () {
            //eventHandler.insertInitCredit();
            window.resizeTo(280, 280);
            bind();
        };
        window.onload = function() {
            init();
        };
        
        $(document).ready(function(){
            $(document).bind('keydown',function(e){
                if (e.keyCode == 123 /* F12 */) {
                    e.preventDefault();
                    //alert("F12 is not available.");
                    e.returnValue = false;
                }
                if (e.ctrlKey && e.shiftKey) { 
                    e.preventDefault(); 
                    //alert("Ctrl + Shift is not available.");
                    e.returnValue = false;
                }
            });
        });
        
        document.onmousedown=disableclick;
        function disableclick(event){
            if (event.button==2) {
                event.preventDefault(); 
                alert("Right click is not available.");
                return false;
            }
        }
    /*]]>*/
    </script>
</body>
</html>