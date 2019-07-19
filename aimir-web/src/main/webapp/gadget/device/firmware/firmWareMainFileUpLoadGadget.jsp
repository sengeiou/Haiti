<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", -1); //prevents caching at the proxy
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script language='javascript'>

  $(document).ready(function(){
         //----------------------------- 파일업로드 설정 부분---------------------------------    
        $("#form_input").ajaxForm({
            //beforeSubmit : //alert("파일전송전 벨리데이션 처리 할 부분"),
            success:function(responseText, statusText, xhr, $form){
                if($.trim(statusText) == "success"){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"파일을 성공적으로 등록하였습니다.");
                    $("#list1").trigger("reloadGrid");
                }else{
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"FileUpload Error..");
                }
            }
        });

        $("#form_input").submit(function(){return false;});//일단 전송중지 시킨다

        //파일전송부분
        $("#meterInstallImgInsert").click(function(){

            var imgno = "0"; // 파일 번호
            var nono = "0";   // 에러 체크 변수
            $('#newfile').each(function() {
             var imgval = $(this).val();
             imgno++;

               if (!imgval.toLowerCase().match(/.(tar.gz)$/i)) {  // 이부분의 확장자를 변경하시면 됩니다.
              Ext.Msg.alert('<fmt:message key='aimir.message'/>',"tar.gz 파일이 아닙니다.");
              nono = "1";
              return false;
             }
            });
            if (nono == 1) {
             return false;
            }

            
            if(confirm("파일전송 하시겠습니까?")){
                $("#div_input").ajaxError(function(evt,xhr,opt){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"오류가 발생하였습니다.");
                    $('#div_input').unblock();
                });
                $("<input type='hidden' name='seprator' id='seprator' value='admin_upt'>").appendTo("#form_input");
                $("#form_input").attr("action","${ctx}/gadget/device/file_upload.do");
                $("#form_input").submit();
            }
            

        });

      //파일전송부분
        $("#file_submit2").click(function(){

            var imgno = "0"; // 파일 번호
            var nono = "0";   // 에러 체크 변수
            $('#newfile').each(function() {
             var imgval = $(this).val();
             imgno++;

               if (!imgval.toLowerCase().match(/.(tar.gz)$/i)) {  // 이부분의 확장자를 변경하시면 됩니다.
              Ext.Msg.alert('<fmt:message key='aimir.message'/>',"tar.gz 파일이 아닙니다.");
              nono = "1";
              return false;
             }
            });
            if (nono == 1) {
             return false;
            }

            
            if(confirm("파일전송 하시겠습니까?")){
                $("#div_input").ajaxError(function(evt,xhr,opt){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"오류가 발생하였습니다.");
                    $('#div_input').unblock();
                });
                $("<input type='hidden' name='seprator' id='seprator' value='admin_upt'>").appendTo("#form_input");
                $("#form_input").attr("action","${ctx}/gadget/device/file_upload.do");
                $("#form_input").submit();
            }
            

        });
        
      });

	  function go_upload(){
		  //$("file:enabled");
	   //var input = $("input:file").css({background:"yellow", border:"3px red solid"});
	//    $("div").text("For this type jQuery found " + input.length + ".")         .css("color", "red");
	    $("form_input").submit(function () { return false; }); // so it won't submit


		           
		  
		  
		  //$('#newfile').val("C:\Users\happy\Desktop\1.tar.gz");
		  
		//$('#newfile').click();
		//document.form_input.newfile.click();
		  
		//$('#binaryFileName').val($('#newfile').val());
	  }

	  function go_upload2(){
		  $("#file_submit2").click();
	  }
  
</script>




	
</head>
<body>

<form id="form_input" name="form_input" method="post" enctype="multipart/form-data">
<table>
<td class="txtRight">파일명</td>
   <td>
     <input type="hidden" name="oldFile" id="oldFile" value="">
     <span id="file" style="display: none"><a href="#" id="fname"></a></span>
     <div id="filearea">
       <input type="file" id="newfile" name="newfile"  style="font-size: 9pt; display: block;"/>
     </div>
          <input type="hidden" id="filenamez" name="filenamez" value=""/>
          <input type="button" name="file_submit2" id="file_submit2" value="파일전송" style="width: 70px;" /></td>
</table>
</form>


