CKEDITOR.dialog.add( 'documentDialog', function ( editor ) {
    
    return {
        title: i18n.get('label.insertDocTitle'),
        minWidth: 400,
        minHeight: 200,
        contents: [{
                id: 'mainFrameDocument',
                label: 'Basic Settings',
                elements: [{
                    id: 'textDocUrlID',
                    type: 'text',
                    editable : false,
                    label: i18n.get('label.file'),
                    style: 'width: 100%',
                    'default': '',
                    setup: function( data ) {
                        this.getElement().unselectable();
                    },
                    onKeyUp: function() {
                        this.allowOnChange = true;
                    },
                    onChange: function() {
                        // reset the dataLinkComponent value
                        if (this.getValue() == ""){
                            this.dataLinkComponent = null;
                        }
                        if ( this.allowOnChange ) // Dont't call on dialog load.
                        this.onKeyUp();
                    },
                    validate: function() {
                        var dialog = this.getDialog();
                        var documentText = dialog.getContentElement('mainFrameDocument','labelDocID');
                        
                        if (Ext.isEmpty(documentText.getValue())) {
                            documentText.setValue(this.documentName);
                        }
                        
                        if (!this.documentComponent || !this.getValue()) {
                            var text = i18n.get('label.noDocumentChoosen');
                            alert(text);
                            return false;
                        }
                        return true;
                        
                    },
                    commit: function( data ) {
                        if (!data.documentComponent) {
                            data.documentComponent = {};
                        }
                        
                        data.documentComponent.link = this.documentComponent;
                        // reset the dataLinkComponent value
                        this.documentComponent = null;
                    }
                }, {
                    type: 'button',
                    id: 'uploadDocument',
                    label: 'Document...',
                    title: i18n.get('label.chooseDocument'),
                    onClick: function() {
                        var docBrowser = new sitools.widget.sitoolsEditorPlugins.documentBrowser({
                            datastorageUrl : CKEDITOR.datastorageUrl,
                            dialog : CKEDITOR.dialog.getCurrent(),
                            editor : editor,
                            zindex : 20000
                         });
                         
                         docBrowser.show(document, function (data, config) {
//                             var dialog = CKEDITOR.dialog.getCurrent();
//                             dialog.setValueOf('info','txtUrl', data.url);
                         });
                    }
                }, {
                    id: 'labelDocID',
                    type: 'text',
                    label: "Texte",
                    style: 'width: 100%',
                    'default': '',
                    onLoad: function() {
                        this.allowOnChange = true;
                    },
                    onKeyUp: function() {
                        this.allowOnChange = true;
                    },
                    onChange: function() {
                        if ( this.allowOnChange ) // Dont't call on dialog load.
                        this.onKeyUp();
                    },
                    setup: function( data ) {
                        this.allowOnChange = false;
                        if (data.url)
                            this.setValue(data.url.url);
                        this.allowOnChange = true;

                    },
                    commit: function( data ) {
                        // IE will not trigger the onChange event if the mouse has been used
                        // to carry all the operations #4724
                        this.onChange();

                        if (!data.documentComponent) {
                            data.documentComponent = {};
                        }
                        
                        data.documentComponent.text = this.getValue();
                        this.allowOnChange = false;
                    }
                }, {
                    type: 'checkbox',
                    id: 'openIframeID',
                    label: i18n.get('label.openIframe'),
                    'default': 'unchecked',
                    onLoad: function() {
                        this.allowOnChange = true;
                    },
                    onKeyUp: function() {
                        this.allowOnChange = true;
                    },
                    onChange: function() {
                        if ( this.allowOnChange ) // Dont't call on dialog load.
                        this.onKeyUp();
                    },
                    setup: function( data ) {
                        this.allowOnChange = false;
                        if (data.url)
                            this.setValue(data.url.url);
                        this.allowOnChange = true;

                    },
                    commit: function( data ) {
                        this.onChange();

                        if (!data.documentComponent) {
                            data.documentComponent = {};
                        }
                        
                        if (this.getValue() == false) {
                            var documentText = this.getDialog().getContentElement('mainFrameDocument','textDocUrlID');
                            data.documentComponent.link = String.format("parent.sitools.user.component.dataviews.dataviewUtils.downloadFile(\"{0}\"); return false;",
                                    documentText.documentUrl);
                        }
                        
                        data.documentComponent.openIframe = this.getValue();
                        this.allowOnChange = false;
                    }
                }]
        }],
        onOk: function() {
            var attributes = {},
                removeAttributes = [],
                data = {},
                me = this,
                editor = this.getParentEditor();

            this.commitContent(data);

            attributes[ 'data-cke-saved-href' ] = '#';
            attributes[ 'data-cke-pa-onclick' ] = data.documentComponent.link;
            
            var selection = editor.getSelection();

            // Browser need the "href" fro copy/paste link to work. (#6641)
            attributes.href = attributes[ 'data-cke-saved-href' ];

            if ( !this._.selectedElement ) {
                var range = selection.getRanges( 1 )[ 0 ];

                // Use link URL as text with a collapsed cursor.
                if ( range.collapsed ) {
                    var textString;
                    textString = data.documentComponent.text; // insertion of document label
                    
                    var text = new CKEDITOR.dom.text(textString, editor.document);
                    range.insertNode(text);
                    range.selectNodeContents(text);
                }

                // Apply style.
                var style = new CKEDITOR.style({ element: 'a', attributes: attributes } );
                style.type = CKEDITOR.STYLE_INLINE; // need to override... dunno why.
                style.applyToRange( range );
                range.select();
            } else {
                // We're only editing an existing link, so just overwrite the attributes.
                var element = this._.selectedElement,
                    href = element.data( 'cke-saved-href' ),
                    textView = element.getHtml();

                element.setAttributes( attributes );
                element.removeAttributes( removeAttributes );

                if ( data.adv && data.adv.advName && CKEDITOR.plugins.link.synAnchorSelector )
                    element.addClass( element.getChildCount() ? 'cke_anchor' : 'cke_anchor_empty' );

                // Update text view when user changes protocol (#4612).
                if ( href == textView || data.type == 'email' && textView.indexOf( '@' ) != -1 ) {
                    // Short mailto link text view (#5736).
                    element.setHtml( data.type == 'email' ? data.email.address : attributes[ 'data-cke-saved-href' ] );
                }

                selection.selectElement( element );
                delete this._.selectedElement;
            }
        }
    };
    
   
});