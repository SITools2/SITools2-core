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

import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.util.WrapperRestlet;

/**
 * WADL wrapper for {@link Restlet} instances. Useful if you need to provide the
 * WADL documentation for instances of classes such as {@link Directory}.
 * 
 * @author Thierry Boileau
 */
public abstract class WadlWrapper extends WrapperRestlet implements
        WadlDescribable {

    /** The description of the wrapped Restlet. */
    private ResourceInfo resourceInfo;

    /**
     * Constructor.
     * 
     * @param wrappedRestlet
     *            The Restlet to wrap.
     */
    public WadlWrapper(Restlet wrappedRestlet) {
        super(wrappedRestlet);
    }

    /**
     * Returns the description of the wrapped Restlet.
     * 
     * @return The ResourceInfo object of the wrapped Restlet.
     */
    public ResourceInfo getResourceInfo() {
        return this.resourceInfo;
    }

    /**
     * Sets the description of the wrapped Restlet.
     * 
     * @param resourceInfo
     *            The ResourceInfo object of the wrapped Restlet.
     */
    public void setResourceInfo(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

}
