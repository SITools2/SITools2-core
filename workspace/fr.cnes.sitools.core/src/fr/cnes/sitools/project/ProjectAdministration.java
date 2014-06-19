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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import com.google.common.io.Files;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotifierFilter;
import fr.cnes.sitools.project.graph.GraphNotificationResource;
import fr.cnes.sitools.project.graph.GraphResource;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.watch.ProjectsWatchServiceRunnable;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.watch.ScheduledThreadWatchService;

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

  /** lastProjectRefresh timestamp */
  private long lastProjectRefresh = 0;

  /** The service used for synchronization */
  private ScheduledThreadWatchService scheduledService;

  /** The period of time before each refresh */
  private int projectRefreshPeriod;

  /** The number of threads used to check for refresh */
  private int projectRefreshThreads;

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

    if (isAppsRefreshSync()) {
      setProjectsRefreshThreads(Integer.parseInt(getSettings().getString("Starter.APPS_REFRESH_THREADS")));
      setProjectsRefreshPeriod(Integer.parseInt(getSettings().getString("Starter.APPS_REFRESH_PERIOD")));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#start()
   */
  @Override
  public synchronized void start() throws Exception {
    if (isStopped()) {
      if (isAppsRefreshSync()) {
        // set the rolesLastModified
        File appFile = getProjectsEventFile();
        if (appFile != null && appFile.exists()) {
          setLastProjectsRefresh(appFile.lastModified());
        }

        ProjectsWatchServiceRunnable runnable = new ProjectsWatchServiceRunnable(getSettings(), this);

        scheduledService = new ScheduledThreadWatchService(runnable, this.projectRefreshThreads,
            this.projectRefreshPeriod, TimeUnit.SECONDS);
        scheduledService.start();
      }
    }
    super.start();
  }

  /**
   * True is sitools is in Application refresh mode, false otherwise
   * 
   * @return True is sitools is in Application refresh mode, false otherwise
   */
  private boolean isAppsRefreshSync() {
    return Boolean.parseBoolean(getSettings().getString("Starter.APPS_REFRESH", "false"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#stop()
   */
  @Override
  public synchronized void stop() throws Exception {
    if (isStarted() && isAppsRefreshSync()) {
      scheduledService.stop();
    }
    super.stop();
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

  @Override
  public void attachProject(Project proj, boolean isSynchro) {
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
    appContext.getAttributes().put("IS_SYNCHRO", new Boolean(isSynchro));

    appContext.getAttributes().put(ContextAttributes.COOKIE_AUTHENTICATION, Boolean.TRUE);

    ProjectApplication proja = new ProjectApplication(appContext, proj.getId());

    getSettings().getAppRegistry().attachApplication(proja);
    if (!isSynchro) {
      updateProjectsLastModified();
    }
  }

  /**
   * Create and attach a ProjectApplication according to the given Project object
   * 
   * @param proj
   *          Project object
   */
  @Override
  public void attachProject(Project proj) {
    attachProject(proj, false);
  }

  /**
   * Detach the ProjectApplication according to the given Project object
   * 
   * @param proj
   *          Project object
   */
  @Override
  public void detachProject(Project proj) {
    detachProject(proj, false);
  }

  @Override
  public void detachProject(Project project, boolean isSynchro) {
    ProjectApplication proja = (ProjectApplication) getSettings().getAppRegistry().getApplication(project.getId());
    getSettings().getAppRegistry().detachApplication(proja);
    if (!isSynchro) {
      updateProjectsLastModified();
    }
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

  /**
   * Gets the lastApplicationRefresh value
   * 
   * @return the lastApplicationRefresh
   */
  public long getLastProjectsRefresh() {
    return lastProjectRefresh;
  }

  /**
   * Sets the value of lastApplicationRefresh
   * 
   * @param lastApplicationRefresh
   *          the lastApplicationRefresh to set
   */
  public void setLastProjectsRefresh(long lastApplicationRefresh) {
    this.lastProjectRefresh = lastApplicationRefresh;
  }

  /**
   * Get the projectEventFile
   * 
   * @return the projectEventFile
   */
  public File getProjectsEventFile() {
    String fileUrl = getSettings().getStoreDIR("Starter.PROJECTS_EVENT_FILE");
    File file = new File(fileUrl);
    return file;
  }

  /**
   * Get the number of threads to keep in the scheduled pool (ScheduledThreadPoolExecutor)
   * 
   * @return applicationRefreshThreads
   */
  public int getProjectsRefreshThreads() {
    return projectRefreshThreads;
  }

  /**
   * Sets the number of threads to keep in the scheduled pool (ScheduledThreadPoolExecutor)
   * 
   * @param projectsRefreshThreads
   *          , the number of threads to keep in the pool, even if they are idle
   */
  public void setProjectsRefreshThreads(int projectsRefreshThreads) {
    this.projectRefreshThreads = projectsRefreshThreads;
  }

  /**
   * Get the period between successive executions of the ScheduledThreadPoolExecutor
   * 
   * @return applicationRefreshPeriod
   */
  public int getProjectsRefreshPeriod() {
    return projectRefreshPeriod;
  }

  /**
   * Sets the period between successive executions of the ScheduledThreadPoolExecutor
   * 
   * @param projectsRefreshPeriod
   *          , the period between successive executions (in a time unit to be specified)
   */
  public void setProjectsRefreshPeriod(int projectsRefreshPeriod) {
    this.projectRefreshPeriod = projectsRefreshPeriod;
  }

  /**
   * updateUsersAndGroupsLastModified
   */
  public synchronized void updateProjectsLastModified() {
    if (isAppsRefreshSync()) {
      try {
        File file = getProjectsEventFile();
        Files.touch(file);
        // pour eviter un refresh de l'instance courante à chaque modification
        setLastProjectsRefresh(file.lastModified());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
