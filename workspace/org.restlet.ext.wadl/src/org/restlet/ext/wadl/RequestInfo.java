 /*******************************************************************************
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
 ******************************************************************************/
package org.restlet.ext.wadl;

import static org.restlet.ext.wadl.WadlRepresentation.APP_NAMESPACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.restlet.ext.xml.XmlWriter;
import org.xml.sax.SAXException;

/**
 * Describes the properties of a request associated to a parent method.
 * 
 * @author Jerome Louvel
 */
public class RequestInfo extends DocumentedInfo {

    /** List of parameters. */
    private List<ParameterInfo> parameters;

    /** List of supported input representations. */
    private List<RepresentationInfo> representations;

    /**
     * Constructor.
     */
    public RequestInfo() {
        super();
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param documentation
     *            A single documentation element.
     */
    public RequestInfo(DocumentationInfo documentation) {
        super(documentation);
    }

    /**
     * Constructor with a list of documentation elements.
     * 
     * @param documentations
     *            The list of documentation elements.
     */
    public RequestInfo(List<DocumentationInfo> documentations) {
        super(documentations);
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param documentation
     *            A single documentation element.
     */
    public RequestInfo(String documentation) {
        super(documentation);
    }

    /**
     * Returns the list of parameters.
     * 
     * @return The list of parameters.
     */
    public List<ParameterInfo> getParameters() {
        // Lazy initialization with double-check.
        List<ParameterInfo> p = this.parameters;
        if (p == null) {
            synchronized (this) {
                p = this.parameters;
                if (p == null) {
                    this.parameters = p = new ArrayList<ParameterInfo>();
                }
            }
        }
        return p;
    }

    /**
     * Returns the list of supported input representations.
     * 
     * @return The list of supported input representations.
     */
    public List<RepresentationInfo> getRepresentations() {
        // Lazy initialization with double-check.
        List<RepresentationInfo> r = this.representations;
        if (r == null) {
            synchronized (this) {
                r = this.representations;
                if (r == null) {
                    this.representations = r = new ArrayList<RepresentationInfo>();
                }
            }
        }
        return r;
    }

    /**
     * Sets the list of parameters.
     * 
     * @param parameters
     *            The list of parameters.
     */
    public void setParameters(List<ParameterInfo> parameters) {
        this.parameters = parameters;
    }

    /**
     * Sets the list of supported input representations.
     * 
     * @param representations
     *            The list of supported input representations.
     */
    public void setRepresentations(List<RepresentationInfo> representations) {
        this.representations = representations;
    }

    @Override
    public void updateNamespaces(Map<String, String> namespaces) {
        namespaces.putAll(resolveNamespaces());

        for (final ParameterInfo parameterInfo : getParameters()) {
            parameterInfo.updateNamespaces(namespaces);
        }
        for (final RepresentationInfo representationInfo : getRepresentations()) {
            representationInfo.updateNamespaces(namespaces);
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
        if (getDocumentations().isEmpty() && getParameters().isEmpty()
                && getRepresentations().isEmpty()) {
            writer.emptyElement(APP_NAMESPACE, "request");
        } else {
            writer.startElement(APP_NAMESPACE, "request");

            for (final DocumentationInfo documentationInfo : getDocumentations()) {
                documentationInfo.writeElement(writer);
            }

            for (final ParameterInfo parameterInfo : getParameters()) {
                parameterInfo.writeElement(writer);
            }

            for (final RepresentationInfo representationInfo : getRepresentations()) {
                representationInfo.writeElement(writer);
            }

            writer.endElement(APP_NAMESPACE, "request");
        }
    }

}
