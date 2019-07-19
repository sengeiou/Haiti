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
            <td align="center"> 0x0003       </td>
            <td align="left"><a href="#factorysetting">
                     &nbsp  Factory Setting  </a></td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
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
                    0xA000 
                </p>
            </td>
            <td colspan="5" nowrap="" valign="top">
                <p align="center">
                    <a href="#coordinator"> &nbsp Coordinator</a>
                </p>
            </td>
        </tr>
        <tr>
            <td align="center"> 0xA001       </td>
            <td align="left"><a href="#coordinatorinformation"> &nbsp  Coordinator Information </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center">              </td>
            <td align="center">              </td>
        </tr>
        <tr>
            <td align="center"> 0xA003       </td>
            <td align="left"><a href="#bootloaderjump"> &nbsp  Bootloader Jump </a> </td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA005       </td>
            <td align="left"><a href="#networkipv6prefix">  &nbsp  Network IPv6 Prefix  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA006       </td>
            <td align="left"><a href="#coordinatoreui">  &nbsp  Coordinator EUI  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA007       </td>
            <td align="left"><a href="#coordinatorbroadcastconfiguration"> &nbsp  Coordinator Broadcast Configuration  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA008       </td>
            <td align="left"><a href="#networkkey"> &nbsp  Network Key  </a> </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA009       </td>
            <td align="left"><a href="#coordinatoronetimebroadcast"> &nbsp  Coordinator One-Time Broadcast  </a> </td>
            <td align="center">              </td>
            <td align="center">              </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
        </tr>
        <tr>
            <td align="center"> 0xA00A       </td>
            <td align="left"><a href="#networkfilterrssivalue"> &nbsp Network filter rssi value   </a></td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
            <td align="center"> &#9675       </td>
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


<p id="factorysetting"><h3>1.2.  Factory Setting</h3></p>

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


<p id="rollbackimage"><h3>1.3. Rollback Image</h3></p>

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


<p id="cloneonoff"><h3>1.4. Clone On/Off</h3></p>

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


<p id="fwimageinformation"><h3>2.5. FW Image Information</h3></p>

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


<p id="modemtxpower"><h3>3.3. Modem TX Power</h3></p>

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


<p id="formjoinnetwork"><h3>3.4. Form/Join Network</h3></p>

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


<p id="networkspeed"><h3>3.5. Network Speed</h3></p>

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



<p id="modemipinformation"><h3>3.6. Modem IP Information</h3></p>

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
     

<p id="modemportinformation"><h3>3.7. Modem Port Information</h3></p>

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


<p id="rawromaccess"><h3>3.8. Raw ROM Access</h3></p>

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



<p id="coordinator"><h2>4. Coordinator</h2></p>
<p id="coordinatorinformation"><h3>4.1. Coordinator Information </h3></p>

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
                  Coordinator Information Response.<br>
                  This is the response to the Coordinator Information Request.
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Coordinator Information Response

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
                <p align="center"> Network Channel  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     The network channel value is the frequency band of each channel (MHz).
                     <br>Channel  1 = 917.3
                     <br>Channel  2 = 917.9
                     <br>Channel  3 = 918.5
                     <br>Channel  4 = 919.1
                     <br>Channel  5 = 919.7
                     <br>Channel  6 = 920.3
                     <br>Channel  7 = 920.7
                     <br>Channel  8 = 920.9
                     <br>Channel  9 = 921.1
                     <br>Channel 10 = 921.3
                     <br>Channel 11 = 921.5
                     <br>Channel 12 = 921.7
                     <br>Channel 13 = 921.9
                     <br>Channel 14 = 922.1
                     <br>Channel 15 = 922.3
                     <br>Channel 16 = 922.5
                     <br>Channel 17 = 922.7
                     <br>Channel 18 = 922.9
                     <br>Channel 19 = 923.1
                     <br>Channel 20 = 923.3
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Pan ID  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 2    </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Network Fan ID
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Network Key  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 16   </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Network key
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> RF Power  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1    </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     It means RF Tx Power (dBm)
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p id="bootloaderjump"><h3>4.2. Bootloader Jump</h3></p>

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
                    This command forces jump to the boot loader.
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
                    Bootloader Jump Code
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
        
<p>Bootloader Jump Code

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
                <p align="center"> Bootloader Jump Code   </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 4      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                    Use the command (0x00, 0x03, 0x01, 0x04) to jump to the boot loader.
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


<p id="networkipv6prefix"><h3>4.3. Network Ipv6 Prefix</h3></p>

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
                    Coordinator This message is sent when the boot finishes.
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
                  Prefix Format
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
                    Prefix Format
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
                   Prefix Format
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Prefix Format

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
                <p align="center"> Network Ipv6 Prefix  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 6      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     IPv6 Prefix to use on the network
                </p>
            </td>
        </tr>
   </tbody>
</table>

<p id="coordinatoreui"><h3>4.4. Coordinator EUI</h3></p>

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
                    This command sets or reads the Coordinator's EUI.
                    <br>Since the EUI is stored in the OTP area of the CPU's internal flash, the number of times it can be changed is limited (up to five times in total).
                    <br>If the EUI is restored to its original state, the index is 0, and when the EUI is planted, the corresponding EUI is stored in Index 0.
                    <br>When the EUI is planted again, the Index increases to 1 and the corresponding EUI is stored in Index 1.
                    <br>If the Index is 4, the EUI can no longer be planted.
                    
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
                  Coordinator EUI format
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
                   Coordinator EUI 
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
                   Coordinator EUI format
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Coordinator EUI format

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
                <p align="center"> OTP index  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     OTP index of currently in use or newly established EUI..
                </p>
            </td>
        </tr>
        <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> Coordinator EUI  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 8     </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     Coordinator EUI
                </p>
            </td>
        </tr>
    </tbody>
