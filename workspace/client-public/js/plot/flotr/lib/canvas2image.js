/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*
 * Canvas2Image v0.1
 * Copyright (c) 2008 Jacob Seidelin, cupboy@gmail.com
 * MIT License [http://www.opensource.org/licenses/mit-license.php]
 */

var Canvas2Image = (function () {
    // check if we have canvas support
    var oCanvas = document.createElement("canvas");

    // no canvas, bail out.
    if (!oCanvas.getContext) {
        return {
            saveAsBMP : function () {
            },
            saveAsPNG : function () {
            },
            saveAsJPEG : function () {
            }
        };
    }

    var bHasImageData = !!(oCanvas.getContext("2d").getImageData);
    var bHasDataURL = !!(oCanvas.toDataURL);
    var bHasBase64 = !!(window.btoa);

    var strDownloadMime = "image/octet-stream";

    // ok, we're good
    var readCanvasData = function (oCanvas) {
        var iWidth = parseInt(oCanvas.width);
        var iHeight = parseInt(oCanvas.height);
        return oCanvas.getContext("2d").getImageData(0, 0, iWidth, iHeight);
    };

    // base64 encodes either a string or an array of charcodes
    var encodeData = function (data) {
        var strData = "";
        if (typeof data == "string") {
            strData = data;
        } else {
            var aData = data;
            for ( var i = 0; i < aData.length; i++) {
                strData += String.fromCharCode(aData[i]);
            }
        }
        return btoa(strData);
    };

    // creates a base64 encoded string containing BMP data
    // takes an imagedata object as argument
    var createBMP = function (oData) {
        var aHeader = [];

        var iWidth = oData.width;
        var iHeight = oData.height;

        aHeader.push(0x42); // magic 1
        aHeader.push(0x4D);

        var iFileSize = iWidth * iHeight * 3 + 54; // total header size = 54
                                                    // bytes
        aHeader.push(iFileSize % 256);
        iFileSize = Math.floor(iFileSize / 256);
        aHeader.push(iFileSize % 256);
        iFileSize = Math.floor(iFileSize / 256);
        aHeader.push(iFileSize % 256);
        iFileSize = Math.floor(iFileSize / 256);
        aHeader.push(iFileSize % 256);

        aHeader.push(0); // reserved
        aHeader.push(0);
        aHeader.push(0); // reserved
        aHeader.push(0);

        aHeader.push(54); // data offset
        aHeader.push(0);
        aHeader.push(0);
        aHeader.push(0);

        var aInfoHeader = [];
        aInfoHeader.push(40); // info header size
        aInfoHeader.push(0);
        aInfoHeader.push(0);
        aInfoHeader.push(0);

        var iImageWidth = iWidth;
        aInfoHeader.push(iImageWidth % 256);
        iImageWidth = Math.floor(iImageWidth / 256);
        aInfoHeader.push(iImageWidth % 256);
        iImageWidth = Math.floor(iImageWidth / 256);
        aInfoHeader.push(iImageWidth % 256);
        iImageWidth = Math.floor(iImageWidth / 256);
        aInfoHeader.push(iImageWidth % 256);

        var iImageHeight = iHeight;
        aInfoHeader.push(iImageHeight % 256);
        iImageHeight = Math.floor(iImageHeight / 256);
        aInfoHeader.push(iImageHeight % 256);
        iImageHeight = Math.floor(iImageHeight / 256);
        aInfoHeader.push(iImageHeight % 256);
        iImageHeight = Math.floor(iImageHeight / 256);
        aInfoHeader.push(iImageHeight % 256);

        aInfoHeader.push(1); // num of planes
        aInfoHeader.push(0);

        aInfoHeader.push(24); // num of bits per pixel
        aInfoHeader.push(0);

        aInfoHeader.push(0); // compression = none
        aInfoHeader.push(0);
        aInfoHeader.push(0);
        aInfoHeader.push(0);

        var iDataSize = iWidth * iHeight * 3;
        aInfoHeader.push(iDataSize % 256);
        iDataSize = Math.floor(iDataSize / 256);
        aInfoHeader.push(iDataSize % 256);
        iDataSize = Math.floor(iDataSize / 256);
        aInfoHeader.push(iDataSize % 256);
        iDataSize = Math.floor(iDataSize / 256);
        aInfoHeader.push(iDataSize % 256);

        for ( var i = 0; i < 16; i++) {
            aInfoHeader.push(0); // these bytes not used
        }

        var iPadding = (4 - ((iWidth * 3) % 4)) % 4;

        var aImgData = oData.data;

        var strPixelData = "";
        var y = iHeight;
        do {
            var iOffsetY = iWidth * (y - 1) * 4;
            var strPixelRow = "";
            for ( var x = 0; x < iWidth; x++) {
                var iOffsetX = 4 * x;

                strPixelRow += String.fromCharCode(aImgData[iOffsetY + iOffsetX + 2]);
                strPixelRow += String.fromCharCode(aImgData[iOffsetY + iOffsetX + 1]);
                strPixelRow += String.fromCharCode(aImgData[iOffsetY + iOffsetX]);
            }
            for ( var c = 0; c < iPadding; c++) {
                strPixelRow += String.fromCharCode(0);
            }
            strPixelData += strPixelRow;
        } while (--y);

        return encodeData(aHeader.concat(aInfoHeader)) + encodeData(strPixelData);
    };

    // sends the generated file to the client
    var saveFile = function (strData, fileName) {
        if (!window.open(strData)) {
            document.location.href = strData;
        }
    };


    var makeDataURI = function (strData, strMime) {
        return "data:" + strMime + ";base64," + strData;
    };

    // generates a <img> object containing the imagedata
    var makeImageObject = function (strSource) {
        var oImgElement = document.createElement("img");
        oImgElement.src = strSource;
        return oImgElement;
    };

    var scaleCanvas = function (oCanvas, iWidth, iHeight) {
        if (iWidth && iHeight) {
            var oSaveCanvas = document.createElement("canvas");

            oSaveCanvas.width = iWidth;
            oSaveCanvas.height = iHeight;
            oSaveCanvas.style.width = iWidth + "px";
            oSaveCanvas.style.height = iHeight + "px";

            var oSaveCtx = oSaveCanvas.getContext("2d");

            oSaveCtx.drawImage(oCanvas, 0, 0, oCanvas.width, oCanvas.height, 0, 0, iWidth, iWidth);

            return oSaveCanvas;
        }
        return oCanvas;
    };

    return {
        saveAsPNG : function (oCanvas, fileName, bReturnImg, iWidth, iHeight) {
            if (!bHasDataURL) {
                return false;
            }
            var oScaledCanvas = scaleCanvas(oCanvas, iWidth, iHeight);
            var strData = oScaledCanvas.toDataURL("image/png");
            if (bReturnImg) {
                return makeImageObject(strData);
            } else {
//                saveFile(strData.replace("image/png", strDownloadMime));
                // uses the HTML5 fileSaver API
                oScaledCanvas.toBlob(function(blob) {
					saveAs(
						  blob
						, fileName + ".png"
					);
				}, "image/png");   
            }
            return true;
        },

        saveAsJPEG : function (oCanvas, bReturnImg, iWidth, iHeight) {
            if (!bHasDataURL) {
                return false;
            }

            var oScaledCanvas = scaleCanvas(oCanvas, iWidth, iHeight);
            var strMime = "image/jpeg";
            var strData = oScaledCanvas.toDataURL(strMime);

            // check if browser actually supports jpeg by looking for the mime
            // type in the data uri.
            // if not, return false
            if (strData.indexOf(strMime) != 5) {
                return false;
            }

            if (bReturnImg) {
                return makeImageObject(strData);
            } else {
                saveFile(strData.replace(strMime, strDownloadMime));
            }
            return true;
        },

        saveAsBMP : function (oCanvas, bReturnImg, iWidth, iHeight) {
            if (!(bHasImageData && bHasBase64)) {
                return false;
            }

            var oScaledCanvas = scaleCanvas(oCanvas, iWidth, iHeight);

            var oData = readCanvasData(oScaledCanvas);
            var strImgData = createBMP(oData);
            if (bReturnImg) {
                return makeImageObject(makeDataURI(strImgData, "image/bmp"));
            } else {
                saveFile(makeDataURI(strImgData, strDownloadMime));
            }
            return true;
        }
    };

})();
