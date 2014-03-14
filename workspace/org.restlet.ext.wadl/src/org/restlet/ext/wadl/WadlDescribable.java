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

import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;

/**
 * Interface that any Restlet can implement in order to provide their own WADL
 * documentation. This is especially useful for subclasses of {@link Directory}
 * or other resource finders when the WADL introspection can't reach
 * {@link ServerResource} or better {@link WadlServerResource} instances.
 * 
 * @author Thierry Boileau
 */
public interface WadlDescribable {

    /**
     * Returns a full documented {@link ResourceInfo} instance.
     * 
     * @param applicationInfo
     *            The parent WADL application descriptor.
     * 
     * @return A full documented {@link ResourceInfo} instance.
     */
    public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo);

}
