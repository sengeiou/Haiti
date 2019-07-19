<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>부하관리</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

<%-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.debug.js"></script> --%>
<!--  %@ include file="/gadget/system/preLoading.jsp"%-->

<script language="JavaScript"><!--/*<![CDATA[*/

    var flex;
    var flex2;
    var  flexShedList;

    var currTabId = "DR";  // 현재 탭
    var supplierId = "";
    var operatorId = "";

    var selectedType = "";
    var selectedId = "";

    var groupName = "<fmt:message key='aimir.groupNmember.name'/>";
    var memberName = "<fmt:message key='aimir.membername'/>";

    var sendFlag = false;
    // 수정권한
    var editAuth = "${editAuth}";

    function getEditAuth() {
        return editAuth;
    }

    /********************
     * 유저 세션 정보 가져오기
     *******************/
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != "" && json.operatorId != ""){
                    supplierId = json.supplierId;
                    operatorId = parseInt(json.operatorId);
                }
            }
    );

    /*********************
     * Operator Id 가져오기
     *********************/
    function getOperatorId() {
        return operatorId;
    }

    /*********************
     * TAB UI
     *********************/
    $(function(){
        $(function() { $('#_loadDRListTab').bind('click',  function(event){ currTabId = "DR"; }); });
        $(function() { $('#_loadControlTab').bind('click', function(event){ currTabId = "LC"; }); });
        $(function() { $('#_loadLimitTab').bind('click',   function(event){ currTabId = "LM"; }); });
        $(function() { $('#_loadShedTab').bind('click',    function(event){ currTabId = "LS"; }); });
        $('#loadMgmtTab').tabs();
    });

    /***********************
     * FLEX 객체 가져오기
     ***********************/
    $(document).ready(function() {
        // 수정권한 체크
        if (editAuth == "true") {
            $("#groupBtnList").show();
            $("#memberBtnList").show();
        } else {
            $("#groupBtnList").hide();
            $("#memberBtnList").hide();
        }

        //hide();
        dateInterval = setInterval("dateDelay()",100);

        flex2       = getFlexObject('groupMemberFlex');
        flexShedList= getFlexObject('loadMgmtShedListFlex');
        flex        = getFlexObject('groupMgmtFlex');

        $('#groupType').selectbox();    // 그룹타입
        $('#groupNames').selectbox();   // 그룹명
        $('#memberType').selectbox();   // 멤버 타입

        // 그룹별DR탭
        $('#drtab_groupType').selectbox();

        $('#lctab_groupType').selectbox();
        $('#lmtab_groupType').selectbox();

        $('#lstab_groupType').selectbox();
        $('#lstab_scheduleType').selectbox();
        $('#ls2tab_groupType').selectbox();

        $('div.rightbox select').selectbox();

        $('#ls_div_groupName').hide();
    });

    /***********************************************************
     * Load Control/Load Limit/Load Shed 탭별 라디오 활성/비활성  설정
     ***********************************************************/
    $(function(){

        $(function() {// LOAD CONTROL 탭
            $('#lc_ondemand_radio').change( function(){ if(this.checked) { changeLCTabStatus(true,'');} });
            $('#lc_schedule_radio').change( function(){ if(this.checked) { changeLCTabStatus(false,'');} });
            $('#lc_ondemand_off').change(   function(){ if(this.checked) $('#lc_ondemand_on').attr('checked',false) });
            $('#lc_ondemand_on').change(    function(){ if(this.checked) $('#lc_ondemand_off').attr('checked',false) });
            $('#lc_eventradio_off').change( function(){ if(this.checked) $('#lc_eventradio_on').attr('checked',false) });
            $('#lc_eventradio_on').change(  function(){ if(this.checked) $('#lc_eventradio_off').attr('checked',false) });

             // Date list, lm_ondemand_on
            $('#lc_schedule_date_radio').change( function(){ if(this.checked){changeLCTabStatus(false, 'date');} });
            // Load Control에서 week list
            $('#lc_schedule_week_radio').change( function(){ if(this.checked){changeLCTabStatus(false, 'week');} });

            // 리스트박스에서 타겟 검색 결과를 선택
            $('#lctab_listbox').change( function(){
                var val = this.value;
                getLoadControlScheduleList(val);
            });
        }).trigger('change');

        $(function(){// LOAD LIMIT 탭
            $('#lm_ondemand_radio').change( function() { if(this.checked){changeLMTabStatus(true,'');} }); // ondemand
            $('#lm_schedule_radio').change( function() { if(this.checked){changeLMTabStatus(false,'');} }); // 예약 스케쥴

            $('#lm_schedule_date_radio').change( function() { if(this.checked){changeLMTabStatus(false,'date');} });// 예약 스케쥴
            $('#lm_schedule_week_radio').change( function() { if(this.checked){changeLMTabStatus(false,'week');} });// 예약 스케쥴

            // 리스트박스에서 타겟 검색 결과를 선택
            $('#lmtab_listbox').change( function(){
                var val = this.value;
                getLoadLimitScheduleList(val);
            });
        }).trigger('change');

        $('#ls_div_searchDate').show();
        $('#ls_div_searchDay').hide()
        $('#ls_div_groupName').hide();
        $(function() { // LOAD SHED 탭
            $('#ls_schedule_date_radio').change( function(){ if(this.checked){changeLSTabStatus('date');} });
            $('#ls_schedule_week_radio').change( function(){ if(this.checked){changeLSTabStatus('week');} });

            $('#ls_eventradio_on').change(  function() { if(this.checked) $('#ls_eventradio_off').attr('checked',false) });
            $('#ls_eventradio_off').change( function() { if(this.checked) $('#ls_eventradio_on').attr('checked',false) });

         // 리스트박스에서 타겟 검색 결과를 선택
            $('#lstab_listbox').change( function(){
                var val = this.value;
                getLoadShedScheduleList(val);
            });

            $('#lstab_scheduleType').change( function(){    // LoadShed 검색 창
                if(this.value == 'Date'){
                    $('#ls_div_searchDate').show();
                    $('#ls_div_searchDay').hide()
                    $('#ls_div_groupName').hide();
                }else if(this.value == 'DayOfWeek'){
                    $('#ls_div_searchDate').hide();
                    $('#ls_div_searchDay').show()
                    $('#ls_div_groupName').hide();
                }else if(this.value == 'None'){
                    $('#ls_div_searchDate').hide();
                    $('#ls_div_searchDay').hide();
                    $('#ls_div_groupName').show();
                }
            });
        }).trigger('change');
    });

    /***********************************************
    * LoadControl, LoadLimit 의 좌측 타겟 타입별 목록 검색
    ***********************************************/
    function searchTargetListBox(tab){
        var result;

        var gtype = $('#'+tab+'tab_groupType').val();
        var gname = $('#'+tab+'tab_groupName').val();

        // JSON 으로 Controller 에 전달, 결과 받기
        $.getJSON('${ctx}/gadget/device/getLoadShedGroupMembers.do',
            {groupType : gtype, groupName : gname},
            function(json){
                result = json.scheduleList;

                // 리턴된 결과를 리스트박스에 뿌리기
                if(result != null && result.length > 0){
                    var select  = $('#'+tab+'tab_listbox');
                    var options = select.attr('options');
                    $('option', select).remove();

                    //var objOption = document.createElement('option');
                    //objOption.text = result[i].targetName;
                    //objOption.value = result[i].targetId;

                    for(var i=0; i<result.length; i++){
                        options[options.length] = new Option(result[i].targetName, result[i].targetId, false, false);
                    }
                }
        });
    }

    /***********************************************
    * LoadShed탭 하단 그룹검색 결과 목록
    ***********************************************/
    function searchLoadShedGroupListBox(){
        var result;
        var gtype = $('#ls2tab_groupType').val();
        var gname = $('#ls2tab_groupName').val();

        // JSON 으로 Controller 에 전달, 결과 받기(스케쥴 없는 LoadShedGroup
        $.getJSON('${ctx}/gadget/device/getLoadShedGroupsWithoutSchedule.do',
            {groupType : gtype, groupName : gname},
            function(json){
                result = json.scheduleList;
                // remove the list on selectbox
                var select  = $('#lstab_listbox');
                var options = select.attr('options');
                $('option', select).remove();
                // 리턴된 결과를 리스트박스에 뿌리기
                if(result != null && result.length > 0){
                    for(var i=0; i<result.length; i++){
                        options[options.length] = new Option(result[i].groupName, result[i].groupId, true, true);
                    }
                }
        });
    }

    var flag;
    // =============== 액션스크립트에서 호출하는 함수 =======================

    /***********************************************
    * 그룹별DR고객 탭 - 그룹 타입 선택
    ***********************************************/
    function groupSearch() {

        if ( $('#drtab_groupName').val() == '') {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg15'/>");
            return;
        }
        var gtype;
        if($('#drtab_groupType').val() == ''){
            gtype = null;
        }else{

            gtype = $('#drtab_groupType option:selected').text();

        }
        flex.groupSearch($('#drtab_groupName').val(), gtype);
    }

    /***********************************************
    * 그룹별DR고객 탭 - 멤버 검색
    ***********************************************/
    function memberSearch() {
        if ( selectedId == '' ){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg17'/>");
            return;
        }
        if ( $('#memberType').val() == '' ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg18'/>");
            return;
        }
        if ( $('#memberType').val() != selectedType ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg19'/>");
            return;
        }
        if ( $('#memberName').val() == '' || $('#memberName').val() == memberName ) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg16'/>");
            return;
        }
        flex2.requestSend();
    }

    /***********************************************
    * LOAD SHED TAB 상단 리스트 조회
    ***********************************************/
    function searchLoadShed(){
        if($('#lstab_groupType').val() == ""){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.groupMgmt.msg15'/>");
            return;
        }
        // 날짜타입이 None 일때

        // None 이 아닐때

        // 플렉스의 검색 메소드 호출, 그룹 타입, 검색 시작일, 검색 종료일을 넘겨야 함
        flexShedList.searchLoadShedList();
    }

    /*********************************
     * 각 탭별 파라미터 전달
     *********************************/
    function getCondition(tab){
        var cnt = 0;
        var condArray = new Array();

        if(tab == 'customer'){
            condArray[cnt++] = supplierId;
            condArray[cnt++] = operatorId;
            condArray[cnt++] = $('#memberType option:selected').text(); // 그룹타입
            condArray[cnt++] = $('#memberName').val() == memberName ? "" : $('#memberName').val();
            condArray[cnt++] = selectedId;

        }else if(tab == 'ls_list'){
            condArray[cnt++] = supplierId;
            condArray[cnt++] = operatorId;
            condArray[cnt++] = $('#lstab_groupType option:selected').text();
            condArray[cnt++] = $('#lstab_scheduleType option:selected').val();
            condArray[cnt++] = $('#searchStartDateA').val();
            condArray[cnt++] = $('#searchEndDateA').val();
            condArray[cnt++] = $('#ls_list_dayOfWeek option:selected').val();
            condArray[cnt++] = $('#ls_groupName').val();


        }else if(tab == 'ls_detailA'){
            //alert('ls_detailA tab');

        }else if(tab == 'ls_detailB'){
            //alert('ls_detailB');
        }else{ // CUSTOMER List
            condArray[cnt++] = supplierId;
            condArray[cnt++] = operatorId;
            condArray[cnt++] = $('#memberType option:selected').val(); // searchMemberType
            condArray[cnt++] = $('#memberName').val() == memberName ? "" : $('#memberName').val();
            condArray[cnt++] = selectedId;  //groupId
            condArray[cnt++] = $('#memberType option:selected').text(); // 그룹타입
            //alert('sup['+supplierId+'], op['+operatorId+'], memType['+$('#memberType option:selected').val()+'], memName['+$('#memberName').val()+'], gId['+selectedId+'], gType['+$('#memberType option:selected').text()+']');
        }

        return condArray;
    }

   /***********************************************
    * 그룹별 DR 고객탭에서 그리드 선택 시. 넘어온 type, id 를 그룹멤버 플렉스에서 참조.
    ***********************************************/
   function gridItemClick(type, id){
        $('#memberType').option(type);
        $('#memberName').val(memberName);

        selectedType = type;    // group or member
        selectedId   = id;

        flex2.requestSend();    // 그룹멤버 플렉스 호출
    }

    // 취소
    function cancel() {
        flex.requestSend();
    }

    // 저장
    function saveRow() {
        flex.saveRow();
    }

    // 그룹 추가
    function addRow() {
        flex.addRow();
    }

    // 삭제
    function deleteRow() {
        flex.deleteRow();
    }

    // 복사
    function copyRow() {
        flex.copyRow();
    }

    // 이동
    function moveRow() {
        flex.moveRow();
    }

    // Refresh
    function refreshFlex(obj){
        if(obj=='DRGroupList'){ flex.requestSend(); }
        else if(obj=='LoadShedList') { flex2.requestSend(); }
    }

    /***********************************************
    * Load Control 탭 우측 버튼 활성/비활성
    ***********************************************/
    function changeLCTabStatus(isOndemand, type){

        if(isOndemand){
            $('#lc_ondemand :input').removeAttr('disabled');

            // event log
            $('#lc_schedule_eventlog input:radio').attr('disabled', 'disabled');
            // date
            $('#lc_schedule_date :input').attr('disabled', 'disabled');
            $('#lc_schedule_date select').attr('disabled', 'disabled');
            // week
            $('#lc_schedule_weekday :input').attr('disabled', 'disabled');
            $('#lc_schedule_weekday select').attr('disabled', 'disabled');

        }else{
            // on-demand input 비활성화
            $('#lc_ondemand :input').attr('disabled','disabled');
            $('#lc_ondemand_radio').removeAttr('disabled').attr('checked',false);

            $('#lc_schedule_eventlog input:radio').removeAttr('disabled');

            if(type == 'date'){
                $('#lc_schedule_date select').removeAttr('disabled');
                $('#lc_schedule_date :input').removeAttr('disabled');

                $('#lc_schedule_weekday select').attr('disabled', 'disabled');
                $('#lc_schedule_weekday :input').attr('disabled', 'disabled');
                $('#lc_schedule_week_radio').removeAttr('disabled').attr('checked',false);

            }else if(type == 'week'){
                 $('#lc_schedule_weekday select').removeAttr('disabled');
                 $('#lc_schedule_weekday :input').removeAttr('disabled');

                 $('#lc_schedule_date select').attr('disabled','disabled');
                 $('#lc_schedule_date :input').attr('disabled','disabled');
                 $('#lc_schedule_date_radio').removeAttr('disabled').attr('checked',false);

            }else{
                // 하단 라디오 버튼 모두 unchecked
                $('#lc_schedule_date select').attr('disabled','disabled');
                 $('#lc_schedule_date :input').attr('disabled','disabled');
                $('#lc_schedule_weekday select').attr('disabled', 'disabled');
                $('#lc_schedule_weekday :input').attr('disabled', 'disabled');
                $('#lc_schedule_date_radio').removeAttr('disabled').attr('checked',false);
                $('#lc_schedule_week_radio').removeAttr('disabled').attr('checked',false);
            }
        }
    }

    /***********************************************
     * Load Limit 탭 오른쪽 버튼 활성/비활성
     ***********************************************/
    function changeLMTabStatus(isOndemand, type){
        if(isOndemand){
            // Ondemand 의 Input 태그 활성화
            $('#lm_ondemand input').removeAttr('disabled');
            // 예약 스케쥴 하위 input 태그  비활성화
            $('#lm_schedule_date :input').attr('disabled','disabled');
            $('#lm_schedule_date select').attr('disabled','disabled');

            $('#lm_schedule_weekday input').attr('disabled','disabled');
            $('#lm_schedule_weekday select').attr('disabled','disabled');

        }else{
            // Ondemand의 input 비활성화 (radio 제외)
            $('#lm_ondemand input').attr('disabled','disabled');
            $('#lm_ondemand_onOff').removeAttr('disabled');

            // 예약 스케쥴 하위 radio 활성화
            if(type=='date'){

                $('#lm_schedule_date input').removeAttr('disabled');
                $('#lm_schedule_date select').removeAttr('disabled');
                $('#lm_schedule_date_radio').attr('checked',true);

                $('#lm_schedule_weekday input').attr('disabled','disabled');
                $('#lm_schedule_weekday select').attr('disabled','disabled');
                $('#lm_schedule_week_radio').removeAttr('disabled').attr('checked',false);

            }else if(type=='week'){
                $('#lm_schedule_weekday input').removeAttr('disabled');
                $('#lm_schedule_weekday select').removeAttr('disabled');
                $('#lm_schedule_week_radio').attr('checked',true);

                $('#lm_schedule_date input').attr('disabled','disabled');
                $('#lm_schedule_date select').attr('disabled','disabled');
                $('#lm_schedule_date_radio').removeAttr('disabled').attr('checked',false);
            }else{
                $('#lm_schedule_date_radio').removeAttr('disabled').attr('checked',false);
                $('#lm_schedule_week_radio').removeAttr('disabled').attr('checked',false);
            }
        }
    }

    //
    /***********************************************
    * Load Shed 탭  버튼 상태 변경
    ***********************************************/
    function changeLSTabStatus(type){

        if(type=='date'){
            $('#ls_schedule_date input').removeAttr('disabled');
            $('#ls_schedule_date select').removeAttr('disabled');
            $('#lm_schedule_date_radio').attr('checked',true);

            $('#ls_schedule_weekday input').attr('disabled','disabled');
            $('#ls_schedule_weekday select').attr('disabled','disabled');
            $('#ls_schedule_week_radio').removeAttr('disabled').attr('checked',false);
        }else if(type=='week'){
            $('#ls_schedule_weekday input').removeAttr('disabled');
            $('#ls_schedule_weekday select').removeAttr('disabled');
            $('#ls_schedule_week_radio').attr('checked',true);

            $('#ls_schedule_date input').attr('disabled','disabled');
            $('#ls_schedule_date select').attr('disabled','disabled');
            $('#ls_schedule_date_radio').removeAttr('disabled').attr('checked',false);
        }
    }

    /****************************
    * LoadControl 그룹의 스케쥴 목록
    ****************************/
    function getLoadControlScheduleList(targetId){
        // Date Schedule
        $.getJSON('${ctx}/gadget/device/getLoadControlScheduleListByDate.do',
                {targetId:targetId},
                function(json){
                    var schedules = json.scheduleList;
                    deleteTableRows('lc_date_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadControlTableRow(schedules[i].scheduleType, schedules[i]);
                        }
                    }
                });
        // DayOfWeek Schedule
        $.getJSON('${ctx}/gadget/device/getLoadControlScheduleListByWeekday.do',
                {targetId:targetId},
                function(json){
                    var schedules = json.scheduleList;
                    deleteTableRows('lc_week_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadControlTableRow(schedules[i].scheduleType, schedules[i]);
                        }
                    }
                });
    }

    /**************************
    * LoadLimit 그룹의 스케쥴 목록
    ***************************/
    function getLoadLimitScheduleList(targetId){
        $.getJSON('${ctx}/gadget/device/getLoadLimitScheduleListByDate.do',
                {targetId : targetId},
                function(json){

                    var schedules = json.scheduleList;
                    deleteTableRows('lm_date_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadLimitTableRow(schedules[i].scheduleType, schedules[i], i);
                        }
                    }
                });
        $.getJSON('${ctx}/gadget/device/getLoadLimitScheduleListByWeekday.do',
                {targetId : targetId},
                function(json){

                    var schedules = json.scheduleList;
                    deleteTableRows('lm_week_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadLimitTableRow(schedules[i].scheduleType, schedules[i], i);
                        }
                    }
                });
    }

    // ############ LoadShedList 플렉스에서 그룹 클릭시 스케쥴 목록 표시. Flex 에서 호출함
    var lsTargetId = "";
    function getLoadShedScheduleList(groupId){
        lsTargetId = groupId;
        // Date
        $.getJSON('${ctx}/gadget/device/getLoadShedScheduleListByDate.do',
                {groupId : groupId},
                function(json){

                    var schedules = json.scheduleList;
                    deleteTableRows('ls_date_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadShedTableRow(schedules[i].scheduleType, schedules[i], i);
                        }
                    }
                });
        // Week
        $.getJSON('${ctx}/gadget/device/getLoadShedScheduleListByWeekday.do',
                {groupId : groupId},
                function(json){

                    var schedules = json.scheduleList;
                    deleteTableRows('ls_week_tablegrid');
                    if(schedules != null && schedules.length > 0){
                        for(var i=0; i<schedules.length; i++){
                            addLoadShedTableRow(schedules[i].scheduleType, schedules[i], i);
                        }
                    }
                });
    }

    /************************************
    * 해당 테이블의 첫번째 ROW를 제외한 ROWS 삭제
    *************************************/
    function deleteTableRows(table){
        var rows = $('#'+table+' tr:gt(0)');
        rows.remove();
    }

    function getNumOfRows(table){
        var rows = $('#'+table+' tr:gt(0)');
        return rows.length + 1;
    }
    /**************************
    * Load Control Table 행 추가
    ***************************/
    function addLoadControlTableRow(type, sc){
        var content = "";

        if(type=='Date'){
            index = getNumOfRows('lc_date_tablegrid');
            if(index == 5){
                return;
            }
            content += "<tr>";
            content += "<td><input type='checkbox' class='transonly' name='lc_chkbox_date' value='"+sc.id+"'/></td>";
            content += "<td>"+sc.startTime.substring(0,8)+"</td>";
            content += "<td>"+sc.startTime.substring(8)+"</td>";
            content += "<td>"+sc.endTime.substring(8)+"</td>";
            content += "<td>"+sc.onOff+"</td>";
            content += "</tr>";
            $('#lc_date_tablegrid tbody').append(content);

        }else if(type=='DayOfWeek'){
            content +="<tr>";
            content +="<td><input type='checkbox' class='transonly' name='lc_chkbox_week' value='"+sc.id+"'/></td>";
            content +="<td>"+sc.weekDay+"</td>";
            content +="<td>"+sc.startTime.substring(8)+"</td>";
            content +="<td>"+sc.endTime.substring(8)+"</td>";
            content +="<td>"+sc.onOff+"</td>";
            content +="</tr>";
            $('#lc_week_tablegrid tbody').append(content);
        }
    }

    /********************************************
    * Load Limit Table 행 추가
    *********************************************/
    function addLoadLimitTableRow(type, sc, index){
        var content = "";
        if(type=='Date'){
            content += "<tr>";
            content += "<td><input type='checkbox' class='transonly' name='lm_chkbox_date' value='"+sc.id+"'/></td>";
            content += "<td>"+sc.startTime.substring(0,8)+"</td>";
            content += "<td>"+sc.startTime.substring(8)+"</td>";
            content += "<td>"+sc.endTime.substring(8)+"</td>";
            content += "<td>"+sc.limitType+"</td>";
            content += "<td>"+sc.limit+"</td>";
            content += "<td>"+sc.openPeriod+"</td>";
            content += "<td></td>";
            content += "</tr>";
            $('#lm_date_tablegrid tbody').append(content);
        }else if(type=='DayOfWeek'){
            content +="<tr>";
            content +="<td><input type='checkbox' class='transonly' name='lm_chkbox_week' value='"+sc.id+"'/></td>";
            content +="<td>"+sc.weekDay+"</td>";
            content +="<td>"+sc.startTime.substring(8)+"</td>";
            content +="<td>"+sc.endTime.substring(8)+"</td>";
            content +="<td>"+sc.limitType+"</td>";
            content +="<td>"+sc.limit+"</td>";
            content +="<td>"+sc.openPeriod+"</td>";
            content +="</tr>";
            $('#lm_week_tablegrid tbody').append(content);
        }
    }

    /********************************************
    * Load Shed Table 행 추가
    *********************************************/
    function addLoadShedTableRow(type, sc, index){
        var content = "";
        if(type=='Date'){
            content += "<tr>";
            content += "<td><input type='checkbox' class='transonly' name='ls_chkbox_date' value='"+sc.id+"'/></td>";
            content += "<td>"+sc.startTime.substring(0,8)+"</td>";
            content += "<td>"+sc.startTime.substring(8)+"</td>";
            content += "<td>"+sc.endTime.substring(8)+"</td>";
            content += "<td>"+sc.onOff+"</td>";
            content += "</tr>";
            $('#ls_date_tablegrid tbody').append(content);
        }else if(type=='DayOfWeek'){
            content += "<tr>";
            content += "<td><input type='checkbox' class='transonly' name='ls_chkbox_week' value='"+sc.id+"'/></td>";
            content += "<td>"+sc.weekDay+"</td>";
            content += "<td>"+sc.startTime.substring(8)+"</td>";
            content += "<td>"+sc.endTime.substring(8)+"</td>";
            content += "</tr>";
            $('#ls_week_tablegrid tbody').append(content);
        }
    }

    /********************************************
    * 스케쥴 추가 테스트 코드!!!!
    *********************************************/
    function addLoadControlSchedule(type){
        // 값 읽어와서 controller에 넘기기
        var obj = new Object();
        obj.targetType    = $('#lctab_groupType option:selected').val();
        obj.targetId      = $('#lctab_listbox option:selected').val();

        if(type=='ondemand'){
            if( $('#lc_ondemand_radio').attr('checked') == false ) return;
            obj.scheduleType = "Immediately";
            obj.onOff        = $('input[name=lc_ondemand_onOff]').filter( function() {if(this.checked) return this;}).val();
            obj.runTime      = $('#lc_ondemand_runTime').val();  // ondemand only
            obj.delay        = $('#lc_ondemand_delay').val();
        }else if(type=='date'){
            if( $('#lc_schedule_date_radio').attr('checked') == false ) return;
            obj.scheduleType = "Date";
            obj.onOff        = "Off";
            obj.startTime    = $('#searchStartDateB').val() + $('#lc_date_fromHour').val() + $('#lc_date_fromMinute').val() + "00";
            //obj.endTime      = $('#searchEndDateB').val() + $('#lc_date_toHour').val() + $('#lc_date_toMinute').val() + "00";
            obj.endTime      = $('#searchStartDateB').val() + $('#lc_date_toHour').val() + $('#lc_date_toMinute').val() + "00";
        }else if(type=='week'){
            if( $('#lc_schedule_week_radio').attr('checked') == false ) return;
            obj.scheduleType = "DayOfWeek";
            obj.weekDay      = $('#lc_dayOfWeek option:selected').val();   // week only. 0, 1, 2, 3
            obj.onOff        = "Off";
            // 년월일이 빠져있는 상태. controller 에서 보완필요
            obj.startTime    = $('#lc_week_fromHour').val() + $('#lc_week_fromMinute').val() + "00";
            obj.endTime      = $('#lc_week_toHour').val()   + $('#lc_week_toMinute').val()   + "00";
        }

        //alert('targetId['+obj.targetId+'], groupType['+obj.targetType+'], scheduleType['+obj.scheduleType+'], onOff['+obj.onOff+'], startTime['+obj.startTime+'], endTime['+obj.endTime+'], weekDay['+obj.weekDay+']');
        $.getJSON('${ctx}/gadget/device/addLoadControlSchedule.do',
                {targetType:obj.targetType, targetId:obj.targetId, scheduleType:obj.scheduleType,
                 onOff:obj.onOff, startTime:obj.startTime, endTime:obj.endTime, weekDay:obj.weekDay,
                 runTime:obj.runTime, delay:obj.delay},
                function(json){
                    getLoadControlScheduleList(obj.targetId);   // 목록 다시 표시
                });
    }

    /********************************************
    * Load Limit 스케쥴 추가
    *********************************************/
    function addLoadLimitSchedule(type){
        // 값 읽어와서 controller에 넘기기
        var obj = new Object();
        //obj.targetId      = lsTargetId;
        obj.targetType  = $('#lmtab_groupType option:selected').val();
        obj.targetId    = $('#lmtab_listbox option:selected').val();

        if(type=='ondemand'){
            if( $('#lm_ondemand_radio').attr('checked') == false) return;
            obj.scheduleType = "Immediately";

        }else if(type=='date'){
            if( $('#lm_schedule_date_radio').attr('checked') == false) return;

            obj.scheduleType = "Date";
            obj.limitType    = $('#lm_date_limitType option:selected').val();
            obj.limit        = $('#lm_date_limit').val();
            obj.peakType     = $('#lm_date_peakType option:selected').val();
            obj.startTime    = $('#searchStartDateB').val() + $('#lm_date_fromHour').val() + $('#lm_date_fromMinute').val() + "00";
            obj.endTime      = $('#searchStartDateB').val() + $('#lm_date_toHour').val()   + $('#lm_date_toMinute').val()   + "00";
            //obj.endTime      = $('#searchEndDateB').val() + $('#lm_date_toHour').val() + $('#lm_date_toMinute').val() + "00";

        }else if(type=='week'){
            if( $('#lm_schedule_week_radio').attr('checked') == false) return;

            obj.scheduleType = "DayOfWeek";
            obj.limitType    = $('#lm_week_limitType').val();
            obj.limit        = $('#lm_week_limit').val();
            obj.peakType     = $('#lm_week_peakType option:selected').val();
            obj.weekDay      = $('#lm_dayOfWeek option:selected').val();   // week only. 0, 1, 2, 3
            obj.startTime    = $('#lm_week_fromHour').val() + $('#lm_week_fromMinute').val() + "00";
            obj.endTime      = $('#lm_week_toHour').val() + $('#lm_week_toMinute').val() + "00";
        }

        //alert('targetId['+obj.targetId+'], groupType['+obj.targetType+'], scheduleType['+obj.scheduleType+'], limitType['+obj.limitType+'], limit['+obj.limit+'], openPeriod['+obj.peakType+'], startTime['+obj.startTime+'], endTime['+obj.endTime+'], weekDay['+obj.weekDay+']');
        $.getJSON('${ctx}/gadget/device/addLoadLimitSchedule.do',
                {targetType:obj.targetType, targetId:obj.targetId, scheduleType:obj.scheduleType,
                 limitType:obj.limitType, limit:obj.limit, peakType:obj.peakType,
                 startTime:obj.startTime, endTime:obj.endTime, weekDay:obj.weekDay},
                function(json){
                    // 목록 다시 표시
                    getLoadLimitScheduleList(obj.targetId);
                });

    }

    //
    /********************************************
    * Load Shed 스케쥴 추가
    *********************************************/
    function addLoadShedSchedule(type){
        var obj = new Object();
        obj.targetId    = lsTargetId;

        if(type=='ondemand'){
            obj.scheduleType   = "Immediately";
            obj.onOff          = $('#ls_ondemand_onOff option:selected').val();
            obj.supplyCapacity = $('#ls_ondemand_supplyCapacity').val();
            obj.supplyThreshold= $('#ls_ondemand_supplyThreshold').val();
        }else if(type=='date'){
            //alert('date');
            obj.scheduleType   = "Date";
            obj.onOff          = $('#ls_date_onOff option:selected').val();;
            obj.startTime      = $('#searchStartDateB').val() + $('#ls_date_fromHour').val() + $('#ls_date_fromMinute').val() + "00";
            obj.endTime        = $('#searchStartDateB').val() + $('#ls_date_toHour').val()   + $('#ls_date_toMinute').val()   + "00";
        }else if(type=='week'){
            //alert('week');
            obj.scheduleType   = "DayOfWeek";
            obj.onOff          = $('#ls_week_onOff option:selected').val();
            obj.startTime      = $('#ls_week_fromHour').val() + $('#ls_week_fromMinute').val() + "00";
            obj.endTime        = $('#ls_week_toHour').val() + $('#ls_week_toMinute').val() + "00";
            obj.weekDay         = $('#ls_dayOfWeek option:selected').val();
        }

        $.getJSON('${ctx}/gadget/device/addLoadShedSchedule.do',
                {targetId:obj.targetId, scheduleType:obj.scheduleType,
                 onOff:obj.onOff, startTime:obj.startTime, endTime:obj.endTime, weekDay:obj.weekDay},
                function(json){
                        getLoadShedScheduleList(obj.targetId);
                });
    }

     /********************************************
     * 스케쥴 삭제
     *********************************************/
    function delSchedule(tab, chkbox){  // LoadControlTab, LoadLimitTab, LoadShedTab

        //var chk = $("#"+tab+" input[name="+chkbox+"]:checkbox:checked");
        //alert("lc_chkbox_date length["+chk.length+"]");
        var chkList = "";
        $("#"+tab+" input[name="+chkbox+"]:checkbox:checked").each( function(){
               chkList += (this.value + ",");   // add checked targetId
           });

        if(tab=='loadControlTab'){
            $.getJSON('${ctx}/gadget/device/deleteLoadControlSchedule.do',
                    {scheduleList:chkList},
                    function(json){
                        getLoadControlScheduleList($('#lctab_listbox option:selected').val()); // refresh
                    });
        }else if(tab=='loadLimitTab'){
            $.getJSON('${ctx}/gadget/device/deleteLoadLimitSchedule.do',
                    {scheduleList:chkList},
                    function(json){
                           getLoadLimitScheduleList($('#lmtab_listbox option:selected').val());
                    });
        }else if(tab=='loadShedTab'){
            $.getJSON('${ctx}/gadget/device/deleteLoadShedSchedule.do',
                    {scheduleList:chkList},
                    function(json){
                           getLoadShedScheduleList(lsTargetId);
                    });
        }

    }

    // 멤버 목록
    function saveMember(val) {
        flex2.saveMember(val);
    }

    function cancel2() {
        flex2.requestSend();
    }

    // ######################### 날짜 관련 함수 ################
    function conditionInit(){

        // 달력붙이기
        locDateFormat = "yymmdd";
        // LOAD CONTROL
        $("#lc_detail_startDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'lc_detail_startDate');}
        });
        /*$("#lc_detail_endDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'lc_detail_endDate');}
        });*/
        // LOAD LIMIT
        $("#lm_detail_startDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'lm_detail_startDate');}
        });
        /*$("#lm_detail_endDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'lm_detail_endDate');}
        });*/

        // LOAD SHED
        $("#ls_list_startDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'ls_list_startDate');}
        });
        $("#ls_list_endDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'ls_list_endDate');}
        });

        $("#ls_detail_startDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'ls_detail_startDate');}
        });
        /*$("#ls_detail_endDate").datepicker({
            maxDate:'+0m', showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true,
            dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, 'ls_detail_endDate');}
        });*/

        // 오늘 날짜 세팅
        var today = new Date();
        var yyyy = today.getFullYear();
        var mm = today.getMonth()+1;
        var dd = today.getDate();
        var date = yyyy + "" + padZero(mm, 2) + "" + padZero(dd, 2);
        modifyDate(date, 'lc_detail_startDate');
        //modifyDate(date, 'lc_detail_endDate');
        modifyDate(date, 'lm_detail_startDate');
        //modifyDate(date, 'lm_detail_endDate');
        modifyDate(date, 'ls_list_startDate');
        modifyDate(date, 'ls_list_endDate');
        modifyDate(date, 'ls_detail_startDate');
        //modifyDate(date, 'ls_detail_endDate');
        //$('#searchStartDate').val(date);

        //조회버튼클릭 이벤트 생성
        //$(function() { $('#btnSearch').bind('click',function(event) { send(); } ); });

