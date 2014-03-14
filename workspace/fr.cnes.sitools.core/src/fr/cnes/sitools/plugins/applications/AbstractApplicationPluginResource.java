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
package fr.cnes.sitools.plugins.applications;

/**
 * Abstract Resource for Resource attached to the ApplicationPluginApplication Gives access to the store
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractApplicationPluginResource extends AbstractApplicationPluginCommonResource {
  /** parent application */
  private ApplicationPluginApplication application = null;

  /** store */
  private ApplicationPluginStore store = null;

  /** appId */
  private String appId;

  @Override
  protected void doInit() {
    super.doInit();

    application = (ApplicationPluginApplication) getApplication();
    store = application.getStore();

    this.appId = (String) this.getRequest().getAttributes().get("applicationPluginId");

  }

  /**
   * Get the application ID
   * 
   * @return the ID
   */
  public final String getAppId() {
    return this.appId;
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store
   */
  public final ApplicationPluginStore getStore() {
    return this.store;
  }

  /**
   * Get the application handling this resource
   * 
   * @return the application
   */
  public final ApplicationPluginApplication getResourceApplication() {
    return this.application;
  }
}
