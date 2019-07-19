<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script type="text/javascript" src="${ctx}/js/prevention.js"></script>
<head>
<style type="text/css">
    div a:link{text-decoration:none; color:white}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">

        var userId;
        var memoObj;
        var memoCount;  //전체 메모의 개수
        var memoList = []; //조회된 메모를 담는다
        var memoLoad = false; //조회성공시 true이 변경된다.
        var memolen = 0; //조회된 메모의 개수
        
        var startIndex = 0; //페이징을 시작할 인덱스 번호
        var maxIndex = 12; //페이징의 크기, 즉 한번 페이징 할 때마다 가져올 목록의 개수

        //로그인 아이디
        function getUser() {
            jQuery.ajaxSetup( {
                cache : false
            });
            $.getJSON('${ctx}/gadget/system/memo/getUser.do', function(json) {
                userId = json.user;
                getMemo(userId); //가져온 아이디로 리스트 로딩
            });
        }

        //init
        function init(){
            getUser();
            var add = "<a href='javascript:submitDefault()' class=\"on\">" + "<fmt:message key='aimir.memo.add'/>" + "</a>";
                $('#addmemo').html(add);                
        }

            
        //리스트 조회
        function getMemo(userId) {
//          alert("????");
            if(userId != null) {
                memoLoad = true;
                //최초 조회시 가장 최근의 12개의 메모를 조회하게된다
                $.getJSON('${ctx}/gadget/system/memo/ListMemos.do',{userId:userId, startIndex:startIndex, maxIndex:maxIndex},
                    function(json) {
                        memoObj = json.memo;
                        memoCount = json.memocount; //전체메모 카운트
                        bindingMemo("new"); 
                });
            }else{
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.memo.loginmsg' />");
            }
        }

        //binding
        function bindingMemo(type) {
//          alert("!!!!!!!!!");
            var innerHtml = "";
            var pageHtml = "";

            //innerHtml
            if (memoObj == "" && type == "search") {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.extjs.empty' />");
                    return;
            }else{
                //메모 리스트 생성
                $.each(memoObj, function(mIndex, memo){
                    //<br/>을 \r\n 으로 치환
                    var content = memo.cont.replace("<br/>", "\r\n");
                    
                    memoList[mIndex] = memo;
                    memolen = memoList.length;
                    
                    innerHtml += "  <div id='draggable"+ mIndex +"'" + "style='width:23.5%;height:170px;color:white;" +
                                 "       padding:5px 5px 5px 5px; float:left; background:#3399ff;margin:1px' align='left'" + 
                                 "      onMouseOver=" + "this.style.backgroundColor='red'"+ 
                                 "      onMouseOut=" + "this.style.backgroundColor='#3399ff' class='ui-widget-shadow ui-corner-all'>" + 
                                 "  <form id='viewmemo" + mIndex + "' modelAttribute='memo'>" +
                                 "      <div style='float:left;color:white;'>" +
                                 memo.in_date + 
                                 "      </div>" +
                                 "      <div style='float:right; color:white;'>" + 
                                 "              <a href='javascript:deleteMemo(" + memo['Id'] + ")'><fmt:message key='aimir.button.delete'/></a>" +
                                 "      </div>" +
                                 "      <div style='clear:both;width:93.3%; height:130px; padding:10px;background:#ffffff;'" +
                                 "              class='ui-widget ui-widget-content ui-corner-all'>" +
                                 "      <textarea name='cont' id='cont' style='width:100%; height:130px; border:0;line-height:120%;" + 
                                 "              background-color:white; font-size:12pt; color:black; overflow:auto'" + 
                                 "              onfocus=\"this.style.color='blue'; this.style.textDecoration='underline'\"" + 
                                 "              onblur=\"this.style.color='black'; this.style.textDecoration='none';" + 
                                 "                      javascript:updateMemo('viewmemo" + mIndex + "'," + 
                                 "                      '" + memoList[mIndex].cont + "')\"" + //form의 아이디와 최초 메모내용을 인자로 한다. 
                                 "              onkeyup='javascript:fc_chk_byte(this,300)'>" + content +
                                 "</textarea>" +
                                 "</div>" +
                                 "          <input type='hidden' name='id' value="+ memo['Id'] +" />" + 
                                 "          <input type='hidden' name='userId' value="+ memo['userId'] +" />" +
                                 "          <input type='hidden' name='in_date' value="+ memo['in_date'] +" />" +
                                 "  </form>" +
                                 "  </div>";
                });
//              alert("$$$$$$$$");          
            }
                $('#memoboard2').html(innerHtml);

                pagebtn(type); //페이징 버튼 생성
                
                if(memolen == 0){
                    
                }
        }

        function pagebtn(type){
            if(type == "new")
            {
                pageHtml = "<div id='btn'>" +
                           "<ul>" + 
                           "<li>" +
                           "<a href=\"javascript:selectpage('before')\" class=\"on\">" + "<fmt:message key='aimir.memo.pre'/>" + "</a>" +
                           "</li>" +
                           "</ul>" +
                           "<ul>" +
                           "<li>" +
                           "<a href=\"javascript:selectpage('next')\" class=\"on\">" + "<fmt:message key='aimir.next'/>" + "</a>" +
                           "</li>" +
                           "</ul>" +
                           "</div>";
            }else{
                pageHtml = "<div id='btn'>" +
                           "<ul>" + 
                           "<li>" +
                           "<a href=\"javascript:getMemo(" + userId + ")\">" + "<fmt:message key='aimir.memo.list'/>" + "</a>" +
                           "</li>" +
                           "</ul>" +
                           "</div>";
            }
            $('#paging').html(pageHtml);
        }

        function cx_trim(str){  //공백제거 함수
            return str.replace(/(^\s*)|(\s*$)/g,""); //공백제거 후 return
        }
                
        //update
        function updateMemo(formId, words){ 
            var newconts = document.getElementById(formId).cont.value;
            //내용 변경 전, 변경 후 비교
            if(cx_trim(words) != cx_trim(newconts)){ //변경이 있을 경우 update 수행
                var options = {
                    success : memoUpdateResult,
                    url : '${ctx}/gadget/system/memo/updateMemo.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#'+formId).ajaxSubmit(options);
            }else{ //변경이 없으면 반응없음.
                //alert("변경된 내용이 없습니다.");
            }
        }
        
        //search
        function searchMemo(){
            var word = document.getElementById("searchmemo").word.value;
            word = encodeURIComponent(word);
            if(word != "") {
                memoLoad = true;
                $.getJSON('${ctx}/gadget/system/memo/searchMemo.do', {word:word},
                    function(json) {
                        memoObj = json.memo;
                        bindingMemo("search");  
                });
            }else{
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"검색어를 입력하세요");
            }
        }

        
        //addnew
        function submitDefault() {
            var options = {
                success : memoAddResult,    
                url : '${ctx}/gadget/system/memo/addMemo.do',  
                type : 'post',
                datatype : 'json'
            };
            $('#addnewmemo').ajaxSubmit(options);    
        }

        //add 후 처리
        function memoAddResult(responseText, status) {
            //success 메세지
//          alert(responseText.result);

            //메모목록 refresh 
            getMemo(userId);
        }

        //update 후 처리
        function memoUpdateResult(responseText, status) {
            //alert(responseText.result);
        }



        //개별삭제
        function deleteMemo(id) {
            if(memoLoad == true) {
                $.ajax({ 
                    url: "${ctx}/gadget/system/memo/deleteMemo.do?Id=" + id, 
                    cache: false, 
                    success: function(){
                        locLoad = false;
                    }
                }); 
            }
            getMemo(userId);
        }

        //전체삭제
        function deleteAll() {
            if(confirm("<fmt:message key='aimir.memo.deleteall' />")){ //전체 삭제 실행시 confirm
                if(memolen == 0){   //메모의 갯수가 0일 경우
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.memo.nomemomsg' />");
                }else{  //메모가 존재할 경우
                    if(memoLoad == true) {
                        $.ajax({ 
                            url: "${ctx}/gadget/system/memo/deleteAll.do?userId=" + userId, 
                            cache: false, 
                            success: function(){
                            locLoad = false;
                            }
                        }); 
                    }
                    memolen = 0; //전제 삭제 실행 후 전체 메모의 갯수가 0이된다.
                    getMemo(userId);
                }
            }
        }

        //Textarea 글자수 제한 및 체크
        function fc_chk_byte(aro_name,ari_max){

            var ls_str = aro_name.value;    // 이벤트가 일어난 컨트롤의 value 값
            var li_str_len = ls_str.length; // 전체길이

            var li_max = ari_max;           // 제한할 글자수 크기
            var i = 0;                      // for문에 사용
            var li_byte = 0;                // 한글일경우는 2 그밗에는 1을 더함
            var li_len = 0;                 // substring하기 위해서 사용
            var ls_one_char = "";           // 한글자씩 검사한다
            var ls_str2 = "";               // 글자수를 초과하면 제한할수 글자전까지만 보여준다.
        
            for(i=0; i< li_str_len; i++){
                
                ls_one_char = ls_str.charAt(i); //한글자추출
        
                if (escape(ls_one_char).length > 4){
                    li_byte += 2;   //한글이면 2를 더한다.
                }else{
                    li_byte++;      //그 외의 경우 1을 더함
                }

                if(li_byte <= li_max){
                    li_len = i + 1; //전체 크기가 li_max를 넘지않으면
                }
            }
        
            if(li_byte > li_max){   // 전체길이를 초과하면
                Ext.Msg.alert('<fmt:message key='aimir.message'/>', li_max + " <fmt:message key='aimir.memo.maxlength' /> ");
                ls_str2 = ls_str.substr(0, li_len);
                aro_name.value = ls_str2;
            }
            aro_name.focus(); 
        }

        //검색창 
        function delTxt() {
            var text = document.searchmemo;    
            text.word.value = '';
            flag = true;    
            return;
        }

        //페이징 버튼
        function selectpage(choice){
            if(choice == 'before' && startIndex > 0){
                startIndex = startIndex-12;
            }else if(choice == 'next' && startIndex < memoCount-1){
                startIndex = startIndex+12;
                if(startIndex > memoCount-1)
                    startIndex = startIndex-12;
            }

            //재설정된 startIndex 변수로 페이징 조회
            if(userId != "") {
                memoLoad = true;
                $.getJSON('${ctx}/gadget/system/memo/ListMemos.do',{userId:userId, startIndex:startIndex, maxIndex:maxIndex},
                    function(json) {
                        memoObj = json.memo;
                        bindingMemo("new"); 
                });
            }else{
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.memo.loginmsg' />");
            }
        }
    </script>
