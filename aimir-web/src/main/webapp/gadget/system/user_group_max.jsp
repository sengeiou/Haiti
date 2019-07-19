<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ include file="/taglibs.jsp" %>
<%
    int i = 0;
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>사용자그룹 상세정보</title>
    
<script>


	$(document).ready(function(){
		
		
		//alert("user_grup_max.jsp");
		
	});
</script>    
</head>


<body >

<!--추가 페이지 operatorMgmtMax.jsp 로 이동 <div id=pane-user-addPage></div> 추가 페이지  끝-->

<!--상세보기 -->
<div id=pane-user-viewPage>
   <form name="roleModel" id="roleModel" method='post'>
      <input type=hidden id="id" name="id" />
      <input type=hidden id="supplier" name="supplier" />
      <input type=hidden id="name" name="name" />
      <input type=hidden id="codes" name="codes" />
      <input type=hidden id="roleId" name="roleId" value=${roleId} />      

      <!--사용자 그룹 상세정보 왼쪽 시작-->
      <div id="btn-right" class="btn_topright_operator"> <!-- // operatorMgmtMax.jsp 함수(addPageView) 호출 -->
      	<ul>
        	<li>
          	<a id="newGroupAdd" href="javascript:addPageView();">
            <span class="greenbold11pt">
            
            	<%-- <fmt:message key="aimir.newGroup"/>&nbsp;<fmt:message key="aimir.button.register"/> --%>
            	<fmt:message key="aimir.button.addRole"/>
            
            </span>
            </a>
           </li>
          </ul>
      </div>

      <!--오른쪽 가젯리스트-->
      <div class="floatright" style="width:470px ">
			<div class="dividedbox-gadgetmanage">
				<label class="bold"><fmt:message key="aimir.permissionGadget"/><!-- 허용된 가젯 --></label>
				<div class="box-bluegradation2">
					<ul>
          	<li>
              <span class="select-gadgetsearchtype">
                <select name="permitedGadgetSearchType" id="permitedGadgetSearchType">
                  <option value="searchName"><fmt:message key="aimir.name"/></option>
                  <option value="searchTag"><fmt:message key="aimir.tag"/></option>
                </select>
              </span>
              <div class="search-s1">
                <ul>
                  <li>
                    <span class="search-s1-input"><input name="permitedGadgetSearchName" id="permitedGadgetSearchName" type="text" value="" onClick="javascript:delPermitedTxt();"></span>
                    <span class="search-s1-btn" style="*margin-top:-19px;"><a id="viewGadgetSearch" href="#;"></a></span>
                  </li>
                </ul>
              </div>
            </li>
            <li>
              <div class="container-multipleselect">
              
              <!-- 허가된 가젯 select 박스 부분. -->
              <select id="permitedGadgets" name="permitedGadgets" multiple="multiple">
              
              </select>
              </div>
					</li>
         </ul>
				</div>
			</div>

			<div class="dividedbox-gadgetmanage-button">
				<ul>
					<li class="btn-putin"><a id="gadgetAddDel" href="#;"><fmt:message key="aimir.remove"/><!-- 제거 --></a></li>
					<li class="btn-putout"><a id="gadgetAddDetail" href="#;"><fmt:message key="aimir.add"/><!-- 추가 --></a></li>
				</ul>
			</div>

			<!-- 전체가젯  -->
			<div id="style-gen1" class="dividedbox-gadgetmanage">
				<label class="bold"><fmt:message key="aimir.allGadget"/><!-- 전체 가젯 --></label>
				<div class="box-bluegradation2">
					<ul>
          	<li>
              <span class="select-gadgetsearchtype">
                <select name="allGadgetSearchType" id="allGadgetSearchType">
                  <option value="searchName"><fmt:message key="aimir.name"/></option>
                  <option value="searchTag"><fmt:message key="aimir.tag"/></option>
                </select>
              </span>
              <div class="search-s1">
                  <ul>
                    <li><span class="search-s1-input"><input name="allGadgetSearchName" id="allGadgetSearchName" type="text" value="" onClick="javascript:delAllGadgetTxt();"></span>
                      <span class="search-s1-btn"  style="*margin-top:-19px;"><a id="allGadgetSearch" href="#;"></a></span>
                    </li>
                  </ul>
              </div>
						</li>
						<li>
              <div class="container-multipleselect">
              <select name="original" id="original" multiple="multiple"></select>
              </div>
						</li>
           </ul>
				</div>
			</div>
    </div>
   	<!--오른쪽 가젯리스트 끝-->
     
    <div class="w_auto" style="width:auto; margin-right:500px">
    <div class="padding-t7px margin-b5px"><label class="check"><fmt:message key="aimir.button.basicinfo"/><!-- 기본정보 --></label></div>
    
    <!-- 베이직 info 부분 start -->
      <table class="customer_detail">
      <colgroup>
		<col width="20%"  />
		<col width="" />
	  </colgroup>
	  <!-- 접속허용여부 => 사용자별로 접속허용여부 체크하므로 사용하지 않음 -->
	  <!--
      <tr>
      	<th class="bold"><fmt:message key="aimir.permittedYn"/></th>
        <td>
          <input name="loginAuthority" id="loginAuthority1" type="radio" value="1" class="trans">
          <input type="text" value="YES" class="border-trans" style="width:50px;">
          <input name="loginAuthority" id="loginAuthority2" type="radio" value="0" class="trans">
          <input type="text" value="NO" class="border-trans" style="width:80px;">
         </td>
      </tr>
      -->      
      <tr>
      	<th class="bold"><fmt:message key='aimir.customerRole.YesNo'/><!-- CurtomerRole 선택 여부--></th>
        <td>
          <input type="text" name="customerRole" id="customerRole" value="" class="border-trans" style="width:50px;" readonly>
         </td>
      </tr>
      <tr>
        <th class="bold"><fmt:message key="aimir.dashboardPermitted"/><!-- 대쉬보드권한 --></th>
        <td>
          <input name="hasDashboardAuth" id="hasDashboardAuth1" type="radio" value="1" class="trans">
          <input type="text" value="YES" class="border-trans" style="width:50px;">
          <input name="hasDashboardAuth" id="hasDashboardAuth2" type="radio" value="0" class="trans">
          <input type="text" value="NO" class="border-trans" style="width:80px;">
         </td>
      </tr>
      <%-- <tr>
      	<th class="bold"><fmt:message key="aimir.measurement"/><!-- 검침데이터 --></th>
        <td>
          <input name="mtrAuthority" id="mtrAuthority1" type="radio" value="r" class="trans">
          <input type="text" value="<fmt:message key="aimir.read"/>" class="border-trans" style="width:50px;">
          <input name="mtrAuthority" id="mtrAuthority2" type="radio" value="w" class="trans">
          <input type="text" value="<fmt:message key="aimir.write"/>" class="border-trans" style="width:80px;">
          <input name="mtrAuthority" id="mtrAuthority3" type="radio" value="c" class="trans">
          <input type="text" value="<fmt:message key="aimir.instrumentation"/>" class="border-trans" style="width:80px;">
        </td>
      </tr> --%>
      <tr>
      	<th class="bold"><fmt:message key="aimir.system.info"/><!-- 시스템정보 --></th>
        <td>
          <input name="systemAuthority" id="systemAuthority1" type="radio" value="r" class="trans">
          <input type="text" value="<fmt:message key="aimir.read"/>" class="border-trans" style="width:50px;">
          <input name="systemAuthority" id="systemAuthority2" type="radio" value="w" class="trans">
          <input type="text" value="<fmt:message key="aimir.write"/>" class="border-trans" style="width:80px;">
         <%--  <input name="systemAuthority" id="systemAuthority3" type="radio" value="c" class="trans">
          <input type="text" value="<fmt:message key="aimir.instrumentation"/>" class="border-trans" style="width:80px;"> --%>
        </td>
      </tr>
      <tr id="maxMeter"> 
      	<th class="bold"><fmt:message key="aimir.maxMeters"/></th> 
        <th class="bold"><input type="text" id=maxMeters name="maxMeters" style="width:100px;" value=0> &nbsp;Maximum number of group command ('0'  is 'Unlimited')</th>
      </tr>
      <tr>
      	<th class="bold"><fmt:message key="aimir.description"/><!-- 설명 --></th>
        <td><textarea name="descr" id="descr" class="descr"></textarea></td>
      </tr>
      
      
      <!--  command execute 부분 start -->
      <tr>
      	<th class="bold"><fmt:message key="aimir.instrumentation"/>&nbsp;<fmt:message key="aimir.execute"/><!-- 명령실행 --></th>
        <td>
            <div class="box-foldertree" id="commandsTree">
              <ul>
              	<c:forEach items="${commandList}" var="parent" >
                  <li id="parent_<%=i++%>"><a href="#" class="parent"><ins>&nbsp;</ins>${parent.key}</a>
  
                    <ul class="align-children">
                      <c:forEach items="${parent.value}" var="children" >
                        <li id="${children.id}"><a><ins>&nbsp;</ins>${children.name}</a></li>
                      </c:forEach>
                    </ul>
                    
                  </li>
                </c:forEach>
              </ul>
            </div>
        </td>
      </tr>
     </table>

  <div id="btn-right" class="btn_right_bottom">
  <!-- delete 버튼 부분 하단 -->
    <ul id="deleteUl" style="display:none"><li><a id="delete" class="on">
    
    <fmt:message key="aimir.button.delete"/><!-- 삭제 --></a></li></ul>
    <ul><li><a id="update" class="on"><fmt:message key="aimir.save2"/><!-- 저장 --></a></li></ul>
  </div>
		
