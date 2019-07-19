<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
	<style>
	body{overflow:hidden}
	</style>

<link href="../css/gadget_ext.css" rel="stylesheet" type="text/css">

<script src="Scripts/swfobject_modified.js" type="text/javascript"></script>
</head>
<body> 
<!--상단탭-->
<div class="gad_sub_tab">
 <ul >
  <li><a href="#" id="current">전기</a></li>
  <li><a href="#">가스</a></li>
  <li><a href="#">수도</a></li>
  <li><a href="#">온수</a></li>
  <li><a href="#">열공급</a></li>
  <li><a href="#">냉방</a></li>
  <li><a href="#">난방</a></li>      
  <li><a href="#">냉난방</a></li>    
 </ul>
</div>
<!--상단탭 끝-->
<div style="padding:6px">
<div style="width:100%; float:left"><h1><h1 class="fc_orange">2009.09.15</h1> 기준</h1></div>
<div style="float:left; width:200px;">
  <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="200" height="200">
    <param name="movie" value="../flexapp/swf/desin_pie.swf">
    <param name="quality" value="high">
    <param name="wmode" value="transparent">
    <param name="swfversion" value="9.0.45.0">
    <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
    <param name="expressinstall" value="Scripts/expressInstall.swf">
    <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
    <!--[if !IE]>-->
    <object type="application/x-shockwave-flash" data="../flexapp/swf/desin_pie.swf" width="200" height="200">
      <!--<![endif]-->
      <param name="quality" value="high">
      <param name="wmode" value="transparent">
      <param name="swfversion" value="9.0.45.0">
      <param name="expressinstall" value="Scripts/expressInstall.swf">
      <!-- The browser displays the following alternative content for users with Flash Player 6.0 and older. -->
      <div>
        <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
        <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
      </div>
      <!--[if !IE]>-->
    </object>
    <!--<![endif]-->
  </object>
</div>
<div id="nuri_cust_r">
<dl>
<dt class="font_web fc_bl">전체고객수 <span class="fc_orange">3020</span>가구</dt>
<dt style="margin-top:5px">계약고객 <span class="fc_bu font_web">3000</span>가구</dt>
<dt>임시고객 <span class="fc_bu font_web">300</span>가구</dt>
<dt>해지고객 <span class="fc_bu font_web">300</span>가구</dt>
<dt>금일 신규고객 <a href="#" class="fc_bu font_web">20</a>가구</dt>
<dt style="margin:0 0 10px 0">금일 해지고객 <a href="#" class="fc_bu font_web">10</a>가구</dt>
</dl>
  <div id="nuri_btn"><ul>
  <li class="input"><a href="#"><span >계약정보 입력</span></a></li></ul>
</div>
</div>

<div style="width:100%; float:left">grid</div>


</div>
</body>
</html>
