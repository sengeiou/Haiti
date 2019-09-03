<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<%
    String miniSupplierId = request.getParameter("supplierId") == null ? "" : request.getParameter("supplierId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<style type='text/css'>

    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }

    input.rate-value {
        width: 30px;
    }
    em.tax-update,
    em.commission-update {
        margin-left: 10px;
    }
    .taxRate-form,
    .commissionRate-form {
        margin-bottom: 30px;
    }
    .no-width {
        width: 0px;
        visibility: hidden;
    }
    .tou-description {
        vertical-align: top;
    }
        .tou-description ul {
            margin: 0px;
            padding: 0px;
        }
    .calendar-form {
        margin-right: 10px;
    }
        .calendar-form input.date.alt {
            width: 60px;
        }
    form select,
    form span ,
    form input{
      float: none;
      display: inline;
    }
    form input.selectbox {
        display: block;
    }
    .inline-div {
        display: inline;
    }
    #new-electric-tariff span,
    #new-electric-tariff input {
        display: inline-block;
    }
    .clear {
        display: none;
        clear: both;
    }
    
    #supplier-tab1 span, #supplier-tab1 a {
    	float: none; 
    }
    
    .x-tree-selected li, .x-tree-selected a, .x-tree-node-over li, .x-tree-node-over a{
    	background: #beebff;
    	border:1px solid #99defd; 
    	padding:0px 7px 1px 7px;		 
    }
    
    .x-grid3-cell { vertical-align: middle !important; }
    .x-grid3-hd-inner { text-align: center !important; font-weight: bold !important; }
   
   .grid-row-span .x-grid3-row {
    border-bottom: 0;
	}
	.grid-row-span .x-grid3-col {
	    border-bottom: 1px solid #ededed;
	}
	.grid-row-span .row-span {
	    border-bottom: 1px solid #fff;
	}
	.grid-row-span .row-span-first {
	    position: relative;
	}
	.grid-row-span .row-span-first .x-grid3-cell-inner {
	    position: absolute;
	}
	.grid-row-span .row-span-last {
	    border-bottom: 1px solid #ededed;
	} 
	.supplyModify{
		display:inline-block;
		text-align:center;
		width:100px;
	}