//        if( flex != null ){
//            // 콤보 선택 시 request send
//            $('#ls_list_startDate').change(function() { if(sendFlag) send(); });
//            $('#ls_list_endDate').change(function() { if(sendFlag) send(); });
//            $('#ls_list_startDate').change(function() { if(sendFlag) send(); });
//            $('#ls_list_endDate').change(function() { if(sendFlag) send(); });
//        }

    }

    function dateDelay(){
        if(supplierId != '' && flex != null){
            clearInterval(dateInterval);
            conditionInit();
        }
    }

    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate, inst){

        var dateId = '#' + inst;
        if(inst == 'ls_list_startDate' || inst == 'ls_list_endDate'){
            //alert('ls_list');
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        convertSearchDate('ls_list');
                    });
        }else if(inst == 'ls_detail_startDate' /*|| inst == 'ls_detail_endDate' */){
            //alert('ls_detail');
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        convertSearchDate('ls_detail');
                    });
        }else if(inst == 'lc_detail_startDate' /*|| inst == 'lc_detail_endDate'*/){
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        convertSearchDate('lc_detail');
                    });
        }else if(inst == 'lm_detail_startDate' /*|| inst == 'lm_detail_endDate'*/){
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        convertSearchDate('lm_detail');
                    });
        }
    }

    function dbDateToLocale(setDate) {
        var result;
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    result = json.localDate;
                });
        return result;
    }


    /**************************************
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     ****************************************/
    function convertSearchDate(sel_picker){
        // endDate 가 필요 없을 겨우 searchStartDate, searchEndDate 를 동일하게
        // LOAD SHED 상단
        if(sel_picker == 'ls_list'){
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#ls_list_startDate').val(), searchEndDate:$('#ls_list_endDate').val(), supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDateA').val(json.searchStartDate);
                        $('#searchEndDateA').val(json.searchEndDate);
                        $('#searchStartDateA').trigger('change');
                    });
        // LOAD SHED 하단
        }else if(sel_picker == 'ls_detail'){
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#ls_detail_startDate').val(), searchEndDate:$('#ls_detail_startDate').val(), supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDateB').val(json.searchStartDate);
                        $('#searchEndDateB').val(json.searchEndDate);
                        $('#searchStartDateB').trigger('change');
                    });
        }else if(sel_picker == 'lc_detail'){
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#lc_detail_startDate').val(), searchEndDate:$('#lc_detail_startDate').val(), supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDateB').val(json.searchStartDate);
                        $('#searchEndDateB').val(json.searchEndDate);
                        $('#searchStartDateB').trigger('change');
                    });
        }else if(sel_picker == 'lm_detail'){
            $.getJSON("${ctx}/common/convertSearchDate.do"
                    ,{searchStartDate:$('#lm_detail_startDate').val(), searchEndDate:$('#lm_detail_startDate').val(), supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDateB').val(json.searchStartDate);
                        $('#searchEndDateB').val(json.searchEndDate);
                        $('#searchStartDateB').trigger('change');
                    });
        }
    }

    /**
     * 날짜 포맷팅
     */
    function padZero(num,leng) {
        var zero = leng-(""+num).length;
        if (typeof(num) == "number" && zero > 0) {
            var tmp = "";
            for (var i=0; i<zero; i++) tmp += "0";
            return tmp + num;
        }
        else {
            return num;
        }
    }

    function sendEnd(){
        if(!sendFlag) sendFlag = true;
    }

    function delTxt(tab){
        $('#'+tab).val('');
    }

    // 그룹명/멤버명 검색 시 엔터
    function keyEvent(event, type) {
        var evKeyup = null;
        if(event)
            // firefox
            evKeyup = event;
        else
            // explorer
            evKeyup = window.event;

        if ( evKeyup.keyCode == 13 ) {
            var searchData;
            if( type == "drgroup" ){    // 첫번째 탭 DRGROUP
                groupSearch();
            }else if(type == "drmember"){
                memberSearch();
            }else if(type == "lctab_groupName"){
                searchTargetListBox('lc');
            }else if(type == "lmtab_groupName"){
                searchTargetListBox('lm');
            }else if(type == "ls2tab_groupName"){
                searchLoadShedGroupListBox();
            }
        }
    }

    function getFmtMessage(){
        var cnt = 0;
        var fmtMessage = new Array();

        fmtMessage[cnt++] = "<fmt:message key="aimir.updatedata.notexist"/>";     // 저장할 데이터가 없습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg4"/>";    // 그룹명을 입력해 주세요.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg5"/>";    // 복사할 Row 를 선택해 주세요.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg6"/>";    // 삭제가능한 ROW가 없습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg7"/>";    // 등록된 멤버가 있는 그룹은 삭제할 수 없습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.msg.wantdelete"/>";          // 삭제 하시겠습니까
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg8"/>";    // 이동할 멤버 데이터와 그룹을 선택 해 주세요.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg9"/>";    // 이동할 멤버 데이터를 선택 해 주세요.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg10"/>";   // 이동할 그룹 선택 해 주세요.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg11"/>";   // 동일한 그룹으로 이동할 수 없습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg12"/>";   // 동일한 그룹타입으로만 이동 가능합니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg13"/>";   // 이동할 그룹은 하나만 선택하실 수 있습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg14"/>";   // 개 멤버를 이동 하시겠습니까?
        fmtMessage[cnt++] = "<fmt:message key="aimir.msg.deletesuccess"/>";       // 삭제되었습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg2"/>";    // 이동 되었습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.save"/>";                    // 저장되었습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg3"/>";    // 중복 된 멤버가 있습니다.
        fmtMessage[cnt++] = "<fmt:message key="aimir.alert.groupMgmt.msg1"/>";    // 수정 되었습니다.

        fmtMessage[cnt++] = "<fmt:message key="aimir.name"/>";                    // 이름
        fmtMessage[cnt++] = "<fmt:message key="aimir.grouptype"/>";               // 그룹타입
        fmtMessage[cnt++] = "<fmt:message key="aimir.allUserAccess"/>";           // 전체사용자 허용

        fmtMessage[cnt++] = "<fmt:message key="aimir.customer.dr"/>";     // 그룹별 DR 고객
        fmtMessage[cnt++] = "<fmt:message key="aimir.loadmgmt.supplycapacity"/>";    // 공급 용량
        fmtMessage[cnt++] = "<fmt:message key="aimir.threshold"/>";      // 임계치
        fmtMessage[cnt++] = "<fmt:message key="aimir.description"/>";    // 설명
        fmtMessage[cnt++] = "<fmt:message key="aimir.loadmgmt.currentdemand"/>";    // 현재수요

        fmtMessage[cnt++] = "<fmt:message key="aimir.group.name"/>";            //그룹명
        fmtMessage[cnt++] = "<fmt:message key="aimir.loadmgmt.createdate"/>";   //생성일

        fmtMessage[29] = "<fmt:message key="aimir.alert"/>";

        return fmtMessage;
    }

    /*]]>*/
