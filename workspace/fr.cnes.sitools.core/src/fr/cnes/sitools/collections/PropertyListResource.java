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
package fr.cnes.sitools.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.properties.model.SitoolsProperty;

/**
 * Retrieves the list of all properties for a collection of datasets
 * 
 * 
 * @author m.gond
 */
public class PropertyListResource extends AbstractCollectionsResource {

  @Override
  public void sitoolsDescribe() {
    setName("CommonConceptsResource");
    setDescription("Retrieves the list of all properties for a collection of datasets");
  }

  /**
   * Retrieve common concepts
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrievePropertyList(Variant variant) {
    Response response = null;
    Collection collection = getStore().retrieve(getCollectionId());
    List<SitoolsProperty> propReturn = null;
    if (collection != null) {
      Map<String, SitoolsProperty> properties = new HashMap<String, SitoolsProperty>();

      List<Resource> datasets = collection.getDataSets();
      for (Resource datasetRes : datasets) {
        DataSet dataset = getDataset(datasetRes.getId());
        if (dataset != null) {
          List<SitoolsProperty> dsPropList = dataset.getProperties();
          if (dsPropList != null) {
            for (SitoolsProperty sitoolsProperty : dsPropList) {
              properties.put(sitoolsProperty.getName(), sitoolsProperty);
            }
          }
        }
      }
      propReturn = new ArrayList<SitoolsProperty>(properties.values());
      response = new Response(true, propReturn, SitoolsProperty.class);
    }
    else {
      response = new Response(false, "Collection not found");
    }

    return getRepresentation(response, variant);
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of properties on a collection of Datasets");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }
}