</style>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/jquery-ajaxQueue.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<script type="text/javascript">
        var supplierObj;
        var locationObj;
        var sId="${supplierId}";
        var locId;
        var sLoad = false;
        var locLoad = false;
        var count;
        // 요금관리 - Flex Object
        var grid1;
        var grid2;
        var grid3;
        // 요금관리 - 공급타입
        var supplyType;
        var filePath;
        var blnImport = false;

        // 전기 tariff 달력
        var electricDate;

        // _mgmt 탭 선택시에만 true.
        var selectedTab = false;
        // 수정 권한
        var editAuth = "${editAuth}";
        var calendarProp = {
            showOn: 'button',
            buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly: true
        };

        var SupplyTypeCon = {
            "Electricity": [
                'Electricity',
                '<fmt:message key="aimir.electricity"/>'
            ],
            "Gas": [
                'Gas',
                '<fmt:message key="aimir.gas"/>'
            ],
            "Water": [
                'Water',
                '<fmt:message key="aimir.water"/>'
            ],
            "Heat": [
                'Heat',
                '<fmt:message key="aimir.heatmeater"/>'
            ]
        };
        
        var supplyMin;
    	var supplyMax;
    	var condition1;
    	var condition2;
    	
    	var red = '#F31523'; 
        var orange = '#FC8F00'; 
        var gold = '#A99903';
    	var blue = '#006BF7';
    	var grey = '#969696';
    	var green = '#80E12A';
    	var pink = '#FF607F';
    	var setColorFrontTag = "<b style=\"color:";
        var setColorMiddleTag = ";\">"; 
        var setColorBackTag = "</b>";
        
        var licenceUse = " ";
        var totalLicenceCount = " ";
        var registeredLicenceCount = " ";
        var availableLicenceCount = " ";
        var tariffTypeId;
        
        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
        
        var getSupplyType = function (type) {
            var ret;
            $.each(SupplyTypeCon, function (index, element) {
                if ( $.inArray(type, element) > -1 ) {
                    ret = element[0];
                }
            })
            return ret;
        }
        
        function getEditAuth() {
            return editAuth;
        }

        function modifiedDateFormat(date, target) {
            var $this = target.input || target ;

            $.getJSON("${ctx}/common/convertLocalDate.do",
              {supplierId: supplierObj.id,
                dbDate: date},
              function(data) {
                $("." + $this.attr('name')).val(data.localDate);
              });
        }

        function initCalendar() {
            calendarProp.dateFormat = 'yymmdd';
            calendarProp.altFormat = '';
            var eleDate = new Date();
            var eleCalProp = $.extend(true, calendarProp);
            electricDate = $('#electricDiv input[name=date]').datepicker(eleCalProp);
            electricDate.datepicker('setDate', eleDate);
            electricDate.datepicker(
                'option',{onSelect:modifiedDateFormat});
            modifiedDateFormat($.format.date(eleDate,"yyyyMMdd"), $('#electricDiv input[name=date]'));
        }        
        
        var locationTreeObj;
        var locationTree;     
        var locationTreeModel;
		function getLocationTreeObj() { //Tree 구조 획득
        		//Controller에 정의되어 있음
                $.getJSON('${ctx}/gadget/system/supplier/getLocations.do', {supplierId:sId},
                function(json) {
                	//DB에서 Location 테이블의 항목을 불러옴
                    locationObj = json.locationlist;
                    locationTreeObj = locationObj;
                    //트리를 구성할 수 있는 형태의 문자열 생성
                    tStoreNew = makeTreePanJson(locationTreeObj);
                    getLocationTree();
            });                                       
        }
		
		function resetLocationTreeObj(){
			if(locationTree != null)
				//destroy시에 리스너가 동작하여 트리를 재생성함.
				locationTree.destroy();
		}
        
		function getLocationTree(){		
        	
/*         	locationTreeString = '';
        	for(i = 0; i < locationTreeObj.length; i++) {
        		locationTreeString += "{id:" + locationTreeObj[i].id + ", text: '"+locationTreeObj[i].name + "', leaf: true },";
        	} */
        	locationTreeModel = Ext.tree;
        	
        	locationTree = new locationTreeModel.TreePanel({
        		useArrows: false,
                autoScroll: true,
                animate: true,
                enableDD: false,                
                border: false,                
                containerScroll: true,
                rootVisible: false,                
                height: 300,
                 
        		//children: eval('([' + locationTreeString + '])'),              
                root : {
                	   
                	children : tStoreNew, margins: '15 0 15 0',               	
                },
             
            
                listeners: {
                	//노드를 클릭했을때 발생하는 이벤트
                    click: function(node, event) {
                        getLocation(node.id);              
                        
                        //alert(""+this.getSelectionModel().getSelectedNode());
                    },
                    //노드가 아닌 부분을 클릭하면 발생하는 이벤트
                    containerclick: function(tree,event) {                    	
                    	locLoad = false;
                    	resetLocation(function(){
                        	locationTree.destroy();
                        });                    	
                    },
                    destroy: function(tree) {
                    	 getLocationTreeObj(); 
                    },
                    
                }
        	});
        	
        	//render the tree
        	locationTree.render('supplier-tab1');
        	locationTree.getRootNode();        	
        
        }
        
        $(document).ready(function() {
            // 전기 
            //grid1 = getFlexObject('chargeMgmtEm');
            // 가스
            //grid2 = getFlexObject('chargeMgmtGm');
            // 수도 
            //grid3 = getFlexObject('chargeMgmtWm');
			
            $('#yyyymmddCombo').selectbox();
			$('#tariffTypeCombo').selectbox();
            init();
            
            //location Tree 항목에 필요한 데이터를 불러오며, 트리를 그려주는 함수가 포함되어 있다.
            getLocationTreeObj();        	


            if (editAuth == "true") {
                $("#locationBtnList").show();
                $("#importEMBtn").show();
                $("#importGMBtn").show();
                $("#importWMBtn").show();
                $("#importWMCaliberBtn").show();
                $("#tariffUpdateBtn").show();
            } else {
                $("#locationBtnList").hide();
                $("#importEMBtn").hide();
                $("#importGMBtn").hide();
                $("#importWMBtn").hide();
                $("#importWMCaliberBtn").hide();
                $("#tariffUpdateBtn").hide();
            }

            $(function() { $('#_basic').bind('click',function(event) { selectedTab = false; }); });
            $(function() { $('#_mgmt') .bind('click',function(event) { selectedTab = true; }); });
            $(function() { $('#_pay')  .bind('click',
                    function(event) {
                        selectedTab = false;
                        changeFlexDiv($('#supplierType option:selected').get(0).id);
                    });
            });

            // 공급타입 변경시 flex div 변경
            $(function() { $('#supplierType').bind('change',function(event) { changeFlexDiv($('#supplierType option:selected').get(0).id); }); });

            //조회버튼클릭 이벤트 생성
            $(function() { $('#btnSearch').bind('click',function(event) { send(); } ); });

            new AjaxUpload('importGM', {
                action: '${ctx}/gadget/system/supplier/getTempFileName.do',
                data : {
                },
                responseType : false,
                onSubmit : function(file , ext) {
                    if (ext && /^(xls)$/.test(ext)) {
                        this.setData({
                            'key': 'This string will be send with the file'
                        });
                        return true;
                    } else {
                        return false;
                    }
                },
                onComplete : function(file, response) {
                    var response = JSON.parse($(response).text());
                    setGrid(response.filePath);
                }
            });

            /* new AjaxUpload('importWM', {
                action: '${ctx}/gadget/system/supplier/getTempFileName.do',
                data : {
                },
                responseType : false,
                onSubmit : function(file , ext) {
                    if (ext && /^(xls)$/.test(ext)) {
                        this.setData({
                            'key': 'This string will be send with the file'
                        });
                        return true;
                    } else {
                        return false;
                    }
                },
                onComplete : function(file, response) {
                    var response = JSON.parse($(response).text());
                    setGrid(response.filePath);
                }
            }); */

            /* new AjaxUpload('importWMCaliber', {
                action: '${ctx}/gadget/system/supplier/getTempFileName.do',
                data : {
                },
                responseType : false,
                onSubmit : function(file , ext) {
                    if (ext && /^(xls)$/.test(ext)) {
                        this.setData({
                            'key': 'This string will be send with the file'
                        });
                        return true;
                    } else {
                        return false;
                    }
                },
                onComplete : function(file, response) {
                    var response = JSON.parse($(response).text());
                    setGrid(response.filePath);
                }
            }); */
        });

        var winObj;
        function exportExcel(id) {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var yyyymmdd = "";
            var fileType = $('#supplierType option:selected').val();
            yyyymmdd = $('#yyyymmddCombo option:selected').val();
            var tariffType = $('#tariffTypeCombo option:selected').val();
            
            if (fileType=='3.1'){ fileType = 'Electricity';}
            else if (fileType=='3.2'){ fileType = 'Water';}
            else if (fileType=='3.3'){ fileType = 'Gas';}
            if (id == 'ExportWMCaliber') {
                fileType = 'WaterCaliber';
            }

            obj.supplierId          = supplierObj.id;
            obj.fileType            = fileType;
            obj.yyyymmdd            = yyyymmdd;
			obj.tariffType 			= tariffType;
			
            if(winObj) {
                winObj.close();
            }
            winObj = window.open("${ctx}/gadget/system/supplier/supplierMaxExportPopup.do?fileType="+fileType, "supplierExcel", opts);
            winObj.opener.obj = obj;
        }

        function initDialog() {
            var $dialog = $("div#new-electric-tariff");
            var $season = $dialog.find("select[name=season]");
            var $peakType = $dialog.find("select[name=peakType]");

            $.each(Season, function (idx, element) {
                var $option = $("<option></option>");
                $option.attr('value', Season[idx]);
                $option.text(Season[idx]);
                $season.append($option);
            });
            $season.selectbox();

            $.each(PeakType, function (idx, element) {
                var $option = $("<option></option>");
                $option.attr('value', PeakType[idx]);
                $option.text(PeakType[idx]);
                $peakType.append($option);
            });
            $peakType.selectbox();

            $('#new-electric-tariff').dialog({
                autoOpen: false,
                resizable: false,
                modal: false,
                open: function() {
                    $season.selectbox();
                    $peakType.selectbox();
                }
            });
        }

        function init() {
            hide();
            getSuppliers(initCalendar);
            resetDefault();
            resetType();
            resetLocation();
            resetLocationService();            
            var supplierId = "<%=miniSupplierId%>";
            if (supplierId != "") {
                getSupplier(supplierId);
            }
            $("#supplierTab").tabs();
            initDialog();
        }

        function setGrid(filePath) {
            var fileType = $('#supplierType option:selected').val();
            var appliedDate = $('#yyyymmddCombo option:selected').val();
            if (appliedDate == "") {
                appliedDate = $('#yyyymmddCombo option:eq(1)').text();
            }

            blnImport = true; // import했는지 판별

            if (fileType=="3.1") {
                fileType="Electricity";
                grid1.importExcel(filePath, fileType, supplierObj.id, appliedDate);
            } else if (fileType=="3.2") {
                fileType="Water(Caliber)";
                grid3.importExcel(filePath, fileType, supplierObj.id, appliedDate);
            } else if (fileType=="3.3") {
                fileType="Gas";
                grid2.importExcel(filePath, fileType, supplierObj.id, appliedDate);
            }
        }

        // 공급사 리스트 로딩
        function getSuppliers(callback) {
            $.getJSON('${ctx}/gadget/system/supplier/getSuppliers.do',
                function(json) {
                    if (json.suppliers != null) {
                        $('#slist').pureSelect(json.suppliers);

                        getCount('suppliers');
                        getSupplier(json.suppliers[0].id, callback);

                        var innerHtml = "<a href='javascript:addDefault();'><ul><li><fmt:message key='aimir.supplier.regist'/></li></ul></a>";
                        $('#supplierAddButton1').html(innerHtml);

                        $('#slist').change( function () {
                            supplierId = $(this).val();
                            if (supplierId != 0) {
                                getSupplier(supplierId, callback);                                
                            }
                            resetLocation();
                            resetLocationService();
                            $("#supplierTab").tabs("select",0);
                        });
                    } else if (json.supplier != null) {
                        var supplier = json.supplier;
                        licenceUse = json.licenceUse;
                       	totalLicenceCount = json.totalLicenceCount;
                        registeredLicenceCount = json.registeredLicenceCount;
                        availableLicenceCount = json.availableLicenceCount;
                        
                        var innerHtml = "<span id='supplierFont2' class='bg-white input-fake border-blue bluebold11pt'>" + supplier['name'] + "</span>";

                        $('#supplierList').html(innerHtml);
                        
                        getCount('supplier');
                        getSupplier(supplier['id'], callback);
                        setTab(supplier['name']);
                    }
                }
            );
        }

        // 공급사 선택 시 정보를 로딩한다.
        function getSupplier(supplierId, callback) {
            if (supplierId != null) {
                sId = supplierId;
                sLoad = true;
            }
            $.getJSON('${ctx}/gadget/system/supplier/getSupplier.do', {supplierId: sId},
                function(json) {
                    supplierObj = json.supplier;
                    
                    licenceUse = json.licenceUse;
                    totalLicenceCount = json.totalLicenceCount;
                    registeredLicenceCount = json.registeredLicenceCount;
                    availableLicenceCount = json.availableLicenceCount;
                    
                    bindingDefault();
                    bindingType(json.supplyTypes);
                    if (callback) {
                        callback();
                    }
            }); 

            // 요금관리 - 공급타입 콤보 조회
            $.getJSON('${ctx}/gadget/system/supplier/getSupplierTypes.do', {supplierId: sId},
                function(json) {
                    $('option', $('#supplierType')).remove();
                    $.each(json.supplyTypes, function(index, supplyType) {
                        $('#supplierType').append("<option value='"
                            +supplyType['typeCode'] + "' id='"+supplyType['type']+"' "
                            +">"+supplyType['typeDescr']+"</option>");
                    });
                    $('#supplierType').selectbox();
            });
        }
    	
        function bindingDefault() {
            $('#supplierAddButton1').show();
            $('#supplierAddButton2').hide();
            $('#typeControl_title').show();
            $('#typeControl').show();
            
            if (supplierObj == null) {
                resetDefault();
            }
            else {
                var innerHtml = "";

                innerHtml += "<div class='headspace-enter'><label class='check'>" + supplierObj.name + "&nbsp;<fmt:message key='aimir.button.basicinfo'/></label></div>"
                + "<table class='customer_detail'>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.supplier.name'/></th>"
                //+ "<td><input readonly class='noborder' value='" + supplierObj.name + "'/></td></tr>"
                + '<td><input readonly class="noborder" value="' + supplierObj.name + '"/></td></tr>'
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.description'/></th>"
                //+ "<td><input readonly class='noborder' value='" + supplierObj.descr + "'/></td></tr>"
                + '<td><input readonly class="noborder" value="' + supplierObj.descr + '"/></td></tr>'
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.tel.no'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.telno + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.country'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.country + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.supplier.address'/></th>"
                //+ "<td><input readonly class='noborder' value='" + supplierObj.address + "'/></td></tr>"
                + '<td><input readonly class="noborder" value="' + supplierObj.address + '"/></td></tr>'
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.language'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.language + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.timezone'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.timezone + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'>Date Pattern</th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.sysDatePattern + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.md.pattern'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.mdPattern + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.cd.pattern'/></th>"
                + "<td><input readonly class='noborder' value='" + supplierObj.cdPattern + "'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.md.round'/></th>";
                
                if (supplierObj.mdRound == "r") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.half'/>'/></td></tr>";
                } else if (supplierObj.mdRound == "c") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.ceil'/>'/></td></tr>";
                } else if (supplierObj.mdRound == "f") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.down'/>'/></td></tr>";
                }
             	
                innerHtml += "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.cd.round'/></th>";

                if (supplierObj.cdRound == "r") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.half'/>'/></td></tr>";
                } else if (supplierObj.cdRound == "c") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.ceil'/>'/></td></tr>";
                } else if (supplierObj.cdRound == "f") {
                    innerHtml += "<td><input readonly class='noborder' value='<fmt:message key='aimir.supplier.round.down'/>'/></td></tr>";
                }
                
                innerHtml += "<tr><th class='darkgraybold11pt'>Licence Use</th>";

                if (licenceUse == 1) {
                	innerHtml +=  '<td><input readonly class="noborder" value="Y"/></td></tr>';
                } else {
                	innerHtml +=  '<td><input readonly class="noborder" value="N"/></td></tr>';
                }

           		innerHtml +=  "<tr><th class='darkgraybold11pt'>Total Contract Licence</th>"
                   			+ '<td><input readonly class="noborder" value="' + totalLicenceCount + '"/></td></tr>'
                   			
                   			+ "<tr><th class='darkgraybold11pt'>Registered Licence</th>"
                   			+ '<td><input readonly class="noborder" value="' + registeredLicenceCount + '"/></td></tr>'
                   			
                   			+ "<tr><th class='darkgraybold11pt'>Available Licence</th>"
                   			+ '<td><input readonly class="noborder" value="' + availableLicenceCount + '"/></td></tr>';
            	
                innerHtml += "</table>";
                
                if (editAuth == "true") {
                    innerHtml += "<div class='btn btn_right_bottom'><ul><li style='margin:0;'><a href='javascript:updateDefault()' class='on'><fmt:message key='aimir.update'/></a></li></ul>"
                               + "</div>";
                }
                $('#default').html(innerHtml);
            }
        }

        // 공급사 추가
        function addDefault() {
            $('#supplierAddButton1').hide();
            $('#supplierAddButton2').show();
            $('#typeControl_title').hide();
            $('#typeControl').hide();
            $("#default").load("${ctx}/gadget/system/supplier/addSupplier.do?supplierId=" + sId);

            $("#supplierTab").tabs("select",0);
        }

        function addSupplierResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertsuccess'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertfail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getSuppliers();
                document.getElementById("supplierDefault").reset();
            }
        }

        // 공급사 수정
        function updateDefault() {
            $('#typeControl_title').hide();
            $('#typeControl').hide();
            
            if (sLoad == true) {
            	$("#default").load("${ctx}/gadget/system/supplier/updateSupplier.do?supplierId=" + sId);
            }
        }

        function updateSupplierResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatesuccess'/>");
                $.getJSON('${ctx}/gadget/system/supplier/sendIHDCustomerInfosMessage.do', {supplierId: sId},
                        function(json) {
                            //IHD그룹이 없을경우 IHD관련 리턴메세지를 보여주지 않는다.
                            if (!(json.cntSuccess == 0 && json.cntFail == 0)) {
                                //결과값 리턴
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"IHD SUCCESS ["+json.cntSuccess+"], FAIL ["+json.cntFail+"]");
                            }
                    });
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatefail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
            	getSupplier(sId);
            }
        }
	
        // 공급사 전달
        function submitDefault(type) {
            $('#typeControl_title').hide();
            $('#typeControl').hide();

            if (type == "add") {
                var options = {
                    success : addSupplierResult,
                    url : '${ctx}/gadget/system/supplier/addSupplier.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#supplierDefault').ajaxSubmit(options);
            } else if (type == "update" && sLoad == true) {
            	var options = {
	                    success : updateSupplierResult,
	                    url : '${ctx}/gadget/system/supplier/updateSupplier.do',
	                    type : 'post',
	                    datatype : 'json'
					}

                $('#supplierDefault').ajaxSubmit(options); 
            }
        }

        // 공급사 삭제
        function deleteDefault() {
            if (sLoad == true) {
                $.ajax({
                    url: "${ctx}/gadget/system/supplier/deleteSupplier.do?supplierId=" + sId,
                    cache: false,
                    success: deleteSupplierResult
                });
            }
        }

        function deleteSupplierResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deletesuccess'/>");
                sLoad = false;
                supplierObj = null;
                getSuppliers();
                resetDefault();
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deleteFail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            }
        }

        // 공급사 reset
        function resetDefault() {
            var innerHtml = "";
            innerHtml += "<ul>" +
                         "  <li style='width:100px;height:200px;font-weight:bold;'></li>" +
                         "</ul>";
            $('#default').html(innerHtml);
        }

        function bindingType(supplyTypes) {
            var innerHtml = "";

            addType(); //type에 innerHtml부름

            if (supplyTypes.length == 0) {
                resetType();
            }
            else {
                innerHtml += "<div><table>";
                $.each(supplyTypes, function(index, supplyType) {

                    //마우스 컨트롤을 위해서 supplyType을 id로 건네준다.
                    innerHtml += "<tr class='addsupplytype-tr' id='"+ supplyType['type'] +"'onMouseOver='chtr(this)' onMouseOut='chtr2(this)'><td class='addsupplytype-td'>";
                    innerHtml += "<table id='type" + supplyType['id'] + "' class='addsupplytype wfree'><tr>" +
                                 "  <td class='addsupplytype-select bluebold11pt withinput'>" + supplyType['typeDescr'] + "</td>" +
                                  "  <td class='addsupplytype-co2name bluebold11pt withinput '> ("+supplyType.co2Formula[0].name+")</td>" +
                                 "  <td class='gray11pt withinput'><fmt:message key='aimir.paydate'/> : </td>" +
                                 "  <td class='addsupplytype-input-date bluebold11pt withinput'>" + supplyType['billDate'] + "</td>";
                    if (supplyType['co2Formula'] != '') {
                        $.each(supplyType['co2Formula'], function(index, co2) {
                        innerHtml += "  <td class='gray11pt withinput'><fmt:message key='aimir.co2formula'/> : </td>" +
                                     "  <td class='withinput'><span class='addsupplytype-input-co2 bluebold11pt textalign-right'>" + co2['co2emissions'] + "</span><span class='blue11pt'>&nbsp;&nbsp;㎏ CO₂</span></td>" +
                                     "  <td class='lightgray11pt withinput'>(<fmt:message key='aimir.usage'/>" +
                                     co2['unitUsage'] + "&nbsp;&nbsp;" + co2['unit'] + ")</td></tr></table></td>";
                        });
                    } else {
                        innerHtml += "  <td class='gray11pt withinput'><fmt:message key='aimir.co2formula'/> : </td></tr></table></td>";
                    }

                    //각각의 id를 supplyType이름에 1을 더해서 준다
                    /* innerHtml += "<td class='addsupplytype-td-btn'><div id='" + supplyType['type'] + "1' class='gadget_btn addsupplytype' style='display:none;'>" +
                     "      <ul><li id='gadget_del'>" +
                     "          <a href=javascript:deleteType('" + supplyType['id'] + "') " +
                     "              title='<fmt:message key='aimir.button.delete'/>'>" +
                     "          </a>" +
                     "      </li>" +
                     "      <li id='gadget_modi'>" +
                     "          <a href=javascript:updateType('" + supplyType['id'] + "') " +
                     "              title='<fmt:message key='aimir.update'/>'>" +
                     "          </a>" +
                     "      </li>" +
                     "  </ul></div>"; */

                    innerHtml += "<td class='addsupplytype-td-btn'>";
                    if (editAuth == "true") {
                        innerHtml += "<div id='" + supplyType['type'] + "1' class='gadget_btn addsupplytype' style='display:none;'>" +
                                     "    <ul><li id='gadget_del'>" +
                                     "            <a href=javascript:deleteType('" + supplyType['id'] + "') " +
                                     "               title='<fmt:message key='aimir.button.delete'/>'>" +
                                     "            </a>" +
                                     "        </li>" +
                                     "        <li id='gadget_modi'>" +
                                     "            <a href=javascript:updateType('" + supplyType['id'] + "') " +
                                     "               title='<fmt:message key='aimir.update'/>'>" +
                                     "            </a>" +
                                     "        </li>" +
                                     "    </ul></div>";
                    }

                    innerHtml += "</td></tr>";
                });

                innerHtml += "</table></div>";
                $('#type1').html(innerHtml);
            }
        }

        // 공급타입 추가
        function addType() {
            if (sLoad == true) {
                $("#type").load("${ctx}/gadget/system/supplier/addSupplyType.do?supplierId=" + sId);
            }
        }

        // 공급타입 전달
        function submitType(type) {
            var billDate = null;
            if (type == "add") {
                billDate = $("#supplyTypeAddForm #billDate").val();
            } else if (type == "update" && sLoad == true) {
                billDate = $("#supplyTypeUpdateForm #billDate").val();
            }

            if (billDate == null || billDate == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.firmware.msg16'/>");
                return;
            }

            if (type == "add") {
                var options = {
                    success : addSupplyTypeResult,
                    url : '${ctx}/gadget/system/supplier/addSupplyType.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#supplyTypeAddForm').ajaxSubmit(options);
            } else if (type == "update" && sLoad == true) {
                var options = {
                    success : updateSupplyTypeResult,
                    //url : '${ctx}/gadget/system/supplier/updateSupplyType.do?billDate=' + $("#billDate").val(),
                    url : '${ctx}/gadget/system/supplier/updateSupplyType.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#supplyTypeUpdateForm').ajaxSubmit(options);
            }
        }

        function addSupplyTypeResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.msg.insertsuccess'/>");
            } else if(responseText.result == "fmerror"){
            	Ext.Msg.alert("<fmt:message key='aimir.message'/>", "Can't find the valid Co2-Formula for input type");
            } else {
                Ext.Msg.alert("<fmt:message key='aimir.message'/>", "<fmt:message key='aimir.msg.insertfail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getSupplier(sId);
            }
        }

        function updateSupplyTypeResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatesuccess'/>");
                $.getJSON('${ctx}/gadget/system/supplier/sendIHDCustomerInfosMessage.do', {supplierId: sId},
                        function(json) {
                            //IHD그룹이 없을경우 IHD관련 리턴메세지를 보여주지 않는다.
                            if (!(json.cntSuccess == 0 && json.cntFail == 0)) {
                                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"IHD SUCCESS ["+json.cntSuccess+"], FAIL ["+json.cntFail+"]");
                            }
                    });
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatefail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getSupplier(sId);
            }
        }

        // 공급타입 수정
        function updateType(typeId) {
            if (sLoad == true) {
                var url = "${ctx}/gadget/system/supplier/updateSupplyType.do";
                url = url + "?supplyTypeId=" + typeId;
                var div = "#type" + typeId;
                $(div).load(url);
            }
        }

        // 공급사 삭제
        function deleteType(typeId) {
            if (sLoad == true) {
                $.ajax({
                    url: "${ctx}/gadget/system/supplier/deleteSupplyType.do?supplyTypeId=" + typeId,
                    cache: false,
                    success: deleteSupplyTypeResult
                });
            }
        }

        function deleteSupplyTypeResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deletesuccess'/>");
                getSupplier();
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deleteFail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            }
        }

        function resetType() {
            var innerHtml = "";
            innerHtml += "<ul>" +
                         "  <li style='width:100px;height:200px;font-weight:bold'></li>" +
                         "</ul>";

            $('#type').html(innerHtml);
            $('#type1').html(innerHtml);
        }

        

        function getLocation(locationId, callback) {
            if (locationId != null) {
                locId = locationId;
                locLoad = true;
            }
            $.getJSON('${ctx}/gadget/system/supplier/getLocation.do', {locationId:locId},
                function(json) {
                    locationObj = json.location;
                    bindingLocation();
                    bindingLocationService(json.locationServices);
            });
            
        }

        function addLocation() {
            if (sLoad == true) {
                var parentId = -1;
                if (locLoad == true) {
                   
                    parentId = locationTree.getSelectionModel().selNode.id;
                    }

                var url = "${ctx}/gadget/system/supplier/addLocation.do";
                url = url + "?supplierId=" + sId;
                url = url + "&parentId=" + parentId;

                $("#location").load(url);
            }
        }

        function updateLocation() {
            if (locLoad == true) {
                $("#location").load("${ctx}/gadget/system/supplier/updateLocation.do?locationId=" + locId);
            }
        }

        function deleteLocation(typeId) {
            if (locLoad == true) {
                $.ajax({
                    url: "${ctx}/gadget/system/supplier/deleteLocation.do?locationId=" + locId,
                    cache: false,
                    success: deleteLocationResult
                });
            }
        }

        function deleteLocationResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deletesuccess'/>");
                resetLocationTreeObj();
                resetLocation();
                resetLocationService();
                locLoad = false;
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deleteFail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            }
        }

        function bindingLocation() {        
            if (locationObj == null) {
                resetLocation();
            }
            else {
                var innerHtml = "";

                innerHtml += "<div class='headspace-enter'><span class='openfolder'><input readonly value='" + locationObj['name'] + "'/></span></div>"
                + "<div class='clear'></div><div class='supplierlocation-detail'><table class='customer_detail_noborder'>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.parent.location'/></th>";

                if (locationObj['parent'] == 'null') {
                    innerHtml += "<td><input readonly value=''/></td></tr>";
                }
                else {
                    innerHtml += "<td><input readonly value='" + locationObj['parent'] + "' class='noborder'/></td></tr>";
                }

                innerHtml += "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.siteName'/></th>"
                + "<td><input readonly value='" + locationObj['name'] + "' class='noborder'/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.orderNo'/></th>"
                + "<td><input readonly value='" + locationObj['orderNo'] + "' class='noborder'/></td></tr>"
                + "</table></div>"
                + "<div class='clear'></div>";

                if (editAuth == "true") {
                    innerHtml += "<div class='btn_right_bottom'><span id='btn'><ul><li style='margin:0;'><a href='javascript:updateLocation()' class='on'><fmt:message key='aimir.update'/></a></li></ul></span></div>";
                }

                $('#location').html(innerHtml);
            }
        }

        function submitLocation(type) {
            if ($("#locationName").val().trim() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.msg.inputlocationname"/>");
                $("#locationName").val('');
                $("#locationName").focus();
                return;
            }

            if ($("#orderNo").val().trim() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key="aimir.msg.inputorderno"/>");
                $("#orderNo").val('');
                $("#orderNo").focus();
                return;
            }

            if (type == "add") {
                var check = $("#checkTop");
                var parent = $("#parentId");

                if (check.is(":checked")) {
                    parent.val("");
                }

                var options = {
                    success : addLocationResult,
                    url : '${ctx}/gadget/system/supplier/addLocation.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#locationAddForm').ajaxSubmit(options);
            } else if (type == "update" && sLoad == true) {
                var options = {
                    success : updateLocationResult,
                    url : '${ctx}/gadget/system/supplier/updateLocation.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#locationUpdateForm').ajaxSubmit(options);
            }
        }

        function addLocationResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertsuccess'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertfail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
            	resetLocationTreeObj();
                resetLocation();             //
                resetLocationService();      //
            }
        }

        function updateLocationResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatesuccess'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatefail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i=0 ; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
            	resetLocationTreeObj();
                resetLocation();            //
                resetLocationService();     //
            }
        }

        function resetLocation() {   
            var innerHtml = "";
            innerHtml += "<div class='headspace-enter'><span class='openfolder'><input readonly value='" + "<fmt:message key='aimir.tree.selectmsg' />" + "'></span></div>"
                + "<div class='clear'></div><div class='supplierlocation-detail'><table class='customer_detail_noborder'>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.parent.location'/></th>"
                + "<td><input readonly class='noborder' value=''/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.siteName'/></th>"
                + "<td><input readonly class='noborder' value=''/></td></tr>"
                + "<tr><th class='darkgraybold11pt'><fmt:message key='aimir.orderNo'/></th>"
                + "<td><input readonly class='noborder' value=''/></td></tr>"
                + "</table></div>";

            $('#location').html(innerHtml);            
        }

        function addLocationService() {        	
            if (locLoad == true) {
                var url = "${ctx}/gadget/system/supplier/addLocationService.do";
                url = url + "?supplierId=" + sId;
                url = url + "&locationId=" + locId;

                $("#locationService").load(url);
            }
        }

        function bindingLocationService(locationServices) {
            var innerHtml = "";
            addLocationService();
            if (locationServices.length == 0) {
                resetLocationService();
            } else {
                innerHtml += "<div><table>";
                $.each(locationServices, function(index, service) {
                    //마우스 컨트롤을 위해서 service을 id로 건네준다.

                    innerHtml += "<tr class='addsupplytype-tr' id='"+ service['supplyType'] +"'onMouseOver='chtr3(this)' onMouseOut='chtr4(this)'><td class='addsupplytype-td'>";
                    innerHtml += "<table id='service" + service['id'] + "' class='addsupplytype wfree'><tr>" +
                                 "  <td class='addsupplytype-select bluebold11pt withinput'>" + service['supplyType'] + "</td>" +
                                 "  <td class='gray11pt withinput'><fmt:message key='aimir.constract.capacity'/> : </td>" +
                                 "  <td class='addsupplytype-input-capacity bluebold11pt withinput'>" + service['constractCapacity']+" kW" + "</td></tr></table></td>";

                    //각각의 id를 service이름에 2을 더해서 준다
                    /* innerHtml += "  <td class='addsupplytype-td-btn'><div id='" + service['supplyType'] + "2' class='gadget_btn addsupplytype' style='display:none'>" +
                                 "  <ul><li id='gadget_del'>" +
                                 "         <a href=javascript:deleteLocationService('" + service['id'] + "')>" +
                                 "          </a>" +
                                 "      </li>" +
                                 "      <li id='gadget_modi'>" +
                                 "          <a href=javascript:updateLocationService('" + service['id'] + "')>" +
                                 "          </a>" +
                                 "      </li>" +
                                 "  </ul></div>";
                    innerHtml += "</td></tr>"; */

                    innerHtml += "  <td class='addsupplytype-td-btn'>";
                    if (editAuth == "true") {
                        innerHtml += "  <div id='" + service['supplyType'] + "2' class='gadget_btn addsupplytype' style='display:none'>" +
                                     "  <ul><li id='gadget_del'>" +
                                     "         <a href=javascript:deleteLocationService('" + service['id'] + "')>" +
                                     "          </a>" +
                                     "      </li>" +
                                     "      <li id='gadget_modi'>" +
                                     "          <a href=javascript:updateLocationService('" + service['id'] + "')>" +
                                     "          </a>" +
                                     "      </li>" +
                                     "  </ul></div>";
                    }
                    innerHtml += "</td></tr>";
                });

                innerHtml += "</table></div>";
                $('#locationService1').html(innerHtml);
            }
        }

        function updateLocationService(locationServiceId) {
            if (locLoad == true) {
                var url = "${ctx}/gadget/system/supplier/updateLocationService.do";
                url = url + "?locationServiceId=" + locationServiceId;
                var div = "#service" + locationServiceId;

                $(div).load(url);
            }
        }

        function submitLocationService(type) {
            if (type == "add") {
                $('#supplyTypeLocation').val(locId);
                var options = {
                    success : addLocationServiceResult,
                    url : '${ctx}/gadget/system/supplier/addLocationService.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#locationServiceAddForm').ajaxSubmit(options);
            } else if (type == "update" && sLoad == true) {
                var options = {
                    success : updateLocationServiceResult,
                    url : '${ctx}/gadget/system/supplier/updateLocationService.do',
                    type : 'post',
                    datatype : 'json'
                };
                $('#locationServiceUpdateForm').ajaxSubmit(options);
            }
        }

        function addLocationServiceResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertsuccess'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.insertfail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getSuppliers();
                getLocation(locId);
                document.getElementById("locationServiceAddForm").reset();
            }
        }

        function updateLocationServiceResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatesuccess'/>");
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.updatefail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            } else {
                getSuppliers();
                getLocation(locId);
            }
        }

        function deleteLocationService(locationServiceId) {
            if (locLoad == true) {
                $.ajax({
                    url: "${ctx}/gadget/system/supplier/deleteLocationService.do?locationServiceId=" + locationServiceId,
                    cache: false,
                    success: deleteLocationServiceResult
                });
            }
        }

        function deleteLocationServiceResult(responseText, status) {
            if (responseText.result == "success") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deletesuccess'/>");
                getLocation();
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.msg.deleteFail'/>");
            }
            if (responseText.errors && responseText.errors.errorCount > 0) {
                var i, fieldErrors = responseText.errors.fieldErrors;
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            }
        }

        function resetLocationService() {
            var innerHtml = "";
            $('#locationService').html(innerHtml);
            $('#locationService1').html(innerHtml);
        }

        function chtr(obj) { // 마우스 오버
            obj.style.cursor = "hand";
            obj.style.backgroundColor = "#f3fad1";

            if (obj.id == "Electricity") {
                $('#Electricity1').show();
            }
            else if (obj.id == "Gas") {
                $('#Gas1').show();
            }
            else if (obj.id == "Heat") {
                $('#Heat1').show();
            }
            else if (obj.id == "Water") {
                $('#Water1').show();
            }
            else if (obj.id == "VolumeCorrector") {
                $('#VolumeCorrector1').show();
            }
        }

        function chtr2(obj) { // 마우스 아웃
            obj.style.backgroundColor="#ffffff";
            $('#typeButton').hide();
            if (obj.id == "Electricity") {
                $('#Electricity1').hide();
            }
            else if (obj.id == "Gas") {
                $('#Gas1').hide();
            }
            else if (obj.id == "Heat") {
                $('#Heat1').hide();
            }
            else if (obj.id == "Water") {
                $('#Water1').hide();
            }
            else if (obj.id == "VolumeCorrector") {
                $('#VolumeCorrector1').hide();
            }
        }

        function chtr3(obj) { // 마우스 오버
            obj.style.cursor="hand";
            obj.style.backgroundColor="#f3fad1";

            if (obj.id == "Electricity") {
                $('#Electricity2').show();
            }
            else if (obj.id == "Gas") {
                $('#Gas2').show();
            }
            else if (obj.id == "Heat") {
                $('#Heat2').show();
            }
            else if (obj.id == "Water") {
                $('#Water2').show();
            }
            else if (obj.id == "VolumeCorrector") {
                $('#VolumeCorrector2').show();
            }
        }

        function chtr4(obj) { // 마우스 아웃
            obj.style.backgroundColor="#ffffff";
            $('#typeButton').hide();
            if (obj.id == "Electricity") {
                $('#Electricity2').hide();
            }
            else if (obj.id == "Gas") {
                $('#Gas2').hide();
            }
            else if (obj.id == "Heat") {
                $('#Heat2').hide();
            }
            else if (obj.id == "Water") {
                $('#Water2').hide();
            }
            else if (obj.id == "VolumeCorrector") {
                $('#VolumeCorrector2').hide();
            }
        }

        function getCount(type) {
            var innerHtml = "";

            if (type == 'suppliers') {
                jQuery.ajaxSetup({
                    cache: false
                });
                $.getJSON('${ctx}/gadget/system/supplier/getCount.do', function(json) {
                    count = json.count;

                    innerHtml += "<ul>" +
                    "<li class='result-text'><fmt:message key='aimir.supplier.list'/> : </li><li class='result-num'>&nbsp;" + count + "&nbsp;</li>" +
                    "</ul>";
                });
            }
            else if (type == 'supplier') {
                innerHtml += "<ul>" +
                "<li class='result-text'><fmt:message key='aimir.supplier.list'/> : </li><li class='result-num'>1</li>" +
                "</ul>";
            }
            $('#supplierCount').html(innerHtml);
        }

        function selectAppliedTariffDate(supplierType, callback) {
            var date = $.format.date(new Date(), "yyyyMMdd");
            $.getJSON("${ctx}/gadget/system/supplier/getAppliedTariffDate.do",
                {supplierType: supplierType,
                    yyyymmdd: date,
                    supplierId: sId},
                    function(json) {
                        if (json.date) {
                            $('#yyyymmddCombo').val(json.date);
                            $('#yyyymmddCombo').selectbox('change');
                            $('#yyyymmddCombo').selectbox();
                            
                            getTariffGrid();
                            
                            if (callback) {
                                callback();
                            }
                        }
                    });
        }

        // 요금관리 - 공급타입별 적용일자 콤보 리스트 조회
        function getYyyymmddList(supplyType, callback) {
            $.getJSON('${ctx}/gadget/system/supplier/getYyyymmddList.do', {supplierType:supplyType, supplierId:sId},
                    function(json) {
                        $('#yyyymmddCombo').empty();
                        $('#yyyymmddCombo').append("<option value=''><fmt:message key='aimir.buildingMgmt.applyDate'/></option>");
                        if (json.yyyymmddList != null) {
                            $.each(json.yyyymmddList, function(index, yyyymmdd) {
                                $('#yyyymmddCombo').append("<option value='"+yyyymmdd['yyyymmdd']+"'>"+yyyymmdd['yyyymmdd']+"</option>");
                            });
                        }
                        //selectAppliedTariffDate() EM일 경우에만 최근 date를 알 수 있기때문에 WM은 리스트의 마지막 date를 수동으로 넣어서 조회
                        if(supplyType=='Water'){
                        	if(json.yyyymmddList.length > 0){
                        		$('#yyyymmddCombo').val(json.yyyymmddList[json.yyyymmddList.length-1].yyyymmdd);
                        	}else{
	                        	$('#yyyymmddCombo').val();
                        	}
                        	$('#yyyymmddCombo').selectbox('change');
                        	getTariffGrid();
                        }
                        $('#yyyymmddCombo').selectbox();
                        selectAppliedTariffDate(supplyType, callback);
            });
        }

        // 요금관리 - 공급타입 선택 시 Flex Div 변경
        function changeFlexDiv(type) {

            supplyType = getSupplyType(type);
            getTariffTypeList();
            // 공급타입별 적용일자 콤보 리스트 로딩
            getYyyymmddList(supplyType);

            if (supplyType == SupplierType.Electricity) {
                $('#electricDiv').css('display','inline');
                $('#gasDiv').css('display','none');
                $('#waterDiv').css('display','none');
            }
            else if (supplyType == SupplierType.Gas
             || supplyType == SupplierType.Heat) {
                $('#electricDiv').css('display','none');
                $('#gasDiv').css('display','inline');
                $('#waterDiv').css('display','none');
            }
            else if (supplyType == SupplierType.Water
             || supplyType == '<fmt:message key="water"/>') {
                $('#electricDiv').css('display','none');
                $('#gasDiv').css('display','none');
                $('#waterDiv').css('display','inline');
            }
        }

        // 요금관리 - 메세지 처리
        function getFmtMessage() {
            var cnt = 0;
            var fmtMessage = new Array();
            
            /*
            //ECG
            if (supplyType == SupplierType.Electricity) {
                cnt = 0;
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.season"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.supplySize"/>"+"(x)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.serviceCharge"/>";
                fmtMessage[cnt++] = "Old Subsidy(S2)";
                fmtMessage[cnt++] = "Gov Levy";
                fmtMessage[cnt++] = "Street Light Levy";
                fmtMessage[cnt++] = "Vat";
                fmtMessage[cnt++] = "<fmt:message key="aimir.tou"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.activeEnergyCharge"/>";
                fmtMessage[cnt++] = "Lifeline Subsidy(S1)";
                fmtMessage[cnt++] = "New Subsidy(S3)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.excel.chargeMgmtEm"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.energy.excess"/>";       // 초과
                fmtMessage[cnt++] = "<fmt:message key="aimir.below"/>";               // 미만
                fmtMessage[cnt++] = "<fmt:message key="aimr.morethan"/>";             // 이상
                fmtMessage[cnt++] = "<fmt:message key="aimir.less"/>";                // 이하
                fmtMessage[cnt++] = "<fmt:message key="aimir.hour"/>";                // 시간
                fmtMessage[cnt++] = "Utility Relief";
            }
            */

            if (supplyType == SupplierType.Electricity) {
                cnt = 0;
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff"/>";    //0'Tariff'
                fmtMessage[cnt++] = "<fmt:message key="aimir.season"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.supplySize"/>"+"(x)";  //2'Supply Size'
                fmtMessage[cnt++] = "<fmt:message key="aimir.serviceCharge"/>";     //3'Service Charge'
                fmtMessage[cnt++] = "<fmt:message key="aimir.adminCharge"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.transmissionNetworkCharge"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.distributionNetworkCharge"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.energyDemandCharge"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.tou"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.activeEnergyCharge"/>"; //9'Active Energy Charge'
                fmtMessage[cnt++] = "<fmt:message key="aimir.reactiveEnergyCharge"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.rateRevalancingLevy"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.excel.chargeMgmtEm"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.energy.excess"/>";       // 초과
                fmtMessage[cnt++] = "<fmt:message key="aimir.below"/>";               // 미만
                fmtMessage[cnt++] = "<fmt:message key="aimr.morethan"/>";             // 이상
                fmtMessage[cnt++] = "<fmt:message key="aimir.less"/>";                // 이하
                fmtMessage[cnt++] = "<fmt:message key="aimir.hour"/>";                // 시간
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.govSubsidy"/>"; //18'Government Subsidy'
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.publicLevy"/>"; //19'Public Levy'
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.vat"/>";    //20'VAT'
                fmtMessage[cnt++] = "<fmt:message key="aimir.utilityRelief"/>";     //21'Utility Relief'
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.additionalSubsidy"/>"; //22'Additional Subsidy', (S3)
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.lifeLineSubsidy"/>";   //23'Lifeline Subsidy'
                fmtMessage[cnt++] = "<fmt:message key="aimir.prepayment.govLevy"/>";    //24'Government Levy'
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff.nhil"/>";   //25'NHIL'
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff.getfund"/>"; //26'GETFUND'
            }
            else if (supplyType == SupplierType.Gas) {
                cnt = 0;
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.season"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.basicRate"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.unitUsage"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.salePrice"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.adjustmentFactor"/>";
                fmtMessage[cnt++] = "<fmt:message key="aimir.excel.chargeMgmtGm"/>";

            }
            else if (supplyType == SupplierType.Water) {
                cnt = 0;
                //전기 Tariff 화면 적용을 위한 메시지 헤더 변경
                /* fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title1"/>";  // 구경별 기본요금";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title2"/>";  // 사 용 요 금";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title3"/>";  // 업종/구분";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title4"/>";  // 구경(mm)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title5"/>";  // 기본요금(냉수)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title6"/>";  // 기본요금(온수)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title7"/>";  // 사용구분(㎥)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title8"/>";  // 단가(원)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title9"/>";  // 물이용부담금(원)";
                fmtMessage[cnt++] = "<fmt:message key="aimir.energy.excess"/>";       // 초과
                fmtMessage[cnt++] = "<fmt:message key="aimir.below"/>";               // 미만
                fmtMessage[cnt++] = "<fmt:message key="aimr.morethan"/>";             // 이상
                fmtMessage[cnt++] = "<fmt:message key="aimir.less"/>";                // 이하
                fmtMessage[cnt++] = "<fmt:message key="aimir.excel.chargeMgmtWm"/>"; */
                fmtMessage[cnt++] = "<fmt:message key="aimir.tariff"/>";			  // 구분
                fmtMessage[cnt++] = "<fmt:message key="aimir.season"/>";			  // 계절
                fmtMessage[cnt++] = "<fmt:message key="aimir.supplySize"/>"+"(x)";    // 공급사이즈
                fmtMessage[cnt++] = "<fmt:message key="aimir.serviceCharge"/>";		  // 서비스 요금
                fmtMessage[cnt++] = "<fmt:message key="aimir.adminCharge"/>";		  // 관리비용
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title3"/>";  // 업종/구분
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title4"/>";  // 구경(mm)
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title7"/>";  // 사용구분(m3)
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title9"/>";  // 물이용부담금(원)
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title5"/>";  // 기본요금(냉수)
                fmtMessage[cnt++] = "<fmt:message key="aimir.waterCharge.title6"/>";  // 기본요금(온수)
                fmtMessage[cnt++] = "<fmt:message key="aimir.rateRevalancingLevy"/>";  // 에너지수요요금
                fmtMessage[cnt++] = "<fmt:message key="aimir.excel.chargeMgmtWm"/>";  // 수도요금관리보고서
                fmtMessage[cnt++] = "<fmt:message key="aimir.energy.excess"/>";       // 초과
                fmtMessage[cnt++] = "<fmt:message key="aimir.below"/>";               // 미만
                fmtMessage[cnt++] = "<fmt:message key="aimr.morethan"/>";             // 이상
                fmtMessage[cnt++] = "<fmt:message key="aimir.less"/>";                // 이하
                fmtMessage[cnt++] = "<fmt:message key="aimir.hour"/>";                // 시간
			   
            }


            fmtMessage[50] = "<fmt:message key="aimir.msg.updatesuccess"/>";   // 수정 성공
            fmtMessage[51] = "<fmt:message key="aimir.msg.updatefail"/>";      // 수정 실패
            fmtMessage[52] = "<fmt:message key="aimir.data.notexist"/>";       // 데이터 없음
            fmtMessage[53] = "<fmt:message key="aimir.updatedata.notexist"/>"; // 수정할 데이터 없음
            fmtMessage[54] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[55] = "<fmt:message key="aimir.firmware.msg09"/>";       //데이터를 찾을수 없습니다.
            fmtMessage[56] = "<fmt:message key="aimir.msg.wantdelete"/>";   // 정말로 삭제하시겠습니까?
            fmtMessage[57] = "<fmt:message key="aimir.msg.deletesuccess"/>";  //삭제 성공
            fmtMessage[58] = "<fmt:message key="aimir.msg.deleteFail"/>";  //삭제 실패
            fmtMessage[59] = '<fmt:message key="aimir.msg.select.cell.before.add.row"/>';

            fmtMessage[60] = '<fmt:message key="aimir.supplySize"/>';   // Supply Size
            fmtMessage[61] = '<fmt:message key="aimir.min.upper"/>';    // MIN
            fmtMessage[62] = '<fmt:message key="aimir.max"/>';          // MAX
            fmtMessage[63] = '<fmt:message key="aimir.msg.minmaxsupplysize"/>';   // Max Supply Size 는 Min Supply Size 보다 커야 한다.
            fmtMessage[64] = '<fmt:message key="aimir.msg.inputminsupplysize"/>';   // 최소 공급 사이즈를 입력해 주세요.
            fmtMessage[65] = '<fmt:message key="aimir.msg.inputmaxsupplysize"/>';   // 최대 공급 사이즈를 입력해 주세요.

            return fmtMessage;
        }
		
        function getTariffTypeList(){
        	 var fileType = $('#supplierType option:selected').val();
        	$.getJSON('${ctx}/gadget/system/supplier/getTariffType.do'
    				,{ serviceType : fileType, 
        				supplierId : sId
    				}, function (json){ 
    					var result = json.result;
                        var tariffArr = Array();
    					for(var i=0;i<result.length; i++){
    						var obj = new Object();
                            obj.name=result[i].name;
                            tariffArr[i]=obj;
    					}
    					$("#tariffTypeCombo").loadSelect(tariffArr);
    					$("#tariffTypeCombo option:eq(0)").replaceWith("<option value=''>" + "<fmt:message key="aimir.all"/>" + "</option>");
    					$("#tariffTypeCombo").val("");
    					$('#tariffTypeCombo').selectbox();
    					
    				});
        }
        
        // Tariff 그리드 
        var tariffModelEm; // Energy
        var tariffModelGm; // Gas
        var tariffModelWm; // Water
        var tariffStore;
        var tariffGridPanel;
        var tariffInstanceOn = false;
        var tariffWmInstanceOn = false;
        function getTariffGrid(){
        	var yyyymmdd = "";
            var fileType = $('#supplierType option:selected').val();
            yyyymmdd = $('#yyyymmddCombo option:selected').val();
            var tariffType = $('#tariffTypeCombo option:selected').val();
           
            if (fileType=='3.1'){ fileType = 'Electricity';}
            else if (fileType=='3.2'){ fileType = 'Water';}
            else if (fileType=='3.3'){ fileType = 'Gas';}

            var fmtMsgArr = getFmtMessage();
            
        	tariffStore = new Ext.data.JsonStore({
				autoLoad: true,
				url:"${ctx}/gadget/system/supplier/getTariffGrid.do",
				root: 'GridData',
				baseParams:{
					supplierId:sId,
					fileType:fileType,
					yyyymmdd:yyyymmdd,
					tariffType: tariffType
				}, 
				fields: [
					"ID",
					"SEASON",
					"SUPPLYSIZEMIN",
					"SUPPLYSIZEMAX",
					"CONDITION1",
					"CONDITION2",
					"PEAKTYPE",
					"REACTIVEENERGYCHARGE",
					"ADMINCHARGE",
					"SUPPLYSIZEMAX",
					"TARIFFTYPE",
					"TRANSMISSIONNETWORKCHARGE",
					"ACTIVEENERGYCHARGE",
					"SEASON",
					"MAXDEMAND",
					"ENERGYDEMANDCHARGE",
					"DISTRIBUTIONNETWORKCHARGE",
					"SERVICECHARGE",
					"RATEREBALANCINGLEVY",
					//"STARTHOUR",
					//"ENDHOUR",
                    //"NHIL",
                    //"GETFUND",
					"HOUR",
					"TARIFFTYPEID",
					"ERS"
				]
			});//Store End
			
			 var edit = true;

	         if (editAuth != "true") {
	                edit = false;
	         }
			
	        if(fileType == 'Electricity'){ 
			tariffModelEm = new Ext.grid.ColumnModel({
				columns:[
					{
						header:fmtMsgArr[0], //message는 getFmtMessage참조.
						dataIndex:'TARIFFTYPE',
						editable: edit,
						width: 160,
		                editor: {
                            id : 'tariffTypeEdit',
                            xtype: 'textfield',
                            allowBlank : false,
                        },
                        renderer : function(value, me, record, rowNumber, columnIndex, store) {
                        	var tariffType_name = record.data.TARIFFTYPE;
	                        	if(tariffType_name=="PRE-AGRICULTURE")
	                       			return setColorFrontTag + pink + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name=="PRE-GOVERNMENT")
	                       			return setColorFrontTag + green  + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name=="PRE-COMMERCIAL")
	                       			return setColorFrontTag + grey  + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name=="PRE-INDUSTRIAL")
	                       			return setColorFrontTag + gold + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name=="PRE-RESIDENTIAL")
	                       			return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name=="Non Residential")
	                       			return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
	                        	if(tariffType_name="Residential")
	                       			return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
	                        	else
	                        		return value;
	                        	
               				}
					},
					{
						header:'TARIFFTYPEID', 
						dataIndex:'TARIFFTYPEID',
						hidden:true,
						renderer: function (value, meta, record, rowIndex, colIndex, store) {
							$.ajaxSetup({
		       	                async : false
		       	            });
							
							var tariffType_name = record.data.TARIFFTYPE;
							var count = tariffStore.getCount();
		                    var data = [];
		                    for(var i=0; i < count; i++){
		                    	data[i] = tariffStore.data.items[i].data;
		                    	if(data[i].TARIFFTYPE == tariffType_name){
		                    		 TARIFFTYPEID = data[i].TARIFFTYPEID;
		                    		
		                    		 if (tariffType_name != "" && TARIFFTYPEID == "") {
		                    			 setTariffId(tariffType_name);
		                    			 TARIFFTYPEID = tariffTypeId;
		                    		 }
		                    		 
		                    		 break;
		                    	}
		                    }  
	    					record.data.TARIFFTYPEID = TARIFFTYPEID;
						}
					},{
						header:fmtMsgArr[2],
						dataIndex:'SUPPLYSIZE',
						width: 140,
						editable: edit,
						align: 'center',
						renderer: function(value, metaData, record, index) {
	                    	var data = record.data;
	                    	supplyMin = data.SUPPLYSIZEMIN;
	                    	supplyMax = data.SUPPLYSIZEMAX;
	                    	condition1 = data.CONDITION1;
	                    	condition2 = data.CONDITION2; 
	                    	
	                    	if(condition1==null) condition1 = "";
	                    	if(condition2==null) condition2 = "";
	                    	if(supplyMin==null) supplyMin = "";
	                    	if(supplyMax==null) supplyMax = ""; 
	                    	if(supplyMin==0) supplyMin = supplyMin.toString();
	                    	if(supplyMax==0) supplyMax = supplyMax.toString();
	                    	
	                    	var hAe = ">=";
	                    	var textHtml = "";

	                    	if (supplyMax == "" && condition1 != "") {
	                    		textHtml = "x" + " " + condition1 + " " + supplyMin;
	                    	} else if (supplyMin == "" && condition2 != "") {
	                    		textHtml = "x" + " " + condition2 + " " + supplyMax;
	                    	} else if (supplyMax != "" && supplyMin != "") {
	                    		if (condition1 == ">") {
	                    			condition1 = "<";
	                    		} else if (condition1 == hAe) {
	                    			condition1 = "<=";
	                    		}
	                    		
	                    		textHtml = supplyMin + " " + condition1 + " x " + condition2 + " " + supplyMax;
	                    	} else {
	                    		textHtml = "-";
	                    	}

	                    	var btnHtml = "<div class='supplyModify' style='cursor:pointer' onclick='javascript:modifySupply(\"" 
	                    		+ supplyMin + "\"," + "\"" + supplyMax + "\"," + "\"" + condition1 + "\"," + "\"" + condition2 + "\"" + ")'>" + textHtml +"</div>";
	                    		                    	
	                    	var tplText = new Ext.Template(btnHtml);
	                    	
	                        return tplText.apply();
	                        
	                    }
	                    
					},{
						header:fmtMsgArr[3],
						dataIndex:'SERVICECHARGE',
						width: 120,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'serviceChange',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[24],
						dataIndex:'TRANSMISSIONNETWORKCHARGE',
						width: 140,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'tranmission',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[19],
						dataIndex:'DISTRIBUTIONNETWORKCHARGE',
						width: 110,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'distribution',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[20],
						dataIndex:'ENERGYDEMANDCHARGE',
						width: 90,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'energy',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[9],
						dataIndex:'ACTIVEENERGYCHARGE',
						width: 150,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'activeEnergy',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[23],
						dataIndex:'REACTIVEENERGYCHARGE',
						width: 120,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'reactiveEnergy',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[18],
						dataIndex:'ADMINCHARGE',
						width: 150,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'adminCharge',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[22],
						dataIndex:'RATEREBALANCINGLEVY',
						width: 140,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'rateRebalance',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[21],
						dataIndex:'MAXDEMAND',
						width: 110,
						editable: edit,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'maxDemand',
                            allowBlank : true
                        })
					/* },{
						header:fmtMsgArr[25], //nhil로 변경..?
						dataIndex:'NHIL',
						editable: edit,
						width: 90,
						align:'center',
						editor: new Ext.form.TextField({
                            id : 'nhil',
                            allowBlank : true
                        })
					},{
						header:fmtMsgArr[26], //getfund로 변경?
						dataIndex:'GETFUND',
						editable: edit,
						width: 110,
						align:'right',
						editor: new Ext.form.TextField({
                            id : 'getfund',
                            allowBlank : true 
                        }) */
					},{
						header:'Delete', 
						width: 60,
						//editable: edit,
						//layout: 'fit',
						align:'center',
						renderer: function(value, metaData, record, index) {
	                    	var data = record.data;
	                    	tariffId = data.ID;
	                    	var btnHtml = "<div class='am_button'> <a href='#;' onclick='javascript:deleteRow();' >Delete</a> </div>";
	                        var tplBtn = new Ext.Template(btnHtml);
	                        return tplBtn.apply();
	                    } 
					}
				],
				
				defaults: {
					sortable: false,
				},
				 
			});
	        }
	        
	        if(fileType == 'Water'){
	        	tariffModelWm  = new Ext.grid.ColumnModel({
					columns:[
						{
							header:'Tariff', 
							dataIndex:'TARIFFTYPE',
							editable: edit,
							width: 180,
			                editor: {
	                            id : 'tariffTypeEdit',
	                            xtype: 'textfield',
	                            allowBlank : false,
	                        },
	                        renderer : function(value, me, record, rowNumber, columnIndex, store) {
	                        	var tariffType_name = record.data.TARIFFTYPE;
		                        	if(tariffType_name=="PRE-AGRICULTURE")
		                       			return setColorFrontTag + pink + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-GOVERNMENT")
		                       			return setColorFrontTag + green  + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-COMMERCIAL")
		                       			return setColorFrontTag + grey  + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-INDUSTRIAL")
		                       			return setColorFrontTag + gold + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-RESIDENTIAL")
		                       			return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="Non Residential")
		                       			return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name="Residential")
		                       			return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
		                        	else
		                        		return value;
		                        	
	               				}
						},
						{
							header:'TARIFFTYPEID', 
							dataIndex:'TARIFFTYPEID',
							hidden:true,
							renderer: function (value, meta, record, rowIndex, colIndex, store) {
								var tariffType_name = record.data.TARIFFTYPE;
								var count = tariffStore.getCount();
			                    var data = [];
			                    for(var i=0; i < count; i++){
			                    	data[i] = tariffStore.data.items[i].data;
			                    	if(data[i].TARIFFTYPE == tariffType_name){
			                    		 TARIFFTYPEID = data[i].TARIFFTYPEID;
			                    		 break;
			                    	}
			                    }  	
			    				record.data.TARIFFTYPEID = TARIFFTYPEID;
							}
						},{
							header:'Supply Size(x)', 
							dataIndex:'SUPPLYSIZE',
							width: 140,
							editable: edit,
							align: 'center',
							renderer: function(value, metaData, record, index) {
		                    	var data = record.data;
		                    	supplyMin = data.SUPPLYSIZEMIN;
		                    	supplyMax = data.SUPPLYSIZEMAX;
		                    	condition1 = data.CONDITION1;
		                    	condition2 = data.CONDITION2; 
		                    	
		                    	if(condition1==null) condition1 = "";
		                    	if(condition2==null) condition2 = "";
		                    	if(supplyMin==null) supplyMin = "";
		                    	if(supplyMax==null) supplyMax = ""; 
		                    	if(supplyMin==0) supplyMin = supplyMin.toString();
		                    	if(supplyMax==0) supplyMax = supplyMax.toString();
		                    	
		                    	var hAe = ">=";
		                    	var textHtml = "";

		                    	if (supplyMax == "" && condition1 != "") {
		                    		textHtml = "x" + " " + condition1 + " " + supplyMin;
		                    	} else if (supplyMin == "" && condition2 != "") {
		                    		textHtml = "x" + " " + condition2 + " " + supplyMax;
		                    	} else if (supplyMax != "" && supplyMin != "") {
		                    		if (condition1 == ">") {
		                    			condition1 = "<";
		                    		} else if (condition1 == hAe) {
		                    			condition1 = "<=";
		                    		}
		                    		
		                    		textHtml = supplyMin + " " + condition1 + " x " + condition2 + " " + supplyMax;
		                    	} else {
		                    		textHtml = "-";
		                    	}

		                    	var btnHtml = "<div class='supplyModify' style='cursor:pointer' onclick='javascript:modifySupply(\"" 
		                    		+ supplyMin + "\"," + "\"" + supplyMax + "\"," + "\"" + condition1 + "\"," + "\"" + condition2 + "\"" + ")'>" + textHtml +"</div>";
		                    		                    	
		                    	var tplText = new Ext.Template(btnHtml);
		                    	
		                        return tplText.apply();
		                        
		                    }
		                    
						},{
							header:'Service Charge', 
							dataIndex:'SERVICECHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'serviceChange',
	                            allowBlank : true
	                        })
						},{
							header:'Transmission', 
							dataIndex:'TRANSMISSIONNETWORKCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'tranmission',
	                            allowBlank : true
	                        })
						},{
							header:'Distribution', 
							dataIndex:'DISTRIBUTIONNETWORKCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'distribution',
	                            allowBlank : true
	                        })
						},{
							header:'Energy', 
							dataIndex:'ENERGYDEMANDCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'energy',
	                            allowBlank : true
	                        })
						},{
							header:'Active Energy', 
							dataIndex:'ACTIVEENERGYCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'activeEnergy',
	                            allowBlank : true
	                        })
						},{
							header:'Reative', 
							dataIndex:'REACTIVEENERGYCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'reactiveEnergy',
	                            allowBlank : true
	                        })
						},{
							header:'Admin Charge', 
							dataIndex:'ADMINCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'adminCharge',
	                            allowBlank : true
	                        })
						},{
							header:'Rate', 
							dataIndex:'RATEREBALANCINGLEVY',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'rateRebalance',
	                            allowBlank : true
	                        })
						},{
							header:'MAXDEMAND', 
							dataIndex:'MAXDEMAND',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'maxDemand',
	                            allowBlank : true
	                        })
						},{
							header:'SEASON', 
							dataIndex:'SEASON',
							editable: false,
							width: 100,
							align:'center',
							editor: new Ext.form.TextField({
	                            id : 'season',
	                            allowBlank : true
	                        })
						},{
							header:'TOU Rate', 
							dataIndex:'PEAKTYPE',
							editable: false,
							width: 100,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'tourateId',
	                            allowBlank : true
	                        })
						},{
							header:'Hour', 
							dataIndex:'HOUR',
							editable: edit,
							width: 100,
							align:'center',
							editor: new Ext.form.TextField({
	                            id : 'hour',
	                            allowBlank : true
	                        })
						},{
							header:'Delete', 
							width: 60,
							//editable: edit,
							//layout: 'fit',
							align:'center',
							renderer: function(value, metaData, record, index) {
		                    	var data = record.data;
		                    	tariffId = data.ID;
		                    	var btnHtml = "<div class='am_button'> <a href='#;' onclick='javascript:deleteRow();' >Delete</a> </div>";
		                        var tplBtn = new Ext.Template(btnHtml);
		                        return tplBtn.apply();
		                    } 
						}
					],
					
					defaults: {
						sortable: false,
					},
					 
				});
	        }
	        
	        if(fileType == 'Gas'){
	        	tariffModelGm  = new Ext.grid.ColumnModel({
					columns:[
						{
							header:'Tariff', 
							dataIndex:'TARIFFTYPE',
							editable: edit,
							width: 180,
			                editor: {
	                            id : 'tariffTypeEdit',
	                            xtype: 'textfield',
	                            allowBlank : false,
	                        },
	                        renderer : function(value, me, record, rowNumber, columnIndex, store) {
	                        	var tariffType_name = record.data.TARIFFTYPE;
		                        	if(tariffType_name=="PRE-AGRICULTURE")
		                       			return setColorFrontTag + pink + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-GOVERNMENT")
		                       			return setColorFrontTag + green  + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-COMMERCIAL")
		                       			return setColorFrontTag + grey  + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-INDUSTRIAL")
		                       			return setColorFrontTag + gold + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="PRE-RESIDENTIAL")
		                       			return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name=="Non Residential")
		                       			return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
		                        	if(tariffType_name="Residential")
		                       			return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
		                        	else
		                        		return value;
		                        	
	               				}
						},
						{
							header:'TARIFFTYPEID', 
							dataIndex:'TARIFFTYPEID',
							hidden:true,
							renderer: function (value, meta, record, rowIndex, colIndex, store) {
								var tariffType_name = record.data.TARIFFTYPE;
								var count = tariffStore.getCount();
			                    var data = [];
			                    for(var i=0; i < count; i++){
			                    	data[i] = tariffStore.data.items[i].data;
			                    	if(data[i].TARIFFTYPE == tariffType_name){
			                    		 TARIFFTYPEID = data[i].TARIFFTYPEID;
			                    		 break;
			                    	}
			                    }  
		    					record.data.TARIFFTYPEID = TARIFFTYPEID;
							}
						},{
							header:'Supply Size(x)', 
							dataIndex:'SUPPLYSIZE',
							width: 140,
							editable: edit,
							align: 'center',
							renderer: function(value, metaData, record, index) {
		                    	var data = record.data;
		                    	supplyMin = data.SUPPLYSIZEMIN;
		                    	supplyMax = data.SUPPLYSIZEMAX;
		                    	condition1 = data.CONDITION1;
		                    	condition2 = data.CONDITION2; 
		                        
		                    	if(condition1==null) condition1 = "";
		                    	if(condition2==null) condition2 = "";
		                    	if(supplyMin==null) supplyMin = "";
		                    	if(supplyMax==null) supplyMax = ""; 
		                    	if(supplyMin==0) supplyMin = supplyMin.toString();
		                    	if(supplyMax==0) supplyMax = supplyMax.toString();
		                    	
		                    	var hAe = ">=";
		                    	var textHtml = "";

		                    	if (supplyMax == "" && condition1 != "") {
		                    		textHtml = "x" + " " + condition1 + " " + supplyMin;
		                    	} else if (supplyMin == "" && condition2 != "") {
		                    		textHtml = "x" + " " + condition2 + " " + supplyMax;
		                    	} else if (supplyMax != "" && supplyMin != "") {
		                    		if (condition1 == ">") {
		                    			condition1 = "<";
		                    		} else if (condition1 == hAe) {
		                    			condition1 = "<=";
		                    		}
		                    		
		                    		textHtml = supplyMin + " " + condition1 + " x " + condition2 + " " + supplyMax;
		                    	} else {
		                    		textHtml = "-";
		                    	}

		                    	var btnHtml = "<div class='supplyModify' style='cursor:pointer' onclick='javascript:modifySupply(\"" 
		                    		+ supplyMin + "\"," + "\"" + supplyMax + "\"," + "\"" + condition1 + "\"," + "\"" + condition2 + "\"" + ")'>" + textHtml +"</div>";
		                    		                    	
		                    	var tplText = new Ext.Template(btnHtml);
		                    	
		                        return tplText.apply();
		                        
		                    }
		                    
						},{
							header:'Service Charge', 
							dataIndex:'SERVICECHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'serviceChange',
	                            allowBlank : true
	                        })
						},{
							header:'Transmission', 
							dataIndex:'TRANSMISSIONNETWORKCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'tranmission',
	                            allowBlank : true
	                        })
						},{
							header:'Distribution', 
							dataIndex:'DISTRIBUTIONNETWORKCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'distribution',
	                            allowBlank : true
	                        })
						},{
							header:'Energy', 
							dataIndex:'ENERGYDEMANDCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'energy',
	                            allowBlank : true
	                        })
						},{
							header:'Active Energy', 
							dataIndex:'ACTIVEENERGYCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'activeEnergy',
	                            allowBlank : true
	                        })
						},{
							header:'Reative', 
							dataIndex:'REACTIVEENERGYCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'reactiveEnergy',
	                            allowBlank : true
	                        })
						},{
							header:'Admin Charge', 
							dataIndex:'ADMINCHARGE',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'adminCharge',
	                            allowBlank : true
	                        })
						},{
							header:'Rate', 
							dataIndex:'RATEREBALANCINGLEVY',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'rateRebalance',
	                            allowBlank : true
	                        })
						},{
							header:'MAXDEMAND', 
							dataIndex:'MAXDEMAND',
							width: 120,
							editable: edit,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'maxDemand',
	                            allowBlank : true
	                        })
						},{
							header:'SEASON', 
							dataIndex:'SEASON',
							editable: edit,
							width: 100,
							align:'center',
							editor: new Ext.form.TextField({
	                            id : 'season',
	                            allowBlank : true
	                        })
						},{
							header:'TOU Rate', 
							dataIndex:'PEAKTYPE',
							editable: edit,
							width: 100,
							align:'right',
							editor: new Ext.form.TextField({
	                            id : 'tourateId',
	                            allowBlank : true
	                        })
						},{
							header:'Hour', 
							dataIndex:'HOUR',
							editable: edit,
							width: 100,
							align:'center',
							editor: new Ext.form.TextField({
	                            id : 'hour',
	                            allowBlank : true
	                        })
						},{
							header:'Delete', 
							width: 60,
							//editable: edit,
							//layout: 'fit',
							align:'center',
							renderer: function(value, metaData, record, index) {
		                    	var data = record.data;
		                    	tariffId = data.ID;
		                    	var btnHtml = "<div class='am_button'> <a href='#;' onclick='javascript:deleteRow();' >Delete</a> </div>";
		                        var tplBtn = new Ext.Template(btnHtml);
		                        return tplBtn.apply();
		                    } 
						}
					],
					
					defaults: {
						sortable: false,
					},
					 
				});
	        }
	        
			//Column Model
			
			if(fileType=='Electricity'){
				if(tariffInstanceOn == false){
					tariffGridPanel = new Ext.grid.EditorGridPanel({
						store: tariffStore,
						colModel: tariffModelEm,
						autoScroll: true,
						scroll: true,
						width: 'auto',
						height: 450,
						cls: 'grid-row-span',
						layout: 'fit',
						//stripeRows: true,
						columnLines: true,
						loadMask : {
	                        msg : 'loading...'
	                    },
						clicksToEdit : 1,
						renderTo: 'chargeMgmtEm',
						hideHeaders: false,
						border: false,
						sm : new Ext.grid.RowSelectionModel({
			    			singleSelect:true,
			    			moveEditorOnEnter : false,
			    			listeners: {
			                    rowselect: function(selectionModel, row, rec) {
			                    	var data = rec.data;
			                    	supplyMin = data.SUPPLYSIZEMIN;
			                    	supplyMax = data.SUPPLYSIZEMAX;
			                    	condition1 = data.CONDITION1;
			                    	condition2 = data.CONDITION2;
			                    	
			                    }
			                }
			    		}), 
					});//Grid Panel End
					tariffInstanceOn = true;
				} else{
					tariffGridPanel.reconfigure(tariffStore, tariffModelEm);
				}
			}
			if(fileType=='Water'){
				if(tariffWmInstanceOn == false){
					tariffGridPanel = new Ext.grid.EditorGridPanel({
						store: tariffStore,
						colModel: tariffModelWm,
						autoScroll: true,
						scroll: true,
						width: 'auto',
						height: 450,
						cls: 'grid-row-span',
						layout: 'fit',
						//stripeRows: true,
						columnLines: true,
						loadMask : {
	                        msg : 'loading...'
	                    },
						clicksToEdit : 1,
						renderTo: 'chargeMgmtWm',
						hideHeaders: false,
						border: false,
						sm : new Ext.grid.RowSelectionModel({
			    			singleSelect:true,
			    			moveEditorOnEnter : false,
			    			listeners: {
			                    rowselect: function(selectionModel, row, rec) {
			                    	var data = rec.data;
			                    	supplyMin = data.SUPPLYSIZEMIN;
			                    	supplyMax = data.SUPPLYSIZEMAX;
			                    	condition1 = data.CONDITION1;
			                    	condition2 = data.CONDITION2;
			                    	
			                    	
			                    }
			                }
			    		}), 
			    		
					});//Grid Panel End
					tariffWmInstanceOn = true;
					
				} else{
					tariffGridPanel.reconfigure(tariffStore, tariffModelWm);
				}
			}
			if(fileType=='Gas'){
				if(tariffInstanceOn == false){
					tariffGridPanel = new Ext.grid.EditorGridPanel({
						store: tariffStore,
						colModel: tariffModelGm,
						autoScroll: true,
						scroll: true,
						width: 'auto',
						height: 450,
						cls: 'grid-row-span',
						layout: 'fit',
						//stripeRows: true,
						columnLines: true,
						loadMask : {
	                        msg : 'loading...'
	                    },
						clicksToEdit : 1,
						renderTo: 'chargeMgmtGm',
						hideHeaders: false,
						border: false,
						sm : new Ext.grid.RowSelectionModel({
			    			singleSelect:true,
			    			moveEditorOnEnter : false,
			    			listeners: {
			                    rowselect: function(selectionModel, row, rec) {
			                    	var data = rec.data;
			                    	supplyMin = data.SUPPLYSIZEMIN;
			                    	supplyMax = data.SUPPLYSIZEMAX;
			                    	condition1 = data.CONDITION1;
			                    	condition2 = data.CONDITION2;
			                    	
			                    	
			                    }
			                }
			    		}), 
			    		
					});//Grid Panel End
					tariffInstanceOn = true;
					
				} else{
					tariffGridPanel.reconfigure(tariffStore, tariffModelGm);
				}
			}
			
        }
        
        function modifySupply(supplyMin,supplyMax,condition1,condition2){
        	
        	if(condition1=="<")
        		condition1 = ">";
        	else if(condition1=="<=")
        		condition1= ">=";
        	/* else if(condition1=="")
        		condtion1= ""; */
        		
            var supplyWin = new Ext.Window({
            	title: 'Modify Supply Size(x)',
                id: 'supplyWin',
                autoScroll: true,
                modal: true, closable:true, resizable: true,
                border:true, plain:false,  
                width: 300, height: 200,
                //closeAction:'hide',
                //html: html,
                items  : [{
                    xtype: 'panel',
                    frame: false, border: false,
                    items:{
                      id: 'modifyTariff_form',
                      xtype: 'form',
                      bodyStyle:'padding:10px',
                      labelWidth: 100,
                      frame: false, border: false,
                      items: [
                    	  {
                        	  xtype: 'textfield',
                        	  fieldLabel: 'Min value', width: 100,
                        	  id:'min_val', name: 'min_val', value:supplyMin,
                        	  listeners: {
		                          change : function(record,value){
		                        	  Ext.getCmp('min_val').setValue(value);
		                        	  if(Ext.getCmp('min_val').value=='' || Ext.getCmp('min_val').value==null)
		                        	 	 Ext.getCmp('min_id').setValue("");
		                          }
		                        }
                          },{
	                        xtype: 'combo', width: 50, 
	                        id:'min_id', name: 'min_name', value:condition1,                               
	                        fieldLabel: 'Min x', triggerAction: 'all', editable: false, mode: 'local',
	                        store: new Ext.data.JsonStore({
	                        	  root:'datas',
	                        	  fields: ['code','name'],
	                        	  //autoLoad: true,
	                        	  data: {datas: [
	                        		  {"code":'',"name":'-'},
	     						  	 {"code":1,"name":'>'},
	     						  	{"code":2,"name":'>='}
	                        	  ]},
	                          }),
	                          valueField: 'code', displayField: 'name',
		                      anchor: '100%',
		                      listeners: {
		                    	  select : function(combo, record, index){
		                    		  if(record.data.name=='-'){
		                        		  Ext.getCmp('min_id').setValue("");
		                        		  Ext.getCmp('min_val').setValue("");
		                    		  }
		                    		  else
		                        	  	  Ext.getCmp('min_id').setValue(record.data.name);
		                          },
		                          change : function(record, value){
		                        	  if(Ext.getCmp('min_id').value=='' || Ext.getCmp('min_id').value==null)
		                        	  	Ext.getCmp('min_val').setValue("");
		                          },
		                          
		                     //tpl : '<tpl for="."><div class="x-combo-list-item">{name}&nbsp;</div></tpl>' 
		                       }
	                      },
	                      {
	                        xtype: 'combo', width: 50,
	                        id:'max_id', name: 'max_name', value:condition2,          
	                        fieldLabel: 'Max x', triggerAction: 'all', editable: false, mode: 'local',
	                        store: new Ext.data.JsonStore({
	                      	  root:'datas',
	                      	  fields: ['code','name'],
	                      	  autoLoad: true,
	                      	  data: {datas: [
	   						  	 {"code":'',"name":'-'},
	   						  	{"code":1,"name":'<'},
	   							 {"code":2,"name":'<='},
	                      	  ]}
	                        }),
	                        valueField: 'code', displayField: 'name',
	                        anchor: '100%',
	                        listeners: {
	                          select : function(combo, record, index){
	                        	  if(record.data.name=='-'){
	                        		  Ext.getCmp('max_id').setValue("");
	                        		  Ext.getCmp('max_val').setValue("");  
	                        	  }
	                        	  else
	                        	  	Ext.getCmp('max_id').setValue(record.data.name);
	                          },
	                          change : function(record, value){
	                        	  if(Ext.getCmp('max_id').value=='' || Ext.getCmp('max_id').value==null)
	                        		  Ext.getCmp('max_val').setValue('');
	                          }
	                        }
                      },{
                    	  xtype: 'textfield', width: 100,
                    	  fieldLabel: 'Max value',
                    	  id:'max_val', name: 'max_val', value:supplyMax,   
                    	  listeners: {
	                          change : function(record,value){
	                        	  Ext.getCmp('max_val').setValue(value);
	                        	  if(Ext.getCmp('max_val').value=='' || Ext.getCmp('max_val').value==null)
	                        	 	  Ext.getCmp('max_id').setValue('');
	                          }
	                        }
                      },
                      ]
                    }
                }],
                buttons: [
                	{
                    	text: 'Ok',
                    	handler: function() {
                         	var record = tariffGridPanel.getSelectionModel().getSelected();

                         	var SUPPLYSIZEMIN = Ext.getCmp('min_val').value;
                         	var SUPPLYSIZEMAX = Ext.getCmp('max_val').value;
                         	var CONDITION1 = Ext.getCmp('min_id').value;
                        	var CONDITION2 = Ext.getCmp('max_id').value;
                        		
                        	var flag = true;
                        	if(SUPPLYSIZEMAX!='' && CONDITION2!=''){
                        		var MIN = parseInt(Ext.getCmp('min_val').value);
                              	var MAX = parseInt(Ext.getCmp('max_val').value);	
                        	 if(MIN >= MAX)  {
                               	  	  Ext.Msg.alert("","Please check Min and Max values");
                               		  flag = false;
                               	}
                           	}
                        	
                        	if(SUPPLYSIZEMAX=="" && CONDITION2!=""){
                        		Ext.Msg.alert("","Please check the values");
                         		flag = false;
                        	}
                        	if(CONDITION2=="" && SUPPLYSIZEMAX!=""){
                        		Ext.Msg.alert("","Please check the values");
                         		flag = false;
                        	}
                        	if(SUPPLYSIZEMIN=="" && CONDITION1!=""){
                        		Ext.Msg.alert("","Please check the values");
                         		flag = false;
                        	}
                        	if(CONDITION1=="" && SUPPLYSIZEMIN!=""){
                        		Ext.Msg.alert("","Please check the values");
                         		flag = false;
                        	}
                        	
                        	if(flag){
                    			supplyWin.close();
                         	
	                        	record.set("SUPPLYSIZEMIN",SUPPLYSIZEMIN);
	                        	record.set("SUPPLYSIZEMAX",SUPPLYSIZEMAX);
	                        	record.set("CONDITION1",CONDITION1);
	                        	record.set("CONDITION2",CONDITION2);
                        	}
                    	}
                    }, {
                        text: '<fmt:message key="aimir.cancel"/>',
                        handler: function() {
                        	supplyWin.close();
                        }
                    }
                   ]
            });
        	  	
      
	       			
        	supplyWin.show(this);

        } 
        

       	// 요금관리 - 조회조건 처리
        function getCondition() {
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = sId;
            condArray[cnt++] = supplyType;
            condArray[cnt++] = $('#supplierType').val();
            condArray[cnt++] = $('#yyyymmddCombo').val();

            return condArray;
        }

        // 요금관리 - request 전송
        function send() {
/*              if (supplyType == SupplierType.Electricity) {
                if (grid1.requestSend) grid1.requestSend();
                else send.defer(300);
            }
            else if (supplyType == SupplierType.Gas) {
                if (grid2.requestSend) grid2.requestSend();
                else send.defer(300);
            }
            else if (supplyType == SupplierType.Water) {
                if (grid3.requestSend) grid3.requestSend();
                else send.defer(300);
            }  */
        }

        // 요금관리 - 수정
        function updateData() {
        	var flag = true;
        	var date = electricDate.datepicker('getDate');
            date = $.format.date(date, 'yyyyMMdd');
            if (confirm('Applied Date will be '+date+'.'+' <fmt:message key="aimir.msg.updateconfirm"/> ')) {
				
                if (supplyType == SupplierType.Electricity) {
                    emergePre();
                 
                    var count = tariffStore.getCount();
                    var data = [];
                    for(var i=0; i < count; i++){
                    	data[i] = tariffStore.data.items[i].data;
                    	if(data[i].TARIFFTYPEID == null)
                    		flag = false;
                    } 
                    
                    //var date = electricDate.datepicker('getDate');
                    //date = $.format.date(date, 'yyyyMMdd');
                    data = JSON.stringify(data);
                   
                    if(!flag){
                    	Ext.Msg.alert("","Please insert Tariff Type");
                    	hide();
                    	
                    }else{
	                    if (supplyType == SupplierType.Electricity) {
	                        $.post('${ctx}/gadget/system/supplier/updateTariffTable.do',
	                            {data: data,
	                            date: date,
	                            
	                            },
	                            function(json) {
	                                if (json.result == 'success') {
	                                    getYyyymmddList(SupplierType.Electricity,
	                                        function() {
	                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
	                                            hide();
	                                        });
	                                } else {
	                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.systemError'/>");
	                                    hide();
	                                }
	                            });
	                    }
                    }
                }
                if (blnImport) {
                    if (supplyType == SupplierType.Gas || supplyType == SupplierType.Heat) {
                    	emergePre();
                        
                        var count = tariffStore.getCount();
                        var data = [];
                        for(var i=0; i < count; i++){
                        	data[i] = tariffStore.data.items[i].data;
                        	if(data[i].TARIFFTYPEID == null)
                        		flag = false;
                        } 
                        
                        var date = electricDate.datepicker('getDate');
                        date = $.format.date(date, 'yyyyMMdd');
                        data = JSON.stringify(data);
                       
                        if(!flag){
                        	Ext.Msg.alert("","Please insert Tariff Type");
                        	hide();
                        	
                        }else{
    	                    if (supplyType == SupplierType.Gas || supplyType == SupplierType.Heat) {
    	                        $.post('${ctx}/gadget/system/supplier/updateTariffTable.do',
    	                            {data: data,
    	                            date: date,
    	                            
    	                            },
    	                            function(json) {
    	                                if (json.result == 'success') {
    	                                    getYyyymmddList(SupplierType.Gas,
    	                                        function() {
    	                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
    	                                            hide();
    	                                        });
    	                                } else {
    	                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.systemError'/>");
    	                                    hide();
    	                                }
    	                            });
    	                    }
                        }
                    }
                    else if (supplyType == SupplierType.Water) {
                    	emergePre();
                        
                        var count = tariffStore.getCount();
                        var data = [];
                        for(var i=0; i < count; i++){
                        	data[i] = tariffStore.data.items[i].data;
                        	if(data[i].TARIFFTYPEID == null)
                        		flag = false;
                        } 
                        
                        var date = electricDate.datepicker('getDate');
                        date = $.format.date(date, 'yyyyMMdd');
                        data = JSON.stringify(data);
                       
                        if(!flag){
                        	Ext.Msg.alert("","Please insert Tariff Type");
                        	hide();
                        	
                        }else{
    	                    if (supplyType == SupplierType.Water) {
    	                        $.post('${ctx}/gadget/system/supplier/updateTariffTable.do',
    	                            {data: data,
    	                            date: date,
    	                            
    	                            },
    	                            function(json) {
    	                                if (json.result == 'success') {
    	                                    getYyyymmddList(SupplierType.Water,
    	                                        function() {
    	                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
    	                                            hide();
    	                                        });
    	                                } else {
    	                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.systemError'/>");
    	                                    hide();
    	                                }
    	                            });
    	                    }
                        }
                    }
                    blnImport = false;
                } else {
                    if (supplyType == SupplierType.Gas || supplyType == SupplierType.Heat) {
                    	emergePre();
                        
                        var count = tariffStore.getCount();
                        var data = [];
                        for(var i=0; i < count; i++){
                        	data[i] = tariffStore.data.items[i].data;
                        	if(data[i].TARIFFTYPEID == null)
                        		flag = false;
                        } 
                        
                        var date = electricDate.datepicker('getDate');
                        date = $.format.date(date, 'yyyyMMdd');
                        data = JSON.stringify(data);
                       
                        if(!flag){
                        	Ext.Msg.alert("","Please insert Tariff Type");
                        	hide();
                        	
                        }else{
    	                    if (supplyType == SupplierType.Gas) {
    	                        $.post('${ctx}/gadget/system/supplier/updateTariffTable.do',
    	                            {data: data,
    	                            date: date,
    	                            
    	                            },
    	                            function(json) {
    	                                if (json.result == 'success') {
    	                                    getYyyymmddList(SupplierType.Gas,
    	                                        function() {
    	                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
    	                                            hide();
    	                                        });
    	                                } else {
    	                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.systemError'/>");
    	                                    hide();
    	                                }
    	                            });
    	                    }
                        }
                    }
                    else if (supplyType == SupplierType.Water) {
                    	emergePre();
                        
                        var count = tariffStore.getCount();
                        var data = [];
                        for(var i=0; i < count; i++){
                        	data[i] = tariffStore.data.items[i].data;
                        	if(data[i].TARIFFTYPEID == null)
                        		flag = false;
                        } 
                        
                        var date = electricDate.datepicker('getDate');
                        date = $.format.date(date, 'yyyyMMdd');
                        data = JSON.stringify(data);
                        
	                   /*  var data = grid3.getGridData();
	                    var date = electricDate.datepicker('getDate');
	                    date = $.format.date(date, 'yyyyMMdd');
	                    var data = JSON.stringify(data); */
                        if(!flag){
                        	Ext.Msg.alert("","Please insert Tariff Type");
                        	hide();
                        	
                        }else{
		                    if (supplyType == SupplierType.Water) {
		                        $.post('${ctx}/gadget/system/supplier/updateTariffWMTable.do',
		                            {data: data,
		                            date: date},
		                            function(json) {
		                                if (json.result == 'success') {
		                                    getYyyymmddList(SupplierType.Water,
		                                        function() {
		                                            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
		                                            hide();
		                                        });
		                                } else {
		                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.hems.systemError'/>");
		                                    hide();
		                                }
		                            });	                            
		                    }
                   	 }
                    } // ~else if (supp...
                } // ~else {
            }
        }

        //키입력체크
        document.onkeydown = CheckKeyPress;
        document.onkeyup = CheckKeyPress;
        function CheckKeyPress() {
            //리턴 키 입력시
            if (event.keyCode == 13) {
                event.keyCode = 0;
                return false;
            }
        }

        function IHDSendTariffMessage() {

            $.getJSON('${ctx}/gadget/system/supplier/sendIHDTariffMessage.do'
                    , {}
                    , function (json) {
                        //IHD그룹이 없을경우 IHD관련 리턴메세지를 보여주지 않는다.
                        if (!(json.cntSuccess == 0 && json.cntFail == 0)) {
                            //결과값 리턴
                             Ext.Msg.alert('<fmt:message key='aimir.message'/>',"IHD SUCCESS ["+json.cntSuccess+"], FAIL ["+json.cntFail+"]");
                        }
                    });
        }

        /* function addRow() {
            grid1.focus();
            grid1.addRow();
        } */
        function addRow(){
        	 var store = tariffGridPanel.getStore();
             var Plant = store.recordType;
             var textHtml = "-";
            
             var p = new Plant({
                 ID : "",
                 SEASON : "",
                 PEAKTYPE : "",
                 //STARTHOUR : null,
                 //ENDHOUR : null,
                 SUPPLYSIZEMIN : null,
                 SUPPLYSIZEMAX : null,
                 CONDITION1 : null,
                 CONDITION2 : null,
                 SERVICECHARGE : "",
                 TRANSMISSIONNETWORKCHARGE : "",
                 DISTRIBUTIONNETWORKCHARGE : "",
                 ENERGYDEMANDCHARGE : "",
                 ACTIVEENERGYCHARGE : "",
                 REACTIVEENERGYCHARGE : "",
                 ACTIVEENERGYCHARGE : "",
                 RATEREBALANCINGLEVY : "",
                 MAXDEMAND : "",
                 ADMINCHARGE : "",
                 TARIFFTYPE : "",
                 TARIFFTYPEID : "",
                 HOUR : null
                 //status : "add"
             });
             var length = store.getCount();
             tariffGridPanel.stopEditing();
             tariffStore.insert(length, p);
             tariffGridPanel.startEditing(length, 0);
             tariffGridPanel.getSelectionModel().selectLastRow();
        }
        
        function addRowWm() {
            grid3.focus();
            grid3.addRow();
        }
		
        function deleteRow() {
            var record = tariffGridPanel.getSelectionModel().getSelected();

            if (record == null) {
                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "Do you want to delete from Tariff?");
                return;
            }

                // 데이터가 있는 경우 삭제안됨
            	 /* if (record.get("") != "" && record.get("") > 0) {
                     Ext.Msg.alert("<fmt:message key="aimir.message"/>", "You can't delete. Data exists");
                     hide();
                     return;
                 } */
                Ext.MessageBox.confirm('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.msg.wantdelete"/>',
                        function(btn) {
                            if (btn == 'yes') {
                                /* $.getJSON("${ctx}/gadget/system/deleteTariffRow.do"
                                        ,{tariffId : record.get("ID")}
                                        ,function(json) {
                                            hide();
                                            if (json.result == "success") { */
                                             if (record.get("") != "" && record.get("") > 0) {
                                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "You can't delete. Data exists");
                                                hide();
                                                return;
                                            } 
                                            tariffStore.remove(record);
                                               
                                           /*  } else {
                                                Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key='aimir.hems.alert.failDelete'/>");
                                            }
                                }); */
                            } else if(btn == 'no'){
                            	hide();
                            }
                        });
                //getTariffGrid();
                //tariffStore.reload();
        }

        function addEMTariffType(tariffName) {
            $.post("${ctx}/gadget/system/supplier/addEMTariffType.do",
            {
              supplierId: supplierObj.id,
              tariffType: tariffName,
              energyType: SupplierType.Electricity
            }, function(json) {
                if (json.result == 'success') {

                }
            });
        }

        /* function deleteRow() {
            grid1.deleteRow();
        } */
        
        /* function deleteRowWm() {
            grid3.deleteRow();
        } */
        
		function setTab(name){
			if(name == "MOE") {
				$('#_pay').hide();
			}
		}
        
        function cmdGetTariff() {

      	  	var tariffType = '';
            var searchWin = new Ext.Window({
              title: '<b>STS Setting</b>',
              modal: true, closable:true, resizable: true,
              width:300, height:130,
              border:true, plain:false,                      
              items:[{
                  xtype: 'panel',
                  frame: false, border: false,
                  items:{
                    id: 'getTariffMode_form',
                    xtype: 'form',
                    bodyStyle:'padding:10px',
                    labelWidth: 100,
                    frame: false, border: false,
                    items: [{
                      xtype: 'label', html:'<div style="text-align:left;">' + 'Please select tariff mode.' +'</div>',  anchor: '100%'
                    },{
                      xtype: 'combo',
                      id:'tariffMode_id', name: 'tariffMode_name', value:'Select...',          
                      fieldLabel: 'Tariff mode', triggerAction: 'all', editable: false, mode: 'local',
                      store: new Ext.data.JsonStore({
                    	  root:'datas',
                    	  fields: ['code','name'],
                    	  autoLoad: true,
                    	  data: {datas: [
 						  	 {"code":0,"name":'Current tariff'},
 						  	{"code":1,"name":'Future tariff'}
                    	  ]}
                      }),
                      valueField: 'code', displayField: 'name',
                      anchor: '100%',
                      listeners: {
                        select : function(combo, record, index){
                          Ext.getCmp('tariffMode_id').setValue(record.data.name);
                          tariffType = record.data.code;
                        }
                      }
                    }]
                  }
              }],
              
              buttons: [{
                text: 'Ok',
                handler: function() {
  				  var flag = true;

              	  if((flag && (Ext.getCmp('tariffMode_id').value == null || Ext.getCmp('tariffMode_id').value == ''))
              			  || (Ext.getCmp('tariffMode_id').value == 'Select...')) {
              		  Ext.Msg.alert("","Please select Tariff mode");
              		  flag = false;
              		  return flag;
              	  }
              	  
              	  if(flag) {
              		  searchWin.close();
      	              
              		  Ext.Msg.show({
                     		title: '<b>STS Setting<b/>',
                     		msg: 'Do you want to save?',
                     		buttons : Ext.MessageBox.OKCANCEL,
                     		fn : function(btn) {
			                	if(btn == 'ok') {
			                		$.ajaxSetup({
				       	                async : true
				       	            });		                	
			                		Ext.Msg.wait('Waiting for response.', 'Wait !');
				               		
				       				$.getJSON('${ctx}/gadget/device/command/cmdGetTariff.do', {
				       					'supplierId':sId,		
				       					'target' : '',
				       					'tariffMode' : tariffType
				                       }, function(returnData) {
				                    	   Ext.Msg.hide();
				                           if(returnData.rtnStr != null && returnData.rtnStr.indexOf("FAIL") > -1) {
				                        	   Ext.Msg.show({
						                       		title: '',
						                       		msg: returnData.rtnStr,
						                       		buttons : Ext.MessageBox.OK
					                       		});   
				                           } else {
				                        	   Ext.Msg.show({
						                       		title: '',
						                       		msg: "Please check the Result on Prepayment customer gadget.",
						                       		buttons : Ext.MessageBox.OK
					                       		});  
				                           }
				                           
				                       });
			                	}
                     		}
              		  })
	              		
              		  
              	  }
                }
              }, {
                text: '<fmt:message key="aimir.cancel"/>',
                handler: function() {
              	  searchWin.close();
                }
              }]
            });

            searchWin.show(this);
        }
        
        function cmdSetTariff() {
    	  var tariffType = '';
    	  var tariffTypeName = '';
          var searchWin = new Ext.Window({
            title: '<b>STS Setting</b>',
            modal: true, closable:true, resizable: true,
            width:300, height:225,
            border:true, plain:false,                      
            items:[{
                xtype: 'panel',
                frame: false, border: false,
                items:{
                  id: 'reTypeAmount_form',
                  xtype: 'form',
                  bodyStyle:'padding:10px',
                  labelWidth: 100,
                  frame: false, border: false,
                  items: [{
                    xtype: 'label', html:'<div style="text-align:left;">' + 'Please input tariff Information.' +'</div>',  anchor: '100%'
                  },{
                      xtype: 'combo',
                      id:'tariffType_id', name: 'tariffType_name', value:'Select...',          
                      fieldLabel: 'Tariff', triggerAction: 'all', editable: false, mode: 'local',
                      store: new Ext.data.JsonStore({
                        autoLoad   : true,
                        baseParams: {serviceType : $('#supplierType option:selected').val(), supplierId : sId},
                        url: '${ctx}/gadget/system/supplier/getTariffType.do',
                        storeId: 'tariffTypeListStore',
                        root: 'result',
                        idProperty: 'name',
                        fields: ['name',{name: 'id', type: 'int'}],
                        listeners: {
                          load: function(store, records, options){
                            Ext.getCmp('tariffType_id').setValue(records[0].data.name);
                            tariffType = records[0].data.id;
                            tariffTypeName = records[0].data.name;
                          }
                        }
                      }),
                      valueField: 'id', displayField: 'name',
                      anchor: '100%',
                      listeners: {
                        render: function() {
                          this.store.load();
                        },
                        select : function(combo, record, index){
                          Ext.getCmp('tariffType_id').setValue(record.data.name);
                          tariffType = record.data.id;
                          tariffTypeName = record.data.name;
                        }
                      }
                  }, {
                    xtype: 'datefield', fieldLabel: 'Tariff Date', id: 'tariffDate_id', name: 'tariffDate_name', anchor: '100%'
                  },{
                	  xtype: 'textfield', fieldLabel: 'Gov. Subsidy Limit', id: 'condLimit1_id', name: 'condLimit1_name', anchor: '100%'  
                  },{
                	  xtype: 'textfield', fieldLabel: 'Utility Relief Limit', id: 'condLimit2_id', name: 'condLimit2_name', anchor: '100%'  
                  }]
                }
            }],
            
            buttons: [{
              text: 'Ok',
              handler: function() {
				  var flag = true;
				  if(flag && Ext.getCmp('tariffType_id').getValue() == null) {
            		  Ext.Msg.alert("","Please select Tariff");
            		  flag = false;
            		  return flag;
            	  }
				  
				  if(flag && (Ext.getCmp('tariffDate_id').value == null || Ext.getCmp('tariffDate_id').value == '')) {
            		  Ext.Msg.alert("","Please input Tariff Date.");
            		  flag = false;
            		  return flag;
            	  }

            	  if(flag && ($('#condLimit1_id').val() == null || $('#condLimit1_id').val() == '' || isNaN($('#condLimit1_id').val()))) {
            		  Ext.Msg.alert("","Please input Gove. Subsidy limit.");
            		  flag = false;
            		  return flag;
            	  }
            	  
            	  if(flag && ($('#condLimit2_id').val() == null || $('#condLimit2_id').val() == '' || isNaN($('#condLimit2_id').val()))) {
            		  Ext.Msg.alert("","Please input Utility Relief limit");
            		  flag = false;
            		  return flag;
            	  }
            	 //dd/mm/yyyy 포맷으로 저장되어 있음
          		 var yyyymmdd = Ext.getCmp('tariffDate_id').getValue().format('Ymd')
            	  var tariffParam = {
               	    'tariffType' : tariffType,
            	  	'yyyymmdd' : yyyymmdd,
            	  	'condLimit1' : $('#condLimit1_id').val(),
            	  	'condLimit2' : $('#condLimit2_id').val(),
                    'tariffTypeName' : tariffTypeName

            	  }
            	  
            	  if(flag) {
            		  searchWin.close();
            		  var tariffStr;
            		  
    	              $.getJSON('${ctx}/gadget/system/supplier/getSTSTariff.do'
    	                      , {'tariffIndexId' : tariffType,
    	            	  		'yyyymmdd' : yyyymmdd
    	              }, function(json) {
    	            	  drawTariffGrid(tariffParam, json.tariffInfo); 
                      });
            	  }
              }
            }, {
              text: '<fmt:message key="aimir.cancel"/>',
              handler: function() {
            	  searchWin.close();
              }
            }]
          });

          searchWin.show(this);
        }

        var tariffSTSGrid;
        var tariffSTSStore;
        function drawTariffGrid(tariffParam, tariffInfo) {

        	tariffSTSStore = new Ext.data.JsonStore({
                fields : [ {name:'cons'},{name:'fixedRate'},{name:'varRate'},{name:'condRate1'},{name:'condRate2'}]
            });

		   tariffSTSStore.loadData(tariffInfo);

	       var colModel = new Ext.grid.ColumnModel({
	            defaults : {
	                width : 80,
	                height : 100,
	                sortable : true
	            },
	            columns : [{
	                width : 100,
	                header : "<b>Supply Min Size(kWh)</b>",
	                dataIndex : "cons",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'cons'
                    })
	            }, {
	                header : "<b>Service Charge</b>",
	                width : 90,
	                dataIndex : "fixedRate",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'fixedRate'
                    })
	            }, {
	                header : "<b>Var Rate</b>",
	                width : 80,
	                dataIndex : "varRate",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'varRate'
                    })
	            }, {
	                header : "<b>Gove. Subsidy</b>",
	                width : 100,
	                dataIndex : "condRate1",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'condRate1'
                    })
	            }, {
	                header : "<b>Utility Relief</b>",
	                width : 100,
	                dataIndex : "condRate2",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'condRate2'
                    })
	            }]
	        });
	       
	       tariffSTSGrid = new Ext.grid.EditorGridPanel({
	   			 store: tariffSTSStore,
	   			 autoScroll : true,
	   			 loadMask: true,
	   			 colModel: colModel,
	   			 listeners: {
	   				afteredit: function(e) {
	   					
	   				}
	   			 },
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: 600,
	  		      height: 300,
	  		      tbar:[{
	  		    	  iconCls: 'icon-obis-add',
	  		    	  text: "<b><fmt:message key='aimir.add'/></b>",
	  		    	  handler: function() {
	  		    		addRecordData();
	  		    	  }
	  		      },{
	  		    	  iconCls: 'icon-obis-delete',
	  		    	  text: "<b><fmt:message key='aimir.button.delete'/></b>",
	  		    	  handler: function() {
	  		    		delRecordData();
	  		    	  }
	  		      }]
	   		  });
	
	        $('#setTariffDiv').empty();

	        var imgWin = new Ext.Window({
	            title : 'Set Tariff',
	            id : 'setTariffWinId',
	            applyTo : 'setTariffDiv',
	            autoScroll : true,
	            autoHeight : true,
	            pageX : 400,
	            pageY : 130,
	            width : 600,
	            height : 300,
	            items : tariffSTSGrid,
	            buttons : [{text : '<fmt:message key="aimir.save2"/>',
	            	handler : function() {	
	            		$.ajaxSetup({
	       	                async : true
	       	            });
	            		
	            		var records = tariffSTSStore.data.items;
	            		var flag = false;
	            		
	            		//마지막라인에 대해서 유효성 체크
	            		if(records.length-1 < 0) {
	            			Ext.Msg.alert("<fmt:message key='aimir.error'/>","<fmt:message key='aimir.data.empty'/>");
	            			flag = false;
	            			return false;
	            		}
	            		
	                   	flag = validateMandatory(tariffSTSGrid,"<fmt:message key='aimir.mandatoryValue'/>");
        	        	if(flag) {
        	        		var saveArrList = new Array();
		            		for(var i = 0; i<records.length; i++) {
		            			var saveArr = new Array();
		            			saveArr.push({
		            				'cons' : records[i].data.cons,
				                    'fixedRate' : records[i].data.fixedRate,
				                    'varRate' : records[i].data.varRate,
				                    'condRate1' : records[i].data.condRate1,
				                    'condRate2' : records[i].data.condRate2
			            		})
			            		
			            		saveArrList.push(saveArr);
		            		}
		            		
		            		Ext.Msg.show({
	                       		title: '<b>STS Setting<b/>',
	                       		msg: 'Do you want to save?',
	                       		buttons : Ext.MessageBox.OKCANCEL,
	                       		fn : function(btn) {
				                	if(btn == 'ok') {
				                		$.ajaxSetup({
					       	                async : true
					       	            });
				                		imgWin.hide(this);
				                		Ext.Msg.wait('Waiting for response.', 'Wait !');
					            		$.post('${ctx}/gadget/device/command/cmdSetTariff.do',{
											'supplierId':sId,		            			
											'target':'',
											'tariffType':tariffParam.tariffType,
											'yyyymmdd':tariffParam.yyyymmdd,
											'condLimit1':tariffParam.condLimit1,
											'condLimit2':tariffParam.condLimit2,
											'param':JSON.stringify(saveArrList)
					                    }, function(returnData) {
					                    	Ext.Msg.hide();
					                    	if(returnData.rtnStr != null && returnData.rtnStr.indexOf("FAIL") > -1) {
					                    		Ext.Msg.show({
						                       		title: '',
						                       		msg: returnData.rtnStr,
						                       		buttons : Ext.MessageBox.OK
					                       		});   
				                            } else {
				                        	   Ext.Msg.show({
						                       		title: '',
						                       		msg: "Please check the Result on Prepayment customer gadget.",
						                       		buttons : Ext.MessageBox.OK
					                       		});  
				                            }
					                    });
				                	} else {
				                		return;
				                	}
				              }
                       		}); 
        	        	}
                	}},
                	{text : '<fmt:message key="aimir.board.close"/>',
                	handler : function() {
                		
                		imgWin.hide(this);        		
                	}}],
	            closeAction : 'hide',
	            onHide : function() {
	            }
	        });
	        Ext.getCmp('setTariffWinId').show();
        }
        
       function addRecordData() {
        	$.ajaxSetup({
                async : false
            });
			var flag = true;
			
			var store = tariffSTSGrid.getStore();
			
        	if(store.data.length > 0) {
	        	var preRecord = store.data.last().data;
                
	        	flag = validate(tariffSTSGrid,tariffSTSStore.data.length-1,preRecord.cons,"<fmt:message key='aimir.mandatoryValue'/>",0);
	        	
	        	if(flag && (isNaN(preRecord.cons) || (preRecord.cons + "").indexOf(".") >= 0)) {
	        		flag=false;
	        		Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(integer type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSStore.data.length-1, 0); return false;});
	        	}
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSStore.data.length-1,preRecord.fixedRate,"<fmt:message key='aimir.mandatoryValue'/>",1);
                }
                
                if(flag && isNaN(preRecord.fixedRate)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSStore.data.length-1, 1); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSStore.data.length-1,preRecord.varRate,"<fmt:message key='aimir.mandatoryValue'/>",2);
                }
                
                if(flag && isNaN(preRecord.varRate)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSStore.data.length-1, 2); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSStore.data.length-1,preRecord.condRate1,"<fmt:message key='aimir.mandatoryValue'/>",3);
                }
                
                if(flag && isNaN(preRecord.condRate1)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSStore.data.length-1, 3); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSStore.data.length-1,preRecord.condRate2,"<fmt:message key='aimir.mandatoryValue'/>",4);
                }
                
                if(flag && isNaN(preRecord.condRate2)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSStore.data.length-1, 4); return false;});	
                }

        	}

        	if(flag) {
                var Plant = store.recordType;
	            var p = new Plant({
	                consumption : "",
	                price : "",
	            });
	            var length = store.getCount();
	            tariffSTSGrid.stopEditing();
	            tariffSTSStore.insert(length, p);
	            tariffSTSGrid.startEditing(length, 0);
	            tariffSTSGrid.getSelectionModel().selectLastRow();
        	}
        }
       
       function delRecordData() {
    	   tariffSTSGrid.stopEditing();
           var s = tariffSTSGrid.getSelectionModel().selection.record
           tariffSTSStore.remove(s);  
       }
       
       function validate(grid,rec,data,msg,row) {
		var bol;
       	if(data == null || (data+"" == "")) {
			bol = false;       		
	        	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", msg,
	    				function() {
	    			grid.startEditing(rec, row);
	        		return bol;
	    		});
       	} else {
			bol = true;       		
       		return bol;
       	}
       	return bol;
       }

       	
       function validateMandatory(grid,msg) {
    	   $.ajaxSetup({
    	        async : false
    	    });

    	   var items = grid.store.data.items;
    	   for(var i = 0; i<items.length; i++) {
    		   var obj = items[i].data;
    		   var flag = validate(grid,i,obj.cons,msg,0);
    		   if(flag && (isNaN(obj.cons) || (obj.cons + "").indexOf(".") >= 0)) {
	        		flag=false;
	        		Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(integer type)",
		    				function() { grid.startEditing(i, 0); return false;});
	        	}
               
               if(flag) {
               	flag = validate(grid,i,obj.fixedRate,"<fmt:message key='aimir.mandatoryValue'/>",1);
               }
               if(flag && isNaN(obj.fixedRate)) {
               	flag=false;
               	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { grid.startEditing(i, 1); return false;});	
               }
               
               if(flag) {
                  	flag = validate(grid,i,obj.varRate,"<fmt:message key='aimir.mandatoryValue'/>",2);
               }
               if(flag && isNaN(obj.varRate)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 2); return false;});	
                }
                  
               if(flag) {
                  	flag = validate(grid,i,obj.condRate1,"<fmt:message key='aimir.mandatoryValue'/>",3);
               }
               if(flag && isNaN(obj.condRate1)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 3); return false;});	
               }
                     
               if(flag) {
                  	flag = validate(grid,i,obj.condRate2,"<fmt:message key='aimir.mandatoryValue'/>",4);
               }
               if(flag && isNaN(obj.condRate2)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 4); return false;});	
               }
               if(!flag) {
            	   return flag;
               }
    	   }
    	   return flag;
       }
        //report window(Excel)
        var winLocationObj;
        function openExcelReport() {
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            //obj.condition = getCondition();
            obj.supplierId = supplierObj.id;
            obj.fmtMessage = "<fmt:message key="aimir.location"/>";

            if(winLocationObj)
                winLocationObj.close();
            winLocationObj = window.open("${ctx}/gadget/system/supplier/supplierLocationExportExcelPopup.do", "supplierLocationExcel", opts);
            
            winLocationObj.opener.obj = obj;
            /* $.post("${ctx}/gadget/system/supplier/getAllLocationsForExcel.do", 
                    {
                      supplierId: supplierObj.id
                    }, function(json) {
                        console.log("test end!!!");
                    }); */
        }
        
        function setTariffId(tariffType_name) {
			var supplierType = $('#supplierType option:selected').val();
            
			$.getJSON('${ctx}/gadget/system/supplier/getTariffType.do'
					,{ serviceType : supplierType, 
						supplierId : sId
					}, function (json){ 
						var result = json.result;
						
						for(var i=0; i < result.length; i++) {
							if (result[i].name == tariffType_name) {
								tariffTypeId = result[i].id;
							}
						}
					});
        	
        }

    </script>
