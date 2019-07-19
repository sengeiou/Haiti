<!-- 문서 변환  https://word2cleanhtml.com/ -->
<!-- AIMIR 4.0 User Menual Appendix A CoAP Browser Command List 내용을 변환했습니다. -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style>
::selection {
  background: #a8d1ff; 
}
</style>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">


</head>
<body>

<script language="JavaScript"> 

var TRange=null

function findString (str) {
 if (parseInt(navigator.appVersion)<4) return;
 var strFound;
 if (navigator.appName=="Netscape") {

  // NAVIGATOR-SPECIFIC CODE

  strFound=self.find(str);
  if (!strFound) {
   strFound=self.find(str,0,1)
   while (self.find(str,0,1)) continue
  }
 }
 if (navigator.appName.indexOf("Microsoft")!=-1) {

  // EXPLORER-SPECIFIC CODE

  if (TRange!=null) {
   TRange.collapse(false)
   strFound=TRange.findText(str)
   if (strFound) TRange.select()
  }
  if (TRange==null || strFound==0) {
   TRange=self.document.body.createTextRange()
   strFound=TRange.findText(str)
   if (strFound) TRange.select()
  }
 }
 if (!strFound) alert ("String '"+str+"' not found!")
}
//-->

</script> 
<form name="f1" action="" 
onSubmit="if(this.t1.value!=null && this.t1.value!='')
findString(this.t1.value);return false"
>
Find: 
<input type="text" name=t1 value="" size=20>
</form>
</br>
<div style="height: auto;width: auto;border:2px solid skyblue;padding-left: 20px;padding-right: 20px;">
<h2>Command List</h2>
<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="100" nowrap="" valign="top">
                <p align="center">
                    <strong>Function</strong>
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    <strong>Attribute Name</strong>
                </p>
            </td>
            <td width="102" nowrap="" colspan="2" valign="top">
                <p align="center">
                    <strong>COAP</strong>
                </p>
            </td>
            <td width="289" nowrap="" colspan="4" valign="top">
                <p align="center">
                    <strong>Device and System</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    <strong>GET</strong>
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <strong>PUT</strong>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    <strong>RF</strong>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    <strong>MBB</strong>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    <strong>Ethernet</strong>
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    <strong>DCU</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="2" valign="top">
                <p align="center">
                    Common Operation
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Factory Setting
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Reset
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="2" valign="top">
                <p align="center">
                    Time
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    utc time
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    time_zone
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="13" valign="top">
                <p align="center">
                    Meter Information
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Meter Serial Number
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Meter Manufacture Number
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Customer Number
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Model Name
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    HW Version
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    SW Version
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Meter Status
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Last Update Time
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Last Comm Time
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    LP Channel Count
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    LP Interval
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Cumulative Active Power
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Cumulative Active Power Time
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="10" valign="top">
                <p align="center">
                    Electric Meter Information
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    CT
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    PT
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Transformer Ratio
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Switch Status
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Phase Configuration
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Frequency
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    VA_SF
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    VAH_SF
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    DISP_SCALAR
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    DISP_MULTIPLIER
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="7" valign="top">
                <p align="center">
                    Meter
                    <br/>
                    Terminal
                    <br/>
                    Information
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Primary Power Source Type
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Secondary Power Source Type
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Reset Count
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Reset Reason
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Operation Time
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Reset Schedule
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Network Management System
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="4" valign="top">
                <p align="center">
                    Data Concentrator Unit
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Model Name
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    HW Version
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    SW Version
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    DCU ID
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="2" valign="top">
                <p align="center">
                    Communication Interface　
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Type(Main)
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Type(Sub)
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="2" valign="top">
                <p align="center">
                    Ethernet Interface　
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Main IPv4 Address
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Main Port Number
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="11" valign="top">
                <p align="center">
                    Mobile Interface
                </p>
            </td>
            <td width="109" valign="top">
                <p align="center">
                    Mobile Type
                    <br/>
                    (Access Technology Used)
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Mobile ID (IMEI)
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    IMSI
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Mobile Number
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Mobile Mode
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    APN
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    ID
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    PASSWORD
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    IP Address
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Read Current Network Status
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Connection Status
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" rowspan="2" valign="top">
                <p align="center">
                    6LoWPAN Information
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Interface IPv6 Address
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    IPv6 Address
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Interface Listen Port
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Network Listen Port
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Frequency
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Bandwidth
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Base Station Address
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    APP Key
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Hops to Base Station
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    EUI 64
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="left">
                    <s>
                    </s>
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Listen Port
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Max Hop
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
                <p align="center">
                    Meter Terminal operation
                    <br/>
                    Schedule Information
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Metering schedule
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" valign="top">
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    LP Upload schedule
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" valign="top">
                <p align="center">
                    Event
                </p>
            </td>
            <td width="109" nowrap="" valign="top">
                <p align="center">
                    Event Type
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="47" nowrap="" valign="top">
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="76" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
            <td width="62" nowrap="" valign="top">
                <p align="center">
                    √
                </p>
            </td>
        </tr>
    </tbody>
</table>
<h2>
    <a name="OLE_LINK8"></a>
    <a name="OLE_LINK7"></a>
    <a name="OLE_LINK11"></a>
    <a name="OLE_LINK10"></a>
    <a name="OLE_LINK13"></a>
    <a name="OLE_LINK12"></a>
    <a name="_Toc462402337"></a>
</h2>
<h3>
    <a name="_Toc462402338"></a>
    <a name="_Toc459807207">A.4.1 Common Operation</a>
</h3>
<h4>
    <a name="_Toc459807208">A.4.1.1 Factory Setting</a>
