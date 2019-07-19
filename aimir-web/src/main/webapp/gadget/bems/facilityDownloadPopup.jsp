<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<title><fmt:message key="aimir.report.fileDownload"/></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<script type="text/javascript">
		(function(window) {
			function init() {
				var trigger = {
						
					fileDown: function(fname) {
				    	var input = document.getElementById("fileNameInput");
				        var url = "${ctx}/common/fileDownload.do";
				        var downform = document.getElementById("reportDownloadForm");
				        downform.action = url;
				        downform.target = "downFrame";
				        downform.submit();
				    },
					
					winClose: function() {
			            window.close();
			        }
				};
				
				function eventBind() {
					var wrapper = document.getElementById("wrapper");
					wrapper.onclick = function(e) {
						var target = e.target || e.srcElement;
						var action = target.getAttribute("action");
						if(action === 'download-zipfile') {
							trigger.fileDown(target.id);
						}
						else if(action === 'download-file') {
							trigger.fileDown(target.id);
						}
						else if(action === 'close-window') {
							trigger.winClose();
						}
					}			
				}
				eventBind();
				window.hide();
			}
			
			window.onload = init;
			
		})(window);
	</script>
</head>
<body>
	<form name="reportDownloadForm" 
		id="reportDownloadForm" method="post"
		target="downFrame" style="display: none;">
		<input type="hidden" id="filePathInput" name="filePath" value="${filePath}" />
		<input type="hidden" id="fileNameInput" name="fileName" value="${fileName}"/>
		<input type="hidden" id="zipFileNameInput" name="zipFileName" value="${zipFileName}"/>
		<input type="hidden" id="realFileNameInput" name="realFileName" />
	</form>
	<iframe name="downFrame" style="display:none;"></iframe>
	<div id="wrapper">
		<div class="popup_title"><span class="icon_download"></span><fmt:message key="aimir.report.fileDownload"/></div>
		<c:choose>
			<c:when test="${fileNames!= null && !empty fileNames}">
				<div id="datalist" class="overflow_hidden"  style="display:block">
					<div class="download_zip">
						<ul>
						<li class="file" id="zipFile">${zipFileName}</li>
						<li class="downbtn">
							<em class="am_button">
								<div action="download-zipfile" id="${zipFileName}" class="divbutton">
									<fmt:message key="aimir.report.fileDownload"/> 
								</div>
							</em>
						</li>
						</ul>
					</div>
					
					<div class="downloadbox">
						<table class="download" id="filelist">
							<colgroup>
				            	<col width='45' /> 
				            	<col width='' /> 
				            	<col width='' /> 
			            	</colgroup>
							<c:forEach varStatus="l" var="file" items="${fileNames}">
								<tr>
				                	<th>${l.index + 1}</th>
			                		<td>${file}</td> 
			                		<td class='button'>
			                			<input type='hidden' 
			                				name='${file}-${l.index + 1}' id='file-${l.index}' 
			                				value='${file}'/>
			                			<button action='download-file' id="${file}" type='button' class='input_button' style="cursor: pointer;">
			                				<fmt:message key="aimir.report.fileDownload"/> 
			                			</button>
			                		</td> 
			                	</tr>
							</c:forEach>
						</table>
					</div>
					
					<div class="overflow_hidden textalign-center">
						<em class="am_button">
							<div action="close-window" class="divbutton">
								<fmt:message key="aimir.board.close"/>
							</div>
						</em>
					</div>
					
				</div>
			</c:when>
			<c:otherwise>
				<div id="nodata" class="nodata">
					<ul>
						<li class="text"><fmt:message key="aimir.data.notexist"/></li>
						<li class="close"><em class="am_button">
			                <div action="close-window" class="divbutton" style="cursor: pointer;">
								<fmt:message key="aimir.board.close"/>
							</div>
						</em></li>
					</ul>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>