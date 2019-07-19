/*
 * jQuery selectbox plugin
 *
 * Copyright (c) 2007 Sadri Sahraoui (brainfault.com)
 * Licensed under the GPL license and MIT:
 *   http://www.opensource.org/licenses/GPL-license.php
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * The code is inspired from Autocomplete plugin (http://www.dyve.net/jquery/?autocomplete)
 *
 * Revision: $Id$
 * Version: 1.2
 * 
 * Changelog :
 *  Version 1.2 By Guillaume Vergnolle (web-opensource.com)
 *  - Add optgroup support
 *  - possibility to choose between span or input as replacement of the select box
 *  - support for jquery change event
 *  - add a max height option for drop down list
 *  Version 1.1 
 *  - Fix IE bug
 *  Version 1.0
 *  - Support jQuery noConflict option
 *  - Add callback for onChange event, thanks to Jason
 *  - Fix IE8 support
 *  - Fix auto width support
 *  - Fix focus on firefox dont show the carret
 *  Version 0.6
 *  - Fix IE scrolling problem
 *  Version 0.5 
 *  - separate css style for current selected element and hover element which solve the highlight issue 
 *  Version 0.4
 *  - Fix width when the select is in a hidden div   @Pawel Maziarz
 *  - Add a unique id for generated li to avoid conflict with other selects and empty values @Pawel Maziarz
 */
jQuery.fn.extend({
	selectbox: function(options) {
		return this.each(function() {
			new jQuery.SelectBox(this, options);
		});
	}
});

function unescapeHtmlString(str){
	var rtnStr='';
	if(str=='&amp;'){
		rtnStr = '&';
	}else if(str=='&lt;'){
		rtnStr = '<';
	}else if(str=='&gt;'){
		rtnStr = '>';
	}else if(str=='&quot;'){
		rtnStr = '\'';
	}else{
		rtnStr = str;
	}
	return rtnStr;
}

jQuery.fn.extend({
	option: function(option) {
		return this.each(function() {
			
			var elm_id = this.id;
			var $select = $(this);
			var $container = $('#'+elm_id+'_container');
			var $input = $('#'+elm_id+'_input');
			
			var prevli = jQuery("li.selected", $container).get(0);
			if(prevli!=null&&prevli!='undefined')	
			prevli.className="";
			
			var li = $("li#"+elm_id+'_input'+'_'+option, $container).get(0);
			if(li!=null&&li!='undefined'){
				li.className = "selected";
				
				var ar = (''+li.id).split('_');
				var el = ar[ar.length-1];
				$select.val(el);
				
				$input.val(unescapeHtmlString($(li).html()));
			}
		});
	}
});

/* pawel maziarz: work around for ie logging */
if (!window.console) {
	var console = {
		log: function(msg) { 
	 	}
	};
}

// 다른 영역을 클릭 시, 포커스가 맞춰질 시 닫는다.
// @author	javarouka
(function($) {
	function hide() {
		if(jQuery.SelectBox.$$currentShowed) {
			jQuery.SelectBox.$$currentShowed.hide();
			jQuery.SelectBox.$$currentShowed = null;
		}
	}
	function hideIfNotSelectbox(e) {
		if(e && e.keyCode && e.keyCode === 9) {
			hide();
			return;
		}
		var t = $(e.target);
		if(!t.is("input.selectbox") && !t.is("select")/* && !t.is("textarea")*/) {
			hide();
		}		
	}
	$(document).click(hideIfNotSelectbox);
	$(document).keydown(hideIfNotSelectbox);
	
	$("input:not(.selectbox)").live("focus", function(e) {
		hide();
	});
})(jQuery);

