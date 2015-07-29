/**
 * Created by m.gond on 29/07/2015.
 */

Ext.define('sitools.extension.utils.MizarUtils', {
    singleton: true,

    zoomTo: function (layer) {
        var barycenter = this.computeGeometryBarycenter(layer.features);
        mizarWidget.navigation.zoomTo(barycenter, 2.0, 2000);
    },

    computeGeometryBarycenter : function (features) {
        var sLon = 0;
        var sLat = 0;
        var nbGeometries = 0;

        for (var i = 0; i < features.length; i++) {
            var barycenter = MizarGlobal.utils.computeGeometryBarycenter(features[i].geometry);
            sLon += barycenter[0];
            sLat += barycenter[1];
            nbGeometries++;
        }

        return [sLon / nbGeometries, sLat / nbGeometries];
    }

});

mizarUtils = sitools.extension.utils.MizarUtils;