</h4>
<p>
    This command is used to perform the factory setting.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /common_operation/ factory_setting
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    ?set=1
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /1/f_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    ?set=1
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807209">A.4.1.2 Reset</a>
</h4>
<p>
    This command is used to perform the equipment reset.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /common_operation/reset
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    ?set=1
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /1/reset
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    ?set=1
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h3>
    <a name="_Toc462402339"></a>
    <a name="_Toc459807213">A.4.2 Time</a>
</h3>
<h4>
    <a name="_Toc459807214">A.4.2.1 UTC Time</a>
</h4>
<p>
    This command is used to get the equipment time.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /time/utc_time
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /3/u_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Year
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Month
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Day
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Hour
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Minute
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Second
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>7 Bytes</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807215">A.4.2.2 Time Zone</a>
</h4>
<p>
    This command is used to get the time zone.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /time/time_zone
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /3/t_z
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Offset
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Time zone offset value: 0-23
</p>
<p>
</p>
<h3>
    <a name="_Toc462402340"></a>
    <a name="_Toc459807216">A.4.3 Meter Information</a>
</h3>
<h4>
    <a name="_Toc459807217">A.4.3.1 Meter Serial Number</a>
</h4>
<p>
    This command is used to get the meter serial number.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/m_ser
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807218">A.4.3.2 Meter Manufacture Number</a>
</h4>
<p>
    This command is used to get the meter manufacturing number.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/m_m
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807219">A.4.3.3 Customer Number</a>
</h4>
<p>
    This command is used to get the customer number.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/c_n
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807220">A.4.3.4 Model Name</a>
</h4>
<p>
    This command is used to get the model name.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/m_n
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807221">A.4.3.5 HW Version</a>
</h4>
<p>
    This command is used to get the hardware version.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/hw_ver
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807222">A.4.3.6 SW Version</a>
</h4>
<p>
    This command is used to get the software version.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/sw_ver
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807223">A.4.3.7 Meter Status</a>
</h4>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/m_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<p>
    <em>Meter Status Register</em>
</p>



<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="407" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" valign="top">
                <p align="left">
                    Tag
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="407" valign="top">
                <p align="left">
                    OBIS Code Tag.
                </p>
            </td>
        </tr>
        <tr>
            <td width="12%">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="22%">
                <p align="center">
                    4
                </p>
            </td>
            <td width="64%">
                <table border="1" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td width="451" colspan="8">
                                <p align="center">
                                    Byte0
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="100">
                                <p align="center">
                                    Clock invalid
                                </p>
                            </td>
                            <td width="73">
                                <p align="center">
                                    Replace battery
                                </p>
                            </td>
                            <td width="59">
                                <p align="center">
                                    Power up
                                </p>
                            </td>
                            <td width="71">
                                <p align="center">
                                    L1 error
                                </p>
                            </td>
                            <td width="57">
                                <p align="center">
                                    L2 error
                                </p>
                            </td>
                            <td width="43">
                                <p align="center">
                                    L3 error
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="451" colspan="8">
                                <p align="center">
                                    Byte1
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="100">
                                <p align="center">
                                    Program memory error
                                </p>
                            </td>
                            <td width="73">
                                <p align="center">
                                    RAM error
                                </p>
                            </td>
                            <td width="59">
                                <p align="center">
                                    NV memory error
                                </p>
                            </td>
                            <td width="71">
                                <p align="center">
                                    Watchdog error
                                </p>
                            </td>
                            <td width="57">
                                <p align="center">
                                    Fraud attempt
                                </p>
                            </td>
                            <td width="43">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="451" colspan="8">
                                <p align="center">
                                    Byte2
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="100">
                                <p align="center">
                                    Communication error M-BUS
                                </p>
                            </td>
                            <td width="73">
                                <p align="center">
                                    New M-BUS device discovered
                                </p>
                            </td>
                            <td width="59">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="71">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="57">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="43">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="451" colspan="8">
                                <p align="center">
                                    Byte3
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="100">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="73">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="59">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="71">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="57">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="43">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                            <td width="24">
                                <p align="center">
                                    -
                                </p>
                            </td>
                        </tr>
                         </tbody>
                </table>
            </td>
        </tr>
    </tbody>
</table>




<p>
</p>
<h4>
    <a name="_Toc459807224">A.4.3.8 Last Update Time</a>
</h4>
<p>
    This command is used to get the last updated time.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/l_up_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Year
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Month
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Day
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Hour
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Minute
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Second
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>7 Bytes</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807225">A.4.3.9 Last Comm Time</a>
</h4>
<p>
    This command is used to get the last communication time.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/l_comm_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Year
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Month
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Day
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Hour
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Minute
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Second
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>7 Bytes</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807226">A.4.3.10 LP Channel Count</a>
</h4>
<p>
    This command is used to get the LP channel count.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/lp_ch_cnt
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Count
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Count: LP channel count of a meter.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807227">A.4.3.11 LP Interval</a>
</h4>
<p>
    This command is used to get the LP interval.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/lp_in
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - LP Interval: LP interval of a meter.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807228">A.4.3.12 Cumulative Active Power</a>
</h4>
<p>
    This command is used to get the cumulated active power.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/c_a_power
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
        
    </tbody>
</table>
<p>
    - Value: Currently cumulated active power.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807229">A.4.3.13 Cumulative Active Power Time</a>
</h4>
<p>
    This command is used to get the cumulated active power time.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /4/c_a_power_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Year
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Month
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Day
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Hour
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Minute
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    Second
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>7 Bytes</strong>
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
            <td width="71" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h3>
    <a name="_Toc462402341"></a>
    <a name="_Toc459807230">A.4.4 Electric Meter Information</a>
</h3>
<h4>
    <a name="_Toc459807231">A.4.4.1 CT</a>