</head>
<body onload="init()" oncontextmenu="return false" onselectstart="return false" ondragstart="return false">
<div id="gadget_body">


    <div id="btn" class="div-con1">
        <form:form id="addnewmemo" modelAttribute="memo">
        <ul>
            <li id="addmemo">
            </li>
        </ul>
        </form:form>
    </div>
    <div id="style-gen1" style="width:50%" >
        <div id="style-gen1" style="width:100%;">
        <form:form id="searchmemo" name="searchmemo">
        <div id="style-gen1" style="width:30%">
            <input type="text" name="word" id="text-set1"
                        value='<fmt:message key="aimir.memo.search"/>' onclick='javascript:delTxt();'></input>
        </div>
        <div id="btn" style="float:left;margin-left:3%;">
        <ul>
            <li><a href="javascript:searchMemo()" class="on"><fmt:message key="aimir.button.search"/></a></li>
        </ul>
        </div>
        </form:form>
        </div>
    </div>
    <div id="btn" style="float:right">
        <ul id="style-gen2" style="margin-right:20px">
            <li><a href="javascript:deleteAll()"><fmt:message key="aimir.memo.delall"/></a></li>
        </ul>
    </div>
    <div id="memoboard2"></div>

    <div id='paging'></div>
                
</div>
</body>
</html>