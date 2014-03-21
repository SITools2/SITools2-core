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
package fr.cnes.sitools.common.resource;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ExtendedWadlServerResource;
import org.restlet.representation.Variant;

import fr.cnes.sitools.common.SitoolsMediaType;

/**
 * Base class for SITools resources
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractSitoolsResource extends ExtendedWadlServerResource {

  /**
   * Sets the name with the canonical name of the class.
   */
  public AbstractSitoolsResource() {
    super();
    setName(this.getClass().getCanonicalName());
    sitoolsDescribe();
  }

  /**
   * Method implementation mandatory
   */
  public abstract void sitoolsDescribe();

  /**
   * Gets the mediaType of the request
   * 
   * @param variant
   *          the variant of the request
   * @return the MediaType of the request
   */
  public MediaType getMediaType(Variant variant) {
    MediaType defaultMediaType = null;
    if (variant == null) {
      if (this.getRequest().getClientInfo().getAcceptedMediaTypes().size() > 0) {
        MediaType first = this.getRequest().getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        if (first.isConcrete()
            && ((first.isCompatible(MediaType.APPLICATION_JAVA_OBJECT) || first
                .isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)))) {
          defaultMediaType = first;
        }
      }
      // negociation de contenu (@see classe ServerResource.doNegotiatedHandle)
      if ((defaultMediaType == null) && (getVariants() != null) && (!getVariants().isEmpty())) {
        Variant preferredVariant = getClientInfo().getPreferredVariant(getVariants(), getMetadataService());
        defaultMediaType = preferredVariant.getMediaType();
      }
    }
    else {
      defaultMediaType = variant.getMediaType();
    }
    return defaultMediaType;
  }
}
