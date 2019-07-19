<%--
 @ Sub Page For ModemMaxGadget.jsp
 @ Insert to  (<!-- Tab 3 : schedule (S) -->)

--%>

<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
         contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!-- SCRIPT -->
<script>
    if(modemType == "MMIU" || modemType == "SubGiga"){
        $('#pulseModemSchedule').hide();
        $('#energyModemSchedule').show();
    }else{
        $('#pulseModemSchedule').show();
        $('#energyModemSchedule').hide();
    }
</script>
<!-- 1. Pulse Type (S) -->
<div id="pulseModemSchedule">
<ul><li>

    <!-- search-default (S) -->
    <div class="blueline" style="height:490px;">
        <ul class="width">
            <li class="padding">

                <form id="modemScheduleForm">

                    <!-- DIV INSIDE (S) -->

                    <ul><li class="modem-schedule-checklist">
                        <span><label class="check"><fmt:message key="aimir.sendperiod"/></label></span>
                            <span><select id="lpPeriod" name="lpPeriod">
                                      <option value="0">No LP</option>
                                      <option value="1">60 <fmt:message key="aimir.minute"/></option>
                                      <option value="2">30 <fmt:message key="aimir.minute"/></option>
                                      <option value="3">15 <fmt:message key="aimir.minute"/></option>
                                  </select>
                            </span>
                        <span style="width:30px">&nbsp;</span>

                        <span><label class="check"><fmt:message key="aimir.alarmFlag"/></label></span>
                            <span><select id="alarmFlag" name="alarmFlag">
                                      <option value="0">disable</option>
                                      <option value="1">enable</option>
                                  </select>
                            </span>
                        <span style="width:30px">&nbsp;</span>

                        <span><label class="check">LP Choice</label></span>
                            <span><select id="lpChoice" name="lpChoice">
                                        <option value="0">0</option>
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                        <option value="6">6</option>
                                        <option value="7">7</option>
                                        <option value="8">8</option>
                                        <option value="9">9</option>
                                        <option value="10">10</option>
                                        <option value="11">11</option>
                                        <option value="12">12</option>
                                        <option value="13">13</option>
                                        <option value="14">14</option>
                                        <option value="15">15</option>
                                        <option value="16">16</option>
                                        <option value="17">17</option>
                                        <option value="18">18</option>
                                        <option value="19">19</option>
                                        <option value="20">20</option>
                                        <option value="21">21</option>
                                        <option value="22">22</option>
                                        <option value="23">23</option>
                                        <option value="24">24</option>
                                        <option value="25">25</option>
                                        <option value="26">26</option>
                                        <option value="27">27</option>
                                        <option value="28">28</option>
                                        <option value="29">29</option>
                                        <option value="30">30</option>
                                        <option value="31">31</option>
                                        <option value="32">32</option>
                                        <option value="33">33</option>
                                        <option value="34">34</option>
                                        <option value="35">35</option>
                                        <option value="36">36</option>
                                        <option value="37">37</option>
                                        <option value="38">38</option>
                                        <option value="39">39</option>
                                  </select>
                            </span>
                    </li></ul>


                    <div class="padding-t20px">
                        <table class="time_table">
                            <caption><label class="check"><fmt:message key="aimir.meteringtime"/></label></caption>
                            <tr>
                                <th>
                                    <span class="margin-l5 margin-t5px greenbold11pt"><fmt:message key="aimir.all"/></span>
                                    <span><input type="checkbox" class="transonly" id="hAll" onClick="javascript:minAll()" /></span>
                                </th>
                                <th>00</th>
                                <th>01</th>
                                <th>02</th>
                                <th>03</th>
                                <th>04</th>
                                <th>05</th>
                                <th>06</th>
                                <th>07</th>
                                <th>08</th>
                                <th>09</th>
                                <th>10</th>
                                <th>11</th>
                                <th>12</th>
                                <th>13</th>
                                <th>14</th>
                                <th>15</th>
                                <th>16</th>
                                <th>17</th>
                                <th>18</th>
                                <th>19</th>
                                <th>20</th>
                                <th>21</th>
                                <th>22</th>
                                <th>23</th>
                            </tr>
                            <tr>
                                <th>0 <fmt:message key="aimir.minute"/></th>
                                <td><input type="checkbox" class="transonly" id="h93" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h92" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h85" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h84" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h77" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h76" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h69" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h68" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h61" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h60" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h53" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h52" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h45" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h44" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h37" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h36" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h29" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h28" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h21" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h20" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h13" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h12" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h5"  onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h4"  onClick="javascript:timeCheck()"/></td>
                            </tr>
                            <tr>
                                <th>15 <fmt:message key="aimir.minute"/></th>
                                <td><input type="checkbox" class="transonly" id="h94" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h91" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h86" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h83" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h78" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h75" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h70" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h67" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h62" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h59" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h54" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h51" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h46" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h43" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h38" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h35" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h30" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h27" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h22" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h19" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h14" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h11" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h6"  onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h3"  onClick="javascript:timeCheck()"/></td>
                            </tr>
                            <tr>
                                <th>30 <fmt:message key="aimir.minute"/></th>
                                <td><input type="checkbox" class="transonly" id="h95" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h90" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h87" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h82" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h79" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h74" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h71" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h66" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h63" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h58" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h55" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h50" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h47" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h42" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h39" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h34" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h31" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h26" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h23" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h18" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h15" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h10" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h7"  onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h2"  onClick="javascript:timeCheck()"/></td>
                            </tr>
                            <tr>
                                <th>45<fmt:message key="aimir.minute"/></th>
                                <td><input type="checkbox" class="transonly" id="h96" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h89" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h88" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h81" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h80" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h73" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h72" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h65" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h64" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h57" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h56" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h49" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h48" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h41" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h40" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h33" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h32" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h25" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h24" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h17" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h16" onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h9"  onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h8"  onClick="javascript:timeCheck()"/></td>
                                <td><input type="checkbox" class="transonly" id="h1"  onClick="javascript:timeCheck()"/></td>
                            </tr>
                        </table>
                    </div>

                    <input type="hidden" id="modemId" name="id" />
                    <input type="hidden" id="meteringDay"  name="meteringDay" />
                    <input type="hidden" id="meteringHour" name="meteringHour" />

                </form>

                <div id="scheduleBtnList" class="headspace_2ndline" style="width:100%;">
                    <em class="btn_bluegreen"><a href="javascript:updateSchedule()"><fmt:message key="aimir.update"/></a></em>
                    <em class="am_button"><a href="javascript:setModemScheduleReload();"><fmt:message key="aimir.cancel"/></a></em>
                </div>
                <!-- DIV INSIDE (E) -->


            </li>
        </ul>
    </div>
    <!-- search-default (E) -->

