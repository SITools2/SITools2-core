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
package fr.cnes.sitools.proxy;

import java.util.Map;
import java.util.Properties;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;

/**
 * Proxy settings
 * 
 * @author AKKA Technologies
 * 
 */
public final class ProxySettings {

  /** Proxy configuration - host */
  private static String proxyHost = null;
  /** Proxy configuration - port */
  private static String proxyPort = null;

  /** Proxy configuration - user */
  private static String proxyUser = null;
  /** Proxy configuration - password */
  private static String proxyPassword = null;
  
  /** Proxy configuration - password */
  private static String nonProxyHosts = null;

  /** Cache Proxy ChallengeResponse */
  private static ChallengeResponse proxyAuthentication = null;

  /** Proxy configuration enable / disable */
  private static boolean proxySet = false;

  /** ------------------- */
  /** Proxy configuration */

  /** Proxy configuration - host system property */
  private static final String HTTP_PROXYHOST = "http.proxyHost";
  /** Proxy configuration - port system property */
  private static final String HTTP_PROXYPORT = "http.proxyPort";

  /** TODO correct usage of others */
  //private static final String HTTP_PROXYUSER = "http.proxyUser";
  
  /** proxy user name */
  //private static final String HTTP_PROXYUSERNAME = "http.proxyUserName";
  
  /** proxy user password*/
//  private static final String HTTP_PROXYPASSWORD = "http.proxyPassword";
  
  /** proxy set*/
//  private static final String HTTP_PROXYSET = "http.proxySet";
  
  /** non proxy hosts */
  private static final String HTTP_NONPROXYHOSTS = "http.nonProxyHosts";
  
  /**
   * Private constructor
   */
  private ProxySettings() {
    super();
  }
  
  /**
   * Init the proxy setting
   * 
   * @param  context Map<String, Object>
   *          proxy attributes
   */
  public static void init(Map<String, Object> context) {
    
    proxyHost = (String) context.get(ContextAttributes.PROXY_HOST);
    proxyPort = (String) context.get(ContextAttributes.PROXY_PORT);
    
    proxyUser = (String) context.get(ContextAttributes.PROXY_USER);
    proxyPassword = (String) context.get(ContextAttributes.PROXY_PASSWORD);
    nonProxyHosts = (String) context.get(ContextAttributes.NONPROXY_HOSTS);
    
    if ((proxyHost != null) && !proxyHost.equals("") && (proxyPort != null) && !proxyPort.equals("")) {
      ProxySettings.proxySet = true;

      Properties properties = System.getProperties();
      properties.put(HTTP_PROXYHOST, proxyHost);
      properties.put(HTTP_PROXYPORT, proxyPort);
      properties.put(HTTP_NONPROXYHOSTS, nonProxyHosts);

      // Add the client authentication to the call
      ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;

      // User + Password sur le proxy
      proxyAuthentication = new ChallengeResponse(scheme, proxyUser, proxyPassword);
    }
  }

  /**
   * Init the proxy setting
   * 
   * @param args
   *          command line arguments
   * @param settings SITools settings
   */
  public static void init(String[] args, SitoolsSettings settings) {

    // lancer avec argument jvm "proxy" pour le RedirectorProxy
    if (args.length > 0) {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("proxy")) {
          ProxySettings.proxySet = true;
          break;
        }
      }
    }
    
    // proprietes par defaut
    if ("true".equals(settings.getString("Starter.WITH_PROXY"))) {
      ProxySettings.proxySet = true;
    }
    
    if (proxySet) {

      /** Proxy configuration - host */
      proxyHost = settings.getString("Starter.PROXY_HOST");
      /** Proxy configuration - port */
      proxyPort = settings.getString("Starter.PROXY_PORT");

      /** Proxy configuration - user */
      proxyUser = settings.getString("Starter.PROXY_USER");
      /** Proxy configuration - password */
      proxyPassword = settings.getString("Starter.PROXY_PASSWORD");

      /** Proxy configuration - non proxy hosts */
      nonProxyHosts = settings.getString("Starter.NONPROXY_HOSTS");
      
      // component.getClients().add(Protocol.HTTP);
      // Client client = new Client(Protocol.HTTP);
      Properties properties = System.getProperties();
      properties.put(HTTP_PROXYHOST, proxyHost);
      properties.put(HTTP_PROXYPORT, proxyPort);

      // properties.put(HTTP_PROXYUSERNAME, Constants.PROXY_USER);
      // properties.put(HTTP_PROXYPASSSWORD, Constants.PROXY_PASSWORD);
      // component.getClients().add(client);

      properties.put(HTTP_NONPROXYHOSTS, nonProxyHosts);
      
//      System.setProperty(HTTP_PROXYHOST, proxyHost);
//      System.setProperty(HTTP_PROXYPORT, proxyPort);
//      System.setProperty(HTTP_NONPROXYHOSTS, nonProxyHosts);
      
      // Add the client authentication to the call
      ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;

      // User + Password sur le proxy
      proxyAuthentication = new ChallengeResponse(scheme, proxyUser, proxyPassword);
    }
  }

  /**
   * Init the proxy setting
   */
  public static void reset() {
    proxySet = false;
    Properties properties = System.getProperties();
    properties.remove(HTTP_PROXYHOST);
    properties.remove(HTTP_PROXYPORT);
    properties.remove(HTTP_NONPROXYHOSTS);
    proxyAuthentication = null;
  }

  /**
   * Gets the withProxy value
   * @return the withProxy
   */
  public static boolean isWithProxy() {
    return proxySet;
  }

  /**
   * Sets the value of withProxy
   * @param withProxy the withProxy to set
   */
  public static void setWithProxy(boolean withProxy) {
    ProxySettings.proxySet = withProxy;
  }

  /**
   * Gets the proxyHost value
   * @return the proxyHost
   */
  public static String getProxyHost() {
    return proxyHost;
  }

  /**
   * Gets the proxyPort value
   * @return the proxyPort
   */
  public static String getProxyPort() {
    return proxyPort;
  }

  /**
   * Gets the proxyUser value
   * @return the proxyUser
   */
  public static String getProxyUser() {
    return proxyUser;
  }

  /**
   * Gets the proxyPassword value
   * @return the proxyPassword
   */
  public static String getProxyPassword() {
    return proxyPassword;
  }

  /**
   * Gets the proxyAuthentication value
   * @return the proxyAuthentication
   */
  public static ChallengeResponse getProxyAuthentication() {
    return proxyAuthentication;
  }

  /**
   * Gets the nonproxyHosts value
   * @return the nonproxyHosts
   */
  public static String getNonProxyHosts() {
    return nonProxyHosts;
  }
  
  
}
