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
package fr.cnes.sitools.plugins.applications;

import java.util.ArrayList;
import java.util.Set;
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

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginModelDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;

/**
 * Resource for single class of application plug-in model get/update/delete
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ApplicationPluginResource extends AbstractApplicationPluginResource {

  @Override
  public void sitoolsDescribe() {
    setName("ApplicationPluginResource");
    setDescription("Resource for single class of application plug-in model get/update/delete");
  }

  /**
   * Get on DataSet
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveAppModel(Variant variant) {
    try {

      if (getAppId() != null) {
        ApplicationPluginModel appModel = getStore().get(getAppId());
        Response response = null;
        if (appModel != null) {
          ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(appModel);
          response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class, "ApplicationPluginModel");
        }
        else {
          response = new Response(false, "NOT_FOUND");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
        ArrayList<ApplicationPluginModel> appPluginModels = new ArrayList<ApplicationPluginModel>(getStore().getList(
            filter));
        int total = appPluginModels.size();
        appPluginModels = new ArrayList<ApplicationPluginModel>(getStore().getPage(filter, appPluginModels));
        ArrayList<ApplicationPluginModelDTO> appPluginModelsDTO = new ArrayList<ApplicationPluginModelDTO>();
        for (ApplicationPluginModel applicationPluginModel : appPluginModels) {
          appPluginModelsDTO.add(getApplicationModelDTO(applicationPluginModel));
        }
        Response response = new Response(true, appPluginModelsDTO, ApplicationPluginModelDTO.class,
            "ApplicationPluginModels");
        response.setTotal(total);
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
   * Update / Validate existing DataSet
   * 
   * @param representation
   *          DataSet Representation
   * @param variant
   *          Variant client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateAppPlugin(Representation representation, Variant variant) {
    ApplicationPluginModel appOutput = null;
    try {
      ApplicationPluginModelDTO appInputDTO = null;
      if (representation != null) {

        appInputDTO = getObject(representation);
        ApplicationPluginModel appInput = getApplicationModelFromDTO(appInputDTO);

        // get the classname from the store
        if (appInput.getClassName() == null || appInput.getClassName().equals("")) {
          ApplicationPluginModel modelStored = getStore().get(appInput.getId());
          appInput.setClassName(modelStored.getClassName());
        }

        // VALIDATION PART
        Set<ConstraintViolation> constraints = checkValidity(appInput);
        if (constraints != null) {
          ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
          array = constraints.toArray(array);

          Response response = new Response(false, array, ConstraintViolation.class, "constraints");
          return getRepresentation(response, variant);
        }
        // END OF THE VALIDATION PART

        ApplicationPluginModel app = getStore().get(getAppId());
        if ("ACTIVE".equals(app.getStatus())) {
          Response response = new Response(false, "APP_PLUGIN_ACTIVE");
          return getRepresentation(response, variant);
        }
        appInput.setStatus("INACTIVE");
        appInput.setClassName(app.getClassName());
        appInput.setName(app.getName());
        appInput.setDescription(app.getDescription());

        getStore().save(appInput);

        appOutput = getStore().get(getAppId());

      }

      if (appOutput != null) {
        ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(appOutput);
        Response response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class,
            "ApplicationPluginModel");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "Can not validate ApplicationPluginModel");
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
   * Delete ApplicationPluginModel
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteAppPlugin(Variant variant) {
    try {

      ApplicationPluginModel appOutput = getStore().get(getAppId());
      Response response;
      if (appOutput != null) {
        // Business service
        getStore().delete(appOutput);
        getResourceApplication().detachApplicationDefinively(appOutput);
        response = new Response(true, "ApplicationPluginModel.delete.success");

      }
      else {
        // Response
        response = new Response(true, "ApplicationPluginModel.delete.failure");
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

  @Override
  protected void describeGet(MethodInfo info) {
    info.setDocumentation("Gets multiple ApplicationPlugins or one ApplicationPlugin if id is given in parameter into a standard Response representation");
    info.setIdentifier("get_plugins_by_id");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramInfo = new ParameterInfo("applicationPluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the class to retrieve.");
    info.getRequest().getParameters().add(paramInfo);

    // Response 200
    this.addStandardResponseInfo(info);

    // Failure
    this.addStandardInternalServerErrorInfo(info);
  }

  @Override
  protected void describePut(MethodInfo info) {
    info.setDocumentation("Modify given ApplicationPlugin if id is given in parameter into a standard Response representation");
    info.setIdentifier("modify_appPlugins_by_id");

    // Request
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramInfo = new ParameterInfo("applicationPluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the class to treat");
    info.getRequest().getParameters().add(paramInfo);

    // Response 200
    this.addStandardResponseInfo(info);

    // Failure
    this.addStandardInternalServerErrorInfo(info);
  }

  @Override
  protected void describeDelete(MethodInfo info) {
    info.setDocumentation("Delete given ApplicationPlugin if id is given in parameter into a standard Response representation");
    info.setIdentifier("delete_appPlugins_by_id");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramInfo = new ParameterInfo("applicationPluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the class to delete");
    info.getRequest().getParameters().add(paramInfo);

    // Response 200
    this.addStandardSimpleResponseInfo(info);

    // Failure
    this.addStandardInternalServerErrorInfo(info);
  }

}
