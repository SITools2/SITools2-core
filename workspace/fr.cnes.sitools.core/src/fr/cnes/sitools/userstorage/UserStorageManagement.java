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
package fr.cnes.sitools.userstorage;

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

/**
 * Application for User accounts Administration
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UserStorageManagement extends SitoolsApplication {

  /** request attribute for user account */
  private static final String USER_ATTRIBUTE = "identifier";

  /** Root directory for user accounts (UNIX home) */
  private String rootDirectory;

  /** User accounts database */
  private UserStorageStoreInterface store = null;
  
  /**
   * Constructor
   * 
   * @param context
   *          restlet application context
   */
  public UserStorageManagement(Context context) {
    super(context);
    rootDirectory = (String) context.getAttributes().get("USER_STORAGE_ROOT");
    this.store = (UserStorageStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
    
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("UserStorageManagement");
    setDescription("Management of user space disk on server");
  }

  /**
   * Gets the rootDirectory value
   * @return the rootDirectory
   */
  public String getRootDirectory() {
    return rootDirectory;
  }
  
  @Override
  public Restlet createInboundRoot() {
    
    Router router = new Router(getContext());
    
    router.attachDefault(UserStorageConfigurationResource.class);
    router.attach("/users", UserStorageCollectionResource.class);
    router.attach("/users/{" + USER_ATTRIBUTE + "}", UserStorageResource.class);
    router.attach("/users/{" + USER_ATTRIBUTE + "}/{action}", UserStorageActionResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public UserStorageStoreInterface getStore() {
    return store;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public void setStore(UserStorageStoreInterface store) {
    this.store = store;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("User storage plug-in management.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
