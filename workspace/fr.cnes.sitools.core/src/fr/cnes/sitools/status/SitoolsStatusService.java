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
package fr.cnes.sitools.status;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.UniformResource;
import org.restlet.service.StatusService;

import fr.cnes.sitools.common.application.ContextAttributes;

/**
 * Subclass of StatusService to adapt Representation of different status code.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsStatusService extends StatusService {

  /** freemarker template */
  private String template = null;

  /**
   * Default constructor
   */
  public SitoolsStatusService() {
    super();
  }

  /**
   * Constructor with service enabled
   * 
   * @param enabled
   *          boolean to active or disable StatusService
   */
  public SitoolsStatusService(boolean enabled) {
    super(enabled);
  }

  /**
   * Gets the template value
   * 
   * @return the template
   */
  public String getTemplate() {
    return template;
  }

  /**
   * Sets the value of template
   * 
   * @param template
   *          the template to set
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  @Override
  public Representation getRepresentation(Status status, Request request, Response response) {
    // if the attribute NO_STATUS_SERVICE is set
    if (Boolean.TRUE.equals(response.getAttributes().get(ContextAttributes.NO_STATUS_SERVICE))) {
      return response.getEntity();
    }
    else {
      Reference ref = null;
      ref = LocalReference.createFileReference(template);

      Representation statusFtl = new ClientResource(ref).get();

      StatusDTO statusDTO = new StatusDTO();
      statusDTO.setStatus(status);
      statusDTO.setService(this);

      // Wraps the bean with a FreeMarker representation
      return new TemplateRepresentation(statusFtl, statusDTO, MediaType.TEXT_HTML);
    }

  }

  /* (non-Javadoc)
   * @see org.restlet.service.StatusService#getStatus(java.lang.Throwable, org.restlet.Request, org.restlet.Response)
   */
  @Override
  public Status getStatus(Throwable arg0, Request arg1, Response arg2) {
    // TODO Auto-generated method stub
    return super.getStatus(arg0, arg1, arg2);
  }

  /* (non-Javadoc)
   * @see org.restlet.service.StatusService#getStatus(java.lang.Throwable, org.restlet.resource.UniformResource)
   */
  @Override
  public Status getStatus(Throwable throwable, UniformResource resource) {
    // TODO Auto-generated method stub
    return super.getStatus(throwable, resource);
  }
  
  

}
