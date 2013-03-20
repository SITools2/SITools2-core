/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.Image
 * @extends Ext.util.Observable
 * <p>A plugin that creates an image button in the HtmlEditor toolbar for inserting an image. The method to select an image must be defined by overriding the selectImage method. Supports resizing of the image after insertion.</p>
 * <p>The selectImage implementation must call insertImage after the user has selected an image, passing it a simple image object like the one below.</p>
 * <pre>
 *      var img = {
 *         Width: 100,
 *         Height: 100,
 *         ID: 123,
 *         Title: 'My Image'
 *      };
 * </pre>
 */
Ext.ux.form.HtmlEditor.Image = Ext.extend(Ext.util.Observable, {
	// Image language text
	langTitle: 'Insert Image',
    urlSizeVars: ['width','height'],
    basePath: 'image.php',
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
        this.cmp.on('initialize', this.onInit, this, {single: true});
    },
    
    onEditorMouseUp : function(e){
        Ext.get(e.getTarget()).select('img').each(function(el){
            var w = el.getAttribute('width'), h = el.getAttribute('height'), src = el.getAttribute('src')+' ';
            src = src.replace(new RegExp(this.urlSizeVars[0]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[0]+'='+w+'$1');
            src = src.replace(new RegExp(this.urlSizeVars[1]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[1]+'='+h+'$1');
            el.set({src:src.replace(/\s+$/,"")});
        }, this);
        
    },
    onInit: function(){
        Ext.EventManager.on(this.cmp.getDoc(), {
			'abort': this.onEditorMouseUp,
			scope: this
		});
    },
    onRender: function() {
    	
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-image',
            scope: this,
            handler: this.selectImage,
            tooltip: {
                title: this.langTitle
            },
            overflowText: this.langTitle
        });
    },
    selectImage: function (){
    	 function validate(data, config) {
             config.fieldUrl.setValue(data.url);
         }
         var chooser = new ImageExplorer({
        	 directoryImageUrl : this.cmp.directoryImageUrl,
             width : 500,
             height : 350,
             htmlEditor : this.cmp
         });
         chooser.show(document);
    },
    
    insertImage: function(img) {
        this.cmp.insertAtCursor('<img src="'+this.basePath+'?'+this.urlSizeVars[0]+'='+img.Width+'&'+this.urlSizeVars[1]+'='+img.Height+'&id='+img.ID+'" title="'+img.Name+'" alt="'+img.Name+'">');
    }
    
});

Image.insertImage = function(data, config) {
	var htmlEditor = config.htmlEditor;
     var url = data.url;
    var indexDatastorageUrl = url.indexOf(config.directoryImageUrl);
    if (indexDatastorageUrl !== -1) {
        url = "./images" + url.substr(indexDatastorageUrl + config.directoryImageUrl.length, url.length);
    }
    
	if (data.width && data.height){
		htmlEditor.insertAtCursor('<img src="'+ url +'" title="'+ data.text +'" width="'+ data.width +'" height="'+ data.height +'" alt="'+ data.text +'">');
	}
	else {
		htmlEditor.insertAtCursor('<img src="'+ url +'" title="'+ data.text +'" alt="'+ data.text +'">');
	}
};
