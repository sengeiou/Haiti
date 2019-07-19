<%-- 
    commonDateTabButtonType3.jsp 를 참고함.
    - 하나의 jsp 에서 두 개의 날짜조건 사용 가능하도록 모든 변수명과 function 명 뒤에 별도의 접미사를 추가함.
      style 이 div 의 id 와 맞물려있어서 id 변경 시 style 이 적용되지 않아 일단 Report 관리 화면에서 사용하는 Period 만 적용함.

    - 하나의 jsp 에서 두번 이상 사용할 경우를 위해 접미사를 동적으로 적용하게 수정함.
      접미사를 별도로 정하지 않는경우 'Snd' 로 적용됨.
      적용예: 접미사를 'Trd' 로 지정함.
          <c:set var="datesuffix" value="Trd"/>
          <%@ include file="/gadget/commonDateTabButtonType4.jsp"%>
--%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<c:if test="${datesuffix == null || datesuffix == ''}">
    <c:set var="datesuffix" value="Snd"/>
</c:if>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonData${datesuffix};

    var _supplierId${datesuffix}='';
    $(document).ready(function(){
        commonDateTabButtonInterval${datesuffix} = setInterval("commonDateTabButtonDelay${datesuffix}()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId${datesuffix} = json.supplierId;
                }
        );
    });

    function commonDateTabButtonDelay${datesuffix}(){
        if(_supplierId${datesuffix} != ''){
            clearInterval(commonDateTabButtonInterval${datesuffix});
            commonDateTabButtonInit${datesuffix}();
          }
    }

    function commonDateTabButtonInit${datesuffix}(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_period${datesuffix}')        .bind('click',function(event) { $('#searchDateType${datesuffix}').val(DateType.PERIOD        );getSearchDate${datesuffix}(DateType.PERIOD); } ); });

        //조회버튼클릭 이벤트 생성
        $(function() { $('#btnSearch${datesuffix}').bind('click',function(event) { sendRequest${datesuffix}($('#searchDateType${datesuffix}').val()); } ); });

        $(function() { $('#periodSearch${datesuffix}')       .bind('click',function(event) { sendRequest${datesuffix}(DateType.PERIOD     ); } ); });

<%--
        //기간별조회 버튼클릭 이벤트 생성
        $(function() { $('#periodType${datesuffix}')     .bind('change',function(event)
                { var idx = $('#periodType${datesuffix}').val();
                if(idx==0){
                    setPeriod${datesuffix}(0);
                }else if(idx==1){
                    setPeriod${datesuffix}(-1);
                }else if(idx==2){
                    setPeriod${datesuffix}(-2);
                }else if(idx==3){
                    setPeriod${datesuffix}(-6);
                }else if(idx==4){
                    setPeriod${datesuffix}(-30);
                }

                } ); });
--%>

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#periodStartDate${datesuffix}')     .bind('change', function(event) { getSearchDate${datesuffix}(DateType.PERIOD); } ); });
        $(function() { $('#periodEndDate${datesuffix}')       .bind('change', function(event) { getSearchDate${datesuffix}(DateType.PERIOD); } ); });

        var locDateFormat${datesuffix} = "yymmdd";

        //탭별 일자DatePicker 생성
        $("#periodStartDate${datesuffix}")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat${datesuffix}, onSelect: function(dateText, inst) { modifyDate${datesuffix}(dateText, inst);}} );
        $("#periodEndDate${datesuffix}")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat${datesuffix}, onSelect: function(dateText, inst) { modifyDate${datesuffix}(dateText, inst);}} );

        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:_supplierId${datesuffix}}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear   = json.currYear;//currDate.getYear();
                     var currDate  = json.currDate;

                     //탭별 현재일자 설정
                     if(tabs${datesuffix}.period!=0)setPeriod${datesuffix}(0,false);


                });


        var peroidLabels${datesuffix} = new Array();
        peroidLabels${datesuffix}[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
        peroidLabels${datesuffix}[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
        peroidLabels${datesuffix}[2] = '<fmt:message key="aimir.threedays"/>';//3일''
        peroidLabels${datesuffix}[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
        peroidLabels${datesuffix}[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

<%--
        $("#periodType${datesuffix}").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabels${datesuffix}});
        $("#periodType${datesuffix}").selectbox();
--%>

        //탭생성
        $("#datetab${datesuffix}").hide();
        $("#datetab${datesuffix}").tabs();
        $("#datetab${datesuffix}").show();

        //탭숨김처리
        if(tabs${datesuffix}.period==0)hideTab${datesuffix}(DateType.PERIOD);

        if(tabNames${datesuffix}.period!=null&&tabNames${datesuffix}.period!=''&&tabNames${datesuffix}.period!='undefined')$('#_period${datesuffix}').html(tabNames${datesuffix}.period);

        // 검색버튼 표시
        if(tabs${datesuffix}.search_period==1)        showSearchBtn${datesuffix}('periodSearch${datesuffix}');

    }


    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate${datesuffix}(setDate, inst){
        var dateId = '#' + inst.id;

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:_supplierId${datesuffix}}
                ,function(json) {
                    $(dateId).val(json.localDate);
                    $(dateId).trigger('change');
                });
    }


    /**
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function sendRequest${datesuffix}(_dateType){
        // 조회조건 검증
        if(!validateSearchCondition${datesuffix}(_dateType))return false;
        send${datesuffix}();
    }


    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertSearchDate${datesuffix}(){
        $.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#searchStartDate${datesuffix}').val(), searchEndDate:$('#searchEndDate${datesuffix}').val(), supplierId:_supplierId${datesuffix}}
                ,function(json) {
                    $('#searchStartDate${datesuffix}').val(json.searchStartDate);
                    $('#searchEndDate${datesuffix}').val(json.searchEndDate);
                });
    }


    /**
     * 기간별 버튼처리
     */
    function setPeriod${datesuffix}(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierId${datesuffix}}
                ,function(json) {
                    // TODO - 날짜 textbox 에 값 입력
                    $('#periodStartDate${datesuffix}').val(json.searchDate);
                    $('#periodEndDate${datesuffix}').val(json.currDate);
                    // TODO - 날짜 textbox width 를 입력된 값의 길이로 설정.
                    $('#periodStartDate${datesuffix}').attr("size", $('#periodStartDate${datesuffix}').val().length);
                    $('#periodEndDate${datesuffix}').attr("size", $('#periodEndDate${datesuffix}').val().length);
                    
                    getSearchDate${datesuffix}(DateType.PERIOD,flag);
                });
    }


    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getSearchDate${datesuffix}(_dateType,flag){
        var startDate='';
        var endDate='';

        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if(flag == false){
            $('div#datetab${datesuffix} > ul:first > li:first > a:first').trigger('click');
            return;
        }

        if(DateType.PERIOD == _dateType){
            if(tabs${datesuffix}.period==0)return;
            $('#searchStartDate${datesuffix}').val($('#periodStartDate${datesuffix}').val());
            $('#searchEndDate${datesuffix}').val($('#periodEndDate${datesuffix}').val());

            convertSearchDate${datesuffix}();
        }


    }


    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function hideTab${datesuffix}(_idx){
        $("#datetab${datesuffix}").tabs("remove",_idx);
        $("#datetab${datesuffix}").tabs("select",0);
    }

    /**
     * 조회버튼을 표시한다.
     */
    function showSearchBtn${datesuffix}(btnName){

        var liName = '#' + btnName + 'Li';
        $(liName).css('display','block');

    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchCondition${datesuffix}(_dateType){

        if(DateType.PERIOD == _dateType){
            //시작일,종료일 필수 체크
            //시작일,종료일 선후 체크
            //시작일,종료일 기간체크(31일 이내)
            if(Number($('#searchStartDate${datesuffix}').val()) > Number($('#searchEndDate${datesuffix}').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if(Number($('#searchEndDate${datesuffix}').val()) - Number($('#searchStartDate${datesuffix}').val()) > 30){
                //최대 31일까지만 조회 가능합니다.
                //alert('<fmt:message key="aimir.season.error"/>');
                //return false;
            }
            return true;
        } else{
            //날짜타입오류
            return false;
        }
    }

    //======================================================================================

/*]]>*/
</script>

<!-- Tab (S) -->
<div id="datetab${datesuffix}" class="buttontype" style="display:none; padding:0 !important">
    <div id="period${datesuffix}">
        <!-- <label><fmt:message key="aimir.period"/></label> -->
        <ul class="period_date">
<%--
            <li><select id="periodType${datesuffix}" class="date-w90"></select></li>
            <li class="date-space"></li>
--%>
            <!-- <li><input id="periodStartDate${datesuffix}" class="day" style="text-align:center;" type="text" readonly="readonly" size="15"></li> -->
            <li><input id="periodStartDate${datesuffix}" style="text-align:center;" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <!-- <li><input id="periodEndDate${datesuffix}" class="day" type="text" readonly="readonly"></li> -->
            <li><input id="periodEndDate${datesuffix}" style="text-align:center;" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
            <ul><li id="periodSearch${datesuffix}Li"><a href="#;" id="periodSearch${datesuffix}" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <ul class="buttontype" style="display:none">
        <li><a href="#period${datesuffix}" id="_period${datesuffix}"><fmt:message key="aimir.period"/></a></li>
    </ul>
        <input id="searchStartDate${datesuffix}" type="hidden"/>
        <input id="searchEndDate${datesuffix}" type="hidden" />
        <input id="searchDateType${datesuffix}" type="hidden" value="0"/>
</div>
<!-- Tab (E) -->
