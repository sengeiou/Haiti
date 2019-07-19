<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>


    <script>
    $(function(){

        $('a').click(function(event) {
            event.preventDefault();
        });
       
        $('#configForm a#add3').click( function() {

            var devicemodelId = $("#modelForm :hidden[name='id']").val();
            $("#configForm :hidden[name='devicemodelId']").val(devicemodelId);
            
            var options = {
                    success : configAddResult,    
                    url : '${ctx}/gadget/system/deviceconfigadd.do',  
                    type : 'post',
                    datatype : 'json'
                };
            
            $('#configForm').ajaxSubmit(options);
                        
        });
        
        $('#configForm a#update3').click( function() {
            var deviceconfigId = $("#configForm :hidden[name='id']").val();

            if (deviceconfigId) {
                var options = {
                        success : configUpdateResult,    
                        url : '${ctx}/gadget/system/deviceconfigedit.do',  
                        type : 'post',
                        datatype : 'json'
                    };
                
                $('#configForm').ajaxSubmit(options);
            }             
        });
        
        $('#configForm a#delete3').click( function() {
            var deviceconfigId = $("#configForm :hidden[name='id']").val();

            if (deviceconfigId) {
                var options = {
                        success : configDeleteResult,    
                        url : '${ctx}/gadget/system/deviceconfigdelete.do?deviceconfigId='+deviceconfigId,  
                        datatype : 'json'
                    };
                
                $('#configForm').ajaxSubmit(options);
            }             
        });
        

    });
    
    <!-- config callback -->
    function configAddResult(responseText, status) {
        alert(responseText.result);
        
        $("#configForm :hidden[name='id']").val(responseText.configForm.id);
        modelTabListener();
    }
    
    function configUpdateResult(responseText, status) {
        alert(responseText.result);
        modelTabListener();
    }
    
    function configDeleteResult(responseText, status) {
        alert(responseText.result);
        $('#configForm').resetForm();
        modelTabListener();
    } 

    <!-- meterconfig data setting -->
    function bindingConfigInfo(deviceConfig) {
        $("#configForm :hidden[name='id']").val(deviceConfig.id);
        $("#configForm :input[name='meterClass']").val(deviceConfig.meterClass);
        $("#configForm :input[name='phase']").val(deviceConfig.phase);
        $("#configForm :input[name='powerSupplySpec']").val(deviceConfig.powerSupplySpec);
        $("#configForm :input[name='lpInterval']").val(deviceConfig.lpInterval);
        $("#configForm :input[name='pulseConst']").val(deviceConfig.pulseConst);
    } 
    </script>


    <form id="configForm">
        <input type="hidden" name="id" />
        <input type="hidden" name="devicemodelId" />
        <ul>
            <li ><fmt:message key="aimir.meter.class" /></li>
            <li>
                <input type="text" name="meterClass" size="30" maxlength="80" />
            </li>
        </ul>
        <ul>
            <li ><fmt:message key="aimir.phasetype2" /></li>
            <li>
                <input type="text" name="phase" size="30" maxlength="80" />
            </li>
        </ul>
        <ul>
            <li><fmt:message key="aimir.supply.powerspec" /></li>
            <li>
              <input type="text" name="powerSupplySpec" size="30" maxlength="80" />
            </li>
        </ul>
        <ul>
            <li ><fmt:message key="aimir.loadprofile.save.period" /></li>
            <li>
              <input type="text" name="lpInterval" size="30" maxlength="80" />
            </li>
        </ul>

        <!-- 
        <ul>
            <li >채널1</li>
            <li>
              <select name="" class="nuri_search_n" style="width:150px; padding:4px">
              <option selected>Active Energy</option>
            </select>
            </li>
            <li>
            <div>
            <ul>
            <li><a href="#">삭제</a></li>
            
            </ul>
            </div>
            </li>
        </ul>
        <ul>
            <li>채널2</li><li>
              <select name="" class="nuri_search_n" style="width:150px; padding:4px">
              <option selected>Reactive Energy</option>
            </select>
            </li>
            <li>
            <div>
            <ul>
            <li><a href="#">삭제</a></li>
            
            </ul>
            </div>
            </li>
        </ul>
         -->
        
        <ul>
            <li><fmt:message key="aimir.pulse.constant" /></li>
            <li>
              <input type="text" name="pulseConst" size="30" maxlength="80" />m3/h
            </li>
        </ul>
        
        <div>
        <!-- 
            <button id="add">등록</button>
            <button id="update">수정</button>
            <button id="delete">삭제</button>
         -->
                        <a href="#" id="add3"><fmt:message key="aimir.button.register" /></a>
                        <a href="#" id="update3"><fmt:message key="aimir.update" /></a>
                        <a href="#" id="delete3"><fmt:message key="aimir.button.delete" /></a>            
        </div>
    </form> 
