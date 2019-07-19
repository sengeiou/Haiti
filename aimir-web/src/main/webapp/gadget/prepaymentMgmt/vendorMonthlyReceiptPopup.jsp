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
        /* height: 580px; */
        <c:choose>
            <c:when test="${hasArrears == 'true'}">
            /* height: 740px; */
            height: 760px;
            </c:when>
            <c:otherwise>
            /* height: 620px; */
            height: 640px;
            </c:otherwise>
        </c:choose>
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
        width: 85px;
        height: 85px;
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
        height: 450px;
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
            <table border="1" width="200">
                <tr><td>
                <table border="0">
                    <tr>
                        <td>
                            <b>Electricity Company Of Ghana</b>
                        </td>
                        <td rowspan=4>
                            <img class="logo" src="${ctx}/images/ECG_logo.gif"/>
                        </td>
                    </tr>
                    <tr>
                        <td><b>${vendorName}</b></td>
                    </tr>
                    <tr>
                        <td><b>${vendorLocation}</b></td>
                    </tr>
                    <tr>
                        <td>
                            <b>${casherName}</b>
                        </td>
                    </tr>
                </table>
            </td></tr></table>
        </div>

        <div>
            <table>
                <tr>
                    <td><b>Receipt No.</b></td>
                    <td><b>SC-- ${logId}</b></td>
                </tr>
            </table>
        </div>

        <div class="contents-wrapper">
            <table border="1" width="200">
                <tr><td>
                <table border="0" cellspacing="0">
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.date'/></b>
                        </td>
                        <td colspan="2">
                            ${date}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.customer'/></b>
                        </td>
                        <td colspan="2">
                            ${customer}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.accountNo'/></b>
                        </td>
                        <td colspan="2">
                            ${customerNumber}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.meterid'/></b>
                        </td>
                        <td colspan="2">
                            ${meter}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.code.g'/></b>
                        </td>
                        <td colspan="2">
                            ${gCode}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.residental.activity'/></b>
                        </td>
                        <td colspan="2">
                            ${activity}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.location.district'/></b>
                        </td>
                        <td colspan="2">
                            ${distinct}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.payment'/></b>
                        </td>
                        <td colspan="2">
                            ${payType}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b><fmt:message key='aimir.address'/></b>
                        </td>
                        <td colspan="2">
                            ${address}
                        </td>
                    </tr>
                    <tr><td colspan=3 style="padding-right: 1px;">
                        <table border="1" style="table-layout: fixed;">
                            <tr>
                                <td style="width: 120px;"><b>Fee</b></td>
                                <td><b>Value</b></td>
                                <td><b>Date</b></td>
                            </tr>
                            <tr>
                                <td><fmt:message key='aimir.serviceCharge'/></td>
                                <td>${serviceCharge}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr>
                                <td><fmt:message key='aimir.prepayment.govLevy'/></td>
                                <td>${govLevy}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr>
                                <td><fmt:message key='aimir.prepayment.publicLevy'/></td>
                                <td>${publicLevy}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr class="non-residential">
                                <td><fmt:message key='aimir.prepayment.vat'/></td>
                                <td>${vat}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr>
                                <td>Additional<br/>Subsidy<%-- <fmt:message key='aimir.prepayment.additionalSubsidy'/> --%></td>
                                <td>${additionalSubsidy}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr class="residential">
                                <td>Government<br/>Subsidy<%-- <fmt:message key='aimir.prepayment.govSubsidy'/> --%></td>
                                <td>${govSubsidy}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>
                            <tr class="residential">
                                <td><fmt:message key='aimir.prepayment.lifeLineSubsidy'/></td>
                                <td>${lifeLineSubsidy}</td>
                                <td>${dateByYyyymmdd}</td>
                            </tr>

                        </table>
                    </td></tr>

                    <tr>
                        <td colspan="2">
                            <b>Total Fees</b> <%-- <fmt:message key='aimir.prepayment.vat'/> --%>
                        </td>
                        <td>
                            <b>${totalFees}</b>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <b><fmt:message key='aimir.prepayment.prepaidamount'/><!-- Prepaid Amount --></b>
                        </td>
                        <td>
                            <b>${monthlyPaidAmount}</b>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <b><fmt:message key='aimir.prepayment.monthlycharge'/></b>
                        </td>
                        <td>
                            <b>${chargeValue}</b>
                        </td>
                    </tr>

                    <c:choose>
                        <c:when test="${hasArrears == 'true'}">

                        <tr><td colspan=3 style="padding-right: 2px">
                            <table border="1">
                                <tr>
                                    <td>
                                    </td>
                                    <td>
                                        <fmt:message key='aimir.prepayment.initialcredit'/>
                                    </td>
                                    <td>
                                        <fmt:message key='aimir.prepayment.currentcredit'/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Meter<br/>ID<%-- <fmt:message key="aimir.meterid"/> --%>
                                    </td>
                                    <td>
                                        ${lastMeter}
                                    </td>
                                    <td>
                                        ${meter}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key='aimir.credit'/>
                                    </td>
                                    <td>
                                        ${preArrears}
                                    </td>
                                    <td>
                                        ${preBalance}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key='aimir.usageFee2'/>
                                    </td>
                                    <td>
                                        ${arrears}
                                    </td>
                                    <td>
                                        ${amount}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <fmt:message key='aimir.balance'/>
                                    </td>
                                    <td>
                                        ${currentArrears}
                                    </td>
                                    <td>
                                        ${currentBalance}
                                    </td>
                                </tr>
                            </table>
                        </td></tr>

                        <tr>
                            <td colspan="2">
                                <b><fmt:message key="aimir.amount.paid"/></b>
                            </td>
                            <td>
                                <b>${totalAmount}</b>
                            </td>
                        </tr>

                        <tr>
                            <td colspan="2">
                                <b><fmt:message key="aimir.prepayment.chargedarrears"/></b>
                            </td>
                            <td>
                                <b>${arrears}</b>
                            </td>
                        </tr>

                        </c:when>
                        <c:otherwise>

                        <tr>
                            <td colspan="2">
                                <b><fmt:message key="aimir.amount.paid"/></b>
                            </td>
                            <td>
                                <b>${amount}</b>
                            </td>
                        </tr>

                        </c:otherwise>
                    </c:choose>

                    <tr>
                        <td colspan="2">
                            <b><fmt:message key="aimir.chargeAmount"/></b>
                        </td>
                        <td>
                            <b>${amount}</b>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2">
                            <b><fmt:message key='aimir.prepayment.beforebalance'/></b>
                        </td>
                        <td>
                            <b>${preBalance}</b>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2">
                            <b><fmt:message key='aimir.prepayment.currentbalance'/></b>
                        </td>
                        <td>
                            <b>${currentBalance}</b>
                        </td>
                    </tr>

                </table>
            </td></tr></table>
        </div>
        <div>
            <table>
                <tr align="center">
                    <td><b>Clear Debt on Credit Meter <br/>&amp; Avoid Blockage</b></td>
                </tr>
            </table>
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
        var tariffName = {
            Residential: "Residential",
            NonResidential: "Non Residential"
        };

        var eventHandler = {
            receiptPrint: function() {
                document.jzebra.findPrinter();
                /*document.jzebra.findPrinter("SEWOO Lite #1");*/
                document.jzebra.setEncoding("UTF-8");
                document.jzebra.setEndOfDocument("\r\n");

                var logo = "<img src=\"";
                logo += window.location.origin
                    + "${ctx}/images/ECG_logo.gif\" width=\"60\" height=\"60\" />";

                var print = function() {
                    if (document.jzebra && document.jzebra.findPrinter) {
                        var html = "<html><body><div style='font-size:9pt;'>" + $("#receipt-form").html()
                            + "</div></body></html>";

                        if (html.indexOf('<img class="logo" src="${ctx}/images/ECG_logo.gif">') > -1) {
                            html = html.replace('<img class="logo" src="${ctx}/images/ECG_logo.gif">', logo);
                        }
                        document.jzebra.appendHTML(html);
                        document.jzebra.printHTML();
                    } else {
                        window.print();
                    }
                };
                print();
            },

            close: function() {
                window.close();
            }
        };

        // 영수증 폼에 사실 Residential 항목과 NonResidential 항목이 포함되 있으므로,
        // tariff에 따라 관련 항목들만 나타나게 한다.
        var initElement = function() {
            var tariff = "${activity}";
            if (tariff == tariffName.Residential) {
                $("tr.non-residential").remove();
            } else if (tariff == tariffName.NonResidential) {
                $("tr.residential").remove();
            }
        };

        var bind = function () {
            $("span.print").click(eventHandler.receiptPrint);
            $("span.close").click(eventHandler.close);
        };
        var init = function () {
            initElement();

            <c:choose>
                <c:when test="${hasArrears == 'true'}">
                //window.resizeTo(320, 850);
                window.resizeTo(320, 870);
                </c:when>
                <c:otherwise>
                //window.resizeTo(320, 730);
                window.resizeTo(320, 750);
                </c:otherwise>
            </c:choose>
            bind();
        };
        window.onload = function() {
            init();
        };
    /*]]>*/
    </script>
</body>
</html>