<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Location Tree</title>
</head>
<body>

<div style="width:100%; float:left; padding:10px 0 0 0">
  <object id="locationTree" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%"
  codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
    <param name="movie" value="${ctx}/flexapp/swf/locationTree.swf">
    <param name="quality" value="high">
    <param name="wmode" value="opaque">
    <param name="swfversion" value="9.0.45.0">
    <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
    <param name="expressinstall" value="Scripts/expressInstall.swf">
    <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
    <!--[if !IE]>-->
    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/locationTree.swf" width="100%" height="324">
      <!--<![endif]-->
      <param name="quality" value="high">
      <param name="wmode" value="opaque">
      <param name="swfversion" value="9.0.45.0">
      <param name="expressinstall" value="Scripts/expressInstall.swf">
      <!-- The browser displays the following alternative content for users with Flash Player 6.0 and older. -->
      <div>
        <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
        <p><a href="http://www.adobe.com/go/getflashplayr"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
      </div>
      <!--[if !IE]>-->
    </object>
    <!--<![endif]-->
  </object>
</div>

</body>
</html>