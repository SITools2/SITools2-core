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

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

/**
 * A filter used to forbid request with bad credentials
 * 
 * 
 * @author m.gond
 */
public class NotAuthenticatedFilter extends Filter {

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#beforeHandle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  protected int beforeHandle(Request request, Response response) {
    if (request.getChallengeResponse() != null) {
      String id = request.getChallengeResponse().getIdentifier();
      if (request.getClientInfo() != null && !request.getClientInfo().isAuthenticated() && id != null && !id.isEmpty()) {
        response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "Bad credentials");
        log(request, response, id);
        return STOP;
      }
    }
    return CONTINUE;
  }

  /**
   * Log.
   * 
   * @param request
   *          the request
   * @param id
   *          the id
   */
  private void log(Request request, Response response, String id) {

    String message = "Request to : " + request.getResourceRef().getPath()
        + " forbidden, bad credentials for user: " + id;

    LogRecord record = new LogRecord(Level.WARNING, message);
    response.getAttributes().put("LOG_RECORD", record);

  }

}
