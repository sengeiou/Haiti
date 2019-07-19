<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<script type="text/javascript">

	var operatorId;

	function pageModify() {
		operatorId = $("#operatorId").val();
		$("#operatorInfo").load("${ctx}/gadget/system/membership/membershipModify.do?operatorId=" + operatorId);
	}

	var deleteMembership = function() {

        var params = {
                "operatorId" : $("#operatorId").val()
        };

        $.getJSON('${ctx}/gadget/system/membership/deleteMembership.do', params,
            function(result) {

        		if (result.status == true) {

            		//alert("<fmt:message key='aimir.hems.inform.leaveEnd'/>");
                    Ext.MessageBox.show({
                        title:'<fmt:message key='aimir.hems.label.deleteMember'/>',
                        msg: '<fmt:message key='aimir.hems.inform.leaveEnd'/>',
                        buttons: Ext.MessageBox.OK,
                        minWidth:300,
                        fn: function() {parent.location.href = "${ctx}/customer/login.jsp";},
                        icon: Ext.MessageBox.INFO
                    }); 
        		} else {

            		//alert("<fmt:message key='aimir.hems.error.leave'/>\n<fmt:message key='aimir.hems.systemError'/>");
                    Ext.MessageBox.show({
                        title:'<fmt:message key='aimir.hems.label.deleteMember'/>',
                        msg: '<fmt:message key='aimir.hems.error.leave'/>\n<fmt:message key='aimir.hems.systemError'/>',
                        buttons: Ext.MessageBox.OK,
                        minWidth:300,
                        fn: function() {},
                        icon: Ext.MessageBox.WARNING
                    });             		
        		}
            	return;
    		}
    	);
	};

</script>
<div class="margin_t30">
	<div id="wrap">
		<div class="bigbox_blue">
			<div class="top_roundboxbg_blue" >
				<!--top-->
				<div class="top_roundbox_blue">
					<div class="top_contentbox_blue">
					
						<!-- tab -->
						<div class="tab_blue padding_t30">
						  	<ul>
						  		<li><a href="javascript:pageModify();"><fmt:message key='aimir.hems.label.modifyMember'/></a></li>
						  		<li><a href="#" class="current"><fmt:message key='aimir.hems.label.deleteMember'/></a></li>
						  	</ul>
						</div>
						<!--// tab -->
						
				 	</div>
				</div>
		     	<!--// top-->
	        	
				<!--Content-->
				<div class="roundbox_blue">
					<div class="contents_blue">
				        <div class="margin_10">
				        
				           	<!--confirm-->
				           	<div class="margin_t30">
				               	<label class="bold_16px"><span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.deleteMember'/></label>
				           		<div class="margin_t10 blue_box">
				               		<div class="comment_box">
				                    	<ul class="text_gray7"><fmt:message key='aimir.hems.inform.deleteText'/></ul>
				                	</div>
				               	</div>
				           	</div>
				           	<!--//confirm-->
				          
				           	<!--button-->
				           	<div class="margin_t30 margin_b50 align_right">
								<em class="hm_button_bold"><a href="javascript:deleteMembership();"><fmt:message key='aimir.hems.label.deleteMember'/></a></em>
						   	</div>
				           	<!--//button-->
				           
				        </div>
				    </div>
				</div>
				<!--//Content-->
			</div>
		</div>
	</div>
</div>
<input id="operatorId" name="operatorId" type="hidden" value="${operator.id}">