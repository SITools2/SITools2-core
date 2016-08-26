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
package fr.cnes.sitools.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Get the list of forms for the project with authorizations
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectListFormsResource extends AbstractProjectResource {
  /** The project Application */
  private ProjectApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListFormsResource");
    setDescription("List of forms for a project with authorization");
  }

  /**
   * Get the list of forms for the project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getFormsList(Variant variant) {

    Representation rep = null;

    User user = this.getRequest().getClientInfo().getUser();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    application = (ProjectApplication) getApplication();

    Project proj = application.getProject();
    List<Resource> dsList = proj.getDataSets();

    //ArrayList<FormDTO> formListOutput = new ArrayList<FormDTO>();
    List<FormDTO> formsDTO = new ArrayList<FormDTO>();
    //Response resp;
    
    if (dsList == null) {
      Response response = new Response(true, formsDTO, Form.class);
      return getRepresentation(response, variant);
    }

    AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();
    
    for (Iterator<Resource> iterator = dsList.iterator(); iterator.hasNext();) {
      Resource ds = iterator.next();

      // retrouver l'objet application
      SitoolsApplication myApp = appManager.getApplication(ds.getId());
      boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);

      DataSet dsModel = this.getDataset(ds.getId());
      boolean visible = dsModel.isVisible();
      if (authorized || visible) {
        List<Form> forms = getFormsByDatasetId(ds.getId());
        if (forms != null) {
          FormDTO dto = null;
          for (Form form : forms) {
            dto = FormDTO.formToDTO(form);
            dto.setAuthorized(Boolean.valueOf(authorized).toString());
            formsDTO.add(dto);
          }
        }
      }
    }

    Response response = new Response(true, formsDTO, Form.class);
    rep = getRepresentation(response, variant);

    return rep;
  }

  private List<Form> getFormsByDatasetId(String datasetId) {
    try {
      if (datasetId == null) {
        datasetId = "None";
        throw new IllegalArgumentException();
      }
      //ResourceCollectionFilter filter = new ResourceCollectionFilter(0,0,"");
      //filter.setParent(datasetId);
      List<Form> befforms = getFormStore().getList();
      List<Form> forms = new ArrayList<Form>();
      for (Form form : befforms) {
        if (form.getParent().equals(datasetId)) {
          forms.add(form);
        }
      }
      return forms;
      
    } catch (ResourceException e) {
      trace(Level.FINE, "Cannot view available query forms for the dataset - id : " + datasetId);
      getLogger().log(Level.INFO, null, e);
      throw e;
    } catch (Exception e) {
      trace(Level.FINE, "Cannot view available query forms for the dataset - id : " + datasetId);
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

//  /**
//   * Get the list of forms for a dataset
//   * @param id the id of the dataset
//   * @return a Response containing the list of Forms
//   */
//  private Response getFormResponse(String id) {
//    //getFormStore().retrieve()
//    
//    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
//        + application.getSettings().getString(Consts.APP_DATASETS_URL) + "/" + id + "/forms");
//    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
//    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
//    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
//    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);
//
//    if (response == null || Status.isError(response.getStatus().getCode())) {
//      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
//    }
//
//    @SuppressWarnings("unchecked")
//    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
//    try {
//      return or.getObject();
//    }
//    catch (IOException e) { // marshalling error
//      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
//    }
//  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of forms associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
