
Ext.ux.tree.EditTreeGrid = Ext.extend(Ext.ux.tree.TreeGrid, {
  
    idProperty: 'id',

    enableSort: false,

    enableHdMenu: false,

    highlightColor: '#d9e8fb',

    depth: Number.MAX_VALUE,

    rowEdit: true,
   
    delConfirm: 'Confirm',
    
    delConfirmMsg: 'Are you sure you want to do that?',

    isTreeEditor: true,

    initComponent: function() {
        this.enableHdMenu = false; 

        if (this.rowEdit) {
            this.animate = false; 

            this.editor = new Ext.ux.tree.TreeRowEditor({
                listeners: {
                    scope: this,
                    canceledit: this.cancelEdit,
                    afteredit: this.saveNode
                }
            });
            this.plugins = this.plugins || [];
            this.plugins.push(this.editor);
        }

        Ext.ux.tree.EditTreeGrid.superclass.initComponent.call(this);
    },

    // private
    beforeDestroy: function() {
        Ext.destroy(this.editor);
        Ext.ux.tree.EditTreeGrid.superclass.beforeDestroy.call(this);
    },

    // private
    initColumns: function() {
        var cs = this.columns, len = cs.length, columns = [], i, c, tpl;

        for (i = 0; i < len; i++) {
            c = cs[i];
            if (!c.isColumn) {
                c.xtype = c.xtype ? (/^tg/.test(c.xtype) ? c.xtype : 'tg' + c.xtype) : 'tgcolumn';

                // 构建操作功能
                if (c.buttons) {
                    c.buttons = Ext.isArray(c.buttons) ? c.buttons : [c.buttons];
                    c.buttonIconCls = Ext.isDefined(c.buttonIconCls) ? (Ext.isArray(c.buttonIconCls) ? c.buttonIconCls : [c.buttonIconCls]) : [];
                    c.buttonText = Ext.isDefined(c.buttonText) ? (Ext.isArray(c.buttonText) ? c.buttonText : [c.buttonText]) : [];
                    c.buttonTips = Ext.isDefined(c.buttonTips) ? (Ext.isArray(c.buttonTips) ? c.buttonTips : [c.buttonTips]) : [];

                    /*수정된 코드 
                    기존소스 문제점 : rowEdit 판단으로 buttonHandler 활성화 시킴. 
                                    사용자가 임의의 buttonHandler를 재구현 할수 없음.
                    변경 후 : 사용자가 buttonHandler 구현시, 재정의된 handler를 구현하도록 설계.  
                    if (this.rowEdit) {
                        c.buttonHandler = [];
                    } else {
                        c.buttonHandler = c.buttonHandler || [];
                        c.buttonHandler = Ext.isArray(c.buttonHandler) ? c.buttonHandler : [c.buttonHandler];
                    }
                    */
                    if (this.rowEdit) {
                        c.buttonHandler = c.buttonHandler || [];
                        c.buttonHandler = Ext.isArray(c.buttonHandler) ? c.buttonHandler : [c.buttonHandler];
                    }
                    tpl = [];
                  

                    Ext.each(c.buttons, function(b, index) {
                        b = Ext.util.Format.lowercase(b);
                        tpl.push('<div gridbtn="', b, '" class="x-treegrid-button-item x-toolbar"></div>');
                        if (this.rowEdit) {
                            /*추가*/
                            if(c.buttonHandler[index] == null ){
                                c.buttonHandler.push(this[b + 'Node']);
                            }
                            
                        }
                    }, this);
                    c.tpl = new Ext.XTemplate(tpl);
                    c.dataIndex = this.idProperty;
                    c.editable = false;
                }

                c = Ext.create(c);
            }
            c.init(this);
            columns.push(c);

            if (this.enableSort !== false && c.sortable !== false) {
                c.sortable = true;
                this.enableSort = true;
            }
        }

        this.columns = columns;
    },

    updateColumnWidths: function() {
        var cols = this.columns, colCount = cols.length, groups = this.outerCt.query('colgroup'), groupCount = groups.length, c, g, i, j;

        for (i = 0; i < colCount; i++) {
            c = cols[i];
            for (j = 0; j < groupCount; j++) {
                g = groups[j];
                g.childNodes[i].style.width = (c.hidden ? 0 : c.width) + 'px';
            }
        }

        for (i = 0, groups = this.innerHd.query('td'), len = groups.length; i < len; i++) {
            c = Ext.fly(groups[i]);
            if (cols[i] && cols[i].hidden) {
                c.addClass('x-treegrid-hd-hidden');
            } else {
                c.removeClass('x-treegrid-hd-hidden');
            }
        }

        var tcw = this.getTotalColumnWidth();
        Ext.fly(this.innerHd.dom.firstChild).setWidth(tcw + (this.scrollOffset || 0));
        this.outerCt.select('table').each(function(el, c, idx) {
            if (!el.hasClass('x-btn')) {
                el.setWidth(tcw);
            }
        }, this);
        this.syncHeaderScroll();
    },

    addNode: function(parentNode) {
        if (this.editor.editing || parentNode.getDepth() + 1 > this.depth) {
            return;
        }

        var o = {
            _isNewTreeGridNode: true
        };
        o[this.idProperty] = '';
        var cs = this.columns, len = cs.length, c;
        for (i = 0; i < len; i++) {
            c = cs[i];
            if (c.dataIndex) {
                o[c.dataIndex] = '';
            }
        }

        var node = new Ext.tree.TreeNode(o);
        if (parentNode.isLeaf()) {
            parentNode.leaf = false;
        } else if (parentNode.lastChild) {
            var degradeButton = this.getButton(parentNode.lastChild, 'degrade');
            if (degradeButton) {
                degradeButton.enable();
            }
        }
        parentNode.expand(false, false, function() {
            parentNode.appendChild(node);
            Ext.fly(node.ui.elNode).highlight(this.highlightColor);
            this.editNode(node);
        }, this);
    },

    updateNode: function(n) {
        if (this.editor.editing) {
            return;
        }
        this.editNode(n);
    },

    // private
    cancelEdit: function(n) {
        
        if (n.attributes._isNewTreeGridNode === undefined) {
         
             /*기존 소스에 추가*/
             var treeedit = n.getOwnerTree().editor;
             var changes = {}, cm = treeedit.tree.columns, c, fields = treeedit.items.items;

             for (var i = 0, len = cm.length; i < len; i++) {
                 c = cm[i];
                 if (!c.hidden && !c.buttons) {
                    var dindex = c.dataIndex;
                    changes[dindex] = fields[i].originalValue;
                 }
            }

            Ext.iterate(changes, function(name, value) {
                var index = 0, c;
                for (var i = 0, len = cm.length; i < len; i++) {
                    c = cm[i];
                    if (c.dataIndex == name) {
                        index = i;
                        break;
                    }
                }
                if (index == 0) {
                    n.ui.textNode.innerHTML = c.tpl ? c.tpl.apply(n.attributes) : value;
                } else {
                    n.ui.elNode.childNodes[index].firstChild.innerHTML = c.tpl ? c.tpl.apply(n.attributes) : value;
                }
            });

        }else{
          
            var parentNode = n.parentNode;
            if (parentNode.childNodes.length == 1) {
                parentNode.leaf = true;
            }
            n.remove();
            if (parentNode.childNodes.length < 1) {
                this.updateLeafIcon(parentNode);
            } else {
                var degradeButton = this.getButton(parentNode.lastChild, 'degrade');
                if (degradeButton) {
                    degradeButton.disable();
                }
            }

        }
    },

    // private
    saveNode: function(n, changes) {
        Ext.fly(n.ui.elNode).highlight(this.highlightColor);
        
        var params = {}, options = {
            node: n,
            changes: changes
        };
        Ext.applyIf(params, n.attributes);
        params.parentNodeId = n.parentNode.id;

        var cm = this.columns;
        Ext.iterate(changes, function(name, value) {
            var index = 0, c;
            for (var i = 0, len = cm.length; i < len; i++) {
                c = cm[i];
                if (c.dataIndex == name) {
                    index = i;
                    break;
                }
            }
            Ext.fly(n.ui.elNode.childNodes[index]).addClass('x-grid3-dirty-cell');
        });

        this.doRequest(n.attributes._isNewTreeGridNode ? 'add' : 'update', this.filterParams(params), this.processSave, options);
    },

    // private
    processSave: function(response, options) {

        try {
            var n = options.node, changes = options.changes;
        
            if (n.attributes._isNewTreeGridNode) {
                var resp = Ext.decode(response.responseText);
                n.attributes._isNewTreeGridNode = false;
            
                if (resp.id) {
                    n.setId(resp.id);
                }
                if (resp[this.idProperty]) {
                    n.attributes[this.idProperty] = resp[this.idProperty];
                }
            }
            var cm = this.columns;
            Ext.iterate(changes, function(name, value) {
                var index = 0, c;
                for (var i = 0, len = cm.length; i < len; i++) {
                    c = cm[i];
                    if (c.dataIndex == name) {
                        index = i;
                        break;
                    }
                }
                Ext.fly(n.ui.elNode.childNodes[index]).removeClass('x-grid3-dirty-cell');
            });
        } catch (e) {
        }
    },

    removeNode: function(n) {
        if (this.editor.editing) {
            return;
        }

        var parentNode = n.parentNode, previousSibling = n.previousSibling, nextSibling = n.nextSibling;
        if (parentNode.childNodes.length == 1) {
            parentNode.leaf = true;
        }
        n.remove();
        if (parentNode.childNodes.length < 1) {
            this.updateLeafIcon(parentNode);
        } else {
            if (previousSibling && previousSibling.isLast()) {
                var degradeButton = this.getButton(previousSibling, 'degrade');
                if (degradeButton) {
                    degradeButton.disable();
                }
            }
            if (nextSibling && nextSibling.isFirst()) {
                var upgradeButton = this.getButton(nextSibling, 'upgrade');
                if (upgradeButton) {
                    upgradeButton.disable();
                }
            }
        }

        var params = {
            id: n.id,
            parentNodeId: parentNode.id
        };
        params[this.idProperty] = n.attributes[this.idProperty];
        this.doRequest('remove', this.filterParams(params));
    },


    upgradeNode: function(n) {
        if ((this.editor && this.editor.editing) || n.isFirst()) {
            return;
        }
        n.parentNode.insertBefore(n, n.previousSibling);
        if (n.isFirst()) {
            this.getButton(n, 'upgrade').disable();
            this.getButton(n, 'degrade').enable();
            this.getButton(n.nextSibling, 'upgrade').enable();
            if (n.nextSibling.isLast()) {
                this.getButton(n.nextSibling, 'degrade').disable();
            }
        } else {
            this.getButton(n, 'degrade').enable();
            this.getButton(n.nextSibling, 'upgrade').enable();
            if (n.nextSibling.isLast()) {
                this.getButton(n.nextSibling, 'degrade').disable();
            }
        }
        Ext.fly(n.ui.elNode).highlight(this.highlightColor);

        var params = {
            id: n.id,
            parentNodeId: n.parentNode.id
        };
        params[this.idProperty] = n.attributes[this.idProperty];
        this.doRequest('upgrade', this.filterParams(params));
    },

    degradeNode: function(n) {
        if ((this.editor && this.editor.editing) || n.isLast()) {
            return;
        }
        n.parentNode.insertBefore(n, n.nextSibling.nextSibling);
        if (n.isLast()) {
            this.getButton(n, 'upgrade').enable();
            this.getButton(n, 'degrade').disable();
            if (n.previousSibling.isFirst()) {
                this.getButton(n.previousSibling, 'upgrade').disable();
            }
            this.getButton(n.previousSibling, 'degrade').enable();
        } else {
            this.getButton(n, 'upgrade').enable();
            this.getButton(n, 'degrade').enable();
            if (n.previousSibling.isFirst()) {
                this.getButton(n.previousSibling, 'upgrade').disable();
            }
            this.getButton(n.previousSibling, 'degrade').enable();
        }
        Ext.fly(n.ui.elNode).highlight(this.highlightColor);

        var params = {
            id: n.id,
            parentNodeId: n.parentNode.id
        };
        params[this.idProperty] = n.attributes[this.idProperty];
        this.doRequest('degrade', this.filterParams(params));
    },

    doRequest: function(action, params, callback, o) {
        if (!this.requestApi || !this.requestApi[action]) {
            return;
        }

        params = Ext.apply({
            requestAction: action
        }, params);
        o = Ext.applyIf(o || {}, {
            params: params
        });
        if (Ext.isString(this.requestApi[action])) {
            o.url = this.requestApi[action];
        } else {
            Ext.applyIf(o, this.requestApi[action]);
        }
        if (callback) {
            if (o.success) {
                o.success = callback.createDelegate(this).createSequence(o.success);
            } else if (o.callback) {
                o.callback = callback.createDelegate(this).createSequence(o.callback);
            } else {
                o.success = callback.createDelegate(this);
            }
        }
        Ext.Ajax.request(o);
    },

    // private
    getButton: function(n, k) {
        return n.buttons.get(k);
    },

    // private
    updateLeafIcon: function(n) {
        if (n.ui.elNode) {
            Ext.fly(n.ui.elNode).replaceClass("x-tree-node-collapsed", "x-tree-node-leaf");
        }
    },

    // private
    filterParams: function(params) {
        delete params.uiProvider;
        delete params.iconCls;
        delete params.loader;
        delete params.leaf;
        delete params.children;
        delete params._isNewTreeGridNode;
        return params;
    },

    disableButton: function(n, b) {
        n = Ext.isString(n) ? this.getNodeById(n) : n;
        n.disableButton(b);
    },

    enableButton: function(n, b) {
        n = Ext.isString(n) ? this.getNodeById(n) : n;
        n.enableButton(b);
    },

    hideButton: function(n, b) {
        n = Ext.isString(n) ? this.getNodeById(n) : n;
        n.hideButton(b);
    },

    showButton: function(n, b) {
        n = Ext.isString(n) ? this.getNodeById(n) : n;
        n.showButton(b);
    }
});

