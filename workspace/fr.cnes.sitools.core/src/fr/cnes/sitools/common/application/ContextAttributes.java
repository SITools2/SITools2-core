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
package fr.cnes.sitools.common.application;

/**
 * Commons attributes for SitoolsApplications
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public interface ContextAttributes {

  /** ----------------------------------- */
  /** Context attributes for applications */

  /** Attribute String for SitoolsSettings object */
  String SETTINGS = "SETTINGS";

  /** Attribute String for application attachment to host component */
  String APP_ATTACH_REF = "APP_ATTACH_REF";

  /** Attribute boolean for application registering in AppManager */
  String APP_REGISTER = "APP_REGISTER";

  /** Attribute String for Application ID - must be set specially for dynamic applications */
  String APP_ID = "APP_ID";

  /** Attribute object for specific application Store */
  String APP_STORE = "APP_STORE";

  /** Attribute object for SitoolsRealm */
  String APP_REALM = "APP_REALM";

  /** Host name */
  String HOST_NAME = "HOST_NAME";

  /** Host port */
  String HOST_PORT = "HOST_PORT";

  /** Public host name */
  String PUBLIC_HOST_NAME = "PUBLIC_HOST_NAME";

  /** Proxy host */
  String PROXY_HOST = "PROXY_HOST";

  /** Proxy port */
  String PROXY_PORT = "PROXY_PORT";

  /** Proxy user name */
  String PROXY_USER = "PROXY_USER";

  /** Proxy user password */
  String PROXY_PASSWORD = "PROXY_PASSWORD";

  /** Proxy host */
  String NONPROXY_HOSTS = "NONPROXY_HOSTS";

  /** Attribute for temporary folder */
  String TEMPORARY_FOLDER = "TEMPORARY_FOLDER";

  /** Attribute for authorizing or not cookie authentication */
  String COOKIE_AUTHENTICATION = "COOKIE_AUTHENTICATION";

  /** Attribute for plugins resources to retrieve model by attachment */
  String RESOURCE_ATTACHMENT = "RES_ATTACH_REF";

  /** Attribute for a list of SitoolsProperty */
  String LIST_SITOOLS_PROPERTIES = "LIST_SITOOLS_PROPERTIES";

  /** True to Log to the Application Logger, false otherwise */
  String LOG_TO_APP_LOGGER = "LOG_TO_APP_LOGGER";

  /** True not to use the status service when the response is an error, false otherwise */
  String NO_STATUS_SERVICE = "NO_STATUS_SERVICE";

  Object START_WITH_MIGRATION = "START_WITH_MIGRATION";

}
