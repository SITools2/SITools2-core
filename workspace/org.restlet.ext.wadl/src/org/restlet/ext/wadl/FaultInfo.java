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

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Status;

/**
 * Describes an error condition for response descriptions.
 * 
 * @author Jerome Louvel
 * @deprecated This element has been removed from the WADL specification.
 */
@Deprecated
public class FaultInfo extends RepresentationInfo {

    /**
     * Constructor.
     * 
     * @param status
     *            The associated status code.
     */
    public FaultInfo(Status status) {
        super();
        getStatuses().add(status);
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param status
     *            The associated status code.
     * @param documentation
     *            A single documentation element.
     */
    public FaultInfo(Status status, DocumentationInfo documentation) {
        super(documentation);
        getStatuses().add(status);
    }

    /**
     * Constructor with a list of documentation elements.
     * 
     * @param status
     *            The associated status code.
     * @param documentations
     *            The list of documentation elements.
     */
    public FaultInfo(Status status, List<DocumentationInfo> documentations) {
        super(documentations);
        getStatuses().add(status);
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param status
     *            The associated status code.
     * @param documentation
     *            A single documentation element.
     */
    public FaultInfo(Status status, String documentation) {
        this(status, new DocumentationInfo(documentation));
    }

    /**
     * Constructor with a single documentation element.
     * 
     * @param status
     *            The associated status code.
     * @param mediaType
     *            The fault representation's media type.
     * @param documentation
     *            A single documentation element.
     */
    public FaultInfo(Status status, MediaType mediaType, String documentation) {
        this(status, new DocumentationInfo(documentation));
        setMediaType(mediaType);
    }
}
