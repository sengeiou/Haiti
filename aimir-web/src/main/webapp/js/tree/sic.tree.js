    //replaceAll() 설정하기
    String.prototype._replaceAll = function( searchStr, replaceStr) {
	    var temp = this;
	    while (temp.indexOf( searchStr ) != -1) {
		    temp = temp.replace( searchStr, replaceStr );
	    }
	    return temp;
    };

    // Parent Node 를 만들때 사용한다.
    var _makeTreeJson = function(_sics, _prefix) {
        var jsonStr = "";
        var prefix = '';
        if(_prefix != undefined && _prefix != '') prefix = _prefix + '_';
        for (var i = 0, size = _sics.length; i < size; i++) {
            var sic = _sics[i];
            if (jsonStr!="") jsonStr+=",";

            jsonStr += "{";
            jsonStr += "attributes : { 'id' : '" + prefix + sic.id + "' },";
            jsonStr += "data : '" + sic.name + "'";

            if (sic.children != null && sic.children.length > 0) {
                jsonStr += ", children:[";
                jsonStr += _makeTreeChild(sic.children, prefix);
                jsonStr += "]";
            }
            jsonStr += "}";
        }
        jsonStr=jsonStr._replaceAll("}{","},{");

        var obj = eval('([' + jsonStr + '])');
        return obj;
    };

    // Children Node 를 만들때 사용한다.
    var _makeTreeChild = function(_sics, _prefix) {
        var jsonStr = "";
        for (var i = 0, size = _sics.length; i < size; i++) {
            var sic = _sics[i];

            jsonStr += "{";
            jsonStr += "attributes : { 'id' : '" + _prefix + sic.id + "' },";
            jsonStr += "data:'" + _prefix + sic.name + "'";
            
            if (sic.children != null && sic.children.length > 0) {
                jsonStr += ", children:[";
                jsonStr += _makeTreeChild(sic.children, _prefix);
                jsonStr += "]";
            }
            jsonStr += "}";
        }
        jsonStr += "";
        return jsonStr;
    };

    // 트리에서 체크된 지역 ID
    function getSicIds(obj) {
        var sicIdArray = Array();

        $.tree.plugins.checkbox.get_checked($.tree.reference(obj)).each(function() {
            sicIdArray.push(this.id);
        });

        return sicIdArray.join();
    };

    var sicTreeGoGo = function(_treeDivId, _searchKeyId, _sicId, _prefix, _containSicIds, _selOnlyLeaf) {
        if (_prefix == null || _prefix == "") {
            _prefix = "sic";
        }

        $('#' + _treeDivId + 'Outer')
            .bind('mouseleave', function(event) { document.getElementById(_treeDivId + 'Outer').style.display = "none"; });

        var flag = true;

        $('#' + _searchKeyId)
            .bind('click', function(event) {
                var tree_div = $('#' + _treeDivId + 'Outer');
                var searchKey = $('#' + _searchKeyId);
                var tree_div_position = $(this).position();
                tree_div_position.top += searchKey.outerHeight();
                tree_div.css(tree_div_position);

                if (tree_div.width() < searchKey.width()) {
                    tree_div.css('min-width',searchKey.width());
                }

                if (flag) {
                    document.getElementById(_searchKeyId).value = '';
                    flag = false;
                }
                _autoComplete(_treeDivId, _searchKeyId, _sicId, _containSicIds);
            })
            .bind('keyup', function(event) { _autoComplete(_treeDivId, _searchKeyId, _sicId, _containSicIds);});

        $.get("../../gadget/system/getSicCodes.do",

            function(data) {
                var sicData = _makeTreeJson(data.sicCodes, _prefix);
                $('#' + _treeDivId).tree({
                    data : {
                        type : "json",
                        opts : {
                            static : sicData
                        }
                    },
                    callback : {
                        'onselect' : function(n, t) {
                        	var children = $('#' + n.id + ' li');
                        	if (_selOnlyLeaf == true && (children != null && children.length > 0)) {
                        		return;
                        	}
                        	
                            var sicName = $('#' + n.id + ' a').html().replace('<INS>&nbsp;</INS>', '').replace('<ins>&nbsp;</ins>', '');
                            document.getElementById(_searchKeyId).value = sicName;

                            var tempId = n.id;
                            var sicId = '';
                            var underBarIndex = tempId.indexOf('_');

                            if (underBarIndex == -1) {
                                sicId = tempId;
                            } else {
                                sicId = tempId.substr(underBarIndex + 1, tempId.length);
                            }

                            document.getElementById(_sicId).value = sicId;
                            document.getElementById(_treeDivId + 'Outer').style.display = "none";

                            if (_containSicIds != null) {
                            	var sicIds = new Array();

                            	sicIds.push(sicId);

                            	if (children != null && children.length != null && children.length > 0) {
                            		for (var i = 0; i < children.length; i++) {
                            			sicIds.push(children[i].id);
                            		}
                            	}
                        		document.getElementById(_containSicIds).value = sicIds.join(",");
                            }
                        },
                        'onsearch' : function(n, t) {
                            //t.container.find('.clicked').removeClass('clicked');
                            //t.container.find('.searchResult').removeClass('searchResult');
                            //n.addClass('searchResult');
                        }
                    }
                });
                //$.tree.focused().open_all('#' + _treeDivId);
            }
        );
    };

    var _autoComplete = function(_treeDivId, _searchKeyId, _sicId, _containSicIds) {

        var keyWord = document.getElementById(_searchKeyId).value;

        if (keyWord == '') {
            document.getElementById(_sicId).value = '';
            if (_containSicIds != null) {
                document.getElementById(_containSicIds).value = '';
            }
        }

        document.getElementById(_treeDivId + 'Outer').style.display = "block";
        $.tree.focused().search(keyWord);
    };