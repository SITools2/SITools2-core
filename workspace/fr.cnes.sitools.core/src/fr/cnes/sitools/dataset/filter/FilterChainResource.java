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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedModelDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedOrderDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;

/**
 * Resource for a filter
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class FilterChainResource extends AbstractFilterResource {
  /** The FilterStore */
  private SitoolsStore<FilterChainedModel> store;

  /**
   * Initiate the resource
   */
  public void doInit() {
    super.doInit();
    getVariants().add(new Variant(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL));

    store = ((FilterApplication) getApplication()).getStore();
  }

  @Override
  public void sitoolsDescribe() {
    setName("FilterChainResource");
    setDescription("Resource for managing an ordered chain of filters defined on a dataset.");
    setNegotiated(false);
  }

  /**
   * Create / attach Filter
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newFilter(Representation representation, Variant variant) {
    Response response = null;
    try {
      FilterModelDTO filterInputDTO = getObject(representation);

      FilterModel filterInput = getFilterModelFromDTO(filterInputDTO);

      // // VALIDATION PART
      Set<ConstraintViolation> constraints = checkValidity(filterInput);
      if (constraints != null) {
        ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
        array = constraints.toArray(array);

        response = new Response(false, array, ConstraintViolation.class, "constraints");
        return getRepresentation(response, variant);
      }

      // END OF THE VALIDATION PART

      // get the filterChained
      FilterChainedModel filterList = getStore().retrieve(getDatasetId());
      if (filterList == null) {
        filterList = new FilterChainedModel();
        filterList.setId(getDatasetId());
        // Business service
        filterList.setParent(getDatasetId());
        filterList = getStore().create(filterList);
      }

      if (filterInput.getId() == null || filterInput.getId().equals("")) {
        filterInput.setId(UUID.randomUUID().toString());
      }

      // activate the filter by default
      filterInput.setStatus("ACTIVE");

      filterList.getFilters().add(filterInput);

      FilterChainedModel filterChained = getStore().update(filterList);
      FilterModel filterOut = getFilterModel(filterChained, filterInput.getId());
      // Response

      // register observer
      registerObserver(filterChained);
      FilterModelDTO filterModelDTO = getFilterModelDTO(filterOut);
      response = new Response(true, filterModelDTO, FilterModelDTO.class, "filter");
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

  }

  /**
   * Describe the POST command
   * 
   * @param info
   *          the info sent
   * @param path
   *          url attachment of the resource
   */
  @Override
  public void describePost(MethodInfo info, String path) {
    if (path.endsWith("{filterChainedId}")) {
      info.setDocumentation("POST " + path + " : Creates a new single filter object attached to a dataset");
    }
    else if (path.equals("")) {
      info.setDocumentation("POST " + path + " : Create a new single filter object in the chain.");
    }

    // Method
    // info.setDocumentation("This method permits to create a filter attached to a dataset");
    info.setIdentifier("create_filter");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);

    // Response 500
    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);

  }

  /**
   * Gets the FilterChainedModel object if the call is from RIAP or the FilterChainedModelDTO if the call is from
   * another client
   * 
   * @param variant
   *          The {@link Variant} needed
   * 
   * @return the FilterChainedModel object if the client asked for MediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL or
   *         the FilterChainedModelDTO otherwise
   */
  @Get
  @Override
  public Representation get(Variant variant) {
    FilterChainedModel filterChModel = getStore().retrieve(getDatasetId());
    Response response;
    // Return the Model if it has been specifically asked, return the DTO otherwise
    MediaType mediaType = getMediaType(variant);
    if (mediaType.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      response = new Response(true, filterChModel, FilterChainedModel.class, "filterChainedModel");
    }
    else {
      FilterChainedModelDTO filterChainedOutDTO = getFilterChainedModelDTO(filterChModel);
      addCurrentClassDescription(filterChainedOutDTO);
      response = new Response(true, filterChainedOutDTO, FilterChainedModelDTO.class, "filterChainedModel");
    }

    return getRepresentation(response, variant);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.ext.wadl.ExtendedWadlServerResource#describe(org.restlet.ext.wadl.ApplicationInfo,
   * java.lang.String)
   */
  @Override
  protected void describe(ApplicationInfo applicationInfo, String path) {
    // TODO Auto-generated method stub
    // super.describe(applicationInfo, path);
    if (path.endsWith("{filterChainedId}")) {
      applicationInfo.setDocumentation("Resource on a single filter object");
    }
    else if (path.equals("")) {
      applicationInfo.setDocumentation("Resource on the filter collection");
    }
  }

  /**
   * Describe the Get command
   * 
   * @param info
   *          the info sent
   * @param path
   *          url attachment of the resource
   */
  @Override
  public void describeGet(MethodInfo info, String path) {
    // Method
    info.setDocumentation("This method permits to retrieve a chain of filters");
    info.setIdentifier("retrieve_filterschained");

    // Request
    this.addStandardGetRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);
  }

  /**
   * Update / Validate existing Filter
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateFilterChainedModel(Representation representation, Variant variant) {
    FilterChainedModel filterOutput = null;
    Response response = null;
    try {
      FilterChainedOrderDTO orderDTO = null;
      if (representation != null) {
        if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
          // Parse the XML representation to get the filterChainedOrderDTO bean
          orderDTO = new XstreamRepresentation<FilterChainedOrderDTO>(representation).getObject();
        }
        else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
          // Parse the JSON representation to get the bean
          orderDTO = new JacksonRepresentation<FilterChainedOrderDTO>(representation, FilterChainedOrderDTO.class)
              .getObject();
        }

        filterOutput = getStore().retrieve(getDatasetId());
        if (filterOutput.getId().equals(orderDTO.getId())) {
          filterOutput = this.changeFilterOrder(filterOutput, orderDTO);

          // Business service
          filterOutput = getStore().update(filterOutput);
          // Register FilterResource as observer of datasets resources
          unregisterObserver(filterOutput);

          registerObserver(filterOutput);

          if (filterOutput != null) {
            // Response
            FilterChainedModelDTO filterChainedOutDTO = getFilterChainedModelDTO(filterOutput);
            response = new Response(true, filterChainedOutDTO, FilterChainedModelDTO.class, "filterChainedModel");

          }

        }
        else {
          response = new Response(false, "Can not validate filterChained");
        }

      }

      else {
        // Response

        response = new Response(false, "Can not validate filterChained");

      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Sort the filter list using the given order
   * 
   * @param filterChained
   *          the filterChainedModel to sort
   * @param orderDTO
   *          the new order
   * @return the sorted filterChainedModel
   */
  private FilterChainedModel changeFilterOrder(FilterChainedModel filterChained, FilterChainedOrderDTO orderDTO) {

    List<FilterModel> outputList = new ArrayList<FilterModel>();
    List<FilterModel> filterList = filterChained.getFilters();

    boolean found = false;
    if (orderDTO.getIdOrder() != null) {
      for (Iterator<String> iterator = orderDTO.getIdOrder().iterator(); iterator.hasNext();) {
        String filterId = iterator.next();
        found = false;
        for (Iterator<FilterModel> it2 = filterList.iterator(); it2.hasNext() && !found;) {
          FilterModel filterModel = it2.next();
          if (filterId.equals(filterModel.getId())) {
            outputList.add(filterModel);
            it2.remove();
            found = true;
          }
        }
      }
    }
    filterChained.setFilters(outputList);
    return filterChained;

  }

  /**
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   * @param path
   *          url attachment of the resource
   */
  @Override
  public void describePut(MethodInfo info, String path) {
    // Method
    info.setDocumentation("This method permits to modify the order of the filter in the chain of filters");
    info.setIdentifier("update_filterschained");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);
    // Response 500

    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);

  }

  /**
   * Delete Filter
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteFilterChainedModel(Variant variant) {
    try {

      FilterChainedModel filter = getStore().retrieve(getDatasetId());
      Response response;
      if (filter != null) {

        // Business service
        getStore().delete(getDatasetId());

        // unregister as observer
        unregisterObserver(filter);

        // Response
        response = new Response(true, "filterChained.deleted.success");
      }
      else {
        response = new Response(false, "filterChained.deleted.failure");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the Delete command
   * 
   * @param info
   *          the info sent
   * @param path
   *          url attachment of the resource
   */
  @Override
  public void describeDelete(MethodInfo info, String path) {
    if (path.endsWith("{filterChainedId}")) {
      // FIXME Erreur
      info.setDocumentation("DELETE " + path + " : Delete the single filter object");
    }
    else if (path.equals("")) {
      // FIXME confirm
      info.setDocumentation("DELETE " + path + " : Delete the chain of filters");
    }

    // Method
    info.setIdentifier("delete_filterschained");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("filterChainedId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the filter");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardSimpleResponseInfo(info);

    // Response 500
    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);

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
