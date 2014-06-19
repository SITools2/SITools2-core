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
package fr.cnes.sitools.project.watch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.project.ProjectAdministration;
import fr.cnes.sitools.project.ProjectApplication;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface;

/**
 * The Class UserAndGroupWatchServiceRunnable.
 * 
 * @author m.gond (AKKA Technologies)
 */
public class ProjectsWatchServiceRunnable implements SitoolsWatchServiceRunnableInterface {

  /** The settings. */
  private SitoolsSettings settings;

  /** ProjectAdministration */
  private ProjectAdministration projectApp;

  /**
   * Instantiates a new user and group watch service runnable.
   * 
   * @param settings
   *          the settings
   * @param app
   *          the projectAdministration
   */
  public ProjectsWatchServiceRunnable(SitoolsSettings settings, ProjectAdministration app) {
    this.settings = settings;
    this.projectApp = app;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface#execute()
   */
  @Override
  public void execute() {
    try {
      File appFileEvent = this.projectApp.getEventFile();

      long lastProjectRefresh = this.projectApp.getLastRefresh();

      if (appFileEvent != null && appFileEvent.lastModified() > lastProjectRefresh) {
        System.out.println("c'est parti pour le refresh des projets");

        Map<String, ProjectApplication> projectApps = getAllProjectApplication();

        ProjectStoreInterface store = this.projectApp.getStore();
        List<Project> projects = store.getList();
        for (Project project : projects) {
          System.out.println("PROJECT : " + project.getName() + " project.getLastStatusUpdate() : "
              + project.getLastStatusUpdate());
          if (project.getLastStatusUpdate() != null && project.getLastStatusUpdate().getTime() > lastProjectRefresh) {

            if ("ACTIVE".equals(project.getStatus())) {
              System.out.println("ACTIVE " + project.getName());
              projectApp.detachProject(project, true);
              projectApp.attachProject(project, true);
            }
            else {
              System.out.println("INACTIVE " + project.getName());
              projectApp.detachProject(project, true);
            }

          }
        }
        this.projectApp.setLastRefresh(appFileEvent.lastModified());

        // remove the datasets that doesn't exists anymore
        for (ProjectApplication app : projectApps.values()) {
          System.out.println("DETACH DEFINITIVELY " + app.getProject().getName());
          projectApp.detachProjectDefinitif(app.getProject(), true);
        }
      }

    }
    catch (Exception e) {
      Engine.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,
          "Error while watching for changing files", e);
    }

  }

  /**
   * Gets all the ProjectApplication in the appRegistry
   * 
   * @return all the ProjectApplication in the appRegistry
   */
  private Map<String, ProjectApplication> getAllProjectApplication() {
    Map<String, SitoolsApplication> allApps = getSettings().getAppRegistry().getApplications();
    Map<String, ProjectApplication> datasetApps = new HashMap<String, ProjectApplication>();
    for (SitoolsApplication app : allApps.values()) {
      if (app instanceof ProjectApplication) {
        datasetApps.put(app.getId(), (ProjectApplication) app);
      }
    }
    return datasetApps;
  }

  /**
   * Gets the settings.
   * 
   * @return the settings
   */
  private SitoolsSettings getSettings() {
    return settings;
  }

}
