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
package fr.cnes.sitools.project;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotifierFilter;
import fr.cnes.sitools.project.graph.GraphNotificationResource;
import fr.cnes.sitools.project.graph.GraphResource;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;

/**
 * Application for managing projects Dependencies : Projects
 * 
 * TODO Constructor with all generic security configuration (Authenticator informations) Configure security application
 * by spring or from server main
 * 
 * @author AKKA
 * 
 */
public final class ProjectAdministration extends AbstractProjectApplication {

  /** host parent router */
  private Router parentRouter = null;

  /**
   * Category
   * 
   * @param context
   *          parent context
   */
  public ProjectAdministration(Context context) {
    super(context);
  }

  /**
   * Default constructor
   * 
   * @param parentRouter
   *          for ProjectApplication attachment
   * @param context
   *          the context
   */
  public ProjectAdministration(Router parentRouter, Context context) {
    super(context);
    this.parentRouter = parentRouter;

    Project[] projects = getStore().getArray();
    for (int i = 0; i < projects.length; i++) {
      if ("ACTIVE".equals(projects[i].getStatus())) {
        attachProject(projects[i]);
      }
    }
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("ProjectAdministration");
    setDescription("Project Management");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(ProjectCollectionResource.class);

    // attach dynamic resources
    attachParameterizedResources(router);

    router.attach("/{projectId}", ProjectResource.class);
    router.attach("/{projectId}/start", ActivationProjectResource.class);
    router.attach("/{projectId}/stop", ActivationProjectResource.class);
    router.attach("/{projectId}/startmaintenance", ActivationProjectResource.class);
    router.attach("/{projectId}/stopmaintenance", ActivationProjectResource.class);

    // graph resource
    router.attach("/{projectId}/graph", GraphResource.class);

    // graph resource
    router.attach("/{projectId}/datasets", ProjectDatasetCollectionResource.class);

    // notification des modifications de resources liées au projet
    router.attach("/{projectId}/notify", ProjectNotificationResource.class);
    router.attach("/{projectId}/graph/notify", GraphNotificationResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);

    return filter;
  }

  /**
   * Create and attach a ProjectApplication according to the given Project object
   * 
   * @param proj
   *          Project object
   */
  @Override
  public void attachProject(Project proj) {

    if ((proj.getSitoolsAttachementForUsers() == null) || proj.getSitoolsAttachementForUsers().equals("")) {
      proj.setSitoolsAttachementForUsers("/" + proj.getId());
      getStore().update(proj);
    }

    Context appContext = parentRouter.getContext().createChildContext();
    // Le register est fait explicitement dans le constructeur du
    // ProjectApplication une fois l'instance complètement initialisée
    appContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());
    appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
    appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, proj.getSitoolsAttachementForUsers());
    appContext.getAttributes().put(ContextAttributes.APP_ID, proj.getId());
    appContext.getAttributes().put(ContextAttributes.APP_STORE, getStore());
    appContext.getAttributes().put(Consts.APP_STORE_GRAPH, getGraphStore());
    appContext.getAttributes().put(ContextAttributes.LOG_TO_APP_LOGGER, Boolean.TRUE);

    appContext.getAttributes().put(ContextAttributes.COOKIE_AUTHENTICATION, Boolean.TRUE);

    ProjectApplication proja = new ProjectApplication(appContext, proj.getId());

    getSettings().getAppRegistry().attachApplication(proja);
  }

  /**
   * Detach the ProjectApplication according to the given Project object
   * 
   * @param proj
   *          Project object
   */
  @Override
  public void detachProject(Project proj) {
    ProjectApplication proja = (ProjectApplication) getSettings().getAppRegistry().getApplication(proj.getId());
    getSettings().getAppRegistry().detachApplication(proja);
  }

  /**
   * Detach the ProjectApplication according to the given Project object
   * 
   * @param proj
   *          Project object
   */
  public void detachProjectDefinitif(Project proj) {
    ProjectApplication proja = (ProjectApplication) getSettings().getAppRegistry().getApplication(proj.getId());
    if (proja != null) {
      getSettings().getAppRegistry().detachApplication(proja);
      proja.unregister();
    }
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo(
        "Project administration application to deal with projects on admin side in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  @Override
  public void detachProjectDefinitif(Project ds, boolean isSynchro) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void detachProject(Project project, boolean isSynchro) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void attachProject(Project project, boolean isSynchro) {
    // TODO Auto-generated method stub
    
  }

}