//-->
</script>
</head>
<body>


<div id="loadMgmtTab"><!-- 상단 탭 시작-->
<ul>
    <li><a href="#loadDRListTab" id="_loadDRListTab" name="LoadMgmtTab1"><fmt:message key='aimir.customer.dr' /></a></li>
    <!-- DR고객 그룹관리 -->
    <li><a href="#loadControlTab" id="_loadControlTab" name="LoadMgmtTab2"><fmt:message key='aimir.loadmgmt.loadcontrol' /></a></li>
    <!-- Load Control -->
    <li><a href="#loadLimitTab" id="_loadLimitTab" name="LoadMgmtTab3"><fmt:message key='aimir.loadmgmt.loadlimit' /></a></li>
    <!-- Load Limit -->
    <li><a href="#loadShedTab" id="_loadShedTab" name="LoadMgmtTab4"><fmt:message key='aimir.loadmgmt.loadshed' /></a></li>
    <!-- Load Shed -->
</ul>
<!-- 상단 탭 끝 --> <!-- ########## 첫번째 탭 시작  ########## -->
<div id="loadDRListTab"><!-- 검색옵션 시작 -->
<!-- <div class="search-bg-basic2"> -->
<ul class="basic-ul">
    <!-- 그룹 타입 리스트박스-->
    <li class="basic-li" style="margin-left: 39px;"><select id="drtab_groupType" name="select"
        style="width: 120px;">
        <option value=""><fmt:message key="aimir.grouptype" /></option>
        <c:forEach var="groupType" items="${groupType}">
            <option value="${groupType.id}">${groupType.name}</option>
        </c:forEach>
    </select></li>

    <!-- 그룹명 리스트 박스 -->
    <!-- <li class="basic-li">
               <select id="groupNames" name="select" style="width:120px;">
                    <option value=""><fmt:message key="그룹명"/></option>
                    <c:forEach var="groupNames" items="${groupNames}">
                       <option value="${groupNames.id}">${groupNames.name}</option>
                    </c:forEach>
                </select>
            </li> -->

    <!-- 검색창 -->
    <li class="basic-li">
    <form name="searchform" method="post" onSubmit="return false;">
    <input type=hidden id="id" />
    <div class="search-s1">
    <ul>
        <li class="search-s1-input">
        <input id="drtab_groupName" type="text" value="<fmt:message key='aimir.groupNmember.name'/>"
            onclick="javascript:delTxt('drtab_groupName');"
            onkeydown="javascript:keyEvent(event, 'drgroup');"></li>
        <li class="search-s1-btn"><a href="javascript:groupSearch();"></a></li>
    </ul>
    </div>
    </form>
    </li>
