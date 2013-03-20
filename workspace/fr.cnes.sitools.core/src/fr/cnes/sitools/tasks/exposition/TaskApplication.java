/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.tasks.exposition;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.business.TaskManager;

/**
 * Application to manage Tasks
 * 
 * 
 * @author m.gond
 */
public class TaskApplication extends SitoolsApplication {
  /** request attribute for user identifier */
  private static final String USER_ATTRIBUTE = "identifier";

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  public TaskApplication(Context context) {
    super(context);

    context.getAttributes().put(Consts.APP_STORE_TASK, context.getAttributes().get(ContextAttributes.APP_STORE));
    // Initialisation du TaskManager
    TaskManager.getInstance().init(context);

    // this.store = (SitoolsStore<TaskModel>) context.getAttributes().get(Consts.APP_STORE_TASK);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("TaskApplication");
    setDescription("Expose the list of tasks");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attachDefault(TaskCollectionResource.class);
    router.attach("/{taskId}", TaskResource.class);
    router.attach("/{taskId}/represent", TaskRepresentResource.class);
    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Task exposition application");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
