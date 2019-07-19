<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<style type="text/css">
html{overflow:auto !important}
</style>
<title><fmt:message key='aimir.hems.title.registration'/></title>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" charset="utf-8">

    $(document).ready(function() {

 //       if ( "${param.checked}".length <= 0 || "${param.name}".length <= 0 || "${param.number}".length <= 0) {
 //           
//            home();
 //       } else {

            $("#wrap").show();
            $("#email3").selectbox();

            addContract(1);

            $("#id").focus();
 //       }
    });

    var idChecked = false;
     
    var changeId = function() {

        idChecked = false;
    };

    var checkId = function() {

        if ( $("#id").val().length <= 0 ) {

            //alert("<fmt:message key='aimir.alert.inputid'/>");

            //$("#id").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.alert.inputid'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("id").focus();},
                icon: Ext.MessageBox.WARNING
            });
            return;
        } else {

            var params = {
                "id" : $("#id").val()
            };

            $.getJSON('${ctx}/gadget/system/membership/checkId.do', params,
                function(result) {

                    if (result.status == true) {

                        //alert("<fmt:message key='aimir.abailableId'/>");
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.abailableId'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() {},
                            icon: Ext.MessageBox.INFO
                        });

                        idChecked = true;
                    } else {

                        //alert("<fmt:message key='aimir.exist'/>");
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.exist'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() {Ext.get("id").focus();},
                            icon: Ext.MessageBox.WARNING
                        });

                        idChecked = false;
                    }

                    return;
                }
            );
        }
    };

