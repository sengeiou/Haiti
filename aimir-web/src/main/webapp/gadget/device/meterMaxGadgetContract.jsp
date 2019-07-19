<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" charset="utf-8">



function updateContractMeterId(){

	var flexMeterModifyContract = getFlexObject('meterModifyContract');

	flexMeterModifyContract.updateContract(meterId);
	


}

function meterModifyContractGrid(){

    var fmtMessage = new Array();
    fmtMessage[0] = "<fmt:message key="aimir.contractNumber"/>";
    fmtMessage[1] = "<fmt:message key="aimir.meterid"/>";
    fmtMessage[2] = "<fmt:message key="aimir.customerid"/>";
    fmtMessage[3] = "<fmt:message key="aimir.customername"/>";
    fmtMessage[4] = "<fmt:message key="aimir.customeraddress"/>";
    
    var dataFild = new Array();
    dataFild[0] = "CONTRACT_NUMBER";    
    dataFild[1] = "MDS_ID";
    dataFild[2] = "CUSTOMERNO";
    dataFild[3] = "NAME";
    dataFild[4] = "LOCNAME";

    var gridAlign = new Array();
    gridAlign[0] = "left";
    gridAlign[1] = "center";
    gridAlign[2] = "left";
    gridAlign[4] = "center";
    gridAlign[5] = "left";
    
    var gridWidth = new Array();
    gridWidth[0] = "500";
    gridWidth[1] = "1000";
    gridWidth[2] = "1500";
    gridWidth[3] = "1000";
    gridWidth[4] = "1000";
    
    var dataGrid = new Array();
    dataGrid[0] = fmtMessage;
    dataGrid[1] = dataFild;
    dataGrid[2] = gridAlign;
    dataGrid[3] = gridWidth;

    return dataGrid;

}

function getFmtMessageModifyContract(){
    var fmtMessage = new Array();;

    fmtMessage[0] = "<fmt:message key="aimir.contract.select.message"/>";         // 계약은 하나만 선택이 가능합니다.
    
    return fmtMessage;
}

</script>

<div class="headspace"><label class="check"><fmt:message key="aimir.equipment"/> <fmt:message key="aimir.contractInfo"/> <fmt:message key="aimir.update"/></label></div>

<div class="box-bluegradation-meter">
<ul><li class="box-bluegradation-meter-padding">

    
            <div class="flexlist">
                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="200px" id="meterModifyContractEx">
                    <param name="movie" value="${ctx}/flexapp/swf/meterModifyContract.swf" />
                    <param name="wmode" value="opaque" />
                    <!--[if !IE]>-->
                    <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterModifyContract.swf" width="100%" height="200px" id="meterModifyContractOt">
                    <param name="wmode" value="opaque" />
                    <!--<![endif]-->
                    <p>Alternative content</p>
                    <!--[if !IE]>-->
                    </object>
                    <!--<![endif]-->
                </object>
            </div>    


</li></ul>
</div>


    <div id="btn" class="meter-info-btn2">

        <!-- 변경 -->
        <div id="meterDefaultInfoInsertButton" style="display:block;" >
            <ul><li><a href="javaScript:updateContractMeterId();" class="on"><fmt:message key="aimir.update"/></a></li></ul>
            <ul><li><a href="javaScript:getMeter();" class="on"><fmt:message key="aimir.equipstatistics"/></a></li></ul>
        </div>
    </div>

