<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <style type="text/css">
      TABLE{border-collapse: collapse; width:auto;}
        /* pivot grid 의 style 이 어긋나는 부분 수정 */
        .x-pivotgrid .x-grid3-row-headers table td {
            height: 19px !important;
        }
        .ext-gecko .x-pivotgrid .x-grid3-row-headers table td {
            height: 22px !important;
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        /* tree node 의 style 이 어긋나는 부분 수정 */
        .x-tree-node span, .x-tree-node a {
            float:none !important;
        }
        /* context menu 의 style 이 어긋나는 부분 수정 */
        .x-menu-list-item span, .x-menu-list-item a {
            float:none !important;
        }
    </style>
	<script type="text/javascript" charset="utf-8">
		var sId;			// 현재 로딩된 SupplierId
        var chromeColAdd = 2;
		// 공급사 리스트 로딩
		function getSuppliers() {
			$.getJSON('${ctx}/gadget/system/supplier/getSuppliers.do',
				function(json) {
					if (json.suppliers != null) {
                 		$('#slist').loadSelect(json.suppliers);

                   		getSupplier(json.suppliers[0].id);

                   		$('#slist').change( function () {
				         	supplierId = $(this).val();
				            if(supplierId != 0){
				               	getSupplier(supplierId);
				               	if(document.getElementsByName('supplierTab')[1].id == "current") {
				               		getLocationFlex();
				               	}
							}
						});
                   	} else if (json.supplier != null){
						var supplier = json.supplier;
						var innerHtml = "<li class='top' id='supplierFont2'>" + supplier['name'] + "</li>";

						$('#supplierList').html(innerHtml);

						getSupplier(supplier['id']);
					}
                }
            );
		}

     $(window).resize(function() {
  

        supplierGrid.destroy();
        supplierGridInstanceOn = false;
        
        getSuppliers();
            
    });   
		// 공급사 선택 시 정보를 로딩한다.
		function getSupplier(supplierId) {
			if(supplierId != null) {
				sId = supplierId;
			}
			$.getJSON('${ctx}/gadget/system/supplier/getSupplier.do', {supplierId:sId},
				function(json) {
	                bindingDefault(json.supplier);
	                bindingType(json.minisupplyTypes);
			});
		}
		function bindingDefault(supplier) {
			var innerHtml1 = "";
			var innerHtml2 = "";
			var innerHtml3 = "";
			var innerHtml4 = "";
			var innerHtml5 = "";
			var innerHtml6 = "";
			var innerHtml7 = "";
			var innerHtml8 = "";
				innerHtml1 = "<label class=\"check\"><fmt:message key='aimir.description'/></label>";
				innerHtml2 = "<span class=\"top\">" + supplier.descr + "</span>";
				innerHtml3 = "<label class=\"check\"><fmt:message key='aimir.tel.no'/></label>";
				innerHtml4 = "<span class=\"top\">" + supplier.telno + "</span>";
				innerHtml5 = "<label class=\"check\"><fmt:message key='aimir.country'/></label>";
				innerHtml6 = "<span class=\"top\">" + supplier.country + "</span>";
				innerHtml7 = "<label class=\"check\"><fmt:message key='aimir.supply.type'/></label>";
				innerHtml8 = "<span class=\"top\"></span>";

				$('#default1').html(innerHtml1);
				$('#default2').html(innerHtml2);
				$('#default3').html(innerHtml3);
				$('#default4').html(innerHtml4);
				$('#default5').html(innerHtml5);
				$('#default6').html(innerHtml6);
				$('#default7').html(innerHtml7);
				$('#default8').html(innerHtml8);
		}
		// 공급사 reset
		function resetDefault() {
			var innerHtml1 = "";
			var innerHtml3 = "";
			var innerHtml5 = "";
			var innerHtml7 = "";
				innerHtml1 = "<label class=\"check\"><fmt:message key='aimir.description'/></label>";
				innerHtml3 = "<label class=\"check\"><fmt:message key='aimir.tel.no'/></label>";
				innerHtml5 = "<label class=\"check\"><fmt:message key='aimir.country'/></label>";
				innerHtml7 = "<label class=\"check\"><fmt:message key='aimir.supply.type'/></label>";
			$('#default1').html(innerHtml1);
			$('#default3').html(innerHtml3);
			$('#default5').html(innerHtml5);
			$('#default7').html(innerHtml7);
		}
		function goMax() {
//			document.supplierMini.supplierId.value = sId;
			document.supplierMini.submit();
		}

		// grid column tooltip
        function co2Unit(value, metadata) {
            if (value != null && value != "") {
                value= value+"㎏ CO₂";
            }
            return value;
        }

        function co2UsageUnit(value, metadata) {
            if (value != null && value != "") {
                value= value;
            }
            return value;
        }

    	var supplierGridInstanceOn = false;
    	var supplierGrid;
    	var supplierModel;

		function bindingType(supplyTypes) {

		var width = $("#supplierType_table").width();
      	var supplierStore = new Ext.data.JsonStore({
            autoLoad: true,
            data: supplyTypes || {},
            root:'',
            fields: [
                      "billDate"
                     , "id"
                     , "supplier"
                     , "type"
                     , "typeCode"
                     , "co2emissions"
                     , "co2unitUsage"
                     ]
        });

	       supplierModel = new Ext.grid.ColumnModel({
                columns: [
                	
                     {header: "<fmt:message key="aimir.type2"/>",
                      dataIndex: 'type', 
                      align:'center',
                      width:80
                     }
                    ,{header: "<fmt:message key="aimir.paydate"/>", 
                      dataIndex: 'billDate', 
                      align:'right',
                      width:60
                     }
                     ,{
                     header: "<fmt:message key="aimir.co2formula"/>"             
					 ,dataIndex: 'co2emissions' 
                     ,align:'right'
                     ,renderer : co2Unit
                     },{
                      header: "<fmt:message key="aimir.unitsusage"/>"   
                      ,dataIndex: 'co2unitUsage'
                      ,align:'right'
                      ,renderer:co2UsageUnit
                  
                 }
                  
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 125
                }
            });

            if (supplierGridInstanceOn == false) {
                supplierGrid = new Ext.grid.GridPanel({
                    id: 'supplierGrid',
                    store: supplierStore,
                    colModel : supplierModel,
                    autoScroll: false,
                    width: width,
                    height: 120,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'supplierType_table',
                    viewConfig: {
                        forceFit:true,
                        scrollOffset: 1,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    }
                });
                supplierGridInstanceOn = true;
            } else {
                supplierGrid.setWidth(width);
                supplierGrid.reconfigure(supplierStore, supplierModel);
            }

        }

	</script>
</head>



<body onLoad="getSuppliers();">
<div id="gadget_body" class="margin-t10px">
	<form name="supplierMini" action="/gadget/system/supplier/supplierMgmtMax.do" method="POST">
		<input type="hidden" name="supplierId">
	</form>
	
<!--  상단 공급사 기본 정보 설정 / bindingDefault()로 값 설정-->
        <table class="wfree searchoption" style="margin-left:20px;">    
        <!-- 테이블 자체를 센터정렬할때는 : table class="wfree searchoption align-center"-->
        <tr>
        <td id="default0" class="padding-r20px"><label class="check"><fmt:message key='aimir.supplier.name'/></label></td>
        <td id="supplierList"><select id="slist" name="list"></select></td>
        </tr>
        <tr>
        <td id="default1" class="padding-r20px"></td>
        <td id="default2"></td>
        </tr>
        <tr>
        <td id="default3" class="padding-r20px"></td>
        <td id="default4"></td>
        </tr>
        <tr>
        <td id="default5" class="padding-r20px"></td>
        <td id="default6"></td>
        </tr>
        <tr>
        <td id="default7" class="padding-r20px"></td>
        <td id="default8"></td>
        </tr>
        </table>

		<div id="type" class="margin-t10px"></div>
		<div id="supplierType_table"></div>
</div>
</body>
</html>
