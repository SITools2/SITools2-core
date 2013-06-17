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
package fr.cnes.sitools.dataset.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.business.FilterChained;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Low coupling between DateSetApplication and Query Filters
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ClientFilter {

  /**
   * Private constructor for utility class
   */
  private ClientFilter() {
    super();
  }

  /**
   * Gets the FilterChained object instance for that DataSet
   * 
   * @param datasetId
   *          the datasetId
   * @param context
   *          the RESTlet context
   * @return the FilterChained object instance for that DataSet
   */
  public static FilterChainedModel getFilterChainedModel(Context context, String datasetId) {
    DataSetApplication datasetApp = (DataSetApplication) context.getAttributes().get("DataSetApplication");
    SitoolsSettings settings = datasetApp.getSettings();

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_DATASETS_URL)
        + "/" + datasetId + settings.getString(Consts.APP_DATASETS_FILTERS_URL));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();

    objectMediaType.add(new Preference<MediaType>(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL));

    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      throw new ResourceException(response.getStatus(), response.getEntityAsText());
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      FilterChainedModel filterChainedModel = (FilterChainedModel) resp.getItem();
      if (filterChainedModel != null && filterChainedModel.getFilters() != null) {
        // get only the activated ones
        List<FilterModel> filters = filterChainedModel.getFilters();
        for (Iterator<FilterModel> iterator = filters.iterator(); iterator.hasNext();) {
          FilterModel filterModel = iterator.next();
          if (!"ACTIVE".equals(filterModel.getStatus())) {
            iterator.remove();
          }
        }
      }
      return filterChainedModel;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Get converterChained object instance
   * 
   * @param context
   *          the RESTlet context
   * @param filterChainedModel
   *          the model
   * @return a converterChained object with the parameterized converters
   */
  public static FilterChained getFilterChained(Context context, FilterChainedModel filterChainedModel) {
    // TODO Auto-generated method stub

    FilterChained filter = new FilterChained();
    filter.setName(filterChainedModel.getName());
    filter.setDescription(filterChainedModel.getDescription());
    filter.setContext(context);

    if (filterChainedModel.getFilters() != null) {
      FilterModel convModel;
      AbstractFilter convImpl;
      try {
        for (Iterator<FilterModel> it = filterChainedModel.getFilters().iterator(); it.hasNext();) {
          convModel = it.next();

          @SuppressWarnings("unchecked")
          Class<AbstractFilter> classImpl = (Class<AbstractFilter>) Class.forName(convModel.getClassName());
          convImpl = classImpl.newInstance();
          convImpl.setParametersMap(convModel.getParametersMap());

          convImpl.setContext(context);
          filter.addFilter(convImpl);
        }

      }
      catch (ClassNotFoundException e) {
        Logger.getLogger(ClientFilter.class.getName()).severe(e.getMessage());
      }
      catch (InstantiationException e) {
        Logger.getLogger(ClientFilter.class.getName()).severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        Logger.getLogger(ClientFilter.class.getName()).severe(e.getMessage());
      }
    }

    return filter;
  }

}
