 /*******************************************************************************
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
 ******************************************************************************/
package org.restlet.ext.wadl;

import static org.restlet.ext.wadl.WadlRepresentation.APP_NAMESPACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.restlet.ext.xml.XmlWriter;
import org.xml.sax.SAXException;

/**
 * Describes the grammars used by representation descriptions. This is
 * especially useful to formally describe XML representations using XML Schema
 * or Relax NG standards.
 * 
 * @author Jerome Louvel
 */
public class GrammarsInfo extends DocumentedInfo {

    /** Definitions of data format descriptions to be included by reference. */
    private List<IncludeInfo> includes;

    /**
     * Constructor.
     */
    public GrammarsInfo() {
        super();
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param documentation
     *            A single documentation element.
     */
    public GrammarsInfo(DocumentationInfo documentation) {
        super(documentation);
    }

    /**
     * Constructor with a list of documentation elements.
     * 
     * @param documentations
     *            The list of documentation elements.
     */
    public GrammarsInfo(List<DocumentationInfo> documentations) {
        super(documentations);
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param documentation
     *            A single documentation element.
     */
    public GrammarsInfo(String documentation) {
        super(documentation);
    }

    /**
     * Returns the list of include elements.
     * 
     * @return The list of include elements.
     */
    public List<IncludeInfo> getIncludes() {
        // Lazy initialization with double-check.
        List<IncludeInfo> i = this.includes;
        if (i == null) {
            synchronized (this) {
                i = this.includes;
                if (i == null) {
                    this.includes = i = new ArrayList<IncludeInfo>();
                }
            }
        }
        return i;
    }

    /**
     * Sets the list of include elements.
     * 
     * @param includes
     *            The list of include elements.
     */
    public void setIncludes(List<IncludeInfo> includes) {
        this.includes = includes;
    }

    @Override
    public void updateNamespaces(Map<String, String> namespaces) {
        namespaces.putAll(resolveNamespaces());

        for (final IncludeInfo includeInfo : getIncludes()) {
            includeInfo.updateNamespaces(namespaces);
        }
    }

    /**
     * Writes the current object as an XML element using the given SAX writer.
     * 
     * @param writer
     *            The SAX writer.
     * @throws SAXException
     */
    public void writeElement(XmlWriter writer) throws SAXException {

        if (getDocumentations().isEmpty() && getIncludes().isEmpty()) {
            writer.emptyElement(APP_NAMESPACE, "grammars");
        } else {
            writer.startElement(APP_NAMESPACE, "grammars");

            for (final DocumentationInfo documentationInfo : getDocumentations()) {
                documentationInfo.writeElement(writer);
            }

            for (final IncludeInfo includeInfo : getIncludes()) {
                includeInfo.writeElement(writer);
            }

            writer.endElement(APP_NAMESPACE, "grammars");
        }
    }

}
