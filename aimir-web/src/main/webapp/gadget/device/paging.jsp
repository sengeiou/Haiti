<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>	 	
<%@ include file="/taglibs.jsp"%>

<script language="JavaScript">

	var setPage = function(page) {
		document.getElementById('page').value = page; 
	};	

	var prevBlock = function() {

		var startPage = '${paging.startPage}';
		
		setPage(parseInt(startPage) - 1);

		searchList();
	};

	var nextBlock = function() {
		
		var endPage = '${paging.endPage}';		
		
		setPage(parseInt(endPage) + 1);

		searchList();
	};

	var prevPage = function() {

		var page = '${paging.page}';

		setPage(parseInt(page) - 1); 

		searchList();
	};

	var nextPage = function() {

		var page = '${paging.page}';

		setPage(parseInt(page) + 1); 

		searchList();

	};
	
</script>

<input type="hidden" id="page" value="${paging.page}" />

<ul>
	<li style="text-align:center; height:35px; padding:15px 0 0 0; border-bottom:2px solid white; width:100%;">

		<c:if test="${paging.prevBlock == 'true'}">
			<a href='JavaScript:prevBlock();'>[<fmt:message key="aimir.memo.pre" /> 10 <fmt:message key="aimir.page" />]</a>
		</c:if>
		
		<c:if test="${paging.prevPage == 'true'}">
			<a href='JavaScript:prevPage();'>[<fmt:message key="aimir.prevpage" />]</a>
		</c:if>		
			
		<c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
			<c:choose>
				<c:when test="${paging.page == i}">
					<a href="#">${i}</a>
				</c:when>
				<c:otherwise>
					<a href="JavaScript:setPage('${i}');searchList();">[${i}]</a>
				</c:otherwise>
			</c:choose>			
		</c:forEach>
		
		<c:if test="${paging.nextPage == 'true'}">
			<a href='JavaScript:nextPage();'>[<fmt:message key="aimir.nextpage" />]</a>
		</c:if>		
		
		<c:if test="${paging.nextBlock == 'true'}">
			<a href='JavaScript:nextBlock();'>[<fmt:message key="aimir.next" /> 10  <fmt:message key="aimir.next" />]</a>
		</c:if>
		
	</li>	
</ul>