</h4>
<p>
    This command is used to get the CT value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/ct
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: CT value (Current Ratio) of a meter.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807232">A.4.4.2 PT</a>
</h4>
<p>
    This command is used to get the PT value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/pt
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: PT value (Voltage Ratio) of a meter.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807233">A.4.4.3 Transformer Ratio</a>
</h4>
<p>
    This command is used to get the transformer ratio value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/t_r
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Transformer Ratio value (CT*PT = Transformer Ratio).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807234">A.4.4.4 Phase Configuration</a>
</h4>
<p>
    This command is used to get the phase configuration.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/p_c
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <strong> </strong>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>4 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: 4Bytes Text/Plain
</p>

<p>
</p>
<h4>
    <a name="_Toc459807235">A.4.4.5 Switch Status</a>
</h4>
<p>
    This command is used to get the switch status.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/s_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
     
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Switch status (0: Switch Off, 1: Switch On).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807236">A.4.4.6 Frequency</a>
</h4>
<p>
    This command is used to get the line frequency value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/fre
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Line frequency value of a meter.
</p>
<h4>
    <a name="_Toc459807237">A.4.4.7 VA_SF</a>
</h4>
<p>
    This command is used to get the power scale factor value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/va_sf
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Power scale factor value of a meter.
</p>
<p>
</p>
<p>
</p>
<h4>
    <a name="_Toc459807238">A.4.4.8 VAH_SF</a>
</h4>
<p>
    This command is used to get the energy scale factor value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/vah_sf
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    OBIS CODE Tag
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>5 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Bytes
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Energy scale factor value of a meter.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807239">A.4.4.9 DISP_SCALAR</a>
</h4>
<p>
    This command is used to get the display scalar value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/disp_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <strong> </strong>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Scale value to apply before displaying.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807240">A.4.4.10 DISP_MULTIPLIER</a>
</h4>
<p>
    This command is used to get the display multiplier value.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /5/disp_m
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Value
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Value: Display multiplier value of a meter.
</p>
<p>
</p>
<h3>
    <a name="_Toc462402342"></a>
    <a name="_Toc459807241">A.4.5 Meter Terminal Information</a>
</h3>
<h4>
    <a name="_Toc459807242">A.4.5.1 Primary Power Source Type</a>
</h4>
<p>
    This command is used to get the primary power source.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/p_p_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type: 0: Unknown, 1: Electric, 2: Battery, 3: Solar, 4: SuperCap
</p>
<p>
</p>
<h4>
    <a name="_Toc459807243">A.4.5.2 Secondary Power Source Type</a>
</h4>
<p>
    This command is used to get the secondary power source type.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/s_p_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type: 0: Unknown, 1: Electric, 2: Battery, 3: Solar, 4: SuperCap
</p>
<p>
</p>
<h4>
    <a name="_Toc459807244">A.4.5.3 Reset Count</a>
</h4>
<p>
    This command is used to get the reset count.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/r_cnt
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Count
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>2 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Count: The number of a meter terminal reset count.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807245">A.4.5.4 Reset Reason</a>
</h4>
<p>
    This command is used to get the last reset reason.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/r_r
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Reason
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Reason
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="47" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    Reserved
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    Low power
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    WWDOG
                </p>
            </td>
            <td width="57" valign="top">
                <p align="left">
                    IWDOG
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    Software
                </p>
            </td>
            <td width="69" valign="top">
                <p align="left">
                    POR/PDR
                </p>
            </td>
            <td width="35" valign="top">
                <p align="left">
                    Pin
                </p>
            </td>
            <td width="118" valign="top">
                <p align="left">
                    POR/PDR or BOR
                </p>
            </td>
        </tr>
        <tr>
            <td width="47" valign="top">
                <p align="left">
                    <strong>Bit</strong>
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    7
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    6
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    5
                </p>
            </td>
            <td width="57" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    3
                </p>
            </td>
            <td width="69" valign="top">
                <p align="left">
                    2
                </p>
            </td>
            <td width="35" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="118" valign="top">
                <p align="left">
                    0
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807246">A.4.5.5 Operation Time</a>
</h4>
<p>
    This command is used to get the total operation time.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/o_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    Count
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>4 Bytes</strong>
                </p>
            </td>
            <td width="87" valign="top">
                <p align="left">
                    4 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Count: The total operation time of a meter terminal (seconds).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807247">A.4.5.6 Reset Schedule</a>
</h4>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/r_sch
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Reset Schedule
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Reset Schedule: 0-23 hours. If the value is ‘0xFF’, it does not perform the modem reset.
</p>
<h4>
    <a name="_Toc459807248">A.4.5.7 Network Management System</a>
</h4>
<p>
    You can get the network-related information by using this command.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /6/n_m_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Network Type</strong>
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field Data</strong>
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="113" valign="top" rowspan="4">
                <p align="left">
                    MBB
                </p>
                <p align="left">
                    (7 Bytes)
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    CPU Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average CPU usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Memory Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average memory usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Total TX Size
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Total uploading data until now (Byte)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Network
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Current network of the modem
                </p>
                <p align="left">
                    0 : GSM (2G)
                </p>
                <p align="left">
                    2 : UTRAN (3G)
                </p>
                <p align="left">
                    3 : GSM w/EGPRS (2G)
                </p>
                <p align="left">
                    4 : UTRAN w/HSDPA (3G)
                </p>
                <p align="left">
                    5 : UTRAN w/HSUPA (3G)
                </p>
                <p align="left">
                    6 : UTRAN w/HSDPA and HSUPA (3G)
                </p>
                <p align="left">
                    7 : E-UTRAN (4G)
                </p>
            </td>
        </tr>
        <tr>
            <td width="113" valign="top" rowspan="3">
                <p align="left">
                    Ethernet
                </p>
                <p align="left">
                    (6 Bytes)
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    CPU Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average CPU usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Memory Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average memory usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Total TX Size
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Total uploading data until now (Byte)
                </p>
            </td>
        </tr>
        <tr>
            <td width="113" valign="top" rowspan="7">
                <p align="left">
                    RF
                </p>
                <p align="left">
                    (18 Bytes)
                </p>
            </td>
            <td width="123" valign="top">
                <p align="left">
                    Parent Node ID
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    8
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Parent Node EUI ID of the modem
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    RSSI
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    RSSI value from the modem
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    LQI
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    LQI value from the modem
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    ETX
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    2
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Communication success rate used at the RF stack. Refer to the ETX description of Parent Node Info.
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    CPU Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average CPU usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Memory Usage
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Average memory usage (%) (Ex: 80 -&gt; 80%)
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Total TX Size
                </p>
            </td>
            <td width="66" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="307" valign="top">
                <p align="left">
                    Total uploading data until now (Byte)
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <a name="_Toc459807249"> </a>
</p>
<h3>
    <a name="_Toc462402343">A.4.6 Data Concentrator</a>
