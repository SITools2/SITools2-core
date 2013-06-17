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
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.solr.directory.SolrDirectoryActionResource;

/**
 * SitoolsApplication with embedded Solr for standard secure access
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SolrApplication extends SitoolsApplication {
  
  /**
   * The solr directory path
   */
  private String solrDirectory = null;
  
  /**
   * The solr template directory path
   */
  private String solrDirectoryTemplate = null;

  /**
   * SolrApplication constructor
   * 
   * @param context
   *          the context of the SolrApplication
   */
  public SolrApplication(Context context) {
    super(context);
    solrDirectory = (String) context.getAttributes().get("SOLR_DIRECTORY");
    setSolrDirectoryTemplate((String) context.getAttributes().get("SOLR_TEMPLATE_DIRECTORY"));
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("SolrApplication");
    setDescription("Sample Solr integration");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // HTTP exposition - SolrForward
    router.attachDefault(new SolrForward(getContext()));
    
    // solr directory indexer / searcher
    router.attach("/directory/create", SolrDirectoryActionResource.class);
    router.attach("/directory/update", SolrDirectoryActionResource.class);
    router.attach("/directory/{osId}/refresh", SolrDirectoryActionResource.class);
    router.attach("/directory/{osId}/delete", SolrDirectoryActionResource.class);
    router.attach("/directory/{osId}/cancel", SolrDirectoryActionResource.class);
//    router.attach("/directory/{osId}/clean", SolrDirectoryActionResource.class);
    router.attach("/directory/{osId}/refreshXSLT", SolrDirectoryActionResource.class);

    router.attach("/directory/{osId}/{fieldList}/suggest", SolrResource.class);
    router.attach("/directory/{osId}/execute", SolrResource.class);
    
    router.attach("/create", SolrActionResource.class);
    router.attach("/update", SolrActionResource.class);
    router.attach("/{osId}/refresh", SolrActionResource.class);
    router.attach("/{osId}/delete", SolrActionResource.class);
    router.attach("/{osId}/cancel", SolrActionResource.class);
//    router.attach("/{osId}/clean", SolrActionResource.class);
    router.attach("/{osId}/refreshXSLT", SolrActionResource.class);

    router.attach("/{osId}/{fieldList}/suggest", SolrResource.class);
    router.attach("/{osId}/execute", SolrResource.class);


    
    
    return router;

  }

  /**
   * Gets the solrDirectory value
   * 
   * @return the solrDirectory
   */
  public String getSolrDirectory() {
    return solrDirectory;
  }

  /**
   * Sets the value of solrDirectory
   * 
   * @param solrDirectory
   *          the solrDirectory to set
   */
  public void setSolrDirectory(String solrDirectory) {
    this.solrDirectory = solrDirectory;
  }

  /**
   * Gets the solrDirectoryTemplate value
   * 
   * @return the solrDirectoryTemplate
   */
  public String getSolrDirectoryTemplate() {
    return solrDirectoryTemplate;
  }

  /**
   * Sets the value of solrDirectoryTemplate
   * 
   * @param solrDirectoryTemplate
   *          the solrDirectoryTemplate to set
   */
  public void setSolrDirectoryTemplate(String solrDirectoryTemplate) {
    this.solrDirectoryTemplate = solrDirectoryTemplate;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Solr application to handle Solr index and requests in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
