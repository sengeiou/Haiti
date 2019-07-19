<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/prevention.js"></script>
    
    <script type="text/javascript" charset="utf-8">
        
	var checkState = 0; //최초 트리의 상태 - 0:show 1:hidden

    // Tree 생성
    function createLocationTree(){
            $.get("${ctx}/gadget/system/location/getLocations.do",
            function(data) {                      
                var locationData = makeTreeJson(data.locations);
                $('#treeDiv').tree({
                    data : { 
                        type : "json",
                        opts : {
                            static : locationData
                        }
                    },
/*                    ui : {             
                        theme_name : "checkbox"             
                    },
                    plugins : {
                        checkbox : { }
                    },
*/
                    callback : {
                    	'onselect' : function(n, t) {
	                    },
	                    'onsearch' : function(n, t) {
	                        t.container.find('.searchResult').removeClass('searchResult');
	                        n.addClass('searchResult');	
	                    },
	                    'ondblclk' : function(n, t) {  //더블클릭시 해당 함수를 실행
		                    alert(n.id);              //n.id - 더블클릭되는 노드의 아이디
	                    }
                    }
                });
                //$.tree.focused().open_all("#treeDiv");                                      
            });
    }

    
    //자동완성기능처럼 보이기위함.
    function autoTest() {
        var keyWord = document.getElementById("searchWord").value;
        keyWord = encodeURIComponent(keyWord);
        if(keyWord != "" && keyWord != " "){
         $.get("${ctx}/gadget/system/location/searchLocations.do", {keyWord : keyWord},
                 function(json) {
                    var loc_obj = json.locations;
                    keyWord = json.keyWord; //이전까지 encode된 상태이기때문에 controller에서 decode한 keyWord를 받아와서 재설정한다.
                     if(loc_obj != 0){
                         document.getElementById("treeDiv1").style.display = "block";
                         checkState = 1;
                         $.tree.focused().search(keyWord);
                     }else{
                          document.getElementById("treeDiv1").style.display = "none";
                          checkState = 0;
                     }
             });
         }else{
             document.getElementById("treeDiv1").style.display = "none";
             checkState = 0;
         }
     }
	

    //버튼클릭시 tree show/hidden
    function checkTree() {
        if(checkState == 0){
        	document.getElementById("treeDiv1").style.display = "block";
        	checkState = 1;
        }else{
        	document.getElementById("treeDiv1").style.display = "none";
        	checkState = 0;
        }
    }

    function openTree(){
    	document.getElementById("treeDiv1").style.display = "block";
        checkState = 1;
    }
    

    function idcheck(){
        var obj = document.getElementById("treeDiv");
        var id_confirm = getLocationIds1(obj);
        alert(id_confirm);
    }
	</script>
</head>
<body onLoad="createLocationTree()" oncontextmenu="return false" onselectstart="return false" ondragstart="return false">
    <form name="treeTest">
<!-- 메인 시작 -->
	<table border="0" style="margin:2% 0 0 2%;width:250px;">
		<tr>
		<td style="border-collapse: separate">
            <div style="float:left;position:relative;margin-left:10px;">
				<ul style="float:left">
				    <li style="float:left"><input name="searchWord" id='searchWord' type="text" 
				                                   value="Search" onkeyup="javascript:autoTest()"></li>
				    <li style="float:left;padding:0 10px 0 5px"><a href="javascript:checkTree()" id="search">Tree</a></li>
	                <li style="float:left;"><a href="javascript:idcheck()">Id_check</a></li>
				</ul>
            </div>
            
            <div id="treeDiv1" style="width:100%;float:left;left:10px;position:relative;display:none;">
                <div style="position:absolute;background-color:white;border:solid 1px black;">
                    <div class="demo" id="treeDiv" 
                         style="height:150px;width:150px;overflow:auto;background-color:white"></div>
                </div>
            </div>
	     </td>
	     </tr>
	     <tr height="40px">
	         <td valign="middle">
	            <div  style="padding:10px">
	                <span>트리가 떠있는 상태를 확인</span>
	            </div>
			</td>
	     </tr>
	</table>
	
    <div style="border:1px solid red;background-color:yellow;width:250px;height:100px;margin-left:2%;">
        <div style="margin:40px 0 0 80px">
            <a href='#' onclick="window.open('${ctx}/gadget/help/index.html?usermgmt.htm',
            'window','location=no, directories=no,resizable=yes,status=no,toolbar=no,menubar=no, scrollbars=yes');return false">클릭</a>
        </div>
    </div>
    
    </form>
<!-- 메인 끝 -->
</body>
</html>
