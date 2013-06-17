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

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;

/**
 * Activate or disactivate a Filter
 * 
 * 
 * @author m.gond
 */
public final class FilterActivationResource extends AbstractFilterResource {

  /** filterChained identifier parameter */
  private String filterId = null;
  
  /** The FilterStore */
  private SitoolsStore<FilterChainedModel> store;

  /**
   * Initiate the resource
   */
  public void doInit() {
    super.doInit();
    filterId = (String) this.getRequest().getAttributes().get("filterId");

    store = ((FilterApplication) getApplication()).getStore();

  }

  @Override
  public void sitoolsDescribe() {
    setName("FilterActivationResource");
    setDescription("Resource to modify the status of a Filter.");
  }

  /**
   * Actions on PUT
   * 
   * @param representation
   *          could be null.
   * @param variant
   *          MediaType of response
   * @return Representation response
   */
  @Put
  public Representation validate(Representation representation, Variant variant) {
    Response response = null;
    Representation rep = null;
    try {
      do {

        if (getDatasetId() == null) {
          response = new Response(false, "FILTER_CHAINED_NOT_FOUND");
          break;
        }
        FilterChainedModel filterChainedModel = getStore().retrieve(getDatasetId());

        if (filterChainedModel == null) {
          response = new Response(false, "FILTER_CHAINED_IS_NULL");
          break;
        }

        FilterModel filter = getFilterModel(filterChainedModel, filterId);
        if (filter == null) {
          response = new Response(false, "FILTER_NOT_FOUND");
          break;
        }

        if (this.getReference().toString().endsWith("start")) {
          if ("ACTIVE".equals(filter.getStatus())) {
            response = new Response(true, "filter.update.blocked");
            break;
          }
          else {
            filter.setStatus("ACTIVE");
            getStore().update(filterChainedModel);
            FilterModel filterOut = getFilterModel(filterChainedModel, filterId);

            FilterModelDTO filterModelDTO = getFilterModelDTO(filterOut);
            response = new Response(true, filterModelDTO, FilterModelDTO.class, "filter");
            response.setMessage("filter.start.success");
          }
        }

        if (this.getReference().toString().endsWith("stop")) {
          if ("INACTIVE".equals(filter.getStatus())) {
            response = new Response(true, "filter.stop.blocked");
            break;
          }
          else {
            filter.setStatus("INACTIVE");
            getStore().update(filterChainedModel);
            FilterModel filterOut = getFilterModel(filterChainedModel, filterId);
            FilterModelDTO filterModelDTO = getFilterModelDTO(filterOut);
            response = new Response(true, filterModelDTO, FilterModelDTO.class, "filter");
            response.setMessage("filter.stop.success");
          }

        }
      } while (false);

      // Response
      if (response == null) {
        response = new Response(false, "filter.action.error");
      }
    }
    finally {
      rep = getRepresentation(response, variant);
    }
    return rep;
  }

  /**
   * Describe the PUT method
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment in application
   */
  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Activate the Filter from a FilterChained attached to a Dataset making it available for the Records API.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Disactivate the Filter from a FilterChained attached to a Dataset making it unavailable for DataSetApplication API users.");
    }
    else {
      info.setDocumentation("Method to activate/stop a Filter.");
    }
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<FilterChainedModel> getStore() {
    return store;
  }
}
