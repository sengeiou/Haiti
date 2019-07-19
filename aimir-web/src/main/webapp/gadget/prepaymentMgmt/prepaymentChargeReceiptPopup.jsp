<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <!-- <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" /> -->
    <style type="text/css">
        body {
            margin: 0px;
            padding: 0px;
            font-family: Arial, Tahoma, Helvetica;
            font-size: 12px;
        }
        textarea     {
            margin: 0px;
            padding: 0px;
            font-family: Arial, Tahoma, Helvetica;
            font-size: 12px;
            /* overflow: auto; */
            width: 100%;
            height: 100%;
            /* white-space: pre-wrap; */
            /* word-berak: break-all; */
            /* word-wrap: break-word; */
            border:0 solid;
            background-color:transparent;
            /* max-width: 100px;
            max-height: 100px; */
            resize: none;
        }
        .testMainBorder {
            border-style: solid;
            border-width: 1px;
            /* border-color: blue; */
            border-color: transparent;
        }
        .testDivBorder {
            border-style: solid;
            border-width: 1px;
            /* border-color: orange; */
            border-color: transparent;
        }
        .defaultLabel {
            font-weight: bold;
            color: #7FA5C8;
        }
        .defaultLine {
            border-style: solid;
            border-width: 2px;
            border-color: #7FA5C8;
        }
        .defaultBelowLine {
            border-style: solid;
            border-width: 0px 2px 2px;
            border-color: #7FA5C8;
        }
        #listTbl td, #btmTbl td {
            border:2px #7FA5C8 solid;
        }
        .fillColor {
            background-color: #AEE0F9;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/


    /*]]>*/
    </script>
</head>
<body>

<!-- <div class="testMainBorder" style="width: 715px; height: 720px; margin:10px;"> -->
<div class="testMainBorder" style="width: 715px; height: 660px; margin:10px;">

    <!-- Logo -->
    <div class="testDivBorder" style="width: 90px; height: 100px;"></div>

    <!-- Title -->
    <div class="testDivBorder" style="width: 430px; height: 40px; position: absolute; top: 10px; left: 100px; padding: 8px 0px 0px 0px; color: #7FA5C8;">
        <span style="margin: 0px; padding: 0px; font-weight: 900; font-size: 18px;">ELECTRICITY COMPANY OF GHANA</span><br/>
        <span style="margin: 0px; padding: 0px; font-size: 10px;">V.A.T. REG. NO. 714 V 000395</span>
    </div>
    <!-- Electricity Company -->
    <div class="testDivBorder defaultLabel" style="width: 175px; height: 30px; position: absolute; top: 10px; left: 545px; padding: 1px 0px 0px 0px;
         text-align: center;">
        ELECTRICITY COMPANY<br/>OF GHANA
    </div>

    <!-- Name Address -->
    <div class="testDivBorder defaultLabel" style="width: 90px; height: 50px; position: absolute; top: 115px; left: 18px;">
        SERVICE CENT.<br/>NAME/ADDRESS
    </div>

    <!-- Name Address Value -->
    <div class="testDivBorder" style="width: 250px; height: 140px; position: absolute; top: 65px; left: 115px;">
        <textarea>District Code : 10
CALL : 0302-611611
HALF ASSINI
EZUAH AKROMAH J
E C G NO 100
TAKYINTA
        </textarea>
    </div>

    <!-- Account NO. Left ........  left: 400px; -->
    <div class="testDivBorder" style="width: 175px; height: 165px; position: absolute; top: 40px; left: 365px; padding-right: 5px;">

        <div class="defaultLabel defaultLine" style="width: 160px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ACCOUNT NO.
        </div>
        <div class="defaultBelowLine" style="width: 160px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ${accountId}<!-- 707-8132-001 8 -->
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            METER NO.
        </div>
        <div class="defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ${mdsId}<!-- 0241572234 -->
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            BILL DATE
        </div>
        <div class="defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            29/11/12
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 100px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            MONTH
        </div>
        <div class="defaultBelowLine" style="width: 100px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            NOV 2012
        </div>
    
    </div>

    <!-- Account NO. Right -->
    <div class="testDivBorder" style="width: 175px; height: 165px; position: absolute; top: 40px; left: 545px; padding-right: 5px;">

        <div class="defaultLabel defaultLine" style="width: 160px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ACCOUNT NO.
        </div>
        <div class="defaultBelowLine" style="width: 160px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ${accountId}<!-- 707-8132-001 8 -->
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            METER NO.
        </div>
        <div class="defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            ${mdsId}<!-- 0241572234 -->
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            BILL DATE
        </div>
        <div class="defaultBelowLine" style="width: 130px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            29/11/12
        </div>

        <div class="defaultLabel defaultBelowLine" style="width: 100px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            MONTH
        </div>
        <div class="defaultBelowLine" style="width: 100px; height: 15px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            NOV 2012
        </div>

    </div>

    <!-- GEO ID -->
    <div class="testDivBorder" style="width: 700px; height: 20px; position: absolute; top: 206px; left: 12px;">
        <!-- GEO. ID : 07-10-570-090-080-5600 RESIDENTIAL -->
        GEO. ID : ${contractNumber} RESIDENTIAL
    </div>


    <!-- TABLE -->
    <!-- <div class="testDivBorder" style="width: 532px; height: 480px; position: absolute; top: 227px; left: 12px;"> -->
    <div class="testDivBorder" style="width: 532px; height: 421px; position: absolute; top: 227px; left: 12px;">
        <!-- <table id="listTbl" cellpadding="0" cellspacing="0" border="2" style="width: 532px; height: 440px; border-collapse: collapse;"> -->
        <table id="listTbl" cellpadding="0" cellspacing="0" border="2" style="width: 532px; height: 380px; border-collapse: collapse;">
            <tr>
                <td colspan="2" class="defaultLabel" style="height: 8px; text-align: center; font-size: 10px">READING</td>
                <td rowspan="2" class="defaultLabel" style="width: 190px; text-align: center;">
                    <span style="float:left; margin-left: 25px;">UNIT/RATE</span>
                    <span style="float:right; margin-right: 25px;">DETAILS</span>
                </td>
                <td rowspan="2" class="defaultLabel" style="text-align: center;">AMOUNT</td>
            </tr>
            <tr>
                <td class="defaultLabel" style="width: 80px; height: 8px; text-align: center; font-size: 10px">PRESENT</td>
                <td class="defaultLabel" style="width: 80px; height: 8px; text-align: center; font-size: 10px">PREVIOUS</td>
            </tr>
            <tr>
                <td class="fillColor"></td>
                <td class="fillColor"></td>
                <td class="fillColor"></td>
                <td class="fillColor"></td>
            </tr>
        </table>


        <!-- Table Data -->
        <!-- TOU -->
        <div class="testDivBorder" style="width: 76px; height: 16px; position: absolute; top: 32px; left: 3px;">
            3661
        </div>
        <div class="testDivBorder" style="width: 76px; height: 16px; position: absolute; top: 32px; left: 85px;">
            3630
        </div>
        <div class="testDivBorder" style="width: 92px; height: 16px; position: absolute; top: 32px; left: 167px; text-align: center;">
            31
        </div>

        <!-- CHARGES -->
        <div class="testDivBorder" style="width: 76px; height: 16px; position: absolute; top: 58px; left: 85px;">
            CHARGES
        </div>
        <div class="testDivBorder" style="width: 92px; height: 16px; position: absolute; top: 58px; left: 167px; text-align: center;">
            31
        </div>
        <div class="testDivBorder" style="width: 92px; height: 16px; position: absolute; top: 58px; left: 261px; text-align: right;">
            @ 0.095
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 58px; left: 357px; text-align: right;">
            2.95
        </div>

        <!-- SERV CHG -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 75px; left: 167px;">
            SERV CHG
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 75px; left: 357px; text-align: right;">
            1.00
        </div>

        <!-- GV S LEVY -->
        <div class="testDivBorder" style="width: 92px; height: 16px; position: absolute; top: 92px; left: 167px;">
            GV S LEVY
        </div>
        <div class="testDivBorder" style="width: 92px; height: 16px; position: absolute; top: 92px; left: 261px; text-align: right;">
            @0.00002
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 92px; left: 357px; text-align: right;">
            0.01
        </div>

        <!-- TOTAL MONTH -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 109px; left: 167px;">
            TOTAL THIS MONTH
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 109px; left: 357px; text-align: right;">
            3.96
        </div>

        <!-- GOVT SUBSIDY -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 126px; left: 167px;">
            GOVT SUBSIDY
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 126px; left: 357px; text-align: right;">
            -1.12
        </div>

        <!-- NET CHARGE -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 143px; left: 167px;">
            NET CHARGE
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 143px; left: 357px; text-align: right;">
            2.84
        </div>

        <!-- BALANCE B/F -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 160px; left: 167px;">
            BALANCE B/F
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 160px; left: 357px; text-align: right;">
            12.33
        </div>

        <!-- TOTAL PAYMENT -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 177px; left: 167px;">
            TOTAL PAYMENT
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 177px; left: 357px; text-align: right;">
            -3.00
        </div>

        <!-- TOTAL AMOUNT -->
        <div class="testDivBorder" style="width: 188px; height: 16px; position: absolute; top: 194px; left: 167px;">
            TOTAL AMOUNT
        </div>
        <div class="testDivBorder" style="width: 168px; height: 16px; position: absolute; top: 194px; left: 357px; text-align: right;">
            12.17
        </div>

        
        <!-- BOTTOM TABLE -->
        <table id="btmTbl" cellpadding="0" cellspacing="0" border="2" style="border-top-width: 0px; width: 532px; height: 40px; border-collapse: collapse;">
            <tr>
                <td class="defaultLabel" style="border-top-width: 0px; width: 110px; height: 8px; text-align: center; font-size: 10px">DATE OF READING</td>
                <td class="defaultLabel" style="border-top-width: 0px; width: 110px; height: 8px; text-align: center; font-size: 10px">LAST PAY DATE</td>
                <td class="defaultLabel" style="border-top-width: 0px; width: 110px; height: 8px; text-align: center; font-size: 10px">PLEASE PAY BY</td>
                <td class="defaultLabel" style="border-top-width: 0px; height: 8px; text-align: center; font-size: 10px">TOTAL</td>
            </tr>
            <tr>
                <td style="height: 12px; text-align: left; padding-left: 5px;">04/11/12</td>
                <td style="height: 12px; text-align: left; padding-left: 5px;">01/11/12</td>
                <td style="height: 12px; text-align: left; padding-left: 5px;">AT ONCE</td>
                <td class="fillColor" style="height: 12px; text-align: right; padding-right: 10px;">12.17</td>
            </tr>
        </table>
    
    </div>


    <!-- CUSTOMER INFORMATION -->
    <!-- <div class="testDivBorder" style="width: 175px; height: 480px; position: absolute; top: 228px; left: 545px; padding-right: 5px;"> -->
    <div class="testDivBorder" style="width: 175px; height: 421px; position: absolute; top: 227px; left: 545px; padding-right: 5px;">

        <!-- CUSTOMER INFORMATION PLEASE -->
        <div class="defaultLabel defaultLine" style="width: 160px; height: 60px; float: right; padding: 3px 0px 0px 0px; text-align: center;">

            <div style="text-decoration: underline; margin-top: 10px;">CUSTOMER INFORMATION</div>
            
            <div style="margin-top: 10px;">PLEASE SEE OVER</div>

        </div>


        <!-- CUSTOMER INFORMATION DETAIL 1 -->
        <div class="testDivBorder" style="width: 165px; height: 100px; float: right;">
            GEO.ID: ${contractNumber}<!-- GEO.ID: 07-10-570-080-5600 -->
        </div>

        <!-- CUSTOMER INFORMATION DETAIL 2 -->
        <!-- <div class="testDivBorder" style="width: 165px; height: 100px; float: right;"> -->
        <div class="testDivBorder" style="width: 165px; height: 80px; float: right;">
            Dist. Code: 10
        </div>

        <!-- CUSTOMER INFORMATION DETAIL 3 -->
        <!-- <div class="testDivBorder" style="width: 165px; height: 94px; float: right; font-size: 14px;"> -->
        <div class="testDivBorder" style="width: 165px; height: 53px; float: right; font-size: 14px;">
            ${accountId}<!-- 707-8132-001 8 -->
        </div>


        <!-- THIS BILL -->
        <div class="testDivBorder" style="width: 160px; height: 44px; float: right; text-align: center; font-weight: bold; color: #7FA5C8;">
            THIS BILL<br/>MUST ACCOMPANY<br/>REMITTANCE
        </div>

        <!-- TOTAL -->
        <div class="defaultLabel defaultLine" style="width: 160px; height: 16px; float: right; padding: 3px 0px 0px 0px; text-align: center;">
            <div style="margin-top: 1px;">TOTAL</div>
        </div>

        <!-- DOWN ARROW -->
        <div class="testDivBorder" style="width: 160px; height: 18px; float: right;">
            <img src="receipt_downarrow.svg" style="margin-left: 5px;">
        </div>


        <!-- TOTAL VALUE -->
        <div class="defaultLine fillColor" style="width: 160px; height: 16px; float: right; padding: 3px 0px 0px 0px; text-align: right;">
            <span style="margin-right: 10px;">12.17</span>
        </div>

    </div>


    <!-- DISCONNECT -->
    <!-- <div class="testDivBorder" style="width: 700px; height: 20px; position: absolute; top: 709px; left: 12px;"> -->
    <div class="testDivBorder" style="width: 700px; height: 20px; position: absolute; top: 649px; left: 12px;">
        DISCONNECT 28DAY AFTER DELIVERY
    </div>


</div>



</body>
</html>