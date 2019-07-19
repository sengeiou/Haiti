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
    div#receipt-form {
        font-size: 12pt;
        padding: 10px;
        width: 280px;
        height: 390px;
    }
    #receipt-form tr{
        height: 20px;
    }
    #receipt-form td{
        padding-left: 5px;
    }
    #receipt-form td.total-amount {
        text-align: center;
        font-weight: bold;
        padding-top: 10px;
        padding-bottom: 10px;
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
        padding: 0px;
        font-size: 12pt;
        font-weight: normal;
        height: 355px;
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
<body style="overflow: scroll">
    <applet name="jzebra" code="jzebra.PrintApplet.class"
        archive="${ctx}/lib/jzebra.jar" width="0" height="0"></applet>
    <canvas id="screenshot" style="display:none;"></canvas>

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
                        <tr>
                            <td width="110">${vendorName}</td>
                        </tr>
                        <tr>
                            <td width="110">${vendorLocation}</td>
                        </tr>
                        <tr>
                            <td width="110">
                                ${casherName}
                            </td>
                        </tr>
                    </table>
                </td></tr></table>
            </div>

            <div>
                <table>
                    <tr>
                        <td><fmt:message key='aimir.contract.receioptNo'/></td>
                        <td>SC-- ${logId}</td>
                    </tr>
                </table>
            </div>

            <div class="contents-wrapper">
                <table border="1" width="200"><tr><td>
                    <table border="0" cellspacing="0">
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
                                <fmt:message key='aimir.customer'/>
                            </td>
                            <td>
                                ${customer}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.accountNo'/>
                            </td>
                            <td>
                                ${customerNumber}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.meterid'/>
                            </td>
                            <td>
                                ${meter}
                            </td>
                        </tr>
<!--                        <tr>
                            <td>
                                <fmt:message key='aimir.code.g'/>
                            </td>
                            <td>
                                ${gCode}
                            </td>
                        </tr>   -->
                        <tr>
                            <td>
                                <fmt:message key='aimir.residental.activity'/>
                            </td>
                            <td>
                                ${activity}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.location.district'/>
                            </td>
                            <td>
                                ${distinct}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.payment'/>
                            </td>
                            <td>
                                ${payType}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.address'/>
                            </td>
                            <td>
                                ${address}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.date.last.charge.date'/>
                            </td>
                            <td>
                                ${daysFromCharge}
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
                                <fmt:message key='aimir.chargeAmount'/>
                            </td>
                            <td>
                                ${amount}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.prepayment.beforebalance'/>
                            </td>
                            <td>
                                ${preBalance}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key='aimir.prepayment.currentbalance'/>
                            </td>
                            <td>
                                ${currentBalance}
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
        var eventHandler = {
            receiptPrint: function() {
                document.jzebra.findPrinter();
                /*document.jzebra.findPrinter("SEWOO Lite #1");*/
                document.jzebra.setEncoding("UTF-8");
                document.jzebra.setEndOfDocument("\r\n");

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
            window.resizeTo(315, 530);
            bind();
        };
        window.onload = function() {
            init();
        };
    /*]]>*/
    </script>
</body>
</html>