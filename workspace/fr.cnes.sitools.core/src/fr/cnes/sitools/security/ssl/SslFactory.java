    /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.security.ssl;

import java.io.File;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;

import fr.cnes.sitools.common.SitoolsSettings;

/**
 * Utility class to configure SSL Support.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SslFactory {

  /** Ssl support is true here */
  protected static boolean sslSupport = true;

  /** SSL context factory name */
  private static final String HTTPS_SSL_CONTEXT_FACTORY = "sslContextFactory";
  
  /** Key store, path*/
  private static final String HTTPS_KEYSTORE_PATH = "keystorePath";
  
  /** Key store, password */
  private static final String HTTPS_KEYSTORE_PASSWORD = "keystorePassword";
  
  /** Key password */
  private static final String HTTPS_KEY_PASSWORD = "keyPassword";
  
  /** Keystore, type */
  private static final String HTTPS_KEYSTORE_TYPE = "keystoreType";

  /**
   * Private constructor for utility class
   */
  private SslFactory() {
    super();
  }

  /**
   * Adds SSL support to server component.
   * 
   * @param component
   *          Component
   * @param settings SitoolsSettings
   * @return Component
   */
  public static Component addSslSupport(Component component, SitoolsSettings settings) {
    // Add a new HTTPS server listening on port HTTPS_PORT
    String httpsPort = settings.getString("Starter.HOST_PORT_HTTPS");
    if ((httpsPort != null) && !httpsPort.equals("")) {
      int port = Integer.parseInt(httpsPort);
      Server server = component.getServers().add(Protocol.HTTPS, port);
      Series<Parameter> parameters = server.getContext().getParameters();

      File keystoreFile = new File(settings.getStoreDIR("Starter.SSL_STORE_DIR"),
          settings.getString("Starter.HTTPS_keystorePath"));
      if (keystoreFile.exists()) {
        String keystorePassword = settings.getString("Starter.HTTPS_keystorePassword");
        parameters.add(HTTPS_SSL_CONTEXT_FACTORY, "org.restlet.ext.ssl.PkixSslContextFactory");
        parameters.add(HTTPS_KEYSTORE_PATH, keystoreFile.getAbsolutePath()); // keystoreFile.toURI().toASCIIString()
        parameters.add(HTTPS_KEYSTORE_PASSWORD, keystorePassword);
        parameters.add(HTTPS_KEY_PASSWORD, keystorePassword);
        parameters.add(HTTPS_KEYSTORE_TYPE, "JKS");

        sslSupport = true;
      }
    }
    return component;
  }

}
