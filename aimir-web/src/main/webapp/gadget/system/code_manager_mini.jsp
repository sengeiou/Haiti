<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String name = request.getParameter("name") == null ? "<fmt:message key='aimir.alert.insertCodename'/>" : request.getParameter("name");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
    <title><fmt:message key='gadget.system001' /></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
        <%-- TreeGrid 관련 js --%>

    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.Search.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.RowActions.js"></script>
   <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/src/widgets/grid/GridPanel.js"></script>
    <style type="text/css">
    /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
    TABLE{border-collapse: collapse; width:auto;}
    /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        .x-grid3-row td.x-grid3-cell {
            padding-left: 0px;
            padding-right: 0px;
        }
    }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{ 
        text-align: center !important;
        font-weight: bold;
    }
    .selectrow a.x-tree-node-anchor span{
        color : #FF0000 !important;
        font-weight: bold;
    }

    }*/
     /* grid button icon */
        .search {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam/search3.png) !important;
        }
        .add {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam/add.png) !important;
        }
        .update {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam/accept.png) !important;
        }
        .remove {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/fam/delete.gif) !important;
        }
        .save {
            background-image:url(${ctx}/js/extjs/examples/shared/icons/save.gif) !important;
        }
        .cancel {
            background-image:url(${ctx}/js/extjs/examples/image-organizer/images/icons/cancel.png) !important;
        }
    </style>
