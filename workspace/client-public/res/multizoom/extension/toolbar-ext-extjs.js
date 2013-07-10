/*global Ext, viewer, document, retInt, window, fireEvent, getMouseXY, Event, loadUrl, document, i18n*/
//viewer.toolbarImages = loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar";
viewer.toolbarextjs = function (viewer) {
    
    var self = this;
    
    viewer.toolbarHeight = 53;
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
    
    var labelPourcentage = new Ext.form.Label({
        // xtype: 'button', // default for Toolbars, same as 'tbbutton'
        text: '100%',
        xtype : 'label',
        name : 'labelZoomName',
        cls : 'zoomFactorLabel',
        autoWidth : true
    });
    
    var tb = new Ext.Toolbar({
        renderTo: id,
        height: 50,
        enableOverflow : true,
        cls : 'preview-toolbar',
        items: [
            {
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
            },
            '->',
            labelPourcentage
            ,
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar/zoom-in.png",
                tooltip : i18n.get('label.zoomIn'),
                iconCls : 'previewBarBtn-icon',
                handler : function () {
                    var frameDimension = viewer.getFrameDimension();
                    viewer.zoomTo(viewer.getZoomLevel() + 1, frameDimension[0] / 2, frameDimension[1] / 2);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar/zoom-out.png",
                tooltip : i18n.get('label.zoomOut'),
                iconCls : 'previewBarBtn-icon',
                handler : function () {
                    var frameDimension = viewer.getFrameDimension();
                    viewer.zoomTo(viewer.getZoomLevel() - 1, frameDimension[0] / 2, frameDimension[1] / 2);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + '/common/res/multizoom/images/toolbar/download-image.png',
                tooltip : i18n.get('label.downloadImage'),
                iconCls : 'previewBarBtn-icon',
                //text : 'download',
                handler : function () {
                    var img = viewer.frameElement.firstChild;
                    sitools.user.component.dataviews.dataviewUtils.downloadFile(img.src);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + '/common/res/multizoom/images/toolbar/copy-image.png',
                tooltip : i18n.get('label.copyImage'),
                iconCls : 'previewBarBtn-icon',
                //text : 'copy',
                handler : function () {
                    var img = viewer.frameElement.firstChild;
                    sitools.user.component.dataviews.dataviewUtils.copyImageToClipboard(img.src);
                }
            },
            {
                xtype: 'button', // same as 'tbsplitbutton'
                icon : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/stretch-optimally.png',
                tooltip : i18n.get('label.resetZoom'),
                iconCls : 'previewBarBtn-icon',
                handler : function () {
                    viewer.reset();
                }
            }, {
                xtype : 'component',
                autoEl : {
                    tag : 'img',
                    style : 'padding-left:10px;',
                    src : loadUrl.get('APP_URL') + "/common/res/multizoom/images/toolbar" + '/movement-controls.png',
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
    
    
	
}