<!-- 문서 변환  https://word2cleanhtml.com/ -->
<!-- AIMIR 4.0 User Menual Appendix A CoAP Browser Command List 내용을 변환했습니다. -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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

</script> 
<form name="f1" action="" 
onSubmit="if(this.t1.value!=null && this.t1.value!='') 
findString(this.t1.value);return false" 
> 
<center>
<input type="text" name=t1 value="" size=20> 
<input type="submit" name=b1 value="Find"> </center>
</form> 
</br>
<div style="height: auto;width: auto;border:2px solid skyblue;padding-left: 20px;padding-right: 20px;min-width: 1000px;">
<h2>
Execute NI Command
</h2>
<h3>
	Procedure
</h3>
<p>
	1. Enter the AttributeID. It is specified with the hex character. "0x" is not required.
</p>
<p>
	2. Enter Attribute Parameters. It is specified with the hex character. If Parameter is unnecessary, leave it blank.
</p>
<p>
	3. Request Type Select "GET" or "SET".
</p>
<p>
	4．Execute by pressing the Execute button.
</p>

<h3>
	Example
</h3>
<p>
	・  When acquiring Modem Time
</p>
<p>
	Attribute ID: 2001
</p>
<p>
	Attribute Parameters: (blank)
</p>
<p>
	Select GET and execute it.
</p>
<p>

</p>
<p>
	・  When deleting the schedule of Meter shared key in Modem Schedule Run
</p>
<p>
	Attribute ID: 6001
</p>
<p>
	Attribute Parameters: 0300000000
</p>
<p>
	Select SET and execute.
</p>
<p>
</p>


<h2>Attribute Id List</h2>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#bbbbbb">
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    <strong>Attribute Id</strong>
                </p>
            </td>
            <td width="370" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
            <td width="110" nowrap="" colspan="2" valign="top">
                <p align="center">
                    <strong>Get</strong>
                </p>
            </td>
            <td width="110" nowrap="" colspan="2" valign="top">
                <p align="center">
                    <strong>Set</strong>
                </p>
            </td>
        </tr>
        <tr bgcolor="#bbbbbb">
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    <strong>Req</strong>
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    <strong>Res</strong>
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    <strong>Req</strong>
                </p>
            </td>
            <td width="55" nowrap="" valign="top">
                <p align="center">
                    <strong>Res</strong>
                </p>
            </td>
        </tr>
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0x0000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#action"> Action </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0x0001       </td>
            <td align="left"><a href="#resetmodem">
                          &nbsp  Reset Modem </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x0002       </td>
            <td align="left"><a href="#uploadmeteringdata">
             &nbsp  Upload Metering Data(Metering+Upload)  </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x0003       </td>
            <td align="left"><a href="#factorysetting">
                     &nbsp  Factory Setting  </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x0005       </td>
            <td align="left"><a href="#reauthenticate">
                     &nbsp  Re-Authenticate  </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x0007       </td>
            <td align="left"><a href="#rollbackimage">
                     &nbsp  Rollback Image   </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x0008       </td>
            <td align="left"><a href="#watchdogtest">
                     &nbsp  Watchdog Test    </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x0009       </td>
            <td align="left"><a href="#uploadmeteringdata">
                      &nbsp  Upload Metering Data(Upload Only) </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x000A       </td>
            <td align="left"><a href="#realtimemetering"> 
                     &nbsp  Real Time Metering</a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x000B       </td>
            <td align="left" ><a href="#cloneonoff">
                        &nbsp  Clone On/Off  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0x1000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#information">Information </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0x1001       </td>
            <td align="left"><a href="#modeminformation"> 
                    &nbsp  Modem Information </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x1004       </td>
            <td align="left"><a href="#modemstatus">
                        &nbsp  Modem Status  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x1005       </td>
            <td align="left"><a href="#meterinformation">
                   &nbsp  Meter Information  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x1006       </td>
            <td align="left"><a href="#modemeventlog"> 
                      &nbsp  Modem Event Log </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x1007       </td>
            <td align="left"><a href="#modemdtlshandshakestatus">
                    &nbsp  Modem DTLS Handshake Status </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x1008       </td>
            <td align="left"><a href="#fwimageinformation">
                 &nbsp  FW Image Information </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0x2000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                    <a href="#configuration">Configuration(Common)</a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0x2001       </td>
            <td align="left"><a href="#modemtime">
                          &nbsp  Modem Time  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2002       </td>
            <td align="left"><a href="#modemresettime">
                     &nbsp Modem Reset Time  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2003       </td>
            <td align="left"><a href="#modemmode">
                          &nbsp  Modem Mode  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2005       </td>
            <td align="left"><a href="#meteringinterval">
                    &nbsp  Metering Interval </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2008       </td>
            <td align="left"><a href="#modemtxpower">
                      &nbsp  Modem TX Power  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x200C       </td>
            <td align="left"><a href="#formjoinnetwork">
                   &nbsp  Form/Join Network  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0x200D       </td>
            <td align="left"><a href="#networkspeed">
                       &nbsp  Network Speed  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x200F       </td>
            <td align="left"><a href="#modemipinformation"> 
                &nbsp  Modem IP Information  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2010       </td>
            <td align="left"><a href="#modemportinformation">
               &nbsp Modem Port Information  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2011       </td>
            <td align="left"><a href="#alarmeventcommandonoff"> 
                        &nbsp  Alarm/Event Command ON_OFF  </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2012       </td>
            <td align="left"><a href="#meterbaud">
                           &nbsp  Meter Baud </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2013       </td>
            <td align="left"><a href="#transmitfrequency">
                  &nbsp  Transmit Frequency  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2014       </td>
            <td align="left"><a href="#retrycount">
                         &nbsp  Retry Count  </td>
            <td align="center"> &#9675       </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2015       </td>
            <td align="left"><a href="#snmptraponoff">
                    &nbsp  SNMP Trap ON_OFF  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0x2016       </td>
            <td align="left"><a href="#rawromaccess">
                     &nbsp  Raw ROM Access  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0x5000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                    <a href="#networkcommunicationtest"> &nbsp Network Communication Test</a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0x5001       </td>
            <td align="left"><a href="#testconfiguration"> &nbsp  Test Configuration </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
       </tr>
        <tr>
            <td align="center"> 0x5002       </td>
            <td align="left"> <a href="#testdataupload"> &nbsp  Test Data Upload  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0x6000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                   <a href="#schedulecontrol"> &nbsp  Schedule Control </a> 
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0x6001       </td>
            <td align="left"> <a href="#modemschedulerun"> &nbsp  Modem Schedule Run </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xC000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#modeminstall">    Modem Install  </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xC003       </td>
            <td align="left"><a href="#joinbackofftimer">  &nbsp  Join Backoff Timer  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xC004       </td>
            <td align="left"><a href="#authbackofftimer">   &nbsp  Auth Backoff Timer  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xC005       </td>
            <td align="left"><a href="#metersharedkey">   &nbsp  Meter Shared Key  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xC007       </td>
            <td align="left"><a href="#certificateupdate">  &nbsp  Certificate Update  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xC100 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#nullbypass">
                    Null Bypass </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xC101       </td>
            <td align="left"><a href="#nullbypassopen"> &nbsp  Null Bypass Open </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xC102       </td>
            <td align="left"><a href="#nullbypassclose"> &nbsp Null Bypass Close </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xC200 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#polling">
                    Polling </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xC201       </td>
            <td align="left"><a href="#romread"> &nbsp  ROM Read </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xC204       </td>
            <td align="left"><a href="#meteringdatarequest">  &nbsp  Metering Data Request  </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xC300 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#networkinformation">
                    Network Information </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xC301       </td>
            <td align="left"><a href="#parentnodeinfo"> &nbsp  Parent Node Info </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xC302       </td>
            <td align="left"><a href="#parenthopcount"> &nbsp  Parent Hop Count </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xC303       </td>
            <td align="left"><a href="#1-hopneighborlist"> &nbsp  1-Hop Neighbor List </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xC304       </td>
            <td align="left"><a href="#childnodelist"> &nbsp  Child Node List </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xC305       </td>
            <td align="left"><a href="#nodeauthorization"> &nbsp Node Authorization </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#commonoperation">
                    Common Operation
                </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB001       </td>
            <td align="left"><a href="#factorysetting2"> &nbsp  Factory Setting </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB002       </td>
            <td align="left"><a href="#reset"> &nbsp  Reset </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB200 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#time">
                    Time
                </a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB201       </td>
            <td align="left"><a href="#utctime"> &nbsp  Utc Time </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB202       </td>
            <td align="left"><a href="#timezone"> &nbsp  TimeZone </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB300 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#meterinformation2">
                    Meter Information
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB301       </td>
            <td align="left"><a href="#meterserialnumber"> &nbsp  Meter Serial Number </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB302       </td>
            <td align="left"><a href="#metermanufacturenumber"> &nbsp  Meter Manufacture Number </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB303       </td>
            <td align="left"><a href="#customernumber"> &nbsp  Customer Number </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB304       </td>
            <td align="left"><a href="#modelname"> &nbsp  Model Name </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB305       </td>
            <td align="left"><a href="#hwversion"> &nbsp  HW Version </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB306       </td>
            <td align="left"><a href="#swversion"> &nbsp  SW Version </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB307       </td>
            <td align="left"><a href="#meterstatus"> &nbsp  Meter Status </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB308       </td>
            <td align="left"><a href="#lastupdatetime"> &nbsp  Last Update Time </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB309       </td>
            <td align="left"><a href="#lastcommtime"> &nbsp  Last Comm Time </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB30A       </td>
            <td align="left"><a href="#lpchannelcount"> &nbsp  LP Channel Count </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB30B       </td>
            <td align="left"><a href="#lpinterval"> &nbsp  LP Interval </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB30C       </td>
            <td align="left"><a href="#cumulativeactivepower"> &nbsp  Cumulative Active Power </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB30D       </td>
            <td align="left"><a href="#cumulativeactivepowertime"> &nbsp  Cumulative Active Power Time </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>

        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB400 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#electricmeterinformation">
                    Electric Meter Information
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB401       </td>
            <td align="left"><a href="#ct"> &nbsp  CT  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB402       </td>
            <td align="left"><a href="#pt"> &nbsp  PT  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB403       </td>
            <td align="left"><a href="#transformerratio"> &nbsp  Transformer Ratio </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB404       </td>
            <td align="left"><a href="#phaseconfiguration"> &nbsp  Phase Configuration </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB405       </td>
            <td align="left"><a href="#switchstatus"> &nbsp  Switch Status </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB406       </td>
            <td align="left"><a href="#frequency"> &nbsp  Frequency </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB407       </td>
            <td align="left"><a href="#vasf"> &nbsp  VA_SF </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB408       </td>
            <td align="left"><a href="#vahsf"> &nbsp  VAH_SF  </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB409       </td>
            <td align="left"><a href="#dispscalar"> &nbsp DISP_SCALAR   </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB40A       </td>
            <td align="left"><a href="#dispmultiplier"> &nbsp  DISP_MULTIPLIER  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB500 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#meterterminalinformation">
                    Meter Terminal Information
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB501       </td>
            <td align="left"><a href="#primarypowersourcetype"> &nbsp  Primary Power Source Type </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB502       </td>
            <td align="left"><a href="#secondarypowersourcetype"> &nbsp  Secondary PowerSource Type </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB503       </td>
            <td align="left"><a href="#resetcount"> &nbsp  Reset Count</a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB504       </td>
            <td align="left"><a href="#resetreason"> &nbsp  Reset Reason </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB505       </td>
            <td align="left"><a href="#operationtime"> &nbsp  Operation Time </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB506       </td>
            <td align="left"><a href="#resetschedule"> &nbsp   Reset Schedule </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB600 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#communicationinterface">
                    Communication Interface
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB601       </td>
            <td align="left"><a href="#typemain"> &nbsp  Type(Main) </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB602       </td>
            <td align="left"><a href="#typesub"> &nbsp  Type(Sub) </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB700 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#6lowpaninformation">
                    6LoWPAN Information
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB701       </td>
            <td align="left"><a href="#interfaceipv6address"> &nbsp  Interface IPv6 Address </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB702       </td>
            <td align="left"><a href="#ipv6address"> &nbsp  IPv6 Address </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB703       </td>
            <td align="left"><a href="#interfacelistenport"> &nbsp  Interface Listen Port </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xB704       </td>
            <td align="left"><a href="#networklistenport"> &nbsp  Network Listen Port </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xB705       </td>
            <td align="left"><a href="#frequency2"> &nbsp  Frequency </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB706       </td>
            <td align="left"><a href="#bandwidth"> &nbsp  Bandwidth </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB707       </td>
            <td align="left"><a href="#basestationaddress"> &nbsp  Base Station Address  </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB708       </td>
            <td align="left"><a href="#appkey"> &nbsp  APP Key </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB709       </td>
            <td align="left"><a href="#hopstobasestation"> &nbsp  Hops to Base Station </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB70A       </td>
            <td align="left"><a href="#eui64"> &nbsp  EUI 64 </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xB70B       </td>
            <td align="left"><a href="#listenport"> &nbsp  Listen Port </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xB70C       </td>
            <td align="left"><a href="#maxhop"> &nbsp  Max Hop </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB800 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#meterterminaloperationscheduleinformation">
                    Meter Terminal operation Schedule Information
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB801       </td>
            <td align="left"><a href="#meteringschedule"> &nbsp  Metering schedule  </a.</td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xB802       </td>
            <td align="left"><a href="#lpuploadschedule"> &nbsp  LP Upload schedule </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        
        <tr bgcolor="#cccccc">
            <td  nowrap="" valign="top">
                <p align="center">
                    0xB900 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                <a href="#event">
                    Event
                </a></p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xB901       </td>
            <td align="left"><a href="#eventtype"> &nbsp  Event Type </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
 
	</tbody>
