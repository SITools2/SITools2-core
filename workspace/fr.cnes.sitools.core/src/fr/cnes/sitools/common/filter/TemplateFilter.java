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
package fr.cnes.sitools.common.filter;

import java.util.HashMap;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Encoding;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;

/**
 * Inspired by Freemarker TemplateFilter
 * 
 * Extension for processing all text resources like a freemarker template
 * 
 * @author Akka Technologies
 */
public class TemplateFilter extends org.restlet.ext.freemarker.TemplateFilter {

  @Override
  protected void afterHandle(Request request, Response response) {
    boolean processTemplate = "true".equals(request.getResourceRef().getQueryAsForm()
        .getFirstValue("processTemplate", "false"));
    if (processTemplate
        && response.isEntityAvailable()
        && (MediaType.TEXT_ALL.isCompatible(response.getEntity().getMediaType()) || response.getEntity().getEncodings()
            .contains(Encoding.FREEMARKER))) {
      TemplateRepresentation representation = new TemplateRepresentation(response.getEntity(),
          super.getConfiguration(), response.getEntity().getMediaType());
      representation.setDataModel(createDataModel(request, response));
      response.setEntity(representation);

      // TODO IMPROVEMENT make CacheDirectives a directory model property
      response.getCacheDirectives().add(CacheDirective.noCache());
    }
  }

  /**
   * Creates the FreeMarker data model for a given call.
   * 
   * @param request
   *          The handled request.
   * @param response
   *          The handled response.
   * @return The FreeMarker data model for the given call.
   */
  @Override
  protected Object createDataModel(Request request, Response response) {
    HashMap<String, Object> result = new HashMap<String, Object>();
    result.put("request", request);
    result.put("response", response);
    result.put("directory", getConfiguration().getCustomAttribute("directory"));
    return result;
  }

}