</li></ul>
</div> <!-- 1. Pulse Type (E) -->


<!-- 2. Energy Type (S) -->
<div id="energyModemSchedule" class="blueline" style="height:490px;">

    <!-- Metering Interval (S) -->
    <ul class="width">

        <div><label class="check">Metering Interval</label></div>

        <table class="wfree margin-t10px">
            <tr>
                <th><label class="blue12pt">Hour </label></th>
                <td><input id="intervalHour" type="text" style="width:35px;"/></td>
                <th><label class="blue12pt"> Minute </label></th>
                <td><input id="intervalMinute" type="text" style="width:35px;"/></td>
                <th><label class="blue12pt"> Second </label></th>
                <td><input id="intervalSecond" type="text" style="width:35px;"/></td>
                <td><label id="intervalComment" class="red11pt"> </label></td>
            </tr>
        </table>
        <div class="margin-t2px margin-b3px">
            <label id="intervalUpdateResult" class="bluebold12pt" >Update Result : </label>
            <br>
            <label class="blue12pt">* Read LP data at specific period. (Usually every 60 minutes.)</label>
        </div>
        <div class="headspace" style="width:100%;">
            <em class="am_button"><a href="javascript:getMtrIntervalAction();"><fmt:message key="aimir.get"/></a></em>
            <em class="btn_bluegreen"><a href="javascript:setMtrIntervalAction()"><fmt:message key="aimir.update"/></a></em>
        </div>

    </ul>
    <br>
    <!-- Retry Count (S) -->
    <ul class="width">

        <div><label class="check">Retry Count</label></div>
        <table class="wfree margin-t10px">
            <tr>
                <th><label class="blue12pt">Count </label></th>
                <td><input id="retryCountInput" type="text" value="0" ></td>
                <td><label id="retryComment" class="red11pt"></label></td>
            </tr>
        </table>
        <div class="margin-t2px margin-b3px"><label class="blue12pt">* The number of re-upload.(Default 3 times)</label></div>
        <div class="headspace" style="width:100%;">
            <em class="am_button"><a href="javascript:getRetryCountAction();"><fmt:message key="aimir.get"/></a></em>
            <em class="btn_bluegreen"><a href="javascript:setRetryCountAction()"><fmt:message key="aimir.update"/></a></em>
        </div>
    </ul>


</div> <!-- 2. Energy Type (E) -->