</table>

<br>


<p id="action"><h2>1. Action</h2></p>
<p id="resetmodem"><h3>1.1.  Reset Modem</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>

       <tr>
            <td width="1000" colspan="3" align="left"> 
            This is the command to reset the modem. The modem reset is executed immediately upon receiving the command, and there is no response.
In principle, the Reset command should not be used at the same time as other Attribute Ids in the Set Request, since the modem will be reset immediately upon receiving the command.  
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    No request data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="uploadmeteringdata"><h3>1.2.  Upload Metering Data(Metering+Upload)</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            After receiving the request, it transmits the response, performs the metering, and transmits the measured data. The meter reading meter checks all the items in the OBIS List Table of the modem.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload Metering Data Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload Metering Data Response<br>
                    After transmitting the status code, the response frame transmits the last reading data to the Metering Data Frame.
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Upload Metering Data Request

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Upload Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    - If 0, send immediately<br>
                    - Sent when a positive number, after a specified number of seconds
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Number of meters to upload
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Port Address
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1xN
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    - Refer to the Port Address Table for the port address of the meter.<br>
                    - 0xFF: All meters<br>
                    &nbsp * At 0xFF, Meter Count becomes 1.<br>
                    &nbsp * Modem: Use 0xFF if the meter relationship is 1: 1.
                    </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Upload Metering Data Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    See  Status Code
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="factorysetting"><h3>1.3.  Factory Setting</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            When the command is received, all settings are returned to the factory default settings.
When the modem factory reset setting command is received, it is immediately executed, and the modem is reset, so there is no response.
In principle, modem initialization command should not be used at the same time with other attribute id in set request since modem is reset immediately after factory initialization when receiving command.
</tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Factory Setting
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Factory Setting

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Code
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Use "0x0314" as the factory initialization code.<br>
                    If a value other than the corresponding code is received, the factory reset is not executed.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="reauthenticate"><h3>1.4. Re-Authenticate</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            The modem receiving the command will perform the 3-Pass Authentication procedure and the Meter Shared Key issuance procedure again.
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Result Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    See Status Code</p>
            </td>
        </tr>
    </tbody>
</table>

<p id="rollbackimage"><h3>1.5. Rollback Image</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            Used to roll back the firmware image of the modem to the previous version. In the case of the first produced modem, the command is meaningless and it is valid only once the version has been upgraded due to OTA or serial upgrade.</td>
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Image Version
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Rollback Code
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   See Status Code
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Image Version

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Current Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Means the current firmware version. </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Previous Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Means the previous version of the firmware version stored. If there is no previous version, use 0x0000.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Rollback Code

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The rollback code uses: 0x1425</p>
            </td>
        </tr>
    </tbody>
</table>


<p id="watchdogtest"><h3>1.6. Watchdog Test</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            The command to disable the watchdog of the modem is that the modem that received the command becomes a watchdog reset and remains in the modem log. This command is for testing purposes only.
            </td>
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Image Version

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Current Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Means the current firmware version. </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Previous Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Means the previous version of the firmware version stored. If there is no previous version, use 0x0000.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Rollback Code

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The rollback code uses: 0x1425</p>
            </td>
        </tr>
    </tbody>
</table>


<p id="uploadmeteringdata"><h3>1.7. Upload Metering Data(Upload Only)</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            After receiving the request, it transmits the response and transmits the stored meter reading data.
The type of the meter reading data to be transmitted is all the interval data to be measured by the modem, and the number to be transmitted differs according to the parameter of the request. However, since the same number is applied to all the interval data of the modem, one interval data is uploaded when one data is requested.
When both the Offset and Count of the request are 0, the modem uploads data of the basic push operation.
- Basic push operation data: Data that modem does not yet upload
</td>
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Upload Response<br>
                    After transmitting the status code, the response frame transmits the inquiry data requested by the Metering Data Frame.
                </p>
            </td>
        </tr>
    </tbody>
</table>
  
<p>Upload Request

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Option
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 = Offset / Count ignored. Upload data of basic push operation.<br>
                    1 = Offset / Count applied. 
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Offset
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Offset of the meter reading data index to be uploaded.0 means the most recent.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The number of data to be read from the offset.<br>
                    Ex) Offset 0, Count 1: 1 most recent data.<br>
                    Ex) Offset 4, Count 5: 5 most recent data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Delay Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">   
               <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    A value that specifies whether to upload data after a few seconds.
