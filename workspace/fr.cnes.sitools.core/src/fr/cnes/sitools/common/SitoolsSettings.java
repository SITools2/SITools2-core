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
package fr.cnes.sitools.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.authentication.SitoolsRealm;
import fr.cnes.sitools.server.Consts;

/**
 * Class for managing global settings
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsSettings {

  // STATIC --------------------------------------------------------------

  /** Default host port for HTTP server */
  public static final String DEFAULT_HOST_PORT = "8182";

  /** Default host name for HTTP server */
  public static final String DEFAULT_HOST_NAME = "localhost|sitools.cnes.fr";

  /** Default bundle name */
  public static final String DEFAULT_BUNDLE = "sitools";

  /** Multiple component bundle management */
  private static final ConcurrentHashMap<String, SitoolsSettings> SETTINGS = new ConcurrentHashMap<String, SitoolsSettings>();

  // PRIVATE --------------------------------------------------------------

  /** Resource bundle */
  private ResourceBundle bundle = null;

  /** Default Realm for SITools */
  private SitoolsRealm authenticationRealm = null;

  /** Secure */
  private boolean secured = true;

  /**
   * Total number of threads for all client connection
   * 
   * @see http://www.mail-archive.com/discuss@restlet.tigris.org/msg11952.html
   */
  private String maxTotalConnections; // = getString("Starter.maxTotalConnections");

  /**
   * Max number of threads for one client connection
   * 
   * @see http://www.mail-archive.com/discuss@restlet.tigris.org/msg11952.html
   */
  private String maxConnectionsPerHost; // = getString("Starter.maxConnectionsPerHost");

  /**
   * URL of the temporary folder
   */
  private String tmpFolderUrl;

  /** Root directory for application stores. NOT final for tests */
  private String storeDIR; // = getString("Starter.STORE_DIR");

  /** Authorization Domain for BASIC / DIGEST Authentication */
  private String authenticationDOMAIN; // = getString("Starter.AUTHENTICATION_DOMAIN");

  /** Secret server key for DIGEST Authentication */
  private String secretKey; // = getString("Starter.SEEVER_KEY");

  /** Authentication Scheme BASIC / DIGEST */
  private String authenticationSCHEME; // = getString("Starter.AUTHENTICATION_SCHEME");

  /** Authentication Algorithm OPENLDAP-MD5 / DIGEST-MD5 / NONE */
  private String authenticationALGORITHM; // = getString("Starter.AUTHENTICATION_SCHEME");

  /** Authentication Cookie when standard HTTP authentication is not possible */
  private String authenticationCOOKIE; // = getString("Starter.AUTHENTICATION_COOKIE");

  /** Authentication Agent to specialized response representation (json if client sitools) */
  private String authenticationAGENT; // = getString("Starter.AUTHENTICATION_AGENT");

  /** Execution path property */
  private String rootDirectory; // = getString("Starter.ROOT_DIRECTORY");

  /**
   * Public URL for server. Needed when a host domain name is used with an external proxy.
   */
  private String publicHostDomain; // = getString("Starter.PUBLIC_HOST_DOMAIN");

  /**
   * Application Registry
   */
  private AppRegistryApplication appRegistry = null;

  /**
   * Notification Manager
   */
  private NotificationManager notificationManager = null;

  /**
   * Stores
   */
  private Map<String, Object> stores = null;

  /**
   * The list of applications categories configured as intranet application
   */
  private List<Category> intranetCategories = null;

  /**
   * The list of addresses configured as intranet addresses
   */
  private List<String> intranetAddresses = null;

  /**
   * The Parent component
   */
  private SitoolsComponent component = null;

  /**
   * UserStorage refresh delay constant
   */
  private int userStorageRefreshDelay;

  /**
   * Whether or not to start the server with migration
   */
  private boolean startWithMigration = false;

  /**
   * Whether or not to check stores at startup
   */
  private boolean checkStores = false;

  /**
   * Private constructor for utility class
   */
  private SitoolsSettings() {
    super();
  }

  /**
   * Set resource bundle
   * 
   * @param resBundle
   *          ResourceBundle
   */
  private void setBundle(ResourceBundle resBundle) {
    this.bundle = resBundle;
  }

  /**
   * Initialize the settings
   */
  private void init() {
    if (bundle == null) {
      System.out.println("WARNING bundle not found");
      return;
    }
    setRootDirectory(getString("Starter.ROOT_DIRECTORY"));
    setStoreDIR(getString("Starter.STORE_DIR"));
    setAuthenticationDOMAIN(getString("Starter.AUTHENTICATION_DOMAIN"));
    setAuthenticationSCHEME(getString("Starter.AUTHENTICATION_SCHEME"));
    setAuthenticationALGORITHM(getString("Starter.AUTHENTICATION_ALGORITHM"));
    setSecretKey(getString("Starter.SECRET_KEY"));
    setPublicHostDomain(getString("Starter.PUBLIC_HOST_DOMAIN"));

    setMaxConnectionsPerHost(getString("Starter.maxConnectionsPerHost"));
    setMaxTotalConnections(getString("Starter.maxTotalConnections"));
    setTmpFolderUrl(getStoreDIR("Starter.APP_TMP_FOLDER_DIR"));

    setAuthenticationCOOKIE(getString("Starter.AUTHENTICATION_COOKIE"));
    setAuthenticationAGENT(getString("Starter.AUTHENTICATION_AGENT"));

    initIntranetCategories();
    initIntranetAddresses();

    initUserStorageRefreshDelay();

    setCheckStores(Boolean.parseBoolean(getString("Starter.CHECK_STORES_AT_STARTUP", "true")));

  }

  // PUBLIC ---------------------------------------------------------------

  /**
   * Settings constructors
   * 
   * @param bundle
   *          String name of resource bundle
   * @param classloader
   *          ClassLoader of the bundle (useful in osgi environment)
   * @param locale
   *          <code>Locale</code>
   * @param defaultBundle
   *          true if this settings must be stored as default settings.
   * @return SitoolsSettings
   */
  public static SitoolsSettings getInstance(String bundle, ClassLoader classloader, Locale locale, boolean defaultBundle) {
    SitoolsSettings settings = SETTINGS.get(bundle);
    if (settings != null) {
      return settings;
    }

    settings = new SitoolsSettings();
    ResourceBundle resBundle = ResourceBundle.getBundle(bundle, locale, classloader);
    settings.setBundle(resBundle);
    settings.init();
    SETTINGS.put(bundle, settings);

    if (defaultBundle) {
      SETTINGS.put(DEFAULT_BUNDLE, settings);
    }

    return settings;
  }

  /**
   * Gets bundle settings
   * 
   * @param bundle
   *          String bundle name
   * @return SitoolsSettings
   */
  public static SitoolsSettings getInstance(String bundle) {
    SitoolsSettings settings = SETTINGS.get(bundle);
    if (settings != null) {
      return settings;
    }

    settings = new SitoolsSettings();
    ResourceBundle resBundle = ResourceBundle.getBundle(bundle);
    settings.setBundle(resBundle);
    settings.init();
    SETTINGS.put(bundle, settings);
    SETTINGS.put(DEFAULT_BUNDLE, settings);
    return settings;
  }

  /**
   * Gets default bundle settings
   * 
   * @return SitoolsSettings
   */
  public static SitoolsSettings getInstance() {
    SitoolsSettings settings = SETTINGS.get(DEFAULT_BUNDLE);
    if (settings != null) {
      return settings;
    }

    settings = new SitoolsSettings();

    ResourceBundle resBundle = ResourceBundle.getBundle(DEFAULT_BUNDLE);
    settings.setBundle(resBundle);
    settings.init();
    SETTINGS.put(DEFAULT_BUNDLE, settings);

    return settings;
  }

  /**
   * Sets the value of authenticationDOMAIN
   * 
   * @param authenticationDOMAIN
   *          the authenticationDOMAIN to set
   */
  public void setAuthenticationDOMAIN(String authenticationDOMAIN) {
    this.authenticationDOMAIN = authenticationDOMAIN;
  }

  /**
   * Gets the authenticationDOMAIN value
   * 
   * @return the authenticationDOMAIN
   */
  public String getAuthenticationDOMAIN() {
    return authenticationDOMAIN;
  }

  /**
   * Gets the secretKey value
   * 
   * @return the secretrKey
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Sets the value of secretKey
   * 
   * @param secretKey
   *          the secretKey to set
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Gets the authenticationSCHEME value
   * 
   * @return the authenticationSCHEME
   */
  public String getAuthenticationSCHEME() {
    return authenticationSCHEME;
  }

  /**
   * Sets the value of authenticationSCHEME
   * 
   * @param authenticationSCHEME
   *          the authenticationSCHEME to set
   */
  public void setAuthenticationSCHEME(String authenticationSCHEME) {
    this.authenticationSCHEME = authenticationSCHEME;
  }

  /**
   * Gets the rootDirectory value
   * 
   * @return the rootDirectory
   */
  public String getRootDirectory() {
    return rootDirectory;
  }

  /**
   * Sets the value of rootDirectory
   * 
   * @param rootDirectory
   *          the rootDirectory to set
   */
  public void setRootDirectory(String rootDirectory) {
    this.rootDirectory = rootDirectory;
  }

  /**
   * Sets the value of authenticationRealm
   * 
   * @param authenticationRealmParameter
   *          the authenticationRealm to set
   */
  public void setAuthenticationRealm(SitoolsRealm authenticationRealmParameter) {
    authenticationRealm = authenticationRealmParameter;
  }

  /**
   * Gets the authenticationRealm value
   * 
   * @return the authenticationRealm
   */
  public SitoolsRealm getAuthenticationRealm() {
    if (isSecured()) {
      return authenticationRealm;
    }
    else {
      return null;
    }
  }

  /**
   * Gets the maxTotalConnections value
   * 
   * @return the maxTotalConnections
   */
  public String getMaxTotalConnections() {
    return maxTotalConnections;
  }

  /**
   * Sets the value of maxTotalConnections
   * 
   * @param maxTotalConnections
   *          the maxTotalConnections to set
   */
  public void setMaxTotalConnections(String maxTotalConnections) {
    this.maxTotalConnections = maxTotalConnections;
  }

  /**
   * Gets the maxConnectionsPerHost value
   * 
   * @return the maxConnectionsPerHost
   */
  public String getMaxConnectionsPerHost() {
    return maxConnectionsPerHost;
  }

  /**
   * Sets the value of maxConnectionsPerHost
   * 
   * @param maxConnectionsPerHost
   *          the maxConnectionsPerHost to set
   */
  public void setMaxConnectionsPerHost(String maxConnectionsPerHost) {
    this.maxConnectionsPerHost = maxConnectionsPerHost;
  }

  /**
   * Sets the value of publicHostDomain
   * 
   * @param publicHostDomainParameter
   *          the publicHostDomain to set
   */
  public void setPublicHostDomain(String publicHostDomainParameter) {
    this.publicHostDomain = publicHostDomainParameter;
  }

  /**
   * Gets the publicHostDomain value
   * 
   * @return the publicHostDomain
   */
  public String getPublicHostDomain() {
    return publicHostDomain;
  }

  /**
   * Gets the storeDIR value
   * 
   * @return the storeDIR
   */
  public String getStoreDIR() {
    return storeDIR;
  }

  /**
   * Sets the value of storeDIR
   * 
   * @param storeDIRParameter
   *          the storeDIR to set
   */
  public void setStoreDIR(String storeDIRParameter) {
    storeDIR = storeDIRParameter;
  }

  /**
   * Gets Store Directory for a specific ID
   * 
   * @param storeID
   *          property key
   * @return String
   */
  public String getStoreDIR(String storeID) {
    return getRootDirectory() + getStoreDIR() + getString(storeID);
  }

  /**
   * Gets Store Directory for a specific ID keeping ${ROOT_DIRECTORY}
   * 
   * @param storeID
   *          the store identifier
   * @return the store directory
   */
  public String getVariableStoreDIR(String storeID) {
    return "${ROOT_DIRECTORY}" + getStoreDIR() + getString(storeID);
  }

  /**
   * Sets the value of tmpFolderUrl
   * 
   * @param tmpFolderUrl
   *          the tmpFolderUrl to set
   */
  public void setTmpFolderUrl(String tmpFolderUrl) {
    this.tmpFolderUrl = tmpFolderUrl;
  }

  /**
   * Gets the tmpFolderUrl value
   * 
   * @return the tmpFolderUrl
   */
  public String getTmpFolderUrl() {
    return tmpFolderUrl;
  }

  /**
   * To replace "${ROOT_DIRECTORY}" in String
   * 
   * @param format
   *          the string to format
   * @return The formated string
   */
  public String getFormattedString(String format) {
    if ((format == null) || "".equals(format)) {
      return format;
    }
    String result = format.replaceAll("\\$\\{ROOT_DIRECTORY\\}", getRootDirectory());
    return result.replaceFirst("^(file://.:)", result.substring(0, 8));
  }

  /**
   * gets the property value
   * 
   * @param key
   *          property
   * @return String value
   */
  public String getString(String key) {
    try {
      return bundle.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  /**
   * gets the property value
   * 
   * @param key
   *          property
   * @param defaultValue
   *          returned value if key not found
   * @return String value
   */
  public String getString(String key, String defaultValue) {
    try {
      return bundle.getString(key);
    }
    catch (MissingResourceException e) {
      return defaultValue;
    }
  }

  /**
   * gets the property value
   * 
   * @param key
   *          property
   * @return int value
   */
  public int getInt(String key) {
    return Integer.parseInt(bundle.getString(key));
  }

  /**
   * gets the property value
   * 
   * @param key
   *          property
   * @return int value
   */
  public Long getLong(String key) {
    return Long.parseLong(bundle.getString(key));
  }

  /**
   * Gets settings starting with baseName
   * 
   * @param baseName
   *          String
   * @return Map<String, Object>
   */
  public Map<String, Object> getSettings(String baseName) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (String key : bundle.keySet()) {
      if (key.startsWith(baseName)) {
        map.put(key, bundle.getObject(key));
      }
    }
    return map;
  }

  /**
   * Gets settings starting with baseName and replaces baseName with replacement.
   * 
   * @param baseName
   *          String
   * @param replacement
   *          String
   * @return Map<String, Object>
   */
  public Map<String, Object> getSettings(String baseName, String replacement) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (String key : bundle.keySet()) {
      if (key.startsWith(baseName)) {
        map.put(key.replaceFirst(baseName, replacement), bundle.getObject(key));
      }
    }
    return map;
  }

  /**
   * Init the intranet categories
   */
  public void initIntranetCategories() {
    // set the categories
    intranetCategories = new ArrayList<Category>();
    for (Category category : Category.values()) {
      String isIntranetStr = getString("Security.Intranet." + category);
      if (isIntranetStr != null) {
        boolean isIntranet = new Boolean(isIntranetStr);
        if (isIntranet) {
          intranetCategories.add(category);
        }
      }
    }
  }

  /**
   * Init the intranet addresses
   */
  private void initIntranetAddresses() {
    StringTokenizer st = new StringTokenizer(getString("Security.Intranet.net"), "|");
    this.intranetAddresses = new ArrayList<String>();
    while (st.hasMoreElements()) {
      this.intranetAddresses.add(st.nextToken());
    }
  }

  /**
   * Initialize the userstorage refresh delay. Default value is 0
   */
  private void initUserStorageRefreshDelay() {
    String userStorageStr = this.getString(Consts.USERSTORAGE_REFRESH_DELAY);
    try {
      int userStorageInt = Integer.parseInt(userStorageStr);
      setUserStorageRefreshDelay(userStorageInt);
    }
    catch (NumberFormatException e) {
      setUserStorageRefreshDelay(0);
    }
  }

  /**
   * Gets the appRegistry value
   * 
   * @return the appRegistry
   */
  public AppRegistryApplication getAppRegistry() {
    return appRegistry;
  }

  /**
   * Sets the value of appRegistry
   * 
   * @param appRegistry
   *          the appRegistry to set
   */
  public void setAppRegistry(AppRegistryApplication appRegistry) {
    this.appRegistry = appRegistry;
  }

  /**
   * Gets the notificationManager value
   * 
   * @return the notificationManager
   */
  public NotificationManager getNotificationManager() {
    return notificationManager;
  }

  /**
   * Sets the value of notificationManager
   * 
   * @param notificationManager
   *          the notificationManager to set
   */
  public void setNotificationManager(NotificationManager notificationManager) {
    this.notificationManager = notificationManager;
  }

  /**
   * Gets the bundle value
   * 
   * @return the bundle
   */
  public ResourceBundle getBundle() {
    return bundle;
  }

  /**
   * Gets the stores value
   * 
   * @return the stores
   */
  public Map<String, Object> getStores() {
    return stores;
  }

  /**
   * Sets the value of stores
   * 
   * @param stores
   *          the stores to set
   */
  public void setStores(Map<String, Object> stores) {
    this.stores = stores;
  }

  /**
   * Gets the authenticationALGORITHM value
   * 
   * @return the authenticationALGORITHM
   */
  public String getAuthenticationALGORITHM() {
    return authenticationALGORITHM;
  }

  /**
   * Sets the value of authenticationALGORITHM
   * 
   * @param authenticationALGORITHM
   *          the authenticationALGORITHM to set
   */
  public void setAuthenticationALGORITHM(String authenticationALGORITHM) {
    this.authenticationALGORITHM = authenticationALGORITHM;
  }

  /**
   * Gets the authenticationCOOKIE value
   * 
   * @return the authenticationCOOKIE
   */
  public String getAuthenticationCOOKIE() {
    return authenticationCOOKIE;
  }

  /**
   * Sets the value of authenticationCOOKIE
   * 
   * @param authenticationCOOKIE
   *          the authenticationCOOKIE to set
   */
  public void setAuthenticationCOOKIE(String authenticationCOOKIE) {
    this.authenticationCOOKIE = authenticationCOOKIE;
  }

  /**
   * Gets the authenticationAGENT value
   * 
   * @return the authenticationAGENT
   */
  public String getAuthenticationAGENT() {
    return authenticationAGENT;
  }

  /**
   * Sets the value of authenticationAGENT
   * 
   * @param authenticationAGENT
   *          the authenticationAGENT to set
   */
  public void setAuthenticationAGENT(String authenticationAGENT) {
    this.authenticationAGENT = authenticationAGENT;
  }

  /**
   * Gets the intranetCategories value
   * 
   * @return the intranetCategories
   */
  public List<Category> getIntranetCategories() {
    return intranetCategories;
  }

  /**
   * Sets the value of intranetCategories
   * 
   * @param intranetCategories
   *          the intranetCategories to set
   */
  public void setIntranetCategories(List<Category> intranetCategories) {
    this.intranetCategories = intranetCategories;
  }

  /**
   * Sets the value of intranetAddresses
   * 
   * @param intranetAddresses
   *          the intranetAddresses to set
   */
  public void setIntranetAddresses(List<String> intranetAddresses) {
    this.intranetAddresses = intranetAddresses;
  }

  /**
   * Gets the intranetAddresses value
   * 
   * @return the intranetAddresses
   */
  public List<String> getIntranetAddresses() {
    return intranetAddresses;
  }

  /**
   * Sets the value of component
   * 
   * @param component
   *          the component to set
   */
  public void setComponent(SitoolsComponent component) {
    this.component = component;
  }

  /**
   * Gets the component value
   * 
   * @return the component
   */
  public SitoolsComponent getComponent() {
    return component;
  }

  /**
   * Gets the secured value
   * 
   * @return the secured
   */
  public boolean isSecured() {
    return secured;
  }

  /**
   * Sets the value of secured
   * 
   * @param secured
   *          the secured to set
   */
  public void setSecured(boolean secured) {
    this.secured = secured;
  }

  /**
   * Gets the userStorageRefreshDelay value
   * 
   * @return the userStorageRefreshDelay
   */
  public int getUserStorageRefreshDelay() {
    return userStorageRefreshDelay;
  }

  /**
   * Sets the value of userStorageRefreshDelay
   * 
   * @param userStorageRefreshDelay
   *          the userStorageRefreshDelay to set
   */
  public void setUserStorageRefreshDelay(int userStorageRefreshDelay) {
    this.userStorageRefreshDelay = userStorageRefreshDelay;
  }

  /**
   * Gets the startWithMigration value
   * 
   * @return the startWithMigration
   */
  public boolean isStartWithMigration() {
    return startWithMigration;
  }

  /**
   * Sets the value of startWithMigration
   * 
   * @param startWithMigration
   *          the startWithMigration to set
   */
  public void setStartWithMigration(boolean startWithMigration) {
    this.startWithMigration = startWithMigration;
  }

  /**
   * Get the userstorage directory for a particular username
   * 
   * @param username
   *          the username
   * @return userstorage directory for a particular username
   */
  public String getUserStorageDir(String username) {
    return getRootDirectory() + getStoreDIR() + getString(Consts.USERSTORAGE_ROOT) + "/" + username;
  }

  /**
   * Gets the checkStores value
   * 
   * @return the checkStores
   */
  public boolean isCheckStores() {
    return checkStores;
  }

  /**
   * Sets the value of checkStores
   * 
   * @param checkStores
   *          the checkStores to set
   */
  public void setCheckStores(boolean checkStores) {
    this.checkStores = checkStores;
  }

}