</ul>
<!-- </div> -->
<!-- 검색옵션 끝 --> <!-- 고객 그룹목록 시작 -->

<div class="gadget_body"><object id="groupMgmtFlexEx"
    classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
    height="370">
    <param name="movie" value="${ctx}/flexapp/swf/loadManagementMax.swf" />
    <param name="wmode" value="opaque">
    <!--[if !IE]>--> <object id="groupMgmtFlexOt"
        type="application/x-shockwave-flash"
        data="${ctx}/flexapp/swf/loadManagementMax.swf" width="100%"
        height="370">
        <param name="wmode" value="opaque">
        <!--<![endif]-->
        <div>
        <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
        <p><a href="http://www.adobe.com/go/getflashplayer"><img
            src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"
            alt="Get Adobe Flash player" width="112" height="33" /></a></p>
        </div>
        <!--[if !IE]>--> </object> <!--<![endif]--> </object></div>
<!-- 고객 그룹목록 끝 -->

<div id="groupBtnList">
    <div id="btn-right">
    <ul>
        <li><a href="javascript:deleteRow();" class="on"><fmt:message
            key="aimir.button.delete" /></a></li>
    </ul>
    <ul>
        <li><a href="javascript:cancel();" class="on"><fmt:message
            key="aimir.cancel" /></a></li>
    </ul>
    <ul>
        <li><a href="javascript:saveRow();" class="on"><fmt:message
            key="aimir.save2" /></a></li>
    </ul>
    <ul>
        <li><a href="javascript:addRow();" class="on"><fmt:message
            key="aimir.button.addGroup" /></a></li>
    </ul>
    </div>
