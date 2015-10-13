Ext.namespace('sitools.extension.component.datasetExplorerOchart');

Ext.define('sitools.extension.component.datasetExplorerOchart.OChart', {
    extend: 'Ext.Component',
    alias: 'widget.ochart',
    itemId: 'ochart',
    requires: [
        'Ext.LoadMask',
        'Ext.data.StoreManager',
        'Ext.dom.Query',
        'sitools.extension.model.OChartModel',
        'Ext.dd.ScrollManager'
    ],

    mixins: {
        bindable: 'Ext.util.Bindable'
    },

    baseCls: Ext.baseCSSPrefix + 'ochart',

    /**
     * In some places it's need to render full tables because <IE9 have some bugs and makes tr and table readonly
     * @private
     */
    renderBuffer: document.createElement('div'),

    /**
     * @cfg {Boolean} rootVisible=true true to include the root node in the chart.
     */
    rootVisible: true,

    /**
     * @cfg {Boolean} toolsVisible=true true to show the item floating tools.
     */
    toolsVisible: true,

    /**
     * @cfg {Ext.data.NodeInterface} root=null The chart´s root node or null for the store root node.
     */
    root: null,

    /**
     * @cfg {Boolean} autoLoadStore=tru
     * If this config is true and the store isn't loaded or already loading, the component will trigger a load command to the store
     * during component initialization.
     */
    autoLoadStore: true,

    /**
     * @cfg {String} displayField="text" The field used to render the node contents.
     */
    displayField: 'text',

    /**
     * @cfg {Boolean/Object} loadMask
     * False to disable a load mask from displaying while the view is loading. This can also be a
     * {@link Ext.LoadMask} configuration object.
     */
    loadMask: true,

    /**
     * @cfg {String} loadingText
     * A string to display during data load operations.  If specified, this text will be
     * displayed in a loading div and the view's contents will be cleared while loading, otherwise the view's
     * contents will continue to display normally until the new data is loaded and the contents are replaced.
     */
    loadingText: 'Loading...',

    /**
     * @cfg {Boolean} loadingUseMsg
     * Whether or not to use the loading message.
     * @private
     */
    loadingUseMsg: true,

    /**
     * @cfg {Boolean} allowContainerDrop=true
     * False to disable dropping itens on the container, true to allow dropping itens on the container.
     * When itens are dropped on the container they will be appended to the root node.
     */
    allowContainerDrop: true,

    /**
     * @cfg {String} loadingCls
     * The CSS class to apply to the loading message element. Defaults to Ext.LoadMask.prototype.msgCls "x-mask-loading".
     */

    /**
     * @cfg {Number} lineWeight=1 Weight of node connector lines.
     */
    lineWeight: 1,

    /**
     * @property {String} lineColor="#000" HTML color to use for the node connector lines.
     */
    lineColor: '#000',

    /**
     * @cfg {Number} levelSpacing=15 Space in pixels between the parent and children nodes.
     */
    levelSpacing: 15,

    /**
     * @cfg {Number} nodeSpacing=10 Margin in pixels between adjacent nodes.
     */
    nodeSpacing: 10,

    /**
     * @cfg {String} itemCls=null Additional class for the node content element.
     */
    itemCls: null,

    /** inheritdoc */
    renderTpl: ['<table class="{baseCls}-wrap" cellpadding="0" cellspacing="0" border="0"><tbody><tr class="{baseCls}-container"></tr></tbody></table>'],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} downLineTpl Node down line connector template
     * @private
     */
    downLineTpl: [
        '<tr class="{view.baseCls}-lines {view.downLineCls}">',
        '<td colspan="{node.childNodes.length}">',
        '<div class="{view.baseCls}-left" style="border-top-width: {view.lineWeight}px; height: {view.levelSpacing}px; border-color:{view.lineColor} !important;"></div>',
        '<div class="{view.baseCls}-right" style="border-top-width: {view.lineWeight}px; border-left-width: {view.lineWeight}px; height: {view.levelSpacing}px; border-color:{view.lineColor} !important;"></div>',
        '</td>',
        '</tr>',
        '<tr class="{view.expanderRowCls}">',
        '<td colspan="{node.childNodes.length}">',
        '<span class="{view.expanderCmpCls}{[values.view.expanderCls ? " "+values.view.expanderCls : ""]}" data-qtip="{view.expandTip:htmlEncode}"></span>',
        '</td>',
        '</tr>'
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} childrenLineTpl Simple child inner line connector template
     * @private
     */
    childrenLineTpl: [
        '<div class="{view.baseCls}-left" style="border-top-width:{view.lineWeight}px; height: {view.levelSpacing}px; border-color:{view.lineColor} !important;"></div>',
        '<div class="{view.baseCls}-right" style="border-top-width:{view.lineWeight}px; border-left-width:{view.lineWeight}px; height: {view.levelSpacing}px; border-color:{view.lineColor} !important;"></div>'
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} childrenLinesTpl Multiple child line connector template
     * @private
     */
    childrenLinesTpl: [
        '<tpl if="node.childNodes.length &gt; 1">',
        //multiple lines
        '<tr class="{view.baseCls}-lines {view.childrenLinesCls}">',
        '{[this.childLines(values.view, values.node.childNodes)]}',
        '</tr>',
        '</tpl>',
        {
            childLines: function (view, nodes) {
                var out = [],
                    len = nodes.length,
                    last = len - 1,
                    cls = view.baseCls,
                    clsLeft = cls + '-left',
                    clsRight = cls + '-right',
                    lineWeight = view.lineWeight,
                    lineColor = view.lineColor,
                    height = view.levelSpacing,
                    div = '<div class="{0}" style="border-color: {1}; border-top-width: {2}px; border-left-width: {3}px; border-right-width: {4}px; height: {5}px;"></div>',
                    format = Ext.String.format,
                    i, td;

                for (i = 0; i < len; ++i) {
                    td = '<td';
                    //it's a first or last line?
                    if (i == 0) td += ' class="' + cls + '-first"';
                    else if (i == last) td += ' class="' + cls + '-last"';
                    td += '>';

                    td += format(div, clsLeft, lineColor, lineWeight, 0, i == last ? lineWeight : 0, height);
                    td += format(div, clsRight, lineColor, lineWeight, lineWeight, 0, height);

                    td += '</td>';

                    out.push(td);
                }

                return out.join('');
            }
        }
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} childrenTpl Container with children template
     * @private
     */
    childrenTpl: [
        '<tr class="{view.containerCls}">',
        '{%values.view.renderNodes(values.node.childNodes, out)%}',
        '</tr>'
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} containerTpl Simple container template
     * @private
     */
    containerTpl: '<tr class="{view.containerCls}"></tr>',

    /**
     * Record node image format
     * @private
     */
    imgText: '<img src="{0}" class="{1}" />',

    /**
     * Inner node structure template
     * @private
     */
    innerNodeTpl: [
        '<table cellpadding="0" cellspacing="0" border="0"><tbody>',
        //node content
        '<tr class="{view.nodeContentRowCls}">',
        '<td colspan="{node.childNodes.length}">',
        '<tpl if="node.data.icon || node.data.iconCls">',
        '{[Ext.String.format(values.view.imgText, values.node.data.icon ? values.node.data.icon : Ext.BLANK_IMAGE_URL, values.node.data.iconCls ? values.node.data.iconCls : "")]}',
        '</tpl>',
        '<span id="{nodeId}" class="{view.nodeContentCls}' +
        '{[values.view.itemCls ? " "+values.view.itemCls : ""]}',
        '{[values.node.get("cls") ? " "+values.node.get("cls") : ""]}',
        '" data-recordId="{[values.view.getRecordId(values.node)]}"',
        '<tpl if="node.data.qtitle && node.data.qtip">',
        ' data-qtitle="{node.data.qtitle:htmlEncode}"',
        '</tpl>',
        '<tpl if="node.data.qtip">',
        ' data-qtip="{node.data.qtip:htmlEncode}"',
        '</tpl>',
        '>{[values.view.renderItem(values.view, values.node)]}',
        '</span>',
        '</td>',
        '</tr>',

        //children
        '<tpl if="this.handleChildren(node)">',
        //down line
        '{[values.view.downLineTpl.apply(values)]}',

        //children lines
        '{[values.view.childrenLinesTpl.apply(values)]}',

        //children container
        '{[this.renderChildren(values)]}',
        '</tpl>',
        '</tbody></table>',
        {
            renderChildren: function (values) {
                var out = [];
                values.view.childrenTpl.applyOut(values, out);
                return out.join('');
            },
            handleChildren: function (node) {
                return (node.childNodes.length || (!node.isLeaf() && !node.get('loaded')));
            }
        }
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]}} nodeTpl Full node component template
     * @private
     */
    nodeTpl: [
        '<td class="{view.nodeCls}{[!values.node.isLeaf() && !values.node.isExpanded() ? " " + values.view.collapseCls: ""]}" style="padding: 0 {view.nodeSpacing}px;">',
        '{[values.view.innerNodeTpl.apply(values)]}',
        '</td>'
    ],

    /**
     * @cfg {Ext.XTemplate/String/String[]} itemTpl Template used to render the node's content.
     */
    itemTpl: '{text}',

    /**
     * @cfg {String} wrapperSelector Component wrapper CSS selector.
     * @private
     */
    wrapperSelector: '.' + Ext.baseCSSPrefix + 'ochart-wrap',

    /**
     * @cfg {String} itemSelector Node content CSS selector
     * private
     */
    itemSelector: '.' + Ext.baseCSSPrefix + 'ochart-node-content',

    /**
     * @cfg {String} itemRowSelector Item content row CSS selector
     * private
     */
    itemRowSelector: '.' + Ext.baseCSSPrefix + 'ochart-node-row',

    /**
     * @cfg {String} nodeItemContainerSelector Node content row CSS selector
     * private
     */
    nodeItemContainerSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-node-row',

    /**
     * @property {String} nodeSelector Node container CSS selector
     * private
     */
    nodeSelector: '.' + Ext.baseCSSPrefix + 'ochart-node',

    nodeBodySelector: 'table > tbody',

    nodeContainerSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-container',

    expanderSelector: '.' + Ext.baseCSSPrefix + 'ochart-expander',

    inlineExpanderContainerSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-expander-row',

    inlineExpanderContentSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-expander-row > td',

    downLineContainerSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-down',

    downLineSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-down > td',

    childrenLinesSelector: 'table > tbody > .' + Ext.baseCSSPrefix + 'ochart-children-lines',

    expandTip: 'Click here to expand this node.',

    collapseTip: 'Click here to collapse this node.',

    addBeforeTip: 'Add a new item before this.',

    addAfterTip: 'Add a new item after this.',

    addChildTip: 'Add a new child node to this item.',

    removeItemTip: 'Remove this item.',

    /**
     * @cfg {Boolean} trackOver
     * When `true` the {@link #overItemCls} will be applied to nodes when hovered over.
     * This in return will also cause {#highlightitem} and
     * {#unhighlightitem} events to be fired.
     *
     * Enabled automatically when the {@link #overItemCls} config is set.
     */
    trackOver: false,

    /**
     * @cfg {Number} [mouseOverOutBuffer=20]
     * The number of milliseconds to buffer mouseover and mouseout event handling on view items.
     *
     * Configure this as `false` to process mouseover and mouseout events immediately.
     */
    mouseOverOutBuffer: 20,

    inputTagRe: /^textarea$|^input$/i,

    /**
     * @cfg {String} overItemCls='x-ochart-over-node' Mouse over item class.
     */
    overItemCls: Ext.baseCSSPrefix + 'ochart-over-node',

    /**
     * @cfg {String} expanderCls Expand tool class.
     */
    expanderCls: null,

    /**
     * @cfg {String} addBeforeCls Add before tool class.
     */
    addBeforeCls: null,

    /**
     * @cfg {String} addAfterCls Add after tool class.
     */
    addAfterCls: null,

    /**
     * @cfg {String} addChildCls Add child tool class.
     */
    addChildCls: null,

    /**
     * @cfg {String} removeItemCls Remove item tool class.
     */
    removeItemCls: null,

    /**
     * @cfg {String} selectedItemCls
     * A CSS class to apply to each selected item in the view.
     */
    selectedItemCls: Ext.baseCSSPrefix + 'item-selected',

    /**
     * @cfg {String} collapseCls
     * A CSS class to apply to each item that is collapsed in the view.
     */
    collapseCls: Ext.baseCSSPrefix + 'item-collapsed',

    inheritableStatics: {
        /**
         * Event maps
         *
         * @static
         * @protected
         */
        EventMap: {
            mousedown: 'MouseDown',
            mouseup: 'MouseUp',
            click: 'Click',
            dblclick: 'DblClick',
            contextmenu: 'ContextMenu',
            mouseover: 'MouseOver',
            mouseout: 'MouseOut',
            mouseenter: 'MouseEnter',
            mouseleave: 'MouseLeave',
            keydown: 'KeyDown',
            focus: 'Focus'
        }
    },

    /**
     * @cfg {String} triggerEvent="itemclick"
     * Trigger event used by the selection model to handle item click
     *
     * @private
     */
    triggerEvent: 'itemclick',

    /**
     * @cfg {String} triggerCtEvent="containerclick"
     * Trigger event used by the selection model to handle container click
     *
     * @private
     */
    triggerCtEvent: 'containerclick',

    /** @inheritdoc */
    initComponent: function () {
        var me = this,
            store = me.store,
            root = me.root;

        /**
         * @cfg {String} wrapperCls Component wrapper class
         * @private
         */
        me.wrapperCls = me.baseCls + '-wrap';

        /**
         * @cfg {String} containerCls Node container class
         * @private
         */
        me.containerCls = me.baseCls + '-container';

        /**
         * @cfg {String} nodeCls Node component class
         * @private
         */
        me.nodeCls = me.baseCls + '-node';

        /**
         * @cfg {String} nodeContentRowCls Node's content line class
         * @private
         */
        me.nodeContentRowCls = me.baseCls + '-node-row';

        /**
         * @cfg {String} nodeContentCls Node's content class
         * @private
         */
        me.nodeContentCls = me.baseCls + '-node-content';

        /**
         * @cfg {String} downLineCls Node's down line connector class
         * @private
         */
        me.downLineCls = me.baseCls + '-down';

        /**
         * @cfg {String} expanderRowCls Inline expander row class
         * @private
         */
        me.expanderRowCls = me.baseCls + '-expander-row';

        /**
         * @cfg {String} expanderCmpCls Expander component class
         * @private
         */
        me.expanderCmpCls = me.baseCls + '-expander';

        /**
         * @cfg {String} addNodeCmpCls Add node component class
         * @private
         */
        me.addNodeCmpCls = me.baseCls + '-add';

        /**
         * @cfg {String} removeNodeCmpCls Remove node component class
         * @private
         */
        me.removeNodeCmpCls = me.baseCls + '-remove';

        /**
         * @cfg {String} childrenLinesCls Children connector lines row
         * @private
         */
        me.childrenLinesCls = me.baseCls + '-children-lines';

        //prepare templates
        me.rootTpl = Ext.XTemplate.getTpl(this, 'rootTpl');
        me.nodesTpl = Ext.XTemplate.getTpl(this, 'nodesTpl');
        me.nodeTpl = Ext.XTemplate.getTpl(this, 'nodeTpl');
        me.innerNodeTpl = Ext.XTemplate.getTpl(this, 'innerNodeTpl');
        me.downLineTpl = Ext.XTemplate.getTpl(this, 'downLineTpl');
        me.childrenLinesTpl = Ext.XTemplate.getTpl(this, 'childrenLinesTpl');
        me.childrenLineTpl = Ext.XTemplate.getTpl(this, 'childrenLineTpl');
        me.childrenTpl = Ext.XTemplate.getTpl(this, 'childrenTpl');
        me.containerTpl = Ext.XTemplate.getTpl(this, 'containerTpl');
        me.itemTpl = Ext.XTemplate.getTpl(this, 'itemTpl');

        //adjust spacings
        if (me.levelSpacing < 5) me.levelSpacing = 5;

        //create mouse over buffer if need
        if (me.mouseOverOutBuffer) {
            me.handleMouseOverOrOut = Ext.Function.createBuffered(me.handleMouseOverOrOut, me.mouseOverOutBuffer, me);
            me.lastMouseOverOutEvent = new Ext.EventObjectImpl();
        }

        if (me.overItemCls) {
            me.trackOver = true;
        }

        this.addEvents(
            /**
             * @event beforeitemmousedown
             * Fires before the mousedown event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemmousedown',
            /**
             * @event beforeitemmouseup
             * Fires before the mouseup event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemmouseup',
            /**
             * @event beforeitemmouseenter
             * Fires before the mouseenter event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemmouseenter',
            /**
             * @event beforeitemmouseleave
             * Fires before the mouseleave event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemmouseleave',
            /**
             * @event beforeitemclick
             * Fires before the click event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemclick',
            /**
             * @event beforeitemdblclick
             * Fires before the dblclick event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemdblclick',
            /**
             * @event beforeitemcontextmenu
             * Fires before the contextmenu event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'beforeitemcontextmenu',
            /**
             * @event beforeitemkeydown
             * Fires before the keydown event on an item is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object. Use {@link Ext.EventObject#getKey getKey()} to retrieve the key that was pressed.
             */
            'beforeitemkeydown',
            /**
             * @event itemmousedown
             * Fires when there is a mouse down on an item
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemmousedown',
            /**
             * @event itemmouseup
             * Fires when there is a mouse up on an item
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemmouseup',
            /**
             * @event itemmouseenter
             * Fires when the mouse enters an item.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemmouseenter',
            /**
             * @event itemmouseleave
             * Fires when the mouse leaves an item.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemmouseleave',
            /**
             * @event itemclick
             * Fires when an item is clicked.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemclick',
            /**
             * @event itemdblclick
             * Fires when an item is double clicked.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemdblclick',
            /**
             * @event itemcontextmenu
             * Fires when an item is right clicked.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object
             */
            'itemcontextmenu',
            /**
             * @event itemkeydown
             * Fires when a key is pressed while an item is currently selected.
             * @param {Ext.view.View} this
             * @param {Ext.data.Model} record The record that belongs to the item
             * @param {HTMLElement} item The item's element
             * @param {Number} index The item's index
             * @param {Ext.EventObject} e The raw event object. Use {@link Ext.EventObject#getKey getKey()} to retrieve the key that was pressed.
             */
            'itemkeydown',
            /**
             * @event beforecontainermousedown
             * Fires before the mousedown event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainermousedown',
            /**
             * @event beforecontainermouseup
             * Fires before the mouseup event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainermouseup',
            /**
             * @event beforecontainermouseover
             * Fires before the mouseover event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainermouseover',
            /**
             * @event beforecontainermouseout
             * Fires before the mouseout event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainermouseout',
            /**
             * @event beforecontainerclick
             * Fires before the click event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainerclick',
            /**
             * @event beforecontainerdblclick
             * Fires before the dblclick event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainerdblclick',
            /**
             * @event beforecontainercontextmenu
             * Fires before the contextmenu event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'beforecontainercontextmenu',
            /**
             * @event beforecontainerkeydown
             * Fires before the keydown event on the container is processed. Returns false to cancel the default action.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object. Use {@link Ext.EventObject#getKey getKey()} to retrieve the key that was pressed.
             */
            'beforecontainerkeydown',
            /**
             * @event containermouseup
             * Fires when there is a mouse up on the container
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containermouseup',
            /**
             * @event containermouseover
             * Fires when you move the mouse over the container.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containermouseover',
            /**
             * @event containermouseout
             * Fires when you move the mouse out of the container.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containermouseout',
            /**
             * @event containerclick
             * Fires when the container is clicked.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containerclick',
            /**
             * @event containerdblclick
             * Fires when the container is double clicked.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containerdblclick',
            /**
             * @event containercontextmenu
             * Fires when the container is right clicked.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object
             */
            'containercontextmenu',
            /**
             * @event containerkeydown
             * Fires when a key is pressed while the container is focused, and no item is currently selected.
             * @param {Ext.view.View} this
             * @param {Ext.EventObject} e The raw event object. Use {@link Ext.EventObject#getKey getKey()} to retrieve the key that was pressed.
             */
            'containerkeydown',

            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#selectionchange
             */
            'selectionchange',
            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#beforeselect
             */
            'beforeselect',
            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#beforedeselect
             */
            'beforedeselect',
            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#select
             */
            'select',
            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#deselect
             */
            'deselect',
            /**
             * @event
             * @inheritdoc Ext.selection.DataViewModel#focuschange
             */
            'focuschange',

            /**
             * @event highlightitem
             * Fires when a node is highlighted using keyboard navigation, or mouseover.
             * @param {Ext.view.View} view This View Component.
             * @param {Ext.Element} node The highlighted node.
             */
            'highlightitem',

            /**
             * @event unhighlightitem
             * Fires when a node is unhighlighted using keyboard navigation, or mouseout.
             * @param {Ext.view.View} view This View Component.
             * @param {Ext.Element} node The previously highlighted node.
             */
            'unhighlightitem',

            /**
             * @event additem
             * Fires when one of add tools is clicked.
             * @param {Ext.view.View} view This View Component.
             * @param {Ext.data.NodeInterface} record The reference record for add action.
             * @param {"before/after/child"} where Where to add the new record in relation to the reference record.
             * @param {Ext.dom.Element} node The node element.
             */
            'additem',

            /**
             * @event removeitem
             * Fires when remove item tool is clicked.
             * @param {Ext.view.View} view This View Component.
             * @param {Ext.data.NodeInterface} record The reference record for remove action.
             * @param {Ext.dom.Element/null} node The node element.
             */
            'removeitem',

            /**
             * @event itemupdate
             * Fires when one of nodes has been changed.
             * @param {Ext.view.View} view This View Component.
             * @param {Ext.data.NodeInterface} record The reference record for add action.
             * @param {Ext.dom.Element} node The node element.
             */
            'itemupdate'
        );

        //create store if needed
        if (Ext.isString(store)) {
            //it's an store id
            store = Ext.StoreManager.lookup(store);
        }
        else if (!store || Ext.isObject(store) && !store.isStore) {
            //it's an store object declaration
            store = me.store = new Ext.data.TreeStore(Ext.apply({
                root: root,
                fields: me.fields,
                model: me.model
            }), store);


        }
        else if (root) {
            store = me.store = Ext.data.StoreManager.lookup(store);
        }

        //sets the root node
        me.root = root || store.getRootNode();

        //binds the store
        me.bindStore(store, true, 'dataStore');
        me.callParent(arguments);
    },

    /** @inheritdoc */
    onRender: function () {
        var me = this;
        me.callParent(arguments);
        me.el.ddScrollConfig = {
            vthresh: 25,
            hthresh: 25,
            frequency: 300,
            increment: 100
        };
    },

    beforeRender: function () {
        var me = this;
        me.callParent(arguments);
        //me.protoEl.set('unselectable', true);
        me.getSelectionModel().beforeViewRender(me);
    },

    afterRender: function () {
        var me = this,
            store = this.store,
            onMouseOverOut = me.mouseOverOutBuffer ? me.onMouseOverOut : me.handleMouseOverOrOut;
        me.callParent();

        //todo handle keyboard an context menu
        //init component input event handler
        me.mon(me.el, {
            scope: me,
            /*
             * We need to make copies of this since some of the events fired here will end up triggering
             * a new event to be called and the shared event object will be mutated. In future we should
             * investigate if there are any issues with creating a new event object for each event that
             * is fired.
             */
            freezeEvent: true,
            click: me.handleEvent,
            mousedown: me.handleEvent,
            mouseup: me.handleEvent,
            dblclick: me.handleEvent,
            contextmenu: me.handleEvent,
            keydown: me.handleEvent,
            mouseover: me.onMouseOverOut,
            mouseout: me.onMouseOverOut
        });

        //there's not a root node?
        if (!me.store.getCount()) {
            //load the store if need and postpone rendering
            if (!store.isLoading() && me.autoLoadStore) {
                store.load();
            }
        }
        else {
            this.refresh();
        }

        this.getSelectionModel().bindComponent(this);

        //inline expander tool config
        me.el.on({
            scope: me,
            delegate: me.expanderSelector,
            mouseover: me.onExpanderMouseOver,
            mouseout: me.onExpanderMouseOut,
            click: me.onInlineExpanderClick
        });
        //init floating tools
        me.expandTool = Ext.create('Ext.Component', {
            floating: true,
            border: 0,
            autoEl: {
                tag: 'div',
                'data-qtip': Ext.util.Format.htmlEncode(me.collapseTip)
            },
            shadow: false,
            hidden: true,
            width: 16,
            height: 16,
            cls: me.expanderCmpCls + (me.expanderCls ? ' ' + me.expanderCls : ''),
            listeners: {
                scope: me,
                render: function () {
                    var el = this.expandTool.getEl();
                    el.on('click', this.onItemExpandClick, this);
                }
            }
        });
        me.addBeforeTool = Ext.create('Ext.Component', {
            floating: true,
            border: 0,
            autoEl: {
                tag: 'div',
                'data-qtip': Ext.util.Format.htmlEncode(me.addBeforeTip)
            },
            shadow: false,
            hidden: true,
            width: 16,
            height: 16,
            cls: me.addNodeCmpCls + (me.addBeforeCls ? ' ' + me.addBeforeCls : ''),
            listeners: {
                scope: me,
                render: function () {
                    var el = this.addBeforeTool.getEl();
                    this.mon(el, 'click', this.onItemAddBeforeClick, this);
                }
            }
        });

        me.addAfterTool = Ext.create('Ext.Component', {
            floating: true,
            border: 0,
            autoEl: {
                tag: 'div',
                'data-qtip': Ext.util.Format.htmlEncode(me.addAfterTip)
            },
            shadow: false,
            hidden: true,
            width: 16,
            height: 16,
            cls: me.addNodeCmpCls + (me.addAfterCls ? ' ' + me.addAfterCls : ''),
            listeners: {
                scope: me,
                render: function () {
                    var el = this.addAfterTool.getEl();
                    this.mon(el, 'click', this.onItemAddAfterClick, this);
                }
            }
        });

        me.addChildTool = Ext.create('Ext.Component', {
            floating: true,
            border: 0,
            autoEl: {
                tag: 'div',
                'data-qtip': Ext.util.Format.htmlEncode(me.addChildTip)
            },
            shadow: false,
            hidden: true,
            width: 16,
            height: 16,
            cls: me.addNodeCmpCls + (me.addChildCls ? ' ' + me.addChildCls : ''),
            listeners: {
                scope: me,
                render: function () {
                    var el = this.addChildTool.getEl();
                    this.mon(el, 'click', this.onItemAddChildClick, this);
                }
            }
        });

        me.removeItemTool = Ext.create('Ext.Component', {
            floating: true,
            border: 0,
            autoEl: {
                tag: 'div',
                'data-qtip': Ext.util.Format.htmlEncode(me.removeItemTip)
            },
            shadow: false,
            hidden: true,
            width: 16,
            height: 16,
            cls: me.removeNodeCmpCls + (me.removeItemCls ? ' ' + me.removeItemCls : ''),
            listeners: {
                scope: me,
                render: function () {
                    var el = this.removeItemTool.getEl();
                    this.mon(el, 'click', this.onItemRemoveClick, this);
                }
            }
        });
    },

    /** @inheritdoc */
    onDestroy: function () {
        var me = this;

        //unbind store
        me.bindStore(null);

        //fre component input handler
        me.mun(me.el, {
            scope: me,
            /*
             * We need to make copies of this since some of the events fired here will end up triggering
             * a new event to be called and the shared event object will be mutated. In future we should
             * investigate if there are any issues with creating a new event object for each event that
             * is fired.
             */
            freezeEvent: true,
            click: me.handleEvent,
            mousedown: me.handleEvent,
            mouseup: me.handleEvent,
            dblclick: me.handleEvent,
            //contextmenu: me.handleEvent,
            //keydown    : me.handleEvent,
            mouseover: me.onMouseOverOut,
            mouseout: me.onMouseOverOut
        });

        //free inline expand tool handler
        me.el.un({
            scope: me,
            delegate: me.expanderSelector,
            mouseover: me.onExpanderMouseOver,
            mouseout: me.onExpanderMouseOut,
            click: me.onInlineExpanderClick
        });

        //fre the floating tools handlers
        if (me.expandTool) {
            me.mun(me.expandTool.getEl(), 'click', me.onItemExpandClick, me);
            me.expandTool.destroy();
        }
        if (me.addBeforeTool) {
            me.mun(me.addBeforeTool.getEl(), 'click', me.onItemAddBeforeClick, me);
            me.addBeforeTool.destroy();
        }
        if (me.addAfterTool) {
            me.mun(me.addAfterTool.getEl(), 'click', me.onItemAddAfterClick, me);
            me.addAfterTool.destroy();
        }
        if (me.addChildTool) {
            me.mun(me.addChildTool.getEl(), 'click', me.onItemAddChildClick, me);
            me.addChildTool.destroy();
        }
        if (me.removeItemTool) {
            me.mun(me.removeItemTool.getEl(), 'click', me.onItemRemoveClick, me);
            me.removeItemTool.destroy();
        }

        //free the selection model
        if (me.selModel) {
            me.selModel.destroy();
        }


        me.callParent(arguments);
    },

    /**
     * Sets the root node for the component
     * @param node
     */
    setRootNode: function (node) {
        var me = this
        store = this.store;
        this.root = node;

        //if root is set by loading store, postpone for onload event
        if (!store.isLoading() && store.getCount() && me.initialLoad) this.refresh();
    },

    /**
     * Returns the root node for this instance
     *
     * @returns {Ext.data.NodeInterface/null} The root node
     */
    getRootNode: function () {
        return this.root;
    },

    /** @inheritdoc */
    onBindStore: function (store, initial) {
        var me = this;
        me.store = store;
        me.initialLoad = false;

        // Bind the store to our selection model unless it's the initial bind.
        // Initial bind takes place in afterRender
        // Same for first rendering
        if (!initial) {
            me.getSelectionModel().bindStore(store);
            me.refresh();
        }
    },

    /** @inheritdoc */
    onUnbindStore: function (store, initial) {
        var me = this;
        me.store = null;
    },

    /** @inheritdoc */
    getStoreListeners: function () {
        var me = this,
            listeners = {
                idchanged: me.onStoreIdChanged,
                load: me.onStoreLoad,
                beforeload: me.onStoreBeforeLoad,
                rootchange: me.onStoreRootChange,
                beforeexpand: me.onStoreBeforeExpand,
                expand: me.onStoreExpand,
                beforecollapse: me.onStoreBeforeCollapse,
                collapse: me.onStoreCollapse,
                //datachanged   : me.onStoreDataChanged,
                append: me.onStoreAppend,
                insert: me.onStoreInsert,
                clear: me.onStoreClear,
                refresh: me.onDataRefresh,
                update: me.onStoreUpdate
            };

        listeners['remove'] = me.onStoreRemove;
        if (Ext.versions.extjs.isLessThan('4.2.0')) {
            //there´s a bug in code below 4.2.0, update event is not fired for treestore
            //so use a flag to solve it in the component code
            me.needNodeJoin = true;
        }
        else {
            listeners['bulkremove'] = me.onStoreBulkRemove;
        }

        return listeners;
    },

    /**
     * Draws/Redraws the full component's content
     */
    refresh: function () {
        var me = this,
            el = me.el,
            store = me.store,
            root = me.root,
            body, out;

        //not rendered yet or already refreshing, so postpone
        if (!me.rendered || me.refreshingView) return;

        body = me.getChildrenContainer(me.el);

        me.hideTools();

        //its rendered but doesnt have associated data??
        //so clear the component
        if (!store || !root) {
            me.renderTpl.overwrite(me.el, {});
            return;
        }

        me.suspendEvents();

        me.refreshingView = true;

        me.initialLoad = true;

        out = ['<table class="' + me.wrapperCls + '"><tr class="' + me.containerCls + '">'];
        me.renderNodes(me.rootVisible ? [root] : root.childNodes, out);
        out.push('</tr></table>');

        Ext.fly(me.el).setHTML(out.join(''));

        me.refreshingView = false;

        me.resumeEvents();
    },

    /**
     *
     * @param root
     *
     * @todo Precisa acertar, pois o nó pode não ser a raiz da arvore e pode ser necessário ler até encontrar
     */
    onStoreRootChange: function (root) {
        var me = this;
        //there is a bug with update event for ext below 4.2.0
        //so we need fire this events any way
        if (!me.needNodeJoin) {
            me.store.un('append', me.onStoreAppend, me);
            me.store.un('update', me.onStoreUpdate, me);
        }
        me.storeRootLoad = true;
        me.setRootNode(root);
    },

    onStoreBeforeExpand: function (record) {
        var me = this,
            item = me.getItemByRecord(record);

        if (!item) return;
        me.nodeRegionOnExpand = Ext.fly(item).getRegion();
    },

    /**
     * Expands a record that is loaded in the view.
     *
     * If an animated collapse or expand of the record is in progress, this call will be ignored.
     * @param {Ext.data.Model} record The record to expand
     * @param {Boolean} [deep] True to expand nodes all the way down the tree hierarchy.
     * @param {Function} [callback] The function to run after the expand is completed
     * @param {Object} [scope] The scope of the callback function.
     */
    expand: function (record, deep, callback, scope) {
        var me = this,
            result;

        // Need to suspend layouts because the expand process makes multiple changes to the UI
        // in addition to inserting new nodes. Folder and elbow images have to change, so we
        // need to coalesce all resulting layouts.
        Ext.suspendLayouts();
        result = record.expand(deep, callback, scope);
        Ext.resumeLayouts(true);
        return result;
    },

    collapse: function (record, deep, callback, scope) {
        var me = this,
            result;

        // Need to suspend layouts because the expand process makes multiple changes to the UI
        // in addition to inserting new nodes. Folder and elbow images have to change, so we
        // need to coalesce all resulting layouts.
        Ext.suspendLayouts();
        result = record.collapse(deep, callback, scope);
        Ext.resumeLayouts(true);
        return result;
    },

    onStoreExpand: function (record) {
        var me = this,
            item = me.getItemByRecord(record),
            node,
            cls = me.collapseCls,
            ct;

        //verify if record is in the component
        if (!item) return;

        node = me.getNodeFromChildEl(item);

        if (!node) return;

        node.removeCls(cls);

        if (me.nodeRegionOnExpand) {
            me.preserveScroll(item);
        }
        else me.focusNode(record);
        me.nodeRegionOnExpand = null;
    },

    onStoreBeforeCollapse: function (record) {
        var me = this,
            item = me.getItemByRecord(record);

        if (!item) return;
        me.nodeRegionOnExpand = Ext.fly(item).getRegion();
    },

    preserveScroll: function (node) {
        var me = this,
            region = Ext.fly(node).getRegion(),
            oldRegion = me.nodeRegionOnExpand,
            diffX, diffY;

        diffY = region.top - oldRegion.top;
        diffX = region.left - oldRegion.left;

        me.scrollBy(diffX, diffY);

    },

    onStoreCollapse: function (record) {
        var me = this,
            item = me.getItemByRecord(record),
            node,
            cls = me.collapseCls,
            ct;

        //verify if record is in the component
        if (!item) return;

        node = me.getNodeFromChildEl(item);

        if (!node) return;

        node.addCls(cls);
        if (me.nodeRegionOnExpand) {
            me.preserveScroll(item);
        }
        else me.focusNode(record);
        me.nodeRegionOnExpand = null;
    },

    onStoreIdChanged: function (store, rec, oldId, newId, oldInternalId) {
        var me = this,
            nodeDom;

        if (me.rendered) {
            nodeDom = me.getNode(oldId || oldInternalId);
            if (nodeDom) {
                nodeDom.setAttribute('data-recordId', me.getRecordId(rec));
                nodeDom.id = me.getNodeId(rec);
            }
        }
    },

    /*onStoreDataChanged: function(){
     console.log('changed');
     },*/

    onStoreBeforeLoad: function (store, operation, options) {
        var me = this,
            loadMask = me.loadMask,
            nodeItem, node;

        if (me.rendered && loadMask) {
            if (Ext.isObject(loadMask)) {
                loadMask.show();
            }
            else {
                nodeItem = me.getItemByRecord(operation.node);
                if (!nodeItem) return;
                node = me.getNodeFromChildEl(nodeItem);
                node = me.getNodeInlineExpanderContainer(node);
                Ext.fly(node).mask(me.loadingText);
            }
        }
    },

    onStoreLoad: function (store, fillRoot, newNodes) {
        var me = this,
            root = me.root,
            loadMask = me.loadMask,
            parent, node, nodeItem;

        // Always update the current node, since the load may be triggered
        // by .load() directly instead of .expand() on the node
        fillRoot.triggerUIUpdate();

        me.storeRootLoad = false;
        if (!me.needNodeJoin) {
            me.store.on('append', me.onStoreAppend, me);
            me.store.on('update', me.onStoreUpdate, me);
        }

        if (!me.rendered) return;

        if (loadMask) {
            if (Ext.isObject(loadMask)) {
                loadMask.hide();
            }
            else {
                nodeItem = me.getItemByRecord(fillRoot);
                if (nodeItem) {
                    node = me.getNodeFromChildEl(nodeItem);
                    node = me.getNodeInlineExpanderContainer(node);
                    Ext.fly(node).unmask();
                }
            }
        }

        if (fillRoot == root) {
            me.refresh();
            return;
        }

        if (root != store.getRootNode()) {
            //not all nodes of store belongs to the component
            //so we need figure out if the loaded nodes belongs to component's root
            parent = fillRoot.parentNode;
            while (parent) {
                if (parent == root) break;
                parent = parent.parentNode;
            }

            if (parent != root) return;
        }
        me.refreshNode(fillRoot);
    },


    getRecordId: function (record) {
        return (record.get('id') || record.internalId);
    },

    /**
     * Get node's id to use for node item HTML element
     * @param {Ext.data.NodeInterface} record The record that the element represents
     * @returns {String} The element id
     */
    getNodeId: function (record) {
        return this.id + '-record-' + this.getRecordId(record);
    },

    /**
     * Gets the record for the node item HTML element
     *
     * @param {HtmlElement} node The html element that represents the record
     * @returns {Ext.data.NodeInterface} The record if found
     */
    getRecord: function (node) {
        node = this.getNode(node);
        if (node) {
            var id = node.getAttribute('data-recordId');
            if (!id && Ext.isIE) {
                id = node.id.substr(this.id.length + 8);
            }
            return this.store.getNodeById(id);
        }
    },

    /**
     * Gets a template node item.
     *
     * @param {HTMLElement/String/Number/Ext.data.Model} nodeInfo An HTMLElement template node,
     * the id of a template node or the record associated with the node.
     *
     * @return {HTMLElement} The node or null if it wasn't found
     */
    getNode: function (nodeInfo) {
        if ((!nodeInfo && nodeInfo !== 0) || !this.rendered) {
            return null;
        }

        if (Ext.isString(nodeInfo)) {
            return document.getElementById(nodeInfo);
        }

        if (nodeInfo.isModel) {
            return this.getItemByRecord(nodeInfo);
        }

        return nodeInfo; // already an HTMLElement
    },

    /**
     * Get the nodes on this component
     *
     * @returns {Array} The nodes list
     *
     * @todo implement this method
     */
    getNodes: function () {
        return [];
    },

    /**
     * Gets a template node content or node container template from a record
     *
     * @param {Ext.data.NodeInterface} record The record to find the node template
     * @param {Boolean} container When true the node's container element is returned if found, if false the node's content element
     *
     * @returns {HtmlElement}
     */
    getItemByRecord: function (record, container) {
        var id = this.getNodeId(record);
        return this.retrieveNode(id, container);
    },

    /**
     * Gets a template node content or node container template from it's id
     *
     * @param {Ext.data.NodeInterface} record The record to find the node template
     * @param {Boolean} container When true the node's container element is returned if found, if false the node's content element
     *
     * @returns {HtmlElement}
     *
     * @protected
     */
    retrieveNode: function (id, container) {
        var me = this,
            result = me.el.getById(id, true),
            fly;

        if (container && result) {
            if (!(fly = Ext.fly(result)).is(me.itemSelector)) {
                return me.getNodeFromChildEl(fly);
            }
        }

        return result;
    },

    /**
     * Gets the node's container for the node content
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element/null} The content container if the node contains it
     */
    getItemContainer: function (node) {
        return Ext.fly(node).child(this.nodeItemContainerSelector);
    },

    /**
     * Gets the node content container that contains the item
     *
     * @param {HTMLElement|Ext.dom.Element} item The item contents element
     *
     * @returns {Ext.dom.Element/null} The content row if found
     */
    getItemRowFromItem: function (item) {
        return Ext.fly(item).parent(this.itemRowSelector);
    },

    /**
     * Get the node element from one of its children elements
     *
     * @param {HTMLElement|Ext.dom.Element} item The node's child element
     *
     * @returns {Ext.dom.Element/null} The node element if found
     */
    getNodeFromChildEl: function (item) {
        return Ext.fly(item).up(this.nodeSelector);
    },

    /**
     * Get the node's body element
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The body of the node element if found
     */
    getNodeBody: function (node) {
        return Ext.fly(node).child(this.nodeBodySelector);
    },

    /**
     * Get the node's children container
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The children container element
     */
    getChildrenContainer: function (node) {
        return Ext.fly(node).child(this.nodeContainerSelector);
    },

    /**
     * Get the container of the children connector lines
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The container for the children lines of the node
     */
    getChildrenLinesContainer: function (node) {
        return Ext.fly(node).child(this.childrenLinesSelector);
    },

    /**
     * Get the node's wrapper for the down line connector
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The node's down line connector wrapper
     */
    getNodeDownLine: function (node) {
        return Ext.fly(node).down(this.downLineSelector);
    },

    /**
     * Get the node container for the down line connector
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The node's down line connector container
     */
    getNodeDownLineContainer: function (node) {
        return Ext.fly(node).down(this.downLineContainerSelector);
    },

    /**
     * Get the node container for the inline expander
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The node's down line connector wrapper
     */
    getNodeInlineExpanderContainer: function (node) {
        return Ext.fly(node).child(this.inlineExpanderContainerSelector);
    },

    /**
     * Get the node wrapper for the inline expander
     *
     * @param {HTMLElement|Ext.dom.Element} node The node element
     *
     * @returns {Ext.dom.Element|null} The node's down line connector wrapper
     */
    getNodeInlineExpander: function (node) {
        return Ext.fly(node).child(this.inlineExpanderContentSelector);
    },


    /**
     * Removes a node from the component using the record data
     *
     * @param {Ext.data.NodeInterface} record The node record
     *
     * @return {HtmlElement} The node element removed
     */
    removeNodeFromRecord: function (record) {
        var me = this,
            item = me.getItemByRecord(record),
            node, parent, count, tmp;

        if (!item) return null;

        node = me.getNodeFromChildEl(item);
        parent = me.getNodeFromChildEl(node);
        me.getSelectionModel().deselect(record);
        //Ext.fly(item).removeCls('x-item-selected');
        Ext.fly(node).remove();

        //could be a root node child and root isn't shown in the component
        if (parent) {
            //adjust the parent node to reflect the removal
            tmp = me.getNodeDownLine(parent);
            count = tmp.getAttribute('colSpan') - 1;
            if (!count) {
                //become a leaf, so remove unnecessary elements (down line, inline expander, child nodes container, etc..)
                tmp.parent().remove();
                me.getNodeInlineExpanderContainer(parent).remove();
                me.getChildrenContainer(parent).remove();
            }
            else {
                //adjust the parent node
                tmp.set({colSpan: count});
                me.getNodeInlineExpander(parent).set({colSpan: count});
                tmp = me.getChildrenLinesContainer(parent).dom;
                //removes the child connector line
                if (count < 2) {
                    //remove the full container of children connector lines
                    Ext.fly(tmp).remove();
                }
                else {
                    //removes just the connector line of one child
                    Ext.fly(tmp.childNodes[1]).remove();
                }
            }
        }

        return node;
    },

    /**
     * Removes a node from the component
     *
     * @param {Ext.data.NodeInterface} record The node element
     *
     * @return {HtmlElement} The node element removed
     */
    removeNode: function (node) {
        var me = this,
            parent, count, tmp;

        if (!node) return null;

        parent = me.getNodeFromChildEl(node);
        Ext.fly(node).remove();

        //could be a root node child and root isn't shown in the component
        if (parent) {
            //adjust the parent node to reflect the removal
            tmp = me.getNodeDownLine(parent);
            count = tmp.getAttribute('colSpan') - 1;
            if (!count) {
                //become a leaf, so remove unnecessary elements (down line, inline expander, child nodes container, etc..)
                tmp.parent().remove();
                me.getNodeInlineExpanderContainer(parent).remove();
                me.getChildrenContainer(parent).remove();
            }
            else {
                //adjust the parent node
                tmp.set({colSpan: count});
                me.getNodeInlineExpander(parent).set({colSpan: count});
                tmp = me.getChildrenLinesContainer(parent).dom;
                //removes the child connector line
                if (count < 2) {
                    //remove the full container of children connector lines
                    Ext.fly(tmp).remove();
                }
                else {
                    //removes just the connector line of one child
                    Ext.fly(tmp.childNodes[1]).remove();
                }
            }
        }

        return node;
    },

    /**
     * Rebuild the full node structure
     *
     * @param record The record to have it's structure rebuilded
     */
    refreshNode: function (record) {
        var me = this,
            nodeItem = me.getItemByRecord(record),
            node;

        //the node was not found?
        if (!nodeItem) {
            return;
        }

        node = me.getNodeFromChildEl(nodeItem);
        if (!node) {
            return;
        }

        me.innerNodeTpl.overwrite(node, {view: me, node: record, nodeId: me.getNodeId(record)});
    },

    insertNodeFromRecord: function (record, refRecord) {
        var me = this,
            parentRecord = record.parentNode,
            node, nodeItem,
            inWrapper = false,
            childCount = parentRecord.childNodes.length,
            renderBuffer = me.renderBuffer,
            out, container, body, len, tmp, parentItem,
            parentNode, values, downLine, reference;

        if (!me.rendered) {
            return;
        }

        nodeItem = me.getItemByRecord(record);
        if (nodeItem) {
            node = me.getNodeFromChildEl(nodeItem);
        }

        parentItem = me.getItemByRecord(parentRecord);

        //insert at root but root could not be visible, so add to component wrapper
        if (!parentItem) {
            parentNode = me.el;
            inWrapper = true;
        }
        else {
            parentNode = me.getNodeFromChildEl(parentItem);
        }

        //if the node doesn't exist yet, create it
        if (!node) {
            //'<td class="{view.nodeCls}{[!values.node.isLeaf() && !values.node.isExpanded() ? " " + values.view.collapseCls: ""]}" style="padding: 0 {view.nodeSpacing}px;">',
            node = Ext.DomHelper.createDom({
                tag: 'td',
                cls: me.nodeCls + (!record.isLeaf() && !record.isExpanded() ? " " + me.collapseCls : "" ),
                style: "padding: 0 " + me.nodeSpacing + "px",
                html: me.innerNodeTpl.apply({view: me, node: record, nodeId: me.getNodeId(record)})
            });
        }
        else {
            tmp = Ext.DomHelper.createDom({
                tag: 'td',
                cls: me.nodeCls + (!record.isLeaf() && !record.isExpanded() ? " " + me.collapseCls : "" ),
                style: "padding: 0 " + me.nodeSpacing + "px"
            });

            //Ext.fly(nodeItem).removeCls('x-item-selected');
            Ext.fly(tmp).appendChild(node.first());
            me.removeNode(node);
            node = tmp;
        }

        values = {view: me, node: parentRecord, nodeId: me.getNodeId(parentRecord)};


        //it´s an append?
        if (!refRecord) {
            if (inWrapper) {
                //just add to the wrapper container
                container = me.getChildrenContainer(parentNode);
                container.appendChild(node);
            }
            else {
                out = [];

                downLine = me.getNodeDownLine(parentNode);

                //doesn't have a down line? so it was a leaf before
                if (!downLine) {
                    //create full container node parts

                    out.push('<table>');
                    out.push(me.downLineTpl.apply(values));
                    out.push(me.childrenLinesTpl.apply(values));
                    out.push(me.containerTpl.apply(values));
                    out.push('</table>');
                    Ext.fly(renderBuffer).setHTML(out.join(''));

                    if (!parentRecord.isExpanded()) {
                        parentNode.addCls(me.collapseCls);
                    }

                    container = me.getChildrenContainer(renderBuffer);
                    body = me.getNodeBody(parentNode);
                    tmp = Ext.fly(renderBuffer).query('tr');
                    len = tmp.length;
                    for (var i = 0; i < len; ++i) {
                        body.appendChild(tmp[i]);
                    }

                    Ext.fly(container).appendChild(node)

                    if (!parentRecord.isExpanded()) {
                        parentRecord.expand(false, function () {
                            me.focusNode(me.getItemByRecord(record));
                        }, me)
                    }
                    else me.focusNode(me.getItemByRecord(record));

                    return;
                }
                else {
                    //adjust the down line and inline expander
                    Ext.fly(parentItem).parent().set({colSpan: childCount});
                    downLine.set({colSpan: childCount});
                    me.getNodeInlineExpander(parentNode).set({colSpan: childCount});
                }

                container = me.getChildrenContainer(parentNode);

                //adjust the children lines
                if (childCount > 1) {
                    tmp = me.getChildrenLinesContainer(parentNode);
                    if (!tmp) {
                        //was a single child, so children lines doesn't exist yet
                        out = ['<table>']
                        out.push(me.childrenLinesTpl.apply(values));
                        out.push('</table>')
                        Ext.fly(me.renderBuffer).setHTML(out.join(''));
                        Ext.fly(me.renderBuffer).down('tr').insertBefore(container);
                    }
                    else {
                        //add the new child line
                        var line = Ext.DomHelper.createDom({
                            tag: 'td',
                            html: me.childrenLineTpl.apply(values)
                        });
                        Ext.fly(line).insertAfter(tmp.first());
                    }
                }

                //add the new node to children container
                container.appendChild(node);
            }
        }
        else {
            if (!inWrapper) {
                Ext.fly(parentItem).parent().set({colSpan: childCount});
                me.getNodeDownLine(parentNode).set({colSpan: childCount});
                me.getNodeInlineExpander(parentNode).set({colSpan: childCount});
            }

            container = me.getChildrenContainer(parentNode);

            //adjust the children lines
            if (!inWrapper && childCount > 1) {
                tmp = me.getChildrenLinesContainer(parentNode);
                if (!tmp) {
                    //was a single child, so children lines doesn't exist yet
                    out = ['<table>']
                    out.push(me.childrenLinesTpl.apply(values));
                    out.push('</table>')
                    Ext.fly(me.renderBuffer).setHTML(out.join(''));
                    Ext.fly(me.renderBuffer).down('tr').insertBefore(container);
                }
                else {
                    //add the new child line
                    var line = Ext.DomHelper.createDom({
                        tag: 'td',
                        html: me.childrenLineTpl.apply(values)
                    });
                    Ext.fly(line).insertAfter(tmp.first());
                }
            }

            reference = me.getItemByRecord(refRecord);
            if (!reference) {
                //reference not found so append to container
                container.appendChild(node);
            }
            else {
                reference = me.getNodeFromChildEl(reference);
                Ext.fly(node).insertBefore(reference);
            }

        }

        if (!parentRecord.isExpanded()) {
            parentRecord.expand(false, function () {
                me.focusNode(me.getItemByRecord(record));
            }, me)
        }
        else me.focusNode(me.getItemByRecord(record));

    },

    onStoreAppend: function (parentRecord, record, index, opt) {
        var me = this;

        if (me.needNodeJoin) record.join(me.store);

        if (me.initialLoad && !me.storeRootLoad) {
            //looks like a bug in 4.2.1 and 4.2.2 maybe before too
            if (parentRecord.isLeaf()) {
                parentRecord.set('leaf', false);
            }

            me.insertNodeFromRecord(record);
        }
    },

    onStoreInsert: function (parent, record, refNode, opt) {
        if (this.needNodeJoin) record.join(this.store);
        this.insertNodeFromRecord(record, refNode);
    },

    onStoreRemove: function (parentRecord, record, isMove, opt) {
        var me = this;

        //looks like a bug in 4.2.1 and 4.2.2 maybe before too
        if (!parentRecord.isLeaf() && !parentRecord.childNodes.length) {
            parentRecord.set('leaf', true);
        }

        if (isMove) return;

        if (me.needNodeJoin) {
            record.unjoin(me.store);
        }

        me.removeNodeFromRecord(record);
    },

    onStoreBulkRemove: function (store, records, isMove, opt) {
        var me = this,
            needJoin = me.needNodeJoin,
            len = records.length,
            record, i;

        if (isMove) return;
        me.getSelectionModel().deselect(records);
        for (i = 0; i < len; ++i) {
            record = records[i];
            if (needJoin) {
                record.unjoin(store);
            }

            me.removeNodeFromRecord(record);
        }
    },

    onStoreUpdate: function (store, record, op, modifiedFields, op) {
        var me = this,
            node;
        if (!me.rendered) {
            return;
        }

        node = me.getItemByRecord(record);
        if (!node) return;

        Ext.fly(node).setHTML(me.renderItem(me, record));

        me.fireEvent('itemupdate', me, record, node);
    },

    onDataRefresh: function () {
        this.refresh();
    },

    onStoreClear: function () {
        if (this.rendered) {
            this.renderTpl.overwrite(this.el, {});
        }
    },

    renderNodes: function (records, out) {
        var me = this,
            len = records.length,
            i;

        for (i = 0; i < len; ++i) {
            me.renderNode(records[i], out);
        }
        var elems = document.querySelectorAll('.task-item');
        //console.log(elems);
        //if(elems != [])
        //alert(elems[0].outerHTML);
        //me.onInlineExpanderClick(null, '<span id="ochart-1063-record-root" class="x-ochart-node-content task-item" data-recordid="root">');
    },

    renderNode: function (record, out) {
        var me = this,
            tpl = me.nodeTpl,
            value = {view: me, node: record, nodeId: me.getNodeId(record)};
        if (out) {
            tpl.applyOut(value, out);
        }
        else return tpl.apply(value);
    },

    renderItem: function (view, record) {
        return view.itemTpl.apply(record.data);
    },

    /**
     * Gets the selection model for this component.
     * @return {Ext.selection.Model} The selection model
     */
    getSelectionModel: function () {
        var me = this,
            mode = 'SINGLE';

        if (me.simpleSelect) {
            mode = 'SIMPLE';
        }
        /*else if (me.multiSelect) {
         mode = 'MULTI';
         }*/

        // No selModel specified, or it's just a config; Instantiate
        if (!me.selModel || !me.selModel.events) {
            me.selModel = Ext.create('sitools.extension.model.OChartModel', (Ext.apply({
                allowDeselect: me.allowDeselect,
                mode: mode,
                enableKeyNav: true,
                deselectOnContainerClick: true
            }, me.selModel)));
        }

        if (!me.selModel.hasRelaySetup) {
            me.relayEvents(me.selModel, [
                'selectionchange', 'beforeselect', 'beforedeselect', 'select', 'deselect', 'focuschange'
            ]);
            me.selModel.hasRelaySetup = true;
        }

        // lock the selection model if user
        // has disabled selection
        if (me.disableSelection) {
            me.selModel.locked = true;
        }

        return me.selModel;
    },

    /**
     * Returns true if the passed node is selected, else false.
     * @param {HTMLElement/Number/Ext.data.Model} node The node, node index or record to check
     * @return {Boolean} True if selected, else false
     * @since 2.3.0
     */
    isSelected: function (node) {
        var r = this.getRecord(node);
        return this.selModel.isSelected(r);
    },

    /**
     * Handles buffered mouse over and out event
     * @param {HtmlEvent} e The browser event
     *
     * @protected
     */
    onMouseOverOut: function (e) {
        var me = this;

        // Determining if we are entering or leaving view items is deferred until
        // mouse move churn settles down.
        me.lastMouseOverOutEvent.setEvent(e.browserEvent, true);
        me.handleMouseOverOrOut(me.lastMouseOverOutEvent);
    },

    /**
     * Handles the the mouse over and out events
     *
     * @param {HtmlEvent} e The browser event
     * @protected
     */
    handleMouseOverOrOut: function (e) {
        var me = this,
            isMouseout = e.type === 'mouseout',
            method = isMouseout ? e.getRelatedTarget : e.getTarget,
            nowOverItem = method.call(e, me.itemSelector);

        // If the mouse event of whatever type tells use that we are no longer over the current mouseOverItem...
        if (!me.mouseOverItem || nowOverItem !== me.mouseOverItem) {

            // First fire mouseleave for the item we just left
            if (me.mouseOverItem) {
                e.item = me.mouseOverItem;
                e.newType = 'mouseleave';
                me.handleEvent(e);
            }

            // If we are over an item, fire the mouseenter
            me.mouseOverItem = nowOverItem;
            if (me.mouseOverItem) {
                e.item = me.mouseOverItem;
                e.newType = 'mouseenter';
                me.handleEvent(e);
            }
            else {
                e.item = null;
                me.handleEvent(e);
            }
        }
        else  me.handleEvent(e);
    },

    /**
     * Handle browser events and internal events
     *
     * @param e The event
     *
     * @protected
     */
    handleEvent: function (e) {
        var me = this,
            key = e.type == 'keydown' && e.getKey();

        me.processUIEvent(e);

        // After all listeners have processed the event, then unless the user is typing into an input field,
        // prevent browser's default action on SPACE which is to focus the event's target element.
        // Focusing causes the browser to attempt to scroll the element into view.
        if (key === e.SPACE) {
            if (!me.inputTagRe.test(e.getTarget().tagName)) {
                e.stopEvent();
            }
        }
    },

    /**
     * Try to process an UI event
     *
     * @param e
     *
     * @returns {Boolean} true if the event was processed
     *
     * @protected
     */
    processUIEvent: function (e) {
        // If the target event has been removed from the body (data update causing view DOM to be updated),
        // do not process. isAncestor uses native methods to check.
        if (!Ext.getBody().isAncestor(e.target)) {
            return;
        }

        var me = this,
            item = e.getTarget(me.itemSelector),
            map = this.statics().EventMap,
            index, record,
            type = e.type,
            newType = e.type,
            sm;


        // If the event is a mouseover/mouseout event converted to a mouseenter/mouseleave,
        // use that event type and ensure that the item is correct.
        if (e.newType) {
            newType = e.newType;
            item = e.item;
        }

        // For keydown events, try to get either the last focused item or the selected item.
        // If we have not focused an item, we'll just fire a container keydown event.
        if (!item && type == 'keydown') {
            sm = me.getSelectionModel();
            record = sm.lastFocused || sm.getLastSelected();
            if (record) {
                item = me.getNode(record, true);
            }
        }

        //node item event?
        if (item) {
            //get the record for the template element
            if (!record) {
                record = me.getRecord(item);
            }

            // It is possible for an event to arrive for which there is no record... this
            // can happen with dblclick where the clicks are on removal actions (think a
            // grid w/"delete row" action column)
            if (!record) {
                return false;
            }

            index = record.get('index');

            //call event handlers
            if ((me['onBeforeItem' + map[newType]](record, item, index, e) === false) ||
                (me.fireEvent('beforeitem' + newType, me, record, item, index, e) === false) ||
                (me['onItem' + map[newType]](record, item, index, e) === false)
            ) {
                return false;
            }

            me.fireEvent('item' + newType, me, record, item, index, e);
        }
        else {
            item = e.getTarget(me.expanderSelector, 1);
            if (item) return false;
            //container event handler
            if ((me.processContainerEvent(e) === false) ||
                (me['onBeforeContainer' + map[type]](e) === false) ||
                (me.fireEvent('beforecontainer' + type, me, e) === false) ||
                (me['onContainer' + map[type]](e) === false)
            ) {
                return false;
            }

            me.fireEvent('container' + type, me, e);
        }

        return true;
    },

    // @private
    setHighlightedItem: function (item) {
        var me = this,
            highlighted = me.highlightedItem,
            overItemCls = me.overItemCls,
            beforeOverItemCls = me.beforeOverItemCls,
            previous;

        if (highlighted != item) {
            if (highlighted) {
                Ext.fly(highlighted).removeCls(overItemCls);
                previous = highlighted.previousSibling;
                if (beforeOverItemCls && previous) {
                    Ext.fly(previous).removeCls(beforeOverItemCls);
                }
                me.fireEvent('unhighlightitem', me, highlighted);
            }

            me.highlightedItem = item;

            if (item) {
                Ext.fly(item).addCls(me.overItemCls);
                previous = item.previousSibling;
                if (beforeOverItemCls && previous) {
                    Ext.fly(previous).addCls(beforeOverItemCls);
                }
                me.fireEvent('highlightitem', me, item);
            }
        }
    },

    /**
     * Highlights a given item in the View. This is called by the mouseover handler if {@link #overItemCls}
     * and {@link #trackOver} are configured, but can also be called manually by other code, for instance to
     * handle stepping through the list via keyboard navigation.
     * @param {HTMLElement} item The item to highlight
     */
    highlightItem: function (item) {
        this.setHighlightedItem(item);
    },

    /**
     * Un-highlights the currently highlighted item, if any.
     */
    clearHighlight: function () {
        this.setHighlightedItem(undefined);
    },

    // protected
    onItemMouseEnter: function (record, item, index, e) {
        var me = this,
            h;

        if (me.panning) return;

        if (this.trackOver) {
            this.highlightItem(item);
        }

        if (me.toolsVisible && !me.dragging) {
            if (!record.isLeaf() && record.isExpanded() && !record.isRoot()) {
                me.expandTool.show();
                me.expandTool.alignTo(item, 'bl-tl');
            }

            if (!this.readOnly) {
                if (!record.isRoot()) {
                    me.addBeforeTool.show();
                    me.addBeforeTool.alignTo(item, 'r-l');
                    me.addAfterTool.show();
                    me.addAfterTool.alignTo(item, 'l-r');
                    me.removeItemTool.show();
                    me.removeItemTool.alignTo(item, 'br-tr');
                }
                me.addChildTool.show();
                me.addChildTool.alignTo(item, 't-b');
            }
        }

        me.lastOverItem = item;
    },

    // protected
    onItemMouseLeave: function (record, item, index, e) {
        if (this.trackOver) {
            this.clearHighlight();
        }
    },

    // protected, template methods
    processContainerEvent: Ext.emptyFn,
    processItemEvent: Ext.emptyFn,

    onBeforeItemMouseOut: Ext.emptyFn,
    onBeforeItemMouseOver: Ext.emptyFn,
    onItemMouseOut: Ext.emptyFn,
    onItemMouseOver: Ext.emptyFn,

    onItemMouseDown: Ext.emptyFn,
    onItemMouseUp: Ext.emptyFn,
    onItemFocus: Ext.emptyFn,
    onItemClick: function (record) {
        if (Ext.versions.extjs.isLessThan('4.2.0')) {
            this.focusNode(record);
        }
    },
    onItemDblClick: Ext.emptyFn,
    onItemContextMenu: Ext.emptyFn,
    onItemKeyDown: Ext.emptyFn,
    onBeforeItemMouseDown: Ext.emptyFn,
    onBeforeItemMouseUp: Ext.emptyFn,
    onBeforeItemFocus: Ext.emptyFn,
    onBeforeItemMouseEnter: Ext.emptyFn,
    onBeforeItemMouseLeave: Ext.emptyFn,
    onBeforeItemClick: Ext.emptyFn,
    onBeforeItemDblClick: Ext.emptyFn,
    onBeforeItemContextMenu: Ext.emptyFn,
    onBeforeItemKeyDown: Ext.emptyFn,

    onExpanderMouseOver: Ext.emptyFn,
    onExpanderMouseOut: Ext.emptyFn,

    onInlineExpanderClick: function (e, t) {
        //   console.log(e);
        //console.log(t);
        var me = this,
            node = me.getNodeFromChildEl(t),
            record,
            item,
            nodeRegion;

        if (!node) return;

        item = node.down(me.itemSelector);
        record = me.getRecord(item);
        if (!record) return;
        //console.log(record);
        if (record.isExpanded()) me.collapse(record);
        else me.expand(record);
    },

    onItemExpandClick: function () {
        var me = this;
        me.onInlineExpanderClick(null, me.lastOverItem);
        me.hideTools();
    },

    hideTools: function () {
        var me = this;
        if (me.toolsVisible && me.expandTool) {
            me.expandTool.hide();
            me.addBeforeTool.hide();
            me.addAfterTool.hide();
            me.addChildTool.hide();
            me.removeItemTool.hide();
            me.lastOverItem = null;
        }
    },

    fireAddItem: function (item, where) {
        var me = this,
            record;
        if (!item) return;

        record = me.getRecord(item)

        me.fireEvent('additem', me, record, where, item);
        me.hideTools();
    },

    onItemAddBeforeClick: function () {
        var item = this.lastOverItem;
        if (!item) return;
        this.fireAddItem(item, 'before');
    },

    onItemAddAfterClick: function () {
        var item = this.lastOverItem;
        if (!item) return;
        this.fireAddItem(item, 'after');
    },

    onItemAddChildClick: function () {
        var item = this.lastOverItem;
        if (!item) return;
        this.fireAddItem(item, 'child');
    },

    onItemRemoveClick: function () {
        var me = this,
            item = this.lastOverItem,
            record;

        if (!item) return;

        record = me.getRecord(item)

        me.fireEvent('removeitem', me, record, item);
        me.hideTools();
    },

    onPanning: function (e) {
        var me = this;
        e.stopEvent();

        var x = e.getPageX(),
            y = e.getPageY(),
            xDelta = x - me.mouseX,
            yDelta = y - me.mouseY;

        me.scrollBy(-xDelta, -yDelta);
        me.mouseX = x;
        me.mouseY = y;
    },

    onFinishPanning: function (e) {
        var me = this;
        Ext.getBody().un('mousemove', me.onPanning, me);
        Ext.getDoc().un('mouseup', me.onFinishPanning, me);

        if (Ext.isIE || Ext.isGecko) {
            Ext.getBody().un('mouseenter', me.onFinishPanning, me);
        }

        me.el.setStyle('cursor', 'default');

        me.panning = false;
    },

    onContainerMouseDown: function (e) {
        var me = this;
        me.mouseX = e.getPageX();
        me.mouseY = e.getPageY();
        Ext.getBody().on('mousemove', me.onPanning, me);
        Ext.getDoc().on('mouseup', me.onFinishPanning, me);

        // For IE (and FF if using frames), if you move mouse onto the browser chrome and release mouse button
        // we won't know about it. Next time mouse enters the body, cancel any ongoing pan activity as a fallback.
        if (Ext.isIE || Ext.isGecko) {
            Ext.getBody().on('mouseenter', me.onFinishPanning, me);
        }

        me.el.setStyle('cursor', 'move');

        me.panning = true;

        // required for some weird chrome bug/behavior, when whole panel was scrolled-out
        e.stopEvent();
    },

    onContainerMouseUp: Ext.emptyFn,

    onContainerMouseOver: function () {
        this.hideTools();
    },
    onContainerMouseOut: Ext.emptyFn,
    onContainerClick: function () {
        this.el.focus();
    },
    onContainerDblClick: Ext.emptyFn,
    onContainerContextMenu: Ext.emptyFn,
    onContainerKeyDown: Ext.emptyFn,
    onBeforeContainerMouseDown: Ext.emptyFn,
    onBeforeContainerMouseUp: Ext.emptyFn,
    onBeforeContainerMouseOver: Ext.emptyFn,
    onBeforeContainerMouseOut: Ext.emptyFn,
    onBeforeContainerClick: Ext.emptyFn,
    onBeforeContainerDblClick: Ext.emptyFn,
    onBeforeContainerContextMenu: Ext.emptyFn,
    onBeforeContainerKeyDown: Ext.emptyFn,

    // invoked by the selection model to maintain visual UI cues
    onItemSelect: function (record) {
        var node = this.getNode(record);

        if (node) {
            Ext.fly(node).addCls(this.selectedItemCls);
        }
    },

    // invoked by the selection model to maintain visual UI cues
    onItemDeselect: function (record) {
        var node = this.getNode(record);

        if (node) {
            Ext.fly(node).removeCls(this.selectedItemCls);
        }
    },

    /**
     * Gets the item CSS selector (node content)
     *
     * This is required by the selection model
     * @returns {String} The CSS selector
     */
    getItemSelector: function () {
        return this.itemSelector;
    },

    /**
     * Focuses a node in the view.
     * @param {Ext.data.Model} rec The record associated to the node that is to be focused.
     */
    focusNode: function (rec) {
        var me = this,
        //todo verificar se deve trazer o container do nó
        //node        = me.getNode(rec, true),
            node = me.getNode(rec),
            el = me.el,
            adjustmentY = 0,
            adjustmentX = 0,
            elRegion = el.getRegion(),
            nodeRegion;

        // Viewable region must not include scrollbars, so use
        // DOM client dimensions
        elRegion.bottom = elRegion.top + el.dom.clientHeight;
        elRegion.right = elRegion.left + el.dom.clientWidth;
        if (node) {
            nodeRegion = Ext.fly(node).getRegion();
            // node is above
            if (nodeRegion.top < elRegion.top) {
                adjustmentY = nodeRegion.top - elRegion.top;
                // node is below
            } else if (nodeRegion.bottom > elRegion.bottom) {
                adjustmentY = nodeRegion.bottom - elRegion.bottom;
            }

            // node is left
            if (nodeRegion.left < elRegion.left) {
                adjustmentX = nodeRegion.left - elRegion.left;
                // node is right
            } else if (nodeRegion.right > elRegion.right) {
                adjustmentX = nodeRegion.right - elRegion.right;
            }

            if (adjustmentX || adjustmentY) {
                me.scrollBy(adjustmentX, adjustmentY, false);
            }
            el.focus();
        }
    },

    startDrag: function () {
        this.dragging = true;
    },

    endDrag: function () {
        this.dragging = false;
    }
});