</head>
<body>

<div id="setTariffDiv"></div>
<!-- 검색 (S)-->
<div id="search-background1">
<div id="search-default">
<ul>
    <li>
    <div id="supplierList"><select id="slist" name="list"></select></div>
    </li>
    <li>
    <div id="supplierCount"></div>
    </li>
</ul>
</div>
</div>
<!-- 검색 (E) -->


<!-- 버튼 - 새 공급사 등록 (S) -->
<div id="supplierAddButton2" class="btngroup-topright-none">
<ul>
    <li><a href="javascript:bindingDefault()"><fmt:message
        key="aimir.cancel" /></a></li>
</ul>
<ul>
    <li><a href="javascript:submitDefault('add')"><fmt:message
        key="aimir.supplier.message.regist" /></a></li>
</ul>
<ul>
    <li><label><fmt:message key="aimir.supplier.message.new" /></label></li>
</ul>
</div>
<div id="supplierAddButton1" class="btngroup-topright-none"></div>
<!-- 버튼 - 새 공급사 등록 (E) -->


<!-- 탭 박스 (S) -->
<div id="supplierTab" class="tabcontainer_nobg">
<ul>
  <li><a href="#BASIC" id="_basic"><fmt:message key="aimir.button.basicinfo" /></a></li>
    <li><a href="#MGMT" id="_mgmt"><fmt:message key="aimir.location" /></a></li>
    <li><a href="#PAY" id="_pay"><fmt:message key="aimir.tariff" /></a></li>
