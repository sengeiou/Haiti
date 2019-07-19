<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp" %>
<%
	int i = 0;
%>

<!DOCTYPE html>

<html>
<head>

</head>
<body>
<div class="pane-user-addpage-in ie9-maring-fix">
<form name="addRoleModel" id="addRoleModel" method='post'>
    <input type="hidden" id="permitedGadgetIds" name="permitedGadgetIds"/>
    <input type="hidden" id="codes" name="codes" />
		<div id="btn-right" class="btn_topright_operator">
			<ul>
                <li>
                 <!--
                    <a id="closeGroupAdd" href="javascript:closeGroupAdd();">   
                        <span class="greenbold11pt"><fmt:message key="aimir.insertPageClose"/></span>
                    </a>
                 -->  
                    <a id="closeGroupAdd" href="javascript:addPageView();">
                        <span class="greenbold11pt"><fmt:message key="aimir.button.addRole"/>
                    </a>
                </li>
            </ul>
		</div>

        <div class="bodyleft_usergroup"> 
        <ul><li>
				<div class="headspace-enter"><label class="check"><fmt:message key="aimir.newGroup"/>&nbsp;<fmt:message key="aimir.button.register"/></label></div>


				<table class="customer_detail">
					<colgroup>
					<col width="20%"  />
					<col width="" />
					</colgroup>
					<tr><th class="darkgraybold11pt"></em><fmt:message key="aimir.supplier"/><!-- 공급사 --></th>
						<td><select name="supplier" id="supplierS" style="width:200px">
								<c:forEach items="${supplier}" var="item" >
									<option value="${item.id}" selected="selected">${item.name}</option>
								</c:forEach>
							</select></td>
					</tr>
					<tr><th class="darkgraybold11pt"><fmt:message key="aimir.group.name"/><!-- 그룹명--></th>
						<td><span><input name="name" id="name1" type="text" value="" style="width:200px;"/></span>
							<span><div id="btn"><ul><li><a id="nameOverlapCheck" class="on"><fmt:message key="aimir.checkDuplication" /></a></li></ul></div></span>
							<div id="checkValue" class="check-overlap check-overlap-usergroup"></div></td>
					</tr>
					<!-- 접속허용여부 -->
					<!--
					<tr><th class="darkgraybold11pt"><fmt:message key="aimir.permittedYn"/></th>
						<td class="gray11pt">
							<input name="loginAuthority" id="loginAuthority3" type="radio" value="1" class="trans">
							<input type="text" value="YES" class="border-trans" style="width:50px;">
							<input name="loginAuthority" id="loginAuthority4" type="radio" value="0" class="trans">
							<input type="text" value="NO" class="border-trans" style="width:80px;"></td>
					</tr>
					-->
                    <tr><th class="darkgraybold11pt"><fmt:message key='aimir.customerRole.YesNo'/><!-- CurtomerRole 선택 여부--></th>
                        <td class="gray11pt">
                          <input name="customerRole" id="isCustomerRoleAdd" type="radio" value="1" class="trans">
                          <input type="text" value="YES" class="border-trans" style="width:50px;">
                          <input name="customerRole" id="isNotCustomerRoleAdd" type="radio" value="0" class="trans">
                          <input type="text" value="NO" class="border-trans" style="width:80px;"></td>
                     </tr>
                    <tr><th class="darkgraybold11pt"><fmt:message key="aimir.dashboardPermitted"/><!-- 대쉬보드권한--></th>
						<td class="gray11pt">
							<input name="hasDashboardAuth" id="hasDashboardAuth3" type="radio" value="1" class="trans">
							<input type="text" value="YES" class="border-trans" style="width:50px;">
							<input name="hasDashboardAuth" id="hasDashboardAuth4" type="radio" value="0" class="trans">
							<input type="text" value="NO" class="border-trans" style="width:80px;"></td>
					</tr>
					<%-- <tr><th class="darkgraybold11pt"><fmt:message key="aimir.measurement"/><!--검침데이터--></th>
						<td class="gray11pt">
							<input name="mtrAuthority" id="mtrAuthority4" type="radio" value="r" class="trans">
							<input type="text" value="<fmt:message key="aimir.read"/>" class="border-trans" style="width:50px;">
							<input name="mtrAuthority" id="mtrAuthority5" type="radio" value="w" class="trans">
							<input type="text" value="<fmt:message key="aimir.write"/>" class="border-trans" style="width:80px;">
							<input name="mtrAuthority" id="mtrAuthority6" type="radio" value="c" class="trans">
							<input type="text" value="<fmt:message key="aimir.instrumentation"/>" class="border-trans" style="width:80px;"></td>
					</tr>  --%>
					<tr><th class="darkgraybold11pt"><fmt:message key="aimir.system.info"/><!-- 시스템정보--></th>
						<td class="gray11pt">
							<input name="systemAuthority" id="systemAuthority4" type="radio" value="r" class="trans">
							<input type="text" value="<fmt:message key="aimir.read"/>" class="border-trans" style="width:50px;">
							<input name="systemAuthority" id="systemAuthority5" type="radio" value="w" class="trans">
							<input type="text" value="<fmt:message key="aimir.write"/>" class="border-trans" style="width:80px;">
							<!-- 
							<input name="systemAuthority" id="systemAuthority6" type="radio" value="c" class="trans">
							<input type="text" value="<fmt:message key="aimir.instrumentation"/>" class="border-trans" style="width:80px;">
							-->
							</td>
					</tr>
					<tr id="maxMeter"> 
				      	<th class="darkgraybold11pt"><fmt:message key="aimir.maxMeters"/></th> 
				        <th><input type="text" id=maxMeters name="maxMeters" style="width:100px;" value=0> &nbsp;Maximum number of group command ('0'  is 'Unlimited')</th>
				      </tr>
					<tr><th class="darkgraybold11pt"><fmt:message key="aimir.description"/><!-- 설명 --></th>
						<td><textarea name="descr" id="descr1" class="descr-edit"></textarea></td>
					</tr>
					
					<!-- Command part start -->
					<tr><th class="darkgraybold11pt"><fmt:message key="aimir.instrumentation"/><!-- 명령실행 --></th>
						<td>

							  <div class="box-foldertree" id="addCommandsTree" style="height:196px; #height:192px;">
								  <ul><c:forEach items="${commandList}" var="parent" >
									  <li id="addParent_<%=i++%>">
										<a href="#" class="parent"><ins>&nbsp;</ins>${parent.key}</a>

										  <ul class="align-children">
											  <c:forEach items="${parent.value}" var="children" >
												  <li id="add_${children.id}"><a><ins>&nbsp;</ins>${children.name}</a>
													<input type="checkbox" name="commands" id="addCommands_${children.id}" value="${children.id}" style="display:none"/>
												  </li>
											  </c:forEach>
										  </ul>
									  </li>
									  </c:forEach>
								  </ul>
							  </div>

						</td>
					</tr>
				</table>


				<div id="btn-right" class="btn_fullright_bottom">
					<ul><li><a id="cancel" href='#'><fmt:message key="aimir.cancel"/><!-- 취소 --></a></li></ul>
					<ul><li><a id="roleAdd" class="on"><fmt:message key="aimir.save2"/><!-- 저장--></a></li></ul>
				</div>

			</li>
		</ul>
		</div>




        <!--오른쪽 가젯리스트-->
        <div class="bodyright_usergroup">

			<div class="dividedbox-gadgetmanage">
				<label class="graybold11pt"><fmt:message key="aimir.permissionGadget"/><!-- 허용된 가젯 --></label>
				<div class="box-bluegradation2">
				<ul>
					<li style="height:21px;"></li>
					<li>
						<div class="container-multipleselect">
						<select id="permitedGadgets1" name="permitedGadgets" multiple="multiple"></select>
						<div>
					</li>
				</ul>
				</div>

				<!--<ul><li>
					<div id="permitedGadgetSize">
						<ul><li>총 :  개</li></ul>
					</div>
					</li></ul>-->

			</div>

			<div class="dividedbox-gadgetmanage-button">
				<ul>
					<li class="btn-putin"><a href="javascript:gadgetDel('detail');"><fmt:message key="aimir.remove"/></a></li>
					<li class="btn-putout"><a href="javascript:gadgetAdd('detail');"><fmt:message key="aimir.add"/></a></li>
				</ul>
			</div>


			<!-- 전체가젯  -->
			<div id="style-gen1" class="dividedbox-gadgetmanage">
				<label class="graybold11pt"><fmt:message key="aimir.allGadget"/><!-- 전체 가젯 --></label>
				<div class="box-bluegradation2">
					<ul><li>
						<span class="select-gadgetsearchtype">
							<select name="allGadgetSearchType" id="allGadgetSearchType">
								<option value="searchName"><fmt:message key="aimir.name"/></option>
								<option value="searchTag"><fmt:message key="aimir.tag"/></option>
							</select>
						</span>
						<div class="search-s1">
								<ul>
									<li><span class="search-s1-input"><input name="allGadgetSearchName1" id="allGadgetSearchName1" type="text" value="" onclick="javascript:delAllGadgetTxt();"></span>
										<span class="search-s1-btn"><a href="javascript:addAllGadgetSearch('search');"></a></span>
									</li>
								</ul>
						</div>

						</li>
						<li><div class="container-multipleselect">
							<select name="original" id="original1" multiple="multiple"></select>
							</div>
						</li></ul>
				</div>

				<!--<ul><li>
					<div id="remainGadgetSize">
						<ul><li>총 :  개</li></ul>
					</div>
					</li></ul>-->
			</div>


        </div>
        <!--오른쪽 가젯리스트 끝-->


