Ext.define('Ext.ux.form.ToolFieldSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.toolfieldset',
    tools: [],

    createLegendCt: function () {
        var me = this,
            items = [],
            legend = {
                xtype: 'container',
                baseCls: me.baseCls + '-header',
                id: me.id + '-legend',
                autoEl: 'legend',
                items: items,
                ownerCt: me,
                ownerLayout: me.componentLayout
            };

        // Checkbox
        if (me.checkboxToggle) {
            items.push(me.createCheckboxCmp());
        } else if (me.collapsible) {
            // Toggle button
            items.push(me.createToggleCmp());
        }
        // Add Extra Tools
        if (Ext.isArray(me.tools)) {
            for(var i = 0; i < me.tools.length; i++) {
                items.push(me.createToolCmp(me.tools[i]));
            }
        }
        // Title
        items.push(me.createTitleCmp());

        return legend;
    },

    createToolCmp: function(toolCfg) {
        var me = this;
        Ext.apply(toolCfg, {
            xtype:  'tool',
            width:  15,
            height: 15,
            id:     me.id + '-tool-' + toolCfg.type,
            scope:  me
        });
        return Ext.widget(toolCfg);
    }
});