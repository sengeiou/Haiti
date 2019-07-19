<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<form:form id="supplierDefault" modelAttribute="supplier">

<div class="updatesupplier-iehack">

	<div class="headspace-enter">
		<label class="check"><fmt:message key="aimir.supplier.info.modify"/></label>
	</div>

			<table class="customer_detail">
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.supplier.name"/></th>
					<td><input type="text" name="name" value="${supplier.name}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.description"/></th>
					<td><input type="text" name="descr" value="${supplier.descr}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.tel.no"/></th>
					<td><input type="text" name="telno" value="${supplier.telno}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.country"/></th>
					<td><select name="country.id">
							<c:forEach var="country" items="${countryList}">
								<option value="${country.id}"
									<c:if test='${supplier.country.id == country.id}'>selected="selected"</c:if>>
									${country.name}
								</option>
							</c:forEach>
						</select></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.supplier.address"/></th>
					<td><input type="text" name="address" value="${supplier.address}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.language"/></th>
					<td><select name="lang.id">
							<c:forEach var="lang" items="${languageList}">
								<option value="${lang.id}"
								<c:if test="${supplier.lang.id == lang.id}">selected="selected"</c:if>>
								${lang.name}
							</option>
							</c:forEach>
						</select>
					</td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.timezone"/></th>
					<td><select name="timezone.id">
							<c:forEach var="timezone" items="${timeZoneList}">
								<option value="${timezone.id}"
								<c:if test="${supplier.timezone.id == timezone.id}">selected="selected"</c:if>>
								${timezone.name}
							</option>
							</c:forEach>
						</select>
					</td></tr>
				<tr><th class="darkgraybold11pt">Date Pattern</th>
					<td><select name='sysDatePattern'>
						<option value="No Option" title="No Option"
							<c:if test="${supplier.sysDatePattern == 'No Option'}">selected="selected"</c:if>>
							No Option
						</option>
						<option value="DD.MM.YY HH:MM:SS" title="ex) 30.03.16 22:12:45"
							<c:if test="${supplier.sysDatePattern == 'DD.MM.YY HH:MM:SS'}">selected="selected"</c:if>>
							DD.MM.YY HH:MM:SS
						</option>
						<option value="DD/MM/YY HH:MM:SS" title="ex) 30/03/16 22:12:45"
							<c:if test="${supplier.sysDatePattern == 'DD/MM/YY HH:MM:SS'}">selected="selected"</c:if>>
							DD/MM/YY HH:MM:SS
						</option>
						<option value="DD/MM/YY HH.MM.SS" title="ex) 30/03/16 22.12.45"
							<c:if test="${supplier.sysDatePattern == 'DD/MM/YY HH.MM.SS'}">selected="selected"</c:if>>
							DD/MM/YY HH.MM.SS
						</option>
						<option value="DD/MM/YY HH:MM:SS AM/PM" title="ex) 30/03/16 10:12:45 PM"
							<c:if test="${supplier.sysDatePattern == 'DD/MM/YY HH:MM:SS AM/PM'}">selected="selected"</c:if>>
							DD/MM/YY HH:MM:SS AM/PM
						</option>
						<option value="MM/DD/YY HH:MM:SS AM/PM" title="ex) 3/30/16 10:12:45 PM"
							<c:if test="${supplier.sysDatePattern == 'M/DD/YY HH:MM:SS AM/PM'}">selected="selected"</c:if>>
							MM/DD/YY HH:MM:SS AM/PM
						</option>
						<option value="YY/MM/DD HH:MM:SS" title="ex) 16/03/30 22:12:45"
							<c:if test="${supplier.sysDatePattern == 'YY/MM/DD HH:MM:SS'}">selected="selected"</c:if>>
							YY/MM/DD HH:MM:SS
						</option>
						<option value="YY-MM-DD HH:MM:SS" title="ex) 16-3-30 22:12:45"
							<c:if test="${supplier.sysDatePattern == 'YY-MM-DD HH:MM:SS'}">selected="selected"</c:if>>
							YY-MM-DD HH:MM:SS
						</option>
						</select>
					</td></tr>
				</td>
				</tr>
				
				<!-- <input type="checkbox" name="checkBtn" href='javascript:updateDefault()' onclick="javascript:fn_checkBox()" style="width:15%; vertical-align:middle;"></th> -->	
									
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.md.pattern"/></th>
					<td><input type="text" name="md.pattern" value="${supplier.md.pattern}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.cd.pattern"/></th>
					<td><input type="text" name="cd.pattern" value="${supplier.cd.pattern}"/></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.md.groupingSeperator"/></th>
					<td><select name='md.groupingSeperator' style='width:120px;'>
							<option value=" "
								<c:if test="${supplier.md.groupingSeperator == ' '}">selected="selected"</c:if>>&nbsp;</option>
							<option value=","
								<c:if test="${supplier.md.groupingSeperator == ','}">selected="selected"</c:if>>,</option>
							<option value="."
								<c:if test="${supplier.md.groupingSeperator == '.'}">selected="selected"</c:if>>.</option>
						</select></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.cd.groupingSeperator"/></th>
					<td><select name='cd.groupingSeperator' style='width:120px;'>
							<option value=" "
								<c:if test="${supplier.cd.groupingSeperator == ' '}">selected="selected"</c:if>>&nbsp;</option>
							<option value=","
								<c:if test="${supplier.cd.groupingSeperator == ','}">selected="selected"</c:if>>,</option>
							<option value="."
								<c:if test="${supplier.cd.groupingSeperator == '.'}">selected="selected"</c:if>>.</option>
						</select></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.md.decimalSeperator"/></th>
					<td><select name='md.decimalSeperator' style='width:120px;'>
							<option value=" "
								<c:if test="${supplier.md.decimalSeperator == ' '}">selected="selected"</c:if>>&nbsp;</option>
							<option value=","
								<c:if test="${supplier.md.decimalSeperator == ','}">selected="selected"</c:if>>,</option>
							<option value="."
								<c:if test="${supplier.md.decimalSeperator == '.'}">selected="selected"</c:if>>.</option>
						</select></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.cd.decimalSeperator"/></th>
					<td><select name='cd.decimalSeperator' style='width:120px;'>
							<option value=" "
								<c:if test="${supplier.cd.decimalSeperator == ' '}">selected="selected"</c:if>>&nbsp;</option>
							<option value=","
								<c:if test="${supplier.cd.decimalSeperator == ','}">selected="selected"</c:if>>,</option>
							<option value="."
								<c:if test="${supplier.cd.decimalSeperator == '.'}">selected="selected"</c:if>>.</option>
						</select></td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.md.round"/></th>
					<td><select name='md.round' style='width:120px;'>
							<option value="c"
								<c:if test="${supplier.md.round == 'c'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.ceil'/>
							</option>
							<option value="f"
								<c:if test="${supplier.md.round == 'f'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.down'/>
							</option>
							<option value="r"
								<c:if test="${supplier.md.round == 'r'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.half'/>
							</option>
						</select>
					</td></tr>
				<tr><th class="darkgraybold11pt"><fmt:message key="aimir.cd.round"/></th>
					<td><select name='cd.round' style='width:120px;'>
							<option value="c"
								<c:if test="${supplier.cd.round == 'c'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.ceil'/>
							</option>
							<option value="f"
								<c:if test="${supplier.cd.round == 'f'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.down'/>
							</option>
							<option value="r"
								<c:if test="${supplier.cd.round == 'r'}">selected="selected"</c:if>>
								<fmt:message key='aimir.supplier.round.half'/>
							</option>
						</select>
					</td></tr>
			</table>


                <div class="btn_right_bottom">
					<span id="btn">
						<ul><li style="margin:0;"><a href="javascript:submitDefault('update')" class="on"><fmt:message key='aimir.ok'/></a></li></ul>
					</span>
					<span id="btn">
						<ul><li style="margin:0;"><a href="javascript:getSupplier()"><fmt:message key='aimir.cancel'/></a></li></ul>
					</span>
				</div>
</div>

</form:form>