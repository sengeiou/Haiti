
    //replaceAll() 설정하기
    String.prototype.replaceAll = function( searchStr, replaceStr )
    {
    var temp = this;
    while( temp.indexOf( searchStr ) != -1 )
    {
    temp = temp.replace( searchStr, replaceStr );
    }
    return temp;
    };
    
    // 일반 Tree Pannel을 위한 Parent Node 를 만들때 사용한다.
    var makeTreePanJson = function(_locations, _prefix) {
        var jsonStr = "";
        var prefix = '';
        if(_prefix != undefined && _prefix != '') prefix = _prefix + '_';
        for(var i = 0, size = _locations.length ; i < size ; i++) {
            var location = _locations[i];
            if (jsonStr!="") jsonStr+=",";
            if(location.children != null && location.children.length > 0) {
                jsonStr += "{";
                jsonStr += "'id' : '" + location.id + "' ,";
                jsonStr += "'text' : '" + location.name + "'";
                jsonStr += ", children:[";
                jsonStr += makeTreePanChild(location.children, prefix);
                jsonStr += "]";
                jsonStr += "}";
            } else {
                jsonStr += "{";
                jsonStr += " 'id' : '" + location.id + "' ,";
                jsonStr += "'text' : '" + location.name + "'";
                jsonStr += ",'leaf' : true}";
            }
        }
        jsonStr=jsonStr.replaceAll("}{","},{");
        //alert(jsonStr);
        var obj = eval('([' + jsonStr + '])');
        return obj;
    };
    
    // 일반  Tree Pannel을 위한 Children Node 를 만들때 사용한다.
    var makeTreePanChild = function(_locations, _prefix) {
        var jsonStr = "";
        for(var i = 0, size = _locations.length ; i < size ; i++) {
            var location = _locations[i];
            //alert(location.name);
            if(location.children != null && location.children.length > 0) {
                jsonStr += "{";
                jsonStr += "'id' : '" + location.id + "' ,";
                jsonStr += "'text':'" + location.name + "'";
                jsonStr += ", children:[";
                jsonStr += makeTreePanChild(location.children, _prefix);
                jsonStr += "]";
                jsonStr += "}";
            } else {
                jsonStr += "{";
                jsonStr += " 'id' : '" + location.id + "' ,";
                jsonStr += "'text':'" + location.name + "', 'leaf' : true}";
            }
        }
        jsonStr += "";
        return jsonStr;
    };

    // Parent Node 를 만들때 사용한다.
    var makeTreeJson = function(_locations, _prefix) {
        var jsonStr = "";
        var prefix = '';
        if(_prefix != undefined && _prefix != '') prefix = _prefix + '_';
        for(var i = 0, size = _locations.length ; i < size ; i++) {
            var location = _locations[i];
            if (jsonStr!="") jsonStr+=",";
            if(location.children != null && location.children.length > 0) {
                jsonStr += "{";
                jsonStr += "attributes : { 'id' : '" + prefix + location.id + "' },";
                jsonStr += "data : '" + location.name + "'";
                jsonStr += ", children:[";
                jsonStr += makeTreeChild(location.children, prefix);
                jsonStr += "]";
                jsonStr += "}";
            } else {
                jsonStr += "{";
                jsonStr += "attributes : { 'id' : '" + prefix + location.id + "' },";
                jsonStr += "data : '" + location.name + "'";
                jsonStr += "}";
            }
        }
        jsonStr=jsonStr.replaceAll("}{","},{");
        //alert(jsonStr);
        var obj = eval('([' + jsonStr + '])');
        return obj;
    };

    // Children Node 를 만들때 사용한다.
    var makeTreeChild = function(_locations, _prefix) {
        var jsonStr = "";
        for(var i = 0, size = _locations.length ; i < size ; i++) {
            var location = _locations[i];
            //alert(location.name);
            if(location.children != null && location.children.length > 0) {
                jsonStr += "{";
                jsonStr += "attributes : { 'id' : '" + _prefix + location.id + "' },";
                jsonStr += "data:'" + location.name + "'";
                jsonStr += ", children:[";
                jsonStr += makeTreeChild(location.children, _prefix);
                jsonStr += "]";
                jsonStr += "}";
            } else {
                jsonStr += "{";
                jsonStr += "attributes : { 'id' : '" + location.id + "' },";
                jsonStr += "data:'" + location.name + "'}";
            }
        }
        jsonStr += "";
        return jsonStr;
    };

    // 트리에서 체크된 지역 ID
    function getLocationIds(obj) {
        var locationIdArray = Array();

        $.tree.plugins.checkbox.get_checked($.tree.reference(obj)).each(function() {
            locationIdArray.push(this.id);
        });

        return locationIdArray.join();
    };

    function locationTreeGoGo  (_treeDivId, _searchKeyId, _locationId, _prefix) {

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

                if(tree_div.width()<searchKey.width()){
                    tree_div.css('min-width',searchKey.width());
                }

                if(flag) {
                    document.getElementById(_searchKeyId).value = '';
                    flag = false;
                }
                autoComplete(_treeDivId, _searchKeyId, _locationId);
            })
            .bind('keyup', function(event) { autoComplete(_treeDivId, _searchKeyId, _locationId);});

        $.get("/aimir-web/gadget/system/location/getLocations.do",

            function(data) {
                var locationData = makeTreeJson(data.locations, _prefix);
                $('#' + _treeDivId).tree({
                    data : {
                        type : "json",
                        opts : {
                            static : locationData
                        }
                    },
                    callback : {
                        'onselect' : function(n, t) {
                            var locationName = $('#' + n.id + ' a').html().replace('<INS>&nbsp;</INS>', '').replace('<ins>&nbsp;</ins>', '');
                            document.getElementById(_searchKeyId).value = locationName;

                            var tempId = n.id;
                            var locationId = '';
                            var underBarIndex = tempId.indexOf('_');

                            if(underBarIndex == -1) {
                                locationId = tempId;
                            } else {
                                locationId = tempId.substr(underBarIndex + 1, tempId.length);
                            }

                            document.getElementById(_locationId).value = locationId;
                            document.getElementById(_treeDivId + 'Outer').style.display = "none";
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

    function locationTreeForPermitLocation  (_treeDivId, _searchKeyId, _locationId, _paramLocationId, _prefix) {

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

                if(tree_div.width()<searchKey.width()){
                    tree_div.css('min-width',searchKey.width());
                }

                if(flag) {
                    document.getElementById(_searchKeyId).value = '';
                    flag = false;
                }
                autoComplete(_treeDivId, _searchKeyId, _locationId);
            })
            .bind('keyup', function(event) { autoComplete(_treeDivId, _searchKeyId, _locationId);});

        $.get("/aimir-web/gadget/system/location/getUserLocation.do",
            {'locationId' : _paramLocationId},
            function(data) {
                var locationData = makeTreeJson(data.locations, _prefix);
                $('#' + _treeDivId).tree({
                    data : {
                        type : "json",
                        opts : {
                            static : locationData
                        }
                    },
                    callback : {
                        'onselect' : function(n, t) {
                            var locationName = $('#' + n.id + ' a').html().replace('<INS>&nbsp;</INS>', '').replace('<ins>&nbsp;</ins>', '');
                            document.getElementById(_searchKeyId).value = locationName;

                            var tempId = n.id;
                            var locationId = '';
                            var underBarIndex = tempId.indexOf('_');

                            if(underBarIndex == -1) {
                                locationId = tempId;
                            } else {
                                locationId = tempId.substr(underBarIndex + 1, tempId.length);
                            }

                            document.getElementById(_locationId).value = locationId;
                            document.getElementById(_treeDivId + 'Outer').style.display = "none";
                        },
                        'onsearch' : function(n, t) {
                        }
                    }
                });
            }
        );
    };

    var autoComplete = function(_treeDivId, _searchKeyId, _locationId) {

        var keyWord = document.getElementById(_searchKeyId).value;

        if(keyWord == '') {
            document.getElementById(_locationId).value = '';
        }

        document.getElementById(_treeDivId + 'Outer').style.display = "block";
        $.tree.focused().search(keyWord);
    };