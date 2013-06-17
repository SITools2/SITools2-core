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
package fr.cnes.sitools.security.filter;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.http.conn.util.InetAddressUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;

/**
 * Restlet Filter filtering on the ip address of the request It checks If the address is compatible with a specified
 * intranet.
 * 
 * 
 * @author m.gond
 */
public class SecurityFilter extends Filter {
  /**
   * The intranet subnetwork mask
   */
  private long lIntranetMask;

  /**
   * The intranet network addresses, as String
   */
  private List<String> intranetNets;

  /**
   * If the filter has to filter the ip address
   */
  private boolean doFilter;
  /**
   * The SitoolsSettings
   */
  private SitoolsSettings settings;

  /**
   * The application to which the Filter is attached
   */
  private SitoolsApplication application;

  /**
   * Constructor with context and a boolean to specify if ip needs to be filtered
   * 
   * @param context
   *          the context
   */
  public SecurityFilter(Context context) {
    super(context);
    // get the SitoolsSettings
    settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    // get the intranet address
    intranetNets = settings.getIntranetAddresses();
    // get the intranet mask
    String intranetMask = settings.getString("Security.Intranet.mask");

    application = (SitoolsApplication) context.getAttributes().get("application");

    if (intranetMask == null || "".equals(intranetMask) || intranetNets == null || intranetNets.size() == 0) {
      this.doFilter = false;
    }
    else {
      this.doFilter = settings.getIntranetCategories().contains(application.getCategory());
      lIntranetMask = getAddress(intranetMask);
    }

  }

  /**
   * Method executed before the request is handled. It checks if the address of the request is compatible with the
   * intranet parameters
   * 
   * @param request
   *          the request
   * @param response
   *          the response
   * @return CONTINUE if the address is from the intranet, STOP otherwise
   */
  @Override
  protected int beforeHandle(Request request, Response response) {
    int status = STOP;
    if (isIntranet(request)) {
      status = CONTINUE;
      request.getAttributes().put("Sitools.intranet", true);
      // FIXME It is used to create url for notifications
      // remove it when all notifications urls are in RIAP
      request.getAttributes().put(ContextAttributes.PUBLIC_HOST_NAME, settings.getPublicHostDomain());
    }
    else {
      request.getAttributes().put("Sitools.intranet", false);
      request.getAttributes().put(ContextAttributes.PUBLIC_HOST_NAME, settings.getPublicHostDomain());
    }

    if (doFilter && status == STOP) {
      response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Your IP address was blocked");
    }
    else {
      status = CONTINUE;
    }

    getLogger().log(Level.FINEST, "Request from " + request.getClientInfo().getAddress() + " OK = " + status);
    return status;
  }

  /**
   * Check if the given request if from the intranet
   * 
   * @param request
   *          the request
   * @return true if the request is from the intranet, false otherwise
   */
  public boolean isIntranet(Request request) {
    // get the address of the Request
    String ip = getIpAddress(request);
    // transform this address into a long
    long lAddress = getAddress(ip);
    // loop thought all the intranet address to check if the given address match one of them
    boolean ok = false;
    for (Iterator<String> iterator = intranetNets.iterator(); iterator.hasNext() && !ok;) {
      Long inet = getAddress(iterator.next());
      if ((lAddress & lIntranetMask) == inet) {
        ok = true;
      }
    }
    return ok;
  }

  /**
   * Gets the IP address from the request
   * 
   * @param request
   *          the {@link Request}
   * @return the IP address as a String
   */
  protected String getIpAddress(Request request) {
    return request.getClientInfo().getUpstreamAddress();
  }

  /**
   * Check if the given request if from the extranet
   * 
   * @param request
   *          the request
   * @return true if the request is from the extranet, false otherwise
   */
  public boolean isExtranet(Request request) {
    return !isIntranet(request);
  }

  /**
   * Transform an address from string to long
   * 
   * @param ip
   *          the String representing an ip address
   * @return the long representation of the given ip address
   */
  private long getAddress(String ip) {
    boolean isIPv4 = InetAddressUtils.isIPv4Address(ip);
    if (isIPv4) {
      StringTokenizer st = new StringTokenizer(ip, ".");

      long a1 = Integer.parseInt(st.nextToken());
      long a2 = Integer.parseInt(st.nextToken());
      long a3 = Integer.parseInt(st.nextToken());
      long a4 = Integer.parseInt(st.nextToken());

      return a1 << 24 | a2 << 16 | a3 << 8 | a4;
    }

    boolean isIPv6 = InetAddressUtils.isIPv6Address(ip);
    if (isIPv6) {
      getContext().getLogger().warning("SecurityFilter: IPV6 Address cannot match IPV4 mask");
      return 0;
    }
    getContext().getLogger().warning("SecurityFilter: Not a valid IP Address");
    return 0;
  }

  /**
   * Gets the settings value
   * 
   * @return the settings
   */
  public SitoolsSettings getSettings() {
    return settings;
  }

  /**
   * Sets the value of settings
   * 
   * @param settings
   *          the settings to set
   */
  public void setSettings(SitoolsSettings settings) {
    this.settings = settings;
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public SitoolsApplication getApplication() {
    return application;
  }

  /**
   * Sets the value of application
   * 
   * @param application
   *          the application to set
   */
  public void setApplication(SitoolsApplication application) {
    this.application = application;
  }

}