</h3>
<h4>
    <a name="_Toc459807250">A.4.6.1 Model Name</a>
</h4>
<p>
    This command is used to get the model name.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /dcu_info/model_name
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807251">A.4.6.2 HW Version</a>
</h4>
<p>
    This command is used to get the hardware version.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /dcu_info/hw_version
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807252">A.4.6.3 SW Version</a>
</h4>
<p>
    This command is used to get the software version.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /dcu_info/sw_version
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807253">A.4.6.4 DCU ID</a>
</h4>
<p>
    This command is used to get the concentrator ID.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /dcu_info/id
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (20 Bytes).
</p>
<p>
</p>
<h3>
    <a name="_Toc462402344"></a>
    <a name="_Toc459807254">A.4.7 Communication Interface</a>
</h3>
<h4>
    <a name="_Toc459807255">A.4.7.1 Type (Main)</a>
</h4>
<p>
    This command is used to get the concentrator communication interface.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <a name="OLE_LINK5"></a>
                    <a name="OLE_LINK4">/comm_interface/main_type</a>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type (0: Ethernet, 1: Mobile)
</p>
<p>
</p>
<h4>
    <a name="_Toc459807256">A.4.7.2 Type (Sub)</a>
</h4>
<p>
    This command is used to get the concentrator sub-communication interface.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /comm_interface/sub_type
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type (0: ZigBee, 1: PLC, 2: SubGiga)
</p>
<p>
</p>
<h3>
    <a name="_Toc462402345"></a>
    <a name="_Toc459807257">A.4.8 Ethernet Interface</a>
</h3>
<h4>
    <a name="_Toc459807258">A.4.8.1 Main IP Address</a>
</h4>
<p>
    This command is used to get or set the main IP address.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="95" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="165" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    <a name="OLE_LINK3"></a>
                    <a name="OLE_LINK2">/ether_interface/main_ip_address</a>
                </p>
            </td>
            <td width="95" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="165" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="165" valign="top">
                <p align="left">
                    <em>?address=<strong>#Address</strong></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /9/m_ip_addr
                </p>
            </td>
            <td width="95" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="165" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="165" valign="top">
                <p align="left">
                    <em>?address=<strong>#Address</strong></em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the IP address on the <strong>#Address</strong> part of the query. (<em>Ex: ?address=198.172.2.14</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<p>
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    address
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Ex (IPv4): 187.1.30.141
                </p>
                <p align="left">
                    Ex (IPv6): FD80:0:0:0:0:0:0:1
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<h4>
    <a name="_Toc459807259">A.4.8.2 Main Port Number</a>
</h4>
<p>
    This command is used to get or set the main port number (Auth port).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /ether_interface/main_port_number
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?port=<strong>#Port</strong></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /9/m_port_num
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?port=<strong>#Port</strong></em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Port</strong> part of the query. (<em>Ex: ?port=8089</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Port
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    2 (N)
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Port number
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h3>
    <a name="_Toc462402346"></a>
    <a name="_Toc459807260">A.4.9 Mobile Interface</a>
</h3>
<h4>
    <a name="_Toc459807261">A.4.9.1 Mobile Type (Access Technology)</a>
</h4>
<p>
    This command is used to get the mobile interface type.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/mobile_type
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/m_t
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type
</p>
<p>
    &gt; 0: GSM, 1: GSM Compact, 2: UTRAN, 3: GSM w/EGPRS, 4: UTRAN w/HSDPA,
</p>
<p>
    5: UTRAN w/HSUPA, 6: UTRAN w/HSDPA and HSUPA, 7: E-UTRAN
</p>
<p>
</p>
<h4>
    <a name="_Toc459807262">A.4.9.2 Mobile ID (IMEI)</a>
