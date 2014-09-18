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
package fr.cnes.sitools.dataset.filter;

import java.util.Set;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;

/**
 * Resource for a filter
 * 
 * @author AKKA
 * 
 */
public final class FilterResource extends AbstractFilterResource {
  /** The FilterStore */
  private FilterStoreInterface store;

  /** Filter identifier parameter */
  private String filterId = null;

  @Override
  public void sitoolsDescribe() {
    setName("FilterResource");
    setDescription("Create, read, update and delete filter chains for a dataset.");
  }

  @Override
  protected void doInit() {
    super.doInit();

    filterId = (String) this.getRequest().getAttributes().get("filterId");
    store = ((FilterApplication) getApplication()).getStore();
  }

  /**
   * Get the description of a filter in the filterChained list
   * 
   * @param variant
   *          the variant needed
   * @return the filter object
   */
  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = null;
    if (getDatasetId() != null) {
      FilterChainedModel filterChainedModel = getStore().retrieve(getDatasetId());
      if (filterChainedModel != null) {
        FilterModel filter = getFilterModel(filterChainedModel, filterId);
        if (filter != null) {
          FilterModelDTO filterOutDTO = getFilterModelDTO(filter);
          response = new Response(true, filterOutDTO, FilterModelDTO.class, "filter");
        }
        else {
          // filterChainedModel does not exists
          response = new Response(false, "filter.dontExists");
        }
      }
      else {
        // filterChainedModel null
        response = new Response(false, "NOT FOUND");
      }
    }
    else {
      // filterChainedModel null
      response = new Response(false, "NOT FOUND");
    }
    return getRepresentation(response, variant);
  }

  /**
   * Describe the Get command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeGet(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to retrieve a filter in a chain of filters");
    info.setIdentifier("retrieve_filter");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("filterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the filter");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);
  }

  /**
   * Modify an existing filter in the filterChained list
   * 
   * @param representation
   *          the filter representation
   * @param variant
   *          the variant needed
   * @return the modified filter
   */
  @Put
  @Override
  public Representation put(Representation representation, Variant variant) {
    Response response = null;

    try {
      FilterModelDTO filterInputDTO = getObject(representation);

      FilterModel filterInput = getFilterModelFromDTO(filterInputDTO);

      if (getDatasetId() != null) {

        FilterChainedModel filterChainedModel = getStore().retrieve(getDatasetId());
        if (filterChainedModel != null) {
          FilterModel filter = getFilterModel(filterChainedModel, filterId);
          if (filter != null) {
            updateFilter(filter, filterInput);

            if (filterInput.getClassName() == null || filterInput.getClassName().equals("")) {
              filterInput.setClassName(filter.getClassName());
            }

            // VALIDATION PART
            Set<ConstraintViolation> constraints = checkValidity(filter);
            if (constraints != null && !constraints.isEmpty()) {
              ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
              array = constraints.toArray(array);

              response = new Response(false, array, ConstraintViolation.class, "constraints");
              return getRepresentation(response, variant);
            }
            // END OF THE VALIDATION PART

            getStore().update(filterChainedModel);

            FilterModel filterOut = getFilterModel(filterChainedModel, filterId);

            FilterModelDTO filterModelDTO = getFilterModelDTO(filterOut);
            response = new Response(true, filterModelDTO, FilterModelDTO.class, "filter");
            
            trace(Level.INFO, "Update the filter " + filterModelDTO.getName() + " for the dataset - id : " + getDatasetId());
            
          }
          else {
            // filterChainedModel does not exists
            response = new Response(false, "filter.dontExists");
            trace(Level.INFO, "Cannot update the filter for the dataset - id : " + getDatasetId());
          }
        }
        else {
          // filterChainedModel null
          response = new Response(false, "NOT FOUND");
          trace(Level.INFO, "Cannot update the filter for the dataset - id : " + getDatasetId());
        }
      }
      else {
        // filterChainedModel null
        response = new Response(false, "NOT FOUND");
        trace(Level.INFO, "Cannot update the filter for the dataset - id : " + getDatasetId());
      }
    }

    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update the filter for the dataset - id : " + getDatasetId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update the filter for the dataset - id : " + getDatasetId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

    return getRepresentation(response, variant);
  }

  /**
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to modify a filter in the chain of filter");
    info.setIdentifier("update_filter");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("filterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the filter");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);
    // Response 500

    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);
  }

  /**
   * Update dest FilterModel objet with input FilterModel's attributes
   * 
   * @param dest
   *          the FilterModel to update
   * @param input
   *          the source of the update
   */
  private void updateFilter(FilterModel dest, FilterModel input) {
    dest.setDescriptionAction(input.getDescriptionAction());
    dest.setParametersMap(input.getParametersMap());
  }

  /**
   * Delete an existing filter in the filterChained list
   * 
   * @param variant
   *          the variant needed
   * @return a representation telling whether it is successful or not
   */
  @Delete
  @Override
  protected Representation delete(Variant variant) {

    FilterChainedModel filterChained = getStore().retrieve(getDatasetId());
    Response response = null;
    if (filterChained != null) {
      FilterModel filter = getFilterModel(filterChained, filterId);
      if (filter != null) {
        boolean success = filterChained.getFilters().remove(filter);
        getStore().update(filterChained);
        if (success) {
          response = new Response(true, "filter.deleted.success");
          trace(Level.INFO, "Delete the filter " + filter.getName() + " for the dataset - id : " + getDatasetId());
        }
        else {
          response = new Response(false, "filter.deleted.failure");
          trace(Level.INFO, "Cannot delete the filter " + filter.getName() + " for the dataset - id : " + getDatasetId());
        }
      }
      else {
        response = new Response(false, "filter.deleted.failure.notfound");
        trace(Level.INFO, "Cannot delete the filter for the dataset - id : " + getDatasetId());
      }

    }
    else {
      response = new Response(false, "filter.deleted.failure.filterChained.notfound");
      trace(Level.INFO, "Cannot delete the filter for the dataset - id : " + getDatasetId());
    }
    return getRepresentation(response, variant);

  }

  /**
   * Describe the Delete command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("This method delete a filter  in the chain of filter");
    info.setIdentifier("delete_filter");

    this.addStandardGetRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("filterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the filter");
    info.getRequest().getParameters().add(pic);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public FilterStoreInterface getStore() {
    return store;
  }

}