jQuery.SelectBox = function(selectobj, options) {
	
	var opt = options || {};
	opt.inputType = opt.inputType || "input";
	opt.inputClass = opt.inputClass || "selectbox";
	opt.containerClass = opt.containerClass || "selectbox-wrapper";
	opt.hoverClass = opt.hoverClass || "current";
	opt.currentClass = opt.currentClass || "selected";
	opt.groupClass = opt.groupClass || "groupname"; //css class for group
	opt.maxHeight = opt.maxHeight || 200; // max height of dropdown list
	opt.loopnoStep = opt.loopnoStep || false; // to remove the step in list moves loop
	opt.onChangeCallback = opt.onChangeCallback || false;
	opt.onChangeParams = opt.onChangeParams || false;
	opt.debug = opt.debug || false;
	opt.option  = opt.option||false;
	
	if(opt.option)$(selectobj).pureSelect(opt.option);
	
	var elm_id = selectobj.id;
	var active = 0;
	var inFocus = false;
	var hasfocus = 0;
	
	var $select = jQuery(selectobj);
	
	$('#'+elm_id+'_container').remove();
	$('#'+elm_id+'_input').remove();
	
	var $container = setupContainer(opt);
	
	var $input = setupInput(opt);
	
	$select.hide().before($input).before($container);
	
	init();

	$input
	.click(function(e) {
		if (!inFocus) {
			if ($container.not(':visible')) {
				anotherSelectClose();
				$container.stop(true).show(100);
				jQuery.SelectBox.$$currentShowed = $container;
			}
			else {
				$container.stop(true).hide(100);
				jQuery.SelectBox.$$currentShowed = null;
			}
		}		
		inFocus = false;		
	})
	.focus(function(){
		if ($container.not(':visible')) {
			anotherSelectClose();
			inFocus = true;
			$container.stop(true).show(100);
			jQuery.SelectBox.$$currentShowed = $container;
		}		
	})
	.keydown(function(event) {	
		switch(event.keyCode) {
			case 38: // up
				event.preventDefault();
				moveSelect(-1);
				break;
			case 40: // down
				event.preventDefault();
				moveSelect(1);
				break;
			//case 9:  // tab 
			case 13: // return
				event.preventDefault(); // seems not working in mac !
				$('li.'+opt.hoverClass).trigger('click');
				break;
			case 27: //escape
			  hideMe();
			  break;
		}
	});
	/*
	.blur(function(e) {
		if ($container.is(':visible') && hasfocus > 0 ) {
			if(opt.debug) console.log('container visible and has focus');
		} 
		else {
			var is_chrome = navigator.userAgent.toLowerCase();
			if(jQuery.browser.msie || jQuery.browser.safari || is_chrome.indexOf('chrome') > -1){ // check for safari too – workaround for webkit
				try{
					if(document.activeElement.getAttribute('id') && document.activeElement.getAttribute('id').indexOf('_container')==-1){
						hideMe();
					}
					else {
						//$input.focus();
						//hideMe();
					}
				}
				catch(e){
					hideMe();
				}
			} 
			else {
				hideMe();
			}
		}
	});
*/
	// 현재 열린 셀렉트 박스를 셀렉트박스 네임스페이스에 지정하고,
	// 새로 열리게 될 때 그 박스를 닫는다.
	// 현재 열린 박스가 새로 열리게 될때 오동작을 방지하기 위해서 
	// 멤버변수인 $container 와 비교해서 같지 않을때만 닫는다.
	//
	// @author	javarouka
	function anotherSelectClose() {
		try {
			if(jQuery.SelectBox.$$currentShowed 
				&& jQuery.SelectBox.$$currentShowed !== $container) {
				jQuery.SelectBox.$$currentShowed.hide();
			}
			jQuery.SelectBox.$$currentShowed = $container;
		}
		catch(unknownError) {
			if($container) $container.hide();
			jQuery.SelectBox.$$currentShowed = null;
		}
	}
	
	function hideMe() { 
		hasfocus = 0;
		$container.stop(true).hide(100); 
	}
	
	function init() {
		
		$container.append(getSelectOptions($input.attr('id'))).hide();
		var width = $input.css('width');
		if($container.height() > opt.maxHeight){
			$container.width(parseInt(width)+parseInt($input.css('paddingRight'))+parseInt($input.css('paddingLeft')));
			$container.height(opt.maxHeight);
		} else{
			$container.width(width); 
		}
	}
	
	function setupContainer(options) {
		var container = document.createElement("div");
		$container = jQuery(container);
		$container.attr('id', elm_id+'_container');
		$container.addClass(options.containerClass);
        $container.css('display', 'none');
		
		return $container;
	}
	
	function setupInput(options) {
		if(opt.inputType == "span"){
			var input = document.createElement("span");
			var $input = jQuery(input);
			$input.attr("id", elm_id+"_input");
			$input.addClass(options.inputClass);
			$input.attr("tabIndex", $select.attr("tabindex"));
		} 
		else {
			var input = document.createElement("input");
			var $input = jQuery(input);
			$input.attr("id", elm_id+"_input");
			$input.attr("type", "text");
			$input.addClass(options.inputClass);
			$input.attr("autocomplete", "off");
			$input.attr("readonly", "readonly");
			$input.attr("tabIndex", $select.attr("tabindex")); // "I" capital is important for ie
			$input.css("width", $select.css("width"));
        	}
		return $input;	
	}
	
	function moveSelect(step) {
		var lis = jQuery("li", $container);
		if (!lis || lis.length == 0) return false;
		// find the first non-group (first option)
		firstchoice = 0;
		while($(lis[firstchoice]).hasClass(opt.groupClass)) firstchoice++;
		active += step;
    		// if we are on a group step one more time
    		if($(lis[active]).hasClass(opt.groupClass)) active += step;
		//loop through list from the first possible option
		if (active < firstchoice) {
			(opt.loopnoStep ? active = lis.size()-1 : active = lis.size() );
		} 
		else if (opt.loopnoStep && active > lis.size()-1) {
			active = firstchoice;
		} 
		else if (active > lis.size()) {
			active = firstchoice;
		}
        	scroll(lis, active);
		lis.removeClass(opt.hoverClass);

		jQuery(lis[active]).addClass(opt.hoverClass);
	}
	
	function scroll(list, active) {
  		var el = jQuery(list[active]).get(0);
  		var list = $container.get(0);      	

  		if(el) {
			if (el.offsetTop + el.offsetHeight > list.scrollTop + list.clientHeight) {
				list.scrollTop = el.offsetTop + el.offsetHeight - list.clientHeight;      
			} 
			else if(el.offsetTop < list.scrollTop) {
				list.scrollTop = el.offsetTop;
			}
		}
	}
	
	function setCurrent() {	
		var li = jQuery("li."+opt.currentClass, $container).get(0);
		var ar = (''+li.id).split('_');
		var el = ar[ar.length-1];
		if (opt.onChangeCallback){
        		$select.get(0).selectedIndex = $('li', $container).index(li);
        		opt.onChangeParams = { selectedVal : $select.val() };
			opt.onChangeCallback(opt.onChangeParams);
		} 
		else {
			$select.val(el);
			$select.change();
		}
		if(opt.inputType == 'span') $input.html($(li).html());
		else $input.val(unescapeHtmlString($(li).html())); 
		
		/* 11-05-04 kskim
    	 * select box 의 텍스트가 화살표와 겹칠경우
    	 */
    	//select box 의 텍스트
		var selectedText = $input.val();
    	// 텍스트 가 표시될 최대 넓이
		var maxWidth = $select.width()-21;
		// 텍스트 넓이
		var txtWidth = textWidth(selectedText);
		// 텍스트 변경 여부
		var chSwitch = false;
		// 최대 넓이보다 텍스트 넓이가 넓다면 조절 필요
        while(txtWidth>maxWidth){
        	chSwitch = true;
        	//뒤에서 한글자씩 빼면서 다시 비교
        	selectedText = selectedText.substr(0,selectedText.length-1);
        	txtWidth = textWidth(selectedText);
        }
        // 텍스트 변경시 input 에 반영
        if(chSwitch)
        	$input.val(selectedText+"..");
		/*end*/
        
		return true;
	}
	/* 11-05-04 kskim */
    function textWidth(text){
    	 var calc = '<span style="display:none">' + text + '</span>';
    	 $('body').append(calc);
    	 var width = $('body').find('span:last').width();
    	 $('body').find('span:last').remove();
    	 return width;
    	};
	
	
	// select value
	function getCurrentSelected() {
		return $select.val();
	}
	
	// input value
	function getCurrentValue() {
		return $input.val();
	}
	
	function getSelectOptions(parentid) {
		var select_options = new Array();
		var ul = document.createElement('ul');
		select_options = $select.children('option');
		if(select_options.length == 0) {
			var select_optgroups = new Array();
			select_optgroups = $select.children('optgroup');
			for(x=0;x<select_optgroups.length;x++){
				select_options = $("#"+select_optgroups[x].id).children('option');
				var li = document.createElement('li');
				li.setAttribute('id', parentid + '_' + $(this).val());
				li.innerHTML = $("#"+select_optgroups[x].id).attr('label');
				li.className = opt.groupClass;
				ul.appendChild(li);
				select_options.each(function() {
					var li = document.createElement('li');
					li.setAttribute('id', parentid + '_' + $(this).val());
					li.innerHTML = $(this).html();
					if ($(this).is(':selected')) {
						$input.html($(this).html());
						$(li).addClass(opt.currentClass);
					}
					ul.appendChild(li);
					$(li)
					.mouseover(function(event) {
						//hasfocus = 1;
						if (opt.debug) console.log('over on : '+this.id);
						jQuery(event.target, $container).addClass(opt.hoverClass);
					})
					.mouseout(function(event) {
						//hasfocus = -1;
						if (opt.debug) console.log('out on : '+this.id);
						jQuery(event.target, $container).removeClass(opt.hoverClass);
					})
					.click(function(event) {
						var fl = $('li.'+opt.hoverClass, $container).get(0);
						if (opt.debug) console.log('click on :'+this.id);
						$('li.'+opt.currentClass, $container).removeClass(opt.currentClass); 
						$(this).addClass(opt.currentClass);
						setCurrent();
						$select.get(0).blur();
						hideMe();
					});
				});
			}
		} else select_options.each(function() {
			var li = document.createElement('li');
			li.setAttribute('id', parentid + '_' + $(this).val());
			li.innerHTML = $(this).html();
			if ($(this).is(':selected')) {
				$input.val(unescapeHtmlString($(this).html()));
				$(li).addClass(opt.currentClass);
			}
			ul.appendChild(li);
			$(li)
			.mouseover(function(event) {
				hasfocus = 1;
				if (opt.debug) console.log('over on : '+this.id);
				jQuery(event.target, $container).addClass(opt.hoverClass);
			})
			.mouseout(function(event) {
				hasfocus = -1;
				if (opt.debug) console.log('out on : '+this.id);
				jQuery(event.target, $container).removeClass(opt.hoverClass);
			})
			.click(function(event) {
			  	var fl = $('li.'+opt.hoverClass, $container).get(0);
				if (opt.debug) console.log('click on :'+this.id);
				$('li.'+opt.currentClass, $container).removeClass(opt.currentClass); 
				$(this).addClass(opt.currentClass);
				setCurrent();
				$select.get(0).blur();
				hideMe();
			});
		});
		return ul;
	}
};