</ul>
<table><thead style="text-align: center; size: 13px"></thead></table>
<!-- 1st 탭 : Basic (S) -->
<div id="BASIC">
<div id="supplier-tab2" class="bodyleft_supplier">
<ul>
    <li id="default"></li>
</ul>
<!--기본정보 innerHTML #143--></div>

<div id="supplier-tab3" class="bodyright_supplier">
<ul>
    <li>

    <div id="typeControl_title" class="headspace-enter"><label
        class="check"><fmt:message key="aimir.supply.type" /></label></div>

    <div id="typeControl" class="suppliertype-box">
    <div id="type"></div>
    <!-- Add Supply type : addSupplyType.jsp -->
    <div id="type1"></div>
    <!-- Supply type : innerHTML #300 --></div>
    </li>
</ul>
</div>
</div>
<!-- 1st 탭 : Basic (E) --> <!-- 2nd 탭 : MGMT (S) -->
<div id="MGMT">
<div class="bodyleft_supplier">
<ul>
    <li>

    <div class="headspace-enter"><label class="check"><fmt:message
        key="aimir.location.basicinfo" /></label></div>

    <div class="supplierlocation-box">
    <span id="supplier-tab1"; class="supplierlocation-foldertree"; style="width: 36%;">
        <!-- treepan.js : ext-js 삽입  -->       
        
     </span> <span class="dashedline-v">
        
    <div class="dashedline-v-div">
    <ul>
        <li></li>
    </ul>
    </div>
    </span> <span id="supplier-tab4" style="width: 55%; margin-left: 2%;">
    <div id="location"></div>
    </span></div>

    <div id="locationBtnList" class="floatright margin-t10px"><em
        class="am_button margin-r5"> <a href='javascript:addLocation()'
        class='on'><fmt:message key='aimir.add' /></a> </em> <em
        class="am_button margin-r5"> <a
        href='javascript:updateLocation()' class='on'><fmt:message
        key='aimir.update' /></a> </em> <em class="am_button margin-r5"> <a
        href='javascript:deleteLocation()'><fmt:message
        key="aimir.button.delete" /></a> </em>
        <em class="am_button">
            <a href='javascript:openExcelReport();' class='on'>
                <fmt:message key='aimir.button.excel' />
            </a>
        </em>
    </div>

    <!--div class='btn_right_bottom'>

                                <span id='btn'><ul><li style='margin:0;'><a href='javascript:addLocation()' class='on'><fmt:message key='aimir.add'/></a></li></ul></span>
                                <span id='btn'><ul><li style='margin:0;'><a href='javascript:updateLocation()' class='on'><fmt:message key='aimir.update'/></a></li></ul></span>
                                <span id='btn'><ul><li style='margin:0;'><a href='javascript:deleteLocation()'><fmt:message key="aimir.button.delete" /></a></li></ul></span>
                                </div--></li>
