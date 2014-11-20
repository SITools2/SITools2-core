/*global viewer, document, retInt, window, fireEvent, getMouseXY, Event*/
function setClassNames(element, tabClassNames) {
    element.className = tabClassNames.join(" ");
}

function addClassName(element, className) {
    var classNames = element.className;
    var tabClassNames = classNames.split(" ");
    var contains = false;
    for (var i = 0; i < tabClassNames.length && !contains; i++) {
        if (tabClassNames[i] === className) {
            contains = true;
        }
    }
    if (!contains) {
        tabClassNames.push(className);
    }
    setClassNames(element, tabClassNames);
}

function removeClassName(element, className) {
    var classNames = element.className;
    var tabClassNames = classNames.split(" ");
    var tabClassNamesResult = [];
    for (var i = 0; i < tabClassNames.length; i++) {
        if (tabClassNames[i] !== className) {
            tabClassNamesResult.push(tabClassNames[i]);
        }
    }
    setClassNames(element, tabClassNamesResult);
}

viewer.preview = function (viewer) {

	var self = this;
	var previewFrameElement = document.createElement('div');
	var previewBox = document.createElement('div');
	var previewDimension = [200, 200];
	var image = document.createElement("img");
	var mylastMousePosition = [];

	self.getPreviewFrameDimension = function () {
		return [ previewDimension[0], previewDimension[1] ];
	};

	

	self.fitToPreviewFrame = function (width, height, frameDimension) { // width
																		// and
																		// height
																		// of
																		// image
		var newWidth, newHeight;

		newWidth = frameDimension[0];
		newHeight = Math.round((newWidth * height) / width);
		if (newHeight > (frameDimension[1])) {
			newHeight = frameDimension[1];
			newWidth = Math.round((newHeight * width) / height);
		}
		return [ newWidth, newHeight ];
	};

	self.setPreviewSize = function (width, height) { // width and height of image
		previewBox.style.width = width + "px";
		previewBox.style.height = height + "px";
	};

	self.getPreviewSize = function () { // width and height of image
		return [retInt(previewBox.style.width, "px"),
				retInt(previewBox.style.height, "px") ];
	};

	self.setPreviewPosition = function (x, y) { // width and height of image
		previewBox.style.left = x + "px";
		previewBox.style.top = y + "px";
	};
	
	self.getPreviewPosition = function () { 
		return [ retInt(previewBox.style.left, "px"),
					retInt(previewBox.style.top, "px") ];
		
	};

	self.setImagePosition = function (x, y) { // x and y coordinate of image
		image.style.left = (Math.round(x) + 'px');
		image.style.top = (Math.round(y) + 'px');
	};

	self.getImageDimension = function () {
		return [ image.width, image.height ];
	};

	self.getImagePosition = function () {
		return [ retInt(image.style.left, "px"), retInt(image.style.top, "px") ];
	};
	
	self.onmousemove = function (event) {
		if (!event) {//For IE
			event = window.event;
            event.returnValue = false;
		}
		else if (event.preventDefault) {
		    event.preventDefault();
		}
		

		var mousePosition = getMouseXY(event);
		var position = self.getPreviewPosition();

		var imageSize = self.getImageDimension();
		var previewSize = self.getPreviewSize();
		

		position[0] += (mousePosition[0] - mylastMousePosition[0]);
        position[1] += (mousePosition[1] - mylastMousePosition[1]);
		mylastMousePosition = mousePosition;
		
		if (position[0] < 0) {
            position[0] = 0;
        } else if (position[0] + previewSize[0] > imageSize[0]) {
            position[0] = imageSize[0] - previewSize[0];
        }

        if (position[1] < 0) {
            position[1] = 0;
        } else if (position[1] + previewSize[1] > imageSize[1]) {
            position[1] = imageSize[1] - previewSize[1];
        }

		self.setPreviewPosition(position[0], position[1]);
		var positionRealImage = self.calcRealImagePosition(position);
		viewer.setPosition(positionRealImage[0], positionRealImage[1]);
	};
	

	self.onmouseup_or_out = function (event) {
	    if (!event) { // For IE
            event = window.event;
            event.returnValue = false;
        } else if (event.preventDefault) {
            event.preventDefault();
        }
        removeClassName(previewBox, "previewBox_over");
        previewFrameElement.onmousemove = previewBox.onmouseup = null;
        previewBox.onmousedown = self.onmousedown;
    };
    
	self.onmousedown =  function (event) {
//		self.previewFrameElement.focus();
	    if (!event) { // For IE
            event = window.event;
            event.returnValue = false;
        } else if (event.preventDefault) {
            event.preventDefault();
        }
	    addClassName(previewBox, "previewBox_over");
		mylastMousePosition = getMouseXY(event);
		previewFrameElement.onmousemove = self.onmousemove;
        previewBox.onmouseup = self.onmouseup_or_out;
	};
	
	self.calcRealImagePosition = function (position) {
		var imageSize = viewer.getDimension();
		var imagePreviewSize = self.getImageDimension();

		var ratioImageX = imagePreviewSize[0] / imageSize[0];
		var ratioImageY = imagePreviewSize[1] / imageSize[1];

		var frameSize = viewer.getFrameDimension();

		var diffX = 0;
        var diffY = 0;
        if (frameSize[0] > imageSize[0]) {
            diffX = (frameSize[0] - imageSize[0]) / 2;
        }
        if (frameSize[1] > imageSize[1]) {
            diffY = (frameSize[1] - imageSize[1]) / 2;
        }
		
		var posX = position[0] / ratioImageX - diffX;
		var posY = position[1] / ratioImageY - diffY;

		return [-posX, -posY];
	};
	
	
	
	var imageSize = viewer.getOriginalDimension();
	var imageDimension = self.fitToPreviewFrame(imageSize[0], imageSize[1], previewDimension);
		
	previewDimension[0] = imageDimension[0];
	previewDimension[1] = imageDimension[1];

	previewFrameElement.style.zIndex = "250000";
	previewFrameElement.style.left = "0px";
	previewFrameElement.style.top = "0px";
	previewFrameElement.style.width = previewDimension[0] + "px";
	previewFrameElement.style.height = previewDimension[1] + "px";
	previewFrameElement.className = "previewFrame";

	previewBox.style.zIndex = "250010";
	previewBox.style.left = "0px";
	previewBox.style.top = "0px";
	previewBox.style.width = previewDimension[0] + "px";
	previewBox.style.height = previewDimension[1] + "px";
	previewBox.className = "previewBox";
	previewBox.onmousedown = self.onmousedown;

	
	image.style.position = "absolute";
	// TODO load a thumbnail
	// image.src = viewer.getImageSrc();
	image.src = viewer.previewSrc;

	

	image.style.width = Math.round(imageDimension[0]) + "px";
	image.style.height = Math.round(imageDimension[1]) + "px";

	var imagePosition = [0, 0];

	self.setImagePosition(imagePosition[0], imagePosition[1]);

	previewFrameElement.appendChild(image);
	previewFrameElement.appendChild(previewBox);
	viewer.frameElement.appendChild(previewFrameElement);

	var callbackZoomTo = function (viewer) {
		self.resizePreview(viewer);
		self.movePreview(viewer);

	};

	self.resizePreview = function (viewer) {
		var imageSize = viewer.getDimension();
		var frameSize = viewer.getFrameDimension();
		var previewSize = self.getPreviewFrameDimension();
		

		var x;
        var y;
        if (frameSize[0] > imageSize[0]) {
            x = previewSize[0];
        } else {
            x = previewSize[0] * frameSize[0] / imageSize[0];
        }

        if (frameSize[1] > imageSize[1]) {
            y = previewSize[1];
        } else {
            y = previewSize[1] * frameSize[1] / imageSize[1];
        }

        // ajust the position of the imagePreview if the image doesn't fill the
        // whole frame
        self.setPreviewSize(x, y);
	};

	self.movePreview = function (viewer) {

		var imageSize = viewer.getDimension();
		var frameSize = viewer.getFrameDimension();
		var imagePreviewSize = self.getImageDimension();

		var ratioImageX = imagePreviewSize[0] / imageSize[0];
		var ratioImageY = imagePreviewSize[1] / imageSize[1];

		var position = viewer.getPosition();
		
		var posX;
		var posY; 

		if (frameSize[0] > imageSize[0]) {
            posX = 0;
        } else {
            posX = (Math.abs(position[0]) * ratioImageX);
        }

        if (frameSize[1] > imageSize[1]) {
            posY = 0;
        } else {
            posY = Math.abs(position[1]) * ratioImageY;
        }

		self.setPreviewPosition(posX, posY);
	};

	Event.observe(viewer.frameElement, 'zoomto', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'resize', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'reset', callbackZoomTo.bind(self, viewer));
    Event.observe(viewer.frameElement, 'move', self.movePreview.bind(self, viewer));
    Event.observe(viewer.frameElement, 'moveby', self.movePreview.bind(self, viewer));
    
};

function debugTab(tab) {
    var str = "[";
    for (var i = 0; i < tab.length; i++) {
        if (i !== 0) {
            str += ",";
        }
        str += tab[i];
    }
    str += "]";
    return str;
}


