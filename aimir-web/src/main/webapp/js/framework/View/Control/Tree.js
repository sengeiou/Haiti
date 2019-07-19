define([
    "jquery",
	"tree/jquery.tree",
	"tree/location.tree"
], function($) {
	
	var data = {};
	var opt = {
		id: "tree-div",
		loadMask: true,
		url: "",
		width: 200,
		height: 300,
		headers: {},
		param: {}
	};

	var Node = function(spec) {
		spec = spec || {};
		this.id = spec.id || 'root-wrapper';
		this.text = spec.text || 'root-wrapper';
		this.children = [];
	};
	Node.fn = Node.prototype;	

	var transformToTreeJSON = function(opt, icon) {

		var makeTreeData = function(data, icon) {
			var node = new Node({
				id: data.id,
				text: data.name
			});

			if(icon && icon.branch) {
				node.icon = icon.branch;
			}
			if(data.children) {
				node.leaf = (data.children.length > 0) ? false : true;
				if(!node.leaf) {
					for(var i = 0, len = data.children.length; i < len; i++) {
						node.children.push(
							makeTreeData(data.children[i], icon)
						);
					}
				}
				else if(icon && icon.leaf) {
					node.icon = icon.leaf;
				}
			}	
			return node;
		};

		var node = new Node();

		var r = opt.root;
		if(r.constructor !== Array) {
			r = [ r ];
		}

		for (var i = 0, len = r.length; i < len; i++) {
			node.children.push(makeTreeData(r[i], icon))
		};

		return node;
	};

	var createTree = function(opt, icon) {
		var node = transformToTreeJSON(opt, icon);
	    var tree = new Ext.tree.TreePanel({
	    	root: {
	            nodeType: 'async'
	        },
	        autoScroll: true,
	        animate: true,
	        width: opt.width,
	        height: opt.height,
	        enableDD: false,
	        containerScroll: true,
	        border: true,
	        rootVisible: false,
	        listeners: opt.listeners, 
	        root: node
	    });
	    tree.render(opt.id);
	    tree.getRootNode().expandChildNodes();

	    return tree;
	};

	var render = function(spec, icon) {
		spec = spec || {};
		var conf = Ext.apply({}, opt);
		conf = Ext.apply(conf, spec);
		return createTree(conf, icon);
	};
	
	return {
		opt: opt,
		data: data,
		locationTreeGoGo: locationTreeGoGo, // location.tree.js tree plugin
		render: render
	};

});