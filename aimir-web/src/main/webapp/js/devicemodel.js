	jQuery(document).ready(function(){
		//$(function(){
			$('#device-tree').hide();
			//$('#container-0').show();  //show를 기술하지 않아도  show
			$('#container-1').hide();
			$('#container-0').tabs();    //tab설정은 최초 한번
			$('#container-1').tabs();    //tab설정은 최초 한번

			$('a').click(function(event) {
				event.preventDefault();
			});

            $("input[@name='searchType']").click(function() {
                var searchType = $("input[@name='searchType']:checked").val();

                if (searchType == "M") {
                    $('#container-0').hide();
                    $('#container-1').show();               
                } else {
                    $('#container-0').show();
                    $('#container-1').hide();
                }
            });
                       
            $('#supplier-list a').click( function () {
                //선택된 공급사의 ID는 여러 곳에서 쓰인다.
                var url = $(this).attr('href');

                $('#device-tree').show();
                //$('#device-tree').append('<div id="vendor-list"></div>').load("./sample.html ul#favoriteMovies");
                /*
                $('#device-tree').append('<div id="vendor-list"></div>')
                                 .children("#vendor-list").hide()
                                 .load("/aimir-web/gadget/system/sample.jsp ul#favoriteMovies"
	                                    , function()
					                     {
					                      $("#vendor-list").slideDown("slow");
					                     }); //.load(url);
                */

                $('#device-tree').load(url);
            });
			
			$('#action-link a#add').click( function () {
				//작업구분 타입( & 공급사) 에 따라 호출하는 url이 달라진다. 
				var searchType = $("input[@name='searchType']:checked").val();
				
			    //$('#fragment-0').load('/aimir-web/gadget/system/devicevendoradd.do?supplierId=102');
			    $('#container-1').load('/aimir-web/gadget/system/devicemodeladd.do?supplierId=102&devicetypeId=53');
			});
			
			$('#action-link a#update').click( function () {
				//작업구분 타입( & 공급사) 에 따라 호출하는 url이 달라진다. 
				var searchType = $("input[@name='searchType']:checked").val();
				
			    $('#fragment-0').load('/aimir-web/gadget/system/devicevendoradd.do?supplierId=102');
			});
			
			$('#action-link a#delete').click( function () {
				//작업구분 타입( & 공급사) 에 따라 호출하는 url이 달라진다. 
				/*
				var searchType = $("input[@name='searchType']:checked").val();
				
			    $('#fragment-0').load('/aimir-web/gadget/system/devicevendoradd.do?supplierId=102');
			    */
			    $('#container-1').load('/aimir-web/gadget/system/devicemodeledit.do?supplierId=102&devicemodelId=2');

			    
			});

		});