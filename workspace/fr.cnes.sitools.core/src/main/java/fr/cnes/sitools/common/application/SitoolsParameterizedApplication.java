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
package fr.cnes.sitools.common.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreInterface;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.common.Consts;

/**
 * Base class for Application that gets parameterized resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class SitoolsParameterizedApplication extends SitoolsApplication {

  /**
   * Storing models for all ParameterizedResource
   */
  private Map<String, ResourceModel> modelMap = new HashMap<String, ResourceModel>();

  /**
   * Finders attached to the router and linked to the ClassResource
   */
  private Map<String, Restlet> finderMap = new HashMap<String, Restlet>();

  /**
   * Constructor with context
   * 
   * @param context
   *          the context
   */
  public SitoolsParameterizedApplication(Context context) {
    super(context);
  }

  /**
   * Default constructor
   */
  public SitoolsParameterizedApplication() {
    super();
  }

  /**
   * Constructor with a Context and a Representation
   * 
   * @param context
   *          The context to use based on parent component context. This context should be created using the
   *          {@link Context#createChildContext()} method to ensure a proper isolation with the other applications.
   * @param wadl
   *          The WADL description document.
   */
  public SitoolsParameterizedApplication(Context context, Representation wadl) {
    super(context, wadl);
  }

  protected Context getContext(String path, ServerResource sr) {
    if (modelMap.get("/" + path) != null) {
      Context ctx = getContext().createChildContext();
      ctx.setAttributes(getContext().getAttributes());
      ctx.getAttributes().put(ContextAttributes.RESOURCE_ATTACHMENT, "/" + path);
      return ctx;
    }
    else {
      return getContext();
    }
  }

  /**
   * Attach parameterized resources
   * 
   * @param router
   *          the router
   */
  public final void attachParameterizedResources(Router router) {

    ResourcePluginStoreInterface resourceStore = (ResourcePluginStoreInterface) this.getPluginStore();
    if (resourceStore == null) {
      getLogger().warning("ResourcePluginStoreInterface not found");
      return;
    }

    List<ResourceModel> resList = resourceStore.retrieveByParent(this.getId());
    if (resList != null) {
      // clear the list of resources attached to the application
      modelMap.clear();
      for (ResourceModel resDto : resList) {
        if (resDto.getApplicationClassName() != null && !resDto.getApplicationClassName().equals("")) {
          Class<?> className = null;
          try {
            className = Class.forName(resDto.getApplicationClassName());
          }
          catch (ClassNotFoundException e) {
            getLogger().warning("Application class : " + resDto.getApplicationClassName() + " not found");
          }

          if (!className.isAssignableFrom(this.getClass())) {
            getLogger().warning(
                "Resource : " + resDto.getApplicationClassName() + " is not compatible with a "
                    + this.getClass().getName() + " application class");
          }
        }
        // Finder attached to router
        Restlet finder = null;
        if (resDto != null && resDto.getParent().equals(this.getId())) {
          for (ResourceParameter parameter : resDto.getParametersMap().values()) {
            if (parameter.getType().equals(ResourceParameterType.PARAMETER_ATTACHMENT)) {
              try {
                modelMap.put(parameter.getValue(), resDto);
                TemplateRoute route = router.attach(parameter.getValue(), (Class<? extends ServerResource>) Class.forName(resDto.getResourceClassName()));
                Context ctx = route.getContext().createChildContext();
                ctx.setAttributes(route.getContext().getAttributes());
                ctx.getAttributes().put(ContextAttributes.RESOURCE_ATTACHMENT, parameter.getValue());
                finder = route.getNext();
                finderMap.put(parameter.getValue(), finder);
                finder.setContext(ctx);
              }
              catch (ClassNotFoundException e) {
                getLogger().warning(e.getMessage());
              }
            }
          }
        }
      }
    }
  }

  /**
   * 
   * Method to detach the resource from the application
   * 
   * @param resource
   *          the resource to detach
   * 
   *          Warning: This method is not used. When a resource is POST / PUT / DELETE the application is entirely
   *          restarted with the new complete list of dynamic resources.
   */
  public final void detachParameterizedResource(SitoolsParameterizedResource resource) {
    // retrieve attach ref and finder for the resource
    ResourceModel resDto = resource.getModel();
    if (resDto != null && resDto.getParent().equals(this.getId())) {
      for (ResourceParameter parameter : resDto.getParametersMap().values()) {
        if (parameter.getType().equals(ResourceParameterType.PARAMETER_ATTACHMENT)) {

          // detach the finder of the resource attached to the router
          getRouter().detach(finderMap.get(parameter.getValue()));

          // remove resource references from the maps
          modelMap.remove(parameter.getValue());
          finderMap.remove(parameter.getValue());
        }
      }
    }
  }

  /**
   * Gets the pluginStore value
   * 
   * @return the pluginStore
   */
  @SuppressWarnings("unchecked")
  public final ResourcePluginStoreInterface getPluginStore() {
    return (ResourcePluginStoreInterface) getSettings().getStores().get(Consts.APP_STORE_PLUGINS_RESOURCES);
  }

  /**
   * Get model by reference
   * 
   * @param ref
   *          the reference
   * @return the model
   */
  public ResourceModel getModel(String ref) {
    return modelMap.get(ref);
  }

  /**
   * Get the whole model map
   * 
   * @return the whole model map
   */
  public Map<String, ResourceModel> getModelMap() {
    return modelMap;
  }

  // /**
  // * Gets the modelMap value
  // *
  // * @return the modelMap
  // */
  // private final Map<String, ResourceModel> getModelMap() {
  // return modelMap;
  // }

  /**
   * Sets the value of modelMap
   * 
   * @param modelMap
   *          the modelMap to set
   */
  public final void setModelMap(Map<String, ResourceModel> modelMap) {
    this.modelMap = modelMap;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    result.getRepresentations().addAll(SitoolsApplication.representationInfos.values());
    return result;
  }

}