The unit is 'seconds'.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Upload Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    See Status Code</p>
            </td>
        </tr>
    </tbody>
</table>


<p id="realtimemetering"><h3>1.8. Real Time Metering</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            After receiving the request, send the response, and if the response is successful, upload the data by measuring in real time. At this time, the data going to the response goes directly to the HES. Immediately after receiving the command, it executes the real time meter reading once, and operates according to the set parameter value.
            <br>Ex) Interval 60, Duration 10 When setting: Meter reading for the first time / Meter reading / uploading every 60 seconds after upload is repeated for 10 minutes.
The real-time meter reading values are as follows.
<br>1. Active energy import (+ A): Class 3, OBIS 1.0.1.8.0.255, Attr 2
<br>2. Active energy Export (-A): Class 3, OBIS 1.0.2.8.0.255, Attr 2
<br>3. Reactive energy import (+ R): Class 3, OBIS 1.0.3.8.0.255, Attr 2
<br>4. Reactive energy export (-R): Class 3, OBIS 1.0.3.8.0.255, Attr 2
<br>5. Instantaneous voltage L1: Class 3, 1.0.32.7.0.255, Attr 2
<br>6. Instantaneous voltage L2: Class 3, 1.0.52.7.0.255, Attr 2
<br>7. Instantaneous voltage L3: Class 3, 1.0.72.7.0.255, Attr 2
</td>
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Real Time Metering Command
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Real Time Metering Command Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Real Time Metering Command

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Interval
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The unit is 'seconds' and the minimum unit is 60 seconds.
                <br>Ex) When set to 60, read the real time reading value every 60 seconds and upload.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Duration
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Time to perform real-time meter reading in 'minutes' and maximum duration is 10 minutes.
                <br>Ex) If set to 10, read the real-time meter reading value for every interval for 10 minutes and upload.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Real Time Metering Command Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : Success
                <br>1 : Fail : Invalid interval
                <br>2 : Fail : Invalid Duration
                <br>3 : Fail : Meter key not received yet.
                <br>4 : Fail : Real Time Metering already in process.
                <br>5 : Fail : Other reason
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="cloneonoff"><h3>1.9. Clone On/Off</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            This command turns on / off the Clone function of the modem. Optional items are all or none.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Clone Configuration
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Clone Configuration
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Clone Configuration
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Clone Configuration

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Clone Code
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    If the clone code is correct, execute the command for clone on / off.
                    The behavior depends on the code.
                <br>- 0x0314: Use clone's own image (auto-propagation X)
                <br>- 0x0315: Use clone's own image (auto propagation O)
                <br>- 0x8798: Use clone system image (automatic radio X)
                <br>- 0x8799: Use clone system image (auto propagation O)
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Clone Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    It means the time value for executing the clone, and the unit is 15 minutes.
                <br>・The value is valid when the value is between 0 and 20 ~ 96. (Otherwise, the error is processed)
                <br>Ex) Clone operation for 24 hours when set to 96
                <br>If you want to terminate the clone, give a value of 0.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Manual Version
                <br>(Optional)
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    When you specify a target and clone it, you can manually give the firmware version. If the modem has the same version, it will resume and if it is different, it will proceed from the beginning.
                <br>If you want to target all modems without manual designation, use the corresponding value as 0x0000.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Manual EUI Count
                <br>(Optional)
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Up to 20 fields can be used to clone a target.
                <br>If you want to target all modems without manual setting, set the value to 0.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Manual EUI Table
                <br>(Optional)
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    This field is used when Clone is specified by the target, and the EUI 8 bytes of the target modem are repeated as much as the Manual EUI Count.
                <br>If you want to target all modems without manual designation, do not give a value in the corresponding field.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="information"><h2>2. Information</h2></p>

<p id="modeminformation"><h3>2.1. Modem Information</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
            It is a command to read the modem basic information setting value.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Information Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Information Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   EUI ID
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    EUI ID of the modem
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Modem Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x00: Coordinator modem
                <br>0x01: Built-in modem (standard)
                <br>0x02: External E-type modem
                <br>0x03: G-type
                <br>0x04: Water Modem
                <br>0x05: Gas Modem
                <br>0x10: Iraq NB-PLC Modem
                <br>0x20: S-Project RF Coordinator Modem
                <br>0x21: S-Project RF Router Modem
                <br>0x22: S-Project MBB Modem
                <br>0x23: S-Project Ethernet Modem
                <br>0x24: S-Project Dongle Modem
                <br>0x25: S-Project RF Router PANA Modem
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Modem Reset Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem reset time can be set from 0 to 23 hours as a unit of time
                <br>In case of 0xFF, modem reset is not performed.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Node Kind
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    20
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Node Kind information of the modem. (ASCII)
                               
<!--a table in a table -->               
<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
         <tr>
            <td width="300" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Site</strong>
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Values</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Iraq <br> NB-PLC
                </p>
            </td>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter modem
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                    NAMR-C402PG
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    Repeater modem
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NZR-O121PX
                </p>
            </td>
        </tr>
        <tr> 
            <td width="130" nowrap="" rowspan="4" valign="center">
                <p align="center">
                    KEPCO <br> 900Mhz RF
                </p>
            </td>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    coordinator
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                    NCB-E101
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    G-Type
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P206SR
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    E-Type
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P207SR
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    S-Type
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P209SR
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    KEPCO <br> G3-PLC
                </p>
            </td>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    E-Type
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-C102SL_EXT
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    S-Type
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-C102SL
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="5" valign="center">
                <p align="center">
                    S-Project
                </p>
            </td>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    RF Coorrginator
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NCB-S201
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    RF Router
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P214SR
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    MBB
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P117LT
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    Ethernet
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NAMR-P212ET
                </p>
            </td>
        </tr>
        <tr>
            <td width="170" nowrap="" rowspan="" valign="center">
                <p align="">
                    Dongle
                </p>
            </td>
            <td width="400" nowrap="" rowspan="" valign="center">
                <p align="">
                   NCB-DG201
                </p>
            </td>
        </tr>
    </tbody>
</table>                
<!-- end-->
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    F/W Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    About F / W Version
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Build Number
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                   Build Number of F / W
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    H/W Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    About H/W Version
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Modem Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="3" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x00 : Idle
                    </p>
            </td>
        </tr>
        <tr>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x01 : Meter Reading
                </p>
            </td>
        </tr>
        <tr>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x02 : Firmware Upgrade
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Modem Mode
                </p>
            </td>
            <td width="" nowrap="" rowspan="2" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x00 : Push Mode
                    </p>
            </td>
        </tr>
        <tr>
            <td width="550" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x01 : Poll(Bypass) Mode
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="modemstatus"><h3>2.2. Modem Status</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            This is the current status information of the modem.
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Status Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Status Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    0x00 : Idle
                <br>0x01 : Meter Reading
                <br>0x02 : Firmware Upgrade
                <br>0x03～0xFF : Reserved
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="meterinformation"><h3>2.3. Meter Information</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              It is information about meter.
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter Info Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Meter Info Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="130" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="690" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Comm Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="690" nowrap="" rowspan="" valign="center">
                <p align="">
                Meter Communication Status
                <br>0x00 : Normal
                <br>0x01 : Meter not responding
                <br>0x02 : Meter communication protocol error
                </p>
            </td>
         </tr>
         <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="690" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter Count connected to the modem
                </p>
            </td>
         </tr>
         <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Serial
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    20xMeterCount
                </p>
            </td>
            <td width="690" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter Serial List.
                </p>
            </td>
         </tr>
    </tbody>
</table>


<p id="modemeventlog"><h3>2.4. Modem Event Log</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                  Modem Event Log
            </td>
       </tr>
        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                   <p align="">
                    Modem Event Log Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Event Log Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Event Log Request 

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Event Log Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                Event Log The number of logs to read from Offset.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Event Log Offset
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The offset value of the log to read.
                   <br> where 0 means the most recent index. If there is no value in the field, it is always recognized as offset 0. 
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p>Modem Event Log Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Event Log Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Number of event logs read
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" nowrap="" rowspan="4" valign="center">
                <p align="center">
                    Event <br>Log <br>Data
                </p>
            </td>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Index
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Eent Log Index
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    7
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Event Log Time(0xYYYYMMDDhhmmss)
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Event Code
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Event Code
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Event Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Event Data
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="modemdtlshandshakestatus"><h3>2.5. Modem DTLS Handshake Status</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              DTLS This command reads or sets the handshaking status information.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Handshake Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Handshake Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Handshake Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Handshake Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   DTLS<br> Handshake Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Get : 
                <br> 0 : No handshaking
                <br> 1 : Completed handshake
              <br> Set :
                <br> 1 : command to take handshake again
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="fwimageinformation"><h3>2.6. FW Image Information</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                 It is used to read the firmware image information stored in the modem.
                 It is mainly used to check if the image has been properly received.
            </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   FW Image Info
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>FW Image Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="280" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    My Image Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the total size of the image received from the parent.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    My Image CRC
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the CRC of the image received from the parent.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    My Image Received Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the size of the image that I received so far.
                    If this value is equal to My Image Size, it means that the entire image has been received.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    My Image Sequence
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    Sequence of the image received from the parent and mainly uses the FW Version value. </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Other Device Model Name
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    20
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the model name of other equipment image that has been received from the top.
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Other Device Image Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                     It is the total size of other equipment image delivered from the top.</p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Other Device Image CRC
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is CRC of other equipment image delivered from the upper part.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Other Device Image Received Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                     It is the size of the image of other equipment that has been delivered so far.
                     If this value is equal to My Image Size, it means that the entire image has been received.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Other Device Image Sequence
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="650" nowrap="" rowspan="" valign="center">
                <p align="">
                    Sequence of other equipment image transmitted from the upper side and mainly uses FW Version value.
                </p>
            </td>
        </tr>
     </tbody>