Ext.reg('edittreegrid', Ext.ux.tree.EditTreeGrid);

Ext.apply(Ext.ux.tree.TreeGridNodeUI.prototype, {
    renderElements: function(n, a, targetNode, bulkRender) {

        var t = n.getOwnerTree(), cols = t.columns, c = cols[0], i, buf, len;

        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        buf =
                ['<tbody class="x-tree-node">', '<tr ext:tree-node-id="', n.id, '" class="x-tree-node-el x-tree-node-leaf ', a.cls, '">', '<td class="x-treegrid-col">', '<span class="x-tree-node-indent">', this.indentMarkup, "</span>", '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow">', '<img src="', a.icon
                        || this.emptyIcon, '" class="x-tree-node-icon', (a.icon ? " x-tree-node-inline-icon" : ""), (a.iconCls ? " " + a.iconCls : ""), '" unselectable="on">', '<a hidefocus="on" class="x-tree-node-anchor" href="', a.href
                        ? a.href
                        : '#', '" tabIndex="1" ', a.hrefTarget ? ' target="' + a.hrefTarget + '"' : '', '>', '<span unselectable="on">', (c.tpl ? c.tpl.apply(a) : a[c.dataIndex] || c.text), '</span></a>', '</td>'];

        for (i = 1, len = cols.length; i < len; i++) {
            c = cols[i];
            buf.push('<td class="x-treegrid-col ', (c.cls ? c.cls : ''), '">', '<div unselectable="on" class="', c.buttons ? 'x-treegrid-button' : 'x-treegrid-text', '"', (c.align
                    ? ' style="text-align: ' + c.align + ';"'
                    : ''), '>', (c.tpl ? c.tpl.apply(a) : a[c.dataIndex]), '</div>', '</td>');
        }

        buf.push('</tr><tr class="x-tree-node-ct"><td colspan="', cols.length, '">', '<table class="x-treegrid-node-ct-table" cellpadding="0" cellspacing="0" style="table-layout: fixed; display: none; width: ', t.innerCt.getWidth(), 'px;"><colgroup>');
        for (i = 0, len = cols.length; i < len; i++) {
            buf.push('<col style="width: ', (cols[i].hidden ? 0 : cols[i].width), 'px;" />');
        }
        buf.push('</colgroup></table></td></tr></tbody>');

        if (bulkRender !== true && n.nextSibling && n.nextSibling.ui.getEl()) {
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin", n.nextSibling.ui.getEl(), buf.join(''));
        } else {
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(''));
        }

        if (!n.buttons) {
            n.buttons = new Ext.util.MixedCollection(false, function(o) {
                return o.itemId;
            });
        }

        var wrapEl = Ext.get(this.wrap);
        for (i = 0, len = cols.length; i < len; i++) {
            c = cols[i];
            if (c.buttons) {
                Ext.each(c.buttons, function(b, index) {
                    var handler = c.buttonHandler[index];
                    var btn = new Ext.Button({
                        itemId: b,
                        disabled: (n.attributes[b + 'BtnDisabled'] === true) || (b == 'add' && n.getDepth() == t.depth), // 最大深度树节点，禁用添加按钮
                        hidden: (n.attributes[b + 'BtnHidden'] === true),
                        iconCls: c.buttonIconCls[index],
                        text: c.buttonText[index],
                        tooltip: c.buttonTips[index],
                        handler: function() {
                            /*handler가 null인 경우에 사용자가 정의해놓은 handler를 구현하도록 설정.*/
                            // 기존 소스 :  
                            // if (b == 'remove'){
                            if ((b == 'remove') && (handler == null)) {
                                Ext.MessageBox.confirm(t.delConfirm, t.delConfirmMsg, function(btn) {
                                    if (btn == 'yes') {
										
                                        handler.call(t, n);
                                    }
                                });
                                return;
                            }
                            handler.call(t, n);
                        },
                        scope: t
                    });
                    if ((b == 'upgrade' && n.isFirst()) || (b == 'degrade' && n.isLast())) {
                        btn.disable();
                    }
                    n.buttons.add(btn);
                    btn.render(wrapEl.child('[gridbtn=' + b + ']'));
                }, this);
            }
        }

        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1].firstChild.firstChild;
        var cs = this.elNode.firstChild.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        this.anchor = cs[3];
        this.textNode = cs[3].firstChild;
    }
});

Ext.apply(Ext.tree.TreeNode.prototype, {
    disableButton: function(b) {
        if (b == 'upgrade' || b == 'degrade') {
            return;
        }
        if (b) {
            this.buttons.get(b).disable();
        }
    },

    enableButton: function(b) {
        if (b == 'upgrade' || b == 'degrade') {
            return;
        }
        if (b) {
            this.buttons.get(b).enable();
        }
    },

    hideButton: function(b) {
        if (b) {
            this.buttons.get(b).hide();
        }
    },

    showButton: function(b) {
        if (b) {
            this.buttons.get(b).show();
        }
    },

    originalTreeNodeDestroy: Ext.tree.TreeNode.prototype.destroy,
    destroy: function(silent) {
        if (this.buttons) {
            this.buttons.each(function(btn) {
                Ext.destroy(btn);
            }, this);
            this.buttons.clear()
        }
        this.originalTreeNodeDestroy.call(this, silent);
    }
});