</div>

<!-- 그룹/멤버추가 전체 시작 -->
<div class="gadget_body"><!-- Step 02b - 멤버추가 (S) -->
<div class="groupmanage-create-tab blueline bg-blue clear"
    style="border-bottom: none !important;"><label
    class="member bluebold11pt"><fmt:message key="aimir.memberAdd" /></label>
</div>

<div class="groupmanage-create-member blueline bg-blue clear">
<ul class="width">
    <li class="padding minustop">

    <div class="blueline-searchoption">
    <ul class="row">
        <li class="col"><select id="memberType" style="width: 120px">
            <option value=""><fmt:message key="aimir.memberType" /></option>
            <c:forEach var="groupType" items="${groupType}">
                <option value="${groupType.name}">${groupType.name}</option>
            </c:forEach>
        </select></li>
        <li class="col">
        <div class="search-s1">
        <ul>
            <li class="search-s1-input"><input id="memberName" type="text"
                value="<fmt:message key='aimir.membername'/>"
                onclick="javascript:delTxt('memberName');"
                onkeydown="javascript:keyEvent(event, 'drmember');"></li>
            <li class="search-s1-btn"><a href="javascript:memberSearch();"></a></li>
        </ul>
        </div>
        </li>
    </ul>
    </div>
    <div class="dashedline-dark clear"></div>

    <!-- 플렉스그리드 (S) -->
    <div class="flexlist"><object id="groupMemberFlexEx"
        classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
        height="105px">
        <param name="movie" value="${ctx}/flexapp/swf/groupMemberMiniList.swf" />
        <param name="wmode" value="transparent">
        <!--[if !IE]>--> <object id="groupMemberFlexOt"
            type="application/x-shockwave-flash"
            data="${ctx}/flexapp/swf/groupMemberMiniList.swf" width="100%"
            height="105px">
            <param name="wmode" value="transparent">
            <!--<![endif]-->
            <div>
            <h4>Content on this page requires a newer version of Adobe Flash
            Player.</h4>
            <p><a href="http://www.adobe.com/go/getflashplayer"><img
                src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"
                alt="Get Adobe Flash player" width="112" height="33" /></a></p>
            </div>
            <!--[if !IE]>--> </object> <!--<![endif]--> </object></div>

    <div id="memberBtnList" class="margin-t5px floatright">
    <button type="button" class="sm_current"
        onclick="javascript:saveMember('false');"><fmt:message
        key="aimir.save2" /></button>
    <button type="button" class="sm_current"
        onclick="javascript:saveMember('true');"><fmt:message
        key="aimir.button.saveNadd" /></button>
    <button type="button" class="sm thelast"
        onclick="javascript:cancel2();"><fmt:message
        key="aimir.cancel" /></button>
    </div>

    </li>
</ul>
</div>
<!-- Step 02b - 멤버추가 (E) --></div> 
<!-- 그룹/멤버추가 전체 끝 --></div>
<!-- 첫번째 탭 끝-->

<!-- ##################### 두번째 탭 시작 (Load Control) ####################### -->
<div id="loadControlTab" class="margin-t20px"><!--s: 두번째 탭 전체 영역-->

<!--s: Navi 왼쪽 영역-->
<div class="leftbox box-bluegradation3 naviheight2">

<div class="padding-left3px">
<ul>
    <li class="height28px">
        <select id="lctab_groupType" name="select" style="width: 228px\9; width: 235px">
        <option value=""><fmt:message key="aimir.grouptype" /></option>
        <c:forEach var="groupType" items="${groupType}">
            <option value="${groupType.name}">${groupType.name}</option>
        </c:forEach>
        </select></li>
    <li class="graybold11pt floatleft"><fmt:message key="aimir.target" /> :</li>
    <li>
    <div class="search-box">
    <ul>
        <li class="search-s1-input">
        <input id="lctab_groupName" type="text" value="<fmt:message key='aimir.groupNmember.name'/>"
            onclick="javascript:delTxt('lctab_groupName');"
            onkeydown="javascript:keyEvent(event, 'lctab_groupName');"></li>
        <li class="search-s1-btn">
          <a href="javascript:searchTargetListBox('lc');"></a></li>
    </ul>
    </div>
    </li>
</ul>
</div>

<select id="lctab_listbox" name="lctab_listbox" size="10"
    style="width: 240px; height: 425px;" class="margin-t20px border_blu" multiple></select>
</div>

<!--e: Navi --> <!--s: 오른쪽  영역-->
<div class="rightbox">
<div class="groupmanage-create-tab blueline bg-blue clear"
    style="border-bottom: none !important;">
    <label class="loadmgmt bluebold11pt"><fmt:message key='aimir.loadmgmt.loadcontrol' /></label><!-- Load Control -->
</div>

