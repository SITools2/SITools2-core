/*
    This file is generated and updated by Sencha Cmd. You can edit this file as
    needed for your application, but these edits will have to be merged by
    Sencha Cmd when upgrading.
*/

var userPreferences = null;
var userLogin = null;

Ext.util.openLink = function(href) {
    var link = document.createElement('a');
    link.setAttribute('href', href);
    link.setAttribute('target','_blank');
    var clickevent = document.createEvent('Event');
    clickevent.initEvent('click', true, false);
    link.dispatchEvent(clickevent);
    return false;
}

Ext.application({
    name: 'sitools.clientportal',

    extend: 'sitools.clientportal.Application',
    
    paths : {
        "sitools.public" : "../client-public/js",
        "sitools.user" : "../client-user/app"
    }
    
});