</h4>
<p>
    This command is used to get the IMEI.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/mobile_id
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/m_i
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (16 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807263">A.4.9.3 IMSI</a>
</h4>
<p>
    This command is used to get the IMSI.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/imsi
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /8/imsi
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (16 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807264">A.4.9.4 Mobile Number (MSISDN)</a>
</h4>
<p>
    This command is used to get the subscriber number.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/mobile_number
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/m_n
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (16 Bytes).
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<h4>
    <a name="_Toc459807265">A.4.9.5 Mobile Mode</a>
</h4>
<p>
    This command is used to get the mobile mode.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/mobile_mode
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/m_m
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Type
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Type (0: CSD, 1: Packet, 2: Always On)
</p>
<p>
</p>
<h4>
    <a name="_Toc459807266">A.4.9.6 APN</a>
</h4>
<p>
    This command is used to get or set the APN information.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="151" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="259" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="151" valign="top" rowspan="2">
                <p align="left">
                    /mobile_interface/apn
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="259" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="259" valign="top">
                <p align="left">
                    <em>?apn=<strong>#APN</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="151" valign="top" rowspan="2">
                <p align="left">
                    /10/apn
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="259" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="259" valign="top">
                <p align="left">
                    <em>?apn=<strong>#APN</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the APN info on the <strong>#APN</strong> part of the query. (<em>Ex: ?apn=apninformation</em>)
</p>
<p>
</p>
<h4>
    <a name="_Toc459807267">A.4.9.7 ID</a>
</h4>
<p>
    This command is used to get or set the APN ID.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /mobile_interface/id
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?id=<strong>#ID</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /10/id
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?id=<strong>#ID</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the ID on the <strong>#ID</strong> part of the query. (<em>Ex: ?id=myid</em>)
</p>
<h4>
    <a name="_Toc459807268">A.4.9.8 Password</a>
</h4>
<p>
    This command is used to get or set the APN password.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="180" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="180" valign="top" rowspan="2">
                <p align="left">
                    /mobile_interface/password
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <em>?password=<strong>#Password</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="180" valign="top" rowspan="2">
                <p align="left">
                    /10/pwd
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <em>?password=<strong>#Password</strong> (Max 20 Bytes)</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the password on the <strong>#Password</strong> part of the query. (<em>Ex: ?password=mypassword</em>)
</p>
<p>
</p>
<h4>
    <a name="_Toc459807269">A.4.9.9 IP Address</a>
</h4>
<p>
    This command is used to get the IP address.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/ip_address
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/ip_addr
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    IPv4
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Use 4 Bytes for IPv4 (Ex: 187.1.30.141 -&gt; 0xBB011E8D)
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <a name="_Toc459807270"> </a>
</p>
<h4>
    A.4.9.10 Read Current Network Status
</h4>
<p>
    This command is used to get the read current network status.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/read_current_network_status
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/r_c_n_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <strong> </strong>
</p>
<p>
    <strong>[Response]</strong>
</p>
<p>
    The response will be displayed as text/plain value (150 Bytes).
</p>
<p>
</p>
<h4>
    <a name="_Toc459807271">A.4.9.11 Connection Status</a>
</h4>
<p>
    This command is used to get the connection status.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /mobile_interface/connection_status
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /10/c_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>1 Byte</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    1 Byte
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Status (0: disconnected, 1: connected)
</p>
<p>
</p>
<h3>
    <a name="_Toc462402347"></a>
    <a name="_Toc459807272">A.4.10 6LoWPAN Information</a>
</h3>
<h4>
    <a name="_Toc459807273">A.4.10.1 Interface IPv6 Address</a>
</h4>
<p>
    This command is used to get the interface IPv6 (DCU IP).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/i_i6_addr
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Address
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>16 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    16 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Address: IPv6 address
</p>
<p>
</p>
<h4>
    <a name="_Toc459807274">A.4.10.2 IPv6 Address</a>
</h4>
<p>
    This command is used to get the IPv6 address.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/i6_addr
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Address
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>16 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    16 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Address: IPv6 address
</p>
<p>
</p>
<h4>
    <a name="_Toc459807275">A.4.10.3 Interface Listen Port</a>
</h4>
<p>
    This command is used to get or set the interface listen port (DCU DTLS).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /11/i_l_port
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?port=<strong>#Port</strong></em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Port</strong> part of the query. (<em>Ex: ?port=8089</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Port
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    2 (N)
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Interface listen port
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807276">A.4.10.4 Network Listen Port</a>
</h4>
<p>
    This command is used to get or set the network listen port (DCU DTLS).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /11/n_l_port
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?port=<strong>#Port</strong></em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Port</strong> part of the query. (<em>Ex: ?port=8089</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Port
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    2 (N)
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Network listen port
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807277">A.4.10.5 Frequency</a>
</h4>
<p>
    This command is used to get the operation frequency bandwidth.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/fre
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Start Freq
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    End Freq
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>6 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    3 Bytes
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    3 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Start Freq: Start frequency. First two bytes are integer portion and last one byte is decimal portion.
</p>
<p>
    &gt; Ex: 917.3MHz -&gt; 0x039503
</p>
<p>
    - End Freq: End frequency. First two bytes are integer portion and last one byte is decimal portion.
</p>
<p>
    &gt; Ex: 923.1MHz -&gt; 0x039B01
</p>
<p>
</p>
<h4>
    <a name="_Toc459807278">A.4.10.6 Bandwidth</a>
</h4>
<p>
    This command is used to get the frequency bandwidth for each channel.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/band
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Bandwidth
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>2 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Bandwidth: Frequency bandwidth (kHz).
</p>
<p>
    &gt; Ex: 0x00C8 -&gt; 200 kHz
</p>
<p>
</p>
<h4>
    <a name="_Toc459807279">A.4.10.7 Base Station Address</a>
</h4>
<p>
    This command is used to get the base station address (Coordi).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/b_s_addr
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Address
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>16 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    16 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Address: IPv6 address
</p>
<p>
</p>
<h4>
    <a name="_Toc459807280">A.4.10.8 APP Key</a>
</h4>
<p>
    This command is used to get the APP Key (Session Key)
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/a_key
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Key
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>16 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    16 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Key: Session Key
</p>
<p>
</p>
<h4>
    <a name="_Toc459807281">A.4.10.9 Hops to Base Station</a>
</h4>
<p>
    This command is used to get the hops to the Coordinator (Concentrator).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/h_t_b_s
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    Hops
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>2 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    2 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - Hops: The number of hops
</p>
<p>
    &gt; 0x0001: Coordinator, 0x0100: 1hope node, 0x0200: 2hope node, 0x0300: 3hope node
</p>
<p>
</p>
<h4>
    <a name="_Toc459807282">A.4.10.10 EUI 64</a>
</h4>
<p>
    This command is used to get the EUI 64 of a modem.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/e_64
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>Total</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    EUI 64
                </p>
            </td>
        </tr>
        <tr>
            <td width="112" valign="top">
                <p align="left">
                    <strong>8 Bytes</strong>
                </p>
            </td>
            <td width="134" valign="top">
                <p align="left">
                    8 Bytes
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    - EUI 64: EUI 64 of a modem.
</p>
<p>
</p>
<h4>
    <a name="_Toc459807283">A.4.10.11 Listen Port</a>
</h4>
<p>
    This command is used to get or set the listen port (DCU DTLS).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="227" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="227" valign="top" rowspan="2">
                <p align="left">
                    /11/l_port
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="184" valign="top">
                <p align="left">
                    <em>?port=<strong>#Port</strong></em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Port</strong> part of the query. (<em>Ex: ?port=8089</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Port
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    2 (N)
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Port number
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    <a name="_Toc459807284">A.4.10.12 Max Hop</a>
</h4>
<p>
    This command is used to get the max hop.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="331" valign="top">
                <p align="left">
                    /11/m_h
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="80" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Hop
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Max hop
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    <a name="_Toc459807285"> </a>
</p>
<h3>
    <a name="_Toc462402348">A.4.11 Meter Terminal Operation Schedule Information</a>
</h3>
<h4>
    <a name="_Toc459807286">A.4.11.1 Metering Schedule</a>
</h4>
<p>
    This command is used to get or set the metering schedule.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="180" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="180" valign="top" rowspan="2">
                <p align="left">
                    /12/metering
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <em>?second=<strong>#Value</strong> (Seconds)</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Value</strong> part of the query. (<em>Ex: ?second=54</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Schedule
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    2
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Seconds
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h4>
    A.4.11.2 LP Upload Schedule
</h4>
<p>
    This command is used to get or set the LP upload schedule.
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="180" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top" rowspan="2">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="180" valign="top" rowspan="2">
                <p align="left">
                    /12/lp_upload
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    PUT
                </p>
            </td>
            <td width="231" valign="top">
                <p align="left">
                    <em>?second=<strong>#Value</strong> (Seconds)</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
    * Enter the port number on the <strong>#Value</strong> part of the query. (<em>Ex: ?second=54</em>)
</p>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>

<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="123" valign="top">
                <p align="left">
                    Schedule
                </p>
            </td>
            <td width="113" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="373" valign="top">
                <p align="left">
                    Seconds
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
</p>
<p>
</p>
<h3>
    <a name="_Toc462402349"></a>
    <a name="_Toc459807290">A.4.12 Event</a>
</h3>
<h4>
    <a name="_Toc459807291">A.4.12.1 Event Type</a>
</h4>
<p>
    This command is used to get the event information based on a type (Modem support only 0, 4 types).
</p>
<table border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="103" valign="top">
                <p align="left">
                    <strong>Equipment</strong>
                </p>
            </td>
            <td width="108" valign="top">
                <p align="left">
                    <strong>URL</strong>
                </p>
            </td>
            <td width="72" valign="top">
                <p align="left">
                    <strong>GET/PUT</strong>
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <strong>Type</strong>
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <strong>Query</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="103" valign="top" rowspan="3">
                <p align="left">
                    DCU
                </p>
            </td>
            <td width="108" valign="top" rowspan="3">
                <p align="left">
                    /event/type
                </p>
            </td>
            <td width="72" valign="top" rowspan="3">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    0
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type?start_index&amp;end_index</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    2
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type&amp;count</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type&amp;poll_type&amp;offset&amp;count</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="103" valign="top" rowspan="3">
                <p align="left">
                    Modem
                </p>
            </td>
            <td width="108" valign="top" rowspan="3">
                <p align="left">
                    /13/type
                </p>
            </td>
            <td width="72" valign="top" rowspan="3">
                <p align="left">
                    GET
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    0
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type?start_index&amp;end_index</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    2
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type&amp;count</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    4
                </p>
            </td>
            <td width="250" valign="top">
                <p align="left">
                    <em>?type&amp;poll_type&amp;offset&amp;count</em>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Type 0] Query</strong>
</p>
<table width="609" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <em>?type</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    0: modem event, 1: network event, 2: DCU event, 3: server event, 4: Meter event
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <em>?start_index</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    Start index of the log to get. (0 is the latest log.)
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <em>?end_index</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    End index of the log to get.
                </p>
                <p align="left">
                    If you want to get the latest 10 logs: start_index=0, end_index=9
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Type 2] Query</strong>
</p>
<table width="609" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <em>?type</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    0: modem event, 1: network event, 2: DCU event, 3: server event, 4: Meter event
                </p>
            </td>
        </tr>
        <tr>
            <td width="95" valign="top">
                <p align="left">
                    <em>count</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="468" valign="top">
                <p align="left">
                    The number of logs to get.
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Type 4] Query</strong>
</p>
<table width="609" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="142" valign="top" colspan="2">
                <p align="left">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="420" valign="top">
                <p align="left">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="142" valign="top" colspan="2">
                <p align="left">
                    <em>?type</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    1
                </p>
            </td>
            <td width="420" valign="top">
                <p align="left">
                    0: modem event, 1: network event, 2: DCU event, 3: server event, 4: Meter event
                </p>
            </td>
        </tr>
        <tr>
            <td width="66" valign="top" rowspan="3">
                <p align="left">
                    Poll data
                </p>
            </td>
            <td width="76" valign="top">
                <p align="left">
                    <em>poll_type</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="420" valign="top">
                <p align="left">
                    The type to get. Refer to the <strong>Poll Type</strong> table as follows.
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    <em>offset</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="420" valign="top">
                <p align="left">
                    Start offset of metering data (0 is the latest one.)
                </p>
            </td>
        </tr>
        <tr>
            <td width="76" valign="top">
                <p align="left">
                    <em>count</em>
                </p>
            </td>
            <td width="47" valign="top">
                <p align="left">
                    N
                </p>
            </td>
            <td width="420" valign="top">
                <p align="left">
                    The number of metering data.
                </p>
                <p align="left">
                    Ex) offset 0, count 3 -&gt; You can get the latest three data.
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>Poll Type</strong>
</p>
<table width="98%" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="15%">
                <p align="center">
                    <strong>Value</strong>
                </p>
            </td>
            <td width="84%">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    1
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Load profile
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    2
                </p>
            </td>
            <td width="84%">
                <p>
                    M-BUS Data 1
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    3
                </p>
            </td>
            <td width="84%">
                <p>
                    M-BUS Data 2
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    4
                </p>
            </td>
            <td width="84%">
                <p>
                    M-BUS Data 3
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    5
                </p>
            </td>
            <td width="84%">
                <p>
                    M-BUS Data 4
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    6
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Standard events
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    7
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Control logs
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    8
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Power failure logs for single-phase/poly-phase
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    9
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Power quality logs
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    10
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Tampering logs
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    11
                </p>
            </td>
            <td width="84%">
                <p>
                    DLMS Firmware upgrade logs
                </p>
            </td>
        </tr>
        <tr>
            <td width="15%">
                <p align="center">
                    8~255
                </p>
            </td>
            <td width="84%">
                <p>
                    Reserved
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
    <strong>[Response]</strong>
