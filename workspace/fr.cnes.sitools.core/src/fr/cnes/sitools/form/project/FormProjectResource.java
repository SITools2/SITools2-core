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
package fr.cnes.sitools.form.project;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class Resource for managing single FormProject (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class FormProjectResource extends AbstractFormProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("FormProjectResource");
    setDescription("Resource for managing an identified formProject");
    setNegotiated(false);
  }

  /**
   * get all formProject
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveFormProject(Variant variant) {
    // XStream xstream = XStreamFactory.getInstance().getXStreamWriter(variant.getMediaType(), false);
    if (getFormProjectId() != null) {
      FormProject formProject = getStore().retrieve(getFormProjectId());
      Response response = new Response(true, formProject, FormProject.class, "formProject");
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<FormProject> formProject = getStore().getList(filter);
      int total = formProject.size();
      formProject = getStore().getPage(filter, formProject);
      Response response = new Response(true, formProject, FormProject.class, "formProject");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formProjectId", true, "class", ParameterStyle.TEMPLATE,
        "FormProject component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Update / Validate existing formProject
   * 
   * @param representation
   *          FormProject representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateFormProject(Representation representation, Variant variant) {
    FormProject formProjectOutput = null;
    try {

      FormProject formProjectInput = null;
      if (representation != null) {
        // Parse object representation
        formProjectInput = getObject(representation, variant);
        if (formProjectInput.getParent() == null) {
          formProjectInput.setParent(getProjectId());
        }

        FormProject formProjectFromStore = getStore().retrieve(formProjectInput.getId());
        formProjectInput.setIdServiceDatasetSearch(formProjectFromStore.getIdServiceDatasetSearch());
        formProjectInput.setIdServicePropertiesSearch(formProjectFromStore.getIdServicePropertiesSearch());

        updateServices(formProjectInput);
        // Business service
        formProjectOutput = getStore().update(formProjectInput);

      }

      Response response = new Response(true, formProjectOutput, FormProject.class, "formProject");
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

  @Override
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single formProject sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formProjectId", true, "class", ParameterStyle.TEMPLATE,
        "FormProject identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete formProject
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteFormProject(Variant variant) {
    try {
      FormProject formProject = getStore().retrieve(getFormProjectId());
      detachServices(formProject);
      // Business service
      getStore().delete(getFormProjectId());

      // Response
      Response response = new Response(true, "formProject.delete.success");
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
   * Detach (Delete) the services to the current project application
   * 
   * @param formProject
   *          the FormProject
   */
  private void detachServices(FormProject formProject) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    String url = settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + getProjectId()
        + settings.getString(Consts.APP_RESOURCES_URL);
    RIAPUtils.deleteObject(url + "/" + formProject.getIdServiceDatasetSearch(), getContext());
    RIAPUtils.deleteObject(url + "/" + formProject.getIdServicePropertiesSearch(), getContext());
  }

  /**
   * Update the services to the current project application
   * 
   * @param formProject
   *          the FormProject
   */
  private void updateServices(FormProject formProject) {
    String nbDatasetsMax = (formProject.getNbDatasetsMax() != null) ? formProject.getNbDatasetsMax().toString() : null;
    updateAService(formProject, formProject.getIdServiceDatasetSearch(), formProject.getUrlServiceDatasetSearch(),
        nbDatasetsMax);
    updateAService(formProject, formProject.getIdServicePropertiesSearch(),
        formProject.getUrlServicePropertiesSearch(), null);
  }

  /**
   * update a particular service
   * 
   * @param formProject
   *          the FormProject
   * @param idResource
   *          the if of the resource of the service
   * @param urlService
   *          the url of the service to set
   * @param nbDatasetsMax
   *          the maximum number of datasets allowed for the request, only used for dataset search resource
   * @return the modified {@link ResourceModelDTO}
   */
  private ResourceModelDTO updateAService(FormProject formProject, String idResource, String urlService,
      String nbDatasetsMax) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    String url = settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + getProjectId()
        + settings.getString(Consts.APP_RESOURCES_URL);
    ResourceModelDTO resModelDTO = RIAPUtils.getObject(url + "/" + idResource, getContext());

    List<ResourceParameter> parameters = resModelDTO.getParameters();
    for (ResourceParameter resourceParameter : parameters) {
      if (resourceParameter.getName().equals(collectionParamName)) {
        resourceParameter.setValue(formProject.getCollection().getId());
      }
      if (resourceParameter.getName().equals(dictionaryParamName)) {
        resourceParameter.setValue(formProject.getDictionary().getId());
      }
      if (resourceParameter.getName().equals("url")) {
        resourceParameter.setValue(urlService);
      }
      if (resourceParameter.getName().equals("nbDatasetsMax")) {
        resourceParameter.setValue(nbDatasetsMax);
      }
    }
    return RIAPUtils.updateObject(resModelDTO, url + "/" + idResource, getContext());

  }

  @Override
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formProjectId", true, "class", ParameterStyle.TEMPLATE,
        "FormProject component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
