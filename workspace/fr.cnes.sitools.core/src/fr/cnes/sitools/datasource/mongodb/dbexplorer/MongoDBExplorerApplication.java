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
package fr.cnes.sitools.datasource.mongodb.dbexplorer;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;

/**
 * Finder mapping a database resource
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class MongoDBExplorerApplication extends SitoolsApplication {

  /**
   * Context key
   */
  public static final String CONTEXT_KEY = MongoDBExplorerApplication.class.getCanonicalName();

  /**
   * Real "resources" are databases
   */

  private volatile SitoolsMongoDBDataSource dataSource;

  /** Target class */
  private Class<? extends ServerResource> targetClass = null;

  /**
   * -------------------------------------------------------------------------- --------------------
   */

  /**
   * Constructor
   * 
   * @param context
   *          RESTlet Host Context
   * @param dataSource
   *          SQL DataSource
   */
  public MongoDBExplorerApplication(Context context, SitoolsMongoDBDataSource dataSource) {
    super(context);
    this.dataSource = dataSource;
    configure();

    setName("DBExplorerApplication for datasource " + dataSource.getDsModel().getName());

    register();
  }

  /**
   * configure the application
   */
  private void configure() {

    setTargetClass(MongoDBExplorerResource.class);
  }

  /**
   * -------------------------------------------------------------------------- -----------------------
   */

  /**
   * Get the DataSource
   * 
   * @return the DataSource
   */
  public SitoolsMongoDBDataSource getDataSource() {
    return dataSource;
  }

  /**
   * -------------------------------------------------------------------------- -----------------------
   */

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // with no schema (depends on database.schemaOnConnection if given)
    router.attachDefault(targetClass);
    router.attach("/collections", targetClass);

    // with schema (depends on database.schemaOnConnection if given)
    router.attach("/collections/{collectionName}", targetClass);
    router.attach("/collections/{collectionName}/records", targetClass);
    router.attach("/collections/{collectionName}/records/{_id}", targetClass);
    router.attach("/collections/{collectionName}/metadata", targetClass);

    return router;
  }

  /**
   * Like Finder
   * 
   * @param target
   *          the target to set
   */
  private void setTargetClass(Class<? extends ServerResource> target) {
    this.targetClass = target;
  }

  @Override
  public void sitoolsDescribe() {
    setName("DBExplorerApplication");
    setDescription("Finder mapping a database resource.");
    setCategory(Category.ADMIN_DYNAMIC);
  }

}