</table>


<p>Coordinator EUI

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
                <p align="center"> Coordinator EUI  </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 8      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                     An 8-byte coordinator EUI value.
                     <br>Note: In the Set command, the upper 3 bytes must use the company code (0x000B12) issued by Nuri Telecom.
                     <br>Ex) 00 0B 12 xx xx xx xx xx
                </p>
            </td>
        </tr>
   </tbody>
</table>


<p id="coordinatorbroadcastconfiguration"><h3>4.5. Coordinator Broadcast Configuration</h3></p>

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
              his is a setting value for broadcasting from the coordinator to the network. In the Enable configuration, each setting should be set to True (1), but the modem will apply the setting value.
               If it is set to false (0), it will not be applied. The size that can be propagated is 10 bytes in total.
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
                   Broadcast configuration
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
                    Broadcast configuration
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
        

<p>Broadcast configuration


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
                    Enable configuration
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                                  
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
                <p align="center">    0(LSB)         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Modem mode ON/OFF
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    ETC Configuration ON/OFF
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Metering interval ON/OFF
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Transmit frequency ON/OFF
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Reserved
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    Reserved
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    6         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reserved
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     7(MSB)          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Modem mode
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the same as Modem Mode.
                </p>
             </td>
         </tr>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    ETC Configuration
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    See ETC Configuration below.
                </p>
             </td>
         </tr>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Metering interval
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the same as Metering interval.
                </p>
             </td>
         </tr>
           <tr>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Transmit frequency
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                    It is the same as Transmit Frequency.
                </p>
             </td>
         </tr>
     </tbody>
</table>


<p>ETC Configuration


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
                    Configuration
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center"> 
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                                  
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
                <p align="center">    0(LSB)         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   If the value is set to 1, the current Clone is stopped.
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     1          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : MAC Push metering off.<br>
                    1 : MAC Push metering on.
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     2          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : APC off.<br>
                    1 : APC on.
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     3          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : Auto upgrade self off.<br>
                    1 : Auto upgrade self on.
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     4          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : Auto upgrade 3rd party device off.<br>
                    1 : Auto upgrade 3rd party device on.
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     5          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                    0 : Do not use DTLS with DCU.<br>
                    1 : Use DTLS with DCU.
                </p>
              </td>
            </tr>
             <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">    6         </p>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reserved
                </p>
              </td>
            </tr>
            <tr>
              <td width="200" nowrap="" rowspan="" valign="center">
                <p align="center">     7(MSB)          </p>
              </td>
              <td width="500" nowrap="" rowspan="" valign="center">
                <p align="">
                   Reserved
                </p>
              </td>
            </tr>
            </tbody>
            </table>
     </tbody>
</table>


<p id="networkkey"><h3>4.6. Network Key</h3></p>

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
               Used to manage the keys to be used in the network.(PANA, L2, etc.)
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
                   Network Key
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
                    Network Key
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
                   Network Key
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>Network Key

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
                    Key ID
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   It means ID of key to use among Key Info (0 and 1 are excluded).
                </p>
            </td>
        </tr>
        <tr>
            <td width="80" nowrap="" rowspan="4" valign="center">
                <p align="center">
                    Key Info
                </p>
            </td>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Key Length
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    2
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   Key Indicates the length of the Info key.
                </p>
            </td>
        </tr>
        <tr>
            <td width="100" nowrap="" rowspan="" valign="center">
                <p align="center">
                    Key
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    N
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   This is the key information to use in the network.
                </p>
            </td>
        </tr>
     </tbody>
</table>


<p id="coordinatoronetimebroadcast"><h3>4.7. Coordinator One-Time Broadcast</h3></p>

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
                    This command is used by the coordinator to propagate a specific command to the whole network. 
                    It should be noted that the command may be propagated once across the entire network and may result in a modem that does not receive commands for communication reasons.
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
                     Broadcast Configuration
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
        
<p> Broadcast Configuration

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
                <p align="center"> Command   </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center"> 1      </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">       
                   1: Modem Factory Settings<br>
                   2: Reset the modem
                </p>
            </td>
        </tr>
    </tbody>
</table>

<p>Status Code

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
                <p align="center"> Status Code   </p>
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


<p id="networkfilterrssivalue"><h3>4.8. Network filter RSSI value</h3></p>

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
               Modems are the commands that set the RSSI value that is the basis when selecting the parent.
               When the corresponding command is set to Coordinated, the corresponding value is loaded into the EB. 
               Modems that receive EBs select the parent based on this value when the parent is selected.
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
                   RSSI Value
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
                    RSSI Value
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
                   RSSI Value
                </p>
            </td>
        </tr>
    </tbody>
</table>
        

<p>RSSI Value

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
                    RSSI Value
                </p>
            </td>
            <td width="" nowrap="" rowspan="" valign="center">
                <p align="center">
                    1
                </p>
            </td>
            <td width="750" nowrap="" rowspan="" valign="center">
                <p align="">
                   The default value is -127. 
                   By default, modems do not use this setting. 
                   The range of values that can be set is -127 to -21.
                </p>
            </td>
        </tr>
    </tbody>
</table>



<p id="networkinformation"><h2>5. Network Information</h2></p>
<p id="1-hopneighborlist"><h3>5.1. 1-Hop Neighbor List</h3></p>

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


<p id="childnodelist"><h3>5.2. Child Node List</h3></p>

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


<p id="nodeauthorization"><h3>5.3. Node Authorization</h3></p>

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




</div>
</body>
</html>