</table>


<p id="configuration"><h2>3. Configuration(Common)</h2></p>
<p id="modemtime"><h3>3.1. Modem Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    Modem current time information.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Time Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Time Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Time Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Year    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       year    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Month    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       month    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Day    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       day    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Hour    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       hour    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Minute    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       minute    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Second    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       second    </p>
            </td>
        </tr>
         </tbody>
</table>

<p>Result Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="modemresettime"><h3>3.2. Modem Reset Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    The modem reset time setting command is performed based on the current time of the modem.
                </p>
            </td>
      </tr>
       <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem Reset Time Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Reset Time Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Reset Time Request

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Modem Reset Time    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">         
                    You can set the time from 0 to 23 hours,
                    If it is 0xFF, do not reset the modem.
                </p>
            </td>
        </tr>
     </tbody>
</table>

<p> Modem Reset Time Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>

<p id="modemmode"><h3>3.3. Modem Mode</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    This is a command to change the mode of the modem. Push Mode Pushes the meter reading data according to the schedule of the modem,
Modem Schedule does not work in Poll Mode.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Modem Mode Request 
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem Mode Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Mode Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Mode Request

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Modem Mode   </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                     0x00: Push Mode (default setting value)
                 <br>0x01: Poll (Bypass) Mode (default setting value for SORIA)
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Modem Mode Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="meteringinterval"><h3>3.4. Metering Interval</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    It is a meter reading meter.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Metering Interval Data 
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Metering Interval Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Metering Interval Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Metering Interval   </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                     Metering Interval setting value. (Second)
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Result Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>

