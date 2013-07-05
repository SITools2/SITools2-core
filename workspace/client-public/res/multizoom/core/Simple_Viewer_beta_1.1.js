/**
 *  Simple Javascript Image Viewer
    Copyright (C) 2010  Munawwar Firoz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details (http://www.gnu.org/licenses/)
*/
function getObjectXY(object) {
	var left,top;
	objectCopy=object;
	if (object.offsetParent) {
		left=top=0;
		do {
			left += object.offsetLeft;
			if(object.style.borderLeftWidth!='')
				left+=parseInt(object.style.borderLeftWidth);
			else
				object.style.borderLeftWidth='0px';
			top += object.offsetTop;
			if(object.style.borderTopWidth!='')
				top+=parseInt(object.style.borderTopWidth);
			else
				object.style.borderTopWidth='0px';
		}
		while (object = object.offsetParent);
	}
	return [left-parseInt(objectCopy.style.borderLeftWidth),top-parseInt(objectCopy.style.borderLeftWidth)];
}

//String compare (Case-sensitive)
function strcmp(string1,string2) {
	return (string1.length===string2.length && string1.indexOf(string2)!=-1);
}
//String compare (Case-insensitive)
function strcmpi(string1,string2) {
	return (string1.length===string2.length && string1.toLowerCase().indexOf(string2.toLowerCase())!=-1);
}
//string1 should end with string2 to return true (Case-insensitive)
function strEndsWith(string1,string2) {
	var index=string1.length-string2.length;
	return (string1.toLowerCase().lastIndexOf(string2.toLowerCase(),index)==index);
}
//Returns the constructor (which is a function) name
function returnDataType(object) {
	if(typeof object==='undefined')
		return 'undefined';
	if(typeof object==='null')
		return 'null';
	code=new String(object.constructor);
	return code.substring( code.indexOf(' ')+1, code.indexOf('(') );
}
//Verifies wether the datatype of a variable is as you expect it to be
function verifyDataType(object,Datatype_expected_in_string) {
	return strcmpi( returnDataType(object), Datatype_expected_in_string );
}
function retInt(str, suffix) {
	if(typeof str=='number')
		return str;
	var result=str.indexOf(suffix);
	return parseInt(str.substring(0,(result!=-1)?result:str.length))
}

/*Mouse related functions*/
//Used to retrieve the mouse cursor position on screen (but event is needed as argument)
function getMouseXY(event) {
	var posx = 0, posy = 0;
	if (!event) event = window.event;	//firefox
	if (event.pageX || event.pageY) {
		posx = event.pageX;
		posy = event.pageY;
	}
	else if (event.clientX || event.clientY) {	//IE
		posx = event.clientX + document.body.scrollLeft
			+ document.documentElement.scrollLeft;
		posy = event.clientY + document.body.scrollTop
			+ document.documentElement.scrollTop;
	}
	return [posx,posy];
}

function mouseWheel() {
	var self=this;
	/*Event handlers*/
	/*Mouse wheel functions*/

	//Default mouse wheel callback function
	//Variable local to 'this'
	var wheelCallback = function(event,object,delta){
		/*Override this function and write your code there*/
		/*
			delta=-1 when mouse wheel is rolled backwards (towards yourself)
			delta=1 when mouse wheel is rolled forward (away from one's self)
			Note: Here is where you can call the getMouseXY function using the 'event' argument
		*/
	}
	//Mouse wheel event handler
	self.wheelHandler = function (event){
		var delta = 0;
		if (!event) //For IE
			event = window.event;
		if (event.wheelDelta) 	//IE
		{
			delta = event.wheelDelta/120;
			//if (window.opera) delta = -delta; //for Opera...hmm I read somewhere opera 9 need the delta sign inverted...tried in opera 10 and it doesnt require this!?
		}
		else if (event.detail) //firefox
			delta = -event.detail/3;

		if (event.preventDefault)
			event.preventDefault();
		event.returnValue = false;
		if (delta)
			wheelCallback(event,this,delta);	//callback function
	}
	//Mouse wheel initialization
	self.init = function(object,callback) {
		if (object.addEventListener) //For firefox
			object.addEventListener('DOMMouseScroll', this.wheelHandler, false); //Mouse wheel initialization
		//For IE
		object.onmousewheel = this.wheelHandler; //Mouse wheel initialization
		wheelCallback=callback;
	}
	this.setCallback = function(callback){
		wheelCallback=callback;
	}
}

