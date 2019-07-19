<!-- 
Topology (Network Diagram With D3 API)
Reference URL: https://flowingdata.com/2012/08/02/how-to-make-an-interactive-network-visualization/
// import d3.js 
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta charset="utf-8">

<style>
 circle.node {
    stroke: #fff;
    stroke-width: 1px;
}
line.link {
    stroke-width: 1px;
    /* stroke: skyBlue; */
    stroke: #999;
    stroke-opacity: 0.8;
}
</style>
<ul><li class="bluebold11pt" style="float:right">&nbsp;Drag : pin a node&nbsp;&nbsp;&nbsp;Double click : unpin a node&nbsp;&nbsp;&nbsp;Click : on/off a label&nbsp;</li></ul>
<div id= topologyDiv></br></div>
<body>
<script type="text/javascript" charset="utf-8">

var json;
$(document).ready(function () {
	$.ajax({
		  async: false,
		  url: '${ctx}/gadget/device/topology/getTopologyInfo.do', // TopologyOperationController.java
		  data: {'mcuId' : mcuId
			}, 
		  success: function(data) {
			//alert(data.json); 
			/*
			* json string을 받아옴.
			* node index는0부터 시작
			* source index와  target index를 연결
			* group으로 color결정
			* Ex)
			* {"nodes":[{"name":"TEST02_mcu","group":1},{"name":"TEST03_modem","group":2},{"name":"TEST01_meter","group":3}],"links":[{"source":0,"value":1,"target":1},{"source":1,"value":1,"target":2}]}
			*/
		    json = JSON.parse(data.json);
		  }
		});
		
	var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
	myMask.hide();
})

//화면 크기
var width = $(window).width()-100;
var height = 800;

var color = d3.scale.category10();
var node;
var labelToggle = true;
var svg = d3.select('#topologyDiv').append("svg")
    .attr("width", width)
    .attr("height", height);
    
var force = d3.layout.force()
    //.gravity(.07)
    // charge와 linkDistace로 그래프 규모를 조절할 수 있음.
    .charge(-150)                 
    .linkDistance(16)
    .size([width, height]);
   
<!------------------------- Search 기능(s) ------------------------------------>
var optArray = [];
for (var i = 0; i < json.nodes.length - 1; i++) {
    optArray.push(json.nodes[i].name);
}
	optArray = optArray.sort();
	$(function () {
    	$("#search").autocomplete({
        	source: optArray
    	});
	});
function searchNode() {
    //find the node (highlight)
    var selectedVal = document.getElementById('search').value;
    var node = svg.selectAll(".node");
    if (selectedVal != ""){
        var selected = node.filter(function (d, i) {
            return !(d.name.toLowerCase().includes(selectedVal.toLowerCase())); // Like 검색 
            //return d.name != selectedVal; // fullName 검색
        });
        selected.style("opacity", "0");
        var link = svg.selectAll(".link")
        link.style("opacity", "0");
        d3.selectAll(".node, .link").transition()
            .duration(15000)
            .style("opacity", 1);
    }
}
<!------------------------- Search 기능(e) ------------------------------------>


<!-------------------------- Tooltip (s) ------------------------------------>
// import d3Tooltip.js
var tip = d3.tip()
    .offset([-10, 0])
    .html(function (d) {
    return  d.name + "";
})
svg.call(tip);
<!-------------------------- Tooltip (e) ------------------------------------->


<!------------------------- Node Pin 기능(s) ----------------------------------->
var node_drag = d3.behavior.drag()
.on("dragstart", dragstart)
.on("drag", dragmove)
.on("dragend", dragend);  

	function dragstart(d, i) {
	  force.stop() 
	}
	function dragmove(d, i) {
	  d.px += d3.event.dx;
	  d.py += d3.event.dy;
	  d.x += d3.event.dx;
	  d.y += d3.event.dy;
	}
	function dragend(d, i) {
	  d.fixed = true; 
	  force.resume();
	}
	function releasenode(d) {
	  d.fixed = false; 
	}
<!------------------------- Node Pin 기능(e) ----------------------------------->


<!--------------------------- Update Graph(s) --------------------------------> 
function updateGraph(json) {
  force
      .nodes(json.nodes)
      .links(json.links)
      .start();
  
  var link = svg.selectAll("line.link")
      .data(json.links)
      .enter().append("line")
      .attr("class", "link")
      .style("stroke-width", function(d) { return Math.sqrt(d.weight); });

  node = svg.selectAll("circle.node")
      .data(json.nodes)
      .enter().append("g")
      .attr("class", "node")
      //.attr("r", 8)
      .call(node_drag)                                    // pin a node
      .on('dblclick', releasenode)                        // unpin a node
      .on("click",function(d){                           
    	  if(d3.select(this).select("text") != "")
    		  d3.select(this).selectAll("text").remove(); // hide a label
    	  else{                                           // show a label
	          d3.select(this)
	          .append("text")
			    .attr("dx", 10)
			    .attr("dy", ".35em")
			    .text(function(d) { 
			    	return d.name });
    	  }
        })
      .on('mouseover', tip.show)                          // show a tooltip 
      .on('mouseout', tip.hide);                          // hide a tooltip
  node.append("circle")
  	  .style("fill", function(d) {
  		  if(d.group == 1){         // DCU
  			color(d.group);
  			return "orange";
  		  }else if(d.group == 2){   // Modem
  			color(d.group);
  			return "#0174DF"; 
  		  }else{                    // Meter
  			color(d.group);
  			return "green";   
  		  }
  	  })
  	  .style("stroke", "white")
  	  .style("stroke-width", "1.1px")
      .attr("r","9");

  force.on("tick", function() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });
  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
   	
  // 범주 설정
  var legend = svg.selectAll(".legend")
	 .data(color.domain())
	 .enter().append("g")
	 .attr("class", "legend")
	 .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });
	
  legend.append("rect")
	 .attr("x", 18)
	 .attr("width", 18)
	 .attr("height", 18)
	 .style("fill", function(d){
		 if(d == 1)                // DCU
			 return "orange";
		 else if(d == 2)           // Modem
			 return "#0174DF";
		 else                      // Meter
			 return "green";
	 });
	
  legend.append("text")
	 .attr("x", 40)
	 .attr("y", 9)
	 .attr("dy", ".35em")
	 .text(function(d) {
		  if(d == 1)
			  return "<fmt:message key='aimir.mcu'/>";
		  else if(d == 2)
			  return "<fmt:message key='aimir.modem'/>";
		  else
			  return "<fmt:message key='aimir.meter'/>";
	  });
  });
};
<!--------------------------- Update Graph(e) -------------------------------->


<!---------------------- Label Toggle 기능(s) -------------------------------->
function labelOnOff(){
	if(labelToggle == true){
		labelToggle = false;
		node.append("text")                 // show labels
		    .attr("dx", 10)
		    .attr("dy", ".35em")
		    .text(function(d) { 
		    	return d.name });
	}else{
		  node.selectAll("text").remove();  // hide labels
		  
	      labelToggle = true; 
	}
}
<!---------------------- Label Toggle 기능(e) --------------------------------> 


<!----------------------------- Unpin 기능(s) -------------------------------->
function unpin(){ // 멈춤현상있음.
	force.nodes().forEach(function(d){ d.fixed = false; });}
<!----------------------------- Unpin 기능(e) -------------------------------->


updateGraph(json); // Update graph

</script>
</body>