<p id="modemtxpower"><h3>3.5. Modem TX Power</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    This command changes the TX Power in the modem.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  TX Power Data 
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    TX Power Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Metering Interval Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> TX Power    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                     This field sets the RF Power value of the modem (Signed Value)
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Result Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="formjoinnetwork"><h3>3.6. Form/Join Network</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    When a command to form / join the network is received,
                     the network is formed (coordinator) / join (modem) with the corresponding parameter after resetting.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Form/Join Network Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  &#10005; 
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Metering Interval Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Channel    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                      Automatic forming / joining when set to 0 for the channels of the foaming / joining network
                      (Not meaningful in the Soria project where 6top is applied)  
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Pan ID   </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                       When the fan ID of the network to be formed / join is set to 0, the coordinator generates randomly and automatically joins the nodes
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="networkspeed"><h3>3.7. Network Speed</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    It is a command to change the speed of the network. 
                    After changing the set value, the modem is reset.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Network Speed
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Network Speed
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Network Speed 

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Network Speed    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                     Network Speed setting value.
                     <br>- 4.8 Kbps: 1
                     <br>- 38.4 Kbps: 2
                     <br>- 50 Kbps: 3
                     <br>- 100 Kbps: 4
                     <br>- 150 Kbps: 5
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Result Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">     Refer to  Status Code    </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="modemipinformation"><h3>3.8. Modem IP Information</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               The device sets or reads the IP address of the communication target.
               Usually, Ipv6 address is used for RF modem and Ipv4 for MBB / Ethernet modem (MBB / Ethernet can be changed to Ipv6).
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Get IP format
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IP Format
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    IP Format
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IP Format
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Get IP Format

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Target Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
                               
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   DCU (RF_Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    HES
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    SNMP
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NTP (MBB Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem (Ethernet Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
       <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    IP Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv4
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    IPv6
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
     </tbody>
</table>

<p>IP Format

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Target Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
                               
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   DCU (RF_Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    HES
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    SNMP
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NTP (MBB Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem (Ethernet Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
       <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    IP Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv4
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    IPv6
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    IP Address
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    According to the IP type, it is as follows.
                    <br>IP Type = 0 : IPv4 Format.
                    <br>IP Type = 1 : IPv6 Format.
                 </p>
              </td>
        </tr>
    </tbody>
</table>

<p>IPv4 Format

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Ipv4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   4 bytes are used as the Ipv4 value.
                   <br>Ex) IP is 187.1.30.141 -> 0xBB011E8D
                </p>
            </td>
        </tr>
    </tbody>
</table>                             
     
<p>IPv6 Format

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Ipv6
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    16
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   16 bytes are used for the IPv6 value.
                </p>
            </td>
        </tr>
    </tbody>
</table>                             
     

<p id="modemportinformation"><h3>3.9. Modem Port Information</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               Set or read the port of the target device to communicate with.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Get Port format
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port Format
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Port Format
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port Format
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Get Port Format


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Target Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
                               
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   DCU Server (only supports RF_Modem)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    DCU Client (only supports RF_Modem)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    HES Srver
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    HES Client
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    hes Auth
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    SNMP
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    6         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Coap
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     7          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NI
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     8          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NTP (MBB Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    9          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem (Ethernet Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     10~255
                         </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
     </tbody>
</table>

<p>Port Format

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Target Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The types are as follows.
                               
              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Security DCU Server (only supports RF_Modem)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Security DCU Client (only supports RF_Modem)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Security HES Srver
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Security HES Client
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Hes Auth
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    SNMP
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    6         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Coap
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     7          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NI
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     8          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    NTP (MBB Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    9          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem (Ethernet Modem only)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     10~255
                         </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port
                 </p>
              </td>
        </tr>
    </tbody>
</table>


<p id="alarmeventcommandonoff"><h3>3.10. Alarm/Event Command ON_OFF</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               It is ON / OFF function of Alarm / Event Command.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Alarm / Event Command Request
                    <br>(However, when count is 0x00, all commands are responded.)
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Alarm / Event Command 
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Alarm / Event Command 
                     <br>(However, if count is 0x00, all alarm / events are turned off and there is no command data.
If count is 0xFF, all alarm / events are turned on and there is no command data.)
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Alarm / Event Command 
                    <br>(However, if the count at the time of request is 0x00 or 0xFF, it gives all the alarm / events that the modem has in response.)
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Alarm/Event Command Request


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Command to change settings
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Command
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2xN
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Command to change settings
                                            
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Field</strong>
                 </p>
               </td>
                <td width="100" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Byte</strong>
                 </p>
               </td>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Alarm/Event Type ID         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    2         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Alarm/Event Type ID
                </p>
              </td>
            </tr>
          </tbody>
          </table>                
        </tr>
     </tbody>
</table>


<p>Alarm/Event Command

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Command to change settings
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Command
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    3xN
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Command to change settings
                                            
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Field</strong>
                 </p>
               </td>
                <td width="100" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Byte</strong>
                 </p>
               </td>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Alarm/Event Type ID         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    2         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Alarm/Event Type ID
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Status         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    1         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   0x00 : Off
                   <br>0x01 : On
                   <br>0x02 : Read/Write Failed
                </p>
              </td>
            </tr>
          </tbody>
          </table>                
        </tr>
     </tbody>
</table>


<p id="meterbaud"><h3>3.11. Meter Baud</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    It is the communication speed of the serial interface communicating with the meter.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Baudrate
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Baudrate
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Baudrate
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Baudrate

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Baudrate    </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 4       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">   Meter communication speed   <br>Kaifa default: 38400    </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="transmitfrequency"><h3>3.12. Transmit Frequency</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    When the modem is in the push mode, it is a cycle to upload the meter reading data. 
                    The factory setting value is set equal to the Metering Interval.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Transmit Frequency Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Transmit Frequency Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Result Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Transmit Frequency Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Transmit Frequency  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">  Transmit Frequency setting value. (Second)    </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Result Status>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2       </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">  Refer to Status Code   </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="retrycount"><h3>3.13. Retry Count</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    If the modem is in Push Mode, it is the number of times to retry in case of failure to push the meter reading data.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Retry Count
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Retry Count
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Retry Count
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Retry Count

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Retry Count  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align=""> Retry count   </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="snmptraponoff"><h3>3.14. SNMP Trap ON_OFF</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    This command enables / disables the SNMP Trap of the modem. (Cycle is 3600 seconds)
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  SNMP Trap Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    SNMP Trap Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   SNMP Trap Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>SNMP Trap Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Status  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     0 : SNMP Trap OFF
                     <br> 1 : SNMP Trap ON
                </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="rawromaccess"><h3>3.15. Raw ROM Access</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    It is the ability to write arbitrary data to a specific ROM address of the modem.
                    Failure to do so may cause the modem to malfunction, so use caution when using it.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    ROM information
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  ROM Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   ROM Data
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   ROM Data
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>ROM Information

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Address  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 4      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It means the address of the ROM to read.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Read Length  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Means the size of the data area to be read from the address.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p>ROM Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Address  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 4      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It means the ROM address of Data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Data Length  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It means the size of the data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Data  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> N      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Raw data that is written to or read from the ROM.
                </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="networkcommunicationtest"><h2>4. Network Communication Test</h2></p>
<p id="testconfiguration"><h3>4.1. Test Configuration</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                <p align="">
                    This is the setting information for testing the communication status.
                </p>
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  INfomation
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   data Configuration
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Infomation
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Information

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> EUI64  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 8      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It is the EUI64 of the modem to test.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Data  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 4     </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Data Configuratin
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p>Data Configuration

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Data Interval  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     The unit is 'seconds' as the period for sending test data.
                     When the value is set to 0, it is not sent periodically.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Data Count  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It is the number of data sent at once when sending test data periodically.
                     When sending periodically, always send the latest value first.
                     For the format of data, refer to  <a href="#testdataupload"> &nbsp Test Data Upload   </a>.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="testdataupload"><h3>4.2. Test Data Upload</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to request data at communication test.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Get Info
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   In response to Get, we use the Dummy Meter frame
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Get Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Offset
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The offset of the test data to read, 0 means the most recent value.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The number of test data to read.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="schedulecontrol"><h2>5. Schedule Control</h2></p>
<p id="modemschedulerun"><h3>5.1. Modem Schedule Run</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to run the modem's schedule. 
               These commands are schedules that automatically operate according to the schedule of the modem and do not operate other than the existing operation.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Schedule Info
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Status Code
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Schedule Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    See Modem Schedule Type below
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The value is set to 'seconds' to set the schedule to run after a few seconds.
                    When set to 0, the corresponding schedule is turned off.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Modem Schedule Type

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="120" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Type</strong>
                </p>
            </td>
            <td width="300" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Name</strong>
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Read meter serial
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Reads the meter's serial number.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Three pass authentication
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    3-pass authentication is executed.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    3
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter shared key
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Request the meter key to the HES.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Metering
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Perform meter reading.
                </p>
            </td>
        </tr>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    5
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Basic metering data upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload the meter basic information to the top.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    6
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    LP metering data upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload LP data to upper level (DTLS application)
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    15
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Polling metering data upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Uploads the response data for the polling command to the upper side.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    16
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Timesync with HES
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Time synchronization with HES.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    17
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Modem info upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload the modem information to the parent.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    19
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    FW image backup
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Compares the current firmware version with the firmware version stored in ROM and backs up if it matches.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   22
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Delayed SMS proc
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    MBB modem SMS processor
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    23
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Real time metering
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Real-time meter reading is executed.
                </p>
            </td>
        </tr>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    24
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Real time metering upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload the real-time meter reading data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    29
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    PANA authentication
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    PANA authentication is executed.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    30
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    MAC data upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload MC meter reading data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    31
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter timesync
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Synchronize the meter with modem time.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    32
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter power quality set
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Set the power quality load profile period of the meter to 24 hours.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    33
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Device_ID4 Set
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    The operation of planting the EUI of the modem on the meter.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    34
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    UDP Upload
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Upload LP data to the top (DTLS not used)
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    35
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Event Poll
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem dedicated command
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    36
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter LP Recover
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Modem dedicated command
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    37
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Handshake status
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Commands to request a concentrator for DTLS session state
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    38
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    MSK to DCU
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Command to request the concentrator MSK
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    39
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter OTA
                </p>
            </td>
            <td width="580" nowrap="" rowspan="" valign="center">
                <p align="">
                    Command to upgrade your meter to your current image
                </p>
            </td>
        </tr>
     </tbody>
</table>



<p id="modeminstall"><h2>6. Modem Install</h2></p>
<p id="joinbackofftimer"><h3>6.1. Join Backoff Timer</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              The time in seconds that the modem will wait before being reconnected, in seconds.
               Even if the join fails, the join is attempted after the waiting time.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Backoff Timer
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Backoff Timer
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Backoff Timer
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Backoff Timer

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Join Timer
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   FEP to verify MAC process to pass the physical join. 
                   At this time, this timer is used to place a limit on the modem that has not passed the Verify MAC procedure (a restriction on frequent join attempts).
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="authbackofftimer"><h3>6.2. Auth Backoff Timer</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              The unit is 'seconds' after the modem is joined, waiting for the authentication procedure to be performed.
               Even if the authentication procedure fails, it waits for the time and retries.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Backoff Timer
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Backoff Timer
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Backoff Timer
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Backoff Timer

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Auth Timer
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   After physically joining, use this timer to reduce the load on the network for frequent Auth.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="metersharedkey"><h3>6.3. Meter Shared Key</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              It is a command to set or fetch Meter Shared Key for HLS.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    MSK Request Info
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   MSK
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    MSK
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   MSK Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>MSK Request Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request Info
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   0: Used when requesting DCU / HES  Modem. At this time, the following data (Modem EUI, Meter Serial Size, Meter Serial) is not included.<br>
                   1: 3PASS Modem  Used when requesting with HES. At this time, request the following data (Modem EUI, Meter Serial Size, Meter Serial). In case of 3PASS modem, the response is transmitted in encrypted form (one key size 64 bytes).<br>
                   2: PANA Modem  Used when requesting to HES. At this time, request the following data (Modem EUI, Meter Serial Size, Meter Serial). In the case of PANA modem, it transmits the United States without encrypting (one key size 16 bytes).

                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Modem EUI
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem's EUI64
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Serial Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter Indicates the size of the serial.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Serial
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is the serial number of the meter.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>MSK(3PASS)

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="80" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Master_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    64
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    HLS-Secret-Unique
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    64
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Global_encryption_unicast_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   64
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Authentication_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    64
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>MSK(PANA)

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="80" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Master_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    16
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    HLS-Secret-Unique
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    16
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Global_encryption_unicast_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   16
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Authentication_key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    16
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key used in HLS. If there is no key, all zeros are filled.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>MSK Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Refer to Status Code
                </p>
            </td>
        </tr>
      </body>
   </table>

<p id="certificateupdate"><h3>6.4. Certificate Update</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              This is the command used to get or update the certificate information. 
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Cert Info
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Cert Info 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Cert Info Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Cert Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Cert Payload Length 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Cert The length of the payload.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Cert Payload 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is certificate information (detailed format will be defined later)
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Cert Info Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   0: Success. This means that the certificate has been changed normally.<br>
                   1: Fail. It means the certificate change failure.
                </p>
            </td>
        </tr>
      </body>
   </table>


<p id="nullbypass"><h2>7. Null Bypass</h2></p>
<p id="nullbypassopen"><h3>7.1. Null Bypass Open</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              Used when requesting null bypass.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Bypass Request 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Port Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   0x0000: A null-bypassable state (Get Request). Set Bypass Success.<br>
                   0x0001 ~ 0xFFFF: This means that the system is proceeding null bypassing to the corresponding port from another place. In this case, it is impossible to bypass null bypass.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Bypass Request

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   This is the port on which to perform null bypassing.<br>
                   Default: 65300
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Timeout 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The timeout interval between each frame when null bypassing is in units of ms.<br>
                   Ex) Set to 5000 → Apply 5 second timeout every frame when null bypassing.<br>
                   Default: 30000
                </p>
            </td>
        </tr>
      </body>
   </table>


<p id="nullbypassclose"><h3>7.2. Null Bypass Close</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
              Used to request null-bypass termination. 
              When bypass is terminated by Timeout without receiving any command, Close Status is transmitted by Trap Response.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Port
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Close Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Close Status 

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   0: Terminated gracefully by command<br>
                   1: Termination of bypass due to timeout<br>
                   2: Port Mismatch</p>
            </td>
        </tr>
    </tbody>
</table>

<p>Port

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  This is the port to exit bypass.
                </p>
            </td>
        </tr>
      </body>
   </table>



<p id="polling"><h2>8. Polling</h2></p>
<p id="romread"><h3>8.1. ROM Read</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               It is an instruction to read the meter reading data stored in the ROM of the modem.
               </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    polling Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Polling Response
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Polling Request


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    The type of polling is different depending on the type of data requested and responding.
                              
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Raw ROM Read
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Basic Metering Data Read

                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Interval Metering Data Read (LP, Log, Event, ...)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Interval Metering Data Read (LP, Log, Event, ...) with timestamp
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
       <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Data
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    According to the above type, it is divided as follows.
                
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Poll Address
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    X
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Poll Information(Interval)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Poll Information(Timestamp)
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
     </tbody>
</table>


<p>Poll Address


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Start Address
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Means the start address of the ROM area to be read.
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Read Size
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Means the size of the ROM area to read from the start address.
                </p>
            </td>
        </tr>
    </tbody>
</table>                             
     

        
<p>Poll Information(Interval)

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Total Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means the total number of Poll Data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Poll Data 
                </p>
            </td>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It refers to the type of reading data to read and refers to Poll type.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Offset
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Means the start offset of the reading data to be read. (0 is the most recent)
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means the number of meter reading data to read from Offset, and reads the past data from offset.
                   <br>Ex) offset 0, count 3 -> Get the three most recent values.
                </p>
            </td>
        </tr>
        
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   ...
                </p>
            </td>
        </tr>
    </tbody>
</table>

        
<p>Poll Information(Timestamp)

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Total Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means the total number of Poll Data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Poll Data 
                </p>
            </td>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It refers to the type of reading data to read and refers to Poll type.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Start Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    7
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means the start time of the meter reading data to be read. 
                   The Start Time must be past the End Time. For the time format, see End Time below.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    End Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    7
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Means the end time of the reading data to be read. The Start Time must be past the End Time. The time format is as follows.
                
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Field</strong>
                 </p>
               </td>
                <td width="100" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Byte</strong>
                 </p>
               </td>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Day         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    1         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Day
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Hour         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    1         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                  Hour
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Minute         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    1         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Minute
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">   Second         </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    1         </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                  Second
                </p>
              </td>
            </tr>
          </tbody>
          </table>                
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   ...
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Poll Type

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
       <tr>
         <td width="180" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
             <p align="center">
                <strong>Value</strong>
             </p>
         </td>
         <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
             <p align="center">
                <strong>Description</strong>
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    1         </p>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                DLMS Load profile
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    2          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 M-BUS Data 1
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">     3          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 Power Quality Profile
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">     6          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 DLMS Standard events
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    7         </p>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                DLMS Control logs
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    8          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 DLMS Power failure logs for single-phase/poly-phase
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">     9          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 DLMS Power quality logs
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">     10          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                 DLMS Tampering logs
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    11         </p>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                DLMS Firmware upgrade logs
             </p>
         </td>
       </tr>
       <tr>
         <td width="180" nowrap="" rowspan="" valign="center">
             <p align="center">    12~255          </p>
         </td>
         <td width="500" nowrap="" rowspan="" valign="center">
             <p align="">
                Reserved
             </p>
         </td>
       </tr>
   </tbody>
</table>                


<p>Polling Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Means the type of data read.
                             
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Raw ROM Read
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Basic Metering Data Read
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Interval Metering Data Read(LP, Log, Event, …)
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Interval Metering Data Read(LP, Log, Event, …) with timestamp
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
       <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Data
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    According to the above type, it is divided as follows.
               
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Value</strong>
                 </p>
               </td>
               <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     0         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Poll ROM Data
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Poll Basic Data
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Poll Interval Data
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Poll Interval Data
                </p>
              </td>
            </tr>
            </tbody>
            </table>                
        </tr>
     </tbody>
</table>


<p>Poll ROM Data


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Length
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Means the length of the data.
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Data
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Read ROM data.  
                </p>
            </td>
        </tr>
    </tbody>
</table>                             
     

<p>Poll Basic Data


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Meter Serial
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    20
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  The serial number of the meter
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Length 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Length of Basic Meter Data 
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Basic Meter Data
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  Metering Data Frame's DLMS Meter Format
                </p>
            </td>
        </tr>
   </tbody>
</table>                             
     
        
<p>Poll Interval Data

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Total Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means the total number of Poll Data.
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Poll Data 
                </p>
            </td>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It refers to the type of data to be read and refers to Poll type.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Length
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Means the size of the meter reading data (Data) read.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The data format is as follows according to Poll Type.
                
                <table border="1" cellspacing="0" cellpadding="0" width="0">
                <tbody>
                <tr>
                <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                  <p align="center">
                     <strong>Poll Type</strong>
                  </p>
                </td>
                <td width="500" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                  <p align="center">
                    <strong>Format</strong>
                  </p>
                </td>
                </tr>
               <tr>
               <td width="200" nowrap="" rowspan="" valign="center">
                  <p align="center">     1~11        </p>
                  <td width="500" nowrap="" rowspan="" valign="center">
                  <p align="">
                      Metering Data Frame's DLMS Meter Format
                  </p>
               </td>
               </tr>
               <tr>
               <td width="200" nowrap="" rowspan="" valign="center">
                 <p align="center">     12~255          </p>
               </td>
               <td width="500" nowrap="" rowspan="" valign="center">
                 <p align="">
                    reserved
                 </p>
               </td>
               </tr>
               </tbody>
               </table>                
           </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ...
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   ...
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="meteringdatarequest"><h3>8.2. Metering Data Request</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               It is a command for requesting the meter reading data (Energy Load Profile) stored in the modem.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Metering Data Request
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Metering Data Result 
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Metering Data Request


<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="150" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Header Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   See Header Type.
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Metering Data Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   See Metering Data Type
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Param Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                   Indicates the number of Params. The maximum number is 24.
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Parameter
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    6 X Param Count
                </p>
            </td>
            <td width="670" nowrap="" rowspan="" valign="center">
                <p align="">
                    These are the time values of the metering data requested.
                     For the efficiency of the logic to search the data, the order of the data entering the parameter comes later, with the most recent value being the first.
                                            
             <table border="1" cellspacing="0" cellpadding="0" width="0">
             <tbody>
             <tr>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Size</strong>
                 </p>
               </td>
                <td width="100" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Index</strong>
                 </p>
               </td>
               <td width="200" colspan="" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                 <p align="center">
                    <strong>Description</strong>
                 </p>
               </td>
             </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    2        </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    0        </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Year
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    1        </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    2        </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Month
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    1        </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    3        </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Day
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    1        </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    4        </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Hour
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    1        </p>
                <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">    5        </p>
              <td width="3" nowrap="" rowspan="" valign="center">
                <p align="">
                   Minute
                </p>
              </td>
            </tr>
          </tbody>
          </table>                
        </tr>
     </tbody>
</table>


<p>Meeting Data Response

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   See Status Code
                 </p>
              </td>
        </tr>
        <tr>
           <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response Data
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Only exists when Status is Success.
                    In response to the item of the Metering Data Request, the format is the same as the Metering Data Frame.
                 </p>
              </td>
        </tr>
     </tbody>
</table>



<p id="networkinformation"><h2>9. Network Information</h2></p>
<p id="parentnodeinfo"><h3>9.1. Parent Node Info</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               The parent node information of the object.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Node Information
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Node Information 

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Index 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   This is the index of the neighbor table of the modem.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    EUI 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Use EUI64 as the unique identifier of the modem.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem type on the network.
                   <br>0 : Coordinator
                   <br>1 : Router
                   <br>2 : Host
                   <br>3 : Sleepy Host
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    State 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Current status on the network.
                   <br>0: None (communication impossible)
                   <br>1: Neighbor Discovery Incomplete
                   <br>2: Neighbor Discovery Probe (communication impossible)
                   <br>3: Neighbor Discovery Delay (communication impossible)
                   <br>4: Stale (Tx only for that node)
                   <br>5: Reachable (Tx Rx all possible)
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Distance 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The number of hops of a node.
                   <br>0x0001: Coordinator
                   <br>0x01xx: 1 hop node
                   <br>0x02xx: 2-hop node
                   <br>0x03xx: 3-hop node
                   <br>...
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ETX 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Estimated Transmit Count.
                   <br>The expected number of Tx times to communicate is determined by a formula.
                   <br>((Previous ETX value * aging value) + current ETX value * (100-aging value)) / 100;
                   <br>The aging value can be changed by setting the default value is 99.
                   <br>The first ETX value is 0x0100. As the communication fails, the ETX value gradually increases and gradually drops down (it does not go below 0x0100). 
                   If the ETX value of the parent node becomes 0x0200, the communication environment is considered to be bad and the other parent node is searched. 
                   When the ETX value reaches 0x0300, the node is blacklisted.
                   <br>Based on the default aging value of 99, when the communication fails 29 times consecutively, 0x0200 is exceeded, and the parent node is changed.
               </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Lifetime 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The lifetime of the target node is updated to 11700 for every communication and decreases by 1 every second. 
                   When Lifetime reaches 0, the node is deleted from the table.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Dodag_ver 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The current version of the network has the same value for all nodes including the coordinator.
                    If the Dodag_ver of the coordinator increases, the whole network regenerates the routing path.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    dtsn 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Destination Advertisement Trigger Sequence Number.
                   This value is used in the network to maintain the routing path.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tx_cnt 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is the number of the modem and MAC tx.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tx_fail 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is the number of the modem and MAC tx.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Rx 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is the number of MAC rx received from the modem.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    rssi
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The value of rssi with the modem is negative.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    lgi 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                  This is the lqi value of the modem.
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="parenthopcount"><h3>9.2. Parent Hop Count</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               The parent node information of the object.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Hop Count
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Hop Count

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hop Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It has the same meaning as Distance of Node Information.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="1-hopneighborlist"><h3>9.3. 1-Hop Neighbor List</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               It is a table for itself and the nodes within a one-hop communication distance.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Request Info 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Neighbor List
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Request Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Start Index 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The starting index to read from the list.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Start Index is the number of nodes to read from.
                   <br>If the Start Index is 0 and the value is 0xFFFF, the whole table is read.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Neighbor List</p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Neighbor Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The number of node information read.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Node
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Contains Node Information as much as Neighbor Count.
                   (See parent node info)
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="childnodelist"><h3>9.4. Child Node List</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               It is a table for itself and the nodes within a one-hop communication distance.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Request Info 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Child List
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Request Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Start Index 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The starting index to read from the list.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Start Index is the number of nodes to read from.
                   <br>If the Start Index is 0 and the value is 0xFFFF, the whole table is read.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Child List

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Child Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Child Information.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Child Index 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Child INformation
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Child Information is as much as Child Count.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Child Information

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Destnation 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   EUI64 of the target child node.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Next Node
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Destination The EUI64 of the node to send to the node.
                   <br>If Destination and Next Node are the same, it means that they are communicating directly with the Destination Node.
                   If they are different, Destination Node means the child node of Next Node.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Life Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The lifetime of the target node is updated to 11700 for each communication and decreases by 1 every second.
                    When Lifetime reaches 0, the node is deleted from the table.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Path Sequence
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is incremented by 1 each time the target node and path are updated.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                   Updated
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   This field indicates whether the node has been normally updated in the current update period. 0 means no update and 1 means update.
                    Even if this cycle is not renewed, there will be no problem in operation if the next cycle is renewed.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="nodeauthorization"><h3>9.5. Node Authorization</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               Only the Coordinator has the information in response to the node's authentication status.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Authorization Request Table 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Authrization List
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Authorization List 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Authrization List
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Authorization Request Table

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="140" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="680" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Node Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="680" nowrap="" rowspan="" valign="center">
                <p align="">
                   The entire table is set to 0x0000 upon request, 
                   and the EUI 64, which requests a certain number of requests, is included in the EUI List.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    EUI List 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8 x Node Count
                </p>
            </td>
            <td width="680" nowrap="" rowspan="" valign="center">
                <p align="">
                   The node count contains EUI64 of the node.
                   <br>When the Node Count is 0x0000, the corresponding item is left empty.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Authorization List

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Node Count 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The number of Authorization Table.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Authorization Info
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   There are as many Authorization Info as Node Count.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p>Authorization Info

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Node EUI 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   EUI 64 of the target node.
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Authorization Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   This is the authentication status of the target node.
                   <br>0: HES not authorized, security unauthenticated status
                   <br>1: HES permission, security unauthenticated status (3-PASS)
                   <br>2: HES permission, security authentication status (3-PASS)
                   <br>11: Security Authentication Status (PANA)
                   <br>12: Security Failure Backoff
                   <br>255: No node information
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Backoff Time
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The value applied when Authorization Status is 12, in seconds.
                   <br>When the command is received, the coordinator blocks all the traffic of the target node by the set number of seconds.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="commonoperation"><h2>10. Common Operation</h2></p>
<p id="factorysetting2"><h3>10.1. Factory Setting</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               This is the command to set the modem to the factory setting.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Set 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
 
<p>Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Returns a value of 1.
                </p>
            </td>
        </tr>
   </tbody>
</table>

<p>Set

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Set 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   1: Device Factory Setting Ignore any other values.
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="reset"><h3>10.2. Reset</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="800" colspan="3" align="left"> 
               This is a command to reset the modem.
            </td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Status
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Set 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
 
<p>Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Returns a value of 1.
                </p>
            </td>
        </tr>
   </tbody>
</table>

<p>Set

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Set 
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   1: Device Reset Other values are ignored.
                </p>
            </td>
        </tr>
   </tbody>
</table>



<p id="time"><h2>11. Time</h2></p>
<p id="utctime"><h3>11.1.  Utc Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the modem's time.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Time
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Time

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Year
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    year
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Month
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    month
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    day
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Day
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hour
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    hour
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Minute
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    minute
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Second
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   second
                    </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="timezone"><h3>11.2. Time Zone</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            This command reads the time zone offset of the modem.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Offset
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Offset

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Offset
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    year
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="meterinformation2"><h2>12. Meter Information</h2></p>
<p id="meterserialnumber"><h3>12.1. Meter Serial Number</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the serial number of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter Serial Number
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Meter Serial Number

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Number
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="metermanufacturenumber"><h3>12.2. Meter Manufacture Number</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the manufacturer number of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter Manufacture Number
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Meter Manufacture Number

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Number
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="customernumber"><h3>12.3. Customer Number</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the customer number of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Customer Number
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Customer Number

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Number
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="modelname"><h3>12.4. Model Name</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to read the model name of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Model Name
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Model Name

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Name
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="hwversion"><h3>12.5. HW Version</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to read the hardware version of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   HW Version
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>HW Version

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="swversion"><h3>12.6. SW Version</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to read the software version of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   SW Version
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>HW Version

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Version
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   20
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    20 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="meterstatus"><h3>12.7. Meter Status</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to read the software version of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Meter Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Varue
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                 <table border="1" cellspacing="0" cellpadding="0" width="0">
                 <tbody>
                    <tr bgcolor="#cccccc">
                      <td width="" colspan="8" nowrap="" rowspan="" valign="center">
                      <p align="center">
                          <strong>Byte0(Msb->Lsb)</strong>
                      </p></td>
                    <tr>
                      <td width="130" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Clock Invalid
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Replace battery
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Power Up
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            L1 error
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            L2 error
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            L3 error
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                    </tr>
                    <tr bgcolor="#cccccc">
                      <td width="" colspan="8" nowrap="" rowspan="" valign="center">
                      <p align="center">
                          <strong>Byte1(Msb->Lsb)</strong>
                      </p></td>
                    <tr>
                      <td width="130" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Program memory error
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            RAM error
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            NV memory error
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                           Watchdog error
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Fraud attempt
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>

                    <tr bgcolor="#cccccc">
                      <td width="" colspan="8" nowrap="" rowspan="" valign="center">
                      <p align="center">
                          <strong>Byte2(Msb->Lsb)</strong>
                      </p></td>
                    <tr>
                      <td width="130" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Communication error M-BUS
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            New M-BUS device discovered
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>

                    <tr bgcolor="#cccccc">
                      <td width="" colspan="8" nowrap="" rowspan="" valign="center">
                      <p align="center">
                          <strong>Byte3(Msb->Lsb)</strong>
                      </p></td>
                    <tr>
                      <td width="130" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="110" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="100" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                      <td width="40" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             -
                      </p></td>
                   </tr>
               </tbody>
               </table>
            </td>
        </tr>
    </tbody>
</table>


<p id="lastupdatetime"><h3>12.8.  Last Update Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the last update time of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Last Update Time
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Last Update Time

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Year
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    year
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Month
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    month
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    day
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Day
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hour
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    hour
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Minute
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    minute
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Second
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   second
                    </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="lastcommtime"><h3>12.9.  Last Comm Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
            It is a command to read the last communication time of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Last Comm Time
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Last Comm Time

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Year
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    year
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Month
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    month
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    day
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Day
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hour
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    hour
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Minute
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    minute
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Second
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   second
                    </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="lpchannelcount"><h3>12.10. LP Channel Count</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to get the LP channel count of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   LP Channel Count
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>LP Interval

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="lpinterval"><h3>12.11. LP Interval</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to get the LP Interval of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   LP Interval
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>LP Interval

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Interval
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter's LP storage cycle
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="cumulativeactivepower"><h3>12.12. Cumulative Active Power</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is an instruction to fetch the meter's current cumulative active power.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Cumulative Active Power
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Cumulative Action Power

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Current cumulative effective power
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="cumulativeactivepowertime"><h3>12.13.  Cumulative Active Power Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
                  This is an instruction to get the meter's current cumulative active power time.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Active Power Time
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Active Power Time

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Year
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    year
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Month
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    month
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    day
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Day
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hour
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    hour
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Minute
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    minute
                    </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Second
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   second
                    </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="electricmeterinformation"><h2>13. Electric Meter Information</h2></p>
<p id="ct"><h3>13.1. CT</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the CT value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   CT
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>CT

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    The CT value of the meter
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="pt"><h3>13.2. PT</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the PT value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   PT
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>PT

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    The PT value of the meter
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="transformerratio"><h3>13.3. Transformer Ratio</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Transformer Ratio value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Transformer Ratio
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Transformer Ratio

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    The Transformer Ratio value. (CT * PT = Transformer Ratio)
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="phaseconfiguration"><h3>13.4. Phase Configuration</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the phase configuration value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Phase Configuration
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Phase Configuration

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Phase
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    4 bytes of text / plain value
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="switchstatus"><h3>13.5. Switch Status</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the switch status of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Switch Status
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Switch Status

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Status
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : Switch Off 
                    <br>1 : Switch On
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="frequency"><h3>13.6. Frequency</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the Line Frequency value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Frequency
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Frequency

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Line Frequency value of meter
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="vasf"><h3>13.7. VA_SF</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Power Scale Factor value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   VA_SF
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>VA_SF

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter's Power Scale Factor Value
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p id="vahsf"><h3>13.8. VAH_SF</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Energy Scale Factor value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   VAH_SF
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>VAH_SF

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Tag
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    OBIS Code Tag
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                    Meter's Energy Scale Factor Value
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p id="dispscalar"><h3>13.9. DISP_SCALAR</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Display Scalar value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   DISP_SCALAR
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>DISP_SCALAR

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   Scale value to be applied before displaying.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="dispmultiplier"><h3>13.10. DISP_MULTIPLIER</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Display Multiplier value of the meter.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   DISP_MULTIPLIER
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>DISP_MULTPLIER

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   Meter's Display Multiplier Value
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="meterterminalinformation"><h2>14. Meter Terminal Information</h2></p>
<p id="primarypowersourcetype"><h3>14.1. Primary Power Source Type</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is the command to bring the main power source of the meter terminal.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Primary Power Source Type
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Primary Power Source Type

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   0: Unknown
                   <br>1: Electric
                   <br>2: Battery
                   <br>3: Solar
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="secondarypowersourcetype"><h3>14.2. Secondary Power Source Type</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the auxiliary power source of the meter terminal.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Secondary Power Source Type
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Secondary Power Source Type

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   0: Unknown
                   <br>1: Electric
                   <br>2: Battery
                   <br>3: Solar
                   <br>4: Super Cap
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="resetcount"><h3>14.3. Reset Count</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the number of times the meter terminal has been reset.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reset Count
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Reset Count

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   Number of times the meter terminal was reset
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="resetreason"><h3>14.4. Reset Reason</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the reason why the meter terminal was last reset.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reset Reason
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Reset Reason

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Reason
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                 <table border="1" cellspacing="0" cellpadding="0" width="0">
                 <tbody>
                    <tr bgcolor="#cccccc">
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            <strong>Field<strong>
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Reserved
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Low Power
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            WWDOG
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            IWDOG
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            Software
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             POR/PDR
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             Pin
                      </p></td>
                      <td width="90" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             POR/PDR or BOR
                      </p></td>
                    </tr>
                    <tr>
                      <td width="80" nowrap="" rowspan="" valign="center" bgcolor="#cccccc">
                      <p align="center">
                            <strong>Bit<strong>
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            7
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            6
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            5
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            4
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                            3
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             2
                      </p></td>
                      <td width="80" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             1
                      </p></td>
                      <td width="90" nowrap="" rowspan="" valign="center">
                      <p align="center">
                             0
                      </p></td>
                    </tr>
               </tbody>
               </table>
            </td>
        </tr>
    </tbody>
</table>


<p id="operationtime"><h3>14.5. Operation Time</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Operation Time</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Operation Time
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Operation Time

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   4
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   The total cumulative time value operated by the meter terminal, in seconds.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="resetschedule"><h3>14.6. Reset Schedule</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Modem reset schedule.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reset Schedule
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Reset Schedule

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Reset Schedule
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   It is set to the time from 0 to 23 hours.
                   If it is 0xFF, do not reset the modem.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="communicationinterface"><h2>15. Communication Interface</h2></p>
<p id="typemain"><h3>15.1. Type(Main)</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to get the kind of main communication interface of modem / DCU.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Communication Interface
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Communication Interface

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   0 : Ethernet, 1 : Mobile
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="typesub"><h3>15.2. Type(Sub)</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is a command to get the type of sub-communication interface of modem / DCU.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Communication Interface
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Communication Interface

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Type
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   0 : ZigBee, 1 : PLC, 2 : Subgiga
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="6lowpaninformation"><h2>16. 6LoWPAN Information</h2></p>
<p id="interfaceipv6address"><h3>16.1. Interface IPv6 Address</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Interface Ipv6 (DCU IP) This command reads the address.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Address
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>IPv6 Address

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Password
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   16
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Address
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="ipv6address"><h3>16.2. IPv6 Address</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is an instruction to read the IPv6 address.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Address
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>IPv6 Address

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Password
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   16
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Address
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="interfacelistenport"><h3>16.3. Interface Listen Port</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Interface Listen Port (DCU DTLS) This command sets or reads the address.</td>
       </tr>
        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   port
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    port 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>port

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   Interface Listen Port
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="networklistenport"><h3>16.4. Network Listen Port</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Network Listen Port (DCU DTLS) This command sets or reads the address.</td>
       </tr>
        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005; 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   port
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    port 
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>port

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="">
                   Network Listen Port
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="frequency2"><h3>16.5. Frequency</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               It is an instruction to get the operating frequency band of the modem.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Frequency
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Frequency

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Start Freq
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   3
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The starting frequency, the upper 2 bytes, is the integer part and the lower 1 byte is the fractional part.
                   <br> Ex) 917.3 MHZ -> 0x039503
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    End Freq
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   3
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   At the end frequency, the upper 2 bytes are the integer part and the lower 1 byte is the fractional part.
                   <br> Ex) 923.1 MHZ -> 0x039B01
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="bandwidth"><h3>16.6. Bandwidth</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command fetches the frequency bandwidth used by each channel.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Bandwidth
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Bandwidth

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Bandwidth
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The frequency bandwidth is in kHz.
                   <br> Ex) 0x00C8 -> 200kHz 
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="basestationaddress"><h3>16.7. Base Station Address</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This command gets the Base Station Address (Coordi).</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Adress
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>IPv6 Address

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Address
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   16
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   IPv6 Address
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="appkey"><h3>16.8. APP Key</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is the command to get the APP Key (session key).</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   APP Key
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>APP Key

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   16
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Session key
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="hopstobasestation"><h3>16.9. Hops to Base Station</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              This command gets the number of hops up to Coordinator (Concentrator).</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Hops to Base Station
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Hops to Base Station

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hops
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The number of hops of a node.
                   <br>0x0001: Coordinator
                   <br>0x0100: 1-hop node
                   <br>0x0200: 2-hop node
                   <br>0x0300: 3-hop node
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="eui64"><h3>16.10. EUI 64</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              It is an instruction to get EUI 64 of modem.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   EUI 64
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>EUI 64

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    EUI 64
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   8
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem-specific EUI 64.
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="listenport"><h3>16.11. Listen Port</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              Listen Port (DCU DTLS) This command sets or reads the address.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   port
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    port
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
        </tbody>
</table>
        
<p>port

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    port
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Port number
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="maxhop"><h3>16.12. Max Hop</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              It is an order to bring Max Hop.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="3" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Max Hop
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Max Hop

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Hop
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Max Hop
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="meterterminaloperationscheduleinformation"><h2>17. Meter Terminal Operation Schedule Information</h3></p>
<p id="meteringschedule"><h3>17.1. Metering Schedule</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
              This is the command to set or read the Metering schedule.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Metering Schedule
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Metering Schedule
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
        </tbody>
</table>
        
<p>Metering Schedule

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width=""  nowrap="" rowspan="" valign="center">
                <p align="center">
                    Second
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Schedule time (unit: second)
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p id="lpuploadschedule"><h3>17.2. LP Upload Schedule</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               Modem LP Upload It sets up or reads the schedule.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem LP upload schedule
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Set
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem LP upload schedule
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   &#10005;
                </p>
            </td>
        </tr>
        </tbody>
</table>
        
<p>Modem LP upload schedule

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Schedule
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Schedule time (unit: second)
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="event"><h2>18. Event</h2></p>
<p id="eventtype"><h3>18.1. Event Type</h3></p>

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr bgcolor="#cccccc">

            <td width="1000"  colspan="3" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
       </tr>
       <tr>
            <td width="1000" colspan="3" align="left"> 
               This is a command to fetch 10 Modem Events.</td>
       </tr>

        <tr bgcolor="#cccccc">
            <td width="250" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>operation</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Data</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="120" nowrap="" rowspan="2" valign="center">
                <p align="center">
                    Get
                </p>
            </td>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Request
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    &#10005;
                </p>
            </td>
        </tr>
        <tr>
            <td width="130" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Response
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem Event
                </p>
            </td>
        </tr>
    </tbody>
</table>
        
<p>Modem Event

<table border="1" cellspacing="0" cellpadding="0" width="0">
    <tbody>
        <tr>
            <td width="180" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Field</strong>
                </p>
            </td>
            <td width="70" colspan="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Byte</strong>
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="center">
                    <strong>Description</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="" colspan="2" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Event Log Count
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                   2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Number of event logs read
                </p>
            </td>
        </tr>
        <tr>
            <td width="70" nowrap="" rowspan="4" valign="center">
                <p align="center">
                    Event Log Data
                </p>
            </td>
            <td width="110" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Index
                </p>
            </td>
            <td width="70" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    Event Log Index
                </p>
            </td>
        </tr>
        <tr>
            <td width="110" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Time
                </p>
            </td>
            <td width="70" nowrap="" rowspan="" valign="center">
                <p align="center">
                    7
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Event Log Time(0xYYYYMMDDhhmmss)
                </p>
            </td>
        </tr>
        <tr>
            <td width="110" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Log Code
                </p>
            </td>
            <td width="70" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Log Code (see MIU_Event_Code_List in Appendix)
                </p>
            </td>
        </tr>
        <tr>
            <td width="110" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Log Value
                </p>
            </td>
            <td width="70" nowrap="" rowspan="" valign="center">
                <p align="center">
                    4
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Log Data (see MIU_Event_Code_List in Appendix)
                </p>
            </td>
        </tr>
    </tbody>
</table>





</div>
</body>
</html>