</p>
<ul>
    <li>
        <p>
            Event
        </p>
    </li>
</ul>
<table width="101%" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="12%">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="17%">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="70%">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="12%">
                <p align="center">
                    Event
                </p>
            </td>
            <td width="17%">
                <p align="center">
                    N
                </p>
            </td>
            <td width="70%">
                <p>
                    0: <em>Modem Event </em>=&gt; Do not get over 50 logs because of network overload.
                </p>
                <p>
                    4: <em>Meter Event </em>=&gt; Response payload is over 1024, the result will be the Bad Request.
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<ul>
    <li>
        <p>
            Modem Event
        </p>
    </li>
</ul>
<table width="98%" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="30%" valign="top" colspan="2">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="58%">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="30%" valign="top" colspan="2">
                <p align="center">
                    Event Log Count
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    2
                </p>
            </td>
            <td width="58%">
                <p>
                    The number of event log
                </p>
            </td>
        </tr>
        <tr>
            <td width="10%" rowspan="4">
                <p align="center">
                    Event
                </p>
                <p align="center">
                    Log
                </p>
                <p align="center">
                    Data
                </p>
            </td>
            <td width="19%" valign="top">
                <p align="center">
                    Index
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    2
                </p>
            </td>
            <td width="58%">
                <p>
                    Event Log Index
                </p>
            </td>
        </tr>
        <tr>
            <td width="19%" valign="top">
                <p align="center">
                    Time
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    7
                </p>
            </td>
            <td width="58%">
                <p>
                    Event Log Time (0xYYYYMMDDhhmmss)
                </p>
            </td>
        </tr>
        <tr>
            <td width="19%" valign="top">
                <p align="center">
                    Log Code
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    2
                </p>
            </td>
            <td width="58%">
                <p>
                    Log Code. Refer to the <strong><em>‘A.5 MIU Event Code List’</em></strong>.
                </p>
            </td>
        </tr>
        <tr>
            <td width="19%" valign="top">
                <p align="center">
                    Log Value
                </p>
            </td>
            <td width="11%">
                <p align="center">
                    4
                </p>
            </td>
            <td width="58%">
                <p>
                    Log Data. Refer to the <strong><em>‘A.5 MIU Event Code List’</em></strong>.
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<ul>
    <li>
        <p>
            Meter Event
        </p>
    </li>
