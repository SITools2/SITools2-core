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

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;

/**
 * Abstract Class for ProjectApplications
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class AbstractProjectApplication extends SitoolsParameterizedApplication {

  /** Project id */
  private String projectId = null;

  /** Store */
  private SitoolsStore<Project> store = null;

  /** store for graph **/
  private SitoolsStore<Graph> graphStore = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  @SuppressWarnings("unchecked")
  public AbstractProjectApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<Project>) context.getAttributes().get(ContextAttributes.APP_STORE);
    this.graphStore = (SitoolsStore<Graph>) context.getAttributes().get(Consts.APP_STORE_GRAPH);
  }

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   * @param projectId
   *          the project identifier
   */
  @SuppressWarnings("unchecked")
  public AbstractProjectApplication(Context context, String projectId) {
    super(context);
    this.projectId = projectId;
    this.store = (SitoolsStore<Project>) context.getAttributes().get(ContextAttributes.APP_STORE);
    this.graphStore = (SitoolsStore<Graph>) context.getAttributes().get(Consts.APP_STORE_GRAPH);
    setCategory(Category.USER);
  }

  /**
   * Gets the project store value
   * 
   * @return the ProjectStore
   */
  public final SitoolsStore<Project> getStore() {
    return store;
  }

  /**
   * Sets the value of graphStore
   * 
   * @param graphStore
   *          the graphStore to set
   */
  public final void setGraphStore(SitoolsStore<Graph> graphStore) {
    this.graphStore = graphStore;
  }

  /**
   * Gets the graphStore value
   * 
   * @return the graphStore
   */
  public final SitoolsStore<Graph> getGraphStore() {
    return graphStore;
  }

  /**
   * Create and attach a new ProjectApplication
   * 
   * @param ds
   *          Project object
   */
  public abstract void attachProject(Project ds);

  /**
   * Detach the ProjectApplication corresponding with the Project given object
   * 
   * @param ds
   *          Project object
   */
  public abstract void detachProject(Project ds);

  /**
   * Detach the ProjectApplication corresponding with the Project given object
   * 
   * @param ds
   *          Project object
   */
  public abstract void detachProjectDefinitif(Project ds);

  /**
   * Gets the projectId value
   * 
   * @return the projectId
   */
  public final String getProjectId() {
    return projectId;
  }

}
