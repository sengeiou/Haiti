<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

    <script type="text/javascript" charset="utf-8">
    var userId;
    var memoLoad = false;
    var memoObj;
    var memolist = [];
    var arr_len;            //memolist의 길이
    var current_memo = 0;   //memolist에서의 인덱스 값
    var innerHtml = "";
    var hiddenHtml = "";

    function init(){
        getUser();
    }

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

    function getMemo(userId){
        if(userId != null) {
            memoLoad = true;
            $.getJSON('${ctx}/gadget/system/memo/getMemo.do',{userId:userId},
                function(json) {
                    memoObj = json.memo;
                    bindingMemo();
            });
        }

        function bindingMemo() {
            if (memoObj == "") {

                arr_len = 0;
                innerHtml = "0/0";
                document.getElementById('saveit').style.display = 'none'; //메모가 없을 때는 저장 버튼을 display한다.

                hiddenHtml = "<div class='ui-dialog-content ui-widget-content' id='in-bord2'>" +
                 "<form id='memoDefault' modelAttribute='memo'>" +
                 "<textarea id='cont' name='cont' readonly='readonly'" +
                 " onkeyup='javascript:fc_chk_byte(this,300)'>" +
                 "<fmt:message key='aimir.data.notexist'/>" +
                 "</textarea>" +
                 "</form>" +
                 "</div>";
            }

            
            else {

                if (memoObj != null) {
                    $.each(memoObj, function(mIndex, memo){
                        memolist[mIndex] = memo;
                    });
                }
                arr_len = memolist.length;  //전체 글의 갯수 셋팅
//              document.getElementById('cont').value = memolist[current_memo].cont;    //가장 최근 메모의 내용을 textarea안에 입력
                innerHtml = "<ul style=\"float:right\">" +
                            "<li>" +arr_len + " / " + arr_len + "</li>" +  //현재 글의 순서 표시 (현재글순서 / 총 글의 갯수) 최초 가장 최근의 글이 선택되므로 "총갯수/총갯수"가 된다.
                            "</ul>";

                hiddenHtml = "<div id='in-bord1'>" +
                             "<form id='memoDefault' modelAttribute='memo'>" +
                             "<textarea id='cont' name='cont'" +
                             " onkeyup='javascript:fc_chk_byte(this,500)'>" +
                             memolist[current_memo].cont +
                             "</textarea>" + "<div id='current_memo' style=\"float:right\">11</div>"+
                             "<input type='hidden' name='id' id='id' value='" + memolist[current_memo].Id + "'/>" +
                             "<input type='hidden' name='userId' id='userId' value='" + memolist[current_memo].userId + "'/>" +
                             "<input type='hidden' name='in_date' id='in_date' value='"+ memolist[current_memo].in_date +"' />" +
                             "</form>" +
                             "</div>";
            }

            $('#memoboard1').html(hiddenHtml);

            $('#current_memo').html(innerHtml);
        }
    }

    //update
    function updateMemo(){
        var newconts = document.getElementById("cont").value;
            var options = {
                success : memoUpdateResult,
                url : '${ctx}/gadget/system/memo/updateMemo.do',
                type : 'post',
                datatype : 'json'
            };
            $('#memoDefault').ajaxSubmit(options);
    }

    //update 후 처리
    function memoUpdateResult(responseText, status) {
        //alert(responseText.result);
    }

    //이전 글 가져오기
    function getBeforeMemo(){
        if(current_memo == memolist.length-1 || arr_len == 0){
            //alert("이전 메모가 없습니다.");
        }else{
            current_memo = current_memo+1;  //현재 메모의 리스트 인덱스의 값 (인덱스의 값이 커질수록 이전의 메모가 된다)
            arr_len = arr_len-1;            //현재 글의 순서 설정. (작아질수록 오래된 글, 커질수록 최근의 글)
            document.getElementById('cont').value = memolist[current_memo].cont;    //이전 글
            document.getElementById('id').value = memolist[current_memo].Id;
            document.getElementById('userId').value = memolist[current_memo].userId;
            document.getElementById('in_date').value = memolist[current_memo].in_date;
            
            innerHtml = "<ul style=\"float:right\">" +
                        "<li>" + (arr_len) + " / " + memolist.length + "</li>" +
                        "</ul>";
            $('#current_memo').html(innerHtml)
        }
    }

    //다음 글 가져오기
    function getNextMemo(){
        if(current_memo == 0){
            //alert("다음 메모가 없습니다.");
        }else{
            current_memo = current_memo-1;
            arr_len = arr_len+1;    //현재 글의 순서 설정
            document.getElementById('cont').value = memolist[current_memo].cont;    //다음 글
            document.getElementById('id').value = memolist[current_memo].Id;
            document.getElementById('userId').value = memolist[current_memo].userId;
            document.getElementById('in_date').value = memolist[current_memo].in_date;

            innerHtml = "<ul style=\"float:right\">" +
                        "<li>" + (arr_len) + " / " + memolist.length + "</li>" +
                        "</ul>";
            $('#current_memo').html(innerHtml)
        }
    }

    // max가젯으로 listcount 전달
    function toMax(type) {
        if(type == "list"){
            if(memoLoad == true) {
                document.memoMini.submit();
            }
        }else{
            if(memoLoad == true) {
                document.memoMini.submit();
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
            Ext.Msg.alert('<fmt:message key='aimir.message'/>', li_max + " <fmt:message key='aimir.memo.maxlength'/>");
            ls_str2 = ls_str.substr(0, li_len);
            aro_name.value = ls_str2;
        }
        aro_name.focus();
    }

    </script>
</head>
<body onload="init()">
<div id="gadget_body">

        <div id="out-bord1">
            <div id="btn">
                <ul>
                    <li id="saveit"><a href="javascript:updateMemo()" class="on"><fmt:message key="aimir.save2"/></a></li>
                </ul>
                <ul style="float:right">
                    <li><a href="javascript:getNextMemo()" class="on">▶</a></li>
                </ul>
                <ul style="float:right">
                    <li><a href="javascript:getBeforeMemo()" class="on">◀</a></li>
                </ul>
            </div>

            <div class="ui-widget-shadow ui-corner-all" id="out-bord2">
            </div>
            <div id='memoboard1' class="ui-widget ui-widget-content ui-corner-all">
            </div>
        </div>

</div>
</body>
</html>