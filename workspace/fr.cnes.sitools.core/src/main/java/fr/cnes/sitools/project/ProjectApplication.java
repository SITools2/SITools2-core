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

import java.util.Date;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.feeds.FeedsClientResource;
import fr.cnes.sitools.plugins.resources.ListPluginExpositionResource;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Application for exposing projects (one instance of ProjectApplication per Project)
 * 
 * TODO Constructor with all generic security configuration (Authenticator informations)
 * 
 * @author AKKA
 * 
 */
public final class ProjectApplication extends AbstractProjectApplication {

  /** The project model object */
  private Project project = null;

  /**
   * Default constructor
   * 
   * @param context
   *          the context
   */
  public ProjectApplication(Context context) {
    super(context);
  }

  /**
   * Constructor with a DataSet id
   * 
   * @param context
   *          Restlet Host context
   * @param projectId
   *          project identifier
   */
  public ProjectApplication(Context context, String projectId) {
    super(context, projectId);

    project = getStore().retrieve(projectId);

    // Description de cette instance projectId d'application.
    sitoolsDescribe();
    register();
  }

  @Override
  public void sitoolsDescribe() {
    // si appelé par le constructeur parent => projectId encore null ici
    if (project == null) {
      setName("projectApplication");
      setDescription("Exposition du projet");
    }
    else {
      setId(project.getId());
      setName(project.getName());
      setDescription("Exposition du project " + project.getName());
    }
    setCategory(Category.USER_DYNAMIC);
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    router.attachDefault(ProjectExpositionResource.class);
    // datasets
    router.attach("/datasets", ProjectListDatasetsResource.class);
    // forms
    router.attach("/forms", ProjectListFormsResource.class);
    // opensearch
    router.attach("/opensearch", ProjectListOpensearchResource.class);
    // graphs
    router.attach("/graph", ProjectGraphExpositionResource.class);
    // List feeds
    router.attach("/feeds", ProjectListFeedsResource.class);
    // IHM components list ????

    // resource for rss or atom feeds
    router.attach("/clientFeeds/{feedsId}", FeedsClientResource.class);

    // List of resources
    router.attach("/services", ListPluginExpositionResource.class);

    router.attach("/formsProject", ProjectListFormsProjectResource.class);

    // List of modules
    String target = RIAPUtils.getRiapBase() + getSettings().getString(Consts.APP_PROJECTS_MODULES_URL)
        + "/listComponents.json";
    Redirector redirector = new Redirector(getContext(), target);
    router.attach("/listComponents.json", redirector);

    // list of projectModuleModel from store => Actually used
    router.attach("/projectModules", ProjectListProjectModulesResource.class);

    // list of datasetViews from datasets used in the project
    router.attach("/datasetViews", ProjectListDatasetViewsResource.class);

    // list of guiservices from datasets used in the project
    router.attach("/guiServices", ProjectListGuiServicesResource.class);

    // attach dynamic resources
    attachParameterizedResources(router);

    return router;
  }

  @Override
  public void attachProject(Project ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachProject(Project ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void attachProject(Project ds, boolean synchro) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachProject(Project ds, boolean synchro) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachProjectDefinitif(Project ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachProjectDefinitif(Project ds, boolean synchro) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public synchronized void start() throws Exception {
    super.start();
    boolean isSynchro = getIsSynchro();
    if (isStarted()) {
      Project proj = getStore().retrieve(getId());
      if (proj != null) {
        if (!isSynchro) {
          proj.setStatus("ACTIVE");
          proj.setLastStatusUpdate(new Date());
          this.project = proj;
          getStore().update(proj);
        }
      }
    }
    else {
      getLogger().warning("ProjectApplication should be started.");
      Project proj = getStore().retrieve(getId());
      if (proj != null) {
        if (!isSynchro) {
          proj.setStatus("INACTIVE");
          proj.setLastStatusUpdate(new Date());
          getStore().update(proj);
        }
      }
    }
  }

  @Override
  public synchronized void stop() throws Exception {
    super.stop();
    boolean isSynchro = getIsSynchro();
    if (isStopped()) {
      Project proj = getStore().retrieve(getId());
      if (proj != null) {
        if (!isSynchro) {
          proj.setStatus("INACTIVE");
          proj.setLastStatusUpdate(new Date());
          getStore().update(proj);
        }
      }
    }
    else {
      getLogger().warning("ProjectApplication should be stopped.");
      Project proj = getStore().retrieve(getId());
      if (proj != null) {
        if (!isSynchro) {
          proj.setStatus("ACTIVE");
          proj.setLastStatusUpdate(new Date());
          this.project = proj;
          getStore().update(proj);
        }
      }
    }

  }

  /**
   * Return true if the application is in synchro mode, false otherwise
   * 
   * @return true if the application is in synchro mode, false otherwise
   */
  private boolean getIsSynchro() {
    Object dontUpdateStatusDate = getContext().getAttributes().get("IS_SYNCHRO");
    if (dontUpdateStatusDate == null) {
      return false;
    }
    return ((Boolean) dontUpdateStatusDate);
  }

  /**
   * Gets the project value
   * 
   * @return the project
   */
  public Project getProject() {
    return project;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Project application to simply obtain its objects in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
