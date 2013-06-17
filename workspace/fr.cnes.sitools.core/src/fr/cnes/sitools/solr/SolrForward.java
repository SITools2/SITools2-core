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
package fr.cnes.sitools.solr;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlDescribable;

/**
 * Solr Core Container If you would like to interact with Solr through http without a Servlet container you can use this
 * restlet.
 * 
 * @author AKKA Technologies
 * 
 * @see http ://wiki.restlet.org/docs_2.0/13-restlet/28-restlet/229-restlet/version /2.pdf
 * 
 */
public class SolrForward extends Restlet implements WadlDescribable {

  // /**
  // * Default constructor
  // */
  // public SolrForward() {
  // }

  /**
   * Constructor
   * 
   * @param context
   *          Restlet parent context
   */
  public SolrForward(Context context) {
    super(context);
    setName("SolrForward");
    setDescription("Restlet handling Solr core services.");
  }

  @Override
  public void handle(Request request, Response response) {
    super.handle(request, response);
    if (!Method.OPTIONS.equals(request.getMethod())) {
      String path = request.getResourceRef().getRemainingPart();
      Reference solrRef = new Reference("solr:/" + path);
      Request solrRequest = new Request(request.getMethod(), solrRef, request.getEntity());
      solrRequest.setAttributes(request.getAttributes());
      solrRequest.setClientInfo(request.getClientInfo());
      Response solrResp = getContext().getClientDispatcher().handle(solrRequest);
      response.setStatus(solrResp.getStatus());
      response.setEntity(solrResp.getEntity());
    }
  }

  @Override
  public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
    ResourceInfo resourceInfo = new ResourceInfo();
    DocumentationInfo doc = new DocumentationInfo();
    doc.setTitle(getName());
    doc.setTextContent("Handle solr core services on the embedded SolR server. For more information on the SolR API please check the solr documentation : http://wiki.apache.org/solr/");
    resourceInfo.setDocumentation(doc);
    return resourceInfo;
  }

}