/*
    var changeMail = function() {

        if ( $("#email3 option:selected").val() == "0" ) {

            $("#email2").removeAttr("readonly"); 
            $("#email2").val("");
            $("#email2").focus();
        } else {

            $("#email2").attr("readonly", true);
            $("#email2").val($("#email3 option:selected").text());
        }
    };
*/    
    var registration = function() {

        if ( mustCheck() ) {

            $("#lastLine").val(lastLine);

            $.post("${ctx}/gadget/system/membership/insertMembership.do",
                $("#third").serialize(),
                function(result, status) {

                    if (result.status == true) {
                        
                        //alert("<fmt:message key='aimir.hems.alert.signUp'/>");
                        Ext.MessageBox.buttonText.ok = "<fmt:message key='aimir.login.login'/>";
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.hems.alert.signUp'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() { 
                                $("#third").attr("action", "${ctx}/customer/login.jsp");
                                $("#third").submit();},
                            icon: Ext.MessageBox.INFO
                        });
                        

                    } else {
                        
                        //alert("<fmt:message key='aimir.hems.error.signUp'/>");
                        Ext.MessageBox.show({
                            title:"<fmt:message key='aimir.hems.label.registMember'/>",
                            msg: "<fmt:message key='aimir.hems.error.signUp'/>",
                            buttons: Ext.MessageBox.OK,
                            minWidth:300,
                            fn: function() {},
                            icon: Ext.MessageBox.ERROR
                        });
                    }
                },
                "json"
            );
        } 
    };

    var mustCheck = function() {

        if ( $("#id").val().length <= 0 ) {
            
            //alert("<fmt:message key='aimir.alert.inputid'/>");

            //$("#id").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.alert.inputid'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("id").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( idChecked == false ) {

            //alert("<fmt:message key='aimir.chkDuplicateId'/>");

            //$("#id").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.chkDuplicateId'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("id").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( $("#pw").val().length <= 0 ) {
            
            //alert("<fmt:message key='aimir.hems.alert.inputPassword'/>");

            //$("#pw").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPassword'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("pw").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( $("#pwConfirm").val().length <= 0 ) {
            
            //alert("<fmt:message key='aimir.hems.alert.inputPasswordConfirm'/>");

            //$("#pwConfirm").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputPasswordConfirm'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("pwConfirm").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( $("#pw").val() != $("#pwConfirm").val() ) {

            //alert("<fmt:message key='aimir.hems.alert.notMatchPassword'/>");

            //$("#pwConfirm").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.hems.alert.notMatchPassword'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("pwConfirm").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        for (var i = 1; i < lastLine; i++) {
             
            if ( $("#checked" + i).val() == "Y" ) {

                break;
            } else if ( i == lastLine - 1 ) {
                    
                //alert("<fmt:message key='aimir.hems.alert.checkContractNo'/>");

                //$("#contractNo1").focus();
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.hems.label.registMember'/>",
                    msg: "<fmt:message key='aimir.hems.alert.checkContractNo'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {Ext.get("friendlyName1").focus();},
                    icon: Ext.MessageBox.WARNING
                });

                return false;
            }
        }
        
        if ( $("#email1").val().length <= 0 ) {
            
            //alert("<fmt:message key='aimir.inputemailaddress'/>");

            //$("#email1").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.inputemailaddress'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email1").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( $("#email2").val().length <= 0 ) {
            
            //alert("<fmt:message key='aimir.inputemailaddress'/>");

            //$("#email2").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.inputemailaddress'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email2").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        if ( $("#email2").val().indexOf(".") == -1 ) {

            //alert("<fmt:message key='aimir.emailwarn1'/>");     // '.'이 Email주소에서 누락되었습니다.

            //$("#email2").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.emailwarn1'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email2").focus();},
                icon: Ext.MessageBox.WARNING
            });

            return false;
        } else if ( $("#email2").val().indexOf(".") - $("#email2").val().indexOf("@") == 1 ) {

            //alert("<fmt:message key='aimir.emailwarn2'/>");     // '@' 다음에 바로 '.'이 올수 없습니다.

            //$("#email2").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.emailwarn2'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email2").focus();},
                icon: Ext.MessageBox.WARNING
            });

            return false;
        } else if ( $("#email2").val().charAt( $("#email2").val().length - 1 ) == ".") {

            //alert("<fmt:message key='aimir.emailwarn3'/>");     // '.'은 Email주소 끝에 올 수 없습니다.

            //$("#email2").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.emailwarn3'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email2").focus();},
                icon: Ext.MessageBox.WARNING
            });

            return false;
        } else if ( $("#email2").val().charAt( $("#email2").val().length - 1 ) == "@") {

            //alert("<fmt:message key='aimir.emailwarn4'/>");     // '@'은 Email주소 끝에 올 수 없습니다.

            //$("#email2").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.emailwarn4'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("email2").focus();},
                icon: Ext.MessageBox.WARNING
            });

            return false;
        }
        
        //email_1 체크
        for( i = 0 ; i < $("#email1").val().length ; i++) {
            
            var c = $("#email1").val().charAt(i);

            if ( !(c >= "0" && c <= "9") && !(c >= "a" && c <= "z") && !(c >= "A" && c <= "Z") && (c != "-") && (c != "_") ) {
            
                //alert("<fmt:message key='aimir.emailwarn5'/>");     // E-mail은 영어, 숫자, '-', '_' 만 가능 합니다.

                //$("#email1").focus();
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.hems.label.registMember'/>",
                    msg: "<fmt:message key='aimir.emailwarn5'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {Ext.get("email1").focus();},
                    icon: Ext.MessageBox.WARNING
                });

                return false;
            }
        }
        
        //email_2 체크
        for( i = 0 ; i < $("#email2").val().length ; i++) {
            
            var f = $("#email2").val().charAt(i);

            if ( !(f >= "0" && f <= "9") && !(f >= "a" && f <= "z") && !(f >= "A" && f <= "Z") && (f != ".") && (f != "-") && (f != "_") ) {
            
                //alert("<fmt:message key='aimir.emailwarn5'/>");     // E-mail은 영어, 숫자, '-', '_' 만 가능 합니다.

                //$("#email2").focus();
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.hems.label.registMember'/>",
                    msg: "<fmt:message key='aimir.emailwarn5'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {Ext.get("email2").focus();},
                    icon: Ext.MessageBox.WARNING
                });

                return false;
            }
        }

        if ( $("input:radio[name='smsYn']:checked").val() == "1" && $("#mobileNumber").val().length <= 0 ) {

            //alert("<fmt:message key='aimir.hems.alert.inputCelluarphone'/>");

            //$("#mobileNumber").focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.hems.alert.inputCelluarphone'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("mobileNumber").focus();},
                icon: Ext.MessageBox.WARNING
            });
            
            return false;
        } 

        return true;
    };

    var home = function() {
        
        $("#third").attr("action", "${ctx}/customer/login.jsp");
        $("#third").submit();
    };

    var intValue = function(input) {
        
        if (isNaN($("#" + input).val())) {
            
            //alert("<fmt:message key='aimir.alert.onlydigit'/>");
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.alert.onlydigit'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {},
                icon: Ext.MessageBox.WARNING
            });
            
            $("#" + input).val("");

            $("#" + input).focus();
        } 
    };
    
    var checked = function(name, value) {

        $("input:radio[name=" + name + "]").filter("input:radio[value=" + value + "]").attr("checked", "checked");
    };

    var goZip = function() {

        window.open ('${ctx}/gadget/system/membership/zipCodeFind.jsp', 'zipCodeFind', 
            'toolbar=0, location=0, status=0, menubar=0, scrollbars=0, resizable=0, top=200, left=200, width=365, height=380');
    };

    var lastLine;

    var addContract = function(lineNum) {

        var beforeLine = lineNum - 1;

        lastLine = lineNum++;

        if (lastLine != 1) {
            
            if ($("#checked" + beforeLine).val() == "N" ) {

                //alert("<fmt:message key='aimir.hems.alert.addCheckContract'/>");
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.hems.label.registMember'/>",
                    msg: "<fmt:message key='aimir.hems.alert.addCheckContract'/>",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {},
                    icon: Ext.MessageBox.WARNING
                });

                return;
            }

            $("#add" + beforeLine).hide();
        }
        
        contractHtml = "<div class='line_add' id='contractDiv" + lastLine + "'>"
                     + "<input type='hidden' name='checked" + lastLine + "' id='checked" + lastLine + "' value='N' />" 
                     + "<span class='padding_r10'><fmt:message key='aimir.hems.label.contractFriendlyName'/></span>"
                     + "<span class='padding_r10'><input type='text' name='friendlyName" + lastLine + "' id='friendlyName" + lastLine + "' onchange='javascript:changeContract(" + lastLine + ");'  /></span>"  
					 + "<span class='padding_r10'><fmt:message key='aimir.supplier'/></span>"
                     + "<span class='padding_r10'>"
                     + "<select name='supplier" + lastLine + "' id='supplier" + lastLine + "' style='width:110px' onchange='javascript:changeContract(" + lastLine + ");' >"
                     + "</select>"
                     + "</span>"
                     + "<span class='padding_r10'><fmt:message key='aimir.contractNumber'/></span>"
                     + "<span><input type='text' name='contractNo" + lastLine + "' id='contractNo" + lastLine + "' onchange='javascript:changeContract(" + lastLine + ");' /></span>"
                     + "<span class='hm_button margin_l3' id='check" + lastLine + "' ><a href='javascript:checkContract(" + lastLine + ");'><fmt:message key='aimir.hems.label.check'/></a></span>"
                     + "<span class='hm_button margin_l3' id='close" + lastLine + "' ><a href='javascript:closeContract(" + lastLine + ");'><fmt:message key='aimir.hems.label.close'/></a></span>"
                     + "<span class='hm_button margin_l3' id='open" + lastLine + "' ><a href='javascript:openContract(" + lastLine + ");'><fmt:message key='aimir.hems.label.open'/></a></span>"
                     + "<span class='hm_button margin_l3' id='add" + lastLine + "' ><a href='javascript:addContract(" + lineNum + ");'><fmt:message key='aimir.add'/></a></span>"
                     + "</div>";

        $(contractHtml).appendTo("#contractTd");

        $("#open" + lastLine).hide();
        $("#close" + lastLine).hide();
        $("#add" + lastLine).hide();

        getSupplierList(lastLine);

        //$("#contractNo" + lastLine).focus();
        $("#friendlyName" + lastLine).focus();

        lastLine = lineNum;
    };

    var getSupplierList = function (value) {

        $.getJSON('${ctx}/gadget/system/membership/getSupplierList.do'
            , function (returnData){
                $("#supplier" + value).pureSelect(returnData.supplierList);
                $("#supplier" + value).selectbox();
        });
    };

    var checkContract = function (value) {

        if ($("#friendlyName" + value).val().length <= 0) {

            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "명칭을 입력해 주세요.",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("friendlyName" + value).focus();},
                icon: Ext.MessageBox.WARNING
            });
            return;
        }
      
        if ($("#contractNo" + value).val().length <= 0) {

            //alert("<fmt:message key='aimir.enterContractNo'/>");

            //$("#contractNo" + value).focus();
            Ext.MessageBox.show({
                title:"<fmt:message key='aimir.hems.label.registMember'/>",
                msg: "<fmt:message key='aimir.enterContractNo'/>",
                buttons: Ext.MessageBox.OK,
                minWidth:300,
                fn: function() {Ext.get("contractNo" + value).focus();},
                icon: Ext.MessageBox.WARNING
            });
            return;
        }

        for (var i = 1; i <= lastLine; i++) {

            if (i == value) {
                continue;
            } else if($("#contractNo" + value).val() == $("#contractNo" + i).val()
            	&& $("#supplier" + value).val() == $("#supplier" + i).val()) {
            	
	            Ext.MessageBox.show({
	                title:"<fmt:message key='aimir.hems.label.registMember'/>",
	                msg: "<fmt:message key='aimir.hems.chkDuplicateContractNo'/>",
	                buttons: Ext.MessageBox.OK,
	                minWidth:300,
	                fn: function() {Ext.get("contractNo" + value).focus();},
	                icon: Ext.MessageBox.WARNING
	            });
	            
	            return;
            } else if($("#friendlyName" + value).val() == $("#friendlyName" + i).val()) {
                Ext.MessageBox.show({
                    title:"<fmt:message key='aimir.hems.label.registMember'/>",
                    msg: "중복된 명칭은 입력 불가능합니다.",
                    buttons: Ext.MessageBox.OK,
                    minWidth:300,
                    fn: function() {Ext.get("friendlyName" + value).focus();},
                    icon: Ext.MessageBox.WARNING
                });

                return;              
            }
        }

        var params = {
                "supplier" : $("#supplier" + value).val(),
                "contractNo" : $("#contractNo" + value).val()
            };

        $.getJSON('${ctx}/gadget/system/membership/checkContract.do', params
            , function (result){
                if (result.status == true) {

                    //alert("<fmt:message key='aimir.hems.alert.okContract'/>");
                    Ext.MessageBox.show({
                        title:"<fmt:message key='aimir.hems.label.registMember'/>",
                        msg: "<fmt:message key='aimir.hems.alert.okContract'/>",
                        buttons: Ext.MessageBox.OK,
                        minWidth:300,
                        fn: function() {
                            $("#close" + value).show();
                            $("#check" + value).hide();
                            if (lastLine == (value + 1)) {
                                
                                $("#add" + value).show();
                            }
                            $("#checked" + value).val("Y");},
                        icon: Ext.MessageBox.INFO
                    });


                } else {

                    //alert("<fmt:message key='aimir.hems.alert.notExistContractNo'/>");
                    Ext.MessageBox.show({
                        title:"<fmt:message key='aimir.hems.label.registMember'/>",
                        msg: "<fmt:message key='aimir.hems.alert.notExistContractNo'/>",
                        buttons: Ext.MessageBox.OK,
                        minWidth:300,
                        fn: function() {Ext.get("contractNo" + value).focus();},
                        icon: Ext.MessageBox.WARNING
                    });
                }
        });
    };

    var closeContract = function (value) {

        //alert("<fmt:message key='aimir.hems.alert.okClose'/>");
        Ext.MessageBox.show({
            title:"<fmt:message key='aimir.hems.label.registMember'/>",
            msg: "<fmt:message key='aimir.hems.alert.okClose'/>",
            buttons: Ext.MessageBox.OK,
            minWidth:300,
            fn: function() {
                
                if (lastLine == (value + 1)) {
                    
                	addContract(lastLine);
                	
	                $("#close" + value).hide();
	                $("#open" + value).show();
	                $("#checked" + value).val("N");
	                $("#contractDiv" + value).hide();
                }
            },
            icon: Ext.MessageBox.INFO
        });
       

    };

    var openContract = function (value) {
        
        //alert("<fmt:message key='aimir.hems.alert.okOpen'/>");
        Ext.MessageBox.show({
            title:"<fmt:message key='aimir.hems.label.registMember'/>",
            msg: "<fmt:message key='aimir.hems.alert.okOpen'/>",
            buttons: Ext.MessageBox.OK,
            minWidth:300,
            fn: function() {        
                $("#close" + value).show();
                $("#open" + value).hide();
                $("#checked" + value).val("Y");},
            icon: Ext.MessageBox.INFO
        });
        

    };

    var changeContract = function (value) {

        $("#check" + value).show();
        $("#open" + value).hide();
        $("#close" + value).hide();
        $("#add" + value).hide();

        $("#checked" + value).val("N");
    };
    
