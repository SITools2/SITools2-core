/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	config.language = 'en';
	// config.uiColor = '#AADC6E';
	config.skin = 'moonocolor';
	
	config.allowedContent = true;
	
	config.toolbar = [
       { name: 'document',    items : [ 'Source','Preview'] },
       { name: 'clipboard',   items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
       { name: 'editing',     items : [ 'Find','Replace','-','SelectAll' ] },
       { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
       { name: 'links',       items : [ 'Link','Unlink','Anchor' ] },
       { name: 'tools',       items : [ 'Maximize', 'ShowBlocks','-','About' ] },
       '/',
       { name: 'styles',      items : [ 'Styles','Format','Font','FontSize' ] },
       { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
       { name: 'colors',      items : [ 'TextColor','BGColor' ] },
       { name: 'insert',      items : [ 'Image','Table','HorizontalRule','SpecialChar', 'Iframe' ] }
       ];

};
