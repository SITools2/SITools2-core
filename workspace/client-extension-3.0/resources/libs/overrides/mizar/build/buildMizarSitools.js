({
	baseUrl: "../src/mizar/js",
	name: "../../../build/almond",
	include: ['MizarWidget'],
	out: "../src/mizar/MizarWidget.Sitools.min.js",
	//Remove all wrapping to be abble to use Mizar the same way in Production and Development mode
	//wrap: {
 	//    start: "(function (root, factory) {\
	//	    if (typeof define === 'function' && define.amd) {\
	//		define(['jquery', 'underscore-min'], factory);\
	//	    } else {\
	//		root.MizarWidget = factory(root.$, root._);\
	//	    }\
	//	}(this, function ($, _) {",
	//    end: "return require('MizarWidget');}));"
	//},
	optimize: "uglify",
	paths: {
		"jquery": "../externals/jquery-1.11.1.min",
		"jquery.ui": "../externals/jquery-ui-1.11.0.min",
		"underscore-min": "../externals/underscore-1.6.0.min",
		"jquery.nicescroll.min": "../externals/jquery.nicescroll-3.5.4.min",
		"fits": "../externals/fits",
		"samp": "../externals/samp",
		"gzip": "../externals/gzip",
		"crc32": "../externals/crc32",
		"deflate-js": "../externals/deflate",
		"inflate-js": "../externals/inflate",
		"wcs": "../externals/wcs",
		"jquery.ui.timepicker": "../externals/jquery.ui.timepicker",
		"gw": "../externals/GlobWeb/src"
	},
	shim: {
		'jquery': {
			deps: [],
			exports: 'jQuery'
		},
		'jquery.ui': {
			deps: ['jquery'],
			exports: 'jQuery'
		},
		'underscore-min': {
			deps: ['jquery'],
			exports: '_'
		},
		'jquery.nicescroll.min': {
			deps: ['jquery'],
			exports: ''
		},
		'jquery.ui.timepicker': {
			deps: ['jquery.ui'],
			exports: 'jQuery'
		}
	},
  	uglify: {
        //Example of a specialized config. If you are fine
        //with the default options, no need to specify
        //any of these properties.
        output: {
            beautify: false
        },
        compress: {
 	    	unsafe: true
        },
        warnings: true,
        mangle: true
    }
})