</form>
</div>

</body>

</html>



<script>

    $(function(){

        //명령실행 가져오기
        getCommands();
        //전체가젯 가져오기
        getAllGadget();

        //명령실해 트리 모두 열기
        for ( var i=0; i<3; i++) {
            //체크 모두 해제
            $("#addParent_" + i ).children("a").removeClass("undetermined").addClass("unchecked");
            $("#addParent_" + i ).children("ul").children("li").children("a").removeClass("checked").addClass("unchecked");
            //부모창  전부 열기.
            $("#addParent_" + i ).removeClass("closed").addClass("open");
        }
        $("#pane-user-addPage").show();


        //취소
        $("#cancel").click(function() {
            //addPageView();

            hiddenAddPage(); 
            return;

            //이하 기존 소스
/*
            //추가 페이지
            $("#pane-user-addPage").hide();
            //명령실행가져오기
            getCommands();
            //가젯가져오기
            getGadgets();
            if (roleId) {
                //나의 정보가져오기
                getMyRole();
                //나의 허용된 가젯
                getPermitedGadget();
                //그룹명
                getGroups();
            }
            //상세보기화면
            $("#pane-user-viewPage").show(); 
*/            
        });

        //css 적용
        $("#allGadgetSearchType").selectbox();
        $("#supplierS").selectbox();

    });

    //코드에서 명령실행 리스트 가져오기
    function getCommands() {
        $('#addCommandsTree').tree({
            ui : {
                theme_name : "checkbox"
            },
            plugins : {
                checkbox : {
                }
            }
        });
    }

    var msgStr1 = "<fmt:message key="aimir.availableGroupName"/>"; //사용가능한 그룹명입니다.
    var msgStr2 = "<fmt:message key="aimir.redundantGroupName"/>"; //중복된 그룹명이 있습니다
    var msgStr3 = "<fmt:message key="aimir.duplicateGroupName"/>"; //"그룹명 중복 확인을 해 주세요"

    //중복체크
    $(function(){
        $("#nameOverlapCheck").click(function() {
            $("#checkValue").hide();
            var name = $("#name1").val();
            if ( name == "" || name.length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg4'/>"); //그룹명을 입력해 주세요
                return;
            }
            $.getJSON('${ctx}/gadget/system/user_group_max.do?param=overlapcheck', { name:name },
                function(json) {
                    if ( json.count == 0 ) {
                        $("#checkValue").html("<li class='available' style='width:300px';>" + msgStr1 + "</li>");
                        $("#checkValue").show();
                    } else {
                        $("#checkValue").html("<li class='reject' style='width:300px';>" + msgStr2 + "</li>");
                        $("#checkValue").show();
                        $("#name1").val('');
                        $("#name1").focus();
                    }
                    $("#checkYN").val(json.checkYN);
                }
            );
        });
    });

    var msgStr4 = "<fmt:message key="aimir.chooseAllowAccess"/>"; //접속 허용여부를 선택 해 주세요
    var msgStr5 = "<fmt:message key="aimir.privilegeDashboard"/>"; //"대시보드 권한을 선택 해 주세요"
    var msgStr6 = "<fmt:message key="aimir.readingDataSelect"/>"; //검침데이터를 선택 해 주세요
    var msgStr7 = "<fmt:message key="aimir.checkSystemInfo"/>"; //시스템 정보를 체크 해 주세요
    var msgStr8 = "<fmt:message key="aimir.wouldSave"/>"; //"저장 하시겠습니까?"
    var msgStr9 = "<fmt:message key="aimir.chooseCustomerRole"/>"; //Customer Role 선택여부를 선택 해 주세요

    //Role 추가
    $(function(){
    	
	// 저장시 실행되는 func    	
        $("#roleAdd").click(function() 
   		{
            var checkYN = $("#checkYN").val();

            if ( checkYN == "false" ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr4);
                $("#name1").focus();
                $("#name1").val('');
                return;
            }

            //접속허용여부
            if ( $("#loginAuthority3:checked").length == 0 && $("#loginAuthority4:checked").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr4);
                $("#loginAuthority3").focus();
                return;
            }
            //접속허용여부
            if ( $("#isCustomerRoleAdd:checked").length == 0 && $("#isNotCustomerRoleAdd:checked").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr9);
                $("#isCustomerRoleAdd").focus();
                return;
            }
            //대쉬보드 권한
            if ( $("#hasDashboardAuth3:checked").length == 0 && $("#hasDashboardAuth4:checked").length == 0 ) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr5);
                $("#hasDashboardAuth3").focus();
                return;
            }
            //검침데이터 읽기/쓰기
            /* if ( $("#mtrAuthority4:checked").length == 0 && $("#mtrAuthority5:checked").length == 0 && $("#mtrAuthority6:checked").length == 0) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr6);
                $("#mtrAuthority4").focus();
                return;
            } */
            //시스템 정보 읽기/쓰기
            if ( $("#systemAuthority4:checked").length == 0 && $("#systemAuthority5:checked").length == 0 && $("#systemAuthority6:checked").length == 0) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msgStr7);
                $("#systemAuthority4").focus();
                return;
            }
            
            
            //command add part start --js func
            if ( confirm(msgStr8) ) {
            	
            	//alert("rolladd===>");
            	
            	

                var selOpt = $("#permitedGadgets1 option");
                var selLen = selOpt.size();

                // 허용된 가젯
                if ( selLen > 0 ) {
                    var permitedGadgetArr = new Array();

                    for (var i = 0 ; i < selLen ; i++) {
                        permitedGadgetArr.push(selOpt[i].value);
                    } // end of for
                    $("#permitedGadgetIds").val(permitedGadgetArr.join(","));
                }
                else
                {
                	//차후 메세지 프로퍼티로 수정.
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.alert.msg.permissionGadget" />');
                	return false;
                }
                

                // 체크된 명령실행 저장
                var checked_command = [];

                $.tree.plugins.checkbox.get_checked($.tree.reference('#addCommandsTree')).each( function() {
                    checked_command.push(this.id);
                });
                
                //alert(checked_command);

                var code = checked_command.join();
                
              //부모 체크 된 값 들은 제거
                code = code.replace('addParent_0,' , '').replace('addParent_1,','').replace('addParent_2,','') ;
                
                
                
              //  alert(code);
                
               // alert(code.length);

                
                $("#codes").attr("value" , code);

                var options = {
                    success : roleAddResult,
                    url : '${ctx}/gadget/system/user_group_max.do?param=add2' ,
                    type : 'post',
                    datatype : 'json',
                    data:{codes:code}
                };
                
                //ajax submit
                $('#addRoleModel').ajaxSubmit(options);
                
                //window.location.href = "${ctx}/gadget/system/operatorMgmtMax.do";
                
                
            }
        });// roleAdd End
    });

    function roleAddResult(responseText, status) {
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',responseText.result);

        for ( var i=0; i<3; i++) {
            //체크 모두 해제
            $("#addParent_" + i ).children("a").removeClass("undetermined").addClass("unchecked");
            $("#addParent_" + i ).children("ul").children("li").children("a").removeClass("checked undetermined").addClass("unchecked");
            //부모창  전부 열기.
            $("#addParent_" + i ).removeClass("closed").addClass("open");
        }

        reset();

        //전체가젯 가져오기
        getAllGadget();
        //상세보기에서 허용 된 가젯 리스트 리셋
        resetPermitedGadget();
        getGroups();
        //max가젯을 reload        
        document.roleModel.action = '${ctx}/gadget/system/operatorMgmtMax.do';
        document.roleModel.submit();
    }

    //전체가젯
    function getAllGadget() 
    {
    	
    	
    	var roleId = $("#roleId").val()
    	
    	//alert("롤id는==>"+ roleId);
        
        $.ajax({
            type:"POST",
            dataType:"json",
            data:{
            roleId:roleId, 
            supplierId:supplierId},
            url:"${ctx}/gadget/system/user_group_max.do?param=gadgets",
            success:function(json, status) 
            {
            	//alert("성공");
            	
            	$('#original1').pureSelect(json.gadgets);
            },
            error:function(request, status)
            {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"getAllGadget fetch failed");
            }
        });// ajaxEnd
        
        
        
    }

    //허용된 가젯 리스트 리셋
    function resetPermitedGadget() {
        for ( var i =0; i< document.addRoleModel.permitedGadgets.options.length;i++) {
            var opt = document.addRoleModel.permitedGadgets.options[i];
            document.addRoleModel.permitedGadgets.options[i] = null;
            i = i -1;
        }
    }

    //가젯 허용하기
    function gadgetAdd(type) {
        //var selnum = 0;
        var isSel = false;
        var originOpt = document.addRoleModel.original.options;
        var len = originOpt.length;

        if ( $("#original1 option:selected").size() > 0 ) {
            //for (var i=0; i<document.addRoleModel.original.options.length;i++) {
            for (var i = 0 ; i < len ; i++) {
                var opt = originOpt[i];
                if(opt.selected == true ) {
                    loc = document.addRoleModel.permitedGadgets.length;
                    var temp = originOpt[i].text;
                    var temp2 = originOpt[i].value;
                    document.addRoleModel.permitedGadgets.options[loc] = new Option(temp,temp2,false,true);
                }
            } // end of for
        } else {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"추가 할 가젯이 선택되지 않았습니다.");
        }
        addDelGadget();
    }  //end of fucntion

    function addDelGadget() {
        for ( var i =0; i< document.addRoleModel.original.options.length;i++) {
            var opt = document.addRoleModel.original.options[i];
            if ( opt.selected == true ) {
                document.addRoleModel.original.options[i] = null;
                i = i -1 ;
            }
        } //end of for
    } //end of function

    //허용된 가젯 삭제
    function gadgetDel(type) {
        add_Gadget(type);
        var selnum = 0;
        for(var j=0; j<document.addRoleModel.permitedGadgets.options.length;j++) {
            if ( document.addRoleModel.permitedGadgets.options[j].selected == true ) {
                selnum = selnum + 1;
            }
        }
        if ( selnum > 0 ) {
            for ( var i =0; i< document.addRoleModel.permitedGadgets.options.length;i++) {
                var opt = document.addRoleModel.permitedGadgets.options[i];
                if ( opt.selected == true ) {
                    document.addRoleModel.permitedGadgets.options[i] = null;
                    i = i -1;
                }
            }//end of for
        } else {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"삭제할 가젯이 선택되지 않았습니다.");
            return;
        } //end of if
    } //end of function

    function add_Gadget(type) {
        for ( var i =0; i< document.addRoleModel.permitedGadgets.options.length;i++) {
            var opt = document.addRoleModel.permitedGadgets.options[i];
            var l = document.addRoleModel.permitedGadgets.options[i].value;
            if ( opt.selected == true ) {
                loc = document.addRoleModel.original.length;
                var temp = document.addRoleModel.permitedGadgets.options[i].text;
                var temp2 = document.addRoleModel.permitedGadgets.options[i].value;
                document.addRoleModel.original.options[loc] = new Option(temp,temp2);
            }
        } //end of for
    } //end of function

    function reset() {
        //접속허용여부
        //$("#loginAuthority3").filter("input[value=1]").attr("checked", "");
        //$("#loginAuthority4").filter("input[value=0]").attr("checked", "");
        //CustomerRole 선택 여부
        $("#isCustomerRoleAdd").filter("input[value=1]").attr("checked", "");
        $("#isNotCustomerRoleAdd").filter("input[value=0]").attr("checked", "");
        //대쉬보드 권한
        $("#hasDashboardAuth3").filter("input[value=1]").attr("checked", "");
        $("#hasDashboardAuth4").filter("input[value=0]").attr("checked", "");
        //검침데이터 읽기/쓰기
       /*  $("#mtrAuthority4").filter("input[value=r]").attr("checked", "");
        $("#mtrAuthority5").filter("input[value=w]").attr("checked", "");
        $("#mtrAuthority6").filter("input[value=c]").attr("checked", ""); */
        //시스템 정보 읽기/쓰기
        $("#systemAuthority4").filter("input[value=r]").attr("checked", "");
        $("#systemAuthority5").filter("input[value=w]").attr("checked", "");
        //$("#systemAuthority6").filter("input[value=c]").attr("checked", "");
        //그룹명
        $("#name1").val('');
        //설명
        $("#descr1").attr("value" , '');
        $("#allGadgetSearchName1").attr("value", '');
        //중복체크 값
        $("#checkYN").val('false');
        $("#checkValue").hide();
    }

    //ADD화면 가젯 검색
    function addAllGadgetSearch(type) {
        var search = $("#allGadgetSearchName1").val();
        var searchType = $("#allGadgetSearchType").val();
        //var l = document.addRoleModel.allGadgetSearchName1;

        //검색
        //if ( l.value == '' || l.length == 0 ) {
        //    search = '%%';
        //}
        //search = $("#allGadgetSearchName1").val();

        // ADD화면에서 가젯 검색 시 가젯 전체에서 검색하기 위해 roleId 를 -1로 넘김
        $.getJSON("${ctx}/gadget/system/user_group_max.do?param=viewAllSearch",
            //{ allGadgetSearchName:search, allGadgetSearchType:searchType, roleId:$("#roleId").val(), supplierId:supplierId },
            { allGadgetSearchName:search, allGadgetSearchType:searchType, roleId:-1, supplierId:supplierId },
            function(data) {
                $('#original1').pureSelect(data.remainGadgets);
            }
        );
    }

    function delAllGadgetTxt() {
        document.addRoleModel.allGadgetSearchName1.value = '';
        return;
    }

    function closeGroupAdd(){
        hiddenAddPage(); // user_group_max.jsp 함수 호출...
    }


</script>