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
package fr.cnes.sitools.plugins.applications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginModelDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;

/**
 * 
 * ApplicationPluginCollectionResource
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ApplicationPluginCollectionResource extends AbstractApplicationPluginResource {

  @Override
  public void sitoolsDescribe() {
    setName("ApplicationPluginCollectionResource");
    setDescription("Get or create an ApplicationPlugin.");
  }

  /**
   * Gets multiple ApplicationPlugins or one ApplicationPlugin if id is given in parameter into a standard Response
   * representation
   * 
   * @param variant
   *          Variant client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveAppPlugin(Variant variant) {
    try {
      if (getAppId() != null) {
        ApplicationPluginModel appModel = getStore().retrieve(getAppId());
        ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(appModel);
        addCurrentClassDescription(appModelOutDTO);
        Response response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class,
            "ApplicationPluginModel");
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
        addCurrentClassDescription(appPluginModelsDTO);
        Response response = new Response(true, appPluginModelsDTO, ApplicationPluginModelDTO.class,
            "ApplicationPluginModels");
        response.setTotal(total);
        trace(Level.FINE, "View available application plugins");
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

  @Override
  protected void describeGet(MethodInfo info) {
    info.setDocumentation("Gets multiple ApplicationPlugins or one ApplicationPlugin if id is given in parameter into a standard Response representation");

    // Add the standard request for GET
    this.addStandardGetRequestInfo(info);

    // Add the standard response : status 200, response with object or not
    this.addStandardResponseInfo(info);

    // Failure
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Create a new ApplicationPluginModel
   * 
   * @param representation
   *          ApplicationPluginModel Representation
   * @param variant
   *          Variant user preferred MediaType
   * @return Representation
   */
  @Post
  public Representation newAppModel(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "APPLICATION_PLUGIN_MODEL_REPRESENTATION_REQUIRED");
    }
    Response response;
    try {
      ApplicationPluginModelDTO appModelInputDTO = getObject(representation);

      ApplicationPluginModel appModelInput = getApplicationModelFromDTO(appModelInputDTO);

      // VALIDATION PART
      Set<ConstraintViolation> constraints = checkValidity(appModelInput);
      if (constraints != null) {
        ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
        array = constraints.toArray(array);

        response = new Response(false, array, ConstraintViolation.class, "constraints");
        return getRepresentation(response, variant);
      }
      // END OF THE VALIDATION PART

      if (appModelInput.getId() == null || "".equals(appModelInput.getId())) {
        appModelInput.setId(UUID.randomUUID().toString());
      }

      if (appModelInput.getUrlAttach() == null || "".equals(appModelInput.getUrlAttach())) {
        appModelInput.setUrlAttach("/" + appModelInput.getId());
      }

//      getStore().save(appModelInput);

      // instanciation de la nouvelle application

//      try {
//        // we attach the new application and don't start it
//        getResourceApplication().attachApplication(appModelInput, false);
//
//      }
//      catch (ClassNotFoundException e) {
//        response = new Response(false, "ClassNotFoundException");
//        getLogger().severe(e.getMessage());
//      }
//      catch (NoSuchMethodException e) {
//        response = new Response(false, "NoSuchMethodException");
//        getLogger().severe(e.getMessage());
//      }
//      catch (InstantiationException e) {
//        response = new Response(false, "InstantiationException");
//        getLogger().severe(e.getMessage());
//      }
//      catch (IllegalAccessException e) {
//        response = new Response(false, "IllegalAccessException");
//        getLogger().severe(e.getMessage());
//      }
//      catch (InvocationTargetException e) {
//        response = new Response(false, "InvocationTargetException");
//        getLogger().severe(e.getMessage());
//      }
//      catch (Exception e) {
//        response = new Response(false, "app.plugin.create.error");
//        getLogger().severe(e.getMessage());
//      }

      appModelInput.setStatus("NEW");
      getStore().create(appModelInput);

      ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(appModelInput);
      response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class, "ApplicationPluginModel");

      trace(Level.INFO, "Add the application plugin");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot add the application plugin");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describePost(MethodInfo info) {
    info.setDocumentation("Create and activate a new application plug-in model from representation sent.");
    info.setIdentifier("create_appPlugins_model");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);

    // Failure
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Add current Class description for comparison with stored application descriptions
   * 
   * @param appPluginModels
   *          the app plugins
   */
  private void addCurrentClassDescription(List<ApplicationPluginModelDTO> appPluginModels) {
    if (appPluginModels.size() > 0) {
      for (ApplicationPluginModelDTO app : appPluginModels) {
        addCurrentClassDescription(app);
      }
    }

  }

  /**
   * Add current Class description for comparison with stored application descriptions
   * 
   * @param app
   *          the app plugin
   */
  private void addCurrentClassDescription(ApplicationPluginModelDTO app) {
    try {
      @SuppressWarnings("unchecked")
      Class<AbstractApplicationPlugin> appClass = (Class<AbstractApplicationPlugin>) Class.forName(app.getClassName());
      Constructor<AbstractApplicationPlugin> appConstructor = appClass.getDeclaredConstructor();
      AbstractApplicationPlugin object = appConstructor.newInstance();
      app.setCurrentClassAuthor(object.getModel().getClassAuthor());
      app.setCurrentClassVersion(object.getModel().getClassVersion());
    }
    catch (ClassNotFoundException e) {
      app.setCurrentClassAuthor("CLASS_NOT_FOUND");
      app.setCurrentClassVersion("CLASS_NOT_FOUND");
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
