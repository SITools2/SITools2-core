/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
function getXPathNodes(document, xpath) {
    var xpathResult = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null);
    var result = [];
    var item = xpathResult.iterateNext();
    while (item != null) {
        result.push(item);
        item = xpathResult.iterateNext();
    }
    return result;
}

function getXPath(targetNode) {
    var useLowerCase = (targetNode.ownerDocument instanceof HTMLDocument);
    var nodePath = getNodePath(targetNode);
    var nodeNames = [];
    for (var i in nodePath) {
        var nodeIndex;
        var node = nodePath[i];
        if (node.nodeType == 1) { // && node.tagName != "TBODY") {
            if (i == 0 && node.hasAttribute("id")) {
                nodeNames.push("/*[@id='" + node.getAttribute("id") + "']");
            } else {
                var tagName = node.tagName;
                if (useLowerCase) {
                    tagName = tagName.toLowerCase();
                }
                nodeIndex = getNodeIndex(node);
                if (nodeIndex != null) {
                    nodeNames.push(tagName + "[" + nodeIndex + "]");
                } else {
                    nodeNames.push(tagName);
                }
            }
        } else if (node.nodeType == 3) {
            nodeIndex = getTextNodeIndex(node);
            if (nodeIndex != null) {
                nodeNames.push("text()[" + nodeIndex + "]");
            } else {
                nodeNames.push("text()");
            }
        }
    }
    return "/" + nodeNames.join("/");

}

function getNodeIndex(node) {
    if (node.nodeType != 1 || node.parentNode == null) return null;
    var list = getChildNodesWithTagName(node.parentNode, node.tagName);
    if (list.length == 1 && list[0] == node) return null;
    for (var i = 0; i < list.length; i++) {
        if (list[i] == node) return i + 1;
    }
    throw "couldn't find node in parent's list: " + node.tagName;
}

function getTextNodeIndex(node) {
    var list = getChildTextNodes(node.parentNode);
    if (list.length == 1 && list[0] == node) return null;
    for (var i = 0; i < list.length; i++) {
        if (list[i] == node) return i + 1;
    }
    throw "couldn't find node in parent's list: " + node.tagName;
}

function getChildNodesWithTagName(parent, tagName) {
    var result = [];
    var child = parent.firstChild;
    while (child != null) {
        if (child.tagName && child.tagName == tagName) {
            result.push(child);
        }
        child = child.nextSibling;
    }
    return result;
}

function getChildTextNodes(parent) {
    var result = [];
    var child = parent.firstChild;
    while (child != null) {
        if (child.nodeType==3) {
            result.push(child);
        }
        child = child.nextSibling;
    }
    return result;
}

function getNodePath(node) {
    var result = [];
    while (node.nodeType == 1 || node.nodeType == 3) {
        result.unshift(node);
        if (node.nodeType == 1 && node.hasAttribute("id")) return result;
        node = node.parentNode;
    }
    return result;
}