</ul>
<table width="101%" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="18%" colspan="2">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="7%">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="74%">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="10%" rowspan="3">
                <p align="center">
                    Poll Data
                </p>
            </td>
            <td width="7%">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="7%">
                <p align="center">
                    1
                </p>
            </td>
            <td width="74%">
                <p>
                    Refer to the Poll Type table above.
                </p>
            </td>
        </tr>
        <tr>
            <td width="7%">
                <p align="center">
                    Length
                </p>
            </td>
            <td width="7%">
                <p align="center">
                    2
                </p>
            </td>
            <td width="74%">
                <p>
                    Data size.
                </p>
            </td>
        </tr>
        <tr>
            <td width="7%">
                <p align="center">
                    Data
                </p>
            </td>
            <td width="7%">
                <p align="center">
                    N
                </p>
            </td>
            <td width="74%">
                <p>
                    Format is as follows:
                </p>
                <table width="100%" border="1" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td width="16%">
                                <p align="center">
                                    <strong>Poll Type</strong>
                                </p>
                            </td>
                            <td width="83%">
                                <p align="center">
                                    <strong>Format</strong>
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="16%">
                                <p align="center">
                                    1~11
                                </p>
                            </td>
                            <td width="83%">
                                <p>
                                    Metering Data Frame’s <em>DLMS Meter</em> Format
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="16%">
                                <p align="center">
                                    12~255
                                </p>
                            </td>
                            <td width="83%">
                                <p>
                                    <em>Reserved</em>
                                </p>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p>
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h2>
    <a name="_Toc462402350"></a>
    <a name="_Toc459807292"><u>A.5 </u></a>
    <u>MIU Event Code List</u>
</h2>
<h3>
    <a name="_Toc462402351"></a>
    <a name="_Toc459807293">A.5.1 Status Code</a>
</h3>
<table width="610" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    <strong>Value</strong>
                </p>
            </td>
            <td width="440" valign="top">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x0000
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Success
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x1001
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Format Error
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x1002
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Parameter Error
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x1003
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Value Overflow Error
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x1004
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Invalid Attribute Id
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0x2000
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Metering Busy
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" valign="top">
                <p align="center">
                    0xFF00
                </p>
            </td>
            <td width="440" valign="top">
                <p>
                    Unknown
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<h3>
    <a name="_Toc462402352"></a>
    <a name="_Toc459807294">A.5.2 Event Code List</a>