// .emptySelect : Selectbox를 비움
// .initSelect  : ALL
// .loadSelect  : ALL / setData
// .pureSelect  : setData
// .noneSelect  : none / setData

jQuery.fn.emptySelect = function() {
	return this.each(function() {
		if (this.tagName == 'SELECT') this.options.length = 0;
	});
};

jQuery.fn.loadSelect = function(optionArray) {
	return this.emptySelect().each(function(){
		if (this.tagName == 'SELECT') {
			var selectElement = this;
			var totalOption = new Option('All', '');
			if ($.browser.msie) {
				selectElement.add(totalOption);
			} else {
				selectElement.add(totalOption,null);
			}
			
			if(optionArray != null){
				$.each(optionArray, function(index, optionData){
					var option = new Option(optionData.name, optionData.id);
					if ($.browser.msie) {
						selectElement.add(option);
					} else {
						selectElement.add(option,null);
					}
				});
			}
		}
	});
};

jQuery.fn.initSelect = function() {
	return this.emptySelect().each(function(){
		if (this.tagName == 'SELECT') {
			var selectElement = this;
			var totalOption = new Option('All', '');
			if ($.browser.msie) {
				selectElement.add(totalOption);
			} else {
				selectElement.add(totalOption,null);
			}
			
		}
	});
};

