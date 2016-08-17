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
package fr.cnes.sitools.dataset;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to get the list of services on a Dataset
 * 
 * 
 * @author m.gond
 */
public class ListServicesResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("ListServicesResource");
    setDescription("Gets the list of services for the dataset");
  }

  /**
   * Get the list of services for the dataset
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of resources
   */
  @Get
  public Representation getResourcesList(Variant variant) {

    DataSetApplication application = (DataSetApplication) getApplication();
    SitoolsSettings settings = application.getSettings();
    DataSet dataset = application.getDataSet();

    String url = settings.getString(Consts.APP_DATASETS_URL) + "/" + dataset.getId()
        + settings.getString(Consts.APP_SERVICES_URL);
    ServiceCollectionModel serviceCollection = RIAPUtils.getObject(url, getContext());

    Response response = new Response(true, serviceCollection, ServiceCollectionModel.class, "ServiceCollectionModel");

    return getRepresentation(response, variant);
  }
}