</script>
</head>

<body>
<form name="third" id="third" method="post" >
    <input type="hidden" name="checked" id="checked" value="${param.checked}"></input>
    <input type="hidden" name="name" id="name" value="${param.name}"></input>
    <input type="hidden" name="number" id="number" value="${param.number}"></input>
    <input type="hidden" name="lastLine" id="lastLine" value=""></input>
    <div id="wrap" style="display:none" >
        <div class="overflow_hidden">
            <a href="javascript:home();"><span class="logo_aimir"></span></a>
        </div>
        <div class="bigbox_black">
            <div class="top_roundboxbg" >
                <!--top-->
                <div class="top_roundbox">
                    <div class="top_contentbox">
                    
                        <!-- tab -->
                        <div class="hm_tab padding_t30">
                            <ul>
                                <li class="current"><fmt:message key='aimir.hems.label.registMember'/></li>
                            </ul>
                        </div>
                        <!--// tab -->
                        
                    </div>
                </div>
                <!--// top-->
                
                <!--Content-->
                <div class="roundbox">
                    <div class="contents">
                        <div class="margin_10">
                        
                            <!--step-->
                           <div class="overflow_hidden step">
                             <span>01. <fmt:message key='aimir.hems.label.usedMemberText'/></span>
                             <span class="icon_arrow"></span>
                             <span>02. <fmt:message key='aimir.hems.label.checkMemberName'/></span>
                             <span class="icon_arrow"></span>
                             <span class="on">03. <fmt:message key='aimir.hems.label.insertMemberInf'/></span>
                           </div>
                           <!--//step-->
                           
                           <!--confirm-->
                           <div class="margin_t30">
                               <label class="bold_16px"><span class="icon_title"></span><fmt:message key='aimir.hems.label.insertMemberInf'/></label>
                               <div>
                                <div class="align_right text_gray7"><fmt:message key='aimir.hems.inform.requiredField'/></div>                               
                                    <table class="member_join">
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.id'/></th>
                                            <td>
                                                <span class="margin_r3"><input type="text" name="id" id="id" style="IME-MODE:disabled" onchange="javascript:changeId();" /></span>
                                                <span class="hm_button"><a href="javascript:checkId();"><fmt:message key='aimir.hems.label.checkId'/></a></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.password'/></th>
                                            <td><input type="password" name="pw" id="pw" /></td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.confirmpassword'/></th>
                                            <td><input type="password" name="pwConfirm" id="pwConfirm" /></td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.contractNumber'/></th>
                                            <!-- <td id="contractTd" > -->
                                            <td id="contractTd">
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.hems.label.name'/></th>
                                            <td>${param.name}</td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.hems.label.pucNumber'/></th>
                                            <td>${param.number1}*******</td>
                                        </tr>
                                        <tr>
                                            <th><em class="icon_star">&nbsp;</em><fmt:message key='aimir.email'/></th>
                                            <td>
                                                <span><input type="text" name="email1" id="email1" /></span>
                                                <span class="text_side5"> @ </span>
                                                <span><input type="text" name="email2" id="email2" /></span>

                                                <!--                                                                                              
                                                <span><input type="text" name="email1" id="email1" style="IME-MODE:disabled" /></span>
                                                <span class="text_side5"> @ </span>
                                                <span><input type="text" name="email2" id="email2" style="IME-MODE:disabled" /></span>
                                                <span>
                                                    <select name="email3" id="email3" class="w_110px" onchange="javascript:changeMail()">
                                                        <option value="0"><fmt:message key='aimir.hems.label.directInput'/></option>
                                                        <option value="1">naver.com</option>
                                                        <option value="2">yahoo.co.kr</option>
                                                        <option value="3">hanmail.net</option>
                                                        <option value="4">nate.com</option>
                                                        <option value="5">korea.com</option>
                                                        <option value="6">hotmail.com</option>
                                                        <option value="7">freechal.com</option>
                                                        <option value="8">gmail.com</option>
                                                        <option value="9">paran.com</option>
                                                        <option value="10">chollian.net</option>
                                                        <option value="11">dreamwiz.com</option>
                                                        <option value="12">hananet.net</option>
                                                        <option value="13">hanmir.com</option>
                                                        <option value="14">hitel.net</option>
                                                        <option value="15">kornet.net</option>
                                                        <option value="16">lycos.co.kr</option>
                                                        <option value="17">hanafos.com</option>
                                                    </select>
                                                </span> 
                                                 -->
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.email'/><fmt:message key='aimir.operator.notificationSet'/></th>
                                            <td class="link">
                                                <a href="javascript:checked('emailYn', '1');" >
                                                    <span><input type="radio" name="emailYn" id="emailYn" class="radio" value="1" checked="checked" /></span>
                                                    <span class="margin_r20"><fmt:message key='aimir.allowReceive'/></span>
                                                </a>
                                                <a href="javascript:checked('emailYn', '0');" >
                                                    <span><input type="radio" name="emailYn" id="emailYn" class="radio" value="0" /></span>
                                                    <span><fmt:message key='aimir.blocked'/></span>
                                                </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.hems.label.zipCode'/></th>
                                            <td>
                                                <p class="overflow_hidden">
                                                    <span><input type="text" name="zipCode" id="zipCode" class="w_66px" maxlength="6" style="IME-MODE:disabled" onblur="javascript:intValue('zipCode');" /></span>
                                                    <!-- <span><input type="text" name="zipCode1" id="zipCode1" class="w_66px" maxlength="3" readonly="readonly" onclick="javascript:goZip();" /></span>
                                                    <span><input type="text" name="zipCode1" id="zipCode1" class="w_66px" maxlength="3" style="IME-MODE:disabled" onblur="javascript:intValue('zipCode1');" /></span>
                                                    <span class="text_side5"> - </span>
                                                    <span><input type="text" name="zipCode2" id="zipCode2" class="w_66px" maxlength="3" readonly="readonly" onclick="javascript:goZip();" /></span> 
                                                    <span><input type="text" name="zipCode2" id="zipCode2" class="w_66px" maxlength="3" style="IME-MODE:disabled" onblur="javascript:intValue('zipCode2');" /></span>
                                                    <span class="hm_button">
                                                    <a href="javascript:goZip();" > zip code</a>  
                                                    </span> -->
                                                    <span class="text_gray7"> <fmt:message key='aimir.hems.inform.insertNo-'/></span>
                                                </p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.address'/></th>
                                            <td>
                                                <p><input type="text" name="address1" id="address1" style="width:380px"/></p>
                                                <p><input type="text" name="address2" id="address2" style="width:380px"/></p>   
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.tel.no'/></th>
                                            <td>
                                                <span><input type="text" name="telNo" id="telNo" class="w_66px" style="width:100px; IME-MODE:disabled" maxlength="11" onblur="javascript:intValue('telNo');" /></span>
                                                <!-- <span><input type="text" name="telNo1" id="telNo1" class="w_66px" maxlength="3" style="IME-MODE:disabled" onblur="javascript:intValue('telNo1');" /></span>
                                                <span class="text_side5">- </span>
                                                <span><input type="text" name="telNo2" id="telNo2" class="w_66px" maxlength="4" style="IME-MODE:disabled" onblur="javascript:intValue('telNo2');" /></span>
                                                <span class="text_side5">- </span>
                                                <span><input type="text" name="telNo3" id="telNo3" class="w_66px" maxlength="4" style="IME-MODE:disabled" onblur="javascript:intValue('telNo3');" /></span> -->
                                                <span class="text_gray7"> <fmt:message key='aimir.hems.inform.insertNo-'/></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.celluarphone'/></th>
                                            <td>
                                                <span><input type="text" name="mobileNumber" id="mobileNumber" style="width:100px; IME-MODE:disabled" maxlength="11" onblur="javascript:intValue('mobileNumber');" /></span>
                                                <!-- <span><input type="text" name="mobileNumber1" id="mobileNumber1" class="w_66px" maxlength="3" style="IME-MODE:disabled" onblur="javascript:intValue('mobileNumber1');" /></span>
                                                <span class="text_side5">- </span>
                                                <span><input type="text" name="mobileNumber2" id="mobileNumber2" class="w_66px" maxlength="4" style="IME-MODE:disabled" onblur="javascript:intValue('mobileNumber2');" /></span>
                                                <span class="text_side5">- </span>
                                                <span><input type="text" name="mobileNumber3" id="mobileNumber3" class="w_66px" maxlength="4" style="IME-MODE:disabled" onblur="javascript:intValue('mobileNumber3');" /></span> -->
                                                <span class="text_gray7"> <fmt:message key='aimir.hems.inform.insertNo-'/></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><em class="margin_l13"></em><fmt:message key='aimir.hems.label.sms'/><fmt:message key='aimir.operator.notificationSet'/></th>
                                            <td class="link">
                                                <a href="javascript:checked('smsYn', '1');" >
                                                    <span><input type="radio" name="smsYn" id="smsYn" class="radio" value="1" checked="checked" /></span>
                                                    <span class="margin_r20"><fmt:message key='aimir.allowReceive'/></span>
                                                </a>
                                                <a href="javascript:checked('smsYn', '0');" >
                                                    <span><input type="radio" name="smsYn" id="smsYn" class="radio" value="0" /></span>
                                                    <span><fmt:message key='aimir.blocked'/></span>
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                <!--//confirm-->
                           
                                <!--button-->
                                <div class="margin_t30 align_right">
                                    <em class="hm_button_bold"><a href="javascript:registration();"><fmt:message key='aimir.hems.label.registMember'/></a></em>
                                    <em class="hm_button margin_l3"><a href="javascript:home();"><fmt:message key='aimir.hems.label.cancelRegist'/></a></em>
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
</form>
</body>
</html>