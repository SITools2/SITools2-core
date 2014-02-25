/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
function PasswordGenerator() {
    
    
    this.generate=generate;
    
    function generate(string_length){
		var password = "";
        
        var nbEachClasses = Math.floor(string_length / 3);
        
        for (var i = 0; i < nbEachClasses; i++) {
            password += randomNumber();
            password += randomChar(true);
            password += randomChar(false);
        }
        
        for(var i = nbEachClasses*3; i<string_length;i++){
            password += randomChar(false);
        }
        
        return shuffle(password);
	}
    
    
	
    function shuffle (string) {
        var a = string.split(""),
            n = a.length;

        for(var i = n - 1; i > 0; i--) {
            var j = Math.floor(Math.random() * (i + 1));
            var tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a.join("");
    }
    
    function randomNumber() {
        var chars = "0123456789";
        var rnum = Math.floor(Math.random() * chars.length);
        return chars.substring(rnum, rnum + 1);
    }
    
    function randomChar (lowercase) {
        var chars = "ABCDEFGHIJKLMNOPQRSTUVWXTZ";
        var rnum = Math.floor(Math.random() * chars.length);
        var char = chars.substring(rnum, rnum + 1);
        if (lowercase) {
            return char.toLowerCase();
        } else {
            return char;
        }
    }
	
	
	
}