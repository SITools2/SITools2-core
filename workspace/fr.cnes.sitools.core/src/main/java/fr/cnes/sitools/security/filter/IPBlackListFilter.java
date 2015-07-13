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
package fr.cnes.sitools.security.filter;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * Filter to abort request of black listed client IP
 * 
 * sitools.properties contains a Security.IPBlackList String of IP addresses separated with |
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class IPBlackListFilter extends SecurityFilter {

  /** Container of banished IP addresses */
  private StringContainer ipContainer = null;

  /**
   * Constructor
   * 
   * @param context
   *          Context
   */
  public IPBlackListFilter(Context context) {
    super(context);

    ipContainer = (StringContainer) (context.getAttributes().get("Security.filter.blacklist.Container"));
    if (ipContainer == null) {
      ipContainer = (StringContainer) getSettings().getStores().get("Security.filter.blacklist.Container");

      if (ipContainer == null) {
        ipContainer = new IPBlackListTreeSet(context);
        getSettings().getStores().put("Security.filter.blacklist.Container", ipContainer);
      }
    }
  }

  @Override
  protected int beforeHandle(Request request, Response response) {
    int status = STOP;

    if (super.beforeHandle(request, response) == STOP) {
      return STOP;
    }

    String clientip = getIpAddress(request);
    status = ((ipContainer != null) && ipContainer.contains(clientip)) ? STOP : CONTINUE;
    if (status == STOP) {
      response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Your IP address was blacklisted");
      log(request, response, clientip);
    }
    return status;
  }

  /**
   * Log.
   * 
   * @param request
   *          the request
   * @param response
   *          the response
   * @param clientip
   *          the client ip
   */
  private void log(Request request, Response response, String clientip) {

    String message = "Request to : " + request.getResourceRef().getPath() + " forbidden, IP address:" + clientip
        + " is in blacklist";

    LogRecord record = new LogRecord(Level.WARNING, message);
    response.getAttributes().put("LOG_RECORD", record);

  }

  /**
   * Getter of ipContainer
   * 
   * @return StringContainer
   */
  public StringContainer getIpContainer() {
    return ipContainer;
  }

  /**
   * Setter of ipContainer
   * 
   * @param ipContainer
   *          StringContainer
   */
  public void setIpContainer(StringContainer ipContainer) {
    this.ipContainer = ipContainer;
  }

}
