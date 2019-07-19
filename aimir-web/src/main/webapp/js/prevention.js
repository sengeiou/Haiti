
	//드래그, 복사, 우클릭 방지 코드
	//js와 함께 body에 
	//oncontextmenu="return false" onselectstart="return false" ondragstart="return false"
	//옵션을 추가해 주어야 한다.

	var omitformtags=["input", "textarea", "select"];
	
	omitformtags=omitformtags.join("|");
	
	function disableselect(e){
	if (omitformtags.indexOf(e.target.tagName.toLowerCase())==-1)
	return false;
	}
	
	function reEnable(){
	return true;
	}
	
	if (typeof document.onselectstart!="undefined")
	document.onselectstart=new Function ("return false");
	else{
	document.onmousedown=disableselect;
	document.onmouseup=reEnable;
	}