<script language="JavaScript">

    var editAuth = "${editAuth}";
    var flag;

    //윈도우 리싸이즈시 event
    $(window).resize(function() {
            
        codeMiniGrid.destroy();
        codeMiniGridOn  = false;
        
        getcodeMiniGrid();
            
    });

    $(document).ready(function() {
        Ext.QuickTips.init();
        // getCodeTreeDataList();
        getcodeMiniGrid();

    });

    function getEditAuth() {
        return editAuth;
    }

    var codeData;
    var codeText="";
    //액션스크립트에서 호출하는 함수
    var cnt = 0;
    var searchCode = null;
    function codeSearch(value) {
        searchCode = value;
        codeMiniGridStore.reload();

    }

    function codeRootSearch(value){
        searchCode = null;
        var l = document.codeform;

        var msg = "<fmt:message key='aimir.alert.insertCodename' />";
        if (l.name.value == '' || !flag) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',msg);
            return;
        }

       codeText = value.toUpperCase().toString();
       var rootNode = codeMiniGrid.getRootNode();
       var fstChildNodes = rootNode.childNodes; //1차 depth   
     
       var flen = fstChildNodes.length; 
       // var flen = 2; 
       if (fstChildNodes != null && fstChildNodes.length > 0) {

        for(var i=0;i<flen;i++){
            var node = fstChildNodes[i].attributes;

            if(node.name.toUpperCase().indexOf(codeText)!=-1){
                 fstChildNodes[i].ui.addClass("selectrow");
            }else{
                 fstChildNodes[i].ui.removeClass("selectrow");
            }
            
            totalCount  = 0;
           
            var ismapping = codeChildSearch(node);
            if(ismapping > 0 ){
          
                fstChildNodes[i].expand(); //1 depth 펼침.
                // node.expanded = true;
            }else{

                fstChildNodes[i].collapse();
                // node.expanded = false;
            }
            
         }
       }  
    }

    var totalCount  = 0;
    function codeChildSearch(childNodes){

         var rootmappingCount = 0 ;
       
         if(hasChild(childNodes)!=true){
            var childlen = childNodes.children.length;
            
            for(var i=0;i<childlen;i++){

                var childmappingCount = 0;
                if(childNodes.children[i].name.toUpperCase().indexOf(codeText)!=-1){
                    childNodes.children[i].cls="selectrow";
                    // childNodes.children[i].expanded=true;
                    rootmappingCount++;
                 }else{
                     childNodes.children[i].cls="";
                 }

                childmappingCount = codeChildSearch(childNodes.children[i]);             

                rootmappingCount = rootmappingCount+childmappingCount;
            }
          
            if(rootmappingCount >0){   
              childNodes.expanded = true;
            }else{
              childNodes.expanded = false;
            }
         }

        totalCount = rootmappingCount; 
        return totalCount;
    }

    function hasChild(obj){ 
     var str=false;
     if(obj.children == null){
        str = true;
     }
     return str;
    }

    function keyEvent(event,value) {

        var evKeyup = null;
        if (event)
            // firefox
            evKeyup = event;
        else
            // explorer
            evKeyup = window.event;

        var l = document.codeform;
        var msg = "<fmt:message key='aimir.alert.insertCodename' />";

        if (evKeyup.keyCode == 13) {
            if (l.name.value == '' || !flag) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',msg);
                return;
            } else {
                codeSearch(value);
            }
        }
    }

    function delTxt() {

        var l = document.codeform;
        l.name.value = '';
        flag = true;
        return;
    }

    function getFmtMessage() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.childadderror"/>";         // 하위 노드에 추가할 수 없습니다.
        fmtMessage[1] = "<fmt:message key="aimir.alert.groupMgmt.msg6"/>";    // 삭제 가능한 행이 없습니다.
        fmtMessage[2] = "<fmt:message key="aimir.msg.wantdelete"/>";        // 삭제를 하시겠습니까?
        fmtMessage[3] = "<fmt:message key="aimir.child.deleteall"/>";       // 하위 노드에 있는 값들도 모두 삭제 됩니다.
        fmtMessage[4] = "<fmt:message key="aimir.updatedata.notexist"/>";   // 저장할 데이터가 없습니다.
        fmtMessage[5] = "<fmt:message key="aimir.alert.insertCodename"/>";  // 코드명을 입력해 주세요!
        fmtMessage[6] = "<fmt:message key="aimir.codeinsert"/>";            // 코드값을 입력해 주세요.
        fmtMessage[7] = "<fmt:message key="aimir.desc.message"/>";          // 설명을 입력해 주세요.
        fmtMessage[8] = "<fmt:message key="aimir.msg.deletesuccess"/>";     // 삭제 되었습니다
        fmtMessage[9] = "<fmt:message key="aimir.duplicateerror"/>";        // 중복된 값이 있습니다.
        fmtMessage[10] = "<fmt:message key="aimir.save"/>";                 // 저장되었습니다.
        fmtMessage[11] = "<fmt:message key="aimir.childadddone"/>";         // 하위 노드에 추가(저장) 되었습니다.
        fmtMessage[12] = "<fmt:message key="aimir.alert.groupMgmt.msg1"/>"; // 수정 되었습니다.
        fmtMessage[13] = "<fmt:message key="aimir.code.wrongaccess"/>";     // 참조된 코드가 있어서 삭제할 수 없습니다.
        fmtMessage[14] = "<fmt:message key="aimir.codename"/>";             // 코드이름
        fmtMessage[15] = "<fmt:message key="aimir.code"/>";                 // 코드
        fmtMessage[16] = "<fmt:message key="aimir.description"/>";          // 설명

        return fmtMessage;
    }

    var treeData;
    var originData;

    var codeMiniGridStore;
    var codeMiniGridColModel;
    var codeTreeRootNode;
    var codeMiniGridOn = false;
    var codeMiniGrid;
    //codeMini 그리드
    function getcodeMiniGrid(){
       
          var message  = getFmtMessage();
           var width = $("#CodeListGridDiv").width(); 
           codeMiniGridStore = new Ext.data.JsonStore({
            // autoLoad: {params:{start: 0, limit: 10}},
            autoLoad: true,
            url: '${ctx}/gadget/system/getCodeListwithChildren.do',
            reader: new Ext.data.JsonReader({
               
                root:'result',
                 fields: [
                { name: 'name', type: 'String' },
                { name: 'code', type: 'String' },
                { name: 'descr', type: 'String' }
            ]}),
            root:'result',
             fields: [
                { name: 'name', type: 'String' },
                { name: 'code', type: 'String' },
                { name: 'descr', type: 'String' }
            ],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                          page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
                },
                load: function(store, record, options){
                    makecodeMiniGridTree();
                    if (searchCode != null) {
                        codeRootSearch(searchCode);
                    }
                }
            }
        });
        
    };


    function makecodeMiniGridTree(){
        
         var message  = getFmtMessage();
         var width = $("#CodeListGridDiv").width(); 
        
        
         codeMiniGridColModel = [

                {
                    header:message[14],                 
                    dataIndex:'name',
                    width: width/3+10,
                    align:'center',
                    editor: new Ext.form.TextField({
                       id: 'nameId',
                       allowBlank: false,
                       allowNegative: false})
                 }
                 ,{
                    header:message[15],                 
                    dataIndex:'code',
                    width: width/3-20,
                    align:'left',
                    editor: new Ext.form.TextField({
                       id: 'codeId',
                       allowBlank: false,
                       allowNegative: false})
                 }
                 ,{
                    header:message[16],                 
                    dataIndex:'descr',
                    width: width/3-10,
                    align:'center',
                    editor: new Ext.form.TextField({
                       id: 'descrId',
                       allowBlank: false,
                       allowNegative: false})
                 }
            ];

            codeTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'result',
                allowChildren: true,
                draggable:false,
                expanded: true,
                // children: treeData
                children:codeMiniGridStore.reader.jsonData.result
            });
            


            if (!codeMiniGridOn) {

                codeMiniGrid =  new Ext.ux.tree.TreeGrid({

                renderTo: Ext.getBody(),//
                width: width,
                height: 262,
                store : codeMiniGridStore,
                enableDD: true,
                selType:'cellmodel',
                root: codeTreeRootNode,               
                columns: codeMiniGridColModel,
                useArrows: true,  
                renderTo: "CodeListGridDiv",
                
                });
                codeMiniGridOn = true;
            } else{
                codeMiniGrid.setWidth(width);
                codeMiniGrid.setRootNode(codeTreeRootNode);
                codeMiniGrid.render();

            }
        };

</script>
</head>
<body>

<!-- Gadget Body (S) -->
<form name="codeform" method="post" onSubmit="return false;">
    <div class="search-bg-basic">
        <ul class="basic-ul">
            <li>

                <input type=hidden id="id"/>

                    <!-- search -->
                    <div class="search-s1">
                        <ul style="width:220px !important">
                            <li class="search-s1-input"><input name="name" type="text" value="<fmt:message key='aimir.alert.insertCodename'/>"
                                  onclick='javascript:delTxt();' onkeydown="javascript:keyEvent(event,document.codeform.name.value);"></li>
                            <li class="search-s1-btn"><a href="javascript:codeSearch(document.codeform.name.value);" ></a></li>
                        </ul>
                    </div>

            </li>
        </ul>
    </div>
</form>
   <div id="gadget_body">
    <div id="CodeListGridDiv"></div>
   </div>

</body>
</html>