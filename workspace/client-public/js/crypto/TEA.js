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
//
// TEAencrypt: Use Corrected Block TEA to encrypt plaintext using password
//             (note plaintext & password must be strings not string objects)
//
// Return encrypted text as string
//

Ext.namespace('Ext.ux', 'Ext.ux.Crypto');

Ext.ux.Crypto.TEA = function() {
  // escape control chars etc which might cause problems with encrypted texts
  var escCtrlCh = function(str) {
      return str.replace(/[\0\t\n\v\f\r\xa0'"!]/g, function(c) { return '!' + c.charCodeAt(0) + '!'; });
  };
  
  // unescape potentially problematic nulls and control characters
  var unescCtrlCh = function(str) {  // unescape potentially problematic nulls and control characters
      return str.replace(/!\d\d?\d?!/g, function(c) { return String.fromCharCode(c.slice(1,-1)); });
  };
  // convert string to array of longs, each containing 4 chars
  var strToLongs = function(s) {
      // note chars must be within ISO-8859-1 (with Unicode code-point < 256) to fit 4/long
      var l = new Array(Math.ceil(s.length/4));
      for (var i=0; i<l.length; i++) {
          // note little-endian encoding - endianness is irrelevant as long as 
          // it is the same in longsToStr() 
          l[i] = s.charCodeAt(i*4) + (s.charCodeAt(i*4+1)<<8) + 
                 (s.charCodeAt(i*4+2)<<16) + (s.charCodeAt(i*4+3)<<24);
      }
      return l;  // note running off the end of the string generates nulls since 
  }              // bitwise operators treat NaN as 0
  // convert array of longs back to string
  var longsToStr = function(l) {
      var a = new Array(l.length);
      for (var i=0; i<l.length; i++) {
          a[i] = String.fromCharCode(l[i] & 0xFF, l[i]>>>8 & 0xFF, 
                                     l[i]>>>16 & 0xFF, l[i]>>>24 & 0xFF);
      }
      return a.join('');  // use Array.join() rather than repeated string appends for efficiency
  };


  return {
    // TEAencrypt: Use Corrected Block TEA to encrypt plaintext using password
    encrypt : function(plaintext, password) {
        if (plaintext.length == 0) return('');  // nothing to encrypt
        // 'escape' plaintext so chars outside ISO-8859-1 work in single-byte packing, but keep
        // spaces as spaces (not '%20') so encrypted text doesn't grow too long (quick & dirty)
        var asciitext = escape(plaintext).replace(/%20/g,' ');
        var v = strToLongs(asciitext);  // convert string to array of longs
        if (v.length <= 1) v[1] = 0;  // algorithm doesn't work for n<2 so fudge by adding a null
        var k = strToLongs(password.slice(0,16));  // simply convert first 16 chars of password as key
        var n = v.length;
    
        var z = v[n-1], y = v[0], delta = 0x9E3779B9;
        var mx, e, q = Math.floor(6 + 52/n), sum = 0;
    
        while (q-- > 0) {  // 6 + 52/n operations gives between 6 & 32 mixes on each word
            sum += delta;
            e = sum>>>2 & 3;
            for (var p = 0; p < n; p++) {
                y = v[(p+1)%n];
                mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[p&3 ^ e] ^ z);
                z = v[p] += mx;
            }
        }
    
        var ciphertext = longsToStr(v);
    
        return escCtrlCh(ciphertext);
    },
    // TEAdecrypt: Use Corrected Block TEA to decrypt ciphertext using password
    decrypt : function(ciphertext, password) {
        if (ciphertext.length == 0) return('');
        var v = strToLongs(unescCtrlCh(ciphertext));
        var k = strToLongs(password.slice(0,16)); 
        var n = v.length;
    
        var z = v[n-1], y = v[0], delta = 0x9E3779B9;
        var mx, e, q = Math.floor(6 + 52/n), sum = q*delta;
    
        while (sum != 0) {
            e = sum>>>2 & 3;
            for (var p = n-1; p >= 0; p--) {
                z = v[p>0 ? p-1 : n-1];
                mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[p&3 ^ e] ^ z);
                y = v[p] -= mx;
            }
            sum -= delta;
        }
    
        var plaintext = longsToStr(v);
    
        // strip trailing null chars resulting from filling 4-char blocks:
        plaintext = plaintext.replace(/\0+$/,'');
    
        return unescape(plaintext);
    }
  }
}();
