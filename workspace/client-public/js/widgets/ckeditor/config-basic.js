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
	
	config.basicEntities = false;
	
	// The toolbar groups arrangement, optimized for two toolbar rows.
    config.toolbar = [
		{ name: 'document',    items : [ 'Source','Preview'] },
		{ name: 'basicstyles', items : [ 'Bold','Italic','Underline'] },
		{ name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
		{ name: 'insert',      items : [ 'Link', 'Image','Table' ] },
		'/',
		{ name: 'styles',      items : [ 'Format','Font','FontSize' ] },
		{ name: 'colors',      items : [ 'TextColor','BGColor' ] },
		{ name: 'tools',       items : [ 'Maximize', 'ShowBlocks','-','About' ] }
		];
	
  

};
