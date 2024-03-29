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

        <div class="logo-wrapper">
            <table border='1' width='200'><tr><td>
                <table border='0'>
                    <tr>
                        <td>
                            Electricity Company Of Ghana
                        </td>
                        <td rowspan=4>
                            <img class="logo" src="${ctx}/images/ECG_logo.gif"/>
                        </td>
                    </tr>
                    <tr>
                        <td>${vendorName}</td>
                    </tr>
                    <tr>
                        <td>${vendorLocation}</td>
                    </tr>
                    <tr>
                        <td>
                            ${casherName}
                        </td>
                    </tr>
                </table>
            </td></tr></table>
        </div>

        <div>
            <table>
                <tr>
                    <td>
                        <fmt:message key='aimir.receipt'/>&nbsp;<fmt:message key='aimir.number'/>
                    </td>
                    <td>
                        SC-- ${logId}
                    </td>
                </tr>
            </table>
        </div>

        <div class="contents-wrapper">
            <table border='1' width='200'><tr><td>
                <table border='0' cellspacing="0">
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
                            <fmt:message key='aimir.accountNo'/><%-- <fmt:message key='aimir.customerid'/> --%>
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
                    <tr>
                        <td>
                            <fmt:message key='aimir.code.g'/>
                        </td>
                        <td>
                            ${gCode}
                        </td>
                    </tr>
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
                        <td colspan=2 class='td-table'>
                            <table border='1' class='inner-table' style="width: 100%;">
                                <tr>
                                    <td>
                                    </td>
                                    <td class='table-header'>
                                        <fmt:message key='aimir.prepayment.initialcredit'/>
                                    </td>
                                    <td class='table-header'>
                                        <fmt:message key='aimir.prepayment.currentcredit'/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key="aimir.meterid"/>
                                    </td>
                                    <td class='table-value'>
                                        ${lastMeter}
                                    </td>
                                    <td class='table-value'>
                                        ${meter}
                                    </td>
                                </tr>
                                <tr class='tr-credit'>
                                    <td>
                                        <fmt:message key='aimir.credit'/>
                                    </td>
                                    <td class='table-value'>
                                        ${preArrears}
                                    </td>
                                    <td class='table-value'>
                                        ${preBalance}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key='aimir.usageFee2'/>
                                    </td>
                                    <td class='table-value'>
                                        ${arrears}
                                    </td>
                                    <td class='table-value'>
                                        ${amount}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key='aimir.balance'/>
                                    </td>
                                    <td class='table-value'>
                                        ${currentArrears}
                                    </td>
                                    <td class='table-value'>
                                        ${currentBalance}
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="aimir.amount.paid"/>
                        </td>
                        <td>
                            ${totalAmount}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="aimir.prepayment.chargedarrears"/>
                        </td>
                        <td>
                            ${arrears}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="aimir.chargeAmount"/>
                        </td>
                        <td class="charge-amount">
                            ${amount}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="aimir.prepayment.beforebalance"/>
                        </td>
                        <td>
                            ${preBalance}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="aimir.prepayment.currentbalance"/>
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
        var initCredit = Number(${initCredit});
        var eventHandler = {
            receiptPrint: function() {
                document.jzebra.findPrinter();
                /*document.jzebra.findPrinter("SEWOO Lite #1");*/
                document.jzebra.setEncoding("UTF-8");
                document.jzebra.setEndOfDocument("\r\n");

                var logo = "<img src=\"";
                logo += window.location.origin + "${ctx}/images/ECG_logo.gif\" width=\"60\" height=\"60\" />";


                var print = function() {
                    if(document.jzebra && document.jzebra.findPrinter) {
                        var html = "<html><div style='font-size:9pt;'>" + $("#receipt-form").html()
                            + "</div></html>";

                        if(html.indexOf('<img class="logo" src="${ctx}/images/ECG_logo.gif">') > -1) {
                            html = html.replace('<img class="logo" src="${ctx}/images/ECG_logo.gif">', logo);
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
            window.resizeTo(375, 750);
            bind();
        };
        window.onload = function() {
            init();
        };
    /*]]>*/
    </script>
</body>
</html>