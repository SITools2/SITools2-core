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
package fr.cnes.sitools.client;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;

import fr.cnes.sitools.applications.ClientAdminApplication;
import fr.cnes.sitools.client.model.AdminIndexDTO;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.server.Consts;

/**
 * Resource providing an index.html file for admin interface.
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 */
public class AdminIndex extends SitoolsResource {

  /**
   * The clientAdminApplication
   */
  private ClientAdminApplication application;
  
  @Override
  public void sitoolsDescribe() {
    setName("AdminIndex");
    setDescription("Resource to return the index.html page of the admin interface");
  }

  @Override
  protected void doInit() {
    super.doInit();
    application = (ClientAdminApplication) this.getApplication();
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    return get();
  }
  
  @Get
  @Override
  public Representation get() {

    getApplication().getLogger().info("get adminIndex");
    
    AdminIndexDTO aid = new AdminIndexDTO();
    
    // Dynamic sitools url
    aid.setAppUrl(application.getSettings().getString(Consts.APP_URL));
    
    Reference ref = LocalReference.createFileReference(application.getAdminIndexUrl());

    Representation adminFtl = new ClientResource(ref).get();

    // Wraps the bean with a FreeMarker representation
    return new TemplateRepresentation(adminFtl, aid, MediaType.TEXT_HTML);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL documentation info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the admin view.");
    this.addStandardGetRequestInfo(info);
    ResponseInfo responseInfo = new ResponseInfo();
    RepresentationInfo representationInfo = new RepresentationInfo();
    representationInfo.setReference("html_freemarker");
    responseInfo.getRepresentations().add(representationInfo);
    info.getResponses().add(responseInfo);
  }
  
}
