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
package fr.cnes.sitools.datasource.jdbc.dbexplorer;

import java.util.Date;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.security.MemoryRealm;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.datasource.jdbc.JDBCDataSourceStoreInterface;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;

/**
 * Finder mapping a database resource
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class DBExplorerApplication extends SitoolsApplication {

  /**
   * Context key
   */
  public static final String CONTEXT_KEY = DBExplorerApplication.class.getCanonicalName();

  /**
   * Real "resources" are databases
   */

  private volatile SitoolsSQLDataSource dataSource;


  /** Target class */
  private Class<? extends ServerResource> targetClass = null;

  /**
   * REALMS
   */
  private volatile MemoryRealm realms;

  /**
   * Indicates if modifications to local resources are allowed (false by default).
   */
  private volatile boolean modifiable = false;

  /**
   * GRANT DDL Create/Read/Update/Delete rights on SQL database structure.
   */

  /**
   * GRANT DML CRUD rights on data of the SQL database. 
   * Possibly overloaded for each table.
   * TODO PUT pour modifier les operations permises
   */
  private volatile boolean selectAllowed = true;
  /**
   * Update granted
   */
  private volatile boolean updateAllowed = modifiable;
  /**
   * Insert granted
   */
  private volatile boolean insertAllowed = modifiable;
  /**
   * Delete granted
   */
  private volatile boolean deleteAllowed = modifiable;
  

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
  public DBExplorerApplication(Context context, SitoolsSQLDataSource dataSource) {
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

    this.selectAllowed = true;
    this.updateAllowed = false;
    this.deleteAllowed = false;
    this.insertAllowed = false;

    this.modifiable = false;

    setTargetClass(DBExplorerResource.class);
  }

  /**
   * -------------------------------------------------------------------------- -----------------------
   */


  /**
   * Check if select is allowed
   * 
   * @return boolean true if select is allowed
   */
  public boolean isSelectAllowed() {
    return this.selectAllowed;
  }

  /**
   * Gets the realms value
   * 
   * @return the realms
   */
  public MemoryRealm getRealms() {
    return realms;
  }

  /**
   * Sets the value of realms
   * 
   * @param realms
   *          the realms to set
   */
  public void setRealms(MemoryRealm realms) {
    this.realms = realms;
  }

  /**
   * Get the DataSource
   * 
   * @return the DataSource
   */
  public SitoolsSQLDataSource getDataSource() {
    return dataSource;
  }

  /**
   * Check if update is allowed
   * 
   * @return true if update is allowed
   */
  public boolean isUpdateAllowed() {
    return this.updateAllowed;
  }

  /**
   * Check if delete is allowed
   * 
   * @return true if delete is allowed
   */
  public boolean isDeleteAllowed() {
    return this.deleteAllowed;
  }

  /**
   * Check if insert is allowed
   * 
   * @return true if insert is allowed
   */
  public boolean isInsertAllowed() {
    return this.insertAllowed;
  }

  /**
   * Indicates if modifications to local resources (most likely files) are allowed. Returns false by default.
   * 
   * @return True if modifications to local resources are allowed.
   */
  public boolean isModifiable() {
    return this.modifiable;
  }


  /**
   * Set if select is allowed
   * 
   * @param selectAllowed
   *          value to set
   */
  public void setSelectAllowed(boolean selectAllowed) {
    this.selectAllowed = selectAllowed;
  }

  /**
   * Set if update is allowed
   * 
   * @param updateAllowed
   *          value to set
   */
  public void setUpdateAllowed(boolean updateAllowed) {
    this.updateAllowed = updateAllowed;
  }

  /**
   * Set if delete is allowed
   * 
   * @param deleteAllowed
   *          value to set
   */
  public void setDeleteAllowed(boolean deleteAllowed) {
    this.deleteAllowed = deleteAllowed;
  }

  /**
   * Set if insert is allowed
   * 
   * @param insertAllowed
   *          value to set
   */
  public void setInsertAllowed(boolean insertAllowed) {
    this.insertAllowed = insertAllowed;
  }

  /**
   * Indicates if modifications to local resources are allowed.
   * 
   * @param modifiable
   *          True if modifications to local resources are allowed.
   */
  public void setModifiable(boolean modifiable) {
    this.modifiable = modifiable;
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
    router.attach("/{tableName}", targetClass);
//    router.attach("/{tableName}/records", targetClass);
//    router.attach("/{tableName}/records/{record}", targetClass);

    // with schema (depends on database.schemaOnConnection if given)
    router.attach("/schemas/{schemaName}", targetClass);
    router.attach("/schemas/{schemaName}/tables", targetClass);
    router.attach("/schemas/{schemaName}/tables/{tableName}", targetClass);
//    router.attach("/schemas/{schemaName}/tables/{tableName}/records", targetClass);
//    router.attach("/schemas/{schemaName}/tables/{tableName}/records/{record}", targetClass);

    router.attach("/schemas/{schemaName}/tables/{tableName}/dataset", DataSetWrapperResource.class);

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
  
  @Override
  public synchronized void start() throws Exception {
    super.start();
    boolean isSynchro = getIsSynchro();
    JDBCDataSourceStoreInterface store = (JDBCDataSourceStoreInterface) getContext().getAttributes().get(
        ContextAttributes.APP_STORE);
    if (isStarted()) {
      JDBCDataSource datasource = store.retrieve(getId());
      if (datasource != null) {
        if (!isSynchro) {
          datasource.setStatus("ACTIVE");
          datasource.setLastStatusUpdate(new Date());
//          this.dataSource = datasource;
          store.update(datasource);
        }
      }
    }
    else {
      getLogger().warning("DBExplorerApplication should be started.");
      JDBCDataSource datasource = store.retrieve(getId());
      if (datasource != null) {
        if (!isSynchro) {
          datasource.setStatus("INACTIVE");
          datasource.setLastStatusUpdate(new Date());
          store.update(datasource);
        }
      }
    }
  }

  @Override
  public synchronized void stop() throws Exception {
    super.stop();
    boolean isSynchro = getIsSynchro();
    JDBCDataSourceStoreInterface store = (JDBCDataSourceStoreInterface) getContext().getAttributes().get(
        ContextAttributes.APP_STORE);
    if (isStopped()) {
      JDBCDataSource datasource = store.retrieve(getId());
      if (datasource != null) {
        if (!isSynchro) {
          datasource.setStatus("INACTIVE");
          datasource.setLastStatusUpdate(new Date());
          store.update(datasource);
        }
      }
    }
    else {
      getLogger().warning("DBExplorerApplication should be stopped.");
      JDBCDataSource datasource = store.retrieve(getId());
      if (datasource != null) {
        if (!isSynchro) {
          datasource.setStatus("ACTIVE");
          datasource.setLastStatusUpdate(new Date());
//          this.dataSource = datasource;
          store.update(datasource);
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
  



}