</h3>
<table width="98%" border="1" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <strong>Code</strong>
                </p>
            </td>
            <td width="75%" valign="top">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="75%" valign="top">
                <p align="center">
                    <strong>Modem Event</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1001
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Boot up
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <table border="1" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td width="153">
                                <p align="center">
                                    MSB
                                </p>
                            </td>
                            <td width="96">
                                <p align="center">
                                    …
                                </p>
                            </td>
                            <td width="100">
                                <p align="center">
                                    …
                                </p>
                            </td>
                            <td width="102">
                                <p align="center">
                                    LSB
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="249" colspan="2">
                                <p align="center">
                                    Reset Count(size : 2 bytes)
                                </p>
                            </td>
                            <td width="202" colspan="2">
                                <p align="center">
                                    Reset Reason(size : 2 bytes)
                                </p>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p>
                    * Reset Reasons are as follows:
                </p>
                <p>
                    0x0000 /**&lt; Power On Reset */
                </p>
                <p>
                    0x0001 /**&lt; Brown Out Detector Unregulated Domain Reset */
                </p>
                <p>
                    0x0002 /**&lt; Brown Out Detector Regulated Domain Reset */
                </p>
                <p>
                    0x0003 /**&lt; External Pin Reset */
                </p>
                <p>
                    0x0004 /**&lt; Watchdog Reset */
                </p>
                <p>
                    0x0005 /**&lt; LOCKUP Reset */
                </p>
                <p>
                    0x0006 /**&lt; System Request Reset */
                </p>
                <p>
                    0x0007 /**&lt; EM4 Reset */
                </p>
                <p>
                    0x0008 /**&lt; EM4 Wake-up Reset */
                </p>
                <p>
                    0x0009 /**&lt; AVDD0 Bod Reset */
                </p>
                <p>
                    0x000A /**&lt; AVDD1 Bod Reset */
                </p>
                <p>
                    0x000B /**&lt; Backup Brown Out Detector, VDD_DREG */
                </p>
                <p>
                    0x000C /**&lt; Backup Brown Out Detector, BU_VIN */
                </p>
                <p>
                    0x000D /**&lt; Backup Brown Out Detector Unregulated Domain */
                </p>
                <p>
                    0x000E /**&lt; Backup Brown Out Detector Regulated Domain */
                </p>
                <p>
                    0x000F /**&lt; Backup mode reset */
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1002
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Time sync
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1003
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Factory Setting
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1004
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Power Outage
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1005
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Power Recovery
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1006
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Case Open
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1007
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Case Close
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1008
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Modem Reset
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    1 : By CLI
                </p>
                <p>
                    2 : By Schedule
                </p>
                <p>
                    3 : By Hardfault
                </p>
                <p>
                    4 : By Ondemand
                </p>
                <p>
                    5 : By Mac Command
                </p>
                <p>
                    6 : By Blacklist Add
                </p>
                <p>
                    7 : By Network Down
                </p>
                <p>
                    8 : By Network Scan Fail(Max count)
                </p>
                <p>
                    9 : By Factory Setting
                </p>
                <p>
                    10: By Network Transmit Data Fail(Max count)
                </p>
                <p>
                    11: By Modem No Network Alive
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x1009
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Voltage Low
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x100A
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    CLI Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    1 : CLI Login
                </p>
                <p>
                    2 : CLI Logout
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x100B
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    OTA Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    1 : OTA CRC Fail
                </p>
                <p>
                    2 : OTA Timeout
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="75%" valign="top">
                <p align="center">
                    <strong>Network Event</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x2001
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Network Scan Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    TI NB-PLC : [TI_G3_host_msg.pdf] <em>4.5 G3 Error Status Code Definitions</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x2002
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Network Join Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    1 : Timeout Increase Param
                </p>
                <p>
                    2 : Timeout Default Param
                </p>
                <p>
                    * TI NB-PLC : [TI_G3_host_msg.pdf] <em>4.5 G3 Error Status Code Definitions</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x2003
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Network Transmit Data Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    TI NB-PLC : [TI_G3_host_msg.pdf] <em>4.5 G3 Error Status Code Definitions</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x2004
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Network Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Format
                </p>
                <table border="1" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td width="147">
                                <p align="center">
                                    MSB
                                </p>
                            </td>
                            <td width="99">
                                <p align="center">
                                    …
                                </p>
                            </td>
                            <td width="94">
                                <p align="center">
                                    …
                                </p>
                            </td>
                            <td width="100" colspan="2">
                                <p align="center">
                                    LSB
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td width="147">
                                <p align="center">
                                    Channel
                                </p>
                            </td>
                            <td width="195" colspan="3">
                                <p align="center">
                                    Panid
                                </p>
                            </td>
                            <td width="99">
                                <p>
                                    1 : Up
                                </p>
                                <p>
                                    2 : Down
                                </p>
                            </td>
                        </tr>
                        <tr height="0">
                            <td width="147">
                            </td>
                            <td width="99">
                            </td>
                            <td width="94">
                            </td>
                            <td width="1">
                            </td>
                            <td width="99">
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x2005
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    PPP Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    1 : Up
                </p>
                <p>
                    2 : Down
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="75%" valign="top">
                <p align="center">
                    <strong>Metering</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <a name="_Hlk433290557">0x3001</a>
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Meter Read Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x3002
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Upload Metering Data Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="75%" valign="top">
                <p align="center">
                    <strong>GPRS</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x4001
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Module Init Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x4002
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Network Connect Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x4003
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Transmit Data Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x4004
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    AT Command Fail
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    0x4005
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    SMS Receive
                </p>
            </td>
        </tr>
        <tr>
            <td width="24%" valign="top">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="75%" valign="top">
                <p>
                    Obis code 3Bytes save
                </p>
            </td>
        </tr>
    </tbody>
</table>
<p>
</p>
<p>
</p>
</div>
</body>
</html>