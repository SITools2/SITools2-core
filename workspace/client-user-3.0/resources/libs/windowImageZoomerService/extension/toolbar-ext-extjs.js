/*global Ext, viewer, document, retInt, window, fireEvent, getMouseXY, Event, loadUrl, document, i18n*/
//viewer.toolbarImages = loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar";
viewer.toolbarextjs = function (viewer) {
    
    var self = this;
    
    viewer.toolbarHeight = 63;
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
    
    var labelPourcentage = Ext.create("Ext.form.Label", {
        // xtype: 'button', // default for Toolbars, same as 'tbbutton'
        text: '100%',
        xtype : 'label',
        name : 'labelZoomName',
        cls : 'zoomFactorLabel',
        autoWidth : true
    });
    
    var tb = Ext.create("Ext.Toolbar", {
        renderTo: id,
        height: 60,
        padding : 5,
        enableOverflow : true,
        cls : 'preview-toolbar',
        items: [
            /*{
                xtype: 'button', // same as 'tbsplitbutton'
                //iconCls : 'arrow-back',
                icon : loadUrl.get('APP_URL') + '/common/res/multizoom/images/toolbar/backward.png',
                iconCls : 'previewBarBtn-icon',
                tooltip : i18n.get('label.previous'),
                handler : function () {
                    alert("TODO");
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
            //    iconCls : 'arrow-next',
                icon : loadUrl.get('APP_URL') + '/common/res/multizoom/images/toolbar/forward.png',
                iconCls : 'previewBarBtn-icon',
                tooltip : i18n.get('label.next'),
                handler : function () {
                    alert("TODO");
                }
            },*/
            '->',
            labelPourcentage
            ,
            {
                xtype: 'image', // same as 'tbsplitbutton'
                src : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/resources/libs/windowImageZoomerService/images/toolbar/zoom-in.png",
                tooltip : i18n.get('label.zoomIn'),
                width : 32,
                cls : 'previewBarBtn-icon',
//                handler : function () {
//                    var frameDimension = viewer.getFrameDimension();
//                    viewer.zoomTo(viewer.getZoomLevel() + 1, frameDimension[0] / 2, frameDimension[1] / 2);
//                }
                listeners : {
                    el : {
                        click : function () {
                            var frameDimension = viewer.getFrameDimension();
                            viewer.zoomTo(viewer.getZoomLevel() + 1, frameDimension[0] / 2, frameDimension[1] / 2);
                        }
                    }
                }
            },
            {
                xtype: 'image', // same as 'tbsplitbutton'
                src : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL')+"/resources/libs/windowImageZoomerService/images/toolbar/zoom-out.png",
                tooltip : i18n.get('label.zoomOut'),
                cls : 'previewBarBtn-icon',
                width : 32,
                listeners : {
                    el : {
                        click : function () {
                            var frameDimension = viewer.getFrameDimension();
                            viewer.zoomTo(viewer.getZoomLevel() - 1, frameDimension[0] / 2, frameDimension[1] / 2);
                        }
                    }
                }
            },
            {
                xtype: 'image', // same as 'tbsplitbutton'
                src : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + '/resources/libs/windowImageZoomerService/images/toolbar/download-image.png',
                tooltip : i18n.get('label.downloadImage'),
                cls : 'previewBarBtn-icon',
                width : 32,
                //text : 'download',
                listeners : {
                    el : {
                        click : function () {
                            var img = viewer.frameElement.firstChild;
                            sitools.user.component.dataviews.dataviewUtils.downloadFile(img.src);
                        }
                    }
                }
            },
            /*{
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + '/common/res/multizoom/images/toolbar/copy-image.png',
                tooltip : i18n.get('label.copyImage'),
                iconCls : 'previewBarBtn-icon',
                //text : 'copy',
                handler : function () {
                    var img = viewer.frameElement.firstChild;
                    sitools.user.component.dataviews.dataviewUtils.copyImageToClipboard(img.src);
                }
            },*/
            {
                xtype: 'image', // same as 'tbsplitbutton'
                src : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/resources/libs/windowImageZoomerService/images/toolbar" + '/stretch-optimally.png',
                tooltip : i18n.get('label.resetZoom'),
                cls : 'previewBarBtn-icon',
                width : 32,
                listeners : {
                    el : {
                        click : function () {
                            viewer.reset();
                        }
                    }
                }
            }, {
                xtype : 'image',
                height : 48,
                width : 48,
                autoEl : {
                    tag : 'img',
                    src : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/resources/libs/windowImageZoomerService/images/toolbar" + '/movement-controls.png',
                    useMap : "#controls"
                }
            }
        ]
    });
    

    var callbackZoomTo = function (viewer) {
        
        var dimensionImage = viewer.getDimension();
        var originalDimensionImage = viewer.getOriginalDimension();
        
        var pourcentage = dimensionImage[0] / originalDimensionImage[0];
        
        pourcentage = Math.round(pourcentage * 100);
        labelPourcentage.setText(pourcentage + "%");

    };
    
    Event.observe(viewer.frameElement, 'zoomto', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'init', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'resize', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'reset', callbackZoomTo.bind(self, viewer));
    
    
	
}