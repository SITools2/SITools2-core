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
package fr.cnes.sitools.userstorage;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.proxy.DirectoryUser;

/**
 * Application for user storage directories.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UserStorageApplication extends SitoolsApplication {

  /** request attribute for user identifier */
  private static final String USER_ATTRIBUTE = "identifier";

  /** Root directory for user accounts (home) */
  private String rootDirectory;

  /** User accounts database with quota constraints and status */
  private UserStorageStore store = null;

  /**
   * Constructor
   * 
   * @param context
   *          restlet application context
   */
  public UserStorageApplication(Context context) {
    super(context);
    this.store = (UserStorageStore) context.getAttributes().get(ContextAttributes.APP_STORE);
    this.rootDirectory = (String) context.getAttributes().get("USER_STORAGE_ROOT");
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("UserStorageApplication");
    setDescription("Access to users space disk on server\n" + "The security on this application is a bit different\n"
        + "-> The owner of the userstorage has all rights on its userstorage\n"
        + "-> Otherwise the application authorizations are used");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {

    Router inboundRouter = new Router(getContext());

    DirectoryUser userDir = new DirectoryUser(getContext(), "file:///"
        + getSettings().getFormattedString(rootDirectory), getAttachementRef(), USER_ATTRIBUTE);
    userDir.setListingAllowed(true);
    userDir.setModifiable(true);
    userDir.setDeeplyAccessible(true);
    userDir.setDescription("Exposition of all user storage directories.");

    // To allow listing of all subdirectories for all users
    inboundRouter.attach("/files", userDir);
    inboundRouter.attach("/status", UserStorageStatusResource.class);

    return inboundRouter;
  }

  /**
   * Secure application for specific user access in addition of default configured authorization
   * 
   * @return Restlet
   */
  @Override
  public Restlet getSecure() {
    // GET authorized for public userstorage
    List<Method> methods = new ArrayList<Method>();
    methods.add(Method.GET);
    return addSecurity(this, USER_ATTRIBUTE, methods);
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public UserStorageStore getStore() {
    return store;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public void setStore(UserStorageStore store) {
    this.store = store;
  }

}
