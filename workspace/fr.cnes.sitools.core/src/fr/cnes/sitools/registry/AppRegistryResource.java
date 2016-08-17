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
package fr.cnes.sitools.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.Application;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreInterface;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.registry.model.AppRegistry;

/**
 * Resource for application registration
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AppRegistryResource extends AppRegistryAbstractResource {

  @Override
  public void sitoolsDescribe() {
    setName("AppRegistryResource");
    setDescription("Resource for application registering");
    this.setNegotiated(false);
  }

  /**
   * Gets all registered applications
   * 
   * @param variant
   *          required Variant (if negotiated)
   * @return Representation
   */
  @Get
  public Representation getApplications(Variant variant) {
    Response response = null;
    try {
      AppRegistry resourceManager = getAppRegistryApplication().getResourceManager();
      List<Resource> resources = resourceManager.getResources();

      String customizable = getRequest().getResourceRef().getQueryAsForm().getFirstValue("customizable");
      boolean bCustomizable = false;
      if ((customizable != null) && customizable.equals("true")) {
        bCustomizable = true;
      }

      if (getResourceId() != null) {
        for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
          Resource resource = (Resource) iterator.next();
          if (resource.getId().equals(getResourceId())) {
            Application app = getAppRegistryApplication().getApplications().get(resource.getId());

            if (app != null) {
              if (app.isStarted()) {
                resource.setStatus("ACTIVE");
              }
              else {
                resource.setStatus("INACTIVE");
              }
            }

            // For SitoolsParameterizedApplications marked customizable
            if (bCustomizable && (app instanceof SitoolsParameterizedApplication)) {
              trace(Level.INFO, "Cannot edit application - id: " + getApplication());
              response = null;
            }
            else {
              trace(Level.INFO, "Edit application " + resource.getName());
              response = new Response(true, resource, Resource.class, "application");
            }
            break;
          }
        }
        if (response == null) {
          trace(Level.INFO, "Cannot edit application - id: " + getApplication());
          response = new Response(false, "application.notfound");
        }
      }
      else {
        ResourceCollectionFilter rf = new ResourceCollectionFilter(getRequest());
        resources = getList(rf, resources);
        int total = resources.size();
        resources = getPage(rf, resources);

        // Informations complémentaires sur les applications
        for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
          Resource resource = (Resource) iterator.next();

          String idApplication = resource.getId();
          Application app = getAppRegistryApplication().getApplications().get(idApplication);

          if (app != null) {
            if (app.isStarted()) {
              resource.setStatus("ACTIVE");
            }
            else {
              resource.setStatus("INACTIVE");
            }

            // if app != null && only return customizable : return this resource if app is a
            // SitoolsParameterizedApplication
            if (bCustomizable && !(app instanceof SitoolsParameterizedApplication)) {
              iterator.remove();
            }
          }
          else {
            // if app null && only return customizable : do not return this resource
            if (bCustomizable) {
              iterator.remove();
            }
          }
        }
        total = resources.size();
        trace(Level.FINE, "View available applications");
        response = new Response(true, resources, Resource.class, "applications");
        response.setTotal(total);
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view applications");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view applications");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to obtain the list of registered applications");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramResourceId = new ParameterInfo("resourceId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource to get.");
    info.getRequest().getParameters().add(paramResourceId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Register an application SitoolsApplication auto-registration
   * 
   * @param representation
   *          Representation of a resource
   * @param variant
   *          required Variant (if negotiated)
   * @return Representation
   */
  @Post
  public Representation register(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "RESOURCE_REPRESENTATION_REQUIRED");
    }
    try {
      Resource resourceInput = null;
      if (MediaType.APPLICATION_JAVA_OBJECT.isCompatible(representation.getMediaType())) {
        // ObjectRepresentation
        if (representation instanceof ObjectRepresentation<?>) {
          Object object = ((ObjectRepresentation<?>) representation).getObject();
          if (object instanceof Resource) {
            resourceInput = (Resource) object;
          }
        }
      }
      else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        resourceInput = new XstreamRepresentation<Resource>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        resourceInput = new JacksonRepresentation<Resource>(representation, Resource.class).getObject();
      }

      // BAD REQUEST
      if (resourceInput == null) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Resource representation required");
      }

      // Business service
      if ((resourceInput.getId() == null) || resourceInput.getId().equals("")) {
        // Nouvel element sans id
        getApplication().getLogger().warning("APP_REGISTRY_RESOURCE_WITH_NO_ID");
        resourceInput.setId(UUID.randomUUID().toString());
        AppRegistry resourceManager = getAppRegistryApplication().getResourceManager();
        resourceManager.getResources().add(resourceInput);

        resourceManager.setLastUpdate(new Date().toString());
        getStore().update(resourceManager);
      }
      else {
        AppRegistry resourceManager = getAppRegistryApplication().getResourceManager();
        // controler que le la resource n'est pas deja dans le manager
        List<Resource> resources = resourceManager.getResources();
        for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
          Resource resource = (Resource) iterator.next();
          if ((resource.getId() != null) && (resource.getId().equals(resourceInput.getId()))) {
            // Mise a jour d'un element deja existant
            iterator.remove();
          }
          // Ou ajout d'un nouvel element avec son identifiant
        }
        resourceManager.getResources().add(resourceInput);
        resourceManager.setLastUpdate(new Date().toString());
        getStore().update(resourceManager);
      }

      return new ObjectRepresentation<Resource>(resourceInput);
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
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to add an application in the list of registered applications");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Unregister an application
   * @param representation Representation of a resource
   * @param variant required Variant (if negotiated)
   * @return Representation
   */
  @Delete
  public Representation unregister(Representation representation, Variant variant) {
    Response response = null;
    if (getResourceId() == null) {
      trace(Level.INFO, "Cannot delete the application");
      response = new Response(false, "RESOURCE_UNKNOWN");
    }
    else {
      ApplicationPluginModel appOutput = getPluginStore().retrieve(getResourceId());
      if (appOutput != null) {
        getPluginStore().delete(appOutput.getId());
      }
      AppRegistry resourceManager = getAppRegistryApplication().getResourceManager();
      boolean updated = false;
      List<Resource> resources = resourceManager.getResources();
      for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
        Resource resource = (Resource) iterator.next();
        if (resource.getId().equals(getResourceId())) {
          iterator.remove();
          updated = true;
        }

      }
      if (updated) {
        trace(Level.INFO, "Delete the application - id: " + getResourceId());
        getStore().update(resourceManager);
        response = new Response(true, "RESOURCE_DELETED");
      }
      else {
        trace(Level.INFO, "Cannot delete the application - id: " + getResourceId());
        response = new Response(false, "RESOURCE_UNKNOWN");
      }
    }

    // Response
    return getRepresentation(response, variant);
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete an application.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramResourceId = new ParameterInfo("resourceId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource to get.");
    info.getRequest().getParameters().add(paramResourceId);
    this.addStandardSimpleResponseInfo(info);
  }

  /**
   * Action on an application
   * @param representation Representation of a resource
   * @param variant required Variant (if negotiated)
   * @return Representation
   */
  @Put
  public synchronized Representation action(Representation representation, Variant variant) {
    Response response = null;
    try {
      do {
        if (getResourceId() == null) {
          trace(Level.INFO, "Cannot perform action on the application");
          response = new Response(false, "RESOURCE_UNKNOWN");
          break;
        }

        SitoolsApplication app = getAppRegistryApplication().getApplications().get(getResourceId());
        if (app == null) {
          trace(Level.INFO, "Cannot perform action on the application - id: " + getResourceId());
          response = new Response(false, "APPLICATION_INSTANCE_NOT_FOUND");
          break;
        }

        Form form = getRequest().getResourceRef().getQueryAsForm();
        Parameter param = form.getFirst("action");
        if (param == null) {
          int lastSlash = getReference().toString().lastIndexOf("/");
          String lastAction = getReference().toString().substring(lastSlash + 1);
          if (lastAction != null && !"".equals(lastAction)) {
            param = new Parameter("action", lastAction);
          }
        }

        if (param == null) {
          trace(Level.INFO, "Cannot perform action on the application - id: " + getResourceId());
          response = new Response(false, "APPLICATION_INSTANCE_ACTION_EXPECTED");
          break;
        }

        String value = param.getValue();

        if ("start".equals(value)) {
          synchronized (app) {
            if (app.isStarted()) {
              trace(Level.INFO, "Cannot start the application " + app.getName());
              response = new Response(false, "APPLICATION_ALREADY_STARTED");
              break;
            }
            getAppRegistryApplication().attachApplication(app);
            app.start();
            trace(Level.INFO, "Start the application " + app.getName());
            response = new Response(true, "APPLICATION_STARTED");
            break;
          }
        }

        if ("stop".equals(value)) {
          getAppRegistryApplication().detachApplication(app);
          app.stop();
          trace(Level.INFO, "Stop the application " + app.getName());
          response = new Response(true, "APPLICATION_STOPPED");
          break;
        }

        if ("restart".equals(value)) {
          getAppRegistryApplication().detachApplication(app);
          app.stop();
          wait(2000);
          getAppRegistryApplication().attachApplication(app);
          app.start();
          trace(Level.INFO, "Restart the application " + app.getName());
          response = new Response(true, "APPLICATION_RESTARTED");
          break;
        }

      } while (false);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot perform action on the application - id: " + getResourceId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot perform action on the application - id: " + getResourceId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

    // Response
    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to start/stop/restart a registered applications");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramResourceId = new ParameterInfo("resourceId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource to get.");
    info.getRequest().getParameters().add(paramResourceId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Filters a list by query and sorts it.
   * @param filter for Query
   * @param list source
   * @return List<Resource>
   */
  private List<Resource> getList(ResourceCollectionFilter filter, List<Resource> list) {
    List<Resource> result = new ArrayList<Resource>();
    if ((list == null) || (list.size() <= 0)) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (Resource resource : list) {
        if (null == resource.getName()) {
          continue;
        }
        if (resource.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          result.add(resource);
        }
      }
    }
    else {
      result.addAll(list);
    }

    // Tri
    sort(result, filter);

    return result;
  }

  /**
   * Filters a list according to the pagination
   * @param filter for pagination
   * @param result source
   * @return ArrayList<Resource>
   */
  private List<Resource> getPage(ResourceCollectionFilter filter, List<Resource> result) {
    if (result.size() == 0) {
      return result;
    }
    // Pagination
    int start = (filter.getStart() <= 0) ? 0 : filter.getStart() - 1;
    int limit = ((filter.getLimit() <= 0) || ((filter.getLimit() + start) > result.size())) ? (result.size() - start)
        : filter.getLimit();
    List<Resource> page = result.subList(start, start + limit); // pas -1
                                                                // puisque
                                                                // exclusive

    return new ArrayList<Resource>(page);
  }

  /**
   * Sort the list (by default on the name)
   * @param result list to be sorted
   * @param filter ResourceCollectionFilter with sort properties.
   */
  private void sort(List<Resource> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<IResource>(filter));
    }
  }

}
