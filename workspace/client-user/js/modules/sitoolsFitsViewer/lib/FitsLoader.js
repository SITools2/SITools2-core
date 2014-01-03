/*******************************************************************************
 * Copyright 2012, 2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

function FitsLoader() {

	/**
	 * Parse fits file
	 * 
	 * @param response
	 *            XHR response containing fits
	 * 
	 * @return Parsed data
	 */
	function parseFits(response) {
		var FITS = astro.ASTROFITS;
		// Initialize the FITS.File object using
		// the array buffer returned from the XHR
		var fits = new FITS.File(response);
		// Grab the first HDU with a data unit
		var hdu = fits.getHDU();
		var data = hdu.data;

		var uintPixels;
		var swapPixels = new Uint8Array(data.view.buffer, data.begin,
				data.length); // with gl.UNSIGNED_byte

		var bpe = data.arrayType.BYTES_PER_ELEMENT;
		for (var i = 0; i < swapPixels.length; i += bpe) {
			var temp;
			// Swap to little-endian
			for (var j = 0; j < bpe / 2; j++) {
				temp = swapPixels[i + j];
				swapPixels[i + j] = swapPixels[i + bpe - 1 - j];
				swapPixels[i + bpe - 1 - j] = temp;
			}
		}

		return fits;
	};

	var loadFits = function(url, successCallback, failCallback,
			onprogressCallback) {
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function(e) {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					if (xhr.response) {
						var fits = parseFits(xhr.response);
						if (successCallback) {
							successCallback(fits);
						}
					}
				} else {
					console.log("Error while loading " + url);
					if (failCallback) {
						failCallback(xhr);
					}
				}
			}
		};

		xhr.onprogress = onprogressCallback;

		xhr.open("GET", url);
		xhr.responseType = 'arraybuffer';
		xhr.send();
		return xhr;
	};

	return {
		loadFits : loadFits,
		parseFits : parseFits
	};

};