/******************************************************************************* 
* Copyright 2012, 2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES 
* 
* This file is part of SITools2. 
* 
* SITools2 is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
* 
* SITools2 is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
* 
* You should have received a copy of the GNU General Public License 
* along with SITools2. If not, see <http://www.gnu.org/licenses/>. 
******************************************************************************/ 

/**
 * Histogram module : create histogram to the given image
 */
Histogram = {
        init : function (options) {


         // Private variables
         var nbBins;

         var hist = [];
         var hmax; // histogram max to scale in image space

         // Origin histogram point
         var originX = 5.;
         var originY;
         var hwidth;
         var paddingBottom = 15.;

         /**
          *  Get mouse position on canvas
          */
         function _getMousePos(canvas, evt) {
         var rect = canvas.getBoundingClientRect();
             return {
                 x: evt.clientX - rect.left,
                 y: evt.clientY - rect.top
             };
         }

         /**
          *  Isoscele triangle object for thresholds manipulation
          *  
          *  @param a Pointer of threshold pointing on histogram
          *  @param b Isoscele point 1
          *  @param c Isoscele point 2
          */
         var Triangle = function(a,b,c)
         {
             this.initA = a.slice(0);
             this.initB = b.slice(0);
             this.initC = c.slice(0);

             this.a = a; // Pointer to histogram
             this.b = b; // Isoscele point 1
             this.c = c; // Isoscele point 2

             this.dragging = false;
             this.hover = false;
             this.halfWidth = Math.abs( (c[0] - b[0])/2 );
         }

         /**
          *  Reset to initial position
          */
         Triangle.prototype.reset = function()
         {
             this.a = this.initA.slice(0);
             this.b = this.initB.slice(0);
             this.c = this.initC.slice(0);
         }

         /**
          *  Test if triangle contains the given point
          */
         Triangle.prototype.contains = function(p)
         {
             return _pointInTriangle(p,this.a,this.b,this.c);
         }

         /**
          *  Draw the triangle
          */
         Triangle.prototype.draw = function(ctx)
         {
             if ( this.dragging )
             {
//                 ctx.fillStyle = "#FF0";
                 ctx.fillStyle = "#FF8F00";
             }
             else
             {
//                 ctx.fillStyle = "#F00"; 
                 ctx.fillStyle = "#FF8F00";
             }

             ctx.beginPath();
             ctx.moveTo(this.a[0],this.a[1]);
             ctx.lineTo(this.b[0],this.b[1]);
             ctx.lineTo(this.c[0],this.c[1]);
             ctx.closePath();
             ctx.fill();

             if ( !this.dragging && this.hover )
             {
//                 ctx.strokeStyle = "#FF0";
                 ctx.strokeStyle = "#FF9B2D";
                 ctx.stroke();
             }
         }

         /**
          *  Modify triangle's position by the given "pointer" point
          *  (could be modified only by X-axis)
          */
         Triangle.prototype.modifyPosition = function(point)
         {
             this.a[0] = point[0];
             this.b[0] = point[0]-this.halfWidth;
             this.c[0] = point[0]+this.halfWidth;
         }

         /**************************************************************************************************************/

         /**
          *  Test returning true if p1 and p2 are both lying on the same side of a-b, false otherwise
          */
         function _sameSide(p1,p2,a,b)
         {
             var temp1 = [];
             var temp2 = [];
             var temp3 = [];
             var cp1 = [];
             var cp2 = [];
             this.vec3().cross( this.vec3().subtract(b,a, temp1), this.vec3().subtract(p1,a,temp2), cp1 );
             this.vec3().cross( temp1, this.vec3().subtract(p2,a,temp3), cp2 );
             if ( this.vec3().dot( cp1,cp2 ) >= 0 )
             {
                 return true;
             }
             else
             {
                 return false;
             }
         }

         /**
          *  Private function to check if point is inside the given triangle
          *  If the point was on the same side of a-b as c and is also on the same side of b-c as a and on the same side of c-a as b, then it is in the triangle
          */
         function _pointInTriangle(p,a,b,c)
         {
             if ( _sameSide(p,a,b,c) && _sameSide(p,b,a,c) && _sameSide(p,c,a,b) )
             {
                 return true;
             }
             else
             {
                 return false;
             }
         }

         /**
          *  TODO: split on HistogramView and Histogram
          *  Histogram contructor
          *  @param options Histogram options
          *      <ul>
          *          <li>image: The image which is represented by current histogram(required)</li>
          *          <li>nbBins: Number of bins, representing the sampling of histogram(optional)</li>
          *          <li>onUpdate: On update callback
          *          <li>accuracy: The accuracy of histogram(numbers after floating point)
          *      </ul>
          */
//         var Histogram = function(options)
         
             this.jsFits = options.jsFits;
             this.viewer = options.viewer;
             this.vec3 = options.vec3;
             
             nbBins = options.nbBins || 256;
             this.image = options.image;
             this.onUpdate = options.onUpdate;
             this.accuracy = options.accuracy || 6;

             // Init canvas
             var canvas = document.getElementById(options.canvas);
             this.ctx = canvas.getContext('2d');

             // Init origins
             originY = canvas.height - paddingBottom;
             hwidth = nbBins + originX > canvas.width ? canvas.width : nbBins + originX; // clamp to canvas.width
             var triangleHalfWidth = 5;
             this.minThreshold = new Triangle(
                                         [originX,originY+1,0],
                                         [originX-triangleHalfWidth,originY+paddingBottom-1,0],
                                         [originX+triangleHalfWidth,originY+paddingBottom-1,0]
                                 );
             this.maxThreshold = new Triangle(
                                         [hwidth,originY+1, 0],
                                         [hwidth-triangleHalfWidth,originY+paddingBottom-1, 0],
                                         [hwidth+triangleHalfWidth,originY+paddingBottom-1, 0]
                                 );


             // Show bin pointed by mouse
             var self = this;
             canvas.addEventListener('mousemove', function(evt) {
                 var mousePos = _getMousePos(canvas, evt);

                 self.ctx.clearRect(0., originY, canvas.width, paddingBottom);           

                 if ( self.minThreshold.contains( [mousePos.x, mousePos.y, 0] ) )
                 {
                     self.minThreshold.hover = true;
                 }
                 else
                 {
                     self.minThreshold.hover = false;
                 }

                 if ( self.maxThreshold.contains( [mousePos.x, mousePos.y, 0] ) )
                 {
                     self.maxThreshold.hover = true;
                 }
                 else
                 {
                     self.maxThreshold.hover = false;
                 }

                 // Draw threshold controls
                 if ( self.minThreshold.dragging && mousePos.x >= self.minThreshold.initA[0] && mousePos.x < self.maxThreshold.a[0] )
                 {
                     self.minThreshold.modifyPosition([mousePos.x, self.minThreshold.a[1]]);
                 }

                 if ( self.maxThreshold.dragging && mousePos.x <= self.maxThreshold.initA[0] && mousePos.x > self.minThreshold.a[0] )
                 {
                     self.maxThreshold.modifyPosition([mousePos.x, self.maxThreshold.a[1]]);
                 }
                 self.drawThresholdControls();

                 // Don't draw histogram values if the mouse is out of histogram canvas
                 if ( mousePos.y > canvas.height || mousePos.y < 0. || mousePos.x > originX + nbBins || mousePos.x < originX )
                 {
                     return;
                 }

                 // Draw the text indicating the histogram value on mouse position
                 self.ctx.font = '10pt Calibri';
                 self.ctx.fillStyle = '#F0F0F0';
                 self.ctx.shadowColor = '#F3F3F3';
                 self.ctx.shadowBlur = 1;
                 self.ctx.shadowOffsetX = 1;
                 self.ctx.shadowOffsetY = 1;
                 
                 var thresholdValue = self.getHistValue( [mousePos.x, mousePos.y] );
                 self.ctx.fillText(thresholdValue, canvas.width/2-15., originY+paddingBottom);
                 // Draw a tiny line indicating the mouse position on X-axis
                 self.ctx.fillRect( mousePos.x, originY, 1, 2 );
             });
             
             // Handle threshold controller selection
             canvas.addEventListener('mousedown', function(evt) {
                 var mousePos = _getMousePos(canvas, evt);

                 if ( self.minThreshold.contains( [mousePos.x, mousePos.y, 0] ) )
                 {
                     self.minThreshold.dragging = true;
                     self.minThreshold.draw(self.ctx);
                 }

                 if ( self.maxThreshold.contains( [mousePos.x, mousePos.y, 0] ) )
                 {
                     self.maxThreshold.dragging = true;
                     self.maxThreshold.draw(self.ctx);
                 }
             });
             
             // Update histogram on mouseup
             canvas.addEventListener('mouseup', function(evt) {
                 self.minThreshold.dragging = false;
                 self.maxThreshold.dragging = false;

                 if ( self.updateThreshold )
                 {
                     var min = self.getHistValue(self.minThreshold.a);
                     var max = self.getHistValue(self.maxThreshold.a);

                     self.minThreshold.reset();
                     self.maxThreshold.reset();

                     self.updateThreshold(min,max);
                 }


             });

         /**
          *  Get histogram value from the given X-position on canvas
          */
             this.getHistValue = function( position )
         {
             return Math.floor((((position[0]-originX)/256.)*(this.image.tmax-this.image.tmin) + this.image.tmin)*Math.pow(10,this.accuracy))/Math.pow(10, this.accuracy);
         }

         /**
          *  Draw threshold controls(two triangles which represents min/max of current histogram)
          */
         this.drawThresholdControls = function()
         {
             this.minThreshold.draw(this.ctx);
             this.maxThreshold.draw(this.ctx);
         }

         /**
          *  Draw histogram
          */
         this.drawHistogram = function() {
//             this.ctx.fillStyle = "blue";
             this.ctx.fillStyle = "#4151C5";
             for ( var i=0; i<hist.length; i++ )
             {
                 // Scale to y-axis height
                 var rectHeight = (hist[i]/hmax)*originY;
                 this.ctx.fillRect( originX + i, originY, 1, -rectHeight );
             }
         }

         /**
          *  Draw histogram axis
          */
         this.drawAxes = function() {

             var leftY, rightX;
             leftY = 0;
             rightX = originX + hwidth;
             // Draw y axis.
             this.ctx.beginPath();
             this.ctx.moveTo(originX, leftY);
             this.ctx.lineTo(originX, originY);

             // Draw x axis.
             this.ctx.moveTo(originX, originY);
             this.ctx.lineTo(rightX, originY);

             // Define style and stroke lines.
             this.ctx.closePath();
             this.ctx.strokeStyle = "#fff";
             this.ctx.lineWidth = 2;
             this.ctx.stroke();
         }

         /**
          *  Draw transfer function(linear, log, asin, sqrt, sqr)
          */
         this.drawTransferFunction = function()
         {
             // Draw transfer functions
             // "Grey" colormap for now(luminance curve only)
//             this.ctx.fillStyle = "red";
             this.ctx.fillStyle = "#F8AD0D";
             
             for ( var i=0; i<nbBins; i++ )
             {
                 var value = i;
                 var posX = originX + value;

                 var scaledValue;
                 switch (this.image.transferFn) {
                case "linear":
                    scaledValue = (value / nbBins) * originY;
                    break;
                case "log":
                    scaledValue = Math.log(value / 10. + 1) / Math.log(nbBins / 10. + 1) * originY;
                    break;
                case "sqrt":
                    scaledValue = Math.sqrt(value / 10.) / Math.sqrt(nbBins / 10.) * originY;
                    break;
                case "sqr":
                    scaledValue = Math.pow(value, 2) / Math.pow(nbBins, 2) * originY;
                    break;
                case "asin":
                    scaledValue = Math.log(value + Math.sqrt(Math.pow(value, 2) + 1.)) / Math.log(nbBins + Math.sqrt(Math.pow(nbBins, 2) + 1.)) * originY;
                    break;
                default:
                    break;
                }

                 if ( !this.image.inverse )
                 {
                     scaledValue = originY - scaledValue
                 }

                 this.ctx.fillRect( posX, scaledValue, 1, 1);
             }
         }

         /**
          *  Draw the histogram in canvas
          */
         this.draw = function()
         {
             this.ctx.clearRect(0,0, this.ctx.canvas.width, this.ctx.canvas.height);
             this.drawHistogram();
             this.drawTransferFunction();
             this.drawAxes();
             this.drawThresholdControls();
         }

         /**
          *  TODO : create different module
          *  Compute histogram values
          */
         this.compute = function()
         {
             
             var image = this.image;
         //  var image = this.jsFits.image;
             
             // Initialize histogram
             hist = new Array(nbBins);
             for ( var i=0; i<hist.length; i++ )
             {
                 hist[i] = 0;
             }

             // Compute histogram
             hmax = Number.MIN_VALUE;
             for ( var i=0; i<image.pixels.length; i++ )
             {
                 var val = image.pixels[i];
                 
                 // Skip NaN
                 if ( isNaN(val) )
                     continue;
                 // Take only values which belongs to the interval [tmin,tmax]
                 if ( val < image.tmin )
                     continue;
                 if ( val >= image.tmax )
                     continue;

                 // Scale to [0,255]
                 var bin = Math.floor(nbBins * (val - image.tmin)/(image.tmax - image.tmin));
                 hist[bin]++;

                 // Compute histogram max value
                 if ( hist[bin] > hmax )
                 {
                     hmax = hist[bin];
                 }
             }

             // Logarithmic scale for better layout
             for ( var i=0; i<hist.length; i++ )
             {
                 hist[i] = Math.log(1 + hist[i]);
             }
             hmax = Math.log(1 + hmax);
         },
         
         this.updateThreshold = function (min, max)
         {
             this.image.tmin = min;
             this.image.tmax = max;

             this.compute();
             this.draw();
             this.jsFits.update({
                 max : max,
                 min : min
             });
             
             this.viewer.min = min;
             this.viewer.max = max;
             
             this.viewer.thresoldSlider.setValue(0, min, true);
             this.viewer.thresoldSlider.setValue(1, max, true);
             
             this.viewer.canvasPanel.getEl().unmask();
         },

         /**
          *  Set image
          */
         this.setImage = function(image)
         {
             this.image = image;
         }

         return this;

        }
};