</ul>
</div>


<div class="bodyright_supplier">
<ul>
    <li>

    <div class="headspace-enter"><label class="check"><fmt:message
        key="aimir.location.basicinfo.service" /></label></div>

    <div id="supplier-tab3" class="suppliertype-box">
    <div id="locationService"></div>
    <div id="locationService1"></div>
    </div>
    </li>
</ul>
</div>
</div>
<!-- 2nd 탭 : MGMT (E) --> <!-- 3rd 탭 : PAY (S) -->
<div id="PAY" class="tabinside-nopadding"><!--상단검색-->

    <div class="search-bg-withouttabs">
        <div class="searchoption-container">
            <table class="searchoption wfree">
                <tr>
               		<td><select id="tariffTypeCombo" style="width: 150px;"></select></td>
                    <td><select id="supplierType" style="width: 150px;"></select></td>
                    <td><select id="yyyymmddCombo" style="width: 150px;"></select></td>
                    <td><em class="am_button">
                        <a href="javascript:getTariffGrid();"id="btnSearch">
                        <fmt:message key="aimir.button.search" />
                        </a> </em>
                    </td>
                </tr>
            </table>
        </div>
    </div>
<!--상단검색 끝-->
    <div id="new-electric-tariff" class="mvm-popwin-iframe-outer">
        <span><fmt:message key='aimir.tariff'/></span>
        <input name='tariffType'></input>
        <span><fmt:message key='aimir.season'/></span>
        <span>
            <select id='season-select' name='season'></select>
        </span>
        <span><fmt:message key='aimir.tou'/></span>
        <span>
            <select id='peakType-select' name='peakType'></select>
        </span>
    </div>
    <!--  TariffType : Electricity -->
    <div id="electricDiv" style="display: none;">
    <!-- <div class="search-bg-withouttabs"> -->
    	<div class="tou-description" style="margin-left: 1%;">
			<ul>
				<li>CRITICAL_PEAK: Tou_Tariff1
			    <li>PEAK: Tou_Tariff2
			     <li>OFF_PEAK: Tou_Tariff3
			</ul>
		</div>
		<br>
        <div class="searchoption-container">
	    	<table class="searchoption wfree">
	                <tr>
	                    <td>
	                    	 <span style="margin-bottom: 5px">
			                	<ul>
			                		<li>
			                			<span id="padding-10" style="padding-left: 10px; padding-right: 10px;"><label class="check">Applied Date</label></span>
			                			<span class="calendar-form">
					                        <input class="alt date" type='text' readOnly></input>
					                        <input name="date" class="no-width" type="text"></input>
					                    </span>
					                    <span id="padding-10">
					                    <em class="am_button"> <a href="javascript:addRow();" id="addRow"><fmt:message key="aimir.add" /> Row</a> </em>
					                    <%-- <em class="am_button"> <a href="javascript:deleteRow();" id="deleteRow"><fmt:message key="aimir.button.delete" /></a> </em> --%>
					                    <em class="am_button"> <a href="javascript:exportExcel('ExportEm');" id="ExportEm"><fmt:message key="aimir.button.excel" /></a> </em>
			                			<%-- <em class="am_button"> <a href="javascript:getTariffGrid();"id="btnSearch">	<fmt:message key="aimir.button.search" /></a></em> --%>
			                			<!-- <em class="am_button"> <a href="javascript:cmdSetTariff();" id="setTariff">STS Set Tariff</a> </em> -->
			                			<!-- <em class="am_button"> <a href="javascript:cmdGetTariff();" id="setTariff">STS Get Tariff</a> </em> -->
			                			</span>
					                    <br><br>
			                			<span id="padding-10" style="padding-right: 10px;" >(Tariff date will be saved as Applied Date above.)</span>
					                    <!-- <br><br> -->
			                		</li>
			                	</ul>
			                	<br>
	               			 </span>
	               			 
	                    </td>
	                    <div id="tariffUpdateBtn" class="btn_right_bottom gadget_body2">
					      <%-- <span class="lightgray11pt margin-r5"><fmt:message key="aimir.save"/></span> --%>
					      <span><em class="am_button"><a href="javascript:updateData();">Save</a></em></span>
					    </div>
	                </tr>
	        </table>
	        </div>
	        <br>
	        <div id="padding-10" style="padding-left: 10px; padding-right: 10px;">
	        	<div id="chargeMgmtEm" class="tabcontentsbox border-blue" ></div>
	        </div>
		<!-- </div> -->
    </div>