<div class="bluebox naviheight3"><!--s: on-demand 검색 -->
<div id="lc_ondemand" class="padding-b10px">
<table class="searchoption wfree">
    <tr>
        <td><input id="lc_ondemand_radio" name="" type="radio" class="trans"></td>
        <th><fmt:message key='aimir.ondemand.operation' /> :</th>
        <!-- On-Demand  -->
        <td><input id="lc_ondemand_runTime" type="text" value="" style="width: 80px" /></td>
        <td class="padding-r20px vmiddle"><fmt:message key='aimir.hour' /></td>
        <!-- hour -->
        <th><fmt:message key='aimir.delay' /> : </th>
        <td><input id="lc_ondemand_delay" type="text" value="" style="width: 80px" /></td>
        <td class="padding-r20px vmiddle"><fmt:message key='aimir.min' /></td>
        <!-- min -->
        <td><fmt:message key='aimir.loadmgmt.supplypower' /> : </td>
        <td><input id="lc_ondemand_on" value="On" name="lc_ondemand_onOff" type="radio" class="transonly"></td>
        <th><fmt:message key='aimir.on' /></th>
        <!-- On -->
        <td><input id="lc_ondemand_off" value="Off" name="lc_ondemand_onOff" type="radio" class="transonly"></td>
        <th class="padding-r20px"><fmt:message key='aimir.off' /></th>
        <!-- Off -->
        <td>
        <em class="btn_org"><a href="javascript:addLoadControlSchedule('ondemand');"><fmt:message key='aimir.execute' /></a></em>
        </td>
    </tr>
</table>
</div>
<!--e: on-demand 검색 -->

<p class="dotline"></p>

<!--s: 예약스캐줄  -->
<div id="lc_schedule" class="margin-t15px">
    <span><input id="lc_schedule_date_radio" name="" type="radio" class="trans"></span>
    <span class="bluebold11pt margin-t3px">
        <fmt:message key='aimir.loadmgmt.schedule.reservation' /></span><!-- 예약 스케줄 -->
</div>

<div style="clear: both"></div>

<div id="lc_schedule_eventlog" class="floatright">
    <span><input id="lc_eventradio_on" name="lc_" type="radio" class="transonly"></span>
    <span class="margin-t5px padding-r20px">
        <fmt:message key='aimir.loadmgmt.schedule.eventresult' /></span><!-- 이벤트 결과 발생 -->
    <span><input id="lc_eventradio_off" name="" type="radio" class="transonly"></span>
    <span class="margin-t5px">
        <fmt:message key='aimir.loadmgmt.schedule.noeventresult' /></span><!-- 이벤트 결과 발생안함 -->
</div>
<!--s: 예약스캐줄  -->

<div id="lc_schedule_date"><!-- ========== s: 검색 1st ================= -->
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="lc_schedule_date_radio" type="radio" class="trans"></li>
    <!-- <li><input id="startDateB" type="text" style="width:80px;"></li> -->
    <li><input id="lc_detail_startDate" type="text" style="width: 80px;"></li>
    <li><select id="lc_date_fromHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_date_fromMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>

    <li><input value="~" type="text" class="between"></li>

    <!-- <li><input id="endDateB" type="text" style="width:80px;"></li> -->
    <!-- endDate삭제 <li><input id="lc_detail_endDate" type="text" style="width:80px;"></li>  -->
    <li><select id="lc_date_toHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_date_toMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_date_onOff" style="width: 50px">
        <c:forEach var="onOffCombo" items="${onOffCombo}">
            <option value="${onOffCombo.onOff}">${onOffCombo.name}</option>
        </c:forEach>
        </select></li>
    <li>
    <div id="btn">
    <ul>
        <li><a href="javascript:addLoadControlSchedule('date');"
            class="on"><fmt:message key='aimir.button.apply' /></a></li>
    </ul>
    <!-- 적용 -->
    <ul>
        <li><a href="javascript:delSchedule('loadControlTab','lc_chkbox_date');" class="on">
        <fmt:message key='aimir.button.delete' /></a></li><!-- 삭제 -->
    </ul>
    </div>
    </li>

</ul>
</div>
<!-- ======== e: 검색 1st ============= --> <!--s: 테이블 1st  -->
<div class="width-auto clear">
<table id="lc_date_tablegrid" class="table_grid">

    <colgroup>
        <col width="15" />
        <col width="10%" />
        <col width="25%" />
        <col width="25%" />
        <col width="" />
    </colgroup>
    <tr>
        <th align="center">
          <input name="lc_chkbox_date" class="transonly" id="lc_week_check" type="checkbox" /></th>
        <th><fmt:message key='aimir.time.date' /></th><!-- Date -->
        <th><fmt:message key='aimir.starttime' /></th><!-- From -->
        <th><fmt:message key='aimir.loadmgmt.endtime' /></th><!-- To -->
        <th>ON/OFF</th>
    </tr>
</table>
</div>
<!--e: 테이블 1st  --></div>

<div id="lc_schedule_weekday"><!--s: 검색 2nd -->
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="lc_schedule_week_radio" type="radio" class="trans"></li>
    <li><select id="lc_dayOfWeek" style="width: 100px">
        <option value=""><fmt:message key='aimir.dayofweek' /></option><!-- Day Of Week -->
        <c:forEach var="weekDayCombo" items="${weekDayCombo}">
            <option value="${weekDayCombo.id}">${weekDayCombo.name}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_week_fromHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_week_fromMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>

    <li><input value="~" type="text" class="between"></li>
    <li><select id="lc_week_toHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option>
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_week_toMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option>
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><select id="lc_week_onOff" style="width: 50px">
        <c:forEach var="onOffCombo" items="${onOffCombo}">
            <option value="${onOffCombo.onOff}">${onOffCombo.name}</option>
        </c:forEach>
    </select></li>
    <li>
    <div id="btn">
    <ul>
        <li><a href="javascript:addLoadControlSchedule('week');" class="on">
        <fmt:message key='aimir.button.apply' /></a></li>
    </ul>
    <!-- 적용 -->
    <ul>
        <li><a href="javascript:delSchedule('loadControlTab','lc_chkbox_week');" class="on">
        <fmt:message key='aimir.button.delete' /></a></li><!-- Delete -->
    </ul>
    </div>
    </li>
</ul>
</div>
<!--e: 검색 2nd --> <!--s: 테이블 2nd -->
<div class="width-auto clear">
<table id="lc_week_tablegrid" class="table_grid">

    <colgroup>
        <col width="15" />
        <col width="10%" />
        <col width="30%" />
        <col width="30%" />
        <col width="" />
    </colgroup>

    <tr>
        <th align="center">
          <input name="lc_chkbox_week" id="lc_chkbox_week" class="transonly" type="checkbox" /></th>
        <th><fmt:message key='aimir.dayofweek' /></th><!-- Day -->
        <th><fmt:message key='aimir.starttime' /></th><!-- From -->
        <th><fmt:message key='aimir.loadmgmt.endtime' /></th><!-- To -->
        <th>ON/OFF</th>
    </tr>

    <!--
                    <c:forEach var="weekDayCombo" items="${weekDayCombo}">
                    <tr>
                        <td align="center"><input type="checkbox" /></td>
                        <td value="{weekDayCombo.id}">${weekDayCombo.name}</td>
                        <td></td>
                        <td></td>
                    </tr>
                    </c:forEach>
                     -->

</table>
</div>
<!--e: 테이블 2nd --></div>

<div class="margin-t20px">
    <textarea name="" cols="5" rows="5" style="width: 100%"></textarea>
</div>

    <!--s: 버튼 -->
    <div class="btn_right_top2 margin-t10px">
        <em class="btn_org">
            <a href="javaScript:saveSupplyCapacity('Activation')" id="btnSearch">
            <fmt:message key='aimir.execute' /></a>
        </em>
    </div>
    <!--e: 버튼 -->
</div>

</div>
<!--e: 오른쪽 영역--> <!--e: 두번째 탭 전체 영역--></div>
<!-- 두번째 탭 끝 (Load Control)--> <!-- 세번째 탭 시작 (Load Limit) -->
<div id="loadLimitTab"><!--s: 세번째 탭 전체 영역-->
<div class="margin-t20px"><!--s: Navi -->
<div class="leftbox box-bluegradation3 naviheight2">

<div class="padding-left3px">
<ul>
    <li class="height28px"><select id="lmtab_groupType" name="select" style="width: 227px\9; width: 235px">
        <option value=""><fmt:message key="aimir.grouptype" /></option>
        <c:forEach var="groupType" items="${groupType}">
            <option value="${groupType.name}">${groupType.name}</option>
        </c:forEach>
    </select></li>
    <li class="graybold11pt floatleft"><fmt:message key='aimir.target' /> : </li><!-- 대상 -->
    <li>
    <div class="search-box">
    <ul>
        <li class="search-s1-input">
          <input id="lmtab_groupName" type="text" value="<fmt:message key='aimir.groupNmember.name'/>"
            onclick="javascript:delTxt('lmtab_groupName');"
            onkeydown="javascript:keyEvent(event, 'lmtab_groupName');"></li>
        <li class="search-s1-btn">
          <a href="javascript:searchTargetListBox('lm');"></a></li>
    </ul>
    </div>
    </li>
</ul>
</div>


<select id="lmtab_listbox" name="lmtab_listbox"
    style="width: 240px; height: 425px;" class="margin-t20px border_blu" multiple>
</select>
</div>
<!--e: Navi --> <!--s: 오른쪽  영역-->
<div class="rightbox">
<div class="groupmanage-create-tab blueline bg-blue clear" style="border-bottom: none !important;">
    <label class="loadmgmt bluebold11pt"><fmt:message key='aimir.loadmgmt.loadlimit' /></label><!-- Load Limit -->
</div>

<div class="bluebox naviheight3">

<div class="floatright">
    <span><input type="text" value="" style="width: 80px" /></span>
    <span class="btn_org margin-t2px"><a href="javascript:customerSearch();" class="on"><fmt:message key='aimir.loadmgmt.emergency' /></a></span>
</div>

<div style="clear: both"></div>

<p class="dotline margin-t15px"></p>

<!--s: on-demand 검색 -->
<div id="lm_ondemand" class="padding-b10px margin-t15px">
<table class="searchoption wfree">
    <tr>
        <td><input id="lm_ondemand_radio" name="" type="radio" class="trans"></td>
        <th><fmt:message key='aimir.ondemand.operation' /> : </th><!-- 실시간 명령 -->
        <!-- <td><input type="text" value="" style="width:80px"/></td> -->
        <!-- Limit Type  -->
        <td><select id="lm_ondemand_limitType" style="width: 100px">
            <option value=""><fmt:message key='aimir.loadmgmt.limittype' /></option><!-- LimitType -->
            <c:forEach var="limitTypeCombo" items="${limitTypeCombo}">
                <option value="${limitTypeCombo.name}">${limitTypeCombo.name}</option>
            </c:forEach>
        </select></td>
        <!-- <td class="padding-r20px vmiddle">hour</td> -->
        <!-- Limit -->
        <td><input type="text" value="" style="width: 80px" /></td>

        <!-- Period -->
        <td><select id="lm_ondemand_peakType" style="width: 130px">
            <option value=""><fmt:message key='aimir.loadmgmt.peaktype' /></option><!-- Peak Type -->
            <c:forEach var="peakTypeCombo" items="${peakTypeCombo}">
                <option value="${peakTypeCombo.name}">${peakTypeCombo.name}</option>
            </c:forEach>
        </select></td>
        <th>Period : </th>
        <td><input type="text" value="" style="width: 80px" /></td>
        <td class="padding-r20px vmiddle">h</td>
        <!-- <td><input id="lm_ondemand_on" name="" type="radio" class="transonly"></td>
                        <th>On</th>
                        <td><input id="lm_ondemand_off" name="" type="radio" class="transonly"></td>
                        <th class="padding-r20px" >Off</th> -->
        <td>
            <em class="btn_org"><a href="javascript:customerSearch();"><fmt:message key='aimir.execute' /></em>
        </td>
    </tr>
