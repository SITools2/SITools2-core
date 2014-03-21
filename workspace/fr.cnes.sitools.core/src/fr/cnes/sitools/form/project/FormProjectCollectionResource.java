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
package fr.cnes.sitools.form.project;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class Resource for managing FormProject FormProject (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class FormProjectCollectionResource extends AbstractFormProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("FormProjectCollectionResource");
    setDescription("Resource for managing FormProject collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing FormProject
   * 
   * @param representation
   *          FormProject representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newFormProject(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot create the multi-dataset - id: " + getFormProjectId());
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      FormProject formProjectInput = getObject(representation, variant);
      if (formProjectInput.getParent() == null) {
        formProjectInput.setParent(getProjectId());
      }

      // attach new resource to the projectApplication for search service
      formProjectInput = attachServices(formProjectInput);

      // Business service
      FormProject formProjectOutput = getStore().create(formProjectInput);

      trace(Level.INFO, "Create the multi-dataset " + formProjectInput.getName());

      // Response
      Response response = new Response(true, formProjectOutput, FormProject.class, "formProject");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the multi-dataset - id: " + getFormProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the multi-dataset - id: " + getFormProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Attach the search services to the current project application
   * 
   * @param formProject
   *          the FormProject
   * @return the FormProject with the id of resources assigned
   * @throws SitoolsException
   *           if the resource wasn't created properly
   * @throws InstantiationException
   *           if there is an error while instantiating className
   * @throws IllegalAccessException
   *           if the class or its nullary constructor is not accessible
   * @throws ClassNotFoundException
   *           if className cannot be found
   */
  private FormProject attachServices(FormProject formProject) throws SitoolsException, InstantiationException,
      IllegalAccessException, ClassNotFoundException {

    String resourceModelClassNamePropertiesSearch = "fr.cnes.sitools.form.project.services.ServicePropertiesSearchResourceModel";
    String resourceModelClassNameDatasetSearch = "fr.cnes.sitools.form.project.services.ServiceDatasetSearchResourceModel";
    // attach resource for properties
    String idPropertiesSearch = attachPropertiesServiceResource(resourceModelClassNamePropertiesSearch,
        formProject.getUrlServicePropertiesSearch(), formProject.getCollection().getId(), formProject.getDictionary()
            .getId(), formProject.getName() + "FormProject's properties service");
    formProject.setIdServicePropertiesSearch(idPropertiesSearch);
    // attach resource for dataset search
    String idDatasetSearch = attachSearchServiceResource(resourceModelClassNameDatasetSearch,
        formProject.getUrlServiceDatasetSearch(), formProject.getCollection().getId(), formProject.getDictionary()
            .getId(), formProject.getName() + "FormProject's search service", formProject.getNbDatasetsMax());
    formProject.setIdServiceDatasetSearch(idDatasetSearch);

    return formProject;

  }

  /**
   * Attach a propertiesService resource with the given parameters to the current project
   * 
   * @param className
   *          the class of the resource to attach
   * @param attachment
   *          the urlAttachment
   * @param collectionId
   *          the id of the collection
   * @param dictionary
   *          the id of the dictionary
   * @param descriptionAction
   *          the DescriptionAction to set to the resource
   * @return The id of the created Resource
   * @throws SitoolsException
   *           if the resource wasn't created properly
   * @throws InstantiationException
   *           if there is an error while instantiating className
   * @throws IllegalAccessException
   *           if the class or its nullary constructor is not accessible
   * @throws ClassNotFoundException
   *           if className cannot be found
   */
  private String attachPropertiesServiceResource(String className, String attachment, String collectionId,
      String dictionary, String descriptionAction) throws SitoolsException, InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    ResourceModel resourceModel = createResourceModelWithCommonParam(className, attachment, collectionId, dictionary,
        descriptionAction);
    return persistResourceModel(resourceModel);
  }

  /**
   * Attach a searchService resource with the given parameters to the current project
   * 
   * @param className
   *          the class of the resource to attach
   * @param attachment
   *          the urlAttachment
   * @param collectionId
   *          the id of the collection
   * @param dictionary
   *          the id of the dictionary
   * @param descriptionAction
   *          the DescriptionAction to set to the resource
   * @param nbDatasetsMax
   *          the maximal number of Dataset allowed for the request
   * @return The id of the created Resource
   * @throws SitoolsException
   *           if the resource wasn't created properly
   * @throws InstantiationException
   *           if there is an error while instantiating className
   * @throws IllegalAccessException
   *           if the class or its nullary constructor is not accessible
   * @throws ClassNotFoundException
   *           if className cannot be found
   */
  private String attachSearchServiceResource(String className, String attachment, String collectionId,
      String dictionary, String descriptionAction, Integer nbDatasetsMax) throws SitoolsException,
      InstantiationException, IllegalAccessException, ClassNotFoundException {
    ResourceModel resourceModel = createResourceModelWithCommonParam(className, attachment, collectionId, dictionary,
        descriptionAction);
    if (nbDatasetsMax != null) {
      resourceModel.getParameterByName(nbDatasetsMaxParamName).setValue(nbDatasetsMax.toString());
    }
    return persistResourceModel(resourceModel);

  }

  /**
   * Create a new ResourceModel from the given ClassName and fill the common parameters
   * 
   * @param className
   *          the class of the resource to attach
   * @param attachment
   *          the urlAttachment
   * @param collectionId
   *          the id of the collection
   * @param dictionary
   *          the id of the dictionary
   * @param descriptionAction
   *          the DescriptionAction to set to the resource
   * @return The id of the created Resource
   * @throws SitoolsException
   *           if the resource wasn't created properly
   * @throws InstantiationException
   *           if there is an error while instantiating className
   * @throws IllegalAccessException
   *           if the class or its nullary constructor is not accessible
   * @throws ClassNotFoundException
   *           if className cannot be found
   */
  private ResourceModel createResourceModelWithCommonParam(String className, String attachment, String collectionId,
      String dictionary, String descriptionAction) throws SitoolsException, InstantiationException,
      IllegalAccessException, ClassNotFoundException {

    @SuppressWarnings("unchecked")
    Class<ResourceModel> resourceModelClass = (Class<ResourceModel>) Class.forName(className);

    ResourceModel resourceModel = resourceModelClass.newInstance();
    resourceModel.initParametersForAdmin(getContext());

    resourceModel.getParameterByName("url").setValue(attachment);
    resourceModel.getParameterByName(collectionParamName).setValue(collectionId);
    resourceModel.getParameterByName(dictionaryParamName).setValue(dictionary);

    resourceModel.setDescriptionAction(descriptionAction);

    return resourceModel;

  }

  /**
   * Persist a ResourceModel into the store
   * 
   * @param resourceModel
   *          the {@link ResourceModel} to persist
   * @return the id of the persisted {@link ResourceModel}
   * @throws SitoolsException
   *           if there is an error while persisting the resource
   */
  private String persistResourceModel(ResourceModel resourceModel) throws SitoolsException {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    String url = settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + getProjectId()
        + settings.getString(Consts.APP_RESOURCES_URL);
    ResourceModelDTO dto = ResourceModelDTO.resourceModelToDTO(resourceModel);
    ResourceModelDTO resourceModelOut = RIAPUtils.persistObject(dto, url, getContext());
    if (resourceModelOut == null) {
      throw new SitoolsException("Cannot create resource model");
    }
    return resourceModelOut.getId();
  }

  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new form components sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all FormProject
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveFormProject(Variant variant) {
    try {
      Response response;
      if (getFormProjectId() != null) {
        FormProject formProject = getStore().retrieve(getFormProjectId());
        if ((getProjectId() != null) && (!getProjectId().equals(formProject.getParent()))) {
          trace(Level.INFO, "Cannot edit multi-dataset information for the multi-dataset - id: " + getFormProjectId());
          response = new Response(false, "FORM_DONT_BELONG_TO_PROJECT");
        }
        else {
          trace(Level.FINE, "Edit multi-dataset information for the multi-dataset " + formProject.getName());
          response = new Response(true, formProject, FormProject.class, "formProject");
        }
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        if (getProjectId() != null) {
          filter.setParent(getProjectId());
        }
        List<FormProject> formProject = getStore().getList(filter);
        int total = formProject.size();
        formProject = getStore().getPage(filter, formProject);
        trace(Level.FINE, "View available query forms for multi-datasets");
        response = new Response(true, formProject, FormProject.class, "formProject");
        response.setTotal(total);

      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available query forms for multi-datasets");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available query forms for multi-datasets");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of form components available on the server.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