<!--  TariffType : Gas -->
    <div id="gasDiv" class="" style="display: none;">
        <ul>
            <li>
                <span style="float:right; margin-bottom: 5px">
                    <span class="calendar-form">
                        <input class="alt date" type='text' readOnly></input>
                        <input name="date" class="no-width" type="text"></input>
                    </span>
                    <em class="am_button"> <a href="javascript:addRowWm();" id="addRowWm"><fmt:message key="aimir.add" /></a> </em>
                    <%-- <em class="am_button"> <a href="javascript:deleteRowWm();" id="deleteRow"><fmt:message key="aimir.button.delete" /></a> </em> --%>
                    <em class="am_button"> <a href="javascript:exportExcel('ExportWM');" id="ExportWm"><fmt:message key="aimir.button.export" /></a> </em>
                </span>
            </li>
        	<li>
            	<!--  <em id="chargeMgmtGmEx"></em> -->
        	</li>
        </ul>
    </div>

	<div id="waterDiv" class="" style="display: none;">
        <ul>
            <li>
                <span style="float:right; margin-right: 10px">
                    <span class="calendar-form">
                        <input class="alt date" type='text' readOnly></input>
                        <input name="date" class="no-width" type="text"></input>
                    </span>
                    <em class="am_button"> <a href="javascript:addRowWm();" id="addRowWm"><fmt:message key="aimir.add" /></a> </em>
                    <%-- <em class="am_button"> <a href="javascript:deleteRowWm();" id="deleteRow"><fmt:message key="aimir.button.delete" /></a> </em> --%>
                    <em class="am_button"> <a href="javascript:exportExcel('ExportWM');" id="ExportWm"><fmt:message key="aimir.button.export" /></a> </em>
                </span>
            </li>
            <li>
            	<!-- <em id="chargeMgmtWmEx"></em> -->
        	</li>
        </ul>
        <br>
        <br>
        <div id="padding-10" style="padding-left: 10px; padding-right: 10px;">
        	<div id="chargeMgmtWm" class="tabcontentsbox border-blue" ></div>
        </div>
   	</div>

 
    
</div>
<!-- 3rd 탭 : PAY (E) --> <!-- 4th 탭 : TAX (S) -->
</div>

<!-- 탭 박스 (E) -->
</body>
</html>