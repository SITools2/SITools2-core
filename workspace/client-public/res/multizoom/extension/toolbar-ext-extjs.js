/*global Ext, viewer, document, retInt, window, fireEvent, getMouseXY, Event, loadUrl, document, i18n*/
//viewer.toolbarImages = loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar";
viewer.toolbarextjs = function (viewer) {
    
    var self = this;
    
    viewer.toolbarHeight = 50;
    viewer.withToolbar = true;
    
    var id  = Ext.id();
    
    var divToolbar = document.createElement("div");
    divToolbar.style.width = "100%";
    divToolbar.style.height = viewer.toolbarHeight + "px";
    divToolbar.id = id;
    

    var map = document.createElement('map');
    map.name = "controls";

    var up = document.createElement('area');
    up.shape = 'rect';
    up.coords = "17,1,31,17";
    up.style.cursor = 'pointer !important';
    up.onmousedown = function () {
        var dimension = viewer.getFrameDimension();
        viewer.moveBy(0, dimension[1] * 0.1); // 10%
    };
    map.appendChild(up);

    var down = document.createElement('area');
    down.shape = 'rect';
    down.coords = "17,31,31,47";
    down.style.cursor = 'pointer !important';
    down.onmousedown = function () {
        var dimension = viewer.getFrameDimension();
        viewer.moveBy(0, -1 * dimension[1] * 0.1);
    };
    map.appendChild(down);

    var left = document.createElement('area');
    left.shape = 'rect';
    left.coords = "1,17,17,31";
    left.style.cursor = 'pointer !important';
    left.onmousedown = function () {
        var dimension = viewer.getFrameDimension();
        viewer.moveBy(dimension[0] * 0.1, 0);
    };
    map.appendChild(left);

    var right = document.createElement('area');
    right.shape = 'rect';
    right.coords = "31,17,47,31";
    right.style.cursor = 'pointer !important';
    right.onmousedown = function () {
        var dimension = viewer.getFrameDimension();
        viewer.moveBy(-1 * dimension[0] * 0.1, 0);
    };
    map.appendChild(right);
    
    divToolbar.appendChild(map);
    viewer.frameElement.parentNode.appendChild(divToolbar);
    
    
    var tb = new Ext.Toolbar({
        renderTo: id,
        height: 50,
        items: [
            {
                // xtype: 'button', // default for Toolbars, same as 'tbbutton'
                text: '100%',
                xtype : 'label',
                cls : 'pourcentage'
            },
            '->',
            {
                xtype: 'button', // same as 'tbsplitbutton'
                iconCls : 'arrow-back',
                handler : function () {
                    alert("TODO");
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                iconCls : 'arrow-next',
                handler : function () {
                    alert("TODO");
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/download.png',
                tooltip : i18n.get('label.downloadImage'),
                text : 'download',
                handler : function () {
                    var img = viewer.frameElement.firstChild;
                    sitools.user.component.dataviews.dataviewUtils.downloadFile(img.src);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/copy.png',
                tooltip : i18n.get('label.copyImage'),
                text : 'copy',
                handler : function () {
                    alert("TODO");
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/zoom_in.png',
                tooltip : i18n.get('label.zoomin'),
                handler : function () {
                    var frameDimension = viewer.getFrameDimension();
                    viewer.zoomTo(viewer.getZoomLevel() + 1, frameDimension[0] / 2, frameDimension[1] / 2);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/zoom_out.png',
                tooltip : i18n.get('label.zoomout'),
                handler : function () {
                    var frameDimension = viewer.getFrameDimension();
                    viewer.zoomTo(viewer.getZoomLevel() - 1, frameDimension[0] / 2, frameDimension[1] / 2);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/stretch_optimally.png',
                tooltip : i18n.get('label.reset'),
                handler : function () {
                    viewer.reset();
                }
            }, {
                xtype : 'component',
                autoEl : {
                    tag : 'img',
                    src : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/movement-controls.png',
                    useMap : "#controls"
                }
            }
            
        ]
    });
    

    var callbackZoomTo = function (viewer) {
        var labelPourcentage = tb.items.get(0);
        var zoomLevel = viewer.getZoomLevel();
        var zoomFactor = viewer.getZoomFactor() - 1;
        var pourcentage = 100;
        for ( var i = zoomLevel; i > 0; i--) {
            pourcentage -= pourcentage * zoomFactor;
        }
        pourcentage = Math.round(pourcentage * 100) / 100;
        labelPourcentage.setText(pourcentage + "%");

    };
    
    Event.observe(viewer.frameElement, 'zoomto', callbackZoomTo.bind(self, viewer));
    
    
	
}