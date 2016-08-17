    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project.services;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.tasks.TaskUtils;

/**
 * Facade for a resource to Perform a multidataset search on a collection of dataset
 * 
 * 
 * @author m.gond
 */
public class ServiceDatasetSearchResourceFacade extends SitoolsParameterizedResource {
  /**
   * Description de la ressource
   */
  @Override
  public void sitoolsDescribe() {
    setName("ServiceDatasetSearchResourceFacade");
    setDescription("Perform a multidataset search on a collection of dataset."
        + "This resource recursively calls all dataset of the collection. For complete parameters list please refer to the dataset /records API");

  }

  /**
   * Description WADL de la methode POST
   * 
   * @param info
   *          The method description to update.
   */
  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to Perform a multidataset search on a collection of dataset.");
    info.setIdentifier("multidataset_search");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    this.addInfo(info);

  }

  /**
   * Create the order
   * 
   * @param represent
   *          the {@link Representation} entity
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  @Post
  public Representation processSearch(Representation represent, Variant variant) {
    return TaskUtils.execute(this, variant);
  }
}