jQuery.fn.pureSelect = function(optionArray) {
	return this.emptySelect().each(function(){
		if (this.tagName == 'SELECT') {
			var selectElement = this;
			
			if(optionArray != null){
			$.each(optionArray, function(index, optionData){
				var option = new Option(optionData.name, optionData.id);
				if ($.browser.msie) {
					selectElement.add(option);
				} else {
					selectElement.add(option,null);
				}
			});			
			}
		}
	});
};


jQuery.fn.noneSelect = function(optionArray) {
	return this.emptySelect().each(function(){
		if (this.tagName == 'SELECT') {
			var selectElement = this;
			var totalOption = new Option('-', '');
			if ($.browser.msie) {
				selectElement.add(totalOption);
			} else {
				selectElement.add(totalOption,null);
			}
			
			if(optionArray != null){
				$.each(optionArray, function(index, optionData){
					var option = new Option(optionData.name, optionData.id);
					if ($.browser.msie) {
						selectElement.add(option);
					} else {
						selectElement.add(option,null);
					}
				});
			}
		}
	});
};

// .setText - select의 val값이 아닌  text값과 동일한 것을 선택
// 주의 .setText한 후에 .selectbox()로 다시 그려주어어야 한다. 
// ex)$('#protocolType').setText($('#protocolTypeHidden').val());
//    $('#protocolType').selectbox();
//
jQuery.fn.setText = function(setText) {
	return this.children('option').each(function(index, optionData){
		if(optionData.text == setText)
			optionData.selected = true;
	    });
};

