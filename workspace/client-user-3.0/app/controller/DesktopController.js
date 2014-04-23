Ext.define('clientuser.controller.DesktopController', {
   
    extend: 'Ext.app.Controller',
    
    views : ["windows.Window"],
    
    init : function () {
        
        this.control({
            
            "menuitem[text='start']" : {
                click : function () {
                    var window = Ext.create("clientuser.view.windows.Window");
                    window.show();
                }
            },
            "window" : {
                afterrender : function (panel) {
                    if (!Ext.isEmpty(panel.grid)) {
                        panel.grid.getStore().load();
                    }
                }
            }
        });
    }
});