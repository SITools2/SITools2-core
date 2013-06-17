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
package fr.cnes.sitools.plugins.filters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.filters.dto.FilterModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.registry.AppRegistryApplication;

/**
 * Resource handling filter plugins on applications ...
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class FilterPluginResource extends AbstractFilterPluginResource {

  /** store */
  private SitoolsStore<FilterModel> store = null;

  @Override
  public void doInit() {
    super.doInit();

    store = ((FilterPluginApplication) getApplication()).getStore();
  }

  @Override
  public void sitoolsDescribe() {
    setName("FilterPluginResource");
    setDescription("Filter plugins to attach applications or directories");
    setNegotiated(false);
  }

  /**
   * Create / attach filter
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newFilterPlugin(Representation representation, Variant variant) {
    try {
      FilterModelDTO filterModelDTO = getObject(representation);

      FilterModel filterInput = getFilterModelFromDTO(filterModelDTO);

      // // VALIDATION PART
      Set<ConstraintViolation> constraints = checkValidity(filterInput);
      if (constraints != null) {
        ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
        array = constraints.toArray(array);

        Response response = new Response(false, array, ConstraintViolation.class, "constraints");
        return getRepresentation(response, variant);
      }

      // set id
      if ((getFilterPluginId() != null) && !getFilterPluginId().equals("")) {
        filterInput.setId(getFilterPluginId());
      }
      else if (filterInput.getId() == null || filterInput.getId().equals("")) {
        filterInput.setId(UUID.randomUUID().toString());
      }

      // Business service
      // application parent
      // resourceInput.setParent(getParentId());

      // Response
      // fillParametersMap(resourceInput);

      FilterModel filterOutput = getStore().create(filterInput);

      // register observer
      registerObserver(filterOutput);

      // // A quelle restlet / application ajouter l'utilisation du filtre ?
      // // // Add to dynamic list of resources
      // SitoolsParameterizedApplication app = (SitoolsParameterizedApplication)
      // getFilterPluginApplication().getSettings().getAppRegistry()
      // .getApplication(resourceOutput.getParent());
      // if (app.isStarted()) {
      // refreshApplication(app);
      // }
      // nécessite de redémarrer l'appelant ... Directory Storage ou application

      FilterModelDTO filterModelOutDTO = getFilterModelDTO(filterOutput);
      Response response = new Response(true, filterModelOutDTO, FilterModelDTO.class, "filterPlugin");
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
    info.setDocumentation("Creates a new filter attached to an application");

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

  @Get
  @Override
  public Representation get(Variant variant) {

    if (getFilterPluginId() != null) {
      FilterModel filter = getStore().retrieve(getFilterPluginId());
      // Response
      Response response;
      if (filter == null) {
        response = new Response(false, "no.filters.found");
      }
      else {
        // Return the Model if it has been specifically asked, return the DTO otherwise
        MediaType mediaType = getMediaType(variant);
        if (mediaType.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
          response = new Response(true, filter, FilterModel.class, "filterPlugin");
        }
        else {
          FilterModelDTO filterModelOutDTO = getFilterModelDTO(filter);
          addCurrentClassDescription(filterModelOutDTO);
          response = new Response(true, filterModelOutDTO, FilterModelDTO.class, "filterPlugin");
        }
      }
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
      List<FilterModel> filterList = getStore().getList(filter);
      List<FilterModel> filterArray = new ArrayList<FilterModel>();
      for (FilterModel model : filterList) {
        if (model.getParent().equals(model.getParent())) {
          // Response
          filterArray.add(model);
        }
      }
      int total = filterArray.size();
      filterArray = getStore().getPage(filter, filterArray);
      List<FilterModelDTO> filterArrayDTO = new ArrayList<FilterModelDTO>();
      for (FilterModel filterModel : filterArray) {
        filterArrayDTO.add(getFilterModelDTO(filterModel));
      }
      addCurrentClassDescription(filterArrayDTO);
      Response response = new Response(true, filterArrayDTO, FilterModelDTO.class, "filterPlugins");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
  }

  /**
   * GET method description
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment of the resource
   */
  public void describeGet(MethodInfo info, String path) {
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    if (path.endsWith("{pluginId}")) {
      info.setDocumentation("GET " + path + ": Method to retrieve a specified filter attached to an application");
      ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the filter");
      info.getRequest().getParameters().add(pic);
    }
    else {
      info.setDocumentation("GET " + path + ": Method to retrieve all filter for an application");
    }

  }

  /**
   * Update / Validate existing Filters
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateFilterPlugin(Representation representation, Variant variant) {
    FilterModel resourceOutput = null;
    try {
      FilterModel filterInput = null;
      if (representation != null) {

        FilterModelDTO filterInputDTO = getObject(representation);

        filterInput = getFilterModelFromDTO(filterInputDTO);

        // // VALIDATION PART
        Set<ConstraintViolation> constraints = checkValidity(filterInput);
        if (constraints != null) {
          ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
          array = constraints.toArray(array);

          Response response = new Response(false, array, ConstraintViolation.class, "constraints");
          return getRepresentation(response, variant);
        }

        // Business service
        resourceOutput = getStore().update(filterInput);

        // Register ConverterResource as observer of datasets resources
        unregisterObserver(resourceOutput);

        registerObserver(resourceOutput);
      }

      if (resourceOutput != null) {
        // Response

        // Add to dynamic list of resources
        if (resourceOutput.getParent() != null) {
          SitoolsApplication app = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry()
              .getApplication(resourceOutput.getParent());
          if ((app != null) && app.isStarted()) {
            refreshApplication(app);
          }
        }

        FilterModelDTO filterModelOutDTO = getFilterModelDTO(resourceOutput);
        Response response = new Response(true, filterModelOutDTO, FilterModelDTO.class, "filterPlugin");
        return getRepresentation(response, variant);

      }
      else {
        // Response

        Response response = new Response(false, "Can not validate resource plugin");
        return getRepresentation(response, variant);

      }

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
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to modify a filter attached to an application");
    info.setIdentifier("update_filter");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
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
   * Delete filter
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteFilterPlugin(Variant variant) {
    try {
      FilterModel filter = getStore().retrieve(getFilterPluginId());
      Response response = null;
      if (filter != null) {

        // Business service
        getStore().delete(getFilterPluginId());

        // Response
        response = new Response(true, "filterplugin.deleted.success");

        // Register ConverterChainedModel as observer of datasets resources
        unregisterObserver(filter);

        // Add to dynamic list of resources
        if (filter != null && filter.getParent() != null) {
          SitoolsApplication app = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry()
              .getApplication(filter.getParent());
          if ((app != null) && app.isStarted()) {
            refreshApplication(app);
          }
        }

      }
      else {
        response = new Response(false, "filterplugin.deleted.failure");
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
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("This method delete a filter attached to an application");
    info.setIdentifier("delete_filter");

    this.addStandardGetRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the filter");
    info.getRequest().getParameters().add(pic);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * register the new filter to the application
   * 
   * @param app
   *          the application
   */
  private void refreshApplication(SitoolsApplication app) {
    AppRegistryApplication registry = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();
    app.setInboundRoot(app.createInboundRoot());
    registry.reattachApplication(app);
  }
  
  /**
   * Add current Class description for comparison with stored application descriptions
   * 
   * @param filterPluginList
   *          the app plugins
   */
  private void addCurrentClassDescription(List<FilterModelDTO> filterPluginList) {
    if (filterPluginList.size() > 0) {
      for (FilterModelDTO app : filterPluginList) {
        addCurrentClassDescription(app);
      }
    }

  }

  /**
   * Add the current class descriptions of the converters in the chained model
   * 
   * @param filter
   *          the filter model to apply
   */
  private void addCurrentClassDescription(FilterModelDTO filter) {
    if (filter != null && filter.getClassName() != null) {
      try {
        @SuppressWarnings("unchecked")
        Class<FilterModel> filterClass = (Class<FilterModel>) Class.forName(filter.getClassName());
        Constructor<FilterModel> filterConstructor = filterClass.getDeclaredConstructor();
        FilterModel object = filterConstructor.newInstance();
        filter.setCurrentClassAuthor(object.getClassAuthor());
        filter.setCurrentClassVersion(object.getClassVersion());
      }
      catch (ClassNotFoundException e) {
        filter.setCurrentClassAuthor("CLASS_NOT_FOUND");
        filter.setCurrentClassVersion("CLASS_NOT_FOUND");
        getLogger().severe(e.getMessage());
      }
      catch (SecurityException e) {
        getLogger().severe(e.getMessage());
      }
      catch (NoSuchMethodException e) {
        getLogger().severe(e.getMessage());
      }
      catch (IllegalArgumentException e) {
        getLogger().severe(e.getMessage());
      }
      catch (InstantiationException e) {
        getLogger().severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        getLogger().severe(e.getMessage());
      }
      catch (InvocationTargetException e) {
        getLogger().severe(e.getMessage());
      }
    }
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<FilterModel> getStore() {
    return store;
  }

}