//obj is Array
//Array : [[label,value],[label,value],....] 
//set options to select node
//obj is null or obj is number
//get options from select node
$.fn.options = function(obj){
	if(obj || obj == 0){
		if(obj instanceof Array){
			this.each(function(){
				this.options.length = 0;
				for(var i = 0,len = obj.length;i<len;i++){
					var tmp = obj[i];
					if(tmp.length && tmp.length == 2){
						this.options[this.options.length] = new Option(tmp[0],tmp[1]);
					}
				}
			});
			return this;
		}else if(typeof obj == 'number'){
			return $('option:eq('+obj+')',this);
		}else if(obj == 'selected'){
			return this.val();
		}
	}else{
		return $('option',this)
	}
	return $([]);
}
$.fn.numericOptions = function(settings){
	settings = jQuery.extend({
		remove:true
		,from:1
		,to:31
		,selectedIndex:0
		,valuePadding:0
		,namePadding:0
		,labels:[]
		,exclude:null
		,startLabel:null
	},settings);
	//error check
	if(!(settings.from+'').match(/^\d+$/)||!(settings.to+'').match(/^\d+$/)||!(settings.selectedIndex+'').match(/^\d+$/)||!(settings.valuePadding+'').match(/^\d+$/)||!(settings.namePadding+'').match(/^\d+$/)) return;
	if(settings.from > settings.to) return;
	if(settings.to - settings.from < settings.selectedIndex) return;
	//add options
	if(settings.remove) this.children().remove();
	var padfunc = function(v,p){
		if((''+v).length < p){
			for(var i = 0,l = p - (v+'').length;i < l ;i++){
				v = '0' + v;
			}
		}
		return v;			
	}
	var exclude_strings = (settings.exclude && settings.exclude instanceof Array && settings.exclude.length > 0)?' '+settings.exclude.join(' ')+' ':'';
	this.each(function(){
		this.options.length = 0
		//set startLabel
		var sl = settings.startLabel;
		if(sl && sl.length && sl.length == 2){
			this.options[0] = new Option(sl[0],sl[1]);
		}
	});
	for(var i=settings.from,j=0;i<=settings.to;i++){
		this.each(function(){
			var val = padfunc(i,settings.valuePadding);
			if(exclude_strings.indexOf(' '+val+' ') < 0){
				var lab = (settings.labels[j])?settings.labels[j]:padfunc(i,settings.namePadding);
				this.options[this.options.length] = new Option(lab,val);
				j++;
			}
		});
	}
	this.each(function(){
			if(jQuery.browser.opera){
				this.options[settings.selectedIndex].defaultSelected = true;
			}else{
				this.selectedIndex = settings.selectedIndex;
			}
		});
	return this;
};
//
$.fn.datePulldown = function(settings){
	if(!settings.year || !settings.month) return ;
	var y = settings.year;
	var m = settings.month;
	if(!y.val() || !m.val()) return;
	if(!y.val().match(/^\d{1,4}$/)) return;
	if(!m.val().match(/^[0][1-9]$|^[1][1,2]$|^[0-9]$/)) return;

	var self = this;
	var fnc = function(){
		var tmp = new Date(new Date(y.val(),m.val()).getTime() - 1000);
		var lastDay = tmp.getDate() - 0;
		self.each(function(){
			var ind = (this.selectedIndex<lastDay-1)?this.selectedIndex:lastDay-1;
			this.selectedIndex = ind;
			$(this).numericOptions({to:lastDay,selectedIndex:ind});
		});
	}
	y.change(fnc);
	m.change(fnc);
	return this;	
};
