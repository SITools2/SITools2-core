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
package fr.cnes.sitools.status;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.application.StatusFilter;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.service.StatusService;

/**
 * Sitools Status Filter
 * @author AKKA
 *
 */
public class SitoolsStatusFilter extends StatusFilter {

  /**
   * Constructor
   * @param context restlet context
   * @param overwriting indicates if overwrites
   * @param email email
   * @param homeRef home reference
   */
  public SitoolsStatusFilter(Context context, boolean overwriting, String email, Reference homeRef) {
    super(context, overwriting, email, homeRef);
  }

  /**
   * Constructor
   * @param context restlet context
   * @param statusService new status service
   */
  public SitoolsStatusFilter(Context context, StatusService statusService) {
    super(context, statusService);
  }

  /**
   * Returns a representation for the given status.<br>
   * In order to customize the default representation, this method can be overridden.
   * 
   * @param status
   *          The status to represent.
   * @param request
   *          The request handled.
   * @param response
   *          The response updated.
   * @return The representation of the given status.
   */
  protected Representation getDefaultRepresentation(Status status, Request request, Response response) {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html>\n");
    sb.append("<head>\n");
    sb.append("   <title>Sitools Status page</title>\n");
    sb.append("</head>\n");
    sb.append("<body style=\"font-family: sans-serif;\">\n");

    sb.append("<p style=\"font-size: 1.2em;font-weight: bold;margin: 1em 0px;\">");
    sb.append(getStatusInfo(status));
    sb.append("</p>\n");
    if (status.getDescription() != null) {
      sb.append("<p>");
      sb.append(status.getDescription());
      sb.append("</p>\n");
    }

    sb.append("<p>You can get technical details <a href=\"");
    sb.append(status.getUri());
    sb.append("\">here</a>.<br>\n");

    if (getContactEmail() != null) {
      sb.append("For further assistance, you can contact the <a href=\"mailto:");
      sb.append(getContactEmail());
      sb.append("\">administrator</a>.<br>\n");
    }

    if (getHomeRef() != null) {
      sb.append("Please continue your visit at our <a href=\"");
      sb.append(getHomeRef());
      sb.append("\">home page</a>.\n");
    }

    sb.append("</p>\n");
    sb.append("</body>\n");
    sb.append("</html>\n");

    return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
  }

  /**
   * Returns a representation for the given status.<br>
   * In order to customize the default representation, this method can be overridden.
   * 
   * @param status
   *          The status to represent.
   * @param request
   *          The request handled.
   * @param response
   *          The response updated.
   * @return The representation of the given status.
   */
  protected Representation getRepresentation(Status status, Request request, Response response) {
    Representation result = getStatusService().getRepresentation(status, request, response);

    if ((result == null) && (null != response.getAttributes().get("status.template"))) {
      String templateFileName = (String) response.getAttributes().get("status.template");
      Representation templateFileRepresentation = new ClientResource(
          LocalReference.createClapReference(templateFileName)).get();

      // Wraps the bean with a FreeMarker representation
      result = new TemplateRepresentation(templateFileRepresentation, status, MediaType.TEXT_HTML);
    }

    if (result == null) {
      result = getDefaultRepresentation(status, request, response);
    }

    return result;
  }

}
