<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>


    <script>
    $(document).ready(function(){

        
    });
    <!-- mcuconfig data setting -->
    function bindingConfigInfo(deviceConfig) {
        $("#configForm :hidden[name='id']").val(deviceConfig.id);
    }    
    </script>


    <form id="configForm">
        <input type="hidden" name="id" />
        <input type="hidden" name="devicemodelId" />
            <h1> 장비 환경설정</h1>
            <br/>
            <br/>
            <p>미터장비를 제외한 설정화면은 아직 디자인이 나오지 않았습니다.</p>
    </form> 