</div>
</form>
</div>
<!--상세보기 끝-->


</body>
</html>



<script>
    //var roleId = ${roleId};
    //var supplierId = ${supplierId};

   // $(function(){
   var maxMeters = 0;
   $(document).ready(function() {

		
        getCommands();

        getGadgets();

        //if (roleId) {
        if ($('#roleId').val() != "") {
            getMyRole();
            getPermitedGadget();
        }
        getGroups();

        $('#roleName').change(function () {
//            roleId = $('#roleName').val();
            $('#roleId').val($('#roleName').val());
            getGadgets();
            getMyRole();
            getPermitedGadget();
        });

        var updateStr = "<fmt:message key="aimir.wouldChange"/>";
        //업데이트
        $("#update").click(function() {
        	maxMeters = $('#maxMeters').val();
            if ( confirm(updateStr)){//"변경 하시겠습니까?") ) {

                var checked_command = [];

                $.tree.plugins.checkbox.get_checked($.tree.reference('#commandsTree')).each( function() {
                    checked_command.push(this.id);
                    //$("#commands_"+this.id).filter("input[value=" + this.id + "]").attr("checked", "checked");
                });

                var code = checked_command.join();
                var code2 = checked_command.join(",");
                //alert("checked_command:"+code+"\nchecked_command2:"+code2);
                //부모 노드 값 들은 제거
                code = code.replace('parent_0,' , '').replace('parent_1,','').replace('parent_2,','') ;

                //$("#codes").attr("value" , code);
                $("#codes").val(code);

                
                var options = {
                    success : roleUpdateResult,
                    url : '${ctx}/gadget/system/user_group_max.do?param=update',
                    type : 'post',
                    datatype : 'json'
                };

                $('#roleModel').ajaxSubmit(options);
            } else
                return;
        });

        var delStr = "<fmt:message key="aimir.msg.wantdelete"/>";

        //삭제 버튼 클릭시 발생 이벤트.
        $("#delete").click( function() {
            if ( confirm(delStr)){ //"삭제 하시겠습니까?") ) {
                var options = {
                    success : roleDeleteResult,
                    url : '${ctx}/gadget/system/user_group_max.do?param=delete' ,
                    type : 'post',
                    datatype : 'json',
                };
                $('#roleModel').ajaxSubmit(options);
            } else
                return;
        });
        
        //딜리트시 콜백함수.
        function roleDeleteResult(responseText, status) 
        {
            alert(responseText.result);
            document.roleModel.action = '${ctx}/gadget/system/operatorMgmtMax.do';
            document.roleModel.submit();
        }

        
        

        $("#newGroupAdd").click( function() {
            addPageView(); // operatorMgmtMax.jsp 함수 호출
        });

        $("#viewGadgetSearch").click( function() {
            gadgetSearch('search');
        });

        $("#gadgetAddDel").click( function() {
            gadgetAddDel();
        });

        $("#gadgetAddDetail").click( function() {
            gadgetAddDetail();
        });

        $("#allGadgetSearch").click( function() {
            allGadgetSearch('search');
        });

        //style 적용
        $("#permitedGadgetSearchType").selectbox();
        $("#allGadgetSearchType").selectbox();
    });


    //#########!!!########### 사용자 그룹명 가져오기
    function getGroups() {

        $.getJSON('${ctx}/gadget/system/user_group_max.do?param=groups', { roleId:$("#roleId").val() , supplierId:supplierId },
            function(json) {
                $('#roleName').pureSelect(json.rolegroups);
                //사용자 그룹명 선택
                $("#roleName option[value=" + roleId + "]").attr("selected", "true");
                //style 적용
                $('#roleName').selectbox();
            }
        );
    }

    //코드에서 명령실행 리스트 가져오기
    //코멘트 execute 리스팅 하는 javascript func
    function getCommands() {
        $('#commandsTree').tree({
            ui : {
                theme_name : "checkbox"
            },
            plugins : {
                checkbox : {
                }
            }
        });
        
        
    }

    //내가 가지고 있는 정보
    function getMyRole() {
        $.getJSON("${ctx}/gadget/system/user_group_max.do?param=myRoleView", { roleId:$("#roleId").val() },
            function(json) {
                if ( json.role != null ) {
                    $('#maxMeters').val(json.role.maxMeters);
                	var l = json.role.loginAuthority;
                    var h = json.role.hasDashboardAuth;
                    var m = json.role.mtrAuthority;
                    var s = json.role.systemAuthority;
					var c = json.role.customerRole;
                    //현재 roleId
                    var id = json.role.id;
                    var len = ${parentSize};
                    //세션아이디
                    var sessionId = ${roleId};
                    //삭제버튼 숨김
                    document.getElementById("deleteUl").style.display = "none";

                    if ( sessionId != id ) {
                        //$("#delete").hide();
                        document.getElementById("deleteUl").style.display = "";
                    }

                    for ( var i=0; i<len; i++) {
                        //체크 모두 해제
                        $("#parent_" + i ).children("ul").children("li").children("input").attr("checked", "");
                        //체크 모두 해제
                        $("#parent_" + i ).children("ul").children("li").children("a").removeClass("checked").addClass("unchecked");
                        //체크 해제 된 부모창 닫기 현재 전부닫음.
                        $("#parent_" + i ).removeClass("closed").addClass("open");
                    }

                    $.each(json.myCommands , function(i,code) {
                        //내가 가지고 있는 명령실행의 부모 노드 열기
                        //$("#"+code.id).parent().parent().removeClass("closed").addClass("open");
                        //내가 가지고 있는 명령실행에 체크하기
                        $("#"+code.id).children("a").removeClass("unchecked").addClass("checked");
                    });

                    $("#name").attr("value" , json.role.name);
                    $("#id").attr("value" , json.role.id);
                    $("#supplier").attr("value" , json.role.supplier);
                    //접속허용여부
                    $("#customerRole").attr("value" , json.role.customerRole);

                    //접속허용여부
                    if ( l == "true" ) {
                        $("#loginAuthority1").filter("input[value=1]").attr("checked", "checked");
                    } else {
                        $("#loginAuthority2").filter("input[value=0]").attr("checked", "checked");
                    }
                    //customerRole 선택 여부
                    if ( c == "true" ) {
                        $("#customerRole").attr("value" , "YES");
                    } else {
                        $("#customerRole").attr("value" , "NO");
                    }
                    //대쉬보드 권한
                    if ( h == "true" ) {
                        $("#hasDashboardAuth1").filter("input[value=1]").attr("checked", "checked");
                    } else {
                        $("#hasDashboardAuth2").filter("input[value=0]").attr("checked", "checked");
                    }
                    //검침데이터 읽기/쓰기
                    if ( m == "r" ) {
                        $("#mtrAuthority1").filter("input[value=r]").attr("checked", "checked");
                    } else if ( m == "w" ) {
                        $("#mtrAuthority2").filter("input[value=w]").attr("checked", "checked");
                    } else {
                        $("#mtrAuthority3").filter("input[value=c]").attr("checked", "checked");
                    }
                    //시스템 정보 읽기/쓰기
                    if ( s == "r" ) {
                        $("#systemAuthority1").filter("input[value=r]").attr("checked", "checked");
                    } else if ( s == "w" ) {
                        $("#systemAuthority2").filter("input[value=w]").attr("checked", "checked");
                    } else {
                        $("#systemAuthority3").filter("input[value=c]").attr("checked", "checked");
                    }
                    $("#descr").attr("value" , json.role.descr);
                } else
                    addPageView(); // operatorMgmtMax.jsp 함수 호출

            }
        );
    }

    //허용된 가젯을 제외한 전체가젯 목록
    function getGadgets() {
        $.getJSON('${ctx}/gadget/system/user_group_max.do?param=remainGadgets', { roleId:$("#roleId").val(), supplierId:supplierId },
            function(json) {
                $('#original').pureSelect(json.remainGadgets);
            }
        );
    }

    //전체가젯
    function getAllGadget() 
    {
    
    	
    
        $.getJSON('${ctx}/gadget/system/user_group_max.do?param=gadgets', {},
            function(json) {
                $('#original').pureSelect(json.gadgets);
            }
        );
    }

    function delPermitedTxt() {
        document.roleModel.permitedGadgetSearchName.value = '';
        return;
    }

    //@허용된 가젯 목록 가져오기
    //@허용된 가젯 목록 가져오기
    //@허용된 가젯 목록 가져오기
    function getPermitedGadget()
    {
    	
    	//alert("getPermitedGadget")	;
    	
    
        $.getJSON('${ctx}/gadget/system/user_group_max.do?param=permitedGadgets', { roleId:$("#roleId").val(), supplierId:supplierId },
        		
        	//콜백함수..
            function(json) 
            {
        	
        		/* alert(json.permitedGadget.id);
        		alert(json.permitedGadget.name); */
            
                $('#permitedGadgets').pureSelect(json.permitedGadget);
            }
        );
    }

    function delAllGadgetTxt() {
        document.roleModel.allGadgetSearchName.value = '';
        return;
    }

    //허용 된 가젯 검색
    function gadgetSearch(type) {
        //검색
        $.getJSON("${ctx}/gadget/system/user_group_max.do?param=viewPermitedSearch" ,
            { permitedGadgetSearchName:$("#permitedGadgetSearchName").val()
            , permitedGadgetSearchType:$("#permitedGadgetSearchType").val()
            , roleId:$("#roleId").val()
            , supplierId:supplierId},
            function(data) {
                $('#permitedGadgets').pureSelect(data.permited);
            }
        );
    }

    //상세보기 전체 가젯 검색
    function allGadgetSearch(type) {
        //검색
        $.getJSON("${ctx}/gadget/system/user_group_max.do?param=viewAllSearch" ,
            { allGadgetSearchName:$("#allGadgetSearchName").val()
            , allGadgetSearchType:$("#allGadgetSearchType").val()
            , roleId:$("#roleId").val()
            , supplierId:supplierId},
            function(data) {
                $('#original').pureSelect(data.remainGadgets);
            }
        );
    }

    var notChooseAddGadgetStr = "<fmt:message key="aimir.notChooseAddGadget"/>";

    //가젯 허용하기
    function gadgetAddDetail() {
        var selnum = 0;
        var addGadgetArr = new Array();

        for(var j=0; j<document.roleModel.original.options.length;j++) {
            if ( document.roleModel.original.options[j].selected == true ) {
                selnum = selnum + 1;
            }
        }

        if ( selnum > 0 ) {
            for (var i=0; i<document.roleModel.original.options.length;i++) {
                var opt = document.roleModel.original.options[i];
                if(opt.selected == true ) {
                    loc = document.roleModel.permitedGadgets.length;
                    var temp = document.roleModel.original.options[i].text;
                    var temp2 = document.roleModel.original.options[i].value;
                    document.roleModel.permitedGadgets.options[loc] = new Option(temp,temp2);
                    addGadgetArr.push(temp2);
                }
            } // end of for

            ////상세보기 화면에서 허용된 가젯 업데이트
            //for (var i =0; i< document.roleModel.permitedGadgets.options.length;i++) {
            //    var l = document.roleModel.permitedGadgets.options[i].value;
            //    $.getJSON("${ctx}/gadget/system/user_group_max.do?param=gadgetAdd" , { gadgetId:l, roleId:$("#roleId").val() },
            //        function(data) {
            //        }
            //    );
            //} // end of for

            // 상세보기 화면에서 허용된 가젯 업데이트
            if (addGadgetArr.length > 0) {
                $.getJSON("${ctx}/gadget/system/user_group_max.do?param=gadgetAdd",
                        { roleId:$("#roleId").val(),
                          supplierId:supplierId,
                          gadgetIds:addGadgetArr.join(",") },
                        function(data) {}                    
                );
            }
        } else {
            alert(notChooseAddGadgetStr);//"추가 할 가젯이 선택되지 않았습니다.");
        }
        delGadget();
    }  //end of fucntion

    function delGadget() {
        for ( var i =0; i< document.roleModel.original.options.length;i++) {
            var opt = document.roleModel.original.options[i];
            if ( opt.selected == true ) {
                document.roleModel.original.options[i] = null;
                i = i -1 ;
            }
        } //end of for
    } //end of function

    var notChooseDelGadgetStr = "<fmt:message key="aimir.notChooseDelGadget"/>";

    //허용된 가젯 삭제
    function gadgetAddDel() {
        addGadget();
        var selnum = 0;
        for(var j=0; j<document.roleModel.permitedGadgets.options.length;j++) {
            if ( document.roleModel.permitedGadgets.options[j].selected == true ) {
                selnum = selnum + 1;
            }
        }
        if ( selnum > 0 ) {
            for ( var i =0; i< document.roleModel.permitedGadgets.options.length;i++) {
                var opt = document.roleModel.permitedGadgets.options[i];
                if ( opt.selected == true ) {
                    document.roleModel.permitedGadgets.options[i] = null;
                    i = i -1;
                }
            }//end of for
        } else {
            alert(notChooseDelGadgetStr);//"삭제할 가젯이 선택되지 않았습니다.");
            return;
        } //end of if
    } //end of function

    // 허용된 가젯 삭제 : 선택한 가젯을 selectbox 에서 삭제하고 서버 삭제함수 호출
    function addGadget() {
        var delGadgetArr = new Array();
        for ( var i =0; i< document.roleModel.permitedGadgets.options.length;i++) {
            var opt = document.roleModel.permitedGadgets.options[i];
            var l = document.roleModel.permitedGadgets.options[i].value;
            if ( opt.selected == true ) {
                loc = document.roleModel.original.length;
                var temp = document.roleModel.permitedGadgets.options[i].text;
                var temp2 = document.roleModel.permitedGadgets.options[i].value;
                document.roleModel.original.options[loc] = new Option(temp,temp2);

                delGadgetArr.push(l);
            }
        } //end of for

        if (delGadgetArr.length > 0) {
            $.getJSON("${ctx}/gadget/system/user_group_max.do?param=gadgetDel",
                    { roleId:$("#roleId").val(), 
                      supplierId:supplierId, 
                      gadgetIds:delGadgetArr.join(",") },
                      //gadgetIds:delGadgetArr },
                    function(data) {}
            );
        }
    } //end of function

    function roleUpdateResult(responseText, status) {
        alert(responseText.result);
        getMyRole();
    }

   
    function test(){alert("test");}

</script>