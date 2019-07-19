<script>
<!--//
function hide()
{	
    var pre = document.getElementById('preLoadingDiv');
    pre.style.visibility="hidden";
    
}
function emergePre()
{
	var pre = document.getElementById('preLoadingDiv');
	pre.style.visibility="visible";
}
//-->
</script>

<style type="text/css">
	html, body{height:100%;margin:0}  
	.preload{position:fixed;_position:absolute;top:0;left:0;width:100%;height:100%;z-index:255;}  
	.preload .bg{position:absolute;top:0;left:0;width:100%;height:100%;background:#FFF;opacity:.7;filter:alpha(opacity=70)} 
	.preload .image{position:absolute;top:50%;left:50%;width:60px;height:12px;margin:-6px 0 0 -30px;padding:0;}  
</style>


<body>
<div id="preLoadingDiv" class="preload">
    <div class="bg">  
         <div class="image"><img src="${ctx}/themes/images/default/progress/img_loading.gif"></div>  
    </div> 
</div> 
</body>

<% out.flush(); %>