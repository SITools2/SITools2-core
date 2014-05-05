CKEDITOR.plugins.add( 'documentimport', {
    icons: 'documentimport',
    init: function( editor ) {
        
        editor.addCommand( 'documentDialog', new CKEDITOR.dialogCommand('documentDialog'));
        
        editor.ui.addButton( 'documentimport', {
            label: 'Insert Document',
            command: 'documentDialog',
            toolbar: 'links'
        });
        
        CKEDITOR.dialog.add( 'documentDialog', this.path + 'dialogs/documentimport.js' );
    }
});