</table>
</div>
<!--e: on-demand 검색 -->

<p class="dotline"></p>

<!--s: 예약스캐줄  -->
<div id="lm_schedule" class="margin-t15px">
    <span><input id="lm_schedule_radio" name="" type="radio" class="trans"></span>
    <span class="bluebold11pt margin-t3px"><fmt:message key='aimir.loadmgmt.schedule.reservation' /></span><!-- 예약 스케줄 -->
</div>
<div style="clear: both"></div>
<!--s: 예약스캐줄  --> <!-- 상단 테이블 설정 form --> <!-- <div id="lm_scheduleList"> -->

<div id="lm_schedule_date"><!-- ========== s: 검색 1st ================= -->
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="lm_schedule_date_radio" type="radio" class="trans"></li>
    <!-- <li><input id="startDateB" type="text" style="width:80px;"></li> -->
    <li><input id="lm_detail_startDate" type="text" style="width: 65px;"></li>
    <li><select id="lm_date_fromHour" style="width: 60px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_date_fromMinute" style="width: 60px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>

    <li><input value="~" type="text" class="between"></li>

    <!-- <li><input id="endDateB" type="text" style="width:80px;"></li> -->
    <!-- endDate삭제 <li><input id="lm_detail_endDate" type="text" style="width:65px;"></li> -->
    <li><select id="lm_date_toHour" style="width: 60px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_date_toMinute" style="width: 60px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_date_limitType" style="width: 100px">
        <option value=""><fmt:message key='aimir.loadmgmt.limittype' /></option><!-- LimitType -->
        <c:forEach var="limitTypeCombo" items="${limitTypeCombo}">
            <option value="${limitTypeCombo.name}">${limitTypeCombo.name}</option>
        </c:forEach>
    </select></li>
    <li><input type="text" id="lm_date_limit" value="" style="width: 60px">
       <fmt:message key='aimir.kw' /></li><!-- kW -->
    <li><select id="lm_date_peakType" style="width: 130px">
        <option value=""><fmt:message key='aimir.loadmgmt.peaktype' /></option><!-- Peak Type -->
        <c:forEach var="peakTypeCombo" items="${peakTypeCombo}">
            <option value="${peakTypeCombo.name}">${peakTypeCombo.name}</option>
        </c:forEach>
    </select></li>
    <li>
    <div id="btn">
    <ul>
        <li><a href="javascript:addLoadLimitSchedule('date');" class="on">
        <fmt:message key='aimir.button.apply' /></a></li>
    </ul>
    <!-- 적용 -->
    <ul>
        <li><a href="javascript:delSchedule('loadLimitTab','lm_chkbox_date');" class="on">
        <fmt:message key='aimir.button.delete' /></a></li>
    </ul>
    </div>
    </li>

</ul>
</div>

<!--s: 테이블 1st  -->
<div class="width-auto clear">
<table id="lm_date_tablegrid" class="table_grid">

    <colgroup>
        <col width="15" />
        <col width="10%" />
        <col width="15%" />
        <col width="15%" />
        <col width="15%" />
        <col width="15%" />
        <col width="15%" />
    </colgroup>

    <tr>
        <th><input name="lm_chkbox_date" id="lc_date_check"
            class="transonly" type="checkbox" /></th>
        <th><fmt:message key='aimir.time.date'/></th>
        <th><fmt:message key='aimir.number' /></th>
        <th><fmt:message key='aimir.starttime' /></th>
        <th><fmt:message key='aimir.loadmgmt.endtime' /></th>
        <th><fmt:message key='aimir.loadmgmt.limittype' /></th>
        <th><fmt:message key='aimir.loadmgmt.limit' /></th>
        <th><fmt:message key='aimir.loadmgmt.peaktype' /></th>
    </tr>
</table>
</div>
</div>
<!--e: 테이블 1st  --> <!--s: 테이블 2nd -->
<div id="lm_schedule_weekday"><!--s: 검색 2nd -->
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="lm_schedule_week_radio" type="radio" class="trans"></li>
    <li><select id="lm_dayOfWeek" style="width: 60px">
        <option value=""><fmt:message key='aimir.dayofweek' /></option>
        <c:forEach var="weekDayCombo" items="${weekDayCombo}">
            <option value="${weekDayCombo.id}">${weekDayCombo.name}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_week_fromHour" style="width: 60px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_week_fromMinute" style="width: 60px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>

    <li><input value="~" type="text" class="between"></li>
    <li><select id="lm_week_toHour" style="width: 60px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_week_toMinute" style="width: 60px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><select id="lm_week_limitType" style="width: 100px">
        <option value=""><fmt:message key='aimir.loadmgmt.limittype' /></option><!-- Limit Type -->
        <c:forEach var="limitTypeCombo" items="${limitTypeCombo}">
            <option value="${limitTypeCombo.name}">${limitTypeCombo.name}</option>
        </c:forEach>
    </select></li>
    <li><input type="text" id="lm_week_limit" value="" style="width: 80px"></li>
    <li><select id="lm_week_peakType" style="width: 130px">
        <option value=""><fmt:message key='aimir.loadmgmt.peaktype' /></option><!-- Peak Type -->
        <c:forEach var="peakTypeCombo" items="${peakTypeCombo}">
            <option value="${peakTypeCombo.name}">${peakTypeCombo.name}</option>
        </c:forEach>
    </select></li>
    <li>
    <div id="btn">
    <ul>
        <li><a href="javascript:addLoadLimitSchedule('week');" class="on">
          <fmt:message key='aimir.button.apply' /></a></li>
    </ul>
    <!-- 적용 -->
    <ul>
        <li><a href="javascript:delSchedule('loadLimitTab','lm_chkbox_week');" class="on">
          <fmt:message key='aimir.button.delete' /></a></li>
    </ul>
    </div>
    </li>
</ul>
</div>
<!--e: 검색 2nd -->

<div class="width-auto clear">
<table id="lm_week_tablegrid" class="table_grid">

    <colgroup>
        <col width="15" />
        <col width="10%" />
        <col width="15%" />
        <col width="15%" />
        <col width="15%" />
        <col width="15%" />
        <col width="" />
    </colgroup>

    <tr>
        <th><input name="lm_chkbox_date" id="lc_week_check"
            class="transonly" type="checkbox" /></th>
        <th><fmt:message key='aimir.dayofweek' /></th>
        <th><fmt:message key='aimir.starttime' /></th>
        <th><fmt:message key='aimir.loadmgmt.endtime' /></th>
        <th><fmt:message key='aimir.loadmgmt.limittype' /></th>
        <th><fmt:message key='aimir.loadmgmt.limit' /></th>
        <th><fmt:message key='aimir.loadmgmt.peaktype' /></th>
    </tr>
</table>
</div>
</div>
<!--e: 테이블 2nd --> <!-- </div> -->

<div class="margin-t20px">
    <textarea name="" cols="5" rows="5" style="width: 100%"></textarea>
</div>

<!--s: 버튼 -->
<div  class="btn_right_top2 margin-t10px">
    <em class="btn_org">
        <a href="javaScript:saveSupplyCapacity('Activation')" class="on" id="btnSearch">
        <fmt:message key='aimir.execute' /></a>
    </em>
</div>
<!--e: 버튼 -->

</div>

</div>
<!--e: 오른쪽 영역--></div>
<!--e: 두번째 탭 전체 영역--></div>
<!-- 세번째 탭 끝 (Load Limit) -->

<!-- 네번째 탭 시작 (Load Shed) -->
<div id="loadShedTab"><!-- 검색옵션 시작 -->
<!-- <div class="search-bg-basic2"> -->
<ul class="basic-ul">
    <!-- 그룹 타입 리스트박스-->
    <li class="basic-li">
    <table class="searchoption wfree">
        <tr>
            <td><fmt:message key="aimir.grouptype" /></td>
            <td><select id="lstab_groupType" name="select" style="width: 120px;">
                <option value=""><fmt:message key="aimir.grouptype" /></option>
                <c:forEach var="groupType" items="${groupType}">
                    <option value="${groupType.id}">${groupType.name}</option>
                </c:forEach>
                </select></td>
            <td><select id="lstab_scheduleType" style="width: 100px;">
                <option value="Date"><fmt:message key='aimir.time.date' /></option><!-- Date -->
                <option value="DayOfWeek"><fmt:message key='aimir.dayofweek' /></option><!-- Week -->
                <option value="None"><fmt:message key='aimir.loadmgmt.noschedule' /></option><!-- No Schedule -->
                </select></td>
            <td><div id="ls_div_searchDate">
                  <span><input id="ls_list_startDate" type="text" class="day" readonly="readonly"></span>
                  <span><input id="ls_list_btw" value="~" class="between" type="text" readonly="readonly"></span>
                  <span><input id="ls_list_endDate" type="text" class="day" readonly="readonly"></span>
                </div>
                <div id="ls_div_searchDay">
                  <select id="ls_list_dayOfWeek">
                  <option value="">전체</option>
                  <c:forEach var="weekDayCombo" items="${weekDayCombo}">
                    <option value="${weekDayCombo.id}">${weekDayCombo.name}</option>
                  </c:forEach>
                  </select>
                </div>
                <div id="ls_div_groupName">
                  <span><input id="ls_groupName" type="text" style="width:100px;"></span>
                </div>
            <input id="sInstallStartDateHidden" type="hidden">
            <input id="sInstallEndDateHidden" type="hidden"></td>
            <td>
            <div id="btn">
            <ul>
                <li><a href="javascript:searchLoadShed();" class="on">
                    <fmt:message key='aimir.button.search' /></a></li>
            </ul>
            <!-- 조회 --></div>
            </td>

        </tr>
    </table>
    </li>