//Debugging
function debug_msgs() {
	this.counter=0;
	this.clear=function() {
		var div=document.getElementById('debug');
		div.innerHTML='';
		this.counter=0;
	}
	this.print=function(string) {
	var div=document.getElementById('debug');
		div.innerHTML+=string;
	}
	this.println=function(string) {
		var div=document.getElementById('debug');
		div.innerHTML+=string+'<br>';
		this.counter++;
	}
}

function setVisible(element, visibility){
	element.style.display = visibility?"block":"none";
}


var debug=new debug_msgs();

/*-------------The image viewer--------------*/
function viewer(arguments) //argument array
{
	var self=this;

	/*Properties*/
	//Public access
	self.outerFrame=null;
	
	//Private access
	var image=null,imageSource=null,parent=null,replace=null,preLoader=null;
	var frame=['400px','400px',true]; //Format: ['width in px or % (number)','height in px or % (number)', auto adjust frameElement to image (boolean)]
	var borderClass=null;
	var zoomFactor='10%'; //10% increase per zoom in			
	var maxZoom='300%'; //Either 'percentage' or ['max width in pixel', 'max height in pixel']

	/*Set user defined properties and configurations*/
	/*
		The following configurations are for pure javascript image viewers:
			imageSource : string - Source to the image you want to show
			
			parent : HTMLElement - The parent element of the image viewer
			or
			replace: HTMLElement - The image viewer replace this HTML element
			(Exactly one of the above two properties is absolutly needed)
			
			preLoader (optional) : string - Source to a pre-loader image. Useful in case of large images
			
		The following configurations are for partial javascript/partial HTML image viewers:
			image - HTMLElement - The reference to the image HTML element
		
		Common to both:
			frame - An array of format [width, height, widthIsMax, heightIsMax]
				width and height are strings like '400px' or '100%'. width and height can be in px or %
				widthIsMax and heightIsMax are optional boolean values. 
				Say if widthIsMax is set to true, it treats the width as the maximum width limit.
				So if the zoomed image isn't fitting exactly into the frameElement, the frameElement dimension is reduced and adjusted to fit the image.
			
			zoomFactor (optional) - number - Decides the amount zoomed per zoom event. Default is set to 10 (in %)
			maxZoom (optional) - Sets the makimum image zoom in percentage or pixels. Format :Either 'percentage' or ['max width in pixel', 'max height in pixel']
				Example: '150%' or ['500px,'500px']
	*/
	var key;
	for (key in arguments) {
		var temp=arguments[key];
		eval(key + '=temp;');
	}

	/*Internal states,HTML elements and properties*/
	self.frameElement = null;
	var orignalW,orignalH, zoomLevel=0;
	var lastMousePosition=null, speed=5;
	var mouseWheelObject=null;

	/*Methods*/
	self.getFrameDimension =  function() {
		return [self.frameElement.clientWidth,self.frameElement.clientHeight];
	}				
	self.setDimension = function(width,height) { //width and height of image
		image.width=Math.round(width);
		image.height=Math.round(height);
	}
	self.getDimension =  function() {
		return [image.width,image.height];
	}
	self.setPosition = function(x,y) { //x and y coordinate of image
		image.style.left=(Math.round(x)+'px');
		image.style.top=(Math.round(y)+'px');
	}
	self.getPosition = function() {
		return [retInt(image.style.left,'px'),retInt(image.style.top,'px')];
	}
	self.setMouseCursor = function() {
		var dimension = self.getDimension();
		var frameDimension =  self.getFrameDimension();
		
		var cursor='crosshair';
		if(dimension[0]>frameDimension[0] && dimension[1]>frameDimension[1])
			cursor='move';
		else if(dimension[0]>frameDimension[0])
			cursor='e-resize';
		else if(dimension[1]>frameDimension[1])
			cursor='n-resize';
		
		image.style.cursor=cursor;
	}
	self.maxZoomCheck = function(width,height) {
		if(typeof width=='undefined' || typeof height=='undefined') {
			var temp = self.getDimension();
			width=temp[0], height=temp[1];
		}
		if(typeof maxZoom=='number') {
			return ((width/orignalW)>maxZoom || (height/orignalH)>maxZoom);
		}
		else if(typeof maxZoom=='object') {
			return (width>maxZoom[0] || height>maxZoom[1]);
		}
	}
	self.fitToFrame = function(width, height) { //width and height of image
		if(typeof width=='undefined' || typeof height=='undefined') {
			width=orignalW, height=orignalH;
		}
		var frameDimension = self.getFrameDimension(), newWidth,newHeight;
		
		newWidth = frameDimension[0];
		newHeight = Math.round((newWidth*height)/width);
		if(newHeight>(frameDimension[1])) {
			newHeight = frameDimension[1];
			newWidth = Math.round((newHeight*width)/height); 
		}
		return [newWidth,newHeight];
	}
	self.getZoomLevel = function() {
		return zoomLevel;
	}
	self.getZoomFactor = function() {
		return zoomFactor;
	}
	self.zoomTo = function(newZoomLevel, x, y) {
		var frameDimension = self.getFrameDimension();
		//check if x and y coordinate is within the self.frameElement
		if(newZoomLevel<0 || x<0 || y<0 || x>=frameDimension[0] || y>=frameDimension[1])
			return false;
		
		var dimension = self.fitToFrame(orignalW,orignalH);
		for(var i=newZoomLevel; i>0;i--)
			dimension[0] *= zoomFactor, dimension[1] *= zoomFactor;
		
		//Calculate percentage increase/decrease and fix the image over given x,y coordinate
		var curWidth=image.width, curHeight=image.height;
		var position = self.getPosition();
		
		//The Maths
		/*
			New point/Old point = New image width/Old image width
		=>	New point = New width/Old width * Old point
			
			Difference between new and old point 
			= New point - Old point
			= New width/Old width * Old point - Old point
			= Old Point * (New width/Old width - 1)
			
			Moving the image by this difference brings the zoomed image to the same (pivot) point.
			
			The point (x,y) sent into this function is relative to the self.frameElement. However, it should be relative to the image for the above formula to work.
			Hence, point = (x-left, y-top).
		*/
		position[0]-=((x-position[0])*((dimension[0]/curWidth)-1)), position[1]-=((y-position[1])*((dimension[1]/curHeight)-1)); //Applying the above formula
		
		
		//Center image
		position = self.centerImage(dimension[0],dimension[1], position[0],position[1]);
		
		//Set dimension and position
		if(!self.maxZoomCheck(dimension[0],dimension[1])) {
			zoomLevel = newZoomLevel;
			self.setDimension(dimension[0],dimension[1]);
			self.setPosition(position[0],position[1]);
			self.setMouseCursor();
			self.fireEvent('zoomto');
		}
		else
			return false;
		return true;
	}
	self.centerImage = function(width,height, x,y) { //width and height of image and (x,y) is the (left,top) of the image
		if(typeof width=='undefined' || typeof height=='undefined') {
			var temp = self.getDimension();
			width=temp[0], height=temp[1];
		}
		if(typeof x=='undefined' || typeof y=='undefined') {
			var temp = self.getPosition();
			x=temp[0], y=temp[1];
		}
			
		var frameDimension = self.getFrameDimension();
		
		if(width<=frameDimension[0])
			x = Math.round((frameDimension[0] - width)/2);
		if(height<=frameDimension[1])
			y = Math.round((frameDimension[1] - height)/2);

		if(width>frameDimension[0]) {
			if(x>0)
				x=0;
			else
			if((x+width)<frameDimension[0])
				x=frameDimension[0]-width;
		}

		if(height>frameDimension[1]) {
			if(y>0)
				y=0;
			else
			if((y+height)<frameDimension[1])
				y=frameDimension[1]-height;
		}

		return [x,y];
	}
	self.relativeToAbsolute = function(x,y) {
		if(x<0 || y<0 || x>=self.frameElement.clientWidth || y>=self.frameElement.clientHeight)
			return null;
		return [x-retInt(image.style.left,'px'),y-retInt(image.style.top,'px')];
	}
	self.reset = function() {
		var dimension = self.fitToFrame(orignalW,orignalH);
		var position = self.centerImage(dimension[0],dimension[1], 0,0);
		self.setDimension(dimension[0],dimension[1]);
		self.setPosition(position[0],position[1]);
		zoomLevel=0;
		self.fireEvent("reset");
	}
	self.moveBy = function(x,y) {
		var position = self.getPosition();
		position = self.centerImage(image.width,image.height, position[0]+x,position[1]+y);
		self.setPosition(position[0],position[1]);
		self.fireEvent("moveby");
		
	}
	self.hide = function() {
		if(self.outerFrame)
			self.outerFrame.style.display='none';
		else
			self.frameElement.style.display = 'none';
	}
	self.show = function() {
		if(self.outerFrame)
			self.outerFrame.style.display='block';
		else
			self.frameElement.style.display = 'block';
	}
	//Experimental
	self.moveTo = function(x,y) { //Coordinates relative to (left,top) of image
		if(x<0 || y<0 || x>=image.width || y>=image.height)
			return;
		var left = self.frameElement.clientWidth/2-x, top = self.frameElement.clientHeight/2-y;
		var position = self.centerImage(image.width,image.height, left,top);
		self.setPosition(position[0],position[1]);
	}

	/*User defined events*/
	//Non-static events
	self.onload = null;
	
	/*Event handlers*/
	self.onmousewheel = function(event,object,direction) {
		self.frameElement.focus();
		if (!event) //For IE
			event=window.event, event.returnValue = false;
		else
		if (event.preventDefault)
			event.preventDefault();
		
		if((zoomLevel+direction)>=0) {
			var mousePos = getMouseXY(event);
			var framePos = getObjectXY(self.frameElement);
			self.zoomTo(zoomLevel+direction, mousePos[0]-framePos[0], mousePos[1]-framePos[1]);
		}
	}
	self.onmousemove = function(event) {
		if (!event) //For IE
			event=window.event, event.returnValue = false;
		else
		if (event.preventDefault)
			event.preventDefault();
		
		var mousePosition=getMouseXY(event);
		var position = self.getPosition();
		position[0]+=(mousePosition[0]-lastMousePosition[0]), position[1]+=(mousePosition[1]-lastMousePosition[1]);
		lastMousePosition=mousePosition;
		
		position = self.centerImage(image.width,image.height, position[0],position[1]);
		self.setPosition(position[0],position[1]);
		self.fireEvent("move");
	}
	self.onmouseup_or_out = function(event) {
		if (!event) //For IE
			event=window.event, event.returnValue = false;
		else
		if (event.preventDefault)
			event.preventDefault();
		
		image.onmousemove=image.onmouseup=image.onmouseout=null;
		image.onmousedown=self.onmousedown;
	}
	self.onmousedown =  function(event) {
		self.frameElement.focus();
		if (!event) //For IE
			event=window.event, event.returnValue = false;
		else
		if (event.preventDefault)
			event.preventDefault();
	
		lastMousePosition=getMouseXY(event);
		image.onmousemove = self.onmousemove;
		image.onmouseup=image.onmouseout=self.onmouseup_or_out;
	}
	self.onkeypress = function(event) {
		var keyCode;
		if(window.event) // IE
			event=window.event, keyCode = event.keyCode, event.returnValue = false;
		else
		if(event.which) // Netscape/Firefox/Opera
			keyCode = event.which, event.preventDefault();
		
		keyCode = String.fromCharCode(keyCode);
		
		var position = self.getPosition();
		var LEFT='a',UP='w',RIGHT='d',DOWN='s', CENTER_IMAGE='c', ZOOMIN='=', ZOOMOUT='-'; ///Keys a,w,d,s
		if(keyCode==LEFT)
			position[0]+=speed;
		else if(keyCode==UP)
			position[1]+=speed;
		else if(keyCode==RIGHT)
			position[0]-=speed;
		else if(keyCode==DOWN)
			position[1]-=speed;
		else if(keyCode==CENTER_IMAGE || keyCode=='C')
			self.reset();
		else if(keyCode==ZOOMIN || keyCode=='+' || keyCode=='x' || keyCode=='X')
			self.zoomTo(zoomLevel+1, self.frameElement.clientWidth/2, self.frameElement.clientHeight/2);
		else if( (keyCode==ZOOMOUT || keyCode=='z' || keyCode=='Z') && zoomLevel>0)
			self.zoomTo(zoomLevel-1, self.frameElement.clientWidth/2, self.frameElement.clientHeight/2);
		
		if(keyCode==LEFT || keyCode==UP || keyCode==RIGHT || keyCode==DOWN) {
			position = self.centerImage(image.width,image.height, position[0],position[1]);
			self.setPosition(position[0],position[1]);
			speed+=2;
		}
	}
	self.onkeyup = function(event) {
		speed=5;
	}
	/*Initializaion*/
	self.setZoomProp = function(newZoomFactor,newMaxZoom) {
		if(newZoomFactor==null)
			zoomFactor=10;
		zoomFactor=1 + retInt(newZoomFactor,'%')/100;
		
		if(typeof newMaxZoom=='string')
			maxZoom = retInt(newMaxZoom,'%')/100;
		else if(typeof newMaxZoom=='object' && newMaxZoom!=null) {
			maxZoom[0]=retInt(newMaxZoom[0],'px');
			maxZoom[1]=retInt(newMaxZoom[1],'px');
		}
		else maxZoom='300%';
	}
	
	self.setFrameProp = function(newFrameProp) {
		self.frameElement.style.width=newFrameProp[0];
		self.frameElement.style.height=newFrameProp[1];
	}
	self.initImage = function() {
		image.style.maxWidth=image.style.width=image.style.maxHeight=image.style.height=null;
		orignalW=image.width;
		orignalH=image.height;
		
		var dimension=self.fitToFrame(orignalW, orignalH);
		self.setDimension(dimension[0],dimension[1]);
		
		if(frame[2]==true)
			self.frameElement.style.width=(Math.round(dimension[0])+ 'px');
		if(frame[3]==true)
			self.frameElement.style.height=(Math.round(dimension[1]) + 'px');
		
		var pos = self.centerImage(dimension[0],dimension[1], 0,0);
		self.setPosition(pos[0],pos[1]);
		self.setMouseCursor();
		
		//Set mouse handlers
		mouseWheelObject = new mouseWheel();
		mouseWheelObject.init(image, self.onmousewheel);
		image.onmousedown = self.onmousedown;
		
		//Set keyboard handlers
		self.frameElement.onkeypress = self.onkeypress;
		self.frameElement.onkeyup = self.onkeyup;
		
		if(viewer.onload!=null)
			viewer.onload(self);
		if(self.onload!=null)
			self.onload();
	}
	self.preInitImage = function() { //Triggers after pre-Loader image has been loaded					
		if(preLoader!=null) 
		{
			image.style.left=((self.frameElement.clientWidth-image.width)/2) + 'px';
			image.style.top=((self.frameElement.clientHeight-image.height)/2) + 'px';
		}
		image.onload=self.initImage;
		image.src=imageSource;
	}				
	self.setNewImage = function(newImageSource,newPreLoader) {
		if(typeof newImageSource=='undefined')
			return;
		imageSource=newImageSource;
		if(typeof newPreLoader!=='undefined')
			preLoader=newPreLoader;
		if(preLoader!=null) {
			image.onload=self.preInitImage;
			image.src=preLoader;
			return;
		}
		image.onload=self.initImage;
		image.src=imageSource;
	}
	
	self.getImageSrc = function() {
		return image.src;
	}
	
	self.fireEvent = function (event){
		var element = self.frameElement;
	    if (document.createEventObject){
	    // dispatch for IE
	    var evt = document.createEventObject();
	    return element.fireEvent('on'+event,evt)
	    }
	    else{
	    // dispatch for firefox + others
	    var evt = document.createEvent("HTMLEvents");
	    evt.initEvent(event, true, true ); // event type,bubbling,cancelable
	    return !element.dispatchEvent(evt);
	    }
	}
	
	self.getOriginalDimension = function () {
		return [orignalW,orignalH];
	}

	
	/*Set a base*/
	self.setZoomProp(zoomFactor,maxZoom);
	//Create self.frameElement - One time initialization
	self.frameElement=document.createElement('div');
	self.frameElement.style.width=frame[0];
	self.frameElement.style.height=frame[1];
	self.frameElement.style.border="0px solid #000";
	self.frameElement.style.margin="0px";
	self.frameElement.style.padding="0px";
	self.frameElement.style.overflow="hidden";
	self.frameElement.style.position="relative";
	self.frameElement.style.zIndex=2;
	self.frameElement.tabIndex=1;
			
	if(image!=null) {
		if (parent != null) {
			image.parentNode.removeChild(image);
			parent.appendChild(self.frameElement);
		}
		else if (replace != null) {
			image.parentNode.removeChild(image);
			replace.parentNode.replaceChild(self.frameElement, replace);
		}
		else
			image.parentNode.replaceChild(self.frameElement,image);
		
		image.style.margin=image.style.padding="0";
		image.style.borderWidth="0px";
		image.style.position='absolute';
		image.style.zIndex=3;
		self.frameElement.appendChild(image);
		
		if(imageSource!=null)
			self.preInitImage();
		else
			self.initImage();
	}
	else {		
		if(parent!=null)
			parent.appendChild(self.frameElement);
		else if(replace!=null)
			replace.parentNode.replaceChild(self.frameElement,replace);
			
		image=document.createElement('img');
		image.style.position='absolute';
		image.style.zIndex=3;
		self.frameElement.appendChild(image);
		
		self.setNewImage(imageSource);
	}
	//Experimental
	if(borderClass!=null) { //Browser rendering of borders with padding have been a problem.
		self.outerFrame = document.createElement('div');
		self.outerFrame.className=borderClass;
		self.frameElement.parentNode.replaceChild(self.outerFrame,self.frameElement);
		self.outerFrame.appendChild(self.frameElement);
	}
}
//Static events
viewer.onload = null;