</ul>
<!-- </div> -->
<!-- 검색옵션 끝 --> <!--s: 상단 테이블 1st -->
<div class="width-auto margin-t20px"><object
    id="loadMgmtShedListFlexEx"
    classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
    height="122">
    <param name="movie" value="${ctx}/flexapp/swf/loadMgmtShedList.swf" />
    <param name="wmode" value="opaque">

    <!--[if !IE]>--> <object id="loadMgmtShedListFlexOt"
        type="application/x-shockwave-flash"
        data="${ctx}/flexapp/swf/loadMgmtShedList.swf" width="100%"
        height="122">
        <param name="wmode" value="opaque">
        <!--<![endif]-->
        <div>
        <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
        <p><a href="http://www.adobe.com/go/getflashplayer"><img
            src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"
            alt="Get Adobe Flash player" width="112" height="33" /></a></p>
        </div>
        <!--[if !IE]>--> </object> <!--<![endif]--> </object></div>
<!--e: 상단 테이블 1st --> <!--s: 테이블 버튼  -->
<div id="btn" class="btn_right_top2 margin-t10px">
<ul>
    <li><a href="javaScript:saveSupplyCapacity('Activation')" class="on" id="btnSearch">
       <fmt:message key='aimir.add' /></a></li>
</ul>
<ul>
    <li><a href="javaScript:saveSupplyCapacity('Deactivation')" class="on" id="btnSearch">
       <fmt:message key='aimir.button.delete' /></a></li>
</ul>
</div>
<!--e: 테이블 버튼  --> <!-- 화면 하단 Load Shed 정책 설정-->
<div class="headspace_2ndline2 clear">
    <label class="check"><fmt:message key='aimir.loadmgmt.loadshed.setpolicy' /></label><!-- Load Shed 정책 설정 -->
</div>

<!--s: 하단 영역-->
<div class="box-bluegradation3"><!--s:emergency 검색-->

<!--s: emergency 검색--> <!--s: 하단서브 영역-->
<div class="margin-t20px"><!--s: Navi --> <!-- <div class="navibox naviheight">금천구> 가산동 > 우림 아이온스 벨리 A동</div> -->
<!--e: Navi --> <!--s: 오른쪽  영역-->
<div class="rightbox">
<div class="groupmanage-create-tab blueline bg-blue clear" style="border-bottom: none !important;">
    <label class="loadmgmt bluebold11pt"><fmt:message key='aimir.loadmgmt.loadshed.set' /></label><!-- Load Shed 설정 -->
</div>

<div class="bluebox"><!--s: 검색-->
<div>
<table class="searchoption wfree">
    <tr>
        <th><fmt:message key='aimir.loadmgmt.supply' /> : </th>
        <!-- Supply -->
        <td><input id="ls_ondemand_supplyCapacity" type="text" value="" style="width: 80px" /></td>
        <td class="padding-r20px" style="vertical-align: middle"><fmt:message key='aimir.kw' /></td><!-- kw -->
        <th><fmt:message key='aimir.threshold' /> : </th><!-- Threshold, 임계치 -->
        <td><input id="ls_ondemand_supplyThreshold" type="text" value="" style="width: 80px" /></td>
        <td><select id="ls_ondemand_onOff" style="width: 50px">
            <c:forEach var="onOffCombo" items="${onOffCombo}">
                <option value="${onOffCombo.onOff}">${onOffCombo.name}</option>
            </c:forEach>
            </select></td>
        <td><div id="btn">
        <ul><li><a href="javascript:searchModem()" class="on"><fmt:message key='aimir.loadmgmt.emergency' /></a></li></ul></td><!-- Emergency -->
    </tr>
</table>
</div>
<!--e: 검색 -->

<div style="clear: both"></div>

<!--s: 이벤트 결과 발생 여부  -->
<div class="floatright">
    <span><input id="ls_eventradio_on" name="" type="radio" class="transonly"></span>
    <span class="margin-t5px padding-r20px"><fmt:message key='aimir.loadmgmt.schedule.eventresult' /></span><!-- 이벤트 결과 발생 -->
    <span><input id="ls_eventradio_off" name="" type="radio" class="transonly"></span>
    <span class="margin-t5px"><fmt:message key='aimir.loadmgmt.schedule.noeventresult' /></span><!-- 이벤트 결과 발생안함 -->
</div>
<!--e: 이벤트 결과 발생 여부--> <!--s: 검색 1st -->
<div id="ls_schedule_date">
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="ls_schedule_date_radio" type="radio" class="trans"></li>
    <li><span><input id="ls_detail_startDate" type="text"
        class="day" readonly="readonly" onChange="javascript:send();"></span></li>
    <li><select id="ls_date_fromHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="ls_date_fromMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><input value="~" type="text" class="between"></li>

    <!-- endDate삭제 <li><span><input id="ls_detail_endDate" type="text" class="day" readonly="readonly" onChange="javascript:send();"></span></li> -->
    <li><select id="ls_date_toHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
    </select></li>
    <li><select id="ls_date_toMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
    </select></li>
    <li><select id="ls_date_onOff" style="width: 50px">
        <c:forEach var="onOffCombo" items="${onOffCombo}">
            <option value="${onOffCombo.onOff}">${onOffCombo.name}</option>
        </c:forEach>
    </select></li>
    <li>
    <div id="btn">
    <ul>
        <li>
          <a href="javascript:addLoadShedSchedule('date');" class="on"><fmt:message key='aimir.button.apply' /></a>
        </li>
    </ul>
    <ul>
        <li>
          <a href="javascript:delSchedule('loadShedTab','ls_chkbox_date');" class="on"><fmt:message key='aimir.button.delete' /></a>
        </li>
    </ul>
    </div>
    </li>
</ul>
</div>
<!--e: 검색 1st --> <!--s: 테이블 1st  -->
<div class="width-auto clear">
<table id="ls_date_tablegrid" class="table_grid">

    <colgroup>
        <col width="1%" />
        <col width="10%" />
        <col width="25%" />
        <col width="25%" />
        <col width="" />
    </colgroup>

    <tr>
        <th></th>
        <th><fmt:message key='aimir.time.date' /></th>
        <th><fmt:message key='aimir.starttime' /></th>
        <th><fmt:message key='aimir.loadmgmt.endtime' /></th>
        <th>*</th>
    </tr>
</table>
</div>
</div>
<!--e: 테이블 1st  --> <!--s: 검색 2nd -->
<div id="ls_schedule_weekday">
<div class="searchoption select-treetype search-space">
<ul>
    <li><input id="ls_schedule_week_radio" type="radio" class="trans"></li>
    <li><select id="ls_dayOfWeek" style="width: 60px">
        <option value=""><fmt:message key='aimir.dayofweek' /></option><!-- Day -->
        <c:forEach var="weekDayCombo" items="${weekDayCombo}">
            <option value="${weekDayCombo.id}">${weekDayCombo.name}</option>
        </c:forEach>
        </select></li>
    <li><select id="ls_week_fromHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
        </select></li>
    <li><select id="ls_week_fromMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
        </select></li>
    <li><input value="~" type="text" class="between"></li>
    <li><select id="ls_week_toHour" style="width: 100px">
        <option value=""><fmt:message key='aimir.hour' /></option><!-- Hour -->
        <c:forEach var="hourCombo" items="${hourCombo}">
            <option value="${hourCombo.hour}">${hourCombo.hour}</option>
        </c:forEach>
        </select></li>
    <li><select id="ls_week_toMinute" style="width: 100px">
        <option value=""><fmt:message key='aimir.minute' /></option><!-- Minute -->
        <c:forEach var="minuteCombo" items="${minuteCombo}">
            <option value="${minuteCombo.minute}">${minuteCombo.minute}</option>
        </c:forEach>
        </select></li>
    <li><select id="ls_week_onOff" style="width: 50px">
        <c:forEach var="onOffCombo" items="${onOffCombo}">
            <option value="${onOffCombo.onOff}">${onOffCombo.name}</option>
        </c:forEach>
        </select></li>
    <li>
    <div id="btn">
    <ul>
        <li>
          <a href="javascript:addLoadShedSchedule('week');" class="on"><fmt:message key='aimir.button.apply' /></a>
        </li><!-- 적용 -->
    </ul>
    <ul>
        <li>
          <a href="javascript:delSchedule('loadShedTab','ls_chkbox_week');" class="on"><fmt:message key='aimir.button.delete' /></a>
        </li>
    </ul>
    </div>
    </li>
</ul>
</div>
<!--e: 검색 2nd --> <!--s: 테이블 2nd -->
<div class="width-auto">
<table id="ls_week_tablegrid" class="table_grid clear">

    <colgroup>
        <col width="1%" />
        <col width="10%" />
        <col width="40%" />
        <col width="" />
    </colgroup>

    <tr>
        <th></th>
        <th><fmt:message key='aimir.dayofweek' /></th><!-- Day -->
        <th><fmt:message key='aimir.time.from' /></th><!-- From -->
        <th><fmt:message key='aimir.time.to' /></th><!-- To -->
    </tr>
</table>
</div>
</div>
<!--e: 테이블 2nd --> <!--s: 하단 버튼 -->
<div id="btn" class="btn_right_top2 margin-t10px">
<ul>
    <li><a href="javaScript:saveSupplyCapacity('Activation')"
        class="on" id="btnSearch"><fmt:message key='aimir.add' /></a></li><!-- 추가 -->
</ul>
<ul>
    <li><a href="javaScript:saveSupplyCapacity('Deactivation')"
        class="on" id="btnSearch"><fmt:message key='aimir.button.delete' /></a></li><!-- 삭제 -->
</ul>
</div>
<!--e: 하단 버튼 --></div>

</div>
<!--e: 오른쪽 영역--></div>
<!--e: 하단서브 영역--></div>
<!--e: 하단 영역--></div>
<!-- 네번째 탭 끝 (Load Shed) --> <!-- 캘린더용 변수 -->
<li class="basic-li"><input id="searchStartDateA" type="hidden" /></li>
<li class="basic-li"><input id="searchEndDateA" type="hidden" /></li>

<li class="basic-li"><input id="searchStartDateB" type="hidden" /></li>
<li class="basic-li"><input id="searchEndDateB" type="hidden" /></li>

</div>
</body>
</html>